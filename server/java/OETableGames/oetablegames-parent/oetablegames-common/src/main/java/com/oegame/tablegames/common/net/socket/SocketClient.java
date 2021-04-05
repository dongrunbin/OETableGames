package com.oegame.tablegames.common.net.socket;

import java.net.InetSocketAddress;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;


public class SocketClient
{
	
	IoSession session = null;
	
	public void connect(String host, int port, IoHandlerAdapter handler, IRout route)
	{
		if(session != null)
		{
			dispose();
		}
		 // 创建一个socket连接        
        NioSocketConnector connector= new NioSocketConnector();
        // 获取过滤器链          
        DefaultIoFilterChainBuilder chain = connector.getFilterChain();
          
        ProtocolCodecFilter filter= new ProtocolCodecFilter(new SocketCodecFactory(new SocketDecoder(route),new SocketEncoder()));
        // 添加编码过滤器 处理乱码、编码问题    
        chain.addLast("objectFilter",filter);
        // 消息核心处理器       
        connector.setHandler(handler);
        // 设置链接超时时间       
        connector.setConnectTimeoutCheckInterval(30);
        // 连接服务器，知道端口、地址      
        ConnectFuture cf = connector.connect(new InetSocketAddress(host,port));
        
        session = cf.getSession();
	}
	
	public void send(byte[] data)
	{
		session.write(data);
	}
	
	public void dispose()
	{
		session.closeNow();
		session = null;
	}
}
