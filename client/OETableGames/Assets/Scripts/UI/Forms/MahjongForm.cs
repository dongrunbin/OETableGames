//===================================================
//Author      : DRB
//CreateTime  ：2021/4/6 3:22:10
//Description ：
//===================================================
using DrbFramework.Extensions;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class MahjongForm : FormBase
{
    [SerializeField]
    private UIItemSeat[] m_Seats;
    [SerializeField]
    private GameObject[] m_Animations;
    [SerializeField]
    private UIItemRoomInfo m_RoomInfo;
    [SerializeField]
    private UIItemOperator m_Operator;
    [SerializeField]
    private UIItemTingTip m_TingTip;
    [SerializeField]
    private Button m_TingTipSwitch;
    [SerializeField]
    private Button m_ButtonReady;
    [SerializeField]
    private UIItemTimeTip m_TimeTip;
    //[SerializeField]
    //private UIDisbandView m_DisbandView;
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

            m_RoomInfo.SetBaseScore(room.BaseScore);
            m_RoomInfo.SetLoop(room.currentLoop, room.maxLoop);
            m_RoomInfo.SetRoomConfig(room.Setting.ToString());
        }

        CloseDisband();
        List<Seat> seats = room.SeatList;
        for (int i = 0; i < seats.Count; ++i)
        {
            if (seats[i].DisbandState == DisbandStatus.Apply)
            {
                ShowDisband(room);
            }
        }
        for (int i = 0; i < seats.Count; ++i)
        {
            Seat seat = seats[i];

            UIItemSeat itemSeat = GetItemSeatByIndex(seat.Index);
            if (itemSeat == null) continue;
            if (seat.PlayerId > 0)
            {

                itemSeat.Show();
            }
            itemSeat.SetReady(seat.Status == SeatStatus.Ready);
            itemSeat.SetBanker(seat.IsBanker);
            itemSeat.SetGold(seat.Gold);
            itemSeat.SetNickName(seat.Nickname);
        }
    }


    public void CloseOperator()
    {
        m_Operator.SafeSetActive(false);
    }

    public void ShowTingTip(List<Mahjong> lst, Dictionary<int, int> pokerCount)
    {
        if (m_TingTip == null) return;
        m_TingTip.SafeSetActive(true);
        m_TingTip.ShowTip(lst, pokerCount);
    }

    public void CloseTingTip()
    {
        if (m_TingTip == null) return;
        m_TingTip.Close();
    }

    protected override void OnBtnClick(GameObject go)
    {
        base.OnBtnClick(go);

        switch (go.name)
        {
            //case GameEvent.btnGameViewDisband://解散
            //    SendNotification(GameEvent.btnGameViewDisband);
            //    break;
            //case GameEvent.btnGameViewGameRule://规则
            //    SendNotification(GameEvent.btnGameViewGameRule);
            //    break;
            //case GameEvent.btnGameViewSetting://设置
            //    SendNotification(GameEvent.btnGameViewSetting);
            //    break;
            //case GameEvent.btnGameViewLeave://离开
            //    SendNotification(GameEvent.btnGameViewLeave);
            //    break;
            //case GameEvent.BtnGameViewReady://准备
            //    SendNotification(GameEvent.BtnGameViewReady);
            //    break;
        }
    }

    public void ShowDisband(Room room)
    {
        //m_DisbandView.Show();
        //m_DisbandView.SetUI(room);
    }

    public void CloseDisband()
    {
        //m_DisbandView.Hide();
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
        m_RoomInfo.SetBaseScore(room.BaseScore);
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
        m_TimeTip.Show();
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
