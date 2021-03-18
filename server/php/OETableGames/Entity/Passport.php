<?php
namespace Entity;


use Core\BaseEntity;

class Passport extends BaseEntity
{

	var $PRIMARY_KEY = 'id';

	/**
	 *
	 * 用户ID
	 *
	 * @var int
	 */
	var $id;
	
	/**
	 *
	 * 管理员账户
	 *
	 * @var string
	 */
	var $passport;
	
	/**
	 *
	 * 管理员密码
	 *
	 * @var string
	 */
	var $password;
	
	/**
	 *
	 * 令牌
	 *
	 * @var string
	 */
	var $token;
	
	/**
	 *
	 * 令牌失效时间
	 *
	 * @var bigint
	 */
	var $tokenExpire;
	
	/**
	 *
	 * 
	 *
	 * @var string
	 */
	var $access_token;
	
	/**
	 *
	 * 
	 *
	 * @var string
	 */
	var $refresh_token;
	
	/**
	 *
	 * 状态
	 *
	 * @var int
	 */
	var $status;
	
	/**
	 *
	 * 创建时间
	 *
	 * @var bigint
	 */
	var $reg_time;
	
	/**
	 *
	 * 创建IP
	 *
	 * @var bigint
	 */
	var $reg_ip;
	
	/**
	 *
	 * 登录时间
	 *
	 * @var bigint
	 */
	var $log_time;
	
	/**
	 *
	 * 登录IP
	 *
	 * @var bigint
	 */
	var $log_ip;
	
	/**
	 *
	 * 登录次数
	 *
	 * @var int
	 */
	var $log_count;
}
?>