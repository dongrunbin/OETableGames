//===================================================
//Author      : DRB
//CreateTime  ：2021/4/6 3:53:24
//Description ：
//===================================================
using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class UIItemOperationDetail : UIItemBase
{
    [SerializeField]
    private Transform m_Container;
    [SerializeField]
    private Image[] m_ImagePoker;
    [SerializeField]
    private Image m_BG;

    private Action<List<Mahjong>> m_OnPokerClick;

    private List<Mahjong> m_PokerList;

    private void Awake()
    {
        m_BG.GetComponent<Button>().onClick.AddListener(OnBtnClick);
    }

    private void OnBtnClick()
    {
        if (m_OnPokerClick != null)
        {
            m_OnPokerClick(m_PokerList);
        }
    }

    public void SetUI(List<Mahjong> lst, Action<List<Mahjong>> onClick)
    {
        m_PokerList = lst;
        for (int i = 0; i < m_ImagePoker.Length; ++i)
        {
            m_ImagePoker[i].gameObject.SetActive(false);
        }

        for (int i = 0; i < lst.Count; ++i)
        {
            m_ImagePoker[i].gameObject.SetActive(true);
            //m_ImagePoker[i].overrideSprite = MahjongManager.Instance.LoadPokerSprite(lst[i], false);
        }

        m_BG.rectTransform.sizeDelta = new Vector2(86 * lst.Count + 100, m_BG.rectTransform.sizeDelta.y);
        m_OnPokerClick = onClick;
    }
}
