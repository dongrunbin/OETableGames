package com.oegame.tablegames.model;

import java.util.HashMap;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisHelper
{
    private static final Logger logger = LoggerFactory.getLogger(RedisHelper.class);
	private static HashMap<RedisConfig,JedisPool> redisMap = new HashMap<RedisConfig,JedisPool>();
	
	public static Jedis getRedis(RedisConfig config)
	{
		if(!redisMap.containsKey(config))
		{
			int maxActive = -1;
			int maxIdle = 10;
			int minIdle = 5;
//			int maxWait = 5000;

			JedisPoolConfig poolConfig = new JedisPoolConfig();
			//同一时间可以从池分配的最多连接数量。设置为-1时表示无限制。
			poolConfig.setMaxTotal(maxActive);
			//池里不会被释放的最多空闲连接数量。设置为-1时表示无限制。
			poolConfig.setMaxIdle(maxIdle);
			//在不新建连接的条件下，池中保持空闲的最少连接数。
			poolConfig.setMinIdle(minIdle);
			//在抛出异常之前，池等待连接被回收的最长时间（当没有可用连接时）。设置为-1表示无限等待。
//			poolConfig.setMaxWaitMillis(maxWait);

			//返回连接时是否进行有效性验证（即是否还和数据库连通的）
			//config.setTestOnReturn(true);

			//每过timeBetweenEvictionRunsMillis 时间，就会启动一个线程
			//config.setTimeBetweenEvictionRunsMillis(3000);
			//校验连接池中闲置时间超过minEvictableIdleTimeMillis的连接对象
			//config.setMinEvictableIdleTimeMillis(180000);
			

			redisMap.put(config,new JedisPool(poolConfig, config.getHost(), config.getPort(), 0, config.getPassword(), config.getDataBaseIndex()));
		}
		
		Jedis redis = null;

		boolean broken = false;
		
		JedisPool pool = redisMap.get(config);
		try 
		{
			redis = pool.getResource();
		} 
		catch (JedisException e) 
		{
			broken = handleJedisException(e,pool);

		} 
//		finally 
//		{
//			closeResource(redis, broken, pool);
//		}

		return redis;
	}
	
	private static boolean handleJedisException(JedisException jedisException, JedisPool pool)
	{
		if (jedisException instanceof JedisConnectionException) 
		{
			logger.warn("Redis connection " + pool + " lost.");
		} 
		else if (jedisException instanceof JedisDataException)
		{
			if ((jedisException.getMessage() != null) && (jedisException.getMessage().indexOf("READONLY") != -1)) 
			{
				logger.warn("Redis connection " + pool + " are read-only slave.");
			} 
			else 
			{
				return false;
			}
		} 
		else 
		{
			logger.warn("Jedis exception happen.");
		}
		return true;
	}
	
	@SuppressWarnings("deprecation")
	private static void closeResource(Jedis jedis, boolean broken, JedisPool pool) 
	{
		try 
		{
			if (broken)
			{
				pool.returnBrokenResource(jedis);
			} else {
				pool.returnResource(jedis);
			}
		} 
		catch (Exception e) 
		{
			jedis = null;
		}
	}
}
