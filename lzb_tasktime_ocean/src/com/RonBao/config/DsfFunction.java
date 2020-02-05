package com.RonBao.config;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.apache.commons.lang3.ArrayUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.RonBao.util.Md5Encrypt;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


public class DsfFunction {

    public static String BuildMysign(Map<String,String> sArray, String key) {
        String prestr = CreateLinkString(sArray);  //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        prestr = prestr + key;                     //把拼接后的字符串再与安全校验码直接连接起来
        System.out.println("=="+prestr+"============");
        String mysign = Md5Encrypt.md5(prestr);
        //System.out.println("+++"+mysign+"============");
        return mysign;
    }

    public static Map parse(String protocolXML){ 
		 try {
			 Map<String, Object> map = new HashMap<String, Object>();
			 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();   
			 DocumentBuilder builder = factory.newDocumentBuilder();   
			 Document doc = builder.parse(new InputSource(new StringReader(protocolXML)));   
		  
			 Element root = doc.getDocumentElement();   
			 NodeList books = root.getChildNodes();   
			 if (books != null) {   
				 for (int i = 0; i < books.getLength(); i++) {   
					 Node book = books.item(i);
					 if(book.getNodeName().equals("batchContent")){
						 map.put(book.getNodeName(), book.getFirstChild().getTextContent());
					 }else{
					    map.put(book.getNodeName(), book.getFirstChild().getNodeValue());
					 }
				 }   
			 }   
			 return map;
		 } catch (Exception e) {
				// TODO: handle exception
			 return null;
		 }
	 }
   
    
    /**
     * 功能：除去数组中的空值和签名参数
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static Map<String,String> ParaFilter(Map<String,String> sArray){
        List<String> keys = new ArrayList<String>(sArray.keySet());
        Map<String,String> sArrayNew = new HashMap<String,String>();
        for(int i = 0; i < keys.size(); i++){
            String key = keys.get(i);
            String value = sArray.get(key);
            /*if(value.equals("") || value == null ||
                       key.equalsIgnoreCase("sign") || key.equalsIgnoreCase("sign_type")){//新增notifyid不参加签名,只做标识用
                   continue;
               }*/

            sArrayNew.put(key, value);
        }

        return sArrayNew;
    }
    /**
     * 功能：把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String CreateLinkString(Map<String,String> params){
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符o
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }

        return prestr;
    }

    /**
     * 普通TXT格式
     * @param fileName
     * @return
     */
    public static String readFileByLines(String fileName) {
        StringBuffer sb=new StringBuffer();

        FileInputStream  file = null;
        BufferedReader reader = null;
        try {
            file = new FileInputStream (fileName);
            InputStreamReader isr = new InputStreamReader(file, "GBK");
            reader = new BufferedReader(isr);
            String tempString = null;
            int line = 0;
            while ((tempString = reader.readLine()) != null) {
                //过滤空行
                if (tempString.trim().length()==0) {
                    continue;
                }
                line++;
                //过滤第一行
                if(line>1){
                    sb.append(tempString+"|");
                }

            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();

                } catch (IOException e1) {
                }
            }
        }
        //System.out.println(sb.toStrieng());
        return sb.toString();
    }
    
    /**
     * @Title: jm
     * @author Eric bjwdong@cn.ibm.com
     * @Description: 加密
     * @param @param content
     * @param @param pa
     * @param @return
     * @param @throws UnsupportedEncodingException
     * @param @throws CertificateException
     * @param @throws FileNotFoundException
     * @param @throws NoSuchAlgorithmException
     * @param @throws NoSuchPaddingException
     * @param @throws InvalidKeyException
     * @param @throws IllegalBlockSizeException
     * @param @throws BadPaddingException
     * @return String
     * @throws
     */
    public static String jm(String content,String pa) throws UnsupportedEncodingException, CertificateException, FileNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        byte[] msg = content.getBytes("GBK");     // 待加解密的消息

        // 用证书的公钥加密
        CertificateFactory cff = CertificateFactory.getInstance("X.509");
        FileInputStream fis1 = new FileInputStream(pa); // 证书文件
        Certificate cf = cff.generateCertificate(fis1);
        PublicKey pk1 = cf.getPublicKey();           // 得到证书文件携带的公钥
        Cipher c1 = Cipher.getInstance("RSA/ECB/PKCS1Padding");      // 定义算法：RSA
        byte[] dataReturn=null;
        c1.init(Cipher.PUBLIC_KEY, pk1);
        //StringBuilder sb = new StringBuilder();
        for (int i = 0; i < msg.length; i += 100) {
            byte[] doFinal = c1.doFinal(ArrayUtils.subarray(msg, i, i + 100));

            //sb.append(new String(doFinal,"gbk"));
            dataReturn = ArrayUtils.addAll(dataReturn, doFinal);
        }

        BASE64Encoder encoder = new BASE64Encoder();

        String afjmText=encoder.encode(dataReturn);

        return afjmText;
    }
    /**
     * @Title: jim
     * @author Eric bjwdong@cn.ibm.com
     * @Description: 解密
     * @param @param dataReturn_r
     * @param @param pa
     * @param @return
     * @param @throws Exception
     * @return String
     * @throws
     */
    public static String jim(byte [] dataReturn_r,String pa) throws Exception{
        final String KEYSTORE_FILE = pa;
        final String KEYSTORE_PASSWORD = "clientok";
        final String KEYSTORE_ALIAS = "clientok";

        KeyStore ks = KeyStore.getInstance("PKCS12");
        FileInputStream fis = new FileInputStream(KEYSTORE_FILE);

        char[] nPassword = null;
        if((KEYSTORE_PASSWORD == null)||KEYSTORE_PASSWORD.trim().equals("")){
            nPassword = null;
        }else{
            nPassword = KEYSTORE_PASSWORD.toCharArray();
        }
        ks.load(fis,nPassword);
        fis.close();
        PrivateKey prikey = (PrivateKey)ks.getKey(KEYSTORE_ALIAS, nPassword);
        Cipher rc2 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rc2.init(Cipher.DECRYPT_MODE, prikey);
//		    byte[] rmsg2 = rc2.doFinal(dataReturn_r); // 解密后的数据，不能超过128个字节
        StringBuilder bf_r = new StringBuilder();
        byte [] bs = null;
        for (int i = 0; i < dataReturn_r.length; i += 128) {
            byte[] subarray = ArrayUtils.subarray(dataReturn_r, i,i + 128);
            byte[] doFinal = rc2.doFinal(subarray);
            bs = ArrayUtils.addAll(bs, doFinal);
            //bf_r.append(new String(doFinal,DsfConfig.input_charset));
        }
        bf_r.append(new String(bs,DsfConfig.input_charset));
        return bf_r.toString();
    }

    public static void main(String[] args) {
//        int a=6;
//        int b = a%2==0?1:0;
//        System.out.println(b);
        String str = "X55KVckCBFO25DKi272ukRc/7ceceP3tXwaZpO+wzECb87mQhDRJXiWOyyjTh/2PIISA1xbG9QuH" +
        		"T9XGeg72QvtAUEEFQP7zxaM6QWK4U+4vY2t3gQMM8VtxXLzZVcMdQ0nSYK2phyS3as0eGGVm5ROU" +
        		"q6fdPIy6Vj0hygD0NHDJWd/5MSZBqtTRvIwLHVCL3BfGf9iJPGwI5nlRL9m/mOr7caVuyw4LDBVC" +
        		"3ffAOM6xQnTzlHimUEG/DTjLPBamHbfYaBp6g+IqrWN4M5qXAau1X0sg3L/AomO4opl97Dr5ujQs" +
        		"gI9FvxbbNEYqm24bieuZSgQ8OO1HVbEaocNgi4a41nCMUsWVbmxTwx6YDB9xIDUDgvkjXyVhSvBI" +
        		"4JBJbrnLmJ3GlbHmfbvRHNumU7MZnURSbJQ4cI17sy90XSjqSyVrHkJ8ltB1XP7s0KnJbN0osRQh" +
        		"vVRMp66/A/phgjHmkbyFoqWnIgv4qE/MBdYACBDnORRzvL52nfU9o4ILMcM700D0lLEtp/nwXzmz" +
        		"feuR7XtbREOMZKAvTGmulJRuMWa+8Nqs4Eee/hHzaeRgGJsSdgy2R0Cb73bitVI1Y3Bj9lcl8+r7" +
        		"c9dWZ6VFlvM7yrDV3kQDpltSF55feckUoRXsuy7euIKPFZshDOeFF9oFcBEGrPOdx6k361GYqx9V" +
        		"x0eRIh7D0zllUzkgZOl7IRde5RnFuhD/H3iRo1INEJNQ8aw5sQqdmmLb6s/Q8fUNQTCZdelycyEm" +
        		"htHzJmCN6bEs89I1JsSqSJh/xB3O8UDmsh74hCx6mYbbxILTML3a/lwWklsDU7VVxjIONMV00UM2" +
        		"aIZdSoCm/KsNHwSJQw==";
        DsfFunction df = new DsfFunction();
        BASE64Decoder decoder = new BASE64Decoder();
        String ss;
		try {
			byte[] bb = decoder.decodeBuffer(str);
			ss = DsfFunction.jim(bb, "");
			System.out.println("::::+"+ss);
		} catch (Exception e) {
			// TODO: handle exception
		}
        
        
    }
}
