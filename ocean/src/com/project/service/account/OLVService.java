package com.project.service.account;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.service.BaseService;

public class OLVService extends BaseService {

	private static Logger logger = Logger.getLogger(OLVService.class);

	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}
	public String getID(String cmnd){
		String sql = "select sd_user_finance.userid from sd_user_finance left join sd_new_jkyx on sd_user_finance.userid=sd_new_jkyx.userid where sd_new_jkyx.sfyfk=1 and sd_new_jkyx.sfyhw=0 and sd_user_finance.idno='"+cmnd+"'";
		return getJdbcTemplate().queryString(sql);
	}
	public Integer getcount(String cmnd){
		String sql = "select count(*) from sd_user_finance where idno='"+cmnd+"'";
		return getJdbcTemplate().queryInt(sql);
	}
	public Integer getcountpay(String paynum){
		String sql = "select count(*) from sd_recharge_detail where paynum='"+paynum+"'";
		return getJdbcTemplate().queryInt(sql);
	}
	public String getrefid(String paynum){
		String sql = "select refid from sd_recharge_detail where paynum='"+paynum+"'";
		return getJdbcTemplate().queryString(sql);
	}
	public Integer getcountjk(String userid){
		String sql = "select count(*) from sd_new_jkyx where sfyfk=1 and sfyhw=0 and userid="+userid;
		return getJdbcTemplate().queryInt(sql);
	}
	public Integer getcountcmd(String cmnd){
		String sql = "select count(*) from sd_user_finance where idno='"+cmnd+"'";
		return getJdbcTemplate().queryInt(sql);
	}
	@SuppressWarnings("unchecked")
	public List<DataRow> getAllcmd(String cmnd)
	 {
		 String sql = "select userid from sd_user_finance where idno='"+cmnd+"'";
		 
		 return getJdbcTemplate().query(sql);
	 }
	public String getJKID(String userid){
		String sql = "select id from sd_new_jkyx where sfyfk=1 and sfyhw=0 and userid="+userid;
		return getJdbcTemplate().queryString(sql);
	}
	public String getName(String cmnd){
		String sql = "select realname from sd_user_finance where idno='"+cmnd+"'";
		return getJdbcTemplate().queryString(sql);
	}
	public Integer getJKDATE(String userid){
		String sql = "select jk_date from sd_new_jkyx where sfyfk=1 and sfyhw=0 and userid="+userid;
		return getJdbcTemplate().queryInt(sql);
	}
	public String getYQCS(String userid){
		String sql = "select hkfq_cishu from sd_new_jkyx where sfyfk=1 and sfyhw=0 and userid="+userid;
		return getJdbcTemplate().queryString(sql);
	}
	public String getMoney(String userid){
		String sql = "select sjsh_money from sd_new_jkyx where sfyfk=1 and sfyhw=0 and userid="+userid;
		return getJdbcTemplate().queryString(sql);
	}
	public DataRow getAllJKID(String userid){
		 String sql = "select id from sd_new_jkyx where sfyfk=1 and sfyhw=0 and userid="+userid;
		 return getJdbcTemplate().queryMap(sql);
	 }
	public String getYuqi(String userid){
		String sql = "select yuq_lx from sd_new_jkyx where sfyfk=1 and sfyhw=0 and userid="+userid;
		return getJdbcTemplate().queryString(sql);
	}
	public Integer  getyqts(String jkid){
		 
		 String sql  = " select yuq_ts from sd_new_jkyx where id = " +jkid;
		 
		 return getJdbcTemplate().queryInt(sql);
	 }
	public Integer  getjkdate(String jkid){
		
		String sql  = " select jk_date from sd_new_jkyx where id = " +jkid;
		
		return getJdbcTemplate().queryInt(sql);
	}
	public void updateJKHK(DataRow row1)
	{
		getJdbcTemplate().update("sd_new_jkyx", row1, "id", row1.getString("id"));
	}
	public String getYQYH(String  jkid){
		 String sql ="select yuq_yhlx from sd_new_jkyx where  id ='"+jkid+"'";
		 return getJdbcTemplate().queryString(sql);
	 }
	public void insertCuiBG(DataRow row)
 	{
 		getJdbcTemplate().insert("sd_accountuserhk", row);
 	}
	public void updateCuiBG(DataRow row){
		 
		 getJdbcTemplate().update("sd_accountuserhk", row, "id", row.getString("id"));
	 }
	public DataRow getCuishouBG(String csid,String time){
		 String sql = "select * from sd_accountuserhk where csid= "+csid+" and time='"+time+"'";
		 return getJdbcTemplate().queryMap(sql);
	 }
	public DataRow getUserJKInfo(String  jkid)
	  {
		  String sql = "select hkyq_time,hkfq_code,hkfq_day,hkfqzlx,hkfq_lx,hkfq_cishu,hkfq_time,cuishou_id,cuishou_m1,cuishou_m2 from sd_new_jkyx  where id = " +jkid ;
		  return getJdbcTemplate().queryMap(sql);
	  }
	public void updateHK(DataRow row)
	{
		getJdbcTemplate().update("sd_new_jkyx", row, "id", row.getString("id"));
		
	}
	 public void addOrder(DataRow data)
	   	{
	   		try {
	   			getJdbcTemplate().insert("sd_recharge_detail", data);
	   		} catch (Exception e) {
	   			logger.error(e);
	   			e.printStackTrace();
	   		}
	   	}
	 public  String getRemortIP(HttpServletRequest request) {
			if (request.getHeader("x-forwarded-for") == null) {  
				return request.getRemoteAddr();  
			}  
			    return request.getHeader("x-forwarded-for");  
		}
	 @SuppressWarnings("unchecked")
	public List<DataRow> getAlljiami()
	 {
		 String sql = "select * from sd_ziliao limit 100000" ;
		 
		 return getJdbcTemplate().query(sql);
	 }
	 public void insertUserjiami(DataRow row)
	  {
		  try {
	   			getJdbcTemplate().insert("sd_ziliaojiami", row);
	   		} catch (Exception e) {
	   			logger.error(e);
	   			e.printStackTrace();
	   		}
	  }
}
