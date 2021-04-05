package com.zhenyi.remoting.framework.cluster.impl;

import java.util.List;

import com.zhenyi.remoting.framework.cluster.ClusterStrategy;
import com.zhenyi.remoting.framework.model.ProviderService;

/**
 * 软负载参数负载实现
 * @author Binge
 *
 */
public class ParametersClusterStrategyImpl implements ClusterStrategy
{
	@Override
    public ProviderService select(List<ProviderService> providerServices, Object param) 
	{
		long l = 0;
		try
		{
			l = Long.valueOf(String.valueOf(param));
		}
		catch(Exception e)
		{
			
		}
		for(int i = 0; i < providerServices.size(); ++i)
		{
			ProviderService providerService = providerServices.get(i);
			if(l <= providerService.getMaxParam() && l >= providerService.getMinParam())
			{
				return providerService;
			}
		}
		return providerServices.get(0);
    }
}
