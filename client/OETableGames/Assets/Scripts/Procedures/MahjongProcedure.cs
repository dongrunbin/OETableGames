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

    public void Init(Room room)
    {
        m_Proxy = new MahjongProxy(room);
        m_Logic.Init(room);
        m_MahjongForm.Init(room);

        if (room.PlayerSeat.Status == SeatStatus.Operate)
        {
            if (room.AskPokerGroup != null && room.AskPokerGroup.Count > 0)
            {
                AskOperation(room.AskPokerGroup);
            }
        }
    }

    public void Enter(Seat seat)
    {
        if (m_Proxy == null) return;
        m_Proxy.EnterRoom(seat);

        m_Logic.Enter(seat);
        m_MahjongForm.Enter(seat);
    }

    public void Leave(int playerId)
    {
        if (m_Proxy == null) return;
        m_Proxy.ExitRoom(playerId);
    }

    public void Ready(int playerId)
    {
        if (m_Proxy == null) return;
        m_Proxy.Ready(playerId);
        Seat seat = m_Proxy.GetSeatByPlayerId(playerId);
        DrbFramework.Log.Info(seat.Status);
        DrbFramework.Log.Info(seat.IsPlayer);
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
        m_Logic.Draw(seat, mahjong, isFromLast);
        m_MahjongForm.Draw(seat);
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
        List<Mahjong> lstPeng = null;
        List<List<Mahjong>> lstGangs = null;
        List<List<Mahjong>> lstChi = null;
        bool isZimo = false;

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
                case OperationType.Peng:
                    lstPeng = m_Proxy.GetPeng(mahjong);
                    break;
                case OperationType.Gang:
                    if (mahjong == null)
                    {
                        lstGangs = m_Proxy.GetAnGang();
                        List<Mahjong> lstBuGangs = m_Proxy.GetBuGang();
                        for (int j = 0; j < lstBuGangs.Count; ++j)
                        {
                            lstGangs.Add(new List<Mahjong> { lstBuGangs[j] });
                        }
                    }
                    else
                    {
                        lstGangs = new List<List<Mahjong>>();
                        List<Mahjong> lstMingGang = m_Proxy.GetMingGang(mahjong);
                        if (lstMingGang != null && lstMingGang.Count > 0)
                        {
                            lstGangs.Add(lstMingGang);
                        }
                    }
                    break;
                case OperationType.Chi:
                    lstChi = m_Proxy.GetChi(mahjong);
                    break;
                case OperationType.Hu:
                    canHu = true;
                    break;
                case OperationType.ZiMo:
                    canHu = true;
                    isZimo = true;
                    break;
            }
        }

        m_MahjongForm.AskOperation(lstChi, lstPeng, lstGangs, canHu, isZimo);
    }

    public void Operation(int playerId, OperationType type, int subType, List<Mahjong> lst)
    {
        if (m_Proxy == null) return;
        m_Proxy.OperatePoker(type, playerId, subType, lst);
        Seat seat = m_Proxy.GetSeatByPlayerId(playerId);
        m_MahjongForm.Operation(seat);
        m_Logic.Operation(seat);
    }

    public void Pass()
    {
        if (m_Proxy == null) return;
        m_Proxy.Pass();
    }

    public void Settle(Room room)
    {
        if (room == null) return;
    }

    public void Result(Room room)
    {
        if (m_Proxy == null) return;
        m_Proxy.GameOver(room);
    }

    public override void OnEnter(object userData)
    {
        base.OnEnter(userData);
        MahjongService.Instance.AddListener();
        DrbComponent.SceneSystem.OnSceneLoaded += OnSceneLoaded;
        DrbComponent.SceneSystem.LoadSceneAsync("MahjongScene");

        //m_AI = new MahjongAI(this);
        //AssetBundle ab = (AssetBundle)DrbComponent.ResourceSystem.LoadAssetBundle("Downloads/Scenes/MahjongScene.scene", DrbFramework.Resource.LoadMode.Editor);
        //DrbComponent.SceneSystem.LoadScene("MahjongScene");
    }

    public override void OnLeave()
    {
        base.OnLeave();
        MahjongService.Instance.RemoveListener();
        DrbComponent.SceneSystem.OnSceneLoaded -= OnSceneLoaded;
    }

    private void OnSceneLoaded(string sceneName, LoadSceneMode mode)
    {
        if (sceneName.Equals("MahjongScene"))
        {
            DrbComponent.UISystem.OpenFormAsync("UI/Forms/MahjongForm", "BackGround", (IUIForm form) =>
            {
                m_MahjongForm = (MahjongForm)form;
                m_MahjongForm.Operator.OnOperatorClick = OnOperatorClick;

                m_Logic = Object.FindObjectOfType<MahjongLogic>();
                m_Logic.OnDoubleClickMahjong = OnDoubleClick;
                MahjongManager.Instance.Init();
                MahjongService.Instance.ClientSendRoomInfo();
            });
        }
    }

    private void OnDoubleClick(Mahjong mahjong)
    {
        if (m_Proxy == null) return;
        Room room = m_Proxy.Room;
        if (room == null) return;
        Seat playerSeat = room.PlayerSeat;
        if (playerSeat == null) return;

        if (room.PlayerSeat.Status == SeatStatus.PlayPoker && room.RoomStatus == RoomStatus.Gaming)
        {
            MahjongService.Instance.ClientSendPass();
            MahjongService.Instance.ClientSendPlayPoker(mahjong);
            m_MahjongForm.ShowTingTip(m_Proxy.GetHu(mahjong), room.PokerCount);
        }
    }

    private void OnOperatorClick(OperationType type, List<Mahjong> lst)
    {
        if (m_Proxy == null) return;

        if (type == OperationType.Cancel || type == OperationType.Pass)
        {
            m_Logic.ClearSelectedMahjongs();
        }
        else
        {
            m_MahjongForm.CloseOperator();
            MahjongService.Instance.ClientSendOperate(type, lst);
        }
    }
}
