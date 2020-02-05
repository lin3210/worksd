package com.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.lianpay.util.HttpUtil;
import com.service.AotuZDSHALLService;
import com.shove.security.Encrypt;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.timerengine.Task;

public class CSWBFenpeicuishou implements Task {
	private static Logger logger = Logger.getLogger(CSWBFenpeicuishou.class);
	private static AotuZDSHALLService aotuZDSHALLService = new AotuZDSHALLService();
	String jiami = "G0eHIW3op8dYIWvdXDcePAn8QvfqHVSh";
	
	@Override
	public void execute() 
	{
		Date date = new Date();
	    Calendar calendar = Calendar.getInstance();//日历对象
	    calendar.setTime(date);//设置当前日期			
	    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");		 
        String today = sdf.format(date);
        calendar.add(Calendar.DATE, 2);
        String nextDay = sdf.format(calendar.getTime());
        String ri="";
        String yue="";
        if(today.substring(3, 5).equals(nextDay.substring(3, 5))){
        	ri= nextDay.substring(0, 2);
        	yue= nextDay.substring(3, 5);
        }else{
        	ri= nextDay.substring(0, 2);
        	yue= nextDay.substring(3, 5);
        }
		List<DataRow> list = aotuZDSHALLService.getAllYQ30();
		for(int i=0;i<list.size();i++){
			DataRow datarow = list.get(i);
			String userid = datarow.getString("userid");
			String jkid = datarow.getString("id");
			DataRow rowsf = aotuZDSHALLService.getShenfen(userid);
			DataRow rowbk = aotuZDSHALLService.getBankcard(userid);
			DataRow rowuser = aotuZDSHALLService.getUserUser(userid);
			DataRow rowjk = aotuZDSHALLService.getUserJK(jkid);
			DataRow rowzp = aotuZDSHALLService.getZhaopian(userid);
			DataRow rowlx = aotuZDSHALLService.getLianxi(userid);
			DataRow rowdw = aotuZDSHALLService.getDingwei(userid);
		   List<DataRow> listthjl = aotuZDSHALLService.getAllTHJL(userid);
		   List<DataRow> listtxl = aotuZDSHALLService.getAllTXL(userid);
		   com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
		   com.alibaba.fastjson.JSONArray arraythjl=new com.alibaba.fastjson.JSONArray();
		   com.alibaba.fastjson.JSONArray arraytxl=new com.alibaba.fastjson.JSONArray();
		   for(int j=0;j<listthjl.size();j++){
			   com.alibaba.fastjson.JSONObject lan1=new com.alibaba.fastjson.JSONObject();
			   DataRow datarowthjl = listthjl.get(j);
			   lan1.put("name", datarowthjl.getString("name"));
			   lan1.put("phone", datarowthjl.getString("number"));
			   arraythjl.add(lan1);
		   }
		   for(int k=0;k<listtxl.size();k++){
			   com.alibaba.fastjson.JSONObject lan2=new com.alibaba.fastjson.JSONObject();
			   DataRow datarowtxl = listtxl.get(k);
			   lan2.put("name", datarowtxl.getString("name"));
			   lan2.put("phone", datarowtxl.getString("number"));
			   arraytxl.add(lan2);
		   }
		   json.put("rowthjl", arraythjl);
		   json.put("rowtxl", arraytxl);
		   json.put("rowsf", rowsf);
		   json.put("rowbk", rowbk);
		   json.put("rowuser", rowuser);
		   json.put("rowjk", rowjk);
		   json.put("rowzp", rowzp);
		   json.put("rowlx", rowlx);
		   json.put("rowdw", rowdw);
		   
		   
		   
		   
		   
		   String sfmiwen = Encrypt.MD5(userid+jiami);
	 		
		    json.put("secret", sfmiwen);
			json.put("userid",userid);
			String url = "http://www.movabank.com/servlet/current/JBDUserAction?function=GetJKcuishouNewOlv";
			//String response = PostHttp.getJsonData(json,url);	
			String response = HttpUtil.doPost(url, json, "UTF-8");
			
			com.alibaba.fastjson.JSONObject json1 = com.alibaba.fastjson.JSONObject.parseObject(response);
			String code = json1.getString("status");
		}
		
	}
	  public static void main(String[] args){
		  Date date = new Date();
		    Calendar calendar = Calendar.getInstance();//日历对象
		    calendar.setTime(date);//设置当前日期			
		    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");		 
	        String today = sdf.format(date);
	        calendar.add(Calendar.DATE, 2);
	        String nextDay = sdf.format(calendar.getTime());
	        String ri="";
	        String yue="";
	        if(today.substring(3, 5).equals(nextDay.substring(3, 5))){
	        	ri= nextDay.substring(0, 2);
	        	yue= nextDay.substring(3, 5);
	        }else{
	        	ri= nextDay.substring(0, 2);
	        	yue= nextDay.substring(3, 5);
	        }
			List<DataRow> list = aotuZDSHALLService.getAllYQ30();
			for(int i=0;i<list.size();i++){
				DataRow datarow = list.get(i);
				String userid = datarow.getString("userid");
				String jkid = datarow.getString("id");
				DataRow rowsf = aotuZDSHALLService.getShenfen(userid);
				DataRow rowbk = aotuZDSHALLService.getBankcard(userid);
				DataRow rowuser = aotuZDSHALLService.getUserUser(userid);
				DataRow rowjk = aotuZDSHALLService.getUserJK(jkid);
				DataRow rowzp = aotuZDSHALLService.getZhaopian(userid);
				DataRow rowlx = aotuZDSHALLService.getLianxi(userid);
				DataRow rowdw = aotuZDSHALLService.getDingwei(userid);
			   List<DataRow> listthjl = aotuZDSHALLService.getAllTHJL(userid);
			   List<DataRow> listtxl = aotuZDSHALLService.getAllTXL(userid);
			   com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
			   com.alibaba.fastjson.JSONArray arraythjl=new com.alibaba.fastjson.JSONArray();
			   com.alibaba.fastjson.JSONArray arraytxl=new com.alibaba.fastjson.JSONArray();
			   for(int j=0;j<listthjl.size();j++){
				   com.alibaba.fastjson.JSONObject lan1=new com.alibaba.fastjson.JSONObject();
				   DataRow datarowthjl = listthjl.get(j);
				   lan1.put("name", datarowthjl.getString("name"));
				   lan1.put("phone", datarowthjl.getString("number"));
				   arraythjl.add(lan1);
			   }
			   for(int k=0;k<listtxl.size();k++){
				   com.alibaba.fastjson.JSONObject lan2=new com.alibaba.fastjson.JSONObject();
				   DataRow datarowtxl = listtxl.get(k);
				   lan2.put("name", datarowtxl.getString("name"));
				   lan2.put("phone", datarowtxl.getString("number"));
				   arraytxl.add(lan2);
			   }
			   json.put("rowthjl", arraythjl);
			   json.put("rowtxl", arraytxl);
			   json.put("rowsf", rowsf);
			   json.put("rowbk", rowbk);
			   json.put("rowuser", rowuser);
			   json.put("rowjk", rowjk);
			   json.put("rowzp", rowzp);
			   json.put("rowlx", rowlx);
			   json.put("rowdw", rowdw);
			   
			   
			   
			   String jiami = "G0eHIW3op8dYIWvdXDcePAn8QvfqHVSh";
			   
			   String sfmiwen = Encrypt.MD5(userid+jiami);
		 		
			    json.put("secret", sfmiwen);
				json.put("userid",userid);
				String url = "http://192.168.0.113/servlet/current/JBDUserAction?function=GetJKcuishouNewOlv";
				//String response = PostHttp.getJsonData(json,url);	
				String response = HttpUtil.doPost(url, json, "UTF-8");
				
				com.alibaba.fastjson.JSONObject json1 = com.alibaba.fastjson.JSONObject.parseObject(response);
				String code = json1.getString("status");
			}
	  }
}

