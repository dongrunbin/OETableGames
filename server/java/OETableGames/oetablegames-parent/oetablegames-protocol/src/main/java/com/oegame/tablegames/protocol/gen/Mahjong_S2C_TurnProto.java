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
/// S2C_Turn
/// </summary>
public class Mahjong_S2C_TurnProto
{
    public static final int CODE = 30005; 

    private long countdown; //
    private ArrayList<MahjongGroup> askMahjongGroup = new ArrayList<MahjongGroup>(); //
    public long getCountdown(){
        return this.countdown;
    }

    public void setCountdown(long value){
        this.countdown = value;
    }

    public ArrayList<MahjongGroup> getaskMahjongGroupList(){
        return this.askMahjongGroup;
    };

    public MahjongGroup getAskMahjongGroup(int index){
        return this.askMahjongGroup.get(index);
    };

    public int askMahjongGroupCount(){
        return this.askMahjongGroup.size();
    };

    public void addAskMahjongGroup(MahjongGroup value){
        this.askMahjongGroup.add(value);
    };


    /// <summary>
    /// 
    /// </summary>
    public static class MahjongGroup
    {
        private byte typeId; //
        private byte subTypeId; //
        private int playerId; //
        private Mahjong mahjongs; //
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

        public Mahjong getMahjongs(){
            return this.mahjongs;
        }

        public void setMahjongs(Mahjong value){
            this.mahjongs = value;
        }

        public byte[] toArray()
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStreamExt dos = new DataOutputStreamExt(baos);
            byte[] ret = null;
            try{
                dos.writeByte(typeId);
                dos.writeByte(subTypeId);
                dos.writeInt(playerId);
                if(mahjongs != null)
                {
                    dos.writeBytes(mahjongs.toArray());
                }
                else
                {
                    dos.writeInt(0);
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

        public static MahjongGroup getProto(byte[] buffer)
        {
            if(buffer == null) return null;
            MahjongGroup proto = new MahjongGroup();
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            DataInputStreamExt dis = new DataInputStreamExt(bais);
            try{
                proto.typeId = dis.readByte();
                proto.subTypeId = dis.readByte();
                proto.playerId = dis.readInt();
                proto.mahjongs = Mahjong.getProto(dis.readBytes());
                dis.close();
                bais.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
            return proto;
        }
    }
    /// <summary>
    /// 
    /// </summary>
    public static class Mahjong
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

        public static Mahjong getProto(byte[] buffer)
        {
            if(buffer == null) return null;
            Mahjong proto = new Mahjong();
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
            dos.writeLong(countdown);
            dos.writeShort(askMahjongGroup.size());
            for (int i = 0; i < askMahjongGroup.size(); ++i)
            {
                if(askMahjongGroup != null)
                {
                    dos.writeBytes(askMahjongGroup.get(i).toArray());
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

    public static Mahjong_S2C_TurnProto getProto(byte[] buffer)
    {
        if(buffer == null) return null;
        Mahjong_S2C_TurnProto proto = new Mahjong_S2C_TurnProto();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStreamExt dis = new DataInputStreamExt(bais);
        try{
            proto.countdown = dis.readLong();
            short askMahjongGroupLength = dis.readShort();
            for (int i = 0; i < askMahjongGroupLength; ++i)
            {
                proto.askMahjongGroup.add(MahjongGroup.getProto(dis.readBytes()));
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
