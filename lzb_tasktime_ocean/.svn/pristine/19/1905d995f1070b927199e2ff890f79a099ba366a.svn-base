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
public class AotuTHLXSHService extends BaseService
{

	private static Logger logger = Logger.getLogger(AotuTHLXSHService.class);
	
	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}
	
	/**
	 * 获取借款每日借款信息
	 */
	public List<DataRow> getAllZMSHList()
	{		
		String sql = "select j.id as jkid , j.userid ,p.contactPhone ,p.contactPhone02 from  sd_new_jkyx j left join sd_personjk p  on  p.userId =j.userid  where  cl_status=0    ";
		
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
     
    
     public Integer getLxThts(String userid ,String phone){
 		StringBuffer sb = new StringBuffer();
 		sb.append("select count(*) from sd_tonghuajl where userid ="+userid+" and peer_number='"+phone +"'"  );
 		System.out.println(sb.toString());
 		return getJdbcTemplate().queryInt(sb.toString());
 	}
}
