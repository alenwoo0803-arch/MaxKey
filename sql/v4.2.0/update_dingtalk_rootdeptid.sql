-- 同步器增加钉钉根部门ID配置，为空时使用 1
ALTER TABLE `mxk_synchronizers`
    ADD COLUMN `rootdeptid` varchar(45) DEFAULT NULL COMMENT '钉钉根部门ID';
