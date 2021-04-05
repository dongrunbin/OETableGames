package com.oegame.tablegames.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import com.oegame.tablegames.common.util.*;

public abstract class AbstractMySqlDBModel<T extends AbstractMySqlEntity>
{
	protected abstract String getConnectionString();
	
	protected abstract String getTableName();
	
	protected abstract MySqlParameter[] valueParas(T entity);
	
	protected abstract ArrayList<String> getColumnList();
	
    protected abstract T makeEntity(HashMap<String,Object> map);
	
	public ReturnValue<Object> insert(T entity)
	{
		return insert(null, entity);
	}
	
	public ReturnValue<Object> insert(Connection conn, T entity)
	{
	     MySqlParameter[] paramArray = valueParas(entity);
	     paramArray[0].setDirection(ParameterDirection.Output);
         paramArray[paramArray.length - 1].setDirection(ParameterDirection.Output);
         if (conn == null)
         {
        	 MySqlHelper.getInstance().executeNonQuery(getConnectionString(), CommandType.StoredProcedure, getTableName() + "_Create", paramArray);
         }
         else
         {
        	 MySqlHelper.getInstance().executeNonQuery(conn, CommandType.StoredProcedure, getTableName() + "_Create", paramArray);
         }
         
         int nReturnCode = Byte.parseByte(paramArray[paramArray.length - 1].getValue().toString());
         
         ReturnValue<Object> val = new ReturnValue<Object>();
         if (nReturnCode < 0)
         {
             val.setHasError(true);
         }
         else
         {
        	 val.setHasError(false);
        	 entity.id = StringUtil.toLong(paramArray[0].getValue().toString());
        	 val.setOutputValue("id", entity.id);
         }
         val.setReturnCode(nReturnCode);
         return val;
	}
	
	public ReturnValue<Object> update(T entity)
	{
		return update(null, entity);
	}
	
	public ReturnValue<Object> update(Connection conn, T entity)
	{
	     MySqlParameter[] paramArray = valueParas(entity);
         paramArray[paramArray.length - 1].setDirection(ParameterDirection.Output);
         
         if (conn == null)
         {
        	 MySqlHelper.getInstance().executeNonQuery(getConnectionString(), CommandType.StoredProcedure, getTableName() + "_Update", paramArray);
         }
         else
         {
        	 MySqlHelper.getInstance().executeNonQuery(conn, CommandType.StoredProcedure, getTableName() + "_Update", paramArray);
         }
         
         int nReturnCode = Byte.parseByte(paramArray[paramArray.length - 1].getValue().toString());

         ReturnValue<Object> val = new ReturnValue<Object>();
         if (nReturnCode < 0)
         {
             val.setHasError(true);
         }
         else
         {
        	 val.setHasError(false);
         }
         val.setReturnCode(nReturnCode);
         return val;
	}
	
	public ReturnValue<Object> delete(long id)
	{
		return delete(null, id);
	}
	
	public ReturnValue<Object> delete(Connection conn, long id)
	{
        MySqlParameter[] paramArray = new MySqlParameter[] { 
                new MySqlParameter("id", id, Types.BIGINT, 20),
                new MySqlParameter("retValue",Types.TINYINT, 11)
            };
        paramArray[paramArray.length - 1].setDirection(ParameterDirection.Output);
        
        if (conn == null)
        {
       	 	MySqlHelper.getInstance().executeNonQuery(getConnectionString(), CommandType.StoredProcedure, getTableName() + "_Delete", paramArray);
        }
        else
        {
       	 	MySqlHelper.getInstance().executeNonQuery(conn, CommandType.StoredProcedure, getTableName() + "_Delete", paramArray);
        }
        int nReturnCode = Byte.parseByte(paramArray[paramArray.length - 1].getValue().toString());
        ReturnValue<Object> val = new ReturnValue<Object>();
        if (nReturnCode < 0)
        {
            val.setHasError(true);
        }
        else
        {
       	 val.setHasError(false);
        }
        val.setReturnCode(nReturnCode);
        return val;
	}
	
    public ReturnValue<Object> delete(ArrayList<Long> ids)
    {
    	ReturnValue<Object> val = new ReturnValue<Object>();
    	
    	Connection conn = MySqlHelper.getInstance().connect(this.getConnectionString());
        try
        {
        	conn.setAutoCommit(false);
        	for(int i = 0; i <ids.size(); ++i)
        	{
        		delete(conn, ids.get(i));
        	}
        	conn.commit();
        	val.setHasError(false);
            val.setMessage("批量删除成功");
        }
        catch(Exception e)
        {
            try
            {
				conn.rollback();
			} 
            catch (SQLException e1) 
            {
				e1.printStackTrace();
			}
            val.setHasError(true);
            val.setMessage("批量删除失败");
        }
        return val;
    }
    
    public T getEntity(long id)
    {
    	MySqlParameter[] paramArray = new MySqlParameter[] { 
                new MySqlParameter("id", id, Types.BIGINT, 0)
            };

        return getEntity(this.getTableName() + "_GetEntity", paramArray, CommandType.StoredProcedure);
    }
    
    public T getEntity(String condition, boolean isAutoStatus)
    {
        if (isAutoStatus && this.getColumnList().contains("status") && condition.indexOf("status") == -1)
        {
            String statuString = " status = 1 ";
            condition = (statuString + ((condition == null || condition.equals("")) ? "" : " And ") + condition);
        }

        return getEntity(String.format("select * from `%s` where %s",this.getTableName(),condition), null, CommandType.Text);
    }
    
    public T getEntity(String sql, MySqlParameter[] paramArray, CommandType commandType)
    {
    	T entity = null;
    	ArrayList<HashMap<String,Object>> result = MySqlHelper.getInstance().executeQuery(this.getConnectionString(), commandType, sql, paramArray);
    	if(result != null && result.size() > 0)
		{
			entity = makeEntity(result.get(0));
		}
    	
    	return entity;
    }
    
    protected ArrayList<T> getEntityList(ArrayList<HashMap<String,Object>> map) throws SQLException
    {
    	ArrayList<T> list = new ArrayList<T>();
		if(map != null)
		{
			for(int i = 0; i < map.size(); ++i)
			{
				if(map.get(i) != null)
				{
					list.add(makeEntity(map.get(i)));
				}
			}
		}

        return list;
    }
    
    public ReturnValue<ArrayList<T>> getPageList(String tableName, String columns, String condition, String orderby, boolean isDesc, int pageSize, int pageIndex, boolean isAutoStatus)
    {
        return getPageListWithTran(null, tableName, columns, condition, orderby, isDesc, pageSize, pageIndex, isAutoStatus);
    }
    
    public ReturnValue<ArrayList<T>> getPageListWithTran(Connection conn, String tableName, String columns, String condition, String orderby, boolean isDesc, int pageSize, int pageIndex, boolean isAutoStatus)
    {
        if (isAutoStatus && this.getColumnList().contains("status") && condition.indexOf("status") == -1)
        {
            String statuString = " status = 1 ";
            condition = statuString + ((condition == null || condition.equals("")) ? "" : " And ") + condition;
        }

        orderby = orderby.toLowerCase();
        if (orderby.indexOf("asc") == -1 && orderby.indexOf("desc") == -1 && orderby.indexOf(",") == -1)
        {
            orderby += isDesc ? " desc" : " asc";
        }

        if (tableName == null || tableName.equals(""))
        {
            tableName = this.getTableName();
        }
        //表名
        MySqlParameter tableParam = new MySqlParameter("_tables", tableName,Types.VARCHAR,200);
        //查询列
        MySqlParameter colParam = new MySqlParameter("_fields", columns,Types.VARCHAR,200);
        //查询条件
        MySqlParameter whereParam = new MySqlParameter("_orderby", orderby,Types.VARCHAR,200);
        //排序条件
        MySqlParameter orderbyParam = new MySqlParameter("_where", condition,Types.VARCHAR,200);
        //分页条数
        MySqlParameter pageSizeParam = new MySqlParameter("_pagesize", pageSize,Types.INTEGER,11);
        //当前页码
        MySqlParameter pageIndexParam = new MySqlParameter("_pageindex", pageIndex,Types.INTEGER,11);
        //总记录数
        MySqlParameter pagesParam = new MySqlParameter("_totalcount", Types.INTEGER, 11);
        pagesParam.setDirection(ParameterDirection.Output);
        //总页数
        MySqlParameter pageCountParam = new MySqlParameter("_pagecount", Types.INTEGER, 11);
        pageCountParam.setDirection(ParameterDirection.Output);

        MySqlParameter[] paramArray = new MySqlParameter[] { tableParam, colParam, whereParam, orderbyParam, pageSizeParam, pageIndexParam, pagesParam, pageCountParam};

        ArrayList<HashMap<String,Object>> result = null;
        if (conn == null)
        {
        	result = MySqlHelper.getInstance().executeQuery(this.getConnectionString(), CommandType.StoredProcedure, "GetPageList", paramArray);
        }
        else
        {
        	result  = MySqlHelper.getInstance().executeQuery(conn, CommandType.StoredProcedure, "GetPageList", paramArray);
        }
        ReturnValue<ArrayList<T>> backVal = new ReturnValue<ArrayList<T>>();
        try 
        {
			backVal.setValue(this.getEntityList(result));
		} 
        catch (SQLException e) 
        {
			e.printStackTrace();
		}
        backVal.setOutputValue("totalCount", pagesParam.getValue());
        backVal.setOutputValue("pageCount", pageCountParam.getValue());
        return backVal;
    }
    
    public ArrayList<T> getList()
    {
    	return getListWithTran(null, this.getTableName(), "*", "", "id", false, true);
    }
    
    public ArrayList<T> getList(String tableName, String columns, String condition, String orderby, boolean isDesc, boolean isAutoStatus)
    {
        return getListWithTran(null, tableName, columns, condition, orderby, isDesc, isAutoStatus);
    }
    
    public ArrayList<T> getListWithTran(Connection conn, String tableName, String columns, String condition, String orderby, boolean isDesc, boolean isAutoStatus)
    {
        return getPageListWithTran(conn, tableName, columns, condition, orderby, isDesc, 1000000, 1, isAutoStatus).getValue();
    }
    
    public int getCount(String condition)
    {
        condition = (condition == null || condition .equals("")) ? "" : " where " + condition;
        String strSql = "select count(0) from " + this.getTableName() + condition + ";";
        ArrayList<HashMap<String,Object>> ret = MySqlHelper.getInstance().executeQuery(this.getConnectionString(), CommandType.Text, strSql, null);
        if(ret.size() > 0 && ret.get(0).containsKey("count(0)"))
        {
        	return (int)((long)ret.get(0).get("count(0)"));
        }
        return 0;
    }
}
