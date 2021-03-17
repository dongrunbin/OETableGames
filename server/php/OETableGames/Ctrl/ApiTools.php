<?php
namespace Ctrl;

use Common\Model;
use Core\Util;

class ApiTools
{

    public static $key = SERVER_KEY;

    public static function succeed($passportId, $token)
    {
        $player = Model::Player($passportId);
        
        if ($player->nickname == '') {
            $player->nickname = 'G' . $passportId;
        }
        $passport = Model::Passport($passportId);
        
        $ret = array();
        $ret['code'] = 1;
        $ret['msg'] = 'succeed';
        $ret['data'] = array(
            'passportId' => $passportId,
            'token' => $token,
            'nickname' => $player->nickname,
            'gender' => intval($player->gender),
            'cards' => intval($player->cards),
            'gold' => intval($player->gold),
            'ipaddr' => Util::getClientIP(0)
        );
        
        echo json_encode($ret);
    }

    public static function token($passportId)
    {
        return md5($passportId . rand(1000, 9999) . time() . rand(1000, 9999) . ApiTools::$key);
    }

    public static function checkToken($passportId, $token)
    {
        $passport = Model::Passport($passportId);
        
        if ($passport->token != $token) {
            return false;
        }
        
        if ((REQUEST_TIME - $passport->tokenExpire) > 0) {
            return false;
        }
        
        return true;
    }
}

?>