<?php
/**
 * Created by PhpStorm.
 * User: Binge
 * Date: 2018/5/15
 * Time: 13:31
 */

namespace Model;


class TestEntity
{
    public $id;

    public $name;

    public $description;

    function __construct(){}


    public function serialize()
    {
        pack("qhh",$this->id,$this->name,$this->description);
    }

    public static function deserialize($data)
    {
        $array = unpack("qhh",$data);
        $entity = new TestEntity();
        $entity->id = $array[0];
        $entity->name = $array[1];
        $entity->description = $array[2];
        return $entity;
    }
}