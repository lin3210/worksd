package com.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.service.AccountTuiGuangService;
import com.service.AccountUserService;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.timerengine.Task;

public class AccountTuiGuangTask implements Task{

	private static Logger logger = Logger.getLogger(AccountTuiGuangTask.class);
	private static AccountTuiGuangService accounttuiguangService=new AccountTuiGuangService();
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		SimpleDateFormat sdfday =new SimpleDateFormat("yyyy-MM-dd");
		
		String daytime = sdfday.format(new Date());
		Calendar cd = Calendar.getInstance();
		try {
			try {
				cd.setTime(sdfday.parse(daytime));
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date datenow = cd.getTime();
		String nowdate = sdfday.format(datenow); // 前面日期 
		cd.add(Calendar.DATE, 1);// // 减一天
		Date date = cd.getTime();
		String dateno = sdfday.format(date); // 后面日期
		try {
			List<DataRow> list = accounttuiguangService.getTGList();
			for (DataRow dataRow : list) {
				int tgid = dataRow.getInt("id");
				int jrcishu = dataRow.getInt("tgjrcishu");
				DataRow row = new DataRow();
				row.set("tgid", tgid);
				row.set("tgcishu", jrcishu);
				row.set("createtime", daytime);
				accounttuiguangService.addTGList("sd_tuiguanguser", row);
				
				
				DataRow row1 = new DataRow();
				row1.set("id", tgid);
				row1.set("tgjrcishu", 0);
				accounttuiguangService.updateTGID(row1);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
