package com.project.constant;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 描述: 站点公用常量类
 * 版权: Copyright (c) 2010
 * 公司: 思迪
 * 作者: 李建  lijian@thinkive.com
 * 版本: 1.0
 * 创建日期: Mar 11, 2010
 * 创建时间: 5:29:43 PM
 */
public class WebConstants extends HashMap
{
	
	
	private static Log log = LogFactory.getLog(WebConstants.class);
	
	
	private static Map map;
	
	
	
	//	>>>>>>>>>>>>>>>>>>>>>>>>>> 站点相关常量 开始	
	
	/**
	 * web主站站点
	 */
	public static final String SITE_MAIN = "main";
	
	
	
	//	<<<<<<<<<<<<<<<<<<<<<<<<<< 站点相关常量 结束
	
	
	//	>>>>>>>>>>>>>>>>>>>>>>>>>> 数据库相关常量 开始
	
	/**
	 * cc系统所对应的数据库
	 */
	public static final String DB_CC = "cc";
	
	
	
	/**
	 * web网站所对应的数据库
	 */
	public static final String DB_WEB = "web";
	
	
	
	//	<<<<<<<<<<<<<<<<<<<<<<<<<< 数据库相关常量 结束
	
	
	//	>>>>>>>>>>>>>>>>>>>>>>>>>> 文章相关常量 开始
	
	/**
	 * 文章已发布状态
	 */
	public static final int ARTICLE_STATE_PUBLISHED = 3;
	
	
	public static final int ARTICLE_IS_HEAD = 1;
	
	
	
	/**
	 * 文章展示时包涵子栏目
	 */
	public static final int ARTICLE_LIST_IS_HAVE_CHILDREN = 1;
	
	
	
	//	<<<<<<<<<<<<<<<<<<<<<<<<<< 文章相关常量 结束
	
	
	//	>>>>>>>>>>>>>>>>>>>>>>>>>> SESSION用户相关常量 开始
	/**
	 * session中保存的客户登陆IDkey
	 */
	public static final String SESSION_CLIENT_LOGIN_ID = "@home_client_loginid";
	
	
	
	/**
	 * session中保存的客户自增长id的key
	 */
	public static final String SESSION_CLIENT_ID = "@home_client_id";
	
	
	
	/**
	 * session中保存的客户的姓名
	 */
	public static final String SESSION_CLIENT_NAME = "@home_client_name";
	
	
	
	/**
	 * session中保存的客户cif中的姓名
	 */
	public static final String SESSION_CIF_CLIENT_NAME = "@home_cif_client_name";
	
	
	
	/**
	 * session中保存的客户的类型
	 */
	public static final String SESSION_CLIENT_TYPE = "@home_client_type";
	
	
	
	/**
	 * session中保存的客户的级别
	 */
	public static final String SESSION_CLIENT_LEVEL = "@home_client_level";
	
	
	
	/**
	 * session中保存的客户的属性
	 */
	public static final String SESSION_CLIENT_ATTRIBUTE = "@home_client_attribute";
	
	
	
	/**
	 * session中保存的客户资金账号的key
	 */
	public static final String SESSION_CLIENT_ASSETACCOUNT = "@home_client_assetaccount";
	
	
	
	/**
	 * session中保存的客户资金账号的密码
	 */
	public static final String SESSION_CLIENT_ASSET_PASSWORD = "@trade_client_asset_password";
	
	
	
	/**
	 * session中保存的客户网点
	 */
	public static final String SESSION_CLIENT_BRANCHNO = "@home_client_branchno";
	
	
	
	/**
	 * session中保存的客户经理ID
	 */
	public static final String SESSION_CLIENT_MNG_ID = "@home_client_mng_id";
	
	
	
	/**
	 * session中保存的客户经理Name
	 */
	public static final String SESSION_CLIENT_MNG_NAME = "@home_client_mng_name";
	
	
	
	//	<<<<<<<<<<<<<<<<<<<<<<<<<< SESSION用户相关常量 结束
	
	
	//	>>>>>>>>>>>>>>>>>>>>>>>>>> COOKIE用户相关常量 开始
	
	/**
	 * cookie中保存用户信息的key name
	 */
	public static final String COOKIE_CLIENT_INFO_KEY = "user_login";
	
	
	
	//	<<<<<<<<<<<<<<<<<<<<<<<<<< COOKIE用户相关常量 结束
	
	
	//	>>>>>>>>>>>>>>>>>>>>>>>>>> 用户注册信息相关常量 开始
	/**
	 * 是否绑定
	 */
	public static final String CLIENT_IS_VALIDATE = "1";
	
	
	
	/**
	 * 邮件未验证
	 */
	public static final String CLIENT_MAIL_NO_VALIDATE = "0";
	
	
	
	/**
	 * 邮件已经验证
	 */
	public static final String CLIENT_MAIL_VALIDATE = "1";
	
	
	
	/**
	 * 手机号未验证
	 */
	public static final String CLIENT_PHONE_NO_VALIDATE = "0";
	
	
	
	/**
	 * 手机号已验证
	 */
	public static final String CLIENT_PHONE_VALIDATE = "1";
	
	
	
	//	<<<<<<<<<<<<<<<<<<<<<<<<<< 用户注册信息相关常量 结束
	
	
	//	>>>>>>>>>>>>>>>>>>>>>>>>>> 其它常量 开始
	
	/**
	 * 股票搜索类型
	 */
	public static final String MY_STOCK_TYPE = "0;1;2;3;4;9;10;11;12;18;";
	
	
	
	
	//	<<<<<<<<<<<<<<<<<<<<<<<<<< 其它常量 结束
	
	
	//	>>>>>>>>>>>>>>>>>>>>>>>>>> 临时常量 开始
	
	
	//	<<<<<<<<<<<<<<<<<<<<<<<<<< 临时常量 结束
	
	/**
	 * 使用该构造方法实现一个JSTL可访问的系统常量，
	 */
	public WebConstants()
	{
		// initialize only once...
		if (map != null)
			return;
		
		map = new HashMap();
		Class c = this.getClass();
		Field[] fields = c.getDeclaredFields();
		for (int i = 0; i < fields.length; i++)
		{
			Field field = fields[i];
			int modifier = field.getModifiers();
			//排除private类型
			if (Modifier.isFinal(modifier) && !Modifier.isPrivate(modifier))
			{
				try
				{
					this.put(field.getName(), field.get(this));
				}
				catch (IllegalAccessException e)
				{
					//e.printStackTrace();
					log.error(e);
				}
			}
		}
	}
	
	
	@Override
	public Object get(Object key)
	{
		return map.get(key);
	}
	
	
	@Override
	public Object put(Object key, Object value)
	{
		return map.put(key, value);
	}
	
}
