package com.oegame.tablegames.service;

import com.oegame.tablegames.service.game.mahjong.MahjongService;
import com.zhenyi.remoting.framework.context.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.oegame.tablegames.service.player.PlayerService;
import com.oegame.tablegames.service.proxy.ProxyService;

public class ServiceUtil
{
    private static Logger logger = LoggerFactory.getLogger(ServiceUtil.class);

    private static ApplicationContext context;

    private static PlayerService playerService;

    private static ProxyService proxyService;

    private static MahjongService mahjongService;

    public static void init(String... names)
    {
        if(context != null) return;
        context = new ApplicationContext(names);
        if(playerService == null && context.containsBean("playerService"))
        {
            playerService = context.getBean("playerService");
        }
        if(proxyService == null && context.containsBean("proxyService"))
        {
            proxyService = context.getBean("proxyService");
        }
        if(mahjongService == null && context.containsBean("mahjongService"))
        {
            mahjongService = context.getBean("mahjongService");
        }
    }

    public static ProxyService getProxyService()
    {
        return proxyService;
    }

    public static PlayerService getPlayerService() { return playerService; }

    public static MahjongService getMahjongService() { return mahjongService; }
}
