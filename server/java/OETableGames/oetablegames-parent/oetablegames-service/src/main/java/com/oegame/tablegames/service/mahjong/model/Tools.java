package com.oegame.tablegames.service.mahjong.model;

import java.util.ArrayList;

public class Tools {

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

	public static ArrayList<Mahjong> mahjong(ArrayList<Mahjong> mahjongs, String... mahjong) {
		ArrayList<Mahjong> res = new ArrayList<Mahjong>();
		ArrayList<Mahjong> pkr = mahjong(mahjong);

		for (int i = 0; i < pkr.size(); i++) {
			for (int j = 0; j < mahjongs.size(); j++) {
				if (pkr.get(i).color == mahjongs.get(j).color && pkr.get(i).size == mahjongs.get(j).size) {
					res.add(new Mahjong(mahjongs.get(j).index, mahjongs.get(j).color, mahjongs.get(j).size));
					mahjongs.remove(j);
					break;
				}
			}
		}

		return res;
	}

	/**
	 * 
	 * @param mahjong
	 * @return
	 */
	public static ArrayList<Mahjong> mahjong(String... mahjong) {
		ArrayList<Mahjong> mj = new ArrayList<Mahjong>();

		for (int i = 0; i < mahjong.length; i++) {

			String[] pk = mahjong[i].split("_");

			Mahjong pb_mahjong = new Mahjong();

			if (pk[1].equals("万")) {

				pb_mahjong.setIndex(i);
				pb_mahjong.setColor(1);
				pb_mahjong.setSize(Integer.parseInt(pk[0]));

			} else if (pk[1].equals("筒")) {

				pb_mahjong.setIndex(i);
				pb_mahjong.setColor(2);
				pb_mahjong.setSize(Integer.parseInt(pk[0]));

			} else if (pk[1].equals("条")) {

				pb_mahjong.setIndex(i);
				pb_mahjong.setColor(3);
				pb_mahjong.setSize(Integer.parseInt(pk[0]));

			} else if (pk[1].equals("红中")) {

				pb_mahjong.setIndex(i);
				pb_mahjong.setColor(5);
				pb_mahjong.setSize(1);

			} else if (pk[1].equals("发财")) {

				pb_mahjong.setIndex(i);
				pb_mahjong.setColor(5);
				pb_mahjong.setSize(2);

			} else if (pk[1].equals("白板")) {

				pb_mahjong.setIndex(i);
				pb_mahjong.setColor(5);
				pb_mahjong.setSize(3);

			} else if (pk[1].equals("东")) {

				pb_mahjong.setIndex(i);
				pb_mahjong.setColor(4);
				pb_mahjong.setSize(1);

			} else if (pk[1].equals("南")) {

				pb_mahjong.setIndex(i);
				pb_mahjong.setColor(4);
				pb_mahjong.setSize(2);

			} else if (pk[1].equals("西")) {

				pb_mahjong.setIndex(i);
				pb_mahjong.setColor(4);
				pb_mahjong.setSize(3);

			} else if (pk[1].equals("北")) {

				pb_mahjong.setIndex(i);
				pb_mahjong.setColor(4);
				pb_mahjong.setSize(4);

			} else if (pk[1].equals("春")) {

				pb_mahjong.setIndex(i);
				pb_mahjong.setColor(6);
				pb_mahjong.setSize(1);

			} else if (pk[1].equals("夏")) {

				pb_mahjong.setIndex(i);
				pb_mahjong.setColor(6);
				pb_mahjong.setSize(2);

			} else if (pk[1].equals("秋")) {

				pb_mahjong.setIndex(i);
				pb_mahjong.setColor(6);
				pb_mahjong.setSize(3);

			} else if (pk[1].equals("冬")) {

				pb_mahjong.setIndex(i);
				pb_mahjong.setColor(6);
				pb_mahjong.setSize(4);

			}

			mj.add(pb_mahjong);

		}

		return mj;
	}
}
