package com.oegame.tablegames.common.net.socket;

import org.apache.mina.core.session.IoSession;

public interface IRout
{
	public void call(IoSession session, int code, byte[] data);
}
