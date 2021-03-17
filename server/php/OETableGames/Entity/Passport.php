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
	 * 第三方平台ID
	 *
	 * @var int
	 */
	var $apiId;
	
	/**
	 *
	 * 第三方平台令牌
	 *
	 * @var string
	 */
	var $unionId;
	
	/**
	 *
	 * 
	 *
	 * @var string
	 */
	var $openId;
	
	/**
	 *
	 * 用户组ID
	 *
	 * @var int
	 */
	var $groupId;
	
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
	 * 手机号码
	 *
	 * @var string
	 */
	var $phone;
	
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
	 * 验证码
	 *
	 * @var int
	 */
	var $verifyCode;
	
	/**
	 *
	 * 验证码失效时间
	 *
	 * @var bigint
	 */
	var $verifyExpire;
	
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
	 * 
	 *
	 * @var bigint
	 */
	var $bind_time;
	
	/**
	 *
	 * 
	 *
	 * @var bigint
	 */
	var $bind_ip;
	
	/**
	 *
	 * 登录次数
	 *
	 * @var int
	 */
	var $log_count;
	
	/**
	 *
	 * 绑定送房卡
	 *
	 * @var bigint
	 */
	var $bind_gift;
	
	/**
	 *
	 * 
	 *
	 * @var int
	 */
	var $urlbind;
	
	/**
	 *
	 * 
	 *
	 * @var bigint
	 */
	var $urlbind_time;
	
	/**
	 *
	 * 
	 *
	 * @var bigint
	 */
	var $urlbind_ip;
	
	/**
	 *
	 * 
	 *
	 * @var int
	 */
	var $codebind;
	
	/**
	 *
	 * 
	 *
	 * @var bigint
	 */
	var $codebind_time;
	
	/**
	 *
	 * 
	 *
	 * @var bigint
	 */
	var $codebind_ip;
	
	/**
	 *
	 * 
	 *
	 * @var bigint
	 */
	var $codebind_gift;
	
	/**
	 *
	 * 
	 *
	 * @var int
	 */
	var $first_pay;
	
	/**
	 *
	 * 绑定的代理ID
	 *
	 * @var int
	 */
	var $agentbind;
	
	/**
	 *
	 * 联系人姓名
	 *
	 * @var string
	 */
	var $contact_name;
	
	/**
	 *
	 * 联系电话
	 *
	 * @var string
	 */
	var $contact_phone;
	
	/**
	 *
	 * 联系地址
	 *
	 * @var string
	 */
	var $contact_address;
	
	/**
	 *
	 * 身份证号
	 *
	 * @var string
	 */
	var $idCard;
	
	/**
	 *
	 * 真实姓名
	 *
	 * @var string
	 */
	var $realName;
	
	
}
?>