package com.task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import sun.misc.BASE64Decoder;

import com.shove.security.Encrypt;
import com.alibaba.fastjson.JSON;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.util.SessionHelper;
import com.thinkive.base.util.StringHelper;
import com.thinkive.base.util.security.RSAUtil;
import com.thinkive.web.base.ActionResult;
import com.thinkive.web.base.BaseAction;

public class OLVSacombank extends BaseAction {
	private static Logger logger = Logger.getLogger(OLVSacombank.class);
	/*private static UserService userService = new UserService();*/
	
	private static final String DEFAULT_PUBLIC_KEY= "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC+kj3qG+FQPKGQf/v2YjhKcPue"+
					"snkRETQzxxyDwUga254bybqZamRi7vIuQjVZFEaAQpVzibSDkFw2DuLlgdjVkUDg"+
					"f7bUrKD0Z0bXiV82KGPFC3Q+X8jQYA3UmB4PJ2b8QHhMPkhTs8yAUomvqfolvRsO"+
					"ecVavowbToGIWBP3IQIDAQAB";
		      
private static final String DEFAULT_PRIVATE_KEY= "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAL6SPeob4VA8oZB/"+
						"+/ZiOEpw+56yeRERNDPHHIPBSBrbnhvJuplqZGLu8i5CNVkURoBClXOJtIOQXDYO"+
						"4uWB2NWRQOB/ttSsoPRnRteJXzYoY8ULdD5fyNBgDdSYHg8nZvxAeEw+SFOzzIBS"+
						"ia+p+iW9Gw55xVq+jBtOgYhYE/chAgMBAAECgYEApoJE0r6VeXPgfs80qaxcHr7l"+
						"O14ZZg4wHGWGxqAkjkMBvKnf/pf1KnQ46q4yyqhqiTqkNpVFQgbqatlK6tkvSLjQ"+
						"yZe3pvGBtXhkZvu61uF6NhQ077wJzGDpq4QZ/3kLMnLfBBY8KJK1fFlBNLqeM1hf"+
						"mEa4wiI0ATz6skx8q7kCQQDpoWog7d5/cNQ+HXmYG4mi9eiaMaEhbhj3r1fclooY"+
						"nC/QgSbxH/7Efo00hr6vnpVTvUQD0K7AjNAmLYXP51FXAkEA0NFjm+lYIgIlEbM1"+
						"sBdyfYm5b2VXvPByUIyRruNyn/8wMh7VBTJHpypU3qn5kkavqwJ1/9FiqzMceq/y"+
						"ui3YRwJATHAVSwTIYSXHKzN1jwCjfX+7RIesUkW1QYRK2M8gC4E+W5eF6CoxrpRB"+
						"GshjJw5ZiPZJhH4ITtNDXtpSB32FLQJAUPqSnVjdc7rPM1EDJfnBzYtGXTu4za2V"+
						"N0V8DY1o20E0KIuqYmyAOwSAiyTxgtdksnWu2vkRQYD9r1piT+8NawJALskiziLJ"+
						"pkyCMy1l5stP9dp9E/y1tLdte4/2Jiuaaqm7Klksc5p9oKt9HyQgznjoRqCACKlt"+
						"pKUgkETnoLDyhQ==";
public static String getDefaultPrivateKey() {
	return DEFAULT_PRIVATE_KEY;
}
	public static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
  }
	public static PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
  }
	/**
	 * kiem tra chu ky so
	 * 
	 * @param xml
	 * @return
	 */
	public boolean verifySignature(String xmlReq, String signature) {
		boolean rsVerify = false;
		Signature sig;
		try {
			sig = Signature.getInstance("SHA256withRSA");
			System.out.println("Signature in verify: " + signature);
			sig.initVerify(convertStringToPublicKey(OLVSacombank.DEFAULT_PUBLIC_KEY));
			sig.update(xmlReq.getBytes("utf-8"));
			System.out.println("Signature lenght in verify: " + Base64.decode(signature).length);
			return sig.verify(Base64.decode(signature));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rsVerify;
	}

	public PublicKey convertStringToPublicKey(String publicKeyContent) throws Exception {
		byte[] publicBytes = Base64.decode(publicKeyContent);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey pubKey = keyFactory.generatePublic(keySpec);
		return pubKey;
	}

	/**
	 * Ky du lieu
	 * 
	 * @param xml
	 * @return sign
	 */
	public String signature(String xml,PrivateKey privateKey) {
		String sigData = "";
		Signature rsa = null;
		try {
			rsa = Signature.getInstance("SHA256withRSA");
			rsa.initSign(privateKey);
			rsa.update(xml.getBytes());
			sigData = Base64.encode(rsa.sign());
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
		return sigData;
	}

	/*public PrivateKey getPrivateKeyStore() throws Exception {
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(Main2.class.getResourceAsStream("/root/key/xthinh.private.jks"), "123456".toCharArray());
		Enumeration aliasesEnum = ks.aliases();
		PrivateKey privateKey = null;
		Certificate[] certificateChain = null;
		if (aliasesEnum.hasMoreElements()) {
			String alias = (String) aliasesEnum.nextElement();
			certificateChain = ks.getCertificateChain(alias);
			privateKey = (PrivateKey) ks.getKey(alias, "123456".toCharArray());

		}
		return privateKey;
	}*/

	/**
	 * Them value trong header
	 * 
	 * @param key
	 * @param value
	 * @param header
	 * @return
	 */
	public HttpHeaders addHeaderValue(String key, String value, HttpHeaders header) {
		if (header == null) {
			header = new HttpHeaders();
		}
		header.add(key, value);
		return header;
	}
	
	 public void readStringXml(String xml) {
         Document doc = null;
         int paystatus = 1;
         try {
             doc = DocumentHelper.parseText(xml); // 将字符串转为XML
             Element rootElt = doc.getRootElement(); // 获取根节点
             System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
             Iterator iter = rootElt.elementIterator("DOCUMENT"); // 获取根节点下的子节点head
             // 遍历head节点
             while (iter.hasNext()) {
                 Element recordEle = (Element) iter.next();
                 String title = recordEle.elementTextTrim("title"); // 拿到head节点下的子节点title值
                 System.out.println("title:" + title);
                 Iterator iters = recordEle.elementIterator("script"); // 获取子节点head下的子节点script
                 // 遍历Header节点下的Response节点
                 while (iters.hasNext()) {
                     Element itemEle = (Element) iters.next();
                     String username = itemEle.elementTextTrim("username"); // 拿到head下的子节点script下的字节点username的值
                     String password = itemEle.elementTextTrim("password");
                     System.out.println("username:" + username);
                     System.out.println("password:" + password);
                 }
             }
         } catch (DocumentException e) {
             e.printStackTrace();
         } catch (Exception e) {
             e.printStackTrace();
         }
     }
}