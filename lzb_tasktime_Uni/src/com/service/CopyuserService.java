package com.service;


import java.util.List;

import org.apache.log4j.Logger;

import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.jdbc.session.Session;
import com.thinkive.base.service.BaseService;

public class CopyuserService extends BaseService{
	private static Logger logger = Logger.getLogger(CopyuserService.class);
	
	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}
	
	public Integer getPage(){
		String sql ="select id,copyid from sd_jishu where id=1";
		return getJdbcTemplate().queryInt(sql);
	}
	 public void addPhone(DataRow data)
		{
			getJdbcTemplate().insert(" sd_phonecopy", data);
		}
	 public void updatePage(DataRow data)
		{
			getJdbcTemplate().update("sd_jishu", data, "id", data.getString("id"));
		}
}
