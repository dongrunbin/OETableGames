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
/// C2S_EnterRoom
/// </summary>
public class Game_C2S_EnterRoomProto
{
    public static final int CODE = 20002; 

    private int roomId; //room id
    private int gameId; //game id
    public int getRoomId(){
        return this.roomId;
    }

    public void setRoomId(int value){
        this.roomId = value;
    }

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
            dos.writeInt(roomId);
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

    public static Game_C2S_EnterRoomProto getProto(byte[] buffer)
    {
        if(buffer == null) return null;
        Game_C2S_EnterRoomProto proto = new Game_C2S_EnterRoomProto();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStreamExt dis = new DataInputStreamExt(bais);
        try{
            proto.roomId = dis.readInt();
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
