using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System;

public class SpawnPoolsDict : IDictionary<string, SpawnPool>
{
    #region Event Handling
    public delegate void OnCreatedDelegate(SpawnPool pool);

    /// <summary>
    /// 创建时委托字典
    /// </summary>
    internal Dictionary<string, OnCreatedDelegate> onCreatedDelegates = new Dictionary<string, OnCreatedDelegate>();

    /// <summary>
    /// 添加创建时委托
    /// </summary>
    /// <param name="poolName"></param>
    /// <param name="createdDelegate"></param>
    public void AddOnCreatedDelegate(string poolName, OnCreatedDelegate createdDelegate)
    {
        // Assign first delegate "just in time"
        if (!this.onCreatedDelegates.ContainsKey(poolName))
        {
            this.onCreatedDelegates.Add(poolName, createdDelegate);
            return;
        }

        this.onCreatedDelegates[poolName] += createdDelegate;
    }

    /// <summary>
    /// 移除创建时委托
    /// </summary>
    /// <param name="poolName"></param>
    /// <param name="createdDelegate"></param>
    public void RemoveOnCreatedDelegate(string poolName, OnCreatedDelegate createdDelegate)
    {
        if (!this.onCreatedDelegates.ContainsKey(poolName))
            throw new KeyNotFoundException
            (
                "No OnCreatedDelegates found for pool name '" + poolName + "'."
            );

        this.onCreatedDelegates[poolName] -= createdDelegate;
    }

    #endregion Event Handling

    #region Public Custom Memebers
    /// <summary>
    /// 创建池
    /// </summary>
    /// <param name="poolName">池名字</param>
    /// <returns></returns>
    public SpawnPool Create(string poolName)
    {
        GameObject owner = new GameObject(poolName);
        return owner.AddComponent<SpawnPool>();
    }

    /// <summary>
    /// 创建池
    /// </summary>
    /// <param name="poolName">池名字</param>
    /// <param name="owner">池对象</param>
    /// <returns></returns>
    public SpawnPool Create(string poolName, GameObject owner)
    {
        if (!this.assertValidPoolName(poolName)) return null;

        string ownerName = owner.gameObject.name;

        try
        {
            owner.gameObject.name = poolName;
            return owner.AddComponent<SpawnPool>();
        }
        finally
        {
            owner.gameObject.name = ownerName;
        }
    }


    /// <summary>
    /// 确保池名字是否有效
    /// </summary>
    /// <param name="poolName">池名字</param>
    /// <returns>是否有效</returns>
    private bool assertValidPoolName(string poolName)
    {
        if (this.ContainsKey(poolName))
        {
            return false;
        }
        return true;
    }


    /// <summary>
    /// 返回所有池的名字
    /// </summary>
    /// <returns></returns>
    public override string ToString()
    {
        string[] keysArray = new string[this.m_PoolDic.Count];
        this.m_PoolDic.Keys.CopyTo(keysArray, 0);
        return string.Format("[{0}]", String.Join(", ", keysArray));
    }



    /// <summary>
    /// 删除池
    /// </summary>
    /// <param name="poolName">池名字</param>
    public bool Destroy(string poolName)
    {
        SpawnPool spawnPool;
        if (!m_PoolDic.TryGetValue(poolName, out spawnPool))
        {
            return false;
        }

        UnityEngine.Object.Destroy(spawnPool.gameObject);
        return true;
    }

    /// <summary>
    /// 删除所有池
    /// </summary>
    public void DestroyAll()
    {
        foreach (KeyValuePair<string, SpawnPool> pair in m_PoolDic)
            UnityEngine.Object.Destroy(pair.Value);
        m_PoolDic.Clear();
    }
    #endregion Public Custom Memebers

    #region Dict Functionality
    /// <summary>
    /// 所有池字典
    /// </summary>
    private Dictionary<string, SpawnPool> m_PoolDic = new Dictionary<string, SpawnPool>();

    /// <summary>
    /// 添加池
    /// </summary>
    /// <param name="spawnPool">要加入的池</param>
    internal void Add(SpawnPool spawnPool)
    {
        if (this.ContainsKey(spawnPool.PoolName))
        {
            Debug.LogError(string.Format("{0}已经存在", spawnPool.PoolName));
            return;
        }

        m_PoolDic.Add(spawnPool.PoolName, spawnPool);

        if (onCreatedDelegates.ContainsKey(spawnPool.PoolName))
            onCreatedDelegates[spawnPool.PoolName](spawnPool);
    }

    /// <summary>
    /// 未实现IDictionary这个方法
    /// </summary>
    /// <param name="key"></param>
    /// <param name="value"></param>
    public void Add(string key, SpawnPool value)
    {
        throw new NotImplementedException();
    }


    /// <summary>
    /// 移除池
    /// </summary>
    /// <param name="spawnPool"></param>
    internal bool Remove(SpawnPool spawnPool)
    {
        if (!this.ContainsKey(spawnPool.PoolName) & Application.isPlaying)
        {
            Debug.LogError(string.Format("{0}池不存在", spawnPool.PoolName));
            return false;
        }

        m_PoolDic.Remove(spawnPool.PoolName);
        return true;
    }

    /// <summary>
    /// 未实现IDictionary这个方法
    /// </summary>
    /// <param name="poolName"></param>
    /// <returns></returns>
    public bool Remove(string poolName)
    {
        throw new NotImplementedException();
    }

    /// <summary>
    /// 池总数
    /// </summary>
    public int Count { get { return m_PoolDic.Count; } }

    /// <summary>
    /// 是否存在池
    /// </summary>
    /// <param name="poolName">池名字</param>
    /// <returns></returns>
    public bool ContainsKey(string poolName)
    {
        return this.m_PoolDic.ContainsKey(poolName);
    }

    /// <summary>
    /// 尝试获取池
    /// </summary>
    /// <param name="key">池名字</param>
    /// <param name="value">池对象</param>
    /// <returns></returns>
    public bool TryGetValue(string poolName, out SpawnPool spawnPool)
    {
        return this.m_PoolDic.TryGetValue(poolName, out spawnPool);
    }

    #region Not Implimented
    /// <summary>
    /// 未实现IDictionary这个方法
    /// </summary>
    /// <param name="item"></param>
    /// <returns></returns>
    public bool Contains(KeyValuePair<string, SpawnPool> item)
    {
        throw new NotImplementedException();
    }

    public SpawnPool this[string key]
    {
        get
        {
            SpawnPool pool;
            try
            {
                pool = m_PoolDic[key];
            }
            catch (KeyNotFoundException)
            {

                throw new KeyNotFoundException(string.Format("{0}池不存在", key));
            }
            return pool;
        }
        set
        {
            throw new NotImplementedException();
        }
    }

    public ICollection<string> Keys
    {
        get
        {
            throw new NotImplementedException();
        }
    }


    public ICollection<SpawnPool> Values
    {
        get
        {
            throw new NotImplementedException();
        }
    }


    #region ICollection<KeyValuePair<string,SpawnPool>> Members
    private bool IsReadOnly { get { return true; } }
    bool ICollection<KeyValuePair<string, SpawnPool>>.IsReadOnly { get { return true; } }

    public void Add(KeyValuePair<string, SpawnPool> item)
    {
        throw new NotImplementedException();
    }

    public void Clear()
    {
        throw new NotImplementedException();

    }

    private void CopyTo(KeyValuePair<string, SpawnPool>[] array, int arrayIndex)
    {
        throw new NotImplementedException();
    }

    void ICollection<KeyValuePair<string, SpawnPool>>.CopyTo(KeyValuePair<string, SpawnPool>[] array, int arrayIndex)
    {
        throw new NotImplementedException();
    }

    public bool Remove(KeyValuePair<string, SpawnPool> item)
    {
        throw new NotImplementedException();
    }
    #endregion ICollection<KeyValuePair<string, SpawnPool>> Members
    #endregion Not Implimented

    #region IEnumerable<KeyValuePair<string,SpawnPool>> Members
    public IEnumerator<KeyValuePair<string, SpawnPool>> GetEnumerator()
    {
        return m_PoolDic.GetEnumerator();
    }
    #endregion

    #region IEnumerable Members
    IEnumerator IEnumerable.GetEnumerator()
    {
        return m_PoolDic.GetEnumerator();
    }
    #endregion

    #endregion Dict Functionality

}
