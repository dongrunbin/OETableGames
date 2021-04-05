package com.oegame.tablegames.common.net.socket;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class SocketCodecFactory implements ProtocolCodecFactory {

	private final ProtocolDecoder decoder;

	private final ProtocolEncoder encoder;

	public SocketCodecFactory(ProtocolDecoder decoder, ProtocolEncoder encoder) 
	{
		this.decoder = decoder;
		this.encoder = encoder;
	}

	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}

	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}

}