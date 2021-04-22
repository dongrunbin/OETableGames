package com.oegame.tablegames.model.gen;

import com.oegame.tablegames.model.AbstractMySqlDBModel;
import com.oegame.tablegames.model.MySqlConn;
import com.oegame.tablegames.model.MySqlParameter;
import com.oegame.tablegames.model.gen.PassportEntity;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;


/// <summary>
/// DBModel
/// </summary>
public class PassportDBModel extends AbstractMySqlDBModel<PassportEntity>
{
    private PassportDBModel()
    {

    }

    private static volatile PassportDBModel instance = null;
    public static PassportDBModel getInstance()
    {
        if(instance == null)
        {
            synchronized (PassportDBModel.class)
            {
                if (instance == null)
                {
                    instance = new PassportDBModel();
                }
            }
        }
        return instance;
    }


    // 数据库连接字符串
    @Override
    protected String getConnectionString()
    {
        return MySqlConn.passport;
    }

    // 表名
    @Override
    protected String getTableName()
    {
        return "passport";
    }

    private ArrayList<String> _columnList = null;
    // 列名集合
    @Override
    protected ArrayList<String> getColumnList()
    {
        if (_columnList == null)
        {
            _columnList = new ArrayList<String>();
            _columnList.add("id");
            _columnList.add("passport");
            _columnList.add("password");
            _columnList.add("token");
            _columnList.add("tokenExpire");
            _columnList.add("access_token");
            _columnList.add("refresh_token");
            _columnList.add("status");
            _columnList.add("reg_time");
            _columnList.add("reg_ip");
            _columnList.add("log_time");
            _columnList.add("log_ip");
            _columnList.add("log_count");
            _columnList.add("device");
        }
        return _columnList;
    }

    // 转换参数
    @Override
    protected MySqlParameter[] valueParas(PassportEntity entity)
    {
        MySqlParameter[] parameters = new MySqlParameter[] {
                new MySqlParameter("id", entity.id, Types.BIGINT, 20),
                new MySqlParameter("passport", entity.passport, Types.VARCHAR, 64),
                new MySqlParameter("password", entity.password, Types.VARCHAR, 32),
                new MySqlParameter("token", entity.token, Types.VARCHAR, 255),
                new MySqlParameter("tokenExpire", entity.tokenExpire, Types.BIGINT, 10),
                new MySqlParameter("status", entity.status, Types.TINYINT, 3),
                new MySqlParameter("reg_time", entity.reg_time, Types.BIGINT, 10),
                new MySqlParameter("reg_ip", entity.reg_ip, Types.BIGINT, 10),
                new MySqlParameter("log_time", entity.log_time, Types.BIGINT, 10),
                new MySqlParameter("log_ip", entity.log_ip, Types.BIGINT, 10),
                new MySqlParameter("log_count", entity.log_count, Types.INTEGER, 11),
                new MySqlParameter("device", entity.device, Types.INTEGER, 11),
                new MySqlParameter("retValue", Types.TINYINT, 11)
            };
        return parameters;
    }

    // 封装对象
    @Override
    protected PassportEntity makeEntity(HashMap<String,Object> map)
    {
        PassportEntity entity = new PassportEntity();
        entity.id = (long)map.get("id");
        entity.passport = (String)map.get("passport");
        entity.password = (String)map.get("password");
        entity.token = (String)map.get("token");
        entity.tokenExpire = (long)map.get("tokenExpire");
        entity.status = (byte)map.get("status");
        entity.reg_time = (long)map.get("reg_time");
        entity.reg_ip = (long)map.get("reg_ip");
        entity.log_time = (long)map.get("log_time");
        entity.log_ip = (long)map.get("log_ip");
        entity.log_count = (int)map.get("log_count");
        entity.device = (int)map.get("device");
        return entity;
   }

}
