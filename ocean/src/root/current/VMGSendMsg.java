package root.current;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

//import utils.Protocol;


public class VMGSendMsg {
	
	
	 public static String sendVMG(String phone,String message) throws MalformedURLException, IOException {
		 String url="http://brandsms.vn:8018/VMGAPI.asmx?wsdl";

		 String xml="<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
					"<Body>" + 
					"<BulkSendSms xmlns=\"http://tempuri.org/\">" + 
					"<msisdn>"+phone+"</msisdn>" + 
					"<alias>0898566896</alias>"+
					"<message>"+message+"</message>" + 
					"<sendTime></sendTime>" + 
					"<authenticateUser>providence</authenticateUser>" + 
					"<authenticatePass>vmg123456</authenticatePass>" + 
					"</BulkSendSms>" + 
					"</Body>" + 
					"</Envelope>";
			String response = HttpRequest(url,xml);
			
			if(response.contains("<error_code>0</error_code>")){
				System.out.println("1");
			}
			
			return response ;
	 }
	public static void main(String[] args) throws MalformedURLException, IOException {
	   
			 
				
				VMGSendMsg sms = new VMGSendMsg();
				sms.sendVMG("1682542920", "OLAVA xin thong bao:Vui long nhap ma 3315, hieu luc 3phut. Vui long khong cung cap thong tin cho bat cu ai.");
				
	}
	
	
	public static String HttpRequest(String urlReq, String xml) throws MalformedURLException,
	IOException {
	 
	//Code to make a webservice HTTP request
	String responseString = "";
	String outputString = "";
	URL url = new URL(urlReq);
	URLConnection connection = url.openConnection();
	HttpURLConnection httpConn = (HttpURLConnection)connection;
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
	  
}

