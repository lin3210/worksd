package com.util;


public class HttpSenderTest {
	public static void main(String[] args) {

		String url = "https://sms.253.com/msg/send";// 应用地址
		String un = "N4064842";// 账号
		String pw = "1O9AgpU3Y257ae";// 密码
		String phone = "13543281052";// 手机号码，多个号码使用","分割
		//String msg = "【光速贷】您正在注册光速贷，验证码为{code}，3分钟内有效。任何人向您索要验证码均为诈骗，请勿泄露。";// 短信内容
		//String msg = "【闪贷侠】您正在注册闪贷侠，验证码为123456，3分钟内有效。任何人向您索要验证码均为诈骗，请勿泄露。";// 短信内容
		String msg ="【绿洲闪贷】尊敬的李磊先生，您的借款将于04/07到期，还款金额为500.00元，请及时登录绿洲闪贷APP还款，以免影响征信记录，谢谢!";
		//String msg="【光速贷】尊敬的朱武智先生，您的借款将于0217-02-20到期，还款金额为500.00元，请及时登录光速贷APP还款，以免影响征信记录，谢谢！。"
		String rd = "0";// 是否需要状态报告，需要1，不需要0
		String ex = null;// 扩展码

		
		try {
			String returnString = HttpSender.batchSend(url, un, pw, phone, msg, rd, ex);
			/*returnString=returnString.substring(returnString.indexOf(","),3);*/
			System.out.println(returnString);
		    System.out.println(returnString.length());
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
}