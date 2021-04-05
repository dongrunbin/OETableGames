package com.oegame.tablegames.model;

public class MySqlParameter 
{
	private String parameterName;
	
	private Object value;
	
	private ParameterDirection direction = ParameterDirection.Input;
	
	private int dbType;
	
	private int size;
	
	public MySqlParameter(String parameterName,Object value)
	{
		this.parameterName = parameterName;
		this.value = value;
	}
	
	public MySqlParameter(String parameterName,int dbType, int size)
	{
		this.parameterName = parameterName;
		this.dbType = dbType;
		this.size = size;
	}
	
	public MySqlParameter(String parameterName, Object value, int dbType, int size)
	{
		this.parameterName = parameterName;
		this.value = value;
		this.dbType = dbType;
		this.size = size;
	}
	
	public String getParameterName()
	{
		return this.parameterName;
	}
	
	public Object getValue()
	{
		return this.value;
	}
	
	public ParameterDirection getDirection()
	{
		return this.direction;
	}
	
	public int getDbType()
	{
		return this.dbType;
	}
	
	public int getSize()
	{
		return this.size;
	}
	
	public void setParameterName(String value)
	{
		this.parameterName = value;
	}
	
	public void setValue(Object value)
	{
		this.value = value;
	}
	
	public void setDirection(ParameterDirection value)
	{
		this.direction = value;
	}
	
	public void setDbType(int value)
	{
		this.dbType = value;
	}
	
	public void setSize(int value)
	{
		this.size = value;
	}

}
