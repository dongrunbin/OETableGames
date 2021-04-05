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
/// S2C_AskDisband
/// </summary>
public class Game_S2C_AskDisbandProto
{
    public static final int CODE = 20009; 

    private int playerId; //
    private byte disbandStatus; //
    private long disbandTimestamp; //
    private long disbandMaxTime; //
    public int getPlayerId(){
        return this.playerId;
    }

    public void setPlayerId(int value){
        this.playerId = value;
    }

    public byte getDisbandStatus(){
        return this.disbandStatus;
    }

    public void setDisbandStatus(byte value){
        this.disbandStatus = value;
    }

    public long getDisbandTimestamp(){
        return this.disbandTimestamp;
    }

    public void setDisbandTimestamp(long value){
        this.disbandTimestamp = value;
    }

    public long getDisbandMaxTime(){
        return this.disbandMaxTime;
    }

    public void setDisbandMaxTime(long value){
        this.disbandMaxTime = value;
    }


    public byte[] toArray()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStreamExt dos = new DataOutputStreamExt(baos);
        byte[] ret = null;
        try{
            dos.writeInt(CODE);
            dos.writeInt(playerId);
            dos.writeByte(disbandStatus);
            dos.writeLong(disbandTimestamp);
            dos.writeLong(disbandMaxTime);
            ret = baos.toByteArray();
            dos.close();
            baos.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return ret;
    }

    public static Game_S2C_AskDisbandProto getProto(byte[] buffer)
    {
        if(buffer == null) return null;
        Game_S2C_AskDisbandProto proto = new Game_S2C_AskDisbandProto();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStreamExt dis = new DataInputStreamExt(bais);
        try{
            proto.playerId = dis.readInt();
            proto.disbandStatus = dis.readByte();
            proto.disbandTimestamp = dis.readLong();
            proto.disbandMaxTime = dis.readLong();
            dis.close();
            bais.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return proto;
    }
}
