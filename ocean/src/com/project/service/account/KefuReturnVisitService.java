package com.project.service.account;

import java.util.List;

import org.apache.log4j.Logger;
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
	public DataRow getUserList(String userid,String phone,String idno){
		String sql = "select sd_user.id,mobilephone,isshenfen,isjop,islianxi,yhbd,isschool,heihu_zt,profession,idno from sd_user left join sd_user_finance on sd_user.id=sd_user_finance.userid  where 1=1 ";
		if (!StringHelper.isEmpty(userid)) {
			
			sql += " and  id=" + userid;
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  mobilePhone='" + phone+"'";
		}
		if (!StringHelper.isEmpty(idno)) {

			sql += " and  idno='" + idno+"'";
		}
		return getJdbcTemplate().queryMap(sql);
	}
	public DataRow getUserJKList(int userid){
		String sql = "select id,cl_status,cl_yj,cl_time,cl03_time,cl02_time,cl02_yj,cl03_yj,cl02_status,cl03_status,fkr,sfyhw,sfyfk,jk_date,sjsh_money from sd_new_jkyx where userid="+userid;
		sql += " order by id desc limit 1 ";
		return getJdbcTemplate().queryMap(sql);
	}
	public String getUserName(int userid){
		String sql = "select realname from sd_user_finance where userid=" + userid;
		return getJdbcTemplate().queryString(sql);
	}
	public String getName(int userid){
		String sql = "SELECT username FROM sd_user WHERE id=" + userid;
		return getJdbcTemplate().queryString(sql);
	}
	public String getContent(int jkid){
		String sql = "select content from sd_returnvisit where jkjl_id="+jkid ;
	    sql += " order by id desc limit 1" ;
		return getJdbcTemplate().queryString(sql);
	}
	public DBPage getReturnVisitListSB(int curPage, int numPerPage,
			String userId, String realName, String phone, String startDate,
			String endDate, String commit, String idCard) throws Exception {
		logger.info("查找需要回访的用户");
		String sql = "select sd_new_jkyx.id as jkid, u.id as id ,f.realname as realName,f.idno as idNo ,b.cardno as cardno ,b.cardname as cardname ,u.mobilePhone as mobilephone "
				+ ", sfyfk,cl03_status,fkr,fkr_time, cl03_time from  sd_new_jkyx  left join sd_user u on sd_new_jkyx.userid = u.id left join sd_user_finance f on sd_new_jkyx.userid = f.userid LEFT join "
				+ "sd_bankcard b on b.userId =sd_new_jkyx.userid where  sfyfk=3";
		if (!StringHelper.isEmpty(userId)) {
			sql += " and  sd_user.id =" + userId;
		}
		
		if (!StringHelper.isEmpty(startDate)) {

			sql += " and sd_new_jkyx.fkr_time >'" + startDate + "'";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and sd_new_jkyx.fkr_time <'" + endDate + "'";
		}

		sql += " order by SUBSTRING(sd_new_jkyx.fkr_time,7,4) DESC ,SUBSTRING(sd_new_jkyx.fkr_time,4,2) DESC , SUBSTRING(sd_new_jkyx.fkr_time,1,2) DESC ,SUBSTRING(sd_new_jkyx.fkr_time,12,2) DESC,SUBSTRING(sd_new_jkyx.fkr_time,15,2) DESC,SUBSTRING(sd_new_jkyx.fkr_time,18,2) DESC ";

		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public DBPage getReturnVisitListSBHK(int curPage, int numPerPage,int code,int cmsuserid,
			String userId, String realName, String phone, String startDate,
			String endDate, String commit, String idCard) throws Exception {
		logger.info("查找需要回访的用户");
		String sql = "select sd_new_jkyx.id as jkid, u.id as id ,f.realname as realName,f.idno as idNo ,u.mobilePhone as mobilephone "
				+ ", sfyfk,sfyhw,hksb,hkpz,hkpz_time from  sd_new_jkyx  left join sd_user u on sd_new_jkyx.userid = u.id left join sd_user_finance f on sd_new_jkyx.userid = f.userid "
				+ " where  sfyfk=1 and sfyhw=0 and hksb=0 ";
		if (!StringHelper.isEmpty(userId)) {
			sql += " and  sd_user.id =" + userId;
		}
		
		if (!StringHelper.isEmpty(startDate)) {

			sql += " and sd_new_jkyx.hkpz_time >'" + startDate + "'";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and sd_new_jkyx.hkpz_time <'" + endDate + "'";
		}
		if(code==0){
			sql += " and kfhksb="+cmsuserid;
		}
		sql += " order by SUBSTRING(sd_new_jkyx.hkpz_time,7,4) ,SUBSTRING(sd_new_jkyx.hkpz_time,4,2) , SUBSTRING(sd_new_jkyx.hkpz_time,1,2) ,SUBSTRING(sd_new_jkyx.hkpz_time,12,2) ,SUBSTRING(sd_new_jkyx.hkpz_time,15,2) ,SUBSTRING(sd_new_jkyx.hkpz_time,18,2) ";

		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public List<DataRow> getReturnVisitListSBHK(int code,int cmsuserid,
			String userId, String realName, String phone, String startDate,
			String endDate, String commit, String idCard) throws Exception {
		logger.info("查找需要回访的用户");
		String sql = "select sd_new_jkyx.id as jkid, u.id as id ,f.realname as realName,f.idno as idNo ,u.mobilePhone as mobilephone "
				+ ", sfyfk,sfyhw,hkpz,hksb,hkpz_time from  sd_new_jkyx  left join sd_user u on sd_new_jkyx.userid = u.id left join sd_user_finance f on sd_new_jkyx.userid = f.userid "
				+ " where  sfyfk=1 and sfyhw=0 and hksb=0 ";
		if (!StringHelper.isEmpty(userId)) {
			sql += " and  sd_user.id =" + userId;
		}
		
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and sd_new_jkyx.hkpz_time >'" + startDate + "'";
		}
		if (!StringHelper.isEmpty(endDate)) {
			
			sql += " and sd_new_jkyx.hkpz_time <'" + endDate + "'";
		}
		if(code==0){
			sql += " and kfhksb="+cmsuserid;
		}
		sql += " order by SUBSTRING(sd_new_jkyx.hkpz_time,7,4) ,SUBSTRING(sd_new_jkyx.hkpz_time,4,2) , SUBSTRING(sd_new_jkyx.hkpz_time,1,2) ,SUBSTRING(sd_new_jkyx.hkpz_time,12,2) ,SUBSTRING(sd_new_jkyx.hkpz_time,15,2) ,SUBSTRING(sd_new_jkyx.hkpz_time,18,2) ";
		
		return getJdbcTemplate().query(sql);
	}
	public DBPage getReturnVisitListSBALL(int curPage, int numPerPage,
			String userId, String realName, String phone, String startDate,
			String endDate, String commit, String idCard) throws Exception {
		logger.info("查找所有放款失败的用户");
		String sql = "select sd_new_jkyx.id as jkid, u.id as id ,f.realname as realName,f.idno as idNo ,b.cardno as cardno ,b.cardname as cardname ,u.mobilePhone as mobilephone "
				+ ", sfyfk,cl03_status,fkr,fkr_time, cl03_time from  sd_new_jkyx  left join sd_user u on sd_new_jkyx.userid = u.id left join sd_user_finance f on sd_new_jkyx.userid = f.userid LEFT join "
				+ "sd_bankcard b on b.userId =sd_new_jkyx.userid where cl03_yj='Bankcard Error'";
		if (!StringHelper.isEmpty(userId)) {
			sql += " and  sd_user.id =" + userId;
		}
		
		if (!StringHelper.isEmpty(startDate)) {

			sql += " and sd_new_jkyx.cl03_time >'" + startDate + "'";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and sd_new_jkyx.cl03_time <'" + endDate + "'";
		}

		sql += " order by SUBSTRING(sd_new_jkyx.cl03_time,7,4) DESC ,SUBSTRING(sd_new_jkyx.cl03_time,4,2) DESC , SUBSTRING(sd_new_jkyx.cl03_time,1,2) DESC ,SUBSTRING(sd_new_jkyx.cl03_time,12,2) DESC,SUBSTRING(sd_new_jkyx.cl03_time,15,2) DESC,SUBSTRING(sd_new_jkyx.cl03_time,18,2) DESC ";

		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public DBPage getReturnVisitListCG(int curPage, int numPerPage,
			String userId, String realName, String phone, String startDate,
			String endDate, String commit, String idCard) throws Exception {
		logger.info("查找需要回访的用户");
		String sql = "select sd_new_jkyx.id as jkid, u.id as id ,f.realname as realName,f.idno as idNo ,b.cardno as cardno ,b.cardname as cardname ,u.mobilePhone as mobilephone "
				+ ", sfyfk,cl03_status,fkr,fkr_time, cl03_time from  sd_new_jkyx  left join sd_user u on sd_new_jkyx.userid = u.id left join sd_user_finance f on sd_new_jkyx.userid = f.userid LEFT join "
				+ "sd_bankcard b on b.userId =sd_new_jkyx.userid where  sfyfk=1 and kfhf_cgfk=0 ";
		if (!StringHelper.isEmpty(userId)) {
			sql += " and  sd_user.id =" + userId;
		}
		
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and sd_new_jkyx.fkr_time >'" + startDate + "'";
		}
		if (!StringHelper.isEmpty(endDate)) {
			
			sql += " and sd_new_jkyx.fkr_time <'" + endDate + "'";
		}
		
		sql += " order by cl03_time asc";
		
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
	public String getUserid(int jkid){
		String sql = "select userid from sd_new_jkyx where id="+jkid;
		
		return  getJdbcTemplate().queryString(sql);
	}
	public String getKefu(int jkid){
		String sql = "select cl03_yj from sd_new_jkyx where id="+jkid;
		
		return getJdbcTemplate().queryString(sql);
	}
	public Integer getKefuHKQD(int jkid){
		String sql = "select hkqd from sd_new_jkyx where id="+jkid;
		
		return getJdbcTemplate().queryInt(sql);
	}
	public String getKefuCG(int jkid){
		String sql = "select kfhf_cgfk from sd_new_jkyx where id="+jkid;
		
		return getJdbcTemplate().queryString(sql);
	}
	public Integer getKefuCXCS(int jkid){
		String sql = "select kfcxcs from sdcms_user where user_id="+jkid;
		
		return getJdbcTemplate().queryInt(sql);
	}
	public void UpdateBankcard(DataRow row)  throws Exception {
		getJdbcTemplate().update("sd_bankcard", row, "userid", row.getString("userid"));
		
	}
	public void InsertMsg(DataRow row)  throws Exception {
		getJdbcTemplate().insert("sd_msg",row);
		
	}
	public void InsertUserSelect(DataRow row)  throws Exception {
		getJdbcTemplate().insert("sd_userselectcx",row);
		
	}
	public void UpdateJK(DataRow row)  throws Exception {
		getJdbcTemplate().update("sd_new_jkyx", row, "id", row.getString("id"));
		
	}
	public void UpdateYHBD(DataRow row)  throws Exception {
		getJdbcTemplate().update("sd_user", row, "id", row.getString("id"));
		
	}
	public void UpdateKFCX(DataRow row)  throws Exception {
		getJdbcTemplate().update("sdcms_user", row, "user_id", row.getString("user_id"));
		
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
	 * 更新借款项的回访ID
	 */
	public Boolean updateKfhf_cgfk(int hfid, int jkid) {
		try {
			String sql = "update sd_new_jkyx set kfhf_cgfk=? where id=?";
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
				" on sd_user_finance.userId =sd_new_jkyx.userid  " +
				"left JOIN sd_returnvisit on sd_returnvisit.kefuid=sd_new_jkyx.kfhf_kfid and sd_returnvisit.jkjl_id=sd_new_jkyx.id  where  1=1 and kfhf_kfid<>0 ";
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
	/**
	 * 得到所有已经回访过的
	 * 
	 * @return
	 */
	public DBPage getAllReturnVisited(int curPage, int numPerPage, String userId,
			String realName,int temp1,  String startDate, String endDate) {
		logger.info("查询所有已经回访过的用户");
		String sql = "select sd_returnvisit.user_id as id,visitdate,content,sd_returnvisit.state,sd_returnvisit.code,kefuid,sdcms_user.name as kfname,sd_user_finance.realname as realname from sd_returnvisit left join sdcms_user on sd_returnvisit.kefuid = sdcms_user.user_id left join sd_user_finance on sd_returnvisit.user_id=sd_user_finance.userid  where 1=1";
		if(temp1 !=0){
			sql += " and sd_returnvisit.code="+temp1;
		}
		if (!StringHelper.isEmpty(userId)) {
			sql += " and  kefuid =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  kfname like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(startDate)) {

			sql += " and visitdate >='" + startDate + "'";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and visitdate <='" + endDate + "'";
		}

		sql += " order by SUBSTRING(visitdate,7,4) DESC ,SUBSTRING(visitdate,4,2) DESC , SUBSTRING(visitdate,1,2) DESC ,SUBSTRING(visitdate,12,2) DESC,SUBSTRING(visitdate,15,2) DESC,SUBSTRING(visitdate,18,2) DESC ";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
}
