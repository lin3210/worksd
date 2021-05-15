package com.service;

import java.util.List;

import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.service.BaseService;

public class DcStatusService extends BaseService{
	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}

	public List<DataRow> getjiamenglist() {//只统计尚未发放的加盟用户
		String sql = " select id,dcstatus from t_user where wxid in (select wxid from t_wx_group_list) and dcstatus = 0 and jlstatus = 0 ";
		return getJdbcTemplate().query(sql);
	}
	public double getdcsum(String userid) {
		String sql = " select IFNULL(SUM(investAmount) ,0)from t_current_invest where investor = " + userid;
		return Double.parseDouble(getJdbcTemplate().queryString(sql));
	}
}
