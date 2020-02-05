package com.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.service.BaseService;
import com.thinkive.base.jdbc.DataRow;

public class SelectUserAndBankService  extends BaseService{
	
private static Logger logger = Logger.getLogger(DealmakingService.class);
	
	public JdbcTemplate getJdbcTemplate() {
		
		return getJdbcTemplate("web");
	}

	public DataRow getUserone(String project) {
		
		String sql = "select username,createtime from  sd_user  WHERE SUBSTRING(username,1,4) ='"+project+"'  AND username like '%AND%'   ORDER BY id DESC LIMIT 1 ";	
		
		return getJdbcTemplate().queryMap(sql);
	}
	
	public DataRow getBankone() {
		
		String sql = "  select create_time,userId from  sd_bankcard WHERE napasbankno<>0  ORDER BY id DESC LIMIT 1 ";	
		
		return getJdbcTemplate().queryMap(sql);
	}
	
	/**************Start every day send msg***************************************/	
	/**
	 * 查询电话号码
	 * @return
	 */
	public List<DataRow> getnewPhone(String tablename,int startnum,int sendnum){
		String sql ="SELECT distinct phone FROM "+tablename+" WHERE 1=1 LIMIT "+startnum+","+sendnum;
		
		return getJdbcTemplate().query(sql);
	}
	
	/**
	 * 更新电话
	 * @param row
	 */
	public void updanewPhone(DataRow row) {
		getJdbcTemplate().update("sduser_new_phone", row, "phone", row.getString("phone"));
	}
	
	/**
	 * 查询电话是否存在
	 * @param phone
	 * @return
	 */
	public int getuserphone(String phone) {
		String sql =" SELECT count(*) FROM sd_user  WHERE  mobilephone ='"+phone+"'";
		
		return getJdbcTemplate().queryInt(sql);
	}
	
	/**
	 * 数据插入方法
	 * @param tablename
	 * @param row
	 */
	public void insertTableData(String tablename,DataRow row) {
		
		getJdbcTemplate().insert(tablename, row);
	}
	
	/**
	 * 更新电话记录
	 * @param row
	 */
	public void updateTableData(DataRow row) {
		getJdbcTemplate().update("sduser_new_phone_record", row, "id", row.getString("id"));
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
	
	 public DataRow getnewmsg_function(String functionname) {
			
			String sql = "  SELECT * FROM sdcms_function_control  WHERE  function_name ='"+functionname+"'";	
			
			return getJdbcTemplate().queryMap(sql);
		}
	    
	    /**
		 * 查询电话条数
		 * @param phone
		 * @return
		 */
		public int getuserphonenconut(String table) {
			String sql =" SELECT count(*) FROM "+ table;
			
			return getJdbcTemplate().queryInt(sql);
		}
		
		public void updatafunction(String tablename,DataRow row) {
			getJdbcTemplate().update(tablename, row, "id", row.getString("id"));
		}
		
		/**
		 * 查询电话是否存在借款
		 * @param phone
		 * @return
		 */
		public int getuserphoneJK(String phone) {
			String sql =" SELECT COUNT(*) FROM sd_new_jkyx WHERE sfyfk=1 AND sfyhw=0 AND userid = (SELECT id FROM sd_user WHERE mobilephone = '"+phone+"')" ;
			
			return getJdbcTemplate().queryInt(sql);
		}
		
		public String  getuserphone_idno(String phone) {
			String sql =" SELECT idno FROM sd_user_finance WHERE userid = ( SELECT id FROM sd_user WHERE mobilePhone ='"+phone+"')";
			
			return getJdbcTemplate().queryString(sql);
		}
/**************end every day send msg***************************************/	
}
