package com.task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.service.WxSendService;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.timerengine.Task;
import com.util.MemCacheServer;
import com.util.MemCachedUtil;
public class WxSendTask  implements Task 
{

	private static WxSendService wxsendservice = new WxSendService();
	private static Logger logger = Logger.getLogger(WxSendTask.class);
	private static long time = 1000*60*100;//һ����Сʱˢ��һ��
	private static String appid = "wx1428c5eb3988403c";
	private static String secret = "5ea6baa3ca6ccb73d8902a2732d42ca9";
	@Override
	public void execute() 
	{
		//��ѯ���͵�ģ��
		DataRow data = new DataRow();
		data = wxsendservice.getPush();
		logger.info("��ʼִ��ģ������" + data);
		if(data != null){
			wxsendservice.updatePush("3",data);//��״̬��Ϊ������
			String json = wxsendservice.getTemplate("ENDw_g-4EgHCt6F2ljvs4nf9Ni5lwGOuGSx0XOqHFSE");
			String first = data.getString("first");
			String keyword1 = data.getString("keyword1");
			String keyword2 = data.getString("keyword2");
			String remark = data.getString("remark");
			String url = data.getString("url");
			//��ѯ�����û�
			List<DataRow> list = new ArrayList();
//			DataRow row1 = new DataRow();
//			row1.set("w_openid", "odQqBuHFnSoq4aV7soCfcpc_H-1c");
//			DataRow row2 = new DataRow();
//			row2.set("w_openid", "odQqBuHFnSoq4aV7soCfcpc_H-1c");
//			list.add(row1);
//			list.add(row2);
			list = wxsendservice.getWxUserList();
			if(list.size() > 0){
				for(DataRow row : list){
					String temp = json;
					temp = temp.replace("{first}", first);
					temp = temp.replace("{keyword1}", keyword1);
					temp = temp.replace("{keyword2}", keyword2);
					temp = temp.replace("{remark}", remark);
					temp = temp.replace("{template_id}", "ENDw_g-4EgHCt6F2ljvs4nf9Ni5lwGOuGSx0XOqHFSE");
					temp = temp.replace("{url}", url);
					String  openid = row.getString("w_openid");
					temp = temp.replace("{openid}", openid);
					new WxSendTask();
					String message = WxSendTask.doHttpPostJson("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+getAccess_Token(),temp);
					 //�����û�û��ע����״̬ �´β�����
				    net.sf.json.JSONObject object = net.sf.json.JSONObject.fromObject(message);
				    String retCode = object.optString("errcode");
				    if("43004".equals(retCode)){
				    	  DataRow d = new DataRow();
				    	  d.set("w_openid", openid);
				    	  d.set("w_type", 1);
				    	  wxsendservice.updatewxinfo(d);
				    }
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//��Ϣ�������ģ����Ϊ�ѷ���
				wxsendservice.updatePush("2",data);
			}
		}
	}
	public static void main(String[] args) {
		MemCacheServer.getInstance();
		new WxSendTask().execute();
//		MemCachedUtil.cachedClient.set("wx_datou", "��ͷ��ͷ", new Date(time));
//		System.out.println("123"+MemCachedUtil.cachedClient.get("wx_datou").toString());
	}
	
	
	public static String doHttpPostJson(String Url,String json)
    {
	    String message = "";
	    try
	    {
	      URL url = new URL(Url);
	      HttpURLConnection http = (HttpURLConnection) url.openConnection(); 
	      http.setRequestMethod("POST");	 
	      http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");  
	      http.setDoOutput(true);	
	      http.setDoInput(true);
	      System.setProperty("sun.net.client.defaultConnectTimeout", "30000");//���ӳ�ʱ30��
	      System.setProperty("sun.net.client.defaultReadTimeout", "30000"); //��ȡ��ʱ30��
	      http.connect();
	      OutputStream os= http.getOutputStream();  
	      os.write(json.getBytes("UTF-8"));//�������
	      os.flush();
	      os.close();
	      InputStream is =http.getInputStream();
	      int size =is.available();
	      byte[] jsonBytes =new byte[size];
	      is.read(jsonBytes);
	      message=new String(jsonBytes,"UTF-8");
	      System.out.println(message);
	      logger.info(message);
	    } 
	    catch (MalformedURLException e)
	    {
	        e.printStackTrace();
	    }
	    catch (IOException e)
	    {
	        e.printStackTrace();
	    } 
	    return message;
	}
	
	/**
	 * ��ȡAccess_Token���뻺�棬ʱ��7200�룬��ʱ֮���ٴλ�ȡ
	 */
	public static String  getAccess_Token()
	{
		try 
		{
			if(null==MemCachedUtil.cachedClient.get("wx_ldc_access_token"))
			{
				new WxSendTask();
				JSONObject obj = WxSendTask.sendUrlRequest("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appid+"&secret="+secret);
			    if(obj!=null)
			    {
			    	if(null!=obj.getString("access_token"))
			    	{
			    		MemCachedUtil.cachedClient.set("wx_ldc_access_token", obj.getString("access_token"), new Date(time));
			    		return obj.getString("access_token");
			    	}
			    }
			}
			else
			{
				return MemCachedUtil.cachedClient.get("wx_ldc_access_token").toString();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
		return "";
	}
	
	/**
	 * HTTP����
	 * @param urlStr
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static  JSONObject sendUrlRequest(String urlStr) throws Exception
	{
		String tempStr=null;
		HttpClient httpclient=new DefaultHttpClient();
		Properties properties=new Properties();
		HttpEntity entity=null;
		String xmlContent="";
		httpclient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		try
		{
			//���ó�ʱʱ��
			httpclient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,20000);
			httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,20000);
	        
			//��װ��Ҫ���ݵĲ���
			List<NameValuePair> nvps = new ArrayList();
			//�ͻ��˵����󷽷�����
			HttpPost httpPost=new HttpPost(urlStr);
			httpPost.setEntity(new UrlEncodedFormEntity(nvps,"UTF-8"));
			HttpResponse response=httpclient.execute(httpPost);
			
			//��ȡ����������Http��Content-Type��ֵ
			tempStr=response.getHeaders("Content-Type")[0].getValue().toString();
			//��ȡ����������ҳ���ֵ
			entity=response.getEntity();
			xmlContent=EntityUtils.toString(entity);
			String trmessage=null;
			logger.info("������Ϣ��"+xmlContent);
			httpPost.abort();
			JSONObject obj = JSONObject.fromString(xmlContent);
			return obj;
	        
		}
		catch(SocketTimeoutException e)
		{
		}
		catch(Exception ex)
		{
		ex.printStackTrace();
		}
		finally{
		httpclient.getConnectionManager().shutdown();
		}
		return null;
	}
}
