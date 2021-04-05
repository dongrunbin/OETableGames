package com.oegame.tablegames.common.net.rpc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class RpcServiceHandler extends IoHandlerAdapter
{
	
	private Object service;
	public RpcServiceHandler(Object service)
	{
		this.service = service; 
	}
	
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception 
	{
		byte[] data = (byte[]) message;
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ObjectInputStream input = new ObjectInputStream(bais);
		try
		{
			String methodName = input.readUTF();
			Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
			Object[] arguments = (Object[]) input.readObject();
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream output = new ObjectOutputStream(baos);
			try
			{
				Method method = this.service.getClass().getMethod(methodName, parameterTypes);
				Object result = method.invoke(service, arguments);
				output.writeObject(result);
				int length = baos.size();
				output.reset();
				output.writeInt(length);
				output.writeObject(result);
				
				session.write(baos.toByteArray());
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
}
