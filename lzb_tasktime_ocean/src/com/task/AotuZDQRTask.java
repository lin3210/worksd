package com.task;

import java.util.List;
import org.apache.log4j.Logger;

import com.service.AotuZDQRService;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.timerengine.Task;
/**
 * �Զ���˽��
 * @author Administrator
 *
 */
public class AotuZDQRTask implements Task
{

	private static Logger logger = Logger.getLogger(AotuZDQRTask.class);
	private static AotuZDQRService aotuZDQRService = new AotuZDQRService();
	@Override
	public void execute() 
	{
		
		List<DataRow> list = aotuZDQRService.getZDQRList();
		for (DataRow dataRow : list) 
		{
			try {				
			
				// ȷ��Ͷ��
			   	
			} catch (Exception e) {
				
				logger.error(e);
			}
		}
		
	
	}
    
	public static void main(String[] args) 
	{
		
						
	}
	
	
}
