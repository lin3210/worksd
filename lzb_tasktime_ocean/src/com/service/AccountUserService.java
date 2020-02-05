package com.service;


import java.util.List;

import org.apache.log4j.Logger;

import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.jdbc.session.Session;
import com.thinkive.base.service.BaseService;

public class AccountUserService extends BaseService{
	private static Logger logger = Logger.getLogger(AccountUserService.class);
	
	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}
	
	public int getCreateTimeByDate(String startDate,String endDate){
		JdbcTemplate template = new JdbcTemplate();
		String sql=" select count(id) from sd_user where yearmonthday BETWEEN '"+startDate+"' and '"+endDate+"'";
		return template.queryInt(sql);
	}
	
	/**
	 * ��ѯÿ����֤������
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public int getGZCreateTimeByDate(String startDate,String endDate){
		JdbcTemplate template = new JdbcTemplate();
		String sql=" select count(id) from sd_user where vipStatus=1 and yearmonthday BETWEEN '"+startDate+"' and '"+endDate+"'";
		return template.queryInt(sql);
	}
	public int getXSCreateTimeByDate(String startDate,String endDate){
		JdbcTemplate template = new JdbcTemplate();
		String sql=" select count(id) from sd_user where yhbd =1 and isshenfen=1 and isschool=1 and islianxi=1 and profession=1 and yearmonthday BETWEEN '"+startDate+"' and '"+endDate+"'";
		return template.queryInt(sql);
	}
	/**
	 * 查询每天借款的人数
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public int getShenqingJK(String startDate,String endDate){
		String sql=" select count(sd_new_jkyx.id) from sd_new_jkyx left join sd_user on sd_new_jkyx.userid=sd_user.id where yearmonthday BETWEEN ? and ?  and daytime BETWEEN ? and ?";
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
	 * ����һ��ͳ�Ƽ�¼
	 * @param tableName
	 * @param dr
	 */
	public void addAcountUser(String tableName,DataRow dr){
		JdbcTemplate template = new JdbcTemplate();
		template.insert(tableName, dr);
	}
	public List<DataRow> getYuqList(String UserID){
		String sql =" select j.userid, j.id as jkid2 ,w.name as csname ,j.zxcsbz as msg from  (select id ,userid ,cl03_ren,cuishou_id,zxcsbz  "
				+ "  from  sd_new_jkyx where sfyfk <>0 and yuq_ts > 0 and sfyhw= 0 ) j  left join sd_user u on "
				+ "  j.userid = u.id left join  sdcms_user w on w.USER_ID = j.cuishou_id where 1 =1    ";
		return this.getJdbcTemplate().query(sql);
	}
	public void addKFList(String tableName,DataRow dr){
		this.getJdbcTemplate().insert(tableName, dr);
	}
	public void addCSList(String tableName,DataRow dr){
		this.getJdbcTemplate().insert(tableName, dr);
	}
	public void updateJishu(DataRow row){
		this.getJdbcTemplate().update("sd_jishu",row,"id", row.getString("id"));
	}
	/**
	 * ɾ����ļ�¼
	 * @param time
	 * @return
	 */
	public Boolean deleteAcountByTime(String time){
		JdbcTemplate template = new JdbcTemplate();
		Session session = getSession("web");
		session.beginTrans();
		try {
			String sql="delete from sd_accountuser where time='"+time+"'";
			template.update(sql);
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
	public List<DataRow> getShenqingJKLYH(String startDate,String endDate1,String endDate){
		String sql=" select sd_new_jkyx.userid from sd_new_jkyx left join sd_user on sd_new_jkyx.userid=sd_user.id where yearmonthday<?  and daytime BETWEEN ? and ? group by sd_new_jkyx.userid";
		Object[] obj=new Object[]{endDate1,startDate,endDate};
		return getJdbcTemplate().query(sql,obj);
	}
	public DataRow  getLYHJK(int userid){
		String sql=" select userid from sd_new_jkyx where sfyfk=1 and sfyhw=1 and userid="+userid;
		return getJdbcTemplate().queryMap(sql);
	}
	public int getZShenqingJK(String startDate,String endDate){
		String sql=" select count(id) from sd_new_jkyx where daytime BETWEEN ? and ?";
		Object[] obj=new Object[]{startDate,endDate};
		return getJdbcTemplate().queryInt(sql,obj);
	}
	public int getSmsCode(){
		String sql = "select smscode from sd_jishu where id=1";
		return getJdbcTemplate().queryInt(sql);
	}
	
	public int getOLDCreateTimeByDate(String startDate,String endDate){
		String sql=" select count(id) from sd_new_jkyx where is_old_user=1 and daytime BETWEEN ? and ?";
		Object[] obj=new Object[]{startDate,endDate};
		return getJdbcTemplate().queryInt(sql,obj);
	}
}
