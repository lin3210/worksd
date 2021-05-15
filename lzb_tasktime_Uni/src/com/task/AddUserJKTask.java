package com.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.service.AotuZDSHALLService;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.timerengine.Task;

public class AddUserJKTask implements Task {
	private static Logger logger = Logger.getLogger(AddUserJKTask.class);
	private static AotuZDSHALLService aotuZDSHALLService = new AotuZDSHALLService();
	@Override
	public void execute() 
	{
		Date date = new Date();
	    Calendar calendar = Calendar.getInstance();//日历对象
	    calendar.setTime(date);//设置当前日期			
	    
	    SimpleDateFormat fmtrq  = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		SimpleDateFormat sdfday  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");		 
        String today = sdf.format(date);
        calendar.add(Calendar.DATE, -1);
        String nextDay = sdf.format(calendar.getTime());
        
        logger.info("添加时间"+nextDay);
        String starttime ="2019-08-01";
		//List<DataRow> list = aotuZDSHALLService.getAllMYJKList(nextDay);
        List<DataRow> list = aotuZDSHALLService.getAllMYJKList2(starttime,nextDay);
		int size = list.size();
		
		//审核人员名单
		//int shenhezu[] = {2001,2002,2003,2004,2005};
		List<DataRow> listsh = aotuZDSHALLService.getAuditors();
		 if(listsh.size() ==0) {
	        	listsh =aotuZDSHALLService.getAuditorsshzz();
	        }
        int shenhezu[] = new int[listsh.size()];
        for (int i = 0, n = listsh.size(); i < n; i++) {
            int cmsUserId = listsh.get(i).getInt("user_id");
            shenhezu[i] = cmsUserId;
        }
       
        
		
		try{
			for(int i=0; i<size; i++){
				 Random random = new Random();
	        	 int xiabiao = random.nextInt(shenhezu.length);
	        	 int shenheren = shenhezu[xiabiao];
	        	 
				 DataRow dataRow = list.get(i);
				 int userid = dataRow.getInt("id");
				 String  mobilephone = dataRow.getString("mobilephone");
				 int jkcount = aotuZDSHALLService.getJKCount(userid);
			     int hhzt = aotuZDSHALLService.getHHZT(userid);
			     int shenfen = aotuZDSHALLService.getShenfen(userid);
			     int yhbd = aotuZDSHALLService.getYHBD(userid);
			     int lianxi = aotuZDSHALLService.getLianxi(userid);
			    
			     String idno = aotuZDSHALLService.getIdno(userid);
			     String realname = aotuZDSHALLService.getRealname(userid);
			     int sfcount = aotuZDSHALLService.getSFCount(idno,realname); 
			    
			    
			     int sfzu [] = new int[sfcount];
			     int jkcountzu [] = new int[sfcount];
			     int code = 0;
			     if(sfcount>1){
			    	List<DataRow> listjk = aotuZDSHALLService.getALLshenfen(idno,realname);
			    	for(int j =0; j<sfcount;j++){
			    		DataRow row11 = listjk.get(j);
			    		if(row11.getInt("userid") != userid){
				    		sfzu [j] = row11.getInt("userid");
				    		jkcountzu [j] =  aotuZDSHALLService.getJKCount(sfzu [j]);
				    		if(jkcountzu [j]>0){
				    			code=1;
				    		}
			    		 }
			    	 }
			      }
			     
			     //a匹配到信息数目
			     int pp_num = aotuZDSHALLService.getphoneidno_pp(idno,mobilephone); 
			     
			    if(jkcount==0 && hhzt ==0 && code==0 && shenfen==1 && yhbd==1 && lianxi==1 && pp_num >0){	
			    	DataRow row = new DataRow();
					row.set("userid",userid);
					row.set("create_date", fmtrq.format(new Date()));
					row.set("daytime",sdfday.format(new Date()));
					row.set("jk_money", "2,000,000");
					row.set("jk_date", 3);
					row.set("annualrate", 30);			
					row.set("shy_money", "2,000,000");
					row.set("onesh", shenheren);
					row.set("twosh", shenheren);
					row.set("threesh", shenheren);
					row.set("myjkcode", 1);
					aotuZDSHALLService.insertUserJK(row);
					logger.info("成功添加userid"+userid);
			    }
						
			}
		}catch (Exception e) {
			// TODO: handle exception
		   e.printStackTrace() ;
		}
	}
	
//	 public static void main(String[] args) {
//		 AddUserJKTask fc = new AddUserJKTask();
//		             fc.execute();
////	              
//	   } 
}

