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
/// S2C_Discard
/// </summary>
public class Mahjong_S2C_DiscardProto
{
    public static final int CODE = 30004; 

    private int playerId; //
    private int index; //
    private int color; //
    private int number; //
    private boolean isReadyHand; //
    public int getPlayerId(){
        return this.playerId;
    }

    public void setPlayerId(int value){
        this.playerId = value;
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
            dos.writeInt(playerId);
            dos.writeInt(index);
            dos.writeInt(color);
            dos.writeInt(number);
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

    public static Mahjong_S2C_DiscardProto getProto(byte[] buffer)
    {
        if(buffer == null) return null;
        Mahjong_S2C_DiscardProto proto = new Mahjong_S2C_DiscardProto();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStreamExt dis = new DataInputStreamExt(bais);
        try{
            proto.playerId = dis.readInt();
            proto.index = dis.readInt();
            proto.color = dis.readInt();
            proto.number = dis.readInt();
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
