package com.oegame.tablegames.service.proxy;

import org.apache.mina.core.session.IoSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.oegame.tablegames.common.net.socket.IRoute;
import com.oegame.tablegames.common.util.TimeUtil;
import com.oegame.tablegames.protocol.gen.*;
import com.oegame.tablegames.service.player.Player;
import com.oegame.tablegames.service.ServiceUtil;

public class SocketRoute implements IRoute
{
	private static final Logger logger = LoggerFactory.getLogger(SocketRoute.class);

	public SocketRoute() {

	}

	public void call(IoSession session, int code, byte[] data) {
		if (code != ProtoCodeDef.System_C2S_HeartBeatProto) {
			logger.info(String.format("收到%s消息:%d, %s, %s",
					session.containsAttribute("playerId") ? session.getAttribute("playerId").toString() : "", code,
					ProtoCodeDef.getEn(code), ProtoCodeDef.getCn(code)));
		}
		if (code == ProtoCodeDef.System_C2S_ConnectProto) {
			logger.info("wtffffffff");
			if (session.getAttribute("TokenMark") != null) {
				logger.info("重复握手");
				session.closeNow();
				return;
			}
			System_C2S_ConnectProto proto = System_C2S_ConnectProto.getProto(data);
			Player player = ServiceUtil.getPlayerService().auth(proto.getPassportId(), proto.getToken(), 0);
			if (player == null) {
				logger.info("握手失败" + proto.getPassportId());
				session.closeNow();
				return;
			} else {
				session.setAttribute("TokenMark", proto.getToken());
				session.setAttribute("playerId", (long) proto.getPassportId());
			}
			if (ClientManager.getInstance().containsPlayer(proto.getPassportId())) {
				ClientManager.getInstance().removePlayer(proto.getPassportId());
			}
			ClientManager.getInstance().addPlayer(player.playerId, session);
			System_S2C_ConnectProto s2c = new System_S2C_ConnectProto();
			s2c.setTimestamp(TimeUtil.millisecond());
			session.write(s2c.toArray());
			return;
		}

		if (session.getAttribute("TokenMark") == null) {
			session.closeNow();
			return;
		}
		long playerId = (long) session.getAttribute("playerId");
		if (code == ProtoCodeDef.System_C2S_HeartBeatProto) {
			System_C2S_HeartBeatProto recvProto = System_C2S_HeartBeatProto.getProto(data);

			System_S2C_HeartBeatProto s2c = new System_S2C_HeartBeatProto();
			s2c.setClientTimestamp(recvProto.getClientTimestamp());
			s2c.setServerTimestamp(TimeUtil.millisecond());
			session.write(s2c.toArray());
			return;
		} else if (code == ProtoCodeDef.System_C2S_DisconnectProto) // 断开连接
		{
			ServiceUtil.getPlayerService().logout(playerId);
			session.removeAttribute("TokenMark");
			ClientManager.getInstance().removePlayer(playerId);
			session.write(new System_S2C_DisconnectProto().toArray());
			return;
		}
		Player player = ServiceUtil.getPlayerService().getPlayer(playerId);

//		if (code == ProtoCodeDef.C2S_Game_RoomCreateProto) // 创建房间
//		{
//			C2S_Game_RoomCreateProto c2s = C2S_Game_RoomCreateProto.getProto(data);
//
//			int roomId = RandomUtil.Range(100000, 999999);
//			try {
//				ServiceUtil.getGameService(c2s.getGameId()).create(roomId, playerId, c2s.getsettingIdList());
//			} catch (Exception e) {
//				S2C_System_ErrorProto s2c = new S2C_System_ErrorProto();
//				s2c.setCode(-1);
//				s2c.setMessage("暂未开放该游戏");
//				session.write(s2c.toArray());
//			}
//		} else if (code == ProtoCodeDef.C2S_Game_RoomEnterProto) // 进入房间
//		{
//			C2S_Game_RoomEnterProto c2s = C2S_Game_RoomEnterProto.getProto(data);
//			ArrayList<sys_roomEntity> lst = sys_roomCacheModel.getInstance().getListByRoomId(c2s.getRoomId());
//			if (lst.size() > 0) {
//				try {
//					ServiceUtil.getGameService(lst.get(0).gameId).enter(c2s.getRoomId(), player, 0);
//				} catch (Exception e) {
//					S2C_System_ErrorProto s2c = new S2C_System_ErrorProto();
//					s2c.setCode(-1);
//					s2c.setMessage("暂未开放该游戏");
//					session.write(s2c.toArray());
//				}
//			} else {
//				S2C_System_ErrorProto s2c = new S2C_System_ErrorProto();
//				s2c.setCode(-1);
//				s2c.setMessage("房间不存在");
//				session.write(s2c.toArray());
//			}
//		} else if (code == ProtoCodeDef.C2S_Game_RoomLeaveProto) // 离开房间
//		{
//			// C2S_Game_RoomLeaveProto c2s = C2S_Game_RoomLeaveProto.getProto(data);
//			if (player.roomId == 0)
//				return;
//			ServiceUtil.getGameService(player.gameId).leave(player.roomId, playerId);
//		} else if (code == ProtoCodeDef.C2S_Game_QueryRoomProto) // 查询房间
//		{
//			// C2S_Game_QueryRoomProto c2s = C2S_Game_QueryRoomProto.getProto(data);
//			S2C_Game_EnterGameProto s2c = new S2C_Game_EnterGameProto();
//			s2c.setGameId(player.gameId);
//			s2c.setRoomId(player.roomId);
//			session.write(s2c.toArray());
//		} else if (code == ProtoCodeDef.C2S_Game_RoomDisbandApplyProto) // 房间解散
//		{
//			C2S_Game_RoomDisbandApplyProto c2s = C2S_Game_RoomDisbandApplyProto.getProto(data);
//			if (player.roomId == 0)
//				return;
//			ServiceUtil.getGameService(player.gameId).disbandApply(player.roomId, playerId,
//					DisbandStatus.values()[c2s.getDisbandStatus()]);
//		} else if (code == ProtoCodeDef.C2S_Game_RoomReadyProto) // 准备
//		{
//			if (player.roomId == 0)
//				return;
//			ServiceUtil.getGameService(player.gameId).ready(player.roomId, playerId);
//		} else if (code == ProtoCodeDef.C2S_Game_RoomUnreadyProto) // 取消准备
//		{
//			if (player.roomId == 0)
//				return;
//			ServiceUtil.getGameService(player.gameId).unready(player.roomId, playerId);
//		} else if (code == ProtoCodeDef.C2S_Game_AfkProto) // 暂离
//		{
//			if (player.roomId == 0)
//				return;
//			C2S_Game_AfkProto c2s = C2S_Game_AfkProto.getProto(data);
//			ServiceUtil.getGameService(player.gameId).afk(player.roomId, playerId, c2s.getIsAfk());
//		} else if (code == ProtoCodeDef.C2S_Game_SendMessageProto) // 发送消息
//		{
//			if (player.roomId == 0)
//				return;
//			C2S_Game_SendMessageProto c2s = C2S_Game_SendMessageProto.getProto(data);
//			ServiceUtil.getGameService(player.gameId).message(player.roomId, playerId, c2s.getMessageType(),
//					c2s.getMessage(), c2s.getToPlayerId());
//		} else if (code == ProtoCodeDef.C2S_Mahjong_DiscardProto) // 出牌
//		{
//			if (player.roomId == 0)
//				return;
//			C2S_Mahjong_DiscardProto c2s = C2S_Mahjong_DiscardProto.getProto(data);
//			MahjongService service = ServiceUtil.getMahjongService();
//			service.discard(player.roomId, playerId, c2s.getIndex(), c2s.getIsTing());
//		} else if (code == ProtoCodeDef.C2S_Mahjong_PassProto) // 过
//		{
//			if (player.roomId == 0)
//				return;
//			MahjongService service = ServiceUtil.getMahjongService();
//			service.pass(player.roomId, playerId);
//		} else if (code == ProtoCodeDef.C2S_Mahjong_OperateProto) // 操作
//		{
//			if (player.roomId == 0)
//				return;
//			C2S_Mahjong_OperateProto c2s = C2S_Mahjong_OperateProto.getProto(data);
//			MahjongService service = ServiceUtil.getMahjongService();
//			service.operate(player.roomId, playerId, c2s.getTypeId(), c2s.getindexList());
//		} else if (code == ProtoCodeDef.C2S_Game_RoomInfoProto) // 房间信息
//		{
//			if (player.roomId == 0) {
//				S2C_Game_RoomExpireProto s2c = new S2C_Game_RoomExpireProto();
//				session.write(s2c.toArray());
//				return;
//			}
//			try {
//				ServiceUtil.getGameService(player.gameId).roomInfo(player.roomId, playerId);
//			} catch (Exception e) {
//				S2C_Game_RoomExpireProto s2c = new S2C_Game_RoomExpireProto();
//				session.write(s2c.toArray());
//			}
//		} else if (code == ProtoCodeDef.C2S_Mahjong_ChooseHorseResultProto) // 选马
//		{
//			C2S_Mahjong_ChooseHorseResultProto proto = C2S_Mahjong_ChooseHorseResultProto.getProto(data);
//			ServiceUtil.getMahjongService().chooseHorse(player.roomId, playerId, proto.getNunber());
//		} else if (code == ProtoCodeDef.C2S_Mahjong_RobHorseResultProto) // 抢马
//		{
//			C2S_Mahjong_RobHorseResultProto proto = C2S_Mahjong_RobHorseResultProto.getProto(data);
//			ServiceUtil.getMahjongService().robHorse(player.roomId, playerId, proto.getIsRob());
//		}else if (code == ProtoCodeDef.C2S_Game_TrusteeProto) //出牌
//		{
//			if (player.roomId == 0)
//			return;
//			C2S_Game_TrusteeProto proto = C2S_Game_TrusteeProto.getProto(data);
//			ServiceUtil.getGameService(player.gameId).trustee(player.roomId, playerId, proto.getIsTrustee());
//		}
	}
}
