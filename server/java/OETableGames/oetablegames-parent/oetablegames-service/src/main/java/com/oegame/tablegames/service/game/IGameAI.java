package com.oegame.tablegames.service.game;

public interface IGameAI
{
	
	void setRoomCtrl(RoomCtrlBase ctrl);
	
	long getTimeSpan();
	
	void doAI();
}
