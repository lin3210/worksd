package com.task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;


import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import sun.misc.BASE64Decoder;

import com.service.AotuZDSHALLService;
import com.alibaba.fastjson.JSON;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.util.security.RSAUtil;
import com.thinkive.timerengine.Task;

public class OLVconnection implements Task {
	private static Logger logger = Logger.getLogger(OLVconnection.class);
	private static AotuZDSHALLService aotuZDSHALLService = new AotuZDSHALLService();
	/*private static UserService userService = new UserService();*/
	//private static String pwd = "olv1283609640";s
	public static final String KEY_ALGORITHM = "RSA";
	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
	  private static final String DEFAULT_PUBLIC_KEY= "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC3+bWQP/qqpjzT2XruGDwhbWDl"+
			  "ofPNuvufFm5+vrQvG8WhBrN63hfwg+NhR2NQxvLRaGNoklWKp4hUZa3rm6myMLaB"+
			  "GKV3BEuXl1CSWf1TBJSBMT1zy5C/G+3w1/jrf779QtX0gUixQ9YdwHShJTcBE+0q"+
			  "hS7MqF+y5Z5NXy+fLwIDAQAB";
		      
		    private static final String DEFAULT_PRIVATE_KEY= "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALf5tZA/+qqmPNPZ"+
		    		"eu4YPCFtYOWh8826+58Wbn6+tC8bxaEGs3reF/CD42FHY1DG8tFoY2iSVYqniFRl"+
		    		"reubqbIwtoEYpXcES5eXUJJZ/VMElIExPXPLkL8b7fDX+Ot/vv1C1fSBSLFD1h3A"+
		    		"dKElNwET7SqFLsyoX7Llnk1fL58vAgMBAAECgYBo5NDmW/QZlAqeZyM12U9/Z5OV"+
		    		"mc8d/3wzamC5lxW4vkbh1qZCaZqQoUHlVwSDK8uKJdB38Ocg1QBfzlFpQilv3egS"+
		    		"lKcrk8YkgsUmpJcn4omKItB4iGEvYY8bJusLzaQEpRBmeM9plxA76aC20DFEDUR+"+
		    		"9z01rWuhRz5UXk56wQJBANpvUtxj7RnJMK0/0GDGYn5pj2Z6GcNy8VI9ntxYQMRF"+
		    		"xFnvYSfEib2oE4hfG0pHBWvBtDrXDi5XD851bo8eGx8CQQDXnUvTzhvoVSezChMz"+
		    		"1Zvfl8b4TiyTSuZWVuhrlnQqQgAGLOKfdBsz2A0jfDfzktjLbAoVzpPpvVaSI/Qy"+
		    		"nAnxAkEAwGyTPS0WIMIYjHaL1cTN3XiWZ/smGQR3zDAWcxuXqo+fQm7bUpITmSyo"+
		    		"UFkgDFX2U4/nenIavv3ZIdJXW+J0lwJBAK3pLOlJXM84KE5MORLdH93ocU+E1oVz"+
		    		"q3hGny9waoBPPe+9MonEv9BAWtCdeA/aCU2C9luChWHKG1LC90v++jECQETxyI7l"+
		    		"6UDwfky+s4pAhbrvsWdv09d/zsyQe9g899op6XHIchAXAHve1QgLxWOJfWrww924"+
		    		"c21h3y9MuDyTsmY=";
		    /** 
		     * 私钥 
		     */  
		    private RSAPrivateKey privateKey;  
		  
		    /** 
		     * 公钥 
		     */  
		    private RSAPublicKey publicKey; 
		    /** 
		     * 获取私钥 
		     * @return 当前的私钥对象 
		     */  
		    public RSAPrivateKey getPrivateKey() {  
		        return privateKey;  
		    }  
		  
		    /** 
		     * 获取公钥 
		     * @return 当前的公钥对象 
		     */  
		    public RSAPublicKey getPublicKey() {  
		        return publicKey;  
		    }  
		    
	//字符串进行加密算法的名称
	public static final String ALGORITHM = "RSA";
	//字符串进行加密填充的名称
	public static final String PADDING = "RSA/NONE/NoPadding";
	//字符串持有安全提供者的名称
	public static final String PROVIDER = "BC";
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;
    /** 
     * 字节数据转字符串专用集合 
     */  
    private static final char[] HEX_CHAR= {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};  
     
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;
	

public static final String PRIVATE_KEY_FILE = RSAUtil.class.getClassLoader().getResource("").getPath() + "key" + "pri.key";
//公钥文件路径
public static final String PUBLIC_KEY_FILE = RSAUtil.class.getClassLoader().getResource("").getPath() + "key" + "pub.key";
/** 
 * 从字符串中加载公钥 
 * @param publicKeyStr 公钥数据字符串 
 * @throws Exception 加载公钥时产生的异常 
 */  
public void execute() {
	OLVconnection rsaEncrypt= new OLVconnection();  
    //rsaEncrypt.genKeyPair();
	
    //加载公钥  
    try {  
        rsaEncrypt.loadPublicKey(OLVconnection.DEFAULT_PUBLIC_KEY);  
        System.out.println("加载公钥成功");  
    } catch (Exception e) {  
        System.err.println(e.getMessage());  
        System.err.println("加载公钥失败");  
    }  
    //测试字符串  
    List<DataRow> list = aotuZDSHALLService.getAlljiami();
    
    for(int i =0;i<=10000;i++){
    	logger.info(i);
    	DataRow datarow = list.get(i);
    	String xm= datarow.getString("xm");
    	String sr= datarow.getString("sr");
    	String sfzhm= datarow.getString("sfzhm");
    	String qr1= datarow.getString("qr1");
    	String qr2= datarow.getString("qr2");
    	String qr3= datarow.getString("qr3");
    	String czq= datarow.getString("czq");
    	String czdz= datarow.getString("czdz");
    	String zzdz= datarow.getString("zzdz");
    	String zzqy= datarow.getString("zzqy");
    	String jycd= datarow.getString("jycd");
    	String zy= datarow.getString("zy");
    	String zysr= datarow.getString("zysr");
    	String qtsr= datarow.getString("qtsr");
    	String gzgs= datarow.getString("gzgs");
    	String gzsj= datarow.getString("gzsj");
    	String zt= datarow.getString("zt");
    	String zje= datarow.getString("zje");
    	logger.info(qr3);
    	logger.info(czq);
    	logger.info(jycd);
    	logger.info(qr2);
    	logger.info(qtsr);
    	logger.info(gzsj);
    	logger.info(zysr);
    	logger.info(sfzhm);
    	logger.info(zje);
    	logger.info(sr);
    	 try {  
    	        //加密  
    	        byte[] xm1 = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), xm.getBytes());  
    	        byte[] sr1 = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), sr.getBytes());  
    	        byte[] sfzhm1 = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), sfzhm.getBytes());  
    	        byte[] qr11 = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), qr1.getBytes());  
    	        byte[] qr21 = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), qr2.getBytes());  
    	        byte[] qr31 = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), qr3.getBytes());  
    	        byte[] czq1 = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), czq.getBytes());  
    	        byte[] czdz1 = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), czdz.getBytes());  
    	        byte[] zzdz1 = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), zzdz.getBytes());  
    	        byte[] zzqy1 = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), zzqy.getBytes());  
    	        byte[] jycd1 = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), jycd.getBytes());  
    	        byte[] zy1 = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), zy.getBytes());  
    	        byte[] zysr1 = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), zysr.getBytes());  
    	        byte[] qtsr1 = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), qtsr.getBytes());  
    	        byte[] gzgs1 = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), gzgs.getBytes());  
    	        byte[] gzsj1 = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), gzsj.getBytes());  
    	        byte[] zt1 = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), zt.getBytes());  
    	        byte[] zje1 = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), zje.getBytes());
    	        logger.info("333333");
    	        logger.info(OLVconnection.byte2Base64StringFun(xm1));
    	        logger.info(OLVconnection.byte2Base64StringFun(sr1));
    	        logger.info(OLVconnection.byte2Base64StringFun(qr11));
    	        logger.info(OLVconnection.byte2Base64StringFun(qr21));
    	        logger.info(OLVconnection.byte2Base64StringFun(czq1));
    	        logger.info(OLVconnection.byte2Base64StringFun(zzdz1));
    	        logger.info(OLVconnection.byte2Base64StringFun(zy1));
    	        logger.info(OLVconnection.byte2Base64StringFun(gzgs1));
    	        logger.info(OLVconnection.byte2Base64StringFun(zje1));
    	        
    	        DataRow row = new DataRow();
    	        row.set("id",i);
    	        row.set("xm",OLVconnection.byte2Base64StringFun(xm1));
    	        row.set("sr",OLVconnection.byte2Base64StringFun(sr1));
    	        row.set("sfzhm",OLVconnection.byte2Base64StringFun(sfzhm1));
    	        row.set("qr1",OLVconnection.byte2Base64StringFun(qr11));
    	        row.set("qr2",OLVconnection.byte2Base64StringFun(qr21));
    	        row.set("qr3",OLVconnection.byte2Base64StringFun(qr31));
    	        row.set("czq",OLVconnection.byte2Base64StringFun(czq1));
    	        row.set("czdz",OLVconnection.byte2Base64StringFun(czdz1));
    	        row.set("zzdz",OLVconnection.byte2Base64StringFun(zzdz1));
    	        row.set("zzqy",OLVconnection.byte2Base64StringFun(zzqy1));
    	        row.set("jycd",OLVconnection.byte2Base64StringFun(jycd1));
    	        row.set("zy",OLVconnection.byte2Base64StringFun(zy1));
    	        row.set("zysr",OLVconnection.byte2Base64StringFun(zysr1));
    	        row.set("qtsr",OLVconnection.byte2Base64StringFun(qtsr1));
    	        row.set("gzgs",OLVconnection.byte2Base64StringFun(gzgs1));
    	        row.set("gzsj",OLVconnection.byte2Base64StringFun(gzsj1));
    	        row.set("zt",OLVconnection.byte2Base64StringFun(zt1));
    	        row.set("zje",OLVconnection.byte2Base64StringFun(zje1));
    	        aotuZDSHALLService.insertUserjiami(row);
    	        logger.info(i);
    	         
    	    } catch (Exception e) {  
    	        System.err.println(e.getMessage());  
    	    } 
    }
}
public void loadPublicKey(String publicKeyStr) throws Exception{  
    try {  
        BASE64Decoder base64Decoder= new BASE64Decoder();  
        byte[] buffer= base64Decoder.decodeBuffer(publicKeyStr);  
        KeyFactory keyFactory= KeyFactory.getInstance("RSA");  
        X509EncodedKeySpec keySpec= new X509EncodedKeySpec(buffer);  
        this.publicKey= (RSAPublicKey) keyFactory.generatePublic(keySpec);  
    } catch (NoSuchAlgorithmException e) {  
        throw new Exception("无此算法");  
    } catch (InvalidKeySpecException e) {  
        throw new Exception("公钥非法");  
    } catch (IOException e) {  
        throw new Exception("公钥数据内容读取错误");  
    } catch (NullPointerException e) {  
        throw new Exception("公钥数据为空");  
    }  
}  
public void loadPrivateKey(String privateKeyStr) throws Exception{  
    try {  
        BASE64Decoder base64Decoder= new BASE64Decoder();  
        byte[] buffer= base64Decoder.decodeBuffer(privateKeyStr);  
        PKCS8EncodedKeySpec keySpec= new PKCS8EncodedKeySpec(buffer);  
        KeyFactory keyFactory= KeyFactory.getInstance("RSA");  
        this.privateKey= (RSAPrivateKey) keyFactory.generatePrivate(keySpec);  
    } catch (NoSuchAlgorithmException e) {  
        throw new Exception("无此算法");  
    } catch (InvalidKeySpecException e) {  
        throw new Exception("私钥非法");  
    } catch (IOException e) {  
        throw new Exception("私钥数据内容读取错误");  
    } catch (NullPointerException e) {  
        throw new Exception("私钥数据为空");  
    }  
}  
/** 
 * 加密过程 
 * @param publicKey 公钥 
 * @param plainTextData 明文数据 
 * @return 
 * @throws Exception 加密过程中的异常信息 
 */  
public byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData) throws Exception{  
    if(publicKey== null){  
        throw new Exception("加密公钥为空, 请设置");  
    }  
    Cipher cipher= null;  
    try {  
        cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());  
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);  
        byte[] output= cipher.doFinal(plainTextData);  
        return output;  
    } catch (NoSuchAlgorithmException e) {  
        throw new Exception("无此加密算法");  
    } catch (NoSuchPaddingException e) {  
        e.printStackTrace();  
        return null;  
    }catch (InvalidKeyException e) {  
        throw new Exception("加密公钥非法,请检查");  
    } catch (IllegalBlockSizeException e) {  
        throw new Exception("明文长度非法");  
    } catch (BadPaddingException e) {  
        throw new Exception("明文数据已损坏");  
    }  
}  

/** 
 * 解密过程 
 * @param privateKey 私钥 
 * @param cipherData 密文数据 
 * @return 明文 
 * @throws Exception 解密过程中的异常信息 
 */  
public byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData) throws Exception{  
    if (privateKey== null){  
        throw new Exception("解密私钥为空, 请设置");  
    }  
    Cipher cipher= null;  
    try {  
        cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());  
        cipher.init(Cipher.DECRYPT_MODE, privateKey);  
        byte[] output= cipher.doFinal(cipherData);  
        return output;  
       
    } catch (NoSuchAlgorithmException e) {  
        throw new Exception("无此解密算法");  
    } catch (NoSuchPaddingException e) {  
        e.printStackTrace();  
        return null;  
    }catch (InvalidKeyException e) {  
        throw new Exception("解密私钥非法,请检查");  
    } catch (IllegalBlockSizeException e) {  
        throw new Exception("密文长度非法");  
    } catch (BadPaddingException e) {  
        throw new Exception("密文数据已损坏");  
    }         
}  
//base64字符串转byte[]    
public static byte[] base64String2ByteFun(String base64Str){    
    return Base64.decodeBase64(base64Str);    
}    
//byte[]转base64    
public static String byte2Base64StringFun(byte[] b){    
    return Base64.encodeBase64String(b);    
}
private  com.alibaba.fastjson.JSONObject getRequestJson(HttpServletRequest request) throws IOException {
	  InputStream in = request.getInputStream();
	  byte[] b = new byte[10240];
	  int len;
	  ByteArrayOutputStream baos = new ByteArrayOutputStream();
	  while ((len = in.read(b)) > 0) {
	      baos.write(b, 0, len);
	  }
	  String bodyText1 = new String(baos.toByteArray(), "UTF-8");
	  bodyText1 = bodyText1.replaceAll("%(?![0-9a-fA-F]{2})", "%25");  
	  String bodyText = URLDecoder.decode(bodyText1,"UTF-8");
	  
	  com.alibaba.fastjson.JSONObject json =  (com.alibaba.fastjson.JSONObject) JSON.parse(bodyText);
	  if (true) {
		  logger.info("received notify message:");
		  logger.info(JSON.toJSONString(json, true));
	  }
	  return json;
}
/** 
 * 字节数据转十六进制字符串 
 * @param data 输入数据 
 * @return 十六进制内容 
 */  
public static String byteArrayToString(byte[] data){  
    StringBuilder stringBuilder= new StringBuilder();  
    for (int i=0; i<data.length; i++){  
        //取出字节的高四位 作为索引得到相应的十六进制标识符 注意无符号右移  
        stringBuilder.append(HEX_CHAR[(data[i] & 0xf0)>>> 4]);  
        //取出字节的低四位 作为索引得到相应的十六进制标识符  
        stringBuilder.append(HEX_CHAR[(data[i] & 0x0f)]);  
        if (i<data.length-1){  
            stringBuilder.append(' ');  
        }  
    }  
    return stringBuilder.toString();  
}  

}
