-- 用户兼任部门表：主部门仍存放在 mxk_userinfo.departmentid
CREATE TABLE IF NOT EXISTS `mxk_userinfo_dept` (
  `id` varchar(45) NOT NULL,
  `userid` varchar(45) NOT NULL COMMENT '用户ID',
  `departmentid` varchar(45) NOT NULL COMMENT '兼任部门ID',
  `department` varchar(200) DEFAULT NULL COMMENT '兼任部门',
  `instid` varchar(45) NOT NULL,
  `createddate` datetime DEFAULT CURRENT_TIMESTAMP,
  `modifieddate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_userinfo_dept` (`userid`,`departmentid`,`instid`),
  KEY `idx_userinfo_dept_dept` (`departmentid`,`instid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='用户兼任部门';
