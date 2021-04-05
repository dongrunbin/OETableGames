package com.zhenyi.remoting.framework.zookeeper;

import com.zhenyi.remoting.framework.model.ConsumerService;
import com.zhenyi.remoting.framework.model.ProviderService;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * 服务治理接口
 * @author Binge
 *
 */
public interface IGovernanceServiceCenter 
{

    /**
     * 获取服务提供者列表与服务消费者列表
     *
     * @param serviceName
     * @param appKey
     * @return
     */
    public Pair<List<ProviderService>, List<ConsumerService>> queryProvidersAndInvokers(String serviceName, String appKey);


}
