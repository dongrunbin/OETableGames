package com.oegame.tablegames.service.mahjong;

import java.util.ArrayList;
import com.oegame.tablegames.service.game.GameService;

public interface MahjongService extends GameService
{
	void discard(int roomId,long playerId, int index);

	void pass(int roomId,long playerId);

	void operate(int roomId,long playerId, byte typeId, ArrayList<Integer> index);
}
