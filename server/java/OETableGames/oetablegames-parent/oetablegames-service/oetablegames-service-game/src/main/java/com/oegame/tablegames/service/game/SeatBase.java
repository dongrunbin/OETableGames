package com.oegame.tablegames.service.game;


import com.oegame.tablegames.service.player.Player;
import java.io.Serializable;

public abstract class SeatBase implements Serializable
{
	private static final long serialVersionUID = 1L;

		public long playerId = 0;
		public SeatStatus status = SeatStatus.SEAT_STATUS_IDLE;
		public Player player;
		public boolean isBanker = false;
		public boolean isAfk = false;
		public int pos = 0;
		public long countdown = 0;
		public DisbandStatus dismiss = DisbandStatus.DISSMISS_IDLE;
		public boolean winner = false;
		public boolean loser = false;
		public int gold = 0;
		public int initGold = 0;
		public int settle = 0;
		public boolean isLeave = false;
		
		public SeatBase(int pos)
		{
			this.pos = pos;
		}
		
		public void clear()
		{
			playerId = 0;
			player = null;
			status = SeatStatus.SEAT_STATUS_IDLE;
			gold = 0;
			initGold = 0;
			settle = 0;
			winner = false;
			loser = false;
			dismiss = DisbandStatus.DISSMISS_IDLE;
			isBanker = false;
			isAfk = false;
			countdown = 0;
			isLeave = false;
		}
		
		public abstract SeatBase clone();
}
