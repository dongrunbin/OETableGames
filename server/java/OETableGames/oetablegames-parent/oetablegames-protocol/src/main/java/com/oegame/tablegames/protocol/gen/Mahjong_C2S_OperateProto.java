//===================================================
//作    者：DRB
//创建时间：2021-04-05 20:52:14
//备    注：
//===================================================
package com.oegame.tablegames.protocol.gen;
import java.util.ArrayList;
import com.oegame.tablegames.common.io.*;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/// <summary>
/// C2S_Operate
/// </summary>
public class Mahjong_C2S_OperateProto
{
    public static final int CODE = 30006; 

    private byte typeId; //
    private int index; //
    public byte getTypeId(){
        return this.typeId;
    }

    public void setTypeId(byte value){
        this.typeId = value;
    }

    public int getIndex(){
        return this.index;
    }

    public void setIndex(int value){
        this.index = value;
    }


    public byte[] toArray()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStreamExt dos = new DataOutputStreamExt(baos);
        byte[] ret = null;
        try{
            dos.writeInt(CODE);
            dos.writeByte(typeId);
            dos.writeInt(index);
            ret = baos.toByteArray();
            dos.close();
            baos.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return ret;
    }

    public static Mahjong_C2S_OperateProto getProto(byte[] buffer)
    {
        if(buffer == null) return null;
        Mahjong_C2S_OperateProto proto = new Mahjong_C2S_OperateProto();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStreamExt dis = new DataInputStreamExt(bais);
        try{
            proto.typeId = dis.readByte();
            proto.index = dis.readInt();
            dis.close();
            bais.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return proto;
    }
}
