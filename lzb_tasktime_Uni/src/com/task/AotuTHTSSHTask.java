package com.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

import com.service.AotuTHTSSHService;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.timerengine.Task;
/**
 * �Զ���˽��
 * @author Administrator
 *
 */
public class AotuTHTSSHTask implements Task
{

	private static Logger logger = Logger.getLogger(AotuTHTSSHTask.class);
	private static AotuTHTSSHService aotuTHTSSHService = new AotuTHTSSHService();
	@Override
	public void execute() 
	{
		//��ѯ����֥���С��580�� ���ڳ������״̬�еĽ����Ŀ
		List<DataRow> list = aotuTHTSSHService.getTHTSSHList();
		for (DataRow dataRow : list) 
		{
			try {				
			
				String userid =	dataRow.getString("userid");//����û�id
			    String jkid = dataRow.getString("jkid"); //���id ; 
				//����û�ID �жϸ��û��Ƿ�֥�����÷�С��580		
			    int tonghuajilu  = aotuTHTSSHService.getTonghuats(userid);
			    if(tonghuajilu < 800){
				    //����û�id ��ѯ���û���ͨ����¼����
			    	//���½����Ŀ
			    	Date  date = new Date();
					Calendar calendar =Calendar.getInstance();
					SimpleDateFormat fmtrq  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
			 		DataRow row = new DataRow() ;
					row.set("id", jkid);
					row.set("cl_status","3");
					row.set("cl_yj","ͨ����¼������١�");	
					row.set("cl_ren",161);				
					row.set("cl_time",fmtrq.format(calendar.getTime()));
					aotuTHTSSHService.updateUserJk(row);
					
					//�����û���Ϣ�����û����ڣ�
					DataRow row2 = new DataRow() ;
					row2.set("id", userid );
					row2.set("heihu_zt",1);
					aotuTHTSSHService.updateUserInfo(row2);
			     	//������Ϣ	
					DataRow row3 =  new DataRow();	
				    row3.set("userid", userid);
				    row3.set("title", "���֪ͨ") ;
				    row3.set("neirong" ,"��Ǹ���������δͨ�����������һ������ԭ����ɣ�" +
				    		"1.�����������㣬��ծ�ȹ�ߣ�2.�����������ȶ��Բ��㣻3.���ü�¼������4.δ��Ҫ���ṩ���ϻ��ṩ�����ϲ����꾡��5.ϵͳ�ۺ����ֲ��㡣");
				    row3.set("fb_time", new Date());
				    aotuTHTSSHService.insertUserMsg(row3);
			   }	
			} catch (Exception e) {
				
				logger.error(e);
			}
		}
		
	
	}
    
	public static void main(String[] args) 
	{
		
						
	}
	
	
}
