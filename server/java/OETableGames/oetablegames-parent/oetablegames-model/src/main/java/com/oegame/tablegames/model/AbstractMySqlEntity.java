package com.oegame.tablegames.model;

public abstract class AbstractMySqlEntity
{
	
	public long id;
	
	public abstract byte[] serialize();
}
