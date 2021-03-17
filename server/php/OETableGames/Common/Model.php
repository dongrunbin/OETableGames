<?php
namespace Common;

use Core\Util;

class Model
{
	/**
	 *
	 * Passport
	 *
	 * @param int $id
	 *
	 * @return \Entity\Passport|\Model\Passport
	 *
	 */
	public static function Passport($id = 0)
	{
	
		return Util::loadModel('Model\Passport', 'Passport', $id);
	
	}

	/**
	 *
	 * Player
	 *
	 * @param int $id
	 *
	 * @return \Entity\Player|\Model\Player
	 *
	 */
	public static function Player($id = 0)
	{
	
		return Util::loadModel('Model\Player', 'Player', $id);
	
	}

	public static function Server($id = 0)
    {
        return Util::loadModel('Model\Server', 'Server', $id);
    }
	
	
}
?>