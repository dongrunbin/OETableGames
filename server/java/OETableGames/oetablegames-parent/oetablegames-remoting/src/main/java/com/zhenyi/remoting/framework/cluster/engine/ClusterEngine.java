package com.zhenyi.remoting.framework.cluster.engine;

import com.zhenyi.remoting.framework.cluster.ClusterStrategy;
import com.zhenyi.remoting.framework.cluster.impl.*;
import avro.shaded.com.google.common.collect.Maps;

import java.util.Map;

/**
 * 负载均衡引擎
 * @author Binge
 *
 */
public class ClusterEngine
{

	private static final Map<ClusterStrategyEnum, ClusterStrategy> clusterStrategyMap = Maps.newConcurrentMap();

	static
	{
		clusterStrategyMap.put(ClusterStrategyEnum.Random, new RandomClusterStrategyImpl());
		clusterStrategyMap.put(ClusterStrategyEnum.WeightRandom, new WeightRandomClusterStrategyImpl());
		clusterStrategyMap.put(ClusterStrategyEnum.Polling, new PollingClusterStrategyImpl());
		clusterStrategyMap.put(ClusterStrategyEnum.WeightPolling, new WeightPollingClusterStrategyImpl());
		clusterStrategyMap.put(ClusterStrategyEnum.Hash, new HashClusterStrategyImpl());
		clusterStrategyMap.put(ClusterStrategyEnum.Parameters, new ParametersClusterStrategyImpl());
	}

	public static ClusterStrategy queryClusterStrategy(String clusterStrategy)
	{
		ClusterStrategyEnum clusterStrategyEnum = ClusterStrategyEnum.queryByCode(clusterStrategy);
		if (clusterStrategyEnum == null)
		{
			// 默认选择随机算法
			return new RandomClusterStrategyImpl();
		}

		return clusterStrategyMap.get(clusterStrategyEnum);
	}

}
