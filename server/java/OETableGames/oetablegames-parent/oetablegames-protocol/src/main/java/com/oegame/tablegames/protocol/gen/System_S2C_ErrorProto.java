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
/// S2C_Error
/// </summary>
public class System_S2C_ErrorProto
{
    public static final int CODE = 10007; 

    private int code; //
    private String message = ""; //
    public int getCode(){
        return this.code;
    }

    public void setCode(int value){
        this.code = value;
    }

    public String getMessage(){
        return this.message;
    }

    public void setMessage(String value){
        this.message = value;
    }


    public byte[] toArray()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStreamExt dos = new DataOutputStreamExt(baos);
        byte[] ret = null;
        try{
            dos.writeInt(CODE);
            dos.writeInt(code);
            dos.writeUTF(message);
            ret = baos.toByteArray();
            dos.close();
            baos.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return ret;
    }

    public static System_S2C_ErrorProto getProto(byte[] buffer)
    {
        if(buffer == null) return null;
        System_S2C_ErrorProto proto = new System_S2C_ErrorProto();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStreamExt dis = new DataInputStreamExt(bais);
        try{
            proto.code = dis.readInt();
            proto.message = dis.readUTF();
            dis.close();
            bais.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return proto;
    }
}
