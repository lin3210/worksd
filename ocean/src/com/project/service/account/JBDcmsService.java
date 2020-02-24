package com.project.service.account;

import java.util.Date;
import java.util.List;

import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;

import com.thinkive.base.jdbc.DBPage;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.jdbc.session.Session;
import com.thinkive.base.service.BaseService;
import com.thinkive.base.util.StringHelper;

public class JBDcmsService extends BaseService {

	private static Logger logger = Logger.getLogger(JBDcmsService.class);
	
	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}
	
	
		 
	/**********************************xiong-start*****************************************/
		
	//xiong-20190821-查询所有提交借款1天没有上传视频的用户	
	public  List<DataRow> getSendUserId(){
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT s.userid,s.id,u.mobilePhone,u.username,f.realName,s.spdz,s.cl02_time ,cl03_status FROM  sd_new_jkyx s  ");
		sb.append(" LEFT JOIN  sd_user u ON s.userid= u.id  LEFT JOIN sd_user_finance f ON s.userid=f.userId ");
		sb.append(" WHERE s.spdz IS NULL AND cl_status=1 AND cl02_status=1 AND cl03_status=0 AND spzt=0 ");
		sb.append(" AND CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2)) <date_format(adddate(now(),-1),'%Y-%m-%d') ");
		logger.info(sb.toString());
		return getJdbcTemplate().query(sb.toString());
	}
	
	//xiong-20190821-今天发送短信提醒上传视频的总条数
	public int getSendUserNum() {
		String sql=" SELECT COUNT(*) FROM(SELECT COUNT(*) FROM sd_task_msg WHERE CONCAT(SUBSTRING(create_time,1,10))=DATE_FORMAT(NOW(),'%Y-%m-%d') AND msgtype=3 GROUP BY userid) a ";
		
		return getJdbcTemplate().queryInt(sql);
	}
	//xiong-20190821-总共发送短信提醒上传视频的总条数
	public int getSendUserAllNum() {
		String sql=" SELECT COUNT(*) FROM(SELECT COUNT(*) FROM sd_task_msg WHERE msgtype=3 GROUP BY userid) a ";
		
		return getJdbcTemplate().queryInt(sql);
	}
	//xiong-20190821-总过有多少发送了短信的用户上传了视频
	public int getSendUserALLVideoNum() {
		String sql = " SELECT COUNT(*) FROM(SELECT COUNT(*) FROM sd_task_msg t LEFT JOIN  sd_new_jkyx n  ON t.userid=n.userid  WHERE  t.msgtype=3   AND spzt=1  GROUP BY t.userid) a ";

		return getJdbcTemplate().queryInt(sql);

	}

	// xiong-20190821-插入发送短信信息
	public void intsertUserMsg(DataRow row) {

		getJdbcTemplate().insert("sd_task_msg", row);
	}

	//xiong-20190613
	public void insertUserKFMsg(DataRow row) {
        getJdbcTemplate().insert("sd_returnvisit", row);
    }
	
	//xiong-20190613
	public DBPage getAllCSHMDList(int curPage, int numPerPage, String userId,
			String jkid,String startDate, String endDate)
					throws Exception {
		
		String sql = "select r.id,r.jkjl_id,r.user_id,r.visitdate,r.content,r.kefuid,r.state,r.yuqts,u.name from sd_returnvisit_hmd r left join sdcms_user u on r.kefuid=u.user_id ";
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " where r.user_id =" + userId;
		}
		if (!StringHelper.isEmpty(jkid)) {
			
			sql += " where r.jkjl_id =" + jkid;
		}
		sql += " order by id desc";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	
	//xiong-失联名单
		public DBPage getAllUserSlmd(int curPage, int numPerPage, String userId,
				String jkid,String startDate, String endDate)
						throws Exception {
			
			String sql = "select r.id,r.jkjl_id,r.user_id,r.visitdate,r.content,r.kefuid,r.yuqts,u.name from sd_returnvisit_shilian r left join sdcms_user u on r.kefuid=u.user_id  ";
			if (!StringHelper.isEmpty(userId)) {
				
				sql += " where r.user_id =" + userId;
			}
			if (!StringHelper.isEmpty(jkid)) {
				
				sql += " where r.jkjl_id =" + jkid;
			}
			sql += " order by id desc";			
			return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
		}
	
		public void updateUserSlmd(DataRow row) throws Exception {
			  getJdbcTemplate().update("sd_returnvisit_slmd", row, "id", row.getString("id"));
			 }	
		
	/**********************************xiong-end*****************************************/	
	public void updateUserInfoHMD(DataRow row) throws Exception {
		  getJdbcTemplate().update("sd_returnvisit_hmd", row, "id", row.getString("id"));
		 }
	

	public DataRow getUserByNameAndPwd(String userName, String pwd) {

//		StringBuffer sb = new StringBuffer();
//		sb.append("select * from sdcms_user where phone = '");
//		sb.append(userName);
//		sb.append("' and password = '");
//		sb.append(pwd + "'");
//		String dr = getJdbcTemplate().queryString(sb.toString());
		
		String sql ="SELECT * FROM sdcms_user WHERE phone = ?  AND PASSWORD = ?";
		Object[] obj=new Object[]{userName,pwd};
		
		return getJdbcTemplate().queryMap(sql,obj);
	}

	public void updateUser(String id, String lastIP, String lastTime,
			int login_times) throws Exception {
		DataRow dr = new DataRow();
		dr.set("lastip", lastIP);
		dr.set("last_time", lastTime);
		dr.set("login_times", login_times);
		getJdbcTemplate().update("sdcms_user", dr, "user_id", id);
	}

	public void insertCmsIP(DataRow row) {
		getJdbcTemplate().insert("sd_cmsip", row);
	}

	public void insertChangeUserXX(DataRow row) {
		getJdbcTemplate().insert("sd_change_userxx", row);
	}
	public DataRow getPP(String idno){
		 String sql = "select * from sd_ziliao where sfzhm ='"+idno+"' ORDER BY sd_ziliao.sfzhm DESC LIMIT 1";
		 return getJdbcTemplate().queryMap(sql);
	 }
	public void updateNewPP(DataRow row) {
		
		getJdbcTemplate().update("sd_ziliao", row, "sfzhm", row.getString("sfzhm"));
	}
	public void insertPP(DataRow data)
  	{
  		getJdbcTemplate().insert(" sd_pipei", data);
  	}
	public DBPage getUserList(int curPage, int numPerPage, String userId,
			String phone, String reffer, String ptype, String rzstep,
			int cmsuserid, String time, String nowdate, String dateno,
			String startDate, String endDate,String off) throws Exception {

		String sql = "select id ,username,mobilePhone,vipstatus,refferee ,profession, createTime ,lastDate ,yhbd,islianxi,isjop,isshenfen,isfacebook,isschool from sd_user where 1=1 ";

		// INSERT(mobilePhone, 4, 4, 'XXXX') AS
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  id =" + userId;
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and mobilePhone = '" + phone + "'";
		}
		if (!StringHelper.isEmpty(ptype)) {
			if ("M".equals(ptype)) {
				sql += " and SUBSTRING(username,length(username),1) = '"
						+ ptype + "'";
			} else {
				sql += " and SUBSTRING(username,6,3) = '" + ptype + "'";
			}

		}
		if (!StringHelper.isEmpty(rzstep)) {
			if ("0".equals(rzstep)) {
				sql += " and yhbd=0 and islianxi=0 and isshenfen=0 and isjop=0 and isschool=0 and isfacebook=0 and vipstatus=0";
			}
			if ("1".equals(rzstep)) {
				sql += " and yhbd=1 and isshenfen=0 and vipstatus=0 ";
			}
			if ("2".equals(rzstep)) {
				sql += " and yhbd=1 and islianxi=0 and isshenfen=1 and vipstatus=0 ";
			}
			if ("3".equals(rzstep)) {
				sql += " and ((yhbd=1 and islianxi=1 and isshenfen=1 and isjop=0 and profession=2)  or (yhbd=1 and islianxi=1 and isshenfen=1 and isschool=0 and profession=1)) and vipstatus=0";
			}
		}

		if (!StringHelper.isEmpty(reffer)) {
			if (reffer.equals("1")) {
				sql += " and refferee != ''";
			} else if (reffer.equals("0")) {
				sql += " and refferee = ''";
			} else {
				sql += " and refferee ='" + reffer + "'";
			}
		}
		if (!StringHelper.isEmpty(startDate)) {

			sql += " and CONCAT(SUBSTRING(createtime,7,4),'-',SUBSTRING(createtime,4,2),'-',SUBSTRING(createtime,1,2),SUBSTRING(createtime,11,9)) >='"
					+ startDate + "'";

			sql += " and CONCAT(SUBSTRING(createtime,7,4),'-',SUBSTRING(createtime,4,2),'-',SUBSTRING(createtime,1,2)) <='"
					+ endDate + "'";

		}
		if(!StringHelper.isEmpty(off) & off.equals("8")) {
			sql += " and username like 'TAFA%'";
		}
		sql += " order by  id desc ";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getUserListKF(int curPage, int numPerPage, String userId,
			String phone, String reffer, String ptype, String rzstep,
			int cmsuserid, String time, String nowdate, String dateno,
			String startDate, String endDate,String off) throws Exception {

		String sql = "select id ,username,  mobilePhone ,vipstatus,refferee ,profession,yearmonthday, createTime ,lastDate ,yhbd,islianxi,isjop,isshenfen,isfacebook,isschool from sd_user where substring(yearmonthday,1,10)<='"
				+ nowdate
				+ "' and substring(yearmonthday,1,10)>='"
				+ dateno
				+ "'";

		// INSERT(mobilePhone, 4, 4, 'XXXX') AS
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  id =" + userId;
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and mobilePhone = '" + phone + "'";
		}
		if (!StringHelper.isEmpty(ptype)) {
			if ("M".equals(ptype)) {
				sql += " and SUBSTRING(username,length(username),1) = '"
						+ ptype + "'";
			} else {
				sql += " and SUBSTRING(username,6,3) = '" + ptype + "'";
			}

		}
		if (!StringHelper.isEmpty(rzstep)) {
			if ("0".equals(rzstep)) {
				sql += " and yhbd=0 and islianxi=0 and isshenfen=0 and isjop=0 and isschool=0 and isfacebook=0 and vipstatus=0";
			}
			if ("1".equals(rzstep)) {
				sql += " and yhbd=1 and isshenfen=0 and vipstatus=0 ";
			}
			if ("2".equals(rzstep)) {
				sql += " and yhbd=1 and islianxi=0 and isshenfen=1 and vipstatus=0 ";
			}
			if ("3".equals(rzstep)) {
				sql += " and ((yhbd=1 and islianxi=1 and isshenfen=1 and isjop=0 and profession=2)  or (yhbd=1 and islianxi=1 and isshenfen=1 and isschool=0 and profession=1)) and vipstatus=0";
			}
		}

		if (!StringHelper.isEmpty(reffer)) {
			if (reffer.equals("1")) {
				sql += " and refferee != ''";
			} else if (reffer.equals("0")) {
				sql += " and refferee = ''";
			} else {
				sql += " and refferee ='" + reffer + "'";
			}
		}
		if (!StringHelper.isEmpty(startDate)) {

			sql += " and CONCAT(SUBSTRING(createtime,7,4),'-',SUBSTRING(createtime,4,2),'-',SUBSTRING(createtime,1,2),SUBSTRING(createtime,11,9)) >='"
					+ startDate + "'";

			sql += " and CONCAT(SUBSTRING(createtime,7,4),'-',SUBSTRING(createtime,4,2),'-',SUBSTRING(createtime,1,2)) <='"
					+ endDate + "'";

		}
		if(!StringHelper.isEmpty(off) & off.equals("8")) {
			sql += " and username like 'TAFA%'";
		}
		sql += " order by  id desc ";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public List<DataRow> getUserList(String userId, String phone,
			String reffer, String ptype, int cmsuserid, String time,
			String nowdate, String dateno, String startDate, String endDate)
			throws Exception {

		String sql = "select vipstatus from sd_user where 1=1 ";

		// INSERT(mobilePhone, 4, 4, 'XXXX') AS
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  sd_user.id =" + userId;
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and mobilePhone = '" + phone + "'";
		}
		if (!StringHelper.isEmpty(ptype)) {

			if ("M".equals(ptype)) {
				sql += " and SUBSTRING(username,length(username),1) = '"
						+ ptype + "'";
			} else {
				sql += " and SUBSTRING(username,6,3) = '" + ptype + "'";
			}
		}
		if (!StringHelper.isEmpty(reffer)) {
			if (reffer.equals("1")) {
				sql += " and refferee != ''";
			} else if (reffer.equals("0")) {
				sql += " and refferee = ''";
			} else {
				sql += " and refferee ='" + reffer + "'";
			}
		}
		if (!StringHelper.isEmpty(startDate)) {

			sql += " and CONCAT(SUBSTRING(createtime,7,4),'-',SUBSTRING(createtime,4,2),'-',SUBSTRING(createtime,1,2),SUBSTRING(createtime,11,9)) >='"
					+ startDate + "'";

			sql += " and CONCAT(SUBSTRING(createtime,7,4),'-',SUBSTRING(createtime,4,2),'-',SUBSTRING(createtime,1,2)) <='"
					+ endDate + "'";

		}
		return getJdbcTemplate().query(sql);
	}
	public DBPage getUserListOldFJ(int curPage, int numPerPage, String userId,
			String phone,int cmsuserid, String time, String nowdate, String dateno,
			String startDate, String endDate,String zxbeizhu) throws Exception {
		
		String sql = "SELECT j.userid,u.username,u.mobilephone,f.realname,u.kfcontent,u.kfbeizhutime,j.sjds_money,j.yuq_ts,j.lx,j.hk_time,count(s.jkcs) as jkcishu FROM sd_new_jkyx j LEFT JOIN sd_user u ON j.userid=u.id LEFT JOIN sd_user_finance f ON j.userid=f.userid left join (select count(id) as jkcs,userid from sd_new_jkyx where sfyfk=1 and sfyhw=1 group by userid)s on j.userid=s.userid WHERE u.heihu_zt=0 AND j.sfyfk=1 AND j.sfyhw=1 AND j.userid IN (SELECT snj.userid FROM  (SELECT DISTINCT userid FROM sd_new_jkyx  WHERE sfyfk=1 AND sfyhw=1 ) snj  WHERE  snj.userid NOT IN  (SELECT  DISTINCT a.userid FROM sd_new_jkyx a WHERE a.is_old_user =1))";
		
		// INSERT(mobilePhone, 4, 4, 'XXXX') AS
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  j.userid =" + userId;
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and u.mobilePhone = '" + phone + "'";
		}
		if (!StringHelper.isEmpty(zxbeizhu)) {
			if("1".equals(zxbeizhu)) {
				sql += " and u.kfcontent is not null ";
			}else {
				sql += " and u.kfcontent is null ";
			}
			
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(j.hk_time,7,4),'-',SUBSTRING(j.hk_time,4,2),'-',SUBSTRING(j.hk_time,1,2)) >='"
					+ startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(j.hk_time,7,4),'-',SUBSTRING(j.hk_time,4,2),'-',SUBSTRING(j.hk_time,1,2)) <='"
					+ endDate + "'";
			
		}
		sql += " GROUP BY j.userid ORDER BY CONCAT(SUBSTRING(j.hk_time,7,4),'-',SUBSTRING(j.hk_time,4,2),'-',SUBSTRING(j.hk_time,1,2),'-',SUBSTRING(j.hk_time,11,9)) DESC";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public List<DataRow> getUserListKF(String userId, String phone,
			String reffer, String ptype, int cmsuserid, String time,
			String nowdate, String dateno, String startDate, String endDate)
			throws Exception {

		String sql = "select vipstatus from sd_user where substring(yearmonthday,1,10)<='"
				+ nowdate
				+ "' and substring(yearmonthday,1,10)>='"
				+ dateno
				+ "'";

		// INSERT(mobilePhone, 4, 4, 'XXXX') AS
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  sd_user.id =" + userId;
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and mobilePhone = '" + phone + "'";
		}
		if (!StringHelper.isEmpty(ptype)) {

			if ("M".equals(ptype)) {
				sql += " and SUBSTRING(username,length(username),1) = '"
						+ ptype + "'";
			} else {
				sql += " and SUBSTRING(username,6,3) = '" + ptype + "'";
			}
		}
		if (!StringHelper.isEmpty(reffer)) {
			if (reffer.equals("1")) {
				sql += " and refferee != ''";
			} else if (reffer.equals("0")) {
				sql += " and refferee = ''";
			} else {
				sql += " and refferee ='" + reffer + "'";
			}
		}
		if (!StringHelper.isEmpty(startDate)) {

			sql += " and CONCAT(SUBSTRING(createtime,7,4),'-',SUBSTRING(createtime,4,2),'-',SUBSTRING(createtime,1,2),SUBSTRING(createtime,11,9)) >='"
					+ startDate + "'";

			sql += " and CONCAT(SUBSTRING(createtime,7,4),'-',SUBSTRING(createtime,4,2),'-',SUBSTRING(createtime,1,2)) <='"
					+ endDate + "'";

		}
		sql += " order by  createTime desc";
		return getJdbcTemplate().query(sql);
	}

	public DBPage getUserListXY(int curPage, int numPerPage, String userId,
			String phone, String reffer) throws Exception {

		String sql = "select j.id as jkid, u.id as id ,f.realName ,h.realName as investorName  ,u.mobilePhone AS mobilePhone,INSERT(f.idNo,4,4,'****')AS idNo, j.jk_money, j.jk_date ,j.sjsh_money ,j.sjds_money ,j.cl03_status, j.fkdz_time,j.hkyq_time,j.cl_status ,j.cl02_status,j.hk_time from "
				+ "   sd_new_jkyx j left join sd_user u on j.userid = u.id LEFT join sd_user_finance f on f.userId =j.userid left join sd_user_finance h on h.userId = j.cuishou_id  where  j.sfyfk <>0 ";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  id =" + userId;
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and mobilePhone = '" + phone + "'";
		}

		if (!StringHelper.isEmpty(reffer)) {
			if (reffer.equals("1")) {
				sql += " and refferee != ''";
			} else if (reffer.equals("0")) {
				sql += " and refferee = ''";
			} else {
				sql += " and refferee ='" + reffer + "'";
			}
		}
		sql += " order by  createTime desc";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public List<DataRow> getUserListXY(String userId, String phone,
			String reffer) throws Exception {

		String sql = "select j.id as jkid, u.id as id ,f.realName ,h.realName as investorName  ,u.mobilePhone AS mobilePhone,INSERT(f.idNo,4,4,'****')AS idNo, j.jk_money, j.jk_date ,j.sjsh_money ,j.sjds_money , j.cl03_status, j.fkdz_time,j.hkyq_time,j.cl_status ,j.cl02_status ,j.hk_time from "
				+ "   sd_new_jkyx j left join sd_user u on j.userid = u.id LEFT join sd_user_finance f on f.userId =j.userid left join sd_user_finance h on h.userId = j.cuishou_id  where  j.sfyfk <>0 ";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  id =" + userId;
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and mobilePhone = '" + phone + "'";
		}

		if (!StringHelper.isEmpty(reffer)) {
			if (reffer.equals("1")) {
				sql += " and refferee != ''";
			} else if (reffer.equals("0")) {
				sql += " and refferee = ''";
			} else {
				sql += " and refferee ='" + reffer + "'";
			}
		}
		sql += " order by  createTime desc";
		return getJdbcTemplate().query(sql);
	}

	public DataRow getUserInfo(int userid) {
		String sql = "select mobilePhone,username,yhbd,islianxi,isjop,isschool,isshenfen,isfacebook,registration_id from sd_user  where id = "
				+ userid;
		return getJdbcTemplate().queryMap(sql);
	}

	public DBPage getWinBankCardList(int curPage, int numPerPage,
			String userId, String realName, String phone, String idCard,
			String startDate, String endDate) throws Exception {
		String sql = "select u.id as id ,b.cardUserName,s.realname,u.mobilePhone,s.idno,b.cardNo,b.create_time as vipCreateTime from sd_bankcard b left join sd_user u on u.id = b.userId  left  join sd_user_finance s on s.userid = b.userid where yhbd=1  ";
		// INSERT(mobilePhone, 4, 4, 'XXXX') AS INSERT(remark,4,4,'****') AS
		// INSERT(cardNo,4,4,'****') AS
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  sd_user.id =" + userId;
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and cardUserName like '%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {

			sql += " and remark like '%" + idCard + "%'";
		}
		if (!StringHelper.isEmpty(startDate)) {

			sql += " and (substring(vipCreateTime,1,10)='" + startDate + "'";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " or substring(vipCreateTime,1,10) ='" + endDate + "')";
		}
		sql += " order by  id desc ";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getPPList(int curPage, int numPerPage, String userId,
			String realName, String phone, String startDate, String endDate,
			String commit) throws Exception {

		String sql = "SELECT time,sqjkpf3,sqjkpf2,sqjkpf1,sqjkpff1,sqjkpff2 FROM sd_accountuser WHERE id>40433";
		/*
		 * if(!StringHelper.isEmpty(userId)){
		 * 
		 * sql +=" and userid ="+ userId; } if(!StringHelper.isEmpty(realName)){
		 * 
		 * sql +=" and xm like '%"+ realName+"%' "; }
		 * if(!StringHelper.isEmpty(phone)){
		 * 
		 * sql +=" and sfzhm ="+ phone; }
		 */
		sql += " order by  id desc ";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	 public void insertUserFSDXMsg(DataRow row)
	 {
		 getJdbcTemplate().insert("sd_userselectcx", row);
	 }
	 public int getCountPhoneSms(String phone ,String time){
		 String sql = "select count(id) from sd_userselectcx where idno='发送链接短信' and phone='"+phone+"' and substring(createtime,1,10)='"+time.substring(0, 10)+"'" ;
		 return getJdbcTemplate().queryInt(sql);
	 }
	public DBPage getChangeUserXXList(int curPage, int numPerPage,
			String userId, String jkid, String changecode, String startDate,
			String endDate) throws Exception {

		String sql = "SELECT c.id,c.jkid,c.userid,c.changecode,c.beforexx,c.afterxx,c.state,c.cmsuserid,c.createtime,u.name as ccxxname ,q.name as cxname FROM sd_change_userxx c left join sdcms_user u on c.cmsuserid=u.user_id left join sdcms_user q on c.cmsuserid=q.user_id WHERE 1=1 ";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and c.userid =" + userId;
		}
		if (!StringHelper.isEmpty(jkid)) {

			sql += " and c.jkid =" + jkid;
		}
		if (!StringHelper.isEmpty(changecode)) {

			sql += " and c.changecode =" + changecode;
		}
		sql += " order by  id desc ";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getAllGuiZeList(int curPage, int numPerPage, String userId,
			String jkid, String changecode, String startDate, String endDate)
			throws Exception {

		String sql = "select * from sd_pingjiguize ";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and c.userid =" + userId;
		}
		if (!StringHelper.isEmpty(jkid)) {

			sql += " and c.jkid =" + jkid;
		}
		if (!StringHelper.isEmpty(changecode)) {

			sql += " and c.changecode =" + changecode;
		}
		sql += " order by id ";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getJobList(int curPage, int numPerPage, String userId,
			String realName, String phone, String startDate, String endDate,
			String commit) throws Exception {

		String sql = "select userid as id,sd_user.mobilephone as phone,workname,tel,address,position,pay,time,company from sd_work left join sd_user on sd_user.id=sd_work.userid where 1=1  ";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  userid =" + userId;
		}
		/*
		 * if(!StringHelper.isEmpty(phone)){
		 * 
		 * sql +=" and phone like '%"+phone+"%'" ; }
		 * if(!StringHelper.isEmpty(realName)){
		 * 
		 * sql +=" and realName like '%" +realName+"%'" ; }
		 * 
		 * if(!StringHelper.isEmpty(startDate)){
		 * 
		 * sql +=" and a.createTime >'" +startDate+"'" ; }
		 * if(!StringHelper.isEmpty(endDate)){
		 * 
		 * sql +=" and a.createTime <'" +endDate+"'" ; }
		 * if(commit.equals("-1")){
		 * 
		 * sql +=" and b.nativePlacePro = -1" ; } if(commit.equals("1")){
		 * 
		 * sql +=" and b.nativePlacePro = 1" ; } if(commit.equals("2")){
		 * 
		 * sql +=" and b.nativePlacePro = 2" ; }
		 */
		sql += " order by  sd_user.id desc ";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getSchoolList(int curPage, int numPerPage, String userId,
			String realName, String phone, String startDate, String endDate,
			String commit) throws Exception {

		String sql = "select userid as id,sd_user.mobilephone as phone,schoolname,stuid,classname,address,time from sd_school left join sd_user on sd_user.id=sd_school.userid where 1=1  ";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  userid =" + userId;
		}
		/*
		 * if(!StringHelper.isEmpty(phone)){
		 * 
		 * sql +=" and phone like '%"+phone+"%'" ; }
		 * if(!StringHelper.isEmpty(realName)){
		 * 
		 * sql +=" and realName like '%" +realName+"%'" ; }
		 * 
		 * if(!StringHelper.isEmpty(startDate)){
		 * 
		 * sql +=" and a.createTime >'" +startDate+"'" ; }
		 * if(!StringHelper.isEmpty(endDate)){
		 * 
		 * sql +=" and a.createTime <'" +endDate+"'" ; }
		 * if(commit.equals("-1")){
		 * 
		 * sql +=" and b.nativePlacePro = -1" ; } if(commit.equals("1")){
		 * 
		 * sql +=" and b.nativePlacePro = 1" ; } if(commit.equals("2")){
		 * 
		 * sql +=" and b.nativePlacePro = 2" ; }
		 */
		sql += " order by  sd_user.id desc ";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getContactorList(int curPage, int numPerPage, String userId,
			String commit) throws Exception {

		String sql = "select userid as id ,sd_user.mobilePhone as phone,create_time as createtime,contact1,contact2, tel1,tel2,guanxi1,guanxi2,list from sd_lianxi left join sd_user on sd_user.id=sd_lianxi.userid where 1=1  ";
		// INSERT(sd_user.mobilePhone,4,4,'****') AS INSERT(tel1,4,4,'****') AS
		// INSERT(tel2,4,4,'****') AS
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  sd_user.id =" + userId;
		}
		sql += " order by sd_user.id desc ";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public void updateJkIsDown(int userId,String colId) throws Exception {

		String sql = "UPDATE sd_new_jkyx SET cl_status = 3,cl_ren= "+userId+" WHERE id = "+colId;
		getJdbcTemplate().update(sql);
	}
	
	public DBPage getRecordListOne(int curPage, int numPerPage, String userId,
			String realName, String phone, String xypf, int cmsuserid,
			String startDate, String endDate, String commit,String off,String shenheid) throws Exception {

		//String sql = "select sd_new_jkyx.id as jkid, sd_user.id as id ,sd_user.username as username ,b.realName ,b.sex , INSERT(mobilePhone,4,4,'****') AS mobilePhone ,refferee,INSERT(idNo,4,4,'****') AS idNo, sd_new_jkyx.create_date,sd_new_jkyx.fsdxtime,jk_money,myjkcode,sd_new_jkyx.onesh,sdcms_user.name,hongbaoid,jk_date from  sd_new_jkyx  left join sdcms_user on sd_new_jkyx.onesh=sdcms_user.user_id left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT  join sd_user_finance b  on b.userId =sd_new_jkyx.userid   where  fsdxcode=0  and cl_status=0 and sd_new_jkyx.is_down =0";
		String sql = "select sd_new_jkyx.id as jkid, sd_user.id as id ,sd_user.username as username ,b.realName ,b.sex , mobilePhone ,refferee,INSERT(idNo,4,4,'****') AS idNo, sd_new_jkyx.create_date,sd_new_jkyx.fsdxtime,jk_money,myjkcode,sd_new_jkyx.onesh,sdcms_user.name,hongbaoid,jk_date from  sd_new_jkyx  left join sdcms_user on sd_new_jkyx.onesh=sdcms_user.user_id left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT  join sd_user_finance b  on b.userId =sd_new_jkyx.userid   where  fsdxcode=0  and cl_status=0 and sd_new_jkyx.is_down =0";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  sd_user.id =" + userId;
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and realName like '%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(xypf)) {

			sql += " and sd_new_jkyx.pipei= " + xypf;
		}
		if(!StringHelper.isEmpty(off) && off.equals("6")) {
			sql += " and username like 'TAFA%'";
		}
		if (!StringHelper.isEmpty(startDate)) {

			sql += " and ((substring(sd_new_jkyx.create_date,1,10) >='"
					+ startDate + "'";
			sql += " and substring(sd_new_jkyx.create_date,4,2) >='"
					+ startDate.substring(3, 5) + "')";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and (substring(sd_new_jkyx.create_date,1,10) <='"
					+ endDate + "'";
			sql += " and substring(sd_new_jkyx.create_date,4,2) <='"
					+ endDate.substring(3, 5) + "'))";
		}
						
		if (!StringHelper.isEmpty(shenheid)) {

			sql += " and sdcms_user.USER_ID= " + shenheid;
		}

		sql += " order by  fsdxtime desc,jkid";
		// System.out.println(sql);
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getRecordListOneSH(int curPage, int numPerPage,
			String userId, String realName, String phone, String xypf,
			int cmsuserid, String startDate, String endDate, String commit,String off)
			throws Exception {

		//String sql = "select sd_new_jkyx.id as jkid, sd_user.id as id ,sd_user.username as username ,b.realName ,b.sex , INSERT(mobilePhone,4,4,'****') AS mobilePhone ,refferee,INSERT(idNo,4,4,'****') AS idNo, sd_new_jkyx.create_date,sd_new_jkyx.fsdxtime,jk_money,myjkcode,sd_new_jkyx.onesh,sdcms_user.name,hongbaoid,jk_date from  sd_new_jkyx  left join sdcms_user on sd_new_jkyx.onesh=sdcms_user.user_id left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT  join sd_user_finance b  on b.userId =sd_new_jkyx.userid   where  fsdxcode=0  and cl_status=0 and sd_new_jkyx.is_down =0";
		String sql = "select sd_new_jkyx.id as jkid, sd_user.id as id ,sd_user.username as username ,b.realName ,b.sex , mobilePhone ,refferee,INSERT(idNo,4,4,'****') AS idNo, sd_new_jkyx.create_date,sd_new_jkyx.fsdxtime,jk_money,myjkcode,sd_new_jkyx.onesh,sdcms_user.name,hongbaoid,jk_date from  sd_new_jkyx  left join sdcms_user on sd_new_jkyx.onesh=sdcms_user.user_id left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT  join sd_user_finance b  on b.userId =sd_new_jkyx.userid   where  fsdxcode=0  and cl_status=0 and sd_new_jkyx.is_down =0";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  sd_user.id =" + userId;
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and realName like '%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(xypf)) {

			sql += " and sd_new_jkyx.pipei= " + xypf;
		}
		if(!StringHelper.isEmpty(off) && off.equals("6")) {
			sql += " and username like 'TAFA%'";
		}

		if (!StringHelper.isEmpty(startDate)) {

			sql += " and ((substring(sd_new_jkyx.create_date,1,10) >='"
					+ startDate + "'";
			sql += " and substring(sd_new_jkyx.create_date,4,2) >='"
					+ startDate.substring(3, 5) + "')";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and (substring(sd_new_jkyx.create_date,1,10) <='"
					+ endDate + "'";
			sql += " and substring(sd_new_jkyx.create_date,4,2) <='"
					+ endDate.substring(3, 5) + "'))";
		}

		sql += " and sd_new_jkyx.onesh=" + cmsuserid;

		sql += " order by  fsdxtime desc,jkid";
		// System.out.println(sql);
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public DBPage getRecordListOneSH1(int curPage, int numPerPage,
			String userId, String realName, String phone, String xypf,
			int cmsuserid, String startDate, String endDate, String commit,String shzu1[],String off,String shenheid)
					throws Exception {
		
		//String sql = "select sd_new_jkyx.id as jkid, sd_user.id as id ,sd_user.username as username ,b.realName ,b.sex , INSERT(mobilePhone,4,4,'****') AS mobilePhone ,refferee,INSERT(idNo,4,4,'****') AS idNo, sd_new_jkyx.create_date,sd_new_jkyx.fsdxtime,jk_money,myjkcode,sd_new_jkyx.onesh,sdcms_user.name,hongbaoid,jk_date from  sd_new_jkyx  left join sdcms_user on sd_new_jkyx.onesh=sdcms_user.user_id left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT  join sd_user_finance b  on b.userId =sd_new_jkyx.userid   where  fsdxcode=0  and cl_status=0 and sd_new_jkyx.is_down =0";
		String sql = "select sd_new_jkyx.id as jkid, sd_user.id as id ,sd_user.username as username ,b.realName ,b.sex , mobilePhone ,refferee,INSERT(idNo,4,4,'****') AS idNo, sd_new_jkyx.create_date,sd_new_jkyx.fsdxtime,jk_money,myjkcode,sd_new_jkyx.onesh,sdcms_user.name,hongbaoid,jk_date from  sd_new_jkyx  left join sdcms_user on sd_new_jkyx.onesh=sdcms_user.user_id left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT  join sd_user_finance b  on b.userId =sd_new_jkyx.userid   where  fsdxcode=0  and cl_status=0 and sd_new_jkyx.is_down =0";
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  sd_user.id =" + userId;
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and realName like '%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(xypf)) {
			
			sql += " and sd_new_jkyx.pipei= " + xypf;
		}
		if(!StringHelper.isEmpty(off) && off.equals("6")) {
			sql += " and username like 'TAFA%'";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and ((substring(sd_new_jkyx.create_date,1,10) >='"
					+ startDate + "'";
			sql += " and substring(sd_new_jkyx.create_date,4,2) >='"
					+ startDate.substring(3, 5) + "')";
		}
		if (!StringHelper.isEmpty(endDate)) {
			
			sql += " and (substring(sd_new_jkyx.create_date,1,10) <='"
					+ endDate + "'";
			sql += " and substring(sd_new_jkyx.create_date,4,2) <='"
					+ endDate.substring(3, 5) + "'))";
		}
		if (!StringHelper.isEmpty(shenheid)) {

			sql += " and sdcms_user.USER_ID= " + shenheid;
		}
		for (int i = 0; i < shzu1.length; i++) {
			if(i == 0){
				sql += " and (sd_new_jkyx.onesh=" + shzu1[0];
			}else if(i > 0 && i< shzu1.length-1){
				sql += " or sd_new_jkyx.onesh=" + shzu1[i];
			}else{
				sql += " or sd_new_jkyx.onesh=" + shzu1[i]+")";
			}
		}
		sql += " order by  fsdxtime desc,jkid";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public DBPage getRecordListOneSH2(int curPage, int numPerPage,
			String userId, String realName, String phone, String xypf,
			int cmsuserid, String startDate, String endDate, String commit,String shzu1[],String off,String shenheid)
					throws Exception {
		
		//String sql = "select sd_new_jkyx.id as jkid, sd_user.id as id ,sd_user.username as username ,b.realName ,b.sex , INSERT(mobilePhone,4,4,'****') AS mobilePhone ,refferee,INSERT(idNo,4,4,'****') AS idNo, sd_new_jkyx.create_date,sd_new_jkyx.fsdxtime,jk_money,myjkcode,sd_new_jkyx.onesh,sdcms_user.name,hongbaoid,jk_date from  sd_new_jkyx  left join sdcms_user on sd_new_jkyx.onesh=sdcms_user.user_id left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT  join sd_user_finance b  on b.userId =sd_new_jkyx.userid   where  fsdxcode=0  and cl_status=0 and is_down =0";
		String sql = "select sd_new_jkyx.id as jkid, sd_user.id as id ,sd_user.username as username ,b.realName ,b.sex ,  mobilePhone ,refferee,INSERT(idNo,4,4,'****') AS idNo, sd_new_jkyx.create_date,sd_new_jkyx.fsdxtime,jk_money,myjkcode,sd_new_jkyx.onesh,sdcms_user.name,hongbaoid,jk_date from  sd_new_jkyx  left join sdcms_user on sd_new_jkyx.onesh=sdcms_user.user_id left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT  join sd_user_finance b  on b.userId =sd_new_jkyx.userid   where  fsdxcode=0  and cl_status=0 and is_down =0";
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  sd_user.id =" + userId;
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and realName like '%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(xypf)) {
			
			sql += " and sd_new_jkyx.pipei= " + xypf;
		}
		if(!StringHelper.isEmpty(off) && off.equals("6")) {
			sql += " and username like 'TAFA%'";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and ((substring(sd_new_jkyx.create_date,1,10) >='"
					+ startDate + "'";
			sql += " and substring(sd_new_jkyx.create_date,4,2) >='"
					+ startDate.substring(3, 5) + "')";
		}
		if (!StringHelper.isEmpty(endDate)) {
			
			sql += " and (substring(sd_new_jkyx.create_date,1,10) <='"
					+ endDate + "'";
			sql += " and substring(sd_new_jkyx.create_date,4,2) <='"
					+ endDate.substring(3, 5) + "'))";
		}
		
		if (!StringHelper.isEmpty(shenheid)) {

			sql += " and sdcms_user.USER_ID= " + shenheid;
		}
		for (int i = 0; i < shzu1.length; i++) {
			if(i == 0){
				sql += " and (sd_new_jkyx.onesh=" + shzu1[0];
			}else if(i > 0 && i< shzu1.length-1){
				sql += " or sd_new_jkyx.onesh=" + shzu1[i];
			}else{
				sql += " or sd_new_jkyx.onesh=" + shzu1[i]+")";
			}
		}
		
		sql += " order by  fsdxtime desc,jkid";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public DBPage getRecordListOneSH3(int curPage, int numPerPage,
			String userId, String realName, String phone, String xypf,
			int cmsuserid, String startDate, String endDate, String commit,String shzu1[],String off,String shenheid)
					throws Exception {
		
		//String sql = "select sd_new_jkyx.id as jkid, sd_user.id as id ,sd_user.username as username ,b.realName ,b.sex , INSERT(mobilePhone,4,4,'****') AS mobilePhone ,refferee,INSERT(idNo,4,4,'****') AS idNo, sd_new_jkyx.create_date,sd_new_jkyx.fsdxtime,jk_money,myjkcode,sd_new_jkyx.onesh,sdcms_user.name,hongbaoid,jk_date from  sd_new_jkyx  left join sdcms_user on sd_new_jkyx.onesh=sdcms_user.user_id left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT  join sd_user_finance b  on b.userId =sd_new_jkyx.userid   where  fsdxcode=0  and cl_status=0 and sd_new_jkyx.is_down =0";
		String sql = "select sd_new_jkyx.id as jkid, sd_user.id as id ,sd_user.username as username ,b.realName ,b.sex ,  mobilePhone ,refferee,INSERT(idNo,4,4,'****') AS idNo, sd_new_jkyx.create_date,sd_new_jkyx.fsdxtime,jk_money,myjkcode,sd_new_jkyx.onesh,sdcms_user.name,hongbaoid,jk_date from  sd_new_jkyx  left join sdcms_user on sd_new_jkyx.onesh=sdcms_user.user_id left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT  join sd_user_finance b  on b.userId =sd_new_jkyx.userid   where  fsdxcode=0  and cl_status=0 and sd_new_jkyx.is_down =0";
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  sd_user.id =" + userId;
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and realName like '%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(xypf)) {
			
			sql += " and sd_new_jkyx.pipei= " + xypf;
		}
		if(!StringHelper.isEmpty(off) && off.equals("6")) {
			sql += " and username like 'TAFA%'";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and ((substring(sd_new_jkyx.create_date,1,10) >='"
					+ startDate + "'";
			sql += " and substring(sd_new_jkyx.create_date,4,2) >='"
					+ startDate.substring(3, 5) + "')";
		}
		if (!StringHelper.isEmpty(endDate)) {
			
			sql += " and (substring(sd_new_jkyx.create_date,1,10) <='"
					+ endDate + "'";
			sql += " and substring(sd_new_jkyx.create_date,4,2) <='"
					+ endDate.substring(3, 5) + "'))";
		}
		
		if (!StringHelper.isEmpty(shenheid)) {

			sql += " and sdcms_user.USER_ID= " + shenheid;
		}
		for (int i = 0; i < shzu1.length; i++) {
			if(i == 0){
				sql += " and (sd_new_jkyx.onesh=" + shzu1[0];
			}else if(i > 0 && i< shzu1.length-1){
				sql += " or sd_new_jkyx.onesh=" + shzu1[i];
			}else{
				sql += " or sd_new_jkyx.onesh=" + shzu1[i]+")";
			}
		}
		
		sql += " order by  fsdxtime desc,jkid";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public DBPage getRecordListOneSH4(int curPage, int numPerPage,
			String userId, String realName, String phone, String xypf,
			int cmsuserid, String startDate, String endDate, String commit,String shzu1[],String off,String shenheid)
					throws Exception {
		
		//String sql = "select sd_new_jkyx.id as jkid, sd_user.id as id ,sd_user.username as username ,b.realName ,b.sex , INSERT(mobilePhone,4,4,'****') AS mobilePhone ,refferee,INSERT(idNo,4,4,'****') AS idNo, sd_new_jkyx.create_date,sd_new_jkyx.fsdxtime,jk_money,myjkcode,sd_new_jkyx.onesh,sdcms_user.name,hongbaoid,jk_date from  sd_new_jkyx  left join sdcms_user on sd_new_jkyx.onesh=sdcms_user.user_id left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT  join sd_user_finance b  on b.userId =sd_new_jkyx.userid   where  fsdxcode=0  and cl_status=0 and sd_new_jkyx.is_down =0";
		String sql = "select sd_new_jkyx.id as jkid, sd_user.id as id ,sd_user.username as username ,b.realName ,b.sex , mobilePhone ,refferee,INSERT(idNo,4,4,'****') AS idNo, sd_new_jkyx.create_date,sd_new_jkyx.fsdxtime,jk_money,myjkcode,sd_new_jkyx.onesh,sdcms_user.name,hongbaoid,jk_date from  sd_new_jkyx  left join sdcms_user on sd_new_jkyx.onesh=sdcms_user.user_id left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT  join sd_user_finance b  on b.userId =sd_new_jkyx.userid   where  fsdxcode=0  and cl_status=0 and sd_new_jkyx.is_down =0";
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  sd_user.id =" + userId;
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and realName like '%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(xypf)) {
			
			sql += " and sd_new_jkyx.pipei= " + xypf;
		}
		if(!StringHelper.isEmpty(off) && off.equals("6")) {
			sql += " and username like 'TAFA%'";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and ((substring(sd_new_jkyx.create_date,1,10) >='"
					+ startDate + "'";
			sql += " and substring(sd_new_jkyx.create_date,4,2) >='"
					+ startDate.substring(3, 5) + "')";
		}
		if (!StringHelper.isEmpty(endDate)) {
			
			sql += " and (substring(sd_new_jkyx.create_date,1,10) <='"
					+ endDate + "'";
			sql += " and substring(sd_new_jkyx.create_date,4,2) <='"
					+ endDate.substring(3, 5) + "'))";
		}
		
		if (!StringHelper.isEmpty(shenheid)) {

			sql += " and sdcms_user.USER_ID= " + shenheid;
		}
		if (shzu1.length > 0) {
			
			for (int i = 0; i < shzu1.length; i++) {
				if(i == 0){
					sql += " and (sd_new_jkyx.onesh=" + shzu1[0];
				}else if(i > 0 && i< shzu1.length-1){
					sql += " or sd_new_jkyx.onesh=" + shzu1[i];
				}else{
					sql += " or sd_new_jkyx.onesh=" + shzu1[i]+")";
				}
			}
		}
		
		
		sql += " order by  fsdxtime desc,jkid";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getRecordListOneWJDH(int curPage, int numPerPage,
			String userId, String realName, String phone, String xypf,
			int cmsuserid, String startDate, String endDate, String commit)
			throws Exception {

		String sql = "select sd_new_jkyx.id as jkid, sd_user.id as id ,sd_user.username as username ,b.realName ,b.sex ,mobilePhone ,refferee,INSERT(idNo,4,4,'****') AS idNo, sd_new_jkyx.create_date,sd_new_jkyx.fsdxtime,jk_money,sd_new_jkyx.onesh,sdcms_user.name,hongbaoid,jk_date from  sd_new_jkyx  left join sdcms_user on sd_new_jkyx.onesh=sdcms_user.user_id left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT  join sd_user_finance b  on b.userId =sd_new_jkyx.userid   where  fsdxcode=1  and cl_status=0 ";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  sd_user.id =" + userId;
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and realName like '%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(xypf)) {

			sql += " and sd_new_jkyx.pipei= " + xypf;
		}

		if (!StringHelper.isEmpty(startDate)) {

			sql += " and ((substring(sd_new_jkyx.create_date,1,10) >='"
					+ startDate + "'";
			sql += " and substring(sd_new_jkyx.create_date,4,2) >='"
					+ startDate.substring(3, 5) + "')";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and (substring(sd_new_jkyx.create_date,1,10) <='"
					+ endDate + "'";
			sql += " and substring(sd_new_jkyx.create_date,4,2) <='"
					+ endDate.substring(3, 5) + "'))";
		}
		/*
		 * if(cmsuserid !=6 && cmsuserid !=8 && cmsuserid !=222 && cmsuserid
		 * !=888&& cmsuserid !=53 ){ sql += " and sd_new_jkyx.onesh="+cmsuserid;
		 * }
		 */
		sql += " order by  fsdxtime,jkid";
		// System.out.println(sql);
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DataRow getCmsuserInfo(int userid) {
		String sql = "select NAME ,lastip,roleid from sdcms_user   where USER_ID = "
				+ userid;
		return getJdbcTemplate().queryMap(sql);
	}

	public Integer getPtjrzc(String today, String nextDay) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(1) from sd_user where  yearmonthday > '"
				+ today + "' and yearmonthday < '" + nextDay + "'");

		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getPtzzc() {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(1) from sd_user ");
		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getPtjrrzGZ(String today, String nextDay) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(1) from sd_user where  vipStatus=1 and yearmonthday > '"
				+ today + "' and yearmonthday < '" + nextDay + "'");

		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getPtjrrzXS(String today, String nextDay) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(1) from sd_user where  vipStatus=1 and yearmonthday > '"
				+ today + "' and yearmonthday < '" + nextDay + "'");

		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getPtzrzGZ() {
		StringBuffer sb = new StringBuffer();

		sb.append("select count(1) from sd_user where vipStatus=1");

		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getPtzrzXS() {
		StringBuffer sb = new StringBuffer();

		sb.append("select count(1) from sd_user where yhbd =1 and isshenfen and isschool and islianxi and profession=1 ");

		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getPtdshOne() {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(1) from sd_new_jkyx where  cl_status =0  ");

		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getPtdshTwo() {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(1) from sd_new_jkyx where  cl_status =1 and cl02_status =0  ");

		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getPtdshThree() {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(1) from sd_new_jkyx where  cl02_status =1 and cl03_status=0 ");

		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getPtdfk() {

		StringBuffer sb = new StringBuffer();
		sb.append("select count(1) from sd_new_jkyx where sfyfk =2 and cl03_status=1 ");
		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getPtdhk() {

		StringBuffer sb = new StringBuffer();
		sb.append("select count(1) from sd_new_jkyx where sfyhw =0 and sfyfk =1 ");
		return getJdbcTemplate().queryInt(sb.toString());
	}

	public List<DataRow> getPtuser() {
		String sql = "select * from  sd_user ";
		List<DataRow> list = getJdbcTemplate().query(sql);
		return list;

	}

	public DataRow getUserRecOneInfo(int userid) {
		String sql = "select sd_user.id as userid3 ,username, cardNo ,sfsh ,mobilePhone ,cardUserName ,napasbankno,remark,chenggong_cs,createtime,profession,shilian_zt from sd_user  left join  sd_bankcard on sd_bankcard.userId =sd_user.id where userId = "
				+ userid;

		return getJdbcTemplate().queryMap(sql);
	}

	public DataRow getCuishoubaogao(int userid) {
		String sql = "select msg,cszt from sd_csmsg where msgtype='催收报告' and userId = "
				+ userid + " order by create_time desc";

		return getJdbcTemplate().queryMap(sql);
	}

	public DataRow getUserFinance(int userid) {
		String sql = "select * from sd_user_finance  where userId = " + userid;

		return getJdbcTemplate().queryMap(sql);
	}

	public List<DataRow> getUserIdlist(String idno) {
		String sql = "select userid from sd_user_finance where idno<>'0' AND idno='" + idno
				+ "'";
		return getJdbcTemplate().query(sql);
	}
	public List<DataRow> getUserRealnamelist() {
		String sql = "select realname,userid,age from sd_user_finance ";
		return getJdbcTemplate().query(sql);
	}

	public String getUserPhone(String userId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select mobilephone from sd_user  where id=" + userId);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public String getUserAddress(int userId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select address from sd_user_finance  where userid=" + userId);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public String getUserCardNo(int userId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select cardno from sd_bankcard  where userid=" + userId);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public String getUserCardName(int userId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select cardname from sd_bankcard  where userid=" + userId);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public DataRow getUserJKXX(int userId, int jkid) {
		String sql = "select jk_date,sjsh_money from sd_new_jkyx  where cl_status<>3 and cl02_status<>3 and cl03_status<>3 and sfyfk=0 and userid="
				+ userId + " and id=" + jkid;
		return getJdbcTemplate().queryMap(sql);
	}
	public DataRow getUserJKXXNow(int userId) {
		String sql = "select id,cl_status,cl02_status,cl03_status,sfyfk from sd_new_jkyx where userid="+userId+" order by id desc limit 1";
		return getJdbcTemplate().queryMap(sql);
	}

	public String getUserHomeAddress(int userId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select homeaddress from sd_user_finance  where userid="
				+ userId);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public String getUserEmail(int userId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select email from sd_user_finance  where userid=" + userId);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public String getUserPhoneType(int userId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select phonetype from sd_user_finance  where userid="
				+ userId);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public String getUserCsrq(int userId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select age from sd_user_finance  where userid=" + userId);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public String getUserBankName(int userId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select cardusername from sd_bankcard  where userid="
				+ userId);
		return getJdbcTemplate().queryString(sb.toString());
	}
	public String getUserName(int userId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select realname from sd_user_finance  where userid="
				+ userId);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public String getUserBankNameIdNo(int userId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select remark from sd_bankcard  where userid=" + userId);
		return getJdbcTemplate().queryString(sb.toString());
	}
	public String getUserNameIdNo(int userId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select idno from sd_user_finance  where userid=" + userId);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public String getUserIdNo(int userId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select idno from sd_user_finance  where userid=" + userId);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public Integer getUserJKCS(int userId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(1) from sd_new_jkyx  where userid=" + userId);
		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getUserID(String jkId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select userid from sd_new_jkyx  where id=" + jkId);
		return getJdbcTemplate().queryInt(sb.toString());
	}

	public String getUserJKFQCS(int userId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select hkfq_cishu from sd_new_jkyx  where userid=" + userId);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public Integer getUserJKSBCS(int userId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(1) from sd_firfail_jkyx  where userid="
				+ userId);
		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getUserJKcg(int userId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(1) from sd_new_jkyx  where  jksfwc =1 and userid="
				+ userId);
		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getUserYQCS(int userId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(1) from sd_new_jkyx  where yuq_ts <> 0 and userid="
				+ userId);
		return getJdbcTemplate().queryInt(sb.toString());
	}

	public DataRow getUserChangeXX(int id) {
		String sql = "select * from sd_change_userxx  where id  = " + id;

		return getJdbcTemplate().queryMap(sql);
	}

	public DataRow personInfo(int userid) {
		String sql = "select job ,sfsh, highestEdu,income,companyline ,companyAdd,companyphone,companyName , nativePlacePro ,nativePlaceCity ,contactname ,contactRelationship ,contactPhone  ,contactname02 ,contactRelationship02,contactPhone02 from sd_personjk  where userid  = "
				+ userid;

		return getJdbcTemplate().queryMap(sql);
	}

	public DataRow personYN(int userid) {
		String sql = "select contact1 ,contact2, tel1,tel2,guanxi1 ,guanxi2   from sd_lianxi  where userid  = "
				+ userid;

		return getJdbcTemplate().queryMap(sql);
	}

	public DataRow personInfoYN(int userid) {
		String sql = "select * from sd_work  where userid  = '" + userid + "'";

		return getJdbcTemplate().queryMap(sql);
	}

	public DataRow personLianXi(int userid) {
		String sql = "select * from sd_lianxi  where userid  = '" + userid
				+ "'";

		return getJdbcTemplate().queryMap(sql);
	}

	public DataRow personBank(int userid) {
		String sql = "select * from sd_bankcard  where userid  = '" + userid
				+ "'";

		return getJdbcTemplate().queryMap(sql);
	}

	public DataRow personFinance(int userid) {
		String sql = "select address,email,age,homeaddress,phonetype from sd_user_finance  where userid  = '"
				+ userid + "'";

		return getJdbcTemplate().queryMap(sql);
	}

	public DataRow personShenfen(int userid) {
		String sql = "select * from sd_zhaopian  where userid  = '" + userid
				+ "'";

		return getJdbcTemplate().queryMap(sql);
	}

	public DataRow personPP(int userid) {
		String sql = "select * from sd_new_jkyx  where userid  = '" + userid
				+ "'";

		return getJdbcTemplate().queryMap(sql);
	}

	public DataRow getPPLianxi(String idno) {
		String sql = "select qr1,qr2,qr3 from sd_ziliao  where sfzhm  = '"
				+ idno + "'";

		return getJdbcTemplate().queryMap(sql);
	}

	public DataRow personPiPei(int userid) {
		String sql = "select xm,yddh from sd_pipei  where userid  = '" + userid
				+ "' order by sfzhm desc limit 1";

		return getJdbcTemplate().queryMap(sql);
	}

	public DataRow getUserSchool(int userid) {

		String sql = "select * from  sd_school  where  userid = '" + userid
				+ "'";
		return getJdbcTemplate().queryMap(sql);
	}

	public DataRow getUserJK(int userid) {

		String sql = "select * from  sd_new_jkyx  where  userid = '" + userid
				+ "' and sfyfk=1 and sfyhw=0";
		return getJdbcTemplate().queryMap(sql);
	}

	public DataRow getUserSF(int userid) {

		String sql = "select * from  sd_bankcard  where  userid = '" + userid
				+ "'";
		return getJdbcTemplate().queryMap(sql);
	}

	public DataRow getPersonYdInfo(int userid) {

		String sql = "select orderid ,idnumber,loanplatformcount,loanlastmodifiedtime,repaymentplatformcount ,repaymenttimescount ,totaltrackcount from sduser_portrait  where  userid = "
				+ userid
				+ " and smrenzhen =1 order by lasttracktime desc limit 1  ";

		return getJdbcTemplate().queryMap(sql);
	}

	public DataRow getPersonBL(int userid) {

		String sql = "select thjlbl,txlbl,smsbl from sd_user  where  userid = "
				+ userid;

		return getJdbcTemplate().queryMap(sql);
	}

	public DataRow getUserTonghua(int userid) {

		String sql = "select * from  sd_tonghuayy  where  userid = " + userid;
		return getJdbcTemplate().queryMap(sql);
	}

	public DataRow getUserTonghuaYN(int userid) {

		String sql = "select * from  sd_tonghuajilu  where  userid = " + userid;
		return getJdbcTemplate().queryMap(sql);
	}

	public Integer getUserId(int jkid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select userid from sd_new_jkyx  where id=" + jkid);
		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getUserPiPei(int jkid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select pipei from sd_new_jkyx  where id=" + jkid);
		return getJdbcTemplate().queryInt(sb.toString());
	}

	public String getP1(int userid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select p1 from sd_zhaopian  where userid=" + userid);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public String getP2(int userid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select p2 from sd_zhaopian  where userid=" + userid);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public String getP3(int userid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select p3 from sd_zhaopian  where userid=" + userid);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public String getP1W(int userid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select p1 from sd_work  where userid=" + userid);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public String getP2W(int userid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select p2 from sd_work  where userid=" + userid);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public String getP3W(int userid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select p3 from sd_work  where userid=" + userid);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public String getP1S(int userid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select p1 from sd_school  where userid=" + userid);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public String getP2S(int userid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select p2 from sd_school  where userid=" + userid);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public String getP3S(int userid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select p3 from sd_school  where userid=" + userid);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public Integer getUserSB1Id(int jkid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select userid from sd_firfail_jkyx  where id=" + jkid);
		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getJKSHinfoOne(String jkid) {

		StringBuffer sb = new StringBuffer();
		sb.append("select cl_status from sd_new_jkyx  where id=" + jkid);
		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getJKSHpipei(String jkid) {

		StringBuffer sb = new StringBuffer();
		sb.append("select pipei from sd_new_jkyx  where id=" + jkid);
		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getJKSHinfoTwo(String jkid) {

		StringBuffer sb = new StringBuffer();
		sb.append("select cl02_status from sd_new_jkyx  where id=" + jkid);
		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getHongbaoid(String jkid) {

		StringBuffer sb = new StringBuffer();
		sb.append("select hongbaoid from sd_new_jkyx  where id=" + jkid);
		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getChangeXXCS(int cmsuserid) {

		StringBuffer sb = new StringBuffer();
		sb.append("select ggxxcs from sdcms_user  where user_id=" + cmsuserid);
		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getMoneyCode(String userid) {

		StringBuffer sb = new StringBuffer();
		sb.append("select changemoney from sd_user  where id=" + userid);
		return getJdbcTemplate().queryInt(sb.toString());
	}
	
	public String getusername(int userid) {

		StringBuffer sb = new StringBuffer();
		sb.append("select username from sd_user  where id=" + userid);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public Integer getHongbaojine(int hongbaoid) {

		StringBuffer sb = new StringBuffer();
		sb.append("select hongbao from sd_hongbao  where id=" + hongbaoid);
		return getJdbcTemplate().queryInt(sb.toString());
	}

	public void updateUserJk(DataRow row) throws Exception {

		getJdbcTemplate().update("sd_new_jkyx", row, "id", row.getString("id"));
	}

	public void updateUserBL(DataRow row) throws Exception {

		getJdbcTemplate().update("sd_user", row, "id", row.getString("id"));
	}

	public void updateChangeUserXX(DataRow row) throws Exception {

		getJdbcTemplate().update("sd_change_userxx", row, "id",
				row.getString("id"));
	}

	public void updateChangeUserXXSFXX(DataRow row) throws Exception {

		getJdbcTemplate().update("sd_user_finance", row, "userid",
				row.getString("userid"));
	}

	public void updateChangeUserXXBank(DataRow row) throws Exception {

		getJdbcTemplate().update("sd_bankcard", row, "userid",
				row.getString("userid"));
	}

	public void updateChangeUserGGXXCS(DataRow row) throws Exception {

		getJdbcTemplate().update("sdcms_user", row, "user_id",
				row.getString("user_id"));
	}

	public void updateChangeGuiZe(DataRow row) throws Exception {

		getJdbcTemplate().update("sd_pingjiguize", row, "id",
				row.getString("id"));
	}

	public void updateUserPersonInfo(DataRow row) throws Exception {
		getJdbcTemplate().update("sd_user", row, "id", row.getString("id"));
	}

	public void updateUserInfo(DataRow row) throws Exception {
		getJdbcTemplate().update("sd_user", row, "id", row.getString("id"));
	}

	public void updateUserInfoH(DataRow row) throws Exception {
		getJdbcTemplate().update("sd_user", row, "id", row.getString("id"));
	}

	public DBPage getRecordListTwo(int curPage, int numPerPage, String userId,
			String realName, String phone, String xypf, int cmsuserid,
			String startDate, String endDate, String commit,String off) throws Exception {

		//String sql = "select sd_new_jkyx.id as jkid,sd_new_jkyx.twosh, sd_user.id as id ,sd_user.username,b.realName , INSERT(mobilePhone,4,4,'****') AS mobilePhone  ,sd_user.refferee,INSERT(idNo,4,4,'****') AS idNo, jk_money,pipei,cl_time, jk_date ,jyfk_money, "
		String sql = "select sd_new_jkyx.id as jkid,sd_new_jkyx.twosh, sd_user.id as id ,sd_user.username,b.realName , mobilePhone  ,sd_user.refferee,INSERT(idNo,4,4,'****') AS idNo, jk_money,pipei,cl_time, jk_date ,jyfk_money, "
				+ "cl02_status ,sdcms_user.name as skname from  sd_new_jkyx  left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT join sd_user_finance b  "
				+ "on b.userId =sd_new_jkyx.userid LEFT JOIN sdcms_user on sdcms_user.USER_ID =sd_new_jkyx.cl_ren  where  1=1  and cl_status=1 and cl02_status = 0";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  sd_user.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  sd_user_finance.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(xypf)) {

			sql += " and  pipei =" + xypf;
		}
		if(!StringHelper.isEmpty(off) && off.equals("7")) {
			sql += " and username like 'TAFA%'";
		}
		if (!StringHelper.isEmpty(startDate)) {

			sql += " and (substring(sd_new_jkyx.cl_time,1,10) ='" + startDate
					+ "'";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " or substring(sd_new_jkyx.cl_time,1,10) ='" + endDate
					+ "' )";
		}
		
		sql += " order by  jkid ";

		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public DBPage getRecordListTwoSH(int curPage, int numPerPage, String userId,
			String realName, String phone, String xypf, int cmsuserid,
			String startDate, String endDate, String commit,String off) throws Exception {
		
		//String sql = "select sd_new_jkyx.id as jkid,sd_new_jkyx.twosh,sd_user.username, sd_user.id as id ,b.realName , INSERT(mobilePhone,4,4,'****') AS mobilePhone  ,sd_user.refferee,INSERT(idNo,4,4,'****') AS idNo, jk_money,pipei,cl_time, jk_date ,jyfk_money, "
		String sql = "select sd_new_jkyx.id as jkid,sd_new_jkyx.twosh,sd_user.username, sd_user.id as id ,b.realName ,  mobilePhone  ,sd_user.refferee,INSERT(idNo,4,4,'****') AS idNo, jk_money,pipei,cl_time, jk_date ,jyfk_money, "
				+ "cl02_status ,sdcms_user.name as skname from  sd_new_jkyx  left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT join sd_user_finance b  "
				+ "on b.userId =sd_new_jkyx.userid LEFT JOIN sdcms_user on sdcms_user.USER_ID =sd_new_jkyx.cl_ren  where  1=1  and cl_status=1 and cl02_status = 0";
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  sd_user.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and  sd_user_finance.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and  mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(xypf)) {
			
			sql += " and  pipei =" + xypf;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and (substring(sd_new_jkyx.cl_time,1,10) ='" + startDate
					+ "'";
		}
		if(!StringHelper.isEmpty(off) && off.equals("7")) {
			sql += " and username like 'TAFA%'";
		}
		if (!StringHelper.isEmpty(endDate)) {
			
			sql += " or substring(sd_new_jkyx.cl_time,1,10) ='" + endDate
					+ "' )";
		}
		
		sql += " and sd_new_jkyx.twosh=" + cmsuserid;
		
		sql += " order by  jkid ";
		
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getRecordListTwoSH1(int curPage, int numPerPage, String userId,
			String realName, String phone, String xypf, int cmsuserid,
			String startDate, String endDate, String commit, String shzu1[],String off) throws Exception {
		
		//String sql = "select sd_new_jkyx.id as jkid,sd_new_jkyx.twosh, sd_user.id as id ,sd_user.username,b.realName , INSERT(mobilePhone,4,4,'****') AS mobilePhone  ,sd_user.refferee,INSERT(idNo,4,4,'****') AS idNo, jk_money,pipei,cl_time, jk_date ,jyfk_money, "
		String sql = "select sd_new_jkyx.id as jkid,sd_new_jkyx.twosh, sd_user.id as id ,sd_user.username,b.realName ,  mobilePhone  ,sd_user.refferee,INSERT(idNo,4,4,'****') AS idNo, jk_money,pipei,cl_time, jk_date ,jyfk_money, "
				+ "cl02_status ,sdcms_user.name as skname from  sd_new_jkyx  left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT join sd_user_finance b  "
				+ "on b.userId =sd_new_jkyx.userid LEFT JOIN sdcms_user on sdcms_user.USER_ID =sd_new_jkyx.cl_ren  where  1=1  and cl_status=1 and cl02_status = 0";
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  sd_user.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and  sd_user_finance.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and  mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(xypf)) {
			
			sql += " and  pipei =" + xypf;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and (substring(sd_new_jkyx.cl_time,1,10) ='" + startDate
					+ "'";
		}
		if(!StringHelper.isEmpty(off) && off.equals("7")) {
			sql += " and username like 'TAFA%'";
		}
		if (!StringHelper.isEmpty(endDate)) {
			
			sql += " or substring(sd_new_jkyx.cl_time,1,10) ='" + endDate
					+ "' )";
		}
		for (int i = 0; i < shzu1.length; i++) {
			if(i == 0){
				sql += " and (sd_new_jkyx.twosh=" + shzu1[0];
			}else if(i > 0 && i< shzu1.length-1){
				sql += " or sd_new_jkyx.twosh=" + shzu1[i];
			}else{
				sql += " or sd_new_jkyx.twosh=" + shzu1[i]+")";
			}
		}
		sql += " order by  jkid ";
		
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getRecordListTwoSH2(int curPage, int numPerPage, String userId,
			String realName, String phone, String xypf, int cmsuserid,
			String startDate, String endDate, String commit, String shzu1[],String off) throws Exception {
		
		//String sql = "select sd_new_jkyx.id as jkid,sd_new_jkyx.twosh, sd_user.id as id ,sd_user.username,b.realName , INSERT(mobilePhone,4,4,'****') AS mobilePhone  ,sd_user.refferee,INSERT(idNo,4,4,'****') AS idNo, jk_money,pipei,cl_time, jk_date ,jyfk_money, "
		String sql = "select sd_new_jkyx.id as jkid,sd_new_jkyx.twosh, sd_user.id as id ,sd_user.username,b.realName ,  mobilePhone  ,sd_user.refferee,INSERT(idNo,4,4,'****') AS idNo, jk_money,pipei,cl_time, jk_date ,jyfk_money, "
				+ "cl02_status ,sdcms_user.name as skname from  sd_new_jkyx  left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT join sd_user_finance b  "
				+ "on b.userId =sd_new_jkyx.userid LEFT JOIN sdcms_user on sdcms_user.USER_ID =sd_new_jkyx.cl_ren  where  1=1  and cl_status=1 and cl02_status = 0";
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  sd_user.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and  sd_user_finance.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and  mobilePhone like '%" + phone + "%'";
		}
		if(!StringHelper.isEmpty(off) && off.equals("7")) {
			sql += " and username like 'TAFA%'";
		}
		
		if (!StringHelper.isEmpty(xypf)) {
			
			sql += " and  pipei =" + xypf;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and (substring(sd_new_jkyx.cl_time,1,10) ='" + startDate
					+ "'";
		}
		if (!StringHelper.isEmpty(endDate)) {
			
			sql += " or substring(sd_new_jkyx.cl_time,1,10) ='" + endDate
					+ "' )";
		}
		
		for (int i = 0; i < shzu1.length; i++) {
			if(i == 0){
				sql += " and (sd_new_jkyx.twosh=" + shzu1[0];
			}else if(i > 0 && i< shzu1.length-1){
				sql += " or sd_new_jkyx.twosh=" + shzu1[i];
			}else{
				sql += " or sd_new_jkyx.twosh=" + shzu1[i]+")";
			}
		}
		
		sql += " order by  jkid ";
		
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getRecordListTwoSH3(int curPage, int numPerPage, String userId,
			String realName, String phone, String xypf, int cmsuserid,
			String startDate, String endDate, String commit, String shzu1[],String off) throws Exception {
		
		//String sql = "select sd_new_jkyx.id as jkid,sd_new_jkyx.twosh,sd_user.username, sd_user.id as id ,b.realName , INSERT(mobilePhone,4,4,'****') AS mobilePhone  ,sd_user.refferee,INSERT(idNo,4,4,'****') AS idNo, jk_money,pipei,cl_time, jk_date ,jyfk_money, "
		String sql = "select sd_new_jkyx.id as jkid,sd_new_jkyx.twosh,sd_user.username, sd_user.id as id ,b.realName , mobilePhone  ,sd_user.refferee,INSERT(idNo,4,4,'****') AS idNo, jk_money,pipei,cl_time, jk_date ,jyfk_money, "
				+ "cl02_status ,sdcms_user.name as skname from  sd_new_jkyx  left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT join sd_user_finance b  "
				+ "on b.userId =sd_new_jkyx.userid LEFT JOIN sdcms_user on sdcms_user.USER_ID =sd_new_jkyx.cl_ren  where  1=1  and cl_status=1 and cl02_status = 0";
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  sd_user.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and  sd_user_finance.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and  mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(xypf)) {
			
			sql += " and  pipei =" + xypf;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and (substring(sd_new_jkyx.cl_time,1,10) ='" + startDate
					+ "'";
		}
		if(!StringHelper.isEmpty(off) && off.equals("7")) {
			sql += " and username like 'TAFA%'";
		}
		if (!StringHelper.isEmpty(endDate)) {
			
			sql += " or substring(sd_new_jkyx.cl_time,1,10) ='" + endDate
					+ "' )";
		}
		
		for (int i = 0; i < shzu1.length; i++) {
			if(i == 0){
				sql += " and (sd_new_jkyx.twosh=" + shzu1[0];
			}else if(i > 0 && i< shzu1.length-1){
				sql += " or sd_new_jkyx.twosh=" + shzu1[i];
			}else{
				sql += " or sd_new_jkyx.twosh=" + shzu1[i]+")";
			}
		}
		
		sql += " order by  jkid ";
		
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getRecordListTwoSH4(int curPage, int numPerPage, String userId,
			String realName, String phone, String xypf, int cmsuserid,
			String startDate, String endDate, String commit, String shzu1[],String off) throws Exception {
		
		//String sql = "select sd_new_jkyx.id as jkid,sd_new_jkyx.twosh, sd_user.username,sd_user.id as id ,b.realName , INSERT(mobilePhone,4,4,'****') AS mobilePhone  ,sd_user.refferee,INSERT(idNo,4,4,'****') AS idNo, jk_money,pipei,cl_time, jk_date ,jyfk_money, "
		String sql = "select sd_new_jkyx.id as jkid,sd_new_jkyx.twosh, sd_user.username,sd_user.id as id ,b.realName ,  mobilePhone  ,sd_user.refferee,INSERT(idNo,4,4,'****') AS idNo, jk_money,pipei,cl_time, jk_date ,jyfk_money, "
				+ "cl02_status ,sdcms_user.name as skname from  sd_new_jkyx  left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT join sd_user_finance b  "
				+ "on b.userId =sd_new_jkyx.userid LEFT JOIN sdcms_user on sdcms_user.USER_ID =sd_new_jkyx.cl_ren  where  1=1  and cl_status=1 and cl02_status = 0";
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  sd_user.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and  sd_user_finance.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and  mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(xypf)) {
			
			sql += " and  pipei =" + xypf;
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and (substring(sd_new_jkyx.cl_time,1,10) ='" + startDate
					+ "'";
		}
		if(!StringHelper.isEmpty(off) && off.equals("7")) {
			sql += " and username like 'TAFA%'";
		}
		if (!StringHelper.isEmpty(endDate)) {
			
			sql += " or substring(sd_new_jkyx.cl_time,1,10) ='" + endDate
					+ "' )";
		}
		for (int i = 0; i < shzu1.length; i++) {
			if(i == 0){
				sql += " and (sd_new_jkyx.twosh=" + shzu1[0];
			}else if(i > 0 && i< shzu1.length-1){
				sql += " or sd_new_jkyx.twosh=" + shzu1[i];
			}else{
				sql += " or sd_new_jkyx.twosh=" + shzu1[i]+")";
			}
		}
		sql += " order by  jkid ";
		
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getTongxunlu(int curPage, int numPerPage, String userId,
			int jkid, String realName, String phone, String startDate,
			String endDate, String idCard, String jkdata) throws Exception {

		String sql = "select sd_new_jkyx.userid as id, sd_tongxunlu.userid as userid ,sd_tongxunlu.name as realname , sd_tongxunlu.phone as mobilePhone,create_date,sd_new_jkyx.id as wyid from sd_new_jkyx left join sd_tongxunlu on sd_new_jkyx.userid = sd_tongxunlu.userid where 1=1 and sd_tongxunlu.userid ='"
				+ userId + "' and sd_new_jkyx.id = " + jkid;

		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getTonghuajilu(int curPage, int numPerPage, String userId,
			int jkid, String realName, String phone, String startDate,
			String endDate, String idCard, String jkdata) throws Exception {

		String sql = "select count(*)as geshu, id, userid ,sd_tonghuajilu.name as realname , sd_tonghuajilu.number as mobilephone,calldatestr,dttype, callduration from sd_tonghuajilu where userid ='"
				+ userId + "'";

		sql += " GROUP by mobilephone  ,dttype  order by  geshu desc ";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getUserIP(int curPage, int numPerPage, String userId)
			throws Exception {

		String sql = "select * from sd_dwip where userid ='" + userId
				+ "' order by create_time desc";

		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public List<DataRow> getIP(String userId) {
		String sql = "select * from sd_dwip where userid ='" + userId + "'";
		return getJdbcTemplate().query(sql);
	}

	public DataRow getShTwoInfo(String jkid) {

		StringBuffer sb = new StringBuffer();
		sb.append(" select id , userid,jk_money ,jk_date from sd_new_jkyx  where cl02_status=0 and id =  ");
		sb.append(jkid);
		return getJdbcTemplate().queryMap(sb.toString());
	}

	public DataRow getHKinformation(String jkid) {

		StringBuffer sb = new StringBuffer();
		sb.append(" select id , userid,sjsh_money ,yuq_lx,jk_date,hkyq_time,hkfq_time from sd_new_jkyx  where sfyhw=0 and sfyfk=1 and id =  ");
		sb.append(jkid);
		return getJdbcTemplate().queryMap(sb.toString());
	}
	public String getHKYQtime(String  jkid){
		 String sql ="select hkyq_time from sd_new_jkyx where  id ='"+jkid+"'";
		 return getJdbcTemplate().queryString(sql);
	 }
	 public String getHKFQtime(String  jkid){
		 String sql ="select hkfq_time from sd_new_jkyx where  id ='"+jkid+"'";
		 return getJdbcTemplate().queryString(sql);
	 }
	public DBPage getRecordListThree(int curPage, int numPerPage,
			String userId, String realName, String phone, String xypf,
			int cmsuserid, String startDate, String endDate, String commit,String off,int type) throws Exception {

		//String sql = "select sd_new_jkyx.id as jkid,sd_user.id as id ,sd_user.username,b.realName , INSERT(mobilePhone,4,4,'****') AS mobilePhone  ,INSERT(idNo,4,4,'****') AS idNo,sd_user.refferee,jk_money, jk_date ,sjsh_money, "
		String sql = "select sd_new_jkyx.id as jkid,sd_user.id as id ,sd_user.username,b.realName , mobilePhone  ,INSERT(idNo,4,4,'****') AS idNo,sd_user.refferee,jk_money, jk_date ,sjsh_money, "
				+ "sdcms_user.name as skname, cl02_time ,spsj ,spdz from  sd_new_jkyx  left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT join sd_user_finance b "
				+ "on b.userId =sd_new_jkyx.userid LEFT JOIN sdcms_user on sdcms_user.USER_ID =sd_new_jkyx.cl02_ren where cl02_status=1 and cl03_status = 0";
		if(!StringHelper.isEmpty(String.valueOf(type))) {
			sql += " and  sd_new_jkyx.is_old_user =" + type;
		}
		
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  sd_user.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  sd_user_finance.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(xypf)) {

			sql += " and  pipei= " + xypf;
		}
		if(!StringHelper.isEmpty(off) && off.equals("6")) {
			sql += " and username like 'TAFA%'";
		}
		if (!StringHelper.isEmpty(startDate)) {

			sql += " and (substring(sd_new_jkyx.cl02_time,1,10) ='" + startDate
					+ "'";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " or substring(sd_new_jkyx.cl02_time,1,10) ='" + endDate
					+ "')";
		}
		
		sql += " order by spzt desc ";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getRecordListThreeSH(int curPage, int numPerPage,
			String userId, String realName, String phone, String xypf,
			int cmsuserid, String startDate, String endDate, String commit,String off,int type) throws Exception {
	
		//String sql = "select sd_new_jkyx.id as jkid,sd_user.id as id ,sd_user.username,b.realName , INSERT(mobilePhone,4,4,'****') AS mobilePhone  ,INSERT(idNo,4,4,'****') AS idNo,sd_user.refferee,jk_money, jk_date ,sjsh_money, "
		String sql = "select sd_new_jkyx.id as jkid,sd_user.id as id ,sd_user.username,b.realName ,  mobilePhone  ,INSERT(idNo,4,4,'****') AS idNo,sd_user.refferee,jk_money, jk_date ,sjsh_money, "
				+ "sdcms_user.name as skname, cl02_time ,spsj ,spdz from  sd_new_jkyx  left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT join sd_user_finance b "
				+ "on b.userId =sd_new_jkyx.userid LEFT JOIN sdcms_user on sdcms_user.USER_ID =sd_new_jkyx.cl02_ren where cl02_status=1 and cl03_status = 0";
		if(!StringHelper.isEmpty(String.valueOf(type))) {
			sql += " and  sd_new_jkyx.is_old_user =" + type;
		}
		if (!StringHelper.isEmpty(userId)) {
	
			sql += " and  sd_user.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
	
			sql += " and  sd_user_finance.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
	
			sql += " and  mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(xypf)) {
	
			sql += " and  pipei= " + xypf;
		}
		if(!StringHelper.isEmpty(off) && off.equals("6")) {
			sql += " and username like 'TAFA%'";
		}
		if (!StringHelper.isEmpty(startDate)) {
	
			sql += " and (substring(sd_new_jkyx.cl02_time,1,10) ='" + startDate
					+ "'";
		}
		if (!StringHelper.isEmpty(endDate)) {
	
			sql += " or substring(sd_new_jkyx.cl02_time,1,10) ='" + endDate
					+ "')";
		}
		sql += " and sd_new_jkyx.twosh=" + cmsuserid;
		
		sql += " order by spzt desc ";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getRecordListThreeSH1(int curPage, int numPerPage,
			String userId, String realName, String phone, String xypf,
			int cmsuserid, String startDate, String endDate, String commit,
			String shzu1[],String off,int type) throws Exception {
	
		//String sql = "select sd_new_jkyx.id as jkid,sd_user.id as id ,sd_user.username,b.realName , INSERT(mobilePhone,4,4,'****') AS mobilePhone  ,INSERT(idNo,4,4,'****') AS idNo,sd_user.refferee,jk_money, jk_date ,sjsh_money, "
		String sql = "select sd_new_jkyx.id as jkid,sd_user.id as id ,sd_user.username,b.realName ,  mobilePhone  ,INSERT(idNo,4,4,'****') AS idNo,sd_user.refferee,jk_money, jk_date ,sjsh_money, "
				+ "sdcms_user.name as skname, cl02_time ,spsj ,spdz from  sd_new_jkyx  left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT join sd_user_finance b "
				+ "on b.userId =sd_new_jkyx.userid LEFT JOIN sdcms_user on sdcms_user.USER_ID =sd_new_jkyx.cl02_ren where cl02_status=1 and cl03_status = 0";
		if(!StringHelper.isEmpty(String.valueOf(type))) {
			sql += " and  sd_new_jkyx.is_old_user =" + type;
		}
		if (!StringHelper.isEmpty(userId)) {
	
			sql += " and  sd_user.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
	
			sql += " and  sd_user_finance.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
	
			sql += " and  mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(xypf)) {
	
			sql += " and  pipei= " + xypf;
		}
		if (!StringHelper.isEmpty(startDate)) {
	
			sql += " and (substring(sd_new_jkyx.cl02_time,1,10) ='" + startDate
					+ "'";
		}
		if(!StringHelper.isEmpty(off) && off.equals("6")) {
			sql += " and username like 'TAFA%'";
		}
		if (!StringHelper.isEmpty(endDate)) {
	
			sql += " or substring(sd_new_jkyx.cl02_time,1,10) ='" + endDate
					+ "')";
		}
		for (int i = 0; i < shzu1.length; i++) {
			if(i == 0){
				sql += " and (sd_new_jkyx.twosh=" + shzu1[0];
			}else if(i > 0 && i< shzu1.length-1){
				sql += " or sd_new_jkyx.twosh=" + shzu1[i];
			}else{
				sql += " or sd_new_jkyx.twosh=" + shzu1[i]+")";
			}
		}
		sql += " order by spzt desc ";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getRecordListThreeSH2(int curPage, int numPerPage,
			String userId, String realName, String phone, String xypf,
			int cmsuserid, String startDate, String endDate, String commit,
			String shzu1[],String off,int type) throws Exception {
	
		//String sql = "select sd_new_jkyx.id as jkid,sd_user.id as id ,sd_user.username,b.realName , INSERT(mobilePhone,4,4,'****') AS mobilePhone  ,INSERT(idNo,4,4,'****') AS idNo,sd_user.refferee,jk_money, jk_date ,sjsh_money, "
		String sql = "select sd_new_jkyx.id as jkid,sd_user.id as id ,sd_user.username,b.realName ,  mobilePhone  ,INSERT(idNo,4,4,'****') AS idNo,sd_user.refferee,jk_money, jk_date ,sjsh_money, "
				+ "sdcms_user.name as skname, cl02_time ,spsj ,spdz from  sd_new_jkyx  left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT join sd_user_finance b "
				+ "on b.userId =sd_new_jkyx.userid LEFT JOIN sdcms_user on sdcms_user.USER_ID =sd_new_jkyx.cl02_ren where cl02_status=1 and cl03_status = 0";
		if(!StringHelper.isEmpty(String.valueOf(type))) {
			sql += " and  sd_new_jkyx.is_old_user =" + type;
		}
		if (!StringHelper.isEmpty(userId)) {
	
			sql += " and  sd_user.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
	
			sql += " and  sd_user_finance.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
	
			sql += " and  mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(xypf)) {
	
			sql += " and  pipei= " + xypf;
		}
		if (!StringHelper.isEmpty(startDate)) {
	
			sql += " and (substring(sd_new_jkyx.cl02_time,1,10) ='" + startDate
					+ "'";
		}
		if(!StringHelper.isEmpty(off) && off.equals("6")) {
			sql += " and username like 'TAFA%'";
		}
		if (!StringHelper.isEmpty(endDate)) {
	
			sql += " or substring(sd_new_jkyx.cl02_time,1,10) ='" + endDate
					+ "')";
		}
		for (int i = 0; i < shzu1.length; i++) {
			if(i == 0){
				sql += " and (sd_new_jkyx.twosh=" + shzu1[0];
			}else if(i > 0 && i< shzu1.length-1){
				sql += " or sd_new_jkyx.twosh=" + shzu1[i];
			}else{
				sql += " or sd_new_jkyx.twosh=" + shzu1[i]+")";
			}
		}
		sql += " order by spzt desc ";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getRecordListThreeSH3(int curPage, int numPerPage,
			String userId, String realName, String phone, String xypf,
			int cmsuserid, String startDate, String endDate, String commit,
			String shzu1[],String off,int type) throws Exception {
	
		//String sql = "select sd_new_jkyx.id as jkid,sd_user.id as id ,sd_user.username,b.realName , INSERT(mobilePhone,4,4,'****') AS mobilePhone  ,INSERT(idNo,4,4,'****') AS idNo,sd_user.refferee,jk_money, jk_date ,sjsh_money, "
		String sql = "select sd_new_jkyx.id as jkid,sd_user.id as id ,sd_user.username,b.realName ,  mobilePhone  ,INSERT(idNo,4,4,'****') AS idNo,sd_user.refferee,jk_money, jk_date ,sjsh_money, "
				+ "sdcms_user.name as skname, cl02_time ,spsj ,spdz from  sd_new_jkyx  left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT join sd_user_finance b "
				+ "on b.userId =sd_new_jkyx.userid LEFT JOIN sdcms_user on sdcms_user.USER_ID =sd_new_jkyx.cl02_ren where cl02_status=1 and cl03_status = 0";
		if(!StringHelper.isEmpty(String.valueOf(type))) {
			sql += " and  sd_new_jkyx.is_old_user =" + type;
		}
		if (!StringHelper.isEmpty(userId)) {
	
			sql += " and  sd_user.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
	
			sql += " and  sd_user_finance.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
	
			sql += " and  mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(xypf)) {
	
			sql += " and  pipei= " + xypf;
		}
		if (!StringHelper.isEmpty(startDate)) {
	
			sql += " and (substring(sd_new_jkyx.cl02_time,1,10) ='" + startDate
					+ "'";
		}
		if(!StringHelper.isEmpty(off) && off.equals("6")) {
			sql += " and username like 'TAFA%'";
		}
		if (!StringHelper.isEmpty(endDate)) {
	
			sql += " or substring(sd_new_jkyx.cl02_time,1,10) ='" + endDate
					+ "')";
		}
		for (int i = 0; i < shzu1.length; i++) {
			if(i == 0){
				sql += " and (sd_new_jkyx.twosh=" + shzu1[0];
			}else if(i > 0 && i< shzu1.length-1){
				sql += " or sd_new_jkyx.twosh=" + shzu1[i];
			}else{
				sql += " or sd_new_jkyx.twosh=" + shzu1[i]+")";
			}
		}
		sql += " order by spzt desc ";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getRecordListThreeSH4(int curPage, int numPerPage,
			String userId, String realName, String phone, String xypf,
			int cmsuserid, String startDate, String endDate, String commit,
			String shzu1[],String off,int type) throws Exception {
	
		//String sql = "select sd_new_jkyx.id as jkid,sd_user.id as id ,sd_user.username,b.realName , INSERT(mobilePhone,4,4,'****') AS mobilePhone  ,INSERT(idNo,4,4,'****') AS idNo,sd_user.refferee,jk_money, jk_date ,sjsh_money, "
		String sql = "select sd_new_jkyx.id as jkid,sd_user.id as id ,sd_user.username,b.realName ,  mobilePhone  ,INSERT(idNo,4,4,'****') AS idNo,sd_user.refferee,jk_money, jk_date ,sjsh_money, "
				+ "sdcms_user.name as skname, cl02_time ,spsj ,spdz from  sd_new_jkyx  left join sd_user on sd_new_jkyx.userid = sd_user.id LEFT join sd_user_finance b "
				+ "on b.userId =sd_new_jkyx.userid LEFT JOIN sdcms_user on sdcms_user.USER_ID =sd_new_jkyx.cl02_ren where cl02_status=1 and cl03_status = 0";
		if(!StringHelper.isEmpty(String.valueOf(type))) {
			sql += " and  sd_new_jkyx.is_old_user =" + type;
		}
		if (!StringHelper.isEmpty(userId)) {
	
			sql += " and  sd_user.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
	
			sql += " and  sd_user_finance.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
	
			sql += " and  mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(xypf)) {
	
			sql += " and  pipei= " + xypf;
		}
		if(!StringHelper.isEmpty(off) && off.equals("6")) {
			sql += " and username like 'TAFA%'";
		}
		if (!StringHelper.isEmpty(startDate)) {
	
			sql += " and (substring(sd_new_jkyx.cl02_time,1,10) ='" + startDate
					+ "'";
		}
		if (!StringHelper.isEmpty(endDate)) {
	
			sql += " or substring(sd_new_jkyx.cl02_time,1,10) ='" + endDate
					+ "')";
		}
		for (int i = 0; i < shzu1.length; i++) {
			if(i == 0){
				sql += " and (sd_new_jkyx.twosh=" + shzu1[0];
			}else if(i > 0 && i< shzu1.length-1){
				sql += " or sd_new_jkyx.twosh=" + shzu1[i];
			}else{
				sql += " or sd_new_jkyx.twosh=" + shzu1[i]+")";
			}
		}
		sql += " order by spzt desc ";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DataRow getShThreeInfo(int jkid) {

		StringBuffer sb = new StringBuffer();
		sb.append(" select id  , userid,jk_money ,jk_date ,sjsh_money ,sjds_money ,spdz from sd_new_jkyx  where cl02_status=1 and cl03_status=0 and id =  ");
		sb.append(jkid);
		return getJdbcTemplate().queryMap(sb.toString());
	}

	public DataRow getZhaopian(int userid) {

		StringBuffer sb = new StringBuffer();
		sb.append(" select p3 , userid from sd_zhaopian  where userid =  ");
		sb.append(userid);
		return getJdbcTemplate().queryMap(sb.toString());
	}

	public DataRow getUserRecThreeInfo(int userid) {
		String sql = "select cardNo, INSERT(mobilePhone,4,4,'****') AS mobilePhone  ,username , cardUserName ,bankbs ,bankName ,remark from sd_user  left join  sd_bankcard on sd_bankcard.userId =sd_user.id where userId = "
				+ userid;

		return getJdbcTemplate().queryMap(sql);
	}

	public String getSjdz(String jkid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select sjds_money from sd_new_jkyx  where id=" + jkid);
		return getJdbcTemplate().queryString(sb.toString());
	}
	public String getVideoName(String jkid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select spdz from sd_new_jkyx  where id=" + jkid);
		return getJdbcTemplate().queryString(sb.toString());
	}

	/**
	 * 提现申请(手动提交事务，如出现问题变回滚)
	 */
	public boolean userTx(DataRow withdraw, DataRow user, String money) {

		Session session = getSession("web");
		session.beginTrans();
		try {
			DataRow fundrecord = new DataRow();// 添加资金流向
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
		} finally {
			session.close();
		}
		return false;
	}

	public DataRow getUser(int userid) {
		String sql = "select * from sd_user where id= " + userid;

		return getJdbcTemplate().queryMap(sql);
	}

	public void insertUserMsg(DataRow row) {
		getJdbcTemplate().insert("sd_msg", row);
	}

	public DBPage getUserTh(int curPage, int numPerPage, int userId,
			String phone) throws Exception {

		String sql = "select count(*)as geshu ,id ,peer_number ,phone ,dial_type ,duration  ,location ,location_type ,update_time,create_time ,tonghua_time from sd_tonghuajl  where  userid = "
				+ userId;

		if (!StringHelper.isEmpty(phone)) {

			sql += " and peer_number = '" + phone + "'";
		}

		sql += " GROUP by peer_number  ,dial_type  order by  geshu desc ";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getUserThYN(int curPage, int numPerPage, int userId,
			String phone, int code, String cxid, String cxphone)
			throws Exception {

		String sql = "select count(*)as geshu ,id ,name ,number ,dttype ,calldatestr,sum(callduration)as callduration ,create_time,code  from sd_tonghuajilu  where  userid = "
				+ userId;

		if (!StringHelper.isEmpty(phone)) {

			sql += " and number = '" + phone + "'";
		}
		if (!StringHelper.isEmpty(cxid)) {

			sql += " and id =" + cxid;
		}
		if (!StringHelper.isEmpty(cxphone)) {

			sql += " and number = '" + cxphone + "'";
		}

		sql += " GROUP by number  ,dttype";
		if (code == 0) {
			sql += "  order by  geshu desc  ";
		} else if (code == 1) {
			sql += "  order by  geshu desc  ";
		} else if (code == 2) {
			sql += "  order by  callduration desc  ";
		} else if (code == 3) {
			sql += "  order by  calldatestr desc  ";
		}
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getUserThYNGJ(int curPage, int numPerPage, int userId,
			String phone, int code) throws Exception {

		String sql = "select count(*)as geshu ,id ,name ,number ,dttype ,calldatestr  ,sum(callduration)as callduration ,create_time,code  from sd_tonghuajilu  where  userid = "
				+ userId;

		if (!StringHelper.isEmpty(phone)) {

			sql += " and number = '" + phone + "'";
		}

		sql += " and code =1";
		sql += " GROUP by number  ,dttype";
		if (code == 0) {
			sql += "  order by  geshu desc  ";
		} else if (code == 1) {
			sql += "  order by  geshu desc  ";
		} else if (code == 2) {
			sql += "  order by  callduration desc  ";
		} else if (code == 3) {
			sql += "  order by  calldatestr desc  ";
		}

		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public Integer getTonghuaCount(int userid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(1) from sd_tonghuajl where  userid = " + userid);

		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getTonghuaCountYN(int userid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(1) from sd_tonghuajilu where  userid = "
				+ userid);

		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getTongxunluCountYN(int userid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(1) from sd_tongxunlu where  userid = " + userid);

		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getMsgCountYN(int userid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(1) from sd_sms where  userid = " + userid+" GROUP BY content ");

		return getJdbcTemplate().queryInt(sb.toString());
	}

	public DataRow getUserJDJB(int userid) {
		String sql = "select * from sd_jingdongsh where userid= " + userid;

		return getJdbcTemplate().queryMap(sql);
	}

	public DataRow getUserJDJL(int userid) {
		String sql = "select * from sd_userxx where ydmethod ='jingdong' and userid= "
				+ userid;

		return getJdbcTemplate().queryMap(sql);
	}

	public List<DataRow> getShowJDsh(int userid) {
		String sql = "select * from sd_jingdongsh where userid =" + userid;
		List<DataRow> list = getJdbcTemplate().query(sql);
		return list;
	}

	public DBPage getUserTongxunlu(int curPage, int numPerPage, int userId,
			String cxid, String phone, String name) throws Exception {

		String sql = "select  * from sd_tongxunlu  where  userid = " + userId;
		if (!StringHelper.isEmpty(cxid)) {

			sql += " and id=" + cxid;
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and phone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(name)) {

			sql += " and name like '%" + name + "%'";
		}
		sql += "  order by  id desc ";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getUserTongxunluGJ(int curPage, int numPerPage, int userId,
			String phone) throws Exception {

		String sql = "select  * from sd_tongxunlu  where  userid = " + userId;
		if (!StringHelper.isEmpty(phone)) {

			sql += " and phone like '%" + phone + "%'";
		}
		sql += " and code =1 ";
		sql += "  order by  id  desc ";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getUserMsg(int curPage, int numPerPage, int userId,
			String content, String phone,String sms) throws Exception {

		String sql = "select  * from sd_sms  where  userid = " + userId;
		if (!StringHelper.isEmpty(content)) {

			sql += " and content like '%" + content + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			String[] strarr = phone.split("/");
			StringBuffer sb = new StringBuffer();
	        for(int i=0; i<strarr.length; i++) {
	            if("".equals(strarr[i])) {
	                continue;
	            }
	            sb.append(strarr[i]);
	            if(i != strarr.length - 1) {
	                sb.append(";");
	            }
	        }
	        //用String的split方法分割，得到数组
	        strarr = sb.toString().split(";");
			if (strarr.length > 1) {
				for (int i = 0; i < strarr.length; i++) {
					if (i == 0) {
						sql += " and (phone like '%" + strarr[0].substring(1) + "%'";
					} else if (i > 0 && i <= (strarr.length - 2)) {
						sql += " or phone like '%" + strarr[i].substring(1) + "%'";
					} else {
						sql += " or phone like '%" + strarr[i].substring(1) + "%') ";
					}
				}
			} else {
				phone = phone.replace("/", "");
				sql += " and phone like '%" + phone.substring(1) + "%'";
			}
		}
		if (!StringHelper.isEmpty(sms)) {

			sql += " and phone= '" + sms + "'";
		}
		sql += " GROUP BY content  order by sms_time desc , id desc";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public String getPhoneTXL(String phoneid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select phone from sd_tongxunlu  where id=" + phoneid);
		return getJdbcTemplate().queryString(sb.toString());
	}
	public String getPhoneMsg(String smsid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select phone from sd_sms  where id=" + smsid);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public DBPage getUserMsgGJ(int curPage, int numPerPage, int userId,
			String content, String phone) throws Exception {

		String sql = "select  * from sd_sms  where  userid = " + userId;
		if (!StringHelper.isEmpty(content)) {

			sql += " and content like '%" + content + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and phone like '%" + phone + "%'";
		}
		sql += " and code =1 ";
		sql += "  order by  id desc";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getUserJDJY(int curPage, int numPerPage, int userId)
			throws Exception {

		String sql = "select  * from sd_jingdongjy  where  userid = " + userId;
		sql += "  order by  trade_createtime desc ";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public List<DataRow> getShowTBsh(int userid) {
		String sql = "select * from sd_taobaosh where userid =" + userid;
		List<DataRow> list = getJdbcTemplate().query(sql);
		return list;
	}

	public DataRow getUserTBJL(int userid) {
		String sql = "select * from sd_userxx where ydmethod ='taobao' and userid= "
				+ userid;

		return getJdbcTemplate().queryMap(sql);
	}

	public DBPage getUserTBJY(int curPage, int numPerPage, int userId)
			throws Exception {

		String sql = "select  * from sd_taobaojy  where  userid = " + userId;
		sql += "  order by  trade_createtime desc ";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DataRow getTaobaoUserInfo(int userid) {
		String sql = "select * from sd_taobaouserinfo where userid= " + userid;

		return getJdbcTemplate().queryMap(sql);
	}

	public DataRow getUserTHJL(int userid) {
		String sql = "select * from sd_userxx where ydmethod ='carrier' and userid= "
				+ userid;

		return getJdbcTemplate().queryMap(sql);
	}

	public List<DataRow> getJKSBList(int userid) {
		String sql = "select j.id as jkid, u.id as id ,f.realName , INSERT(u.mobilePhone,4,4,'****'),INSERT(f.idNo,4,4,'****'), j.jk_money, j.create_date ,j.cl_status ,j.cl_yj  ,"
				+ "j.cl_time ,j.cl02_yj ,j.cl02_ren ,j.cl_ren ,j.cl03_ren ,j.cl02_status , j.cl02_time  ,j.cl03_yj ,j.cl03_status ,j.cl03_time from  (select id ,jk_money ,create_date ,"
				+ "cl_status ,cl_yj ,cl02_time ,cl_time ,cl03_yj ,userid , cl02_status, cl02_yj,cl03_status ,cl03_time ,cl_ren ,cl02_ren ,cl03_ren  from sd_new_jkyx where cl_status =3 or "
				+ " cl02_status =3 or cl03_status=3 )j left join sd_user u on j.userid = u.id LEFT join sd_user_finance f on f.userId =j.userid  where  1 =1 and j.userid ="
				+ userid + " order by j.create_date desc limit 8  ";
		return getJdbcTemplate().query(sql);
	}

	public List<DataRow> getJKSBListOne(int userid) {
		String sql = "select j.id as jkid, u.id as id ,f.realName , INSERT(u.mobilePhone,4,4,'****') ,INSERT(f.idNo,4,4,'****'), j.jk_money, j.create_date ,j.cl_status ,j.cl_yj  ,"
				+ "j.cl_time ,j.cl_ren from sd_new_jkyx j  left join sd_user u on j.userid = u.id LEFT join sd_user_finance f on f.userId =j.userid  where  1 =1 and j.cl_status =3 and j.userid ="
				+ userid + "  limit 4  ";
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
		String sql = "select j.fkdz_time, j.cl02_time,j.cl_ren,j.cl_time,j.cl03_time ,j.cl02_ren ,j.cl03_ren ,j.hkyq_time,j.hk_time ,j.hkfq_time,j.yuq_ts,j.sjds_money,j.lx,  j.sjsh_money , j.sfyhw ,j.create_date "
				+ " from  sd_new_jkyx j  where  j.sfyfk=1  and j.userid ="
				+ userid + " order by j.daytime desc ";
		return getJdbcTemplate().query(sql);
	}

	public List<DataRow> getAllUserJK(String userid) {
		String sql = "select yuq_ts from  sd_new_jkyx where sfyfk=1 and sfyhw=1 and userid ="
				+ userid;
		return getJdbcTemplate().query(sql);
	}

	public List<DataRow> getJKCSList(int userid) {
		String sql = "select id,user_id,visitdate,jkjl_id,content,kefuid,code from sd_returnvisit where (code=1 or code=6 or code=11) and user_id ="
				+ userid + " order by id desc limit 10";
		return getJdbcTemplate().query(sql);
	}

	public DataRow getUserZmfs(int userid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from sd_zmrz where successjg='true'  and userid ="
				+ userid);

		return getJdbcTemplate().queryMap(sb.toString());
	}

	public Integer getSJXTResult(int userid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select sjxt  from sd_user where  id= " + userid);

		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getDSBResult(String rec_id) {
		StringBuffer sb = new StringBuffer();
		sb.append("select cl03_status  from sd_new_jkyx where  id= " + rec_id);

		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getESPeople(String rec_id) {
		StringBuffer sb = new StringBuffer();
		sb.append("select cl02_ren  from sd_new_jkyx where  id= " + rec_id);

		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getHangCount(int userid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(*) from sd_new_jkyx  where  jksfwc=0 and cl_status <>3 and cl02_status <>3 and cl03_status <>3  and  userid = "
				+ userid);

		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getBNFKBZ(int userid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(id) from sd_returnvisit  where code=11 and content like '%KH không thể vay thành công lần nữa%' and user_id = " + userid);
		
		return getJdbcTemplate().queryInt(sb.toString());
	}
	public Integer getSHQX(int cmsuserid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select shqx from sdcms_user  where user_id = " + cmsuserid);

		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getCGJKCS(String userid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(id) from sd_new_jkyx  where sfyfk=1 and sfyhw=1 and userid = "
				+ userid);

		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getONESH(String jkid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select onesh from sd_new_jkyx  where id = " + jkid);
		
		return getJdbcTemplate().queryInt(sb.toString());
	}
	public Integer getTWOSH(String jkid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select twosh from sd_new_jkyx  where id = " + jkid);
		
		return getJdbcTemplate().queryInt(sb.toString());
	}
	public Integer getPingFen(String jkid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select pipei from sd_new_jkyx  where id = " + jkid);

		return getJdbcTemplate().queryInt(sb.toString());
	}

	public String getGJCGL() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang1 from sd_pingjiguize  where id =15");
		
		return getJdbcTemplate().queryString(sb.toString());
	}
	public int getYQCSGZ() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang1 from sd_pingjiguize  where id =14");

		return getJdbcTemplate().queryInt(sb.toString());
	}
	public int getYQCSGZJJ() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang1 from sd_pingjiguize  where id =17");
		
		return getJdbcTemplate().queryInt(sb.toString());
	}

	public int getYQTSGZJJ() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang1 from sd_pingjiguize  where id =16");
		
		return getJdbcTemplate().queryInt(sb.toString());
	}
	public int getYQTSGZ() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang1 from sd_pingjiguize  where id =13");

		return getJdbcTemplate().queryInt(sb.toString());
	}

	public String getPJGZ8() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang1 from sd_pingjiguize  where id =8 ");

		return getJdbcTemplate().queryString(sb.toString());
	}

	public String getPJGZ9() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang1 from sd_pingjiguize  where id =9 ");

		return getJdbcTemplate().queryString(sb.toString());
	}

	public String getPJGZSHENHE() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang1 from sd_pingjiguize  where id =19 ");
		
		return getJdbcTemplate().queryString(sb.toString());
	}
	public String getPJGZSHENHEZZ1() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang1 from sd_pingjiguize  where id =24 ");
		
		return getJdbcTemplate().queryString(sb.toString());
	}
	public String getPJGZSHENHE1() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang2 from sd_pingjiguize  where id =24 ");
		
		return getJdbcTemplate().queryString(sb.toString());
	}
	public String getPJGZSHENHEZZ2() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang1 from sd_pingjiguize  where id =25 ");
		
		return getJdbcTemplate().queryString(sb.toString());
	}
	public String getPJGZSHENHE2() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang2 from sd_pingjiguize  where id =25 ");
		
		return getJdbcTemplate().queryString(sb.toString());
	}
	public String getPJGZSHENHEZZ3() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang1 from sd_pingjiguize  where id =26 ");
		
		return getJdbcTemplate().queryString(sb.toString());
	}
	public String getPJGZSHENHE3() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang2 from sd_pingjiguize  where id =26 ");
		
		return getJdbcTemplate().queryString(sb.toString());
	}
	public String getPJGZSHENHEZZ4() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang1 from sd_pingjiguize  where id =27 ");
		
		return getJdbcTemplate().queryString(sb.toString());
	}
	public String getPJGZSHENHE4() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang2 from sd_pingjiguize  where id =27 ");
		
		return getJdbcTemplate().queryString(sb.toString());
	}
	public String getPJGZ10() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang1 from sd_pingjiguize  where id =10 ");

		return getJdbcTemplate().queryString(sb.toString());
	}

	public String getPJGZ11() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang1 from sd_pingjiguize  where id =11 ");

		return getJdbcTemplate().queryString(sb.toString());
	}

	public String getPJGZ18() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang1 from sd_pingjiguize  where id =18 ");
		
		return getJdbcTemplate().queryString(sb.toString());
	}
	public String getPJGZ12() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang1 from sd_pingjiguize  where id =12 ");

		return getJdbcTemplate().queryString(sb.toString());
	}

	public DataRow getWJDHfsdx(String datenow, String enddate) {
		String sql = " select  * from sd_csmsg where create_time>'" + datenow
				+ "' and create_time<'" + enddate + "'";
		return getJdbcTemplate().queryMap(sql);
	}

	public List<DataRow> getAllcuishouM2() {
		String sql = "SELECT user_id FROM sdcms_user WHERE (roleid=24 or roleid=51)";
		return getJdbcTemplate().query(sql);
	}
	
	
	 public List<DataRow> getALLshenfenSalo(String idno){
		 String sql = "select userid from sd_user_finance where  idno ='"+idno+"'" ;
		 return getJdbcTemplate().query(sql);
	 }
	 public int  getUserIDSF(String idno){
		 
		 String sql = "select userid from sd_user_finance where  idno ="+idno ;	 
		 return getJdbcTemplate().queryInt(sql);	 
	 }
	 public Integer  getHHZTTXL(int userid){
		 
		 String sql = "select islianxi from sd_user where  id ="+userid ;
		 return getJdbcTemplate().queryInt(sql);	 
	 }
	 public Integer  getHHZT(int userid){
			
	 String sql = "select vipstatus from sd_user where  id ="+userid ;
	 return getJdbcTemplate().queryInt(sql);	 
	}
	 public void insertPPCX(DataRow row)
     {
    	 getJdbcTemplate().insert("sd_userppcx", row);
     }
	 
/*** 20190801 linc 信息查询**start****/
	/**
	 * 黑户状态
	 * @param userid
	 * @return
	 */
	 public Integer  getHHZT_state(int userid){
			
		 String sql = "select heihu_zt from sd_user where  id ="+userid ;
		 return getJdbcTemplate().queryInt(sql);	 
	}
      /**
       * 未逾期成功还款次数
       * @param userid
       * @return
       */
	 public Integer  getuserjkcs(int userid){
			
		 String sql = " SELECT COUNT(*) AS jkcs ,userid FROM sd_new_jkyx  WHERE cl03_status =1 AND sfyhw =1 AND sfyfk = 1 AND yuq_ts <10  and userid ="+userid ;
		 return getJdbcTemplate().queryInt(sql);	 
	}
	 
 /*** 20190801 linc 信息查询**end****/

/*** 20190801 linc 信息查询**end****/
	 
	 
	 //** a用户信息查询2019年8月5日 lic  start 
	 public DataRow getUserfianceDataRow(String userid,String usercmnd) {
		 String sql = " select  * from sd_user_finance where 1=1";
		 if (!userid.equals("")) {
			 sql+= " and userId = "+userid;
		}
		 
		 if (!usercmnd.equals("")) {
			 sql+= " and idNo = "+usercmnd;
		}
		 sql+= " ORDER BY id DESC LIMIT 1 ";
		
			return getJdbcTemplate().queryMap(sql);
	 }
	 

	 public DataRow getUserphotoRow(String userid) {
		 String sql = " SELECT * FROM sd_zhaopian  WHERE  1=1";
		 if (!userid.equals("")) {
			 sql+= " and userId = "+userid;
		}
		 
	   return getJdbcTemplate().queryMap(sql);
	 }
	 
	 public DataRow getUserphoneRow(String userid) {
		 String sql = " SELECT * FROM sd_user  WHERE  1=1";
		 if (!userid.equals("")) {
			 sql+= " and id = "+userid;
		}
		
	  return getJdbcTemplate().queryMap(sql);
	 }
	 
	 public DataRow getUserjkinfoDataRow(String userid) {
		 String sql = " SELECT * FROM sd_new_jkyx WHERE  spdz IS NOT NULL";
		 if (!userid.equals("")) {
			 sql+= " and userid = "+userid;
		}
		 
		 sql+= " ORDER BY id DESC LIMIT 1 ";
	
		return getJdbcTemplate().queryMap(sql);
	 }
	 
	 public void insertUserpv(DataRow row)
     {
    	 getJdbcTemplate().insert("sdcms_userpv_record", row);
     }
//** a用户信息查询2019年8月5日 lic  end
	 
	 public int getCMSuserRoleid(int cmsuserid) {
		 String sql = "select roleid from sdcms_user  where user_id="+cmsuserid;
		 return getJdbcTemplate().queryInt(sql);
	 }
	 public int getCMSuserState(String cmsuserid) {
		 
		 if (!StringHelper.isEmpty(cmsuserid)) {
			 String sql = "select state from sdcms_user  where user_id='"+cmsuserid+"'";
			 return getJdbcTemplate().queryInt(sql);
		 }
		return 0;
	}
	 public String getCMSuserOnline(String cmsuserid) {
			String sql = "select last_time from sdcms_user  where user_id='"+cmsuserid+"'";
			return getJdbcTemplate().queryString(sql);
	    }
	 
	 //basicInfo
	 public DataRow getFacebookUser(int userid) {
		 String sql = " SELECT * FROM sd_report_task_token_fb_user WHERE userid="+userid;
		 
		 sql+= " ORDER BY id DESC LIMIT 1 ";
	
		return getJdbcTemplate().queryMap(sql);
	 }
	 public DataRow getZaloUser(int userid) {
		 String sql = " SELECT * FROM sd_report_task_token_zalo_user WHERE userid="+userid;
		 
		 sql+= " ORDER BY id DESC LIMIT 1 ";
		 
		 return getJdbcTemplate().queryMap(sql);
	 }
	 public DataRow getYYSUser(int userid) {
		 String sql = " SELECT * FROM sd_report_task_token_fb_user WHERE userid="+userid;
		 
		 sql+= " ORDER BY id DESC LIMIT 1 ";
		 
		 return getJdbcTemplate().queryMap(sql);
	 }
	 //facebook
	 public DataRow getFacebookPost(int userid) {
		 String sql = " SELECT * FROM sd_report_task_token_fb_user WHERE userid="+userid;
		 
		 sql+= " ORDER BY id DESC LIMIT 1 ";
	
		return getJdbcTemplate().queryMap(sql);
	 }
	 public List<DataRow> getFacebook(int userid) {
			String sql = "select * from sd_report_task_token_fb_posts where userid =" + userid;
			List<DataRow> list = getJdbcTemplate().query(sql);
			return list;
	}
	 public List<DataRow> getZaloFriend(int userid) {
		 String sql = "select * from sd_report_task_token_zalo_friends where userid =" + userid;
		 List<DataRow> list = getJdbcTemplate().query(sql);
		 return list;
	 }
	 public List<DataRow> getZaloGroup(int userid) {
		 String sql = "select * from sd_report_task_token_zalo_groups where userid =" + userid;
		 List<DataRow> list = getJdbcTemplate().query(sql);
		 return list;
	 }
	 public DBPage getZaloFriend(int curPage, int numPerPage, int userId,
				String cxid, String phone,String name) throws Exception {

			String sql = "select  * from sd_report_task_token_zalo_friends  where  userid = " + userId;
			if (!StringHelper.isEmpty(cxid)) {

				sql += " and id=" + cxid;
			}
			if (!StringHelper.isEmpty(phone)) {

				sql += " and friendphone_number='" + phone + "'";
			}
			if (!StringHelper.isEmpty(name)) {
				
				sql += " and display_name like '%" + name + "%'";
			}
			sql += "  order by friendphone_number desc ";
			return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	 public DBPage getUserYys(int curPage, int numPerPage, int userId,
				String cxid, String phone) throws Exception {

			String sql = "select  * from sd_report_task_token_yys_detail  where  userid = " + userId;
			if (!StringHelper.isEmpty(cxid)) {

				sql += " and id=" + cxid;
			}
			if (!StringHelper.isEmpty(phone)) {

				sql += " and phone='" + phone + "'";
			}
			sql += "  order by call_length desc,call_cnt desc ";
			return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	 public Integer getYysCount(int userid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(1) from sd_report_task_token_yys_detail where  userid = " + userid);

		return getJdbcTemplate().queryInt(sb.toString());
	}
	 public DataRow geYysUser(int userid) {
		 String sql = " SELECT * FROM sd_report_task_token_yys_user WHERE userid="+userid;
		 
		 sql+= " ORDER BY id DESC LIMIT 1 ";
		 
		 return getJdbcTemplate().queryMap(sql);
	 }
	 public List<DataRow> getYysMonth(int userid) {
		 String sql = "select * from sd_report_task_token_yys_mth where userid =" + userid;
		 List<DataRow> list = getJdbcTemplate().query(sql);
		 return list;
	 }
	 public List<DataRow> getYysUp(int userid) {
		 String sql = "select * from sd_report_task_token_yys_history where userid =" + userid;
		 List<DataRow> list = getJdbcTemplate().query(sql);
		 return list;
	 }
	 
	 /**
		 * 审核备注统计表
		 * @param userid
		 * @return
		 */
		public DBPage getJKFaceBookbzPage(int curPage, int numPerPage, String userid,String jkid) throws Exception {
			String sql = "select id,user_id,visitdate,jkjl_id,content,kefuid,code from sd_returnvisit where  user_id ="+ userid 
					+  " ORDER BY CASE WHEN content LIKE '%www.facebook.com%' THEN 0 ELSE 1 END  ";
			return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
		}
		
		public List<DataRow> getJKFaceBookbzList( String userid,String jkid) throws Exception {
			String sql = "select id,user_id,visitdate,jkjl_id,content,kefuid,code from sd_returnvisit where  user_id ="+ userid ;
			return getJdbcTemplate().query(sql);
		}
}
