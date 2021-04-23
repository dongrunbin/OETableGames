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
/// C2S_LeaveRoom
/// </summary>
public class Game_C2S_LeaveRoomProto
{
    public static final int CODE = 20004; 

    private int roomId; //
    public int getRoomId(){
        return this.roomId;
    }

    public void setRoomId(int value){
        this.roomId = value;
    }


    public byte[] toArray()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStreamExt dos = new DataOutputStreamExt(baos);
        byte[] ret = null;
        try{
            dos.writeInt(CODE);
            dos.writeInt(roomId);
            ret = baos.toByteArray();
            dos.close();
            baos.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return ret;
    }

    public static Game_C2S_LeaveRoomProto getProto(byte[] buffer)
    {
        if(buffer == null) return null;
        Game_C2S_LeaveRoomProto proto = new Game_C2S_LeaveRoomProto();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStreamExt dis = new DataInputStreamExt(bais);
        try{
            proto.roomId = dis.readInt();
            dis.close();
            bais.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return proto;
    }
}
