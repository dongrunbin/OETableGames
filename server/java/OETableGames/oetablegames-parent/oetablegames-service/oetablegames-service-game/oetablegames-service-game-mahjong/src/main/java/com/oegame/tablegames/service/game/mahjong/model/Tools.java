package com.oegame.tablegames.service.game.mahjong.model;

import java.util.ArrayList;

import com.google.gson.Gson;

public class Tools {

	/**
	 * 
	 */
	public static Gson gson = new Gson();

	/**
	 * 
	 * @param colorId
	 * @param sizeId
	 * @return
	 */
	public static String mahjong(int colorId, int sizeId) {
		String color = "";
		String size = sizeId + "";

		switch (colorId) {
		case 1:
			color = "万";
			break;
		case 2:
			color = "筒";
			break;
		case 3:
			color = "条";
			break;
		case 4: //东西南北
			size = "风";
			break;
		case 5: //中发白
			color = "";
			break;
		case 6: //春夏秋冬
			color = "";
			break;
		case 7: //梅兰竹菊
			color = "";
			break;
		}

		if (colorId == 4) {
			switch (sizeId) {
			case 1:
				size = "东";
				break;
			case 2:
				size = "南";
				break;
			case 3:
				size = "西";
				break;
			case 4:
				size = "北";
				break;
			}
		}
		if (colorId == 5) {
			switch (sizeId) {
			case 1:
				size = "红中";
				break;
			case 2:
				size = "发财";
				break;
			case 3:
				size = "白板";
				break;
			}
		}
		if (colorId == 6) {
			switch (sizeId) {
			case 1:
				size = "春";
				break;
			case 2:
				size = "夏";
				break;
			case 3:
				size = "秋";
				break;
			case 4:
				size = "冬";
				break;
			}
		}
		if (colorId == 7) {
			switch (sizeId) {
			case 1:
				size = "梅";
				break;
			case 2:
				size = "兰";
				break;
			case 3:
				size = "竹";
				break;
			case 4:
				size = "菊";
				break;
			}
		}
		return size + color;
	}

	public static ArrayList<Poker> mahjong(ArrayList<Poker> pokers, String... poker) {
		ArrayList<Poker> res = new ArrayList<Poker>();
		ArrayList<Poker> pkr = mahjong(poker);

		for (int i = 0; i < pkr.size(); i++) {
			for (int j = 0; j < pokers.size(); j++) {
				if (pkr.get(i).color == pokers.get(j).color && pkr.get(i).size == pokers.get(j).size) {
					res.add(new Poker(pokers.get(j).index, pokers.get(j).color, pokers.get(j).size));
					pokers.remove(j);
					break;
				}
			}
		}

		return res;
	}

	/**
	 * 
	 * @param poker
	 * @return
	 */
	public static ArrayList<Poker> mahjong(String... poker) {
		ArrayList<Poker> mj = new ArrayList<Poker>();

		for (int i = 0; i < poker.length; i++) {

			String[] pk = poker[i].split("_");

			Poker pb_poker = new Poker();

			if (pk[1].equals("万")) {

				pb_poker.setIndex(i);
				pb_poker.setColor(1);
				pb_poker.setSize(Integer.parseInt(pk[0]));

			} else if (pk[1].equals("筒")) {

				pb_poker.setIndex(i);
				pb_poker.setColor(2);
				pb_poker.setSize(Integer.parseInt(pk[0]));

			} else if (pk[1].equals("条")) {

				pb_poker.setIndex(i);
				pb_poker.setColor(3);
				pb_poker.setSize(Integer.parseInt(pk[0]));

			} else if (pk[1].equals("红中")) {

				pb_poker.setIndex(i);
				pb_poker.setColor(5);
				pb_poker.setSize(1);

			} else if (pk[1].equals("发财")) {

				pb_poker.setIndex(i);
				pb_poker.setColor(5);
				pb_poker.setSize(2);

			} else if (pk[1].equals("白板")) {

				pb_poker.setIndex(i);
				pb_poker.setColor(5);
				pb_poker.setSize(3);

			} else if (pk[1].equals("东")) {

				pb_poker.setIndex(i);
				pb_poker.setColor(4);
				pb_poker.setSize(1);

			} else if (pk[1].equals("南")) {

				pb_poker.setIndex(i);
				pb_poker.setColor(4);
				pb_poker.setSize(2);

			} else if (pk[1].equals("西")) {

				pb_poker.setIndex(i);
				pb_poker.setColor(4);
				pb_poker.setSize(3);

			} else if (pk[1].equals("北")) {

				pb_poker.setIndex(i);
				pb_poker.setColor(4);
				pb_poker.setSize(4);

			} else if (pk[1].equals("春")) {

				pb_poker.setIndex(i);
				pb_poker.setColor(6);
				pb_poker.setSize(1);

			} else if (pk[1].equals("夏")) {

				pb_poker.setIndex(i);
				pb_poker.setColor(6);
				pb_poker.setSize(2);

			} else if (pk[1].equals("秋")) {

				pb_poker.setIndex(i);
				pb_poker.setColor(6);
				pb_poker.setSize(3);

			} else if (pk[1].equals("冬")) {

				pb_poker.setIndex(i);
				pb_poker.setColor(6);
				pb_poker.setSize(4);

			}

			mj.add(pb_poker);

		}

		return mj;
	}
}
