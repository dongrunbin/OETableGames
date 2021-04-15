//===================================================
//Author      : DRB
//CreateTime  ：2021/4/14 1:57:07
//Description ：
//===================================================
using DrbFramework.Extensions;
using DrbFramework.Internal;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class MahjongCtrl : MonoBehaviour
{
    public enum ForwardColorType
    {
        Normal,
        Gray,
        Yellow,
    }

    /// <summary>
    /// 数据
    /// </summary>
    [SerializeField]
    private Mahjong m_Poker;
    public Mahjong Mahjong
    {
        get { return m_Poker; }
        private set { m_Poker = value; }
    }
    [SerializeField]
    private GameObject m_Tip;
    [SerializeField]
    private GameObject m_Universal;
    [SerializeField]
    private GameObject m_Model;

    public GameObject Model
    {
        get { return m_Model; }
    }

    private void Awake()
    {
        m_Model = transform.GetChild(0).gameObject;
        BoxCollider collider = gameObject.AddComponent<BoxCollider>();
        collider.size = new Vector3(7f, 5f, 10f);
    }

    public void Init(Mahjong mahjong, bool isUniversal)
    {
        Reset();
        Mahjong = mahjong;
        SetUniversal(isUniversal);
    }

    public void SetUniversal(bool isUniversal)
    {
        if (m_Universal != null)
        {
            m_Universal.SetActive(isUniversal);
            m_Universal.SetLayer(gameObject.layer);
        }
    }

    public void ShowTip(bool isHandPoker)
    {
        m_Tip.gameObject.SetActive(true);
        if (isHandPoker)
        {
            m_Tip.transform.localPosition = new Vector3(0f, 0f, 8f);
            m_Tip.transform.localScale = Vector3.one;
        }
        else
        {
            m_Tip.transform.position = transform.position + new Vector3(0f, 8f, 0f);
            m_Tip.transform.localScale = new Vector3(1.8f, 1.8f, 1.8f);
        }
        m_Tip.SetLayer(gameObject.layer);
        m_Tip.transform.rotation = Quaternion.identity;
    }

    public void Show(bool isPlayer)
    {
        if (isPlayer)
        {
            m_Model.transform.localEulerAngles = new Vector3(60f, 0f, 0f);
            m_Model.transform.localPosition = Vector3.zero;
        }
        else
        {
            m_Model.transform.localEulerAngles = new Vector3(90f, 0f, 0f);
            m_Model.transform.localPosition = new Vector3(0f, 0f, -2.7f);
        }
    }

    public void CloseTip()
    {
        if (m_Tip != null)
        {
            m_Tip.SetActive(false);
        }
    }

    public void SetLocalAngles(Vector3 v)
    {
        m_Model.transform.localEulerAngles = v;
    }

    public void Reset()
    {
        Mahjong = null;
        CloseTip();
        SetUniversal(false);
        m_Model.transform.localEulerAngles = Vector3.zero;
        m_Model.transform.localPosition = Vector3.zero;
        m_Model.transform.localScale = Vector3.one;
        isSelect = false;
        ForwardColor = ForwardColorType.Normal;
    }

    private bool m_isSelect;
    public bool isSelect
    {
        get { return m_isSelect; }
        set
        {
            m_isSelect = value;
            if (m_isSelect)
            {
                m_Model.transform.localPosition = new Vector3(0, 0, 2);

            }
            else
            {
                m_Model.transform.localPosition = Vector3.zero;
            }
        }
    }

    private ForwardColorType m_ForwardColor;
    public ForwardColorType ForwardColor
    {
        get { return m_ForwardColor; }
        set
        {
            if (m_ForwardColor.Equals(value)) return;
            m_ForwardColor = value;
            LoadMaterial();
        }
    }

    private void LoadMaterial()
    {
        //string name = string.Format("mj_{0}_{1}", m_BackColor, m_ForwardColor.ToString());
        //string path = string.Format("download/{0}/source/modelsource/materials/{1}.drb", ConstDefine.GAME_NAME, name);
        //DrbComponent.ResourceSystem.LoadAssetFromAssetBundleAsync.(path, name, (Material mat) =>
        //{
        //    if (mat != null)
        //    {
        //        GetComponentInChildren<Renderer>().material = mat;
        //    }
        //}, 0);
    }
}
