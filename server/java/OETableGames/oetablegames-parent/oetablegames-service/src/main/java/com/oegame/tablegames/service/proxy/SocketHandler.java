package com.oegame.tablegames.service.proxy;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketSessionConfig;

import com.oegame.tablegames.common.log.Logger;
import com.oegame.tablegames.common.log.LoggerFactory;
import com.oegame.tablegames.service.ServiceUtil;

public class SocketHandler extends IoHandlerAdapter
{

	private static final Logger logger = LoggerFactory.getLogger(SocketHandler.class);

	@Override
	public void sessionCreated(IoSession session) throws Exception
	{
		logger.info("sessionCreated.." + session.getRemoteAddress());

		SocketSessionConfig cfg = (SocketSessionConfig) session.getConfig();
		cfg.setTcpNoDelay(true);
		cfg.setKeepAlive(true);
		cfg.setReuseAddress(true);
		// cfg.setSoLinger(-1);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception
	{
		logger.info("sessionOpened.." + session.getRemoteAddress());
	}

	// 当有客户端关闭
	@Override
	public void sessionClosed(IoSession session) throws Exception
	{
		if (session.getAttribute("TokenMark") != null)
		{
			long playerId = (long) session.getAttribute("playerId");
			logger.info("客户端主动断开连接" + playerId);
			session.removeAttribute("TokenMark");
			session.removeAttribute("playerId");
			ServiceUtil.getPlayerService().logout(playerId);
			ClientManager.getInstance().removePlayer(playerId);
		}
		super.sessionClosed(session);
		session.closeNow();
	}

	// 当接口中其他方法抛出异常未被捕获时触发此方法
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception
	{
		if (session.getAttribute("TokenMark") != null)
		{
			long playerId = (long) session.getAttribute("playerId");
			logger.info("客户端异常断开连接" + playerId + cause.toString());
			session.removeAttribute("TokenMark");
			session.removeAttribute("playerId");
			ServiceUtil.getPlayerService().logout(playerId);
			ClientManager.getInstance().removePlayer(playerId);
		}
		session.closeNow();
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception
	{

	}

	@Override
	public void messageSent(IoSession session, Object message)
	{

	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception
	{
		session.closeOnFlush();
	}
}
