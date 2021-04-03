//===================================================
//作    者：DRB
//创建时间：2021-03-06 23:48:45
//备    注：
//===================================================
package com.zhenyi.wangque.protocol.gen;
import java.util.ArrayList;
import com.zhenyi.wangque.common.io.*;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/// <summary>
/// Disconnect
/// </summary>
public class System_DisconnectProto
{
    public static final int CODE = 10002; 


    public byte[] toArray()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStreamExt dos = new DataOutputStreamExt(baos);
        byte[] ret = null;
        try{
            dos.writeInt(CODE);
            ret = baos.toByteArray();
            dos.close();
            baos.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return ret;
    }

    public static System_DisconnectProto getProto(byte[] buffer)
    {
        if(buffer == null) return null;
        System_DisconnectProto proto = new System_DisconnectProto();
        return proto;
    }
}
