//===================================================
//Author      : DRB
//CreateTime  ：2021/4/6 3:21:13
//Description ：
//===================================================
using DG.Tweening;
using DrbFramework.Extensions;
using DrbFramework.Internal;
using DrbFramework.Procedure;
using DrbFramework.UI;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.EventSystems;

public class MahjongProcedure : Procedure
{
    private MahjongForm m_MahjongForm;
    private MahjongService m_Service;
    private MahjongProxy m_Proxy;
    private MahjongLogic m_Logic;

    public void Init(Room room)
    {
        if (m_Proxy == null) return;
        m_Proxy = new MahjongProxy(room);

        m_Logic.Init(room);
        m_MahjongForm.Init(room);
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
    }

    public void Draw(int playerId, Mahjong mahjong, bool isFromLast)
    {
        if (m_Proxy == null) return;
        m_Proxy.Draw(playerId, mahjong);
    }

    public void Discard(int playerId, Mahjong mahjong)
    {
        if (m_Proxy == null) return;
        m_Proxy.Discard(playerId, mahjong);
    }

    public void AskOperation(List<MahjongGroup> lst)
    {
        if (m_Proxy == null) return;
        m_Proxy.AskOperation(lst);
    }

    public void Operation(int playerId, OperationType type, int subType, List<Mahjong> lst)
    {
        if (m_Proxy == null) return;
        m_Proxy.OperatePoker(type, playerId, subType, lst);
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
        DrbComponent.SceneSystem.LoadScene("MahjongScene");
        DrbComponent.UISystem.OpenFormAsync("Downloads/UI/Forms/MahjongForm", "BackGround", (IUIForm form) =>
        {
            m_MahjongForm = (MahjongForm)form;
        });

        //m_AI = new MahjongAI(this);
        //AssetBundle ab = (AssetBundle)DrbComponent.ResourceSystem.LoadAssetBundle("Downloads/Scenes/MahjongScene.scene", DrbFramework.Resource.LoadMode.Editor);
        //DrbComponent.SceneSystem.LoadScene("MahjongScene");
        m_Logic = GameObject.Find("MahjongLogic").GetComponent<MahjongLogic>();
        m_Logic.OnDoubleClickMahjong = OnDoubleClick;
        m_Service = new MahjongService(this);
        MahjongManager.Instance.Init();
        m_Service.ClientSendRoomInfo();
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
            m_Service.ClientSendPass();
            m_Service.ClientSendPlayPoker(mahjong);
            m_MahjongForm.ShowTingTip(m_Proxy.GetHu(mahjong), room.PokerCount);
        }
    }
}
