<?php
namespace Ctrl;

use Core\BaseCtrl;
use Core\Util;
use Common\Model;

class Game extends BaseCtrl {

    public function hf() {

        echo date("Y-m-d H:i:s", 1513489181);
    
    }

    /**
     * 获取apiurl
     * @string gameName
     * @string platform
     */
    public function init() {

        $unixtime = $this->getString(1, true);
        
        $platform = $this->getString("platform", true);
        
        $sign = $this->getString("sign", true);
        
        $md5 = md5("init" . $platform . $unixtime . ApiTools::$key);
        
        if (strtolower($md5) != strtolower($sign)) {
            $ret = array ();
            $ret['code'] = ERR_SIGN_ERROR;
            $ret['msg'] = 'sign error';
            $ret['data'] = array ();
            echo json_encode($ret);
            exit();
        }

        $url = "https://wangqueshenhe.oss-cn-beijing.aliyuncs.com/";
        
        $ret = array ();
        $ret['code'] = 1;
        $ret['msg'] = 'succeed';
        $ret['data'] = array (
            'downloadUrl' => $url 
        );
        
        echo json_encode($ret);
    
    }
}

?>