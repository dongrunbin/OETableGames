package com.zhenyi.remoting.framework.consumer;

import com.zhenyi.remoting.framework.model.RequestEntity;
import com.zhenyi.remoting.framework.model.ResponseEntity;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Netty 请求发起线程
 * 
 * @author Binge
 *
 */
public class ConsumerServiceCallable implements Callable<ResponseEntity>
{

	private static final Logger logger = LoggerFactory.getLogger(ConsumerServiceCallable.class);

	private Channel channel;
	private InetSocketAddress inetSocketAddress;
	private RequestEntity request;

	public static ConsumerServiceCallable of(InetSocketAddress inetSocketAddress, RequestEntity request)
	{
		return new ConsumerServiceCallable(inetSocketAddress, request);
	}

	public ConsumerServiceCallable(InetSocketAddress inetSocketAddress, RequestEntity request)
	{
		this.inetSocketAddress = inetSocketAddress;
		this.request = request;
	}

	@Override
	public ResponseEntity call() throws Exception
	{
		
		ConsumerResponseHolder.initResponseData(request.getUniqueKey());
		
		ArrayBlockingQueue<Channel> blockingQueue = NettyChannelPoolFactory.acquire(inetSocketAddress);
		try
		{
			if (channel == null)
			{
				channel = blockingQueue.poll(request.getInvokeTimeout(), TimeUnit.MILLISECONDS);
			}
			
			// 若获取的channel通道已经不可用,则重新获取一个
			while (channel == null || !channel.isOpen() || !channel.isActive() || !channel.isWritable())
			{
				logger.warn("----------retry get new Channel------------");
				channel = blockingQueue.poll(request.getInvokeTimeout(), TimeUnit.MILLISECONDS);
				if (channel == null)
				{
					channel = NettyChannelPoolFactory.registerChannel(inetSocketAddress);
				}
			}

			ChannelFuture channelFuture = channel.writeAndFlush(request);
			channelFuture.syncUninterruptibly();

			long invokeTimeout = request.getInvokeTimeout();
			return ConsumerResponseHolder.getValue(request.getUniqueKey(), invokeTimeout);
		} 
		catch (Exception e)
		{
			logger.error("service invoke error.", e);
		} 
		finally
		{
			NettyChannelPoolFactory.release(blockingQueue, channel, inetSocketAddress);
		}
		return null;
	}
}
