package com.oegame.tablegames.model;

import java.util.ArrayList;
import java.util.List;

import com.oegame.tablegames.common.util.StringUtil;

import redis.clients.jedis.Jedis;

public abstract class AbstractCacheModel<T extends AbstractMySqlEntity, M extends AbstractMySqlDBModel<T>>
{
	//缓存时间
    protected int getCacheTime()
    {
    	return 60 * 3; 
    }
    
    protected abstract RedisConfig getRedisConfg();
    
    protected abstract M getDBModel();
    
    public abstract T getEntity(long id);
    
    public abstract T getEntity(String condition, boolean isAutoStatus);
    
    public abstract ReturnValue<Object> insert(T entity);
    
    public abstract ReturnValue<Object> update(T entity);
    
    public abstract ReturnValue<Object> delete(long id);
    
    public abstract ReturnValue<Object> delete(ArrayList<Long> ids);
    
    public abstract int getCount(String condition);
    
    public abstract ArrayList<T> getList(String tableName, String columns, String condition, String orderby, boolean isDesc, boolean isAutoStatus);
    
    public abstract ArrayList<T> getList();
    
    public abstract ReturnValue<ArrayList<T>> getPageList(String tableName, String columns, String condition, String orderby, boolean isDesc, int pageSize, int pageIndex, boolean isAutoStatus);
    
    protected Jedis getRedis()
    {
    	return RedisHelper.getRedis(this.getRedisConfg());
    }
    
    private byte[] redisKey = null;
    protected byte[] getRedisKey()
    {
    	if(redisKey == null) redisKey = this.getDBModel().getTableName().getBytes();
    	return redisKey;
    }
    
    protected void setCache(T entity)
    {
    	if(entity == null) return;
    	setCache("id" + entity.id,entity);
    }
    
    protected void setCache(String field, T entity)
    {
    	Jedis redis = this.getRedis();
    	redis.hset(this.getRedisKey(), field.getBytes(), entity.serialize());
    	redis.close();
    }
    
    protected void lpushCache(String key, T entity)
    {
    	Jedis redis = this.getRedis();
    	redis.lpush((this.getDBModel().getTableName() + key).getBytes(), StringUtil.longToByteArray(entity.id));
    	redis.close();
    }
    
    protected void lpushCacheList(String key, List<T> list)
    {
    	if(list == null) return;
    	Jedis redis = this.getRedis();
    	byte[] byteKey = (this.getDBModel().getTableName() + key).getBytes();
    	redis.watch(byteKey);
    	for(int i = 0 ; i < list.size(); ++i)
    	{
    		redis.lpush(byteKey, StringUtil.longToByteArray(list.get(i).id));
    	}
    	redis.unwatch();
    	redis.close();
    }
    
    protected byte[] getCache(long id)
    {
    	return getCache("id" + id);
    }
    
    protected byte[] getCache(String field)
    {
    	byte[] bytes = null;
    	try
    	{
    		Jedis redis = this.getRedis();
    		bytes = redis.hget(this.getRedisKey(), field.getBytes());
    		redis.close();
    	}
    	catch(Exception e)
    	{
    		
    	}
    	
    	return bytes;
    }
    
    protected ArrayList<byte[]> getCacheList(String field)
    {
    	ArrayList<byte[]> bytes = null;
    	try
    	{
    		 Jedis redis = this.getRedis();
    		 bytes = (ArrayList<byte[]>)redis.lrange((this.getDBModel().getTableName() + field).getBytes(), 0, -1);
    		 redis.close();
    	}
    	catch(Exception e)
    	{
    		
    	}
    	return bytes;
    }
    
    protected boolean existsCache(String field)
    {
		 Jedis redis = this.getRedis();
		 boolean ret = redis.exists(this.getDBModel().getTableName() + field);
		 redis.close();
		 return ret;
    }
    
    protected boolean hexistsCache(byte[] key, byte[] field)
    {
		 Jedis redis = this.getRedis();
		 boolean ret =redis.hexists(key, field);
		 redis.close();
		 return ret;
    }
    
    protected void deleteCache(long id)
    {
    	deleteCache("id" + id);
    }
    
    protected void deleteCache(String field)
    {
    	Jedis redis = this.getRedis();
    	if(redis.hexists(this.getRedisKey(), field.getBytes()))
    	{
    		redis.hdel(this.getRedisKey(), field.getBytes());
    	}
    	else if(redis.exists((this.getDBModel().getTableName() + field).getBytes()))
    	{
    		redis.del((this.getDBModel().getTableName() + field).getBytes());
    	}
    	redis.close();
    }
}
