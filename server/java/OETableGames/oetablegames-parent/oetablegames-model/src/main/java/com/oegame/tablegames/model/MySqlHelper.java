package com.oegame.tablegames.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.CallableStatement;

public class MySqlHelper 
{
	
	private static volatile MySqlHelper instance = null;
	
	private HashMap<String, BasicDataSource> dicDataSource;
	
	// 数据库驱动
	private String driver = "com.mysql.jdbc.Driver";
	
	public static MySqlHelper getInstance()
	{
		if(instance == null)
		{
			synchronized(MySqlHelper.class)
			{
				if(instance == null)
				{
					instance = new MySqlHelper();
				}
			}
		}
		return instance;
	}
	
	private MySqlHelper()
	{
		dicDataSource = new HashMap<String, BasicDataSource>();
	}
	
	public synchronized Connection connect(String connString)
	{
		if(!dicDataSource.containsKey(connString))
		{
			int initialSize = 1;
			int maxActive = 30;
			int maxIdle = 20;
			int minIdle = 10;
			int maxWait = 5000;

			BasicDataSource bds = new BasicDataSource();
//			bds.setUrl("jdbc:mysql://" + Config.MYSQL_MASTER_HOST + ":" + Config.MYSQL_MASTER_PORT + "/" + Config.MYSQL_MASTER_DATA + "?characterEncoding=UTF-8&autoReconnectForPools=true&rewriteBatchedStatements=true&useCursorFetch=true&defaultFetchSize=20");
			bds.setUrl(connString);
			bds.setDriverClassName(driver);
			bds.setUsername(PropertyConfigHelper.getMysqlUsername());
			bds.setPassword(PropertyConfigHelper.getMysqlPassword());

			//池启动时创建的连接数量
			bds.setInitialSize(initialSize);
			//同一时间可以从池分配的最多连接数量。设置为-1时表示无限制。
			bds.setMaxTotal(maxActive);
			//池里不会被释放的最多空闲连接数量。设置为-1时表示无限制。
			bds.setMaxIdle(maxIdle);
			//在不新建连接的条件下，池中保持空闲的最少连接数。
			bds.setMinIdle(minIdle);
			//在抛出异常之前，池等待连接被回收的最长时间（当没有可用连接时）。设置为-1表示无限等待。
			bds.setMaxWaitMillis(maxWait);

			//取得连接时是否进行有效性验证（即是否还和数据库连通的）
			bds.setTestOnBorrow(true);
			//返回连接时是否进行有效性验证（即是否还和数据库连通的）
			bds.setTestOnReturn(true);
			//连接空闲时是否进行有效性验证（即是否还和数据库连通的）
			bds.setTestWhileIdle(true);

			//每过timeBetweenEvictionRunsMillis 时间，就会启动一个线程
			bds.setTimeBetweenEvictionRunsMillis(3000);
			//校验连接池中闲置时间超过minEvictableIdleTimeMillis的连接对象
			bds.setMinEvictableIdleTimeMillis(180000);

			//是否自动回收超时连接
			bds.setRemoveAbandonedOnBorrow(true);
			//超时时间(以秒数为单位)
			bds.setRemoveAbandonedTimeout(180);
			dicDataSource.put(connString, bds);
		}
		Connection conn = null;
		try {
			conn = dicDataSource.get(connString).getConnection();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (conn == null) {
			System.out.println("没有获得连接..");
		}
		return conn;
	}
	
	public int executeNonQuery(String connString, CommandType commandType,String commandText)
	{
		return executeNonQuery(connString,commandType,commandText,null);
	}
	
	public int executeNonQuery(String connString, CommandType commandType,String commandText, MySqlParameter[] parameters)
	{
		Connection conn = this.connect(connString);
		int ret = executeNonQuery(conn,commandType,commandText,parameters);
		try
		{
			conn.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return ret;
	}
	
	public int executeNonQuery(Connection conn, CommandType commandType, String commandText, MySqlParameter[] parameters)
	{
		int ret = 0;
		if(conn == null) return ret;
		
		try 
		{
			if(commandType == CommandType.StoredProcedure)
			{
	        	String p = "(";
	        	if(parameters != null)
				{
		        	 for(int i = 0; i < parameters.length; ++i)
		        	 {
		        		 if(i == parameters.length - 1)
		        		 {
		        			 p += "?";
		        		 }
		        		 else
		        		 {
		        			 p += "?,";
		        		 }
		        	 }
				}
	        	p += ");";
	        	String sql = "call " + commandText + p;
//	        	System.out.println(commandText);
				CallableStatement cs = conn.prepareCall(sql);
				attachParams(cs,parameters);
				ret = cs.executeUpdate();
				getOutParams(cs, parameters);
				
				cs.close();
			}
			else if(commandType == CommandType.Text)
			{
//	        	System.out.println(commandText);
				PreparedStatement ps = conn.prepareStatement(commandText);
				attachParams(ps,parameters);
				ret = ps.executeUpdate();
				
				ps.close();
			}
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return ret;
	}
	
	public ArrayList<HashMap<String,Object>> executeQuery(String connString, CommandType commandType,String commandText, MySqlParameter[] parameters)
	{
		Connection conn = this.connect(connString);
		ArrayList<HashMap<String,Object>> ret = executeQuery(conn,commandType,commandText,parameters);
		try
		{
			conn.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return ret;
	}
	
	public ArrayList<HashMap<String,Object>> executeQuery(Connection conn, CommandType commandType,String commandText, MySqlParameter[] parameters)
	{
		ArrayList<HashMap<String,Object>> ret = new ArrayList<HashMap<String,Object>>();
		if(conn == null) return ret;
		
		try 
		{
			ResultSet rs = null;
			if(commandType == CommandType.StoredProcedure)
			{
	        	String p = "(";
	        	if(parameters != null)
				{
		        	 for(int i = 0; i < parameters.length; ++i)
		        	 {
		        		 if(i == parameters.length - 1)
		        		 {
		        			 p += "?";
		        		 }
		        		 else
		        		 {
		        			 p += "?,";
		        		 }
		        	 }
				}
	        	p += ");";
	        	String sql = "CALL " + commandText + p;
//	        	System.out.println(sql);
				CallableStatement cs = conn.prepareCall(sql);
				attachParams(cs,parameters);
				rs = cs.executeQuery();
				getOutParams(cs, parameters);
			}
			else if(commandType == CommandType.Text)
			{
//				System.out.println(commandText);
				PreparedStatement ps = conn.prepareStatement(commandText);
				attachParams(ps,parameters);
				rs = ps.executeQuery();
			}
			ResultSetMetaData mete = rs.getMetaData();
			while (rs != null && rs.next()) 
			{
				HashMap<String, Object> data = new HashMap<String, Object>();

				for (int i = 0; i < mete.getColumnCount(); i++) 
				{
					String ColumnName = mete.getColumnName(i + 1);
					int type = mete.getColumnType(i + 1);
					switch(type)
					{
					case Types.TINYINT:
						data.put(ColumnName, rs.getByte(ColumnName));
						break;
					case Types.BIGINT:
						data.put(ColumnName, rs.getLong(ColumnName));
						break;
					case Types.DOUBLE:
						data.put(ColumnName, rs.getDouble(ColumnName));
						break;
					case Types.FLOAT:
						data.put(ColumnName, rs.getFloat(ColumnName));
						break;
					case Types.INTEGER:
						data.put(ColumnName, rs.getInt(ColumnName));
						break;
					case Types.NVARCHAR:
						data.put(ColumnName, rs.getNString(ColumnName));
						break;
					case Types.VARCHAR:
						data.put(ColumnName, rs.getString(ColumnName));
						break;
					case Types.TIMESTAMP:
						data.put(ColumnName, rs.getLong(ColumnName));
						break;
					default:
						data.put(ColumnName, rs.getObject(ColumnName));
						break;
					}
				}
				ret.add(data);
			}
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return ret;
	}
	
	//附加参数
	private void attachParams(CallableStatement cs, MySqlParameter[] parameters) throws SQLException
	{
		if(cs == null)
		{
			return;
		}
		if(parameters != null)
		{
			for(int i = 0; i < parameters.length; ++i)
			{
				MySqlParameter p = parameters[i];
				if(p.getDirection() == ParameterDirection.Input)
				{
					setParam(cs, p);
				}
				else if(p.getDirection() == ParameterDirection.Output)
				{
					cs.registerOutParameter(p.getParameterName(), p.getDbType());
				}
				else if(p.getDirection() == ParameterDirection.InputOutput)
				{
					setParam(cs, p);
					cs.registerOutParameter(p.getParameterName(), p.getDbType());
				}
			}
		}
	}
	
	private void setParam(CallableStatement cs, MySqlParameter p) throws SQLException
	{
		String pName = p.getParameterName();
		switch(p.getDbType())
		{
		case Types.BIGINT:
			cs.setLong(pName,(long)p.getValue());
			break;
		case Types.BIT:
			cs.setByte(pName,(byte)p.getValue());
			break;
		case Types.BOOLEAN:
			cs.setBoolean(pName,(boolean)p.getValue());
			break;
		case Types.CHAR:
			cs.setByte(pName,(byte)p.getValue());
			break;
		case Types.INTEGER:
			cs.setInt(pName,(int)p.getValue());
			break;
		case Types.NVARCHAR:
			cs.setNString(pName,(String)p.getValue());
			break;
		case Types.TIMESTAMP:
			cs.setLong(pName,(long)p.getValue());
			break;
		case Types.TINYINT:
			cs.setByte(pName,(byte)p.getValue());
			break;
		case Types.VARCHAR:
			cs.setString(pName,(String)p.getValue());
			break;
		case Types.LONGVARCHAR:
			cs.setString(pName,(String)p.getValue());
			break;
		case Types.LONGNVARCHAR:
			cs.setNString(pName,(String)p.getValue());
			break;
		case Types.FLOAT:
			cs.setFloat(pName,(float)p.getValue());
			break;
		case Types.DOUBLE:
			cs.setDouble(pName,(double)p.getValue());
			break;
		}
	}
	
	//获取输出参数
	private void getOutParams(CallableStatement cs, MySqlParameter[] parameters) throws SQLException
	{
		if(cs == null)
		{
			return;
		}
		if(parameters != null)
		{
			for(int i = 0; i < parameters.length; ++i)
			{
				MySqlParameter p = parameters[i];
				if(p.getDirection() == ParameterDirection.Output || p.getDirection() == ParameterDirection.InputOutput)
				{
					getOutParam(cs, parameters[i]);
				}
			}
		}
	}
	
	private void getOutParam(CallableStatement cs, MySqlParameter p) throws SQLException
	{
		String pName = p.getParameterName();
		switch(p.getDbType())
		{
		case Types.BIGINT:
			p.setValue(cs.getLong(pName));
			break;
		case Types.BIT:
			p.setValue(cs.getBoolean(pName));
			break;
		case Types.BOOLEAN:
			p.setValue(cs.getBoolean(pName));
			break;
		case Types.CHAR:
			p.setValue(cs.getString(pName));
			break;
		case Types.INTEGER:
			p.setValue(cs.getInt(pName));
			break;
		case Types.NVARCHAR:
			p.setValue(cs.getNString(pName));
			break;
		case Types.TIMESTAMP:
			p.setValue(cs.getLong(pName));
			break;
		case Types.TINYINT:
			p.setValue(cs.getByte(pName));
			break;
		case Types.VARCHAR:
			p.setValue(cs.getString(pName));
			break;
		case Types.FLOAT:
			p.setValue(cs.getFloat(pName));
			break;
		case Types.DOUBLE:
			p.setValue(cs.getDouble(pName));
			break;
		}
	}
	
	//附加参数
	private void attachParams(PreparedStatement ps, MySqlParameter[] parameters) throws SQLException
	{
		if(ps == null)
		{
			return;
		}
		if(parameters != null)
		{
			for(int i = 0; i < parameters.length; ++i)
			{
				MySqlParameter p = parameters[i];
				ps.setObject(i + 1, p.getValue());
			}
		}
	}

}
