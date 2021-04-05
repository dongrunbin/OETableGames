package com.zhenyi.remoting.framework.serialization.serializer.impl;

import com.zhenyi.remoting.framework.serialization.serializer.ISerializer;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Xml
 * @author Binge
 *
 */
public class XmlSerializer implements ISerializer
{

	private static final XStream xStream = new XStream(new DomDriver());

	public <T> byte[] serialize(T obj)
	{
		return xStream.toXML(obj).getBytes();
	}

	public <T> T deserialize(byte[] data, Class<T> clazz)
	{
		String xml = new String(data);
		return (T) xStream.fromXML(xml);
	}

}
