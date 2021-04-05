package com.oegame.tablegames.service.player;

import com.oegame.tablegames.model.gen.PassportDBModel;
import com.oegame.tablegames.model.gen.PassportEntity;
import com.oegame.tablegames.model.gen.PlayerDBModel;
import com.oegame.tablegames.model.gen.PlayerEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.oegame.tablegames.common.util.StringUtil;
import com.oegame.tablegames.common.util.TimeUtil;
import com.oegame.tablegames.service.ServiceUtil;

public class PlayerServiceImpl implements PlayerService
{
	private static final Logger logger = LoggerFactory.getLogger(PlayerServiceImpl.class);
	
	private Map<Long,Player> players = new ConcurrentHashMap<Long,Player>();
	
	@Override
	public Player auth(long passportId, String token, int serverId)
	{
		PassportEntity entity = PassportDBModel.getInstance().getEntity(passportId);
		if(entity == null)
		{
			logger.warn("不存在用户" + passportId);
			return null;
		}
		else if(!entity.token.equals(token))
		{
			logger.warn("用户token不正确:" + token + "正确的token:" + entity.token);
			return null;
		}
		logger.info(passportId + "登录");
		PlayerEntity sys_player = PlayerDBModel.getInstance().getEntity(passportId);
		sys_player.online = TimeUtil.second();
		if(StringUtil.isBlank(sys_player.nickname))
		{
			sys_player.nickname = "游客" + sys_player.id;
		}
		sys_player.setIpaddr(serverId);
		PlayerDBModel.getInstance().update(sys_player);
		
		Player player = new Player(sys_player.id,sys_player.nickname,sys_player.avatar + "/132",sys_player.gender,sys_player.roomId,sys_player.online,sys_player.cards, sys_player.gameId,sys_player.gold);
		players.put(passportId, player);
		return player;
	}
	
	public void logout(long playerId)
	{
		PlayerEntity player  = PlayerDBModel.getInstance().getEntity(playerId);
		if(player == null) return;
		if(player.online == 0) return;
		logger.info(playerId + "登出");
		player.online = 0;
		PlayerDBModel.getInstance().update(player);
	}

	@Override
	public Player getPlayer(long playerId)
	{
//		if(!this.players.containsKey(playerId))
//		{
		PlayerEntity entity = PlayerDBModel.getInstance().getEntity(playerId);
			if(entity == null) return null;
			Player player = new Player(entity.id,entity.nickname,entity.avatar + "/132",entity.gender,entity.roomId,entity.online,entity.cards,entity.gameId,entity.gold);
			this.players.put(playerId, player);
//		}
		return this.players.get(playerId);
	}

	@Override
	public void addCards(long playerId, int cards, int type, String description)
	{
		PlayerEntity sys_player = PlayerDBModel.getInstance().getEntity(playerId);
		if(sys_player == null) return;
		if(cards < 0 && sys_player.getCards() + cards < 0)
		{
			return;
		}
		sys_player.setCards(sys_player.getCards() + cards);
		PlayerDBModel.getInstance().update(sys_player);
		
		this.getPlayer(playerId).cards += cards;
		
//		if(sys_player.getOnline() > 0)
//		{
//			S2C_Player_CardsProto s2c = new S2C_Player_CardsProto();
//			s2c.setCards(sys_player.getCards());
//			s2c.setGold(sys_player.getGold());
//			ServiceUtil.getProxyService().sendMessage(playerId, s2c.toArray());
//		}
	}

	@Override
	public void setRoomId(long playerId, int roomId, int gameId)
	{
		PlayerEntity entity = PlayerDBModel.getInstance().getEntity(playerId);
		if(entity == null) return;
		entity.roomId = roomId;
		entity.gameId = gameId;
		if(roomId == 0)
		{
			logger.info(playerId + "离开房间");
		}
		else
		{
			logger.info(playerId + "进入房间" + roomId);
		}
		this.getPlayer(playerId).roomId = roomId;
		this.getPlayer(playerId).gameId = gameId;
		PlayerDBModel.getInstance().update(entity);
	}

	@Override
	public void addGold(long playerId, int gold)
	{
		PlayerEntity sys_player = PlayerDBModel.getInstance().getEntity(playerId);
		if(sys_player == null) return;
		sys_player.gold += gold;
		PlayerDBModel.getInstance().update(sys_player);
		
//		if(sys_player.getOnline() > 0)
//		{
//			S2C_Player_CardsProto s2c = new S2C_Player_CardsProto();
//			s2c.setCards(sys_player.getCards());
//			s2c.setGold(sys_player.getGold());
//			ServiceUtil.getProxyService().sendMessage(playerId, s2c.toArray());
//		}
	}

	@Override
	public List<Player> getPlayers(int roomId)
	{
//		List<sys_playerEntity> lstPlayer = sys_playerCacheModel.getInstance().getListByRoomId(roomId);
//		if(lstPlayer != null && lstPlayer.size() > 0)
//		{
//			List<Player> ret = new ArrayList<Player>();
//			for(int i = 0; i < lstPlayer.size(); ++i)
//			{
//				sys_playerEntity entity = lstPlayer.get(i);
//				Player player = new Player(entity.id,entity.nickname,entity.avatar + "/132",entity.gender,entity.roomId,entity.online,entity.latitude,entity.longitude,entity.cards,entity.gameId);
//				this.players.put(entity.id, player);
//				ret.add(player);
//			}
//			return ret;
//		}
//		return null;
		
		
		List<PlayerEntity> lstPlayer = PlayerDBModel.getInstance().getList("", "*", "roomId = " + roomId, "id", false, true);
		if(lstPlayer != null && lstPlayer.size() > 0)
		{
			List<Player> ret = new ArrayList<Player>();
			for(int i = 0; i < lstPlayer.size(); ++i)
			{
				PlayerEntity entity = lstPlayer.get(i);
				Player player = new Player(entity.id,entity.nickname,entity.avatar + "/132",entity.gender,entity.roomId,entity.online,entity.cards,entity.gameId,entity.gold);
				this.players.put(entity.id, player);
				ret.add(player);
			}
			return ret;
		}
		return null;
	}
}
