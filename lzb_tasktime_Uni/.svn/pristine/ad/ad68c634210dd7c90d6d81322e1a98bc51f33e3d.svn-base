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
public class AotuZMSHService extends BaseService
{

	private static Logger logger = Logger.getLogger(AotuZMSHService.class);
	
	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}
	
	/**
	 * 获取借款每日借款信息
	 */
	public List<DataRow> getAllZMSHList()
	{		
		String sql = "select id as jkid , userid from  sd_new_jkyx  where  cl_status=0  and userid in (select userid from sd_zmrz  where zm_score <= 580)  ";
		
		return getJdbcTemplate().query(sql);
	}
	
     public void updateUserJk(DataRow row) throws Exception {
		
		getJdbcTemplate().update("sd_new_jkyx", row, "id", row.getString("id"));
	}
     public void insertUserMsg(DataRow row)
	  	{
	  		getJdbcTemplate().insert("sd_msg", row);
	  	}
    
     
     public void  updateUserInfo(DataRow row) throws Exception {
 		
 		getJdbcTemplate().update("sd_user", row, "id", row.getString("id"));
 	}
}
