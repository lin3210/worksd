package com.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.jdbc.session.Session;
import com.thinkive.base.service.BaseService;

public class ZuiHouCuiShouFenDanService extends BaseService {
	
private static Logger logger = Logger.getLogger(ZuiHouCuiShouFenDanService.class);
	
	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}
	
	
	// mz的意思：m1:1,m2:2,m3：3.day:0重没有提醒，3是3天没有提醒。7是7天没有提醒。-1所有的单子
	// a是查询的哪个催收人员。.num需要查询出催收单数量.0是代表所有数量
	public List<DataRow> getYqFenDan(int mz, int day, int a, int num,int yuqts) {
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT  sj.* FROM sd_new_jkyx sj WHERE  sfyhw=0  AND sfyfk=1 ");

		if (1 == mz) {
			if(yuqts>0 && yuqts<16) {
				sb.append(" AND yuq_ts>"+yuqts);
			}
			sb.append(" AND yuq_ts>0 AND yuq_ts<16 AND sj.cuishou_m1=sj.cuishou_id  ");
		} else if (2 == mz) {
			if(yuqts>15 && yuqts <46) {
				sb.append(" AND yuq_ts="+yuqts);
			}
			sb.append(" AND yuq_ts>15 AND yuq_ts<46 AND sj.cuishou_m2=sj.cuishou_id ");
		} else if (3 == mz) {
			if(yuqts>45) {
				sb.append(" AND yuq_ts>"+yuqts);
			}
			sb.append(" AND yuq_ts>45 AND sj.cuishou_m3=sj.cuishou_id ");
		}

		
		if(7 == day||3 == day||0 == day) {
			
			sb.append(" AND id NOT IN (SELECT DISTINCT sc.jkid FROM sd_new_jkyx snj LEFT JOIN sd_csmsg sc ON snj.id=sc.jkid WHERE snj.sfyhw=0 AND sfyfk=1 AND sc.cl_ren =sj.cuishou_id AND snj.cuishou_id = sj.cuishou_id  AND sc.msgtype='备注' ");
			
			if (7 == day) {			
				sb.append(" AND SUBSTRING(sc.create_time,1,10) <=date_format(now(),'%Y-%m-%d')  AND   SUBSTRING(sc.create_time,1,10) >=date_format(adddate(now(),-7),'%Y-%m-%d') ");
			}else if (3 == day) {
				
			    sb.append(" AND SUBSTRING(sc.create_time,1,10) <=date_format(now(),'%Y-%m-%d')  AND   SUBSTRING(sc.create_time,1,10) >=date_format(adddate(now(),-3),'%Y-%m-%d') ");
			}
			sb.append(" )");
		}
			
		if(0 != a) {
		   sb.append(" AND sj.cuishou_id= " + a);
		}
		
		if (0 != num) {
			sb.append(" ORDER BY RAND() LIMIT " + num);
		}
		logger.info(sb.toString());
		return getJdbcTemplate().query(sb.toString());
	}

	
	
	/**
	 *  手动分单的事务，要么全部成功，要么全部失败
	 */
	public Boolean FenDanShiWu(DataRow dataRow, DataRow rowe, int a, DataRow dataRow1, DataRow acf, DataRow row11,
			DataRow datacs) {

		// JdbcTemplate template =
		// Session session =template.getSession();
		Session session = getSession("web");

		session.beginTrans();
		try {
			int jkid = dataRow.getInt("id");
			String cuishouId = dataRow.getString("cuishou_id");
			session.update("sd_new_jkyx", dataRow, "id", dataRow.getString("id"));
			session.insert("sd_cuishou_record", rowe);
			if (0 != a) {
				session.update("sd_cuishou_fendan", dataRow1, "id", dataRow1.getString("id"));
			}
			// int c=12/0;
			session.insert("sd_cuishou_fendan", acf);
			if (null == datacs) {
				session.insert("sd_accountuserhk", row11);
			} else {
				session.update("sd_accountuserhk", row11, "id", row11.getString("id"));
			}
			session.commitTrans();
			logger.info("催收人员=" + cuishouId + ",jkid=" + jkid);
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
	 *  查询在职的催收人员
	 */
	public List<DataRow> getAllcuishou(int cuishouM) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT user_id FROM sdcms_user WHERE fdqx<>0 ");

		if (1 == cuishouM) {
			sb.append(" AND (roleid=21 or roleid=22 or roleid=23 or roleid=25 or roleid=50 ) ");
		} else if (2 == cuishouM) {
			sb.append(" AND (roleid=24 or roleid=51 ) ");
		} else if (3 == cuishouM) {
			sb.append(" AND (roleid=26 or roleid=54) ");
		}

		logger.info(sb.toString());
		return getJdbcTemplate().query(sb.toString());
	}

	
	
	
	/**
	 *  查询请假和用分单人的名单
	 */
	public List<DataRow> getUserLeavetList(int cuishouzu, String bufendansql) {

		String sql = "SELECT user_id FROM sdcms_user WHERE  fdqx<>0  AND leaver=0 ";

		if (1 == cuishouzu) {
			sql += " AND (roleid=21 OR roleid=50) ";
		} else if (2 == cuishouzu) {
			sql += " AND (roleid=24 OR roleid=51) ";

		} else if (3 == cuishouzu) {
			sql += " AND (roleid=26 OR roleid=54) ";
		}
		sql += bufendansql;
		logger.info("没有请假的人员sdcms_user==" + sql);
		// return getJdbcTemplate().queryIntArray(sql);
		return getJdbcTemplate().query(sql);
	}

	
	
	/**
	 * 查询今天要需要分的所有单数
	 */
	public List<DataRow> getAllYQListYQ(int cuishouzu,int cuishouAll[])
	{		
		String sql = " select * from  sd_new_jkyx  where  sfyhw=0 AND sfyfk=1 ";
		
		if (1 == cuishouzu) {
			sql += " AND yuq_ts>0 AND yuq_ts<16  " ;
		} else if (2 == cuishouzu) {
			sql +=" AND yuq_ts>15 AND yuq_ts<46 "   ;
		} else if (3 == cuishouzu) {
			sql += " AND yuq_ts>45  " ;
		}
		
		
		for (int i = 0; i < cuishouAll.length; i++) {
			sql += " and cuishou_id<>"+cuishouAll[i];
			//sql += " and cuishou_m1<>"+cuishouzuyqm1[i];
		}
		
		logger.info(sql);
		return getJdbcTemplate().query(sql);
		}
	
	
	public Integer getCuishouBG(String jkid){
		  String sql = "select cuishou_m1 from sd_new_jkyx where id="+jkid;
		  return getJdbcTemplate().queryInt(sql);
	 }	
	
	 public String getCuishouzz(String jkid){
		 String sql = "select cuishouzz from sd_new_jkyx where id= "+jkid;
		 return getJdbcTemplate().queryString(sql);
	 }
	 
	 public String getSJSHJE(String jkid){
		 String sql = "select sjsh_money from sd_new_jkyx where id= "+jkid;
		 return getJdbcTemplate().queryString(sql);
	 }
	 
	 public String getYQLX(String  jkid){
		 String sql ="select yuq_lx from sd_new_jkyx where  id ="+jkid ;
		 return getJdbcTemplate().queryString(sql);
	 }
	 
	 public DataRow getCuishouBG(int csid,String time){
		 String sql = "select * from sd_accountuserhk where csid= "+csid+" and time='"+time+"'";
		 return getJdbcTemplate().queryMap(sql);
	 }	
	 
	 
	 public List<DataRow>   getcuishoufendanid(int jkid,String curtime,int cuishou_z) {

			String sql = " SELECT id ,fendan_cs ,cuishou_jine ,cuishou_id,cuishou_z FROM sd_cuishou_fendan WHERE id IN  ( SELECT MAX(a.id) AS id  FROM sd_cuishou_fendan a WHERE  a.jk_id="+jkid+" )";
			if(!curtime.isEmpty()) {
		    	  sql +="  and SUBSTRING(fendan_time,1,7)='"+curtime+"'";
		       }
			sql +="  and cuishou_z="+cuishou_z;
						
			 return getJdbcTemplate().query(sql);
		 }
	 
	 
	 public double getfendancuihuijine(int jkid,int cuishouid,String curtime) {
		 String sql =" SELECT SUM(recharge_money) FROM sd_cuishou_fendan WHERE ";
		        sql+=" jk_id = "+jkid +" AND cuishou_id ="+ cuishouid;
		 
		        if(!curtime.isEmpty()) {
			    	  sql +=" and SUBSTRING(fendan_time,1,7)='"+curtime+"'";
			       }
		 return this.getJdbcTemplate().queryLong(sql);
	 }
	 
	 
	public double getrechargeMoneyAccount(int jkid, int cuishouid, String curtime) {
		String sql = " SELECT SUM(REPLACE(rechargemoney,',','')) AS rechargemoney FROM sd_recharge_detail  WHERE  dqyqts >0 ";
		sql += " AND rechargeNumber=" + jkid + " AND cuishouid =" + cuishouid;

		if (!curtime.isEmpty()) {
			sql += " and SUBSTRING(rechargetime,4,7)='" + curtime + "'";
		}
		// sql+= " AND
		// CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2))
		// >='"+starttime+"'";
		// sql+= " AND
		// CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2))
		// <='"+endTime +"'";
		return this.getJdbcTemplate().queryLong(sql);
	}

	// 根据jkid查询用户id
	public String getUserid(String jkid) {
		String sql = "select userid from sd_new_jkyx where id= " + jkid;
		return getJdbcTemplate().queryString(sql);
	}
	
	/**
	 * 根据userID，查询全部借款信息
	 * @param userid
	 * @return
	 */
	public DataRow getnewjkyx(String userid) {
		String sql = "select *  from sd_new_jkyx where sfyfk=1 and sfyhw=0 and userid= " + userid;
		return getJdbcTemplate().queryMap(sql);
	}

/******** 分单还原方法****start****************/
	/**
	 * 根据userID，查询全部借款信息
	 * @param userid
	 * @return
	 */
	public List<DataRow> getfendanhy(String createtime,String oldcuishou_id,String newcuishou_id,String operate_id) {
		String sql = "SELECT * FROM  sd_cuishou_record WHERE  unix_timestamp(DATE_FORMAT(create_time, '%Y-%m-%d'))=unix_timestamp('"+createtime+"')";
//		String sql = "SELECT * FROM  sd_cuishou_record WHERE 1=1";

			sql+=" AND oldcuishou_id ="+oldcuishou_id;
		
	
			sql+=" AND cmsuser_id ="+newcuishou_id;
	

			sql+=" AND cmsuserld_id ="+operate_id;
		logger.info(sql);
		 return getJdbcTemplate().query(sql);
	}
	
	
	
	
	
/******** 分单还原方法****end****************/
	
}
