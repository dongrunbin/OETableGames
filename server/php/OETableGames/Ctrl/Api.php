<?php
namespace Ctrl;

use Core\BaseCtrl;
use Core\MakerEntity;
use Core\MakerModel;
use ReflectionMethod;
use Core\Util;

class Api extends BaseCtrl
{

    public function make()
    {
        MakerEntity::buildAll();
        MakerModel::buildAll();
    }

    public function main()
    {
        $methods = get_class_methods(new Game());
        $game = array();
        foreach ($methods as $name) {
            if ($name[0] == '_') {
                break;
            }
            
            $reflection = new ReflectionMethod("Ctrl\\Game", $name);
            $tmp = $reflection->getDocComment();
            
            $flag = preg_match_all('/@(.*?)\n/', $tmp, $tmp);
            if ($flag) {
                $game[] = array(
                    'name' => $name,
                    'args' => array_map(function ($args) {
                        return array_map('trim', explode(' ', $args));
                    }, $tmp[1])
                );
            }
        }
        
        $methods = get_class_methods(new Passport());
        $passport = array();
        foreach ($methods as $name) {
            if ($name[0] == '_') {
                break;
            }
            
            $reflection = new ReflectionMethod("Ctrl\\Passport", $name);
            $tmp = $reflection->getDocComment();
            
            $flag = preg_match_all('/@(.*?)\n/', $tmp, $tmp);
            if ($flag) {
                $passport[] = array(
                    'name' => $name,
                    'args' => array_map(function ($args) {
                        return array_map('trim', explode(' ', $args));
                    }, $tmp[1])
                );
            }
        }

        $this->assign('game', $game);
        $this->assign('passport', $passport);
        
        $this->display('main.html');
    }

    public function doPost()
    {
        $ctrl = $this->getString('ctrl', true);
        $url = $this->getString('url', true);
        $key = $this->getStrings('key', true);
        $val = $this->getStrings('val', true);
        
        $unixtime = time();
        
        $args = '';
        foreach ($val as $v) {
            $args .= $v;
        }
        
        $sign = md5($url . $args . $unixtime . "mj12321jm");
        
        $params = array();
        foreach ($val as $k => $v) {
            $params[$key[$k]] = $v;
        }
        
        $params['sign'] = $sign;
        $url = 'http://' . $_SERVER['HTTP_HOST'] . '/' . $ctrl . '/' . $url . '/' . $unixtime;
        
        echo Util::curl_request($url, 'POST', $params);
    }

    public function game_renter()
    {
        $unixtime = time();
        
        $passportId = 1001;
        $token = 'token';
        $bundleId = 'bundleId';
        
        $sign = md5("renter" . $passportId . $token . $bundleId . $unixtime . "mj12321jm");
        
        $form = array();
        $form['action'] = "/game/renter/{$unixtime}";
        
        $option = array();
        $option['unixtime'] = $unixtime;
        $option['passportId'] = $passportId;
        $option['token'] = $token;
        $option['bundleId'] = $bundleId;
        $option['sign'] = $sign;
        
        $form['option'] = $option;
        $this->assign('form', $form);
        
        $this->assign('name', __METHOD__);
        
        $this->display('form.html');
    }

    public function game_enter()
    {
        $unixtime = time();
        
        $passportId = 1;
        $token = 'token';
        $bundleId = 'bundleId';
        $roomId = '1';
        
        $sign = md5("enter" . $passportId . $token . $bundleId . $roomId . $unixtime . "mj12321jm");
        
        $form = array();
        $form['action'] = "/game/enter/{$unixtime}";
        
        $option = array();
        $option['unixtime'] = $unixtime;
        $option['passportId'] = $passportId;
        $option['token'] = $token;
        $option['bundleId'] = $bundleId;
        $option['roomId'] = $roomId;
        $option['sign'] = $sign;
        
        $form['option'] = $option;
        $this->assign('form', $form);
        
        $this->assign('name', __METHOD__);
        $this->display('form.html');
    }

    public function game_gateway()
    {
        $unixtime = time();
        
        $passportId = 6944;
        $token = '54e638b3bdf71e9aa4f474f9c2bae32e';
        $bundleId = 'bundleId';
        $sign = md5("gateway" . $passportId . $token . $bundleId . $unixtime . "mj12321jm");
        
        $form = array();
        $form['action'] = "/game/gateway/{$unixtime}";
        
        $option = array();
        $option['unixtime'] = $unixtime;
        $option['passportId'] = $passportId;
        $option['token'] = $token;
        $option['bundleId'] = $bundleId;
        $option['sign'] = $sign;
        
        $form['option'] = $option;
        $this->assign('form', $form);
        
        $this->assign('name', __METHOD__);
        $this->display('form.html');
    }


    public function passport_guest()
    {
        $unixtime = time();
        
        $sign = md5("guest" . $unixtime . "mj12321jm");
        
        $form = array();
        $form['action'] = "/passport/guest/{$unixtime}";
        
        $option = array();
        $option['unixtime'] = $unixtime;
        $option['sign'] = $sign;
        
        $form['option'] = $option;
        $this->assign('form', $form);
        
        $this->assign('name', __METHOD__);
        $this->display('form.html');
    }
}
?>