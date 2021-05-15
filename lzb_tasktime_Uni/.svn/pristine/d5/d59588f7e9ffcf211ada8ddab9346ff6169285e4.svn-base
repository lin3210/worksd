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
public class GsdLoanService extends BaseService
{

	private static Logger logger = Logger.getLogger(GsdLoanService.class);
	
	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}
	
	/**
	 * 获取投资人已投资，且未放款的项目
	 */
	public List<DataRow> getLoanList()
	{		
		String sql = "select gsd_new_jkyx.id AS jkid,gsd_user.id as uid,gsd_new_jkyx.investor_id as investid,gsd_ex_secsuc.received_amount as sjfk,gsd_bankcard.cardUserName"
				+",gsd_bankcard.idNo,gsd_bankcard.bankName,gsd_user.mobilePhone,gsd_new_jkyx.annual_rate,gsd_new_jkyx.investor_id"
				+",gsd_ex_secsuc.received_amount,gsd_ex_secsuc.actual_amount,gsd_bankcard.cardNo"
				+" from gsd_ex_thrsuc"
				+" left join gsd_new_jkyx on gsd_new_jkyx.id=gsd_ex_thrsuc.loanID"
				+" left join gsd_bankcard on gsd_bankcard.userId=gsd_ex_thrsuc.userid"
				+" left join gsd_user on gsd_user.id=gsd_ex_thrsuc.userid"
				+" left join gsd_ex_secsuc on gsd_ex_secsuc.loanID=gsd_ex_thrsuc.loanID"
				+" where gsd_new_jkyx.is_inverRepay=2 and gsd_new_jkyx.isLoan=2 and gsd_new_jkyx.loan_status=5";
		return getJdbcTemplate().query(sql);
	}
	/**
	 * 修改借款信息
	 * @param withdraw
	 */
	public void updateGsdNewJkyx(DataRow withdraw)
	{
		try {
			getJdbcTemplate().update("gsd_new_jkyx", withdraw, "id", withdraw.getInt("id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 修改用户信息
	 * @param withdraw
	 */
	public void updateUser(DataRow withdraw)
	{
		try {
			getJdbcTemplate().update("gsd_user", withdraw, "id", withdraw.getInt("id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 新增提现
	 * @param row
	 */
	public void insertWithdraw(DataRow row)
	{
	  	try {
			getJdbcTemplate().insert("gsd_withdraw", row);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 *
	 * @param row
	 */
	public void insertUserMsg(DataRow row)
	{
	 		try {
				getJdbcTemplate().insert("gsd_msg", row);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	/**
	 * 查询投资人信息
	 * @param userid
	 * @return
	 */
	public DataRow getUserRecThreeInfo(String  userid)
	{
		String sql = "select gsd_user.id as userid ,idNo, mobilePhone ,username , cardUserName ,bankbs ,bankName" 
				+" from gsd_user  left join  gsd_bankcard on gsd_bankcard.userId =gsd_user.id where gsd_user.id = " +userid ;

		return getJdbcTemplate().queryMap(sql);
	}
}
