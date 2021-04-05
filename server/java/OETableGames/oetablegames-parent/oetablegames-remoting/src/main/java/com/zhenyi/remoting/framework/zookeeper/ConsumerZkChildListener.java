package com.zhenyi.remoting.framework.zookeeper;

import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.zhenyi.remoting.framework.model.ProviderService;

public class ConsumerZkChildListener implements IZkChildListener
{

	@Override
	public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception
	{
		if (currentChilds == null)
		{
			currentChilds = Lists.newArrayList();
		}
		String[] serviceInfos = StringUtils.split(parentPath, '/');
		String serviceName = serviceInfos[serviceInfos.length - 2];
		
		List<ProviderService> lst = Lists.newArrayList();
		for(String serviceInfo : currentChilds)
		{
			String serverIp = StringUtils.split(serviceInfo, "|")[0];
			int serverPort = Integer.parseInt(StringUtils.split(serviceInfo, "|")[1]);
			int weight = Integer.parseInt(StringUtils.split(serviceInfo, "|")[2]);
			int workerThreads = Integer.parseInt(StringUtils.split(serviceInfo, "|")[3]);
			String group = StringUtils.split(serviceInfo, "|")[4];
			ProviderService providerService = new ProviderService();
			try
			{
				providerService.setServiceItf(ClassUtils.getClass(serviceName));
			} 
			catch (ClassNotFoundException e)
			{
				throw new RuntimeException(e);
			}

			providerService.setServerIp(serverIp);
			providerService.setServerPort(serverPort);
			providerService.setWeight(weight);
			providerService.setWorkerThreads(workerThreads);
			providerService.setGroupName(group);
			lst.add(providerService);
		}
		ServiceCenter.singleton().refreshServiceMetaDataMap(serviceName, lst);
	}
}
