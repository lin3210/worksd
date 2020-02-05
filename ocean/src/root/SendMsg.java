package root;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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

import com.alibaba.fastjson.JSONException;

import net.sf.json.JSONObject;

public class SendMsg {
	 //��֤�û���Ϣ
	 private static final String loginName = "AB38MG3";
	 private static final String brandName = "n%2Fa";// n/a
	 private static final String serviceTypeId = "190";
	 private static final String content = "";
	 private static final String sign = "2b949d146e92f69664fffb5e4825e65f";
	 //�ַ����뼰��������
	//VERIFY
	 private static final String loginNameVLP = "AB38MG3";
	 private static final String sendSmsPassword = "34PURHEPK";
	 private static final String brandNameVLP = "Verify";// n/a
	 private static final String serviceTypeIdVLP = "30";
	 private static final String contentVLP = "";
	 private static final String signVLP = "715a3440d2859d96b910fd1a2c3c956a";
	 
	 //PLUTUS
	 private static final String loginNamePLUTUS = "AB38MG3";
	 private static final String brandNamePLUTUS = "PLUTUS";// n/a
	 private static final String serviceTypeIdPLUTUS = "30";
	 private static final String signPLUTUS = "94b471dfe70877275fff5a228fb81969";
	 //OTP
	 private static final String loginNameOTP = "AB38MG3";
	 private static final String brandNameOTP = "FastMart.vn";// n/a
	 private static final String serviceTypeIdOTP = "30";
	 private static final String signOTP = "30f280e5584613a8d5120a7d241dada1";
	 //�ַ����뼰��������
	 private static final String charset = "utf-8";
	 private static final String url = "http://api.abenla.com/Service.asmx/SendSms2";
	 private static Logger logger = Logger.getLogger(SendMsg.class);
	 /*
	 * �����󷵻ص�������ֻ��Ҫcode��result�����ֶε���Ϣ����˷���ֻ����һ������������ֵ��map
	 */
	 //get��ʽ����
	 public static String sendMessageByGet(String content,String phone) throws UnsupportedEncodingException{
 	        if(isViettelPhone(phone) == true){
 	        	SendFTP sms = new SendFTP();
 			    try {
 			    	content = URLDecoder.decode(content,"utf-8");
 			    	JSONObject jsonObj = JSONObject.fromObject(content.replace("[", "").replace("]", ""));
 		 	        String smscon = jsonObj.getString("Message");
 					String returnsms = sms.sendMessageFTP(smscon.replaceFirst("MOFA", "F168").replaceFirst("Mofa", "F168"),phone);
 					logger.info(returnsms);
 				} catch (JSONException e) {
 					// TODO Auto-generated catch block
 					e.printStackTrace();
 				}
 		   }else{
 			    String map = "";
 		        HttpClient httpClient = new DefaultHttpClient();
 		        String fullUrl="";
 		        /*if(isVINAPHONE(phone) == true){
 		        	fullUrl = url + "?loginName="+loginNameVLP+"&brandName="+brandNameVLP+"&serviceTypeId="+serviceTypeIdVLP+"&content="+content+"&sign="+signVLP;
 		        }else{*/
 		        	//fullUrl = url + "?loginName="+loginName+"&brandName="+brandName+"&serviceTypeId="+serviceTypeId+"&content="+content+"&sign="+sign;
 		        //}
 	        	
 	        	fullUrl = url + "?loginName="+loginName+"&brandName="+brandName+"&serviceTypeId="+serviceTypeId+"&content="+content+"&sign="+sign;
 	        	logger.info(fullUrl);
 		        HttpGet httpGet = new HttpGet(fullUrl);
 		         try {
 		            HttpResponse response = httpClient.execute(httpGet);
 		            HttpEntity entity = response.getEntity();
 		           if (null != entity) {
 		               InputStream in = entity.getContent();//�����ص�����������������
 		              // ����һ��Document��������
 		               DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
 		                DocumentBuilder builder = factory.newDocumentBuilder();
 		              // ������������ΪDocument
 		                 Document document = builder.parse(in);//��������ʵ����Document
 		 
 		                Element rootElement = document.getDocumentElement();
 		         
 		                 NodeList codeNode = rootElement.getElementsByTagName("Code");
 		                 map =codeNode.item(0).getTextContent();  
 		            }
 		         } catch (ClientProtocolException e) {
 		            e.printStackTrace();
 		         } catch (IllegalStateException e) {
 		            e.printStackTrace();
 		         } catch (DOMException e) {
 		              e.printStackTrace();
 		         } catch (IOException e) {
 		              e.printStackTrace();
 		         } catch (ParserConfigurationException e) {
 		             e.printStackTrace();
 		         } catch (SAXException e) {
 		             e.printStackTrace();
 		         }
 		          return map;
 		   }
 	        return null;
}
	      
	    /*
	 * �����󷵻ص�������ֻ��Ҫcode��result�����ֶε���Ϣ����˷���ֻ����һ������������ֵ��map
	 */
	 //get��ʽ����
	 public static String sendMessageByGetOTP(String content,String phone){
	        String map = "";
	        HttpClient httpClient = new DefaultHttpClient();
	        String fullUrl="";
	        
	        fullUrl = url + "?loginName="+loginNameOTP+"&brandName="+brandNameOTP+"&serviceTypeId="+serviceTypeIdOTP+"&content="+content+"&sign="+signOTP;
	        
	       
	       logger.info(fullUrl);
	        HttpGet httpGet = new HttpGet(fullUrl);
	         try {
	            HttpResponse response = httpClient.execute(httpGet);
	            HttpEntity entity = response.getEntity();
	           if (null != entity) {
	               InputStream in = entity.getContent();//�����ص�����������������
	              // ����һ��Document��������
	               DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	                DocumentBuilder builder = factory.newDocumentBuilder();
	              // ������������ΪDocument
	                 Document document = builder.parse(in);//��������ʵ����Document
	
	                Element rootElement = document.getDocumentElement();
	         
	                 NodeList codeNode = rootElement.getElementsByTagName("Code");
	                 map =codeNode.item(0).getTextContent();  
	            }
	         } catch (ClientProtocolException e) {
	            e.printStackTrace();
	         } catch (IllegalStateException e) {
	            e.printStackTrace();
	         } catch (DOMException e) {
	              e.printStackTrace();
	         } catch (IOException e) {
	              e.printStackTrace();
	         } catch (ParserConfigurationException e) {
	             e.printStackTrace();
	         } catch (SAXException e) {
	             e.printStackTrace();
	         }
	          return map;
	      }

		//post��ʽ����
	     public static Map<String,String> sendMessageByPost(String content,String phones){
	         Map<String,String> map = new HashMap<String, String>();
	        // ����Ĭ�ϵ�httpClientʵ��.    
	         CloseableHttpClient httpclient = HttpClients.createDefault(); 
	        // ����httppost    
	          HttpPost httppost = new HttpPost(url);  
	        // ������������    
	        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
	        formparams.add(new BasicNameValuePair("user", loginName));  
	       formparams.add(new BasicNameValuePair("pwd", brandName)); 
	        formparams.add(new BasicNameValuePair("mobiles", phones)); 
	         formparams.add(new BasicNameValuePair("contents", content)); 
	         
	       UrlEncodedFormEntity uefEntity;
	        try{
	            uefEntity = new UrlEncodedFormEntity(formparams, charset);
	            httppost.setEntity(uefEntity);
	            System.out.println("executing request " + httppost.getURI());
	            CloseableHttpResponse response = httpclient.execute(httppost);
	            try{
	                HttpEntity entity = response.getEntity(); 
	                if (entity != null) { 
	                    //�����ص�����ֱ��ת��String
	                   String str = EntityUtils.toString(entity, "UTF-8") ;
	                     System.out.println("--------------------------------------");  
	                    //ע�����ﲻ��д��EntityUtils.toString(entity, "UTF-8"),��ΪEntityUtilsֻ�ܵ���һ�Σ�����ᱨ��java.io.IOException: Attempted read from closed stream
	                    System.out.println("Response content: " + str);  
	                   System.out.println("--------------------------------------");  
	                    
	                   //������str��Ϊ������� Document ����
	                    org.dom4j.Document document = DocumentHelper.parseText(str);
	                    org.dom4j.Element rootElement = document.getRootElement();
	                   
	                    String code = rootElement.element("Code").getText();
	                    String result = rootElement.element("Result").getText();
	                    map.put("Code", code);
	                    map.put("Result", result);
	                }
	            }catch (DocumentException e) {
	                 // TODO Auto-generated catch block
	                 e.printStackTrace();
	             }finally{
	                 response.close();
	             }
	            
	        } catch (ClientProtocolException e) {  
	             e.printStackTrace();  
	        } catch (UnsupportedEncodingException e1) {  
	            e1.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }finally{
	             // �ر�����,�ͷ���Դ    
	             try {  
	                httpclient.close();  
	             } catch (IOException e) {  
	                 e.printStackTrace();  
	             }  
	        }
	         
	         return map ;
	     }
	 	public static boolean isVINAPHONE(String phone){
			if("094".equals(phone.substring(0, 3))){
				return true;
			}else if("091".equals(phone.substring(0, 3))){
				return true;
			}else if("088".equals(phone.substring(0, 3))){
				return true;
			}else if("083".equals(phone.substring(0, 3))){
				return true;
			}else if("084".equals(phone.substring(0, 3))){
				return true;
			}else if("085".equals(phone.substring(0, 3))){
				return true;
			}else if("081".equals(phone.substring(0, 3))){
				return true;
			}else if("082".equals(phone.substring(0, 3))){
				return true;
			}else if("0123".equals(phone.substring(0, 4))){
				return true;
			}else if("0124".equals(phone.substring(0, 4))){
				return true;
			}else if("0125".equals(phone.substring(0, 4))){
				return true;
			}else if("0127".equals(phone.substring(0, 4))){
				return true;
			}else if("0129".equals(phone.substring(0, 4))){
				return true;
			}else{
				return false;
			}
		}    
	 	public static boolean isViettelPhone(String phone){
	 		if("096".equals(phone.substring(0, 3))){
	 			return true;
	 		}else if("097".equals(phone.substring(0, 3))){
	 			return true;
	 		}else if("098".equals(phone.substring(0, 3))){
	 			return true;
	 		}else if("086".equals(phone.substring(0, 3))){
	 			return true;
	 		}else if("032".equals(phone.substring(0, 3))){
	 			return true;
	 		}else if("033".equals(phone.substring(0, 3))){
	 			return true;
	 		}else if("034".equals(phone.substring(0, 3))){
	 			return true;
	 		}else if("035".equals(phone.substring(0, 3))){
	 			return true;
	 		}else if("036".equals(phone.substring(0, 3))){
	 			return true;
	 		}else if("037".equals(phone.substring(0, 3))){
	 			return true;
	 		}else if("038".equals(phone.substring(0, 3))){
	 			return true;
	 		}else if("039".equals(phone.substring(0, 3))){
	 			return true;
	 		}else if("0162".equals(phone.substring(0, 4))){
	 			return true;
	 		}else if("0163".equals(phone.substring(0, 4))){
	 			return true;
	 		}else if("0164".equals(phone.substring(0, 4))){
	 			return true;
	 		}else if("0165".equals(phone.substring(0, 4))){
	 			return true;
	 		}else if("0166".equals(phone.substring(0, 4))){
	 			return true;
	 		}else if("0167".equals(phone.substring(0, 4))){
	 			return true;
	 		}else if("0168".equals(phone.substring(0, 4))){
	 			return true;
	 		}else if("0169".equals(phone.substring(0, 4))){
	 			return true;
	 		}else{
	 			return false;
	 		}
	 	}   
	    //��֤�ֻ��ŷ���
    public static boolean checkPhoneNo(String phone){
	        if(phone==null || phone.trim().equals("")){
	            return false;
	         }
	        String regExp = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$"; 
	        Pattern p = Pattern.compile(regExp);  
	        Matcher m = p.matcher(phone);  
	        return m.find();
	    }
	     
	     public static void main(String args[]) throws UnsupportedEncodingException{
		/*
		 * String content =
		 * "[{\"PhoneNumber\":\"0933760149\",\"Message\":\"[M88]Ma xac thuc OTP cua ban la 1234, ma xac thuc co hieu luc trong thoi gian 5 phut ke tu khi ban gui tin nhan.\",\"SmsGuid\":\"0933760149\",\"ContentType\":1}]"
		 * ; String con = URLEncoder.encode(content, "utf-8"); SendMsg sendMsg = new
		 * SendMsg(); String returnString =
		 * SendMsg.sendMessageByGetOTP(con,"0933760149");
		 * System.out.println(returnString);
		 */
	    	 String  REF_ID="1VTP2068145148";
	    	 String cmnd= REF_ID.substring(REF_ID.indexOf("P")+1);
	    	 System.out.println(cmnd);
	    }
}

