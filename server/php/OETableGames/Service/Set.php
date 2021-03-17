<?php
$client = new swoole_client(SWOOLE_SOCK_TCP);

$client->connect('127.0.0.1', 9501, 0.5);

$client->send(json_encode(array(
    'method' => 'crontab',
    'action' => 'insert',
    'params' => array(
        'tags' => 'flush_product',
        'rule' => '* * * 0 0 0',
        'ctrl' => 'Common\Goods',
        'method' => 'crontab_flush',
        'params' => array(
            'page' => 1
        ),
        'loop' => true
    )
)));

$recv = $client->recv();

$recv = json_decode($recv, true);

echo "\n";
print_r($recv);
echo "\n";

$client->close();
