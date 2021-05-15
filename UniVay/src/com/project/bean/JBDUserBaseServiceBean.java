package com.project.bean;



import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.thinkive.base.jdbc.DBPage;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.jdbc.session.Session;
import com.thinkive.base.service.BaseService;
import com.thinkive.base.util.StringHelper;



public class JBDUserBaseServiceBean extends BaseService {
	
    private static Logger logger = Logger.getLogger(JBDUserBaseServiceBean.class);
	
	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}
	
	/**
	 *  Max loan money
	 * @param userid
	 * @return
	 */
	public List<DataRow> getUserjkMoneyMax(String userid){
		 String sql = "SELECT sjsh_money,yuq_ts,sjds_money,lx FROM sd_new_jkyx WHERE sfyfk =1 AND sfyhw =1 AND userid="+userid+"  ORDER BY id ASC ";
		 
		 return getJdbcTemplate().query(sql);		
	  }
	/**
	 * Get interest  information
	 * @param userid
	 * @return
	 */
	public DataRow getUseryuqts(String userid) {
		String sql =" SELECT  SUM(a.yuq_ts) AS totalyuqts ,a.yuq_ts AS lastyuqts ,COUNT(*) AS totalcs ,a.id AS lastjkid FROM  ( SELECT * FROM sd_new_jkyx WHERE userid ="+ userid+" AND cl03_status =1 AND sfyhw =1 AND sfyfk =1 ORDER BY id  DESC ) a";
		
		return getJdbcTemplate().queryMap(sql);	
	}
	
	/**
	 * Successful borrowing
	 * @param userid
	 * @return
	 */
	public int getUserSuccessLoanTotal(String userid,String maxMoney){
	   	 StringBuffer sb = new StringBuffer();
	   	 sb.append("SELECT COUNT(*) as maxCount FROM sd_new_jkyx WHERE cl03_status =1 AND  sfyfk =1 AND sfyhw =1 AND userid= ");
	     sb.append(userid);
	     
	 	if(!maxMoney.equals("") && maxMoney!= null) {
			 sb.append(" AND sjsh_money = '"+maxMoney+"'");
		 }
		
	 return getJdbcTemplate().queryInt(sb.toString());		
   
	}
	
	/**
	 * 获取用户isgood 状态
	 */
	public int getuserGoodStatus(String userid) {
		String sql = "SELECT is_good FROM sd_user WHERE id ="+userid;
		 
		 return getJdbcTemplate().queryInt(sql);
	}
	
	/**
	 * 获取用户借款 状态
	 */
	public DataRow getUserjkStatus(String userid) {
		String sql = "SELECT * FROM sd_new_jkyx WHERE sfyfk=1 and sfyhw=0 and  userid ="+userid;
		 
		 return getJdbcTemplate().queryMap(sql);
	}
	
	/**
	 * 获取用户
	 */
	public String getusername(String userid) {
		String sql = "SELECT username FROM sd_user WHERE id ="+userid;
		 
		 return getJdbcTemplate().queryString(sql);
	}

}
