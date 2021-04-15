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
#if UNITY_EDITOR
        return resourceSystem.LoadAsset<T>(assetPath, LoadMode.Editor);
#else
        string assetName = Path.GetFileNameWithoutExtension(assetPath);
        return resourceSystem.LoadAssetFromAssetBundle<T>(assetPath, assetName, LoadMode.Persistent);
#endif
    }

    public static void LoadAssetAsync(this ResourceSystem resourceSystem, string assetPath, LoadAssetCompleteEventHandler onComplete, object userData)
    {
#if UNITY_EDITOR
        resourceSystem.LoadAssetAsync(assetPath, LoadMode.Editor, onComplete, userData);
#else
        string assetName = Path.GetFileNameWithoutExtension(assetPath);
        resourceSystem.LoadAssetFromAssetBundleAsync(assetPath, assetName, LoadMode.Persistent, onComplete, userData);
#endif
    }

    public static Sprite LoadSprite(this ResourceSystem resourceSystem, string spritePath, string spriteName)
    {
#if UNITY_EDITOR
        Texture2D tex = resourceSystem.LoadAsset<Texture2D>(spritePath + ".png", LoadMode.Editor);
        Rect iconRect;
        try
        {
            iconRect = new Rect(0, 0, tex.width, tex.height);
        }
        catch
        {
            return null;
        }
        Sprite iconSprite = Sprite.Create(tex, iconRect, new Vector2(0.5f, 0.5f));
        return iconSprite;
#else
            return null;
#endif
    }
}