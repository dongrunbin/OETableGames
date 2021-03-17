<?php
namespace Ctrl;

use Core\BaseCtrl;
use Core\Util;
use Common\Model;
use Common\Tools;

class Passport extends BaseCtrl {


    /**
     * 游客登录
     */
    public function guest() {
        
        $unixtime = $this->getString(1, true);
        
        $sign = $this->getString("sign", true);
        
        $md5 = md5('guest' . $unixtime . ApiTools::$key);
        
        if (strtolower($md5) != strtolower($sign)) {
            $ret = array ();
            $ret['code'] = ERR_SIGN_ERROR;
            $ret['msg'] = 'ERR_SIGN_ERROR';
            $ret['data'] = array ();
            return $ret;
        }
        
        $m_passport = Model::Passport();
        
        $token = ApiTools::token(rand(1000, 9999));
        
        $array = array ();
        $array['passport'] = uniqid();
        $array['password'] = uniqid();
        
        $array['phone'] = 'UN' . uniqid();
        
        $array['token'] = $token;
        $array['tokenExpire'] = REQUEST_TIME + 60 * 60 * 24;
        
        $array['status'] = 1;
        
        $array['reg_time'] = REQUEST_TIME;
        $array['reg_ip'] = Util::getClientIP();
        
        $array['log_time'] = $array['reg_time'];
        $array['log_ip'] = $array['reg_ip'];
        
        $passportId = $m_passport->insert($array);
        
        if ($passportId > 0) {
            
            $m_player = Model::Player($passportId);
            $arr = array();
            $arr['cards'] = GUEST_CARDS;
            $arr['gold'] = GUEST_GOLD;
            $m_player->update("where `id` = '{$passportId}'", $arr);
        }
        
        if ($passportId <= 0) {
            $ret = array ();
            $ret['code'] = ERR_PASSPORT_ALREADY_EXISTS;
            $ret['msg'] = 'ERR_PASSPORT_ALREADY_EXISTS';
            $ret['data'] = array ();
            return $ret;
        }
        
        $ret = array ();
        $ret['code'] = 1;
        $ret['msg'] = 'succeed';
        $ret['data'] = array (
            'passportId' => $passportId, 
            'token' => $token 
        );
        
        return $ret;
    
    }

    /**
     * 再次登录
     * @int passportId
     * @string token
     */
    public function relogin() {

        $unixtime = $this->getString(1, true);
        
        $passportId = $this->getInteger("passportId", true, true);
        
        $token = $this->getString("token", true);

        $device = $this->getString("device", true);
        
        $sign = $this->getString("sign", true);
        
        $md5 = md5("relogin" . $passportId . $token . $device . $unixtime. ApiTools::$key);
        
        if (strtolower($md5) != strtolower($sign)) {
            $ret = array ();
            $ret['code'] = ERR_SIGN_ERROR;
            $ret['msg'] = 'sign error';
            $ret['data'] = array ();
            return $ret;
        }
        
        if (!ApiTools::checkToken($passportId, $token)) {
            $ret = array ();
            $ret['code'] = ERR_TOKEN_EXPIRE;
            $ret['msg'] = 'ERR_TOKEN_EXPIRE';
            $ret['data'] = array ();
            echo json_encode($ret);
            exit();
        }
        
        $passport = Model::Passport($passportId);
        
        if (!$passport->exists) {
            $ret = array ();
            $ret['code'] = ERR_PASSPORT_NOT_EXISTS;
            $ret['msg'] = 'ERR_PASSPORT_NOT_EXISTS';
            $ret['data'] = array ();
            return $ret;
        }

        $array = array ();
        $array['tokenExpire'] = REQUEST_TIME + 60 * 60 * 24;
        $array['log_time'] = REQUEST_TIME;
        $array['log_ip'] = Util::getClientIP();
        $array['log_count'] = $passport->log_count + 1;
        $array['device'] = $device;

        $passport->update("where `id` = '{$passportId}'", $array);

        ApiTools::succeed(intval($passportId), $token);
    
    }

    /**
     * 登录
     */
    public function login() {

        $unixtime = $this->getString(1, true);
        
        $passport = $this->getString("passport", true);
        
        $password = $this->getString("password", true);
        
        $sign = $this->getString("sign", true);
        
        $md5 = md5("login" . $passport . $password . $unixtime . ApiTools::$key);
        
        if (strtolower($md5) != strtolower($sign)) {
            $ret = array ();
            $ret['code'] = ERR_SIGN_ERROR;
            $ret['msg'] = 'ERR_SIGN_ERROR';
            $ret['data'] = array ();
            return $ret;
        }
        
        $m_passport = Model::Passport();
        $passport = $m_passport->node("where `passport` = '{$passport}'");
        
        if (!$passport) {
            $ret = array ();
            $ret['code'] = ERR_PASSPORT_NOT_EXISTS;
            $ret['msg'] = 'ERR_PASSPORT_NOT_EXISTS';
            $ret['data'] = array ();
            return $ret;
        }
        
        if (Tools::md5pswd($passport['passport'], $password) != $passport['password']) {
            
            $ret = array ();
            $ret['code'] = ERR_PASSWORD_ERROR;
            $ret['msg'] = 'ERR_PASSWORD_ERROR';
            $ret['data'] = array ();
            
            return $ret;
        }
        
        $token = ApiTools::token($passport['id']);
        
        $array = array ();
        $array['token'] = $token;
        $array['tokenExpire'] = REQUEST_TIME + 60 * 60 * 24;
        
        $array['log_time'] = REQUEST_TIME;
        $array['log_ip'] = Util::getClientIP();
        $array['log_count'] = $passport['log_count'] + 1;
        
        $m_passport->update("where `id` = '{$passport['id']}'", $array);
        
        ApiTools::succeed(intval($passport['id']), $token);
    
    }

    public function server()
    {
        $unixtime = $this->getString(1, true);

        $passportId = $this->getInteger("passportId", true, true);

        $token = $this->getString("token", true);

        $sign = $this->getString("sign", true);

        $md5 = md5("server" . $passportId . $token . $unixtime . ApiTools::$key);

        if (strtolower($md5) != strtolower($sign)) {
            $ret = array ();
            $ret['code'] = ERR_SIGN_ERROR;
            $ret['msg'] = 'sign error';
            $ret['data'] = array ();
            return $ret;
        }

        if (!ApiTools::checkToken($passportId, $token)) {
            $ret = array ();
            $ret['code'] = ERR_TOKEN_EXPIRE;
            $ret['msg'] = 'ERR_TOKEN_EXPIRE';
            $ret['data'] = array ();
            return $ret;
        }

        $serverModel = Model::Server();
        $list = $serverModel->select("where 1");

        if(count($list) == 0)
        {
            $ret = array ();
            $ret['code'] = -1;
            $ret['msg'] = '服务器暂未开放';
            $ret['data'] = array ();
            return $ret;
        }

        $index = rand(0,count($list) - 1);
        $server = $list[$index];

        $data = array ();
        $data['ip'] = $server['host'];
        $data['port'] = $server['port'];

        $ret = array ();
        $ret['code'] = 1;
        $ret['msg'] = 'succeed';
        $ret['data'] = $data;
        return $ret;
    }

}

?>