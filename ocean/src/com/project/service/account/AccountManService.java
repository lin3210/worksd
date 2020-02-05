package com.project.service.account;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.jdbc.session.Session;
import com.thinkive.base.service.BaseService;

/**
 * 统计平台用户
 * @author Administrator
 *
 */
public class AccountManService extends BaseService{
	
private static Logger logger = Logger.getLogger(AccountManService.class);
	
	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}
	
	/**
	 * 查询每天注册的人数
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public int getCreateTimeByDate(String startDate,String endDate){
		String sql=" select count(id) from sd_user where yearmonthday BETWEEN ? and ?";
		Object[] obj=new Object[]{startDate,endDate};
		return getJdbcTemplate().queryInt(sql,obj);
	}
	
	/**
	 * 查询每天注册的人数
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public int getVipCreateTimeByDate(String startDate,String endDate){
		String sql=" select count(id) from sd_user where yhbd=1 and createtime BETWEEN ? and ?";
		Object[] obj=new Object[]{startDate,endDate};
		return getJdbcTemplate().queryInt(sql,obj);
	}
	
	/**
	 * 查询每天认证的人数工作
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public int getVipCreateTimeByDateGZ(String startDate,String endDate){
		String sql=" select count(id) from sd_user where vipStatus=1  and yearmonthday BETWEEN ? and ?";
		Object[] obj=new Object[]{startDate,endDate};
		return getJdbcTemplate().queryInt(sql,obj);
	}
	public int getOLDCreateTimeByDate(String startDate,String endDate){
		String sql=" select count(id) from sd_new_jkyx where is_old_user=1 and daytime BETWEEN ? and ?";
		Object[] obj=new Object[]{startDate,endDate};
		return getJdbcTemplate().queryInt(sql,obj);
	}
	/**
	 * 查询每天认证的人数学生
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public int getVipCreateTimeByDateXS(String startDate,String endDate){
		String sql=" select count(id) from sd_user where yhbd =1 and isschool and islianxi and isshenfen and profession = 1  and yearmonthday BETWEEN ? and ?";
		Object[] obj=new Object[]{startDate,endDate};
		return getJdbcTemplate().queryInt(sql,obj);
	}
/**
 * 查询每天借款的老用户人数
 * @param startDate
 * @param endDate
 * @return
 */
public List<DataRow> getShenqingJKLYH(String startDate,String endDate1,String endDate){
	String sql=" select sd_new_jkyx.userid from sd_new_jkyx left join sd_user on sd_new_jkyx.userid=sd_user.id where yearmonthday<?  and fkdz_time_day BETWEEN ? and ? group by sd_new_jkyx.userid";
	Object[] obj=new Object[]{endDate1,startDate,endDate};
	return getJdbcTemplate().query(sql,obj);
}
public DataRow  getLYHJK(int userid){
	String sql=" select userid from sd_new_jkyx where sfyfk=1 and sfyhw=1 and userid="+userid;
	return getJdbcTemplate().queryMap(sql);
}
/**
 * 查询每天借款的人数
 * @param startDate
 * @param endDate
 * @return
 */
public int getShenqingJK(String startDate,String endDate){
	String sql=" select count(DISTINCT sd_new_jkyx.userid) from sd_new_jkyx left join sd_user on sd_new_jkyx.userid=sd_user.id where yearmonthday BETWEEN ? and ?  and daytime BETWEEN ? and ?";
	Object[] obj=new Object[]{startDate,endDate,startDate,endDate};
	return getJdbcTemplate().queryInt(sql,obj);
}
/**
 * 查询每天借款的人数
 * @param startDate
 * @param endDate
 * @return
 */
public int getShenqingJKPF3(String startDate,String endDate){
	String sql=" select count(DISTINCT sd_new_jkyx.userid) from sd_new_jkyx left join sd_user on sd_new_jkyx.userid=sd_user.id where daytime BETWEEN ? and ? and sd_new_jkyx.pipei=3 ";
	Object[] obj=new Object[]{startDate,endDate};
	return getJdbcTemplate().queryInt(sql,obj);
}
/**
 * 查询每天借款的人数
 * @param startDate
 * @param endDate
 * @return
 */
public int getShenqingJKPF2(String startDate,String endDate){
	String sql=" select count(DISTINCT sd_new_jkyx.userid) from sd_new_jkyx left join sd_user on sd_new_jkyx.userid=sd_user.id where daytime BETWEEN ? and ? and sd_new_jkyx.pipei=2 ";
	Object[] obj=new Object[]{startDate,endDate};
	return getJdbcTemplate().queryInt(sql,obj);
}
/**
 * 查询每天借款的人数
 * @param startDate
 * @param endDate
 * @return
 */
public int getShenqingJKPF1(String startDate,String endDate){
	String sql=" select count(DISTINCT sd_new_jkyx.userid) from sd_new_jkyx left join sd_user on sd_new_jkyx.userid=sd_user.id where daytime BETWEEN ? and ? and sd_new_jkyx.pipei=1 ";
	Object[] obj=new Object[]{startDate,endDate};
	return getJdbcTemplate().queryInt(sql,obj);
}
/**
 * 查询每天借款的人数
 * @param startDate
 * @param endDate
 * @return
 */
public int getShenqingJKPFf1(String startDate,String endDate){
	String sql=" select count(DISTINCT sd_new_jkyx.userid) from sd_new_jkyx left join sd_user on sd_new_jkyx.userid=sd_user.id where daytime BETWEEN ? and ? and sd_new_jkyx.pipei=-1 ";
	Object[] obj=new Object[]{startDate,endDate};
	return getJdbcTemplate().queryInt(sql,obj);
}
/**
 * 查询每天借款的人数
 * @param startDate
 * @param endDate
 * @return
 */
public int getShenqingJKPFf2(String startDate,String endDate){
	String sql=" select count(DISTINCT sd_new_jkyx.userid) from sd_new_jkyx left join sd_user on sd_new_jkyx.userid=sd_user.id where daytime BETWEEN ? and ? and sd_new_jkyx.pipei=-2 ";
	Object[] obj=new Object[]{startDate,endDate};
	return getJdbcTemplate().queryInt(sql,obj);
}
/**
 * 查询每天借款的人数
 * @param startDate
 * @param endDate
 * @return
 */
public int getZShenqingJK(String startDate,String endDate){
	String sql=" select count(id) from sd_new_jkyx where daytime BETWEEN ? and ?";
	Object[] obj=new Object[]{startDate,endDate};
	return getJdbcTemplate().queryInt(sql,obj);
}
/**
 * 查询每天借款成功的人数
 * @param startDate
 * @param endDate
 * @return
 */
public int getShenqingJKCG(String startDate,String endDate){
	String sql=" select count(id) from sd_new_jkyx where sfyfk=1 and fkdz_time_day BETWEEN ? and ?";
	Object[] obj=new Object[]{startDate,endDate};
	return getJdbcTemplate().queryInt(sql,obj);
}
	/**
	 * 查询每天还款的人数
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public int getCreateTimeByDateHK(String startDate,String endDate){
		String sql=" select count(id) from sd_new_jkyx where sfyhw = 1 and hk_time BETWEEN ? and ?";
		Object[] obj=new Object[]{startDate,endDate};
		return getJdbcTemplate().queryInt(sql,obj);
	}
	
	/**
	 * 查询每天逾期的人数
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public int getVipCreateTimeByDateYQ(String startDate,String endDate){
		String sql=" select count(id) from sd_new_jkyx where yuq_ts >0 and hkyq_time BETWEEN ? and ?";
		Object[] obj=new Object[]{startDate,endDate};
		return getJdbcTemplate().queryInt(sql,obj);
	}
	
	/**
	 * 新增一条统计记录
	 * @param tableName
	 * @param dr
	 */
	public void addAcountUser(String tableName,DataRow dr){
		this.getJdbcTemplate().insert(tableName, dr);
	}
	
	/**
	 * 删除当天的记录
	 * @param time
	 * @return
	 */
	public Boolean deleteAcountByTime(String time){
		Session session = getSession("web");
		session.beginTrans();
		try {
			String sql="delete from sd_accountuser where time='"+time+"'";
			getJdbcTemplate().update(sql);
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
	
	/**
	 * 删除当天的记录
	 * @param time
	 * @return
	 */
	public Boolean deleteAcountByTimeHK(String time){
		Session session = getSession("web");
		session.beginTrans();
		try {
			String sql="delete from sd_accountuserhk where time='"+time+"'";
			getJdbcTemplate().update(sql);
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
	/**
	 * 查询一年的平台用户统计
	 * @return
	 */
	public List<DataRow>  getAllAcountUser(){
		String sql="select * from sd_accountuser order by id desc limit 365";
		return this.getJdbcTemplate().query(sql);
	}
	/**
	 * 查询一年的平台还款用户统计
	 * @return
	 */
	public List<DataRow>  getAllAcountUserHK(){
		String sql="select * from sd_accountuserhk order by time desc limit 365";
		return this.getJdbcTemplate().query(sql);
	}
	/**
	 * 查询所有男性别的用户
	 * @return
	 */
	public int getAllNanUser(){//得到所有男性别的用户
		String sql="select count(id) from sd_user_finance where sex=1 ";
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 查询所有女性别的用户
	 * @return
	 */
	public int getAllNvUser(){//得到所有女性别的用户
		String sql="select count(id) from sd_user_finance where sex=2 ";
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 查询所有小于18年龄的用户
	 * @return
	 */
	public int getAllZeroUser(int year){//得到所有小于18年龄的用户
		String sql = "select count(id) from sd_user_finance where substring(age,7,4)>"+(year-18);
		
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 查询所有18-23年龄的用户
	 * @return
	 */
	public int getAllOneUser(int year){//得到所有18-23年龄的用户
		String sql = "select count(id) from sd_user_finance where substring(age,7,4)<="+(year-18)+" and substring(age,7,4)>"+(year-23);
		
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 查询所有24-29年龄的用户
	 * @return
	 */
	public int getAllTwoUser(int year){//得到所有24-29年龄的用户
		String sql = "select count(id) from sd_user_finance where substring(age,7,4)<="+(year-23)+" and substring(age,7,4)>"+(year-29);
		
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 查询所有30-35年龄的用户
	 * @return
	 */
	public int getAllThreeUser(int year){//得到所有30-35年龄的用户
		String sql = "select count(id) from sd_user_finance where substring(age,7,4)<="+(year-29)+" and substring(age,7,4)>"+(year-35);
		
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 查询所有36-41年龄的用户
	 * @return
	 */
	public int getAllFourUser(int year){//得到所有36-41年龄的用户
		String sql = "select count(id) from sd_user_finance where substring(age,7,4)<="+(year-35)+" and substring(age,7,4)>"+(year-41);
		
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 查询所有42-47年龄的用户
	 * @return
	 */
	public int getAllFiveUser(int year){//得到所有42-47年龄的用户
		String sql = "select count(id) from sd_user_finance where substring(age,7,4)<="+(year-41)+" and substring(age,7,4)>"+(year-47);
		
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 查询所有48-53年龄的用户
	 * @return
	 */
	public int getAllSixUser(int year){//得到所有48-53年龄的用户
		String sql = "select count(id) from sd_user_finance where substring(age,7,4)<="+(year-47)+" and substring(age,7,4)>"+(year-53);
		
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 查询所有53以上年龄的用户
	 * @return
	 */
	public int getAllSevenUser(int year){//得到所有53以上年龄的用户
		String sql = "select count(id) from sd_user_finance where substring(age,7,4)<="+(year-53);
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 查询所有学生的用户
	 * @return
	 */
	public int getAllStudentUser(int year){//得到所有53以上年龄的用户
		String sql = "select count(id) from sd_user where profession=1";
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 查询所有工作的用户
	 * @return
	 */
	public int getAllWorkUser(int year){//得到所有53以上年龄的用户
		String sql = "select count(id) from sd_user where profession=2";
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 查询所有工作的用户
	 * @return
	 */
	public int getAllXinzioneUser(){//得到所有薪资小于500万的用户
		String sql = "select count(id) from sd_work where replace(pay,' ','') like '%Dưới5,000,000%'";
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 查询所有工作的用户
	 * @return
	 */
	public int getAllXinzitwoUser(){//得到所有500-1000的用户
		String sql = "select count(id) from sd_work where replace(pay,' ','') like '%5,000,000-10,000,000%'";
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 查询所有工作的用户
	 * @return
	 */
	public int getAllXinzithreeUser(){//得到所有1000-1500的用户
		String sql = "select count(id) from sd_work where replace(pay,' ','') like '%10,000,000-15,000,000%'";
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 查询所有工作的用户
	 * @return
	 */
	public int getAllXinzifourUser(){//得到所有1500-2000的用户
		String sql = "select count(id) from sd_work where replace(pay,' ','') like '%15,000,000-20,000,000%'";
		return this.getJdbcTemplate().queryInt(sql);
	}
	/**
	 * 查询所有工作的用户
	 * @return
	 */
	public int getAllXinzifiveUser(){//得到所有2000-2500的用户
		String sql = "select count(id) from sd_work where replace(pay,' ','') like '%Trên 20,000,000%'";
		return this.getJdbcTemplate().queryInt(sql);
	}
	public List<DataRow> getAllCompany(){
		String sql = "select company from sd_work";
		return this.getJdbcTemplate().query(sql);
	}
	public List<DataRow> getAllTime(){
		String sql = "select time from sd_work";
		return this.getJdbcTemplate().query(sql);
	}
	public List<DataRow> getAllPhone(){
		String sql = "select phonetype from sd_user_finance";
		return this.getJdbcTemplate().query(sql);
	}
	//得到审核部门的userid
	public List<DataRow> getSHuserid(){
		String sql = "select user_id from sdcms_user where (roleid=2 or roleid=16) and user_id != 111 and state=1 ";
		return this.getJdbcTemplate().query(sql);
	}
	//得到催收部门的userid
	public List<DataRow> getCSuserid(){
		String sql = "select user_id from sdcms_user where (roleid=19 or roleid=20  or roleid=21 or roleid=23 or roleid=24 or roleid=25 or roleid=26) and state=1";
		return this.getJdbcTemplate().query(sql);
	}
	//得到放款部门的userid
	public List<DataRow> getFKuserid(){
		String sql = "select user_id from sdcms_user where roleid=4 and state=1 ";
		return this.getJdbcTemplate().query(sql);
	}
	//一审总笔数
	public List<DataRow> getZBS(int userid,String startDate, String endDate){
		String sql = "select count(id) from sd_new_jkyx where cl_ren='"+userid+"'";
		sql += " and cl_time>'"+startDate+"'";
		sql += " and substring(cl_time,4,2)='"+startDate.substring(3, 5)+"'";
		sql += " and cl_time<'"+endDate+"'";
		return this.getJdbcTemplate().query(sql);
	}
	//一审通过笔数
	public List<DataRow> getZBSTG(int userid,String startDate, String endDate){
		String sql = "select count(id) from sd_new_jkyx where cl_status=1 and cl_ren='"+userid+"'";
		sql += " and cl_time>'"+startDate+"'";
		sql += " and substring(cl_time,4,2)='"+startDate.substring(3, 5)+"'";
		sql += " and cl_time<'"+endDate+"'";
		return this.getJdbcTemplate().query(sql);
	}
	//三审通过笔数
		public List<DataRow> getSSTG(int userid,String startDate, String endDate){
			String sql = "select count(id) from sd_new_jkyx where cl03_status=1 and cl03_ren='"+userid+"'";
			sql += " and cl03_time>'"+startDate+"'";
			sql += " and substring(cl03_time,4,2)='"+startDate.substring(3, 5)+"'";
			sql += " and cl03_time<'"+endDate+"'";
			return this.getJdbcTemplate().query(sql);
		}
		//三审逾期笔数
		public List<DataRow> getSSYQ(int userid,String startDate, String endDate){
			String sql = "select count(id) from sd_new_jkyx where cl03_status=1 and yuq_ts>0 and cl03_ren='"+userid+"'";
			sql += " and cl03_time>'"+startDate+"'";
			sql += " and substring(cl03_time,4,2)='"+startDate.substring(3, 5)+"'";
			sql += " and cl03_time<'"+endDate+"'";
			return this.getJdbcTemplate().query(sql);
		}
		//三审逾期未还笔数
		public List<DataRow> getSSYQWH(int userid,String startDate, String endDate){
			String sql = "select count(id) from sd_new_jkyx where cl03_status=1 and yuq_ts>0 and sfyhw=0 and cl03_ren='"+userid+"'";
			sql += " and cl03_time>'"+startDate+"'";
			sql += " and substring(cl03_time,4,2)='"+startDate.substring(3, 5)+"'";
			sql += " and cl03_time<'"+endDate+"'";
			return this.getJdbcTemplate().query(sql);
		}
		//催收总笔数
		public List<DataRow> getCSZBS(int userid,String startDate, String endDate){
			String sql = "select count(id) from sd_new_jkyx where sfyhw=1 and cuishou_id='"+userid+"'";
			sql += " and hk_time>'"+startDate+"'";
			sql += " and substring(hk_time,4,2)='"+startDate.substring(3, 5)+"'";
			sql += " and hk_time<'"+endDate+"'";
			return this.getJdbcTemplate().query(sql);
		}
		//催收总金额
		public List<DataRow> getCSZJE(int userid,String startDate, String endDate){
			String sql = "select SUM(replace(sjsh_money, ',' ,'')+replace(yuq_lx, ',' ,'')) as zje from sd_new_jkyx where cuishou_id='"+userid+"'";
			sql += " and hk_time>'"+startDate+"'";
			sql += " and substring(hk_time,4,2)='"+startDate.substring(3, 5)+"'";
			sql += " and hk_time<'"+endDate+"'";
			return this.getJdbcTemplate().query(sql);
		}
		//催收已经还款总金额
		public List<DataRow> getCSZJEYH(int userid,String startDate, String endDate){
			String sql = "select SUM(replace(sjsh_money, ',' ,'')+replace(yuq_lx, ',' ,'')+replace(hkfqzlx, ',' ,'')+replace(yuq_yhlx, ',' ,'')) as yhje from sd_new_jkyx where sfyhw=1 and cuishou_id='"+userid+"'";
			sql += " and hk_time>'"+startDate+"'";
			sql += " and substring(hk_time,4,2)='"+startDate.substring(3, 5)+"'";
			sql += " and hk_time<'"+endDate+"'";
			return this.getJdbcTemplate().query(sql);
		}
		//催收已经还款总金额延期和部分的
		public List<DataRow> getCSZJEYH1(int userid,String startDate, String endDate){
			String sql = "select SUM(replace(hkfqzlx, ',' ,'')+replace(yuq_yhlx, ',' ,'')) as yqje from sd_new_jkyx where sfyhw=0 and cuishou_id='"+userid+"'";
			sql += " and hk_time>'"+startDate+"'";
			sql += " and substring(hk_time,4,2)='"+startDate.substring(3, 5)+"'";
			sql += " and hk_time<'"+endDate+"'";
			return this.getJdbcTemplate().query(sql);
		}
		//提醒还款总金额
		public List<DataRow> getTXZJE(int userid,String startDate, String endDate){
			String sql = "select SUM(replace(sjsh_money, ',' ,'')+replace(yuq_lx, ',' ,'')) as txzje from sd_new_jkyx where yuq_ts=0 and cuishou_id='"+userid+"'";
			sql += " and hk_time>'"+startDate+"'";
			sql += " and substring(hk_time,4,2)='"+startDate.substring(3, 5)+"'";
			sql += " and hk_time<'"+endDate+"'";
			return this.getJdbcTemplate().query(sql);
		}
		//提醒已经还款总金额
		public List<DataRow> getTXYHZJE(int userid,String startDate, String endDate){
			String sql = "select SUM(replace(sjsh_money, ',' ,'')+replace(yuq_lx, ',' ,'')+replace(hkfqzlx, ',' ,'')+replace(yuq_yhlx, ',' ,'')) as txyhzje from sd_new_jkyx where yuq_ts=0 and sfyhw=1 and cuishou_id='"+userid+"'";
			sql += " and hk_time>'"+startDate+"'";
			sql += " and substring(hk_time,4,2)='"+startDate.substring(3, 5)+"'";
			sql += " and hk_time<'"+endDate+"'";
			return this.getJdbcTemplate().query(sql);
		}
		//催收已经还款总金额延期和部分的
		public List<DataRow> getTXYHZJE1(int userid,String startDate, String endDate){
			String sql = "select SUM(replace(hkfqzlx, ',' ,'')+replace(yuq_yhlx, ',' ,'')) as txyhyqje from sd_new_jkyx where yuq_ts=0 and sfyhw=0 and cuishou_id='"+userid+"'";
			sql += " and hk_time>'"+startDate+"'";
			sql += " and substring(hk_time,4,2)='"+startDate.substring(3, 5)+"'";
			sql += " and hk_time<'"+endDate+"'";
			return this.getJdbcTemplate().query(sql);
		}
		//总笔数
		public List<DataRow> getFKZBS(int userid,String startDate, String endDate){
			String sql = "select count(id) from sd_new_jkyx where (sfyfk=1 or sfyfk=3 or cl03_yj='Bankcard Error') and fkr='"+userid+"'";
			sql += " and fkr_time>'"+startDate+"'";
			sql += " and substring(fkr_time,4,2)='"+startDate.substring(3, 5)+"'";
			sql += " and fkr_time<'"+endDate+"'";
			return this.getJdbcTemplate().query(sql);
		}
		//放款成功总笔数
		public List<DataRow> getFKCG(int userid,String startDate, String endDate){
			String sql = "select count(id) from sd_new_jkyx where sfyfk=1 and fkr='"+userid+"'";
			sql += " and fkr_time>'"+startDate+"'";
			sql += " and substring(fkr_time,4,2)='"+startDate.substring(3, 5)+"'";
			sql += " and fkr_time<'"+endDate+"'";
			return this.getJdbcTemplate().query(sql);
		}
		//放款成功总金额
		public List<DataRow> getFKCGJE(int userid,String startDate, String endDate){
			String sql = "select SUM(replace(sjds_money, ',' ,'')) as fkcgje from sd_new_jkyx where sfyfk=1 and fkr='"+userid+"'";
			sql += " and fkr_time>'"+startDate+"'";
			sql += " and substring(fkr_time,4,2)='"+startDate.substring(3, 5)+"'";
			sql += " and fkr_time<'"+endDate+"'";
			return this.getJdbcTemplate().query(sql);
		}
		//放款失败总笔数
		public List<DataRow> getFKSB(int userid,String startDate, String endDate){
			String sql = "select count(id) from sd_new_jkyx where (sfyfk=3 or cl03_yj='Bankcard Error') and fkr='"+userid+"'";
			sql += " and fkr_time>'"+startDate+"'";
			sql += " and substring(fkr_time,4,2)='"+startDate.substring(3, 5)+"'";
			sql += " and fkr_time<'"+endDate+"'";
			return this.getJdbcTemplate().query(sql);
		}
		//放款失败总金额
		public List<DataRow> getFKSBJE(int userid,String startDate, String endDate){
			String sql = "select SUM(replace(sjds_money, ',' ,'')) as fksbje from sd_new_jkyx where (sfyfk=3 or cl03_yj='Bankcard Error') and fkr='"+userid+"'";
			sql += " and fkr_time>'"+startDate+"'";
			sql += " and substring(fkr_time,4,2)='"+startDate.substring(3, 5)+"'";
			sql += " and fkr_time<'"+endDate+"'";
			return this.getJdbcTemplate().query(sql);
		}
		/**
		 * 查询每天还款的人数
		 * @param startDate
		 * @param endDate
		 * @return
		 */
		public int getCreateTimeByDateHKRS(String startDate,String endDate){
			String sql=" select count(id) from sd_new_jkyx where sfyhw = 0 and sfyfk=1 ";
			sql += " and (CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)) ='" + startDate + "'";
			
			sql += " or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))='" + startDate + "') ";
			return getJdbcTemplate().queryInt(sql);
		}
}
