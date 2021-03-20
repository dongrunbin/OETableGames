//===================================================
//Author      : DRB
//CreateTime  ：2021/3/19 19:49:37
//Description ：
//===================================================
using UnityEngine;

public static class DeviceUtil
{

    public static string GetBundleIdentifier()
    {
        return Application.identifier;
    }

    public static RuntimePlatform GetPlatform()
    {
        return Application.platform;
    }

    public static int GetOssPlatform()
    {
        int ret = 0;
        RuntimePlatform platform = GetPlatform();
        switch (platform)
        {
            case RuntimePlatform.Android:
                ret = 3;
                break;
            case RuntimePlatform.IPhonePlayer:
                ret = 4;
                break;
        }

        return ret;
    }
}
