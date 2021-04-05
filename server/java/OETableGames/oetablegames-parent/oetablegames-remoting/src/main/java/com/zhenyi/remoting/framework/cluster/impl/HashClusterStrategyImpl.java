package com.zhenyi.remoting.framework.cluster.impl;

import com.zhenyi.remoting.framework.helper.IPHelper;
import com.zhenyi.remoting.framework.cluster.ClusterStrategy;
import com.zhenyi.remoting.framework.model.ProviderService;

import java.util.List;

/**
 * 软负载哈希算法实现
 * 
 * @author Binge
 *
 */
public class HashClusterStrategyImpl implements ClusterStrategy
{

	@Override
	public ProviderService select(List<ProviderService> providerServices, Object param)
	{
		String localIP = IPHelper.localIp();
		int hashCode = localIP.hashCode();
		int size = providerServices.size();

		return providerServices.get(hashCode % size);
	}
}
