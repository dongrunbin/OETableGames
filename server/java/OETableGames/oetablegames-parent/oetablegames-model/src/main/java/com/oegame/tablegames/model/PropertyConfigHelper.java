package com.oegame.tablegames.model;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyConfigHelper
{

	private static final Logger logger = LoggerFactory.getLogger(PropertyConfigHelper.class);

	private static final String PROPERTY_CLASSPATH = "/model.properties";
	private static final Properties properties = new Properties();

	
	private static String mysqlUsername = "";
	
	private static String mysqlPassword = "";
	
	private static String redisPassportHost = "";
	
	private static int redisPassportPort;
	
	private static int redisPassportBaseIndex;
	
	private static String redisPassportPassword = "";
	
	private static String mysqlPassportConnStr = "";
	
	private static String mysqlLogConnStr = "";
	
	private static String mysqlStatisticsConnStr = "";

	/**
	 * 初始化
	 */
	static
	{
		InputStream is = null;
		try
		{
			is = PropertyConfigHelper.class.getResourceAsStream(PROPERTY_CLASSPATH);
			if (null == is)
			{
				throw new IllegalStateException("remoting.properties can not found in the classpath.");
			}
			properties.load(is);
			mysqlUsername = properties.getProperty("mysql_username");
			mysqlPassword = properties.getProperty("mysql_password");
			redisPassportHost = properties.getProperty("redis_passport_host");
			redisPassportPort = Integer.parseInt(properties.getProperty("redis_passport_port", "0"));
			redisPassportBaseIndex = Integer.parseInt(properties.getProperty("redis_passport_base_index", "0"));
			redisPassportPassword = properties.getProperty("redis_passport_password");
			
			mysqlPassportConnStr = properties.getProperty("mysql_passport_conn_str");
			mysqlLogConnStr = properties.getProperty("mysql_log_conn_str");
			mysqlStatisticsConnStr = properties.getProperty("mysql_statistics_conn_str");
		} 
		catch (Throwable t)
		{
			logger.warn("load remoting's properties file failed.", t);
			throw new RuntimeException(t);
		} 
		finally
		{
			if (null != is)
			{
				try
				{
					is.close();
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public static String getRedisPassportHost()
	{
		return redisPassportHost;
	}

	public static int getRedisPassportPort()
	{
		return redisPassportPort;
	}

	public static int getRedisPassportBaseIndex()
	{
		return redisPassportBaseIndex;
	}
	
	public static String getRedisPassportPassword()
	{
		return redisPassportPassword;
	}
	
	public static String getMysqlPassportConnStr()
	{
		return mysqlPassportConnStr;
	}
	
	public static String getMysqlLogConnStr()
	{
		return mysqlLogConnStr;
	}
	
	public static String getMysqlStatisticsConnStr()
	{
		return mysqlStatisticsConnStr;
	}
	
	public static String getMysqlPassword()
	{
		return mysqlPassword;
	}
	
	public static String getMysqlUsername()
	{
		return mysqlUsername;
	}
}
