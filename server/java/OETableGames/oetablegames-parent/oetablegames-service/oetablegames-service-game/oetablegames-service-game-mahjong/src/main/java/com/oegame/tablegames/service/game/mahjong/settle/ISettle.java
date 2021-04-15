package com.oegame.tablegames.service.game.mahjong.settle;

import com.oegame.tablegames.service.game.mahjong.Room;
import com.oegame.tablegames.service.game.mahjong.model.SettleVO;

public interface ISettle {

	//正常计算胡分
	public void huSettle(int winer,int loser,boolean isSelf,Room room);
	
	//计算胡分
	public void huScore(int winer,int loser,boolean isSelf,SettleVO res,Room room);
	
	//计算杠分
	public void gangScore(Room room);
	
}
