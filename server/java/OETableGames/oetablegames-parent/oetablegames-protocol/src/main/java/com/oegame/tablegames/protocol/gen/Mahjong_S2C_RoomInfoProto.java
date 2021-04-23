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
/// S2C_RoomInfo
/// </summary>
public class Mahjong_S2C_RoomInfoProto
{
    public static final int CODE = 30010; 

    private byte roomStatus; //
    private int roomId; //
    private ArrayList<Integer> settingIds = new ArrayList<Integer>(); //
    private int mahjongAmount; //
    private int loop; //
    private int maxLoop; //
    private int mahjongTotal; //
    private int baseScore; //
    private int diceFirst; //
    private int diceSecond; //
    private int diceFirstA; //
    private int diceFirstB; //
    private int diceSecondA; //
    private int diceSecondB; //
    private long dismissMaxTime; //
    private long dismissTime; //
    private ArrayList<MahjongGroup> askMahjongGroups = new ArrayList<MahjongGroup>(); //
    private ArrayList<Seat> seats = new ArrayList<Seat>(); //
    public byte getRoomStatus(){
        return this.roomStatus;
    }

    public void setRoomStatus(byte value){
        this.roomStatus = value;
    }

    public int getRoomId(){
        return this.roomId;
    }

    public void setRoomId(int value){
        this.roomId = value;
    }

    public ArrayList<Integer> getsettingIdsList(){
        return this.settingIds;
    };

    public int getSettingIds(int index){
        return this.settingIds.get(index);
    };

    public int settingIdsCount(){
        return this.settingIds.size();
    };

    public void addSettingIds(int value){
        this.settingIds.add(value);
    };

    public int getMahjongAmount(){
        return this.mahjongAmount;
    }

    public void setMahjongAmount(int value){
        this.mahjongAmount = value;
    }

    public int getLoop(){
        return this.loop;
    }

    public void setLoop(int value){
        this.loop = value;
    }

    public int getMaxLoop(){
        return this.maxLoop;
    }

    public void setMaxLoop(int value){
        this.maxLoop = value;
    }

    public int getMahjongTotal(){
        return this.mahjongTotal;
    }

    public void setMahjongTotal(int value){
        this.mahjongTotal = value;
    }

    public int getBaseScore(){
        return this.baseScore;
    }

    public void setBaseScore(int value){
        this.baseScore = value;
    }

    public int getDiceFirst(){
        return this.diceFirst;
    }

    public void setDiceFirst(int value){
        this.diceFirst = value;
    }

    public int getDiceSecond(){
        return this.diceSecond;
    }

    public void setDiceSecond(int value){
        this.diceSecond = value;
    }

    public int getDiceFirstA(){
        return this.diceFirstA;
    }

    public void setDiceFirstA(int value){
        this.diceFirstA = value;
    }

    public int getDiceFirstB(){
        return this.diceFirstB;
    }

    public void setDiceFirstB(int value){
        this.diceFirstB = value;
    }

    public int getDiceSecondA(){
        return this.diceSecondA;
    }

    public void setDiceSecondA(int value){
        this.diceSecondA = value;
    }

    public int getDiceSecondB(){
        return this.diceSecondB;
    }

    public void setDiceSecondB(int value){
        this.diceSecondB = value;
    }

    public long getDismissMaxTime(){
        return this.dismissMaxTime;
    }

    public void setDismissMaxTime(long value){
        this.dismissMaxTime = value;
    }

    public long getDismissTime(){
        return this.dismissTime;
    }

    public void setDismissTime(long value){
        this.dismissTime = value;
    }

    public ArrayList<MahjongGroup> getaskMahjongGroupsList(){
        return this.askMahjongGroups;
    };

    public MahjongGroup getAskMahjongGroups(int index){
        return this.askMahjongGroups.get(index);
    };

    public int askMahjongGroupsCount(){
        return this.askMahjongGroups.size();
    };

    public void addAskMahjongGroups(MahjongGroup value){
        this.askMahjongGroups.add(value);
    };

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
    public static class MahjongGroup
    {
        private int typeId; //
        private int subTypeId; //
        private int playerId; //
        private ArrayList<Mahjong> mahjongs = new ArrayList<Mahjong>(); //
        public int getTypeId(){
            return this.typeId;
        }

        public void setTypeId(int value){
            this.typeId = value;
        }

        public int getSubTypeId(){
            return this.subTypeId;
        }

        public void setSubTypeId(int value){
            this.subTypeId = value;
        }

        public int getPlayerId(){
            return this.playerId;
        }

        public void setPlayerId(int value){
            this.playerId = value;
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
                dos.writeInt(typeId);
                dos.writeInt(subTypeId);
                dos.writeInt(playerId);
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
                proto.typeId = dis.readInt();
                proto.subTypeId = dis.readInt();
                proto.playerId = dis.readInt();
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
    /// <summary>
    /// 
    /// </summary>
    public static class Seat
    {
        private byte status; //
        private byte gender; //
        private byte disbandtatus; //
        private boolean isBanker; //
        private int playerId; //
        private int pos; //
        private int handCount; //
        private int mahjongAmount; //
        private int gold; //
        private Mahjong hitMahjong; //
        private ArrayList<Mahjong> desktop = new ArrayList<Mahjong>(); //
        private ArrayList<Mahjong> mahjongs = new ArrayList<Mahjong>(); //
        private ArrayList<Mahjong> universal = new ArrayList<Mahjong>(); //
        private ArrayList<MahjongGroup> usedMahjongGroup = new ArrayList<MahjongGroup>(); //
        private String nickname = ""; //
        private String avatar = ""; //
        public byte getStatus(){
            return this.status;
        }

        public void setStatus(byte value){
            this.status = value;
        }

        public byte getGender(){
            return this.gender;
        }

        public void setGender(byte value){
            this.gender = value;
        }

        public byte getDisbandtatus(){
            return this.disbandtatus;
        }

        public void setDisbandtatus(byte value){
            this.disbandtatus = value;
        }

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

        public ArrayList<Mahjong> getdesktopList(){
            return this.desktop;
        };

        public Mahjong getDesktop(int index){
            return this.desktop.get(index);
        };

        public int desktopCount(){
            return this.desktop.size();
        };

        public void addDesktop(Mahjong value){
            this.desktop.add(value);
        };

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

        public ArrayList<Mahjong> getuniversalList(){
            return this.universal;
        };

        public Mahjong getUniversal(int index){
            return this.universal.get(index);
        };

        public int universalCount(){
            return this.universal.size();
        };

        public void addUniversal(Mahjong value){
            this.universal.add(value);
        };

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

        public String getNickname(){
            return this.nickname;
        }

        public void setNickname(String value){
            this.nickname = value;
        }

        public String getAvatar(){
            return this.avatar;
        }

        public void setAvatar(String value){
            this.avatar = value;
        }

        public byte[] toArray()
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStreamExt dos = new DataOutputStreamExt(baos);
            byte[] ret = null;
            try{
                dos.writeByte(status);
                dos.writeByte(gender);
                dos.writeByte(disbandtatus);
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
                dos.writeShort(desktop.size());
                for (int i = 0; i < desktop.size(); ++i)
                {
                    if(desktop != null)
                    {
                        dos.writeBytes(desktop.get(i).toArray());
                    }
                    else
                    {
                        dos.writeInt(0);
                    }
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
                dos.writeShort(universal.size());
                for (int i = 0; i < universal.size(); ++i)
                {
                    if(universal != null)
                    {
                        dos.writeBytes(universal.get(i).toArray());
                    }
                    else
                    {
                        dos.writeInt(0);
                    }
                }
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
                dos.writeUTF(nickname);
                dos.writeUTF(avatar);
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
                proto.gender = dis.readByte();
                proto.disbandtatus = dis.readByte();
                proto.isBanker = dis.readBoolean();
                proto.playerId = dis.readInt();
                proto.pos = dis.readInt();
                proto.handCount = dis.readInt();
                proto.mahjongAmount = dis.readInt();
                proto.gold = dis.readInt();
                proto.hitMahjong = Mahjong.getProto(dis.readBytes());
                short desktopLength = dis.readShort();
                for (int i = 0; i < desktopLength; ++i)
                {
                    proto.desktop.add(Mahjong.getProto(dis.readBytes()));
                }
                short mahjongsLength = dis.readShort();
                for (int i = 0; i < mahjongsLength; ++i)
                {
                    proto.mahjongs.add(Mahjong.getProto(dis.readBytes()));
                }
                short universalLength = dis.readShort();
                for (int i = 0; i < universalLength; ++i)
                {
                    proto.universal.add(Mahjong.getProto(dis.readBytes()));
                }
                short usedMahjongGroupLength = dis.readShort();
                for (int i = 0; i < usedMahjongGroupLength; ++i)
                {
                    proto.usedMahjongGroup.add(MahjongGroup.getProto(dis.readBytes()));
                }
                proto.nickname = dis.readUTF();
                proto.avatar = dis.readUTF();
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

        public byte[] toArray()
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStreamExt dos = new DataOutputStreamExt(baos);
            byte[] ret = null;
            try{
                dos.writeInt(index);
                dos.writeInt(color);
                dos.writeInt(number);
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
            dos.writeByte(roomStatus);
            dos.writeInt(roomId);
            dos.writeShort(settingIds.size());
            for (int i = 0; i < settingIds.size(); ++i)
            {
                dos.writeInt(settingIds.get(i));
            }
            dos.writeInt(mahjongAmount);
            dos.writeInt(loop);
            dos.writeInt(maxLoop);
            dos.writeInt(mahjongTotal);
            dos.writeInt(baseScore);
            dos.writeInt(diceFirst);
            dos.writeInt(diceSecond);
            dos.writeInt(diceFirstA);
            dos.writeInt(diceFirstB);
            dos.writeInt(diceSecondA);
            dos.writeInt(diceSecondB);
            dos.writeLong(dismissMaxTime);
            dos.writeLong(dismissTime);
            dos.writeShort(askMahjongGroups.size());
            for (int i = 0; i < askMahjongGroups.size(); ++i)
            {
                if(askMahjongGroups != null)
                {
                    dos.writeBytes(askMahjongGroups.get(i).toArray());
                }
                else
                {
                    dos.writeInt(0);
                }
            }
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

    public static Mahjong_S2C_RoomInfoProto getProto(byte[] buffer)
    {
        if(buffer == null) return null;
        Mahjong_S2C_RoomInfoProto proto = new Mahjong_S2C_RoomInfoProto();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStreamExt dis = new DataInputStreamExt(bais);
        try{
            proto.roomStatus = dis.readByte();
            proto.roomId = dis.readInt();
            short settingIdsLength = dis.readShort();
            for (int i = 0; i < settingIdsLength; ++i)
            {
                proto.settingIds.add(dis.readInt());
            }
            proto.mahjongAmount = dis.readInt();
            proto.loop = dis.readInt();
            proto.maxLoop = dis.readInt();
            proto.mahjongTotal = dis.readInt();
            proto.baseScore = dis.readInt();
            proto.diceFirst = dis.readInt();
            proto.diceSecond = dis.readInt();
            proto.diceFirstA = dis.readInt();
            proto.diceFirstB = dis.readInt();
            proto.diceSecondA = dis.readInt();
            proto.diceSecondB = dis.readInt();
            proto.dismissMaxTime = dis.readLong();
            proto.dismissTime = dis.readLong();
            short askMahjongGroupsLength = dis.readShort();
            for (int i = 0; i < askMahjongGroupsLength; ++i)
            {
                proto.askMahjongGroups.add(MahjongGroup.getProto(dis.readBytes()));
            }
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
