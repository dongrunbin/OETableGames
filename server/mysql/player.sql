-- =============================================
-- 作者 :DRB
-- Create Date:2021-04-20 23:00:20
-- Description:添加
-- =============================================
CREATE PROCEDURE `player_Create`
(
    OUT `id` LONG,
    IN `nickname` VARCHAR(32),
    IN `gender` TINYINT(3),
    IN `cards` INT(11),
    IN `roomId` INT(11),
    IN `gameId` INT(11),
    IN `matchId` INT(11),
    IN `status` TINYINT(3),
    IN `online` BIGINT(10),
    IN `ipaddr` BIGINT(10),
    IN `gold` INT(11),
    OUT `retValue` LONG
)
BEGIN
    
    INSERT INTO player
        (player.`nickname`,player.`gender`,player.`cards`,player.`roomId`,player.`gameId`,player.`matchId`,player.`status`,player.`online`,player.`ipaddr`,player.`gold`)
    VALUES
        (`nickname`,`gender`,`cards`,`roomId`,`gameId`,`matchId`,`status`,`online`,`ipaddr`,`gold`);
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
CREATE PROCEDURE `player_Update`
(
    IN `id` LONG,
    IN `nickname` VARCHAR(32),
    IN `gender` TINYINT(3),
    IN `cards` INT(11),
    IN `roomId` INT(11),
    IN `gameId` INT(11),
    IN `matchId` INT(11),
    IN `status` TINYINT(3),
    IN `online` BIGINT(10),
    IN `ipaddr` BIGINT(10),
    IN `gold` INT(11),
    OUT `retValue` LONG
)
BEGIN

		UPDATE
			player
		SET
		    player.nickname=`nickname`,
		    player.gender=`gender`,
		    player.cards=`cards`,
		    player.roomId=`roomId`,
		    player.gameId=`gameId`,
		    player.matchId=`matchId`,
		    player.status=`status`,
		    player.online=`online`,
		    player.ipaddr=`ipaddr`,
		    player.gold=`gold`
		WHERE
		    player.id=`id`;
			
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
CREATE PROCEDURE `player_Delete`
(
    IN `id` LONG,
    OUT `retValue` LONG
)
BEGIN
		UPDATE player SET player.`status`=0 WHERE player.`id`=`id`;
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
CREATE PROCEDURE `player_GetEntity`
(
    IN `id` LONG
)
BEGIN
	
		SELECT * FROM player WHERE player.`id`=`id`;
	
END;

