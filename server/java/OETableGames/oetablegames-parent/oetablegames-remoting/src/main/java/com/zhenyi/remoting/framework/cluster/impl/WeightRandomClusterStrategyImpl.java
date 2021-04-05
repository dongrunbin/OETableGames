package com.zhenyi.remoting.framework.cluster.impl;

import com.zhenyi.remoting.framework.cluster.ClusterStrategy;
import com.zhenyi.remoting.framework.model.ProviderService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;

/**
 * 软负载加权随机算法实现
 * 
 * @author Binge
 *
 */
public class WeightRandomClusterStrategyImpl implements ClusterStrategy
{

	@Override
	public ProviderService select(List<ProviderService> providerServices, Object param)
	{
		List<ProviderService> providerList = Lists.newArrayList();
		for (ProviderService provider : providerServices)
		{
			int weight = provider.getWeight();
			for (int i = 0; i < weight; i++)
			{
				providerList.add(provider.copy());
			}
		}

		int MAX_LEN = providerList.size();
		int index = RandomUtils.nextInt(0, MAX_LEN - 1);
		return providerList.get(index);
	}
}
