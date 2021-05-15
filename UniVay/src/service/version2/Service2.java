package service.version2;

import java.net.HttpURLConnection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.thinkive.base.config.Configuration;
import com.thinkive.base.jdbc.DataRow;

import service.encrypt.RSA;
import service.encrypt.TripleDes;
import service.util.SslConnection;
import service.util.Strings;
import service.util.Tools;
import service.util.Util;

public class Service2 {

	private static Logger logger = Logger.getLogger(Service2.class.getName());

	private static String dna_pub_key = Configuration.getString("pay.dna_pub_key");//公钥
	private static String mer_pfx_key = Configuration.getString("pay.mer_pfx_key");//私钥
	private static String mer_pfx_pass = Configuration.getString("pay.mer_pfx_pass");//私钥密码
	private static String url = Configuration.getString("pay.url");//测试环境下单地址
	private static String user_name = Configuration.getString("pay.user_name");//商户后台用户
	
	//查询银行卡所属银行
	public static DataRow queryAccountInfo(String no) {

		MsgBean req_bean = new MsgBean();
		req_bean.setVERSION("2.0");
		req_bean.setMSG_TYPE("400001");
		req_bean.setBATCH_NO(no);
		req_bean.setUSER_NAME(user_name);//系统后台用户名
		DataRow data = null;
		MsgBody body = new MsgBody();
		body.setSN("LDC"+new Date().getTime());
		body.setACC_NO(no);
		req_bean.getBODYS().add(body);
		String res = sendAndRead(signANDencrypt(req_bean));
		MsgBean res_bean = decryptANDverify(res);
		if("0000".equals(res_bean.getTRANS_STATE())) {
			logger.info("请求成功");
			logger.info(res_bean.toXml());
			data  = new DataRow();
			data.set("bank_name", Tools.getXMLValue(res_bean.toXml(), "BANK_NAME"));
			data.set("bank_type", Tools.getXMLValue(res_bean.toXml(), "RESERVE"));
			return data;
		}
		return null;
	}
	
	private static MsgBean decryptANDverify(String res) {
		
		String msg_sign_enc = res.split("\\|")[0];
		String key_3des_enc = res.split("\\|")[1];
		
		logger.info("mer_pfx_key:"+mer_pfx_key+",mer_pfx_pass:"+mer_pfx_pass);
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
	
	//代收查询
	public static boolean gather_query(String oderid) {

		MsgBean req_bean = new MsgBean();
		req_bean.setVERSION("2.0");
		req_bean.setMSG_TYPE("200002");
		req_bean.setBATCH_NO(oderid); //同代收交易请求的批次号
		req_bean.setUSER_NAME(user_name);//系统后台用户名
		String res = sendAndRead(signANDencrypt(req_bean));
		MsgBean res_bean = decryptANDverify(res);
		Map<String, Object> data = res_bean.toMap(res_bean.toXml());
		if("0000".equals(res_bean.getTRANS_STATE())) {
			logger.info("请求成功");
			if(null!=data.get("TRANS_DETAILS"))
			{
				Map<String, Object> TRANS_DETAILS = ((List<Map<String, Object>>) data.get("TRANS_DETAILS")).get(0);
				String PAY_STATE = TRANS_DETAILS.get("PAY_STATE").toString();
				if(!PAY_STATE.equals("0000"))
				{
					return false;
				}
			}
		}
		logger.info(res_bean.toXml());
		return true;
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
