-- =============================================
-- 作者 :DRB
-- Create Date:2021-04-20 23:00:20
-- Description:添加
-- =============================================
CREATE PROCEDURE `passport_Create`
(
    OUT `id` LONG,
    IN `passport` VARCHAR(64),
    IN `password` VARCHAR(32),
    IN `token` VARCHAR(255),
    IN `tokenExpire` BIGINT(10),
    IN `access_token` VARCHAR(255),
    IN `refresh_token` VARCHAR(255),
    IN `status` TINYINT(3),
    IN `reg_time` BIGINT(10),
    IN `reg_ip` BIGINT(10),
    IN `log_time` BIGINT(10),
    IN `log_ip` BIGINT(10),
    IN `log_count` INT(11),
    IN `device` INT(11),
    OUT `retValue` LONG
)
BEGIN
    
    INSERT INTO passport
        (passport.`passport`,passport.`password`,passport.`token`,passport.`tokenExpire`,passport.`access_token`,passport.`refresh_token`,passport.`status`,passport.`reg_time`,passport.`reg_ip`,passport.`log_time`,passport.`log_ip`,passport.`log_count`,passport.`device`)
    VALUES
        (`passport`,`password`,`token`,`tokenExpire`,`access_token`,`refresh_token`,`status`,`reg_time`,`reg_ip`,`log_time`,`log_ip`,`log_count`,`device`);
    SET `id` = LAST_INSERT_ID();
    IF row_count() = 0 THEN
      SET `retValue` = -1;
    ELSE
      SET `retValue` = 1;
    END IF;
END;

-- =============================================
-- 作者 :DRB
-- Create Date:2021-04-20 23:00:20
-- Description:修改
-- =============================================
CREATE PROCEDURE `passport_Update`
(
    IN `id` LONG,
    IN `apiId` INT(11),
    IN `unionId` VARCHAR(32),
    IN `openId` VARCHAR(128),
    IN `groupId` INT(11),
    IN `passport` VARCHAR(64),
    IN `password` VARCHAR(32),
    IN `phone` VARCHAR(32),
    IN `token` VARCHAR(255),
    IN `tokenExpire` BIGINT(10),
    IN `access_token` VARCHAR(255),
    IN `refresh_token` VARCHAR(255),
    IN `verifyCode` INT(11),
    IN `verifyExpire` BIGINT(10),
    IN `status` TINYINT(3),
    IN `reg_time` BIGINT(10),
    IN `reg_ip` BIGINT(10),
    IN `log_time` BIGINT(10),
    IN `log_ip` BIGINT(10),
    IN `bind_time` BIGINT(10),
    IN `bind_ip` BIGINT(10),
    IN `log_count` INT(11),
    IN `bind_gift` BIGINT(10),
    IN `urlbind` INT(11),
    IN `urlbind_time` BIGINT(10),
    IN `urlbind_ip` BIGINT(10),
    IN `codebind` INT(11),
    IN `codebind_time` BIGINT(10),
    IN `codebind_ip` BIGINT(10),
    IN `codebind_gift` BIGINT(10),
    IN `first_pay` INT(11) UNSIGNED,
    IN `agentbind` INT(11),
    IN `contact_name` VARCHAR(32),
    IN `contact_phone` VARCHAR(32),
    IN `contact_address` VARCHAR(64),
    IN `idCard` VARCHAR(18),
    IN `realName` VARCHAR(16),
    IN `baiduChannelId` VARCHAR(32),
    IN `device` INT(11),
    OUT `retValue` LONG
)
BEGIN

		UPDATE
			passport
		SET
		    passport.passport=`passport`,
		    passport.password=`password`,
		    passport.token=`token`,
		    passport.tokenExpire=`tokenExpire`,
		    passport.access_token=`access_token`,
		    passport.refresh_token=`refresh_token`,
		    passport.status=`status`,
		    passport.reg_time=`reg_time`,
		    passport.reg_ip=`reg_ip`,
		    passport.log_time=`log_time`,
		    passport.log_ip=`log_ip`,
		    passport.log_count=`log_count`,
		    passport.device=`device`
		WHERE
		    passport.id=`id`;
			
		IF row_count() = 0 THEN
			SET `retValue` = -1;
		ELSE 
		    SET `retValue` = 1;
		END IF;
END;

-- =============================================
-- 作者 :DRB
-- Create Date:2021-04-20 23:00:20
-- Description:删除
-- =============================================
CREATE PROCEDURE `passport_Delete`
(
    IN `id` LONG,
    OUT `retValue` LONG
)
BEGIN
		UPDATE passport SET passport.`status`=0 WHERE passport.`id`=`id`;
		IF row_count() = 0 THEN
			SET `retValue` = -1;
		ELSE 
		    SET `retValue` = 1;
		END IF;
END;

-- =============================================
-- 作者 :DRB
-- Create Date:2021-04-20 23:00:20
-- Description:查询实体
-- =============================================
CREATE PROCEDURE `passport_GetEntity`
(
    IN `id` LONG
)
BEGIN
	
		SELECT * FROM passport WHERE passport.`id`=`id`;
	
END;

