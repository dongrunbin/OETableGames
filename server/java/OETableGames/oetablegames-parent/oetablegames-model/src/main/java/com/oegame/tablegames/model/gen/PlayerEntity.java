package com.oegame.tablegames.model.gen;

import com.oegame.tablegames.model.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.oegame.tablegames.common.io.DataInputStreamExt;
import com.oegame.tablegames.common.io.DataOutputStreamExt;

/// <summary>
/// 
/// </summary>
public class PlayerEntity extends AbstractMySqlEntity
{
    //昵称 
    public String nickname;

    //头像 
    public String avatar;

    //性别 
    public byte gender;

    //房卡 
    public int cards;

    //房间ID 
    public int roomId;

    //游戏id 
    public int gameId;

    //状态 
    public byte status;

    //在线时间 
    public long online;

    //在线IP 
    public long ipaddr;

    //金币 
    public int gold;

    public long getId(){ return this.id;}

    public String getNickname(){ return this.nickname;}

    public String getAvatar(){ return this.avatar;}

    public byte getGender(){ return this.gender;}

    public int getCards(){ return this.cards;}

    public int getRoomId(){ return this.roomId;}

    public int getGameId(){ return this.gameId;}

    public byte getStatus(){ return this.status;}

    public long getOnline(){ return this.online;}

    public long getIpaddr(){ return this.ipaddr;}

    public int getGold(){ return this.gold;}

    public void setId(long value){ this.id = value;}

    public void setNickname(String value){ this.nickname = value;}

    public void setAvatar(String value){ this.avatar = value;}

    public void setGender(byte value){ this.gender = value;}

    public void setCards(int value){ this.cards = value;}

    public void setRoomId(int value){ this.roomId = value;}

    public void setGameId(int value){ this.gameId = value;}

    public void setStatus(byte value){ this.status = value;}

    public void setOnline(long value){ this.online = value;}

    public void setIpaddr(long value){ this.ipaddr = value;}

    public void setGold(int value){ this.gold = value;}

    public byte[] serialize()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStreamExt dos = new DataOutputStreamExt(baos);
        byte[] ret = null;
        try
        {
            dos.writeLong(id);
            dos.writeUTF(nickname);
            dos.writeUTF(avatar);
            dos.writeByte(gender);
            dos.writeInt(cards);
            dos.writeInt(roomId);
            dos.writeInt(gameId);
            dos.writeByte(status);
            dos.writeLong(online);
            dos.writeLong(ipaddr);
            dos.writeInt(gold);
            ret = baos.toByteArray();
            dos.close();
            baos.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return ret;
    }

    public static PlayerEntity deserialize(byte[] buffer)
    {
        if(buffer == null) return null;
        PlayerEntity entity = new PlayerEntity();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStreamExt dis = new DataInputStreamExt(bais);
        try
        {
            entity.id = dis.readLong();
            entity.nickname = dis.readUTF();
            entity.avatar = dis.readUTF();
            entity.gender = dis.readByte();
            entity.cards = dis.readInt();
            entity.roomId = dis.readInt();
            entity.gameId = dis.readInt();
            entity.status = dis.readByte();
            entity.online = dis.readLong();
            entity.ipaddr = dis.readLong();
            entity.gold = dis.readInt();
            dis.close();
            bais.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return entity;
    }

}
