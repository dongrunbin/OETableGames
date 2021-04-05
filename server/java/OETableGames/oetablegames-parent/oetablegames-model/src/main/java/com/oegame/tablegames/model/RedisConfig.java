package com.oegame.tablegames.model;

public class RedisConfig
{
	private String host;
	private int port;
	private int dataBaseIndex;
	private String password;
	
	
	public int maxActive = 10;
	public int maxIdle = 5;
	public int minIdle = 1;
	public int maxWait = 5000;
	
	public RedisConfig(String host, int port, int dataBaseIndex, String password)
	{
		this.host = host;
		this.port = port;
		this.dataBaseIndex = dataBaseIndex;
		this.password = password;
	}
	
	public String getHost()
	{
		return this.host;
	}
	
	public int getPort()
	{
		return this.port;
	}
	
	public int getDataBaseIndex()
	{
		return this.dataBaseIndex;
	}
	
	public String getPassword()
	{
		return this.password;
	}
}
