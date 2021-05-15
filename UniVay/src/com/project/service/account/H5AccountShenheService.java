package com.project.service.account;

import java.util.List;

import org.apache.log4j.Logger;

import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.service.BaseService;
import com.thinkive.base.util.StringHelper;

/**
 * 统计审核
 * @author 苏兰
 *
 */
public class H5AccountShenheService extends BaseService{
	
	private static Logger logger=Logger.getLogger(H5AccountShenheService.class);
	
	public JdbcTemplate getJdbcTemplate(){
		return getJdbcTemplate("web");
	}
	
	/**
	 * 根据用户ID查找他审批的一审(总数)
	 * @param userId
	 * @return
	 */
	public int getOneCount(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` where cl_ren=? and cl_status<>0 AND sd_user.`username` LIKE 'TAFA%'";
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		 if (!StringHelper.isEmpty(startDate)) {
				
			sql += " and CONCAT(SUBSTRING(cl_time,7,4),'-',SUBSTRING(cl_time,4,2),'-',SUBSTRING(cl_time,1,2),SUBSTRING(cl_time,11,9)) >='" + startDate + "'";
					
			sql += " and CONCAT(SUBSTRING(cl_time,7,4),'-',SUBSTRING(cl_time,4,2),'-',SUBSTRING(cl_time,1,2)) <='" + endDate + "'";
		}
		Object[] object=new Object[]{userId};
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他审批的一审(总数)
	 * @param userId
	 * @return
	 */
	public int getOnesjrsCount(int userId,String startDate,String endDate,String name,String phone){
		String sql="SELECT COUNT(jishu) FROM (SELECT COUNT(sd_new_jkyx.id) AS jishu,cl_time FROM sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` WHERE cl_ren=? AND cl_status<>0 AND sd_user.`username` LIKE 'TAFA%'";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(cl_time,7,4),'-',SUBSTRING(cl_time,4,2),'-',SUBSTRING(cl_time,1,2),SUBSTRING(cl_time,11,9)) >='" + startDate + "'";
					
			sql += " and CONCAT(SUBSTRING(cl_time,7,4),'-',SUBSTRING(cl_time,4,2),'-',SUBSTRING(cl_time,1,2)) <='" + endDate + "'";
				
		}
		sql +=" GROUP BY userid)a  ";
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他审批的二审(总数)
	 * @param userId
	 * @return
	 */
	public int getTwoCount(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` where cl02_ren=? and cl02_status<>0 AND sd_user.`username` LIKE 'TAFA%'";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2),SUBSTRING(cl02_time,11,9)) >='" + startDate + "'";
					
			sql += " and CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2)) <='" + endDate + "'";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他审批的二审(总数)
	 * @param userId
	 * @return
	 */
	public int getTwosjrsCount(int userId,String startDate,String endDate,String name,String phone){
		String sql="SELECT COUNT(jishu) FROM (SELECT COUNT(id) AS jishu,cl02_time FROM sd_new_jkyx INNER JOIN sdcms_user ON  sdcms_user.USER_ID=sd_new_jkyx.cl02_ren WHERE cl02_ren=? AND cl02_status<>0 ";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2),SUBSTRING(cl02_time,11,9)) >='" + startDate + "'";
				
			sql += " and CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2)) <='" + endDate + "'";
		
		}
		sql += " GROUP BY userid)a ";
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他审批的二审通过并且放款的(总数)
	 * @param userId
	 * @return
	 */
	public int getTwoCountTGFK(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` where cl02_ren=? and cl02_status=1 and sfyfk=1 AND sd_user.`username` LIKE 'TAFA%'";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2),SUBSTRING(cl02_time,11,9)) >='" + startDate + "'";
				
			sql += " and CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他审批的二审通过并且放款的(总数)
	 * @param userId
	 * @return
	 */
	public int getNowTwoCountTGFK(int userId,String startDate,String endDate,String nowdate, String enddate,String name,String phone){
		String sql= "";
		if (!StringHelper.isEmpty(startDate)) {
			sql="select count(*) from sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` where cl02_ren=? and cl02_status=1 and sfyfk=1 AND sd_user.`username` LIKE 'TAFA%'";
			
				
				sql += " and CONCAT(SUBSTRING(cl_time,7,4),'-',SUBSTRING(cl_time,4,2),'-',SUBSTRING(cl_time,1,2),SUBSTRING(cl_time,11,9)) >='" + startDate + "'";
				
				sql += " and CONCAT(SUBSTRING(cl_time,7,4),'-',SUBSTRING(cl_time,4,2),'-',SUBSTRING(cl_time,1,2)) <='" + endDate + "'";
			
		}else{
			sql="select count(*) from sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cl02_ren where cl02_ren=? AND sd_user.`username` LIKE 'TAFA%' and cl02_status=1 and sfyfk=1 and substring(cl_time,1,10) ='"+enddate+"' ";
		}
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	public int getNowTwoCountTGFKOLD(int userId,String startDate,String endDate,String nowdate, String enddate,String name,String phone){
		String sql= "";
		if (!StringHelper.isEmpty(startDate)) {
			sql="select count(*) from sd_new_jkyx left join sd_user on sd_new_jkyx.userid=sd_user.id where cl02_ren=? and cl02_status=1 and sfyfk=1 and oldyonghu>0 ";
			
			
			sql += " and CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2),SUBSTRING(cl02_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2)) <='" + endDate + "'";
			
		}else{
			sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cl02_ren where cl02_ren=? and cl02_status=1 and sfyfk=1 and substring(cl_time,1,10) ='"+enddate+"' ";
		}
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他审批的二审通过并且还款的(总数)
	 * @param userId
	 * @return
	 */
	public int getTwoCountTGHK(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` where cl02_ren=? and cl02_status=1 and sfyfk=1 and sfyhw=1 and sd_new_jkyx.yuq_ts=0 AND sd_user.`username` LIKE 'TAFA%'";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2),SUBSTRING(cl02_time,11,9)) >='" + startDate + "'";
				
			sql += " and CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2)) <='" + endDate + "'";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他审批的二审通过并且放款的(总数)
	 * @param userId
	 * @return
	 */
	public int getTwoCountTGFKYQ(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` where cl02_ren=? and cl02_status=1 and sfyfk=1 and sd_new_jkyx.yuq_ts>0 AND sd_user.`username` LIKE 'TAFA%'";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2),SUBSTRING(cl02_time,11,9)) >='" + startDate + "'";
				
			sql += " and CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2)) <='" + endDate + "'";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他审批的二审通过并且放款的(总数)
	 * @param userId
	 * @return
	 */
	public int getTwoCountTGFKYQWH(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` where cl02_ren=? and cl02_status=1 and sfyfk=1 and sd_new_jkyx.yuq_ts>0 and sfyhw=0 AND sd_user.`username` LIKE 'TAFA%'";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2),SUBSTRING(cl02_time,11,9)) >='" + startDate + "'";
				
			sql += " and CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2)) <='" + endDate + "'";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他审批的三审(总数)
	 * @param userId
	 * @return
	 */
	public int getThreeCount(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` where cl03_ren=? and cl03_status<>0 AND sd_user.`username` LIKE 'TAFA%'";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(cl03_time,7,4),'-',SUBSTRING(cl03_time,4,2),'-',SUBSTRING(cl03_time,1,2),SUBSTRING(cl03_time,11,9)) >='" + startDate + "'";
				
			sql += " and CONCAT(SUBSTRING(cl03_time,7,4),'-',SUBSTRING(cl03_time,4,2),'-',SUBSTRING(cl03_time,1,2)) <='" + endDate + "'";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他审批的三审(总数)
	 * @param userId
	 * @return
	 */
	public int getThreesjrsCount(int userId,String startDate,String endDate,String name,String phone){
		String sql="SELECT COUNT(jishu) FROM (SELECT COUNT(id) AS jishu,cl03_time FROM sd_new_jkyx INNER JOIN sdcms_user ON  sdcms_user.USER_ID=sd_new_jkyx.cl03_ren WHERE cl03_ren=? AND cl03_status<>0 ";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
				sql += " and CONCAT(SUBSTRING(cl03_time,7,4),'-',SUBSTRING(cl03_time,4,2),'-',SUBSTRING(cl03_time,1,2),SUBSTRING(cl03_time,11,9)) >='" + startDate + "'";
				
				sql += " and CONCAT(SUBSTRING(cl03_time,7,4),'-',SUBSTRING(cl03_time,4,2),'-',SUBSTRING(cl03_time,1,2)) <='" + endDate + "'";
		}
		sql += " GROUP BY userid)a ";
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他审批的一审(总数)
	 * @param userId
	 * @return
	 */
	public int getOneCountSB(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` where cl_ren=? and cl_status=3 AND sd_user.`username` LIKE 'TAFA%'";
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
				sql += " and CONCAT(SUBSTRING(cl_time,7,4),'-',SUBSTRING(cl_time,4,2),'-',SUBSTRING(cl_time,1,2),SUBSTRING(cl_time,11,9)) >='" + startDate + "'";
				
				sql += " and CONCAT(SUBSTRING(cl_time,7,4),'-',SUBSTRING(cl_time,4,2),'-',SUBSTRING(cl_time,1,2)) <='" + endDate + "'";
		}
		Object[] object=new Object[]{userId};
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他审批的一审(总数)
	 * @param userId
	 * @return
	 */
	public int getOneCountSBXXCW(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` where cl_ren=? and cl_status=3 AND sd_user.`username` LIKE 'TAFA%'";
		sql +=" and (cl_yj like '%KH cap nhat lai thoi han vay%' ";
		sql +=" or cl_yj like '%Hinh CMND khong hop le%' ";
		sql +=" or cl_yj like '%Sai thong tin ngan hang%' ";
		sql +=" or cl_yj like '%Sai thong tin cong viec%' ";
		sql +=" or cl_yj like '%Sai thong tin nguoi lien he%' ";
		sql +=" or cl_yj like '%KH chua co nhu cau vay%') ";
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(cl_time,7,4),'-',SUBSTRING(cl_time,4,2),'-',SUBSTRING(cl_time,1,2),SUBSTRING(cl_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(cl_time,7,4),'-',SUBSTRING(cl_time,4,2),'-',SUBSTRING(cl_time,1,2)) <='" + endDate + "'";
		}
		Object[] object=new Object[]{userId};
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他审批的二审(总数)
	 * @param userId
	 * @return
	 */
	public int getTwoCountSB(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` where cl02_ren=? and cl02_status=3 AND sd_user.`username` LIKE 'TAFA%'";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
				sql += " and CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2),SUBSTRING(cl02_time,11,9)) >='" + startDate + "'";
				
				sql += " and CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2)) <='" + endDate + "'";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他审批的三审(总数)
	 * @param userId
	 * @return
	 */
	public int getThreeCountSB(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` where cl03_ren=? and cl03_status=3 AND sd_user.`username` LIKE 'TAFA%'";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
				sql += " and CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2),SUBSTRING(cl02_time,11,9)) >='" + startDate + "'";
				
				sql += " and CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2)) <='" + endDate + "'";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他审批的三审通过(总数)
	 * @param userId
	 * @return
	 */
	public int getThreeCountTG(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` where cl03_ren=? and cl03_status<>0 and sfyfk=1 AND sd_user.`username` LIKE 'TAFA%'";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
		
				sql += " and CONCAT(SUBSTRING(cl03_time,7,4),'-',SUBSTRING(cl03_time,4,2),'-',SUBSTRING(cl03_time,1,2),SUBSTRING(cl03_time,11,9)) >='" + startDate + "'";
				
				sql += " and CONCAT(SUBSTRING(cl03_time,7,4),'-',SUBSTRING(cl03_time,4,2),'-',SUBSTRING(cl03_time,1,2)) <='" + endDate + "'";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他审批的三审通过(总数)
	 * @param userId
	 * @return
	 */
	public int getNowThreeCountTG(int userId,String startDate,String endDate,String nowdate, String enddate,String name,String phone){
		String sql = "";
		if (!StringHelper.isEmpty(startDate)) {
			sql="select count(*) from sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` where cl03_ren="+userId+" and cl03_status<>0 and sfyfk=1 AND sd_user.`username` LIKE 'TAFA%'";
			
			sql += " and CONCAT(SUBSTRING(cl03_time,7,4),'-',SUBSTRING(cl03_time,4,2),'-',SUBSTRING(cl03_time,1,2),SUBSTRING(cl03_time,11,9)) >='" + startDate + "'";
					
			sql += " and CONCAT(SUBSTRING(cl03_time,7,4),'-',SUBSTRING(cl03_time,4,2),'-',SUBSTRING(cl03_time,1,2)) <='" + endDate + "'";
		}else{
			sql="select count(*) from sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cl03_ren where cl03_ren="+userId+" and cl03_status<>0 and sfyfk=1 and substring(cl03_time,1,10) ='"+nowdate+"'  AND sd_user.`username` LIKE 'TAFA%'";
		}
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他审批的三审通过已还(总数)
	 * @param userId
	 * @return
	 */
	public int getYH(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cl03_ren where cl03_ren=? and cl03_status<>0 and sfyfk=1 and sfyhw=1";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(cl03_time,7,4),'-',SUBSTRING(cl03_time,4,2),'-',SUBSTRING(cl03_time,1,2),SUBSTRING(cl03_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(cl03_time,7,4),'-',SUBSTRING(cl03_time,4,2),'-',SUBSTRING(cl03_time,1,2)) <='" + endDate + "'";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他审批的三审逾期(总数)
	 * @param userId
	 * @return
	 */
	public int getThreeCountYQ(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cl03_ren where cl03_ren=? and cl03_status<>0 and yuq_ts<>0";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(cl03_time,7,4),'-',SUBSTRING(cl03_time,4,2),'-',SUBSTRING(cl03_time,1,2),SUBSTRING(cl03_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(cl03_time,7,4),'-',SUBSTRING(cl03_time,4,2),'-',SUBSTRING(cl03_time,1,2)) <='" + endDate + "'";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他审批的三审逾期未还(总数)
	 * @param userId
	 * @return
	 */
	public int getYQWH(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cl03_ren where cl03_ren=? and cl03_status<>0 and yuq_ts<>0 and sfyhw<>1";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(cl03_time,7,4),'-',SUBSTRING(cl03_time,4,2),'-',SUBSTRING(cl03_time,1,2),SUBSTRING(cl03_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(cl03_time,7,4),'-',SUBSTRING(cl03_time,4,2),'-',SUBSTRING(cl03_time,1,2)) <='" + endDate + "'";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	
	/**
	 * 根据用户ID查找他审批的一审(总数)
	 * @param userId
	 * @return
	 */
	public int getNowOneCount(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		
			sql="select count(*) from sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` where cl_ren="+userId+" and cl_status<>0 and substring(cl_time,1,10) ='"+startDate+"' AND sd_user.`username` LIKE 'TAFA%'";
		
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	
	/**
	 * 根据用户ID查找他审批的一审(总数)
	 * @param userId
	 * @return
	 */
	public int getNowOnesjrsCount(int userId,String startDate,String endDate,String nowdate, String enddate,String name,String phone){
		String sql= "";
		if (!StringHelper.isEmpty(startDate)) {
			sql="SELECT COUNT(jishu) FROM (SELECT COUNT(sd_new_jkyx.id) AS jishu FROM sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` WHERE cl_ren="+userId+" AND cl_status<>0  AND sd_user.`username` LIKE 'TAFA%'";
			
				
				sql += " and CONCAT(SUBSTRING(cl_time,7,4),'-',SUBSTRING(cl_time,4,2),'-',SUBSTRING(cl_time,1,2),SUBSTRING(cl_time,11,9)) >='" + startDate + "'";
				
				sql += " and CONCAT(SUBSTRING(cl_time,7,4),'-',SUBSTRING(cl_time,4,2),'-',SUBSTRING(cl_time,1,2)) <='" + endDate + "'";
			
			sql += " GROUP BY userid)a ";
		}else{
			sql="SELECT COUNT(jishu) FROM (SELECT COUNT(sd_new_jkyx.id) AS jishu FROM sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` INNER JOIN sdcms_user ON  sdcms_user.USER_ID=sd_new_jkyx.cl_ren WHERE cl_ren="+userId+" AND cl_status<>0 AND SUBSTRING(cl_time,1,10) ='"+nowdate+"'AND sd_user.`username` LIKE 'TAFA%' GROUP BY userid)a ";
		}
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		
		return this.getJdbcTemplate().queryInt(sql);
	}
	public List<DataRow> getNowLYHCount(int userId,String startDate,String endDate,String name,String phone){
		String sql="select userid from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cl02_ren where cl02_ren=? and cl02_status=1 and sfyfk=1 ";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2),SUBSTRING(cl02_time,11,9)) >='" + startDate + "'";
				
			sql += " and CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().query(sql, object);
	}
	public int getAllSfyfk(int userid,String time){
		String sql = "select count(id) from sd_new_jkyx where sfyfk=1 and userid="+userid;
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他审批的二审(总数)
	 * @param userId
	 * @return
	 */
	public int getNowTwoCount(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		
			sql="select count(*) from sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` where  cl02_ren="+userId+" and cl02_status<>0 and substring(cl02_time,1,10) ='"+startDate+"'AND sd_user.`username` LIKE 'TAFA%'";
		
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他审批的二审(总数)
	 * @param userId
	 * @return
	 */
	public int getNowTwosjrsCount(int userId,String startDate,String endDate,String nowdate, String enddate,String name,String phone){
		String sql= "";
		if (!StringHelper.isEmpty(startDate)) {
			sql="SELECT COUNT(jishu) FROM (SELECT COUNT(sd_new_jkyx.id) AS jishu FROM sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` INNER JOIN sdcms_user ON  sdcms_user.USER_ID=sd_new_jkyx.cl02_ren WHERE cl02_ren="+userId+" AND cl02_status<>0 AND sd_user.`username` LIKE 'TAFA%'";

			sql += " and CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2),SUBSTRING(cl02_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2)) <='" + endDate + "'";
		
			sql += " GROUP BY userid)a where 1=1 ";
		}else{
			sql="SELECT COUNT(jishu) FROM (SELECT COUNT(sd_new_jkyx.id) AS jishu FROM sd_new_jkyx INNER JOIN sdcms_user ON  sdcms_user.USER_ID=sd_new_jkyx.cl02_ren WHERE cl02_ren="+userId+" AND cl02_status<>0 AND SUBSTRING(cl02_time,4,2)='"+nowdate.substring(3, 5)+"' AND SUBSTRING(cl02_time,1,10) ='"+nowdate+"' GROUP BY userid)a where 1=1";
		}
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他审批的一审(总数)
	 * @param userId
	 * @return
	 */
	public int getNowOneCountSB(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		
			sql="select count(*) from sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` where cl_ren="+userId+" and cl_status=3 and substring(cl_time,1,10) ='"+startDate+"' AND sd_user.`username` LIKE 'TAFA%'";
		
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他审批的一审(总数)
	 * @param userId
	 * @return
	 */
	public int getNowOneCountSBXXCW(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		
		sql="select count(*) from sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` where cl_ren="+userId+" and cl_status=3 and substring(cl_time,1,10) ='"+startDate+"' AND sd_user.`username` LIKE 'TAFA%'";
		sql +=" and (cl_yj like '%KH cap nhat lai thoi han vay%' ";
		sql +=" or cl_yj like '%Hinh CMND khong hop le%' ";
		sql +=" or cl_yj like '%Sai thong tin ngan hang%' ";
		sql +=" or cl_yj like '%Sai thong tin cong viec%') ";
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他审批的二审(总数)
	 * @param userId
	 * @return
	 */
	public int getNowTwoCountSB(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		
			sql="select count(*) from sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` where  cl02_ren="+userId+" and cl02_status=3 and substring(cl02_time,1,10) ='"+startDate+"' AND sd_user.`username` LIKE 'TAFA%'";
		
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他放款成功的(总数)
	 * @param userId
	 * @return
	 */
	public int getFKCGCountZS(int userId,String name,String phone){
		String sql= "select count(id) from sd_withdraw where checkid="+userId;
		
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他放款成功的(总数)
	 * @param userId
	 * @return
	 */
	public int getWRZCount(String startDate,String endDate){
		String sql="";
		if(startDate.substring(3, 5).equals(endDate.substring(3, 5))){
			sql="select count(*) from sd_user  where vipstatus=0 and substring(createtime,4,2)="+startDate.substring(3, 5)+" and createtime BETWEEN '"+startDate+"' and '"+endDate+"' ";
		}else{
			sql="select count(*) from sd_user  where vipstatus=0 and substring(createtime,4,2)="+startDate.substring(3, 5)+" and createtime>'"+startDate+"' ";
		}
		
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他放款成功的(总数)
	 * @param userId
	 * @return
	 */
	public int getKFHFCGCountZS(int userId,String name,String phone){
		String sql="select count(*) from sd_returnvisit inner join sdcms_user on  sdcms_user.USER_ID=sd_returnvisit.kefuid where kefuid=? and (code=9 or code=10) ";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他当天放款成功的(总数)
	 * @param userId
	 * @return
	 */
	public int getFKCGCount(int userId,String startDate,String endDate,String name,String phone){
		String sql= "select count(id) from sd_withdraw where substring(checktime,1,10)='"+startDate+"' and checkid="+userId;
		
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getOfflineZS(int userId,String startDate,String endDate,String name,String phone){
		
		String sql= "select count(w.id) from sd_withdraw w left join sd_bankcard b on w.userid=b.userid where substring(w.checktime,1,10)='"+startDate+"' and substring(orderid,1,4)='mova' and w.checkid="+userId;
		
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getAgriBank(int userId,String startDate,String endDate,String name,String phone){
		
		String sql= "select count(w.id) from sd_withdraw w left join sd_bankcard b on w.userid=b.userid where substring(w.checktime,1,10)='"+startDate+"' and substring(w.orderid,1,4)='mova' and substring(b.cardname,1,8)='Agribank' and w.checkid="+userId;
		
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getVietcomBank(int userId,String startDate,String endDate,String name,String phone){
		
		String sql= "select count(w.id) from sd_withdraw w left join sd_bankcard b on w.userid=b.userid where substring(w.checktime,1,10)='"+startDate+"' and  substring(orderid,1,4)='mova' and substring(b.cardname,1,11)='VietcomBank' and w.checkid="+userId;
		
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getOnlineSacombank(int userId,String startDate,String endDate,String name,String phone){
		
		String sql= "select count(w.id) from sd_withdraw w left join sd_bankcard b on w.userid=b.userid where substring(w.checktime,1,10)='"+startDate+"' and  substring(orderid,1,5)='CTYXT' and w.checkid="+userId;
		
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他当天放款成功的(总数)
	 * @param userId
	 * @return
	 */
	public int getKFHFCGCount(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		if(startDate.substring(3, 5).equals(endDate.substring(3, 5))){
			sql="select count(*) from sd_returnvisit inner join sdcms_user on  sdcms_user.USER_ID=sd_returnvisit.kefuid where kefuid="+userId+" and (code=9 or code=10) and substring(visitdate,4,2)="+startDate.substring(3, 5)+" and visitdate BETWEEN '"+startDate+"' and '"+endDate+"' ";
		}else{
			sql="select count(*) from sd_returnvisit inner join sdcms_user on  sdcms_user.USER_ID=sd_returnvisit.kefuid where kefuid="+userId+" and (code=9 or code=10) and substring(visitdate,4,2)="+startDate.substring(3, 5)+" and visitdate>'"+startDate+"' ";
		}
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他放款失败的(总数)
	 * @param userId
	 * @return
	 */
	public int getFKSBCountZS(int userId,String name,String phone){
		String sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.fkr where fkr=?  and (sfyfk=3 or cl03_yj='Bankcard Error') ";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他当天放款失败的(总数)
	 * @param userId
	 * @return
	 */
	public int getFKSBCount(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		if(startDate.substring(3, 5).equals(endDate.substring(3, 5))){
			sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.fkr where fkr="+userId+" and  (sfyfk=3 or cl03_yj='Bankcard Error') and substring(fkr_time,4,2)="+startDate.substring(3, 5)+" and fkr_time BETWEEN '"+startDate+"' and '"+endDate+"' ";
		}else{
			sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.fkr where fkr="+userId+" and  (sfyfk=3 or cl03_yj='Bankcard Error') and substring(fkr_time,4,2)="+startDate.substring(3, 5)+" and fkr_time>'"+startDate+"' ";
		}
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他审批的三审(总数)
	 * @param userId
	 * @return
	 */
	public int getNowThreeCount(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		
			sql="select count(*) from sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` where cl03_ren="+userId+" and cl03_status<>0 and substring(cl03_time,1,10) ='"+startDate+"' AND sd_user.`username` LIKE 'TAFA%'";
		
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他审批的三审(总数)
	 * @param userId
	 * @return
	 */
	public int getNowThreesjrsCount(int userId,String startDate,String endDate,String nowdate, String enddate,String name,String phone){
		String sql= "";
		if (!StringHelper.isEmpty(startDate)) {
			sql="SELECT COUNT(jishu) FROM (SELECT COUNT(sd_new_jkyx.id) AS jishu FROM sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` INNER JOIN sdcms_user ON  sdcms_user.USER_ID=sd_new_jkyx.cl03_ren WHERE cl03_ren="+userId+" AND cl03_status<>0 AND sd_user.`username` LIKE 'TAFA%'";

			sql += " and CONCAT(SUBSTRING(cl03_time,7,4),'-',SUBSTRING(cl03_time,4,2),'-',SUBSTRING(cl03_time,1,2),SUBSTRING(cl03_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(cl03_time,7,4),'-',SUBSTRING(cl03_time,4,2),'-',SUBSTRING(cl03_time,1,2)) <='" + endDate + "'";
		
			sql += " GROUP BY userid)a where 1=1 ";
		}else{
			sql="SELECT COUNT(jishu) FROM (SELECT COUNT(sd_new_jkyx.id) AS jishu FROM sd_new_jkyx INNER JOIN sdcms_user ON  sdcms_user.USER_ID=sd_new_jkyx.cl03_ren WHERE cl03_ren="+userId+" AND cl03_status<>0 AND SUBSTRING(cl03_time,4,2)='"+nowdate.substring(3, 5)+"' AND SUBSTRING(cl03_time,1,10) ='"+nowdate+"' GROUP BY userid)a where 1=1";
		}
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他审批的三审(总数)
	 * @param userId
	 * @return
	 */
	public int getNowThreeCountSB(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		
			sql="select count(*) from sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.`userid` = sd_user.`id` where cl03_ren="+userId+" and cl03_status=3 and substring(cl03_time,1,10) ='"+startDate+"' AND sd_user.`username` LIKE 'TAFA%'";
		
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他催收的M1(总数)
	 * @param userId
	 * @return
	 */
	public int getCSOneCount(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_m1 where cuishou_m1=? and sfyhw=1 and yuq_ts>0 and yuq_ts<4";
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2),SUBSTRING(hk_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2)) <='" + endDate + "'";
		}
		Object[] object=new Object[]{userId};
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	public int getCSOneCountM0(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx where cuishou_m0=? and sfyhw=1 and yuq_ts>0 and yuq_ts<4 ";
		
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2),SUBSTRING(hk_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2)) <='" + endDate + "'";
		}
		Object[] object=new Object[]{userId};
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	public int getCSOneCountM1(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx where cuishou_m1=? and sfyhw=1 and yuq_ts>0 and yuq_ts<16 ";
		
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2),SUBSTRING(hk_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2)) <='" + endDate + "'";
		}
		Object[] object=new Object[]{userId};
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	public int getCSOneCountM0SS(int userId,String startDate,String endDate,String name,String phone,int number){
		String sql="select count(r.id) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where (r.remark='全额还款' or r.remark='完成还款，部分逾期利息还款') and r.dqyqts>0 and j.sfyhw=1 and j.yuq_ts>0 and j.yuq_ts<4 and j.cuishou_id='"+userId+"' and j.cuishou_m0='"+userId+"' " ;
		
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
		}else{
			String time="";
			String time2="";
			if(number < 10){
				time = "0" + number;
				time2 = "0" + (number+1);
			}
			sql +=  " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2))>='2019-"+time+"-01' "+
					" and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2))<'2019-"+time2+"-01' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getCSOneCountM1SS(int userId,String startDate,String endDate,String name,String phone,int number){
		String sql="select count(r.id) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where (r.remark='全额还款' or r.remark='完成还款，部分逾期利息还款') and r.dqyqts>0 and j.sfyhw=1 and j.yuq_ts>0 and j.cuishou_id='"+userId+"' and j.cuishou_m1='"+userId+"' " ;
				
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
		}else{
			String time="";
			String time2="";
			if(number < 10){
				time = "0" + number;
				time2 = "0" + (number+1);
			}
			sql +=  " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2))>='2019-"+time+"-01' "+
					" and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2))<'2019-"+time2+"-01' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getCSOneCountM2SS(int userId,String startDate,String endDate,String name,String phone,int number){
		String sql="select count(r.id) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where (r.remark='全额还款' or r.remark='完成还款，部分逾期利息还款') and r.dqyqts>15 and j.sfyhw=1 and j.yuq_ts>15 and j.cuishou_id='"+userId+"' and j.cuishou_m2='"+userId+"' " ;
		
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
		}else{
			String time="";
			String time2="";
			if(number < 10){
				time = "0" + number;
				time2 = "0" + (number+1);
			}
			sql +=  " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2))>='2019-"+time+"-01' "+
					" and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2))<'2019-"+time2+"-01' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getCSOneCountM3SS(int userId,String startDate,String endDate,String name,String phone,int number){
		String sql="select count(r.id) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where (r.remark='全额还款' or r.remark='完成还款，部分逾期利息还款') and r.dqyqts>45 and j.sfyhw=1 and j.yuq_ts>45 and j.cuishou_id='"+userId+"' and j.cuishou_m3='"+userId+"' " ;
		
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
		}else{
			String time="";
			String time2="";
			if(number < 10){
				time = "0" + number;
				time2 = "0" + (number+1);
			}
			sql +=  " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2))>='2019-"+time+"-01' "+
					" and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2))<'2019-"+time2+"-01' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他催收的M1(总数)
	 * @param userId
	 * @return
	 */
	public int getCSOneCountCSWB(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx inner join sdcms_user_cs on  sdcms_user_cs.USER_ID=sd_new_jkyx.cuishouwb where cuishouwb=? and sfyhw=1 and yuq_ts>0 and yuq_ts<4";
		if(!name.equals("")){
			sql+=" and sdcms_user_cs.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user_cs.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2),SUBSTRING(hk_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2)) <='" + endDate + "'";
		}
		Object[] object=new Object[]{userId};
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他催收的M2(总数)
	 * @param userId
	 * @return
	 */
	public int getCSTwoCount(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_m1 where cuishou_m1=? and sfyhw=1 and yuq_ts>3 and yuq_ts<8";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2),SUBSTRING(hk_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2)) <='" + endDate + "'";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他催收的M2(总数)
	 * @param userId
	 * @return
	 */
	public int getCSTwoCountM2(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx where cuishou_m2=? and sfyhw=1 and yuq_ts>15 ";
		Object[] object=new Object[]{userId};
		
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2),SUBSTRING(hk_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2)) <='" + endDate + "'";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	public int getCSTwoCountM3(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx where cuishou_m3=? and sfyhw=1 and yuq_ts>45 ";
		Object[] object=new Object[]{userId};
		
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2),SUBSTRING(hk_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2)) <='" + endDate + "'";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他催收的M2(总数)
	 * @param userId
	 * @return
	 */
	public int getCSTwoCountCSWB(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx inner join sdcms_user_cs on  sdcms_user_cs.USER_ID=sd_new_jkyx.cuishouwb where cuishouwb=? and sfyhw=1 and yuq_ts>3 and yuq_ts<8";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user_cs.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user_cs.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2),SUBSTRING(hk_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2)) <='" + endDate + "'";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他催收的M3(总数)
	 * @param userId
	 * @return
	 */
	public int getCSThreeCount(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_m1 where cuishou_m1=? and sfyhw=1 and yuq_ts>7 and yuq_ts<31";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2),SUBSTRING(hk_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2)) <='" + endDate + "'";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他催收的M3(总数)
	 * @param userId
	 * @return
	 */
	public int getCSThreeCountCSWB(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx inner join sdcms_user_cs on  sdcms_user_cs.USER_ID=sd_new_jkyx.cuishouwb where cuishouwb=? and sfyhw=1 and yuq_ts>7 and yuq_ts<31";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user_cs.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user_cs.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2),SUBSTRING(hk_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2)) <='" + endDate + "'";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他催收的M4(总数)
	 * @param userId
	 * @return
	 */
	public int getCSFourCount(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_m1 where cuishou_m1=? and cuishou_id=? and sfyhw=1 and yuq_ts>30 ";
		Object[] object=new Object[]{userId,userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2),SUBSTRING(hk_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2)) <='" + endDate + "'";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他催收的M4(总数)
	 * @param userId
	 * @return
	 */
	public int getCSFourCountCSWB(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx inner join sdcms_user_cs on  sdcms_user_cs.USER_ID=sd_new_jkyx.cuishouwb where cuishouwb=? and sfyhw=1 and yuq_ts>30 ";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user_cs.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user_cs.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2),SUBSTRING(hk_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2)) <='" + endDate + "'";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	
	/**
	 * 根据用户ID查找他催收的一审(总数)
	 * @param userId
	 * @return
	 */
	public int getCSNowOneCount(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		
			sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_m1 where cuishou_m1="+userId+" and sfyhw=1 and yuq_ts>0 and yuq_ts<4 and substring(hk_time,4,2)="+startDate.substring(3, 5)+" and substring(hk_time,1,10) ='"+startDate+"' ";
		
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getCSNowOneCountM0(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		
		sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_m0 where cuishou_m0="+userId+" and sfyhw=1 and yuq_ts>0 and yuq_ts<4 and substring(hk_time,4,2)="+startDate.substring(3, 5)+" and substring(hk_time,1,10) ='"+startDate+"' ";
		
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getCSNowOneCountM1(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		
		sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_m1 where cuishou_m1="+userId+" and sfyhw=1 and yuq_ts>0 and yuq_ts<16 and substring(hk_time,4,2)="+startDate.substring(3, 5)+" and substring(hk_time,1,10) ='"+startDate+"' ";
		
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他催收的一审(总数)
	 * @param userId
	 * @return
	 */
	public int getCSNowOneCountCSWB(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		
		sql="select count(*) from sd_new_jkyx inner join sdcms_user_cs on  sdcms_user_cs.USER_ID=sd_new_jkyx.cuishouwb where cuishouwb="+userId+" and sfyhw=1 and yuq_ts>0 and yuq_ts<4 and substring(hk_time,4,2)="+startDate.substring(3, 5)+" and substring(hk_time,1,10) ='"+startDate+"' ";
		
		if(!name.equals("")){
			sql+=" and sdcms_user_cs.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user_cs.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他催收的一审(总数)
	 * @param userId
	 * @return
	 */
	public int getJRCSNowOneCount(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_m1 where cuishou_m1="+userId+" and sfyhw=1 and yuq_ts=0 and substring(hkyq_time,4,2)="+startDate.substring(3, 5)+" and substring(hkyq_time,1,10) ='"+startDate+"' ";
		
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他催收的一审(总数)
	 * @param userId
	 * @return
	 */
	public int getJRCSNowOneCountM2(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_m2 where cuishou_m2="+userId+" and sfyhw=1 and yuq_ts=0 and substring(hkyq_time,4,2)="+startDate.substring(3, 5)+" and substring(hkyq_time,1,10) ='"+startDate+"' ";
		
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他催收的一审(总数)
	 * @param userId
	 * @return
	 */
	public int getJRCSNowOneCountCSWB(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		sql="select count(*) from sd_new_jkyx inner join sdcms_user_cs on  sdcms_user_cs.USER_ID=sd_new_jkyx.cuishouwb where cuishouwb="+userId+" and sfyhw=1 and yuq_ts=0 and substring(hkyq_time,4,2)="+startDate.substring(3, 5)+" and substring(hkyq_time,1,10) ='"+startDate+"' ";
		
		if(!name.equals("")){
			sql+=" and sdcms_user_cs.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user_cs.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他催收的一审(总数)
	 * @param userId
	 * @return
	 */
	public int getDTZBSNowOneCount(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_id where cuishou_id="+userId+" and sfyhw=1 and yuq_ts=0 and substring(hk_time,4,2)="+startDate.substring(3, 5)+" and substring(hk_time,1,10) ='"+startDate+"' ";
		
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他催收的一审(总数)
	 * @param userId
	 * @return
	 */
	public int getDTDDHNowOneCount(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		sql="select count(*) from sd_returnvisit inner join sdcms_user on  sdcms_user.USER_ID=sd_returnvisit.kefuid where kefuid="+userId+" and code=1 and substring(visitdate,4,2)="+startDate.substring(3, 5)+" and substring(visitdate,1,10) ='"+startDate+"' ";
		
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他催收的一审(总数)
	 * @param userId
	 * @return
	 */
	public int getJRCSZCount(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_id where cuishou_id="+userId+"  and yuq_ts=0 and ((substring(hkyq_time,4,2)="+startDate.substring(3, 5)+" and substring(hkyq_time,1,10) ='"+startDate+"') ";
		
		sql += " or (substring(hkfq_time,4,2)="+startDate.substring(3, 5)+" and substring(hkfq_time,1,10) ='"+startDate+"')) ";
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他催收的一审(总数)
	 * @param userId
	 * @return
	 */
	public int getMQZBSCount(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_id where cuishou_id="+userId+"  and yuq_ts=0 and sfyhw=0 ";
		
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他催收的二审(总数)
	 * @param userId
	 * @return
	 */
	public int getCSNowTwoCount(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		
			sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_m1 where cuishou_m1="+userId+" and sfyhw=1 and yuq_ts>3 and yuq_ts<8 and substring(hk_time,4,2)="+startDate.substring(3, 5)+" and substring(hk_time,1,10) ='"+startDate+"' ";
		
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他催收的二审(总数)
	 * @param userId
	 * @return
	 */
	public int getCSNowTwoCountM2(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		
		sql="select count(*) from sd_new_jkyx where cuishou_m2="+userId+" and sfyhw=1 and yuq_ts>15 and substring(hk_time,4,2)="+startDate.substring(3, 5)+" and substring(hk_time,1,10) ='"+startDate+"' ";
		
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getCSNowTwoCountM3(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		
		sql="select count(*) from sd_new_jkyx where cuishou_m3="+userId+" and sfyhw=1 and yuq_ts>45 and substring(hk_time,4,2)="+startDate.substring(3, 5)+" and substring(hk_time,1,10) ='"+startDate+"' ";
		
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他催收的二审(总数)
	 * @param userId
	 * @return
	 */
	public int getCSNowTwoCountCSWB(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		
		sql="select count(*) from sd_new_jkyx inner join sdcms_user_cs on  sdcms_user_cs.USER_ID=sd_new_jkyx.cuishouwb where cuishouwb="+userId+" and sfyhw=1 and yuq_ts>3 and yuq_ts<8 and substring(hk_time,4,2)="+startDate.substring(3, 5)+" and substring(hk_time,1,10) ='"+startDate+"' ";
		
		if(!name.equals("")){
			sql+=" and sdcms_user_cs.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user_cs.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他催收的三审(总数)
	 * @param userId
	 * @return
	 */
	public int getCSNowThreeCount(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		
			sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_m1 where cuishou_m1="+userId+" and sfyhw=1 and yuq_ts>7 and yuq_ts<31 and substring(hk_time,4,2)="+startDate.substring(3, 5)+" and substring(hk_time,1,10) ='"+startDate+"' ";
		
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他催收的三审(总数)
	 * @param userId
	 * @return
	 */
	public int getCSNowThreeCountCSWB(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		
		sql="select count(*) from sd_new_jkyx inner join sdcms_user_cs on  sdcms_user_cs.USER_ID=sd_new_jkyx.cuishouwb where cuishouwb="+userId+" and sfyhw=1 and yuq_ts>7 and yuq_ts<31 and substring(hk_time,4,2)="+startDate.substring(3, 5)+" and substring(hk_time,1,10) ='"+startDate+"' ";
		
		if(!name.equals("")){
			sql+=" and sdcms_user_cs.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user_cs.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他催收的四审(总数)
	 * @param userId
	 * @return
	 */
	public int getCSNowFourCount(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		
			sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_m1 where cuishou_m1="+userId+" and cuishou_id="+userId+" and sfyhw=1 and yuq_ts>30 and substring(hk_time,4,2)="+startDate.substring(3, 5)+" and substring(hk_time,1,10) ='"+startDate+"' ";
		
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他催收的四审(总数)
	 * @param userId
	 * @return
	 */
	public int getCSNowFourCountCSWB(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		
		sql="select count(*) from sd_new_jkyx inner join sdcms_user_cs on  sdcms_user_cs.USER_ID=sd_new_jkyx.cuishouwb where cuishouwb="+userId+" and sfyhw=1 and yuq_ts>30 and substring(hk_time,4,2)="+startDate.substring(3, 5)+" and substring(hk_time,1,10) ='"+startDate+"' ";
		
		if(!name.equals("")){
			sql+=" and sdcms_user_cs.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user_cs.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他催收的总数(总数)
	 * @param userId
	 * @return
	 */
	public int getCSDateNowCount(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		if(startDate.substring(3, 5).equals(endDate.substring(3, 5))){
			sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_m1 where cuishou_m1="+userId+" and cuishou_id="+userId+" and sfyhw=1 and yuq_ts>0 and substring(hk_time,4,2)="+startDate.substring(3, 5)+" and hk_time BETWEEN '"+startDate+"' and '"+endDate+"' ";
		}else{
			sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_m1 where cuishou_m1="+userId+" and cuishou_id="+userId+" and sfyhw=1 and yuq_ts>0 and substring(hk_time,4,2)="+startDate.substring(3, 5)+" and hk_time>'"+startDate+"' ";
		}
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		
		return this.getJdbcTemplate().queryInt(sql);
	}
	public double getCSDateNowCountDTJEM0(int userId,String startDate,String endDate,String name,String phone){
		String sql= "select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where r.remark<>'延期还款' and j.cuishou_m0='"+userId+"' and r.cuishouid<>0 and r.dqyqts>0 and r.dqyqts<4 and SUBSTRING(r.rechargetime,1,10)='"+startDate+"' " ;
		
		return this.getJdbcTemplate().queryLong(sql);
	}
	public double getCSDateNowCountDTJE(int userId,String startDate,String endDate,String name,String phone){
		String sql= "select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where r.remark<>'延期还款' and j.cuishou_m1='"+userId+"' and r.cuishouid<>0 and r.dqyqts>0 and r.dqyqts<16 and SUBSTRING(r.rechargetime,1,10)='"+startDate+"' " ;
		
		return this.getJdbcTemplate().queryLong(sql);
	}
	public double getCSDateNowCountDTJEM2(int userId,String startDate,String endDate,String name,String phone){
		String sql= "select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where j.cuishou_m2='"+userId+"' and j.cuishou_id='"+userId+"' and r.cuishouid<>0 and r.dqyqts>15 and SUBSTRING(r.rechargetime,1,10)='"+startDate+"' " ;
		
		return this.getJdbcTemplate().queryLong(sql);
	}
	public double getCSDateNowCountDTJEM3(int userId,String startDate,String endDate,String name,String phone){
		String sql= "select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where j.cuishou_m3='"+userId+"' and j.cuishou_id='"+userId+"' and r.cuishouid<>0 and r.dqyqts>45 and SUBSTRING(r.rechargetime,1,10)='"+startDate+"' " ;
		
		return this.getJdbcTemplate().queryLong(sql);
	}
	public int getCSDateNowCountM0(int userId,String startDate,String endDate,String name,String phone){
		String sql= "select count(r.id) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where (r.remark='全额还款' or r.remark='完成还款，部分逾期利息还款') and r.dqyqts>0 and j.sfyhw=1 and j.cuishou_id='"+userId+"' and j.cuishou_m0='"+userId+"' and substring(r.rechargetime,1,10)='"+startDate+"' ";
		
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getCSDateNowCountM1(int userId,String startDate,String endDate,String name,String phone){
		String sql= "select count(r.id) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where (r.remark='全额还款' or r.remark='完成还款，部分逾期利息还款') and r.dqyqts>0 and j.sfyhw=1 and j.cuishou_id='"+userId+"' and j.cuishou_m1='"+userId+"' and substring(r.rechargetime,1,10)='"+startDate+"' ";
		
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他催收的总数(总数)
	 * @param userId
	 * @return
	 */
	public int getCSDateNowCountM2(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		if(startDate.substring(3, 5).equals(endDate.substring(3, 5))){
			sql="select count(*) from sd_new_jkyx where cuishou_m2="+userId+" and cuishou_id="+userId+" and sfyhw=1 and yuq_ts>15 and substring(hk_time,4,2)="+startDate.substring(3, 5)+" and hk_time BETWEEN '"+startDate+"' and '"+endDate+"' ";
		}else{
			sql="select count(*) from sd_new_jkyx where cuishou_m2="+userId+" and cuishou_id="+userId+" and sfyhw=1 and yuq_ts>15 and substring(hk_time,4,2)="+startDate.substring(3, 5)+" and hk_time>'"+startDate+"' ";
		}
		
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getCSDateNowCountM3(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		if(startDate.substring(3, 5).equals(endDate.substring(3, 5))){
			sql="select count(*) from sd_new_jkyx where cuishou_m3="+userId+" and cuishou_id="+userId+" and sfyhw=1 and yuq_ts>45 and substring(hk_time,4,2)="+startDate.substring(3, 5)+" and hk_time BETWEEN '"+startDate+"' and '"+endDate+"' ";
		}else{
			sql="select count(*) from sd_new_jkyx where cuishou_m3="+userId+" and cuishou_id="+userId+" and sfyhw=1 and yuq_ts>45 and substring(hk_time,4,2)="+startDate.substring(3, 5)+" and hk_time>'"+startDate+"' ";
		}
		
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他催收的总数(总数)
	 * @param userId
	 * @return
	 */
	public int getCSDateNowCountCSWB(int userId,String startDate,String endDate,String name,String phone){
		String sql= "";
		if(startDate.substring(3, 5).equals(endDate.substring(3, 5))){
			sql="select count(*) from sd_new_jkyx inner join sdcms_user_cs on  sdcms_user_cs.USER_ID=sd_new_jkyx.cuishouwb where cuishouwb="+userId+" and sfyhw=1 and yuq_ts>0 and substring(hk_time,4,2)="+startDate.substring(3, 5)+" and hk_time BETWEEN '"+startDate+"' and '"+endDate+"' ";
		}else{
			sql="select count(*) from sd_new_jkyx inner join sdcms_user_cs on  sdcms_user_cs.USER_ID=sd_new_jkyx.cuishouwb where cuishouwb="+userId+" and sfyhw=1 and yuq_ts>0 and substring(hk_time,4,2)="+startDate.substring(3, 5)+" and hk_time>'"+startDate+"' ";
		}
		if(!name.equals("")){
			sql+=" and sdcms_user_cs.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user_cs.phone like '%"+phone+"%' ";
		}
		
		return this.getJdbcTemplate().queryInt(sql);
	}

	/**
	 * 根据用户ID查找他催收的总数(总数)
	 * @param userId
	 * @return
	 */
	public int getCSNowCount(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_m1 where cuishou_m1="+userId+" and yuq_ts>0 " ;
		
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and (((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) <'" + startDate + "' ";
			sql += " or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) <'" + startDate+ "') ";
			sql += " and sfyhw=0 ) ";
			sql += " or ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "')))";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getCSNowCountM0(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx where cuishou_m0="+userId+" and yuq_ts>0 " ;
		
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and (((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) <'" + startDate + "' ";
			sql += " or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) <'" + startDate+ "') ";
			sql += " and sfyhw=0 ) ";
			sql += " or ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "')))";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getCSNowCountM1(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx where cuishou_m1="+userId+" and yuq_ts>0 " ;
		
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and (((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) <'" + startDate + "' ";
			sql += " or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) <'" + startDate+ "') ";
			sql += " and sfyhw=0 ) ";
			sql += " or ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "')))";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getCSNowCountM0SS(int userId,String startDate,String endDate,String name,String phone,int number){
		String sql="select count(*) from sd_new_jkyx where cuishou_m0="+userId+" and yuq_ts>0 and yuq_ts<4 and hkfq_code=0 ";
		
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
		}else{
			String time="";
			String time2="";
			if(number < 10){
				time = "0" + number;
				time2 = "0" + (number+1);
			}
			sql +=	" and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))>='2019-"+time+"-01' " +
					" and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))<'2019-"+time2+"-01' )" +
					" or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))>='2019-"+time+"-01' " +
					" and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))<'2019-"+time2+"-01' ))" ;
		}
		
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getCSNowCountM1SS(int userId,String startDate,String endDate,String name,String phone,int number){
		String sql="select count(*) from sd_new_jkyx where cuishou_m1="+userId+" and yuq_ts>0 and hkfq_code=0 ";
				
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
		}else{
			String time="";
			String time2="";
			if(number < 10){
				time = "0" + number;
				time2 = "0" + (number+1);
			}
			sql +=	" and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))>='2019-"+time+"-01' " +
					" and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))<'2019-"+time2+"-01' )" +
					" or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))>='2019-"+time+"-01' " +
					" and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))<'2019-"+time2+"-01' ))" ;
		}
		
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getCSNowCountM2SS(int userId,String startDate,String endDate,String name,String phone,int number){
		String sql="select count(*) from sd_new_jkyx where cuishou_m2="+userId+" and yuq_ts>15 and yuq_ts<46 and hkfq_code=0 ";
		
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getCSNowCountM3SS(int userId,String startDate,String endDate,String name,String phone,int number){
		String sql="select count(*) from sd_new_jkyx where cuishou_m3="+userId+" and yuq_ts>45 and hkfq_code=0 ";
		
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他催收的总数(总数)
	 * @param userId
	 * @return
	 */
	public int getCSNowCountM2(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx where cuishou_id="+userId+" and yuq_ts>0 and hkfq_code=0 " ;
		
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and (((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) <'" + startDate + "' ";
			sql += " or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) <'" + startDate+ "') ";
			sql += " and sfyhw=0 ) ";
			sql += " or ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "')))";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getCSNowCountM3(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx where cuishou_id="+userId+" and yuq_ts>0 and hkfq_code=0 " ;
		
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and (((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) <'" + startDate + "' ";
			sql += " or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) <'" + startDate+ "') ";
			sql += " and sfyhw=0 ) ";
			sql += " or ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "')))";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他催收的总数(总数)
	 * @param userId
	 * @return
	 */
	public int getCSNowCountCSWB(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx inner join sdcms_user_cs on  sdcms_user_cs.USER_ID=sd_new_jkyx.cuishouwb where cuishouwb="+userId+" and yuq_ts>0 " ;
		
		if(!name.equals("")){
			sql+=" and sdcms_user_cs.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user_cs.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and (((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) <'" + startDate + "' ";
			sql += " or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) <'" + startDate+ "') ";
			sql += " and sfyhw=0 ) ";
			sql += " or ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "')))";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他催收的总数(总数)
	 * @param userId
	 * @return
	 */
	public int getCSNowCountTX(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_tx where cuishou_tx="+userId ;
		
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and (((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) <'" + startDate + "' ";
			sql += " or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) <'" + startDate+ "') ";
			sql += " and sfyhw=0 ) ";
			sql += " or ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "')))";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他催收的总数(总数)
	 * @param userId
	 * @return
	 */
	public int getJRCSNowCount(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_tx where cuishou_tx=? and yuq_ts=0 " ;
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
		
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他催收的总数(总数)
	 * @param userId
	 * @return
	 */
	public int getDYCSNowCount(int userId,String startDate,String endDate,String nowdate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_m1 where cuishou_m1=? and yuq_ts>0 " ;
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
		}else{
			sql += " and (substring(hkyq_time,4,2) ='" + nowdate.substring(3, 5) + "'";
			sql += " or substring(hkfq_time,4,2) ='" + nowdate.substring(3, 5) + "')";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	public int getDYCSNowCountM0(int userId,String startDate,String endDate,String nowdate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx where cuishou_m0=? and yuq_ts>0 " ;
		Object[] object=new Object[]{userId};
		
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
		}else{
			sql += " and (substring(hkyq_time,4,2) ='" + nowdate.substring(3, 5) + "'";
			sql += " or substring(hkfq_time,4,2) ='" + nowdate.substring(3, 5) + "')";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	public int getDYCSNowCountM1(int userId,String startDate,String endDate,String nowdate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx where cuishou_m1=? and yuq_ts>0 " ;
		Object[] object=new Object[]{userId};
		
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
		}else{
			sql += " and (substring(hkyq_time,4,2) ='" + nowdate.substring(3, 5) + "'";
			sql += " or substring(hkfq_time,4,2) ='" + nowdate.substring(3, 5) + "')";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他催收的总数(总数)
	 * @param userId
	 * @return
	 */
	public int getDYCSNowCountM2(int userId,String startDate,String endDate,String nowdate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx where cuishou_m2=? and yuq_ts>15 " ;
		Object[] object=new Object[]{userId};
		
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
		}else{
			sql += " and (substring(hkyq_time,4,2) ='" + nowdate.substring(3, 5) + "'";
			sql += " or substring(hkfq_time,4,2) ='" + nowdate.substring(3, 5) + "')";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	public int getDYCSNowCountM3(int userId,String startDate,String endDate,String nowdate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx where cuishou_m3=? and yuq_ts>45 " ;
		Object[] object=new Object[]{userId};
		
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
		}else{
			sql += " and (substring(hkyq_time,4,2) ='" + nowdate.substring(3, 5) + "'";
			sql += " or substring(hkfq_time,4,2) ='" + nowdate.substring(3, 5) + "')";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他催收的总数(总数)
	 * @param userId
	 * @return
	 */
	public int getDYCSNowCountCSWB(int userId,String startDate,String endDate,String nowdate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx inner join sdcms_user_cs on  sdcms_user_cs.USER_ID=sd_new_jkyx.cuishouwb where cuishouwb=? and yuq_ts>0 " ;
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user_cs.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user_cs.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
		}else{
			sql += " and (substring(hkyq_time,4,2) ='" + nowdate.substring(3, 5) + "'";
			sql += " or substring(hkfq_time,4,2) ='" + nowdate.substring(3, 5) + "')";
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他催收的总数(总数)
	 * @param userId
	 * @return
	 */
	public int getJRCSCount(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_tx where cuishou_tx=? and yuq_ts=0 " ;
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
		}
		sql += " and sfyhw=1 ";
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他催收的总数(总数)
	 * @param userId
	 * @return
	 */
	public int getDYCSCount(int userId,String startDate,String endDate,String nowdate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_m1 where cuishou_m1=? and cuishou_id=? and yuq_ts>0 " ;
		Object[] object=new Object[]{userId,userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
		}else{
			sql += " and (substring(hkyq_time,4,2) ='" + nowdate.substring(3, 5) + "' ";
			sql += " or substring(hkfq_time,4,2) ='" + nowdate.substring(3, 5) + "' )";
		}
		sql += " and sfyhw=1 ";
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	public int getDYCSCountM0(int userId,String startDate,String endDate,String nowdate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx where cuishou_m0=? and cuishou_id=? and yuq_ts>0 " ;
		Object[] object=new Object[]{userId,userId};
		
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
		}else{
			sql += " and (substring(hkyq_time,4,2) ='" + nowdate.substring(3, 5) + "' ";
			sql += " or substring(hkfq_time,4,2) ='" + nowdate.substring(3, 5) + "' )";
		}
		sql += " and sfyhw=1 ";
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	public int getDYCSCountM1(int userId,String startDate,String endDate,String nowdate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx where cuishou_m1=? and cuishou_id=? and yuq_ts>0 " ;
		Object[] object=new Object[]{userId,userId};
		
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
		}else{
			sql += " and (substring(hkyq_time,4,2) ='" + nowdate.substring(3, 5) + "' ";
			sql += " or substring(hkfq_time,4,2) ='" + nowdate.substring(3, 5) + "' )";
		}
		sql += " and sfyhw=1 ";
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他催收的总数(总数)
	 * @param userId
	 * @return
	 */
	public int getDYCSCountM2(int userId,String startDate,String endDate,String nowdate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx where cuishou_m2=? and cuishou_id=? and yuq_ts>15 " ;
		Object[] object=new Object[]{userId,userId};
		
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
		}else{
			sql += " and (substring(hkyq_time,4,2) ='" + nowdate.substring(3, 5) + "' ";
			sql += " or substring(hkfq_time,4,2) ='" + nowdate.substring(3, 5) + "' )";
		}
		sql += " and sfyhw=1 ";
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	public int getDYCSCountM3(int userId,String startDate,String endDate,String nowdate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx where cuishou_m3=? and cuishou_id=? and yuq_ts>45 " ;
		Object[] object=new Object[]{userId,userId};
		
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
		}else{
			sql += " and (substring(hkyq_time,4,2) ='" + nowdate.substring(3, 5) + "' ";
			sql += " or substring(hkfq_time,4,2) ='" + nowdate.substring(3, 5) + "' )";
		}
		sql += " and sfyhw=1 ";
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他催收的总数(总数)
	 * @param userId
	 * @return
	 */
	public int getDYCSCountCSWB(int userId,String startDate,String endDate,String nowdate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx inner join sdcms_user_cs on  sdcms_user_cs.USER_ID=sd_new_jkyx.cuishouwb where cuishouwb=? and yuq_ts>0 " ;
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user_cs.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user_cs.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
		}else{
			sql += " and (substring(hkyq_time,4,2) ='" + nowdate.substring(3, 5) + "' ";
			sql += " or substring(hkfq_time,4,2) ='" + nowdate.substring(3, 5) + "' )";
		}
		sql += " and sfyhw=1 ";
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他催收的M1金额(总数)
	 * @param userId
	 * @return
	 */
	public double getOneMoney(int userId,String startDate,String endDate,String name,String phone){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id left join sdcms_user u on  u.USER_ID=j.cuishou_m1 where j.cuishou_m1=? and j.sfyhw=1 and j.yuq_ts>0 and j.yuq_ts<4" ;
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and u.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and u.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
		
		

	}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getOneMoneyM0(int userId,String startDate,String endDate,String name,String phone){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where j.cuishou_m0=? and j.sfyhw=1 and j.yuq_ts>0 and j.yuq_ts<4 " ;
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and u.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and u.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
			
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getOneMoneyM1(int userId,String startDate,String endDate,String name,String phone){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where j.cuishou_m1=? and j.sfyhw=1 and j.yuq_ts>0 and j.yuq_ts<16 " ;
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and u.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and u.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
			
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	/**
	 * 根据用户ID查找他催收的M1金额(总数)
	 * @param userId
	 * @return
	 */
	public double getOneMoneyCSWB(int userId,String startDate,String endDate,String name,String phone){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id left join sdcms_user_cs u on  u.USER_ID=j.cuishouwb where j.cuishouwb=? and j.sfyhw=1 and j.yuq_ts>0 and j.yuq_ts<4" ;
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and u.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and u.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
			
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	/**
	 * 根据用户ID查找他催收的M2金额(总数)
	 * @param userId
	 * @return
	 */
	public double getTwoMoney(int userId,String startDate,String endDate,String name,String phone){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id left join sdcms_user u on  u.USER_ID=j.cuishou_m1 where j.cuishou_m1=? and j.sfyhw=1 and j.yuq_ts>3 and j.yuq_ts<8" ;
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and u.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and u.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
		
		

	}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getTwoMoneyM2(int userId,String startDate,String endDate,String name,String phone){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where j.cuishou_m2=? and j.sfyhw=1 and j.yuq_ts>15 " ;
		Object[] object=new Object[]{userId};
		
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
			
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getTwoMoneyM3(int userId,String startDate,String endDate,String name,String phone){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where j.cuishou_m3=? and j.sfyhw=1 and j.yuq_ts>45 " ;
		Object[] object=new Object[]{userId};
		
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
			
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	/**
	 * 根据用户ID查找他催收的M2金额(总数)
	 * @param userId
	 * @return
	 */
	public double getTwoMoneyCSWB(int userId,String startDate,String endDate,String name,String phone){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id left join sdcms_user_cs u on  u.USER_ID=j.cuishouwb where j.cuishouwb=? and j.sfyhw=1 and j.yuq_ts>3 and j.yuq_ts<8" ;
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and u.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and u.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
			
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	/**
	 * 根据用户ID查找他催收的M3金额(总数)
	 * @param userId
	 * @return
	 */
	public double getThreeMoney(int userId,String startDate,String endDate,String name,String phone){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id left join sdcms_user u on  u.USER_ID=j.cuishou_m1 where j.cuishou_m1=? and j.sfyhw=1 and j.yuq_ts>7 and j.yuq_ts<31" ;
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and u.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and u.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
		
		

	}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	/**
	 * 根据用户ID查找他催收的M3金额(总数)
	 * @param userId
	 * @return
	 */
	public double getThreeMoneyCSWB(int userId,String startDate,String endDate,String name,String phone){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id left join sdcms_user_cs u on  u.USER_ID=j.cuishouwb where j.cuishouwb=? and j.sfyhw=1 and j.yuq_ts>7 and j.yuq_ts<31" ;
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and u.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and u.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
			
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	/**
	 * 根据用户ID查找他催收的M4金额(总数)
	 * @param userId
	 * @return
	 */
	public double getThreeTGMoney(int userId,String startDate,String endDate,String name,String phone){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id left join sdcms_user u on  u.USER_ID=j.cuishou_m1 where j.cuishou_m1=? and j.cuishou_id=? and j.sfyhw=1 and j.yuq_ts>30 " ;
		Object[] object=new Object[]{userId,userId};
		if(!name.equals("")){
			sql+=" and u.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and u.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
		
		

	}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	/**
	 * 根据用户ID查找他催收的M4金额(总数)
	 * @param userId
	 * @return
	 */
	public double getThreeTGMoneyCSWB(int userId,String startDate,String endDate,String name,String phone){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id left join sdcms_user_cs u on  u.USER_ID=j.cuishouwb where j.cuishouwb=? and j.sfyhw=1 and j.yuq_ts>30 " ;
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and u.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and u.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
			
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	/**
	 * 根据用户ID查找他催收的金额(总数)
	 * @param userId
	 * @return
	 */
	public double getCSJE(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(sjds_money, ',' ,'')+replace(lx, ',' ,'')+replace(yuq_lx, ',' ,'')+replace(yuq_yhlx, ',' ,'')+replace(hkfqzlx, ',' ,'')) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_m1 where cuishou_m1=? and yuq_ts>0 ";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and (((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) <'" + startDate + "' ";
			sql += " or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) <'" + startDate+ "') ";
			sql += " and sfyhw=0 ) ";
			sql += " or ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "')))";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getCSJEM0(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(sjds_money, ',' ,'')+replace(lx, ',' ,'')+replace(yuq_lx, ',' ,'')+replace(yuq_yhlx, ',' ,'')+replace(hkfqzlx, ',' ,'')) from sd_new_jkyx where cuishou_m0=? and yuq_ts>0 ";
		Object[] object=new Object[]{userId};
		
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and (((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) <'" + startDate + "' ";
			sql += " or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) <'" + startDate+ "') ";
			sql += " and sfyhw=0 ) ";
			sql += " or ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "')))";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getCSJEM1(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(sjds_money, ',' ,'')+replace(lx, ',' ,'')+replace(yuq_lx, ',' ,'')+replace(yuq_yhlx, ',' ,'')+replace(hkfqzlx, ',' ,'')) from sd_new_jkyx where cuishou_m1=? and yuq_ts>0 ";
		Object[] object=new Object[]{userId};
		
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and (((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) <'" + startDate + "' ";
			sql += " or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) <'" + startDate+ "') ";
			sql += " and sfyhw=0 ) ";
			sql += " or ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "')))";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getYQ1T(int userId,String startDate,String endDate,String name,String phone,String yuqts,int number){
		String sql="SELECT COUNT(*) FROM sd_new_jkyx WHERE sfyhw=1 AND sfyfk=1 AND yuq_ts=1 AND cuishou_id="+ userId;
		if(!StringHelper.isEmpty(startDate)){
			sql +=" AND ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='"+startDate+"' AND CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='"+endDate+"') OR (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='"+startDate+"' AND CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='"+endDate+"'))";
		}else{
			sql +=" AND ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='2019-03-01' AND CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='2019-11-30') OR (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='2019-03-01' AND CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='2019-11-30'))";
		}
		return this.getJdbcTemplate().queryLong(sql);
	}
	public double getYQ2T(int userId,String startDate,String endDate,String name,String phone,String yuqts,int number){
		String sql="SELECT COUNT(*) FROM sd_new_jkyx WHERE sfyhw=1 AND sfyfk=1 AND yuq_ts=2 AND cuishou_id="+ userId;
		if(!StringHelper.isEmpty(startDate)){
			sql +=" AND ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='"+startDate+"' AND CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='"+endDate+"') OR (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='"+startDate+"' AND CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='"+endDate+"'))";
		}else{
			sql +=" AND ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='2019-03-01' AND CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='2019-11-30') OR (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='2019-03-01' AND CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='2019-11-30'))";
		}
		return this.getJdbcTemplate().queryLong(sql);
	}
	public double getYQ3T(int userId,String startDate,String endDate,String name,String phone,String yuqts,int number){
		String sql="SELECT COUNT(*) FROM sd_new_jkyx WHERE sfyhw=1 AND sfyfk=1 AND yuq_ts=3 AND cuishou_id="+ userId;
		if(!StringHelper.isEmpty(startDate)){
			sql +=" AND ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='"+startDate+"' AND CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='"+endDate+"') OR (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='"+startDate+"' AND CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='"+endDate+"'))";
		}else{
			sql +=" AND ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='2019-03-01' AND CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='2019-11-30') OR (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='2019-03-01' AND CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='2019-11-30'))";
		}
		return this.getJdbcTemplate().queryLong(sql);
	}
	public double getCSJEM1SSBG(int userId,String startDate,String endDate,String name,String phone,String yuqts,int number){
		String sql="select SUM(totaljine) from sd_accountuserhk where csid=? ";
		
		Object[] object=new Object[]{userId};
		
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and time>='" + startDate + "'";
			sql += " and time<='" + endDate + "'";
			
		}else{
			sql += " and substring(time,6,2)="+number;
		}
		
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getCSJEM1SS(int userId,String startDate,String endDate,String name,String phone,String yuqts,int number){
		String sql="select SUM(replace(sjsh_money, ',' ,'')+replace(yuq_lx, ',' ,'')) from sd_new_jkyx where cuishou_m1=? and yuq_ts>0 and hkfq_code=0 ";
				
		Object[] object=new Object[]{userId};
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
		}else{
			sql +=  " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))>='2019-"+(number-1)+"-31' " +
					" and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))<'2019-"+number+"-30' )" +
					" or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))>='2019-"+(number-1)+"-31' " +
					" and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))<'2019-"+number+"-30' ))" ;
		}
		
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getCSJEM2SSBG(int userId,String startDate,String endDate,String name,String phone,String yuqts,int number){
		String sql="select SUM(totaljine) from sd_accountuserhk where csid=? ";
		
		Object[] object=new Object[]{userId};
		
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and time>='" + startDate + "'";
			sql += " and time<='" + endDate + "'";
			
		}else{
			sql += " and substring(time,6,2)="+number;
		}
		
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getCSJEM3SSBG(int userId,String startDate,String endDate,String name,String phone,String yuqts,int number){
		String sql="select SUM(totaljine) from sd_accountuserhk where csid=? ";
		
		Object[] object=new Object[]{userId};
		
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and time>='" + startDate + "'";
			sql += " and time<='" + endDate + "'";
			
		}else{
			sql += " and substring(time,6,2)="+number;
		}
		
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getCSJEM2SS(int userId,String startDate,String endDate,String name,String phone,String yuqts,int number){
		String sql="select SUM(replace(sjsh_money, ',' ,'')+replace(yuq_lx, ',' ,'')) from sd_new_jkyx where cuishou_id=? and yuq_ts>0 and hkfq_code=0 ";
		
		Object[] object=new Object[]{userId};
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
		}
		
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getCSJEM2(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(sjsh_money, ',' ,'')+replace(yuq_lx, ',' ,'')) from sd_new_jkyx where cuishou_m2=? and yuq_ts>0 ";
		Object[] object=new Object[]{userId};
		
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getCSJEM3(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(sjsh_money, ',' ,'')+replace(yuq_lx, ',' ,'')) from sd_new_jkyx where cuishou_m3=? and yuq_ts>0 ";
		Object[] object=new Object[]{userId};
		
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	/**
	 * 根据用户ID查找他催收的金额(总数)
	 * @param userId
	 * @return
	 */
	public double getCSJECSWB(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(sjds_money, ',' ,'')+replace(lx, ',' ,'')+replace(yuq_lx, ',' ,'')+replace(yuq_yhlx, ',' ,'')+replace(hkfqzlx, ',' ,'')) from sd_new_jkyx inner join sdcms_user_cs on  sdcms_user_cs.USER_ID=sd_new_jkyx.cuishouwb where cuishouwb=? and yuq_ts>0 ";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user_cs.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user_cs.phone like '%"+phone+"%' ";
		}
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and (((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) <'" + startDate + "' ";
			sql += " or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) <'" + startDate+ "') ";
			sql += " and sfyhw=0 ) ";
			sql += " or ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "')))";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	/**
	 * 根据用户ID查找他催收的金额(总数)
	 * @param userId
	 * @return
	 */
	public double getJRCSJE(int userId,String startDate,String endDate,String name,String phone){
		String sql="select SUM(replace(sjds_money, ',' ,'')+replace(lx, ',' ,'')+replace(yuq_lx, ',' ,'')+replace(yuq_yhlx, ',' ,'')+replace(hkfqzlx, ',' ,'')) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_tx where cuishou_tx="+userId ;
		
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
		}
		return this.getJdbcTemplate().queryLong(sql);
	}
	/**
	 * 根据用户ID查找他催收的金额(总数)
	 * @param userId
	 * @return
	 */
	public double getDYCSJE(int userId,String startDate,String endDate,String nowdate,String name,String phone,String yuqts){
		String sql="select SUM(replace(sjds_money, ',' ,'')+replace(lx, ',' ,'')+replace(yuq_lx, ',' ,'')+replace(yuq_yhlx, ',' ,'')) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_m1 where cuishou_m1=? and yuq_ts>0 " ;
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
			
			
		}else{
			sql += " and (substring(hkyq_time,4,2) ='" + nowdate.substring(3, 5) + "' ";
			sql += " or substring(hkfq_time,4,2) ='" + nowdate.substring(3, 5) + "' )";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getDYCSJEM0(int userId,String startDate,String endDate,String nowdate,String name,String phone,String yuqts){
		String sql="select SUM(replace(sjds_money, ',' ,'')+replace(lx, ',' ,'')+replace(yuq_lx, ',' ,'')+replace(yuq_yhlx, ',' ,'')) from sd_new_jkyx where cuishou_m0=? and yuq_ts>0 " ;
		Object[] object=new Object[]{userId};
		
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
			
		}else{
			sql += " and (substring(hkyq_time,4,2) ='" + nowdate.substring(3, 5) + "' ";
			sql += " or substring(hkfq_time,4,2) ='" + nowdate.substring(3, 5) + "' )";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getDYCSJEM1(int userId,String startDate,String endDate,String nowdate,String name,String phone,String yuqts){
		String sql="select SUM(replace(sjds_money, ',' ,'')+replace(lx, ',' ,'')+replace(yuq_lx, ',' ,'')+replace(yuq_yhlx, ',' ,'')) from sd_new_jkyx where cuishou_m1=? and yuq_ts>0 " ;
		Object[] object=new Object[]{userId};
		
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
			
		}else{
			sql += " and (substring(hkyq_time,4,2) ='" + nowdate.substring(3, 5) + "' ";
			sql += " or substring(hkfq_time,4,2) ='" + nowdate.substring(3, 5) + "' )";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getDYCSJEM2(int userId,String startDate,String endDate,String nowdate,String name,String phone,String yuqts){
		String sql="select SUM(replace(sjds_money, ',' ,'')+replace(lx, ',' ,'')+replace(yuq_lx, ',' ,'')+replace(yuq_yhlx, ',' ,'')) from sd_new_jkyx where cuishou_m2=? and yuq_ts>15 " ;
		Object[] object=new Object[]{userId};
		
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
			
		}else{
			sql += " and (substring(hkyq_time,4,2) ='" + nowdate.substring(3, 5) + "' ";
			sql += " or substring(hkfq_time,4,2) ='" + nowdate.substring(3, 5) + "' )";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getDYCSJEM3(int userId,String startDate,String endDate,String nowdate,String name,String phone,String yuqts){
		String sql="select SUM(replace(sjds_money, ',' ,'')+replace(lx, ',' ,'')+replace(yuq_lx, ',' ,'')+replace(yuq_yhlx, ',' ,'')) from sd_new_jkyx where cuishou_m3=? and yuq_ts>45 " ;
		Object[] object=new Object[]{userId};
		
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
			
		}else{
			sql += " and (substring(hkyq_time,4,2) ='" + nowdate.substring(3, 5) + "' ";
			sql += " or substring(hkfq_time,4,2) ='" + nowdate.substring(3, 5) + "' )";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	/**
	 * 根据用户ID查找他催收的金额(总数)
	 * @param userId
	 * @return
	 */
	public double getDYCSJECSWB(int userId,String startDate,String endDate,String nowdate,String name,String phone,String yuqts){
		String sql="select SUM(replace(sjds_money, ',' ,'')+replace(lx, ',' ,'')+replace(yuq_lx, ',' ,'')+replace(yuq_yhlx, ',' ,'')) from sd_new_jkyx inner join sdcms_user_cs on  sdcms_user_cs.USER_ID=sd_new_jkyx.cuishouwb where cuishouwb=? and yuq_ts>0 " ;
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user_cs.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user_cs.phone like '%"+phone+"%' ";
		}
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
			
			
		}else{
			sql += " and (substring(hkyq_time,4,2) ='" + nowdate.substring(3, 5) + "' ";
			sql += " or substring(hkfq_time,4,2) ='" + nowdate.substring(3, 5) + "' )";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	/**
	 * 根据用户ID查找他已催收的金额(总数)
	 * @param userId
	 * @return
	 */
	public double getYCSJE(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id left join sdcms_user u on  u.USER_ID=j.cuishou_m1 where j.cuishou_m1=? and j.cuishou_id=? and j.sfyhw=1 and j.yuq_ts>0 " ;
		Object[] object=new Object[]{userId,userId};
		if(!name.equals("")){
			sql+=" and u.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and u.phone like '%"+phone+"%' ";
		}
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
		
		

	}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getYCSJEM0(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where j.cuishou_m0=? and j.cuishou_id=? and j.sfyhw=1 and j.yuq_ts>0 and j.yuq_ts<4 " ;
		Object[] object=new Object[]{userId,userId};
		
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
			
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getYCSJEM1(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where j.cuishou_m1=? and j.cuishou_id=? and j.sfyhw=1 and j.yuq_ts>0 and j.yuq_ts<16 " ;
		Object[] object=new Object[]{userId,userId};
		
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
			
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getYCSJEM1SSBG(int userId,String startDate,String endDate,String name,String phone,String yuqts,int number){
		String sql="select SUM(ycsjine) from sd_accountuserhk where csid=? " ;
		
		Object[] object=new Object[]{userId};
		
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and time>='" + startDate + "'";
			sql += " and time<='" + endDate + "'";
			
		}else{
			sql += " and substring(time,6,2)="+number;
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getYCSJEM1SS(int userId,String startDate,String endDate,String name,String phone,String yuqts,int number){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where j.cuishou_m1=? and j.cuishou_id=? and j.cuishou_m2=0 and j.sfyhw=1 and j.yuq_ts>0 and r.dqyqts>0 and r.dqyqts<16 AND r.remark<>'延期还款' " ;
				
		Object[] object=new Object[]{userId,userId};
		
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
		}else {
			String time="";
			String time2="";
			if(number < 10){
				time = "0" + number;
				time2 = "0" + (number+1);
			}
			sql +=  " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2))>='2019-"+time+"-01' " +
					" and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2))<'2019-"+time2+"-01' " ;
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getYCSJEM2SSBG(int userId,String startDate,String endDate,String name,String phone,String yuqts,int number){
		String sql="select SUM(ycsjine) from sd_accountuserhk where csid=? ";
		
		Object[] object=new Object[]{userId};
		
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and time>='" + startDate + "'";
			sql += " and time<='" + endDate + "'";
			
		}else{
			sql += " and substring(time,6,2)="+number;
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getYCSJEM3SSBG(int userId,String startDate,String endDate,String name,String phone,String yuqts,int number){
		String sql="select SUM(ycsjine) from sd_accountuserhk where csid=? ";
		
		Object[] object=new Object[]{userId};
		
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and time>='" + startDate + "'";
			sql += " and time<='" + endDate + "'";
			
		}else{
			sql += " and substring(time,6,2)="+number;
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getYCSJEM2SS(int userId,String startDate,String endDate,String name,String phone,String yuqts,int number){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where j.cuishou_m2=? and j.cuishou_id=? and j.sfyhw=1 and j.yuq_ts>15 and r.dqyqts>15 AND r.remark<>'延期还款' " ;
		
		Object[] object=new Object[]{userId,userId};
		
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
		}else {
			String time="";
			String time2="";
			if(number < 10){
				time = "0" + number;
				time2 = "0" + (number+1);
			}
			sql +=  " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2))>='2019-"+time+"-01' " +
					" and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2))<'2019-"+time2+"-01' " ;
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getYCSJEM2(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where j.cuishou_m2=? and j.cuishou_id=? and j.sfyhw=1 and j.yuq_ts>15 and r.dqyqts>15 and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2))>'2019-10-01' " ;
		Object[] object=new Object[]{userId,userId};
		
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getYCSJEM3(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where j.cuishou_m3=? and j.cuishou_id=? and j.sfyhw=1 and j.yuq_ts>45 and r.dqyqts>45 and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2))>'2019-10-01' " ;
		Object[] object=new Object[]{userId,userId};
		
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	/**
	 * 根据用户ID查找他已催收的金额(总数)
	 * @param userId
	 * @return
	 */
	public double getYCSJECSWB(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id left join sdcms_user_cs u on  u.USER_ID=j.cuishouwb where j.cuishouwb=? and j.sfyhw=1 and j.yuq_ts>0 and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) >='2019-09-27'";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and u.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and u.phone like '%"+phone+"%' ";
		}
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
			
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	/**
	 * 根据用户ID查找他已催收的延期金额(总数)
	 * @param userId
	 * @return
	 */
	public double getYCSJEYQ(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(sjds_money, ',' ,'')+replace(lx, ',' ,'')+replace(yuq_lx, ',' ,'')+replace(yuq_yhlx, ',' ,'')+replace(hkfqzlx, ',' ,'')) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_m1 where cuishou_m1=? and cuishou_id=? and sfyhw=0 and hkfq_code=1 and yuq_ts>0 " ;
		Object[] object=new Object[]{userId,userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2),SUBSTRING(hk_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2)) <='" + endDate + "'";
		
		

	    }
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getYCSJEYQM0(int userId,String startDate,String endDate,String name,String phone,String yuqts,int number){
		String sql="select SUM(replace(sjsh_money, ',' ,'')+replace(yuq_lx, ',' ,'')) from sd_new_jkyx j left join sd_recharge_detail r on r.rechargenumber=j.id where r.cuishouid=? and r.dqyqts>0 and r.dqyqts<4 and r.remark='延期还款' " ;
		Object[] object=new Object[]{userId};
		
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2),SUBSTRING(rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2)) <='" + endDate + "'";
			
		}else{
			String time="";
			String time2="";
			if(number < 10){
				time = "0" + number;
				time2 = "0" + (number+1);
			}
			sql +=	" and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2))>='2019-"+time+"-01' " +
					" and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2))<'2019-"+time2+"-01' " ;
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getYCSJEYQM1(int userId,String startDate,String endDate,String name,String phone,String yuqts,int number){
		String sql="select SUM(replace(sjsh_money, ',' ,'')+replace(yuq_lx, ',' ,'')) from sd_new_jkyx j left join sd_recharge_detail r on r.rechargenumber=j.id where r.cuishouid=? and r.dqyqts>0 and r.dqyqts<16 and r.remark='延期还款' " ;
		Object[] object=new Object[]{userId};
		
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2),SUBSTRING(rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2)) <='" + endDate + "'";
			
		}else{
			String time="";
			String time2="";
			if(number < 10){
				time = "0" + number;
				time2 = "0" + (number+1);
			}
			sql +=	" and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2))>='2019-"+time+"-01' " +
					" and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2))<'2019-"+time2+"-01' " ;
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getYCSJEYQM1SS(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(sjds_money, ',' ,'')+replace(lx, ',' ,'')+replace(yuq_lx, ',' ,'')+replace(yuq_yhlx, ',' ,'')+replace(hkfqzlx, ',' ,'')) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_m1 where cuishou_m1=? and cuishou_id=? and sfyhw=0 and hkfq_code=1 and yuq_ts>0 and yuq_ts<11 " ;
		Object[] object=new Object[]{userId,userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2),SUBSTRING(hk_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2)) <='" + endDate + "'";
			
			
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getYCSJEYQM2(int userId,String startDate,String endDate,String name,String phone,String yuqts,int number){
		
		String sql="select SUM(replace(sjsh_money, ',' ,'')+replace(yuq_lx, ',' ,'')) from sd_new_jkyx j left join sd_recharge_detail r on r.rechargenumber=j.id where r.cuishouid=? and r.dqyqts>15 and r.remark='延期还款' " ;
		Object[] object=new Object[]{userId};
		
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2),SUBSTRING(rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2)) <='" + endDate + "'";
			
		}else{
			String time="";
			String time2="";
			if(number < 10){
				time = "0" + number;
				time2 = "0" + (number+1);
			}
			sql +=	" and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2))>='2019-"+time+"-01' " +
					" and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2))<'2019-"+time2+"-01' " ;
		}
		
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getYCSJEYQM3(int userId,String startDate,String endDate,String name,String phone,String yuqts,int number){
		
		String sql="select SUM(replace(sjsh_money, ',' ,'')+replace(yuq_lx, ',' ,'')) from sd_new_jkyx j left join sd_recharge_detail r on r.rechargenumber=j.id where r.cuishouid=? and r.dqyqts>45 and r.remark='延期还款' " ;
		Object[] object=new Object[]{userId};
		
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2),SUBSTRING(rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2)) <='" + endDate + "'";
			
		}else{
			String time="";
			String time2="";
			if(number < 10){
				time = "0" + number;
				time2 = "0" + (number+1);
			}
			sql +=	" and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2))>='2019-"+time+"-01' " +
					" and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2))<'2019-"+time2+"-01' " ;
		}
		
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	/**
	 * 根据用户ID查找他已催收的延期金额(总数)
	 * @param userId
	 * @return
	 */
	public double getYCSJEYQCSWB(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(sjds_money, ',' ,'')+replace(lx, ',' ,'')+replace(yuq_lx, ',' ,'')+replace(yuq_yhlx, ',' ,'')+replace(hkfqzlx, ',' ,'')) from sd_new_jkyx inner join sdcms_user_cs on  sdcms_user_cs.USER_ID=sd_new_jkyx.cuishouwb where cuishouwb=? and sfyhw=0 and hkfq_code=1 and yuq_ts>0 and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2)) >='2019-09-27' " ;
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user_cs.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user_cs.phone like '%"+phone+"%' ";
		}
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2),SUBSTRING(hk_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2)) <='" + endDate + "'";
			
			
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	/**
	 * 根据用户ID查找他已催收的延期金额(总数)
	 * @param userId
	 * @return
	 */
	public double getYCSJEBF(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id left join sdcms_user u on  u.USER_ID=j.cuishou_m1 where j.cuishou_m1=? and j.cuishou_id=? and j.sfyhw=0 and j.yuq_ts>0 and j.hkfq_code=0 ";
		Object[] object=new Object[]{userId,userId};
		if(!name.equals("")){
			sql+=" and u.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and u.phone like '%"+phone+"%' ";
		}
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
			
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getYCSJEBFM0(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where j.cuishou_m0=? and r.remark<>'延期还款' and j.sfyhw=0 and j.yuq_ts>0 and j.yuq_ts<4  and j.hkfq_code=0 ";
		Object[] object=new Object[]{userId};
		
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
			
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getYCSJEBFM1(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where j.cuishou_m1=? and r.remark<>'延期还款' and j.sfyhw=0 and j.yuq_ts>0 and j.yuq_ts<16  and j.hkfq_code=0 ";
		Object[] object=new Object[]{userId};
		
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
			
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getYCSJEBFM1SS(int userId,String startDate,String endDate,String name,String phone,String yuqts,int number){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where j.cuishou_m1=? and j.sfyhw=0 and j.yuq_ts>0 and r.dqyqts>0 and r.dqyqts<16  and r.remark<>'延期还款' " ;
				
		Object[] object=new Object[]{userId};
		
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
		}else{
			String time="";
			String time2="";
			if(number < 10){
				time = "0" + number;
				time2 = "0" + (number+1);
			}
			sql +=  " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2))>='2019-"+time+"-01' "+
					" and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2))<'2019-"+time2+"-01' ";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getYCSJEBFM2SS(int userId,String startDate,String endDate,String name,String phone,String yuqts,int number){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where j.cuishou_m2=? and j.cuishou_id=? and j.sfyhw=0 and j.yuq_ts>15 and r.dqyqts>15 and r.remark<>'延期还款' " ;
		
		Object[] object=new Object[]{userId,userId};
		
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
		}else{
			String time="";
			String time2="";
			if(number < 10){
				time = "0" + number;
				time2 = "0" + (number+1);
			}
			sql +=  " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2))>='2019-"+time+"-01' "+
					" and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2))<'2019-"+time2+"-01' ";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getYCSJEBFM2(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where j.cuishou_m2=? and j.cuishou_id=? and j.sfyhw=0 and j.yuq_ts>15 and r.dqyqts>15 and j.hkfq_code=0 and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2))>'2019-09-30' ";
		Object[] object=new Object[]{userId,userId};
		
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getYCSJEBFM3(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where j.cuishou_m3=? and j.cuishou_id=? and j.sfyhw=0 and j.yuq_ts>45 and r.dqyqts>45 and j.hkfq_code=0 and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2))>'2019-09-30' ";
		Object[] object=new Object[]{userId,userId};
		
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	/**
	 * 根据用户ID查找他已催收的延期金额(总数)
	 * @param userId
	 * @return
	 */
	public double getYCSJEBFCSWB(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id left join sdcms_user_cs u on  u.USER_ID=j.cuishouwb where j.cuishouwb=? and j.sfyhw=0 and j.yuq_ts>0 and j.hkfq_code=0 and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) >='2019-09-27' ";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and u.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and u.phone like '%"+phone+"%' ";
		}
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "'";
			
			
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	/**
	 * 根据用户ID查找他已催收的延期金额(总数)
	 * @param userId
	 * @return
	 */
	public Integer getYCSJEYQCount(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select count(id) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_m1 where cuishou_m1=? and cuishou_id=? and sfyhw=0 and hkfq_code=1 and yuq_ts>0 ";
		Object[] object=new Object[]{userId,userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "'";
		
		

	}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	public Integer getYCSJEYQCountM0(int userId,String startDate,String endDate,String name,String phone,String yuqts,int number){
		String sql="select count(id) from sd_recharge_detail where cuishouid=? and dqyqts>0 and dqyqts<4 and remark='延期还款' ";
		Object[] object=new Object[]{userId};
		
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2),SUBSTRING(rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2)) <='" + endDate + "'";
			
		}else{
			String time="";
			String time2="";
			if(number < 10){
				time = "0" + number;
				time2 = "0" + (number+1);
			}
			sql +=	" and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2))>='2019-"+time+"-01' " +
					" and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2))<'2019-"+time2+"-01' " ;
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	public Integer getYCSJEYQCountM1(int userId,String startDate,String endDate,String name,String phone,String yuqts,int number){
		String sql="select count(id) from sd_recharge_detail where cuishouid=? and dqyqts>0 and dqyqts<16 and remark='延期还款' ";
		Object[] object=new Object[]{userId};
		
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2),SUBSTRING(rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2)) <='" + endDate + "'";
			
		}else{
			String time="";
			String time2="";
			if(number < 10){
				time = "0" + number;
				time2 = "0" + (number+1);
			}
			sql +=	" and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2))>='2019-"+time+"-01' " +
					" and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2))<'2019-"+time2+"-01' " ;
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	public Integer getYCSJEYQCountM2(int userId,String startDate,String endDate,String name,String phone,String yuqts,int number){
		
		String sql="select count(id) from sd_recharge_detail where cuishouid=? and dqyqts>16 and remark='延期还款' ";
		Object[] object=new Object[]{userId};
		
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2),SUBSTRING(rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2)) <='" + endDate + "'";
			
		}else{
			String time="";
			String time2="";
			if(number < 10){
				time = "0" + number;
				time2 = "0" + (number+1);
			}
			sql +=	" and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2))>='2019-"+time+"-01' " +
					" and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2))<'2019-"+time2+"-01' " ;
		}
		
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	public Integer getYCSJEYQCountM3(int userId,String startDate,String endDate,String name,String phone,String yuqts,int number){
		
		String sql="select count(id) from sd_recharge_detail where cuishouid=? and dqyqts>45 and remark='延期还款' ";
		Object[] object=new Object[]{userId};
		
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2),SUBSTRING(rechargetime,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2)) <='" + endDate + "'";
			
		}else{
			String time="";
			String time2="";
			if(number < 10){
				time = "0" + number;
				time2 = "0" + (number+1);
			}
			sql +=	" and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2))>='2019-"+time+"-01' " +
					" and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2))<'2019-"+time2+"-01' " ;
		}
		
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他已催收的延期金额(总数)
	 * @param userId
	 * @return
	 */
	public Integer getYCSJEYQCountCSWB(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select count(id) from sd_new_jkyx inner join sdcms_user_cs on  sdcms_user_cs.USER_ID=sd_new_jkyx.cuishouwb where cuishouwb=? and sfyhw=0 and hkfq_code=1 and yuq_ts>0 ";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user_cs.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user_cs.phone like '%"+phone+"%' ";
		}
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "'";
			
			
			
		}
		return this.getJdbcTemplate().queryInt(sql, object);
	}
	/**
	 * 根据用户ID查找他已催收的金额(总数)
	 * @param userId
	 * @return
	 */
	public double getJRYCSJE(int userId,String startDate,String endDate,String name,String phone){
		String sql="select SUM(replace(sjds_money, ',' ,'')+replace(lx, ',' ,'')+replace(yuq_lx, ',' ,'')+replace(yuq_yhlx, ',' ,'')+replace(hkfqzlx, ',' ,'')) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_tx where cuishou_tx=? and sfyhw=1 and yuq_ts=0 " ;
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	/**
	 * 根据用户ID查找他已催收的金额(总数)
	 * @param userId
	 * @return
	 */
	public double getDYYCSJE(int userId,String startDate,String endDate,String nowdate,String name,String phone,String yuqts){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id left join sdcms_user u on  u.USER_ID=j.cuishou_m1 where j.cuishou_m1=? and j.cuishou_id=? and j.sfyhw=1 and j.yuq_ts>0 ";
		Object[] object=new Object[]{userId,userId};
		if(!name.equals("")){
			sql+=" and u.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and u.phone like '%"+phone+"%' ";
		}
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and ((CONCAT(SUBSTRING(j.hkyq_time,7,4),'-',SUBSTRING(j.hkyq_time,4,2),'-',SUBSTRING(j.hkyq_time,1,2),SUBSTRING(j.hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(j.hkyq_time,7,4),'-',SUBSTRING(j.hkyq_time,4,2),'-',SUBSTRING(j.hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(j.hkfq_time,7,4),'-',SUBSTRING(j.hkfq_time,4,2),'-',SUBSTRING(j.hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(j.hkfq_time,7,4),'-',SUBSTRING(j.hkfq_time,4,2),'-',SUBSTRING(j.hkfq_time,1,2)) <='" + endDate + "'))";
			sql += " and (CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "')";
		}else{
			sql += " and (substring(j.hkyq_time,4,2) ='" + nowdate.substring(3, 5) + "' ";
			sql += " or substring(j.hkfq_time,4,2) ='" + nowdate.substring(3, 5) + "' )";
			sql += " and substring(r.rechargetime,4,2) ='" + nowdate.substring(3, 5) + "' ";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getDYYCSJEM0(int userId,String startDate,String endDate,String nowdate,String name,String phone,String yuqts){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where j.cuishou_m0=? and j.cuishou_id=? and j.cuishou_m1=0 and j.sfyhw=1 and j.yuq_ts>0 and r.dqyqts>0 and r.dqyqts<4 AND r.remark<>'延期还款' ";
		Object[] object=new Object[]{userId,userId};
		
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and (CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "')";
		}else{
			sql += " and substring(r.rechargetime,4,2) ='" + nowdate.substring(3, 5) + "' ";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getDYYCSJEM1(int userId,String startDate,String endDate,String nowdate,String name,String phone,String yuqts){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where j.cuishou_m1=? and j.cuishou_id=? and j.cuishou_m2=0 and j.sfyhw=1 and j.yuq_ts>0 and r.dqyqts>0 and r.dqyqts<16 AND r.remark<>'延期还款' ";
		Object[] object=new Object[]{userId,userId};
		
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and (CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "')";
		}else{
			sql += " and substring(r.rechargetime,4,2) ='" + nowdate.substring(3, 5) + "' ";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getDYYCSJEM2(int userId,String startDate,String endDate,String nowdate,String name,String phone,String yuqts){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where j.cuishou_m2=? and j.cuishou_id=? and j.sfyhw=1 and j.yuq_ts>15 and r.dqyqts>15 ";
		Object[] object=new Object[]{userId,userId};
		
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and (CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "')";
		}else{
			sql += " and substring(r.rechargetime,4,2) ='" + nowdate.substring(3, 5) + "' ";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getDYYCSJEM3(int userId,String startDate,String endDate,String nowdate,String name,String phone,String yuqts){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where j.cuishou_m3=? and j.cuishou_id=? and j.sfyhw=1 and j.yuq_ts>45 and r.dqyqts>45 ";
		Object[] object=new Object[]{userId,userId};
		
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and (CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "')";
		}else{
			sql += " and substring(r.rechargetime,4,2) ='" + nowdate.substring(3, 5) + "' ";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	/**
	 * 根据用户ID查找他已催收的金额(总数)
	 * @param userId
	 * @return
	 */
	public double getDYYCSJECSWB(int userId,String startDate,String endDate,String nowdate,String name,String phone,String yuqts){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id left join sdcms_user_cs u on  u.USER_ID=j.cuishouwb where j.cuishouwb=? and j.sfyhw=1 and j.yuq_ts>0 ";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and u.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and u.phone like '%"+phone+"%' ";
		}
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and ((CONCAT(SUBSTRING(j.hkyq_time,7,4),'-',SUBSTRING(j.hkyq_time,4,2),'-',SUBSTRING(j.hkyq_time,1,2),SUBSTRING(j.hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(j.hkyq_time,7,4),'-',SUBSTRING(j.hkyq_time,4,2),'-',SUBSTRING(j.hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(j.hkfq_time,7,4),'-',SUBSTRING(j.hkfq_time,4,2),'-',SUBSTRING(j.hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(j.hkfq_time,7,4),'-',SUBSTRING(j.hkfq_time,4,2),'-',SUBSTRING(j.hkfq_time,1,2)) <='" + endDate + "'))";
			sql += " and (CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "')";
		}else{
			sql += " and (substring(j.hkyq_time,4,2) ='" + nowdate.substring(3, 5) + "' ";
			sql += " or substring(j.hkfq_time,4,2) ='" + nowdate.substring(3, 5) + "' )";
			sql += " and substring(r.rechargetime,4,2) ='" + nowdate.substring(3, 5) + "' ";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	/**
	 * 根据用户ID查找他已催收的金额(总数)
	 * @param userId
	 * @return
	 */
	public double getJRYCSJE1(int userId,String startDate,String endDate,String name,String phone){
		String sql="select SUM(replace(hkfqzlx, ',' ,'')+replace(yuq_yhlx, ',' ,'')) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_tx where cuishou_tx=? and sfyhw=0  and yuq_ts=0 " ;
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and ((CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2),SUBSTRING(hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'))";
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	/**
	 * 根据用户ID查找他已催收的金额(总数)
	 * @param userId
	 * @return
	 */
	public double getDYYCSJE1(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(hkfqzlx, ',' ,'')+replace(yuq_yhlx, ',' ,'')) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cuishou_m1 where cuishou_m1=? and cuishou_id=? and sfyhw=0  and yuq_ts>0 ";
		Object[] object=new Object[]{userId,userId};
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2),SUBSTRING(hk_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getDYYCSJE1M0(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(hkfqzlx, ',' ,'')+replace(yuq_yhlx, ',' ,'')) from sd_new_jkyx where cuishou_m0=? and cuishou_id=? and sfyhw=0  and yuq_ts>0  and yuq_ts<4 ";
		Object[] object=new Object[]{userId,userId};
		
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2),SUBSTRING(hk_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2)) <='" + endDate + "'";
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getDYYCSJE1M1(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(hkfqzlx, ',' ,'')+replace(yuq_yhlx, ',' ,'')) from sd_new_jkyx where cuishou_m1=? and cuishou_id=? and sfyhw=0  and yuq_ts>0  and yuq_ts<16 ";
		Object[] object=new Object[]{userId,userId};
		
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2),SUBSTRING(hk_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2)) <='" + endDate + "'";
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getDYYCSJE1M2(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(hkfqzlx, ',' ,'')+replace(yuq_yhlx, ',' ,'')) from sd_new_jkyx where cuishou_m2=? and cuishou_id=? and sfyhw=0  and yuq_ts>15 ";
		Object[] object=new Object[]{userId,userId};
		
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2),SUBSTRING(hk_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2)) <='" + endDate + "'";
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getDYYCSJE1M3(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(hkfqzlx, ',' ,'')+replace(yuq_yhlx, ',' ,'')) from sd_new_jkyx where cuishou_m3=? and cuishou_id=? and sfyhw=0  and yuq_ts>45 ";
		Object[] object=new Object[]{userId,userId};
		
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2),SUBSTRING(hk_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2)) <='" + endDate + "'";
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	/**
	 * 根据用户ID查找他已催收的金额(总数)
	 * @param userId
	 * @return
	 */
	public double getDYYCSJE1CSWB(int userId,String startDate,String endDate,String name,String phone,String yuqts){
		String sql="select SUM(replace(hkfqzlx, ',' ,'')+replace(yuq_yhlx, ',' ,'')) from sd_new_jkyx inner join sdcms_user_cs on  sdcms_user_cs.USER_ID=sd_new_jkyx.cuishouwb where cuishouwb=? and sfyhw=0  and yuq_ts>0 ";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and sdcms_user_cs.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user_cs.phone like '%"+phone+"%' ";
		}
		if(!yuqts.equals("")){
			sql+=" and yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2),SUBSTRING(hk_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2)) <='" + endDate + "'";
			
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	/**
	 * 根据用户ID查找他已催收的金额(总数)
	 * @param userId
	 * @return
	 */
	public double getDYYCSJE2(int userId,String startDate,String endDate,String nowdate,String name,String phone,String yuqts){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id left join sdcms_user u on  u.USER_ID=j.cuishou_m1 where j.cuishou_m1=? and j.cuishou_id=? and j.sfyhw=0 and j.yuq_ts>0 and j.hkfq_code=0 ";
		Object[] object=new Object[]{userId,userId};
		if(!name.equals("")){
			sql+=" and u.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and u.phone like '%"+phone+"%' ";
		}
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and ((CONCAT(SUBSTRING(j.hkyq_time,7,4),'-',SUBSTRING(j.hkyq_time,4,2),'-',SUBSTRING(j.hkyq_time,1,2),SUBSTRING(j.hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(j.hkyq_time,7,4),'-',SUBSTRING(j.hkyq_time,4,2),'-',SUBSTRING(j.hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(j.hkfq_time,7,4),'-',SUBSTRING(j.hkfq_time,4,2),'-',SUBSTRING(j.hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(j.hkfq_time,7,4),'-',SUBSTRING(j.hkfq_time,4,2),'-',SUBSTRING(j.hkfq_time,1,2)) <='" + endDate + "'))";
			sql += " and (CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "')";
		}else{
			sql += " and (substring(j.hkyq_time,4,2) ='" + nowdate.substring(3, 5) + "' ";
			sql += " or substring(j.hkfq_time,4,2) ='" + nowdate.substring(3, 5) + "' )";
			sql += " and substring(r.rechargetime,4,2) ='" + nowdate.substring(3, 5) + "' ";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getDYYCSJE2M0(int userId,String startDate,String endDate,String nowdate,String name,String phone,String yuqts){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where j.cuishou_m0=? and j.sfyhw=0 and j.yuq_ts>0 and r.dqyqts>0 and r.dqyqts<4  and r.remark<>'延期还款' ";
		Object[] object=new Object[]{userId};
		
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and (CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "')";
		}else{
			sql += " and substring(r.rechargetime,4,2) ='" + nowdate.substring(3, 5) + "' ";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getDYYCSJE2M1(int userId,String startDate,String endDate,String nowdate,String name,String phone,String yuqts){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where j.cuishou_m1=? and j.sfyhw=0 and j.yuq_ts>0 and r.dqyqts>0 and r.dqyqts<16  and r.remark<>'延期还款' ";
		Object[] object=new Object[]{userId};
		
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and (CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "')";
		}else{
			sql += " and substring(r.rechargetime,4,2) ='" + nowdate.substring(3, 5) + "' ";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getDYYCSJE2M2(int userId,String startDate,String endDate,String nowdate,String name,String phone,String yuqts){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where j.cuishou_m2=? and j.cuishou_id=? and j.sfyhw=0 and j.yuq_ts>15 and j.hkfq_code=0 and r.dqyqts>15 ";
		Object[] object=new Object[]{userId,userId};
		
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and (CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "')";
		}else{
			sql += " and substring(r.rechargetime,4,2) ='" + nowdate.substring(3, 5) + "' ";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public double getDYYCSJE2M3(int userId,String startDate,String endDate,String nowdate,String name,String phone,String yuqts){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id where j.cuishou_m3=? and j.cuishou_id=? and j.sfyhw=0 and j.yuq_ts>45 and j.hkfq_code=0 and r.dqyqts>45 ";
		Object[] object=new Object[]{userId,userId};
		
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and (CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "')";
		}else{
			sql += " and substring(r.rechargetime,4,2) ='" + nowdate.substring(3, 5) + "' ";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	/**
	 * 根据用户ID查找他已催收的金额(总数)
	 * @param userId
	 * @return
	 */
	public double getDYYCSJE2CSWB(int userId,String startDate,String endDate,String nowdate,String name,String phone,String yuqts){
		String sql="select SUM(replace(r.rechargemoney, ',' ,'')) from sd_recharge_detail r left join sd_new_jkyx j on r.rechargenumber=j.id left join sdcms_user_cs u on  u.USER_ID=j.cuishouwb where j.cuishouwb=? and j.sfyhw=0 and j.yuq_ts>0 and j.hkfq_code=0 ";
		Object[] object=new Object[]{userId};
		if(!name.equals("")){
			sql+=" and u.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and u.phone like '%"+phone+"%' ";
		}
		if(!yuqts.equals("")){
			sql+=" and j.yuq_ts<"+yuqts ;
		}
		if (!StringHelper.isEmpty(startDate)) {
			sql += " and ((CONCAT(SUBSTRING(j.hkyq_time,7,4),'-',SUBSTRING(j.hkyq_time,4,2),'-',SUBSTRING(j.hkyq_time,1,2),SUBSTRING(j.hkyq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(j.hkyq_time,7,4),'-',SUBSTRING(j.hkyq_time,4,2),'-',SUBSTRING(j.hkyq_time,1,2)) <='" + endDate + "')";
			sql += " or (CONCAT(SUBSTRING(j.hkfq_time,7,4),'-',SUBSTRING(j.hkfq_time,4,2),'-',SUBSTRING(j.hkfq_time,1,2),SUBSTRING(hkfq_time,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(j.hkfq_time,7,4),'-',SUBSTRING(j.hkfq_time,4,2),'-',SUBSTRING(j.hkfq_time,1,2)) <='" + endDate + "'))";
			sql += " and (CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2),SUBSTRING(r.rechargetime,11,9)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2)) <='" + endDate + "')";
		}else{
			sql += " and (substring(j.hkyq_time,4,2) ='" + nowdate.substring(3, 5) + "' ";
			sql += " or substring(j.hkfq_time,4,2) ='" + nowdate.substring(3, 5) + "' )";
			sql += " and substring(r.rechargetime,4,2) ='" + nowdate.substring(3, 5) + "' ";
		}
		return this.getJdbcTemplate().queryLong(sql, object);
	}
	public void updatePX(DataRow row3)
	{
		
		//getJdbcTemplate().update("sd_user", row3, "id", row3.getString("id"));
		getJdbcTemplate().update(" sdcms_user",row3,"user_id",row3.getString("user_id"));
	}
	public void updatePXCSWB(DataRow row3)
	{
		
		//getJdbcTemplate().update("sd_user", row3, "id", row3.getString("id"));
		getJdbcTemplate().update(" sdcms_user_cs",row3,"user_id",row3.getString("user_id"));
	}
	public int getZong1(String startDate,String endDate){
		String sql="select count(id) from sd_new_jkyx where sfyfk=1";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getZong2(String startDate,String endDate){
		String sql="select count(id) from sd_new_jkyx where sfyfk=1 and sfyhw=1 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getZong3(String startDate,String endDate){
		String sql="select count(id) from sd_new_jkyx where sfyfk=1 and hkfq_code=0 and yuq_ts>0 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getZong4(String startDate,String endDate){
		String sql="select count(id) from sd_new_jkyx where sfyfk=1 and hkfq_code=0 and yuq_ts>0 and sfyhw=0 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getFifteen1(String startDate,String endDate){
		String sql="select count(id) from sd_new_jkyx where sfyfk=1 and jk_date=1";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getFifteen2(String startDate,String endDate){
		String sql="select count(id) from sd_new_jkyx where sfyfk=1 and jk_date=1 and sfyhw=1 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getFifteen3(String startDate,String endDate){
		String sql="select count(id) from sd_new_jkyx where sfyfk=1 and hkfq_code=0 and jk_date=1 and yuq_ts>0 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getFifteen4(String startDate,String endDate){
		String sql="select count(id) from sd_new_jkyx where sfyfk=1 and hkfq_code=0 and jk_date=1 and yuq_ts>0 and sfyhw=0 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getThirty1(String startDate,String endDate){
		String sql="select count(id) from sd_new_jkyx where sfyfk=1 and jk_date=2";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getThirty2(String startDate,String endDate){
		String sql="select count(id) from sd_new_jkyx where sfyfk=1 and jk_date=2 and sfyhw=1 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getThirty3(String startDate,String endDate){
		String sql="select count(id) from sd_new_jkyx where sfyfk=1 and hkfq_code=0 and jk_date=2 and yuq_ts>0 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getThirty4(String startDate,String endDate){
		String sql="select count(id) from sd_new_jkyx where sfyfk=1 and hkfq_code=0 and jk_date=2 and yuq_ts>0 and sfyhw=0 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getNewuser1(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 GROUP BY userid)a WHERE a.cishu=1";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getNewuser2(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 and sfyhw=1 GROUP BY userid)a WHERE a.cishu=1";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getNewuser3(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 and hkfq_code=0 and yuq_ts>0 GROUP BY userid)a WHERE a.cishu=1";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getNewuser4(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 and hkfq_code=0 and yuq_ts>0 and sfyhw=0 GROUP BY userid)a WHERE a.cishu=1";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getOlduser1(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 GROUP BY userid)a WHERE a.cishu>1";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getOlduser2(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 and sfyhw=1 GROUP BY userid)a WHERE a.cishu>1";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getOlduser3(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 and hkfq_code=0 and yuq_ts>0 GROUP BY userid)a WHERE a.cishu>1";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getOlduser4(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 AND yuq_ts>0 AND sfyhw=0 GROUP BY userid)a LEFT JOIN (SELECT COUNT(id) AS jkcishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 GROUP BY userid)b ON a.userid=b.userid WHERE b.jkcishu>=2 AND a.cishu>=1";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getTotaluser1(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 GROUP BY userid)a ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getTotaluser2(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 and sfyhw=1 GROUP BY userid)a ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getTotaluser3(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 and hkfq_code=0 and yuq_ts>0 GROUP BY userid)a ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getTotaluser4(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 and hkfq_code=0 and yuq_ts>0 and sfyhw=0 GROUP BY userid)a ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getTwicejk1(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 GROUP BY userid)a where a.cishu>=2";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getTwicejk2(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 and sfyhw=1 GROUP BY userid)a where a.cishu>=2 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getTwicejk3(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 AND yuq_ts>0 GROUP BY userid)a LEFT JOIN (SELECT COUNT(id) AS jkcishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 GROUP BY userid)b ON a.userid=b.userid WHERE b.jkcishu>=2 AND a.cishu>=1";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getTwicejk4(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 AND yuq_ts>0 AND sfyhw=0 GROUP BY userid)a LEFT JOIN (SELECT COUNT(id) AS jkcishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 GROUP BY userid)b ON a.userid=b.userid WHERE b.jkcishu>=2 AND a.cishu>=1 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getThirdjk1(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 GROUP BY userid)a where a.cishu>=3";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getThirdjk2(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 and sfyhw=1 GROUP BY userid)a where a.cishu>=3 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getThirdjk3(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 AND yuq_ts>0 GROUP BY userid)a LEFT JOIN (SELECT COUNT(id) AS jkcishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 GROUP BY userid)b ON a.userid=b.userid WHERE b.jkcishu>=3 AND a.cishu>=2";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getThirdjk4(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 AND yuq_ts>0 AND sfyhw=0 GROUP BY userid)a LEFT JOIN (SELECT COUNT(id) AS jkcishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 GROUP BY userid)b ON a.userid=b.userid WHERE b.jkcishu>=3 AND a.cishu>=1 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getFourjk1(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 GROUP BY userid)a where a.cishu>=4";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getFourjk2(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 and sfyhw=1 GROUP BY userid)a where a.cishu>=4 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getFourjk3(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 AND yuq_ts>0 GROUP BY userid)a LEFT JOIN (SELECT COUNT(id) AS jkcishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 GROUP BY userid)b ON a.userid=b.userid WHERE b.jkcishu>=4 AND a.cishu>=2";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getFourjk4(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 AND yuq_ts>0 AND sfyhw=0 GROUP BY userid)a LEFT JOIN (SELECT COUNT(id) AS jkcishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 GROUP BY userid)b ON a.userid=b.userid WHERE b.jkcishu>=4 AND a.cishu>=1 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getFivejk1(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 GROUP BY userid)a where a.cishu>=5";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getFivejk2(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 and sfyhw=1 GROUP BY userid)a where a.cishu>=5 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getFivejk3(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 AND yuq_ts>0 GROUP BY userid)a LEFT JOIN (SELECT COUNT(id) AS jkcishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 GROUP BY userid)b ON a.userid=b.userid WHERE b.jkcishu>=5 AND a.cishu>=2";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getFivejk4(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 AND yuq_ts>0 AND sfyhw=0 GROUP BY userid)a LEFT JOIN (SELECT COUNT(id) AS jkcishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 GROUP BY userid)b ON a.userid=b.userid WHERE b.jkcishu>=5 AND a.cishu>=1 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getSixjk1(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 GROUP BY userid)a where a.cishu>=6 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getSixjk2(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 and sfyhw=1 GROUP BY userid)a where a.cishu>=6 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getSixjk3(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 AND yuq_ts>0 GROUP BY userid)a LEFT JOIN (SELECT COUNT(id) AS jkcishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 GROUP BY userid)b ON a.userid=b.userid WHERE b.jkcishu>=6 AND a.cishu>=2";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getSixjk4(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 AND yuq_ts>0 AND sfyhw=0 GROUP BY userid)a LEFT JOIN (SELECT COUNT(id) AS jkcishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 GROUP BY userid)b ON a.userid=b.userid WHERE b.jkcishu>=6 AND a.cishu>=1 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getSevenjk1(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 GROUP BY userid)a where a.cishu>=7";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getSevenjk2(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 and sfyhw=1 GROUP BY userid)a where a.cishu>=7 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getSevenjk3(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 AND yuq_ts>0 GROUP BY userid)a LEFT JOIN (SELECT COUNT(id) AS jkcishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 GROUP BY userid)b ON a.userid=b.userid WHERE b.jkcishu>=7 AND a.cishu>=2";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getSevenjk4(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 AND yuq_ts>0 AND sfyhw=0 GROUP BY userid)a LEFT JOIN (SELECT COUNT(id) AS jkcishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 GROUP BY userid)b ON a.userid=b.userid WHERE b.jkcishu>=7 AND a.cishu>=1 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getEightjk1(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 GROUP BY userid)a where a.cishu>=8 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getEightjk2(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 and sfyhw=1 GROUP BY userid)a where a.cishu>=8 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getEightjk3(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 AND yuq_ts>0 GROUP BY userid)a LEFT JOIN (SELECT COUNT(id) AS jkcishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 GROUP BY userid)b ON a.userid=b.userid WHERE b.jkcishu>=8 AND a.cishu>=2";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getEightjk4(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 AND yuq_ts>0 AND sfyhw=0 GROUP BY userid)a LEFT JOIN (SELECT COUNT(id) AS jkcishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 GROUP BY userid)b ON a.userid=b.userid WHERE b.jkcishu>=8 AND a.cishu>=1 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getNinejk1(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 GROUP BY userid)a where a.cishu>=9 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getNinejk2(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 and sfyhw=1 GROUP BY userid)a where a.cishu>=9 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getNinejk3(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 AND yuq_ts>0 GROUP BY userid)a LEFT JOIN (SELECT COUNT(id) AS jkcishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 GROUP BY userid)b ON a.userid=b.userid WHERE b.jkcishu>=9 AND a.cishu>=2";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getNinejk4(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 AND yuq_ts>0 AND sfyhw=0 GROUP BY userid)a LEFT JOIN (SELECT COUNT(id) AS jkcishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 GROUP BY userid)b ON a.userid=b.userid WHERE b.jkcishu>=9 AND a.cishu>=1 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getTenjk1(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 GROUP BY userid)a where a.cishu>=10 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getTenjk2(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 and sfyhw=1 GROUP BY userid)a where a.cishu>=10 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getTenjk3(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 AND yuq_ts>0 GROUP BY userid)a LEFT JOIN (SELECT COUNT(id) AS jkcishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 GROUP BY userid)b ON a.userid=b.userid WHERE b.jkcishu>=10 AND a.cishu>=2";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getTenjk4(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 AND yuq_ts>0 AND sfyhw=0 GROUP BY userid)a LEFT JOIN (SELECT COUNT(id) AS jkcishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 GROUP BY userid)b ON a.userid=b.userid WHERE b.jkcishu>=10 AND a.cishu>=1 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getDytenjk1(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 GROUP BY userid)a where a.cishu>10";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getDytenjk2(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 and sfyhw=1 GROUP BY userid)a where a.cishu>10 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getDytenjk3(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 AND yuq_ts>0 GROUP BY userid)a LEFT JOIN (SELECT COUNT(id) AS jkcishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 GROUP BY userid)b ON a.userid=b.userid WHERE b.jkcishu>10 AND a.cishu>=2";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getDytenjk4(String startDate,String endDate){
		String sql="SELECT COUNT(cishu) FROM (SELECT COUNT(id) AS cishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 AND yuq_ts>0 AND sfyhw=0 GROUP BY userid)a LEFT JOIN (SELECT COUNT(id) AS jkcishu,userid FROM sd_new_jkyx WHERE sfyfk=1 AND hkfq_code=0 GROUP BY userid)b ON a.userid=b.userid WHERE b.jkcishu>10 AND a.cishu>=1 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	
	public int getHavashuju1(String startDate,String endDate){
		String sql="select count(id) from sd_new_jkyx where sfyfk=1 and pipei<>0 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getHavashuju2(String startDate,String endDate){
		String sql="select count(id) from sd_new_jkyx where sfyfk=1 and pipei<>0 and sfyhw=1";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getHavashuju3(String startDate,String endDate){
		String sql="select count(id) from sd_new_jkyx where sfyfk=1 and hkfq_code=0 and pipei<>0 and yuq_ts>0";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getHavashuju4(String startDate,String endDate){
		String sql="select count(id) from sd_new_jkyx where sfyfk=1 and hkfq_code=0 and pipei<>0 and yuq_ts>0 and sfyhw=0";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getNoshuju1(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx where sfyfk=1 and pipei=0 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getNoshuju2(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx where sfyfk=1 and pipei=0 and sfyhw=1";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getNoshuju3(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx where sfyfk=1 and hkfq_code=0 and pipei=0 and yuq_ts>0";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getNoshuju4(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx where sfyfk=1 and hkfq_code=0 and pipei=0 and yuq_ts>0 and sfyhw=0";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getIosjk1(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx left join sd_user on sd_new_jkyx.userid=sd_user.id where sfyfk=1 and  SUBSTRING(sd_user.username,6,3)='IOS'";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getIosjk2(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx left join sd_user on sd_new_jkyx.userid=sd_user.id where sfyfk=1 and  sfyhw=1 and SUBSTRING(sd_user.username,6,3)='IOS'";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
			
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getIosjk3(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx left join sd_user on sd_new_jkyx.userid=sd_user.id where sfyfk=1 and hkfq_code=0  and sd_new_jkyx.yuq_ts>0 and SUBSTRING(sd_user.username,6,3)='IOS'";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
			
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getIosjk4(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx left join sd_user on sd_new_jkyx.userid=sd_user.id where sfyfk=1 and hkfq_code=0  and sd_new_jkyx.yuq_ts>0 and sfyhw=0 and SUBSTRING(sd_user.username,6,3)='IOS'";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getAndjk1(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx left join sd_user on sd_new_jkyx.userid=sd_user.id where sfyfk=1 and  SUBSTRING(sd_user.username,6,3)='AND'";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
			
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getAndjk2(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx left join sd_user on sd_new_jkyx.userid=sd_user.id where sfyfk=1 and  sfyhw=1 and SUBSTRING(sd_user.username,6,3)='AND'";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getAndjk3(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx left join sd_user on sd_new_jkyx.userid=sd_user.id where sfyfk=1 and hkfq_code=0  and sd_new_jkyx.yuq_ts>0 and SUBSTRING(sd_user.username,6,3)='AND'";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
			
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getAndjk4(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx left join sd_user on sd_new_jkyx.userid=sd_user.id where sfyfk=1 and hkfq_code=0  and sd_new_jkyx.yuq_ts>0 and sfyhw=0 and SUBSTRING(sd_user.username,6,3)='AND'";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
			
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getManjk1(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx left join sd_user_finance on sd_new_jkyx.userid=sd_user_finance.userid where sfyfk=1 and  sd_user_finance.sex=1";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
			
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getManjk2(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx left join sd_user_finance on sd_new_jkyx.userid=sd_user_finance.userid where sfyfk=1 and  sfyhw=1 and sd_user_finance.sex=1";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
			
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getManjk3(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx left join sd_user_finance on sd_new_jkyx.userid=sd_user_finance.userid where sfyfk=1 and hkfq_code=0  and yuq_ts>0 and sd_user_finance.sex=1";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
			
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getManjk4(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx left join sd_user_finance on sd_new_jkyx.userid=sd_user_finance.userid where sfyfk=1 and hkfq_code=0  and yuq_ts>0 and sfyhw=0 and sd_user_finance.sex=1";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
			
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getWomenjk1(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx left join sd_user_finance on sd_new_jkyx.userid=sd_user_finance.userid where sfyfk=1  and sd_user_finance.sex=2 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
			
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getWomenjk2(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx left join sd_user_finance on sd_new_jkyx.userid=sd_user_finance.userid where sfyfk=1 and  sfyhw=1 and sd_user_finance.sex=2 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
			
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getWomenjk3(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx left join sd_user_finance on sd_new_jkyx.userid=sd_user_finance.userid where sfyfk=1 and hkfq_code=0  and yuq_ts>0 and sd_user_finance.sex=2 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
			
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getWomenjk4(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx left join sd_user_finance on sd_new_jkyx.userid=sd_user_finance.userid where sfyfk=1 and hkfq_code=0  and yuq_ts>0 and sfyhw=0 and sd_user_finance.sex=2 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
			
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getPingfen31(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx where sfyfk=1 and pipei=3 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getPingfen32(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx where sfyfk=1 and pipei=3 and sfyhw=1";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getPingfen33(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx where sfyfk=1 and hkfq_code=0 and pipei=3 and yuq_ts>0 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getPingfen34(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx where sfyfk=1 and hkfq_code=0 and pipei=3 and yuq_ts>0 and sfyhw=0";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getPingfen21(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx where sfyfk=1 and pipei=2 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getPingfen22(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx where sfyfk=1 and pipei=2 and sfyhw=1";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getPingfen23(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx where sfyfk=1 and hkfq_code=0 and pipei=2 and yuq_ts>0 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getPingfen24(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx where sfyfk=1 and hkfq_code=0 and pipei=2 and yuq_ts>0 and sfyhw=0 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getPingfen11(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx where sfyfk=1 and pipei=1 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getPingfen12(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx where sfyfk=1 and pipei=1 and sfyhw=1 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getPingfen13(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx where sfyfk=1 and hkfq_code=0 and pipei=1 and yuq_ts>0 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getPingfen14(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx where sfyfk=1 and hkfq_code=0 and pipei=1 and yuq_ts>0 and sfyhw=0 ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getPingfenf11(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx where sfyfk=1 and pipei=-1";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getPingfenf12(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx where sfyfk=1 and pipei=-1 and sfyhw=1";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getPingfenf13(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx where sfyfk=1 and hkfq_code=0 and pipei=-1 and yuq_ts>0";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getPingfenf14(String startDate,String endDate){
		String sql="select count(*) from sd_new_jkyx where sfyfk=1 and hkfq_code=0 and pipei=-1 and yuq_ts>0 and sfyhw=0";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2),SUBSTRING(create_date,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(create_date,7,4),'-',SUBSTRING(create_date,4,2),'-',SUBSTRING(create_date,1,2)) <='" + endDate + "'";
		
		}
		return this.getJdbcTemplate().queryInt(sql);
	}

	/**
	 * 获取当天新增的提醒还款用户数
	 */
	public int getAllYQList(int userId,String ri, String yue,String name,String phone)
	{		
		
		String sql = " select count(*) from  sd_new_jkyx  where cuishou_id="+userId;
		sql += " and ((SUBSTRING(hkyq_time,1,2) ='" + ri + "'";
		sql += " and SUBSTRING(hkyq_time,4,2) ='" + yue+ "')";
		sql += " or (SUBSTRING(hkfq_time,1,2) ='" + ri + "'";
		sql += " and SUBSTRING(hkfq_time,4,2) ='" + yue+ "'))";
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 按时还款
	 * 
	 */
	public int getASHK(int userid){
		String sql = "select count(id) from sd_returnvisit where content like '%KH sẽ thanh toán đúng hạn%' and kefuid="+userid;
		return getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 约期还款
	 * 
	 */
	public int getYQHK(int userid){
		String sql = "select count(id) from sd_returnvisit where content like '%KH hẹn lại ngày thanh toán%' and kefuid="+userid;
		return getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 延期15天
	 * 
	 */
	public int getSQYQ15(int userid){
		String sql = "select count(id) from sd_returnvisit where content like '%KH xin gia hạn 15 ngày%' and kefuid="+userid;
		return getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 延期30天
	 * 
	 */
	public int getSQYQ30(int userid){
		String sql = "select count(id) from sd_returnvisit where content like '%KH xin gia hạn 30 ngày%' and kefuid="+userid;
		return getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 不接电话
	 * 
	 */
	public int getBJDH(int userid){
		String sql = "select count(id) from sd_returnvisit where content like '%KH không nghe máy%' and kefuid="+userid;
		return getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 关机
	 * 
	 */
	public int getGJ(int userid){
		String sql = "select count(id) from sd_returnvisit where content like '%KH khóa máy%' and kefuid="+userid;
		return getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 停机
	 * 
	 */
	public int getTJ(int userid){
		String sql = "select count(id) from sd_returnvisit where content like '%Thuê bao ngừng sử dụng%' and kefuid="+userid;
		return getJdbcTemplate().queryInt(sql);
	}
	
	
	//当天的
	/**
	 * 按时还款
	 * 
	 */
	public int getDTASHK(int userid,String time){
		String sql = "select count(id) from sd_returnvisit where content like '%KH sẽ thanh toán đúng hạn%' and kefuid="+userid+" and substring(visitdate,1,10)='"+time+"'";
		return getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 约期还款
	 * 
	 */
	public int getDTYQHK(int userid,String time){
		String sql = "select count(id) from sd_returnvisit where content like '%KH hẹn lại ngày thanh toán%' and kefuid="+userid+" and substring(visitdate,1,10)='"+time+"'";
		return getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 延期15天
	 * 
	 */
	public int getDTSQYQ15(int userid,String time){
		String sql = "select count(id) from sd_returnvisit where content like '%KH xin gia hạn 15 ngày%' and kefuid="+userid+" and substring(visitdate,1,10)='"+time+"'";
		return getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 延期30天
	 * 
	 */
	public int getDTSQYQ30(int userid,String time){
		String sql = "select count(id) from sd_returnvisit where content like '%KH xin gia hạn 30 ngày%' and kefuid="+userid+" and substring(visitdate,1,10)='"+time+"'";
		return getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 不接电话
	 * 
	 */
	public int getDTBJDH(int userid,String time){
		String sql = "select count(id) from sd_returnvisit where content like '%KH không nghe máy%' and kefuid="+userid+" and substring(visitdate,1,10)='"+time+"'";
		return getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 关机
	 * 
	 */
	public int getDTGJ(int userid,String time){
		String sql = "select count(id) from sd_returnvisit where content like '%KH khóa máy%' and kefuid="+userid+" and substring(visitdate,1,10)='"+time+"'";
		return getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 停机
	 * 
	 */
	public int getDTTJ(int userid,String time){
		String sql = "select count(id) from sd_returnvisit where content like '%Thuê bao ngừng sử dụng%' and kefuid="+userid+" and substring(visitdate,1,10)='"+time+"'";
		return getJdbcTemplate().queryInt(sql);
	}
	public String getPJGZSHENHEZZ1() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang1 from sd_pingjiguize  where id =24 ");
		
		return getJdbcTemplate().queryString(sb.toString());
	}
	public String getPJGZSHENHE1() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang2 from sd_pingjiguize  where id =24 ");
		
		return getJdbcTemplate().queryString(sb.toString());
	}
	public String getPJGZSHENHEZZ2() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang1 from sd_pingjiguize  where id =25 ");
		
		return getJdbcTemplate().queryString(sb.toString());
	}
	public String getPJGZSHENHE2() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang2 from sd_pingjiguize  where id =25 ");
		
		return getJdbcTemplate().queryString(sb.toString());
	}
	public String getPJGZSHENHEZZ3() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang1 from sd_pingjiguize  where id =26 ");
		
		return getJdbcTemplate().queryString(sb.toString());
	}
	public String getPJGZSHENHE3() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang2 from sd_pingjiguize  where id =26 ");
		
		return getJdbcTemplate().queryString(sb.toString());
	}
	public String getPJGZSHENHEZZ4() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang1 from sd_pingjiguize  where id =27 ");
		
		return getJdbcTemplate().queryString(sb.toString());
	}
	public String getPJGZSHENHE4() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang2 from sd_pingjiguize  where id =27 ");
		
		return getJdbcTemplate().queryString(sb.toString());
	}
	
}
