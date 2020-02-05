package service.versionSMS;

import java.net.HttpURLConnection;

import org.apache.log4j.Logger;

import com.thinkive.base.config.Configuration;
import com.thinkive.base.jdbc.DataRow;

import service.encrypt.RSA;
import service.encrypt.TripleDes;
import service.util.Base64;
import service.util.SslConnection;
import service.util.Strings;
import service.util.Util;

public class PcService2 {

	private static Logger logger = Logger.getLogger(PcService2.class.getName());

	private static String dna_pub_key = Configuration.getString("pay.dna_pub_key");//公钥
	private static String mer_pfx_key = Configuration.getString("pay.mer_pfx_key");//私钥
	private static String mer_pfx_pass = Configuration.getString("pay.mer_pfx_pass");//私钥密码
	private static String url = Configuration.getString("pay.url");//测试环境下单地址
	private static String user_name = Configuration.getString("pay.user_name");//商户后台用户
//	private static String url = "https://agent.payeco.com/service";//生产环境下单地址
	
	public static void main(String[] args) throws Exception {

		//verify();//认证
//		verify_query(); //认证查询
//		queryAccountInfo();//查询银行卡所属银行名称
		
//		send_message();//发送短信验证码
//		gather();//代收
//		gather_query();//查询代收结果

//		pay();//代付
//		pay_query();//查询代付结果

//		System.out.println("1111111");
	}

	//认证
	public static boolean verify(String accno,String accname,String mobileno,String idno,String orderno) throws Exception {

		MsgBean req_bean = new MsgBean();
		req_bean.setVERSION("2.0");
		req_bean.setMSG_TYPE("300001");
		String batch_no = orderno;//每笔订单不可重复，建议：公司简称缩写+yymmdd+流水号
		req_bean.setBATCH_NO(batch_no);
		req_bean.setUSER_NAME(user_name);//系统后台登录名
								
		MsgBody body = new MsgBody();
		body.setSN(batch_no);//流水号，同一批次不重复即可
		body.setACC_NO(accno);//必填
		body.setACC_NAME(accname);//必填
		body.setAMOUNT("1");//不必填：填写一个(0,5)之间的数值；不填写时随机取(0,5)之间的数值
		body.setCNY("CNY");
		body.setREMARK("绿洲宝平台银行卡实名认证");
		body.setMOBILE_NO(mobileno);//支付手机号，必填
		body.setID_NO(idno);//必填
		body.setID_TYPE("0");//必填
		body.setRETURN_URL(Configuration.getString("pay.MERCHANT_NOTIFY_URL"));
		body.setMER_ORDER_NO(batch_no);
		body.setTRANS_DESC("绿洲宝平台银行卡实名认证");//首笔外呼播报语音内容
		req_bean.getBODYS().add(body);
		String res = sendAndRead(signANDencrypt(req_bean));
		MsgBean res_bean = decryptANDverify(res);
		logger.info(res_bean.toXml());
		if("0000".equals(res_bean.getTRANS_STATE())) {
			logger.info("请求成功");
			return true;
		}
		else
		{
			return false;
		}
	}
	
	//认证查询
	public static void verify_query() throws Exception {

		MsgBean req_bean = new MsgBean();
		req_bean.setVERSION("2.0");
		req_bean.setMSG_TYPE("300002");
		String batch_no = new String(Base64.decode(Util.generateKey(99999,14)));
		req_bean.setBATCH_NO(batch_no);
		req_bean.setUSER_NAME("13728096874");//系统后台登录名
								
		MsgBody body = new MsgBody();
		body.setSN("101000001");
		body.setACC_NO("6222023602076055577");
		body.setACC_NAME("李四123");
//		body.setACC_PROVINCE("广东");
//		body.setACC_CITY("广州");

		body.setID_NO("");
		body.setID_TYPE("0");
		body.setRESERVE("Y");
		req_bean.getBODYS().add(body);
		
		/*	MsgBody body2 = new MsgBody();
		body2.setSN("101000002");
		body2.setACC_NO("6222023602076096878");
		body2.setACC_NAME("李是");
		body2.setAMOUNT("2");
		req_bean.getBODYS().add(body2);
		
		MsgBody body3 = new MsgBody();
		body3.setSN("0000000000000003");
		body3.setACC_NO("6225887800100101");
		body3.setACC_NAME("王午");
		body3.setAMOUNT("2.2");
		req_bean.getBODYS().add(body3);*/

		String res = sendAndRead(signANDencrypt(req_bean));
		
		MsgBean res_bean = decryptANDverify(res);
		
		if("0000".equals(res_bean.getTRANS_STATE())) {
			logger.info("请求成功");
		}
		logger.info(res_bean.toXml());
	}

	//查询银行卡所属银行
	public static void queryAccountInfo() {

		MsgBean req_bean = new MsgBean();
		req_bean.setVERSION("2.0");
		req_bean.setMSG_TYPE("400001");
		req_bean.setBATCH_NO("99EE936559D864");
		req_bean.setUSER_NAME("13728096874");//系统后台登录名
		
		MsgBody body = new MsgBody();
		body.setSN("101000004");
		body.setACC_NO("6225380048403812");
		req_bean.getBODYS().add(body);
		
		String res = sendAndRead(signANDencrypt(req_bean));

		MsgBean res_bean = decryptANDverify(res);
		
		if("0000".equals(res_bean.getTRANS_STATE())) {
			logger.info("请求成功");
		}
		logger.info(res_bean.toXml());

	}

	//发送短信验证码
	public static String send_message(String cardno,String phone,String ms) throws Exception{
		MsgBean req_bean = new MsgBean();
		req_bean.setVERSION("2.0");
		req_bean.setMSG_TYPE("500001");//发送短信
		req_bean.setBATCH_NO(new String(Base64.decode(Util.generateKey(99999,14))));//每笔订单不可重复，建议：公司简称缩写+yymmdd+流水号
		req_bean.setUSER_NAME(user_name);//系统后台登录名
						
		MsgBody body = new MsgBody();
		body.setACC_NO(cardno);//必填
		body.setMOBILE_NO(phone);//系统默认发送短信验证码到用户外呼验密绑定的手机号
		body.setTRANS_DESC(ms);//短信内容不超过40字
		req_bean.getBODYS().add(body);
		
		String res = sendAndRead(signANDencrypt(req_bean));
		
		MsgBean res_bean = decryptANDverify(res);
		
		if("0000".equals(res_bean.getTRANS_STATE())) {
			logger.info("请求成功");
			return res_bean.getBATCH_NO();
		}
		
		return "";
	}

	//代收
	public static DataRow gather(String BATCH_NO,String ACC_NO,String ACC_NAME,String AMOUNT,String idno,String REMARK,String MOBILE_NO,String SMS_CODE,String orderid) throws Exception {

		MsgBean req_bean = new MsgBean();
		req_bean.setVERSION("2.0");
		req_bean.setMSG_TYPE("200001");
		req_bean.setBATCH_NO(BATCH_NO);//1.与send_message接口的BATCH_NO一致；2.每笔订单不可重复，建议：公司简称缩写+yymmdd+流水号
		req_bean.setUSER_NAME(user_name);//系统后台登录名
						
		MsgBody body = new MsgBody();
		body.setSN(orderid);//流水号，同一批次不重复即可
		body.setACC_NO(ACC_NO);//必填
		body.setACC_NAME(ACC_NAME);//必填
		body.setID_NO(idno);//开户证件号
		body.setID_TYPE("0");//证件类型
		body.setAMOUNT(AMOUNT);//必填
		body.setCNY("CNY");
		body.setREMARK(REMARK);
		body.setMOBILE_NO(MOBILE_NO);//支付手机号
		body.setRETURN_URL(Configuration.getString("pay.MERCHANT_NOTIFY_URL"));//异步通知地址
		body.setMER_ORDER_NO(orderid);
		body.setMER_SEQ_NO("");
		body.setTRANS_DESC(REMARK);//首笔外呼播报语音内容
		body.setSMS_CODE(SMS_CODE);//短信码
		req_bean.getBODYS().add(body);
		String res = sendAndRead(signANDencrypt(req_bean));
		MsgBean res_bean = decryptANDverify(res);
		if("0000".equals(res_bean.getTRANS_STATE())) {
			logger.info("请求成功");
		}
		logger.info("请求结果："+res_bean.getBODYS().get(0).getPAY_STATE()+"-"+res_bean.getBODYS().get(0).getREMARK());
		DataRow row = new DataRow();
		row.set("pay_state", res_bean.getBODYS().get(0).getPAY_STATE());
		row.set("pay_ret", res_bean.getBODYS().get(0).getREMARK());
		logger.info(res_bean.toXml());
		return row;
	}

	//代收查询
	public static void gather_query() {

		MsgBean req_bean = new MsgBean();
		req_bean.setVERSION("2.0");
		req_bean.setMSG_TYPE("200002");
		req_bean.setBATCH_NO("A43A424B50D87B"); //同代收交易请求的批次号
		req_bean.setUSER_NAME("13728096874");//系统后台登录名
		
		/*MsgBody body1 = new MsgBody();
		body1.setQUERY_NO_FLAG("1");
		body1.setMER_ORDER_NO("");
		body1.setMER_SEQ_NO("");
		body1.setRETURN_URL("http://10.123.18.44:8080/notifyasyn?beanName=PayEcoNotifyHome&amp;ENCODING=utf-8");
		req_bean.getBODYS().add(body1);*/
			
//		MsgBody body2 = new MsgBody();
//		body2.setQUERY_NO_FLAG("1");
//		body2.setMER_ORDER_NO("MONBE932A83421E6C");//KK78965421354
//		req_bean.getBODYS().add(body2);
		
		String res = sendAndRead(signANDencrypt(req_bean));

		MsgBean res_bean = decryptANDverify(res);
		
		if("0000".equals(res_bean.getTRANS_STATE())) {
			logger.info("请求成功");
		}
		logger.info(res_bean.toXml());

	}	
	
	//代付
	public static void pay() throws Exception {

		MsgBean req_bean = new MsgBean();
		req_bean.setVERSION("2.1");
		req_bean.setMSG_TYPE("100001");
		req_bean.setBATCH_NO(new String(Base64.decode(Util.generateKey(99999,8))));//每笔订单不可重复，建议：公司简称缩写+yymmdd+流水号
		req_bean.setUSER_NAME("13728096874");//系统后台登录名
		
		MsgBody body = new MsgBody();
		body.setSN("0000000001");//流水号，同一批次不重复即可
		body.setACC_NO("6222023602076055577");
		body.setACC_NAME("李四");
		body.setAMOUNT("1");
		/*body.setACC_PROVINCE("上海市");
		body.setACC_CITY("上海市");*/
		body.setBANK_NAME("交通银行");
		body.setACC_PROP("0");
		body.setMER_ORDER_NO("DF1234567811");
		req_bean.getBODYS().add(body);
		
		/*MsgBody body2 = new MsgBody();
		body2.setSN("0000000000000002");
		body2.setACC_NO("6013821900046267618");
		body2.setACC_NAME("李氏2");
		body2.setAMOUNT("256.58");
		body2.setBANK_NAME("中国银行股份有限公司广州天文苑支行");
		req_bean.getBODYS().add(body2);
		
		MsgBody body3 = new MsgBody();
		body3.setSN("0000000000000003");
		body3.setACC_NO("6228480082238310112");
		body3.setACC_NAME("王午3");
		body3.setAMOUNT("11.2");
		body3.setBANK_NAME("农业银行同福东路支行");
		req_bean.getBODYS().add(body3);*/

		String res = sendAndRead(signANDencrypt(req_bean));

		MsgBean res_bean = decryptANDverify(res);
		
		if("0000".equals(res_bean.getTRANS_STATE())) {
			logger.info("请求成功");
		}
		logger.info(res_bean.toXml());
	}

	//代付查询
	public static void pay_query() {

		MsgBean req_bean = new MsgBean();
		req_bean.setVERSION("2.1");
		req_bean.setMSG_TYPE("100002");
		req_bean.setBATCH_NO("A7762217");//同代付交易请求批次号
		req_bean.setUSER_NAME("13728096874");//系统后台登录名

//		MsgBody body = new MsgBody();
//		body.setQUERY_NO_FLAG("0");
//		body.setMER_ORDER_NO("DF123456789");
//		req_bean.getBODYS().add(body);
		String res = sendAndRead(signANDencrypt(req_bean));

		MsgBean res_bean = decryptANDverify(res);
		
		if("0000".equals(res_bean.getTRANS_STATE())) {
			logger.info("请求成功");
		}
		logger.info(res_bean.toXml());

	}
	
	private static MsgBean decryptANDverify(String res) {
		
		String msg_sign_enc = res.split("\\|")[0];
		String key_3des_enc = res.split("\\|")[1];
		
		//解密密钥
		String key_3des = RSA.decrypt(key_3des_enc,mer_pfx_key,mer_pfx_pass);
		
		//解密报文
		String msg_sign = TripleDes.decrypt(key_3des, msg_sign_enc);
		MsgBean res_bean = new MsgBean();
		res_bean.toBean(msg_sign);
		logger.info("res:" + res_bean.toXml());

		//验签
		String dna_sign_msg = res_bean.getMSG_SIGN();
		res_bean.setMSG_SIGN("");
		String verify = Strings.isNullOrEmpty(res_bean.getVERSION())? res_bean.toXml(): res_bean.toSign() ;
		logger.info("verify:" + verify);
		if(!RSA.verify(dna_sign_msg, dna_pub_key, verify)) {
			logger.error("验签失败");
			res_bean.setTRANS_STATE("00A0");
		}
		return res_bean;
	}

	private static String signANDencrypt(MsgBean req_bean) {
		
		//商户签名
		
		System.out.println("before sign xml =="+ req_bean.toSign());
		System.out.println("msg sign = "+RSA.sign(req_bean.toSign(),mer_pfx_key,mer_pfx_pass));
		req_bean.setMSG_SIGN(RSA.sign(req_bean.toSign(),mer_pfx_key,mer_pfx_pass));
		logger.info("req:" + req_bean.toXml());

		//加密报文
		String key = Util.generateKey(9999,24);
		logger.info("key:" + key);
		String req_body_enc = TripleDes.encrypt(key, req_bean.toXml());
		logger.info("req_body_enc:" + req_body_enc);
		//加密密钥
		String req_key_enc = RSA.encrypt(key, dna_pub_key);
		logger.info("req_key_enc:" + req_key_enc);
		logger.info("signANDencrypt:" + req_body_enc+"|"+req_key_enc);
		return req_body_enc+"|"+req_key_enc;

	}

	public static String sendAndRead(String req) {

		try {
			HttpURLConnection connect = new SslConnection().openConnection(url);
			
	        connect.setReadTimeout(30000);
			connect.setConnectTimeout(10000);

			connect.setRequestMethod("POST");
			connect.setDoInput(true);
			connect.setDoOutput(true);
			connect.connect();

			byte[] put = req.getBytes("UTF-8");
			connect.getOutputStream().write(put);

			connect.getOutputStream().flush();
			connect.getOutputStream().close();
			String res = SslConnection.read(connect);

			connect.getInputStream().close();
			connect.disconnect();
			
//			String res = new SslConnection().connect(url);

			return res;
		} catch(Exception e) {
			logger.error(Strings.getStackTrace(e));
		}
		return "";
	}
}
