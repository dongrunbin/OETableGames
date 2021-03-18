<?php
namespace Entity;


use Core\BaseEntity;

class Player extends BaseEntity
{

	var $PRIMARY_KEY = 'id';

	/**
	 *
	 * 通行证ID
	 *
	 * @var int
	 */
	var $id;
	
	/**
	 *
	 * 昵称
	 *
	 * @var string
	 */
	var $nickname;
	
	/**
	 *
	 * 性别
	 *
	 * @var int
	 */
	var $gender;
	
	/**
	 *
	 * 房卡
	 *
	 * @var int
	 */
	var $cards;
	
	/**
	 *
	 * 房间ID
	 *
	 * @var int
	 */
	var $roomId;
	
	/**
	 *
	 * 游戏id
	 *
	 * @var int
	 */
	var $gameId;
	
	/**
	 *
	 * 
	 *
	 * @var int
	 */
	var $matchId;
	
	/**
	 *
	 * 状态
	 *
	 * @var int
	 */
	var $status;
	
	/**
	 *
	 * 在线时间
	 *
	 * @var bigint
	 */
	var $online;
	
	/**
	 *
	 * 在线IP
	 *
	 * @var bigint
	 */
	var $ipaddr;

    /**
     *
     * 金币
     *
     * @var int
     */
	var $gold;
}
?>