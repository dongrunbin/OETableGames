package com.zhenyi.remoting.framework.cluster;

import com.zhenyi.remoting.framework.model.ProviderService;

import java.util.List;

/**
 * 负载策略算法接口
 * @author Binge
 *
 */
public interface ClusterStrategy 
{

    /**
     * 负载策略算法
     *
     * @param providerServices
     * @return
     */
    public ProviderService select(List<ProviderService> providerServices, Object param);
}
