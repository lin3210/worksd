package com.task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.thinkive.web.base.BaseAction;

import com.alibaba.fastjson.JSON;
import com.lianpay.api.util.TraderRSAUtil;
import com.lianpay.bean.PaymentResponseBean;
import com.lianpay.bean.QueryPaymentRequestBean;
import com.lianpay.bean.QueryPaymentResponseBean;
import com.lianpay.constant.PaymentConstant;
import com.lianpay.constant.PaymentStatusEnum;
import com.lianpay.constant.RetCodeEnum;
import com.lianpay.util.HttpUtil;
import com.lianpay.util.SignUtil;
import com.service.AotuZDSHALLService;
import com.service.JBDLLpayService;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.util.SessionHelper;
import com.thinkive.timerengine.Task;
import com.thinkive.web.base.ActionResult;
import com.util.PartnerRequestDemo;
import com.util.ZmxyGSUtils;
/**
 * 自动审核借款
 * @author Administrator
 *
 */
public class AotuZDHKTask implements Task
{

	private static Logger logger = Logger.getLogger(AotuZDHKTask.class);
	private static AotuZDSHALLService aotuZDSHAllService = new AotuZDSHALLService();
	String url253 = "https://sms.253.com/msg/send";// 应用地址
	String un253 = "N5186211";// 账号
	String pw253 = "fA5nv7cI9";// 密码	
	String rd253 = "0";// 是否需要状态报告，需要1，不需要0
	String ex253 = null;// 扩展码	
	long time = 1000*60*3;
	static String url ="http://www.shandkj.com/";
	@Override
	public void execute() 
	{
		//查询所有待审核借款项目
		List<DataRow> list = aotuZDSHAllService.getUserRecThreeInfo2();
		logger.info("进入签约");
    	JSONObject jsonObject = new JSONObject();
    	Date  date = new Date();
		Calendar calendar =Calendar.getInstance();
		SimpleDateFormat fmtrq  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat fmtrq1  = new SimpleDateFormat("yyyy-MM-dd");
		Date datenow = new Date();
		String timerq = fmtrq1.format(datenow);
       
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timesj = sdf.format(datenow);
	   
			
		for (DataRow dataRow : list) 
		{
		  try {
			String userid =	dataRow.getString("userid");//借款用户id
		    String jkid = dataRow.getString("id"); //借款id ;
		    String sjsh_money = aotuZDSHAllService.getSjsh(jkid);
		    String time=sdf.format(dataRow.getObject("createtime"));
		    String cardno = dataRow.getString("cardno");
		    String cardname= dataRow.getString("cardusername");
		    String noagree = dataRow.getString("noagree");
		    String merchOrderId = "KK" + System.currentTimeMillis();
		    BankCardAgreeBean bankCardAgreeBean = new BankCardAgreeBean();
	        bankCardAgreeBean.setOid_partner("201707111001880525");
	        bankCardAgreeBean.setUser_id(userid);
	        bankCardAgreeBean.setSign_type("RSA");
	        bankCardAgreeBean.setApi_version("1.0");
	        bankCardAgreeBean.setRepayment_no(timesj);
	        bankCardAgreeBean.setRepayment_plan("{\"repaymentPlan\":[{\"date\":\""+timerq+"\",\"amount\":\""+sjsh_money+"\"}]}");
	        bankCardAgreeBean.setSms_param("{\"contract_type\":\"粒粒贷或者人人闪贷\",\"contact_way\":\"0755-83201440\"}");
	        bankCardAgreeBean.setPay_type("D");
	        bankCardAgreeBean.setNo_agree(noagree);
	        bankCardAgreeBean.setSign(SignUtil.genRSASign(JSON.parseObject(JSON.toJSONString(bankCardAgreeBean))));
	        String reqJson = JSON.toJSONString(bankCardAgreeBean);
	        String response1 = YTHttpHandler.getInstance().doRequestPostString(reqJson,"https://repaymentapi.lianlianpay.com/agreenoauthapply.htm");
	        PaymentResponseBean paymentResponseBean = JSON.parseObject(response1, PaymentResponseBean.class);
	        if (paymentResponseBean.getRet_code().equals("0000")) {
	    		       BankCardPayBean bankCardPayBean = new BankCardPayBean();
	    		       bankCardPayBean.setNo_order(merchOrderId);
	    		       bankCardPayBean.setDt_order(sdf.format(date));
	    		       bankCardPayBean.setMoney_order(sjsh_money);
	    		       bankCardPayBean.setBusi_partner("101001");
	    		       bankCardPayBean.setName_goods("KK");
	    		       bankCardPayBean.setSchedule_repayment_date(timerq);
	    		       bankCardPayBean.setCard_no(cardno);	
	    		       bankCardPayBean.setAcct_name(cardname);
	    		       bankCardPayBean.setRepayment_no(timesj);
	    		       bankCardPayBean.setPay_type("D");
	    		       bankCardPayBean.setNo_agree(noagree);
	    		       bankCardPayBean.setInfo_order("扣款");
			    		// 填写商户自己的接收付款结果回调异步通知
	    		       bankCardPayBean.setNotify_url(url+"servlet/current/JBDLLpayAction?function=ZDPaymentResults");
	    		       bankCardPayBean.setOid_partner("201707111001880525");
			    		// paymentRequestBean.setPlatform("test.com");
	    		       bankCardPayBean.setApi_version("1.0");
	    		       bankCardPayBean.setSign_type("RSA");
	    		       bankCardPayBean.setRisk_item("{\"frms_ware_category\":\"2010\",\"user_info_mercht_userno\":\""+userid+"\",\"user_info_dt_register\":\""+time+"\",\"user_info_bind_phone\":\""+dataRow.getString("mobilephone")+"\",\"user_info_identify_state\":\"1\",\"user_info_identify_type\":\"1\",\"user_info_full_name\":\""+cardname+"\",\"user_info_id_no\":\""+dataRow.getString("remark")+"\"}");
	    		       bankCardPayBean.setUser_id(userid);
			    		// 用商户自己的私钥加签
	    		       bankCardPayBean.setSign(SignUtil.genRSASign(JSON.parseObject(JSON.toJSONString(bankCardPayBean))));
			    	   String jsonStr1 = JSON.toJSONString(bankCardPayBean);
			    		logger.info("实时付款请求报文：" + jsonStr1);
			    		String response3 = YTHttpHandler.getInstance().doRequestPostString(jsonStr1, "https://repaymentapi.lianlianpay.com/bankcardrepayment.htm");			    	
			    		logger.info("付款接口返回响应报文：" + response3);
			    		if (StringUtils.isEmpty(response3)) {
			    			// 出现异常时调用订单查询，明确订单状态，不能私自设置订单为失败状态，以免造成这笔订单在连连付款成功了，而商户设置为失败
			    			queryZDPaymentAndDealBusiness(bankCardPayBean.getNo_order());
			    		} else {
			    			PaymentResponseBean paymentResponse = JSON.parseObject(response3, PaymentResponseBean.class);
			    			// 对返回0000时验证签名
			    			if (paymentResponse.getRet_code().equals("0000")) {
			    				
			    					DataRow withdraw = new DataRow();
			    					withdraw.set("name", dataRow.getString("cardusername"));
			    					withdraw.set("cellphone", dataRow.getString("mobilephone"));
			    					withdraw.set("acount", dataRow.getString("cardno"));
			    					withdraw.set("sum", sjsh_money);
			    					withdraw.set("applytime", new Date());
			    					withdraw.set("checkTime", new Date());
			    					withdraw.set("userid", userid);
			    					withdraw.set("status", 6);
			    					withdraw.set("remark", "自动扣款");
			    					withdraw.set("versoin", jkid);
			    					withdraw.set("orderid", merchOrderId);
			    					DataRow user2 = aotuZDSHAllService.getUser(userid); 	
			    					aotuZDSHAllService.userKk(withdraw, user2, sjsh_money);
			    					DataRow addorder = new DataRow();
			    					int orderpaytype = 4;
			    			    	String remark = "扣款";
			    					addorder.set("userid", userid);
		    					    addorder.set("rechargetime", new Date());
		    					    addorder.set("rechargetype", 33);
		    					    addorder.set("rechargemoney",sjsh_money);
		    					    addorder.set("result", 1);
		    					    addorder.set("rzcode", "0000");
		    					    addorder.set("paynumber", merchOrderId);
		    					    addorder.set("rechargenumber", merchOrderId);			     
		    					    addorder.set("remark", remark);
		    					    addorder.set("ordertype", 1);
		    					    addorder.set("resultInfo", "SUCCESS");
		    					    addorder.set("orderpaytype", orderpaytype);			 
		    					    aotuZDSHAllService.addOrder(addorder);	
			    					DataRow jkxm2 = new DataRow(); 
								    jkxm2.set("userid", userid) ;
								    jkxm2.set("id", jkid);
								    jkxm2.set("jksfwc", 1) ;
								    jkxm2.set("sfyhw", 1) ;
								    jkxm2.set("hk_time", new Date()) ;
								    aotuZDSHAllService.updateZDHK(jkxm2);		
			    		    		DataRow row3 =  new DataRow();	
			    	                row3.set("userid",userid);
			    		            row3.set("title", "还款通知");
			    		            row3.set("neirong","亲爱的"+dataRow.getString("cardusername") +"本期借款您已经完成还款，感谢您对【粒粒贷】的支持。");		             
			    		            row3.set("fb_time", new Date());				
			    		            aotuZDSHAllService.insertUserMsg(row3);	
			    		            jsonObject.put("error", 1);	
			    		 	        jsonObject.put("msg", "成功");
			    		 	        logger.info("成功");
			    		 	       String userName =dataRow.getString("username");
			   			        
								   userName =userName.substring(0,3);	
								    String appName ="粒粒贷";
								    if(userName.equals("LLD")){
								    	appName="粒粒贷";					    	
								    }else if(userName.equals("JBD")){					    	
								    	appName="人人闪贷";
								    }					   	   	
							     String msg253 ="【"+appName+"】亲爱的"+dataRow.getString("cardusername") +"本期借款您已经完成还款，感谢您对【"+appName+"】的支持。" ; ;
								
							     String returnString = HttpSender.batchSend(url253, un253, pw253,dataRow.getString("mobilephone"), msg253, rd253, ex253);  
			    				
			    			
			    				// 已生成连连支付单，付款处理中（交易成功，不是指付款成功，是指跟连连流程正常），商户可以在这里处理自已的业务逻辑（或者不处理，在异步回调里处理逻辑）,最终的付款状态由异步通知回调告知
			    			} else if (paymentResponseBean.getRet_code().equals("4002")
			    					|| paymentResponseBean.getRet_code().equals("4004")) {
			    				// 当调用付款接口返回4002，4003，4004,会同时返回验证码，用于确认付款接口
			    				// 对于疑似重复订单，需先人工审核这笔订单是否正常的付款请求，而不是系统产生的重复订单，确认后再调用确认付款接口或者在连连商户站后台操作疑似订单，api不调用确认付款接口
			    				// 对于疑似重复订单，也可不做处理，
			    				//解析"confirm_code":"306764","no_order":"1494411328605"			    				
			    			    com.alibaba.fastjson. JSONObject reqObject1 = (com.alibaba.fastjson.JSONObject) JSON.parse(response3);
			    				DataRow withdraw = new DataRow();
		    					withdraw.set("name", dataRow.getString("cardusername"));
		    					withdraw.set("cellphone", dataRow.getString("mobilephone"));
		    					withdraw.set("acount", dataRow.getString("cardno"));
		    					withdraw.set("sum", sjsh_money);
		    					withdraw.set("applytime", new Date());
		    					withdraw.set("checkTime", new Date());
		    					withdraw.set("userid", userid);
		    					withdraw.set("status", 10);
		    					withdraw.set("remark", "扣款");
		    					withdraw.set("versoin", jkid);
		    					withdraw.set("suborder",reqObject1.get("confirm_code"));
		    					withdraw.set("orderid", merchOrderId);
		    					DataRow user2 = aotuZDSHAllService.getUser(userid); 
		    					aotuZDSHAllService.userKk(withdraw, user2, sjsh_money);
			    				jsonObject.put("error", -6);
		    					jsonObject.put("msg", "疑似重复订单或收款银行卡和姓名不一致");
		    					
			    				// TODO
			    			} else if (RetCodeEnum.isNeedQuery(paymentResponseBean.getRet_code())) {
			    				// 出现1002，2005，4006，4007，4009，9999这6个返回码时（或者对除了0000之后的code都查询一遍查询接口）调用订单查询，明确订单状态，不能私自设置订单为失败状态，以免造成这笔订单在连连付款成功了，而商户设置为失败
			    				// 第一次测试对接时，返回{"ret_code":"4007","ret_msg":"敏感信息解密异常"},可能原因报文加密用的公钥改动了,demo中的公钥是连连公钥，商户生成的公钥用于上传连连商户站用于连连验签，生成的私钥用于加签
			    				queryZDPaymentAndDealBusiness(bankCardPayBean.getNo_order());
			    				jsonObject.put("error", -6);
		    					jsonObject.put("msg", "放款支付处理异常02,请联系技术处理！");
		    									
		    					
			    			} else {
			    				// 返回其他code时，可将订单置为失败
			    				jsonObject.put("error", -6);
		    					jsonObject.put("msg", "放款支付处理异常03,请联系技术处理！");
		    					logger.info("失败");
		    								    				
			    			}
			    		}		         
	    		
	        }	
		  
	    
		  }catch(Exception e) {
			  
			  e.printStackTrace() ;
		  }
	}
}
		// 异常时，先查询订单状态，再根据订单状态处理业务逻辑
			public static void queryZDPaymentAndDealBusiness(String orderNo) {
				
				DataRow   withdrawInfo = aotuZDSHAllService.getWithdraw(orderNo);
				// 连连内部测试环境数据
				QueryPaymentRequestBean queryRequestBean = new QueryPaymentRequestBean();
				queryRequestBean.setNo_order(orderNo);
				queryRequestBean.setOid_partner("201707111001880525");
				// queryRequestBean.setPlatform("test.com");
				queryRequestBean.setApi_version("1.0");
				queryRequestBean.setSign_type("RSA");
				queryRequestBean.setSign(SignUtil.genRSASign(JSON.parseObject(JSON.toJSONString(queryRequestBean))));
				String queryResult = HttpUtil.doPost("https://repaymentapi.lianlianpay.com/bankcardrepayment.htm",
						JSON.parseObject(JSON.toJSONString(queryRequestBean)), "UTF-8");
				logger.info("实时付款查询接口响应报文：" + queryResult);
				if (StringUtils.isEmpty(queryResult)) {
					// 可抛异常，查看原因				
					withdrawInfo.set("status","8");
					withdrawInfo.set("checkTime", new Date());
					withdrawInfo.set("remarkResult","实时付款查询接口响应异常");
					aotuZDSHAllService.updateWithRawInfo(withdrawInfo);				
					return;
				}
				QueryPaymentResponseBean queryPaymentResponseBean = JSON.parseObject(queryResult,
						QueryPaymentResponseBean.class);

				// 先对结果验签
				boolean signCheck = TraderRSAUtil.checksign("MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBALPoMsWGg5gIyk/z"+
						"Wa4wcMok3Z4fW0A1ZBnPLY3wmJ7FCzyxoTGpCIvWXtT4A73HPiZN7Qzqj8HxM0lB"+
						"/jyy+WHtuyjSwG2cKO+IGTGI8LAV58gnV0M9PrzOBOKE6lzQF7a3L5R9u4qS7NHH"+
						"xGTAbYAdffzyI479LawQgFE37BVBAgMBAAECgYBPOhlpzUQUZwKZVOSQhjqVesiy"+
						"AsMPsrODfi5kjKjZepLpRpxjHzppQp1+kj4rjBu9iKG1B3MJiKv6Pfq1Rmf1z5B0"+
						"g+ycsuvcu8WFGVmIPdNhsiybf48aKf+1JB1A1+zFXcrsiLNGd475PCQNMcfVvq5u"+
						"6E+ir2yC6tPYY4U8hQJBAOrFoiT8Y9+go6QOsxihOum/tQ51fC4IwWoWC0tXKhxt"+
						"GZI/SgAiljqCKst+kDq6wTlyE84dxZ9Oi+jk+NTtOwMCQQDELJUEs1SBtbSfkge7"+
						"Nnlowqq+kFpa10T+WoyTYZsOlqLDT80kRLRrhxwB9v+kLPZOfrqcRQ6bLhNKQWsS"+
						"oHlrAkAP3a1YjIn/We7VLn0iA/tkQqVsxbnPrp3LmpPG0qww4ZqhzI8mtS+r4pIb"+
						"0IDUxzw5sqDuBAsP+hHwelDqquGbAkAUnv8XHGawr9IJyAbqBgLjITtjhrcIv4Iw"+
						"HoKSZ3suIGWBlFzjCBnTB8PI7RbYQiWuAKJLFPNBGqnKb2/66EV7AkBxlcWYW0+8"+
						"dqwun9gtueYYtnQWD/+aKD81KK2/MttY9+KVK8f3ksw8D0cGy85AoyzrivQEWNdd"+
						"4Pvqll9NWYhS",
						SignUtil.genSignData(JSON.parseObject(queryResult)), queryPaymentResponseBean.getSign());
				if (!signCheck) {
					// 传送数据被篡改，可抛出异常，再人为介入检查原因
					withdrawInfo.set("status","8");
					withdrawInfo.set("checkTime", new Date());
					withdrawInfo.set("remarkResult","返回结果验签异常,可能数据被篡改");	
					aotuZDSHAllService.updateWithRawInfo(withdrawInfo);	
					return;
				}
				if (queryPaymentResponseBean.getRet_code().equals("0000")) {
					PaymentStatusEnum paymentStatusEnum = PaymentStatusEnum
							.getPaymentStatusEnumByValue(queryPaymentResponseBean.getResult_pay());
					// TODO商户根据订单状态处理自已的业务逻辑
					switch (paymentStatusEnum) {
					case PAYMENT_APPLY:
						// 付款申请，这种情况一般不会发生，如出现，请直接找连连技术处理
						withdrawInfo.set("status","8");
						withdrawInfo.set("checkTime", new Date());
						withdrawInfo.set("remarkResult","联系连连技术处理");
						aotuZDSHAllService.updateWithRawInfo(withdrawInfo);	
						break;
					case PAYMENT_CHECK:
						// 复核状态 TODO
						// 返回4002，4003，4004时，订单会处于复核状态，这时还未创建连连支付单，没提交到银行处理，如需对该订单继续处理，需商户先人工审核这笔订单是否是正常的付款请求，没问题后再调用确认付款接口
						// 如果对于复核状态的订单不做处理，可当做失败订单
						withdrawInfo.set("status","8");
						withdrawInfo.set("checkTime", new Date());
						withdrawInfo.set("remarkResult","需商户先人工审核这笔订单是否是正常的付款请求");
						aotuZDSHAllService.updateWithRawInfo(withdrawInfo);	
						break;
					case PAYMENT_SUCCESS:
						// 成功 TODO
						withdrawInfo.set("status","8");
						withdrawInfo.set("checkTime", new Date());
						withdrawInfo.set("remarkResult","SUCCESSS");
						aotuZDSHAllService.updateWithRawInfo(withdrawInfo);	
						break;
					case PAYMENT_FAILURE:
						// 失败 TODO
						withdrawInfo.set("status","8");
						withdrawInfo.set("checkTime", new Date());
						withdrawInfo.set("remarkResult","FAILURE");
						aotuZDSHAllService.updateWithRawInfo(withdrawInfo);	
						break;
					case PAYMENT_DEALING:
						// 处理中 TODO
						withdrawInfo.set("status","8");
						withdrawInfo.set("checkTime", new Date());
						withdrawInfo.set("remarkResult","处理中");
						aotuZDSHAllService.updateWithRawInfo(withdrawInfo);
						break;
					case PAYMENT_RETURN:
						withdrawInfo.set("status","8");
						withdrawInfo.set("checkTime", new Date());
						withdrawInfo.set("remarkResult","退款");
						aotuZDSHAllService.updateWithRawInfo(withdrawInfo);
						// 退款 TODO
						// 可当做失败（退款情况
						// 极小概率下会发生，个别银行处理机制是先扣款后打款给用户时再检验卡号信息是否正常，异常时会退款到连连商户账上）
						break;
					case PAYMENT_CLOSED:
						// 关闭 TODO 可当做失败 ，对于复核状态的订单不做处理会将订单关闭
						withdrawInfo.set("status","8");
						withdrawInfo.set("checkTime", new Date());
						withdrawInfo.set("remarkResult","交易关闭");
						aotuZDSHAllService.updateWithRawInfo(withdrawInfo);
						break;
					default:
						break;
					}
				} else if (queryPaymentResponseBean.getRet_code().equals("8901")) {
					// 订单不存在，这种情况可以用原单号付款，最好不要换单号，如换单号，在连连商户站确认下改订单是否存在，避免系统并发时返回8901，实际有一笔订单
					withdrawInfo.set("status","8");
					withdrawInfo.set("checkTime", new Date());
					withdrawInfo.set("remarkResult","订单不存在");
					aotuZDSHAllService.updateWithRawInfo(withdrawInfo);
				} else {
					// 查询异常（极端情况下才发生,对于这种情况，可人工介入查询，在连连商户站查询或者联系连连客服，查询订单状态）
					withdrawInfo.set("status","8");
					withdrawInfo.set("checkTime", new Date());
					withdrawInfo.set("remarkResult","查询异常");
					aotuZDSHAllService.updateWithRawInfo(withdrawInfo);
					logger.error("查询异常");
				}

		}
		
		
	}
    
	
	
	

