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
/// S2C_Settle
/// </summary>
public class Mahjong_S2C_SettleProto
{
    public static final int CODE = 30008; 

    private boolean isOver; //
    private ArrayList<Seat> seats = new ArrayList<Seat>(); //
    public boolean getIsOver(){
        return this.isOver;
    }

    public void setIsOver(boolean value){
        this.isOver = value;
    }

    public ArrayList<Seat> getseatsList(){
        return this.seats;
    };

    public Seat getSeats(int index){
        return this.seats.get(index);
    };

    public int seatsCount(){
        return this.seats.size();
    };

    public void addSeats(Seat value){
        this.seats.add(value);
    };


    /// <summary>
    /// 
    /// </summary>
    public static class Seat
    {
        private byte status; //
        private boolean isLoser; //
        private boolean isWinner; //
        private boolean isReadyHand; //
        private int playerId; //
        private int pos; //
        private int gold; //
        private int settle; //
        private Mahjong universalMahjong; //
        private Mahjong hitMahjong; //
        private ArrayList<Mahjong> mahjongs = new ArrayList<Mahjong>(); //
        private ArrayList<Mahjong> desktopMahjongs = new ArrayList<Mahjong>(); //
        private String incomesDesc = ""; //
        private ArrayList<MahjongGroup> usedMahjongGroup = new ArrayList<MahjongGroup>(); //
        public byte getStatus(){
            return this.status;
        }

        public void setStatus(byte value){
            this.status = value;
        }

        public boolean getIsLoser(){
            return this.isLoser;
        }

        public void setIsLoser(boolean value){
            this.isLoser = value;
        }

        public boolean getIsWinner(){
            return this.isWinner;
        }

        public void setIsWinner(boolean value){
            this.isWinner = value;
        }

        public boolean getIsReadyHand(){
            return this.isReadyHand;
        }

        public void setIsReadyHand(boolean value){
            this.isReadyHand = value;
        }

        public int getPlayerId(){
            return this.playerId;
        }

        public void setPlayerId(int value){
            this.playerId = value;
        }

        public int getPos(){
            return this.pos;
        }

        public void setPos(int value){
            this.pos = value;
        }

        public int getGold(){
            return this.gold;
        }

        public void setGold(int value){
            this.gold = value;
        }

        public int getSettle(){
            return this.settle;
        }

        public void setSettle(int value){
            this.settle = value;
        }

        public Mahjong getUniversalMahjong(){
            return this.universalMahjong;
        }

        public void setUniversalMahjong(Mahjong value){
            this.universalMahjong = value;
        }

        public Mahjong getHitMahjong(){
            return this.hitMahjong;
        }

        public void setHitMahjong(Mahjong value){
            this.hitMahjong = value;
        }

        public ArrayList<Mahjong> getmahjongsList(){
            return this.mahjongs;
        };

        public Mahjong getMahjongs(int index){
            return this.mahjongs.get(index);
        };

        public int mahjongsCount(){
            return this.mahjongs.size();
        };

        public void addMahjongs(Mahjong value){
            this.mahjongs.add(value);
        };

        public ArrayList<Mahjong> getdesktopMahjongsList(){
            return this.desktopMahjongs;
        };

        public Mahjong getDesktopMahjongs(int index){
            return this.desktopMahjongs.get(index);
        };

        public int desktopMahjongsCount(){
            return this.desktopMahjongs.size();
        };

        public void addDesktopMahjongs(Mahjong value){
            this.desktopMahjongs.add(value);
        };

        public String getIncomesDesc(){
            return this.incomesDesc;
        }

        public void setIncomesDesc(String value){
            this.incomesDesc = value;
        }

        public ArrayList<MahjongGroup> getusedMahjongGroupList(){
            return this.usedMahjongGroup;
        };

        public MahjongGroup getUsedMahjongGroup(int index){
            return this.usedMahjongGroup.get(index);
        };

        public int usedMahjongGroupCount(){
            return this.usedMahjongGroup.size();
        };

        public void addUsedMahjongGroup(MahjongGroup value){
            this.usedMahjongGroup.add(value);
        };

        public byte[] toArray()
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStreamExt dos = new DataOutputStreamExt(baos);
            byte[] ret = null;
            try{
                dos.writeByte(status);
                dos.writeBoolean(isLoser);
                dos.writeBoolean(isWinner);
                dos.writeBoolean(isReadyHand);
                dos.writeInt(playerId);
                dos.writeInt(pos);
                dos.writeInt(gold);
                dos.writeInt(settle);
                if(universalMahjong != null)
                {
                    dos.writeBytes(universalMahjong.toArray());
                }
                else
                {
                    dos.writeInt(0);
                }
                if(hitMahjong != null)
                {
                    dos.writeBytes(hitMahjong.toArray());
                }
                else
                {
                    dos.writeInt(0);
                }
                dos.writeShort(mahjongs.size());
                for (int i = 0; i < mahjongs.size(); ++i)
                {
                    if(mahjongs != null)
                    {
                        dos.writeBytes(mahjongs.get(i).toArray());
                    }
                    else
                    {
                        dos.writeInt(0);
                    }
                }
                dos.writeShort(desktopMahjongs.size());
                for (int i = 0; i < desktopMahjongs.size(); ++i)
                {
                    if(desktopMahjongs != null)
                    {
                        dos.writeBytes(desktopMahjongs.get(i).toArray());
                    }
                    else
                    {
                        dos.writeInt(0);
                    }
                }
                dos.writeUTF(incomesDesc);
                dos.writeShort(usedMahjongGroup.size());
                for (int i = 0; i < usedMahjongGroup.size(); ++i)
                {
                    if(usedMahjongGroup != null)
                    {
                        dos.writeBytes(usedMahjongGroup.get(i).toArray());
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

        public static Seat getProto(byte[] buffer)
        {
            if(buffer == null) return null;
            Seat proto = new Seat();
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            DataInputStreamExt dis = new DataInputStreamExt(bais);
            try{
                proto.status = dis.readByte();
                proto.isLoser = dis.readBoolean();
                proto.isWinner = dis.readBoolean();
                proto.isReadyHand = dis.readBoolean();
                proto.playerId = dis.readInt();
                proto.pos = dis.readInt();
                proto.gold = dis.readInt();
                proto.settle = dis.readInt();
                proto.universalMahjong = Mahjong.getProto(dis.readBytes());
                proto.hitMahjong = Mahjong.getProto(dis.readBytes());
                short mahjongsLength = dis.readShort();
                for (int i = 0; i < mahjongsLength; ++i)
                {
                    proto.mahjongs.add(Mahjong.getProto(dis.readBytes()));
                }
                short desktopMahjongsLength = dis.readShort();
                for (int i = 0; i < desktopMahjongsLength; ++i)
                {
                    proto.desktopMahjongs.add(Mahjong.getProto(dis.readBytes()));
                }
                proto.incomesDesc = dis.readUTF();
                short usedMahjongGroupLength = dis.readShort();
                for (int i = 0; i < usedMahjongGroupLength; ++i)
                {
                    proto.usedMahjongGroup.add(MahjongGroup.getProto(dis.readBytes()));
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
    /// <summary>
    /// 
    /// </summary>
    public static class MahjongGroup
    {
        private byte typeId; //
        private byte subTypeId; //
        private ArrayList<Mahjong> mahjongs = new ArrayList<Mahjong>(); //
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

        public ArrayList<Mahjong> getmahjongsList(){
            return this.mahjongs;
        };

        public Mahjong getMahjongs(int index){
            return this.mahjongs.get(index);
        };

        public int mahjongsCount(){
            return this.mahjongs.size();
        };

        public void addMahjongs(Mahjong value){
            this.mahjongs.add(value);
        };

        public byte[] toArray()
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStreamExt dos = new DataOutputStreamExt(baos);
            byte[] ret = null;
            try{
                dos.writeByte(typeId);
                dos.writeByte(subTypeId);
                dos.writeShort(mahjongs.size());
                for (int i = 0; i < mahjongs.size(); ++i)
                {
                    if(mahjongs != null)
                    {
                        dos.writeBytes(mahjongs.get(i).toArray());
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

        public static MahjongGroup getProto(byte[] buffer)
        {
            if(buffer == null) return null;
            MahjongGroup proto = new MahjongGroup();
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            DataInputStreamExt dis = new DataInputStreamExt(bais);
            try{
                proto.typeId = dis.readByte();
                proto.subTypeId = dis.readByte();
                short mahjongsLength = dis.readShort();
                for (int i = 0; i < mahjongsLength; ++i)
                {
                    proto.mahjongs.add(Mahjong.getProto(dis.readBytes()));
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
    public byte[] toArray()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStreamExt dos = new DataOutputStreamExt(baos);
        byte[] ret = null;
        try{
            dos.writeInt(CODE);
            dos.writeBoolean(isOver);
            dos.writeShort(seats.size());
            for (int i = 0; i < seats.size(); ++i)
            {
                if(seats != null)
                {
                    dos.writeBytes(seats.get(i).toArray());
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

    public static Mahjong_S2C_SettleProto getProto(byte[] buffer)
    {
        if(buffer == null) return null;
        Mahjong_S2C_SettleProto proto = new Mahjong_S2C_SettleProto();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStreamExt dis = new DataInputStreamExt(bais);
        try{
            proto.isOver = dis.readBoolean();
            short seatsLength = dis.readShort();
            for (int i = 0; i < seatsLength; ++i)
            {
                proto.seats.add(Seat.getProto(dis.readBytes()));
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
