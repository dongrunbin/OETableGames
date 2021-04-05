package com.zhenyi.remoting.framework.zookeeper;

import com.zhenyi.remoting.framework.model.ProviderService;

public interface IServiceListener
{
	public void onServiceRegiste(ProviderService service);
	
	public void onServiceRemove(ProviderService service);
	
	public void onDisconnect();
}
