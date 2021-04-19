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
/// C2S_ApplyDisband
/// </summary>
public class Game_C2S_ApplyDisbandProto
{
    public static final int CODE = 20008; 

    private byte disbandStatus; //
    public byte getDisbandStatus(){
        return this.disbandStatus;
    }

    public void setDisbandStatus(byte value){
        this.disbandStatus = value;
    }


    public byte[] toArray()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStreamExt dos = new DataOutputStreamExt(baos);
        byte[] ret = null;
        try{
            dos.writeInt(CODE);
            dos.writeByte(disbandStatus);
            ret = baos.toByteArray();
            dos.close();
            baos.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return ret;
    }

    public static Game_C2S_ApplyDisbandProto getProto(byte[] buffer)
    {
        if(buffer == null) return null;
        Game_C2S_ApplyDisbandProto proto = new Game_C2S_ApplyDisbandProto();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStreamExt dis = new DataInputStreamExt(bais);
        try{
            proto.disbandStatus = dis.readByte();
            dis.close();
            bais.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return proto;
    }
}
