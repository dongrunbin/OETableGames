package com.zhenyi.remoting.framework.cluster.impl;

import java.util.List;

import com.zhenyi.remoting.framework.cluster.ClusterStrategy;
import com.zhenyi.remoting.framework.model.ProviderService;

public class ParametersHashClusterStrategyImpl implements ClusterStrategy
{
	@Override
    public ProviderService select(List<ProviderService> providerServices, Object param) 
	{
		int hashCode = 0;
		try
		{
			hashCode = param.hashCode();
		}
		catch(Exception e)
		{
			
		}
		int size = providerServices.size();
		return providerServices.get(hashCode % size);
    }
}
