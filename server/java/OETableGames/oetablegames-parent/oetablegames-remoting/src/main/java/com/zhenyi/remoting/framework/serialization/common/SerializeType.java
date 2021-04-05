package com.zhenyi.remoting.framework.serialization.common;

import org.apache.commons.lang.StringUtils;

/**
 * 序列化类型
 * 
 * @author Binge
 *
 */
public enum SerializeType
{

	DefaultJavaSerializer("DefaultJavaSerializer"), 
	HessianSerializer("HessianSerializer"), 
	JSONSerializer("JSONSerializer"), 
	ProtoStuffSerializer("ProtoStuffSerializer"), 
	XmlSerializer("XmlSerializer"), 
	MarshallingSerializer("MarshallingSerializer"),
	AvroSerializer("AvroSerializer"), 
	ProtocolBufferSerializer("ProtocolBufferSerializer"), 
	ThriftSerializer("ThriftSerializer");

	private String serializeType;

	private SerializeType(String serializeType)
	{
		this.serializeType = serializeType;
	}

	public static SerializeType queryByType(String serializeType)
	{
		if (StringUtils.isBlank(serializeType))
		{
			return null;
		}

		for (SerializeType serialize : SerializeType.values())
		{
			if (StringUtils.equals(serializeType, serialize.getSerializeType()))
			{
				return serialize;
			}
		}
		return null;
	}

	public String getSerializeType()
	{
		return serializeType;
	}
}
