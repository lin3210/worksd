package com.task;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.task.HttpSender;

import com.service.AccountUserService;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.timerengine.Task;

public class ServiceFalseTask implements Task {
	private static Logger logger = Logger.getLogger(ServiceFalseTask.class);
	private static AccountUserService accountUserService = new AccountUserService();
	String url253 = "https://sms.253.com/msg/send";// 应用地址
	String un253 = "N5186211";// 账号
	String pw253 = "fA5nv7cI9";// 密码	
	String rd253 = "0";// 是否需要状态报告，需要1，不需要0
	String ex253 = null;// 扩展码	
	String mobilePhone1 = "01638154034";
	String mobilePhone2 = "01682942520";
	@Override
	public void execute() {
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		Calendar cd = Calendar.getInstance();
		try {
			cd.setTime(sdf.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow); 
		cd.add(Calendar.MINUTE, -30);//
		Date date = cd.getTime();
		String dateno = sdf.format(date); 
		int yizhuce = accountUserService.getCreateTimeByDate(dateno,nowdate);
		int smscode = accountUserService.getSmsCode();
		
		if ( yizhuce == 0) {
		  	try {
		  		int jishu = smscode+1;
		  		if(jishu == 2){
		  			jishu =0;
		  		}
		  		DataRow row = new DataRow();
		  		row.set("id", 1);
		  		row.set("smscode", jishu);
		  		accountUserService.updateJishu(row);
		  		if(jishu==1){
			  		/*String content = "[{\"PhoneNumber\":\""+mobilePhone1+"\",\"Message\":\"Olava thong bao: OLAVA error.\",\"SmsGuid\":\""+mobilePhone1+"\",\"ContentType\":1}]";
					String con = URLEncoder.encode(content, "utf-8");
					SendMsg sendMsg = new SendMsg();
					String returnString = SendMsg.sendMessageByGet(con,mobilePhone1);
					String content1 = "[{\"PhoneNumber\":\""+mobilePhone2+"\",\"Message\":\"Olava thong bao: OLAVA error.\",\"SmsGuid\":\""+mobilePhone2+"\",\"ContentType\":1}]";
					String con1 = URLEncoder.encode(content1, "utf-8");
					SendMsg sendMsg1 = new SendMsg();
					String returnString1 = SendMsg.sendMessageByGet(con1,mobilePhone2);*/
		  		}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		} 
	}
}
