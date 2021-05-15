package root;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

public class Postaliyun {
    /**  
     * ��ָ�� URL ����POST����������  
     *   
     * @param url  
     *            ��������� URL  
     * @param param  
     *            ����������������Ӧ���� name1=value1&name2=value2 ����ʽ��  
     * @return ������Զ����Դ����Ӧ���  
     */  
    public static String sendPost(String url, String param) {  
        PrintWriter out = null;  
        BufferedReader in = null;  
        String result = "";  
        try {  
            URL realUrl = new URL(url);  
            // �򿪺�URL֮�������  
            URLConnection conn = realUrl.openConnection();  
            // ����ͨ�õ���������  
            conn.setRequestProperty("accept", "*/*");  
            conn.setRequestProperty("connection", "Keep-Alive");    
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            // ����POST�������������������  
            conn.setDoOutput(true);  
            conn.setDoInput(true);  
            // ��ȡURLConnection�����Ӧ�������  
            out = new PrintWriter(conn.getOutputStream());  
            // �����������  
            out.print(param);  
            // flush������Ļ���  
            out.flush();  
            // ����BufferedReader����������ȡURL����Ӧ  
            in = new BufferedReader(  
                    new InputStreamReader(conn.getInputStream(),"UTF-8"));  
            String line = "";  
            while ((line = in.readLine()) != null) {  
                result += line;  
            }  
        } catch (Exception e) {  
            System.out.println("���� POST ��������쳣��"+e);  
            e.printStackTrace();  
        }  
        //ʹ��finally�����ر��������������  
        finally{  
            try{  
                if(out!=null){  
                    out.close();  
                }  
                if(in!=null){  
                    in.close();  
                }  
            }  
            catch(IOException ex){  
                ex.printStackTrace();  
            }  
        }  
        return result;  
    } 
}
