package root.role;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.project.constant.IConstants;
import com.project.service.role.UserService;
import com.shove.security.Encrypt;
import com.thinkive.base.jdbc.DBPage;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.util.SessionHelper;
import com.thinkive.web.base.ActionResult;
import com.thinkive.web.base.BaseAction;

public class UserAction extends BaseAction{
	private static Logger logger = Logger.getLogger(UserAction.class);
	private static UserService userService=new UserService();
	private static roleAuthorityMangement roleauthoritymangement  = new roleAuthorityMangement();
	
	/**
	 * 查询后台所有用户
	 * @return
	 */
	public ActionResult doOLAVAGetUserList(){
		logger.info("进入用户信息列表");
		int temp = getIntParameter("temp",0);
		String  tempVelue = getStrParameter("tempvl"); 	
		//默认设置为查询所有
		int type=getIntParameter("type",0);
		//默认第一页
		int curPage  =getIntParameter("curPage",1);	
		DBPage page = userService.selectUserList(curPage, 15,type,temp,tempVelue); 
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
 		return null;
	}
	
	/**
	 * 查询所有没有角色的用户
	 * @return
	 */
	public ActionResult doGetNoUserList(){
		logger.info("进入查询所有没有角色的用户");	
		List<DataRow> list=userService.selectNoUserList();
		JSONArray isArray=JSONArray.fromObject(list);
		this.getWriter().write(isArray.toString());
		return null;
	}
	
	/**
	 * 根据用户角色查找用户
	 * @return
	 */
	public ActionResult doGetUserByRole(){
		int temp = getIntParameter("temp",0);
		String  tempVelue = getStrParameter("tempvl"); 		
		//默认第一页
		int curPage  =getIntParameter("curPage",1);	
		int roleid=getIntParameter("role_id");
		DBPage page = userService.selectUserByRole(curPage, 30, roleid); 
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
		return null;
	}

	/**
	 * 根据用户ID删除角色表里的成员
	 * @return
	 */
	public ActionResult doUpdateUserById(){
		int userid=getIntParameter("user_id");
		int roleId=getIntParameter("role_id");
		Boolean bool=userService.updateUserFromRole(roleId, userid);
		JSONObject jsonObject = new JSONObject();
		if(bool==false){
			jsonObject.put("data", 1);
		}
		this.getWriter().write(jsonObject.toString());
		return null;
	}
	
	/**
	 * 新增一个用户
	 * @return
	 */
	public ActionResult doOLAVAAddUser(){
		String resPWD = Encrypt.MD5(getStrParameter("password") + IConstants.PASS_KEY);
		Date date=new Date();
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time=format.format(date);
		DataRow dr=new DataRow();
		dr.set("NAME", getStrParameter("user_name"));
		dr.set("PASSWORD",resPWD);
		dr.set("PHONE", getStrParameter("phone"));
		dr.set("USER_ID", getStrParameter("bh"));
		dr.set("state", getIntParameter("state"));
		dr.set("IS_SYSTEM", 0);
		dr.set("LOGIN_TIMES", 0);
		dr.set("ROLEID", 100);
		dr.set("SITENO", "main");
		dr.set("CREATE_DATE",time);
		Boolean bool=userService.addUser("sdcms_user", dr);
		JSONObject jsonObject = new JSONObject();
		if(bool==false){
			jsonObject.put("data", 1);
		}
		this.getWriter().write(jsonObject.toString());
		return null;
	}
	
	/**
	 * 修改用户
	 * @return
	 */
	public ActionResult doOLAVAUpdateUser(){
		String name=getStrParameter("user_name");
		String phone=getStrParameter("phone");
		int state=getIntParameter("state");
		int user_id=getIntParameter("user_id");
		Boolean bool=userService.updateUser(name, phone, state, user_id);
		JSONObject jsonObject = new JSONObject();
		if(bool==false){
			jsonObject.put("data", 1);
		}
		this.getWriter().write(jsonObject.toString());
		return null;
	}
	
	/**
	 * 修改用户的密码
	 * @return
	 */
	public ActionResult doOLAVAUpdateUserPwd(){
		int user_id=getIntParameter("user_id");
		String resPWD = Encrypt.MD5(getStrParameter("pwd") + IConstants.PASS_KEY);
		Boolean bool=userService.updateUserPwd(user_id, resPWD);
		JSONObject jsonObject=new JSONObject();
		if(bool==false){
			jsonObject.put("data", 1);
		}
		this.getWriter().write(jsonObject.toString());
		return null;
	}
	
	
	/***************************************xiong start	*************************************/
	/**
	 * xiong-催收员工页面-20190709
	 * @return
	 */
	public ActionResult doUserLeaveList(){
		logger.info("催收员工页面");
		int temp = getIntParameter("temp",0);
		String  tempVelue = getStrParameter("tempvl"); 	
		
		int type=getIntParameter("type",0);

		int curPage  =getIntParameter("curPage",1);	
		DBPage page = userService.selectUserLeavetList(curPage, 15); 
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
 		return null;
	}
	
	/**
	 * xiong-修改请假天数
	 * @return
	 */
	public ActionResult doUpdateUserLeaveNum(){
		int user_id=getIntParameter("user_id");
		int leavenum=getIntParameter("leavenum");
		JSONObject jsonObject=new JSONObject();
		int a =0;
		if(leavenum<0){
			jsonObject.put("data",0);
		}else{
			 a= userService.updateUserLeaveNum(user_id, leavenum);
		}
						
		if(a==1){
			jsonObject.put("data", 1);
		}
		this.getWriter().write(jsonObject.toString());
		return null;
	}
	
	/**
	 * xiong-异常登录统计-20190725
	 * @return
	 */
	public ActionResult doSelectUserLoginExceptionList(){
		logger.info("催收员工页面");
		int temp = getIntParameter("temp",0);
		String  tempVelue = getStrParameter("tempvl");
		int type=getIntParameter("type",0);
		int curPage  =getIntParameter("curPage",1);			
		DBPage page = userService.selectUserLoginExceptionList(curPage, 15);
		
		
		List<DataRow> listuserid=page.getData();		
		Iterator<DataRow> iterator  = listuserid.iterator();		
		while(iterator.hasNext()){
			DataRow dataRow = iterator.next();
			String createTime =dataRow.getString("time");			
			dataRow.set("createTime",createTime);
			
//			String ip=dataRow.getString("ip");			
//			if(ip.equals("118.69.224.164") || ip.equals("116.118.114.241") ||  ip.equals("118.69.191.195") ||  ip.equals("127.0.0.1") ||  ip.equals("0:0:0:0:0:0:0:1")) {
//				iterator.remove();				
//			}	
		}		
		page.setData(listuserid);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
 		return null;
	}
	
	/**
	 * xiong-审核员工页面是否有审核权限-20190820
	 * @return
	 */
	public ActionResult doUserShenheList(){
		logger.info("催收员工页面");
		int temp = getIntParameter("temp",0);
		String  tempVelue = getStrParameter("tempvl");
		
		JSONObject jsonObject = new JSONObject();
		int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());	
//		if ( cmsuserid != 8 && cmsuserid != 888
//				&& cmsuserid != 6 && cmsuserid != 222 && cmsuserid !=9999 && cmsuserid !=8888 && cmsuserid !=2038) {
		if(!roleauthoritymangement.getRoleAM_CSlist(cmsuserid+"")){
			jsonObject.put("error", -3);
			//jsonObject.put("msg", "Quyền hạn của chủ quản！");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		
		int type=getIntParameter("type",0);

		int curPage  =getIntParameter("curPage",1);	
		DBPage page = userService.selectUserShenheList(curPage, 15); 
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
 		return null;
	}
	
	/**
	 * xiong-修改审核员工审核权限
	 * @return
	 */
	public ActionResult doUpdateUserShenhefdqx(){
		int user_id=getIntParameter("user_id");
		int fdqx=getIntParameter("fdqx");
		JSONObject jsonObject=new JSONObject();
		int a =0;
		if(fdqx<0){
			jsonObject.put("data",0);
		}else{
			 a= userService.updateUserShenhefdqx(user_id,fdqx);
		}
						
		if(a==1){
			jsonObject.put("data", 1);
		}
		this.getWriter().write(jsonObject.toString());
		return null;
	}
/***************************************xiong end	*************************************/
}
