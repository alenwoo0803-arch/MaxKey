/*
 * Copyright [2021] [MaxKey of copyright http://www.maxkey.top]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 

package org.dromara.maxkey.synchronizer.dingtalk;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dromara.maxkey.constants.ConstsStatus;
import org.dromara.maxkey.entity.SynchroRelated;
import org.dromara.maxkey.entity.idm.UserInfo;
import org.dromara.maxkey.entity.idm.UserInfoDept;
import org.dromara.maxkey.synchronizer.AbstractSynchronizerService;
import org.dromara.maxkey.synchronizer.ISynchronizerService;
import org.dromara.maxkey.entity.SynchroAssociation;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiV2UserListRequest;
import com.dingtalk.api.response.OapiV2UserListResponse;
import com.dingtalk.api.response.OapiV2UserListResponse.ListUserResponse;

import static org.dromara.maxkey.synchronizer.utils.FieldUtil.*;

@Service
public class DingtalkUsersService  extends AbstractSynchronizerService implements ISynchronizerService{
    static final  Logger _logger = LoggerFactory.getLogger(DingtalkUsersService.class);
    
    String access_token;

    private static final Integer USER_TYPE = 1;

    
    @Override
    public void sync() {
        _logger.info("Sync Dingtalk Users...");
        try {
            List<SynchroRelated> synchroRelateds =
                    synchroRelatedService.findOrgs(this.synchronizer);
            Map<String, SynchroRelated> deptMappings = new HashMap<>();
            for(SynchroRelated relatedOrg : synchroRelateds) {
                deptMappings.put(relatedOrg.getOriginId(), relatedOrg);
            }
            Map<String, UserSyncInfo> userSyncMap = new LinkedHashMap<>();

            for(SynchroRelated relatedOrg : synchroRelateds) {
                DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/list");
                _logger.debug("DingTalk deptId : {}" , relatedOrg.getOriginId());
                Long cursor = 0L;
                boolean hasMore = true;
                while(hasMore && cursor != null) {
                    OapiV2UserListRequest req = new OapiV2UserListRequest();
                    req.setDeptId(Long.parseLong(relatedOrg.getOriginId()));
                    req.setCursor(cursor);
                    req.setSize(100L);
                    req.setOrderField("modify_desc");
                    req.setContainAccessLimit(true);
                    req.setLanguage("zh_CN");
                    OapiV2UserListResponse rsp = client.execute(req, access_token);
                    _logger.trace("response : {}" , rsp.getBody());

                    if(rsp.getErrcode()!=0) {
                        _logger.error("DingTalk user list failed, deptId {} , errcode {} , errmsg {}"
                                , relatedOrg.getOriginId(), rsp.getErrcode(), rsp.getErrmsg());
                        break;
                    }

                    for(ListUserResponse user :rsp.getResult().getList()) {
                        _logger.debug("name : {} , {} , {}", user.getName(),user.getLoginId(),user.getUserid());

                        UserInfo userInfo  = buildUserInfoByFieldMap(user,relatedOrg);
                        _logger.trace("userInfo {}" , userInfo);
                        userInfo.setPassword(userInfo.getUsername() + UserInfo.DEFAULT_PASSWORD_SUFFIX);
                        userInfoService.saveOrUpdate(userInfo);

                        SynchroRelated synchroRelated = new SynchroRelated(
                                userInfo.getId(),
                                userInfo.getUsername(),
                                userInfo.getDisplayName(),
                                UserInfo.CLASS_TYPE,
                                synchronizer.getId(),
                                synchronizer.getName(),
                                user.getUnionid(),
                                user.getName(),
                                user.getUserid(),
                                "",
                                synchronizer.getInstId());
                        synchroRelatedService.updateSynchroRelated(
                                this.synchronizer,synchroRelated,UserInfo.CLASS_TYPE);

                        socialsAssociate(synchroRelated,"dingtalk");

                        UserSyncInfo userSync = userSyncMap.computeIfAbsent(
                                userInfo.getId(), k -> new UserSyncInfo(userInfo));
                        if(user.getDeptIdList() == null || user.getDeptIdList().isEmpty()) {
                            userSync.addDepartment(Long.parseLong(relatedOrg.getOriginId()));
                        }else {
                            userSync.addDepartments(user.getDeptIdList());
                        }
                    }

                    hasMore = Boolean.TRUE.equals(rsp.getResult().getHasMore());
                    cursor = rsp.getResult().getNextCursor();
                }
            }

            for(UserSyncInfo userSync : userSyncMap.values()) {
                saveUserDepartments(userSync, deptMappings);
            }

            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    private static class UserSyncInfo {
        final UserInfo userInfo;
        final LinkedHashSet<Long> deptIds = new LinkedHashSet<>();

        UserSyncInfo(UserInfo userInfo) {
            this.userInfo = userInfo;
        }

        void addDepartment(Long deptId) {
            if (deptId != null) {
                deptIds.add(deptId);
            }
        }

        void addDepartments(List<Long> deptIdList) {
            for (Long deptId : deptIdList) {
                addDepartment(deptId);
            }
        }
    }

    /**
     * 钉钉返回的部门列表中第一个作为主部门，其余写入兼任部门表。
     */
    private void saveUserDepartments(UserSyncInfo userSync, Map<String, SynchroRelated> deptMappings) {
        List<SynchroRelated> departments = new ArrayList<>();
        for (Long deptId : userSync.deptIds) {
            SynchroRelated department = deptMappings.get(String.valueOf(deptId));
            if (department != null && !containsDepartment(departments, department.getObjectId())) {
                departments.add(department);
            }
        }
        if (departments.isEmpty()) {
            userInfoDeptService.replaceUserDepts(
                    userSync.userInfo.getId(), synchronizer.getInstId(), new ArrayList<>());
            return;
        }

        SynchroRelated primary = departments.get(0);
        userSync.userInfo.setDepartmentId(primary.getObjectId());
        userSync.userInfo.setDepartment(primary.getObjectName());
        userInfoService.update(userSync.userInfo);

        List<UserInfoDept> adjunctDepts = new ArrayList<>();
        for (int i = 1; i < departments.size(); i++) {
            SynchroRelated department = departments.get(i);
            adjunctDepts.add(new UserInfoDept(
                    null,
                    department.getObjectId(),
                    department.getObjectName(),
                    synchronizer.getInstId()));
        }
        userInfoDeptService.replaceUserDepts(
                userSync.userInfo.getId(), synchronizer.getInstId(), adjunctDepts);
    }

    private boolean containsDepartment(List<SynchroRelated> departments, String objectId) {
        for (SynchroRelated department : departments) {
            if (department.getObjectId().equals(objectId)) {
                return true;
            }
        }
        return false;
    }

    public UserInfo buildUserInfo(ListUserResponse user,SynchroRelated relatedOrg) {
        UserInfo userInfo = new  UserInfo();

        userInfo.setUsername(user.getUserid());
        userInfo.setNickName(user.getName());
        userInfo.setDisplayName(user.getName());
        userInfo.setFormattedName(user.getName());
        
        userInfo.setEmail(StringUtils.isBlank(user.getEmail())? user.getUserid() +"@maxkey.top":user.getEmail());
        userInfo.setEntryDate(new DateTime(user.getHiredDate()).toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
        userInfo.setMobile(user.getMobile());
        
        userInfo.setDepartmentId(relatedOrg.getObjectId()+"");
        userInfo.setDepartment(relatedOrg.getObjectName());
        userInfo.setEmployeeNumber(user.getJobNumber());
        userInfo.setJobTitle(user.getTitle());
        userInfo.setWorkEmail(user.getOrgEmail());
        userInfo.setWorkPhoneNumber(user.getTelephone());
        userInfo.setWorkOfficeName(user.getWorkPlace());
        if(user.getActive()) {
            userInfo.setStatus(ConstsStatus.ACTIVE);
        }else {
            userInfo.setStatus(ConstsStatus.INACTIVE);
        }

        //userInfo.setInstId(this.synchronizer.getInstId());
        userInfo.setDescription("dingtalk "+user.getRemark());
        return userInfo;
    }


    public UserInfo buildUserInfoByFieldMap(ListUserResponse user, SynchroRelated relatedOrg){
        UserInfo userInfo = new UserInfo();
        Map<String, String> fieldMap = getFieldMap(Long.parseLong(synchronizer.getId()));
        for (Map.Entry<String, String> entry : fieldMap.entrySet()) {

            String userInfoProperty = entry.getKey();
            String sourceProperty = entry.getValue();

            try {
                Object sourceValue = null;

                if("email".equals(sourceProperty)){
                    userInfo.setEmail(StringUtils.isBlank(user.getEmail())? user.getUserid() +"@maxkey.top":user.getEmail());
                    continue;
                }
                if("active".equals(sourceProperty)){
                    userInfo.setStatus(user.getActive()?ConstsStatus.ACTIVE:ConstsStatus.INACTIVE);
                    continue;
                }
                if("remark".equals(sourceProperty)){
                    userInfo.setDescription("dingtalk "+user.getRemark());
                    continue;
                }
                if("hiredDate".equals(sourceProperty)){
                    userInfo.setEntryDate(new DateTime(user.getHiredDate()).toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
                    continue;
                }
                if (hasField(OapiV2UserListResponse.ListUserResponse.class, sourceProperty)) {
                    sourceValue = getFieldValue(user, sourceProperty);
                }

                else if (hasField(SynchroRelated.class, sourceProperty)) {
                    sourceValue = getFieldValue(relatedOrg, sourceProperty);
                }

                if (sourceValue != null) {
                    setFieldValue(userInfo, userInfoProperty, sourceValue);
                }

            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        userInfo.setInstId(this.synchronizer.getInstId());
        userInfo.setUserType("EMPLOYEE");
        userInfo.setUserState("RESIDENT");
        return userInfo;
    }


    public Map<String,String> getFieldMap(Long jobId){
        Map<String,String> userFieldMap = new HashMap<>();
        //根据job id查询属性映射表
        List<SynchroAssociation> syncJobConfigFieldList = synchroAssociationService.findBySyncId(jobId);
        //获取用户属性映射
        for(SynchroAssociation element:syncJobConfigFieldList){
            if(Integer.parseInt(element.getObjectType()) == USER_TYPE.intValue()){
                userFieldMap.put(element.getTargetField(), element.getSourceField());
            }
        }
        return userFieldMap;
    }



    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

}
