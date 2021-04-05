package com.zhenyi.remoting.framework.consumer;

import com.zhenyi.remoting.framework.helper.PropertyConfigeHelper;
import com.zhenyi.remoting.framework.model.ResponseEntity;
import com.zhenyi.remoting.framework.model.ProviderService;
import com.zhenyi.remoting.framework.serialization.NettyDecoderHandler;
import com.zhenyi.remoting.framework.serialization.NettyEncoderHandler;
import com.zhenyi.remoting.framework.serialization.common.SerializeType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * Netty信道池工厂
 * 
 * @author Binge
 *
 */
public class NettyChannelPoolFactory
{

	private static final Logger logger = LoggerFactory.getLogger(NettyChannelPoolFactory.class);

	private static final Map<InetSocketAddress, ArrayBlockingQueue<Channel>> channelPoolMap = Maps.newConcurrentMap();
	
	private static final int channelConnectSize = PropertyConfigeHelper.getChannelConnectSize();
	
	private static final SerializeType serializeType = PropertyConfigeHelper.getSerializeType();
	
	private static final List<ProviderService> serviceMetaDataList = Lists.newArrayList();

	private NettyChannelPoolFactory()
	{
	}
	
	/**
	 * 添加信道池
	 * @param service
	 */
	public static void addChannelPool(ProviderService service)
	{
		if(serviceMetaDataList.contains(service))
		{
			return;
		}
		
		serviceMetaDataList.add(service);
		String serviceIp = service.getServerIp();
		int servicePort = service.getServerPort();
		InetSocketAddress socketAddress = new InetSocketAddress(serviceIp, servicePort);
		
		if(channelPoolMap.containsKey(socketAddress))
		{
			return;
		}
		
		logger.info("注册信道:" + socketAddress.getAddress() + ":" + socketAddress.getPort());
		try
		{
			int realChannelConnectSize = 0;
			while (realChannelConnectSize < channelConnectSize)
			{
				Channel channel = null;
				while (channel == null)
				{
					channel = registerChannel(socketAddress);
				}
				realChannelConnectSize++;

				ArrayBlockingQueue<Channel> channelArrayBlockingQueue = channelPoolMap.get(socketAddress);
				if (channelArrayBlockingQueue == null)
				{
					channelArrayBlockingQueue = new ArrayBlockingQueue<Channel>(channelConnectSize);
					channelPoolMap.put(socketAddress, channelArrayBlockingQueue);
				}
				channelArrayBlockingQueue.offer(channel);
			}
		} 
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 移除信道池
	 * @param service
	 */
	public static void removeChannelPool(ProviderService service)
	{
		if(!serviceMetaDataList.contains(service))
		{
			return;
		}
		serviceMetaDataList.remove(service);
		
		Iterator<Entry<InetSocketAddress, ArrayBlockingQueue<Channel>>> iterator = channelPoolMap.entrySet().iterator();
		while(iterator.hasNext())
		{
			Map.Entry<InetSocketAddress, ArrayBlockingQueue<Channel>> entry = iterator.next();
			if(entry.getKey().getAddress().getHostAddress().equals(service.getServerIp()) && entry.getKey().getPort() == service.getServerPort())
			{
				entry.getValue().clear();
				iterator.remove();
				logger.info("移除信道" + entry.getKey().getAddress().getHostAddress() + ":" + entry.getKey().getPort());
			}
		}
	}

	/**
	 * 初始化Netty channel 连接队列Map
	 *
	 * @param providerMap
	 */
	public static void initChannelPoolFactory(Map<String, List<ProviderService>> providerMap)
	{
		if (MapUtils.isEmpty(providerMap))
		{
			return;
		}
		Collection<List<ProviderService>> collectionServiceMetaDataList = providerMap.values();
		for (List<ProviderService> serviceMetaDataModels : collectionServiceMetaDataList)
		{
			if (CollectionUtils.isEmpty(serviceMetaDataModels))
			{
				continue;
			}
			for(ProviderService service : serviceMetaDataModels)
			{
				addChannelPool(service);
			}
		}
	}

	/**
	 * 根据服务提供者地址获取对应的Netty Channel阻塞队列
	 *
	 * @param socketAddress
	 * @return
	 */
	public static ArrayBlockingQueue<Channel> acquire(InetSocketAddress socketAddress)
	{
		return channelPoolMap.get(socketAddress);
	}

	/**
	 * Channel使用完毕之后,回收到阻塞队列arrayBlockingQueue
	 *
	 * @param arrayBlockingQueue
	 * @param channel
	 * @param inetSocketAddress
	 */
	public static void release(ArrayBlockingQueue<Channel> arrayBlockingQueue, Channel channel,
			InetSocketAddress inetSocketAddress)
	{
		if (arrayBlockingQueue == null)
		{
			return;
		}

		if (channel == null || !channel.isActive() || !channel.isOpen() || !channel.isWritable())
		{
			if (channel != null)
			{
				channel.deregister().syncUninterruptibly().awaitUninterruptibly();
				channel.closeFuture().syncUninterruptibly().awaitUninterruptibly();
			}
			Channel newChannel = null;
			while (newChannel == null)
			{
				logger.debug("---------register new Channel-------------");
				newChannel = registerChannel(inetSocketAddress);
			}
			arrayBlockingQueue.offer(newChannel);
			return;
		}
		arrayBlockingQueue.offer(channel);
	}

	/**
	 * 为服务提供者地址socketAddress注册新的Channel
	 *
	 * @param socketAddress
	 * @return
	 */
	public static Channel registerChannel(InetSocketAddress socketAddress)
	{
		try
		{
			EventLoopGroup group = new NioEventLoopGroup(10);
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.remoteAddress(socketAddress);

			bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<SocketChannel>()
					{
						@Override
						public void initChannel(SocketChannel ch) throws Exception
						{
							ch.pipeline().addLast(new NettyEncoderHandler(serializeType));
							ch.pipeline().addLast(new NettyDecoderHandler(ResponseEntity.class, serializeType));
							ch.pipeline().addLast(new NettyClientInvokeHandler());
						}
					});

			ChannelFuture channelFuture = bootstrap.connect().sync();
			final Channel newChannel = channelFuture.channel();
			final CountDownLatch connectedLatch = new CountDownLatch(1);

			final List<Boolean> isSuccessHolder = Lists.newArrayListWithCapacity(1);
			channelFuture.addListener(new ChannelFutureListener()
			{
				@Override
				public void operationComplete(ChannelFuture future) throws Exception
				{
					if (future.isSuccess())
					{
						isSuccessHolder.add(Boolean.TRUE);
					} 
					else
					{
						future.cause().printStackTrace();
						isSuccessHolder.add(Boolean.FALSE);
					}
					connectedLatch.countDown();
				}
			});

			connectedLatch.await();
			if (isSuccessHolder.get(0))
			{
				return newChannel;
			}
		} 
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		return null;
	}
}
