//===================================================
//Author      : DRB
//CreateTime  ：2021/4/6 3:21:13
//Description ：
//===================================================
using DrbFramework.Internal;
using DrbFramework.Procedure;
using DrbFramework.Scene;
using DrbFramework.UI;
using System.Collections.Generic;
using UnityEngine;

public class MahjongProcedure : Procedure
{
    private MahjongForm m_MahjongForm;
    private MahjongProxy m_Proxy;
    private MahjongLogic m_Logic;

    public override void OnEnter(object userData)
    {
        base.OnEnter(userData);
        MahjongService.Instance.AddListener();
        DrbComponent.SceneSystem.OnSceneLoaded += OnSceneLoaded;

#if ASSETBUNDLE
        DrbComponent.ResourceSystem.LoadAssetBundle("Downloads/Scenes/MahjongScene.unity", DrbFramework.Resource.LoadMode.Persistent);
#endif
        DrbComponent.SceneSystem.AddSceneAsync("MahjongScene");
    }

    public override void OnUpdate(float elapseSeconds, float realElapseSeconds)
    {
        base.OnUpdate(elapseSeconds, realElapseSeconds);
    }

    public override void OnLeave()
    {
        base.OnLeave();

        DrbComponent.SceneSystem.UnloadScene("MahjongScene");
        if (m_MahjongForm != null)
        {
            DrbComponent.UISystem.CloseForm(m_MahjongForm);
        }

        MahjongService.Instance.RemoveListener();
        DrbComponent.SceneSystem.OnSceneLoaded -= OnSceneLoaded;

        DrbComponent.AudioSystem.StopAllAudios();
    }

    private void OnSceneLoaded(string sceneName, LoadSceneMode mode)
    {
        if (sceneName.Equals("MahjongScene"))
        {
            DrbComponent.UISystem.OpenFormAsync("MahjongForm", "BackGround", (IUIForm form) =>
            {
                m_MahjongForm = (MahjongForm)form;
                m_MahjongForm.Operator.OnOperatorClick = OnOperatorClick;

                m_Logic = Object.FindObjectOfType<MahjongLogic>();
                m_Logic.OnDoubleClickMahjong = OnMahjongDoubleClick;
                m_Logic.OnSelectMahjong = OnMahjongClick;
                MahjongManager.Instance.Init();
                MahjongService.Instance.ClientSendRoomInfo();

                DrbComponent.AudioSystem.PlayMusic("bgm_mahjong");
            });
        }
    }

    private void OnMahjongClick(Mahjong mahjong)
    {
        if (m_Proxy == null) return;
        Room room = m_Proxy.Room;
        if (room == null) return;
        Seat playerSeat = room.PlayerSeat;
        if (playerSeat == null) return;

        if (room.PlayerSeat.Status == SeatStatus.Discard && room.RoomStatus == RoomStatus.Gaming)
        {
            m_MahjongForm.ShowTingTip(m_Proxy.GetHu(mahjong));
        }
    }

    private void OnMahjongDoubleClick(Mahjong mahjong)
    {
        if (m_Proxy == null) return;
        Room room = m_Proxy.Room;
        if (room == null) return;
        Seat playerSeat = room.PlayerSeat;
        if (playerSeat == null) return;

        if (room.PlayerSeat.Status == SeatStatus.Discard && room.RoomStatus == RoomStatus.Gaming)
        {
            MahjongService.Instance.ClientSendPass();
            MahjongService.Instance.ClientSendPlayMahjong(mahjong);
            m_MahjongForm.ShowTingTip(m_Proxy.GetHu(mahjong));
        }
    }

    private void OnOperatorClick(OperationType type, List<Mahjong> lst)
    {
        if (m_Proxy == null) return;

        if (type == OperationType.Cancel || type == OperationType.Pass)
        {
            m_Logic.ClearSelectedMahjongs();
            if (type == OperationType.Pass)
            {
                MahjongService.Instance.ClientSendPass();
            }
        }
        else
        {
            m_MahjongForm.CloseOperator();
            if (type == OperationType.Win)
            {
                if (m_Proxy.Room.PlayerSeat.HitMahjong != null)
                {
                    type = OperationType.WinBySelf;
                }
            }
            MahjongService.Instance.ClientSendOperate(type, lst);
        }
    }

    public void Init(Room room)
    {
        m_Proxy = new MahjongProxy(room);
        m_Logic.Init(room);
        m_MahjongForm.Init(room);

        if (room.PlayerSeat.Status == SeatStatus.Operate)
        {
            if (room.AskMahjongGroup != null && room.AskMahjongGroup.Count > 0)
            {
                AskOperation(room.AskMahjongGroup);
            }
        }
        m_Proxy.CheckTing();
        m_Logic.CheckTing(room.PlayerSeat);
        m_MahjongForm.ShowTingTip(m_Proxy.Room.PlayerSeat.TingList);
    }

    public void Enter(Seat seat)
    {
        if (m_Proxy == null) return;
        m_Proxy.EnterRoom(seat);
        seat = m_Proxy.GetSeatByPlayerId(seat.PlayerId);
        m_Logic.Enter(seat);
        m_MahjongForm.Enter(seat);
    }

    public void Leave(int playerId)
    {
        if (m_Proxy == null) return;
        Seat seat = m_Proxy.GetSeatByPlayerId(playerId);
        int seatIndex = seat.Index;
        m_Proxy.LeaveRoom(playerId);

        m_MahjongForm.Leave(seatIndex);

        if (seat.IsPlayer)
        {
            ChangeState<MainMenuProcedure>();
        }
    }

    public void Ready(int playerId)
    {
        if (m_Proxy == null) return;
        m_Proxy.Ready(playerId);
        Seat seat = m_Proxy.GetSeatByPlayerId(playerId);
        m_MahjongForm.Ready(seat);
        m_Logic.Ready(seat);
    }

    public void Disband()
    {
        if (m_Proxy == null) return;
        m_Proxy.Disband(true);
    }

    public void Begin(Room room)
    {
        if (m_Proxy == null) return;
        m_Proxy.Begin(room);
        m_MahjongForm.Begin(m_Proxy.Room);
        m_Logic.Begin(m_Proxy.Room);
    }

    public void Draw(int playerId, Mahjong mahjong, bool isFromLast)
    {
        if (m_Proxy == null) return;
        m_Proxy.Draw(playerId, mahjong);
        Seat seat = m_Proxy.GetSeatByPlayerId(playerId);
        m_Logic.Draw(seat, seat.HitMahjong, isFromLast);
        m_MahjongForm.Draw(seat);

        if (seat.IsPlayer)
        {
            m_Proxy.CheckTing();
            m_Logic.CheckTing(seat);
            m_MahjongForm.ShowTingTip(m_Proxy.Room.PlayerSeat.TingList);
        }
    }

    public void Discard(int playerId, Mahjong mahjong)
    {
        if (m_Proxy == null) return;
        m_Proxy.Discard(playerId, mahjong);
        Seat seat = m_Proxy.GetSeatByPlayerId(playerId);
        m_Logic.Discard(seat, mahjong);
        m_MahjongForm.Discard(seat);
    }

    public void AskOperation(List<MahjongGroup> lst)
    {
        if (m_Proxy == null) return;
        m_Proxy.AskOperation(lst);

        bool canHu = false;
        List<Mahjong> lstPong = null;
        List<List<Mahjong>> lstKong = null;
        List<List<Mahjong>> lstChow = null;

        for (int i = 0; i < lst.Count; ++i)
        {
            OperationType type = lst[i].CombinationType;
            DrbFramework.Log.Info("The player can " + type);
            Mahjong mahjong = null;
            if (lst[i].MahjongList.Count > 0)
            {
                mahjong = lst[i].MahjongList[0];
            }
            switch (type)
            {
                case OperationType.Pong:
                    lstPong = m_Proxy.GetPeng(mahjong);
                    break;
                case OperationType.Kong:
                    if (mahjong == null)
                    {
                        lstKong = m_Proxy.GetAnGang();
                        List<Mahjong> lstBuGangs = m_Proxy.GetBuGang();
                        for (int j = 0; j < lstBuGangs.Count; ++j)
                        {
                            lstKong.Add(new List<Mahjong> { lstBuGangs[j] });
                        }
                    }
                    else
                    {
                        lstKong = new List<List<Mahjong>>();
                        List<Mahjong> lstMingGang = m_Proxy.GetMingGang(mahjong);
                        if (lstMingGang != null && lstMingGang.Count > 0)
                        {
                            lstKong.Add(lstMingGang);
                        }
                    }
                    break;
                case OperationType.Chow:
                    lstChow = m_Proxy.GetChi(mahjong);
                    break;
                case OperationType.Win:
                    canHu = true;
                    break;
                case OperationType.WinBySelf:
                    canHu = true;
                    break;
            }
        }

        m_MahjongForm.AskOperation(lstChow, lstPong, lstKong, canHu);
    }

    public void Operation(int playerId, OperationType type, int subType, List<Mahjong> lst)
    {
        if (m_Proxy == null) return;
        m_Proxy.OperateMahjong(type, playerId, subType, lst);
        Seat seat = m_Proxy.GetSeatByPlayerId(playerId);
        m_MahjongForm.Operation(seat);
        m_Logic.Operation(seat, seat.UsedMahjongGroups.Count > 0 ? seat.UsedMahjongGroups[seat.UsedMahjongGroups.Count - 1] : null);

        if (seat.IsPlayer)
        {
            m_Proxy.CheckTing();
            m_Logic.CheckTing(seat);
            m_MahjongForm.ShowTingTip(m_Proxy.Room.PlayerSeat.TingList);
        }
    }

    public void Pass()
    {
        if (m_Proxy == null) return;
        m_Proxy.Pass();
    }

    public void Settle(Room room)
    {
        if (m_Proxy == null) return;
        m_Proxy.Settle(room);

        m_Logic.Settle(m_Proxy.Room);
        m_MahjongForm.Settle(m_Proxy.Room);
    }

    public void Result(Room room)
    {
        if (m_Proxy == null) return;
        m_Proxy.Result(room);
    }
}
