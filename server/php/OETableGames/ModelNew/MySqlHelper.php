<?php
/**
 * Created by PhpStorm.
 * User: Binge
 * Date: 2018/5/15
 * Time: 13:58
 */

namespace Model;


class MySqlHelper
{
    public static $instance = null;

    private $mysql;

    public static function getInstance()
    {
        if(self::$instance == null)
        {
            self::$instance = new __CLASS__;
        }
        return self::$instance;
    }

    public function connect($host,$user,$password,$database,$port)
    {
        $this->mysql = new \mysqli($host,$user,$password,$database,$port);
    }
}