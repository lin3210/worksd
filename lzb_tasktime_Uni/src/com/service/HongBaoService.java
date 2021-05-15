package com.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.service.BaseService;

public class HongBaoService extends BaseService{
	private static Logger logger = Logger.getLogger(HongBaoService.class);
	
	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}

	public List<DataRow> getUserList(String curdate) {
		String sql = "select id,usablesum from t_user where hongb = 1 and tzstatus = 0 AND DATE_FORMAT(createTime,'%Y%m%d') < '"+curdate+"'";
		return getJdbcTemplate().query(sql);
	}

	public List<DataRow> getDC(int userid) {
		String sql = "select * from t_current_invest where investor = " + userid;
		return getJdbcTemplate().query(sql);
	}

	public List<DataRow> getHQ(int userid) {
		String sql = "select * from t_current_scsq_list where userid = " + userid;
		return getJdbcTemplate().query(sql);
	}

	public void updateUser(DataRow row) {
		getJdbcTemplate().update("t_user", row, "id", row.getString("id"));
	}

	public DataRow getHb(String id) {
		String sql = "select* from t_zhcz where type = 1 and userid = " + id;
		return getJdbcTemplate().queryMap(sql);
	}

	public void delete(DataRow data) {
		getJdbcTemplate().delete("t_zhcz", "id", data.getString("id"));
	}

	public void insert(DataRow row) {
		getJdbcTemplate().insert("t_zhcz", row);
	}
	
	public List<DataRow> getUserJKList() {
		String sql = "SELECT * FROM sd_new_jkyx WHERE sfyfk=2";
		return getJdbcTemplate().query(sql);
	}
	public void updateUserJK(DataRow row) {
		getJdbcTemplate().update("sd_new_jkyx", row, "id", row.getString("id"));
	}
}
