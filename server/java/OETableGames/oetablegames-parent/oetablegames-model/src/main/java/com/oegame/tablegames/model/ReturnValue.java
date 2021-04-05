package com.oegame.tablegames.model;

import java.util.HashMap;

public class ReturnValue<T> 
{
	
	private T value;
	
	private String message;
	
	private boolean hasError;
	
	private Exception exception;
	
	private int returnCode;
	
	private HashMap<String,Object> outputValues;
	
	public ReturnValue()
	{
		outputValues = new HashMap<String,Object>();
	}
	
	public ReturnValue(T value)
	{
		outputValues = new HashMap<String,Object>();
		this.value = value;
	}
	
	public T getValue()
	{
		return this.value;
	}
	
	public void setValue(T value)
	{
		this.value = value;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
	public void setMessage(String value)
	{
		this.message = value;
	}
	
	public boolean getHasError()
	{
		return this.hasError;
	}
	
	public void setHasError(boolean value)
	{
		this.hasError = value;
	}
	
	public Exception getException()
	{
		return this.exception;
	}
	
	public void setException(Exception value)
	{
		this.exception = value;
	}
	
	public int getReturnCode()
	{
		return this.returnCode;
	}
	
	public void setReturnCode(int value)
	{
		this.returnCode = value;
	}
	
	public HashMap<String,Object> getOutputValues()
	{
		return outputValues;
	}
	
	@SuppressWarnings("unchecked")
	public <TM> Object getOutputValue(String key)
	{
		if(!outputValues.containsKey(key))
		{
			return null;
		}
		return (TM)outputValues.get(key);
	}
	
	public <TM> void setOutputValue(String key, TM value)
	{
		outputValues.put(key, value);
	}
	
	

}
