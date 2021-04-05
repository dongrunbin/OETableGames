package com.oegame.tablegames.model.gen;

import com.oegame.tablegames.common.util.StringUtil;
import com.oegame.tablegames.model.AbstractCacheModel;
import com.oegame.tablegames.model.RedisConfig;
import com.oegame.tablegames.model.RedisConn;
import com.oegame.tablegames.model.ReturnValue;
import com.oegame.tablegames.model.gen.PassportDBModel;
import com.oegame.tablegames.model.gen.PassportEntity;
import java.util.ArrayList;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Jedis;

/// <summary>
/// sys_passportCacheModel
/// </summary>
public class PassportCacheModel extends AbstractCacheModel<PassportEntity, PassportDBModel>
{
    private PassportCacheModel(){}

    private static volatile PassportCacheModel instance = null;
    public static PassportCacheModel getInstance()
    {
        if(instance == null)
        {
            synchronized (PassportCacheModel.class)
            {
                if (instance == null)
                {
                    instance = new PassportCacheModel();
                }
            }
        }
        return instance;
    }

    // 数据模型层单例
    @Override
    protected PassportDBModel getDBModel() { return PassportDBModel.getInstance();}
    @Override
    protected RedisConfig getRedisConfg()
    {
        return RedisConn.passport;
    }

    // 创建
    public ReturnValue<Object> insert(PassportEntity entity)
    {
        ReturnValue<Object> ret = this.getDBModel().insert(entity);
        if(ret.getReturnCode() < 0)
        {
            return ret;
        }
        long id = (long)ret.getOutputValue("id");
        entity.id = id;
        this.setCache(entity);
        if(this.existsCache("passport" + entity.passport))
        {
            this.lpushCache("passport" + entity.passport, entity);
        }
        return ret;
    }

    // 修改
    public ReturnValue<Object> update(PassportEntity entity)
    {
        PassportEntity old = this.getEntity(entity.id);
        if(old.passport != entity.passport)
        {
            this.deleteCache("passport" + old.passport);
            this.deleteCache("passport" + entity.passport);
        }
        if(old.status != entity.status)
        {
            this.deleteCache("passport" + old.passport);
            this.deleteCache("passport" + entity.passport);
        }
        ReturnValue<Object> ret = this.getDBModel().update(entity);
        if(ret.getReturnCode() > 0)
        {
            this.setCache(entity);
        }
        return ret;
    }

    // 根据编号删除
    public ReturnValue<Object> delete(long id)
    {
        PassportEntity entity = PassportEntity.deserialize(this.getCache(id));
        if(entity != null)
        {
        this.deleteCache("passport" + entity.passport);
        this.deleteCache(id);
        }
        return this.getDBModel().delete(id);
    }

    // 根据多个编号删除
    public ReturnValue<Object> delete(ArrayList<Long> ids)
    {
        Jedis redis =  this.getRedis();
        Transaction trans = redis.multi();
        for(int i = 0; i < ids.size(); ++i)
        {
            PassportEntity entity = PassportEntity.deserialize(this.getCache(ids.get(i)));
            if(entity != null)
            {
                this.deleteCache("passport" + entity.passport);
                this.deleteCache(entity.id);
            }
        }
        trans.exec();
        redis.close();
        return this.getDBModel().delete(ids);
    }

    // 根据条件查询数量
    public int getCount(String condition)
    {
        return this.getDBModel().getCount(condition);
    }

    // 根据编号查询实体
    public PassportEntity getEntity(long id)
    {
        PassportEntity entity = PassportEntity.deserialize(this.getCache(id));
        if(entity == null)
        {
            entity = this.getDBModel().getEntity(id);
            if(entity != null)
            {
                this.setCache(entity);
            }
        }
        return entity;
    }

    // 根据条件查询实体
    public PassportEntity getEntity(String condition, boolean isAutoStatus)
    {
        return this.getDBModel().getEntity(condition, isAutoStatus);
    }

    // 获取列表
    public ArrayList<PassportEntity> getList(String tableName, String columns, String condition, String orderby, boolean isDesc, boolean isAutoStatus)
    {
        return this.getDBModel().getList(tableName, columns, condition, orderby, isDesc, isAutoStatus);
    }

    // 获取列表
    public ArrayList<PassportEntity> getList()
    {
        ArrayList<PassportEntity> ret = this.getDBModel().getList();
        if(ret != null && ret.size() > 0)
        {
            for(int i = 0; i < ret.size(); ++i)
            {
                this.setCache(ret.get(i));
            }
        }
        return ret;
    }

    // 获取分页列表
    public ReturnValue<ArrayList<PassportEntity>> getPageList(String tableName, String columns, String condition, String orderby, boolean isDesc, int pageSize, int pageIndex, boolean isAutoStatus)
    {
        return this.getDBModel().getPageList(tableName, columns, condition, orderby, isDesc, pageSize, pageIndex, isAutoStatus);
    }

    public ArrayList<PassportEntity> getListByPassport(String passport)
    {
        ArrayList<byte[]> bytes = this.getCacheList("passport" + passport);
        ArrayList<PassportEntity> lst;
        if(bytes == null || bytes.size() == 0)
        {
            lst = this.getDBModel().getList("", "*", "passport = " + passport, "id", false, true);
            if(lst != null && lst.size() > 0)
            {
                this.lpushCacheList("passport" + passport, lst);
            }
        }
        else
        {
            lst = new ArrayList<PassportEntity>(bytes.size());
            for(int i = 0; i < bytes.size(); ++i)
            {
                long id = StringUtil.byteArrayToLong(bytes.get(i));
                PassportEntity entity = this.getEntity(id);
                lst.add(entity);
            }
        }
        return lst;
    }
    public ArrayList<PassportEntity> getListByPhone(String phone)
    {
        ArrayList<byte[]> bytes = this.getCacheList("phone" + phone);
        ArrayList<PassportEntity> lst;
        if(bytes == null || bytes.size() == 0)
        {
            lst = this.getDBModel().getList("", "*", "phone = " + phone, "id", false, true);
            if(lst != null && lst.size() > 0)
            {
                this.lpushCacheList("phone" + phone, lst);
            }
        }
        else
        {
            lst = new ArrayList<PassportEntity>(bytes.size());
            for(int i = 0; i < bytes.size(); ++i)
            {
                long id = StringUtil.byteArrayToLong(bytes.get(i));
                PassportEntity entity = this.getEntity(id);
                lst.add(entity);
            }
        }
        return lst;
    }
}
