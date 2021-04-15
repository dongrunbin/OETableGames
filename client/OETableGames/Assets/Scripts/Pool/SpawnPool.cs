using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System;

public sealed class SpawnPool : MonoBehaviour, IList<Transform>
{
    #region Inspector Parameters
    /// <summary>
    /// 池名字
    /// </summary>
    public string PoolName = "";

    /// <summary>
    /// 匹配池比例
    /// </summary>
    public bool MatchPoolScale = false;

    /// <summary>
    /// 匹配池层级
    /// </summary>
    public bool MatchPoolLayer = false;

    /// <summary>
    /// 不重新指定根节点
    /// </summary>
    public bool DontReparent = false;

    /// <summary>
    /// 加载时不删除
    /// </summary>
    public new bool DontDestroyOnLoad
    {
        get
        {
            return m_DontDestroyOnLoad;
        }

        set
        {
            m_DontDestroyOnLoad = value;

            if (Group != null && m_DontDestroyOnLoad)
                UnityEngine.Object.DontDestroyOnLoad(Group.gameObject);
        }
    }
    [SerializeField]
    private bool m_DontDestroyOnLoad = false;

    /// <summary>
    /// 是否打印信息
    /// </summary>
    [SerializeField]
    internal bool m_LogMessages = false;

    /// <summary>
    /// 对象池
    /// </summary>
    public List<PrefabPool> PerPrefabPoolOptions = new List<PrefabPool>();
    #endregion Inspector Parameters



    #region Public Code-only Parameters
    /// <summary>
    /// 最大粒子消除时间
    /// </summary>
    public float maxParticleDespawnTime = 300;

    /// <summary>
    /// 池的根节点
    /// </summary>
    public Transform Group { get; private set; }

    /// <summary>
    /// 所有预制体
    /// </summary>
    public PrefabsDict Prefabs = new PrefabsDict();

    /// <summary>
    /// 预制体池
    /// </summary>
    public Dictionary<string, PrefabPool> prefabPools
    {
        get
        {
            var dict = new Dictionary<string, PrefabPool>();

            for (int i = 0; i < m_PrefabPools.Count; i++)
                dict[m_PrefabPools[i].PrefabGO.name] = m_PrefabPools[i];

            return dict;
        }
    }
    #endregion Public Code-only Parameters



    #region Private Properties
    private List<PrefabPool> m_PrefabPools = new List<PrefabPool>();
    internal List<Transform> m_Spawned = new List<Transform>();
    #endregion Private Properties



    #region Constructor and Init
    private void Awake()
    {
        if (m_DontDestroyOnLoad) UnityEngine.Object.DontDestroyOnLoad(gameObject);

        Group = transform;

        if (PoolName == "")
        {
            PoolName = Group.name.Replace("Pool", "");
            PoolName = PoolName.Replace("(Clone)", "");
        }


        if (m_LogMessages)
            Debug.Log(string.Format("{0}初始化", PoolName));

        for (int i = 0; i < PerPrefabPoolOptions.Count; i++)
        {
            if (PerPrefabPoolOptions[i].Prefab == null)
            {
                Debug.LogError(string.Format("{0}预制体池的预制体是空的", PoolName));
                continue;
            }
            PerPrefabPoolOptions[i].inspectorInstanceConstructor();
            CreatePrefabPool(PerPrefabPoolOptions[i]);
        }
        PoolManager.Pools.Add(this);
    }



    public delegate GameObject InstantiateDelegate(GameObject prefab, Vector3 pos, Quaternion rot);
    public delegate void DestroyDelegate(GameObject instance);

    /// <summary>
    /// 实例化委托
    /// </summary>
    public InstantiateDelegate instantiateDelegates;

    /// <summary>
    /// 删除委托
    /// </summary>
    public DestroyDelegate destroyDelegates;

    /// <summary>
    /// 实例化预制体
    /// </summary>
    /// <param name="prefab">预制体</param>
    /// <param name="pos">坐标</param>
    /// <param name="rot">旋转</param>
    /// <returns>实例对象</returns>
    internal GameObject InstantiatePrefab(GameObject prefab, Vector3 pos, Quaternion rot)
    {
        if (instantiateDelegates != null)
        {
            return instantiateDelegates(prefab, pos, rot);
        }
        else
        {
            return InstanceHandler.InstantiatePrefab(prefab, pos, rot);
        }
    }


    /// <summary>
    /// 删除实体
    /// </summary>
    /// <param name="prefab">实体对象</param>
    /// <returns>void</returns>
    internal void DestroyInstance(GameObject instance)
    {
        if (destroyDelegates != null)
        {
            destroyDelegates(instance);
        }
        else
        {
            InstanceHandler.DestroyInstance(instance);
        }
    }


    void OnDestroy()
    {
        if (m_LogMessages)
            Debug.Log(string.Format("池{0}销毁", PoolName));

        PoolManager.Pools.Remove(this);

        StopAllCoroutines();
        m_Spawned.Clear();
        for (int i = 0; i < m_PrefabPools.Count; ++i)
        {
            m_PrefabPools[i].SelfDestruct();
        }
        m_PrefabPools.Clear();
        Prefabs._Clear();
    }


    /// <summary>
    /// 创建预制体池
    /// </summary>
    /// <param name="prefabPool">预制体池对象</param>
    public void CreatePrefabPool(PrefabPool prefabPool)
    {
        bool isExistPool = GetPrefabPool(prefabPool.Prefab) == null ? false : true;
        if (isExistPool)
        {
            throw new System.Exception(string.Format("{0}已经存在", PoolName));
        }
        prefabPool.SpawnPool = this;

        m_PrefabPools.Add(prefabPool);

        Prefabs._Add(prefabPool.Prefab.name, prefabPool.Prefab);

        if (!prefabPool.Preloaded)
        {
            prefabPool.PreloadInstances();
        }
    }


    /// <summary>
    /// 添加实体进池
    /// </summary>
    /// <param name="instance"></param>
    /// <param name="prefabName"></param>
    /// <param name="despawn"></param>
    /// <param name="parent"></param>
    public void Add(Transform instance, string prefabName, bool despawn, bool parent)
    {
        for (int i = 0; i < m_PrefabPools.Count; i++)
        {
            if (m_PrefabPools[i].PrefabGO == null)
            {
                Debug.LogError("池预制体是空的");
                return;
            }

            if (m_PrefabPools[i].PrefabGO.name == prefabName)
            {
                m_PrefabPools[i].AddUnpooled(instance, despawn);

                if (parent)
                {
                    bool worldPositionStays = !(instance is RectTransform);
                    instance.SetParent(Group, worldPositionStays);
                }

                if (!despawn) m_Spawned.Add(instance);
                return;
            }
        }
        Debug.LogError(string.Format("没找到{0}预制体池", prefabName));
    }
    #endregion Constructor and Init



    #region List Overrides
    /// <summary>
    /// 未实现IList这个方法
    /// </summary>
    /// <param name="item"></param>
    public void Add(Transform item)
    {
        throw new NotImplementedException();
    }


    /// <summary>
    /// 未实现IList这个方法
    /// </summary>
    /// <param name="item"></param>
    public void Remove(Transform item)
    {
        throw new NotImplementedException();
    }
    #endregion List Overrides



    #region Pool Functionality
    /// <summary>
    /// 创建
    /// </summary>
    /// <param name="prefab">预制体</param>
    /// <param name="pos">坐标</param>
    /// <param name="rot">旋转</param>
    /// <param name="parent">父节点</param>
    /// <returns></returns>
    public Transform Spawn(Transform prefab, Vector3 pos, Quaternion rot, Transform parent)
    {
        Transform inst;

        #region 从池里创建
        for (int i = 0; i < m_PrefabPools.Count; i++)
        {
            if (m_PrefabPools[i].PrefabGO == prefab.gameObject)
            {
                inst = m_PrefabPools[i].SpawnInstance(pos, rot);
                if (inst == null) return null;

                if (parent != null)
                {
                    inst.parent = parent;
                }
                else if (!DontReparent && inst.parent != Group)
                {
                    inst.SetParent(Group, false);
                }
                m_Spawned.Add(inst);

                inst.gameObject.BroadcastMessage("OnSpawned", this, SendMessageOptions.DontRequireReceiver);
                return inst;
            }
        }
        #endregion

        #region 新生成
        PrefabPool newPrefabPool = new PrefabPool(prefab);
        CreatePrefabPool(newPrefabPool);
        inst = newPrefabPool.SpawnInstance(pos, rot);
        if (parent != null)
        {
            inst.parent = parent;
        }
        else
        {
            inst.SetParent(Group, false);
        }

        m_Spawned.Add(inst);


        inst.gameObject.BroadcastMessage("OnSpawned", this, SendMessageOptions.DontRequireReceiver);
        return inst;
        #endregion New PrefabPool
    }


    /// <summary>
    /// 创建
    /// </summary>
    public Transform Spawn(Transform prefab, Vector3 pos, Quaternion rot)
    {
        Transform inst = Spawn(prefab, pos, rot, null);
        if (inst == null) return null;
        return inst;
    }


    /// <summary>
    /// 创建
    /// </summary>
    public Transform Spawn(Transform prefab)
    {
        return Spawn(prefab, Vector3.zero, Quaternion.identity, null);
    }


    /// <summary>
    /// 创建
    /// </summary>
    public Transform Spawn(Transform prefab, Transform parent)
    {
        return Spawn(prefab, Vector3.zero, Quaternion.identity, parent);
    }


    #region GameObject Overloads
    public Transform Spawn(GameObject prefab, Vector3 pos, Quaternion rot, Transform parent)
    {
        return Spawn(prefab.transform, pos, rot, parent);
    }

    public Transform Spawn(GameObject prefab, Vector3 pos, Quaternion rot)
    {
        return Spawn(prefab.transform, pos, rot);
    }

    public Transform Spawn(GameObject prefab)
    {
        return Spawn(prefab.transform);
    }

    public Transform Spawn(GameObject prefab, Transform parent)
    {
        return Spawn(prefab.transform, parent);
    }
    #endregion GameObject Overloads


    /// <summary>
    /// 创建
    /// </summary>
    /// <param name="prefabName">预制体名称</param>
    /// <returns></returns>
    public Transform Spawn(string prefabName)
    {
        Transform prefab = Prefabs[prefabName];
        return Spawn(prefab);
    }

    public Transform Spawn(string prefabName, Transform parent)
    {
        Transform prefab = Prefabs[prefabName];
        return Spawn(prefab, parent);
    }

    public Transform Spawn(string prefabName, Vector3 pos, Quaternion rot)
    {
        Transform prefab = Prefabs[prefabName];
        return Spawn(prefab, pos, rot);
    }

    public Transform Spawn(string prefabName, Vector3 pos, Quaternion rot, Transform parent)
    {
        Transform prefab = Prefabs[prefabName];
        return Spawn(prefab, pos, rot, parent);
    }


    public AudioSource Spawn(AudioSource prefab, Vector3 pos, Quaternion rot)
    {
        return Spawn(prefab, pos, rot, null);
    }


    public AudioSource Spawn(AudioSource prefab)
    {
        return Spawn(prefab, Vector3.zero, Quaternion.identity, null);
    }


    public AudioSource Spawn(AudioSource prefab, Transform parent)
    {
        return Spawn(prefab, Vector3.zero, Quaternion.identity, parent);
    }


    public AudioSource Spawn(AudioSource prefab, Vector3 pos, Quaternion rot, Transform parent)
    {
        Transform inst = Spawn(prefab.transform, pos, rot, parent);
        if (inst == null) return null;
        AudioSource src = inst.GetComponent<AudioSource>();
        src.Play();
        StartCoroutine(ListenForAudioStop(src));
        return src;
    }

    public ParticleSystem Spawn(ParticleSystem prefab, Vector3 pos, Quaternion rot)
    {
        return Spawn(prefab, pos, rot, null);
    }

    public ParticleSystem Spawn(ParticleSystem prefab, Vector3 pos, Quaternion rot, Transform parent)
    {
        Transform inst = Spawn(prefab.transform, pos, rot, parent);
        if (inst == null) return null;

        ParticleSystem emitter = inst.GetComponent<ParticleSystem>();

        StartCoroutine(ListenForEmitDespawn(emitter));

        return emitter;
    }

    /// <summary>
    /// 删除
    /// </summary>
    /// <param name="instance"></param>
    public void Despawn(Transform instance)
    {
        bool despawned = false;
        for (int i = 0; i < m_PrefabPools.Count; i++)
        {
            if (m_PrefabPools[i].m_Spawned.Contains(instance))
            {
                despawned = m_PrefabPools[i].DespawnInstance(instance);
                break;
            }
            else if (m_PrefabPools[i].m_Despawned.Contains(instance))
            {
                Debug.LogError(string.Format("{0}已经删除过了", instance.name));
                return;
            }
        }

        if (!despawned)
        {
            Debug.LogError(string.Format("池里没有{0}", instance.name));
            return;
        }
        m_Spawned.Remove(instance);
    }

    public void Despawn(Transform instance, Transform parent)
    {
        instance.parent = parent;
        Despawn(instance);
    }

    public void Despawn(Transform instance, float seconds)
    {
        StartCoroutine(DoDespawnAfterSeconds(instance, seconds, false, null));
    }

    public void Despawn(Transform instance, float seconds, Transform parent)
    {
        StartCoroutine(DoDespawnAfterSeconds(instance, seconds, true, parent));
    }

    private IEnumerator DoDespawnAfterSeconds(Transform instance, float seconds, bool useParent, Transform parent)
    {
        GameObject go = instance.gameObject;
        while (seconds > 0)
        {
            yield return null;
            if (!go.activeInHierarchy) yield break;
            seconds -= Time.deltaTime;
        }
        if (useParent)
            Despawn(instance, parent);
        else
            Despawn(instance);
    }


    /// <description>
    /// 隐藏所有池里的对象
    /// </description>
    public void DespawnAll()
    {
        List<Transform> spawned = new List<Transform>(m_Spawned);
        for (int i = 0; i < spawned.Count; i++)
        {
            Despawn(spawned[i]);
        }
    }

    /// <summary>
    /// 该对象是否正在显示
    /// </summary>
    /// <param name="instance"></param>
    /// <returns></returns>
    public bool IsSpawned(Transform instance)
    {
        return m_Spawned.Contains(instance);
    }

    #endregion Pool Functionality



    #region Utility Functions
    /// <summary>
    /// 获取预制体池
    /// </summary>
    /// <param name="prefab">预制体</param>
    /// <returns>预制体池</returns>
    public PrefabPool GetPrefabPool(Transform prefab)
    {
        for (int i = 0; i < m_PrefabPools.Count; i++)
        {
            if (m_PrefabPools[i].PrefabGO == null)
                Debug.LogError(string.Format("生产池 {0} 的预制体是空的", PoolName));

            if (m_PrefabPools[i].PrefabGO == prefab.gameObject)
                return m_PrefabPools[i];
        }
        return null;
    }


    /// <summary>
    /// 获取预制体池
    /// </summary>
    /// <param name="prefab">预制体</param>
    /// <returns>预制体池</returns>
    public PrefabPool GetPrefabPool(GameObject prefab)
    {
        for (int i = 0; i < m_PrefabPools.Count; i++)
        {
            if (m_PrefabPools[i].PrefabGO == null)
                Debug.LogError(string.Format("生产池 {0} 的预制体是空的", PoolName));

            if (m_PrefabPools[i].PrefabGO == prefab)
                return m_PrefabPools[i];
        }
        return null;
    }


    /// <summary>
    /// 根据实例获取预制体
    /// </summary>
    /// <param name="instance">实例对象</param>
    /// <returns>预制体</returns>
    public Transform GetPrefab(Transform instance)
    {
        for (int i = 0; i < m_PrefabPools.Count; i++)
        {
            if (m_PrefabPools[i].Contains(instance))
            {
                return m_PrefabPools[i].Prefab;
            }
        }
        return null;
    }

    /// <summary>
    /// 根据实例获取预制体
    /// </summary>
    /// <param name="instance">实例对象</param>
    /// <returns>预制体</returns>
    public GameObject GetPrefab(GameObject instance)
    {
        for (int i = 0; i < m_PrefabPools.Count; i++)
        {
            if (m_PrefabPools[i].Contains(instance.transform))
            {
                return m_PrefabPools[i].PrefabGO;
            }
        }
        return null;
    }

    /// <summary>
    /// 监听声源停止
    /// </summary>
    /// <param name="src"></param>
    /// <returns></returns>
    private IEnumerator ListenForAudioStop(AudioSource src)
    {
        yield return null;

        GameObject srcGameObject = src.gameObject;
        while (src.isPlaying)
        {
            yield return null;
        }
        if (!srcGameObject.activeInHierarchy)
        {
            src.Stop();
            yield break;
        }
        Despawn(src.transform);
    }

    private IEnumerator ListenForEmitDespawn(ParticleSystem emitter)
    {
        yield return new WaitForSeconds(emitter.main.startDelay.constant + 0.25f);

        float safetimer = 0;
        GameObject emitterGO = emitter.gameObject;
        while (emitter.IsAlive(true) && emitterGO.activeInHierarchy)
        {
            safetimer += Time.deltaTime;
            if (safetimer > maxParticleDespawnTime)
                Debug.LogWarning("粒子时间超出");

            yield return null;
        }
        if (emitterGO.activeInHierarchy)
        {
            Despawn(emitter.transform);
            emitter.Clear(true);
        }
    }

    #endregion Utility Functions



    /// <summary>
    /// 返回所有存在的实例名字
    /// </summary>
    public override string ToString()
    {
        List<string> name_list = new List<string>();
        for (int i = 0; i < m_Spawned.Count; ++i)
        {
            name_list.Add(m_Spawned[i].name);
        }
        return string.Join(", ", name_list.ToArray());
    }

    public Transform this[int index]
    {
        get { return m_Spawned[index]; }
        set { throw new NotImplementedException("ReadOnly"); }
    }

    /// <summary>
    /// 未实现
    /// </summary>
    /// <param name="item"></param>
    /// <returns></returns>
    public bool Contains(Transform item)
    {
        throw new NotImplementedException("未实现该方法");
    }

    public void CopyTo(Transform[] array, int arrayIndex)
    {
        m_Spawned.CopyTo(array, arrayIndex);
    }

    /// <summary>
    /// 显示中的对象数量
    /// </summary>
    public int Count
    {
        get { return m_Spawned.Count; }
    }


    public IEnumerator<Transform> GetEnumerator()
    {
        for (int i = 0; i < m_Spawned.Count; i++)
            yield return m_Spawned[i];
    }

    IEnumerator IEnumerable.GetEnumerator()
    {
        for (int i = 0; i < m_Spawned.Count; i++)
            yield return m_Spawned[i];
    }

    public int IndexOf(Transform item) { throw new NotImplementedException(); }
    public void Insert(int index, Transform item) { throw new NotImplementedException(); }
    public void RemoveAt(int index) { throw new NotImplementedException(); }
    public void Clear() { throw new NotImplementedException(); }
    public bool IsReadOnly { get { throw new NotImplementedException(); } }
    bool ICollection<Transform>.Remove(Transform item) { throw new NotImplementedException(); }

}