package com.project.service.kefu;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.record.PageBreakRecord;

import com.thinkive.base.jdbc.DBPage;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.service.BaseService;
import com.thinkive.base.util.StringHelper;

public class KefuReturnVisitService extends BaseService {
	private static Logger logger = Logger
			.getLogger(KefuReturnVisitService.class);

	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}

	/**
	 * 需要回访客户列表
	 * 
	 * @param curPage
	 *            开始页
	 * @param numPerPage
	 * @param userId
	 *            用户ID
	 * @param realName
	 *            用户姓名
	 * @param phone
	 *            电话号码
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @param commit
	 * @param idCard
	 *            身份证
	 * @return
	 * @throws Exception
	 */
	public DBPage getReturnVisitList(int curPage, int numPerPage,
			String userId, String realName, String phone, String startDate,
			String endDate, String commit, String idCard) throws Exception {
		logger.info("查找需要回访的用户");
		String sql = "select sd_new_jkyx.id as jkid, sd_user.id as id ,sd_user_finance.realName as realName, mobilePhone "
				+ ",idNo, jk_money, sjsh_money,jk_date , cl03_status,sdcms_user.name as skname, cl02_time,spdz ,"
				+ "spsj,spzt  from  sd_new_jkyx  left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT join "
				+ "sd_user_finance on sd_user_finance.userId =sd_new_jkyx.userid LEFT JOIN sdcms_user on "
				+ "sdcms_user.USER_ID =sd_new_jkyx.cl02_ren  where  1=1  and cl02_status=1 and cl03_status = 0 and spzt=1 and kfhf_kfid=0";
		if (!StringHelper.isEmpty(userId)) {
			sql += " and  sd_user.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  sd_user_finance.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {

			sql += " and  idNo like '%" + idCard + "%'";
		}
		if (!StringHelper.isEmpty(startDate)) {

			sql += " and sd_new_jkyx.cl02_time >'" + startDate + "'";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and sd_new_jkyx.cl02_time <'" + endDate + "'";
		}

		sql += " order by spsj asc";

		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	/**
	 * 新增一条回访记录
	 * 
	 * @param tableName
	 * @param dr
	 * @return
	 */
	public int addReturnVisit(String tableName, DataRow dr) {
		int id = 0;
		try {
			this.getJdbcTemplate().insert(tableName, dr);
			id = this.getJdbcTemplate().queryInt(
					"select max(id) from sd_returnvisit");
			return id;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 更新借款项的回访ID
	 */
	public Boolean updateKfhf_kfid(int hfid, int jkid) {
		try {
			String sql = "update sd_new_jkyx set kfhf_kfid=? where id=?";
			Object[] org = new Object[] { hfid, jkid };
			this.getJdbcTemplate().update(sql, org);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 得到所有已经回访过的
	 * 
	 * @return
	 */
	public DBPage getAllVisited(int curPage, int numPerPage, String userId,
			String realName, String phone, String startDate, String endDate,
			Integer state) {
		logger.info("查询已经回访过的用户");
		String sql = " select sd_new_jkyx.id as jkid,sd_user.id as id,sd_user_finance.realname,mobilephone,idno," +
				"jk_money,sjsh_money,sjds_money,jk_date,sd_returnvisit.content remark,visitdate,sd_returnvisit.state stated " +
				" from sd_new_jkyx left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT join sd_user_finance" +
				" on sd_user_finance.userId =sd_new_jkyx.userid LEFT JOIN sdcms_user on sdcms_user.USER_ID =sd_new_jkyx.cl02_ren " +
				"left JOIN sd_returnvisit on sd_returnvisit.Id=sd_new_jkyx.kfhf_kfid where  1=1 and cl02_status=1 and cl03_status = 0 and kfhf_kfid<>0 ";
		if (!StringHelper.isEmpty(userId)) {
			sql += " and  sd_user.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  sd_user_finance.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(startDate)) {

			sql += " and visitdate >='" + startDate + "'";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and visitdate <='" + endDate + "'";
		}

		if (state != -1) {
			sql += " and sd_returnvisit.state =" + state + "";
		}
		sql += " order by visitdate desc";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
}
