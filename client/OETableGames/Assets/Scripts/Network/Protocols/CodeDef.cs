//===================================================
//作    者：DRB
//创建时间：2021-04-15 03:55:33
//备    注：this code is generated by the tool
//===================================================
using System.Collections.Generic;

/// <summary>
/// 协议编号定义
/// </summary>
public static class CodeDef
{
    /// <summary>
    /// C2S_HeartBeat
    /// </summary>
    public const int System_C2S_HeartBeatProto = 10001;

    /// <summary>
    /// C2S_Disconnect
    /// </summary>
    public const int System_C2S_DisconnectProto = 10002;

    /// <summary>
    /// C2S_Connect
    /// </summary>
    public const int System_C2S_ConnectProto = 10003;

    /// <summary>
    /// S2C_HeartBeat
    /// </summary>
    public const int System_S2C_HeartBeatProto = 10004;

    /// <summary>
    /// S2C_Disconnect
    /// </summary>
    public const int System_S2C_DisconnectProto = 10005;

    /// <summary>
    /// S2C_Connect
    /// </summary>
    public const int System_S2C_ConnectProto = 10006;

    /// <summary>
    /// C2S_CreateRoom
    /// </summary>
    public const int Game_C2S_CreateRoomProto = 20001;

    /// <summary>
    /// C2S_EnterRoom
    /// </summary>
    public const int Game_C2S_EnterRoomProto = 20002;

    /// <summary>
    /// S2C_EnterRoom
    /// </summary>
    public const int Game_S2C_EnterRoomProto = 20003;

    /// <summary>
    /// C2S_LeaveRoom
    /// </summary>
    public const int Game_C2S_LeaveRoomProto = 20004;

    /// <summary>
    /// S2C_LeaveRoom
    /// </summary>
    public const int Game_S2C_LeaveRoomProto = 20005;

    /// <summary>
    /// C2S_Ready
    /// </summary>
    public const int Game_C2S_ReadyProto = 20006;

    /// <summary>
    /// S2C_Ready
    /// </summary>
    public const int Game_S2C_ReadyProto = 20007;

    /// <summary>
    /// C2S_ApplyDisband
    /// </summary>
    public const int Game_C2S_ApplyDisbandProto = 20008;

    /// <summary>
    /// S2C_AskDisband
    /// </summary>
    public const int Game_S2C_AskDisbandProto = 20009;

    /// <summary>
    /// S2C_Disband
    /// </summary>
    public const int Game_S2C_DisbandProto = 20010;

    /// <summary>
    /// S2C_AFK
    /// </summary>
    public const int Game_S2C_AFKProto = 20011;

    /// <summary>
    /// C2S_QueryRoomInfo
    /// </summary>
    public const int Game_C2S_QueryRoomInfoProto = 20012;

    /// <summary>
    /// S2C_GameBegin
    /// </summary>
    public const int Mahjong_S2C_GameBeginProto = 30001;

    /// <summary>
    /// S2C_Draw
    /// </summary>
    public const int Mahjong_S2C_DrawProto = 30002;

    /// <summary>
    /// C2S_Discard
    /// </summary>
    public const int Mahjong_C2S_DiscardProto = 30003;

    /// <summary>
    /// S2C_Discard
    /// </summary>
    public const int Mahjong_S2C_DiscardProto = 30004;

    /// <summary>
    /// S2C_Turn
    /// </summary>
    public const int Mahjong_S2C_TurnProto = 30005;

    /// <summary>
    /// C2S_Operate
    /// </summary>
    public const int Mahjong_C2S_OperateProto = 30006;

    /// <summary>
    /// S2C_Operate
    /// </summary>
    public const int Mahjong_S2C_OperateProto = 30007;

    /// <summary>
    /// S2C_Settle
    /// </summary>
    public const int Mahjong_S2C_SettleProto = 30008;

    /// <summary>
    /// S2C_Result
    /// </summary>
    public const int Mahjong_S2C_ResultProto = 30009;

    /// <summary>
    /// S2C_RoomInfo
    /// </summary>
    public const int Mahjong_S2C_RoomInfoProto = 30010;

    /// <summary>
    /// S2C_AskOperation
    /// </summary>
    public const int Mahjong_S2C_AskOperationProto = 30011;

    /// <summary>
    /// C2S_Pass
    /// </summary>
    public const int Mahjong_C2S_PassProto = 30012;

    /// <summary>
    /// S2C_Pass
    /// </summary>
    public const int Mahjong_S2C_PassProto = 30013;

    /// <summary>
    /// S2C_OperationWait
    /// </summary>
    public const int Mahjong_S2C_OperationWaitProto = 30014;

    private readonly static Dictionary<int, string> DicCn = new Dictionary<int, string>();
    private readonly static Dictionary<int, string> DicEn = new Dictionary<int, string>();

    static CodeDef()
    {
        DicCn[System_C2S_HeartBeatProto] = "C2S_HeartBeat";
        DicEn[System_C2S_HeartBeatProto] = "System_C2S_HeartBeatProto";
        DicCn[System_C2S_DisconnectProto] = "C2S_Disconnect";
        DicEn[System_C2S_DisconnectProto] = "System_C2S_DisconnectProto";
        DicCn[System_C2S_ConnectProto] = "C2S_Connect";
        DicEn[System_C2S_ConnectProto] = "System_C2S_ConnectProto";
        DicCn[System_S2C_HeartBeatProto] = "S2C_HeartBeat";
        DicEn[System_S2C_HeartBeatProto] = "System_S2C_HeartBeatProto";
        DicCn[System_S2C_DisconnectProto] = "S2C_Disconnect";
        DicEn[System_S2C_DisconnectProto] = "System_S2C_DisconnectProto";
        DicCn[System_S2C_ConnectProto] = "S2C_Connect";
        DicEn[System_S2C_ConnectProto] = "System_S2C_ConnectProto";
        DicCn[Game_C2S_CreateRoomProto] = "C2S_CreateRoom";
        DicEn[Game_C2S_CreateRoomProto] = "Game_C2S_CreateRoomProto";
        DicCn[Game_C2S_EnterRoomProto] = "C2S_EnterRoom";
        DicEn[Game_C2S_EnterRoomProto] = "Game_C2S_EnterRoomProto";
        DicCn[Game_S2C_EnterRoomProto] = "S2C_EnterRoom";
        DicEn[Game_S2C_EnterRoomProto] = "Game_S2C_EnterRoomProto";
        DicCn[Game_C2S_LeaveRoomProto] = "C2S_LeaveRoom";
        DicEn[Game_C2S_LeaveRoomProto] = "Game_C2S_LeaveRoomProto";
        DicCn[Game_S2C_LeaveRoomProto] = "S2C_LeaveRoom";
        DicEn[Game_S2C_LeaveRoomProto] = "Game_S2C_LeaveRoomProto";
        DicCn[Game_C2S_ReadyProto] = "C2S_Ready";
        DicEn[Game_C2S_ReadyProto] = "Game_C2S_ReadyProto";
        DicCn[Game_S2C_ReadyProto] = "S2C_Ready";
        DicEn[Game_S2C_ReadyProto] = "Game_S2C_ReadyProto";
        DicCn[Game_C2S_ApplyDisbandProto] = "C2S_ApplyDisband";
        DicEn[Game_C2S_ApplyDisbandProto] = "Game_C2S_ApplyDisbandProto";
        DicCn[Game_S2C_AskDisbandProto] = "S2C_AskDisband";
        DicEn[Game_S2C_AskDisbandProto] = "Game_S2C_AskDisbandProto";
        DicCn[Game_S2C_DisbandProto] = "S2C_Disband";
        DicEn[Game_S2C_DisbandProto] = "Game_S2C_DisbandProto";
        DicCn[Game_S2C_AFKProto] = "S2C_AFK";
        DicEn[Game_S2C_AFKProto] = "Game_S2C_AFKProto";
        DicCn[Game_C2S_QueryRoomInfoProto] = "C2S_QueryRoomInfo";
        DicEn[Game_C2S_QueryRoomInfoProto] = "Game_C2S_QueryRoomInfoProto";
        DicCn[Mahjong_S2C_GameBeginProto] = "S2C_GameBegin";
        DicEn[Mahjong_S2C_GameBeginProto] = "Mahjong_S2C_GameBeginProto";
        DicCn[Mahjong_S2C_DrawProto] = "S2C_Draw";
        DicEn[Mahjong_S2C_DrawProto] = "Mahjong_S2C_DrawProto";
        DicCn[Mahjong_C2S_DiscardProto] = "C2S_Discard";
        DicEn[Mahjong_C2S_DiscardProto] = "Mahjong_C2S_DiscardProto";
        DicCn[Mahjong_S2C_DiscardProto] = "S2C_Discard";
        DicEn[Mahjong_S2C_DiscardProto] = "Mahjong_S2C_DiscardProto";
        DicCn[Mahjong_S2C_TurnProto] = "S2C_Turn";
        DicEn[Mahjong_S2C_TurnProto] = "Mahjong_S2C_TurnProto";
        DicCn[Mahjong_C2S_OperateProto] = "C2S_Operate";
        DicEn[Mahjong_C2S_OperateProto] = "Mahjong_C2S_OperateProto";
        DicCn[Mahjong_S2C_OperateProto] = "S2C_Operate";
        DicEn[Mahjong_S2C_OperateProto] = "Mahjong_S2C_OperateProto";
        DicCn[Mahjong_S2C_SettleProto] = "S2C_Settle";
        DicEn[Mahjong_S2C_SettleProto] = "Mahjong_S2C_SettleProto";
        DicCn[Mahjong_S2C_ResultProto] = "S2C_Result";
        DicEn[Mahjong_S2C_ResultProto] = "Mahjong_S2C_ResultProto";
        DicCn[Mahjong_S2C_RoomInfoProto] = "S2C_RoomInfo";
        DicEn[Mahjong_S2C_RoomInfoProto] = "Mahjong_S2C_RoomInfoProto";
        DicCn[Mahjong_S2C_AskOperationProto] = "S2C_AskOperation";
        DicEn[Mahjong_S2C_AskOperationProto] = "Mahjong_S2C_AskOperationProto";
        DicCn[Mahjong_C2S_PassProto] = "C2S_Pass";
        DicEn[Mahjong_C2S_PassProto] = "Mahjong_C2S_PassProto";
        DicCn[Mahjong_S2C_PassProto] = "S2C_Pass";
        DicEn[Mahjong_S2C_PassProto] = "Mahjong_S2C_PassProto";
        DicCn[Mahjong_S2C_OperationWaitProto] = "S2C_OperationWait";
        DicEn[Mahjong_S2C_OperationWaitProto] = "Mahjong_S2C_OperationWaitProto";
    }
    public static string GetCn(int code)
    {
        if(!DicCn.ContainsKey(code)) return "未知消息";
        return DicCn[code];
    }
    public static string GetEn(int code)
    {
        if(!DicEn.ContainsKey(code)) return "Unknown protocol";
        return DicEn[code];
    }
}
