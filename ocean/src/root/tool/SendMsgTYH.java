package root.tool;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import com.project.service.account.JBDUserService;
import com.shove.security.Encrypt;
import com.thinkive.base.jdbc.DataRow;

import root.SendMsg;


/**
 * date:2020年2月17日
 * 天一泓
 * @author Administrator
 *
 */


public class SendMsgTYH {
	
	 //PLUTUS
	 private static final String loginNamePLUTUS = "cs_rqv8oq";
	 private static final String brandsenderid = "";// n/a
	 private static final String loginPasswoPLUTUS = "WuLNPPix"; 

	 //OTP
	 private static final String loginNameOTP = "cs_hgm5wf";
	 private static final String loginPasswoOTP = "MduxqFGk"; 
	 private static final String senderid = "lalatet.vn";
	 
	 private static final String charset = "utf-8";
	 private static final String url = "http://sms.skylinelabs.cc:20003/";
	 
	 private static Logger logger = Logger.getLogger(SendMsgTYH.class);
	 private static JBDUserService jdbUserService = new JBDUserService();
	 
	 
	 /**a  账号余额查询
	  * http://sms.skylinelabs.cc:20003/getbalanceV2?account=***&sign=***&datetime=***
	  * @param content
	  * @param phone
	  * @return
	  * @throws UnsupportedEncodingException
	  */
	public  static String[] SendMsgBalance() throws UnsupportedEncodingException {
		String []return_str=new String[] {"",""};
		 HttpClient httpClient = new DefaultHttpClient();
		 
		// a加密sign
		 Date date = new Date();
		 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		 Calendar rightNow = Calendar.getInstance();  
	     rightNow.setTime(date);  
	     rightNow.add(Calendar.HOUR, 1);  
	     Date dt1 = rightNow.getTime();  
		 String curTime =dateFormat.format(dt1);
		 System.out.println(dateFormat.format(dt1)); 
		 String sign = Encrypt.MD5(loginNameOTP + loginPasswoOTP + curTime);
		 
    	String fullUrl = url + "getbalanceV2?account="+loginNameOTP+"&sign="+sign+"&datetime="+curTime;
     	logger.info(fullUrl);
	     HttpGet httpGet = new HttpGet(fullUrl);
	     
	     try {
			HttpResponse response = httpClient.execute(httpGet);
			
			 HttpEntity entity = response.getEntity();
	           if (null != entity) {
	        	   String str = EntityUtils.toString(entity, "UTF-8") ;    	   
                   System.out.println("--------------------------------------");  
                   System.out.println("Response content: " + str);  
   
	        	   com.alibaba.fastjson.JSONObject json1 = com.alibaba.fastjson.JSONObject.parseObject(str);
	   			   int status = json1.getInteger("status");
	   			   System.out.println("--------------------------------------："+status);  
	   			   if(status==0) {
	   				String return_str0 = json1.getString("balance");  //a实际账户的余额
	   				String return_str1 = json1.getString("gift");   //a赠送账户余额
	   				return_str[0]=return_str0;
	   				return_str[1]=return_str1;
	   			   }
	           }
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 
		 return return_str;
		 
	}
	 
	 /**a  2.群发短信 sendsms --- 电话用分号隔开
	  * http://sms.skylinelabs.cc:20003/sendsmsV2?account=***&sign=***&datetime=***&senderid=***&numbers=10010,1008611&content=***
	  * @param content
	  * @param phone
	  * @return
	  * @throws UnsupportedEncodingException
	 * @throws DocumentException 
	  */
	public  static String SendMsgSMS(String content,String phone) throws UnsupportedEncodingException, DocumentException { 
		String return_str="";
		String sqlPhone =phone;
		phone= "84"+phone.substring(1);
		 content = URLEncoder.encode(content, "utf-8");
		 HttpClient httpClient = new DefaultHttpClient();
		 
		 // a加密sign
		 Date date = new Date();
		 SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		 Calendar rightNow = Calendar.getInstance();  
	     rightNow.setTime(date);  
	     rightNow.add(Calendar.HOUR, 1);  
	     Date dt1 = rightNow.getTime();  
		 String curTime =dateFormat.format(dt1);
		 System.out.println(dateFormat.format(dt1)); 
		 String sign = Encrypt.MD5(loginNameOTP + loginPasswoOTP + curTime);	 
     	String fullUrl = url + "sendsmsV2?account="+loginNameOTP+"&sign="+sign+"&datetime="+curTime+"&senderid="+senderid+"&numbers="+phone+"&content="+content;
      	logger.info(fullUrl);
	     HttpGet httpGet = new HttpGet(fullUrl);
	     
	     try {
			HttpResponse response = httpClient.execute(httpGet);
			
			 HttpEntity entity = response.getEntity();
	           if (null != entity) {
	        	   String str = EntityUtils.toString(entity, "UTF-8") ;    	   
                   System.out.println("--------------------------------------");  
                   System.out.println("Response content: " + str);  
   
	        	   com.alibaba.fastjson.JSONObject json1 = com.alibaba.fastjson.JSONObject.parseObject(str);
	   			   int status = json1.getInteger("status");
	   			    System.out.println("--------------------------------------："+status);
	   			    if(0==status) {
	   			    	int success = json1.getInteger("success");
	   			    	int fail = json1.getInteger("fail");
	   			    	
	   			    	return_str= success+"";
	   			    	DataRow row = new DataRow();
	   			    	row.set("phone", sqlPhone);
	   			    	row.set("sendtime", curTime);
	   			    	row.set("contact", "SendMsgSMS");
	   			    	row.set("create_time", fmtrq.format(date));
	   			    	row.set("success", success);
	   			    	row.set("fail", fail);
	   			    	jdbUserService.addSendSMSTYH(row);
	   			    	
	   			    }
	   			   
                  
	           }
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 
		 return return_str;
		 
	}
	
	
	
	/**a 3.查询短信发送结果 getreport
	  * http://sms.skylinelabs.cc:20003/getreportV2?account=***&sign=***&datetime=***&ids=1,2
	  * @param content
	  * @param phone
	  * @return
	  * @throws UnsupportedEncodingException
	  */
	public  static String SendMsgreport(String sendTime,String phone) throws UnsupportedEncodingException {
		logger.info("SendMsgreport");
		
		 String return_str="";
		 HttpClient httpClient = new DefaultHttpClient();
		 
		 // a加密sign
		 String sign = Encrypt.MD5(loginNameOTP + loginPasswoOTP + sendTime);
		 //ol 指定查询发送结果的短信id（该id在提交时由系统返回），多个号码之间以英文逗号分隔(最多200个）
		 String ids=phone;
		 
   	    String fullUrl = url + "getreportV2?account="+loginNameOTP+"&sign="+sign+"&datetime="+sendTime+"&ids="+ids;
    	logger.info(fullUrl);
	     HttpGet httpGet = new HttpGet(fullUrl);
	     
	     try {
			HttpResponse response = httpClient.execute(httpGet);
			
			 HttpEntity entity = response.getEntity();
	           if (null != entity) {
	        	   String str = EntityUtils.toString(entity, "UTF-8") ;    	   
                  System.out.println("--------------------------------------");  
                  System.out.println("Response content: " + str);  
                  System.out.println("--------------------------------------");  
  
	        	   com.alibaba.fastjson.JSONObject json1 = com.alibaba.fastjson.JSONObject.parseObject(str);
	   			   int status = json1.getInteger("status");
	   			   if(0==status) {
	   				int success = json1.getInteger("success");
   			    	int fail = json1.getInteger("fail");
	   			   }
	   			   
	           }
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 
		 return return_str;
		 
	}
	
	/**a 4.批量提交群发短信 batchsendsms
	  *  POST /batchsendsmsV2?account=***&sign=***&datetime=**** HTTP/1.1
	  * @param content
	  * @param phone
	  * @return
	  * @throws UnsupportedEncodingException
	  */
	public  static String SendMsgbatchsendsms(String sendTime,String phone) throws UnsupportedEncodingException {
		logger.info("SendMsgreport");
		
		 String return_str="";
		 HttpClient httpClient = new DefaultHttpClient();
		 
		 // a加密sign
		 String sign = Encrypt.MD5(loginNameOTP + loginPasswoOTP + sendTime);
		 //ol 指定查询发送结果的短信id（该id在提交时由系统返回），多个号码之间以英文逗号分隔(最多200个）
		 String ids=phone;
		 
  	    String fullUrl = url + "getreportV2?account="+loginNameOTP+"&sign="+sign+"&datetime="+sendTime+"&ids="+ids;
     	logger.info(fullUrl);
	     HttpGet httpGet = new HttpGet(fullUrl);
	     
	     try {
			HttpResponse response = httpClient.execute(httpGet);
			
			 HttpEntity entity = response.getEntity();
	           if (null != entity) {
	        	   String str = EntityUtils.toString(entity, "UTF-8") ;    	   
                 System.out.println("--------------------------------------");  
                 System.out.println("Response content: " + str);  
                 System.out.println("--------------------------------------");  
 
	        	   com.alibaba.fastjson.JSONObject json1 = com.alibaba.fastjson.JSONObject.parseObject(str);
	   			   int status = json1.getInteger("status");
	   			   if(0==status) {
	   				int success = json1.getInteger("success");
  			    	int fail = json1.getInteger("fail");
	   			   }
	   			   
	           }
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 
		 return return_str;
		 
	}
	
	/**a 5.查询已发送短信结果 getsentrcd
	  * http://sms.skylinelabs.cc:20003/getsentrcdV2?account=***&sign=***&datetime=***&date=***&begintime=***&endtime=***&startindex=***
	  * @param content
	  * @param phone
	  * @return
	  * @throws UnsupportedEncodingException
	  */
	public  static String SendMsgSentrcd(String sendTime,String phone) throws UnsupportedEncodingException {
		logger.info("SendMsgreport");
		
		 String return_str="";
		 HttpClient httpClient = new DefaultHttpClient();
		 
		 // a加密sign
		 String sign = Encrypt.MD5(loginNameOTP + loginPasswoOTP + sendTime);
		 //ol 指定查询发送结果的短信id（该id在提交时由系统返回），多个号码之间以英文逗号分隔(最多200个）
		 String ids=phone;
		 
  	    String fullUrl = url + "getreportV2?account="+loginNameOTP+"&sign="+sign+"&datetime="+sendTime+"&ids="+ids;
   	logger.info(fullUrl);
	     HttpGet httpGet = new HttpGet(fullUrl);
	     
	     try {
			HttpResponse response = httpClient.execute(httpGet);
			
			 HttpEntity entity = response.getEntity();
	           if (null != entity) {
	        	   String str = EntityUtils.toString(entity, "UTF-8") ;    	   
                 System.out.println("--------------------------------------");  
                 System.out.println("Response content: " + str);  
                 System.out.println("--------------------------------------");  
 
	        	   com.alibaba.fastjson.JSONObject json1 = com.alibaba.fastjson.JSONObject.parseObject(str);
	   			   int status = json1.getInteger("status");
	   			   if(0==status) {
	   				int success = json1.getInteger("success");
  			    	int fail = json1.getInteger("fail");
	   			   }
	   			   
	           }
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 
		 return return_str;
		 
	}
	
	
	/**
	 * String Exchange Map Type
	 * @param str
	 * @return
	 */
	public static Map<String, String> strExchangeMap(String str) {

		String[] strs = str.split(",");
		Map<String, String> m = new HashMap<String, String>();
		for(String s:strs){
		String[] ms = s.split(":");
		m.put(ms[0], ms[1]);
		}
		
		return m;

	}
	
	 /**a  2.群发短信 sendsms --- 电话用分号隔开  营销账号
	  * http://sms.skylinelabs.cc:20003/sendsmsV2?account=***&sign=***&datetime=***&senderid=***&numbers=10010,1008611&content=***
	  * @param content
	  * @param phone
	  * @return
	  * @throws UnsupportedEncodingException
	 * @throws DocumentException 
	  */
	public  static String SendMsgSMSYX(String content,String phone) throws UnsupportedEncodingException, DocumentException {
		String return_str="";
		 String sqlPhone =phone;
		 phone= "84"+phone.substring(1);
		 content = URLEncoder.encode(content, "utf-8");
		 HttpClient httpClient = new DefaultHttpClient();
		 
		 // a加密sign
		 Date date = new Date();
		 SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		 Calendar rightNow = Calendar.getInstance();  
	     rightNow.setTime(date);  
	     rightNow.add(Calendar.HOUR, 1);  
	     Date dt1 = rightNow.getTime();  
		 String curTime =dateFormat.format(dt1);
		 System.out.println(dateFormat.format(dt1)); 
		 String sign = Encrypt.MD5(loginNamePLUTUS + loginPasswoPLUTUS + curTime);	 
    	 String fullUrl = url + "sendsmsV2?account="+loginNamePLUTUS+"&sign="+sign+"&datetime="+curTime+"&senderid="+brandsenderid+"&numbers="+phone+"&content="+content;
     	 logger.info(fullUrl);
     	 
	     HttpGet httpGet = new HttpGet(fullUrl);
	     
	     try {
			HttpResponse response = httpClient.execute(httpGet);
			
			 HttpEntity entity = response.getEntity();
	           if (null != entity) {
	        	   String str = EntityUtils.toString(entity, "UTF-8") ;    	   
                  System.out.println("--------------------------------------");  
                  System.out.println("Response content: " + str);  
  
	        	   com.alibaba.fastjson.JSONObject json1 = com.alibaba.fastjson.JSONObject.parseObject(str);
	   			   int status = json1.getInteger("status");
	   			    System.out.println("--------------------------------------："+status);
	   			    if(0==status) {
	   			    	String success = json1.getString("success");
	   			    	String fail = json1.getString("fail");
	   			    	return_str= success+"";
	   			    	
	   			    	DataRow row = new DataRow();
	   			    	row.set("phone", sqlPhone);
	   			    	row.set("sendtime", curTime);
	   			    	row.set("contact", "SendMsgSMSYX");
	   			    	row.set("create_time", fmtrq.format(date));
//	   			    	row.set("success", success);
//	   			    	row.set("fail", fail);
	   			    	jdbUserService.addSendSMSTYH(row);
	   			    	
	   			    }
	   			   
                 
	           }
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 
		 return return_str;
		 
	}
	
	public static void main(String []arg) throws UnsupportedEncodingException, DocumentException  {
		Date date = new Date();
		 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		 System.out.println(dateFormat.format(date));
		 
//		SendMsgSMSYX("Ung dung xin thong bao ma OTP cua ban la 715302 adavigo","0587321816"); 
		SendMsgSMS("Ung dung xin thong bao ma OTP cua ban la 715302 adavigo","0587321816"); 
//		 try {
//			 String str ="Mã đơn hàng của bạn là 3568";
//			//SendMsgSMS(str,"0587321816");  
//			//SendMsgSMSYX("ban la gi ","0587321816");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}

}
