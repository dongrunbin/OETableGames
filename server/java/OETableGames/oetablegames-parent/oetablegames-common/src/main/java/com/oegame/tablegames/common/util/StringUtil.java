package com.oegame.tablegames.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil 
{
	public static int toInt(String str)
	{
		int ret;
		try
		{
			ret = Integer.parseInt(str);
		}
		catch(Exception e)
		{
			ret = 0;
		}
		
		return ret;
	}
	
	public static long toLong(String str)
	{
		long ret;
		try
		{
			ret = Long.parseLong(str);
		}
		catch(Exception e)
		{
			ret = 0;
		}
		
		return ret;
	}
	
	public static float toFloat(String str)
	{
		float ret;
		try
		{
			ret = Float.parseFloat(str);
		}
		catch(Exception e)
		{
			ret = 0;
		}
		return ret;
	}
	
	public static boolean toBoolean(String str)
	{
		boolean ret;
		try
		{
			ret = Boolean.parseBoolean(str);
			if(!ret)
			{
				if(str.equals("1"))
				{
					ret = true;
				}
			}
		}
		catch(Exception e)
		{
			ret = false;
		}
		return ret;
	}
	
	public static byte[] intToByteArray(int i)
	{
		byte[] targets = new byte[4];

		targets[3] = (byte) (i & 0xff);
		targets[2] = (byte) ((i >> 8) & 0xff);
		targets[1] = (byte) ((i >> 16) & 0xff);
		targets[0] = (byte) (i >>> 24);
		return targets; 
	}
	
	public static int byteArrayToInt(byte[] b) 
	{  
		return   b[3] & 0xFF |  
		         (b[2] & 0xFF) << 8 |  
		         (b[1] & 0xFF) << 16 |  
		         (b[0] & 0xFF) << 24;  
	} 
	
	public static byte[] longToByteArray(long l)
	{
		byte[] buffer = new byte[8];
		for (int i = 0; i < 8; i++) {
		int offset = 64 - (i + 1) * 8;
		buffer[i] = (byte) ((l >> offset) & 0xff);
		}
		return buffer;
	}
	
	public static long byteArrayToLong(byte[] b)
	{
		long values = 0;
		for (int i = 0; i < 8; i++)
		{
			values <<= 8; 
			values|= (b[i] & 0xff);
		}
		return values;
	}
	
	public static boolean regex(String regex, String str)
	{
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		return m.matches();
	}
	
    public static int getStrLength(String value) 
    {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        for (int i = 0; i < value.length(); i++) 
        {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) 
            {
                valueLength += 2;
            } 
            else 
            {
                valueLength += 1;
            }
        }
        return valueLength;
    }
    
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}
    
	public static long ip2long(String ipaddr) 
	{
		long longip = 0;

		try 
		{
			long[] ip = new long[4];
			int position1 = ipaddr.indexOf(".");
			int position2 = ipaddr.indexOf(".", position1 + 1);
			int position3 = ipaddr.indexOf(".", position2 + 1);

			if (position1 != -1 && position2 != -1 && position3 != -1) 
			{
				String s1 = ipaddr.substring(0, position1);
				String s2 = ipaddr.substring(position1 + 1, position2);
				String s3 = ipaddr.substring(position2 + 1, position3);
				String s4 = ipaddr.substring(position3 + 1);

				if (isNumeric(s1) && isNumeric(s2) && isNumeric(s3) && isNumeric(s4)) 
				{
					ip[0] = Integer.parseInt(s1);
					ip[1] = Integer.parseInt(s2);
					ip[2] = Integer.parseInt(s3);
					ip[3] = Integer.parseInt(s4);
					longip = (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
				}
			}
		} 
		catch (NumberFormatException e) 
		{
			e.printStackTrace();
		}

		return longip;

	}

	// 将十进制整数形式转换成127.0.0.1形式的ip地址
	public static String long2ip(long longIp)
	{
		StringBuffer sb = new StringBuffer("");
		sb.append(String.valueOf((longIp >>> 24)));
		sb.append(".");
		sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
		sb.append(".");
		sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
		sb.append(".");
		sb.append(String.valueOf((longIp & 0x000000FF)));
		return sb.toString();
	}
	
	public static boolean isBlank(final CharSequence cs) 
	{
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) 
        {
            return true;
        }
        for (int i = 0; i < strLen; i++) 
        {
            if (Character.isWhitespace(cs.charAt(i)) == false) 
            {
                return false;
            }
        }
        return true;
    }
}
