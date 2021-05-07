//===================================================
//Author      : DRB
//CreateTime  ：2021/4/28 0:25:24
//Description ：
//===================================================
using DG.Tweening;
using DrbFramework.Extensions;
using DrbFramework.Internal;
using System.Collections.Generic;
using UnityEngine;

public class MahjongManager : MonoBehaviour
{
    private SpawnPool m_WallPool;

    public static MahjongManager Instance = null;

    [SerializeField]
    private List<Sprite> MahjongSprites;
    [SerializeField]
    private List<Sprite> MahjongSprites_Pong;

    private void Awake()
    {
        Instance = this;
    }

    private void OnDestroy()
    {
        if (m_WallPool != null)
        {
            Destroy(m_WallPool.gameObject);
            PoolManager.Pools.Destroy("MahjongPool");
            m_WallPool = null;
        }
    }

    private GameObject LoadPrefab(Mahjong mahjong)
    {
        string mahjongName = (mahjong == null || mahjong.number == 0) ? Mahjong.DefaultName : string.Format("{0}_{1}", mahjong.color, mahjong.number);
        string path = string.Format("Downloads/Prefabs/{0}.prefab", mahjongName);
        return DrbComponent.ResourceSystem.LoadAsset<GameObject>(path);
    }

    public void Init()
    {
        m_WallPool = PoolManager.Pools.Create("MahjongPool");
        m_WallPool.Group.parent = null;
        m_WallPool.Group.position = new Vector3(0f, 5000f, 0f);

        for (int i = 0; i < 6; ++i)
        {
            if (i == 4) continue;
            for (int j = 0; j < 10; ++j)
            {
                if (i != 0 && j == 0) continue;
                if (i == 5 && j > 3) break;
                GameObject prefab = LoadPrefab(new Mahjong(i, j));
                PrefabPool prefabPool = new PrefabPool(prefab.transform);
                if (i == 0 && j == 0)
                {
                    prefabPool.PreloadAmount = 136;
                }
                else
                {
                    prefabPool.PreloadAmount = 5;
                }
                m_WallPool.CreatePrefabPool(prefabPool);
                if (i == 0) break;
            }
        }
    }

    public MahjongCtrl SpawnMahjong(Mahjong mahjong, bool isUniversal)
    {
        MahjongCtrl ctrl = m_WallPool.Spawn((mahjong == null || mahjong.color == 0) ? Mahjong.DefaultName : mahjong.ToString()).gameObject.GetOrAddComponent<MahjongCtrl>();
        ctrl.Init(mahjong, isUniversal);
        return ctrl;
    }

    public GameObject SpawnDice()
    {
        string prefabName = "dice";
        string path = string.Format("Downloads/Prefabs/{0}.prefab", prefabName);
        GameObject prefab = DrbComponent.ResourceSystem.LoadAsset<GameObject>(path);
        return UnityEngine.Object.Instantiate(prefab);
    }

    public GameObject SpawnHand_Tui()
    {
        string handPrefabName = "hand";
        string handPath = string.Format("Downloads/Prefabs/{0}.prefab", handPrefabName);
        GameObject prefab = DrbComponent.ResourceSystem.LoadAsset<GameObject>(handPath);
        return UnityEngine.Object.Instantiate(prefab);
    }

    public GameObject SpawnDiceHand()
    {
        string handPrefabName = "dicehand";
        string handPath = string.Format("Downloads/Prefabs/{0}.prefab", handPrefabName);
        GameObject prefab = DrbComponent.ResourceSystem.LoadAsset<GameObject>(handPath);
        return UnityEngine.Object.Instantiate(prefab);
    }

    public void DespawnMahjong(MahjongCtrl majiang)
    {
        majiang.Reset();
        majiang.transform.DOKill(false);
        m_WallPool.Despawn(majiang.transform);
    }

    public Sprite GetMahjongSprite(Mahjong mahjong)
    {
        int index;
        if (mahjong.color == 0)
        {
            index = 9;
        }
        else if (mahjong.color == 5)
        {
            index = 19;
        }
        else
        {
            index = (mahjong.color - 1) * 10 + mahjong.number - 1;
        }
        Sprite sprite = MahjongSprites[index];
        return sprite;
    }
}
