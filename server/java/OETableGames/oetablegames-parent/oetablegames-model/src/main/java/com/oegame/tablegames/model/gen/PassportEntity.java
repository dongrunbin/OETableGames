package com.oegame.tablegames.model.gen;

import com.oegame.tablegames.common.io.DataInputStreamExt;
import com.oegame.tablegames.common.io.DataOutputStreamExt;
import com.oegame.tablegames.model.AbstractMySqlEntity;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PassportEntity extends AbstractMySqlEntity
{
    //管理员账户 
    public String passport;

    //令牌 
    public String token;

    //令牌失效时间 
    public long tokenExpire;

    //状态 
    public byte status;

    //创建时间 
    public long reg_time;

    //创建IP 
    public long reg_ip;

    //登录时间 
    public long log_time;

    //登录IP 
    public long log_ip;

    //设备 
    public int device;

    public long getId(){ return this.id;}

    public String getPassport(){ return this.passport;}

    public String getToken(){ return this.token;}

    public long getTokenExpire(){ return this.tokenExpire;}

    public byte getStatus(){ return this.status;}

    public long getReg_time(){ return this.reg_time;}

    public long getReg_ip(){ return this.reg_ip;}

    public long getLog_time(){ return this.log_time;}

    public long getLog_ip(){ return this.log_ip;}

    public int getDevice(){ return this.device;}

    public void setId(long value){ this.id = value;}

    public void setPassport(String value){ this.passport = value;}

    public void setToken(String value){ this.token = value;}

    public void setTokenExpire(long value){ this.tokenExpire = value;}

    public void setStatus(byte value){ this.status = value;}

    public void setReg_time(long value){ this.reg_time = value;}

    public void setReg_ip(long value){ this.reg_ip = value;}

    public void setLog_time(long value){ this.log_time = value;}

    public void setLog_ip(long value){ this.log_ip = value;}

    public void setDevice(int value){ this.device = value;}

    public byte[] serialize()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStreamExt dos = new DataOutputStreamExt(baos);
        byte[] ret = null;
        try
        {
            dos.writeLong(id);
            dos.writeUTF(passport);
            dos.writeUTF(token);
            dos.writeLong(tokenExpire);
            dos.writeByte(status);
            dos.writeLong(reg_time);
            dos.writeLong(reg_ip);
            dos.writeLong(log_time);
            dos.writeLong(log_ip);
            dos.writeInt(device);
            ret = baos.toByteArray();
            dos.close();
            baos.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return ret;
    }

    public static PassportEntity deserialize(byte[] buffer)
    {
        if(buffer == null) return null;
        PassportEntity entity = new PassportEntity();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStreamExt dis = new DataInputStreamExt(bais);
        try
        {
            entity.id = dis.readLong();
            entity.passport = dis.readUTF();
            entity.token = dis.readUTF();
            entity.tokenExpire = dis.readLong();
            entity.status = dis.readByte();
            entity.reg_time = dis.readLong();
            entity.reg_ip = dis.readLong();
            entity.log_time = dis.readLong();
            entity.log_ip = dis.readLong();
            entity.device = dis.readInt();
            dis.close();
            bais.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return entity;
    }

}
