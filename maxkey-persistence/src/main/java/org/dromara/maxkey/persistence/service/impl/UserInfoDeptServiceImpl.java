/*
 * Copyright [2026] [MaxKey of copyright http://www.maxkey.top]
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

package org.dromara.maxkey.persistence.service.impl;

import java.sql.Types;
import java.util.List;

import org.dromara.maxkey.entity.idm.UserInfoDept;
import org.dromara.maxkey.persistence.mapper.UserInfoDeptMapper;
import org.dromara.maxkey.persistence.service.UserInfoDeptService;
import org.dromara.mybatis.jpa.service.impl.JpaServiceImpl;
import org.springframework.stereotype.Repository;

@Repository
public class UserInfoDeptServiceImpl
        extends JpaServiceImpl<UserInfoDeptMapper, UserInfoDept, String>
        implements UserInfoDeptService {

    @Override
    public List<UserInfoDept> findByUserId(String userId, String instId) {
        return find("userid = ? and instid = ?",
                new Object[] { userId, instId },
                new int[] { Types.VARCHAR, Types.VARCHAR });
    }

    @Override
    public void replaceUserDepts(String userId, String instId, List<UserInfoDept> depts) {
        getMapper().deleteByUserId(userId, instId);
        if (depts == null) {
            return;
        }
        for (UserInfoDept dept : depts) {
            dept.setId(null);
            dept.setUserId(userId);
            dept.setInstId(instId);
            insert(dept);
        }
    }
}
