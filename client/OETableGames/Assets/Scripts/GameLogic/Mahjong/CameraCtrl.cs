//===================================================
//Author      : DRB
//CreateTime  ：2021/4/14 2:12:22
//Description ：
//===================================================
using DG.Tweening;
using System;
using UnityEngine;

public class CameraCtrl : MonoBehaviour
{
    [SerializeField]
    protected Transform[] m_Pos;

    public Transform CenterContainer;
    [HideInInspector]
    public Camera MainCamera;

    [SerializeField]
    private Transform[] m_HandMahjongCameraContainers;
    [SerializeField]
    public Camera HandMahjongCamera;


    private void Awake()
    {
        MainCamera = GetComponent<Camera>();
        MainCamera.aspect = 1920f / 1080f;
    }

    private void Start()
    {
        if (HandMahjongCamera != null)
        {
            if (HandMahjongCamera.aspect < 1920f / 1080f)
            {
                HandMahjongCamera.aspect = 1920f / 1080f;
            }
        }
    }


    public void SetPos(int playerSeatId, int seatCount)
    {
        int index = playerSeatId - 1;
        if (index < 0)
        {
            index = 0;
        }
        if (seatCount == 2)
        {
            if (index == 1)
            {
                index = 2;
            }
        }
        for (int i = 0; i < m_Pos.Length; ++i)
        {
            m_Pos[i].gameObject.SetActive(index == i);
        }
        gameObject.transform.SetParent(m_Pos[index]);
    }

    public virtual void SetPos(int seatId, Action onComplete)
    {
        int index = seatId - 1;
        if (index < 0)
        {
            index = 0;
        }
        for (int i = 0; i < m_Pos.Length; ++i)
        {
            m_Pos[i].gameObject.SetActive(index == i);
        }
        transform.SetParent(m_Pos[index]);
        transform.DOLocalMove(Vector3.zero, 1f).SetEase(Ease.Linear).OnComplete(() =>
        {
            if (onComplete != null)
            {
                onComplete();
            }
        });
        transform.DOLocalRotate(Vector3.zero, 1f).SetEase(Ease.Linear);
    }
}
