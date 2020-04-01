package com.project.service.account;

import java.util.List;

import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;
import com.thinkive.base.jdbc.DBPage;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.service.BaseService;
import com.thinkive.base.util.StringHelper;

import root.role.roleAuthorityMangement;

public class JBDcms2Service extends BaseService {

	private static Logger logger = Logger.getLogger(JBDcms2Service.class);
	private static roleAuthorityMangement roleauthoritymangement  = new roleAuthorityMangement();

	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}
	
		//**********************************xiong20190614********start****************************************
	public List<DataRow> getRepayPlanListRow(String userId,String jkid,String phone,int hkcode,int yqcode,String startDate,String endDate,String commit,String orderid ,String cmsuserid,String hkbank,String idno,String time,String funpay,int maproleid)throws Exception
  	{

	    String sql = " select  r.id as hkid,r.userid  as userid  ,j.id as jkid ,j.fkdz_time_day, r.rechargeTime ,r.userhktime ,r.bankjztime ,s.realname, s.idno,INSERT(f.cardNo,4,4,'****')AS cardNo ,r.rechargeMoney ," +
  				" r.result ,r.remark,r.yqdate,r.resultInfo ,r.hkbank,r.dqyqts,r.dqyqlx,r.bankorderid,r.qrhkcode,r.operatorid,j.yuq_ts ,j.sjds_money,j.lx,j.hongbaoid,j.jk_date ,j.yuq_lx from  sd_recharge_detail r left join sd_bankcard f  on f.userid = r.userid left join  sd_user_finance s  on s.userid = r.userid left join sd_new_jkyx j on" +
  				"  j.id=r.rechargeNumber LEFT JOIN sdcms_user w ON w.USER_ID = r.cuishouid   where r.nowstate=1 ";  		         
  		if(!StringHelper.isEmpty(userId)){	
  			
			sql +=" and r.userid= "+userId  ;
		}
       
		if(!StringHelper.isEmpty(jkid)){	
			
            sql +=" and j.id =" +jkid ;
		}
		if(!StringHelper.isEmpty(cmsuserid)){	
			
			sql +=" and r.cuishouid =" +cmsuserid ;
		}
		if(!StringHelper.isEmpty(hkbank)){	
			
			sql +=" and r.hkbank =" +hkbank ;
		}
		if(!StringHelper.isEmpty(idno)){	
			
			sql +=" and s.idno ='" +idno+"'" ;
		}
       if(hkcode == 1){
    	   
    	   sql +=" and r.remark ='全额还款' " ;
       }
       
       if(hkcode == 2){
    	   
    	   sql +=" and (r.remark ='逾期利息还款' or r.remark ='逾期利息还完，部分本金还款' or r.remark ='部分本金还款')" ;
       }
       if(hkcode == 3){
    	   
    	   sql +=" and r.remark ='完成还款，部分逾期利息还款' " ;
       }
       
       if(hkcode == 4){
    	   
    	   sql +=" and r.remark ='延期还款' " ;
       }
       if(!StringHelper.isEmpty(funpay)){
    	   
    	   sql +=" and r.orderpaytype =" +funpay;
       }
       
       if(yqcode == 1){
    	   
    	   sql +=" and j.yuq_ts =0 " ;
       }
       
       if(yqcode == 2){
    	   
    	   sql +=" and j.yuq_ts >0 " ;
       }
       /*if(!StringHelper.isEmpty(orderid)){
			
            sql +=" and r.paynumber ='" +orderid +"'" ;
		}*/
       if (!StringHelper.isEmpty(startDate)) {
			if("1".equals(time)){
				sql += " and CONCAT(SUBSTRING(r.bankjztime,7,4),'-',SUBSTRING(r.bankjztime,4,2),'-',SUBSTRING(r.bankjztime,1,2),SUBSTRING(r.bankjztime,11,9)) >='" + startDate + "'";	
				sql += " and CONCAT(SUBSTRING(r.bankjztime,7,4),'-',SUBSTRING(r.bankjztime,4,2),'-',SUBSTRING(r.bankjztime,1,2)) <='" + endDate + "'";
			}else{
				sql += " and CONCAT(SUBSTRING(r.rechargeTime,7,4),'-',SUBSTRING(r.rechargeTime,4,2),'-',SUBSTRING(r.rechargeTime,1,2),SUBSTRING(r.rechargeTime,11,9)) >='" + startDate + "'";	
				sql += " and CONCAT(SUBSTRING(r.rechargeTime,7,4),'-',SUBSTRING(r.rechargeTime,4,2),'-',SUBSTRING(r.rechargeTime,1,2)) <='" + endDate + "'";
			}
	   }
       
       if(50==maproleid) {
	 		 sql += " and r.dqyqts >3 and r.dqyqts <16 " ;
	 		 sql +=" AND (w.roleid =21 OR w.roleid=50)  ";
	 	  }else if(51==maproleid) {
	 		 sql += " and r.dqyqts >15 and r.dqyqts <=60 " ;
	 		 sql +=" AND (w.roleid =24 OR w.roleid=51) ";
	 	  }else if(54==maproleid) {
	 		 sql += " and r.dqyqts >45 " ;
	 		 sql +=" AND (w.roleid =26 OR w.roleid=54) ";
	 	  }else if(20==maproleid) {
	 		 sql += " and r.dqyqts >0 and r.dqyqts <4 " ;
	 		 sql +=" AND (w.roleid =19 OR w.roleid=20) ";
	 	  }else if(61==maproleid) {
	 		 sql += " and r.dqyqts >45 " ;
	 		 sql +=" AND (w.roleid =60 OR w.roleid=61) ";
	 	  }else if(63==maproleid) {
		 		 sql +=" AND (w.roleid =60 OR w.roleid=61 or w.roleid=62) ";
		 	  }
  	    return getJdbcTemplate().query(sql);
  	}
	
	
	
	public DBPage getRepayPlanListHK(int curPage ,int numPerPage,String userId,String jkid,String phone,int hkcode,int yqcode,String startDate,String endDate,String commit,String orderid ,String cmsuserid,String hkbank,String idno,String time,String funpay,int maproleid)throws Exception
  	{

  		String sql = " select  r.id as hkid,r.userid  as userid  ,j.id as jkid ,j.fkdz_time_day,j.fkdz_time,j.sjsh_money,u.username, r.rechargeTime ,r.userhktime ,r.bankjztime ,r.rechargeMoney ," +
  				" r.result ,r.remark,r.yqdate,r.resultInfo ,r.hkbank,r.dqyqts,r.dqyqlx,r.bankorderid,r.qrhkcode,r.operatorid,j.yuq_ts ,j.sjds_money,j.lx,j.hongbaoid,j.jk_date ,j.yuq_lx,su.name AS cuishouname,suc.name AS caiwuname,s.realname from  sd_recharge_detail r   left join sd_new_jkyx j on" +
  				"  j.id=r.rechargeNumber LEFT JOIN sd_user u  ON r.userid =u.id  LEFT JOIN sdcms_user su ON su.USER_ID=r.cuishouid  LEFT JOIN sdcms_user suc ON suc.USER_ID=r.operatorId  left join  sd_user_finance s  on s.userid = r.userid  LEFT JOIN sdcms_user w ON w.USER_ID = r.cuishouid   where r.nowstate=1 AND dqyqts <>0 ";	  		         
  		if(!StringHelper.isEmpty(userId)){	
  			
			sql +=" and r.userid= "+userId  ;
		}
       
		if(!StringHelper.isEmpty(jkid)){	
			
            sql +=" and j.id =" +jkid ;
		}
		if(!StringHelper.isEmpty(cmsuserid)){	
			
			sql +=" and r.cuishouid =" +cmsuserid ;
		}
		if(!StringHelper.isEmpty(hkbank)){	
			
			sql +=" and r.hkbank =" +hkbank ;
		}
//		if(!StringHelper.isEmpty(idno)){	
//			
//			sql +=" and s.idno ='" +idno+"'" ;
//		}
       /*if(!StringHelper.isEmpty(phone)){
			
            sql +=" and u.mobilePhone like'%" +phone +"'" ;
		}*/
       if(hkcode == 1){
    	   
    	   sql +=" and r.remark ='全额还款' " ;
       }
       
       if(hkcode == 2){
    	   
    	   sql +=" and (r.remark ='逾期利息还款' or r.remark ='逾期利息还完，部分本金还款' or r.remark ='部分本金还款')" ;
       }
       if(hkcode == 3){
    	   
    	   sql +=" and r.remark ='完成还款，部分逾期利息还款' " ;
       }
       
       if(hkcode == 4){
    	   
    	   sql +=" and r.remark ='延期还款' " ;
       }
       
       if(yqcode == 1){
    	   
    	   sql +=" and j.yuq_ts =0 " ;
       }
       
       if(yqcode == 2){
    	   
    	   sql +=" and j.yuq_ts >0 " ;
       }
       
       if(!StringHelper.isEmpty(orderid)){
			
            sql +=" and r.bankorderid ='" +orderid +"'" ;
	   }
       if(!StringHelper.isEmpty(funpay)){
    	   
    	   sql +=" and r.orderpaytype =" +funpay;
       }
       if (!StringHelper.isEmpty(startDate)) {
			if("1".equals(time)){
				sql += " and CONCAT(SUBSTRING(r.bankjztime,7,4),'-',SUBSTRING(r.bankjztime,4,2),'-',SUBSTRING(r.bankjztime,1,2),SUBSTRING(r.bankjztime,11,9)) >='" + startDate + "'";	
				sql += " and CONCAT(SUBSTRING(r.bankjztime,7,4),'-',SUBSTRING(r.bankjztime,4,2),'-',SUBSTRING(r.bankjztime,1,2)) <='" + endDate + "'";
			}else{
				sql += " and CONCAT(SUBSTRING(r.rechargeTime,7,4),'-',SUBSTRING(r.rechargeTime,4,2),'-',SUBSTRING(r.rechargeTime,1,2),SUBSTRING(r.rechargeTime,11,9)) >='" + startDate + "'";	
				sql += " and CONCAT(SUBSTRING(r.rechargeTime,7,4),'-',SUBSTRING(r.rechargeTime,4,2),'-',SUBSTRING(r.rechargeTime,1,2)) <='" + endDate + "'";
			}
	   }
       
       if(50==maproleid) {
	 		 sql += " and r.dqyqts >3 and r.dqyqts <16 " ;
	 		 sql +=" AND (w.roleid =21 OR w.roleid=50)  ";
	 	  }else if(51==maproleid) {
	 		 sql += " and r.dqyqts >15 and r.dqyqts <=60 " ;
	 		 sql +=" AND (w.roleid =24 OR w.roleid=51) ";
	 	  }else if(54==maproleid) {
	 		 sql += " and r.dqyqts >45 " ;
	 		 sql +=" AND (w.roleid =26 OR w.roleid=54) ";
	 	  }else if(20==maproleid) {
	 		 sql += " and r.dqyqts >0 and r.dqyqts <4 " ;
	 		 sql +=" AND (w.roleid =19 OR w.roleid=20) ";
	 	  }else if(61==maproleid) {
	 		 sql += " and r.dqyqts >45 " ;
	 		 sql +=" AND (w.roleid =60 OR w.roleid=61) ";
	 	  }else if(63==maproleid) {
		 		 sql +=" AND (w.roleid =60 OR w.roleid=61 or w.roleid=62) ";
		 	  }
  		
  		sql +="  order by SUBSTRING(r.rechargeTime,7,4) desc ,SUBSTRING(r.rechargeTime,4,2) desc , SUBSTRING(r.rechargeTime,1,2) desc ,SUBSTRING(r.rechargeTime,12,2) desc,SUBSTRING(r.rechargeTime,15,2) desc,SUBSTRING(r.rechargeTime,18,2) desc" ;
  	  
  	    return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
  	}
	
	
	public List<DataRow> getRechargeByJkid(String jkid){
		String sql ="SELECT   sr.rechargeMoney as money,sr.cuishouid,sr.rechargeTime as retime,sr.rechargeNumber,su.NAME as csname,yqdate,remark,userhktime"; 	
				sql+=" FROM sd_recharge_detail sr  LEFT JOIN sdcms_user su ON sr.cuishouid=su.USER_ID  ";
				sql+=" WHERE   rechargeNumber= "+jkid;
				return getJdbcTemplate().query(sql);				
	}
	
	
	//
	public DBPage getYqZuList(int curPage, int numPerPage, String userId,
			String realName, String phone,String startDate, String endDate,
			String commit, String idCard, String hkstat, String et_day,
			String st_day,int cmsuserid,String cuishouid,int maproleid,String jkdate,String yuqts) throws Exception {

		String sql = " select j.userid, j.id as jkid2 ,u.username, u.mobilePhone,u.heihu_zt,s.realname,w.name as csname , j.yuq_lx ,j.yuq_ts ,j.sjds_money ,j.lx ,j.sjsh_money ,j.hkyq_time,j.hkfq_code,j.hk_time,j.hkfq_time,j.hkfq_cishu,j.jk_date,j.fkr_time,j.sfyhw,j.hkqd,j.hkstate,j.zxcsbz as msg,j.hkpz,j.hkpz_time from  (select id ,yuq_ts ,jk_date,userid ,yuq_lx , "
				+ "  sjsh_money,sjds_money,lx,cuishou_id,fkr_time,hkyq_time,hkfq_time,hkfq_cishu,hk_time,hkfq_code,hkqd,sfyhw,hkstate,zxcsbz,hkpz,hkpz_time from  sd_new_jkyx where yuq_ts > 0  ) j  left join sd_user u on "
				+ "  j.userid = u.id left join sd_user_finance s on s.userId =j.userid left join sdcms_user w on w.USER_ID = j.cuishou_id where j.sfyhw = 0 and j.hkfq_code=0";

		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  s.realname like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {
			
			sql += " and  s.idno='" + idCard + "'";
		}
		if (!StringHelper.isEmpty(cuishouid)) {
			
			sql += " and  j.cuishou_id=" + cuishouid;
		}
		if (!StringHelper.isEmpty(yuqts)) {
			
			sql += " and  j.yuq_ts=" + yuqts;
		}
		if (!StringHelper.isEmpty(jkdate)) {
			
			sql += " and  (j.jk_date=3 or j.jk_date=4) ";
		}
		if (!StringHelper.isEmpty(et_day) && !et_day.equals("0")) {

			sql += " and  j.yuq_ts >= " + et_day;
		}
		if (!StringHelper.isEmpty(st_day) && !st_day.equals("0")) {

			sql += " and  j.yuq_ts <= " + st_day;
		}
		
		
		/*if (hkstat.equals("1")) {

			sql += " and  j.sfyhw = 1";
		}

		if (hkstat.equals("2")) {
*/
		
		//}
		if(50==maproleid) {
	 		 sql += " and j.yuq_ts >3 and j.yuq_ts <16 " ;
	 		 sql +=" AND (w.roleid =21 OR w.roleid=50)  ";
	 	  }else if(51==maproleid) {
	 		 sql += " and j.yuq_ts >15 and j.yuq_ts <=60 " ;
	 		 sql +=" AND (w.roleid =24 OR w.roleid=51) ";
	 	  }else if(54==maproleid) {
	 		 sql += " and j.yuq_ts >45 " ;
	 		 sql +=" AND (w.roleid =26 OR w.roleid=54) ";
	 	  }else if(20==maproleid) {
	 		 sql += " and j.yuq_ts >0 and j.yuq_ts <4 " ;
	 		 sql +=" AND (w.roleid =19 OR w.roleid=20) ";
	 	  }else if(61==maproleid) {
	 		 sql += " and j.yuq_ts >45 " ;
	 		 sql +=" AND (w.roleid =60 OR w.roleid=61) ";
	 	  }else if(63==maproleid) {
		 		 sql +=" AND (w.roleid =60 OR w.roleid=61 or w.roleid=62 OR  w.roleid=63 OR  w.roleid =26 OR w.roleid=54) ";
		  }
		
		
//		if(cmsuserid == 8 || cmsuserid == 888 || cmsuserid == 222 || cmsuserid == 6 || cmsuserid == 2016 ||cmsuserid==9999 ||cmsuserid == 8888){
//			for (int i = 0; i < cuishouzuyqm1.length; i++) {
//				if(i == 0){
//					sql +=" and (j.cuishou_id=2016 or j.cuishou_id=" + cuishouzuyqm1[0];
//				}else if(i>0 && i<(cuishouzuyqm1.length-1)){
//					sql +=" or j.cuishou_id=" + cuishouzuyqm1[i];
//				}else{
//					sql +=" or j.cuishou_id=" + cuishouzuyqm1[cuishouzuyqm1.length-1] + " )";
//				}
//			}
//		}else{
//			sql +=" and j.cuishou_id="+cmsuserid;
//		}
		sql += " order by LENGTH(j.sjsh_money)>8 DESC,j.sjsh_money>='4,000,000' DESC,j.yuq_ts, j.yuq_lx ";

		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	
	public List<DataRow> getYqZuList0(String userId,
			String realName, String phone, String startDate, String endDate,
			String commit, String idCard, String hkstat, String et_day,
			String st_day,int cmsuserid,String cuishouid,int maproleid,String jkdate,String yuqts) throws Exception {

		String sql = " select j.yuq_lx ,j.sjsh_money from  (select id ,yuq_ts ,jk_date,userid ,yuq_lx , "
				+ "  sjsh_money,cuishou_id,fkr_time,hkyq_time,zxcsbz,hkfq_time,hk_time,hkfq_code,hkqd,sfyhw,hkstate from  sd_new_jkyx where yuq_ts > 0  ) j left join sd_user u on "
				+ "  j.userid = u.id left join sd_user_finance s on s.userId =j.userid left join sdcms_user w on w.USER_ID = j.cuishou_id where j.sfyhw = 0 and j.hkfq_code=0";
		
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  s.realname like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {
			
			sql += " and  s.idno='" + idCard + "'";
		}
		if (!StringHelper.isEmpty(cuishouid)) {
			
			sql += " and  j.cuishou_id=" + cuishouid;
		}
		if (!StringHelper.isEmpty(yuqts)) {
			
			sql += " and  j.yuq_ts=" + yuqts;
		}
		if (!StringHelper.isEmpty(jkdate)) {
			
			sql += " and  (j.jk_date=3 or j.jk_date=4) ";
		}
		if (!StringHelper.isEmpty(et_day) && !et_day.equals("0")) {

			sql += " and  j.yuq_ts >= " + et_day;
		}
		if (!StringHelper.isEmpty(st_day) && !st_day.equals("0")) {

			sql += " and  j.yuq_ts <= " + st_day;
		}
		if(50==maproleid) {
	 		 sql += " and j.yuq_ts >3 and j.yuq_ts <16 " ;
	 		 sql +=" AND (w.roleid =21 OR w.roleid=50)  ";
	 	  }else if(51==maproleid) {
	 		 sql += " and j.yuq_ts >15 and j.yuq_ts <=60 " ;
	 		 sql +=" AND (w.roleid =24 OR w.roleid=51) ";
	 	  }else if(54==maproleid) {
	 		 sql += " and j.yuq_ts >45 " ;
	 		 sql +=" AND (w.roleid =26 OR w.roleid=54) ";
	 	  }else if(20==maproleid) {
	 		 sql += " and j.yuq_ts >0 and j.yuq_ts <4 " ;
	 		 sql +=" AND (w.roleid =19 OR w.roleid=20) ";
	 	  }else if(61==maproleid) {
	 		 sql += " and j.yuq_ts >45 " ;
	 		 sql +=" AND (w.roleid =60 OR w.roleid=61) ";
	 	  }else if(63==maproleid) {
		 		 sql +=" AND (w.roleid =60 OR w.roleid=61 or w.roleid=62 OR  w.roleid=63 OR  w.roleid =26 OR w.roleid=54) ";
		  }
//		if(cmsuserid == 8 || cmsuserid == 888 || cmsuserid == 222 || cmsuserid == 6 || cmsuserid == 2016||cmsuserid == 2016 ||cmsuserid ==9999||cmsuserid == 8888){
//			for (int i = 0; i < cuishouzuyqm1.length; i++) {
//				if(i == 0){
//					sql +=" and (j.cuishou_id=2016 or j.cuishou_id=" + cuishouzuyqm1[0];
//				}else if(i>0 && i<(cuishouzuyqm1.length-1)){
//					sql +=" or j.cuishou_id=" + cuishouzuyqm1[i];
//				}else{
//					sql +=" or j.cuishou_id=" + cuishouzuyqm1[cuishouzuyqm1.length-1] + " )";
//				}
//			}
//		}else{
//			sql +=" and j.cuishou_id="+cmsuserid;
//		}
		sql += " order by LENGTH(j.sjsh_money)>8 DESC,j.sjsh_money>='4,000,000' DESC,j.yuq_ts, j.yuq_lx ";

		return getJdbcTemplate().query(sql);
	}
	//xiong20190614
		public Integer getYQTS(int jkid) {
	        StringBuffer sb = new StringBuffer();
	        sb.append("select yuq_ts from sd_new_jkyx  where id = " + jkid);
	        
	        return getJdbcTemplate().queryInt(sb.toString());
	    }
		
		//xiong20190614	
		public void insertUserKFMsgHMD(DataRow row) {
	        getJdbcTemplate().insert("sd_returnvisit_hmd", row);
	    }
		public void insertUserKFMsgSL(DataRow row) {
			getJdbcTemplate().insert("sd_returnvisit_shilian", row);
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
		
		/**
		 * xiong查询员工sdcms_user
		 *
		 */
		
		public DataRow getSdcmsUser(int a){
			String sql ="select roleid FROM sdcms_user  WHERE USER_ID="+a;	
		    return getJdbcTemplate().queryMap(sql);
		}

	//**********************************xiong20190614********end****************************************
	public DBPage getFindOrder(int curPage, int numPerPage, String phone,
			String orderId, String realName, String statuc, String userId)
			throws Exception {

		String sql = "  select w.id ,w.name, INSERT(w.cellPhone,4,4,'****')AS cellPhone  ,w.poundage , w.applyTime , w.sum , w.status ,w.userId ,INSERT(w.acount,4,4,'****')AS acount ,sd_user.usableSum ,sd_bankcard.remark as remark3  from sd_withdraw w "
				+ "left join sd_user on sd_user.id =w.userid left join sd_bankcard on sd_bankcard.userid =w.userid where w.remark ='提现'  and 1 =1  ";

		if (!StringHelper.isEmpty(phone)) {

			sql += " and w.cellPhone= '" + phone + "'";
		}
		if (!StringHelper.isEmpty(statuc)) {

			sql += " and w.status= " + statuc;
		}
		if (!StringHelper.isEmpty(orderId)) {

			sql += " and w.orderid ='" + orderId + "'";
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and w.name like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(userId)) {

			sql += " and w.userId  =" + userId;
		}
		sql += " order by  applyTime desc";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DataRow getWidthinfo(int txid) {
		String sql = " select w.id ,w.status ,w.userid ,w.cellPhone as mobilephone , w.sum ,w.checkTime ,"
				+ "w.remarkResult, sd_bankcard.bankbs , sd_bankcard.remark ,"
				+ "sd_bankcard.cardNo ,sd_bankcard.cardUserName ,w.orderid  from sd_withdraw w  left join  sd_bankcard on sd_bankcard.userId =w.userid where w.id = "
				+ txid;

		return getJdbcTemplate().queryMap(sql);
	}

	public void updateWithdrawTx(DataRow row) throws Exception {

		getJdbcTemplate().update("sd_withdraw", row, "id", row.getString("id"));
	}

	public DataRow getUserInfo(String txid) {
		String sql = "select id ,usableSum ,freezeSum  from sd_user where id="
				+ txid;
		return getJdbcTemplate().queryMap(sql);
	}

	public DataRow getUserTx(int userid) {
		//select u.id ,u.usableSum ,b.cardUserName ,u.mobilePhone,sd_user_finance.idno,u.vipType from sd_user u  left join sd_bankcard b on u.id =b.userid left join sd_user_finance on sd_user_finance.userId=u.id where u.id=8888;
		 
		String sql = "select u.id ,u.usableSum ,b.cardUserName ,u.mobilePhone,sd_user_finance.idno,u.vipType from sd_user u  left join sd_bankcard b on u.id =b.userid left join sd_user_finance on sd_user_finance.userId=u.id where u.id="
				+ userid;
		return getJdbcTemplate().queryMap(sql);
	}

	public void updateUserInfo(DataRow row) throws Exception {

		getJdbcTemplate().update("sd_user", row, "id", row.getString("id"));
	}
	public void updateUserWJDH(DataRow row) throws Exception {
		
		getJdbcTemplate().update("sd_new_jkyx", row, "id", row.getString("id"));
	}

	public void insertUserMsg(DataRow row) {
		getJdbcTemplate().insert("sd_msg", row);
	}

	public void insertZmrz(DataRow row) {
		getJdbcTemplate().insert("sd_zmrz", row);
	}

	public DBPage getFindByuid(int curPage, int numPerPage, int userId,
			String phone) throws Exception {

		String sql = " select w.id , w.mobilePhone ,h.cardUserName ,h.remark ,w.vipCreateTime from sd_user w left join "
				+ "   sd_bankcard h on h.userid = w.id where  w.yhbd =1 and w.refferee in('"
				+ userId + "','" + phone + "')     ";

		sql += " order by  w.vipCreateTime desc";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	/**
	 * 查询推荐借款次数及总借款金额
	 * @param phone
	 * @return
	 */
	public DataRow getTuiJianCountJkAnSumMoney(int userId,String phone,String startDate,String endDate){
		String sql="select count(j.id) as count,sum(j.sjsh_money) as sum from sd_new_jkyx j left join sd_user u on j.userid = u.id  where  j.sfyfk =1 ";
		
		if(!StringHelper.isEmpty(startDate)){
			sql +=" and j.fkdz_time  >'" +startDate+"'" ;
		}
		if(!StringHelper.isEmpty(endDate)){
			sql +=" and j.fkdz_time  <'" +endDate+"'" ;
		}
		sql+=" and u.refferee in('"+ userId + "','" + phone + "') ";
		return this.getJdbcTemplate().queryMap(sql);
	}
	public String getUserPhone(int userid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select mobilePhone from sd_user  where id=" + userid);
		return getJdbcTemplate().queryString(sb.toString());
	}

	public DBPage getFindOrderList(int curPage, int numPerPage, String phone,
			String orderId, String realName, String statuc, String userId)
			throws Exception {

		String sql = "  select w.id ,w.name, INSERT(w.cellPhone,4,4,'****')AS cellPhone  , w.checkTime , w.sum ,w.orderid, w.status ,w.userId ,INSERT(w.acount,4,4,'****')AS acount ,sd_user.usableSum ,sd_bankcard.remark as remark3  from sd_withdraw w "
				+ "left join sd_user on sd_user.id =w.userid left join sd_bankcard on sd_bankcard.userid =w.userid where w.remark ='提现'  and 1 =1  ";

		if (!StringHelper.isEmpty(phone)) {

			sql += " and w.cellPhone= '" + phone + "'";
		}
		if (!StringHelper.isEmpty(statuc)) {

			sql += " and w.status= " + statuc;
		}
		if (!StringHelper.isEmpty(orderId)) {

			sql += " and w.orderid ='" + orderId + "'";
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and w.name like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(userId)) {

			sql += " and w.userId  =" + userId;
		}
		sql += " order by  applyTime desc";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public Integer getCountYq(int userid, String phone,String startDate,String endDate) {
		String sql ="select count(*) from sd_user where refferee in('"+ userid + "','" + phone + "') ";
		if(!StringHelper.isEmpty(startDate)){
			sql +=" and createTime  >'" +startDate+"'" ;
		}
		if(!StringHelper.isEmpty(endDate)){
			sql +=" and createTime  <'" +endDate+"'" ;
		}
		return getJdbcTemplate().queryInt(sql);
	}

	public Integer getCountRzR(int userid, String phone,String startDate,String endDate) {
		
		String sql="select count(*) from sd_user where  yhbd=1  and refferee in('"+ userid + "','" + phone + "') ";
		if(!StringHelper.isEmpty(startDate)){
			sql +=" and vipCreateTime >'" +startDate+"'" ;
		}
		if(!StringHelper.isEmpty(endDate)){
			sql +=" and vipCreateTime  <'" +endDate+"'" ;
		}
		return getJdbcTemplate().queryInt(sql);
	}

	
	public Integer getCountYq2(int userid, String phone) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(*) from sd_user where refferee in(" + userid
				+ ",'" + phone + "')");

		return getJdbcTemplate().queryInt(sb.toString());
	}

	public Integer getCountRzR2(int userid, String phone) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(*) from sd_user where  yhbd=1  and refferee in("
				+ userid + ",'" + phone + "')");
		return getJdbcTemplate().queryInt(sb.toString());
	}

	/**
	 * 查找所有的推荐人
	 * @return
	 */
	public DBPage getAllTuijianMan(int curPage, int numPerPage, String phone)
			throws Exception {
		String sql = "   select userId from sd_fundrecord inner join sd_user on sd_user.id=sd_fundrecord.userId  where operateType=66  ";
		if (!StringHelper.isEmpty(phone)) {
			sql += " and sd_user.mobilePhone='" + phone + "'";
		}
		sql += "   group by userId order  by recordTime  desc  ";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	
	/**
	 * 根据ID查找他的详细信息
	 * @param userid
	 * @return
	 */
	public DataRow getTuiJianManDetail(String userid){
		String sql="select sd_user.id uid,realName,INSERT(mobilePhone,4,4,'****')AS mobilePhone,sd_user.usableSum from sd_user left join sd_user_finance on sd_user.id=sd_user_finance.userId where sd_user.id=?  or mobilePhone=?";
		Object[] obj=new Object[]{userid,userid};
		return this.getJdbcTemplate().queryMap(sql, obj);
	}
	
	/**
	 * 得到总推荐认证金额已提现金额
	 * @param phone
	 * @return
	 */
	public int getAllTuiRZmoneyYT(String phone){
		String sql="select ifNull (SUM(sum),0) from sd_withdraw left join sd_user u on u.id=sd_withdraw.userid where  remark ='提现'  and status  not in('3','5')";
		if (!StringHelper.isEmpty(phone)) {
			sql += " and u.mobilePhone = '" + phone + "'";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}
	
	public int getAllTuiRZmoney(String phone){
		String sql="select sum(handleSum) from sd_fundrecord left join sd_user u on sd_fundrecord.userid = u.id where operateType=66 ";
		if (!StringHelper.isEmpty(phone)) {
			sql += " and  u.mobilePhone = '" + phone + "'";
		}
		return this.getJdbcTemplate().queryInt(sql);
	}


	public Long getDoubleYt(int userid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select ifNull (SUM(sum),0)from sd_withdraw where  remark ='提现'  and status  not in('3','5') and userid =" + userid);

		return getJdbcTemplate().queryLong(sb.toString());
	}

	public DBPage getRecordListChg(int curPage, int numPerPage, String userId,
			String realName, String phone, String startDate, String endDate,
			String commit, String username, String hkstat,String off) throws Exception {
	
		String sql = "select j.id as jkid, u.id as id ,f.realName , INSERT(u.mobilePhone,4,4,'****')AS mobilePhone ,u.username, j.jk_money, j.jk_date ,j.sjsh_money ,j.sjds_money ,j.sfyfk, j.fkdz_time,j.hkyq_time,j.sfyhw ,j.yuq_lx ,j.hk_time from "
				+ "   sd_new_jkyx j left join sd_user u on j.userid = u.id LEFT join sd_user_finance f on f.userId =j.userid  where  j.sfyfk = 1 ";
		if (!StringHelper.isEmpty(userId)) {
	
			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
	
			sql += " and  f.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(username)) {
	
			sql += " and  f.idNo like " + username ;
		}
		if(!StringHelper.isEmpty(off) && off.equals("5")) {
			sql += " and u.username like 'TAFA%'";
		}
		if (hkstat.equals("1")) {
	
			sql += " and  j.sfyhw = 1";
		}
	
		if (hkstat.equals("2")) {
	
			sql += " and  j.sfyhw = 0 ";
		}
		if (!StringHelper.isEmpty(startDate)) {
	
			sql += " and (substring(j.hk_time,1,10) ='" + startDate + "'";
		}
		if (!StringHelper.isEmpty(endDate)) {
	
			sql += " or substring(j.hk_time,1,10) ='" + endDate + "')";
		}
	
		sql += " order by  SUBSTRING(j.fkdz_time,7,4) DESC ,SUBSTRING(j.fkdz_time,4,2) DESC , SUBSTRING(j.fkdz_time,1,2) DESC ,SUBSTRING(j.fkdz_time,12,2) DESC,SUBSTRING(j.fkdz_time,15,2) DESC,SUBSTRING(j.fkdz_time,18,2) DESC ";
	
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getRecordJRList(int curPage, int numPerPage, String userId,
			String realName, String phone, String startDate, String endDate,String yue,String ri,
			String lastri,String commit, String username, String hkstat,int cmsuserid,String jkdate) throws Exception {

		String sql = "select j.id as jkid, u.id as id ,f.realName , u.mobilePhone,u.username, j.jk_money, j.jk_date ,j.sjsh_money ,j.sjds_money,j.lx , j.sfyfk, j.fkdz_time,j.hkyq_time,j.hkfq_time,j.hkfq_code,j.sfyhw ,j.yuq_lx ,j.cuishou_id,h.create_time ,h.msg, j.hk_time from "
				+ "   sd_new_jkyx j left join sd_user u on j.userid = u.id left join ( select * from (select * from  sd_csmsg  where msgtype ='提醒' order by create_time desc ) `temp`  group by jkid order by create_time desc )h  on h.jkid =j.id LEFT join sd_user_finance f on f.userId =j.userid  where  j.sfyfk <>0 and j.sfyhw=0 ";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  f.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(username)) {

			sql += " and  f.idNo like " + username ;
		}
		if (!StringHelper.isEmpty(jkdate)) {
			
			sql += " and  (j.jk_date=3 or j.jk_date=4) ";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and ((CONCAT(SUBSTRING(j.hkyq_time,7,4),'-',SUBSTRING(j.hkyq_time,4,2),'-',SUBSTRING(j.hkyq_time,1,2),SUBSTRING(j.hkyq_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(j.hkyq_time,7,4),'-',SUBSTRING(j.hkyq_time,4,2),'-',SUBSTRING(j.hkyq_time,1,2)) <='" + endDate + "')";
			
			sql += " or (CONCAT(SUBSTRING(j.hkfq_time,7,4),'-',SUBSTRING(j.hkfq_time,4,2),'-',SUBSTRING(j.hkfq_time,1,2),SUBSTRING(j.hkfq_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(j.hkfq_time,7,4),'-',SUBSTRING(j.hkfq_time,4,2),'-',SUBSTRING(j.hkfq_time,1,2)) <='" + endDate + "'))";
		
	}
//		if(cmsuserid != 8 && cmsuserid != 888 && cmsuserid != 8888 && cmsuserid != 6 && cmsuserid != 6868 && cmsuserid != 8015 && cmsuserid !=8023){
		if(!roleauthoritymangement.getRoleAM_JRlist(cmsuserid+"")){
			sql += " and j.cuishou_id="+cmsuserid;
		}
		
		sql += " order by   str_to_date(fkdz_time,'%d-%m-%Y %H:%i:%s')"; 
		//sql += " order by  j.hkyq_time ";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public DBPage getRecordListShibALL(int curPage, int numPerPage, String userId,
			String realName, String phone, String commit, String idCard,String cmsid,String xsgz,String startDate,String endDate)
			throws Exception {
		String sql = "select j.id as jkid, u.id as id ,f.realName , INSERT(u.mobilePhone,4,4,'****')AS mobilePhone  ,INSERT(f.idNo,4,4,'****')AS idNo,u.refferee,u.profession, j.jk_money, j.create_date ,j.cl_status ,j.cl_yj  , s.name ,"
				+ "j.cl_time as cl_time ,j.cl_ren,j.cl02_time,j.cl02_ren,j.cl02_status,j.cl02_yj,j.cl03_time,j.cl03_ren,j.cl03_status,j.cl03_yj , j.pipei from sd_new_jkyx j left join sd_user u on j.userid = u.id LEFT join sd_user_finance f on f.userId =j.userid left join sdcms_user s on s.USER_ID = j.cl_ren and s.USER_ID = j.cl02_ren and s.USER_ID = j.cl03_ren  where  (j.cl_status=3 or j.cl02_status=3 or j.cl03_status=3) ";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  f.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {
			
			sql += " and  j.pipei = " + idCard ;
		}
		if (!StringHelper.isEmpty(xsgz)) {
			
			sql += " and  u.profession = " + xsgz ;
		}
		if (!StringHelper.isEmpty(startDate)) {

			sql += " and (((substring(j.cl_time,1,10) >='" + startDate + "'";
			sql += " and substring(j.cl_time,4,2) >='" + startDate.substring(3, 5) + "')";
			sql += " or  (substring(j.cl02_time,1,10) >='" + startDate + "'";
			sql += " and  substring(j.cl02_time,4,2) >='" + startDate.substring(3, 5) + "')";
			sql += " or (substring(j.cl03_time,1,10) >='" + startDate + "'" + " ";
			sql += " and substring(j.cl03_time,4,2) >='" + startDate.substring(3, 5) + "'" + ")) ";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and ((substring(j.cl_time,1,10) <='" + endDate + "'";
			sql += " and substring(j.cl_time,4,2) <='" + endDate.substring(3, 5) + "')";
			sql += " or (substring(j.cl02_time,1,10) <='" + endDate + "'";
			sql += " and substring(j.cl02_time,4,2) <='" + endDate.substring(3, 5) + "')";
			sql += " or (substring(j.cl03_time,1,10) <='" + endDate + "'" + " ";
			sql += " and substring(j.cl03_time,4,2) <='" + endDate.substring(3, 5) + "'" + "))) ";
		}
		if (!StringHelper.isEmpty(cmsid)) {
			
			sql += " and  (j.cl_ren=" + cmsid ;
			sql += " or  j.cl02_ren=" + cmsid ;
			sql += " or  j.cl03_ren=" + cmsid + ")" ;
		}
		sql += " order by  SUBSTRING(cl_time,7,4) DESC ,SUBSTRING(cl_time,4,2) DESC , SUBSTRING(cl_time,1,2) DESC ,SUBSTRING(cl_time,12,2) DESC,SUBSTRING(cl_time,15,2) DESC,SUBSTRING(cl_time,18,2) DESC ";

		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);

	}
	public DBPage getRecordListShib(int curPage, int numPerPage, String userId,
			String realName, String phone, String commit, String idCard,String cmsid,String pingfen)
			throws Exception {
		String sql = "select j.id as jkid, u.id as id ,u.username,f.realName , INSERT(u.mobilePhone,4,4,'****')AS mobilePhone  ,INSERT(f.idNo,4,4,'****')AS idNo,u.refferee, j.jk_money, j.create_date ,j.cl_status ,j.cl_yj  , s.name ,"
				+ "j.cl_time as cl_time ,j.pipei,j.cl_ren from sd_new_jkyx j left join sd_user u on j.userid = u.id LEFT join sd_user_finance f on f.userId =j.userid left join sdcms_user s on s.USER_ID = j.cl_ren  where  j.cl_status=3 and j.cl_ren<>'888' ";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(pingfen)) {
			
			sql += " and  j.pipei =" + pingfen;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  f.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {

			sql += " and  f.idNo like '%" + idCard + "%'";
		}
		if (!StringHelper.isEmpty(cmsid)) {
			
			sql += " and  j.cl_ren=" + cmsid ;
		}
		sql += " order by  SUBSTRING(cl_time,7,4) DESC ,SUBSTRING(cl_time,4,2) DESC , SUBSTRING(cl_time,1,2) DESC ,SUBSTRING(cl_time,12,2) DESC,SUBSTRING(cl_time,15,2) DESC,SUBSTRING(cl_time,18,2) DESC ";

		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);

	}
	public DBPage getRecordListShib2(int curPage, int numPerPage,
			String userId, String realName, String phone, String commit,
			String idCard,String cmsid) throws Exception {
		String sql = "select j.id as jkid, u.id as id ,u.username,f.realName ,INSERT(u.mobilePhone,4,4,'****')AS mobilePhone  ,INSERT(f.idNo,4,4,'****')AS idNo, j.jk_money, j.create_date ,j.cl_status ,j.cl_yj  , s.name ,"
				+ "j.cl_time  ,j.cl02_yj ,j.cl02_ren ,j.cl_ren ,j.cl03_ren , j.cl02_time as cl02_time  ,j.cl03_yj ,j.cl03_status ,j.cl03_time from sd_new_jkyx j left join sd_user u on j.userid = u.id LEFT join sd_user_finance f on f.userId =j.userid left join sdcms_user s on s.USER_ID = j.cl02_ren  where cl02_status =3 and 1 =1 ";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  f.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {

			sql += " and  f.idNo like '%" + idCard + "%'";
		}
		if (!StringHelper.isEmpty(cmsid)) {
			
			sql += " and  j.cl02_ren=" + cmsid ;
		}

		sql += " order by  SUBSTRING(cl02_time,7,4) DESC ,SUBSTRING(cl02_time,4,2) DESC , SUBSTRING(cl02_time,1,2) DESC ,SUBSTRING(cl02_time,12,2) DESC,SUBSTRING(cl02_time,15,2) DESC,SUBSTRING(cl02_time,18,2) DESC ";

		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);

	}

	public DBPage getRecordListShib3(int curPage, int numPerPage,
			String userId, String realName, String phone, String commit,
			String idCard,String cmsid) throws Exception {
		String sql = "select j.id as jkid, u.id as id ,f.realName , INSERT(u.mobilePhone,4,4,'****')AS mobilePhone  ,INSERT(f.idNo,4,4,'****')AS idNo, j.jk_money, j.create_date ,j.cl_status ,j.cl_yj  , s.name ,"
				+ "j.cl_time  ,j.cl02_yj ,j.cl02_ren ,j.cl_ren ,j.cl03_ren , j.cl02_time  ,j.cl03_yj ,j.cl03_status ,j.cl03_time as cl03_time from sd_new_jkyx j left join sd_user u on j.userid = u.id LEFT join sd_user_finance f on f.userId =j.userid left join sdcms_user s on s.USER_ID = j.cl03_ren  where  cl03_status =3 and 1 =1 ";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  f.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {

			sql += " and  f.idNo like '%" + idCard + "%'";
		}
		if (!StringHelper.isEmpty(cmsid)) {
			
			sql += " and  j.cl03_ren=" + cmsid ;
		}

		sql += " order by  SUBSTRING(cl03_time,7,4) DESC ,SUBSTRING(cl03_time,4,2) DESC , SUBSTRING(cl03_time,1,2) DESC ,SUBSTRING(cl03_time,12,2) DESC,SUBSTRING(cl03_time,15,2) DESC,SUBSTRING(cl03_time,18,2) DESC ";

		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);

	}

	public List<DataRow> getAllFKCxList() {
		String sql = "select * from sd_bankcard  where cardStatus =1  and  bankbs is null  ";
		return getJdbcTemplate().query(sql);
	}

	public void updateBankInfo(DataRow row) throws Exception {

		getJdbcTemplate().update("sd_bankcard", row, "id", row.getString("id"));
	}

	public DBPage getYqList(int curPage, int numPerPage, String userId,
			String realName, String phone, String startDate, String endDate,
			String commit, String cuishouname,String cuishouid,int bfhk,int yqhk, String hkstat,String state,int maproleid) throws Exception {

		String sql = "select j.id as jkid, u.id as id ,f.cardUserName ,s.realname, u.mobilePhone,u.username ,f.cardNo,  j.jk_date ,j.sjsh_money ,j.cl03_time , j.sjds_money ,j.tzjx, j.sfyfk, j.fkdz_time,j.hkyq_time,j.sfyhw ,"
				+ "    j.yuq_lx ,j.yuq_yhlx ,j.hkfq_lx ,j.hkfqzlx ,j.hkfq_time ,j.yuq_ts ,j.hk_time ,j.cuishou_id,q.name,j.zxcsbz as msg from sd_new_jkyx j left join sd_user u on j.userid = u.id left join sd_user_finance s on s.userid = j.userid  LEFT join sd_bankcard f on f.userId =j.userid left join sdcms_user q on "
				+ "    q.USER_ID = j.cuishou_id where  j.sfyfk <>0 and j.yuq_ts > 0 and j.hkfq_code=0  ";
	
 		if(!StringHelper.isEmpty(userId)){	
 			
 		   sql +=" and  u.id ="+ userId ;
 		}
 		if(!StringHelper.isEmpty(realName)){	
 			
 			   sql +=" and  f.cardUserName like'%"+ realName +"%'";
 		  }
 		if(!StringHelper.isEmpty(phone)){	
 			  
 			   sql +=" and  u.mobilePhone like '%"+phone +"%'";
 			}
 		
 		if(!StringHelper.isEmpty(cuishouname)){	
 			
 			sql +=" and  q.name like '%"+cuishouname +"%'";
 		}
 		if(!StringHelper.isEmpty(cuishouid)){	
 			
  		   sql +=" and  j.cuishou_id ="+ cuishouid ;
  		}
 		if(!StringHelper.isEmpty(state)){	
 			
 			sql +=" and  q.state ="+ state ;
 		}
 		if(bfhk == 1){	
 			
 			sql +=" and  j.yuq_yhlx <>0";
 		}
 		if(yqhk == 1){	
 			
 			sql +=" and  j.hkfq_lx <>0";
 		}
 		if(!StringHelper.isEmpty(hkstat)&&!hkstat.equals("0") ){	 //查询还款状态
			   if(!hkstat.equals("2")){
			       sql +=" and  j.sfyhw ="+hkstat ;
			   } else{
				   sql +=" and  j.sfyhw = 0" ;				   
			   }
	     }
 	    
 	   if(!StringHelper.isEmpty(startDate)){
			
           sql +=" and (substring(j.hkyq_time,1,10) ='" +startDate+"'" ;
		}
 	  if(!StringHelper.isEmpty(endDate)){
			
          sql +=" or substring(j.hkyq_time,1,10) ='" +endDate+"')" ;
		}
 	  //xiong_20190810_
 	  if(50==maproleid) {
 		 sql +=" AND (q.roleid =21 OR q.roleid=50)  ";
 	  }else if(51==maproleid) {
 		 sql +=" AND (q.roleid =24 OR q.roleid=51)";
 	  }else if(54==maproleid) {
 		 sql +=" AND (q.roleid =26 OR q.roleid=54) ";
 	  }
 	  
		sql += " order by  j.yuq_ts desc";
		
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
		
	}

	public DataRow getUserYqInfo(int jkid) {
		String sql = " select j.id as jkid, f.cardUserName , u.mobilePhone ,u.username ,j.hkyq_time,j.sjsh_money ,"
				+ "    j.yuq_lx ,j.yuq_ts  from sd_new_jkyx j left join sd_user u on j.userid = u.id LEFT join sd_bankcard f on f.userId =j.userid "
				+ "    where  j.id= " + jkid;
		return getJdbcTemplate().queryMap(sql);
	}
	public DataRow getUserYqInfoYX(int userid) {
		String sql = " select mobilePhone,username from sd_user where id= " + userid;
		return getJdbcTemplate().queryMap(sql);
	}
	public DataRow getUserYqShenFen(int userid) {
		String sql = "select * from sd_user_finance where userid="+userid;
		return getJdbcTemplate().queryMap(sql);
	}

	public void insertUserChMsg(DataRow row) {
		getJdbcTemplate().insert("sd_csmsg", row);
	}
	
	public void insertUserKFMsg(DataRow row) {
		getJdbcTemplate().insert("sd_returnvisit", row);
	}
	

	public Integer getmsgCount(int userid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(*) from sd_csmsg where msgtype='手机短信' and  userid ="
				+ userid + " and  to_days(create_time) = to_days(now()) ");

		return getJdbcTemplate().queryInt(sb.toString());
	}
	public Integer getmsgCountYX(int userid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(*) from sd_csmsg where msgtype='营销短信' and  userid ="
				+ userid + " and  to_days(create_time) = to_days(now()) ");
		
		return getJdbcTemplate().queryInt(sb.toString());
	}

	public DBPage getYqNList(int curPage, int numPerPage, String userId,
			String realName, String phone,String cuishou,String shenheid, String startDate, String endDate,
			String commit, String idCard, String hkstat, String et_day,
			String st_day) throws Exception {

		String sql = " select j.userid, j.id as jkid2 ,u.username, u.mobilePhone ,f.cardUserName ,s.realname,f.cardNo , q.name as shname ,w.name as csname , j.yuq_lx ,j.yuq_ts ,j.sjsh_money,j.create_date ,j.zxcsbz as msg from  (select id ,yuq_ts ,userid ,cl02_ren,cl03_ren ,yuq_lx , "
				+ "  sjsh_money,zxcsbz,create_date,cl_ren from  sd_new_jkyx where sfyfk <>0 and yuq_ts > 0 and sfyhw= 0 ) j left join sd_user u on "
				+ "  j.userid = u.id left join sd_bankcard f on f.userId =j.userid left join sd_user_finance s on s.userId =j.userid left join sdcms_user q on q.USER_ID = j.cl03_ren left join sdcms_user w on w.USER_ID = j.cl_ren where 1 =1    ";
		
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  f.cardUserName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(cuishou)) {

			sql += " and  w.user_id=" + cuishou;
		}
		if (!StringHelper.isEmpty(shenheid)) {
			
			sql += " and  j.cl02_ren ='" + shenheid + "'";
		}
		if (!StringHelper.isEmpty(et_day) && !et_day.equals("0")) {

			sql += " and  j.yuq_ts >= " + et_day;
		}
		if (!StringHelper.isEmpty(st_day) && !st_day.equals("0")) {

			sql += " and  j.yuq_ts <= " + st_day;
		}

		sql += " order by j.yuq_ts ";
		
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	
	}
	public DBPage getYqNListALL1(int curPage, int numPerPage, String userId,
			String realName, String phone,String cuishou, String startDate, String endDate,
			String commit, String idCard, String hkstat, String et_day,
			String st_day) throws Exception {

		String sql = " select j.userid, j.id as jkid2 ,u.username, u.mobilePhone ,f.cardUserName ,s.realname,f.cardNo , q.name as shname ,w.name as csname , j.yuq_lx ,j.yuq_ts ,j.sjsh_money,h.create_time ,j.zxcsbz as msg from  (select id ,yuq_ts ,userid ,cl02_ren,cl03_ren ,yuq_lx , "
				+ "  sjsh_money,zxcsbz from  sd_new_jkyx where sfyfk <>0 and yuq_ts > 0 and sfyhw= 0 ) j left join sd_user u on "
				+ "  j.userid = u.id left join sd_bankcard f on f.userId =j.userid left join sd_user_finance s on s.userId =j.userid left join sdcms_user q on q.USER_ID = j.cl03_ren left join sdcms_user w on w.USER_ID = h.cl_ren where 1 =1    ";
		
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  f.cardUserName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(cuishou)) {

			sql += " and  w.name like '%" + cuishou + "%'";
		}
		if (!StringHelper.isEmpty(et_day) && !et_day.equals("0")) {

			sql += " and  j.yuq_ts >= " + et_day;
		}
		if (!StringHelper.isEmpty(st_day) && !st_day.equals("0")) {

			sql += " and  j.yuq_ts <= " + st_day;
		}

		sql += " order by j.yuq_ts ";
		
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	
	}
	public DBPage getYqNListALL2(int curPage, int numPerPage, String userId,
			String realName, String phone,String cuishou, String startDate, String endDate,
			String commit, String idCard, String hkstat, String et_day,
			String st_day) throws Exception {

		String sql = " select j.userid, j.id as jkid2 ,u.username, u.mobilePhone ,f.cardUserName ,s.realname,f.cardNo , q.name as shname ,w.name as csname , j.yuq_lx ,j.yuq_ts ,j.sjsh_money,h.create_time ,j.zxcsbz as msg from  (select id ,yuq_ts ,userid ,cl02_ren,cl03_ren ,yuq_lx , "
				+ "  sjsh_money,zxcsbz from  sd_new_jkyx where sfyfk <>0 and yuq_ts > 0 and sfyhw= 0 ) j left join sd_user u on "
				+ "  j.userid = u.id left join sd_bankcard f on f.userId =j.userid left join sd_user_finance s on s.userId =j.userid left join sdcms_user q on q.USER_ID = j.cl03_ren left join sdcms_user w on w.USER_ID = h.cl_ren where 1 =1    ";
		
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  f.cardUserName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(cuishou)) {

			sql += " and  w.name like '%" + cuishou + "%'";
		}
		if (!StringHelper.isEmpty(et_day) && !et_day.equals("0")) {

			sql += " and  j.yuq_ts >= " + et_day;
		}
		if (!StringHelper.isEmpty(st_day) && !st_day.equals("0")) {

			sql += " and  j.yuq_ts <= " + st_day;
		}

		sql += " order by j.yuq_ts ";
		
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	
	}
	public DBPage getYqNListALL3(int curPage, int numPerPage, String userId,
			String realName, String phone,String cuishou, String startDate, String endDate,
			String commit, String idCard, String hkstat, String et_day,
			String st_day) throws Exception {

		String sql = " select j.userid, j.id as jkid2 ,u.username, u.mobilePhone ,f.cardUserName ,s.realname,f.cardNo , q.name as shname ,w.name as csname , j.yuq_lx ,j.yuq_ts ,j.sjsh_money,h.create_time ,j.zxcsbz as msg from  (select id ,yuq_ts ,userid ,cl02_ren,cl03_ren ,yuq_lx , "
				+ "  sjsh_money,zxcsbz from  sd_new_jkyx where sfyfk <>0 and yuq_ts > 0 and sfyhw= 0 ) j left join sd_user u on "
				+ "  j.userid = u.id left join sd_bankcard f on f.userId =j.userid left join sd_user_finance s on s.userId =j.userid left join sdcms_user q on q.USER_ID = j.cl03_ren left join sdcms_user w on w.USER_ID = h.cl_ren where 1 =1    ";
		
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  f.cardUserName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(cuishou)) {

			sql += " and  w.name like '%" + cuishou + "%'";
		}
		if (!StringHelper.isEmpty(et_day) && !et_day.equals("0")) {

			sql += " and  j.yuq_ts >= " + et_day;
		}
		if (!StringHelper.isEmpty(st_day) && !st_day.equals("0")) {

			sql += " and  j.yuq_ts <= " + st_day;
		}

		sql += " order by j.yuq_ts ";
		
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	
	}
	public DBPage getYqNListALL4(int curPage, int numPerPage, String userId,
			String realName, String phone,String cuishou, String startDate, String endDate,
			String commit, String idCard, String hkstat, String et_day,
			String st_day) throws Exception {

		String sql = " select j.userid, j.id as jkid2 ,u.username, u.mobilePhone ,f.cardUserName ,s.realname,f.cardNo , q.name as shname ,w.name as csname , j.yuq_lx ,j.yuq_ts ,j.sjsh_money,h.create_time ,j.zxcsbz as msg from  (select id ,yuq_ts ,userid ,cl02_ren,cl03_ren ,yuq_lx , "
				+ "  sjsh_money,zxcsbz from  sd_new_jkyx where sfyfk <>0 and yuq_ts > 0 and sfyhw= 0 ) j left join sd_user u on "
				+ "  j.userid = u.id left join sd_bankcard f on f.userId =j.userid left join sd_user_finance s on s.userId =j.userid left join sdcms_user q on q.USER_ID = j.cl03_ren left join sdcms_user w on w.USER_ID = h.cl_ren where 1 =1    ";
		
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  f.cardUserName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(cuishou)) {

			sql += " and  w.name like '%" + cuishou + "%'";
		}
		if (!StringHelper.isEmpty(et_day) && !et_day.equals("0")) {

			sql += " and  j.yuq_ts >= " + et_day;
		}
		if (!StringHelper.isEmpty(st_day) && !st_day.equals("0")) {

			sql += " and  j.yuq_ts <= " + st_day;
		}

		sql += " order by j.yuq_ts ";
		
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	
	}
	public DBPage getYqCSList(int curPage, int numPerPage, String userId,
			String realName, String phone, String startDate, String endDate,
			String commit, String idCard, String hkstat, String et_day,
			String st_day) throws Exception {

		String sql = " select j.userid, j.id as jkid2 ,u.username, u.mobilePhone ,f.cardUserName ,s.realname,f.cardNo , q.name as shname ,w.name as csname , j.yuq_lx ,j.yuq_ts ,j.fkr_time,j.sjsh_money ,j.zxcsbz as msg from  (select id ,yuq_ts ,userid ,cl03_ren ,yuq_lx , "
				+ "  fkr_time,sjsh_money,cuishou_id,zxcsbz from  sd_new_jkyx where sfyfk <>0 and yuq_ts > 0 and sfyhw= 0 ) j  left join sd_user u on "
				+ "  j.userid = u.id left join sd_bankcard f on f.userId =j.userid left join sd_user_finance s on s.userId =j.userid left join sdcms_user q on q.USER_ID = j.cl03_ren left join sdcms_user w on w.USER_ID = j.cuishou_id where ";

		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  f.cardUserName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {
			
			sql += " and  s.idno='" + idCard + "'";
		}
		
		sql += " j.yuq_ts >=7 " ;
		sql += " order by j.yuq_ts, j.yuq_lx ,j.sjsh_money ";

		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	
	public DBPage getYqKFList(int curPage, int numPerPage, String userId,
			String realName, String phone,String startDate, String endDate,
			String commit, String idCard, String hkstat, String et_day,
			String st_day) throws Exception {

		String sql = " select j.userid, j.id as jkid2 ,u.username, u.mobilePhone ,f.cardUserName ,s.realname,f.cardNo , q.name as shname ,w.name as csname , j.yuq_lx ,j.yuq_ts ,j.sjsh_money ,j.fkr_time,j.zxcsbz as msg from  (select id ,yuq_ts ,userid ,cl03_ren ,yuq_lx , "
				+ "  fkr_time,sjsh_money,cuishou_id,zxcsbz from  sd_new_jkyx where sfyfk <>0 and yuq_ts > 0 and sfyhw= 0 ) j  left join sd_user u on "
				+ "  j.userid = u.id left join sd_bankcard f on f.userId =j.userid left join sd_user_finance s on s.userId =j.userid left join sdcms_user q on q.USER_ID = j.cl03_ren left join sdcms_user w on w.USER_ID = j.cuishou_id where ";

		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  f.cardUserName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {
			
			sql += " and  s.idno='" + idCard + "'";
		}
		sql += " j.yuq_ts <4 " ;
		sql += " order by j.yuq_ts, j.yuq_lx ,j.sjsh_money ";

		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public List<DataRow> getAllcuishouM0(){
		String sql = "SELECT user_id FROM sdcms_user WHERE (roleid=19 or roleid=20) and state=1";
		return getJdbcTemplate().query(sql);
	}
	public List<DataRow> getAllcuishouM1(){
		 String sql = "SELECT user_id FROM sdcms_user WHERE (roleid=21 OR roleid=50)  and state=1 ";
		 return getJdbcTemplate().query(sql);
	 }
	public List<DataRow> getAllcuishouM2(){
		String sql = "SELECT user_id FROM sdcms_user WHERE roleid=22 and state=1";
		return getJdbcTemplate().query(sql);
	}
	public List<DataRow> getAllcuishouM3(){
		String sql = "SELECT user_id FROM sdcms_user WHERE roleid=23 and state=1";
		return getJdbcTemplate().query(sql);
	}
	public List<DataRow> getAllcuishouM4(){
		String sql = "SELECT user_id FROM sdcms_user WHERE roleid=25 and state=1";
		return getJdbcTemplate().query(sql);
	}
	public List<DataRow> getAllcuishouM5(){
		String sql = "SELECT user_id FROM sdcms_user WHERE (roleid=24 OR roleid=51) and state=1";
		return getJdbcTemplate().query(sql);
	}
	public List<DataRow> getAllcuishouM6(){
		String sql = "SELECT user_id FROM sdcms_user WHERE (roleid=26 OR roleid=54) and state=1";
		return getJdbcTemplate().query(sql);
	}
	public DBPage getYqM0List(int curPage, int numPerPage, String userId,
			String realName, String phone,String startDate, String endDate,
			String commit, String idCard, String hkstat, String et_day,
			String st_day,int cmsuserid,String cuishouid,int[] cuishouzuyqm1,String jkdate,String yuqts) throws Exception {
		
		String sql = " select j.userid, j.id as jkid2 ,u.username, u.mobilePhone,u.heihu_zt,s.realname,w.name as csname , j.yuq_lx ,j.hkfq_cishu,j.yuq_ts ,j.sjsh_money,j.lx,j.sjds_money ,j.hkyq_time,j.hkfq_code,j.hk_time,j.hkfq_time,j.jk_date,j.fkr_time,j.sfyhw,j.hkqd,j.hkstate,j.zxcsbz as msg from sd_new_jkyx j left join sd_user u on "
				+ "  j.userid = u.id left join sd_user_finance s on s.userId =j.userid left join sdcms_user w on w.USER_ID = j.cuishou_id where j.sfyhw = 0 and j.hkfq_code=0";
		
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and  s.realname like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {
			
			sql += " and  s.idno='" + idCard + "'";
		}
		if (!StringHelper.isEmpty(cuishouid)) {
			
			sql += " and  j.cuishou_id=" + cuishouid;
		}
        if (!StringHelper.isEmpty(yuqts)) {
			
			sql += " and  j.yuq_ts=" + yuqts;
		}
		if (!StringHelper.isEmpty(jkdate)) {
			
			sql += " and  (j.jk_date=3 or j.jk_date=4) ";
		}
		if (!StringHelper.isEmpty(et_day) && !et_day.equals("0")) {
			
			sql += " and  j.yuq_ts >= " + et_day;
		}
		if (!StringHelper.isEmpty(st_day) && !st_day.equals("0")) {
			
			sql += " and  j.yuq_ts <= " + st_day;
		}
		/*if (hkstat.equals("1")) {

			sql += " and  j.sfyhw = 1";
		}

		if (hkstat.equals("2")) {
		 */
		
		//}
		sql += " and j.yuq_ts >0 and j.yuq_ts <4 " ;
//		if(cmsuserid == 8 || cmsuserid == 888 || cmsuserid == 222 || cmsuserid == 6 || cmsuserid == 8 || cmsuserid == 53){
		if(roleauthoritymangement.getRoleAM_CSlist(cmsuserid+"")){
			for (int i = 0; i < cuishouzuyqm1.length; i++) {
				if(i == 0){
					sql +=" and (j.cuishou_id=" + cuishouzuyqm1[0];
				}else if(i>0 && i<(cuishouzuyqm1.length-1)){
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[i];
				}else{
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[cuishouzuyqm1.length-1] ;
				}
				
				if((cuishouzuyqm1.length-1) ==i) {
					sql+=" ) ";
				}
			}
		}else{
			sql +=" and j.cuishou_id="+cmsuserid;
		}
		sql += " order by LENGTH(j.sjsh_money)>8 DESC,j.sjsh_money>='4,000,000' DESC,j.yuq_ts, j.yuq_lx ";
		
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public DBPage getYqZYList(int curPage, int numPerPage, String userId,
			String realName, String phone,String startDate, String endDate,
			String commit, String idCard, String hkstat, String et_day,
			String st_day,int cmsuserid,String cuishouid,int[] cuishouzuyqm1,String jkdate,String yuqts) throws Exception {

		String sql = " select j.userid, j.id as jkid2 ,u.username, u.mobilePhone,u.heihu_zt,s.realname,w.name as csname , j.yuq_lx ,j.hkfq_cishu,j.yuq_ts ,j.sjsh_money ,j.hkyq_time,j.hkfq_code,j.hk_time,j.hkfq_time,j.jk_date,j.fkr_time,j.sfyhw,j.hkqd,j.hkstate,j.zxcsbz as msg,j.sjds_money,j.lx from sd_new_jkyx j left join sd_user u on "
				+ "  j.userid = u.id left join sd_user_finance s on s.userId =j.userid left join sdcms_user w on w.USER_ID = j.cuishou_id where j.sfyhw = 0 and j.hkfq_code=0";
		
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  s.realname like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {
			
			sql += " and  s.idno='" + idCard + "'";
		}
		if (!StringHelper.isEmpty(cuishouid)) {
			
			sql += " and  j.cuishou_id=" + cuishouid;
		}
		if (!StringHelper.isEmpty(yuqts)) {
			
			sql += " and  j.yuq_ts=" + yuqts;
		}
		if (!StringHelper.isEmpty(jkdate)) {
			
			sql += " and  (j.jk_date=3 or j.jk_date=4) ";
		}
		if (!StringHelper.isEmpty(et_day) && !et_day.equals("0")) {

			sql += " and  j.yuq_ts >= " + et_day;
		}
		if (!StringHelper.isEmpty(st_day) && !st_day.equals("0")) {

			sql += " and  j.yuq_ts <= " + st_day;
		}
		/*if (hkstat.equals("1")) {

			sql += " and  j.sfyhw = 1";
		}

		if (hkstat.equals("2")) {
*/
		
		//}
		sql += " and j.yuq_ts >3 and j.yuq_ts <16 " ;
//		if(cmsuserid == 8 || cmsuserid == 888 || cmsuserid == 222 || cmsuserid == 6 || cmsuserid == 2016 ||cmsuserid==9999 ||cmsuserid == 8888){
		if(roleauthoritymangement.getRoleAM_CSlist(cmsuserid+"")){
			for (int i = 0; i < cuishouzuyqm1.length; i++) {
				if(i == 0){
					sql +=" and (j.cuishou_id=2016 or j.cuishou_id=" + cuishouzuyqm1[0];
				}else if(i>0 && i<(cuishouzuyqm1.length-1)){
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[i];
				}else{
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[cuishouzuyqm1.length-1] ;
				}
				if((cuishouzuyqm1.length-1) ==i) {
					sql+=" ) ";
				}
			}
		}else{
			sql +=" and j.cuishou_id="+cmsuserid;
		}
		sql += " order by LENGTH(j.sjsh_money)>8 DESC,j.sjsh_money>='4,000,000' DESC,j.yuq_ts, j.yuq_lx ";

		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	//xiong -20190806-
	public DBPage getYqYSMList(int curPage, int numPerPage, String userId,
			String realName, String phone,String startDate, String endDate,
			String commit, String idCard, String hkstat, String et_day,
			String st_day,int cmsuserid,String cuishouid,int[] cuishouzuyqm1,String jkdate,String yuqts) throws Exception {
		
		String sql = " select j.userid, j.id as jkid2 ,u.username, u.mobilePhone,u.heihu_zt,s.realname,w.name as csname , j.yuq_lx ,j.hkfq_cishu,j.yuq_ts ,j.sjsh_money ,j.hkyq_time,j.hkfq_code,j.hk_time,j.hkfq_time,j.jk_date,j.fkr_time,j.sfyhw,j.hkqd,j.hkstate,j.zxcsbz as msg from sd_new_jkyx j left join sd_user u on "
				+ "  j.userid = u.id left join sd_user_finance s on s.userId =j.userid left join sdcms_user w on w.USER_ID = j.cuishou_id where j.sfyhw = 0 and j.hkfq_code=0";
		

		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and  s.realname like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {
			
			sql += " and  s.idno='" + idCard + "'";
		}
		if (!StringHelper.isEmpty(cuishouid)) {
			
			sql += " and  j.cuishou_id=" + cuishouid;
		}
		if (!StringHelper.isEmpty(yuqts)) {
			
			sql += " and  j.yuq_ts=" + yuqts;
		}
		if (!StringHelper.isEmpty(jkdate)) {
			
			sql += " and  (j.jk_date=3 or j.jk_date=4) ";
		}
		if (!StringHelper.isEmpty(et_day) && !et_day.equals("0")) {
			
			sql += " and  j.yuq_ts >= " + et_day;
		}
		if (!StringHelper.isEmpty(st_day) && !st_day.equals("0")) {
			
			sql += " and  j.yuq_ts <= " + st_day;
		}
		/*if (hkstat.equals("1")) {

			sql += " and  j.sfyhw = 1";
		}

		if (hkstat.equals("2")) {
		 */
		
		//}
		sql += " and j.yuq_ts >3 and j.yuq_ts <16 " ;
//		if(cmsuserid == 8 || cmsuserid == 888 || cmsuserid == 222 || cmsuserid == 6 || cmsuserid == 8 || cmsuserid == 2004 || cmsuserid == 9999 ||cmsuserid == 8888){
		if(roleauthoritymangement.getRoleAM_CSlist(cmsuserid+"")){
			for (int i = 0; i < cuishouzuyqm1.length; i++) {
				if(i == 0){
					sql +=" and (j.cuishou_id=2004 or j.cuishou_id=" + cuishouzuyqm1[0];
				}else if(i>0 && i<(cuishouzuyqm1.length-1)){
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[i];
				}else{
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[cuishouzuyqm1.length-1] ;
				}
				if((cuishouzuyqm1.length-1) ==i) {
					sql+=" ) ";
				}
			}
		}else{
			sql +=" and j.cuishou_id="+cmsuserid;
		}
		sql += " order by LENGTH(j.sjsh_money)>8 DESC,j.sjsh_money>='4,000,000' DESC,j.yuq_ts, j.yuq_lx ";
		
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public DBPage getYqLYLList(int curPage, int numPerPage, String userId,
			String realName, String phone,String startDate, String endDate,
			String commit, String idCard, String hkstat, String et_day,
			String st_day,int cmsuserid,String cuishouid,int[] cuishouzuyqm1,String jkdate,String yuqts) throws Exception {
		
		String sql = " select j.userid, j.id as jkid2 ,u.username, u.mobilePhone,u.heihu_zt,s.realname,w.name as csname , j.yuq_lx ,j.hkfq_cishu,j.yuq_ts ,j.sjsh_money ,j.hkyq_time,j.hkfq_code,j.hk_time,j.hkfq_time,j.jk_date,j.fkr_time,j.sfyhw,j.hkqd,j.hkstate,j.zxcsbz as msg from sd_new_jkyx j left join sd_user u on "
				+ "  j.userid = u.id left join sd_user_finance s on s.userId =j.userid left join sdcms_user w on w.USER_ID = j.cuishou_id where j.sfyhw = 0 and j.hkfq_code=0";
		
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and  s.realname like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {
			
			sql += " and  s.idno='" + idCard + "'";
		}
		if (!StringHelper.isEmpty(cuishouid)) {
			
			sql += " and  j.cuishou_id=" + cuishouid;
		}
		if (!StringHelper.isEmpty(yuqts)) {
			
			sql += " and  j.yuq_ts=" + yuqts;
		}
		if (!StringHelper.isEmpty(jkdate)) {
			
			sql += " and  (j.jk_date=3 or j.jk_date=4) ";
		}
		if (!StringHelper.isEmpty(et_day) && !et_day.equals("0")) {
			
			sql += " and  j.yuq_ts >= " + et_day;
		}
		if (!StringHelper.isEmpty(st_day) && !st_day.equals("0")) {
			
			sql += " and  j.yuq_ts <= " + st_day;
		}
		/*if (hkstat.equals("1")) {
	
			sql += " and  j.sfyhw = 1";
		}
	
		if (hkstat.equals("2")) {
		 */
		
		//}
		sql += " and j.yuq_ts >3 and j.yuq_ts <16 " ;
//		if(cmsuserid == 8 || cmsuserid == 888 || cmsuserid == 222 || cmsuserid == 6 || cmsuserid == 8 || cmsuserid == 2016 || cmsuserid == 5051|| cmsuserid == 5053 || cmsuserid == 9999|| cmsuserid == 8888){
		if(roleauthoritymangement.getRoleAM_CSlist(cmsuserid+"")){
			for (int i = 0; i < cuishouzuyqm1.length; i++) {
				if(i == 0){
					sql +=" and (j.cuishou_id=2016 or j.cuishou_id=" + cuishouzuyqm1[0];
				}else if(i>0 && i<(cuishouzuyqm1.length-1)){
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[i];
				}else{
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[cuishouzuyqm1.length-1] ;
				}
				
				if((cuishouzuyqm1.length-1) ==i) {
					sql+=" ) ";
				}
			}
		}else{
			sql +=" and j.cuishou_id="+cmsuserid;
		}
		sql += " order by LENGTH(j.sjsh_money)>8 DESC,j.sjsh_money>='4,000,000' DESC,j.yuq_ts, j.yuq_lx ";
		
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public DBPage getYqDWGList(int curPage, int numPerPage, String userId,
			String realName, String phone,String startDate, String endDate,
			String commit, String idCard, String hkstat, String et_day,
			String st_day,int cmsuserid,String cuishouid,int[] cuishouzuyqm1,String jkdate,String yuqts) throws Exception {
		
		String sql = " select j.userid, j.id as jkid2 ,u.username, u.mobilePhone,u.heihu_zt,s.realname,w.name as csname , j.yuq_lx ,j.hkfq_cishu,j.yuq_ts ,j.sjsh_money ,j.hkyq_time,j.hkfq_code,j.hk_time,j.hkfq_time,j.jk_date,j.fkr_time,j.sfyhw,j.hkqd,j.hkstate,j.zxcsbz as msg from sd_new_jkyx j left join sd_user u on "
				+ "  j.userid = u.id left join sd_user_finance s on s.userId =j.userid left join sdcms_user w on w.USER_ID = j.cuishou_id where j.sfyhw = 0 and j.hkfq_code=0";
		
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and  s.realname like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {
			
			sql += " and  s.idno='" + idCard + "'";
		}
		if (!StringHelper.isEmpty(cuishouid)) {
			
			sql += " and  j.cuishou_id=" + cuishouid;
		}
		if (!StringHelper.isEmpty(yuqts)) {
			
			sql += " and  j.yuq_ts=" + yuqts;
		}
		if (!StringHelper.isEmpty(jkdate)) {
			
			sql += " and  (j.jk_date=3 or j.jk_date=4) ";
		}
		if (!StringHelper.isEmpty(et_day) && !et_day.equals("0")) {
			
			sql += " and  j.yuq_ts >= " + et_day;
		}
		if (!StringHelper.isEmpty(st_day) && !st_day.equals("0")) {
			
			sql += " and  j.yuq_ts <= " + st_day;
		}
		/*if (hkstat.equals("1")) {

			sql += " and  j.sfyhw = 1";
		}

		if (hkstat.equals("2")) {
		 */
		
		//}
		sql += " and j.yuq_ts >3 and j.yuq_ts <16 " ;
//		if(cmsuserid == 8 || cmsuserid == 888 || cmsuserid == 222 || cmsuserid == 6 || cmsuserid == 8 || cmsuserid == 2018 || cmsuserid == 5051 || cmsuserid == 5053 || cmsuserid == 9999 || cmsuserid == 8888){
		if(roleauthoritymangement.getRoleAM_CSlist(cmsuserid+"")){
			for (int i = 0; i < cuishouzuyqm1.length; i++) {
				if(i == 0){
					sql +=" and (j.cuishou_id=2018 or j.cuishou_id=" + cuishouzuyqm1[0];
				}else if(i>0 && i<(cuishouzuyqm1.length-1)){
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[i];
				}else{
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[cuishouzuyqm1.length-1] ;
				}
				if((cuishouzuyqm1.length-1) ==i) {
					sql+=" ) ";
				}
			}
		}else{
			sql +=" and j.cuishou_id="+cmsuserid;
		}
		sql += " order by LENGTH(j.sjsh_money)>8 DESC,j.sjsh_money>='4,000,000' DESC,j.yuq_ts, j.yuq_lx ";
		
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	
	
//	public DBPage getYqM3List(int curPage, int numPerPage, String userId,
//			String realName, String phone,String startDate, String endDate,
//			String commit, String idCard, String hkstat, String et_day,
//			String st_day,int cmsuserid,String cuishouid,int[] cuishouzuyqm1,String jkdate,String yuqts) throws Exception {
//		
//		String sql = " select j.userid, j.id as jkid2 ,u.username, u.mobilePhone,s.realname,w.name as csname , j.yuq_lx ,j.yuq_ts ,j.jk_date,j.sjsh_money ,j.sjds_money ,j.lx ,j.hkyq_time,j.hkfq_code,j.hk_time,j.hkfq_time,j.fkr_time,j.sfyhw,j.hkqd,j.zxcsbz as msg from  (select id ,yuq_ts ,jk_date,userid ,yuq_lx , "
//				+ "  sjsh_money,sjds_money,lx,cuishou_id,fkr_time,hkyq_time,hkfq_time,hk_time,hkfq_code,hkqd,sfyhw from  sd_new_jkyx where yuq_ts > 0  ) j  left join sd_user u on "
//				+ "  j.userid = u.id left join sd_user_finance s on s.userId =j.userid left join sdcms_user w on w.USER_ID = j.cuishou_id where j.sfyhw = 0 and j.hkfq_code=0 ";
//		
//		if (!StringHelper.isEmpty(userId)) {
//			
//			sql += " and  u.id =" + userId;
//		}
//		if (!StringHelper.isEmpty(realName)) {
//			
//			sql += " and  s.realname like'%" + realName + "%'";
//		}
//		if (!StringHelper.isEmpty(phone)) {
//			
//			sql += " and  u.mobilePhone like '%" + phone + "%'";
//		}
//		if (!StringHelper.isEmpty(idCard)) {
//			
//			sql += " and  s.idno='" + idCard + "'";
//		}
//		if (!StringHelper.isEmpty(cuishouid)) {
//			
//			sql += " and  j.cuishou_id=" + cuishouid;
//		}
//		if (!StringHelper.isEmpty(yuqts)) {
//			
//			sql += " and  j.yuq_ts=" + yuqts;
//		}
//		if (!StringHelper.isEmpty(jkdate)) {
//			
//			sql += " and  (j.jk_date=3 or j.jk_date=4) ";
//		}
//		if (!StringHelper.isEmpty(et_day) && !et_day.equals("0")) {
//			
//			sql += " and  j.yuq_ts >= " + et_day;
//		}
//		if (!StringHelper.isEmpty(st_day) && !st_day.equals("0")) {
//			
//			sql += " and  j.yuq_ts <= " + st_day;
//		}
//		/*if (hkstat.equals("1")) {
//
//			sql += " and  j.sfyhw = 1";
//		}
//
//		if (hkstat.equals("2")) {
//		 */
//		
//		//}
//		sql += " and j.yuq_ts >45 " ;
//		if(cmsuserid == 8 || cmsuserid == 888 || cmsuserid == 222 || cmsuserid == 6 || cmsuserid == 8 || cmsuserid == 5051|| cmsuserid == 5053|| cmsuserid == 9999|| cmsuserid == 8888){
//			for (int i = 0; i < cuishouzuyqm1.length; i++) {
//				if(i == 0){
//					sql +=" and (j.cuishou_id=" + cuishouzuyqm1[0];
//				}else if(i>0 && i<(cuishouzuyqm1.length-1)){
//					sql +=" or j.cuishou_id=" + cuishouzuyqm1[i];
//				}else{
//					sql +=" or j.cuishou_id=" + cuishouzuyqm1[cuishouzuyqm1.length-1] + " )";
//				}
//			}
//		}else{
//			sql +=" and j.cuishou_id="+cmsuserid;
//		}
//		sql += " order by LENGTH(j.sjsh_money)>8 DESC,j.sjsh_money>='4,000,000' DESC,j.yuq_ts, j.yuq_lx ";
//		
//		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
//	}
	public DBPage getYqM2List(int curPage, int numPerPage, String userId,
			String realName, String phone,String startDate, String endDate,
			String commit, String idCard, String hkstat, String et_day,
			String st_day,int cmsuserid,String cuishouid,int[] cuishouzuyqm1,String jkdate,String yuqts) throws Exception {
		
		String sql = " select j.userid, j.id as jkid2 ,u.username, u.mobilePhone,u.heihu_zt,s.realname,w.name as csname , j.yuq_lx ,j.hkfq_cishu,j.yuq_ts ,j.sjsh_money ,j.hkyq_time,j.hkfq_code,j.hk_time,j.hkfq_time,j.jk_date,j.fkr_time,j.sfyhw,j.hkqd,j.hkstate,j.zxcsbz as msg,j.sjds_money,j.lx from sd_new_jkyx j left join sd_user u on "
				+ "  j.userid = u.id left join sd_user_finance s on s.userId =j.userid left join sdcms_user w on w.USER_ID = j.cuishou_id where j.sfyhw = 0 and j.hkfq_code=0";
		
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and  s.realname like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {
			
			sql += " and  s.idno='" + idCard + "'";
		}
		if (!StringHelper.isEmpty(cuishouid)) {
			
			sql += " and  j.cuishou_id=" + cuishouid;
		}
		if (!StringHelper.isEmpty(yuqts)) {
			
			sql += " and  j.yuq_ts=" + yuqts;
		}
		if (!StringHelper.isEmpty(jkdate)) {
			
			sql += " and  (j.jk_date=3 or j.jk_date=4) ";
		}
		if (!StringHelper.isEmpty(et_day) && !et_day.equals("0")) {
			
			sql += " and  j.yuq_ts >= " + et_day;
		}
		if (!StringHelper.isEmpty(st_day) && !st_day.equals("0")) {
			
			sql += " and  j.yuq_ts <= " + st_day;
		}
		/*if (hkstat.equals("1")) {

			sql += " and  j.sfyhw = 1";
		}

		if (hkstat.equals("2")) {
		 */
		
		//}
		sql += " and j.yuq_ts >15 and j.yuq_ts <=60  " ;
		if(cmsuserid == 8 || cmsuserid == 888 || cmsuserid == 222 || cmsuserid == 6 || cmsuserid == 8 || cmsuserid == 13|| cmsuserid == 9999||cmsuserid == 8888){
			for (int i = 0; i < cuishouzuyqm1.length; i++) {
				if(i == 0){
					sql +=" and( j.cuishou_id=13 or j.cuishou_id=" + cuishouzuyqm1[0];
				}else if(i>0 && i<(cuishouzuyqm1.length-1)){
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[i];
				}else{
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[cuishouzuyqm1.length-1] ;
				}
				
				if((cuishouzuyqm1.length-1) ==i) {
					sql+=" ) ";
				}
			}
		}else{
			sql +=" and j.cuishou_id="+cmsuserid;
		}
		sql += " order by LENGTH(j.sjsh_money)>8 DESC,j.sjsh_money>='4,000,000' DESC,j.yuq_ts, j.yuq_lx ";
	
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}



	//	public List<DataRow> getYqM3List(String userId,
	//			String realName, String phone, String startDate, String endDate,
	//			String commit, String idCard, String hkstat, String et_day,
	//			String st_day,int cmsuserid,String cuishouid,int[] cuishouzuyqm1,String jkdate,String yuqts) throws Exception {
	//		
	//		String sql = " select j.userid, j.id as jkid2 ,u.username, u.mobilePhone,s.realname,w.name as csname , j.yuq_lx ,j.yuq_ts ,j.jk_date,j.sjsh_money ,j.hkyq_time,j.hkfq_code,j.hk_time,j.hkfq_time,j.fkr_time,j.sfyhw,j.hkqd,j.zxcsbz as msg from  (select id ,yuq_ts ,jk_date,userid ,yuq_lx , "
	//				+ "  sjsh_money,cuishou_id,fkr_time,hkyq_time,hkfq_time,hk_time,hkfq_code,hkqd,sfyhw,zxcsbz from  sd_new_jkyx where yuq_ts > 0  ) j  left join sd_user u on "
	//				+ "  j.userid = u.id left join sd_user_finance s on s.userId =j.userid left join sdcms_user w on w.USER_ID = j.cuishou_id where ";
	//		sql += " j.sfyhw = 0 and j.hkfq_code=0 ";
	//		if (!StringHelper.isEmpty(userId)) {
	//			
	//			sql += " and  u.id =" + userId;
	//		}
	//		if (!StringHelper.isEmpty(realName)) {
	//			
	//			sql += " and  s.realname like'%" + realName + "%'";
	//		}
	//		if (!StringHelper.isEmpty(phone)) {
	//			
	//			sql += " and  u.mobilePhone like '%" + phone + "%'";
	//		}
	//		if (!StringHelper.isEmpty(idCard)) {
	//			
	//			sql += " and  s.idno='" + idCard + "'";
	//		}
	//		if (!StringHelper.isEmpty(cuishouid)) {
	//			
	//			sql += " and  j.cuishou_id=" + cuishouid;
	//		}
	//		if (!StringHelper.isEmpty(yuqts)) {
	//			
	//			sql += " and  j.yuq_ts=" + yuqts;
	//		}
	//		if (!StringHelper.isEmpty(jkdate)) {
	//			
	//			sql += " and  (j.jk_date=3 or j.jk_date=4) ";
	//		}
	//		if (!StringHelper.isEmpty(et_day) && !et_day.equals("0")) {
	//			
	//			sql += " and  j.yuq_ts >= " + et_day;
	//		}
	//		if (!StringHelper.isEmpty(st_day) && !st_day.equals("0")) {
	//			
	//			sql += " and  j.yuq_ts <= " + st_day;
	//		}
	//		
	//		sql += " and  j.yuq_ts >45 " ;
	//		if(cmsuserid == 8 || cmsuserid == 888 || cmsuserid == 222 || cmsuserid == 6 || cmsuserid == 8 || cmsuserid == 5051|| cmsuserid == 5053 || cmsuserid == 9999|| cmsuserid == 8888){
	//			for (int i = 0; i < cuishouzuyqm1.length; i++) {
	//				if(i == 0){
	//					sql +=" and (j.cuishou_id=" + cuishouzuyqm1[0];
	//				}else if(i>0 && i<(cuishouzuyqm1.length-1)){
	//					sql +=" or j.cuishou_id=" + cuishouzuyqm1[i];
	//				}else{
	//					sql +=" or j.cuishou_id=" + cuishouzuyqm1[cuishouzuyqm1.length-1] + " )";
	//				}
	//			}
	//		}else{
	//			sql +=" and j.cuishou_id="+cmsuserid;
	//		}
	//		sql += " order by LENGTH(j.sjsh_money)>8 DESC,j.sjsh_money>='4,000,000' DESC,j.yuq_ts, j.yuq_lx ";
	//		
	//		return getJdbcTemplate().query(sql);
	//	}
		//xiong-20190807
	public DBPage getYqM3List(int curPage, int numPerPage, String userId,
			String realName, String phone,String startDate, String endDate,
			String commit, String idCard, String hkstat, String et_day,
			String st_day,int cmsuserid,String cuishouid,int[] cuishouzuyqm1,String jkdate,String yuqts) throws Exception {
		
		String sql = " select j.userid, j.id as jkid2 ,u.username, u.mobilePhone,u.heihu_zt,s.realname,w.name as csname , j.yuq_lx ,j.hkfq_cishu,j.yuq_ts ,j.sjsh_money ,j.hkyq_time,j.hkfq_code,j.hk_time,j.hkfq_time,j.jk_date,j.fkr_time,j.sfyhw,j.hkqd,j.hkstate,j.zxcsbz as msg,j.sjds_money,j.lx from sd_new_jkyx j left join sd_user u on "
				+ "  j.userid = u.id left join sd_user_finance s on s.userId =j.userid left join sdcms_user w on w.USER_ID = j.cuishou_id where j.sfyhw = 0 and j.hkfq_code=0";
		
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and  s.realname like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {
			
			sql += " and  s.idno='" + idCard + "'";
		}
		if (!StringHelper.isEmpty(cuishouid)) {
			
			sql += " and  j.cuishou_id=" + cuishouid;
		}
		if (!StringHelper.isEmpty(yuqts)) {
			
			sql += " and  j.yuq_ts=" + yuqts;
		}
		if (!StringHelper.isEmpty(jkdate)) {
			
			sql += " and  (j.jk_date=3 or j.jk_date=4) ";
		}
		if (!StringHelper.isEmpty(et_day) && !et_day.equals("0")) {
			
			sql += " and  j.yuq_ts >= " + et_day;
		}
		if (!StringHelper.isEmpty(st_day) && !st_day.equals("0")) {
			
			sql += " and  j.yuq_ts <= " + st_day;
		}
		/*if (hkstat.equals("1")) {
	
			sql += " and  j.sfyhw = 1";
		}
	
		if (hkstat.equals("2")) {
		 */
		
		//}
		sql += " and j.yuq_ts >45 " ;
//		if(cmsuserid == 8 || cmsuserid == 888 || cmsuserid == 222 || cmsuserid == 6 || cmsuserid == 16 || cmsuserid == 5151 || cmsuserid == 5153 || cmsuserid == 9999||cmsuserid == 8888){
		if(roleauthoritymangement.getRoleAM_CSlist(cmsuserid+"")){
			for (int i = 0; i < cuishouzuyqm1.length; i++) {
				if(i == 0){
					sql +=" and (j.cuishou_id=16 or j.cuishou_id=" + cuishouzuyqm1[0];
				}else if(i>0 && i<(cuishouzuyqm1.length-1)){
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[i];
				}else{
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[cuishouzuyqm1.length-1];
				}
				if((cuishouzuyqm1.length-1) ==i) {
					sql+=" ) ";
				}
				
			}
		}else{
			int roleid = getAllcuishouM3jia(cmsuserid);
			if(roleid != 26) {
				List<DataRow> cuishoum3 = getAllcuishouM3jia();
				int cuishouzuyqm3[] = new int[cuishoum3.size()];
				
				for (int m = 0; m < cuishoum3.size(); m++) {
					DataRow row = cuishoum3.get(m);
					cuishouzuyqm3[m] = row.getInt("user_id");
				}
				for (int i = 0; i < cuishouzuyqm3.length; i++) {
					if(i == 0){
						sql +=" and (j.cuishou_id=" + cuishouzuyqm3[0];
					}else if(i>0 && i<(cuishouzuyqm3.length-1)){
						sql +=" or j.cuishou_id=" + cuishouzuyqm3[i];
					}else{
						sql +=" or j.cuishou_id=" + cuishouzuyqm3[cuishouzuyqm3.length-1] ;
					}
					if((cuishouzuyqm1.length-1) ==i) {
						sql+=" ) ";
					}
					
				}
			}
			sql +=" and j.cuishou_id="+cmsuserid;
			
		}
		sql += " order by LENGTH(j.sjsh_money)>8 DESC,j.sjsh_money>='4,000,000' DESC,j.yuq_ts, j.yuq_lx ";
		
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public Integer getAllcuishouM3jia(int cmsuserid){
		 String sql = "SELECT roleid FROM sdcms_user WHERE state=1 and user_id="+cmsuserid;
		 return getJdbcTemplate().queryInt(sql);
	 }
	public List<DataRow> getAllcuishouM3jia(){
		String sql = "SELECT user_id FROM sdcms_user WHERE (roleid=60 OR roleid=61) and state=1";
		return getJdbcTemplate().query(sql);
	}
	
	public List<DataRow> getYqCSList( String userId,
			String realName, String phone,String startDate, String endDate,
			String commit, String idCard, String hkstat, String et_day,
			String st_day) throws Exception {

		String sql = " select j.userid, j.id as jkid2 ,u.username, u.mobilePhone ,f.cardUserName ,f.cardNo , q.name as shname ,w.name as csname , j.yuq_lx ,j.yuq_ts ,j.sjsh_money ,j.zxcsbz as msg from sd_new_jkyx j  left join sd_user u on "
				+ "  j.userid = u.id left join sd_bankcard f on f.userId =j.userid left join sdcms_user q on q.USER_ID = j.cl03_ren left join sdcms_user w on w.USER_ID = j.cuishou_id where ";
		sql += " j.yuq_ts >=7 " ;
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  f.cardUserName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {
			
			sql += " and  s.idno='" + idCard + "'";
		}
		
		
		sql += " order by j.yuq_ts, j.yuq_lx ,j.sjsh_money ";

		return getJdbcTemplate().query(sql);
	}
	
	public List<DataRow> getYqKFList(String userId,
			String realName, String phone, String startDate, String endDate,
			String commit, String idCard, String hkstat, String et_day,
			String st_day) throws Exception {

		String sql = " select j.userid, j.id as jkid2 ,u.username, u.mobilePhone ,f.cardUserName ,f.cardNo , q.name as shname ,w.name as csname , j.yuq_lx ,j.yuq_ts ,j.sjsh_money ,j.zxcsbz as msg from sd_new_jkyx j  left join sd_user u on "
				+ "  j.userid = u.id left join sd_bankcard f on f.userId =j.userid left join sdcms_user q on q.USER_ID = j.cl03_ren left join sdcms_user w on w.USER_ID = j.cuishou_id where ";
		sql += " j.yuq_ts <7 and j.yuq_ts>0" ;
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  f.cardUserName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		
		
		sql += " order by j.yuq_ts, j.yuq_lx ,j.sjsh_money ";

		return getJdbcTemplate().query(sql);
	}
	public List<DataRow> getYqM0List(String userId,
			String realName, String phone, String startDate, String endDate,
			String commit, String idCard, String hkstat, String et_day,
			String st_day,int cmsuserid,String cuishouid,int[] cuishouzuyqm1,String jkdate,String yuqts) throws Exception {
		
		String sql = " select j.yuq_lx ,j.sjsh_money from sd_new_jkyx j left join sd_user u on "
				+ "  j.userid = u.id left join sd_user_finance s on s.userId =j.userid where j.sfyhw = 0 and j.hkfq_code=0";
		
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and  s.realname like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {
			
			sql += " and  s.idno='" + idCard + "'";
		}
		if (!StringHelper.isEmpty(cuishouid)) {
			
			sql += " and  j.cuishou_id=" + cuishouid;
		}
		if (!StringHelper.isEmpty(yuqts)) {
			
			sql += " and  j.yuq_ts=" + yuqts;
		}
		if (!StringHelper.isEmpty(jkdate)) {
			
			sql += " and  (j.jk_date=3 or j.jk_date=4) ";
		}
		if (!StringHelper.isEmpty(et_day) && !et_day.equals("0")) {
			
			sql += " and  j.yuq_ts >= " + et_day;
		}
		if (!StringHelper.isEmpty(st_day) && !st_day.equals("0")) {
			
			sql += " and  j.yuq_ts <= " + st_day;
		}
		
		sql += " and  j.yuq_ts >0 and j.yuq_ts <4 " ;
//		if(cmsuserid == 8 || cmsuserid == 888 || cmsuserid == 222 || cmsuserid == 6 || cmsuserid == 8 || cmsuserid == 53){
		if(roleauthoritymangement.getRoleAM_CSlist(cmsuserid+"")){
			for (int i = 0; i < cuishouzuyqm1.length; i++) {
				if(i == 0){
					sql +=" and (j.cuishou_id=" + cuishouzuyqm1[0];
				}else if(i>0 && i<(cuishouzuyqm1.length-1)){
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[i];
				}else{
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[cuishouzuyqm1.length-1] ;
				}
				if((cuishouzuyqm1.length-1) ==i) {
					sql+=" ) ";
				}
			}
		}else{
			sql +=" and j.cuishou_id="+cmsuserid;
		}
		sql += " order by LENGTH(j.sjsh_money)>8 DESC,j.sjsh_money>='4,000,000' DESC,j.yuq_ts, j.yuq_lx ";
		
		return getJdbcTemplate().query(sql);
	}
//xiong20190807黑名单
	public List<DataRow> getYqZYList(String userId,
			String realName, String phone, String startDate, String endDate,
			String commit, String idCard, String hkstat, String et_day,
			String st_day,int cmsuserid,String cuishouid,int[] cuishouzuyqm1,String jkdate,String yuqts) throws Exception {

		String sql = " select j.yuq_lx ,j.sjsh_money from sd_new_jkyx j left join sd_user u on "
				+ "  j.userid = u.id left join sd_user_finance s on s.userId =j.userid where j.sfyhw = 0 and j.hkfq_code=0";
		
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  s.realname like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {
			
			sql += " and  s.idno='" + idCard + "'";
		}
		if (!StringHelper.isEmpty(cuishouid)) {
			
			sql += " and  j.cuishou_id=" + cuishouid;
		}
		if (!StringHelper.isEmpty(yuqts)) {
			
			sql += " and  j.yuq_ts=" + yuqts;
		}
		if (!StringHelper.isEmpty(jkdate)) {
			
			sql += " and  (j.jk_date=3 or j.jk_date=4) ";
		}
		if (!StringHelper.isEmpty(et_day) && !et_day.equals("0")) {

			sql += " and  j.yuq_ts >= " + et_day;
		}
		if (!StringHelper.isEmpty(st_day) && !st_day.equals("0")) {

			sql += " and  j.yuq_ts <= " + st_day;
		}
		
		sql += " and  j.yuq_ts >0 and j.yuq_ts <16 " ;
//		if(cmsuserid == 8 || cmsuserid == 888 || cmsuserid == 222 || cmsuserid == 6 || cmsuserid == 2016||cmsuserid == 2016 ||cmsuserid ==9999||cmsuserid == 8888){
		if(roleauthoritymangement.getRoleAM_CSlist(cmsuserid+"")){
			for (int i = 0; i < cuishouzuyqm1.length; i++) {
				if(i == 0){
					sql +=" and (j.cuishou_id=2016 or j.cuishou_id=" + cuishouzuyqm1[0];
				}else if(i>0 && i<(cuishouzuyqm1.length-1)){
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[i];
				}else{
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[cuishouzuyqm1.length-1];
				}
				
				if((cuishouzuyqm1.length-1) ==i) {
					sql+=" ) ";
				}
			}
		}else{
			sql +=" and j.cuishou_id="+cmsuserid;
		}
		sql += " order by LENGTH(j.sjsh_money)>8 DESC,j.sjsh_money>='4,000,000' DESC,j.yuq_ts, j.yuq_lx ";

		return getJdbcTemplate().query(sql);
	}
	////xiong20190614催收和黑名单
	public List<DataRow> getYqYSMList(String userId,
			String realName, String phone, String startDate, String endDate,
			String commit, String idCard, String hkstat, String et_day,
			String st_day,int cmsuserid,String cuishouid,int[] cuishouzuyqm1,String jkdate,String yuqts) throws Exception {
		
		String sql = " select j.yuq_lx ,j.sjsh_money from sd_new_jkyx j left join sd_user u on "
				+ "  j.userid = u.id left join sd_user_finance s on s.userId =j.userid where j.sfyhw = 0 and j.hkfq_code=0";
		
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and  s.realname like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {
			
			sql += " and  s.idno='" + idCard + "'";
		}
		if (!StringHelper.isEmpty(cuishouid)) {
			
			sql += " and  j.cuishou_id=" + cuishouid;
		}
		if (!StringHelper.isEmpty(yuqts)) {
			
			sql += " and  j.yuq_ts=" + yuqts;
		}
		if (!StringHelper.isEmpty(jkdate)) {
			
			sql += " and  (j.jk_date=3 or j.jk_date=4) ";
		}
		if (!StringHelper.isEmpty(et_day) && !et_day.equals("0")) {
			
			sql += " and  j.yuq_ts >= " + et_day;
		}
		if (!StringHelper.isEmpty(st_day) && !st_day.equals("0")) {
			
			sql += " and  j.yuq_ts <= " + st_day;
		}
		
		sql += " and  j.yuq_ts >0 and j.yuq_ts <16 " ;
//		if(cmsuserid == 8 || cmsuserid == 888 || cmsuserid == 222 || cmsuserid == 6 || cmsuserid == 8 || cmsuserid == 2004 || cmsuserid == 5051 || cmsuserid == 5053 || cmsuserid == 9999|| cmsuserid == 8888){
		if(roleauthoritymangement.getRoleAM_CSlist(cmsuserid+"")){
			for (int i = 0; i < cuishouzuyqm1.length; i++) {
				if(i == 0){
					sql +=" and (j.cuishou_id=2004 or j.cuishou_id=" + cuishouzuyqm1[0];
				}else if(i>0 && i<(cuishouzuyqm1.length-1)){
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[i];
				}else{
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[cuishouzuyqm1.length-1] ;
				}
				if((cuishouzuyqm1.length-1) ==i) {
					sql+=" ) ";
				}
			}
		}else{
			sql +=" and j.cuishou_id="+cmsuserid;
		}
		sql += " order by LENGTH(j.sjsh_money)>8 DESC,j.sjsh_money>='4,000,000' DESC,j.yuq_ts, j.yuq_lx ";
		
		return getJdbcTemplate().query(sql);
	}
	public List<DataRow> getYqLYLList(String userId,
			String realName, String phone, String startDate, String endDate,
			String commit, String idCard, String hkstat, String et_day,
			String st_day,int cmsuserid,String cuishouid,int[] cuishouzuyqm1,String jkdate,String yuqts) throws Exception {
		
		String sql = " select j.yuq_lx ,j.sjsh_money from sd_new_jkyx j left join sd_user u on "
				+ "  j.userid = u.id left join sd_user_finance s on s.userId =j.userid where j.sfyhw = 0 and j.hkfq_code=0";
		
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and  s.realname like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {
			
			sql += " and  s.idno='" + idCard + "'";
		}
		if (!StringHelper.isEmpty(cuishouid)) {
			
			sql += " and  j.cuishou_id=" + cuishouid;
		}
		if (!StringHelper.isEmpty(yuqts)) {
			
			sql += " and  j.yuq_ts=" + yuqts;
		}
		if (!StringHelper.isEmpty(jkdate)) {
			
			sql += " and  (j.jk_date=3 or j.jk_date=4) ";
		}
		if (!StringHelper.isEmpty(et_day) && !et_day.equals("0")) {
			
			sql += " and  j.yuq_ts >= " + et_day;
		}
		if (!StringHelper.isEmpty(st_day) && !st_day.equals("0")) {
			
			sql += " and  j.yuq_ts <= " + st_day;
		}
		
		sql += " and  j.yuq_ts >0 and j.yuq_ts <16 " ;
//		if(cmsuserid == 8 || cmsuserid == 888 || cmsuserid == 222 || cmsuserid == 6 || cmsuserid == 8 || cmsuserid == 2016 || cmsuserid == 5051 || cmsuserid == 5053){
		if(roleauthoritymangement.getRoleAM_CSlist(cmsuserid+"")){
			for (int i = 0; i < cuishouzuyqm1.length; i++) {
				if(i == 0){
					sql +=" and (j.cuishou_id=2016 or j.cuishou_id=" + cuishouzuyqm1[0];
				}else if(i>0 && i<(cuishouzuyqm1.length-1)){
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[i];
				}else{
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[cuishouzuyqm1.length-1] ;
				}
				if((cuishouzuyqm1.length-1) ==i) {
					sql+=" ) ";
				}
			}
		}else{
			sql +=" and j.cuishou_id="+cmsuserid;
		}
		sql += " order by LENGTH(j.sjsh_money)>8 DESC,j.sjsh_money>='4,000,000' DESC,j.yuq_ts, j.yuq_lx ";
		
		return getJdbcTemplate().query(sql);
	}
	public List<DataRow> getYqDWGList(String userId,
			String realName, String phone, String startDate, String endDate,
			String commit, String idCard, String hkstat, String et_day,
			String st_day,int cmsuserid,String cuishouid,int[] cuishouzuyqm1,String jkdate,String yuqts) throws Exception {
		
		String sql = " select j.yuq_lx ,j.sjsh_money from sd_new_jkyx j left join sd_user u on "
				+ "  j.userid = u.id left join sd_user_finance s on s.userId =j.userid where j.sfyhw = 0 and j.hkfq_code=0";
		
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and  s.realname like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {
			
			sql += " and  s.idno='" + idCard + "'";
		}
		if (!StringHelper.isEmpty(cuishouid)) {
			
			sql += " and  j.cuishou_id=" + cuishouid;
		}
		if (!StringHelper.isEmpty(yuqts)) {
			
			sql += " and  j.yuq_ts=" + yuqts;
		}
		if (!StringHelper.isEmpty(jkdate)) {
			
			sql += " and  (j.jk_date=3 or j.jk_date=4) ";
		}
		if (!StringHelper.isEmpty(et_day) && !et_day.equals("0")) {
			
			sql += " and  j.yuq_ts >= " + et_day;
		}
		if (!StringHelper.isEmpty(st_day) && !st_day.equals("0")) {
			
			sql += " and  j.yuq_ts <= " + st_day;
		}
		
		sql += " and  j.yuq_ts >0 and j.yuq_ts <16 " ;
//		if(cmsuserid == 8 || cmsuserid == 888 || cmsuserid == 222 || cmsuserid == 6 || cmsuserid == 8 || cmsuserid == 2018|| cmsuserid == 5051|| cmsuserid == 5053 || cmsuserid == 9999|| cmsuserid == 8888){
		if(roleauthoritymangement.getRoleAM_CSlist(cmsuserid+"")){
			for (int i = 0; i < cuishouzuyqm1.length; i++) {
				if(i == 0){
					sql +=" and (j.cuishou_id=2018 or j.cuishou_id=" + cuishouzuyqm1[0];
				}else if(i>0 && i<(cuishouzuyqm1.length-1)){
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[i];
				}else{
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[cuishouzuyqm1.length-1] ;
				}
				if((cuishouzuyqm1.length-1) ==i) {
					sql+=" ) ";
				}
			}
		}else{
			sql +=" and j.cuishou_id="+cmsuserid;
		}
		sql += " order by LENGTH(j.sjsh_money)>8 DESC,j.sjsh_money>='4,000,000' DESC,j.yuq_ts, j.yuq_lx ";
		
		return getJdbcTemplate().query(sql);
	}
public List<DataRow> getYqM2List(String userId,
			String realName, String phone, String startDate, String endDate,
			String commit, String idCard, String hkstat, String et_day,
			String st_day,int cmsuserid,String cuishouid,int[] cuishouzuyqm1,String jkdate,String yuqts) throws Exception {
		
		String sql = " select j.yuq_lx ,j.sjsh_money from sd_new_jkyx j left join sd_user u on "
				+ "  j.userid = u.id left join sd_user_finance s on s.userId =j.userid where j.sfyhw = 0 and j.hkfq_code=0";
		
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and  s.realname like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {
			
			sql += " and  s.idno='" + idCard + "'";
		}
		if (!StringHelper.isEmpty(cuishouid)) {
			
			sql += " and  j.cuishou_id=" + cuishouid;
		}
		if (!StringHelper.isEmpty(yuqts)) {
			
			sql += " and  j.yuq_ts=" + yuqts;
		}
		if (!StringHelper.isEmpty(jkdate)) {
			
			sql += " and  (j.jk_date=3 or j.jk_date=4) ";
		}
		if (!StringHelper.isEmpty(et_day) && !et_day.equals("0")) {
			
			sql += " and  j.yuq_ts >= " + et_day;
		}
		if (!StringHelper.isEmpty(st_day) && !st_day.equals("0")) {
			
			sql += " and  j.yuq_ts <= " + st_day;
		}
		
		sql += " and  j.yuq_ts >15  and j.yuq_ts <=60 " ;
//		if(cmsuserid == 8 || cmsuserid == 888 || cmsuserid == 222 || cmsuserid == 6 || cmsuserid == 13){
		if(roleauthoritymangement.getRoleAM_CSlist(cmsuserid+"")){
			for (int i = 0; i < cuishouzuyqm1.length; i++) {
				if(i == 0){
					sql +=" and (j.cuishou_id=13 or j.cuishou_id=" + cuishouzuyqm1[0];
				}else if(i>0 && i<(cuishouzuyqm1.length-1)){
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[i];
				}else{
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[cuishouzuyqm1.length-1] ;
				}
				if((cuishouzuyqm1.length-1) ==i) {
					sql+=" ) ";
				}
			}
		}else{
			sql +=" and j.cuishou_id="+cmsuserid;
		}
		sql += " order by LENGTH(j.sjsh_money)>8 DESC,j.sjsh_money>='4,000,000' DESC,j.yuq_ts, j.yuq_lx ";
		
		return getJdbcTemplate().query(sql);
	}

public List<DataRow> getYqM3List(String userId,
		String realName, String phone, String startDate, String endDate,
		String commit, String idCard, String hkstat, String et_day,
		String st_day,int cmsuserid,String cuishouid,int[] cuishouzuyqm1,String jkdate,String yuqts) throws Exception {
	
	String sql = " select j.yuq_lx ,j.sjsh_money from sd_new_jkyx j left join sd_user u on "
			+ "  j.userid = u.id left join sd_user_finance s on s.userId =j.userid where j.sfyhw = 0 and j.hkfq_code=0";
	
	if (!StringHelper.isEmpty(userId)) {
		
		sql += " and  u.id =" + userId;
	}
	if (!StringHelper.isEmpty(realName)) {
		
		sql += " and  s.realname like'%" + realName + "%'";
	}
	if (!StringHelper.isEmpty(phone)) {
		
		sql += " and  u.mobilePhone like '%" + phone + "%'";
	}
	if (!StringHelper.isEmpty(idCard)) {
		
		sql += " and  s.idno='" + idCard + "'";
	}
	if (!StringHelper.isEmpty(cuishouid)) {
		
		sql += " and  j.cuishou_id=" + cuishouid;
	}
	if (!StringHelper.isEmpty(yuqts)) {
		
		sql += " and  j.yuq_ts=" + yuqts;
	}
	if (!StringHelper.isEmpty(jkdate)) {
		
		sql += " and  (j.jk_date=3 or j.jk_date=4) ";
	}
	if (!StringHelper.isEmpty(et_day) && !et_day.equals("0")) {
		
		sql += " and  j.yuq_ts >= " + et_day;
	}
	if (!StringHelper.isEmpty(st_day) && !st_day.equals("0")) {
		
		sql += " and  j.yuq_ts <= " + st_day;
	}
	
	sql += " and  j.yuq_ts >45 " ;
//	if(cmsuserid == 8 || cmsuserid == 888 || cmsuserid == 222 || cmsuserid == 6 || cmsuserid == 16 ){
	if(roleauthoritymangement.getRoleAM_CSlist(cmsuserid+"")){
		for (int i = 0; i < cuishouzuyqm1.length; i++) {
			if(i == 0){
				sql +=" and (j.cuishou_id=16 or j.cuishou_id=" + cuishouzuyqm1[0];
			}else if(i>0 && i<(cuishouzuyqm1.length-1)){
				sql +=" or j.cuishou_id=" + cuishouzuyqm1[i];
			}else{
				sql +=" or j.cuishou_id=" + cuishouzuyqm1[cuishouzuyqm1.length-1] ;
			}
			if((cuishouzuyqm1.length-1) ==i) {
				sql+=" ) ";
			}
		}
	}else{
		int roleid = getAllcuishouM3jia(cmsuserid);
		if(roleid != 26) {
			List<DataRow> cuishoum3 = getAllcuishouM3jia();
			int cuishouzuyqm3[] = new int[cuishoum3.size()];
			
			for (int m = 0; m < cuishoum3.size(); m++) {
				DataRow row = cuishoum3.get(m);
				cuishouzuyqm3[m] = row.getInt("user_id");
			}
			for (int i = 0; i < cuishouzuyqm3.length; i++) {
				if(i == 0){
					sql +=" and (j.cuishou_id=" + cuishouzuyqm3[0];
				}else if(i>0 && i<(cuishouzuyqm3.length-1)){
					sql +=" or j.cuishou_id=" + cuishouzuyqm3[i];
				}else{
					sql +=" or j.cuishou_id=" + cuishouzuyqm3[cuishouzuyqm3.length-1] ;
				}
				if((cuishouzuyqm1.length-1) ==i) {
					sql+=" ) ";
				}
				
			}
		}
		sql +=" and j.cuishou_id="+cmsuserid;
	}
	sql += " order by LENGTH(j.sjsh_money)>8 DESC,j.sjsh_money>='4,000,000' DESC,j.yuq_ts, j.yuq_lx ";
	
	return getJdbcTemplate().query(sql);
}
	public List<DataRow> getCsList(int jkid) {
		String sql = "select content as msg ,visitdate as create_time,kefuid as cl_ren,name from  sd_returnvisit  left join sdcms_user on kefuid=sdcms_user.user_id where  (code=6 or code=1 or code=8) and  jkjl_id = "
				+ jkid + " order by id desc  ";
		List<DataRow> list = getJdbcTemplate().query(sql);
		return list;

	}
	public List<DataRow> getFwList(int jkid) {
		String sql = "select content ,visitdate,kefuid,name,sdcms_user.user_id from  sd_returnvisit  left join sdcms_user on kefuid=sdcms_user.user_id where  code=9 and  sd_returnvisit.user_id = "
				+ jkid + " order by id desc ";
		List<DataRow> list = getJdbcTemplate().query(sql);
		return list;
		
	}
	public List<DataRow> getFwListYX(int jkid) {
		String sql = "select content ,visitdate,kefuid,name,sdcms_user.user_id from  sd_returnvisit  left join sdcms_user on kefuid=sdcms_user.user_id where  code=12 and  sd_returnvisit.user_id = "
				+ jkid + " order by id desc ";
		List<DataRow> list = getJdbcTemplate().query(sql);
		return list;
		
	}
	public List<DataRow> getShList(int jkid) {
		String sql = "select content ,visitdate,kefuid,name,sdcms_user.user_id from  sd_returnvisit  left join sdcms_user on kefuid=sdcms_user.user_id where  (code=8 or code=6 or code=1 ) ";
		
		sql +="and  sd_returnvisit.user_id = "+ jkid + " order by id desc ";
		
		List<DataRow> list = getJdbcTemplate().query(sql);
		return list;
		
	}
	public List<DataRow> getWJDHList(int jkid) {
		String sql = "select content ,visitdate,kefuid,name,sdcms_user.user_id from  sd_returnvisit  left join sdcms_user on kefuid=sdcms_user.user_id where code=10";
		
		sql +=" and  sd_returnvisit.user_id = "+ jkid + " order by id desc ";
		
		List<DataRow> list = getJdbcTemplate().query(sql);
		return list;
		
	}
	public List<DataRow> getTXList(int jkid) {
		String sql = "select msg ,create_time,cl_ren,name from  sd_csmsg  left join sdcms_user on cl_ren=user_id  where  msgtype='提醒' and  jkid = "
				+ jkid + " order by create_time desc  ";
		List<DataRow> list = getJdbcTemplate().query(sql);
		return list;
		
	}

	public DBPage getRecordListDFK(int curPage, int numPerPage, String userId,
			String realName, String phone, String commit, String idCard,
			String startDate, String endDate,int isolduser) throws Exception {
		String sql = " select j.id as jkid, j.jk_date as jkqx , u.id as id ,u.username as username,f.realName ,u.mobilePhone AS mobilePhone ,f.idNo as idNo,b.cardNo as cardNo,b.cardname as cardname,b.napasbankno,j.sjsh_money ,j.sjds_money ,q.name as sh_name ,cl03_time,j.lx from "
				+ " sd_new_jkyx j left join sd_user u on j.userid = u.id left join sd_bankcard b on j.userid = b.userid LEFT join sd_user_finance f on f.userId =j.userid  left join sdcms_user q on "
				+ " q.USER_ID = j.cl03_ren where spzt=1  and j.sfyfk =2 ";
		sql+= " and is_old_user="+ isolduser;
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  f.realName like'%" + realName + "%'";
		}
		
		if (!StringHelper.isEmpty(phone)) {
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {

			sql += " and  f.idNo like '%" + idCard + "%'";
		}

		if (!StringHelper.isEmpty(startDate)) {


			sql += " and ((substring(j.cl03_time,1,10) >='" + startDate + "'";
			sql += " and substring(j.cl03_time,4,2) >='" + startDate.substring(3, 5) + "')";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and (substring(j.cl03_time,1,10) <='" + endDate + "'";
			sql += " and substring(j.cl03_time,4,2) <='" + endDate.substring(3, 5) + "'))";
		}
		sql += " order by  SUBSTRING(j.cl03_time,7,4) ASC,SUBSTRING(j.cl03_time,4,2) ASC,SUBSTRING(j.cl03_time,1,2) ASC ,SUBSTRING(j.cl03_time,11) ASC";

		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public List<DataRow> getRecordListDFKList(int curPage, int numPerPage, String userId,
			String realName, String phone, String commit, String idCard,String time,
			String startDate, String endDate,int isolduser) throws Exception {
		String sql = " select j.id as jkid, j.jk_date as jkqx ,u.id as id ,u.username as username,f.realName ,u.mobilePhone AS mobilePhone ,f.idNo as idNo,b.cardNo as cardNo,b.cardname as cardname,b.napasbankno,j.sjsh_money ,j.sfyfk,j.sjds_money , q.name as sh_name ,cl03_time from "
				+ " sd_new_jkyx j left join sd_user u on j.userid = u.id left join sd_bankcard b on j.userid = b.userid LEFT join sd_user_finance f on f.userId =j.userid  left join sdcms_user q on "
				+ " q.USER_ID = j.cl03_ren where spzt=1 ";
		
		sql +=" and (substring(cl03_time,1,10)='"+time+"' or substring(fkdz_time,1,10)='"+time+"')";
		sql+= " and is_old_user="+ isolduser;
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and  f.realName like'%" + realName + "%'";
		}
		
		if (!StringHelper.isEmpty(phone)) {
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {
			
			sql += " and  f.idNo like '%" + idCard + "%'";
		}
		
		/*if (!StringHelper.isEmpty(startDate)) {
			
			
			sql += " and ((substring(j.cl03_time,1,10) >='" + startDate + "'";
			sql += " and substring(j.cl03_time,4,2) >='" + startDate.substring(3, 5) + "')";
		}
		if (!StringHelper.isEmpty(endDate)) {
			
			sql += " and (substring(j.cl03_time,1,10) <='" + endDate + "'";
			sql += " and substring(j.cl03_time,4,2) <='" + endDate.substring(3, 5) + "'))";
		}*/
		sql += " order by j.cl03_time  ";
		
		return getJdbcTemplate().query(sql);
	}

  public double getdfkmoneyaccount(int isolduwer) {
		
		String sql=" SELECT SUM(REPLACE(sjds_money,',','')) FROM sd_new_jkyx WHERE sfyfk =2";
		
		if (1== isolduwer) {
			sql += " and is_old_user ="+isolduwer;
		}else if (0 == isolduwer) {
			sql += " and is_old_user ="+isolduwer;
		}
		return getJdbcTemplate().queryLong(sql);
	}

	public DBPage getRecordListChg02(int curPage, int numPerPage,
			String userId, String realName, String phone, String startDate,
			String endDate, String commit, String idCard, String hkstat,
			String investorId, String investorName,String hkqd) throws Exception {

		String sql = "select j.id as jkid, u.id as id ,f.realName ,h.realName as investorName  ,INSERT(u.mobilePhone,4,4,'****')AS mobilePhone,INSERT(f.idNo,4,4,'****')AS idNo, j.jk_money, j.jk_date ,j.sjsh_money ,j.sjds_money ,j.hkqd, j.sfyfk, j.fkdz_time,j.hkyq_time,j.sfyhw ,j.yuq_lx ,j.hk_time from "
				+ "   sd_new_jkyx j left join sd_user u on j.userid = u.id LEFT join sd_user_finance f on f.userId =j.userid left join sd_user_finance h on h.userId = j.cuishou_id  where  j.sfyfk <>0 ";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  f.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {

			sql += " and  f.idNo like '%" + idCard + "%'";
		}

		if (hkstat.equals("1")) {

			sql += " and  j.sfyhw = 1";
		}

		if (hkstat.equals("2")) {

			sql += " and  j.sfyhw = 0 ";
		}

		if (!StringHelper.isEmpty(investorId)) {

			sql += " and j.investor_id = " + investorId;
		}

		if (!StringHelper.isEmpty(startDate)) {


			sql += " and ((substring(j.fkdz_time,1,10) >='" + startDate + "'";
			sql += " and substring(j.fkdz_time,4,2) >='" + startDate.substring(3, 5) + "')";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and (substring(j.fkdz_time,1,10) <='" + endDate + "'";
			sql += " and substring(j.fkdz_time,4,2) <='" + endDate.substring(3, 5) + "'))";
		}
		if (!StringHelper.isEmpty(hkqd)) {

			sql += " and j.hkqd <>'0'";
		}

		sql += " order by  j.fkdz_time desc";

		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public List<DataRow> getRecordListChg02(String userId, String realName,
			String phone, String startDate, String endDate, String commit,
			String idCard, String hkstat, String investorId, String investorName)
			throws Exception {

		String sql = "select j.id as jkid, u.id as id ,f.realName ,h.realName as investorName,INSERT(u.mobilePhone,4,4,'****')AS mobilePhone,INSERT(f.idNo,4,4,'****')AS idNo,j.yuq_ts,j.annualrate, j.jk_money, j.jk_date ,j.hkqd,j.sjsh_money ,j.sjds_money , j.sfyfk, j.fkdz_time,j.hkyq_time,j.sfyhw ,j.yuq_lx ,j.hk_time from "
				+ "   sd_new_jkyx j left join sd_user u on j.userid = u.id LEFT join sd_user_finance f on f.userId =j.userid left join sd_user_finance h on h.userId = j.cuishou_id  where  j.sfyfk <>0 ";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  f.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			System.out.println(phone);
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {

			sql += " and  f.idNo like '%" + idCard + "%'";
		}
		if (hkstat.equals("1")) {

			sql += " and  j.sfyhw = 1";
		}

		if (hkstat.equals("2")) {

			sql += " and  j.sfyhw = 0 ";
		}

		if (!StringHelper.isEmpty(investorId)) {

			sql += " and j.investor_id = " + investorId;
		}

		if (!StringHelper.isEmpty(startDate)) {


			sql += " and ((substring(j.fkdz_time,1,10) >='" + startDate + "'";
			sql += " and substring(j.fkdz_time,4,2) >='" + startDate.substring(3, 5) + "')";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and (substring(j.fkdz_time,1,10) <='" + endDate + "'";
			sql += " and substring(j.fkdz_time,4,2) <='" + endDate.substring(3, 5) + "'))";
		}

		sql += " order by  j.fkdz_time desc";
		return getJdbcTemplate().query(sql);
	}
	public Integer getSHQX(int cmsuserid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select xrcode from sdcms_user  where user_id = " + cmsuserid);

		return getJdbcTemplate().queryInt(sb.toString());
	}

	public DBPage getRecordListChg07(int curPage, int numPerPage,
			String userId, String realName, String phone, String startDate,
			String endDate, String commit, String idCard, String hkstat,
			String investorId, String investorName,String hkqd) throws Exception {

		String sql = "select j.id as jkid, u.id as id ,u.username as username,f.realName,f.idNo AS idNo, j.jk_date ,j.sjsh_money ,j.sjds_money,j.lx ,j.hkqd,j.hksb,j.hkpz,j.hkpz_time, j.sfyfk, j.fkdz_time,j.fkdz_time_day,j.fkr_time,j.hkyq_time,j.hkfq_time,j.hkfq_day,j.hkfq_code,j.sfyhw ,j.yuq_lx ,j.tzjx,j.hk_time,j.hongbaoid from "
				+ "   sd_new_jkyx j left join sd_user u on j.userid = u.id LEFT join sd_user_finance f on f.userId =j.userid where  j.sfyfk =1 ";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  f.realName like '%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {

			sql += " and  f.idNo like '%" + idCard + "%'";
		}

		/*if (hkstat.equals("1")) {

			sql += " and  j.sfyhw = 1";
		}

		if (hkstat.equals("2")) {
*/
			sql += " and  j.sfyhw = 0 ";
		//}

		if (!StringHelper.isEmpty(investorId)) {

			sql += " and j.investor_id = " + investorId;
		}

		if (!StringHelper.isEmpty(startDate)) {


			sql += " and ((substring(j.fkdz_time,1,10) >='" + startDate + "'";
			sql += " and substring(j.fkdz_time,4,2) >='" + startDate.substring(3, 5) + "')";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and (substring(j.fkdz_time,1,10) <='" + endDate + "'";
			sql += " and substring(j.fkdz_time,4,2) <='" + endDate.substring(3, 5) + "'))";
		}
		if (!StringHelper.isEmpty(hkqd)) {

			sql += " and j.hkqd <>'0'";
		}
		sql += " order by  j.hkqd desc, SUBSTRING(j.hkyq_time,7,4)  ,SUBSTRING(j.hkyq_time,4,2)  , SUBSTRING(j.hkyq_time,1,2)  ,SUBSTRING(j.hkyq_time,12,2) ,SUBSTRING(j.hkyq_time,15,2) ,SUBSTRING(j.hkyq_time,18,2)  ";

		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public List<DataRow> getRecordListChg07(String userId, String realName,
			String phone, String startDate, String endDate, String commit,
			String idCard, String hkstat, String investorId, String investorName)
			throws Exception {

		String sql = "select j.id as jkid, u.id as id ,f.realName ,h.Name as investorName,INSERT(u.mobilePhone,4,4,'****')AS mobilePhone,f.idNo AS idNo,j.yuq_ts,j.annualrate, j.jk_money, j.jk_date ,j.hkqd,j.hkpz,j.sjsh_money ,j.sjds_money , j.sfyfk, j.fkdz_time,j.hkyq_time,j.sfyhw ,j.yuq_lx ,j.hk_time from "
				+ "   sd_new_jkyx j left join sd_user u on j.userid = u.id LEFT join sd_user_finance f on f.userId =j.userid left join sdcms_user h on h.user_Id = j.cuishou_id  where  j.sfyfk =1 ";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  f.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {

			sql += " and  f.idNo like '%" + idCard + "%'";
		}
		if (hkstat.equals("1")) {

			sql += " and  j.sfyhw = 1";
		}

		if (hkstat.equals("2")) {

			sql += " and  j.sfyhw = 0 ";
		}

		if (!StringHelper.isEmpty(investorId)) {

			sql += " and j.investor_id = " + investorId;
		}

		if (!StringHelper.isEmpty(startDate)) {

			sql += " and (substring(j.fkdz_time,1,10) ='" + startDate + "'";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " or substring(j.fkdz_time,1,10) ='" + endDate + "')";
		}

		sql += " order by  j.fkdz_time desc";
		return getJdbcTemplate().query(sql);
	}
	public DBPage getRecordListChg03(int curPage, int numPerPage,
			String userId, String realName, String phone,String profession, String hongbaoid,String pingfen,String zffangshi,int temp,String jkqx,String refferee,String startDate,
			String endDate, String commit, String idCard, String hkstat,
			String investorId, String investorName,String off,String olduser) throws Exception {

		String sql = "select j.id as jkid, u.id as id ,u.username as username,f.realName ,h.NAME as investorName  , w.versoin, INSERT(u.mobilePhone,4,4,'****')AS mobilePhone,INSERT(f.idNo,4,4,'****')AS idNo, j.jk_money, j.jk_date ,j.sjsh_money ,j.sjds_money , j.hongbaoid,j.pipei,j.sfyfk, j.fkdz_time,j.fkdz_time_day,j.fkr_time,j.hkyq_time,j.hkfq_time,j.hkfq_code,j.hkfq_day,j.hkfq_lx,j.sfyhw ,j.yuq_lx ,j.yuq_whlx,j.hk_time,j.kfhf_cgfk,j.lx from "
				+ "   sd_new_jkyx j left join sd_user u on j.userid = u.id left join sd_withdraw w on j.fkdz_time = w.checktime and w.userid=j.userid LEFT join sd_user_finance f on f.userId =j.userid left join sdcms_user h on h.USER_ID= j.fkr  where  j.sfyfk = 1 ";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  f.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		
		if (!StringHelper.isEmpty(profession)) {
			
			sql += " and  u.profession =" + profession ;
		}
		if (!StringHelper.isEmpty(hongbaoid)) {
			
			sql += " and  u.hongbao =" + hongbaoid+" and j.hongbaoid="+ hongbaoid ;
		}
		if (!StringHelper.isEmpty(idCard)) {

			sql += " and  f.idNo like '%" + idCard + "%'";
		}
		if(!StringHelper.isEmpty(off) && off.equals("14")) {
			sql += " and username like 'TAFA%'";
		}
		if (!StringHelper.isEmpty(pingfen)) {
			
			sql += " and  j.pipei = " + pingfen;
		}
		if (!StringHelper.isEmpty(olduser)) {
			
			sql += " and  j.is_old_user = " + olduser;
		}
		if (temp == 11 || temp == 12) {
			if(temp == 11 && TextUtils.isEmpty(jkqx)){
				sql += " and  (j.jk_date = 1 or j.jk_date = 2)" ;
			}else if(temp == 12 && TextUtils.isEmpty(jkqx)){
				sql += " and  (j.jk_date = 3 or j.jk_date = 4)" ;
			}else{
				sql += " and  j.jk_date = " + jkqx;
			}
		}
		if (!StringHelper.isEmpty(refferee)) {
			
			sql += " and  u.refferee = '" + refferee+"'";
		}
		if (!StringHelper.isEmpty(zffangshi)) {
			
			sql += " and  w.versoin = " + zffangshi;
		}

		if (hkstat.equals("1")) {

			sql += " and  j.sfyhw = 1";
		}
		if (hkstat.equals("2")) {

			sql += " and  j.sfyhw = 0 ";
		}
		if (hkstat.equals("3")) {
			
			sql += " and  j.yuq_ts>0 and j.sfyhw = 1 ";
		}
		if (hkstat.equals("4")) {
			
			sql += " and  j.yuq_ts>0 and j.sfyhw = 0 ";
		}

		if (!StringHelper.isEmpty(investorId)) {

			sql += " and j.cl03_ren = " + investorId;
		}
		if (!StringHelper.isEmpty(investorName)) {

			sql += " and  h.NAME like '%" + investorName + "%'";
		}

		if (!StringHelper.isEmpty(startDate)) {
			
				sql += " and CONCAT(SUBSTRING(j.fkdz_time,7,4),'-',SUBSTRING(j.fkdz_time,4,2),'-',SUBSTRING(j.fkdz_time,1,2),SUBSTRING(j.fkdz_time,11,9)) >='" + startDate + "'";
				
				sql += " and CONCAT(SUBSTRING(j.fkdz_time,7,4),'-',SUBSTRING(j.fkdz_time,4,2),'-',SUBSTRING(j.fkdz_time,1,2)) <='" + endDate + "'";
		
		}

		sql += " order by j.sfyhw, SUBSTRING(j.fkdz_time,7,4) DESC ,SUBSTRING(j.fkdz_time,4,2) DESC , SUBSTRING(j.fkdz_time,1,2) DESC ,SUBSTRING(j.fkdz_time,12,2) DESC,SUBSTRING(j.fkdz_time,15,2) DESC,SUBSTRING(j.fkdz_time,18,2) DESC ";
		
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	
	public List<DataRow> getRecordListChgGLL(String userId, String realName,
			String phone, String profession,String hongbaoid,String pingfen,String zffangshi,int temp,String jkqx,String refferee,String startDate, String endDate, String commit,
			String idCard, String hkstat, String investorId, String investorName,String off,String olduser)
			throws Exception {

		String sql = "select j.id as jkid, u.id as id ,u.username,f.realName ,h.NAME as investorName  ,w.userid,w.versoin, INSERT(u.mobilePhone,4,4,'****')AS mobilePhone,INSERT(f.idNo,4,4,'****')AS idNo,j.yuq_ts,j.annualrate, j.jk_money,j.hongbaoid, j.jk_date ,j.lx ,j.sjsh_money ,j.sjds_money ,j.lx, j.sfyfk, j.fkdz_time,j.fkdz_time_day,j.hkyq_time,j.hkfq_time,j.hkfq_day,j.hkfq_code,j.hkfq_lx,j.sfyhw ,j.yuq_yhlx ,j.yuq_lx ,j.hkfqzlx,j.yuq_whlx,j.hk_time from "
				+ "   sd_new_jkyx j left join sd_user u on j.userid = u.id left join sd_withdraw w on j.fkdz_time = w.checktime and w.userid=j.userid LEFT join sd_user_finance f on f.userId =j.userid left join sdcms_user h on h.USER_ID = j.fkr  where  j.sfyfk =1 ";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  f.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(profession)) {
			
			sql += " and  u.profession =" + profession ;
		}
		if (!StringHelper.isEmpty(hongbaoid)) {
			
			sql += " and  u.hongbao =" + hongbaoid+" and j.hongbaoid="+ hongbaoid ;
		}
		if (!StringHelper.isEmpty(idCard)) {

			sql += " and  f.idNo like '%" + idCard + "%'";
		}
		if (!StringHelper.isEmpty(pingfen)) {
			
			sql += " and  j.pipei = " + pingfen;
		}
		if (!StringHelper.isEmpty(olduser)) {
			
			sql += " and  j.is_old_user = " + olduser;
		}
		if (!"0".equals(jkqx) && !StringHelper.isEmpty(jkqx)) {
			sql += " and  j.jk_date = " + jkqx;
		}
		if(!StringHelper.isEmpty(off) &&temp ==14) {
			sql += " and  u.username like 'TAFA%'";
		}
		if (!StringHelper.isEmpty(refferee)) {
			
			sql += " and  u.refferee = '" + refferee+"'";
		}
		if (!StringHelper.isEmpty(zffangshi)) {
			
			sql += " and  w.versoin = " + zffangshi;
		}
		if (hkstat.equals("1")) {

			sql += " and  j.sfyhw = 1";
		}

		if (hkstat.equals("2")) {

			sql += " and  j.sfyhw = 0 ";
		}
		if (hkstat.equals("3")) {
			
			sql += " and  j.yuq_ts>0 and j.sfyhw = 1 ";
		}
		if (hkstat.equals("4")) {
			
			sql += " and  j.yuq_ts>0 and j.sfyhw = 0 ";
		}

		if (!StringHelper.isEmpty(investorId)) {

			sql += " and j.cl03_ren = " + investorId;
		}
		if (!StringHelper.isEmpty(investorName)) {

			sql += " and  h.NAME like '%" + investorName + "%'";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
				sql += " and CONCAT(SUBSTRING(j.fkdz_time,7,4),'-',SUBSTRING(j.fkdz_time,4,2),'-',SUBSTRING(j.fkdz_time,1,2),SUBSTRING(j.fkdz_time,11,9)) >='" + startDate + "'";
				
				sql += " and CONCAT(SUBSTRING(j.fkdz_time,7,4),'-',SUBSTRING(j.fkdz_time,4,2),'-',SUBSTRING(j.fkdz_time,1,2)) <='" + endDate + "'";
		
		}
		sql += " and  ((j.jk_date=1 and substring(u.username,1,3)='M88' and (u.id>95000 or u.id=90003)) or (j.jk_date=2 and substring(u.username,1,4)='F168' and u.id>90000))";
		sql += " order by  j.fkdz_time desc";
		return getJdbcTemplate().query(sql);
	}
	public DBPage getRecordListChgGLL(int curPage, int numPerPage,
			String userId, String realName, String phone,String profession, String hongbaoid,String pingfen,String zffangshi,int temp,String jkqx,String refferee,String startDate,
			String endDate, String commit, String idCard, String hkstat,
			String investorId, String investorName,String off,String olduser) throws Exception {
		
		String sql = "select j.id as jkid, u.id as id ,u.username as username,f.realName ,h.NAME as investorName  , w.versoin, INSERT(u.mobilePhone,4,4,'****')AS mobilePhone,INSERT(f.idNo,4,4,'****')AS idNo, j.jk_money, j.jk_date ,j.sjsh_money ,j.sjds_money ,j.lx, j.hongbaoid,j.pipei,j.sfyfk, j.fkdz_time,j.fkdz_time_day,j.fkr_time,j.hkyq_time,j.hkfq_time,j.hkfq_code,j.hkfq_day,j.hkfq_lx,j.sfyhw ,j.yuq_lx ,j.yuq_whlx,j.hk_time,j.kfhf_cgfk from "
				+ "   sd_new_jkyx j left join sd_user u on j.userid = u.id left join sd_withdraw w on j.fkdz_time = w.checktime and w.userid=j.userid LEFT join sd_user_finance f on f.userId =j.userid left join sdcms_user h on h.USER_ID= j.fkr  where  j.sfyfk = 1 ";
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and  f.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		
		if (!StringHelper.isEmpty(profession)) {
			
			sql += " and  u.profession =" + profession ;
		}
		if (!StringHelper.isEmpty(hongbaoid)) {
			
			sql += " and  u.hongbao =" + hongbaoid+" and j.hongbaoid="+ hongbaoid ;
		}
		if (!StringHelper.isEmpty(idCard)) {
			
			sql += " and  f.idNo like '%" + idCard + "%'";
		}
		if(!StringHelper.isEmpty(off) && off.equals("14")) {
			sql += " and username like 'TAFA%'";
		}
		if (!StringHelper.isEmpty(pingfen)) {
			
			sql += " and  j.pipei = " + pingfen;
		}
		if (!StringHelper.isEmpty(olduser)) {
			
			sql += " and  j.is_old_user = " + olduser;
		}
		if (!"0".equals(jkqx) && !StringHelper.isEmpty(jkqx)) {
			sql += " and  j.jk_date = " + jkqx;
		}
		if (!StringHelper.isEmpty(refferee)) {
			
			sql += " and  u.refferee = '" + refferee+"'";
		}
		if (!StringHelper.isEmpty(zffangshi)) {
			
			sql += " and  w.versoin = " + zffangshi;
		}
		
		if (hkstat.equals("1")) {
			
			sql += " and  j.sfyhw = 1";
		}
		if (hkstat.equals("2")) {
			
			sql += " and  j.sfyhw = 0 ";
		}
		if (hkstat.equals("3")) {
			
			sql += " and  j.yuq_ts>0 and j.sfyhw = 1 ";
		}
		if (hkstat.equals("4")) {
			
			sql += " and  j.yuq_ts>0 and j.sfyhw = 0 ";
		}
		
		if (!StringHelper.isEmpty(investorId)) {
			
			sql += " and j.cl03_ren = " + investorId;
		}
		if (!StringHelper.isEmpty(investorName)) {
			
			sql += " and  h.NAME like '%" + investorName + "%'";
		}
		
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(j.fkdz_time,7,4),'-',SUBSTRING(j.fkdz_time,4,2),'-',SUBSTRING(j.fkdz_time,1,2),SUBSTRING(j.fkdz_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(j.fkdz_time,7,4),'-',SUBSTRING(j.fkdz_time,4,2),'-',SUBSTRING(j.fkdz_time,1,2)) <='" + endDate + "'";
			
		}
		sql += " and  ((j.jk_date=1 and substring(u.username,1,3)='M88' and (u.id>95000 or u.id=90003)) or (j.jk_date=2 and substring(u.username,1,4)='F168' and u.id>90000))";
		sql += " order by j.sfyhw, SUBSTRING(j.fkdz_time,7,4) DESC ,SUBSTRING(j.fkdz_time,4,2) DESC , SUBSTRING(j.fkdz_time,1,2) DESC ,SUBSTRING(j.fkdz_time,12,2) DESC,SUBSTRING(j.fkdz_time,15,2) DESC,SUBSTRING(j.fkdz_time,18,2) DESC ";
		
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	
	public List<DataRow> getRecordListChg03(String userId, String realName,
			String phone, String profession,String hongbaoid,String pingfen,String zffangshi,int temp,String jkqx,String refferee,String startDate, String endDate, String commit,
			String idCard, String hkstat, String investorId, String investorName,String off,String olduser)
					throws Exception {
		
		String sql = "select j.id as jkid, u.id as id ,u.username,f.realName ,h.NAME as investorName  ,w.userid,w.versoin, INSERT(u.mobilePhone,4,4,'****')AS mobilePhone,INSERT(f.idNo,4,4,'****')AS idNo,j.yuq_ts,j.annualrate, j.jk_money,j.hongbaoid, j.jk_date ,j.lx ,j.sjsh_money ,j.sjds_money , j.sfyfk, j.fkdz_time,j.fkdz_time_day,j.hkyq_time,j.hkfq_time,j.hkfq_day,j.hkfq_code,j.hkfq_lx,j.sfyhw ,j.yuq_yhlx ,j.yuq_lx ,j.hkfqzlx,j.yuq_whlx,j.hk_time from "
				+ "   sd_new_jkyx j left join sd_user u on j.userid = u.id left join sd_withdraw w on j.fkdz_time = w.checktime and w.userid=j.userid LEFT join sd_user_finance f on f.userId =j.userid left join sdcms_user h on h.USER_ID = j.fkr  where  j.sfyfk =1 ";
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and  f.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(profession)) {
			
			sql += " and  u.profession =" + profession ;
		}
		if (!StringHelper.isEmpty(hongbaoid)) {
			
			sql += " and  u.hongbao =" + hongbaoid+" and j.hongbaoid="+ hongbaoid ;
		}
		if (!StringHelper.isEmpty(idCard)) {
			
			sql += " and  f.idNo like '%" + idCard + "%'";
		}
		if (!StringHelper.isEmpty(pingfen)) {
			
			sql += " and  j.pipei = " + pingfen;
		}
		if (!StringHelper.isEmpty(olduser)) {
			
			sql += " and  j.is_old_user = " + olduser;
		}
		if (temp == 11 || temp == 12) {
			if(temp == 11 && TextUtils.isEmpty(jkqx)){
				sql += " and  (j.jk_date = 1 or j.jk_date = 2)" ;
			}else if(temp == 12 && TextUtils.isEmpty(jkqx)){
				sql += " and  (j.jk_date = 3 or j.jk_date = 4)" ;
			}else{
				sql += " and  j.jk_date = " + jkqx;
			}
		}
		if(!StringHelper.isEmpty(off) &&temp ==14) {
			sql += " and  u.username like 'TAFA%'";
		}
		if (!StringHelper.isEmpty(refferee)) {
			
			sql += " and  u.refferee = '" + refferee+"'";
		}
		if (!StringHelper.isEmpty(zffangshi)) {
			
			sql += " and  w.versoin = " + zffangshi;
		}
		if (hkstat.equals("1")) {
			
			sql += " and  j.sfyhw = 1";
		}
		
		if (hkstat.equals("2")) {
			
			sql += " and  j.sfyhw = 0 ";
		}
		if (hkstat.equals("3")) {
			
			sql += " and  j.yuq_ts>0 and j.sfyhw = 1 ";
		}
		if (hkstat.equals("4")) {
			
			sql += " and  j.yuq_ts>0 and j.sfyhw = 0 ";
		}
		
		if (!StringHelper.isEmpty(investorId)) {
			
			sql += " and j.cl03_ren = " + investorId;
		}
		if (!StringHelper.isEmpty(investorName)) {
			
			sql += " and  h.NAME like '%" + investorName + "%'";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(j.fkdz_time,7,4),'-',SUBSTRING(j.fkdz_time,4,2),'-',SUBSTRING(j.fkdz_time,1,2),SUBSTRING(j.fkdz_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(j.fkdz_time,7,4),'-',SUBSTRING(j.fkdz_time,4,2),'-',SUBSTRING(j.fkdz_time,1,2)) <='" + endDate + "'";
			
		}
		sql += " order by  j.fkdz_time desc";
		return getJdbcTemplate().query(sql);
	}
	public DBPage getRecordListChg111(int curPage, int numPerPage,
			String userId, String realName, String phone,String profession, String hongbaoid,String pingfen,String zffangshi,int temp,String jkqx,String refferee,String startDate,
			String endDate, String commit, String idCard, String hkstat,
			String investorId, String investorName,String off,String olduser) throws Exception {
		
		String sql = "select j.id as jkid, u.id as id ,u.username as username,f.realName ,h.NAME as investorName  , w.versoin, INSERT(u.mobilePhone,4,4,'****')AS mobilePhone,INSERT(f.idNo,4,4,'****')AS idNo, j.jk_money, j.jk_date ,j.sjsh_money ,j.sjds_money , j.hongbaoid,j.pipei,j.sfyfk, j.fkdz_time,j.fkdz_time_day,j.fkr_time,j.hkyq_time,j.hkfq_time,j.hkfq_code,j.hkfq_day,j.hkfq_lx,j.sfyhw ,j.yuq_lx ,j.yuq_whlx,j.hk_time,j.kfhf_cgfk from "
				+ "   sd_new_jkyx j left join sd_user u on j.userid = u.id left join sd_withdraw w on j.fkdz_time = w.checktime and w.userid=j.userid LEFT join sd_user_finance f on f.userId =j.userid left join sdcms_user h on h.USER_ID= j.fkr  where  j.sfyfk = 1  and  j.is_old_user =0 ";
		
		sql += " and CONCAT(SUBSTRING(j.fkdz_time,7,4),'-',SUBSTRING(j.fkdz_time,4,2),'-',SUBSTRING(j.fkdz_time,1,2)) >='2019-07-13' ";
		
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and  f.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		
		if (!StringHelper.isEmpty(profession)) {
			
			sql += " and  u.profession =" + profession ;
		}
		if (!StringHelper.isEmpty(hongbaoid)) {
			
			sql += " and  u.hongbao =" + hongbaoid+" and j.hongbaoid="+ hongbaoid ;
		}
		if (!StringHelper.isEmpty(idCard)) {
			
			sql += " and  f.idNo like '%" + idCard + "%'";
		}
		if(!StringHelper.isEmpty(off) && off.equals("14")) {
			sql += " and username like 'TAFA%'";
		}
		if (!StringHelper.isEmpty(pingfen)) {
			
			sql += " and  j.pipei = " + pingfen;
		}
		if (!StringHelper.isEmpty(olduser)) {
			
			sql += " and  j.is_old_user = " + olduser;
		}
		if (temp == 11 || temp == 12) {
			if(temp == 11 && TextUtils.isEmpty(jkqx)){
				sql += " and  (j.jk_date = 1 or j.jk_date = 2)" ;
			}else if(temp == 12 && TextUtils.isEmpty(jkqx)){
				sql += " and  (j.jk_date = 3 or j.jk_date = 4)" ;
			}else{
				sql += " and  j.jk_date = " + jkqx;
			}
		}
		if (!StringHelper.isEmpty(refferee)) {
			
			sql += " and  u.refferee = '" + refferee+"'";
		}
		if (!StringHelper.isEmpty(zffangshi)) {
			
			sql += " and  w.versoin = " + zffangshi;
		}
		
		if (hkstat.equals("1")) {
			
			sql += " and  j.sfyhw = 1";
		}
		if (hkstat.equals("2")) {
			
			sql += " and  j.sfyhw = 0 ";
		}
		if (hkstat.equals("3")) {
			
			sql += " and  j.yuq_ts>0 and j.sfyhw = 1 ";
		}
		if (hkstat.equals("4")) {
			
			sql += " and  j.yuq_ts>0 and j.sfyhw = 0 ";
		}
		if (hkstat.equals("5")) {
			
			sql += " and  j.hkfq_time IS NOT NULL  and j.sfyhw = 1 ";
		}
		if (hkstat.equals("6")) {
			
			sql += " and  j.j.hkfq_time IS NOT NULL and j.sfyhw = 0 ";
		}
		
		if (!StringHelper.isEmpty(investorId)) {
			
			sql += " and j.cl03_ren = " + investorId;
		}
		if (!StringHelper.isEmpty(investorName)) {
			
			sql += " and  h.NAME like '%" + investorName + "%'";
		}
		
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(j.fkdz_time,7,4),'-',SUBSTRING(j.fkdz_time,4,2),'-',SUBSTRING(j.fkdz_time,1,2),SUBSTRING(j.fkdz_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(j.fkdz_time,7,4),'-',SUBSTRING(j.fkdz_time,4,2),'-',SUBSTRING(j.fkdz_time,1,2)) <='" + endDate + "'";
			
		}
		
		sql += " order by j.sfyhw, SUBSTRING(j.fkdz_time,7,4) DESC ,SUBSTRING(j.fkdz_time,4,2) DESC , SUBSTRING(j.fkdz_time,1,2) DESC ,SUBSTRING(j.fkdz_time,12,2) DESC,SUBSTRING(j.fkdz_time,15,2) DESC,SUBSTRING(j.fkdz_time,18,2) DESC ";
		
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public List<DataRow> getRecordListChg111(String userId, String realName,
			String phone, String profession,String hongbaoid,String pingfen,String zffangshi,int temp,String jkqx,String refferee,String startDate, String endDate, String commit,
			String idCard, String hkstat, String investorId, String investorName,String off,String olduser)
					throws Exception {
		
		String sql = "select j.id as jkid, u.id as id ,u.username,f.realName ,h.NAME as investorName  ,w.userid,w.versoin, INSERT(u.mobilePhone,4,4,'****')AS mobilePhone,INSERT(f.idNo,4,4,'****')AS idNo,j.yuq_ts,j.annualrate, j.jk_money,j.hongbaoid, j.jk_date ,j.lx ,j.sjsh_money ,j.sjds_money , j.sfyfk, j.fkdz_time,j.fkdz_time_day,j.hkyq_time,j.hkfq_time,j.hkfq_day,j.hkfq_code,j.hkfq_lx,j.sfyhw ,j.yuq_yhlx ,j.yuq_lx ,j.hkfqzlx,j.yuq_whlx,j.hk_time from "
				+ "   sd_new_jkyx j left join sd_user u on j.userid = u.id left join sd_withdraw w on j.fkdz_time = w.checktime and w.userid=j.userid LEFT join sd_user_finance f on f.userId =j.userid left join sdcms_user h on h.USER_ID = j.fkr  where  j.sfyfk =1  and  j.is_old_user =0 ";
		
		sql += " and CONCAT(SUBSTRING(j.fkdz_time,7,4),'-',SUBSTRING(j.fkdz_time,4,2),'-',SUBSTRING(j.fkdz_time,1,2)) >='2019-07-13' ";
		
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and  f.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(profession)) {
			
			sql += " and  u.profession =" + profession ;
		}
		if (!StringHelper.isEmpty(hongbaoid)) {
			
			sql += " and  u.hongbao =" + hongbaoid+" and j.hongbaoid="+ hongbaoid ;
		}
		if (!StringHelper.isEmpty(idCard)) {
			
			sql += " and  f.idNo like '%" + idCard + "%'";
		}
		if (!StringHelper.isEmpty(pingfen)) {
			
			sql += " and  j.pipei = " + pingfen;
		}
		if (!StringHelper.isEmpty(olduser)) {
			
			sql += " and  j.is_old_user = " + olduser;
		}
		if (temp == 11 || temp == 12) {
			if(temp == 11 && TextUtils.isEmpty(jkqx)){
				sql += " and  (j.jk_date = 1 or j.jk_date = 2)" ;
			}else if(temp == 12 && TextUtils.isEmpty(jkqx)){
				sql += " and  (j.jk_date = 3 or j.jk_date = 4)" ;
			}else{
				sql += " and  j.jk_date = " + jkqx;
			}
		}
		if(!StringHelper.isEmpty(off) &&temp ==14) {
			sql += " and  u.username like 'TAFA%'";
		}
		if (!StringHelper.isEmpty(refferee)) {
			
			sql += " and  u.refferee = '" + refferee+"'";
		}
		if (!StringHelper.isEmpty(zffangshi)) {
			
			sql += " and  w.versoin = " + zffangshi;
		}
		if (hkstat.equals("1")) {
			
			sql += " and  j.sfyhw = 1";
		}
		
		if (hkstat.equals("2")) {
			
			sql += " and  j.sfyhw = 0 ";
		}
		if (hkstat.equals("3")) {
			
			sql += " and  j.yuq_ts>0 and j.sfyhw = 1 ";
		}
		if (hkstat.equals("4")) {
			
			sql += " and  j.yuq_ts>0 and j.sfyhw = 0 ";
		}
		
		if (!StringHelper.isEmpty(investorId)) {
			
			sql += " and j.cl03_ren = " + investorId;
		}
		if (!StringHelper.isEmpty(investorName)) {
			
			sql += " and  h.NAME like '%" + investorName + "%'";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
			sql += " and CONCAT(SUBSTRING(j.fkdz_time,7,4),'-',SUBSTRING(j.fkdz_time,4,2),'-',SUBSTRING(j.fkdz_time,1,2),SUBSTRING(j.fkdz_time,11,9)) >='" + startDate + "'";
			
			sql += " and CONCAT(SUBSTRING(j.fkdz_time,7,4),'-',SUBSTRING(j.fkdz_time,4,2),'-',SUBSTRING(j.fkdz_time,1,2)) <='" + endDate + "'";
			
		}
		sql += " order by  j.fkdz_time desc";
		return getJdbcTemplate().query(sql);
	}
	public int getCreateTimeByDate(String startDate,String endDate){
		String sql=" select count(id) from sd_user  where SUBSTRING(yearmonthday,1,10)>='2019-07-13' ";
		if (!StringHelper.isEmpty(startDate)) {
			sql += "  and SUBSTRING(yearmonthday,1,10)>='"+startDate+"' and SUBSTRING(yearmonthday,1,10)<='"+endDate+"' ";
			
		}
		return getJdbcTemplate().queryInt(sql);
	}
	public int getZShenqingJK(String startDate,String endDate){
		String sql=" select COUNT(DISTINCT userid) from sd_new_jkyx where is_old_user=0  and SUBSTRING(daytime,1,10)>='2019-07-13' ";
		if (!StringHelper.isEmpty(startDate)) {
			sql += "  and SUBSTRING(daytime,1,10)>='"+startDate+"' and SUBSTRING(daytime,1,10)<='"+endDate+"' ";
			
		}
		return getJdbcTemplate().queryInt(sql);
	}
	public List<DataRow> getRecordListChgH5(String userId, String realName,
			String phone, String profession,String hongbaoid,String pingfen,String zffangshi,int temp,String jkqx,String refferee,String startDate, String endDate, String commit,
			String idCard, String hkstat, String investorId, String investorName)
			throws Exception {

		String sql = "select j.id as jkid, u.id as id ,f.realName ,u.username as uname ,h.NAME as investorName  ,w.userid,w.versoin, INSERT(u.mobilePhone,4,4,'****')AS mobilePhone,INSERT(f.idNo,4,4,'****')AS idNo,j.yuq_ts,j.annualrate, j.jk_money,j.hongbaoid, j.jk_date ,j.lx ,j.sjsh_money ,j.sjds_money , j.sfyfk, j.fkdz_time,j.fkdz_time_day,j.hkyq_time,j.hkfq_time,j.hkfq_day,j.hkfq_code,j.hkfq_lx,j.sfyhw ,j.yuq_yhlx ,j.yuq_lx ,j.hkfqzlx,j.yuq_whlx,j.hk_time from "
				+ "   sd_new_jkyx j left join sd_user u on j.userid = u.id left join sd_withdraw w on j.fkdz_time = w.checktime and w.userid=j.userid LEFT join sd_user_finance f on f.userId =j.userid left join sdcms_user h on h.USER_ID = j.fkr  where  j.sfyfk =1 and uname like 'TAFA%'";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  f.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(profession)) {
			
			sql += " and  u.profession =" + profession ;
		}
		if (!StringHelper.isEmpty(hongbaoid)) {
			
			sql += " and  u.hongbao =" + hongbaoid+" and j.hongbaoid="+ hongbaoid ;
		}
		if (!StringHelper.isEmpty(idCard)) {

			sql += " and  f.idNo like '%" + idCard + "%'";
		}
		if (!StringHelper.isEmpty(pingfen)) {
			
			sql += " and  j.pipei = " + pingfen;
		}
		if (temp == 11 || temp == 12) {
			if(temp == 11 && TextUtils.isEmpty(jkqx)){
				sql += " and  (j.jk_date = 1 or j.jk_date = 2)" ;
			}else if(temp == 12 && TextUtils.isEmpty(jkqx)){
				sql += " and  (j.jk_date = 3 or j.jk_date = 4)" ;
			}else{
				sql += " and  j.jk_date = " + jkqx;
			}
		}
		if (!StringHelper.isEmpty(refferee)) {
			
			sql += " and  u.refferee = '" + refferee+"'";
		}
		if (!StringHelper.isEmpty(zffangshi)) {
			
			sql += " and  w.versoin = " + zffangshi;
		}
		if (hkstat.equals("1")) {

			sql += " and  j.sfyhw = 1";
		}

		if (hkstat.equals("2")) {

			sql += " and  j.sfyhw = 0 ";
		}
		if (hkstat.equals("3")) {
			
			sql += " and  j.yuq_ts>0 and j.sfyhw = 1 ";
		}
		if (hkstat.equals("4")) {
			
			sql += " and  j.yuq_ts>0 and j.sfyhw = 0 ";
		}

		if (!StringHelper.isEmpty(investorId)) {

			sql += " and j.cl03_ren = " + investorId;
		}
		if (!StringHelper.isEmpty(investorName)) {

			sql += " and  h.NAME like '%" + investorName + "%'";
		}
		if (!StringHelper.isEmpty(startDate)) {
			
				sql += " and CONCAT(SUBSTRING(j.fkdz_time,7,4),'-',SUBSTRING(j.fkdz_time,4,2),'-',SUBSTRING(j.fkdz_time,1,2),SUBSTRING(j.fkdz_time,11,9)) >='" + startDate + "'";
				
				sql += " and CONCAT(SUBSTRING(j.fkdz_time,7,4),'-',SUBSTRING(j.fkdz_time,4,2),'-',SUBSTRING(j.fkdz_time,1,2)) <='" + endDate + "'";
		
		}
		sql += " order by  j.fkdz_time desc";
		return getJdbcTemplate().query(sql);
	}
	
	public List<DataRow> getRecordListChg13(int i)
					throws Exception {
		String sql = "select j.id as jkid, u.id as id ,f.realName ,h.NAME as investorName  , INSERT(u.mobilePhone,4,4,'****')AS mobilePhone,INSERT(f.idNo,4,4,'****')AS idNo,j.yuq_ts,j.annualrate, j.jk_money, j.jk_date ,j.sjsh_money ,j.sjds_money , j.sfyfk, j.fkdz_time,j.hkyq_time,j.sfyhw ,j.yuq_lx ,j.yuq_whlx,j.hk_time from "
				+ "   sd_new_jkyx j left join sd_user u on j.userid = u.id LEFT join sd_user_finance f on f.userId =j.userid left join sdcms_user h on h.USER_ID = j.fkr  where  j.sfyfk =1 ";
		sql += " and substring(j.fkdz_time,1,2) >='01'";
		sql += " and substring(j.fkdz_time,5,1) =" + i;
		sql += " and substring(j.fkdz_time,7,4) ='2018'";
		sql += " and substring(j.fkdz_time,1,2) <='31'";
		sql += " order by  j.fkdz_time desc";
		return getJdbcTemplate().query(sql);
	}
	public DBPage getRecordListChg05(int curPage, int numPerPage,
			String userId, String realName, String phone, String startDate,
			String endDate, String commit, String idCard, String hkstat,
			String investorId, String investorName) throws Exception {

		String sql = "select j.id as jkid, u.id as id ,f.realName ,h.NAME as investorName  ,  INSERT(u.mobilePhone,4,4,'****')AS mobilePhone ,INSERT(f.idNo,4,4,'****')AS idNo, j.jk_money, j.jk_date ,j.sjsh_money ,j.sjds_money , j.sfyfk, j.fkdz_time,j.hkyq_time,j.sfyhw ,j.yuq_lx ,j.hk_time from "
				+ "   sd_new_jkyx j left join sd_user u on j.userid = u.id LEFT join sd_user_finance f on f.userId =j.userid left join sdcms_user h on h.USER_ID= j.cl03_ren  where  j.sfyfk =1 ";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  f.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {

			sql += " and  f.idNo like '%" + idCard + "%'";
		}

		if (hkstat.equals("1")) {

			sql += " and  j.sfyhw = 1";
		}

		if (hkstat.equals("2")) {

			sql += " and  j.sfyhw = 0 ";
		}

		if (!StringHelper.isEmpty(investorId)) {

			sql += " and j.cl03_ren = " + investorId;
		}
		if (!StringHelper.isEmpty(investorName)) {

			sql += " and  h.NAME like '%" + investorName + "%'";
		}

		if (!StringHelper.isEmpty(startDate)) {


			sql += " and ((substring(j.fkdz_time,1,10) >='" + startDate + "'";
			sql += " and substring(j.fkdz_time,4,2) >='" + startDate.substring(3, 5) + "')";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and (substring(j.fkdz_time,1,10) <='" + endDate + "'";
			sql += " and substring(j.fkdz_time,4,2) <='" + endDate.substring(3, 5) + "'))";
		}

		sql += " order by  j.fkdz_time desc";

		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public List<DataRow> getRecordListChg05(String userId, String realName,
			String phone, String startDate, String endDate, String commit,
			String idCard, String hkstat, String investorId, String investorName)
			throws Exception {

		String sql = "select j.id as jkid, u.id as id ,f.realName ,h.NAME as investorName  ,  INSERT(u.mobilePhone,4,4,'****')AS mobilePhone ,INSERT(f.idNo,4,4,'****')AS idNo,j.yuq_ts,j.annualrate, j.jk_money, j.jk_date ,j.sjsh_money ,j.sjds_money , j.sfyfk, j.fkdz_time,j.hkyq_time,j.sfyhw ,j.yuq_lx ,j.hk_time from "
				+ "   sd_new_jkyx j left join sd_user u on j.userid = u.id LEFT join sd_user_finance f on f.userId =j.userid left join sdcms_user h on h.USER_ID = j.cl03_ren  where  j.sfyfk =1 ";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  f.realName like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {

			sql += " and  f.idNo like '%" + idCard + "%'";
		}
		if (hkstat.equals("1")) {

			sql += " and  j.sfyhw = 1";
		}

		if (hkstat.equals("2")) {

			sql += " and  j.sfyhw = 0 ";
		}

		if (!StringHelper.isEmpty(investorId)) {

			sql += " and j.cl03_ren = " + investorId;
		}
		if (!StringHelper.isEmpty(investorName)) {

			sql += " and  h.NAME like '%" + investorName + "%'";
		}
		if (!StringHelper.isEmpty(startDate)) {


			sql += " and ((substring(j.fkdz_time,1,10) >='" + startDate + "'";
			sql += " and substring(j.fkdz_time,4,2) >='" + startDate.substring(3, 5) + "')";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and (substring(j.fkdz_time,1,10) <='" + endDate + "'";
			sql += " and substring(j.fkdz_time,4,2) <='" + endDate.substring(3, 5) + "'))";
		}

		sql += " order by  j.fkdz_time desc";
		return getJdbcTemplate().query(sql);
	}
	public DBPage getRecharge(int curPage, int numPerPage,
			String userId, String realName, String phone,  Integer startmoney,String startDate,
			String endDate) throws Exception {

		String sql = "select USER_ID,Name , PHONE ,STARTM from sdcms_user u where 1=1 ";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.USER_ID =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  u.NAME like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  u.PHONE like '%" + phone + "%'";
		}

		if (!StringHelper.isEmpty(startDate)) {

			sql += " and u.CREATE_DATE >'" + startDate + "'";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and u.CREATE_DATE <'" + endDate + "'";
		}

		sql += " order by  u.CREATE_DATE desc";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}

	public List<DataRow> getRecharge(
			String userId, String realName, String phone,  Integer startmoney,String startDate,
			String endDate)
			throws Exception {

		String sql = "select USER_ID,Name , PHONE ,STARTM from sdcms_user u where 1=1 ";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  u.USER_ID =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  u.NAME like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			System.out.println(phone);
			sql += " and  u.PHONE like '%" + phone + "%'";
		}
		
		if (!StringHelper.isEmpty(startDate)) {

			sql += " and u.CREATE_DATE >'" + startDate + "'";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and u.CREATE_DATE <'" + endDate + "'";
		}

		sql += " order by  u.CREATE_DATE desc";
		return getJdbcTemplate().query(sql);
	}
	public void addRecharge(String tableName,DataRow dr){
		this.getJdbcTemplate().insert(tableName, dr);
	}
	 public void updateRecharge(DataRow dr)
		{
			getJdbcTemplate().update("sdcms_user", dr, "USER_ID", dr.getString("USER_ID"));
		}
	 public void updateChangeHKFS(DataRow dr)
	 {
		 getJdbcTemplate().update("sd_new_jkyx", dr, "id", dr.getString("id"));
	 }
	public List<DataRow> getReSu(String userId,String realName,String phone,String startDate,String endDate){
		String sql = "select user_id,name,phone,create_date,rechargem,czid from sdcms_userrecharge u where 1=1";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  USER_ID =" + userId;
		}
		if (!StringHelper.isEmpty(startDate)) {

			sql += " and CREATE_DATE >'" + startDate + "'";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and CREATE_DATE <'" + endDate + "'";
		}
		sql += " order by  u.CREATE_DATE desc";
		return getJdbcTemplate().query(sql);
	}
	public DBPage getReSu(int curPage,int numPerPage,String userId,String realName,String phone,String startDate,String endDate){
		String sql = "select user_id,name,phone,create_date,rechargem,czid from sdcms_userrecharge u where 1=1";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  USER_ID =" + userId;
		}
		if (!StringHelper.isEmpty(startDate)) {

			sql += " and CREATE_DATE >'" + startDate + "'";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and CREATE_DATE <'" + endDate + "'";
		}
		sql += " order by  u.CREATE_DATE desc";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public List<DataRow> getReSuTX(String userId,String realName,String phone,String startDate,String endDate){
		String sql = "select user_id,name,phone,create_date,rechargem,czid from sdcms_userrechargetx u where 1=1";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  USER_ID =" + userId;
		}
		if (!StringHelper.isEmpty(startDate)) {

			sql += " and CREATE_DATE >'" + startDate + "'";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and CREATE_DATE <'" + endDate + "'";
		}
		sql += " order by  u.CREATE_DATE desc";
		return getJdbcTemplate().query(sql);
	}
	public DBPage getReSuTX(int curPage,int numPerPage,String userId,String realName,String phone,String startDate,String endDate){
		String sql = "select user_id,name,phone,create_date,rechargem,czid from sdcms_userrechargetx u where 1=1";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  USER_ID =" + userId;
		}
		if (!StringHelper.isEmpty(startDate)) {

			sql += " and CREATE_DATE >'" + startDate + "'";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and CREATE_DATE <'" + endDate + "'";
		}
		sql += " order by  u.CREATE_DATE desc";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public List<DataRow> getReSuCms(String userId,String realName,String phone,String startDate,String endDate){
		String sql = "select user_id,name,phone,create_date,rechargem,czid from sdcms_userrecharge u where 1=1";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  USER_ID =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  NAME like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			sql += " and  PHONE like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(startDate)) {

			sql += " and CREATE_DATE >'" + startDate + "'";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and CREATE_DATE <'" + endDate + "'";
		}
		sql += " order by  u.CREATE_DATE desc";
		return getJdbcTemplate().query(sql);
	}
	public DBPage getReSuCms(int curPage,int numPerPage,String userId,String realName,String phone,String startDate,String endDate){
		String sql = "select user_id,name,phone,create_date,rechargem,czid from sdcms_userrecharge u where 1=1";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  USER_ID =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  NAME like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			sql += " and  PHONE like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(startDate)) {

			sql += " and CREATE_DATE >'" + startDate + "'";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and CREATE_DATE <'" + endDate + "'";
		}
		sql += " order by  u.CREATE_DATE desc";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public List<DataRow> getReSuTXCms(String userId,String realName,String phone,String startDate,String endDate){
		String sql = "select user_id,name,phone,create_date,rechargem,czid from sdcms_userrechargetx u where 1=1";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  USER_ID =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  NAME like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  PHONE like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(startDate)) {

			sql += " and CREATE_DATE >'" + startDate + "'";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and CREATE_DATE <'" + endDate + "'";
		}
		sql += " order by  u.CREATE_DATE desc";
		return getJdbcTemplate().query(sql);
	}
	public DBPage getReSuTXCms(int curPage,int numPerPage,String userId,String realName,String phone,String startDate,String endDate){
		String sql = "select user_id,name,phone,create_date,rechargem,czid from sdcms_userrechargetx u where 1=1";
		if (!StringHelper.isEmpty(userId)) {

			sql += " and  USER_ID =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {

			sql += " and  NAME like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {

			sql += " and  PHONE like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(startDate)) {

			sql += " and CREATE_DATE >'" + startDate + "'";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and CREATE_DATE <'" + endDate + "'";
		}
		sql += " order by  u.CREATE_DATE desc";
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public int getStartM(String userId){
		String sql = "select startm from sdcms_user where user_id='"+userId+"'";
		return getJdbcTemplate().queryInt(sql);
	}
	public int getStartMN(String name){
		String sql = "select startm from sdcms_user where name like'%"+name+"%'";
		return getJdbcTemplate().queryInt(sql);
	}
	public int getStartMsum(String userId){
		String sql = "select sum(startm) from sdcms_user ";
		return getJdbcTemplate().queryInt(sql);
	}
	public String getNameCms(int userId){
		String sql = "select name from sdcms_user where user_id='"+userId+"'";
		return getJdbcTemplate().queryString(sql);
	}
	public List<DataRow> getRecordListChg04(String startDate, String endDate, String commit,
			 String investorId,String investorName)
			throws Exception {

		String sql = "select j.id as jkid, u.id as id ,f.realName ,h.NAME as investorName  , INSERT(u.mobilePhone,4,4,'****')AS mobilePhone ,INSERT(f.idNo,4,4,'****')AS idNo,j.yuq_ts,j.annualrate, j.jk_money, j.jk_date ,j.sjsh_money ,j.sjds_money , j.sfyfk, j.fkdz_time,j.hkyq_time,j.sfyhw ,j.yuq_lx ,j.hk_time from "
				+ "   sd_new_jkyx j left join sd_user u on j.userid = u.id LEFT join sd_user_finance f on f.userId =j.userid left join sdcms_user h on h.USER_ID = j.cl03_ren  where  j.sfyfk =1 ";
		
		if (!StringHelper.isEmpty(investorId)) {

			sql += " and j.cl03_ren = " + investorId;
		}
		if (!StringHelper.isEmpty(investorName)) {

			sql += " and  h.NAME like '%" + investorName + "%'";
		}
		if (!StringHelper.isEmpty(startDate)) {

			sql += " and j.hk_time >'" + startDate + "'";
		}
		if (!StringHelper.isEmpty(endDate)) {

			sql += " and j.hk_time <'" + endDate + "'";
		}

		return getJdbcTemplate().query(sql);
	}
	public List<DataRow> getAllDxinList() {
		String sql = " select username ,mobilePhone from sd_user  where createTime <'2017-04-01' and yhbd =0   ";
		return getJdbcTemplate().query(sql);
	}

	public List<DataRow> getAllTxList() {
		String sql = " select u.username ,u.mobilePhone  from sd_new_jkyx j left join sd_user u on j.userid = u.id where j.cl03_status =1 and  sfyhw =0  ";
		return getJdbcTemplate().query(sql);
	}
	public DataRow getUserInfo(int  jkid)
	{
		String sql = "select mobilePhone,realname,sjsh_money,hkyq_time,username from sd_user left join sd_user_finance on sd_user.id=sd_user_finance.userid left join sd_new_jkyx on sd_user.id=sd_new_jkyx.userid where sd_new_jkyx.id = " +jkid ;
		return getJdbcTemplate().queryMap(sql);
	}
	  public Integer getUserId(int jkid){
			StringBuffer sb = new StringBuffer();
			sb.append("select userid from sd_new_jkyx  where id="+jkid );
			return getJdbcTemplate().queryInt(sb.toString());
		}
		//xiong20190620-每天老用户详细审核失败原因
		public DBPage getRecordListold(int curPage, int numPerPage,
				String userId, String realName, String phone, String commit,
				String idCard,String cmsid, String daytime) throws Exception {
			String sql = "select j.id as jkid, u.id as id ,f.realName , INSERT(u.mobilePhone,4,4,'****')AS mobilePhone  ,INSERT(f.idNo,4,4,'****')AS idNo, j.jk_money, j.create_date ,j.cl_status ,j.cl_yj  , s.name ,"
					+ "j.cl_time  ,j.cl02_yj ,j.cl02_status ,j.cl02_ren ,j.cl_ren ,j.cl03_ren , j.cl02_time  ,j.cl03_yj ,j.cl03_status ,j.cl03_time as cl03_time from sd_new_jkyx j left join sd_user u on j.userid = u.id LEFT join sd_user_finance f on f.userId =j.userid left join sdcms_user s on s.USER_ID = IFNULL(j.cl_ren,IFNULL(j.cl02_ren,j.cl03_ren))  "
					+ " where (j.cl_status=3 or j.cl02_status=3 or j.cl03_status=3) and j.is_old_user=1  AND j.userid IN  (SELECT  DISTINCT a.userid FROM sd_new_jkyx a WHERE a.sfyfk =1 AND a.sfyhw =1 AND a.daytime <j.daytime) AND SUBSTRING(j.daytime,1,10)='"+ daytime + "'" ;
			if (!StringHelper.isEmpty(userId)) {

				sql += " and  u.id =" + userId;
			}
			if (!StringHelper.isEmpty(realName)) {

				sql += " and  f.realName like'%" + realName + "%'";
			}
			if (!StringHelper.isEmpty(phone)) {

				sql += " and  u.mobilePhone like '%" + phone + "%'";
			}
			if (!StringHelper.isEmpty(idCard)) {

				sql += " and  f.idNo like '%" + idCard + "%'";
			}
			if (!StringHelper.isEmpty(cmsid)) {
				
				sql += " and  j.cl03_ren=" + cmsid ;
			}

			sql += "  GROUP BY j.userId order by j.id DESC";

			//sql +="  GROUP BY daytime ORDER BY daytime DESC";
			return getJdbcTemplate().queryPage(sql, curPage, numPerPage);

		}
  
   public int getZongzhuce(){
		String sql="select count(id) from sd_user ";
		
		return this.getJdbcTemplate().queryInt(sql);
	}
   public int getZongrenzheng(){
	   String sql="select count(id) from sd_user where vipstatus=1 ";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongshenqing(){
	   String sql="select count(DISTINCT userid) from sd_new_jkyx ";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongfk(){
	   String sql="select count(id) from sd_new_jkyx where sfyfk=1 ";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongdfk(){
	   String sql="select count(id) from sd_new_jkyx where sfyfk=2 ";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public double getZongfkbj(){
	   String sql="select SUM(REPLACE(sjds_money, ',' ,'')) from sd_new_jkyx where sfyfk=1 ";
	   
	   return this.getJdbcTemplate().queryLong(sql);
   }
   public double getZongzdye(){
	   String sql="select SUM(REPLACE(sjds_money, ',' ,'')) from sd_new_jkyx where sfyfk=1 and sfyhw=0 ";
	   
	   return this.getJdbcTemplate().queryLong(sql);
   }
   public double getZongfjds(){
	   String sql="select SUM(REPLACE(sjds_money, ',' ,'')) from sd_new_jkyx where sfyfk=1 and is_old_user=1 ";
	   
	   return this.getJdbcTemplate().queryLong(sql);
   }
   public double getZongzydq(String datenow){
	   String sql="select SUM(REPLACE(sjds_money, ',' ,'')) from sd_new_jkyx where sfyfk=1 and ((hkfq_code=0 and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))<='"+datenow+"') or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))<='"+datenow+"')";
	   
	   return this.getJdbcTemplate().queryLong(sql);
   }
   public double getZongzydqyq(String datenow){
	   String sql="select SUM(REPLACE(sjds_money, ',' ,'')) from sd_new_jkyx where sfyfk=1 and sfyhw=0 and yuq_ts>0 and ((hkfq_code=0 and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))<='"+datenow+"') or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))<='"+datenow+"')";
	   
	   return this.getJdbcTemplate().queryLong(sql);
   }
   public double getZongzydqtqhw(String datenow){
	   String sql="select SUM(REPLACE(sjds_money, ',' ,'')) from sd_new_jkyx where sfyfk=1 and sfyhw=1 and yuq_ts=0 and ((hkfq_code=0 and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))<='"+datenow+"') or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))<='"+datenow+"')";
	   
	   return this.getJdbcTemplate().queryLong(sql);
   }
   public double getZongzydqyh(String datenow){
	   String sql="select SUM(REPLACE(sjds_money, ',' ,'')) from sd_new_jkyx where sfyfk=1 and sfyhw=1 and ((hkfq_code=0 and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))<='"+datenow+"') or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))<='"+datenow+"')";
	   
	   return this.getJdbcTemplate().queryLong(sql);
   }
   public double getZongzydqold(String datenow){
	   String sql="select SUM(REPLACE(sjds_money, ',' ,'')) from sd_new_jkyx where is_old_user=1 and sfyfk=1 and ((hkfq_code=0 and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))<='"+datenow+"') or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))<='"+datenow+"')";
	   
	   return this.getJdbcTemplate().queryLong(sql);
   }
   public double getZongzydqnew(String datenow){
	   String sql="select SUM(REPLACE(sjds_money, ',' ,'')) from sd_new_jkyx where is_old_user=0 and sfyfk=1 and ((hkfq_code=0 and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))<='"+datenow+"') or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))<='"+datenow+"')";
	   
	   return this.getJdbcTemplate().queryLong(sql);
   }
   public double getZongdqhkje(String datenow){
	   String sql="select SUM(REPLACE(rechargeMoney, ',' ,'')) from sd_recharge_detail left join sd_new_jkyx on sd_recharge_detail.rechargeNumber=sd_new_jkyx.id where nowstate=1 and sfyfk=1 and ((hkfq_code=0 and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))<='"+datenow+"') or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))<='"+datenow+"')";
	   
	   return this.getJdbcTemplate().queryLong(sql);
   }
   public double getZongdqhkjeyq(String datenow){
	   String sql="select SUM(REPLACE(rechargeMoney, ',' ,'')) from sd_recharge_detail left join sd_new_jkyx on sd_recharge_detail.rechargeNumber=sd_new_jkyx.id where nowstate=1 and sfyfk=1 and sd_new_jkyx.yuq_ts>0 and ((hkfq_code=0 and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))<='"+datenow+"') or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))<='"+datenow+"')";
	   
	   return this.getJdbcTemplate().queryLong(sql);
   }
   public double getZongdqhkjetqhw(String datenow){
	   String sql="select SUM(REPLACE(rechargeMoney, ',' ,'')) from sd_recharge_detail left join sd_new_jkyx on sd_recharge_detail.rechargeNumber=sd_new_jkyx.id where nowstate=1 and sfyfk=1 and sfyhw=1 and sd_new_jkyx.yuq_ts=0 and ((hkfq_code=0 and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))<='"+datenow+"') or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))<='"+datenow+"')";
	   
	   return this.getJdbcTemplate().queryLong(sql);
   }
   public double getZongdqhkjehw(String datenow){
	   String sql="select SUM(REPLACE(rechargeMoney, ',' ,'')) from sd_recharge_detail left join sd_new_jkyx on sd_recharge_detail.rechargeNumber=sd_new_jkyx.id where nowstate=1 and sfyfk=1 and sfyhw=1 and ((hkfq_code=0 and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))<='"+datenow+"') or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))<='"+datenow+"')";
	   
	   return this.getJdbcTemplate().queryLong(sql);
   }
   public double getZongdqhkjeold(String datenow){
	   String sql="select SUM(REPLACE(rechargeMoney, ',' ,'')) from sd_recharge_detail left join sd_new_jkyx on sd_recharge_detail.rechargeNumber=sd_new_jkyx.id where nowstate=1 and is_old_user=1 and sfyfk=1 and ((hkfq_code=0 and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))<='"+datenow+"') or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))<='"+datenow+"')";
	   
	   return this.getJdbcTemplate().queryLong(sql);
   }
   public double getZongdqhkjenew(String datenow){
	   String sql="select SUM(REPLACE(rechargeMoney, ',' ,'')) from sd_recharge_detail left join sd_new_jkyx on sd_recharge_detail.rechargeNumber=sd_new_jkyx.id where nowstate=1 and is_old_user=0 and sfyfk=1 and ((hkfq_code=0 and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))<='"+datenow+"') or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))<='"+datenow+"')";
	   
	   return this.getJdbcTemplate().queryLong(sql);
   }
   public int getZongydq(String datenow){
	   String sql="select count(id) from sd_new_jkyx where sfyfk=1 and ((hkfq_code=0 and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))<='"+datenow+"') or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))<='"+datenow+"')";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongydqtqhw(String datenow){
	   String sql="select count(id) from sd_new_jkyx where sfyfk=1 and sfyhw=1 and yuq_ts=0 and ((hkfq_code=0 and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))<='"+datenow+"') or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))<='"+datenow+"')";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongydqyh(String datenow){
	   String sql="select count(id) from sd_new_jkyx where sfyfk=1 and sfyhw=1 and ((hkfq_code=0 and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))<='"+datenow+"') or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))<='"+datenow+"')";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongydqnew(String datenow){
	   String sql="select count(id) from sd_new_jkyx where is_old_user=0 and sfyfk=1 and ((hkfq_code=0 and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))<='"+datenow+"') or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))<='"+datenow+"')";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongydqold(String datenow){
	   String sql="select count(id) from sd_new_jkyx where is_old_user=1 and sfyfk=1 and ((hkfq_code=0 and CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))<='"+datenow+"') or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))<='"+datenow+"')";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongydqzt(String datenow){
	   String sql="select count(id) from sd_new_jkyx where sfyfk=1 and hkfq_code=0 and (CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))<'"+datenow+"' or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))<'"+datenow+"')";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongydqDT(String datenow){
	   String sql="select count(id) from sd_new_jkyx where sfyfk=1 and hkfq_code=0 and (CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))='"+datenow+"' or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))='"+datenow+"')";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public double getZongys(){
	   String sql="select SUM(REPLACE(sjsh_money, ',' ,'')+REPLACE(yuq_lx, ',' ,'')) from sd_new_jkyx where sfyfk=1 ";
	   
	   return this.getJdbcTemplate().queryLong(sql);
   }
   public int getZongyhk(){
	   String sql="select count(id) from sd_new_jkyx where sfyfk=1 and sfyhw=1 ";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public double getZongyhkje(){
	   String sql="select SUM(REPLACE(rechargeMoney, ',' ,'')) from sd_recharge_detail where nowstate=1 ";
	   
	   return this.getJdbcTemplate().queryLong(sql);
   }
   public int getZongfj(){
	   String sql="select count(id) from sd_new_jkyx where sfyfk=1 and is_old_user=1 ";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongyhkzs(){
	   String sql="SELECT count(id) FROM sd_new_jkyx WHERE  sfyfk=1 AND sfyhw=1 AND yuq_ts=0 ";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongyqzs(){
	   String sql="SELECT count(id) FROM sd_new_jkyx WHERE  sfyfk=1 AND yuq_ts>0 ";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongyqzswh(){
	   String sql="SELECT count(id) FROM sd_new_jkyx WHERE  sfyfk=1 and sfyhw=0 AND yuq_ts>0 ";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongyq13(){
	   String sql="SELECT count(id) FROM sd_new_jkyx WHERE  sfyfk=1 AND yuq_ts>=1 AND yuq_ts<=3 ";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongyq415(){
	   String sql="SELECT count(id) FROM sd_new_jkyx WHERE  sfyfk=1 AND yuq_ts>=4 AND yuq_ts<=15 ";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongyq1645(){
	   String sql="SELECT count(id) FROM sd_new_jkyx WHERE  sfyfk=1 AND yuq_ts>=16 AND yuq_ts<=45 ";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongyq46(){
	   String sql="SELECT count(id) FROM sd_new_jkyx WHERE  sfyfk=1 AND yuq_ts>=46 ";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongyqzsyhk(){
	   String sql="SELECT count(id) FROM sd_new_jkyx WHERE  sfyfk=1 AND yuq_ts>0 and sfyhw=1";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongyq13yhk(){
	   String sql="SELECT count(id) FROM sd_new_jkyx WHERE  sfyfk=1 AND yuq_ts>=1 AND yuq_ts<=3 and sfyhw=1";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongyq415yhk(){
	   String sql="SELECT count(id) FROM sd_new_jkyx WHERE  sfyfk=1 AND yuq_ts>=4 AND yuq_ts<=15 and sfyhw=1";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongyq1645yhk(){
	   String sql="SELECT count(id) FROM sd_new_jkyx WHERE  sfyfk=1 AND yuq_ts>=16 AND yuq_ts<=45 and sfyhw=1";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongyq46yhk(){
	   String sql="SELECT count(id) FROM sd_new_jkyx WHERE  sfyfk=1 AND yuq_ts>=46 and sfyhw=1";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongzhuce(String nowdate){
		String sql="select count(id) from sd_user where substring(yearmonthday,1,10)='"+nowdate+"'";
		return this.getJdbcTemplate().queryInt(sql);
	}
   public int getZongrenzheng(String nowdate){
	   String sql="select count(id) from sd_user where vipstatus=1 and substring(yearmonthday,1,10)='"+nowdate+"'";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongshenqing(String nowdate){
	   String sql="select count(DISTINCT userid) from sd_new_jkyx where substring(daytime,1,10)='"+nowdate+"'";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongfk(String nowdate){
	   String sql="select count(id) from sd_new_jkyx where sfyfk=1 and substring(fkdz_time_day,1,10)='"+nowdate+"'";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongfkxk(String nowdate){
	   String sql="select count(id) from sd_new_jkyx where sfyfk=1 and is_old_user=0 and substring(fkdz_time_day,1,10)='"+nowdate+"'";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public double getZongfkbj(String nowdate){
	   String sql="select SUM(REPLACE(sjds_money, ',' ,'')) from sd_new_jkyx where sfyfk=1 and substring(fkdz_time_day,1,10)='"+nowdate+"'";
	   
	   return this.getJdbcTemplate().queryLong(sql);
   }
   public double getDayZongyhkje(String nowdate){
	   String sql="select SUM(REPLACE(rechargeMoney, ',' ,'')) from sd_recharge_detail where nowstate=1 and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2))='"+nowdate+"'";
	   
	   return this.getJdbcTemplate().queryLong(sql);
   }
   public int getZongyhk(String nowdate){
	   String sql="select count(id) from sd_new_jkyx where sfyfk=1 and sfyhw=1 and (CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))='"+nowdate+"' or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))='"+nowdate+"')";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongyhkzs(String nowdate){
	   String sql="SELECT count(id) FROM sd_new_jkyx WHERE  sfyfk=1 AND sfyhw=1 AND yuq_ts=0 and (CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2))='"+nowdate+"' or CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2))='"+nowdate+"')";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongfj(String nowdate){
	   String sql="select count(id) from sd_new_jkyx where sfyfk=1 and is_old_user=1 and substring(fkdz_time_day,1,10)='"+nowdate+"'";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getZongyhkje(String nowdate){
	   String sql="SELECT count(id) FROM sd_recharge_detail WHERE nowstate=1 AND dqyqts>0 and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2))='"+nowdate+"'";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getCuishourenshu(String nowdate){
	   String sql="SELECT cuishourenshu FROM sd_accountuser WHERE CONCAT(SUBSTRING(time,7,4),'-',SUBSTRING(time,4,2),'-',SUBSTRING(time,1,2))='"+nowdate+"'";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   
   public int getNewrate25FZ(){
	   String sql="SELECT COUNT(*) FROM (SELECT COUNT(*) FROM sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.userid=sd_user.id WHERE sfyfk=1 and is_old_user=1 AND (is_good=1 OR sd_user.id<47000) GROUP BY userid )j ";
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getNewrate25FM(String nowdate){
	   String sql="SELECT COUNT(*) FROM sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.userid=sd_user.id WHERE sfyfk=1 and  is_old_user=0 AND (is_good=1 OR sd_user.id<47000) " + 
	   		"AND (IF(hkfq_code=1,CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)),CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)))<'"+nowdate+"' "
	   				+ " or (sfyhw=1 AND IF(hkfq_code=1,CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)),CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)))>='"+nowdate+"'))";
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getOldrate25FZ(){
	   String sql="SELECT COUNT(*) FROM sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.userid=sd_user.id WHERE sfyfk=1 and  is_old_user=1 AND (is_good=1 OR sd_user.id<47000) ";
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getOldrate25FM(String nowdate){
	   String sql="SELECT COUNT(*) FROM sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.userid=sd_user.id WHERE sfyfk=1 and  is_old_user=1 AND (is_good=1 OR sd_user.id<47000) " + 
	   		"AND (IF(hkfq_code=1,CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)),CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)))<'"+nowdate+"' " 
	   		+ " or (sfyhw=1 AND IF(hkfq_code=1,CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)),CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)))>='"+nowdate+"'))";
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getRate25FZ(){
	   String sql="SELECT COUNT(*) FROM sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.userid=sd_user.id WHERE sfyfk=1 and  is_old_user=1 AND (is_good=1 OR sd_user.id<47000) ";
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getRate25FM(String nowdate){
	   String sql="SELECT COUNT(*) FROM sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.userid=sd_user.id WHERE sfyfk=1 and  (is_good=1 OR sd_user.id<47000) " + 
			   "AND (IF(hkfq_code=1,CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)),CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)))<'"+nowdate+"' " 
			   + " or (sfyhw=1 AND IF(hkfq_code=1,CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)),CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)))>='"+nowdate+"'))";
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getNewrate30FZ(){
	   String sql="SELECT COUNT(*) FROM (SELECT COUNT(*) FROM sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.userid=sd_user.id WHERE sfyfk=1 and  is_old_user=1 AND (is_good=0 and sd_user.id>47000) GROUP BY userid )j ";
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getNewrate30FM(String nowdate){
	   String sql="SELECT COUNT(*) FROM sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.userid=sd_user.id WHERE sfyfk=1 and  is_old_user=0 AND (is_good=0 and sd_user.id>47000) " + 
			   "AND (IF(hkfq_code=1,CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)),CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)))<'"+nowdate+"' "
			   + " or (sfyhw=1 AND IF(hkfq_code=1,CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)),CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)))>='"+nowdate+"'))";
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getOldrate30FZ(){
	   String sql="SELECT COUNT(*) FROM sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.userid=sd_user.id WHERE sfyfk=1 and  is_old_user=1 AND (is_good=0 and sd_user.id>47000) ";
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getOldrate30FM(String nowdate){
	   String sql="SELECT COUNT(*) FROM sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.userid=sd_user.id WHERE sfyfk=1 and  is_old_user=1 AND (is_good=0 and sd_user.id>47000) " + 
			   "AND (IF(hkfq_code=1,CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)),CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)))<'"+nowdate+"' " 
			   + " or (sfyhw=1 AND IF(hkfq_code=1,CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)),CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)))>='"+nowdate+"'))";
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getRate30FZ(){
	   String sql="SELECT COUNT(*) FROM sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.userid=sd_user.id WHERE  sfyfk=1 and is_old_user=1 AND (is_good=0 and sd_user.id>47000) ";
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getRate30FM(String nowdate){
	   String sql="SELECT COUNT(*) FROM sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.userid=sd_user.id WHERE sfyfk=1 and  (is_good=0 and sd_user.id>47000) " + 
			   "AND (IF(hkfq_code=1,CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)),CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)))<'"+nowdate+"' " 
			   + " or (sfyhw=1 AND IF(hkfq_code=1,CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)),CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)))>='"+nowdate+"'))";
	   return this.getJdbcTemplate().queryInt(sql);
   }
   
   public int getNewrateFZ(){
	   String sql="SELECT COUNT(*) FROM (SELECT COUNT(*) FROM sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.userid=sd_user.id WHERE sfyfk=1 and  is_old_user=1 GROUP BY userid )j ";
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getNewrateFM(String nowdate){
	   String sql="SELECT COUNT(*) FROM sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.userid=sd_user.id WHERE sfyfk=1 and  is_old_user=0 " + 
			   "AND (IF(hkfq_code=1,CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)),CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)))<'"+nowdate+"' "
			   + " or (sfyhw=1 AND IF(hkfq_code=1,CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)),CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)))>='"+nowdate+"'))";
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getOldrateFZ(){
	   String sql="SELECT COUNT(*) FROM sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.userid=sd_user.id WHERE sfyfk=1 and  is_old_user=1 ";
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getOldrateFM(String nowdate){
	   String sql="SELECT COUNT(*) FROM sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.userid=sd_user.id WHERE sfyfk=1 and  is_old_user=1 " + 
			   "AND (IF(hkfq_code=1,CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)),CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)))<'"+nowdate+"' " 
			   + " or (sfyhw=1 AND IF(hkfq_code=1,CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)),CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)))>='"+nowdate+"'))";
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getRateFZ(){
	   String sql="SELECT COUNT(*) FROM sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.userid=sd_user.id WHERE sfyfk=1 and  is_old_user=1 ";
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getRateFM(String nowdate){
	   String sql="SELECT COUNT(*) FROM sd_new_jkyx LEFT JOIN sd_user ON sd_new_jkyx.userid=sd_user.id WHERE sfyfk=1 and  " + 
			   " (IF(hkfq_code=1,CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)),CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)))<'"+nowdate+"' " 
			   + " or (sfyhw=1 AND IF(hkfq_code=1,CONCAT(SUBSTRING(hkfq_time,7,4),'-',SUBSTRING(hkfq_time,4,2),'-',SUBSTRING(hkfq_time,1,2)),CONCAT(SUBSTRING(hkyq_time,7,4),'-',SUBSTRING(hkyq_time,4,2),'-',SUBSTRING(hkyq_time,1,2)))>='"+nowdate+"'))";
	   return this.getJdbcTemplate().queryInt(sql);
   }
   
   public int getShenheZongshu(String nowdate){
	   String sql="select count(id) from sd_new_jkyx where cl03_ren<>888 and (CONCAT(SUBSTRING(cl_time,7,4),'-',SUBSTRING(cl_time,4,2),'-',SUBSTRING(cl_time,1,2))='"+nowdate+"' or CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2))='"+nowdate+"' or CONCAT(SUBSTRING(cl03_time,7,4),'-',SUBSTRING(cl03_time,4,2),'-',SUBSTRING(cl03_time,1,2))='"+nowdate+"')";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   public int getShenheTongguo(String nowdate){
	   String sql="select count(id) from sd_new_jkyx where cl03_ren<>888 and (sfyfk=1 or sfyfk=2) and (CONCAT(SUBSTRING(cl_time,7,4),'-',SUBSTRING(cl_time,4,2),'-',SUBSTRING(cl_time,1,2))='"+nowdate+"' or CONCAT(SUBSTRING(cl02_time,7,4),'-',SUBSTRING(cl02_time,4,2),'-',SUBSTRING(cl02_time,1,2))='"+nowdate+"' or CONCAT(SUBSTRING(cl03_time,7,4),'-',SUBSTRING(cl03_time,4,2),'-',SUBSTRING(cl03_time,1,2))='"+nowdate+"')";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   /**
    * 根据身份证查userid
    * 
    */
   
   public int getuserfinance(String idno) {
	   String sql = " select userid from sd_user_finance where  idno='"+idno+"'";
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   
   /**
    * 根据userid查最后一笔借款
    * 
    */
   
   public int getuserlastJK(String userid) {
	   String sql = " select rechargenumber from sd_recharge_detail where  userid='"+userid+"' ORDER BY id DESC  LIMIT 1" ;
	   
	   return this.getJdbcTemplate().queryInt(sql);
   }
   
   /**
    * 催收M123
    * @return
    */
   public List<DataRow> getAllcuishouM123(){
		String sql = "SELECT user_id FROM sdcms_user WHERE roleid=62 and state=1";
		return getJdbcTemplate().query(sql);
	}
   
   /**
    * 催收M123
    * @return
    */
   public DBPage getYqM123List(int curPage, int numPerPage, String userId,
			String realName, String phone,String startDate, String endDate,
			String commit, String idCard, String hkstat, String et_day,
			String st_day,int cmsuserid,String cuishouid,int[] cuishouzuyqm1,String jkdate,String yuqts) throws Exception {
		
		String sql = " select j.userid, j.id as jkid2 ,u.username, u.mobilePhone,u.heihu_zt,s.realname,w.name as csname , j.yuq_lx ,j.hkfq_cishu,j.yuq_ts ,j.sjsh_money ,j.hkyq_time,j.hkfq_code,j.hk_time,j.hkfq_time,j.jk_date,j.fkr_time,j.sfyhw,j.hkqd,j.hkstate,j.zxcsbz as msg from sd_new_jkyx j left join sd_user u on "
				+ "  j.userid = u.id left join sd_user_finance s on s.userId =j.userid left join sdcms_user w on w.USER_ID = j.cuishou_id where j.sfyhw = 0 and j.hkfq_code=0";
		
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and  s.realname like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {
			
			sql += " and  s.idno='" + idCard + "'";
		}
		if (!StringHelper.isEmpty(cuishouid)) {
			
			sql += " and  j.cuishou_id=" + cuishouid;
		}
		if (!StringHelper.isEmpty(yuqts)) {
			
			sql += " and  j.yuq_ts=" + yuqts;
		}
		if (!StringHelper.isEmpty(jkdate)) {
			
			sql += " and  (j.jk_date=3 or j.jk_date=4) ";
		}
		if (!StringHelper.isEmpty(et_day) && !et_day.equals("0")) {
			
			sql += " and  j.yuq_ts >= " + et_day;
		}
		if (!StringHelper.isEmpty(st_day) && !st_day.equals("0")) {
			
			sql += " and  j.yuq_ts <= " + st_day;
		}
		/*if (hkstat.equals("1")) {

			sql += " and  j.sfyhw = 1";
		}

		if (hkstat.equals("2")) {
		 */
		
		//}
		sql += " and j.yuq_ts >0  " ;
//		if(cmsuserid == 8 || cmsuserid == 888 || cmsuserid == 222 || cmsuserid == 6 || cmsuserid == 8 || cmsuserid == 2018 || cmsuserid == 5051 || cmsuserid == 5053 || cmsuserid == 9999 || cmsuserid == 8888){
		if(roleauthoritymangement.getRoleAM_CSlist(cmsuserid+"")){
			for (int i = 0; i < cuishouzuyqm1.length; i++) {
				if(i == 0){
					sql +=" and (j.cuishou_id=2018 or j.cuishou_id=" + cuishouzuyqm1[0];
				}else if(i>0 && i<(cuishouzuyqm1.length-1)){
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[i];
				}else{
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[cuishouzuyqm1.length-1] + " )";
				}
				if(1==cuishouzuyqm1.length) {
					sql +=" ) ";
				}
			}
		}else{
			int roleid = getAllcuishouM3jia(cmsuserid);
			if(roleid == 63) {
				List<DataRow> cuishoum123 = getAllcuishouM123();
				int cuishouzuyqm123[] = new int[cuishoum123.size()];
				
				for (int m = 0; m < cuishoum123.size(); m++) {
					DataRow row = cuishoum123.get(m);
					cuishouzuyqm123[m] = row.getInt("user_id");
				}
				for (int i = 0; i < cuishouzuyqm123.length; i++) {
					if(i == 0){
						sql +=" and (j.cuishou_id=" + cuishouzuyqm123[0];
					}else if(i>0 && i<(cuishouzuyqm123.length-1)){
						sql +=" or j.cuishou_id=" + cuishouzuyqm123[i];
					}else{
						sql +=" or j.cuishou_id=" + cuishouzuyqm123[cuishouzuyqm123.length-1] + " )";
					}
					if(1==cuishouzuyqm123.length) {
						sql +=" ) ";
					}
					
				}
			}
			sql +=" and j.cuishou_id="+cmsuserid;
		}
		sql += " order by LENGTH(j.sjsh_money)>8 DESC,j.sjsh_money>='4,000,000' DESC,j.yuq_ts, j.yuq_lx ";
		
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
   
   /**
    * 催收M123
    * @return
    */
   public List<DataRow> getYqM123List(String userId,
			String realName, String phone, String startDate, String endDate,
			String commit, String idCard, String hkstat, String et_day,
			String st_day,int cmsuserid,String cuishouid,int[] cuishouzuyqm1,String jkdate,String yuqts) throws Exception {
		
		String sql = " select j.yuq_lx ,j.sjsh_money from sd_new_jkyx j left join sd_user u on "
				+ "  j.userid = u.id left join sd_user_finance s on s.userId =j.userid where j.sfyhw = 0 and j.hkfq_code=0";
		
		if (!StringHelper.isEmpty(userId)) {
			
			sql += " and  u.id =" + userId;
		}
		if (!StringHelper.isEmpty(realName)) {
			
			sql += " and  s.realname like'%" + realName + "%'";
		}
		if (!StringHelper.isEmpty(phone)) {
			
			sql += " and  u.mobilePhone like '%" + phone + "%'";
		}
		if (!StringHelper.isEmpty(idCard)) {
			
			sql += " and  s.idno='" + idCard + "'";
		}
		if (!StringHelper.isEmpty(cuishouid)) {
			
			sql += " and  j.cuishou_id=" + cuishouid;
		}
		if (!StringHelper.isEmpty(yuqts)) {
			
			sql += " and  j.yuq_ts=" + yuqts;
		}
		if (!StringHelper.isEmpty(jkdate)) {
			
			sql += " and  (j.jk_date=3 or j.jk_date=4) ";
		}
		if (!StringHelper.isEmpty(et_day) && !et_day.equals("0")) {
			
			sql += " and  j.yuq_ts >= " + et_day;
		}
		if (!StringHelper.isEmpty(st_day) && !st_day.equals("0")) {
			
			sql += " and  j.yuq_ts <= " + st_day;
		}
		
		sql += " and  j.yuq_ts >0  " ;
//		if(cmsuserid == 8 || cmsuserid == 888 || cmsuserid == 222 || cmsuserid == 6 || cmsuserid == 8 || cmsuserid == 2018|| cmsuserid == 5051|| cmsuserid == 5053 || cmsuserid == 9999|| cmsuserid == 8888){
		if(roleauthoritymangement.getRoleAM_CSlist(cmsuserid+"")){
			for (int i = 0; i < cuishouzuyqm1.length; i++) {
				if(i == 0){
					sql +=" and (j.cuishou_id=2018 or j.cuishou_id=" + cuishouzuyqm1[0];
				}else if(i>0 && i<(cuishouzuyqm1.length-1)){
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[i];
				}else{
					sql +=" or j.cuishou_id=" + cuishouzuyqm1[cuishouzuyqm1.length-1] + " )";
				}
				if(1==cuishouzuyqm1.length) {
					sql +=" ) ";
				}
			}
		}else{
			int roleid = getAllcuishouM3jia(cmsuserid);
			if(roleid == 63) {
				List<DataRow> cuishoum123 = getAllcuishouM123();
				int cuishouzuyqm123[] = new int[cuishoum123.size()];
				
				for (int m = 0; m < cuishoum123.size(); m++) {
					DataRow row = cuishoum123.get(m);
					cuishouzuyqm123[m] = row.getInt("user_id");
				}
				for (int i = 0; i < cuishouzuyqm123.length; i++) {
					if(i == 0){
						sql +=" and (j.cuishou_id=" + cuishouzuyqm123[0];
					}else if(i>0 && i<(cuishouzuyqm123.length-1)){
						sql +=" or j.cuishou_id=" + cuishouzuyqm123[i];
					}else{
						sql +=" or j.cuishou_id=" + cuishouzuyqm123[cuishouzuyqm123.length-1] + " )";
					}
					if(1==cuishouzuyqm123.length) {
						sql +=" ) ";
					}
					
				}
			}
			sql +=" and j.cuishou_id="+cmsuserid;
		}
		sql += " order by LENGTH(j.sjsh_money)>8 DESC,j.sjsh_money>='4,000,000' DESC,j.yuq_ts, j.yuq_lx ";
		
		return getJdbcTemplate().query(sql);
	}
}
