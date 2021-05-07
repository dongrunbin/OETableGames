//===================================================
//Author      : DRB
//CreateTime  ：2021/4/6 4:01:28
//Description ：
//===================================================
using DrbFramework.Extensions;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class UIItemTingTip : UIItemBase
{
    [SerializeField]
    private Transform m_Container;
    [SerializeField]
    private Text m_TxtAny;
    [SerializeField]
    private GameObject m_ItemPrefab;

    private const int SHOW_ALL_HU = 27;
    private const int SHOW_COUNT = 5;

    private List<GameObject> m_HuList = new List<GameObject>();

    protected override void OnAwake()
    {
        base.OnAwake();
        this.SafeSetActive(false);
    }

    public void ShowTip(List<Mahjong> lst)
    {
        if (lst == null || lst.Count == 0)
        {
            Close();
            return;
        }
        for (int i = 0; i < m_HuList.Count; ++i)
        {
            Destroy(m_HuList[i]);
        }
        m_HuList.Clear();
        if (lst.Count < SHOW_ALL_HU)
        {
            m_TxtAny.SafeSetActive(false);

            for (int i = 0; i < lst.Count; ++i)
            {
                GameObject go = Instantiate(m_ItemPrefab);
                m_HuList.Add(go);
                go.SetActive(true);
                go.SetParentAndReset(m_Container);

                int hash = lst[i].GetHashCode();
                Image img = go.GetOrAddComponent<Image>();
                img.overrideSprite = MahjongManager.Instance.GetMahjongSprite(lst[i]);
                img.SetNativeSize();
            }
            if (lst.Count >= SHOW_COUNT)
            {
                return;
            }
        }
        else
        {
            m_TxtAny.gameObject.SetActive(true);
        }
    }

    public void Close()
    {
        gameObject.SetActive(false);
    }
}
