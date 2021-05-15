package com.task;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
 
 
public class PostHttp {
 
	public static String getJsonData(JSONObject jsonParam,String urls) {
		StringBuffer sb=new StringBuffer();
		try {
			;
			// ����url��Դ
			URL url = new URL(urls);
			// ����http����
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// �����������
			conn.setDoOutput(true);
                        // ������������
                        conn.setDoInput(true);
                       // ���ò��û���
                       conn.setUseCaches(false);
                       // ���ô��ݷ�ʽ
                       conn.setRequestMethod("POST");
                       // ����ά�ֳ�����
                        conn.setRequestProperty("Connection", "Keep-Alive");
                       // �����ļ��ַ���:
                       conn.setRequestProperty("Charset", "UTF-8");
                       // ת��Ϊ�ֽ�����
                       byte[] data = (jsonParam.toString()).getBytes();
                      // �����ļ�����
                       conn.setRequestProperty("Content-Length", String.valueOf(data.length));
                      // �����ļ�����:
                      conn.setRequestProperty("contentType", "application/json");
                        // ��ʼ��������
                       conn.connect();		
                    OutputStream out = new DataOutputStream(conn.getOutputStream()) ;
			// д��������ַ���
			out.write((jsonParam.toString()).getBytes());
			out.flush();
			out.close();
 
			System.out.println(conn.getResponseCode());
			
			// ���󷵻ص�״̬
			if (HttpURLConnection.HTTP_OK == conn.getResponseCode()){
				System.out.println("���ӳɹ�");
				// ���󷵻ص�����
				InputStream in1 = conn.getInputStream();
				try {
				      String readLine=new String();
				      BufferedReader responseReader=new BufferedReader(new InputStreamReader(in1,"UTF-8"));
				      while((readLine=responseReader.readLine())!=null){
				        sb.append(readLine).append("\n");
				      }
				      responseReader.close();
				      System.out.println(sb.toString());
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else {
				System.out.println("error++");
				
			}
 
		} catch (Exception e) {
 
		}
		
		return sb.toString();
 
	}
    public static String sendGet(String url) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url ;
            //+ "?" + param
            URL realUrl = new URL(urlNameString);
            // �򿪺�URL֮�������
            URLConnection connection = realUrl.openConnection();
            // ����ͨ�õ���������
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            //	���ó�ʱʱ��
            connection.setConnectTimeout(10000);
            // ����ʵ�ʵ�����
            connection.connect();
            // ��ȡ������Ӧͷ�ֶ�
            Map<String, List<String>> map = connection.getHeaderFields();
            
            // ���� BufferedReader����������ȡURL����Ӧ
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("����GET��������쳣��" + e);
            e.printStackTrace();
        }
        // ʹ��finally�����ر�������
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
}
