-- 将存量数据库中的默认品牌信息更新为 GMC
-- 适用于已初始化过 mxk_institutions 默认数据（ID = 1）的环境

SET NAMES utf8mb4;

UPDATE `mxk_institutions`
SET `NAME` = 'GMC',
    `FULLNAME` = 'GMC统一身份认证平台',
    `LOGO` = './assets/logo.png',
    `frontTitle` = 'GMC统一身份认证平台',
    `CONSOLETITLE` = 'GMC统一身份认证平台'
WHERE `ID` = '1';

UPDATE `mxk_apps`
SET `NAME` = 'GMC管理系统'
WHERE `ID` = '622076759805923328';

-- 将内置管理控制台应用指向本地前端（开发环境 8526）
UPDATE `mxk_apps`
SET `LOGINURL` = 'http://localhost:8526/maxkey-mgt'
WHERE `ID` = '622076759805923328';

UPDATE `mxk_apps_cas_details`
SET `SERVICE` = 'http://localhost:8526/maxkey-mgt/#/passport/trust/auth',
    `CALLBACKURL` = 'http://localhost:8526/maxkey-mgt/#/passport/trust/auth'
WHERE `ID` = '622076759805923328';
