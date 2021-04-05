package com.oegame.tablegames.common.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class DeviceUtil
{
	public static String getIp()
	{
		
		String ip = "127.0.0.1";
		try
		{
			InetAddress addr = InetAddress.getLocalHost();
			ip= addr.getHostAddress().toString();
		} 
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}  
        
         return ip;
	}
}
