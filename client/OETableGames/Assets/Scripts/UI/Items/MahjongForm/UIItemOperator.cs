//===================================================
//Author      : DRB
//CreateTime  ：2021/4/6 3:37:30
//Description ：
//===================================================
using DrbFramework.Extensions;
using System;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class UIItemOperator : UIItemBase
{
    [SerializeField]
    private Image m_ImageChi;
    [SerializeField]
    private Image m_ImagePeng;
    [SerializeField]
    private Image m_ImageGang;
    [SerializeField]
    private Image m_ImageHu;
    [SerializeField]
    private Image m_ImageZiMo;
    [SerializeField]
    private Image m_ImagePass;
    [SerializeField]
    private Image m_ImageCancel;
    [SerializeField]
    private Transform m_DetailContainer;
    [SerializeField]
    private GameObject m_DetailPrefab;


    private OperationType m_CurrentType;
    private List<UIItemOperationDetail> m_ListDetail = new List<UIItemOperationDetail>(2);
    private List<Mahjong> m_PengList;
    private List<List<Mahjong>> m_GangList;
    private List<List<Mahjong>> m_ChiList;
    private bool m_isZiMo;
    private bool m_isHu;

    public Action<OperationType, List<Mahjong>> OnOperatorClick;

    public void Show(List<List<Mahjong>> chiList, List<Mahjong> pengList, List<List<Mahjong>> gangList, bool isHu, bool isZiMo)
    {
        m_ChiList = chiList;
        m_PengList = pengList;
        m_GangList = gangList;
        m_isZiMo = isZiMo;
        m_isHu = isHu;
        ShowOperator();
    }

    private void ShowOperator()
    {
        m_ImageChi.SafeSetActive(m_ChiList != null && m_ChiList.Count > 0);
        m_ImagePeng.SafeSetActive(m_PengList != null && m_PengList.Count > 0);
        m_ImageGang.SafeSetActive(m_GangList != null && m_GangList.Count > 0);
        m_ImageZiMo.SafeSetActive(m_isZiMo && m_isHu);
        m_ImageHu.SafeSetActive(!m_isZiMo && m_isHu);
        m_ImagePass.SafeSetActive(true);
    }

    public void Close(bool isClear = true)
    {
        for (int i = 0; i < m_ListDetail.Count; ++i)
        {
            m_ListDetail[i].gameObject.SetActive(false);
        }
        m_ImageChi.SafeSetActive(false);
        m_ImagePeng.SafeSetActive(false);
        m_ImageGang.SafeSetActive(false);
        m_ImageHu.SafeSetActive(false);
        m_ImagePass.SafeSetActive(false);
        m_ImageCancel.SafeSetActive(false);
        m_ImageZiMo.SafeSetActive(false);
        if (isClear)
        {
            m_PengList = null;
            m_GangList = null;
            m_ChiList = null;
            m_isZiMo = false;
            m_isHu = false;
        }
    }

    private void OnBtnClick(GameObject go)
    {
        List<Mahjong> lst = null;
        if (go == m_ImageChi.gameObject)
        {
            m_CurrentType = OperationType.Chi;
            lst = m_ChiList[0];
            if (m_ChiList.Count > 1)
            {
                m_DetailContainer.GetComponent<GridLayoutGroup>().spacing = new Vector2(400, 0);
                for (int i = 0; i < m_ChiList.Count; ++i)
                {
                    UIItemOperationDetail detail = null;
                    if (i >= m_ListDetail.Count)
                    {
                        GameObject obj = Instantiate(m_DetailPrefab);
                        obj.transform.SetParent(m_DetailContainer);
                        detail = obj.GetComponent<UIItemOperationDetail>();
                        m_ListDetail.Add(detail);
                    }
                    else
                    {
                        detail = m_ListDetail[i];
                        detail.gameObject.SetActive(true);
                    }
                    detail.SetUI(m_ChiList[i], OnDetailClick);
                }
                return;
            }
        }
        else if (go == m_ImagePeng.gameObject)
        {
            m_CurrentType = OperationType.Peng;
            lst = m_PengList;
        }
        else if (go == m_ImageGang.gameObject)
        {
            m_CurrentType = OperationType.Gang;
            lst = m_GangList[0];
            if (m_GangList.Count > 1)
            {
                m_DetailContainer.GetComponent<GridLayoutGroup>().spacing = new Vector2(400, 0);
                for (int i = 0; i < m_GangList.Count; ++i)
                {
                    UIItemOperationDetail detail = null;
                    if (i >= m_ListDetail.Count)
                    {
                        GameObject obj = Instantiate(m_DetailPrefab);
                        obj.transform.SetParent(m_DetailContainer);
                        detail = obj.GetComponent<UIItemOperationDetail>();
                        m_ListDetail.Add(detail);
                    }
                    else
                    {
                        detail = m_ListDetail[i];
                        detail.gameObject.SetActive(true);
                    }
                    detail.SetUI(m_GangList[i], OnDetailClick);
                }
                return;
            }
        }
        else if (go == m_ImageHu.gameObject)
        {
            m_CurrentType = OperationType.Hu;
        }
        else if (go == m_ImagePass.gameObject)
        {
            m_CurrentType = OperationType.Pass;
            Close();
        }
        else if (m_ImageCancel != null && go == m_ImageCancel.gameObject)
        {
            m_CurrentType = OperationType.Cancel;
            ShowOperator();
            m_ImageCancel.gameObject.SetActive(false);
        }

        if (OnOperatorClick != null)
        {
            OnOperatorClick(m_CurrentType, lst);
        }
    }

    private void OnDetailClick(List<Mahjong> lst)
    {
        if (OnOperatorClick != null)
        {
            OnOperatorClick(m_CurrentType, lst);
        }
    }
}
