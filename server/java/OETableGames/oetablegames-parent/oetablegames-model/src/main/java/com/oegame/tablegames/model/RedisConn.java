package com.oegame.tablegames.model;


public class RedisConn 
{
	public static RedisConfig passport = new RedisConfig(
			PropertyConfigHelper.getRedisPassportHost(), PropertyConfigHelper.getRedisPassportPort(), 
			PropertyConfigHelper.getRedisPassportBaseIndex(), PropertyConfigHelper.getRedisPassportPassword());
}
