package com.zhenyi.remoting.framework.consumer;

import com.zhenyi.remoting.framework.model.ResponseEntity;
import com.zhenyi.remoting.framework.model.ResponseWrapper;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消费者返回数据持有者
 * @author Binge
 *
 */
public class ConsumerResponseHolder
{
	private static final Logger logger = LoggerFactory.getLogger(ConsumerResponseHolder.class);

	// 服务返回结果Map
	private static final Map<String, ResponseWrapper> responseMap = Maps.newConcurrentMap();
	// 清除过期的返回结果
	private static final ExecutorService removeExpireKeyExecutor = Executors.newSingleThreadExecutor();

	static
	{
		removeExpireKeyExecutor.execute(new Runnable()
		{
			@Override
			public void run()
			{
				while (true)
				{
					for (Map.Entry<String, ResponseWrapper> entry : responseMap.entrySet())
					{
						boolean isExpire = entry.getValue().isExpire();
						if (isExpire)
						{
							responseMap.remove(entry.getKey());
						}
					}
					try
					{
						Thread.sleep(1000);
					} 
					catch (Throwable e)
					{
						e.printStackTrace();
					}
				}
			}
		});
	}

	/**
	 * 初始化返回结果容器,requestUniqueKey唯一标识本次调用
	 *
	 * @param requestUniqueKey
	 */
	public static void initResponseData(String requestUniqueKey)
	{
		responseMap.put(requestUniqueKey, ResponseWrapper.of());
	}

	/**
	 * 将Netty调用异步返回结果放入阻塞队列
	 *
	 * @param response
	 */
	public static void putResultValue(ResponseEntity response)
	{
		long currentTime = System.currentTimeMillis();
		ResponseWrapper responseWrapper = responseMap.get(response.getUniqueKey());
		if(responseWrapper == null)
		{
			logger.error("消息超时！！！！");
			return;
		}
		responseWrapper.setResponseTime(currentTime);
		responseWrapper.getResponseQueue().add(response);
	}

	/**
	 * 从阻塞队列中获取Netty异步返回的结果值
	 *
	 * @param requestUniqueKey
	 * @param timeout
	 * @return
	 */
	public static ResponseEntity getValue(String requestUniqueKey, long timeout)
	{
		ResponseWrapper responseWrapper = responseMap.get(requestUniqueKey);
		try
		{
			return responseWrapper.getResponseQueue().poll(timeout, TimeUnit.MILLISECONDS);
		} 
		catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		} 
		finally
		{
			responseMap.remove(requestUniqueKey);
		}
	}

}
