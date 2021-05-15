package com.task;

import java.util.Date;
import java.util.List;

import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.util.DateHelper;

public class Test2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub 20150623   0513   0613  0713  0813 0913
		JdbcTemplate j = new JdbcTemplate();
		String sql = " select id,deadline,hasdeadline,DATE_FORMAT(investtime,'%Y%m%d')as time from t_current_invest where isAutoBid = 0 and flag = 0 and currentid <> 2 ";
		List<DataRow> list = j.query(sql);
		System.out.println(list.size());
		String now = DateHelper.formatDate(new Date(),"yyyyMMdd");
		int nowyue = Integer.parseInt(now.substring(4, 6));
		int nowri = Integer.parseInt(now.substring(6));
		if(list.size() > 0){
			for (DataRow dataRow : list) {
				String time = dataRow.getString("time");
				int yue = Integer.parseInt(time.substring(4, 6));
				int ri = Integer.parseInt(time.substring(6));
				DataRow row = new DataRow();
				row.set("id", dataRow.getInt("id"));
				if(ri <= nowri){
					int a = nowyue - yue;
					row.set("hasdeadline", 1+a);
					 
				}else{
					int b = nowyue - yue - 1;
					row.set("hasdeadline", 1+b);
				}
				j.update("t_current_invest", row, "id", row.getInt("id"));
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("½áÊø");
		}
	}

}
