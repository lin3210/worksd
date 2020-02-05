package com.service;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.jdbc.session.Session;
import com.thinkive.base.service.BaseService;

/**
 * 自动提现service
 * @author Administrator
 *
 */
public class AotuSDFKService extends BaseService
{

	private static Logger logger = Logger.getLogger(AotuSDFKService.class);
	
	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}
	
	
	/**
	 * 获取用户认证信息
	 * @return
	 */
	public DataRow getUserRz(int userid)
	{
		String sql = "select * from t_user_finance f,t_bankcard b where f.userId = b.userId and f.userId = "+userid;
		return getJdbcTemplate().queryMap(sql);
	}
	
	/**
	 * 获取用户借款信息
	 * 
	 * 
	 */
	public DataRow getUserJK(String userid)
	{
		String sql = "select * from sd_new_jkyx  where sfyfk = 2  and userId =" +userid;
		return getJdbcTemplate().queryMap(sql);
	}
	
	/**
	 * 获取需要自动提现
	 */
	public List<DataRow> getAllTxList()
	{
		//String sql = "select w.id,w.orderid,w.acount as cardno,w.name as cardusername,w.sum,b.bankname,w.userId,DATE_FORMAT(w.applyTime,'%Y%m%d%H%i%s') as applyTime from t_withdraw w,t_bankcard b where w.acount = b.cardno and w.status = 4";
		String sql = "select a.*,f.pay_type,f.bind_no,f.realName,f.cellPhone from (select w.id,w.orderid,w.acount as cardno,w.name as cardusername,w.sum,b.bankname,w.userId,DATE_FORMAT(w.applyTime,'%Y%m%d%H%i%s') as applyTime from t_withdraw w,t_bankcard b where w.acount = b.cardno and w.status = 4) a,t_user_finance f where a.userid = f.userId";
		return getJdbcTemplate().query(sql);
	}
	
	/**
	 * 更新提现
	 */
	public void updateWithdraw(DataRow withdraw)
	{
	
		getJdbcTemplate().update("sd_withdraw", withdraw, "id", withdraw.getInt("id"));
	}
	
	
	/**
	 * 获取需要查询放款跟踪的数据
	 */
	public List<DataRow> getAllFKCxList()
	{
		String sql = "select id, orderid , DATE_FORMAT(applyTime,'%Y%m%d') as checkTime2 ,userid ,remark ,sum ,versoin from sd_withdraw  where status in (4,6) order by checkTime   limit 10 ";
		return getJdbcTemplate().query(sql);
	}
	
	/**
	 * 更新借款信息
	 * 
	 */
	public void updateJk(DataRow jkxx)
	{
		getJdbcTemplate().update("sd_new_jkyx", jkxx, "id", jkxx.getString("id"));
	}
	
     public void insertUserMsg(DataRow row)
	  	{
	  		getJdbcTemplate().insert(" sd_msg", row);
	  	}
     public DataRow getBankInfo(String userid)
 	{
 		String sql = "select f.cardUserName,f.cardNo ,u.username ,u.mobilePhone ,f.userid from sd_bankcard f left join sd_user u on f.userid =u.id  where f.userid = "+userid;
 		return getJdbcTemplate().queryMap(sql);
 	}
     
	/**
	 * 获取提现详情
	 * @param orderid
	 * @return
	 */
	public DataRow getTxInfo(String orderid)
	{
		String sql = "select w.sum,(u.freezeSum-w.sum) freezeSum,(u.usableSum+w.sum) usableSum,w.errstatus,u.id from t_withdraw w,t_user u where w.userid = u.id and w.orderid  = '"+orderid+"' and w.errstatus = 0";
		return getJdbcTemplate().queryMap(sql);
	}
	
	/**
	 * 更新提现处理失败状态
	 * @return
	 */
	public void updateTxErrStatus(String orderid)
	{
		getJdbcTemplate().update("update t_withdraw set errstatus = 1 where orderid = '"+orderid+"'");
	}
	
	/**
	 * 更新用户信息
	 */
	public void updateUserMoney(DataRow user)
	{
		getJdbcTemplate().update("t_user", user, "id", user.getString("userid"));
	}
	
	
	public DataRow getInvestorInfo(String userid) {
		
		String sql = "select * from  sd_user  where id ="+userid;
		return getJdbcTemplate().queryMap(sql);
		
	}
	
	
	public String getSjdz(String jkid){
		StringBuffer sb = new StringBuffer();
		sb.append("select sjds_money from sd_new_jkyx  where id="+jkid );
		return getJdbcTemplate().queryString(sb.toString());
	}
		
	public String getSHMoney(String jkid){
		StringBuffer sb = new StringBuffer();
		sb.append("select sjsh_money from sd_new_jkyx  where id="+jkid );
		return getJdbcTemplate().queryString(sb.toString());
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
	 
	
		public String  getJKname(String jkid){
			StringBuffer sb = new StringBuffer();
			sb.append("select b.cardUserName from sd_new_jkyx j left join sd_bankcard b on j.userid= b.userId  where j.id="+jkid );
			return getJdbcTemplate().queryString(sb.toString());
		}
		 public String findReffer(String userId){
				StringBuffer sb = new StringBuffer();
				sb.append("select refferee from sd_user where  id = ");
				sb.append(userId);
				
				return getJdbcTemplate().queryString(sb.toString());
		}
		 public DataRow getRefferUser(String refferCount)
			{
				String sql = "select * from sd_user where id = "+refferCount +" or mobilePhone =" +refferCount;
				return getJdbcTemplate().queryMap(sql);
			}
		 
		 public boolean updateUserJLMoney(DataRow user,double money,String orderid ,String investorId)
			{
			
				Session session = getSession("web");
				session.beginTrans();
				try {
					DataRow fundrecord = new DataRow();//添加资金流向
		        	fundrecord.set("userid", user.getInt("id"));
		        	fundrecord.set("fundmode", "推荐人投资奖励");
		        	fundrecord.set("handlesum", money);
		        	fundrecord.set("usablesum", user.getDouble("usablesum"));
		        	fundrecord.set("freezesum", 0.00);//当前冻结金额
		        	fundrecord.set("dueinsum", 0);
		        	fundrecord.set("recordtime", new Date());
		        	fundrecord.set("operatetype", 56);
		        	fundrecord.set("spending", money);
		        	fundrecord.set("borrow_id", 0);
		        	fundrecord.set("trader", 0);
		        	
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
}
