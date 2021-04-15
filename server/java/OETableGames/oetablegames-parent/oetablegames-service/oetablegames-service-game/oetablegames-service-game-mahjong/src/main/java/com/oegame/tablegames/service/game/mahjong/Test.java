package com.oegame.tablegames.service.game.mahjong;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.oegame.tablegames.common.util.TimeUtil;
import com.oegame.tablegames.service.game.mahjong.model.Poker;
import com.oegame.tablegames.service.game.mahjong.model.Seat;
import com.oegame.tablegames.service.game.mahjong.model.Tools;

public class Test {
	
	static Timer timer = new Timer();
	static TimerTask task;
	
	public static void main(String[] args) {
		
		MahjongDiscardAi ai = new MahjongDiscardAi();
		
		MahjongAlgorithm mahjong = new MahjongAlgorithm();

		int index = 0;
		ArrayList<Poker> poker = new ArrayList<>();

		// 装填万
		for (int size = 1; size <= 9; size++) {
			for (int c = 1; c <= 4; c++) {
				index++;
				poker.add(new Poker(index, 1, size));
			}
		}
		// 装填筒
		for (int size = 1; size <= 9; size++) {
			for (int i = 1; i <= 4; i++) {
				index++;
				poker.add(new Poker(index, 2, size));
			}
		}
		// 装填条
		for (int size = 1; size <= 9; size++) {
			for (int i = 1; i <= 4; i++) {
				index++;
				poker.add(new Poker(index, 3, size));
			}
		}

		for (int i = 1; i <= 4; i++) {
			index++;
			poker.add(new Poker(index, 5, 1));
		}

		Collections.shuffle(poker, new Random(TimeUtil.millisecond()));

		for (int i = 0; i < poker.size(); i++) {
			poker.get(i).index = i + 1;
		}
		
		Seat seat = new Seat(1);
		
		System.out.print(" 发牌 ：");
		for (int i = 0; i < 13; i++) {
			Poker pk = poker.remove(0);
			seat.poker.put(pk.index, pk);
			System.out.print(Tools.mahjong(pk.color, pk.size)+" ,");
		}
		System.out.print("\n");

		ArrayList<Poker> sprite = new ArrayList<Poker>();
		sprite.add(Tools.mahjong("0_红中").get(0));
	
		task = new TimerTask() {			
			@Override
			public void run() {
				Poker pk = poker.remove(0);
				seat.poker.put(pk.index, pk);
				System.out.println(" 摸牌  "+Tools.mahjong(pk.color, pk.size));
				if (mahjong.checkHu(seat.poker, sprite, true, null, null, false).code > 0) {
					System.out.println("---------胡了,结束---------");
					task.cancel();
					timer = null;
				}else {
					long begin = System.currentTimeMillis();
					int suoyin = ai.discardPoker(seat, null);
					Poker pk1 = seat.poker.remove(suoyin);
					System.out.println(" 出牌  "+Tools.mahjong(pk1.color, pk1.size));
					System.out.println("ms: " + (System.currentTimeMillis() - begin));
				}
			}
		};
		timer.schedule(task, 5000,5000);
				
		
	}

	
}
