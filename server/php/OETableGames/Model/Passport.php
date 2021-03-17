<?php
namespace Model;

use Core\BaseModel;

class Passport extends BaseModel
{

    public function myfunc()
    {
        echo "myfunc";
    }

    public function getAll($sql)
    {
        return $this->query($sql);
    }
}
?>