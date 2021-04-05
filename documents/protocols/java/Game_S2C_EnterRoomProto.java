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
/// S2C_EnterRoom
/// </summary>
public class Game_S2C_EnterRoomProto
{
    public static final int CODE = 20003; 

    private int playerId; //
    private byte gender; //
    private String nickname = ""; //
    private String avatar = ""; //
    private int gold; //
    private int pos; //
    private long online; //
    private int originalGold; //
    public int getPlayerId(){
        return this.playerId;
    }

    public void setPlayerId(int value){
        this.playerId = value;
    }

    public byte getGender(){
        return this.gender;
    }

    public void setGender(byte value){
        this.gender = value;
    }

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

    public int getGold(){
        return this.gold;
    }

    public void setGold(int value){
        this.gold = value;
    }

    public int getPos(){
        return this.pos;
    }

    public void setPos(int value){
        this.pos = value;
    }

    public long getOnline(){
        return this.online;
    }

    public void setOnline(long value){
        this.online = value;
    }

    public int getOriginalGold(){
        return this.originalGold;
    }

    public void setOriginalGold(int value){
        this.originalGold = value;
    }


    public byte[] toArray()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStreamExt dos = new DataOutputStreamExt(baos);
        byte[] ret = null;
        try{
            dos.writeInt(CODE);
            dos.writeInt(playerId);
            dos.writeByte(gender);
            dos.writeUTF(nickname);
            dos.writeUTF(avatar);
            dos.writeInt(gold);
            dos.writeInt(pos);
            dos.writeLong(online);
            dos.writeInt(originalGold);
            ret = baos.toByteArray();
            dos.close();
            baos.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return ret;
    }

    public static Game_S2C_EnterRoomProto getProto(byte[] buffer)
    {
        if(buffer == null) return null;
        Game_S2C_EnterRoomProto proto = new Game_S2C_EnterRoomProto();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStreamExt dis = new DataInputStreamExt(bais);
        try{
            proto.playerId = dis.readInt();
            proto.gender = dis.readByte();
            proto.nickname = dis.readUTF();
            proto.avatar = dis.readUTF();
            proto.gold = dis.readInt();
            proto.pos = dis.readInt();
            proto.online = dis.readLong();
            proto.originalGold = dis.readInt();
            dis.close();
            bais.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return proto;
    }
}
