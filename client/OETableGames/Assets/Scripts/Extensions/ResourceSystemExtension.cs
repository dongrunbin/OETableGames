//===================================================
//Author      : DRB
//CreateTime  ：2021/2/25 13:08:42
//Description ：
//===================================================
using DrbFramework.Resource;
using System.IO;
using UnityEngine;

[XLua.LuaCallCSharp]
public static class ResourceSystemExtensions
{
    public static T LoadAsset<T>(this ResourceSystem resourceSystem, string assetPath) where T : class
    {
#if !ASSETBUNDLE
        return resourceSystem.LoadAsset<T>(assetPath, LoadMode.Editor);
#else
        string assetName = Path.GetFileNameWithoutExtension(assetPath);
        return resourceSystem.LoadAssetFromAssetBundle<T>(assetPath, assetName, LoadMode.Persistent);
#endif
    }

    public static void LoadAssetAsync(this ResourceSystem resourceSystem, string assetPath, LoadAssetCompleteEventHandler onComplete, object userData)
    {
#if !ASSETBUNDLE
        resourceSystem.LoadAssetAsync(assetPath, LoadMode.Editor, onComplete, userData);
#else
        string assetName = Path.GetFileNameWithoutExtension(assetPath);
        resourceSystem.LoadAssetFromAssetBundleAsync(assetPath, assetName, LoadMode.Persistent, onComplete, userData);
#endif
    }
}