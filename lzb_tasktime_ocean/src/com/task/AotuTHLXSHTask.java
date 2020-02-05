package com.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

import com.service.AotuTHLXSHService;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.timerengine.Task;
/**
 * �Զ���˽��
 * @author Administrator
 *
 */
public class AotuTHLXSHTask implements Task
{

	private static Logger logger = Logger.getLogger(AotuTHLXSHTask.class);
	private static AotuTHLXSHService aotuTHLXSHService = new AotuTHLXSHService();
	@Override
	public void execute() 
	{
		//����������˵Ľ���¼
		List<DataRow> list = aotuTHLXSHService.getAllZMSHList();
		for (DataRow dataRow : list) 
		{
			try {				
			
				String userid =	dataRow.getString("userid");//����û�id
			    String jkid = dataRow.getString("jkid"); //���id ; 
				String phone1 = dataRow.getString("contactphone").replaceAll(" ", "").replaceAll("-", ""); //��ϵ��01�ֻ��
				String phone2 = dataRow.getString("contactphone02").replaceAll(" ", "").replaceAll("-", ""); //��ϵ��02�ֻ��
			    //����û�id �õ���ϵ�˵�ͨ������
				int thts01 = aotuTHLXSHService.getLxThts(userid ,phone1);
				int thts02 = aotuTHLXSHService.getLxThts(userid ,phone2);
				if(thts01 <25 || thts02< 25){
			      
					//���½����Ŀ
			    	Date  date = new Date();
					Calendar calendar =Calendar.getInstance();
					SimpleDateFormat fmtrq  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
			 		DataRow row = new DataRow() ;
					row.set("id", jkid);
					row.set("cl_status","3");
					row.set("cl_yj","�ṩ������������ϵ��ͨ����¼������٣��������ϵ�ˣ���");	
					row.set("cl_ren",161);				
					row.set("cl_time",fmtrq.format(calendar.getTime()));
					aotuTHLXSHService.updateUserJk(row);
					
					//�����û���Ϣ�����û���ϵ��״̬��أ�
					DataRow row2 = new DataRow() ;
					row2.set("id", userid );
					row2.set("isLianxi",0);
					aotuTHLXSHService.updateUserInfo(row2);
			     	//������Ϣ	
					DataRow row3 =  new DataRow();	
				    row3.set("userid", userid);
				    row3.set("title", "���֪ͨ") ;
				    row3.set("neirong" ,"��Ϣ��˲�ͨ�� �ṩ������������ϵ��ͨ����¼������٣��������ϵ�ˣ���");
				    row3.set("fb_time", new Date());
				    aotuTHLXSHService.insertUserMsg(row3);
				}
				
			} catch (Exception e) {
				
				logger.error(e);
			}
		}
		
	
	}
    
	public static void main(String[] args) 
	{
		String str = " 13543281052"; 
		String str2 = str.replaceAll(" ", "").replaceAll("-", ""); 
		System.out.println(str2); 

						
	}
	
	
}
