//===================================================
//Author      : DRB
//CreateTime  ：2021/4/27 23:35:21
//Description ：
//===================================================
using UnityEngine;
using DrbFramework.Extensions;

public class SingletonMono<T> : MonoBehaviour where T : MonoBehaviour
{
    private static T instance;

    public static T Instance
    {
        get
        {
            if (instance == null)
            {
                GameObject obj = new GameObject(typeof(T).Name);
                DontDestroyOnLoad(obj);
                instance = obj.GetOrCreatComponent<T>();
            }
            return instance;
        }
    }

    public static void CreateInstance()
    {
        if (instance == null)
        {
            GameObject obj = new GameObject(typeof(T).Name);
            DontDestroyOnLoad(obj);
            instance = obj.GetOrCreatComponent<T>();
        }
    }

    void Awake()
    {
        OnAwake();
    }

    void Start()
    {
        OnStart();
    }

    void OnDestroy()
    {
        BeforeOnDestroy();
    }

    protected virtual void OnAwake() { }
    protected virtual void OnStart() { }
    protected virtual void BeforeOnDestroy() { }
}
