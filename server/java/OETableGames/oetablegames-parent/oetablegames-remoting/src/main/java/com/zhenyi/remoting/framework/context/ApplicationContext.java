package com.zhenyi.remoting.framework.context;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ApplicationContext
{
	
	
	
	private static ClassPathXmlApplicationContext context = null;
	
	
	
	
	
	public ApplicationContext(String xmlPath)
	{
		context = new ClassPathXmlApplicationContext(xmlPath);
	}
	
	public ApplicationContext(String... xmlPath)
	{
		context = new ClassPathXmlApplicationContext(xmlPath);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getBean(String name)
	{
		return (T)context.getBean(name);
	}
	
	public <T> T getBean(Class<T> requiredType)
	{
		return context.getBean(requiredType);
	}
	
	public boolean containsBean(String name)
	{
		return context.containsBean(name);
	}
}
