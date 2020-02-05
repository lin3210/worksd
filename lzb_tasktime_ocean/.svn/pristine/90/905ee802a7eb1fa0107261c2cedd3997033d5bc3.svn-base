/**
 *@author: chenyoulong 
 *@Title:DownloadTest.java
 *@date 2014-9-2 下午9:21:00 
 *@Description:TODO
 */
package service.download;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;

import service.encrypt.MD5;
import service.util.SslConnection;



/**
 *@ClassName:DownloadTest
 *@author: chenyoulong  Email: chen.youlong@payeco.com
 *@date :2014-9-2 下午9:21:00
 *@Description:TODO 
 */
public class DownloadTest {
	
	public static void main(String[] args) {
//		testDownloadCheckingFile(); //下载对账文件（已对账）
		testDownloadCheckingSuccessFile();//下载对账文件（交易成功）
	}
	public static void testDownloadCheckingFile(){
		HttpURLConnection conn = null;
		FileOutputStream fos = null ;
	    
		try {
			String msg_type = "200003";
			String merchant_no = "00000001";//系统商户编号
			String merchant_key = "5D38F98CC75A4B9D";//商户秘钥
			String trans_date = "20140831";
			
			String org_mac=msg_type +" " + merchant_no +" " + trans_date+" " +merchant_key;
			System.out.println("MAC源码："+org_mac);
			MD5 md5 = new MD5();
			String MAC = md5.getMD5ofStr(org_mac);
			
			String url = "https://agent.payeco.com/download?MSG_TYPE="+msg_type+"&MERCHANT_NO="+merchant_no+"&TRANS_DATE="+trans_date+"&MAC="+MAC;
			System.out.println("URL:"+url);
			
			//SSL
			SslConnection ssl = new SslConnection();
			conn = ssl.openConnection(url); 
			conn.setRequestMethod("POST");
			conn.setReadTimeout(60000);
			conn.setConnectTimeout(60000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			InputStream instream = conn.getInputStream();	
			fos = new FileOutputStream("D:\\Test\\代收对账单.txt");
			byte[] buf = new byte[1024];
			
			int len = 0;
			
			while((len = instream.read(buf)) != -1){
				fos.write(buf, 0, len);
			}
			fos.flush();
			fos.close();	
			System.out.println("下载成功！");
			
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("下载失败！");
		}finally{
			if(conn != null)
				conn.disconnect();
			
		}
	}
	
	public static void testDownloadCheckingSuccessFile(){
		HttpURLConnection conn = null;
		FileOutputStream fos = null ;
	    
		try {
			String msg_type = "200004";
			String merchant_no = "00000001"; //商户号
			String merchant_key = "5D38F98CC75A4B9D";//商户密钥
			String trans_date = "20140831"; //查询日期
			
			String org_mac=msg_type +" " + merchant_no +" " + trans_date+" " +merchant_key;
			System.out.println("MAC源码："+org_mac);
			MD5 md5 = new MD5();
			String MAC = md5.getMD5ofStr(org_mac);
			
			String url = "http://localhost:8080/download?MSG_TYPE="+msg_type+"&MERCHANT_NO="+merchant_no+"&TRANS_DATE="+trans_date+"&MAC="+MAC;
			System.out.println("URL:"+url);
			
			//SSL
			SslConnection ssl = new SslConnection();
			conn = ssl.openConnection(url); 
			conn.setRequestMethod("POST");
			conn.setReadTimeout(60000);
			conn.setConnectTimeout(60000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			InputStream instream = conn.getInputStream();	
			fos = new FileOutputStream("D:\\Test\\代收对账单（交易成功）.txt");
			byte[] buf = new byte[1024];
			
			int len = 0;
			
			while((len = instream.read(buf)) != -1){
				fos.write(buf, 0, len);
			}
			fos.flush();
			fos.close();	
			System.out.println("下载成功！");
			
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("下载失败！");
		}finally{
			if(conn != null)
				conn.disconnect();
			
		}
	}
}
