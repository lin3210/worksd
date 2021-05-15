package com.task;

import java.util.List;
import org.apache.log4j.Logger;

import com.service.AotuSDFKService;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.timerengine.Task;
import com.util.SDFKUtil;
/**
 * �Զ�ת��task
 * @author Administrator
 *
 */
public class AotuSDFKCxTask implements Task
{

	private static Logger logger = Logger.getLogger(AotuSDFKCxTask.class);
	private static AotuSDFKService aotuSDFKService = new AotuSDFKService();
	@Override
	public void execute() 
	{
		//������Ҫ��ѯ�����
		List<DataRow> list = aotuSDFKService.getAllFKCxList();
		for (DataRow dataRow : list) 
		{
			try {
				SDFKUtil.fy_query(dataRow);//����
			} catch (Exception e) {
				
				logger.error(e);
			}
		}
	}
	
	public static void main(String[] args) {
		   //������Ҫ��ѯ�����
				List<DataRow> list = aotuSDFKService.getAllFKCxList();
				for (DataRow dataRow : list) 
				{
				   try {   
						SDFKUtil.fy_query(dataRow);//����
						
					} catch (Exception e) {	
						
						logger.error(e);
					}
				}
	}
}
