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

package org.dromara.maxkey.entity.idm;

import java.io.Serializable;
import java.util.Date;

import org.dromara.mybatis.jpa.entity.JpaEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 用户兼任部门，主部门仍存放在 mxk_userinfo.departmentid。
 */
@Entity
@Table(name = "MXK_USERINFO_DEPT")
public class UserInfoDept extends JpaEntity implements Serializable {

    private static final long serialVersionUID = 6194273051625410771L;

    @Id
    @Column
    @GeneratedValue
    String id;

    @Column
    String userId;

    @Column
    String departmentId;

    @Column
    String department;

    @Column
    String instId;

    @Column
    Date createdDate;

    @Column
    Date modifiedDate;

    public UserInfoDept() {
    }

    public UserInfoDept(String userId, String departmentId, String department, String instId) {
        this.userId = userId;
        this.departmentId = departmentId;
        this.department = department;
        this.instId = instId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getInstId() {
        return instId;
    }

    public void setInstId(String instId) {
        this.instId = instId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Override
    public String toString() {
        return "UserInfoDept [id=" + id + ", userId=" + userId
                + ", departmentId=" + departmentId + ", department=" + department
                + ", instId=" + instId + "]";
    }
}
