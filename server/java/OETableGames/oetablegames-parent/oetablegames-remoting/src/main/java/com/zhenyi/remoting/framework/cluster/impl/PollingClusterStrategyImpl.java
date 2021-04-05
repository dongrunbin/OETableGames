package com.zhenyi.remoting.framework.cluster.impl;

import com.zhenyi.remoting.framework.cluster.ClusterStrategy;
import com.zhenyi.remoting.framework.model.ProviderService;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 软负载轮询算法实现
 * 
 * @author Binge
 *
 */
public class PollingClusterStrategyImpl implements ClusterStrategy
{

	// 计数器
	private int index = 0;
	private Lock lock = new ReentrantLock();

	@Override
	public ProviderService select(List<ProviderService> providerServices, Object param)
	{

		ProviderService service = null;
		try
		{
			lock.tryLock(10, TimeUnit.MILLISECONDS);
			
			if (index >= providerServices.size())
			{
				index = 0;
			}
			service = providerServices.get(index);
			index++;

		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		} 
		finally
		{
			lock.unlock();
		}

		if (service == null)
		{
			service = providerServices.get(0);
		}
		return service;
	}
}
