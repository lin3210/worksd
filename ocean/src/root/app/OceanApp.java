package root.app;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
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
import com.project.service.account.JBDcmsService;
import com.project.service.account.MofaUserService;
import com.project.utils.MemCachedUtil;
import com.shove.security.Encrypt;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.util.SessionHelper;
import com.thinkive.base.util.StringHelper;
import com.thinkive.web.base.ActionResult;
import com.thinkive.web.base.BaseAction;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import root.current.Base64;
import root.current.OLVSacombank;
import root.current.SSLClient;
import root.order.UserMoneyBase;
import root.tool.SendMsgCL;
import root.tool.SendMsgTYH;

/**
 * r2020年3月6日
 * add work rz
 * 说明： 在原来API流程上增加工作认证
 */
public class OceanApp extends BaseAction {
	private static Logger logger = Logger.getLogger(OceanApp.class);
	/* private static UserService userService = new UserService(); */
	private static JBDUserService jdbUserService = new JBDUserService();
	private static JBDcms3Service jbdcms3Service = new JBDcms3Service();
	private static MofaUserService mofaUserService = new MofaUserService();
	private static JBDcmsService jbdcmsService = new JBDcmsService();
	private static UserMoneyBase  userMoneyBase = new UserMoneyBase();
	
	String jiami = "S9uKJD8HEOI9j9O89ujhdf8H093kfld12NVbvc";
	long time = 1000 * 60 * 3;

	public ActionResult doOceanHome() throws IOException {  
		logger.info("请求ip" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
		com.alibaba.fastjson.JSONObject jsonObj = jsonString;
		
		if( jsonObj == null ||  jsonObj.getString("oceanid") == null || jsonObj.equals("") || jsonObj.getString("oceanid").equals("")) {
			return null;
		}
		
		String miwen = jsonObj.getString("oceantoken");
		int userId = jsonObj.getInteger("oceanid");
		String jiamiwen = Encrypt.MD5(userId + jiami);
		if (jiamiwen.equals(miwen)) {

			logger.info("用户请求手机:" + userId);
			DataRow row = new DataRow();
			DataRow isStep1 = new DataRow();
			DataRow isStep2 = new DataRow();
			DataRow isStep3 = new DataRow();
			DataRow isStep4 = new DataRow();
			if (userId != 0) {
				DataRow data = jdbUserService.findUserById(userId + "");
				DataRow data1 = jdbUserService.findUserZPById(userId + "");

				DataRow data4 = jdbUserService.findUserHKQD(userId + "");
				DataRow rz = jdbUserService.getRZBK(userId);
				DataRow rzsf = jdbUserService.getRZSF(userId);
				DataRow jkData = jdbUserService.getjkMoney(userId + "");
				
				// 未再借用户标识2019年6月17日lin
			    if(1==data.getInt("user_mark")) {
			     //把user表中的user_mark更新为2 提交借款;
			    jdbUserService.updateRowUserMark(data.getInt("id"), 2);
			    }
				DecimalFormat famt = new DecimalFormat("###,###");
				String maxMoney = "";
				int sjshmoneyshow = userMoneyBase.getUMBaseMaxLoanMoney_showApp(userId);   //最大额度显示
				int sjshmoney = userMoneyBase.getUMBaseMaxLoanMoney(userId);   //最大额度选择
				int maxCount = userMoneyBase.getUMBaseSuccessfulLoanNum(userId, maxMoney);
				row.set("oceanMm", famt.format(sjshmoney));
				row.set("oceanMmShow", famt.format(sjshmoneyshow));
				row.set("oceanMc", maxCount);
				row.set("oceanSsr", 1);
				row.set("oceanVc", 8);
				if (data != null) {
					String username = data.getString("username");
					String phone = data.getString("mobilephone");
					row.set("oceanCp", phone);

					if (data4 != null) {
						row.set("oceanIr", data4.getString("hkqd"));
					} else {
						row.set("oceanIr", "0");
					}
					
					row.set("oceanPd", data.getString("changejkts"));//1为7天和14天，0为15天和30天
					
					row.set("oceanFdv", "0.1955");
					row.set("oceanTdv", "0.291");
					row.set("oceanPs", "{\"7\":[0.0045,0.3]}");  //,\"30\":[0.009,0.4]
					
					if (rz != null) {
						isStep1.set("oceanBun", rz.getString("cardusername"));
						isStep1.set("oceanBn", rz.getString("bankname"));
						isStep1.set("oceanCn", rz.getString("cardname"));
						if (rz.getString("cardno").length() > 9) {
							isStep1.set("oceanCd", "********" + rz.getString("cardno")
									.substring(rz.getString("cardno").length() - 4, rz.getString("cardno").length()));

						} else {
							isStep1.set("oceanCd", "********" + rz.getString("cardno")
									.substring(rz.getString("cardno").length() - 4, rz.getString("cardno").length()));

						}
					}

					if (rzsf != null) {
						isStep2.set("oceanIc", rzsf.getString("idno"));
						isStep2.set("oceanHa", rzsf.getString("homeaddress"));
						isStep2.set("oceanBir", rzsf.getString("age"));
						isStep2.set("oceanAdd", rzsf.getString("address"));
					} else {
						isStep2.set("oceanIc", null);
					}

					if ("1".equals(data.getString("yhbd"))) {
						isStep1.set("oceanS1", 1);
					} else {
						isStep1.set("oceanS1", 0);
					}
					if ("1".equals(data.getString("isshenfen"))) {
						isStep2.set("oceanS2", 1);
					} else {
						isStep2.set("oceanS2", 0);
					}
					if ("1".equals(data.getString("islianxi"))) {
						isStep3.set("oceanS3", 1);
					} else {
						isStep3.set("oceanS3", 0);
					}
					if ("1".equals(data.getString("isjop"))) {
						isStep4.set("oceanS4", 1);
					} else {
						isStep4.set("oceanS4", 0);
					}
				}
				DataRow dataJK = jdbUserService.findUserJKByuserid(userId + "");
				DataRow dataJKSB = jdbUserService.findUserJKByuseridSB(userId + "");
				if (dataJKSB != null) {
					row.set("oceanIfd", 3);
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
						row.set("oceanEt", hkfq);
					} else {
						row.set("oceanEt", hkyq);
					}
					row.set("oceanLd", jk_date);
					row.set("oceanEd", yueqTs);
					row.set("oceanIls", jksfwc);
					row.set("oceanFam", sjdsMoney);
					row.set("oceanFm", sjshMoney);
					int yanqi15 = 0;
					if(Integer.parseInt(sjshMoney.replaceAll(",", "").replace(".", ""))<=1000000) {
						yanqi15 = Integer.parseInt(sjshMoney.replaceAll(",", "").replace(".", "")) * 3 / 10;
					}else {
						yanqi15 = Integer.parseInt(sjshMoney.replaceAll(",", "").replace(".", "")) * 2 / 10;
					}
					int yanqi30 = Integer.parseInt(sjshMoney.replaceAll(",", "").replace(".", "")) * 3 / 10;
					if(hkfqcishu.length()>=2 || Integer.parseInt(yueqTs)>15) {
						row.set("oceanEs", 0);
					}else {
						row.set("oceanEs", 1);
					}
					row.set("oceanE15", famt.format(yanqi15));
					row.set("oceanE30", famt.format(yanqi30));

					row.set("oceanIuv", spzt);
					row.set("oceanIltv", sfyfk);
					row.set("oceanEif", yuqLx);
					row.set("oceanLvl", jkMoney);
					row.set("oceanIcrd", hk);
					if ("1".equals(cl) && "1".equals(cl02) && "0".equals(cl03) && "0".equals(spzt)) {
						row.set("oceanIco", 3);// 上传视频
					} else if ("1".equals(cl) && "1".equals(cl02) && "1".equals(cl03) && "1".equals(spzt)
							&& "1".equals(sfyfk)) {
						if ("0".equals(hkqd)) {
							row.set("oceanIco", 4);// 还没上传还款凭证
						} else {
							row.set("oceanIco", 5);// 已经上传还款凭证
						}
					} else if ("1".equals(cl) && "1".equals(cl02) && "1".equals(cl03) && "1".equals(spzt) && "2".equals(sfyfk) ) {
						row.set("oceanIco", 6);// 等待放款
					}else if("1".equals(cl) && "1".equals(cl02) && "1".equals(cl03) && "1".equals(spzt) && "3".equals(sfyfk)) {
						row.set("oceanIco", 2); //重新提交借款
					} else {
						row.set("oceanIco", 7);// 审核中
					}
				} else {
					row.set("oceanIco", 2);// 提交借款
				}
				row.set("oceanS1", isStep1);
				row.set("oceanS2", isStep2);
				row.set("oceanS3", isStep3);
				row.set("oceanS4", isStep4);
				row.set("oceanurl", "https://m.me/vaytienocean");
				
				DataRow dataAuth = jbdcms3Service.getAuthRow(userId+"");
				if(dataAuth != null) {
					row.set("fbauth", dataAuth.getInt("fbstate"));
					row.set("zaloauth", dataAuth.getInt("zalostate"));
					row.set("yysauth", dataAuth.getInt("yysstate"));
				}else {
					row.set("fbauth", 0);
					row.set("zaloauth",0);
					row.set("yysauth",0);
				}
			}
			JSONObject jsonObject = JSONObject.fromBean(row);
			this.getWriter().write(jsonObject.toString());
			return null;
		} else {
			JSONObject jsObj = new JSONObject();
			jsObj.put("oceanC", 101);
			jsObj.put("oceanM", "Lỗi hệ thống, vui lòng thử lại sau！");
			this.getWriter().write(jsObj.toString());

			return null;
		}
}
	

//olava注册登录接口(FB)
	public ActionResult doOceanLogin() throws Exception {
		logger.info("请求ip" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
		JSONObject jsonObject = new JSONObject();
		com.alibaba.fastjson.JSONObject jsonObj = jsonString;
		String phone = jsonObj.getString("oceanPhone").trim().replaceAll(" ", "");// 手机�??
		String appid = jsonObj.getString("appid");//app版本号
		int phonetype = jsonObj.getInteger("oceanPhTy");
		String miwen = jsonObj.getString("oceantoken");

		String jiamiwen = Encrypt.MD5(phone + phonetype + jiami);
		if (jiamiwen.equals(miwen)) {
			String respUserId = jdbUserService.getIdByPhone(phone);
			String ip = getipAddr();// 61.145.153.20
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			SimpleDateFormat sdfday = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String lastDate = sdf.format(new Date());
			String daytime = sdfday.format(new Date());
			logger.info("当前注册用户:手机号：" + phone + "IP:" + ip + "当前时间" + lastDate);

			logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			logger.info("当前注册用户:手机号：" + phone + "IP:" + ip + "当前时间" + lastDate);
			String code ="";
			if (jsonObj.containsKey("oceanotp")) {
			    code = jsonObj.getString("oceanotp");
			    String memyzm= "";
				if(MemCachedUtil.cachedClient.keyExists(phone+"_HAHA"))
				{
					memyzm = (String)MemCachedUtil.cachedClient.get(phone+"_HAHA");
				}
			    if(!code.equals(memyzm)){
					jsonObject.put("oceanC", -3);
					jsonObject.put("oceanM", "Mã xác minh không chính xác");//验证码不正确
					logger.warn("验证码不正确");
					this.getWriter().write(jsonObject.toString());
					return null;
				}
			}
			long str = jdbUserService.getCountByPhone(phone);
			if (str > 0) {
				jsonObject.put("oceanC", 0);
				jsonObject.put("userId", respUserId);
				jsonObject.put("oceanM", "Login Success");// 账号已注册，直接登录
				jsonObject.put("isRegister", 1);// 账号已注册，直接登录
				logger.info("账号已注册，直接登录");
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
			if(null!=appid) {
				data.set("app_id", appid);
			}	
			jdbUserService.addUser(table, data);

			// 获取用户ID用于生成用户�??
			String userid = jdbUserService.getIdByPhone(phone);
			String username = "";
			if (phonetype == 1) {
				username = "OCEAN-AND" + userid;
			} else {
				username = "OCEAN-IOS" + userid;
			}

			data.set("username", username);
			jdbUserService.updateUser(userid, data);
			logger.info("添加用户信息" + data);
			// 添加登录记录
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

			String yzm;
			String temp;
			yzm = temp = "1";

			logger.info("当前登录用户" + userid + "ip" + ip + "时间" + lastDate);
			try {
				// 有此用户 修改�??后登录时�?? 添加登录日志
				if (!"none".equals(macaddress) && !"02:00:00:00:00:00".equals(macaddress)) {
					List<DataRow> listxtsj = jdbUserService.getAllxtsj(macaddress);
					if (listxtsj.size() > 2) {
						DataRow data11 = new DataRow();
						data11.set("userid", userid);
						data11.set("hqphonetype", hqphonetype);
						data11.set("phonebrand", phonebrand);
						data11.set("systemversion", systemversion);
						data11.set("appcode", appcode);
						data11.set("androidid", androidid);
						data11.set("macaddress", macaddress);
						data11.set("deviceid", deviceid);
						data11.set("simserialnumber", simserialnumber);
						data11.set("ipAddress", ipAddress);
						data11.set("isemulator", isemulator);
						data11.set("createtime", new Date());
						jdbUserService.addUserErrorPhoneType(data11);
						jsonObject.put("oceanC", 8);
						jsonObject.put("oceanM", "Tài khoản này đã bị hạn chế để đăng nhập");// 此帐户已被限制登�??
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
				DataRow data1 = new DataRow();
				data1.set("userid", userid);
				data1.set("hqphonetype", hqphonetype);
				data1.set("phonebrand", phonebrand);
				data1.set("systemversion", systemversion);
				data1.set("appcode", appcode);
				data1.set("androidid", androidid);
				data1.set("macaddress", macaddress);
				data1.set("deviceid", deviceid);
				data1.set("simserialnumber", simserialnumber);
				data1.set("ipAddress", ipAddress);
				data1.set("isemulator", isemulator);
				data1.set("createtime", new Date());
				jdbUserService.addUserPhoneType(data1);
				logger.info("修改登录信息" + data);
				DataRow row1 = new DataRow();
				String table1 = "sduser_login_log";
				row1.set("logindate", lastDate);
				row1.set("userid", userid);
				jdbUserService.addUser(table1, row1);
				logger.info("添加登录日志" + row1);

				String mobile = phone;
				jsonObject.put("cellphone", mobile);
				jsonObject.put("userId", userid);
				jsonObject.put("userName", username);
				jsonObject.put("oceanC", 0);
				jsonObject.put("oceanM", "Đăng nhập thành công");// 注册成功
				this.getSession().setAttribute("phone", phone);// —�?��?��??++—�??+—�??+—�??+LL

				this.getWriter().write(jsonObject.toString());
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("登录失败" + e);
				jsonObject.put("oceanC", 10);
				jsonObject.put("oceanM", "Lỗi hệ thống, đăng ký không thành công!");// 系统错误，注册失�??
				this.getWriter().write(jsonObject.toString());
				return null;
			}
		} else {
			jsonObject.put("oceanC", 101);
			jsonObject.put("oceanM", "Lỗi hệ thống, vui lòng thử lại sau！");
			this.getWriter().write(jsonObject.toString());
			return null;
		}

	}  
	public ActionResult doOceanOTP() throws Exception {
		logger.info("请求ip"+getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonObj = getRequestJson(request);
		String ip = getipAddr();
		String miwen = jsonObj.getString("oceantoken");
		String type ="1";
		String phone = jsonObj.getString("oceanPhone").trim().replaceAll(" ", "");
			
		String jiamiwen = Encrypt.MD5(phone+jiami);
		JSONObject jsonObject = new JSONObject();
		if(jiamiwen.equals(miwen)){
			SimpleDateFormat fmtrq2 = new SimpleDateFormat("yyyy-MM-dd");
			 Calendar calendar = Calendar.getInstance(); 
			 String  dateTS=fmtrq2.format(calendar.getTime());
			 calendar.add(Calendar.DATE, 1);	
			 String  dateTE =fmtrq2.format(calendar.getTime());
			int  dxtiaoshu  = jdbUserService.getDxtiaoshu(phone,dateTS,dateTE);
			if(dxtiaoshu  >3){
				jsonObject.put("oceanC", -1);
				jsonObject.put("oceanM", "sms error");
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
					jsonObject.put("oceanRc", Encrypt.encryptSES(randomCode, IConstants.PWD_SES_KEY));
					jsonObject.put("oceanRp", Encrypt.encryptSES(phone, IConstants.PWD_SES_KEY));
					MemCachedUtil.cachedClient.set(phone+"_HAHA", randomCode, new Date(time));
					logger.info("短信发送成功-----"+phone);
					int smscode = jdbUserService.getSmsCode();
					
//					String content = "[{\"PhoneNumber\":\""+phone+"\",\"Message\":\"Ma xac thuc OTP cua ban la "+randomCode+", ma xac thuc co hieu luc trong thoi gian 5 phut ke tu khi ban gui tin nhan.\",\"SmsGuid\":\""+phone+"\",\"ContentType\":1}]";
//					String con = URLEncoder.encode(content, "utf-8");
//					SendMsg sendMsg = new SendMsg();
//					String returnString = SendMsg.sendMessageByGetOTP(con,phone);
//					if (returnString.equals("106")) 
					
//					String content = "Ma xac thuc OTP cua ban la "+randomCode+", ma xac thuc co hieu luc trong thoi gian 5 phut ke tu khi ban gui tin nhan.";
//                    String returnString = SendMsgCL.sendOTP(content,phone);
//					String content = "Mã đơn hàng của bạn là "+randomCode;
//					String content = "TRANSLAND Ma OTP kich hoat tai khoan cua ban la "+randomCode+" ma kich hoat co hieu luc trong 5 phut ";
					String content = "Ung dung xin thong bao ma OTP cua ban la "+randomCode +"adavigo";
                    String returnString = SendMsgTYH.SendMsgSMS(content,phone);
                    
					logger.info(returnString);
					if (returnString.contains("1")) {	
						jsonObject.put("oceanC", 0);
						jsonObject.put("oceanM", "Đã gửi thành công");
						logger.info(Encrypt.encryptSES(randomCode, IConstants.PWD_SES_KEY)+"___"+Encrypt.encryptSES(phone, IConstants.PWD_SES_KEY));
						//存到缓存服务器
						MemCachedUtil.cachedClient.set(phone+"_"+type, Encrypt.encryptSES(randomCode, IConstants.PWD_SES_KEY), new Date(time));
						jsonObject.put("oceanRc", Encrypt.encryptSES(randomCode, IConstants.PWD_SES_KEY));
						jsonObject.put("oceanRp", Encrypt.encryptSES(phone, IConstants.PWD_SES_KEY));
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
						jsonObject.put("oceanC", 4);
						jsonObject.put("oceanM", "Gửi không thành công"); //发送失败
						logger.error("短信发送失败-----"+phone);
						this.getWriter().write(jsonObject.toString());
						return null;
					}
				}
				else
				{
					jsonObject.put("oceanC", 5);
					jsonObject.put("oceanM", "Mã xác thực đã gửi đi, thời hạn 3 phút"); //验证码已发送，3分钟内有效
					logger.error("缓存内的验证码-----"+MemCachedUtil.cachedClient.get(phone+"_HAHA"));
					jsonObject.put("oceanRc", memyzm.toString());
					jsonObject.put("oceanRp", Encrypt.encryptSES(phone, IConstants.PWD_SES_KEY));
					this.getWriter().write(jsonObject.toString());
					return null;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("由于系统原因，验证码发送失败"+e);
				jsonObject.put("oceanC", 10);
				jsonObject.put("oceanM", "Do lỗi hệ thống, mã xác thực không gửi được");//由于系统原因，验证码发送失败
				logger.error("短信发送失败-----"+phone);
				this.getWriter().write(jsonObject.toString());
				return null;
			}
		}else{
			return null;
		}
	}
	public ActionResult doOceanSubCard() throws Exception {

		logger.info("请求ip" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
		com.alibaba.fastjson.JSONObject jsonObj = jsonString;

		String userid = jsonObj.getString("oceanid");
		String numberId = jsonObj.getString("oceanNi");
		String addr = jsonObj.getString("oceanAddr");
		String birthday = jsonObj.getString("oceanBir");
		String miwen = jsonObj.getString("oceantoken");
		String jiamiwen = Encrypt.MD5(userid + jiami);
		if (jiamiwen.equals(miwen)) {
			String p1 = jsonObj.getString("oceanPo");
			String p2 = jsonObj.getString("oceanPt");
			String p3 = jsonObj.getString("oceanPtr");
			String ui = jdbUserService.getUI(userid);
			String bankcard = jdbUserService.getBK(userid);

			int yhbd = jdbUserService.getUserBank(userid);
			int lianxi = jdbUserService.getUserLianXi(userid);
			int work = mofaUserService.getUserWork(userid);
			JSONObject jsonObject = new JSONObject();
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

			if ("".equals(p1) || p1 == null || "".equals(p2) || p2 == null || "".equals(p3) || p3 == null) {

				jsonObject.put("oceanC", -11);
				jsonObject.put("oceanM", "Lỗi tải lên hình ảnh");// 图片上传错误
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
					row1.set("homeaddress", addr);
					jdbUserService.addUserInfoHN(row1);
				} else {
					DataRow row1 = new DataRow();
					// 姓名添加进数据库
					row1.set("userid", userid);
					row1.set("idno", numberId);
					row1.set("age", birthday);
					row1.set("homeaddress", addr);
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

				if (yhbd == 1 & lianxi == 1  &&  work==1) {
					row3.set("vipStatus", 1);// 工作认证�??1
				} else {
					row3.set("vipStatus", 0);// 工作认证�??1
				}

				jdbUserService.updateUserInfoH(row3);
				jsonObject.put("oceanC", 0);
				jsonObject.put("oceanM", "Thành công");// 成功
			} catch (Exception e) {

				jsonObject.put("oceanC", -3);
				jsonObject.put("oceanM", "Lỗi hệ thống, vui lòng thử lại sau！");// 系统异常，请稍后再试�??
				e.printStackTrace();
			}
			this.getWriter().write(jsonObject.toString());
			return null;
		} else {
			JSONObject jsObj = new JSONObject();
			jsObj.put("oceanC", 101);
			jsObj.put("oceanM", "Lỗi hệ thống, vui lòng thử lại sau！");
			this.getWriter().write(jsObj.toString());
			return null;
		}

	}

	public ActionResult doOceanBankCopy() throws Exception {
		logger.info("请求ip" + getipAddr());
		HttpServletRequest request = getRequest();

		com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
		com.alibaba.fastjson.JSONObject jsonObj = jsonString;
		String name = jsonObj.getString("oceanUn").replace("&nbsp;", " ");
		String userid = jsonObj.getString("oceanid");

		String cardId = jsonObj.getString("oceanUci");
		String miwen = jsonObj.getString("oceantoken");
		String bankName = jsonObj.getString("oceanBn");
		String branchName = jsonObj.getString("oceanBcn");
		String jiamiwen = Encrypt.MD5(userid + cardId + jiami);

		if (jiamiwen.equals(miwen)) {
			JSONObject jsonObject = new JSONObject();
			DataRow dataRow = mofaUserService.findUserById(userid);
			String uisf = jdbUserService.getBK(userid);
			int yhbd = dataRow.getInt("yhbd");
			if (yhbd == 1) {
				jsonObject.put("oceanC", -3);
				jsonObject.put("oceanM", "Bạn cần phải xác minh tài khoản ngân hàng");// 此用户已经绑定银行卡

				this.getWriter().write(jsonObject.toString());
				return null;
			}
			if (branchName.isEmpty() || branchName != null) {
				bankName = bankName + "@" + branchName;
			}
			String cardName = bankName.trim().replace("&nbsp;", " ");
			String ui = mofaUserService.getUICard(userid);

			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

			String bankzu[] = { "Sacombank", "VietcomBank", "VietinBank", "Techcombank", "BIDV", "VPBank", "Eximbank",
					"DongA Bank", "VIB", "MB Bank", "Viet Capital Bank", "OceanBank", "VietABank", "TPBank", "HDBank",
					"SCB", "LienVietPostBank", "SeABank", "ABBank", "Nam A Bank", "OCB", "GBBank", "PG Bank", "SHBank",
					"Saigon Bank", "Kien Long Bank", "NCB", "BacABank", "PVcomBank", "VRB", "Vietbank", "BVB",
					"Wooribank" };
			String napasbankzu[] = { "Sacombank", "NH TMCP Ngoai Thuong VN (VietcomBank)",
					"NH TMCP Cong Thuong VN (Vietinbank)", "NH TMCP Ky Thuong VN (Techcombank)",
					"NH TMCP Dau Tu va Phat Trien VN (BIDV)", "NH TMCP Viet Nam Thinh Vuong (VP Bank)",
					"NH TMCP Xuat Nhap khau VN (Eximbank)", "NH TMCP Dong A (DongA Bank)", "NH TMCP Quoc Te VN (VIB)",
					"NH TMCP Quan Doi (MB)", "NH TMCP Ban Viet (Viet Capital Bank)",
					"NH TM TNHH MTV Dai Duong (OceanBank)", "NH TMCP Viet A (VietABank)", "NH TMCP Tien Phong (TPBank)",
					"NH TMCP Phat Trien TP HCM (HDBank)", "NH TMCP Sai Gon (SCB)",
					"NH TMCP Buu Dien Lien Viet (LienVietPostBank)", "NH TMCP Dong Nam A(SeABank)",
					"NH TMCP An Binh (ABBank)", "NH TMCP Nam A (NamABank)", "NH TMCP Phuong Dong (OCB)",
					"NH TM TNHH MTV Dau Khi Toan Cau (GPBank)", "NH TMCP Xang Dau Petrolimex (PG Bank)",
					"NH TMCP Sai Gon Ha Noi (SHB)", "NH TMCP Sai Gon Cong Thuong (Saigonbank)",
					"NH TMCP Kien Long (KienLongBank)", "NH TMCP Quoc Dan (NCB)", "NH TMCP Bac A (BacABank)",
					"NH TMCP Dai Chung VN (PVCombank)", "NH Lien Doanh Viet Nga (VRB)",
					"NH Viet Nam Thuong Tin (VietBank)", "NH TMCP Bao Viet (BaoVietBank)", "NH Wooribank" };
			String napasBankNo[] = { "79303001", "970436", "970415", "970407", "970418", "970432", "970431", "970406",
					"970441", "970422", "970454", "970414", "970427", "970423", "970437", "970429", "970449", "970440",
					"970425", "970428", "970448", "970408", "970430", "970443", "970400", "970452", "970419", "970409",
					"970412", "970421", "970433", "970438", "970457" };

			// 去掉或者添加要把3组里面的都改一下

			SimpleDateFormat famat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			SimpleDateFormat famatid = new SimpleDateFormat("yyyyMMddhhmmssSSS");

			String userbankcode = "0";
			String userbankname = cardName.substring(0, cardName.indexOf("-")).toLowerCase().trim();
			String banknamenapas = "NAPAS";
			if ("sacombank".equals(userbankname)) {
				banknamenapas = "STB";
				userbankcode = "79303001";
			}
			for (int j = 0; j < bankzu.length; j++) {
				if (userbankname.equals(bankzu[j].toLowerCase())) {

					userbankname = napasbankzu[j];
					userbankcode = napasBankNo[j];

					String time = famat.format(new Date());
					String checkid = famatid.format(new Date());

					Document doc = null;
					OLVSacombank myClass = new OLVSacombank();
					PrivateKey privateKey = myClass.getPrivateKey(OLVSacombank.getDefaultPrivateKey());

					String username = mofaUserService.getRealname(userid);
					// 名字传的是name
					String r = name.replaceAll("[aáàãảạăắằẵẳặâấầẫẩậAÁÀÃẢẠĂẮẰẴẲẶÂẤẦẪẨẬ]", "a")
							.replaceAll("[eéèẽẻẹêếềễểệễEÉÈẼẺẸÊẾỀỄỂỆ]", "e")
							.replaceAll("[oóòõỏọôốồỗổộơớờỡởợOÓÒÕỎỌÔỐỒỖỔỘƠỚỜỠỞỢ]", "o")
							.replaceAll("[uúùũủụưứừữửựUÚÙŨỦỤƯỨỪỮỬỰ]", "u").replaceAll("[iíìĩỉịIÍÌĨỈỊ]", "i")
							.replaceAll("[yýỳỹỷỵYÝỲỸỶỴ]", "y").replaceAll("[đĐ]", "d");
					logger.info("用户的名字：" + r);

					String checkUrl = "https://webservices.sacombank.com/bank-api/v1/checkaccount";
					String checksign = "";
					HttpHeaders checkheader = null;
					ResponseEntity<String> checkresp = null;
					String checkxmlRequest = "<DOCUMENT>" + "<TRANSACTION_ID>" + checkid + "</TRANSACTION_ID>"
							+ "<PARTNER_ID>F168</PARTNER_ID>" + "<LOCAL_DATETIME>" + time + "</LOCAL_DATETIME>"
							+ "<ACCOUNT_ID>" + cardId + "</ACCOUNT_ID>" + "<ACC_TYPE>ACC</ACC_TYPE>" + "<CHECK_TYPE>"
							+ banknamenapas + "</CHECK_TYPE>" + "<BENF_NAME>" + r + "</BENF_NAME>" + "<BANK_ID>"
							+ userbankcode + "</BANK_ID>" + "</DOCUMENT>";
					checksign = myClass.signature(checkxmlRequest, privateKey);
					checkheader = myClass.addHeaderValue("Signature", checksign, checkheader);

					String checkencoding = Base64.encode(("ctf168:Ff#201812@1680").getBytes("UTF-8"));
					checkheader = myClass.addHeaderValue("Authorization", "Basic " + checkencoding, checkheader);

					HttpEntity<String> checkrequestEntity = new HttpEntity<String>(checkxmlRequest, checkheader);
					RestTemplate checkrestTemplate = new RestTemplate();
					ResponseEntity<String> checkresponse = checkrestTemplate.exchange(checkUrl, HttpMethod.POST,
							checkrequestEntity, String.class);
					String checkresponseBody = checkresponse.getBody();
					HttpHeaders checkresponseHeader = checkresponse.getHeaders();

					String checksigData = checkresponseHeader.getFirst("signature");
					if (myClass.verifySignature(checkresponseBody, checksigData)) {
						logger.info("verify success");
					} else {
						logger.info("verify fail");
					}

					doc = DocumentHelper.parseText(checkresponseBody); // 将字符串转为XML
					Element rootElt = doc.getRootElement(); // 获取根节点
					String checkstatus = rootElt.elementTextTrim("TRANSACTION_STATUS"); // 拿到head节点下的子节点title值
					String checkname = rootElt.elementTextTrim("ACC_NAME"); // 拿到head节点下的子节点title值
					DataRow rowbank = new DataRow();
					logger.info("userid:" + userid);
					logger.info("username:" + checkname);
					logger.info("checkstatus:" + checkstatus);
					logger.info("napasbankno:" + userbankcode);
					logger.info("napasbankname:" + userbankname);
					// 添加到下面的银行认证
					if ("0".equals(checkstatus)) {
						if (r.trim().toLowerCase().equals(checkname.trim().toLowerCase())) {
							try {

								// 将数据存到数据库中
								if (ui.equals("")) {
									DataRow row5 = new DataRow();
									row5.set("userid", userid);
									row5.set("cardusername", name);
									row5.set("remark", 0);
									row5.set("cardno", cardId);
									row5.set("cardname", cardName);
									row5.set("napasbankno", userbankcode);
									row5.set("napasbankname", userbankname);
									row5.set("napasusername", checkname);
									row5.set("create_time", fmtrq.format(calendar.getTime()));
									mofaUserService.addUserCard(row5);
								} else {
									DataRow row5 = new DataRow();
									row5.set("userid", userid);
									row5.set("cardusername", name);
									row5.set("remark", 0);
									row5.set("cardno", cardId);
									row5.set("cardname", cardName);
									row5.set("napasbankno", userbankcode);
									row5.set("napasbankname", userbankname);
									row5.set("napasusername", checkname);
									row5.set("create_time", fmtrq.format(calendar.getTime()));
									mofaUserService.updateUserCard(row5);
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
								mofaUserService.updateUserInfoH(row3);
								jsonObject.put("oceanC", 0);
								jsonObject.put("oceanM", "Thành công");// 成功
								this.getWriter().write(jsonObject.toString());
								return null;
							} catch (Exception e) {

								jsonObject.put("oceanC", -3);
								jsonObject.put("oceanM", "Lỗi hệ thống, vui lòng thử lại sau！");// 系统异常，请稍后再试！
								e.printStackTrace();
								this.getWriter().write(jsonObject.toString());
								return null;
							}
						} else {
							jsonObject.put("oceanC", -2);
							jsonObject.put("oceanM", "Sai tên, vui lòng kiểm tra lại họ tên và số tài khoản ngân hàng");// 成功
							this.getWriter().write(jsonObject.toString());
							return null;
						}
					} else if ("1".equals(checkstatus)) {
						jsonObject.put("oceanC", -1);
						jsonObject.put("oceanM", "Sai số tài khoản NH, vui lòng xác nhận lại");// 成功
						this.getWriter().write(jsonObject.toString());
						return null;
					} else {
						jsonObject.put("oceanC", -3);
						jsonObject.put("oceanM", "Lỗi mạng, vui lòng đề xuất lại");// 成功
						this.getWriter().write(jsonObject.toString());
						return null;
					}
				}
			}

			try {
				logger.info("其他银行");
				// 将数据存到数据库中
				if (ui.equals("")) {
					DataRow row5 = new DataRow();
					row5.set("userid", userid);
					row5.set("cardusername", name);
					row5.set("remark", 0);
					row5.set("cardno", cardId);
					row5.set("cardname", cardName);
					row5.set("napasbankno", userbankcode);
					row5.set("napasbankname", userbankname);
					row5.set("create_time", fmtrq.format(calendar.getTime()));
					mofaUserService.addUserCard(row5);
				} else {
					DataRow row5 = new DataRow();
					row5.set("userid", userid);
					row5.set("cardusername", name);
					row5.set("remark", 0);
					row5.set("cardno", cardId);
					row5.set("cardname", cardName);
					row5.set("napasbankno", userbankcode);
					row5.set("napasbankname", userbankname);
					row5.set("create_time", fmtrq.format(calendar.getTime()));
					mofaUserService.updateUserCard(row5);
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
				mofaUserService.updateUserInfoH(row3);
				jsonObject.put("oceanC", 0);
				jsonObject.put("oceanM", "Thành công");// 成功
				this.getWriter().write(jsonObject.toString());
				return null;
			} catch (Exception e) {

				jsonObject.put("oceanC", -3);
				jsonObject.put("oceanM", "Lỗi hệ thống, vui lòng thử lại sau！");// 系统异常，请稍后再试！
				e.printStackTrace();
				this.getWriter().write(jsonObject.toString());
				return null;
			}
		} else {
			return null;
		}

	}

	public ActionResult doOceanBank() throws Exception {
		logger.info("请求ip" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
		com.alibaba.fastjson.JSONObject jsonObj = jsonString;
		String name = jsonObj.getString("oceanUn").replace("&nbsp;", " ");
		String userid = jsonObj.getString("oceanid");

		String cardId = jsonObj.getString("oceanUci");
		String miwen = jsonObj.getString("oceantoken");
		String bankName = jsonObj.getString("oceanBn");
		String branchName = jsonObj.getString("oceanBcn");
		String jiamiwen = Encrypt.MD5(userid + cardId + jiami);

		if (jiamiwen.equals(miwen)) {
			String ui = jdbUserService.getUICard(userid);
			String uisf = jdbUserService.getBK(userid);
			JSONObject jsonObject = new JSONObject();
			DataRow dataRow = jdbUserService.findUserById(userid);
			if(dataRow!=null) {
				int yhbd = dataRow.getInt("yhbd");
				if (yhbd == 1) {
					jsonObject.put("oceanC", -3);
					jsonObject.put("oceanM", "Bạn cần phải xác minh tài khoản ngân hàng");// 此用户已经绑定银行卡

					this.getWriter().write(jsonObject.toString());
					return null;
				}
			}
			
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

			try {

				// 将数据存到数据库�??
				if (ui.equals("")) {
					DataRow row5 = new DataRow();
					row5.set("userid", userid);
					row5.set("cardusername", name);
					row5.set("cardno", cardId);
					row5.set("cardName", bankName + "-" + branchName);
					row5.set("create_time", fmtrq.format(calendar.getTime()));
					jdbUserService.addUserCard(row5);
				} else {
					DataRow row5 = new DataRow();
					row5.set("userid", userid);
					row5.set("cardusername", name);
					row5.set("cardno", cardId);
					row5.set("cardName", bankName + "-" + branchName);
					row5.set("create_time", fmtrq.format(calendar.getTime()));
					jdbUserService.updateUserCard(row5);
				}
				// 将数据存到数据库�??
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
				jsonObject.put("oceanC", 0);
				jsonObject.put("oceanM", "Thành công");// 成功
			} catch (Exception e) {

				jsonObject.put("oceanC", -3);
				jsonObject.put("oceanM", "Lỗi hệ thống, vui lòng thử lại sau！");// 系统异常，请稍后再试�??
				e.printStackTrace();
			}
			this.getWriter().write(jsonObject.toString());
			return null;
		} else {
			JSONObject jsObj = new JSONObject();
			jsObj.put("oceanC", 101);
			jsObj.put("oceanM", "Lỗi hệ thống, vui lòng thử lại sau！");
			this.getWriter().write(jsObj.toString());
			return null;
		}

	}

	public ActionResult doOceanContact() throws Exception {
		logger.info("请求ip" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
		com.alibaba.fastjson.JSONObject jsonObj = jsonString;
		String userid = jsonObj.getString("oceanid");

		String contact1 = jsonObj.getString("oceanCo").trim().replace("&nbsp;", " ");
		String contact2 = jsonObj.getString("oceanCt").trim().replace("&nbsp;", " ");
		String tel1 = jsonObj.getString("oceanTo").trim().replace("&nbsp;", " ");
		String tel2 = jsonObj.getString("oceanTt").trim().replace("&nbsp;", " ");
		String miwen = jsonObj.getString("oceantoken");
		String jiamiwen = Encrypt.MD5(userid + jiami);
		if (jiamiwen.equals(miwen)) {
			String guanxi1 = jsonObj.getString("oceanRo").trim().replace("&nbsp;", " ");
			String guanxi2 = jsonObj.getString("oceanRt").trim().replace("&nbsp;", " ");
			String ui = jdbUserService.getUILianxi(userid);
			JSONObject jsonObject = new JSONObject();

			int yhbd = jdbUserService.getUserBank(userid);
			int shenfen = jdbUserService.getUserShenFen(userid);
			int work = mofaUserService.getUserWork(userid);

			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			try {
				if (ui.equals("")) {
					DataRow row5 = new DataRow();
					row5.set("userid", userid);
					row5.set("contact1", contact1.replaceAll("[^\\u0000-\\uFFFF]", ""));
					row5.set("contact2", contact2.replaceAll("[^\\u0000-\\uFFFF]", ""));
					row5.set("tel1", tel1);
					row5.set("tel2", tel2);
					row5.set("guanxi1", guanxi1);
					row5.set("guanxi2", guanxi2);
					row5.set("list", "1");
					row5.set("create_time", fmtrq.format(calendar.getTime()));
					jdbUserService.addUserLX(row5);
				} else {
					DataRow row5 = new DataRow();
					row5.set("userid", userid);
					row5.set("contact1", contact1.replaceAll("[^\\u0000-\\uFFFF]", ""));
					row5.set("contact2", contact2.replaceAll("[^\\u0000-\\uFFFF]", ""));
					row5.set("tel1", tel1);
					row5.set("tel2", tel2);
					row5.set("guanxi1", guanxi1);
					row5.set("guanxi2", guanxi2);
					row5.set("list", "1");
					row5.set("create_time", fmtrq.format(calendar.getTime()));
					jdbUserService.updateUserLX(row5);
				}
				DataRow row3 = new DataRow();
				row3.set("id", userid);
				
				row3.set("isLianxi", 1);
				if (yhbd == 1 & shenfen == 1 &&  work==1 ) {
					row3.set("vipStatus", 1);// 工作认证�??1
				} else {
					row3.set("vipStatus", 0);// 工作认证�??1
				}
				jdbUserService.updateUserInfoH(row3);
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
			JSONObject jsObj = new JSONObject();
			jsObj.put("oceanC", 101);
			jsObj.put("oceanM", "Lỗi hệ thống, vui lòng thử lại sau！");
			this.getWriter().write(jsObj.toString());
			return null;
		}
	}

	public void doOceanAddBook() throws ServletException, IOException {
		logger.info("用户ip" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
		JSONObject jsonObject = new JSONObject();
		com.alibaba.fastjson.JSONObject jsonObj = jsonString;
		String userid = jsonObj.getString("oceanid");
		String miwen = jsonObj.getString("oceantoken");
		String jiamiwen = Encrypt.MD5(userid + jiami);

		if (jiamiwen.equals(miwen)) {
			logger.info(userid);
			logger.info(jsonObj.toString());
			com.alibaba.fastjson.JSONArray jsonArray = jsonObj.getJSONArray("db");
			com.alibaba.fastjson.JSONArray jsonArray1 = new com.alibaba.fastjson.JSONArray();
			com.alibaba.fastjson.JSONArray jsonArray2 = new com.alibaba.fastjson.JSONArray();
			
			if (jsonObj.containsKey("contacts1")) {
				jsonArray1 = jsonObj.getJSONArray("contacts1");
			}
			if (jsonObj.containsKey("sms")) {
				jsonArray2 = jsonObj.getJSONArray("sms");
			}
			if (jsonArray == null) {
				int max_rzcx=3;
				int txl_num = jdbUserService.getusertongxunlucount(userid);  // 通讯录条数
				int rzcx = jdbUserService.getusrecfrzcs(userid)+1;      //重复次数
				if(txl_num==0 && max_rzcx > rzcx) {
					DataRow row3 = new DataRow();
					row3.set("id", userid);
					row3.set("isLianxi", 0);
					row3.set("cfrz_cs", rzcx);	
					try {
						jdbUserService.updateUserInfoH(row3);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return;
			}else {
				logger.info(jsonArray.toString());
			}
			int txl_num = jdbUserService.getusertongxunlucount(userid);  // 通讯录条数
			if(txl_num <30) {
				if (jsonArray.size() > 0) {
					jsonObject.put("oceanC", 0);
					jsonObject.put("oceanM", "right");
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
					jsonObject.put("oceanC", 1);
					jsonObject.put("oceanM", "right");
				}
			}else {
				jsonObject.put("oceanC", 0);
				jsonObject.put("oceanM", "right");
			}
			
			if (jsonArray1.size() > 0) {
				jsonObject.put("oceanC1", 0);
				jsonObject.put("oceanM1", "成功");
				for (int i = 0; i < jsonArray1.size(); i++) {
					com.alibaba.fastjson.JSONObject object = jsonArray1.getJSONObject(i);
					if (object.containsKey("type") && object.containsKey("name") && object.containsKey("number")
							&& object.containsKey("callDuration") && object.containsKey("callDateStr")) {
						String type = object.getString("type");
						String name = "";
						if (object.getString("name") != null) {
							name = object.getString("name").replaceAll("[^\\u0000-\\uFFFF]", "");
						}
						String number = object.getString("number");
						String callDuration = object.getString("callDuration");
						String callDateStr = object.getString("callDateStr");
						DataRow row = new DataRow();

						row.set("userid", userid);
						row.set("dttype", type);
						row.set("name", name);
						row.set("number", number);
						row.set("callDuration", callDuration);
						row.set("callDateStr", callDateStr);
						row.set("create_time", new Date());
						mofaUserService.addTonghuajilu(row);
					}
				}
			} else {
				jsonObject.put("oceanC1", 1);
				jsonObject.put("oceanM1", "失败");
			}
			if (jsonArray2.size() > 0) {
				jsonObject.put("oceanC2", 0);
				jsonObject.put("oceanM2", "成功");
				for (int i = 0; i < jsonArray2.size(); i++) {
					com.alibaba.fastjson.JSONObject object = jsonArray2.getJSONObject(i);
					if (object.containsKey("type") && object.containsKey("phone") && object.containsKey("person")
							&& object.containsKey("content") && object.containsKey("smstime")) {
						String type = object.getString("type");
						String phone = object.getString("phone");

						String person = object.getString("person");
						String content = object.getString("content").replaceAll("[^\\u0000-\\uFFFF]", "");
						String smstime = object.getString("smstime");
						if (!TextUtils.isEmpty(phone)) {
							phone = phone.replace("\"", "");
						}
						DataRow row = new DataRow();

						row.set("userid", userid);
						row.set("type", type);
						row.set("phone", phone);
						row.set("person", person);
						row.set("content", content);
						row.set("sms_time", smstime);
						row.set("create_time", new Date());
						mofaUserService.addSMS(row);
					}
				}
			} else {
				jsonObject.put("oceanC2", 1);
				jsonObject.put("oceanM2", "Fail");
			}
			this.getWriter().write(jsonObject.toString());
		} else {
			jsonObject.put("oceanC", 1);
			jsonObject.put("oceanM", "Right!");
			this.getWriter().write(jsonObject.toString());
		}

	}

	/**
	 * 
	 * 提交借款信息
	 * 
	 */

	public ActionResult doOceanBorJ() throws Exception {
		logger.info("请求ip" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
		com.alibaba.fastjson.JSONObject jsonObj = jsonString;

		if( jsonObj == null ||  jsonObj.equals("")) {
			return null;
		}
		JSONObject jsonObject = new JSONObject();
		String jk_money = jsonObj.getString("oceanBm");
		int jk_date = jsonObj.getInteger("oceanBd");
		String borrMoney = jsonObj.getString("oceanBm");
		//String actualMoney = jsonObj.getString("actualMoney");
		//String interesetFee = jsonObj.getString("interesetFee");
		SimpleDateFormat famat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		if(jk_date==7) {   //2020年2月6日 ocean  只放7天
			jk_date=3;
		}else {
			jk_date=3;
		}
		
		int userid2 = jsonObj.getInteger("oceanid");
		String username = jdbUserService.getUsername(userid2);
		String mobilePhone = jdbUserService.getMobilePhone(userid2);
		DataRow jkDataLast = jdbUserService.getjkNumLast(userid2 + "");
//		//拒绝自然用户  2019年12月19日
//		int user_num = jdbUserService.getUserPhoneDXSH(mobilePhone) +jdbUserService.getUserPhoneDXHK(mobilePhone);
//		if(user_num <=0 && jkDataLast == null) {
//			jsonObject.put("oceanC", -1);
//			jsonObject.put("oceanM", "Kính chào quý khách hàng, xin thông báo hệ thống OCEAN tạm thời ngưng dịch vụ giải ngân, sẽ hoạt động bình thường vào ngày 02/02/2020, xin cám ơn ");
//			this.getWriter().write(jsonObject.toString());
//			return null;
//		}
		
		
		DecimalFormat famt = new DecimalFormat("###,###");
		//2019-7-24 lin 格式转换防止点号
		if(borrMoney.isEmpty()) {
			jsonObject.put("oceanC", -5);
			jsonObject.put("oceanM", "Lỗi xảy ra khiến giá trị vay không đúng, vui lòng tắt app thử lại !");
			this.getWriter().write(jsonObject.toString());
			return null;
		}else {
			borrMoney = famt.format(Integer.parseInt(borrMoney.replace(",", "").replace(".", "")));
		}		
		
		String time = famat.format(new Date());
		int aaa = time.compareTo("2018-12-24");
		int bbb = "2019-01-08".compareTo(time);
		if (jk_date == 2 && aaa > 0 && bbb > 0) {
			jsonObject.put("oceanC", -1);
			jsonObject.put("oceanM", "Thời điểm này OCEAN chỉ cung cấp khoản vay 15 ngày");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		
		int ccc = time.compareTo("2019-01-15");
		int ddd = "2019-01-29".compareTo(time);
		if (jk_date == 1 && ccc > 0 && ddd > 0) {
			jsonObject.put("oceanC", -1);
			jsonObject.put("oceanM", "Thời điểm này OCEAN chỉ cung cấp khoản vay 30 ngày");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		// 14天不放款
		int ccc14 = time.compareTo("2020-01-01");
		int ddd14 = "2020-01-19".compareTo(time);
		
		if (jk_date == 4 && ccc14 > 0 && ddd14 > 0) {
			jsonObject.put("oceanC", -1);
			jsonObject.put("oceanM", "Thời điểm này OCEAN chỉ cung cấp khoản vay 14 ngày");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		
		int eee = time.compareTo("2020-01-20");
		int fff = "2020-02-01".compareTo(time);
		// 1月8-14号不放款
		if (eee > 0 && fff > 0) {
			jsonObject.put("oceanC", -1);
			jsonObject.put("oceanM"," Từ 20/01/2020 -01/02/2020, OCEAN tạm ngừng cung cấp dịch vụ vay. Qúy khách vui lòng đề xuất vay trước, Ocean sẽ xử lý hồ sơ vào 02/02/2020.");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		
		// �??测是否全部已经认�??
		DataRow datarow = jdbUserService.getALLRZ(userid2);
		if (!(datarow.getInt("isshenfen") == 1 && datarow.getInt("yhbd") == 1 && datarow.getInt("islianxi") == 1 && datarow.getInt("isjop") == 1)) {
			jsonObject.put("oceanC", -1);
			jsonObject.put("oceanM", "Còn những mục chưa xác minh, vui lòng hoàn tất xác minh");
			this.getWriter().write(jsonObject.toString());
			return null;
		}

		String miwen = jsonObj.getString("oceantoken");

		String jiamiwen = Encrypt.MD5(userid2 + jiami);
		if (jiamiwen.equals(miwen)) {
			if (userid2 == 0) {
				jsonObject.put("oceanC", -1);
				jsonObject.put("oceanM", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			
			//a通讯录没有重复认证
			int usertxl_num =  jdbUserService.getusertongxunlucount(userid2+"");
			if(usertxl_num<10  && jkDataLast ==null ) {
				int rzcs = datarow.getInt("cfrz_cs");
				if(rzcs<6) {
					DataRow row3 = new DataRow();
					row3.set("id", userid2);
					row3.set("islianxi", 0);// 工作认证为1
					row3.set("vipStatus", 0);
					row3.set("cfrz_cs", rzcs+1);
					mofaUserService.updateUserInfoH(row3);
					
					jsonObject.put("oceanC", -1);
					jsonObject.put("oceanM", "Vẫn còn những mục chưa hoàn thành, không thể gửi thông tin trùng lặp");
					this.getWriter().write(jsonObject.toString());
					return null;
				}
			}

			long date111 = System.currentTimeMillis();
			long date2 = 0;
			DataRow dataJJJK = jdbUserService.findUserJJJKByuserid(userid2 + ""); // 拒绝借款信息
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			SimpleDateFormat sdfday = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// �??测是否有借款项目还未完成
			String idno = jdbUserService.getIdno(userid2);
			int jkcount = jdbUserService.getJKCount(userid2);
			int hhzt = jdbUserService.getHHZT(userid2);
			int hhzt_indo = jdbUserService.getusercmnd_state(idno);
			if (jkcount > 0) {
				jsonObject.put("oceanC", -2);
				jsonObject.put("oceanM", "Vẫn còn những mục chưa hoàn thành, không thể gửi thông tin trùng lặp");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			if (hhzt == 1 ||  hhzt_indo >0) {
				jsonObject.put("oceanC", -4);
				jsonObject.put("oceanM", "Thẩm định không thông qua, vui lòng một tháng sau đề xuất lại.");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			
			//借款金额不能小于150万
			int money_jk=Integer.parseInt(jk_money.replace(",","").replace(".",""));
			if(money_jk<1500000) {
				jsonObject.put("oceanC", -1);
				jsonObject.put("oceanM", "Tạm thời ngừng chức năng vay 1000.000 vnđ, vui lòng đề xuất lại khoản vay khác.");
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
				} else if (dataJJJK.getInt("cl02_status") != 3 && dataJJJK.getInt("cl03_status") == 3
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
					jsonObject.put("oceanC", -4);
					jsonObject.put("oceanM", "Cách lần vay kế tiếp còn " + dateday + " ngày " + datehour + " tiếng "
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
						jsonObject.put("oceanC", -3);
						jsonObject.put("oceanM", "Vẫn còn những mục chưa hoàn thành, không thể gửi thông tin trùng lặp");
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

			if (jk_date == 30) {
				jk_date = 2;
			}
			if (jk_date == 15) {
				jk_date = 1;
			}
			try {
				List<DataRow> list = jdbUserService.getAuditors();
                int shenhezu[] = new int[list.size()];
                for (int i = 0, n = list.size(); i < n; i++) {
                    int cmsUserId = list.get(i).getInt("user_id");
                    shenhezu[i] = cmsUserId;
                }
				Random random = new Random();
				//int xiabiao = random.nextInt(shenhezu.length);
				//int shenheren = shenhezu[xiabiao];
				int shenheren = 2002;
				int oldshenheren = jdbUserService.getShenHeRen(userid2);
				int stateold = jdbUserService.getOLDstate(oldshenheren);

				if (oldshenheren != 0 && stateold == 1  && oldshenheren !=2004  && oldshenheren !=2015  && oldshenheren !=2038 && oldshenheren !=2043 ) {
					shenheren = oldshenheren;
				}
				Calendar calendar = Calendar.getInstance();
				SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				DataRow data3 = new DataRow();
				data3.set("userid", userid2);
				data3.set("create_date", fmtrq.format(calendar.getTime()));
				data3.set("daytime", sdfday.format(calendar.getTime()));
				data3.set("jk_money", jk_money);
				data3.set("jk_date", jk_date);
				data3.set("annualrate", 30);
				data3.set("shy_money", jk_money);
				data3.set("onesh", shenheren);
				data3.set("twosh", shenheren);
				data3.set("threesh", shenheren);

				String appurl="Hotline: 0923087819";
				String  funtionname = "APP_USER_JK";
				DataRow rowFun=  jdbUserService.getnewmsg_function(funtionname);
				if(rowFun!=null) {
					appurl=rowFun.getString("content");
				}
				
				DataRow user = jdbUserService.getUserRecThreeInfo(userid2);
				if (jkDataLast != null) {
					int yuqts = jkDataLast.getInt("yuq_ts");
					if(yuqts<16) {
						data3.set("cl_status", 1);
						data3.set("cl02_status", 1);
						data3.set("is_old_user", 1);
						data3.set("cl_yj","Old User");	
					    data3.set("cl_ren","888");
					    data3.set("cl02_yj","Old User");	
					    data3.set("cl02_ren","888");
						int nn = Integer.parseInt(borrMoney.replace(",", "").replace(".",""));
						
						int fklv = 75;
						int lx = userMoneyBase.getUMBaseCalculateProductInterest(nn, jk_date, userid2, username);
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
					    
					    data3.set("cl03_status","1");
					    data3.set("cl03_yj","Old User");	
					    data3.set("cl03_ren","888");
					    data3.set("spzt","1");
					    data3.set("sfyfk","2");
					    data3.set("cl03_time",fmtrq.format(calendar.getTime()));
			    		
						String appName ="OCEAN" ; //APP名字
					    if(username.substring(0,4).equals("OCEAN")){
					    	appName="OCEAN";					    	
					    }
//						String content   =  appName+" chao "+userrealname+" de xuat vay cua ban da duoc chap thuan, sau 24h chua nhan duoc khoan vay xin"+appurl;
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
						int fklv = 75;
						int lx = userMoneyBase.getUMBaseCalculateProductInterest(nn, jk_date, userid2, username);
						if(lx > 0) {
							fklv =100 - lx;
						}
						data3.set("sjsh_money", borrMoney);
						data3.set("sjds_money", famt.format(nn * fklv / 100));
						data3.set("jyfk_money", borrMoney);
						data3.set("lx", famt.format(nn * (100 - fklv) / 100));
						
						String appName ="OCEAN" ; //APP名字
					    if(username.substring(0,4).equals("OCEAN")){
					    	appName="OCEAN";					    	
					    }
//						String content   =  appName+" chao! Vui long vao ung dung hoan tat quay video de vay tien lien tay chi voi 10 phut."+appurl;
//						SendFTP sms = new SendFTP();
//						String  response = sms.sendMessageFTP(content,mobilePhone);
					}
					
				}
				if (jkcount == 0) {
					jdbUserService.insertJKInfo(data3);
				}
				jsonObject.put("oceanC", 0);
				jsonObject.put("oceanM", "Đề nghị đã được gửi đi, nhân viên cần 1 ngày làm việc để xác nhận.");
				// 增加消息
				DataRow row = new DataRow();
				row.set("userid", userid2);
				row.set("title", "Đề xuất vay");
				row.set("neirong", "Chúng tôi sẽ cố gắng xử lý đề xuất vay của bạn trong thời gian sớm nhất.");
				row.set("fb_time", fmtrq.format(calendar.getTime()));
				jdbUserService.insertUserMsg(row);
			} catch (Exception e) {
				jsonObject.put("oceanC", -3);
				jsonObject.put("oceanM", "Lỗi hệ thống, đề nghị vay gửi đi không thành công");
				e.printStackTrace();
			}
			this.getWriter().write(jsonObject.toString());
			return null;
		} else {
			return null;
		}

	}

	// ShowJKJD
	public ActionResult doOceanBorHRe() throws Exception {
		logger.info("请求ip" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
		com.alibaba.fastjson.JSONObject jsonObj = jsonString;
		int userId = jsonObj.getInteger("oceanid");
		// 根据id 获取被邀请人的信�??
		String miwen = jsonObj.getString("oceantoken");

		String jiamiwen = Encrypt.MD5(userId + jiami);

		if (jiamiwen.equals(miwen)) {
			DataRow row = new DataRow();
			// 默认第一�??
			int curPage = jsonObj.getInteger("oceanPn");
			JSONArray jsonArray = new JSONArray();
			List<DataRow> list = jdbUserService.getBorrHisRecordListPage(curPage, 10, userId);

			for (DataRow object : list) {
				DataRow data = new DataRow();
				data.put("id", object.getString("id"));
				data.put("oceanIr", object.getString("hkqd"));
				data.put("oceanFm", object.getString("sjsh_money"));
				data.put("oceanDlo", object.getString("jk_date"));
				data.put("oceanFam", object.getString("sjds_money"));
				data.put("oceanLvl", object.getString("jk_money"));
				data.put("oceanCd", object.getString("create_date"));
				jsonArray.add(JSONObject.fromBean(data));
			}

			int moneycode = jdbUserService.getMoneyCode(userId);
			row.set("oceanHlist", jsonArray);
			row.set("oceanCv", moneycode + "");
			JSONObject object = JSONObject.fromBean(row);
			this.getWriter().write(object.toString());
			return null;
		} else {
			JSONObject jsObj = new JSONObject();
			jsObj.put("oceanC", 101);
			jsObj.put("oceanM", "Lỗi hệ thống, vui lòng thử lại sau！");
			this.getWriter().write(jsObj.toString());
			return null;
		}

	}

	// 项目进行还款(详情)
	public ActionResult doOceanRfuRe() throws Exception {
		logger.info("请求ip" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
		com.alibaba.fastjson.JSONObject jsonObj = jsonString;

		int jkid = jsonObj.getInteger("oceanjkid");
		JSONObject jsonObject = new JSONObject();
		String miwen = jsonObj.getString("oceantoken");

		String jiamiwen = Encrypt.MD5(jkid + jiami);

		if (jiamiwen.equals(miwen)) {
			if (jkid == 0) {
				jsonObject.put("oceanC", -1);
				jsonObject.put("oceanM", "Lỗi hệ thống, vui lòng thử lại sau！");
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
			object.put("oceanC", 0);
			object.put("oceanYq15", yanqi15);
			object.put("oceanYq30", yanqi30);
			this.getWriter().write(object.toString());
			return null;
		} else {
			return null;
		}

	}

	// 确定借款视频是否上传（返回结果）
	public ActionResult doOceanViCh() throws Exception {
		logger.info("请求ip" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
		com.alibaba.fastjson.JSONObject jsonObj = jsonString;

		String videoUrl = jsonObj.getString("oceanVu");
		logger.info("视频地址" + videoUrl);
		int userid = jsonObj.getInteger("oceanid");
		String miwen = jsonObj.getString("oceantoken");

		String jiamiwen = Encrypt.MD5(userid + jiami);

		if (jiamiwen.equals(miwen)) {
			JSONObject jsonObject = new JSONObject();

			if (userid == 0) {
				jsonObject.put("oceanC", -1);
				jsonObject.put("oceanM", "Vui lòng đăng nhập trước"); // 请先登录
				this.getWriter().write(jsonObject.toString());

				return null;

			}
			// 根据用户ID 查询�??要上传视频的借款项目

			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			int jkshid = jdbUserService.getJKshid(userid);
			if (jkshid == 0) {

				jsonObject.put("oceanC", -2);
				jsonObject.put("oceanM",
						"Video cần tải không tồn tại, vui lòng liên hệ bộ phận dịch vụ khách hàng để được hỗ trợ"); // 要上传的视频的项目不存在
																													// 请联系客服处理
				// 请联系客服处�??
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			
			//判断视频地址
			if(videoUrl == null  || videoUrl.isEmpty()) {
				jsonObject.put("oceanC", -3);
				jsonObject.put("oceanM","Không thể đăng tải video của bạn do không tim thấy video của bạn. Vui lòng thử lại sau ít phút hoặc liên hệ CSKH của chúng tôi để được hỗ trợ đăng tả."); // 要上传的视频的地址不存在
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
			jsonObject.put("oceanC", 0);
			jsonObject.put("oceanM", "Cập nhật thành công video đề nghị vay"); // 更新借款视频成功
			this.getWriter().write(jsonObject.toString());

			return null;
		} else {
			return null;
		}

	}

	public ActionResult doOceanReMo() throws Exception {
		logger.info("请求ip" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
		com.alibaba.fastjson.JSONObject jsonObj = jsonString;
		String userid = jsonObj.getString("oceanid");
		String hkqd = jsonObj.getString("oceanHk");

		String miwen = jsonObj.getString("oceantoken");

		String jiamiwen = Encrypt.MD5(hkqd + userid + jiami);

		if (jiamiwen.equals(miwen)) {
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
				jsonObject.put("oceanC", 0);
				jsonObject.put("oceanM", "Thành công");
			} catch (Exception e) {
				jsonObject.put("oceanM", "Lỗi hệ thống, vui lòng thử lại sau！");
				e.printStackTrace();
			}
			this.getWriter().write(jsonObject.toString());
			return null;
		} else {
			return null;
		}

	}

	public ActionResult doOceanUsIoC() throws Exception {
		logger.info("请求ip" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
		com.alibaba.fastjson.JSONObject jsonObj = jsonString;

		String userid = jsonObj.getString("oceanid");
		String miwen = jsonObj.getString("oceantoken");
		String jiamiwen = Encrypt.MD5(userid + jiami);

		if (jiamiwen.equals(miwen)) {
			String dwlat = jsonObj.getString("oceanDa");
			String dwlng = jsonObj.getString("oceanDn");

			JSONObject jsonObject = new JSONObject();
			try {

				DataRow row5 = new DataRow();
				row5.set("userId", userid);
				String jtdz = "none";
				/*String jsonstring = sendGet(dwlat.replace(",", "."), dwlng.replace(",", "."));
				com.alibaba.fastjson.JSONObject json = (com.alibaba.fastjson.JSONObject) JSON.parse(jsonstring);
				com.alibaba.fastjson.JSONArray jsonArray = json.getJSONArray("results");
				if (jsonArray.size() > 0) {
					com.alibaba.fastjson.JSONObject object = jsonArray.getJSONObject(0);
					jtdz = object.getString("formatted_address");
				}*/
				row5.set("dwlat", dwlat.replace(",", "."));
				row5.set("dwlng", dwlng.replace(",", "."));
				row5.set("jtdz", jtdz);
				row5.set("create_time", new Date());
				jdbUserService.addUserDWIP(row5);

				jsonObject.put("oceanC", 0);
				jsonObject.put("oceanM", "Location Success");
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

	/**
	 * 获得客户的真实IP地址
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
		String url = "https://maps.google.com/maps/api/geocode/json?key=AIzaSyA-w4Gq2lAEe15VHVK5y7T4JZEi5PgDMqY&latlng="
				+ lat + "," + lng + "&language=EN&sensor=false";
		JSONObject jsonObject = null;
		StringBuilder json = new StringBuilder();
		String nonce = null;
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

	public ActionResult doOceanBaCRf() throws Exception {
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
		JSONObject jsonObject = new JSONObject();
		com.alibaba.fastjson.JSONObject jsonObj = jsonString;

		String miwen = jsonObj.getString("oceantoken");
		String jiamiwen = Encrypt.MD5("ourbk" + jiami);
		if (jiamiwen.equals(miwen)) {
			JSONArray array = new JSONArray();
			JSONObject obj = new JSONObject();
			obj.put("oceanAt", "QUACH THI XUYEN");
			obj.put("oceanCn", "1903 5460 1810 10");
			obj.put("oceanBn", "Techcombank -Buôn Ma Thuột ");
//			JSONObject obj1 = new JSONObject();
//			obj1.put("oceanAt", "CÔNG TY TNHH F168");
//			obj1.put("oceanCn", "01810 - 0360 - 2501");
//			obj1.put("oceanBn", "VIETCOMBANK - CN Nam Sài Gòn");
//			JSONObject obj2 = new JSONObject();
//			obj2.put("oceanAt", "CÔNG TY TNHH F168");
//			obj2.put("oceanCn", "63402 - 0101 - 7335");
//			obj2.put("oceanBn", "AGRIBANK - CN Nhà Bè");
			array.add(obj);
//			array.add(obj1);
//			array.add(obj2);
			jsonObject.put("oceanBl", array);
			this.getWriter().write(jsonObject.toString());
			return null;
		} else {
			JSONObject jsObj = new JSONObject();
			jsObj.put("oceanC", 101);
			jsObj.put("oceanM", "Lỗi hệ thống, vui lòng thử lại sau！");
			this.getWriter().write(jsObj.toString());
			return null;
		}

	}

	public ActionResult doOceanMeGt() throws Exception {
		logger.info("请求ip" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
		com.alibaba.fastjson.JSONObject jsonObj = jsonString;
		String miwen = jsonObj.getString("oceantoken");
		String jiamiwen = Encrypt.MD5("msg" + jiami);
		JSONObject jsonObject = new JSONObject();
		if (jiamiwen.equals(miwen)) {
			List<DataRow> list = jdbUserService.getMsgCenter();
			JSONArray jsonArray = new JSONArray();
			for (Object object : list) {

				jsonArray.add(JSONObject.fromBean(object));
			}

			jsonObject.put("oceanMl", jsonArray);

		} else {
			JSONObject jsObj = new JSONObject();
			jsObj.put("oceanC", 101);
			jsObj.put("oceanM", "Lỗi hệ thống, vui lòng thử lại sau！");
			this.getWriter().write(jsObj.toString());
		}

		this.getWriter().write(jsonObject.toString());
		return null;
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
	public ActionResult doGetFBBook() throws Exception {
		logger.info("用户ip" + getipAddr());
		JSONObject jsonObject = new JSONObject();
		String userid = getStrParameter("oceanid");
		
		logger.info("认证Facebook："+userid);
		SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String report_task_token = getStrParameter("reporttoken");
		String fbtoken ="";
		String urlr = "https://vn.dotconnect.io/authorize_api/finishProcess?report_task_token="+report_task_token;
		String resopnser = sendGet(urlr);
		com.alibaba.fastjson.JSONObject Objr = com.alibaba.fastjson.JSONObject.parseObject(resopnser);
		if("20000".equals(Objr.getString("code"))) {
			String urlauth = "https://vn.dotconnect.io/data_api/reports/"+report_task_token;
			String resopnseauth = sendGet(urlauth);
			com.alibaba.fastjson.JSONObject Objauth = com.alibaba.fastjson.JSONObject.parseObject(resopnseauth);
			if("20000".equals(Objauth.getString("code"))) {
				String data = Objauth.getString("data");
				com.alibaba.fastjson.JSONObject userObjdata = com.alibaba.fastjson.JSONObject.parseObject(data);
				com.alibaba.fastjson.JSONArray datasource_status = userObjdata.getJSONArray("datasource_status");
				if(datasource_status.size()>0) {
					for (int i=0;i<datasource_status.size();i++) {
						com.alibaba.fastjson.JSONObject postsfbObj = datasource_status.getJSONObject(i);
						String website = postsfbObj.getString("website");
						if("facebook".equals(website) && "FINISHED".equals(postsfbObj.getString("running_status"))) {
							fbtoken = postsfbObj.getString("auth_token");
						}
					}
				}
			}
		}
		
		//fbtoken
		String urlfb = "https://vn.dotconnect.io/data_api/raw_data/"+fbtoken;
		String resopnsefb = sendGet(urlfb);
		com.alibaba.fastjson.JSONObject Objfb = com.alibaba.fastjson.JSONObject.parseObject(resopnsefb);
		if("20000".equals(Objfb.getString("code"))) {
			String datafb = Objfb.getString("data");
			com.alibaba.fastjson.JSONObject dataObjectfb = com.alibaba.fastjson.JSONObject.parseObject(datafb);
			String datafb1 = dataObjectfb.getString("data");
			com.alibaba.fastjson.JSONObject dataObjectfb1 = com.alibaba.fastjson.JSONObject.parseObject(datafb1);
			
			String userfb = dataObjectfb1.getString("user");
			com.alibaba.fastjson.JSONObject userObjfb = com.alibaba.fastjson.JSONObject.parseObject(userfb);
			String nameString = userObjfb.getString("name");
			String idString = userObjfb.getString("id");
			String emailString = userObjfb.getString("email");
			DataRow  row = new DataRow();
			row.set("userid", userid);
			row.set("fbname", nameString);
			row.set("fbid", idString);
			row.set("fbemail", emailString);
			com.alibaba.fastjson.JSONArray friendsfb = dataObjectfb1.getJSONArray("friends");
			com.alibaba.fastjson.JSONArray photosfb = dataObjectfb1.getJSONArray("photos");
			if(friendsfb.size()>0) {
				row.set("fbfriend", friendsfb.size());
			}
			if(friendsfb.size()>0) {
				row.set("fbphoto", photosfb.size());
			}
			row.set("createtime", fmtrq.format(new Date()));
			jbdcms3Service.addReporttokenFBuser(row);
			
			com.alibaba.fastjson.JSONArray postsfb = dataObjectfb1.getJSONArray("posts");
			if(postsfb.size()>0) {
				for (int i=0;i<postsfb.size();i++) {
					com.alibaba.fastjson.JSONObject postsfbObj = postsfb.getJSONObject(i);
					String comment_count = postsfbObj.getString("comment_count");
					String like_count = postsfbObj.getString("like_count");
					String format_time = postsfbObj.getString("format_time");
					String post_count = postsfbObj.getString("post_count");
					DataRow  row11 = new DataRow();
					row11.set("userid", userid);
					row11.set("comment_count", comment_count);
					row11.set("like_count", like_count);
					row11.set("format_time", format_time);
					row11.set("post_count", post_count);
					row11.set("createtime", fmtrq.format(new Date()));
					jbdcms3Service.addReporttokenFBPosts(row11);
				}
			}
			DataRow dataAuth = jbdcms3Service.getAuthRow(userid);
			if(dataAuth != null) {
				DataRow  row111 = new DataRow();
				row111.set("userid", userid);
				row111.set("report_task_token_fb", report_task_token);
				row111.set("facebook", fbtoken);
				row111.set("fbstate", 1);
				row111.set("rztime_fb", fmtrq.format(new Date()));
				jbdcms3Service.updateAuthRow(row111);
			}else {
				DataRow  row111 = new DataRow();
				row111.set("userid", userid);
				row111.set("report_task_token_fb", report_task_token);
				row111.set("facebook", fbtoken);
				row111.set("fbstate", 1);
				row111.set("rztime_fb", fmtrq.format(new Date()));
				jbdcms3Service.addReporttoken(row111);
			}
			jsonObject.put("oceanC", 0);
			jsonObject.put("oceanM", "Thành công");
			this.getWriter().write(jsonObject.toString());
		}
		return null;
	}
	
	public ActionResult doGetZaloAuth() throws Exception {
		logger.info("用户ip" + getipAddr());
		JSONObject jsonObject = new JSONObject();
		String userid = getStrParameter("oceanid");

		logger.info("认证Zalo："+userid);
		SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String report_task_token = getStrParameter("reporttoken");
		String fbtoken ="";
		String urlr = "https://vn.dotconnect.io/authorize_api/finishProcess?report_task_token="+report_task_token;
		String resopnser = sendGet(urlr);
		com.alibaba.fastjson.JSONObject Objr = com.alibaba.fastjson.JSONObject.parseObject(resopnser);
		if("20000".equals(Objr.getString("code"))) {
			String urlauth = "https://vn.dotconnect.io/data_api/reports/"+report_task_token;
			String resopnseauth = sendGet(urlauth);
			com.alibaba.fastjson.JSONObject Objauth = com.alibaba.fastjson.JSONObject.parseObject(resopnseauth);
			if("20000".equals(Objauth.getString("code"))) {
				String data = Objauth.getString("data");
				com.alibaba.fastjson.JSONObject userObjdata = com.alibaba.fastjson.JSONObject.parseObject(data);
				com.alibaba.fastjson.JSONArray datasource_status = userObjdata.getJSONArray("datasource_status");
				if(datasource_status.size()>0) {
					for (int i=0;i<datasource_status.size();i++) {
						com.alibaba.fastjson.JSONObject postsfbObj = datasource_status.getJSONObject(i);
						String website = postsfbObj.getString("website");
						if("zalo".equals(website)  && "FINISHED".equals(postsfbObj.getString("running_status")) ) {
							fbtoken = postsfbObj.getString("auth_token");
						}
					}
				}
			}
		}
		
		//zalotoken
		String urlfb = "https://vn.dotconnect.io/data_api/raw_data/"+fbtoken;
		String resopnsefb = sendGet(urlfb);
		com.alibaba.fastjson.JSONObject Objfb = com.alibaba.fastjson.JSONObject.parseObject(resopnsefb);
		if("20000".equals(Objfb.getString("code"))) {
			String datafb = Objfb.getString("data");
			com.alibaba.fastjson.JSONObject dataObjectfb = com.alibaba.fastjson.JSONObject.parseObject(datafb);
			
			String datafb1 = dataObjectfb.getString("basic_info");
			com.alibaba.fastjson.JSONObject dataObjectfb1 = com.alibaba.fastjson.JSONObject.parseObject(datafb1);
			String birthday = dataObjectfb1.getString("birthday");
			SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			SimpleDateFormat sim=new SimpleDateFormat("dd-MM-yyyy");
			if(!StringHelper.isEmpty(birthday)) {
				birthday = sim.format(new Date(Long.parseLong(String.valueOf(birthday)))); 
			}
			String gender = dataObjectfb1.getString("gender");
			String nick_name = dataObjectfb1.getString("nick_name");
			String phone_number = dataObjectfb1.getString("phone_number");
			if(!StringHelper.isEmpty(phone_number)) {
				if(phone_number.length()>3) {
					if("+84".equals(phone_number.substring(0, 3))) {
						phone_number = "0" + phone_number.substring(3);
					}
				}
			}
			String avatar = dataObjectfb1.getString("avatar");

			DataRow  row = new DataRow();
			row.set("userid", userid);
			row.set("birthday", birthday);
			row.set("gender", gender);
			row.set("nick_name", nick_name);
			row.set("phone_number", phone_number);
			row.set("avatar", avatar);
			com.alibaba.fastjson.JSONArray activities = dataObjectfb.getJSONArray("activities");
			com.alibaba.fastjson.JSONArray officials = dataObjectfb.getJSONArray("officials");
			com.alibaba.fastjson.JSONArray groups = dataObjectfb.getJSONArray("groups");
			com.alibaba.fastjson.JSONArray friends = dataObjectfb.getJSONArray("friends");
			if(activities != null) {
				row.set("activities", activities.size());
			}
			if(officials != null) {
				row.set("officials", officials.size());
			}
			if(groups != null) {
				row.set("groups", groups.size());
			}
			if(friends != null) {
				row.set("friends", friends.size());
			}
			row.set("createtime", fmtrq.format(new Date()));
			jbdcms3Service.addReporttokenZalouser(row);
			
			com.alibaba.fastjson.JSONArray postsfb = dataObjectfb.getJSONArray("groups");
			if(postsfb != null) {
				for (int i=0;i<postsfb.size();i++) {
					com.alibaba.fastjson.JSONObject postsfbObj = postsfb.getJSONObject(i);
					String group_creator_id = postsfbObj.getString("group_creator_id");
					String group_img = postsfbObj.getString("group_img");
					String group_id = postsfbObj.getString("group_id");
					String group_name = postsfbObj.getString("group_name");
					String group_desc = postsfbObj.getString("group_desc");
					String num_mems = postsfbObj.getString("num_mems");
					DataRow  row11 = new DataRow();
					row11.set("userid", userid);
					row11.set("group_creator_id", group_creator_id);
					row11.set("group_img", group_img);
					row11.set("group_id", group_id);
					row11.set("group_name", group_name);
					row11.set("group_desc", group_desc);
					row11.set("num_mems", num_mems);
					row11.set("createtime", fmtrq.format(new Date()));
					jbdcms3Service.addReporttokenZalogroups(row11);
				}
			}
			
			com.alibaba.fastjson.JSONArray friendsArr = dataObjectfb.getJSONArray("friends");
			if(friendsArr != null) {
				for (int i=0;i<friendsArr.size();i++) {
					com.alibaba.fastjson.JSONObject postsfbObj = friendsArr.getJSONObject(i);
					String friendbirthday = postsfbObj.getString("birthday");
					if(!StringHelper.isEmpty(friendbirthday)) {
						friendbirthday = sim.format(new Date(Long.parseLong(String.valueOf(friendbirthday)))); 
					}
					String last_update_time = postsfbObj.getString("last_update_time");
					if(!StringHelper.isEmpty(last_update_time)) {
						last_update_time = sdf.format(new Date(Long.parseLong(String.valueOf(last_update_time)))); 
					}
					String last_action_time = postsfbObj.getString("last_action_time");
					if(!StringHelper.isEmpty(last_action_time)) {
						last_action_time = sdf.format(new Date(Long.parseLong(String.valueOf(last_action_time)))); 
					}
					String friendgender = postsfbObj.getString("gender");
					String user_id = postsfbObj.getString("user_id");
					String user_name = postsfbObj.getString("user_name");
					String friendphone_number = postsfbObj.getString("phone_number");
					if(!StringHelper.isEmpty(friendphone_number)) {
						if(friendphone_number.length()>3) {
							if("+84".equals(friendphone_number.substring(0, 3))) {
								friendphone_number = "0" + friendphone_number.substring(3);
							}
						}
					}
					String friendavatar = postsfbObj.getString("avatar");
					String display_name = postsfbObj.getString("display_name");
					String type = postsfbObj.getString("type");
					DataRow  row11 = new DataRow();
					row11.set("userid", userid);
					row11.set("friendbirthday", friendbirthday);
					row11.set("last_update_time", last_update_time);
					row11.set("last_action_time", last_action_time);
					row11.set("friendgender", friendgender);
					row11.set("user_id", user_id);
					row11.set("user_name", user_name);
					row11.set("friendphone_number", friendphone_number);
					row11.set("friendavatar", friendavatar);
					row11.set("display_name", display_name);
					row11.set("type", type);
					row11.set("createtime", fmtrq.format(new Date()));
					jbdcms3Service.addReporttokenZalofriends(row11);
				}
			}
			DataRow dataAuth = jbdcms3Service.getAuthRow(userid);
			if(dataAuth != null) {
				DataRow  row111 = new DataRow();
				row111.set("userid", userid);
				row111.set("report_task_token_zalo", report_task_token);
				row111.set("zalo", fbtoken);
				row111.set("zalostate", 1);
				row111.set("rztime_zalo", fmtrq.format(new Date()));
				jbdcms3Service.updateAuthRow(row111);
			}else {
				DataRow  row111 = new DataRow();
				row111.set("userid", userid);
				row111.set("report_task_token_zalo", report_task_token);
				row111.set("zalo", fbtoken);
				row111.set("zalostate", 1);
				row111.set("rztime_zalo", fmtrq.format(new Date()));
				jbdcms3Service.addReporttoken(row111);
			}
			jsonObject.put("oceanC", 0);
			jsonObject.put("oceanM", "Thành công");
			this.getWriter().write(jsonObject.toString());
		}
		return null;
	}
	
	public ActionResult doGetYYSPhone() throws Exception {
		logger.info("用户ip" + getipAddr());
		JSONObject jsonObject = new JSONObject();
		String userid = getStrParameter("oceanid");
		
		logger.info("认证运营商："+userid);
		SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String report_task_token = getStrParameter("reporttoken");
		String fbtoken ="";
		String urlr = "https://vn.dotconnect.io/authorize_api/finishProcess?report_task_token="+report_task_token;
		String resopnser = sendGet(urlr);
		com.alibaba.fastjson.JSONObject Objr = com.alibaba.fastjson.JSONObject.parseObject(resopnser);
		int aaaa=0;
		if("20000".equals(Objr.getString("code"))) {
			String urlauth = "https://vn.dotconnect.io/data_api/reports/"+report_task_token;
			String resopnseauth = sendGet(urlauth);
			com.alibaba.fastjson.JSONObject Objauth = com.alibaba.fastjson.JSONObject.parseObject(resopnseauth);
			if("20000".equals(Objauth.getString("code"))) {
				String data = Objauth.getString("data");
				com.alibaba.fastjson.JSONObject userObjdata = com.alibaba.fastjson.JSONObject.parseObject(data);
				com.alibaba.fastjson.JSONArray datasource_status = userObjdata.getJSONArray("datasource_status");
				if(datasource_status.size()>0) {
					for (int i=0;i<datasource_status.size();i++) {
						com.alibaba.fastjson.JSONObject postsfbObj = datasource_status.getJSONObject(i);
						String website = postsfbObj.getString("website");
						if("viettel".equals(website)  && "FINISHED".equals(postsfbObj.getString("running_status")) ) {
							fbtoken = postsfbObj.getString("auth_token");
							aaaa=1;
						}else if("vinaphone".equals(website)  && "FINISHED".equals(postsfbObj.getString("running_status")) ) {
							fbtoken = postsfbObj.getString("auth_token");
							aaaa=1;
						}else if("mobifone".equals(website)  && "FINISHED".equals(postsfbObj.getString("running_status")) ) {
							fbtoken = postsfbObj.getString("auth_token");
							aaaa=1;
						}
					}
				}
				if(aaaa == 1) {
					com.alibaba.fastjson.JSONObject report_data = userObjdata.getJSONObject("report_data");
					String operator_list = report_data.getString("operator_list");
					com.alibaba.fastjson.JSONObject datayys= com.alibaba.fastjson.JSONObject.parseObject(operator_list);
					
					com.alibaba.fastjson.JSONArray person_basic_info = datayys.getJSONArray("person_basic_info");
					if(person_basic_info.size()>0) {
						com.alibaba.fastjson.JSONObject postsfbObj = person_basic_info.getJSONObject(0);
						String balance = postsfbObj.getString("balance");
						String phone = postsfbObj.getString("phone");
						if(!StringHelper.isEmpty(phone)) {
							if(!"0".equals(phone.charAt(0))) {
								phone = "0"+phone;
							}
						}
						String validity_date = postsfbObj.getString("validity_date");
						String source = postsfbObj.getString("source");
						
						String packages ="";
						String packages_date ="";
						String name ="";
						String start_date ="";
						com.alibaba.fastjson.JSONArray packagesobj = postsfbObj.getJSONArray("packages");
						if(packagesobj.size()>0) {
							com.alibaba.fastjson.JSONObject pack = packagesobj.getJSONObject(0);
							packages = pack.getString("balance")+pack.getString("unit");
							packages_date = pack.getString("validity_date");
							name = pack.getString("name");
							start_date = pack.getString("start_date");
						}
						DataRow  row11 = new DataRow();
						row11.set("userid", userid);
						row11.set("balance", balance);
						row11.set("phone", phone);
						row11.set("validity_date", validity_date);
						row11.set("source", source);
						row11.set("packages", packages);
						row11.set("packages_date", packages_date);
						row11.set("name", name);
						row11.set("start_date", start_date);
						row11.set("createtime", fmtrq.format(new Date()));
						jbdcms3Service.addReporttokenYysuser(row11);
						
					}
					
					com.alibaba.fastjson.JSONArray call_detail_summary = datayys.getJSONArray("call_detail_summary");
					if(call_detail_summary.size()>0) {
						for (int i=0;i<call_detail_summary.size();i++) {
							com.alibaba.fastjson.JSONObject postsfbObj = call_detail_summary.getJSONObject(i);
							String call_cnt = postsfbObj.getString("call_cnt");
							String phone = postsfbObj.getString("phone");
							String call_length = postsfbObj.getString("call_length");
							DataRow  row11 = new DataRow();
							row11.set("userid", userid);
							row11.set("call_cnt", call_cnt);
							row11.set("phone", phone);
							row11.set("call_length", call_length);
							row11.set("createtime", fmtrq.format(new Date()));
							jbdcms3Service.addReporttokenYysdetail(row11);
						}
					}
					
					com.alibaba.fastjson.JSONArray top_up_history = datayys.getJSONArray("top_up_history");
					if(top_up_history.size()>0) {
						for (int i=0;i<top_up_history.size();i++) {
							com.alibaba.fastjson.JSONObject postsfbObj = top_up_history.getJSONObject(i);
							String source = postsfbObj.getString("source");
							String top_up_number = postsfbObj.getString("top_up_number");
							String amount = postsfbObj.getString("amount");
							String date = postsfbObj.getString("date");
							DataRow  row11 = new DataRow();
							row11.set("userid", userid);
							row11.set("source", source);
							row11.set("top_up_number", top_up_number);
							row11.set("amount", amount);
							row11.set("date", date);
							row11.set("createtime", fmtrq.format(new Date()));
							jbdcms3Service.addReporttokenYyshistory(row11);
						}
					}
					
					com.alibaba.fastjson.JSONArray call_mth_summary = datayys.getJSONArray("call_mth_summary");
					if(call_mth_summary.size()>0) {
						for (int i=0;i<call_mth_summary.size();i++) {
							com.alibaba.fastjson.JSONObject postsfbObj = call_mth_summary.getJSONObject(i);
							String call_cnt = postsfbObj.getString("call_cnt");
							String month = postsfbObj.getString("month");
							String call_length = postsfbObj.getString("call_length");
							DataRow  row11 = new DataRow();
							row11.set("userid", userid);
							row11.set("call_cnt", call_cnt);
							row11.set("month", month);
							row11.set("call_length", call_length);
							row11.set("createtime", fmtrq.format(new Date()));
							jbdcms3Service.addReporttokenYysmth(row11);
						}
					}
					DataRow dataAuth = jbdcms3Service.getAuthRow(userid);
					if(dataAuth != null) {
						DataRow  row111 = new DataRow();
						row111.set("userid", userid);
						row111.set("report_task_token_yys", report_task_token);
						row111.set("yys", fbtoken);
						row111.set("yysstate", 1);
						row111.set("rztime_yys", fmtrq.format(new Date()));
						jbdcms3Service.updateAuthRow(row111);
					}else {
						DataRow  row111 = new DataRow();
						row111.set("userid", userid);
						row111.set("report_task_token_yys", report_task_token);
						row111.set("yys", fbtoken);
						row111.set("yysstate", 1);
						row111.set("rztime_yys", fmtrq.format(new Date()));
						jbdcms3Service.addReporttoken(row111);
					}
					jsonObject.put("oceanC", 0);
					jsonObject.put("oceanM", "Thành công");
					this.getWriter().write(jsonObject.toString());
				}
			}
		}
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
		String jiamiwen = Encrypt.MD5(userid + tel + jiami);

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
	
	
	// 更新视频
	public ActionResult doOceanVideoUpdate() throws Exception {
		
		logger.info("请求ip" + getipAddr());
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
		com.alibaba.fastjson.JSONObject jsonObj = jsonString;

		String videoUrl = jsonObj.getString("oceanVu");
		logger.info("视频地址" + videoUrl);
		int userid = jsonObj.getInteger("oceanid");
		String miwen = jsonObj.getString("oceantoken");

		String jiamiwen = Encrypt.MD5(userid + jiami);

		if (jiamiwen.equals(miwen)) {
			JSONObject jsonObject = new JSONObject();

			if (userid == 0) {
				jsonObject.put("oceanC", -1);
				jsonObject.put("oceanM", "Vui lòng đăng nhập trước"); // 请先登录
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			
			// 根据用户ID 查询�??要上传视频的借款项目
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			int jkshid = jdbUserService.getJKshviid(userid);
			if (jkshid == 0) {

				jsonObject.put("oceanC", -2);
				jsonObject.put("oceanM","Video cần tải không tồn tại, vui lòng liên hệ bộ phận dịch vụ khách hàng để được hỗ trợ"); // 要上传的视频的项目不存在
				// 请联系客服处�??
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			
			//判断视频地址
			if(videoUrl == null  || videoUrl.isEmpty()) {
				jsonObject.put("oceanC", -3);
				jsonObject.put("oceanM","Không thể đăng tải video của bạn do không tim thấy video của bạn. Vui lòng thử lại sau ít phút hoặc liên hệ CSKH của chúng tôi để được hỗ trợ đăng tả."); // 要上传的视频的地址不存在
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			DataRow row = new DataRow();
			row.set("id", jkshid);
			row.set("spdz", videoUrl);
			row.set("spzt", 1);
			row.set("spsj", fmtrq.format(calendar.getTime()));
			jdbUserService.updateJKSPInfo(row);

			jsonObject.put("oceanC", 0);
			jsonObject.put("oceanM", "Cập nhật thành công video đề nghị vay"); // 更新借款视频成功
			this.getWriter().write(jsonObject.toString());

			return null;
		} 
		return null;
	}
	
	// 更新视频
		public ActionResult doOceanPhotoUpdate() throws Exception {
			

			logger.info("请求ip" + getipAddr());
			HttpServletRequest request = getRequest();
			com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
			com.alibaba.fastjson.JSONObject jsonObj = jsonString;

			String userid = jsonObj.getString("oceanid");
			String miwen = jsonObj.getString("oceantoken");
			String jiamiwen = Encrypt.MD5(userid + jiami);
			if (jiamiwen.equals(miwen)) {
				String p1 = jsonObj.getString("oceanPo");
				String p2 = jsonObj.getString("oceanPt");
				String p3 = jsonObj.getString("oceanPtr");
				String ui = jdbUserService.getUI(userid);

				int yhbd = jdbUserService.getUserBank(userid);
				int lianxi = jdbUserService.getUserLianXi(userid);
				int work = mofaUserService.getUserWork(userid);
				JSONObject jsonObject = new JSONObject();
				Calendar calendar = Calendar.getInstance();
				SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

				if ("".equals(p1) || p1 == null || "".equals(p2) || p2 == null || "".equals(p3) || p3 == null) {
					jsonObject.put("oceanC", -11);
					jsonObject.put("oceanM", "Lỗi tải lên hình ảnh");// 图片上传错误
					this.getWriter().write(jsonObject.toString());
					return null;

				}
				
				try {
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
					if (yhbd == 1 & lianxi == 1  &&  work==1) {
						row3.set("vipStatus", 1);// 工作认证�??1
					} else {
						row3.set("vipStatus", 0);// 工作认证�??1
					}
					jdbUserService.updateUserInfoH(row3);
					jsonObject.put("oceanC", 0);
					jsonObject.put("oceanM", "Thành công");// 成功
				} catch (Exception e) {

					jsonObject.put("oceanC", -3);
					jsonObject.put("oceanM", "Lỗi hệ thống, vui lòng thử lại sau！");// 系统异常，请稍后再试�??
					e.printStackTrace();
				}
				this.getWriter().write(jsonObject.toString());
				return null;
			} else {
				JSONObject jsObj = new JSONObject();
				jsObj.put("oceanC", 101);
				jsObj.put("oceanM", "Lỗi hệ thống, vui lòng thử lại sau！");
				this.getWriter().write(jsObj.toString());
				return null;
			}
		}
	
	
}
