package com.zhenyi.remoting.framework.serialization.serializer;

/**
 * 序列化接口
 * 
 * @author Binge
 *
 */
public interface ISerializer
{

	/**
	 * 序列化
	 *
	 * @param obj
	 * @param <T>
	 * @return
	 */
	public <T> byte[] serialize(T obj);

	/**
	 * 反序列化
	 *
	 * @param data
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public <T> T deserialize(byte[] data, Class<T> clazz);
}
