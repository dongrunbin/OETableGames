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
/// S2C_Ready
/// </summary>
public class Game_S2C_ReadyProto
{
    public static final int CODE = 20007; 

    private int playerId; //
    public int getPlayerId(){
        return this.playerId;
    }

    public void setPlayerId(int value){
        this.playerId = value;
    }


    public byte[] toArray()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStreamExt dos = new DataOutputStreamExt(baos);
        byte[] ret = null;
        try{
            dos.writeInt(CODE);
            dos.writeInt(playerId);
            ret = baos.toByteArray();
            dos.close();
            baos.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return ret;
    }

    public static Game_S2C_ReadyProto getProto(byte[] buffer)
    {
        if(buffer == null) return null;
        Game_S2C_ReadyProto proto = new Game_S2C_ReadyProto();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStreamExt dis = new DataInputStreamExt(bais);
        try{
            proto.playerId = dis.readInt();
            dis.close();
            bais.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return proto;
    }
}
