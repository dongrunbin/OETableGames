package com.oegame.tablegames.common.net.rpc;

import java.lang.reflect.Proxy;

import com.oegame.tablegames.common.net.socket.SocketServer;

public class RpcFrameworkNew
{
	public static void export(final Object service, int port)
	{
		if (service == null)
			throw new IllegalArgumentException("service不能是null");
		if (port <= 0 || port > 65535)
			throw new IllegalArgumentException("无效端口：" + port + ",端口范围(0-65535]");
		System.out.println(String.format("在%s端口上提供%s服务",port,service.getClass().getName()));
		SocketServer server = new SocketServer();
		server.start(new RpcServiceHandler(service), null, port);
	}


	@SuppressWarnings("unchecked")
	public static <T> T refer(final Class<T> interfaceClass, final String host, final int port)
	{
		if (interfaceClass == null)
			throw new IllegalArgumentException("接口是空的");
		if (!interfaceClass.isInterface())
			throw new IllegalArgumentException(interfaceClass.getName() + "必须是一个接口");
		if (host == null || host.length() == 0)
			throw new IllegalArgumentException("host不能是null");
		if (port <= 0 || port > 65535)
			throw new IllegalArgumentException("无效端口：" + port + ",端口范围(0-65535]");
		System.out.println(String.format("从%s:%d获取远程服务%s",host,port,interfaceClass.getName()));
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]
		{ interfaceClass }, new RpcInvokerHandler(host,port));
	}
}
