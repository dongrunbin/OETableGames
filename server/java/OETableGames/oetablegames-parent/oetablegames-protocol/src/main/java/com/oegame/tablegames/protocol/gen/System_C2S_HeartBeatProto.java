//===================================================
//作    者：DRB
//创建时间：2021-04-23 08:05:42
//备    注：
//===================================================
package com.oegame.tablegames.protocol.gen;
import java.util.ArrayList;
import com.oegame.tablegames.common.io.*;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/// <summary>
/// C2S_HeartBeat
/// </summary>
public class System_C2S_HeartBeatProto
{
    public static final int CODE = 10001; 

    private long clientTimestamp; //
    public long getClientTimestamp(){
        return this.clientTimestamp;
    }

    public void setClientTimestamp(long value){
        this.clientTimestamp = value;
    }


    public byte[] toArray()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStreamExt dos = new DataOutputStreamExt(baos);
        byte[] ret = null;
        try{
            dos.writeInt(CODE);
            dos.writeLong(clientTimestamp);
            ret = baos.toByteArray();
            dos.close();
            baos.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return ret;
    }

    public static System_C2S_HeartBeatProto getProto(byte[] buffer)
    {
        if(buffer == null) return null;
        System_C2S_HeartBeatProto proto = new System_C2S_HeartBeatProto();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStreamExt dis = new DataInputStreamExt(bais);
        try{
            proto.clientTimestamp = dis.readLong();
            dis.close();
            bais.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return proto;
    }
}
