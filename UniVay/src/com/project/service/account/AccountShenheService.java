package com.project.service.account;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.thinkive.base.jdbc.DBPage;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.service.BaseService;
import com.thinkive.base.util.StringHelper;

/**
 * 统计审核
 * @author 苏兰
 *
 */
public class AccountShenheService extends BaseService{
	
	private static Logger logger=Logger.getLogger(AccountShenheService.class);
	
	public JdbcTemplate getJdbcTemplate(){
		return getJdbcTemplate("web");
	}
	/**
	 *复借总数15天
	 * 
	 */
	public int getFJZS15(String riqi){
		String sql = "select count(id) from sd_new_jkyx WHERE jk_date=1 AND is_old_user=1 AND SUBSTRING(fkdz_time_day,1,7)='"+riqi+"'";
		return getJdbcTemplate().queryInt(sql);
	}
	/**
	 *复借总数30天
	 * 
	 */
	public int getFJZS30(String riqi){
		String sql = "select count(id) from sd_new_jkyx WHERE jk_date=2 AND is_old_user=1 AND SUBSTRING(fkdz_time_day,1,7)='"+riqi+"'";
		return getJdbcTemplate().queryInt(sql);
	}
	/**
	 *复借还款数15天
	 * 
	 */
	public int getFJHK15(String riqi){
		String sql = "select count(id) from sd_new_jkyx WHERE jk_date=1 AND sfyhw=1  AND is_old_user=1 AND SUBSTRING(fkdz_time_day,1,7)='"+riqi+"'";
		return getJdbcTemplate().queryInt(sql);
	}
	/**
	 *复借还款数30天
	 * 
	 */
	public int getFJHK30(String riqi){
		String sql = "select count(id) from sd_new_jkyx WHERE jk_date=2 AND sfyhw=1  AND is_old_user=1 AND SUBSTRING(fkdz_time_day,1,7)='"+riqi+"'";
		return getJdbcTemplate().queryInt(sql);
	}
	/*************************老用户统计************xiong  start*****************************************************************************/
	/**
	 *每天还款成功
	 * 
	 */
	public int getDayCGHK(String riqi){
		String sql = "select count(id) from sd_new_jkyx where sfyfk=1 and sfyhw=1 and CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2))='"+riqi+"'";
		return getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 查询这些每天还款成功的里面有没有当天又借的
	 * 
	 */
	public int getDayCGHKJK(String riqi){
		String sql = "SELECT  COUNT(snj.userid ) FROM  (SELECT DISTINCT userid FROM sd_new_jkyx  WHERE sfyfk=1 and SUBSTRING(daytime,1,10)='"+riqi+"') snj  "

				+ " WHERE  snj.userid IN  (SELECT  DISTINCT a.userid FROM sd_new_jkyx a WHERE a.sfyfk =1 AND a.sfyhw =1 AND CONCAT(SUBSTRING(a.hk_time,7,4),'-',SUBSTRING(a.hk_time,4,2),'-',SUBSTRING(a.hk_time,1,2))='"+riqi+"')";
		
		return getJdbcTemplate().queryInt(sql);
	}
	/**
	 *总还款成功
	 * 
	 */
	public int getDayCGHK(){
		String sql = "SELECT COUNT(DISTINCT userid) FROM sd_new_jkyx WHERE sfyfk=1 AND sfyhw=1 ";
		return getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 总查询这些每天还款成功的里面有没有当天又借的
	 * 
	 */
	public int getDayCGHKJK(){
		String sql = "SELECT  COUNT(snj.userid ) FROM  (SELECT userid FROM sd_new_jkyx  WHERE sfyfk=1) snj  "
				
				+ " WHERE  snj.userid IN  (SELECT  DISTINCT a.userid FROM sd_new_jkyx a WHERE a.sfyfk =1 AND a.sfyhw =1 )";
		
		return getJdbcTemplate().queryInt(sql);
	}
	/**
	 * xiong 分页查询每天申请贷款的老用户成功的数量
	 * @param curPage
	 * @param numPerPage
	 * @return
	 */
	//cl_status=1 AND cl02_status=1 AND cl03_status=1
	public DBPage selectGetOldUserList(int curPage, int numPerPage, String startDate, String endDate) {

		String sql = "SELECT  COUNT(snj.userid ) as oldyonghunum,snj.daytime as  riqi FROM  (SELECT DISTINCT userid,SUBSTRING(fkdz_time_day,1,10) AS daytime  FROM sd_new_jkyx  WHERE sfyfk=1 and is_old_user=1 )  snj  "

				+ " WHERE  snj.userid IN  (SELECT  DISTINCT a.userid FROM sd_new_jkyx a WHERE a.sfyfk =1 AND a.sfyhw =1 AND a.daytime <snj.daytime)  ";
		if (!StringHelper.isEmpty(startDate)) {

			sql += " and SUBSTRING(snj.daytime,1,10)<='" + startDate + "'";

			sql += " SUBSTRING(j.daytime,1,10)>='" + endDate + "'";
		}
		sql += " GROUP BY daytime ORDER BY daytime DESC";

		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public DBPage selectGetOldUserListCGHK(int curPage, int numPerPage, String startDate, String endDate) {
		
		String sql = "SELECT COUNT(id) AS daycghk,CONCAT(SUBSTRING(hk_time,7,4),'-',SUBSTRING(hk_time,4,2),'-',SUBSTRING(hk_time,1,2)) AS riqi FROM sd_new_jkyx WHERE sfyfk=1 AND sfyhw=1";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and SUBSTRING(daytime,1,10)<='" + startDate + "'";
			
			sql += " SUBSTRING(daytime,1,10)>='" + endDate + "'";
		}
		sql += " GROUP BY riqi ORDER BY riqi DESC";
		
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public DBPage selectGetOldUserListCGHKJK(int curPage, int numPerPage, String startDate, String endDate) {
		
		String sql = "SELECT  COUNT(snj.userid ) as daycghkjk,snj.daytime as  riqi FROM  (SELECT DISTINCT userid,SUBSTRING(daytime,1,10) AS daytime  FROM sd_new_jkyx )  snj  "

				+ " WHERE  snj.userid IN  (SELECT  DISTINCT a.userid FROM sd_new_jkyx a WHERE a.sfyfk =1 AND a.sfyhw =1 AND CONCAT(SUBSTRING(a.hk_time,7,4),'-',SUBSTRING(a.hk_time,4,2),'-',SUBSTRING(a.hk_time,1,2))=snj.daytime)  ";
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and SUBSTRING(daytime,1,10)<='" + startDate + "'";
			
			sql += " SUBSTRING(daytime,1,10)>='" + endDate + "'";
		}
		sql += " GROUP BY daytime ORDER BY daytime DESC";
		
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	/**
	 * xiong 分页查询每天申请贷款的老用户失败的数量
	 * @param curPage
	 * @param numPerPage
	 * @return
	 */
	//cl_status=3
	public DBPage selectGetOldUserListsb(int curPage ,int numPerPage,String startDate,String endDate){
		
		String sql ="SELECT  COUNT(snj.userid ) as oldyonghunum,snj.daytime as  riqi FROM  (SELECT DISTINCT userid,SUBSTRING(daytime,1,10) AS daytime  FROM sd_new_jkyx  WHERE  (cl_status=3 or cl02_status=3 or cl03_status=3) and is_old_user=1 )  snj  "
				+" WHERE  snj.userid IN  (SELECT  DISTINCT a.userid FROM sd_new_jkyx a WHERE a.sfyfk =1 AND a.sfyhw =1 AND a.daytime <snj.daytime)  ";
				if (!StringHelper.isEmpty(startDate)) {
					
					sql += " and SUBSTRING(snj.daytime,1,10)<='" + startDate + "'";
							
					sql += " SUBSTRING(j.daytime,1,10)>='" + endDate + "'";
				}
				
				
			sql +="  GROUP BY daytime ORDER BY daytime DESC";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	
	/**
	 * xiong 分页查询查询每个审核人员的每天审核老员工数量
	 * @param curPage
	 * @param numPerPage
	 * @return
	 */
	public DBPage selectGetOldUser(String cl_ren,int curPage ,int numPerPage){
		
		String sql ="SELECT snj.cl_ren,CONCAT(SUBSTRING(snj.create_date,7,4),'-',SUBSTRING(snj.create_date,4,2),'-',SUBSTRING(snj.create_date,1,2)) as riqi,"
				+ " sum(case when snj.cl_status<>0  then 1 else 0 end+case when snj.cl02_status<>0  then 1 else 0 end+case when snj.cl03_status<>0  then 1  else 0 end) as oldyonghunum "
				+ "FROM  sd_new_jkyx snj   "
				+ "left join sdcms_user  ssu on snj.onesh =ssu.USER_ID left join sd_user su on snj.userid = su.id "
				+ "WHERE su.oldyonghu>0  "
				+ "AND  cl_status<>0 "
				+ "AND cl_ren="+cl_ren+" group by  CONCAT(SUBSTRING(snj.create_date,7,4),'-',SUBSTRING(snj.create_date,4,2),'-',SUBSTRING(snj.create_date,1,2)) "
				+ " order by CONCAT(SUBSTRING(snj.create_date,7,4),'-',SUBSTRING(snj.create_date,4,2),'-',SUBSTRING(snj.create_date,1,2)) desc";
		
	   return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	

	/**
	 * xiong 分页查询每天申请贷款的老用户数量
	 * @param curPage
	 * @param numPerPage
	 * @return
	 */
	
	public List<DataRow> selectShenHeList(){
		String sql ="select guizeneirong1,guizebianliang2 FROM sd_pingjiguize  WHERE  guizeneirong2 in ('成员')";	
	    return getJdbcTemplate().query(sql);
	}
	/**
	 * xiong查询员工sdcms_user
	 *
	 */
	
	public DataRow getSdcmsUser(int a){
		String sql ="select roleid FROM sdcms_user  WHERE USER_ID="+a;	
	    return getJdbcTemplate().queryMap(sql);
	}
	
	/*************************老用户统计************xiong  end*****************************************************************************/
	
	/**
	 * 根据用户ID查找他审批的一审(总数)
	 * @param userId
	 * @return
	 */
	public int getOneCount(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx where cl_ren=? and cl_status<>0";
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
		String sql="SELECT COUNT(jishu) FROM (SELECT COUNT(id) AS jishu,cl_time FROM sd_new_jkyx WHERE cl_ren=? AND cl_status<>0 ";
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
		String sql="select count(*) from sd_new_jkyx where cl02_ren=? and cl02_status<>0";
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
		String sql="select count(*) from sd_new_jkyx where cl02_ren=? and cl02_status=1 and sfyfk=1 ";
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
	
	public int getH5TwoCountTGFK(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx LEFT JOIN sd_user on sd_new_jkyx.userid=sd_user.id where cl02_ren=? and cl02_status=1 and sfyfk=1 and sd_user.username like 'TAFA%'";
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
	public int getCountLYH(int userId,String startDate,String endDate,String name,String phone){
		String sql="select count(*) from sd_new_jkyx LEFT JOIN sd_user on sd_new_jkyx.userid=sd_user.id where cl02_ren=? and cl02_status=1 and sfyfk=1 and is_old_user=1 ";
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
	public int getNowOneCountSBDay(int userId,String startDate,String endDate,String nowdate, String enddate,String name,String phone){
		String sql= "";
		if (!StringHelper.isEmpty(startDate)) {
			sql="SELECT COUNT(jishu) FROM (select count(id) as jishu from sd_new_jkyx where cl_ren="+userId+" and cl_status=3 ";
			
				
				sql += " and CONCAT(SUBSTRING(cl_time,7,4),'-',SUBSTRING(cl_time,4,2),'-',SUBSTRING(cl_time,1,2)) >='" + startDate + "'";
				
				sql += " and CONCAT(SUBSTRING(cl_time,7,4),'-',SUBSTRING(cl_time,4,2),'-',SUBSTRING(cl_time,1,2)) <='" + endDate + "'";
			
		}else{
			sql="SELECT COUNT(jishu) FROM (select count(id) as jishu from sd_new_jkyx where cl_ren="+userId+" and cl_status=3 and substring(cl_time,1,10) ='"+nowdate+"' ";
		}
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		sql +=" and (cl_yj not like '%KH cap nhat lai thoi han vay%' ";
		sql +=" and cl_yj not like '%Hinh CMND khong hop le%' ";
		sql +=" and cl_yj not like '%Sai thong tin ngan hang%' ";
		sql +=" and cl_yj not like '%Sai thong tin nguoi lien he%')  GROUP BY userid) a";
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他审批的一审(总数)
	 * @param userId
	 * @return
	 */
	public int getNowOneCountSBXXCWDay(int userId,String startDate,String endDate,String nowdate, String enddate,String name,String phone){
		String sql= "";
		if (!StringHelper.isEmpty(startDate)) {
			sql="SELECT COUNT(jishu) FROM (select count(id) as jishu from sd_new_jkyx where cl_ren="+userId+" and cl_status=3 ";
			
				
				sql += " and CONCAT(SUBSTRING(cl_time,7,4),'-',SUBSTRING(cl_time,4,2),'-',SUBSTRING(cl_time,1,2)) >='" + startDate + "'";
				
				sql += " and CONCAT(SUBSTRING(cl_time,7,4),'-',SUBSTRING(cl_time,4,2),'-',SUBSTRING(cl_time,1,2)) <='" + endDate + "'";
			
		}else{
			sql="SELECT COUNT(jishu) FROM (select count(id) as jishu from sd_new_jkyx where cl_ren="+userId+" and cl_status=3 and substring(cl_time,1,10) ='"+nowdate+"' ";
		}
		sql +=" and (cl_yj like '%KH cap nhat lai thoi han vay%' ";
		sql +=" or cl_yj like '%Hinh CMND khong hop le%' ";
		sql +=" or cl_yj like '%Sai thong tin ngan hang%' ";
		sql +=" or cl_yj like '%Sai thong tin nguoi lien he%')  GROUP BY userid) a";
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 根据用户ID查找他审批的一审(总数)
	 * @param userId
	 * @return
	 */
	public int getNowOneCountSBXXCWDayHL(int userId,String startDate,String endDate,String nowdate, String enddate,String name,String phone){
		String sql= "";
		
		if (!StringHelper.isEmpty(startDate)) {
			sql="SELECT count(*) FROM sd_new_jkyx j RIGHT JOIN (SELECT id,userid FROM sd_new_jkyx WHERE cl_ren="+userId+" AND cl_status=3";
			sql += " and CONCAT(SUBSTRING(cl_time,7,4),'-',SUBSTRING(cl_time,4,2),'-',SUBSTRING(cl_time,1,2)) >='" + startDate + "'";
			sql += " and CONCAT(SUBSTRING(cl_time,7,4),'-',SUBSTRING(cl_time,4,2),'-',SUBSTRING(cl_time,1,2)) <='" + endDate + "'";
			sql += " AND (cl_yj LIKE '%KH cap nhat lai thoi han vay%' "
			+ " OR cl_yj LIKE '%Hinh CMND khong hop le%' "
			+ " OR cl_yj LIKE '%Sai thong tin ngan hang%' "
			+ " OR cl_yj LIKE '%Sai thong tin nguoi lien he%')) k" 
			+ " ON j.userid=k.userid WHERE j.id>k.id ";
			sql += " and SUBSTRING(j.create_date,1,10)>='" + startDate + "'";
			sql += " and SUBSTRING(j.create_date,1,10)<='" + endDate + "'";
		}else{
			sql="SELECT count(*) FROM sd_new_jkyx j RIGHT JOIN " + 
				"(SELECT id,userid FROM sd_new_jkyx WHERE cl_ren="+userId+" AND cl_status=3 AND SUBSTRING(cl_time,1,10) ='"+nowdate+"' "
				+ " AND (cl_yj LIKE '%KH cap nhat lai thoi han vay%' "
				+ " OR cl_yj LIKE '%Hinh CMND khong hop le%' "
				+ " OR cl_yj LIKE '%Sai thong tin ngan hang%' "
				+ " OR cl_yj LIKE '%Sai thong tin nguoi lien he%')) k" 
				+ " ON j.userid=k.userid WHERE j.id>k.id AND SUBSTRING(j.create_date,1,10)='"+nowdate+"'";
		}
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getNowOneCountTGFK(int userId,String startDate,String endDate,String nowdate, String enddate,String name,String phone){
		String sql= "";
		if (!StringHelper.isEmpty(startDate)) {
			sql="select count(*) from sd_new_jkyx where cl02_ren=? and cl02_status=1 and sfyfk=1 ";
			
				
				sql += " and CONCAT(SUBSTRING(cl_time,7,4),'-',SUBSTRING(cl_time,4,2),'-',SUBSTRING(cl_time,1,2),SUBSTRING(cl_time,11,9)) >='" + startDate + "'";
				
				sql += " and CONCAT(SUBSTRING(cl_time,7,4),'-',SUBSTRING(cl_time,4,2),'-',SUBSTRING(cl_time,1,2)) <='" + endDate + "'";
			
		}else{
			sql="select count(*) from sd_new_jkyx where cl02_ren=? and cl02_status=1 and sfyfk=1 and substring(cl_time,1,10) ='"+nowdate+"' ";
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
	 * 根据用户ID查找他审批的二审通过并且放款的(总数)
	 * @param userId
	 * @return
	 */
	public int getNowTwoCountTGFK(int userId,String startDate,String endDate,String nowdate, String enddate,String name,String phone){
		String sql= "";
		if (!StringHelper.isEmpty(startDate)) {
			sql="select count(*) from sd_new_jkyx where cl02_ren=? and cl02_status=1 and sfyfk=1 ";
			
				
				sql += " and CONCAT(SUBSTRING(cl_time,7,4),'-',SUBSTRING(cl_time,4,2),'-',SUBSTRING(cl_time,1,2),SUBSTRING(cl_time,11,9)) >='" + startDate + "'";
				
				sql += " and CONCAT(SUBSTRING(cl_time,7,4),'-',SUBSTRING(cl_time,4,2),'-',SUBSTRING(cl_time,1,2)) <='" + endDate + "'";
			
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
		String sql="select count(*) from sd_new_jkyx where cl02_ren=? and cl02_status=1 and sfyfk=1 and sfyhw=1 and yuq_ts=0 ";
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
		String sql="select count(*) from sd_new_jkyx where cl02_ren=? and cl02_status=1 and sfyfk=1 and yuq_ts>0 ";
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
		String sql="select count(*) from sd_new_jkyx where cl02_ren=? and cl02_status=1 and sfyfk=1 and yuq_ts>0 and sfyhw=0 ";
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
		String sql="select count(*) from sd_new_jkyx where cl03_ren=? and cl03_status<>0";
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
		String sql="select count(*) from sd_new_jkyx where cl_ren=? and cl_status=3";
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
		String sql="select count(*) from sd_new_jkyx where cl_ren=? and cl_status=3";
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
		String sql="select count(*) from sd_new_jkyx where cl02_ren=? and cl02_status=3";
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
		String sql="select count(*) from sd_new_jkyx where cl03_ren=? and cl03_status=3";
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
		String sql="select count(*) from sd_new_jkyx where cl03_ren=? and cl03_status<>0 and sfyfk=1";
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
			sql="select count(*) from sd_new_jkyx where cl03_ren="+userId+" and cl03_status<>0 and sfyfk=1 ";
			
			sql += " and CONCAT(SUBSTRING(cl03_time,7,4),'-',SUBSTRING(cl03_time,4,2),'-',SUBSTRING(cl03_time,1,2),SUBSTRING(cl03_time,11,9)) >='" + startDate + "'";
					
			sql += " and CONCAT(SUBSTRING(cl03_time,7,4),'-',SUBSTRING(cl03_time,4,2),'-',SUBSTRING(cl03_time,1,2)) <='" + endDate + "'";
		}else{
			sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cl03_ren where cl03_ren="+userId+" and cl03_status<>0 and sfyfk=1 and substring(cl03_time,1,10) ='"+nowdate+"' ";
		}
		if(!name.equals("")){
			sql+=" and sdcms_user.NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and sdcms_user.phone like '%"+phone+"%' ";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	public int getNowThreeCountTGOLD(int userId,String startDate,String endDate,String nowdate, String enddate,String name,String phone){
		String sql = "";
		if (!StringHelper.isEmpty(startDate)) {
			sql="select count(*) from sd_new_jkyx where cl03_ren="+userId+" and cl03_status<>0 and sfyfk=1 AND is_old_user=1";
			
			sql += " and CONCAT(SUBSTRING(cl03_time,7,4),'-',SUBSTRING(cl03_time,4,2),'-',SUBSTRING(cl03_time,1,2),SUBSTRING(cl03_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(cl03_time,7,4),'-',SUBSTRING(cl03_time,4,2),'-',SUBSTRING(cl03_time,1,2)) <='" + endDate + "'";
		}else{
			sql="select count(*) from sd_new_jkyx inner join sdcms_user on  sdcms_user.USER_ID=sd_new_jkyx.cl03_ren where cl03_ren="+userId+" and cl03_status<>0 and sfyfk=1 AND is_old_user=1 and substring(cl03_time,1,10) ='"+nowdate+"' ";
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
		
			sql="select count(*) from sd_new_jkyx where cl_ren="+userId+" and cl_status<>0 and substring(cl_time,1,10) ='"+startDate+"' ";
		
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
			sql="SELECT COUNT(jishu) FROM (SELECT COUNT(id) AS jishu FROM sd_new_jkyx WHERE cl_ren="+userId+" AND cl_status<>0 ";
			
				
				sql += " and CONCAT(SUBSTRING(cl_time,7,4),'-',SUBSTRING(cl_time,4,2),'-',SUBSTRING(cl_time,1,2),SUBSTRING(cl_time,11,9)) >='" + startDate + "'";
				
				sql += " and CONCAT(SUBSTRING(cl_time,7,4),'-',SUBSTRING(cl_time,4,2),'-',SUBSTRING(cl_time,1,2)) <='" + endDate + "'";
			
			sql += " GROUP BY userid)a ";
		}else{
			sql="SELECT COUNT(jishu) FROM (SELECT COUNT(id) AS jishu FROM sd_new_jkyx INNER JOIN sdcms_user ON  sdcms_user.USER_ID=sd_new_jkyx.cl_ren WHERE cl_ren="+userId+" AND cl_status<>0 AND SUBSTRING(cl_time,1,10) ='"+nowdate+"' GROUP BY userid)a ";
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
		
			sql="select count(*) from sd_new_jkyx where  cl02_ren="+userId+" and cl02_status<>0 and substring(cl02_time,1,10) ='"+startDate+"' ";
		
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
			sql="SELECT COUNT(jishu) FROM (SELECT COUNT(id) AS jishu FROM sd_new_jkyx INNER JOIN sdcms_user ON  sdcms_user.USER_ID=sd_new_jkyx.cl02_ren WHERE cl02_ren="+userId+" AND cl02_status<>0 ";

			sql += " and CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2),SUBSTRING(cl02_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2)) <='" + endDate + "'";
		
			sql += " GROUP BY userid)a where 1=1 ";
		}else{
			sql="SELECT COUNT(jishu) FROM (SELECT COUNT(id) AS jishu FROM sd_new_jkyx INNER JOIN sdcms_user ON  sdcms_user.USER_ID=sd_new_jkyx.cl02_ren WHERE cl02_ren="+userId+" AND cl02_status<>0 AND SUBSTRING(cl02_time,4,2)='"+nowdate.substring(3, 5)+"' AND SUBSTRING(cl02_time,1,10) ='"+nowdate+"' GROUP BY userid)a where 1=1";
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
		
			sql="select count(*) from sd_new_jkyx where cl_ren="+userId+" and cl_status=3 and substring(cl_time,1,10) ='"+startDate+"' ";
		
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
		
		sql="select count(*) from sd_new_jkyx where cl_ren="+userId+" and cl_status=3 and substring(cl_time,1,10) ='"+startDate+"' ";
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
		
			sql="select count(*) from sd_new_jkyx where  cl02_ren="+userId+" and cl02_status=3 and substring(cl02_time,1,10) ='"+startDate+"' ";
		
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
		
			sql="select count(*) from sd_new_jkyx where cl03_ren="+userId+" and cl03_status<>0 and substring(cl03_time,1,10) ='"+startDate+"' ";
		
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
			sql="SELECT COUNT(jishu) FROM (SELECT COUNT(id) AS jishu FROM sd_new_jkyx INNER JOIN sdcms_user ON  sdcms_user.USER_ID=sd_new_jkyx.cl03_ren WHERE cl03_ren="+userId+" AND cl03_status<>0 ";

			sql += " and CONCAT(SUBSTRING(cl03_time,7,4),'-',SUBSTRING(cl03_time,4,2),'-',SUBSTRING(cl03_time,1,2),SUBSTRING(cl03_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(cl03_time,7,4),'-',SUBSTRING(cl03_time,4,2),'-',SUBSTRING(cl03_time,1,2)) <='" + endDate + "'";
		
			sql += " GROUP BY userid)a where 1=1 ";
		}else{
			sql="SELECT COUNT(jishu) FROM (SELECT COUNT(id) AS jishu FROM sd_new_jkyx INNER JOIN sdcms_user ON  sdcms_user.USER_ID=sd_new_jkyx.cl03_ren WHERE cl03_ren="+userId+" AND cl03_status<>0 AND SUBSTRING(cl03_time,4,2)='"+nowdate.substring(3, 5)+"' AND SUBSTRING(cl03_time,1,10) ='"+nowdate+"' GROUP BY userid)a where 1=1";
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
		
			sql="select count(*) from sd_new_jkyx where cl03_ren="+userId+" and cl03_status=3 and substring(cl03_time,1,10) ='"+startDate+"' ";
		
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
	/**
	 * 修改为还款时间
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public int getZong2hk(String startDate,String endDate){
		String sql="select count(id) from sd_new_jkyx where sfyfk=1 AND hkfq_time IS NULL ";
		if (!StringHelper.isEmpty(startDate)) {
	       sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) >='" + startDate + "'";
		}
		if (!StringHelper.isEmpty(endDate)) {
		 
				sql += " and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) <='" + endDate + "'";
			}
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 修改为还款分期时间
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public int getZong2hkfq(String startDate,String endDate){
		String sql="select count(id) from sd_new_jkyx where sfyfk=1  AND hkfq_time IS  NOT NULL ";
		if (!StringHelper.isEmpty(startDate)) {
	       sql += " and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) >='" + startDate + "'";
		}
		if (!StringHelper.isEmpty(endDate)) {
		 
				sql += " and  CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)) <='" + endDate + "'";
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
	
	/*************************d催收单单分配统计表************Start***********************/
	 /**
	  * 催收小组成员查询
	  * @param curPage
	  * @param numPerPage
	  * @param startDay
	  * @param endDay
	  * @return
	  */
	 public DBPage getcuishoutjUserDbPage(int curPage,int numPerPage, int userId,String name, String phone,String cuishouZ,String startTime,String endtime) {
	   
		 String sql =" SELECT a.user_id,a.state,a.name  FROM (SELECT user_id ,state,NAME,roleid FROM sdcms_user   WHERE (roleid=19 OR roleid=20 OR roleid=21 OR roleid=22 OR roleid=23 OR roleid=25 OR roleid=50 OR roleid=24 OR roleid=51 OR roleid=26 OR roleid=54 OR roleid=60 OR roleid=61 OR roleid=62 or roleid=63 )) a";
	       
	        if (!StringHelper.isEmpty(startTime)) { //f分时间区间
	        	sql +=" LEFT JOIN (SELECT DISTINCT scf.cuishou_id FROM sd_cuishou_fendan scf WHERE SUBSTRING(scf.fendan_time,1,7)>=SUBSTRING('"+startTime+" ',1,7) "
	        			+ " and  SUBSTRING(scf.fendan_time,1,7)<=SUBSTRING('"+ endtime+"',1,7)  AND scf.cuishou_jine>0  ";
	        	if(cuishouZ.equals("M0")) {
		   			 sql+= " AND scf.cuishou_z= 4) b ON a.user_id =b.cuishou_id";
		   		 }else if(cuishouZ.equals("M1")) {
		   			 sql+= " AND scf.cuishou_z= 1) b ON a.user_id =b.cuishou_id";
		   		 }else if(cuishouZ.equals("M2")) {
		   			sql+= " AND scf.cuishou_z= 2) b ON a.user_id =b.cuishou_id";
		   		 }else if(cuishouZ.equals("M3")) {
		   			sql+= " AND scf.cuishou_z= 3) b ON a.user_id =b.cuishou_id";
		   		 }else if(cuishouZ.equals("M3_A")) {
			   			sql+= " AND scf.cuishou_z= 3) b ON a.user_id =b.cuishou_id";
		   		 }else if(cuishouZ.equals("M123")) {
			   			sql+= " AND scf.cuishou_z= 123) b ON a.user_id =b.cuishou_id";
			   	 }else {
		   			sql+= " ) b ON a.user_id =b.cuishou_id";
		   		 }
	        	
//	        	sql+=" LEFT JOIN  (SELECT DISTINCT srd.cuishouid FROM sd_recharge_detail srd WHERE  "
//	        			+ " CONCAT(SUBSTRING(srd.rechargetime,7,4),'-',SUBSTRING(srd.rechargetime,4,2)) >=SUBSTRING('"+startTime + "',1,7)  and CONCAT(SUBSTRING(srd.rechargetime,7,4),'-',SUBSTRING(srd.rechargetime,4,2)) <=SUBSTRING('"+endtime + "',1,7) ";
//	        			
//	        	if(cuishouZ.equals("M0")) {
//		   			 sql+= " AND  srd.dqyqts >0  AND srd.dqyqts <=3 ) c ON a.user_id= c.cuishouid";
//		   		 }else if(cuishouZ.equals("M1")) {
//		   			 sql+= " AND  srd.dqyqts >3  AND srd.dqyqts <=15 ) c ON a.user_id= c.cuishouid";
//		   		 }else if(cuishouZ.equals("M2")) {
//		   			sql+= " AND srd.dqyqts >15  AND srd.dqyqts <=45 ) c ON a.user_id= c.cuishouid";
//		   		 }else if(cuishouZ.equals("M3")) {
//		   			sql+= " AND srd.dqyqts >45 ) c ON a.user_id= c.cuishouid";
//		   		 }else {
//		   			sql+= " AND srd.dqyqts >0 ) c ON a.user_id= c.cuishouid";
//		   		 }
	        			
	        }else {//当月
	        	sql +=" LEFT JOIN (SELECT DISTINCT scf.cuishou_id FROM sd_cuishou_fendan scf WHERE  SUBSTRING(scf.fendan_time,1,7)=SUBSTRING('"+ endtime+"',1,7)  AND scf.cuishou_jine>0  ";
	        	 
	        	if(cuishouZ.equals("M0")) {
		   			 sql+= " AND scf.cuishou_z= 4) b ON a.user_id =b.cuishou_id";
		   		 }else if(cuishouZ.equals("M1")) {
		   			 sql+= " AND scf.cuishou_z= 1) b ON a.user_id =b.cuishou_id";
		   		 }else if(cuishouZ.equals("M2")) {
		   			sql+= " AND scf.cuishou_z= 2) b ON a.user_id =b.cuishou_id";
		   		 }else if(cuishouZ.equals("M3")) {
		   			sql+= " AND scf.cuishou_z= 3) b ON a.user_id =b.cuishou_id ";
		   		 }else if(cuishouZ.equals("M3_A")) {
			   			sql+= " AND scf.cuishou_z= 3) b ON a.user_id =b.cuishou_id";
		   		 }else if(cuishouZ.equals("M123")) {
			   			sql+= " AND scf.cuishou_z= 123) b ON a.user_id =b.cuishou_id";
			   	 }else {
		   			sql+= " ) b ON a.user_id =b.cuishou_id";
		   		 }
	        	
//	        	sql+=" LEFT JOIN  (SELECT DISTINCT srd.cuishouid FROM sd_recharge_detail srd WHERE  CONCAT(SUBSTRING(srd.rechargetime,7,4),'-',SUBSTRING(srd.rechargetime,4,2)) =SUBSTRING('"+endtime+"',1,7) ";
//	        	if(cuishouZ.equals("M0")) {
//		   			 sql+= " AND  srd.dqyqts >0  AND srd.dqyqts <=3 ) c ON a.user_id= c.cuishouid";
//		   		 }else if(cuishouZ.equals("M1")) {
//		   			 sql+= " AND  srd.dqyqts >3  AND srd.dqyqts <=15 ) c ON a.user_id= c.cuishouid";
//		   		 }else if(cuishouZ.equals("M2")) {
//		   			sql+= " AND  srd.dqyqts >15  AND srd.dqyqts <=45 ) c ON a.user_id= c.cuishouid";
//		   		 }else if(cuishouZ.equals("M3")) {
//		   			sql+= " AND srd.dqyqts >45 ) c ON a.user_id= c.cuishouid";
//		   		 }else {
//		   			sql+= " AND srd.dqyqts >0  ) c ON a.user_id= c.cuishouid";
//		   		 }
	        }
	 
	        sql+= " WHERE a.user_id =b.cuishou_id   ";
	        
	        if(cuishouZ.equals("M0")) {
	   			 sql+= "";
	   		 }else if(cuishouZ.equals("M1")) {
	   			 sql+= "";
	   		 }else if(cuishouZ.equals("M2")) {
	   			sql+= "";
	   		 }else if(cuishouZ.equals("M3")) {
	   			sql+= " AND  (a.roleid<>61 and a.roleid<>60)";
	   		 }else if(cuishouZ.equals("M3_A")) {
		   			sql+= " AND  (a.roleid=61 OR a.roleid=60)";
	   		 }else if(cuishouZ.equals("M123")) {
	   			sql+= " AND  ( a.roleid=62 or a.roleid=63)";
		   	 }else {
	   			sql+= "";
	   		 }
	        
	        sql+= " ORDER BY a.state DESC,a.user_id  ";
	    return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
}

	
	
	 /**
	  * 应催收金额  sd_cuishou_fendan
	  * @param cuishouid
	  * @param startTime
	  * @param endtTime
	  * @param MonDayType
	  * @return
	  */
	public double getCSFendanjine(int cuishouid ,String startTime,String endtTime,int MonDayType,String cuishouz) {
			String sql =" SELECT SUM(cuishou_jine) FROM sd_cuishou_fendan WHERE ";

			if (cuishouid > 0) {
				sql+=" cuishou_id=  "+ cuishouid +" and ";
				if (!StringHelper.isEmpty(startTime)) { //界面有时间选择
					sql +=" SUBSTRING(fendan_time,1,10)>='"+ startTime+"'  and SUBSTRING(fendan_time,1,10) <= '" +endtTime+"' ";
				}else if (!StringHelper.isEmpty(endtTime)) {
					if (1 ==MonDayType) {
						sql +=" SUBSTRING(fendan_time,1,10) = '"+ endtTime+"'";
					} else if (2==MonDayType) {
						sql +=" SUBSTRING(fendan_time,1,7) = SUBSTRING('"+ endtTime+"',1,7)";
					}
				}
				
			}else {
				if (!StringHelper.isEmpty(startTime)) { //界面有时间选择
					sql +=" SUBSTRING(fendan_time,1,10)>='"+ startTime+"'  and SUBSTRING(fendan_time,1,10) <= '" +endtTime+"' ";
				}else if (!StringHelper.isEmpty(endtTime)) {
					if (1 ==MonDayType) {
						sql +=" SUBSTRING(fendan_time,1,10)= '"+ endtTime+"'";
					} else if (2==MonDayType) {
						sql +=" SUBSTRING(fendan_time,1,7)=SUBSTRING('"+ endtTime+"',1,7) ";
					}
				}
			}
			if(cuishouz.equals("M0")) {
	   			 sql+= " and cuishou_z=4";
	   		 }else if(cuishouz.equals("M1")) {
	   			 sql+= " and cuishou_z=1";
	   		 }else if(cuishouz.equals("M2")) {
	   			 sql+= " and cuishou_z=2";
	   		 }else if(cuishouz.equals("M3")||cuishouz.equals("M3_A")) {
	   			 sql+= " and cuishou_z=3";
	   		 }else if(cuishouz.equals("M123")) {
	   			 sql+= " and cuishou_z=123";
	   		 }
			//logger.info("getCSTJychzjehk + sql  "+ sql);
			return this.getJdbcTemplate().queryLong(sql);
		}
	
	 /**
	  * 查询未还金额    sd_new_jkyx
	  * @param userid
	  * @param startTime
	  * @param endtTime
	  * @param MonDayType
	  * @return
	  */
	 public double getCSTJycsjinewhw(String cuishouM,int userid ) {
		 String sql ="SELECT SUM(REPLACE(sjsh_money, ',' ,'')+REPLACE(yuq_lx, ',' ,'')) FROM sd_new_jkyx WHERE yuq_ts >0  AND sfyhw =0 and hkfq_code=0 ";
		 
		   if(cuishouM.equals("M0")) {
			   sql+=" AND  yuq_ts > 0 AND  yuq_ts <= 3 and cuishou_id<>0";
		   }else if (cuishouM.equals("M1")) {
				sql+=" AND  yuq_ts > 3 AND  yuq_ts <= 15 and cuishou_id<>0";
			}else if (cuishouM.equals("M2")) {
				sql+=" AND  yuq_ts > 15 AND  yuq_ts <= 60 and cuishou_id<>0";
			}else if (cuishouM.equals("M3")||cuishouM.equals("M3_A")) {
				sql+=" AND  yuq_ts >45 and cuishou_id<>0";
			}else if (cuishouM.equals("M123")) {
				sql+=" AND  yuq_ts > 0 and cuishou_id<>0";
			}else {
				sql+=" AND  yuq_ts >0 ";
			}
		 
		   if (userid>0) {
			   sql+=" and  cuishou_id= "+ userid;
		   }
	
		 return this.getJdbcTemplate().queryLong(sql);
	 }
	 
	 /** 
	  * 时间格式 dd-MM-yyyy
	  * 已经催回金额
	  * @return
	  */
	 public double getCSTJychzje(int userid ,String startTime,String endtTime,int MonDayType,String cuishouz) {
			
		    String setMxaTime ="2019-11-01";  //a从11月份开始增加M0催收组
			Calendar cdar = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				cdar.setTime(sdf.parse(setMxaTime));
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long maxtime=cdar.getTimeInMillis();
			try {
				cdar.setTime(sdf.parse(endtTime));
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long endtime=cdar.getTimeInMillis();
			
			String sql ="SELECT SUM(REPLACE(rechargeMoney,',','')) FROM sd_recharge_detail WHERE dqyqts >0 and remark<>'延期还款'  ";
			if (userid>0) {
				sql +=" and  cuishouid = "+ userid;
			}
			if (!StringHelper.isEmpty(startTime)) { //界面有时间选择
				sql +="  and  CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2))>='"+ startTime+"'  and  CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2)) <= '" +endtTime+"' ";
			}else if (!StringHelper.isEmpty(endtTime)) {
				if (1 ==MonDayType) {
					sql +=" and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2)) = '"+ endtTime+"'";
				} else if (2==MonDayType) {
					sql +="  and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2))  =SUBSTRING('"+ endtTime+"',1,7) ";
				}
			}
			if(maxtime <= endtime) {
				if(cuishouz.equals("M0")) {
					sql+= " and dqyqts > 0 and dqyqts <=3";
				}else if(cuishouz.equals("M1")) {
		   			 sql+= " and dqyqts > 3 and dqyqts <=15";
		   		 }else if(cuishouz.equals("M2")) {
		   			sql+= " and dqyqts > 15 and dqyqts <=60";
		   		 }else if(cuishouz.equals("M3")||cuishouz.equals("M3_A")) {
		   			sql+= " and dqyqts > 45";
		   		 }else if(cuishouz.equals("M123")) {
		   			 sql+= " and dqyqts > 0 ";
		   		 }
			}else {
				if(cuishouz.equals("M0")) {
					sql+= " and dqyqts > 0 and dqyqts <=3";
				}else if(cuishouz.equals("M1")) {
		   			 sql+= " and dqyqts > 3 and dqyqts <=15";
		   		 }else if(cuishouz.equals("M2")) {
		   			sql+= " and dqyqts > 15 and dqyqts <=60";
		   		 }else if(cuishouz.equals("M3")||cuishouz.equals("M3_A")) {
		   			sql+= " and dqyqts > 45";
		   		 }else if(cuishouz.equals("M123")) {
		   			 sql+= " and dqyqts > 0 ";
		   		 }
			}
			
			return this.getJdbcTemplate().queryLong(sql);
		}
	 
	
	 
	 /** 
	  * 时间格式 dd-MM-yyyy
	  * 催收单数 总计
	  * @return
	  */
	 public Integer getCSTJcsds(int userid ,String startTime,String endtTime,int MonDayType,String cuishouz) {
		 StringBuffer sb = new StringBuffer();
		 sb.append("SELECT count(*) FROM sd_cuishou_fendan WHERE  ");
		 
		 if(!StringHelper.isEmpty(startTime)) {
				sb.append("  SUBSTRING(fendan_time,1,10)>='"+startTime +"' and SUBSTRING(fendan_time,1,10)<='"+endtTime+"'") ;
	
			}else if(!StringHelper.isEmpty(endtTime)) {
				if(1==MonDayType) {   //d查询当天
					sb.append(" SUBSTRING(fendan_time,1,10)='"+endtTime+"'");
				}else if(2==MonDayType) {//d查询当月
					sb.append(" SUBSTRING(fendan_time,1,7)= SUBSTRING('"+endtTime+"',1,7)");
				}
			}
			if (userid>0) {
	            sb.append("  and cuishou_id ="+ userid)	;	
			}
			if(cuishouz.equals("M0")) {
				sb.append( " and cuishou_z=4");
	   		 }else if(cuishouz.equals("M1")) {
				sb.append( " and cuishou_z=1");
	   		 }else if(cuishouz.equals("M2")) {
	   			sb.append( " and cuishou_z=2");
	   		 }else if(cuishouz.equals("M3")||cuishouz.equals("M3_A")) {
	   			sb.append( " and cuishou_z=3");
	   		}else if(cuishouz.equals("M123")) {
				sb.append( " and cuishou_z=123");
	   		 }
			sb.append( " and cuishou_jine >0 ");
    //logger.info("getCSTJcsds sb-- "+sb);
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 
//	 /** 
//	  * 时间格式 dd-MM-yyyy
//	  * 已催回单数 月
//	  * @return
//	  */
//	 public Integer getCSTJychds(int userid ,String startTime,String endtTime,int MonDayType) {
//		 StringBuffer sb = new StringBuffer();
//		 sb.append("SELECT count(*) FROM sd_new_jkyx WHERE  yuq_ts>0 AND sfyhw=1 ");
//			if (userid>0) {
//				sb.append(" and cuishou_id = "+ userid);
//				sb.append(" and (cuishou_m1 = "+ userid);
//				sb.append(" or cuishou_m2 = "+ userid);
//				sb.append(" or cuishou_m3 = "+ userid +")");
//			}
//			
//			if(!StringHelper.isEmpty(startTime)) {
//				sb.append(" and  ( (CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))>='"+startTime +"' and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))<='"+endtTime+"')" 
//							+" or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))>='"+startTime +"' and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))<='"+endtTime+"'))");			
//			}else if(!StringHelper.isEmpty(endtTime)) {
//				if(1==MonDayType) {   //d查询当天
//					sb.append(" and  ( CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))='"+endtTime+"')" 
//							+" or (CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))='"+endtTime+"')");
//				}else if(2==MonDayType) {//d查询当月
//					sb.append(" and (  CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2))= SUBSTRING('"+endtTime+"',1,7) ) "
//							+ " or ( CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2))= SUBSTRING('"+endtTime+"',1,7) )");
//				}
//			}
//		 
//		 return getJdbcTemplate().queryInt(sb.toString());
//	 }
	 
	 /** 
	  * 时间格式 dd-MM-yyyy
	  * 当日已催回单数
	  * @return
	  */
	 public Integer getCSTJychds1(int userid ,String startTime,String endtTime,int MonDayType,String cuishouz) {
		 
		 String setMxaTime ="2019-11-01";  //a从11月份开始增加M0催收组
		Calendar cdar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			cdar.setTime(sdf.parse(setMxaTime));
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long maxtime=cdar.getTimeInMillis();
		try {
			cdar.setTime(sdf.parse(endtTime));
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endtime=cdar.getTimeInMillis();
			
		 StringBuffer sb = new StringBuffer();
		 sb.append("SELECT COUNT(r.id) FROM sd_recharge_detail r LEFT JOIN sd_new_jkyx j ON r.rechargenumber=j.id WHERE (r.remark='全额还款' OR r.remark='完成还款，部分逾期利息还款') AND r.dqyqts>0 AND j.sfyhw=1  ");
			if (userid>0) {
				sb.append(" and j.cuishou_id = "+ userid);
				sb.append(" and ((j.cuishou_m0 = "+ userid + " AND r.dqyqts>0  and r.dqyqts<=3 )");
				sb.append(" or (j.cuishou_m1 = "+ userid + " AND r.dqyqts>3  and r.dqyqts<=15 )");
				sb.append(" or (j.cuishou_m2 = "+ userid+ " and r.dqyqts>15 and r.dqyqts<=45) ");
				sb.append(" or (j.cuishou_m3 = "+ userid +" and r.dqyqts>45 ))");
			}

			if(!StringHelper.isEmpty(startTime)) {
					sb.append(" and (CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2))>='"+startTime +"'"
							+ " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2))<='"+endtTime+"')");
			}else if(!StringHelper.isEmpty(endtTime)) {
				if(1==MonDayType) {   //d查询当天
					sb.append(" and ( CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2))='"+endtTime+"')" );
				}else if(2==MonDayType) {//d查询当月
					sb.append(" and  CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2))= SUBSTRING('"+endtTime+"',1,7)  ");
				}
			}
			
			if(maxtime <= endtime) {
				if(cuishouz.equals("M0")) {
					sb.append( " and r.dqyqts > 0 and r.dqyqts <=3");
		   		 }else if(cuishouz.equals("M1")) {
					sb.append( " and r.dqyqts > 3 and r.dqyqts <=15");
		   		 }else if(cuishouz.equals("M2")) {
		   			sb.append( " and r.dqyqts > 15 and r.dqyqts <=60");
		   		 }else if(cuishouz.equals("M3")||cuishouz.equals("M3_A")) {
		   			sb.append( " and r.dqyqts >45");
		   		}else if(cuishouz.equals("M123")) {
					sb.append( " and r.dqyqts > 0");
		   		 }
			}else {
				if(cuishouz.equals("M0")) {
					sb.append( " and r.dqyqts > 0 and r.dqyqts <=3");
		   		 }else if(cuishouz.equals("M1")) {
					sb.append( " and r.dqyqts > 3 and r.dqyqts <=15");
		   		 }else if(cuishouz.equals("M2")) {
		   			sb.append( " and r.dqyqts > 15 and r.dqyqts <=60");
		   		 }else if(cuishouz.equals("M3")||cuishouz.equals("M3_A")) {
		   			sb.append( " and r.dqyqts >45");
		   		}else if(cuishouz.equals("M123")) {
					sb.append( " and r.dqyqts > 0");
		   		 }
			}
			
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 
	 /** 
	  * 时间格式 dd-MM-yyyy
	  * 未催回单数
	  * @return
	  */
	 public Integer getCSTJwchds(int userid ,String startTime,String endtTime,int MonDayType,String cuishouz) {
		 
		String setMxaTime ="2019-11-01";  //a从11月份开始增加M0催收组
		Calendar cdar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			cdar.setTime(sdf.parse(setMxaTime));
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long maxtime=cdar.getTimeInMillis();
		try {
			cdar.setTime(sdf.parse(endtTime));
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endtime=cdar.getTimeInMillis();
			
		 StringBuffer sb = new StringBuffer();
		 sb.append("SELECT count(*) FROM sd_new_jkyx WHERE  yuq_ts>0 AND sfyhw=0  ");
			if (userid>0) {
				sb.append(" and cuishou_id = "+ userid);
				sb.append(" and (cuishou_m0 = "+ userid);
				sb.append(" or cuishou_m1 = "+ userid);
				sb.append(" or cuishou_m2 = "+ userid);
				sb.append(" or cuishou_m3 = "+ userid +")");
			}else {
				if(!StringHelper.isEmpty(startTime)) {
						sb.append(" and  ( (CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))>='"+startTime +"' and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))<='"+endtTime+"' AND hkfq_time IS NULL)" 
								+" or (hkfq_code = 0 AND CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))>='"+startTime +"' and CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))<='"+endtTime+"' AND hkfq_time IS NULL))");			
				}else if(!StringHelper.isEmpty(endtTime)) {
					if(1==MonDayType) {   //d查询当天
						sb.append(" and  ( CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))='"+endtTime+"' AND hkfq_time IS NULL)" 
								+" or ( hkfq_code = 0 AND CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))='"+endtTime+"')");
					}else if(2==MonDayType) {//d查询当月
						sb.append(" and (  CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2))= SUBSTRING('"+endtTime+"',1,7) AND hkfq_time IS NULL ) "
								+ " or ( hkfq_code = 0 AND CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2))= SUBSTRING('"+endtTime+"',1,7) )");
					}
				}
			}
			
			if(maxtime <= endtime) {
				if(cuishouz.equals("M0")) {
					sb.append( " and yuq_ts > 0 and yuq_ts <=3");
		   		 }else if(cuishouz.equals("M1")) {
					sb.append( " and yuq_ts > 3 and yuq_ts <=15");
		   		 }else if(cuishouz.equals("M2")) {
		   			sb.append( " and yuq_ts > 15 and yuq_ts <=60");
		   		 }else if(cuishouz.equals("M3")||cuishouz.equals("M3_A")) {
		   			sb.append( " and yuq_ts >45");
		   		}else if(cuishouz.equals("M123")) {
					sb.append( " and yuq_ts > 0");
		   		 }
			}else {
				if(cuishouz.equals("M0")) {
					sb.append( " and yuq_ts > 0 and yuq_ts <=3");
		   		 }else if(cuishouz.equals("M1")) {
					sb.append( " and yuq_ts > 3 and yuq_ts <=15");
		   		 }else if(cuishouz.equals("M2")) {
		   			sb.append( " and yuq_ts > 15 and yuq_ts <=60");
		   		 }else if(cuishouz.equals("M3")||cuishouz.equals("M3_A")) {
		   			sb.append( " and yuq_ts >45");
		   		}else if(cuishouz.equals("M123")) {
					sb.append( " and yuq_ts > 0");
		   		 }
			}
			
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 
	 
	 /** 
	  * 时间格式 dd-MM-yyyy
	  * 催收已提醒 未还款用户
	  * @return
	  */
	 public Integer getCSTJytxwhk(int userid ,String startTime,String endtTime,int MonDayType,String cuishouz) {
		 StringBuffer sb = new StringBuffer();
		 sb.append("SELECT count(DISTINCT sc.jkid) FROM sd_new_jkyx  snj LEFT JOIN sd_csmsg sc ON snj.id=sc.jkid WHERE snj.sfyhw=0  ");
			if (userid>0) {
				sb.append(" and ( (cuishou_m0 = "+ userid +" and sc.cl_ren = snj.cuishou_m0 )");
				sb.append(" or (cuishou_m1 = "+ userid +" and sc.cl_ren = snj.cuishou_m1 )");
				sb.append(" or( cuishou_m2 = "+ userid +" and sc.cl_ren = snj.cuishou_m2) ");
				sb.append(" or (cuishou_m3 = "+ userid +" and sc.cl_ren = snj.cuishou_m3) "+")");
			}
			if(!StringHelper.isEmpty(startTime)) {
				if(1==MonDayType) {   //d查询当天
					sb.append(" and ( SUBSTRING(hkyq_time,1,10)= '"+startTime+"' or SUBSTRING(hkfq_time,1,10)= '"+startTime+"')");
				}else if(2==MonDayType) {//d查询当月
					sb.append(" and (SUBSTRING(hkyq_time,4,7)= '"+startTime+"' or SUBSTRING(hkfq_time,4,7)= '"+startTime+"')");
				}
			}else if(!StringHelper.isEmpty(endtTime)) {
				if(1==MonDayType) {   //d查询当天
					sb.append(" and ( SUBSTRING(hkyq_time,1,10)= '"+endtTime+"' or SUBSTRING(hkfq_time,1,10)= '"+endtTime+"')");
				}else if(2==MonDayType) {//d查询当月
					sb.append(" and ( SUBSTRING(hkyq_time,4,7)= '"+endtTime+"' or SUBSTRING(hkfq_time,1,10)= '"+endtTime+"')");
				}
			}
		 
			if(cuishouz.equals("M0")) {
				sb.append( " and snj.yuq_ts > 0 and snj.yuq_ts <=3");
	   		 }else if(cuishouz.equals("M1")) {
				sb.append( " and snj.yuq_ts > 3 and snj.yuq_ts <=15");
	   		 }else if(cuishouz.equals("M2")) {
	   			sb.append( " and snj.yuq_ts > 15 and snj.yuq_ts <=60");
	   		 }else if(cuishouz.equals("M3")||cuishouz.equals("M3_A")) {
	   			sb.append( " and snj.yuq_ts >45");
	   		}else if(cuishouz.equals("M123")) {
				sb.append( " and snj.yuq_ts > 0 ");
	   		 }
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 
	 
	 /** 
	  * 时间格式 create_time   yyyy-MM-dd
	  * 催收人员提醒数量且未还款用户
	  * @return
	  */
	 public Integer getCSTJtxzs(int userid ,String startTime,String curnowTime,int MonDayType,String cuishouz) {
		 StringBuffer sb = new StringBuffer();
		 sb.append("SELECT  COUNT(DISTINCT sc.jkid)  FROM sd_new_jkyx  snj LEFT JOIN sd_csmsg sc ON snj.id=sc.jkid WHERE  snj.sfyhw=0  ");
			if (userid>0) {
				sb.append("  and sc.cl_ren =" + userid+" and snj.cuishou_id = "+ userid);
				sb.append(" and ((snj.cuishou_m0 = "+ userid + " AND snj.yuq_ts>0 and snj.yuq_ts <=3  )");
				sb.append(" and (snj.cuishou_m1 = "+ userid + " AND snj.yuq_ts>3 and snj.yuq_ts <=15  )");
				sb.append(" or (snj.cuishou_m2 = "+ userid+ " and snj.yuq_ts>15 and snj.yuq_ts <=45) ");
				sb.append(" or (snj.cuishou_m3 = "+ userid +" and snj.yuq_ts>45 ))");
			}
			else {
				sb.append(" and SUBSTRING( sc.cl_ren,1,2) = 50" );
			}
			if(!StringHelper.isEmpty(curnowTime)) {
				if(1==MonDayType) {   //d查询当天
					sb.append(" and SUBSTRING( sc.create_time,1,10) >= '"+startTime+"' and SUBSTRING( sc.create_time,1,10)<='"+curnowTime+"'");
				}else if(2==MonDayType) {//d查询当月
					sb.append(" and SUBSTRING( sc.create_time,1,7)= SUBSTRING('"+curnowTime+"',1,7)");
				}
			}
			if(cuishouz.equals("M0")) {
				sb.append( " and snj.yuq_ts > 0 and snj.yuq_ts <=3");
	   		 }else if(cuishouz.equals("M1")) {
				sb.append( " and snj.yuq_ts > 3 and snj.yuq_ts <=15");
	   		 }else if(cuishouz.equals("M2")) {
	   			sb.append( " and snj.yuq_ts > 15 and snj.yuq_ts <=60");
	   		 }else if(cuishouz.equals("M3")||cuishouz.equals("M3_A")) {
	   			sb.append( " and snj.yuq_ts >45");
	   		}else if(cuishouz.equals("M123")) {
				sb.append( " and snj.yuq_ts > 0 ");
	   		 }
			sb.append(" AND  sc.msgtype='备注' ");
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 
	 /**
	  * d当日分单数
	  * @param cuishouid
	  * @param startTime
	  * @param curnowTime
	  * @param MonDayType
	  * @param cuishouz
	  * @return
	  */
	 public Integer getcuitimefendanAccount(int cuishouid ,String startTime,String curnowTime,String cuishouz) {

		 StringBuffer sb = new StringBuffer();
		 sb.append("SELECT COUNT(*) FROM sd_cuishou_fendan	WHERE cuishou_jine>0  ");
		 
		 if(cuishouid>0) {
			 sb.append(" and cuishou_id ="+cuishouid);
		 }
		 
		 if(cuishouz.equals("M0")) {
				sb.append( " and cuishou_z= 4");
		 }else if(cuishouz.equals("M1")) {
				sb.append( " and cuishou_z= 1 ");
		 }else if(cuishouz.equals("M2")) {
			sb.append( " and  cuishou_z= 2");
		 }else if(cuishouz.equals("M3")||cuishouz.equals("M3_A")) {
			sb.append( " and cuishou_z= 3 ");
		 }else if(cuishouz.equals("M123")) {
				sb.append( " and cuishou_z= 123 ");
		 }
		 if(!StringHelper.isEmpty(startTime)) {
			 sb.append(" and SUBSTRING(fendan_time,1,10) >= '"+startTime+"' and SUBSTRING(fendan_time,1,10)<='"+curnowTime+"'");
		 }else {
			 sb.append(" and SUBSTRING(fendan_time,1,10) ='" +curnowTime+"'");
		 }
		 return getJdbcTemplate().queryInt(sb.toString());
	
	 }
	 
	 /**
	  * d逾期再延期数量
	  * @param cuishouid
	  * @param startTime
	  * @param curnowTime
	  * @param cuishouz
	  * @return
	  */
	 public Integer getcuishouhkyqAccount(int cuishouid ,String startTime,String curnowTime,int MonDayType,String cuishouz) {
		 String setMxaTime ="2019-11-01";  //a从11月份开始增加M0催收组
		Calendar cdar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			cdar.setTime(sdf.parse(setMxaTime));
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long maxtime=cdar.getTimeInMillis();
		try {
			cdar.setTime(sdf.parse(curnowTime));
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endtime=cdar.getTimeInMillis();
			
		 StringBuffer sb = new StringBuffer();
		 sb.append("SELECT COUNT(*) FROM sd_recharge_detail WHERE dqyqts> 0  AND remark='延期还款'  ");
		 
		 if(cuishouid>0) {
			 sb.append(" and cuishouid ="+cuishouid);
		 }
		 
		 if(maxtime <= endtime) {
			 if(cuishouz.equals("M0")) {
					sb.append( " and dqyqts > 0 and dqyqts <=3 ");
	  		 }else if(cuishouz.equals("M1")) {
					sb.append( " and dqyqts > 3 and dqyqts <16 ");
	  		 }else if(cuishouz.equals("M2")) {
	  			sb.append( " and  dqyqts >15 and dqyqts <=60");
	  		 }else if(cuishouz.equals("M3")||cuishouz.equals("M3_A")) {
	  			sb.append( " and dqyqts > 46 ");
	  		}else if(cuishouz.equals("M123")) {
				sb.append( " and dqyqts > 0");
	  		 }
		 }else {
			 if(cuishouz.equals("M0")) {
					sb.append( " and dqyqts > 0 and dqyqts <=3 ");
	  		 }else if(cuishouz.equals("M1")) {
					sb.append( " and dqyqts > 3 and dqyqts <16 ");
	  		 }else if(cuishouz.equals("M2")) {
	  			sb.append( " and  dqyqts >15 and dqyqts <=60");
	  		 }else if(cuishouz.equals("M3")||cuishouz.equals("M3_A")) {
	  			sb.append( " and dqyqts > 46 ");
	  		}else if(cuishouz.equals("M123")) {
				sb.append( " and dqyqts > 0");
	  		 }
		 }
		 
		 
		 if(!StringHelper.isEmpty(startTime)) {
				sb.append(" and (CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2))>='"+startTime +"'"
						+ " and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2))<='"+curnowTime+"')");
		}else if(!StringHelper.isEmpty(curnowTime)) {
			if(1==MonDayType) {   //d查询当天
				sb.append(" and ( CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2))='"+curnowTime+"')" );
			}else if(2==MonDayType) {//d查询当月
				sb.append(" and  CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2))= SUBSTRING('"+curnowTime+"',1,7)  ");
			}
		}
		 
		 return getJdbcTemplate().queryInt(sb.toString());
		 
	 }
	 
	 /***************/
//	 /**
//	  * 查询未还金额    sd_new_jkyx
//	  * @param userid
//	  * @param startTime
//	  * @param endtTime
//	  * @param MonDayType
//	  * @return
//	  */
//	 public double getCSTJycsjinewhw(String cuishouM,int userid ) {
//		 String sql ="SELECT SUM(REPLACE(sjsh_money, ',' ,'')+REPLACE(yuq_lx, ',' ,'')) FROM sd_new_jkyx WHERE yuq_ts >0  AND sfyhw =0 ";
//		 
//		 if (userid>0) {
//			 if (cuishouM.equals("m1")) {
//				sql+="AND  yuq_ts > 0 AND  yuq_ts <= 15 and cuishou_m1 ="+ userid;
//			}else if (cuishouM.equals("m2")) {
//				sql+="AND  yuq_ts > 15 AND  yuq_ts <= 45 and cuishou_m2 ="+ userid;
//			}else if (cuishouM.equals("m3")) {
//				sql+="AND  yuq_ts >45 and cuishou_m3 ="+ userid;
//			}else {
//				sql+=" and  cuishou_id= "+ userid;
//			}
//		}
//	
//		 return this.getJdbcTemplate().queryLong(sql);
//	 }
//	 /**
//	  * 当前已催回金额  sd_new_jkyx  sd_recharge_detail
//	  * @param cuishouM
//	  * @param userid
//	  * @param startTime
//	  * @param endtTime
//	  * @param MonDayType
//	  * @return
//	  */
//	 public double getCSTJychjine(int userid ,String startTime,String endtTime,int MonDayType) {
//		 
//		 String sql =" SELECT SUM(REPLACE(A.sjsh_money, ',' ,'')+REPLACE(A.yuq_lx, ',' ,'')) ";
//		        sql+=" FROM sd_new_jkyx A, sd_recharge_detail B WHERE (B.remark='全额还款' OR B.remark='完成还款，部分逾期利息还款')  and  B.dqyqts > 0 and A.id=B.rechargeNumber  ";
//		        sql+=" and B.cuishouid ="+ userid ;
//		        
//		        if (!StringHelper.isEmpty(startTime)) { //界面有时间选择
//					sql +=" and CONCAT(SUBSTRING(B.rechargetime,7,4),'-',SUBSTRING(B.rechargetime,4,2),'-',SUBSTRING(B.rechargetime,1,2))>='"+ startTime+"'  and  CONCAT(SUBSTRING(B.rechargetime,7,4),'-',SUBSTRING(B.rechargetime,4,2),'-',SUBSTRING(B.rechargetime,1,2)) <= '" +endtTime+"' ";
//				}else if (!StringHelper.isEmpty(endtTime)) {
//					if (1 ==MonDayType) {
//						sql +=" and  CONCAT(SUBSTRING(B.rechargetime,7,4),'-',SUBSTRING(B.rechargetime,4,2),'-',SUBSTRING(B.rechargetime,1,2)) = '"+ endtTime+"'";
//					} else if (2==MonDayType) {
//						sql +=" and  CONCAT(SUBSTRING(B.rechargetime,7,4),'-',SUBSTRING(B.rechargetime,4,2))  =SUBSTRING('"+ endtTime+"',1,7) ";
//					}
//				}
//		 return this.getJdbcTemplate().queryLong(sql);
//	 }
	 
/************************d催收单单分配统计表************end***********************/

		 
	/**************************20190627 新老用户逾期率统计表************Start**********************/
		 /**
		  * 时间格式   hkyq_time  hkfq_time   dd-MM-yyyy
		  * @param indextype    0  查询总数， 1 查询时间为hkyq_time，  2 查询时间为  hkfq_time
		  * @param isolduser    ture  老用户，  false 总数
		  * @param isyhkstate   0 未还款   1 已还款
		  * @param minNum
		  * @param maxNum
		  * @param startTime
		  * @param endtTime
		  * @param MonDayType     1 为日 ，2 为月
		  * @return
		  */
		 public Integer getnewoldyqltjInteger(int indextype, boolean isolduser,int  isyhkstate,int minNum,int maxNum,String startTime,String endtTime,int MonDayType ) {
			 StringBuffer sb = new StringBuffer();

			 sb.append("SELECT count(*) FROM sd_new_jkyx A where A.sfyfk=1  ");
			 
			 if (isolduser==true) {
				sb.append(" and EXISTS (SELECT userid FROM sd_new_jkyx B WHERE B.sfyhw=1 AND B.sfyfk=1 AND A.userid=B.userid) ");
			 }
			 
			 if (0==isyhkstate) {
				  sb.append(" and  A.sfyhw =0 ");
			 }else if (isyhkstate==1) {
				  sb.append(" and  A.sfyhw =1 ");
		     } 
			 
			 if (minNum >-1) {
				 sb.append(" and A.yuq_ts >"+ minNum);
			  }
			 if (maxNum >-1 && maxNum>= minNum) {
				 sb.append(" and A.yuq_ts <="+ maxNum);
			  }
			 
			 if(!StringHelper.isEmpty(startTime)) {
				 if(1==indextype) {  // hkyq_time
					 if(1==MonDayType) {   //d查询当天
							sb.append(" and  SUBSTRING(hkyq_time,1,10)= '"+startTime+"'");
						}else if(2==MonDayType) {//d查询当月
							sb.append(" and SUBSTRING(hkyq_time,4,7)= '"+startTime+"'");
						}
					}else if(2==indextype) { //hkfq_time
						if(1==MonDayType) {   //d查询当天
							sb.append(" and SUBSTRING(hkfq_time,1,10)= '"+startTime+"'");
						}else if(2==MonDayType) {//d查询当月
							sb.append(" and  SUBSTRING(hkfq_time,1,10)= '"+startTime+"'");
						}
					}
				}else if(!StringHelper.isEmpty(endtTime)) {
					 if(1==indextype) {  // hkyq_time
						 if(1==MonDayType) {   //d查询当天
								sb.append(" and  SUBSTRING(hkyq_time,1,10)= '"+endtTime+"'");
							}else if(2==MonDayType) {//d查询当月
								sb.append(" and SUBSTRING(hkyq_time,4,7)= '"+endtTime+"'");
							}
						}else if(2==indextype) { //hkfq_time
							if(1==MonDayType) {   //d查询当天
								sb.append(" and SUBSTRING(hkfq_time,1,10)= '"+endtTime+"'");
							}else if(2==MonDayType) {//d查询当月
								sb.append(" and  SUBSTRING(hkfq_time,1,10)= '"+endtTime+"'");
							}
						}
				}
			 return getJdbcTemplate().queryInt(sb.toString());
		 }
		 
		 /**
		  * 时间格式   hkyq_time  hkfq_time   dd-MM-yyyy
		  * @param indextype    0  查询总数， 1 查询时间为hkyq_time，  2 查询时间为  hkfq_time
		  * @param isolduser    ture  老用户，  false 总数
		  * @param sfyhwstate   0 未还款   1 已还款
		  * @param minNum
		  * @param maxNum
		  * @param startTime
		  * @param endtTime
		  * @param MonDayType     1 为日 ，2 为月
		  * @return
		  */
		 public DBPage getnewoldyqltjdDbPage(int curPage, int numPerPage ,int indextype, boolean isolduser,int  sfyhwstate,int minNum,int maxNum,String startTime,String endtTime,int MonDayType ) {
			 StringBuffer sb = new StringBuffer();
			 
			if(1==indextype) {  // hkyq_time
				if(1==MonDayType) {   //d查询当天
					sb.append("SELECT CONCAT(SUBSTRING(A.hkyq_time,7,4),'-',SUBSTRING(A.hkyq_time,4,2),'-',SUBSTRING(A.hkyq_time,1,2)) AS riqi,COUNT(A.userid) AS yh_total ");
				}else if(2==MonDayType) {//d查询当月
					sb.append("SELECT CONCAT(SUBSTRING(A.hkyq_time,7,4),'-',SUBSTRING(A.hkyq_time,4,2)) AS riqi,COUNT(A.userid) AS yh_total ");
				}else {
					sb.append("SELECT count(*) ");
				}
			}else if(2==indextype) { //hkfq_time
				if(1==MonDayType) {   //d查询当天
					sb.append("SELECT CONCAT(SUBSTRING(A.hkfq_time,7,4),'-',SUBSTRING(A.hkfq_time,4,2),'-',SUBSTRING(A.hkfq_time,1,2)) AS riqi,COUNT(A.userid) AS yh_total ");
				}else if(2==MonDayType) {//d查询当月
					sb.append("SELECT CONCAT(SUBSTRING(A.hkfq_time,7,4),'-',SUBSTRING(A.hkfq_time,4,2)) AS riqi,COUNT(A.userid) AS yh_total ");
				}else {
					sb.append("SELECT count(*) ");
				}
			}else {
				sb.append("SELECT count(*) ");
			}
			 
			 sb.append(" FROM sd_new_jkyx A where A.sfyfk=1 ");
			 
			 if (isolduser==true) {
				sb.append(" and EXISTS (SELECT userid FROM sd_new_jkyx B WHERE B.sfyhw=1 AND B.sfyfk=1 AND A.userid=B.userid) ");
			 }
			 
			 if (0==sfyhwstate) {
					sb.append(" and  A.sfyhw =0 ");
			 }else if (sfyhwstate==1) {
					sb.append(" and  A.sfyhw =1 ");
		     } 
			 
			 if (minNum >-1) {
				 sb.append(" and A.yuq_ts >"+ minNum);
			  }
			 if (maxNum >-1 &&  maxNum >= minNum) {
				 sb.append(" and A.yuq_ts <="+ maxNum);
			  }
			 
			 if(!StringHelper.isEmpty(startTime)) {
				   if(1==indextype) {  // hkyq_time
					 if(1==MonDayType) {   //d查询当天
							sb.append(" AND A.hkfq_time IS NULL and  CONCAT(SUBSTRING(A.hkyq_time,7,4),'-',SUBSTRING(A.hkyq_time,4,2),'-',SUBSTRING(A.hkyq_time,1,2)) >= '"+startTime+"' and CONCAT(SUBSTRING(A.hkyq_time,7,4),'-',SUBSTRING(A.hkyq_time,4,2),'-',SUBSTRING(A.hkyq_time,1,2)) <= '"+endtTime+"'");
							sb.append(" GROUP BY   CONCAT(SUBSTRING(A.hkyq_time,7,4),'-',SUBSTRING(A.hkyq_time,4,2),'-',SUBSTRING(A.hkyq_time,1,2))"
									+ " ORDER BY  SUBSTRING(A.hkyq_time,7,4) DESC, SUBSTRING(A.hkyq_time,4,2) DESC, SUBSTRING(A.hkyq_time,1,2) DESC");
						}else if(2==MonDayType) {//d查询当月
							sb.append(" AND A.hkfq_time IS NULL and CONCAT(SUBSTRING(A.hkyq_time,7,4),'-',SUBSTRING(A.hkyq_time,4,2),'-',SUBSTRING(A.hkyq_time,1,2))>= '"+startTime+"' and CONCAT(SUBSTRING(A.hkyq_time,7,4),'-',SUBSTRING(A.hkyq_time,4,2),'-',SUBSTRING(A.hkyq_time,1,2)) <= '"+endtTime+"'");
							sb.append(" GROUP BY   CONCAT(SUBSTRING(A.hkyq_time,7,4),'-',SUBSTRING(A.hkyq_time,4,2))  ORDER BY  SUBSTRING(A.hkyq_time,7,4) DESC, SUBSTRING(A.hkyq_time,4,2) DESC");
						}
					}else if(2==indextype) { //hkfq_time
						if(1==MonDayType) {   //d查询当天
							sb.append(" and CONCAT(SUBSTRING(A.hkfq_time,7,4),'-',SUBSTRING(A.hkfq_time,4,2),'-',SUBSTRING(A.hkfq_time,1,2))>= '"+startTime+"' and CONCAT(SUBSTRING(A.hkfq_time,7,4),'-',SUBSTRING(A.hkfq_time,4,2),'-',SUBSTRING(A.hkfq_time,1,2)) <= '"+endtTime+"'");
							sb.append(" GROUP BY  CONCAT(SUBSTRING(A.hkfq_time,7,4),'-',SUBSTRING(A.hkfq_time,4,2),'-',SUBSTRING(A.hkfq_time,1,2))"
									+ " ORDER BY  SUBSTRING(A.hkfq_time,7,4) DESC, SUBSTRING(A.hkfq_time,4,2) DESC, SUBSTRING(A.hkfq_time,1,2) DESC");
						}else if(2==MonDayType) {//d查询当月
							sb.append(" and  CONCAT(SUBSTRING(A.hkfq_time,7,4),'-',SUBSTRING(A.hkfq_time,4,2),'-',SUBSTRING(A.hkfq_time,1,2))>= '"+startTime+"' and CONCAT(SUBSTRING(A.hkfq_time,7,4),'-',SUBSTRING(A.hkfq_time,4,2),'-',SUBSTRING(A.hkfq_time,1,2)) <= '"+endtTime+"'");
							sb.append(" GROUP BY CONCAT(SUBSTRING(A.hkfq_time,7,4),'-',SUBSTRING(A.hkfq_time,4,2)) ORDER BY  SUBSTRING(A.hkfq_time,7,4) DESC, SUBSTRING(A.hkfq_time,4,2) DESC");
						}
					}
				}else {
					if(1==indextype) {  // hkyq_time
						 if(1==MonDayType) {   //d查询当天
								sb.append(" AND A.hkfq_time IS NULL");
								sb.append(" GROUP BY   CONCAT(SUBSTRING(A.hkyq_time,7,4),'-',SUBSTRING(A.hkyq_time,4,2),'-',SUBSTRING(A.hkyq_time,1,2))"
										+ " ORDER BY SUBSTRING(A.hkyq_time,7,4) DESC, SUBSTRING(A.hkyq_time,4,2) DESC, SUBSTRING(A.hkyq_time,1,2) DESC");
							}else if(2==MonDayType) {//d查询当月
								sb.append(" AND A.hkfq_time IS NULL");
								sb.append(" GROUP BY CONCAT(SUBSTRING(A.hkyq_time,7,4),'-',SUBSTRING(A.hkyq_time,4,2)) ORDER BY  SUBSTRING(A.hkyq_time,7,4) DESC, SUBSTRING(A.hkyq_time,4,2) DESC");
							}
						}else if(2==indextype) { //hkfq_time
							if(1==MonDayType) {   //d查询当天
								
								sb.append(" GROUP BY   CONCAT(SUBSTRING(A.hkfq_time,7,4),'-',SUBSTRING(A.hkfq_time,4,2),'-',SUBSTRING(A.hkfq_time,1,2))"
										+ " ORDER BY  SUBSTRING(A.hkfq_time,7,4) DESC, SUBSTRING(A.hkfq_time,4,2) DESC, SUBSTRING(A.hkfq_time,1,2) DESC");
							}else if(2==MonDayType) {//d查询当月
								
								sb.append(" GROUP BY  CONCAT(SUBSTRING(A.hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2)) ORDER BY SUBSTRING(A.hkfq_time,7,4) DESC, SUBSTRING(A.hkfq_time,4,2) DESC");
							}
						}
				}
			 return getJdbcTemplate().queryPage(sb.toString(), curPage, numPerPage);
		 }
		 
		 
		 
//		 /**
//		  * 待还款统计
//		  * @param indextype    1 hkyq_time ; 2 hkfq_time
//		  * @param isolduser
//		  * @param sfyhwstate    sfyhw  0 ; 1 
//		  * @param minNum
//		  * @param maxNum
//		  * @param startTime
//		  * @param endtTime
//		  * @param MonDayType    1 日；2月
//		  * @return
//		  */
//		public DBPage getdhkaccountdDbPage(int curPage, int numPerPage ,int indextype,boolean isolduser,int  sfyhwstate,String startTime,String endtTime,int MonDayType ) {
//			
//			StringBuffer sb = new StringBuffer();
//			
//			if(1==indextype) {  // hkyq_time
//				if(1==MonDayType) {   //d查询当天
//					sb.append("SELECT CONCAT(SUBSTRING(A.hkyq_time,7,4),'-',SUBSTRING(A.hkyq_time,4,2),'-',SUBSTRING(A.hkyq_time,1,2)) AS riqi,COUNT(A.userid) AS yh_total ");
//				}else if(2==MonDayType) {//d查询当月
//					sb.append("SELECT CONCAT(SUBSTRING(A.hkyq_time,7,4),'-',SUBSTRING(A.hkyq_time,4,2)) AS riqi,COUNT(A.userid) AS yh_total ");
//				}else {
//					sb.append("SELECT count(*) ");
//				}
//			}else if(2==indextype) { //hkfq_time
//				if(1==MonDayType) {   //d查询当天
//					sb.append("SELECT CONCAT(SUBSTRING(A.hkfq_time,7,4),'-',SUBSTRING(A.hkfq_time,4,2),'-',SUBSTRING(A.hkfq_time,1,2)) AS riqi,COUNT(A.userid) AS yh_total ");
//				}else if(2==MonDayType) {//d查询当月
//					sb.append("SELECT CONCAT(SUBSTRING(A.hkfq_time,7,4),'-',SUBSTRING(A.hkfq_time,4,2)) AS riqi,COUNT(A.userid) AS yh_total ");
//				}else {
//					sb.append("SELECT count(*) ");
//				}
//			}else {
//				sb.append("SELECT count(*) ");
//			}
	//
//			 sb.append(" FROM sd_new_jkyx A where A.sfyfk=1  ");
//			 
//			 if (isolduser==true) {
//				sb.append(" and EXISTS (SELECT userid FROM sd_new_jkyx B WHERE B.sfyhw=1 AND B.sfyfk=1 AND A.userid=B.userid) ");
//			 }
//			 
//			 if (0==sfyhwstate) {
//				  sb.append(" and  A.sfyhw =0 ");
//			 }else if (sfyhwstate==1) {
//				  sb.append(" and  A.sfyhw =1 ");
//		     } 
//			 
//			 
//			 if(!StringHelper.isEmpty(startTime)) {
//				 if(1==MonDayType) {   //d查询当天
//						sb.append(" and  (SUBSTRING(hkyq_time,1,10)= '"+startTime+"' " +" or SUBSTRING(hkfq_time,1,10)= '"+startTime+"')");
//					}else if(2==MonDayType) {//d查询当月
//						sb.append(" and ( SUBSTRING(hkyq_time,4,7)= '"+startTime+"'"+" or  SUBSTRING(hkfq_time,1,10)= '"+startTime+"')");
//					}
//				}else if(!StringHelper.isEmpty(endtTime)) {
//					if(1==MonDayType) {   //d查询当天
//						sb.append(" and  (SUBSTRING(hkyq_time,1,10)= '"+startTime+"' " +" or SUBSTRING(hkfq_time,1,10)= '"+startTime+"')");
//					}else if(2==MonDayType) {//d查询当月
//						sb.append(" and ( SUBSTRING(hkyq_time,4,7)= '"+startTime+"'"+" or  SUBSTRING(hkfq_time,1,10)= '"+startTime+"')");
//					}
//				}
//			 if(1==indextype) {  // hkyq_time
//				 if(1==MonDayType) {   //d查询当天
//						sb.append(" AND A.hkfq_time IS NULL");
//						sb.append(" GROUP BY   CONCAT(SUBSTRING(A.hkyq_time,7,4),'-',SUBSTRING(A.hkyq_time,4,2),'-',SUBSTRING(A.hkyq_time,1,2))"
//								+ " ORDER BY SUBSTRING(A.hkyq_time,7,4) DESC, SUBSTRING(A.hkyq_time,4,2) DESC, SUBSTRING(A.hkyq_time,1,2) DESC");
//					}else if(2==MonDayType) {//d查询当月
//						sb.append(" AND A.hkfq_time IS NULL");
//						sb.append(" GROUP BY CONCAT(SUBSTRING(A.hkyq_time,7,4),'-',SUBSTRING(A.hkyq_time,4,2)) ORDER BY  SUBSTRING(A.hkyq_time,7,4) DESC, SUBSTRING(A.hkyq_time,4,2) DESC");
//					}
//				}else if(2==indextype) { //hkfq_time
//					if(1==MonDayType) {   //d查询当天
//						
//						sb.append(" GROUP BY   CONCAT(SUBSTRING(A.hkfq_time,7,4),'-',SUBSTRING(A.hkfq_time,4,2),'-',SUBSTRING(A.hkfq_time,1,2))"
//								+ " ORDER BY  SUBSTRING(A.hkfq_time,7,4) DESC, SUBSTRING(A.hkfq_time,4,2) DESC, SUBSTRING(A.hkfq_time,1,2) DESC");
//					}else if(2==MonDayType) {//d查询当月
//						
//						sb.append(" GROUP BY  CONCAT(SUBSTRING(A.hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2)) ORDER BY SUBSTRING(A.hkfq_time,7,4) DESC, SUBSTRING(A.hkfq_time,4,2) DESC");
//					}
//				}
//	       logger.info("getnewoldyqltjInteger sb  " + sb);
//	       return getJdbcTemplate().queryPage(sb.toString(), curPage, numPerPage);
//		}
//			
		 
	/**************************20190627 新老用户逾期率统计表************end**********************/
}
