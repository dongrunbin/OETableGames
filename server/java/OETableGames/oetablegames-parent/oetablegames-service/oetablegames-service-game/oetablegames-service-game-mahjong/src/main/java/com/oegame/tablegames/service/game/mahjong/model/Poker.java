package com.oegame.tablegames.service.game.mahjong.model;


public class Poker
{
	private static final long serialVersionUID = 1L;

	public int index = 0;
	public int color = 0;
	public int size = 0;
	public int pos = 0;

	public int amount = 0;

	public Poker(int index, int color, int size)
	{
		this.index = index;
		this.color = color;
		this.size = size;
	}

	public Poker()
	{

	}

	public void setIndex(int value)
	{
		this.index = value;
	}

	public void setColor(int value)
	{
		this.color = value;
	}

	public void setSize(int value)
	{
		this.size = value;
	}
}

