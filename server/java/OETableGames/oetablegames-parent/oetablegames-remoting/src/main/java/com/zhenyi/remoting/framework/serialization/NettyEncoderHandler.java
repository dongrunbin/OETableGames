package com.zhenyi.remoting.framework.serialization;

import com.zhenyi.remoting.framework.serialization.common.SerializeType;
import com.zhenyi.remoting.framework.serialization.engine.SerializerEngine;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Netty编码器
 * 
 * @author Binge
 *
 */
public class NettyEncoderHandler extends MessageToByteEncoder<Object>
{
	private SerializeType serializeType;

	public NettyEncoderHandler(SerializeType serializeType)
	{
		this.serializeType = serializeType;
	}

	@Override
	public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception
	{
		byte[] data = SerializerEngine.serialize(in, serializeType.getSerializeType());
		out.writeInt(data.length);
		out.writeBytes(data);
	}
}
