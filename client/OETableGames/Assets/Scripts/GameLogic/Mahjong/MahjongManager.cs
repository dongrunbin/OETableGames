using DG.Tweening;
using DrbFramework.Extensions;
using DrbFramework.Internal;
using UnityEngine;

public class MahjongManager : Singleton<MahjongManager>
{
    private SpawnPool m_WallPool;

    private GameObject LoadPrefab(Mahjong mahjong)
    {
        string mahjongName = (mahjong == null || mahjong.size == 0) ? Mahjong.DefaultName : string.Format("{0}_{1}", mahjong.color, mahjong.size);
        string path = string.Format("Downloads/Prefabs/{0}", mahjongName);
        return DrbComponent.ResourceSystem.LoadAsset<GameObject>(path);
    }

    public void Init()
    {
        m_WallPool = PoolManager.Pools.Create("MaJiang");
        m_WallPool.Group.parent = null;
        m_WallPool.Group.position = new Vector3(0f, 5000f, 0f);

        for (int i = 0; i < 8; ++i)
        {
            for (int j = 0; j < 10; ++j)
            {
                if (i != 0 && j == 0) continue;
                if ((i == 4 || i == 6 || i == 7) && j > 4) break;
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
        MahjongCtrl ctrl = m_WallPool.Spawn((mahjong == null || mahjong.color == 0) ? Mahjong.DefaultName : mahjong.ToString()).gameObject.GetOrCreatComponent<MahjongCtrl>();
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

    public GameObject SpawnHand_Fang()
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

    public Sprite LoadMahjongSprite(Mahjong mahjong, bool isPeng = false)
    {
        string spriteName;
        if (mahjong == null || mahjong.color == 0)
        {
            spriteName = "0_b";
        }
        else
        {
            spriteName = "0_" + mahjong.ToString() + (isPeng ? "_t" : "");
        }

        string path = string.Format("Downloads/UI/Source/Mahjong/{0}", spriteName);

        Sprite sprite = DrbComponent.ResourceSystem.LoadSprite(path, spriteName);
        return sprite;
    }
}
