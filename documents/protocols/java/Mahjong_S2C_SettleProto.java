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
        private Poker universalPoker; //
        private Poker hitPoker; //
        private ArrayList<Poker> pokers = new ArrayList<Poker>(); //
        private ArrayList<Poker> desktopPokers = new ArrayList<Poker>(); //
        private String incomesDesc = ""; //
        private ArrayList<PokerGroup> usedPokerGroup = new ArrayList<PokerGroup>(); //
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

        public Poker getUniversalPoker(){
            return this.universalPoker;
        }

        public void setUniversalPoker(Poker value){
            this.universalPoker = value;
        }

        public Poker getHitPoker(){
            return this.hitPoker;
        }

        public void setHitPoker(Poker value){
            this.hitPoker = value;
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

        public ArrayList<Poker> getdesktopPokersList(){
            return this.desktopPokers;
        };

        public Poker getDesktopPokers(int index){
            return this.desktopPokers.get(index);
        };

        public int desktopPokersCount(){
            return this.desktopPokers.size();
        };

        public void addDesktopPokers(Poker value){
            this.desktopPokers.add(value);
        };

        public String getIncomesDesc(){
            return this.incomesDesc;
        }

        public void setIncomesDesc(String value){
            this.incomesDesc = value;
        }

        public ArrayList<PokerGroup> getusedPokerGroupList(){
            return this.usedPokerGroup;
        };

        public PokerGroup getUsedPokerGroup(int index){
            return this.usedPokerGroup.get(index);
        };

        public int usedPokerGroupCount(){
            return this.usedPokerGroup.size();
        };

        public void addUsedPokerGroup(PokerGroup value){
            this.usedPokerGroup.add(value);
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
                if(universalPoker != null)
                {
                    dos.writeBytes(universalPoker.toArray());
                }
                else
                {
                    dos.writeInt(0);
                }
                if(hitPoker != null)
                {
                    dos.writeBytes(hitPoker.toArray());
                }
                else
                {
                    dos.writeInt(0);
                }
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
                dos.writeShort(desktopPokers.size());
                for (int i = 0; i < desktopPokers.size(); ++i)
                {
                    if(desktopPokers != null)
                    {
                        dos.writeBytes(desktopPokers.get(i).toArray());
                    }
                    else
                    {
                        dos.writeInt(0);
                    }
                }
                dos.writeUTF(incomesDesc);
                dos.writeShort(usedPokerGroup.size());
                for (int i = 0; i < usedPokerGroup.size(); ++i)
                {
                    if(usedPokerGroup != null)
                    {
                        dos.writeBytes(usedPokerGroup.get(i).toArray());
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
                proto.universalPoker = Poker.getProto(dis.readBytes());
                proto.hitPoker = Poker.getProto(dis.readBytes());
                short pokersLength = dis.readShort();
                for (int i = 0; i < pokersLength; ++i)
                {
                    proto.pokers.add(Poker.getProto(dis.readBytes()));
                }
                short desktopPokersLength = dis.readShort();
                for (int i = 0; i < desktopPokersLength; ++i)
                {
                    proto.desktopPokers.add(Poker.getProto(dis.readBytes()));
                }
                proto.incomesDesc = dis.readUTF();
                short usedPokerGroupLength = dis.readShort();
                for (int i = 0; i < usedPokerGroupLength; ++i)
                {
                    proto.usedPokerGroup.add(PokerGroup.getProto(dis.readBytes()));
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
    /// <summary>
    /// 
    /// </summary>
    public static class PokerGroup
    {
        private byte typeId; //
        private byte subTypeId; //
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

        public byte[] toArray()
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStreamExt dos = new DataOutputStreamExt(baos);
            byte[] ret = null;
            try{
                dos.writeByte(typeId);
                dos.writeByte(subTypeId);
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

        public static PokerGroup getProto(byte[] buffer)
        {
            if(buffer == null) return null;
            PokerGroup proto = new PokerGroup();
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            DataInputStreamExt dis = new DataInputStreamExt(bais);
            try{
                proto.typeId = dis.readByte();
                proto.subTypeId = dis.readByte();
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
