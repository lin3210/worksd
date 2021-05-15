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
public class AotuZDQRService extends BaseService
{

	private static Logger logger = Logger.getLogger(AotuZDQRService.class);
	
	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}
	
	/**
	 * 获取需要自动提现
	 */
	public List<DataRow> getZDQRList()
	{		
		String sql = " select * from sd_withdraw where remark ='投资还本' and suborder is not null   ";
		return getJdbcTemplate().query(sql);
	}
}
