package com.oegame.tablegames.common.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class IOUtil
{
//	private static final Logger logger = LoggerFactory.getLogger(IOUtil.class);

	public static byte[] getData(String filePath)
	{
		ByteArrayOutputStream bos = null;
		BufferedInputStream in = null;
		try
		{
			File f = new File(filePath);
			if (f.exists())
			{
				in = new BufferedInputStream(new FileInputStream(f));
				bos = new ByteArrayOutputStream((int) f.length());

				int buf_size = 1024;
				byte[] buffer = new byte[buf_size];
				int len = 0;
				while (-1 != (len = in.read(buffer, 0, buf_size)))
				{
					bos.write(buffer, 0, len);
				}
			}

		} 
		catch (IOException e)
		{
			e.printStackTrace();
		} 
		finally
		{
			try
			{
				in.close();
				bos.close();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return bos.toByteArray();
	}
}
