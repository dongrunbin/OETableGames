package com.zhenyi.remoting.framework.serialization;

import com.zhenyi.remoting.framework.serialization.common.SerializeType;
import com.zhenyi.remoting.framework.serialization.engine.SerializerEngine;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Netty解码器
 * 
 * @author Binge
 *
 */
public class NettyDecoderHandler extends ByteToMessageDecoder
{
	private Class<?> genericClass;
	private SerializeType serializeType;

	public NettyDecoderHandler(Class<?> genericClass, SerializeType serializeType)
	{
		this.genericClass = genericClass;
		this.serializeType = serializeType;
	}

	@Override
	public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
	{
		if (in.readableBytes() < 4)
		{
			return;
		}
		in.markReaderIndex();
		int dataLength = in.readInt();
		if (dataLength < 0)
		{
			ctx.close();
		}
		if (in.readableBytes() < dataLength)
		{
			in.resetReaderIndex();
			return;
		}
		byte[] data = new byte[dataLength];
		in.readBytes(data);

		Object obj = SerializerEngine.deserialize(data, genericClass, serializeType.getSerializeType());
		out.add(obj);
	}

}
