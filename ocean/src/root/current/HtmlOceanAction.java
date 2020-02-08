package root.current;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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

import root.SendFTP;
import root.SendMsg;
import root.img.WebConstants;
import root.order.UserMoneyBase;
import root.tool.SendMsgCL;

import com.alibaba.fastjson.JSON;
import com.project.constant.IConstants;

import com.project.service.account.JBDUserService;
import com.project.utils.MemCachedUtil;
import com.shove.security.Encrypt;
import com.thinkive.base.jdbc.DBPage;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.util.CookieHelper;
import com.thinkive.base.util.SessionHelper;
import com.thinkive.base.util.StringHelper;
import com.thinkive.web.base.ActionResult;
import com.thinkive.web.base.BaseAction;

public class HtmlOceanAction extends BaseAction {
	private static Logger logger = Logger.getLogger(HtmlOceanAction.class);
	private static UserMoneyBase  userMoneyBase = new UserMoneyBase();
	/*private static UserService userService = new UserService();*/
	private static JBDUserService jdbUserService = new JBDUserService();
	String jiami = "GKHJD83df21IKkjdefu82ik82VSh";
	long time = 1000*60*3;
	
	/**
     * 生成随机数当作getItemID
     * n ： 需要的长度
     * @return
     */
    private static String getItemID( int n )
    {
        String val = "";
        Random random = new Random();
        for ( int i = 0; i < n; i++ )
        {
            String str = random.nextInt( 2 ) % 2 == 0 ? "num" : "char";
            if ( "char".equalsIgnoreCase( str ) )
            { // 产生字母
                int nextInt = random.nextInt( 2 ) % 2 == 0 ? 65 : 97;
                // System.out.println(nextInt + "!!!!"); 1,0,1,1,1,0,0
                val += (char) ( nextInt + random.nextInt( 26 ) );
            }
            else if ( "num".equalsIgnoreCase( str ) )
            { // 产生数字
                val += String.valueOf( random.nextInt( 10 ) );
            }
        }
        return val;
    }
	@Override
	public ActionResult doDefault() throws Exception{
		JSONObject jsonObject = new JSONObject();
		int userid ;
		String phone = "";
		int p = getIntParameter("type");
		if(p == 1){
			userid = getIntParameter("userid");
		}else{
			userid = SessionHelper.getInt("userid", getSession());
//			phone = SessionHelper.getString("mobilephone", getSession());
		}
		logger.info("AuthAction~~~~"+"userid~~~~"+userid+",phone~~~"+SessionHelper.getString("mobilephone", getSession())+",username~~~"+SessionHelper.getString("username", getSession()));
		if (userid > 0) {
			//获取身份证信息
			jsonObject.put("error", 0);
			DataRow drIdCard = jdbUserService.findUserFinance(userid+"");
			List<DataRow> bank = jdbUserService.findBankList();
			JSONArray jsonArray = new JSONArray();
			for (Object object : bank){
				jsonArray.add(JSONObject.fromBean(object));
			}
			jsonObject.put("banklist", jsonArray);
			if (drIdCard!=null) {
				jsonObject.put("realname", drIdCard.getString("realname"));//真实姓名
				jsonObject.put("idno", drIdCard.getString("idno"));//身份证号
				jsonObject.put("status", drIdCard.getString("status"));//状态
				phone =  drIdCard.getString("cellphone");
				if(StringHelper.isNotEmpty(phone)){
					jsonObject.put("mobilephone", phone);//手机
				}else{
					jsonObject.put("mobilephone", "");//手机
				}
			}else{
				jsonObject.put("realname", "");//真实姓名
				jsonObject.put("idno", "");//身份证号
				jsonObject.put("status", 0);//状态
				jsonObject.put("mobilephone", "");//手机
			}
			//获取银行信息
			DataRow drBankcard = jdbUserService.findUserBankcard(userid+"");
			if (drBankcard!=null) {
				
				jsonObject.put("bankname", drBankcard.getString("cardname"));//开户行
				jsonObject.put("cardno", drBankcard.getString("cardno").substring(drBankcard.getString("cardno").length()-4, drBankcard.getString("cardno").length()));//银行卡号
				jsonObject.put("cardstatus", drBankcard.getString("cardstatus"));//状态
			}else{
				jsonObject.put("bankname", "");//开户行
				jsonObject.put("cardno", "");//银行卡号
				jsonObject.put("cardstatus", 0);//状态
			}
		} 
		else 
		{
			jsonObject.put("error", -1);
			jsonObject.put("username", "");
		}
		this.getWriter().write(jsonObject.toString());
		return null;
	}
	/**
	* 获得客户的真实IP地址
	*
	* @return
	*/
	public String getipAddr( ){
		String ip = getRequest().getHeader("X-Real-IP");
		if(StringHelper.isEmpty(ip)){
			ip = jdbUserService.getRemortIP(getRequest());
		}
		return ip;
	}
	//h5注册接口
	public String ChangPhone(String phone) {
		String newphone = "";
		if("84".equals(phone.substring(0, 2))){
			phone = "0" + phone.substring(2);
		}
		String changphone = phone.substring(0, 4);
		System.out.println(changphone);
		if ("0120".equals(changphone)) {
			newphone = "070" + phone.substring(4);
		} else if ("0121".equals(changphone)) {
			newphone = "079" + phone.substring(4);
		} else if ("0122".equals(changphone)) {
			newphone = "077" + phone.substring(4);
		} else if ("0126".equals(changphone)) {
			newphone = "076" + phone.substring(4);
		} else if ("0128".equals(changphone)) {
			newphone = "078" + phone.substring(4);
		} else if ("0123".equals(changphone)) {
			newphone = "083" + phone.substring(4);
		} else if ("0124".equals(changphone)) {
			newphone = "084" + phone.substring(4);
		} else if ("0125".equals(changphone)) {
			newphone = "085" + phone.substring(4);
		} else if ("0127".equals(changphone)) {
			newphone = "081" + phone.substring(4);
		} else if ("0129".equals(changphone)) {
			newphone = "082" + phone.substring(4);
		} else if ("0162".equals(changphone)) {
			newphone = "032" + phone.substring(4);
		} else if ("0163".equals(changphone)) {
			newphone = "033" + phone.substring(4);
		} else if ("0164".equals(changphone)) {
			newphone = "034" + phone.substring(4);
		} else if ("0165".equals(changphone)) {
			newphone = "035" + phone.substring(4);
		} else if ("0166".equals(changphone)) {
			newphone = "036" + phone.substring(4);
		} else if ("0167".equals(changphone)) {
			newphone = "037" + phone.substring(4);
		} else if ("0168".equals(changphone)) {
			newphone = "038" + phone.substring(4);
		} else if ("0169".equals(changphone)) {
			newphone = "039" + phone.substring(4);
		} else if ("0186".equals(changphone)) {
			newphone = "056" + phone.substring(4);
		} else if ("0188".equals(changphone)) {
			newphone = "058" + phone.substring(4);
		} else if ("0199".equals(changphone)) {
			newphone = "059" + phone.substring(4);
		} else {
			newphone = phone;
		}
		return newphone;
	}
	//h5注册接口facebook
	public ActionResult doRegisterBookNewFaceHFiveCopy() throws Exception{  
		HttpServletRequest request = getRequest();
		JSONObject jsonObject = new JSONObject();
		//String phone = getStrParameter("mobile").trim().replaceAll(" ", "");//手机号
		String tokentoken = getStrParameter("token");
		logger.info(tokentoken);
		
		String url = "https://graph.accountkit.com/v1.3/access_token?grant_type=authorization_code&"
				+"code="+tokentoken+"&access_token=AA|1464848710356888|d1876a12a25b4abb24787ceb73524f65";
		
	    StringBuilder json = new StringBuilder();
	    
	    try {
	        URL getUrl = new URL(url);
	        // 返回URLConnection子类的对象
	        
	        HttpsURLConnection connection = (HttpsURLConnection) getUrl.openConnection();
	        // 连接
	        connection.setHostnameVerifier(new TrustAnyHostnameVerifier());
	        connection.connect();
	        InputStream inputStream = connection.getInputStream();
	        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
	        // 使用Reader读取输入流
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
	   System.out.println(json.toString());
	   com.alibaba.fastjson.JSONObject jsonString = com.alibaba.fastjson.JSONObject.parseObject(json.toString());
	   String accesstoken = jsonString.getString("access_token");
	   System.out.println(accesstoken);
	   
	   String url1 = "https://graph.accountkit.com/v1.0/me?access_token="+accesstoken;
		
	   
	    StringBuilder json1 = new StringBuilder();
	    try {
	        URL getUrl = new URL(url1);
	        // 返回URLConnection子类的对象
	        HttpsURLConnection connection = (HttpsURLConnection) getUrl.openConnection();
	        // 连接
	        connection.connect();
	        InputStream inputStream = connection.getInputStream();
	        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
	        // 使用Reader读取输入流
	        BufferedReader reader = new BufferedReader(inputStreamReader);
	        String lines;
	        while ((lines = reader.readLine()) != null) {
	            json1.append(lines);
	        }
	        reader.close();
	        // 断开连接
	        connection.disconnect();
	    } catch (IOException ioException) {
	        ioException.printStackTrace();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	   System.out.println(json1.toString());
	   com.alibaba.fastjson.JSONObject jsonString1 = com.alibaba.fastjson.JSONObject.parseObject(json1.toString());
	   String phonenumber = jsonString1.getString("phone");
	   com.alibaba.fastjson.JSONObject jsonString2 = com.alibaba.fastjson.JSONObject.parseObject(phonenumber);
	   String phone = "0"+jsonString2.getString("national_number");
	   if(phone.length() != 10 ){
			jsonObject.put("error", -5);
			jsonObject.put("msg", "Vui lòng xác nhận lại số điện thoại！");//用户不存在请先注册
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		int phonetype = getIntParameter("phonetype");
		String profession = getStrParameter("profession");
		int rating = getIntParameter("rating",0);
		String ip = getipAddr();//61.145.153.20
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		SimpleDateFormat sdfday = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String lastDate = sdf.format(new Date());
		String daytime = sdfday.format(new Date());
		logger.info("当前注册用户:手机号："+phone+"IP："+ip+"当前时间"+lastDate);
		
		String refferee = getStrParameter("refferee", "-1");
		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		logger.info("当前注册用户:手机号："+phone+"IP："+ip+"当前时间"+lastDate);
		
		//生成的秘钥
		String token = System.currentTimeMillis()+getItemID(16);
		long str = jdbUserService.getCountByPhone(phone);
		if (str > 0) {
			//添加登录记录
			//获取用户ID用于生成用户名
			String userid = jdbUserService.getIdByPhone(phone);
			int vipstatus = jdbUserService.getVipstatus(userid);
			String username = jdbUserService.getUsername(userid);
			//存到缓存
			SessionHelper.setString("tokenhtml",token,getSession());
			DataRow row = new DataRow();
			row.set("logindate", lastDate);
			row.set("userid", userid);
			jdbUserService.addUser("sduser_login_log",row);
			
			logger.info("当前登录用户"+userid+"ip"+ip+"时间"+lastDate);
			try {      	
				DataRow data = new DataRow();
				data.set("lastIP", ip);
				data.set("lastDate", lastDate);
				data.set("tokenhtml", token);//注册入口
				if(vipstatus == 0){
					if(phonetype == 1){
						username = "OCEAN-AND"+userid+"-on";
					}else if(phonetype == 2){
						username = "OCEAN-IOS"+userid+"-on";
					}else{
						username = "OCEAN-WIN"+userid+"-on";
					}
				}
				data.set("username", username);
				logger.info("userid:"+userid+",data"+data);
				jdbUserService.updateUser(userid, data);
				
				String mobile = phone;
				jsonObject.put("mobile", mobile);
				jsonObject.put("ui", userid);
				jsonObject.put("token", token);		
				jsonObject.put("error", -222);
				jsonObject.put("msg", "Thành công！");//注册成功
				this.getWriter().write(jsonObject.toString());
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("登录失败"+e);
				jsonObject.put("error", -10);
				jsonObject.put("msg", "Lỗi hệ thống, đăng ký không thành công!");//系统错误，注册失败
				this.getWriter().write(jsonObject.toString());
				return null;
			}
		}else{
			String pwd="";
			//向sd_user添加注册信息
			int intCount = (new Random()).nextInt(999999) + (new Random()).nextInt(999999);
			pwd = intCount+"";
			String resPWD = Encrypt.MD5(pwd + IConstants.PASS_KEY);
			String table = "sd_user";
			DataRow data = new DataRow();
			data.set("mobilePhone", phone);
			data.set("password", resPWD);
			data.set("businessPwd", resPWD);//交易密码默认为登录密码
			data.set("profession", 2);//职业状态
			data.set("lastIP", ip);
			data.set("hongbao", 0);
			data.set("lastDate", lastDate);
			data.set("createTime", lastDate);
			data.set("yearmonthday", daytime);
			if(!phone.equals(refferee)){
				data.set("refferee", refferee);
			}
			//登录次数
			data.set("loginCount", 0);
			data.set("rating", 0);//注册入口
			data.set("tokenhtml", token);//注册入口
			jdbUserService.addUser(table,data);
			//存到缓存
			SessionHelper.setString("tokenhtml",token,getSession());
			//获取用户ID用于生成用户名
			String userid = jdbUserService.getIdByPhone(phone);
			
			String username = "";
			if(phonetype == 1){
				username = "OCEAN-AND"+userid+"-off";
			}else if(phonetype == 2){
				username = "OCEAN-IOS"+userid+"-off";
			}else{
				username = "OCEAN-WIN"+userid+"-off";
			}
			data.set("username", username);
			jdbUserService.updateUser(userid, data);
			logger.info("添加用户信息"+data);
			//添加登录记录
			DataRow row = new DataRow();
			table = "sduser_login_log";
			row.set("logindate", lastDate);
			row.set("userid", userid);
			jdbUserService.addUser(table,row);
			
			logger.info("当前登录用户"+userid+"ip"+ip+"时间"+lastDate);
			try {      	
				//有此用户  修改最后登录时间  添加登录日志
				
				data.set("lastIP", ip);
				data.set("lastDate", lastDate);
				logger.info("userid:"+userid+",data"+data);
				jdbUserService.updateUser(userid, data);
				
				String mobile = phone;
				jsonObject.put("mobile", mobile);
				jsonObject.put("ui", userid);
				jsonObject.put("token", token);		
				jsonObject.put("error", 0);
				jsonObject.put("msg", "Đăng ký thành công！");//注册成功			
				this.getWriter().write(jsonObject.toString());
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("注册失败"+e);
				jsonObject.put("error", -10);
				jsonObject.put("msg", "Lỗi hệ thống, đăng ký không thành công!");//系统错误，注册失败
				this.getWriter().write(jsonObject.toString());
				return null;
			}
		}
}
	
	//h5注册接口facebook
			public ActionResult doRegisterBookNewFaceHFive() throws Exception{  
				HttpServletRequest request = getRequest();
				JSONObject jsonObject = new JSONObject();
				//String phone = getStrParameter("mobile").trim().replaceAll(" ", "");//手机号
				String tokentoken = getStrParameter("token");
				String phone = getStrParameter("mobile");
				logger.info(tokentoken);
				logger.info(phone);
				String memyzm = "";
				if (MemCachedUtil.cachedClient.keyExists(phone + "_HAHA")) {
					memyzm = (String) MemCachedUtil.cachedClient.get(phone + "_HAHA");
				}
				logger.info("memyzm:" + memyzm + ",code:" + getStrParameter("code"));
				String url = "https://graph.accountkit.com/v1.3/access_token?grant_type=authorization_code&"
						+"code="+tokentoken+"&access_token=AA|436349787125595|d1876a12a25b4abb24787ceb73524f65";
				if (StringUtils.isEmpty(memyzm)) {
					jsonObject.put("error", 3);
					jsonObject.put("msg", "Lỗi mã xác minh");// 验证码错误
					logger.warn("验证码有误");
					this.getWriter().write(jsonObject.toString());
					return null;
				}
				if (!tokentoken.equals(memyzm)) {
					jsonObject.put("error", 4);
					jsonObject.put("msg", "Mã xác minh không chính xác");// 验证码不正确
					logger.warn("验证码不正确");
					this.getWriter().write(jsonObject.toString());
					return null;
				}
				int phonetype = getIntParameter("phonetype");
				String ip = getipAddr();//61.145.153.20
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				SimpleDateFormat sdfday = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String lastDate = sdf.format(new Date());
				String daytime = sdfday.format(new Date());
				logger.info("当前注册用户:手机号："+phone+"IP："+ip+"当前时间"+lastDate);
				
				String refferee = getStrParameter("refferee", "-1");
				logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
				logger.info("当前注册用户:手机号："+phone+"IP："+ip+"当前时间"+lastDate);
				
				//生成的秘钥
				String token = System.currentTimeMillis()+getItemID(16);
				long str = jdbUserService.getCountByPhone(phone);
				if (str > 0) {
					//添加登录记录
					//获取用户ID用于生成用户名
					String userid = jdbUserService.getIdByPhone(phone);
					int vipstatus = jdbUserService.getVipstatus(userid);
					String username = jdbUserService.getUsername(userid);
					//存到缓存
					SessionHelper.setString("tokenhtml",token,getSession());
					DataRow row = new DataRow();
					row.set("logindate", lastDate);
					row.set("userid", userid);
					jdbUserService.addUser("sduser_login_log",row);
					
					logger.info("当前登录用户"+userid+"ip"+ip+"时间"+lastDate);
					try {      	
						DataRow data = new DataRow();
						data.set("lastIP", ip);
						data.set("lastDate", lastDate);
						data.set("tokenhtml", token);//注册入口
						if(vipstatus == 0){
							if(phonetype == 1){
								username = "OCEAN-AND"+userid+"-on";
							}else if(phonetype == 2){
								username = "OCEAN-IOS"+userid+"-on";
							}else{
								username = "OCEAN-WIN"+userid+"-on";
							}
						}
						data.set("username", username);
						logger.info("userid:"+userid+",data"+data);
						jdbUserService.updateUser(userid, data);
						
						String mobile = phone;
						jsonObject.put("mobile", mobile);
						jsonObject.put("ui", userid);
						jsonObject.put("token", token);		
						jsonObject.put("error", -222);
						jsonObject.put("msg", "Thành công！");//注册成功
						this.getWriter().write(jsonObject.toString());
						return null;
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("登录失败"+e);
						jsonObject.put("error", -10);
						jsonObject.put("msg", "Lỗi hệ thống, đăng ký không thành công!");//系统错误，注册失败
						this.getWriter().write(jsonObject.toString());
						return null;
					}
				}else{
					String pwd="";
					//向sd_user添加注册信息
					int intCount = (new Random()).nextInt(999999) + (new Random()).nextInt(999999);
					pwd = intCount+"";
					String resPWD = Encrypt.MD5(pwd + IConstants.PASS_KEY);
					String table = "sd_user";
					DataRow data = new DataRow();
					data.set("mobilePhone", phone);
					data.set("password", resPWD);
					data.set("businessPwd", resPWD);//交易密码默认为登录密码
					data.set("profession", 2);//职业状态
					data.set("lastIP", ip);
					data.set("hongbao", 0);
					data.set("lastDate", lastDate);
					data.set("createTime", lastDate);
					data.set("yearmonthday", daytime);
					if(!phone.equals(refferee)){
						data.set("refferee", refferee);
					}
					//登录次数
					data.set("loginCount", 0);
					data.set("rating", 0);//注册入口
					data.set("tokenhtml", token);//注册入口
					jdbUserService.addUser(table,data);
					//存到缓存
					SessionHelper.setString("tokenhtml",token,getSession());
					//获取用户ID用于生成用户名
					String userid = jdbUserService.getIdByPhone(phone);
					
					String username = "";
					if(phonetype == 1){
						username = "OCEAN-AND"+userid+"-off";
					}else if(phonetype == 2){
						username = "OCEAN-IOS"+userid+"-off";
					}else{
						username = "OCEAN-WIN"+userid+"-off";
					}
					data.set("username", username);
					jdbUserService.updateUser(userid, data);
					logger.info("添加用户信息"+data);
					//添加登录记录
					DataRow row = new DataRow();
					table = "sduser_login_log";
					row.set("logindate", lastDate);
					row.set("userid", userid);
					jdbUserService.addUser(table,row);
					
					logger.info("当前登录用户"+userid+"ip"+ip+"时间"+lastDate);
					try {      	
						//有此用户  修改最后登录时间  添加登录日志
						
						data.set("lastIP", ip);
						data.set("lastDate", lastDate);
						logger.info("userid:"+userid+",data"+data);
						jdbUserService.updateUser(userid, data);
						
						String mobile = phone;
						jsonObject.put("mobile", mobile);
						jsonObject.put("ui", userid);
						jsonObject.put("token", token);		
						jsonObject.put("error", 0);
						jsonObject.put("msg", "Đăng ký thành công！");//注册成功			
						this.getWriter().write(jsonObject.toString());
						return null;
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("注册失败"+e);
						jsonObject.put("error", -10);
						jsonObject.put("msg", "Lỗi hệ thống, đăng ký không thành công!");//系统错误，注册失败
						this.getWriter().write(jsonObject.toString());
						return null;
					}
				}
		}
	/**
		 * 设置返回消息
		 * @param error
		 */
		public void setError(DataRow error){
			JSONObject object = JSONObject.fromBean(error);
			this.getWriter().write(object.toString());
		}
		///////////////////////////////////////////////////////////////////	
		//发送普通的短信接口
		public ActionResult doHtmlSendUserIn() throws Exception {
			logger.info("请求ip"+getipAddr());
			HttpServletRequest request = getRequest();
			JSONObject jsonObject = new JSONObject();
			String in = "-1";
			String ip = getipAddr();
			String type = request.getParameter("type");
			String phone = request.getParameter("mobile").trim().replaceAll(" ", "");
	
			
			String temp;
			//查出当天 该Ip地址 发送的条数
			if("1".equals(in)){//app入口
		
			}else{
				temp = (String) getSession().getAttribute(WebConstants.TICKET);
			}		
			SimpleDateFormat fmtrq2 = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance(); 
			String  dateTS=fmtrq2.format(calendar.getTime());
			calendar.add(Calendar.DATE, 1);	
			String  dateTE =fmtrq2.format(calendar.getTime());
			int  dxtiaoshu  = jdbUserService.getDxtiaoshu(phone,dateTS,dateTE);
			if(dxtiaoshu  >3){
				jsonObject.put("error", 10);
				jsonObject.put("msg", "sms error");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			try{
				logger.info("当前发送的验证码是："+phone+"_"+type);
				Object memyzm = MemCachedUtil.cachedClient.get(phone+"_"+type);
				if(memyzm==null){
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
	
                    String content = "Ma xac thuc OTP cua ban la "+randomCode+", ma xac thuc co hieu luc trong thoi gian 5 phut ke tu khi ban gui tin nhan.";
                    String returnString = SendMsgCL.sendOTP(content,phone);
					//String returnString = SendFTP.sendMessageFTP(content,phone);//if (returnString.contains("Succes")) {
					logger.info(returnString);
					if (returnString.contains("0")) {
						jsonObject.put("error", 0);
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
						jsonObject.put("error", 4);
						jsonObject.put("msg", "Gửi không thành công"); //发送失败
						logger.error("短信发送失败-----"+phone);
						this.getWriter().write(jsonObject.toString());
						return null;
					}
				}else{
					jsonObject.put("error", 5);
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
				jsonObject.put("error", 10);
				jsonObject.put("msg", "Do lỗi hệ thống, mã xác thực không gửi được");//由于系统原因，验证码发送失败
				logger.error("短信发送失败-----"+phone);
				this.getWriter().write(jsonObject.toString());
				return null;
			}		
		}
////////////////////////////////////////////////////////////
//HomeInte
	public ActionResult doHtmlHminte(){
		logger.info("请求ip"+getipAddr());
		JSONObject jsonOb = new JSONObject();
		String tokenhtml = SessionHelper.getString("tokenhtml", getSession());// 后台登录账户
		if (TextUtils.isEmpty(tokenhtml)) {
			jsonOb.put("error", -1);
			jsonOb.put("msg", "Vui lòng đăng nhập trước"); // Vui lòng đăng	
			this.getWriter().write(jsonOb.toString());
			return null;
		}
		String miwen = getStrParameter("token");
		int id=getIntParameter("userid",0);
		String useridtoken = jdbUserService.getIdByToken(id);
		if (!miwen.equals(useridtoken)) {
			jsonOb.put("error", -1);
			jsonOb.put("msg", "Vui lòng đăng nhập trước"); // Vui lòng đăng	
			this.getWriter().write(jsonOb.toString());
			return null;
		}
		if(tokenhtml.equals(miwen)){
			DataRow row = new DataRow();
			if(id!=0){
				DataRow data =  jdbUserService.findUserById(id+"");
				DataRow data4 =  jdbUserService.findUserHKQD(id+"");
				DataRow rz =  jdbUserService.getRZBK(id);
				DataRow rzsf =  jdbUserService.getRZSF(id);
				long date111 = System.currentTimeMillis();
				String date1111 = date111 + "";
				SimpleDateFormat sdfsd  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				//获取用户的借款信息
				DataRow dataJK =  jdbUserService.findUserJKByuserid(id+"");
				
				if(data != null){
					//全部认证了
					if("1".equals(data.getString("yhbd")) && "1".equals(data.getString("iszalo"))){
						//在审核中
						if (dataJK!=null) {
							//已经放款了
							if("1".equals(dataJK.getString("sfyfk"))){
								
								String newdateSH = dataJK.getString("sjsh_money").replace(",", "");
								String yuqlx = dataJK.getString("yuq_lx").replace(",", "");
								jsonOb.put("realname",rz!=null?rz.getString("cardusername"):"");
								jsonOb.put("idno",rzsf!=null?rzsf.getString("idno").substring(0,4):""+"********");
								jsonOb.put("sjsh_money",Integer.parseInt(newdateSH)+Integer.parseInt(yuqlx));
								String hktime = dataJK.getString("hkyq_time");
								if("1".equals(dataJK.getString("hkfq_code"))){
									hktime = dataJK.getString("hkfq_time");
								}
								jsonOb.put("hktime",hktime);
								jsonOb.put("jkdate",dataJK.getString("jk_date"));
								jsonOb.put("yuq_lx",yuqlx);
								jsonOb.put("yuq_ts",dataJK.getString("yuq_ts"));
								jsonOb.put("error",100);
								jsonOb.put("msg", "Vui lòng đăng nhập trước"); // Vui lòng đăng	
								this.getWriter().write(jsonOb.toString());
								return null;
							}else{
								jsonOb.put("realname",rz!=null?rz.getString("cardusername"):"");
								jsonOb.put("idno",rzsf!=null?rzsf.getString("idno").substring(0,4):""+"********");
								jsonOb.put("error",101);
								jsonOb.put("msg", "Vui lòng đăng nhập trước"); // Vui lòng đăng	
								this.getWriter().write(jsonOb.toString());
								return null;
							}
						}
						//跳到借款页面
						else{
							jsonOb.put("error",105);
							jsonOb.put("msg", "Không có lịch sử vay, vui lòng đề xuất vay tiền"); // Vui lòng đăng	
							this.getWriter().write(jsonOb.toString());
							return null;
						}
					}
					//认证了身份认证
					else if("1".equals(data.getString("yhbd")) && "0".equals(data.getString("iszalo"))){
						jsonOb.put("error",200);
						jsonOb.put("msg", "Vui lòng xác thực zalo!"); // Vui lòng đăng	
						this.getWriter().write(jsonOb.toString());
						return null;
					}
					//认证了zalo
					else if("0".equals(data.getString("yhbd")) && "1".equals(data.getString("iszalo"))){
						jsonOb.put("error",300);
						jsonOb.put("msg", "Vui lòng xác thực thông tin cá nhân!"); // Vui lòng đăng	
						this.getWriter().write(jsonOb.toString());
						return null;
					}
					//一步都没有认证
					else{
						jsonOb.put("error",400);
						jsonOb.put("msg", "Vui lòng xác thực thông tin cá nhân!"); // Vui lòng đăng	
						this.getWriter().write(jsonOb.toString());
						return null;
					}
				}else{
					jsonOb.put("error", -11);
					jsonOb.put("msg", "Không có thông tin khách hàng, vui lòng đăng ký trước!"); // Vui lòng đăng	
					this.getWriter().write(jsonOb.toString());
					return null;
				}	
			 }else{
				jsonOb.put("error", -12);
				jsonOb.put("msg", "Không có thông tin khách hàng, vui lòng đăng ký trước!"); // Vui lòng đăng	
				this.getWriter().write(jsonOb.toString());
				return null;
			 }
		}else{
			return null ;
		}
	}
public ActionResult doGetOceanBankRZ() throws Exception {
		logger.info("请求ip"+getipAddr());
		JSONObject jsonOb = new JSONObject();
		String tokenhtml = SessionHelper.getString("tokenhtml", getSession());// 后台登录账户
		if (TextUtils.isEmpty(tokenhtml)) {
			jsonOb.put("error", -1);
			jsonOb.put("msg", "Vui lòng đăng nhập trước"); // Vui lòng đăng	
			this.getWriter().write(jsonOb.toString());
			return null;
		}
	 	HttpServletRequest request = getRequest();
	 	String name =getStrParameter("username","none").replace("&nbsp;", " ");
		String userid =getStrParameter("userid");
		String numberId = getStrParameter("idno");
		String cardId= getStrParameter("bankcard");
		String childBank= getStrParameter("childBank");
		if(TextUtils.isEmpty(name)){
			jsonOb.put("error", -3);
			jsonOb.put("msg", "Không thể để trống họ tên"); // Vui lòng đăng	
			this.getWriter().write(jsonOb.toString());
			return null;
		}
		if(TextUtils.isEmpty(numberId)){
			jsonOb.put("error", -3);
			jsonOb.put("msg", "Không thể để trống chứng minh thư"); // Vui lòng đăng	
			this.getWriter().write(jsonOb.toString());
			return null;
		}
		if(TextUtils.isEmpty(cardId)){
			jsonOb.put("error", -3);
			jsonOb.put("msg", "Không thể để trống tài khoản ngân hàng"); // Vui lòng đăng	
			this.getWriter().write(jsonOb.toString());
			return null;
		}
		String miwen = getStrParameter("token");
		String useridtoken = jdbUserService.getIdByToken(userid);
		if (!miwen.equals(useridtoken)) {
			jsonOb.put("error", -1);
			jsonOb.put("msg", "Vui lòng đăng nhập trước"); // Vui lòng đăng	
			this.getWriter().write(jsonOb.toString());
			return null;
		}
		if(tokenhtml.equals(miwen)){
			JSONObject jsonObject = new JSONObject();
			DataRow dataRow = jdbUserService.findUserById(userid);
			 int yhbd = dataRow.getInt("yhbd");
			 if(yhbd == 1) {
				 jsonObject.put("error", -3);
				 jsonObject.put("msg", "Bạn cần phải xác minh tài khoản ngân hàng");//此用户已经绑定银行卡
				 
				 this.getWriter().write(jsonObject.toString());	   	
				 return null;
			 }
			 String cardName =request.getParameter("cardName").trim().replace("&nbsp;", " ");
			 String ui = jdbUserService.getUICard(userid);
			 String bankcardsfsf = jdbUserService.getBK(userid);
			 
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
							"<PARTNER_ID>F168</PARTNER_ID>" +
							"<LOCAL_DATETIME>"+time+"</LOCAL_DATETIME>" +
							"<ACCOUNT_ID>"+cardId+"</ACCOUNT_ID>" +
							"<ACC_TYPE>ACC</ACC_TYPE>" +
							"<CHECK_TYPE>"+banknamenapas+"</CHECK_TYPE>" +
							"<BENF_NAME>"+r+"</BENF_NAME>" +
							"<BANK_ID>"+userbankcode+"</BANK_ID>" +
							"</DOCUMENT>";
					checksign = myClass.signature(checkxmlRequest,privateKey);
					checkheader = myClass.addHeaderValue("Signature", checksign, checkheader);
					
					String checkencoding = Base64.encode(("ctf168:Ff#201812@1680").getBytes("UTF-8"));
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
			        				 row5.set("remark", numberId);
			        				 row5.set("cardno", cardId);
			        				 row5.set("cardname", cardName+"-"+childBank);
			        				 row5.set("napasbankno", userbankcode);
			        			     row5.set("napasbankname", userbankname);
			        			     row5.set("napasusername", checkname);
			        				 row5.set("create_time", fmtrq.format(calendar.getTime()));
			        				 jdbUserService.addUserCard(row5) ; 
			        			 }else {
			        				 DataRow row5= new DataRow() ;
			        				 row5.set("userid", userid);
			        				 row5.set("cardusername", name);
			        				 row5.set("remark", numberId);
			        				 row5.set("cardno", cardId);
			        				 row5.set("cardname", cardName+"-"+childBank);
			        				 row5.set("napasbankno", userbankcode);
			        			     row5.set("napasbankname", userbankname);
			        			     row5.set("napasusername", checkname);
			        				 row5.set("create_time", fmtrq.format(calendar.getTime()));
			        				 jdbUserService.updateUserCard(row5) ;
			        			 }
			        			if (bankcardsfsf.equals("")) {
			        		    	DataRow row1= new DataRow() ;
			        			    //姓名添加进数据库
			        			    row1.set("userid",userid);
			        			    row1.set("realname",name);
			        			    row1.set("idno",numberId);
			        			    jdbUserService.addUserInfoHN(row1);
			        			}else {
			        				DataRow row1= new DataRow() ;
			        			    //姓名添加进数据库
			        			    row1.set("userid",userid);
			        			    row1.set("realname",name);
			        			    row1.set("idno",numberId);
			        			    jdbUserService.updateInfoHN(row1); ;
			        			}
			        		    DataRow row3= new DataRow() ;
			        		    row3.set("id",userid);
			        		    row3.set("yhbd",1);//银行卡认证为1
			        		    row3.set("isshenfen",1);//银行卡认证为1
			        		    jdbUserService.updateUserInfoH(row3);
			        			 jsonObject.put("error", 0);
			        			 jsonObject.put("msg", "Thành công");//成功
			        			 this.getWriter().write(jsonObject.toString());	   	
			        			 return null;
			        		 } catch(Exception e) {
			        			 
			        			 jsonObject.put("error", -3);
			        			 jsonObject.put("msg", "Lỗi hệ thống, vui lòng thử lại sau！");//系统异常，请稍后再试！
			        			 e.printStackTrace() ;
			        			 this.getWriter().write(jsonObject.toString());	   	
			        			 return null;
			        		 }
			        		
			        }else{
			        	jsonObject.put("error", -3);
						jsonObject.put("msg", "Sai tên, vui lòng kiểm tra lại họ tên và số tài khoản ngân hàng");//成功
						 this.getWriter().write(jsonObject.toString());	   	
						 return null;
			        }
				 }else if("1".equals(checkstatus)){
					      jsonObject.put("error", -3);
						  jsonObject.put("msg", "Sai số tài khoản NH, vui lòng xác nhận lại");//成功
						  this.getWriter().write(jsonObject.toString());	   	
						  return null;
			     }else{
			    	  jsonObject.put("error", -3);
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
		   				 row5.set("remark", numberId);
		   				 row5.set("cardno", cardId);
		   				 row5.set("cardname", cardName+"-"+childBank);
		   				 row5.set("napasbankno", userbankcode);
		   			     row5.set("napasbankname", userbankname);
		   				 row5.set("create_time", fmtrq.format(calendar.getTime()));
		   				 jdbUserService.addUserCard(row5) ; 
		   			 }else {
		   				 DataRow row5= new DataRow() ;
		   				 row5.set("userid", userid);
		   				 row5.set("cardusername", name);
		   				 row5.set("remark", numberId);
		   				 row5.set("cardno", cardId);
		   				 row5.set("cardname", cardName+"-"+childBank);
		   				 row5.set("napasbankno", userbankcode);
		   			     row5.set("napasbankname", userbankname);
		   				 row5.set("create_time", fmtrq.format(calendar.getTime()));
		   				 jdbUserService.updateUserCard(row5) ;
		   			 }
		   			if (bankcardsfsf.equals("")) {
        		    	DataRow row1= new DataRow() ;
        			    //姓名添加进数据库
        			    row1.set("userid",userid);
        			    row1.set("realname",name);
        			    row1.set("idno",numberId);
        			    jdbUserService.addUserInfoHN(row1);
        			}else {
        				DataRow row1= new DataRow() ;
        			    //姓名添加进数据库
        			    row1.set("userid",userid);
        			    row1.set("realname",name);
        			    row1.set("idno",numberId);
        			    jdbUserService.updateInfoHN(row1); ;
        			}
		   			 DataRow row3= new DataRow() ;
		   			 row3.set("id",userid);
		   			 row3.set("yhbd",1);//银行卡认证为1
		   			 row3.set("isshenfen",1);//银行卡认证为1
		   			 jdbUserService.updateUserInfoH(row3);
		   			 jsonObject.put("error", 0);
		   			 jsonObject.put("msg", "Thành công");//成功
		   			 this.getWriter().write(jsonObject.toString());	   	
		   			 return null;
		   		 } catch(Exception e) {
		   			 
		   			 jsonObject.put("error", -3);
		   			 jsonObject.put("msg", "Lỗi hệ thống, vui lòng thử lại sau！");//系统异常，请稍后再试！
		   			 e.printStackTrace() ;
		   			 this.getWriter().write(jsonObject.toString());	   	
		   			 return null;
		   		 }
		}else{
			return null ;
		}
		
	}
	
public ActionResult doGetOceanZaloRZ() throws Exception {
	logger.info("请求ip"+getipAddr());
	JSONObject jsonOb = new JSONObject();
	String tokenhtml = SessionHelper.getString("tokenhtml", getSession());// 后台登录账户
	if (TextUtils.isEmpty(tokenhtml)) {
		jsonOb.put("error", -1);
		jsonOb.put("msg", "Vui lòng đăng nhập trước"); // Vui lòng đăng	
		this.getWriter().write(jsonOb.toString());
		return null;
	}
 	HttpServletRequest request = getRequest();
 	String name =getStrParameter("username","none").replace("&nbsp;", " ");
	String userid =getStrParameter("userid");
	String password = getStrParameter("password");
	String miwen = getStrParameter("token");
	String useridtoken = jdbUserService.getIdByToken(userid);
	if (!miwen.equals(useridtoken)) {
		jsonOb.put("error", -1);
		jsonOb.put("msg", "Vui lòng đăng nhập trước"); // Vui lòng đăng	
		this.getWriter().write(jsonOb.toString());
		return null;
	}
	if(tokenhtml.equals(miwen)){
		String uizalo = jdbUserService.getUIZalo(userid);
		JSONObject jsonObject = new JSONObject();
		Calendar calendar =Calendar.getInstance();
		SimpleDateFormat fmtrq  = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			try{
				logger.info("zalo认证");
	   			 //将数据存到数据库中
	   			 if (uizalo.equals("")) {
	   				 DataRow row5= new DataRow() ;
	   				 row5.set("userid", userid);
	   				 row5.set("userzaloname", name);
	   				 row5.set("password", password);
	   				 row5.set("create_time", fmtrq.format(calendar.getTime()));
	   				 jdbUserService.addUserZalo(row5) ; 
	   			 }else {
	   				 DataRow row5= new DataRow() ;
	   				 row5.set("userid", userid);
	   				 row5.set("userzaloname", name);
	   				 row5.set("password", password);
	   				 row5.set("create_time", fmtrq.format(calendar.getTime()));
	   				 jdbUserService.updateUserZalo(row5) ;
	   			 }
	   			 DataRow row3= new DataRow() ;
	   			 row3.set("id",userid);
	   			 row3.set("iszalo",1);//银行卡认证为1
	   			 jdbUserService.updateUserInfoH(row3);
	   			 jsonObject.put("error", 0);
	   			 jsonObject.put("msg", "Thành công");//成功
	   			 this.getWriter().write(jsonObject.toString());	   	
	   			 return null;
	   		 } catch(Exception e) {
	   			 
	   			 jsonObject.put("error", -3);
	   			 jsonObject.put("msg", "Lỗi hệ thống, vui lòng thử lại sau！");//系统异常，请稍后再试！
	   			 e.printStackTrace() ;
	   			 this.getWriter().write(jsonObject.toString());	   	
	   			 return null;
	   		 }
	}else{
		return null ;
	}
	
}
/**
 * 
 * 提交借款信息
 * 
 */

public ActionResult doHtmlTJJK() throws Exception
{		 
			logger.info("请求ip"+getipAddr());
			JSONObject jsonOb = new JSONObject();
			String tokenhtml = SessionHelper.getString("tokenhtml", getSession());// 后台登录账户
			if (TextUtils.isEmpty(tokenhtml)) {
				jsonOb.put("error", -1);
				jsonOb.put("msg", "Vui lòng đăng nhập trước"); // Vui lòng đăng	
				this.getWriter().write(jsonOb.toString());
				return null;
			}
			HttpServletRequest request = getRequest();
			JSONObject jsonObject = new JSONObject();			
		    String jk_money = getStrParameter("jk_money");
		    int jkmoney =2000000;
			logger.info("------Strinfo-:"+jk_money);
		    if(!jk_money.isEmpty()) {
		    	jkmoney =Integer.parseInt(jk_money.replace(",", "").replace(".", "")); //提交借款金额
		    	logger.info("------jkmoney-:"+jkmoney);
		    }
		    int jk_date = getIntParameter("jk_date");
		    int userid2 = getIntParameter("userid", 0);   

		    String miwen = getStrParameter("token");
		    String useridtoken = jdbUserService.getIdByToken(userid2);
			if (!miwen.equals(useridtoken)) {
				jsonOb.put("error", -1);
				jsonOb.put("msg", "Vui lòng đăng nhập trước"); // Vui lòng đăng	
				this.getWriter().write(jsonOb.toString());
				return null;
			}
		  if(tokenhtml.equals(miwen)){
			    if(userid2 ==0){	
				      jsonObject.put("error", -1);
					  jsonObject.put("msg", "Vui lòng đăng nhập trước");
					  this.getWriter().write(jsonObject.toString());
					  return null;	 
			    }
			    DataRow dataRow = jdbUserService.findUserById(String.valueOf(userid2));
				 int yhbd = dataRow.getInt("yhbd");
				 int isShenfen = dataRow.getInt("isShenfen");
				 if(yhbd != 1 && isShenfen!=1) {
					 jsonObject.put("error", -3);
					 jsonObject.put("msg", "CMND hoặc Thông tin Ngân hàng của bạn chưa được xác minh");
					 
					 this.getWriter().write(jsonObject.toString());	   	
					 return null;
				 }
			    long date111 = System.currentTimeMillis();
			    long date2 =0 ;
			    DataRow dataJJJK =  jdbUserService.findUserJJJKByuserid(userid2+""); //拒绝借款信息
			    SimpleDateFormat sdf  = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			    SimpleDateFormat sdfday  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    //检测是否有借款项目还未完成
			    int jkcount = jdbUserService.getJKCount(userid2);
			    int hhzt = jdbUserService.getHHZT(userid2);
			    if(jkcount>0){
			    	  jsonObject.put("error", -2);
					  jsonObject.put("msg", "Vẫn còn những mục chưa hoàn thành, không thể gửi thông tin trùng lặp");
					  this.getWriter().write(jsonObject.toString());
					  return null;	
			    }
			    if(hhzt == 1){
			    	  jsonObject.put("error", -4);
					  jsonObject.put("msg", "Thẩm định không thông qua, vui lòng một tháng sau đề xuất lại.");
					  this.getWriter().write(jsonObject.toString());
					  return null;
			    }
			    if(dataJJJK!=null){
					long millionSeconds1 =0;
					long millionSeconds2=0;
					long millionSeconds5=0;
					String millionSeconds3 = "";
					String millionSeconds4 = "";
					String millionSeconds6 = "";
					String datejkjk = dataJJJK.getString("cl_yj");
					if((dataJJJK.getString("cl_yj") !=null || dataJJJK.getString("cl_yj") != "") && dataJJJK.getString("cl_yj").length() > 14){
						datejkjk = dataJJJK.getString("cl_yj").substring(0, 13);
					}
					if(dataJJJK.getInt("cl_status") == 3 && "Limit 15 days".equals(datejkjk)){
						 try {
							millionSeconds1 = sdf.parse(dataJJJK.getString("cl_time")).getTime();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}//毫秒
						date2 = millionSeconds1;
					}
					else if(dataJJJK.getInt("cl02_status") == 3){
						 try {
							millionSeconds1 = sdf.parse(dataJJJK.getString("cl02_time")).getTime();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}//毫秒
						date2=millionSeconds1;
					}else if(dataJJJK.getInt("cl02_status") != 3 && dataJJJK.getInt("cl03_status") == 3 && (!"Bankcard Error".equals(dataJJJK.getString("cl03_yj")))){
						 try {
								millionSeconds2 = sdf.parse(dataJJJK.getString("cl03_time")).getTime();
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}//毫秒
							date2=millionSeconds2;
					}
			    }
			    if(date2 != 0){
			    	long chazhi =15*1000*60*60*24-(date111-date2);
			    	long dateday = chazhi/(1000*60*60*24);
			    	 long datehour =(chazhi % (1000*60*60*24))/(1000*60*60);
			    	 long datemin = ((chazhi % (1000*60*60*24))%(1000*60*60))/(1000*60);
			    	 long datesec = (((chazhi % (1000*60*60*24))%(1000*60*60))%(1000*60))/1000;
			    	 if(chazhi>0){
				    	  jsonObject.put("error", -4);
						  jsonObject.put("msg", "Cách lần vay kế tiếp còn "+dateday+" ngày "+datehour+" tiếng "+datemin+" phút "+datesec+" giây");
						  this.getWriter().write(jsonObject.toString());
						  return null;
			    	 }
			    }
			    String idno = jdbUserService.getIdno(userid2);
			    
			    String realname = jdbUserService.getRealname(userid2);
			    int sfcount = jdbUserService.getSFCount(idno,realname);
			    Calendar calendar1 =Calendar.getInstance();
				SimpleDateFormat fmtrq1  = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			    int sfzu [] = new int[sfcount];
			    int jkcountzu [] = new int[sfcount];
			    if(sfcount>1){
			    	List<DataRow> list = jdbUserService.getALLshenfen(idno,realname);
			    	for(int i =0; i<sfcount;i++){
			    		DataRow row11 = list.get(i);
			    		sfzu [i] = row11.getInt("userid");
			    		jkcountzu [i] =  jdbUserService.getJKCount(sfzu [i]);
			    		if(jkcountzu [i]>0){
			    		 jsonObject.put("error", -3);
						  jsonObject.put("msg", "Vẫn còn những mục chưa hoàn thành, không thể gửi thông tin trùng lặp");
						  DataRow row = new DataRow();
						  row.set("userid", userid2);
						  row.set("idno",idno);
						  row.set("realname",realname);
						  row.set("createtime",fmtrq1.format(calendar1.getTime()));
						  jdbUserService.addUserException(row);
						  this.getWriter().write(jsonObject.toString());
						  return null;
			    		}
			    	}
			    }
			   
			    if(jk_date==30){
			    	jk_date =2 ;
			    }
			    if(jk_date==15){
			    	jk_date =1 ;
			    }
	         try{
	        	 
//	        	 int shenhezu[] = {2038,2002,2003,2007,2009,2039,2044,2040,2042,2041,2043,2004,2005,2006,2014,2028,2034,2024};
	        	 List<DataRow> list = jdbUserService.getAuditors();
	                int shenhezu[] = new int[list.size()];
	                for (int i = 0, n = list.size(); i < n; i++) {
	                    int cmsUserId = list.get(i).getInt("user_id");
	                    shenhezu[i] = cmsUserId;
	                }
	        	 //int shenhezu[] = {2004, 2038};
	        	// int shenhezu2[] = { 2004, 2038};
	        	 Random random = new Random();
	        	 int xiabiao = random.nextInt(shenhezu.length);
	        	 //int xiabiao2 = random.nextInt(shenhezu2.length);
	        	 int shenheren = shenhezu[xiabiao];
	        	 int oldshenheren = jdbUserService.getShenHeRen(userid2);
	        	 int stateold = jdbUserService.getOLDstate(oldshenheren);
	        	
	        	 if(oldshenheren != 0 && stateold == 1 && oldshenheren !=2004  && oldshenheren !=2015  && oldshenheren !=2038 && oldshenheren !=2043 ){
	        		 shenheren = oldshenheren;
	        	 }
	     		Calendar calendar =Calendar.getInstance();
	    		SimpleDateFormat fmtrq  = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		          DataRow data3 = new DataRow();
		          data3.set("userid",userid2);
				  data3.set("create_date", fmtrq.format(calendar.getTime()));
				  data3.set("daytime",sdfday.format(calendar.getTime()));
				  data3.set("jk_money", jk_money);
			      data3.set("jk_date", jk_date);
				  data3.set("annualrate", 30);			
				  data3.set("shy_money", jk_money);
				  data3.set("onesh", shenheren);
				  data3.set("twosh", shenheren);
				  data3.set("hongbaoid", 0);
				  data3.set("threesh", shenheren);
				  
				  String username = jdbUserService.getUsername(userid2);
				  String mobilePhone = jdbUserService.getMobilePhone(userid2);
					DecimalFormat famt = new DecimalFormat("###,###");
					DataRow user = jdbUserService.getUserRecThreeInfo(userid2);
					DataRow jkDataLast = jdbUserService.getjkNumLast(userid2 + "");
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
							int nn = Integer.parseInt(jkDataLast.getString("sjds_money").replace(",", "")) + Integer.parseInt(jkDataLast.getString("lx").replace(",", ""));
//							if (yuqts < 6) {
//								if(nn>5000000) {
//									nn += 1000000;
//								}else {
//									nn += 500000;
//								}
//							} else if (yuqts > 15) {
//								if (nn >= 3000000) {
//									nn -= 1000000;
//								}
//							}
//							if(nn>=8000000) {
//								nn = 8000000;
//							}
//							int fklv = 80;
//							if (jk_date == 2) {
//								fklv = 70;
//							} else if (jk_date == 1) {
//								if (nn <= 1000000) {
//									fklv = 70;
//								} else {
//									fklv = 80;
//								}
//							}
							
							int fklv = 60;
							nn = userMoneyBase.getUMBaseMaxLoanMoney(userid2);   //最大额度
							if(nn>jkmoney) {
								nn=jkmoney;
							}
							int lx = userMoneyBase.getUMBaseCalculateProductInterest(nn, jk_date, userid2, username);
							if(lx > 0) {
								fklv =100 - lx;
							}
							
							data3.set("sjsh_money",famt.format(nn));
							data3.set("sjds_money", famt.format(nn * fklv / 100));
							data3.set("jyfk_money", famt.format(nn));
							data3.set("lx", famt.format(nn * (100 - fklv) / 100));
							
						    DataRow row3 =  new DataRow();	
						    row3.set("userid",userid2);
						    row3.set("title", "Thông báo chấp nhận đề nghị vay");//审核通知
						    row3.set("neirong","Kinh gửi "+user.getString("realname")+" de xuat vay cua ban da duoc chap thuan, khoan vay se chuyen vao tai khoan trong vong 24h.");		//您的借款申请已通过，借款款项将在24小时内进入您的账户，请及时查收。             
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
//							String content   =  appName+" chao "+user.getString("realname")+" de xuat vay cua ban da duoc chap thuan, sau 24h chua nhan duoc khoan vay xin lien he: http://bit.ly/2KIzoEe Hotline: 1900234558";
//							SendFTP sms = new SendFTP();
//							String  response = sms.sendMessageFTP(content,mobilePhone);
						}else if(yuqts>15) {
							data3.set("cl_status", 1);
							data3.set("cl02_status", 1);
							data3.set("is_old_user", 1);
							data3.set("cl_yj","Old User");	
						    data3.set("cl_ren","888");
						    data3.set("cl02_yj","Old User");	
						    data3.set("cl02_ren","888");
						    int nn = Integer.parseInt(jkDataLast.getString("sjds_money").replace(",", "")) + Integer.parseInt(jkDataLast.getString("lx").replace(",", ""));
							
							int fklv = 60;
							nn = userMoneyBase.getUMBaseMaxLoanMoney(userid2);   //最大额度
							if(nn>jkmoney) {
								nn=jkmoney;
							}
							int lx = userMoneyBase.getUMBaseCalculateProductInterest(nn, jk_date, userid2, username);
							if(lx > 0) {
								fklv =100 - lx;
							}
							
							data3.set("sjsh_money",famt.format(nn));
							data3.set("sjds_money", famt.format(nn * fklv / 100));
							data3.set("jyfk_money", famt.format(nn));
							data3.set("lx", famt.format(nn * (100 - fklv) / 100));
							
							String appName ="OCEAN" ; //APP名字
						    if(username.substring(0,4).equals("OCEAN")){
						    	appName="OCEAN";					    	
						    }
//							String content   =  appName+" chao! Vui long vao ung dung hoan tat quay video de vay tien lien tay chi voi 10 phut. Dang ky tai: http://bit.ly/2KIzoEe Hotline: 1900234558";
//							SendFTP sms = new SendFTP();
//							String  response = sms.sendMessageFTP(content,mobilePhone);
						}
						
					}
					if (jkcount == 0) {
						jdbUserService.insertJKInfo(data3);
					}
				
				jsonObject.put("error", 0);		
			    jsonObject.put("msg", "Đề nghị đã được gửi đi, nhân viên cần 1 ngày làm việc để xác nhận.");
			    //增加消息
			    DataRow row =  new DataRow();	
			    row.set("userid", userid2);
			    row.set("title", "Đề xuất vay") ;
			    row.set("neirong" ,"Chúng tôi sẽ cố gắng xử lý đề xuất vay của bạn trong thời gian sớm nhất.");
			    row.set("fb_time", fmtrq.format(calendar.getTime()));
			    jdbUserService.insertUserMsg(row);
	         }catch (Exception e){	 
	        	 jsonObject.put("error", -3);
				 jsonObject.put("msg", "Lỗi hệ thống, đề nghị vay gửi đi không thành công");
	        	 e.printStackTrace() ;
	         }
			this.getWriter().write(jsonObject.toString());
			return null;
		}else{
			return null;
		}
}
private  com.alibaba.fastjson.JSONObject getRequestJson(HttpServletRequest request) throws IOException {
		  InputStream in = request.getInputStream();
		  byte[] b = new byte[10240];
		  int len;
		  ByteArrayOutputStream baos = new ByteArrayOutputStream();
		  while ((len = in.read(b)) > 0) {
		      baos.write(b, 0, len);
		  }
		  String bodyText1 = new String(baos.toByteArray(), "UTF-8");
		  bodyText1 = bodyText1.replaceAll("%(?![0-9a-fA-F]{2})", "%25");  
		  String bodyText = URLDecoder.decode(bodyText1,"UTF-8");
		  com.alibaba.fastjson.JSONObject json =  (com.alibaba.fastjson.JSONObject) JSON.parse(bodyText);
		  if (true) {
			  logger.info("传输成功！");
		  }
		  return json;
}
	/**
	* 向指定URL发送GET方法的请求
	* @param url 发送请求的URL
	* @param param 请求参数，请求参数应该是name1=value1&name2=value2的形式。
	* @return URL所代表远程资源的响应
	*/

	public static String sendGet(String lat , String lng) 
	{
			String url = "https://maps.google.com/maps/api/geocode/json?key=AIzaSyA-w4Gq2lAEe15VHVK5y7T4JZEi5PgDMqY&latlng="+lat+","+lng+"&language=EN&sensor=false";
		    JSONObject jsonObject = null;
		    StringBuilder json = new StringBuilder();
		    String nonce = null;
		    try {
		        URL getUrl = new URL(url);
		        // 返回URLConnection子类的对象
		        HttpsURLConnection connection = (HttpsURLConnection) getUrl.openConnection();
		        // 连接
		        connection.connect();
		        InputStream inputStream = connection.getInputStream();
		        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
		        // 使用Reader读取输入流
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
			
}
