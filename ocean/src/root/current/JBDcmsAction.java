package root.current;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;

//import com.aliyun.oss.OSSClient;
import com.project.constant.IConstants;
import com.project.service.account.JBDUserService;
import com.project.service.account.JBDcms2Service;
import com.project.service.account.JBDcmsService;
import com.shove.security.Encrypt;
import com.sun.org.apache.bcel.internal.generic.I2F;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import com.thinkive.base.jdbc.DBPage;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.util.SessionHelper;
import com.thinkive.base.util.StringHelper;
import com.thinkive.web.base.ActionResult;
import com.thinkive.web.base.BaseAction;
import root.SendFTP;

import net.sf.json.JSONObject;
import root.SendMsg;
//import cn.jiguang.common.ClientConfig;
//import cn.jiguang.common.resp.APIConnectionException;
//import cn.jiguang.common.resp.APIRequestException;
//import cn.jpush.api.JPushClient;
//import cn.jpush.api.push.PushResult;
//import cn.jpush.api.push.model.Options;
//import cn.jpush.api.push.model.Platform;
//import cn.jpush.api.push.model.PushPayload;
//import cn.jpush.api.push.model.audience.Audience;
//import cn.jpush.api.push.model.notification.Notification;
import root.order.UserMoneyBase;

public class JBDcmsAction extends BaseAction {

	private static Logger logger = Logger.getLogger(JBDcmsAction.class);
	private static JBDcmsService jbdcmsService = new JBDcmsService();
	private static JBDcms2Service jbdcms2Service = new JBDcms2Service();
	private static JBDUserService jbdUserService = new JBDUserService();
	private static UserMoneyBase  userMoneyBase = new UserMoneyBase();
	private static AccessVerifivationBase accessVeritifivationbase = new AccessVerifivationBase();
	String jiami = "G0eHIW3op8dYIWsdsdeqFSDeafhklRG";

	public static long sendCount = 0;
	long time = 1000 * 60 * 3;
	
/**********************************xiong-start*****************************************/
		//xiong-取消失联名单-20190613
		public ActionResult doCheXiaoSlmd() throws Exception {
			  JSONObject jsonObject = new JSONObject();
			  int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
			  cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
			    if(cmsuserid==0){
						jsonObject.put("error", -1);
						jsonObject.put("msg", "Vui lòng đăng nhập trước");
						this.getWriter().write(jsonObject.toString());	
						return null;		
				}
				logger.info("请求ID:" + cmsuserid);
				
			  int id = getIntParameter("id");
			  int userid = getIntParameter("userid");
			  SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			  
			  DataRow row1 = new DataRow();
			  row1.set("id", id);
			  row1.set("state",0);
			  row1.set("cxtime", fmtrq.format(new Date()));
			  jbdcmsService.updateUserSlmd(row1);
			
			  jsonObject.put("error", 1);
			  jsonObject.put("msg", "成功！");
			  this.getWriter().write(jsonObject.toString());
			  return null;
			 }
	
	//xiong-失联名单列表-20190613
		public ActionResult doGetAllUserSlmd() throws Exception {
			  JSONObject jsonObject = new JSONObject();
			  int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
			  cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
			    if(cmsuserid==0){
						jsonObject.put("error", -1);
						jsonObject.put("msg", "Vui lòng đăng nhập trước");
						this.getWriter().write(jsonObject.toString());	
						return null;		
				}
				logger.info("请求ID:" + cmsuserid);
				
			  int temp = getIntParameter("temp", 0);
			  String tempVelue = getStrParameter("tempvl");
			  
			  String startDate1 = getStrParameter("startDate");
			  String endDate1 = getStrParameter("endDate");
			  String startDate = "";
			  String endDate = "";
			  if (!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)) {
			   String[] sourceStrArray1 = startDate1.split("-");
			   String[] sourceStrArray2 = endDate1.split("-");
			   startDate = sourceStrArray1[2] + "-" + sourceStrArray1[1] + "-"
			     + sourceStrArray1[0];
			   endDate = sourceStrArray2[2] + "-" + sourceStrArray2[1] + "-"
			     + sourceStrArray2[0];
			  }
			  
			  // 定义用户选择条件
			  String userId = "";
			  String jkid = "";
			  
			  if (temp == 1) {
			   
			   userId = tempVelue;
			  }
			  
			  if (temp == 2) {
			   
			   jkid = tempVelue;
			  }
			  
			  // 默认第一页
			  int curPage = getIntParameter("curPage", 1);
			  DBPage page = jbdcmsService.getAllUserSlmd(curPage, 15, userId, jkid, startDate, endDate);
			  DataRow row = new DataRow();
			  row.set("list", page);
			  row.set("temp", temp);
			  row.set("tempvalu", tempVelue);
			  @SuppressWarnings("deprecation")
			  JSONObject object = JSONObject.fromBean(row);
			  this.getWriter().write(object.toString());
			  return null;
			 }
		
	//xiong-审核部长发送短信-20190821
	
	public ActionResult  doGetSendUserId() throws Exception{
						
		JSONObject jsonObject = new JSONObject();
		  int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
		  cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
		    if(cmsuserid==0){
					jsonObject.put("error", -1);
					jsonObject.put("msg", "Vui lòng đăng nhập trước");
					this.getWriter().write(jsonObject.toString());	
					return null;		
		  }
		 logger.info("请求ID:" + cmsuserid);
		 
		 cmsuserid = SessionHelper.getInt("cmsuserid", getSession());

		if ( cmsuserid != 8 && cmsuserid !=2038) {
			jsonObject.put("error", -1);

			this.getWriter().write(jsonObject.toString());						
			return null;
		}
		
		DataRow row = new DataRow();
		//xiong-20190821-查询所有提交借款1天没有上传视频的用户		
		List<DataRow> listSend=jbdcmsService.getSendUserId();
		//总数
		int userNum=listSend.size();
		//xiong-20190821-今天发送短信提醒上传视频的总条数
		int sendUser=jbdcmsService.getSendUserNum();
		//xiong-20190821-总共发送短信提醒上传视频的总条数
		int sendUserAll=jbdcmsService.getSendUserAllNum();
		//xiong-20190821-总过有多少发送了短信的用户上传了视频
		int sendVideo=jbdcmsService.getSendUserALLVideoNum();
		row.set("userNum", userNum);
		row.set("sendUser", sendUser);
		row.set("sendUserAll", sendUserAll);
		row.set("sendVideo", sendVideo);
		row.set("msg",1);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());
		
		return null;
	}
	
	//xiong-审核部长发送短信-20190821
	public ActionResult doSendUserId() throws Exception{
		logger.info("发送短信--开始：");
		JSONObject jsonObject = new JSONObject();
		  int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
		  cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
		    if(cmsuserid==0){
					jsonObject.put("error", -1);
					jsonObject.put("msg", "Vui lòng đăng nhập trước");
					this.getWriter().write(jsonObject.toString());	
					return null;		
			}
			logger.info("请求ID:" + cmsuserid);
			
		 cmsuserid = SessionHelper.getInt("cmsuserid", getSession());
		
		if(2038!=cmsuserid && 8!=cmsuserid) {
			jsonObject.put("error", 0);
			this.getWriter().write(jsonObject.toString());						
			return null;		
		}
		
		//xiong-20190821-今天发送短信提醒上传视频的总条数
		int sendUser=jbdcmsService.getSendUserNum();						
		//一天是只能发送一次
		if (0!=sendUser) {
			jsonObject.put("error", -1);
			this.getWriter().write(jsonObject.toString());						
			return null;
		}
		
		
	//SimpleDateFormat fmtymd  = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat fmtday = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//String curtime = fmtday.format(new Date());
				
		//xiong-20190821-查询所有提交借款1天没有上传视频的用户		
		List<DataRow> listSend=jbdcmsService.getSendUserId();
		SendFTP sendFTP = new SendFTP();
		for(int i = 0; i< listSend.size();i++) {
			DataRow dr=listSend.get(i);
			int  msgtype=3;
			String msg_com="onesms";
			int userid=dr.getInt("userid");
			int jkid=dr.getInt("id");
			int operateid=cmsuserid;
			String mobilePhone = dr.getString("mobilephone");//"0932051902 /84366665504";
			String appname = dr.getString("realname");			
			String returnString ="";
			
			DataRow ls=new DataRow();
			
			 try {
				 logger.info(userid+":发送短信--开始");
				 returnString = sendFTP.sendMessageFTP("ABCDong chuc mung ban la thanh vien VIP. Vao app quay ngay video de nhan duoc khoan vay. Lien he:http://bit.ly/2KIzoEe  Hotline: 1900234558",mobilePhone);
				  if(returnString.length()>70) {
					  returnString = returnString.substring(70,returnString.lastIndexOf("</"));
				  }  
				  logger.info(userid+":发送短信响应状态="+returnString);
			} catch (Exception e) {
				// TODO: handle exception
				 logger.info(userid+":发送短信失败");
				 throw new Exception("发送短信失败");
			}
			 DataRow stm =new DataRow();
			 stm.set("create_time", fmtday.format(new Date()));
			 stm.set("userid", userid);
			 stm.set("jkid", jkid);
			 stm.set("operateid", cmsuserid);			 
			 stm.set("msgtype", msgtype);
			 stm.set("msg_com", msg_com);
			 stm.set("msg_return", returnString);			 
			jbdcmsService.intsertUserMsg(stm); 			 
		}		
		  jsonObject.put("error", 1);
		  jsonObject.put("msg", "成功！");
		  this.getWriter().write(jsonObject.toString());
		  return null;
	}
	
	
	//xiong-黑名单列表-20190613
		public ActionResult doGetAllCSHMDList() throws Exception {
			
			  JSONObject jsonObject = new JSONObject();
			  int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
			  cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
			    if(cmsuserid==0){
						jsonObject.put("error", -1);
						jsonObject.put("msg", "Vui lòng đăng nhập trước");
						this.getWriter().write(jsonObject.toString());	
						return null;		
				}
				logger.info("请求ID:" + cmsuserid);
				
			  int temp = getIntParameter("temp", 0);
			  String tempVelue = getStrParameter("tempvl");
			  
			  String startDate1 = getStrParameter("startDate");
			  String endDate1 = getStrParameter("endDate");
			  String startDate = "";
			  String endDate = "";
			  if (!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)) {
			   String[] sourceStrArray1 = startDate1.split("-");
			   String[] sourceStrArray2 = endDate1.split("-");
			   startDate = sourceStrArray1[2] + "-" + sourceStrArray1[1] + "-"
			     + sourceStrArray1[0];
			   endDate = sourceStrArray2[2] + "-" + sourceStrArray2[1] + "-"
			     + sourceStrArray2[0];
			  }
			  
			  // 定义用户选择条件
			  String userId = "";
			  String jkid = "";
			  
			  if (temp == 1) {
			   
			   userId = tempVelue;
			  }
			  
			  if (temp == 2) {
			   
			   jkid = tempVelue;
			  }
			  
			  // 默认第一页
			  int curPage = getIntParameter("curPage", 1);
			  DBPage page = jbdcms2Service.getAllCSHMDList(curPage, 15, userId, jkid, startDate, endDate);
			  DataRow row = new DataRow();
			  row.set("list", page);
			  row.set("temp", temp);
			  row.set("tempvalu", tempVelue);
			  @SuppressWarnings("deprecation")
			  JSONObject object = JSONObject.fromBean(row);
			  this.getWriter().write(object.toString());
			  return null;
			 }
		
		//xiong-黑名单-20190613
		public ActionResult doCheXiaoHMD() throws Exception {
			JSONObject jsonObject = new JSONObject();
			  int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
			  cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
			    if(cmsuserid==0){
						jsonObject.put("error", -1);
						jsonObject.put("msg", "Vui lòng đăng nhập trước");
						this.getWriter().write(jsonObject.toString());	
						return null;		
				}
				logger.info("请求ID:" + cmsuserid);
				
			  int id = getIntParameter("id");
			  int userid = getIntParameter("userid");
			  SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			
			  DataRow row1 = new DataRow();
			  row1.set("id", id);
			  row1.set("state", cmsuserid);
			  row1.set("cxtime", fmtrq.format(new Date()));
			  jbdcmsService.updateUserInfoHMD(row1);
			  DataRow row = new DataRow();
			  row.set("id", userid);
			  row.set("heihu_zt", 0);
			  jbdcmsService.updateUserInfo(row);
			  jsonObject.put("error", 1);
			  jsonObject.put("msg", "成功！");
			  this.getWriter().write(jsonObject.toString());
			  return null;
			 }
/**********************************xiong-end*****************************************/
	public static void DelectVideo(String videoname) {
		// Endpoint以杭州为例，其它Region请按实际情况填写。
//		String endpoint = "http://oss-cn-hongkong.aliyuncs.com";
//		// 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
//		String accessKeyId = "LTAI8qRUNoC1lemw";
//		String accessKeySecret = "nS892NsXqfrglmHzMsST2SEJns6LBR";
//		String bucketName = "htmlhtml";
//		String objectName = videoname;
//		System.out.println(objectName);
//		// 创建OSSClient实例。
//		OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
//
//		// 删除文件。
//		ossClient.deleteObject(bucketName, objectName);
//
//		// 关闭OSSClient。
//		ossClient.shutdown();
//		logger.info("视频删除成功");
	}
	/**********************************xiong-end*****************************************/

	/**
	 * 获得客户的真实IP地址
	 * 
	 * @return
	 */
	public String getipAddr() {
		String online =SessionHelper.getString("online", getSession());
		return online;
	}

	/**
	 * 后台管理员登录请求
	 * 
	 * @return
	 * @throws Exception
	 */
	public ActionResult doLoginCms() throws Exception {

		HttpServletRequest request = getRequest();
		JSONObject jsonObject = new JSONObject();
		String phone = request.getParameter("phone");
		String ip = getRequest().getHeader("X-Real-IP");
		if(StringHelper.isEmpty(ip))
		{
			ip = jbdUserService.getRemortIP(getRequest());
		}
		logger.info("=====ip=======" + ip);
		String pwd = request.getParameter("pwd");
		int code = getIntParameter("code");
		if (code == 0) {
			jsonObject.put("code", -1);
			jsonObject.put("error", "Change IE to chrome");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		SimpleDateFormat famat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String time = famat.format(new Date());
		String nowtime = time.substring(11, 13);
		if (!phone.equals("0123456789") &&!phone.equals("15000291390") && !phone.equals("0947658884")&& !phone.equals("0986039373")&&!phone.equals("18818690187")) {

			if (!ip.equals("118.69.224.164") && !ip.equals("192.168.1.146") && !ip.equals("192.168.1.77") && !ip.equals("127.0.0.1") 
				    && !ip.equals("192.168.1.138") && !ip.equals("116.118.114.241") && !ip.equals("118.69.191.195") && !ip.equals("0:0:0:0:0:0:0:1")
				    && !ip.equals("14.241.250.101")&& !ip.equals("222.255.200.218")&& !ip.equals("14.241.243.107")) {
				jsonObject.put("code", -1);
				jsonObject.put("error", "Prohibited");
				DataRow row = new DataRow();
				row.set("ip", ip);
				row.set("createtime", new Date());
				row.set("phone", phone);
				row.set("result", "密码不正确");
				jbdcmsService.insertCmsIP(row);
				this.getWriter().write(jsonObject.toString());
				return null;
			}
		}
		if (StringUtils.isBlank(phone)) {// 帐号判断
			jsonObject.put("code", 1);
			jsonObject.put("error", "帐号不能为空");
			// 不为 null 判断是否为空字符串
		} else if (phone.trim().isEmpty()) {
			jsonObject.put("code", 1);
			jsonObject.put("error", "帐号不能为字符串");
		} else if (StringUtils.isBlank(pwd)) {// 密码判断
			jsonObject.put("code", 2);
			jsonObject.put("error", "密码不能为空");
		} else if (pwd.trim().isEmpty()) {
			jsonObject.put("code", 2);
			jsonObject.put("error", "密码不能为空字符串");
		} else {
			String resPWD = Encrypt.MD5(pwd + IConstants.PASS_KEY);
			DataRow dr = jbdcmsService.getUserByNameAndPwd(phone, resPWD);
			if (dr != null && !dr.isEmpty()) {
				if (dr.getInt("state") == 0) {
					jsonObject.put("code", -1);
					jsonObject.put("error", "Bị cấm đăng nhập, vui lòng liên hệ IT！");
					this.getWriter().write(jsonObject.toString());
					return null;
				}

				jsonObject.put("code", 0);
				jsonObject.put("error", "登录成功");
				this.getSession().setAttribute("userCMS", dr);// 保存后台用户信息
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String last_time = sdf.format(new Date());
				int login_times = dr.getInt("login_times") + 1;
				jbdcmsService.updateUser(dr.getString("user_id"), ip, last_time, login_times);
				SessionHelper.setInt("cmsuserid", dr.getInt("user_id"), getSession());
				SessionHelper.setString("online", last_time, getSession());
			} else {
				jsonObject.put("code", 3);
				jsonObject.put("error", "Người dùng hoặc mật khẩu không chính xác");
			}
		}
		this.getWriter().write(jsonObject.toString());
		return null;

	}

	// 检查后台用户是否登录

	public ActionResult doIflogin() throws Exception {
		// 得到登录用户的id
		JSONObject jsonObject = new JSONObject();
		int cmsuser_id =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuser_id =accessVeritifivationbase.checkCMSidAndip(cmsuser_id, getipAddr());
	    if(cmsuser_id==0){
				jsonObject.put("err", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		int userid = SessionHelper.getInt("cmsuserid", getSession());
		if (userid == 0) {
			jsonObject.put("err", -1);
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		jsonObject.put("userid", userid);
		this.getWriter().write(jsonObject.toString());
		return null;
	}

	// 获取所有注册用户信息

	/**
	 * 后台账号退出登录
	 * 
	 */

	public ActionResult doCmslogOut() throws Exception {

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("msg", 1);
		getSession().invalidate();
		this.getWriter().write(jsonObject.toString());
		return null;
	}

	public ActionResult doGetUserList() throws Exception {
		logger.info("进入查注册信息列表");
		JSONObject jsonObject = new JSONObject();
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
			
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl");

		String startDate = getStrParameter("startDate");
		String endDate = getStrParameter("endDate");

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time = format.format(new Date());
		Calendar cd = Calendar.getInstance();
		try {
			cd.setTime(sdf.parse(sdf.format(new Date())));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// cd.add(Calendar.DATE, -i); // 减一天
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, -3); // 减一天
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		// 定义用户选择条件
		time = nowdate;
		String userId = "";
		String phone = "";
		String reffer = "";
		String ptype = "";
		String rzstep = "";
		String off = "";
		if (temp == 1) {

			userId = tempVelue;
		}
		if (temp == 2) {

			phone = tempVelue;
		}
		if (temp == 3) {

			reffer = tempVelue;
		}
		if (temp == 4) {

			reffer = "1";
		}
		if (temp == 5) {

			reffer = "0";
		}
		if (temp == 6) {

			ptype = tempVelue;
		}
		if (temp == 7) {

			rzstep = tempVelue;
		}
		if (temp == 8) {

			off = temp + "";
		}
		// 默认第一页
		int curPage = getIntParameter("curPage", 1);
		if (cmsuserid == 10008) {
			DBPage page = jbdcmsService.getUserListKF(curPage, 15, userId, phone, reffer, ptype, rzstep, cmsuserid,
					time, nowdate, dateno, startDate, endDate, off);
			List<DataRow> list = jbdcmsService.getUserListKF(userId, phone, reffer, ptype, cmsuserid, time, nowdate,
					dateno, startDate, endDate);

			Integer YTJR = 0;
			Integer WTJR = 0;
			Integer B = 0;
			for (DataRow dataRow : list) {
				if (dataRow.getInt("vipstatus") == 1) {
					YTJR++;
				} else {
					WTJR++;
				}

			}
			if ((YTJR + WTJR) != 0) {
				B = 100 * YTJR / (YTJR + WTJR);
			}
			String BL = B + "%";
			DataRow row = new DataRow();
			row.set("YTJR", YTJR);
			row.set("WTJR", WTJR);
			row.set("BL", BL);
			row.set("list", page);
			row.set("temp", temp);
			row.set("tempvalu", tempVelue);
			JSONObject object = JSONObject.fromBean(row);
			this.getWriter().write(object.toString());
			return null;
		} else {
			DBPage page = jbdcmsService.getUserList(curPage, 15, userId, phone, reffer, ptype, rzstep, cmsuserid, time,
					nowdate, dateno, startDate, endDate, off);
			List<DataRow> list = jbdcmsService.getUserList(userId, phone, reffer, ptype, cmsuserid, time, nowdate,
					dateno, startDate, endDate);

			Integer YTJR = 0;
			Integer WTJR = 0;
			Integer B = 0;
			for (DataRow dataRow : list) {
				if (dataRow.getInt("vipstatus") == 1) {
					YTJR++;
				} else {
					WTJR++;
				}

			}
			if ((YTJR + WTJR) != 0) {
				B = 100 * YTJR / (YTJR + WTJR);
			}
			String BL = B + "%";
			DataRow row = new DataRow();
			row.set("YTJR", YTJR);
			row.set("WTJR", WTJR);
			row.set("BL", BL);
			row.set("list", page);
			row.set("temp", temp);
			row.set("tempvalu", tempVelue);
			JSONObject object = JSONObject.fromBean(row);
			this.getWriter().write(object.toString());
			return null;
		}

	}
	public ActionResult doGetUserListOldFJ() throws Exception {
		logger.info("进入老用户没有复借信息列表");
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl");
		String startDate = getStrParameter("startDate");
		String endDate = getStrParameter("endDate");
		
		JSONObject jsonObject = new JSONObject();
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time = format.format(new Date());
		Calendar cd = Calendar.getInstance();
		try {
			cd.setTime(sdf.parse(sdf.format(new Date())));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// cd.add(Calendar.DATE, -i); // 减一天
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, -3); // 减一天
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		// 定义用户选择条件
		time = nowdate;
		String userId = "";
		String phone = "";
		String zxbeizhu = "";
		if (temp == 1) {
			
			userId = tempVelue;
		}
		if (temp == 2) {
			
			phone = tempVelue;
		}
		if (temp == 3) {
			
			zxbeizhu = tempVelue;
		}
		// 默认第一页
		int curPage = getIntParameter("curPage", 1);
		DBPage page = jbdcmsService.getUserListOldFJ(curPage, 15, userId, phone,cmsuserid, time,
				nowdate, dateno, startDate, endDate,zxbeizhu);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp", temp);
		row.set("tempvalu", tempVelue);
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());
		return null;
	}
	// 获取指定ID 用户信息
	public ActionResult doGetUserInfo() throws Exception {
		int userid = getIntParameter("id");
		// 根据用户ID获取用户信息
		DataRow user = jbdcmsService.getUserInfo(userid);
		DataRow row = new DataRow();
		// row.set("bankcard", user.getString("cardno"));
		// row.set("cardid",user.getString("remark"));
		row.set("mobilephone", user.getString("mobilephone"));
		// row.set("realname",user.getString("cardusername"));
		row.set("status", user.getString("status"));
		row.set("yhbd", user.getString("yhbd"));
		row.set("isjop", user.getString("isjop"));
		row.set("isschool", user.getString("isschool"));
		row.set("isfacebook", user.getString("isfacebook"));
		row.set("islianxi", user.getString("islianxi"));
		row.set("isshenfen", user.getString("isshenfen"));
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());
		return null;
	}

	// 获取认证成功的列表(银行卡管理)
	public ActionResult doGetWinBankCardList() throws Exception {

		JSONObject jsonObject = new JSONObject();
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl");

		String startDate1 = getStrParameter("startDate");
		String endDate1 = getStrParameter("endDate");
		String startDate = "";
		String endDate = "";
		if (!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)) {
			String[] sourceStrArray1 = startDate1.split("-");
			String[] sourceStrArray2 = endDate1.split("-");
			startDate = sourceStrArray1[2] + "-" + sourceStrArray1[1] + "-" + sourceStrArray1[0];
			endDate = sourceStrArray2[2] + "-" + sourceStrArray2[1] + "-" + sourceStrArray2[0];
		}
		// 定义用户选择条件
		String userId = "";
		String realName = "";
		String phone = "";
		String idCard = "";
		if (temp == 1) {

			userId = tempVelue;
		}

		if (temp == 2) {

			realName = tempVelue;
		}
		if (temp == 3) {

			phone = tempVelue;
		}
		if (temp == 4) {
			idCard = tempVelue;
		}

		// 默认第一页
		int curPage = getIntParameter("curPage", 1);
		DBPage page = jbdcmsService.getWinBankCardList(curPage, 15, userId, realName, phone, idCard, startDate,
				endDate);

		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp", temp);
		row.set("tempvalu", tempVelue);
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());
		return null;
	}

	// 获取匹配到的用户信息
	public ActionResult doGetPPList() throws Exception {
		
		JSONObject jsonObject = new JSONObject();
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl");

		String startDate1 = getStrParameter("startDate");
		String endDate1 = getStrParameter("endDate");
		String startDate = "";
		String endDate = "";
		if (!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)) {
			String[] sourceStrArray1 = startDate1.split("-");
			String[] sourceStrArray2 = endDate1.split("-");
			startDate = sourceStrArray1[2] + "-" + sourceStrArray1[1] + "-" + sourceStrArray1[0];
			endDate = sourceStrArray2[2] + "-" + sourceStrArray2[1] + "-" + sourceStrArray2[0];
		}

		// 审核状态
		String commit = getStrParameter("commit");
		// 定义用户选择条件
		String userId = "";
		String realName = "";
		String phone = "";

		if (temp == 1) {

			userId = tempVelue;
		}

		if (temp == 2) {

			realName = tempVelue;
		}
		if (temp == 3) {

			phone = tempVelue;
		}

		// 默认第一页
		int curPage = getIntParameter("curPage", 1);
		DBPage page = jbdcmsService.getPPList(curPage, 15, userId, realName, phone, startDate, endDate, commit);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp", temp);
		row.set("tempvalu", tempVelue);
		row.set("commit ", commit);
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());
		return null;
	}

	// 获取工作认证成功
	public ActionResult doGetJobList() throws Exception {

		JSONObject jsonObject = new JSONObject();
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl");

		String startDate1 = getStrParameter("startDate");
		String endDate1 = getStrParameter("endDate");
		String startDate = "";
		String endDate = "";
		if (!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)) {
			String[] sourceStrArray1 = startDate1.split("-");
			String[] sourceStrArray2 = endDate1.split("-");
			startDate = sourceStrArray1[2] + "-" + sourceStrArray1[1] + "-" + sourceStrArray1[0];
			endDate = sourceStrArray2[2] + "-" + sourceStrArray2[1] + "-" + sourceStrArray2[0];
		}

		// 审核状态
		String commit = getStrParameter("commit");
		// 定义用户选择条件
		String userId = "";
		String realName = "";
		String phone = "";

		if (temp == 1) {

			userId = tempVelue;
		}

		if (temp == 2) {

			realName = tempVelue;
		}
		if (temp == 3) {

			phone = tempVelue;
		}

		// 默认第一页
		int curPage = getIntParameter("curPage", 1);
		DBPage page = jbdcmsService.getJobList(curPage, 15, userId, realName, phone, startDate, endDate, commit);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp", temp);
		row.set("tempvalu", tempVelue);
		row.set("commit ", commit);
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());
		return null;
	}

	// 获取学校认证成功
	public ActionResult doGetSchoolList() throws Exception {

		JSONObject jsonObject = new JSONObject();
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl");

		String startDate1 = getStrParameter("startDate");
		String endDate1 = getStrParameter("endDate");
		String startDate = "";
		String endDate = "";
		if (!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)) {
			String[] sourceStrArray1 = startDate1.split("-");
			String[] sourceStrArray2 = endDate1.split("-");
			startDate = sourceStrArray1[2] + "-" + sourceStrArray1[1] + "-" + sourceStrArray1[0];
			endDate = sourceStrArray2[2] + "-" + sourceStrArray2[1] + "-" + sourceStrArray2[0];
		}

		// 审核状态
		String commit = getStrParameter("commit");
		// 定义用户选择条件
		String userId = "";
		String realName = "";
		String phone = "";

		if (temp == 1) {

			userId = tempVelue;
		}

		if (temp == 2) {

			realName = tempVelue;
		}
		if (temp == 3) {

			phone = tempVelue;
		}

		// 默认第一页
		int curPage = getIntParameter("curPage", 1);
		DBPage page = jbdcmsService.getSchoolList(curPage, 15, userId, realName, phone, startDate, endDate, commit);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp", temp);
		row.set("tempvalu", tempVelue);
		row.set("commit ", commit);
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());
		return null;
	}

	// 获取联系人认证成功
	public ActionResult doGetContactorList() throws Exception {

		JSONObject jsonObject = new JSONObject();
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl");

		// 审核状态
		String commit = getStrParameter("type");

		// 定义用户选择条件
		String userId = tempVelue;

		// 默认第一页
		int curPage = getIntParameter("curPage", 1);
		DBPage page = jbdcmsService.getContactorList(curPage, 15, userId, commit);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("commit", commit);
		row.set("tempvalu", tempVelue);
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());
		return null;
	}

	// 借款审核列表（step 01)

	// 第一步审核 获取指定借款ID 用户信息
	public ActionResult doGetUserRecOneInfoYN() throws Exception {

		JSONObject jsonObject = new JSONObject();
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		int jkid = getIntParameter("reid", 0);
		DataRow row = new DataRow();
		int userid1 = jbdcmsService.getUserId(jkid);
		int userid = getIntParameter("userid", userid1);
		cmsuserid = SessionHelper.getInt("cmsuserid", getSession());
		
		logger.info("请求ID:" + cmsuserid);
		logger.info("借款id2 " + jkid);
		logger.info("用户ID2 " + userid);
		Date datenow = new Date();
		SimpleDateFormat famt = new SimpleDateFormat("dd-MM-yyyy");
		DecimalFormat dfamt = new DecimalFormat("###,###");
		String IdNo = jbdcmsService.getUserNameIdNo(userid);

		String userrealname = jbdcmsService.getUserName(userid);

		List<DataRow> list = jbdcmsService.getUserIdlist(IdNo);
		String useridzu[] = new String[list.size()];
		String phonezu[] = new String[list.size()];
		if (list.size() > 1) {
			for (int i = 0; i < list.size(); i++) {
				DataRow rowid = list.get(i);
				useridzu[i] = rowid.getString("userid");
				phonezu[i] = jbdcmsService.getUserPhone(useridzu[i]);
			}
		}
		// 根据用户获取用户的借款失败信息（第一阶段审核失败） ;
		// List<DataRow> numcount = jbdcmsService.getJKSBList(userid);
		List<DataRow> numcount1 = jbdcmsService.getJKSB1ListOne(userid);
		List<DataRow> numcount2 = jbdcmsService.getJKSBListTwo(userid);
		List<DataRow> numcount3 = jbdcmsService.getJKSBListThree(userid);
		// 根据用户获取用户借款成功的信息
		List<DataRow> windrsize = jbdcmsService.getJKCGList(userid);
		// 根据用户获取用户催收的信息
		List<DataRow> cuishouxx = jbdcmsService.getJKCSList(userid);
		// 根据用户ID获取用户信息
		DataRow user = jbdcmsService.getUserRecOneInfo(userid);
		// 根据用户ID获取用户信息
		DataRow cuishoubaogao = jbdcmsService.getCuishoubaogao(userid);
		// 根据用户ID获取用户申请借款的总次数
		int userJK = jbdcmsService.getUserJKCS(userid);
		String JKFQCS = jbdcmsService.getUserJKFQCS(userid);
		int userJKFQ = 0;
		if (!TextUtils.isEmpty(JKFQCS)) {
			userJKFQ = JKFQCS.length();
		}

		int pipei = jbdcmsService.getUserPiPei(jkid);
		if(pipei==0) {
			int pipei_cs = userMoneyBase.getUMBaseCMNDpingfen(IdNo);
		     pipei = userMoneyBase.getUMBaseUserpingfen(pipei_cs,0);
		     //更新用户pipei
		     DataRow rowjk = new DataRow();
		     rowjk.set("id", jkid);
		     rowjk.set("pipei", pipei);
		     jbdcmsService.updateUserJk(rowjk);
		}
		int pingfenedu = userMoneyBase.getUMBasepingfencmndedu(pipei);
		
		
		// 根据用户ID 获取用户成功申请借款的次数
		int userCgJK = jbdcmsService.getUserJKcg(userid);
		// 根据用户ID获取用户申请借款的逾期次数
		int yuq_cs = jbdcmsService.getUserYQCS(userid);

		DataRow userfinance = jbdcmsService.personFinance(userid);

		String address = userfinance.getString("address");
		String csrq = userfinance.getString("age");

		String r = "";
		if(!"".equals(userrealname)) {
			r = userrealname.replaceAll("[aáàãảạăắằẵẳặâấầẫẩậAÁÀÃẢẠĂẮẰẴẲẶÂẤẦẪẨẬ]", "a")
					.replaceAll("[eéèẽẻẹêếềễểệễEÉÈẼẺẸÊẾỀỄỂỆ]", "e").replaceAll("[oóòõỏọôốồỗổộơớờỡởợOÓÒÕỎỌÔỐỒỖỔỘƠỚỜỠỞỢ]", "o")
					.replaceAll("[uúùũủụưứừữửựUÚÙŨỦỤƯỨỪỮỬỰ]", "u").replaceAll("[iíìĩỉịIÍÌĨỈỊ]", "i")
					.replaceAll("[yýỳỹỷỵYÝỲỸỶỴ]", "y").replaceAll("[đĐ]", "d");
		}

		List<DataRow> listname = jbdcmsService.getUserRealnamelist();
		String useridzuage[] = new String[3];
		int jishu = 0;
		for (int i = 0; i < listname.size(); i++) {
			DataRow rowid = listname.get(i);
			String realname = rowid.getString("realname");
			String ageage = rowid.getString("age");
			String useridid = rowid.getString("userid");
			
			if(!"".equals(realname)) {
				realname = realname.replaceAll("[aáàãảạăắằẵẳặâấầẫẩậAÁÀÃẢẠĂẮẰẴẲẶÂẤẦẪẨẬ]", "a")
						.replaceAll("[eéèẽẻẹêếềễểệễEÉÈẼẺẸÊẾỀỄỂỆ]", "e")
						.replaceAll("[oóòõỏọôốồỗổộơớờỡởợOÓÒÕỎỌÔỐỒỖỔỘƠỚỜỠỞỢ]", "o")
						.replaceAll("[uúùũủụưứừữửựUÚÙŨỦỤƯỨỪỮỬỰ]", "u").replaceAll("[iíìĩỉịIÍÌĨỈỊ]", "i")
						.replaceAll("[yýỳỹỷỵYÝỲỸỶỴ]", "y").replaceAll("[đĐ]", "d");
			}

			if (r.trim().toLowerCase().equals(realname.trim().toLowerCase()) && csrq.equals(ageage)) {
				useridzuage[jishu] = useridid;
				jishu++;
			}
			if (jishu > 2) {
				break;
			}
		}

		String homeaddress = userfinance.getString("homeaddress");
		String email = userfinance.getString("email");
		String phonetype = userfinance.getString("phonetype");

		int age = 0;
		if (!TextUtils.isEmpty(csrq)) {
			age = Integer.parseInt(famt.format(datenow).substring(6)) - Integer.parseInt(csrq.substring(6));
		}
		// 获取用户的工作信息
		DataRow personInfo = jbdcmsService.personInfoYN(userid);
		// 联系人信息
		DataRow lianxi = jbdcmsService.personLianXi(userid);
		// 银行卡信息
		DataRow bank = jbdcmsService.personBank(userid);
		// 审核状态
		DataRow shenfen = jbdcmsService.personShenfen(userid);

		//员工状态
		DataRow cmsuseinfo = jbdcmsService.getCmsuserInfo(cmsuserid);
		if(null != cmsuseinfo) {
			
			int userroleid = cmsuseinfo.getInt("roleid");
			if(userroleid ==1 ||userroleid ==24 || userroleid ==26 || userroleid ==51  || userroleid ==54 || userroleid ==52  ) {
				
				if (pipei != 0 && pipei < 10 ) {
					DataRow PPLianXi = jbdcmsService.getPPLianxi(IdNo);
					row.set("qr1", PPLianXi.getString("qr1"));
					row.set("qr2", PPLianXi.getString("qr2"));
					row.set("qr3", PPLianXi.getString("qr3"));
				} else {
					row.set("qr1", "Không");
					row.set("qr2", "Không");
					row.set("qr3", "Không");
				}
			} else {
				row.set("qr1", "M2 được xem");
				row.set("qr2", "M2 được xem");
				row.set("qr3", "M2 được xem");
			}
		}else {
			row.set("qr1", "Không");
			row.set("qr2", "Không");
			row.set("qr3", "Không");
		}

		// 获取用户学校信息
		DataRow userschool = jbdcmsService.getUserSchool(userid);
		/*
		 * //获取用户教育信息 DataRow userTonghua = jbdcmsService.getUserTonghua(userid);
		 */
		String p1 = "";
		String p2 = "";
		String p3 = "";
		if (shenfen != null) {
			p1 = shenfen.getString("p1");
			p2 = shenfen.getString("p2");
			p3 = shenfen.getString("p3");
		}

		String p1w = "";
		String p2w = "";
		String p3w = "";
		String p1s = "";
		String p2s = "";

		String p3s = "";
		if (personInfo != null) {
			p1w = personInfo.getString("p1");
			p2w = personInfo.getString("p2");
			p3w = personInfo.getString("p3");
		}
		if (userschool != null) {
			p1s = userschool.getString("p1");
			p2s = userschool.getString("p2");
			p3s = userschool.getString("p3");
		}

		row.set("p1", p1);
		row.set("p2", p2);
		row.set("p3", p3);
		row.set("p1w", p1w);
		row.set("p2w", p2w);
		row.set("p3w", p3w);
		row.set("p1s", p1s);
		row.set("p2s", p2s);
		row.set("p3s", p3s);
		row.set("pingfen", (double)pipei/10);
		row.set("pingfenedu", dfamt.format(pingfenedu));

		row.set("user_id", user.getString("userid3"));// 编号
		row.set("useridzu", useridzu);// 编号
		row.set("useridzuage", useridzuage);// 生日姓名相同
		row.set("phonezu", phonezu);// 编号
		row.set("username", user.getString("username"));// 手机型号
		row.set("profession", user.getString("profession"));// 编号
		row.set("shilian", user.getString("shilian_zt"));// 编号
		row.set("jk_id", jkid);
		row.set("realname", user.getString("cardusername")); // 姓名
		row.set("napasbankno", user.getString("napasbankno")); // 姓名
		row.set("cardid", IdNo); // 身份证
		row.set("address", address); // 地址
		if (!TextUtils.isEmpty(email)) {
			row.set("email", email); // 邮箱
		} else {
			row.set("email", "Không");
		}
		if (!TextUtils.isEmpty(phonetype)) {
			row.set("phonetype", phonetype); // 手机型号
		} else {
			row.set("phonetype", "Không");
		}
		row.set("csrq", csrq); // 出生日期
		row.set("age", age); // 出生日期
		row.set("homeaddress", homeaddress); // 出生日期
		row.set("userrealname", userrealname); // 出生日期
		row.set("cellphone", user.getString("mobilephone")); // 手机号
		row.set("createtime", user.getString("createtime")); // 注册时间
		row.set("windrsize", user.getString("chenggong_cs")); // 成功放款次数
		row.set("cou", userJK); // 申请借款次数
		row.set("jkfqcs", userJKFQ); // 申请借款次数
		row.set("yuqcount", yuq_cs); // 逾期次数
		row.set("usercgjk", userCgJK); // 成功次数
		if (personInfo != null) {
			row.set("company", personInfo.getString("company"));
			row.set("tel", personInfo.getString("tel"));
			row.set("workaddress", personInfo.getString("address"));
			row.set("workname", personInfo.getString("workname"));
			row.set("position", personInfo.getString("position"));
			row.set("pay", personInfo.getString("pay"));
			row.set("worktime", personInfo.getString("time"));
			row.set("status", personInfo.getString("status"));
		} else {
			row.set("company", "");
			row.set("tel", "");
			row.set("workaddress", "");
			row.set("workname", "");
			row.set("position", "");
			row.set("pay", "");
			row.set("worktime", "");
			row.set("status", "");
		}
		row.set("lianxi", lianxi);
		row.set("bank", bank);
		row.set("shenfen", shenfen);
		if (userschool != null) {
			row.set("schoolname", userschool.getString("schoolname"));
			row.set("schooladdress", userschool.getString("address"));
			row.set("classname", userschool.getString("classname"));
			row.set("stuid", userschool.getString("stuid"));
			row.set("schooltime", userschool.getString("time"));
		} else {
			row.set("schoolname", "");
			row.set("schooladdress", "");
			row.set("classname", "");
			row.set("stuid", "");
			row.set("schooltime", "");
		}
		row.set("numcount1", numcount1);
		row.set("numcount2", numcount2);
		row.set("numcount3", numcount3);
		row.set("windrsize", windrsize);

		row.set("cuishouxx", cuishouxx);
		row.set("cuishoubaogao", cuishoubaogao);
		row.set("error", "0");
		row.set("msg", "系统正常");
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());
		return null;
	}

	// 借款审核列表（step 01)

	// 第一步审核 获取指定借款ID 用户信息
	public ActionResult doGetUserRecOneInfoYNH5() throws Exception {

		JSONObject jsonObject = new JSONObject();
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		
		int jkid = getIntParameter("reid", 0);
		DataRow row = new DataRow();
		int userid1 = jbdcmsService.getUserId(jkid);
		int userid = getIntParameter("userid", userid1);
	    cmsuserid = SessionHelper.getInt("cmsuserid", getSession());

		logger.info("请求ID:" + cmsuserid);
		logger.info("借款id2 " + jkid);
		logger.info("用户ID2 " + userid);
		Date datenow = new Date();
		SimpleDateFormat famt = new SimpleDateFormat("dd-MM-yyyy");
		String IdNo = jbdcmsService.getUserNameIdNo(userid);

		String sfmiwen = Encrypt.MD5(IdNo + jiami);
		com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
		json.put("secret", sfmiwen);
		json.put("idno", IdNo);
		String url = "http://app.m99vn.com/servlet/current/M99User1?function=GetUserM99YN";

		String response = HttpUtil.doPost(url, json, "UTF-8");

		com.alibaba.fastjson.JSONObject json1 = com.alibaba.fastjson.JSONObject.parseObject(response);

		int error = json1.getInteger("error");
		if (error == 1) {
			List<DataRow> numcount1 = jbdcmsService.getJKSB1ListOne(userid);
			List<DataRow> numcount2 = jbdcmsService.getJKSBListTwo(userid);
			List<DataRow> numcount3 = jbdcmsService.getJKSBListThree(userid);
			// 根据用户获取用户借款成功的信息
			List<DataRow> windrsize = jbdcmsService.getJKCGList(userid);
			// 根据用户获取用户催收的信息
			List<DataRow> cuishouxx = jbdcmsService.getJKCSList(userid);
			// 根据用户ID获取用户信息
			DataRow cuishoubaogao = jbdcmsService.getCuishoubaogao(userid);
			DataRow user = jbdcmsService.getUserRecOneInfo(userid);
			int userJK = jbdcmsService.getUserJKCS(userid);
			// 根据用户ID 获取用户成功申请借款的次数
			int userCgJK = jbdcmsService.getUserJKcg(userid);
			// 根据用户ID获取用户申请借款的逾期次数
			int yuq_cs = jbdcmsService.getUserYQCS(userid);
			// 银行卡信息
			DataRow bank = jbdcmsService.personBank(userid);
			String JKFQCS = jbdcmsService.getUserJKFQCS(userid);
			int userJKFQ = 0;
			if (!TextUtils.isEmpty(JKFQCS)) {
				userJKFQ = JKFQCS.length();
			}
			row.set("p1", json1.getString("p1"));
			row.set("p2", json1.getString("p2"));
			row.set("p3", json1.getString("p3"));
			row.set("pingfen", 0);

			row.set("user_id", userid);// 编号
			row.set("useridzu", "");// 编号
			row.set("useridzuage", "");// 生日姓名相同
			row.set("phonezu", "");// 编号
			row.set("username", user.getString("username"));// 手机型号
			row.set("profession", user.getString("profession"));// 编号
			row.set("shilian", user.getString("shilian_zt"));// 编号
			row.set("jk_id", jkid);
			row.set("realname", user.getString("cardusername")); // 姓名
			row.set("napasbankno", user.getString("napasbankno")); // 姓名
			row.set("cardid", IdNo); // 身份证
			row.set("address", json1.getString("address")); // 地址

			row.set("email", "Không");
			row.set("phonetype", "Không");

			row.set("csrq", json1.getString("csrq")); // 出生日期
			row.set("age", json1.getString("age")); // 出生日期
			row.set("homeaddress", json1.getString("homeaddress")); // 出生日期
			row.set("userrealname", json1.getString("userrealname")); // 出生日期
			row.set("cellphone", user.getString("mobilephone")); // 手机号
			row.set("createtime", user.getString("createtime")); // 注册时间
			row.set("windrsize", user.getString("chenggong_cs")); // 成功放款次数
			row.set("cou", userJK); // 申请借款次数
			row.set("jkfqcs", userJKFQ); // 申请借款次数
			row.set("yuqcount", yuq_cs); // 逾期次数
			row.set("usercgjk", userCgJK); // 成功次数

			row.set("company", json1.getString("company"));
			row.set("tel", json1.getString("tel"));
			row.set("workaddress", json1.getString("workaddress"));
			row.set("workname", json1.getString("workname"));
			row.set("position", json1.getString("position"));
			row.set("pay", json1.getString("pay"));
			row.set("worktime", json1.getString("worktime"));
			row.set("status", json1.getString("status"));
			com.alibaba.fastjson.JSONObject lianxi = com.alibaba.fastjson.JSONObject
					.parseObject(json1.getString("lianxi"));
			row.set("lianxi", lianxi);
			row.set("bank", bank);
			row.set("shenfen", json1.getString("shenfen"));
			row.set("numcount1", numcount1);
			row.set("numcount2", numcount2);
			row.set("numcount3", numcount3);
			row.set("windrsize", windrsize);
			row.set("cuishouxx", cuishouxx);
			row.set("cuishoubaogao", cuishoubaogao);
			row.set("error", "0");
			row.set("msg", "系统正常");
			JSONObject object = JSONObject.fromBean(row);
			this.getWriter().write(object.toString());
			return null;
		} else {

			String userrealname = jbdcmsService.getUserBankName(userid);

			List<DataRow> list = jbdcmsService.getUserIdlist(IdNo);
			String useridzu[] = new String[list.size()];
			String phonezu[] = new String[list.size()];
			if (list.size() > 1) {
				for (int i = 0; i < list.size(); i++) {
					DataRow rowid = list.get(i);
					useridzu[i] = rowid.getString("userid");
					phonezu[i] = jbdcmsService.getUserPhone(useridzu[i]);
				}
			}
			List<DataRow> numcount1 = jbdcmsService.getJKSB1ListOne(userid);
			List<DataRow> numcount2 = jbdcmsService.getJKSBListTwo(userid);
			List<DataRow> numcount3 = jbdcmsService.getJKSBListThree(userid);
			// 根据用户获取用户借款成功的信息
			List<DataRow> windrsize = jbdcmsService.getJKCGList(userid);
			// 根据用户获取用户催收的信息
			List<DataRow> cuishouxx = jbdcmsService.getJKCSList(userid);
			// 根据用户ID获取用户信息
			DataRow user = jbdcmsService.getUserRecOneInfo(userid);
			// 根据用户ID获取用户信息
			DataRow cuishoubaogao = jbdcmsService.getCuishoubaogao(userid);
			// 根据用户ID获取用户申请借款的总次数
			int userJK = jbdcmsService.getUserJKCS(userid);
			String JKFQCS = jbdcmsService.getUserJKFQCS(userid);
			int userJKFQ = 0;
			if (!TextUtils.isEmpty(JKFQCS)) {
				userJKFQ = JKFQCS.length();
			}

			int pipei = jbdcmsService.getUserPiPei(jkid);
			// 根据用户ID 获取用户成功申请借款的次数
			int userCgJK = jbdcmsService.getUserJKcg(userid);
			// 根据用户ID获取用户申请借款的逾期次数
			int yuq_cs = jbdcmsService.getUserYQCS(userid);

			DataRow userfinance = jbdcmsService.personFinance(userid);
			String address = "none";
			String csrq = "01-01-2019";
			String homeaddress = "none";
			String email = "none";
			String phonetype = "none";
			if (userfinance != null) {
				address = userfinance.getString("address");
				csrq = userfinance.getString("age");
				homeaddress = userfinance.getString("homeaddress");
				email = userfinance.getString("email");
				phonetype = userfinance.getString("phonetype");
			}
			int age = 0;
			if (!TextUtils.isEmpty(csrq)) {
				age = Integer.parseInt(famt.format(datenow).substring(6)) - Integer.parseInt(csrq.substring(6));
			}
			// 获取用户的工作信息
			DataRow personInfo = jbdcmsService.personInfoYN(userid);
			// 联系人信息
			DataRow lianxi = jbdcmsService.personLianXi(userid);
			// 银行卡信息
			DataRow bank = jbdcmsService.personBank(userid);
			// 审核状态
			DataRow shenfen = jbdcmsService.personShenfen(userid);
			String p1 = "8=1551486709000.jpg";
			String p2 = "8=1551486718000.jpg";
			String p3 = "8=1551486731000.jpg";
			if (shenfen != null) {
				p1 = shenfen.getString("p1");
				p2 = shenfen.getString("p2");
				p3 = shenfen.getString("p3");
			}
			row.set("p1", p1);
			row.set("p2", p2);
			row.set("p3", p3);
			row.set("pingfen", pipei);

			row.set("user_id", user.getString("userid3"));// 编号
			row.set("useridzu", useridzu);// 编号
			row.set("useridzuage", "");// 生日姓名相同
			row.set("phonezu", phonezu);// 编号
			row.set("username", user.getString("username"));// 手机型号
			row.set("profession", user.getString("profession"));// 编号
			row.set("shilian", user.getString("shilian_zt"));// 编号
			row.set("jk_id", jkid);
			row.set("realname", user.getString("cardusername")); // 姓名
			row.set("napasbankno", user.getString("napasbankno")); // 姓名
			row.set("cardid", IdNo); // 身份证
			row.set("address", address); // 地址
			if (!TextUtils.isEmpty(email)) {
				row.set("email", email); // 邮箱
			} else {
				row.set("email", "Không");
			}
			if (!TextUtils.isEmpty(phonetype)) {
				row.set("phonetype", phonetype); // 手机型号
			} else {
				row.set("phonetype", "Không");
			}
			row.set("csrq", csrq); // 出生日期
			row.set("age", age); // 出生日期
			row.set("homeaddress", homeaddress); // 出生日期
			row.set("userrealname", userrealname); // 出生日期
			row.set("cellphone", user.getString("mobilephone")); // 手机号
			row.set("createtime", user.getString("createtime")); // 注册时间
			row.set("windrsize", user.getString("chenggong_cs")); // 成功放款次数
			row.set("cou", userJK); // 申请借款次数
			row.set("jkfqcs", userJKFQ); // 申请借款次数
			row.set("yuqcount", yuq_cs); // 逾期次数
			row.set("usercgjk", userCgJK); // 成功次数
			if (personInfo != null) {
				row.set("company", personInfo.getString("company"));
				row.set("tel", personInfo.getString("tel"));
				row.set("workaddress", personInfo.getString("address"));
				row.set("workname", personInfo.getString("workname"));
				row.set("position", personInfo.getString("position"));
				row.set("pay", personInfo.getString("pay"));
				row.set("worktime", personInfo.getString("time"));
				row.set("status", personInfo.getString("status"));
			} else {
				row.set("company", "");
				row.set("tel", "");
				row.set("workaddress", "");
				row.set("workname", "");
				row.set("position", "");
				row.set("pay", "");
				row.set("worktime", "");
				row.set("status", "");
			}
			row.set("lianxi", lianxi);
			row.set("bank", bank);
			row.set("shenfen", shenfen);
			row.set("numcount1", numcount1);
			row.set("numcount2", numcount2);
			row.set("numcount3", numcount3);
			row.set("windrsize", windrsize);
			row.set("cuishouxx", cuishouxx);
			row.set("cuishoubaogao", cuishoubaogao);
			row.set("error", "0");
			row.set("msg", "系统正常");
			JSONObject object = JSONObject.fromBean(row);
			this.getWriter().write(object.toString());
			return null;
		}
	}
	public ActionResult doGetUserRecOneInfoYNH5MOFA() throws Exception {
		
		JSONObject jsonObject = new JSONObject();
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		int jkid = getIntParameter("reid", 0);
		DataRow row = new DataRow();
		int userid1 = jbdcmsService.getUserId(jkid);
		int userid = getIntParameter("userid", userid1);
	    cmsuserid = SessionHelper.getInt("cmsuserid", getSession());
	
		logger.info("请求ID:" + cmsuserid);
		logger.info("借款id2 " + jkid);
		logger.info("用户ID2 " + userid);
		Date datenow = new Date();
		SimpleDateFormat famt = new SimpleDateFormat("dd-MM-yyyy");
		String IdNo = jbdcmsService.getUserNameIdNo(userid);
		
		String sfmiwen = Encrypt.MD5(IdNo + jiami);
		com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
		json.put("secret", sfmiwen);
		json.put("idno", IdNo);
		String url = "http://app.m99vn.com/servlet/current/M99User1?function=GetUserM99YN";
		
		String response = HttpUtil.doPost(url, json, "UTF-8");
		
		com.alibaba.fastjson.JSONObject json1 = com.alibaba.fastjson.JSONObject.parseObject(response);
		
		int error = json1.getInteger("error");
		if (error == 1) {
			//List<DataRow> numcount1 = jbdcmsService.getJKSB1ListOne(userid);
			//List<DataRow> numcount2 = jbdcmsService.getJKSBListTwo(userid);
			//List<DataRow> numcount3 = jbdcmsService.getJKSBListThree(userid);
			// 根据用户获取用户借款成功的信息
			//List<DataRow> windrsize = jbdcmsService.getJKCGList(userid);
			// 根据用户获取用户催收的信息
			List<DataRow> cuishouxx = jbdcmsService.getJKCSList(userid);
			// 根据用户ID获取用户信息
			DataRow cuishoubaogao = jbdcmsService.getCuishoubaogao(userid);
			DataRow user = jbdcmsService.getUserRecOneInfo(userid);
			int userJK = jbdcmsService.getUserJKCS(userid);
			// 根据用户ID 获取用户成功申请借款的次数
			int userCgJK = jbdcmsService.getUserJKcg(userid);
			// 根据用户ID获取用户申请借款的逾期次数
			int yuq_cs = jbdcmsService.getUserYQCS(userid);
			// 银行卡信息
			DataRow bank = jbdcmsService.personBank(userid);
			String JKFQCS = jbdcmsService.getUserJKFQCS(userid);
			int userJKFQ = 0;
			if (!TextUtils.isEmpty(JKFQCS)) {
				userJKFQ = JKFQCS.length();
			}
			row.set("p1", json1.getString("p1"));
			row.set("p2", json1.getString("p2"));
			row.set("p3", json1.getString("p3"));
			row.set("pingfen", 0);
			
			row.set("user_id", userid);// 编号
			row.set("useridzu", "");// 编号
			row.set("useridzuage", "");// 生日姓名相同
			row.set("phonezu", "");// 编号
			row.set("username", user.getString("username"));// 手机型号
			row.set("profession", user.getString("profession"));// 编号
			row.set("jk_id", jkid);
			row.set("realname", user.getString("cardusername")); // 姓名
			row.set("napasbankno", user.getString("napasbankno")); // 姓名
			row.set("cardid", IdNo); // 身份证
			row.set("address", json1.getString("address")); // 地址
			
			row.set("email", "Không");
			row.set("phonetype", "Không");
			
			row.set("csrq", json1.getString("csrq")); // 出生日期
			row.set("age", json1.getString("age")); // 出生日期
			row.set("homeaddress", json1.getString("homeaddress")); // 出生日期
			row.set("userrealname", json1.getString("userrealname")); // 出生日期
			row.set("cellphone", user.getString("mobilephone")); // 手机号
			row.set("createtime", user.getString("createtime")); // 注册时间
			row.set("windrsize", user.getString("chenggong_cs")); // 成功放款次数
			row.set("cou", userJK); // 申请借款次数
			row.set("jkfqcs", userJKFQ); // 申请借款次数
			row.set("yuqcount", yuq_cs); // 逾期次数
			row.set("usercgjk", userCgJK); // 成功次数
			
			row.set("company", json1.getString("company"));
			row.set("tel", json1.getString("tel"));
			row.set("workaddress", json1.getString("workaddress"));
			row.set("workname", json1.getString("workname"));
			row.set("position", json1.getString("position"));
			row.set("pay", json1.getString("pay"));
			row.set("worktime", json1.getString("worktime"));
			row.set("status", json1.getString("status"));
			com.alibaba.fastjson.JSONObject lianxi = com.alibaba.fastjson.JSONObject
					.parseObject(json1.getString("lianxi"));
			com.alibaba.fastjson.JSONArray numcount1 = com.alibaba.fastjson.JSONArray
					.parseArray(json1.getString("numcount1"));
			com.alibaba.fastjson.JSONArray numcount2 = com.alibaba.fastjson.JSONArray
					.parseArray(json1.getString("numcount2"));
			com.alibaba.fastjson.JSONArray numcount3 = com.alibaba.fastjson.JSONArray
					.parseArray(json1.getString("numcount3"));
			com.alibaba.fastjson.JSONArray windrsize = com.alibaba.fastjson.JSONArray
					.parseArray(json1.getString("windrsize"));
			row.set("lianxi", lianxi);
			row.set("bank", bank);
			row.set("shenfen", json1.getString("shenfen"));
			row.set("numcount1", numcount1);
			row.set("numcount2", numcount2);
			row.set("numcount3", numcount3);
			row.set("windrsize", windrsize);
			row.set("cuishouxx", cuishouxx);
			row.set("cuishoubaogao", cuishoubaogao);
			row.set("error", "0");
			row.set("msg", "系统正常");
			JSONObject object = JSONObject.fromBean(row);
			this.getWriter().write(object.toString());
			return null;
		} else {
			
			String userrealname = jbdcmsService.getUserBankName(userid);
			
			List<DataRow> list = jbdcmsService.getUserIdlist(IdNo);
			String useridzu[] = new String[list.size()];
			String phonezu[] = new String[list.size()];
			if (list.size() > 1) {
				for (int i = 0; i < list.size(); i++) {
					DataRow rowid = list.get(i);
					useridzu[i] = rowid.getString("userid");
					phonezu[i] = jbdcmsService.getUserPhone(useridzu[i]);
				}
			}
			List<DataRow> numcount1 = jbdcmsService.getJKSB1ListOne(userid);
			List<DataRow> numcount2 = jbdcmsService.getJKSBListTwo(userid);
			List<DataRow> numcount3 = jbdcmsService.getJKSBListThree(userid);
			// 根据用户获取用户借款成功的信息
			List<DataRow> windrsize = jbdcmsService.getJKCGList(userid);
			// 根据用户获取用户催收的信息
			List<DataRow> cuishouxx = jbdcmsService.getJKCSList(userid);
			// 根据用户ID获取用户信息
			DataRow user = jbdcmsService.getUserRecOneInfo(userid);
			// 根据用户ID获取用户信息
			DataRow cuishoubaogao = jbdcmsService.getCuishoubaogao(userid);
			// 根据用户ID获取用户申请借款的总次数
			int userJK = jbdcmsService.getUserJKCS(userid);
			String JKFQCS = jbdcmsService.getUserJKFQCS(userid);
			int userJKFQ = 0;
			if (!TextUtils.isEmpty(JKFQCS)) {
				userJKFQ = JKFQCS.length();
			}
			
			int pipei = jbdcmsService.getUserPiPei(jkid);
			// 根据用户ID 获取用户成功申请借款的次数
			int userCgJK = jbdcmsService.getUserJKcg(userid);
			// 根据用户ID获取用户申请借款的逾期次数
			int yuq_cs = jbdcmsService.getUserYQCS(userid);
			
			DataRow userfinance = jbdcmsService.personFinance(userid);
			String address = "none";
			String csrq = "01-01-2019";
			String homeaddress = "none";
			String email = "none";
			String phonetype = "none";
			if (userfinance != null) {
				address = userfinance.getString("address");
				csrq = userfinance.getString("age");
				homeaddress = userfinance.getString("homeaddress");
				email = userfinance.getString("email");
				phonetype = userfinance.getString("phonetype");
			}
			int age = 0;
			if (!TextUtils.isEmpty(csrq)) {
				age = Integer.parseInt(famt.format(datenow).substring(6)) - Integer.parseInt(csrq.substring(6));
			}
			// 获取用户的工作信息
			DataRow personInfo = jbdcmsService.personInfoYN(userid);
			// 联系人信息
			DataRow lianxi = jbdcmsService.personLianXi(userid);
			// 银行卡信息
			DataRow bank = jbdcmsService.personBank(userid);
			// 审核状态
			DataRow shenfen = jbdcmsService.personShenfen(userid);
			String p1 = "8=1551486709000.jpg";
			String p2 = "8=1551486718000.jpg";
			String p3 = "8=1551486731000.jpg";
			if (shenfen != null) {
				p1 = shenfen.getString("p1");
				p2 = shenfen.getString("p2");
				p3 = shenfen.getString("p3");
			}
			row.set("p1", p1);
			row.set("p2", p2);
			row.set("p3", p3);
			row.set("pingfen", pipei);
			
			row.set("user_id", user.getString("userid3"));// 编号
			row.set("useridzu", useridzu);// 编号
			row.set("useridzuage", "");// 生日姓名相同
			row.set("phonezu", phonezu);// 编号
			row.set("username", user.getString("username"));// 手机型号
			row.set("profession", user.getString("profession"));// 编号
			row.set("jk_id", jkid);
			row.set("realname", user.getString("cardusername")); // 姓名
			row.set("napasbankno", user.getString("napasbankno")); // 姓名
			row.set("cardid", IdNo); // 身份证
			row.set("address", address); // 地址
			if (!TextUtils.isEmpty(email)) {
				row.set("email", email); // 邮箱
			} else {
				row.set("email", "Không");
			}
			if (!TextUtils.isEmpty(phonetype)) {
				row.set("phonetype", phonetype); // 手机型号
			} else {
				row.set("phonetype", "Không");
			}
			row.set("csrq", csrq); // 出生日期
			row.set("age", age); // 出生日期
			row.set("homeaddress", homeaddress); // 出生日期
			row.set("userrealname", userrealname); // 出生日期
			row.set("cellphone", user.getString("mobilephone")); // 手机号
			row.set("createtime", user.getString("createtime")); // 注册时间
			row.set("windrsize", user.getString("chenggong_cs")); // 成功放款次数
			row.set("cou", userJK); // 申请借款次数
			row.set("jkfqcs", userJKFQ); // 申请借款次数
			row.set("yuqcount", yuq_cs); // 逾期次数
			row.set("usercgjk", userCgJK); // 成功次数
			if (personInfo != null) {
				row.set("company", personInfo.getString("company"));
				row.set("tel", personInfo.getString("tel"));
				row.set("workaddress", personInfo.getString("address"));
				row.set("workname", personInfo.getString("workname"));
				row.set("position", personInfo.getString("position"));
				row.set("pay", personInfo.getString("pay"));
				row.set("worktime", personInfo.getString("time"));
				row.set("status", personInfo.getString("status"));
			} else {
				row.set("company", "");
				row.set("tel", "");
				row.set("workaddress", "");
				row.set("workname", "");
				row.set("position", "");
				row.set("pay", "");
				row.set("worktime", "");
				row.set("status", "");
			}
			row.set("lianxi", lianxi);
			row.set("bank", bank);
			row.set("shenfen", shenfen);
			row.set("numcount1", numcount1);
			row.set("numcount2", numcount2);
			row.set("numcount3", numcount3);
			row.set("windrsize", windrsize);
			row.set("cuishouxx", cuishouxx);
			row.set("cuishoubaogao", cuishoubaogao);
			row.set("error", "0");
			row.set("msg", "系统正常");
			JSONObject object = JSONObject.fromBean(row);
			this.getWriter().write(object.toString());
			return null;
		}
	}

	public ActionResult doDelNotDown() {
		logger.info("删除h5进来未下载app的");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		cmsuserid = SessionHelper.getInt("cmsuserid", getSession());// 后台登录账户
		
		logger.info("请求ID:" + cmsuserid);
		String cloId = getStrParameter("recid");
		DataRow row = new DataRow();

		try {
			jbdcmsService.updateJkIsDown(cmsuserid, cloId);
			row.set("error", 1);
			row.set("msg", "DELETE SUCCESS");
		} catch (Exception e) {
			row.set("error", -1);
			row.set("msg", "DELETE FAILED");
		}
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());
		return null;
	}

	public ActionResult doGetRecordListOne() throws Exception {
		logger.info("进入借款审核方法内");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl");
		String startDate1 = getStrParameter("startDate");
		String endDate1 = getStrParameter("endTime");
		String startDate = "";
		String endDate = "";
		if (!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)) {
			String[] sourceStrArray1 = startDate1.split("-");
			String[] sourceStrArray2 = endDate1.split("-");
			startDate = sourceStrArray1[2] + "-" + sourceStrArray1[1] + "-" + sourceStrArray1[0];
			endDate = sourceStrArray2[2] + "-" + sourceStrArray2[1] + "-" + sourceStrArray2[0];
		}

		// 审核状态
		String commit = getStrParameter("commit");
		// 定义用户选择条件
		String userId = "";
		String realName = "";
		String phone = "";
		String xypf = "";
		String shenheid = "";

		String off = "";
		if (temp == 1) {

			userId = tempVelue;
		}

		if (temp == 2) {

			realName = tempVelue;
		}
		if (temp == 3) {

			phone = tempVelue;
		}
		if (temp == 5) {

			xypf = tempVelue;
		}
		if (temp == 6) {

			off = 6 + "";
		}
		if(temp==7) {
			shenheid=tempVelue;
		}
		// 默认第一页
		int curPage = getIntParameter("curPage", 1);
		DBPage page = new DBPage(curPage, 15);

		// 一组
		String shenhezu1 = jbdcmsService.getPJGZSHENHE1();
		String shenhezuzz1 = jbdcmsService.getPJGZSHENHEZZ1();
		String shzu1[] = shenhezu1.split(",");

		// 二组
		String shenhezu2 = jbdcmsService.getPJGZSHENHE2();
		String shenhezuzz2 = jbdcmsService.getPJGZSHENHEZZ2();
		String shzu2[] = shenhezu2.split(",");

		// 三组
		String shenhezu3 = jbdcmsService.getPJGZSHENHE3();
		String shenhezuzz3 = jbdcmsService.getPJGZSHENHEZZ3();
		String shzu3[] = shenhezu3.split(",");

		// 四组
		String shenhezu4 = jbdcmsService.getPJGZSHENHE4();
		String shenhezuzz4 = jbdcmsService.getPJGZSHENHEZZ4();
		String shzu4[] = shenhezu4.split(",");

		if (cmsuserid == 6 || cmsuserid == 8 || cmsuserid == 222 || cmsuserid == 888 || cmsuserid == 9999 || cmsuserid == 8888 || cmsuserid == 135699 || cmsuserid == 2038) {
			page = jbdcmsService.getRecordListOne(curPage, 15, userId, realName, phone, xypf, cmsuserid, startDate,
					endDate, commit, off,shenheid);

		}
		// 一组
		else if (shenhezuzz1.equals(cmsuserid + "")) {
			page = jbdcmsService.getRecordListOneSH1(curPage, 15, userId, realName, phone, xypf, cmsuserid, startDate,
					endDate, commit, shzu1, off,shenheid);
		}
		// 二组
		else if (shenhezuzz2.equals(cmsuserid + "")) {
			page = jbdcmsService.getRecordListOneSH2(curPage, 15, userId, realName, phone, xypf, cmsuserid, startDate,
					endDate, commit, shzu2, off,shenheid);
		}
		// 三组
		else if (shenhezuzz3.equals(cmsuserid + "")) {
			page = jbdcmsService.getRecordListOneSH3(curPage, 15, userId, realName, phone, xypf, cmsuserid, startDate,
					endDate, commit, shzu3, off,shenheid);
		}
		// 四组
		else if (shenhezuzz4.equals(cmsuserid + "")) {
			page = jbdcmsService.getRecordListOneSH4(curPage, 15, userId, realName, phone, xypf, cmsuserid, startDate,
					endDate, commit, shzu4, off,shenheid);
		} else {
			page = jbdcmsService.getRecordListOneSH(curPage, 15, userId, realName, phone, xypf, cmsuserid, startDate,
					endDate, commit, off);
		}

		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp", temp);
		row.set("tempvalu", tempVelue);
		row.set("commit ", commit);
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());

		return null;
	}

	public ActionResult doGetRecordListOneWJDH() throws Exception {
		logger.info("进入借款审核方法内");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl");
		String startDate1 = getStrParameter("startDate");
		String endDate1 = getStrParameter("endTime");
		String startDate = "";
		String endDate = "";
		if (!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)) {
			String[] sourceStrArray1 = startDate1.split("-");
			String[] sourceStrArray2 = endDate1.split("-");
			startDate = sourceStrArray1[2] + "-" + sourceStrArray1[1] + "-" + sourceStrArray1[0];
			endDate = sourceStrArray2[2] + "-" + sourceStrArray2[1] + "-" + sourceStrArray2[0];
		}

		// 审核状态
		String commit = getStrParameter("commit");
		// 定义用户选择条件
		String userId = "";
		String realName = "";
		String phone = "";
		String xypf = "";

		if (temp == 1) {

			userId = tempVelue;
		}

		if (temp == 2) {

			realName = tempVelue;
		}
		if (temp == 3) {

			phone = tempVelue;
		}
		if (temp == 5) {

			xypf = tempVelue;
		}

		// 默认第一页
		int curPage = getIntParameter("curPage", 1);
		DBPage page = jbdcmsService.getRecordListOneWJDH(curPage, 15, userId, realName, phone, xypf, cmsuserid,
				startDate, endDate, commit);

		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp", temp);
		row.set("tempvalu", tempVelue);
		row.set("commit ", commit);
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());

		return null;
	}

	// 第二步审核列表
	public ActionResult doGetRecordListTwo() throws Exception {
		logger.info("进入借款审核方法内2");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl");
		String startDate1 = getStrParameter("startDate");
		String endDate1 = getStrParameter("endDate");
		String startDate = "";
		String endDate = "";
		if (!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)) {
			String[] sourceStrArray1 = startDate1.split("-");
			String[] sourceStrArray2 = endDate1.split("-");
			startDate = sourceStrArray1[2] + "-" + sourceStrArray1[1] + "-" + sourceStrArray1[0];
			endDate = sourceStrArray2[2] + "-" + sourceStrArray2[1] + "-" + sourceStrArray2[0];
		}
		// 审核状态
		String commit = getStrParameter("commit");
		// 定义用户选择条件
		String userId = "";
		String realName = "";
		String phone = "";
		String idCard = "";
		String jkdata = "";
		String xypf = "";
		String off = "";
		logger.info("temp:" + temp + "startDate:" + startDate + " endDate" + endDate);
		if (temp == 1) {

			userId = tempVelue;
		}

		if (temp == 2) {

			realName = tempVelue;
		}
		if (temp == 3) {

			phone = tempVelue;
		}
		if (temp == 4) {

			idCard = tempVelue;
		}
		if (temp == 5) {

			jkdata = tempVelue;
		}
		if (temp == 6) {

			xypf = tempVelue;
		}
		if (temp == 7) {

			off = 7 + "";
		}

		// 默认第一页
		int curPage = getIntParameter("curPage", 1);
		DBPage page = new DBPage(curPage, 15);
		// 一组
		String shenhezu1 = jbdcmsService.getPJGZSHENHE1();
		String shenhezuzz1 = jbdcmsService.getPJGZSHENHEZZ1();
		String shzu1[] = shenhezu1.split(",");

		// 二组
		String shenhezu2 = jbdcmsService.getPJGZSHENHE2();
		String shenhezuzz2 = jbdcmsService.getPJGZSHENHEZZ2();
		String shzu2[] = shenhezu2.split(",");

		// 三组
		String shenhezu3 = jbdcmsService.getPJGZSHENHE3();
		String shenhezuzz3 = jbdcmsService.getPJGZSHENHEZZ3();
		String shzu3[] = shenhezu3.split(",");

		// 四组
		String shenhezu4 = jbdcmsService.getPJGZSHENHE4();
		String shenhezuzz4 = jbdcmsService.getPJGZSHENHEZZ4();
		String shzu4[] = shenhezu4.split(",");

		if (cmsuserid == 6 || cmsuserid == 8 || cmsuserid == 222 || cmsuserid == 888 || cmsuserid == 9999
				|| cmsuserid == 8888 || cmsuserid == 135699  || cmsuserid == 2038) {
			page = jbdcmsService.getRecordListTwo(curPage, 15, userId, realName, phone, xypf, cmsuserid, startDate,
					endDate, commit, off);

		}
		// 一组
		else if (shenhezuzz1.equals(cmsuserid + "")) {
			page = jbdcmsService.getRecordListTwoSH1(curPage, 15, userId, realName, phone, xypf, cmsuserid, startDate,
					endDate, commit, shzu1, off);
		}
		// 二组
		else if (shenhezuzz2.equals(cmsuserid + "")) {
			page = jbdcmsService.getRecordListTwoSH2(curPage, 15, userId, realName, phone, xypf, cmsuserid, startDate,
					endDate, commit, shzu2, off);
		}
		// 三组
		else if (shenhezuzz3.equals(cmsuserid + "")) {
			page = jbdcmsService.getRecordListTwoSH3(curPage, 15, userId, realName, phone, xypf, cmsuserid, startDate,
					endDate, commit, shzu3, off);
		}
		// 四组
		else if (shenhezuzz4.equals(cmsuserid + "")) {
			page = jbdcmsService.getRecordListTwoSH4(curPage, 15, userId, realName, phone, xypf, cmsuserid, startDate,
					endDate, commit, shzu4, off);
		} else {
			page = jbdcmsService.getRecordListTwoSH(curPage, 15, userId, realName, phone, xypf, cmsuserid, startDate,
					endDate, commit, off);
		}

		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp", temp);
		row.set("tempvalu", tempVelue);
		row.set("commit ", commit);
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());

		return null;
	}

	// 第三步审核列表
	public ActionResult doGetRecordListThree() throws Exception {
		logger.info("进入借款审核方法内3");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl");
		String startDate1 = getStrParameter("startDate");
		String endDate1 = getStrParameter("endDate");
		String startDate = "";
		String endDate = "";
		if (!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)) {
			String[] sourceStrArray1 = startDate1.split("-");
			String[] sourceStrArray2 = endDate1.split("-");
			startDate = sourceStrArray1[2] + "-" + sourceStrArray1[1] + "-" + sourceStrArray1[0];
			endDate = sourceStrArray2[2] + "-" + sourceStrArray2[1] + "-" + sourceStrArray2[0];
		}
		// 审核状态
		String commit = getStrParameter("commit");
		// 定义用户选择条件
		String userId = "";
		String realName = "";
		String phone = "";
		String idCard = "";
		String xypf = "";
		String off = "";
		logger.info("temp:" + temp + "startDate:" + startDate + " endDate" + endDate);
		if (temp == 1) {

			userId = tempVelue;
		}
		if (temp == 2) {

			realName = tempVelue;
		}
		if (temp == 3) {

			phone = tempVelue;
		}
		if (temp == 4) {

			idCard = tempVelue;
		}
		if (temp == 5) {

			xypf = tempVelue;
		}
		if (temp == 6) {

			off = 6 + "";
		}
		// 默认第一页
		int curPage = getIntParameter("curPage", 1);
		DBPage page = new DBPage(curPage, 15);

		// 一组
		String shenhezu1 = jbdcmsService.getPJGZSHENHE1();
		String shenhezuzz1 = jbdcmsService.getPJGZSHENHEZZ1();
		String shzu1[] = shenhezu1.split(",");

		// 二组
		String shenhezu2 = jbdcmsService.getPJGZSHENHE2();
		String shenhezuzz2 = jbdcmsService.getPJGZSHENHEZZ2();
		String shzu2[] = shenhezu2.split(",");

		// 三组
		String shenhezu3 = jbdcmsService.getPJGZSHENHE3();
		String shenhezuzz3 = jbdcmsService.getPJGZSHENHEZZ3();
		String shzu3[] = shenhezu3.split(",");

		// 四组
		String shenhezu4 = jbdcmsService.getPJGZSHENHE4();
		String shenhezuzz4 = jbdcmsService.getPJGZSHENHEZZ4();
		String shzu4[] = shenhezu4.split(",");

		if (cmsuserid == 6 || cmsuserid == 8 || cmsuserid == 222 || cmsuserid == 888 || cmsuserid == 9999
				|| cmsuserid == 8888 || cmsuserid == 135699  || cmsuserid == 2038) {
			page = jbdcmsService.getRecordListThree(curPage, 15, userId, realName, phone, xypf, cmsuserid, startDate,
					endDate, commit, off,0);

		}
		// 一组
		else if (shenhezuzz1.equals(cmsuserid + "")) {
			page = jbdcmsService.getRecordListThreeSH1(curPage, 15, userId, realName, phone, xypf, cmsuserid, startDate,
					endDate, commit, shzu1, off,0);
		}
		// 二组
		else if (shenhezuzz2.equals(cmsuserid + "")) {
			page = jbdcmsService.getRecordListThreeSH2(curPage, 15, userId, realName, phone, xypf, cmsuserid, startDate,
					endDate, commit, shzu2, off,0);
		}
		// 三组
		else if (shenhezuzz3.equals(cmsuserid + "")) {
			page = jbdcmsService.getRecordListThreeSH3(curPage, 15, userId, realName, phone, xypf, cmsuserid, startDate,
					endDate, commit, shzu3, off,0);
		}
		// 四组
		else if (shenhezuzz4.equals(cmsuserid + "")) {
			page = jbdcmsService.getRecordListThreeSH4(curPage, 15, userId, realName, phone, xypf, cmsuserid, startDate,
					endDate, commit, shzu4, off,0);
		} else {
			page = jbdcmsService.getRecordListThreeSH(curPage, 15, userId, realName, phone, xypf, cmsuserid, startDate,
					endDate, commit, off,0);
		}
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp", temp);
		row.set("tempvalu", tempVelue);
		row.set("commit ", commit);
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());
		return null;
	}
	
	public ActionResult doGetRecordListOldThree() throws Exception {
		logger.info("进入借款审核方法内3");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl");
		String startDate1 = getStrParameter("startDate");
		String endDate1 = getStrParameter("endDate");
		String startDate = "";
		String endDate = "";
		if (!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)) {
			String[] sourceStrArray1 = startDate1.split("-");
			String[] sourceStrArray2 = endDate1.split("-");
			startDate = sourceStrArray1[2] + "-" + sourceStrArray1[1] + "-" + sourceStrArray1[0];
			endDate = sourceStrArray2[2] + "-" + sourceStrArray2[1] + "-" + sourceStrArray2[0];
		}
		// 审核状态
		String commit = getStrParameter("commit");
		// 定义用户选择条件
		String userId = "";
		String realName = "";
		String phone = "";
		String idCard = "";
		String xypf = "";
		String off = "";
		logger.info("temp:" + temp + "startDate:" + startDate + " endDate" + endDate);
		if (temp == 1) {

			userId = tempVelue;
		}
		if (temp == 2) {

			realName = tempVelue;
		}
		if (temp == 3) {

			phone = tempVelue;
		}
		if (temp == 4) {

			idCard = tempVelue;
		}
		if (temp == 5) {

			xypf = tempVelue;
		}
		if (temp == 6) {

			off = 6 + "";
		}
		int type = 1;
		// 默认第一页
		int curPage = getIntParameter("curPage", 1);
		DBPage page = new DBPage(curPage, 15);

		// 一组
		String shenhezu1 = jbdcmsService.getPJGZSHENHE1();
		String shenhezuzz1 = jbdcmsService.getPJGZSHENHEZZ1();
		String shzu1[] = shenhezu1.split(",");

		// 二组
		String shenhezu2 = jbdcmsService.getPJGZSHENHE2();
		String shenhezuzz2 = jbdcmsService.getPJGZSHENHEZZ2();
		String shzu2[] = shenhezu2.split(",");

		// 三组
		String shenhezu3 = jbdcmsService.getPJGZSHENHE3();
		String shenhezuzz3 = jbdcmsService.getPJGZSHENHEZZ3();
		String shzu3[] = shenhezu3.split(",");

		// 四组
		String shenhezu4 = jbdcmsService.getPJGZSHENHE4();
		String shenhezuzz4 = jbdcmsService.getPJGZSHENHEZZ4();
		String shzu4[] = shenhezu4.split(",");

		if (cmsuserid == 6 || cmsuserid == 8 || cmsuserid == 222 || cmsuserid == 888 || cmsuserid == 9999
				|| cmsuserid == 8888 || cmsuserid == 135699  || cmsuserid == 2038) {
			page = jbdcmsService.getRecordListThree(curPage, 15, userId, realName, phone, xypf, cmsuserid, startDate,
					endDate, commit, off,type);

		}
		// 一组
		else if (shenhezuzz1.equals(cmsuserid + "")) {
			page = jbdcmsService.getRecordListThreeSH1(curPage, 15, userId, realName, phone, xypf, cmsuserid, startDate,
					endDate, commit, shzu1, off,type);
		}
		// 二组
		else if (shenhezuzz2.equals(cmsuserid + "")) {
			page = jbdcmsService.getRecordListThreeSH2(curPage, 15, userId, realName, phone, xypf, cmsuserid, startDate,
					endDate, commit, shzu2, off,type);
		}
		// 三组
		else if (shenhezuzz3.equals(cmsuserid + "")) {
			page = jbdcmsService.getRecordListThreeSH3(curPage, 15, userId, realName, phone, xypf, cmsuserid, startDate,
					endDate, commit, shzu3, off,type);
		}
		// 四组
		else if (shenhezuzz4.equals(cmsuserid + "")) {
			page = jbdcmsService.getRecordListThreeSH4(curPage, 15, userId, realName, phone, xypf, cmsuserid, startDate,
					endDate, commit, shzu4, off,type);
		} else {
			page = jbdcmsService.getRecordListThreeSH(curPage, 15, userId, realName, phone, xypf, cmsuserid, startDate,
					endDate, commit, off,type);
		}
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp", temp);
		row.set("tempvalu", tempVelue);
		row.set("commit ", commit);
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());
		return null;
	}

	// 申请结果提交GetShenhOne
	public ActionResult doGetShenhOne() throws Exception {
		logger.info("提交第一步审核");
		// buildPushObject_ios_cid("18171adc0306365ef8f");
		// 要去数据库中建一个表，添加一个用户就+1，然后进行循环
		
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		

		int userid = SessionHelper.getInt("cmsuserid", getSession());
		if (userid == 0) {
			jsonObject.put("error", 1);
			jsonObject.put("msg", "Vui lòng đăng nhập trước");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		logger.info("请求ID:" + userid);
		// 一组
		String shenhezuzz1 = jbdcmsService.getPJGZSHENHEZZ1();
		// 二组
		String shenhezuzz2 = jbdcmsService.getPJGZSHENHEZZ2();
		// 三组
		String shenhezuzz3 = jbdcmsService.getPJGZSHENHEZZ3();
		// 四组
		String shenhezuzz4 = jbdcmsService.getPJGZSHENHEZZ4();
		DecimalFormat famt = new DecimalFormat("###,###");
		String reid = getStrParameter("reid");
		String i_id = getStrParameter("i_id");
		String remark = getStrParameter("remark").replace("&nbsp;", " ");
		String jymoney = getStrParameter("jymoney");
		String newjymoney = "";
		int nn = Integer.parseInt(jymoney + "0000");
		newjymoney = famt.format(nn);
		int ph = getIntParameter("ph");
		int con = getIntParameter("con");
		int job = getIntParameter("job");
		int yh = getIntParameter("yh");
		int school = getIntParameter("school");
		int bhstate = getIntParameter("bhstate", 0);
		String cg_type = getStrParameter("cg_type");
		logger.info("reid: " + reid + " remark:" + remark + " jymoney:" + jymoney + "  ph:" + ph + " con:" + con
				+ " job:" + job + " i_id:" + i_id + "bhstate:" + bhstate + " cg_type" + cg_type);
		// 查询此借款项目是否为审核中
		int sfshjk = jbdcmsService.getJKSHinfoOne(i_id);
		int pipei = jbdcmsService.getJKSHpipei(i_id);
		if (sfshjk != 0) {
			jsonObject.put("error", 1);
			jsonObject.put("msg", "此项目已不在审核中状态，请刷新");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		int cl_status = 0;
		if (bhstate != 0) {
			cl_status = 3;
		} else {
			cl_status = 1;
		}
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		if (cl_status == 1) {
			List<DataRow> list = jbdcmsService.getAllUserJK(reid);
			int gzyuqts = jbdcmsService.getYQTSGZ();
			int gzyuqcs = jbdcmsService.getYQCSGZ();
			int shqx = jbdcmsService.getSHQX(userid);
			int pingfen = jbdcmsService.getPingFen(i_id);
			int onesh = jbdcmsService.getONESH(i_id);
			int bnfkbz = jbdcmsService.getBNFKBZ(userid);
			// 有催收备注不能借款
			if (bnfkbz > 0 && shqx == 0) {
				jsonObject.put("error", 1);
				jsonObject.put("msg", "vui lòng gửi Trưởng phòng Thẩm định xét duyệt！");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			// 更改身份证号码带来的评分变动
			if ((pingfen == -1 || pingfen == -2) && shqx == 0) {
				jsonObject.put("error", 1);
				jsonObject.put("msg", "vui lòng gửi Trưởng phòng Thẩm định xét duyệt！");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			// 检测所有借款，然后进行匹配
			for (int i = 0; i < list.size(); i++) {
				DataRow row = list.get(i);
				int yuqts = row.getInt("yuq_ts");
				int yuqjs = 0;
				if (yuqts > 0) {
					yuqjs++;
				}
				if (yuqts >= gzyuqts && shqx == 0) {
					jsonObject.put("error", 1);
					jsonObject.put("msg", "Số ngày quá hạn đến hiện tại " + yuqts + ",vượt quá limit" + gzyuqts
							+ " ngày, vui lòng gửi Trưởng phòng Thẩm định xét duyệt！");
					this.getWriter().write(jsonObject.toString());
					return null;
				} else if (yuqjs >= gzyuqcs && shqx == 0) {
					jsonObject.put("error", 1);
					jsonObject.put("msg", "Số lần quá hạn đến hiện tại " + yuqts + ",vượt quá limit" + gzyuqts
							+ " lần limit, vui lòng gửi Trưởng phòng Thẩm định xét duyệt!");
					this.getWriter().write(jsonObject.toString());
					return null;
				}
			}
			DataRow row = new DataRow();
			row.set("id", i_id);
			row.set("cl_status", cl_status);
			row.set("cl_yj", remark);
			if (shenhezuzz1.equals(userid + "") || shenhezuzz2.equals(userid + "") || shenhezuzz3.equals(userid + "")
					|| shenhezuzz4.equals(userid + "")) {
				row.set("cl_ren", onesh);
			} else {
				row.set("cl_ren", userid);
			}
			row.set("jyfk_money", newjymoney);
			row.set("cl_time", fmtrq.format(calendar.getTime()));
			jbdcmsService.updateUserJk(row);
			DataRow row3 = new DataRow();
			row3.set("userid", reid);
			row3.set("title", "Thông báo thẩm định");// 审核通知
			row3.set("neirong", "Thẩm định lần 1 đã thông qua, xin vui lòng chờ đợi bước tiếp theo."); // 初审审核通过，请耐心等待！
			row3.set("fb_time", fmtrq.format(calendar.getTime()));
			jbdcmsService.insertUserMsg(row3);
		} else {
			if (bhstate == 4 || bhstate == 5) {

				if ((pipei == 3 || pipei == 2) && !shenhezuzz1.equals(userid + "") && !shenhezuzz2.equals(userid + "")
						&& !shenhezuzz3.equals(userid + "") && !shenhezuzz4.equals(userid + "") && userid != 8
						&& userid != 6) {
					jsonObject.put("error", 1);
					jsonObject.put("msg", "Cần chủ quản duyệt lại !");
					this.getWriter().write(jsonObject.toString());
					return null;
				}
				List<DataRow> list = jbdcmsService.getAllUserJK(reid);
				int gzyuqts = jbdcmsService.getYQTSGZJJ();
				int gzyuqcs = jbdcmsService.getYQCSGZJJ();
				int shqx = jbdcmsService.getSHQX(userid);
				for (int i = 0; i < list.size(); i++) {
					DataRow row = list.get(i);
					int yuqts = row.getInt("yuq_ts");
					int yuqjs = 0;
					if (yuqts > 0) {
						yuqjs++;
					}
					if (yuqts < gzyuqts && shqx == 0) {
						jsonObject.put("error", 1);
						jsonObject.put("msg", "Quyền hạn từ trưởng phòng thẩm định trở lên mới được từ chối KH này ！");
						this.getWriter().write(jsonObject.toString());
						return null;
					} else if (yuqjs < gzyuqcs && shqx == 0) {
						jsonObject.put("error", 1);
						jsonObject.put("msg", "Quyền hạn từ trưởng phòng thẩm định trở lên mới được từ chối KH này !");
						this.getWriter().write(jsonObject.toString());
						return null;
					}
				}
			}
			DataRow row = new DataRow();
			row.set("id", i_id);
			row.set("cl_status", cl_status);
			row.set("cl_yj", remark);
			row.set("cl_ren", userid);
			row.set("cl_time", fmtrq.format(calendar.getTime()));
			jbdcmsService.updateUserJk(row);
			if (bhstate == 3) {
				DataRow row3 = new DataRow();
				row3.set("userid", reid);
				row3.set("title", "Thông báo thẩm định");// 审核通知
				row3.set("neirong", remark); // 抱歉，本次审核未通过，可能由以下一个或多个原因造成：
												// 1.还款能力不足，负债比过高；2.工作和收入稳定性不足；3.信用记录不良；4.未按要求提供资料或提供的资料不够详尽；5.系统综合评分不足。
				row3.set("fb_time", fmtrq.format(calendar.getTime()));
				jbdcmsService.insertUserMsg(row3);
			} else if (bhstate == 4) {
				DataRow row11 = new DataRow();
				row11.set("id", i_id);
				row11.set("cl_yj", "Limit 15 days" + " " + remark);
				jbdcmsService.updateUserJk(row11);
				DataRow row3 = new DataRow();
				row3.set("userid", reid);
				row3.set("title", "Thông báo thẩm định");// 审核通知
				row3.set("neirong",
						"Rất tiếc, khoản vay lần này không được chấp nhận, có thể là do một hoặc nhiều lý do sau: 1. Khả năng hoàn trả không đủ, tỷ lệ nợ quá cao; 2. Tính ổn định của công việc và thu nhập không đủ; 3. Lịch sử tín dụng xấu; 4. Không cung cấp thông tin theo yêu cầu hoặc các thông tin được cung cấp không đầy đủ; 5. Số điểm đánh giá từ hệ thống không đủ.");// 抱歉，本次审核未通过，可能由以下一个或多个原因造成：
																																																																																													// 1.还款能力不足，负债比过高；2.工作和收入稳定性不足；3.信用记录不良；4.未按要求提供资料或提供的资料不够详尽；5.系统综合评分不足。
				row3.set("fb_time", fmtrq.format(calendar.getTime()));
				jbdcmsService.insertUserMsg(row3);
			} else if (bhstate == 5) {
				DataRow row11 = new DataRow();
				row11.set("id", i_id);
				row11.set("cl_yj", "Danh sách đen" + " " + remark);
				jbdcmsService.updateUserJk(row11);
				DataRow row3 = new DataRow();
				row3.set("userid", reid);
				row3.set("title", "Thông báo thẩm định");// 审核通知
				row3.set("neirong",
						"Rất tiếc, khoản vay lần này không được chấp nhận, có thể là do một hoặc nhiều lý do sau: 1. Khả năng hoàn trả không đủ, tỷ lệ nợ quá cao; 2. Tính ổn định của công việc và thu nhập không đủ; 3. Lịch sử tín dụng xấu; 4. Không cung cấp thông tin theo yêu cầu hoặc các thông tin được cung cấp không đầy đủ; 5. Số điểm đánh giá từ hệ thống không đủ.");// 抱歉，本次审核未通过，可能由以下一个或多个原因造成：
																																																																																													// 1.还款能力不足，负债比过高；2.工作和收入稳定性不足；3.信用记录不良；4.未按要求提供资料或提供的资料不够详尽；5.系统综合评分不足。
				row3.set("fb_time", fmtrq.format(calendar.getTime()));
				jbdcmsService.insertUserMsg(row3);
			} else if (bhstate == 6) {
				DataRow row11 = new DataRow();
				row11.set("id", i_id);
				row11.set("cl_yj", "KH chua co nhu cau vay");
				jbdcmsService.updateUserJk(row11);
				DataRow row3 = new DataRow();
				row3.set("userid", reid);
				row3.set("title", "Thông báo thẩm định");// 审核通知
				row3.set("neirong", "KH chua co nhu cau vay.");// 抱歉，本次审核未通过，可能由以下一个或多个原因造成：
																// 1.还款能力不足，负债比过高；2.工作和收入稳定性不足；3.信用记录不良；4.未按要求提供资料或提供的资料不够详尽；5.系统综合评分不足。
				row3.set("fb_time", fmtrq.format(calendar.getTime()));
				jbdcmsService.insertUserMsg(row3);
			} else {
				DataRow row3 = new DataRow();
				row3.set("userid", reid);
				row3.set("title", "Thông báo thẩm định");// 审核通知
				row3.set("neirong",
						"Rất tiếc, khoản vay lần này không được chấp nhận, có thể là do một hoặc nhiều lý do sau: 1. Khả năng hoàn trả không đủ, tỷ lệ nợ quá cao; 2. Tính ổn định của công việc và thu nhập không đủ; 3. Lịch sử tín dụng xấu; 4. Không cung cấp thông tin theo yêu cầu hoặc các thông tin được cung cấp không đầy đủ; 5. Số điểm đánh giá từ hệ thống không đủ.");// 抱歉，本次审核未通过，可能由以下一个或多个原因造成：
																																																																																													// 1.还款能力不足，负债比过高；2.工作和收入稳定性不足；3.信用记录不良；4.未按要求提供资料或提供的资料不够详尽；5.系统综合评分不足。
				row3.set("fb_time", fmtrq.format(calendar.getTime()));
				jbdcmsService.insertUserMsg(row3);
			}
		}
		if (cg_type.equals("0") && bhstate == 0) {
			DataRow row2 = new DataRow();
			row2.set("id", reid);
			row2.set("isshenfen", ph % 3);
			row2.set("islianxi", con % 3);
			row2.set("isjop", job % 3);
			row2.set("yhbd", yh % 3);
			row2.set("isschool", school % 3);
			jbdcmsService.updateUserPersonInfo(row2);

		} else if (cg_type.equals("0") && bhstate != 0) {
			DataRow row2 = new DataRow();
			if (ph == 3) {
				ph = 1;
			}
			if (con == 3) {
				con = 1;
			}
			if (job == 3) {
				job = 1;
			}
			if (school == 3) {
				school = 1;
			}
			if (yh == 3) {
				yh = 1;
			}
			row2.set("id", reid);
			row2.set("isshenfen", ph % 3);
			row2.set("islianxi", con % 3);
			row2.set("isjop", job % 3);
			row2.set("yhbd", yh % 3);
			row2.set("isschool", school % 3);
			jbdcmsService.updateUserPersonInfo(row2);
			
			//把用户身份证更新为0
			DataRow ufrow=jbdcmsService.getUserfianceDataRow(reid,"");
			if(null != ufrow ) {
				String idno =ufrow.getString("idno");
				if(StringHelper.isEmpty(idno)) {
					DataRow rowfin = new DataRow();
					rowfin.set("userid", reid);
					rowfin.set("idno", 0);
					jbdcmsService.updateChangeUserXXSFXX(rowfin);
				}
			}
			
		}

		// 将用户拉入黑名单
		if (bhstate == 5) {
			DataRow row3 = new DataRow();
			row3.set("id", reid);
			row3.set("heihu_zt", 1);
			jbdcmsService.updateUserInfoH(row3);
		}
		jsonObject.put("error", 0);
		this.getWriter().write(jsonObject.toString());
		return null;

	}

	// 提交第二步审核
	public ActionResult doGetShenhTwo() throws Exception {

		logger.info("进入到审核第二步");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		// 一组
		String shenhezuzz1 = jbdcmsService.getPJGZSHENHEZZ1();
		// 二组
		String shenhezuzz2 = jbdcmsService.getPJGZSHENHEZZ2();
		// 三组
		String shenhezuzz3 = jbdcmsService.getPJGZSHENHEZZ3();
		// 四组
		String shenhezuzz4 = jbdcmsService.getPJGZSHENHEZZ4();
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		DecimalFormat famt = new DecimalFormat("###,###");
		String rec_id = getStrParameter("rec_id");
		String kjmoney = getStrParameter("kjmoney");
		int  nn = Integer.parseInt(kjmoney + "0000");
		String newjymoney = "";
		// 查询此借款项目是否为审核中
		int sfshjkone = jbdcmsService.getJKSHinfoOne(rec_id);
		int sfshjk = jbdcmsService.getJKSHinfoTwo(rec_id);
		// 一审不为1
		if (sfshjkone != 1) {
			jsonObject.put("error", -2);
			jsonObject.put("msg", "Người dùng này không có ở đây, vui lòng làm mới");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		if (sfshjk != 0) {
			jsonObject.put("error", -2);
			jsonObject.put("msg", "Người dùng này không có ở đây, vui lòng làm mới");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		
		String state = getStrParameter("state");
		String remarks = getStrParameter("remarks").replace("&nbsp;", " ");
		String userid = getStrParameter("user_id");
		int code = 0;
		// 审核审批金额最大值
		int pingfen = jbdcmsService.getPingFen(rec_id);
		String jkqx = getStrParameter("jkqx");
		
		double zuidamoney = 0;
		if (pingfen == 0) {
			zuidamoney = Integer.parseInt(jbdcmsService.getPJGZ8().replace(",", ""));
		} else if (pingfen == -1) {
			zuidamoney = Integer.parseInt(jbdcmsService.getPJGZ9().replace(",", ""));
		} else if (pingfen == 1) {
			zuidamoney = Integer.parseInt(jbdcmsService.getPJGZ10().replace(",", ""));
		} else if (pingfen == 2) {
			zuidamoney = Integer.parseInt(jbdcmsService.getPJGZ11().replace(",", ""));
		} else if (pingfen == 3) {
			zuidamoney = Integer.parseInt(jbdcmsService.getPJGZ12().replace(",", ""));
		}else {
			zuidamoney = userMoneyBase.getUMBasepingfencmndedu(pingfen);
		}
		
		int cgjkcs = jbdcmsService.getCGJKCS(userid);
		if (jkqx.equals("7") || jkqx.equals("14")) {
			zuidamoney = zuidamoney + cgjkcs * 500000;
		} else {
			zuidamoney = zuidamoney + cgjkcs * 1000000;
		}
		if (jkqx.equals("7") || jkqx.equals("14")) {
			if (zuidamoney > Integer.parseInt(jbdcmsService.getPJGZ18().replace(",", ""))) {
				zuidamoney = Integer.parseInt(jbdcmsService.getPJGZ18().replace(",", ""));
			}
			if (nn > zuidamoney) {
				jsonObject.put("error", -3);
				jsonObject.put("msg", "Số tiền giải ngân tối đa của KH này là " + famt.format(zuidamoney)
						+ ",Nếu nhận thấy có thể giải ngân nhiều hơn, vui lòng gửi trưởng phòng Thẩm định phê duyệt!");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
		}
		int shqx = jbdcmsService.getSHQX(cmsuserid);
		if (nn > zuidamoney && shqx == 0) {
			jsonObject.put("error", -3);
			jsonObject.put("msg", "Số tiền giải ngân tối đa của KH này là " + famt.format(zuidamoney)
					+ ",Nếu nhận thấy có thể giải ngân nhiều hơn, vui lòng gửi trưởng phòng Thẩm định phê duyệt!");
			this.getWriter().write(jsonObject.toString());
			return null;
		}

		DataRow dataRow2 = jbdcmsService.getUserInfo(Integer.parseInt(userid));
		String name = dataRow2.getString("username").substring(5, 8);
		String name1 = dataRow2.getString("username").substring(0, 4);
		if (name.equals("AND")) {
			code = 1;
		} else {
			code = 2;
		}

		System.out.println("用户ID:" + userid);
		int fklv = 75;
		
		// 20190729 修改 lic
		int jk_dataType =0;
		if (jkqx.equals("15")) {
			jk_dataType = 1;
		} else if (jkqx.equals("30")) {
			jk_dataType =2;
		}
		
		int jk_lx=  userMoneyBase.getUMBaseCalculateProductInterest(nn, jk_dataType, Integer.parseInt(userid), name1);
		if(jk_lx > 0) {
			fklv = 100 - jk_lx;
		}
		
		int moneycode = jbdcmsService.getMoneyCode(userid);
		String newsjds = "";
		int jj =  nn * fklv / 100;
				
		newsjds = famt.format(jj);
		newjymoney = famt.format(nn);
		
		logger.info(" rec_id" + rec_id + " kjmoney" + kjmoney + " state" + state + " remarks" + remarks); // 得到的信息
		if (state.equals("1")) { // 审核成功
			if (pingfen == -1 || pingfen == -2 && shqx == 0) {
				jsonObject.put("error", 1);
				jsonObject.put("msg", "vui lòng gửi Trưởng phòng Thẩm định xét duyệt！");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			if (jkqx.equals("0")) {
				jsonObject.put("error", -2);
				jsonObject.put("msg", "Không có thời hạn vay, vui lòng đề xuất IT thêm thời hạn vay");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			DataRow row = new DataRow();
			row.set("id", rec_id);
			row.set("cl02_status", "1");
			row.set("cl02_yj", remarks);
			int twosh = jbdcmsService.getONESH(rec_id);
			if (shenhezuzz1.equals(cmsuserid + "") || shenhezuzz2.equals(cmsuserid + "")
					|| shenhezuzz3.equals(cmsuserid + "") || shenhezuzz4.equals(cmsuserid + "")) {
				row.set("cl02_ren", twosh);
			} else {
				row.set("cl02_ren", cmsuserid);
			}
			row.set("sjsh_money", newjymoney);
			row.set("sjds_money", newsjds);
			row.set("lx", famt.format(nn * (100 - fklv) / 100));
			row.set("cl02_time", fmtrq.format(calendar.getTime()));
			jbdcmsService.updateUserJk(row);

			DataRow row3 = new DataRow();
			row3.set("userid", userid);
			row3.set("title", "Thông báo thẩm định");// 审核通知
			row3.set("neirong",
					"Đề xuất vay của bạn đã hoàn thành bước xét duyệt sơ bộ, vui lòng đăng tải video theo yêu cầu để tiếp tục được xét duyệt");// 您的借款申请我们已初步审核完成，请根据要求上传视频审核
			row3.set("fb_time", fmtrq.format(calendar.getTime()));
			jbdcmsService.insertUserMsg(row3);
			
			String userName = dataRow2.getString("username");
			String appName = "OCEAN";
			userName = userName.substring(0, 4);
			if (userName.equals("OCEAN")) {
				appName = "OCEAN";
			}
			String content = "";
			// 发送上传视频短信提醒
			if (moneycode == 0) {
				content = "[{\"PhoneNumber\":\"" + dataRow2.getString("mobilephone") + "\",\"Message\":\"" + appName
						+ " xin thong bao: Khoan vay cua ban da duoc chap thuan, so tien vay lan nay la " + newjymoney
						+ " VND. Vui long lam theo huong dan tren App de nhan duoc khoan vay.\",\"SmsGuid\":\""
						+ dataRow2.getString("mobilephone") + "\",\"ContentType\":1}]";
			} else if (moneycode == 1) {
				content = "[{\"PhoneNumber\":\"" + dataRow2.getString("mobilephone") + "\",\"Message\":\"" + appName
						+ " xin thong bao: Khoan vay cua ban da duoc chap thuan, so tien vay lan nay la " + newsjds
						+ " VND. Vui long lam theo huong dan tren App de nhan duoc khoan vay.\",\"SmsGuid\":\""
						+ dataRow2.getString("mobilephone") + "\",\"ContentType\":1}]";
			}

			String con = URLEncoder.encode(content, "utf-8");
			SendMsg sendMsg = new SendMsg();
			//String returnString = SendMsg.sendMessageByGet(con, dataRow2.getString("mobilephone"));
		}
		if (state.equals("2") || state.equals("3")) {
			List<DataRow> list = jbdcmsService.getAllUserJK(rec_id);
			int gzyuqts = jbdcmsService.getYQTSGZJJ();
			int gzyuqcs = jbdcmsService.getYQCSGZJJ();
			for (int i = 0; i < list.size(); i++) {
				DataRow row = list.get(i);
				int yuqts = row.getInt("yuq_ts");
				int yuqjs = 0;
				if (yuqts > 0) {
					yuqjs++;
				}
				if (yuqts < gzyuqts && shqx == 0) {
					jsonObject.put("error", 1);
					jsonObject.put("msg", "Quyền hạn từ trưởng phòng thẩm định trở lên mới được từ chối KH này ！");
					this.getWriter().write(jsonObject.toString());
					return null;
				} else if (yuqjs < gzyuqcs && shqx == 0) {
					jsonObject.put("error", 1);
					jsonObject.put("msg", "Quyền hạn từ trưởng phòng thẩm định trở lên mới được từ chối KH này !");
					this.getWriter().write(jsonObject.toString());
					return null;
				}
			}
		}
		if (state.equals("2")) { // 审核失败
			DataRow row2 = new DataRow();
			row2.set("id", rec_id);
			row2.set("cl02_status", "3");
			row2.set("cl02_yj", remarks);
			row2.set("cl02_ren", cmsuserid);
			row2.set("cl02_time", fmtrq.format(calendar.getTime()));
			jbdcmsService.updateUserJk(row2);
			DataRow row3 = new DataRow();
			row3.set("userid", userid);
			row3.set("title", "Thông báo thẩm định");// 审核通知
			row3.set("neirong",
					"Rất tiếc, khoản vay lần này không được chấp nhận, có thể là do một hoặc nhiều lý do sau: 1. Khả năng hoàn trả không đủ, tỷ lệ nợ quá cao; 2. Tính ổn định của công việc và thu nhập không đủ; 3. Lịch sử tín dụng xấu; 4. Không cung cấp thông tin theo yêu cầu hoặc các thông tin được cung cấp không đầy đủ; 5. Số điểm đánh giá từ hệ thống không đủ."); // 抱歉，本次审核未通过，可能由以下一个或多个原因造成：
																																																																																													// //
																																																																																													// 1.还款能力不足，负债比过高；2.工作和收入稳定性不足；3.信用记录不良；4.未按要求提供资料或提供的资料不够详尽；5.系统综合评分不足。
			row3.set("fb_time", fmtrq.format(calendar.getTime()));
			jbdcmsService.insertUserMsg(row3);

		}
		if (state.equals("3")) { // 拉入黑名单
			DataRow row5 = new DataRow();
			row5.set("id", rec_id);
			row5.set("cl02_status", "3");
			row5.set("cl02_yj", "Danh sách đen" + " " + remarks);
			row5.set("cl02_ren", cmsuserid);
			row5.set("cl02_time", fmtrq.format(calendar.getTime()));
			jbdcmsService.updateUserJk(row5);
			DataRow row3 = new DataRow();
			row3.set("id", userid);
			row3.set("heihu_zt", 1);
			jbdcmsService.updateUserInfoH(row3);

			DataRow row6 = new DataRow();
			row6.set("userid", userid);
			row6.set("title", "Thông báo thẩm định");// 审核通知
			row6.set("neirong",
					"Rất tiếc, khoản vay lần này không được chấp nhận, có thể là do một hoặc nhiều lý do sau: 1. Khả năng hoàn trả không đủ, tỷ lệ nợ quá cao; 2. Tính ổn định của công việc và thu nhập không đủ; 3. Lịch sử tín dụng xấu; 4. Không cung cấp thông tin theo yêu cầu hoặc các thông tin được cung cấp không đầy đủ; 5. Số điểm đánh giá từ hệ thống không đủ."); // 抱歉，本次审核未通过，可能由以下一个或多个原因造成：
																																																																																													// //
																																																																																													// 1.还款能力不足，负债比过高；2.工作和收入稳定性不足；3.信用记录不良；4.未按要求提供资料或提供的资料不够详尽；5.系统综合评分不足。
			row6.set("fb_time", fmtrq.format(calendar.getTime()));
			jbdcmsService.insertUserMsg(row6);

		}
		jsonObject.put("error", 1);
		this.getWriter().write(jsonObject.toString());
		return null;
	}

	// 提交第二步审核
	public ActionResult doGetShenhTwoH5() throws Exception {

		logger.info("进入到审核第二步");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		// 一组
		String shenhezuzz1 = jbdcmsService.getPJGZSHENHEZZ1();
		// 二组
		String shenhezuzz2 = jbdcmsService.getPJGZSHENHEZZ2();
		// 三组
		String shenhezuzz3 = jbdcmsService.getPJGZSHENHEZZ3();
		// 四组
		String shenhezuzz4 = jbdcmsService.getPJGZSHENHEZZ4();
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		DecimalFormat famt = new DecimalFormat("###,###");
		String rec_id = getStrParameter("rec_id");
		String kjmoney = getStrParameter("kjmoney");
		int  nn = Integer.parseInt(kjmoney + "0000");
		String newjymoney = "";
		// 查询此借款项目是否为审核中
		int sfshjkone = jbdcmsService.getJKSHinfoOne(rec_id);
		int sfshjk = jbdcmsService.getJKSHinfoTwo(rec_id);
		// 一审不为1
		if (sfshjkone != 1) {
			jsonObject.put("error", -2);
			jsonObject.put("msg", "Người dùng này không có ở đây, vui lòng làm mới");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		if (sfshjk != 0) {
			jsonObject.put("error", -2);
			jsonObject.put("msg", "Người dùng này không có ở đây, vui lòng làm mới");
			this.getWriter().write(jsonObject.toString());
			return null;
		}

		String state = getStrParameter("state");
		String remarks = getStrParameter("remarks").replace("&nbsp;", " ");
		String userid = getStrParameter("user_id");
		int code = 0;
		// 审核审批金额最大值
		int pingfen = jbdcmsService.getPingFen(rec_id);
		String jkqx = getStrParameter("jkqx");

		double zuidamoney = 0;
		if (pingfen == 0) {
			zuidamoney = Integer.parseInt(jbdcmsService.getPJGZ8().replace(",", ""));
		} else if (pingfen == -1) {
			zuidamoney = Integer.parseInt(jbdcmsService.getPJGZ9().replace(",", ""));
		} else if (pingfen == 1) {
			zuidamoney = Integer.parseInt(jbdcmsService.getPJGZ10().replace(",", ""));
		} else if (pingfen == 2) {
			zuidamoney = Integer.parseInt(jbdcmsService.getPJGZ11().replace(",", ""));
		} else if (pingfen == 3) {
			zuidamoney = Integer.parseInt(jbdcmsService.getPJGZ12().replace(",", ""));
		}
		int cgjkcs = jbdcmsService.getCGJKCS(userid);
		if (jkqx.equals("7") || jkqx.equals("14")) {
			zuidamoney = zuidamoney + cgjkcs * 500000;
		} else {
			zuidamoney = zuidamoney + cgjkcs * 1000000;
		}
		if (jkqx.equals("7") || jkqx.equals("14")) {
			if (zuidamoney > Integer.parseInt(jbdcmsService.getPJGZ18().replace(",", ""))) {
				zuidamoney = Integer.parseInt(jbdcmsService.getPJGZ18().replace(",", ""));
			}
			if (nn > zuidamoney) {
				jsonObject.put("error", -3);
				jsonObject.put("msg", "Số tiền giải ngân tối đa của KH này là " + famt.format(zuidamoney)
						+ ",Nếu nhận thấy có thể giải ngân nhiều hơn, vui lòng gửi trưởng phòng Thẩm định phê duyệt!");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
		}
		int shqx = jbdcmsService.getSHQX(cmsuserid);
		if (nn > zuidamoney && shqx == 0) {
			jsonObject.put("error", -3);
			jsonObject.put("msg", "Số tiền giải ngân tối đa của KH này là " + famt.format(zuidamoney)
					+ ",Nếu nhận thấy có thể giải ngân nhiều hơn, vui lòng gửi trưởng phòng Thẩm định phê duyệt!");
			this.getWriter().write(jsonObject.toString());
			return null;
		}

		DataRow dataRow2 = jbdcmsService.getUserInfo(Integer.parseInt(userid));
		String name = dataRow2.getString("username").substring(5, 8);
		String name1 = dataRow2.getString("username").substring(0, 4);

		if (name.equals("AND")) {
			code = 1;
		} else {
			code = 2;
		}

		System.out.println("用户ID:" + userid);
		int fklv = 75;
		
		// 20190729 修改 lic
		int jk_dataType =0;
		if (jkqx.equals("15")) {
			jk_dataType = 1;
		} else if (jkqx.equals("30")) {
			jk_dataType =2;
		} else if (jkqx.equals("7")) {
			jk_dataType = 3;
		} else if (jkqx.equals("14")) {
			jk_dataType = 4;
		}
		
		int jk_lx=  userMoneyBase.getUMBaseCalculateProductInterest(nn, jk_dataType, Integer.parseInt(userid), name1);
		if(jk_lx > 0) {
			fklv = 100 - jk_lx;
		}
		
		int moneycode = jbdcmsService.getMoneyCode(userid);
		String newsjds = "";
		int jj =  nn * fklv / 100;
	
		if (moneycode == 0) {
			newsjds = famt.format(jj);
			newjymoney = famt.format(nn);
		} else if (moneycode == 1) {
			newsjds = famt.format(nn);
		}
		logger.info(" rec_id" + rec_id + " kjmoney" + kjmoney + " state" + state + " remarks" + remarks); // 得到的信息
		if (state.equals("1")) { // 审核成功
			if (pingfen == -1 || pingfen == -2 && shqx == 0) {
				jsonObject.put("error", 1);
				jsonObject.put("msg", "vui lòng gửi Trưởng phòng Thẩm định xét duyệt！");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			DataRow row = new DataRow();
			row.set("id", rec_id);
			row.set("cl02_status", "1");
			row.set("cl02_yj", remarks);
			int twosh = jbdcmsService.getONESH(rec_id);
			if (shenhezuzz1.equals(cmsuserid + "") || shenhezuzz2.equals(cmsuserid + "")
					|| shenhezuzz3.equals(cmsuserid + "") || shenhezuzz4.equals(cmsuserid + "")) {
				row.set("cl02_ren", twosh);
			} else {
				row.set("cl02_ren", cmsuserid);
			}
			row.set("sjsh_money", newjymoney);
			row.set("sjds_money", newsjds);
			row.set("lx", famt.format(nn * (100 - fklv) / 100));
			row.set("cl02_time", fmtrq.format(calendar.getTime()));

			row.set("cl03_time", fmtrq.format(calendar.getTime()));
			row.set("cl03_ren", cmsuserid);
			row.set("cl03_status", 1);
			row.set("sfyfk", "2");
			row.set("spzt", "1");
			row.set("cl03_yj", remarks);
			jbdcmsService.updateUserJk(row);

			DataRow row3 = new DataRow();
			row3.set("userid", userid);
			row3.set("title", "Thông báo thẩm định");// 审核通知
			row3.set("neirong",
					"Đề xuất vay của bạn đã hoàn thành bước xét duyệt sơ bộ, vui lòng đăng tải video theo yêu cầu để tiếp tục được xét duyệt");// 您的借款申请我们已初步审核完成，请根据要求上传视频审核
			row3.set("fb_time", fmtrq.format(calendar.getTime()));
			jbdcmsService.insertUserMsg(row3);
			DataRow row33 = new DataRow();
			row33.set("userid", userid);
			row33.set("title", "Thông báo chấp nhận đề nghị vay");// 审核通知
			row33.set("neirong",
					"Kính gửi Đề xuất vay của bạn đã được chấp thuận, khoản tiền vay sẽ được ghi có vào tài khoản của bạn trong vòng 24 giờ, vui lòng kiểm tra tài khoản."); // 您的借款申请已通过，借款款项将在24小时内进入您的账户，请及时查收。
			row33.set("fb_time", fmtrq.format(calendar.getTime()));
			jbdcmsService.insertUserMsg(row33);
		}
		if (state.equals("2") || state.equals("3")) {
			List<DataRow> list = jbdcmsService.getAllUserJK(rec_id);
			int gzyuqts = jbdcmsService.getYQTSGZJJ();
			int gzyuqcs = jbdcmsService.getYQCSGZJJ();
			for (int i = 0; i < list.size(); i++) {
				DataRow row = list.get(i);
				int yuqts = row.getInt("yuq_ts");
				int yuqjs = 0;
				if (yuqts > 0) {
					yuqjs++;
				}
				if (yuqts < gzyuqts && shqx == 0) {
					jsonObject.put("error", 1);
					jsonObject.put("msg", "Quyền hạn từ trưởng phòng thẩm định trở lên mới được từ chối KH này ！");
					this.getWriter().write(jsonObject.toString());
					return null;
				} else if (yuqjs < gzyuqcs && shqx == 0) {
					jsonObject.put("error", 1);
					jsonObject.put("msg", "Quyền hạn từ trưởng phòng thẩm định trở lên mới được từ chối KH này !");
					this.getWriter().write(jsonObject.toString());
					return null;
				}
			}
		}
		if (state.equals("2")) { // 审核失败
			DataRow row2 = new DataRow();
			row2.set("id", rec_id);
			row2.set("cl02_status", "3");
			row2.set("cl02_yj", remarks);
			row2.set("cl02_ren", cmsuserid);
			row2.set("cl02_time", fmtrq.format(calendar.getTime()));
			jbdcmsService.updateUserJk(row2);
			DataRow row3 = new DataRow();
			row3.set("userid", userid);
			row3.set("title", "Thông báo thẩm định");// 审核通知
			row3.set("neirong",
					"Rất tiếc, khoản vay lần này không được chấp nhận, có thể là do một hoặc nhiều lý do sau: 1. Khả năng hoàn trả không đủ, tỷ lệ nợ quá cao; 2. Tính ổn định của công việc và thu nhập không đủ; 3. Lịch sử tín dụng xấu; 4. Không cung cấp thông tin theo yêu cầu hoặc các thông tin được cung cấp không đầy đủ; 5. Số điểm đánh giá từ hệ thống không đủ."); // 抱歉，本次审核未通过，可能由以下一个或多个原因造成：
																																																																																													// //
																																																																																													// 1.还款能力不足，负债比过高；2.工作和收入稳定性不足；3.信用记录不良；4.未按要求提供资料或提供的资料不够详尽；5.系统综合评分不足。
			row3.set("fb_time", fmtrq.format(calendar.getTime()));
			jbdcmsService.insertUserMsg(row3);
		}
		if (state.equals("3")) { // 拉入黑名单
			DataRow row5 = new DataRow();
			row5.set("id", rec_id);
			row5.set("cl02_status", "3");
			row5.set("cl02_yj", "Danh sách đen" + " " + remarks);
			row5.set("cl02_ren", cmsuserid);
			row5.set("cl02_time", fmtrq.format(calendar.getTime()));
			jbdcmsService.updateUserJk(row5);
			DataRow row3 = new DataRow();
			row3.set("id", userid);
			row3.set("heihu_zt", 1);
			jbdcmsService.updateUserInfoH(row3);

			DataRow row6 = new DataRow();
			row6.set("userid", userid);
			row6.set("title", "Thông báo thẩm định");// 审核通知
			row6.set("neirong",
					"Rất tiếc, khoản vay lần này không được chấp nhận, có thể là do một hoặc nhiều lý do sau: 1. Khả năng hoàn trả không đủ, tỷ lệ nợ quá cao; 2. Tính ổn định của công việc và thu nhập không đủ; 3. Lịch sử tín dụng xấu; 4. Không cung cấp thông tin theo yêu cầu hoặc các thông tin được cung cấp không đầy đủ; 5. Số điểm đánh giá từ hệ thống không đủ."); // 抱歉，本次审核未通过，可能由以下一个或多个原因造成：
																																																																																													// //
																																																																																													// 1.还款能力不足，负债比过高；2.工作和收入稳定性不足；3.信用记录不良；4.未按要求提供资料或提供的资料不够详尽；5.系统综合评分不足。
			row6.set("fb_time", fmtrq.format(calendar.getTime()));
			jbdcmsService.insertUserMsg(row6);

		}
		jsonObject.put("error", 1);
		this.getWriter().write(jsonObject.toString());
		return null;
	}

	// 视频审核GetPlayApplyMedia
	public ActionResult doGetPlayApplyMedia() throws Exception {
		logger.info("进入视频审核");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		String shenhezu = jbdcmsService.getPJGZSHENHE();
		String shzu[] = shenhezu.split(",");
		int code = 0;
		for (int i = 0; i < shzu.length; i++) {
			String string = shzu[i];
			if ((cmsuserid + "").equals(string)) {
				code = 1;
			}
		}
		if (code == 0) {
			jsonObject.put("error", -4);
			jsonObject.put("msg", "Khách hàng này chuyển cho chủ quản thẩm duyệt!");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		int jkid = getIntParameter("apply_id", 0);

		// 通过借款id 找到用户id
		int userid = jbdcmsService.getUserId(jkid);
		int sjxt = jbdcmsService.getSJXTResult(userid);
		
		
		
		// 一组
		String shenhezuzz1 = jbdcmsService.getPJGZSHENHEZZ1();
		// 二组
		String shenhezuzz2 = jbdcmsService.getPJGZSHENHEZZ2();
		// 三组
		String shenhezuzz3 = jbdcmsService.getPJGZSHENHEZZ3();
		// 四组
		String shenhezuzz4 = jbdcmsService.getPJGZSHENHEZZ4();
		if (sjxt == 1 && cmsuserid != 6 && cmsuserid != 8 && !shenhezuzz1.equals(cmsuserid + "")
				&& !shenhezuzz2.equals(cmsuserid + "") && !shenhezuzz3.equals(cmsuserid + "")
				&& !shenhezuzz4.equals(cmsuserid + "") && cmsuserid != 888  &&cmsuserid !=2038) {
			jsonObject.put("error", -5);
			jsonObject.put("msg", "Khách hàng này chuyển cho chủ quản thẩm duyệt!");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		// 根据用户ID获取用户信息
		DataRow user = jbdcmsService.getUserRecOneInfo(userid);
		DataRow usersf = jbdcmsService.getUserFinance(userid);
		// 根据项目的id 返回审核项目的信息
		DataRow jkInfo = jbdcmsService.getShThreeInfo(jkid);
		DataRow zhaopian = jbdcmsService.getZhaopian(userid);
		DataRow useRow = jbdcmsService.getUser(userid);
		String username = useRow.getString("username");
		String idNo = usersf.getString("idNo");
		String sfmiwen = Encrypt.MD5(idNo + jiami);
		com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
		json.put("secret", sfmiwen);
		json.put("idno", idNo);
		String url = "http://app.m99vn.com/servlet/current/M99User1?function=GetUserM99YNH5";

		String response = HttpUtil.doPost(url, json, "UTF-8");

		com.alibaba.fastjson.JSONObject json1 = com.alibaba.fastjson.JSONObject.parseObject(response);
		// String code = json1.getString("status");

		String p3 = "";
		if(username.substring(0, 4).equals("TAFA")) {
			p3=json1.getString("p3");
		}else {
			p3= zhaopian.getString("p3");
		}
		jsonObject.put("user_id", jkInfo.getString("userid"));
		jsonObject.put("re_money", jkInfo.getString("jk_money"));
		jsonObject.put("re_qx", jkInfo.getString("jk_date"));
		jsonObject.put("kj_money", jkInfo.getString("sjsh_money"));
		jsonObject.put("kj_lx", jkInfo.getString("sjsh_money"));
		jsonObject.put("sj_money", jkInfo.getString("sjds_money"));
		jsonObject.put("mediaUrl", jkInfo.getString("spdz"));
		jsonObject.put("id", jkInfo.getString("id"));
		jsonObject.put("p3",p3);
		jsonObject.put("realname", usersf.getString("realname")); // 姓名
		jsonObject.put("cardid", usersf.getString("idno")); // 身份证
		jsonObject.put("cellphone", user.getString("mobilephone")); // 手机号
		jsonObject.put("username", user.getString("username")); // 手机号
		jsonObject.put("error", 1);
		this.getWriter().write(jsonObject.toString());
		return null;
	}

	/**
	 * 获取后台账户登录信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public ActionResult doGetCmsuserInfo() throws Exception {

		int userid = SessionHelper.getInt("cmsuserid", getSession());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		DataRow row = new DataRow();
		String cur_date = sdf.format(new Date());
		if (userid == 0) {
			row.set("iflogin", 1);
			return null;
		} else {
			DataRow user = jbdcmsService.getCmsuserInfo(userid);
			row.set("userid", userid);
			row.set("roleid", user.getInt("roleid"));
			row.set("username", user.getString("name"));
			row.set("ip", user.getString("lastip"));
			row.set("cur_date", cur_date);
			row.set("iflogin", 2);
		}
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());
		return null;
	}

	/**
	 * 获取平台信息
	 * 
	 */

	public ActionResult doGetIndexInfo() throws Exception {
		
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		int userid = SessionHelper.getInt("cmsuserid", getSession());
		if (userid != 8 && userid != 6 && userid != 3 && userid != 888 && userid != 4002 && userid != 8888
				&& userid != 9999 && userid != 135699) {
			return null;
		} else {
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();// 日历对象
			calendar.setTime(date);// 设置当前日期
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			SimpleDateFormat sdfday = new SimpleDateFormat("yyyy-MM-dd");
			String today = sdfday.format(date);
			calendar.add(Calendar.DATE, 1);
			String nextDay = sdfday.format(calendar.getTime());
			// 统计今天注册人数
			int pt_jrzc = jbdcmsService.getPtjrzc(today, nextDay);
			// 统计总注册人数
			int pt_zzc = jbdcmsService.getPtzzc();
			// 统计今日认证人数
			int pt_jrrzGZ = jbdcmsService.getPtjrrzGZ(today, nextDay);

			int pt_jrrz = pt_jrrzGZ;
			// 统计总认证人数
			int pt_zrzGZ = jbdcmsService.getPtzrzGZ();
			// int pt_zrzXS = jbdcmsService.getPtzrzXS();
			int pt_zrz = pt_zrzGZ;
			// 待审核笔数
			int pt_dsh = 0;
			// 待放款笔数
			int pt_dfk = 0;
			// 待还款笔数
			int pt_dhk = 0;
			if (userid != 4002) {
				pt_dsh = jbdcmsService.getPtdshOne() + jbdcmsService.getPtdshTwo() + jbdcmsService.getPtdshThree();
				// 待放款笔数
				pt_dfk = jbdcmsService.getPtdfk();
				// 待还款笔数
				pt_dhk = jbdcmsService.getPtdhk();
			}

			// 平台所有用户信息
			Random rand = new Random();
			int aaa = rand.nextInt(20);
			int bbb = aaa + 100;
			jsonObject.put("pt_jrzc", pt_jrzc);
			jsonObject.put("pt_zzc", pt_zzc);
			jsonObject.put("pt_jrrz", pt_jrrz);
			jsonObject.put("pt_zrz", pt_zrz);
			jsonObject.put("pt_dsh", pt_dsh);
			jsonObject.put("pt_dfk", pt_dfk);
			jsonObject.put("pt_dhk", pt_dhk);

			this.getWriter().write(jsonObject.toString());
			return null;
		}
	}

	// 第一步审核 获取指定借款ID 用户信息
	public ActionResult doGetJKXYYN() throws Exception {

		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		int userid = getIntParameter("jkid");
		DataRow row = new DataRow();
		logger.info("用户ID2 " + userid);
		DataRow user = jbdcmsService.getUserRecOneInfo(userid);
		// 获取用户身份证信息
		DataRow userjk = jbdcmsService.getUserJK(userid);
		// 获取用户的银行卡信息
		DataRow usersf = jbdcmsService.getUserSF(userid);
		row.set("cardusername", user.getString("cardusername")); // 姓名
		row.set("idno", user.getString("remark")); // 身份证
		row.set("mobilephone", user.getString("mobilephone")); // 手机号
		row.set("sjsh_money", userjk.getString("sjsh_money")); // 借款金额
		row.set("sjds_money", userjk.getString("sjds_money")); // 到手金额
		row.set("loan_term", userjk.getString("jk_date")); // 到手金额
		row.set("beginTime02", userjk.getString("fkdz_time")); // 放款时间
		row.set("endTime", userjk.getString("hkyq_time")); // 还款逾期时间
		row.set("cardno", usersf.getString("cardno")); // 银行卡号
		row.set("bankname", usersf.getString("bankname")); // 开户行
		row.set("error", "0");
		row.set("msg", "系统正常");
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());
		return null;
	}

	public StringBuffer readToBuffer(String filePath) throws IOException {
		StringBuffer buffer = new StringBuffer();
		InputStream is = new FileInputStream(filePath);
		String line; // 用来保存每行读取的内容
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		line = reader.readLine(); // 读取第一行
		while (line != null) { // 如果 line 为空说明读完了
			buffer.append(line); // 将读到的内容添加到 buffer 中
			buffer.append("\n"); // 添加换行符
			line = reader.readLine(); // 读取下一行
		}
		reader.close();
		is.close();
		return buffer;
	}

	public ActionResult doGetTongxunlu() throws Exception {
		logger.info("进入通讯录");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		int temp = getIntParameter("temp", 0);
		int jkid = getIntParameter("reid");
		int userid = jbdcmsService.getUserId(jkid);
		String tempVelue = getStrParameter("tempvl");
		String startDate = getStrParameter("startDate");
		String endDate = getStrParameter("endDate");
		// 定义用户选择条件
		String userId = "" + userid;
		String realName = "";
		String phone = "";
		String idCard = "";
		String jkdata = "";
		logger.info("temp:" + temp + "startDate:" + startDate + " endDate" + endDate);
		if (temp == 1) {

			userId = tempVelue;
		}

		if (temp == 2) {

			realName = tempVelue;
		}
		if (temp == 3) {

			phone = tempVelue;
		}
		if (temp == 4) {

			idCard = tempVelue;
		}
		if (temp == 5) {

			jkdata = tempVelue;
		}

		// 默认第一页
		int curPage = getIntParameter("curPage", 1);
		logger.info(userId);
		DBPage page = jbdcmsService.getTongxunlu(curPage, 15, userId, jkid, realName, phone, startDate, endDate, idCard,
				jkdata);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp", temp);
		row.set("tempvalu", tempVelue);
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());

		return null;
	}

	public ActionResult doGetTonghuajilu() throws Exception {
		logger.info("进入通讯录");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		int temp = getIntParameter("temp", 0);
		int jkid = getIntParameter("reid");
		int userid = jbdcmsService.getUserId(jkid);
		String tempVelue = getStrParameter("tempvl");
		String startDate = getStrParameter("startDate");
		String endDate = getStrParameter("endDate");
		// 定义用户选择条件
		String userId = "" + userid;
		String realName = "";
		String phone = "";
		String idCard = "";
		String jkdata = "";
		logger.info("temp:" + temp + "startDate:" + startDate + " endDate" + endDate);
		if (temp == 1) {

			userId = tempVelue;
		}

		if (temp == 2) {

			realName = tempVelue;
		}
		if (temp == 3) {

			phone = tempVelue;
		}
		if (temp == 4) {

			idCard = tempVelue;
		}
		if (temp == 5) {

			jkdata = tempVelue;
		}

		// 默认第一页
		int curPage = getIntParameter("curPage", 1);
		DBPage page = jbdcmsService.getTonghuajilu(curPage, 15, userId, jkid, realName, phone, startDate, endDate,
				idCard, jkdata);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp", temp);
		row.set("tempvalu", tempVelue);
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());

		return null;
	}

	public ActionResult doGetIP() throws Exception {
		logger.info("进入获取IP");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		int temp = getIntParameter("temp", 0);
		String userId = getStrParameter("reid");
		// 定义用户选择条件
		// 默认第一页
		int curPage = getIntParameter("curPage", 1);
		DBPage page = jbdcmsService.getUserIP(curPage, 15, userId);
		List<DataRow> list = jbdcmsService.getIP(userId);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("listdt", list);
		row.set("temp", temp);
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());

		return null;
	}

	// 第二步审核信息
	public ActionResult doGetRecTwoDetail() throws Exception {

		logger.info("进入第二步审核信息");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		

		String recid = getStrParameter("recid"); // 借款项目的id ;
		// 根据项目的id 返回审核项目的信息
		DataRow jkInfo = jbdcmsService.getShTwoInfo(recid);
		//JSONObject jsonObject = new JSONObject();
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		if (jkInfo != null) {

			jsonObject.put("hksj", fmtrq.format(calendar.getTime()));
			jsonObject.put("error", 1);
			jsonObject.put("jkinfo", jkInfo);
		} else {

			jsonObject.put("error", -1);
			jsonObject.put("msg", "此项目已不在审核中状态，请刷新");

		}
		this.getWriter().write(jsonObject.toString());
		return null;
	}

	// 还款信息
	public ActionResult doGetHKinformation() throws Exception {

		logger.info("进入还款信息");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		

		String recid = getStrParameter("recid"); // 借款项目的id ;
		String hksj = getStrParameter("hktime"); // 借款项目的id ;
		//JSONObject jsonObject = new JSONObject();
		Date date1 = new Date();
		SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		DecimalFormat df = new DecimalFormat("###,###");
		DataRow jkInfo = jbdcmsService.getHKinformation(recid);
		if (jkInfo != null) {
			if (!TextUtils.isEmpty(hksj)) {
				hksj = hksj.replace("&nbsp;", " ");
				SimpleDateFormat fmtrq111 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				try {
					// 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证 日期，比如2007/02/29会被接受，
					// 并转换成2007/03/01
					fmtrq111.setLenient(false);
					Date date = fmtrq111.parse(hksj);

				} catch (Exception e) {
					// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
					jsonObject.put("error", -3);
					jsonObject.put("msg", "thời gian hiện tại");
					this.getWriter().write(jsonObject.toString());
					return null;
				}
				hksj = hksj.replace("&nbsp;", " ").substring(0, 10);
				SimpleDateFormat fmtrqcz = new SimpleDateFormat("dd-MM-yyyy");
				String hkyqdate = jbdcmsService.getHKYQtime(recid).substring(0, 10);
				String hkfqdate = jbdcmsService.getHKFQtime(recid);
				if (!TextUtils.isEmpty(hkfqdate)) {
					hkfqdate = hkfqdate.substring(0, 10);
				}
				long millionSeconds1 = fmtrqcz.parse(hksj).getTime();// 实际还款时间
				long millionSeconds2 = fmtrqcz.parse(hkyqdate).getTime();// 还款时间
				long millionSeconds3 = fmtrqcz.parse(fmtrqcz.format(new Date())).getTime();// 当前时间
				if (millionSeconds1 > millionSeconds3) {
					jsonObject.put("error", -5);
					jsonObject.put("msg", "Xin kiểm tra thời gian trả vay, vượt quá thời gian hiện tại！");
					this.getWriter().write(jsonObject.toString());
					return null;
				}
				if (!TextUtils.isEmpty(hkfqdate)) {
					millionSeconds2 = fmtrqcz.parse(hkfqdate).getTime();// 还款时间
				}
				String newsjsh_money = jkInfo.getString("sjsh_money").replace(",", "");
				String yuq_lx = jkInfo.getString("yuq_lx").replace(",", "");
				int jkdate = jkInfo.getInt("jk_date");
				int sjsh = Integer.parseInt(newsjsh_money);
				int yqlx = Integer.parseInt(yuq_lx);
				long chazhi = 0;
				if (millionSeconds2 >= millionSeconds1) {
					chazhi = millionSeconds3 - millionSeconds2;
				} else {
					chazhi = millionSeconds3 - millionSeconds1;
				}
				int ddday = (int) (chazhi / (1000 * 60 * 60 * 24));// 产生的多的逾期天数

				if (ddday > 0) {
					int dqyqlx = 0;
					if (jkdate == 3 || jkdate == 4) {
						dqyqlx = yqlx - (sjsh * 2 / 100) * ddday;
					} else {
						dqyqlx = yqlx - (sjsh * 2 / 100) * ddday;
					}
					if (dqyqlx > 0) {
						jsonObject.put("yqlx", df.format(dqyqlx));
						jsonObject.put("zje", df.format(dqyqlx + sjsh));
					} else {
						jsonObject.put("yqlx", 0);
						jsonObject.put("zje", df.format(sjsh));
					}
				} else {
					jsonObject.put("yqlx", df.format(yqlx));
					jsonObject.put("zje", df.format(yqlx + sjsh));
				}
				jsonObject.put("hksj", getStrParameter("hktime").replace("&nbsp;", " "));
			} else {
				jsonObject.put("hksj", fmtrq.format(date1));
				jsonObject.put("yqlx", jkInfo.getString("yuq_lx"));
				jsonObject.put("zje", df.format(Integer.parseInt(jkInfo.getString("yuq_lx").replace(",", ""))
						+ Integer.parseInt(jkInfo.getString("sjsh_money").replace(",", ""))));
			}

			// 根据项目的id 返回审核项目的信息
			if (!TextUtils.isEmpty(jkInfo.getString("hkfq_time"))) {
				jsonObject.put("dqsj", jkInfo.getString("hkfq_time"));
			} else {
				jsonObject.put("dqsj", jkInfo.getString("hkyq_time"));
			}
			jsonObject.put("error", 1);
			jsonObject.put("jkinfo", jkInfo);
		} else {

			jsonObject.put("error", -1);
			jsonObject.put("msg", "Món này đã không còn trong trạng thái thẩm định, vui lòng refresh");

		}
		this.getWriter().write(jsonObject.toString());
		return null;
	}

	// 手机运营商数据

	public ActionResult doGetMjzxPhoneYN() throws Exception {

		logger.info("进入到数据分析页面");
		int userid = getIntParameter("user_id", 0);
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl").replace("&nbsp;", " ");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		String phone = "";
		if (!"0".equals(getStrParameter("th_phone")) && !"1".equals(getStrParameter("th_phone"))
				&& !"2".equals(getStrParameter("th_phone"))) {
			phone = getStrParameter("th_phone");
		}

		int thts = getIntParameter("thts");
		int thsc = getIntParameter("thsc");
		int thsj = getIntParameter("thsj");
		int code = 0;
		if (phone.equals("0")) {
			phone = "";
		}
		if (thts == 1) {
			code = 1;
		} else if (thsc == 1) {
			code = 2;
		} else if (thsj == 1) {
			code = 3;
		}
		String cxphone = "";
		String cxid = "";
		if (temp == 1) {
			cxid = tempVelue;
		} else if (temp == 2) {
			cxphone = tempVelue;
		}
		DataRow userTonghua = jbdcmsService.getUserTonghuaYN(userid);
		DataRow row = new DataRow();
		// 获取用户的通话记录
		int curPage = getIntParameter("curPage", 1);
		DBPage page = jbdcmsService.getUserThYN(curPage, 15, userid, phone, code, cxid, cxphone);
		// 查询用户添加的联系人账号
		DataRow lxrhm = jbdcmsService.personYN(userid);
		// 通话总条数
		int tonghuaCount = jbdcmsService.getTonghuaCountYN(userid);

		row.set("temp", temp);
		row.set("tempvalu", tempVelue);
		row.set("list", page);
		row.set("usertonghua", userTonghua);
		row.set("lxrhm", lxrhm);
		row.set("th_phone", phone);
		row.set("tonghuaCount", tonghuaCount);
		JSONObject object = JSONObject.fromBean(row);
		// System.out.println(object.toString());
		this.getWriter().write(object.toString());
		return null;
	}
	public ActionResult doGetYys() throws Exception {
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl").replace("&nbsp;", " ");
		int userid = getIntParameter("user_id", 0);
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
	    int roleid = jbdcmsService.getCMSuserRoleid(cmsuserid);
	    if (roleid != 1 && roleid != 2 && roleid != 17 && roleid != 19 && roleid != 21 && roleid != 24 && roleid != 26 && roleid != 20 
	    		&& roleid != 50 && roleid != 51 && roleid != 52 ) {
	    	jsonObject.put("error", -1);
			jsonObject.put("msg", "Vui lòng đăng nhập trước");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		logger.info("请求ID:" + cmsuserid);
		
		DataRow row = new DataRow();
		String phone = "";
		String cxid = "";
		if (temp == 1) {

			cxid = tempVelue;
		} else if (temp == 2) {
			phone = tempVelue;
		} 

		// 京东购物记录
		int curPage = getIntParameter("curPage", 1);
		DBPage page = jbdcmsService.getUserYys(curPage, 15, userid, cxid, phone);
		
		DataRow yys = jbdcmsService.geYysUser(userid);
		List<DataRow> listmonth = jbdcmsService.getYysMonth(userid);
		List<DataRow> listup = jbdcmsService.getYysUp(userid);
		
		int yysCount = jbdcmsService.getYysCount(userid);

		row.set("yyscount", yysCount);
		row.set("list", page);
		
		row.set("yys", yys);
		row.set("listmonth", listmonth);
		row.set("listup", listup);
		
		row.set("temp", temp);
		row.set("tempvalu", tempVelue);
		row.set("error", "0");
		row.set("msg", "Normal System");
		JSONObject object = JSONObject.fromBean(row);

		this.getWriter().write(object.toString());
		return null;
	}
	public ActionResult doGetZalo() throws Exception {
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl").replace("&nbsp;", " ");
		int userid = getIntParameter("user_id", 0);
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
		int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
		cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
		if(cmsuserid==0){
			jsonObject.put("error", -1);
			jsonObject.put("msg", "Vui lòng đăng nhập trước");
			this.getWriter().write(jsonObject.toString());	
			return null;		
		}
		int roleid = jbdcmsService.getCMSuserRoleid(cmsuserid);
	    if (roleid != 1 && roleid != 2 && roleid != 17 && roleid != 19 && roleid != 21 && roleid != 24 && roleid != 26 && roleid != 20 
	    		&& roleid != 50 && roleid != 51 && roleid != 52 ) {
	    	jsonObject.put("error", -1);
			jsonObject.put("msg", "Vui lòng đăng nhập trước");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		logger.info("请求ID:" + cmsuserid);
		
		DataRow row = new DataRow();
		String phone = "";
		String cxid = "";
		String name = "";
		if (temp == 1) {
			
			cxid = tempVelue;
		} else if (temp == 2) {
			phone = tempVelue;
		} else if (temp == 3) {
			name = tempVelue;
		} 
		
		// 京东购物记录
		int curPage = getIntParameter("curPage", 1);
		
		DBPage page = jbdcmsService.getZaloFriend(curPage, 15, userid, cxid, phone,name);
		
		DataRow zalo = jbdcmsService.getZaloUser(userid);

		List<DataRow> listgroup = jbdcmsService.getZaloGroup(userid);
		
		row.set("list", page);
		
		row.set("zalo", zalo);
		row.set("listgroup", listgroup);
		
		row.set("temp", temp);
		row.set("tempvalu", tempVelue);
		row.set("error", "0");
		row.set("msg", "Normal System");
		JSONObject object = JSONObject.fromBean(row);
		
		this.getWriter().write(object.toString());
		return null;
	}
	
	public ActionResult doGetFaceBook() throws Exception {
		int userid = getIntParameter("user_id", 0);
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
	    int roleid = jbdcmsService.getCMSuserRoleid(cmsuserid);
	    if (roleid != 1 && roleid != 2 && roleid != 17 && roleid != 19 && roleid != 21 && roleid != 24 && roleid != 26 && roleid != 20 
	    		&& roleid != 50 && roleid != 51 && roleid != 52 ) {
	    	jsonObject.put("error", -1);
			jsonObject.put("msg", "Vui lòng đăng nhập trước");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		logger.info("请求ID:" + cmsuserid);
		
		DataRow row = new DataRow();

		DataRow facebook = jbdcmsService.getFacebookPost(userid);
		
		List<DataRow> list = jbdcmsService.getFacebook(userid);

		row.set("facebook", facebook);
		row.set("list", list);
		row.set("error", "0");
		row.set("msg", "Normal System");
		JSONObject object = JSONObject.fromBean(row);

		this.getWriter().write(object.toString());
		return null;
	}
	public ActionResult doGetMjzxTXL() throws Exception {
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl").replace("&nbsp;", " ");
		int userid = getIntParameter("user_id", 0);
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		

		String phone = "";
		String name = "";
		String cxid = "";
		if (temp == 1) {

			cxid = tempVelue;
		} else if (temp == 2) {
			phone = tempVelue;
		} else if (temp == 3) {
			name = tempVelue;
		}

		// 京东购物记录
		int curPage = getIntParameter("curPage", 1);
		DBPage page = jbdcmsService.getUserTongxunlu(curPage, 15, userid, cxid, phone, name);
		int tongxunluCount01 = jbdcmsService.getTongxunluCountYN(userid);

		logger.info("tongxunluCount01:"+tongxunluCount01);
		int txl_state =0;
		int tongxunlucount =0;
		com.alibaba.fastjson.JSONObject pagelist = new com.alibaba.fastjson.JSONObject();
		if(tongxunluCount01<=30) {
			tempVelue = URLEncoder.encode(tempVelue);
			String IdNo = jbdcmsService.getUserNameIdNo(userid);
			
			String sfmiwen = Encrypt.MD5(jiami+IdNo);
			com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
			json.put("secret", sfmiwen);
			json.put("idno", IdNo);
			json.put("temp", temp);
			json.put("tempvl", tempVelue);
			json.put("curPage", curPage);
			
			/************************3****************/
			String url = "http://app.m99vn.com/servlet/current/M99User2?function=GetUserTXL3";
			String response = HttpUtil.doPost(url, json, "UTF-8");
			com.alibaba.fastjson.JSONObject json1 = com.alibaba.fastjson.JSONObject.parseObject(response);
			int error = json1.getInteger("error");
			if (error == 1) {
			    tongxunlucount = json1.getInteger("tongxunlucount");
			    logger.info("3:"+tongxunlucount);
				if(tongxunluCount01<tongxunlucount) {
					String list = json1.getString("list");
				    pagelist = com.alibaba.fastjson.JSONObject.parseObject(list);
				    txl_state=1;
				}
			}
			/**********2****************/
			if(txl_state==0) {
				 url = "http://app.m99vn.com/servlet/current/M99User2?function=GetUserTXL2";
				 response = HttpUtil.doPost(url, json, "UTF-8");
				com.alibaba.fastjson.JSONObject json2 = com.alibaba.fastjson.JSONObject.parseObject(response);
			    error = json2.getInteger("error");
				if (error == 1) {
					 logger.info("2:"+tongxunlucount);
				    tongxunlucount = json2.getInteger("tongxunlucount");
					if(tongxunluCount01<tongxunlucount) {
						String list = json2.getString("list");
					    pagelist = com.alibaba.fastjson.JSONObject.parseObject(list);
					    txl_state=1;
					}
				}
			}
			/**********1****************/
			if(txl_state==0) {
				 url = "http://app.m99vn.com/servlet/current/M99User2?function=GetUserTXL1";
				 response = HttpUtil.doPost(url, json, "UTF-8");
				com.alibaba.fastjson.JSONObject json2 = com.alibaba.fastjson.JSONObject.parseObject(response);
			    error = json2.getInteger("error");
				if (error == 1) {
				    tongxunlucount = json2.getInteger("tongxunlucount");
				    logger.info("1:"+tongxunlucount);
					if(tongxunluCount01<tongxunlucount) {
						String list = json2.getString("list");
					    pagelist = com.alibaba.fastjson.JSONObject.parseObject(list);
					    txl_state=1;
					}
				}
			}
			
		}
		
		DataRow row = new DataRow();
		
		if(txl_state==1) {
			row.set("tongxunlucount", tongxunlucount);
			row.set("list", pagelist);
		}else {
			row.set("tongxunlucount", tongxunluCount01);
			row.set("list", page);
		}
		row.set("temp", temp);
		row.set("tempvalu", tempVelue);
		row.set("error", "0");
		row.set("msg", "Normal System");
		JSONObject object = JSONObject.fromBean(row);

		this.getWriter().write(object.toString());
		return null;
	}

	public ActionResult doGetMjzxTXLH5() throws Exception {
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl").replace("&nbsp;", " ");
		tempVelue = URLEncoder.encode(tempVelue);
		int userid = getIntParameter("user_id", 0);

		int curPage = getIntParameter("curPage", 1);

		JSONObject jsonObject = new JSONObject();
		String IdNo = jbdcmsService.getUserNameIdNo(userid);

		String sfmiwen = Encrypt.MD5(IdNo + jiami);
		com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
		json.put("secret", sfmiwen);
		json.put("idno", IdNo);
		json.put("temp", temp);
		json.put("tempvl", tempVelue);
		json.put("curPage", curPage);
		String url = "http://app.m99vn.com/servlet/current/M99User1?function=GetM99TXL";

		String response = HttpUtil.doPost(url, json, "UTF-8");

		com.alibaba.fastjson.JSONObject json1 = com.alibaba.fastjson.JSONObject.parseObject(response);
		// String code = json1.getString("status");

		int error = json1.getInteger("error");
		DataRow row = new DataRow();
		if (error == 1) {
			int tongxunlucount = json1.getInteger("tongxunlucount");
			String list = json1.getString("list");

			row.set("tongxunlucount", tongxunlucount);
			com.alibaba.fastjson.JSONObject page = com.alibaba.fastjson.JSONObject.parseObject(list);
			row.set("list", page);

			row.set("temp", json1.getString("temp"));
			row.set("tempvalu", json1.getString("tempvalu"));
			row.set("error", "0");
			row.set("msg", "Normal System");
			JSONObject object = JSONObject.fromBean(row);
			this.getWriter().write(object.toString());
			return null;
		} else {

			String phone = "";
			String name = "";
			String cxid = "";
			if (temp == 1) {

				cxid = tempVelue;
			} else if (temp == 2) {
				phone = tempVelue;
			} else if (temp == 3) {
				name = tempVelue;
			}

			// 京东购物记录
			//DBPage page = jbdcmsService.getUserTongxunlu(curPage, 15, userid, cxid, phone, name);
			//int tongxunluCount = jbdcmsService.getTongxunluCountYN(userid);
			
			row.set("tongxunlucount", 0);
			row.set("list", "");


			row.set("temp", temp);
			row.set("tempvalu", tempVelue);
			row.set("error", "0");
			row.set("msg", "Normal System");
			JSONObject object = JSONObject.fromBean(row);
			this.getWriter().write(object.toString());
			return null;
		}
	}
	/**
	 * TXL---第三表
	 * @return
	 * @throws Exception
	 */
	public ActionResult doGetMjzxTXL_3() throws Exception {
		
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl").replace("&nbsp;", " ");
		tempVelue = URLEncoder.encode(tempVelue);
		int userid = getIntParameter("user_id", 0);
		int curPage = getIntParameter("curPage", 1);
		String IdNo = jbdcmsService.getUserNameIdNo(userid);
		
		String sfmiwen = Encrypt.MD5(jiami+IdNo);
		com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
		json.put("secret", sfmiwen);
		json.put("idno", IdNo);
		json.put("temp", temp);
		json.put("tempvl", tempVelue);
		json.put("curPage", curPage);
		String url = "http://app.m99vn.com/servlet/current/M99User2?function=GetUserTXL2";
		String response = HttpUtil.doPost(url, json, "UTF-8");

		com.alibaba.fastjson.JSONObject json1 = com.alibaba.fastjson.JSONObject.parseObject(response);

		int error = json1.getInteger("error");
		DataRow row = new DataRow();
		if (error == 1) {
			int tongxunlucount = json1.getInteger("tongxunlucount");
			String list = json1.getString("list");

			row.set("tongxunlucount", tongxunlucount);
			com.alibaba.fastjson.JSONObject page = com.alibaba.fastjson.JSONObject.parseObject(list);
			row.set("list", page);

			row.set("temp", json1.getString("temp"));
			row.set("tempvalu", json1.getString("tempvalu"));
			row.set("error", "0");
			row.set("msg", "Normal System");
			JSONObject object = JSONObject.fromBean(row);
			this.getWriter().write(object.toString());
			return null;
		} else {
			row.set("tongxunlucount", 0);
			row.set("list", "");
			row.set("temp", temp);
			row.set("tempvalu", tempVelue);
			row.set("error", "0");
			row.set("msg", "Normal System");
			JSONObject object = JSONObject.fromBean(row);
			this.getWriter().write(object.toString());
			return null;
		}
	}
	/**
	 * TXL---第四表
	 * @return
	 * @throws Exception
	 */
	public ActionResult doGetMjzxTXL_4() throws Exception {
		
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl").replace("&nbsp;", " ");
		tempVelue = URLEncoder.encode(tempVelue);
		int userid = getIntParameter("user_id", 0);
		int curPage = getIntParameter("curPage", 1);
		String IdNo = jbdcmsService.getUserNameIdNo(userid);
		
		String sfmiwen = Encrypt.MD5(jiami+IdNo);
		com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
		json.put("secret", sfmiwen);
		json.put("idno", IdNo);
		json.put("temp", temp);
		json.put("tempvl", tempVelue);
		json.put("curPage", curPage);
		String url = "http://app.m99vn.com/servlet/current/M99User2?function=GetUserTXL3";
		String response = HttpUtil.doPost(url, json, "UTF-8");

		com.alibaba.fastjson.JSONObject json1 = com.alibaba.fastjson.JSONObject.parseObject(response);

		int error = json1.getInteger("error");
		DataRow row = new DataRow();
		if (error == 1) {
			int tongxunlucount = json1.getInteger("tongxunlucount");
			String list = json1.getString("list");

			row.set("tongxunlucount", tongxunlucount);
			com.alibaba.fastjson.JSONObject page = com.alibaba.fastjson.JSONObject.parseObject(list);
			row.set("list", page);

			row.set("temp", json1.getString("temp"));
			row.set("tempvalu", json1.getString("tempvalu"));
			row.set("error", "0");
			row.set("msg", "Normal System");
			JSONObject object = JSONObject.fromBean(row);
			this.getWriter().write(object.toString());
			return null;
		} else {
			row.set("tongxunlucount", 0);
			row.set("list", "");
			row.set("temp", temp);
			row.set("tempvalu", tempVelue);
			row.set("error", "0");
			row.set("msg", "Normal System");
			JSONObject object = JSONObject.fromBean(row);
			this.getWriter().write(object.toString());
			return null;
		}
	}
	/**
	 * TXL---第5表
	 * @return
	 * @throws Exception
	 */
	public ActionResult doGetMjzxTXL_a() throws Exception {
		
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl").replace("&nbsp;", " ");
		tempVelue = URLEncoder.encode(tempVelue);
		int userid = getIntParameter("user_id", 0);
		int curPage = getIntParameter("curPage", 1);
		String IdNo = jbdcmsService.getUserNameIdNo(userid);

		String sfmiwen = Encrypt.MD5(jiami+IdNo);
		com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
		json.put("secret", sfmiwen);
		json.put("idno", IdNo);
		json.put("temp", temp);
		json.put("tempvl", tempVelue);
		json.put("curPage", curPage);
		String url = "http://app.m99vn.com/servlet/current/M99User2?function=GetuserContactinfoE";
		String response = HttpUtil.doPost(url, json, "UTF-8");

		com.alibaba.fastjson.JSONObject json1 = com.alibaba.fastjson.JSONObject.parseObject(response);

		int error = json1.getInteger("error");
		DataRow row = new DataRow();
		if (error == 1) {
			int tongxunlucount = json1.getInteger("tongxunlucount");
			String list = json1.getString("list");

			row.set("tongxunlucount", tongxunlucount);
			com.alibaba.fastjson.JSONObject page = com.alibaba.fastjson.JSONObject.parseObject(list);
			row.set("list", page);

			row.set("temp", json1.getString("temp"));
			row.set("tempvalu", json1.getString("tempvalu"));
			row.set("error", "0");
			row.set("msg", "Normal System");
			JSONObject object = JSONObject.fromBean(row);
			this.getWriter().write(object.toString());
			return null;
		} else {
			row.set("tongxunlucount", 0);
			row.set("list", "");
			row.set("temp", temp);
			row.set("tempvalu", tempVelue);
			row.set("error", "0");
			row.set("msg", "Normal System");
			JSONObject object = JSONObject.fromBean(row);
			this.getWriter().write(object.toString());
			return null;
		}
	}
	/**
	 * TXL---第6表
	 * @return
	 * @throws Exception
	 */
	public ActionResult doGetMjzxTXL_S() throws Exception {
		
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl").replace("&nbsp;", " ");
		tempVelue = URLEncoder.encode(tempVelue);
		int userid = getIntParameter("user_id", 0);
		int curPage = getIntParameter("curPage", 1);
		String IdNo = jbdcmsService.getUserNameIdNo(userid);

		String sfmiwen = Encrypt.MD5(jiami+IdNo);
		com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
		json.put("secret", sfmiwen);
		json.put("idno", IdNo);
		json.put("temp", temp);
		json.put("tempvl", tempVelue);
		json.put("curPage", curPage);
		String url = "http://app.m99vn.com/servlet/current/M99User2?function=GetuserContactinfoS";
		String response = HttpUtil.doPost(url, json, "UTF-8");

		com.alibaba.fastjson.JSONObject json1 = com.alibaba.fastjson.JSONObject.parseObject(response);

		int error = json1.getInteger("error");
		DataRow row = new DataRow();
		if (error == 1) {
			int tongxunlucount = json1.getInteger("tongxunlucount");
			String list = json1.getString("list");

			row.set("tongxunlucount", tongxunlucount);
			com.alibaba.fastjson.JSONObject page = com.alibaba.fastjson.JSONObject.parseObject(list);
			row.set("list", page);

			row.set("temp", json1.getString("temp"));
			row.set("tempvalu", json1.getString("tempvalu"));
			row.set("error", "0");
			row.set("msg", "Normal System");
			JSONObject object = JSONObject.fromBean(row);
			this.getWriter().write(object.toString());
			return null;
		} else {
			row.set("tongxunlucount", 0);
			row.set("list", "");
			row.set("temp", temp);
			row.set("tempvalu", tempVelue);
			row.set("error", "0");
			row.set("msg", "Normal System");
			JSONObject object = JSONObject.fromBean(row);
			this.getWriter().write(object.toString());
			return null;
		}
	}
	/**
	 * TXL---第7表
	 * @return
	 * @throws Exception
	 */
	public ActionResult doGetMjzxTXL_Y() throws Exception {
		
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl").replace("&nbsp;", " ");
		tempVelue = URLEncoder.encode(tempVelue);
		int userid = getIntParameter("user_id", 0);
		int curPage = getIntParameter("curPage", 1);
		String IdNo = jbdcmsService.getUserNameIdNo(userid);

		String sfmiwen = Encrypt.MD5(jiami+IdNo);
		com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
		json.put("secret", sfmiwen);
		json.put("idno", IdNo);
		json.put("temp", temp);
		json.put("tempvl", tempVelue);
		json.put("curPage", curPage);
		String url = "http://app.m99vn.com/servlet/current/M99User2?function=GetuserContactinfoY";
		String response = HttpUtil.doPost(url, json, "UTF-8");

		com.alibaba.fastjson.JSONObject json1 = com.alibaba.fastjson.JSONObject.parseObject(response);

		int error = json1.getInteger("error");
		DataRow row = new DataRow();
		if (error == 1) {
			int tongxunlucount = json1.getInteger("tongxunlucount");
			String list = json1.getString("list");

			row.set("tongxunlucount", tongxunlucount);
			com.alibaba.fastjson.JSONObject page = com.alibaba.fastjson.JSONObject.parseObject(list);
			row.set("list", page);

			row.set("temp", json1.getString("temp"));
			row.set("tempvalu", json1.getString("tempvalu"));
			row.set("error", "0");
			row.set("msg", "Normal System");
			JSONObject object = JSONObject.fromBean(row);
			this.getWriter().write(object.toString());
			return null;
		} else {
			row.set("tongxunlucount", 0);
			row.set("list", "");
			row.set("temp", temp);
			row.set("tempvalu", tempVelue);
			row.set("error", "0");
			row.set("msg", "Normal System");
			JSONObject object = JSONObject.fromBean(row);
			this.getWriter().write(object.toString());
			return null;
		}
	}
	

	public ActionResult doGetMjzxMsg() throws Exception {

		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl").replace("&nbsp;", " ");
		int userid = getIntParameter("user_id", 0);
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		DataRow row = new DataRow();
		String content = "";
		String phone = "";
		String sms = "";
		String gjcgl = jbdcmsService.getGJCGL();
		System.out.println(gjcgl);
		if (temp == 1) {

			content = tempVelue;
			gjcgl = gjcgl + "," + content;
		}
		String sss[] = gjcgl.split(",");
		System.out.println(sss[0]);
		if (temp == 2) {
			String phoneid = tempVelue;
			phone = jbdcmsService.getPhoneTXL(phoneid);
			if (!TextUtils.isEmpty(phone)) {
				phone = phone.replace(" ", "");
			}
		}
		if (temp == 3) {

			String smsid = tempVelue;

			sms = jbdcmsService.getPhoneMsg(smsid);

		}

		// 京东购物记录
		int curPage = getIntParameter("curPage", 1);
		DBPage page = jbdcmsService.getUserMsg(curPage, 50, userid, content, phone, sms);
		int MsgCount = jbdcmsService.getMsgCountYN(userid);

		row.set("msgcount", MsgCount);
		row.set("list", page);
		row.set("sss", sss);
		row.set("temp", temp);
		row.set("tempvalu", tempVelue);
		row.set("error", "0");
		row.set("msg", "系统正常");
		JSONObject object = JSONObject.fromBean(row);

		this.getWriter().write(object.toString());
		return null;
	}

	public ActionResult doGetChangeXXList() throws Exception {
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl");

		String startDate1 = getStrParameter("startDate");
		String endDate1 = getStrParameter("endDate");
		String startDate = "";
		String endDate = "";
		if (!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)) {
			String[] sourceStrArray1 = startDate1.split("-");
			String[] sourceStrArray2 = endDate1.split("-");
			startDate = sourceStrArray1[2] + "-" + sourceStrArray1[1] + "-" + sourceStrArray1[0];
			endDate = sourceStrArray2[2] + "-" + sourceStrArray2[1] + "-" + sourceStrArray2[0];
		}

		// 定义用户选择条件
		String userId = "";
		String jkid = "";
		String changecode = "";

		if (temp == 1) {

			userId = tempVelue;
		}

		if (temp == 2) {

			jkid = tempVelue;
		}
		if (temp == 3) {

			changecode = tempVelue;
		}

		// 默认第一页
		int curPage = getIntParameter("curPage", 1);
		DBPage page = jbdcmsService.getChangeUserXXList(curPage, 15, userId, jkid, changecode, startDate, endDate);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp", temp);
		row.set("tempvalu", tempVelue);
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());
		return null;
	}

	public ActionResult doChangeUserXX() throws Exception {
		
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		int userid = getIntParameter("userid");
		int lab = getIntParameter("lab");
		int jkid = getIntParameter("jkid");
		String remark = getStrParameter("remark").replace("&nbsp;", " ").trim();
		SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		DecimalFormat famt = new DecimalFormat("###,###");
		cmsuserid = SessionHelper.getInt("cmsuserid", getSession());
		if (cmsuserid == 0) {
			jsonObject.put("error", -1);
			jsonObject.put("msg", "Xin đăng nhập trước");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		DataRow userjkxx = jbdcmsService.getUserJKXXNow(userid);
//		if (userjkxx.getInt("cl_status") == 3 || userjkxx.getInt("cl02_status") == 3
//				|| userjkxx.getInt("cl03_status") == 3 || userjkxx.getInt("sfyfk") == 1) {
//			jsonObject.put("error", -8);
//			jsonObject.put("msg", "不在审核列表中，不能修改信息！");
//			this.getWriter().write(jsonObject.toString());
//			return null;
//		}
		String appname = jbdcmsService.getusername(userid);
		int ggxxcs = jbdcmsService.getChangeXXCS(cmsuserid);
		if (lab == 0) {
			jsonObject.put("error", -7);
			jsonObject.put("msg", "Xin chọn loại hình điều chỉnh！");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		if (ggxxcs == 0) {
			jsonObject.put("error", -5);
			jsonObject.put("msg", "Vượt quá số lần điều chỉnh trong ngày. Vui lòng liên hệ IT tăng số lần điều chỉnh！");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		if (lab == 1) {
			String name = jbdcmsService.getUserName(userid);
			DataRow row = new DataRow();
			row.set("userid", userid);
			row.set("changecode", lab);
			row.set("beforexx", name);
			row.set("afterxx", remark);
			row.set("cmsuserid", cmsuserid);
			row.set("createtime", fmtrq.format(new Date()));
			jbdcmsService.insertChangeUserXX(row);

			DataRow row1 = new DataRow();
			row1.set("userid", userid);
			row1.set("realname", remark);
			jbdcmsService.updateChangeUserXXSFXX(row1);
			DataRow row2 = new DataRow();
			row2.set("userid", userid);
			row2.set("cardusername", remark);
			row2.set("napasbankno", 0);
			jbdcmsService.updateChangeUserXXBank(row2);

			DataRow row3 = new DataRow();
			row3.set("user_id", cmsuserid);
			row3.set("ggxxcs", ggxxcs - 1);
			jbdcmsService.updateChangeUserGGXXCS(row3);

		} else if (lab == 2) {
			String idno = jbdcmsService.getUserNameIdNo(userid);
			DataRow row = new DataRow();
			row.set("userid", userid);
			row.set("changecode", lab);
			row.set("beforexx", idno);
			row.set("afterxx", remark);
			row.set("cmsuserid", cmsuserid);
			row.set("createtime", fmtrq.format(new Date()));
			jbdcmsService.insertChangeUserXX(row);
			DataRow pp = jbdcmsService.getPP(idno);
			DataRow data3 = new DataRow();
			data3.set("id", userjkxx.getInt("id"));
			if (pp != null) {
				if (pp.getString("zt").equals("K")) {
					data3.set("pipei", 3);
					DataRow row1 = new DataRow();
					row1.set("userid", userid);
					row1.set("sfzhm", idno);
					jbdcmsService.updateNewPP(row1);
					jbdcmsService.insertPP(pp);
				} else if (pp.getString("zt").equals("L") || pp.getString("zt").equals("H")) {
					data3.set("pipei", -2);
					DataRow row1 = new DataRow();
					row1.set("userid", userid);
					row1.set("sfzhm", idno);
					jbdcmsService.updateNewPP(row1);
					jbdcmsService.insertPP(pp);
				} else if (pp.getString("zt").equals("D")) {
					data3.set("pipei", -1);
					DataRow row1 = new DataRow();
					row1.set("userid", userid);
					row1.set("sfzhm", idno);
					jbdcmsService.updateNewPP(row1);
					jbdcmsService.insertPP(pp);
				} else if (pp.getString("zt").equals("A")) {
					data3.set("pipei", 2);
					DataRow row1 = new DataRow();
					row1.set("userid", userid);
					row1.set("sfzhm", idno);
					jbdcmsService.updateNewPP(row1);
					jbdcmsService.insertPP(pp);
				} else if (pp.getString("zt").equals("T")) {
					data3.set("pipei", 2);
					DataRow row1 = new DataRow();
					row1.set("userid", userid);
					row1.set("sfzhm", idno);
					jbdcmsService.updateNewPP(row1);
					jbdcmsService.insertPP(pp);
				} else {
					data3.set("pipei", 1);
					DataRow row1 = new DataRow();
					row1.set("userid", userid);
					row1.set("sfzhm", idno);
					jbdcmsService.updateNewPP(row1);
					jbdcmsService.insertPP(pp);
				}
			} else {
				data3.set("pipei", 0);
			}
			jbdcmsService.updateUserJk(data3);

			DataRow row1 = new DataRow();
			row1.set("userid", userid);
			row1.set("idno", remark);
			jbdcmsService.updateChangeUserXXSFXX(row1);

			DataRow row3 = new DataRow();
			row3.set("user_id", cmsuserid);
			row3.set("ggxxcs", ggxxcs - 1);
			jbdcmsService.updateChangeUserGGXXCS(row3);
		} else if (lab == 3) {
			String age = jbdcmsService.getUserCsrq(userid);
			DataRow row = new DataRow();
			row.set("userid", userid);
			row.set("changecode", lab);
			row.set("beforexx", age);
			row.set("afterxx", remark);
			row.set("cmsuserid", cmsuserid);
			row.set("createtime", fmtrq.format(new Date()));
			jbdcmsService.insertChangeUserXX(row);

			DataRow row1 = new DataRow();
			row1.set("userid", userid);
			row1.set("age", remark);
			jbdcmsService.updateChangeUserXXSFXX(row1);

			DataRow row3 = new DataRow();
			row3.set("user_id", cmsuserid);
			row3.set("ggxxcs", ggxxcs - 1);
			jbdcmsService.updateChangeUserGGXXCS(row3);
		} else if (lab == 4) {
			String cardno = jbdcmsService.getUserCardNo(userid);
			DataRow row = new DataRow();
			row.set("userid", userid);
			row.set("changecode", lab);
			row.set("beforexx", cardno);
			row.set("afterxx", remark);
			row.set("cmsuserid", cmsuserid);
			row.set("createtime", fmtrq.format(new Date()));
			jbdcmsService.insertChangeUserXX(row);

			DataRow row1 = new DataRow();
			row1.set("userid", userid);
			row1.set("cardno", remark);
			row1.set("napasbankno", 0);
			jbdcmsService.updateChangeUserXXBank(row1);

			DataRow row3 = new DataRow();
			row3.set("user_id", cmsuserid);
			row3.set("ggxxcs", ggxxcs - 1);
			jbdcmsService.updateChangeUserGGXXCS(row3);
		} else if (lab == 5) {
			String cardname = jbdcmsService.getUserCardName(userid);
			DataRow row = new DataRow();
			row.set("userid", userid);
			row.set("changecode", lab);
			row.set("beforexx", cardname);
			row.set("afterxx", remark);
			row.set("cmsuserid", cmsuserid);
			row.set("createtime", fmtrq.format(new Date()));
			jbdcmsService.insertChangeUserXX(row);

			DataRow row1 = new DataRow();
			row1.set("userid", userid);
			row1.set("cardname", remark);
			row1.set("napasbankno", 0);
			jbdcmsService.updateChangeUserXXBank(row1);

			DataRow row3 = new DataRow();
			row3.set("user_id", cmsuserid);
			row3.set("ggxxcs", ggxxcs - 1);
			jbdcmsService.updateChangeUserGGXXCS(row3);
		} else if (lab == 6) {
			if (!"15".equals(remark) && !"30".equals(remark) && !"7".equals(remark) && !"14".equals(remark)) {
				jsonObject.put("error", -2);
				jsonObject.put("msg",
						"Thời hạn vay: 15 ngày thì nhập 15; 30 ngày thì nhập 30;7 ngày thì nhập 7; 14 ngày thì nhập 14");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			DataRow userjk = jbdcmsService.getUserJKXX(userid, jkid);
			if (userjk != null) {
				int jkqx = userjk.getInt("jk_date");
				String sjshmoney = userjk.getString("sjsh_money");
				int shmoney = 0;
				if (!TextUtils.isEmpty(sjshmoney)) {
					shmoney = Integer.parseInt(sjshmoney.replace(",", ""));
				}
				int sdmoney = 0;
				int lx = 0;

				int ts = 0;
				int ggts = 0;
				int ggqx = 0;
				if (jkqx == 1) {
					ts = 15;
				} else if (jkqx == 3) {
					ts = 7;
				} else if (jkqx == 4) {
					ts = 14;
				} else {
					ts = 30;
				}
//				if ("15".equals(remark)) {
//					ggts = 15;
//					ggqx = 1;
//					if (shmoney == 1000000) {
//						lx = shmoney * 30 / 100;
//						sdmoney = shmoney - lx;
//					} else {
//						lx = shmoney * 20 / 100;
//						sdmoney = shmoney - lx;
//					}
//
//				} else if ("7".equals(remark)) {
//					ggts = 7;
//					ggqx = 3;
//					lx = shmoney * 14 / 100;
//					sdmoney = shmoney - lx;
//				} else if ("14".equals(remark)) {
//					ggts = 14;
//					ggqx = 4;
//					lx = shmoney * 28 / 100;
//					sdmoney = shmoney - lx;
//				} else {
//					if(userid>108000 && appname.substring(0, 4).equals("F168")) {
//						ggts = 40;
//						ggqx = 2;
//						lx = shmoney * ggts / 100;
//						sdmoney = shmoney - lx;
//					}else {
//						ggts = 30;
//						ggqx = 2;
//						lx = shmoney * 30 / 100;
//						sdmoney = shmoney - lx;
//					}
//					
//				}
				
				//修改lic20190801
				if ("15".equals(remark)) {
					ggts = 15;
					ggqx = 1;

				} else if ("7".equals(remark)) {
					ggts = 7;
					ggqx = 3;
				} else if ("14".equals(remark)) {
					ggts = 14;
					ggqx = 4;
				} else {
					ggts = 30;
					ggqx = 2;
				}
				int jk_lx=  userMoneyBase.getUMBaseCalculateProductInterest(shmoney, ggqx, userid, appname);
				lx = shmoney * jk_lx / 100;
				sdmoney = shmoney - lx;
				
				if ((ts == 7 && ggts != 14) || (ts == 14 && ggts != 7) || (ts == 15 && ggts != 30)
						|| (ts == 30 && ggts != 15)) {
					jsonObject.put("error", -2);
					jsonObject.put("msg", "15天只能和30天的相互改，7天只能和14天的相互改");
					this.getWriter().write(jsonObject.toString());
					return null;
				}
				DataRow row = new DataRow();
				row.set("userid", userid);
				row.set("jkid", jkid);
				row.set("changecode", lab);
				row.set("beforexx", ts);
				row.set("afterxx", ggts);
				row.set("cmsuserid", cmsuserid);
				row.set("createtime", fmtrq.format(new Date()));
				jbdcmsService.insertChangeUserXX(row);

				if (!TextUtils.isEmpty(sjshmoney)) {

					DataRow row1 = new DataRow();
					row1.set("id", jkid);
					row1.set("jk_date", ggqx);
					row1.set("sjds_money", famt.format(sdmoney));
					row1.set("sjsh_money", famt.format(shmoney));
					row1.set("lx", famt.format(lx));
					jbdcmsService.updateUserJk(row1);
				} else {
					DataRow row1 = new DataRow();
					row1.set("id", jkid);
					row1.set("jk_date", ggqx);
					jbdcmsService.updateUserJk(row1);
				}

				DataRow row3 = new DataRow();
				row3.set("user_id", cmsuserid);
				row3.set("ggxxcs", ggxxcs - 1);
				jbdcmsService.updateChangeUserGGXXCS(row3);

			} else {
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Không có KH vay này！");
				this.getWriter().write(jsonObject.toString());
				return null;
			}

		} else if (lab == 7) {
			if (Integer.parseInt(remark) > 5000000) {
				jsonObject.put("error", -3);
				jsonObject.put("msg", "Số tiền quá lớn, liên hệ IT điều chỉnh！");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			DataRow userjk = jbdcmsService.getUserJKXX(userid, jkid);
			if (userjk != null) {
				String sjshmoney = userjk.getString("sjsh_money");
				int shmoney = Integer.parseInt(remark.replace(",", ""));
				int jkqx = userjk.getInt("jk_date");
				int sdmoney = 0;
				int lx = 0;
//				if (jkqx == 1) {
//					if (shmoney == 1000000) {
//						lx = shmoney * 30 / 100;
//						sdmoney = shmoney - lx;
//					} else {
//						lx = shmoney * 20 / 100;
//						sdmoney = shmoney - lx;
//					}
//				} else if (jkqx == 3) {
//					lx = shmoney * 14 / 100;
//					sdmoney = shmoney - lx;
//				} else if (jkqx == 4) {
//					lx = shmoney * 28 / 100;
//					sdmoney = shmoney - lx;
//				} else {
//					if(userid>108000 && appname.substring(0, 4).equals("F168")) {
//						lx = shmoney * 40 / 100;
//						sdmoney = shmoney - lx;
//					}else {
//						lx = shmoney * 30 / 100;
//						sdmoney = shmoney - lx;
//					}
//				}
				
				//修改lic20190801
				int jk_lx=  userMoneyBase.getUMBaseCalculateProductInterest(shmoney, jkqx, userid, appname);
				lx = shmoney * jk_lx / 100;
				sdmoney = shmoney - lx;
				
				DataRow row = new DataRow();
				row.set("userid", userid);
				row.set("jkid", jkid);
				row.set("changecode", lab);
				row.set("beforexx", sjshmoney);
				row.set("afterxx", famt.format(Integer.parseInt(remark)));
				row.set("cmsuserid", cmsuserid);
				row.set("createtime", fmtrq.format(new Date()));
				jbdcmsService.insertChangeUserXX(row);

				if (!TextUtils.isEmpty(sjshmoney)) {

					DataRow row1 = new DataRow();
					row1.set("id", jkid);
					row1.set("sjds_money", famt.format(sdmoney));
					row1.set("sjsh_money", famt.format(shmoney));
					row1.set("lx", famt.format(lx));
					jbdcmsService.updateUserJk(row1);
				} else {
					jsonObject.put("error", -4);
					jsonObject.put("msg", "Không có số tiền duyệt vay ! Vui lòng xác nhận KH đang ở bước 2");
					this.getWriter().write(jsonObject.toString());
					return null;
				}

				DataRow row3 = new DataRow();
				row3.set("user_id", cmsuserid);
				row3.set("ggxxcs", ggxxcs - 1);
				jbdcmsService.updateChangeUserGGXXCS(row3);

			} else {
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Không có KH vay này！");
				this.getWriter().write(jsonObject.toString());
				return null;
			}

		}
		jsonObject.put("error", 1);
		jsonObject.put("msg", "成功！");
		this.getWriter().write(jsonObject.toString());
		return null;
	}

	public ActionResult doCheXiaoXX() throws Exception {
		
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		int id = getIntParameter("id");
		DataRow userchangexx = jbdcmsService.getUserChangeXX(id);
		int userid = userchangexx.getInt("userid");
		int lab = userchangexx.getInt("changecode");
		int jkid = userchangexx.getInt("jkid");
		String appname = jbdcmsService.getusername(userid);
		String beforexx = userchangexx.getString("beforexx").replace("&nbsp;", " ").trim();
		DecimalFormat famt = new DecimalFormat("###,###");
	    cmsuserid = SessionHelper.getInt("cmsuserid", getSession());
		if (cmsuserid == 0) {
			jsonObject.put("error", -1);
			jsonObject.put("msg", "Xin đăng nhập trước");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		int ggxxcs = jbdcmsService.getChangeXXCS(cmsuserid);

		int czuserid = userchangexx.getInt("cmsuserid");
		if (cmsuserid != czuserid && cmsuserid != 888 && cmsuserid != 4002 && cmsuserid != 5) {
			jsonObject.put("error", -1);
			jsonObject.put("msg", "KH này không phải của bạn đề xuất nên không thể hủy！");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		if (lab == 1) {

			DataRow row = new DataRow();
			row.set("id", id);
			row.set("state", cmsuserid);
			jbdcmsService.updateChangeUserXX(row);

			DataRow row1 = new DataRow();
			row1.set("userid", userid);
			row1.set("realname", beforexx);
			jbdcmsService.updateChangeUserXXSFXX(row1);

			DataRow row2 = new DataRow();
			row2.set("userid", userid);
			row2.set("cardusername", beforexx);
			jbdcmsService.updateChangeUserXXBank(row2);

			DataRow row3 = new DataRow();
			row3.set("user_id", cmsuserid);
			row3.set("ggxxcs", ggxxcs + 1);
			jbdcmsService.updateChangeUserGGXXCS(row3);

		} else if (lab == 2) {

			DataRow row = new DataRow();
			row.set("id", id);
			row.set("state", cmsuserid);
			jbdcmsService.updateChangeUserXX(row);

			DataRow row1 = new DataRow();
			row1.set("userid", userid);
			row1.set("idno", beforexx);
			jbdcmsService.updateChangeUserXXSFXX(row1);

			DataRow row3 = new DataRow();
			row3.set("user_id", cmsuserid);
			row3.set("ggxxcs", ggxxcs + 1);
			jbdcmsService.updateChangeUserGGXXCS(row3);
		} else if (lab == 3) {

			DataRow row = new DataRow();
			row.set("id", id);
			row.set("state", cmsuserid);
			jbdcmsService.updateChangeUserXX(row);

			DataRow row1 = new DataRow();
			row1.set("userid", userid);
			row1.set("age", beforexx);
			jbdcmsService.updateChangeUserXXSFXX(row1);

			DataRow row3 = new DataRow();
			row3.set("user_id", cmsuserid);
			row3.set("ggxxcs", ggxxcs + 1);
			jbdcmsService.updateChangeUserGGXXCS(row3);
		} else if (lab == 4) {

			DataRow row = new DataRow();
			row.set("id", id);
			row.set("state", cmsuserid);
			jbdcmsService.updateChangeUserXX(row);

			DataRow row1 = new DataRow();
			row1.set("userid", userid);
			row1.set("cardno", beforexx);
			row1.set("napasbankno", 0);
			jbdcmsService.updateChangeUserXXBank(row1);

			DataRow row3 = new DataRow();
			row3.set("user_id", cmsuserid);
			row3.set("ggxxcs", ggxxcs + 1);
			jbdcmsService.updateChangeUserGGXXCS(row3);
		} else if (lab == 5) {

			DataRow row = new DataRow();
			row.set("id", id);
			row.set("state", cmsuserid);
			jbdcmsService.updateChangeUserXX(row);

			DataRow row1 = new DataRow();
			row1.set("userid", userid);
			row1.set("cardname", beforexx);
			row1.set("napasbankno", 0);
			jbdcmsService.updateChangeUserXXBank(row1);

			DataRow row3 = new DataRow();
			row3.set("user_id", cmsuserid);
			row3.set("ggxxcs", ggxxcs + 1);
			jbdcmsService.updateChangeUserGGXXCS(row3);
		} else if (lab == 6) {

			DataRow userjk = jbdcmsService.getUserJKXX(userid, jkid);
			if (userjk != null) {
				int jkqx = userjk.getInt("jk_date");
				String sjshmoney = userjk.getString("sjsh_money");
				int shmoney = 0;
				if (!TextUtils.isEmpty(sjshmoney)) {
					shmoney = Integer.parseInt(sjshmoney.replace(",", ""));
				}
				int sdmoney = 0;
				int lx = 0;

				int ts = 0;
				int ggts = 0;
				int ggqx = 0;
				if (jkqx == 1) {
					ts = 15;
				} else {
					ts = 30;
				}
//				if ("15".equals(beforexx)) {
//					ggts = 15;
//					ggqx = 1;
//					if (shmoney == 1000000) {
//						lx = shmoney * 30 / 100;
//						sdmoney = shmoney - lx;
//					} else {
//						lx = shmoney * 20 / 100;
//						sdmoney = shmoney - lx;
//					}
//
//				} else {
//					if(userid>108000 && appname.substring(0, 4).equals("F168")) {
//						ggts = 40;
//						ggqx = 2;
//						lx = shmoney * ggts / 100;
//						sdmoney = shmoney - lx;
//					}else {
//						ggts = 30;
//						ggqx = 2;
//						lx = shmoney * 30 / 100;
//						sdmoney = shmoney - lx;
//					}
//				}
				
				//修改lic20190801
				if ("15".equals(beforexx)) {
					ggts = 15;
					ggqx = 1;

				} else if ("7".equals(beforexx)) {
					ggts = 7;
					ggqx = 3;
				} else if ("14".equals(beforexx)) {
					ggts = 14;
					ggqx = 4;
				} else {
					ggts = 30;
					ggqx = 2;
				}
				int jk_lx=  userMoneyBase.getUMBaseCalculateProductInterest(shmoney, ggqx, userid, appname);
				lx = shmoney * jk_lx / 100;
				sdmoney = shmoney - lx;
				
				DataRow row = new DataRow();
				row.set("id", id);
				row.set("state", cmsuserid);
				jbdcmsService.updateChangeUserXX(row);

				if (!TextUtils.isEmpty(sjshmoney)) {

					DataRow row1 = new DataRow();
					row1.set("id", jkid);
					row1.set("jk_date", ggqx);
					row1.set("sjds_money", famt.format(sdmoney));
					row1.set("sjsh_money", famt.format(shmoney));
					row1.set("lx", famt.format(lx));
					jbdcmsService.updateUserJk(row1);
				} else {
					DataRow row1 = new DataRow();
					row1.set("id", jkid);
					row1.set("jk_date", ggqx);
					jbdcmsService.updateUserJk(row1);
				}

				DataRow row3 = new DataRow();
				row3.set("user_id", cmsuserid);
				row3.set("ggxxcs", ggxxcs + 1);
				jbdcmsService.updateChangeUserGGXXCS(row3);

			} else {
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Không có KH vay này ! Không thể hủy bỏ");
				this.getWriter().write(jsonObject.toString());
				return null;
			}

		} else if (lab == 7) {

			DataRow userjk = jbdcmsService.getUserJKXX(userid, jkid);
			if (userjk != null) {
				String sjshmoney = userjk.getString("sjsh_money");
				int shmoney = Integer.parseInt(beforexx.replace(",", ""));
				int jkqx = userjk.getInt("jk_date");
				int sdmoney = 0;
				int lx = 0;
//				if (jkqx == 1) {
//					if (shmoney == 1000000) {
//						lx = shmoney * 30 / 100;
//						sdmoney = shmoney - lx;
//					} else {
//						lx = shmoney * 20 / 100;
//						sdmoney = shmoney - lx;
//					}
//				} else {
//					if(userid>108000 && appname.substring(0, 4).equals("F168")) {
//						lx = shmoney * 40/ 100;
//						sdmoney = shmoney - lx;
//					}else {
//						lx = shmoney * 30 / 100;
//						sdmoney = shmoney - lx;
//					}
//				}

				//修改linc20190801
				int jk_lx=  userMoneyBase.getUMBaseCalculateProductInterest(shmoney, jkqx, userid, appname);
				lx = shmoney * jk_lx / 100;
				sdmoney = shmoney - lx;
				
				DataRow row = new DataRow();
				row.set("id", id);
				row.set("state", cmsuserid);
				jbdcmsService.updateChangeUserXX(row);

				if (!TextUtils.isEmpty(sjshmoney)) {

					DataRow row1 = new DataRow();
					row1.set("id", jkid);
					row1.set("sjds_money", famt.format(sdmoney));
					row1.set("sjsh_money", famt.format(shmoney));
					row1.set("lx", famt.format(lx));
					jbdcmsService.updateUserJk(row1);
				} else {
					jsonObject.put("error", -4);
					jsonObject.put("msg", "Không có số tiền duyệt vay ! Vui lòng xác nhận KH đang ở bước 2");
					this.getWriter().write(jsonObject.toString());
					return null;
				}

				DataRow row3 = new DataRow();
				row3.set("user_id", cmsuserid);
				row3.set("ggxxcs", ggxxcs + 1);
				jbdcmsService.updateChangeUserGGXXCS(row3);

			} else {
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Không có KH vay này ! Không thể hủy bỏ");
				this.getWriter().write(jsonObject.toString());
				return null;
			}

		}
		jsonObject.put("error", 1);
		jsonObject.put("msg", "成功！");
		this.getWriter().write(jsonObject.toString());
		return null;
	}

	public ActionResult doGetAllGuiZeList() throws Exception {
		
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl");

		String startDate1 = getStrParameter("startDate");
		String endDate1 = getStrParameter("endDate");
		String startDate = "";
		String endDate = "";
		if (!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)) {
			String[] sourceStrArray1 = startDate1.split("-");
			String[] sourceStrArray2 = endDate1.split("-");
			startDate = sourceStrArray1[2] + "-" + sourceStrArray1[1] + "-" + sourceStrArray1[0];
			endDate = sourceStrArray2[2] + "-" + sourceStrArray2[1] + "-" + sourceStrArray2[0];
		}

		// 定义用户选择条件
		String userId = "";
		String jkid = "";
		String changecode = "";

		if (temp == 1) {

			userId = tempVelue;
		}

		if (temp == 2) {

			jkid = tempVelue;
		}
		if (temp == 3) {

			changecode = tempVelue;
		}

		// 默认第一页
		int curPage = getIntParameter("curPage", 1);
		DBPage page = jbdcmsService.getAllGuiZeList(curPage, 15, userId, jkid, changecode, startDate, endDate);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp", temp);
		row.set("tempvalu", tempVelue);
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());
		return null;
	}

	public ActionResult doChangeGuiZe() throws Exception {
		
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		JSONObject json = new JSONObject();
		int id = getIntParameter("id");
		String gggz1 = getStrParameter("gggz1");
		if (id == 7 || id == 13 || id == 14 || id == 16 || id == 17 || id == 21 || id == 22 || id == 23) {
			if (!isInteger(gggz1)) {
				json.put("error", -1);
				json.put("msg", "格式错误，只能是数字");
				this.getWriter().write(json.toString());
				return null;
			}
		}
		if (id == 8 || id == 9 || id == 10 || id == 11 || id == 12 || id == 18) {
			if (!isInteger(gggz1.replace(",", ""))) {
				json.put("error", -1);
				json.put("msg", "格式错误，只能是带逗号的数字");
				this.getWriter().write(json.toString());
				return null;
			}
		}
		String gggz2 = getStrParameter("gggz2");
		DataRow row = new DataRow();
		row.set("id", id);
		row.set("guizebianliang1", gggz1);
		row.set("guizebianliang2", gggz2);
		jbdcmsService.updateChangeGuiZe(row);
		json.put("error", 1);
		json.put("msg", "成功");
		this.getWriter().write(json.toString());
		return null;
	}

	public ActionResult doSENDSMS() throws Exception {
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		String phone = getStrParameter("phone");
		int lab = getIntParameter("lab");

		SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String time = fmtrq.format(new Date());
		DecimalFormat famt = new DecimalFormat("###,###");
		cmsuserid = SessionHelper.getInt("cmsuserid", getSession());
		if (cmsuserid == 0) {
			jsonObject.put("error", -1);
			jsonObject.put("msg", "Xin đăng nhập trước");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		int count = jbdcmsService.getCountPhoneSms(phone, time);
		if (count > 0) {
			jsonObject.put("error", -2);
			jsonObject.put("msg", "Hôm nay đã gửi tin nhắn rồi");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		String smscontent = "";
		if (lab == 1) {
			smscontent = "mofa thong bao : Quy khach vui long tai app MOFA theo link http://bit.ly/2QJAh16, hotline: 1900234558.";
		}
		String content = "[{\"PhoneNumber\":\"" + phone + "\",\"Message\":\"" + smscontent + "\",\"SmsGuid\":\"" + phone
				+ "\",\"ContentType\":1}]";
		String con = URLEncoder.encode(content, "utf-8");
		SendMsg sendMsg = new SendMsg();
		String returnString ="";
		//returnString= SendMsg.sendMessageByGet(con, phone);
		if (returnString.equals("106")) {
			DataRow row = new DataRow();
			row.set("phone", phone);
			row.set("idno", "发送链接短信");
			row.set("createtime", time);
			row.set("cmsuserid", cmsuserid);
			jbdcmsService.insertUserFSDXMsg(row);
			jsonObject.put("error", 1);
			jsonObject.put("msg", "Đã gửi thành công");
			this.getWriter().write(jsonObject.toString());
			return null;
		} else {
			jsonObject.put("error", -1);
			jsonObject.put("msg", "Gửi thất bại");
			this.getWriter().write(jsonObject.toString());
			return null;
		}

	}

	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}
	
	/**
	 * 用户信息查找
	 * @return
	 * @throws Exception
	 */
	public ActionResult doGetUserChangXXL2() throws Exception {

		logger.info("用户照片信息查询");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		int temp = getIntParameter("temp", -1);
		String tempVelue = getStrParameter("tempvl");
		String startDate1 = getStrParameter("startDate");
		String endDate1 = getStrParameter("endDate");
		String startDate = "";
		String endDate = "";
		
		if("".contentEquals(tempVelue)  || temp==-1   ) {
			return null;
		}
		
		int cmsid = SessionHelper.getInt("cmsuserid", getSession());
		Date newtime =new Date();
		
		String userid = "0";
		String usercmnd = "0";
		String realName = "0";
		String phone = "0";
		//a视频照片
		String [] user_pvi =new String[4];

		DataRow userfinance =  null;
		if (temp == 1) {
			userid = tempVelue;
			 userfinance = jbdcmsService.getUserfianceDataRow(userid,"");
		}
		if (temp == 2) {
			usercmnd = tempVelue;
			userfinance = jbdcmsService.getUserfianceDataRow("",usercmnd);
		}

		logger.info("temp:"+temp+" tempVelue:"+tempVelue+" userid:"+userid+" usercmnd:"+usercmnd);

		if (userfinance!=null) {
			userid = userfinance.getString("userid");
			realName = userfinance.getString("realname");
			usercmnd = userfinance.getString("idno");
		}
		
		DataRow userphoto  = jbdcmsService.getUserphotoRow(userid);
		DataRow userphone  = jbdcmsService.getUserphoneRow(userid);
		DataRow userjkinfo  = jbdcmsService.getUserjkinfoDataRow(userid);
		
		
		if (userphoto!=null) {
			user_pvi[0] =userphoto.getString("p1");
			user_pvi[1] =userphoto.getString("p2");
			user_pvi[2] =userphoto.getString("p3");
			
		}
		if (userphone!=null) {
			phone = userphone.getString("mobilephone");
		}
		if (userjkinfo!=null) {
			user_pvi[3] = userjkinfo.getString("spdz");
		}
		logger.info("userid:"+userid+"realName:"+realName+"usercmnd"+usercmnd+"user_p1:"+user_pvi[0]+"user_vi:"+user_pvi[3]);
	    String url ="https://abc-dong.oss-cn-hongkong.aliyuncs.com/";
		//String url ="https://m99.oss-cn-hongkong.aliyuncs.com/";
	    logger.info("url:"+url);
		for (int i = 0; i < user_pvi.length; i++) {
			if (user_pvi[i]!=null&& !userid.equals("")) {
				user_pvi[i]=url+user_pvi[i];
			}else {
				user_pvi[i]="Không có";
			}
		}
		
		DataRow rowin = new DataRow();
		rowin.set("create_date", newtime );
		rowin.set("cmsid", cmsid );
		rowin.set("useid",Integer.parseInt(userid));
		rowin.set("idno", usercmnd );
		rowin.set("phone", phone );
		jbdcmsService.insertUserpv(rowin);
		
		DataRow row = new DataRow();
		row.set("user_id", userid);
		row.set("user_name", realName);
		row.set("user_cmnd", usercmnd);
		row.set("user_phone", phone);
		row.set("user_p1", user_pvi[0]);
		row.set("user_p2", user_pvi[1]);
		row.set("user_p3", user_pvi[2]);
		row.set("user_vi", user_pvi[3]);

		row.set("temp", temp);
		row.set("tempvalu", tempVelue);
	
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());
		return null;
	}
}
