package com.oegame.tablegames.model.local;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractDBModel<P extends AbstractEntity>
{
    protected List<P> m_List;
    protected Map<Integer, P> m_Dic;
    public AbstractDBModel()
    {
        m_List = new ArrayList<P>();
        m_Dic = new HashMap<Integer, P>();
        Load();
    }

    protected abstract String getFileName();


    protected void Load()
    {
    	InputStream is = AbstractDBModel.class.getResourceAsStream("/data/" + this.getFileName());
    	ByteArrayOutputStream swapStream = new ByteArrayOutputStream();  
    	byte[] buff = new byte[100];  
    	int rc = 0;  
    	try
		{
			while ((rc = is.read(buff, 0, 100)) > 0) 
			{  
			    swapStream.write(buff, 0, rc);  
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}  
    	byte[] data = swapStream.toByteArray();
        LocalDataParser parse = new LocalDataParser(data);
        while (!parse.eof())
        {
            P p = makeEntity(parse);
            m_List.add(p);
            m_Dic.put(p.id, p);
            parse.next();
        }
    }
    public List<P> getList()
    {
        return m_List;
    }
    public P get(int id)
    {
        if (m_Dic.containsKey(id))
        {
            return m_Dic.get(id);
        }
        return null;
    }
    protected abstract P makeEntity(LocalDataParser parse);

	
}
