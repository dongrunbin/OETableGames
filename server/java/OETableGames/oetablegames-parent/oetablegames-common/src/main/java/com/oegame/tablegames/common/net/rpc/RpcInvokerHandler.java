package com.oegame.tablegames.common.net.rpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;

public class RpcInvokerHandler implements InvocationHandler
{
	
	private String host;
	
	private int port;
	
//	private Object result;
//	
//	private Object lock = new Object();
	
	public RpcInvokerHandler(String host, int port)
	{
		this.host = host;
		this.port = port;
	}

	public Object invoke(Object proxy, Method method, Object[] arguments)
	{
//		SocketClient socket = new SocketClient();
//		socket.connect(this.host, this.port, new RpcClientHandler(new RpcClientHandler.RpcEvent() 
//		{
//			public void onReceived(byte[] data) throws IOException
//			{
//				ByteArrayInputStream bais = new ByteArrayInputStream(data);
//				ObjectInputStream input = new ObjectInputStream(bais);
//				this.result = input.readObject();
//				synchronized(lock)
//				{
//					lock.notifyAll();
//				}
//			}
//		}));
//		
//		synchronized (lock)
//		{
//			lock.wait();
//		}
//		return result;
		Socket socket = null;
		
		try
		{
			
			socket = new Socket(this.host, this.port);
		} 
		catch (UnknownHostException e1)
		{
			return null;
		} 
		catch (IOException e1)
		{
			return null;
		}
		try
		{
			
			OutputStream out = socket.getOutputStream();
			ObjectOutputStream output = new ObjectOutputStream(out);
			try
			{
				output.writeUTF(method.getName());
				output.writeObject(method.getParameterTypes());
				output.writeObject(arguments);
				ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
				try
				{
					Object result = input.readObject();
					if (result instanceof Throwable)
					{
						((Throwable) result).printStackTrace();
//						throw (Throwable) result;
						return null;
					}
					return result;
				} 
				catch (ClassNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				finally
				{
					input.close();
				}
			} 
			finally
			{
				output.close();
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(socket != null)
				{
					socket.close();
				}
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
}
