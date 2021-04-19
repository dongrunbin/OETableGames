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
/// S2C_AFK
/// </summary>
public class Game_S2C_AFKProto
{
    public static final int CODE = 20011; 

    private int playerId; //
    private boolean isAfk; //
    public int getPlayerId(){
        return this.playerId;
    }

    public void setPlayerId(int value){
        this.playerId = value;
    }

    public boolean getIsAfk(){
        return this.isAfk;
    }

    public void setIsAfk(boolean value){
        this.isAfk = value;
    }


    public byte[] toArray()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStreamExt dos = new DataOutputStreamExt(baos);
        byte[] ret = null;
        try{
            dos.writeInt(CODE);
            dos.writeInt(playerId);
            dos.writeBoolean(isAfk);
            ret = baos.toByteArray();
            dos.close();
            baos.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return ret;
    }

    public static Game_S2C_AFKProto getProto(byte[] buffer)
    {
        if(buffer == null) return null;
        Game_S2C_AFKProto proto = new Game_S2C_AFKProto();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStreamExt dis = new DataInputStreamExt(bais);
        try{
            proto.playerId = dis.readInt();
            proto.isAfk = dis.readBoolean();
            dis.close();
            bais.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return proto;
    }
}
