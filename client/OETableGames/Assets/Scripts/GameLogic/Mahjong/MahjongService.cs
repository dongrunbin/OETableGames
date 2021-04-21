//===================================================
//Author      : DRB
//CreateTime  ：2021/4/14 3:38:55
//Description ：
//===================================================
using DrbFramework.Event;
using DrbFramework.Internal;
using System;
using System.Collections.Generic;

public class MahjongService
{
    private MahjongProcedure m_Procedure;
    public MahjongService(MahjongProcedure procedure)
    {
        m_Procedure = procedure;
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
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.Mahjong_S2C_DrawProto, OnServerBroadcastDrawPoker);//服务器广播摸牌消息
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.Mahjong_S2C_DiscardProto, OnServerBroadcastPlayPoker);//服务器广播出牌消息
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
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.Mahjong_S2C_DrawProto, OnServerBroadcastDrawPoker);//服务器广播摸牌消息
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.Mahjong_S2C_DiscardProto, OnServerBroadcastPlayPoker);//服务器广播出牌消息
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
        DrbComponent.NetworkSystem.Send(new Game_C2S_QueryRoomInfoProto().Serialize());
    }

    public void ClientSendLeaveRoom()
    {
        Game_C2S_LeaveRoomProto proto = new Game_C2S_LeaveRoomProto();
        DrbComponent.NetworkSystem.Send(proto.Serialize());
    }

    public void ClientSendReady()
    {
        Game_C2S_ReadyProto proto = new Game_C2S_ReadyProto();
        DrbComponent.NetworkSystem.Send(proto.Serialize());
    }

    public void ClientSendApplyDisbandRoom()
    {
        Game_C2S_ApplyDisbandProto proto = new Game_C2S_ApplyDisbandProto();
        proto.disbandStatus = (byte)DisbandStatus.Apply;
        DrbComponent.NetworkSystem.Send(proto.Serialize());
    }

    public void ClientSendDisbandRoom(bool isAgree)
    {
        Game_C2S_ApplyDisbandProto proto = new Game_C2S_ApplyDisbandProto();
        proto.disbandStatus = isAgree ? (byte)DisbandStatus.Agree : (byte)DisbandStatus.Refuse;
        DrbComponent.NetworkSystem.Send(proto.Serialize());
    }

    public void ClientSendPlayPoker(Mahjong mahjong)
    {
        if (mahjong == null) return;
        Mahjong_C2S_DiscardProto proto = new Mahjong_C2S_DiscardProto();
        proto.index = mahjong.index;
        DrbComponent.NetworkSystem.Send(proto.Serialize());
    }

    public void ClientSendPass()
    {
        //DrbComponent.NetworkSystem.Send(new C2S_Mahjong_PassProto().Serialize());
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
        DrbComponent.NetworkSystem.Send(proto.Serialize());
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
        room.PokerTotal = proto.pokerTotal;
        room.PokerAmount = proto.pokerAmount;

        room.SeatList = new List<Seat>();
        for (int i = 0; i < proto.seatList.Count; ++i)
        {
            Seat entity = new Seat();
            Mahjong_S2C_GameBeginProto.Seat protoSeat = proto.seatList[i];
            entity.Pos = protoSeat.pos;
            entity.PlayerId = protoSeat.playerId;
            for (int j = 0; j < protoSeat.pokersList.Count; ++j)
            {
                Mahjong_S2C_GameBeginProto.Poker protoPoker = protoSeat.pokersList[j];
                entity.PokerList.Add(new Mahjong(protoPoker.index, protoPoker.color, protoPoker.size));
            }
            for (int j = 0; j < protoSeat.universalPokerList.Count; ++j)
            {
                Mahjong_S2C_GameBeginProto.Poker op_poker = protoSeat.universalPokerList[j];
                entity.UniversalList.Add(new Mahjong(op_poker.index, op_poker.color, op_poker.size));
            }
            entity.PokerAmount = protoSeat.pokerAmount;
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

    private void OnServerBroadcastDrawPoker(object sender, EventArgs<int> args)
    {
        Mahjong_S2C_DrawProto proto = new Mahjong_S2C_DrawProto(((NetworkEventArgs)args).Data);
        Mahjong mahjong = new Mahjong(proto.index, proto.color, proto.number);
        m_Procedure.Draw(proto.playerId, mahjong, false);
    }

    private void OnServerBroadcastPlayPoker(object sender, EventArgs<int> args)
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
                    Mahjong_S2C_AskOperationProto.Mahjong op_poker = op_Group.mahjongsList[j];
                    lst.Add(new Mahjong(op_poker.index, op_poker.color, op_poker.number));
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

        List<Mahjong> lst = new List<Mahjong>(proto.pokersList.Count);
        for (int i = 0; i < proto.pokersList.Count; ++i)
        {
            Mahjong_S2C_OperateProto.Poker op_poker = proto.pokersList[i];
            lst.Add(new Mahjong(op_poker.index, op_poker.color, op_poker.number));
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
            seat.PokerList.Clear();
            seat.incomesDesc = op_seat.incomesDesc;
            seat.UsedPokerList.Clear();
            for (int j = 0; j < op_seat.usedPokerGroupList.Count; ++j)
            {
                Mahjong_S2C_SettleProto.PokerGroup op_combination = op_seat.usedPokerGroupList[j];
                List<Mahjong> lst = new List<Mahjong>();
                for (int k = 0; k < op_combination.pokersList.Count; ++k)
                {
                    Mahjong_S2C_SettleProto.Poker op_poker = op_combination.pokersList[k];
                    lst.Add(new Mahjong(op_poker.index, op_poker.color, op_poker.number));
                }
                MahjongGroup combination = new MahjongGroup((OperationType)op_combination.typeId, (int)op_combination.subTypeId, lst, seat.Pos);
                combination.Sort();
                seat.UsedPokerList.Add(combination);
            }
            for (int j = 0; j < op_seat.pokersList.Count; ++j)
            {
                Mahjong_S2C_SettleProto.Poker op_Poker = op_seat.pokersList[j];
                seat.PokerList.Add(new Mahjong(op_Poker.index, op_Poker.color, op_Poker.number));
            }
            if (op_seat.hitPoker != null)
            {
                seat.HitPoker = new Mahjong(op_seat.hitPoker.index, op_seat.hitPoker.color, op_seat.hitPoker.number);
            }
            else
            {
                seat.HitPoker = null;
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
            PokerAmount = proto.pokerAmount,
            PokerTotal = proto.pokerTotal,
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
            if (op_seat.hitPoker != null)
            {
                seat.HitPoker = new Mahjong(op_seat.hitPoker.index, op_seat.hitPoker.color, op_seat.hitPoker.number);
            }
            for (int j = 0; j < op_seat.mahjongsList.Count; ++j)
            {
                Mahjong_S2C_RoomInfoProto.Mahjong protoPoker = op_seat.mahjongsList[j];
                seat.PokerList.Add(new Mahjong(protoPoker.index, protoPoker.color, protoPoker.number));
            }
            for (int j = 0; j < op_seat.desktopList.Count; ++j)
            {
                Mahjong_S2C_RoomInfoProto.Mahjong protoPoker = op_seat.desktopList[j];
                Mahjong mahjong = new Mahjong(protoPoker.index, protoPoker.color, protoPoker.number);
                seat.DeskTopPoker.Add(mahjong);
            }
            for (int j = 0; j < op_seat.usedPokerGroupList.Count; ++j)
            {
                Mahjong_S2C_RoomInfoProto.MahjongGroup protoPoker = op_seat.usedPokerGroupList[j];
                List<Mahjong> lst = new List<Mahjong>();
                for (int k = 0; k < protoPoker.pokersList.Count; ++k)
                {
                    Mahjong_S2C_RoomInfoProto.Mahjong op_poker = protoPoker.pokersList[k];
                    Mahjong mahjong = new Mahjong(op_poker.index, op_poker.color, op_poker.number);
                    lst.Add(mahjong);
                }
                MahjongGroup combination = new MahjongGroup((OperationType)protoPoker.typeId, protoPoker.subTypeId, lst, seat.Pos);
                combination.Sort();
                seat.UsedPokerList.Add(combination);
            }
            for (int j = 0; j < op_seat.universalList.Count; ++j)
            {
                Mahjong_S2C_RoomInfoProto.Mahjong op_poker = op_seat.universalList[j];
                seat.UniversalList.Add(new Mahjong(op_poker.index, op_poker.color, op_poker.number));
            }
            seat.PokerAmount = op_seat.pokerAmount;
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

        int askLeng = proto.askPokerGroupsList.Count;
        if (askLeng > 0)
        {
            room.AskPokerGroup = new List<MahjongGroup>();
            for (int i = 0; i < askLeng; ++i)
            {
                Mahjong_S2C_RoomInfoProto.MahjongGroup op_Group = proto.askPokerGroupsList[i];
                List<Mahjong> lst = new List<Mahjong>();
                for (int j = 0; j < op_Group.pokersList.Count; ++j)
                {
                    Mahjong_S2C_RoomInfoProto.Mahjong op_poker = op_Group.pokersList[j];
                    lst.Add(new Mahjong(op_poker.index, op_poker.color, op_poker.number));
                }
                MahjongGroup combination = new MahjongGroup((OperationType)op_Group.typeId, op_Group.subTypeId, lst, 0);
                room.AskPokerGroup.Add(combination);
            }
        }
        else
        {
            room.AskPokerGroup = null;
        }

        m_Procedure.Init(room);
    }
}
