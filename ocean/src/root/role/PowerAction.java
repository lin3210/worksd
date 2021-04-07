package root.role;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.project.bean.TreeVO;
import com.project.service.role.PowerService;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.web.base.ActionResult;
import com.thinkive.web.base.BaseAction;

public class PowerAction extends BaseAction{
	private static Logger logger = Logger.getLogger(PowerAction.class);
	private static PowerService powerService=new PowerService();

	/**
	 * 得到登陆用户的一级权限
	 * @return
	 */
	public ActionResult doGetUserPowerMain(){
		HttpServletRequest request = getRequest();
		int roleId= Integer.valueOf(request.getParameter("roleid"));
		List<DataRow> list=powerService.getPowerListByRoleId(roleId);
		JSONArray jsArray=JSONArray.fromObject(list);
		this.getWriter().write(jsArray.toString());
		return null;
	}
	
	/**
	 * 根据用户的ID和角色查找相对应一级下得子菜单
	 * @return
	 */
	public ActionResult doGetUserPower(){
		HttpServletRequest request = getRequest();
		logger.info(request);
		int roleMain = Integer.valueOf(request.getParameter("roleMain"));
		int roleId= Integer.valueOf(request.getParameter("roleid"));
		//参数传递
		List<DataRow> list=powerService.getUserPower(roleId, roleMain);
		JSONArray isArray=JSONArray.fromObject(list);
		this.getWriter().write(isArray.toString());
		return null;
	}
	
	/**
	 * 得到所有的用户权限或根据角色查找他对应的权限
	 * @return
	 */
	public ActionResult doGetAllPower(){
		List<DataRow> listMain=powerService.getAllPower(0);
		List<TreeVO> treePList=new ArrayList<TreeVO>();
		for (int i = 0; i < listMain.size(); i++) {
				DataRow dr=listMain.get(i);
				//处理一级菜单
				TreeVO vo=new TreeVO();
				int id=i+1;
				String pid=String.valueOf(id);
				vo.setId(pid);
				vo.setName(dr.getString("powername"));
				vo.setOpen("false");
				vo.setpId("0");
				vo.setUrl(String.valueOf(dr.getInt("id")));
				treePList.add(vo);
				List<DataRow> listSon=powerService.getAllPower(dr.getInt("id"));  //根据一级菜单得到二级菜单
				for (int j = 0; j < listSon.size(); j++) {  
					DataRow drj=listSon.get(j);
					TreeVO vo1=new TreeVO();
					int id1=j+1;
					String pid1=pid+String.valueOf(id1);
					vo1.setId(pid1);
					vo1.setName(drj.getString("powername"));
					vo1.setOpen("false");
					vo1.setpId(pid);
					vo1.setUrl(String.valueOf(drj.getInt("id")));
					treePList.add(vo1);
				}
			}
		JSONArray isArray=JSONArray.fromObject(treePList);
		this.getWriter().write(isArray.toString());
		return null;
	}
	
	/**
	 * 根据角色查找他对应的权限
	 * @return
	 */
	public ActionResult doGetPowerByRole(){
		int roleId=getIntParameter("roleId");
		List<DataRow> listMain=this.mianPowerByRole(roleId);
		List<TreeVO> treePList=new ArrayList<TreeVO>();
		for (int i = 0; i < listMain.size(); i++) {
				DataRow dr=listMain.get(i);
				//处理一级菜单
				TreeVO vo=new TreeVO();
				int id=i+1;
				String pid=String.valueOf(id);
				vo.setId(pid);
				vo.setName(dr.getString("powername"));
				vo.setOpen("false");
				vo.setpId("0");
				treePList.add(vo);
				List<DataRow> listSon=this.sonPowerByRoleAndPower(roleId,dr.getInt("id"));  //根据一级菜单得到二级菜单
				for (int j = 0; j < listSon.size(); j++) {  
					DataRow drj=listSon.get(j);
					TreeVO vo1=new TreeVO();
					int id1=j+1;
					String pid1=pid+String.valueOf(id1);
					vo1.setId(pid1);
					vo1.setName(drj.getString("powername"));
					vo1.setOpen("false");
					vo1.setpId(pid);
					treePList.add(vo1);
				}
			}
		JSONArray isArray=JSONArray.fromObject(treePList);
		this.getWriter().write(isArray.toString());
		return null;
	}
	
	/**
	 * 修改权限
	 * @return
	 */
	public ActionResult doAddMenuQx(){
		JSONObject jsonObject = new JSONObject();
		String json_str=getStrParameter("json_str");
		int roleId=getIntParameter("role_id");
		//先删除这个角色有关的所有记录
		Boolean bool=powerService.deleteQRguanxi(roleId);
		if(bool==true){
			//然后再新增这个角色的权限
			String[] qxid=json_str.split(",");
			for (int i = 0; i < qxid.length; i++) {
				DataRow dr=new DataRow();
				dr.set("rid",roleId);
				dr.set("pid", Integer.valueOf(qxid[i]));
				Boolean bo=powerService.addQRguanxi("sdcms_user_rp", dr);
				if(bo==false){
					jsonObject.put("data", 1);
				}
			}
		}else{
			jsonObject.put("data", 1);
		}
		this.getWriter().write(jsonObject.toString());
		return null;
	}
	
	public List<DataRow> mianPowerByRole(int roleId){
		List<DataRow> list=powerService.getPowerListByRoleId(roleId);
		return list;
	}
	
	public List<DataRow> sonPowerByRoleAndPower(int roleId,int roleMain){
		List<DataRow> list=powerService.getUserPower(roleId, roleMain);
		return list;
	}
}

