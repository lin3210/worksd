package com.task;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.service.AotuZDSHALLService;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.timerengine.Task;

public class FenpeiLeaverCuishou  implements Task{
	
	private static Logger logger = Logger.getLogger(FenpeiLeaverCuishou.class);
	private static AotuZDSHALLService aotuZDSHALLService = new AotuZDSHALLService();
	
	
	
	@Override
	public void execute() 
	{
	
		
       SimpleDateFormat fmtrq  = new SimpleDateFormat("yyyy-MM-dd");
       String time111 = fmtrq.format(new Date());
		
		   		
		//请假的催收人员
		List<DataRow> cuishoumleaver = aotuZDSHALLService.selectUserLeavetList();
		
	
		//催收人员的的id数组
				for (int m = 0; m < cuishoumleaver.size(); m++) {
					DataRow row = cuishoumleaver.get(m);
					int userid = row.getInt("user_id");
					logger.info("mofa请假的催收人员: ---"+userid);
				}
				
								
			for (int i = 0; i < cuishoumleaver.size(); i++) {
				
				DataRow dataRow = cuishoumleaver.get(i);
				int userid = dataRow.getInt("user_id");
				
				int leaver =dataRow.getInt("leaver")-1;	
				aotuZDSHALLService.updateUserLeavetList(userid,leaver);
			}
			
			Calendar rightNow = Calendar.getInstance();
			rightNow.add(rightNow.DATE,-3);
			Date dt=rightNow.getTime();
			//禁用最近三天没有登陆的是账号
		    String lasttime = fmtrq.format(dt);
		    
		    aotuZDSHALLService.updateUserStopList(lasttime);
			
	}
		
	
//	public static void main(String[] args) {
//
//		FenpeiLeaverCuishou fc = new FenpeiLeaverCuishou();
//		fc.execute();
//	}	
}
