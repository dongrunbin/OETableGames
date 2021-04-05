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
    private int pokerAmount; //
    private int pokerTotal; //
    private Poker luckPoker; //
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

    public int getPokerAmount(){
        return this.pokerAmount;
    }

    public void setPokerAmount(int value){
        this.pokerAmount = value;
    }

    public int getPokerTotal(){
        return this.pokerTotal;
    }

    public void setPokerTotal(int value){
        this.pokerTotal = value;
    }

    public Poker getLuckPoker(){
        return this.luckPoker;
    }

    public void setLuckPoker(Poker value){
        this.luckPoker = value;
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
    public static class Poker
    {
        private int index; //
        private int color; //
        private int size; //
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

        public int getSize(){
            return this.size;
        }

        public void setSize(int value){
            this.size = value;
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
                dos.writeInt(size);
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
                proto.size = dis.readInt();
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
    public static class Seat
    {
        private boolean isBanker; //
        private int playerId; //
        private int pos; //
        private int handCount; //
        private int pokerAmount; //
        private int gold; //
        private Poker hitPoker; //
        private ArrayList<Poker> pokers = new ArrayList<Poker>(); //
        private ArrayList<Poker> universalPoker = new ArrayList<Poker>(); //
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

        public int getPokerAmount(){
            return this.pokerAmount;
        }

        public void setPokerAmount(int value){
            this.pokerAmount = value;
        }

        public int getGold(){
            return this.gold;
        }

        public void setGold(int value){
            this.gold = value;
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

        public ArrayList<Poker> getuniversalPokerList(){
            return this.universalPoker;
        };

        public Poker getUniversalPoker(int index){
            return this.universalPoker.get(index);
        };

        public int universalPokerCount(){
            return this.universalPoker.size();
        };

        public void addUniversalPoker(Poker value){
            this.universalPoker.add(value);
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
                dos.writeInt(pokerAmount);
                dos.writeInt(gold);
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
                dos.writeShort(universalPoker.size());
                for (int i = 0; i < universalPoker.size(); ++i)
                {
                    if(universalPoker != null)
                    {
                        dos.writeBytes(universalPoker.get(i).toArray());
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
                proto.pokerAmount = dis.readInt();
                proto.gold = dis.readInt();
                proto.hitPoker = Poker.getProto(dis.readBytes());
                short pokersLength = dis.readShort();
                for (int i = 0; i < pokersLength; ++i)
                {
                    proto.pokers.add(Poker.getProto(dis.readBytes()));
                }
                short universalPokerLength = dis.readShort();
                for (int i = 0; i < universalPokerLength; ++i)
                {
                    proto.universalPoker.add(Poker.getProto(dis.readBytes()));
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
            dos.writeInt(pokerAmount);
            dos.writeInt(pokerTotal);
            if(luckPoker != null)
            {
                dos.writeBytes(luckPoker.toArray());
            }
            else
            {
                dos.writeInt(0);
            }
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
            proto.pokerAmount = dis.readInt();
            proto.pokerTotal = dis.readInt();
            proto.luckPoker = Poker.getProto(dis.readBytes());
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
