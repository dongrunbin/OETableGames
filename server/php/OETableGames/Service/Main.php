<?php
use Core\BaseCtrl;
header("Content-Type: text/html; charset=utf-8");

define('APP_PATH', dirname(__DIR__));

/**
 * 自动加载Class
 */
function sys_autoload($class)
{
    $basePath = str_replace('\\', DIRECTORY_SEPARATOR, $class) . '.php';
    $classFile = APP_PATH . DIRECTORY_SEPARATOR . $basePath;
    
    if (! file_exists($classFile)) {
        $classFile = dirname(__DIR__) . DIRECTORY_SEPARATOR . $basePath;
    }
    
    if (! file_exists($classFile)) {
        $classFile = dirname(__DIR__) . DIRECTORY_SEPARATOR . 'doyo' . DIRECTORY_SEPARATOR . $basePath;
    }
    
    if (! file_exists($classFile)) {
        $classFile = dirname(__DIR__) . DIRECTORY_SEPARATOR . 'doyo/Engine/Smarty/libs/plugins/' . $basePath;
    }
    
    if (! file_exists($classFile)) {
        $classFile = dirname(__DIR__) . DIRECTORY_SEPARATOR . 'doyo/Engine/Smarty/libs/sysplugins/' . $basePath;
    }
    
    if (! file_exists($classFile)) {
        $classFile = dirname(__DIR__) . DIRECTORY_SEPARATOR . 'doyo/Sdk/' . $basePath;
    }
    
    if (file_exists($classFile)) {
        require_once ($classFile);
    }
}

spl_autoload_register('sys_autoload');

$configs = glob(APP_PATH . '/Config/*.php');
foreach ($configs as $config) {
    require_once $config;
}

$commons = glob(APP_PATH . '/Common/*.php');
foreach ($commons as $common) {
    require_once $common;
}

date_default_timezone_set(TIMEZONE);

/**
 * ********************************************
 * Begin
 * ********************************************
 */

$server = new swoole_server("127.0.0.1", 9501, SWOOLE_PROCESS, SWOOLE_SOCK_TCP);

$sprocess = new swoole_process(function ($process) use($server) {
    
    $data = $process->pop();
    
    file_put_contents('/tmp/swoole.process.log', "process->pid: " . $process->pid . " - " . $data . "\n\n", FILE_APPEND);
    
    $ndata = json_decode($data, true);
    
    $params = array();
    
    $type = $ndata['type'];
    
    $pdata = $ndata['params'];
    
    $ctrl = $pdata['ctrl'];
    $method = $pdata['method'];
    $params = $pdata['params'];
    
    $callret = array();
    if ($ctrl && $method) {
        
        $ctrls = new $ctrl();
        
        if ($ctrls instanceof BaseCtrl) {
            if ($params) {
                $ctrls->setParams($params);
            }
            $callret = $ctrls->$method();
        } else {
            if ($params) {
                
                $callret = call_user_func_array(array(
                    $ctrls,
                    $method
                ), $params);
            } else {
                $callret = $ctrls->$method();
            }
        }
    }
    
    $ctrls = null;
    
    $server->sendMessage(json_encode(array(
        'callret' => $callret,
        'type' => $type,
        'params' => $pdata
    )), 0);
    
    $process->exit();
});

$sprocess->useQueue();
$server->addProcess($sprocess);
$server->addProcess($sprocess);
$server->addProcess($sprocess);
$server->addProcess($sprocess);
$server->addProcess($sprocess);

$server->set(array(
    'worker_num' => 5,
    'daemonize' => true
));

$server->crontab = array();

$server->on('WorkerStart', function ($serv, $progress_id) use($sprocess) {
    if (! $serv->taskworker) {
        // crontab
        swoole_timer_tick(1000, function ($interval, $serv) use($sprocess) {
            foreach ($serv->crontab as $crontab) {
                
                $rule = explode(' ', $crontab['rule']);
                $ctrl = $crontab['ctrl'];
                $method = $crontab['method'];
                $params = $crontab['params'];
                $loop = $crontab['loop'];
                
                // 年
                if ($rule[0] != '*' && intval(date('Y')) != intval($rule[0])) {
                    continue;
                }
                // 月
                if ($rule[1] != '*' && intval(date('m')) != intval($rule[1])) {
                    continue;
                }
                // 日
                if ($rule[2] != '*' && intval(date('d')) != intval($rule[2])) {
                    continue;
                }
                // 时
                if ($rule[3] != '*' && intval(date('H')) != intval($rule[3])) {
                    continue;
                }
                // 分
                if ($rule[4] != '*' && intval(date('i')) != intval($rule[4])) {
                    continue;
                }
                // 秒
                if ($rule[5] != '*' && intval(date('s')) != intval($rule[5])) {
                    continue;
                }
                
                $sprocess->push(json_encode(array(
                    'type' => 'crontab',
                    'params' => $crontab
                )));
            }
        }, $serv);
    }
});

$server->on('pipeMessage', function ($serv, $src_worker_id, $jsondata) use($sprocess) {
    $data = json_decode($jsondata, true);
    if ($data['type'] == 'crontab') {
        echo 'crontab: ' . $jsondata;
        echo "\n";
        
        $params = $data['params'];
        if ($params['loop']) {
            if ($data['callret']['page'] < $data['callret']['pcount']) {
                $params['params']['page'] = $params['params']['page'] + 1;
                $sprocess->push(json_encode(array(
                    'type' => 'crontab',
                    'params' => $params
                )));
            }
        }
    } else 
        if ($data['type'] == 'process') {
            echo 'process: ' . $jsondata;
            echo "\n";
        }
});

$server->on('Connect', function ($serv, $fd) {});

$server->on('Receive', function ($serv, $fd, $from_id, $data) use($sprocess) {
    
    $res = json_decode($data, true);
    
    if ($res['method'] == 'crontab') {
        if ($res['action'] == 'select') { // 查询
            $serv->send($fd, json_encode($serv->crontab));
        } else 
            if ($res['action'] == 'insert') { // 新增
                $params = $res['params'];
                
                if (isset($serv->crontab[$params['tags']])) {
                    $serv->send($fd, json_encode(array(
                        'WARNING: tags is exists.'
                    )));
                } else {
                    $serv->crontab[$params['tags']] = $params;
                    $serv->send($fd, json_encode($serv->crontab));
                }
            } else 
                if ($res['action'] == 'delete') { // 删除
                    $params = $res['params'];
                    
                    if (! isset($serv->crontab[$params['tags']])) {
                        $serv->send($fd, json_encode(array(
                            'WARNING: tags not exists.'
                        )));
                    } else {
                        unset($serv->crontab[$params['tags']]);
                        $serv->send($fd, json_encode($serv->crontab));
                    }
                } else 
                    if ($res['action'] == 'update') { // 更改
                        $params = $res['params'];
                        
                        if (! isset($serv->crontab[$params['tags']])) {
                            $serv->send($fd, json_encode(array(
                                'WARNING: tags not exists.'
                            )));
                        } else {
                            $serv->crontab[$params['tags']] = $params;
                            $serv->send($fd, json_encode($serv->crontab));
                        }
                    }
    } else 
        if ($res['method'] == 'process') {
            $sprocess->push(json_encode(array(
                'type' => 'process',
                'params' => $res['params']
            )));
        }
    
    file_put_contents('/tmp/swoole.process.log', $from_id . " - " . $data . "\n\n", FILE_APPEND);
});

$server->on('Shutdown', function ($serv) {
    echo "Shutdown... \n";
});

$server->on('Close', function ($serv, $fd) {});

$server->start();