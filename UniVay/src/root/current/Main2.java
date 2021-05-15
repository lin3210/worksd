/**
 * 
 */
package root.current;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import java.util.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import sun.misc.BASE64Decoder;

/**
 * @author os-hungpq
 *
 */
public class Main2 {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		/*创建非对称加密的公钥和私钥示例 将生成的公钥和私钥用Base64编码后打印出来*/
		/*KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(2048); //一般加密位数为1024 对安全要求较高的情况下可以使用2048
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		将公钥base64编码打印出来        System.out.println(Base64.encode(publicKey.getEncoded()));
		将私钥base64编码打印出来      System.out.println(Base64.encode(privateKey.getEncoded()));*/
		String hostUrl = "http://192.168.181.13:9082/bank-api/v1/bankcode/getbankcodenapas";
		String mysign = "";
		HttpHeaders header = null;
		ResponseEntity<String> resp = null;
		Main2 myClass = new Main2();
		String xmlRequest = "<DOCUMENT><TRANSACTION_ID>2040</TRANSACTION_ID><PARTNER_ID>CTYXT</PARTNER_ID><LOCAL_DATETIME>14/05/2018 17:00:53</LOCAL_DATETIME></DOCUMENT>";
		PrivateKey privateKey = myClass.getPrivateKey("MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMi798hiR2Hg/92g"+
					"jisy31URWTOSsxubWwfxI+AHJFtawn9wsxgObnp+uKSExjlTxjvnsdHF9oLvvvOo"+
					"u+1l+5qHy7ct/x83MC0BNlH5m6m2ZNQoWGwMWBqxQLK63NyfXBHIVmEOdKQhQStJ"+
					"HHri8L5NQOLKWOj10DoYqHEtxKsXAgMBAAECgYAY9ncr7jzeSTLIhuznJl5rn8qu"+
					"BTJM3pcFd9F0TK8SxUimvIcpGLJYfWXM384e1vIyBPAyHCI5ykK+3l7weNw+9PgQ"+
					"JCA0hdGWJy6+ePYCs0IleYMpPuaxcPoqARppavKT5AE4rZPeVs0wM6fFd/hBjqo1"+
					"nuMAqBDHAkZ4JlkiUQJBAOmQcD3yzgK6Vi9fCgxZljFzozEGkyjbjBs7p/beBL6n"+
					"ViEfyl4nFisasNSJcoloh6PZapZ39aWpHIBi5mSN0/0CQQDcBDXIBQJs3wETJdHE"+
					"iMMBHWUegC+k2teYe25Mfqt7kATCgYOQz9mkHpgUtWx/C6b1bR1T1eh5YcH3ltIQ"+
					"OcWjAkEAtXB2MTmQnf/g+dXLuYJSscHSMUCZSzEaawFx3s3kQ7M8rmjCzVdx2jRW"+
					"0BSiPGpOjJNWQOOZmYKA2J97uPuQhQJBAIIXnzFQ/Pkg90K6JwmdkCh1mBfr1cAV"+
					"gktwGGz0rTLXmmjkKcYTkG4BhQ0hpTCN1D/kL+YlYlt7WTSrojPj5R8CQFpVGKR5"+
					"zqqWAQ0PwSMtKLWHFNJHxPrBYNIBIb31kTZ5nlL61ceRlkiYrHDOSYz+XmCwKL2v"+
					"HGsG/HWHgeDAiNM=");
		mysign = myClass.signature(xmlRequest,privateKey);
		System.out.println("data sign: " + mysign);
		header = myClass.addHeaderValue("Signature", mysign, header);
		
		String encoding = Base64.encode(("abcd").getBytes("UTF-8"));
		header = myClass.addHeaderValue("Authorization", "Basic " + encoding, header);
		
		HttpEntity<String> requestEntity = new HttpEntity<String>(xmlRequest, header);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(hostUrl, HttpMethod.POST, requestEntity, String.class);
		String responseBody = response.getBody(); 
		System.out.println("responseBody: " + responseBody);
		HttpHeaders responseHeader = response.getHeaders();
		String sigData = responseHeader.getFirst("signature");
		if (myClass.verifySignature(responseBody, sigData)) {
			System.out.println("verify success");
		} else {
			System.out.println("verify fail");
		}
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
			sig.initVerify(convertStringToPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAysam+L456/x31U+cg/Bb9jxtfP9BqQRqRRCobCAMgar3agBhrzwXIw1PGvP/rGB/G8JsC3/3mGsK8BRLXCfGUSUeeTUSfbsHpWLi0wh6wXSJNXkhJPVfwX0ONlQ8sSnX81EBXFe46rLe8VbKghVyfDiED7ez2xKJ2j5dDzGOZ4/DrBjqMyoD0p5fHagdcx9awKWy2H+/4inVmcAZyUpyuNEDv2tG9L9BjwVq/gRl1q7H7LqYqleUICK8TO7rcfvNARejm4HkmFj0osbVYnR2Awt7koYEm/W073tW6MgoXLjTZrgIU6Pq5C7lZzp8D+2q1YPmlIBuP+5o15MGmhTVnwIDAQAB"));
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

	public PrivateKey getPrivateKeyStore() throws Exception {
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
	}

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
}
