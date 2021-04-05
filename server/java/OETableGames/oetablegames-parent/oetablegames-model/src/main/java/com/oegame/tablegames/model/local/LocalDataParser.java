package com.oegame.tablegames.model.local;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.oegame.tablegames.common.io.DataInputStreamExt;
import com.oegame.tablegames.common.util.CompressUtil;

public class LocalDataParser
{
    
    // 异或因子
    private byte[] xorScale = new byte[] {
    		(byte)45, (byte)66, (byte)38, (byte)55, (byte)23, (byte)254, (byte)9, (byte)165, (byte)90, (byte)19, (byte)41, (byte)45, 
    		(byte)201, (byte)58, (byte)55, (byte)37, (byte)254, (byte)185, (byte)165, (byte)169, (byte)19, (byte)171 };

    
    
    public LocalDataParser(byte[] buffer)
    {
        m_FieldNameDic = new HashMap<String, Integer>();
        buffer = CompressUtil.zlibDecompress(buffer);
        int iScaleLen = xorScale.length;
        for (int i = 0; i < buffer.length; i++)
        {
            buffer[i] = (byte)(buffer[i] ^ xorScale[i % iScaleLen]);
        }
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

                if (i == 2)
                { 
                    //表示读取的是字段
                    m_FieldName[j] = str;
                    m_FieldNameDic.put(str, j);

                }
                else if (i > 2)
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
        if (m_CurRowNo < 3 || m_CurRowNo >= m_Row) return null;
        return m_GameData[m_CurRowNo][m_FieldNameDic.get(fieldName)];
    }
}
