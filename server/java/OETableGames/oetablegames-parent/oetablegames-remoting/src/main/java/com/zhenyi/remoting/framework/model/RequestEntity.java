package com.zhenyi.remoting.framework.model;

import java.io.Serializable;

/**
 * 请求数据实体类
 * 
 * @author Binge
 *
 */
public class RequestEntity implements Serializable
{

	// UUID,唯一标识一次返回值
	private String uniqueKey;
	// 服务提供者信息
	private ProviderService providerService;
	// 调用的方法名称
	private String invokedMethodName;
	// 传递参数
	private Object[] args;
	// 消费端应用名
	private String appName;
	// 消费请求超时时长
	private long invokeTimeout;

	public String getUniqueKey()
	{
		return uniqueKey;
	}

	public void setUniqueKey(String uniqueKey)
	{
		this.uniqueKey = uniqueKey;
	}

	public ProviderService getProviderService()
	{
		return providerService;
	}

	public void setProviderService(ProviderService providerService)
	{
		this.providerService = providerService;
	}

	public String getInvokedMethodName()
	{
		return invokedMethodName;
	}

	public void setInvokedMethodName(String invokedMethodName)
	{
		this.invokedMethodName = invokedMethodName;
	}

	public Object[] getArgs()
	{
		return args;
	}

	public void setArgs(Object[] args)
	{
		this.args = args;
	}

	public String getAppName()
	{
		return appName;
	}

	public void setAppName(String appName)
	{
		this.appName = appName;
	}

	public long getInvokeTimeout()
	{
		return invokeTimeout;
	}

	public void setInvokeTimeout(long invokeTimeout)
	{
		this.invokeTimeout = invokeTimeout;
	}
}
