using UnityEngine;
using System.Collections;

public static class InstanceHandler
{
    public delegate GameObject InstantiateDelegate(GameObject prefab, Vector3 pos, Quaternion rot);
    public delegate void DestroyDelegate(GameObject instance);

    /// <summary>
    /// 实例化委托
    /// </summary>
    public static InstantiateDelegate InstantiateDelegates;

    /// <summary>
    /// 删除委托
    /// </summary>
    public static DestroyDelegate DestroyDelegates;

    /// <summary>
    /// 实例化预制体
    /// </summary>
    /// <param name="prefab"></param>
    /// <param name="pos"></param>
    /// <param name="rot"></param>
    /// <returns></returns>
    internal static GameObject InstantiatePrefab(GameObject prefab, Vector3 pos, Quaternion rot)
    {
        if (InstantiateDelegates != null)
        {
            return InstantiateDelegates(prefab, pos, rot);
        }
        else
        {
            return Object.Instantiate(prefab, pos, rot) as GameObject;
        }
    }


    /// <summary>
    /// 删除实例
    /// </summary>
    /// <param name="instance"></param>
    internal static void DestroyInstance(GameObject instance)
    {
        if (DestroyDelegates != null)
        {
            DestroyDelegates(instance);
        }
        else
        {
            Object.Destroy(instance);
        }
    }
}
