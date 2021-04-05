package com.zhenyi.remoting.framework.cluster.impl;

import com.zhenyi.remoting.framework.cluster.ClusterStrategy;
import com.zhenyi.remoting.framework.model.ProviderService;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 软负载加权轮询算法实现
 * 
 * @author Binge
 *
 */
public class WeightPollingClusterStrategyImpl implements ClusterStrategy
{

	private int index = 0;
	
	private Lock lock = new ReentrantLock();

	@Override
	public ProviderService select(List<ProviderService> providerServices, Object param)
	{

		ProviderService service = null;
		try
		{
			lock.tryLock(10, TimeUnit.MILLISECONDS);
			
			List<ProviderService> providerList = Lists.newArrayList();
			for (ProviderService provider : providerServices)
			{
				int weight = provider.getWeight();
				for (int i = 0; i < weight; i++)
				{
					providerList.add(provider.copy());
				}
			}
			
			if (index >= providerList.size())
			{
				index = 0;
			}
			service = providerList.get(index);
			index++;
			return service;

		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		} 
		finally
		{
			lock.unlock();
		}

		return providerServices.get(0);
	}
}
