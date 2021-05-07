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
/// S2C_InRoom
/// </summary>
public class Game_S2C_InRoomProto
{
    public static final int CODE = 20013; 

    private int gameId; //
    public int getGameId(){
        return this.gameId;
    }

    public void setGameId(int value){
        this.gameId = value;
    }


    public byte[] toArray()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStreamExt dos = new DataOutputStreamExt(baos);
        byte[] ret = null;
        try{
            dos.writeInt(CODE);
            dos.writeInt(gameId);
            ret = baos.toByteArray();
            dos.close();
            baos.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return ret;
    }

    public static Game_S2C_InRoomProto getProto(byte[] buffer)
    {
        if(buffer == null) return null;
        Game_S2C_InRoomProto proto = new Game_S2C_InRoomProto();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStreamExt dis = new DataInputStreamExt(bais);
        try{
            proto.gameId = dis.readInt();
            dis.close();
            bais.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return proto;
    }
}
