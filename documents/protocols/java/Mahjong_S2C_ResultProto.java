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
/// S2C_Result
/// </summary>
public class Mahjong_S2C_ResultProto
{
    public static final int CODE = 30009; 

    private Seat seat; //
    private int pos; //
    private int playerId; //
    private int gold; //
    private boolean isWinner; //
    public Seat getSeat(){
        return this.seat;
    }

    public void setSeat(Seat value){
        this.seat = value;
    }

    public int getPos(){
        return this.pos;
    }

    public void setPos(int value){
        this.pos = value;
    }

    public int getPlayerId(){
        return this.playerId;
    }

    public void setPlayerId(int value){
        this.playerId = value;
    }

    public int getGold(){
        return this.gold;
    }

    public void setGold(int value){
        this.gold = value;
    }

    public boolean getIsWinner(){
        return this.isWinner;
    }

    public void setIsWinner(boolean value){
        this.isWinner = value;
    }


    /// <summary>
    /// 
    /// </summary>
    public static class Seat
    {
        public byte[] toArray()
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStreamExt dos = new DataOutputStreamExt(baos);
            byte[] ret = null;
            try{
                ret = baos.toByteArray();
                dos.close();
                baos.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
            return ret;
        }

        public static Seat getProto(byte[] buffer)
        {
            if(buffer == null) return null;
            Seat proto = new Seat();
            return proto;
        }
    }
    public byte[] toArray()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStreamExt dos = new DataOutputStreamExt(baos);
        byte[] ret = null;
        try{
            dos.writeInt(CODE);
            if(seat != null)
            {
                dos.writeBytes(seat.toArray());
            }
            else
            {
                dos.writeInt(0);
            }
            dos.writeInt(pos);
            dos.writeInt(playerId);
            dos.writeInt(gold);
            dos.writeBoolean(isWinner);
            ret = baos.toByteArray();
            dos.close();
            baos.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return ret;
    }

    public static Mahjong_S2C_ResultProto getProto(byte[] buffer)
    {
        if(buffer == null) return null;
        Mahjong_S2C_ResultProto proto = new Mahjong_S2C_ResultProto();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStreamExt dis = new DataInputStreamExt(bais);
        try{
            proto.seat = Seat.getProto(dis.readBytes());
            proto.pos = dis.readInt();
            proto.playerId = dis.readInt();
            proto.gold = dis.readInt();
            proto.isWinner = dis.readBoolean();
            dis.close();
            bais.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return proto;
    }
}
