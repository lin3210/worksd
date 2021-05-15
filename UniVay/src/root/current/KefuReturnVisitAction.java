package root.current;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;
import root.role.roleAuthorityMangement;

import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;


import com.project.service.account.KefuReturnVisitService;
import com.thinkive.base.jdbc.DBPage;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.util.SessionHelper;
import com.thinkive.web.base.ActionResult;
import com.thinkive.web.base.BaseAction;

public class KefuReturnVisitAction extends BaseAction{
	
	private static Logger logger=Logger.getLogger(KefuReturnVisitAction.class);
	private static KefuReturnVisitService kefuReturnVisitService=new KefuReturnVisitService();
	private static roleAuthorityMangement roleauthoritymangement  = new roleAuthorityMangement();
	
	/**
	 * 得到需要回访的客户
	 * @return
	 * @throws Exception 
	 */
	public ActionResult doGetReturnVisit() throws Exception{
		logger.info("进入查询需要回访的用户");
    	int temp = getIntParameter("temp",0);
		String  tempVelue = getStrParameter("tempvl"); 		
		String  startDate =getStrParameter("startDate");
		String  endDate =getStrParameter("endDate");
		//审核状态
		String commit = getStrParameter("commit");
		//定义用户选择条件
		if(!startDate.equals("")&&!endDate.equals("")){
			if(startDate.endsWith(endDate)){
				startDate+=" 00:00:00";
				endDate+=" 23:59:59";
			}
		}
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
		DBPage page = kefuReturnVisitService.getReturnVisitList(curPage,15,userId,realName,phone,startDate,endDate,commit,idCard);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		row.set("commit ",commit);
		JSONObject object = JSONObject.fromBean(row);			
		this.getWriter().write(object.toString());
    	return null ;
	}
	/**
	 * 得到查找的客户
	 * @return
	 * @throws Exception 
	 */
	public ActionResult doGetUserVisit() throws Exception{
		logger.info("进入查询的用户");
		JSONObject jsonObject = new JSONObject();	
		int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());//后台登录账户
		 if(cmsuserid == 0){	    		
		   jsonObject.put("error", -1);
		   jsonObject.put("msg", "请先登录"); //
		   this.getWriter().write(jsonObject.toString());	
		   return null;
		 }
		int chaxuncs = kefuReturnVisitService.getKefuCXCS(cmsuserid);
		
		DateFormat format=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	
    	int temp = getIntParameter("temp",0);
		String  tempVelue = getStrParameter("tempvl"); 		
		String  startDate =getStrParameter("startDate");
		String  endDate =getStrParameter("endDate");
		//审核状态
		String commit = getStrParameter("commit");
		//定义用户选择条件
		if(!startDate.equals("")&&!endDate.equals("")){
			if(startDate.endsWith(endDate)){
				startDate+=" 00:00:00";
				endDate+=" 23:59:59";
			}
		}
		String userID = "" ;	
		String phone = "" ;	
		String idno = "" ;	
		if(temp == 2){
			userID = tempVelue ;
			DataRow row = new DataRow();
			row.set("phone", "YH"+userID);
			row.set("cmsuserid", cmsuserid);
			row.set("createtime", format.format(new Date()));
			kefuReturnVisitService.InsertUserSelect(row);
		}
		if(temp ==3) {
					
			phone = tempVelue ;
			
			DataRow row = new DataRow();
			row.set("phone", phone);
			row.set("cmsuserid", cmsuserid);
			row.set("createtime", format.format(new Date()));
			kefuReturnVisitService.InsertUserSelect(row);
			
		}	
		if(temp ==4) {
			
			idno = tempVelue ;
			
			DataRow row = new DataRow();
			row.set("idno", idno);
			row.set("cmsuserid", cmsuserid);
			row.set("createtime", format.format(new Date()));
			kefuReturnVisitService.InsertUserSelect(row);
		}
		
		//默认第一页
		int curPage  =getIntParameter("curPage",1);
		DataRow row = new DataRow();
		DataRow row1 = kefuReturnVisitService.getUserList(userID,phone,idno);
		if(row1 != null){
			row.set("row1", row1);
		}else {
			row.set("row1", "");
			row.set("msg", "Không tìm thấy khách hàng");//row.set("msg", "没有找到该用户");
			JSONObject object = JSONObject.fromBean(row);			
			this.getWriter().write(object.toString());
	    	return null ;
		}
		int userid  = row1.getInt("id");
		String realname = kefuReturnVisitService.getUserName(userid);
		DataRow jkxx = kefuReturnVisitService.getUserJKList(userid);
		String name = kefuReturnVisitService.getName(userid);
		
		if(jkxx != null){
			row.set("jkxx", jkxx);
		}else{
			row.set("jkxx", "");
		}
		
		DataRow row2 = new DataRow();
		row2.set("user_id", cmsuserid);
		row2.set("kfcxcs", chaxuncs+1);
		kefuReturnVisitService.UpdateKFCX(row2);
		row.set("realname", realname);
		row.set("userid", userid);
		
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		row.set("commit ",commit);
		row.set("username", name);
		JSONObject object = JSONObject.fromBean(row);			
		this.getWriter().write(object.toString());
    	return null ;
	}

	/**
	 * 得到需要回访的客户
	 * @return
	 * @throws Exception 
	 */
	public ActionResult doGetReturnVisitSB() throws Exception{
		logger.info("进入放款失败用户");
    	int temp = getIntParameter("temp",0);
		String  tempVelue = getStrParameter("tempvl"); 		
		String  startDate =getStrParameter("startDate");
		String  endDate =getStrParameter("endDate");
		//审核状态
		String commit = getStrParameter("commit");
		//定义用户选择条件
		if(!startDate.equals("")&&!endDate.equals("")){
			if(startDate.endsWith(endDate)){
				startDate+=" 00:00:00";
				endDate+=" 23:59:59";
			}
		}
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
		DBPage page = kefuReturnVisitService.getReturnVisitListSB(curPage,15,userId,realName,phone,startDate,endDate,commit,idCard);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		row.set("commit ",commit);
		JSONObject object = JSONObject.fromBean(row);			
		this.getWriter().write(object.toString());
    	return null ;
	}
	/**
	 * 得到需要回访的客户
	 * @return
	 * @throws Exception 
	 */
	public ActionResult doGetReturnVisitSBHK() throws Exception{
		logger.info("进入还款失败用户");
		 JSONObject jsonObject = new JSONObject();
	    int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());   //操作员ID
	    if(cmsuserid == 0){	   
	  	  jsonObject.put("err", -1);
	  	  jsonObject.put("msg", "Vui lòng đăng nhập trước");//请先登录
		  this.getWriter().write(jsonObject.toString());	
		  return null;
	    }
    	int temp = getIntParameter("temp",0);
		String  tempVelue = getStrParameter("tempvl"); 		
		String  startDate =getStrParameter("startDate");
		String  endDate =getStrParameter("endDate");
		//审核状态
		String commit = getStrParameter("commit");
		//定义用户选择条件
		if(!startDate.equals("")&&!endDate.equals("")){
			if(startDate.endsWith(endDate)){
				startDate+=" 00:00:00";
				endDate+=" 23:59:59";
			}
		}
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
		int code = 0;
//		if(cmsuserid == 8 || cmsuserid == 6 || cmsuserid == 888 || cmsuserid == 222){
		if(roleauthoritymangement.getRoleAM_SHlist(cmsuserid+"")){
			code = 1;
		}
		int curPage  =getIntParameter("curPage",1);	
		DBPage page = kefuReturnVisitService.getReturnVisitListSBHK(curPage,15,code,cmsuserid,userId,realName,phone,startDate,endDate,commit,idCard);
		
		List<DataRow> list  = kefuReturnVisitService.getReturnVisitListSBHK(code,cmsuserid,userId,realName,phone,startDate,endDate,commit,idCard);
		String remark[] = new String[list.size()];
		for (int i=0;i<list.size();i++) {
			DataRow datarow = new DataRow();
			datarow = list.get(i);
			remark[i] = kefuReturnVisitService.getContent(datarow.getInt("jkid"));
		}
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("datarow", remark);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		row.set("commit ",commit);
		JSONObject object = JSONObject.fromBean(row);			
		this.getWriter().write(object.toString());
    	return null ;
	}
	/**
	 * 得到需要回访的客户
	 * @return
	 * @throws Exception 
	 */
	public ActionResult doGetReturnVisitSBALL() throws Exception{
		logger.info("进入放款失败用户");
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
		if(!startDate.equals("")&&!endDate.equals("")){
			if(startDate.endsWith(endDate)){
				startDate+=" 00:00:00";
				endDate+=" 23:59:59";
			}
		}
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
		DBPage page = kefuReturnVisitService.getReturnVisitListSBALL(curPage,15,userId,realName,phone,startDate,endDate,commit,idCard);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		row.set("commit ",commit);
		JSONObject object = JSONObject.fromBean(row);			
		this.getWriter().write(object.toString());
    	return null ;
	}
	/**
	 * 得到需要回访的客户
	 * @return
	 * @throws Exception 
	 */
	public ActionResult doGetReturnVisitCG() throws Exception{
		logger.info("进入放款成功用户");
    	int temp = getIntParameter("temp",0);
		String  tempVelue = getStrParameter("tempvl"); 		
		String  startDate =getStrParameter("startDate");
		String  endDate =getStrParameter("endDate");
		//审核状态
		String commit = getStrParameter("commit");
		//定义用户选择条件
		if(!startDate.equals("")&&!endDate.equals("")){
			if(startDate.endsWith(endDate)){
				startDate+=" 00:00:00";
				endDate+=" 23:59:59";
			}
		}
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
		//默认第一页
		int curPage  =getIntParameter("curPage",1);	
		DBPage page = kefuReturnVisitService.getReturnVisitListCG(curPage,15,userId,realName,phone,startDate,endDate,commit,idCard);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		row.set("commit ",commit);
		JSONObject object = JSONObject.fromBean(row);			
		this.getWriter().write(object.toString());
    	return null ;
	}
	/**
	 * 得到已回访的客户
	 * @return
	 * @throws Exception 
	 */
	public ActionResult doGetReturnVisited() throws Exception{
		logger.info("进入查询需要回访的用户");
    	int temp = getIntParameter("temp",0);
		String  tempVelue = getStrParameter("tempvl"); 		
		String  startDate =getStrParameter("startDate");
		String  endDate =getStrParameter("endDate");
		Integer state=getIntParameter("select");
		if(!startDate.equals("")&&!endDate.equals("")){
			if(startDate.endsWith(endDate)){
				startDate+=" 00:00:00";
				endDate+=" 23:59:59";
			}
		}
		
		//审核状态
		//String commit = getStrParameter("commit");
		//定义用户选择条件
		String userId ="" ;
		String realName="";
		String phone = "" ;	
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
		//默认第一页
		int curPage  =getIntParameter("curPage",1);	
		DBPage page = kefuReturnVisitService.getAllVisited(curPage, 15, userId, realName, phone, startDate, endDate, state);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		//row.set("commit ",commit);
		JSONObject object = JSONObject.fromBean(row);			
		this.getWriter().write(object.toString());
    	return null ;
	}
	/**
	 * 得到已回访的客户
	 * @return
	 * @throws Exception 
	 */
	public ActionResult doGetALLReturnVisited() throws Exception{
		logger.info("进入查询所有已经回访的用户");
    	int temp = getIntParameter("temp",0);
    	int temp1 = getIntParameter("temp1",0);
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
		//String commit = getStrParameter("commit");
		//定义用户选择条件
		String userId ="" ;
		String realName="";
		logger.info("temp:"+temp+"startDate:"+startDate+" endDate"+endDate);
		if(temp ==1){
				   	
			userId =tempVelue ;
		}
		
		if(temp ==2){
					
			realName =tempVelue ;
		}
		//默认第一页
		int curPage  =getIntParameter("curPage",1);	
		DBPage page = kefuReturnVisitService.getAllReturnVisited(curPage, 15, userId, realName,temp1, startDate, endDate);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp",temp);
		row.set("temp1",temp1);
		row.set("tempvalu",tempVelue);
		//row.set("commit ",commit);
		JSONObject object = JSONObject.fromBean(row);			
		this.getWriter().write(object.toString());
    	return null ;
	}
	/**
	 * 增加回访记录
	 * @return
	 */
	public ActionResult doAddReturnVisit(){
		int jkid=getIntParameter("reid");
		//int userid=getIntParameter("userid");
		JSONObject json = new JSONObject();
		 int cmsid = SessionHelper.getInt("cmsuserid", getSession());
		 if(cmsid == 0){	    		
		   json.put("error", 1);
		   json.put("msg", "请先登录");
		   this.getWriter().write(json.toString());	
		   return null;
		 }
		String remark=getStrParameter("remark").replace("&nbsp;", " ");
		int state=getIntParameter("state");
		Date date=new Date();
		DateFormat format=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String userid = kefuReturnVisitService.getUserid(jkid);
		String time=format.format(date);
		DataRow dr=new DataRow();
		dr.set("jkjl_id",jkid );
		dr.set("user_id",userid );
		dr.set("kefuid",cmsid );
		dr.set("state",state );
		dr.set("code",5);
		dr.set("visitdate", time);
		dr.set("content",remark );
		int id=kefuReturnVisitService.addReturnVisit("sd_returnvisit", dr);
		if(id!=0){
			Boolean bool=kefuReturnVisitService.updateKfhf_kfid(cmsid, jkid);
			if(bool!=true){
				json.put("msg", "添加失败");
			}else{
				json.put("msg", "添加成功");
			}
		}
		this.getWriter().write(json.toString());
		return null;
	}
	/**
	 * 增加放款失败回访记录
	 * @return
	 */
	public ActionResult doAddReturnVisitFKSB()  throws Exception {
		JSONObject json = new JSONObject();
		 int cmsid = SessionHelper.getInt("cmsuserid", getSession());
		 if(cmsid == 0){	    		
 		   json.put("error", 1);
		   json.put("msg", "Vui lòng Đăng nhập");
		   this.getWriter().write(json.toString());	
		   return null;
		 }
		int jkid=getIntParameter("reid");
		String cardname=getStrParameter("cardname");
		String cardnum=getStrParameter("cardnum");
		String remark=getStrParameter("remark").replace("&nbsp;", " ");
		int state=getIntParameter("state");
		String cl03yj = kefuReturnVisitService.getKefu(jkid);
		if("Bankcard Error".equals(cl03yj)){
			json.put("error", -1);
			json.put("msg", "Vui lòng làm mới");
		}
		try {
			
			String userid = kefuReturnVisitService.getUserid(jkid);
			Date date=new Date();
			DateFormat format=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			String time=format.format(date);
			DataRow dr=new DataRow();
			dr.set("jkjl_id",jkid );
			dr.set("user_id",userid );
			dr.set("kefuid",cmsid );
			dr.set("state",state );
			dr.set("code",2);
			dr.set("visitdate", time);
			dr.set("content",remark );
			int id=kefuReturnVisitService.addReturnVisit("sd_returnvisit", dr);
			DataRow row3 =  new DataRow();	
			row3.set("userid",userid);
		    row3.set("title", "Thông báo chấp nhận đề nghị vay");//审核通知
		    row3.set("neirong","Do thẻ ngân hàng của bạn có vấn đề nên chuyển tiền thất bại. Vui lòng điền lại thông tin rồi đề xuất vay lại.");	//由于你的银行卡有问题而导致转账失败，请重新填写以后再次提交借款申请	             
		    row3.set("fb_time", time);
			kefuReturnVisitService.InsertMsg(row3);
			DataRow row1=new DataRow();
			row1.set("id", jkid);
			row1.set("sfyfk", 0);
			row1.set("cl03_status", 3);
			row1.set("cl03_yj", "Bankcard Error");
			row1.set("cl03_time", time);
			kefuReturnVisitService.UpdateJK(row1);
			DataRow row2=new DataRow();
			row2.set("id", userid);
			row2.set("yhbd", 0);
			kefuReturnVisitService.UpdateYHBD(row2);
			if(id!=0){
				Boolean bool=kefuReturnVisitService.updateKfhf_kfid(cmsid, jkid);
				if(bool!=true){
					json.put("msg", "Thất bại");
				}else{
					json.put("msg", "Thành công");
				}
			}
			this.getWriter().write(json.toString());
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 增加还款失败回访记录
	 * @return
	 */
	public ActionResult doAddReturnVisitHKSB()  throws Exception {
		JSONObject json = new JSONObject();
		int cmsid = SessionHelper.getInt("cmsuserid", getSession());
		if(cmsid == 0){	    		
			json.put("error", 1);
			json.put("msg", "Vui lòng Đăng nhập");
			this.getWriter().write(json.toString());	
			return null;
		}
		int jkid=getIntParameter("reid");
		String cardname=getStrParameter("cardname");
		String cardnum=getStrParameter("cardnum");
		String remark=getStrParameter("remark").replace("&nbsp;", " ");
		int state=getIntParameter("state");
		int hkqd = kefuReturnVisitService.getKefuHKQD(jkid);
		if(hkqd == 1){
			json.put("error", -1);
			json.put("msg", "Vui lòng làm mới");
		}
		try {
			
			String userid = kefuReturnVisitService.getUserid(jkid);
			Date date=new Date();
			DateFormat format=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			String time=format.format(date);
			DataRow dr=new DataRow();
			dr.set("jkjl_id",jkid );
			dr.set("user_id",userid );
			dr.set("kefuid",cmsid );
			dr.set("state",state );
			dr.set("code",4);
			dr.set("visitdate", time);
			dr.set("content",remark );
			int id=kefuReturnVisitService.addReturnVisit("sd_returnvisit", dr);
			DataRow row3 =  new DataRow();	
			row3.set("userid",userid);
			row3.set("title", "Thông báo chấp nhận đề nghị vay");//审核通知
			row3.set("neirong","Ủy nhiệm chi không chính xác, vui lòng đăng lại ủy nhiệm chi.");	//由于还款凭证错误，请重新提交还款凭证	             
			row3.set("fb_time", time);
			kefuReturnVisitService.InsertMsg(row3);
			DataRow row1=new DataRow();
			row1.set("id", jkid);
			row1.set("hkqd", 0);
			row1.set("tzjx", 0);
			row1.set("hksb", 1);
			kefuReturnVisitService.UpdateJK(row1);
			if(id!=0){
				Boolean bool=kefuReturnVisitService.updateKfhf_kfid(cmsid, jkid);
				if(bool!=true){
					json.put("msg", "Thất bại");
				}else{
					json.put("msg", "Thành công");
				}
			}
			this.getWriter().write(json.toString());
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 增加放款失败回访记录
	 * @return
	 */
	public ActionResult doAddReturnVisitFKCG()  throws Exception {
		JSONObject json = new JSONObject();
		 int cmsid = SessionHelper.getInt("cmsuserid", getSession());
		 if(cmsid == 0){	    		
 		   json.put("error", 1);
		   json.put("msg", "Vui lòng Đăng nhập");
		   this.getWriter().write(json.toString());	
		   return null;
		 }
		int jkid=getIntParameter("reid");
		String remark=getStrParameter("remark").replace("&nbsp;", " ");
		int state=getIntParameter("state");
		String kehf = kefuReturnVisitService.getKefuCG(jkid);
		if(!kehf.equals("0")){
			json.put("error", -1);
			json.put("msg", "Vui lòng làm mới");
		}
		try {
			
			String userid = kefuReturnVisitService.getUserid(jkid);
			Date date=new Date();
			DateFormat format=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			String time=format.format(date);
			DataRow dr=new DataRow();
			dr.set("jkjl_id",jkid );
			dr.set("user_id",userid );
			dr.set("kefuid",cmsid );
			dr.set("state",state );
			dr.set("code",3);
			dr.set("visitdate", time);
			dr.set("content",remark );
			int id=kefuReturnVisitService.addReturnVisit("sd_returnvisit", dr);
			
			if(id!=0){
				Boolean bool=kefuReturnVisitService.updateKfhf_cgfk(cmsid, jkid);
				if(bool!=true){
					json.put("msg", "添加失败");
				}else{
					json.put("msg", "添加成功");
				}
			}
			this.getWriter().write(json.toString());
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
