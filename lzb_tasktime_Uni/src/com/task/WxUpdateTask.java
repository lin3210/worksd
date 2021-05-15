package com.task;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
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
import net.sf.json.JSONObject;
import com.service.WxUpdateService;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.timerengine.Task;
import com.util.MemCacheServer;
import com.util.MemCachedUtil;

public class WxUpdateTask  implements Task 
{

	private static WxUpdateService wxUpdateService = new WxUpdateService();
	private static Logger logger = Logger.getLogger(WxUpdateTask.class);
	private static long time = 1000*60*100;//һ����Сʱˢ��һ��
	private static String appid = "wx1428c5eb3988403c";
	private static String secret = "5ea6baa3ca6ccb73d8902a2732d42ca9";
	@Override
	public void execute() 
	{
			List<DataRow> list = wxUpdateService.getWxUserList();
			String Access_Token = getAccess_Token();
			System.out.println(Access_Token);
			for (DataRow dataRow : list) 
			{
				System.out.println(dataRow.getString("w_openid"));
				JSONObject access_token = sendUrlRequest("https://api.weixin.qq.com/cgi-bin/user/info?access_token="+Access_Token+"&openid="+dataRow.getString("w_openid")+"&lang=zh_CN");
				logger.info(access_token);
				if(null!=access_token||access_token.toString().indexOf("errcode")==-1)
				{
					if(access_token.get("headimgurl")!=null)
					{
						String headimgurl = access_token.get("headimgurl").toString();
						headimgurl = headimgurl.replace("/0", "/64");
						dataRow.set("w_tx", getImageFromNetByUrl(headimgurl));
						dataRow.set("w_tx_gxtime", new Date());
						logger.info("�����û�ͷ��"+dataRow);
					}
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			wxUpdateService.UpdateWxByList(list);
			list = null;
		
	}
	
	 /** 
     * ��ͼƬд�뵽���� 
     * @param img ͼƬ����� 
     * @param fileName �ļ�����ʱ����� 
     */  
    public static void writeImageToDisk(byte[] img, String fileName){  
        try {  
            File file = new File("C:\\wxtx\\" + fileName);  
            FileOutputStream fops = new FileOutputStream(file);  
            fops.write(img);  
            fops.flush();  
            fops.close();  
            System.out.println("ͼƬ�Ѿ�д�뵽C��");  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  

	/** 
     * ��ݵ�ַ�����ݵ��ֽ��� 
     * @param strUrl �������ӵ�ַ 
     * @return 
     */  
    public static String getImageFromNetByUrl(String strUrl){  
        try {  
            URL url = new URL(strUrl);  
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
            conn.setRequestMethod("GET");  
            conn.setConnectTimeout(5 * 1000);  
            InputStream inStream = conn.getInputStream();//ͨ����������ȡͼƬ���  
            byte[] btImg = readInputStream(inStream);//�õ�ͼƬ�Ķ��������  
            String fileName = new Date().getTime()+".jpg";
            writeImageToDisk(btImg, fileName);
            return fileName;
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return "";  
    }  
    /** 
     * ���������л�ȡ��� 
     * @param inStream ������ 
     * @return 
     * @throws Exception 
     */  
    public static byte[] readInputStream(InputStream inStream) throws Exception{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int len = 0;  
        while( (len=inStream.read(buffer)) != -1 ){  
            outStream.write(buffer, 0, len);  
        }  
        inStream.close();  
        return outStream.toByteArray();  
    }  
    
	public static void main(String[] args) 
	{
		MemCacheServer.getInstance();
		new WxUpdateTask().execute();
//		String str = "http://wx.qlogo.cn/mmopen/DrFWzNzqFEAwNJ1lo8jjDTqhaMq0yvibKfexUZSOGtxQMECEonnn0dzm1uICibKJ7lQTbk7LHb0zVPRibnSmvwicoFjsc3QL54Uw/0";
//		str = str.replace("/0", "/64");
//		System.out.println(str);
//		System.out.println(new WxUpdateTask().getImageFromNetByUrl(str));
	}
	
	/**
	 * ��ȡAccess_Token���뻺�棬ʱ��7200�룬��ʱ֮���ٴλ�ȡ
	 */
	public static String  getAccess_Token()
	{
		try 
		{
			if(null==MemCachedUtil.cachedClient.get("wx_access_token_ldc"))
			{
				JSONObject obj = sendUrlRequest("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appid+"&secret="+secret);
			    if(obj!=null)
			    {
			    	if(null!=obj.getString("access_token"))
			    	{
			    		MemCachedUtil.cachedClient.set("wx_access_token_ldc", obj.getString("access_token"), new Date(time));
			    		return obj.getString("access_token");
			    	}
			    }
			}
			else
			{
				return MemCachedUtil.cachedClient.get("wx_access_token_ldc").toString();
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
	public static  JSONObject sendUrlRequest(String urlStr) 
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
			logger.error(e);
		}
		catch(Exception ex)
		{
			logger.error(ex);
		}
		finally{
		httpclient.getConnectionManager().shutdown();
		}
		return null;
	}
}
