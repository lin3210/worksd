package root.current;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import net.sf.json.JSONObject;
import root.PostHttp;
import root.order.UserMoneyBase;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import sun.misc.BASE64Decoder;

import com.shove.security.Encrypt;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fuiou.util.MD5;
import com.project.service.account.JBDcmsService;
import com.project.service.account.OLVService;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.util.StringHelper;
import com.thinkive.base.util.security.RSAUtil;
import com.thinkive.web.base.ActionResult;
import com.thinkive.web.base.BaseAction;

public class Viettelpost extends BaseAction {
    private static Logger logger = Logger.getLogger(Viettelpost.class);
    /*private static UserService userService = new UserService();*/
    private static OLVService olvservice = new OLVService();
	private static JBDcmsService jbdcmsService = new JBDcmsService();
	private static UserMoneyBase  userMoneyBase = new UserMoneyBase();
    private static String pwd1 = "mofa168viettelpost";
    @SuppressWarnings("unused")
	private static String url = "http";
    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
    private static final String SHA_PUBLIC_KEY= "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgJZI5SNDfe/"
    		+ "tpBf2RsgP5ejZakkmkq6y7dTgjr9BScemkGufUQThNXhUwwoEcKHLvSXfPI0Sn4N1/8oor7hUnXrmGjyxOdl6HXY6"
    		+ "1tOgXNaB1PWLI5EFJnbHHMNlByy8l/bsZBrftynzImkxbB/3YipujofsXHNVQQNpjD054Vae4i4/617NVmnJ8ZALi"
    		+ "HG7WE+H/V+bP2Bk0jPSdtmBmpVDnix+tEHMpSWjziHcSEbYfC1HBtRiqr+lYEpNFMuYjqDWLNbMQVOOmdn02Ob5uu"
    		+ "WmYtv2NzApnu9E31DWNcckATGQmqCBzhfFMzLyLML2L3+kDDFom7RVmQVjO0C9+wIDAQAB";
    private static final String DEFAULT_PUBLIC_KEY= "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxpJW/IijTjtys2q5Z22y" + 
    		"pO/o+OBGasoNIzuVlzNI9vy0AQzs/tnDpwQUXYTm10Yr0uAerBeln268uBiH7cR/" + 
    		"obp6TIXuCg/aRNKfhGhuL1g7ztFfTdcKhvzz8SP3wfWsnePaH0TC/1+rzYm922gy" + 
    		"52IlFv8Q5ZFWDyflpU25fK85bIRaOZap78TOpBz9FinYFifgvRYHyc4VL4q0jERg" + 
    		"980LDnRSrZiWvfSmMdvfrNfrjOr2rFo/YY0SEAsv1beIM7wpbKb8TLDRg24rDuWZ" + 
    		"NiIKqw8DEKLE3uonoYrRlDy9n/9zy3w+s2ydiQx9i7V3uDNjpUctG/9IHOdyNfT/" + 
    		"nwIDAQAB";
            private static final String DEFAULT_PRIVATE_KEY= "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDGklb8iKNOO3Kz" + 
            		"arlnbbKk7+j44EZqyg0jO5WXM0j2/LQBDOz+2cOnBBRdhObXRivS4B6sF6Wfbry4" + 
            		"GIftxH+hunpMhe4KD9pE0p+EaG4vWDvO0V9N1wqG/PPxI/fB9ayd49ofRML/X6vN" + 
            		"ib3baDLnYiUW/xDlkVYPJ+WlTbl8rzlshFo5lqnvxM6kHP0WKdgWJ+C9FgfJzhUv" + 
            		"irSMRGD3zQsOdFKtmJa99KYx29+s1+uM6vasWj9hjRIQCy/Vt4gzvClspvxMsNGD" + 
            		"bisO5Zk2IgqrDwMQosTe6iehitGUPL2f/3PLfD6zbJ2JDH2LtXe4M2OlRy0b/0gc" + 
            		"53I19P+fAgMBAAECggEAJA8dBrzVUvaOrLAbvQF5qGP8nX00rF3GpvhCAPQKMvfO" + 
            		"xEYRn3HyCpk+DYF93efbO28BguTEacBVOuURggbl2nufi9fiUydvzoGlAAlqwOQ3" + 
            		"8CWrNdpwCfVTYx3R9wHi98URrg3xjyhTbX6u4HH6lHQJIjTtX5LV2ui21h7YKSDi" + 
            		"0IATK/d3nZUGK6xGrigNUKYdVsvQSZepP9frkSFQUDuinnQF8pN3i2tsbFahJsAV" + 
            		"Gef3VuDWivYjtNDR5kUjJD7tLrXZ5/ydbwDFZIFiLDkZApJeCyNNz9NE7IVcke6n" + 
            		"2TaUQ8BiDYWe0P9RpCL32yctX+V2f3+Pr0+K9k9AEQKBgQDpS3otGTcaYPlw4GAp" + 
            		"niu1yejy40a/JK9d9jgTwkdqK+6NGnXjgskIcBlPFGgSLP+PA6JQHPLJdJT9a11D" + 
            		"ri0NOWTwXjf/5fNPrHT0RZ91wdpHTHze4bN8KmGvPjk9V7KQVPqo46mt6EceEfN9" + 
            		"/qsnomBbMhuBjMwhbVs2Yfc/+QKBgQDZ5byjDDm19Biz8YDCo1I7JjSaq6TtXa8I" + 
            		"+cE0zcFOGNB4W7+HLpD09MJ/qwOHABZm3Jf6J4vTi4KujdLXVH9qO/f8QM+kZ3Xe" + 
            		"AA4BpgtKuEqpf8EACMnguB+nTroIlQJBQgScOFLRMuvtgE0l8Bsc6JCeKdgBlPaj" + 
            		"JmPaIRTSVwKBgCdVzJHSEJJoC2bWzh/2RVRvaNGbNJCB9PeYzje2qCfkpGIyNfJY" + 
            		"WOENVZcZz7pQz/4jFLkBSIZjG8Pm2GDS+1Ghfb1xhWumL/IdF9Mf8hT+zHj9EojZ" + 
            		"8VNcKgBMM4Z+RONFEMZtLGzgHy2YxT9XMzCJlkmKF00umjSApcJzniaxAoGADkDf" + 
            		"8Q54q/VWtFak5JaIZ4QdRszZkglhzMWBpPGcpn4rDOR7h9088Dkei/lk1qzjykC9" + 
            		"1EhH9kOW/dIbK6jEWO+pTh/zNT7rT+VTBTqWXIHnqGTJ/DWW9xM0Rt54ft04c4pd" + 
            		"ANcSW8rn8fZFlai6LU7tPttf3OKgq5V0TK2DTisCgYEAuSVuBVO9o5sEas+vOT/p" + 
            		"aszCQgzKvs3btUy1WbgeSObRsWmJ1yisoASSvUvQldX+WFzDDd/Ki9LOUSiCnk5J" + 
            		"IncS6e3q8Tn3TviZlDjlSsicY/NJWq0gDUPxAdVhL/vAbXba0K3XUml9pc8Ea3Oy" + 
            		"vVo+DXu2kiRbP0Ni8rnr3VY=";
            /** 
             * 私钥 
             */  
            private static RSAPrivateKey privateKey;  
          
            /** 
             * 公钥 
             */  
            private static RSAPublicKey publicKey; 
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
    @SuppressWarnings("unused")
	private static final int MAX_ENCRYPT_BLOCK = 117;
    /** 
     * 字节数据转字符串专用集合 
     */  
    private static final char[] HEX_CHAR= {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};  
     
    /**
     * RSA最大解密密文大小
     */
    @SuppressWarnings("unused")
	private static final int MAX_DECRYPT_BLOCK = 128;
    

public static final String PRIVATE_KEY_FILE = RSAUtil.class.getClassLoader().getResource("").getPath() + "key" + "pri.key";
//公钥文件路径
public static final String PUBLIC_KEY_FILE = RSAUtil.class.getClassLoader().getResource("").getPath() + "key" + "pub.key";
/** 
 * 从字符串中加载公钥 
 * @param publicKeyStr 公钥数据字符串 
 * @throws Exception 加载公钥时产生的异常 
 */  
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
      String bodyText1111 = new String(baos.toByteArray(),"GBK");
      logger.info(bodyText1111);
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

/**
* 获得客户的真实IP地址
*
* @return
*/
public String getipAddr( )
{
    String ip = getRequest().getHeader("X-Real-IP");
    if(StringHelper.isEmpty(ip))
    {
        ip = olvservice.getRemortIP(getRequest());
    }
    return ip;
}

    public ActionResult doGetCustomerDF() throws Exception
    {   
        JSONObject jsonObject = new JSONObject();
        Viettelpost rsaEncrypt= new Viettelpost();  
        //加载私钥  
        try {  
            rsaEncrypt.loadPrivateKey(Viettelpost.DEFAULT_PRIVATE_KEY);  
            System.out.println("加载私钥成功");  
        } catch (Exception e) {  
            System.err.println(e.getMessage());  
            System.err.println("加载私钥失败");  
        }  
        HttpServletRequest request = getRequest();
        String headerToken = request.getHeader("X-TOKEN");
        if(!pwd1.equals(headerToken)) {
        	jsonObject.put("TRANS_STATUS", -2);
            jsonObject.put("TRANS_MSG", "Invalid partner code or do not transmit partner code");//失败
            this.getWriter().write(jsonObject.toString());
            return null;
        }
        String headerSigned = request.getHeader("X-SIGNED");
        // String jiami = headerSigned;
        // byte[] jiamiwen = Viettelpost.base64String2ByteFun(jiami);
        // byte[] resjiami = rsaEncrypt.decrypt(rsaEncrypt.privateKey, jiamiwen);
        // String resjiamiwen = new String(resjiami,"UTF-8");
        String cmnd= getStrParameter("id");
        String miwen = Encrypt.MD5(cmnd);
        //yan qian
        Boolean yanqianBoolean = rsaValidate(SHA_PUBLIC_KEY,miwen,headerSigned);
        if(!yanqianBoolean) {
        	jsonObject.put("TRANS_STATUS", -1);
            jsonObject.put("TRANS_MSG", "Invalid signature or do not transmit signature");//失败
            this.getWriter().write(jsonObject.toString());
            return null;
        }
        int count = olvservice.getcount(cmnd);
        if(count == 0){
            jsonObject.put("TRANS_STATUS", 2);
            jsonObject.put("TRANS_MSG", "Transmission is wrong compared to the original TRANS_MSG of both parties");//失败
            this.getWriter().write(jsonObject.toString());
            return null;
        }
        String userid = olvservice.getID(cmnd);
        String jkid = olvservice.getJKID(userid);
        String name = olvservice.getName(cmnd);
        int countjk = olvservice.getcountjk(userid);
        if(countjk == 0){
            jsonObject.put("TRANS_STATUS", 4);
            jsonObject.put("TRANS_MSG", "The transaction does not exist or has been successfully paid");//失败
            this.getWriter().write(jsonObject.toString());
            return null;
        }
        //int jkdate = olvservice.getJKDATE(userid);
        String yqcs = olvservice.getYQCS(userid);
        String benjin = olvservice.getMoney(userid).replace(",", "");
        String yuqi = olvservice.getYuqi(userid).replace(",", "");
        int sjsh = Integer.parseInt(benjin);

		DataRow dataRow2 = jbdcmsService.getUserInfo(Integer.parseInt(userid));
		String name1 = dataRow2.getString("username").substring(0, 4);
		String mobilephone = dataRow2.getString("mobilephone");
        int yqlx = Integer.parseInt(yuqi);
        
        int sum = sjsh+yqlx;
        //int yq7 = sjsh * userMoneyBase.getUMBaseCalculateProductInterest(sjsh,3, Integer.parseInt(userid), name1)/100+yqlx;
        //int yq14 = sjsh * userMoneyBase.getUMBaseCalculateProductInterest(sjsh, 4, Integer.parseInt(userid), name1)/100+yqlx ;
        int yq15 = sjsh * userMoneyBase.getUMBaseCalculateProductInterest(sjsh,1, Integer.parseInt(userid), name1)/100+yqlx;
        int yq30 = sjsh * userMoneyBase.getUMBaseCalculateProductInterest(sjsh,2, Integer.parseInt(userid), name1)/100+yqlx;
       
        JSONObject jsonObjectInfo = new JSONObject();
        jsonObjectInfo.put("CODE", name1+mobilephone);
        jsonObjectInfo.put("PHONE", mobilephone);
        jsonObjectInfo.put("DISPLAYNAME", name);
        
        JSONArray EXTENDS_INFO = new JSONArray();
        //quan e huan kuan 
        JSONObject quane = new JSONObject();
        quane.put("TRANS_ID", "1VTP"+cmnd);
        quane.put("AMOUNT", sum);
        quane.put("CONTENT", "Tra toan bo");
        quane.put("TYPE", 1);
        quane.put("EXTENDS_INFO", EXTENDS_INFO);
        //bufen
        JSONObject bufen = new JSONObject();
        bufen.put("TRANS_ID", "2VTP"+cmnd);
        bufen.put("AMOUNT", sum);
        bufen.put("CONTENT", "Tra mot phan");
        bufen.put("TYPE", 2);
        bufen.put("EXTENDS_INFO", EXTENDS_INFO);
        //yan qi 15tian
        JSONObject yanqi15 = new JSONObject();
        yanqi15.put("TRANS_ID", "3VTP"+cmnd);
        yanqi15.put("AMOUNT", yq15);
        yanqi15.put("CONTENT", "Gia han 15 ngay");
        yanqi15.put("TYPE", 1);
        yanqi15.put("EXTENDS_INFO", EXTENDS_INFO);
        //yan qi 15tian
        JSONObject yanqi30 = new JSONObject();
        yanqi30.put("TRANS_ID", "4VTP"+cmnd);
        yanqi30.put("AMOUNT", yq30);
        yanqi30.put("CONTENT", "Gia han 30 ngay");
        yanqi30.put("TYPE", 1);
        yanqi30.put("EXTENDS_INFO", EXTENDS_INFO);
        
        JSONArray jsonObjectTran = new JSONArray();
        jsonObjectTran.add(quane);
        jsonObjectTran.add(bufen);
        jsonObjectTran.add(yanqi15);
        jsonObjectTran.add(yanqi30);
        /*if(TextUtils.isEmpty(yqcs)){
        	jsonObjectTran.add(yanqi15);
            jsonObjectTran.add(yanqi30);
        }else{
            if(yqcs.length()<=2){
            	jsonObjectTran.add(yanqi15);
                jsonObjectTran.add(yanqi30);
            }
        }*/
        
        JSONObject jsonObjectData = new JSONObject();
        jsonObjectData.put("QUERY_CODE", cmnd);
        jsonObjectData.put("CUSTOMER_INFO", jsonObjectInfo);
        jsonObjectData.put("EXTENDS_INFO", "");
        jsonObjectData.put("LIST_TRANS", jsonObjectTran);
        
        jsonObject.put("TRANS_STATUS", 0);//成功
        jsonObject.put("DATA", jsonObjectData);
        jsonObject.put("TRANS_MSG", "Transaction successful");//成功
        this.getWriter().write(jsonObject.toString());
        return null;
    }
    public ActionResult doGetConfirmVay() throws Exception
    {   
        
        Viettelpost rsaEncrypt= new Viettelpost();  
  
        //加载私钥  
        try {  
            rsaEncrypt.loadPrivateKey(Viettelpost.DEFAULT_PRIVATE_KEY);  
            System.out.println("加载私钥成功");  
        } catch (Exception e) {  
            System.err.println(e.getMessage());  
            System.err.println("加载私钥失败");  
        }  
        JSONObject jsonObject = new JSONObject();
        HttpServletRequest request = getRequest();
        String headerToken = request.getHeader("X-TOKEN");
        if(!pwd1.equals(headerToken)) {
        	jsonObject.put("TRANS_STATUS", -2);
            jsonObject.put("TRANS_MSG", "Invalid partner code or do not transmit partner code");//失败
            this.getWriter().write(jsonObject.toString());
            return null;
        }
        String headerSigned = request.getHeader("X-SIGNED");
        com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
        //String jiami = jsonString.getString("data");
        //byte[] jiamiwen = Viettelpost.base64String2ByteFun(jiami);
        //byte[] resjiami = rsaEncrypt.decrypt(rsaEncrypt.privateKey, jiamiwen);
        //String resjiamiwen = new String(resjiami,"UTF-8");
        //com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSONObject.parseObject(resjiamiwen);
        logger.info(jsonString);
        String TRANS_ID = jsonString.getString("TRANS_ID");
        String REF_ID = jsonString.getString("REF_ID");
        String CUSTOMER_CODE = jsonString.getString("CUSTOMER_CODE");
        int AMOUNT = jsonString.getInteger("AMOUNT");
        int FEE = jsonString.getInteger("FEE");
        String CONTENT = jsonString.getString("CONTENT");
        String AGENT_USER = jsonString.getString("AGENT_USER");
        String AGENT_NAME = jsonString.getString("AGENT_NAME");
        String TRANS_DATE = jsonString.getString("TRANS_DATE");
        String signString = "TRANS_ID||"+TRANS_ID+"||REF_ID||"+REF_ID+"||CUSTOMER_CODE||"+CUSTOMER_CODE+"||AMOUNT||"+AMOUNT
        		+"||FEE||"+FEE+"||CONTENT||"+CONTENT+"||AGENT_USER||"+AGENT_USER+"||AGENT_NAME||"+AGENT_NAME+"||TRANS_DATE||"+TRANS_DATE;
        logger.info(signString);
        String miwen = Encrypt.MD5(signString);
        logger.info(miwen);
        //yan qian
        Boolean yanqianBoolean = rsaValidate(SHA_PUBLIC_KEY,miwen,headerSigned);
        if(!yanqianBoolean) {
        	jsonObject.put("TRANS_STATUS", -1);
            jsonObject.put("TRANS_MSG", "Invalid signature or do not transmit signature");//失败
            this.getWriter().write(jsonObject.toString());
            return null;
        }
        
        String paynum= TRANS_ID;
        String cmnd= REF_ID.substring(REF_ID.indexOf("P")+1);
        int state= Integer.parseInt(REF_ID.substring(0,1));
        int money= AMOUNT;
        DecimalFormat df = new DecimalFormat("###,###");
        Calendar calendar =Calendar.getInstance();
        SimpleDateFormat fmtrq  = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        int count = olvservice.getcount(cmnd);
        if(count == 0){
        	jsonObject.put("TRANS_STATUS", 2);
            jsonObject.put("TRANS_MSG", "Transmission is wrong compared to the original TRANS_MSG of both parties");//失败
            this.getWriter().write(jsonObject.toString());
            return null;
        }
        int countpay = olvservice.getcountpay(paynum);
        if(countpay == 1){
            jsonObject.put("TRANS_STATUS", 3);
            jsonObject.put("TRANS_MSG", "Overlapping transaction");//失败
            this.getWriter().write(jsonObject.toString());
            return null;
        }
        String userid = olvservice.getID(cmnd);
        String jkid = olvservice.getJKID(userid);
        int countjk = olvservice.getcountjk(userid);
        if(countjk == 0){
        	jsonObject.put("TRANS_STATUS", 4);
            jsonObject.put("TRANS_MSG", "The transaction does not exist or has been successfully paid");//失败
            this.getWriter().write(jsonObject.toString());
            return null;
        }
        String benjin = olvservice.getMoney(userid).replace(",", "");
        String yuqi = olvservice.getYuqi(userid).replace(",", "");
        String yuq_yhlx = olvservice.getYQYH(jkid).replace(",", "");
        int sjsh = Integer.parseInt(benjin);
        int yqlx = Integer.parseInt(yuqi);
        int yqyhlx = Integer.parseInt(yuq_yhlx);
        int yqts = olvservice.getyqts(jkid);
        int sum = sjsh+yqlx;
        DataRow dataRowjk = olvservice.getUserJKInfo(jkid);
        String cuishouid = dataRowjk.getString("cuishou_id");
        Date dt= new Date();
        Long time= dt.getTime();//这就是距离1970年1月1日0点0分0秒的毫秒数
        String payOrder="viettel"+time;
        
        DataRow addorder = new DataRow();
        addorder.set("userid", userid);
        addorder.set("rechargetime", fmtrq.format(calendar.getTime()));
        addorder.set("userhktime", fmtrq.format(calendar.getTime()));
        addorder.set("bankjztime", fmtrq.format(calendar.getTime()));
        addorder.set("rechargetype", 88);
        addorder.set("rechargemoney",df.format(money));
        addorder.set("hongbaoid",df.format(FEE));
        addorder.set("buyerEmail",AGENT_NAME);
        addorder.set("rechargeid",AGENT_USER);
        addorder.set("dqyqts", yqts);
        addorder.set("dqyqlx", yqlx);
        addorder.set("cuishouid", cuishouid);
        addorder.set("result", 1);
        addorder.set("operatorId", 1000);
        addorder.set("rzcode", "0000");
        addorder.set("paynumber", payOrder);
        addorder.set("refid", REF_ID);
        addorder.set("rechargenumber", jkid);    
        addorder.set("paynum", paynum);
        addorder.set("orderpaytype", 1);   //线上还款为1        
        addorder.set("bankorderid", paynum); 
        
        if(state == 1){
        	if(sum != money) {
        		jsonObject.put("TRANS_STATUS", 2);
                jsonObject.put("TRANS_MSG", "Số tiền nhận được không bằng với số tiền khách cần trả");//失败   收款金额不等于用户的需要还款金额
                this.getWriter().write(jsonObject.toString());
                return null;
        	}
        	String remark = "viettel全额还款";
            addorder.set("remark", remark);
            addorder.set("ordertype", 881);
            addorder.set("resultInfo", "viettel Trả nợ đầy đủ");
            DataRow row = new DataRow();
            row.set("id", jkid);
            row.set("jksfwc", 1);
            row.set("sfyhw", 1);
            row.set("hk_time", fmtrq.format(calendar.getTime()));
            olvservice.updateHK(row);
            
        }else if(state == 2){
        	if(sum == money) {
        		String remark = "viettel全额还款";
                addorder.set("remark", remark);
                addorder.set("ordertype", 881);
                addorder.set("resultInfo", "viettel Trả nợ đầy đủ");
                DataRow row = new DataRow();
                row.set("id", jkid);
                row.set("jksfwc", 1);
                row.set("sfyhw", 1);
                row.set("hk_time", fmtrq.format(calendar.getTime()));
                olvservice.updateHK(row);
        	}else {
        		if(sum < money) {
            		jsonObject.put("TRANS_STATUS", 2);
                    jsonObject.put("TRANS_MSG", "Số tiền nhận được lớn hơn số tiền khách cần trả");//失败  收款金额大于用户的需要还款金额
                    this.getWriter().write(jsonObject.toString());
                    return null;
            	}
            	addorder.set("ordertype", 882);
                if(yqlx>money){
    	        	 String remark = "viettel部分逾期利息还款";
    	             addorder.set("remark", remark);
    	             addorder.set("resultInfo", "viettel Trả nợ một phần của sự quan tâm quá hạn");
    	             
                     DataRow row1 = new DataRow();
                     row1.set("id", jkid);
                     row1.set("yuq_lx", df.format(yqlx - money));
                     row1.set("hk_time", fmtrq.format(calendar.getTime()));
                     olvservice.updateHK(row1);
                }else{
                	String remark = "viettel部分本金还款";
                    addorder.set("remark", remark);
                    addorder.set("resultInfo", "viettel Một phần của số lượng trả nợ");   
                
                    DataRow row1 = new DataRow();
                    row1.set("id", jkid);
                    row1.set("yuq_lx", 0);
                    row1.set("sjsh_money", df.format(sjsh-(money-yqlx)));
                    row1.set("hk_time", fmtrq.format(calendar.getTime()));
                    olvservice.updateHK(row1);
                }
        	}
        }else if(state == 3 || state == 4){
            String remark = "viettel延期还款"; 
            addorder.set("remark", remark);
            addorder.set("ordertype", 884);
            addorder.set("yqdate", state-2);
            addorder.set("resultInfo", "viettel Gia hạn trả vay");
            
            int yqyqlx = 0;
            DataRow dataRow2 = jbdcmsService.getUserInfo(Integer.parseInt(userid));
    		String name1 = dataRow2.getString("username").substring(0, 4);
            if (state == 3) {
                yqyqlx = sjsh * userMoneyBase.getUMBaseCalculateProductInterest(sjsh,1, Integer.parseInt(userid), name1) / 100;
            } else if (state == 4) {
                yqyqlx = sjsh * userMoneyBase.getUMBaseCalculateProductInterest(sjsh,2, Integer.parseInt(userid), name1) / 100;
            } 
            if((yqlx+yqyqlx) != money) {
        		jsonObject.put("TRANS_STATUS", 2);
                jsonObject.put("TRANS_MSG", "Số tiền nhận được không bằng với phí khách hàng gia hạn");//失败  收款金额不等于用户的延期费用
                this.getWriter().write(jsonObject.toString());
                return null;
        	}
            DataRow jkxm2 = new DataRow();
            int tianshu = 0;
            String hkyqtime = dataRowjk.getString("hkyq_time");
            String hkfqtime = dataRowjk.getString("hkfq_time");
            String hkfqcishu = dataRowjk.getString("hkfq_cishu");
            String hkfqlx = dataRowjk.getString("hkfq_lx");
            String hkfqzlx = dataRowjk.getString("hkfqzlx");
            int fqlx = 0;
            int fqzlx = 0;
            if (!"0".equals(hkfqlx)) {
                String aa = hkfqlx.replace(",", "");
                String bb = hkfqzlx.replace(",", "");
                fqlx = Integer.parseInt(aa);
                fqzlx = Integer.parseInt(bb);
            }
            if (state == 3) {
                tianshu = 15 + yqts;
            } else if (state == 4) {
                tianshu = 30 + yqts;
            } 
            Date time1 = new Date();
            if (!StringHelper.isEmpty(hkfqtime)) {
                time1 = fmtrq.parse(hkfqtime);
            } else {
                time1 = fmtrq.parse(hkyqtime);
            }
            Calendar cl = Calendar.getInstance();
            cl.setTime(time1);
            cl.add(Calendar.DATE, tianshu);
            String fenqitime = "";
            fenqitime = fmtrq.format(cl.getTime());
            jkxm2.set("id", jkid);
            if (yqts <= 15) {
                jkxm2.set("cuishou_id", 0);
            }
            jkxm2.set("yuq_yhlx", df.format(yqlx+yqyhlx));
            jkxm2.set("yuq_lx", 0);
            jkxm2.set("yuq_ts", 0);
            jkxm2.set("hkfq_code", 1);
            jkxm2.set("hkfq_time", fenqitime);
            jkxm2.set("hkfq_day", tianshu);
            jkxm2.set("hkfqzlx", df.format(fqzlx + yqyqlx));
            jkxm2.set("hkfq_lx", df.format(fqlx + money));
            jkxm2.set("hkfq_cishu", hkfqcishu + (state-2));
            jkxm2.set("hk_time", fmtrq.format(calendar.getTime()));
            jkxm2.set("hkqd", 0);
            jkxm2.set("tzjx_ts", 0);
            jkxm2.set("tzjx_lx", 0);
            jkxm2.set("tzjx", 0);
            olvservice.updateJKHK(jkxm2);
            
        }
        olvservice.addOrder(addorder);
        
        JSONObject jsonObjectData = new JSONObject();
        jsonObjectData.put("TRANS_ID", TRANS_ID);
        jsonObjectData.put("REF_ID",REF_ID);
        jsonObjectData.put("TRANS_STATUS", 0);
        jsonObjectData.put("TRANS_DATE", time);
        jsonObjectData.put("TRANS_MSG", "OK");
        
        String viery = "TRANS_ID||"+TRANS_ID+"||REF_ID||"+REF_ID+"||TRANS_STATUS||0||TRANS_DATE||"+time+"||TRANS_MSG||OK";
        String md5viery = Encrypt.MD5(viery);
        String singed = rsaSign(DEFAULT_PRIVATE_KEY, md5viery);
        jsonObject.put("DATA", jsonObjectData);
        jsonObject.put("SIGNED", singed);//成功
        this.getWriter().write(jsonObject.toString());
        return null;
    } 
    public ActionResult doGetTransId() throws Exception
	{   
	    JSONObject jsonObject = new JSONObject();
	    HttpServletRequest request = getRequest();
	    String headerToken = request.getHeader("X-TOKEN");
	    if(!pwd1.equals(headerToken)) {
	    	jsonObject.put("TRANS_STATUS", -2);
	        jsonObject.put("TRANS_MSG", "Invalid partner code or do not transmit partner code");//失败
	        this.getWriter().write(jsonObject.toString());
	        return null;
	    }
	    String headerSigned = request.getHeader("X-SIGNED");
	    String TRANS_ID= getStrParameter("tid");
	    String miwen = Encrypt.MD5(TRANS_ID);
	    logger.info(miwen);
	    //yan qian
	    Boolean yanqianBoolean = rsaValidate(SHA_PUBLIC_KEY,miwen,headerSigned);
	    if(!yanqianBoolean) {
	    	jsonObject.put("TRANS_STATUS", -1);
	        jsonObject.put("TRANS_MSG", "Invalid signature or do not transmit signature");//失败
	        this.getWriter().write(jsonObject.toString());
	        return null;
	    }
	    
	    String paynum= TRANS_ID;
	    int countpay = olvservice.getcountpay(paynum);
	    if(countpay == 1){
	    	String REF_ID = olvservice.getrefid(paynum);
	    	Date dt= new Date();
		    Long time= dt.getTime();//这就是距离1970年1月1日0点0分0秒的毫秒数
		    JSONObject jsonObjectData = new JSONObject();
		    jsonObjectData.put("TRANS_ID", TRANS_ID);
		    jsonObjectData.put("REF_ID",REF_ID);
		    jsonObjectData.put("TRANS_STATUS", 0);
		    jsonObjectData.put("TRANS_DATE", time);
		    jsonObjectData.put("TRANS_MSG", "OK");
		    
		    String viery = "TRANS_ID||"+TRANS_ID+"||REF_ID||"+REF_ID+"||TRANS_STATUS||0||TRANS_DATE||"+time+"||TRANS_MSG||OK";
		    String md5viery = Encrypt.MD5(viery);
		    String singed = rsaSign(DEFAULT_PRIVATE_KEY, md5viery);
		    jsonObject.put("DATA", jsonObjectData);
		    jsonObject.put("SIGNED", singed);//成功
		    this.getWriter().write(jsonObject.toString());
		    return null;
	    }else {
	    	jsonObject.put("TRANS_STATUS", 6);
	        jsonObject.put("TRANS_MSG", "Transactions fail And Please do it again");//失败
	        this.getWriter().write(jsonObject.toString());
	        return null;
	    }
	    
	}

	public static String rsaSign(String key, String data) {
        try {
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(DatatypeConverter.parseBase64Binary(key));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey bobPrivateKey = keyFactory.generatePrivate(privateKeySpec);
            Signature rsa = Signature.getInstance("SHA1withRSA");
            rsa.initSign(bobPrivateKey);
            rsa.update(data.getBytes("UTF-8"));
            return DatatypeConverter.printBase64Binary(rsa.sign());
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
        return null;
    }

    public static boolean rsaValidate(String key, String data, String signed) {
        try {
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(DatatypeConverter.parseBase64Binary(key));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey bobPubKey = keyFactory.generatePublic(publicKeySpec);
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(bobPubKey);
            sig.update(data.getBytes("UTF-8"));
            return sig.verify(DatatypeConverter.parseBase64Binary(signed));
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
        return false;
    }
    public static void main(String[] args){
        Viettelpost rsaEncrypt= new Viettelpost();  
           //rsaEncrypt.genKeyPair();  
        String viery = "TRANS_ID||MOFA190925174327605||REF_ID||2VTP452165254||TRANS_STATUS||0||TRANS_DATE||1569466498817||TRANS_MSG||OK";
        String aaString ="OolGUef8ASddKkEpdGnI8p+sjl7lZ99LrJ5Z0nmJcEFOnCsNKQ+DZo/ZJaQrUajsn6NOC9BAWGhOUFMFdbHeSyjsnCU80dFjb+u60/z1pedWx9FnsO3p/OZlWMYLMnXrQDM67KI75c6vm5W593TpO0eMaT30/qah4A5lou4ug7AU9LEc4fFPV/LpSJekZ8qnZj2POzCVHzpaDdLrcBazEcysr94K6p5EvuzdqqegTOZEUr0rewjRtw/ua1FNHczgkpdsskT03alOHm8LcgGivuRau/WYahYfIR/qXRPKUTaacI6dJmYn8Gb+c/kMXYIirPEh2DzxgTpCZNUKicwRNw==";
        String miwen = Encrypt.MD5(viery);
        System.out.println(miwen);
        Boolean yanqianBoolean = rsaValidate(DEFAULT_PUBLIC_KEY,miwen,aaString);
        System.out.println(yanqianBoolean+"11111");
           //加载公钥  
           try {  
               rsaEncrypt.loadPublicKey(Viettelpost.DEFAULT_PUBLIC_KEY);  
               System.out.println("加载公钥成功");  
           } catch (Exception e) {  
               System.err.println(e.getMessage());  
               System.err.println("加载公钥失败");  
           }  
     
           //加载私钥  
           try {  
               rsaEncrypt.loadPrivateKey(Viettelpost.DEFAULT_PRIVATE_KEY);  
               System.out.println("加载私钥成功");  
           } catch (Exception e) {  
               System.err.println(e.getMessage());  
               System.err.println("加载私钥失败");  
           }  
     
           //测试字符串  
           String encryptStr= "{\"cmnd\":082075673,\"sign\":\"88472dea8f0142f7ee5ccbdb026c7b5d\"}";  
           String idString = "452165254";
           String idString1 = "TRANS_ID||vtp2135421521||REF_ID||3VTP452165254||CUSTOMER_CODE||F1680932051902||AMOUNT||300000||FEE||15000||CONTENT||abc vbd||AGENT_USER||12542||AGENT_NAME||abc sds||TRANS_DATE||1235421";
           					  
           String aString = "DjnV8EjcNi6fr9L32WqXzBGPCn/qIJP5XOY9vrYIsX0oMivxC8XcDYzZrWCwTw/oUH2RR4ws+W23VQGg/oWatjOfbe3RGXPKV6xPJra946LjKYLNL9G4XRylJKcwl8w/EbBgwMBqhWt0XCZfBkLp6XP6KvM3z3SBTTWy7SXZTKpYHJLkskzyX8zbnb3UgvOD1rK4wz714nOC5NUmznByqw2Pu387jzLtiyCCkeOlItH61UNtGOKk3UWePQ79k1791jq8+GJQzn6NHycFsFqsZ5N2myKUNM125c4b4UoGOoFtJL++vf93uS95L6bgj41Lr3AQu/ixdgnXhfXrCKMgyA==";
           System.out.println(aString.length());
           System.out.println(SHA_PUBLIC_KEY.length());
           System.out.println(DEFAULT_PUBLIC_KEY.length());
           System.out.println(DEFAULT_PRIVATE_KEY.length());
           String sign = Encrypt.MD5(idString);
           System.out.println(sign);
           try {
        	   
        	   Signature sign11 = Signature.getInstance("SHA1withRSA");
			   sign11.initSign(privateKey);
        	   byte[] data = sign.getBytes();
        	   sign11.update(data);
			   byte[] signature = sign11.sign();
			   String aaaString = rsaSign(DEFAULT_PRIVATE_KEY, sign);
			   System.out.println(aaaString);
			   System.out.println("密文长度:"+ signature.length);
        	   System.out.println(Viettelpost.byte2Base64StringFun(signature));
			   byte[] cipher = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(),signature); 
			   byte[] cipheraaa = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(),Viettelpost.base64String2ByteFun(aaaString)); 
        	   System.out.println("密文长度:"+ cipher.length);
        	   System.out.println(Viettelpost.byte2Base64StringFun(cipher));
        	   System.out.println(Viettelpost.byte2Base64StringFun(cipheraaa));
        	   
        	   byte[] resjiami = rsaEncrypt.decrypt(rsaEncrypt.privateKey, cipher);
        	   byte[] resjiamiAAA = rsaEncrypt.decrypt(rsaEncrypt.privateKey, cipheraaa);
        	   System.out.println("密文长度:"+ resjiami.length);
        	   System.out.println(Viettelpost.byte2Base64StringFun(resjiami));
        	   System.out.println(Viettelpost.byte2Base64StringFun(resjiamiAAA));
			   Signature verifySign = Signature.getInstance("SHA1withRSA");
			   verifySign.initVerify(publicKey);
				//用于验签的数据
			   verifySign.update(data);
			   boolean flag = verifySign.verify(resjiami);
				System.out.println(flag);
				System.out.println(rsaValidate(SHA_PUBLIC_KEY,sign,aString));
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * try { //加密 byte[] cipher = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(),
		 * sign.getBytes()); System.out.println("密文长度:"+ cipher.length);
		 * System.out.println(Viettelpost.byte2Base64StringFun(cipher)); } catch
		 * (Exception e) { System.err.println(e.getMessage()); }
		 */ 
         
    }
    
}
