package com.oegame.tablegames.common.net.socket;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.zip.GZIPOutputStream;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.oegame.tablegames.common.util.EncryptUtil;

public class SocketEncoder implements ProtocolEncoder 
{
	
	private final static String SERVER_KEY = "w92rxtavrkr6c6ab";
	
	private final static int SERVER_ID = 7;
	
	/**
	 * 使用GZIP压缩的字节限制
	 */
	public static int SEND_BUFFER_GZIP_SIZE = 102400000;

	public void encode(IoSession session, Object in, ProtocolEncoderOutput out) throws Exception 
	{
		byte[] data = (byte[])in;
		ByteBuffer proto = null;
		if (data != null) 
		{
			// 判断是否需要压缩
			if (data.length > SEND_BUFFER_GZIP_SIZE) 
			{
				ByteArrayOutputStream gzipData = new ByteArrayOutputStream();
				GZIPOutputStream gzip = new GZIPOutputStream(gzipData);
				gzip.write(data);
				gzip.close();
				byte[] compressData = gzipData.toByteArray();
				proto = ByteBuffer.allocate(compressData.length + 1);
				proto.put((byte) 1);

				proto.put(compressData);
			} 
			else 
			{
				proto = ByteBuffer.allocate(data.length + 1);
				proto.put((byte) 0);

				proto.put(data);
			}
		} 
		else 
		{
			proto = ByteBuffer.allocate(1);
			proto.put((byte) 0);
		}
		byte[] crypt = EncryptUtil.Encrypt(proto.array(),SERVER_KEY, SERVER_ID);
		IoBuffer pack = IoBuffer.allocate(crypt.length + 4);

		pack.putInt(crypt.length);
		pack.put(crypt);

//		System.out.println("服务器发送了一个长度为" + pack.limit() + "的消息");
		
		pack.flip();
		out.write(pack);
	}

	public void dispose(IoSession session) throws Exception 
	{
		System.out.println("SocketEncode session: " + session);
	}

}
