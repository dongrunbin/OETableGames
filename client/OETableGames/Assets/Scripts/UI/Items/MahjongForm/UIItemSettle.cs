//===================================================
//Author      : DRB
//CreateTime  ：2021/5/1 20:56:09
//Description ：
//===================================================
using DrbFramework.Extensions;
using System;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class UIItemSettle : UIItemBase
{
    [SerializeField]
    private GameObject m_ItemSeatPrefab;
    [SerializeField]
    private Transform m_SeatContainer;
    [SerializeField]
    private Button m_BtnGameOver;
    [SerializeField]
    private Button m_BtnReady;
    [SerializeField]
    private List<UIItemSettleSeat> m_Seat = new List<UIItemSettleSeat>();
    [SerializeField]
    private UIItemResult m_ResultView;

    private Room m_Room;

    protected override void OnAwake()
    {
        base.OnAwake();

        m_BtnReady.onClick.AddListener(OnReadyClick);
        m_BtnGameOver.onClick.AddListener(OnGameoverClick);
    }

    private void OnGameoverClick()
    {
        this.Hide();
        m_ResultView.Show();
        m_ResultView.SetUI(m_Room);
    }

    private void OnReadyClick()
    {
        MahjongService.Instance.ClientSendReady();
    }


    public void SetUI(Room room)
    {
        m_Room = room;
        m_BtnGameOver.SafeSetActive(room.isOver);
        m_BtnReady.SafeSetActive(!room.isOver);
        for (int i = 0; i < m_Seat.Count; ++i)
        {
            m_Seat[i].gameObject.SetActive(false);
        }

        for (int i = 0; i < room.SeatList.Count; ++i)
        {
            Seat seat = room.SeatList[i];
            UIItemSettleSeat info = m_Seat[i];
            info.gameObject.SetActive(true);
            info.SetUI(seat, room);
        }
    }
}
