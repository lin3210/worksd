package com.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

import com.service.AotuZDSHService;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.timerengine.Task;
/**
 * �Զ���˽��
 * @author Administrator
 *
 */
public class AotuZDSHTask implements Task
{

	private static Logger logger = Logger.getLogger(AotuZDSHTask.class);
	private static AotuZDSHService aotuZDSHService = new AotuZDSHService();
	@Override
	public void execute() 
	{
		//��ѯ���кڻ������Ŀ
		List<DataRow> list = aotuZDSHService.getAllHHSHList();
		for (DataRow dataRow : list) 
		{
			try {				
				String userid =	dataRow.getString("userid");//����û�id
			    String jkid = dataRow.getString("jkid"); //���id ; 
				//����û�ID �жϸ��û��Ƿ�Ϊ�����û�
			    String sfwhh = aotuZDSHService.getUserZt(userid) ;                    //�Ƿ�Ϊ�ڻ�
			    if(sfwhh.equals("1")){
			    	//���½����Ŀ
			    	Date  date = new Date();
					Calendar calendar =Calendar.getInstance();
					SimpleDateFormat fmtrq  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
			 		DataRow row = new DataRow() ;
					row.set("id", jkid);
					row.set("cl_status","3");
					row.set("cl_yj","�ۺ����ֲ���");	
					row.set("cl_ren",161);				
					row.set("cl_time",fmtrq.format(calendar.getTime()));
					aotuZDSHService.updateUserJk(row);
			     	//������Ϣ	
					DataRow row3 =  new DataRow();	
				    row3.set("userid", userid);
				    row3.set("title", "���֪ͨ") ;
				    row3.set("neirong" ,"��Ǹ���������δͨ�����������һ������ԭ����ɣ�" +
				    		"1.�����������㣬��ծ�ȹ�ߣ�2.�����������ȶ��Բ��㣻3.���ü�¼������4.δ��Ҫ���ṩ���ϻ��ṩ�����ϲ����꾡��5.ϵͳ�ۺ����ֲ��㡣");
				    row3.set("fb_time", new Date());
				    aotuZDSHService.insertUserMsg(row3);
			    }  
			} catch (Exception e) {
				
				logger.error(e);
			}
		}
		
		//��ѯ����֥���  С��570�ֽ����Ŀ
		//List<DataRow> listZm = aotuZDSHService.getAllZMSHList(); 
	}
    
	public static void main(String[] args) 
	{
		//��ѯ���кڻ������Ŀ
		List<DataRow> list = aotuZDSHService.getAllHHSHList();
		for (DataRow dataRow : list) 
		{
			try {				
				
				String userid =	dataRow.getString("userid");//����û�id
			    String jkid = dataRow.getString("jkid"); //���id ; 
				//����û�ID �жϸ��û��Ƿ�Ϊ�����û�
			    String sfwhh = aotuZDSHService.getUserZt(userid) ;                    //�Ƿ�Ϊ�ڻ�
			    if(sfwhh.equals("1")){
			    	//���½����Ŀ
			    	Date  date = new Date();
					Calendar calendar =Calendar.getInstance();
					SimpleDateFormat fmtrq  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
			 		DataRow row = new DataRow() ;
					row.set("id", jkid);
					row.set("cl_status","3");
					row.set("cl_yj","�ۺ����ֲ���");	
					row.set("cl_ren",161);				
					row.set("cl_time",fmtrq.format(calendar.getTime()));
					aotuZDSHService.updateUserJk(row);
			     	//������Ϣ	
					DataRow row3 =  new DataRow();	
				    row3.set("userid", userid);
				    row3.set("title", "���֪ͨ") ;
				    row3.set("neirong" ,"��Ϣ��˲�ͨ���ۺ����ֲ���");
				    row3.set("fb_time", new Date());
				    aotuZDSHService.insertUserMsg(row3);
					
			    }
			} catch (Exception e) {
				
				logger.error(e);
			}
		}
		
	}
	
	
}
