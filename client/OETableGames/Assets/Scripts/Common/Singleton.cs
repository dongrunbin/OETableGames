using System;

public class Singleton<T> : IDisposable where T : new()
{
    private static T instance;

    public static T Instance
    {
        get
        {
            if (instance == null)
            {
                instance = new T();
            }
            return instance;
        }
    }

    public static void CreateInstance()
    {
        if (instance == null)
        {
            instance = new T();
        }
    }

    public static T GetInstance()
    {
        if (instance == null)
        {
            instance = new T();
        }
        return instance;
    }

    public virtual void Dispose()
    {

    }
}