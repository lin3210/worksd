package com.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.service.AccountUserService;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.timerengine.Task;

public class DeleteCopyTask implements Task{

	private static Logger logger = Logger.getLogger(DeleteCopyTask.class);
	private static AccountUserService accountUserService=new AccountUserService();
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf =new SimpleDateFormat("dd-MM-yyyy");
		
		
	}
 
}
