package root.api;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.project.constant.IConstants;
import com.project.service.account.JBDUserService;
import com.project.service.account.JBDcms3Service;
import com.project.service.account.MofaUserService;
import com.project.utils.MemCachedUtil;
import com.shove.security.Encrypt;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.util.StringHelper;
import com.thinkive.web.base.ActionResult;
import com.thinkive.web.base.BaseAction;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import root.SendFTP;
import root.SendMsg;
import root.current.Base64;
import root.current.OLVSacombank;
import root.current.VMGSendMsg;
import root.img.WebConstants;
import root.order.UserMoneyBase;
import root.tool.SendMsgTYH;

/**
 * 使用M88 API 修改为OCEAN vay
 */
public class ApiAction extends BaseAction {
	private static Logger logger = Logger.getLogger(ApiAction.class);
	private static JBDUserService jdbUserService = new JBDUserService();
	private static JBDcms3Service jbdcms3Service = new JBDcms3Service();
	private static UserMoneyBase  userMoneyBase = new UserMoneyBase();
	private static MofaUserService mofaUserService = new MofaUserService();
	String url = "https://sms.253.com/msg/send";// 应用地址https://sms.253.com/msg/send
	String un253 = "N5186211";// account number: N5186211
	String pw253 = "fA5nv7cI9";// password: fA5nv7cI9
	String rd253 = "0";// 这里�??�??1，不�??�??0 0
	String ex253 = null;// 扩展�??
	String encrypt = "a9uh89j9OI8sj7klhjsf9H093kfldkj1h8K";
	long time = 1000 * 60 * 3;
	public static boolean isViettelPhone(String phone){
 		if("096".equals(phone.substring(0, 3))){
 			return true;
 		}else if("097".equals(phone.substring(0, 3))){
 			return true;
 		}else if("098".equals(phone.substring(0, 3))){
 			return true;
 		}else if("086".equals(phone.substring(0, 3))){
 			return true;
 		}else if("032".equals(phone.substring(0, 3))){
 			return true;
 		}else if("033".equals(phone.substring(0, 3))){
 			return true;
 		}else if("034".equals(phone.substring(0, 3))){
 			return true;
 		}else if("035".equals(phone.substring(0, 3))){
 			return true;
 		}else if("036".equals(phone.substring(0, 3))){
 			return true;
 		}else if("037".equals(phone.substring(0, 3))){
 			return true;
 		}else if("038".equals(phone.substring(0, 3))){
 			return true;
 		}else if("039".equals(phone.substring(0, 3))){
 			return true;
 		}else if("0162".equals(phone.substring(0, 4))){
 			return true;
 		}else if("0163".equals(phone.substring(0, 4))){
 			return true;
 		}else if("0164".equals(phone.substring(0, 4))){
 			return true;
 		}else if("0165".equals(phone.substring(0, 4))){
 			return true;
 		}else if("0166".equals(phone.substring(0, 4))){
 			return true;
 		}else if("0167".equals(phone.substring(0, 4))){
 			return true;
 		}else if("0168".equals(phone.substring(0, 4))){
 			return true;
 		}else if("0169".equals(phone.substring(0, 4))){
 			return true;
 		}else{
 			return false;
 		}
 	}  
	public ActionResult doGetHome() throws IOException {
		logger.info("request ip: " + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonObj = getRequestJson(request);
		String token = jsonObj.getString("token");

		int userId = jsonObj.getInteger("userId");

		String idEncrypt = Encrypt.MD5(userId + encrypt);
		System.out.println(idEncrypt);
		if (idEncrypt.equals(token)) {

			logger.info("User Id:" + userId);
			DataRow row = new DataRow();
			DataRow userBankCard = new DataRow();
			DataRow userIndentifyInfo = new DataRow();
			DataRow userReferenceInfo = new DataRow();
			DataRow userJobInfo = new DataRow();
			if (userId != 0) {
				DataRow userData = jdbUserService.findUserById(userId + "");
				DataRow repaidChannel = jdbUserService.findUserHKQD(userId + "");
				DataRow rawUserBankCard = jdbUserService.getRZBK(userId);
				DataRow rawUserFinance = jdbUserService.getRZSF(userId);
				DataRow jkData = jdbUserService.getjkMoney(userId + "");
				// 未再借用户标识2019年6月17日lin
			    if(1==userData.getInt("user_mark")) {
			     //把user表中的user_mark更新为2 提交借款;
			        jdbUserService.updateRowUserMark(userData.getInt("id"), 2);
			    }
				/*** 计算方式整理 2019-7-28 lic****/
			    DecimalFormat famt = new DecimalFormat("###,###");
//				DataRow jkDataMax = jdbUserService.getjkMoneyMax(userId +"");
				String maxMoney = "";
//				if(jkDataMax != null) {
//					 maxMoney = jkDataMax.getString("sjsh_money");
//				}
				int sjshmoney = userMoneyBase.getUMBaseMaxLoanMoney(userId);   //最大额度
				int maxCount = userMoneyBase.getUMBaseSuccessfulLoanNum(userId, maxMoney);
				int sjshmoneyshow = userMoneyBase.getUMBaseMaxLoanMoney_showApp(userId);   //最大额度显示

				
				row.set("loanSuccessCount", 0);
				row.set("maxMoney", famt.format(sjshmoney));
				row.set("maxCount", maxCount);
				row.set("maxMoneyShow", famt.format(sjshmoney));
				/*** 计算方式整理 2019-7-28 lic**end**/
				
				
				if (userData != null) {
					String username = userData.getString("username");
					String phone = userData.getString("mobilephone");
					if(isViettelPhone(phone)) {
						row.set("showPopup", "1");
					}else {
						row.set("showPopup", "0");
					}
					//phone = phone.substring(0, 5) + "***" + phone.substring(9, phone.length());
					row.set("userName", username);
					row.set("logCount", userData.getInt("logincount"));

					DataRow rowlogin = new DataRow();
					rowlogin.set("id", userId);
					rowlogin.set("shownofition", "0");
					if ("AND".equals(username.substring(4, 7))) {
						String appcode = jdbUserService.getAppcode(userId);
						if (!TextUtils.isEmpty(appcode) && !"none".equals(appcode)) {
							int appcodede = Integer.parseInt(appcode);
							if (appcodede < 2208 && appcodede != 1001 && userData.getInt("viptype") == 1) {
								rowlogin.set("shownofition", "2");
								rowlogin.set("viptype", "0");
							}
						}
					}

					if (userData.getInt("appflyer") == 1) {
						rowlogin.set("appflyer", "0");
					}
					rowlogin.set("logincount", userData.getInt("logincount") + 1);
					jdbUserService.updateUser(userId + "", rowlogin);

					row.set("cellphone", phone);

					row.set("creditLimit", userData.getString("creditlimit"));
					
					
					//row.set("userjkqx", userData.getString("changejkts"));//1为7天和14天，0为15天和30天
					row.set("userjkqx", 1);//1为7天和14天，0为15天和30天
					
					if (repaidChannel != null) {
						row.set("isRepaid", repaidChannel.getString("hkqd"));
					} else {
						row.set("isRepaid", "0");
					}

					row.set("fifteenDayValue", "0.1955");
					row.set("thirtyDayValue", "0.291");
					row.set("sevenDayValue", "0.0045");
					row.set("fourteenDayValue", "0.009");
					row.set("twentyoneDayValue", "0.0135");
					row.set("twentyeightDayValue", "0.018");

					row.set("values", "{\"7\":[0.009,0.3]}");
					
					if (rawUserBankCard != null) {
						userBankCard.set("cardname", rawUserBankCard.getString("cardusername"));
						if (rawUserBankCard.getString("cardname").contains("@")) {
							String[] bankName = rawUserBankCard.getString("cardname").split("[@]");
							userBankCard.set("bankName", bankName[0]);
							userBankCard.set("bankBranch", bankName[1]);
						} else {
							userBankCard.set("bankName", rawUserBankCard.getString("cardname"));
						}
						if (rawUserBankCard.getString("cardno").length() > 9) {
							userBankCard.set("card",
									"********" + rawUserBankCard.getString("cardno").substring(
											rawUserBankCard.getString("cardno").length() - 4,
											rawUserBankCard.getString("cardno").length()));

						} else {
							userBankCard.set("card",
									"********" + rawUserBankCard.getString("cardno").substring(
											rawUserBankCard.getString("cardno").length() - 4,
											rawUserBankCard.getString("cardno").length()));

						}
					}

					if (rawUserFinance != null) {
						userIndentifyInfo.set("idcard", rawUserFinance.getString("idno"));
						userIndentifyInfo.set("homeAddress", rawUserFinance.getString("homeaddress"));
						userIndentifyInfo.set("birthday", rawUserFinance.getString("age"));
						userIndentifyInfo.set("address", rawUserFinance.getString("address"));
					} else {
						userIndentifyInfo.set("idcard", null);
					}

					if (userData.getInt("yhbd") == 1) {
						userBankCard.set("isUserBankCard", 1);
					} else {
						userBankCard.set("isUserBankCard", 0);
					}
					if (userData.getInt("isshenfen") == 1) {
						userIndentifyInfo.set("IsUserIndentifyInfo", 1);
					} else {
						userIndentifyInfo.set("IsUserIndentifyInfo", 0);
					}
					if (userData.getInt("islianxi") == 1) {
						userReferenceInfo.set("isReferenceInfo", 1);
					} else {
						userReferenceInfo.set("isReferenceInfo", 0);
					}
					if (userData.getInt("isjop") == 1) {
						userJobInfo.set("isReferenceInfo", 1);
					} else {
						userJobInfo.set("isReferenceInfo", 0);
					}
					
				}
				DataRow dataJK = jdbUserService.findUserJKByuserid(userId + "");
				DataRow dataJKSB = jdbUserService.findUserJKByuseridSB(userId + "");
				if(dataJKSB != null) {
					row.set("isFailed", 3);
					/*
					 * String resion=dataJKSB.getString("cl_yj"); if(dataJKSB.getInt("cl02_status")
					 * ==3) { resion = dataJKSB.getString("cl02_yj"); }else if
					 * (dataJKSB.getInt("cl03_status") ==3) { resion =
					 * dataJKSB.getString("cl03_yj"); } row.set("isFailedYY", resion);
					 */
				}
				if (dataJK != null) {

					String cl03 = dataJK.getString("cl03_status");
					String cl02 = dataJK.getString("cl02_status");
					String cl = dataJK.getString("cl_status");

					String yueqTs = dataJK.getString("yuq_ts");
					String jksfwc = dataJK.getString("jksfwc");
					String sjdsMoney = dataJK.getString("sjds_money");
					String sjshMoney = dataJK.getString("sjsh_money");
					String spzt = dataJK.getString("spzt");
					String hkqd = dataJK.getString("hkqd");
					String sfyfk = dataJK.getString("sfyfk");
					String yuqLx = dataJK.getString("yuq_lx");
					String jkMoney = dataJK.getString("jk_money");
					String jk_date = dataJK.getString("jk_date");
					String hk = dataJK.getString("sfyhw");
					String hkyq = dataJK.getString("hkyq_time");
					String hkfq = dataJK.getString("hkfq_time");
					String hkfqcode = dataJK.getString("hkfq_code");
					String hkfqcishu = dataJK.getString("hkfq_cishu");
					if ("1".equals(hkfqcode)) {
						row.set("expireTime", hkfq);
					} else {
						row.set("expireTime", hkyq);
					}
					row.set("loDate", jk_date);
					row.set("expiredDay", yueqTs);
					row.set("isLoSuccess", jksfwc);
					row.set("finalActualMoney", sjdsMoney);
					row.set("finalMoney", sjshMoney);
					int yanqi15 = 0;
					if(Integer.parseInt(sjshMoney.replaceAll(",", "").replace(".", ""))<=1000000) {
						yanqi15 = Integer.parseInt(sjshMoney.replaceAll(",", "").replace(".", "")) * 3 / 10;
					}else {
						yanqi15 = Integer.parseInt(sjshMoney.replaceAll(",", "").replace(".", "")) * 2 / 10;
					}
					int yanqi30 = Integer.parseInt(sjshMoney.replaceAll(",", "").replace(".", "")) * 3 / 10;
					if(hkfqcishu.length()>=2 || Integer.parseInt(yueqTs)>15) {
						row.set("expiredState", 0);
					}else {
						row.set("expiredState", 1);
					}
					row.set("expired15", famt.format(yanqi15));
					row.set("expired30", famt.format(yanqi30));
					
					row.set("isUploadedVideo", spzt);
					row.set("isLoComeToAccount", sfyfk);
					row.set("expiredInterestFee", yuqLx);
					row.set("loValue", jkMoney);
					row.set("isConfirmRepaidDoneYet", hk);
					
					if ("1".equals(cl) && "1".equals(cl02) && "0".equals(cl03) && "0".equals(spzt)) {
						row.set("iscode", 3);// 上传视频
					} else if ("1".equals(cl) && "1".equals(cl02) && "1".equals(cl03) && "1".equals(spzt)
							&& "1".equals(sfyfk)) {
						if ("0".equals(hkqd)) {
							row.set("iscode", 4);// 还没上传还款凭证
						} else {
							row.set("iscode", 5);// 已经上传还款凭证
						}
					} else if ("1".equals(cl) && "1".equals(cl02) && "1".equals(cl03) && "1".equals(spzt) && "2".equals(sfyfk) ) {
						row.set("iscode", 6);// 等待放款
					}else if("1".equals(cl) && "1".equals(cl02) && "1".equals(cl03) && "1".equals(spzt) && "3".equals(sfyfk)) {
						row.set("iscode", 2);//重新提交借款
					} else {
						row.set("iscode", 7);// 审核中
					}
				} else {
					row.set("iscode", 2);// 提交借款
				}
				row.set("versionCode",9);
				row.set("userBankCard", userBankCard);
				row.set("userIndentifyInfo", userIndentifyInfo);
				row.set("referenceInfo", userReferenceInfo);
				row.set("userJobInfo", userJobInfo);
				row.set("fanpg_url", "");
				JSONArray arrayBank = new JSONArray();
				
				JSONObject obj = new JSONObject();
				obj.put("oceanAt", "QUACH THI XUYEN");
				obj.put("oceanCn", "1903 5460 1810 10");
				obj.put("oceanBn", "Techcombank -Buôn Ma Thuột ");
				
				JSONObject objBank1 = new JSONObject();
				JSONObject objBank2 = new JSONObject();

				arrayBank.add(obj);
				arrayBank.add(objBank1);
				arrayBank.add(objBank2);
				row.set("bankList", arrayBank);
			}

			JSONObject jsonObject = JSONObject.fromBean(row);
			this.getWriter().write(jsonObject.toString());
			return null;
		} else {
			JSONObject jsObj = new JSONObject();
			jsObj.put("code", 101);
			jsObj.put("msg", "Lỗi hệ thống, vui lòng thử lại sau！");
			this.getWriter().write(jsObj.toString());

			return null;
		}

	}
public ActionResult doSMSSend() throws Exception {
	logger.info("请求ip"+getipAddr());
	HttpServletRequest request = getRequest();
	com.alibaba.fastjson.JSONObject jsonObj = getRequestJson(request);
	JSONObject jsonObject = new JSONObject();
	String ip = getipAddr();
	String miwen = jsonObj.getString("token");
	String type = "1";
	String phone = jsonObj.getString("cellphone").trim().replaceAll(" ", "");
		
	String jiamiwen = Encrypt.MD5(phone+encrypt);
	
	if(jiamiwen.equals(miwen)){
		SimpleDateFormat fmtrq2 = new SimpleDateFormat("yyyy-MM-dd");
		 Calendar calendar = Calendar.getInstance(); 
		 String  dateTS=fmtrq2.format(calendar.getTime());
		 calendar.add(Calendar.DATE, 1);	
		 String  dateTE =fmtrq2.format(calendar.getTime());
		int  dxtiaoshu  = jdbUserService.getDxtiaoshu(phone,dateTS,dateTE);
		if(dxtiaoshu  >3){
			jsonObject.put("code", 10);
			jsonObject.put("msg", "sms error");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		try 
		{
			logger.info("当前发送的验证码是："+phone+"_"+type);
			Object memyzm = MemCachedUtil.cachedClient.get(phone+"_"+type);
			if(memyzm==null)
			{
				//产生4位数验证码
				int intCount = 0;
				intCount = (new Random()).nextInt(9999);
				if(intCount < 1000){
					intCount += 1000;
				}
				String randomCode = intCount + "";
				logger.info("当前注册用户手机号码："+phone+"生成验证码是："+randomCode);	
				logger.info(Encrypt.encryptSES(randomCode, IConstants.PWD_SES_KEY)+"___"+Encrypt.encryptSES(phone, IConstants.PWD_SES_KEY));
				//存到缓存服务器
				MemCachedUtil.cachedClient.set(phone+"_"+type, Encrypt.encryptSES(randomCode, IConstants.PWD_SES_KEY), new Date(time));
				jsonObject.put("randomCode", Encrypt.encryptSES(randomCode, IConstants.PWD_SES_KEY));
				jsonObject.put("recivePhone", Encrypt.encryptSES(phone, IConstants.PWD_SES_KEY));
				MemCachedUtil.cachedClient.set(phone+"_HAHA", randomCode, new Date(time));
				logger.info("短信发送成功-----"+phone);
				int smscode = jdbUserService.getSmsCode();
				
//				String content = "[{\"PhoneNumber\":\""+phone+"\",\"Message\":\"Ma xac thuc OTP cua ban la "+randomCode+", ma xac thuc co hieu luc trong thoi gian 5 phut ke tu khi ban gui tin nhan.\",\"SmsGuid\":\""+phone+"\",\"ContentType\":1}]";
//				String con = URLEncoder.encode(content, "utf-8");
//				SendMsg sendMsg = new SendMsg();
//				String returnString = SendMsg.sendMessageByGetOTP(con,phone);
//				if (returnString.equals("106")) {
				
				String content = "Mã đơn hàng của bạn là "+randomCode;
                String returnString = SendMsgTYH.SendMsgSMS(content,phone);  
				logger.info(returnString);
				if (returnString.contains("1")) {		
					jsonObject.put("code", 0);
					jsonObject.put("msg", "Đã gửi thành công");
					logger.info(Encrypt.encryptSES(randomCode, IConstants.PWD_SES_KEY)+"___"+Encrypt.encryptSES(phone, IConstants.PWD_SES_KEY));
					//存到缓存服务器
					MemCachedUtil.cachedClient.set(phone+"_"+type, Encrypt.encryptSES(randomCode, IConstants.PWD_SES_KEY), new Date(time));
					jsonObject.put("randomCode", Encrypt.encryptSES(randomCode, IConstants.PWD_SES_KEY));
					jsonObject.put("recivePhone", Encrypt.encryptSES(phone, IConstants.PWD_SES_KEY));
					MemCachedUtil.cachedClient.set(phone+"_HAHA", randomCode, new Date(time));
					logger.info("短信发送成功-----"+phone);
					DataRow  ipInfo = new  DataRow();
					ipInfo.set("ip",ip);
					ipInfo.set("createTime",new Date());
					ipInfo.set("phone",phone);						
					jdbUserService.insertIpInfo(ipInfo);
					
					this.getWriter().write(jsonObject.toString());
					return null;
				}else{
					jsonObject.put("code", 4);
					jsonObject.put("msg", "Gửi không thành công"); //发送失败
					logger.error("短信发送失败-----"+phone);
					this.getWriter().write(jsonObject.toString());
					return null;
				}
			}
			else
			{
				jsonObject.put("code", 5);
				jsonObject.put("msg", "Mã xác thực đã gửi đi, thời hạn 3 phút"); //验证码已发送，3分钟内有效
				logger.error("缓存内的验证码-----"+MemCachedUtil.cachedClient.get(phone+"_HAHA"));
				jsonObject.put("randomCode", memyzm.toString());
				jsonObject.put("recivePhone", Encrypt.encryptSES(phone, IConstants.PWD_SES_KEY));
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("由于系统原因，验证码发送失败"+e);
			jsonObject.put("code", 10);
			jsonObject.put("msg", "Do lỗi hệ thống, mã xác thực không gửi được");//由于系统原因，验证码发送失败
			logger.error("短信发送失败-----"+phone);
			this.getWriter().write(jsonObject.toString());
			return null;
		}
	}else{
		return null;
	}
}
//login function
	public ActionResult doLogin() throws Exception {
		logger.info("request-ip" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonObj = getRequestJson(request);
		JSONObject jsonObject = new JSONObject();
		String phone = jsonObj.getString("cellphone").trim().replaceAll(" ", "");// 手机�??
		
		int phonetype = jsonObj.getInteger("cellphoneType");
		String token = jsonObj.getString("token");
		String idEncrypt = Encrypt.MD5(phone + phonetype + encrypt);
		if (idEncrypt.equals(token)) {
			String respUserId = jdbUserService.getIdByPhone(phone);
			String ip = getipAddr();// 61.145.153.20
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			SimpleDateFormat sdfday = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String lastDate = sdf.format(new Date());
			String daytime = sdfday.format(new Date());
			logger.info("Currently registered user: mobile phone number：" + phone + " -IP:" + ip + " -current time"
					+ lastDate);
			logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			String code ="";
			if (jsonObj.containsKey("otp")) {
			    code = jsonObj.getString("otp");
			    String memyzm= "";
				if(MemCachedUtil.cachedClient.keyExists(phone+"_HAHA"))
				{
					memyzm = (String)MemCachedUtil.cachedClient.get(phone+"_HAHA");
				}
			    if(!code.equals(memyzm)){
					jsonObject.put("code", -3);
					jsonObject.put("msg", "Mã xác minh không chính xác");//验证码不正确
					logger.warn("验证码不正确");
					this.getWriter().write(jsonObject.toString());
					return null;
				}
			}
			long phoneStr = jdbUserService.getCountByPhone(phone);
			if (phoneStr > 0) {
				jsonObject.put("code", 0);
				jsonObject.put("userId", respUserId);
				jsonObject.put("msg", "Login Success");
				jsonObject.put("user_created", true);
				logger.info("Account registered，Login Directly");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			String table = "sd_user";
			DataRow data = new DataRow();
			data.set("mobilePhone", phone);
			data.set("lastIP", ip);
			data.set("hongbao", 0);
			data.set("lastDate", lastDate);
			data.set("createTime", lastDate);
			data.set("yearmonthday", daytime);
			data.set("loginCount", 0);
			jdbUserService.addUser(table, data);

			// Get the user ID used to generate the user
			String userid = jdbUserService.getIdByPhone(phone);
			String username = "";

			if (phonetype == 1) {
				username = "OCEAN-AND" + userid;
			} else {
				username = "OCEAN-IOS" + userid;
			}	

			data.set("username", username);
			jdbUserService.updateUser(userid, data);
			logger.info("Add user information" + data);
			// Add login record
			DataRow row = new DataRow();
			table = "sduser_login_log";
			row.set("logindate", lastDate);
			row.set("userid", userid);
			jdbUserService.addUser(table, row);

			String hqphonetype = jsonObj.getString("hqphonetype");
			String phonebrand = jsonObj.getString("phonebrand");
			String systemversion = jsonObj.getString("sysVersion");
			String appcode = jsonObj.getString("appIdCode");
			String androidid = jsonObj.getString("anId");
			String macaddress = jsonObj.getString("macAddr");
			String deviceid = jsonObj.getString("deviceId");
			String simserialnumber = jsonObj.getString("simId");
			String ipAddress = jsonObj.getString("ipAddr");
			String isemulator = jsonObj.getString("isEmu");
			String registration_id = jsonObj.getString("regist");

			logger.info("Currently logged in user" + userid + "ip" + ip + "time" + lastDate);
			try {
				// Have this user modify ?? after login? ? Add login log
				if (!"none".equals(macaddress) && !"02:00:00:00:00:00".equals(macaddress)) {
					List<DataRow> listxtsj = jdbUserService.getAllxtsj(macaddress);
					if (listxtsj.size() > 2) {
						DataRow userErrorPhoneType = new DataRow();
						userErrorPhoneType.set("userid", userid);
						userErrorPhoneType.set("hqphonetype", hqphonetype);
						userErrorPhoneType.set("phonebrand", phonebrand);
						userErrorPhoneType.set("systemversion", systemversion);
						userErrorPhoneType.set("appcode", appcode);
						userErrorPhoneType.set("androidid", androidid);
						userErrorPhoneType.set("macaddress", macaddress);
						userErrorPhoneType.set("deviceid", deviceid);
						userErrorPhoneType.set("simserialnumber", simserialnumber);
						userErrorPhoneType.set("ipAddress", ipAddress);
						userErrorPhoneType.set("isemulator", isemulator);
						userErrorPhoneType.set("createtime", new Date());
						jdbUserService.addUserErrorPhoneType(userErrorPhoneType);
						jsonObject.put("code", 8);
						jsonObject.put("msg", "Tài khoản này đã bị hạn chế để đăng nhập");//
						this.getWriter().write(jsonObject.toString());
						return null;
					}
					if (listxtsj.size() > 1 || "true".equals(isemulator)) {
						data.set("sjxt", 1);
					}
				}
				data.set("lastIP", ip);
				data.set("lastDate", lastDate);
				data.set("registration_id", registration_id);
				logger.info("userid:" + userid + ",data" + data);
				jdbUserService.updateUser(userid, data);
				DataRow userPhoneType = new DataRow();
				userPhoneType.set("userid", userid);
				userPhoneType.set("hqphonetype", hqphonetype);
				userPhoneType.set("phonebrand", phonebrand);
				userPhoneType.set("systemversion", systemversion);
				userPhoneType.set("appcode", appcode);
				userPhoneType.set("androidid", androidid);
				userPhoneType.set("macaddress", macaddress);
				userPhoneType.set("deviceid", deviceid);
				userPhoneType.set("simserialnumber", simserialnumber);
				userPhoneType.set("ipAddress", ipAddress);
				userPhoneType.set("isemulator", isemulator);
				userPhoneType.set("createtime", new Date());
				jdbUserService.addUserPhoneType(userPhoneType);
				logger.info("Modify login information---" + data);
				DataRow userLoginLog = new DataRow();
				String tbUserLoginLog = "sduser_login_log";
				userLoginLog.set("logindate", lastDate);
				userLoginLog.set("userid", userid);
				jdbUserService.addUser(tbUserLoginLog, userLoginLog);
				logger.info("Login ----" + userLoginLog);

				String mobile = phone;
				jsonObject.put("cellphone", mobile);
				jsonObject.put("userId", userid);
				jsonObject.put("userName", username);
				jsonObject.put("code", 0);
				jsonObject.put("user_created", false);
				jsonObject.put("msg", "Đăng nhập thành công");
				this.getSession().setAttribute("phone", phone);// —�?��?��??++—�??+—�??+—�??+LL

				this.getWriter().write(jsonObject.toString());
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Login Failed" + e);
				jsonObject.put("code", 10);
				jsonObject.put("msg", "Lỗi hệ thống, đăng ký không thành công!");// 系统错误，注册失�??
				this.getWriter().write(jsonObject.toString());
				return null;
			}
		} else {
			jsonObject.put("code", 101);
			jsonObject.put("msg", "Lỗi hệ thống, vui lòng thử lại sau！");
			this.getWriter().write(jsonObject.toString());
			return null;
		}

	}

	public ActionResult doUpdateIdCard() throws Exception {

		logger.info("request-ip:" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonObj = getRequestJson(request);

		String userid = jsonObj.getString("userId");
		String numberId = jsonObj.getString("numId");
		String birthday = jsonObj.getString("birthday");
		String token = jsonObj.getString("token");
		String idEncrypt = Encrypt.MD5(userid + encrypt);

		if (idEncrypt.equals(token)) {
			String p1 = jsonObj.getString("pOne");
			String p2 = jsonObj.getString("pTwo");
			String p3 = jsonObj.getString("pThree");
			String homeAddress = jsonObj.getString("homeAddress");
			String ui = jdbUserService.getUI(userid);
			String bankcard = jdbUserService.getBK(userid);

			int yhbd = jdbUserService.getUserBank(userid);
			int lianxi = jdbUserService.getUserLianXi(userid);
			int isWork = mofaUserService.getUserWork(userid);
			
			JSONObject jsonObject = new JSONObject();
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			
			if ("".equals(p1) || p1 == null || "".equals(p2) || p2 == null || "".equals(p3) || p3 == null) {

				jsonObject.put("code", -11);
				jsonObject.put("msg", "Lỗi tải lên hình ảnh");// 图片上传错误
				this.getWriter().write(jsonObject.toString());
				return null;

			}
			try {

				if (bankcard.equals("")) {
					DataRow row1 = new DataRow();
					// 姓名添加进数据库
					row1.set("userid", userid);
					row1.set("idno", numberId);
					row1.set("age", birthday);
					row1.set("homeaddress", homeAddress);
					jdbUserService.addUserInfoHN(row1);
				} else {
					DataRow row1 = new DataRow();
					// 姓名添加进数据库
					row1.set("userid", userid);
					row1.set("idno", numberId);
					row1.set("age", birthday);
					row1.set("homeaddress", homeAddress);
					jdbUserService.updateInfoHN(row1);
				}
				if (ui.equals("")) {
					DataRow row5 = new DataRow();
					row5.set("userid", userid);
					row5.set("p1", p1);
					row5.set("p2", p2);
					row5.set("p3", p3);
					row5.set("create_time", fmtrq.format(calendar.getTime()));
					jdbUserService.addUserZhaoPian(row5);
				} else {
					DataRow row5 = new DataRow();
					row5.set("userid", userid);
					row5.set("p1", p1);
					row5.set("p2", p2);
					row5.set("p3", p3);
					row5.set("create_time", fmtrq.format(calendar.getTime()));
					jdbUserService.updateUserZhaoPian(row5);
				}

				DataRow row3 = new DataRow();
				row3.set("id", userid);
				row3.set("isShenfen", 1);

				if (yhbd == 1 & lianxi == 1 && isWork == 1 ) {
					row3.set("vipStatus", 1);// 工作认证�??1
				} else {
					row3.set("vipStatus", 0);// 工作认证�??1
				}

				jdbUserService.updateUserInfoH(row3);
				jsonObject.put("code", 0);
				jsonObject.put("msg", "Thành công");// 成功
			} catch (Exception e) {

				jsonObject.put("code", -3);
				jsonObject.put("msg", "Lỗi hệ thống, vui lòng thử lại sau！");// 系统异常，请稍后再试�??
				e.printStackTrace();
			}
			this.getWriter().write(jsonObject.toString());
			return null;
		} else {
			JSONObject jsObj = new JSONObject();
			jsObj.put("code", 101);
			jsObj.put("msg", "Lỗi hệ thống, vui lòng thử lại sau！");
			this.getWriter().write(jsObj.toString());
			return null;
		}

	}
	public ActionResult doUpdateBankCopy() throws Exception {
		logger.info("请求ip"+getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonObj = getRequestJson(request);
		String name = jsonObj.getString("userName").replace("&nbsp;", " ");
		String userid = jsonObj.getString("userId");
		String cardId = jsonObj.getString("userCardId");
		String token = jsonObj.getString("token");
		String bankName = jsonObj.getString("bankName");
		String bankBranch = jsonObj.getString("bankBranch");
		String idEncrypt = Encrypt.MD5(userid + cardId + encrypt);
		
		JSONObject jsonObject = new JSONObject();
		if(idEncrypt.equals(token)){
			DataRow dataRow = jdbUserService.findUserById(userid);
			String uisf = jdbUserService.getBK(userid);
			int yhbd = dataRow.getInt("yhbd");
			if (yhbd == 1) {
				jsonObject.put("code", -3);
				jsonObject.put("msg", "Bạn cần phải xác minh tài khoản ngân hàng");// 此用户已经绑定银行卡

				this.getWriter().write(jsonObject.toString());
				return null;
			}
			
			if (bankBranch.isEmpty() || bankBranch != null) {
				bankName = bankName + "@" + bankBranch;
			}
			
			 String cardName = bankName.trim().replace("&nbsp;", " ");
			 String ui = jdbUserService.getUICard(userid);
			
			 
			 Calendar calendar =Calendar.getInstance();
			 SimpleDateFormat fmtrq  = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			 
			 
			 String bankzu[] = {"Sacombank","VietcomBank","VietinBank","Techcombank","BIDV","VPBank","Eximbank","DongA Bank","VIB","MB Bank","Viet Capital Bank","OceanBank","VietABank","TPBank","HDBank","SCB","LienVietPostBank","SeABank","ABBank","Nam A Bank","OCB","GBBank","PG Bank","SHBank","Saigon Bank","Kien Long Bank","NCB","BacABank","PVcomBank","VRB","Vietbank","BVB","Wooribank"};
			 String napasbankzu[] = {"Sacombank","NH TMCP Ngoai Thuong VN (VietcomBank)","NH TMCP Cong Thuong VN (Vietinbank)","NH TMCP Ky Thuong VN (Techcombank)","NH TMCP Dau Tu va Phat Trien VN (BIDV)","NH TMCP Viet Nam Thinh Vuong (VP Bank)","NH TMCP Xuat Nhap khau VN (Eximbank)","NH TMCP Dong A (DongA Bank)","NH TMCP Quoc Te VN (VIB)","NH TMCP Quan Doi (MB)",
			    		"NH TMCP Ban Viet (Viet Capital Bank)","NH TM TNHH MTV Dai Duong (OceanBank)","NH TMCP Viet A (VietABank)","NH TMCP Tien Phong (TPBank)","NH TMCP Phat Trien TP HCM (HDBank)","NH TMCP Sai Gon (SCB)","NH TMCP Buu Dien Lien Viet (LienVietPostBank)","NH TMCP Dong Nam A(SeABank)","NH TMCP An Binh (ABBank)","NH TMCP Nam A (NamABank)","NH TMCP Phuong Dong (OCB)","NH TM TNHH MTV Dau Khi Toan Cau (GPBank)","NH TMCP Xang Dau Petrolimex (PG Bank)","NH TMCP Sai Gon Ha Noi (SHB)","NH TMCP Sai Gon Cong Thuong (Saigonbank)",
			    		"NH TMCP Kien Long (KienLongBank)","NH TMCP Quoc Dan (NCB)","NH TMCP Bac A (BacABank)","NH TMCP Dai Chung VN (PVCombank)","NH Lien Doanh Viet Nga (VRB)","NH Viet Nam Thuong Tin (VietBank)","NH TMCP Bao Viet (BaoVietBank)","NH Wooribank"};
			 String napasBankNo[] ={"79303001","970436","970415","970407","970418","970432","970431","970406","970441","970422","970454","970414","970427","970423","970437","970429","970449","970440","970425","970428","970448","970408","970430","970443","970400","970452","970419","970409","970412","970421","970433","970438","970457"}; 
			 
			 //去掉或者添加要把3组里面的都改一下
				
			 SimpleDateFormat famat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			 SimpleDateFormat famatid = new SimpleDateFormat("yyyyMMddhhmmssSSS");
			    
			 String userbankcode = "0";
			 String userbankname = cardName.substring(0, cardName.indexOf("-")).toLowerCase().trim();
			 String banknamenapas = "NAPAS";
			 if("sacombank".equals(userbankname)){
				 banknamenapas = "STB";
				 userbankcode = "79303001";
			 }
			 for(int j =0 ;j<bankzu.length;j++){
				if(userbankname.equals(bankzu[j].toLowerCase())){
						
				userbankname = napasbankzu[j];
				userbankcode = napasBankNo[j];
						
				String time = famat.format(new Date());
			    String checkid = famatid.format(new Date());
	
			    Document doc = null;
			    OLVSacombank myClass = new OLVSacombank();
			    PrivateKey privateKey = myClass.getPrivateKey(OLVSacombank.getDefaultPrivateKey());
			    
					String username = jdbUserService.getRealname(userid);
					//名字传的是name
					String r =name.replaceAll("[aáàãảạăắằẵẳặâấầẫẩậAÁÀÃẢẠĂẮẰẴẲẶÂẤẦẪẨẬ]", "a")
							.replaceAll("[eéèẽẻẹêếềễểệễEÉÈẼẺẸÊẾỀỄỂỆ]", "e")
							.replaceAll("[oóòõỏọôốồỗổộơớờỡởợOÓÒÕỎỌÔỐỒỖỔỘƠỚỜỠỞỢ]", "o")
							.replaceAll("[uúùũủụưứừữửựUÚÙŨỦỤƯỨỪỮỬỰ]", "u")
							.replaceAll("[iíìĩỉịIÍÌĨỈỊ]", "i")
							.replaceAll("[yýỳỹỷỵYÝỲỸỶỴ]", "y")
							.replaceAll("[đĐ]", "d");
					logger.info("用户的名字：" + r);
					
				    String checkUrl = "https://webservices.sacombank.com/bank-api/v1/checkaccount";
					String checksign = "";
					HttpHeaders checkheader = null;
					ResponseEntity<String> checkresp = null;
					String checkxmlRequest = "<DOCUMENT>" +
							"<TRANSACTION_ID>"+checkid+"</TRANSACTION_ID>" +
							"<PARTNER_ID>M99</PARTNER_ID>" +
							"<LOCAL_DATETIME>"+time+"</LOCAL_DATETIME>" +
							"<ACCOUNT_ID>"+cardId+"</ACCOUNT_ID>" +
							"<ACC_TYPE>ACC</ACC_TYPE>" +
							"<CHECK_TYPE>"+banknamenapas+"</CHECK_TYPE>" +
							"<BENF_NAME>"+r+"</BENF_NAME>" +
							"<BANK_ID>"+userbankcode+"</BANK_ID>" +
							"</DOCUMENT>";
					checksign = myClass.signature(checkxmlRequest,privateKey);
					checkheader = myClass.addHeaderValue("Signature", checksign, checkheader);
					
					String checkencoding = Base64.encode(("userM99:Em99@2019#3").getBytes("UTF-8"));
					checkheader = myClass.addHeaderValue("Authorization", "Basic " + checkencoding, checkheader);
					
					HttpEntity<String> checkrequestEntity = new HttpEntity<String>(checkxmlRequest, checkheader);
					RestTemplate checkrestTemplate = new RestTemplate();
					ResponseEntity<String> checkresponse = checkrestTemplate.exchange(checkUrl, HttpMethod.POST, checkrequestEntity, String.class);
					String checkresponseBody = checkresponse.getBody(); 
					HttpHeaders checkresponseHeader = checkresponse.getHeaders();
					
					String checksigData = checkresponseHeader.getFirst("signature");
					if (myClass.verifySignature(checkresponseBody, checksigData)){
						logger.info("verify success");
					} else {
						logger.info("verify fail");
					}
					System.out.println("checkresponseBody: " + checkresponseBody);
			        doc = DocumentHelper.parseText(checkresponseBody); // 将字符串转为XML
			        Element rootElt = doc.getRootElement(); // 获取根节点
			        String checkstatus = rootElt.elementTextTrim("TRANSACTION_STATUS"); // 拿到head节点下的子节点title值
			        String checkname = rootElt.elementTextTrim("ACC_NAME"); // 拿到head节点下的子节点title值
			        DataRow rowbank = new DataRow();
			        logger.info("userid:"+userid);
			        logger.info("username:"+checkname);
			        logger.info("checkstatus:"+checkstatus);
			        logger.info("napasbankno:" + userbankcode);
			        logger.info("napasbankname:" + userbankname);
			        //添加到下面的银行认证
			        if("0".equals(checkstatus)){
			        	if(r.trim().toLowerCase().equals(checkname.trim().toLowerCase())){
			        		try{
			        			 
			        			 //将数据存到数据库中
			        			 if (ui.equals("")) {
			        				 DataRow row5= new DataRow() ;
			        				 row5.set("userid", userid);
			        				 row5.set("cardusername", name);
			        				 row5.set("remark", 0);
			        				 row5.set("cardno", cardId);
			        				 row5.set("cardname", cardName);
			        				 row5.set("napasbankno", userbankcode);
			        			     row5.set("napasbankname", userbankname);
			        			     row5.set("napasusername", checkname);
			        				 row5.set("create_time", fmtrq.format(calendar.getTime()));
			        				 jdbUserService.addUserCard(row5) ; 
			        			 }else {
			        				 DataRow row5= new DataRow() ;
			        				 row5.set("userid", userid);
			        				 row5.set("cardusername", name);
			        				 row5.set("remark", 0);
			        				 row5.set("cardno", cardId);
			        				 row5.set("cardname", cardName);
			        				 row5.set("napasbankno", userbankcode);
			        			     row5.set("napasbankname", userbankname);
			        			     row5.set("napasusername", checkname);
			        				 row5.set("create_time", fmtrq.format(calendar.getTime()));
			        				 jdbUserService.updateUserCard(row5) ;
			        			 }
			        			 if (uisf.equals("")) {
										DataRow row5 = new DataRow();
										row5.set("userid", userid);
										row5.set("realname", name);
										jdbUserService.addUserInfoHN(row5);
									} else {
										DataRow row5 = new DataRow();
										row5.set("userid", userid);
										row5.set("realname", name);
										jdbUserService.updateInfoHN(row5);
									}
			        			 DataRow row3= new DataRow() ;
			        			 row3.set("id",userid);
			        			 row3.set("yhbd",1);//银行卡认证为1
			        			 jdbUserService.updateUserInfoH(row3);
			        			 jsonObject.put("code", 0);
			        			 jsonObject.put("msg", "Thành công");//成功
			        			 this.getWriter().write(jsonObject.toString());	   	
			        			 return null;
			        		 } catch(Exception e) {
			        			 
			        			 jsonObject.put("code", -3);
			        			 jsonObject.put("msg", "Lỗi hệ thống, vui lòng thử lại sau！");//系统异常，请稍后再试！
			        			 e.printStackTrace() ;
			        			 this.getWriter().write(jsonObject.toString());	   	
			        			 return null;
			        		 }
			        }else{
			        	jsonObject.put("code", -2);
						jsonObject.put("msg", "Sai tên, vui lòng kiểm tra lại họ tên và số tài khoản ngân hàng");//成功
						 this.getWriter().write(jsonObject.toString());	   	
						 return null;
			        }
				 }else if("1".equals(checkstatus)){
					      jsonObject.put("code", -1);
						  jsonObject.put("msg", "Sai số tài khoản NH, vui lòng xác nhận lại");//成功
						  this.getWriter().write(jsonObject.toString());	   	
						  return null;
			     }else{
			    	 	jsonObject.put("code", -3);
					  jsonObject.put("msg", "Lỗi mạng, vui lòng đề xuất lại");//成功
					  this.getWriter().write(jsonObject.toString());	   	
					  return null;
			     }
			}
		}
	
				try{
					logger.info("其他银行");
		   			 //将数据存到数据库中
		   			 if (ui.equals("")) {
		   				 DataRow row5= new DataRow() ;
		   				 row5.set("userid", userid);
		   				 row5.set("cardusername", name);
		   				 row5.set("remark", 0);
		   				 row5.set("cardno", cardId);
		   				 row5.set("cardname", cardName);
		   				 row5.set("napasbankno", userbankcode);
		   			     row5.set("napasbankname", userbankname);
		   				 row5.set("create_time", fmtrq.format(calendar.getTime()));
		   				 jdbUserService.addUserCard(row5) ; 
		   			 }else {
		   				 DataRow row5= new DataRow() ;
		   				 row5.set("userid", userid);
		   				 row5.set("cardusername", name);
		   				 row5.set("remark", 0);
		   				 row5.set("cardno", cardId);
		   				 row5.set("cardname", cardName);
		   				 row5.set("napasbankno", userbankcode);
		   			     row5.set("napasbankname", userbankname);
		   				 row5.set("create_time", fmtrq.format(calendar.getTime()));
		   				 jdbUserService.updateUserCard(row5) ;
		   			 }
		   			if (uisf.equals("")) {
						DataRow row5 = new DataRow();
						row5.set("userid", userid);
						row5.set("realname", name);
						jdbUserService.addUserInfoHN(row5);
					} else {
						DataRow row5 = new DataRow();
						row5.set("userid", userid);
						row5.set("realname", name);
						jdbUserService.updateInfoHN(row5);
					}
		   			 DataRow row3= new DataRow() ;
		   			 row3.set("id",userid);
		   			 row3.set("yhbd",1);//银行卡认证为1
		   			 jdbUserService.updateUserInfoH(row3);
		   			 jsonObject.put("code", 0);
		   			 jsonObject.put("msg", "Thành công");//成功
		   			 this.getWriter().write(jsonObject.toString());	   	
		   			 return null;
		   		 } catch(Exception e) {
		   			 
		   			 jsonObject.put("code", -3);
		   			 jsonObject.put("msg", "Lỗi hệ thống, vui lòng thử lại sau！");//系统异常，请稍后再试！
		   			 e.printStackTrace() ;
		   			 this.getWriter().write(jsonObject.toString());	   	
		   			 return null;
		   		 }
		}else{
			return null ;
		}
		
	}

	public ActionResult doUpdateBank() throws Exception {
		logger.info("request-ip" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonObj = getRequestJson(request);
		String name = jsonObj.getString("userName").replace("&nbsp;", " ");
		String userid = jsonObj.getString("userId");
		String cardId = jsonObj.getString("userCardId");
		String token = jsonObj.getString("token");
		String bankName = jsonObj.getString("bankName");
		String bankBranch = jsonObj.getString("bankBranch");

		String idEncrypt = Encrypt.MD5(userid + cardId + encrypt);
		System.out.println(idEncrypt);
		if (idEncrypt.equals(token)) {
			String ui = jdbUserService.getUICard(userid);
			String uisf = jdbUserService.getBK(userid);
			JSONObject jsonObject = new JSONObject();

			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			if (bankBranch.isEmpty() || bankBranch != null) {
				bankName = bankName + "@" + bankBranch;
			}
			try {
				// check user exist?
				if (ui.equals("")) {
					DataRow userCard = new DataRow();
					userCard.set("userid", userid);
					userCard.set("cardusername", name);
					userCard.set("cardno", cardId);

					userCard.set("cardName", bankName);
					userCard.set("create_time", fmtrq.format(calendar.getTime()));
					jdbUserService.addUserCard(userCard);
				} else {
					DataRow userCard = new DataRow();
					userCard.set("userid", userid);
					userCard.set("cardusername", name);
					userCard.set("cardno", cardId);
					userCard.set("cardName", bankName);
					userCard.set("create_time", fmtrq.format(calendar.getTime()));
					jdbUserService.updateUserCard(userCard);
				}
				if (uisf.equals("")) {
					DataRow row5 = new DataRow();
					row5.set("userid", userid);
					row5.set("realname", name);
					jdbUserService.addUserInfoHN(row5);
				} else {
					DataRow row5 = new DataRow();
					row5.set("userid", userid);
					row5.set("realname", name);
					jdbUserService.updateInfoHN(row5);
				}
				DataRow row3 = new DataRow();
				row3.set("id", userid);
				row3.set("yhbd", 1);// 银行卡认证为1
				jdbUserService.updateUserInfoH(row3);
				jsonObject.put("code", 0);
				jsonObject.put("msg", "Thành công");// 成功
			} catch (Exception e) {
				jsonObject.put("code", -3);
				jsonObject.put("msg", "Lỗi hệ thống, vui lòng thử lại sau！");// 系统异常，请稍后再试�??
				e.printStackTrace();
			}
			this.getWriter().write(jsonObject.toString());
			return null;
		} else {
			JSONObject jsObj = new JSONObject();
			jsObj.put("code", 101);
			jsObj.put("msg", "Lỗi hệ thống, vui lòng thử lại sau！");
			this.getWriter().write(jsObj.toString());
			return null;
		}

	}

	public ActionResult doUpdateContact() throws Exception {
		logger.info("request-ip:" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonObj = getRequestJson(request);
		String userid = jsonObj.getString("userId");

		String contact1 = jsonObj.getString("contactOne").trim().replace("&nbsp;", " ");
		String contact2 = jsonObj.getString("contactTwo").trim().replace("&nbsp;", " ");
		String tel1 = jsonObj.getString("telOne").trim().replace("&nbsp;", " ");
		String tel2 = jsonObj.getString("telTwo").trim().replace("&nbsp;", " ");
		String token = jsonObj.getString("token");
		String idEncrypt = Encrypt.MD5(userid + encrypt);
		System.out.println(idEncrypt);
		if (idEncrypt.equals(token)) {
			String relationOne = jsonObj.getString("relationOne").trim().replace("&nbsp;", " ");
			String relationTwo = jsonObj.getString("relationTwo").trim().replace("&nbsp;", " ");
			String ui = jdbUserService.getUILianxi(userid);
			JSONObject jsonObject = new JSONObject();

			int yhbd = jdbUserService.getUserBank(userid);
			int shenfen = jdbUserService.getUserShenFen(userid);
			int isWork = mofaUserService.getUserWork(userid);

			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			try {
				DataRow userRelation = new DataRow();
				userRelation.set("userid", userid);
				userRelation.set("contact1", contact1.replaceAll("[^\\u0000-\\uFFFF]", ""));
				userRelation.set("contact2", contact2.replaceAll("[^\\u0000-\\uFFFF]", ""));
				userRelation.set("tel1", tel1);
				userRelation.set("tel2", tel2);
				userRelation.set("guanxi1", relationOne);
				userRelation.set("guanxi2", relationTwo);
				userRelation.set("list", "1");
				userRelation.set("create_time", fmtrq.format(calendar.getTime()));
				if (ui.equals("")) {
					jdbUserService.addUserLX(userRelation);
				} else {
					jdbUserService.updateUserLX(userRelation);
				}
				DataRow row3 = new DataRow();
				row3.set("id", userid);
				row3.set("isLianxi", 1);
				if (yhbd == 1 & shenfen == 1 && isWork ==1) {
					row3.set("vipStatus", 1);// 工作认证�??1
				} else {
					row3.set("vipStatus", 0);// 工作认证�??1
				}
				jdbUserService.updateUserInfoH(row3);
				jsonObject.put("code", 0);
				jsonObject.put("msg", "Thành công");
			} catch (Exception e) {
				jsonObject.put("code", -3);
				jsonObject.put("msg", "Lỗi hệ thống, vui lòng thử lại sau！");
				e.printStackTrace();
			}
			this.getWriter().write(jsonObject.toString());
			return null;
		} else {
			JSONObject jsObj = new JSONObject();
			jsObj.put("code", 101);
			jsObj.put("msg", "Lỗi hệ thống, vui lòng thử lại sau！");
			this.getWriter().write(jsObj.toString());
			return null;
		}
	}

	public void doGetAddressBook() throws ServletException, IOException {
		logger.info("request-ip:" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonObj = getRequestJson(request);
		JSONObject jsonObject = new JSONObject();
		String userid = jsonObj.getString("userId");
		String token = jsonObj.getString("token");
		String idEncrypt = Encrypt.MD5(userid + encrypt);
		System.out.println(idEncrypt);
		if (idEncrypt.equals(token)) {
			logger.info(userid);
			com.alibaba.fastjson.JSONArray jsonArray = jsonObj.getJSONArray("db");
			if (jsonArray == null) {
				return;
			}
			if (jsonArray.size() > 0) {
				jsonObject.put("code", 0);
				jsonObject.put("msg", "right");
				for (int i = 0; i < jsonArray.size(); i++) {
					com.alibaba.fastjson.JSONObject object = jsonArray.getJSONObject(i);
					if (object.containsKey("dbTen") && object.containsKey("dbDienthoai")) {
						String nameString = object.getString("dbTen").replaceAll("[^\\u0000-\\uFFFF]", "");
						if (nameString.contains("?") || nameString.contains("?")) {
							nameString = "none";
						}
						String phoneString = object.getString("dbDienthoai");
						DataRow row = new DataRow();

						row.set("userid", userid);
						row.set("name", nameString);
						row.set("phone", phoneString);
						row.set("create_time", new Date());
						jdbUserService.addTongxunlu(row);
					}
				}
			} else {
				jsonObject.put("code", 1);
				jsonObject.put("msg", "Lỗi");
			}
			this.getWriter().write(jsonObject.toString());
		} else {
			jsonObject.put("code", 1);
			jsonObject.put("msg", "Lỗi!");
			this.getWriter().write(jsonObject.toString());
		}

	}

	/**
	 * 
	 * 提交借款信息
	 * 
	 */

	public ActionResult doUpdateBorrowMsg() throws Exception {
		logger.info("request-ip:" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
		com.alibaba.fastjson.JSONObject jsonObj = jsonString;
		JSONObject jsonObject = new JSONObject();
		String borrMoney = jsonObj.getString("borrMoney");
		int borrDate = jsonObj.getInteger("borrDate");
		String annualrate = jsonObj.getString("borrAnnualrate");
		String actualMoney = jsonObj.getString("actualMoney");
		String interesetFee = jsonObj.getString("interesetFee");
		int userid2 = jsonObj.getInteger("userId");

		if (borrDate == 30) {
			borrDate = 2;
		}
		if (borrDate == 15) {
			borrDate = 1;
		}
		//只能提交 14天  2020年2月2日
		if (borrDate == 7) {
			borrDate = 3;
		}
		else if (borrDate == 14) {
			borrDate = 4;
		}
		
		
		SimpleDateFormat famat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String userid3=String.valueOf(userid2);
		DataRow jkDataLast = jdbUserService.getjkNumLast(userid3);
//		//新用户停止放款
//		if( jkDataLast==null ) {
//			String Phone = jdbUserService.getMobilePhone(userid2);
//			DataRow row = new DataRow();
//			row.set("create_time",famat.format(new Date()));
//			row.set("phone",Phone);
//			row.set("status",12);
//			jdbUserService.addNewphone(row);
//			
//			jsonObject.put("code", -1);
//			jsonObject.put("msg", "Tạm dừng gửi yêu cầu vay, sẽ xử lý hồ sơ vào 06/01/2020.");
//			this.getWriter().write(jsonObject.toString());
//			return null;
//		}
//		//停止放30天新用户
//		if(borrDate == 2 && jkDataLast==null ) {
//			jsonObject.put("code", -1);
//			jsonObject.put("msg", "30 ngày tạm thời chỉ dành cho khách hàng VIP");
//			this.getWriter().write(jsonObject.toString());
//			return null;
//		}
		
		
		
		DecimalFormat famt = new DecimalFormat("###,###");
		//2019-7-24 lin 格式转换防止点号
		if(borrMoney.isEmpty()) {
			jsonObject.put("code", -1);
			jsonObject.put("msg", "Lỗi xảy ra khiến giá trị vay không đúng, vui lòng tắt app thử lại !");
			this.getWriter().write(jsonObject.toString());
			return null;
		}else {
			borrMoney = famt.format(Integer.parseInt(borrMoney.replace(",", "").replace(".", "")));
		}
		
	
		// 30天不放款
//		String time = famat.format(new Date());
//		int aaa = time.compareTo("2019-12-13");
//		int bbb = "2020-01-02".compareTo(time);
//		if (borrDate == 2 && aaa > 0 && bbb > 0) {
//			jsonObject.put("code", -1);
//			jsonObject.put("msg", "Thời điểm này M99 chỉ cung cấp khoản vay 15 ngày");
//			this.getWriter().write(jsonObject.toString());
//			return null;
//		}
//		// 15天不放款
//		int ccc = time.compareTo("2020-01-02");
//		int ddd = "2020-01-19".compareTo(time);
//		if (borrDate == 1 && ccc > 0 && ddd > 0) {
//			jsonObject.put("code", -1);
//			jsonObject.put("msg", "Thời điểm này M99 chỉ cung cấp khoản vay 30 ngày");
//			this.getWriter().write(jsonObject.toString());
//			return null;
//		}
//		// 放假时间
//		int eee = time.compareTo("2020-01-20");
//		int fff = "2020-02-01".compareTo(time);
//		if (eee > 0 && fff > 0) {
//			jsonObject.put("code", -1);
//			jsonObject.put("msg",
//					"Từ 20/01/2020 -01/02/2020, M88 tạm ngừng cung cấp dịch vụ vay. Qúy khách vui lòng đề xuất vay trước, M99 sẽ xử lý hồ sơ vào 02/02/2020.");
//			this.getWriter().write(jsonObject.toString());
//			return null;
//		}
		
		
		// Have all been recognized
		DataRow datarow = jdbUserService.getALLRZ(userid2);
		if (!(datarow.getInt("isshenfen") == 1 && datarow.getInt("yhbd") == 1 && datarow.getInt("islianxi") == 1)) {
			jsonObject.put("code", -1);
			jsonObject.put("msg", "Còn những mục chưa xác minh, vui lòng hoàn tất xác minh");
			this.getWriter().write(jsonObject.toString());
			return null;
		}

				
		String token = jsonObj.getString("token");

		String idEncrypt = Encrypt.MD5(userid2 + encrypt);
		if (idEncrypt.equals(token)) {
			if (userid2 == 0) {
				jsonObject.put("code", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			long date111 = System.currentTimeMillis();
			long date2 = 0;
			DataRow dataJJJK = jdbUserService.findUserJJJKByuserid(userid2 + ""); // Refusal of loan information
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			SimpleDateFormat sdfday = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// Test whether there is a loan project has not been completed
			String idno = jdbUserService.getIdno(userid2);
			int jkcount = jdbUserService.getJKCount(userid2);
			int hhzt = jdbUserService.getHHZT(userid2);
			int hhzt_indo = jdbUserService.getusercmnd_state(idno);
			if (jkcount > 0) {
				jsonObject.put("code", -2);
				jsonObject.put("msg", "Vẫn còn những mục chưa hoàn thành, không thể gửi thông tin trùng lặp");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			
			if (hhzt == 1 || hhzt_indo >0) {
				jsonObject.put("code", -4);
				jsonObject.put("msg", "Thẩm định không thông qua, vui lòng một tháng sau đề xuất lại.");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			//借款金额不能小于150万
			int money_jk=Integer.parseInt(borrMoney.replace(",","").replace(".",""));
			if(money_jk<1500000) {
				jsonObject.put("code", -1);
				jsonObject.put("msg", "Tạm thời ngừng chức năng vay 1000.000 vnđ, vui lòng đề xuất lại khoản vay khác.");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			if (dataJJJK != null) {
				long millionSeconds1 = 0;
				long millionSeconds2 = 0;
				String datejkjk = dataJJJK.getString("cl_yj");
				if ((dataJJJK.getString("cl_yj") != null || dataJJJK.getString("cl_yj") != "")
						&& dataJJJK.getString("cl_yj").length() > 14) {
					datejkjk = dataJJJK.getString("cl_yj").substring(0, 13);
				}
				if (dataJJJK.getInt("cl_status") == 3 && "Limit 15 days".equals(datejkjk)) {
					try {
						millionSeconds1 = sdf.parse(dataJJJK.getString("cl_time")).getTime();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} // 毫秒
					date2 = millionSeconds1;
				} else if (dataJJJK.getInt("cl02_status") == 3) {
					try {
						millionSeconds1 = sdf.parse(dataJJJK.getString("cl02_time")).getTime();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} // 毫秒
					date2 = millionSeconds1;
				} else if (dataJJJK.getInt("cl02_status") != 3 && dataJJJK.getInt("cl03_status") == 3  && (!"New User".equals(dataJJJK.getString("cl_yj")))
						&& (!"Bankcard Error".equals(dataJJJK.getString("cl03_yj")))) {
					try {
						millionSeconds2 = sdf.parse(dataJJJK.getString("cl03_time")).getTime();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} // 毫秒
					date2 = millionSeconds2;
				}
			}
			if (date2 != 0) {
				long chazhi = 15 * 1000 * 60 * 60 * 24 - (date111 - date2);
				long dateday = chazhi / (1000 * 60 * 60 * 24);
				long datehour = (chazhi % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
				long datemin = ((chazhi % (1000 * 60 * 60 * 24)) % (1000 * 60 * 60)) / (1000 * 60);
				long datesec = (((chazhi % (1000 * 60 * 60 * 24)) % (1000 * 60 * 60)) % (1000 * 60)) / 1000;
				if (chazhi > 0) {
					jsonObject.put("code", -4);
					jsonObject.put("msg", "Cách lần vay kế tiếp còn " + dateday + " ngày " + datehour + " tiếng "
							+ datemin + " phút " + datesec + " giây");

					this.getWriter().write(jsonObject.toString());
					return null;
				}
			}
			

			String realname = jdbUserService.getRealname(userid2);
			int sfcount = jdbUserService.getSFCount(idno, realname);
			Calendar calendar1 = Calendar.getInstance();
			SimpleDateFormat fmtrq1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			int sfzu[] = new int[sfcount];
			int jkcountzu[] = new int[sfcount];
			if (sfcount > 1) {
				List<DataRow> list = jdbUserService.getALLshenfen(idno, realname);
				for (int i = 0; i < sfcount; i++) {
					DataRow row11 = list.get(i);
					sfzu[i] = row11.getInt("userid");
					jkcountzu[i] = jdbUserService.getJKCount(sfzu[i]);
					if (jkcountzu[i] > 0) {
						jsonObject.put("code", -3);
						jsonObject.put("msg", "Vẫn còn những mục chưa hoàn thành, không thể gửi thông tin trùng lặp");
						DataRow row = new DataRow();
						row.set("userid", userid2);
						row.set("idno", idno);
						row.set("realname", realname);
						row.set("createtime", fmtrq1.format(calendar1.getTime()));
						jdbUserService.addUserException(row);
						this.getWriter().write(jsonObject.toString());
						return null;
					}
				}
			}

			try {

				//int shenhezu[] = { 2001, 2020, 2021, 2029, 2015, 2023, 2031, 2027, 2035, 2037 };
				List<DataRow> list = jdbUserService.getAuditors();
                int shenhezu[] = new int[list.size()];
                for (int i = 0, n = list.size(); i < n; i++) {
                    int cmsUserId = list.get(i).getInt("user_id");
                    shenhezu[i] = cmsUserId;
                }
				//int shenhezu[] = { 2020, 2021, 2029,2023, 2031, 2027, 2035,2004,2038};
				//DataRow  jkData= jdbUserService.getjkNum(userid2+"");
				//int jkAllCount = jkData.getInt("count");
				Random random = new Random();
				int shenheren =888;
				int oldshenheren = jdbUserService.getShenHeRen(userid2);
				 if(shenhezu.length > 0) {
					 int xiabiao = random.nextInt(shenhezu.length);
					 shenheren = shenhezu[xiabiao];
				 }
				int stateold = jdbUserService.getOLDstate(oldshenheren);
				//int statenow = jdbUserService.getOLDstate(shenheren);

				if (oldshenheren != 0 && stateold == 1 && oldshenheren !=2004  && oldshenheren !=2015  && oldshenheren !=2038 && oldshenheren !=2043 ) {
					shenheren = oldshenheren;
				}
			
				
				Calendar calendar = Calendar.getInstance();
				SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				
				DataRow data3 = new DataRow();
				data3.set("userid", userid2);
				data3.set("create_date", fmtrq.format(calendar.getTime()));
				data3.set("daytime", sdfday.format(calendar.getTime()));
				data3.set("jk_money", borrMoney);
				data3.set("jk_date", borrDate);
				data3.set("annualrate", annualrate);
				data3.set("shy_money", borrMoney);
				data3.set("onesh", shenheren);
				data3.set("twosh", shenheren);
				data3.set("threesh", shenheren);
				String username = jdbUserService.getUsername(userid2);
				String mobilePhone = jdbUserService.getMobilePhone(userid2);
				
				DataRow user = jdbUserService.getUserRecThreeInfo(userid2);
				
				if (jkDataLast != null) {
					int yuqts = jkDataLast.getInt("yuq_ts");
					if(yuqts>=0) {
						data3.set("cl_status", 1);
						data3.set("cl02_status", 1);
						data3.set("is_old_user", 1);
						data3.set("cl_yj","Old User");	
					    data3.set("cl_ren","888");
					    data3.set("cl02_yj","Old User");	
					    data3.set("cl02_ren","888");
						int nn = Integer.parseInt(borrMoney.replace(",","").replace(".",""));
						int fklv = 80;
						
						int lx = userMoneyBase.getUMBaseCalculateProductInterest(nn, borrDate, userid2, username);
						if(lx > 0) {
							fklv =100 - lx;
						}
						
						data3.set("sjsh_money", borrMoney);
						data3.set("sjds_money", famt.format(nn * fklv / 100));
						data3.set("jyfk_money", borrMoney);
						data3.set("lx", famt.format(nn * (100 - fklv) / 100));
						
						String userrealname = "!";
					    if(user!=null)
					    {
					    	 userrealname =user.getString("realname");
					    }
					    
					    DataRow row3 =  new DataRow();	
					    row3.set("userid",userid2);
					    row3.set("title", "Thông báo chấp nhận đề nghị vay");//审核通知
					    row3.set("neirong","Kinh gửi "+userrealname+" de xuat vay cua ban da duoc chap thuan, khoan vay se chuyen vao tai khoan trong vong 24h.");		//您的借款申请已通过，借款款项将在24小时内进入您的账户，请及时查收。             
					    row3.set("fb_time", fmtrq.format(calendar.getTime()));				
					    jdbUserService.insertUserMsg(row3);
					    
					    if(borrDate==4) {
						    data3.set("cl03_status","1");
						    data3.set("cl03_yj","Old User");	
						    data3.set("cl03_ren","888");
						    data3.set("spzt","1");
						    data3.set("sfyfk","2");
						    data3.set("cl03_time",fmtrq.format(calendar.getTime()));
					    }
//					    String appName ="OCEAN" ; //APP名字
//					    if(username.substring(0,4).equals("OCEAN")){
//					    	appName="OCEAN";					    	
//					    }																
//						String content   =  appName+" chao "+userrealname+" de xuat vay cua ban da duoc chap thuan, sau 24h chua nhan duoc khoan vay xin";
//						SendFTP sms = new SendFTP();
//						String  response = sms.sendMessageFTP(content,mobilePhone);
					}else if(yuqts>15) {
						data3.set("cl_status", 1);
						data3.set("cl02_status", 1);
						data3.set("is_old_user", 1);
						data3.set("cl_yj","Old User");	
					    data3.set("cl_ren","888");
					    data3.set("cl02_yj","Old User");	
					    data3.set("cl02_ren","888");
						int nn = Integer.parseInt(borrMoney.replace(",","").replace(".",""));
						int fklv = 80;
						
						int lx = userMoneyBase.getUMBaseCalculateProductInterest(nn, borrDate, userid2, username);
						if(lx > 0) {
							fklv =100 - lx;
						}
						data3.set("sjsh_money", borrMoney);
						data3.set("sjds_money", famt.format(nn * fklv / 100));
						data3.set("jyfk_money", borrMoney);
						data3.set("lx", famt.format(nn * (100 - fklv) / 100));
						
//						String appName ="OCEAN" ; //APP名字
//					    if(username.substring(0,4).equals("OCEAN")){
//					    	appName="OCEAN";					    	
//					    }
//						String content   =  appName+" chao! Vui long vao ung dung hoan tat quay video de vay tien lien tay chi voi 10 phut.";
//						SendFTP sms = new SendFTP();
//						String  response = sms.sendMessageFTP(content,mobilePhone);
					}
					
				}
				
				
				
				if (jkcount == 0) {
					jdbUserService.insertJKInfo(data3);
				}
				jsonObject.put("code", 0);
				jsonObject.put("msg", "Đề nghị đã được gửi đi, nhân viên cần 1 ngày làm việc để xác nhận.");
				// 增加消息
				DataRow row = new DataRow();
				row.set("userid", userid2);
				row.set("title", "Đề xuất vay");
				row.set("neirong", "Chúng tôi sẽ cố gắng xử lý đề xuất vay của bạn trong thời gian sớm nhất.");
				row.set("fb_time", fmtrq.format(calendar.getTime()));
				jdbUserService.insertUserMsg(row);
			} catch (Exception e) {
				jsonObject.put("code", -3);
				jsonObject.put("msg", "Lỗi hệ thống, đề nghị vay gửi đi không thành công");
				e.printStackTrace();
			}
			this.getWriter().write(jsonObject.toString());
			return null;
		} else {
			return null;
		}

	}

	// ShowJKJD
	public ActionResult doGetBorrowHisRecord() throws Exception {
		logger.info("request-ip" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
		com.alibaba.fastjson.JSONObject jsonObj = jsonString;
		int userId = jsonObj.getInteger("userId");
		String token = jsonObj.getString("token");

		String idEncrypt = Encrypt.MD5(userId + encrypt);

		if (idEncrypt.equals(token)) {
			DataRow row = new DataRow();
			// 默认第一�??
			int curPage = jsonObj.getInteger("pageNum");
			JSONArray jsonArray = new JSONArray();
			List<DataRow> list = jdbUserService.getBorrHisRecordListPage(curPage, 10, userId);
			if (list.size() > 0) {
				for (DataRow object : list) {
					DataRow data = new DataRow();
					data.put("id", object.getString("id"));
					data.put("isRepaid", object.getString("hkqd"));
					data.put("finalMoney", object.getString("sjsh_money"));
					data.put("dateLo", object.getString("jk_date"));
					data.put("finalActualMoney", object.getString("sjds_money"));
					data.put("loValue", object.getString("jk_money"));
					data.put("createDate", object.getString("create_date"));
					jsonArray.add(JSONObject.fromBean(data));
				}
			}
			int moneycode = jdbUserService.getMoneyCode(userId);
			row.set("historyList", jsonArray);
			row.set("changeValue", moneycode + "");
			JSONObject object = JSONObject.fromBean(row);
			this.getWriter().write(object.toString());
			return null;
		} else {
			JSONObject jsObj = new JSONObject();
			jsObj.put("code", 101);
			jsObj.put("msg", "Lỗi hệ thống, vui lòng thử lại sau！");
			this.getWriter().write(jsObj.toString());
			return null;
		}

	}

	// 项目进行还款(详情)
	public ActionResult doGetRefundRecord() throws Exception {
		logger.info("请求ip" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
		com.alibaba.fastjson.JSONObject jsonObj = jsonString;

		int jkid = jsonObj.getInteger("userJkId");
		JSONObject jsonObject = new JSONObject();
		String token = jsonObj.getString("token");

		String idEncrypt = Encrypt.MD5(jkid + encrypt);
		System.out.print(idEncrypt);
		if (idEncrypt.equals(token)) {
			if (jkid == 0) {
				jsonObject.put("code", -1);
				jsonObject.put("msg", "Lỗi hệ thống, vui lòng thử lại sau！");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			DataRow dataJK = new DataRow();
			dataJK = jdbUserService.findUserJKFQById(jkid);
			String newdateSH = dataJK.getString("sjsh_money").replace(",", "").replace(".", "");
			int yanqi15 = Integer.parseInt(newdateSH) * 20 / 100;
			int yanqi30 = Integer.parseInt(newdateSH) * 30 / 100;
			int codede = dataJK.getInt("hkfq_code");
			if (codede == 0) {
				dataJK = jdbUserService.findUserJKById(jkid);
			}
			JSONObject object = JSONObject.fromBean(dataJK);
			object.put("code", 0);
			object.put("dataYQ15", yanqi15);
			object.put("dataYQ30", yanqi30);
			this.getWriter().write(object.toString());
			return null;
		} else {
			jsonObject.put("code", -1);
			jsonObject.put("msg", "Lỗi hệ thống, vui lòng thử lại sau！");
			this.getWriter().write(jsonObject.toString());
			return null;
		}

	}

	// 确定借款视频是否上传（返回结果）
	public ActionResult doCheckVideoUrl() throws Exception {

		logger.info("request-ip:" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonObj = getRequestJson(request);
		String videoUrl = jsonObj.getString("videoUrl");
		logger.info("Video address" + videoUrl);
		int userid = jsonObj.getInteger("userId");
		String token = jsonObj.getString("token");

		String idEncrypt = Encrypt.MD5(userid + "M88" + encrypt);

		if (idEncrypt.equals(token)) {
			JSONObject jsonObject = new JSONObject();

			if (userid == 0) {
				jsonObject.put("code", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước"); // 请先登录
				this.getWriter().write(jsonObject.toString());

				return null;

			}
			// 根据用户ID 查询�??要上传视频的借款项目

			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			int jkshid = jdbUserService.getJKshid(userid);
			if (jkshid == 0) {

				jsonObject.put("code", -2);
				jsonObject.put("msg",
						"Video cần tải không tồn tại, vui lòng liên hệ bộ phận dịch vụ khách hàng để được hỗ trợ"); // 要上传的视频的项目不存在
																													// 请联系客服处理
				// 请联系客服处�??
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			DataRow row = new DataRow();
			row.set("id", jkshid);
			row.set("spdz", videoUrl);
			row.set("spzt", 1);
			row.set("spsj", fmtrq.format(calendar.getTime()));
			jdbUserService.updateJKSPInfo(row);

			DataRow row3 = new DataRow();
			row3.set("userid", userid);
			row3.set("title", "Thông báo chấp nhận đề nghị vay"); // 审核通知
			row3.set("neirong", "Video của bạn đã đăng lên thành công , hãy lưu ý đến kết quả xét duyệt cuối cùng."); // 您的借款申请视频已上传成功，请留意最终审核结果
			row3.set("fb_time", fmtrq.format(calendar.getTime()));
			jdbUserService.insertUserMsg(row3);
			jsonObject.put("code", 0);
			jsonObject.put("msg", "Cập nhật thành công video đề nghị vay"); // 更新借款视频成功
			this.getWriter().write(jsonObject.toString());

			return null;
		} else {
			return null;
		}

	}

	public ActionResult doUpdateRefundMoney() throws Exception {
		logger.info("request-ip:" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonObj = getRequestJson(request);
		String userid = jsonObj.getString("userId");
		String hkqd = jsonObj.getString("userHkqd");

		String token = jsonObj.getString("token");

		String idEncrypt = Encrypt.MD5(hkqd + userid + encrypt);

		if (idEncrypt.equals(token)) {
			String jkid = jdbUserService.getUIHKQD(userid);
			JSONObject jsonObject = new JSONObject();
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			try {
				DataRow row3 = new DataRow();
				row3.set("id", jkid);
				row3.set("hkqd", 1);//
				row3.set("hkpz", hkqd);
				row3.set("hkpz_time", fmtrq.format(calendar.getTime()));
				jdbUserService.updateUserHKQD(row3);
				jsonObject.put("code", 0);
				jsonObject.put("msg", "Thành công");
			} catch (Exception e) {
				jsonObject.put("msg", "Lỗi hệ thống, vui lòng thử lại sau！");
				e.printStackTrace();
			}
			this.getWriter().write(jsonObject.toString());
			return null;
		} else {
			return null;
		}

	}

	public ActionResult doUserLocationCheck() throws Exception {
		logger.info("request-ip:" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
		com.alibaba.fastjson.JSONObject jsonObj = jsonString;

		int userid = jsonObj.getInteger("userId");
		String token = jsonObj.getString("token");
		String idEncrypt = Encrypt.MD5(userid + encrypt);

		if (idEncrypt.equals(token)) {
			String dwlat = jsonObj.getString("downlat");
			String dwlng = jsonObj.getString("downlng");

			JSONObject jsonObject = new JSONObject();
			try {
				String uid = jdbUserService.getUserUserID(userid);
				if (!uid.isEmpty() || !uid.equals("")) {
					DataRow row5 = new DataRow();
					row5.set("userId", userid);
					String jtdz = "none";
					String jsonstring = sendGet(dwlat.replace(",", "."), dwlng.replace(",", "."));
					com.alibaba.fastjson.JSONObject json = (com.alibaba.fastjson.JSONObject) JSON.parse(jsonstring);
					com.alibaba.fastjson.JSONArray jsonArray = json.getJSONArray("results");
					if (jsonArray.size() > 0) {
						com.alibaba.fastjson.JSONObject object = jsonArray.getJSONObject(0);
						jtdz = object.getString("formatted_address");
					}
					row5.set("dwlat", dwlat.replace(",", "."));
					row5.set("dwlng", dwlng.replace(",", "."));
					row5.set("jtdz", jtdz);
					row5.set("create_time", new Date());
					jdbUserService.addUserDWIP(row5);

					jsonObject.put("code", 0);
					jsonObject.put("msg", "Location Success");
				}
			} catch (Exception e) {
				jsonObject.put("code", -3);
				jsonObject.put("msg", "Lỗi hệ thống, vui lòng thử lại sau！");
				e.printStackTrace();
			}
			this.getWriter().write(jsonObject.toString());
			return null;
		} else {
			return null;
		}

	}

	/**
	 * Get the customer's real IP address
	 *
	 * @return
	 */
	public String getipAddr() {
		String ip = getRequest().getHeader("X-Real-IP");
		if (StringHelper.isEmpty(ip)) {
			ip = jdbUserService.getRemortIP(getRequest());
		}
		return ip;
	}

	/**
	 * 设置返回消息
	 * 
	 * @param error
	 */
	public void setError(DataRow error) {
		JSONObject object = JSONObject.fromBean(error);
		this.getWriter().write(object.toString());
	}

	private com.alibaba.fastjson.JSONObject getRequestJson(HttpServletRequest request) throws IOException {
		InputStream in = request.getInputStream();
		byte[] b = new byte[10240];
		int len;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while ((len = in.read(b)) > 0) {
			baos.write(b, 0, len);
		}
		String bodyText1 = new String(baos.toByteArray(), "UTF-8");
		bodyText1 = bodyText1.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
		String bodyText = URLDecoder.decode(bodyText1, "UTF-8");
		com.alibaba.fastjson.JSONObject json = (com.alibaba.fastjson.JSONObject) JSON.parse(bodyText);
		if (true) {
			logger.info("传输成功！");
		}
		return json;
	}

	/**
	 * 向指定URL发�?�GET方法的请�??
	 * 
	 * @param url   发�?�请求的URL
	 * @param param 请求参数，请求参数应该是name1=value1&name2=value2的形式�??
	 * @return URL�??代表远程资源的响�??
	 */

	public static String sendGet(String lat, String lng) {
		String url = "https://maps.google.com/maps/api/geocode/json?key=AIzaSyAWZWiG0MrS3CQC0NnyhczbnneQPB5Fu0Y&latlng="
				+ lat + "," + lng + "&language=EN&sensor=false";
		StringBuilder json = new StringBuilder();
		try {
			URL getUrl = new URL(url);
			// 返回URLConnection子类的对�??
			HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
			// 连接
			connection.connect();
			InputStream inputStream = connection.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			// 使用Reader读取输入�??
			BufferedReader reader = new BufferedReader(inputStreamReader);
			String lines;
			while ((lines = reader.readLine()) != null) {
				json.append(lines);
			}
			reader.close();
			// 断开连接
			connection.disconnect();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json.toString();
	}

	public ActionResult doGetMessage() throws Exception {
		logger.info("request-ip:" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonObj = getRequestJson(request);

		String token = jsonObj.getString("token");
		String idEncrypt = Encrypt.MD5("msg" + encrypt);
		JSONObject jsonObject = new JSONObject();
		if (idEncrypt.equals(token)) {
			List<DataRow> list = jdbUserService.getMsgCenter();
			JSONArray jsonArray = new JSONArray();
			for (Object object : list) {
				jsonArray.add(JSONObject.fromBean(object));
			}
			jsonObject.put("msgList", jsonArray);

		} else {
			JSONObject jsObj = new JSONObject();
			jsObj.put("code", 101);
			jsObj.put("msg", "Lỗi hệ thống, vui lòng thử lại sau！");
			this.getWriter().write(jsObj.toString());
		}

		this.getWriter().write(jsonObject.toString());
		return null;
	}

	public ActionResult doGetNews() throws Exception {
		logger.info("request-ip:" + getipAddr());
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		try {
			List<DataRow> listRow = jdbUserService.getNews(5);
			List<DataRow> listRawData = new ArrayList<DataRow>();
			for (int i = 0; i < listRow.size(); i++) {
				DataRow rawData = new DataRow();
				rawData.set("Id", listRow.get(i).getString("Id"));
				rawData.set("title", listRow.get(i).getString("title"));
				rawData.set("link", listRow.get(i).getString("link"));
				rawData.set("link_img", listRow.get(i).getString("link_img"));
				rawData.set("sort_content", listRow.get(i).getString("sort_content"));
				rawData.set("full_content", listRow.get(i).getString("full_content"));
				rawData.set("daytime", listRow.get(i).getString("daytime"));
				listRawData.add(rawData);
			}
			jsonArr = JSONArray.fromCollection(listRawData);
			jsonObject.put("code", 0);
			jsonObject.put("msg", "success");
			jsonObject.put("values", jsonArr);
		} catch (Exception e) {
			jsonObject.put("code", -3);
			jsonObject.put("msg", "Lỗi hệ thống, vui lòng thử lại sau！");
			e.printStackTrace();
		}
		this.getWriter().write(jsonObject.toString());
		return null;
	}
	public ActionResult doUPdate() throws Exception {
		List<DataRow> listRow = jdbUserService.getUPdaterow();
		for (int i = 0; i < listRow.size(); i++) {
			DataRow rawData = new DataRow();
			rawData = listRow.get(i);
			String cardusernameString = rawData.getString("cardusername");
			DataRow row5 = new DataRow();
			row5.set("userid", rawData.getString("userid"));
			row5.set("realname", cardusernameString);
			jdbUserService.updateRowNAME(row5);
		}
		return null;
	}
	// funpay订单号保存
	public ActionResult doSAVEDingDan() throws Exception {
		logger.info("请求ip" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
		com.alibaba.fastjson.JSONObject jsonObj = jsonString;
		SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		DecimalFormat famt = new DecimalFormat("###,###");
		String userid = jsonObj.getString("yonghuid");
		String orderId = jsonObj.getString("dingdanid");
		//String payId = jsonObj.getString("payId");
		int Money = jsonObj.getInteger("jine");
		JSONObject jsonObject = new JSONObject();
		String miwen = jsonObj.getString("token");

		String jiamiwen = Encrypt.MD5(userid + encrypt);

		if (jiamiwen.equals(miwen)) {

			DataRow row = new DataRow();
			row.set("userid", userid);
			row.set("orderid", orderId);
			//row.set("payid", payId);
			row.set("ordermoney", famt.format(Money));
			row.set("create_time", fmtrq.format(new Date()));
			jdbUserService.insertUserFunPayOrderid(row);

			jsonObject.put("code", 0);
			this.getWriter().write(jsonObject.toString());
			return null;
		} else {
			return null;
		}

	}

	public ActionResult doResHDdz() throws Exception { 
		 HttpServletRequest request = getRequest(); 
		 com.alibaba.fastjson.JSONObject jsonString =getRequestJson(request); 
		 com.alibaba.fastjson.JSONObject jsonObj = jsonString; 
		 String code = jsonObj.getString("code"); 
		 String message = jsonObj.getString("msg"); 
		 String result = jsonObj.getString("result");
		 SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		 JSONObject jsonObject = new JSONObject(); 
		 String ip = getipAddr();
		 if(!"47.52.21.255".equals(ip)){
			 DataRow row = new DataRow();
			 row.set("code", code);
			 row.set("msg", message);
			 row.set("result", result);
			 row.set("errorcontent", "IP error:"+ip);
			 row.set("errortime", fmtrq.format(new Date()));
			 //加入到表中、、、、、、、、、、、、
			 jbdcms3Service.addOrderError(row);
			 jsonObject.put("error", -1);
			 jsonObject.put("msg", "IP地址错误！"); // Vui lòng đăng
			 this.getWriter().write(jsonObject.toString());
			 return null;
		 }
	     logger.info("进入回调地址");  
		 logger.info(code); 
		 logger.info(message);
		 logger.info(result); 
		 logger.info("进入线下支付还款结果返回"); 
		 if("10000".equals(code)) {
			 com.alibaba.fastjson.JSONObject jsonObjresult = com.alibaba.fastjson.JSONObject.parseObject(result);
			 String merchantID = jsonObjresult.getString("merchantID");
			 String businessID = jsonObjresult.getString("businessID");
			 String feeID = jsonObjresult.getString("feeID");
			 String amount = jsonObjresult.getString("amount");
			 String currency = jsonObjresult.getString("currency");
			 String name = jsonObjresult.getString("name");
			 String tradeNo = jsonObjresult.getString("tradeNo");
			 String orderNo = jsonObjresult.getString("orderNo");
			 String orderstate = jsonObjresult.getString("status");
			 if(!"0".equals(orderstate)){
				 DataRow row = new DataRow();
				 row.set("code", code);
				 row.set("msg", message);
				 row.set("result", result);
				 row.set("errorcontent", "state error:"+orderstate);
				 row.set("errortime", fmtrq.format(new Date()));
				 //加入到表中、、、、、、、、、、、、
				 jbdcms3Service.addOrderError(row);
				 jsonObject.put("error", -1);
				 jsonObject.put("msg", "state状态错误！"); // Vui lòng đăng
				 this.getWriter().write(jsonObject.toString());
				 return null;
			 }
			 //根据传的订单号去表中找
			 DataRow datarow = jbdcms3Service.getFunpayOrderNo(orderNo);
			 //判断订单金额和表中的金额是否一致
			 if (datarow == null) 
			 { 
				 DataRow row = new DataRow();
				 row.set("code", code);
				 row.set("msg", message);
				 row.set("result", result);
				 row.set("errorcontent", "订单号为空:"+orderNo);
				 row.set("errortime", fmtrq.format(new Date()));
				 //加入到表中、、、、、、、、、、、、
				 jbdcms3Service.addOrderError(row);
				 jsonObject.put("error", -1);
				 jsonObject.put("msg", "订单号为空:"+orderNo); // Vui lòng đăng
				 this.getWriter().write(jsonObject.toString());
				 return null;
			 }
			//判断订单有没有被处理
			 if (datarow.getInt("orderstate") == 1) 
			 { 
				 DataRow row = new DataRow();
				 row.set("code", code);
				 row.set("msg", message);
				 row.set("result", result);
				 row.set("errorcontent", "订单号已经被处理:"+orderNo);
				 row.set("errortime", fmtrq.format(new Date()));
				 //加入到表中、、、、、、、、、、、、
				 jbdcms3Service.addOrderError(row);
				 jsonObject.put("error", -1);
				 jsonObject.put("msg", "订单号已经被处理:"+orderNo); // Vui lòng đăng
				 this.getWriter().write(jsonObject.toString());
				 return null;
			 }
			 //判断订单金额和表中的金额是否一致
			 if (!datarow.getString("ordermoney").replace(",","").replace(".","").equals(amount)) 
			 { 
				 DataRow row = new DataRow();
				 row.set("code", code);
				 row.set("msg", message);
				 row.set("result", result);
				 row.set("errorcontent", "amount的金额与实际金额不符！金额:"+amount+"订单号:"+orderNo);
				 row.set("errortime", fmtrq.format(new Date()));
				 //加入到表中、、、、、、、、、、、、
				 jbdcms3Service.addOrderError(row);
				 jsonObject.put("error", -1);
				 jsonObject.put("msg", "amount的金额与实际金额不符！金额:"+amount+"订单号:"+orderNo); // Vui lòng đăng
				 this.getWriter().write(jsonObject.toString());
				 return null;
			 } 
			 String userId = datarow.getString("userid");
			 logger.info("请求ID:" + userId);
				String jkid = jbdcms3Service.getJKID(userId); // 接收到的还款id
				logger.info(jkid);
				String hkmoney = amount; // 还款金额
				logger.info(hkmoney);
				
				String sjsh_money = jbdcms3Service.getsjsh(jkid);
				
				int yqts = jbdcms3Service.getyqts(jkid);
				String yuq_lx = jbdcms3Service.getYQLX(jkid);// 逾期利息
				String yuq_yhlx = jbdcms3Service.getYQYH(jkid); // 逾期利息
				String newsjsh_money = sjsh_money.replace(",", "").replace(".",""); // 逾期利息
				String newyuq_lx = yuq_lx.replace(",", "").replace(".",""); // 逾期利息
				String newyuq_yhlx = yuq_yhlx.replace(",", "").replace(".",""); // 逾期利息
				int sjsh = Integer.parseInt(newsjsh_money);
				int hkm = Integer.parseInt(hkmoney);
				int yqlx = Integer.parseInt(newyuq_lx);
				int yqyhlx = Integer.parseInt(newyuq_yhlx);
				int sum = sjsh + yqlx;
				DecimalFormat df = new DecimalFormat("###,###");
				String bankorderid = tradeNo;
				
				int state = Integer.parseInt(orderNo.substring(1, 2)); // 接收到还款选择

				String HKtime = fmtrq.format(new Date());// 接收还款时间
				String payOrder = orderNo;
				logger.info("还款订单：" + payOrder);
				logger.info(HKtime);
				
				String date2 = HKtime;
				DataRow dataRowjk = jbdcms3Service.getUserJKInfo(jkid);
				SimpleDateFormat fmtrq111 = new SimpleDateFormat("yyyy-MM-dd");
				String time111 = fmtrq111.format(new Date());
				String cuishouid = dataRowjk.getString("cuishou_id");
				String cuishoum1 = dataRowjk.getString("cuishou_m1");
				String cuishoum2 = dataRowjk.getString("cuishou_m2");
				DataRow row6 = new DataRow();
				// 支付成功（表示还款成功）
				DataRow addorder = new DataRow();
				int orderpaytype = 8; // 线下还款为3
				String remark = "";
				if (state == 1) {
					remark = "全额还款";
				} else if (state == 2) {
					if (yqlx >= hkm) {
						remark = "逾期利息还款";
					} else {
						remark = "逾期利息还完，部分本金还款";
					}
				} else if (state == 3) {
					remark = "延期还款";
				} else if (state == 4) {
					remark = "延期还款";
				}

				String payNumber = payOrder;
				// 生成订单
				addorder.set("userid", userId);
				addorder.set("rechargetime", fmtrq.format(new Date()));
				addorder.set("userhktime", HKtime);
				addorder.set("bankjztime", getStrParameter("hkjz").replace("&nbsp;", " "));
				addorder.set("rechargetype", 33);
				addorder.set("result", 1);
				addorder.set("operatorId", 888);
				addorder.set("rzcode", "0000");
				addorder.set("paynumber", payNumber);
				addorder.set("rechargenumber", jkid);
				addorder.set("remark", remark);
				addorder.set("ordertype", 1);
				addorder.set("bankorderid", bankorderid);
				addorder.set("orderpaytype", orderpaytype);
				addorder.set("cuishouid", dataRowjk.getString("cuishou_id"));

				if (state == 1) {
					
					addorder.set("dqyqts", yqts);
					addorder.set("dqyqlx", yqlx);
					addorder.set("rechargemoney",df.format(sum));
					// 算出产生多的逾期天数
					DataRow jkxm2 = new DataRow();
					jkxm2.set("id", jkid);
					jkxm2.set("jksfwc", 1);
					jkxm2.set("sfyhw", 1);
					jkxm2.set("hk_time", date2);
					jbdcms3Service.updateJKHK(jkxm2);
					
					// 逾期天数>0，增加催收的催回金额
					if (yqts > 0) {
						if(yqts < 16){
							cuishouid = cuishoum1;
						}else if(yqts > 15 && yqts < 46){
							cuishouid = cuishoum2;
						}
						DataRow datacs = jbdcms3Service.getCuishouBG(cuishouid,time111);
						if (datacs == null) {
							DataRow row11 = new DataRow();
							row11.set("csid", cuishouid);
							row11.set("ycsjine", sum);
							row11.set("time", time111);
							jbdcms3Service.insertCuiBG(row11);
						} else {
							double ysje = datacs.getDouble("ycsjine");
							int cuiid = datacs.getInt("id");
							DataRow row11 = new DataRow();
							row11.set("id", cuiid);
							row11.set("ycsjine", ysje + sum);
							jbdcms3Service.updateCuiBG(row11);
						}
					}
					
				} else if (state == 2) {

					addorder.set("rechargemoney", df.format(hkm));
					addorder.set("dqyqts", yqts);
					addorder.set("dqyqlx", yqlx);
				    DataRow jkxm2 = new DataRow();
					jkxm2.set("id", jkid);
					if (hkm <= yqlx) {
						jkxm2.set("yuq_yhlx", df.format(hkm + yqyhlx));
						jkxm2.set("yuq_lx", df.format(yqlx - hkm));
					} else {
						jkxm2.set("sjsh_money", df.format(sjsh+yqlx-hkm));
						jkxm2.set("yuq_yhlx", df.format(yqlx + yqyhlx));
						jkxm2.set("yuq_lx", 0);
					}	
					jkxm2.set("hk_time", date2);
					jbdcms3Service.updateJKHK(jkxm2);
					// 把金额加到催回金额里面
					if(yqts > 0){
						if(yqts < 16 ){
							cuishouid = cuishoum1 ;
						}
						else if(yqts > 15 && yqts < 46){
							cuishouid = cuishoum2;
						}
						DataRow datacs = jbdcms3Service.getCuishouBG(cuishouid, time111);
						if (datacs == null) {
							DataRow row11 = new DataRow();
							row11.set("csid", cuishouid);
							row11.set("ycsjine", hkm);
							row11.set("time", time111);
							jbdcms3Service.insertCuiBG(row11);
						} else {
							double ysje = datacs.getDouble("ycsjine");
							int cuiid = datacs.getInt("id");
							DataRow row11 = new DataRow();
							row11.set("id", cuiid);
							row11.set("ycsjine", ysje + hkm);
							jbdcms3Service.updateCuiBG(row11);
						}
					}
				} 
				else if (state == 3 || state == 4) {
					addorder.set("rechargemoney", df.format(hkm));
					int yqyqlx = 0;
					if (state == 3) {
						if (sjsh <= 1000000) {
							yqyqlx = sjsh * 30 / 100;
						} else {
							yqyqlx = sjsh * 20 / 100;
						}
					} else if (state == 4) {
						yqyqlx = sjsh * 30 / 100;
					} 
					addorder.set("dqyqts", yqts);
					addorder.set("dqyqlx", yqlx);
					DataRow jkxm3 = new DataRow();
					jkxm3.set("id", jkid);
					jkxm3.set("yuq_yhlx", df.format(yqlx+yqyhlx));
					jkxm3.set("yuq_lx", 0);
					jkxm3.set("yuq_ts", 0);
					jkxm3.set("hk_time", date2);
					jbdcms3Service.updateJKHK(jkxm3);
					
					int tianshu = 0;
					String hkyqtime = dataRowjk.getString("hkyq_time");
					String hkfqtime = dataRowjk.getString("hkfq_time");
					String hkfqcishu = dataRowjk.getString("hkfq_cishu");
					String hkfqlx = dataRowjk.getString("hkfq_lx");
					String hkfqzlx = dataRowjk.getString("hkfqzlx");
					int fqlx = 0;
					int fqzlx = 0;
					if (!"".equals(hkfqlx)) {
						String aa = hkfqlx.replace(",", "").replace(".","");
						String bb = hkfqzlx.replace(",", "").replace(".","");
						fqlx = Integer.parseInt(aa);
						fqzlx = Integer.parseInt(bb);
					}
					if (state == 3) {
						tianshu = 15 + yqts;
					} else if (state == 4) {
						tianshu = 30 + yqts;
					} 
					Date time1 = new Date();
					if (!"".equals(hkfqtime)) {
						time1 = fmtrq.parse(hkfqtime);
					} else {
						time1 = fmtrq.parse(hkyqtime);
					}
					Calendar cl = Calendar.getInstance();
					cl.setTime(time1);
					cl.add(Calendar.DATE, tianshu);
					String fenqitime = "";
					fenqitime = fmtrq.format(cl.getTime());
					DataRow jkxm2 = new DataRow();
					jkxm2.set("id", jkid);
					int yyyqts = jbdcms3Service.getyqts(jkid);
					if (yyyqts <= 15) {
						jkxm2.set("cuishou_id", 0);
					}
					jkxm2.set("hkfq_code", 1);
					jkxm2.set("hkfq_time", fenqitime);
					jkxm2.set("hkfq_day", tianshu);
					jkxm2.set("hkfqzlx", df.format(fqzlx + yqyqlx));
					jkxm2.set("hkfq_lx", df.format(fqlx + hkm));
					jkxm2.set("hkfq_cishu", hkfqcishu + state);
					jkxm2.set("hk_time", date2);
					jkxm2.set("hkqd", 0);
					jkxm2.set("tzjx_ts", 0);
					jkxm2.set("tzjx_lx", 0);
					jkxm2.set("tzjx", 0);
					jbdcms3Service.updateJKHK(jkxm2);
					row6.set("state", 5);
					row6.set("neirong","Đề xuất gia hạn đã được duyệt, chúng tôi đã nhận được phí gia hạn "+ df.format(hkm) + ", thời hạn trả vay đổi thành "+ fenqitime);
				}
				jbdcms3Service.addOrder(addorder);
				
				DataRow row111 = new DataRow();
				row111.set("id", datarow.getInt("id"));
				row111.set("orderstate", 1);
				jbdcms3Service.updateOrderNo(row111);
				String money = "0";
				row6.set("userid", userId);
				row6.set("title", "Thông báo về việc trả vay"); // 还款通知
				if (state == 1) {
					row6.set("neirong", "Trả vay thành công " + df.format(sum));
					money = df.format(sum);
				} else if (state == 2 || state == 3) {
					row6.set("neirong", "Trả vay thành công " + df.format(hkm));
					money = df.format(hkm);
				}// 成功还款多少钱！
				row6.set("fb_time", date2);
				jbdcms3Service.insertUserMsg(row6);
				// 给还款人短信
				
				
				jsonObject.put("error", 1);
				jsonObject.put("msg", "Thành công");
				this.getWriter().write(jsonObject.toString());
				return null;
			 
		 } // 不是正确的返回,需要记录下来 else {
			 DataRow row = new DataRow();
			 row.set("code", code);
			 row.set("msg", message);
			 row.set("result", result);
			 row.set("errorcontent", "code error:"+code);
			 row.set("errortime", fmtrq.format(new Date()));
			 //加入到表中、、、、、、、、、、、、
			 jbdcms3Service.addOrderError(row);
			 jsonObject.put("error", -1);
			 jsonObject.put("msg", "状态码错误！"); // Vui lòng đăng
			 this.getWriter().write(jsonObject.toString());
			 return null;
		 }
	
	//a工作认证
	public ActionResult doOceanWorkRZ() throws Exception {
		logger.info("请求ip" + getipAddr());
		HttpServletRequest request = getRequest();
		
		com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
		com.alibaba.fastjson.JSONObject jsonObj = jsonString;

		JSONObject jsonObject = new JSONObject();
		
		String workName = jsonObj.getString("workName").trim().replace("&nbsp;", " ");  //公司名称
		String userid = jsonObj.getString("userid");
		String tel = jsonObj.getString("tel");   //公司电话
		String miwen = jsonObj.getString("token"); 
		String jiamiwen = Encrypt.MD5(userid + tel + encrypt);

		logger.info("miwen上传：" +miwen);
		logger.info("jiamiwen：" + jiamiwen);
		if (jiamiwen.equals(miwen)) {
			String address = jsonObj.getString("address").trim().replace("&nbsp;", " "); //公司地址
			String position = jsonObj.getString("position").trim().replace("&nbsp;", " ");  //职位
			String pay = jsonObj.getString("pay").trim().replace("&nbsp;", " ");   //薪资范围
			String time = jsonObj.getString("time");    //入职时间
			String company = jsonObj.getString("company").trim().replace("&nbsp;", " ");   //行业类型
			String p1 = getStrParameter("p1");
			String p2 = getStrParameter("p2");
			String p3 = getStrParameter("p3");
			String ui = mofaUserService.getUIWork(userid);

			int yhbd = mofaUserService.getUserBank(userid);
			int lianxi = mofaUserService.getUserLianXi(userid);
			int shenfen = mofaUserService.getUserShenFen(userid);

			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			try {

				// 将数据存到数据库中
				if (ui.equals("")) {
					DataRow row5 = new DataRow();
					row5.set("userid", userid);
					row5.set("workname", workName);
					row5.set("tel", tel);
					row5.set("address", address);
					row5.set("position", position);
					row5.set("pay", pay);
					row5.set("time", time);
					row5.set("company", company);
					row5.set("p1", p1);
					row5.set("p2", p2);
					row5.set("p3", p3);
					row5.set("create_time", fmtrq.format(calendar.getTime()));
					mofaUserService.addUserWork(row5);
				} else {
					DataRow row5 = new DataRow();
					row5.set("userid", userid);
					row5.set("workname", workName);
					row5.set("tel", tel);
					row5.set("address", address);
					row5.set("position", position);
					row5.set("pay", pay);
					row5.set("time", time);
					row5.set("company", company);
					row5.set("p1", p1);
					row5.set("p2", p2);
					row5.set("p3", p3);
					row5.set("create_time", fmtrq.format(calendar.getTime()));
					mofaUserService.updateUserWork(row5);
				}
				logger.info("WorkRZ:"+userid+"--:"+workName);
				DataRow row3 = new DataRow();
				row3.set("id", userid);
				row3.set("isjop", 1);// 工作认证为1

				if (yhbd == 1 & shenfen == 1 && lianxi == 1) {
					row3.set("vipStatus", 1);// 工作认证为1
				}else {
					row3.set("vipStatus", 0);
				}

				row3.set("profession", 2);// 工作认证为1
				mofaUserService.updateUserInfoH(row3);
				jsonObject.put("oceanC", 0);
				jsonObject.put("oceanM", "Thành công");
			} catch (Exception e) {

				jsonObject.put("oceanC", -3);
				jsonObject.put("oceanM", "Lỗi hệ thống, vui lòng thử lại sau！");
				e.printStackTrace();
			}
			this.getWriter().write(jsonObject.toString());
			return null;
		} else {
			return null;
		}

	}
		

}
