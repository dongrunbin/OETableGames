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
/// S2C_Operate
/// </summary>
public class Mahjong_S2C_OperateProto
{
    public static final int CODE = 30007; 

    private byte typeId; //
    private byte subTypeId; //
    private int playerId; //
    private ArrayList<Poker> pokers = new ArrayList<Poker>(); //
    public byte getTypeId(){
        return this.typeId;
    }

    public void setTypeId(byte value){
        this.typeId = value;
    }

    public byte getSubTypeId(){
        return this.subTypeId;
    }

    public void setSubTypeId(byte value){
        this.subTypeId = value;
    }

    public int getPlayerId(){
        return this.playerId;
    }

    public void setPlayerId(int value){
        this.playerId = value;
    }

    public ArrayList<Poker> getpokersList(){
        return this.pokers;
    };

    public Poker getPokers(int index){
        return this.pokers.get(index);
    };

    public int pokersCount(){
        return this.pokers.size();
    };

    public void addPokers(Poker value){
        this.pokers.add(value);
    };


    /// <summary>
    /// 
    /// </summary>
    public static class Poker
    {
        private int index; //
        private int color; //
        private int number; //
        private int pos; //
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

        public int getPos(){
            return this.pos;
        }

        public void setPos(int value){
            this.pos = value;
        }

        public byte[] toArray()
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStreamExt dos = new DataOutputStreamExt(baos);
            byte[] ret = null;
            try{
                dos.writeInt(index);
                dos.writeInt(color);
                dos.writeInt(number);
                dos.writeInt(pos);
                ret = baos.toByteArray();
                dos.close();
                baos.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
            return ret;
        }

        public static Poker getProto(byte[] buffer)
        {
            if(buffer == null) return null;
            Poker proto = new Poker();
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            DataInputStreamExt dis = new DataInputStreamExt(bais);
            try{
                proto.index = dis.readInt();
                proto.color = dis.readInt();
                proto.number = dis.readInt();
                proto.pos = dis.readInt();
                dis.close();
                bais.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
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
            dos.writeByte(typeId);
            dos.writeByte(subTypeId);
            dos.writeInt(playerId);
            dos.writeShort(pokers.size());
            for (int i = 0; i < pokers.size(); ++i)
            {
                if(pokers != null)
                {
                    dos.writeBytes(pokers.get(i).toArray());
                }
                else
                {
                    dos.writeInt(0);
                }
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

    public static Mahjong_S2C_OperateProto getProto(byte[] buffer)
    {
        if(buffer == null) return null;
        Mahjong_S2C_OperateProto proto = new Mahjong_S2C_OperateProto();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStreamExt dis = new DataInputStreamExt(bais);
        try{
            proto.typeId = dis.readByte();
            proto.subTypeId = dis.readByte();
            proto.playerId = dis.readInt();
            short pokersLength = dis.readShort();
            for (int i = 0; i < pokersLength; ++i)
            {
                proto.pokers.add(Poker.getProto(dis.readBytes()));
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
