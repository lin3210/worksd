package root;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
import javax.net.ssl.HttpsURLConnection;
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


public class SendFTP {
	
	 //验证用户信息F360
	 private static final String Username = "f168";
	 private static final String Password = "f16812";// n/a
	 private static final String Brandname = "F168";// n/a
	 
	 private static final String charset = "utf-8";
	 private static final String url = "https://api.onesms.vn/wsPartners/Service.asmx/SendMT";
	 private static Logger logger = Logger.getLogger(SendFTP.class);
	 
	 public static String sendMessageFTP(String content,String phone) throws UnsupportedEncodingException{
	        String map = "";
	        HttpClient httpClient = new DefaultHttpClient();
	        String fullUrl="";
	        String con = URLEncoder.encode(content, "utf-8");
	        /*
			 * if(isViettelPhone(phone)) {
			 * 
			 * }
			 */
		    con = con.replaceFirst("MOFA", "F168").replaceFirst("Mofa", "F168");
	        String phonenum = "84"+phone.substring(1);
	        fullUrl = url + "?user="+Username+"&pass="+Password+"&sms="+con+"&senderName="+Brandname+"&phone="+phonenum+"&isFlash=False&isUnicode=False";
	        System.out.println(fullUrl);
	        PostHttp posthttp = new PostHttp();
	        String resopodmf = posthttp.sendGet(fullUrl);
	        return resopodmf;
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
	 	public static String HttpRequest(String urlReq, String xml) throws MalformedURLException,
		IOException {
		 
		//Code to make a webservice HTTP request
		String responseString = "";
		String outputString = "";
		URL url = new URL(urlReq);
		URLConnection connection = url.openConnection();
		HttpsURLConnection httpConn = (HttpsURLConnection)connection;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		String xmlInput =xml;
		 
		byte[] buffer = new byte[xmlInput.length()];
		buffer = xmlInput.getBytes();
		bout.write(buffer);
		byte[] b = bout.toByteArray();
		// Set the appropriate HTTP parameters.
		//httpConn.setRequestProperty("Content-Length",String.valueOf(b.length));
		httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		httpConn.setRequestMethod("POST");
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);
		OutputStream out = httpConn.getOutputStream();
		//Write the content of the request to the outputstream of the HTTP Connection.
		out.write(b);
		out.close();
		//Ready with sending the request.
		 
		//Read the response.
		InputStreamReader isr =
		new InputStreamReader(httpConn.getInputStream());
		BufferedReader in = new BufferedReader(isr);
		 
		//Write the SOAP message response to a String.
		while ((responseString = in.readLine()) != null) {
		outputString = outputString + responseString+"\n";
		}
			return outputString;
		}
	    //验证手机号方法
    public static boolean checkPhoneNo(String phone){
	        if(phone==null || phone.trim().equals("")){
	            return false;
	         }
	        String regExp = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$"; 
	        Pattern p = Pattern.compile(regExp);  
	        Matcher m = p.matcher(phone);  
	        return m.find();
	    }
	     
	     public static void main(String args[]) throws MalformedURLException, IOException{
	    	 SendFTP sms = new SendFTP();
			  String rESSSS = sms.sendMessageFTP("F168 chao xxx So tien vay 700,000 da chuyen khoan den TK Ngan hang so cuoi 7784 sau 24h chua nhan duoc khoan vay xin lien he: http://bit.ly/2KIzoEe  Hotline: 1900234558","0933760149");
	         System.out.println(rESSSS);
	     }
	     
}

