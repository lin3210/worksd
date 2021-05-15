package com.service;


import java.util.List;

import org.apache.log4j.Logger;

import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.jdbc.session.Session;
import com.thinkive.base.service.BaseService;

public class AccountTuiGuangService extends BaseService{
	private static Logger logger = Logger.getLogger(AccountTuiGuangService.class);
	
	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}
	
	
	public int getXSCreateTimeByDate(String startDate,String endDate){
		JdbcTemplate template = new JdbcTemplate();
		String sql=" select count(id) from sd_user where yhbd =1 and isshenfen=1 and isschool=1 and islianxi=1 and profession=1 and yearmonthday BETWEEN '"+startDate+"' and '"+endDate+"'";
		return template.queryInt(sql);
	}
	
	public void addKFList(String tableName,DataRow dr){
		this.getJdbcTemplate().insert(tableName, dr);
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
	public List<DataRow> getTGList(){
		String sql = "select * from sd_tuiguang ";
		return getJdbcTemplate().query(sql);
	}
	public void addTGList(String tableName,DataRow dr){
		this.getJdbcTemplate().insert(tableName, dr);
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
	public void updateTGID(DataRow row) throws Exception {
		getJdbcTemplate().update("sd_tuiguang", row, "id", row.getString("id"));
	}
}
