package com.oegame.tablegames.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil
{
	
	public static final int DAY_MS = 86400000;
	
	public static long millisecond() 
	{
		return System.currentTimeMillis();
	}
	
	public static long second()
	{
		return millisecond() / 1000;
	}
	
	public static String date() {
		return date("yyyy-MM-dd HH:mm:ss");
	}

	public static String date(String format) {
		return new SimpleDateFormat(format).format(new Date());
	}
	
	public static Date today()
	{
		Date date = new Date();
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		return date;
	}
	
	public static Date current()
	{
		Date date = new Date();
		return date;
	}
	
	public static Date yesterday()
	{
		Date yesterday = new Date(today().getTime() - DAY_MS);
		return yesterday;
	}
}
