<?php


use Common\Model;

$m_cfg = Model::Config();
$m_cfg_data = $m_cfg->node("where `id` = 1" ,"*");

define('INIT_CARDS', $m_cfg_data['cards']);

define('GUEST_CARDS', 0);