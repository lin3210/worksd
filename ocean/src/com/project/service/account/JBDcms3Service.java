package com.project.service.account;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.thinkive.base.jdbc.DBPage;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.jdbc.session.Session;
import com.thinkive.base.service.BaseService;
import com.thinkive.base.util.StringHelper;

public class JBDcms3Service  extends BaseService{

	private static Logger logger = Logger.getLogger(JBDcms3Service.class);
	
	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}
	
	
	/*********************************************************start****xiong-手动分单 *********************************************************************************/
	 
	 
	 public DataRow getCuishouidRoleId(String jkid){
			
			StringBuffer sb = new StringBuffer();
			sb.append(" select cuishou_id ,onesh,userid,name,cl_status,roleid from sd_new_jkyx left join sdcms_user on cuishou_id=sdcms_user.user_id where id ="+jkid);
			logger.info(sb.toString());	
			return getJdbcTemplate().queryMap(sb.toString());
	}
	 /**
		 * xiong-20190702-催收分单表，手动页面修改分单。
		 * 
		 */
		public int updateSdCuishouFendan(String cuishouid,String recid,String cuishou_z){
			 String sql = " UPDATE sd_cuishou_fendan SET cuishou_id="+cuishouid +"  where jk_id = "+recid +"  and cuishou_z="+cuishou_z;
			 //System.out.println(sql);
			 logger.info("手动修改催收人员订单sd_cuishou_fendan"+sql);
			 return getJdbcTemplate().update(sql);
		}
		
				
		public List<DataRow> selectUserLeaverList(int cuishouid,int fenzhu){		
			StringBuffer sb = new StringBuffer();					
			sb.append("select user_id ,name,roleid from sdcms_user  where fdqx=1   AND  leaver=0 ");
			
			if(4==fenzhu) {
				sb.append(" AND (roleid=19 OR roleid=20) ");
			}else if(1==fenzhu) {
				sb.append(" AND (roleid=21 OR roleid=50) ");
			}else if (2==fenzhu){
				sb.append(" AND (roleid=24 OR roleid=51) ");
			}else {
				sb.append(" AND (roleid=26 OR roleid=54 or roleid = 60 or roleid = 61) ");
			}
			sb.append( "  AND user_id<> "+cuishouid);
			sb.append( "  order by  USER_ID desc");
		
			String sql	=sb.toString();
			logger.info(sql);
			return getJdbcTemplate().query(sql);
		}
		
		
		/**
		 * xiong-查询所有催收人员
		 *
		 * 
		 * 
//		 */
//		public List<DataRow> selectUserLeavetList(int cuishouid,int fenzhu){		
//			StringBuffer sb = new StringBuffer();					
//			sb.append("select u.user_id ,name,u.roleid from sdcms_user u left join sdcms_user_role r on u.roleid=r.id  where 1 =1 ");
//			if(fenzhu==0 && cuishouid!=1 ) {
//				sb.append(" AND (roleid=21 or roleid=24  or roleid=26 ) AND  u.user_id= "+cuishouid);			
//			}else if(fenzhu==0 && cuishouid==1) {
//				sb.append(" AND (roleid=21 or roleid=24  or roleid=26 ) ");
//			}else if(fenzhu==1 && cuishouid==1) {
//				sb.append(" AND roleid=21 ");
//			}else if(fenzhu==2 && cuishouid==1) {
//				sb.append(" AND roleid=24 ");
//			}else if(fenzhu==3 && cuishouid==1) {
//				sb.append(" AND roleid=26 ");
//			}			
//			sb.append( " AND state=1  order by  u.USER_ID desc");
//			
//			String sql	=sb.toString();
//			logger.info(sql);
//			return getJdbcTemplate().query(sql);
//		}
		
		 public DataRow getCuishouidRoleid(String jkid){
				
				StringBuffer sb = new StringBuffer();
				sb.append(" select cuishou_id ,onesh,userid,name,cl_status,roleid from sd_new_jkyx left join sdcms_user on cuishou_id=sdcms_user.user_id where id ="+jkid);
				logger.info(sb.toString());	
				return getJdbcTemplate().queryMap(sb.toString());
		}
		 public List<DataRow> getShenheFdqxList(){
				
			 String sql=" select user_id  as userid,name,roleid from sdcms_user  where  STATE=1 AND  roleid=2  ";
			 
			 return getJdbcTemplate().query(sql);
		 }
     /*********************************************************end****xiong-手动分单 *********************************************************************************/
	
	public DBPage getFindOrder(int curPage ,int numPerPage, String phone ,String orderId,String realName,String statuc ,String userId)throws Exception
	{

		String sql = "  select w.id ,w.name, INSERT(w.cellPhone,4,4,'****')AS cellPhone  , w.applyTime , w.sum , w.status ,w.userId ,w.acount ,sd_user.usableSum ,INSERT(sd_bankcard.remark,4,4,'****') as remark3  from sd_withdraw w " +
				"left join sd_user on sd_user.id =w.userid left join sd_bankcard on sd_bankcard.userid =w.userid where w.remark ='提现'  and 1 =1  ";
		
		if(!StringHelper.isEmpty(phone)){
			
			sql +=" and w.cellPhone= '"+phone+"'" ;
		}
        if(!StringHelper.isEmpty(statuc)){
			
			sql +=" and w.status= "+statuc ;
		}
		if(!StringHelper.isEmpty(orderId)){
		
             sql +=" and w.orderid ='" +orderId+"'" ;
		}
		if(!StringHelper.isEmpty(realName)){
			
            sql +=" and w.name like'%" +realName+"%'" ;
		}
       if(!StringHelper.isEmpty(userId)){
			
            sql +=" and w.userId  =" +userId ;
		}
		sql +=" order by  applyTime desc" ;
	    return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
  
	
	 public DBPage getRepaymentList(int curPage ,int numPerPage,String userId,String realName,String phone,String startDate,String endDate,String commit,String idCard ,String hkstat)throws Exception
	  	{

	  		String sql = " select  u.id  as userid  ,u.username , r.rechargeTime , f.cardUserName  , INSERT(u.mobilePhone,4,4,'****')AS mobilePhone , INSERT(f.remark,4,4,'****') as idNo,INSERT(f.cardNo,4,4,'****')AS cardNo ,r.rechargeMoney ,r.result ,r.resultInfo from  sd_recharge_detail r   left join sd_user u on u.id = r.userid  left join  sd_bankcard f  on f.userid = r.userid  where r.rechargeType =33     ";	  		         
	  		if(!StringHelper.isEmpty(userId)){
				
				sql +=" and u.id= "+userId  ;
			}
	       
			if(!StringHelper.isEmpty(realName)){
				
	            sql +=" and f.cardUserName like'%" +realName+"%'" ;
			}
	       if(!StringHelper.isEmpty(phone)){
				
	            sql +=" and u.mobilePhone like'%" +phone +"'" ;
			}
	  		
			if (!StringHelper.isEmpty(startDate)) {


				sql += " and ((substring(r.rechargetime,1,10) >='" + startDate + "'";
				sql += " and substring(r.rechargetime,4,2) >='" + startDate.substring(3, 5) + "')";
			}
			if (!StringHelper.isEmpty(endDate)) {

				sql += " and (substring(r.rechargetime,1,10) <='" + endDate + "'";
				sql += " and substring(r.rechargetime,4,2) <='" + endDate.substring(3, 5) + "'))";
			}
	       
	       
	       if(!StringHelper.isEmpty(hkstat)&&!hkstat.equals("0")){		
	    	   
	           sql +=" and r.result =" +hkstat ;
			}
	  		
	  		sql +="  order  by r.rechargeTime  desc " ;
	  	  
	  	    return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	  	}
	 
	 public DBPage getRepayPlanList(int curPage ,int numPerPage,String userId,String jkid,String phone,String startDate,String endDate,String commit,String orderid )throws Exception
	 {
		 
		 String sql = " select  u.id  as userid  ,j.id as jkid ,u.username , r.rechargeTime ,paynumber , f.cardUserName  ,s.realname, INSERT(u.mobilePhone,4,4,'****')AS mobilePhone ,  INSERT(f.remark,4,4,'****') as idNo,INSERT(f.cardNo,4,4,'****')AS cardNo ,r.rechargeMoney ," +
				 " r.result ,r.remark,r.resultInfo ,j.yuq_ts ,j.jk_date ,j.yuq_lx from  sd_recharge_detail r   left join sd_user u on u.id = r.userid  left join  sd_bankcard f  on f.userid = r.userid left join  sd_user_finance s  on s.userid = r.userid left join sd_new_jkyx j on" +
				 "  r.userid = j.userid  where  r.result = 1  and  r.rechargeType =33 and j.id is not null and j.id=r.rechargeNumber  ";	  		         
		 if(!StringHelper.isEmpty(userId)){	
			 
			 sql +=" and u.id= "+userId  ;
		 }
		 
		 if(!StringHelper.isEmpty(jkid)){	
			 
			 sql +=" and j.id =" +jkid ;
		 }
		 if(!StringHelper.isEmpty(phone)){
			 
			 sql +=" and u.mobilePhone like'%" +phone +"'" ;
		 }
		 if(!StringHelper.isEmpty(orderid)){
			 
			 sql +=" and r.paynumber ='" +orderid +"'" ;
		 }
			if (!StringHelper.isEmpty(startDate)) {


				sql += " and ((substring(r.rechargetime,1,10) >='" + startDate + "'";
				sql += " and substring(r.rechargetime,4,2) >='" + startDate.substring(3, 5) + "')";
			}
			if (!StringHelper.isEmpty(endDate)) {

				sql += " and (substring(r.rechargetime,1,10) <='" + endDate + "'";
				sql += " and substring(r.rechargetime,4,2) <='" + endDate.substring(3, 5) + "'))";
			}
	       
		 
		 
		 sql +="  order by SUBSTRING(r.rechargeTime,7,4) desc ,SUBSTRING(r.rechargeTime,4,2) desc , SUBSTRING(r.rechargeTime,1,2) desc ,SUBSTRING(r.rechargeTime,12,2) desc,SUBSTRING(r.rechargeTime,15,2) desc,SUBSTRING(r.rechargeTime,18,2) desc" ;
		 
		 return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	 }
	 public DBPage getRepayPlanListHK(int curPage ,int numPerPage,String userId,String jkid,String phone,int hkcode,int yqcode,String startDate,String endDate,String commit,String orderid ,String cmsuserid,String hkbank,String idno,String time,String funpay,String viettel)throws Exception
	  	{

	  		String sql = " select  r.id as hkid,r.userid  as userid  ,j.id as jkid ,j.fkdz_time_day,j.fkdz_time,j.sjsh_money,u.username, r.rechargeTime ,r.userhktime ,r.bankjztime ,s.realname, s.idno,INSERT(f.cardNo,4,4,'****')AS cardNo ,r.rechargeMoney ," +
	  				" r.result ,r.remark,r.yqdate,r.resultInfo ,r.hongbaoid as fee,r.hkbank,r.dqyqts,r.dqyqlx,r.bankorderid,r.qrhkcode,r.operatorid,j.yuq_ts ,j.sjds_money,j.lx,j.hongbaoid,j.jk_date ,j.yuq_lx from  sd_recharge_detail r left join sd_bankcard f  on f.userid = r.userid left join  sd_user_finance s  on s.userid = r.userid left join sd_new_jkyx j on" +
	  				"  j.id=r.rechargeNumber LEFT JOIN sd_user u  ON r.userid =u.id   where r.nowstate=1 ";	  		         
	  		if(!StringHelper.isEmpty(userId)){	
	  			
				sql +=" and r.userid= "+userId  ;
			}
	       
			if(!StringHelper.isEmpty(jkid)){	
				
	            sql +=" and j.id =" +jkid ;
			}
			if(!StringHelper.isEmpty(cmsuserid)){	
				
				sql +=" and r.operatorid =" +cmsuserid ;
			}
			if(!StringHelper.isEmpty(hkbank)){	
				
				sql +=" and r.hkbank =" +hkbank ;
			}
			if(!StringHelper.isEmpty(idno)){	
				
				sql +=" and s.idno ='" +idno+"'" ;
			}
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
	       if(!StringHelper.isEmpty(viettel)){
	    	   
	    	   sql +=" and r.orderpaytype =" +viettel;
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
	       
	  		
	  		sql +="  order by SUBSTRING(r.rechargeTime,7,4) desc ,SUBSTRING(r.rechargeTime,4,2) desc , SUBSTRING(r.rechargeTime,1,2) desc ,SUBSTRING(r.rechargeTime,12,2) desc,SUBSTRING(r.rechargeTime,15,2) desc,SUBSTRING(r.rechargeTime,18,2) desc" ;
	  	  
	  	    return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	  	}
	 public List<DataRow> getRepayPlanListRow(String userId,String jkid,String phone,int hkcode,int yqcode,String startDate,String endDate,String commit,String orderid ,String cmsuserid,String hkbank,String idno,String time,String funpay,String viettel)throws Exception
	  	{

		    String sql = " select  r.id as hkid,r.userid  as userid  ,j.id as jkid ,j.fkdz_time_day, r.rechargeTime ,r.userhktime ,r.bankjztime ,s.realname, s.idno,INSERT(f.cardNo,4,4,'****')AS cardNo ,r.rechargeMoney ," +
	  				" r.result ,r.remark,r.yqdate,r.resultInfo ,r.hongbaoid as fee,r.hkbank,r.dqyqts,r.dqyqlx,r.bankorderid,r.qrhkcode,r.operatorid,j.yuq_ts ,j.sjds_money,j.lx,j.hongbaoid,j.jk_date ,j.yuq_lx from  sd_recharge_detail r left join sd_bankcard f  on f.userid = r.userid left join  sd_user_finance s  on s.userid = r.userid left join sd_new_jkyx j on" +
	  				"  j.id=r.rechargeNumber  where r.nowstate=1 ";  		         
	  		if(!StringHelper.isEmpty(userId)){	
	  			
				sql +=" and r.userid= "+userId  ;
			}
	       
			if(!StringHelper.isEmpty(jkid)){	
				
	            sql +=" and j.id =" +jkid ;
			}
			if(!StringHelper.isEmpty(cmsuserid)){	
				
				sql +=" and r.operatorid =" +cmsuserid ;
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
	       
	       if(!StringHelper.isEmpty(viettel)){
	    	   
	    	   sql +=" and r.orderpaytype =" +viettel;
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
	       
	  	  
	  	    return getJdbcTemplate().query(sql);
	  	}
	 public String getHkcount(int curPage ,int numPerPage,String userId,String jkid,String phone,String startDate,String endDate,String commit,String orderid )throws Exception
	  	{

	  		String sql = " select  sum(r.rechargeMoney) " +
	  				" from  sd_recharge_detail r   left join sd_user u on u.id = r.userid  left join  sd_bankcard f  on f.userid = r.userid left join sd_new_jkyx j on" +
	  				"  r.rechargeTime = j.hk_time  where r.nowstate=1 and r.result = 1  and  r.rechargeType =33 and j.id is not null  ";	  		         
	  		if(!StringHelper.isEmpty(userId)){	
	  			
				sql +=" and u.id= "+userId  ;
			}
	       
			if(!StringHelper.isEmpty(jkid)){	
				
	            sql +=" and j.id =" +jkid ;
			}
	       if(!StringHelper.isEmpty(phone)){
				
	            sql +=" and u.mobilePhone like'%" +phone +"'" ;
			}
	       if(!StringHelper.isEmpty(orderid)){
				
	            sql +=" and r.paynumber ='" +orderid +"'" ;
			}
	       if(!StringHelper.isEmpty(startDate)){
				
	            sql +=" and r.rechargeTime >'" +startDate+"'" ;
			}
	       if(!StringHelper.isEmpty(endDate)){
				
	           sql +=" and r.rechargeTime <'" +endDate+"'" ;
			}
	       
	  		
	  	
	  	  
	  	    return getJdbcTemplate().queryString(sql);
	  	}
	 
	 public DBPage  getFindOrderList(int curPage ,int numPerPage, String phone ,String orderId,String realName,String startDate,String endDate ,String userId )throws Exception
		  {

			String sql = "  select w.id ,w.name,w.versoin, INSERT(w.cellPhone,4,4,'****')AS cellPhone  , w.checkTime , w.sum ,w.orderid, w.status ,w.userId ,INSERT(w.acount,4,4,'****')AS acount ,sd_user.usableSum ,sd_bankcard.remark as remark3  from sd_withdraw w " +
					"left join sd_user on sd_user.id =w.userid left join sd_bankcard on sd_bankcard.userid =w.userid where w.remark ='放款'  and w.status =2   and 1 =1  ";
			
			if(!StringHelper.isEmpty(phone)){
				
				sql +=" and w.cellPhone= '"+phone+"'" ;
			}
	       
			if(!StringHelper.isEmpty(orderId)){
			
	            sql +=" and w.orderid ='" +orderId+"'" ;
			}
			if(!StringHelper.isEmpty(realName)){
				
	           sql +=" and w.name like'%" +realName+"%'" ;
			}
	      if(!StringHelper.isEmpty(userId)){
				
	           sql +=" and w.userId  =" +userId ;
			}
	        
	      if(!StringHelper.isEmpty(startDate)){
				
	            sql +=" and w.applyTime >'" +startDate+"'" ;
			}
	       if(!StringHelper.isEmpty(endDate)){
				
	           sql +=" and w.applyTime <'" +endDate+"'" ;
			}
	      
			sql +=" order by  applyTime desc" ;
		    return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
		}
	 
	 public String  getfkCount(int curPage ,int numPerPage, String phone ,String orderId,String realName,String startDate,String endDate ,String userId )throws Exception
	  {

		String sql = "  select sum(w.sum)  from sd_withdraw w " +
				"left join sd_user on sd_user.id =w.userid left join sd_bankcard on sd_bankcard.userid =w.userid where w.remark ='放款'  and 1 =1  ";
		
		if(!StringHelper.isEmpty(phone)){
			
			sql +=" and w.cellPhone= '"+phone+"'" ;
		}
      
		if(!StringHelper.isEmpty(orderId)){
		
           sql +=" and w.orderid ='" +orderId+"'" ;
		}
		if(!StringHelper.isEmpty(realName)){
			
          sql +=" and w.name like'%" +realName+"%'" ;
		}
     if(!StringHelper.isEmpty(userId)){
			
          sql +=" and w.userId  =" +userId ;
		}
       
     if(!StringHelper.isEmpty(startDate)){
			
           sql +=" and w.applyTime >'" +startDate+"'" ;
		}
      if(!StringHelper.isEmpty(endDate)){
			
          sql +=" and w.applyTime <'" +endDate+"'" ;
		}    
		sql +=" order by  applyTime desc" ;
	    return getJdbcTemplate().queryString(sql);
	}
	
	 
	 public DataRow  getRZorder(String order){
			
			String sql  = " select * from sd_recharge_detail where paynumber = '" +order+"'";
			
			return getJdbcTemplate().queryMap(sql);
		}
	 public String  getsjsh(String jkid){
			
			String sql  = " select sjsh_money from sd_new_jkyx where id = " +jkid;
			
			return getJdbcTemplate().queryString(sql);
		}
	 public Integer  gethongbaoid(String jkid){
		 
		 String sql  = " select hongbaoid from sd_new_jkyx where id = " +jkid;
		 
		 return getJdbcTemplate().queryInt(sql);
	 }
	 public Integer  getHKhongbaoid(String jkid){
		 
		 String sql  = " select hkhongbaoid from sd_new_jkyx where id = " +jkid;
		 
		 return getJdbcTemplate().queryInt(sql);
	 }
	 public Integer  getHKhongbaojine(int hkhongbaoid){
		 
		 String sql  = " select hongbao from sd_hongbao where id = " +hkhongbaoid;
		 
		 return getJdbcTemplate().queryInt(sql);
	 }
	 public Integer  getyqts(String jkid){
		 
		 String sql  = " select yuq_ts from sd_new_jkyx where id = " +jkid;
		 
		 return getJdbcTemplate().queryInt(sql);
	 }
	 public Integer  gethkdate(String jkid){
		 
		 String sql  = " select jk_date from sd_new_jkyx where id = " +jkid;
		 
		 return getJdbcTemplate().queryInt(sql);
	 }
	 public DataRow getUserJKInfo(String  jkid)
	  {
		  String sql = "select hkyq_time,hkfq_code,hkfq_day,hkfqzlx,hkfq_lx,hkfq_cishu,hkfq_time,cuishou_id,cuishou_m0,cuishou_m1,cuishou_m2,cuishou_m3 from sd_new_jkyx  where id = " +jkid ;
		  return getJdbcTemplate().queryMap(sql);
	  }
	 public DataRow getUserInfo(int  userid)
		{
			String sql = "select mobilePhone,username,yhbd,islianxi,isjop,isschool,isshenfen,isfacebook,registration_id from sd_user  where id = " +userid ;
			return getJdbcTemplate().queryMap(sql);
		}
	 public void updateOrderNo(DataRow row){
		 getJdbcTemplate().update("sd_app_funpay_orderid", row, "id", row.getString("id"));
	 }
	 public DataRow getFunpayOrderNo(String  orderno)
	 {
		 String sql = "select * from sd_app_funpay_orderid  where orderid ='"+orderno+"'" ;
		 return getJdbcTemplate().queryMap(sql);
	 }
	 public String  gethkqd(String jkid){
			
			String sql  = " select hkqd from sd_new_jkyx where id = " +jkid;
			
			return getJdbcTemplate().queryString(sql);
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
	 
	 public DataRow getRefferUser(String refferCount)
		{
			String sql = "select * from sd_user where id = "+refferCount +" or mobilePhone =" +refferCount;
			return getJdbcTemplate().queryMap(sql);
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
	 public String getRealname(String userid) {
		 StringBuffer sb = new StringBuffer();
		 sb.append("select realname from sd_user_finance  where userid = " + userid);
		 
		 return getJdbcTemplate().queryString(sb.toString());
	 }
	 public void addOrderError(DataRow data)
	 {
		 try {
			 getJdbcTemplate().insert("sd_app_funpay_orderid_error", data);
		 } catch (Exception e) {
			 logger.error(e);
			 e.printStackTrace();
		 }
	 }
	 public void addReporttoken(DataRow data)
	 {
		 try {
			 getJdbcTemplate().insert("sd_report_task_token", data);
		 } catch (Exception e) {
			 logger.error(e);
			 e.printStackTrace();
		 }
	 }
	 public void addReporttokenFBuser(DataRow data)
	 {
		 try {
			 getJdbcTemplate().insert("sd_report_task_token_fb_user", data);
		 } catch (Exception e) {
			 logger.error(e);
			 e.printStackTrace();
		 }
	 }
	 public void addReporttokenFBPosts(DataRow data)
	 {
		 try {
			 getJdbcTemplate().insert("sd_report_task_token_fb_posts", data);
		 } catch (Exception e) {
			 logger.error(e);
			 e.printStackTrace();
		 }
	 }
	 public DataRow getAuthRow(String  userid){
			String sql ="select * from sd_report_task_token where userid ="+userid;
			return getJdbcTemplate().queryMap(sql);
		}
	 public void updateAuthRow(DataRow data) {
			getJdbcTemplate().update("sd_report_task_token", data, "userid", data.getString("userid"));
		}
	 public void addReporttokenZalouser(DataRow data)
	 {
		 try {
			 getJdbcTemplate().insert("sd_report_task_token_zalo_user", data);
		 } catch (Exception e) {
			 logger.error(e);
			 e.printStackTrace();
		 }
	 }
	 public void addReporttokenZalogroups(DataRow data)
	 {
		 try {
			 getJdbcTemplate().insert("sd_report_task_token_zalo_groups", data);
		 } catch (Exception e) {
			 logger.error(e);
			 e.printStackTrace();
		 }
	 }
	 public void addReporttokenYysmth(DataRow data)
	 {
		 try {
			 getJdbcTemplate().insert("sd_report_task_token_yys_mth", data);
		 } catch (Exception e) {
			 logger.error(e);
			 e.printStackTrace();
		 }
	 }
	 public void addReporttokenYysuser(DataRow data)
	 {
		 try {
			 getJdbcTemplate().insert("sd_report_task_token_yys_user", data);
		 } catch (Exception e) {
			 logger.error(e);
			 e.printStackTrace();
		 }
	 }
	 public void addReporttokenYyshistory(DataRow data)
	 {
		 try {
			 getJdbcTemplate().insert("sd_report_task_token_yys_history", data);
		 } catch (Exception e) {
			 logger.error(e);
			 e.printStackTrace();
		 }
	 }
	 public void addReporttokenYysdetail(DataRow data)
	 {
		 try {
			 getJdbcTemplate().insert("sd_report_task_token_yys_detail", data);
		 } catch (Exception e) {
			 logger.error(e);
			 e.printStackTrace();
		 }
	 }
	 public void addReporttokenZalofriends(DataRow data)
	 {
		 try {
			 getJdbcTemplate().insert("sd_report_task_token_zalo_friends", data);
		 } catch (Exception e) {
			 logger.error(e);
			 e.printStackTrace();
		 }
	 }
	 public void addOrderCF(DataRow data)
	 {
		 try {
			 getJdbcTemplate().insert("sd_recharge_detail_chongfu", data);
		 } catch (Exception e) {
			 logger.error(e);
			 e.printStackTrace();
		 }
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
	 public void updateJKHK(DataRow row1)
		{
			getJdbcTemplate().update("sd_new_jkyx", row1, "id", row1.getString("id"));
		}
/*	 public void updateHKhongbaoid(DataRow row1)
	 {
		 getJdbcTemplate().update("sd_hongbao", row1, "hongbaoid", row1.getString("hongbaoid"));
	 }*/
	 public void updateHKhongbaoid(DataRow row1)
	 {
		 getJdbcTemplate().update("sd_user", row1, "id", row1.getString("id"));
	 }
	 public void updatehkqr(DataRow row)
	 {
		 getJdbcTemplate().update("sd_recharge_detail", row, "id", row.getString("id"));
	 }
	 public void updatehkqd(DataRow row)
		{
			getJdbcTemplate().update("sd_new_jkyx", row, "id", row.getString("id"));
		}
	 public Integer getSHQX(int cmsuserid) {
			StringBuffer sb = new StringBuffer();
			sb.append("select xrcode from sdcms_user  where user_id = " + cmsuserid);

			return getJdbcTemplate().queryInt(sb.toString());
		}

	 public void insertUserMsg(DataRow row)
	 	{
	 		getJdbcTemplate().insert(" sd_msg", row);
	 	}
	 
	 public void insertUserSH(DataRow row)
	 {
		 getJdbcTemplate().insert(" sd_userxx", row);
	 }
	 
	 public void insertDHDHJK(DataRow row)
	 {
		 getJdbcTemplate().insert(" sd_dianhua", row);
	 }
	 
	 public void insertPHONE(DataRow row)
	 {
		 getJdbcTemplate().insert(" sd_userselectcx", row);
	 }
	 
	 public void insertUserFSDXMsg(DataRow row)
	 {
		 getJdbcTemplate().insert(" sd_csmsg", row);
	 }
	 
	 public DataRow getUserInfo(String userid)
		{
			String sql = "select u.id ,b.cardUserName ,u.mobilePhone ,u.username from sd_user u left join sd_bankcard b on b.userid = u.id  where u.id = "+userid ;
			return getJdbcTemplate().queryMap(sql);
	}
	 
	 public String getRepaymentstatus(int  jkid){
			StringBuffer sb = new StringBuffer();
			sb.append("select jksfwc from sd_new_jkyx where  id = ");
			sb.append(jkid);
			
			return getJdbcTemplate().queryString(sb.toString());
		}
	 public String getuserid(String  jkid){
			String sql ="select userid from sd_new_jkyx where  id ='"+jkid+"'";
			return getJdbcTemplate().queryString(sql);
		}
	 public String getJKID(int userid){
		 String sql ="select id from sd_new_jkyx where  sfyfk=1 and sfyhw=0 and jksfwc=0 and userid ='"+userid+"'";
		 return getJdbcTemplate().queryString(sql);
	 }
	 public String getJKID(String userid){
		 String sql ="select id from sd_new_jkyx where  sfyfk=1 and sfyhw=0 and jksfwc=0 and userid ='"+userid+"'";
		 return getJdbcTemplate().queryString(sql);
	 }
	 public int getYQTS(String  jkid){
		 String sql ="select yuq_ts from sd_new_jkyx where  id ='"+jkid+"'";
		 return getJdbcTemplate().queryInt(sql);
	 }
	 public String getYQLX(String  jkid){
		 String sql ="select yuq_lx from sd_new_jkyx where  id ='"+jkid+"'";
		 return getJdbcTemplate().queryString(sql);
	 }
	 public String getYQYH(String  jkid){
		 String sql ="select yuq_yhlx from sd_new_jkyx where  id ='"+jkid+"'";
		 return getJdbcTemplate().queryString(sql);
	 }
	 public String getSJDS(String  jkid){
		 String sql ="select sjds_money from sd_new_jkyx where  id ='"+jkid+"'";
		 return getJdbcTemplate().queryString(sql);
	 }
	 public String getLX(String  jkid){
		 String sql ="select lx from sd_new_jkyx where  id ='"+jkid+"'";
		 return getJdbcTemplate().queryString(sql);
	 }
	 public String getHKYQtime(String  jkid){
		 String sql ="select hkyq_time from sd_new_jkyx where  id ='"+jkid+"'";
		 return getJdbcTemplate().queryString(sql);
	 }
	 public String getHKFQtime(String  jkid){
		 String sql ="select hkfq_time from sd_new_jkyx where  id ='"+jkid+"'";
		 return getJdbcTemplate().queryString(sql);
	 }
	 public DataRow getRow(String  jkid){
			String sql ="select * from sd_new_jkyx where  id ='"+jkid+"'";
			return getJdbcTemplate().queryMap(sql);
		}
	 
	 public Integer getBeingProcessed(int  userid  ,String  today){
		 
			String  sql = " select  count(*)  from sd_recharge_detail where resultInfo ='支付处理中' and rechargeType ='33' and rechargeTime > '"+today +"' and  result<>1 and userId ="+userid;		
			
			return getJdbcTemplate().queryInt(sql);
		}
	
	 
	 public DataRow  getRayMentInfo(String payOrder)
		{
			String sql = " select  * from sd_recharge_detail where  paynumber = "+ payOrder;
			return getJdbcTemplate().queryMap(sql);
	    }
	 public DataRow  getWJDHfsdx(String jkid,String datenow,String enddate)
	 {
		 String sql = " select  * from sd_csmsg where  jkid = "+ jkid+" and create_time>"+datenow+" and create_time<"+enddate;
		 return getJdbcTemplate().queryMap(sql);
	 }
	 public void updateRepayMent(DataRow row3)
		{
			//getJdbcTemplate().update("sd_user", row3, "id", row3.getString("id"));
			getJdbcTemplate().update(" sd_recharge_detail",row3,"id",row3.getString("id"));
		}
	 
	 public void updateBankInfo(DataRow row1){
			
			getJdbcTemplate().update("sd_bankcard ", row1, "userId", row1.getString("userid"));
		}
	 public DBPage getLoanSuper(int curPage ,int numPerPage,String id ,String title)throws Exception
	  	{
		 	
	  		String sql = " select  Id,title,introduce,imgurl,url from  sd_loansuper  where 1=1";
	  		if (!StringHelper.isEmpty(id)) {
				sql += "and Id='"+id+"'";
			}
	  		if (!StringHelper.isEmpty(title)) {
				sql += "and title like '%"+title+"%'";
			}
	  	    return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	  	}
	 public List<DataRow> getLoanSuper(String id ,String title)throws Exception
	  	{

	  		String sql = " select  Id,title,introduce,imgurl,url from  sd_loansuper  ";
	  		if (!StringHelper.isEmpty(id)) {
				sql += "and Id='"+id+"'";
			}
	  		if (!StringHelper.isEmpty(title)) {
				sql += "and title like '%"+title+"%'";
			}
	  	    return getJdbcTemplate().query(sql);
	  	}
	 public List<DataRow> getSJXH(String userid)
	 {
		 
		 String sql = " select * from  sd_phonetype where userid= '"+userid+"' group by macaddress";
		 return getJdbcTemplate().query(sql);
	 }
	 public DataRow getcheck(String LoanName){
			
			StringBuffer sb = new StringBuffer();
			sb.append("select * from sd_loansuper where id = '");
			sb.append(LoanName);
			String dr = getJdbcTemplate().queryString(sb.toString());
			return getJdbcTemplate().queryMap(sb.toString());
	 }
	 public void updateCheck(DataRow row){
		 getJdbcTemplate().update("sd_loansuper", row, "id", row.getString("id"));
	 }
	 public Integer  getTonghuaCountYN(int userid){
			StringBuffer sb = new StringBuffer();
			sb.append("select count(1) from sd_tonghuajilu where  userid = "+userid);
			
			return getJdbcTemplate().queryInt(sb.toString());
		}
	 public Integer  getTXLCountYN(int userid){
		 StringBuffer sb = new StringBuffer();
		 sb.append("select count(1) from sd_tongxunlu where  userid = "+userid);
		 
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 public Integer  getSMSCountYN(int userid){
		 StringBuffer sb = new StringBuffer();
		 sb.append("select count(1) from sd_sms where  userid = "+userid);
		 
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 public Integer  getXTTonghuaCountYN(int userid){
		 StringBuffer sb = new StringBuffer();
		 sb.append("select count(1) from sd_tonghuajilu where code=1 and userid = "+userid);
		 
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 public Integer  getXTTXLCountYN(int userid){
		 StringBuffer sb = new StringBuffer();
		 sb.append("select count(1) from sd_tongxunlu where code=1 and userid = "+userid);
		 
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 public Integer  getXTSMSCountYN(int userid){
		 StringBuffer sb = new StringBuffer();
		 sb.append("select count(1) from sd_sms where code=1 and userid = "+userid);
		 
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 public Integer  getCGJKCountYN(int userid){
		 StringBuffer sb = new StringBuffer();
		 sb.append("select count(1) from sd_new_jkyx where sfyhw=1 and jksfwc=1 and userid = "+userid);
		 
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 public Integer  getCGJKYQCountYN(int userid){
		 StringBuffer sb = new StringBuffer();
		 sb.append("select count(1) from sd_new_jkyx where sfyfk=1 and sfyhw=0 and yuq_ts>0 and userid = "+userid);
		 
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 public Integer  getSQJKCountYN(int userid){
		 StringBuffer sb = new StringBuffer();
		 sb.append("select count(1) from sd_new_jkyx where userid = "+userid);
		 
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 public Integer  getSQJKBJCountYN(int userid){
		 StringBuffer sb = new StringBuffer();
		 sb.append("select count(1) from sd_new_jkyx where (cl_status=3 or cl02_status=3 or cl03_status) and userid = "+userid);
		 
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 public Integer  getHHZTYN(int userid){
		 StringBuffer sb = new StringBuffer();
		 sb.append("select heihu_zt from sd_user where id = "+userid);
		 
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 public Integer  getCGJKCountYN1(int userid){
		 StringBuffer sb = new StringBuffer();
		 sb.append("select count(1) from sd_new_jkyx where sfyhw=1 and jksfwc=1 and userid = "+userid);
		 
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 public Integer  getCGJKYQCountYN1(int userid){
		 StringBuffer sb = new StringBuffer();
		 sb.append("select count(1) from sd_new_jkyx where sfyfk=1 and sfyhw=0 and yuq_ts>0 and userid = "+userid);
		 
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 public Integer  getSQJKCountYN1(int userid){
		 StringBuffer sb = new StringBuffer();
		 sb.append("select count(1) from sd_new_jkyx where userid = "+userid);
		 
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 public Integer  getSQJKBJCountYN1(int userid){
		 StringBuffer sb = new StringBuffer();
		 sb.append("select count(1) from sd_new_jkyx where (cl_status=3 or cl02_status=3 or cl03_status) and userid = "+userid);
		 
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 public Integer  getHHZTYN1(int userid){
		 StringBuffer sb = new StringBuffer();
		 sb.append("select heihu_zt from sd_user where id = "+userid);
		 
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 public Integer  getCGJKCountYN2(int userid){
		 StringBuffer sb = new StringBuffer();
		 sb.append("select count(1) from sd_new_jkyx where sfyhw=1 and jksfwc=1 and userid = "+userid);
		 
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 public Integer  getCGJKYQCountYN2(int userid){
		 StringBuffer sb = new StringBuffer();
		 sb.append("select count(1) from sd_new_jkyx where sfyfk=1 and sfyhw=0 and yuq_ts>0 and userid = "+userid);
		 
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 public Integer  getSQJKCountYN2(int userid){
		 StringBuffer sb = new StringBuffer();
		 sb.append("select count(1) from sd_new_jkyx where userid = "+userid);
		 
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 public Integer  getSQJKBJCountYN2(int userid){
		 StringBuffer sb = new StringBuffer();
		 sb.append("select count(1) from sd_new_jkyx where (cl_status=3 or cl02_status=3 or cl03_status) and userid = "+userid);
		 
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 public Integer  getHHZTYN2(int userid){
		 StringBuffer sb = new StringBuffer();
		 sb.append("select heihu_zt from sd_user where id = "+userid);
		 
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 public int getHKOrder(int hkbank , String bankorderid){
		 String sql = "select id from sd_recharge_detail where hkbank="+hkbank+" and bankorderid='"+bankorderid+"' order by id limit 1";
		 return getJdbcTemplate().queryInt(sql);
	 }
	public int getIDID(int userid){
		String sql = "select id from sd_new_jkyx where (cl_yj LIKE '%Sai thong tin ngan hang%' OR cl_yj LIKE '%Hinh CMND khong hop le%' OR cl_yj LIKE '%KH cap nhat lai thoi han vay%' OR cl_yj LIKE '%Sai thong tin cong viec%' OR cl_yj LIKE '%Sai thong tin nguoi lien he%') and userid="+userid;
		return getJdbcTemplate().queryInt(sql);
	}
	public int getIDIDJK(int userid){
		String sql = "select id from sd_new_jkyx where sfyfk=1 and sfyhw=0 and userid="+userid;
		return getJdbcTemplate().queryInt(sql);
	}
	public int getIDIDXZ(int userid){
		String sql = "select id from sd_new_jkyx where substring(cl_yj,1,13)='Limit 15 days' and substring(daytime,1,10)<'2018-09-19' and userid="+userid;
		return getJdbcTemplate().queryInt(sql);
	}
	public int getMaxID(int userid){
		String sql = "select max(id) from sd_new_jkyx where userid="+userid;
		return getJdbcTemplate().queryInt(sql);
	}
	public int getMaxIDJK(int userid){
		String sql = "select max(id) from sd_new_jkyx where sfyfk=1 and sfyhw=1 and userid="+userid;
		return getJdbcTemplate().queryInt(sql);
	}
	public int getMaxIDXZ(int userid){
		String sql = "select max(id) from sd_new_jkyx where userid="+userid;
		return getJdbcTemplate().queryInt(sql);
	}
	public int getPayOrder(String payorder){
		String sql = "select count(id) from sd_recharge_detail where paynumber='"+payorder+"'";
		return getJdbcTemplate().queryInt(sql);
	}
	 public List<DataRow> getAllDH(){
		 String sql = "SELECT sd_new_jkyx.userid,mobilephone,realname FROM sd_new_jkyx left join sd_user on sd_user.id=sd_new_jkyx.userid left join sd_user_finance on sd_user_finance.userid=sd_new_jkyx.userid WHERE (cl_yj LIKE '%Sai thong tin ngan hang%' OR cl_yj LIKE '%Hinh CMND khong hop le%' OR cl_yj LIKE '%KH cap nhat lai thoi han vay%' OR cl_yj LIKE '%Sai thong tin cong viec%' OR cl_yj LIKE '%Sai thong tin nguoi lien he%') GROUP BY sd_new_jkyx.userid";
		 return getJdbcTemplate().query(sql);
	 }
	 public List<DataRow> getAllDHJK(){
		 String sql = "SELECT sd_new_jkyx.userid,mobilephone,realname FROM sd_new_jkyx left join sd_user on sd_user.id=sd_new_jkyx.userid left join sd_user_finance on sd_user_finance.userid=sd_new_jkyx.userid WHERE sfyfk=1 GROUP BY sd_new_jkyx.userid";
		 return getJdbcTemplate().query(sql);
	 }
	 public List<DataRow> getAllDXJX(){
		 String sql = "select dianhua as mobilephone,dhname as realname from sd_dianhua ";
		 return getJdbcTemplate().query(sql);
	 }
	 public List<DataRow> getAllDX(){
		 String sql = "SELECT mobilephone,cardusername FROM sd_bankcard LEFT JOIN sd_user ON sd_bankcard.userid=sd_user.id WHERE heihu_zt=0 AND sd_bankcard.id>1000 and sd_bankcard.id<14000 AND vipstatus=0 AND (yhbd=1 OR islianxi=1 OR isshenfen=1 OR isjop=1 OR isschool=1)";
		 return getJdbcTemplate().query(sql);
	 }
	 public List<DataRow> getAllDXJK(){
		 String sql = "SELECT mobilephone,realname FROM sd_user LEFT JOIN sd_user_finance ON sd_user_finance.userid=sd_user.id LEFT JOIN sd_new_jkyx ON sd_user.id=sd_new_jkyx.userid WHERE sfyfk=2 and sfyhw=0";
		 return getJdbcTemplate().query(sql);
	 }
	 public List<DataRow> getAllDXuser(){
		 String sql = "SELECT sd_user.mobilephone FROM sd_user LEFT JOIN sd_new_jkyx ON sd_user.`id`= sd_new_jkyx.userid WHERE heihu_zt=0 AND sfyfk=1 GROUP BY sd_user.`id`";
		 return getJdbcTemplate().query(sql);
	 }
	 public List<DataRow> getAllDXuserTG(){
		 String sql = "SELECT * FROM sd_user WHERE refferee='007' AND ( SUBSTRING(yearmonthday,1,7)='2018-07' OR SUBSTRING(yearmonthday,1,7)='2018-08')  AND vipStatus=0";
		 return getJdbcTemplate().query(sql);
	 }
	 public List<DataRow> getAllDXusermjk(){
		 String sql = "SELECT mobilephone FROM sd_user LEFT JOIN sd_new_jkyx ON sd_user.`id`=sd_new_jkyx.`userid` WHERE vipStatus=1 AND cl_status=0 AND fsdxcode=1 ";

		 return getJdbcTemplate().query(sql);
	 }
	 public List<DataRow> getAllthjlNum(int userid){
		 String sql = "select number from sd_tonghuajilu where code=1 and userid="+userid+" group by number";
		 return getJdbcTemplate().query(sql);
	 }
	 public List<DataRow> getAlltxlNum(int userid){
		 String sql = "select phone from sd_tongxunlu where code=1 and userid="+userid+" group by phone";
		 return getJdbcTemplate().query(sql);
	 }
	 public List<DataRow> getAllsmsNum(int userid){
		 String sql = "select phone from sd_sms where code=1 and userid="+userid+" group by phone";
		 return getJdbcTemplate().query(sql);
	 }
	 public List<DataRow> getAllsjxhID(String macaddress[],int userid){
		 String sql ="";
		 if(macaddress.length == 1){
			 sql += "select userid,macaddress from sd_phonetype where (macaddress='"+macaddress[0]+"' ";
		 }else{
			 sql += "select userid,macaddress from sd_phonetype where (macaddress='"+macaddress[0]+"' ";
			 for(int i=1;i<macaddress.length;i++){
				 sql += " or macaddress='"+macaddress[i]+"'";
			 }
		 }
		 sql +=") and userid<>"+userid+" group by userid";
		 return getJdbcTemplate().query(sql);
	 }
	 public List<DataRow> getAllthjlID(String number[],int userid){
		 String sql ="";
		 if(number.length == 1){
			 sql += "select userid,number from sd_tonghuajilu where (number='"+number[0]+"' ";
		 }else{
			 sql += "select userid,number from sd_tonghuajilu where (number='"+number[0]+"' ";
			 for(int i=1;i<number.length;i++){
				 sql += " or number='"+number[i]+"'";
			 }
		 }
		 sql +=") and userid<>"+userid+" group by userid";
		 return getJdbcTemplate().query(sql);
	 }
	 public List<DataRow> getAlltxlID(String phone[],int userid){
		 String sql ="";
		 if(phone.length == 1){
			 sql += "select userid,phone from sd_tongxunlu where (phone='"+phone[0]+"' ";
		 }else{
			 sql += "select userid,phone from sd_tongxunlu where (phone='"+phone[0]+"' ";
			 for(int i=1;i<phone.length;i++){
				 sql += " or phone='"+phone[i]+"'";
			 }
		 }
		 sql +=") and userid<>"+userid+" group by userid";
		 return getJdbcTemplate().query(sql);
	 }
	 public List<DataRow> getAllsmsID(String phone[],int userid){
		 String sql ="";
		 if(phone.length == 1){
			 sql += "select userid,phone from sd_sms where (phone='"+phone[0]+"' ";
		 }else{
			 sql += "select userid,phone from sd_sms where (phone='"+phone[0]+"' ";
			 for(int i=1;i<phone.length;i++){
				 sql += " or phone='"+phone[i]+"'";
			 }
		 }
		 sql +=") and userid<>"+userid+" group by userid";
		 return getJdbcTemplate().query(sql);
	 }
	 public List<DataRow> getAllTHJL(String userid){
		 String sql = "SELECT d.name,d.number FROM sd_tonghuajilu d WHERE userid="+userid+" GROUP BY d.number";
		 return getJdbcTemplate().query(sql);
	 }
	 public List<DataRow> getAllTXL(String userid){
		 String sql = "SELECT d.name,d.phone FROM sd_tongxunlu d WHERE userid="+userid+" GROUP BY d.phone";
		 return getJdbcTemplate().query(sql);
	 }
	 public List<DataRow> getAllcuishouM1(){
		 String sql = "SELECT user_id FROM sdcms_user WHERE (roleid=32 or roleid=21 or roleid=22 or roleid=23 or roleid=25 or roleid=50)";
		 return getJdbcTemplate().query(sql);
	 }
	 public List<DataRow> getAllcuishouM2(){
		 String sql = "SELECT user_id FROM sdcms_user WHERE (roleid=24 or roleid=51)";
		 return getJdbcTemplate().query(sql);
	 }
	 public DataRow getCuishouid(String jkid){
			
			StringBuffer sb = new StringBuffer();
			sb.append(" select cuishou_id ,onesh,userid,name,cl_status from sd_new_jkyx left join sdcms_user on cuishou_id=sdcms_user.user_id where id =  ");
			sb.append(jkid);	
			return getJdbcTemplate().queryMap(sb.toString());
	}
	 public int getQxcaiwu(int cmsuserid){
		 String sql = "select shqx from sdcms_user where user_id= "+cmsuserid;
		 return getJdbcTemplate().queryInt(sql);
	 }
	 public int getHKRow(String userid,String hktime,String hkmoney){
		 String sql = "select count(id) from sd_recharge_detail where userid= "+userid+" and nowstate = 1"+" and substring(rechargetime,1,10)='"+hktime.substring(0, 10)+"' and rechargemoney='"+hkmoney+"'";
		 return getJdbcTemplate().queryInt(sql);
	 }
	 public String getCuishouzz(String jkid){
		 String sql = "select cuishouzz from sd_new_jkyx where id= "+jkid;
		 return getJdbcTemplate().queryString(sql);
	 }
	 public Integer getCSRoleID(String csid){
		 String sql = "select roleid from sdcms_user where user_id= "+csid;
		 return getJdbcTemplate().queryInt(sql);
	 }
	 public Integer getCuishouM1State(String csid){
		 String sql = "select state from sdcms_user where user_id= "+csid;
		 return getJdbcTemplate().queryInt(sql);
	 }
	 public Integer getCSRoleIDState(String csid){
		 String sql = "select state from sdcms_user where user_id= "+csid;
		 return getJdbcTemplate().queryInt(sql);
	 }
	 public Integer getOneSH(String jkid){
		 String sql = "select onesh from sd_new_jkyx where id= "+jkid;
		 return getJdbcTemplate().queryInt(sql);
	 }
	 public Integer getDHID(int cmsuserid){
		 String sql = "select dhid from sdcms_user where user_id= "+cmsuserid;
		 return getJdbcTemplate().queryInt(sql);
	 }
	 public String getCuishouID(String jkid){
		 String sql = "select cuishou_id from sd_new_jkyx where id= "+jkid;
		 return getJdbcTemplate().queryString(sql);
	 }
	 public String getCuishouM1(String jkid){
		 String sql = "select cuishou_m1 from sd_new_jkyx where id= "+jkid;
		 return getJdbcTemplate().queryString(sql);
	 }
	 public String getSJSHJE(String jkid){
		 String sql = "select sjsh_money from sd_new_jkyx where id= "+jkid;
		 return getJdbcTemplate().queryString(sql);
	 }
	 public DataRow getCuishouBG(String csid,String time){
		 String sql = "select * from sd_accountuserhk where csid= "+csid+" and time='"+time+"'";
		 return getJdbcTemplate().queryMap(sql);
	 }
	 public double getYSJE(String csid,String time){
		 String sql = "select totaljine from sd_accountuserhk where csid= "+csid+" and time='"+time+"'";
		 return getJdbcTemplate().queryLong(sql);
	 }
	 public double getYSYCSJE(String csid,String time){
		 String sql = "select ycsjine from sd_accountuserhk where csid= "+csid+" and time='"+time+"'";
		 return getJdbcTemplate().queryLong(sql);
	 }
	 
	 public DataRow getShenheid(String jkid){
		 
		 StringBuffer sb = new StringBuffer();
		 sb.append(" select cuishou_id ,onesh,userid,name,cl_status from sd_new_jkyx left join sdcms_user on onesh=sdcms_user.user_id where id =  ");
		 sb.append(jkid);	
		 return getJdbcTemplate().queryMap(sb.toString());
	 }
	 public void updateCuishouID(DataRow row){
			
			getJdbcTemplate().update("sd_new_jkyx", row, "id", row.getString("id"));
	 }
	 public void insertCuishouRecord(DataRow row) {
		 getJdbcTemplate().insert("sd_cuishou_record", row);
	 }
	 public void updateCuiBG(DataRow row){
		 
		 getJdbcTemplate().update("sd_accountuserhk", row, "id", row.getString("id"));
	 }
	 public void insertCuiBG(DataRow row)
	 	{
	 		getJdbcTemplate().insert("sd_accountuserhk", row);
	 	}
	 
		
/*********************************************************start****2019年7月9日 催收X天分单 *********************************************************************************/

	 public double getfendancuihuijine(String jkid,String cuishouid,String curtime) {
			
		 String sql =" SELECT SUM(recharge_money) FROM sd_cuishou_fendan WHERE ";
		        sql+=" jk_id = "+jkid +" AND cuishou_id ="+ cuishouid;
		 
		        if(!curtime.isEmpty()) {
			    	  sql +=" and SUBSTRING(fendan_time,1,7)='"+curtime+"'";
			       }
		 return this.getJdbcTemplate().queryLong(sql);
	 }
	 
	 public double  getrechargeMoneyAccount(String jkid,String cuishouid ,String curtime) {
		    String sql =" SELECT SUM(REPLACE(rechargemoney,',','')) AS rechargemoney FROM sd_recharge_detail  WHERE  dqyqts >0 ";
			       sql += " AND rechargeNumber="+jkid+" AND cuishouid ="+cuishouid;
			       
			       if(!curtime.isEmpty()) {
			    	  sql +=" and SUBSTRING(rechargetime,4,7)='"+curtime+"'";
			       }
		
			 return this.getJdbcTemplate().queryLong(sql);
		 }
		 
		 
		 public void upDateUserJkXX(DataRow row) throws Exception {
				
				getJdbcTemplate().update("sd_new_jkyx", row, "id", row.getString("id"));
			}
		 
		 public List<DataRow>   getcuishoufendanid(String jkid,String cuishouid) {

			 String sql ="  SELECT MAX(id) AS id ,fendan_cs  FROM sd_cuishou_fendan  WHERE  jk_id= "+jkid+"  AND cuishou_id = "+ cuishouid+"  GROUP BY jk_id,cuishou_id ";

			 return getJdbcTemplate().query(sql);
		 }
		 
		 public void updatefendandata(DataRow row){
			 
			 getJdbcTemplate().update("sd_cuishou_fendan", row, "id", row.getString("id"));
		 }
		 public void insertSdCuishouFendan(DataRow row)
			{
				getJdbcTemplate().insert("sd_cuishou_fendan", row);
		}

		
/*********************************************************end****2019年7月9日 催收X天分单 *********************************************************************************/
		 
/************* linc  分单表数据矫正****************Start****************/
	      
	      /**
	                 * 根据 jk_id 和cuishou_id 更新数据
	       * @param udcuishoujine
	       * @param udrechargeMoney
	       * @param jkid
	       * @param cuishouid
	       */
	      public void updateCuiyqfqinfojkid(int udcuishoujine ,int udrechargeMoney,String jkid,String cuishouid){
	 		 String sql = " UPDATE sd_cuishou_fendan  SET cuishou_jine ="+ udcuishoujine+" , recharge_money = "+udrechargeMoney ;
	 		     sql+= " where jk_id = "+jkid+" and cuishou_id ="+cuishouid;
	 		 getJdbcTemplate().update(sql);
	 	 }
	      /**
	                 * 根据 id  和cuishou_id 更新数据
	       * @param udcuishoujine
	       * @param udrechargeMoney
	       * @param id
	       * @param cuishouid
	       */
	      public void updatefendaninfoid(int udcuishoujine ,int udrechargeMoney,String id,String cuishouid){
	   		 String sql = " UPDATE sd_cuishou_fendan  SET cuishou_jine ="+ udcuishoujine+" , recharge_money = "+udrechargeMoney ;
	 		     sql+= " where id = "+id+" and cuishou_id ="+cuishouid;
	 		 getJdbcTemplate().update(sql);
	 	 }
	      /**
	       * h获取分单表数据
	       * @param userid
	       * @param jkid
	       * @param cuishouid
	       * @return
	       */
	      public DataRow getfendaninfo(String userid,String jkid,String cuishouid) {
	    	  String sql ="SELECT * FROM sd_cuishou_fendan WHERE user_id ="+  userid+" AND  jk_id= "+ jkid +" AND  cuishou_id = "+cuishouid +" ORDER BY id DESC LIMIT 1 ";
	    	  
	    	 return  getJdbcTemplate().queryMap(sql);
	      }
	      
	      
	      /**
	                 * 插入数据到分单表
	       * @param row
	       */
	      public void insertCuishoufenan(DataRow row)
		 	{
		 		getJdbcTemplate().insert("sd_cuishou_fendan", row);
		 	}
	      
	      public Integer getrechagemongtotal(String cuishouid,String jkid,String curtime){
	  		 String sql = "SELECT SUM(REPLACE(rechargemoney,',','')) FROM sd_recharge_detail WHERE  cuishouid = "+cuishouid+" AND  rechargenumber = "+ jkid+"  AND dqyqts>0 AND  remark<>'延期还款' AND SUBSTRING(rechargetime,4,7)='"+curtime +"'";
	  		 return getJdbcTemplate().queryInt(sql);
	  	 }
	      
/************* linc  分单表数据矫正***************end****************/ 
	    
/************************************* 发送短信*************/
	      /**
	  	 * 插入短信内容
	  	 * @param row
	  	 */
	  	public void intsertUserMsg(DataRow row) {
	  		
	  		getJdbcTemplate().insert("sd_task_msg", row);
	  	}
	      /**
	  	 * 获取功能信息
	  	 * @param sendtype
	  	 * @param ip
	  	 * @return
	  	 */
	  	public DataRow getSendinfoSET(String sendtype,String ip ){
	  		String sql =" SELECT * FROM sdcms_function_control WHERE 1=1";
	  		if(!"".equals(sendtype)) {
	  			sql+=" and function_name ='"+sendtype+"'";
	  		}
	  		if(!"".equals(ip)) {
	  			sql+=" and ip ='"+ip+"'";
	  		}
	  		return getJdbcTemplate().queryMap(sql);
	  	}
	  	
	  	/**
	  	 * 获取用户信息---姓名
	  	 * @param sendtype
	  	 * @param ip
	  	 * @return
	  	 */
	  	public DataRow getuserfinance(String userid ){
	  		String sql =" SELECT * FROM sd_user_finance WHERE 1=1";
	  		if(!"".equals(userid)) {
	  			sql+=" and userid ="+userid;
	  		}

	  		return getJdbcTemplate().queryMap(sql);
	  	}
}
