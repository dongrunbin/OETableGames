package com.oegame.tablegames.common.log;

public class Logger
{
	
	private org.slf4j.Logger logger;
	
	public Logger(org.slf4j.Logger logger)
	{
		this.logger = logger;
	}
	
	public void debug(String str)
	{
		logger.debug(str);
	}
	
	public void debug(String format, Object[] args)
	{
		logger.debug(format, args);
	}
	
	public void info(String str)
	{
		logger.info(str);
	}
	
	public void info(String format, Object[] args)
	{
		logger.info(format, args);
	}
	
	public void error(String str)
	{
		logger.error(str);
	}
	
	public void error(String format, Object[] args)
	{
		logger.error(format, args);
	}
	
	public void warn(String str)
	{
		logger.warn(str);
	}
	
	public void warn(String format, Object[] args)
	{
		logger.warn(format, args);
	}
	
	public void trace(String str)
	{
		logger.trace(str);
	}
	
	public void trace(String format, Object[] args)
	{
		logger.trace(format, args);
	}
	
	public boolean isDebugEnabled()
	{
		return logger.isDebugEnabled();
	}
	
	public boolean isInfoEnabled()
	{
		return logger.isInfoEnabled();
	}
	
	public boolean isErrorEnabled()
	{
		return logger.isErrorEnabled();
	}
	
	public boolean isWarnEnabled()
	{
		return logger.isWarnEnabled();
	}
	
	public boolean isTraceEnabled()
	{
		return logger.isTraceEnabled();
	}
}
