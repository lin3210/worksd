package com.project.service.role;

import java.util.List;

import org.apache.log4j.Logger;

import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.jdbc.session.Session;
import com.thinkive.base.service.BaseService;

public class PowerService extends BaseService{
	private static Logger logger = Logger.getLogger(PowerService.class);
	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}
	
	/**
	 * 查询用户角色的一级菜单
	 * @param roleId
	 * @return
	 */
	public List<DataRow> getPowerListByRoleId(int roleId){
		String sql = "select p.* from sdcms_user_rp rp inner join sdcms_user_power p on rp.pid=p.id where rp.rid="+roleId+" and powermain=0 order by p.id";
		return getJdbcTemplate().query(sql);
	}
	
	/**
	 * 根据用户的ID和角色查找相对应一级下得子菜单
	 * @return
	 */
	public List<DataRow> getUserPower(int roleId,int powerMain){
		String sql="select DISTINCT p.* from sdcms_user_rp rp inner join sdcms_user_power p on rp.pid=p.id where rp.rid="+roleId+" and powermain="+powerMain+" order by p.id;";
		return getJdbcTemplate().query(sql);
	}

	/**
	 * 查询所有的权限
	 * @return
	 */
	public List<DataRow> getAllPower(int powermain){
		String sql="select * from sdcms_user_power where powermain="+powermain;
		return getJdbcTemplate().query(sql);
	}
	
	/**
	 * 删除这个角色原有的权限
	 * @param roleId
	 * @return
	 */
	public Boolean deleteQRguanxi(int roleId){
		Session session=this.getSession("web");
		session.beginTrans();
		try {
			String sql="delete from sdcms_user_rp where rid="+roleId;
			session.update(sql);
			session.commitTrans();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			e.printStackTrace();
			session.rollbackTrans();
		}finally{
			session.close();
		}
		return false;
	}
	
	public boolean addQRguanxi(String tableName,DataRow rd){
		/*Session session=this.getSession("web");
		session.beginTrans();
		try {*/
			this.getJdbcTemplate().insert(tableName, rd);
			/*session.commitTrans();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			e.printStackTrace();
			session.rollbackTrans();
		}finally{
			session.close();
		}*/
		return true;
	}

}
