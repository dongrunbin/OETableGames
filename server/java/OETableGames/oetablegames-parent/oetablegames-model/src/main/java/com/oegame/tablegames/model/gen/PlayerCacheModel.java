package com.oegame.tablegames.model.gen;

import java.util.ArrayList;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Jedis;
import com.oegame.tablegames.common.util.StringUtil;
import com.oegame.tablegames.model.*;

public class PlayerCacheModel extends AbstractCacheModel<PlayerEntity,PlayerDBModel>
{
    private PlayerCacheModel(){}

    private static volatile PlayerCacheModel instance = null;
    public static PlayerCacheModel getInstance()
    {
        if(instance == null)
        {
            synchronized (PlayerCacheModel.class)
            {
                if (instance == null)
                {
                    instance = new PlayerCacheModel();
                }
            }
        }
        return instance;
    }

    // 数据模型层单例
    @Override
    protected PlayerDBModel getDBModel() { return PlayerDBModel.getInstance();}
    @Override
    protected RedisConfig getRedisConfg()
    {
        return RedisConn.passport;
    }

    // 创建
    public ReturnValue<Object> insert(PlayerEntity entity)
    {
        ReturnValue<Object> ret = this.getDBModel().insert(entity);
        if(ret.getReturnCode() < 0)
        {
            return ret;
        }
        long id = (long)ret.getOutputValue("id");
        entity.id = id;
        this.setCache(entity);
        if(this.existsCache("roomId" + entity.roomId))
        {
            this.lpushCache("roomId" + entity.roomId, entity);
        }
        return ret;
    }

    // 修改
    public ReturnValue<Object> update(PlayerEntity entity)
    {
        PlayerEntity old = this.getEntity(entity.id);
        if(old.roomId != entity.roomId)
        {
            this.deleteCache("roomId" + old.roomId);
            this.deleteCache("roomId" + entity.roomId);
        }
        if(old.status != entity.status)
        {
            this.deleteCache("roomId" + old.roomId);
            this.deleteCache("roomId" + entity.roomId);
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
        PlayerEntity entity = PlayerEntity.deserialize(this.getCache(id));
        if(entity != null)
        {
        this.deleteCache("roomId" + entity.roomId);
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
            PlayerEntity entity = PlayerEntity.deserialize(this.getCache(ids.get(i)));
            if(entity != null)
            {
                this.deleteCache("roomId" + entity.roomId);
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
    public PlayerEntity getEntity(long id)
    {
        PlayerEntity entity = PlayerEntity.deserialize(this.getCache(id));
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
    public PlayerEntity getEntity(String condition, boolean isAutoStatus)
    {
        return this.getDBModel().getEntity(condition, isAutoStatus);
    }

    // 获取列表
    public ArrayList<PlayerEntity> getList(String tableName, String columns, String condition, String orderby, boolean isDesc, boolean isAutoStatus)
    {
        return this.getDBModel().getList(tableName, columns, condition, orderby, isDesc, isAutoStatus);
    }

    // 获取列表
    public ArrayList<PlayerEntity> getList()
    {
        ArrayList<PlayerEntity> ret = this.getDBModel().getList();
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
    public ReturnValue<ArrayList<PlayerEntity>> getPageList(String tableName, String columns, String condition, String orderby, boolean isDesc, int pageSize, int pageIndex, boolean isAutoStatus)
    {
        return this.getDBModel().getPageList(tableName, columns, condition, orderby, isDesc, pageSize, pageIndex, isAutoStatus);
    }

    public ArrayList<PlayerEntity> getListByRoomId(int roomId)
    {
        ArrayList<byte[]> bytes = this.getCacheList("roomId" + roomId);
        ArrayList<PlayerEntity> lst;
        if(bytes == null || bytes.size() == 0)
        {
            lst = this.getDBModel().getList("", "*", "roomId = " + roomId, "id", false, true);
            if(lst != null && lst.size() > 0)
            {
                this.lpushCacheList("roomId" + roomId, lst);
            }
        }
        else
        {
            lst = new ArrayList<PlayerEntity>(bytes.size());
            for(int i = 0; i < bytes.size(); ++i)
            {
                long id = StringUtil.byteArrayToLong(bytes.get(i));
                PlayerEntity entity = this.getEntity(id);
                lst.add(entity);
            }
        }
        return lst;
    }
}
