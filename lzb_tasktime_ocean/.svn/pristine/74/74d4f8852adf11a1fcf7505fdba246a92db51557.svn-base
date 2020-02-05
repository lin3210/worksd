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
public class AotuTHTSSHService extends BaseService
{

	private static Logger logger = Logger.getLogger(AotuTHTSSHService.class);
	
	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}
	
	/**
	 * 查询出（初始待审核人通话记录是否大于800条 ）
	 */
	public List<DataRow> getTHTSSHList()
	{		
		String sql = "select id as jkid , userid from  sd_new_jkyx  where  cl_status=0  ";		
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
     
     public Integer getTonghuats(String userId){
 		StringBuffer sb = new StringBuffer();
 		sb.append("select count(1) from sd_tonghuajl where  userid = ");
 		sb.append(userId);
 		return getJdbcTemplate().queryInt(sb.toString());
 	}
}
