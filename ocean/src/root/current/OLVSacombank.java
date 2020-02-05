package root.current;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import root.SendMsg;
import root.current.Base64;
import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Decoder;
import com.project.service.account.OLVSacombankService;
import com.shove.security.Encrypt;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.util.SessionHelper;
import com.thinkive.web.base.ActionResult;
import com.thinkive.web.base.BaseAction;

public class OLVSacombank extends BaseAction {
	private static Logger logger = Logger.getLogger(OLVSacombank.class);
	/*private static UserService userService = new UserService();*/
	private static OLVSacombankService olvsacombankservice = new OLVSacombankService();
	private static final int merchantID= 1626;
	private static final int bussinessID= 418;
	private static final int feeID= 401;
	private static final double version= 1.3;
	private static final String secretkey= "0mZRw8yAx1wh65bvmM36L70uBr96y6FHEnJGbgv3";
	private static final long timestamp= System.currentTimeMillis();

	private static final String YZ_PUBLIC_KEY= "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDSleEh7tdD1F4i5FVE6Nb2nXuW"+
					"Cjvwt3ymENbKJJ7jFNn2lHz+Q70N5UiUPzXV2TBellqTMEjVsZDoXBUcUfXUbNjs"+
					"fhGqTAFvydwitGg+IOcA4S4M62bZG6OQ7Sxmi/Q1pGFPTTZOVA0Rg4qd7ZQCvS1z"+
					"vkt24k17ZUebBIWi+QIDAQAB";
		      
private static final String DEFAULT_PRIVATE_KEY= "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANKV4SHu10PUXiLk"+
					"VUTo1vade5YKO/C3fKYQ1soknuMU2faUfP5DvQ3lSJQ/NdXZMF6WWpMwSNWxkOhc"+
					"FRxR9dRs2Ox+EapMAW/J3CK0aD4g5wDhLgzrZtkbo5DtLGaL9DWkYU9NNk5UDRGD"+
					"ip3tlAK9LXO+S3biTXtlR5sEhaL5AgMBAAECgYEAoIoWibH1dgZuhZcg0lnXl0bT"+
					"sry2mXqokkmAFbb5UhDRYcQtzgGCHRfQzwOpXRAoadOh1VgYDqFB4YJhradmClt6"+
					"wuzsc7x3mycUvJgYjQUkyVkIGuMv9KU7qsdnw7Oruf4lBfiBXpmqnFLtXri6fkhW"+
					"ZkMcRR5y3H6QPWNJOQkCQQDwnU4A/8x0SWfnnAgb5Lt3a5l6KCrBNBEguZ2MmIPc"+
					"NykiGLdVgGHV2gSZka5gOgeWS1AI5aMRCN9pUJBUOSSvAkEA4A0FNCjzktDstACx"+
					"NqbWzEY3IuNjyvDbplj3BR0GphvFWVQE8eyMkYAC6htxHUvDH3sgKlUh8WJ6Doe9"+
					"o1Js1wJAZQx7DNpgf9mwJEmX8jbuL+nlS7MAuaDnLIonSztl7R7RTHt6yxKeg9QK"+
					"qH/Q7qKQOyMn2oSuJeJHCRCHfId/CwJAP7e0OSdETgaYqoBf5ZkBEUsOtx9P7BEf"+
					"hmlaptugXnEbaNIaLesF9wMe0RFI0/oN3dAYT7GyW4GUELnN1rrENQJAcOFy/ROy"+
					"fc9A0E17ygZF4GbRDaSyVbxdZqr1VvLp6hx+RtfUJ3FQsHn+zyA2Zam/3YGVDSN4"+
					"v9b0hto407YL8A==";
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
			//System.out.println("Signature in verify: " + signature);
			sig.initVerify(convertStringToPublicKey(OLVSacombank.YZ_PUBLIC_KEY));
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
	public ActionResult doPaySacombankCopy() throws Exception{
		JSONObject jsonObject = new JSONObject();
		SimpleDateFormat famat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat famatday = new SimpleDateFormat("dd-MM-yyyy");
		String time = famat.format(new Date());
		String timeday = famatday.format(new Date());
	 	int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());//后台登录账户
		if(cmsuserid == 0){	    		
		   jsonObject.put("error", -1);
		   jsonObject.put("msg", "Vui lòng đăng nhập trước");
		   this.getWriter().write(jsonObject.toString());	
		   return null;
		}
	    String nowtime = time.substring(11,13);
		if((!(Integer.parseInt(nowtime)>=9 && Integer.parseInt(nowtime)<13)) && (!(Integer.parseInt(nowtime)>=13 && Integer.parseInt(nowtime)<21))){	    		
			jsonObject.put("error", -1);
			jsonObject.put("msg", "系统错误！");
			this.getWriter().write(jsonObject.toString());	
			return null;
		}
		int userid = getIntParameter("rec_id");
		logger.info(userid);
		int jkid = olvsacombankservice.getUserID(userid+"");
		//用户是ACC（转账账号）还是 PAN（卡号）
		int bankcardcode = olvsacombankservice.getUserBankcardcode(userid);
		//用户在NAPAS的银行名字
		String userbankname = olvsacombankservice.getUserBankcardname(userid);
		//用户在NAPAS的银行代码
		String userbankno = olvsacombankservice.getUserBankNo(userid);
		//用户的银行账号
		String userbankcardNo = olvsacombankservice.getUserBankcardNo(userid);
		//用户的姓名
		String username = olvsacombankservice.getUserBankusername(userid);
		
		//用户当天的放款记录
		int fkjl = olvsacombankservice.getAllFKcount(userid,timeday);
		if(fkjl == 1){
			jsonObject.put("error", -1);
			jsonObject.put("msg", "Hôm nay đã giải ngân rồi, vui lòng liên hệ IT kiểm tra");
			this.getWriter().write(jsonObject.toString());	
			return null;
		}
		logger.info("userbankname:"+userbankname);
		logger.info("userbankcard:"+userbankno);
		String sjdsmoney = olvsacombankservice.getUserSjdsmoney(jkid);
		logger.info(sjdsmoney);
		if(TextUtils.isEmpty(sjdsmoney)){
			jsonObject.put("error", -1);
			jsonObject.put("msg", "没有借款信息，请刷新页面！");
			this.getWriter().write(jsonObject.toString());	
			return null;
		}
		BigDecimal fkjine=new BigDecimal(sjdsmoney.replace(",", ""));
		
		System.out.println("放款金额是："+fkjine);
	    
         if(fkjine.compareTo(new BigDecimal("10000000")) >= 0){
        	jsonObject.put("error", -7);
        	jsonObject.put("msg", "金额超过10.000.000,请联系技术人员更改！");
        	this.getWriter().write(jsonObject.toString());	
			return null;
         }
         String payUrl = "https://webservices.sacombank.com/bank-api/v1/fundtranfer";
         String paysign = "";
         HttpHeaders payheader = null;
         ResponseEntity<String> payresp = null;
         String payxmlRequest = "<DOCUMENT>" +
         		"<TRANSACTION_ID>"+jkid+"</TRANSACTION_ID>" +
         		"<PARTNER_ID>F168</PARTNER_ID>" +
         		"<LOCAL_DATETIME>"+time+"</LOCAL_DATETIME>";
         		if("Sacombank".equals(userbankname)){
         			payxmlRequest +="<TRANSACTION_TYPE>0</TRANSACTION_TYPE>";
         		}else{
         			payxmlRequest +="<TRANSACTION_TYPE>2</TRANSACTION_TYPE>";
         		}
         		payxmlRequest +="<ACCT1>060198488073</ACCT1>" +
         		"<BENF_NAME>"+username+"</BENF_NAME>";
         		//if(bankcardcode == 1){
         			payxmlRequest +="<ACC_TYPE>ACC</ACC_TYPE>";
         		/*}else{
         			payxmlRequest +="<ACC_TYPE>PAN</ACC_TYPE>";
         		}*/
         		payxmlRequest +="<ACCT2>"+userbankcardNo+"</ACCT2>"+
         		"<AMOUNT>"+fkjine+"</AMOUNT>" +
         		"<CURRENCY>VND</CURRENCY>" +
         		"<CONTENT>F168 Mofa da chuyen khoan thanh cong den khach hang "+username+userid+"</CONTENT>" +
         		"<BANK_NAME>"+userbankname+"</BANK_NAME>" +
         		"<BANKBRANCHCODE>"+userbankno+"</BANKBRANCHCODE>" +
         		"<BANKCODE></BANKCODE>" +
         		"<ADDITION_INFO></ADDITION_INFO>" +
         		"</DOCUMENT>";
         OLVSacombank myClass = new OLVSacombank();
         PrivateKey privateKey = myClass.getPrivateKey(OLVSacombank.getDefaultPrivateKey());
         paysign = myClass.signature(payxmlRequest,privateKey);
         payheader = myClass.addHeaderValue("Signature", paysign, payheader);
         String payencoding = Base64.encode(("ctf168:Ff#201812@1680").getBytes("UTF-8"));
         payheader = myClass.addHeaderValue("Authorization", "Basic " + payencoding, payheader);
         HttpEntity<String> payrequestEntity = new HttpEntity<String>(payxmlRequest, payheader);
         RestTemplate payrestTemplate = new RestTemplate();
         ResponseEntity<String> payresponse = payrestTemplate.exchange(payUrl, HttpMethod.POST, payrequestEntity, String.class);
         String payresponseBody = payresponse.getBody(); 
         System.out.println("responseBody: " + payresponseBody);
         HttpHeaders checkresponseHeader = payresponse.getHeaders();
		 String checksigData = checkresponseHeader.getFirst("signature");
		 if (myClass.verifySignature(payresponseBody, checksigData)) {
			System.out.println("verify success");
		 } else {
			System.out.println("verify fail");
		 }
         String paystatus = "";
         String paytime = "";
         String paystanid = "";
         String errorDescription = "";
         Document doc = null;
         try {
        	 doc = DocumentHelper.parseText(payresponseBody); // 将字符串转为XML
        	 Element rootElt = doc.getRootElement(); // 获取根节点
    		 paystatus = rootElt.elementTextTrim("TRANSACTION_STATUS"); // 拿到head节点下的子节点title值
    		 paytime = rootElt.elementTextTrim("LOCAL_DATETIME"); // 拿到head节点下的子节点title值
    		 paystanid = rootElt.elementTextTrim("STANID"); // 拿到head节点下的子节点title值
    		 errorDescription = rootElt.elementTextTrim("ERROR_DESCRIPTION"); // 拿到head节点下的子节点title值
    		 System.out.println("paystatus:"+paystatus);
    		 System.out.println("paytime:"+paytime);
    		 System.out.println("paystanid:"+paystanid);
    		 System.out.println("errorDescription:"+errorDescription);
         } catch (DocumentException e) {
        	 e.printStackTrace();
         } catch (Exception e) {
        	 e.printStackTrace();
         }
        if("0".equals(paystatus)){
        	HttpHeaders responseHeader = payresponse.getHeaders();
    		String paysigData = responseHeader.getFirst("signature");
    		if (myClass.verifySignature(payresponseBody, paysigData)) {
    			System.out.println("verify success");
    		} else {
    			System.out.println("verify fail");
    		}
    		logger.info("进入放款");
        	SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss") ;
        	SimpleDateFormat fmtrqday = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
    		Calendar calendar =Calendar.getInstance();
        	logger.info(userid);
        	int jkdate = olvsacombankservice.getJK(jkid);
        	DataRow user = olvsacombankservice.getUserRecThreeInfoYN(userid+""); 
    		//String merchOrderId = "scb" + System.currentTimeMillis();
    		String merchOrderId = paystanid;
        	
    	    DataRow row3 =  new DataRow();	
    	    row3.set("userid",userid);
    	    row3.set("title", "Thông báo chấp thuận đề xuất vay");//审核通知
    	    row3.set("neirong","Khoản vay của bạn đã được giải ngân vào tài khoản của bạn, vui lòng kiểm tra tài khoản.");	//您的借款已经汇入您的账户，请及时查收。	             
    	    row3.set("fb_time", fmtrq.format(calendar.getTime()));				
    	    olvsacombankservice.insertUserMsg(row3); 
             //还款期限			   
    	    DataRow row = new DataRow() ;
    	    DataRow row1 = new DataRow() ;
    	    row.set("fkdz_time", fmtrq.format(calendar.getTime())) ; 
    	    row.set("fkdz_time_day", fmtrqday.format(calendar.getTime())) ; 
    	    row.set("fkr_time",fmtrq.format(calendar.getTime()));
    	    row1.set("checktime", fmtrq.format(calendar.getTime())) ; 
    	    if(jkdate == 2){
            	 //当前时间加30天
            	  calendar.add(Calendar.DATE, 30);		        	
             }
             if(jkdate == 1){
            	 //当前时间加15天
            	  calendar.add(Calendar.DATE, 15);    	 
             }
             if(jkdate == 3){
            	 //当前时间加15天
            	 calendar.add(Calendar.DATE, 7);    	 
             }
             if(jkdate == 4){
            	 //当前时间加15天
            	 calendar.add(Calendar.DATE, 14);    	 
             }
             DecimalFormat famt = new DecimalFormat("###,###");
     		int hongbaoid = olvsacombankservice.getHongbaoid(userid+"");
     		int appflyer = olvsacombankservice.getAppflyer(userid+"");
 			int jkhongbaoid = olvsacombankservice.getHongbaoidJK(jkid+"");

			String sjsh_money = olvsacombankservice.getSjshsh(jkid+"");
    		int sjsh = Integer.parseInt(sjsh_money.replace(",", ""));
    		String lx = olvsacombankservice.getLX(jkid+"");
    		int lxlx = Integer.parseInt(lx.replace(",", ""));
    		
    		row.set("id", jkid);
    		row.set("hkyq_time", fmtrq.format(calendar.getTime())) ; 
    		row.set("hk_time", fmtrq.format(calendar.getTime())) ; 
    		row.set("sfyfk","1");
    		if(jkhongbaoid ==1){
    			row.set("sjsh_money",famt.format(sjsh-10000));
	    		row.set("lx",famt.format(lxlx-10000));
    		}
    		row.set("fkr",cmsuserid);
    		olvsacombankservice.updateUserJk(row);	
    		row1.set("name", user.getString("cardusername"));
    		row1.set("cellphone", user.getString("mobilephone"));
    		row1.set("acount", user.getString("cardno"));
    		row1.set("sum", sjdsmoney);
    		row1.set("userid", userid);
    		row1.set("remark", "放款") ; 
    		row1.set("checkid", cmsuserid) ; 
    		row1.set("versoin", 1) ; 
    		row1.set("remarkresult", jkid) ; 
    		row1.set("orderid", merchOrderId) ; 
    		olvsacombankservice.updateUserFK(row1);
    		if(hongbaoid == 0 && jkhongbaoid ==1){
    			DataRow rowhong = new DataRow();
	    		rowhong.set("id", userid);
	    		rowhong.set("hongbao", 1);
	    		olvsacombankservice.updateUserHongbao(rowhong);
    		}
    		if(appflyer == 2){
    			DataRow rowhong = new DataRow();
	    		rowhong.set("id", userid);
	    		rowhong.set("appflyer", 1);
	    		olvsacombankservice.updateUserHongbao(rowhong);
    		}
    		String userName =user.getString("username");
    		  String appName ="Mofa";
    		    userName =userName.substring(0,4);					  
    		    if(userName.equals("MOFA")){
    		    	appName="Mofa";					    	
    		    }else if(userName.equals("F168")){
    				appName = "F168";
    			} 
    	   String content = "[{\"PhoneNumber\":\""+user.getString("mobilephone")+"\",\"Message\":\""+appName+" thong bao: So tien vay "+sjdsmoney+" da chuyen khoan den TK Ngan hang so cuoi "+user.getString("cardno").substring(user.getString("cardno").length()-4, user.getString("cardno").length())+".Neu sau 24 tieng chua nhan duoc tien, vui long goi hotline: 1900234558, inbox http://bit.ly/2QJAh16.\",\"SmsGuid\":\""+user.getString("mobilephone")+"\",\"ContentType\":1}]";
    	   String con = URLEncoder.encode(content, "utf-8");
    	   SendMsg sendMsg = new SendMsg();
    	   String returnString = SendMsg.sendMessageByGet(con,user.getString("mobilephone")); 
    	   /*if(name1.equals("MOVA")){
    			  jbdcmsaction.testSendPushMOVA(registration_id, altertz, code);
    		  }else if(name1.equals("OLVA")){
    			  jbdcmsaction.testSendPushOLVA(registration_id,"Thông báo chấp thuận đề xuất vay",altertz, code);
    		  }*/
           jsonObject.put("error", 1);
           jsonObject.put("msg","Thành công");//成功 
    	   this.getWriter().write(jsonObject.toString());	
    	   return null ;
        }else{
        	SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss") ;
        	Calendar calendar =Calendar.getInstance();
        	DataRow user = olvsacombankservice.getUserRecThreeInfoYN(userid+"");
        	String merchOrderId = paystanid;
        	DataRow row1 = new DataRow() ;
        	row1.set("checktime", fmtrq.format(calendar.getTime())) ; 
        	row1.set("name", user.getString("cardusername"));
    		row1.set("cellphone", user.getString("mobilephone"));
    		row1.set("acount", user.getString("cardno"));
    		row1.set("jkid", jkid);
    		row1.set("sum", sjdsmoney);
    		row1.set("bankname", userbankname);
    		row1.set("bankid", userbankno);
    		row1.set("userid", userid);
    		row1.set("remark", errorDescription) ; 
    		row1.set("checkid", cmsuserid) ; 
    		row1.set("errstatus", paystatus) ; 
    		row1.set("orderid", merchOrderId) ; 
    		olvsacombankservice.updateUserFKSB(row1);
    		DataRow row = new DataRow();
    		row.set("id", jkid);
    		row.set("fkr",cmsuserid);
    		olvsacombankservice.updateUserJk(row);	
        	jsonObject.put("error", -3);
       	 	jsonObject.put("msg", errorDescription);
       	 	this.getWriter().write(jsonObject.toString());	
		    return null;
        }
        
	
	}
	 public ActionResult doPaySacombank() throws Exception{
		JSONObject jsonObject = new JSONObject();
		SimpleDateFormat famat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat famatday = new SimpleDateFormat("dd-MM-yyyy");
		String time = famat.format(new Date());
		String timeday = famatday.format(new Date());
	 	int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());//后台登录账户
		if(cmsuserid == 0){	    		
		   jsonObject.put("error", -1);
		   jsonObject.put("msg", "Vui lòng đăng nhập trước");
		   this.getWriter().write(jsonObject.toString());	
		   return null;
		}
	    String nowtime = time.substring(11,13);
		if((!(Integer.parseInt(nowtime)>=9 && Integer.parseInt(nowtime)<13)) && (!(Integer.parseInt(nowtime)>=13 && Integer.parseInt(nowtime)<21))){	    		
			jsonObject.put("error", -1);
			jsonObject.put("msg", "系统错误！");
			this.getWriter().write(jsonObject.toString());	
			return null;
		}
		int userid = getIntParameter("rec_id");
		logger.info(userid);
		int jkid = olvsacombankservice.getUserID(userid+"");
		//用户是ACC（转账账号）还是 PAN（卡号）
		int bankcardcode = olvsacombankservice.getUserBankcardcode(userid);
		//用户在NAPAS的银行名字
		String userbankname = olvsacombankservice.getUserBankcardname(userid);
		//用户在NAPAS的银行代码
		String userbankcode = olvsacombankservice.getUserBankNo(userid);
		//用户的银行账号
		String cardId = olvsacombankservice.getUserBankcardNo(userid);
		//用户的姓名
		String username = olvsacombankservice.getUserBankusername(userid);
		
		String name = olvsacombankservice.getUserName(userid);
		String phoneNumber = olvsacombankservice.getPhoneNumber(userid);
		//用户当天的放款记录
		int fkjl = olvsacombankservice.getAllFKcount(userid,timeday);
		if(fkjl == 1){
			jsonObject.put("error", -1);
			jsonObject.put("msg", "Hôm nay đã giải ngân rồi, vui lòng liên hệ IT kiểm tra");
			this.getWriter().write(jsonObject.toString());	
			return null;
		}
		logger.info("userbankname:"+userbankname);
		logger.info("userbankcard:"+userbankcode);
		String sjdsmoney = olvsacombankservice.getUserSjdsmoney(jkid);
		logger.info(sjdsmoney);
		if(TextUtils.isEmpty(sjdsmoney)){
			jsonObject.put("error", -1);
			jsonObject.put("msg", "没有借款信息，请刷新页面！");
			this.getWriter().write(jsonObject.toString());	
			return null;
		}
		BigDecimal fkjine=new BigDecimal(sjdsmoney.replace(",", ""));
		
		System.out.println("放款金额是："+fkjine);
	    
	     if(fkjine.compareTo(new BigDecimal("10000000")) >= 0){
	    	jsonObject.put("error", -7);
	    	jsonObject.put("msg", "金额超过10.000.000,请联系技术人员更改！");
	    	this.getWriter().write(jsonObject.toString());	
			return null;
	     }
	    String orderNo = userid+"-"+jkid+"-"+System.currentTimeMillis();
	    String remark = "abcdong Thanh Toán";
	    String returnUrl = "http://www.abcdongvn.com/servlet/app/NewAppAc?function=FunPayHuiTiaoDZ";
	    String urlString = "https://payment.funpay.asia/fun/transfer/transferMoney";
		String String2 ="accountName="+name+"&accountNo="+cardId+"&accountType=0&amount="+fkjine+"&bankBranchNo=&bankLocation=vn"+"&bankNo="+userbankcode+"&businessID="+bussinessID+"&currency=VND&merchantID="+merchantID+"&orderNo="+orderNo+"&phoneNumber="+phoneNumber+"&remark="+remark+"&returnUrl="+returnUrl+"&timestamp="+timestamp+"&version="+version+secretkey;
		String sign = Encrypt.MD5(String2).toUpperCase();
		String String3 ="accountName="+name+"&accountNo="+cardId+"&accountType=0&amount="+fkjine+"&bankBranchNo=&bankLocation=vn"+"&bankNo="+userbankcode+"&businessID="+bussinessID+"&currency=VND&merchantID="+merchantID+"&orderNo="+orderNo+"&phoneNumber="+phoneNumber+"&remark="+remark+"&returnUrl="+returnUrl+"&timestamp="+timestamp+"&version="+version+"&sign="+sign;
		String param =Base64.encode(String3.getBytes("UTF-8"));
		String urlr = urlString+"?"+"param="+param;
		String resopnser = sendGet(urlr);
		com.alibaba.fastjson.JSONObject Objr = com.alibaba.fastjson.JSONObject.parseObject(resopnser);
		String code = Objr.getString("code");
		String msg = Objr.getString("msg");
		if("10000".equals(code)) {
			com.alibaba.fastjson.JSONObject result = com.alibaba.fastjson.JSONObject.parseObject(Objr.getString("result"));
			int status = result.getInteger("status");
			if(status == 0) {
				logger.info("进入放款");
		    	SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss") ;
		    	SimpleDateFormat fmtrqday = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
				Calendar calendar =Calendar.getInstance();
		    	logger.info(userid);
		    	int jkdate = olvsacombankservice.getJK(jkid);
		    	DataRow user = olvsacombankservice.getUserRecThreeInfoYN(userid+""); 
				String merchOrderId = orderNo;
		    	
			    DataRow row3 =  new DataRow();	
			    row3.set("userid",userid);
			    row3.set("title", "Thông báo chấp thuận đề xuất vay");//审核通知
			    row3.set("neirong","Khoản vay của bạn đã được giải ngân vào tài khoản của bạn, vui lòng kiểm tra tài khoản.");	//您的借款已经汇入您的账户，请及时查收。	             
			    row3.set("fb_time", fmtrq.format(calendar.getTime()));				
			    olvsacombankservice.insertUserMsg(row3); 
		         //还款期限			   
			    DataRow row = new DataRow() ;
			    DataRow row1 = new DataRow() ;
			    row.set("fkdz_time", fmtrq.format(calendar.getTime())) ; 
			    row.set("fkdz_time_day", fmtrqday.format(calendar.getTime())) ; 
			    row.set("fkr_time",fmtrq.format(calendar.getTime()));
			    row1.set("checktime", fmtrq.format(calendar.getTime())) ; 
			    if(jkdate == 2){
		        	 //当前时间加30天
		        	  calendar.add(Calendar.DATE, 30);		        	
		         }
		         if(jkdate == 1){
		        	 //当前时间加15天
		        	  calendar.add(Calendar.DATE, 15);    	 
		         }
		         if(jkdate == 3){
		        	 //当前时间加15天
		        	 calendar.add(Calendar.DATE, 7);    	 
		         }
		         if(jkdate == 4){
		        	 //当前时间加15天
		        	 calendar.add(Calendar.DATE, 14);    	 
		         }
		         DecimalFormat famt = new DecimalFormat("###,###");
		 		int hongbaoid = olvsacombankservice.getHongbaoid(userid+"");
		 		int appflyer = olvsacombankservice.getAppflyer(userid+"");
				int jkhongbaoid = olvsacombankservice.getHongbaoidJK(jkid+"");
		
				String sjsh_money = olvsacombankservice.getSjshsh(jkid+"");
				int sjsh = Integer.parseInt(sjsh_money.replace(",", ""));
				String lx = olvsacombankservice.getLX(jkid+"");
				int lxlx = Integer.parseInt(lx.replace(",", ""));
				
				row.set("id", jkid);
				row.set("hkyq_time", fmtrq.format(calendar.getTime())) ; 
				row.set("hk_time", fmtrq.format(calendar.getTime())) ; 
				row.set("sfyfk","1");
				if(jkhongbaoid ==1){
					row.set("sjsh_money",famt.format(sjsh-10000));
		    		row.set("lx",famt.format(lxlx-10000));
				}
				row.set("fkr",cmsuserid);
				olvsacombankservice.updateUserJk(row);	
				row1.set("name", user.getString("cardusername"));
				row1.set("cellphone", user.getString("mobilephone"));
				row1.set("acount", user.getString("cardno"));
				row1.set("sum", sjdsmoney);
				row1.set("userid", userid);
				row1.set("remark", "放款") ; 
				row1.set("checkid", cmsuserid) ; 
				row1.set("versoin", 1) ; 
				row1.set("remarkresult", jkid) ; 
				row1.set("orderid", merchOrderId) ; 
				olvsacombankservice.updateUserFK(row1);
				if(hongbaoid == 0 && jkhongbaoid ==1){
					DataRow rowhong = new DataRow();
		    		rowhong.set("id", userid);
		    		rowhong.set("hongbao", 1);
		    		olvsacombankservice.updateUserHongbao(rowhong);
				}
				if(appflyer == 2){
					DataRow rowhong = new DataRow();
		    		rowhong.set("id", userid);
		    		rowhong.set("appflyer", 1);
		    		olvsacombankservice.updateUserHongbao(rowhong);
				}
			   String userName =user.getString("username");
			   String appName ="abcdong";
			    userName =userName.substring(0,4);					  
			    if(userName.equals("DONG")){
			    	appName="abcdong";					    	
			    }
			   /*String content = "[{\"PhoneNumber\":\""+user.getString("mobilephone")+"\",\"Message\":\""+appName+" thong bao: So tien vay "+sjdsmoney+" da chuyen khoan den TK Ngan hang so cuoi "+user.getString("cardno").substring(user.getString("cardno").length()-4, user.getString("cardno").length())+".Neu sau 24 tieng chua nhan duoc tien, vui long goi hotline: 1900234558, inbox http://bit.ly/2QJAh16.\",\"SmsGuid\":\""+user.getString("mobilephone")+"\",\"ContentType\":1}]";
			   String con = URLEncoder.encode(content, "utf-8");
			   SendMsg sendMsg = new SendMsg();
			   String returnString = SendMsg.sendMessageByGet(con,user.getString("mobilephone"));
			   */
		       jsonObject.put("error", 1);
		       jsonObject.put("msg","Thành công");//成功 
			   this.getWriter().write(jsonObject.toString());	
			   return null ;
			}else {	
				jsonObject.put("newcode", -3);
				jsonObject.put("newmsg", status);// 系统异常，请稍后再试�??
				this.getWriter().write(jsonObject.toString());
				return null;
			}
		}else{
	    	SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss") ;
	    	Calendar calendar =Calendar.getInstance();
	    	DataRow user = olvsacombankservice.getUserRecThreeInfoYN(userid+"");
	    	String merchOrderId = orderNo;
	    	DataRow row1 = new DataRow() ;
	    	row1.set("checktime", fmtrq.format(calendar.getTime())) ; 
	    	row1.set("name", user.getString("cardusername"));
			row1.set("cellphone", user.getString("mobilephone"));
			row1.set("acount", user.getString("cardno"));
			row1.set("jkid", jkid);
			row1.set("sum", sjdsmoney);
			row1.set("bankname", userbankname);
			row1.set("bankid", userbankcode);
			row1.set("userid", userid);
			row1.set("remark", msg) ; 
			row1.set("checkid", cmsuserid) ; 
			row1.set("errstatus",code) ; 
			row1.set("orderid", merchOrderId) ; 
			olvsacombankservice.updateUserFKSB(row1);
			DataRow row = new DataRow();
			row.set("id", jkid);
			row.set("fkr",cmsuserid);
			olvsacombankservice.updateUserJk(row);	
	    	jsonObject.put("error", -3);
	   	 	jsonObject.put("msg",msg);
	   	 	this.getWriter().write(jsonObject.toString());	
		    return null;
	    }
	}
	public ActionResult doPayFunPay() throws Exception{
		JSONObject jsonObject = new JSONObject();
		int userid = getIntParameter("rec_id");
		int jkid = getIntParameter("jk_id");
		BigDecimal fkjine=new BigDecimal(getIntParameter("fkjine"));
		String name = getStrParameter("name");
		String cardId = getStrParameter("cardId");
		String userbankcode = getStrParameter("userbankcode");
		String phoneNumber = getStrParameter("phonenumber");
		
	    String orderNo = userid+"-"+jkid+"-"+System.currentTimeMillis();
	    String remark = "abcdong Thanh Toán";
	    String returnUrl = "http://www.abcdongvn.com/servlet/app/NewAppAc?function=FunPayHuiTiaoDZ";
	    String urlString = "https://payment.funpay.asia/fun/transfer/transferMoney";
		String String2 ="accountName="+name+"&accountNo="+cardId+"&accountType=0&amount="+fkjine+"&bankBranchNo=&bankLocation=vn"+"&bankNo="+userbankcode+"&businessID="+bussinessID+"&currency=VND&merchantID="+merchantID+"&orderNo="+orderNo+"&phoneNumber="+phoneNumber+"&remark="+remark+"&returnUrl="+returnUrl+"&timestamp="+timestamp+"&version="+version+secretkey;
		String sign = Encrypt.MD5(String2).toUpperCase();
		String String3 ="accountName="+name+"&accountNo="+cardId+"&accountType=0&amount="+fkjine+"&bankBranchNo=&bankLocation=vn"+"&bankNo="+userbankcode+"&businessID="+bussinessID+"&currency=VND&merchantID="+merchantID+"&orderNo="+orderNo+"&phoneNumber="+phoneNumber+"&remark="+remark+"&returnUrl="+returnUrl+"&timestamp="+timestamp+"&version="+version+"&sign="+sign;
		String param =Base64.encode(String3.getBytes("UTF-8"));
		String urlr = urlString+"?"+"param="+param;
		String resopnser = sendGet(urlr);
		com.alibaba.fastjson.JSONObject Objr = com.alibaba.fastjson.JSONObject.parseObject(resopnser);
    	jsonObject.put("error", 0);
   	 	jsonObject.put("msg",Objr);
   	 	this.getWriter().write(jsonObject.toString());	
	    return null;
	    
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
	public static String sendGet(String url) {
		HttpClient httpClient = null;  
		 HttpGet httpPost = null;  
	        String result = null;  
	        try{  
	            httpClient = new SSLClient();  
	            httpPost = new HttpGet(url);  
	            httpPost.addHeader("Content-Type", "application/json");
	            httpPost.setHeader("Authorization", "b909af4c98d44a65934f36a7c102be13,c87b7a3f4e8b4711936ebe00f0265cd9");
				
	            HttpResponse response = httpClient.execute(httpPost);  
	            if(response != null){  
	            	org.apache.http.HttpEntity resEntity = (org.apache.http.HttpEntity) response.getEntity();  
	                if(resEntity != null){  
	                    result = EntityUtils.toString((org.apache.http.HttpEntity) resEntity,"UTF-8");  
	                }  
	            }  
	        }catch(Exception ex){  
	        	logger.info(url+"========�쳣========="+ex);
	        }  
	        return result; 
    }
	 public ActionResult doGetNaPasBank() throws Exception{
		 JSONObject jsonobject = new JSONObject();
		 	OLVSacombank myClass = new OLVSacombank();
		 	SimpleDateFormat famat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		 	 SimpleDateFormat famatid = new SimpleDateFormat("yyyyMMddhhmmssSSS");
		 	 
		    PrivateKey privateKey = myClass.getPrivateKey(OLVSacombank.DEFAULT_PRIVATE_KEY);
		 	String hostUrl = "https://webservices.sacombank.com/bank-api/v1/bankcode/getbankcodenapas";
			String mysign = "";
			HttpHeaders header = null;
			ResponseEntity<String> resp = null;
			 String time = famat.format(new Date());
			    String checkid = famatid.format(new Date());
			//OLVSacombank myClass = new OLVSacombank();
			String xmlRequest = "<DOCUMENT><TRANSACTION_ID>"+checkid+"</TRANSACTION_ID><PARTNER_ID>CTYXT</PARTNER_ID><LOCAL_DATETIME>"+time+"</LOCAL_DATETIME></DOCUMENT>";
			//PrivateKey privateKey = myClass.getPrivateKey(OLVSacombank.DEFAULT_PRIVATE_KEY);
			mysign = myClass.signature(xmlRequest,privateKey);
			logger.info("data sign: " + mysign);
			header = myClass.addHeaderValue("Signature", mysign, header);
			
			String encoding = Base64.encode(("ctf168:Ff#201812@1680").getBytes("UTF-8"));
			header = myClass.addHeaderValue("Authorization", "Basic " + encoding, header);
			
			HttpEntity<String> requestEntity = new HttpEntity<String>(xmlRequest, header);
			logger.info(requestEntity);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.exchange(hostUrl, HttpMethod.POST, requestEntity, String.class);
			String responseBody = response.getBody(); 
			logger.info("responseBody: " + responseBody);
			HttpHeaders responseHeader = response.getHeaders();
			String sigData = responseHeader.getFirst("signature");
			if (myClass.verifySignature(responseBody, sigData)) {
				logger.info("verify success");
			} else {
				logger.info("verify fail");
			}
			Document doc = null;
	
	         String bankstatus = "";
	         String bankcode[] = {};
	         String bankname[] = {};
	         try {
	             doc = DocumentHelper.parseText(responseBody); // 将字符串转为XML
	             Element rootElt = doc.getRootElement(); // 获取根节点
	             bankstatus = rootElt.elementTextTrim("TRANSACTION_STATUS"); // 拿到head节点下的子节点title值
	             logger.info("paystatus:" + bankstatus);
	             Element zijiedian = rootElt.element("LISTBANKCODE"); // 获取根节点
	             logger.info("zijiedian:" + zijiedian);
	             List<Element> list = zijiedian.elements("BANKCODE");
	             logger.info(list);
	        	 // 遍历head节点
	             bankcode = new String[list.size()];
	             bankname = new String[list.size()];
	        	 for(int i =0 ; i<list.size();i++){
	        		 Element recordEle = list.get(i);
	        		 bankcode[i] = recordEle.elementTextTrim("BANKCODE"); // 拿到head节点下的子节点title值
	        		 bankname[i] = recordEle.elementTextTrim("BANKNAME"); // 拿到head节点下的子节点title值
	        		 
	        		 logger.info("BANKCODE:" + bankcode[i]);
	        		 logger.info("BANKNAME:" + bankname[i]);
	        		 if("NH TMCP Ngoai Thuong VN (VietcomBank)".equals(bankname[i])){
	        			 logger.info("BANKCODE:" + bankcode[i]);
	        		 }
	        		
	        	 }
	         } catch (DocumentException e) {
	             e.printStackTrace();
	         } catch (Exception e) {
	             e.printStackTrace();
	         }
	         return null;
	 }
	 public static void main(String[] args) throws Exception {
		 	JSONObject jsonobject = new JSONObject();
		 	OLVSacombank myClass = new OLVSacombank();
		 	SimpleDateFormat famat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		 	 SimpleDateFormat famatid = new SimpleDateFormat("yyyyMMddhhmmssSSS");
		 	 
		    PrivateKey privateKey = myClass.getPrivateKey(OLVSacombank.DEFAULT_PRIVATE_KEY);
		 	String hostUrl = "https://webservices.sacombank.com/bank-api/v1/bankcode/getbankcodenapas";
			String mysign = "";
			HttpHeaders header = null;
			ResponseEntity<String> resp = null;
			 String time = famat.format(new Date());
			    String checkid = famatid.format(new Date());
			//OLVSacombank myClass = new OLVSacombank();
			String xmlRequest = "<DOCUMENT><TRANSACTION_ID>"+checkid+"</TRANSACTION_ID><PARTNER_ID>F168</PARTNER_ID><LOCAL_DATETIME>"+time+"</LOCAL_DATETIME></DOCUMENT>";
			//PrivateKey privateKey = myClass.getPrivateKey(OLVSacombank.DEFAULT_PRIVATE_KEY);
			mysign = myClass.signature(xmlRequest,privateKey);
			System.out.println("data sign: " + mysign);
			header = myClass.addHeaderValue("Signature", mysign, header);
			
			String encoding = Base64.encode(("ctf168:Ff#201812@1680").getBytes("UTF-8"));
			header = myClass.addHeaderValue("Authorization", "Basic " + encoding, header);
			
			HttpEntity<String> requestEntity = new HttpEntity<String>(xmlRequest, header);
			System.out.println(requestEntity);
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
			Document doc = null;
	
			 String userbankcode = "";
	         String bankstatus = "";
	         String bankcode[] = {};
	         String bankname[] = {};
	         try {
	             doc = DocumentHelper.parseText(responseBody); // 将字符串转为XML
	             Element rootElt = doc.getRootElement(); // 获取根节点
	             bankstatus = rootElt.elementTextTrim("TRANSACTION_STATUS"); // 拿到head节点下的子节点title值
	             System.out.println("paystatus:" + bankstatus);
	             Element zijiedian = rootElt.element("LISTBANKCODE"); // 获取根节点
	             System.out.println("zijiedian:" + zijiedian);
	             List<Element> list = zijiedian.elements("BANKCODE");
	             System.out.println(list);
	        	 // 遍历head节点
	             bankcode = new String[list.size()];
	             bankname = new String[list.size()];
	        	 for(int i =0 ; i<list.size();i++){
	        		 Element recordEle = list.get(i);
	        		 bankcode[i] = recordEle.elementTextTrim("BANKCODE"); // 拿到head节点下的子节点title值
	        		 bankname[i] = recordEle.elementTextTrim("BANKNAME"); // 拿到head节点下的子节点title值
	        		 
	        		 System.out.println("BANKCODE:" + bankcode[i]);
	        		 System.out.println("BANKNAME:" + bankname[i]);
	        		 if("NH TMCP Ngoai Thuong VN (VietcomBank)".equals(bankname[i])){
	        			 System.out.println("11111");
	        			 System.out.println("BANKCODE:" + bankcode[i]);
	        		 }
	        		
	        	 }
	         } catch (DocumentException e) {
	             e.printStackTrace();
	         } catch (Exception e) {
	             e.printStackTrace();
	         }
		 	/*int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());//后台登录账户
			 if(cmsuserid == 0){	    		
			   jsonObject.put("error", -1);
			   jsonObject.put("msg", "Vui lòng đăng nhập trước");
			   this.getWriter().write(jsonObject.toString());	
			   return null;
			 }*/
		  /*  SimpleDateFormat famat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		    SimpleDateFormat famatid = new SimpleDateFormat("yyyyMMddhhmmssSSS");
		    int userid = 4;
			int jkid = 400;
			String username = olvsacombankservice.getUserBankcardname(userid);
			String userbankname = olvsacombankservice.getUserBankcardname(userid);
			String userbankcard = olvsacombankservice.getUserBankNo(userid);
			System.out.println(userbankname);
			if(userbankname != ""){
				userbankname = userbankname.substring(0, userbankname.indexOf("-")).toLowerCase().trim();
			}
			System.out.println(userbankname);
			String sjdsmoney = olvsacombankservice.getUserSjdsmoney(jkid);
			if(TextUtils.isEmpty(sjdsmoney)){
				jsonobject.put("error", -1);
				jsonobject.put("msg", "没有借款信息，请联系技术人员！");
				this.getWriter().write(jsonobject.toString());	
				return null;
			}
			//int fkjine = Integer.parseInt(sjdsmoney.replace(",", ""));
			BigDecimal fkjine=new BigDecimal("10000000");
			System.out.println(fkjine);
		    String time = famat.format(new Date());
		    String checkid = famatid.format(new Date());
		    
		    OLVSacombank myClass = new OLVSacombank();
		    PrivateKey privateKey = myClass.getPrivateKey(OLVSacombank.DEFAULT_PRIVATE_KEY);
		   */
		    //String time = famat.format(new Date());
		   // String checkid = famatid.format(new Date());
		    /*String checkidUrl = "https://emm.sacombank.com/bank-api/v1/checktransaction";
		    String checkidsign = "";
		    HttpHeaders checkidheader = null;
		    ResponseEntity<String> checkidresp = null;
		    //OLVSacombank myClass = new OLVSacombank();
		    String checkidxmlRequest = "<DOCUMENT><TRANSACTION_ID>"+checkid+"</TRANSACTION_ID><PARTNER_ID>CTYXT</PARTNER_ID><LOCAL_DATETIME>"+time+"</LOCAL_DATETIME><TRAN_INFO>20180613015302387</TRAN_INFO></DOCUMENT>";
		   // PrivateKey privateKey = myClass.getPrivateKey(OLVSacombank.DEFAULT_PRIVATE_KEY);
		    checkidsign = myClass.signature(checkidxmlRequest,privateKey);
		    System.out.println("data sign: " + checkidsign);
		    checkidheader = myClass.addHeaderValue("Signature", checkidsign, checkidheader);
		    
		    String checkidencoding = Base64.encode(("xuongthinhco:xtpass").getBytes("UTF-8"));
		    checkidheader = myClass.addHeaderValue("Authorization", "Basic " + checkidencoding, checkidheader);
		    
		    HttpEntity<String> checkidrequestEntity = new HttpEntity<String>(checkidxmlRequest, checkidheader);
		    System.out.println(checkidrequestEntity);
		    RestTemplate checkidrestTemplate = new RestTemplate();
		    ResponseEntity<String> checkidresponse = checkidrestTemplate.exchange(checkidUrl, HttpMethod.POST, checkidrequestEntity, String.class);
		    String checkidresponseBody = checkidresponse.getBody(); 
		    System.out.println("responseBody: " + checkidresponseBody);
		    HttpHeaders checkidresponseHeader = checkidresponse.getHeaders();
		    String checkidsigData = checkidresponseHeader.getFirst("signature");
		    if (myClass.verifySignature(checkidresponseBody, checkidsigData)) {
		    	System.out.println("verify success");
		    } else {
		    	System.out.println("verify fail");
		    }
		    */
		  //String time = famat.format(new Date());
			   // String checkid = famatid.format(new Date());
		    /*String checkUrl = "https://emm.sacombank.com/bank-api/v1/checkaccount";
			String checksign = "";
			HttpHeaders checkheader = null;
			ResponseEntity<String> checkresp = null;
			String checkxmlRequest = "<DOCUMENT><TRANSACTION_ID>20180629032355298</TRANSACTION_ID><PARTNER_ID>CTYXT</PARTNER_ID><LOCAL_DATETIME>"+time+"</LOCAL_DATETIME><ACCOUNT_ID>742686800270029</ACCOUNT_ID><ACC_TYPE>ACC</ACC_TYPE><CHECK_TYPE>NAPAS</CHECK_TYPE><BENF_NAME></BENF_NAME><BANK_ID>970401</BANK_ID></DOCUMENT>";
			
			checksign = myClass.signature(checkxmlRequest,privateKey);
			System.out.println("data sign: " + checksign);
			checkheader = myClass.addHeaderValue("Signature", checksign, checkheader);
			
			String checkencoding = Base64.encode(("xuongthinhco:xtpass").getBytes("UTF-8"));
			checkheader = myClass.addHeaderValue("Authorization", "Basic " + checkencoding, checkheader);
			
			HttpEntity<String> checkrequestEntity = new HttpEntity<String>(checkxmlRequest, checkheader);
			System.out.println(checkrequestEntity);
			RestTemplate checkrestTemplate = new RestTemplate();
			ResponseEntity<String> checkresponse = checkrestTemplate.exchange(checkUrl, HttpMethod.POST, checkrequestEntity, String.class);
			String checkresponseBody = checkresponse.getBody(); 
			System.out.println("responseBody: " + checkresponseBody);
			HttpHeaders checkresponseHeader = checkresponse.getHeaders();
			String checksigData = checkresponseHeader.getFirst("signature");
			if (myClass.verifySignature(checkresponseBody, checksigData)) {
				System.out.println("verify success");
			} else {
				System.out.println("verify fail");
			}
			*/
			
			/*String hostUrl = "https://emm.sacombank.com/bank-api/v1/bankcode/getbankcodenapas";
			String mysign = "";
			HttpHeaders header = null;
			ResponseEntity<String> resp = null;
			//OLVSacombank myClass = new OLVSacombank();
			String xmlRequest = "<DOCUMENT><TRANSACTION_ID>"+userid+"</TRANSACTION_ID><PARTNER_ID>CTYXT</PARTNER_ID><LOCAL_DATETIME>11/06/2018 17:00:53</LOCAL_DATETIME></DOCUMENT>";
			//PrivateKey privateKey = myClass.getPrivateKey(OLVSacombank.DEFAULT_PRIVATE_KEY);
			mysign = myClass.signature(xmlRequest,privateKey);
			System.out.println("data sign: " + mysign);
			header = myClass.addHeaderValue("Signature", mysign, header);
			
			String encoding = Base64.encode(("xuongthinhco:xtpass").getBytes("UTF-8"));
			header = myClass.addHeaderValue("Authorization", "Basic " + encoding, header);
			
			HttpEntity<String> requestEntity = new HttpEntity<String>(xmlRequest, header);
			System.out.println(requestEntity);
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
			Document doc = null;
			
			 String userbankcode = "";
	         String bankstatus = "";
	         String bankcode[] = {};
	         String bankname[] = {};
	         try {
	             doc = DocumentHelper.parseText(responseBody); // 将字符串转为XML
	             Element rootElt = doc.getRootElement(); // 获取根节点
	             bankstatus = rootElt.elementTextTrim("TRANSACTION_STATUS"); // 拿到head节点下的子节点title值
	             System.out.println("paystatus:" + bankstatus);
	             Element zijiedian = rootElt.element("LISTBANKCODE"); // 获取根节点
	             System.out.println("zijiedian:" + zijiedian);
	             List<Element> list = zijiedian.elements("BANKCODE");
	             System.out.println(list);
	        	 // 遍历head节点
	             bankcode = new String[list.size()];
	             bankname = new String[list.size()];
	        	 for(int i =0 ; i<list.size();i++){
	        		 Element recordEle = list.get(i);
	        		 bankcode[i] = recordEle.elementTextTrim("BANKCODE"); // 拿到head节点下的子节点title值
	        		 bankname[i] = recordEle.elementTextTrim("BANKNAME"); // 拿到head节点下的子节点title值
	        		 
	        		 System.out.println("BANKCODE:" + bankcode[i]);
	        		 System.out.println("BANKNAME:" + bankname[i]);
	        		 if(userbankname.equals(bankname[i])){
	            		 userbankcode = bankcode[i] ;
	            	 }
	        	 }
	         } catch (DocumentException e) {
	             e.printStackTrace();
	         } catch (Exception e) {
	             e.printStackTrace();
	         }*/
	         
	         //System.out.println(userbankcode);
	         /*if("1".equals(bankstatus)){
	        	jsonobject.put("error", -2);
	        	jsonobject.put("msg", "获取NAPAS银行列表失败！");
	        	this.getWriter().write(jsonobject.toString());	
				return null;
	         }
	         if(userbankcode.length() != 6){
	        	jsonobject.put("error", -2);
	        	jsonobject.put("msg", "没有找到对应的银行编号，请联系技术人员查证！");
	        	this.getWriter().write(jsonobject.toString());	
				return null;
	         }
			
			 Document doc = null;
	         String payUrl = "https://emm.sacombank.com/bank-api/v1/fundtranfer";
	         String paysign = "";
	         HttpHeaders payheader = null;
	         ResponseEntity<String> payresp = null;
	         String payxmlRequest = "<DOCUMENT>" +
	         		//"<TRANSACTION_ID>"+userid+"</TRANSACTION_ID>" +
	         		"<TRANSACTION_ID>"+checkid+"</TRANSACTION_ID>" +
	         		"<PARTNER_ID>CTYXT</PARTNER_ID>" +
	         		"<LOCAL_DATETIME>"+time+"</LOCAL_DATETIME>" +
	         		"<TRANSACTION_TYPE>2</TRANSACTION_TYPE>" +
	         		"<ACCT1>060018356859</ACCT1>" +
	         		//"<BENF_NAME>"+username+"</BENF_NAME>" +
	         		"<BENF_NAME>OLAVA</BENF_NAME>" +
	         		"<ACC_TYPE>ACC</ACC_TYPE>" +
	         		//"<ACCT2>"+userbankcard+"</ACCT2>" +
	         		"<ACCT2>742686800270029</ACCT2>" +
	         		"<AMOUNT>4000000</AMOUNT>" +
	         		"<CURRENCY>VND</CURRENCY>" +
	         		"<CONTENT>OLAVA</CONTENT>" +
	         		//"<BANK_NAME>"+userbankname+"<BANK_NAME>" +
	         		"<BANK_NAME>MHB</BANK_NAME>" +
	         		//"<BANKBRANCHCODE>"+userbankcode+"</BANKBRANCHCODE>" +
	         		"<BANKBRANCHCODE>970401</BANKBRANCHCODE>" +
	         		"<BANKCODE></BANKCODE>" +
	         		"<ADDITION_INFO></ADDITION_INFO>" +
	         		"</DOCUMENT>";
	         System.out.println(payxmlRequest);
	         paysign = myClass.signature(payxmlRequest,privateKey);
	         System.out.println("data sign: " + paysign);
	         payheader = myClass.addHeaderValue("Signature", paysign, payheader);
	         String payencoding = Base64.encode(("xuongthinhco:xtpass").getBytes("UTF-8"));
	         payheader = myClass.addHeaderValue("Authorization", "Basic " + payencoding, payheader);
	         
	         HttpEntity<String> payrequestEntity = new HttpEntity<String>(payxmlRequest, payheader);
	         System.out.println(payrequestEntity);
	         
	         
	         SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();  
	         requestFactory.setConnectTimeout(60000);// 设置超时  
	         requestFactory.setReadTimeout(60000);
	         RestTemplate payrestTemplate = new RestTemplate(requestFactory);
	         
	         RestTemplate payrestTemplate = new RestTemplate();
	         ResponseEntity<String> payresponse = payrestTemplate.exchange(payUrl, HttpMethod.POST, payrequestEntity, String.class);
	         String payresponseBody = payresponse.getBody(); 
	         System.out.println("responseBody: " + payresponseBody);
	         String paystatus = "";
	         String paytime = "";
	         String paystanid = "";
	         String errorDescription = "";
	         try {
	        	 doc = DocumentHelper.parseText(payresponseBody); // 将字符串转为XML
	        	 Element rootElt = doc.getRootElement(); // 获取根节点
	        	 System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
        		 paystatus = rootElt.elementTextTrim("TRANSACTION_STATUS"); // 拿到head节点下的子节点title值
        		 paytime = rootElt.elementTextTrim("LOCAL_DATETIME"); // 拿到head节点下的子节点title值
        		 paystanid = rootElt.elementTextTrim("STANID"); // 拿到head节点下的子节点title值
        		 errorDescription = rootElt.elementTextTrim("ERROR_DESCRIPTION"); // 拿到head节点下的子节点title值
        		 System.out.println("paystatus:"+paystatus);
        		 System.out.println("paytime:"+paytime);
        		 System.out.println("paystanid:"+paystanid);
        		 System.out.println("errorDescription:"+errorDescription);
	         } catch (DocumentException e) {
	        	 e.printStackTrace();
	         } catch (Exception e) {
	        	 e.printStackTrace();
	         }
	         
	         if("1".equals(paystatus)){
	        	 jsonobject.put("error", -3);
	        	 jsonobject.put("msg", errorDescription);
	        	 this.getWriter().write(jsonobject.toString());	
				 return null;
	         }
	         if("2".equals(paystatus)){
	        	 jsonobject.put("error", -4);
	        	 jsonobject.put("msg", "核算corebanking 时GD Timeout（交易超时）！");
	        	 this.getWriter().write(jsonobject.toString());	
	        	 return null;
	         }
	         if("3".equals(paystatus)){
	        	 jsonobject.put("error", -5);
	        	 jsonobject.put("msg","发令napas 时GD timeout（交易超时）,与napas核对!");
	        	 this.getWriter().write(jsonobject.toString());	
	        	 return null;
	         }
			  测试用的。。。。。。。。。
			String paysigData = responseHeader.getFirst("signature");
			if (myClass.verifySignature(payresponseBody, paysigData)) {
				System.out.println("verify success");
			} else {
				System.out.println("verify fail");
			}
			
			logger.info("进入放款");
	    	JSONObject jsonObject = new JSONObject();	
	    	
	    	Date  date = new Date();
	    	SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss") ;
	    	SimpleDateFormat fmtrqday = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
			Calendar calendar =Calendar.getInstance();
	    	String rec_id = getStrParameter("rec_id");
	    	logger.info(rec_id);
	    	int jkid = jbdLLpayService.getUserID(rec_id);
	    	int jkdate = jbdLLpayService.getJK(jkid);
	    	DataRow user = jbdLLpayService.getUserRecThreeInfoYN(rec_id); 
			String sjds_money = jbdLLpayService.getSjdzYN(jkid);
			String merchOrderId = "mova" + System.currentTimeMillis();
	    	//判断该项目是否已经被审核（得到第三步审核的状态）
			String sfyfk = jbdLLpayService.getCHGResult(jkid+"");
			int code = 0;
			String name = user.getString("username").substring(5, 8);
	    	String name1 = user.getString("username").substring(0, 4);
	    	String registration_id = user.getString("registration_id");
	    	String altertz = "Số tiền bạn đề xuất vay đã được giải ngân đến TK của bạn, vui lòng kiểm tra lại. Trả vay đúng hạn để tăng hạn mức vay cho lần sau nhé.";
	    	
	    	if(name.equals("AND")){
	    		code=1;
	    	}else{
	    		code=2;
	    	}
			if(sfyfk.equals("1")){
				
				jsonObject.put("error", -3);
				jsonObject.put("msg", "Người dùng này không còn ở đây, vui lòng làm mới！");
				this.getWriter().write(jsonObject.toString());	
			    return null;
			}
			
			else{ //审核成功
	    		//通过userid获取用户的信息	
			    DataRow row3 =  new DataRow();	
			    row3.set("userid",rec_id);
			    row3.set("title", "Thông báo chấp thuận đề xuất vay");//审核通知
			    row3.set("neirong","Khoản vay của bạn đã được giải ngân vào tài khoản của bạn, vui lòng kiểm tra tài khoản.");	//您的借款已经汇入您的账户，请及时查收。	             
			    row3.set("fb_time", fmtrq.format(calendar.getTime()));				
			    jbdLLpayService.insertUserMsg(row3); 
		         //还款期限			   
			    DataRow row = new DataRow() ;
			    DataRow row1 = new DataRow() ;
			    row.set("fkdz_time", fmtrq.format(calendar.getTime())) ; 
			    row.set("fkdz_time_day", fmtrqday.format(calendar.getTime())) ; 
			    row.set("fkr_time",fmtrq.format(calendar.getTime()));
			    row1.set("checktime", fmtrq.format(calendar.getTime())) ; 
			    if(jkdate == 2){
		        	 //当前时间加30天
		        	  calendar.add(Calendar.DATE, 30);		        	
		         }
		         if(jkdate == 1){
		        	 //当前时间加15天
		        	  calendar.add(Calendar.DATE, 15);    	 
		         }
	    		row.set("id", jkid);
	    		row.set("hkyq_time", fmtrq.format(calendar.getTime())) ; 
	    		row.set("hk_time", fmtrq.format(calendar.getTime())) ; 
	    		row.set("sfyfk","1");
	    		row.set("fkr",cmsuserid);
	    		jbdLLpayService.updateUserJk(row);	
				row1.set("name", user.getString("cardusername"));
				row1.set("cellphone", user.getString("mobilephone"));
				row1.set("acount", user.getString("cardno"));
				row1.set("sum", sjds_money);
	    		row1.set("userid", rec_id);
	    		row1.set("remark", "放款") ; 
	    		row1.set("checkid", cmsuserid) ; 
	    		row1.set("orderid", merchOrderId) ; 
	    		jbdLLpayService.updateUserFK(row1);
	    	 }
			String userName =user.getString("username");
			  String appName ="MOVA";
			    userName =userName.substring(0,4);					  
			    if(userName.equals("MOVA")){
			    	appName="MOVA";					    	
			    }else if(userName.equals("OLVA")){					    	
			    	appName="OLAVA";
			    }
		   String content = "[{\"PhoneNumber\":\""+user.getString("mobilephone")+"\",\"Message\":\""+appName+" xin thong bao: So tien vay la "+sjds_money+" cua ban da duoc chuyen thanh cong den TK Ngan hang co so cuoi la "+user.getString("cardno").substring(user.getString("cardno").length()-4, user.getString("cardno").length())+" Vui long kiem tra thong tin ngan hang trong vong 1-2h.\",\"SmsGuid\":\""+user.getString("mobilephone")+"\",\"ContentType\":1}]";
		   String con = URLEncoder.encode(content, "utf-8");
		   SendMsg sendMsg = new SendMsg();
		   String returnString = SendMsg.sendMessageByGet(con); 
		   if(name1.equals("MOVA")){
				  jbdcmsaction.testSendPushMOVA(registration_id, altertz, code);
			  }else if(name1.equals("OLVA")){
				  jbdcmsaction.testSendPushOLVA(registration_id, altertz, code);
			  }
	       jsonObject.put("error", 1);
	       jsonObject.put("msg","Thành công");//成功 
		   this.getWriter().write(jsonObject.toString());	
		   return null ;*/
		}
}