package root.role;

import java.util.ArrayList;
import java.util.Date;
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

public class roleAuthorityMangement extends BaseAction{
	
	private static Logger logger = Logger.getLogger(roleAuthorityMangement.class);
	private static PowerService powerService=new PowerService();
	
	//评审规则ID
	final static int GuiZE_SHTJ = 2000;      //审核权限
	final static int GuiZE_CSTJ = 3000;      //催收权限
	final static int GuiZE_CWTJ = 4000;      //财务权限  
	final static int GuiZE_JRTJ = 5000;      //今日提醒
	final static int GuiZE_FPTJ = 6000;      //首页统计数据  
	
	/**
	 * 今日提醒 统计查询全部 
	 * @param cmsuserid
	 * @param isqual
	 * @return  匹配到返回 true 
	 */
	public boolean getRoleAM_JRlist(String cmsuserid ) {
		boolean result =false;
		DataRow row = powerService.getpingjiguize(GuiZE_JRTJ);  // 
		String  guize1 =row.getString("guizebianliang1").replace(" ","");
		String userzu[] = guize1.split(",");
		
		for(String user:userzu) {
			if(user!= null && !user.equals("") &&cmsuserid.equals(user)) {
				result=true;
				break;
			}
		}
		
		return result;
	}
	
	
	/**
	 * 审核页面 统计查询全部 
	 * @param cmsuserid
	 * @param isqual
	 * @return  匹配到返回 true 
	 */
	public boolean getRoleAM_SHlist(String cmsuserid ) {
		/*
		 * if(cmsuserid == 8 || cmsuserid == 888 || cmsuserid == 222 || cmsuserid == 6 || cmsuserid == 8 || cmsuserid == 53){
		 */
		boolean result =false;
		DataRow row = powerService.getpingjiguize(GuiZE_SHTJ);  // 
		String  guize1 =row.getString("guizebianliang1").replace(" ","");
		String userzu[] = guize1.split(",");
		
		for(String user:userzu) {
			if(user!= null && !user.equals("") &&cmsuserid.equals(user)) {
				result=true;
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * 催收页面 统计查询全部 人员
	 * @param cmsuserid
	 * @param isqual
	 * @return  匹配到返回 true 
	 */
	public boolean getRoleAM_CSlist(String cmsuserid ) {

		boolean result =false;
		DataRow row = powerService.getpingjiguize(GuiZE_CSTJ);  // 
		String  guize1 =row.getString("guizebianliang1").replace(" ","");
		String userzu[] = guize1.split(",");
		
		for(String user:userzu) {
			if(user!= null && !user.equals("") &&cmsuserid.equals(user)) {
				result=true;
				break;
			}
		}
		
		return result;
	}
	/**
	 * 财务 统计数据---查询全部 人员
	 * @param cmsuserid
	 * @param isqual
	 * @return  匹配到返回 true 
	 */
	public boolean getRoleAM_CWlist(String cmsuserid ) {

		boolean result =false;
		DataRow row = powerService.getpingjiguize(GuiZE_CWTJ);  // 
		String  guize1 =row.getString("guizebianliang1").replace(" ","");
		String userzu[] = guize1.split(",");
		
		for(String user:userzu) {
			if(user!= null && !user.equals("") &&cmsuserid.equals(user)) {
				result=true;
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * 首页 统计数据---查询全部 人员
	 * @param cmsuserid
	 * @param isqual
	 * @return  匹配到返回 true 
	 */
	public boolean getRoleAM_FirstPage(String cmsuserid ) {
		/*
		 * if(cmsuserid == 8 || cmsuserid == 888 || cmsuserid == 222 || cmsuserid == 6 || cmsuserid == 8
		 *  || cmsuserid == 2016 || cmsuserid == 5051|| cmsuserid == 5053 || cmsuserid == 9999|| cmsuserid == 8888){
		 */
		boolean result =false;
		DataRow row = powerService.getpingjiguize(GuiZE_FPTJ);  // 
		String  guize1 =row.getString("guizebianliang1").replace(" ","");
		String userzu[] = guize1.split(",");
		
		for(String user:userzu) {
			if(user!= null && !user.equals("") &&cmsuserid.equals(user)) {
				result=true;
				break;
			}
		}
		
		return result;
	}
	
	
	
//	public  static void main(String []arg) {
//		String cmsid ="1888";
//		int cmsuserid = 1888;
//		roleAuthorityMangement  mroleAuthorityMangement = new roleAuthorityMangement();
//		System.out.println("--- :"+mroleAuthorityMangement.getRoleAM_JRlist(cmsid));
//		System.out.println("--- :"+mroleAuthorityMangement.getRoleAM_SHlist(cmsid));
//		System.out.println("--- :"+mroleAuthorityMangement.getRoleAM_CSlist(cmsid));
//		if(mroleAuthorityMangement.getRoleAM_JRlist(cmsid)) {
//			System.out.println(" 1 :");
//		}
//		if(!mroleAuthorityMangement.getRoleAM_JRlist(cmsid)) {
//			System.out.println(" 11 false :");
//		}
//		if(cmsuserid != 1 && cmsuserid != 2 && cmsuserid != 3 && cmsuserid != 4 && cmsuserid != 5 && cmsuserid != 7 &&  cmsuserid != 9 && 
//				cmsuserid != 8 && cmsuserid != 888 && cmsuserid != 8888 && cmsuserid != 6 && cmsuserid != 6868 && cmsuserid != 8015 && cmsuserid !=8023){
//			System.out.println(" 11111 false :");
//		}
//		
////		if(mroleAuthorityMangement.getRoleAM_SHlist(cmsid)) {
////			System.out.println(" 2 :");
////		}
////		if(!mroleAuthorityMangement.getRoleAM_CSlist(cmsid)) {
////			System.out.println(" 3 :");
////		}
//		
//	}

}
