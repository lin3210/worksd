package root.current;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import net.sf.json.JSONObject;

import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;

import com.project.service.account.AccountManService;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.util.SessionHelper;
import com.thinkive.web.base.ActionResult;
import com.thinkive.web.base.BaseAction;

//不用提交
public class AccountManAction extends BaseAction {
	private static Logger logger = Logger.getLogger(AccountManAction.class);
	private static AccountManService accountManService = new AccountManService();

	/**
	 * 
	 * 查询平台用户记录
	 * 
	 * @return
	 * @throws java.text.ParseException
	 */
	public ActionResult doGetAllChart() throws java.text.ParseException {
	   int userid = SessionHelper.getInt("cmsuserid", getSession());
	   if(userid == 8||userid==6||userid==888||userid==222 || userid==4002 || userid==8888 || userid==9999  || userid==135699 ){	  
		logger.info("统计平台用户");
		// 1.得到当前日期
		String data = "{";
		String yrzdata = "\"yrzdata\":["; // 已认证 
		String yzcdata = "\"yzcdata\":["; // 已注册 
		String ybldata = "\"ybldata\":[";// 认证比例
		String xdata = "\"xdata\":["; // 日期
		String lyhdata = "\"lyhdata\":["; // 老用户提交 
		String sqjkdata = "\"sqjkdata\":["; // 申请借款
		String zsqjkdata = "\"zsqjkdata\":["; // 申请借款
		String fkcgdata = "\"fkcgdata\":["; // 成功借款
		SimpleDateFormat sdf =new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat sdfday =new SimpleDateFormat("yyyy-MM-dd");
		String time = sdf.format(new Date());
		String daytime = sdfday.format(new Date());
		Calendar cd = Calendar.getInstance();
		try {
			cd.setTime(sdfday.parse(daytime));
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
		int yizhuce = accountManService.getCreateTimeByDate(nowdate, dateno);
		int yirenzhengGZ = accountManService.getVipCreateTimeByDateGZ(nowdate,dateno);
		int lyhsqjk = accountManService.getOLDCreateTimeByDate(nowdate,dateno);
		/*
		 * List<DataRow> userlist = accountManService.getShenqingJKLYH(nowdate,dateno1
		 * ,dateno); for (DataRow dataRow : userlist) { DataRow row =
		 * accountManService.getLYHJK(dataRow.getInt("userid")); if(row !=null){ lyhsqjk
		 * ++ ; } }
		 */
		int sqjk = accountManService.getShenqingJK(nowdate,dateno);
		int sqjkpf3 = accountManService.getShenqingJKPF3(nowdate,dateno);
		int sqjkpf2 = accountManService.getShenqingJKPF2(nowdate,dateno);
		int sqjkpf1 = accountManService.getShenqingJKPF1(nowdate,dateno);
		int sqjkpff1 = accountManService.getShenqingJKPFf1(nowdate,dateno);
		int sqjkpff2 = accountManService.getShenqingJKPFf2(nowdate,dateno);
		int zsqjk = accountManService.getZShenqingJK(nowdate,dateno);
		int fkcg = accountManService.getShenqingJKCG(nowdate,dateno);
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
		if (accountManService.deleteAcountByTime(time)) {
			accountManService.addAcountUser("sd_accountuser", dr);
		}
		List<DataRow> list = accountManService.getAllAcountUser();
		for (int i = list.size(); i > 0; i--) {
			if (i == 1) {
				yzcdata += "\"" + list.get(i - 1).getInt("createcount") + "\"";
				yrzdata += "\"" + list.get(i - 1).getInt("viprzcount") + "\"";
				ybldata += list.get(i - 1).getInt("rzdata");
				if(userid==4002){
					lyhdata += "\"0\"";
					sqjkdata += "\"0\"";
					zsqjkdata += "\"0\"";
					fkcgdata += "\"0\"";
				}else{
					lyhdata += "\"" + list.get(i - 1).getInt("lyhsqjk") + "\"";
					sqjkdata += "\"" + list.get(i - 1).getInt("sqjk") + "\"";
					zsqjkdata += "\"" + list.get(i - 1).getInt("zsqjk") + "\"";
					fkcgdata += "\"" + list.get(i - 1).getInt("fkcg") + "\"";
				}
				
				xdata += "\"" + list.get(i - 1).getString("time") + "\"";
			} else if(i >= 7 && i <= list.size()-15){
//				Random rand = new Random();
//				int aaa = rand.nextInt(20);
//				int bbb = aaa +300;
				int bbb = 0;
				yzcdata += "\"" + (list.get(i - 1).getInt("createcount") +bbb)+ "\",";
				yrzdata += "\"" + (list.get(i - 1).getInt("viprzcount") +bbb)+ "\",";
				ybldata += list.get(i - 1).getInt("rzdata") + ",";
				if(userid==4002){
					lyhdata += "\"0\",";
					sqjkdata += "\"0\",";
					zsqjkdata += "\"0\",";
					fkcgdata += "\"0\",";
				}else{
					lyhdata += "\"" + (list.get(i - 1).getInt("lyhsqjk") +bbb)+ "\",";
					sqjkdata += "\"" + (list.get(i - 1).getInt("sqjk") +bbb)+ "\",";
					zsqjkdata += "\"" + (list.get(i - 1).getInt("zsqjk") +bbb)+ "\",";
					fkcgdata += "\"" + (list.get(i - 1).getInt("fkcg") )+ "\",";
				}
				
				xdata += "\"" + list.get(i - 1).getString("time") + "\",";
			} else {
				yzcdata += "\"" + list.get(i - 1).getInt("createcount") + "\",";
				yrzdata += "\"" + list.get(i - 1).getInt("viprzcount") + "\",";
				ybldata += list.get(i - 1).getInt("rzdata") + ",";
				if(userid==4002){
					lyhdata += "\"0\",";
					sqjkdata += "\"0\",";
					zsqjkdata += "\"0\",";
					fkcgdata += "\"0\",";
				}else{
					lyhdata += "\"" + list.get(i - 1).getInt("lyhsqjk") + "\",";
					sqjkdata += "\"" + list.get(i - 1).getInt("sqjk") + "\",";
					zsqjkdata += "\"" + list.get(i - 1).getInt("zsqjk") + "\",";
					fkcgdata += "\"" + list.get(i - 1).getInt("fkcg") + "\",";
				}
				
				xdata += "\"" + list.get(i - 1).getString("time") + "\",";
			}
		}
		yrzdata += "]";
		yzcdata += "]";
		ybldata += "]";
		lyhdata += "]";
		sqjkdata += "]";
		zsqjkdata += "]";
		fkcgdata += "]";
		xdata += "]";
		data += yrzdata + "," + yzcdata + "," + ybldata + "," + xdata + "," + lyhdata + "," + zsqjkdata + "," + sqjkdata + "," + fkcgdata + "}";
		
		this.getWriter().write(data);
		return null;
	   }else {
		   return null;
	   }
	}
	/**
	 * 
	 * 查询平台用户记录
	 * 
	 * @return
	 * @throws java.text.ParseException
	 */
	public ActionResult doGetAllChartCS() throws java.text.ParseException {
		int userid = SessionHelper.getInt("cmsuserid", getSession());
		if(userid == 8||userid==6||userid==888||userid==222 || userid==4002 || userid==8888 || userid==9999  || userid==135699 ){	  
			logger.info("统计平台用户");
			// 1.得到当前日期
			String data = "{";
			String yrzdata = "\"yrzdata\":["; // 已认证 
			String xdata = "\"xdata\":["; // 日期
			SimpleDateFormat sdf =new SimpleDateFormat("dd-MM-yyyy");
			SimpleDateFormat sdfday =new SimpleDateFormat("yyyy-MM-dd");
			String daytime = sdfday.format(new Date());
			for (int i = 0; i < 30; i++) {
				Calendar cd = Calendar.getInstance();
				try {
					cd.setTime(sdfday.parse(daytime));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				cd.add(Calendar.DATE, i);// // 减一天
				Date datenow = cd.getTime();
				String nowdate = sdfday.format(datenow); // 前面日期 
				cd.add(Calendar.DATE, 1);// // 减一天
				Date date = cd.getTime();
				String dateno = sdfday.format(date); // 后面日期
				int yizhuce = accountManService.getCreateTimeByDateHKRS(nowdate, dateno);
				
				if (i == 29) {
					yrzdata += "\"" + yizhuce + "\"";
					xdata += "\"" + nowdate + "\"";
				} else {
					yrzdata += "\"" + yizhuce + "\",";
					xdata += "\"" + nowdate + "\",";
				}
			}
			yrzdata += "]";
			xdata += "]";
			data += yrzdata + "," + xdata + "}";
			
			this.getWriter().write(data);
			return null;
		}else {
			return null;
		}
	}
	public ActionResult doGetAllChartFK() throws java.text.ParseException {
		logger.info("统计平台用户");
		// 1.得到当前日期
		String data = "{";
		String yrzdata = "\"yrzdata\":["; // 已还款 
		String yzcdata = "\"yzcdata\":["; // 已逾期 
		String ybldata = "\"ybldata\":[";// 逾期比例
		String xdata = "\"xdata\":["; // 日期
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
		String time = sdf.format(new Date());
		Calendar cd = Calendar.getInstance();
		try {
			cd.setTime(sdf.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow); // 前面日期 
		cd.add(Calendar.DATE, 1);// // 加一天
		Date date = cd.getTime();
		String dateno = sdf.format(date); // 后面日期
		int yizhuce = accountManService.getCreateTimeByDateHK(nowdate, dateno);
		SimpleDateFormat sdfy =new SimpleDateFormat("yyyy-MM-dd");
		String timey = sdfy.format(new Date());
		Calendar cdy = Calendar.getInstance();
		try {
			cd.setTime(sdfy.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date datenowy = cd.getTime();
		String nowdatey = sdfy.format(datenowy); // 前面日期 
		cd.add(Calendar.DATE, -1);// // 减一天
		Date datey = cd.getTime();
		String datenoy = sdfy.format(datey); // 后面日期
		int yirenzheng = accountManService.getVipCreateTimeByDateYQ(datenoy,nowdatey);
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
		dr.set("time", nowdate);
		if (accountManService.deleteAcountByTimeHK(nowdate)) {
			accountManService.addAcountUser("sd_accountuserhk", dr);
		}
		List<DataRow> list = accountManService.getAllAcountUserHK();
		for (int i = list.size(); i > 0; i--) {
			if (i == 1) {
				yzcdata += "\"" + list.get(i - 1).getInt("createcount") + "\"";
				yrzdata += "\"" + list.get(i - 1).getInt("viprzcount") + "\"";
				ybldata += list.get(i - 1).getInt("rzdata");
				xdata += "\"" + list.get(i - 1).getString("time") + "\"";
			} else {
				yzcdata += "\"" + list.get(i - 1).getInt("createcount") + "\",";
				yrzdata += "\"" + list.get(i - 1).getInt("viprzcount") + "\",";
				ybldata += list.get(i - 1).getInt("rzdata") + ",";
				xdata += "\"" + list.get(i - 1).getString("time") + "\",";
			}
		}
		yrzdata += "]";
		yzcdata += "]";
		ybldata += "]";
		xdata += "]";
		data += yrzdata + "," + yzcdata + "," + ybldata + "," + xdata + "}";
		this.getWriter().write(data);
		return null;
	}
	public ActionResult doAddGetAllChart() throws java.text.ParseException {
		logger.info("增加统计平台用户");
		for (int i = 0; i < 365; i++) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time = sdf.format(new Date());
			Calendar cd = Calendar.getInstance();
			try {
				cd.setTime(sdf.parse(time));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			cd.add(Calendar.DATE, -i); // 减一天
			Date datenow = cd.getTime();
			String nowdate = sdf.format(datenow);
			cd.add(Calendar.DATE, -1); // 减一天
			Date date = cd.getTime();
			String dateno = sdf.format(date);
			int yizhuce = accountManService
					.getCreateTimeByDate(dateno, nowdate);

			int yirenzhengGZ = accountManService.getVipCreateTimeByDateGZ(nowdate,dateno);
			int yirenzhengXS = accountManService.getVipCreateTimeByDateXS(nowdate,dateno);
			int yirenzheng = yirenzhengGZ +yirenzhengXS;
			int bi = 0;
			if (yirenzheng != 0 && yizhuce != 0) {
				bi = (yirenzheng* 100 / yizhuce) ;
			} else if (yirenzheng != 0 && yizhuce == 0) {
				bi = 1;
			}
			DataRow dr = new DataRow();
			dr.set("createcount", yizhuce);
			dr.set("viprzcount", yirenzheng);
			dr.set("rzdata", bi);
			dr.set("time", dateno);
			System.out.println("已注册："+yizhuce+",已认证："+yirenzheng+"，比例："+bi+"，日期:"+dateno);
			accountManService.addAcountUser("sd_accountuser", dr);
		}
		return null;
	}
	public ActionResult doGetAllTJT() throws Exception{
		logger.info("进入统计列表");
		JSONObject jsonobject = new JSONObject();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time = sdf.format(new Date());
		int year = Integer.parseInt(time.substring(0, 4));
		int nancount = accountManService.getAllNanUser();
		int nvcount = accountManService.getAllNvUser();
		int zerocount = accountManService.getAllZeroUser(year);
		int onecount = accountManService.getAllOneUser(year);
		int twocount = accountManService.getAllTwoUser(year);
		int threecount = accountManService.getAllThreeUser(year);
		int fourcount = accountManService.getAllFourUser(year);
		int fivecount = accountManService.getAllFiveUser(year);
		int sixcount = accountManService.getAllSixUser(year);
		int sevencount = accountManService.getAllSevenUser(year);
		int xscount = accountManService.getAllStudentUser(year);
		int gzcount = accountManService.getAllWorkUser(year);
		int xinzionegzcount = accountManService.getAllXinzioneUser();
		int xinzitwogzcount = accountManService.getAllXinzitwoUser();
		int xinzithreegzcount = accountManService.getAllXinzithreeUser();
		int xinzifourgzcount = accountManService.getAllXinzifourUser();
		int xinzifivegzcount = accountManService.getAllXinzifiveUser();
		List<DataRow> listcompany = accountManService.getAllCompany();
		int company1 = 0;
		int company2 = 0;
		int company3 = 0;
		int company4 = 0;
		int company5 = 0;
		int company6 = 0;
		int company7 = 0;
		int company8 = 0;
		int company9 = 0;
		int company10 = 0;
		int company11 = 0;
		int company12 = 0;
		int company13 = 0;
		int company14 = 0;
		int company15 = 0;
		int company16 = 0;
		int company17 = 0;
		int company18 = 0;
		int company19 = 0;
		for (DataRow dataRow : listcompany) {
			if("Kinh doanh".equals(dataRow.getString("company"))){
				company1 ++ ;
			}
			else if("Kinh doanh".equals(dataRow.getString("company"))){
				company2 ++ ;
			}
			else if("Hành chính".equals(dataRow.getString("company"))){
				company2 ++ ;
			}
			else if("Bảo vệ".equals(dataRow.getString("company"))){
				company3 ++ ;
			}
			else if("Biên phiên dịch".equals(dataRow.getString("company"))){
				company4 ++ ;
			}
			else if("Dệt may/ Giày da".equals(dataRow.getString("company"))){
				company5 ++ ;
			}
			else if("Du lịch/ Khách sạn".equals(dataRow.getString("company"))){
				company6 ++ ;
			}
			else if("Thể hình/ Spa".equals(dataRow.getString("company"))){
				company7 ++ ;
			}
			else if("Mỹ phẩm/ Thời trang".equals(dataRow.getString("company"))){
				company8 ++ ;
			}
			else if("Nông nghiệp".equals(dataRow.getString("company"))){
				company9 ++ ;
			}
			else if("PG/ PB/ Lễ tân".equals(dataRow.getString("company"))){
				company10 ++ ;
			}
			else if("Phục vụ/ Tạp vụ".equals(dataRow.getString("company"))){
				company11 ++ ;
			}
			else if("Sản xuất".equals(dataRow.getString("company"))){
				company12 ++ ;
			}
			else if("Tài xế/ Giao nhận".equals(dataRow.getString("company"))){
				company13 ++ ;
			}
			else if("Bán thời gian".equals(dataRow.getString("company"))){
				company14 ++ ;
			}
			else if("Thực phẩm/ Dịch vụ ăn uống".equals(dataRow.getString("company"))){
				company15 ++ ;
			}
			else if("Tư vấn bảo hiểm".equals(dataRow.getString("company"))){
				company16 ++ ;
			}
			else if("Sinh viên/ Mới tốt nghiệp".equals(dataRow.getString("company"))){
				company17 ++ ;
			}
			else if("Khác".equals(dataRow.getString("company"))){
				company18 ++ ;
			}
			else if("Đang tìm việc".equals(dataRow.getString("company"))){
				company19 ++ ;
			}
		}
		List<DataRow> listtime = accountManService.getAllTime();
		int time1 = 0;
		int time2 = 0;
		int time3 = 0;
		int time4 = 0;
		int time5 = 0;
		SimpleDateFormat sdftime = new SimpleDateFormat("MM-yyyy");
		String timetime = sdftime.format(new Date());
		Date date1 = sdftime.parse(timetime);
		for (DataRow dataRow2 : listtime) {
			String timesj = dataRow2.getString("time");
			if(dataRow2.getString("time").length() == 6){
				timesj = "0" + dataRow2.getString("time");
			}else if(dataRow2.getString("time").length() == 10){
				timesj = dataRow2.getString("time").substring(3);
			}
			Date date2 = sdftime.parse(timesj);
			long l = date1.getTime() - date2.getTime(); 
			long day = l / (24 * 60 * 60 * 1000);  
			if(day <= 90){
				time1 ++ ;
			}
			else if(day >90 && day <= 180){
				time2 ++ ;
			}
			else if(day >180 && day <= 365){
				time3 ++ ;
			}
			else if(day >365 && day <= 1095){
				time4 ++ ;
			}
			else if(day >1095){
				time5 ++ ;
			}
		}
		List<DataRow> listsj = accountManService.getAllPhone();
		int shouji1 = 0;
		int shouji2 = 0;
		int shouji3 = 0;
		int shouji4 = 0;
		int shouji5 = 0;
		int shouji6 = 0;
		int shouji7 = 0;
		int shouji8 = 0;
		int shouji9 = 0;
		for (DataRow dataRow2 : listsj) {
			String phonetype = dataRow2.getString("phonetype").replace("&nbsp;", "").toLowerCase();
			if(phonetype.contains("samsung")){
				shouji1 ++ ;
			}
			else if(phonetype.contains("huawei")){
				shouji2 ++ ;
			}
			else if(phonetype.contains("vivo")){
				shouji3 ++ ;
			}
			else if(phonetype.contains("oppo")){
				shouji4 ++ ;
			}
			else if(phonetype.contains("sony")){
				shouji5 ++ ;
			}
			else if(phonetype.contains("xiaomi")){
				shouji6 ++ ;
			}
			else if(phonetype.contains("iphone")){
				shouji7 ++ ;
			}
			else if(TextUtils.isEmpty(phonetype) || !"none".equals(phonetype)){
				shouji8 ++ ;
			}
		}
		DataRow row = new DataRow();
		row.set("nancount", nancount);
		row.set("nvcount", nvcount);
		row.set("zerocount", zerocount);
		row.set("onecount", onecount);
		row.set("twocount", twocount);
		row.set("threecount", threecount);
		row.set("fourcount", fourcount);
		row.set("fivecount", fivecount);
		row.set("sixcount", sixcount);
		row.set("sevencount", sevencount);
		row.set("xscount", xscount);
		row.set("gzcount", gzcount);
		row.set("xinzionegzcount", xinzionegzcount);
		row.set("xinzitwogzcount", xinzitwogzcount);
		row.set("xinzithreegzcount", xinzithreegzcount);
		row.set("xinzifourgzcount", xinzifourgzcount);
		row.set("xinzifivegzcount", xinzifivegzcount);
		row.set("company1", company1);
		row.set("company2", company2);
		row.set("company3", company3);
		row.set("company4", company4);
		row.set("company5", company5);
		row.set("company6", company6);
		row.set("company7", company7);
		row.set("company8", company8);
		row.set("company9", company9);
		row.set("company10", company10);
		row.set("company11", company11);
		row.set("company12", company12);
		row.set("company13", company13);
		row.set("company14", company14);
		row.set("company15", company15);
		row.set("company16", company16);
		row.set("company17", company17);
		row.set("company18", company18);
		row.set("company19", company19);
		row.set("time1", time1);
		row.set("time2", time2);
		row.set("time3", time3);
		row.set("time4", time4);
		row.set("time5", time5);
		row.set("shouji1", shouji1);
		row.set("shouji2", shouji2);
		row.set("shouji3", shouji3);
		row.set("shouji4", shouji4);
		row.set("shouji5", shouji5);
		row.set("shouji6", shouji6);
		row.set("shouji7", shouji7);
		row.set("shouji8", shouji8);
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());
		return null;
	}
	public ActionResult doGetAllZTJT() throws Exception{
		logger.info("进入总统计列表");
		JSONObject jsonobject = new JSONObject();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time = sdf.format(new Date());
		int year = Integer.parseInt(time.substring(0, 4));
		int nancount = accountManService.getAllNanUser();
		int nvcount = accountManService.getAllNvUser();
		int zerocount = accountManService.getAllZeroUser(year);
		int onecount = accountManService.getAllOneUser(year);
		int twocount = accountManService.getAllTwoUser(year);
		int threecount = accountManService.getAllThreeUser(year);
		int fourcount = accountManService.getAllFourUser(year);
		int fivecount = accountManService.getAllFiveUser(year);
		int sixcount = accountManService.getAllSixUser(year);
		int sevencount = accountManService.getAllSevenUser(year);
		int xscount = accountManService.getAllStudentUser(year);
		int gzcount = accountManService.getAllWorkUser(year);
		int xinzionegzcount = accountManService.getAllXinzioneUser();
		int xinzitwogzcount = accountManService.getAllXinzitwoUser();
		int xinzithreegzcount = accountManService.getAllXinzithreeUser();
		int xinzifourgzcount = accountManService.getAllXinzifourUser();
		int xinzifivegzcount = accountManService.getAllXinzifiveUser();
		List<DataRow> listcompany = accountManService.getAllCompany();
		int company1 = 0;
		int company2 = 0;
		int company3 = 0;
		int company4 = 0;
		int company5 = 0;
		int company6 = 0;
		int company7 = 0;
		int company8 = 0;
		int company9 = 0;
		int company10 = 0;
		int company11 = 0;
		int company12 = 0;
		int company13 = 0;
		int company14 = 0;
		int company15 = 0;
		int company16 = 0;
		int company17 = 0;
		int company18 = 0;
		int company19 = 0;
		for (DataRow dataRow : listcompany) {
			if("Kinh doanh".equals(dataRow.getString("company"))){
				company1 ++ ;
			}
			else if("Kinh doanh".equals(dataRow.getString("company"))){
				company2 ++ ;
			}
			else if("Hành chính".equals(dataRow.getString("company"))){
				company2 ++ ;
			}
			else if("Bảo vệ".equals(dataRow.getString("company"))){
				company3 ++ ;
			}
			else if("Biên phiên dịch".equals(dataRow.getString("company"))){
				company4 ++ ;
			}
			else if("Dệt may/ Giày da".equals(dataRow.getString("company"))){
				company5 ++ ;
			}
			else if("Du lịch/ Khách sạn".equals(dataRow.getString("company"))){
				company6 ++ ;
			}
			else if("Thể hình/ Spa".equals(dataRow.getString("company"))){
				company7 ++ ;
			}
			else if("Mỹ phẩm/ Thời trang".equals(dataRow.getString("company"))){
				company8 ++ ;
			}
			else if("Nông nghiệp".equals(dataRow.getString("company"))){
				company9 ++ ;
			}
			else if("PG/ PB/ Lễ tân".equals(dataRow.getString("company"))){
				company10 ++ ;
			}
			else if("Phục vụ/ Tạp vụ".equals(dataRow.getString("company"))){
				company11 ++ ;
			}
			else if("Sản xuất".equals(dataRow.getString("company"))){
				company12 ++ ;
			}
			else if("Tài xế/ Giao nhận".equals(dataRow.getString("company"))){
				company13 ++ ;
			}
			else if("Bán thời gian".equals(dataRow.getString("company"))){
				company14 ++ ;
			}
			else if("Thực phẩm/ Dịch vụ ăn uống".equals(dataRow.getString("company"))){
				company15 ++ ;
			}
			else if("Tư vấn bảo hiểm".equals(dataRow.getString("company"))){
				company16 ++ ;
			}
			else if("Sinh viên/ Mới tốt nghiệp".equals(dataRow.getString("company"))){
				company17 ++ ;
			}
			else if("Khác".equals(dataRow.getString("company"))){
				company18 ++ ;
			}
			else if("Đang tìm việc".equals(dataRow.getString("company"))){
				company19 ++ ;
			}
		}
		List<DataRow> listtime = accountManService.getAllTime();
		int time1 = 0;
		int time2 = 0;
		int time3 = 0;
		int time4 = 0;
		int time5 = 0;
		SimpleDateFormat sdftime = new SimpleDateFormat("MM-yyyy");
		String timetime = sdftime.format(new Date());
		Date date1 = sdftime.parse(timetime);
		for (DataRow dataRow2 : listtime) {
			String timesj = dataRow2.getString("time");
			if(dataRow2.getString("time").length() == 6){
				timesj = "0" + dataRow2.getString("time");
			}else if(dataRow2.getString("time").length() == 10){
				timesj = dataRow2.getString("time").substring(3);
			}
			Date date2 = sdftime.parse(timesj);
			long l = date1.getTime() - date2.getTime(); 
			long day = l / (24 * 60 * 60 * 1000);  
			if(day <= 90){
				time1 ++ ;
			}
			else if(day >90 && day <= 180){
				time2 ++ ;
			}
			else if(day >180 && day <= 365){
				time3 ++ ;
			}
			else if(day >365 && day <= 1095){
				time4 ++ ;
			}
			else if(day >1095){
				time5 ++ ;
			}
		}
		List<DataRow> listsj = accountManService.getAllPhone();
		int shouji1 = 0;
		int shouji2 = 0;
		int shouji3 = 0;
		int shouji4 = 0;
		int shouji5 = 0;
		int shouji6 = 0;
		int shouji7 = 0;
		int shouji8 = 0;
		int shouji9 = 0;
		for (DataRow dataRow2 : listsj) {
			String phonetype = dataRow2.getString("phonetype").replace("&nbsp;", "").toLowerCase();
			if(phonetype.contains("samsung")){
				shouji1 ++ ;
			}
			else if(phonetype.contains("huawei")){
				shouji2 ++ ;
			}
			else if(phonetype.contains("vivo")){
				shouji3 ++ ;
			}
			else if(phonetype.contains("oppo")){
				shouji4 ++ ;
			}
			else if(phonetype.contains("sony")){
				shouji5 ++ ;
			}
			else if(phonetype.contains("xiaomi")){
				shouji6 ++ ;
			}
			else if(phonetype.contains("iphone")){
				shouji7 ++ ;
			}
			else if(TextUtils.isEmpty(phonetype) || !"none".equals(phonetype)){
				shouji8 ++ ;
			}
		}
		DataRow row = new DataRow();
		row.set("nancount", nancount);
		row.set("nvcount", nvcount);
		row.set("zerocount", zerocount);
		row.set("onecount", onecount);
		row.set("twocount", twocount);
		row.set("threecount", threecount);
		row.set("fourcount", fourcount);
		row.set("fivecount", fivecount);
		row.set("sixcount", sixcount);
		row.set("sevencount", sevencount);
		row.set("xscount", xscount);
		row.set("gzcount", gzcount);
		row.set("xinzionegzcount", xinzionegzcount);
		row.set("xinzitwogzcount", xinzitwogzcount);
		row.set("xinzithreegzcount", xinzithreegzcount);
		row.set("xinzifourgzcount", xinzifourgzcount);
		row.set("xinzifivegzcount", xinzifivegzcount);
		row.set("company1", company1);
		row.set("company2", company2);
		row.set("company3", company3);
		row.set("company4", company4);
		row.set("company5", company5);
		row.set("company6", company6);
		row.set("company7", company7);
		row.set("company8", company8);
		row.set("company9", company9);
		row.set("company10", company10);
		row.set("company11", company11);
		row.set("company12", company12);
		row.set("company13", company13);
		row.set("company14", company14);
		row.set("company15", company15);
		row.set("company16", company16);
		row.set("company17", company17);
		row.set("company18", company18);
		row.set("company19", company19);
		row.set("time1", time1);
		row.set("time2", time2);
		row.set("time3", time3);
		row.set("time4", time4);
		row.set("time5", time5);
		row.set("shouji1", shouji1);
		row.set("shouji2", shouji2);
		row.set("shouji3", shouji3);
		row.set("shouji4", shouji4);
		row.set("shouji5", shouji5);
		row.set("shouji6", shouji6);
		row.set("shouji7", shouji7);
		row.set("shouji8", shouji8);
		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());
		return null;
	}
	//得到所有统计列表
public ActionResult doGetRecordListAll() throws Exception {
		
		logger.info("进入所有统计列表");
		int temp = getIntParameter("temp",0);
		int temp1 = getIntParameter("temp1",0);	//时间一个星期，一个月
		int temp2 = getIntParameter("temp2",0);	//审核部门
		int temp3 = getIntParameter("temp3",0);	//催收部门
		int temp4 = getIntParameter("temp4",0);	//放款部门
		SimpleDateFormat sdf =new SimpleDateFormat("dd-MM-yyyy");
		String qstime = "13-03-2018";
		Date qsdate = sdf.parse(qstime);
		String time = sdf.format(new Date());
		Date xzdate = sdf.parse(time);
		int days = 0;
		if(temp1 == 0){
			days = (int) ((xzdate.getTime() - qsdate.getTime()) / (1000*3600*24));
		}else if(temp1 == 1){
			days = 7;
		}else if(temp1 == 2){
			days = 30;
		}
		Calendar cd = Calendar.getInstance();
		try {
			cd.setTime(sdf.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(temp == 1 ){ // 审核部门全部
			String AllTime[] = new String[days];
			List<DataRow> listuserid = accountManService.getSHuserid();
			
			int shenheuserid[] = new int[listuserid.size()];
			for(int n = 0;n<listuserid.size();n++){
				DataRow rowuserid = listuserid.get(n);
				shenheuserid[n] = rowuserid.getInt("user_id");
			}
			//int shenheuserid[] = {100,51,52,99};
			int shenheshuju[][] = new int[days][shenheuserid.length];
			for(int i =0;i<AllTime.length;i++){
				Date date11 = new Date();
				Date date111 = new Date();
				long j = i ;
				date11.setTime(xzdate.getTime()-(1000*3600*24*j));
				AllTime[i] =sdf.format(date11);
				
				for(int k = 0 ;k<shenheuserid.length;k++){
					if(i==0){
						String tomorrow = sdf.format(xzdate.getTime()+(1000*3600*24));
						if(temp2 == 0){
							List<DataRow> list=accountManService.getZBS(shenheuserid[k],AllTime[0],tomorrow);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								shenheshuju[0][k] = datarow.getInt("count(id)");
							}
						}else if(temp2 == 1){
							List<DataRow> list=accountManService.getZBSTG(shenheuserid[k],AllTime[0],tomorrow);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								shenheshuju[0][k] = datarow.getInt("count(id)");
							}
						}else if(temp2 == 2){
							List<DataRow> list=accountManService.getZBS(shenheuserid[k],AllTime[0],tomorrow);
							List<DataRow> list1=accountManService.getZBSTG(shenheuserid[k],AllTime[0],tomorrow);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								DataRow datarow1 = list1.get(m);
								if(datarow.getInt("count(id)") !=0){
									shenheshuju[0][k] = datarow1.getInt("count(id)")*100/datarow.getInt("count(id)");
								}else{
									shenheshuju[0][k] = 0 ;
								}
							}
						}else if(temp2 == 3){
							List<DataRow> list=accountManService.getSSTG(shenheuserid[k],AllTime[0],tomorrow);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								shenheshuju[0][k] = datarow.getInt("count(id)");
							}
						}else if(temp2 == 4){
							List<DataRow> list=accountManService.getSSYQ(shenheuserid[k],AllTime[0],tomorrow);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								shenheshuju[0][k] = datarow.getInt("count(id)");
							}
						}else if(temp2 == 5){
							List<DataRow> list=accountManService.getSSTG(shenheuserid[k],AllTime[0],tomorrow);
							List<DataRow> list1=accountManService.getSSYQ(shenheuserid[k],AllTime[0],tomorrow);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								DataRow datarow1 = list1.get(m);
								if(datarow.getInt("count(id)") !=0){
									shenheshuju[0][k] = datarow1.getInt("count(id)")*100/datarow.getInt("count(id)");
								}else{
									shenheshuju[0][k] = 0 ;
								}
							}
						}else if(temp2 == 6){
							List<DataRow> list=accountManService.getSSTG(shenheuserid[k],AllTime[0],tomorrow);
							List<DataRow> list1=accountManService.getSSYQWH(shenheuserid[k],AllTime[0],tomorrow);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								DataRow datarow1 = list1.get(m);
								if(datarow.getInt("count(id)") !=0){
									shenheshuju[0][k] = datarow1.getInt("count(id)")*100/datarow.getInt("count(id)");
								}else{
									shenheshuju[0][k] = 0 ;
								}
							}
						}
					}else{
						date111.setTime(xzdate.getTime()-(1000*3600*24*(j-1)));
						AllTime[i-1] =sdf.format(date111);
						if(temp2 == 0){
							List<DataRow> list=accountManService.getZBS(shenheuserid[k],AllTime[i],AllTime[i-1]);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								shenheshuju[i][k] = datarow.getInt("count(id)");
							}
						}else if(temp2 == 1){
							List<DataRow> list=accountManService.getZBSTG(shenheuserid[k],AllTime[i],AllTime[i-1]);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								shenheshuju[i][k] = datarow.getInt("count(id)");
							}
						}else if(temp2 == 2){
							List<DataRow> list=accountManService.getZBS(shenheuserid[k],AllTime[i],AllTime[i-1]);
							List<DataRow> list1=accountManService.getZBSTG(shenheuserid[k],AllTime[i],AllTime[i-1]);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								DataRow datarow1 = list1.get(m);
								if(datarow.getInt("count(id)") !=0){
									shenheshuju[i][k] = datarow1.getInt("count(id)")*100/datarow.getInt("count(id)");
								}else{
									shenheshuju[i][k] = 0 ;
								}
							}
						}else if(temp2 == 3){
							List<DataRow> list=accountManService.getSSTG(shenheuserid[k],AllTime[i],AllTime[i-1]);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								shenheshuju[i][k] = datarow.getInt("count(id)");
							}
						}else if(temp2 == 4){
							List<DataRow> list=accountManService.getSSYQ(shenheuserid[k],AllTime[i],AllTime[i-1]);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								shenheshuju[i][k] = datarow.getInt("count(id)");
							}
						}else if(temp2 == 5){
							List<DataRow> list=accountManService.getSSTG(shenheuserid[k],AllTime[i],AllTime[i-1]);
							List<DataRow> list1=accountManService.getSSYQ(shenheuserid[k],AllTime[i],AllTime[i-1]);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								DataRow datarow1 = list1.get(m);
								if(datarow.getInt("count(id)") !=0){
									shenheshuju[i][k] = datarow1.getInt("count(id)")*100/datarow.getInt("count(id)");
								}else{
									shenheshuju[i][k] = 0 ;
								}
							}
						}else if(temp2 == 6){
							List<DataRow> list=accountManService.getSSTG(shenheuserid[k],AllTime[i],AllTime[i-1]);
							List<DataRow> list1=accountManService.getSSYQWH(shenheuserid[k],AllTime[i],AllTime[i-1]);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								DataRow datarow1 = list1.get(m);
								if(datarow.getInt("count(id)") !=0){
									shenheshuju[i][k] = datarow1.getInt("count(id)")*100/datarow.getInt("count(id)");
								}else{
									shenheshuju[i][k] = 0 ;
								}
							}
						}
					}
					
				}
				
			}
			DataRow row = new DataRow();
			row.set("temp",temp);
			row.set("temp1",temp1);
			row.set("temp2",temp2);
			row.set("temp3",temp3);
			row.set("temp4",temp4);
			row.set("alltime",AllTime);
			row.set("shenheshuju",shenheshuju);
			row.set("shenheuserid",shenheuserid);
			JSONObject object = JSONObject.fromBean(row);	
			this.getWriter().write(object.toString());
			return null ;
		}else if(temp == 2){
			String AllTime[] = new String[days];
			List<DataRow> listuserid = accountManService.getCSuserid();
			int shenheuserid[] = new int[listuserid.size()];
			for(int n = 0;n<listuserid.size();n++){
				DataRow rowuserid = listuserid.get(n);
				shenheuserid[n] = rowuserid.getInt("user_id");
			}
			//int shenheuserid[] ={14,16,17,18,50,98};
			double shenheshuju[][] = new double[days][shenheuserid.length];
			for(int i =0;i<AllTime.length;i++){
				Date date11 = new Date();
				Date date111 = new Date();
				long j = i ;
				date11.setTime(xzdate.getTime()-(1000*3600*24*j));
				AllTime[i] =sdf.format(date11);
				
				for(int k = 0 ;k<shenheuserid.length;k++){
					if(i==0){
						String tomorrow = sdf.format(xzdate.getTime()+(1000*3600*24));
						if(temp3 == 0){
							List<DataRow> list=accountManService.getCSZBS(shenheuserid[k],AllTime[0],tomorrow);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								shenheshuju[0][k] = datarow.getInt("count(id)");
							}
						}else if(temp3 == 1){
							List<DataRow> list=accountManService.getCSZJE(shenheuserid[k],AllTime[0],tomorrow);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								shenheshuju[0][k] = datarow.getDouble("zje");
							}
						}else if(temp3 == 2){
							List<DataRow> list=accountManService.getCSZJEYH(shenheuserid[k],AllTime[0],tomorrow);
							List<DataRow> list1=accountManService.getCSZJEYH1(shenheuserid[k],AllTime[0],tomorrow);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								DataRow datarow1 = list1.get(m);
								shenheshuju[0][k] = datarow.getDouble("yhje")+datarow1.getDouble("yqje");
								
							}
						}else if(temp3 == 3){
							List<DataRow> list=accountManService.getCSZJE(shenheuserid[k],AllTime[0],tomorrow);
							List<DataRow> list1=accountManService.getCSZJEYH(shenheuserid[k],AllTime[0],tomorrow);
							List<DataRow> list2=accountManService.getCSZJEYH1(shenheuserid[k],AllTime[0],tomorrow);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								DataRow datarow1 = list1.get(m);
								DataRow datarow2 = list2.get(m);
								if(datarow.getDouble("zje") !=0){
									shenheshuju[0][k] = (int)((datarow1.getDouble("yhje")+datarow2.getDouble("yqje"))*100/datarow.getDouble("zje"));
								}else{
									shenheshuju[0][k] = 0;
								}
							}
						}else if(temp3 == 4){
							List<DataRow> list=accountManService.getTXZJE(shenheuserid[k],AllTime[0],tomorrow);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								shenheshuju[0][k] = (int)datarow.getDouble("txzje");
							}
						}else if(temp3 == 5){
							List<DataRow> list=accountManService.getTXYHZJE(shenheuserid[k],AllTime[0],tomorrow);
							List<DataRow> list1=accountManService.getTXYHZJE1(shenheuserid[k],AllTime[0],tomorrow);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								DataRow datarow1 = list1.get(m);
								shenheshuju[0][k] = (int) (datarow.getDouble("txyhzje") + datarow1.getDouble("txyhyqje"));
							}
						}else if(temp3 == 6){
							List<DataRow> list=accountManService.getTXZJE(shenheuserid[k],AllTime[i],AllTime[i-1]);
							List<DataRow> list1=accountManService.getTXYHZJE(shenheuserid[k],AllTime[i],AllTime[i-1]);
							List<DataRow> list2=accountManService.getTXYHZJE1(shenheuserid[k],AllTime[i],AllTime[i-1]);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								DataRow datarow1 = list1.get(m);
								DataRow datarow2 = list2.get(m);
								if(datarow.getDouble("txzje") !=0){
									shenheshuju[0][k] = (int) ((datarow1.getDouble("txyhzje") + datarow2.getDouble("txyhyqje"))*100/datarow.getDouble("txzje"));
								}else{
									shenheshuju[0][k] = 0 ;
								}
							}
						}
					}else{
						date111.setTime(xzdate.getTime()-(1000*3600*24*(j-1)));
						AllTime[i-1] =sdf.format(date111);
						if(temp3 == 0){
							List<DataRow> list=accountManService.getCSZBS(shenheuserid[k],AllTime[i],AllTime[i-1]);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								shenheshuju[i][k] = datarow.getInt("count(id)");
							}
						}else if(temp3 == 1){
							List<DataRow> list=accountManService.getCSZJE(shenheuserid[k],AllTime[i],AllTime[i-1]);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								shenheshuju[i][k] = datarow.getDouble("zje");
							}
						}else if(temp3 == 2){
							List<DataRow> list=accountManService.getCSZJEYH(shenheuserid[k],AllTime[i],AllTime[i-1]);
							List<DataRow> list1=accountManService.getCSZJEYH1(shenheuserid[k],AllTime[i],AllTime[i-1]);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								DataRow datarow1 = list1.get(m);
								shenheshuju[i][k] = datarow.getDouble("yhje")+datarow1.getDouble("yqje");
							}
						}else if(temp3 == 3){
							List<DataRow> list=accountManService.getCSZJE(shenheuserid[k],AllTime[i],AllTime[i-1]);
							List<DataRow> list1=accountManService.getCSZJEYH(shenheuserid[k],AllTime[i],AllTime[i-1]);
							List<DataRow> list2=accountManService.getCSZJEYH1(shenheuserid[k],AllTime[i],AllTime[i-1]);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								DataRow datarow1 = list1.get(m);
								DataRow datarow2 = list2.get(m);
								if(datarow.getDouble("zje") !=0){
									shenheshuju[i][k] = (int)((datarow1.getDouble("yhje")+datarow2.getDouble("yqje"))*100/datarow.getDouble("zje"));
								}else{
									shenheshuju[i][k] =0; 
								}
							}
						}else if(temp3 == 4){
							List<DataRow> list=accountManService.getTXZJE(shenheuserid[k],AllTime[i],AllTime[i-1]);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								shenheshuju[i][k] = datarow.getDouble("txzje");
							}
						}else if(temp3 == 5){
							List<DataRow> list=accountManService.getTXYHZJE(shenheuserid[k],AllTime[i],AllTime[i-1]);
							List<DataRow> list1=accountManService.getTXYHZJE1(shenheuserid[k],AllTime[i],AllTime[i-1]);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								DataRow datarow1 = list1.get(m);
								shenheshuju[i][k] = (int) (datarow.getDouble("txyhzje") + datarow1.getDouble("txyhyqje"));
							}
						}else if(temp3 == 6){
							List<DataRow> list=accountManService.getTXZJE(shenheuserid[k],AllTime[i],AllTime[i-1]);
							List<DataRow> list1=accountManService.getTXYHZJE(shenheuserid[k],AllTime[i],AllTime[i-1]);
							List<DataRow> list2=accountManService.getTXYHZJE1(shenheuserid[k],AllTime[i],AllTime[i-1]);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								DataRow datarow1 = list1.get(m);
								DataRow datarow2 = list2.get(m);
								if(datarow.getDouble("txzje") !=0){
									shenheshuju[i][k] =(int) ((datarow1.getDouble("txyhzje") + datarow2.getDouble("txyhyqje"))*100/datarow.getDouble("txzje"));
								}else{
									shenheshuju[i][k] = 0 ;
								}
							}
						}
					}
					
				}
				
			}
			DataRow row = new DataRow();
			row.set("temp",temp);
			row.set("temp1",temp1);
			row.set("temp2",temp2);
			row.set("temp3",temp3);
			row.set("temp4",temp4);
			row.set("alltime",AllTime);
			row.set("shenheshuju",shenheshuju);
			row.set("shenheuserid",shenheuserid);
			JSONObject object = JSONObject.fromBean(row);	
			this.getWriter().write(object.toString());
			return null ;
		}else if(temp == 3){
			String AllTime[] = new String[days];
			List<DataRow> listuserid = accountManService.getFKuserid();
			int shenheuserid[] = new int[listuserid.size()];
			for(int n = 0;n<listuserid.size();n++){
				DataRow rowuserid = listuserid.get(n);
				shenheuserid[n] = rowuserid.getInt("user_id");
			}
			//int shenheuserid[] ={12,18,19};
			double shenheshuju[][] = new double[days][shenheuserid.length];
			for(int i =0;i<AllTime.length;i++){
				Date date11 = new Date();
				Date date111 = new Date();
				long j = i ;
				date11.setTime(xzdate.getTime()-(1000*3600*24*j));
				AllTime[i] =sdf.format(date11);
				
				for(int k = 0 ;k<shenheuserid.length;k++){
					if(i==0){
						String tomorrow = sdf.format(xzdate.getTime()+(1000*3600*24));
						if(temp4 == 0){
							List<DataRow> list=accountManService.getFKZBS(shenheuserid[k],AllTime[0],tomorrow);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								shenheshuju[0][k] = datarow.getInt("count(id)");
							}
						}else if(temp4 == 1){
							List<DataRow> list=accountManService.getFKCG(shenheuserid[k],AllTime[0],tomorrow);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								shenheshuju[0][k] = datarow.getInt("count(id)");
							}
						}else if(temp4 == 2){
							List<DataRow> list=accountManService.getFKCGJE(shenheuserid[k],AllTime[0],tomorrow);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								shenheshuju[0][k] = datarow.getDouble("fkcgje");
								
							}
						}else if(temp4 == 3){
							List<DataRow> list=accountManService.getFKSB(shenheuserid[k],AllTime[0],tomorrow);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								shenheshuju[0][k] = datarow.getInt("count(id)");
							}
						}else if(temp4 == 4){
							List<DataRow> list=accountManService.getFKSBJE(shenheuserid[k],AllTime[0],tomorrow);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								shenheshuju[0][k] = datarow.getDouble("fksbje");
								
							}
						}else if(temp4 == 5){
							List<DataRow> list=accountManService.getFKZBS(shenheuserid[k],AllTime[0],tomorrow);
							List<DataRow> list1=accountManService.getFKSB(shenheuserid[k],AllTime[0],tomorrow);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								DataRow datarow1 = list1.get(m);
								if(datarow.getInt("count(id)") !=0){
									shenheshuju[0][k] = (datarow1.getInt("count(id)")*100/datarow.getInt("count(id)"));
								}else{
									shenheshuju[0][k] = 0;
								}
							}
						}
					}else{
						date111.setTime(xzdate.getTime()-(1000*3600*24*(j-1)));
						AllTime[i-1] =sdf.format(date111);
						if(temp4 == 0){
							List<DataRow> list=accountManService.getFKZBS(shenheuserid[k],AllTime[i],AllTime[i-1]);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								shenheshuju[i][k] = datarow.getInt("count(id)");
							}
						}else if(temp4 == 1){
							List<DataRow> list=accountManService.getFKCG(shenheuserid[k],AllTime[i],AllTime[i-1]);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								shenheshuju[i][k] = datarow.getInt("count(id)");
							}
						}else if(temp4 == 2){
							List<DataRow> list=accountManService.getFKCGJE(shenheuserid[k],AllTime[i],AllTime[i-1]);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								shenheshuju[i][k] = datarow.getDouble("fkcgje");
							}
						}else if(temp4 == 3){
							List<DataRow> list=accountManService.getFKSB(shenheuserid[k],AllTime[i],AllTime[i-1]);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								shenheshuju[i][k] = datarow.getInt("count(id)");
							}
						}else if(temp4 == 4){
							List<DataRow> list=accountManService.getFKSBJE(shenheuserid[k],AllTime[i],AllTime[i-1]);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								shenheshuju[i][k] = datarow.getDouble("fksbje");
							}
						}else if(temp4 == 5){
							List<DataRow> list=accountManService.getFKZBS(shenheuserid[k],AllTime[i],AllTime[i-1]);
							List<DataRow> list1=accountManService.getFKSB(shenheuserid[k],AllTime[i],AllTime[i-1]);
							for (int m = 0 ;m<list.size();m++) {
								DataRow datarow = list.get(m);
								DataRow datarow1 = list1.get(m);
								if(datarow.getInt("count(id)") != 0){
									shenheshuju[i][k] = (datarow1.getInt("count(id)")*100 / datarow.getInt("count(id)"));
								}else{
									shenheshuju[i][k] = 0 ;
								}
							}
						}
					}
					
				}
				
			}
			DataRow row = new DataRow();
			row.set("temp",temp);
			row.set("temp1",temp1);
			row.set("temp2",temp2);
			row.set("temp3",temp3);
			row.set("temp4",temp4);
			row.set("alltime",AllTime);
			row.set("shenheshuju",shenheshuju);
			row.set("shenheuserid",shenheuserid);
			JSONObject object = JSONObject.fromBean(row);	
			this.getWriter().write(object.toString());
			return null ;
		}
		//List<DataRow> list=accountManService.getRecordListAll(userId,realName,phone,startDate,endDate);
		DataRow row = new DataRow();
		
		//row.set("list",list);
		row.set("temp",temp);
		row.set("temp1",temp1);
		row.set("temp2",temp2);
		row.set("temp3",temp3);
		row.set("temp4",temp4);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
   	    return null ;  
		
	}
}
