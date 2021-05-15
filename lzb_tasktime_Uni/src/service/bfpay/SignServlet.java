package service.bfpay;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.mypay.merchantutil.Md5Encrypt;
import com.mypay.merchantutil.UrlHelper;
import com.mypay.merchantutil.timestamp.TimestampResponseParser;
import com.mypay.merchantutil.timestamp.TimestampResponseResult;
import com.mypay.merchantutil.timestamp.TimestampUtils;

/**
 * 商户支付请求加签示例
 * 
 * @author 300468
 * 
 */
public class SignServlet{

	private static Logger logger = Logger.getLogger(SignServlet.class);	
	
	public static JSONObject getHtmlHttpPostJson(String url,String parameterMap) throws ClientProtocolException, IOException
	{
		HttpClientContext context = new HttpClientContext();
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		StringEntity myEntity = new StringEntity(parameterMap,ContentType.APPLICATION_JSON);
		httpPost.setEntity(myEntity);
		RequestConfig requestConfig = RequestConfig.custom().setRedirectsEnabled(false).build();
		httpPost.setConfig(requestConfig);
		httpPost = setPostInfo(httpPost);
		HttpResponse createCodeKey = client.execute(httpPost, context);
		String content = EntityUtils.toString(createCodeKey.getEntity());
		System.out.println(content);
		JSONObject jsonObject = JSONObject.fromObject(content);  
		return jsonObject;
	}
	
	public static HttpPost setPostInfo(HttpPost httpPost) {
		httpPost.addHeader("Accept",
				"application/json, text/plain, */*");
		httpPost.addHeader("Accept-Language", "zh-cn");
		httpPost.addHeader(
				"User-Agent",
				"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3");
		httpPost.addHeader("Accept-Charset", "utf-8");
		httpPost.addHeader("Keep-Alive", "300");
		httpPost.addHeader("Connection", "Keep-Alive");
		httpPost.addHeader("Cache-Control", "no-cache");
		return httpPost;
	}
	
	 public static String md5(String data) throws NoSuchAlgorithmException {
		 
		         MessageDigest md = MessageDigest.getInstance("MD5");
		 
		         md.update(data.getBytes());
		 
		         StringBuffer buf = new StringBuffer();
		 
		         byte[] bits = md.digest();
		 
		         for(int i=0;i<bits.length;i++){
		 
		             int a = bits[i];
		 
		             if(a<0) a+=256;
		 
		             if(a<16) buf.append("0");
		 
		            buf.append(Integer.toHexString(a));
		 
		         }
		 
		         return buf.toString();
		 
		     }
    /**
     * 认证支付发验证码
     * @return
     */
	public static JSONObject pay_rz_yzm(String card_bind_mobile_phone_no,String bank_code,String amount,String card_no,String cert_no,String orderno,String real_name)
	{
		//String orderno = "LZB"+System.currentTimeMillis();
		String str = "amount="+amount+"&bank_code="+bank_code+"&card_bind_mobile_phone_no="+card_bind_mobile_phone_no+"&card_no="+card_no+"&cert_no="+cert_no+"&cert_type=01&customer_id="+orderno+"&input_charset=UTF-8&out_trade_no="+orderno+"&partner=201511171553101480&real_name="+real_name+"&service=ebatong_mp_dyncode&sign_type=MD501AKC7JZG2G12MOC1D2FNQAUVIWE9Qlhvwii";
		System.out.println(str);
		String sign = Md5Encrypt.encrypt(str);
	
		System.out.println(sign+"===="+Md5Encrypt.encrypt(str));
		//String jsonstr = "{\"cert_type\":\"01\",\"cert_no\":\"430923198710231111\",\"sign_type\":\"MD5\",\"input_charset\":\"UTF-8\",\"card_no\":\"6212264000004800872\",\"out_trade_no\":\""+orderno+"\",\"bank_code\":\"ICBC_D_B2C\",\"card_bind_mobile_phone_no\":\"13243888699\",\"sign\":\""+sign+"\",\"amount\":\"2.00\",\"real_name\":\"龚文书\",\"service\":\"ebatong_mp_dyncode\",\"partner\":\"201206281102516718\",\"customer_id\":\""+orderno+"\"}";
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("amount", amount);
		jsonObject.put("bank_code", bank_code);
		jsonObject.put("card_bind_mobile_phone_no", card_bind_mobile_phone_no);
		jsonObject.put("card_no", card_no);
		jsonObject.put("cert_no", cert_no);
		jsonObject.put("cert_type", "01");
		jsonObject.put("customer_id", orderno);
		jsonObject.put("input_charset", "UTF-8");
		jsonObject.put("out_trade_no", orderno);
		jsonObject.put("partner", "201511171553101480");
		jsonObject.put("real_name", real_name);
		jsonObject.put("service", "ebatong_mp_dyncode");
		jsonObject.put("sign_type", "MD5");
		jsonObject.put("sign", sign);
		try {
			return getHtmlHttpPostJson("https://www.ebatong.com/mobileFast/getDynNum.htm",jsonObject.toString());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	 /**
     * 认证支付发验证码
     * @return
     */
	public static JSONObject pay_tx(JSONObject object)
	{
		Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("service", new String[]{"ebatong_auth_withdraw"});
		map.put("input_charset", new String[]{"UTF-8"});
		map.put("partner", new String[]{"201511171553101480"});
		map.put("sign_type", new String[]{"MD5"});
		
		map.put("return_url", new String[]{object.getString("return_url")});
		map.put("customer_id", new String[]{object.getString("customer_id")});
		map.put("out_trade_no", new String[]{object.getString("out_trade_no")});
		map.put("card_no", new String[]{object.getString("card_no")});
		map.put("real_name", new String[]{object.getString("real_name")});
		map.put("cert_no", new String[]{object.getString("cert_no")});
		map.put("cert_type", new String[]{object.getString("cert_type")});
		map.put("amount_str", new String[]{object.getString("amount_str")});
		map.put("bind_mobile", new String[]{object.getString("bind_mobile")});
		map.put("bank_code", new String[]{object.getString("bank_code")});
		map.put("withdraw_time", new String[]{object.getString("withdraw_time")});
		
		String key = "01AKC7JZG2G12MOC1D2FNQAUVIWE9Qlhvwii"; // 商户加密字符�?
		String paramStr = UrlHelper.sortParamers(map);
        System.out.println(paramStr);
        String plaintext = TimestampUtils.mergePlainTextWithMerKey(paramStr, key);
        
		String str = plaintext;
		String sign = Md5Encrypt.encrypt(str);
		logger.info("sign:"+sign);
		
		JSONObject jsonObject = new JSONObject();
		for (Object o : map.keySet()) 
		{
			String[] par = map.get(o);
			jsonObject.put(o, par[0]);
		}
		jsonObject.put("sign", sign);
		logger.info("提现订单信息："+jsonObject);
		try {
			return getHtmlHttpPostJson("https://www.ebatong.com/gateway/authWithdraw.htm",jsonObject.toString());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e);
		}
		return null;
	}
	
	public static JSONObject pay_submit_order(String order,String token,String code,String bank_card_no,String real_name,String cert_no,String card_bind_mobile_phone_no,String subject,String total_fee,String show_url,String exter_invoke_ip,String anti_phishing_key,String default_bank)
	{
		
		Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("sign_type", new String[]{"MD5"});
		map.put("service", new String[]{"create_direct_pay_by_mp"});
		map.put("partner", new String[]{"201511171553101480"});
		map.put("input_charset", new String[]{"UTF-8"});
		map.put("notify_url", new String[]{"http://tyweb.nat123.net/servlet/order/BfNotifyAction"});
		map.put("customer_id", new String[]{order});
		map.put("dynamic_code_token", new String[]{token});
		map.put("dynamic_code", new String[]{code});
		map.put("bank_card_no", new String[]{bank_card_no});
		map.put("real_name", new String[]{real_name});
		map.put("cert_no", new String[]{cert_no});
		map.put("cert_type", new String[]{"01"});
		map.put("out_trade_no", new String[]{order});
		map.put("card_bind_mobile_phone_no", new String[]{card_bind_mobile_phone_no});
		map.put("subject", new String[]{subject});
		map.put("total_fee", new String[]{total_fee});
		map.put("body", new String[]{subject});
		map.put("show_url", new String[]{show_url});
		map.put("subject", new String[]{subject});
		map.put("pay_method", new String[]{""});
		map.put("exter_invoke_ip", new String[]{exter_invoke_ip});
		map.put("anti_phishing_key", new String[]{anti_phishing_key});
		map.put("extra_common_param", new String[]{""});
		map.put("extend_param", new String[]{""});
		map.put("default_bank", new String[]{default_bank});
		
		String key = "01AKC7JZG2G12MOC1D2FNQAUVIWE9Qlhvwii"; // 商户加密字符�?
		String paramStr = UrlHelper.sortParamers(map);
        System.out.println(paramStr);
        String plaintext = TimestampUtils.mergePlainTextWithMerKey(paramStr, key);
		
		
		String orderno = "LZB"+System.currentTimeMillis();
		String str = plaintext;
		System.out.println(str);
		String sign = Md5Encrypt.encrypt(str);
	
		System.out.println(sign+"===="+Md5Encrypt.encrypt(str));
		//String jsonstr = "{\"cert_type\":\"01\",\"cert_no\":\"430923198710231111\",\"sign_type\":\"MD5\",\"input_charset\":\"UTF-8\",\"card_no\":\"6212264000004800872\",\"out_trade_no\":\""+orderno+"\",\"bank_code\":\"ICBC_D_B2C\",\"card_bind_mobile_phone_no\":\"13243888699\",\"sign\":\""+sign+"\",\"amount\":\"2.00\",\"real_name\":\"龚文书\",\"service\":\"ebatong_mp_dyncode\",\"partner\":\"201206281102516718\",\"customer_id\":\""+orderno+"\"}";
		JSONObject jsonObject = new JSONObject();
		for (Object o : map.keySet()) 
		{
			String[] par = map.get(o);
			jsonObject.put(o, par[0]);
		}
		jsonObject.put("sign", sign);
		System.out.println(jsonObject);
		try {
			return getHtmlHttpPostJson("https://www.ebatong.com/mobileFast/pay.htm",jsonObject.toString());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	public static String getTimestamp() throws Exception {
		String key = "01AKC7JZG2G12MOC1D2FNQAUVIWE9Qlhvwii"; // 商户加密字符�?
		
		// String ask_for_time_stamp_gateway = "https://www.ebatong.com/gateway.htm"; // ebatong商户网关
		String ask_for_time_stamp_gateway = "http://www.ebatong.com/gateway.htm"; // ebatong商户网关
		String service = "query_timestamp"; // 服务名称：请求时间戳
		String partner = "201511171553101480"; // 合作者商户ID
		String input_charset = "UTF-8"; // 字符�?
		String sign_type = "MD5"; // 摘要签名算法
		
		// 请求参数排序
		Map<String, String[]> params = new HashMap<String, String[]>();
		params.put("service",new String[]{service});
		params.put("partner",new String[]{partner});
		params.put("input_charset",new String[]{input_charset});
		params.put("sign_type",new String[]{sign_type});
		String paramStr = UrlHelper.sortParamers(params);
		
		String plaintext = TimestampUtils.mergePlainTextWithMerKey(paramStr, key);
		
		// 加密(MD5加签)，默认已取UTF-8字符集，如果�?��更改，可调用Md5Encrypt.setCharset(inputCharset)
		String sign = Md5Encrypt.encrypt(plaintext); 
		
		// 拼接请求
		String url = ask_for_time_stamp_gateway + "?" + paramStr + "&sign=" + sign;
		
		// 通过HttpClient获取响应字符�?
		HttpClient httpClient = new HttpClient();
		HttpMethod method = new GetMethod(url);
		
		String askForTimestampResponseString = null;
		try {
			httpClient.executeMethod(method);
			
			// 如果响应码为200，从响应中获取响应字符串
			if (method.getStatusCode() == 200) {
				
				askForTimestampResponseString = method.getResponseBodyAsString();
				
				System.out.println(askForTimestampResponseString);
			}
			
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			method.releaseConnection(); // 释放连接
		}
		
		// 验证时间戳有效�?
		TimestampResponseResult result = TimestampResponseParser.parse(askForTimestampResponseString);
		
		String timestamp=null;
		
		if (result.isSuccess()) {
			timestamp = result.getTimestamp();
			String resultMd5 = result.getResultMd5();
			String timestampMergeWithMerKey = TimestampUtils.mergePlainTextWithMerKey(timestamp, key);
			System.out.println("时间戳：" + timestamp);
			System.out.println("有效性：" + resultMd5.equals(Md5Encrypt.encrypt(timestampMergeWithMerKey)));
		}
		
		return timestamp;
	}
	
	public static void main(String[] args) throws Exception {
		String card_bind_mobile_phone_no = "18676396113";
		String bank_code = "PINGAN_D_B2C";
		String amount = "0.01";
		String card_no = "6222980022262242";
		String cert_no = "230522198801281098";
		String real_name = "窦红勋";
		String orderno = "LZB1447928958009";//"LZB"+System.currentTimeMillis();
//		JSONObject yzm = pay_rz_yzm(card_bind_mobile_phone_no, bank_code, amount, card_no, cert_no, orderno, real_name);
//		System.out.println(yzm);
		
		
		//pay_submit_order(orderno, "201511191832288178", "813073", card_no, real_name, cert_no, card_bind_mobile_phone_no, "绿洲宝定存一个月，金额：0.01", amount, "http://www.lvzbao.com", "127.0.0.1", getTimestamp(), bank_code);
	
	}

}