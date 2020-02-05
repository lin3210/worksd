package com.project.service.role;

import org.apache.log4j.Logger;

import com.thinkive.base.jdbc.DBPage;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.jdbc.session.Session;
import com.thinkive.base.service.BaseService;

/**
 * 角色服务类
 * @author Administrator
 *
 */
public class RoleService extends BaseService{
	private static Logger logger = Logger.getLogger(PowerService.class);
	
	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}
	
	/**
	 * 查询所有的用户角色
	 * @return
	 */
	public DBPage getRoleList(){
		String sql="select id,rolename,roleidentifying,createdate from sdcms_user_role where id !=10";
	    return getJdbcTemplate().queryPage(sql, 1, 40);
	}
	
	/**
	 * 新增的用户
	 * @param tableName
	 * @param dataRow
	 */
	public Boolean insertRole(String tableName,DataRow dataRow){
		Session session = getSession("web");
		session.beginTrans();
		try {
        	session.insert(tableName, dataRow);	
			session.commitTrans();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			e.printStackTrace();
			session.rollbackTrans();
		}finally{session.close();}
		return false;
	}
	
}
