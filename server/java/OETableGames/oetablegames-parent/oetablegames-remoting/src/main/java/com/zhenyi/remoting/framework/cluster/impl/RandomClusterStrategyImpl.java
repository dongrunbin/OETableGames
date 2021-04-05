package com.zhenyi.remoting.framework.cluster.impl;

import com.zhenyi.remoting.framework.cluster.ClusterStrategy;
import com.zhenyi.remoting.framework.model.ProviderService;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;

/**
 * 软负载随机算法实现
 * 
 * @author Binge
 *
 */
public class RandomClusterStrategyImpl implements ClusterStrategy
{
	@Override
	public ProviderService select(List<ProviderService> providerServices, Object param)
	{
		int MAX_LEN = providerServices.size();
		int index = RandomUtils.nextInt(0, MAX_LEN - 1);
		return providerServices.get(index);
	}

}
