//===================================================
//Author      : DRB
//CreateTime  ：2021/4/6 3:22:10
//Description ：
//===================================================
using DrbFramework.Extensions;
using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class MahjongForm : FormBase
{
    [SerializeField]
    private UIItemSeat[] m_Seats;
    [SerializeField]
    private UIItemRoomInfo m_RoomInfo;
    [SerializeField]
    public UIItemOperator Operator;
    [SerializeField]
    private UIItemTingTip m_TingTip;
    [SerializeField]
    private Button m_ButtonReady;
    [SerializeField]
    private UIItemTimeTip m_TimeTip;
    [SerializeField]
    private Button m_BtnLeave;
    //[SerializeField]
    //private UISettleViewBase m_SettleView;
    //[SerializeField]
    //private UIResultViewBase m_ResultView;

    public void Init(Room room)
    {
        if (room == null) return;
        if (room.PlayerSeat == null) return;

        if (room.SeatList.Count == 2 && m_Seats.Length == 4)
        {
            m_Seats[1].SeatIndex = 2;
            m_Seats[1].SafeSetActive(false);
            m_Seats[3].SafeSetActive(false);
            m_Seats = new UIItemSeat[2] { m_Seats[0], m_Seats[2] };
            m_Seats[1].SeatIndex = 1;
        }

        m_ButtonReady.SafeSetActive(room.PlayerSeat.Status == SeatStatus.Idle);

        for (int i = 0; i < m_Seats.Length; ++i)
        {
            m_Seats[i].Hide();
        }

        for (int i = 0; i < room.SeatList.Count; ++i)
        {
            m_Seats[room.SeatList[i].Index].SetSeat(room.SeatList[i]);
        }

        if (m_RoomInfo != null)
        {
            m_RoomInfo.SetRoomId(room.roomId);
            m_RoomInfo.SetLoop(room.currentLoop, room.maxLoop);
            m_RoomInfo.SetRoomConfig(room.Setting.ToString());
        }

        List<Seat> seats = room.SeatList;
        for (int i = 0; i < seats.Count; ++i)
        {
            Seat seat = seats[i];

            UIItemSeat itemSeat = GetItemSeatByIndex(seat.Index);
            if (itemSeat == null) continue;
            if (seat.PlayerId != 0)
            {

                itemSeat.Show();
            }
            itemSeat.SetReady(seat.Status == SeatStatus.Ready);
            itemSeat.SetBanker(seat.IsBanker);
            itemSeat.SetGold(seat.Gold);
            itemSeat.SetNickName(seat.Nickname);
        }
    }

    public void Enter(Seat seat)
    {
        UIItemSeat item = GetItemSeatByIndex(seat.Index);
        item.Show();
        item.SetSeat(seat);
    }

    public void Leave(int seatIndex)
    {
        UIItemSeat item = GetItemSeatByIndex(seatIndex);
        item.Hide();
    }

    public void Ready(Seat seat)
    {
        if (seat.IsPlayer)
        {
            m_ButtonReady.SafeSetActive(false);
        }
        UIItemSeat item = GetItemSeatByIndex(seat.Index);
        item.SetReady(seat.Status == SeatStatus.Ready);
    }

    public void Begin(Room room)
    {
        for (int i = 0; i < m_Seats.Length; ++i)
        {
            Seat seat = room.SeatList[i];
            UIItemSeat itemSeat = GetItemSeatByIndex(seat.Index);
            if (itemSeat == null) continue;
            itemSeat.SetReady(seat.Status == SeatStatus.Ready);
            itemSeat.SetBanker(seat.IsBanker);
        }
    }

    public void Draw(Seat seat)
    {
        UIItemSeat itemSeat = GetItemSeatByIndex(seat.Index);
        if (itemSeat == null) return;
        itemSeat.SetOperating(true);
    }

    public void Discard(Seat seat)
    {
        UIItemSeat itemSeat = GetItemSeatByIndex(seat.Index);
        if (itemSeat == null) return;
        itemSeat.SetOperating(false);
    }

    public void AskOperation(List<List<Mahjong>> chiList, List<Mahjong> pengList, List<List<Mahjong>> gangList, bool isHu, bool isZiMo)
    {
        Operator.Show(chiList, pengList, gangList, isHu, isZiMo);
        m_TingTip.Close();
    }

    public void Operation(Seat seat)
    {
        if (seat.IsPlayer)
        {
            Operator.Close();
        }
    }

    public void CloseOperator()
    {
        Operator.Close();
    }

    public void ShowTingTip(List<Mahjong> lst)
    {
        if (m_TingTip == null) return;
        m_TingTip.SafeSetActive(true);
        m_TingTip.ShowTip(lst);
    }

    public void CloseTingTip()
    {
        if (m_TingTip == null) return;
        m_TingTip.Close();
    }

    protected override void OnBtnClick(GameObject go)
    {
        base.OnBtnClick(go);

        if (go == m_ButtonReady.gameObject)
        {
            MahjongService.Instance.ClientSendReady();
        }
        else if (go == m_BtnLeave.gameObject)
        {
            MahjongService.Instance.ClientSendLeaveRoom();
        }
    }

    public void ShowSettle(Room room)
    {
        //if (m_SettleView == null) return;

        //if (room == null) return;
        //m_SettleView.Show();
        //m_SettleView.SetUI(room);
    }

    public void CloseSettle()
    {
        //if (m_SettleView == null) return;
        //m_SettleView.Hide();
    }

    public void ShowResult(Room room)
    {
        //if (m_ResultView == null) return;
        //m_ResultView.Show();
        //m_ResultView.SetUI(room);
    }

    public void CloseResult()
    {
        //if (m_ResultView == null) return;
        //m_ResultView.Hide();
    }

    public void ChangeRoomInfo(Room room)
    {
        if (room == null) return;
        m_RoomInfo.SetRoomId(room.roomId);
        m_RoomInfo.SetLoop(room.currentLoop, room.maxLoop);
        m_RoomInfo.SetRoomConfig(room.Setting.ToString());
    }

    public void ChangeSeatInfo(Seat seat)
    {
        if (seat == null) return;
        if (seat.IsPlayer)
        {
            m_ButtonReady.SafeSetActive(seat.Status == SeatStatus.Idle);
        }
        UIItemSeat itemSeat = m_Seats[seat.Index];
        if (seat.PlayerId > 0)
        {
            itemSeat.Show();
        }
        else
        {
            itemSeat.Hide();
        }
        itemSeat.SetReady(seat.Status == SeatStatus.Ready);
        itemSeat.SetBanker(seat.IsBanker);
        itemSeat.SetGold(seat.Gold);
        itemSeat.SetNickName(seat.Nickname);
    }

    public void SetCountdown(long serverTime, bool isPlayer)
    {
        if (m_TimeTip == null) return;
        if (serverTime == 0)
        {
            m_TimeTip.SetTime(0, isPlayer);
            return;
        }
        //int s = GlobalInit.Instance.GetSecond(serverTime);
        //m_TimeTip.Show();
        //m_TimeTip.SetTime(s, isPlayer);
    }

    private UIItemSeat GetItemSeatByIndex(int index)
    {
        for (int i = 0; i < m_Seats.Length; ++i)
        {
            if (m_Seats[i].SeatIndex == index)
            {
                return m_Seats[i];
            }
        }
        return null;
    }
}
