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
/// S2C_Draw
/// </summary>
public class Mahjong_S2C_DrawProto
{
    public static final int CODE = 30002; 

    private int playerId; //
    private long countdown; //
    private int index; //
    private int color; //
    private int number; //
    private boolean isFromLast; //
    public int getPlayerId(){
        return this.playerId;
    }

    public void setPlayerId(int value){
        this.playerId = value;
    }

    public long getCountdown(){
        return this.countdown;
    }

    public void setCountdown(long value){
        this.countdown = value;
    }

    public int getIndex(){
        return this.index;
    }

    public void setIndex(int value){
        this.index = value;
    }

    public int getColor(){
        return this.color;
    }

    public void setColor(int value){
        this.color = value;
    }

    public int getNumber(){
        return this.number;
    }

    public void setNumber(int value){
        this.number = value;
    }

    public boolean getIsFromLast(){
        return this.isFromLast;
    }

    public void setIsFromLast(boolean value){
        this.isFromLast = value;
    }


    public byte[] toArray()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStreamExt dos = new DataOutputStreamExt(baos);
        byte[] ret = null;
        try{
            dos.writeInt(CODE);
            dos.writeInt(playerId);
            dos.writeLong(countdown);
            dos.writeInt(index);
            dos.writeInt(color);
            dos.writeInt(number);
            dos.writeBoolean(isFromLast);
            ret = baos.toByteArray();
            dos.close();
            baos.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return ret;
    }

    public static Mahjong_S2C_DrawProto getProto(byte[] buffer)
    {
        if(buffer == null) return null;
        Mahjong_S2C_DrawProto proto = new Mahjong_S2C_DrawProto();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStreamExt dis = new DataInputStreamExt(bais);
        try{
            proto.playerId = dis.readInt();
            proto.countdown = dis.readLong();
            proto.index = dis.readInt();
            proto.color = dis.readInt();
            proto.number = dis.readInt();
            proto.isFromLast = dis.readBoolean();
            dis.close();
            bais.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return proto;
    }
}
