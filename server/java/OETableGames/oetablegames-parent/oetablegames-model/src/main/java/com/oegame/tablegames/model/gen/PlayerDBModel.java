package com.oegame.tablegames.model.gen;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import com.oegame.tablegames.model.*;

public class PlayerDBModel extends AbstractMySqlDBModel<PlayerEntity>
{
    private PlayerDBModel()
    {

    }

    private static volatile PlayerDBModel instance = null;
    public static PlayerDBModel getInstance()
    {
        if(instance == null)
        {
            synchronized (PlayerDBModel.class)
            {
                if (instance == null)
                {
                    instance = new PlayerDBModel();
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
        return "player";
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
            _columnList.add("nickname");
            _columnList.add("gender");
            _columnList.add("cards");
            _columnList.add("roomId");
            _columnList.add("gameId");
            _columnList.add("matchId");
            _columnList.add("status");
            _columnList.add("online");
            _columnList.add("ipaddr");
            _columnList.add("gold");
        }
        return _columnList;
    }

    // 转换参数
    @Override
    protected MySqlParameter[] valueParas(PlayerEntity entity)
    {
        MySqlParameter[] parameters = new MySqlParameter[] {
                new MySqlParameter("id", entity.id, Types.BIGINT, 20),
                new MySqlParameter("nickname", entity.nickname, Types.VARCHAR, 32),
                new MySqlParameter("gender", entity.gender, Types.TINYINT, 3),
                new MySqlParameter("cards", entity.cards, Types.INTEGER, 11),
                new MySqlParameter("roomId", entity.roomId, Types.INTEGER, 11),
                new MySqlParameter("gameId", entity.gameId, Types.INTEGER, 11),
                new MySqlParameter("matchId", entity.matchId, Types.INTEGER, 11),
                new MySqlParameter("status", entity.status, Types.TINYINT, 3),
                new MySqlParameter("online", entity.online, Types.BIGINT, 10),
                new MySqlParameter("ipaddr", entity.ipaddr, Types.BIGINT, 10),
                new MySqlParameter("gold", entity.gold, Types.INTEGER, 11),
                new MySqlParameter("retValue", Types.TINYINT, 11)
            };
        return parameters;
    }

    // 封装对象
    @Override
    protected PlayerEntity makeEntity(HashMap<String,Object> map)
    {
        PlayerEntity entity = new PlayerEntity();
        entity.id = (long)map.get("id");
        entity.nickname = (String)map.get("nickname");
        entity.gender = (byte)map.get("gender");
        entity.cards = (int)map.get("cards");
        entity.roomId = (int)map.get("roomId");
        entity.gameId = (int)map.get("gameId");
        entity.matchId = (int)map.get("matchId");
        entity.status = (byte)map.get("status");
        entity.online = (long)map.get("online");
        entity.ipaddr = (long)map.get("ipaddr");
        entity.gold = (int)map.get("gold");
        return entity;
   }

}
