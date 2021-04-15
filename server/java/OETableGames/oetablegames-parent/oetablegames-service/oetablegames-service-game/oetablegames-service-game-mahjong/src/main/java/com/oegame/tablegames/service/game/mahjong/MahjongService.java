package com.oegame.tablegames.service.game.mahjong;

import java.util.ArrayList;
import com.oegame.tablegames.service.game.GameService;

public interface MahjongService extends GameService
{
	public void discard(int roomId,long playerId, int index,boolean isTing);

	public void pass(int roomId,long playerId);

	public void operate(int roomId,long playerId, byte typeId, ArrayList<Integer> index);
}
