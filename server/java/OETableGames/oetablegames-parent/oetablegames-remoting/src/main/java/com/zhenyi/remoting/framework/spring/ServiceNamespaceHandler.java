package com.zhenyi.remoting.framework.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * 服务发布自定义标签
 * 
 * @author Binge
 *
 */
public class ServiceNamespaceHandler extends NamespaceHandlerSupport
{
	@Override
	public void init()
	{
		registerBeanDefinitionParser("service", new ProviderFactoryBeanDefinitionParser());
	}
}
