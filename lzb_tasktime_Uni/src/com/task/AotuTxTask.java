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
public class AotuTxTask implements Task
{

	private static Logger logger = Logger.getLogger(AotuTxTask.class);
	private static AotuTxService aotuTxService = new AotuTxService();
	@Override
	public void execute() 
	{
		//��ѯ��Ҫת�˵Ŀͻ�
		List<DataRow> list = aotuTxService.getAllTxList();
		for (DataRow dataRow : list) 
		{
			try {
				if(dataRow.getString("pay_type").equals("1"))//�ڱ�
				{
					TxUtil.rb_pay(dataRow);
				}
				else
				{
				    TxUtil.pay(dataRow);//����
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e);
			}
		}
	}
    
	public static void main(String[] args) 
	{
		Configure.getInstance();
		List<DataRow> list = aotuTxService.getAllTxList();
		for (DataRow dataRow : list) 
		{
			try {
				if(dataRow.getString("pay_type").equals("1"))//�ڱ�
				{
					TxUtil.rb_pay(dataRow);
				}
				else
				{
				    TxUtil.pay(dataRow);//����
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e);
			}
		}
	}
	
	
}
