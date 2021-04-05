package com.oegame.tablegames.common.net.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executors;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LogLevel;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SocketServer 
{
	private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);
	/**
	 * 监听即将到来的TCP连接
	 */
	private IoAcceptor acceptor = new NioSocketAcceptor();

	/**
	 * 启动
	 * 
	 */
	public void start(IoHandlerAdapter handler, IRout rout, int port) 
	{
		LoggingFilter log = new LoggingFilter();
		log.setMessageReceivedLogLevel(LogLevel.NONE);
		log.setMessageSentLogLevel(LogLevel.NONE);
		log.setSessionClosedLogLevel(LogLevel.NONE);
		log.setSessionCreatedLogLevel(LogLevel.NONE);
		log.setSessionIdleLogLevel(LogLevel.NONE);
		log.setSessionOpenedLogLevel(LogLevel.NONE);
		this.acceptor.getFilterChain().addLast("logger", log);

		this.acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new SocketCodecFactory(new SocketDecoder(rout),new SocketEncoder())));

		this.acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));

		this.acceptor.setHandler(handler);

		this.acceptor.getSessionConfig().setMinReadBufferSize(4096);

		this.acceptor.getSessionConfig().setMaxReadBufferSize(8192);

		this.acceptor.getSessionConfig().setThroughputCalculationInterval(10);

		this.acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 30);

		this.acceptor.getSessionConfig().setWriteTimeout(5);

		try {
			this.acceptor.bind(new InetSocketAddress(port));
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.info("\n-================================-");
		logger.info("SocketServer Start Done.");
		logger.info("\n-================================-");
	}

	public Map<Long, IoSession> sessions() {
		return this.acceptor.getManagedSessions();
	}

	public void shutdown() 
	{
		logger.info("this.acceptor.getManagedSessionCount(): " + this.acceptor.getManagedSessionCount());

		this.acceptor.unbind();

		this.acceptor.dispose(true);

		logger.info("SocketServer shutdown");
	}
}
