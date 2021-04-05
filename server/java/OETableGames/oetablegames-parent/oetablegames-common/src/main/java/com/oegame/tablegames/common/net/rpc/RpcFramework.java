package com.oegame.tablegames.common.net.rpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * RpcFramework
 */
public class RpcFramework
{


	public static void export(final Object service, final int port)
	{
		if (service == null)
			throw new IllegalArgumentException("service不能是null");
		if (port <= 0 || port > 65535)
			throw new IllegalArgumentException("无效端口：" + port + ",端口范围(0-65535]");
		System.out.println(String.format("在%s端口上提供%s服务",port,service.getClass().getName()));

		new Thread(new Runnable()
		{
			@SuppressWarnings("resource")
			public void run()
			{
				ServerSocket server = null;
				try
				{
					server = new ServerSocket(port);
				}
				catch(IOException e)
				{
					
				}
				while(true)
				{
					try
					{
						final Socket socket = server.accept();
						new Thread(new Runnable()
						{
							public void run()
							{
								try
								{
									try
									{
										ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
										try
										{
											String methodName = input.readUTF();
											Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
											Object[] arguments = (Object[]) input.readObject();
											ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
											try
											{
												Method method = service.getClass().getMethod(methodName, parameterTypes);
												Object result = method.invoke(service, arguments);
												output.writeObject(result);
											} 
											catch (Throwable t)
											{
												output.writeObject(t);
											} 
											finally
											{
												output.close();
											}
										} 
										finally
										{
											input.close();
										}
									} 
									finally
									{
										socket.close();
									}
								} 
								catch (Exception e)
								{
//									e.printStackTrace();
								}
							}
						}).start();
					} 
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}).start();
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
//		System.out.println(String.format("从%s:%d获取远程服务%s",host,port,interfaceClass.getName()));
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]
		{ interfaceClass }, new RpcInvokerHandler(host,port));
	}

}
