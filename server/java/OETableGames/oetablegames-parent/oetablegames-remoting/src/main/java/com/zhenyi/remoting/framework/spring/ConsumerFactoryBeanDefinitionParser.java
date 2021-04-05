package com.zhenyi.remoting.framework.spring;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

import com.zhenyi.remoting.framework.consumer.ConsumerFactoryBean;

/**
 * 服务消费者自定义标签解析器
 * @author Binge
 *
 */
public class ConsumerFactoryBeanDefinitionParser extends AbstractSingleBeanDefinitionParser
{
	private static final Logger logger = LoggerFactory.getLogger(ConsumerFactoryBeanDefinitionParser.class);

	protected Class<?> getBeanClass(Element element)
	{
		return ConsumerFactoryBean.class;
	}

	protected void doParse(Element element, BeanDefinitionBuilder bean)
	{

		try
		{
			String timeOut = element.getAttribute("timeout");
			String targetInterface = element.getAttribute("interface");
			String clusterStrategy = element.getAttribute("clusterStrategy");
			String remoteAppKey = element.getAttribute("remoteAppKey");
			String groupName = element.getAttribute("groupName");
			
			bean.addPropertyValue("timeout", Integer.parseInt(timeOut));
			bean.addPropertyValue("targetInterface", Class.forName(targetInterface));
			bean.addPropertyValue("remoteAppKey", remoteAppKey);

			if (StringUtils.isNotBlank(clusterStrategy))
			{
				bean.addPropertyValue("clusterStrategy", clusterStrategy);
			}
			if (StringUtils.isNotBlank(groupName))
			{
				bean.addPropertyValue("groupName", groupName);
			}
		} 
		catch (Exception e)
		{
			logger.error("ConsumerFactoryBeanDefinitionParser error.", e);
			throw new RuntimeException(e);
		}

	}
}
