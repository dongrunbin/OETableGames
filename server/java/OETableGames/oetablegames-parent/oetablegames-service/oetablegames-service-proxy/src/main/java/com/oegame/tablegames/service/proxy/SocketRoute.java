package com.oegame.tablegames.service.proxy;

import com.oegame.tablegames.common.util.RandomUtil;
import com.oegame.tablegames.service.game.mahjong.MahjongService;
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

		if (code == ProtoCodeDef.Game_C2S_CreateRoomProto) // 创建房间
		{
			Game_C2S_CreateRoomProto c2s = Game_C2S_CreateRoomProto.getProto(data);

			int roomId = RandomUtil.Range(100000, 999999);
			ServiceUtil.getMahjongService().create(roomId, playerId, c2s.getsettingIdsList());
		} else if (code == ProtoCodeDef.Game_C2S_EnterRoomProto) // 进入房间
		{
			Game_C2S_EnterRoomProto c2s = Game_C2S_EnterRoomProto.getProto(data);
			ServiceUtil.getMahjongService().enter(c2s.getRoomId(), player, 0);
		} else if (code == ProtoCodeDef.Game_C2S_LeaveRoomProto) // 离开房间
		{
			Game_C2S_LeaveRoomProto c2s = Game_C2S_LeaveRoomProto.getProto(data);
			ServiceUtil.getMahjongService().leave(player.roomId, playerId);
		} else if (code == ProtoCodeDef.Game_C2S_QueryRoomInfoProto) // 查询房间
		{
			Game_C2S_QueryRoomInfoProto c2s = Game_C2S_QueryRoomInfoProto.getProto(data);
			ServiceUtil.getMahjongService().roomInfo(player.roomId, playerId);
		} else if (code == ProtoCodeDef.Game_C2S_ApplyDisbandProto) // 房间解散
		{
			Game_C2S_ApplyDisbandProto c2s = Game_C2S_ApplyDisbandProto.getProto(data);
			ServiceUtil.getMahjongService().disbandApply(player.roomId, playerId, c2s.getDisbandStatus());
		} else if (code == ProtoCodeDef.Game_C2S_ReadyProto) // 准备
		{
			if (player.roomId == 0)
				return;
			ServiceUtil.getMahjongService().ready(player.roomId, playerId);
		} else if (code == ProtoCodeDef.Mahjong_C2S_DiscardProto) // 出牌
		{
			if (player.roomId == 0)
				return;
			Mahjong_C2S_DiscardProto c2s = Mahjong_C2S_DiscardProto.getProto(data);
			MahjongService service = ServiceUtil.getMahjongService();
			service.discard(player.roomId, playerId, c2s.getIndex());
		} else if (code == ProtoCodeDef.Mahjong_C2S_PassProto) // 过
		{
			MahjongService service = ServiceUtil.getMahjongService();
			service.pass(player.roomId, playerId);
		} else if (code == ProtoCodeDef.Mahjong_C2S_OperateProto) // 操作
		{
			Mahjong_C2S_OperateProto c2s = Mahjong_C2S_OperateProto.getProto(data);
			MahjongService service = ServiceUtil.getMahjongService();
			service.operate(player.roomId, playerId, c2s.getTypeId(), c2s.getindexList());
		} else if (code == ProtoCodeDef.Mahjong_S2C_RoomInfoProto) // 房间信息
		{
			ServiceUtil.getMahjongService().roomInfo(player.roomId, playerId);
		}
	}
}
