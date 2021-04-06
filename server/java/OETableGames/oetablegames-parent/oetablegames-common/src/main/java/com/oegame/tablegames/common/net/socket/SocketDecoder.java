package com.oegame.tablegames.common.net.socket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.oegame.tablegames.common.util.EncryptUtil;

public class SocketDecoder extends CumulativeProtocolDecoder
{
	private static final Logger logger = LoggerFactory.getLogger(SocketDecoder.class);
	
	private final static String SERVER_KEY = "w92rxtavrkr6c6ab";
	
	private final static int SERVER_ID = 7;

	private static final int HEAD_LENGTH = 4;
	
	private static final int MAX_LENGTH = 10240000;
	
	private IRoute rout;
	public SocketDecoder(IRoute rout)
	{
		this.rout = rout;
	}

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception
	{
		if (in.remaining() < HEAD_LENGTH)
		{
			return false;
		}

		in.mark();
		int length = in.getInt(in.position());

		// 判断长度是否超过限制
		if (length > MAX_LENGTH)
		{
			logger.info("超长: " + length + " > " + in.remaining() + " - " + MAX_LENGTH + " : "
					+ session.getRemoteAddress());
			session.closeOnFlush();
			return false;
		}

		// 当前流的数据长度不够一个包的长度 return false 继续接收
		if (in.remaining() - HEAD_LENGTH < length)
		{
			in.reset();
			return false;
		} 
		else
		{
			int len = in.getInt();
//			 System.out.println("消息长度：" + len + "================(不含头)");
			byte[] bytes = new byte[len];

			in.get(bytes, 0, len);

			IoBuffer buf = IoBuffer.wrap(bytes);

			this.packages(session, buf, out);

			// 当前流的数据长度依然大于0时 return true 立即处理
			if (in.remaining() > 0)
			{
				return true;
			}
		}

		return false;
	}

	private void packages(IoSession session, IoBuffer buf, ProtocolDecoderOutput out)
	{
		try
		{
			// 判断是否解压
			byte isComp = buf.get();
			int crc = buf.getUnsignedShort();
			int size = buf.limit() - 3;
			byte[] data = new byte[size];
			buf.get(data, 0, size);
			int newCrc = EncryptUtil.CalculateCrc16(data);
			
			if(crc != newCrc)
			{
				logger.info(String.format("crc校验码不正确:%d != %d",crc,newCrc));
				return;
			}
			if (isComp == 1)
			{
				ByteArrayOutputStream read = new ByteArrayOutputStream();
				GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(data));

				byte[] buffer = new byte[256];
				int n;
				while ((n = gzip.read(buffer)) >= 0)
				{
					read.write(buffer, 0, n);
				}
				data = read.toByteArray();
				gzip.close();
				read.close();
			}
			
			data = EncryptUtil.Encrypt(data, SERVER_KEY, SERVER_ID);
			
			IoBuffer buffer = IoBuffer.wrap(data);

			int code = buffer.getInt();
			
			data = new byte[data.length - 4];
			buffer.get(data,0,data.length);
			this.rout.call(session, code, data);
		} 
		catch (UnsupportedEncodingException e)
		{
			session.closeOnFlush();
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			session.closeOnFlush();
			e.printStackTrace();
		}

	}
}
