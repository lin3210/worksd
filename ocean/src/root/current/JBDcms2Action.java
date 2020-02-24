package root.current;


import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;

import root.SendFTP;
import root.SendMsg;

import com.project.service.account.JBDUserService;
import com.project.service.account.JBDcms2Service;
import com.project.service.account.JBDcmsService;
import com.thinkive.base.jdbc.DBPage;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.util.SessionHelper;
import com.thinkive.base.util.StringHelper;
import com.thinkive.web.base.ActionResult;
import com.thinkive.web.base.BaseAction;
/**
 * 后台报表
 * @author lilei
 *
 */
public class JBDcms2Action extends BaseAction{
	
	
	private static Logger logger = Logger.getLogger(JBDcms2Action.class);
	private static JBDcms2Service jbdcms2Service = new JBDcms2Service();
	private static JBDUserService jbdUserService = new JBDUserService();
	private static JBDcmsService jbdcmsService = new JBDcmsService();
	private static AccessVerifivationBase accessVeritifivationbase = new AccessVerifivationBase();
	
	
	
	
	/**
	 *催收部长查看还款成功名单列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public ActionResult doGetRepayPlanList() throws Exception {

		logger.info("催收还款成功订单列表");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuser_id =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuser_id =accessVeritifivationbase.checkCMSidAndip(cmsuser_id, getipAddr());
	    if(cmsuser_id==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuser_id);
		
		int temp = getIntParameter("temp", 0);

		int temp1 = getIntParameter("temp1", 0);
		String tempVelue = getStrParameter("tempvl");
		String startDate = getStrParameter("startDate");
		String endDate = getStrParameter("endDate");

		// 审核状态
		String commit = getStrParameter("commit");
		// 定义用户选择条件
		String userId = "";
		String jkid = "";
		String orderid = "";
		String phone = "";
		String cmsuserid = "";
		String hkbank = "";
		String idno = "";
		String time = "";
		String funpay = "";
		int hkcode = 0;
		int yqcode = 0;


		if(temp!=1 && temp!=11  && temp!=0 && temp!=9) {
			this.getWriter().write(jsonObject.toString());	
			return null;
		}
		
		SimpleDateFormat fmtnyr = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String curTime = fmtnyr.format(date);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -2);
		String curtime2 = fmtnyr.format(cal.getTime());
		if (temp == 0) {
			if(!startDate.equals("") && !endDate.equals("")  ) {
				Date bt=fmtnyr.parse(startDate); 
			    Date et=fmtnyr.parse(curtime2); 
				if (bt.before(et)){ 
				   //表示bt小于et
					startDate = curtime2;
				}
				Date bt1=fmtnyr.parse(curTime); 
				Date et1=fmtnyr.parse(endDate); 
				if (bt1.before(et1)){ 
				   //表示bt小于et
					endDate =  curTime;
				 }
			}else {
				startDate = curtime2;
				endDate =  curTime;
			}
		}
		if (temp == 1) {

			userId = tempVelue;
			jkid =jbdcms2Service.getuserlastJK(userId)+"";
		}

		if (temp == 2) {

			jkid = tempVelue;
		}
		if (temp == 3) {

			orderid = tempVelue.replace("&acute;", "/").replace("&nbsp;", " ").trim(); // 接收到的用户还款银行订单号;
		}
		if (temp == 4) {

			phone = tempVelue;
		}
		if (temp == 5) {

			hkcode = 1;
		}
		if (temp == 6) {

			hkcode = 2;
		}
		if (temp == 7) {

			hkcode = 3;
		}
		if (temp == 8) {

			hkcode = 4;
		}
		if (temp == 9) {
			
			cmsuserid = tempVelue;
			if(!startDate.equals("") && !endDate.equals("")  ) {
				Date bt=fmtnyr.parse(startDate); 
			    Date et=fmtnyr.parse(curtime2); 
				if (bt.before(et)){ 
				   //表示bt小于et
					startDate = curtime2;
				}
				Date bt1=fmtnyr.parse(curTime); 
				Date et1=fmtnyr.parse(endDate); 
				if (bt1.before(et1)){ 
				   //表示bt小于et
					endDate =  curTime;
				 }
			}else {
				startDate = curtime2;
				endDate =  curTime;
			}
		}
		if (temp == 10) {
			
			hkbank = tempVelue;
		}
		if (temp == 11) {
			
			idno = tempVelue;
			userId = jbdcms2Service.getuserfinance(idno)+"";
			jkid =jbdcms2Service.getuserlastJK(userId)+"";
		}
		if (temp == 12) {
			
			time = tempVelue;
		}
		if (temp == 13) {
			
			funpay = tempVelue;
		}
		
		
		if (temp1 == 1) {

			yqcode = 1;
		}
		if (temp1 == 2) {

			yqcode = 2;
		}
		
		logger.info("temp:" + temp + "startDate:" + startDate + " endDate"
				+ endDate);
		
		// 默认第一页
		int curPage = getIntParameter("curPage", 1);
		int userid = SessionHelper.getInt("cmsuserid", getSession());
	    DataRow maprole =jbdcms2Service.getSdcmsUser(userid);
	    int maproleid=maprole.getInt("roleid");
		DBPage page = jbdcms2Service.getRepayPlanListHK(curPage, 15, userId,
				jkid, phone, hkcode, yqcode, startDate, endDate, commit,
				orderid,cmsuserid,hkbank,idno,time,funpay,maproleid);
		List<DataRow> list = jbdcms2Service.getRepayPlanListRow(userId, jkid,
				phone, hkcode, yqcode, startDate, endDate, commit, orderid,cmsuserid,hkbank,idno,time,funpay,maproleid);
		
		
		double total = 0;
		for (DataRow dataRow : list) {
			String a = dataRow.getString("rechargemoney").replace(",", "");
			total += Integer.parseInt(a);

		}
		DecimalFormat df = new DecimalFormat("###,###");
		String sum = df.format(total);
		DataRow row = new DataRow();
		row.set("list", page);
//		if (!StringHelper.isEmpty(startDate)) {
//			DBPage page2 = jbdcms2Service.getRepayPlanListHK(curPage,100000, userId,
//					jkid, phone, hkcode, yqcode, startDate, endDate, commit,
//					orderid,cmsuserid,hkbank,idno,time,funpay);
//			row.set("list2", page2);
//		}else{
//			row.set("list2", page);
//		}
		
		row.set("temp", temp);
		row.set("temp1", temp1);
		row.set("hkcount", sum);
		row.set("tempvalu", tempVelue);

		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());
		return null;
	}
	
	//shilian
		public  ActionResult doAddcuisremarkhshilian() throws Exception {
			logger.info("进入添加失联名单");	
			int rec_id = getIntParameter("re_id",0);//借款ID
			int u_id = jbdcms2Service.getUserId(rec_id);
			String remark= getStrParameter("remark");
			JSONObject jsonObject = new JSONObject(); 
			int userid2 = SessionHelper.getInt("cmsuserid", getSession());
			userid2 =accessVeritifivationbase.checkCMSidAndip(userid2, getipAddr());
			if(userid2==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
			}
			logger.info("请求ID:"+userid2);
			Date date=new Date();
			DateFormat format=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			String time=format.format(date);
			int yuqts = jbdcms2Service.getYQTS(rec_id);
			
			DataRow row18=  new DataRow();	
			row18.set("user_id",u_id);	
			row18.set("jkjl_id",rec_id );
			row18.set("kefuid",userid2);
			row18.set("visitdate", time);
			row18.set("yuqts", yuqts);
			row18.set("content",remark);
			jbdcms2Service.insertUserKFMsgSL(row18);
			
			DataRow row288=  new DataRow();
			row288.set("id",u_id);	
			row288.set("shilian_zt",1);
			jbdcms2Service.updateUserInfo(row288);
			jsonObject.put("error", 1);
			jsonObject.put("msg", "拉入失联成功");
			this.getWriter().write(jsonObject.toString());	
			return null ;
		}
//****************************xiong-start-**********************	
	public ActionResult doGetRechargeByJkid() throws Exception {
			logger.info("催收人员查询催收单的信息");
			String jkid = getStrParameter("jkid"); 
			JSONObject jsonObject = new JSONObject(); // 后台登录账户
			
			List<DataRow> list=jbdcms2Service.getRechargeByJkid(jkid);
			int a=list.size();
			List<DataRow> list2=new ArrayList<DataRow>();
			if(a>0) { 			
			for (int i = 0; i < list.size(); i++) {
				DataRow drold = list.get(i);

				int days = drold.getInt("yqdate");
				String money = drold.getString("money");
				String retime = drold.getString("retime");
				String csname = drold.getString("csname");
				String cuishouid = drold.getString("cuishouid");
				String remark=drold.getString("remark");
				String userhktime=drold.getString("userhktime");
				
				//String jitn =cuishouid.substring(0,2);cuishouid.substring(0,2).contains("50");				
			 boolean cuishoufront=cuishouid.contains("50");
			 if(false==cuishoufront) {
				 csname="Trả vay đúng hạn";
			 }			 
				DataRow dr = new DataRow();
//				if (0 == days) {
//					dr.set("yqdate", 0);
//				} else if (1 == days) {
//					dr.set("yqdate", 15);
//				} else if (2 == days) {
//					dr.set("yqdate", 30);
//				} else if (3 == days) {
//					dr.set("yqdate", 7);
//				} else if (4 == days) {
//					dr.set("yqdate", 14);
//				}
				
				if(remark.equals("延期还款") && 1 == days) {
					dr.set("yqdate", "Gia hạn 15 ngày");
				}else if(remark.equals("延期还款") && 2 == days) {
					dr.set("yqdate", "Gia hạn 30 ngày");
				}else if(remark.equals("延期还款") && 3 == days) {
					dr.set("yqdate", "Gia hạn 7 ngày");
				}else if(remark.equals("延期还款") && 4 == days) {
					dr.set("yqdate", "Gia hạn 14 ngày");
				}
				//remark.equals("逾期利息还款")||remark.equals("逾期利息还完，部分本金还款")
				else{
					dr.set("yqdate", "Trả toàn bộ");
				}																
				dr.set("money", money);
				dr.set("retime", retime);
				dr.set("csname", csname);
				dr.set("cuishouid", cuishouid);
				dr.set("userhktime", userhktime);
				list2.add(dr);				
			}
		}
			
			
			if(a>0) {
				jsonObject.put("rechargelist", list2);
				jsonObject.put("error", 1);
			}else {
				jsonObject.put("msg", "Không có lịch sử trả vay");
				jsonObject.put("error", 0);
			}															
			this.getWriter().write(jsonObject.toString());
			return null;
			
		}
	
	//xiong-20190813-催收组长查看页面
	public ActionResult doGetYqZuList() throws Exception {
		
		logger.info("待催收用户ZY");
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
		
		int temp = getIntParameter("temp",0);
		String  tempVelue = getStrParameter("tempvl"); 		

		String  startDate1 =getStrParameter("startDate");
		String  endDate1 =getStrParameter("endDate");
		String startDate="";
		String endDate="";
		if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
		String[] sourceStrArray1 = startDate1.split("-");
		String[] sourceStrArray2 = endDate1.split("-");
	    startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
	    endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
		}
	  String  hkstat = getStrParameter("hkstat");       
		//审核状态
		String commit = getStrParameter("commit");
		int user_id = SessionHelper.getInt("cmsuserid", getSession());   //操作员ID
		/*if(user_id == 0){	   
		  	  jsonObject.put("err", -1);		  
			  this.getWriter().write(jsonObject.toString());	
			  return null;
		    }*/
		String csname = jbdcms2Service.getNameCms(user_id);
		//定义用户选择条件
		String userId ="" ;
		String realName="";
		String phone = "" ;	
		String idCard ="" ;
		String cuishouid ="" ;
		String jkdate ="" ;
		String yuqts ="" ;
		
		//逾期天数
		String st_day = getStrParameter("s_day");//逾期最小天数
		
		String et_day = getStrParameter("e_day");//逾期最大天数
	 
		if(temp ==1){
				   	
			userId =tempVelue ;
		}
		
		if(temp ==2){
					
			realName =tempVelue ;
		}
		if(temp ==3) {
					
			phone = tempVelue ;
		}
		if(temp ==4) {
			
			idCard = tempVelue ;
		}	
		if(temp ==5) {
			
			cuishouid = tempVelue ;
		}	
		if(temp ==6) {
			
			jkdate = "34" ;
		}	
		if(temp ==7) {
			
			yuqts = tempVelue;
		}	
		
		//xiong-20190813-查询催收人员
		int userid = SessionHelper.getInt("cmsuserid", getSession());
	    DataRow maprole =jbdcms2Service.getSdcmsUser(userid);
	    int maproleid=maprole.getInt("roleid");
			
		//一组的成员
//		List<DataRow> cuishoum1 = jbdcms2Service.getAllcuishouM1();
//		int cuishouzuyqm1[] = new int[cuishoum1.size()];
//		
//		for (int m = 0; m < cuishoum1.size(); m++) {
//			DataRow row = cuishoum1.get(m);
//			cuishouzuyqm1[m] = row.getInt("user_id");
//		}
	    
		//默认第一页
		int curPage  =getIntParameter("curPage",1);	
		DBPage page = jbdcms2Service.getYqZuList(curPage,15,userId,realName,phone,startDate,endDate,commit,idCard,hkstat,st_day,et_day,cmsuserid,cuishouid,maproleid,jkdate,yuqts);
		List<DataRow> list=jbdcms2Service.getYqZuList0(userId,realName,phone,startDate,endDate,commit,idCard,hkstat ,st_day,et_day,cmsuserid,cuishouid,maproleid,jkdate,yuqts);
		double hkje=0;
		DecimalFormat format = new DecimalFormat("###,###");
		for (int i=0;i<list.size();i++) {
			DataRow dataRow = list.get(i);
			String sjshmoney = dataRow.getString("sjsh_money").replace(",", "");
			String yuqmoney = dataRow.getString("yuq_lx").replace(",", "");
			int sjsh = Integer.parseInt(sjshmoney);
			int yuq = Integer.parseInt(yuqmoney);
			hkje += (sjsh+yuq);
		} 
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("hkje", format.format(hkje));
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		row.set("hkstat",hkstat);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
		return null ;  
	 }
	
	//xiong20190620-每天老用户详细审核失败原因
		public ActionResult doGetRecordListOld() throws Exception {
			
			logger.info("借款申请用户");
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
			
			int temp = getIntParameter("temp",0);
			String  tempVelue = getStrParameter("tempvl"); 	
			String daytime =getStrParameter("day");
			StringBuilder sb = new StringBuilder(daytime);
			sb.insert(4, "-");
			sb.insert(7, "-");
			daytime=sb.toString();
			//审核状态
			String commit = getStrParameter("commit");
			//定义用户选择条件
			String userId ="" ;
			String realName="";
			String phone = "" ;	
			String idCard ="" ;	
			String cmsid ="" ;	
			logger.info(temp+"  "+tempVelue);
			if(temp ==1){	   	
				userId =tempVelue ;
			}
			
			if(temp ==2){
						
				realName =tempVelue ;
			}
			if(temp ==3) {
						
				phone = tempVelue ;
			}
			if(temp ==4) {
				
				idCard = tempVelue ;
			}	
			if(temp ==5) {
				
				cmsid = tempVelue ;
			}	
			java.net.URLEncoder.encode(idCard);
			//默认第一页
			int curPage  =getIntParameter("curPage",1);	
			DBPage page = jbdcms2Service.getRecordListold(curPage,15,userId,realName,phone,commit,idCard,cmsid,daytime);
			List<DataRow> list=page.getData();
			
			//拼接前端所需要的数据
			List<Map<String,Object>> ListShenhe=new ArrayList<Map<String,Object>>();
			for (int i = 0; i < list.size(); i++) {
				DataRow dataRow = list.get(i);
				Map<String, Object> map = new 	HashMap<String, Object>();
				  //老用户sd_user
				  map.put("id",dataRow.get("id"));
				  //老用户的真实姓名sd_user_finance
				  map.put("realname",dataRow.get("realname"));
				  //老用户手机号sd_user
				  map.put("mobilephone",dataRow.get("mobilephone"));
				  //老用户身份证号sd_user_finance
				  map.put("idno",dataRow.get("idno"));
				  //老用户借款金额sd_new_jkyx
				  map.put("jk_money",dataRow.get("jk_money"));
				  //老用户审核人员的姓名sdcms_user
				  map.put("name",dataRow.get("name"));
				  //老用户借款表id sd_new_jkyx
				  map.put("jkid",dataRow.get("jkid"));
				  //老用户表申请时间sd_new_jkyx
				  map.put("create_date",dataRow.get("create_date"));
				  //审核时间，第几次审核，哪次审核失败取哪次的意见
				  //意见
				  map.put("yijian",dataRow.getInt("cl_status")==3?dataRow.get("cl_yj"):(dataRow.getInt("cl02_status")==3?dataRow.get("cl02_yj"):dataRow.get("cl03_yj")));
				  //审核阶段失败的阶段
				  map.put("shenheno",dataRow.getInt("cl_status")==3?1:(dataRow.getInt("cl02_status")==3?2:3));
				  //审核失败的时间
				  map.put("shenhetime",dataRow.getInt("cl_status")==3?dataRow.get("cl_time"):(dataRow.getInt("cl02_status")==3?dataRow.get("cl02_time"):dataRow.get("cl03_time")));			  			  
				  
				  ListShenhe.add(map);			 	
			}
			
			page.setData(ListShenhe);
			
			DataRow row = new DataRow();
			row.set("list", page);
			row.set("temp",temp);
			row.set("tempvalu",tempVelue);	
			JSONObject object = JSONObject.fromBean(row);	
			System.out.println(object.toString());
			this.getWriter().write(object.toString());	
			return null ;  
			
		}
	
	
	//xiong20190614-黑名单
	public  ActionResult doAddcuisremarkhmd() throws Exception {
		  logger.info("进入添加催收备注黑名单");	
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
			
		  int rec_id = getIntParameter("re_id",0);//借款ID
		  int u_id=getIntParameter("user_id",0);
		  String remark = getStrParameter("remark");
		  if(remark != "" || remark != null){
			  remark = remark.replace("&nbsp;", " ");
		  }else{
			  jsonObject.put("error", -1);
		  	  jsonObject.put("msg", "Ghi chú không được để trống");
			  this.getWriter().write(jsonObject.toString());	
			  return null;
		  }
		  logger.info(remark);
		  int userid2 = SessionHelper.getInt("cmsuserid", getSession());
		  
		  if(userid2==0){
			  jsonObject.put("error", -1);
		  	  jsonObject.put("msg", "Vui lòng đăng nhập trước");
			  this.getWriter().write(jsonObject.toString());	
			  return null;		
		  }
		  logger.info("请求ID:"+userid2);
		  if(u_id ==0){
			   jsonObject.put("error", -1);
		  	   jsonObject.put("msg", "用户不存在");
			  this.getWriter().write(jsonObject.toString());	
			  return null;				
		 }
	     if(rec_id ==0){
		     jsonObject.put("error", -2);
	 	     jsonObject.put("msg", "逾期不存在");
		  this.getWriter().write(jsonObject.toString());	
		  return null;				
	      }	 
	 	Date date=new Date();
	 	DateFormat format=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	 	String time=format.format(date);
	 	int yuqts = jbdcms2Service.getYQTS(rec_id);
	     //添加催收备注
	     DataRow row7=  new DataRow();	
		  row7.set("userid",u_id);	
		  row7.set("msg" ,remark);
		  row7.set("msgtype","备注");
		  row7.set("jkid",rec_id);//项目id
		  row7.set("create_time", new Date());
		  row7.set("cl_ren",userid2);
		  jbdcms2Service.insertUserChMsg(row7);
		  DataRow row8=  new DataRow();	
		  row8.set("user_id",u_id);	
		  row8.set("content" ,remark);
		  row8.set("jkjl_id",rec_id );
		  row8.set("kefuid",userid2);
		  row8.set("state",1);
		  row8.set("code",6);
		  row8.set("visitdate", time);
		  jbdcms2Service.insertUserKFMsg(row8);
		  DataRow row88=  new DataRow();
		  row88.set("id",rec_id);	
		  row88.set("zxcsbz" ,remark);
		  jbdcms2Service.updateUserWJDH(row88);
		  
		  DataRow row18=  new DataRow();	
		  row18.set("user_id",u_id);	
		  row18.set("content" ,remark);
		  row18.set("jkjl_id",rec_id );
		  row18.set("kefuid",userid2);
		  row18.set("state",1);
		  row18.set("code",6);
		  row18.set("visitdate", time);
		  row18.set("yuqts", yuqts);
		  jbdcms2Service.insertUserKFMsgHMD(row18);
		  
		  DataRow row288=  new DataRow();
		  row288.set("id",u_id);	
		  row288.set("heihu_zt",1);
		  jbdcms2Service.updateUserInfo(row288);
		  jsonObject.put("error", 1);
		  jsonObject.put("msg", "拉入黑名单成功");
		  this.getWriter().write(jsonObject.toString());	
	      return null ;
	   }
	
	/**
	* 获得客户的真实IP地址
	*
	* @return
	*/
	
	public String getipAddr( )
	{
		String online =SessionHelper.getString("online", getSession());
		return online;
	}
	
	/**
	 * 借款申请成功用户
	 * 
	 */
	public ActionResult doGetRecordListChg() throws Exception {
		
		logger.info("借款申请成功用户");
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
		
    	int temp = getIntParameter("temp",0);
		String  tempVelue = getStrParameter("tempvl"); 		

		String  startDate1 =getStrParameter("startDate");
		String  endDate1 =getStrParameter("endDate");
		String startDate="";
		String endDate="";
		if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
		String[] sourceStrArray1 = startDate1.split("-");
		String[] sourceStrArray2 = endDate1.split("-");
        startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
        endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
		}
        String hkstat = getStrParameter("hkstat");       
		//审核状态
		String commit = getStrParameter("commit");
		//定义用户选择条件
		String userId ="" ;
		String realName="";
		String phone = "" ;	
		String username ="" ;
		String off ="" ;
		logger.info("temp:"+temp+"startDate:"+startDate+" endDate"+endDate);
		if(temp ==1){
				   	
			userId =tempVelue ;
		}
		
		if(temp ==2){
					
			realName =tempVelue ;
		}
		if(temp ==3) {
					
			phone = tempVelue ;
		}
		if(temp ==4) {
			
			username = tempVelue ;
		}
		if(temp == 5) {
			off = 5+"";
		}
		//默认第一页
		int curPage  =getIntParameter("curPage",1);	
		DBPage page = jbdcms2Service.getRecordListChg(curPage,15,userId,realName,phone,startDate,endDate,commit,username,hkstat,off);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		row.set("hkstat",hkstat);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
    	return null ;  
		
	}
	/**
	 * 今日还款用户
	 * 
	 */
	public ActionResult doGetRecordJRList() throws Exception {
		
		logger.info("进入今日催收");
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
		
    	int temp = getIntParameter("temp",0);
    	Date date = new Date();
	    Calendar calendar = Calendar.getInstance();//日历对象
	    calendar.setTime(date);//设置当前日期			
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");		 
        String today = sdf.format(date);
        calendar.add(Calendar.DATE, 5);
        String nextDay = sdf.format(calendar.getTime());
        
		String  tempVelue = getStrParameter("tempvl"); 		
		String  startDate = today;
		String  endDate = nextDay;
        String hkstat = getStrParameter("hkstat"); 
		//审核状态
		String commit = getStrParameter("commit");
		//定义用户选择条件
		String userId ="" ;
		String realName="";
		String phone = "" ;	
		String username ="" ;
		String jkdate ="" ;
		String yue = "";
		String ri = "";
		String lastri = "";
		if((today.substring(0, 2).equals("28") && nextDay.substring(0, 2).equals("01"))|| today.substring(0, 2).equals("29") || today.substring(0, 2).equals("30") || today.substring(0, 2).equals("31")){
			yue = nextDay.substring(3, 5);
			ri = nextDay.substring(0, 2);
			lastri = today.substring(0, 2);
		}
		logger.info("temp:"+temp+"startDate:"+startDate+" endDate"+endDate);
		if(temp ==1){
				   	
			userId =tempVelue ;
		}
		
		if(temp ==2){
					
			realName =tempVelue ;
		}
		if(temp ==3) {
					
			phone = tempVelue ;
		}
		if(temp ==4) {
			
			username = tempVelue ;
		}	
		if(temp ==5) {
			
			jkdate = "34" ;
		}	
		//默认第一页
		int curPage  =getIntParameter("curPage",1);	
		DBPage page = jbdcms2Service.getRecordJRList(curPage,15,userId,realName,phone,startDate,endDate,yue,ri,lastri,commit,username,hkstat,cmsuserid,jkdate);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		row.set("hkstat",hkstat);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
    	return null ;  
		
	}
	/**
	 * 借款失败用户
	 * 
	 */
	
	public ActionResult doGetRecordListShibALL() throws Exception {
		
		logger.info("所有借款失败用户");
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
		
    	int temp = getIntParameter("temp",0);
		String  tempVelue = getStrParameter("tempvl"); 				
		String  startDate1 =getStrParameter("startDate");
		String  endDate1 =getStrParameter("endDate");
		String startDate="";
		String endDate="";
		if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
		String[] sourceStrArray1 = startDate1.split("-");
		String[] sourceStrArray2 = endDate1.split("-");
        startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
        endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
		} 
		//审核状态
		String commit = getStrParameter("commit");
		//定义用户选择条件
		String userId ="" ;
		String realName="";
		String phone = "" ;	
		String idCard ="" ;	
		String cmsid ="" ;	
		String xsgz ="" ;	
		logger.info(temp+"  "+tempVelue);
		if(temp ==1){	   	
			userId =tempVelue ;
		}
		
		if(temp ==2){
					
			realName =tempVelue ;
		}
		if(temp ==3) {
					
			phone = tempVelue ;
		}
		if(temp ==4) {
			
			cmsid = tempVelue ;
		}		
		if(temp ==5) {
			
			idCard = tempVelue ;
		}		
		if(temp ==6) {
			
			xsgz = tempVelue ;
		}		
		java.net.URLEncoder.encode(idCard);
		//默认第一页
		int curPage  =getIntParameter("curPage",1);	
		DBPage page = jbdcms2Service.getRecordListShibALL(curPage,15,userId,realName,phone,commit,idCard,cmsid,xsgz,startDate,endDate);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);	
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
    	return null ;  
		
	}
	/**
	 * 借款失败用户
	 * 
	 */
	
public ActionResult doGetRecordListShib() throws Exception {
		
		logger.info("借款申请用户");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuser_id =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuser_id =accessVeritifivationbase.checkCMSidAndip(cmsuser_id, getipAddr());
	    if(cmsuser_id==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuser_id);
		
    	int temp = getIntParameter("temp",0);
		String  tempVelue = getStrParameter("tempvl"); 				
         
		//审核状态
		String commit = getStrParameter("commit");
		//定义用户选择条件
		String userId ="" ;
		String realName="";
		String phone = "" ;	
		String idCard ="" ;	
		String cmsid ="" ;	
		String pingfen ="" ;	
		logger.info(temp+"  "+tempVelue);
		if(temp ==1){	   	
			userId =tempVelue ;
		}
		
		if(temp ==2){
					
			realName =tempVelue ;
		}
		if(temp ==3) {
					
			phone = tempVelue ;
		}
		if(temp ==4) {
			
			idCard = tempVelue ;
		}
		if(temp ==5) {
			
			cmsid = tempVelue ;
		}		
		if(temp ==6) {
			
			pingfen = tempVelue ;
		}		
		java.net.URLEncoder.encode(idCard);
		//默认第一页
		int curPage  =getIntParameter("curPage",1);	
		DBPage page = jbdcms2Service.getRecordListShib(curPage,15,userId,realName,phone,commit,idCard,cmsid,pingfen);
		//double tiaoshu = jbdcms2Service.getRecordListShibtiaoshu();
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);	
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
    	return null ;  
		
	}


public ActionResult doGetRecordListShib2() throws Exception {
	
	logger.info("借款申请用户");
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
	
	int temp = getIntParameter("temp",0);
	String  tempVelue = getStrParameter("tempvl"); 				
     
	//审核状态
	String commit = getStrParameter("commit");
	//定义用户选择条件
	String userId ="" ;
	String realName="";
	String phone = "" ;	
	String idCard ="" ;	
	String cmsid ="" ;	
	logger.info(temp+"  "+tempVelue);
	if(temp ==1){	   	
		userId =tempVelue ;
	}
	
	if(temp ==2){
				
		realName =tempVelue ;
	}
	if(temp ==3) {
				
		phone = tempVelue ;
	}
	if(temp ==4) {
		
		idCard = tempVelue ;
	}
	if(temp ==5) {
		
		cmsid = tempVelue ;
	}	
	java.net.URLEncoder.encode(idCard);
	//默认第一页
	int curPage  =getIntParameter("curPage",1);	
	DBPage page = jbdcms2Service.getRecordListShib2(curPage,15,userId,realName,phone,commit,idCard,cmsid);
	DataRow row = new DataRow();
	row.set("list", page);
	row.set("temp",temp);
	row.set("tempvalu",tempVelue);	
	JSONObject object = JSONObject.fromBean(row);	
	System.out.println(object.toString());
	this.getWriter().write(object.toString());	
	return null ;  
	
}

public ActionResult doGetRecordListShib3() throws Exception {
	
	logger.info("借款申请用户");
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
	
	int temp = getIntParameter("temp",0);
	String  tempVelue = getStrParameter("tempvl"); 				
     
	//审核状态
	String commit = getStrParameter("commit");
	//定义用户选择条件
	String userId ="" ;
	String realName="";
	String phone = "" ;	
	String idCard ="" ;	
	String cmsid ="" ;	
	logger.info(temp+"  "+tempVelue);
	if(temp ==1){	   	
		userId =tempVelue ;
	}
	
	if(temp ==2){
				
		realName =tempVelue ;
	}
	if(temp ==3) {
				
		phone = tempVelue ;
	}
	if(temp ==4) {
		
		idCard = tempVelue ;
	}	
	if(temp ==5) {
		
		cmsid = tempVelue ;
	}	
	java.net.URLEncoder.encode(idCard);
	//默认第一页
	int curPage  =getIntParameter("curPage",1);	
	DBPage page = jbdcms2Service.getRecordListShib3(curPage,15,userId,realName,phone,commit,idCard,cmsid);
	DataRow row = new DataRow();
	row.set("list", page);
	row.set("temp",temp);
	row.set("tempvalu",tempVelue);	
	JSONObject object = JSONObject.fromBean(row);	
	System.out.println(object.toString());
	this.getWriter().write(object.toString());	
	return null ;  
	
}
   /**
    * 逾期用户(借款催收)
    * 	GetYqList
    */
public ActionResult doGetYqList() throws Exception {
	
	logger.info("借款逾期用户2");
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
	
	int temp = getIntParameter("temp",0);
	String  tempVelue = getStrParameter("tempvl"); 		

	String  startDate1 =getStrParameter("startDate");
	String  endDate1 =getStrParameter("endDate");
	String startDate="";
	String endDate="";
	if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
	String[] sourceStrArray1 = startDate1.split("-");
	String[] sourceStrArray2 = endDate1.split("-");
    startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
    endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
	}
    String hkstat = getStrParameter("hkstat");       
	//审核状态
	String commit = getStrParameter("commit");
	//定义用户选择条件
	String userId ="" ;
	String realName="";
	String phone = "" ;	
	String cuishouname ="" ;
	String cuishouid ="" ;
	int bfhk =0 ;
	int yqhk =0 ;
	String state = "";
	logger.info("temp:"+temp+"startDate:"+startDate+" endDate"+endDate);
	if(temp ==1){
			   	
		userId =tempVelue ;
	}
	
	if(temp ==2){
				
		realName =tempVelue ;
	}
	if(temp ==3) {
				
		phone = tempVelue ;
	}
	if(temp ==4) {
		
		cuishouname = tempVelue ;
	}	
	if(temp ==5) {
		
		cuishouid = tempVelue ;
	}	
	if(temp ==6) {
		
		bfhk = 1 ;
	}	
	if(temp ==7) {
		
		yqhk = 1 ;
	}	
	if(temp ==8) {
		
		state = tempVelue ;
	}	
	
	 int userid = SessionHelper.getInt("cmsuserid", getSession());
    DataRow maprole =jbdcms2Service.getSdcmsUser(userid);
    int maproleid=maprole.getInt("roleid");
	
	//默认第一页
	int curPage  =getIntParameter("curPage",1);	
	DBPage page = jbdcms2Service.getYqList(curPage,15,userId,realName,phone,startDate,endDate,commit,cuishouname,cuishouid,bfhk,yqhk,hkstat,state,maproleid);
	DataRow row = new DataRow();
	row.set("list", page);
	row.set("temp",temp);
	row.set("tempvalu",tempVelue);
	row.set("hkstat",hkstat);
	JSONObject object = JSONObject.fromBean(row);	
	this.getWriter().write(object.toString());	
	return null ;  
  }
  /**
   * 发送逾期短信
   */
  public ActionResult  doGetSendCuiS() throws Exception {
	 
	  logger.info("进入逾期发送短信方法内");
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
	  
	  int userid2 = SessionHelper.getInt("cmsuserid", getSession());
      int userid = getIntParameter("userid",0);//用户id
      int jkid = getIntParameter("recorid" ,0);//借款项目id
	  if(userid ==0){
			   jsonObject.put("error", -1);
		  	   jsonObject.put("msg", "Không có khách hàng này");
			  this.getWriter().write(jsonObject.toString());	
			  return null;				
		}
	  if(jkid ==0){
		   jsonObject.put("error", -2);
	  	   jsonObject.put("msg", "Không có quá hạn");
		  this.getWriter().write(jsonObject.toString());	
		  return null;				
	}	
	//判断该用户当天是否有收到催收短信
    int msgcount  =jbdcms2Service.getmsgCount(userid);
	if(msgcount >0)  {
		jsonObject.put("error", -4);
	  	jsonObject.put("msg", "Đã gửi khách hàng này hôm nay, vui lòng đừng gửi lại tin nhắn truy nợ!");
		this.getWriter().write(jsonObject.toString());	
		return null;						
	}
	  
    //通过项目id 查询出用户的信息  
	DecimalFormat famt = new DecimalFormat("###,###");
    DataRow userInfo  =jbdcms2Service.getUserYqInfo(jkid) ;
    DataRow userShen  =jbdcms2Service.getUserYqShenFen(userid) ;
	String appName ="OCEAN";
	String userName = userInfo.getString("username");//用户名
	String cardUserName=userShen.getString("realname");//真实姓名
	String yuq_ts =userInfo.getString("yuq_ts");  //逾期天数
	String mobilePhone =userInfo.getString("mobilephone");
	String sjsh_money =userInfo.getString("sjsh_money").replace(",", "");
	String yuq_lx =userInfo.getString("yuq_lx").replace(",", "");
	String hkje =famt.format((Integer.parseInt(sjsh_money)+Integer.parseInt(yuq_lx)));
	userName = userName.substring(0, 4);
	  if(userName.equals("OCEAN")){
	    	appName="OCEAN";					    	
	    }
  	String content = "[{\"PhoneNumber\":\""+mobilePhone+"\",\"Message\":\""+appName+" xin thong bao:Khoan vay cua ban da qua han "+yuq_ts+" ngay, so tien phai tra la "+hkje+" VND, vui long dang nhap APP "+appName+" de tra no. Cam on ban!\",\"SmsGuid\":\""+mobilePhone+"\",\"ContentType\":1}]";
	String con = URLEncoder.encode(content, "utf-8");
	SendMsg sendMsg = new SendMsg();
	String returnString ="";
	//String returnString = SendMsg.sendMessageByGet(con,mobilePhone);
	Date date=new Date();
	DateFormat format=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	String time=format.format(date);
  	//催收记录添加到app 消息
  	 DataRow row6=  new DataRow();	
	  row6.set("userid",userid);
	  row6.set("title", "Trả nợ nhắc nhở") ;
	  row6.set("neirong" ,""+appName+" xin thong bao:Khoan vay cua ban da qua han "+yuq_ts+" ngay, so tien phai tra la "+hkje+" VND, vui long dang nhap APP "+appName+" de tra no. Cam on ban!");
	  row6.set("fb_time", new Date());
	  jbdcms2Service.insertUserMsg(row6);
  	//添加催收记录（便于后台展示）
	  DataRow row7=  new DataRow();	
	  row7.set("userid",userid);	
	  row7.set("msg" ,""+appName+" xin thong bao:Khoan vay cua ban da qua han "+yuq_ts+" ngay, so tien phai tra la "+hkje+" VND, vui long dang nhap APP "+appName+" de tra no. Cam on ban!");
	  row7.set("msgtype","手机短信");
	  row7.set("jkid",jkid);//项目id
	  row7.set("create_time", new Date());
	  row7.set("cl_ren",userid2);
	  jbdcms2Service.insertUserChMsg(row7);
	  DataRow row8=  new DataRow();	
	  row8.set("user_id",userid );	
	  row8.set("content" ,""+appName+" xin thong bao:Khoan vay cua ban da qua han "+yuq_ts+" ngay, so tien phai tra la "+hkje+" VND, vui long dang nhap APP "+appName+" de tra no. Cam on ban!");
	  row8.set("jkjl_id",jkid );
	  row8.set("kefuid",userid2);
	  row8.set("state",1);
	  row8.set("code",7);
	  row8.set("visitdate", time);
	  jbdcms2Service.insertUserKFMsg(row8);
  	
	if(returnString.equals("106")){
		jsonObject.put("error", 0);
	  	jsonObject.put("msg", "Tin nhắn truy nợ đã gửi thành công");
		this.getWriter().write(jsonObject.toString());	
	}else {
		jsonObject.put("error", -3);
	  	jsonObject.put("msg", "Tin nhắn truy nợ gửi thất bại");
		this.getWriter().write(jsonObject.toString());	
	}
    return null ;
  }
  public ActionResult doGetYqNListALL4() throws Exception {
	  
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
	  
	  int temp = getIntParameter("temp",0);
		String  tempVelue = getStrParameter("tempvl"); 		

		String  startDate1 =getStrParameter("startDate");
		String  endDate1 =getStrParameter("endDate");
		String startDate="";
		String endDate="";
		if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
		String[] sourceStrArray1 = startDate1.split("-");
		String[] sourceStrArray2 = endDate1.split("-");
        startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
        endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
		}
	    String  hkstat = getStrParameter("hkstat");       
		//审核状态
		String commit = getStrParameter("commit");

		String st_day = getStrParameter("s_day");//逾期最小天数
		
		String et_day = getStrParameter("e_day");//逾期最大天数
	  	String userId ="" ;
		String realName="";
		String phone = "" ;	
		String idCard ="" ;
		String cuishou ="" ;
	    int curPage  =getIntParameter("curPage",1);	
		DBPage page = jbdcms2Service.getYqNListALL4(curPage,400,userId,realName,phone,cuishou,startDate,endDate,commit,idCard,hkstat,st_day,et_day);
		DataRow row = new DataRow();
		row.set("list", page);
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());	
		return null ;  
  }
  public ActionResult doGetYqNListALL1() throws Exception {
	  
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
	  
	  int temp = getIntParameter("temp",0);
		String  tempVelue = getStrParameter("tempvl"); 		

		String  startDate1 =getStrParameter("startDate");
		String  endDate1 =getStrParameter("endDate");
		String startDate="";
		String endDate="";
		if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
		String[] sourceStrArray1 = startDate1.split("-");
		String[] sourceStrArray2 = endDate1.split("-");
        startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
        endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
		}
	    String  hkstat = getStrParameter("hkstat");       
		//审核状态
		String commit = getStrParameter("commit");

		String st_day = getStrParameter("s_day");//逾期最小天数
		
		String et_day = getStrParameter("e_day");//逾期最大天数
	  	String userId ="" ;
		String realName="";
		String phone = "" ;	
		String idCard ="" ;
		String cuishou ="" ;
	    int curPage  =getIntParameter("curPage",1);	
		DBPage page = jbdcms2Service.getYqNListALL1(curPage,400,userId,realName,phone,cuishou,startDate,endDate,commit,idCard,hkstat,st_day,et_day);
		DataRow row = new DataRow();
		row.set("list", page);
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());	
		return null ;  
  }
  public ActionResult doGetYqNListALL2() throws Exception {
	  
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
	  
	  int temp = getIntParameter("temp",0);
		String  tempVelue = getStrParameter("tempvl"); 		

		String  startDate1 =getStrParameter("startDate");
		String  endDate1 =getStrParameter("endDate");
		String startDate="";
		String endDate="";
		if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
		String[] sourceStrArray1 = startDate1.split("-");
		String[] sourceStrArray2 = endDate1.split("-");
        startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
        endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
		}
	    String  hkstat = getStrParameter("hkstat");       
		//审核状态
		String commit = getStrParameter("commit");

		String st_day = getStrParameter("s_day");//逾期最小天数
		
		String et_day = getStrParameter("e_day");//逾期最大天数
	  	String userId ="" ;
		String realName="";
		String phone = "" ;	
		String idCard ="" ;
		String cuishou ="" ;
	    int curPage  =getIntParameter("curPage",1);	
		DBPage page = jbdcms2Service.getYqNListALL2(curPage,400,userId,realName,phone,cuishou,startDate,endDate,commit,idCard,hkstat,st_day,et_day);
		DataRow row = new DataRow();
		row.set("list", page);
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());	
		return null ;  
  }
  public ActionResult doGetYqNListALL3() throws Exception {
	  
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
	  
	  int temp = getIntParameter("temp",0);
		String  tempVelue = getStrParameter("tempvl"); 		

		String  startDate1 =getStrParameter("startDate");
		String  endDate1 =getStrParameter("endDate");
		String startDate="";
		String endDate="";
		if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
		String[] sourceStrArray1 = startDate1.split("-");
		String[] sourceStrArray2 = endDate1.split("-");
        startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
        endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
		}
	    String  hkstat = getStrParameter("hkstat");       
		//审核状态
		String commit = getStrParameter("commit");

		String st_day = getStrParameter("s_day");//逾期最小天数
		
		String et_day = getStrParameter("e_day");//逾期最大天数
	  	String userId ="" ;
		String realName="";
		String phone = "" ;	
		String idCard ="" ;
		String cuishou ="" ;
	    int curPage  =getIntParameter("curPage",1);	
		DBPage page = jbdcms2Service.getYqNListALL3(curPage,400,userId,realName,phone,cuishou,startDate,endDate,commit,idCard,hkstat,st_day,et_day);
		DataRow row = new DataRow();
		row.set("list", page);
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());	
		return null ;  
  }
  public static boolean isWeekend(String date) throws ParseException{  
      DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");         
      Date bdate = format1.parse(date);  
      Calendar cal = Calendar.getInstance();  
      cal.setTime(bdate);
      System.out.println(cal);
      System.out.println(Calendar.SUNDAY);
      if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY||cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){  
          return true;  
      }  
      else return false;  
  }
  /**
   * 逾期用户(借款催收)
   * 	GetYqList
   */
public ActionResult doGetYqNList() throws Exception {
	
	logger.info("待催收用户");
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
	
	int temp = getIntParameter("temp",0);
	String  tempVelue = getStrParameter("tempvl"); 		
	String  shenheid =  "";		
	if(cmsuserid == 6 || cmsuserid == 8 || cmsuserid == 888) {
		shenheid = getStrParameter("shid");
	}
	String  startDate1 =getStrParameter("startDate");
	String  endDate1 =getStrParameter("endDate");
	String startDate="";
	String endDate="";
	if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
	String[] sourceStrArray1 = startDate1.split("-");
	String[] sourceStrArray2 = endDate1.split("-");
    startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
    endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
	}
    String  hkstat = getStrParameter("hkstat");       
	//审核状态
	String commit = getStrParameter("commit");
	//定义用户选择条件
	String userId ="" ;
	String realName="";
	String phone = "" ;	
	String idCard ="" ;
	String cuishou ="" ;
	
	//逾期天数
	String st_day = getStrParameter("s_day");//逾期最小天数
	
	String et_day = getStrParameter("e_day");//逾期最大天数
    System.out.println(st_day);
    System.out.println(et_day);
	if(temp ==1){
			   	
		userId =tempVelue ;
	}
	
	if(temp ==2){
				
		realName =tempVelue ;
	}
	if(temp ==3) {
				
		phone = tempVelue ;
	}
	if(temp ==4) {
		
		idCard = tempVelue ;
	}	
	if(temp ==7) {
		
		cuishou = tempVelue ;
	}
	
	//默认第一页
	int curPage  =getIntParameter("curPage",1);	
	DBPage page = jbdcms2Service.getYqNList(curPage,15,userId,realName,phone,cuishou,shenheid,startDate,endDate,commit,idCard,hkstat,st_day,et_day);
	DataRow row = new DataRow();
	row.set("list", page);
	row.set("temp",temp);
	row.set("tempvalu",tempVelue);
	row.set("hkstat",hkstat);
	JSONObject object = JSONObject.fromBean(row);
	this.getWriter().write(object.toString());	
	return null ;  
   }
   
/**
 * 逾期用户(借款催收>7天)--催收组
 * 	GetYqList
 */
public ActionResult doGetYqCSList() throws Exception {
	
	logger.info("待催收用户");
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
	 
	int temp = getIntParameter("temp",0);
	String  tempVelue = getStrParameter("tempvl"); 		

	String  startDate1 =getStrParameter("startDate");
	String  endDate1 =getStrParameter("endDate");
	String startDate="";
	String endDate="";
	if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
	String[] sourceStrArray1 = startDate1.split("-");
	String[] sourceStrArray2 = endDate1.split("-");
    startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
    endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
	}
   String  hkstat = getStrParameter("hkstat");       
	//审核状态
	String commit = getStrParameter("commit");
//	JSONObject jsonObject = new JSONObject();
	int user_id = SessionHelper.getInt("cmsuserid", getSession());   //操作员ID
	if(user_id == 0){	   
	  	  jsonObject.put("err", -1);		  
		  this.getWriter().write(jsonObject.toString());	
		  return null;
	    }
	logger.info("请求ID:"+user_id);
	String csname = jbdcms2Service.getNameCms(user_id);
	//定义用户选择条件
	String userId ="" ;
	String realName="";
	String phone = "" ;	
	String idCard ="" ;
	
	//逾期天数
	String st_day = getStrParameter("s_day");//逾期最小天数
	
	String et_day = getStrParameter("e_day");//逾期最大天数
 
	if(temp ==1){
			   	
		userId =tempVelue ;
	}
	
	if(temp ==2){
				
		realName =tempVelue ;
	}
	if(temp ==3) {
				
		phone = tempVelue ;
	}
	if(temp ==4) {
		
		idCard = tempVelue ;
	}	
	
	//默认第一页
	int curPage  =getIntParameter("curPage",1);	
	DBPage page = jbdcms2Service.getYqCSList(curPage,15,userId,realName,phone,startDate,endDate,commit,idCard,hkstat,st_day,et_day);
	List<DataRow> list=jbdcms2Service.getYqCSList(userId,realName,phone,startDate,endDate,commit,idCard,hkstat ,st_day,et_day);
	int sjsh =0;
	int yuq =0;
	int hkje =0 ;
	String sjshmoney = "";
	String yuqmoney = "";
	DecimalFormat format = new DecimalFormat("###,###");
	for (DataRow dataRow : list) {
		sjshmoney = dataRow.getString("sjsh_money").replace(",", "");
		yuqmoney = dataRow.getString("yuq_lx").replace(",", "");
		sjsh = Integer.parseInt(sjshmoney);
		yuq = Integer.parseInt(yuqmoney);
		hkje = sjsh +yuq;
	} 
	DataRow row = new DataRow();
	row.set("list", page);
	row.set("hkje", format.format(hkje));
	row.set("temp",temp);
	row.set("tempvalu",tempVelue);
	row.set("hkstat",hkstat);
	JSONObject object = JSONObject.fromBean(row);	
	this.getWriter().write(object.toString());	
	return null ;  
 }

/**
 * 逾期用户(借款催收<3天)--客服组
 * 	GetYqList
 */
public ActionResult doGetYqKFList() throws Exception {
	
	logger.info("待催收用户");
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
	 
	int temp = getIntParameter("temp",0);
	String  tempVelue = getStrParameter("tempvl"); 		

	String  startDate1 =getStrParameter("startDate");
	String  endDate1 =getStrParameter("endDate");
	String startDate="";
	String endDate="";
	if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
	String[] sourceStrArray1 = startDate1.split("-");
	String[] sourceStrArray2 = endDate1.split("-");
    startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
    endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
	}
  String  hkstat = getStrParameter("hkstat");       
	//审核状态
	String commit = getStrParameter("commit");
//	JSONObject jsonObject = new JSONObject();
	int user_id = SessionHelper.getInt("cmsuserid", getSession());   //操作员ID
	if(user_id == 0){	   
	  	  jsonObject.put("err", -1);		  
		  this.getWriter().write(jsonObject.toString());	
		  return null;
	    }
	logger.info("请求ID:"+user_id);
	String csname = jbdcms2Service.getNameCms(user_id);
	//定义用户选择条件
	String userId ="" ;
	String realName="";
	String phone = "" ;	
	String idCard ="" ;
	
	//逾期天数
	String st_day = getStrParameter("s_day");//逾期最小天数
	
	String et_day = getStrParameter("e_day");//逾期最大天数
 
	if(temp ==1){
			   	
		userId =tempVelue ;
	}
	
	if(temp ==2){
				
		realName =tempVelue ;
	}
	if(temp ==3) {
				
		phone = tempVelue ;
	}
	if(temp ==4) {
		
		idCard = tempVelue ;
	}	
	
	//默认第一页
	int curPage  =getIntParameter("curPage",1);	

	DBPage page = jbdcms2Service.getYqKFList(curPage,15,userId,realName,phone,startDate,endDate,commit,idCard,hkstat,st_day,et_day);
	List<DataRow> list=jbdcms2Service.getYqKFList(userId,realName,phone,startDate,endDate,commit,idCard,hkstat ,st_day,et_day);
	int sjsh =0;
	int yuq =0;
	int hkje =0 ;
	String hkjedata []= new String[list.size()];
	String sjshmoney = "";
	String yuqmoney = "";
	DecimalFormat format = new DecimalFormat("###,###");
	for (DataRow dataRow : list) {
		sjshmoney = dataRow.getString("sjsh_money").replace(",", "");
		yuqmoney = dataRow.getString("yuq_lx").replace(",", "");
		sjsh = Integer.parseInt(sjshmoney);
		yuq = Integer.parseInt(yuqmoney);
		hkje = sjsh +yuq;
	} 
	DataRow row = new DataRow();
	row.set("list", page);
	row.set("hkje", format.format(hkje));
	row.set("temp",temp);
	row.set("tempvalu",tempVelue);
	row.set("hkstat",hkstat);
	JSONObject object = JSONObject.fromBean(row);	
	this.getWriter().write(object.toString());	
	return null ;  
 }

/**
 * 催收周云
 * 	GetYqList
 */
//1组
public ActionResult doGetYqM0List() throws Exception {
	
	logger.info("待催收用户M0");
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
	 
	int temp = getIntParameter("temp",0);
	String  tempVelue = getStrParameter("tempvl"); 		
	
	String  startDate1 =getStrParameter("startDate");
	String  endDate1 =getStrParameter("endDate");
	String startDate="";
	String endDate="";
	if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
		String[] sourceStrArray1 = startDate1.split("-");
		String[] sourceStrArray2 = endDate1.split("-");
		startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
		endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
	}
	String  hkstat = getStrParameter("hkstat");       
	//审核状态
	String commit = getStrParameter("commit");

	//定义用户选择条件
	String userId ="" ;
	String realName="";
	String phone = "" ;	
	String idCard ="" ;
	String cuishouid ="" ;
	String jkdate ="" ;
	String yuqts ="" ;
	//逾期天数
	String st_day = getStrParameter("s_day");//逾期最小天数
	
	String et_day = getStrParameter("e_day");//逾期最大天数
	
	if(temp ==1){
		
		userId =tempVelue ;
	}
	
	if(temp ==2){
		
		realName =tempVelue ;
	}
	if(temp ==3) {
		
		phone = tempVelue ;
	}
	if(temp ==4) {
		
		idCard = tempVelue ;
	}	
	if(temp ==5) {
		
		cuishouid = tempVelue ;
	}	
	if(temp ==6) {
		
		jkdate = "34" ;
	}	
	if(temp ==7) {
		
		yuqts = tempVelue ;
	}
	
	//一组的成员
	List<DataRow> cuishoum1 = jbdcms2Service.getAllcuishouM0();
	int cuishouzuyqm1[] = new int[cuishoum1.size()];
	
	for (int m = 0; m < cuishoum1.size(); m++) {
		DataRow row = cuishoum1.get(m);
		cuishouzuyqm1[m] = row.getInt("user_id");
	}
	//默认第一页
	int curPage  =getIntParameter("curPage",1);	
	DBPage page = jbdcms2Service.getYqM0List(curPage,15,userId,realName,phone,startDate,endDate,commit,idCard,hkstat,st_day,et_day,cmsuserid,cuishouid,cuishouzuyqm1,jkdate,yuqts);
	List<DataRow> list=jbdcms2Service.getYqM0List(userId,realName,phone,startDate,endDate,commit,idCard,hkstat ,st_day,et_day,cmsuserid,cuishouid,cuishouzuyqm1,jkdate,yuqts);
	int sjsh =0;
	int yuq =0;
	int hkjeje[] = new int[list.size()] ;
	String hkje[] = new String[list.size()] ;
	String sjshmoney = "";
	String yuqmoney = "";
	DecimalFormat format = new DecimalFormat("###,###");
	for (int i=0;i<list.size();i++) {
		DataRow dataRow = list.get(i);
		sjshmoney = dataRow.getString("sjsh_money").replace(",", "");
		yuqmoney = dataRow.getString("yuq_lx").replace(",", "");
		sjsh = Integer.parseInt(sjshmoney);
		yuq = Integer.parseInt(yuqmoney);
		hkjeje[i] = sjsh +yuq;
		hkje[i] = format.format(hkjeje[i]);
		
	} 
	DataRow row = new DataRow();
	row.set("list", page);
	row.set("hkje", hkje);
	row.set("temp",temp);
	row.set("tempvalu",tempVelue);
	row.set("hkstat",hkstat);
	JSONObject object = JSONObject.fromBean(row);	
	this.getWriter().write(object.toString());	
	return null ;  
}

//xiong20190614
public ActionResult doGetYqZYList() throws Exception {
	
	logger.info("待催收用户ZY");
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
	 
	int temp = getIntParameter("temp",0);
	String  tempVelue = getStrParameter("tempvl"); 		

	String  startDate1 =getStrParameter("startDate");
	String  endDate1 =getStrParameter("endDate");
	String startDate="";
	String endDate="";
	if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
	String[] sourceStrArray1 = startDate1.split("-");
	String[] sourceStrArray2 = endDate1.split("-");
    startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
    endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
	}
  String  hkstat = getStrParameter("hkstat");       
	//审核状态
	String commit = getStrParameter("commit");
	int user_id = SessionHelper.getInt("cmsuserid", getSession());   //操作员ID
	/*if(user_id == 0){	   
	  	  jsonObject.put("err", -1);		  
		  this.getWriter().write(jsonObject.toString());	
		  return null;
	    }*/
	String csname = jbdcms2Service.getNameCms(user_id);
	//定义用户选择条件
	String userId ="" ;
	String realName="";
	String phone = "" ;	
	String idCard ="" ;
	String cuishouid ="" ;
	String jkdate ="" ;
	String yuqts ="" ;
	
	//逾期天数
	String st_day = getStrParameter("s_day");//逾期最小天数
	
	String et_day = getStrParameter("e_day");//逾期最大天数
 
	if(temp ==1){
			   	
		userId =tempVelue ;
	}
	
	if(temp ==2){
				
		realName =tempVelue ;
	}
	if(temp ==3) {
				
		phone = tempVelue ;
	}
	if(temp ==4) {
		
		idCard = tempVelue ;
	}	
	if(temp ==5) {
		
		cuishouid = tempVelue ;
	}	
	if(temp ==6) {
		
		jkdate = "34" ;
	}	
	if(temp ==7) {
		
		yuqts = tempVelue;
	}	
		
	//一组的成员
	List<DataRow> cuishoum1 = jbdcms2Service.getAllcuishouM1();
	int cuishouzuyqm1[] = new int[cuishoum1.size()];
	
	for (int m = 0; m < cuishoum1.size(); m++) {
		DataRow row = cuishoum1.get(m);
		cuishouzuyqm1[m] = row.getInt("user_id");
	}
	//默认第一页
	int curPage  =getIntParameter("curPage",1);	
	DBPage page = jbdcms2Service.getYqZYList(curPage,15,userId,realName,phone,startDate,endDate,commit,idCard,hkstat,st_day,et_day,cmsuserid,cuishouid,cuishouzuyqm1,jkdate,yuqts);
	List<DataRow> list=jbdcms2Service.getYqZYList(userId,realName,phone,startDate,endDate,commit,idCard,hkstat ,st_day,et_day,cmsuserid,cuishouid,cuishouzuyqm1,jkdate,yuqts);
	double hkje=0;
	DecimalFormat format = new DecimalFormat("###,###");
	for (int i=0;i<list.size();i++) {
		DataRow dataRow = list.get(i);
		String sjshmoney = dataRow.getString("sjsh_money").replace(",", "");
		String yuqmoney = dataRow.getString("yuq_lx").replace(",", "");
		int sjsh = Integer.parseInt(sjshmoney);
		int yuq = Integer.parseInt(yuqmoney);
		hkje += (sjsh+yuq);
	} 
	DataRow row = new DataRow();
	row.set("list", page);
	row.set("hkje", format.format(hkje));
	row.set("temp",temp);
	row.set("tempvalu",tempVelue);
	row.set("hkstat",hkstat);
	JSONObject object = JSONObject.fromBean(row);	
	this.getWriter().write(object.toString());	
	return null ;  
 }
//xiong20190614原来方法注释
//public ActionResult doGetYqZYList() throws Exception {
//	
//	logger.info("待催收用户ZY");
//	JSONObject jsonObject = new JSONObject(); 
//	   int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());
//	   
//	   if(cmsuserid==0){
//		   jsonObject.put("error", -1);
//		   jsonObject.put("msg", "Vui lòng đăng nhập trước");
//		   this.getWriter().write(jsonObject.toString());	
//		   return null;				 
//		   
//	   }
//	int temp = getIntParameter("temp",0);
//	String  tempVelue = getStrParameter("tempvl"); 		
//
//	String  startDate1 =getStrParameter("startDate");
//	String  endDate1 =getStrParameter("endDate");
//	String startDate="";
//	String endDate="";
//	if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
//	String[] sourceStrArray1 = startDate1.split("-");
//	String[] sourceStrArray2 = endDate1.split("-");
//    startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
//    endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
//	}
//  String  hkstat = getStrParameter("hkstat");       
//	//审核状态
//	String commit = getStrParameter("commit");
//
//	//定义用户选择条件
//	String userId ="" ;
//	String realName="";
//	String phone = "" ;	
//	String idCard ="" ;
//	String cuishouid ="" ;
//	String jkdate ="" ;
//	String yuqts ="" ;
//	
//	//逾期天数
//	String st_day = getStrParameter("s_day");//逾期最小天数
//	
//	String et_day = getStrParameter("e_day");//逾期最大天数
// 
//	if(temp ==1){
//			   	
//		userId =tempVelue ;
//	}
//	
//	if(temp ==2){
//				
//		realName =tempVelue ;
//	}
//	if(temp ==3) {
//				
//		phone = tempVelue ;
//	}
//	if(temp ==4) {
//		
//		idCard = tempVelue ;
//	}	
//	if(temp ==5) {
//		
//		cuishouid = tempVelue ;
//	}	
//	if(temp ==6) {
//		
//		jkdate = "34" ;
//	}	
//	if(temp ==7) {
//		
//		yuqts = tempVelue;
//	}	
//		
//	//一组的成员
//	List<DataRow> cuishoum1 = jbdcms2Service.getAllcuishouM1();
//	int cuishouzuyqm1[] = new int[cuishoum1.size()];
//	
//	for (int m = 0; m < cuishoum1.size(); m++) {
//		DataRow row = cuishoum1.get(m);
//		cuishouzuyqm1[m] = row.getInt("user_id");
//	}
//	//默认第一页
//	int curPage  =getIntParameter("curPage",1);	
//	DBPage page = jbdcms2Service.getYqZYList(curPage,15,userId,realName,phone,startDate,endDate,commit,idCard,hkstat,st_day,et_day,cmsuserid,cuishouid,cuishouzuyqm1,jkdate,yuqts);
//	List<DataRow> list=jbdcms2Service.getYqZYList(userId,realName,phone,startDate,endDate,commit,idCard,hkstat ,st_day,et_day,cmsuserid,cuishouid,cuishouzuyqm1,jkdate,yuqts);
//	int sjsh =0;
//	int yuq =0;
//	int hkjeje[] = new int[list.size()] ;
//	String hkje[] = new String[list.size()] ;
//	String sjshmoney = "";
//	String yuqmoney = "";
//	DecimalFormat format = new DecimalFormat("###,###");
//	for (int i=0;i<list.size();i++) {
//		DataRow dataRow = list.get(i);
//		sjshmoney = dataRow.getString("sjsh_money").replace(",", "");
//		yuqmoney = dataRow.getString("yuq_lx").replace(",", "");
//		sjsh = Integer.parseInt(sjshmoney);
//		yuq = Integer.parseInt(yuqmoney);
//		hkjeje[i] = sjsh +yuq;
//		hkje[i] = format.format(hkjeje[i]);
//
//	} 
//	DataRow row = new DataRow();
//	row.set("list", page);
//	row.set("hkje", hkje);
//	row.set("temp",temp);
//	row.set("tempvalu",tempVelue);
//	row.set("hkstat",hkstat);
//	JSONObject object = JSONObject.fromBean(row);	
//	this.getWriter().write(object.toString());	
//	return null ;  
// }
/**
 * 催收周云
 * 	GetYqList
 */
//2组
//xiong20190614-黑名单新增方法
public ActionResult doGetYqYSMList() throws Exception {
	
	logger.info("待催收用户YSM");
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
	 
	int temp = getIntParameter("temp",0);
	String  tempVelue = getStrParameter("tempvl"); 		
	
	String  startDate1 =getStrParameter("startDate");
	String  endDate1 =getStrParameter("endDate");
	String startDate="";
	String endDate="";
	if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
		String[] sourceStrArray1 = startDate1.split("-");
		String[] sourceStrArray2 = endDate1.split("-");
		startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
		endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
	}
	String  hkstat = getStrParameter("hkstat");       
	//审核状态
	String commit = getStrParameter("commit");
	int user_id = SessionHelper.getInt("cmsuserid", getSession());   //操作员ID
	/*if(user_id == 0){	   
	  	  jsonObject.put("err", -1);		  
		  this.getWriter().write(jsonObject.toString());	
		  return null;
	    }*/
	String csname = jbdcms2Service.getNameCms(user_id);
	//定义用户选择条件
	String userId ="" ;
	String realName="";
	String phone = "" ;	
	String idCard ="" ;
	String cuishouid ="" ;
	String jkdate ="" ;
	String yuqts ="" ;
	
	//逾期天数
	String st_day = getStrParameter("s_day");//逾期最小天数
	
	String et_day = getStrParameter("e_day");//逾期最大天数
	
	if(temp ==1){
		
		userId =tempVelue ;
	}
	
	if(temp ==2){
		
		realName =tempVelue ;
	}
	if(temp ==3) {
		
		phone = tempVelue ;
	}
	if(temp ==4) {
		
		idCard = tempVelue ;
	}
	if(temp ==5) {
		
		cuishouid = tempVelue ;
	}
	if(temp ==6) {
		
		jkdate = "34" ;
	}
	if(temp ==7) {
		
		yuqts = tempVelue;
	}
	//二组的成员
	List<DataRow> cuishoum1 = jbdcms2Service.getAllcuishouM2();
	int cuishouzuyqm1[] = new int[cuishoum1.size()];
	
	for (int m = 0; m < cuishoum1.size(); m++) {
		DataRow row = cuishoum1.get(m);
		cuishouzuyqm1[m] = row.getInt("user_id");
	}
	
	//默认第一页
	int curPage  =getIntParameter("curPage",1);	
	DBPage page = jbdcms2Service.getYqYSMList(curPage,15,userId,realName,phone,startDate,endDate,commit,idCard,hkstat,st_day,et_day,cmsuserid,cuishouid,cuishouzuyqm1,jkdate,yuqts);
	List<DataRow> list=jbdcms2Service.getYqYSMList(userId,realName,phone,startDate,endDate,commit,idCard,hkstat ,st_day,et_day,cmsuserid,cuishouid,cuishouzuyqm1,jkdate,yuqts);
	double hkje=0;
	DecimalFormat format = new DecimalFormat("###,###");
	for (int i=0;i<list.size();i++) {
		DataRow dataRow = list.get(i);
		String sjshmoney = dataRow.getString("sjsh_money").replace(",", "");
		String yuqmoney = dataRow.getString("yuq_lx").replace(",", "");
		int sjsh = Integer.parseInt(sjshmoney);
		int yuq = Integer.parseInt(yuqmoney);
		hkje += (sjsh+yuq);
	}
	DataRow row = new DataRow();
	row.set("list", page);
	row.set("hkje", format.format(hkje));
	row.set("temp",temp);
	row.set("tempvalu",tempVelue);
	row.set("hkstat",hkstat);
	JSONObject object = JSONObject.fromBean(row);	
	this.getWriter().write(object.toString());	
	return null ;  
}
//xiong20190614-黑名单原来的方法注释掉
//public ActionResult doGetYqYSMList() throws Exception {
//	
//	logger.info("待催收用户YSM");
//	JSONObject jsonObject = new JSONObject(); 
//	int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());
//	
//	if(cmsuserid==0){
//		jsonObject.put("error", -1);
//		jsonObject.put("msg", "Vui lòng đăng nhập trước");
//		this.getWriter().write(jsonObject.toString());	
//		return null;				 
//		
//	}
//	int temp = getIntParameter("temp",0);
//	String  tempVelue = getStrParameter("tempvl"); 		
//	
//	String  startDate1 =getStrParameter("startDate");
//	String  endDate1 =getStrParameter("endDate");
//	String startDate="";
//	String endDate="";
//	if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
//		String[] sourceStrArray1 = startDate1.split("-");
//		String[] sourceStrArray2 = endDate1.split("-");
//		startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
//		endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
//	}
//	String  hkstat = getStrParameter("hkstat");       
//	//审核状态
//	String commit = getStrParameter("commit");
//
//	//定义用户选择条件
//	String userId ="" ;
//	String realName="";
//	String phone = "" ;	
//	String idCard ="" ;
//	String cuishouid ="" ;
//	String jkdate ="" ;
//	String yuqts ="" ;
//	
//	//逾期天数
//	String st_day = getStrParameter("s_day");//逾期最小天数
//	
//	String et_day = getStrParameter("e_day");//逾期最大天数
//	
//	if(temp ==1){
//		
//		userId =tempVelue ;
//	}
//	
//	if(temp ==2){
//		
//		realName =tempVelue ;
//	}
//	if(temp ==3) {
//		
//		phone = tempVelue ;
//	}
//	if(temp ==4) {
//		
//		idCard = tempVelue ;
//	}
//	if(temp ==5) {
//		
//		cuishouid = tempVelue ;
//	}
//	if(temp ==6) {
//		
//		jkdate = "34" ;
//	}
//	if(temp ==7) {
//		
//		yuqts = tempVelue;
//	}
//	//二组的成员
//	List<DataRow> cuishoum1 = jbdcms2Service.getAllcuishouM2();
//	int cuishouzuyqm1[] = new int[cuishoum1.size()];
//	
//	for (int m = 0; m < cuishoum1.size(); m++) {
//		DataRow row = cuishoum1.get(m);
//		cuishouzuyqm1[m] = row.getInt("user_id");
//	}
//	
//	//默认第一页
//	int curPage  =getIntParameter("curPage",1);	
//	DBPage page = jbdcms2Service.getYqYSMList(curPage,15,userId,realName,phone,startDate,endDate,commit,idCard,hkstat,st_day,et_day,cmsuserid,cuishouid,cuishouzuyqm1,jkdate,yuqts);
//	List<DataRow> list=jbdcms2Service.getYqYSMList(userId,realName,phone,startDate,endDate,commit,idCard,hkstat ,st_day,et_day,cmsuserid,cuishouid,cuishouzuyqm1,jkdate,yuqts);
//	int sjsh =0;
//	int yuq =0;
//	int hkjeje[] = new int[list.size()] ;
//	String hkje[] = new String[list.size()] ;
//	String sjshmoney = "";
//	String yuqmoney = "";
//	DecimalFormat format = new DecimalFormat("###,###");
//	for (int i=0;i<list.size();i++) {
//		DataRow dataRow = list.get(i);
//		sjshmoney = dataRow.getString("sjsh_money").replace(",", "");
//		yuqmoney = dataRow.getString("yuq_lx").replace(",", "");
//		sjsh = Integer.parseInt(sjshmoney);
//		yuq = Integer.parseInt(yuqmoney);
//		hkjeje[i] = sjsh +yuq;
//		hkje[i] = format.format(hkjeje[i]);
//		
//	} 
//	DataRow row = new DataRow();
//	row.set("list", page);
//	row.set("hkje", hkje);
//	row.set("temp",temp);
//	row.set("tempvalu",tempVelue);
//	row.set("hkstat",hkstat);
//	JSONObject object = JSONObject.fromBean(row);	
//	this.getWriter().write(object.toString());	
//	return null ;  
//}
/**
 * 催收周云
 * 	GetYqList
 */
//3组
//xiong20190614黑名单
public ActionResult doGetYqLYLList() throws Exception {
	
	logger.info("待催收用户LYL");
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
	 
	int temp = getIntParameter("temp",0);
	String  tempVelue = getStrParameter("tempvl"); 		
	
	String  startDate1 =getStrParameter("startDate");
	String  endDate1 =getStrParameter("endDate");
	String startDate="";
	String endDate="";
	if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
		String[] sourceStrArray1 = startDate1.split("-");
		String[] sourceStrArray2 = endDate1.split("-");
		startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
		endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
	}
	String  hkstat = getStrParameter("hkstat");       
	//审核状态
	String commit = getStrParameter("commit");
	int user_id = SessionHelper.getInt("cmsuserid", getSession());   //操作员ID
	/*if(user_id == 0){	   
	  	  jsonObject.put("err", -1);		  
		  this.getWriter().write(jsonObject.toString());	
		  return null;
	    }*/
	String csname = jbdcms2Service.getNameCms(user_id);
	//定义用户选择条件
	String userId ="" ;
	String realName="";
	String phone = "" ;	
	String idCard ="" ;
	String cuishouid ="" ;
	String jkdate ="" ;
	String yuqts ="" ;
	
	//逾期天数
	String st_day = getStrParameter("s_day");//逾期最小天数
	
	String et_day = getStrParameter("e_day");//逾期最大天数
	
	if(temp ==1){
		
		userId =tempVelue ;
	}
	
	if(temp ==2){
		
		realName =tempVelue ;
	}
	if(temp ==3) {
		
		phone = tempVelue ;
	}
	if(temp ==4) {
		
		idCard = tempVelue ;
	}	
	if(temp ==5) {
		
		cuishouid = tempVelue ;
	}	
	if(temp ==6) {
		
		jkdate = "34" ;
	}	
	if(temp ==7) {
		
		yuqts = tempVelue;
	}	
	//三组的成员
	List<DataRow> cuishoum1 = jbdcms2Service.getAllcuishouM3();
	int cuishouzuyqm1[] = new int[cuishoum1.size()];
	
	for (int m = 0; m < cuishoum1.size(); m++) {
		DataRow row = cuishoum1.get(m);
		cuishouzuyqm1[m] = row.getInt("user_id");
	}
	//默认第一页
	int curPage  =getIntParameter("curPage",1);	
	DBPage page = jbdcms2Service.getYqLYLList(curPage,15,userId,realName,phone,startDate,endDate,commit,idCard,hkstat,st_day,et_day,cmsuserid,cuishouid,cuishouzuyqm1,jkdate,yuqts);
	List<DataRow> list=jbdcms2Service.getYqLYLList(userId,realName,phone,startDate,endDate,commit,idCard,hkstat ,st_day,et_day,cmsuserid,cuishouid,cuishouzuyqm1,jkdate,yuqts);
	double hkje=0;
	DecimalFormat format = new DecimalFormat("###,###");
	for (int i=0;i<list.size();i++) {
		DataRow dataRow = list.get(i);
		String sjshmoney = dataRow.getString("sjsh_money").replace(",", "");
		String yuqmoney = dataRow.getString("yuq_lx").replace(",", "");
		int sjsh = Integer.parseInt(sjshmoney);
		int yuq = Integer.parseInt(yuqmoney);
		hkje += (sjsh+yuq);
	}
	DataRow row = new DataRow();
	row.set("list", page);
	row.set("hkje", format.format(hkje));
	row.set("temp",temp);
	row.set("tempvalu",tempVelue);
	row.set("hkstat",hkstat);
	JSONObject object = JSONObject.fromBean(row);	
	this.getWriter().write(object.toString());	
	return null ;  
}
//xiong20190614旧方法注释
//public ActionResult doGetYqLYLList() throws Exception {
//	
//	logger.info("待催收用户LYL");
//	JSONObject jsonObject = new JSONObject(); 
//	int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());
//	
//	if(cmsuserid==0){
//		jsonObject.put("error", -1);
//		jsonObject.put("msg", "Vui lòng đăng nhập trước");
//		this.getWriter().write(jsonObject.toString());	
//		return null;				 
//		
//	}
//	int temp = getIntParameter("temp",0);
//	String  tempVelue = getStrParameter("tempvl"); 		
//	
//	String  startDate1 =getStrParameter("startDate");
//	String  endDate1 =getStrParameter("endDate");
//	String startDate="";
//	String endDate="";
//	if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
//		String[] sourceStrArray1 = startDate1.split("-");
//		String[] sourceStrArray2 = endDate1.split("-");
//		startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
//		endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
//	}
//	String  hkstat = getStrParameter("hkstat");       
//	//审核状态
//	String commit = getStrParameter("commit");
//
//	//定义用户选择条件
//	String userId ="" ;
//	String realName="";
//	String phone = "" ;	
//	String idCard ="" ;
//	String cuishouid ="" ;
//	String jkdate ="" ;
//	String yuqts ="" ;
//	
//	//逾期天数
//	String st_day = getStrParameter("s_day");//逾期最小天数
//	
//	String et_day = getStrParameter("e_day");//逾期最大天数
//	
//	if(temp ==1){
//		
//		userId =tempVelue ;
//	}
//	
//	if(temp ==2){
//		
//		realName =tempVelue ;
//	}
//	if(temp ==3) {
//		
//		phone = tempVelue ;
//	}
//	if(temp ==4) {
//		
//		idCard = tempVelue ;
//	}	
//	if(temp ==5) {
//		
//		cuishouid = tempVelue ;
//	}	
//	if(temp ==6) {
//		
//		jkdate = "34" ;
//	}	
//	if(temp ==7) {
//		
//		yuqts = tempVelue;
//	}	
//	//三组的成员
//	List<DataRow> cuishoum1 = jbdcms2Service.getAllcuishouM3();
//	int cuishouzuyqm1[] = new int[cuishoum1.size()];
//	
//	for (int m = 0; m < cuishoum1.size(); m++) {
//		DataRow row = cuishoum1.get(m);
//		cuishouzuyqm1[m] = row.getInt("user_id");
//	}
//	//默认第一页
//	int curPage  =getIntParameter("curPage",1);	
//	DBPage page = jbdcms2Service.getYqLYLList(curPage,15,userId,realName,phone,startDate,endDate,commit,idCard,hkstat,st_day,et_day,cmsuserid,cuishouid,cuishouzuyqm1,jkdate,yuqts);
//	List<DataRow> list=jbdcms2Service.getYqLYLList(userId,realName,phone,startDate,endDate,commit,idCard,hkstat ,st_day,et_day,cmsuserid,cuishouid,cuishouzuyqm1,jkdate,yuqts);
//	int sjsh =0;
//	int yuq =0;
//	int hkjeje[] = new int[list.size()] ;
//	String hkje[] = new String[list.size()] ;
//	String sjshmoney = "";
//	String yuqmoney = "";
//	DecimalFormat format = new DecimalFormat("###,###");
//	for (int i=0;i<list.size();i++) {
//		DataRow dataRow = list.get(i);
//		sjshmoney = dataRow.getString("sjsh_money").replace(",", "");
//		yuqmoney = dataRow.getString("yuq_lx").replace(",", "");
//		sjsh = Integer.parseInt(sjshmoney);
//		yuq = Integer.parseInt(yuqmoney);
//		hkjeje[i] = sjsh +yuq;
//		hkje[i] = format.format(hkjeje[i]);
//		
//	} 
//	DataRow row = new DataRow();
//	row.set("list", page);
//	row.set("hkje", hkje);
//	row.set("temp",temp);
//	row.set("tempvalu",tempVelue);
//	row.set("hkstat",hkstat);
//	JSONObject object = JSONObject.fromBean(row);	
//	this.getWriter().write(object.toString());	
//	return null ;  
//}
/**
 * 催收周云
 * 	GetYqList
 */
//4组
//xiong20190614
public ActionResult doGetYqDWGList() throws Exception {
	
	logger.info("待催收用户DWG");
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
	 
	int temp = getIntParameter("temp",0);
	String  tempVelue = getStrParameter("tempvl"); 		
	
	String  startDate1 =getStrParameter("startDate");
	String  endDate1 =getStrParameter("endDate");
	String startDate="";
	String endDate="";
	if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
		String[] sourceStrArray1 = startDate1.split("-");
		String[] sourceStrArray2 = endDate1.split("-");
		startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
		endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
	}
	String  hkstat = getStrParameter("hkstat");       
	//审核状态
	String commit = getStrParameter("commit");
	int user_id = SessionHelper.getInt("cmsuserid", getSession());   //操作员ID
	/*if(user_id == 0){	   
	  	  jsonObject.put("err", -1);		  
		  this.getWriter().write(jsonObject.toString());	
		  return null;
	    }*/
	String csname = jbdcms2Service.getNameCms(user_id);
	//定义用户选择条件
	String userId ="" ;
	String realName="";
	String phone = "" ;	
	String idCard ="" ;
	String cuishouid ="" ;
	String jkdate ="" ;
	String yuqts ="" ;
	
	//逾期天数
	String st_day = getStrParameter("s_day");//逾期最小天数
	
	String et_day = getStrParameter("e_day");//逾期最大天数
	
	if(temp ==1){
		
		userId =tempVelue ;
	}
	
	if(temp ==2){
		
		realName =tempVelue ;
	}
	if(temp ==3) {
		
		phone = tempVelue ;
	}
	if(temp ==4) {
		
		idCard = tempVelue ;
	}	
	if(temp ==5) {
		
		cuishouid = tempVelue ;
	}	
	if(temp ==6) {
		
		jkdate = "34" ;
	}	
	if(temp ==7) {
		
		yuqts = tempVelue;
	}	
	//二组的成员
	List<DataRow> cuishoum1 = jbdcms2Service.getAllcuishouM4();
	int cuishouzuyqm1[] = new int[cuishoum1.size()];
	
	for (int m = 0; m < cuishoum1.size(); m++) {
		DataRow row = cuishoum1.get(m);
		cuishouzuyqm1[m] = row.getInt("user_id");
	}
	//默认第一页
	int curPage  =getIntParameter("curPage",1);	
	DBPage page = jbdcms2Service.getYqDWGList(curPage,15,userId,realName,phone,startDate,endDate,commit,idCard,hkstat,st_day,et_day,cmsuserid,cuishouid,cuishouzuyqm1,jkdate,yuqts);
	List<DataRow> list=jbdcms2Service.getYqDWGList(userId,realName,phone,startDate,endDate,commit,idCard,hkstat ,st_day,et_day,cmsuserid,cuishouid,cuishouzuyqm1,jkdate,yuqts);
	double hkje=0;
	DecimalFormat format = new DecimalFormat("###,###");
	for (int i=0;i<list.size();i++) {
		DataRow dataRow = list.get(i);
		String sjshmoney = dataRow.getString("sjsh_money").replace(",", "");
		String yuqmoney = dataRow.getString("yuq_lx").replace(",", "");
		int sjsh = Integer.parseInt(sjshmoney);
		int yuq = Integer.parseInt(yuqmoney);
		hkje += (sjsh+yuq);
	}
	DataRow row = new DataRow();
	row.set("list", page);
	row.set("hkje", format.format(hkje));
	row.set("temp",temp);
	row.set("tempvalu",tempVelue);
	row.set("hkstat",hkstat);
	JSONObject object = JSONObject.fromBean(row);	
	this.getWriter().write(object.toString());	
	return null ;  
}
//xiong2019014黑名单原来的方法
//public ActionResult doGetYqDWGList() throws Exception {
//	
//	logger.info("待催收用户DWG");
//	JSONObject jsonObject = new JSONObject(); 
//	int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());
//	
//	if(cmsuserid==0){
//		jsonObject.put("error", -1);
//		jsonObject.put("msg", "Vui lòng đăng nhập trước");
//		this.getWriter().write(jsonObject.toString());	
//		return null;				 
//		
//	}
//	int temp = getIntParameter("temp",0);
//	String  tempVelue = getStrParameter("tempvl"); 		
//	
//	String  startDate1 =getStrParameter("startDate");
//	String  endDate1 =getStrParameter("endDate");
//	String startDate="";
//	String endDate="";
//	if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
//		String[] sourceStrArray1 = startDate1.split("-");
//		String[] sourceStrArray2 = endDate1.split("-");
//		startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
//		endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
//	}
//	String  hkstat = getStrParameter("hkstat");       
//	//审核状态
//	String commit = getStrParameter("commit");
//	
//	//定义用户选择条件
//	String userId ="" ;
//	String realName="";
//	String phone = "" ;	
//	String idCard ="" ;
//	String cuishouid ="" ;
//	String jkdate ="" ;
//	String yuqts ="" ;
//	
//	//逾期天数
//	String st_day = getStrParameter("s_day");//逾期最小天数
//	
//	String et_day = getStrParameter("e_day");//逾期最大天数
//	
//	if(temp ==1){
//		
//		userId =tempVelue ;
//	}
//	
//	if(temp ==2){
//		
//		realName =tempVelue ;
//	}
//	if(temp ==3) {
//		
//		phone = tempVelue ;
//	}
//	if(temp ==4) {
//		
//		idCard = tempVelue ;
//	}	
//	if(temp ==5) {
//		
//		cuishouid = tempVelue ;
//	}	
//	if(temp ==6) {
//		
//		jkdate = "34" ;
//	}	
//	if(temp ==7) {
//		
//		yuqts = tempVelue;
//	}	
//	//二组的成员
//	List<DataRow> cuishoum1 = jbdcms2Service.getAllcuishouM4();
//	int cuishouzuyqm1[] = new int[cuishoum1.size()];
//	
//	for (int m = 0; m < cuishoum1.size(); m++) {
//		DataRow row = cuishoum1.get(m);
//		cuishouzuyqm1[m] = row.getInt("user_id");
//	}
//	//默认第一页
//	int curPage  =getIntParameter("curPage",1);	
//	DBPage page = jbdcms2Service.getYqDWGList(curPage,15,userId,realName,phone,startDate,endDate,commit,idCard,hkstat,st_day,et_day,cmsuserid,cuishouid,cuishouzuyqm1,jkdate,yuqts);
//	List<DataRow> list=jbdcms2Service.getYqDWGList(userId,realName,phone,startDate,endDate,commit,idCard,hkstat ,st_day,et_day,cmsuserid,cuishouid,cuishouzuyqm1,jkdate,yuqts);
//	int sjsh =0;
//	int yuq =0;
//	int hkjeje[] = new int[list.size()] ;
//	String hkje[] = new String[list.size()] ;
//	String sjshmoney = "";
//	String yuqmoney = "";
//	DecimalFormat format = new DecimalFormat("###,###");
//	for (int i=0;i<list.size();i++) {
//		DataRow dataRow = list.get(i);
//		sjshmoney = dataRow.getString("sjsh_money").replace(",", "");
//		yuqmoney = dataRow.getString("yuq_lx").replace(",", "");
//		sjsh = Integer.parseInt(sjshmoney);
//		yuq = Integer.parseInt(yuqmoney);
//		hkjeje[i] = sjsh +yuq;
//		hkje[i] = format.format(hkjeje[i]);
//		
//	} 
//	DataRow row = new DataRow();
//	row.set("list", page);
//	row.set("hkje", hkje);
//	row.set("temp",temp);
//	row.set("tempvalu",tempVelue);
//	row.set("hkstat",hkstat);
//	JSONObject object = JSONObject.fromBean(row);	
//	this.getWriter().write(object.toString());	
//	return null ;  
//}
/**
 * 催收周云
 * 	GetYqList
 */
//4组
//xiong20190614黑名单
public ActionResult doGetYqM3List() throws Exception {
	
	logger.info("待催收用户M3");
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
	 
	int temp = getIntParameter("temp",0);
	String  tempVelue = getStrParameter("tempvl"); 		
	
	String  startDate1 =getStrParameter("startDate");
	String  endDate1 =getStrParameter("endDate");
	String startDate="";
	String endDate="";
	if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
		String[] sourceStrArray1 = startDate1.split("-");
		String[] sourceStrArray2 = endDate1.split("-");
		startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
		endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
	}
	String  hkstat = getStrParameter("hkstat");       
	//审核状态
	String commit = getStrParameter("commit");
	int user_id = SessionHelper.getInt("cmsuserid", getSession());   //操作员ID
	/*if(user_id == 0){	   
	  	  jsonObject.put("err", -1);		  
		  this.getWriter().write(jsonObject.toString());	
		  return null;
	    }*/
	String csname = jbdcms2Service.getNameCms(user_id);
	//定义用户选择条件
	String userId ="" ;
	String realName="";
	String phone = "" ;	
	String idCard ="" ;
	String cuishouid ="" ;
	String jkdate ="" ;
	String yuqts ="" ;
	
	//逾期天数
	String st_day = getStrParameter("s_day");//逾期最小天数
	
	String et_day = getStrParameter("e_day");//逾期最大天数
	
	if(temp ==1){
		
		userId =tempVelue ;
	}
	
	if(temp ==2){
		
		realName =tempVelue ;
	}
	if(temp ==3) {
		
		phone = tempVelue ;
	}
	if(temp ==4) {
		
		idCard = tempVelue ;
	}	
	if(temp ==5) {
		
		cuishouid = tempVelue ;
	}	
	if(temp ==6) {
		
		jkdate = "34" ;
	}	
	if(temp ==7) {
		
		yuqts = tempVelue ;
	}	
	//二组的成员
	List<DataRow> cuishoum1 = jbdcms2Service.getAllcuishouM6();
	int cuishouzuyqm1[] = new int[cuishoum1.size()];
	
	for (int m = 0; m < cuishoum1.size(); m++) {
		DataRow row = cuishoum1.get(m);
		cuishouzuyqm1[m] = row.getInt("user_id");
	}
	//默认第一页
	int curPage  =getIntParameter("curPage",1);	
	DBPage page = jbdcms2Service.getYqM3List(curPage,15,userId,realName,phone,startDate,endDate,commit,idCard,hkstat,st_day,et_day,cmsuserid,cuishouid,cuishouzuyqm1,jkdate,yuqts);
	List<DataRow> list=jbdcms2Service.getYqM3List(userId,realName,phone,startDate,endDate,commit,idCard,hkstat ,st_day,et_day,cmsuserid,cuishouid,cuishouzuyqm1,jkdate,yuqts);
	double hkje=0;
	DecimalFormat format = new DecimalFormat("###,###");
	for (int i=0;i<list.size();i++) {
		DataRow dataRow = list.get(i);
		String sjshmoney = dataRow.getString("sjsh_money").replace(",", "");
		String yuqmoney = dataRow.getString("yuq_lx").replace(",", "");
		int sjsh = Integer.parseInt(sjshmoney);
		int yuq = Integer.parseInt(yuqmoney);
		hkje += (sjsh+yuq);
	}
	DataRow row = new DataRow();
	row.set("list", page);
	row.set("hkje", format.format(hkje));
	row.set("temp",temp);
	row.set("tempvalu",tempVelue);
	row.set("hkstat",hkstat);
	JSONObject object = JSONObject.fromBean(row);	
	this.getWriter().write(object.toString());	
	return null ;  
}
//xiong20190614黑名单4组
//public ActionResult doGetYqM3List() throws Exception {
//	
//	logger.info("待催收用户M3");
//	JSONObject jsonObject = new JSONObject(); 
//	int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());
//	
//	if(cmsuserid==0){
//		jsonObject.put("error", -1);
//		jsonObject.put("msg", "Vui lòng đăng nhập trước");
//		this.getWriter().write(jsonObject.toString());	
//		return null;				 
//		
//	}
//	int temp = getIntParameter("temp",0);
//	String  tempVelue = getStrParameter("tempvl"); 		
//	
//	String  startDate1 =getStrParameter("startDate");
//	String  endDate1 =getStrParameter("endDate");
//	String startDate="";
//	String endDate="";
//	if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
//		String[] sourceStrArray1 = startDate1.split("-");
//		String[] sourceStrArray2 = endDate1.split("-");
//		startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
//		endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
//	}
//	String  hkstat = getStrParameter("hkstat");       
//	//审核状态
//	String commit = getStrParameter("commit");
//	
//	//定义用户选择条件
//	String userId ="" ;
//	String realName="";
//	String phone = "" ;	
//	String idCard ="" ;
//	String cuishouid ="" ;
//	String jkdate ="" ;
//	String yuqts ="" ;
//	
//	//逾期天数
//	String st_day = getStrParameter("s_day");//逾期最小天数
//	
//	String et_day = getStrParameter("e_day");//逾期最大天数
//	
//	if(temp ==1){
//		
//		userId =tempVelue ;
//	}
//	
//	if(temp ==2){
//		
//		realName =tempVelue ;
//	}
//	if(temp ==3) {
//		
//		phone = tempVelue ;
//	}
//	if(temp ==4) {
//		
//		idCard = tempVelue ;
//	}	
//	if(temp ==5) {
//		
//		cuishouid = tempVelue ;
//	}	
//	if(temp ==6) {
//		
//		jkdate = "34" ;
//	}	
//	if(temp ==7) {
//		
//		yuqts = tempVelue ;
//	}	
//	//二组的成员
//	List<DataRow> cuishoum1 = jbdcms2Service.getAllcuishouM6();
//	int cuishouzuyqm1[] = new int[cuishoum1.size()];
//	
//	for (int m = 0; m < cuishoum1.size(); m++) {
//		DataRow row = cuishoum1.get(m);
//		cuishouzuyqm1[m] = row.getInt("user_id");
//	}
//	//默认第一页
//	int curPage  =getIntParameter("curPage",1);	
//	DBPage page = jbdcms2Service.getYqM3List(curPage,15,userId,realName,phone,startDate,endDate,commit,idCard,hkstat,st_day,et_day,cmsuserid,cuishouid,cuishouzuyqm1,jkdate,yuqts);
//	List<DataRow> list=jbdcms2Service.getYqM3List(userId,realName,phone,startDate,endDate,commit,idCard,hkstat ,st_day,et_day,cmsuserid,cuishouid,cuishouzuyqm1,jkdate,yuqts);
//	int sjsh =0;
//	int yuq =0;
//	int hkjeje[] = new int[list.size()] ;
//	String hkje[] = new String[list.size()] ;
//	String sjshmoney = "";
//	String yuqmoney = "";
//	DecimalFormat format = new DecimalFormat("###,###");
//	for (int i=0;i<list.size();i++) {
//		DataRow dataRow = list.get(i);
//		sjshmoney = dataRow.getString("sjsh_money").replace(",", "");
//		yuqmoney = dataRow.getString("yuq_lx").replace(",", "");
//		sjsh = Integer.parseInt(sjshmoney);
//		yuq = Integer.parseInt(yuqmoney);
//		hkjeje[i] = sjsh +yuq;
//		hkje[i] = format.format(hkjeje[i]);
//		
//	} 
//	DataRow row = new DataRow();
//	row.set("list", page);
//	row.set("hkje", hkje);
//	row.set("temp",temp);
//	row.set("tempvalu",tempVelue);
//	row.set("hkstat",hkstat);
//	JSONObject object = JSONObject.fromBean(row);	
//	this.getWriter().write(object.toString());	
//	return null ;  
//}

/**
 * 催收周云
 * 	GetYqList
 */
//xiong20190614新的黑名单
public ActionResult doGetYqM2List() throws Exception {
	
	logger.info("待催收用户M2");
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
	 
	int temp = getIntParameter("temp",0);
	String  tempVelue = getStrParameter("tempvl"); 		
	
	String  startDate1 =getStrParameter("startDate");
	String  endDate1 =getStrParameter("endDate");
	String startDate="";
	String endDate="";
	if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
		String[] sourceStrArray1 = startDate1.split("-");
		String[] sourceStrArray2 = endDate1.split("-");
		startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
		endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
	}
	String  hkstat = getStrParameter("hkstat");       
	//审核状态
	String commit = getStrParameter("commit");
	int user_id = SessionHelper.getInt("cmsuserid", getSession());   //操作员ID
	/*if(user_id == 0){	   
	  	  jsonObject.put("err", -1);		  
		  this.getWriter().write(jsonObject.toString());	
		  return null;
	    }*/
	String csname = jbdcms2Service.getNameCms(user_id);
	//定义用户选择条件
	String userId ="" ;
	String realName="";
	String phone = "" ;	
	String idCard ="" ;
	String cuishouid ="" ;
	String jkdate ="" ;
	String yuqts ="" ;
	
	//逾期天数
	String st_day = getStrParameter("s_day");//逾期最小天数
	
	String et_day = getStrParameter("e_day");//逾期最大天数
	
	if(temp ==1){
		
		userId =tempVelue ;
	}
	
	if(temp ==2){
		
		realName =tempVelue ;
	}
	if(temp ==3) {
		
		phone = tempVelue ;
	}
	if(temp ==4) {
		
		idCard = tempVelue ;
	}	
	if(temp ==5) {
		
		cuishouid = tempVelue ;
	}	
	if(temp ==6) {
		
		jkdate = "34" ;
	}	
	if(temp ==7) {
		
		yuqts = tempVelue ;
	}	
	//二组的成员
	List<DataRow> cuishoum1 = jbdcms2Service.getAllcuishouM5();
	int cuishouzuyqm1[] = new int[cuishoum1.size()];
	
	for (int m = 0; m < cuishoum1.size(); m++) {
		DataRow row = cuishoum1.get(m);
		cuishouzuyqm1[m] = row.getInt("user_id");
	}
	
	//默认第一页
	int curPage  =getIntParameter("curPage",1);	
	DBPage page = jbdcms2Service.getYqM2List(curPage,15,userId,realName,phone,startDate,endDate,commit,idCard,hkstat,st_day,et_day,cmsuserid,cuishouid,cuishouzuyqm1,jkdate,yuqts);
	List<DataRow> list=jbdcms2Service.getYqM2List(userId,realName,phone,startDate,endDate,commit,idCard,hkstat ,st_day,et_day,cmsuserid,cuishouid,cuishouzuyqm1,jkdate,yuqts);
	double hkje=0;
	DecimalFormat format = new DecimalFormat("###,###");
	for (int i=0;i<list.size();i++) {
		DataRow dataRow = list.get(i);
		String sjshmoney = dataRow.getString("sjsh_money").replace(",", "");
		String yuqmoney = dataRow.getString("yuq_lx").replace(",", "");
		int sjsh = Integer.parseInt(sjshmoney);
		int yuq = Integer.parseInt(yuqmoney);
		hkje += (sjsh+yuq);
	}
	DataRow row = new DataRow();
	row.set("list", page);
	row.set("hkje", format.format(hkje));
	row.set("temp",temp);
	row.set("tempvalu",tempVelue);
	row.set("hkstat",hkstat);
	JSONObject object = JSONObject.fromBean(row);	
	this.getWriter().write(object.toString());	
	return null ;  
}
//xiong20190614原来的黑名单注释
//public ActionResult doGetYqM2List() throws Exception {
//	
//	logger.info("待催收用户M2");
//	JSONObject jsonObject = new JSONObject(); 
//	int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());
//	
//	if(cmsuserid==0){
//		jsonObject.put("error", -1);
//		jsonObject.put("msg", "Vui lòng đăng nhập trước");
//		this.getWriter().write(jsonObject.toString());	
//		return null;				 
//		
//	}
//	int temp = getIntParameter("temp",0);
//	String  tempVelue = getStrParameter("tempvl"); 		
//	
//	String  startDate1 =getStrParameter("startDate");
//	String  endDate1 =getStrParameter("endDate");
//	String startDate="";
//	String endDate="";
//	if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
//		String[] sourceStrArray1 = startDate1.split("-");
//		String[] sourceStrArray2 = endDate1.split("-");
//		startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
//		endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
//	}
//	String  hkstat = getStrParameter("hkstat");       
//	//审核状态
//	String commit = getStrParameter("commit");
//	int user_id = SessionHelper.getInt("cmsuserid", getSession());   //操作员ID
//	/*if(user_id == 0){	   
//	  	  jsonObject.put("err", -1);		  
//		  this.getWriter().write(jsonObject.toString());	
//		  return null;
//	    }*/
//	String csname = jbdcms2Service.getNameCms(user_id);
//	//定义用户选择条件
//	String userId ="" ;
//	String realName="";
//	String phone = "" ;	
//	String idCard ="" ;
//	String cuishouid ="" ;
//	String jkdate ="" ;
//	String yuqts ="" ;
//	
//	//逾期天数
//	String st_day = getStrParameter("s_day");//逾期最小天数
//	
//	String et_day = getStrParameter("e_day");//逾期最大天数
//	
//	if(temp ==1){
//		
//		userId =tempVelue ;
//	}
//	
//	if(temp ==2){
//		
//		realName =tempVelue ;
//	}
//	if(temp ==3) {
//		
//		phone = tempVelue ;
//	}
//	if(temp ==4) {
//		
//		idCard = tempVelue ;
//	}	
//	if(temp ==5) {
//		
//		cuishouid = tempVelue ;
//	}	
//	if(temp ==6) {
//		
//		jkdate = "34" ;
//	}	
//	if(temp ==7) {
//		
//		yuqts = tempVelue ;
//	}	
//	//二组的成员
//	List<DataRow> cuishoum1 = jbdcms2Service.getAllcuishouM5();
//	int cuishouzuyqm1[] = new int[cuishoum1.size()];
//	
//	for (int m = 0; m < cuishoum1.size(); m++) {
//		DataRow row = cuishoum1.get(m);
//		cuishouzuyqm1[m] = row.getInt("user_id");
//	}
//	
//	//默认第一页
//	int curPage  =getIntParameter("curPage",1);	
//	DBPage page = jbdcms2Service.getYqM2List(curPage,15,userId,realName,phone,startDate,endDate,commit,idCard,hkstat,st_day,et_day,cmsuserid,cuishouid,cuishouzuyqm1,jkdate,yuqts);
//	List<DataRow> list=jbdcms2Service.getYqM2List(userId,realName,phone,startDate,endDate,commit,idCard,hkstat ,st_day,et_day,cmsuserid,cuishouid,cuishouzuyqm1,jkdate,yuqts);
//	int sjsh =0;
//	int yuq =0;
//	int hkjeje[] = new int[list.size()] ;
//	String hkje[] = new String[list.size()] ;
//	String sjshmoney = "";
//	String yuqmoney = "";
//	DecimalFormat format = new DecimalFormat("###,###");
//	for (int i=0;i<list.size();i++) {
//		DataRow dataRow = list.get(i);
//		sjshmoney = dataRow.getString("sjsh_money").replace(",", "");
//		yuqmoney = dataRow.getString("yuq_lx").replace(",", "");
//		sjsh = Integer.parseInt(sjshmoney);
//		yuq = Integer.parseInt(yuqmoney);
//		hkjeje[i] = sjsh +yuq;
//		hkje[i] = format.format(hkjeje[i]);
//	} 
//	DataRow row = new DataRow();
//	row.set("list", page);
//	row.set("hkje", hkje);
//	row.set("temp",temp);
//	row.set("tempvalu",tempVelue);
//	row.set("hkstat",hkstat);
//	JSONObject object = JSONObject.fromBean(row);	
//	this.getWriter().write(object.toString());	
//	return null ;  
//}



public  ActionResult doGetCuisByid()  throws Exception {
	   logger.info("进入催收备注");
	   int  jkid =getIntParameter("record_",0);
	   JSONObject jsonObject = new JSONObject();  
	   if(jkid ==0){
				   jsonObject.put("error", -1);
			  	   jsonObject.put("msg", "催收项目不存在");
				  this.getWriter().write(jsonObject.toString());	
				  return null;				
	   }
	   //通过催收ID 查询该项目的催收记录
	   List<DataRow> cslist= jbdcms2Service.getCsList(jkid);
	   jsonObject.put("error", 1);
  	   jsonObject.put("msg", "展示成功");
	   jsonObject.put("cslist",cslist);
	   this.getWriter().write(jsonObject.toString());	
	   return null ;
   }
   public  ActionResult doGetShenHByid()  throws Exception {
	   logger.info("进入审核备注");
	   int  jkid =getIntParameter("record_",0);
	   int  userid =getIntParameter("user_id",0);
	   JSONObject jsonObject = new JSONObject();  
	   
	   //通过催收ID 查询该项目的催收记录
	   List<DataRow> cslist= jbdcms2Service.getShList(userid);
	   jsonObject.put("error", 1);
	   jsonObject.put("msg", "展示成功");
	   jsonObject.put("cslist",cslist);
	   this.getWriter().write(jsonObject.toString());	
	   return null ;
   }
   public  ActionResult doUpdateWJDHByid()  throws Exception {
	   logger.info("进入打回初审未接电话");
	   int  jkid =getIntParameter("re_id",0);
	   JSONObject jsonObject = new JSONObject();  
	   Date date=new Date();
	 	DateFormat format=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	 	String time=format.format(date);
	   if(jkid ==0){
		   jsonObject.put("error", -1);
		   jsonObject.put("msg", "审核项目不存在");
		   this.getWriter().write(jsonObject.toString());	
		   return null;				
	   }
	   DataRow row = new DataRow();
	   row.set("id", jkid);
	   row.set("fsdxtime", time);
	   row.set("fsdxcode", 0);
	   jbdcms2Service.updateUserWJDH(row);
	   jsonObject.put("error", 1);
	   jsonObject.put("msg", "成功");
	   this.getWriter().write(jsonObject.toString());	
	   return null ;
   }
   public  ActionResult doGetWJDHByid()  throws Exception {
	   logger.info("进入审核未接电话备注");
	   int  jkid =getIntParameter("record_",0);
	   int  userid =getIntParameter("user_id",0);
	   JSONObject jsonObject = new JSONObject();  
	   if(jkid ==0){
		   jsonObject.put("error", -1);
		   jsonObject.put("msg", "审核项目不存在");
		   this.getWriter().write(jsonObject.toString());	
		   return null;				
	   }
	   //通过催收ID 查询该项目的催收记录
	   List<DataRow> cslist= jbdcms2Service.getWJDHList(userid);
	   jsonObject.put("error", 1);
	   jsonObject.put("msg", "展示成功");
	   jsonObject.put("cslist",cslist);
	   this.getWriter().write(jsonObject.toString());	
	   return null ;
   }
   /**
    * 发送营销短信
    */
   public ActionResult  doGetSendYX() throws Exception {
 	  
 	  logger.info("进入发送营销短信");
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
 	 
 	  int userid2 = SessionHelper.getInt("cmsuserid", getSession());
 	  int userid = getIntParameter("userid",0);//用户id
 	  if(userid ==0){
 		  jsonObject.put("error", -1);
 		  jsonObject.put("msg", "Không có khách hàng này");
 		  this.getWriter().write(jsonObject.toString());	
 		  return null;				
 	  }
 	  int msgcount  =jbdcms2Service.getmsgCountYX(userid);
 	  if(msgcount >0)  {
 		  jsonObject.put("error", -4);
 		  jsonObject.put("msg", "Đã gửi khách hàng này hôm nay, vui lòng đừng gửi lại tin nhắn truy nợ!");
 		  this.getWriter().write(jsonObject.toString());	
 		  return null;						
 	  }
 	  
 	  //通过项目id 查询出用户的信息  
 	  DecimalFormat famt = new DecimalFormat("###,###");
 	  DataRow userInfo  =jbdcms2Service.getUserYqInfoYX(userid) ;
 	  String appName ="Mofa";
 	  String userName = userInfo.getString("username");//用户名
 	  String mobilePhone =userInfo.getString("mobilephone");
 	  userName = userName.substring(0, 7);
 	  if(userName.equals("ABCDong")){
 		  appName="ABCDong";					    	
 	  }
// 	  String content   ="Tai ngay "+appName+" de co co hoi vay tien nhanh len den 10.000.000vnd. Dang ky tai: http://bit.ly/2KIzoEe. Hotline: 1900234558";
//	  SendFTP sms = new SendFTP();
//      String  response = sms.sendMessageFTP(content,mobilePhone);
 	  Date date=new Date();
 	  DateFormat format=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
 	  String time=format.format(date);
 	  //催收记录添加到app 消息
 	  DataRow row6=  new DataRow();	
 	  row6.set("userid",userid);
 	  row6.set("title", "Trả nợ nhắc nhở") ;
 	  row6.set("neirong" ,"Tai ngay "+appName+" de co co hoi vay tien nhanh len den 10.000.000vnd. Dang ky tai: http://bit.ly/2KIzoEe. Hotline: 1900234558");
 	  row6.set("fb_time", new Date());
 	  jbdcms2Service.insertUserMsg(row6);
 	  //添加催收记录（便于后台展示）
 	  DataRow row7=  new DataRow();	
 	  row7.set("userid",userid);	
 	  row7.set("msg" ,"Tai ngay "+appName+" de co co hoi vay tien nhanh len den 10.000.000vnd. Dang ky tai: http://bit.ly/2KIzoEe. Hotline: 1900234558");
 	  row7.set("msgtype","营销短信");
 	  row7.set("create_time", new Date());
 	  row7.set("cl_ren",userid2);
 	  jbdcms2Service.insertUserChMsg(row7);
 	  DataRow row8=  new DataRow();	
 	  row8.set("user_id",userid );	
 	  row8.set("content" ,"Tai ngay "+appName+" de co co hoi vay tien nhanh len den 10.000.000vnd. Dang ky tai: http://bit.ly/2KIzoEe. Hotline: 1900234558");
 	  row8.set("kefuid",userid2);
 	  row8.set("state",1);
 	  row8.set("code",12);
 	  row8.set("visitdate", time);
 	  jbdcms2Service.insertUserKFMsg(row8);
 	  return null ;
   }
   public  ActionResult doGetFangWByidYX()  throws Exception {
	   logger.info("进入营销备注");
	   int  userid =getIntParameter("user_id",0);
	   JSONObject jsonObject = new JSONObject();
	   //通过催收ID 查询该项目的催收记录
	   List<DataRow> cslist= jbdcms2Service.getFwListYX(userid);
	   jsonObject.put("error", 1);
	   jsonObject.put("msg", "展示成功");
	   jsonObject.put("cslist",cslist);
	   this.getWriter().write(jsonObject.toString());	
	   return null ;
   }
   public  ActionResult doAddfangwremarkYX() throws Exception {
	   logger.info("进入添加营销备注");
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
		 
	   int u_id=getIntParameter("user_id",0);
	   String remark = getStrParameter("remark");
	   if(remark != "" || remark != null){
		   remark = remark.replace("&nbsp;", " ");
	   }
	   //JSONObject jsonObject = new JSONObject(); 
	   int userid2 = SessionHelper.getInt("cmsuserid", getSession());
	   if(userid2==0){
		   jsonObject.put("error", -1);
		   jsonObject.put("msg", "Vui lòng đăng nhập trước");
		   this.getWriter().write(jsonObject.toString());	
		   return null;	
	   }
	   logger.info("请求ID:"+userid2);
	   if(u_id ==0){
		   jsonObject.put("error", -1);
		   jsonObject.put("msg", "用户不存在");
		   this.getWriter().write(jsonObject.toString());	
		   return null;				
	   }	 
	   Date date=new Date();
	   DateFormat format=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	   String time=format.format(date);
	   DataRow row8=  new DataRow();	
	   row8.set("user_id",u_id);	
	   row8.set("content" ,remark);
	   row8.set("jkjl_id",0);
	   row8.set("kefuid",userid2);
	   row8.set("state",1);
	   row8.set("code",12);
	   row8.set("visitdate", time);
	   jbdcms2Service.insertUserKFMsg(row8);
	   DataRow row9=  new DataRow();	
	   row9.set("id",u_id);	
	   row9.set("kfcontent" ,remark);
	   row9.set("kfbeizhutime", time);
	   jbdcms2Service.updateUserInfo(row9);
	   jsonObject.put("error", 1);
	   jsonObject.put("msg", "添加备注成功");
	   this.getWriter().write(jsonObject.toString());	
	   return null ;
   }
   public  ActionResult doGetFangWByid()  throws Exception {
	   logger.info("进入访问备注");
	   int  userid =getIntParameter("user_id",0);
	   JSONObject jsonObject = new JSONObject();
	   //通过催收ID 查询该项目的催收记录
	   List<DataRow> cslist= jbdcms2Service.getFwList(userid);
	   jsonObject.put("error", 1);
	   jsonObject.put("msg", "展示成功");
	   jsonObject.put("cslist",cslist);
	   this.getWriter().write(jsonObject.toString());	
	   return null ;
   }
   public  ActionResult doGetTXByid()  throws Exception {
	   logger.info("进入催收备注");
	   int  jkid =getIntParameter("record_",0);
	   JSONObject jsonObject = new JSONObject();  
	   if(jkid ==0){
		   jsonObject.put("error", -1);
		   jsonObject.put("msg", "项目不存在");
		   this.getWriter().write(jsonObject.toString());	
		   return null;				
	   }
	   //通过催收ID 查询该项目的催收记录
	   List<DataRow> cslist= jbdcms2Service.getTXList(jkid);
	   jsonObject.put("error", 1);
	   jsonObject.put("msg", "展示成功");
	   jsonObject.put("cslist",cslist);
	   this.getWriter().write(jsonObject.toString());	
	   return null ;
   }
   public  ActionResult doChangeHKFS1() throws Exception {
	   int rec_id = getIntParameter("re_id",0);//借款ID
	   JSONObject jsonObject = new JSONObject(); 
	   DataRow row = new DataRow();
	   row.set("id", rec_id);
	   row.set("hkstate", 0);
	   jbdcms2Service.updateChangeHKFS(row);
	   jsonObject.put("error","1");
	   jsonObject.put("msg","Right");
	   this.getWriter().write(jsonObject.toString());
	   return null;
   }
   public  ActionResult doChangeHKFS2() throws Exception {
	   int rec_id = getIntParameter("re_id",0);//借款ID
	   JSONObject jsonObject = new JSONObject(); 
	   DataRow row = new DataRow();
	   row.set("id", rec_id);
	   row.set("hkstate", 1);
	   jbdcms2Service.updateChangeHKFS(row);
	   jsonObject.put("error","1");
	   jsonObject.put("msg","Right");
	   this.getWriter().write(jsonObject.toString());
	   return null;
   }
   public  ActionResult doChangeHKFS3() throws Exception {
	   int rec_id = getIntParameter("re_id",0);//借款ID
	   JSONObject jsonObject = new JSONObject(); 
	   DataRow row = new DataRow();
	   row.set("id", rec_id);
	   row.set("hkstate", 2);
	   jbdcms2Service.updateChangeHKFS(row);
	   jsonObject.put("error","1");
	   jsonObject.put("msg","Right");
	   this.getWriter().write(jsonObject.toString());
	   return null;
   }
   
   public  ActionResult doAddcuisremark() throws Exception {
	  logger.info("进入添加催收备注");	
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
		 
	  int rec_id = getIntParameter("re_id",0);//借款ID
	  int u_id=getIntParameter("user_id",0);
	  String remark = getStrParameter("remark");
	  if(remark != "" || remark != null){
		  remark = remark.replace("&nbsp;", " ");
	  }
	  logger.info(remark);
	  //JSONObject jsonObject = new JSONObject(); 
	  int userid2 = SessionHelper.getInt("cmsuserid", getSession());
	  
	  if(userid2==0){
		  jsonObject.put("error", -1);
	  	  jsonObject.put("msg", "Vui lòng đăng nhập trước");
		  this.getWriter().write(jsonObject.toString());	
		  return null;				 
		  
	  }
	  logger.info("请求ID:"+userid2);
	  if(u_id ==0){
		   jsonObject.put("error", -1);
	  	   jsonObject.put("msg", "用户不存在");
		  this.getWriter().write(jsonObject.toString());	
		  return null;				
	 }
     if(rec_id ==0){
	     jsonObject.put("error", -2);
 	     jsonObject.put("msg", "逾期不存在");
	  this.getWriter().write(jsonObject.toString());	
	  return null;				
      }	 
 	Date date=new Date();
 	DateFormat format=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
 	String time=format.format(date);
     //添加催收备注
     DataRow row7=  new DataRow();	
	  row7.set("userid",u_id);	
	  row7.set("msg" ,remark);
	  row7.set("msgtype","备注");
	  row7.set("jkid",rec_id);//项目id
	  row7.set("create_time", new Date());
	  row7.set("cl_ren",userid2);
	  jbdcms2Service.insertUserChMsg(row7);
	  DataRow row8=  new DataRow();	
	  row8.set("user_id",u_id);	
	  row8.set("content" ,remark);
	  row8.set("jkjl_id",rec_id );
	  row8.set("kefuid",userid2);
	  row8.set("state",1);
	  row8.set("code",6);
	  row8.set("visitdate", time);
	  jbdcms2Service.insertUserKFMsg(row8);
	  DataRow row88=  new DataRow();
	  row88.set("id",rec_id);	
	  row88.set("zxcsbz" ,remark);
	  jbdcms2Service.updateUserWJDH(row88);
	  jsonObject.put("error", 1);
	  jsonObject.put("msg", "添加备注成功");
	  this.getWriter().write(jsonObject.toString());	
      return null ;
   }
   public  ActionResult doAddcuishoubaogao() throws Exception {
	   logger.info("进入添加催收报告");	
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
		 
	   int rec_id = getIntParameter("re_id",0);//借款ID
	   
	   int u_id = jbdcms2Service.getUserId(rec_id);
	   String ztcode=getStrParameter("ztselect");
	   
	   String remark = getStrParameter("remark");
	   if(ztcode != "" || ztcode != null){
		   ztcode = ztcode.replace("&nbsp;", " ");
	   }
	   if(remark != "" || remark != null){
		   remark = remark.replace("&nbsp;", " ");
	   }
	   logger.info(remark);
	  // JSONObject jsonObject = new JSONObject(); 
	   int userid2 = SessionHelper.getInt("cmsuserid", getSession());
	   
	   if(userid2==0){
		   jsonObject.put("error", -1);
		   jsonObject.put("msg", "Vui lòng đăng nhập trước");
		   this.getWriter().write(jsonObject.toString());	
		   return null;				 
		   
	   }
	   logger.info("请求ID:"+userid2);
	   if(u_id ==0){
		   jsonObject.put("error", -1);
		   jsonObject.put("msg", "用户不存在");
		   this.getWriter().write(jsonObject.toString());	
		   return null;				
	   }
	   if(rec_id ==0){
		   jsonObject.put("error", -2);
		   jsonObject.put("msg", "逾期不存在");
		   this.getWriter().write(jsonObject.toString());	
		   return null;				
	   }	 
	   Date date=new Date();
	   DateFormat format=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	   String time=format.format(date);
	   //添加催收备注
	   DataRow row7=  new DataRow();	
	   row7.set("userid",u_id);	
	   row7.set("msg" ,remark);
	   row7.set("msgtype","催收报告");
	   row7.set("jkid",rec_id);//项目id
	   row7.set("create_time", new Date());
	   row7.set("cl_ren",userid2);
	   row7.set("cszt",ztcode);
	   jbdcms2Service.insertUserChMsg(row7);
	   DataRow row8=  new DataRow();	
	   row8.set("user_id",u_id);	
	   row8.set("content" ,remark);
	   row8.set("jkjl_id",rec_id );
	   row8.set("kefuid",userid2);
	   row8.set("state",1);
	   row8.set("code",11);
	   row8.set("visitdate", time);
	   jbdcms2Service.insertUserKFMsg(row8);
	   
	   jsonObject.put("error", 1);
	   jsonObject.put("cszt", ztcode);
	   jsonObject.put("remark", remark);
	   jsonObject.put("msg", "添加催收报告成功");
	   this.getWriter().write(jsonObject.toString());	
	   return null ;
   }
   public  ActionResult doAddshenhremark() throws Exception {
	   logger.info("进入添加审核备注");	
	   JSONObject jsonObject = new JSONObject(); 
		int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
		cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		 
	   int rec_id = getIntParameter("re_id",0);//借款ID
	   int u_id=getIntParameter("user_id",0);
	   String remark = getStrParameter("remark");
	   if(remark != "" || remark != null){
		   remark = remark.replace("&nbsp;", " ");
	   }
	   logger.info(remark);
	  // JSONObject jsonObject = new JSONObject(); 
	   int userid2 = SessionHelper.getInt("cmsuserid", getSession());
	   
	   if(userid2==0){
		   jsonObject.put("error", -1);
		   jsonObject.put("msg", "Vui lòng đăng nhập trước");
		   this.getWriter().write(jsonObject.toString());	
		   return null;				 
		   
	   }
	   logger.info("请求ID:"+userid2);
	   if(u_id ==0){
		   jsonObject.put("error", -1);
		   jsonObject.put("msg", "用户不存在");
		   this.getWriter().write(jsonObject.toString());	
		   return null;				
	   }
	   if(rec_id ==0){
		   jsonObject.put("error", -2);
		   jsonObject.put("msg", "审核不存在");
		   this.getWriter().write(jsonObject.toString());	
		   return null;				
	   }	 
	   Date date=new Date();
	   DateFormat format=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	   String time=format.format(date);
	   DataRow row8=  new DataRow();	
	   row8.set("user_id",u_id);	
	   row8.set("content" ,remark);
	   row8.set("jkjl_id",rec_id );
	   row8.set("kefuid",userid2);
	   row8.set("state",1);
	   row8.set("code",8);
	   row8.set("visitdate", time);
	   jbdcms2Service.insertUserKFMsg(row8);
	   
	   jsonObject.put("error", 1);
	   jsonObject.put("msg", "添加备注成功");
	   this.getWriter().write(jsonObject.toString());	
	   return null ;
   }
   public  ActionResult doAddWJDHremark() throws Exception {
	   logger.info("进入添加审核未接电话备注");	
	   JSONObject jsonObject = new JSONObject(); 
		int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
		cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		 }
		
		 
	   int rec_id = getIntParameter("re_id",0);//借款ID
	   int u_id=getIntParameter("user_id",0);
	   String remark = getStrParameter("remark");
	   if(remark != "" || remark != null){
		   remark = remark.replace("&nbsp;", " ");
	   }
	   logger.info(remark);
	   //JSONObject jsonObject = new JSONObject(); 
	   int userid2 = SessionHelper.getInt("cmsuserid", getSession());
	   
	   if(userid2==0){
		   jsonObject.put("error", -1);
		   jsonObject.put("msg", "Vui lòng đăng nhập trước");
		   this.getWriter().write(jsonObject.toString());	
		   return null;				 
		   
	   }
	   logger.info("请求ID:"+userid2);
	   if(u_id ==0){
		   jsonObject.put("error", -1);
		   jsonObject.put("msg", "用户不存在");
		   this.getWriter().write(jsonObject.toString());	
		   return null;				
	   }
	   if(rec_id ==0){
		   jsonObject.put("error", -2);
		   jsonObject.put("msg", "审核不存在");
		   this.getWriter().write(jsonObject.toString());	
		   return null;				
	   }	 
	   Date date=new Date();
	   DateFormat format=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	   String time=format.format(date);
	   DataRow row8=  new DataRow();	
	   row8.set("user_id",u_id);	
	   row8.set("content" ,remark);
	   row8.set("jkjl_id",rec_id );
	   row8.set("kefuid",userid2);
	   row8.set("state",1);
	   row8.set("code",10);
	   row8.set("visitdate", time);
	   jbdcms2Service.insertUserKFMsg(row8);
	   DataRow row = new DataRow();
	   row.set("id", rec_id);
	   row.set("fsdxtime", time);
	   jbdcms2Service.updateUserWJDH(row);
	   jsonObject.put("error", 1);
	   jsonObject.put("msg", "添加备注成功");
	   this.getWriter().write(jsonObject.toString());	
	   return null ;
   }
   public  ActionResult doAddfangwremark() throws Exception {
	   logger.info("进入添加访问备注");
	   JSONObject jsonObject = new JSONObject(); 
		int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
		cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		 
	   int u_id=getIntParameter("user_id",0);
	   String remark = getStrParameter("remark");
	   if(remark != "" || remark != null){
		   remark = remark.replace("&nbsp;", " ");
	   }
	   //JSONObject jsonObject = new JSONObject(); 
	   int userid2 = SessionHelper.getInt("cmsuserid", getSession());
	   if(userid2==0){
		   jsonObject.put("error", -1);
		   jsonObject.put("msg", "Vui lòng đăng nhập trước");
		   this.getWriter().write(jsonObject.toString());	
		   return null;	
	   }
	   logger.info("请求ID:"+userid2);
	   if(u_id ==0){
		   jsonObject.put("error", -1);
		   jsonObject.put("msg", "用户不存在");
		   this.getWriter().write(jsonObject.toString());	
		   return null;				
	   }	 
	   Date date=new Date();
	   DateFormat format=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	   String time=format.format(date);
	   DataRow row8=  new DataRow();	
	   row8.set("user_id",u_id);	
	   row8.set("content" ,remark);
	   row8.set("jkjl_id",0);
	   row8.set("kefuid",userid2);
	   row8.set("state",1);
	   row8.set("code",9);
	   row8.set("visitdate", time);
	   jbdcms2Service.insertUserKFMsg(row8);
	   jsonObject.put("error", 1);
	   jsonObject.put("msg", "添加备注成功");
	   this.getWriter().write(jsonObject.toString());	
	   return null ;
   }
   public  ActionResult doAddtxremark() throws Exception {
	   logger.info("进入添加提醒还款备注");
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
		 
	   int rec_id = getIntParameter("re_id",0);//借款ID
	   int u_id=getIntParameter("user_id",0);
	   int dxmb =getIntParameter("dxmb",0);
	   int txbz =getIntParameter("txbz",0);
	   String xztxbz = "";
	   if(txbz == 1){
		   xztxbz ="KH sẽ thanh toán đúng hạn";
	   }else if(txbz == 2){
		   xztxbz ="KH hẹn lại ngày thanh toán";
	   }else if(txbz == 3){
		   xztxbz ="KH xin gia hạn 15 ngày";
	   }else if(txbz == 4){
		   xztxbz ="KH xin gia hạn 30 ngày";
	   }else if(txbz == 5){
		   xztxbz ="KH không nghe máy";
	   }else if(txbz == 6){
		   xztxbz ="KH khóa máy";
	   }else if(txbz == 7){
		   xztxbz ="Thuê bao ngừng sử dụng";
	   }
	   
	   String remark = xztxbz+getStrParameter("remark");
	   if(remark != "" || remark != null){
		   remark = remark.replace("&nbsp;", " ");
	   }

	   //JSONObject jsonObject = new JSONObject(); 
	   int userid2 = SessionHelper.getInt("cmsuserid", getSession());
	   
	   if(userid2==0){
		   jsonObject.put("error", -1);
		   jsonObject.put("msg", "Vui lòng đăng nhập trước");
		   this.getWriter().write(jsonObject.toString());	
		   return null;				 
		   
	   }
	   logger.info("请求ID:"+userid2);
	   if(u_id ==0){
		   jsonObject.put("error", -1);
		   jsonObject.put("msg", "用户不存在");
		   this.getWriter().write(jsonObject.toString());	
		   return null;				
	   }
	   if(rec_id ==0){
		   jsonObject.put("error", -2);
		   jsonObject.put("msg", "还款不存在");
		   this.getWriter().write(jsonObject.toString());	
		   return null;				
	   }	 
	   Date date=new Date();
	   DateFormat format=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	   String time=format.format(date);
	   //添加催收备注
	   DataRow  dataRow2 = jbdcms2Service.getUserInfo(rec_id);
	   String userName = dataRow2.getString("username").substring(0, 4);
		String appName = "OCEAN";
		if (userName.equals("OCEAN")) {
			appName = "OCEAN";
		}
//	   if(dxmb != 0){
//		   String smscontent="";
//		   if(dxmb == 1){
//			   smscontent =appName+" xin chao! Vui long tt Khoan vay se dao han ngay "+dataRow2.getString("hkyq_time")+", so tien phai tra "+dataRow2.getString("sjsh_money")+". Thanh toan dung han se tang han muc lan vay sau. Neu tre han, ban phai chiu moi phi phat. Moi thac mac xin inbox http://bit.ly/2QJAh16, hotline 1900234558.";
//		   }else if(dxmb == 2){
//			   smscontent =appName+" chao! Vui long mo app xem thong tin chuyen khoan, co the chuyen qua ngan hang, Viettel (Luu y ghi ma so giao dich Viettel) ,Buu dien (24/7), noi dung chuyen tien: Ho ten, so CMND tra vay. Thac mac xin inbox http://bit.ly/2QJAh16, hotline 1900234558. ";
//		   }
//		   String content = "[{\"PhoneNumber\":\""+dataRow2.getString("mobilephone")+"\",\"Message\":\""+smscontent+"\",\"SmsGuid\":\""+dataRow2.getString("mobilephone")+"\",\"ContentType\":1}]";
//		   String con = URLEncoder.encode(content, "utf-8");
//		   SendMsg sendMsg = new SendMsg();
//		   //String returnString = SendMsg.sendMessageByGet(con,dataRow2.getString("mobilephone"));
//	   }
	   
	   DataRow row7=  new DataRow();	
	   row7.set("userid",u_id);	
	   row7.set("msg" ,remark);
	   row7.set("msgtype","提醒");
	   row7.set("jkid",rec_id);//项目id
	   row7.set("create_time", new Date());
	   row7.set("cl_ren",userid2);
	   jbdcms2Service.insertUserChMsg(row7);
	   DataRow row8=  new DataRow();	
	   row8.set("user_id",u_id);	
	   row8.set("content" ,remark);
	   row8.set("jkjl_id",rec_id );
	   row8.set("kefuid",userid2);
	   row8.set("state",1);
	   row8.set("code",1);
	   row8.set("visitdate", time);
	   jbdcms2Service.insertUserKFMsg(row8);
	   jsonObject.put("error", 1);
	   jsonObject.put("msg", "添加备注成功");
	   this.getWriter().write(jsonObject.toString());	
	   return null ;
   }
   /**
    * 待放款用户
    *    
    */
   public  ActionResult   doGetrecordListFour() throws Exception{
	   
	   logger.info("待放款用户");
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
		 
   	   int temp = getIntParameter("temp",0);
		String  tempVelue = getStrParameter("tempvl"); 				
		String  startDate1 =getStrParameter("startDate");
		String  endDate1 =getStrParameter("endDate");
		int isolduser =getIntParameter("isolduser");
		String startDate="";
		String endDate="";
		if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
		String[] sourceStrArray1 = startDate1.split("-");
		String[] sourceStrArray2 = endDate1.split("-");
        startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
        endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
		}
		SimpleDateFormat famt = new SimpleDateFormat("dd-MM-yyyy");
		String time = famt.format(new Date());
		//审核状态
		String commit = getStrParameter("commit");
		//定义用户选择条件
		String userId ="" ;
		String realName="";
		String phone = "" ;	
		String idCard ="" ;	
		logger.info(temp+"  "+tempVelue);
		if(temp ==1){	   	
			userId =tempVelue ;
		}
		
		if(temp ==2){
					
			realName =tempVelue ;
		}
		if(temp ==3) {
					
			phone = tempVelue ;
		}
		if(temp ==4) {
			
			idCard = tempVelue ;
		}	
		java.net.URLEncoder.encode(idCard);
		//默认第一页		
		int curPage  =getIntParameter("curPage",1);	
		DBPage page = jbdcms2Service.getRecordListDFK(curPage,10,userId,realName,phone,commit,idCard ,startDate,endDate,isolduser);
		List<DataRow> list = jbdcms2Service.getRecordListDFKList(curPage,10,userId,realName,phone,commit,idCard ,time,startDate,endDate,isolduser);
		double xszmoney = 0;
		double xsyjmoney = 0;
		double sdzmoney = 0;
		double sdyjmoney = 0;
		for (DataRow dataRow : list) {
			if(dataRow.getInt("napasbankno") == 0){
				if(dataRow.getInt("sfyfk") == 1){
					sdyjmoney += Integer.parseInt(dataRow.getString("sjds_money").replace(",", ""));
				}
				if(dataRow.getInt("sfyfk") == 1 || dataRow.getInt("sfyfk") == 2){
					sdzmoney += Integer.parseInt(dataRow.getString("sjds_money").replace(",", ""));
				}
			}else{
				if(dataRow.getInt("sfyfk") == 1){
					xsyjmoney += Integer.parseInt(dataRow.getString("sjds_money").replace(",", ""));
				}
				if(dataRow.getInt("sfyfk") == 1 || dataRow.getInt("sfyfk") == 2){
					xszmoney += Integer.parseInt(dataRow.getString("sjds_money").replace(",", ""));
				}
			}
		}
		DecimalFormat fat = new DecimalFormat("###,###");
	    double total_sjdsmoney = jbdcms2Service.getdfkmoneyaccount(isolduser);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("xszmoney", fat.format(xszmoney));
		row.set("xsyjmoney", fat.format(xsyjmoney));
		row.set("sdzmoney", fat.format(sdzmoney));
		row.set("sdyjmoney", fat.format(sdyjmoney));
		row.set("totaldfk", page.getTotalRows());
		row.set("totalsjdsmoney", fat.format(total_sjdsmoney));
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);	
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
     	return null ;  	  
   }
  
	/**
	 * 借款申请成功用户
	 * 
	 */
	public ActionResult doGetRecordListChg02() throws Exception {
		
		logger.info("放款成功用户");
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
		 
		int temp = getIntParameter("temp",0);
		//JSONObject jsonObject = new JSONObject();
		int userid = SessionHelper.getInt("cmsuserid", getSession()); // 操作员ID
		if (userid == 0) {
			jsonObject.put("error", -1);
			jsonObject.put("msg", "Vui lòng đăng nhập trước"); 
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		int xrcode = jbdcms2Service.getSHQX(userid);
		if(xrcode == 1){
			jsonObject.put("error", -1);
			jsonObject.put("msg", "không có quyền thao tác！"); 
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		String  tempVelue = getStrParameter("tempvl"); 
		String hkstat = getStrParameter("hkstat");
		String  startDate1 =getStrParameter("startDate");
		String  endDate1 =getStrParameter("endDate");
		String startDate="";
		String endDate="";
		if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
		String[] sourceStrArray1 = startDate1.split("-");
		String[] sourceStrArray2 = endDate1.split("-");
        startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
        endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
		}
		//审核状态
		String commit = getStrParameter("commit");
		//定义用户选择条件
		String userId ="" ;
		String realName="";
		String phone = "" ;	
		String idCard ="" ;
		String investorId ="" ;
		String investorName ="" ;
		String hkqd = "";
		logger.info("temp:"+temp+"startDate:"+startDate+" endDate"+endDate);
		if(temp ==1){
				   	
			userId =tempVelue ;
		}
		
		if(temp ==2){
					
			realName =tempVelue.replace("&nbsp;", " ") ;
		}
		if(temp ==3) {
					
			phone = tempVelue ;
		}
		if(temp ==4) {
			
			idCard = tempVelue ;
		}
		if(temp ==5) {
			
			investorId =tempVelue ;
		}
        if(temp ==6) {
			
			investorName =tempVelue ;
		}
        if(temp ==7) {
			
			hkqd ="0" ;
		}
		//默认第一页
		int curPage  =getIntParameter("curPage",1);	
		DataRow row = new DataRow();
		
		DBPage page = jbdcms2Service.getRecordListChg07(curPage,15,userId,realName,phone,startDate,endDate,commit,idCard,hkstat ,investorId ,investorName,hkqd);
				
		if (!StringHelper.isEmpty(startDate)) {
			DBPage page2 = jbdcms2Service.getRecordListChg07(curPage,1000000,userId,realName,phone,startDate,endDate,commit,idCard,hkstat ,investorId ,investorName,hkqd);
			row.set("list2", page2);
		}else{
			row.set("list2", page);
		}
		
		System.out.println("1111");
	
		row.set("list", page);
		row.set("error", 1);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		row.set("hkstat",hkstat);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
   	    return null ;  
		
	}
public ActionResult doGetRecordListChg03() throws Exception {
		
		logger.info("放款成功用户");
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
		 
		int temp = getIntParameter("temp",0);
		String  tempVelue = getStrParameter("tempvl"); 		

		String  startDate =getStrParameter("startDate");
		String  endDate =getStrParameter("endDate");
		      
		String hkstat = getStrParameter("hkstat");       
		//审核状态
		String commit = getStrParameter("commit");
		//定义用户选择条件
		String userId ="" ;
		String realName="";
		String phone = "" ;	
		String idCard ="" ;
		String investorId ="" ;
		String investorName ="" ;

		String profession="";
		String hongbaoid="";
		String pingfen="";
		String zffangshi="";
		String jkqx="";
		String refferee="";
		String off="";
		String olduser="";
		
		logger.info("temp:"+temp+"startDate:"+startDate+" endDate"+endDate);
		int startmoney = jbdcms2Service.getStartMsum(investorId) ; // 起始资金
		if(temp ==1){
				   	
			userId =tempVelue ;
		}
		
		if(temp ==2){
					
			realName =tempVelue ;
		}
		if(temp ==3) {
					
			phone = tempVelue ;
		}
		if(temp ==4) {
			
			idCard = tempVelue ;
		}
		if(temp ==5) {
			
			investorId =tempVelue ;
			startmoney = jbdcms2Service.getStartM(investorId) ; // 起始资金
		}
        if(temp ==6) {
			
			investorName =tempVelue ;
			startmoney = jbdcms2Service.getStartMN(investorName) ; // 起始资金
		}
        if(temp ==7) {
    		
    		profession = tempVelue ;
    	}
        if(temp ==8) {
        	
        	hongbaoid = tempVelue ;
        }
        if(temp ==9) {
        	
        	pingfen = tempVelue ;
        }
        if(temp ==10) {
        	
        	zffangshi = tempVelue ;
        }
        if(temp ==11) {
        	jkqx = tempVelue ;
        }
        if(temp ==12) {
        	jkqx = tempVelue ;
        }
        if(temp ==13) {
        	
        	refferee = tempVelue ;
        }
        if(temp ==14) {
        	
        	off = 14+"" ;
        }
        if(temp ==15) {
        	
        	olduser = tempVelue ;
        }
		//默认第一页
		int curPage  =getIntParameter("curPage",1);	
		DBPage page = jbdcms2Service.getRecordListChg03(curPage,15,userId,realName,phone,profession,hongbaoid,pingfen,zffangshi,temp,jkqx,refferee,startDate,endDate,commit,idCard,hkstat ,investorId ,investorName,off,olduser);
		List<DataRow> list=jbdcms2Service.getRecordListChg03(userId,realName,phone,profession,hongbaoid,pingfen,zffangshi,temp,jkqx,refferee,startDate,endDate,commit,idCard,hkstat ,investorId ,investorName,off,olduser);
		DecimalFormat df = new DecimalFormat( "###,###"); //设置double类型小数点后位数格式 
		double totalYE = 0 ; // 账户余额
		double totalFmoney=0;  //总放款金额
		double totalWmoney=0;  //总未还款金额
		double totalYmoney=0;  //总已还款金额
		double totalYmoneyBJ=0;  //总已还款金额
		double totalCapital=0; //待回收本金
		double totalOverdueInterest=0;//逾期总利息
		double totalDHSInterest=0;//待回收利息
		/*Double tatalYHSCapital=0.0;//已回收本金
*/		double totalLY=0;  //总利息
		double totalDHSYQLX =0 ;//待回收逾期总利息
		double totalWHSYQLX =0 ;//免除逾期利息
		double totalYQZB = 0 ;//逾期到手金额   
		double totalYQZBJ = 0 ;//逾期审核金额         
		double totalDSYQZBJLX = 0 ;//待收逾期总本金和利息     
		double totalDSYQZBJ = 0 ;//待收逾期总本金        
		double totalZFQBJ = 0 ;//总分期本金
		double totalDSFQBJ = 0 ;//待收分期本金
		double totalYSFQLX = 0 ;//已收分期利息        
		double yhfqbflx = 0; // 已还延期和部分利息     
		double hongbaojine = 0; // 已还延期和部分利息     
		
		for (DataRow dataRow : list) {
			double sjds =Integer.parseInt(dataRow.getString("sjds_money").replace(",", ""));
			totalFmoney+=sjds;
			
			double yuq =Integer.parseInt(dataRow.getString("yuq_lx").replace(",", ""));
			double yuqyh =Integer.parseInt(dataRow.getString("yuq_yhlx").replace(",", ""));
			double hkfqzlx =Integer.parseInt(dataRow.getString("hkfqzlx").replace(",", ""));
			totalYSFQLX += hkfqzlx;
			double whyuq =Integer.parseInt(dataRow.getString("yuq_whlx").replace(",", ""));
			double sjsh =Integer.parseInt(dataRow.getString("sjsh_money").replace(",", ""));
			double lx =Integer.parseInt(dataRow.getString("lx").replace(",", ""));
			if("1".equals(dataRow.getString("hongbaoid"))){
				if(dataRow.getString("fkdz_time_day").compareTo("2018-08-27 15:20:00")>0 && dataRow.getString("fkdz_time_day").compareTo("2018-09-18 14:11:00")<0){
					hongbaojine += 30000;
				}else if(dataRow.getString("fkdz_time_day").compareTo("2018-09-18 14:10:00")>0){
					hongbaojine += 10000;
				}else{
					hongbaojine += 50000;
				}
			}
			//int fuwum = hkfqlx + yuq + sjsh - sjds;
			if(dataRow.getString("sfyhw").equals("0")){  //该订单未还
				totalWmoney+=sjsh+ yuq;
				if((sjsh-lx)>0){
					totalCapital+=sjsh-lx;
				}else{
					totalCapital+=0;
				}
				if((sjsh-lx)>0){
					totalDHSInterest+= lx+yuq ;
				}else{
					totalDHSInterest+=sjsh+yuq ;
				}
				
				if(dataRow.getInt("hkfq_code") == 1){
					totalDSFQBJ += sjsh-lx;
				}
				if(dataRow.getInt("yuq_ts")>0 && dataRow.getInt("hkfq_code")==0){
					if((sjsh-lx)>=0) {
						totalDHSYQLX += lx+yuq ;
					}else {
						totalDHSYQLX +=sjsh+yuq;
					}
					if((sjsh-lx)>=0){
						totalDSYQZBJ+=sjsh-lx;
					}else{
						totalDSYQZBJ+=0;
					}
					totalDSYQZBJLX+=sjsh+yuq;
				}
				yhfqbflx += hkfqzlx+yuqyh+sjds+lx-sjsh;
			}
			if(dataRow.getString("sfyhw").equals("1")){  //该订单已还
				totalYmoney+=sjds+lx+yuq+yuqyh+hkfqzlx-whyuq;
				totalYmoneyBJ+=(sjsh-lx);
				totalWHSYQLX += whyuq;
				/*tatalYHSCapital+=dataRow.getDouble("sjds_money");*/
			}
			if(dataRow.getInt("yuq_ts")>0 && dataRow.getInt("hkfq_code")==0){
				totalOverdueInterest+=lx+yuq ;
				totalYQZB+=sjds;
				totalYQZBJ+=sjds+lx+yuq;
			}
			if(dataRow.getInt("hkfq_code") == 1){
				totalZFQBJ += sjsh-lx;
			}
			totalLY+=hkfqzlx+yuq+yuqyh+lx-whyuq;  //总利息
		}
		totalYmoney += yhfqbflx ;
		DecimalFormat dfdf = new DecimalFormat("0.00");
		Double yqbl = 0.00;
		Double yqwhbl = 0.00;
		Double ylbl = 0.00;
		yqbl =  ((double)totalYQZB*100 / (totalDSYQZBJ+totalFmoney-totalCapital));
		yqwhbl =  ((double)totalDSYQZBJ*100 / (totalDSYQZBJ+totalYmoneyBJ));
		ylbl =  ((double)(totalLY - totalDHSInterest - totalDSYQZBJ)*100 / (totalFmoney - totalCapital + totalDSYQZBJ));
		totalYE = totalLY -totalDHSInterest -totalYQZB ;
		DataRow row = new DataRow();
		row.set("hongbaojine", df.format(hongbaojine));
		row.set("totalYE", df.format(totalYE));
		row.set("totalYSFQLX", df.format(totalYSFQLX));
		row.set("totalZFQBJ", df.format(totalZFQBJ));
		row.set("totalDSFQBJ", df.format(totalDSFQBJ));
		row.set("tjttotalYE", totalYE);
		row.set("yqwhbl", dfdf.format(yqwhbl));
		row.set("ylbl", dfdf.format(ylbl));
		row.set("yqbl", dfdf.format(yqbl));
		row.set("totalFmoney", df.format(totalFmoney));
		row.set("tjttotalFmoney",totalFmoney);
		row.set("totalWmoney", df.format(totalWmoney));
		row.set("tjttotalWmoney",totalWmoney);
		row.set("totalYmoney", df.format(totalYmoney));
		row.set("tjttotalYmoney",totalYmoney);
		row.set("totalLY", df.format(totalLY));
		row.set("tjttotalLY",totalLY);
		row.set("totalOverdueInterest", df.format(totalOverdueInterest));
		row.set("tjttotalOverdueInterest",totalOverdueInterest);
		row.set("totalCapital", df.format(totalCapital));
		row.set("tjttotalCapital",totalCapital);
		row.set("totalDHSInterest",df.format(totalDHSInterest));
		row.set("tjttotalDHSInterest",totalDHSInterest);
		
		/*row.set("tatalYHSCapital",df.format(tatalYHSCapital));*/
		row.set("totalDHSYQLX",df.format(totalDHSYQLX));
		row.set("tjttotalDHSYQLX",totalDHSYQLX);
		row.set("totalWHSYQLX",df.format(totalWHSYQLX));
		row.set("tjttotalWHSYQLX",totalWHSYQLX);
		row.set("totalYQZB",df.format(totalYQZB));
		row.set("tjttotalYQZB",totalYQZB);
		row.set("totalYQZBJ",df.format(totalYQZBJ));
		row.set("tjttotalYQZBJ",totalYQZBJ);
		row.set("totalDSYQZBJLX",df.format(totalDSYQZBJLX));
		row.set("tjttotalDSYQZBJLX",totalDSYQZBJLX);
		row.set("totalDSYQZBJ",df.format(totalDSYQZBJ));
		row.set("tjttotalDSYQZBJ",totalDSYQZBJ);
		row.set("list", page);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		row.set("hkstat",hkstat);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
   	    return null ;  
		
	}
public ActionResult doGetRecordListChgGLL() throws Exception {
	
	logger.info("放款成功用户高利率");
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
	 
	int temp = getIntParameter("temp",0);
	String  tempVelue = getStrParameter("tempvl"); 		

	String  startDate =getStrParameter("startDate");
	String  endDate =getStrParameter("endDate");
	      
	String hkstat = getStrParameter("hkstat");       
	String jkqx = getStrParameter("jkqx");       
	//审核状态
	String commit = getStrParameter("commit");
	//定义用户选择条件
	String userId ="" ;
	String realName="";
	String phone = "" ;	
	String idCard ="" ;
	String investorId ="" ;
	String investorName ="" ;

	String profession="";
	String hongbaoid="";
	String pingfen="";
	String zffangshi="";
	String refferee="";
	String off="";
	String olduser="";
	
	logger.info("temp:"+temp+"startDate:"+startDate+" endDate"+endDate);
	if(temp ==1){
			   	
		userId =tempVelue ;
	}
	
	if(temp ==2){
				
		realName =tempVelue ;
	}
	if(temp ==3) {
				
		phone = tempVelue ;
	}
	if(temp ==4) {
		
		idCard = tempVelue ;
	}
    if(temp ==7) {
		
		profession = tempVelue ;
	}
    if(temp ==8) {
    	
    	hongbaoid = tempVelue ;
    }
    if(temp ==9) {
    	
    	pingfen = tempVelue ;
    }
    if(temp ==10) {
    	
    	zffangshi = tempVelue ;
    }
    if(temp ==13) {
    	
    	refferee = tempVelue ;
    }
    if(temp ==14) {
    	
    	off = String.valueOf(temp) ;
    }
    if(temp ==15) {
    	
    	olduser = tempVelue ;
    }
	//默认第一页
	int curPage  =getIntParameter("curPage",1);	
	DBPage page = jbdcms2Service.getRecordListChgGLL(curPage,15,userId,realName,phone,profession,hongbaoid,pingfen,zffangshi,temp,jkqx,refferee,startDate,endDate,commit,idCard,hkstat ,investorId ,investorName,off,olduser);
	List<DataRow> list=jbdcms2Service.getRecordListChgGLL(userId,realName,phone,profession,hongbaoid,pingfen,zffangshi,temp,jkqx,refferee,startDate,endDate,commit,idCard,hkstat ,investorId ,investorName,off,olduser);
	DecimalFormat df = new DecimalFormat( "###,###"); //设置double类型小数点后位数格式 
	double totalYE = 0 ; // 账户余额
	double totalFmoney=0;  //总放款金额
	double totalWmoney=0;  //总未还款金额
	double totalYmoney=0;  //总已还款金额
	double totalYmoneyBJ=0;  //总已还款金额
	double totalCapital=0; //待回收本金
	double totalOverdueInterest=0;//逾期总利息
	double totalDHSInterest=0;//待回收利息
	/*Double tatalYHSCapital=0.0;//已回收本金
*/		double totalLY=0;  //总利息
	double totalDHSYQLX =0 ;//待回收逾期总利息
	double totalWHSYQLX =0 ;//免除逾期利息
	double totalYQZB = 0 ;//逾期到手金额   
	double totalYQZBJ = 0 ;//逾期审核金额         
	double totalDSYQZBJLX = 0 ;//待收逾期总本金和利息     
	double totalDSYQZBJ = 0 ;//待收逾期总本金        
	double totalZFQBJ = 0 ;//总分期本金
	double totalDSFQBJ = 0 ;//待收分期本金
	double totalYSFQLX = 0 ;//已收分期利息        
	double yhfqbflx = 0; // 已还延期和部分利息     
	double hongbaojine = 0; // 已还延期和部分利息     
	
	for (DataRow dataRow : list) {
		double sjds =Integer.parseInt(dataRow.getString("sjds_money").replace(",", ""));
		totalFmoney+=sjds;
		
		double yuq =Integer.parseInt(dataRow.getString("yuq_lx").replace(",", ""));
		double yuqyh =Integer.parseInt(dataRow.getString("yuq_yhlx").replace(",", ""));
		double hkfqzlx =Integer.parseInt(dataRow.getString("hkfqzlx").replace(",", ""));
		totalYSFQLX += hkfqzlx;
		double whyuq =Integer.parseInt(dataRow.getString("yuq_whlx").replace(",", ""));
		double sjsh =Integer.parseInt(dataRow.getString("sjsh_money").replace(",", ""));
		double lx =Integer.parseInt(dataRow.getString("lx").replace(",", ""));
		if("1".equals(dataRow.getString("hongbaoid"))){
			if(dataRow.getString("fkdz_time_day").compareTo("2018-08-27 15:20:00")>0 && dataRow.getString("fkdz_time_day").compareTo("2018-09-18 14:11:00")<0){
				hongbaojine += 30000;
			}else if(dataRow.getString("fkdz_time_day").compareTo("2018-09-18 14:10:00")>0){
				hongbaojine += 10000;
			}else{
				hongbaojine += 50000;
			}
		}
		//int fuwum = hkfqlx + yuq + sjsh - sjds;
		if(dataRow.getString("sfyhw").equals("0")){  //该订单未还
			totalWmoney+=sjsh+ yuq;
			if((sjsh-lx)>0){
				totalCapital+=sjsh-lx;
			}else{
				totalCapital+=0;
			}
			if((sjsh-lx)>0){
				totalDHSInterest+= lx+yuq ;
			}else{
				totalDHSInterest+=sjsh+yuq ;
			}
			
			if(dataRow.getInt("hkfq_code") == 1){
				totalDSFQBJ += sjsh-lx;
			}
			if(dataRow.getInt("yuq_ts")>0 && dataRow.getInt("hkfq_code")==0){
				if((sjsh-lx)>=0) {
					totalDHSYQLX += lx+yuq ;
				}else {
					totalDHSYQLX +=sjsh+yuq;
				}
				if((sjsh-lx)>=0){
					totalDSYQZBJ+=sjsh-lx;
				}else{
					totalDSYQZBJ+=0;
				}
				totalDSYQZBJLX+=sjsh+yuq;
			}
			yhfqbflx += hkfqzlx+yuqyh+sjds+lx-sjsh;
		}
		if(dataRow.getString("sfyhw").equals("1")){  //该订单已还
			totalYmoney+=sjds+lx+yuq+yuqyh+hkfqzlx-whyuq;
			totalYmoneyBJ+=(sjsh-lx);
			totalWHSYQLX += whyuq;
			/*tatalYHSCapital+=dataRow.getDouble("sjds_money");*/
		}
		if(dataRow.getInt("yuq_ts")>0 && dataRow.getInt("hkfq_code")==0){
			totalOverdueInterest+=lx+yuq ;
			totalYQZB+=sjds;
			totalYQZBJ+=sjds+lx+yuq;
		}
		if(dataRow.getInt("hkfq_code") == 1){
			totalZFQBJ += sjsh-lx;
		}
		totalLY+=hkfqzlx+yuq+yuqyh+lx-whyuq;  //总利息
	}
	totalYmoney += yhfqbflx ;
	DecimalFormat dfdf = new DecimalFormat("0.00");
	Double yqbl = 0.00;
	Double yqwhbl = 0.00;
	Double ylbl = 0.00;
	yqbl =  ((double)totalYQZB*100 / (totalDSYQZBJ+totalFmoney-totalCapital));
	yqwhbl =  ((double)totalDSYQZBJ*100 / (totalDSYQZBJ+totalYmoneyBJ));
	ylbl =  ((double)(totalLY - totalDHSInterest - totalDSYQZBJ)*100 / (totalFmoney - totalCapital + totalDSYQZBJ));
	totalYE = totalLY -totalDHSInterest -totalYQZB ;
	DataRow row = new DataRow();
	row.set("hongbaojine", df.format(hongbaojine));
	row.set("totalYE", df.format(totalYE));
	row.set("totalYSFQLX", df.format(totalYSFQLX));
	row.set("totalZFQBJ", df.format(totalZFQBJ));
	row.set("totalDSFQBJ", df.format(totalDSFQBJ));
	row.set("tjttotalYE", totalYE);
	row.set("yqwhbl", dfdf.format(yqwhbl));
	row.set("ylbl", dfdf.format(ylbl));
	row.set("yqbl", dfdf.format(yqbl));
	row.set("totalFmoney", df.format(totalFmoney));
	row.set("tjttotalFmoney",totalFmoney);
	row.set("totalWmoney", df.format(totalWmoney));
	row.set("tjttotalWmoney",totalWmoney);
	row.set("totalYmoney", df.format(totalYmoney));
	row.set("tjttotalYmoney",totalYmoney);
	row.set("totalLY", df.format(totalLY));
	row.set("tjttotalLY",totalLY);
	row.set("totalOverdueInterest", df.format(totalOverdueInterest));
	row.set("tjttotalOverdueInterest",totalOverdueInterest);
	row.set("totalCapital", df.format(totalCapital));
	row.set("tjttotalCapital",totalCapital);
	row.set("totalDHSInterest",df.format(totalDHSInterest));
	row.set("tjttotalDHSInterest",totalDHSInterest);
	
	/*row.set("tatalYHSCapital",df.format(tatalYHSCapital));*/
	row.set("totalDHSYQLX",df.format(totalDHSYQLX));
	row.set("tjttotalDHSYQLX",totalDHSYQLX);
	row.set("totalWHSYQLX",df.format(totalWHSYQLX));
	row.set("tjttotalWHSYQLX",totalWHSYQLX);
	row.set("totalYQZB",df.format(totalYQZB));
	row.set("tjttotalYQZB",totalYQZB);
	row.set("totalYQZBJ",df.format(totalYQZBJ));
	row.set("tjttotalYQZBJ",totalYQZBJ);
	row.set("totalDSYQZBJLX",df.format(totalDSYQZBJLX));
	row.set("tjttotalDSYQZBJLX",totalDSYQZBJLX);
	row.set("totalDSYQZBJ",df.format(totalDSYQZBJ));
	row.set("tjttotalDSYQZBJ",totalDSYQZBJ);
	row.set("list", page);
	row.set("temp",temp);
	row.set("tempvalu",tempVelue);
	row.set("hkstat",hkstat);
	row.set("jkqx",jkqx);
	JSONObject object = JSONObject.fromBean(row);	
	this.getWriter().write(object.toString());	
	    return null ;  
	
}
public ActionResult doGetRecordListChg111() throws Exception {
	
	logger.info("放款成功用户");
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
	 
	int temp = getIntParameter("temp",0);
	String  tempVelue = getStrParameter("tempvl"); 		
	
	String  startDate =getStrParameter("startDate");
	String  endDate =getStrParameter("endDate");
	
	String hkstat = getStrParameter("hkstat");       
	//审核状态
	String commit = getStrParameter("commit");
	//定义用户选择条件
	String userId ="" ;
	String realName="";
	String phone = "" ;	
	String idCard ="" ;
	String investorId ="" ;
	String investorName ="" ;
	
	String profession="";
	String hongbaoid="";
	String pingfen="";
	String zffangshi="";
	String jkqx="";
	String refferee="";
	String off="";
	String olduser="";
	
	logger.info("temp:"+temp+"startDate:"+startDate+" endDate"+endDate);
	int startmoney = jbdcms2Service.getStartMsum(investorId) ; // 起始资金
	if(temp ==1){
		
		userId =tempVelue ;
	}
	
	if(temp ==2){
		
		realName =tempVelue ;
	}
	if(temp ==3) {
		
		phone = tempVelue ;
	}
	if(temp ==4) {
		
		idCard = tempVelue ;
	}
	if(temp ==5) {
		
		investorId =tempVelue ;
		startmoney = jbdcms2Service.getStartM(investorId) ; // 起始资金
	}
	if(temp ==6) {
		
		investorName =tempVelue ;
		startmoney = jbdcms2Service.getStartMN(investorName) ; // 起始资金
	}
	if(temp ==7) {
		
		profession = tempVelue ;
	}
	if(temp ==8) {
		
		hongbaoid = tempVelue ;
	}
	if(temp ==9) {
		
		pingfen = tempVelue ;
	}
	if(temp ==10) {
		
		zffangshi = tempVelue ;
	}
	if(temp ==11) {
		jkqx = tempVelue ;
	}
	if(temp ==12) {
		jkqx = tempVelue ;
	}
	if(temp ==13) {
		
		refferee = tempVelue ;
	}
	if(temp ==14) {
		
		off = 14+"" ;
	}
	if(temp ==15) {
		
		olduser = tempVelue ;
	}
	//默认第一页
	int curPage  =getIntParameter("curPage",1);	
	DBPage page = jbdcms2Service.getRecordListChg111(curPage,15,userId,realName,phone,profession,hongbaoid,pingfen,zffangshi,temp,jkqx,refferee,startDate,endDate,commit,idCard,hkstat ,investorId ,investorName,off,olduser);
	List<DataRow> list=jbdcms2Service.getRecordListChg111(userId,realName,phone,profession,hongbaoid,pingfen,zffangshi,temp,jkqx,refferee,startDate,endDate,commit,idCard,hkstat ,investorId ,investorName,off,olduser);
	DecimalFormat df = new DecimalFormat( "###,###"); //设置double类型小数点后位数格式 
	double totalYE = 0 ; // 账户余额
	double totalFmoney=0;  //总放款金额
	double totalWmoney=0;  //总未还款金额
	double totalYmoney=0;  //总已还款金额
	double totalYmoneyBJ=0;  //总已还款金额
	double totalCapital=0; //待回收本金
	double totalOverdueInterest=0;//逾期总利息
	double totalDHSInterest=0;//待回收利息
	/*Double tatalYHSCapital=0.0;//已回收本金
	 */		double totalLY=0;  //总利息
	 double totalDHSYQLX =0 ;//待回收逾期总利息
	 double totalWHSYQLX =0 ;//免除逾期利息
	 double totalYQZB = 0 ;//逾期到手金额   
	 double totalYQZBJ = 0 ;//逾期审核金额         
	 double totalDSYQZBJLX = 0 ;//待收逾期总本金和利息     
	 double totalDSYQZBJ = 0 ;//待收逾期总本金        
	 double totalZFQBJ = 0 ;//总分期本金
	 double totalDSFQBJ = 0 ;//待收分期本金
	 double totalYSFQLX = 0 ;//已收分期利息        
	 double yhfqbflx = 0; // 已还延期和部分利息     
	 double hongbaojine = 0; // 已还延期和部分利息     
	 
	 for (DataRow dataRow : list) {
		 double sjds =Integer.parseInt(dataRow.getString("sjds_money").replace(",", ""));
		 totalFmoney+=sjds;
		 
		 double yuq =Integer.parseInt(dataRow.getString("yuq_lx").replace(",", ""));
		 double yuqyh =Integer.parseInt(dataRow.getString("yuq_yhlx").replace(",", ""));
		 double hkfqzlx =Integer.parseInt(dataRow.getString("hkfqzlx").replace(",", ""));
		 totalYSFQLX += hkfqzlx;
		 double whyuq =Integer.parseInt(dataRow.getString("yuq_whlx").replace(",", ""));
		 double sjsh =Integer.parseInt(dataRow.getString("sjsh_money").replace(",", ""));
		 double lx =Integer.parseInt(dataRow.getString("lx").replace(",", ""));
		 if("1".equals(dataRow.getString("hongbaoid"))){
			 if(dataRow.getString("fkdz_time_day").compareTo("2018-08-27 15:20:00")>0 && dataRow.getString("fkdz_time_day").compareTo("2018-09-18 14:11:00")<0){
				 hongbaojine += 30000;
			 }else if(dataRow.getString("fkdz_time_day").compareTo("2018-09-18 14:10:00")>0){
				 hongbaojine += 10000;
			 }else{
				 hongbaojine += 50000;
			 }
		 }
		 //int fuwum = hkfqlx + yuq + sjsh - sjds;
		 if(dataRow.getString("sfyhw").equals("0")){  //该订单未还
			 totalWmoney+=sjsh+ yuq;
			 if((sjsh-lx)>0){
				 totalCapital+=sjsh-lx;
			 }else{
				 totalCapital+=0;
			 }
			 if((sjsh-lx)>0){
				 totalDHSInterest+= lx+yuq ;
			 }else{
				 totalDHSInterest+=sjsh+yuq ;
			 }
			 
			 if(dataRow.getInt("hkfq_code") == 1){
				 totalDSFQBJ += sjsh-lx;
			 }
			 if(dataRow.getInt("yuq_ts")>0 && dataRow.getInt("hkfq_code")==0){
				 
				 totalDHSYQLX += sjsh-sjds+yuq ;
				 if((sjsh-lx)>0){
					 totalDSYQZBJ+=sjsh-lx;
				 }else{
					 totalDSYQZBJ+=0;
				 }
				 totalDSYQZBJLX+=(sjsh+yuq);
			 }
			 yhfqbflx += hkfqzlx+yuqyh+sjds+lx-sjsh;
		 }
		 if(dataRow.getString("sfyhw").equals("1")){  //该订单已还
			 totalYmoney+=sjds+lx+yuq+yuqyh+hkfqzlx;
			 totalYmoneyBJ+=(sjsh-lx);
			 totalWHSYQLX += whyuq;
			 /*tatalYHSCapital+=dataRow.getDouble("sjds_money");*/
		 }
		 if(dataRow.getInt("yuq_ts")>0 && dataRow.getInt("hkfq_code")==0){
			 totalOverdueInterest+=sjsh-sjds+yuq ;
			 totalYQZBJ+=(sjds+lx+yuq);
			 totalYQZB+=sjds;
		 }
		 if(dataRow.getInt("hkfq_code") == 1){
			 totalZFQBJ += sjsh-lx;
		 }
		 totalLY+=hkfqzlx+yuq+yuqyh+lx;  //总利息
	 }
	 totalYmoney += yhfqbflx ;
	 DecimalFormat dfdf = new DecimalFormat("0.00");
	 Double yqbl = 0.00;
	 Double yqwhbl = 0.00;
	 Double ylbl = 0.00;
	 yqbl =  ((double)totalYQZB*100 / (totalDSYQZBJ+totalFmoney-totalCapital));
	 yqwhbl =  ((double)totalDSYQZBJ*100 / (totalDSYQZBJ+totalYmoneyBJ));
	 ylbl =  ((double)(totalLY - totalDHSInterest - totalDSYQZBJ)*100 / (totalFmoney - totalCapital + totalDSYQZBJ));
	 totalYE = totalLY -totalDHSInterest -totalYQZB ;
	 
	 
	 int zhucerenshu = jbdcms2Service.getCreateTimeByDate(startDate,endDate);
	 int zsqjk = jbdcms2Service.getZShenqingJK(startDate,endDate);
	 int fkzrs = list.size();
	 
	 DataRow row = new DataRow();
	 row.set("hongbaojine", df.format(hongbaojine));
	 row.set("totalYE", df.format(totalYE));
	 row.set("totalYSFQLX", df.format(totalYSFQLX));
	 row.set("totalZFQBJ", df.format(totalZFQBJ));
	 row.set("totalDSFQBJ", df.format(totalDSFQBJ));
	 row.set("tjttotalYE", totalYE);
	 row.set("yqwhbl", dfdf.format(yqwhbl));
	 row.set("ylbl", dfdf.format(ylbl));
	 row.set("yqbl", dfdf.format(yqbl));
	 row.set("totalFmoney", df.format(totalFmoney));
	 row.set("tjttotalFmoney",totalFmoney);
	 row.set("totalWmoney", df.format(totalWmoney));
	 row.set("tjttotalWmoney",totalWmoney);
	 row.set("totalYmoney", df.format(totalYmoney));
	 row.set("tjttotalYmoney",totalYmoney);
	 row.set("totalLY", df.format(totalLY));
	 row.set("tjttotalLY",totalLY);
	 row.set("totalOverdueInterest", df.format(totalOverdueInterest));
	 row.set("tjttotalOverdueInterest",totalOverdueInterest);
	 row.set("totalCapital", df.format(totalCapital));
	 row.set("tjttotalCapital",totalCapital);
	 row.set("totalDHSInterest",df.format(totalDHSInterest));
	 row.set("tjttotalDHSInterest",totalDHSInterest);
	 
	 /*row.set("tatalYHSCapital",df.format(tatalYHSCapital));*/
	 row.set("totalDHSYQLX",df.format(totalDHSYQLX));
	 row.set("tjttotalDHSYQLX",totalDHSYQLX);
	 row.set("totalWHSYQLX",df.format(totalWHSYQLX));
	 row.set("tjttotalWHSYQLX",totalWHSYQLX);
	 row.set("totalYQZB",df.format(totalYQZB));
	 row.set("tjttotalYQZB",totalYQZB);
	 row.set("totalYQZBJ",df.format(totalYQZBJ));
	 row.set("tjttotalYQZBJ",totalYQZBJ);
	 row.set("totalDSYQZBJLX",df.format(totalDSYQZBJLX));
	 row.set("tjttotalDSYQZBJLX",totalDSYQZBJLX);
	 row.set("totalDSYQZBJ",df.format(totalDSYQZBJ));
	 row.set("tjttotalDSYQZBJ",totalDSYQZBJ);
	 row.set("zhucerenshu",zhucerenshu);
	 row.set("zsqjk",zsqjk);
	 row.set("fkzrs",fkzrs);
	 row.set("list", page);
	 row.set("temp",temp);
	 row.set("tempvalu",tempVelue);
	 row.set("hkstat",hkstat);
	 JSONObject object = JSONObject.fromBean(row);	
	 this.getWriter().write(object.toString());	
	 return null ;  
	 
}

public ActionResult doGetRecordListChg14() throws Exception {
	
	logger.info("金融公司统计列表");
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
	 
	int temp = getIntParameter("temp",0);
	String  tempVelue = getStrParameter("tempvl"); 		
	
	String  startDate1 =getStrParameter("startDate");
	String  endDate1 =getStrParameter("endDate");
	String startDate="";
	String endDate="";
	if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
		String[] sourceStrArray1 = startDate1.split("-");
		String[] sourceStrArray2 = endDate1.split("-");
		startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
		endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
	}        
	String hkstat = getStrParameter("hkstat");       
	//审核状态
	String commit = getStrParameter("commit");
	//定义用户选择条件
	String userId ="" ;
	String realName="";
	String phone = "" ;	
	String idCard ="" ;
	String investorId ="" ;
	String investorName ="" ;
	logger.info("temp:"+temp+"startDate:"+startDate+" endDate"+endDate);
	int startmoney = jbdcms2Service.getStartMsum(investorId) ; // 起始资金
	if(temp ==1){
		
		userId =tempVelue ;
	}
	
	if(temp ==2){
		
		realName =tempVelue ;
	}
	if(temp ==3) {
		
		phone = tempVelue ;
	}
	if(temp ==4) {
		
		idCard = tempVelue ;
	}
	if(temp ==5) {
		
		investorId =tempVelue ;
		startmoney = jbdcms2Service.getStartM(investorId) ; // 起始资金
	}
	if(temp ==6) {
		
		investorName =tempVelue ;
		startmoney = jbdcms2Service.getStartMN(investorName) ; // 起始资金
	}
	//默认第一页
	int curPage  =getIntParameter("curPage",1);	
	DBPage page = jbdcms2Service.getRecordListChg03(curPage,15,userId,realName,phone,"","","","",0,"","",startDate,endDate,commit,idCard,hkstat ,investorId ,investorName,"","");
	List<DataRow> list=jbdcms2Service.getRecordListChg03(userId,realName,phone,startDate,"","","",0,"","","",endDate,commit,idCard,hkstat ,investorId ,investorName,"","");
	DecimalFormat df = new DecimalFormat( "###,###"); //设置double类型小数点后位数格式 
	int totalYE = 0 ; // 账户余额
	int totalFmoney=0;  //总放款金额
	int totalWmoney=0;  //总未还款金额
	int totalYmoney=0;  //总已还款金额
	int totalCapital=0; //待回收本金
	int totalOverdueInterest=0;//逾期总利息
	int totalDHSInterest=0;//待回收利息
	/*Double tatalYHSCapital=0.0;//已回收本金
	 */		int totalLY=0;  //总利息
	 int totalDHSYQLX =0 ;//待回收逾期总利息
	 int totalWHSYQLX =0 ;//未还逾期总利息
	 int totalYQZB = 0 ;//逾期到手金额   
	 int totalYQZBJ = 0 ;//逾期审核金额         
	 int totalDSYQZBJLX = 0 ;//待收逾期总本金和利息     
	 int totalDSYQZBJ = 0 ;//待收逾期总本金        
	 
	 
	 for (DataRow dataRow : list) {
		 int sjds =Integer.parseInt(dataRow.getString("sjds_money").replace(",", ""));
		 totalFmoney+=sjds;
		 int yuq =Integer.parseInt(dataRow.getString("yuq_lx").replace(",", ""))*97/100;
		 int whyuq =Integer.parseInt(dataRow.getString("yuq_whlx").replace(",", ""))*97/100;
		 int sjsh =Integer.parseInt(dataRow.getString("sjsh_money").replace(",", ""));
		 int fuwum = yuq + (sjsh - sjds)*97/100;
		 if(dataRow.getString("sfyhw").equals("0")){  //该订单未还
			 totalWmoney+=sjds + fuwum;
			 totalCapital+=sjds;
			 totalDHSInterest+= fuwum ;
			 totalDHSYQLX += yuq ;			
			 if(dataRow.getInt("yuq_ts")>0){
				 
				 totalDSYQZBJ+=sjds;
				 totalDSYQZBJLX+=(sjsh+yuq);
			 }
			 
		 }
		 if(dataRow.getString("sfyhw").equals("1")){  //该订单已还
			 totalYmoney+=sjds+fuwum;
			 totalWHSYQLX += whyuq;
			 /*tatalYHSCapital+=dataRow.getDouble("sjds_money");*/
		 }
		 if(dataRow.getInt("yuq_ts")>0){
			 totalOverdueInterest+=yuq;
			 totalYQZBJ+=(sjsh+yuq);
			 totalYQZB+=sjds;
		 }
		 
		 totalLY+=fuwum;  //总利息
	 }
	 totalYE = totalLY -totalDHSInterest;
	 DataRow row = new DataRow();
	 row.set("tjttotalYE", totalYE);
	 row.set("tjttotalFmoney",totalFmoney);
	 row.set("tjttotalWmoney",totalWmoney);
	 row.set("tjttotalYmoney",totalYmoney);
	 row.set("tjttotalLY",totalLY);
	 row.set("tjttotalOverdueInterest",totalOverdueInterest);
	 row.set("tjttotalCapital",totalCapital);
	 row.set("tjttotalDHSInterest",totalDHSInterest);
	 
	 /*row.set("tatalYHSCapital",df.format(tatalYHSCapital));*/
	 row.set("tjttotalDHSYQLX",totalDHSYQLX);
	 row.set("tjttotalWHSYQLX",totalWHSYQLX);
	 row.set("tjttotalYQZB",totalYQZB);
	 row.set("tjttotalYQZBJ",totalYQZBJ);
	 row.set("tjttotalDSYQZBJLX",totalDSYQZBJLX);
	 row.set("tjttotalDSYQZBJ",totalDSYQZBJ);
	 row.set("list", page);
	 row.set("temp",temp);
	 row.set("tempvalu",tempVelue);
	 row.set("hkstat",hkstat);
	 JSONObject object = JSONObject.fromBean(row);	
	 this.getWriter().write(object.toString());	
	 return null ;  
	 
}
public ActionResult doGetRecordListChg15() throws Exception {
	
	logger.info("典当行统计列表");
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
	 
	int temp = getIntParameter("temp",0);
	String  tempVelue = getStrParameter("tempvl"); 		
	
	String  startDate1 =getStrParameter("startDate");
	String  endDate1 =getStrParameter("endDate");
	String startDate="";
	String endDate="";
	if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
		String[] sourceStrArray1 = startDate1.split("-");
		String[] sourceStrArray2 = endDate1.split("-");
		startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
		endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
	}        
	String hkstat = getStrParameter("hkstat");       
	//审核状态
	String commit = getStrParameter("commit");
	//定义用户选择条件
	String userId ="" ;
	String realName="";
	String phone = "" ;	
	String idCard ="" ;
	String investorId ="" ;
	String investorName ="" ;
	logger.info("temp:"+temp+"startDate:"+startDate+" endDate"+endDate);
	int startmoney = jbdcms2Service.getStartMsum(investorId) ; // 起始资金
	if(temp ==1){
		
		userId =tempVelue ;
	}
	
	if(temp ==2){
		
		realName =tempVelue ;
	}
	if(temp ==3) {
		
		phone = tempVelue ;
	}
	if(temp ==4) {
		
		idCard = tempVelue ;
	}
	if(temp ==5) {
		
		investorId =tempVelue ;
		startmoney = jbdcms2Service.getStartM(investorId) ; // 起始资金
	}
	if(temp ==6) {
		
		investorName =tempVelue ;
		startmoney = jbdcms2Service.getStartMN(investorName) ; // 起始资金
	}
	//默认第一页
	int curPage  =getIntParameter("curPage",1);	
	DBPage page = jbdcms2Service.getRecordListChg03(curPage,15,userId,realName,phone,"","","","",0,"","",startDate,endDate,commit,idCard,hkstat ,investorId ,investorName,"","");
	List<DataRow> list=jbdcms2Service.getRecordListChg03(userId,realName,phone,"","","","",0,"","",startDate,endDate,commit,idCard,hkstat ,investorId ,investorName,"","");
	DecimalFormat df = new DecimalFormat( "###,###"); //设置double类型小数点后位数格式 
	int totalYE = 0 ; // 账户余额
	int totalFmoney=0;  //总放款金额
	int totalWmoney=0;  //总未还款金额
	int totalYmoney=0;  //总已还款金额
	int totalCapital=0; //待回收本金
	int totalOverdueInterest=0;//逾期总利息
	int totalDHSInterest=0;//待回收利息
	/*Double tatalYHSCapital=0.0;//已回收本金
	 */		int totalLY=0;  //总利息
	 int totalDHSYQLX =0 ;//待回收逾期总利息
	 int totalWHSYQLX =0 ;//未还逾期总利息
	 int totalYQZB = 0 ;//逾期到手金额   
	 int totalYQZBJ = 0 ;//逾期审核金额         
	 int totalDSYQZBJLX = 0 ;//待收逾期总本金和利息     
	 int totalDSYQZBJ = 0 ;//待收逾期总本金        
	 
	 
	 for (DataRow dataRow : list) {
		 int sjds =Integer.parseInt(dataRow.getString("sjds_money").replace(",", ""));
		 totalFmoney+=sjds;
		 int yuq =Integer.parseInt(dataRow.getString("yuq_lx").replace(",", ""))*3/100;
		 int whyuq =Integer.parseInt(dataRow.getString("yuq_whlx").replace(",", ""))*3/100;
		 int sjsh =Integer.parseInt(dataRow.getString("sjsh_money").replace(",", ""));
		 int fuwum = yuq + (sjsh - sjds)*3/100;
		 if(dataRow.getString("sfyhw").equals("0")){  //该订单未还
			 totalWmoney+=sjds + fuwum;
			 totalCapital+=sjds;
			 totalDHSInterest+= fuwum ;
			 totalDHSYQLX += yuq ;			
			 if(dataRow.getInt("yuq_ts")>0){
				 
				 totalDSYQZBJ+=sjds;
				 totalDSYQZBJLX+=(sjsh+yuq);
			 }
			 
		 }
		 if(dataRow.getString("sfyhw").equals("1")){  //该订单已还
			 totalYmoney+=sjds+fuwum;
			 totalWHSYQLX += whyuq;
			 /*tatalYHSCapital+=dataRow.getDouble("sjds_money");*/
		 }
		 if(dataRow.getInt("yuq_ts")>0){
			 totalOverdueInterest+=yuq;
			 totalYQZBJ+=(sjsh+yuq);
			 totalYQZB+=sjds;
		 }
		 
		 totalLY+=fuwum;  //总利息
	 }
	 totalYE = startmoney -totalFmoney +totalYmoney ;
	 DataRow row = new DataRow();
	 row.set("tjttotalYE", totalYE);
	 row.set("tjttotalFmoney",totalFmoney);
	 row.set("tjttotalWmoney",totalWmoney);
	 row.set("tjttotalYmoney",totalYmoney);
	 row.set("tjttotalLY",totalLY);
	 row.set("tjttotalOverdueInterest",totalOverdueInterest);
	 row.set("tjttotalCapital",totalCapital);
	 row.set("tjttotalDHSInterest",totalDHSInterest);
	 
	 /*row.set("tatalYHSCapital",df.format(tatalYHSCapital));*/
	 row.set("tjttotalDHSYQLX",totalDHSYQLX);
	 row.set("tjttotalWHSYQLX",totalWHSYQLX);
	 row.set("tjttotalYQZB",totalYQZB);
	 row.set("tjttotalYQZBJ",totalYQZBJ);
	 row.set("tjttotalDSYQZBJLX",totalDSYQZBJLX);
	 row.set("tjttotalDSYQZBJ",totalDSYQZBJ);
	 row.set("list", page);
	 row.set("temp",temp);
	 row.set("tempvalu",tempVelue);
	 row.set("hkstat",hkstat);
	 JSONObject object = JSONObject.fromBean(row);	
	 this.getWriter().write(object.toString());	
	 return null ;  
	 
}
public ActionResult doGetRecordListChg13() throws Exception {
	
	logger.info("统计图");
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
	 
	String investorId = "";
	int startmoney = jbdcms2Service.getStartMsum(investorId) ; // 起始资金
	int totalYE10 []= new int[10] ; // 账户余额
	int totalFmoney10 []= new int[10] ;  //总放款金额
	int totalWmoney10 []= new int[10] ;  //总未还款金额
	int totalYmoney10 []= new int[10] ;  //总已还款金额
	int totalCapital10 []= new int[10] ; //待回收本金
	int totalOverdueInterest10 []= new int[10] ;//逾期总利息
	int totalDHSInterest10 []= new int[10] ;//待回收利息
	/*Double tatalYHSCapital=0.0;//已回收本金
	 */		int totalLY10 []= new int[10] ;  //总利息
	 int totalDHSYQLX10 []= new int[10] ;//待回收逾期总利息
	 int totalWHSYQLX10 []= new int[10] ;//未还逾期总利息
	 int totalYQZB10 []= new int[10] ;//逾期到手金额   
	 int totalYQZBJ10 []= new int[10] ;//逾期审核金额         
	 int totalDSYQZBJLX10 []= new int[10] ;//待收逾期总本金和利息     
	 int totalDSYQZBJ10 []= new int[10] ;//待收逾期总本金     
	 
	 
	int totalYE11 []= new int[11] ; // 账户余额
	int totalFmoney11 []= new int[11] ;  //总放款金额
	int totalWmoney11 []= new int[11] ;  //总未还款金额
	int totalYmoney11 []= new int[11] ;  //总已还款金额
	int totalCapital11 []= new int[11] ; //待回收本金
	int totalOverdueInterest11 []= new int[11] ;//逾期总利息
	int totalDHSInterest11 []= new int[11] ;//待回收利息
	/*Double tatalYHSCapital=0.0;//已回收本金
	 */		int totalLY11 []= new int[11] ;  //总利息
	 int totalDHSYQLX11 []= new int[11] ;//待回收逾期总利息
	 int totalWHSYQLX11 []= new int[11] ;//未还逾期总利息
	 int totalYQZB11 []= new int[11] ;//逾期到手金额   
	 int totalYQZBJ11 []= new int[11] ;//逾期审核金额         
	 int totalDSYQZBJLX11 []= new int[11] ;//待收逾期总本金和利息     
	 int totalDSYQZBJ11 []= new int[11] ;//待收逾期总本金     
	 
	 
	    int totalYE12 []= new int[10] ; // 账户余额
		int totalFmoney12 []= new int[10] ;  //总放款金额
		int totalWmoney12 []= new int[10] ;  //总未还款金额
		int totalYmoney12 []= new int[10] ;  //总已还款金额
		int totalCapital12 []= new int[10] ; //待回收本金
		int totalOverdueInterest12 []= new int[10] ;//逾期总利息
		int totalDHSInterest12 []= new int[10] ;//待回收利息
		/*Double tatalYHSCapital=0.0;//已回收本金
		 */		int totalLY12 []= new int[10] ;  //总利息
		 int totalDHSYQLX12 []= new int[10] ;//待回收逾期总利息
		 int totalWHSYQLX12 []= new int[10] ;//未还逾期总利息
		 int totalYQZB12 []= new int[10] ;//逾期到手金额   
		 int totalYQZBJ12 []= new int[10] ;//逾期审核金额         
		 int totalDSYQZBJLX12 []= new int[10] ;//待收逾期总本金和利息     
		 int totalDSYQZBJ12 []= new int[10] ;//待收逾期总本金       
		 for(int i=0;i<10;i++){
			 List<DataRow> list=jbdcms2Service.getRecordListChg13(i+3);
			 for (DataRow dataRow : list) {
				 int sjds =Integer.parseInt(dataRow.getString("sjds_money").replace(",", ""));
				 totalFmoney11[i] +=sjds;
				 int yuq =Integer.parseInt(dataRow.getString("yuq_lx").replace(",", ""))*97/100;
				 int whyuq =Integer.parseInt(dataRow.getString("yuq_whlx").replace(",", ""))*97/100;
				 int sjsh =Integer.parseInt(dataRow.getString("sjsh_money").replace(",", ""));
				 int fuwum = yuq + (sjsh - sjds)*97/100;
				
				 if(dataRow.getString("sfyhw").equals("0")){  //该订单未还
					 totalWmoney11[i] +=(sjds + fuwum);
					 totalCapital11[i] +=sjds;
					 totalDHSInterest11[i] += fuwum ;
					 totalDHSYQLX11[i] += yuq ;			
					 if(dataRow.getInt("yuq_ts")>0){
						 
						 totalDSYQZBJ11[i] +=sjds;
						 totalDSYQZBJLX11[i] +=(sjsh+yuq);
					 }
					 
				 }
				 if(dataRow.getString("sfyhw").equals("1")){  //该订单已还
					 totalYmoney11[i] +=(sjds+fuwum);
					 totalYE11[i] += fuwum ;
					 totalWHSYQLX11[i] += whyuq;
					 /*tatalYHSCapital+=dataRow.getDouble("sjds_money");*/
				 }
				 if(dataRow.getInt("yuq_ts")>0){
					 totalOverdueInterest11[i]+=yuq;
					 totalYQZBJ11[i]+=(sjsh+yuq);
					 totalYQZB11[i]+=sjds;
				 }
				 
				 totalLY11[i]+=fuwum;  //总利息
				
			 }
			  
			 
			 for (DataRow dataRow : list) {
				 int sjds =Integer.parseInt(dataRow.getString("sjds_money").replace(",", ""));
				 totalFmoney12[i]+=sjds;
				 int yuq =Integer.parseInt(dataRow.getString("yuq_lx").replace(",", ""))*3/100;
				 int whyuq =Integer.parseInt(dataRow.getString("yuq_whlx").replace(",", ""))*3/100;
				 int sjsh =Integer.parseInt(dataRow.getString("sjsh_money").replace(",", ""));
				 int fuwum = yuq + (sjsh - sjds)*3/100;
				 
				 if(dataRow.getString("sfyhw").equals("0")){  //该订单未还
					 totalWmoney12[i]+=sjds + fuwum;
					 totalCapital12[i]+=sjds;
					 totalDHSInterest12[i]+= fuwum ;
					 totalDHSYQLX12[i] += yuq ;			
					 if(dataRow.getInt("yuq_ts")>0){
						 totalDSYQZBJ12[i]+=sjds;
						 totalDSYQZBJLX12[i]+=(sjsh+yuq);
					 }
				 }
				 if(dataRow.getString("sfyhw").equals("1")){  //该订单已还
					 totalYmoney12[i]+=(sjds+fuwum);
					 totalWHSYQLX12[i] += whyuq;
					 /*tatalYHSCapital+=dataRow.getDouble("sjds_money");*/
				 }
				 if(dataRow.getInt("yuq_ts")>0){
					 totalOverdueInterest12[i]+=yuq;
					 totalYQZBJ12[i]+=(sjsh+yuq);
					 totalYQZB12[i]+=sjds;
				 }
				 totalLY12[i]+=fuwum;  //总利息
				 totalYE12[i] = totalLY12[i] -totalDHSInterest12[i]-totalYQZB12[i] ;
			 }
			
		 
		 for (DataRow dataRow : list) {
			 int sjds =Integer.parseInt(dataRow.getString("sjds_money").replace(",", ""));
			 totalFmoney10[i]+=sjds;
			 int yuq =Integer.parseInt(dataRow.getString("yuq_lx").replace(",", ""));
			 int whyuq =Integer.parseInt(dataRow.getString("yuq_whlx").replace(",", ""));
			 int sjsh =Integer.parseInt(dataRow.getString("sjsh_money").replace(",", ""));
			 int fuwum = yuq + sjsh - sjds;
			 
			 if(dataRow.getString("sfyhw").equals("0")){  //该订单未还
				 totalWmoney10[i]+=sjds + fuwum;
				 totalCapital10[i]+=sjds;
				 totalDHSInterest10[i]+= fuwum ;
				 totalDHSYQLX10[i] += yuq ;			
				 if(dataRow.getInt("yuq_ts")>0){
					 totalDSYQZBJ10[i]+=sjds;
					 totalDSYQZBJLX10[i]+=(sjsh+yuq);
				 }
			 }
			 if(dataRow.getString("sfyhw").equals("1")){  //该订单已还
				 totalYmoney10[i]+=(sjds+fuwum);
				 totalWHSYQLX10[i] += whyuq;
				 /*tatalYHSCapital+=dataRow.getDouble("sjds_money");*/
			 }
			 if(dataRow.getInt("yuq_ts")>0){
				 totalOverdueInterest10[i]+=yuq;
				 totalYQZBJ10[i]+=(sjsh+yuq);
				 totalYQZB10[i]+=sjds;
			 }
			 totalLY10[i]+=fuwum;  //总利息
			 totalYE10[i] = totalLY10[i] -totalDHSInterest10[i]-totalYQZB10[i] ;
		 }
}

	 DataRow row = new DataRow();
	 row.set("tjttotalYE10", totalYE10);
	 row.set("tjttotalFmoney10",totalFmoney10);
	 row.set("tjttotalWmoney10",totalWmoney10);
	 row.set("tjttotalYmoney10",totalYmoney10);
	 row.set("tjttotalLY10",totalLY10);
	 row.set("tjttotalOverdueInterest10",totalOverdueInterest10);
	 row.set("tjttotalCapital10",totalCapital10);
	 row.set("tjttotalDHSInterest10",totalDHSInterest10);
	 row.set("tjttotalDHSYQLX10",totalDHSYQLX10);
	 row.set("tjttotalWHSYQLX10",totalWHSYQLX10);
	 row.set("tjttotalYQZB10",totalYQZB10);
	 row.set("tjttotalYQZBJ10",totalYQZBJ10);
	 row.set("tjttotalDSYQZBJLX10",totalDSYQZBJLX10);
	 row.set("tjttotalDSYQZBJ10",totalDSYQZBJ10);
	 
	 
	   row.set("tjttotalYE11", totalYE11);
	   row.set("tjttotalFmoney11",totalFmoney11);
	   row.set("tjttotalWmoney11",totalWmoney11);
	   row.set("tjttotalYmoney11",totalYmoney11);
	   row.set("tjttotalLY11",totalLY11);
	   row.set("tjttotalOverdueInterest11",totalOverdueInterest11);
	   row.set("tjttotalCapital11",totalCapital11);
	   row.set("tjttotalDHSInterest11",totalDHSInterest11);
	   row.set("tjttotalDHSYQLX11",totalDHSYQLX11);
	   row.set("tjttotalWHSYQLX11",totalWHSYQLX11);
	   row.set("tjttotalYQZB11",totalYQZB11);
	   row.set("tjttotalYQZBJ11",totalYQZBJ11);
	   row.set("tjttotalDSYQZBJLX11",totalDSYQZBJLX11);
	   row.set("tjttotalDSYQZBJ11",totalDSYQZBJ11);
	   
	   
	   row.set("tjttotalYE12", totalYE12);
	   row.set("tjttotalFmoney12",totalFmoney12);
	   row.set("tjttotalWmoney12",totalWmoney12);
	   row.set("tjttotalYmoney12",totalYmoney12);
	   row.set("tjttotalLY12",totalLY12);
	   row.set("tjttotalOverdueInterest12",totalOverdueInterest12);
	   row.set("tjttotalCapital12",totalCapital12);
	   row.set("tjttotalDHSInterest12",totalDHSInterest12);
	   row.set("tjttotalDHSYQLX12",totalDHSYQLX12);
	   row.set("tjttotalWHSYQLX12",totalWHSYQLX12);
	   row.set("tjttotalYQZB12",totalYQZB12);
	   row.set("tjttotalYQZBJ12",totalYQZBJ12);
	   row.set("tjttotalDSYQZBJLX12",totalDSYQZBJLX12);
	   row.set("tjttotalDSYQZBJ12",totalDSYQZBJ12);
	 JSONObject object = JSONObject.fromBean(row);	
	 this.getWriter().write(object.toString());	
	 return null ;  
	 
}
	
public ActionResult doGetRecordListChg05() throws Exception {
	
	logger.info("放款成功用户");
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
	 
	int temp = getIntParameter("temp",0);
	String  tempVelue = getStrParameter("tempvl"); 		

	String  startDate1 =getStrParameter("startDate");
	String  endDate1 =getStrParameter("endDate");
	String startDate="";
	String endDate="";
	if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
	String[] sourceStrArray1 = startDate1.split("-");
	String[] sourceStrArray2 = endDate1.split("-");
    startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
    endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
	}
	String hkstat = getStrParameter("hkstat");       
	//审核状态
	String commit = getStrParameter("commit");
	//定义用户选择条件
	//JSONObject jsonObject = new JSONObject();
	int user_id = SessionHelper.getInt("cmsuserid", getSession());   //操作员ID
	if(user_id == 0){	   
	  	  jsonObject.put("err", -1);		  
		  this.getWriter().write(jsonObject.toString());	
		  return null;
	    }
	String namecm = jbdcms2Service.getNameCms(user_id);
	String investorId = ""+user_id;
	String investorName =namecm ;
	String userId ="" ;
	String realName="";
	String phone = "" ;	
	String idCard ="" ;
	logger.info("temp:"+temp+"startDate:"+startDate+" endDate"+endDate);
	if(temp ==1){
	   	
		userId =tempVelue ;
	}
	
	if(temp ==2){
				
		realName =tempVelue ;
	}
	if(temp ==3) {
				
		phone = tempVelue ;
	}
	if(temp ==4) {
		
		idCard = tempVelue ;
	}
	//默认第一页
	int curPage  =getIntParameter("curPage",1);	
    DBPage page = jbdcms2Service.getRecordListChg05(curPage, 15, userId, realName, phone, startDate, endDate, commit, idCard, hkstat, investorId, investorName);
	List<DataRow> list=jbdcms2Service.getRecordListChg05(userId, realName, phone, startDate, endDate, commit, idCard, hkstat, investorId, investorName);
	DecimalFormat df = new DecimalFormat( "0.00"); //设置double类型小数点后位数格式 
	Integer startmoney = jbdcms2Service.getStartM(investorId) ; // 账户起始资金
	Double totalWmoney=0.0;  //总未还款金额
	Double totalCapital=0.0; //待回收本金
	Double totalOverdueInterest=0.0;//逾期总利息
	Double totalDHSInterest=0.0;//待回收利息
	/*Double tatalYHSCapital=0.0;//已回收本金
*/		Double totalLY=0.0;  //总利息
	Double totalDHSYQLX =0.0 ;//待回收逾期总利息
	Double totalYQZB = 0.0 ;//逾期到手金额   
	Double totalYQZBJ = 0.0 ;//逾期审核金额             
	Double totalYE = 0.0 ; //账户余额
	Double totalFmoney=0.0;  //总放款金额
	Double totalYmoney=0.0;  //总已还款金额
	Double totalDSYQZBJ = 0.0 ;//逾期总本金        
	for (DataRow dataRow : list) {
		totalFmoney+=dataRow.getDouble("sjds_money");
		Double fuwum=Double.valueOf(df.format(dataRow.getDouble("yuq_lx")+dataRow.getDouble("sjsh_money")-dataRow.getDouble("sjds_money")));
		if(dataRow.getString("sfyhw").equals("0")){  //该订单未还
			totalWmoney+=(dataRow.getDouble("sjds_money")+fuwum);
			totalCapital+=dataRow.getDouble("sjds_money");
			totalDHSInterest+=fuwum;
			totalDHSYQLX +=dataRow.getDouble("yuq_lx");			
			if(dataRow.getInt("yuq_ts")>0){
				
				totalDSYQZBJ+=dataRow.getDouble("sjsh_money");
			}
			
		}
		if(dataRow.getString("sfyhw").equals("1")){  //该订单已还
			totalYmoney+=(dataRow.getDouble("sjds_money")+fuwum);
			/*tatalYHSCapital+=dataRow.getDouble("sjds_money");*/
		}
		if(dataRow.getInt("yuq_ts")>0){
			totalOverdueInterest+=dataRow.getDouble("yuq_lx");
			totalYQZBJ+=dataRow.getDouble("sjsh_money");
			totalYQZB+=dataRow.getDouble("sjds_money");
		}
	totalLY+=fuwum;  //总利息
	}
	totalYE = startmoney - totalFmoney + totalYmoney;
	DataRow row = new DataRow();
	row.set("totalYE", df.format(totalYE));
	row.set("totalFmoney", df.format(totalFmoney));
	row.set("totalWmoney", df.format(totalWmoney));
	row.set("totalYmoney", df.format(totalYmoney));
	row.set("totalLY", df.format(totalLY));
	row.set("totalOverdueInterest", df.format(totalOverdueInterest));
	row.set("totalCapital", df.format(totalCapital));
	row.set("totalDHSInterest",df.format(totalDHSInterest));
	
	/*row.set("tatalYHSCapital",df.format(tatalYHSCapital));*/
	row.set("totalDHSYQLX",df.format(totalDHSYQLX));
	row.set("totalYQZB",df.format(totalYQZB));
	row.set("totalYQZBJ",df.format(totalYQZBJ));
	row.set("totalDSYQZBJ",df.format(totalDSYQZBJ));
	row.set("list", page);
	row.set("temp",temp);
	row.set("tempvalu",tempVelue);
	row.set("hkstat",hkstat);
	JSONObject object = JSONObject.fromBean(row);
	this.getWriter().write(object.toString());	
	return null ;  
	
}
//用户
public ActionResult doGetRecordListChg06() throws Exception {
	
	logger.info("放款统计数据");
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
	int temp = getIntParameter("temp",0);	

	String  startDate1 =getStrParameter("startDate");
	String  endDate1 =getStrParameter("endDate");
	String startDate="";
	String endDate="";
	if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
	String[] sourceStrArray1 = startDate1.split("-");
	String[] sourceStrArray2 = endDate1.split("-");
    startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
    endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
	}
	//审核状态
	String commit = getStrParameter("commit");
	//定义用户选择条件
	//JSONObject jsonObject = new JSONObject();
	int user_id = SessionHelper.getInt("cmsuserid", getSession());   //操作员ID
	if(user_id == 0){	   
	  	  jsonObject.put("err", -1);		  
		  this.getWriter().write(jsonObject.toString());	
		  return null;
	    }
	String namecm = jbdcms2Service.getNameCms(user_id);
	String investorId = ""+user_id;
	String investorName =namecm ;
	logger.info("temp:"+temp+"startDate:"+startDate+" endDate"+endDate);
	List<DataRow> list=jbdcms2Service.getRecordListChg04(startDate,endDate,commit,investorId ,investorName);
	DecimalFormat df = new DecimalFormat( "0.00"); //设置double类型小数点后位数格式 
	Integer startmoney = jbdcms2Service.getStartM(investorId) ; // 起始资金
	Double totalYEs = 0.0 ; //账户余额
	Double totalFmoneys=0.0;  //总放款金额
	Double tatalYHSCapitals=0.0;//已回收本金
	Double m = 0.0; //净收益
	Double totalYmoneys=0.0;  //总已还款金额
     
	Double totalDSYQZBJs = 0.0 ;//逾期总本金        
	Double b = 0.0;
	Double y =0.0;
	for (DataRow dataRow : list) {
		totalFmoneys+=dataRow.getDouble("sjds_money");
		Double fuwum=Double.valueOf(df.format(dataRow.getDouble("yuq_lx")+dataRow.getDouble("sjsh_money")-dataRow.getDouble("sjds_money")));
		if(dataRow.getString("sfyhw").equals("0")){  //该订单未还
						
			if(dataRow.getInt("yuq_ts")>0){
				
				totalDSYQZBJs+=dataRow.getDouble("sjds_money");
			}
			
		}
		if(dataRow.getString("sfyhw").equals("1")){  //该订单已还
			totalYmoneys+=(dataRow.getDouble("sjds_money")+fuwum);
			tatalYHSCapitals+=dataRow.getDouble("sjds_money");
		}
	}
	y = 100 * totalDSYQZBJs / (tatalYHSCapitals + totalDSYQZBJs)  ;
	m = totalYmoneys - tatalYHSCapitals;
	b = 100 * (m-totalDSYQZBJs) / (totalYmoneys-m);
	totalYEs = startmoney - totalFmoneys + totalYmoneys;
	JSONObject object = new JSONObject();
	object.put("totalYEs", df.format(totalYEs));
	object.put("totalFmoneys", df.format(totalFmoneys));
	object.put("totalYmoneys", df.format(totalYmoneys));
	object.put("totalDSYQZBJs", df.format(totalDSYQZBJs));
	object.put("b", df.format(b));
	object.put("y", df.format(y));
	object.put("m", df.format(m));
	this.getWriter().write(object.toString());	
	return null ;  
	
}
//系统
public ActionResult doGetRecordListChg04() throws Exception {
	
	logger.info("放款成功用户");
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
	 
	int temp = getIntParameter("temp",0);
	String  tempVelue = getStrParameter("tempvl"); 		

	String  startDate1 =getStrParameter("startDate");
	String  endDate1 =getStrParameter("endDate");
	String startDate="";
	String endDate="";
	if(!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)){
	String[] sourceStrArray1 = startDate1.split("-");
	String[] sourceStrArray2 = endDate1.split("-");
    startDate = sourceStrArray1[2]+"-"+sourceStrArray1[1]+"-"+sourceStrArray1[0];
    endDate = sourceStrArray2[2]+"-"+sourceStrArray2[1]+"-"+sourceStrArray2[0];
	}
	//审核状态
	String commit = getStrParameter("commit");
	//定义用户选择条件
	int user_id = SessionHelper.getInt("cmsuserid", getSession());   //操作员ID
	 String namecm = jbdcms2Service.getNameCms(user_id);
	String investorId = "";
	String investorName ="" ;
	logger.info("temp:"+temp+"startDate:"+startDate+" endDate"+endDate);
	int startmoney = jbdcms2Service.getStartMsum(investorId) ; // 起始资金
	if(temp ==5) {
		
		investorId =tempVelue ;
		startmoney = jbdcms2Service.getStartM(investorId) ; // 起始资金
	}
    if(temp ==6) {
		
		investorName =tempVelue ;
		startmoney = jbdcms2Service.getStartMN(investorName) ; // 起始资金
	}
	List<DataRow> list=jbdcms2Service.getRecordListChg04(startDate,endDate,commit,investorId ,investorName);
	DecimalFormat df = new DecimalFormat( "0.00"); //设置double类型小数点后位数格式 
	Double totalYE = 0.0 ; //账户余额
	Double totalFmoney=0.0;  //总放款金额
	Double tatalYHSCapital=0.0;//已回收本金
	Double m = 0.0; //净收益
	Double totalYmoney=0.0;  //总已还款金额
       
	Double totalDSYQZBJ = 0.0 ;//逾期总本金        
	Double b = 0.0;  //收益率
	Double y = 0.0; //逾期率
	for (DataRow dataRow : list) {
		totalFmoney+=dataRow.getDouble("sjds_money");
		Double fuwum=Double.valueOf(df.format(dataRow.getDouble("yuq_lx")+dataRow.getDouble("sjsh_money")-dataRow.getDouble("sjds_money")));
		if(dataRow.getString("sfyhw").equals("0")){  //该订单未还
						
			if(dataRow.getInt("yuq_ts")>0){
				
				totalDSYQZBJ+=dataRow.getDouble("sjds_money");
			}
			
		}
		if(dataRow.getString("sfyhw").equals("1")){  //该订单已还
			totalYmoney+=(dataRow.getDouble("sjds_money")+fuwum);
			tatalYHSCapital+=dataRow.getDouble("sjds_money");
		}
	}
	y = 100 * totalDSYQZBJ / (tatalYHSCapital + totalDSYQZBJ) ;
	m = totalYmoney - tatalYHSCapital;
	b = 100 * (m-totalDSYQZBJ) / (totalYmoney-m);
	totalYE = startmoney - totalFmoney + totalYmoney;
	JSONObject object = new JSONObject();
	object.put("totalYE", df.format(totalYE));
	object.put("totalFmoney", df.format(totalFmoney));
	object.put("totalYmoney", df.format(totalYmoney));
	object.put("totalDSYQZBJ", df.format(totalDSYQZBJ));
	object.put("b", df.format(b));
	object.put("y", df.format(y));
	object.put("m", df.format(m));
	this.getWriter().write(object.toString());	
	return null ;  
	
}


/** 2020年2月23日
 * 添加审核通讯录页面FaceBook 备注
 * AddSHBeizfb
 */
public  ActionResult doAddSHBeizfb() throws Exception {
	  logger.info("添加通讯录页面FaceBook 备注");	
	  JSONObject jsonObject = new JSONObject(); // 后台登录账户
      int cmsuser_id =SessionHelper.getInt("cmsuserid", getSession());
      cmsuser_id =accessVeritifivationbase.checkCMSidAndip(cmsuser_id, getipAddr());
      if(cmsuser_id==0){
			jsonObject.put("error", -1);
			jsonObject.put("msg", "Vui lòng đăng nhập trước");
			this.getWriter().write(jsonObject.toString());	
			return null;		
	  }
	 logger.info("请求ID:" + cmsuser_id);
	
     int rec_id = getIntParameter("rec_id",0);//借款ID
     int user_id=getIntParameter("user_id",0);
     String remark=getStrParameter("remark","");  //备注信息
     if(remark != "" || remark != null){
		   remark = remark.replace("&nbsp;", " ");
	 }

	 if(user_id ==0){
		   jsonObject.put("error", -1);
	  	   jsonObject.put("msg", "User ID is Invalid");
		  this.getWriter().write(jsonObject.toString());	
		  return null;				
	 }
     if(rec_id ==0){
	     jsonObject.put("error", -2);
 	     jsonObject.put("msg", "Borrowing does not exist");
	     this.getWriter().write(jsonObject.toString());	
	     return null;				
      }	 

     //添加备注
       Date date=new Date();
	   DateFormat format=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	   String time=format.format(date);
	   DataRow row7=  new DataRow();	
	   row7.set("userid",user_id);	
	   row7.set("msg" ,remark);
	   row7.set("msgtype","fecebook_bz");
	   row7.set("jkid",rec_id);//项目id
	   row7.set("create_time", new Date());
	   row7.set("cl_ren",cmsuser_id);
	   row7.set("cszt",0);
	   jbdcms2Service.insertUserChMsg(row7);
	   
	   DataRow row8=  new DataRow();	
	   row8.set("user_id",user_id);	
	   row8.set("content" ,remark);
	   row8.set("jkjl_id",rec_id );
	   row8.set("kefuid",cmsuser_id);
	   row8.set("state",1);
	   row8.set("code",8);
	   row8.set("visitdate", time);
	   jbdcms2Service.insertUserKFMsg(row8);

	  jsonObject.put("error", 1);
	  jsonObject.put("msg", "add success");
	  this.getWriter().write(jsonObject.toString());	
      return null ;
   }
}
