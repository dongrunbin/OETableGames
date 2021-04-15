using UnityEngine;
using System.Collections;
using System.Collections.Generic;

[System.Serializable]
public class PrefabPool
{

    #region Public Properties Available in the Editor
    /// <summary>
    /// 预制体
    /// </summary>
    public Transform Prefab;

    /// <summary>
    /// 预制体
    /// </summary>
    internal GameObject PrefabGO;

    /// <summary>
    /// 预加载数量
    /// </summary>
    public int PreloadAmount = 1;

    /// <summary>
    /// 是否延迟预加载
    /// </summary>
    public bool IsPreloadTime = false;

    /// <summary>
    /// 预加载帧数
    /// </summary>
    public int PreloadFrames = 2;

    /// <summary>
    /// 预加载延迟秒数
    /// </summary>
    public float PreloadDelay = 0;

    /// <summary>
    /// 是否限制实例数量
    /// </summary>
    public bool IsLimitInstances = false;

    /// <summary>
    /// 限制实例数量
    /// </summary>
    public int LimitAmount = 100;

    /// <summary>
    /// 是否先进先出
    /// </summary>
    public bool IsLimitFIFO = false;

    /// <summary>
    /// 是否开启缓存池自动清理
    /// </summary>
    public bool IsCullDespawned = false;

    /// <summary>
    /// 缓存池保留数量
    /// </summary>
    public int CullAbove = 50;

    /// <summary>
    /// 多长时间清理一次
    /// </summary>
    public int CullDelay = 60;

    /// <summary>
    /// 每次清理数量
    /// </summary>
    public int CullMaxPerPass = 5;

    /// <summary>
    /// 是否打印信息
    /// </summary>
    internal bool m_LogMessages = false;
    public bool logMessages
    {
        get
        {
            if (m_isForceLoggingSilent) return false;

            if (SpawnPool.m_LogMessages)
                return SpawnPool.m_LogMessages;
            else
                return m_LogMessages;
        }
    }

    /// <summary>
    /// 这个开启的时候，不打印
    /// </summary>
    private bool m_isForceLoggingSilent = false;


    /// <summary>
    /// 生产池
    /// </summary>
    public SpawnPool SpawnPool;
    #endregion Public Properties Available in the Editor


    #region Constructor and Self-Destruction
    /// <summary>
    /// 构造函数
    /// </summary>
    /// <param name="prefab"></param>
    public PrefabPool(Transform prefab)
    {
        Prefab = prefab;
        PrefabGO = prefab.gameObject;
    }

    /// <description>
    ///	构造函数(UnityEditor序列化使)
    /// </description>
    public PrefabPool() { }

    /// <summary>
    /// 构造
    /// </summary>
    internal void inspectorInstanceConstructor()
    {
        PrefabGO = Prefab.gameObject;
        m_Spawned = new List<Transform>();
        m_Despawned = new List<Transform>();
    }


    /// <summary>
    /// 销毁
    /// </summary>
    internal void SelfDestruct()
    {
        Prefab = null;
        PrefabGO = null;
        SpawnPool = null;

        for (int i = 0; i < m_Despawned.Count; ++i)
        {
            if (m_Despawned[i] != null && SpawnPool != null)
            {
                SpawnPool.DestroyInstance(m_Despawned[i].gameObject);
            }
        }
        for (int i = 0; i < m_Spawned.Count; ++i)
        {
            if (m_Spawned[i] != null && SpawnPool != null)
            {
                SpawnPool.DestroyInstance(m_Spawned[i].gameObject);
            }
        }
        m_Spawned.Clear();
        m_Despawned.Clear();
    }
    #endregion Constructor and Self-Destruction


    #region Pool Functionality
    /// <summary>
    /// 缓存池正在清理
    /// </summary>
    private bool m_isCullingActive = false;


    /// <summary>
    /// 正在显示的所有对象
    /// </summary>
    internal List<Transform> m_Spawned = new List<Transform>();
    public List<Transform> Spawned { get { return new List<Transform>(m_Spawned); } }

    /// <summary>
    /// 未显示的所有对象
    /// </summary>
    internal List<Transform> m_Despawned = new List<Transform>();
    public List<Transform> Despawned { get { return new List<Transform>(m_Despawned); } }


    /// <summary>
    /// 总数量（显示的+未显示的）
    /// </summary>
    public int totalCount
    {
        get
        {
            return m_Spawned.Count + m_Despawned.Count;
        }
    }


    /// <summary>
    /// 是否预加载完成
    /// </summary>
    private bool m_isPreloaded = false;
    internal bool Preloaded
    {
        get { return m_isPreloaded; }
        private set { m_isPreloaded = value; }
    }

    /// <summary>
    /// 隐藏实例
    /// </summary>
    /// <param name="xform"></param>
    /// <returns></returns>
    internal bool DespawnInstance(Transform xform)
    {
        return DespawnInstance(xform, true);
    }

    /// <summary>
    /// 隐藏实例
    /// </summary>
    /// <param name="xform">对象</param>
    /// <param name="isSendEventMessage">是否发送事件消息</param>
    /// <returns></returns>
    internal bool DespawnInstance(Transform xform, bool isSendEventMessage)
    {
        if (logMessages)
            Debug.Log(string.Format("{0}消失", xform.name));

        if (xform == null)
        {
            m_Spawned.Remove(xform);
            return false;
        }
        xform.SetParent(SpawnPool.Group, xform is RectTransform);
        m_Spawned.Remove(xform);
        m_Despawned.Add(xform);

        if (isSendEventMessage)
            xform.gameObject.BroadcastMessage("OnDespawned", SpawnPool, SendMessageOptions.DontRequireReceiver);

        xform.gameObject.SetActive(false);

        if (!m_isCullingActive && IsCullDespawned && totalCount > CullAbove)
        {
            m_isCullingActive = true;
            SpawnPool.StartCoroutine(CullDespawned());
        }
        return true;
    }



    /// <summary>
    /// 清理缓存池
    /// </summary>
    /// <returns></returns>
    internal IEnumerator CullDespawned()
    {
        if (logMessages)
            Debug.Log("清理缓存池");

        yield return new WaitForSeconds(CullDelay);

        while (totalCount > CullAbove)
        {
            for (int i = 0; i < CullMaxPerPass; i++)
            {
                if (totalCount <= CullAbove)
                    break;

                if (m_Despawned.Count > 0)
                {
                    Transform inst = m_Despawned[0];
                    m_Despawned.RemoveAt(0);
                    SpawnPool.DestroyInstance(inst.gameObject);
                }
            }
            yield return new WaitForSeconds(CullDelay);
        }

        if (logMessages)
            Debug.Log("缓存池清理完毕");

        m_isCullingActive = false;
        yield return null;
    }



    /// <summary>
    /// 生成实例
    /// </summary>
    /// <param name="pos"></param>
    /// <param name="rot"></param>
    /// <returns></returns>
    internal Transform SpawnInstance(Vector3 pos, Quaternion rot)
    {
        if (IsLimitInstances && IsLimitFIFO && m_Spawned.Count >= LimitAmount)
        {
            Transform firstIn = m_Spawned[0];

            DespawnInstance(firstIn);
            SpawnPool.m_Spawned.Remove(firstIn);
        }

        Transform inst;
        if (m_Despawned.Count == 0)
        {
            inst = SpawnNew(pos, rot);
        }
        else
        {
            inst = m_Despawned[0];
            m_Despawned.RemoveAt(0);
            m_Spawned.Add(inst);

            if (inst == null)
            {
                throw new MissingReferenceException("生成了一个空对象");
            }
            inst.position = pos;
            inst.rotation = rot;
            inst.gameObject.SetActive(true);

        }
        return inst;
    }



    /// <summary>
    /// 生成新实例
    /// </summary>
    /// <returns></returns>
    public Transform SpawnNew() { return SpawnNew(Vector3.zero, Quaternion.identity); }
    public Transform SpawnNew(Vector3 pos, Quaternion rot)
    {
        if (IsLimitInstances && totalCount >= LimitAmount)
        {
            if (logMessages)
            {
                Debug.Log("数量超过限制");
            }

            return null;
        }

        if (pos == Vector3.zero) pos = SpawnPool.Group.position;
        if (rot == Quaternion.identity) rot = SpawnPool.Group.rotation;

        GameObject instGO = SpawnPool.InstantiatePrefab(PrefabGO, pos, rot);
        Transform inst = instGO.transform;

        nameInstance(inst);

        if (!SpawnPool.DontReparent)
        {
            var worldPositionStays = !(inst is RectTransform);
            inst.SetParent(SpawnPool.Group, worldPositionStays);
        }

        if (SpawnPool.MatchPoolScale)
            inst.localScale = Vector3.one;

        if (SpawnPool.MatchPoolLayer)
            SetRecursively(inst, SpawnPool.gameObject.layer);

        m_Spawned.Add(inst);

        if (logMessages)
            Debug.Log("创建新实例");

        return inst;
    }


    /// <summary>
    /// 设定层级
    /// </summary>
    /// <param name="xform"></param>
    /// <param name="layer"></param>
    private void SetRecursively(Transform xform, int layer)
    {
        xform.gameObject.layer = layer;
        foreach (Transform child in xform)
            SetRecursively(child, layer);
    }


    /// <summary>
    /// 添加进池
    /// </summary>
    /// <param name="inst">实例对象</param>
    /// <param name="despawn">是否隐藏</param>
    internal void AddUnpooled(Transform inst, bool despawn)
    {
        nameInstance(inst);

        if (despawn)
        {
            inst.gameObject.SetActive(false);

            m_Despawned.Add(inst);
        }
        else
            m_Spawned.Add(inst);
    }


    /// <summary>
    /// 预加载实例
    /// </summary>
    internal void PreloadInstances()
    {
        if (Preloaded)
        {
            Debug.LogWarning("已经预加载过了");
            return;
        }

        Preloaded = true;

        if (Prefab == null)
        {
            Debug.LogError("预制体为空");
            return;
        }

        if (IsLimitInstances && PreloadAmount > LimitAmount)
        {
            Debug.LogWarning("预加载数量大于限制数量,设置预加载数量为限制数量");

            PreloadAmount = LimitAmount;
        }
        if (IsCullDespawned && PreloadAmount > CullAbove)
        {
            Debug.LogWarning("预加载数量大于缓存池保留数量");
        }

        if (IsPreloadTime)
        {
            if (PreloadFrames > PreloadAmount)
            {
                PreloadFrames = PreloadAmount;
            }

            SpawnPool.StartCoroutine(PreloadOverTime());
        }
        else
        {
            m_isForceLoggingSilent = true;

            Transform inst;
            while (totalCount < PreloadAmount) // Total count will update
            {
                inst = SpawnNew();
                DespawnInstance(inst, false);
            }

            m_isForceLoggingSilent = false;
        }
    }

    /// <summary>
    /// 预加载额外工作
    /// </summary>
    /// <returns></returns>
    private IEnumerator PreloadOverTime()
    {
        yield return new WaitForSeconds(PreloadDelay);

        Transform inst;

        int amount = PreloadAmount - totalCount;
        if (amount <= 0)
            yield break;

        int remainder = amount % PreloadFrames;
        int numPerFrame = amount / PreloadFrames;

        m_isForceLoggingSilent = true;

        int numThisFrame;
        for (int i = 0; i < PreloadFrames; i++)
        {
            numThisFrame = numPerFrame;
            if (i == PreloadFrames - 1)
            {
                numThisFrame += remainder;
            }

            for (int n = 0; n < numThisFrame; n++)
            {
                inst = SpawnNew();
                if (inst != null)
                    DespawnInstance(inst, false);

                yield return null;
            }
            if (totalCount > PreloadAmount)
                break;
        }
        m_isForceLoggingSilent = false;
    }

    #endregion Pool Functionality


    #region Utilities
    /// <summary>
    /// 是否包含实例
    /// </summary>
    /// <param name="transform">实例对象</param>
    /// <returns>true包含</returns>
    public bool Contains(Transform transform)
    {
        if (PrefabGO == null)
            Debug.LogError("预制体是空的");

        bool contains;

        contains = Spawned.Contains(transform);
        if (contains)
            return true;

        contains = Despawned.Contains(transform);
        if (contains)
            return true;

        return false;
    }

    /// <summary>
    /// 命名实例对象
    /// </summary>
    /// <param name="instance"></param>
    private void nameInstance(Transform instance)
    {
        instance.name += (totalCount + 1).ToString("#000");
    }
    #endregion Utilities

}
