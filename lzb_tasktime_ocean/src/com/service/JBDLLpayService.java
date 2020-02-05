package com.service;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.thinkive.base.jdbc.DBPage;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.jdbc.session.Session;
import com.thinkive.base.service.BaseService;
import com.thinkive.base.util.StringHelper;

public class JBDLLpayService  extends BaseService{

	private static Logger logger = Logger.getLogger(JBDLLpayService.class);
	
	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}
   
	public Integer bankNumCount(String bankCard){
		
        StringBuffer sb = new StringBuffer();
		sb.append("select count(1) from sd_bankcard where cardStatus=1 and cardNo = '");
		sb.append(bankCard);
		sb.append("'");
		return getJdbcTemplate().queryInt(sb.toString());
	}
	
	public Integer bankCount(String userId){
		StringBuffer sb = new StringBuffer();
		sb.append("select count(1) from sd_bankcard where  userId = ");
		sb.append(userId);
		return getJdbcTemplate().queryInt(sb.toString());
	}
	
	public void insertBankcard(DataRow data)
	{
	 		getJdbcTemplate().insert(" sd_bankcard", data);
	}
	
    public void updateBankInfo(DataRow row1){
		
		getJdbcTemplate().update("sd_bankcard ", row1, "userId", row1.getString("userid"));
	}
	
    public Integer userCount(String userId){
		StringBuffer sb = new StringBuffer();
		sb.append("select count(1) from sd_user_finance where userId = ");
		sb.append(userId);
		return getJdbcTemplate().queryInt(sb.toString());
	}
    
    public void insertUsercard(DataRow data)
	{
	   getJdbcTemplate().insert(" sd_user_finance", data);
	}
    
    public void updateUserFInfo(DataRow row1){
		
  		getJdbcTemplate().update("sd_user_finance ", row1, "userId", row1.getString("userid"));
  	}
    public void updatePerson(DataRow row1)
	{
		getJdbcTemplate().update("sd_personjk", row1, "userId", row1.getString("userid"));
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
    
	public String getUserId(String bankcard){
		StringBuffer sb = new StringBuffer();
		sb.append(" select userId from sd_bankcard where cardNo= '") ;
		sb.append(bankcard);
		sb.append("'");
		return getJdbcTemplate().queryString(sb.toString());
	}
	
	public void updateUserInfo(DataRow row3)
	{
		
		//getJdbcTemplate().update("sd_user", row3, "id", row3.getString("id"));
		getJdbcTemplate().update(" sd_user",row3,"id",row3.getString("id"));
	}
	public void updateOrder(DataRow data)
	{
		try {
			getJdbcTemplate().update("sd_recharge_detail", data, "paynumber", data.getString("paynumber"));
		} catch (Exception e) {
			
			e.printStackTrace();
			logger.error(e);
		}
	}
	
	public boolean updateUserRz(String  userid,String  money,String orderid ,DataRow row1 ,DataRow row2)
	{
	
		Session session = getSession("web");
		session.beginTrans();
		try {
			DataRow fundrecord = new DataRow();//添加资金流向
        	fundrecord.set("userid", userid);
        	fundrecord.set("fundmode", "实名认证金额");
        	fundrecord.set("handlesum", money);       
        	fundrecord.set("recordtime", new Date());
        	fundrecord.set("operatetype", 110);
        	fundrecord.set("spending", Double.parseDouble(money)/100);
        	fundrecord.set("borrow_id", 0);
        	fundrecord.set("paynumber", orderid);
        	session.update("sd_bankcard ", row1, "userId", row1.getString("userid"));
        	session.update("sd_user_finance ", row2, "userId", row2.getString("userid"));
        	session.insert("sd_fundrecord", fundrecord);   
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
	
	  public String findReffer(String userId){
			StringBuffer sb = new StringBuffer();
			sb.append("select refferee from sd_user where  id = ");
			sb.append(userId);
			
			return getJdbcTemplate().queryString(sb.toString());
		}
	  
	  public boolean updateUserJLMoney(DataRow user,double money,String orderid,String userId)
		{
		
			Session session = getSession("web");
			session.beginTrans();
			try {
				DataRow fundrecord = new DataRow();//添加资金流向
	        	fundrecord.set("userid", user.getInt("id"));
	        	fundrecord.set("fundmode", "推荐人认证奖励");
	        	fundrecord.set("handlesum", money);
	        	fundrecord.set("usablesum", user.getDouble("usablesum"));
	        	fundrecord.set("freezesum", 0.00);//当前冻结金额
	        	fundrecord.set("dueinsum", 0);
	        	fundrecord.set("recordtime", new Date());
	        	fundrecord.set("operatetype", 66);
	        	fundrecord.set("spending", money);
	        	fundrecord.set("borrow_id", 0);
	        	fundrecord.set("trader", userId);
	        	fundrecord.set("paynumber", orderid);           
	        	session.insert("sd_fundrecord", fundrecord);
	        	session.update("sd_user", user, "id", user.getInt("id"));
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
	  public DataRow getRefferUser(String refferCount)
		{
			String sql = "select * from sd_user where id = "+refferCount +" or mobilePhone =" +refferCount;
			return getJdbcTemplate().queryMap(sql);
		}
	  
	  public void updateJKHK(DataRow row1)
		{
			getJdbcTemplate().update("sd_new_jkyx", row1, "id", row1.getString("id"));
		}
	  
	  public void insertUserMsg(DataRow row)
	 	{
	 		getJdbcTemplate().insert(" sd_msg", row);
	 	}
	  public DataRow getUserInfo(int userid)
		{
			String sql = "select u.id ,b.cardUserName ,u.mobilePhone ,u.username from sd_user u left join sd_bankcard b on b.userid = u.id  where u.id = "+userid ;
			return getJdbcTemplate().queryMap(sql);
	}
	  public void updateZDHK(DataRow row1)
		{
			getJdbcTemplate().update("sd_new_jkyx", row1, "id", row1.getString("id"));
		}
	  public Integer  getDSBResult(String rec_id){
			StringBuffer sb = new StringBuffer();
			sb.append("select cl03_status  from sd_new_jkyx where  id= "+rec_id );
			
			return getJdbcTemplate().queryInt(sb.toString());
		}
	  public Integer  getHangCount(int  userid){
			StringBuffer sb = new StringBuffer();
			sb.append("select count(*) from sd_new_jkyx  where  jksfwc=0 and cl_status <>3 and cl02_status <>3 and cl03_status <>3  and  userid = "+userid );
			
			return getJdbcTemplate().queryInt(sb.toString());
		}
	  public DataRow getUserRecThreeInfo(int  userid)
		{
			String sql = "select sd_user.id as userid ,cardNo, mobilePhone ,username , cardUserName ,bankbs ,bankName ,remark from sd_user  left join  sd_bankcard on sd_bankcard.userId =sd_user.id where userId = " +userid;

			return getJdbcTemplate().queryMap(sql);
		}
	  public DataRow getUserRecThreeInfo2(String  userid)
			{
				String sql = "select sd_user.id as userid ,cardNo,createtime, mobilePhone ,username , cardUserName ,bankbs ,bankName ,remark from sd_user  left join  sd_bankcard on sd_bankcard.userId =sd_user.id where userId = '" +userid+"' " ;

				return getJdbcTemplate().queryMap(sql);
			}
	  public String getSjdz(String jkid){
			StringBuffer sb = new StringBuffer();
			sb.append("select sjds_money from sd_new_jkyx  where id="+jkid );
			return getJdbcTemplate().queryString(sb.toString());
		}
	  public String getSjsh(String jkid){
			StringBuffer sb = new StringBuffer();
			sb.append("select sjsh_money+yuq_lx from sd_new_jkyx  where id="+jkid );
			return getJdbcTemplate().queryString(sb.toString());
		}
	  public DataRow getUser(int  userid)
		{
			String sql = "select * from sd_user where id= " +userid ;

			return getJdbcTemplate().queryMap(sql);
		}
	  
	  public DataRow getUser(String   userid)
		{
			String sql = "select * from sd_user where id= " +userid ;

			return getJdbcTemplate().queryMap(sql);
		}
	  
	  /**
		 * 放款申请(手动提交事务，如出现问题变回滚)
		 */
		public boolean userTx(DataRow withdraw,DataRow user,String money)
		{
		
			Session session = getSession("web");
			session.beginTrans();
			try {
				DataRow fundrecord = new DataRow();//添加资金流向
	        	fundrecord.set("userid", user.getInt("id"));
	        	fundrecord.set("fundmode", "借贷放款");
	        	fundrecord.set("handlesum", money);
	        	fundrecord.set("usablesum", user.getDouble("usablesum"));
	        	fundrecord.set("recordtime", new Date());
	        	fundrecord.set("operatetype", 77);
	        	fundrecord.set("spending", money);
	        	fundrecord.set("borrow_id", 0);
	        	fundrecord.set("paynumber", withdraw.getString("orderid"));
	        	session.insert("sd_fundrecord", fundrecord);
	        	session.update("sd_user", user, "id", user.getInt("id"));
	        	session.insert("sd_withdraw", withdraw);
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
		 * 扣款申请(手动提交事务，如出现问题变回滚)
		 */
		public boolean userKk(DataRow withdraw,DataRow user,String money)
		{
		
			Session session = getSession("web");
			session.beginTrans();
			try {
				DataRow fundrecord = new DataRow();//添加资金流向
	        	fundrecord.set("userid", user.getInt("id"));
	        	fundrecord.set("fundmode", "自动扣款");
	        	fundrecord.set("handlesum", money);
	        	fundrecord.set("usablesum", user.getDouble("usablesum"));
	        	fundrecord.set("recordtime", new Date());
	        	fundrecord.set("operatetype", 100);
	        	fundrecord.set("spending", money);
	        	fundrecord.set("borrow_id", 0);
	        	fundrecord.set("paynumber", withdraw.getString("orderid"));
	        	session.insert("sd_fundrecord", fundrecord);
	        	session.update("sd_user", user, "id", user.getInt("id"));
	        	session.insert("sd_withdraw", withdraw);
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
		
      public void updateUserJk(DataRow row) throws Exception {
			
			getJdbcTemplate().update("sd_new_jkyx", row, "id", row.getString("id"));
	 }
      public void updateUserInfoH(DataRow row) throws Exception {
  		getJdbcTemplate().update("sd_user", row, "id", row.getString("id"));
  	}
      
      public DataRow getWithdraw(String orderId)
    		{    			
    	        String sql = "select * from sd_withdraw  where   orderid = '"+orderId +"'" ;
    			return getJdbcTemplate().queryMap(sql);
    		}
      
     
      public void updateWithRawInfo(DataRow row1){
  		
  		getJdbcTemplate().update("sd_withdraw ", row1, "orderid", row1.getString("orderid"));
  	}
      public DataRow getUserJK(String userid)
  	{
  		String sql = "select * from sd_new_jkyx  where  id =" +userid;
  		return getJdbcTemplate().queryMap(sql);
  	}
      
      public DataRow getUserInfo(String userid)
	 {
			String sql = "select u.id ,b.cardUserName , u.username ,u.mobilePhone ,u.username from sd_user u left join sd_bankcard b on b.userid = u.id  where u.id = "+userid ;
			return getJdbcTemplate().queryMap(sql);
	 }  
    
    public void updateJk(DataRow jkxx)
  	{
  		getJdbcTemplate().update("sd_new_jkyx", jkxx, "id", jkxx.getString("id"));
  	}
    
    public DataRow getWidthinfo(int  txid)
	{
		String sql = " select w.id ,w.status ,w.userid ,w.cellPhone as mobilephone , w.sum ,w.checkTime ," +
				"w.remarkResult, sd_bankcard.bankbs , sd_bankcard.remark ," +
				"sd_bankcard.cardNo ,sd_bankcard.cardUserName ,w.orderid  from sd_withdraw w  left join  sd_bankcard on sd_bankcard.userId =w.userid where w.id = " +txid ;
		
		return getJdbcTemplate().queryMap(sql);
	}
    
    public void updateWithdrawTx(DataRow row) throws Exception {
		
		getJdbcTemplate().update("sd_withdraw", row, "id", row.getString("id"));
	}
    
    public String getSHMoney(String jkid){
		StringBuffer sb = new StringBuffer();
		sb.append("select sjsh_money from sd_new_jkyx  where id="+jkid );
		return getJdbcTemplate().queryString(sb.toString());
	}
    public DataRow getBankInfo(String userid)
   	{
   		String sql = "select f.cardUserName,f.cardNo ,u.username ,u.mobilePhone ,f.userid from sd_bankcard f left join sd_user u on f.userid =u.id  where f.userid = "+userid;
   		return getJdbcTemplate().queryMap(sql);
   	}
   public DataRow getInvestorInfo(String userid) {
		
		String sql = " select * from  sd_user  where id ="+userid;
		return getJdbcTemplate().queryMap(sql);
		
	}
   public boolean updateUserJLMoney2(DataRow user,double money,String orderid,String investorId)
	{
	
		Session session = getSession("web");
		session.beginTrans();
		try {
			DataRow fundrecord = new DataRow();//添加资金流向
       	fundrecord.set("userid", user.getInt("id"));
       	fundrecord.set("fundmode", "投资收益");
       	fundrecord.set("handlesum", money);
       	fundrecord.set("usablesum", user.getDouble("usablesum"));
       	fundrecord.set("freezesum", 0.00);//当前冻结金额
       	fundrecord.set("dueinsum", 0);
       	fundrecord.set("recordtime", new Date());
       	fundrecord.set("operatetype", 46);
       	fundrecord.set("spending", money);
       	fundrecord.set("borrow_id", 0);
       	fundrecord.set("fundMode", investorId);
       	fundrecord.set("paynumber", orderid);           
       	session.insert("sd_fundrecord", fundrecord);
       	session.update("sd_user", user, "id", user.getInt("id"));
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
   
   public String  getJKname(String jkid){
		StringBuffer sb = new StringBuffer();
		sb.append("select b.cardUserName from sd_new_jkyx j left join sd_bankcard b on j.userid= b.userId  where j.id="+jkid );
		return getJdbcTemplate().queryString(sb.toString());
	}
   public String getUsername (String userid) {
	   
	    StringBuffer sb = new StringBuffer();
		sb.append("select username from sd_user  where id="+userid );
		return getJdbcTemplate().queryString(sb.toString());
	  
   }
 
   public String  getInvestorid(int  jkid){
		StringBuffer sb = new StringBuffer();
		sb.append("select investor_id  from sd_new_jkyx  where  id ="+jkid );
		
		return getJdbcTemplate().queryString(sb.toString());
	}
}
