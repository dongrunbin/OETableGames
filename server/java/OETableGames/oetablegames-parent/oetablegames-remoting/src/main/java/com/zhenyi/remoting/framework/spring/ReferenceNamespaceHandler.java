package com.zhenyi.remoting.framework.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * 服务引入自定义标签
 * 
 * @author Binge
 *
 */
public class ReferenceNamespaceHandler extends NamespaceHandlerSupport
{
	@Override
	public void init()
	{
		registerBeanDefinitionParser("reference", new ConsumerFactoryBeanDefinitionParser());
	}
}
