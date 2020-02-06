package com.service;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.thinkive.base.jdbc.DBPage;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.jdbc.session.Session;
import com.thinkive.base.service.BaseService;
import com.thinkive.base.util.StringHelper;

/**
 * 自动提现service
 * @author Administrator
 *
 */
public class AotuZDSHALLService extends BaseService
{

	private static Logger logger = Logger.getLogger(AotuZDSHALLService.class);
	
	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}
//	public JdbcTemplate getJdbcTemplate2() {
//		return getJdbcTemplate("web2");
//	}	
	
	
	
	/********************************************xiong  自动分单催收 start******************************************************************/
	
	
//	public static void main(String[] args) {
//			
//		AotuZDSHALLService fc = new AotuZDSHALLService();
//	
//		List<DataRow> list=fc.getUserLeavetList(1,"");
//				int a=list.size();
//		System.out.println("数据源1"+a);
//		List<DataRow> list2=fc.getUserLeavetList2(2,"");
//			int b=list2.size();
//		System.out.println("数据源2"+b);
//					}
	
	
	 public int[] getAllkefu(){
		 String sql = "SELECT user_id FROM sdcms_user WHERE roleid=53  AND fdqx<>0 ";
		 //return getJdbcTemplate().query(sql);
		 
		 return getJdbcTemplate().queryIntArray(sql);
	 }
	
	
	
	/**
	 * 获取每日逾期状态的用户
	 */
	 public List<DataRow> getAllYQListYQNewM0()
	 {		
		 String sql = " select * from  sd_new_jkyx  where  yuq_ts=1 and sfyhw=0 and sfyfk=1 and cuishou_m0=0  ";
		 
		 logger.info(sql);
		 return getJdbcTemplate().query(sql);
	 }
	public List<DataRow> getAllYQListYQNew()
	{		
		String sql = " select * from  sd_new_jkyx  where  yuq_ts=4 and sfyhw=0 and sfyfk=1 and cuishou_m1=0  ";
		
		logger.info(sql);
		return getJdbcTemplate().query(sql);
	}
	
	/**
	  * d当日分单数
	  * @param cuishouid
	  * @param startTime
	  * @param curnowTime
	  * @param MonDayType
	  * @param cuishouz
	  * @return
	  */
	 public Integer getcuitimefendanAccount(int cuishouid ,String startTime,String curnowTime,String cuishouz) {
		 StringBuffer sb = new StringBuffer();
		 sb.append("SELECT COUNT(*) FROM sd_cuishou_fendan	WHERE cuishou_jine>0  ");
		 
		 if(cuishouid>0) {
			 sb.append(" and cuishou_id ="+cuishouid);
		 }
		 if(cuishouz.equals("M1")) {
				sb.append( " and cuishou_z= 1 ");
  		 }else if(cuishouz.equals("M2")) {
  			sb.append( " and  cuishou_z= 2");
  		 }else if(cuishouz.equals("M3")) {
  			sb.append( " and cuishou_z= 3 ");
  		 }else if(cuishouz.equals("M0")) {
   			sb.append( " and cuishou_z= 4 ");
   		 }
		 if(!StringHelper.isEmpty(startTime)) {
			 sb.append(" and SUBSTRING(fendan_time,1,10) >= '"+startTime+"' and SUBSTRING(fendan_time,1,10)<='"+curnowTime+"'");
		 }else {
			 sb.append(" and SUBSTRING(fendan_time,1,10) ='" +curnowTime+"'");
		 }
		 return getJdbcTemplate().queryInt(sb.toString());
	
	 }
	
	
	//手动分单的事务，要么全部成功，要么全部失败
		public Boolean FenDanShiWu(DataRow dataRow,DataRow rowe,int a,DataRow dataRow1,DataRow acf,DataRow row11,DataRow datacs ){
			
			//JdbcTemplate template = 
			//Session session =template.getSession();
			Session session = getSession("web");
				
			session.beginTrans();
			try {
				int jkid=dataRow.getInt("id");
				String cuishouId=dataRow.getString("cuishou_id");
				session.update("sd_new_jkyx", dataRow, "id", dataRow.getString("id"));
				session.insert("sd_cuishou_record", rowe);
				if(0!=a) {
					session.update("sd_cuishou_fendan", dataRow1, "id", dataRow1.getString("id"));
				}	
			//	int c=12/0;
				session.insert("sd_cuishou_fendan", acf);
				/*if(null==datacs) {
					session.insert("sd_accountuserhk", row11);
				}else {
					session.update("sd_accountuserhk", row11, "id",row11.getString("id"));
				}*/			
				session.commitTrans();
				logger.info("催收人员="+cuishouId+",jkid="+jkid);
				return true;
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e);
				e.printStackTrace();
				session.rollbackTrans();
			}finally{session.close();}
			return false;
		}
		
	/**
	 * xiong-20190702-自动分记录表。
	 * 
	 */
	 public void insertCuishouRecord(DataRow row) {
		 getJdbcTemplate().insert("sd_cuishou_record", row);
	 }
	
	//根据jkid查询用户id
	public String getUserid(String jkid){
		 String sql = "select userid from sd_new_jkyx where id= "+jkid;
		 return getJdbcTemplate().queryString(sql);
	 }
	
	public List<DataRow> getAllJK()
	{		
		String sql = " select * from  sd_new_jkyx WHERE SUBSTRING(daytime,1,7)='2019-05'";
		
		return getJdbcTemplate().query(sql);
	}
	
	//查询请假和不用分单人的名单
	 public List<DataRow> getUserLeavetList(int cuishouM,String bufendansql ) {
			 
			 String sql ="SELECT user_id FROM sdcms_user WHERE  fdqx<>0  AND leaver=0 ";		 
			 
			 	if (0==cuishouM) {
					sql+=" AND (roleid=19 OR roleid=20) ";	
				}else if (1==cuishouM) {
					sql+=" AND (roleid=21 OR roleid=50) ";									
//					if(1==a) {
//					sql+=" AND  user_id<>5002 AND user_id<>5074 AND user_id<>5089 ";
//						sql+=" bufendansql ";	
//					}					
				}else if (2==cuishouM) {
					sql+=" AND (roleid=24 OR roleid=51) ";					
					
				}else if (3==cuishouM) {
					sql+=" AND (roleid=26 OR roleid=54 or roleid=60 OR roleid=61) ";
									
				}				
				sql+= bufendansql ;			
			 logger.info("今天请假的人员sdcms_user=="+sql);
		   // return getJdbcTemplate().queryIntArray(sql);			 
			 return getJdbcTemplate().query(sql);		
		 }
	 
	 
	 
	
	/**
	 * xiong-20190724-3天不使用禁止掉。
	 * 
	 */
	
	public int updateUserStopList(String lasttime){
		 String sql = " UPDATE sdcms_user SET state=0 where  roleid<>1 and LAST_TIME <'"+lasttime+"'";
		        		
		 logger.info("3天没有使用的账号禁用===="+sql);
		 return getJdbcTemplate().update(sql);
	}
	
	 public String getCuishouzz(String jkid){
		 String sql = "select cuishouzz from sd_new_jkyx where id= "+jkid;
		 return getJdbcTemplate().queryString(sql);
	 }
	 
	 
	
	/**
	 * xiong-20190710-更改請假的催收单子。
	 * 
	 */
	
	public int updateUserLeavetList(int userid,int leaver){
		 String sql = " UPDATE sdcms_user SET leaver="+leaver +"  where USER_ID ="+userid;
		        
		
		 logger.info("今天请假的人员sdcms_user"+sql);
		 return getJdbcTemplate().update(sql);
	}

	/**
	 * xiong-20190710-查询所有请假的催收人员
	 *
	 * 
	 * 
	 */
	public List<DataRow> selectUserLeavetList(){
		String sql = "select user_id ,name, phone ,rolename ,login_times, last_time ,state,u.leaver from sdcms_user u left join sdcms_user_role r on u.roleid=r.id  where  leaver<>0 order by  u.USER_ID desc" ;
		return getJdbcTemplate().query(sql);
	}
	
	
	
	/**
	 * xiong-20190702-催收分单表，金额不相加。
	 * 
	 */
	public void insertSdCuishouFendan(DataRow row)
	{
		getJdbcTemplate().insert("sd_cuishou_fendan", row);
	}
	public void updatePX(DataRow row3)
	{
		
		//getJdbcTemplate().update("sd_user", row3, "id", row3.getString("id"));
		getJdbcTemplate().update(" sdcms_user",row3,"user_id",row3.getString("user_id"));
	}
	/**
	  * 应催收金额  sd_cuishou_fendan
	  * @param cuishouid
	  * @param startTime
	  * @param endtTime
	  * @param MonDayType
	  * @return
	  */
	public double getCSFendanjine(int cuishouid ,String startTime,String endtTime,int MonDayType,int cuishouz) {
			String sql =" SELECT SUM(cuishou_jine) FROM sd_cuishou_fendan WHERE ";

			if (cuishouid > 0) {
				sql+=" cuishou_id=  "+ cuishouid +" and ";
				if (!StringHelper.isEmpty(startTime)) { //界面有时间选择
					sql +=" SUBSTRING(fendan_time,1,10)>='"+ startTime+"'  and SUBSTRING(fendan_time,1,10) <= '" +endtTime+"' ";
				}else if (!StringHelper.isEmpty(endtTime)) {
					if (1 ==MonDayType) {
						sql +=" SUBSTRING(fendan_time,1,10) = '"+ endtTime+"'";
					} else if (2==MonDayType) {
						sql +=" SUBSTRING(fendan_time,1,7) = SUBSTRING('"+ endtTime+"',1,7)";
					}
				}
				
			}else {
				if (!StringHelper.isEmpty(startTime)) { //界面有时间选择
					sql +=" SUBSTRING(fendan_time,1,10)>='"+ startTime+"'  and SUBSTRING(fendan_time,1,10) <= '" +endtTime+"' ";
				}else if (!StringHelper.isEmpty(endtTime)) {
					if (1 ==MonDayType) {
						sql +=" SUBSTRING(fendan_time,1,10)= '"+ endtTime+"'";
					} else if (2==MonDayType) {
						sql +=" SUBSTRING(fendan_time,1,7)=SUBSTRING('"+ endtTime+"',1,7) ";
					}
				}
			}
			if(cuishouz==1) {
	   			 sql+= " and cuishou_z=1";
	   		 }else if(cuishouz==2) {
	   			 sql+= " and cuishou_z=2";
	   		 }else if(cuishouz==3) {
	   			 sql+= " and cuishou_z=3";
	   		 }else if(cuishouz==4) {
	   			 sql+= " and cuishou_z=4";
	   		 }
			//logger.info("getCSTJychzjehk + sql  "+ sql);
			return this.getJdbcTemplate().queryLong(sql);
		}

	/** 
	  * 时间格式 dd-MM-yyyy
	  * 已经催回金额
	  * @return
	  */
	 public double getCSTJychzje(int userid ,String startTime,String endtTime,int MonDayType,int cuishouz) {
			
			String sql ="SELECT SUM(REPLACE(rechargeMoney,',','')) FROM sd_recharge_detail WHERE dqyqts >0 and remark<>'延期还款'  ";
			if (userid>0) {
				sql +=" and  cuishouid = "+ userid;
			}
			if (!StringHelper.isEmpty(startTime)) { //界面有时间选择
				sql +="  and  CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2))>='"+ startTime+"'  and  CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2)) <= '" +endtTime+"' ";
			}else if (!StringHelper.isEmpty(endtTime)) {
				if (1 ==MonDayType) {
					sql +=" and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2)) = '"+ endtTime+"'";
				} else if (2==MonDayType) {
					sql +="  and CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2))  =SUBSTRING('"+ endtTime+"',1,7) ";
				}
			}
			if(cuishouz==1) {
	   			 sql+= " and dqyqts > 0 and dqyqts <=15";
	   		 }else if(cuishouz==2) {
	   			sql+= " and dqyqts > 15 and dqyqts <=45";
	   		 }else if(cuishouz==3) {
	   			sql+= " and dqyqts > 45";
	   		 }
			return this.getJdbcTemplate().queryLong(sql);
		}
	 
	 
	 public int getfdqx(int userid) {

			String sql = "select fdqx from sdcms_user where  user_id =" + userid;
			return getJdbcTemplate().queryInt(sql);
		}
	 
	 /** 
	  * 时间格式 dd-MM-yyyy
	  * 当日已催回单数
	  * @return
	  */
	 public Integer getCSTJychds1(int userid ,String startTime,String endtTime,int MonDayType,int cuishouz) {
		 StringBuffer sb = new StringBuffer();
		 sb.append("SELECT COUNT(r.id) FROM sd_recharge_detail r LEFT JOIN sd_new_jkyx j ON r.rechargenumber=j.id WHERE (r.remark='全额还款' OR r.remark='完成还款，部分逾期利息还款') AND r.dqyqts>0 AND j.sfyhw=1  ");
			if (userid>0) {
				sb.append(" and j.cuishou_id = "+ userid);
				sb.append(" and ((j.cuishou_m1 = "+ userid + " AND r.dqyqts>0  and r.dqyqts<=15 )");
				sb.append(" or (j.cuishou_m2 = "+ userid+ " and j.yuq_ts>15 and r.dqyqts<=45) ");
				sb.append(" or (j.cuishou_m3 = "+ userid +" and j.yuq_ts>45 ))");
			}

			if(!StringHelper.isEmpty(startTime)) {
					sb.append(" and (CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2))>='"+startTime +"'"
							+ " and CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2))<='"+endtTime+"')");
			}else if(!StringHelper.isEmpty(endtTime)) {
				if(1==MonDayType) {   //d查询当天
					sb.append(" and ( CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2),'-',SUBSTRING(r.rechargetime,1,2))='"+endtTime+"')" );
				}else if(2==MonDayType) {//d查询当月
					sb.append(" and  CONCAT(SUBSTRING(r.rechargetime,7,4),'-',SUBSTRING(r.rechargetime,4,2))= SUBSTRING('"+endtTime+"',1,7)  ");
				}
			}
			if(cuishouz==1) {
				sb.append(" and dqyqts > 0 and dqyqts <=15 ");
	   		 }else if(cuishouz==2) {
	   			sb.append(" and dqyqts > 15 and dqyqts <=45");
	   		 }else if(cuishouz==3) {
	   			sb.append(" and dqyqts > 45 " );
	   		 }

		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	
	/********************************************xiong  自动分单催收 end******************************************************************/

	/**
	 * 获取每日待审核状态的借款信息
	 */
	public List<DataRow> getAllSHList()
	{		
		String sql = " select * from  sd_new_jkyx  where  cl_status=0 and jk_type is null ";
		
		return getJdbcTemplate().query(sql);
	}
	/**
	 * 获取每日待审核状态的借款信息
	 */
	
	public List<DataRow> getAllJKList()
	{		
		String sql = " select * from  sd_new_jkyx  where  sfyhw=0 and sfyfk=1 ";
		
		return getJdbcTemplate().query(sql);
	}
	 public List<DataRow> getUserRecThreeInfo2()
		{
			String sql = "select sd_user.id as userid ,cardNo,createtime, mobilePhone ,username , cardUserName ,bankbs ,bankName ,remark,j.id as id,j.noagree as noagree, j.userid,j.sfyhw,j.sfyfk from sd_user  left join  sd_bankcard on sd_bankcard.userId =sd_user.id left join sd_new_jkyx j on j.userid=sd_user.id where  sfyhw=0 and sfyfk=1  " ;

			return getJdbcTemplate().query(sql);
		}
	 public List<DataRow> getAlljiami()
	 {
		 String sql = "select * from sd_ziliao limit 100000" ;
		 
		 return getJdbcTemplate().query(sql);
	 }
	  public String getSjsh(String jkid){
			StringBuffer sb = new StringBuffer();
			sb.append("select sjsh_money+yuq_lx from sd_new_jkyx  where id="+jkid );
			return getJdbcTemplate().queryString(sb.toString());
		}
	 public void updateZDHK(DataRow row1)
		{
			getJdbcTemplate().update("sd_new_jkyx", row1, "id", row1.getString("id"));
		}
	 public List<DataRow> getAllTHJL(String userid){
		 String sql = "SELECT d.name,d.number FROM sd_tonghuajilu d WHERE userid="+userid+" GROUP BY d.number";
		 return getJdbcTemplate().query(sql);
	 }
	 public List<DataRow> getAllTXL(String userid){
		 String sql = "SELECT d.name,d.phone FROM sd_tongxunlu d WHERE userid="+userid+" GROUP BY d.phone";
		 return getJdbcTemplate().query(sql);
	 }
	/**
	 * 获取每日逾期状态的没有分配借款信息
	 */
	public List<DataRow> getAllYQ30()
	{		
		String sql = " select userid,id from  sd_new_jkyx  where  sfyhw=0 and sfyfk=1 and yuq_ts=30 ";
		return getJdbcTemplate().query(sql);
	}
	/**
	 * 获取每日逾期状态的没有分配借款信息
	 */
	public DataRow getShenfen(String userid)
	{		
		String sql = " select * from  sd_user_finance  where  userid="+userid;
		return getJdbcTemplate().queryMap(sql);
	}
	public DataRow getBankcard(String userid)
	{		
		String sql = " select * from  sd_bankcard  where  userid="+userid;
		return getJdbcTemplate().queryMap(sql);
	}
	public DataRow getUserUser(String userid)
	{		
		String sql = " select * from  sd_user  where  id="+userid;
		return getJdbcTemplate().queryMap(sql);
	}
	public DataRow getUserJK(String jkid)
	{		
		String sql = " select * from  sd_new_jkyx  where  id="+jkid;
		return getJdbcTemplate().queryMap(sql);
	}
	public DataRow getZhaopian(String userid)
	{		
		String sql = " select * from  sd_zhaopian  where  userid="+userid;
		return getJdbcTemplate().queryMap(sql);
	}
	public DataRow getLianxi(String userid)
	{		
		String sql = " select * from  sd_lianxi  where  userid="+userid;
		return getJdbcTemplate().queryMap(sql);
	}
	public DataRow getDingwei(String userid)
	{		
		String sql = " select * from  sd_dwip  where  userid="+userid;
		return getJdbcTemplate().queryMap(sql);
	}
	 public String  getRealname(int userid){
			
		 String sql = "select realname from sd_user_finance where  userid ="+userid ;	 
		 return getJdbcTemplate().queryString(sql);	 
	} 
	 public String  getCardName(String userid){
		 
		 String sql = "select cardname from sd_bankcard where  userid ="+userid ;	 
		 return getJdbcTemplate().queryString(sql);	 
	 } 
	 public String  getNapasCode(String userid){
		 
		 String sql = "select napasbankno from sd_bankcard where  userid ="+userid ;	 
		 return getJdbcTemplate().queryString(sql);	 
	 } 
	 public Integer getSFCount(String idno, String realname){
		    String sql = "select count(*) from sd_user_finance where  idno ='"+idno+"' " ;
			return getJdbcTemplate().queryInt(sql);
	}
	 public List<DataRow> getALLshenfen(String idno, String realname){
		 String sql = "select userid from sd_user_finance where  idno ='"+idno+"'" ;
		 return getJdbcTemplate().query(sql);
	 }
	 
	 public int getphoneidno_pp(String idno, String mobilePhone){
		 String sql = " SELECT COUNT(*) FROM sduser_new_phone_dxsh WHERE idno='"+idno+"' AND phone='"+mobilePhone+"'";
		 return getJdbcTemplate().queryInt(sql);
	 }
	 
	 public void addUserException(DataRow data)
		{
			getJdbcTemplate().insert(" sd_exception", data);
		}
	 public String  getIdno(int userid){
			
		 String sql = "select idno from sd_user_finance where  userid ="+userid ;	 
		 return getJdbcTemplate().queryString(sql);	 
	}
	/**
	 * 获取每日逾期状态的没有分配借款信息
	 */
	public List<DataRow> getAllMYJKList(String today)
	{		
		String sql = " SELECT sd_user.id as id,mobilephone FROM sd_user LEFT JOIN sd_new_jkyx ON sd_user.`id`=sd_new_jkyx.`userid` WHERE vipStatus=1 AND jk_date IS NULL AND isjop=1 AND islianxi=1 AND yhbd=1 AND profession=2 AND substring(yearmonthday,1,10)>='2018-10-20'";
		
		return getJdbcTemplate().query(sql);
	}
	/**
	 * 查找认证完成，没有提交借款用户
	 * @param starttime
	 * @param endtime
	 * @return
	 */
	public List<DataRow> getAllMYJKList2(String starttime,String endtime)
	{		
		String sql = " SELECT sd_user.id,mobilePhone FROM sd_user WHERE isShenfen=1 AND yhbd =1 AND isLianxi =1 ";
		
		if(!"".equals(starttime)) {
			sql+="   AND SUBSTRING(yearmonthday,1,10) >='"+starttime+"'";
		}
		if(!"".equals(endtime)) {
			sql+="   AND SUBSTRING(yearmonthday,1,10) <='"+endtime+"'";
		}
		sql+=" HAVING sd_user.id NOT IN (SELECT userid FROM sd_new_jkyx ) ";
		//logger.info("getAllMYJKList2:"+sql);
		return getJdbcTemplate().query(sql);
	}
	
	/**
	 * 获取每日逾期状态的没有分配借款信息
	 */
	public List<DataRow> getAllYQList(String ri, String yue)
	{		
		String sql = " select sd_new_jkyx.id,sd_user.username from  sd_new_jkyx left join sd_user on sd_new_jkyx.userid=sd_user.id where  sfyhw=0 and sfyfk=1 and cuishou_id=0 ";
		sql += " and ((SUBSTRING(hkyq_time,1,2) ='" + ri + "'";
		sql += " and SUBSTRING(hkyq_time,4,2) ='" + yue+ "')";
		sql += " or (SUBSTRING(hkfq_time,1,2) ='" + ri + "'";
		sql += " and SUBSTRING(hkfq_time,4,2) ='" + yue+ "'))";
		return getJdbcTemplate().query(sql);
	}
	/**
	 * 获取每日逾期状态的没有分配借款信息
	 */
	public List<DataRow> getAllZCWRZList(String today, String nextday)
	{		
		//String sql = " select mobilephone from  sd_user  where  vipstatus=0 and heihu_zt=0 and yearmonthday>'"+today+"'"+" and yearmonthday <'"+nextday+"' ";
		String sql = " select mobilephone from  sd_user  where  vipstatus=0 and heihu_zt=0 and substring(yearmonthday,1,10)='"+today+"'";
		return getJdbcTemplate().query(sql);
	}
	/**
	 * 获取每日逾期状态的没有分配借款信息
	 */
	public List<DataRow> getAllcmsUserList(String today, String nextday)
	{		
		String sql = " select user_id,last_time from  sdcms_user ";
		return getJdbcTemplate().query(sql);
	}
	public String getTXHKGZ() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang1 from sd_pingjiguize  where id =20 ");
		
		return getJdbcTemplate().queryString(sb.toString());
	}
	public int getTXHK1() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang1 from sd_pingjiguize  where id =21 ");
		
		return getJdbcTemplate().queryInt(sb.toString());
	}
	public int getTXHKBL1() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang2 from sd_pingjiguize  where id =21 ");
		
		return getJdbcTemplate().queryInt(sb.toString());
	}
	public int getTXHK2() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang1 from sd_pingjiguize  where id =22 ");
		
		return getJdbcTemplate().queryInt(sb.toString());
	}
	public int getTXHKBL2() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang2 from sd_pingjiguize  where id =22 ");
		
		return getJdbcTemplate().queryInt(sb.toString());
	}
	public int getTXHK3() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang1 from sd_pingjiguize  where id =23 ");
		
		return getJdbcTemplate().queryInt(sb.toString());
	}
	public int getTXHKBL3() {
		StringBuffer sb = new StringBuffer();
		sb.append("select guizebianliang2 from sd_pingjiguize  where id =23 ");
		
		return getJdbcTemplate().queryInt(sb.toString());
	}
	
	//F458的客服人员
		public int getTXHK4() {
			StringBuffer sb = new StringBuffer();
			sb.append("select guizebianliang1 from sd_pingjiguize  where id =28 ");
			
			return getJdbcTemplate().queryInt(sb.toString());
		}
	/**
	 * 获取每日逾期状态的用户
	 */
	public List<DataRow> getAllYQListYQM0(String txhkzu[])
	{		
		String sql = " select * from  sd_new_jkyx  where  yuq_ts>0 and sfyhw=0 and sfyfk=1 and " ;
		for (int i = 0; i < txhkzu.length; i++) {
			if(i==0){
				sql += " (cuishou_id="+txhkzu[0];
			}else if(i>0 && i<txhkzu.length-1 ){
				sql += " or cuishou_id="+txhkzu[i];
			}else{
				sql += " or cuishou_id="+txhkzu[txhkzu.length-1]+") ";
			}
			
		}
		logger.info(sql);
		return getJdbcTemplate().query(sql);
	}
	public List<DataRow> getAllYQListYQ(String txhkzu[])
	{		
		String sql = " select * from  sd_new_jkyx  where  yuq_ts>0 and sfyhw=0 and sfyfk=1 and " ;
		for (int i = 0; i < txhkzu.length; i++) {
			if(i==0){
				sql += " (cuishou_id="+txhkzu[0];
			}else if(i>0 && i<txhkzu.length-1 ){
				sql += " or cuishou_id="+txhkzu[i];
			}else{
				sql += " or cuishou_id="+txhkzu[txhkzu.length-1]+") ";
			}
			
		}
		logger.info(sql);
		return getJdbcTemplate().query(sql);
	}
	/**
	 * 获取每日逾期状态的用户
	 */
	public List<DataRow> getAllYQListYQM0(int cuishouzuyqm0[])
	{		
		String sql = " select * from  sd_new_jkyx  where  yuq_ts>0 and yuq_ts<4 and sfyhw=0 and sfyfk=1 and cuishou_id<>10 ";
		for (int i = 0; i < cuishouzuyqm0.length; i++) {
			sql += " and cuishou_id<>"+cuishouzuyqm0[i];
			//sql += " and cuishou_m1<>"+cuishouzuyqm1[i];
		}
		logger.info(sql);
		return getJdbcTemplate().query(sql);
	}
	public List<DataRow> getAllYQListYQM1(int cuishouzuyqm1[])
	{		
		String sql = " select * from  sd_new_jkyx  where  yuq_ts>3 and yuq_ts<16 and sfyhw=0 and sfyfk=1 and cuishou_id<>10 ";
		for (int i = 0; i < cuishouzuyqm1.length; i++) {
			sql += " and cuishou_id<>"+cuishouzuyqm1[i];
			//sql += " and cuishou_m1<>"+cuishouzuyqm1[i];
		}
		logger.info(sql);
		return getJdbcTemplate().query(sql);
	}
	/**
	 * 提前逾期8-15天的催收单
	 * @param cuishouzuyqm1
	 * @return
	 */
	public List<DataRow> getAllYQListYQM1_T()
	{		
		String sql = " select * from  sd_new_jkyx  where  yuq_ts>7 and yuq_ts<16 and sfyhw=0 and sfyfk=1 ";
		logger.info(sql);
		return getJdbcTemplate().query(sql);
	}
	
	/**
	 * 提前逾期8-15天的催收单
	 * @param cuishouzuyqm1
	 * @return
	 */
	public List<DataRow> getAllYQListYQM3_T()
	{		
		String sql = " select * from  sd_new_jkyx  where  yuq_ts>45 and sfyhw=0 and sfyfk=1 ";
		logger.info(sql);
		return getJdbcTemplate().query(sql);
	}
	public List<DataRow> getAllYQListYQM2_T()
	{		
		String sql = " select * from  sd_new_jkyx  where  yuq_ts>15 and yuq_ts<46 and sfyhw=0 and sfyfk=1 ";
		logger.info(sql);
		return getJdbcTemplate().query(sql);
	}
	
	public List<DataRow> getAllYQListYQM2(int cuishouzuyqm2[])
	{		
		String sql = " select * from  sd_new_jkyx  where  yuq_ts>15 and yuq_ts<46 and sfyhw=0 and sfyfk=1 and cuishou_id<>10 ";
		for (int i = 0; i < cuishouzuyqm2.length; i++) {
			sql += " and cuishou_id<>"+cuishouzuyqm2[i];
			//sql += " and cuishou_m2<>"+cuishouzuyqm2[i];
		}
		logger.info(sql);
		return getJdbcTemplate().query(sql);
	}
	/**
	 * 获取每日逾期状态的用户
	 */
	public List<DataRow> getAllYQListYQM3(int cuishouzuyqm3[])
	{		
		String sql = " select * from  sd_new_jkyx  where  yuq_ts>60 and sfyhw=0 and sfyfk=1  and cuishou_id<>10 ";
		for (int i = 0; i < cuishouzuyqm3.length; i++) {
			sql += " and cuishou_id<>"+cuishouzuyqm3[i];
			//sql += " and cuishou_m3<>"+cuishouzuyqm3[i];
		}
		logger.info(sql);
		return getJdbcTemplate().query(sql);
	}
	
	/**
	 * 获取所有银行卡
	 */
	public List<DataRow> getAllBank()
	{		
		String sql = " select * from  sd_bankcard  where napasbankno=0 ";
		return getJdbcTemplate().query(sql);
	}
	
	/**
	 * 用户成功借款记录
	 * 
	 */
	public Integer getSuccessfulLoanCount(String userid){
		
		StringBuffer sb = new StringBuffer();
		sb.append("select count(1) from sd_new_jkyx where yuq_ts =0 and jksfwc =1  and userid = "+userid);		
		return getJdbcTemplate().queryInt(sb.toString());
	}
    public Integer getSuccessfulLoanCountYQ(String userid){
		
		StringBuffer sb = new StringBuffer();
		sb.append("select count(1) from sd_new_jkyx where yuq_ts > 0 and jksfwc =1  and userid = "+userid);		
		return getJdbcTemplate().queryInt(sb.toString());
	}
	
	/**
	 * 
	 * 更新借款记录
	 * 
	 */
	public void updateUserJk(DataRow row) throws Exception {
		
		getJdbcTemplate().update("sd_new_jkyx", row, "id", row.getString("id"));
	}
	/**
	 * 
	 * 更新借款记录
	 * 
	 */
	public void updateUsercms(DataRow row) throws Exception {
		
		getJdbcTemplate().update("sdcms_user", row, "user_id", row.getString("user_id"));
	}
	/**
	 * 
	 * 更新分配记录
	 * 
	 */
	public void updateUserFP(DataRow row) throws Exception {
		
		getJdbcTemplate().update("sd_new_jkyx", row, "id", row.getString("id"));
	}
	 public Integer getJKCount(int userid2){
			StringBuffer sb = new StringBuffer();
			sb.append("select count(*) from sd_new_jkyx  where sfyhw=0 and cl_status <>3 and cl02_status <>3 and cl03_status <>3  and  userid =  ");
			sb.append(userid2);
			return getJdbcTemplate().queryInt(sb.toString());
	}
	 public String getJKMoney(int userid2){
		 StringBuffer sb = new StringBuffer();
		 sb.append("select jk_money from sd_new_jkyx  where sfyhw=0 and cl_status <>3 and cl02_status <>3 and cl03_status <>3  and  userid =  ");
		 sb.append(userid2);
		 return getJdbcTemplate().queryString(sb.toString());
	 }
	 public Integer getJKDate(int userid2){
		 StringBuffer sb = new StringBuffer();
		 sb.append("select jk_date from sd_new_jkyx  where sfyhw=0 and cl_status <>3 and cl02_status <>3 and cl03_status <>3  and  userid =  ");
		 sb.append(userid2);
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 public Integer getJKID(int userid2){
		 StringBuffer sb = new StringBuffer();
		 sb.append("select id from sd_new_jkyx  where sfyhw=0 and cl_status <>3 and cl02_status <>3 and cl03_status <>3  and  userid =  ");
		 sb.append(userid2);
		 return getJdbcTemplate().queryInt(sb.toString());
	 }
	 public Integer  getHHZT(int userid){
			
		 String sql = "select heihu_zt from sd_user where  id ="+userid ;
		 return getJdbcTemplate().queryInt(sql);	 
		}
	 public Integer  getShenfen(int userid){
		 
		 String sql = "select isshenfen from sd_user where  id ="+userid ;
		 return getJdbcTemplate().queryInt(sql);	 
	 }
	 public Integer  getYHBD(int userid){
		 
		 String sql = "select yhbd from sd_user where  id ="+userid ;
		 return getJdbcTemplate().queryInt(sql);	 
	 }
	 public Integer  getLianxi(int userid){
		 
		 String sql = "select islianxi from sd_user where  id ="+userid ;
		 return getJdbcTemplate().queryInt(sql);	 
	 }
	/**
	 * 审核消息
	 * 
	 */
	public void insertUserJK(DataRow row)
	{
		getJdbcTemplate().insert("sd_new_jkyx", row);
	}
	/**
	 * 审核消息
	 * 
	 */
	  public void insertUserMsg(DataRow row)
	  	{
	  		getJdbcTemplate().insert("sd_msg", row);
	  	}
	  
	  public void insertUserjiami(DataRow row)
	  {
		  try {
	   			getJdbcTemplate().insert("sd_ziliaojiami", row);
	   		} catch (Exception e) {
	   			logger.error(e);
	   			e.printStackTrace();
	   		}
	  }
	  
	  public Integer getZmscore(String userid){
			
			StringBuffer sb = new StringBuffer();
			sb.append("select zm_score from sd_zmrz where   userid = "+userid);		
			return getJdbcTemplate().queryInt(sb.toString());
		}
	  public void  updateUserInfo(DataRow row) throws Exception {
	 		
	 		getJdbcTemplate().update("sd_user", row, "id", row.getString("id"));
	 	}
	  public Integer getTonghuats(String userId){
	 		StringBuffer sb = new StringBuffer();
	 		sb.append("select count(1) from sd_tonghuajilu where  userid = ");
	 		sb.append(userId);
	 		return getJdbcTemplate().queryInt(sb.toString());
	 	}
	  public Integer getTongxunluts(String userId){
		  String sql = "select COUNT(DISTINCT phone) from sd_tongxunlu where  userid = "+userId+" group by userid";
		  return getJdbcTemplate().queryInt(sql);
	  }
	  public Integer getTongxunluGZ(){
		  String sql = "select guizebianliang1 from sd_pingjiguize where id=7 ";
		  return getJdbcTemplate().queryInt(sql);
	  }
	  //主叫
	  public Integer getLxThtszj(String userid ,String phone){
	 		StringBuffer sb = new StringBuffer();
	 		sb.append("select count(*) from sd_tonghuajl where dial_type='DIAL' and userid ="+userid+" and peer_number='"+phone +"'"  ); 		
	 		return getJdbcTemplate().queryInt(sb.toString());
	 	}
	  //被叫
	  public Integer getLxThtsbj(String userid ,String phone){
	 		StringBuffer sb = new StringBuffer();
	 		sb.append("select count(*) from sd_tonghuajl where dial_type='DIALED' and userid ="+userid+" and peer_number='"+phone +"'"  ); 		
	 		return getJdbcTemplate().queryInt(sb.toString());
	 	}
 
	  public DataRow getAllZMSHList(String userid){
			
		   String sql = "select p.contactPhone ,p.contactPhone02 from   sd_personjk p where  userId ="+userid;
			return getJdbcTemplate().queryMap(sql);
		}
	  public DataRow getUserZmfs(String  userid ){			
		    StringBuffer sb = new StringBuffer();
			sb.append("select * from sd_zmrz where successjg='true'  and userid ="+userid );
			
			return getJdbcTemplate().queryMap(sb.toString());
		}
	  public String getUserZt(String userid){
			
			String sql ="select heihu_zt from sd_user where id ="+userid ;
			return getJdbcTemplate().queryString(sql);

		}
	  public String getUserXYPF(String userid){
			
			String sql ="select pipei from sd_new_jkyx where id ="+userid ;
			return getJdbcTemplate().queryString(sql);

		}
	  public int getUserMYJK(String userid){
		  
		  String sql ="select myjkcode from sd_new_jkyx where id ="+userid ;
		  return getJdbcTemplate().queryInt(sql);
		  
	  }
	  public int getUserFSDX(String userid){
		  
		  String sql ="select fsdxcode from sd_new_jkyx where id ="+userid ;
		  return getJdbcTemplate().queryInt(sql);
		  
	  }
	  public String getUserName(String userid){
		  
		  String sql ="select username from sd_user where id ="+userid ;
		  return getJdbcTemplate().queryString(sql);
		  
	  }
	  public String getMobilePhone(String userid){
			 
			 String sql ="select mobilePhone from sd_user where id ="+userid ;
			 return getJdbcTemplate().queryString(sql);
			 
		 }
	  public String getRef(String userid){
			
			String sql ="select refferee from sd_user where id ="+userid ;
			return getJdbcTemplate().queryString(sql);

		}
	  public String getMobilephone(String userid){
		  
		  String sql ="select mobilephone from sd_user where id ="+userid ;
		  return getJdbcTemplate().queryString(sql);
		  
	  }
	  
	  public DataRow getPersonYdInfo(String  userid)
		{
		 
			String sql = "select orderid ,idnumber,loanplatformcount,loanlastmodifiedtime,repaymentplatformcount ,repaymenttimescount ,totaltrackcount from sduser_portrait  where  userid = " +userid  +"  order by lasttracktime desc limit 1  ";		
			
			
			return getJdbcTemplate().queryMap(sql);
		}
	 
	  public boolean updateJKInfo(String jkid, DataRow data)
	  {
	      Session session = getSession("web");
	    session.beginTrans();
	    try {
	      session.insert("sd_firfail_jkyx", data);

	      session.commitTrans();
	      return true;
	    }
	    catch (Exception e) {
	      logger.error(e);
	      e.printStackTrace();
	      session.rollbackTrans(); } finally {
	      session.close();
	    }return false;
	  }
	  /**
		 * 扣款申请(手动提交事务，如出现问题变回滚)
		 */
		public boolean userKk(DataRow withdraw,DataRow user,String money)
		{
		
			Session session = getSession("web");
			session.beginTrans();
			try {
				DataRow fundrecord = new DataRow();//添加资金流向
	        	fundrecord.set("userid", user.getInt("id"));
	        	fundrecord.set("fundmode", "自动扣款");
	        	fundrecord.set("handlesum", money);
	        	fundrecord.set("usablesum", user.getDouble("usablesum"));
	        	fundrecord.set("recordtime", new Date());
	        	fundrecord.set("operatetype", 100);
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
			}finally{session.close();}
			return false;
		}
		 public DataRow getUser(String   userid)
			{
				String sql = "select * from sd_user where id= " +userid ;

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
		  public DataRow getWithdraw(String orderId)
  		{    			
  	        String sql = "select * from sd_withdraw  where   orderid = '"+orderId +"'" ;
  			return getJdbcTemplate().queryMap(sql);
  		}
    
		  public void updateWithRawInfo(DataRow row1){
		  		
		  		getJdbcTemplate().update("sd_withdraw ", row1, "orderid", row1.getString("orderid"));
		  	}
		  public DataRow getUserInfo(String userid)
			{
				String sql = "select u.id ,b.cardUserName ,u.mobilePhone ,u.username from sd_user u left join sd_bankcard b on b.userid = u.id  where u.id = "+userid ;
				return getJdbcTemplate().queryMap(sql);
		}
		  
		  public List<DataRow> getAllcuishouM0(){
			  String sql = "SELECT user_id FROM sdcms_user WHERE (roleid=19 or roleid=20) AND fdqx<>0 ";
			  return getJdbcTemplate().query(sql);
		  }
		  public List<DataRow> getAllcuishouM1(){
				 String sql = "SELECT user_id FROM sdcms_user WHERE (roleid=21 or roleid=22 or roleid=23 or roleid=25 or roleid=50 ) AND fdqx<>0 ";
				 return getJdbcTemplate().query(sql);
			 }
			 public List<DataRow> getAllcuishouM2(){
				 String sql = "SELECT user_id FROM sdcms_user WHERE (roleid=24 or roleid=51 ) AND  fdqx<>0  ";
				 return getJdbcTemplate().query(sql);
			 }
			 public List<DataRow> getAllcuishouM3(){
				 String sql = "SELECT user_id FROM sdcms_user WHERE (roleid=26 or roleid=54 or roleid=60 or roleid=61) AND  fdqx<>0  ";
				 return getJdbcTemplate().query(sql);
			 }
	 public void updateCuiBG(DataRow row){
		 
		 getJdbcTemplate().update("sd_accountuserhk", row, "id", row.getString("id"));
	 }
	 public void insertCuiBG(DataRow row)
	 	{
	 		getJdbcTemplate().insert("sd_accountuserhk", row);
	 	}		 
	 public DataRow getCuishouBG(int csid,String time){
		 String sql = "select * from sd_accountuserhk where csid= "+csid+" and time='"+time+"'";
		 return getJdbcTemplate().queryMap(sql);
	 }		 
	 public String getSJSHJE(String jkid){
		 String sql = "select sjsh_money from sd_new_jkyx where id= "+jkid;
		 return getJdbcTemplate().queryString(sql);
	 }
	 public String getYQLX(String  jkid){
		 String sql ="select yuq_lx from sd_new_jkyx where  id ="+jkid ;
		 return getJdbcTemplate().queryString(sql);
	 }
	 public Integer getCuishouBGState(int csid){
		 String sql = "select state from sdcms_user where user_id="+csid;
		 return getJdbcTemplate().queryInt(sql);
	 }
	 public Integer getCuishouM0(String jkid){
		 String sql = "select cuishou_m0 from sd_new_jkyx where id="+jkid;
		 return getJdbcTemplate().queryInt(sql);
	 }
	 public Integer getCuishouBG(String jkid){
		  String sql = "select cuishou_m1 from sd_new_jkyx where id="+jkid;
		  return getJdbcTemplate().queryInt(sql);
	 }
	 
	
	 
	 public Integer getCuishou(String jkid){
		 String sql = "select cuishou_id from sd_new_jkyx where id="+jkid;
		 return getJdbcTemplate().queryInt(sql);
	 }
	 
	 /**20190703 lin
	  * 查询未还金额    sd_new_jkyx
	  * @param userid
	  * @param startTime
	  * @param endtTime
	  * @param MonDayType
	  * @return
	  */
	 public double getCSTJycsjinewhw(String cuishouM,int userid ) {
		 String sql ="SELECT SUM(REPLACE(sjsh_money, ',' ,'')+REPLACE(yuq_lx, ',' ,'')) FROM sd_new_jkyx WHERE yuq_ts >0  AND sfyhw =0 ";
		 
		 if (userid>0) {
			 if (cuishouM.equals("m1")) {
				sql+="AND  yuq_ts > 0 AND  yuq_ts <= 15 and cuishou_m1 ="+ userid;
			}else if (cuishouM.equals("m2")) {
				sql+="AND  yuq_ts > 15 AND  yuq_ts <= 45 and cuishou_m2 ="+ userid;
			}else if (cuishouM.equals("m3")) {
				sql+="AND  yuq_ts >45 and cuishou_m3 ="+ userid;
			}else {
				sql+=" and  cuishou_id= "+ userid;
			}
		}
	
		 return this.getJdbcTemplate().queryLong(sql);
	 }
	 
	 /**20190703 lin
	  * 当前已催回金额  sd_new_jkyx  sd_recharge_detail
	  * @param cuishouM
	  * @param userid
	  * @param curTime
	  * @return
	  */
	 public double getCSTJychjine(int userid ,String curTime) {
		 
		 String sql =" SELECT SUM(REPLACE(A.sjsh_money, ',' ,'')+REPLACE(A.yuq_lx, ',' ,'')+REPLACE(A.sjsh_money, '.' ,'')+REPLACE(A.yuq_lx, '.' ,'')) ";
		        sql+=" FROM sd_new_jkyx A, sd_recharge_detail B WHERE (B.remark='全额还款' OR B.remark='完成还款，部分逾期利息还款')  and  B.dqyqts > 0 and A.id=B.rechargeNumber  ";
		        sql+=" and B.cuishouid ="+ userid ;
		        sql +=" and  CONCAT(SUBSTRING(B.rechargetime,7,4),'-',SUBSTRING(B.rechargetime,4,2),'-',SUBSTRING(B.rechargetime,1,2)) = '"+ curTime+"'";
		 return this.getJdbcTemplate().queryLong(sql);
	 }
	 
	 /****************************************************2019-07-03**催收统计      Start********************************************************* ***/
		
	 /**
	  * 当前已催回金额  sd_new_jkyx  sd_recharge_detail
	  * @param cuishouM
	  * @param userid
	  * @param startTime
	  * @param endtTime
	  * @param MonDayType
	  * @return
	  */
	 public List<DataRow> getwcuihuijkDataRows(String cuishouM,int userid ) {
		 
		 String sql ="SELECT * FROM sd_new_jkyx WHERE yuq_ts >0  AND sfyhw =0 ";
		 
		 if (userid>0) {
			 if (cuishouM.equals("m4")) {
					sql+="AND  yuq_ts > 0 AND  yuq_ts <= 3 and cuishou_id ="+ userid;
			}else if (cuishouM.equals("m1")) {
				sql+="AND  yuq_ts > 3 AND  yuq_ts <= 7 and cuishou_id ="+ userid;
			}else if (cuishouM.equals("m2")) {
				sql+="AND  yuq_ts > 15 AND  yuq_ts <= 45 and cuishou_id ="+ userid;
			}else if (cuishouM.equals("m3")) {
				sql+="AND  yuq_ts >45 and cuishou_id ="+ userid;
			}else {
				sql+=" and  cuishou_id= "+ userid;
			}
		}
		
	    return getJdbcTemplate().query(sql);
	 }
	 /**
	  * sd_recharge_detail 统计本月已催收单数
	  * @param userid
	  * @param startTime
	  * @param endtTime
	  * @param MonDayType
	  * @return
	  */
	public List<DataRow>  getycuihuidanDataRows(int userid ,String startTime,String endtTime,int MonDayType) {
		String sql =" SELECT A.* ";
        sql+=" FROM sd_new_jkyx A, sd_recharge_detail B WHERE (B.remark='全额还款' OR B.remark='完成还款，部分逾期利息还款')  and  B.dqyqts > 0 and A.id=B.rechargeNumber  ";
        sql+=" and B.cuishouid ="+ userid ;
        
        if (!StringHelper.isEmpty(startTime)) { //界面有时间选择
			sql +=" and CONCAT(SUBSTRING(B.rechargetime,7,4),'-',SUBSTRING(B.rechargetime,4,2),'-',SUBSTRING(B.rechargetime,1,2))>='"+ startTime+"'  and  CONCAT(SUBSTRING(B.rechargetime,7,4),'-',SUBSTRING(B.rechargetime,4,2),'-',SUBSTRING(B.rechargetime,1,2)) <= '" +endtTime+"' ";
		}else if (!StringHelper.isEmpty(endtTime)) {
			if (1 ==MonDayType) {
				sql +=" and  CONCAT(SUBSTRING(B.rechargetime,7,4),'-',SUBSTRING(B.rechargetime,4,2),'-',SUBSTRING(B.rechargetime,1,2)) = '"+ endtTime+"'";
			} else if (2==MonDayType) {
				sql +=" and  CONCAT(SUBSTRING(B.rechargetime,7,4),'-',SUBSTRING(B.rechargetime,4,2))  =SUBSTRING('"+ endtTime+"',1,7) ";
			}
		}
        
      
		 return getJdbcTemplate().query(sql);
	}
	
	public List<DataRow> getfendanDataRows(int cuishou_id,int jkid,int cuishouz ){
		String sql =" SELECT * FROM sd_cuishou_fendan WHERE  cuishou_id = "+cuishou_id   +" AND jk_id = " + jkid +" AND cuishou_z= " +cuishouz;
		return getJdbcTemplate().query(sql);
	}
	
	public void updatefendandata(DataRow row){
		 
		 getJdbcTemplate().update("sd_cuishou_fendan", row, "id", row.getString("id"));
	 }
	 
	public List<DataRow>   getcuishoufendanid(int jkid,String curtime,int cuishou_z) {

		String sql = " SELECT id ,fendan_cs ,cuishou_jine ,cuishou_id,cuishou_z FROM sd_cuishou_fendan WHERE id IN  ( SELECT MAX(a.id) AS id  FROM sd_cuishou_fendan a WHERE  a.jk_id="+jkid+" )";
		if(!curtime.isEmpty()) {
	    	  sql +="  and SUBSTRING(fendan_time,1,7)='"+curtime+"'";
	       }
		sql +="  and cuishou_z="+cuishou_z;
		 return getJdbcTemplate().query(sql);
	 }
	
	 public double  getrechargeMoneyAccount(String jkid,String cuishouid) {
		    String sql =" SELECT SUM(REPLACE(rechargemoney,',','')) AS rechargemoney FROM sd_recharge_detail  WHERE  dqyqts >0 ";
		       sql += " AND rechargeNumber="+jkid+" AND cuishouid ="+cuishouid;
		      // sql+= " AND CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2)) >='"+starttime+"'";
		     //  sql+= " AND CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2)) <='"+endTime +"'";
			 
			 return this.getJdbcTemplate().queryLong(sql);
	 }
	
	 public double getfendancuihuijine(String jkid,String cuishouid) {
		 String sql =" SELECT SUM(recharge_money) FROM sd_cuishou_fendan WHERE ";
	        sql+=" jk_id = "+jkid +" AND cuishou_id ="+ cuishouid;
		 
		 return this.getJdbcTemplate().queryLong(sql);
	 }
	 public double getfendancuihuijine(int jkid,int cuishouid,String curtime) {
		 String sql =" SELECT SUM(recharge_money) FROM sd_cuishou_fendan WHERE ";
		        sql+=" jk_id = "+jkid +" AND cuishou_id ="+ cuishouid;
		 
		        if(!curtime.isEmpty()) {
			    	  sql +=" and SUBSTRING(fendan_time,1,7)='"+curtime+"'";
			       }
		 return this.getJdbcTemplate().queryLong(sql);
	 }
	 
	 public double  getrechargeMoneyAccount(int jkid,int cuishouid ,String curtime) {
		    String sql =" SELECT SUM(REPLACE(rechargemoney,',','')) AS rechargemoney FROM sd_recharge_detail  WHERE  dqyqts >0 ";
			       sql += " AND rechargeNumber="+jkid+" AND cuishouid ="+cuishouid;
			       
			       if(!curtime.isEmpty()) {
			    	  sql +=" and SUBSTRING(rechargetime,4,7)='"+curtime+"'";
			       }
			      // sql+= " AND CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2)) >='"+starttime+"'";
			     //  sql+= " AND CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2)) <='"+endTime +"'";
			 
			 return this.getJdbcTemplate().queryLong(sql);
		 }
	 
/***********************************************************2019-07-03**催收统计      end********************************************************* ***/	 
/**********************************a催收单核对 方法  start *********************************************************/
	 /**
	  * 查询还款表中的催收人员 名单、已催回金额
	  * @param curtime
	  * @return
	  */
	 public List<DataRow>  getrecuishouid() {
		 String sql =" SELECT user_id FROM sdcms_user WHERE";
		        sql+= "   (roleid=21 or roleid=22 or roleid=23 or roleid=25 or roleid=50 or roleid=24 or roleid=51 or roleid=26 or roleid=54) and state=1 ";
		        
		   
		 return getJdbcTemplate().query(sql);
	 }
	 public double getchargemoney(int cuishouid,String curtime ) {
		 String sql =" select  cuishouid ,SUM(REPLACE(rechargeMoney,',','')) as rechargeMoney  ";
		 sql+=" FROM sd_recharge_detail  WHERE dqyqts >0 AND CONCAT(SUBSTRING(rechargetime,7,4),'-',SUBSTRING(rechargetime,4,2),'-',SUBSTRING(rechargetime,1,2))  ="+curtime;
		 sql+= " and  cuishouid = "+ cuishouid;
		 return this.getJdbcTemplate().queryLong(sql);
	 }
	 
	 /**
	  * 查询 sd_accountuserhk 中的数据
	  * @param cuishouid
	  * @param curtime
	  * @return
	  */
	 public List<DataRow>  getaccountuserhk(int cuishouid,String curtime ){
		 String sql ="   SELECT * FROM sd_accountuserhk WHERE csid= "+cuishouid +"  and time = '"+ curtime+"'";
		
		 return getJdbcTemplate().query(sql);
	 }
	 
	 /**
	  *查询每天的应催收金额  cuishou_jine
	  * @param cuishouid
	  * @param curtime
	  * @return
	  */
	 public double geteverydatarucuijine(int cuishouid,String curtime )
	 {
		 String sql = " SELECT  SUM(cuishou_jine) AS cuishou_jine  FROM sd_cuishou_fendan  WHERE fendan_cs =1 ";
		        sql +=" AND cuishou_id ="+cuishouid +" AND fendan_time =" +curtime;
		  
		 return getJdbcTemplate().queryLong(sql);
	 }
	 
	 public void insertaccounthkrecord(DataRow row)
	 	{
	 		getJdbcTemplate().insert("sd_accounthk_change_record", row);
	 	}
	 
/**********************************a催收单核对 方法  end  *********************************************************/
	 
	 public DataRow getjkNumLast(String userid){
			String sql = "SELECT yuq_ts FROM sd_new_jkyx WHERE sfyfk=1 AND sfyhw =1 AND userid="+userid+" order by id desc ";
			return getJdbcTemplate().queryMap(sql);		
		}

	  public List<DataRow> getAuditorsshzz(){
	       String sql = "select user_id,name from sdcms_user u where u.roleId = 17 and u.state = 1 and u.fdqx<>0 ";
	       return getJdbcTemplate().query(sql);
	   }
	  public List<DataRow> getAuditors(){
	       String sql = "select user_id,name from sdcms_user u where u.roleId = 2 and u.state = 1 and u.fdqx<>0  ";
	       return getJdbcTemplate().query(sql);
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
		public Integer getOLDfdqxM0(int userid) {
			
			String sql = "select fdqx from sdcms_user where (roleid=19 or roleid=20) and user_id =" + userid;
			return getJdbcTemplate().queryInt(sql);
		}
		public Integer getOLDfdqx(int userid) {
			
			String sql = "select fdqx from sdcms_user where (roleid=21 or roleid=22 or roleid=23 or roleid=25 or roleid=50 ) and user_id =" + userid;
			return getJdbcTemplate().queryInt(sql);
		}
		

		 
/***************************************** 修改分单*******************/
		 public List<DataRow>   getjkinfoList(int cuishouid ) {

				String sql = "SELECT id,userid ,yuq_lx,sjsh_money,cuishou_id ,hkfq_time,yuq_ts "
						+ " FROM sd_new_jkyx WHERE  sfyhw = 0 AND sfyfk = 1 and yuq_ts > 0 ";
				
				sql+=" and  cuishou_id= "+ cuishouid;	
				sql +=" GROUP BY id";

				logger.info("getjkinfoList: "+sql);
				 return getJdbcTemplate().query(sql);
			 }
		 public List<DataRow>   getrechargemoneyinfoList(int cuishouid,String curMonth ) {
			 String sql = "SELECT userid,rechargeNumber,cuishouid,dqyqts, rechargeMoney ,rechargeTime FROM  sd_recharge_detail WHERE remark<>'延期还款'   AND dqyqts> 0 ";		
				sql+=" and  cuishouid= "+ cuishouid;
				sql+=" AND SUBSTRING(rechargetime,4,7)= '"+ curMonth+"'";

				logger.info("getrechargemoneyinfoList: "+sql);
			 return getJdbcTemplate().query(sql);
		 }
		 
		 public void insertcuishoufendan(DataRow row)
		 	{
		 		getJdbcTemplate().insert("sd_cuishou_fendan", row);
		 	}

		 
/********************************r2019年10月25日  催收分单**********************************/
		 /**
		  * 获取不分单名单
		  * @return
		  */
		 public String getcuishou_NoNeedfendan() {
			 String sql =" SELECT guizebianliang1 FROM sd_pingjiguize WHERE id=29";
			 return getJdbcTemplate().queryString(sql);
		 }
		 
		 /**
		  * 获取只分50%分单名单
		  * @return
		  */
		 public String getcuishou_50Needfendan() {
			 String sql =" SELECT guizebianliang1 FROM sd_pingjiguize WHERE id=30";
			 return getJdbcTemplate().queryString(sql);
		 }

}
