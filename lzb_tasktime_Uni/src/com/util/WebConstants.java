package com.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ����: վ�㹫�ó�����
 * ��Ȩ: Copyright (c) 2010
 * ��˾: ˼��
 * ����: �  lijian@thinkive.com
 * �汾: 1.0
 * ��������: Mar 11, 2010
 * ����ʱ��: 5:29:43 PM
 */
public class WebConstants extends HashMap
{
	
	
	private static Log log = LogFactory.getLog(WebConstants.class);
	
	
	private static Map map;
	
	
	
	//	>>>>>>>>>>>>>>>>>>>>>>>>>> վ����س��� ��ʼ	
	
	/**
	 * web��վվ��
	 */
	public static final String SITE_MAIN = "main";
	
	/**
	 * �ֻ����KEY
	 */
	public static final String APIKEY = "e781ca9195f427ac024200afc1c542b5";
	
	
	public static String TICKET = "com.thinkive.cms.system.admin.ticket";
	
	
	
	//	<<<<<<<<<<<<<<<<<<<<<<<<<< ��ʱ���� ����
	
	/**
	 * ʹ�øù��췽��ʵ��һ��JSTL�ɷ��ʵ�ϵͳ������
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
			//�ų�private����
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
