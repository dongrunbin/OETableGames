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
/// C2S_Connect
/// </summary>
public class System_C2S_ConnectProto
{
    public static final int CODE = 10003; 

    private int passportId; //Passport Id
    private String token = ""; //Token
    public int getPassportId(){
        return this.passportId;
    }

    public void setPassportId(int value){
        this.passportId = value;
    }

    public String getToken(){
        return this.token;
    }

    public void setToken(String value){
        this.token = value;
    }


    public byte[] toArray()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStreamExt dos = new DataOutputStreamExt(baos);
        byte[] ret = null;
        try{
            dos.writeInt(CODE);
            dos.writeInt(passportId);
            dos.writeUTF(token);
            ret = baos.toByteArray();
            dos.close();
            baos.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return ret;
    }

    public static System_C2S_ConnectProto getProto(byte[] buffer)
    {
        if(buffer == null) return null;
        System_C2S_ConnectProto proto = new System_C2S_ConnectProto();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStreamExt dis = new DataInputStreamExt(bais);
        try{
            proto.passportId = dis.readInt();
            proto.token = dis.readUTF();
            dis.close();
            bais.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return proto;
    }
}
