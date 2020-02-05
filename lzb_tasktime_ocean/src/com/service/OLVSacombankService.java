package com.service;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.service.BaseService;

public class OLVSacombankService extends BaseService {

	private static Logger logger = Logger.getLogger(OLVSacombankService.class);

	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}
	public String getID(String cmnd){
		String sql = "select userid from sd_user_finance where idno='"+cmnd+"'";
		return getJdbcTemplate().queryString(sql);
	}
	public String getUserBankname(int userid){
		String sql = "select cardName from sd_bankcard where userid='"+userid+"'";
		return getJdbcTemplate().queryString(sql);
	}
	public String getUserBankcardname(int userid){
		String sql = "select NapasBankName from sd_bankcard where userid='"+userid+"'";
		return getJdbcTemplate().queryString(sql);
	}
	public int getUserBankcardcode(int userid){
		String sql = "select bankcardcode from sd_bankcard where userid='"+userid+"'";
		return getJdbcTemplate().queryInt(sql);
	}
	public String getUserBankNo(int userid){
		String sql = "select NapasBankNo from sd_bankcard where userid='"+userid+"'";
		return getJdbcTemplate().queryString(sql);
	}
	public String getUserBankcardNo(int userid){
		String sql = "select cardno from sd_bankcard where userid='"+userid+"'";
		return getJdbcTemplate().queryString(sql);
	}
	public String getUserName(int userid){
		String sql = "select realname from sd_user_finance where userid='"+userid+"'";
		return getJdbcTemplate().queryString(sql);
	}
	public String getUserSjdsmoney(int jkid){
		String sql = "select sjds_money from sd_new_jkyx where sfyfk=2 and id="+jkid;
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
	public Integer getcountjk(String userid){
		String sql = "select count(*) from sd_new_jkyx where sfyfk=1 and sfyhw=0 and userid="+userid;
		return getJdbcTemplate().queryInt(sql);
	}
	public String getJKID(String userid){
		String sql = "select id from sd_new_jkyx where sfyfk=1 and sfyhw=0 and userid="+userid;
		return getJdbcTemplate().queryString(sql);
	}
	public String getName(String cmnd){
		String sql = "select realname from sd_user_finance where idno='"+cmnd+"'";
		return getJdbcTemplate().queryString(sql);
	}
	public String getMoney(String userid){
		String sql = "select sjsh_money from sd_new_jkyx where sfyfk=1 and sfyhw=0 and userid="+userid;
		return getJdbcTemplate().queryString(sql);
	}
	public String getYuqi(String userid){
		String sql = "select yuq_lx from sd_new_jkyx where sfyfk=1 and sfyhw=0 and userid="+userid;
		return getJdbcTemplate().queryString(sql);
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
	 public Integer getJK(int  jkid)
		{
			String sql = "select jk_date from sd_new_jkyx where id= "+jkid ;

			return getJdbcTemplate().queryInt(sql);
		}
	  public DataRow getUserRecThreeInfoYN(String  userid)
			{
				String sql = "select sd_user.id as userid ,cardNo, mobilePhone ,username , cardUserName ,bankbs ,bankName ,remark,registration_id from sd_user  left join  sd_bankcard on sd_bankcard.userId =sd_user.id where userid='"+userid+"'";

				return getJdbcTemplate().queryMap(sql);
			}
	  public void insertUserMsg(DataRow row)
	 	{
	 		getJdbcTemplate().insert(" sd_msg", row);
	 	}
		
      public void updateUserJk(DataRow row) throws Exception {
			
			getJdbcTemplate().update("sd_new_jkyx", row, "id", row.getString("id"));
	 }
      public void updateUserFK(DataRow row) throws Exception {
			
			getJdbcTemplate().insert("sd_withdraw", row);
	 }
      public void updateUserFKSB(DataRow row) throws Exception {
    	  
    	  getJdbcTemplate().insert("sd_withdraw_error", row);
      }
      public Integer getUserID(String  userid)
    		{
    			String sql = "select id from sd_new_jkyx where userid= '" +userid+"'and sfyfk=2" ;

    			return getJdbcTemplate().queryInt(sql);
    		}
      public void updateUserBank(DataRow row) throws Exception {
    	  
    	  getJdbcTemplate().update("sd_bankcard", row, "id", row.getString("id"));
      }
      public String  getRealname(String userid){
    		 
    		 String sql = "select realname from sd_user_finance where  userid ="+userid ;
    		 return getJdbcTemplate().queryString(sql);	 
    	 }
}
