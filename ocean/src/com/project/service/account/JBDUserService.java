package com.project.service.account;

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

public class JBDUserService extends BaseService {
	private static Logger logger = Logger.getLogger(JBDUserService.class);

	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}

	/**
	 * 更新个人信息（真实信息）
	 * 
	 */
	public boolean updateUserInfo(int userid, DataRow data, DataRow user) {

		Session session = getSession("web");
		session.beginTrans();
		try {
			session.update("sd_personjk", data, "userId", userid);
			session.update("sd_user", user, "id", userid);
			session.commitTrans();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			e.printStackTrace();
			session.rollbackTrans();
		} finally {
			session.close();
		}
		return false;
	}

	public void insertIpInfo(DataRow data) {
		getJdbcTemplate().insert("sd_ip", data);
	}

	public Integer getDxtiaoshu(String phone, String dateTS, String dateTE) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(*) from sd_ip  where createTime >'" + dateTS + "'and createTime <'" + dateTE
				+ "'  and phone='" + phone + "'");

		return getJdbcTemplate().queryInt(sb.toString());
	}

	/**
	 * 
	 * 获取用户被推荐人赚钱信息
	 * 
	 * @param userId
	 * @return
	 */
	public List<DataRow> getReffereeList(int userId) {
		String sql = "select * from sd_user where  refferee = " + userId + " order by createTime desc ";
		List<DataRow> list = getJdbcTemplate().query(sql);
		return list;
	}

	public void insertUserhongbao(DataRow data) {
		getJdbcTemplate().insert(" sd_hongbao", data);
	}

	public void insertJKInfo(DataRow data) {
		getJdbcTemplate().insert(" sd_new_jkyx", data);
	}

	public void insertPP(DataRow data) {
		getJdbcTemplate().insert(" sd_pipei", data);
	}

	public void insertUserMsg(DataRow row) {
		getJdbcTemplate().insert(" sd_msg", row);
	}
	public void insertUserFunPayOrderid(DataRow row) {
		getJdbcTemplate().insert(" sd_app_funpay_orderid", row);
	}

	public void insertBook(DataRow row) {
		getJdbcTemplate().insert(" sd_tushu", row);
	}

	public void insertChangePictureLoad(DataRow row) {
		getJdbcTemplate().insert(" sd_change_userxx_picture", row);
	}

	public List<DataRow> getJKJDList(int userId) {
		String sql = "select * from sd_new_jkyx  where userid  = " + userId + "  order by create_date desc ";
		List<DataRow> list = getJdbcTemplate().query(sql);
		return list;
	}

	// ShowJD()
	public List<DataRow> getShowJD(int type) {
		String sql = "select sd_new_jkyx.jk_money as jk_money , sd_new_jkyx.create_date as create_date ,sd_bankcard.cardUsername as cardUsername from sd_new_jkyx  left join  sd_bankcard on sd_bankcard.userId = sd_new_jkyx.userid   where cl_status = "
				+ type + " order by create_date desc limit 10";
		List<DataRow> list = getJdbcTemplate().query(sql);
		return list;
	}

	public DBPage getRecordJRList(int curPage, int numPerPage, String startDate, String endDate, String hkstat)
			throws Exception {

		String sql = "select j.id as jkid, u.id as id ,f.realName , u.mobilePhone,u.username, j.jk_money, j.jk_date ,j.sjsh_money ,j.sjds_money , j.sfyfk, j.fkdz_time,j.hkyq_time,j.sfyhw ,j.yuq_lx ,j.hk_time from "
				+ "   sd_new_jkyx j left join sd_user u on j.userid = u.id LEFT join sd_user_finance f on f.userId =j.userid  where  j.sfyfk <>0 ";

		if (hkstat.equals("1")) {

			sql += " and  j.sfyhw = 1";
		}

		if (hkstat.equals("2")) {

			sql += " and  j.sfyhw = 0 ";
		}
		sql += " and j.hk_time >'" + startDate + "'";
		sql += " and j.hk_time <'" + endDate + "'";
		sql += " and j.hkyq_time >'" + startDate + "'";
		sql += " and j.hkyq_time <'" + endDate + "'";
		sql += " order by  j.cl03_time desc";

		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DataRow findUserInfoByPhone(String username) {
		String sql = "select * from sd_user where mobilePhone ='" + username + "'";

		return getJdbcTemplate().queryMap(sql);
	}

	public DataRow findUserInfoByName(String username) {
		String sql = "select * from sd_user where username ='" + username + "'";
		return getJdbcTemplate().queryMap(sql);
	}

	public int getSum(String refferee) {
		String sql = "select sum from sd_delect where refferee = '" + refferee + "'";
		return getJdbcTemplate().queryInt(sql);
	}

	public int getProfession(String userid) {
		String sql = "select profession from sd_user where id = '" + userid + "'";
		return getJdbcTemplate().queryInt(sql);
	}

	public int getNum(String refferee) {
		String sql = "select num from sd_delect where refferee = '" + refferee + "'";
		return getJdbcTemplate().queryInt(sql);
	}

	public void updateDelect(String refferee, DataRow data) {
		getJdbcTemplate().update("sd_delect", data, "refferee", refferee);
	}

	public void updateUser(String userid, DataRow data) {
		getJdbcTemplate().update("sd_user", data, "id", userid);
	}

	public void updateBook(String bookname, DataRow data) {
		getJdbcTemplate().update("sd_user", data, "name", bookname);
	}

	public void addUser(String table, DataRow data) {
		getJdbcTemplate().insert(table, data);
	}

	public DataRow getRZ(int userid) {
		String sql = "select * from sd_user_finance a join sd_bankcard b ON a.userId = b.userId where a.userId="
				+ userid;
		return getJdbcTemplate().queryMap(sql);
	}

	public DataRow getRZBK(int userid) {
		String sql = "select * from sd_bankcard a join sd_user b ON a.userId = b.Id where a.userId=" + userid;
		return getJdbcTemplate().queryMap(sql);
	}

	public DataRow getRZSF(int userid) {
		String sql = "select * from sd_user_finance a join sd_user b ON a.userId = b.Id where a.userId=" + userid;
		return getJdbcTemplate().queryMap(sql);
	}
	
	public DataRow getjkMoney(String userid){
	 String sql = "SELECT sjsh_money,yuq_ts,sjds_money,lx FROM sd_new_jkyx WHERE sfyfk =1 AND sfyhw =1 AND userid="+userid+" order by id desc limit 1 ";
   	 return getJdbcTemplate().queryMap(sql);		
    }
	
	public DataRow getjkMoneyMax(String userid){
		 String sql = "SELECT sjsh_money,yuq_ts,sjds_money,lx FROM sd_new_jkyx WHERE sfyfk =1 AND sfyhw =1 AND userid="+userid+" order by sjsh_money desc limit 1 ";
	   	 return getJdbcTemplate().queryMap(sql);		
	    }
	
	public DataRow getjkNum(String userid){
   	 StringBuffer sb = new StringBuffer();
   	 sb.append("SELECT COUNT(*) as count ,MAX(sjsh_money) as maxMoney FROM sd_new_jkyx WHERE cl03_status =1 AND sfyhw =1 AND userid=");
 		 sb.append(userid);
   	 return getJdbcTemplate().queryMap(sb.toString());		
    }
	public DataRow getjkNumLast(String userid){
		String sql = "SELECT yuq_ts FROM sd_new_jkyx WHERE sfyfk=1 AND sfyhw =1 AND userid="+userid+" order by id desc ";
		return getJdbcTemplate().queryMap(sql);		
	}
	 public DataRow getUserRecThreeInfo(int  userid)
		{
			String sql = "select sd_user.id as userid ,sd_bankcard.cardNo, mobilePhone ,username ,sd_user_finance.realname,sd_user_finance.age, sd_bankcard.cardUserName ,sd_bankcard.bankbs ,sd_bankcard.bankName ,sd_bankcard.remark from sd_user  left join  sd_bankcard on sd_bankcard.userId =sd_user.id left join  sd_user_finance on sd_user_finance.userId =sd_user.id where sd_bankcard.userId = " +userid;

			return getJdbcTemplate().queryMap(sql);
		}
	 public DataRow getUserLianxiInfo(int  userid)
	 {
		 String sql = "select * from sd_lianxi where userid = " +userid;
		 
		 return getJdbcTemplate().queryMap(sql);
	 }
	public DataRow getMaxCount(String userid,String maxMoney){
   	 StringBuffer sb = new StringBuffer();
   	 sb.append("SELECT COUNT(*) as maxCount FROM sd_new_jkyx WHERE cl03_status =1 AND sfyhw =1 AND userid=");
 		 sb.append(userid);
 		 if(!maxMoney.equals("") && maxMoney!= null) {
 			 sb.append(" AND sjsh_money = '"+maxMoney+"'");
 		 }
   	 return getJdbcTemplate().queryMap(sb.toString());		
    }
	public DataRow getFacebook(int userid) {
		String sql = "select * from sd_facebook a join sd_user b ON a.userId = b.Id where a.userId=" + userid;
		return getJdbcTemplate().queryMap(sql);
	}

	public List<DataRow> getAllbook(int num) {
		String sql = "select * from sd_tushu  ";
		return getJdbcTemplate().query(sql);
	}

	public DataRow getALLRZ(int userid) {
		String sql = "select isshenfen,isjop,islianxi,yhbd,isschool,profession from sd_user where id=" + userid;
		return getJdbcTemplate().queryMap(sql);
	}

	public List<DataRow> getBorrHisRecordListPage(int curPage, int numPerPage, int userId) throws Exception {

		String sql = "select jk_money ,create_date ,jksfwc ,sfyfk, jk_date,cl03_status ,cl02_status ,cl_status ,spzt ,sjds_money ,sjsh_money ,id,hkqd from sd_new_jkyx where userid  = "
				+ userId + "  order by id desc";
		return getJdbcTemplate().query(sql, curPage, numPerPage);
	}

	public List<DataRow> getMsgCenter() {
		String sql = "SELECT * FROM sd_msg_center";
		return getJdbcTemplate().query(sql);
	}

	public void getDelectbook(DataRow row) {
		getJdbcTemplate().delete("sd_tushu", "id", row.getInt("id"));
	}

	public List<DataRow> getNumbook(int num) {
		String sql = "select * from sd_tushu where num=" + num;
		return getJdbcTemplate().query(sql);
	}

	public List<DataRow> getAllxtsj(String macaddress) {
		String sql = "select userid from sd_phonetype where macaddress='" + macaddress + "' group by userid ";
		return getJdbcTemplate().query(sql);
	}

	public DataRow getfindphone(String phone) {
		String sql = "select * from sd_user where mobilephone='" + phone + "'";
		return getJdbcTemplate().queryMap(sql);
	}

	public List<DataRow> getMybook(String userid) {
		String sql = "select * from sd_tushu where userid='" + userid + "'";
		return getJdbcTemplate().query(sql);
	}

	public List<DataRow> getBook(String bookname) {
		String sql = "select * from sd_tushu where name like'%" + bookname + "%'";
		return getJdbcTemplate().query(sql);
	}

	public DataRow findUserById(String id) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from sd_user where id = ");
		sb.append(id);
		return getJdbcTemplate().queryMap(sb.toString());
	}

	public DataRow findUserZPById(String id) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from sd_zhaopian where userid ='" + id + "' ");
		return getJdbcTemplate().queryMap(sb.toString());
	}

	public DataRow findUserHKQD(String id) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from sd_new_jkyx where userid ='" + id + "' and sfyfk=1 and sfyhw=0 ");
		return getJdbcTemplate().queryMap(sb.toString());
	}

	public DataRow findUserRZuserid(String id) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from sd_tongxunlu where userid =" + id);
		sb.append(" order by id desc limit 1 ");
		return getJdbcTemplate().queryMap(sb.toString());
	}

	public List<DataRow> getAllhongbao(String id) {
		String sql = "select * from sd_hongbao where userid=" + id;
		return getJdbcTemplate().query(sql);
	}

	public DataRow findUserTHJL(String id) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from sd_tonghuajilu where userid ='" + id + "' ");
		return getJdbcTemplate().queryMap(sb.toString());
	}

	public DataRow findUserGZById(String id) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from sd_work where userid ='" + id + "' ");
		return getJdbcTemplate().queryMap(sb.toString());
	}

	public DataRow findUserXXById(String id) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from sd_school where userid ='" + id + "' ");
		return getJdbcTemplate().queryMap(sb.toString());
	}

	public DataRow getuser(String userid) {
		String sql = " select * from v_sd_usermanage_baseinfo_2 where id = " + userid;
		return getJdbcTemplate().queryMap(sql);
	}

	public Integer getCountByPhone(String phone) {
		String sb = "select count(1) from sd_user where mobilePhone ='" + phone + "'";
		return getJdbcTemplate().queryInt(sb.toString());
	}

	public String getIdByToken(String userid) {
		String sql = "select tokenhtml from sd_user where id =" + userid;
		return getJdbcTemplate().queryString(sql);
	}

	public String getIdByToken(int userid) {
		String sql = "select tokenhtml from sd_user where id =" + userid;
		return getJdbcTemplate().queryString(sql);
	}

	public String getIdByPhone(String phone) {
		String sql = "select id from sd_user where mobilePhone = '" + phone + "'";
		return getJdbcTemplate().queryString(sql);
	}

	public String getUsername(String userid) {
		String sql = "select username from sd_user where id =" + userid;
		return getJdbcTemplate().queryString(sql);
	}

	public int getVipstatus(String userid) {
		String sql = "select vipstatus from sd_user where id =" + userid;
		return getJdbcTemplate().queryInt(sql);
	}

	public String getPhone(int userid) {
		String sql = "select mobilephone from sd_user where id =" + userid;
		return getJdbcTemplate().queryString(sql);
	}

	public void addUserPhoneType(DataRow row) throws Exception {
		getJdbcTemplate().insert("sd_phonetype", row);
	}

	public void addUserErrorPhoneType(DataRow row) throws Exception {
		getJdbcTemplate().insert("sd_errorphonetype", row);
	}

	
	public String getUserUserID(int userid) {
		String sql = "select id from sd_user where id = " + userid;
		return getJdbcTemplate().queryString(sql);
	}

	public List<DataRow> getNews(int limit) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from sd_news_finance  ");
		sb.append("order by daytime desc limit 5 ");
		return getJdbcTemplate().query(sb.toString());
	}
	/**
	 * 获取用户
	 * 
	 * @return
	 */
	public DataRow getUser(int userid) {
		String sql = "select * from sd_user where id = " + userid;
		return getJdbcTemplate().queryMap(sql);
	}

	public int getUserShenFen(String userid) {
		String sql = "select isshenfen from sd_user where id = " + userid;
		return getJdbcTemplate().queryInt(sql);
	}

	public int getUserZalo(String userid) {
		String sql = "select iszalo from sd_user where id = " + userid;
		return getJdbcTemplate().queryInt(sql);
	}

	public int getUserBank(String userid) {
		String sql = "select yhbd from sd_user where id = " + userid;
		return getJdbcTemplate().queryInt(sql);
	}

	public int getUserLianXi(String userid) {
		String sql = "select islianxi from sd_user where id = " + userid;
		return getJdbcTemplate().queryInt(sql);
	}

	public int getUserWork(String userid) {
		String sql = "select isjop from sd_user where id = " + userid;
		return getJdbcTemplate().queryInt(sql);
	}

	public int getUserVideo(int userid) {
		String sql = "select spzt from sd_new_jkyx where cl_status=1 and cl02_status=1 and cl03_status=0 and sfyfk=0 and userid = "
				+ userid;
		return getJdbcTemplate().queryInt(sql);
	}

	public int getUserSchool(String userid) {
		String sql = "select isschool from sd_user where id = " + userid;
		return getJdbcTemplate().queryInt(sql);
	}

	public int getUserFaceBook(String userid) {
		String sql = "select isfacebook from sd_user where id = " + userid;
		return getJdbcTemplate().queryInt(sql);
	}

	public String getUI(String userid) {
		String sql = "select userid from sd_zhaopian where userid = '" + userid + "' ";
		return getJdbcTemplate().queryString(sql);
	}

	public String getFaceBook(String userid) {
		String sql = "select userid from sd_facebook where userid = '" + userid + "' ";
		return getJdbcTemplate().queryString(sql);
	}

	public String getBK(String userid) {
		String sql = "select userid from sd_user_finance where userid = '" + userid + "' ";
		return getJdbcTemplate().queryString(sql);
	}

	public String getUIZalo(String userid) {
		String sql = "select userid from sd_zalo where userid = '" + userid + "' ";
		return getJdbcTemplate().queryString(sql);
	}

	public String getUICard(String userid) {
		String sql = "select userid from sd_bankcard where userid = '" + userid + "' ";
		return getJdbcTemplate().queryString(sql);
	}

	public String getUIWork(String userid) {
		String sql = "select userid from sd_work where userid = '" + userid + "' ";
		return getJdbcTemplate().queryString(sql);
	}

	public String getUISchool(String userid) {
		String sql = "select userid from sd_school where userid = '" + userid + "' ";
		return getJdbcTemplate().queryString(sql);
	}

	public String getUILianxi(String userid) {
		String sql = "select userid from sd_lianxi where userid = '" + userid + "' ";
		return getJdbcTemplate().queryString(sql);
	}

	public String getUIHKQD(String userid) {
		String sql = "select id from sd_new_jkyx where userid = '" + userid + "' and sfyfk=1 and sfyhw=0 ";
		return getJdbcTemplate().queryString(sql);
	}
	public String getUIHKQDCCC(String userid) {
		String sql = "select id from sd_new_jkyx where userid = '" + userid + "' order by id desc limit 1";
		return getJdbcTemplate().queryString(sql);
	}

	/**
	 * 更新用户充值金额到账户余额中(手动提交事务，如出现问题变回滚)
	 */
	public boolean updateUserChMoney(DataRow user) {

		Session session = getSession("web");
		session.beginTrans();
		try {
			session.update("sd_user", user, "id", user.getInt("id"));
			session.commitTrans();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			e.printStackTrace();
			session.rollbackTrans();
		} finally {
			session.close();
		}
		return false;
	}

	public DataRow getNewInfo(int id) {
		String sql = "select a.*,DATE_FORMAT(publishTime,'%mm%dd')time from sd_news a where id = " + id;
		return getJdbcTemplate().queryMap(sql);
	}

	public void updateNewInfo(DataRow row) {

		getJdbcTemplate().update("sd_news", row, "id", row.getInt("id"));
	}

	public void updateUserJk(DataRow row) {

		getJdbcTemplate().update("sd_new_jkyx", row, "id", row.getInt("id"));
	}

	public void updateUserHH(DataRow row) {

		getJdbcTemplate().update("sd_user", row, "id", row.getInt("id"));
	}

	public void updateNewPP(DataRow row) {

		getJdbcTemplate().update("sd_ziliao", row, "sfzhm", row.getString("sfzhm"));
	}

	/**
	 * 提现申请(手动提交事务，如出现问题变回滚)
	 */
	public boolean userTx(DataRow withdraw, DataRow user, double money) {

		Session session = getSession("web");
		session.beginTrans();
		try {
			DataRow fundrecord = new DataRow();// 添加资金流向
			fundrecord.set("userid", user.getInt("id"));
			fundrecord.set("fundmode", "账户提现");
			fundrecord.set("handlesum", money);
			fundrecord.set("usablesum", user.getDouble("usablesum"));
			fundrecord.set("freezesum", money);// 当前冻结金额
			fundrecord.set("dueinsum", 0);
			fundrecord.set("recordtime", new Date());
			fundrecord.set("operatetype", 120);
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
		} finally {
			session.close();
		}
		return false;
	}

	public List<DataRow> getNew(String type, int num) {
		String sql = "select id,title,publishTime as time,syimg,xwzy,xwly,type from sd_news WHERE state = 1 ";
		if (!type.equals("-1")) {
			sql += " and type = " + type;
		}
		sql += " ORDER BY publishTime desc";
		return getJdbcTemplate().query(sql, num);
	}

	public DataRow findUserBankcard(String userId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from sd_bankcard where userId = ");
		sb.append(userId);
		return getJdbcTemplate().queryMap(sb.toString());
	}

	public List findBankList() {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from sd_bank_status where status = 0");
		return getJdbcTemplate().query(sb.toString());
	}

	public DataRow findUserFinance(String userId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from sd_user_finance where userId = ");
		sb.append(userId);
		return getJdbcTemplate().queryMap(sb.toString());
	}

	public String getRemortIP(HttpServletRequest request) {
		if (request.getHeader("x-forwarded-for") == null) {
			return request.getRemoteAddr();
		}
		return request.getHeader("x-forwarded-for");
	}

	public void updateUserFinance(String userId, String realName, String idNo, String hint, String phone,
			String pay_type) {
		DataRow data = new DataRow();
		data.set("realName", realName);
		data.set("cellPhone", phone);
		data.set("idNo", idNo);
		data.set("status", 2);
		data.set("pay_type", pay_type);
		getJdbcTemplate().update("sd_user_finance", data, "userId", userId);
	}

	public DataRow findUserByIdcard(String IdCard) {
		String sql = "select * from sd_user_finance where idNo = '" + IdCard + "' and status = 1 ";
		return getJdbcTemplate().queryMap(sql);
	}

	public void addBankcard(String userId, String cardUserName, String bankName, String cardNo, String commitTime,
			int cardStatus, String bankbs) {
		DataRow data = new DataRow();
		data.set("userId", userId);
		data.set("cardUserName", cardUserName);
		data.set("bankName", bankName);
		data.set("bankbs", bankbs);
		data.set("cardNo", cardNo);
		data.set("commitTime", commitTime);
		data.set("cardStatus", cardStatus);
		getJdbcTemplate().insert("sd_bankcard", data);
	}

	public void addUserFinance(String userId, String realName, String idNo, String cellPhone, int status,
			String pay_type) {
		DataRow data = new DataRow();
		data.set("userId", userId);
		data.set("realName", realName);
		data.set("idNo", idNo);
		data.set("cellPhone", cellPhone);
		data.set("status", status);
		data.set("pay_type", pay_type);
		getJdbcTemplate().insert("sd_user_finance", data);
	}

	public void updateBankcard(String userId, String cardUserName, String bankName, String cardNo, String hint,
			String bankbs) {
		DataRow data = new DataRow();
		data.set("cardUserName", cardUserName);
		data.set("bankName", bankName);
		data.set("bankbs", bankbs);
		data.set("cardNo", cardNo);
		data.set("cardStatus", 2);
		data.set("commitTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		getJdbcTemplate().update("sd_bankcard", data, "userId", userId);
	}

	/**
	 * 添加订单
	 */
	public void addOrder(DataRow data) {
		try {
			getJdbcTemplate().insert("sd_recharge_detail", data);
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}

	public DataRow getUserByPhone(String phone) {

		String sql = "select * from sd_user where mobilePhone = " + phone;
		return getJdbcTemplate().queryMap(sql);
	}

	public void updateUser(DataRow user) {
		getJdbcTemplate().update("sd_user", user, "id", user.getInt("id"));

	}

	public void insertZXInfo(DataRow data) {
		getJdbcTemplate().insert(" sduser_portrait", data);
	}

	public void updateUserInfoHN(DataRow row) throws Exception {
		getJdbcTemplate().update("sd_user_finance", row, "id", row.getString("id"));
	}

	public void addUserInfoHN(DataRow row) throws Exception {
		getJdbcTemplate().insert("sd_user_finance", row);
	}

	public void updateUserInfoFB(DataRow row) throws Exception {
		getJdbcTemplate().update("sd_facebook", row, "userid", row.getString("userid"));
	}

	public void addUserInfoFB(DataRow row) throws Exception {
		getJdbcTemplate().insert("sd_facebook", row);
	}

	public void updateUserInfoH(DataRow row) throws Exception {
		getJdbcTemplate().update("sd_user", row, "id", row.getString("id"));
	}

	public void updateUserHKQD(DataRow row) throws Exception {
		getJdbcTemplate().update("sd_new_jkyx", row, "id", row.getString("id"));
	}

	public DBPage getJKJDListPage(int curPage, int numPerPage, int userId) throws Exception {

		String sql = "select jk_money ,create_date ,jksfwc ,sfyfk, jk_date  ,cl03_status ,cl02_status ,cl_status ,spzt ,sjds_money ,sjsh_money ,id,hkqd from sd_new_jkyx where userid  = "
				+ userId + "  order by id desc";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public Integer getMoneyCode(int userid) {

		StringBuffer sb = new StringBuffer();
		sb.append("select changemoney from sd_user  where id=" + userid);
		return getJdbcTemplate().queryInt(sb.toString());
	}

	public DataRow findUserJKByuserid(String id) {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"select id , cl_status  ,cl02_status,cl03_status ,spzt,jk_money ,jk_date,sjsh_money ,sjds_money,yuq_ts ,yuq_lx, sfyfk, jksfwc,hkyq_time,hkfq_time,hkfq_code,hkqd,hkfq_cishu  from sd_new_jkyx  where   jksfwc=0 and   cl_status <>3 and cl02_status <>3 and cl03_status <>3  and  userid = ");
		sb.append(id);
		sb.append(" order by create_date desc  limit 1 ");
		return getJdbcTemplate().queryMap(sb.toString());
	}
	public DataRow findUserJKByuseridSB(String id){
		StringBuffer sb = new StringBuffer();
		sb.append("select id , cl_yj,cl02_yj,cl03_yj,cl_status  ,cl02_status,cl03_status   from sd_new_jkyx  where   jksfwc=0 and   (cl_status =3 or cl02_status =3 or cl03_status =3)  and  userid = ");
		sb.append(id);
		sb.append(" order by create_date desc  limit 1 ");
		return getJdbcTemplate().queryMap(sb.toString());
	}
	public DataRow findUserJJJKByuserid(String id) {// 拒绝借款的信息
		StringBuffer sb = new StringBuffer();
		sb.append(
				"select id ,daytime, cl_status  ,cl_yj,cl_time,cl02_time,cl03_time,cl02_status,cl03_status ,cl03_yj,spzt,jk_money ,sjsh_money ,sjds_money,yuq_ts , sfyfk, jksfwc  from sd_new_jkyx  where userid = ");
		sb.append(id);
		sb.append(" order by id desc  limit 1 ");
		return getJdbcTemplate().queryMap(sb.toString());
	}

	public DBPage getTjjlListPage(int curPage, int numPerPage, int userId, String startDate, String endDate)
			throws Exception {

		String sql = "select handleSum ,recordTime ,sd_user_finance.realName as realName , fundMode from sd_fundrecord left join sd_user_finance on  sd_fundrecord.trader  = sd_user_finance.userId  where  sd_fundrecord.userId  = "
				+ userId + " and operateType in (46,66,56)  order by recordTime desc";

		if (!StringHelper.isEmpty(startDate)) {

			sql += " and recordTime >'" + startDate + "'";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and recordTime <'" + endDate + "'";
		}
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public Integer getJKshid(int userid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select id from sd_new_jkyx where cl02_status =1  and cl03_status =0  and spzt =0 and userid = ");
		sb.append(userid);
		return getJdbcTemplate().queryInt(sb.toString());
	}
	public Integer getJKshidCCC(int userid) {
		String sql = "select id from sd_new_jkyx where userid ="+userid+" order by id desc limit 1";
		return getJdbcTemplate().queryInt(sql);
	}

	public void updateJKSPInfo(DataRow jkinfo) {
		getJdbcTemplate().update("sd_new_jkyx", jkinfo, "id", jkinfo.getInt("id"));
	}

	public DataRow findUserJKById(int jkid) {

		String sql = "select sjsh_money , fkdz_time ,hongbaoid,jk_date, hkyq_time ,annualrate ,sd_new_jkyx.yuq_ts ,sd_new_jkyx.yuq_lx ,yqannualrate ,sjds_money ,hkstate,sd_user.mobilephone,sd_user_finance.address,sd_user_finance.realname as realName ,sd_bankcard.cardNo ,sd_bankcard.remark as idNo from sd_new_jkyx  left join sd_bankcard on sd_bankcard.userId = sd_new_jkyx.userId left join sd_user on sd_user.id = sd_new_jkyx.userId left join sd_user_finance on sd_user_finance.userId = sd_new_jkyx.userId where sd_new_jkyx.id = "
				+ jkid;
		return getJdbcTemplate().queryMap(sql);
	}

	public DataRow findUserJKFQById(int jkid) {

		String sql = "select sjsh_money , fkdz_time ,jk_date, hkfq_time as hkyq_time,hkfq_code ,annualrate ,sd_new_jkyx.yuq_ts ,sd_new_jkyx.yuq_lx ,yqannualrate ,sjds_money ,hkstate,sd_user.mobilephone,sd_user_finance.address,sd_user_finance.realname as realName ,sd_bankcard.cardNo ,sd_bankcard.remark as idNo from sd_new_jkyx  left join sd_bankcard on sd_bankcard.userId = sd_new_jkyx.userId  left join sd_user on sd_user.id = sd_new_jkyx.userId left join sd_user_finance on sd_user_finance.userId = sd_new_jkyx.userId where sd_new_jkyx.id = "
				+ jkid;
		return getJdbcTemplate().queryMap(sql);
	}

	public String getUserMoney(int userid) {

		StringBuffer sb = new StringBuffer();
		sb.append("select usableSum from sd_user where id = ");
		sb.append(userid);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public DataRow getRepaymentJKMoney(int userid) {
		String sql = "select cardNo, mobilePhone ,cardUserName ,bankbs ,bankName ,remark from sd_user  left join  sd_bankcard on sd_bankcard.userId =sd_user.id where userId = "
				+ userid;

		return getJdbcTemplate().queryMap(sql);
	}

	public String getTjliCount(int userid) {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"select ifnull(sum(handleSum),0) from sd_fundrecord left join sd_user_finance on  sd_fundrecord.trader  =sd_user_finance.userId  where   operateType in('66','56')  and sd_fundrecord.userId  = ");
		sb.append(userid);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public List<DataRow> getCMSuserid() {
		String sql = "SELECT user_id FROM sdcms_user WHERE roleid=2 or roleid=16 or roleid=28";

		return getJdbcTemplate().query(sql);
	}

	public List<DataRow> getAlltxlUser(String userid, String phone) {
		String sql = "SELECT userid,phone FROM sd_tongxunlu WHERE  phone='" + phone + "' group by userid ";

		return getJdbcTemplate().query(sql);
	}
	public List<DataRow> getAlltxlUser(String userid) {
		String sql = "SELECT name,phone FROM sd_tongxunlu WHERE userid=" + userid + " group by phone ";
		
		return getJdbcTemplate().query(sql);
	}

	public List<DataRow> getAllthjlUser(String userid, String phone) {
		String sql = "SELECT userid,number FROM sd_tonghuajilu WHERE  number='" + phone + "' group by userid ";

		return getJdbcTemplate().query(sql);
	}

	public List<DataRow> getAllsmsUser(String userid, String phone) {
		String sql = "SELECT userid,content FROM sd_sms WHERE  phone='" + phone + "' group by userid ";

		return getJdbcTemplate().query(sql);
	}

	public void addWithraw(DataRow data) {
		try {
			getJdbcTemplate().insert("sd_withdraw", data);
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}

	public Integer getWdXiaoXiaoCount(int userid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(*) from sd_msg where sfyd =0 and userid = ");
		sb.append(userid);
		return getJdbcTemplate().queryInt(sb.toString());
	}

	public void addUserXX(DataRow data) {
		getJdbcTemplate().insert(" sd_userxx", data);
	}

	public void addUserException(DataRow data) {
		getJdbcTemplate().insert(" sd_exception", data);
	}

	public void addUserZhaoPian(DataRow data) {
		getJdbcTemplate().insert(" sd_zhaopian", data);
	}

	public void addUserZalo(DataRow data) {
		getJdbcTemplate().insert(" sd_zalo", data);
	}

	public void addUserCard(DataRow data) {
		getJdbcTemplate().insert(" sd_bankcard", data);
	}

	public void addTongxunlu(DataRow data) {
		getJdbcTemplate().insert(" sd_tongxunlu", data);
	}

	public void addTonghuajilu(DataRow data) {
		getJdbcTemplate().insert(" sd_tonghuajilu", data);
	}

	public void addSMS(DataRow data) {
		getJdbcTemplate().insert(" sd_sms", data);
	}

	public void addUserWork(DataRow data) {
		getJdbcTemplate().insert(" sd_work", data);
	}

	public void addUserSchool(DataRow data) {
		getJdbcTemplate().insert(" sd_school", data);
	}

	public void addUserLX(DataRow data) {
		getJdbcTemplate().insert(" sd_lianxi", data);
	}

	public void addUserDWIP(DataRow data) {
		getJdbcTemplate().insert(" sd_dwip", data);
	}

	public void updateInfoHN(DataRow data) {
		getJdbcTemplate().update("sd_user_finance", data, "userid", data.getString("userid"));
	}

	public void updateUserZhaoPian(DataRow data) {
		getJdbcTemplate().update("sd_zhaopian", data, "userid", data.getString("userid"));
	}

	public void updateUserLoadVideo(DataRow data) {
		getJdbcTemplate().update("sd_new_jkyx", data, "id", data.getString("id"));
	}

	public void updateUserZalo(DataRow data) {
		getJdbcTemplate().update("sd_zalo", data, "userid", data.getString("userid"));
	}

	public void updateUserCard(DataRow data) {
		getJdbcTemplate().update("sd_bankcard", data, "userid", data.getString("userid"));
	}

	public void updateUserWork(DataRow data) {
		getJdbcTemplate().update("sd_work", data, "userid", data.getString("userid"));
	}

	public void updateUserSchool(DataRow data) {
		getJdbcTemplate().update("sd_school", data, "userid", data.getString("userid"));
	}

	public void updateUserLX(DataRow data) {
		getJdbcTemplate().update("sd_lianxi", data, "userid", data.getString("userid"));
	}

	public void addTonghuayy(DataRow data) {
		getJdbcTemplate().insert(" sd_tonghuayy", data);
	}

	public void addTonghuajl(DataRow data) {
		getJdbcTemplate().insert(" sd_tonghuajl", data);
	}

	public void addTaobaosh(DataRow data) {
		getJdbcTemplate().insert(" sd_taobaosh", data);
	}

	public void addTaobaouserinfo(DataRow data) {
		getJdbcTemplate().insert(" sd_taobaouserinfo", data);
	}

	public void addTaobaojy(DataRow data) {
		getJdbcTemplate().insert(" sd_taobaojy", data);
	}

	public void addJingdongsh(DataRow data) {
		getJdbcTemplate().insert(" sd_jingdongsh", data);
	}

	public void addJingdonguserinfo(DataRow data) {
		getJdbcTemplate().insert(" sd_jingdonguserinfo", data);
	}

	public void addJingdongjyjy(DataRow data) {
		getJdbcTemplate().insert(" sd_jingdongjy", data);
	}

	public List<DataRow> getShowXX() {
		String sql = "select * from sd_userxx  ";
		List<DataRow> list = getJdbcTemplate().query(sql);
		return list;
	}

	public Integer getJKCount(int userid2) {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"select count(*) from sd_new_jkyx  where sfyhw=0 and cl_status <>3 and cl02_status <>3 and cl03_status <>3  and  userid =  ");
		sb.append(userid2);
		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getSFCount(String idno, String realname) {
		String sql = "select count(*) from sd_user_finance where  idno ='" + idno + "' ";
		return getJdbcTemplate().queryInt(sql);
	}

	public List<DataRow> getALLshenfen(String idno, String realname) {
		String sql = "select userid from sd_user_finance where  idno ='" + idno + "'";
		return getJdbcTemplate().query(sql);
	}

	public List<DataRow> getALLshenfenSalo(String idno) {
		String sql = "select userid from sd_user_finance where  idno ='" + idno + "'";
		return getJdbcTemplate().query(sql);
	}

	public List<DataRow> getJKSB1ListOne(int userid) {
		String sql = "select j.id as jkid, u.id as id ,f.realName , INSERT(u.mobilePhone,4,4,'****') ,INSERT(f.idNo,4,4,'****'), j.jk_money, j.create_date ,j.cl_status ,j.cl_yj  ,"
				+ "j.cl_time ,j.cl_ren from sd_new_jkyx j  left join sd_user u on j.userid = u.id LEFT join sd_user_finance f on f.userId =j.userid  where  1 =1  and j.cl_status =3 and j.userid ="
				+ userid + " order by j.create_date desc limit 4  ";
		return getJdbcTemplate().query(sql);
	}

	public List<DataRow> getJKSBListTwo(int userid) {
		String sql = "select j.id as jkid, u.id as id ,f.realName , INSERT(u.mobilePhone,4,4,'****'),INSERT(f.idNo,4,4,'****'), j.jk_money, j.create_date ,j.cl02_status ,j.cl02_yj  ,"
				+ "j.cl02_time ,j.cl02_ren from sd_new_jkyx j  left join sd_user u on j.userid = u.id LEFT join sd_user_finance f on f.userId =j.userid  where  1 =1 and j.cl02_status =3 and j.userid ="
				+ userid + " order by j.create_date desc limit 4  ";
		return getJdbcTemplate().query(sql);
	}

	public List<DataRow> getJKSBListThree(int userid) {
		String sql = "select j.id as jkid, u.id as id ,f.realName , INSERT(u.mobilePhone,4,4,'****') ,INSERT(f.idNo,4,4,'****'), j.jk_money, j.create_date ,j.cl03_status ,j.cl03_yj  ,"
				+ "j.cl03_time ,j.cl03_ren from sd_new_jkyx j left join sd_user u on j.userid = u.id LEFT join sd_user_finance f on f.userId =j.userid  where  1 =1 and j.cl03_status =3 and j.userid ="
				+ userid + " order by j.create_date desc limit 4  ";
		return getJdbcTemplate().query(sql);
	}

	public List<DataRow> getJKCGList(int userid) {
		String sql = "select j.fkdz_time, j.cl02_time,j.cl_ren,j.cl_time,j.cl03_time ,j.cl02_ren ,j.cl03_ren ,j.hkyq_time,j.hk_time ,j.hkyq_time,j.sjds_money,j.lx,  j.sjsh_money , j.sfyhw ,j.create_date "
				+ " from  sd_new_jkyx j  where  j.sfyfk=1  and j.userid =" + userid + " order by j.create_date desc ";
		return getJdbcTemplate().query(sql);
	}

	public List<DataRow> getJKCGYQList(int userid) {
		String sql = "select j.fkdz_time, j.cl02_time,j.cl_ren,j.cl_time,j.cl03_time ,j.cl02_ren ,j.cl03_ren ,j.hkyq_time,j.hk_time ,j.hkyq_time,j.sjds_money,j.lx,  j.sjsh_money , j.sfyhw ,j.create_date "
				+ " from  sd_new_jkyx j  where  j.sfyfk=1  and j.yuq_ts>0 and j.userid =" + userid
				+ " order by j.create_date desc ";
		return getJdbcTemplate().query(sql);
	}

	public List<DataRow> getJKCGWHList(int userid) {
		String sql = "select j.fkdz_time, j.cl02_time,j.cl_ren,j.cl_time,j.cl03_time ,j.cl02_ren ,j.cl03_ren ,j.hkyq_time,j.hk_time ,j.hkyq_time,j.sjds_money,j.lx,  j.sjsh_money , j.sfyhw ,j.create_date "
				+ " from  sd_new_jkyx j  where  j.sfyfk=1 and j.sfyhw=0 and j.userid =" + userid
				+ " order by j.create_date desc ";
		return getJdbcTemplate().query(sql);
	}

	public DataRow getPP(String idno) {
		String sql = "select * from sd_ziliao where sfzhm ='" + idno + "' ORDER BY sd_ziliao.sfzhm DESC LIMIT 1";
		return getJdbcTemplate().queryMap(sql);
	}

	public Integer getPPCount(String idno) {
		String sql = "select count(*) from sd_ziliao where  sfzhm ='" + idno + "'";
		return getJdbcTemplate().queryInt(sql);
	}

	public String getRealname(int userid) {

		String sql = "select realname from sd_user_finance where  userid =" + userid;
		return getJdbcTemplate().queryString(sql);
	}
	public String getFamilyAddress(int userid) {
		
		String sql = "select homeaddress from sd_user_finance where  userid =" + userid;
		return getJdbcTemplate().queryString(sql);
	}
	public String getBirthDate(int userid) {
		
		String sql = "select age from sd_user_finance where  userid =" + userid;
		return getJdbcTemplate().queryString(sql);
	}

	public int getUserID(String idno) {

		String sql = "select userid from sd_user_finance where  idno =" + idno;
		return getJdbcTemplate().queryInt(sql);
	}

	public Integer getPiPei(int userid) {

		String sql = "select pipei from sd_new_jkyx where  userid =" + userid;
		sql += " order by id desc limit 1 ";
		return getJdbcTemplate().queryInt(sql);
	}

	public Integer getShenHeRen(int userid) {

		String sql = "select onesh from sd_new_jkyx where  userid =" + userid;
		sql += " order by id desc limit 1 ";
		return getJdbcTemplate().queryInt(sql);
	}

	public Integer getOLDstate(int userid) {

		String sql = "select state from sdcms_user where  user_id =" + userid;
		return getJdbcTemplate().queryInt(sql);
	}

	public Integer getHHZT(int userid) {

		String sql = "select heihu_zt from sd_user where  id =" + userid;
		return getJdbcTemplate().queryInt(sql);
	}

	public String getTJRuserid(String refferee) {

		String sql = "select id from sd_user where  refferee ='" + refferee + "'";
		return getJdbcTemplate().queryString(sql);
	}

	public String getAppcode(int userid) {
		String sql = "select appcode from sd_phonetype where userid=" + userid + " order by id desc limit 1";
		return getJdbcTemplate().queryString(sql);
	}

	public int getSmsCode() {
		String sql = "select smscode from sd_jishu where id=1";
		return getJdbcTemplate().queryInt(sql);
	}

	public String getUsername(int userid) {

		String sql = "select username from sd_user where  id =" + userid;
		return getJdbcTemplate().queryString(sql);
	}
	public String getCreateDate(int userid) {
		
		String sql = "select yearmonthday from sd_user where  id =" + userid;
		return getJdbcTemplate().queryString(sql);
	}
	 public String  getMobilePhone(int userid){
		 
		 String sql = "select mobilephone from sd_user where  id ="+userid ;
		 return getJdbcTemplate().queryString(sql);	 
	 }
	public String getRealname(String userid) {

		String sql = "select realname from sd_user_finance where  userid =" + userid;
		return getJdbcTemplate().queryString(sql);
	}

	public Integer getVipStatus(int userid) {

		String sql = "select vipstatus from sd_user where  id =" + userid;
		return getJdbcTemplate().queryInt(sql);
	}

	public Integer getFXhongbao(int userid) {

		String sql = "select fxhongbao from sd_user where  id =" + userid;
		return getJdbcTemplate().queryInt(sql);
	}

	public Integer getPiPeiID(int userid) {

		String sql = "select id from sd_new_jkyx where  userid =" + userid;
		sql += " order by id desc limit 1 ";
		return getJdbcTemplate().queryInt(sql);
	}

	public String getIdno(int userid) {

		String sql = "select idno from sd_user_finance where  userid =" + userid;
		return getJdbcTemplate().queryString(sql);
	}

	public String getUserpicP1(int userid) {

		String sql = "select p1 from sd_zhaopian where  userid =" + userid;
		return getJdbcTemplate().queryString(sql);
	}

	public String getUserpicVideoZT(int userid, int jkid) {

		String sql = "select spzt from sd_new_jkyx where cl_status=1 and cl02_status=1 and cl03_status=0 and sfyfk=0 and userid ="
				+ userid + " and id=" + jkid;
		return getJdbcTemplate().queryString(sql);
	}

	public String getUserpicVideo(int userid, int jkid) {

		String sql = "select spdz from sd_new_jkyx where  userid =" + userid + " and id=" + jkid;
		return getJdbcTemplate().queryString(sql);
	}

	public String getUserpicP2(int userid) {

		String sql = "select p2 from sd_zhaopian where  userid =" + userid;
		return getJdbcTemplate().queryString(sql);
	}

	public String getUserpicP3(int userid) {

		String sql = "select p3 from sd_zhaopian where  userid =" + userid;
		return getJdbcTemplate().queryString(sql);
	}

	public String getGeneralAudit(int userid) {

		String sql = "select ifnull(sum(sjsh_money),0) from sd_new_jkyx where  sfyfk =1 and investor_id =" + userid;
		return getJdbcTemplate().queryString(sql);
	}

	public String getTotalLoans(int userid) {

		String sql = "select  ifnull(sum(sjds_money),0) from sd_new_jkyx where sfyfk =1 and  investor_id =" + userid;

		return getJdbcTemplate().queryString(sql);
	}

	public Integer getInvestmentPen(int userid) {

		StringBuffer sb = new StringBuffer();
		sb.append("select count(1) from sd_new_jkyx  where sfyfk =1 and investor_id =" + userid);
		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getTotalInvestmentCount(int userid) {

		StringBuffer sb = new StringBuffer();
		sb.append(
				"select ifnull(sum(handleSum),0) from  sd_fundrecord  where fundmode='推荐人投资奖励' and userId =" + userid);
		return getJdbcTemplate().queryInt(sb.toString());
	}

	public String getTjjlCount(int userid) {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"select ifnull(sum(handleSum),0) from sd_fundrecord left join sd_user_finance on  sd_fundrecord.trader  =sd_user_finance.userId  where   operateType ='66'  and sd_fundrecord.userId  = ");
		sb.append(userid);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public DBPage getPresentationRecordList(int curPage, int numPerPage, int userId) throws Exception {

		String sql = "select * from sd_withdraw where status <>5 and userId =" + userId
				+ " and remark ='提现' order by applytime desc ";

		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public String getPresentationTotal(int userid) {

		StringBuffer sb = new StringBuffer();
		sb.append("select ifnull(sum(sum),0) from  sd_withdraw  where status <>5  and remark ='提现' and userId ="
				+ userid);
		return getJdbcTemplate().queryString(sb.toString());
	}

	/**
	 * 修改用户的身份证
	 * 
	 * @param userId
	 * @param cardNo
	 * @return
	 */
	public Boolean updateUserIdNo(int userId, String idNo) {
		Session session = getSession("web");
		session.beginTrans();
		try {
			String sql = "update sd_bankcard set remark=? where userId=?";
			Object[] object = new Object[] { idNo, userId };
			getJdbcTemplate().update(sql, object);
			session.commitTrans();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			e.printStackTrace();
			session.rollbackTrans();
		} finally {
			session.close();
		}
		return false;
	}

	/**
	 * 修改用户的身份证
	 * 
	 * @param userId
	 * @param cardNo
	 * @return
	 */
	public Boolean updateUserCardNo(int userId, String cardNo) {
		Session session = getSession("web");
		session.beginTrans();
		try {
			String sql = "update sd_bankcard set cardNo=? where userId=?";
			Object[] object = new Object[] { cardNo, userId };
			getJdbcTemplate().update(sql, object);
			session.commitTrans();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			e.printStackTrace();
			session.rollbackTrans();
		} finally {
			session.close();
		}
		return false;
	}

	public List<DataRow> getTGList(int code) {
		String sql = "select * from sd_tuiguang where tgcode=" + code;

		return getJdbcTemplate().query(sql);
	}

	public int getTGCS(int tgid) {
		String sql = "select tgcishu from sd_tuiguang where id=" + tgid;
		return getJdbcTemplate().queryInt(sql);
	}

	public int getJRTGCS(int tgid) {
		String sql = "select tgjrcishu from sd_tuiguang where id=" + tgid;
		return getJdbcTemplate().queryInt(sql);
	}

	public void updateTGCS(DataRow row1) {
		getJdbcTemplate().update("sd_tuiguang", row1, "id", row1.getString("id"));
	}

	/**
	 * 增加用户修改记录
	 * 
	 * @param tableName
	 * @param rd
	 */
	public void addUserRecord(String tableName, DataRow rd) {
		this.getJdbcTemplate().insert(tableName, rd);
	}

	public DBPage getMsgPageList(int curPage, int numPerPage, int userId) throws Exception {

		String sql = "select * from sd_msg   where  userid  = " + userId + "  order by id desc";

		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public void updateUserMsg() {
		String sql = " update sd_msg set sfyd =1 ,yuedu_time =CURRENT_TIMESTAMP where sfyd =0 ";
		getJdbcTemplate().update(sql);

	}
	 public List<DataRow> getAuditors(){
	       String sql = "select user_id,name from sdcms_user u where u.roleId = 2 and u.state = 1 and u.fdqx =1  ";
	       return getJdbcTemplate().query(sql);
	   }
	 public DataRow getUserRoles(int userId) {
	       String sql = "select user_id, name, roleid from sdcms_user u where u.user_id = ?";
	       return getJdbcTemplate().queryMap(sql, new Object[] {String.valueOf(userId)});
	   }
	 public List<DataRow> getUsersByRoles(List<Integer> roles){
	       StringBuilder sb = new StringBuilder();
	       for (Integer r : roles) {
	          sb.append(",").append(r);
	       }
	       String sql = "select user_id, name, roleid from sdcms_user u where u.roleid in (" + sb.substring(1) + ") and u.state = 1";
	       return getJdbcTemplate().query(sql); 
	   } 
	 public List<DataRow> getUPdaterow()
	 {
	 	String sql = "SELECT b.userid,b.cardusername FROM  sd_bankcard b LEFT JOIN sd_user_finance s ON s.userid=b.userid WHERE s.realname IS NULL AND s.userid IS NOT NULL";
	 	return getJdbcTemplate().query(sql);
	 } 
	 public void updateRowNAME(DataRow row1)
		{
			getJdbcTemplate().update("sd_user_finance", row1, "userid", row1.getString("userid"));
		}
	 
	 /**Lin
	   * 修改用户user_mark标识
	   * @param userId
	   * @param userMark
	   */
	  public void updateRowUserMark(int userId, int userMark) {
	   String sql = "update sd_user set user_mark=? where id=?";
	   Object[] object = new Object[] { userMark, userId };
	   getJdbcTemplate().update(sql, object);
	   
	  }
	  
	  public List<DataRow> getAuditorsshzz(){
	       String sql = "select user_id,name from sdcms_user u where u.roleId = 17 and u.state = 1 and u.fdqx =1 ";
	       return getJdbcTemplate().query(sql);
	   }
	  
	  /**
		 * 查询电话记录
		 * @param phone
		 * @return
		 */
		public DataRow getnewPhonerecord(String phone) {
			
			String sql = "  SELECT * FROM sduser_new_phone_record  WHERE  phone ='"+phone+"'";	
			
			return getJdbcTemplate().queryMap(sql);
		}
		
		/**
		 * 短信用户——SH
		 * @param phone
		 * @return
		 */
		public int getUserPhoneDXSH(String phone){
			String sql = "select count(*) from sduser_new_phone_dxsh where phone='"+phone+"'";
			return getJdbcTemplate().queryInt(sql);
		}
		/**
		 * 短信用户——HK
		 * @param phone
		 * @return
		 */
		public int getUserPhoneDXHK(String phone){
			String sql = "select count(*) from sduser_new_phone_dxhk where phone='"+phone+"'";
			return getJdbcTemplate().queryInt(sql);
		}

		/*
		 * 查询身份证状态是否在黑名单
		 */
		public int getusercmnd_state(String  idno) {
			String sql = "SELECT COUNT(*) FROM sd_user_hmd WHERE idno='" + idno+"'";
			return getJdbcTemplate().queryInt(sql);
		}
		
		public DataRow getnewmsg_function(String functionname) {
			
			String sql = "  SELECT * FROM sdcms_function_control  WHERE  function_name ='"+functionname+"'";	
			
			return getJdbcTemplate().queryMap(sql);
		}
		
		/**
		 * 通讯录条数
		 */
		public int getusertongxunlucount(String  userid) {
			String sql = "SELECT COUNT(*) FROM sd_tongxunlu WHERE userid='" + userid+"'";
			return getJdbcTemplate().queryInt(sql);
		}
		
		public int getusrecfrzcs(String id) {
		     String sql ="SELECT cfrz_cs FROM sd_user WHERE id = "+id;
		     return getJdbcTemplate().queryInt(sql);
		}
		
	
}
