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

public class JZDLUsercms implements Task {
	private static Logger logger = Logger.getLogger(JZDLUsercms.class);
	private static AotuZDSHALLService aotuZDSHALLService = new AotuZDSHALLService();
	@Override
	public void execute() 
	{
		Date date = new Date();
	    Calendar calendar = Calendar.getInstance();//日历对象
	    calendar.setTime(date);//设置当前日期			
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");		 
        String today = sdf.format(date);
        calendar.add(Calendar.DATE, -2);
        String nextDay = sdf.format(calendar.getTime());
        
		List<DataRow> list = aotuZDSHALLService.getAllcmsUserList(today,nextDay);
		try{
			for (DataRow dataRow : list) {
				String time = dataRow.getString("last_time");
				if(time.compareTo(nextDay)<0){
					DataRow row =new DataRow();
					row.set("user_id", dataRow.getString("user_id"));
					row.set("state",0);
					aotuZDSHALLService.updateUsercms(row);
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
}

