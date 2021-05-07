package com.oegame.tablegames.service.proxy;

import com.oegame.tablegames.protocol.gen.System_S2C_ErrorProto;
import org.apache.mina.core.session.IoSession;

import com.oegame.tablegames.common.log.Logger;
import com.oegame.tablegames.common.log.LoggerFactory;
import com.oegame.tablegames.common.net.socket.SocketServer;
import com.oegame.tablegames.protocol.gen.ProtoCodeDef;
//import com.oegame.tablegames.protocol.gen.S2C_System_ErrorProto;

public class ProxyServiceImpl implements ProxyService
{
	private static final Logger logger = LoggerFactory.getLogger(ProxyServiceImpl.class);
	public ProxyServiceImpl()
	{
		new SocketServer().start(new SocketHandler(), new SocketRoute(),10917);
	}

	@Override
	public void sendMessage(long playerId, byte[] data)
	{
		ClientRole client = ClientManager.getInstance().getPlayer(playerId);
		if (client == null) return;
		IoSession session = client.getSession();
		if (data != null) 
		{
			int a = (data[0] & 0xff) << 24;
			int b = (data[1] & 0xff) << 16;
			int c = (data[2] & 0xff) << 8;
			int d = data[3] & 0xff;
			int code = a | b | c | d;
			if(code != ProtoCodeDef.System_S2C_HeartBeatProto)
			{
				logger.info(String.format("服务器发送消息给%d:%d, %s, %s",session.getAttribute("playerId"),code, ProtoCodeDef.getEn(code), ProtoCodeDef.getCn(code)));
			}
		}
		session.write(data);
	}

	@Override
	public void sendError(long playerId, int errorCode, String message)
	{
		System_S2C_ErrorProto proto = new System_S2C_ErrorProto();
		proto.setCode(errorCode);
		proto.setMessage(message);
		sendMessage(playerId,proto.toArray());
	}

}
