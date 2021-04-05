package com.zhenyi.remoting.framework.serialization.serializer.impl;

import com.zhenyi.remoting.framework.serialization.serializer.ISerializer;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Hessian
 * 
 * @author Binge
 *
 */
public class HessianSerializer implements ISerializer
{

	public byte[] serialize(Object obj)
	{
		if (obj == null)
			throw new NullPointerException();

		try
		{
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			HessianOutput ho = new HessianOutput(os);
			ho.writeObject(obj);
			return os.toByteArray();
		} 
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public <T> T deserialize(byte[] data, Class<T> clazz)
	{
		if (data == null)
			throw new NullPointerException();

		try
		{
			ByteArrayInputStream is = new ByteArrayInputStream(data);
			HessianInput hi = new HessianInput(is);
			return (T) hi.readObject();
		} 
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

	}

}
