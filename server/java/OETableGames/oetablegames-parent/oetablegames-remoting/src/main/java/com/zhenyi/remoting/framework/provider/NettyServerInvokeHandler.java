package com.zhenyi.remoting.framework.provider;

import com.zhenyi.remoting.framework.model.RequestEntity;
import com.zhenyi.remoting.framework.model.ResponseEntity;
import com.zhenyi.remoting.framework.model.ProviderService;
import com.zhenyi.remoting.framework.zookeeper.IProviderServiceCenter;
import com.zhenyi.remoting.framework.zookeeper.ServiceCenter;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 服务端的逻辑处理
 * 
 * @author Binge
 *
 */
@ChannelHandler.Sharable
public class NettyServerInvokeHandler extends SimpleChannelInboundHandler<RequestEntity>
{

	private static final Logger logger = LoggerFactory.getLogger(NettyServerInvokeHandler.class);

	// 服务端限流
	private static final Map<String, Semaphore> serviceKeySemaphoreMap = Maps.newConcurrentMap();

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
	{
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
//		cause.printStackTrace();
		logger.info(cause.toString());
		ctx.close();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RequestEntity request) throws Exception
	{

		if (ctx.channel().isWritable())
		{
			ProviderService metaDataModel = request.getProviderService();
			long consumeTimeOut = request.getInvokeTimeout();
			final String methodName = request.getInvokedMethodName();

			String serviceKey = metaDataModel.getServiceItf().getName();
			
			int workerThread = metaDataModel.getWorkerThreads();
			Semaphore semaphore = serviceKeySemaphoreMap.get(serviceKey);
			if (semaphore == null)
			{
				synchronized (serviceKeySemaphoreMap)
				{
					semaphore = serviceKeySemaphoreMap.get(serviceKey);
					if (semaphore == null)
					{
						semaphore = new Semaphore(workerThread);
						serviceKeySemaphoreMap.put(serviceKey, semaphore);
					}
				}
			}

			IProviderServiceCenter registerCenter4Provider = ServiceCenter.singleton();
			List<ProviderService> localProviderCaches = registerCenter4Provider.getProviderServiceMap().get(serviceKey);

			Object result = null;
			boolean acquire = false;

			try
			{
				ProviderService localProviderCache = Collections2
						.filter(localProviderCaches, new Predicate<ProviderService>()
						{
							@Override
							public boolean apply(ProviderService input)
							{
								return StringUtils.equals(input.getServiceMethod().getName(), methodName);
							}
						}).iterator().next();
				Object serviceObject = localProviderCache.getServiceObject();

				Method method = localProviderCache.getServiceMethod();
				
				acquire = semaphore.tryAcquire(consumeTimeOut, TimeUnit.MILLISECONDS);
				if (acquire)
				{
					result = method.invoke(serviceObject, request.getArgs());
				}
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				logger.error(methodName + " " + e.toString());
				result = e;
			} 
			finally
			{
				if (acquire)
				{
					semaphore.release();
				}
			}

			ResponseEntity response = new ResponseEntity();
			response.setInvokeTimeout(consumeTimeOut);
			response.setUniqueKey(request.getUniqueKey());
			response.setResult(result);

			ctx.writeAndFlush(response);

		} 
		else
		{
			logger.error("------------channel closed!---------------");
		}

	}
}
