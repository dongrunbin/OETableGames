package com.oegame.tablegames.common.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FcgiUtil
{
	private static Logger logger = LoggerFactory.getLogger(FcgiUtil.class);

	public static int VERSION = 1;

	public static int REQUEST_ID = 1;

	public static int BEGIN_REQUEST = 1;

	public static int ABORT_REQUEST = 2;

	public static int END_REQUEST = 3;

	public static int PARAMS = 4;

	public static int STDIN = 5;

	public static int STDOUT = 6;

	public static int STDERR = 7;

	public static int DATA = 8;

	public static int GET_VALUES = 9;

	public static int GET_VALUES_RESULT = 10;

	public static int UNKNOWN_TYPE = 11;

	public static int CLOSE = 12;

	private HashMap<String, String> params = new HashMap<String, String>();

	private SocketChannel channel = null;

	private static Charset charset = Charset.forName("UTF-8");

	private static CharsetDecoder decoder = charset.newDecoder();
	
	private static volatile FcgiUtil instance = null;
	
	public static FcgiUtil getInstance()
	{
		if(instance == null)
		{
			synchronized(FcgiUtil.class)
			{
				if(instance == null)
				{
					instance = new FcgiUtil();
				}
			}
		}
		return instance;
	}

	private void initParams(String[] _extend, String host, int port, String fileName)
	{
		try
		{
			this.channel = SocketChannel.open();

			// Util.println("server index: " + this.SERVER_INDEX);

			// Util.println(Config.FCGI_HOST[this.SERVER_INDEX] + " - "+ Config.FCGI_PORT +
			// " - " + Config.FCGI_EXEC);

			this.channel.connect(new InetSocketAddress(host, port));

		} catch (IOException e)
		{

			e.printStackTrace();

		}

		this.params.clear();

		this.params.put("GATEWAY_INTERFACE", "CGI/1.1");
		this.params.put("SERVER_PROTOCOL", "HTTP/1.1");
		this.params.put("SCRIPT_FILENAME", fileName);
		this.params.put("SCRIPT_NAME", fileName);
		if (_extend != null)
		{
			this.params.put("UUID", _extend[0]);
			this.params.put("REMOTE_ADDR", _extend[3]);
			this.params.put("REMOTE_PORT", _extend[4]);
		}
	}

	@SuppressWarnings("deprecation")
	private String query_string(HashMap<String, String> _data)
	{

		String data = "version=" + VERSION;

		for (String _key : _data.keySet())
		{
			String key = _key;
			String val = _data.get(_key);
			data += "&" + key + "=" + URLEncoder.encode(val);
		}

		return data;
	}

	public synchronized String get(String host, int port, String fileName, HashMap<String, String> _data,
			String[] _extend)
	{
		this.initParams(_extend, host, port, fileName);

		// Util.println("REMOTE_ADDR: " + this.params.get("REMOTE_ADDR"));

		this.params.put("QUERY_STRING", this.query_string(_data));
		this.params.put("REQUEST_METHOD", "GET");

		String content = "";

		try
		{

			this.channel.write(this.begin());

			this.channel.write(this.setParams());

			this.channel.write(this.stdin());

			content = this.extract();

		} catch (IOException e)
		{

			e.printStackTrace();

		}

		return content;
	}

	public synchronized String post(String host, int port, String fileName, HashMap<String, String> _data,
			String[] _extend)
	{
		this.initParams(_extend, host, port, fileName);

		// Util.println("REMOTE_ADDR: " + this.params.get("REMOTE_ADDR"));

		String postData = this.query_string(_data);

		this.params.put("REQUEST_METHOD", "POST");
		this.params.put("CONTENT_TYPE", "application/x-www-form-urlencoded");
		this.params.put("CONTENT_LENGTH", postData.length() + "");

		String content = "";

		try
		{

			this.channel.write(this.begin());

			this.channel.write(this.setParams());

			this.channel.write(this.stdin(postData));

			content = this.extract();

		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return content;
	}

	private String extract() throws IOException
	{

		String content = "";

		ArrayList<ByteBuffer> array = new ArrayList<ByteBuffer>();

		Boolean read = true;

		int size = 0;

		while (read)
		{

			ByteBuffer head = ByteBuffer.allocate(8);
			size = 0;
			do
			{
				size += this.channel.read(head);
			} 
			while (size < 8 && size > 0);
			head.flip();

			int version = head.get() & 0xFF;
			int type = head.get() & 0xFF;
			int requestId = head.getShort() & 0x0FFFF;
			int contentLength = head.getShort() & 0x0FFFF;
			int padding = head.get() & 0xFF;
			int reserved = head.get() & 0xFF;

			if (contentLength < 0)
			{
				logger.debug("-");
				logger.debug(" version: " + version);
				logger.debug(" type: " + type);
				logger.debug(" requestId: " + requestId);
				logger.debug(" contentLength: " + contentLength);
				logger.debug(" padding: " + padding);
				logger.debug(" reserved: " + reserved);
				logger.debug("");

				continue;
			}

			if (type == STDOUT)
			{

				ByteBuffer data = ByteBuffer.allocate(contentLength);

				size = 0;
				do
				{
					size += this.channel.read(data);
				}
				while (size < contentLength && size > 0);
				data.flip();

				if (padding > 0)
				{
					ByteBuffer skip = ByteBuffer.allocate(padding);
					size = 0;
					do
					{
						size += this.channel.read(skip);
					} 
					while (size < padding && size > 0);
					skip.flip();
				}

				array.add(data);

			} 
			else if (type == END_REQUEST)
			{

				ByteBuffer endStatus = ByteBuffer.allocate(contentLength);

				size = 0;
				do
				{
					size += this.channel.read(endStatus);
				} while (size < contentLength && size > 0);
				endStatus.flip();

				int appStatus = endStatus.getInt() & 0x0FFFFFFFF;
				int protocolStatus = endStatus.get() & 0xFF;

				if (appStatus < 0)
				{
					logger.debug("appStatus: " + appStatus);
					logger.debug("protocolStatus: " + protocolStatus);
				}

				read = false;

			} 
			else
			{

				logger.debug("FCGI type: " + type);

			}
		}

		this.channel.close();

		int limit = 0;
		size = array.size();
		for (int i = 0; i < size; i++)
		{
			limit += array.get(i).limit();
		}

		ByteBuffer data = ByteBuffer.allocate(limit);
		size = array.size();
		for (int i = 0; i < size; i++)
		{
			data.put(array.get(i));
		}
		data.flip();

		CharBuffer buffer = decoder.decode(data);

		content = buffer.toString();

		int head = content.indexOf("\r\n\r\n");
		if (head != -1)
			return content.substring(head + 4);

		return content;
	}

	/**
	 * 请求head
	 * 
	 */
	private ByteBuffer head(int _type, int _length)
	{

		ByteBuffer head = ByteBuffer.allocate(8);

		head.put((byte) VERSION);
		head.put((byte) _type);
		head.putShort((short) (REQUEST_ID & 0xffff)); // requestID
		head.putShort((short) (_length & 0xffff)); // ContentLength
		head.put((byte) 0); // Padding
		head.rewind();

		return head;
	}

	/**
	 * 开始请求
	 * 
	 */
	private ByteBuffer[] begin()
	{

		ByteBuffer[] buffers = new ByteBuffer[2];

		ByteBuffer head = this.head(BEGIN_REQUEST, 8);
		buffers[0] = head;

		ByteBuffer body = ByteBuffer.allocate(8);
		body.putShort((short) (1 & 0xffff)); // RoleID
		body.put((byte) 0); // flags
		body.rewind();
		buffers[1] = body;

		return buffers;
	}

	/**
	 * 设置参数
	 * 
	 * @throws UnsupportedEncodingException
	 * 
	 */
	private ByteBuffer[] setParams() throws UnsupportedEncodingException
	{

		ByteBuffer buffers[] = new ByteBuffer[this.params.size() + 2];

		int length = 0;
		int index = 1;

		for (String _key : this.params.keySet())
		{
			String key = _key;
			String val = this.params.get(_key);

			int klen = 1;
			int vlen = 1;

			if (key.length() >= 0x80)
			{
				klen = 4;
			}

			if (val.length() >= 0x80)
			{
				vlen = 4;
			}

			int kvlength = klen + vlen + key.length() + val.length();

			ByteBuffer byteBuffer = ByteBuffer.allocate(kvlength);

			if (key.length() < 0x80)
			{
				byteBuffer.put((byte) key.length()); // key length
			} else
			{
				byteBuffer.putInt(key.length()); // key length
			}

			if (val.length() < 0x80)
			{
				byteBuffer.put((byte) val.length()); // key length
			} else
			{
				byteBuffer.putInt(val.length()); // key length
			}

			byteBuffer.put(key.getBytes()); // key
			byteBuffer.put(val.getBytes()); // value
			byteBuffer.rewind();

			buffers[index] = byteBuffer;

			length += kvlength;
			++index;
		}

		ByteBuffer head = this.head(PARAMS, length);
		buffers[0] = head;

		ByteBuffer empty = this.head(PARAMS, 0);
		buffers[params.size() + 1] = empty;

		return buffers;
	}

	/**
	 * 无内容输入
	 * 
	 */
	private ByteBuffer[] stdin()
	{
		ByteBuffer buffers[] = new ByteBuffer[1];

		ByteBuffer head = this.head(STDIN, 0);
		buffers[0] = head;

		return buffers;
	}

	/**
	 * POST输入的内容
	 * 
	 */
	private ByteBuffer[] stdin(String _data)
	{
		byte[] postData = _data.getBytes();

		ByteBuffer buffers[] = new ByteBuffer[3];

		ByteBuffer head = this.head(STDIN, postData.length);
		buffers[0] = head;

		ByteBuffer data = ByteBuffer.wrap(postData);
		buffers[1] = data;

		ByteBuffer empty = this.head(STDIN, 0);
		buffers[2] = empty;

		return buffers;
	}

}
