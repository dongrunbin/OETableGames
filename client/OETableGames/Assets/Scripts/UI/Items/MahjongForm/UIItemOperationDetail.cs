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
    private Image[] m_ImageMahjong;
    [SerializeField]
    private Image m_BG;

    private Action<List<Mahjong>> m_OnMahjongClick;

    private List<Mahjong> m_MahjongList;

    private void Awake()
    {
        m_BG.GetComponent<Button>().onClick.AddListener(OnBtnClick);
    }

    private void OnBtnClick()
    {
        if (m_OnMahjongClick != null)
        {
            m_OnMahjongClick(m_MahjongList);
        }
    }

    public void SetUI(List<Mahjong> lst, Action<List<Mahjong>> onClick)
    {
        m_MahjongList = lst;
        for (int i = 0; i < m_ImageMahjong.Length; ++i)
        {
            m_ImageMahjong[i].gameObject.SetActive(false);
        }

        for (int i = 0; i < lst.Count; ++i)
        {
            m_ImageMahjong[i].gameObject.SetActive(true);
            //m_ImageMahjong[i].overrideSprite = MahjongManager.Instance.LoadMahjongSprite(lst[i], false);
        }

        m_BG.rectTransform.sizeDelta = new Vector2(86 * lst.Count + 100, m_BG.rectTransform.sizeDelta.y);
        m_OnMahjongClick = onClick;
    }
}
