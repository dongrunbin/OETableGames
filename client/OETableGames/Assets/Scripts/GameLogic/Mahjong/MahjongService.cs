//===================================================
//Author      : DRB
//CreateTime  ：2021/4/14 3:38:55
//Description ：
//===================================================
using DrbFramework.Event;
using DrbFramework.Internal;
using System;
using System.Collections.Generic;

public class MahjongService : Singleton<MahjongService>
{
    private MahjongProcedure m_Procedure;
    public MahjongService()
    {
        m_Procedure = DrbComponent.ProcedureSystem.GetProcedure<MahjongProcedure>();
    }

    public void AddListener()
    {
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.System_C2S_HeartBeatProto - 1, OnConnected);
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.Game_S2C_EnterRoomProto, OnServerBroadcastEnter);//服务器广播玩家进入消息
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.Game_S2C_LeaveRoomProto, OnServerBroadcastLeave);//服务器广播玩家离开消息
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.Game_S2C_ReadyProto, OnServerBroadcastReady);//服务器广播玩家准备消息
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.Game_S2C_DisbandProto, OnServerBroadcastDisband);//服务器广播解散房间
        //DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.S2C_Game_RoomExpireProto, OnServerReturnRoomExpire);//服务器返回房间过期
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.Mahjong_S2C_RoomInfoProto, OnServerReturnRoomInfo);//服务器返回房间信息
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.Mahjong_S2C_ResultProto, OnServerReturnResult);//服务器返回结果消息
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.Mahjong_S2C_GameBeginProto, OnServerBroadcastBegin);//服务器广播开局消息
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.Mahjong_S2C_DrawProto, OnServerBroadcastDrawMahjong);//服务器广播摸牌消息
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.Mahjong_S2C_DiscardProto, OnServerBroadcastPlayMahjong);//服务器广播出牌消息
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.Mahjong_S2C_AskOperationProto, OnServerPushAskOperation);//服务器询问是否吃碰杠胡
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.Mahjong_S2C_OperateProto, OnServerBroadcastOperation);//服务器广播吃碰杠胡消息
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.Mahjong_S2C_PassProto, OnServerReturnPass);//服务器返回过
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.Mahjong_S2C_SettleProto, OnServerBroadcastSettle);//服务器广播结算
    }

    public void RemoveListener()
    {
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.System_C2S_HeartBeatProto - 1, OnConnected);
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.Game_S2C_EnterRoomProto, OnServerBroadcastEnter);//服务器广播玩家进入消息
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.Game_S2C_LeaveRoomProto, OnServerBroadcastLeave);//服务器广播玩家离开消息
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.Game_S2C_ReadyProto, OnServerBroadcastReady);//服务器广播玩家准备消息
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.Game_S2C_DisbandProto, OnServerBroadcastDisband);//服务器广播解散房间
        //DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.S2C_Game_RoomExpireProto, OnServerReturnRoomExpire);//服务器返回房间过期
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.Mahjong_S2C_RoomInfoProto, OnServerReturnRoomInfo);//服务器返回房间信息
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.Mahjong_S2C_ResultProto, OnServerReturnResult);//服务器返回结果消息
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.Mahjong_S2C_GameBeginProto, OnServerBroadcastBegin);//服务器广播开局消息
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.Mahjong_S2C_DrawProto, OnServerBroadcastDrawMahjong);//服务器广播摸牌消息
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.Mahjong_S2C_DiscardProto, OnServerBroadcastPlayMahjong);//服务器广播出牌消息
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.Mahjong_S2C_AskOperationProto, OnServerPushAskOperation);//服务器询问是否吃碰杠胡
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.Mahjong_S2C_OperateProto, OnServerBroadcastOperation);//服务器广播吃碰杠胡消息
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.Mahjong_S2C_PassProto, OnServerReturnPass);//服务器返回过
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.Mahjong_S2C_SettleProto, OnServerBroadcastSettle);//服务器广播结算
    }

    private void OnServerReturnOperateWait(object sender, EventArgs<int> args)
    {
        throw new NotImplementedException();
    }

    private void OnConnected(object sender, EventArgs<int> args)
    {
        ClientSendRoomInfo();
    }

    public void ClientSendRoomInfo()
    {
        DrbComponent.NetworkSystem.Send(new Game_C2S_QueryRoomInfoProto());
    }

    public void ClientSendLeaveRoom()
    {
        Game_C2S_LeaveRoomProto proto = new Game_C2S_LeaveRoomProto();
        DrbComponent.NetworkSystem.Send(proto);
    }

    public void ClientSendReady()
    {
        Game_C2S_ReadyProto proto = new Game_C2S_ReadyProto();
        DrbComponent.NetworkSystem.Send(proto);
    }

    public void ClientSendApplyDisbandRoom()
    {
        Game_C2S_ApplyDisbandProto proto = new Game_C2S_ApplyDisbandProto();
        proto.disbandStatus = (byte)DisbandStatus.Apply;
        DrbComponent.NetworkSystem.Send(proto);
    }

    public void ClientSendDisbandRoom(bool isAgree)
    {
        Game_C2S_ApplyDisbandProto proto = new Game_C2S_ApplyDisbandProto();
        proto.disbandStatus = isAgree ? (byte)DisbandStatus.Agree : (byte)DisbandStatus.Refuse;
        DrbComponent.NetworkSystem.Send(proto);
    }

    public void ClientSendPlayMahjong(Mahjong mahjong)
    {
        if (mahjong == null) return;
        Mahjong_C2S_DiscardProto proto = new Mahjong_C2S_DiscardProto();
        proto.index = mahjong.index;
        DrbComponent.NetworkSystem.Send(proto);
    }

    public void ClientSendPass()
    {
        DrbComponent.NetworkSystem.Send(new Mahjong_C2S_PassProto());
    }

    public void ClientSendOperate(OperationType type, List<Mahjong> mahjongs)
    {
        switch (type)
        {
            case OperationType.Pass:
                ClientSendPass();
                return;
        }
        Mahjong_C2S_OperateProto proto = new Mahjong_C2S_OperateProto();
        proto.typeId = (byte)type;
        if (mahjongs != null)
        {
            proto.indexList = new List<int>();
            for (int i = 0; i < mahjongs.Count; ++i)
            {
                proto.indexList.Add(mahjongs[i].index);
            }
        }
        DrbComponent.NetworkSystem.Send(proto);
    }

    public void ClientSendAddRobot(int pos)
    {
        Mahjong_C2S_AddRobotProto proto = new Mahjong_C2S_AddRobotProto();
        proto.pos = pos;
        DrbComponent.NetworkSystem.Send(proto);
    }








    private void OnServerBroadcastReady(object sender, EventArgs<int> args)
    {
        Game_S2C_ReadyProto proto = new Game_S2C_ReadyProto(((NetworkEventArgs)args).Data);

        m_Procedure.Ready(proto.playerId);
    }

    private void OnServerBroadcastEnter(object sender, EventArgs<int> args)
    {
        Game_S2C_EnterRoomProto proto = new Game_S2C_EnterRoomProto(((NetworkEventArgs)args).Data);

        Seat seat = new Seat();
        seat.PlayerId = proto.playerId;
        seat.Gold = proto.gold;
        seat.Avatar = proto.avatar;
        seat.Gender = proto.gender;
        seat.Nickname = proto.nickname;
        seat.Pos = proto.pos;
        seat.InitGold = proto.originalGold;

        m_Procedure.Enter(seat);
    }

    private void OnServerBroadcastLeave(object sender, EventArgs<int> args)
    {
        Game_S2C_LeaveRoomProto proto = new Game_S2C_LeaveRoomProto(((NetworkEventArgs)args).Data);

        m_Procedure.Leave(proto.playerId);
    }

    private void OnServerBroadcastDisband(object sender, EventArgs<int> args)
    {
        m_Procedure.Disband();
    }

    private void OnServerReturnRoomExpire(object sender, EventArgs<int> args)
    {

    }

    private void OnServerBroadcastBegin(object sender, EventArgs<int> args)
    {
        Mahjong_S2C_GameBeginProto proto = new Mahjong_S2C_GameBeginProto(((NetworkEventArgs)args).Data);

        Room room = new Room();
        room.currentLoop = proto.gamesCount;
        room.MahjongTotal = proto.mahjongTotal;
        room.MahjongAmount = proto.mahjongAmount;

        room.SeatList = new List<Seat>();
        for (int i = 0; i < proto.seatList.Count; ++i)
        {
            Seat entity = new Seat();
            Mahjong_S2C_GameBeginProto.Seat protoSeat = proto.seatList[i];
            entity.Pos = protoSeat.pos;
            entity.PlayerId = protoSeat.playerId;
            for (int j = 0; j < protoSeat.mahjongsList.Count; ++j)
            {
                Mahjong_S2C_GameBeginProto.Mahjong protoMahjong = protoSeat.mahjongsList[j];
                entity.MahjongList.Add(new Mahjong(protoMahjong.index, protoMahjong.color, protoMahjong.number));
            }
            for (int j = 0; j < protoSeat.universalMahjongsList.Count; ++j)
            {
                Mahjong_S2C_GameBeginProto.Mahjong op_mahjong = protoSeat.universalMahjongsList[j];
                entity.UniversalList.Add(new Mahjong(op_mahjong.index, op_mahjong.color, op_mahjong.number));
            }
            entity.MahjongAmount = protoSeat.mahjongAmount;
            entity.IsBanker = protoSeat.isBanker;

            room.SeatList.Add(entity);
        }

        room.FirstDice = new Dice()
        {
            diceA = proto.dicesList[0],
            diceB = proto.dicesList[1],
            seatPos = proto.dicePosList[0],
        };
        room.SecondDice = new Dice()
        {
            diceA = proto.dicesList[2],
            diceB = proto.dicesList[3],
            seatPos = proto.dicePosList[1],
        };

        m_Procedure.Begin(room);
    }

    private void OnServerBroadcastDrawMahjong(object sender, EventArgs<int> args)
    {
        Mahjong_S2C_DrawProto proto = new Mahjong_S2C_DrawProto(((NetworkEventArgs)args).Data);
        Mahjong mahjong = new Mahjong(proto.index, proto.color, proto.number);
        m_Procedure.Draw(proto.playerId, mahjong, false);
    }

    private void OnServerBroadcastPlayMahjong(object sender, EventArgs<int> args)
    {
        Mahjong_S2C_DiscardProto proto = new Mahjong_S2C_DiscardProto(((NetworkEventArgs)args).Data);
        Mahjong mahjong = new Mahjong(proto.index, proto.color, proto.number);
        m_Procedure.Discard(proto.playerId, mahjong);
    }

    private void OnServerPushAskOperation(object sender, EventArgs<int> args)
    {
        Mahjong_S2C_AskOperationProto proto = new Mahjong_S2C_AskOperationProto(((NetworkEventArgs)args).Data);
        List<MahjongGroup> groups = null;
        int askLeng = proto.askMahjongGroupsList.Count;
        if (askLeng > 0)
        {
            groups = new List<MahjongGroup>();
            for (int i = 0; i < askLeng; ++i)
            {
                List<Mahjong> lst = new List<Mahjong>();
                Mahjong_S2C_AskOperationProto.MahjongGroup op_Group = proto.askMahjongGroupsList[i];
                for (int j = 0; j < op_Group.mahjongsList.Count; ++j)
                {
                    Mahjong_S2C_AskOperationProto.Mahjong op_mahjong = op_Group.mahjongsList[j];
                    lst.Add(new Mahjong(op_mahjong.index, op_mahjong.color, op_mahjong.number));
                }
                MahjongGroup combination = new MahjongGroup((OperationType)op_Group.typeId, op_Group.subTypeId, lst, 0);
                groups.Add(combination);
            }
        }

        m_Procedure.AskOperation(groups);
    }

    private void OnServerBroadcastOperation(object sender, EventArgs<int> args)
    {
        Mahjong_S2C_OperateProto proto = new Mahjong_S2C_OperateProto(((NetworkEventArgs)args).Data);

        List<Mahjong> lst = new List<Mahjong>(proto.mahjongsList.Count);
        for (int i = 0; i < proto.mahjongsList.Count; ++i)
        {
            Mahjong_S2C_OperateProto.Mahjong op_mahjong = proto.mahjongsList[i];
            lst.Add(new Mahjong(op_mahjong.index, op_mahjong.color, op_mahjong.number));
        }

        m_Procedure.Operation(proto.playerId, (OperationType)proto.typeId, proto.subTypeId, lst);
    }

    private void OnServerReturnPass(object sender, EventArgs<int> args)
    {
        m_Procedure.Pass();
    }

    private void OnServerBroadcastSettle(object sender, EventArgs<int> args)
    {
        Mahjong_S2C_SettleProto proto = new Mahjong_S2C_SettleProto(((NetworkEventArgs)args).Data);

        Room room = new Room();
        room.SeatList = new List<Seat>();
        for (int i = 0; i < proto.seatsList.Count; ++i)
        {
            Mahjong_S2C_SettleProto.Seat op_seat = proto.seatsList[i];
            Seat seat = new Seat();
            seat.Pos = op_seat.pos;
            seat.PlayerId = op_seat.playerId;
            seat.Settle = op_seat.settle;
            seat.Gold = op_seat.gold;
            seat.isLoser = op_seat.isLoser;
            seat.isWiner = op_seat.isWinner;
            seat.MahjongList.Clear();
            seat.incomesDesc = op_seat.incomesDesc;
            seat.UsedMahjongGroups.Clear();
            for (int j = 0; j < op_seat.usedMahjongGroupList.Count; ++j)
            {
                Mahjong_S2C_SettleProto.MahjongGroup op_combination = op_seat.usedMahjongGroupList[j];
                List<Mahjong> lst = new List<Mahjong>();
                for (int k = 0; k < op_combination.mahjongsList.Count; ++k)
                {
                    Mahjong_S2C_SettleProto.Mahjong op_mahjong = op_combination.mahjongsList[k];
                    lst.Add(new Mahjong(op_mahjong.index, op_mahjong.color, op_mahjong.number));
                }
                MahjongGroup combination = new MahjongGroup((OperationType)op_combination.typeId, (int)op_combination.subTypeId, lst, seat.Pos);
                combination.Sort();
                seat.UsedMahjongGroups.Add(combination);
            }
            for (int j = 0; j < op_seat.mahjongsList.Count; ++j)
            {
                Mahjong_S2C_SettleProto.Mahjong op_Mahjong = op_seat.mahjongsList[j];
                seat.MahjongList.Add(new Mahjong(op_Mahjong.index, op_Mahjong.color, op_Mahjong.number));
            }
            if (op_seat.hitMahjong != null)
            {
                seat.HitMahjong = new Mahjong(op_seat.hitMahjong.index, op_seat.hitMahjong.color, op_seat.hitMahjong.number);
            }
            else
            {
                seat.HitMahjong = null;
            }
            room.SeatList.Add(seat);
        }
        room.RoomStatus = RoomStatus.Settle;
        room.isOver = proto.isOver;

        m_Procedure.Settle(room);
    }

    private void OnServerReturnResult(object sender, EventArgs<int> args)
    {
        Mahjong_S2C_ResultProto proto = new Mahjong_S2C_ResultProto(((NetworkEventArgs)args).Data);

        Room room = new Room();
        room.SeatList = new List<Seat>();
        room.isOver = true;
        for (int i = 0; i < proto.seatsList.Count; ++i)
        {
            Mahjong_S2C_ResultProto.Seat op_result = proto.seatsList[i];
            Seat seat = new Seat();
            seat.Pos = op_result.pos;
            seat.isWiner = op_result.isWinner;
            seat.Gold = op_result.gold;
            room.SeatList.Add(seat);
        }

        m_Procedure.Result(room);
    }

    private void OnServerReturnRoomInfo(object sender, EventArgs<int> args)
    {
        Mahjong_S2C_RoomInfoProto proto = new Mahjong_S2C_RoomInfoProto(((NetworkEventArgs)args).Data);
        Room room = new Room(proto.roomId, 0, proto.settingIdsList)
        {
            BaseScore = proto.baseScore,
            currentLoop = proto.loop,
            MahjongAmount = proto.mahjongAmount,
            MahjongTotal = proto.mahjongTotal,
            roomId = proto.roomId,
            RoomStatus = (RoomStatus)proto.roomStatus,
            maxLoop = proto.maxLoop,
            DisbandTime = proto.dismissTime,
            DisbandTimeMax = (int)proto.dismissMaxTime,
        };
        room.SeatList = new List<Seat>();
        for (int i = 0; i < proto.seatsList.Count; ++i)
        {
            Mahjong_S2C_RoomInfoProto.Seat op_seat = proto.seatsList[i];
            Seat seat = new Seat();
            seat.Pos = op_seat.pos;
            seat.PlayerId = op_seat.playerId;
            seat.IsPlayer = seat.PlayerId == DrbComponent.SettingSystem.GetObject<AccountEntity>("AccountInfo").passportId;
            seat.Gender = op_seat.gender;
            seat.Gold = op_seat.gold;
            seat.Nickname = op_seat.nickname;
            seat.Avatar = op_seat.avatar;
            seat.Status = (SeatStatus)op_seat.status;
            seat.Status = (SeatStatus)op_seat.status;
            seat.playedTimes = op_seat.handCount;
            room.PlayedTimes += seat.playedTimes;
            seat.DisbandState = (DisbandStatus)op_seat.disbandtatus;
            if (op_seat.hitMahjong != null)
            {
                seat.HitMahjong = new Mahjong(op_seat.hitMahjong.index, op_seat.hitMahjong.color, op_seat.hitMahjong.number);
            }
            for (int j = 0; j < op_seat.mahjongsList.Count; ++j)
            {
                Mahjong_S2C_RoomInfoProto.Mahjong protoMahjong = op_seat.mahjongsList[j];
                seat.MahjongList.Add(new Mahjong(protoMahjong.index, protoMahjong.color, protoMahjong.number));
            }
            for (int j = 0; j < op_seat.desktopList.Count; ++j)
            {
                Mahjong_S2C_RoomInfoProto.Mahjong protoMahjong = op_seat.desktopList[j];
                Mahjong mahjong = new Mahjong(protoMahjong.index, protoMahjong.color, protoMahjong.number);
                seat.DeskTopMahjong.Add(mahjong);
            }
            for (int j = 0; j < op_seat.usedMahjongGroupList.Count; ++j)
            {
                Mahjong_S2C_RoomInfoProto.MahjongGroup protoMahjong = op_seat.usedMahjongGroupList[j];
                List<Mahjong> lst = new List<Mahjong>();
                for (int k = 0; k < protoMahjong.mahjongsList.Count; ++k)
                {
                    Mahjong_S2C_RoomInfoProto.Mahjong op_mahjong = protoMahjong.mahjongsList[k];
                    Mahjong mahjong = new Mahjong(op_mahjong.index, op_mahjong.color, op_mahjong.number);
                    lst.Add(mahjong);
                }
                MahjongGroup combination = new MahjongGroup((OperationType)protoMahjong.typeId, protoMahjong.subTypeId, lst, seat.Pos);
                combination.Sort();
                seat.UsedMahjongGroups.Add(combination);
            }
            for (int j = 0; j < op_seat.universalList.Count; ++j)
            {
                Mahjong_S2C_RoomInfoProto.Mahjong op_mahjong = op_seat.universalList[j];
                seat.UniversalList.Add(new Mahjong(op_mahjong.index, op_mahjong.color, op_mahjong.number));
            }
            seat.MahjongAmount = op_seat.mahjongAmount;
            seat.IsBanker = op_seat.isBanker;
            room.SeatList.Add(seat);
        }

        int applyCount = 0;
        for (int i = 0; i < room.SeatList.Count; ++i)
        {
            if (room.SeatList[i].DisbandState == DisbandStatus.Apply)
            {
                ++applyCount;
            }
        }
        if (applyCount > 1)
        {
            for (int i = 0; i < room.SeatList.Count; ++i)
            {
                room.SeatList[i].DisbandState = DisbandStatus.Wait;
            }
        }

        room.FirstDice = new Dice()
        {
            diceA = proto.diceFirstA,
            diceB = proto.diceFirstB,
            seatPos = proto.diceFirst,
        };
        room.SecondDice = new Dice()
        {
            diceA = proto.diceSecondA,
            diceB = proto.diceSecondB,
            seatPos = proto.diceSecond,
        };

        int askLeng = proto.askMahjongGroupsList.Count;
        if (askLeng > 0)
        {
            room.AskMahjongGroup = new List<MahjongGroup>();
            for (int i = 0; i < askLeng; ++i)
            {
                Mahjong_S2C_RoomInfoProto.MahjongGroup op_Group = proto.askMahjongGroupsList[i];
                List<Mahjong> lst = new List<Mahjong>();
                for (int j = 0; j < op_Group.mahjongsList.Count; ++j)
                {
                    Mahjong_S2C_RoomInfoProto.Mahjong op_mahjong = op_Group.mahjongsList[j];
                    lst.Add(new Mahjong(op_mahjong.index, op_mahjong.color, op_mahjong.number));
                }
                MahjongGroup combination = new MahjongGroup((OperationType)op_Group.typeId, op_Group.subTypeId, lst, 0);
                room.AskMahjongGroup.Add(combination);
            }
        }
        else
        {
            room.AskMahjongGroup = null;
        }

        m_Procedure.Init(room);
    }
}
