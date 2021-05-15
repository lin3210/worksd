package com.util;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.util.URIUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class PartnerRequestDemo {

	private static String fformatStr = "/dsp-front/4.1/dsp-front/default/pubkey/%s/product_code/%s/out_order_id/%s/signature/%s";
	static final String PUB_KEY = "cf755e26-bcf2-4ebb-840e-30387807ea8f"; // �̻�����ʱ�����pub_key
	static final String SECURITY_KEY = "7caa0671-9d30-470b-9d13-792c817e1506"; // �̻�����ʱ�����security_key
	public static String apiCall(String url, String pubkey, String
          	      secretkey, String serviceCode, String outOrderId,
	               Map<String, String> parameter) throws Exception {
	
		             if (parameter == null || parameter.isEmpty())
	                  throw new Exception("error ! the parameter Map can't be null.");
	                  StringBuffer bodySb = new StringBuffer("{");
	              for (Map.Entry<String, String> entry : parameter.entrySet()) {
	                bodySb.append("'").append(entry.getKey()).append("':'").append(entry.getValue()).append("',");
	                }
	              String bodyStr = bodySb.substring(0, bodySb.length() - 1) + "}";
                  String signature = md5(bodyStr + "|" + secretkey);
	              url += String.format(fformatStr, pubkey, serviceCode,
	              System.currentTimeMillis() + "", signature);
	              System.out.println("requestUrl=>" + url);
	              System.out.println("request parameter body=>" + bodyStr);
	              HttpResponse r = makePostRequest(url, bodyStr);
	            return EntityUtils.toString(r.getEntity());
	   }
	private static final CloseableHttpClient client =
	                         HttpClientBuilder.create().build();
	          private static HttpResponse makePostRequest(String uri, String jsonData)
	          throws ClientProtocolException, IOException {
	          HttpPost httpPost = new HttpPost(URIUtil.encodeQuery(uri, "utf-8"));
	          httpPost.setEntity(new StringEntity(jsonData, "UTF-8"));
	          httpPost.setHeader("Accept", "application/json");
	          httpPost.setHeader("Content-type", "application/json; charset=utf-8");
	          return client.execute(httpPost);
	}
	private static String md5(String data) throws NoSuchAlgorithmException {
	      MessageDigest md = MessageDigest.getInstance("MD5");
	      md.update(data.toString().getBytes());
	      return bytesToHex(md.digest());
	}
	private static String bytesToHex(byte[] ch) {
	      StringBuffer ret = new StringBuffer("");
	      for (int i = 0; i < ch.length; i++)
	      ret.append(byteToHex(ch[i]));
	      return ret.toString();
	}
	/**
	* �ֽ�ת��Ϊ16�����ַ�
	*/
	private static String byteToHex(byte ch) {	
		String str[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
		      "A", "B", "C", "D", "E", "F" };
		   return str[ch >> 4 & 0xF] + str[ch & 0xF];
	 }
	
	public static void main(String[] args) {
		
		PartnerRequestDemo ydInfo = new PartnerRequestDemo();
		ydInfo.getYdfkInfo("21092219990205361X", "11174&1490860819703");
		/*Map<String, String> body = new HashMap<String, String>();
		body.put("id_no", "421125198804272713");
			try {
				
			 String result =
			     PartnerRequestDemo.apiCall("https://api4.udcredit.com",
			    		 PUB_KEY, SECURITY_KEY, "Y1001003",
			     "13033&1490929679765", body);
			
			  
			  com.alibaba.fastjson.JSONObject reqObject1 = (com.alibaba.fastjson.JSONObject) JSON.parse(result);
			
			  String header =reqObject1.getString("header");	
			  //������Ϣ
			  com.alibaba.fastjson.JSONObject reqObject3 = (com.alibaba.fastjson.JSONObject) JSON.parse(header);
			
			  if(reqObject3.getString("ret_code").equals("000000")){
			  
					  String body1 =reqObject1.getString("body");
					// System.out.println(body1);  
					  com.alibaba.fastjson.JSONObject reqObject2 = (com.alibaba.fastjson.JSONObject) JSON.parse(body1); 
					//�õ��û��Ļ���Ϣ
					  String user_detail =reqObject2.getString("user_detail");	
					//�õ��û��ƶ��豸ʹ�����			
					com.alibaba.fastjson.JSONArray devices = reqObject2.getJSONArray("devices"); 
					System.out.println("�û��ƶ��豸ʹ�����:"+devices);
					  for (int i = 0; i < devices.size(); i++) { 
						  System.out.println((com.alibaba.fastjson.JSONObject) devices.get(i));
					  }
					 //���е����п� 
					String bankcard_count =reqObject2.getString("bankcard_count");
					  //������п����������0 ������е����п���Ϣ
					  if(Integer.parseInt(bankcard_count)>0){
						  
		                  com.alibaba.fastjson.JSONArray bankcards = reqObject2.getJSONArray("bankcards"); 
		                  for (int i = 0; i < bankcards.size(); i++) { 
		                	  //System.out.println((com.alibaba.fastjson.JSONObject) bankcards.get(i));               	  
		                  }
		    
					  }
					  
					  //�������
					 //(1)�ж��Ƿ����loan_industry �ֶ�
					if(reqObject2.containsKey("loan_industry")){ 
						 com.alibaba.fastjson.JSONArray loan_industrys = reqObject2.getJSONArray("loan_industry"); 
						  for(int i = 0; i < loan_industrys.size(); i++){		      
					      // System.out.println((com.alibaba.fastjson.JSONObject) loan_industrys.get(i)); 
						  }
					 }
			
			  }
			} catch (Throwable e) {
				
		       e.printStackTrace();
		}*/
		
	}
	
	public  String  getYdfkInfo(String idNo ,String orderId) {
		
		Map<String, String> body = new HashMap<String, String>();
		String result="" ;
		body.put("id_no", idNo);
			try {
				
			 result =
			     PartnerRequestDemo.apiCall("https://api4.udcredit.com",
			    		 PUB_KEY, SECURITY_KEY, "Y1001003",
			    		 orderId, body);	
			System.out.println(result);
			} catch (Throwable e) {
			   result ="fail";
		       e.printStackTrace();
		}
		return result ;
	}
}

