package com.util;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;






public class SendMsg {
	
	private static Logger logger = Logger.getLogger(SendMsg.class);
	/**
	 * 服务http地址
	 */
	private static String BASE_URI = "http://yunpian.com";
	
	/**
	 * 服务版本号
	 */
	private static String VERSION = "v1";
	/**
	 * 编码格式
	 */
	private static String ENCODING = "UTF-8";
	/**
	 * 查账户信息的http地址
	 */
	private static String URI_GET_USER_INFO = BASE_URI + "/" + VERSION + "/user/get.json";
	/**
	 * 通用发送接口的http地址
	 */
	private static String URI_SEND_SMS = BASE_URI + "/" + VERSION + "/sms/send.json";
	/**
	 * 模板发送接口的http地址
	 */
	private static String URI_TPL_SEND_SMS = BASE_URI + "/" + VERSION + "/sms/tpl_send.json";
	/**
	 * 取账户信息
	 * @return json格式字符串
	 * @throws IOException 
	 */
	public static String getUserInfo(String apikey) throws IOException{
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(URI_GET_USER_INFO+"?apikey="+apikey);
		HttpMethodParams param = method.getParams();
		param.setContentCharset(ENCODING);
		client.executeMethod(method);
		return method.getResponseBodyAsString();
	}
	/**
	 * 发短信
	 * @param apikey apikey
	 * @param text　短信内容　
	 * @param mobile　接受的手机号
	 * @return json格式字符串
	 * @throws IOException 
	 */
	public static String sendSms(String apikey, String text, String mobile) throws IOException{
		HttpClient client = new HttpClient();
		NameValuePair[] nameValuePairs = new NameValuePair[3];
		nameValuePairs[0] = new NameValuePair("apikey", apikey);
		nameValuePairs[1] = new NameValuePair("text", text);
		nameValuePairs[2] = new NameValuePair("mobile", mobile);
		PostMethod method = new PostMethod(URI_SEND_SMS);
		method.setRequestBody(nameValuePairs);
		HttpMethodParams param = method.getParams();
		param.setContentCharset(ENCODING);
		client.executeMethod(method);
		return method.getResponseBodyAsString();
	}
	
	/**
	 * 通过模板发送短信
	 * @param apikey apikey
	 * @param tpl_id　模板id
	 * @param tpl_value　模板变量值　
	 * @param mobile　接受的手机号
	 * @return json格式字符串
	 * @throws IOException 
	 */
	public static boolean tplSendSms(long tpl_id, String tpl_value, String mobile) throws IOException{
		HttpClient client = new HttpClient();
		NameValuePair[] nameValuePairs = new NameValuePair[4];
		nameValuePairs[0] = new NameValuePair("apikey", WebConstants.APIKEY);
		nameValuePairs[1] = new NameValuePair("tpl_id", String.valueOf(tpl_id));
		nameValuePairs[2] = new NameValuePair("tpl_value", tpl_value);
		nameValuePairs[3] = new NameValuePair("mobile", mobile);
		PostMethod method = new PostMethod(URI_TPL_SEND_SMS);
		method.setRequestBody(nameValuePairs);
		HttpMethodParams param = method.getParams();
		param.setContentCharset(ENCODING);
		client.executeMethod(method);
		net.sf.json.JSONObject object = net.sf.json.JSONObject.fromObject(method.getResponseBodyAsString());
		logger.error(object);
		if(object.getString("code").equals("0"))
		{
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) throws IOException {

	/*	String mobile = "13543281052";
		long tpl_id=2;
		//设置对应的模板变量值
		String tpl_value="#code#=1234&#company#=绿洲宝";
		//模板发送的调用示例
		SendMsg.tplSendSms(tpl_id, tpl_value, mobile);*/
		SendMsg.tplSendSms(1524196,"#name#=纪煜贤&#product#=定存6个月&#money#=7000.0","13802463146");
		//SendMsg.tplSendSms(1130137,"#name#="+rz.getString("realname"), user.getString("mobilephone"))
	}
}
