package com.oegame.tablegames.common.io;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DataOutputStreamExt extends DataOutputStream {

	public DataOutputStreamExt(OutputStream out) {
		super(out);
	}
	
	public void writeBytes(byte[] buffer) throws IOException
	{
		super.writeInt(buffer.length);
		super.write(buffer, 0, buffer.length);
	}

}
