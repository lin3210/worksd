package com.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.task.HttpSender;

import com.service.AccountUserService;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.timerengine.Task;

public class DistributionTask implements Task {
	private static Logger logger = Logger.getLogger(DistributionTask.class);
	private static AccountUserService accountUserService = new AccountUserService();
	@Override
	public void execute() {
		int nums[] = {163,193,194};
		String UserID="";
		List<DataRow> list = accountUserService.getYuqList(UserID);
		for (DataRow dataRow : list) {
			for(int i=0;i<list.size();i++){
			
			}
		}
	}
}             
