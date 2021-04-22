package com.oegame.tablegames.model.local;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.oegame.tablegames.common.io.DataInputStreamExt;
import com.oegame.tablegames.common.util.CompressUtil;

public class LocalDataParser
{
    public LocalDataParser(byte[] buffer)
    {
        m_FieldNameDic = new HashMap<String, Integer>();
    	ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStreamExt dis = new DataInputStreamExt(bais);
        try
        {
        	m_Row = dis.readInt();
            m_Column = dis.readInt();
            dis.close();
            bais.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        
        m_GameData = new String[m_Row][m_Column];
        m_FieldName = new String[m_Column];

        for (int i = 0; i < m_Row; i++)
        {
            for (int j = 0; j < m_Column; j++)
            {
                String str = "";
				try
				{
					str = dis.readUTF();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}

                if (i == 0)
                { 
                    //表示读取的是字段
                    String fieldName = str.substring(0,1).toLowerCase() + str.substring(1);
                    m_FieldName[j] = fieldName;
                    m_FieldNameDic.put(fieldName, j);

                }
                else
                {
                    //表示读取的是内容
                    m_GameData[i][j] = str;
                }
            }
        }
    }

    /// <summary>
    /// 行数
    /// </summary>
    private int m_Row;

    /// <summary>
    /// 列数
    /// </summary>
    private int m_Column;

    /// <summary>
    /// 字段名称
    /// </summary>
    private String[] m_FieldName;

    /// <summary>
    /// 字段名称
    /// </summary>
    public String[] getFieldName()
    {
        return m_FieldName;
    }

    /// <summary>
    /// 游戏数据
    /// </summary>
    private String[][] m_GameData;

    /// <summary>
    /// 当前行号
    /// </summary>
    private int m_CurRowNo = 3;

    /// <summary>
    /// 字段名称字典
    /// </summary>
    private Map<String, Integer> m_FieldNameDic;

    /// <summary>
    /// 是否结束
    /// </summary>
    public boolean eof()
    {
    	return m_CurRowNo == m_Row;
    }

    /// <summary>
    /// 转到下一条
    /// </summary>
    public void next()
    {
        if (this.eof()) return;
        m_CurRowNo++;
    }

    /// <summary>
    /// 获取字段值
    /// </summary>
    /// <param name="fieldName"></param>
    /// <returns></returns>
    public String getFieldValue(String fieldName)
    {
        if (m_CurRowNo == 0 || m_CurRowNo >= m_Row) return null;
        return m_GameData[m_CurRowNo][m_FieldNameDic.get(fieldName)];
    }
}
