package root.role;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.project.service.role.RoleService;
import com.thinkive.base.jdbc.DBPage;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.web.base.ActionResult;
import com.thinkive.web.base.BaseAction;

public class RoleAction extends BaseAction{
	private static Logger logger = Logger.getLogger(RoleAction.class);
	private static RoleService roleService=new RoleService();
	/**
	 * 得到所有的角色
	 * @return
	 */
	public ActionResult doGetAllRoleList(){
		logger.info("进入角色列表");
		DBPage page = roleService.getRoleList(); 
		DataRow row = new DataRow();
		row.set("list", page);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());
 		return null;
	}
	
	/**
	 * 添加角色
	 * @return
	 */
	public ActionResult doAddRole(){
		   JSONObject jsonObject = new JSONObject();
		   String role_name = getStrParameter("role_name");
		   String role_no = getStrParameter("role_no");
		   String description = getStrParameter("description");
		   //得到登录用户的id 
		  /* int userid = SessionHelper.getInt("userid", getSession());
		   if(userid == 0){	   
		  	  jsonObject.put("err", -1);
			  jsonObject.put("msg", "请先登录");
			  this.getWriter().write(jsonObject.toString());
			  return null;
		    }*/
		    DataRow data = new DataRow();
		    data.set("rolename", role_name);
			data.set("roleidentifying", role_no);
			Date date=new Date();
			DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time=format.format(date);
			data.set("createdate",time);
			data.set("roledescribe", description);
			roleService.insertRole("sdcms_user_role", data);
			logger.info("新增角色"+data);
			jsonObject.put("msg", "新增角色成功");
			this.getWriter().write(jsonObject.toString());
		   return null;
	}
}
