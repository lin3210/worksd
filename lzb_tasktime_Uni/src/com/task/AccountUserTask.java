package com.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.service.AccountUserService;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.timerengine.Task;

public class AccountUserTask implements Task{

	private static Logger logger = Logger.getLogger(AccountUserTask.class);
	private static AccountUserService accountUserService=new AccountUserService();
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf =new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat sdfday =new SimpleDateFormat("yyyy-MM-dd");
		String time = sdf.format(new Date());
		String daytime = sdfday.format(new Date());
		Calendar cd = Calendar.getInstance();
		try {
			try {
				cd.setTime(sdfday.parse(daytime));
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date datenow = cd.getTime();
		String nowdate = sdfday.format(datenow); // 前面日期 
		cd.add(Calendar.DATE, 1);// // 减一天
		Date date = cd.getTime();
		String dateno = sdfday.format(date); // 后面日期
		cd.add(Calendar.DATE, -15);
		Date date1 = cd.getTime();
		String dateno1 = sdfday.format(date1); // 后面日期
		int yizhuce = accountUserService.getCreateTimeByDate(nowdate, dateno);
		int yirenzhengGZ = accountUserService.getGZCreateTimeByDate(nowdate,dateno);
		int sqjk = accountUserService.getShenqingJK(nowdate,dateno);
		int sqjkpf3 = accountUserService.getShenqingJKPF3(nowdate,dateno);
		int sqjkpf2 = accountUserService.getShenqingJKPF2(nowdate,dateno);
		int sqjkpf1 = accountUserService.getShenqingJKPF1(nowdate,dateno);
		int sqjkpff1 = accountUserService.getShenqingJKPFf1(nowdate,dateno);
		int sqjkpff2 = accountUserService.getShenqingJKPFf2(nowdate,dateno);
		int zsqjk = accountUserService.getZShenqingJK(nowdate,dateno);
		int lyhsqjk = accountUserService.getOLDCreateTimeByDate(nowdate,dateno);
//		int lyhsqjk = 0;
//		List<DataRow> userlist = accountUserService.getShenqingJKLYH(nowdate,dateno1 ,dateno);
//		for (DataRow dataRow : userlist) {
//			DataRow row = accountUserService.getLYHJK(dataRow.getInt("userid"));
//			if(row !=null){
//				lyhsqjk ++ ;
//			}
//		}
		int fkcg = accountUserService.getShenqingJKCG(nowdate,dateno);
		int yirenzheng = yirenzhengGZ;
		int bi = 0;
		if (yirenzheng != 0 && yizhuce != 0) {
			bi = (yirenzheng * 100 / yizhuce) ;
		} else if (yirenzheng != 0 && yizhuce == 0) {
			bi = 1;
		}
		DataRow dr = new DataRow();
		dr.set("createcount", yizhuce);
		dr.set("viprzcount", yirenzheng);
		dr.set("rzdata", bi);
		dr.set("sqjk", sqjk);
		dr.set("sqjkpf3", sqjkpf3);
		dr.set("sqjkpf2", sqjkpf2);
		dr.set("sqjkpf1", sqjkpf1);
		dr.set("sqjkpff1", sqjkpff1);
		dr.set("sqjkpff2", sqjkpff2);
		dr.set("zsqjk", zsqjk);
		dr.set("lyhsqjk", lyhsqjk);
		dr.set("fkcg", fkcg);
		dr.set("time", time);
		dr.set("daytime", daytime);
		if (accountUserService.deleteAcountByTime(time)) {
			accountUserService.addAcountUser("sd_accountuser", dr);
		}
	}
	
}
