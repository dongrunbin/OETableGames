<?php
/**
 * Created by PhpStorm.
 * User: Binge
 * Date: 2018/6/8
 * Time: 14:26
 */

namespace Entity;

use Core\BaseEntity;

class Server extends BaseEntity
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
    var $status;

    /**
     *
     *
     *
     * @var string
     */
    var $gameId;

    /**
     *
     *
     *
     * @var string
     */
    var $host;

    /**
     *
     *
     *
     * @var string
     */
    var $port;

    /**
     *
     *
     *
     * @var int
     */
    var $createtime;

    /**
     *
     *
     *
     * @var string
     */
    var $updatetime;
}
?>