//===================================================
//作    者：DRB
//创建时间：2021-04-15 03:55:31
//备    注：
//===================================================
package com.oegame.tablegames.protocol.gen;
import java.util.HashMap;

/// <summary>
/// 协议编号定义
/// </summary>
public class ProtoCodeDef
{
    /// <summary>
    /// C2S_HeartBeat
    /// </summary>
    public static final int System_C2S_HeartBeatProto = 10001;

    /// <summary>
    /// C2S_Disconnect
    /// </summary>
    public static final int System_C2S_DisconnectProto = 10002;

    /// <summary>
    /// C2S_Connect
    /// </summary>
    public static final int System_C2S_ConnectProto = 10003;

    /// <summary>
    /// S2C_HeartBeat
    /// </summary>
    public static final int System_S2C_HeartBeatProto = 10004;

    /// <summary>
    /// S2C_Disconnect
    /// </summary>
    public static final int System_S2C_DisconnectProto = 10005;

    /// <summary>
    /// S2C_Connect
    /// </summary>
    public static final int System_S2C_ConnectProto = 10006;

    /// <summary>
    /// C2S_CreateRoom
    /// </summary>
    public static final int Game_C2S_CreateRoomProto = 20001;

    /// <summary>
    /// C2S_EnterRoom
    /// </summary>
    public static final int Game_C2S_EnterRoomProto = 20002;

    /// <summary>
    /// S2C_EnterRoom
    /// </summary>
    public static final int Game_S2C_EnterRoomProto = 20003;

    /// <summary>
    /// C2S_LeaveRoom
    /// </summary>
    public static final int Game_C2S_LeaveRoomProto = 20004;

    /// <summary>
    /// S2C_LeaveRoom
    /// </summary>
    public static final int Game_S2C_LeaveRoomProto = 20005;

    /// <summary>
    /// C2S_Ready
    /// </summary>
    public static final int Game_C2S_ReadyProto = 20006;

    /// <summary>
    /// S2C_Ready
    /// </summary>
    public static final int Game_S2C_ReadyProto = 20007;

    /// <summary>
    /// C2S_ApplyDisband
    /// </summary>
    public static final int Game_C2S_ApplyDisbandProto = 20008;

    /// <summary>
    /// S2C_AskDisband
    /// </summary>
    public static final int Game_S2C_AskDisbandProto = 20009;

    /// <summary>
    /// S2C_Disband
    /// </summary>
    public static final int Game_S2C_DisbandProto = 20010;

    /// <summary>
    /// S2C_AFK
    /// </summary>
    public static final int Game_S2C_AFKProto = 20011;

    /// <summary>
    /// C2S_QueryRoomInfo
    /// </summary>
    public static final int Game_C2S_QueryRoomInfoProto = 20012;

    /// <summary>
    /// S2C_GameBegin
    /// </summary>
    public static final int Mahjong_S2C_GameBeginProto = 30001;

    /// <summary>
    /// S2C_Draw
    /// </summary>
    public static final int Mahjong_S2C_DrawProto = 30002;

    /// <summary>
    /// C2S_Discard
    /// </summary>
    public static final int Mahjong_C2S_DiscardProto = 30003;

    /// <summary>
    /// S2C_Discard
    /// </summary>
    public static final int Mahjong_S2C_DiscardProto = 30004;

    /// <summary>
    /// S2C_Turn
    /// </summary>
    public static final int Mahjong_S2C_TurnProto = 30005;

    /// <summary>
    /// C2S_Operate
    /// </summary>
    public static final int Mahjong_C2S_OperateProto = 30006;

    /// <summary>
    /// S2C_Operate
    /// </summary>
    public static final int Mahjong_S2C_OperateProto = 30007;

    /// <summary>
    /// S2C_Settle
    /// </summary>
    public static final int Mahjong_S2C_SettleProto = 30008;

    /// <summary>
    /// S2C_Result
    /// </summary>
    public static final int Mahjong_S2C_ResultProto = 30009;

    /// <summary>
    /// S2C_RoomInfo
    /// </summary>
    public static final int Mahjong_S2C_RoomInfoProto = 30010;

    /// <summary>
    /// S2C_AskOperation
    /// </summary>
    public static final int Mahjong_S2C_AskOperationProto = 30011;

    /// <summary>
    /// C2S_Pass
    /// </summary>
    public static final int Mahjong_C2S_PassProto = 30012;

    /// <summary>
    /// S2C_Pass
    /// </summary>
    public static final int Mahjong_S2C_PassProto = 30013;

    /// <summary>
    /// S2C_OperationWait
    /// </summary>
    public static final int Mahjong_S2C_OperationWaitProto = 30014;

    private static final HashMap<Integer,String> DicCn = new HashMap<Integer,String>();
    private static final HashMap<Integer,String> DicEn = new HashMap<Integer,String>();

    static
    {
        DicCn.put(System_C2S_HeartBeatProto,"C2S_HeartBeat");
        DicEn.put(System_C2S_HeartBeatProto,"System_C2S_HeartBeatProto");
        DicCn.put(System_C2S_DisconnectProto,"C2S_Disconnect");
        DicEn.put(System_C2S_DisconnectProto,"System_C2S_DisconnectProto");
        DicCn.put(System_C2S_ConnectProto,"C2S_Connect");
        DicEn.put(System_C2S_ConnectProto,"System_C2S_ConnectProto");
        DicCn.put(System_S2C_HeartBeatProto,"S2C_HeartBeat");
        DicEn.put(System_S2C_HeartBeatProto,"System_S2C_HeartBeatProto");
        DicCn.put(System_S2C_DisconnectProto,"S2C_Disconnect");
        DicEn.put(System_S2C_DisconnectProto,"System_S2C_DisconnectProto");
        DicCn.put(System_S2C_ConnectProto,"S2C_Connect");
        DicEn.put(System_S2C_ConnectProto,"System_S2C_ConnectProto");
        DicCn.put(Game_C2S_CreateRoomProto,"C2S_CreateRoom");
        DicEn.put(Game_C2S_CreateRoomProto,"Game_C2S_CreateRoomProto");
        DicCn.put(Game_C2S_EnterRoomProto,"C2S_EnterRoom");
        DicEn.put(Game_C2S_EnterRoomProto,"Game_C2S_EnterRoomProto");
        DicCn.put(Game_S2C_EnterRoomProto,"S2C_EnterRoom");
        DicEn.put(Game_S2C_EnterRoomProto,"Game_S2C_EnterRoomProto");
        DicCn.put(Game_C2S_LeaveRoomProto,"C2S_LeaveRoom");
        DicEn.put(Game_C2S_LeaveRoomProto,"Game_C2S_LeaveRoomProto");
        DicCn.put(Game_S2C_LeaveRoomProto,"S2C_LeaveRoom");
        DicEn.put(Game_S2C_LeaveRoomProto,"Game_S2C_LeaveRoomProto");
        DicCn.put(Game_C2S_ReadyProto,"C2S_Ready");
        DicEn.put(Game_C2S_ReadyProto,"Game_C2S_ReadyProto");
        DicCn.put(Game_S2C_ReadyProto,"S2C_Ready");
        DicEn.put(Game_S2C_ReadyProto,"Game_S2C_ReadyProto");
        DicCn.put(Game_C2S_ApplyDisbandProto,"C2S_ApplyDisband");
        DicEn.put(Game_C2S_ApplyDisbandProto,"Game_C2S_ApplyDisbandProto");
        DicCn.put(Game_S2C_AskDisbandProto,"S2C_AskDisband");
        DicEn.put(Game_S2C_AskDisbandProto,"Game_S2C_AskDisbandProto");
        DicCn.put(Game_S2C_DisbandProto,"S2C_Disband");
        DicEn.put(Game_S2C_DisbandProto,"Game_S2C_DisbandProto");
        DicCn.put(Game_S2C_AFKProto,"S2C_AFK");
        DicEn.put(Game_S2C_AFKProto,"Game_S2C_AFKProto");
        DicCn.put(Game_C2S_QueryRoomInfoProto,"C2S_QueryRoomInfo");
        DicEn.put(Game_C2S_QueryRoomInfoProto,"Game_C2S_QueryRoomInfoProto");
        DicCn.put(Mahjong_S2C_GameBeginProto,"S2C_GameBegin");
        DicEn.put(Mahjong_S2C_GameBeginProto,"Mahjong_S2C_GameBeginProto");
        DicCn.put(Mahjong_S2C_DrawProto,"S2C_Draw");
        DicEn.put(Mahjong_S2C_DrawProto,"Mahjong_S2C_DrawProto");
        DicCn.put(Mahjong_C2S_DiscardProto,"C2S_Discard");
        DicEn.put(Mahjong_C2S_DiscardProto,"Mahjong_C2S_DiscardProto");
        DicCn.put(Mahjong_S2C_DiscardProto,"S2C_Discard");
        DicEn.put(Mahjong_S2C_DiscardProto,"Mahjong_S2C_DiscardProto");
        DicCn.put(Mahjong_S2C_TurnProto,"S2C_Turn");
        DicEn.put(Mahjong_S2C_TurnProto,"Mahjong_S2C_TurnProto");
        DicCn.put(Mahjong_C2S_OperateProto,"C2S_Operate");
        DicEn.put(Mahjong_C2S_OperateProto,"Mahjong_C2S_OperateProto");
        DicCn.put(Mahjong_S2C_OperateProto,"S2C_Operate");
        DicEn.put(Mahjong_S2C_OperateProto,"Mahjong_S2C_OperateProto");
        DicCn.put(Mahjong_S2C_SettleProto,"S2C_Settle");
        DicEn.put(Mahjong_S2C_SettleProto,"Mahjong_S2C_SettleProto");
        DicCn.put(Mahjong_S2C_ResultProto,"S2C_Result");
        DicEn.put(Mahjong_S2C_ResultProto,"Mahjong_S2C_ResultProto");
        DicCn.put(Mahjong_S2C_RoomInfoProto,"S2C_RoomInfo");
        DicEn.put(Mahjong_S2C_RoomInfoProto,"Mahjong_S2C_RoomInfoProto");
        DicCn.put(Mahjong_S2C_AskOperationProto,"S2C_AskOperation");
        DicEn.put(Mahjong_S2C_AskOperationProto,"Mahjong_S2C_AskOperationProto");
        DicCn.put(Mahjong_C2S_PassProto,"C2S_Pass");
        DicEn.put(Mahjong_C2S_PassProto,"Mahjong_C2S_PassProto");
        DicCn.put(Mahjong_S2C_PassProto,"S2C_Pass");
        DicEn.put(Mahjong_S2C_PassProto,"Mahjong_S2C_PassProto");
        DicCn.put(Mahjong_S2C_OperationWaitProto,"S2C_OperationWait");
        DicEn.put(Mahjong_S2C_OperationWaitProto,"Mahjong_S2C_OperationWaitProto");
    }
    public static String getCn(int code)
    {
        if (!DicCn.containsKey(code)) return "未知消息";
        return DicCn.get(code);
    }
    public static String getEn(int code)
    {
        if (!DicCn.containsKey(code)) return "Unknown protocol";
        return DicEn.get(code);
    }
}
