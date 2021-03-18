//===================================================
//Author      : DRB
//CreateTime  ：2021/3/18 5:43:49
//Description ：
//===================================================

using System;

public static class TimeUtil
{

    private const long TIME_STAMP_FROM_0_TO_1970 = 621355968000000000;

    public static long GetTimestamp()
    {
        return (DateTime.Now.ToUniversalTime().Ticks - TIME_STAMP_FROM_0_TO_1970) / 10000000;
    }

    public static long GetTimestampMS()
    {
        return (DateTime.Now.ToUniversalTime().Ticks - TIME_STAMP_FROM_0_TO_1970) / 10000;
    }

    public static DateTime GetCSharpTime(long timestamp)
    {
        timestamp *= 10000;
        timestamp += TIME_STAMP_FROM_0_TO_1970;
        DateTime dt = new DateTime(timestamp, DateTimeKind.Utc);
        return dt.ToLocalTime();
    }

    public static string GetLocalTime()
    {
        return DateTime.Now.ToString("HH:mm");
    }
}
