//===================================================
//Author      : DRB
//CreateTime  ：2021/5/1 20:56:09
//Description ：
//===================================================
using DrbFramework.Extensions;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class UIItemSettle : UIItemBase
{
    [SerializeField]
    protected GameObject m_ItemSeatPrefab;
    [SerializeField]
    protected Transform m_SeatContainer;
    [SerializeField]
    protected Button m_BtnGameOver;
    [SerializeField]
    protected Button m_BtnReady;
    [SerializeField]
    protected List<UIItemSettleSeat> m_Seat = new List<UIItemSettleSeat>();

    protected override void OnAwake()
    {
        base.OnAwake();

        m_BtnReady.onClick.AddListener(OnReadyClick);
    }

    private void OnReadyClick()
    {
        MahjongService.Instance.ClientSendReady();
    }


    public void SetUI(Room room)
    {
        m_BtnGameOver.SafeSetActive(room.isOver);
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