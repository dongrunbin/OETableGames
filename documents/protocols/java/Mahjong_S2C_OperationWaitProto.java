//===================================================
//作    者：DRB
//创建时间：2021-04-15 03:55:31
//备    注：
//===================================================
package com.oegame.tablegames.protocol.gen;
import java.util.ArrayList;
import com.oegame.tablegames.common.io.*;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/// <summary>
/// S2C_OperationWait
/// </summary>
public class Mahjong_S2C_OperationWaitProto
{
    public static final int CODE = 30014; 


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

    public static Mahjong_S2C_OperationWaitProto getProto(byte[] buffer)
    {
        if(buffer == null) return null;
        Mahjong_S2C_OperationWaitProto proto = new Mahjong_S2C_OperationWaitProto();
        return proto;
    }
}
