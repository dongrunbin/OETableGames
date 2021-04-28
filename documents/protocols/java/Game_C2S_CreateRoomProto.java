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
/// C2S_CreateRoom
/// </summary>
public class Game_C2S_CreateRoomProto
{
    public static final int CODE = 20001; 

    private ArrayList<Integer> settingIds = new ArrayList<Integer>(); //setting ids
    private int gameId; //game id
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
            dos.writeShort(settingIds.size());
            for (int i = 0; i < settingIds.size(); ++i)
            {
                dos.writeInt(settingIds.get(i));
            }
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

    public static Game_C2S_CreateRoomProto getProto(byte[] buffer)
    {
        if(buffer == null) return null;
        Game_C2S_CreateRoomProto proto = new Game_C2S_CreateRoomProto();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStreamExt dis = new DataInputStreamExt(bais);
        try{
            short settingIdsLength = dis.readShort();
            for (int i = 0; i < settingIdsLength; ++i)
            {
                proto.settingIds.add(dis.readInt());
            }
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
