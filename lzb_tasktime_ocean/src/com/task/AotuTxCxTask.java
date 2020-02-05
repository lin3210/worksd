package com.task;

import java.util.List;
import org.apache.log4j.Logger;

import com.service.AotuTxService;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.connection.Configure;
import com.thinkive.timerengine.Task;
import com.util.TxUtil;
/**
 * �Զ�ת��task
 * @author Administrator
 *
 */
public class AotuTxCxTask implements Task
{

	private static Logger logger = Logger.getLogger(AotuTxCxTask.class);
	private static AotuTxService aotuTxService = new AotuTxService();
	@Override
	public void execute() 
	{
		//������Ҫ��ѯ�����
		List<DataRow> list = aotuTxService.getAllTxCxList();
		for (DataRow dataRow : list) 
		{
			try {
				TxUtil.rb_query(dataRow);//�ڱ�
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e);
			}
		}
	}
	
	public static void main(String[] args) {
		Configure.getInstance();
		//������Ҫ��ѯ�����
		List<DataRow> list = aotuTxService.getAllTxCxList();
		for (DataRow dataRow : list) 
		{
			try {
				TxUtil.rb_query(dataRow);//�ڱ�
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e);
			}
		}
	}
}
