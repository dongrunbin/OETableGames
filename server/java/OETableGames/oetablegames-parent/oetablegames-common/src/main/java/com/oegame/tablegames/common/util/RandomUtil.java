package com.oegame.tablegames.common.util;

public class RandomUtil
{
	public static int Range(int min, int max)
	{
		return min + (int)(Math.random() * (max - min));
	}
}
