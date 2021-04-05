package com.zhenyi.remoting.framework.provider;

import com.zhenyi.remoting.framework.helper.IPHelper;
import com.zhenyi.remoting.framework.model.ProviderService;
import com.zhenyi.remoting.framework.zookeeper.IProviderServiceCenter;
import com.zhenyi.remoting.framework.zookeeper.ServiceCenter;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 服务Bean发布入口
 * 
 * @author Binge
 *
 */
public class ProviderFactoryBean implements FactoryBean<Object>, InitializingBean
{

	// 服务接口
	private Class<?> serviceItf;
	// 服务实现
	private Object serviceObject;
	// 服务端口
	private String serverPort;
	// 服务超时时间
	private long timeout;
	// 服务代理对象
	private Object serviceProxyObject;
	// 服务提供者唯一标识
	private String appKey;
	// 服务分组组名
	private String groupName = "default";
	// 服务提供者权重[1-100]
	private int weight = 1;
	// 服务端线程数
	private int workerThreads = 10;
	// 参数索引
	private int paramIndex = 0;
	// 最小参数
	private long minParam = 0;
	// 最大参数
	private long maxParam = 0;

	@Override
	public Object getObject() throws Exception
	{
		return serviceProxyObject;
	}

	@Override
	public Class<?> getObjectType()
	{
		return serviceObject.getClass();
	}

	@Override
	public boolean isSingleton()
	{
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		new NettyServer().start(Integer.parseInt(serverPort));

		List<ProviderService> providerServiceList = buildProviderServiceInfos();
		IProviderServiceCenter registerCenter4Provider = ServiceCenter.singleton();
		registerCenter4Provider.registerProvider(providerServiceList);
	}

	private List<ProviderService> buildProviderServiceInfos()
	{
		List<ProviderService> providerList = Lists.newArrayList();
		Method[] methods = serviceObject.getClass().getMethods();
		for (Method method : methods)
		{
			ProviderService providerService = new ProviderService();
			providerService.setServiceItf(this.serviceItf);
			providerService.setServiceObject(serviceObject);
			providerService.setServerIp(IPHelper.localIp());
			providerService.setServerPort(Integer.parseInt(serverPort));
			providerService.setTimeout(timeout);
			providerService.setServiceMethod(method);
			providerService.setWeight(weight);
			providerService.setWorkerThreads(workerThreads);
			providerService.setAppKey(appKey);
			providerService.setGroupName(groupName);
			providerService.setParamIndex(paramIndex);
			providerService.setMinParam(minParam);
			providerService.setMaxParam(maxParam);
			providerList.add(providerService);
		}
		return providerList;
	}

	public Class<?> getServiceItf()
	{
		return serviceItf;
	}

	public void setServiceItf(Class<?> serviceItf)
	{
		this.serviceItf = serviceItf;
	}

	public Object getServiceObject()
	{
		return serviceObject;
	}

	public void setServiceObject(Object serviceObject)
	{
		this.serviceObject = serviceObject;
	}

	public String getServerPort()
	{
		return serverPort;
	}

	public void setServerPort(String serverPort)
	{
		this.serverPort = serverPort;
	}

	public long getTimeout()
	{
		return timeout;
	}

	public void setTimeout(long timeout)
	{
		this.timeout = timeout;
	}

	public Object getServiceProxyObject()
	{
		return serviceProxyObject;
	}

	public void setServiceProxyObject(Object serviceProxyObject)
	{
		this.serviceProxyObject = serviceProxyObject;
	}

	public int getWeight()
	{
		return weight;
	}

	public void setWeight(int weight)
	{
		this.weight = weight;
	}

	public int getWorkerThreads()
	{
		return workerThreads;
	}

	public void setWorkerThreads(int workerThreads)
	{
		this.workerThreads = workerThreads;
	}

	public String getAppKey()
	{
		return appKey;
	}

	public void setAppKey(String appKey)
	{
		this.appKey = appKey;
	}

	public String getGroupName()
	{
		return groupName;
	}

	public void setGroupName(String groupName)
	{
		this.groupName = groupName;
	}
	
	public int getParamIndex()
	{
		return this.paramIndex;
	}
	
	public void setParamIndex(int paramIndex)
	{
		this.paramIndex = paramIndex;
	}
	
	public long getMinParam()
	{
		return this.minParam;
	}
	
	public void setMinParam(long minParam)
	{
		this.minParam = minParam;
	}
	
	public long getMaxParam()
	{
		return this.maxParam;
	}
	
	public void setMaxParam(long maxParam)
	{
		this.maxParam = maxParam;
	}
}
