package com.oegame.tablegames.main;

import java.util.ArrayList;
import com.zhenyi.remoting.framework.context.ApplicationContext;

public class App 
{
	static ApplicationContext refContex;

    public static void main(String[] args) throws Exception
    {

    	if(args == null || args.length == 0)
    	{
    		throw new Exception("service count not can be 0");
    	}
    	
    	ArrayList<String> reference = new ArrayList<String>();
    	ArrayList<String> service = new ArrayList<String>();
    	for(int i = 0; i < args.length; ++i)
    	{
    		if(args[i].indexOf("reference") > -1)
    		{
    			reference.add(args[i]);
    		}
    		else if(args[i].indexOf("service") > -1)
    		{
    			service.add(args[i]);
    		}
    	}
    	String[] ref = new String[reference.size()];
    	ref = (String[])reference.toArray(ref);
    	String[] ser = new String[service.size()];
    	ser = (String[])service.toArray(ser);
    	ApplicationContext context = new ApplicationContext(ser);
    	System.out.println("all of services started");
		refContex = new ApplicationContext(ref);
    	System.out.println("all of services referenced");
    }
}
