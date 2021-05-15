package com.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.service.BaseService;

/**
 * 自动提现service
 * @author Administrator
 *
 */
public class AotuTxService extends BaseService
{

	private static Logger logger = Logger.getLogger(AotuTxService.class);
	
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
		getJdbcTemplate().update("t_withdraw", withdraw, "id", withdraw.getInt("id"));
	}
	
	
	/**
	 * 获取需要查询提现的数据
	 */
	public List<DataRow> getAllTxCxList()
	{
		String sql = "select w.id,w.orderid,w.name ,w.cellPhone , w.sum,DATE_FORMAT(w.checkTime,'%Y%m%d') as checkTime from t_withdraw w,t_user_finance f where w.status in (4,6) and f.pay_type = 1 and w.userId = f.userId";
		return getJdbcTemplate().query(sql);
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
}
