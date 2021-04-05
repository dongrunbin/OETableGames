package com.zhenyi.remoting.framework.consumer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhenyi.remoting.framework.cluster.ClusterStrategy;
import com.zhenyi.remoting.framework.cluster.engine.ClusterEngine;
import com.zhenyi.remoting.framework.model.ProviderService;
import com.zhenyi.remoting.framework.model.RequestEntity;
import com.zhenyi.remoting.framework.model.ResponseEntity;
import com.zhenyi.remoting.framework.zookeeper.IConsumerServiceCenter;
import com.zhenyi.remoting.framework.zookeeper.ServiceCenter;

public class ConsumerInvoker implements InvocationHandler
{
	
	private static final Logger logger = LoggerFactory.getLogger(ConsumerInvoker.class);
	
	public ConsumerInvoker(Class<?> targetInterface, int consumeTimeout, String clusterStrategy)
	{
		this.targetInterface = targetInterface;
		this.consumeTimeout = consumeTimeout;
		this.clusterStrategy = clusterStrategy;
	}
	
	private ExecutorService fixedThreadPool = null;

	// 服务接口
	private Class<?> targetInterface;
	// 超时时间
	private int consumeTimeout;
	// 调用者线程数
	private static int threadWorkerNumber = 50;
	// 负载均衡策略
	private String clusterStrategy;
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{

		String serviceKey = targetInterface.getName();
		IConsumerServiceCenter registerCenter4Consumer = ServiceCenter.singleton();
		List<ProviderService> providerServices = registerCenter4Consumer.getServiceMetaDataMap4Consume()
				.get(serviceKey);
		if(providerServices == null || providerServices.size() == 0)
		{
			throw new Exception("当前没有服务生产者" + serviceKey);
		}
		ClusterStrategy clusterStrategyService = ClusterEngine.queryClusterStrategy(clusterStrategy);
		int paramIndex = providerServices.get(0).getParamIndex();
		Object param = null;
		if (args != null && args.length > 0)
		{
			if(paramIndex < args.length)
			{
				param = args[paramIndex];
			}
		}
		ProviderService providerService = clusterStrategyService.select(providerServices, param);
		ProviderService newProvider = providerService.copy();
		newProvider.setServiceMethod(method);
		newProvider.setServiceItf(targetInterface);
		
		final RequestEntity request = new RequestEntity();
		request.setUniqueKey(UUID.randomUUID().toString() + "-" + Thread.currentThread().getId());
		request.setProviderService(newProvider);
		request.setInvokeTimeout(consumeTimeout);
		request.setInvokedMethodName(method.getName());
		request.setArgs(args);

		try
		{
			if (null == fixedThreadPool)
			{
				synchronized (ConsumerInvoker.class)
				{
					if (null == fixedThreadPool)
					{
						fixedThreadPool = Executors.newFixedThreadPool(threadWorkerNumber);
					}
				}
			}
			String serverIp = request.getProviderService().getServerIp();
			int serverPort = request.getProviderService().getServerPort();
			InetSocketAddress inetSocketAddress = new InetSocketAddress(serverIp, serverPort);
			Future<ResponseEntity> responseFuture = fixedThreadPool
					.submit(ConsumerServiceCallable.of(inetSocketAddress, request));
			ResponseEntity response = responseFuture.get(request.getInvokeTimeout(), TimeUnit.MILLISECONDS);
			if (response != null)
			{
				return response.getResult();
			}
		} 
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		return null;
	}
}
