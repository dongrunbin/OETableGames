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
/// C2S_Operate
/// </summary>
public class Mahjong_C2S_OperateProto
{
    public static final int CODE = 30006; 

    private byte typeId; //
    private ArrayList<Integer> index = new ArrayList<Integer>(); //
    public byte getTypeId(){
        return this.typeId;
    }

    public void setTypeId(byte value){
        this.typeId = value;
    }

    public ArrayList<Integer> getindexList(){
        return this.index;
    };

    public int getIndex(int index){
        return this.index.get(index);
    };

    public int indexCount(){
        return this.index.size();
    };

    public void addIndex(int value){
        this.index.add(value);
    };


    public byte[] toArray()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStreamExt dos = new DataOutputStreamExt(baos);
        byte[] ret = null;
        try{
            dos.writeInt(CODE);
            dos.writeByte(typeId);
            dos.writeShort(index.size());
            for (int i = 0; i < index.size(); ++i)
            {
                dos.writeInt(index.get(i));
            }
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
            short indexLength = dis.readShort();
            for (int i = 0; i < indexLength; ++i)
            {
                proto.index.add(dis.readInt());
            }
            dis.close();
            bais.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return proto;
    }
}
