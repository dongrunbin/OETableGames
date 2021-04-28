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
/// S2C_GameBegin
/// </summary>
public class Mahjong_S2C_GameBeginProto
{
    public static final int CODE = 30001; 

    private int roomId; //
    private int status; //
    private int gamesCount; //
    private ArrayList<Integer> dicePos = new ArrayList<Integer>(); //
    private ArrayList<Integer> dices = new ArrayList<Integer>(); //
    private int mahjongAmount; //
    private int mahjongTotal; //
    private ArrayList<Seat> seat = new ArrayList<Seat>(); //
    public int getRoomId(){
        return this.roomId;
    }

    public void setRoomId(int value){
        this.roomId = value;
    }

    public int getStatus(){
        return this.status;
    }

    public void setStatus(int value){
        this.status = value;
    }

    public int getGamesCount(){
        return this.gamesCount;
    }

    public void setGamesCount(int value){
        this.gamesCount = value;
    }

    public ArrayList<Integer> getdicePosList(){
        return this.dicePos;
    };

    public int getDicePos(int index){
        return this.dicePos.get(index);
    };

    public int dicePosCount(){
        return this.dicePos.size();
    };

    public void addDicePos(int value){
        this.dicePos.add(value);
    };

    public ArrayList<Integer> getdicesList(){
        return this.dices;
    };

    public int getDices(int index){
        return this.dices.get(index);
    };

    public int dicesCount(){
        return this.dices.size();
    };

    public void addDices(int value){
        this.dices.add(value);
    };

    public int getMahjongAmount(){
        return this.mahjongAmount;
    }

    public void setMahjongAmount(int value){
        this.mahjongAmount = value;
    }

    public int getMahjongTotal(){
        return this.mahjongTotal;
    }

    public void setMahjongTotal(int value){
        this.mahjongTotal = value;
    }

    public ArrayList<Seat> getseatList(){
        return this.seat;
    };

    public Seat getSeat(int index){
        return this.seat.get(index);
    };

    public int seatCount(){
        return this.seat.size();
    };

    public void addSeat(Seat value){
        this.seat.add(value);
    };


    /// <summary>
    /// 
    /// </summary>
    public static class Seat
    {
        private boolean isBanker; //
        private int playerId; //
        private int pos; //
        private int handCount; //
        private int mahjongAmount; //
        private int gold; //
        private Mahjong hitMahjong; //
        private ArrayList<Mahjong> mahjongs = new ArrayList<Mahjong>(); //
        private ArrayList<Mahjong> universalMahjongs = new ArrayList<Mahjong>(); //
        public boolean getIsBanker(){
            return this.isBanker;
        }

        public void setIsBanker(boolean value){
            this.isBanker = value;
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

        public int getHandCount(){
            return this.handCount;
        }

        public void setHandCount(int value){
            this.handCount = value;
        }

        public int getMahjongAmount(){
            return this.mahjongAmount;
        }

        public void setMahjongAmount(int value){
            this.mahjongAmount = value;
        }

        public int getGold(){
            return this.gold;
        }

        public void setGold(int value){
            this.gold = value;
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

        public ArrayList<Mahjong> getuniversalMahjongsList(){
            return this.universalMahjongs;
        };

        public Mahjong getUniversalMahjongs(int index){
            return this.universalMahjongs.get(index);
        };

        public int universalMahjongsCount(){
            return this.universalMahjongs.size();
        };

        public void addUniversalMahjongs(Mahjong value){
            this.universalMahjongs.add(value);
        };

        public byte[] toArray()
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStreamExt dos = new DataOutputStreamExt(baos);
            byte[] ret = null;
            try{
                dos.writeBoolean(isBanker);
                dos.writeInt(playerId);
                dos.writeInt(pos);
                dos.writeInt(handCount);
                dos.writeInt(mahjongAmount);
                dos.writeInt(gold);
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
                dos.writeShort(universalMahjongs.size());
                for (int i = 0; i < universalMahjongs.size(); ++i)
                {
                    if(universalMahjongs != null)
                    {
                        dos.writeBytes(universalMahjongs.get(i).toArray());
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
                proto.isBanker = dis.readBoolean();
                proto.playerId = dis.readInt();
                proto.pos = dis.readInt();
                proto.handCount = dis.readInt();
                proto.mahjongAmount = dis.readInt();
                proto.gold = dis.readInt();
                proto.hitMahjong = Mahjong.getProto(dis.readBytes());
                short mahjongsLength = dis.readShort();
                for (int i = 0; i < mahjongsLength; ++i)
                {
                    proto.mahjongs.add(Mahjong.getProto(dis.readBytes()));
                }
                short universalMahjongsLength = dis.readShort();
                for (int i = 0; i < universalMahjongsLength; ++i)
                {
                    proto.universalMahjongs.add(Mahjong.getProto(dis.readBytes()));
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
    public byte[] toArray()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStreamExt dos = new DataOutputStreamExt(baos);
        byte[] ret = null;
        try{
            dos.writeInt(CODE);
            dos.writeInt(roomId);
            dos.writeInt(status);
            dos.writeInt(gamesCount);
            dos.writeShort(dicePos.size());
            for (int i = 0; i < dicePos.size(); ++i)
            {
                dos.writeInt(dicePos.get(i));
            }
            dos.writeShort(dices.size());
            for (int i = 0; i < dices.size(); ++i)
            {
                dos.writeInt(dices.get(i));
            }
            dos.writeInt(mahjongAmount);
            dos.writeInt(mahjongTotal);
            dos.writeShort(seat.size());
            for (int i = 0; i < seat.size(); ++i)
            {
                if(seat != null)
                {
                    dos.writeBytes(seat.get(i).toArray());
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

    public static Mahjong_S2C_GameBeginProto getProto(byte[] buffer)
    {
        if(buffer == null) return null;
        Mahjong_S2C_GameBeginProto proto = new Mahjong_S2C_GameBeginProto();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStreamExt dis = new DataInputStreamExt(bais);
        try{
            proto.roomId = dis.readInt();
            proto.status = dis.readInt();
            proto.gamesCount = dis.readInt();
            short dicePosLength = dis.readShort();
            for (int i = 0; i < dicePosLength; ++i)
            {
                proto.dicePos.add(dis.readInt());
            }
            short dicesLength = dis.readShort();
            for (int i = 0; i < dicesLength; ++i)
            {
                proto.dices.add(dis.readInt());
            }
            proto.mahjongAmount = dis.readInt();
            proto.mahjongTotal = dis.readInt();
            short seatLength = dis.readShort();
            for (int i = 0; i < seatLength; ++i)
            {
                proto.seat.add(Seat.getProto(dis.readBytes()));
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
