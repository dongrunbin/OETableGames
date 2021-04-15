using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class PrefabsDict : IDictionary<string, Transform>
{
    #region Public Custom Memebers
    /// <summary>
    /// 返回所有预制体名称
    /// </summary>
    /// <returns></returns>
    public override string ToString()
    {
        string[] keysArray = new string[m_PrefabsDic.Count];
        m_PrefabsDic.Keys.CopyTo(keysArray, 0);
        return string.Format("[{0}]", string.Join(", ", keysArray));
    }
    #endregion Public Custom Memebers


    #region Internal Dict Functionality
    internal void _Add(string prefabName, Transform prefab)
    {
        m_PrefabsDic.Add(prefabName, prefab);
    }

    internal bool _Remove(string prefabName)
    {
        return m_PrefabsDic.Remove(prefabName);
    }

    internal void _Clear()
    {
        m_PrefabsDic.Clear();
    }
    #endregion Internal Dict Functionality


    #region Dict Functionality
    /// <summary>
    /// 预制体字典
    /// </summary>
    private Dictionary<string, Transform> m_PrefabsDic = new Dictionary<string, Transform>();

    /// <summary>
    /// 返回预制体数量
    /// </summary>
    public int Count { get { return m_PrefabsDic.Count; } }

    /// <summary>
    /// 是否包含预制体
    /// </summary>
    /// <param name="prefabName"></param>
    /// <returns></returns>
    public bool ContainsKey(string prefabName)
    {
        return m_PrefabsDic.ContainsKey(prefabName);
    }

    /// <summary>
    /// 尝试获取预制体
    /// </summary>
    /// <param name="key"></param>
    /// <param name="value"></param>
    /// <returns></returns>
    public bool TryGetValue(string prefabName, out Transform prefab)
    {
        return m_PrefabsDic.TryGetValue(prefabName, out prefab);
    }

    #region Not Implimented

    public void Add(string key, Transform value)
    {
        throw new System.NotImplementedException("未实现该方法");
    }

    public bool Remove(string prefabName)
    {
        throw new System.NotImplementedException("未实现该方法");
    }

    public bool Contains(KeyValuePair<string, Transform> item)
    {
        throw new System.NotImplementedException("未实现该方法");
    }

    public Transform this[string key]
    {
        get
        {
            Transform prefab;
            try
            {
                prefab = m_PrefabsDic[key];
            }
            catch (KeyNotFoundException)
            {
                throw new System.NotImplementedException(string.Format("不存在该预制体:{0}", key));
            }
            return prefab;
        }
        set
        {
            throw new System.NotImplementedException("未实现该方法");
        }
    }

    public ICollection<string> Keys
    {
        get
        {
            return m_PrefabsDic.Keys;
        }
    }


    public ICollection<Transform> Values
    {
        get
        {
            return m_PrefabsDic.Values;
        }
    }


    #region ICollection<KeyValuePair<string, Transform>> Members
    private bool IsReadOnly { get { return true; } }
    bool ICollection<KeyValuePair<string, Transform>>.IsReadOnly { get { return true; } }

    public void Add(KeyValuePair<string, Transform> item)
    {
        throw new System.NotImplementedException("未实现该方法");
    }

    public void Clear() { throw new System.NotImplementedException(); }

    private void CopyTo(KeyValuePair<string, Transform>[] array, int arrayIndex)
    {
        throw new System.NotImplementedException("未实现该方法");
    }

    void ICollection<KeyValuePair<string, Transform>>.CopyTo(KeyValuePair<string, Transform>[] array, int arrayIndex)
    {
        throw new System.NotImplementedException("未实现该方法");
    }

    public bool Remove(KeyValuePair<string, Transform> item)
    {
        throw new System.NotImplementedException("未实现该方法");
    }
    #endregion ICollection<KeyValuePair<string, Transform>> Members
    #endregion Not Implimented




    #region IEnumerable<KeyValuePair<string, Transform>> Members
    public IEnumerator<KeyValuePair<string, Transform>> GetEnumerator()
    {
        return m_PrefabsDic.GetEnumerator();
    }
    #endregion



    #region IEnumerable Members
    IEnumerator IEnumerable.GetEnumerator()
    {
        return m_PrefabsDic.GetEnumerator();
    }
    #endregion

    #endregion Dict Functionality
}
