package com.zhenyi.remoting.framework.provider;

import com.zhenyi.remoting.framework.helper.PropertyConfigeHelper;
import com.zhenyi.remoting.framework.model.RequestEntity;
import com.zhenyi.remoting.framework.serialization.NettyDecoderHandler;
import com.zhenyi.remoting.framework.serialization.NettyEncoderHandler;
import com.zhenyi.remoting.framework.serialization.common.SerializeType;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Netty服务端
 * 
 * @author Binge
 *
 */
public class NettyServer
{

	private Channel channel;
	// boss线程组
	private EventLoopGroup bossGroup;
	// worker线程组
	private EventLoopGroup workerGroup;
	// 序列化类型
	private SerializeType serializeType = PropertyConfigeHelper.getSerializeType();

	/**
	 * 启动Netty服务
	 *
	 * @param port
	 */
	public void start(final int port)
	{
		synchronized (NettyServer.class)
		{
			if (bossGroup != null || workerGroup != null)
			{
				return;
			}

			bossGroup = new NioEventLoopGroup();
			workerGroup = new NioEventLoopGroup();
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 1024).childOption(ChannelOption.SO_KEEPALIVE, true)
					.childOption(ChannelOption.TCP_NODELAY, true).handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new ChannelInitializer<SocketChannel>()
					{
						@Override
						protected void initChannel(SocketChannel ch) throws Exception
						{
							ch.pipeline().addLast(new NettyDecoderHandler(RequestEntity.class, serializeType));
							ch.pipeline().addLast(new NettyEncoderHandler(serializeType));
							ch.pipeline().addLast(new NettyServerInvokeHandler());
						}
					});
			try
			{
				channel = serverBootstrap.bind(port).sync().channel();
			} catch (InterruptedException e)
			{
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * 停止Netty服务
	 */
	public void stop()
	{
		if (null == channel)
		{
			throw new RuntimeException("Netty Server Stoped");
		}
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
		channel.closeFuture().syncUninterruptibly();
	}

	public NettyServer()
	{
	}
}
