package com.zhenyi.remoting.framework.consumer;

import com.zhenyi.remoting.framework.model.ConsumerService;
import com.zhenyi.remoting.framework.model.ProviderService;
import com.zhenyi.remoting.framework.zookeeper.IConsumerServiceCenter;
import com.zhenyi.remoting.framework.zookeeper.IServiceListener;
import com.zhenyi.remoting.framework.zookeeper.ServiceCenter;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

/**
 * 服务bean引入入口
 * 
 * @author Binge
 *
 */
public class ConsumerFactoryBean implements FactoryBean<Object>, InitializingBean
{
	// 服务接口
	private Class<?> targetInterface;
	// 超时时间
	private int timeout;
	// 服务bean
	private Object serviceObject;
	// 负载均衡策略
	private String clusterStrategy;
	// 服务提供者唯一标识
	private String remoteAppKey;
	// 服务分组组名
	private String groupName = "default";

	@Override
	public Object getObject() throws Exception
	{
		return serviceObject;
	}

	@Override
	public Class<?> getObjectType()
	{
		
		return targetInterface;
	}

	@Override
	public boolean isSingleton()
	{
		return false;
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		IConsumerServiceCenter registerCenter4Consumer = ServiceCenter.singleton();
		registerCenter4Consumer.initProviderMap(remoteAppKey, groupName);

		Map<String, List<ProviderService>> providerMap = registerCenter4Consumer.getServiceMetaDataMap4Consume();
		if (MapUtils.isEmpty(providerMap))
		{
//			throw new RuntimeException("service provider list is empty.");
//			return;
		}
		registerCenter4Consumer.registServiceListener(new IServiceListener() 
		{
			public void onServiceRegiste(ProviderService service)
			{
				NettyChannelPoolFactory.addChannelPool(service);
			}
			
			public void onServiceRemove(ProviderService service)
			{
				NettyChannelPoolFactory.removeChannelPool(service);
			}
			
			public void onDisconnect()
			{
				
			}
		});
		NettyChannelPoolFactory.initChannelPoolFactory(providerMap);
		this.serviceObject = this.getProxy(targetInterface, timeout, clusterStrategy);

		ConsumerService invoker = new ConsumerService();
		invoker.setServiceItf(targetInterface);
		invoker.setRemoteAppKey(remoteAppKey);
		invoker.setGroupName(groupName);
		registerCenter4Consumer.registerInvoker(invoker);
	}
	
	public Object getProxy(Class<?> targetInterface, int consumeTimeout, String clusterStrategy)
	{
		return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]
		{ targetInterface }, new ConsumerInvoker(targetInterface, timeout, clusterStrategy));
	}

	public Class<?> getTargetInterface()
	{
		return targetInterface;
	}

	public void setTargetInterface(Class<?> targetInterface)
	{
		this.targetInterface = targetInterface;
	}

	public int getTimeout()
	{
		return timeout;
	}

	public void setTimeout(int timeout)
	{
		this.timeout = timeout;
	}

	public Object getServiceObject()
	{
		return serviceObject;
	}

	public void setServiceObject(Object serviceObject)
	{
		this.serviceObject = serviceObject;
	}

	public String getClusterStrategy()
	{
		return clusterStrategy;
	}

	public void setClusterStrategy(String clusterStrategy)
	{
		this.clusterStrategy = clusterStrategy;
	}

	public String getRemoteAppKey()
	{
		return remoteAppKey;
	}

	public void setRemoteAppKey(String remoteAppKey)
	{
		this.remoteAppKey = remoteAppKey;
	}

	public String getGroupName()
	{
		return groupName;
	}

	public void setGroupName(String groupName)
	{
		this.groupName = groupName;
	}
}
