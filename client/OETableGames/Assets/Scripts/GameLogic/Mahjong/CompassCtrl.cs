//===================================================
//Author      : DRB
//CreateTime  ：2021/4/14 3:31:56
//Description ：
//===================================================
using DG.Tweening;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CompassCtrl : MonoBehaviour
{
    [SerializeField]
    private GameObject[] m_Direction;
    [SerializeField]
    private Material m_Mat;

    private void Start()
    {
        for (int i = 0; i < m_Direction.Length; ++i)
        {
            m_Direction[i].GetComponent<Renderer>().material = m_Mat;
        }
        m_Mat.DOKill();
        m_Mat.color = new Color(1, 1, 1, 0);
        m_Mat.DOColor(new Color(1, 1, 1, 1), 1f).SetLoops(-1, LoopType.Yoyo).SetEase(Ease.Linear);
        SetNormal();
    }

    private void OnDestroy()
    {
        if (m_Mat != null)
        {
            m_Mat.color = new Color(1, 1, 1, 0);
        }
    }

    public void Init(int seatCount)
    {
        if (seatCount == 2 && m_Direction.Length == 4)
        {
            m_Direction = new GameObject[2] { m_Direction[0], m_Direction[2] };
        }
        SetNormal();
    }

    public void SetCurrent(int seatPos)
    {
        int index = 0;
        index = seatPos - 1;

        for (int i = 0; i < m_Direction.Length; ++i)
        {
            m_Direction[i].SetActive(i == index);
        }
    }

    public void SetNormal()
    {
        for (int i = 0; i < m_Direction.Length; ++i)
        {
            m_Direction[i].SetActive(false);
        }
    }
}
