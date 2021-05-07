//===================================================
//作    者：DRB
//创建时间：2021-04-28 02:06:07
//备    注：
//===================================================
package com.oegame.tablegames.protocol.gen;
import java.util.ArrayList;
import com.oegame.tablegames.common.io.*;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/// <summary>
/// S2C_HeartBeat
/// </summary>
public class System_S2C_HeartBeatProto
{
    public static final int CODE = 10004; 

    private long serverTimestamp; //Server timestamp
    private long clientTimestamp; //Client timestamp
    public long getServerTimestamp(){
        return this.serverTimestamp;
    }

    public void setServerTimestamp(long value){
        this.serverTimestamp = value;
    }

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
            dos.writeLong(serverTimestamp);
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

    public static System_S2C_HeartBeatProto getProto(byte[] buffer)
    {
        if(buffer == null) return null;
        System_S2C_HeartBeatProto proto = new System_S2C_HeartBeatProto();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStreamExt dis = new DataInputStreamExt(bais);
        try{
            proto.serverTimestamp = dis.readLong();
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
