package com.oegame.tablegames.common.log;


public class LoggerFactory
{
	public static Logger getLogger(Class<?> obj)
	{
		org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(obj);
		return new Logger(logger);
	}
}
