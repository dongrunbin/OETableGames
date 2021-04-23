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
    private RectTransform m_ScrollRectTran;
    [SerializeField]
    private Transform m_Container;
    [SerializeField]
    private Image m_BG;
    [SerializeField]
    private Image m_ImageAllHu;
    [SerializeField]
    private Image m_ImageHu;
    [SerializeField]
    private GameObject m_ObjLeftRight;
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

    public void ShowTip(List<Mahjong> lst, Dictionary<int, int> mahjongCount)
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
            m_ImageAllHu.gameObject.SetActive(false);
            m_ImageHu.gameObject.SetActive(true);

            for (int i = 0; i < lst.Count; ++i)
            {
                GameObject go = Instantiate(m_ItemPrefab);
                m_HuList.Add(go);
                go.transform.SetParent(m_Container);

                Text txt = go.GetComponentInChildren<Text>();

                int hash = lst[i].GetHashCode();
                int overplus = (4 - mahjongCount[hash]);
                txt.SafeSetText(overplus.ToString());
                Image img = go.GetOrCreatComponent<Image>();
                //img.overrideSprite = MahjongManager.Instance.LoadMahjongSprite(lst[i], false);
                img.SetNativeSize();
                if (overplus == 0)
                {
                    img.color = Color.gray;
                }
                else
                {
                    img.color = Color.white;
                }
            }
            if (lst.Count >= SHOW_COUNT)
            {
                m_ObjLeftRight.gameObject.SetActive(true);
                m_BG.rectTransform.sizeDelta = new Vector2(SHOW_COUNT * 100 + 250, 300);
                m_ScrollRectTran.sizeDelta = new Vector2(SHOW_COUNT * 100, 210);
                return;
            }
            m_ObjLeftRight.gameObject.SetActive(false);
            m_BG.rectTransform.sizeDelta = new Vector2(lst.Count * 100 + 250, 300);
            m_ScrollRectTran.sizeDelta = new Vector2(lst.Count * 100, 210);
        }
        else
        {
            m_ObjLeftRight.SetActive(false);
            m_ImageAllHu.gameObject.SetActive(true);
            m_ImageHu.gameObject.SetActive(false);
            m_BG.rectTransform.sizeDelta = new Vector2(300, 0);
        }
    }

    public void Close()
    {
        gameObject.SetActive(false);
    }
}
