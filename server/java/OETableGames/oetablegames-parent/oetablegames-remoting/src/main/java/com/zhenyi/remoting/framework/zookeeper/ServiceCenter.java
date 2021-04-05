package com.zhenyi.remoting.framework.zookeeper;

import com.zhenyi.remoting.framework.helper.IPHelper;
import com.zhenyi.remoting.framework.helper.PropertyConfigeHelper;
import com.zhenyi.remoting.framework.model.ConsumerService;
import com.zhenyi.remoting.framework.model.ProviderService;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 服务注册中心
 * 
 * @author Binge
 *
 */
public class ServiceCenter implements IConsumerServiceCenter, IProviderServiceCenter, IGovernanceServiceCenter
{
	private static final Logger logger = LoggerFactory.getLogger(ServiceCenter.class);
	
	private static ServiceCenter serviceCenter = new ServiceCenter();

	private static final Map<String, List<ProviderService>> providerServiceMap = Maps.newConcurrentMap();
	
	private static final Map<String, List<ProviderService>> serviceMetaDataMap4Consume = com.google.common.collect.Maps
			.newConcurrentMap();
	
	private static final Map<String, IZkChildListener> changedListenerMap = Maps.newConcurrentMap();

	private static String ZK_SERVICE = PropertyConfigeHelper.getZkService();
	private static int ZK_SESSION_TIME_OUT = PropertyConfigeHelper.getZkConnectionTimeout();
	private static int ZK_CONNECTION_TIME_OUT = PropertyConfigeHelper.getZkConnectionTimeout();
	private static String ROOT_PATH = "/config_register";
	public static String PROVIDER_TYPE = "provider";
	public static String CONSUMER_TYPE = "consumer";
	private static volatile ZkClient zkClient = null;
	
	private List<IServiceListener> serviceListeners = Lists.newArrayList();

	private ServiceCenter()
	{
	}

	public static ServiceCenter singleton()
	{
		return serviceCenter;
	}

	@Override
	public void registerProvider(final List<ProviderService> serviceMetaData)
	{
		if (CollectionUtils.isEmpty(serviceMetaData))
		{
			return;
		}

		synchronized (ServiceCenter.class)
		{
			for (ProviderService provider : serviceMetaData)
			{
				String serviceItfKey = provider.getServiceItf().getName();

				List<ProviderService> providers = providerServiceMap.get(serviceItfKey);
				if (providers == null)
				{
					providers = Lists.newArrayList();
				}
				providers.add(provider);
				providerServiceMap.put(serviceItfKey, providers);
			}

			if (zkClient == null)
			{
				zkClient = new ZkClient(ZK_SERVICE, ZK_SESSION_TIME_OUT, ZK_CONNECTION_TIME_OUT,
						new SerializableSerializer());
				zkClient.subscribeStateChanges(new ProviderZkStateListener());
			}

			String APP_KEY = serviceMetaData.get(0).getAppKey();
			String ZK_PATH = ROOT_PATH + "/" + APP_KEY;
			boolean exist = zkClient.exists(ZK_PATH);
			if (!exist)
			{
				zkClient.createPersistent(ZK_PATH, true);
			}

			for (Map.Entry<String, List<ProviderService>> entry : providerServiceMap.entrySet())
			{
				String groupName = entry.getValue().get(0).getGroupName();
				
				String serviceNode = entry.getKey();
				String servicePath = ZK_PATH + "/" + groupName + "/" + serviceNode + "/" + PROVIDER_TYPE;
				exist = zkClient.exists(servicePath);
				if (!exist)
				{
					zkClient.createPersistent(servicePath, true);
				}

				int serverPort = entry.getValue().get(0).getServerPort();
				int weight = entry.getValue().get(0).getWeight();
				int workerThreads = entry.getValue().get(0).getWorkerThreads();
				int paramIndex = entry.getValue().get(0).getParamIndex();
				long minParam = entry.getValue().get(0).getMinParam();
				long maxParam = entry.getValue().get(0).getMaxParam();
				String localIp = IPHelper.localIp();
				String currentServiceIpNode = servicePath + "/" + localIp + "|" + serverPort + "|" + weight + "|"
						+ workerThreads + "|" + groupName + "|" + paramIndex + "|" + minParam + "|" + maxParam;
				exist = zkClient.exists(currentServiceIpNode);
				if (!exist)
				{
					//临时节点
					zkClient.createEphemeral(currentServiceIpNode);
				}
				
				zkClient.subscribeChildChanges(servicePath, new IZkChildListener()
				{
					@Override
					public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception
					{
						if (currentChilds == null)
						{
							currentChilds = Lists.newArrayList();
						}

						List<String> activityServiceIpList = Lists
								.newArrayList(Lists.transform(currentChilds, new Function<String, String>()
								{
									@Override
									public String apply(String input)
									{
										return StringUtils.split(input, "|")[0];
									}
								}));
						refreshActivityService(activityServiceIpList);
					}
				});

			}
		}
	}

	@Override
	public Map<String, List<ProviderService>> getProviderServiceMap()
	{
		return providerServiceMap;
	}

	@Override
	public void initProviderMap(String remoteAppKey, String groupName)
	{
		serviceMetaDataMap4Consume.putAll(fetchOrUpdateServiceMetaData(remoteAppKey, groupName));
	}

	@Override
	public Map<String, List<ProviderService>> getServiceMetaDataMap4Consume()
	{
		return serviceMetaDataMap4Consume;
	}

	@Override
	public void registerInvoker(ConsumerService consumer)
	{
		if (consumer == null)
		{
			return;
		}
		synchronized (ServiceCenter.class)
		{

			if (zkClient == null)
			{
				zkClient = new ZkClient(ZK_SERVICE, ZK_SESSION_TIME_OUT, ZK_CONNECTION_TIME_OUT,
						new SerializableSerializer());
			}
			
			boolean exist = zkClient.exists(ROOT_PATH);
			if (!exist)
			{
				zkClient.createPersistent(ROOT_PATH, true);
			}
			
			String remoteAppKey = consumer.getRemoteAppKey();
			String groupName = consumer.getGroupName();
			String serviceNode = consumer.getServiceItf().getName();
			String servicePath = ROOT_PATH + "/" + remoteAppKey + "/" + groupName + "/" + serviceNode + "/"
					+ CONSUMER_TYPE;
			exist = zkClient.exists(servicePath);
			if (!exist)
			{
				zkClient.createPersistent(servicePath, true);
			}

			String localIp = IPHelper.localIp();
			String currentServiceIpNode = servicePath + "/" + localIp;
			exist = zkClient.exists(currentServiceIpNode);
			if (!exist)
			{
				//临时节点
				zkClient.createEphemeral(currentServiceIpNode);
			}
			
			logger.info("消费者注册完成" + consumer.getServiceItf().getName());
		}
	}

	private void refreshActivityService(List<String> serviceIpList)
	{
		if (serviceIpList == null)
		{
			serviceIpList = Lists.newArrayList();
		}

		Map<String, List<ProviderService>> currentServiceMetaDataMap = Maps.newHashMap();
		for (Map.Entry<String, List<ProviderService>> entry : providerServiceMap.entrySet())
		{
			String key = entry.getKey();
			List<ProviderService> providerServices = entry.getValue();

			List<ProviderService> serviceMetaDataModelList = currentServiceMetaDataMap.get(key);
			if (serviceMetaDataModelList == null)
			{
				serviceMetaDataModelList = Lists.newArrayList();
			}

			for (ProviderService serviceMetaData : providerServices)
			{
				if (serviceIpList.contains(serviceMetaData.getServerIp()))
				{
					serviceMetaDataModelList.add(serviceMetaData);
				}
			}
			currentServiceMetaDataMap.put(key, serviceMetaDataModelList);
		}
		providerServiceMap.clear();
		System.out.println("currentServiceMetaDataMap," + JSON.toJSONString(currentServiceMetaDataMap));
		providerServiceMap.putAll(currentServiceMetaDataMap);
	}

	public void refreshServiceMetaDataMap(String serviceName, List<ProviderService> serviceInfoList)
	{
		if(!serviceMetaDataMap4Consume.containsKey(serviceName))
		{
			return;
		}
		List<ProviderService> currentProvider = serviceMetaDataMap4Consume.get(serviceName);
		if(serviceInfoList.size() > currentProvider.size())
		{
			for(ProviderService serviceInfo : serviceInfoList)
			{
				boolean isExists = false;
				for(int j = 0; j < currentProvider.size(); ++j)
				{
					if(StringUtils.equals(currentProvider.get(j).getServerIp(), serviceInfo.getServerIp()) && currentProvider.get(j).getServerPort() == serviceInfo.getServerPort())
					{
						isExists = true;
						break;
					}
				}
				if(!isExists)
				{
					logger.info("新增服务:" + serviceName + serviceInfo.getServerIp() + "|" + serviceInfo.getServerPort());
					currentProvider.add(serviceInfo);
					
					if(this.serviceListeners != null)
					{
						for(int k = 0; k < this.serviceListeners.size(); ++k)
						{
							this.serviceListeners.get(k).onServiceRegiste(serviceInfo);
						}
					}
				}
			}
		}
		else if(serviceInfoList.size() < currentProvider.size())
		{
			for(int i = currentProvider.size() - 1; i >=0 ; --i)
			{
				ProviderService service = currentProvider.get(i);
				boolean isExists = false;
				for(int j = 0; j < serviceInfoList.size(); ++j)
				{
					if(StringUtils.equals(serviceInfoList.get(j).getServerIp(), service.getServerIp()) && serviceInfoList.get(j).getServerPort() == service.getServerPort())
					{
						isExists = true;
						break;
					}
				}
				if(!isExists)
				{
					logger.info("注销服务:" + serviceName + service.getServerIp() + "|" + service.getServerPort());
					currentProvider.remove(service);
					
					if(this.serviceListeners != null)
					{
						for(int k = 0; k < this.serviceListeners.size(); ++k)
						{
							this.serviceListeners.get(k).onServiceRemove(service);
						}
					}
				}
			}
		}
		
		logger.info("服务提供变更，当前所有服务:");
		for(Map.Entry<String, List<ProviderService>> entry : serviceMetaDataMap4Consume.entrySet())
		{
			if(entry.getValue().size() == 0) continue;
			logger.info("服务名称 :" + entry.getKey());
			for(ProviderService service : entry.getValue())
			{
				logger.info("服务信息:" + service.getServerIp() + "|" + service.getServerPort());
			}
		}
	}

	public void registServiceListener(IServiceListener listener)
	{
		if(listener == null) return;
		serviceListeners.add(listener);
	}
	
	private Map<String, List<ProviderService>> fetchOrUpdateServiceMetaData(String remoteAppKey, String groupName)
	{
		final Map<String, List<ProviderService>> providerServiceMap = Maps.newConcurrentMap();
		
		synchronized (ServiceCenter.class)
		{
			if (zkClient == null)
			{
				zkClient = new ZkClient(ZK_SERVICE, ZK_SESSION_TIME_OUT, ZK_CONNECTION_TIME_OUT,
						new SerializableSerializer());
				zkClient.subscribeStateChanges(new ConsumerZkStateListener());
			}
		}

		String providePath = ROOT_PATH + "/" + remoteAppKey + "/" + groupName;
		List<String> providerServices = zkClient.getChildren(providePath);

		for (String serviceName : providerServices)
		{
			String servicePath = providePath + "/" + serviceName + "/" + PROVIDER_TYPE;
			List<String> ipPathList = zkClient.getChildren(servicePath);
			List<ProviderService> providerServiceList = Lists.newArrayList();
			providerServiceMap.put(serviceName, providerServiceList);
			for (String ipPath : ipPathList)
			{
				String[] providerNodeArr = StringUtils.split(ipPath, "|");
				String serverIp = providerNodeArr[0];
				String serverPort = providerNodeArr[1];
				int weight = Integer.parseInt(providerNodeArr[2]);
				int workerThreads = Integer.parseInt(providerNodeArr[3]);
				String group = providerNodeArr[4];
				int paramIndex = Integer.parseInt(providerNodeArr[5]);
				long minParam = Long.parseLong(providerNodeArr[6]);
				long maxParam = Long.parseLong(providerNodeArr[7]);
				
				ProviderService providerService = new ProviderService();

				try
				{
					providerService.setServiceItf(ClassUtils.getClass(serviceName));
				} catch (ClassNotFoundException e)
				{
					throw new RuntimeException(e);
				}

				providerService.setServerIp(serverIp);
				providerService.setServerPort(Integer.parseInt(serverPort));
				providerService.setWeight(weight);
				providerService.setWorkerThreads(workerThreads);
				providerService.setGroupName(group);
				providerService.setParamIndex(paramIndex);
				providerService.setMinParam(minParam);
				providerService.setMaxParam(maxParam);
				providerServiceList.add(providerService);
			}
			if(changedListenerMap.containsKey(servicePath))
			{
				continue;
			}
			IZkChildListener listener = new ConsumerZkChildListener();
			zkClient.subscribeChildChanges(servicePath, listener);
			changedListenerMap.put(servicePath, listener);
		}
		return providerServiceMap;
	}

	@Override
	public Pair<List<ProviderService>, List<ConsumerService>> queryProvidersAndInvokers(String serviceName,
			String appKey)
	{
		List<ConsumerService> consumerServices = Lists.newArrayList();
		
		List<ProviderService> providerServices = Lists.newArrayList();

		if (zkClient == null)
		{
			synchronized (ServiceCenter.class)
			{
				if (zkClient == null)
				{
					zkClient = new ZkClient(ZK_SERVICE, ZK_SESSION_TIME_OUT, ZK_CONNECTION_TIME_OUT,
							new SerializableSerializer());
				}
			}
		}

		String parentPath = ROOT_PATH + "/" + appKey;
		
		List<String> groupServiceList = zkClient.getChildren(parentPath);
		if (CollectionUtils.isEmpty(groupServiceList))
		{
			return Pair.of(providerServices, consumerServices);
		}

		for (String group : groupServiceList)
		{
			String groupPath = parentPath + "/" + group;
			
			List<String> serviceList = zkClient.getChildren(groupPath);
			if (CollectionUtils.isEmpty(serviceList))
			{
				continue;
			}
			for (String service : serviceList)
			{
				String servicePath = groupPath + "/" + service;
				List<String> serviceTypes = zkClient.getChildren(servicePath);
				if (CollectionUtils.isEmpty(serviceTypes))
				{
					continue;
				}
				for (String serviceType : serviceTypes)
				{
					if (StringUtils.equals(serviceType, PROVIDER_TYPE))
					{
						String providerPath = servicePath + "/" + serviceType;
						List<String> providers = zkClient.getChildren(providerPath);
						if (CollectionUtils.isEmpty(providers))
						{
							continue;
						}

						for (String provider : providers)
						{
							String[] providerNodeArr = StringUtils.split(provider, "|");

							ProviderService providerService = new ProviderService();
							providerService.setAppKey(appKey);
							providerService.setGroupName(group);
							providerService.setServerIp(providerNodeArr[0]);
							providerService.setServerPort(Integer.parseInt(providerNodeArr[1]));
							providerService.setWeight(Integer.parseInt(providerNodeArr[2]));
							providerService.setWorkerThreads(Integer.parseInt(providerNodeArr[3]));
							providerService.setParamIndex(Integer.parseInt(providerNodeArr[5]));
							providerService.setMinParam(Integer.parseInt(providerNodeArr[6]));
							providerService.setMaxParam(Integer.parseInt(providerNodeArr[7]));
							providerServices.add(providerService);
						}

					} 
					else if (StringUtils.equals(serviceType, CONSUMER_TYPE))
					{
						String consumerPath = servicePath + "/" + serviceType;
						List<String> consumers = zkClient.getChildren(consumerPath);
						if (CollectionUtils.isEmpty(consumers))
						{
							continue;
						}

						for (String consumer : consumers)
						{
							ConsumerService consumerService = new ConsumerService();
							consumerService.setRemoteAppKey(appKey);
							consumerService.setGroupName(group);
							consumerService.setInvokerIp(consumer);
							consumerServices.add(consumerService);
						}
					}
				}
			}

		}
		return Pair.of(providerServices, consumerServices);
	}
}
