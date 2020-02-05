package com.project.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.project.utils.MemCacheServer;
import com.thinkive.base.config.Configuration;
import com.thinkive.base.jdbc.connection.Configure;
import com.thinkive.web.system.Application;

/**
 * 描述:
 * 版权:	 Copyright (c) 2005
 * 公司:	 思迪科技
 * 作者:	 易庆锋
 * 版本:	 1.0
 * 创建日期: 2006-10-11
 * 创建时间: 11:13:50
 */
public class ApplicationLifecycleListener implements ServletContextListener
{
	
	private ServletContext context = null;
	
	private static Logger logger = Logger.getLogger(ApplicationLifecycleListener.class);
	
	/**
	 * 在系统启动时调用
	 *
	 * @param event a ServletContextEvent instance
	 */
	public void contextInitialized(ServletContextEvent event)
	{
		if (logger.isInfoEnabled())
			logger.info("Starting application......");
		
		context = event.getServletContext();
		init();
	}
	
	/**
	 * 在系统停止时调用
	 *
	 * @param event a ServletContextEvent instance
	 */
	public void contextDestroyed(ServletContextEvent event)
	{
		//关闭数据源的连接
		Configure.getInstance().destroyDataSource();
		
		if (logger.isInfoEnabled())
			logger.info("Stopping application......");
	}
	
	/**
	 * 系统启动时初始化相应的数据
	 */
	private void init()
	{
		//初始应用程序根目录路径
		Application.setRootPath(context.getRealPath("/"));
		
		//读入数据库配置文件
		Configure.getInstance();
		
		MemCacheServer.getInstance();
		
		
	}
	
}
