package com.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.service.BaseService;

public class JoinJsService extends BaseService{
	private static Logger logger = Logger.getLogger(JoinJsService.class);
	
	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}

	public List<DataRow> findJoin() {
		String sql = " select * from t_wx_group where status = 0 and groupid > 0 and userid <> 8294 ";
		return getJdbcTemplate().query(sql);
	}

	public List<DataRow> findJoinList(int groupid) {
		String sql = " select * from t_wx_group_list where groupid = " + groupid;
		return getJdbcTemplate().query(sql);
	}

	public List<DataRow> finduserlist(String wxid) {
		String sql = " select id from t_user where wxid = '"+wxid+"' ";
		return getJdbcTemplate().query(sql);
	}

	public String getDCSum(String uid) {
		String sql = " select IFNULL(SUM(investAmount),0)  from t_current_invest a where repayStatus = 1 and investor = " + uid;
		return getJdbcTemplate().queryString(sql);
	}

	public String getHQSum(String uid) {
		String sql = "select IFNULL(SUM(money),0) from t_current_scsq_user where userid = " + uid;
		return getJdbcTemplate().queryString(sql);
	}

	public DataRow findUserById(int userid) {
		String sql = "select * from t_user where id = " + userid;
		return getJdbcTemplate().queryMap(sql);
	}
	
	public void updateUser(DataRow data) {
		getJdbcTemplate().update("t_user", data, "id", data.getString("id"));
	}

	public void insertzhcz(DataRow row) {
		getJdbcTemplate().insert("t_zhcz", row);
	}

	public void insertyue(DataRow data) {
		getJdbcTemplate().insert("t_jmyue", data);
	}
}
