package com.task;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.service.CopyuserService;
import com.service.SDYQService;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.timerengine.Task;
public class CopyuserTask implements Task {
	 //字符编码及其他参数
	 private static Logger logger = Logger.getLogger(CopyuserTask.class);
	 private static CopyuserService service = new CopyuserService();
	 private static final String charset = "utf-8";
	 private static final String url = "http://sdcms.lvzbao.com/servlet/current/JBDcmsAction?function=GetWinBankCardList&curPage=";
	 private static String result="";
	 /*
	 * 因请求返回的数据中只需要code和result两个字段的信息，因此方法只返回一个存有这两个值的map
	 */
	 //get方式请求
	 public void execute() 
		{
		 for(int jj = 1;jj<=700000;jj++){
	        String map = "";
	        int page = service.getPage();
	        logger.info(page);
	        HttpClient httpClient = new DefaultHttpClient();
	        String fullUrl = url + page;
	        String strResult="";
	        HttpGet httpRequest = new HttpGet(fullUrl);  
			try  
			{  
			    //取得HttpClient对象  
			    HttpClient httpclient = new DefaultHttpClient();  
			    //请求HttpClient，取得HttpResponse  
			    HttpResponse httpResponse = httpclient.execute(httpRequest);
			        //取得返回的字符串  
			     strResult = EntityUtils.toString(httpResponse.getEntity());  
			    
			}  
			catch (ClientProtocolException e1)  
			{  
			    e1.getMessage().toString();  
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 		String json = strResult;
	         com.alibaba.fastjson.JSONObject jsonObject1 = new com.alibaba.fastjson.JSONObject();
			try {
				com.alibaba.fastjson.JSONObject jsonObject =  (com.alibaba.fastjson.JSONObject) JSON.parse(json);
				 jsonObject1 = (com.alibaba.fastjson.JSONObject) JSON.parse(jsonObject.getString("list"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	         if(jsonObject1.containsKey("data")){
		         com.alibaba.fastjson.JSONArray jsonArray = jsonObject1.getJSONArray("data");
		         if (jsonArray.size()>0) {
		        	 for (int i = 0; i < jsonArray.size(); i++) {
		        		 com.alibaba.fastjson.JSONObject object = jsonArray.getJSONObject(i);
		        		 String nameString = object.getString("cardusername");
		        		 String phone = object.getString("mobilephone");
		        		 String remark = object.getString("remark");
		        		 String cardno = object.getString("cardno");
		        		 DataRow row = new DataRow();
		        		 row.set("name", nameString);
		        		 row.set("phone", phone);
		        		 row.set("remark", remark);
		        		 row.set("cardno", cardno);
		        		 service.addPhone(row);
		        	 }
		         }
	         }
	         DataRow row2 = new DataRow();
	         
	         row2.set("id", 1);
	         row2.set("copyid", page+1);
	         service.updatePage(row2);
	      }
		}
	 public static String doGet(final String urlStr) {
		 	String retrunStr ="";
			URL url = null;
			HttpURLConnection conn = null;
			InputStream is = null;
			ByteArrayOutputStream baos = null;
			try {
				System.out.println("url--" + urlStr);
				url = new URL(urlStr);
				conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(30000);
				conn.setConnectTimeout(30000);
				conn.setRequestMethod("GET");
				conn.setRequestProperty("accept", "*/*");
				conn.setRequestProperty("connection", "Keep-Alive");
				conn.connect();
				
					is = conn.getInputStream();
					baos = new ByteArrayOutputStream();
					int len = -1;
					byte[] buf = new byte[128];
					System.out.println("--------");
					while ((len = is.read(buf)) != -1) {
						baos.write(buf, 0, len);
					}
					baos.flush();
					System.out.println("doGet:" + baos.toString());
					retrunStr = baos.toString();
				

			} catch (MalformedURLException e) {
				// url错误的异常
				e.printStackTrace();
			} catch (IOException e) {
				// 网络错误异常
				e.printStackTrace();
			} finally {
				try {
					if (is != null)
						is.close();
				} catch (IOException e) {
				}
				try {
					if (baos != null)
						baos.close();
				} catch (IOException e) {
				}
				conn.disconnect();
			}
			return retrunStr;
		}  
		public static String doGetAsyn(final String urlStr) {
			
			new Thread() {
				public void run() {
					try {
						result = doGet(urlStr);

					} catch (Exception e) {
						e.printStackTrace();
					}
				};
			}.start();
			return result;
		}
		
}

