<?php
namespace Entity;


use Core\BaseEntity;

class Ver_info extends BaseEntity
{

	var $PRIMARY_KEY = 'id';

	/**
	 *
	 * 
	 *
	 * @var int
	 */
	var $id;
	
	/**
	 *
	 * 
	 *
	 * @var int
	 */
	var $code;
	
	/**
	 *
	 * 
	 *
	 * @var string
	 */
	var $md5;
	
	/**
	 *
	 * 
	 *
	 * @var string
	 */
	var $url;
	
	/**
	 *
	 * 
	 *
	 * @var string
	 */
	var $size;
	
	/**
	 *
	 * 
	 *
	 * @var string
	 */
	var $date;
	
	/**
	 *
	 * 
	 *
	 * @var string
	 */
	var $version;
	
	/**
	 *
	 * 
	 *
	 * @var string
	 */
	var $platform;
	
	
}
?>