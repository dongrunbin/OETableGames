package com.oegame.tablegames.common.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DataInputStreamExt extends DataInputStream {

	public DataInputStreamExt(InputStream in) {
		super(in);
	}
	
	
	public byte[] readBytes() throws IOException
	{
		int length = super.readInt();
		if(length == 0) return null;
		byte[] ret = new byte[length];
		super.read(ret, 0, length);
		return ret;
	}

}
