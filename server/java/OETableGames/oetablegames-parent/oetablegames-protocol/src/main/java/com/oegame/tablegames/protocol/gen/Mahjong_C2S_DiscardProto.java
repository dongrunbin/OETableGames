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
/// C2S_Discard
/// </summary>
public class Mahjong_C2S_DiscardProto
{
    public static final int CODE = 30003; 

    private int index; //
    private boolean isReadyHand; //
    public int getIndex(){
        return this.index;
    }

    public void setIndex(int value){
        this.index = value;
    }

    public boolean getIsReadyHand(){
        return this.isReadyHand;
    }

    public void setIsReadyHand(boolean value){
        this.isReadyHand = value;
    }


    public byte[] toArray()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStreamExt dos = new DataOutputStreamExt(baos);
        byte[] ret = null;
        try{
            dos.writeInt(CODE);
            dos.writeInt(index);
            dos.writeBoolean(isReadyHand);
            ret = baos.toByteArray();
            dos.close();
            baos.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return ret;
    }

    public static Mahjong_C2S_DiscardProto getProto(byte[] buffer)
    {
        if(buffer == null) return null;
        Mahjong_C2S_DiscardProto proto = new Mahjong_C2S_DiscardProto();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStreamExt dis = new DataInputStreamExt(bais);
        try{
            proto.index = dis.readInt();
            proto.isReadyHand = dis.readBoolean();
            dis.close();
            bais.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return proto;
    }
}
