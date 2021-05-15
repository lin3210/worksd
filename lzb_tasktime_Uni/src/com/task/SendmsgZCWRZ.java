package com.task;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;


import com.service.AotuZDSHALLService;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.timerengine.Task;

public class SendmsgZCWRZ implements Task {
	private static Logger logger = Logger.getLogger(SendmsgZCWRZ.class);
	private static AotuZDSHALLService aotuZDSHALLService = new AotuZDSHALLService();
	@Override
	public void execute() 
	{
		Date date = new Date();
	    Calendar calendar = Calendar.getInstance();//日历对象
	    calendar.setTime(date);//设置当前日期			
	    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		 
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");		 
        String today = sdf.format(date);
        calendar.add(Calendar.DATE, -1);
        String nextDay = sdf.format(calendar.getTime());
        
		List<DataRow> list = aotuZDSHALLService.getAllZCWRZList(nextDay,today);
		int n=0;
		for (int i=0 ;i<list.size();i++) {
			   DataRow dataRow = list.get(i);
			 String smscontent1 = "Chuc mung! "+dataRow.getString("mobilephone")+" Ban da thoa dieu kien vay 3,000,000d tai Olava. Hay hoan tat ho so tai http://bit.ly/2M5wh5W va de xuat vay ngay!";
			   //String smscontent1 = "Olava xin chao "+dataRow.getString("cardusername")+". Ho so cua ban da duoc duyet buoc dau, vui long nghe dien thoai tu Olava de hoan tat ho so va nhan tien ngay. Moi thac mac vui long inbox  http://bit.ly/2zPb4v1  hoac goi den hotline: 028-7300-8816.";
			 //借款成功 //String smscontent1 = "Olava chao "+dataRow.getString("realname")+"! Xin mo app xem thong tin chuyen khoan (Tai khoan NH chi do Cty Xuong Thinh dung ten). Vi hien co nguoi gia danh Olava de truc loi nen mong ban het suc luu y. Thac mac xin inbox ox http://bit.ly/2zPb4v1, hot hotline 02873008816.";
			  // String smscontent1 ="Olava chao "+dataRow.getString("realname")+"! Do he thong ngan hang dang gap su co nen khoan vay cua ban se duoc giai ngan sang thu 2 tuan sau. Mong ban thong cam nhe. Thac mac xin inbox ox http://bit.ly/2zPb4v1, hot hotline 02873008816.";
			   String smscontent="";
			try {
				smscontent = URLEncoder.encode(smscontent1, "utf-8");
				System.out.println(smscontent);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			   String content = "[{\"PhoneNumber\":\""+dataRow.getString("mobilephone")+"\",\"Message\":\""+smscontent+"\",\"SmsGuid\":\""+dataRow.getString("mobilephone")+"\",\"ContentType\":1}]";
			   String con ="";
			   //"+dataRow.getString("mobilephone")+"
			try {
				con = URLEncoder.encode(content, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			   SendMsg sendMsg = new SendMsg();
			   String returnString = SendMsg.sendMessageByGet(con);
			   System.out.println(returnString);
			   n++;
		   }
		   
		   System.out.println(n);
	}
}

