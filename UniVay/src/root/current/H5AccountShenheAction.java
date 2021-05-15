package root.current;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.project.bean.ShenKeAccount;
import com.project.service.account.H5AccountShenheService;
import com.project.service.role.UserService;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;
import com.thinkive.base.jdbc.DBPage;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.util.StringHelper;
import com.thinkive.web.base.ActionResult;
import com.thinkive.web.base.BaseAction;

import net.sf.json.JSONObject;

/**
 * 审核统计
 * @author Administrator
 *
 */
public class H5AccountShenheAction extends BaseAction {

	private static Logger logger= Logger.getLogger(H5AccountShenheAction.class);
	private static H5AccountShenheService accountShenheService=new H5AccountShenheService();
	private static UserService userService=new UserService();
	
	
	/**
	 * 得到审核统计
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetShenheAccount() throws java.text.ParseException{
		//分页查询所有的后台用户
		int temp = getIntParameter("temp",0);
		int curPage  =getIntParameter("curPage",1);	
		String  tempVelue = getStrParameter("tempvl");
		String  startDate =getStrParameter("startDate");
		String  endDate =getStrParameter("endDate");
		int  recode =getIntParameter("recode",0);
		logger.info("========recode======"+recode);
		String name="";
		String phone="";
		if(temp==1){
			name=tempVelue;
		}else if(temp==2){
			phone=tempVelue;
		}
		DBPage page = userService.selectUserListByCondtion(curPage, 40, 0, name, phone); 
		List<DataRow> list=page.getData();
		List<ShenKeAccount> shenke=new ArrayList<ShenKeAccount>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat sdfday = new SimpleDateFormat("yyyy-MM-dd");
		String time = sdf.format(new Date());
		String timeday = sdfday.format(new Date());
		
		Calendar cd = Calendar.getInstance();
		try {
			cd.setTime(sdf.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//cd.add(Calendar.DATE, -i); // 减一天
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 减一天
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		if(recode == 0){
			for (int i=0 ;i<list.size();i++) {
				DataRow dataRow = list.get(i);
				int userid=dataRow.getInt("user_id");
				ShenKeAccount account=new ShenKeAccount();
				account.setUserid(userid);
				account.setUserName(dataRow.getString("name"));
				account.setPhone(dataRow.getString("phone"));
				account.setKfcxcs(dataRow.getString("kfcxcs"));
				
				//当天
				int nowOnesh=accountShenheService.getNowOneCount(userid, nowdate, dateno,name,phone);	  //当天一审笔数
				int nowTwosh=accountShenheService.getNowTwoCount(userid, nowdate, dateno,name,phone);	//当天二审笔数
				int nowThreesh=accountShenheService.getNowThreeCount(userid, nowdate, dateno,name,phone);	//当天三审笔数
				int nowOneshsjrs=accountShenheService.getNowOnesjrsCount(userid, startDate,endDate,nowdate, dateno,name,phone);	  //当天一审笔数
				int nowlyh = 0;
				/*List<DataRow> userlyh=accountShenheService.getNowLYHCount(userid, startDate,endDate,nowdate, dateno,name,phone);	  //当天一审笔数
				for (DataRow dataRow2 : userlyh) {
					int userlyhid = dataRow2.getInt("userid");
					int usersfyfk = accountShenheService.getAllSfyfk(userlyhid,timeday);
					if(usersfyfk>0){
						nowlyh++;
					}
				}*/
				//int nowTwoshsjrs=accountShenheService.getNowTwosjrsCount(userid,startDate,endDate, nowdate, dateno,name,phone);	//当天二审笔数
				//int nowThreeshsjrs=accountShenheService.getNowThreesjrsCount(userid,startDate,endDate, nowdate, dateno,name,phone);	//当天三审笔数
				int nowOneshSB=accountShenheService.getNowOneCountSB(userid, nowdate, dateno,name,phone);	  //失败当天一审笔数
				int nowOneshSBXXCW=accountShenheService.getNowOneCountSBXXCW(userid, nowdate, dateno,name,phone);	  //失败当天一审笔数
				int nowTwoshSB=accountShenheService.getNowTwoCountSB(userid, nowdate, dateno,name,phone);	//失败当天二审笔数
				int nowThreeshSB=accountShenheService.getNowThreeCountSB(userid, nowdate, dateno,name,phone);	//失败当天三审笔数
				int nowtwotgfk=accountShenheService.getNowTwoCountTGFK(userid,startDate,endDate,nowdate, dateno,name,phone);	//二审通过并且放款笔数
				int nowthreeTG = accountShenheService.getNowThreeCountTG(userid,startDate,endDate, nowdate, dateno,name, phone);//三审通过笔数
				
				int nowCount=nowOnesh+nowTwosh+nowThreesh;	  //当天总笔数
				int nowCountsjrs=nowOneshsjrs;	  //当天总笔数
				int nowCountSB=nowOneshSB+nowTwoshSB+nowThreeshSB;	  //失败当天总笔数
				
				//总数
				int one=0;   //一审笔数
				int onesjrs=0;   //一审笔数
				int two=0;	//二审笔数
				//int twosjrs=accountShenheService.getTwosjrsCount(userid,startDate, endDate,name,phone);	//二审笔数
				int twotgfk=0;	//二审通过并且放款笔数
				int twotghk=0;	//二审通过并且正常还款笔数
				int twotgfkyq=0;	//二审通过并且放款笔数
				int twotgfkyqwh=0;	//二审通过并且放款笔数
				int three=0; //三审笔数
				//int threesjrs=accountShenheService.getThreesjrsCount(userid,startDate, endDate,name,phone); //三审笔数
				int oneSB=0;   //失败一审笔数
				int oneSBXXCW=0;   //失败一审信息错误笔数
				int twoSB=0;	//失败二审笔数
				int threeSB=0; //失败三审笔数
				int threeTG = 0;//三审通过笔数
				//int yh = accountShenheService.getYH(userid, startDate, endDate,name, phone);//三审已还
				//int four=accountShenheService.getThreeCountYQ(userid, startDate, endDate,name, phone);	//三审逾期笔数
				int four = twotgfkyq ; 
				//int yqwh = accountShenheService.getYQWH(userid, startDate, endDate,name, phone);//逾期未还笔数
				int count=one+two+three; //总笔数
				//int countsjrs=onesjrs+twosjrs+threesjrs; //总笔数
				int countsjrs=onesjrs; //总笔数
				int countSB=oneSB+twoSB+threeSB; //失败总笔数
				int yqbl = 0;
				int yqwhbl = 0;
				int twotgzhk = twotghk + twotgfkyq ;
				if (twotgzhk == 0) {
					yqbl = 0;
					yqwhbl = 0;
				}else {
					if(twotgfkyq == 0){
						yqbl = 0;
						yqwhbl = 0;
					}else{
						yqbl =100*  twotgfkyq / twotgzhk ;
						yqwhbl = 100 * twotgfkyqwh / twotgzhk;
					
					}
				}
				
				account.setNowlyh(nowlyh);
				account.setCount(count);
				account.setCountsjrs(countsjrs);
				account.setCountSB(countSB);
				account.setFour(four);
				account.setYqbl(yqbl);
				account.setYqwhbl(yqwhbl);
				account.setNowCount(nowCount);
				account.setNowCountsjrs(nowCountsjrs);
				account.setNowOnesh(nowOnesh);
				account.setNowThreesh(nowThreesh);
				account.setNowTwosh(nowTwosh);
				account.setNowCountSB(nowCountSB);
				account.setNowOneshSB(nowOneshSB);
				account.setNowOneshSBXXCW(nowOneshSBXXCW);
				account.setNowThreeshSB(nowThreeshSB);
				account.setNowTwoshSB(nowTwoshSB);
				account.setOne(one);
				account.setThree(three);
				account.setOneSB(oneSB);
				account.setOneSBXXCW(oneSBXXCW);
				account.setThreeSB(threeSB);
				account.setTwotgfk(twotgfk);
				account.setNowtwotgfk(nowtwotgfk);
				account.setTwotgzhk(twotgzhk);
				account.setTwotgfkyqwh(twotgfkyqwh);
				account.setThreeTG(threeTG);
				account.setNowthreeTG(nowthreeTG);
				account.setTwo(two);
				account.setTwoSB(twoSB);
				shenke.add(account);
			}
		}else if(recode == 1){
			for (int i=0 ;i<list.size();i++) {
				DataRow dataRow = list.get(i);
				int userid=dataRow.getInt("user_id");
				ShenKeAccount account=new ShenKeAccount();
				account.setUserid(userid);
				account.setUserName(dataRow.getString("name"));
				account.setPhone(dataRow.getString("phone"));
				account.setKfcxcs(dataRow.getString("kfcxcs"));
				
				//当天
				int nowOnesh=accountShenheService.getNowOneCount(userid, nowdate, dateno,name,phone);	  //当天一审笔数
				int nowTwosh=accountShenheService.getNowTwoCount(userid, nowdate, dateno,name,phone);	//当天二审笔数
				int nowThreesh=accountShenheService.getNowThreeCount(userid, nowdate, dateno,name,phone);	//当天三审笔数
				int nowOneshsjrs=accountShenheService.getNowOnesjrsCount(userid, startDate,endDate,nowdate, dateno,name,phone);	  //当天一审笔数
				//int nowTwoshsjrs=accountShenheService.getNowTwosjrsCount(userid,startDate,endDate, nowdate, dateno,name,phone);	//当天二审笔数
				//int nowThreeshsjrs=accountShenheService.getNowThreesjrsCount(userid,startDate,endDate, nowdate, dateno,name,phone);	//当天三审笔数
				int nowOneshSB=accountShenheService.getNowOneCountSB(userid, nowdate, dateno,name,phone);	  //失败当天一审笔数
				int nowOneshSBXXCW=accountShenheService.getNowOneCountSBXXCW(userid, nowdate, dateno,name,phone);	  //失败当天一审笔数
				int nowTwoshSB=accountShenheService.getNowTwoCountSB(userid, nowdate, dateno,name,phone);	//失败当天二审笔数
				int nowThreeshSB=accountShenheService.getNowThreeCountSB(userid, nowdate, dateno,name,phone);	//失败当天三审笔数
				int nowtwotgfk=accountShenheService.getNowTwoCountTGFK(userid,startDate,endDate,nowdate, dateno,name,phone);	//二审通过并且放款笔数
				int nowthreeTG = accountShenheService.getNowThreeCountTG(userid,startDate,endDate, nowdate, dateno,name, phone);//三审通过笔数
				
				int nowCount=nowOnesh+nowTwosh+nowThreesh;	  //当天总笔数
				int nowCountsjrs=nowOneshsjrs;	  //当天总笔数
				int nowCountSB=nowOneshSB+nowTwoshSB+nowThreeshSB;	  //失败当天总笔数
				int nowlyh = 0;
				//总数
				int one=accountShenheService.getOneCount(userid,startDate, endDate,name,phone);   //一审笔数
				int onesjrs=accountShenheService.getOnesjrsCount(userid,startDate, endDate,name,phone);   //一审笔数
				int two=accountShenheService.getTwoCount(userid,startDate, endDate,name,phone);	//二审笔数
				//int twosjrs=accountShenheService.getTwosjrsCount(userid,startDate, endDate,name,phone);	//二审笔数
				int twotgfk=accountShenheService.getTwoCountTGFK(userid,startDate, endDate,name,phone);	//二审通过并且放款笔数
				int twotghk=accountShenheService.getTwoCountTGHK(userid,startDate, endDate,name,phone);	//二审通过并且正常还款笔数
				int twotgfkyq=accountShenheService.getTwoCountTGFKYQ(userid,startDate, endDate,name,phone);	//二审通过并且放款笔数
				int twotgfkyqwh=accountShenheService.getTwoCountTGFKYQWH(userid,startDate, endDate,name,phone);	//二审通过并且放款笔数
				int three=accountShenheService.getThreeCount(userid,startDate, endDate,name,phone); //三审笔数
				//int threesjrs=accountShenheService.getThreesjrsCount(userid,startDate, endDate,name,phone); //三审笔数
				int oneSB=accountShenheService.getOneCountSB(userid,startDate, endDate,name,phone);   //失败一审笔数
				int oneSBXXCW=accountShenheService.getOneCountSBXXCW(userid,startDate, endDate,name,phone);   //失败一审信息错误笔数
				int twoSB=accountShenheService.getTwoCountSB(userid,startDate, endDate,name,phone);	//失败二审笔数
				int threeSB=accountShenheService.getThreeCountSB(userid,startDate, endDate,name,phone); //失败三审笔数
				int threeTG = accountShenheService.getThreeCountTG(userid, startDate, endDate,name, phone);//三审通过笔数
				//int yh = accountShenheService.getYH(userid, startDate, endDate,name, phone);//三审已还
				//int four=accountShenheService.getThreeCountYQ(userid, startDate, endDate,name, phone);	//三审逾期笔数
				int four = twotgfkyq ; 
				//int yqwh = accountShenheService.getYQWH(userid, startDate, endDate,name, phone);//逾期未还笔数
				int count=one+two+three; //总笔数
				//int countsjrs=onesjrs+twosjrs+threesjrs; //总笔数
				int countsjrs=onesjrs; //总笔数
				int countSB=oneSB+twoSB+threeSB; //失败总笔数
				int yqbl = 0;
				int yqwhbl = 0;
				int twotgzhk = twotghk + twotgfkyq ;
				if (twotgzhk == 0) {
					yqbl = 0;
					yqwhbl = 0;
				}else {
					if(twotgfkyq == 0){
						yqbl = 0;
						yqwhbl = 0;
					}else{
						yqbl =100*  twotgfkyq / twotgzhk ;
						yqwhbl = 100 * twotgfkyqwh / twotgzhk;
					
					}
				}
				
				account.setNowlyh(nowlyh);
				account.setCount(count);
				account.setCountsjrs(countsjrs);
				account.setCountSB(countSB);
				account.setFour(four);
				account.setYqbl(yqbl);
				account.setYqwhbl(yqwhbl);
				account.setNowCount(nowCount);
				account.setNowCountsjrs(nowCountsjrs);
				account.setNowOnesh(nowOnesh);
				account.setNowThreesh(nowThreesh);
				account.setNowTwosh(nowTwosh);
				account.setNowCountSB(nowCountSB);
				account.setNowOneshSB(nowOneshSB);
				account.setNowOneshSBXXCW(nowOneshSBXXCW);
				account.setNowThreeshSB(nowThreeshSB);
				account.setNowTwoshSB(nowTwoshSB);
				account.setOne(one);
				account.setThree(three);
				account.setOneSB(oneSB);
				account.setOneSBXXCW(oneSBXXCW);
				account.setThreeSB(threeSB);
				account.setTwotgfk(twotgfk);
				account.setNowtwotgfk(nowtwotgfk);
				account.setTwotgzhk(twotgzhk);
				account.setTwotgfkyqwh(twotgfkyqwh);
				account.setThreeTG(threeTG);
				account.setNowthreeTG(nowthreeTG);
				account.setTwo(two);
				account.setTwoSB(twoSB);
				shenke.add(account);
			}
		}
		int countaa[] = new int[list.size()];
		int useridaa[] = new int[list.size()];
		for(int j=0 ;j<list.size();j++){
			DataRow dataRow1 = list.get(j);
			useridaa[j]=dataRow1.getInt("user_id");
			int nowOneshsjrs=accountShenheService.getNowOnesjrsCount(useridaa[j], startDate,endDate,nowdate, dateno,name,phone);	  //当天一审笔数
			int nowTwoshsjrs=accountShenheService.getNowTwosjrsCount(useridaa[j],startDate,endDate, nowdate, dateno,name,phone);	//当天二审笔数
			int nowThreeshsjrs=accountShenheService.getNowThreesjrsCount(useridaa[j],startDate,endDate, nowdate, dateno,name,phone);	//当天三审笔数
			countaa[j]=nowOneshsjrs+nowTwoshsjrs+nowThreeshsjrs; //总笔数
		}
		int nn = countaa.length;
	    for (int m = 0; m < nn - 1; m++) {   
	      for (int n = 0; n < nn - 1; n++) { 
	        if (countaa[n] > countaa[n + 1]) { 
	          int tempaa = countaa[n];   
	          int tempaaa = useridaa[n]; 
	          countaa[n] = countaa[n + 1];   
	          useridaa[n] = useridaa[n + 1]; 
	          countaa[n + 1] = tempaa;   
	          useridaa[n + 1] = tempaaa;   
	        }   
	      }   
	    }
	    for(int k=0 ;k<list.size();k++){
			DataRow row = new DataRow();
			row.set("user_id", useridaa[k]);
			row.set("px", list.size()-k);
			accountShenheService.updatePX(row);	
	    }
		page.setData(shenke);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
		return null;
	}
	/**
	 * 得到审核统计
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetShenheAccountRSKPI() throws java.text.ParseException{
		//分页查询所有的后台用户
		int temp = getIntParameter("temp",0);
		int curPage  =getIntParameter("curPage",1);	
		String  tempVelue = getStrParameter("tempvl");
		String  startDate =getStrParameter("startDate");
		String  endDate =getStrParameter("endDate");
		int  recode =getIntParameter("recode",0);

		String name="";
		String phone="";
		if(temp==1){
			name=tempVelue;
		}else if(temp==2){
			phone=tempVelue;
		}
		DBPage page = userService.selectUserListByCondtion(curPage, 40, 0, name, phone); 
		List<DataRow> list=page.getData();
		List<ShenKeAccount> shenke=new ArrayList<ShenKeAccount>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat sdfday = new SimpleDateFormat("yyyy-MM-dd");
		String time = sdf.format(new Date());
		String timeday = sdfday.format(new Date());
		
		Calendar cd = Calendar.getInstance();
		try {
			cd.setTime(sdf.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//cd.add(Calendar.DATE, -i); // 减一天
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 减一天
		Date date = cd.getTime();
		String dateno = sdf.format(date);
	
			for (int i=0 ;i<list.size();i++) {
				DataRow dataRow = list.get(i);
				int userid=dataRow.getInt("user_id");
				ShenKeAccount account=new ShenKeAccount();
				account.setUserid(userid);
				account.setUserName(dataRow.getString("name"));
				account.setPhone(dataRow.getString("phone"));
				account.setKfcxcs(dataRow.getString("kfcxcs"));
				
				//当天
				
				
				
				int twotgfk=accountShenheService.getTwoCountTGFK(userid,startDate, endDate,name,phone);	//二审通过并且放款笔数
				int nowlyh = 0;
				if (!StringHelper.isEmpty(startDate)) {
					List<DataRow> userlyh=accountShenheService.getNowLYHCount(userid, startDate,endDate,name,phone);	  //当天一审笔数
					for (DataRow dataRow2 : userlyh) {
						int userlyhid = dataRow2.getInt("userid");
						int usersfyfk = accountShenheService.getAllSfyfk(userlyhid,timeday);
						if(usersfyfk>1){
							nowlyh++;
						}
					}
				}
				int twotgzhk = twotgfk - nowlyh ;
				account.setNowlyh(nowlyh);
				account.setTwotgfk(twotgfk);
				account.setTwotgzhk(twotgzhk);
				shenke.add(account);
			}
			
		page.setData(shenke);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
		return null;
	}
	/**
	 * 得到审核统计
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetShuJuAccount() throws java.text.ParseException{
		//分页查询所有的后台用户
		int temp = getIntParameter("temp",0);
		int curPage  =getIntParameter("curPage",1);	
		String  tempVelue = getStrParameter("tempvl");
		String  startDate =getStrParameter("startDate");
		String  endDate =getStrParameter("endDate");
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String time = sdf.format(new Date());
		Calendar cd = Calendar.getInstance();
		try {
			cd.setTime(sdf.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int zong[]=new int[7];
		int fifteen[]=new int[7];
		int thirty[]=new int[7];
		int newuser[]=new int[7];
		int olduser[]=new int[7];
		int havashuju[]=new int[7];
		int noshuju[]=new int[7];
		int pingfen3[]=new int[7];
		int pingfen2[]=new int[7];
		int pingfen1[]=new int[7];
		int pingfenf1[]=new int[7];
		int totaluser[]=new int[7];
		int twicejk[]=new int[7];
		int thirdjk[]=new int[7];
		int fourjk[]=new int[7];
		int fivejk[]=new int[7];
		int sixjk[]=new int[7];
		int sevenjk[]=new int[7];
		int eightjk[]=new int[7];
		int ninejk[]=new int[7];
		int tenjk[]=new int[7];
		int dytenjk[]=new int[7];
		int manjk[]=new int[7];
		int womenjk[]=new int[7];
		int iosjk[]=new int[7];
		int andjk[]=new int[7];
	/*	zong[0] = "总统计";
		fifteen[0]="借款期限15天";
		thirty[0]="借款期限30天";
		newuser[0]="新用户(一次借款)";
		olduser[0]="老用户(大于一次借款)";
		havashuju[0]="有匹配到数据";
		noshuju[0]="没有匹配到数据";
		pingfen3[0]="评分为3";
		pingfen2[0]="评分为2";
		pingfen1[0]="评分为1";
		pingfenf1[0]="评分为-1";*/
		
		zong[0] = accountShenheService.getZong1(startDate, endDate);
		fifteen[0]= accountShenheService.getFifteen1(startDate, endDate);
		thirty[0]= accountShenheService.getThirty1(startDate, endDate);
		newuser[0]= accountShenheService.getNewuser1(startDate, endDate);
		olduser[0]= accountShenheService.getOlduser1(startDate, endDate);
		havashuju[0]= accountShenheService.getHavashuju1(startDate, endDate);
		noshuju[0]= accountShenheService.getNoshuju1(startDate, endDate);
		manjk[0]= accountShenheService.getManjk1(startDate, endDate);
		womenjk[0]= accountShenheService.getWomenjk1(startDate, endDate);
		pingfen3[0]= accountShenheService.getPingfen31(startDate, endDate);
		pingfen2[0]= accountShenheService.getPingfen21(startDate, endDate);
		pingfen1[0]= accountShenheService.getPingfen11(startDate, endDate);
		pingfenf1[0]= accountShenheService.getPingfenf11(startDate, endDate);
		totaluser[0]= accountShenheService.getTotaluser1(startDate, endDate);
		twicejk[0]= accountShenheService.getTwicejk1(startDate, endDate);
		thirdjk[0]= accountShenheService.getThirdjk1(startDate, endDate);
		fourjk[0]= accountShenheService.getFourjk1(startDate, endDate);
		fivejk[0]= accountShenheService.getFivejk1(startDate, endDate);
		sixjk[0]= accountShenheService.getSixjk1(startDate, endDate);
		sevenjk[0]= accountShenheService.getSevenjk1(startDate, endDate);
		eightjk[0]= accountShenheService.getEightjk1(startDate, endDate);
		ninejk[0]= accountShenheService.getNinejk1(startDate, endDate);
		tenjk[0]= accountShenheService.getTenjk1(startDate, endDate);
		dytenjk[0]= accountShenheService.getDytenjk1(startDate, endDate);
		iosjk[0]= accountShenheService.getIosjk1(startDate, endDate);
		andjk[0]= accountShenheService.getAndjk1(startDate, endDate);

		
		zong[1] = accountShenheService.getZong2(startDate, endDate);
		fifteen[1]= accountShenheService.getFifteen2(startDate, endDate);
		thirty[1]= accountShenheService.getThirty2(startDate, endDate);
		newuser[1]= accountShenheService.getNewuser2(startDate, endDate);
		olduser[1]= accountShenheService.getOlduser2(startDate, endDate);
		havashuju[1]= accountShenheService.getHavashuju2(startDate, endDate);
		noshuju[1]= accountShenheService.getNoshuju2(startDate, endDate);
		manjk[1]= accountShenheService.getManjk2(startDate, endDate);
		womenjk[1]= accountShenheService.getWomenjk2(startDate, endDate);
		pingfen3[1]= accountShenheService.getPingfen32(startDate, endDate);
		pingfen2[1]= accountShenheService.getPingfen22(startDate, endDate);
		pingfen1[1]= accountShenheService.getPingfen12(startDate, endDate);
		pingfenf1[1]= accountShenheService.getPingfenf12(startDate, endDate);
		totaluser[1]= accountShenheService.getTotaluser2(startDate, endDate);
		twicejk[1]= accountShenheService.getTwicejk2(startDate, endDate);
		thirdjk[1]= accountShenheService.getThirdjk2(startDate, endDate);
		fourjk[1]= accountShenheService.getFourjk2(startDate, endDate);
		fivejk[1]= accountShenheService.getFivejk2(startDate, endDate);
		sixjk[1]= accountShenheService.getSixjk2(startDate, endDate);
		sevenjk[1]= accountShenheService.getSevenjk2(startDate, endDate);
		eightjk[1]= accountShenheService.getEightjk2(startDate, endDate);
		ninejk[1]= accountShenheService.getNinejk2(startDate, endDate);
		tenjk[1]= accountShenheService.getTenjk2(startDate, endDate);
		dytenjk[1]= accountShenheService.getDytenjk2(startDate, endDate);
		iosjk[1]= accountShenheService.getIosjk2(startDate, endDate);
		andjk[1]= accountShenheService.getAndjk2(startDate, endDate);
		
		zong[2] = accountShenheService.getZong3(startDate, endDate);
		fifteen[2]= accountShenheService.getFifteen3(startDate, endDate);
		thirty[2]= accountShenheService.getThirty3(startDate, endDate);
		newuser[2]= accountShenheService.getNewuser3(startDate, endDate);
		olduser[2]= accountShenheService.getOlduser3(startDate, endDate);
		havashuju[2]= accountShenheService.getHavashuju3(startDate, endDate);
		noshuju[2]= accountShenheService.getNoshuju3(startDate, endDate);
		manjk[2]= accountShenheService.getManjk3(startDate, endDate);
		womenjk[2]= accountShenheService.getWomenjk3(startDate, endDate);
		pingfen3[2]= accountShenheService.getPingfen33(startDate, endDate);
		pingfen2[2]= accountShenheService.getPingfen23(startDate, endDate);
		pingfen1[2]= accountShenheService.getPingfen13(startDate, endDate);
		pingfenf1[2]= accountShenheService.getPingfenf13(startDate, endDate);
		totaluser[2]= accountShenheService.getTotaluser3(startDate, endDate);
		twicejk[2]= accountShenheService.getTwicejk3(startDate, endDate);
		thirdjk[2]= accountShenheService.getThirdjk3(startDate, endDate);
		fourjk[2]= accountShenheService.getFourjk3(startDate, endDate);
		fivejk[2]= accountShenheService.getFivejk3(startDate, endDate);
		sixjk[2]= accountShenheService.getSixjk3(startDate, endDate);
		sevenjk[2]= accountShenheService.getSevenjk3(startDate, endDate);
		eightjk[2]= accountShenheService.getEightjk3(startDate, endDate);
		ninejk[2]= accountShenheService.getNinejk3(startDate, endDate);
		tenjk[2]= accountShenheService.getTenjk3(startDate, endDate);
		dytenjk[2]= accountShenheService.getDytenjk3(startDate, endDate);
		iosjk[2]= accountShenheService.getIosjk3(startDate, endDate);
		andjk[2]= accountShenheService.getAndjk3(startDate, endDate);
		
		zong[5] = accountShenheService.getZong4(startDate, endDate);
		fifteen[5]= accountShenheService.getFifteen4(startDate, endDate);
		thirty[5]= accountShenheService.getThirty4(startDate, endDate);
		newuser[5]= accountShenheService.getNewuser4(startDate, endDate);
		olduser[5]= accountShenheService.getOlduser4(startDate, endDate);
		havashuju[5]= accountShenheService.getHavashuju4(startDate, endDate);
		noshuju[5]= accountShenheService.getNoshuju4(startDate, endDate);
		manjk[5]= accountShenheService.getManjk4(startDate, endDate);
		womenjk[5]= accountShenheService.getWomenjk4(startDate, endDate);
		pingfen3[5]= accountShenheService.getPingfen34(startDate, endDate);
		pingfen2[5]= accountShenheService.getPingfen24(startDate, endDate);
		pingfen1[5]= accountShenheService.getPingfen14(startDate, endDate);
		pingfenf1[5]= accountShenheService.getPingfenf14(startDate, endDate);
		totaluser[5]= accountShenheService.getTotaluser4(startDate, endDate);
		twicejk[5]= accountShenheService.getTwicejk4(startDate, endDate);
		thirdjk[5]= accountShenheService.getThirdjk4(startDate, endDate);
		fourjk[5]= accountShenheService.getFourjk4(startDate, endDate);
		fivejk[5]= accountShenheService.getFivejk4(startDate, endDate);
		sixjk[5]= accountShenheService.getSixjk4(startDate, endDate);
		sevenjk[5]= accountShenheService.getSevenjk4(startDate, endDate);
		eightjk[5]= accountShenheService.getEightjk4(startDate, endDate);
		ninejk[5]= accountShenheService.getNinejk4(startDate, endDate);
		tenjk[5]= accountShenheService.getTenjk4(startDate, endDate);
		dytenjk[5]= accountShenheService.getDytenjk4(startDate, endDate);
		iosjk[5]= accountShenheService.getIosjk4(startDate, endDate);
		andjk[5]= accountShenheService.getAndjk4(startDate, endDate);
		
		zong[3] = zong[1] + zong[5];
		fifteen[3]= fifteen[1]+fifteen[5];
		thirty[3]= thirty[1]+thirty[5];
		newuser[3]= newuser[1]+newuser[5];
		olduser[3]= olduser[1]+olduser[5];
		havashuju[3]= havashuju[1]+havashuju[5];
		noshuju[3]= noshuju[1]+noshuju[5];
		manjk[3]= manjk[1]+manjk[5];
		womenjk[3]= womenjk[1]+womenjk[5];
		pingfen3[3]= pingfen3[1]+pingfen3[5];
		pingfen2[3]= pingfen2[1]+pingfen2[5];
		pingfen1[3]= pingfen1[1]+pingfen1[5];
		pingfenf1[3]= pingfenf1[1]+pingfenf1[5];
		totaluser[3]= totaluser[1]+totaluser[5];
		twicejk[3]= twicejk[1]+twicejk[5];
		thirdjk[3]= thirdjk[1]+thirdjk[5];
		fourjk[3]= fourjk[1]+fourjk[5];
		fivejk[3]= fivejk[1]+fivejk[5];
		sixjk[3]= sixjk[1]+sixjk[5];
		sevenjk[3]= sevenjk[1]+sevenjk[5];
		eightjk[3]= eightjk[1]+eightjk[5];
		ninejk[3]= ninejk[1]+ninejk[5];
		tenjk[3]= tenjk[1]+tenjk[5];
		dytenjk[3]= dytenjk[1]+dytenjk[5];
		iosjk[3]= iosjk[1]+iosjk[5];
		andjk[3]= andjk[1]+andjk[5];
		
		if(zong[3] != 0){
			zong[4] = zong[2]* 100 / zong[3];
			zong[6] = zong[5]* 100 / zong[3];
		}else{
			zong[4] = 0 ;
			zong[6] = 0 ;
		}
		if(fifteen[3] != 0){
			fifteen[4]= fifteen[2]*100 /fifteen[3];
			fifteen[6]= fifteen[5]*100 /fifteen[3];
		}else{
			fifteen[4] = 0 ;
			fifteen[6] = 0 ;
		}
		if(thirty[3] != 0){
			thirty[4]= thirty[2]*100 /thirty[3];
			thirty[6]= thirty[5]*100/thirty[3];
		}else{
			thirty[4] = 0 ;
			thirty[6] = 0 ;
		}
		if(newuser[3] != 0){
			newuser[4]= newuser[2]*100 /newuser[3];
			newuser[6]= newuser[5]*100 /newuser[3];
		}else{
			newuser[4] = 0 ;
			newuser[6] = 0 ;
		}
		if(olduser[3] != 0){
			olduser[4]= olduser[2]*100 /olduser[3];
			olduser[6]= olduser[5]*100 /olduser[3];
		}else{
			olduser[4] = 0 ;
			olduser[6] = 0 ;
		}
		if(havashuju[3] != 0){
			havashuju[4]= havashuju[2]*100 /havashuju[3];
			havashuju[6]= havashuju[5]*100 /havashuju[3];
		}else{
			havashuju[4] = 0 ;
			havashuju[6] = 0 ;
		}
		if(noshuju[3] != 0){
			noshuju[4]= noshuju[2]*100 /noshuju[3];
			noshuju[6]= noshuju[5]*100 /noshuju[3];
		}else{
			noshuju[4] = 0 ;
			noshuju[6] = 0 ;
		}
		if(manjk[3] != 0){
			manjk[4]= manjk[2]*100 /manjk[3];
			manjk[6]= manjk[5]*100 /manjk[3];
		}else{
			manjk[4] = 0 ;
			manjk[6] = 0 ;
		}
		if(womenjk[3] != 0){
			womenjk[4]= womenjk[2]*100 /womenjk[3];
			womenjk[6]= womenjk[5]*100 /womenjk[3];
		}else{
			womenjk[4] = 0 ;
			womenjk[6] = 0 ;
		}
		if(pingfen3[3] != 0){
			pingfen3[4]= pingfen3[2]*100 /pingfen3[3];
			pingfen3[6]= pingfen3[5]*100 /pingfen3[3];
		}else{
			pingfen3[4] = 0 ;
			pingfen3[6] = 0 ;
		}
		if(pingfen2[3] != 0){
			pingfen2[4]= pingfen2[2]*100 /pingfen2[3];
			pingfen2[6]= pingfen2[5]*100 /pingfen2[3];
		}else{
			pingfen2[4] = 0 ;
			pingfen2[6] = 0 ;
		}
		if(pingfen1[3] != 0){
			pingfen1[4]= pingfen1[2]*100 /pingfen1[3];
			pingfen1[6]= pingfen1[5]*100 /pingfen1[3];
		}else{
			pingfen1[4] = 0 ;
			pingfen1[6] = 0 ;
		}
		if(pingfenf1[3] != 0){
			pingfenf1[4]= pingfenf1[2]*100 /pingfenf1[3];
			pingfenf1[6]= pingfenf1[5]*100 /pingfenf1[3];
		}else{
			pingfenf1[4] = 0 ;
			pingfenf1[6] = 0 ;
		}
		
		if(totaluser[3] != 0){
			totaluser[4]= totaluser[2]*100 /totaluser[3];
			totaluser[6]= totaluser[5]*100 /totaluser[3];
		}else{
			totaluser[4] = 0 ;
			totaluser[6] = 0 ;
		}
		if(twicejk[3] != 0){
			twicejk[4]= twicejk[2]*100 /twicejk[3];
			twicejk[6]= twicejk[5]*100 /twicejk[3];
		}else{
			twicejk[4] = 0 ;
			twicejk[6] = 0 ;
		}
		if(thirdjk[3] != 0){
			thirdjk[4]= thirdjk[2]*100 /thirdjk[3];
			thirdjk[6]= thirdjk[5]*100 /thirdjk[3];
		}else{
			thirdjk[4] = 0 ;
			thirdjk[6] = 0 ;
		}
		if(fourjk[3] != 0){
			fourjk[4]= fourjk[2]*100 /fourjk[3];
			fourjk[6]= fourjk[5]*100 /fourjk[3];
		}else{
			fourjk[4] = 0 ;
			fourjk[6] = 0 ;
		}
		if(fivejk[3] != 0){
			fivejk[4]= fivejk[2]*100 /fivejk[3];
			fivejk[6]= fivejk[5]*100 /fivejk[3];
		}else{
			fivejk[4] = 0 ;
			fivejk[6] = 0 ;
		}
		if(sixjk[3] != 0){
			sixjk[4]= sixjk[2]*100 /sixjk[3];
			sixjk[6]= sixjk[5]*100 /sixjk[3];
		}else{
			sixjk[4] = 0 ;
			sixjk[6] = 0 ;
		}
		if(sevenjk[3] != 0){
			sevenjk[4]= sevenjk[2]*100 /sevenjk[3];
			sevenjk[6]= sevenjk[5]*100 /sevenjk[3];
		}else{
			sevenjk[4] = 0 ;
			sevenjk[6] = 0 ;
		}
		if(eightjk[3] != 0){
			eightjk[4]= eightjk[2]*100 /eightjk[3];
			eightjk[6]= eightjk[5]*100 /eightjk[3];
		}else{
			eightjk[4] = 0 ;
			eightjk[6] = 0 ;
		}
		if(ninejk[3] != 0){
			ninejk[4]= ninejk[2]*100 /ninejk[3];
			ninejk[6]= ninejk[5]*100 /ninejk[3];
		}else{
			ninejk[4] = 0 ;
			ninejk[6] = 0 ;
		}
		if(tenjk[3] != 0){
			tenjk[4]= tenjk[2]*100 /tenjk[3];
			tenjk[6]= tenjk[5]*100 /tenjk[3];
		}else{
			tenjk[4] = 0 ;
			tenjk[6] = 0 ;
		}
		if(dytenjk[3] != 0){
			dytenjk[4]= dytenjk[2]*100 /dytenjk[3];
			dytenjk[6]= dytenjk[5]*100 /dytenjk[3];
		}else{
			dytenjk[4] = 0 ;
			dytenjk[6] = 0 ;
		}
		if(andjk[3] != 0){
			andjk[4]= andjk[2]*100 /andjk[3];
			andjk[6]= andjk[5]*100 /andjk[3];
		}else{
			andjk[4] = 0 ;
			andjk[6] = 0 ;
		}
		if(iosjk[3] != 0){
			iosjk[4]= iosjk[2]*100 /iosjk[3];
			iosjk[6]= iosjk[5]*100 /iosjk[3];
		}else{
			iosjk[4] = 0 ;
			iosjk[6] = 0 ;
		}
		
		//cd.add(Calendar.DATE, -i); // 减一天
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 减一天
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		DataRow row = new DataRow();
		row.set("zong", zong);
		row.set("fifteen", fifteen);
		row.set("thirty", thirty);
		row.set("newuser", newuser);
		row.set("olduser", olduser);
		row.set("havashuju", havashuju);
		row.set("noshuju", noshuju);
		row.set("manjk", manjk);
		row.set("womenjk", womenjk);
		row.set("pingfen3", pingfen3);
		row.set("pingfen2", pingfen2);
		row.set("pingfen1", pingfen1);
		row.set("pingfenf1", pingfenf1);
		row.set("totaluser", totaluser);
		row.set("twicejk", twicejk);
		row.set("thirdjk", thirdjk);
		row.set("fourjk", fourjk);
		row.set("fivejk", fivejk);
		row.set("sixjk", sixjk);
		row.set("sevenjk", sevenjk);
		row.set("eightjk", eightjk);
		row.set("ninejk", ninejk);
		row.set("tenjk", tenjk);
		row.set("dytenjk", dytenjk);
		row.set("iosjk", iosjk);
		row.set("andjk", andjk);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
		return null;
	}
	/**
	 * 得到放款统计
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetFKAccount() throws java.text.ParseException{
		//分页查询所有的后台用户
		int temp = getIntParameter("temp",0);
		int curPage  =getIntParameter("curPage",1);	
		String  tempVelue = getStrParameter("tempvl");
		String name="";
		String phone="";
		if(temp==1){
			name=tempVelue;
		}else if(temp==2){
			phone=tempVelue;
		}
		DBPage page = userService.selectUserListByCondtionFK(curPage, 15, 0, name, phone); 
		List<DataRow> list=page.getData();
		List<ShenKeAccount> shenke=new ArrayList<ShenKeAccount>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String time = sdf.format(new Date());
		Calendar cd = Calendar.getInstance();
		try {
			cd.setTime(sdf.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//cd.add(Calendar.DATE, -i); // 减一天
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 减一天
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		for (int i=0 ;i<list.size();i++) {
			DataRow dataRow = list.get(i);
			int userid=dataRow.getInt("user_id");
			ShenKeAccount account=new ShenKeAccount();
			account.setUserid(userid);
			account.setUserName(dataRow.getString("name"));
			account.setPhone(dataRow.getString("phone"));
			int nowFKCG=accountShenheService.getFKCGCount(userid, nowdate, dateno,name,phone);	  //当天放款成功笔数
			
			int nowCount=nowFKCG;	  //当天总笔数
			//当天线下Agribank
			int offlinezs = accountShenheService.getOfflineZS(userid, nowdate, dateno,name,phone);//线下总数
			int agribank = accountShenheService.getAgriBank(userid, nowdate, dateno,name,phone);
			int vietcombank = accountShenheService.getVietcomBank(userid, nowdate, dateno,name,phone);
			int offSacombank = offlinezs - agribank - vietcombank ;
			//当天线上
			int onSacombank = accountShenheService.getOnlineSacombank(userid, nowdate, dateno,name,phone);
			
			int FKCG=accountShenheService.getFKCGCountZS(userid,name,phone);   //放款成功笔数
			
			int count=FKCG; //总笔数
			
			
			
				
			account.setCount(count);
			account.setOfflinezs(offlinezs);
			account.setAgribank(agribank);
			account.setVietcombank(vietcombank);
			account.setOffSacombank(offSacombank);
			account.setOnSacombank(onSacombank);
			account.setNowCount(nowCount);
			account.setNowOnesh(nowFKCG);
			account.setOne(FKCG);
			shenke.add(account);
			
		}
		int countaa[] = new int[list.size()];
		int useridaa[] = new int[list.size()];
		for(int j=0 ;j<list.size();j++){
			DataRow dataRow1 = list.get(j);
			useridaa[j]=dataRow1.getInt("user_id");
			int FKCG1=accountShenheService.getFKCGCountZS(useridaa[j],name,phone);   //放款成功笔数
			int FKSB1=accountShenheService.getFKSBCountZS(useridaa[j],name,phone);	//放款失败笔数
			countaa[j]=FKCG1+FKSB1; //总笔数
		}
		int nn = countaa.length;
	    for (int m = 0; m < nn - 1; m++) {   
	      for (int n = 0; n < nn - 1; n++) { 
	        if (countaa[n] > countaa[n + 1]) { 
	          int tempaa = countaa[n];   
	          int tempaaa = useridaa[n]; 
	          countaa[n] = countaa[n + 1];   
	          useridaa[n] = useridaa[n + 1]; 
	          countaa[n + 1] = tempaa;   
	          useridaa[n + 1] = tempaaa;   
	        }   
	      }   
	    }
	    for(int k=0 ;k<list.size();k++){
			DataRow row = new DataRow();
			row.set("user_id", useridaa[k]);
			row.set("px", list.size()-k);
			accountShenheService.updatePX(row);	
	    }
		page.setData(shenke);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
		return null;
	}
	/**
	 * 得到放款统计
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetKFHFAccount() throws java.text.ParseException{
		//分页查询所有的后台用户
		int temp = getIntParameter("temp",0);
		int curPage  =getIntParameter("curPage",1);	
		String  tempVelue = getStrParameter("tempvl");
		String name="";
		String phone="";
		if(temp==1){
			name=tempVelue;
		}else if(temp==2){
			phone=tempVelue;
		}
		DBPage page = userService.selectUserListByCondtionKFHF(curPage, 15, 0, name, phone); 
		List<DataRow> list=page.getData();
		List<ShenKeAccount> shenke=new ArrayList<ShenKeAccount>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String time = sdf.format(new Date());
		Calendar cd = Calendar.getInstance();
		try {
			cd.setTime(sdf.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//cd.add(Calendar.DATE, -i); // 减一天
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 减一天
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		for (int i=0 ;i<list.size();i++) {
			DataRow dataRow = list.get(i);
			int userid=dataRow.getInt("user_id");
			ShenKeAccount account=new ShenKeAccount();
			account.setUserid(userid);
			account.setUserName(dataRow.getString("name"));
			account.setPhone(dataRow.getString("phone"));
			int nowFKCG=accountShenheService.getKFHFCGCount(userid, nowdate, dateno,name,phone);	  //当天放款成功笔数
			
			int nowCount=nowFKCG;	  //当天总笔数
			int FKCG=accountShenheService.getKFHFCGCountZS(userid,name,phone);   //放款成功笔数
			int count=FKCG; //总笔数
			int bl = 0;
			
			account.setCount(count);
			account.setYqbl(bl);
			account.setNowCount(nowCount);
			account.setNowOnesh(nowFKCG);
			account.setOne(FKCG);
			shenke.add(account);
			
		}
		int countaa[] = new int[list.size()];
		int useridaa[] = new int[list.size()];
		for(int j=0 ;j<list.size();j++){
			DataRow dataRow1 = list.get(j);
			useridaa[j]=dataRow1.getInt("user_id");
			int FKCG1=accountShenheService.getKFHFCGCountZS(useridaa[j],name,phone);   //放款成功笔数
			countaa[j]=FKCG1; //总笔数
		}
		int nn = countaa.length;
		for (int m = 0; m < nn - 1; m++) {   
			for (int n = 0; n < nn - 1; n++) { 
				if (countaa[n] > countaa[n + 1]) { 
					int tempaa = countaa[n];   
					int tempaaa = useridaa[n]; 
					countaa[n] = countaa[n + 1];   
					useridaa[n] = useridaa[n + 1]; 
					countaa[n + 1] = tempaa;   
					useridaa[n + 1] = tempaaa;   
				}   
			}   
		}
		for(int k=0 ;k<list.size();k++){
			DataRow row = new DataRow();
			row.set("user_id", useridaa[k]);
			row.set("px", list.size()-k);
			accountShenheService.updatePX(row);	
		}
		int wrz=accountShenheService.getWRZCount(nowdate, dateno);
		page.setData(shenke);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("wrz", wrz);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
		return null;
	}
	/**
	 * 得到催收统计
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetCuishouAccount() throws java.text.ParseException{
		//分页查询所有的后台用户
		int temp = getIntParameter("temp",0);
		int curPage  =getIntParameter("curPage",1);	
		String  tempVelue = getStrParameter("tempvl");
		String  startDate =getStrParameter("startDate");
		String  endDate =getStrParameter("endDate");
		
		String name="";
		String phone="";
		String yuqts="";
		if(temp==1){
			name=tempVelue;
		}else if(temp==2){
			phone=tempVelue;
		}else if(temp==3){
			yuqts=tempVelue;
		}
		DBPage page = userService.selectUserListByCondtionCS(curPage, 25, 0, name, phone); 
		List<DataRow> list=page.getData();
		List<ShenKeAccount> shenke=new ArrayList<ShenKeAccount>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String time = sdf.format(new Date());
		Calendar cd = Calendar.getInstance();
		try {
			cd.setTime(sdf.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//cd.add(Calendar.DATE, -i); // 减一天
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 减一天
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		DecimalFormat famt = new DecimalFormat("###,###");
		DecimalFormat df = new DecimalFormat( "0.00"); //设置double类型小数点后位数格式
		double onemoney = 0; 
		double twomoney = 0; 
		double threemoney = 0; 
		double threeTGmoney = 0; 
		double totalCSmoney=0;  //总催收金额
		double jrtotalCSmoney=0;  //总催收金额
		double jrtotalCSmoney1=0;  //总催收金额
		double totalYCSmoney=0;  //总已催收金额
		double jrtotalYCSmoney=0;  //总已催收金额
		double jrtotalYCSmoney1=0;  //总已催收金额
		double jrtotalYCSmoney2=0;  //总已催收金额
		double jrtotalYCSmoney3=0;  //总已催收金额
		double totalCSmoney1=0;  //总已催收金额
		double totalYCSmoney2=0;  //总已催收金额
		double totalYCSmoney3=0;  //总已催收金额
		double ycsbl=0;  //总已催收比例
		double ycsblyq=0;  //总已催收比例
		double jrycsbl=0;  //总已催收比例
		for (int i=0 ;i<list.size();i++) {
			DataRow dataRow = list.get(i);
			int userid=dataRow.getInt("user_id");
			ShenKeAccount account=new ShenKeAccount();
			account.setUserid(userid);
			account.setUserName(dataRow.getString("name"));
			account.setPhone(dataRow.getString("phone"));
			int jrnowOnesh=accountShenheService.getJRCSNowOneCount(userid, nowdate, dateno,name,phone);	  //当天M1笔数
			int nowOnesh=accountShenheService.getCSNowOneCount(userid, nowdate, dateno,name,phone);	  //当天M1笔数
			int nowTwosh=accountShenheService.getCSNowTwoCount(userid, nowdate, dateno,name,phone);	//当天M2笔数
			int nowThreesh=accountShenheService.getCSNowThreeCount(userid, nowdate, dateno,name,phone);	//当天M3笔数
			int nowFoursh=accountShenheService.getCSNowFourCount(userid, nowdate, dateno, name, phone); //当天M4笔数
			int count=accountShenheService.getCSNowCount(userid,startDate, endDate,name, phone);	  //总笔数
			int jrcount=accountShenheService.getDYCSNowCount(userid,startDate, endDate,nowdate,name, phone);//当月新增逾期总笔数
			int jrycscount=accountShenheService.getDYCSCount(userid,startDate, endDate,nowdate,name, phone);//当月已催收笔数
			int one=accountShenheService.getCSOneCount(userid,startDate, endDate,name,phone);   //M1笔数
			int two=accountShenheService.getCSTwoCount(userid,startDate, endDate,name,phone);	//M2笔数
			int three=accountShenheService.getCSThreeCount(userid,startDate, endDate,name,phone); //M3笔数
			int threeTG = accountShenheService.getCSFourCount(userid,startDate, endDate, name, phone);//M4笔数
			int nowCount=accountShenheService.getCSDateNowCount(userid,nowdate, dateno,name, phone); //总笔数
			onemoney = accountShenheService.getOneMoney(userid,startDate, endDate,name, phone);
			twomoney = accountShenheService.getTwoMoney(userid,startDate, endDate,name, phone);
			threemoney = accountShenheService.getThreeMoney(userid,startDate, endDate,name, phone);
		    threeTGmoney = accountShenheService.getThreeTGMoney(userid,startDate, endDate,name, phone);
			totalCSmoney = accountShenheService.getCSJE(userid,startDate, endDate,name, phone,yuqts); //总催收金额
			jrtotalCSmoney1 = accountShenheService.getDYCSJE(userid,startDate, endDate,nowdate,name, phone,yuqts); //当月催收总金额
			totalYCSmoney = accountShenheService.getYCSJE(userid,startDate, endDate, name, phone,yuqts);//总已催收金额
			totalYCSmoney2 = accountShenheService.getYCSJEYQ(userid,startDate, endDate, name, phone,yuqts);//总已催收延期金额
			totalYCSmoney3 = accountShenheService.getYCSJEBF(userid,startDate, endDate, name, phone,yuqts);//总已催收部分金额
			int countyq = accountShenheService.getYCSJEYQCount(userid,startDate, endDate, name, phone,yuqts);//总已催收延期金额
			totalCSmoney1 = totalCSmoney - totalYCSmoney2 ; // 总催收金额（不包括延期）
			totalYCSmoney  = totalYCSmoney + totalYCSmoney3;
			jrtotalCSmoney = jrtotalCSmoney1; // 总催收金额（不包括延期）
			jrtotalYCSmoney1 = accountShenheService.getDYYCSJE(userid,startDate, endDate,nowdate, name, phone,yuqts);//当月总已催收金额
			jrtotalYCSmoney2 = accountShenheService.getDYYCSJE1(userid,startDate, endDate, name, phone,yuqts);//当月总已催收金额（部分延期）
			jrtotalYCSmoney3 = accountShenheService.getDYYCSJE2(userid,startDate, endDate,nowdate, name, phone,yuqts);//当月总已催收金额（部分延期）
			jrtotalYCSmoney = jrtotalYCSmoney1 + jrtotalYCSmoney3;
			if(totalCSmoney1 != 0){
				ycsbl = totalYCSmoney * 100 / totalCSmoney1 ;
			}else{
				ycsbl = 0;
			}
			//延期的比例
			if(totalCSmoney != 0){
				ycsblyq = totalYCSmoney2 * 100 / totalCSmoney ;
			}else{
				ycsblyq = 0;
			}
			if(jrtotalCSmoney != 0){
				jrycsbl = jrtotalYCSmoney * 100 / jrtotalCSmoney ;
			}else{
				jrycsbl = 0;
			}
			account.setNowCount(nowCount);
			account.setCount(count);
			account.setCountyq(countyq);
			account.setJrcount(jrcount);
			account.setJrycscount(jrycscount);
			account.setNowFoursh(nowFoursh);
			account.setJrnowOnesh(jrnowOnesh);
			account.setNowOnesh(nowOnesh);
			account.setNowThreesh(nowThreesh);
			account.setNowTwosh(nowTwosh);
			account.setOne(one);
			account.setOnemoney(famt.format(onemoney));
			account.setTwomoney(famt.format(twomoney));
			account.setThreemoney(famt.format(threemoney));
			account.setThreeTGmoney(famt.format(threeTGmoney));
			account.setThree(three);
			account.setThreeTG(threeTG);
			account.setTwo(two);
			account.setYcsbl(ycsbl);
			account.setYcsblyq(ycsblyq);
			account.setJrycsbl(jrycsbl);
			account.setTotalCSmoney(famt.format(totalCSmoney));
			account.setTotalCSmoney1(famt.format(totalCSmoney1));
			account.setJrtotalCSmoney(famt.format(jrtotalCSmoney));
			account.setTotalYCSmoney(famt.format(totalYCSmoney));
			account.setTotalYCSmoney2(famt.format(totalYCSmoney2));
			account.setJrtotalYCSmoney(famt.format(jrtotalYCSmoney));
			shenke.add(account);
			
		}
		double countaa[] = new double[list.size()];
		int useridaa[] = new int[list.size()];
		for(int j=0 ;j<list.size();j++){
			DataRow dataRow1 = list.get(j);
			useridaa[j]=dataRow1.getInt("user_id");
			totalYCSmoney = accountShenheService.getYCSJE(useridaa[j],startDate, endDate, name, phone,yuqts);//总已催收金额
			countaa[j]=totalYCSmoney; //总笔数
		}
		int nn = countaa.length;
	    for (int m = 0; m < nn - 1; m++) {   
	      for (int n = 0; n < nn - 1; n++) { 
	        if (countaa[n] > countaa[n + 1]) { 
	          double tempaa = countaa[n];   
	          int tempaaa = useridaa[n]; 
	          countaa[n] = countaa[n + 1];   
	          useridaa[n] = useridaa[n + 1]; 
	          countaa[n + 1] = tempaa;   
	          useridaa[n + 1] = tempaaa;   
	        }   
	      }   
	    }
	    for(int k=0 ;k<list.size();k++){
			DataRow row = new DataRow();
			row.set("user_id", useridaa[k]);
			row.set("px", list.size()-k);
			accountShenheService.updatePX(row);	
	    }
		page.setData(shenke);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
		return null;
	}
	/**
	 * 得到催收统计M123
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetCuishouAccount123() throws java.text.ParseException{
		//分页查询所有的后台用户
		int temp = getIntParameter("temp",0);
		int curPage  =getIntParameter("curPage",1);	
		String  tempVelue = getStrParameter("tempvl");
		String  startDate =getStrParameter("startDate");
		String  endDate =getStrParameter("endDate");
		int  recode =getIntParameter("recode",0);
		
		String name="";
		String phone="";
		String yuqts="";
		if(temp==1){
			name=tempVelue;
		}else if(temp==2){
			phone=tempVelue;
		}else if(temp==3){
			yuqts=tempVelue;
		}
		DBPage page = userService.selectUserListByCondtionCSM1(curPage,40, 0, name, phone); 
		List<DataRow> list=page.getData();
		List<ShenKeAccount> shenke=new ArrayList<ShenKeAccount>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String time = sdf.format(new Date());
		Calendar cd = Calendar.getInstance();
		try {
			cd.setTime(sdf.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//cd.add(Calendar.DATE, -i); // 减一天
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 减一天
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		DecimalFormat famt = new DecimalFormat("###,###");
		DecimalFormat df = new DecimalFormat( "0.00"); //设置double类型小数点后位数格式
		double onemoney = 0; 
		
		double totalCSmoney=0;  //总催收金额
		
		double totalCSmoneySS=0;  //总催收金额
		
		double jrtotalCSmoney=0;  //总催收金额
		double jrtotalCSmoney1=0;  //总催收金额
		
		double totalYCSmoney=0;  //总已催收金额
		double totalYCSmoneySS=0;  //总已催收金额
		
		double jrtotalYCSmoney=0;  //总已催收金额
		double jrtotalYCSmoney1=0;  //总已催收金额
		double jrtotalYCSmoney2=0;  //总已催收金额
		double jrtotalYCSmoney3=0;  //总已催收金额
		double totalCSmoney1=0;  //总已催收金额
		
		double totalYCSmoney2=0;  //总已催收金额
		
		double totalYCSmoney2SS=0;  //总已催收金额
		
		double totalYCSmoney3=0;  //总已催收金额
		
		double totalYCSmoney3SS=0;  //总已催收金额
		
		double totalYCSmoneyDT=0;  //当天总已催收金额
		
		double ycsbl=0;  //总已催收比例
		double kpi=0;  //总已催收比例
		double ycsblyq=0;  //总已催收比例
		double jrycsbl=0;  //总已催收比例
		
		int number = Integer.parseInt(time.substring(3, 5));
		if(recode == 0){
			for (int i=0 ;i<list.size();i++) {
				DataRow dataRow = list.get(i);
				int userid=dataRow.getInt("user_id");
				ShenKeAccount account=new ShenKeAccount();
				account.setUserid(userid);
				account.setUserName(dataRow.getString("name"));
				account.setPhone(dataRow.getString("phone"));
				
				//KPI
				int countSS=accountShenheService.getCSNowCountM1SS(userid,startDate, endDate,name, phone,number);	  //总笔数
				
				int oneSS=accountShenheService.getCSOneCountM1SS(userid,startDate, endDate,name,phone,number);   //M1笔数
				
				SimpleDateFormat sdf111 = new SimpleDateFormat("yyyy-MM-dd");
				String time111 = sdf111.format(new Date());
				
				totalCSmoneySS = accountShenheService.getYQ1T(userid,startDate, endDate,name, phone,yuqts,number); //总催收金额
				totalYCSmoneySS = accountShenheService.getYQ2T(userid,startDate, endDate, name, phone,yuqts,number);//总已催收金额
				
				
				//当天
				int nowCount=(int)accountShenheService.getYQ3T(userid,startDate, endDate, name, phone,yuqts,number); //总笔数
				totalYCSmoneyDT = totalCSmoneySS +  totalYCSmoneySS +  nowCount ;
				

				/*if(totalYCSmoneyDT != 0 && countSS != 0 ){
					kpi = totalYCSmoneyDT * 100 / countSS ;
				}else{
					kpi = 0;
				}*/
				//总数
				int count=0;	  //总笔数
				int one=0;   //M1笔数
				onemoney =0;
				
				int jrcount=0;//当月新增逾期总笔数
				int jrycscount=0;//当月已催收笔数
				
				totalCSmoney = 0; //总催收金额
				
				jrtotalCSmoney1 = 0; //当月催收总金额
				
				totalYCSmoney = 0;//总已催收金额
				
				//totalYCSmoney2 = accountShenheService.getYCSJEYQM1(userid,startDate, endDate, name, phone,yuqts,number);//总已催收延期金额
				
				totalYCSmoney3 =0;//总已催收部分金额
				
				//int countyq = accountShenheService.getYCSJEYQCountM1(userid,startDate, endDate, name, phone,yuqts,number);//总已催收延期金额
				
				
				totalYCSmoney  = totalYCSmoney + totalYCSmoney3;
				
				jrtotalCSmoney = jrtotalCSmoney1; // 总催收金额（不包括延期）
				jrtotalYCSmoney1 = 0;//当月总已催收金额
				jrtotalYCSmoney2 = 0;//当月总已催收金额（部分延期）
				jrtotalYCSmoney3 = 0;//当月总已催收金额（部分延期）
				jrtotalYCSmoney = jrtotalYCSmoney1 + jrtotalYCSmoney3;
				/*if(totalCSmoney1 != 0){
					ycsbl = totalYCSmoney * 100 / totalCSmoney1 ;
				}else{
					ycsbl = 0;
				}*/
				//延期的比例
				/*if(totalCSmoney != 0){
					ycsblyq = totalYCSmoney2 * 100 / totalCSmoney ;
				}else{
					ycsblyq = 0;
				}
				if(jrtotalCSmoney != 0){
					jrycsbl = jrtotalYCSmoney * 100 / jrtotalCSmoney ;
				}else{
					jrycsbl = 0;
				}*/
				account.setNowCount(nowCount);
				account.setCount(count);
				account.setCountSS(countSS);
				//account.setCountyq(countyq);
				account.setJrcount(jrcount);
				account.setJrycscount(jrycscount);
				
				//account.setNowOnesh(nowOnesh);
			
				account.setOne(one);
				account.setOneSS(oneSS);
				account.setOnemoney(famt.format(onemoney));
				
				account.setYcsbl(ycsbl);
				account.setKpi(kpi);
				account.setYcsblyq(ycsblyq);
				account.setJrycsbl(jrycsbl);
				account.setTotalCSmoney(famt.format(totalCSmoney));
				account.setTotalYCSmoneyDT(famt.format(totalYCSmoneyDT));
				account.setTotalCSmoneySS(famt.format(totalCSmoneySS));
				account.setTotalCSmoney1(famt.format(totalCSmoney1));
				account.setJrtotalCSmoney(famt.format(jrtotalCSmoney));
				account.setTotalYCSmoney(famt.format(totalYCSmoney));
				account.setTotalYCSmoneySS(famt.format(totalYCSmoneySS));
				account.setTotalYCSmoney2(famt.format(totalYCSmoney2));
				account.setJrtotalYCSmoney(famt.format(jrtotalYCSmoney));
				shenke.add(account);
				
			}
		}else if(recode == 1){
			for (int i=0 ;i<list.size();i++) {
				DataRow dataRow = list.get(i);
				int userid=dataRow.getInt("user_id");
				ShenKeAccount account=new ShenKeAccount();
				account.setUserid(userid);
				account.setUserName(dataRow.getString("name"));
				account.setPhone(dataRow.getString("phone"));
				/*//KPI
				int countSS=accountShenheService.getCSNowCountM1SS(userid,startDate, endDate,name, phone,number);	  //总笔数
				
				int oneSS=accountShenheService.getCSOneCountM1SS(userid,startDate, endDate,name,phone,number);   //M1笔数
				totalCSmoneySS = accountShenheService.getCSJEM1SS(userid,startDate, endDate,name, phone,yuqts,number); //总催收金额
				totalYCSmoneySS = accountShenheService.getYCSJEM1SS(userid,startDate, endDate, name, phone,yuqts,number);//总已催收金额
				totalYCSmoney3SS = accountShenheService.getYCSJEBFM1SS(userid,startDate, endDate, name, phone,yuqts,number);//总已催收部分金额
				totalYCSmoneySS  = totalYCSmoneySS + totalYCSmoney3SS;
				if(totalCSmoneySS != 0){
					kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
				}else{
					kpi = 0;
				}*/
				//KPI
				int countSS=accountShenheService.getCSNowCountM1SS(userid,startDate, endDate,name, phone,number);	  //总笔数
				
				int oneSS=accountShenheService.getCSOneCountM1SS(userid,startDate, endDate,name,phone,number);   //M1笔数
				
				SimpleDateFormat sdf111 = new SimpleDateFormat("yyyy-MM-dd");
				String time111 = sdf111.format(new Date());
				
				totalCSmoneySS = accountShenheService.getCSJEM1SSBG(userid,startDate, endDate,name, phone,yuqts,number); //总催收金额
				totalYCSmoneySS = accountShenheService.getYCSJEM1SSBG(userid,startDate, endDate, name, phone,yuqts,number);//总已催收金额
				
				if(totalCSmoneySS != 0){
					kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
				}else{
					kpi = 0;
				}
				//当天
				int nowCount=accountShenheService.getCSDateNowCountM1(userid,nowdate, dateno,name, phone); //总笔数
				int nowOnesh=accountShenheService.getCSNowOneCountM1(userid, nowdate, dateno,name,phone);	  //当天M1笔数
				totalYCSmoneyDT = accountShenheService.getCSDateNowCountDTJE(userid,nowdate, dateno,name, phone); //当天催收全额还款金额
				//总数
				int count=accountShenheService.getCSNowCountM1(userid,startDate, endDate,name, phone);	  //总笔数
				int one=accountShenheService.getCSOneCountM1(userid,startDate, endDate,name,phone);   //M1笔数
				onemoney = accountShenheService.getOneMoneyM1(userid,startDate, endDate,name, phone);
				
				int jrcount=accountShenheService.getDYCSNowCountM1(userid,startDate, endDate,nowdate,name, phone);//当月新增逾期总笔数
				int jrycscount=accountShenheService.getDYCSCountM1(userid,startDate, endDate,nowdate,name, phone);//当月已催收笔数
				
				totalCSmoney = accountShenheService.getCSJEM1(userid,startDate, endDate,name, phone,yuqts); //总催收金额
				
				jrtotalCSmoney1 = accountShenheService.getDYCSJEM1(userid,startDate, endDate,nowdate,name, phone,yuqts); //当月催收总金额
				
				totalYCSmoney = accountShenheService.getYCSJEM1(userid,startDate, endDate, name, phone,yuqts);//总已催收金额
				
				totalYCSmoney2 = accountShenheService.getYCSJEYQM1(userid,startDate, endDate, name, phone,yuqts,number);//总已催收延期金额
				
				totalYCSmoney3 = accountShenheService.getYCSJEBFM1(userid,startDate, endDate, name, phone,yuqts);//总已催收部分金额
				
				int countyq = accountShenheService.getYCSJEYQCountM1(userid,startDate, endDate, name, phone,yuqts,number);//总已催收延期金额
				totalCSmoney1 = totalCSmoney - totalYCSmoney2 ; // 总催收金额（不包括延期）
				totalYCSmoney  = totalYCSmoney + totalYCSmoney3;
				
				jrtotalCSmoney = jrtotalCSmoney1; // 总催收金额（不包括延期）
				jrtotalYCSmoney1 = accountShenheService.getDYYCSJEM1(userid,startDate, endDate,nowdate, name, phone,yuqts);//当月总已催收金额
				jrtotalYCSmoney2 = accountShenheService.getDYYCSJE1M1(userid,startDate, endDate, name, phone,yuqts);//当月总已催收金额（部分延期）
				jrtotalYCSmoney3 = accountShenheService.getDYYCSJE2M1(userid,startDate, endDate,nowdate, name, phone,yuqts);//当月总已催收金额（部分延期）
				jrtotalYCSmoney = jrtotalYCSmoney1 + jrtotalYCSmoney3;
				if(totalCSmoney1 != 0){
					ycsbl = totalYCSmoney * 100 / totalCSmoney1 ;
				}else{
					ycsbl = 0;
				}
				
				//延期的比例
				if(totalCSmoney != 0){
					ycsblyq = totalYCSmoney2 * 100 / totalCSmoney ;
				}else{
					ycsblyq = 0;
				}
				if(jrtotalCSmoney != 0){
					jrycsbl = jrtotalYCSmoney * 100 / jrtotalCSmoney ;
				}else{
					jrycsbl = 0;
				}
				account.setNowCount(nowCount);
				account.setCount(count);
				account.setCountSS(countSS);
				account.setCountyq(countyq);
				account.setJrcount(jrcount);
				account.setJrycscount(jrycscount);
				
				account.setNowOnesh(nowOnesh);
			
				account.setOne(one);
				account.setOneSS(oneSS);
				account.setOnemoney(famt.format(onemoney));
				
				account.setYcsbl(ycsbl);
				account.setKpi(kpi);
				account.setYcsblyq(ycsblyq);
				account.setJrycsbl(jrycsbl);
				account.setTotalCSmoney(famt.format(totalCSmoney));
				account.setTotalYCSmoneyDT(famt.format(totalYCSmoneyDT));
				account.setTotalCSmoneySS(famt.format(totalCSmoneySS));
				account.setTotalCSmoney1(famt.format(totalCSmoney1));
				account.setJrtotalCSmoney(famt.format(jrtotalCSmoney));
				account.setTotalYCSmoney(famt.format(totalYCSmoney));
				account.setTotalYCSmoneySS(famt.format(totalYCSmoneySS));
				account.setTotalYCSmoney2(famt.format(totalYCSmoney2));
				account.setJrtotalYCSmoney(famt.format(jrtotalYCSmoney));
				shenke.add(account);
				
			}
		}
		double countaa[] = new double[list.size()];
		int useridaa[] = new int[list.size()];
		for(int j=0 ;j<list.size();j++){
			DataRow dataRow1 = list.get(j);
			useridaa[j]=dataRow1.getInt("user_id");
			totalYCSmoney = accountShenheService.getYCSJE(useridaa[j],startDate, endDate, name, phone,yuqts);//总已催收金额
			countaa[j]=totalYCSmoney; //总笔数
		}
		int nn = countaa.length;
		for (int m = 0; m < nn - 1; m++) {   
			for (int n = 0; n < nn - 1; n++) { 
				if (countaa[n] > countaa[n + 1]) { 
					double tempaa = countaa[n];   
					int tempaaa = useridaa[n]; 
					countaa[n] = countaa[n + 1];   
					useridaa[n] = useridaa[n + 1]; 
					countaa[n + 1] = tempaa;   
					useridaa[n + 1] = tempaaa;   
				}   
			}   
		}
		for(int k=0 ;k<list.size();k++){
			DataRow row = new DataRow();
			row.set("user_id", useridaa[k]);
			row.set("px", list.size()-k);
			accountShenheService.updatePX(row);	
		}
		page.setData(shenke);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
		return null;
	}
	/**
	 * 得到催收统计M0
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetCuishouAccountM0() throws java.text.ParseException{
		//分页查询所有的后台用户
		int temp = getIntParameter("temp",0);
		int curPage  =getIntParameter("curPage",1);	
		String  tempVelue = getStrParameter("tempvl");
		String  startDate =getStrParameter("startDate");
		String  endDate =getStrParameter("endDate");
		int  recode =getIntParameter("recode",0);
		
		String name="";
		String phone="";
		String yuqts="";
		if(temp==1){
			name=tempVelue;
		}else if(temp==2){
			phone=tempVelue;
		}else if(temp==3){
			yuqts=tempVelue;
		}
		DBPage page = userService.selectUserListByCondtionCSM0(curPage,40, 0, name, phone); 
		List<DataRow> list=page.getData();
		List<ShenKeAccount> shenke=new ArrayList<ShenKeAccount>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String time = sdf.format(new Date());
		Calendar cd = Calendar.getInstance();
		try {
			cd.setTime(sdf.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//cd.add(Calendar.DATE, -i); // 减一天
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 减一天
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		DecimalFormat famt = new DecimalFormat("###,###");
		DecimalFormat df = new DecimalFormat( "0.00"); //设置double类型小数点后位数格式
		double onemoney = 0; 
		
		double totalCSmoney=0;  //总催收金额
		
		double totalCSmoneySS=0;  //总催收金额
		
		double jrtotalCSmoney=0;  //总催收金额
		double jrtotalCSmoney1=0;  //总催收金额
		
		double totalYCSmoney=0;  //总已催收金额
		double totalYCSmoneySS=0;  //总已催收金额
		
		double jrtotalYCSmoney=0;  //总已催收金额
		double jrtotalYCSmoney1=0;  //总已催收金额
		double jrtotalYCSmoney2=0;  //总已催收金额
		double jrtotalYCSmoney3=0;  //总已催收金额
		double totalCSmoney1=0;  //总已催收金额
		
		double totalYCSmoney2=0;  //总已催收金额
		
		double totalYCSmoney2SS=0;  //总已催收金额
		
		double totalYCSmoney3=0;  //总已催收金额
		
		double totalYCSmoney3SS=0;  //总已催收金额
		
		double totalYCSmoneyDT=0;  //当天总已催收金额
		
		double ycsbl=0;  //总已催收比例
		double kpi=0;  //总已催收比例
		double ycsblyq=0;  //总已催收比例
		double jrycsbl=0;  //总已催收比例
		
		int number = Integer.parseInt(time.substring(3, 5));
		if(recode == 0){
			for (int i=0 ;i<list.size();i++) {
				DataRow dataRow = list.get(i);
				int userid=dataRow.getInt("user_id");
				ShenKeAccount account=new ShenKeAccount();
				account.setUserid(userid);
				account.setUserName(dataRow.getString("name"));
				account.setPhone(dataRow.getString("phone"));
				/*//KPI
				int countSS=accountShenheService.getCSNowCountM1SS(userid,startDate, endDate,name, phone,number);	  //总笔数
				
				int oneSS=accountShenheService.getCSOneCountM1SS(userid,startDate, endDate,name,phone,number);   //M1笔数
				totalCSmoneySS = accountShenheService.getCSJEM1SS(userid,startDate, endDate,name, phone,yuqts,number); //总催收金额
				totalYCSmoneySS = accountShenheService.getYCSJEM1SS(userid,startDate, endDate, name, phone,yuqts,number);//总已催收金额
				totalYCSmoney3SS = accountShenheService.getYCSJEBFM1SS(userid,startDate, endDate, name, phone,yuqts,number);//总已催收部分金额
				totalYCSmoneySS  = totalYCSmoneySS + totalYCSmoney3SS;
				if(totalCSmoneySS != 0){
					kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
				}else{
					kpi = 0;
				}*/
				//KPI
				int countSS=accountShenheService.getCSNowCountM0SS(userid,startDate, endDate,name, phone,number);	  //总笔数
				
				int oneSS=accountShenheService.getCSOneCountM0SS(userid,startDate, endDate,name,phone,number);   //M1笔数
				
				SimpleDateFormat sdf111 = new SimpleDateFormat("yyyy-MM-dd");
				String time111 = sdf111.format(new Date());
				
				totalCSmoneySS = accountShenheService.getCSJEM1SSBG(userid,startDate, endDate,name, phone,yuqts,number); //总催收金额
				totalYCSmoneySS = accountShenheService.getYCSJEM1SSBG(userid,startDate, endDate, name, phone,yuqts,number);//总已催收金额
				
				if(totalCSmoneySS != 0){
					kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
				}else{
					kpi = 0;
				}
				
				//当天
				int nowCount=accountShenheService.getCSDateNowCountM0(userid,nowdate, dateno,name, phone); //总笔数
				totalYCSmoneyDT = accountShenheService.getCSDateNowCountDTJEM0(userid,nowdate, dateno,name, phone); //当天催收全额还款金额
				
				//总数
				int count=0;	  //总笔数
				int one=0;   //M1笔数
				onemoney =0;
				
				int jrcount=0;//当月新增逾期总笔数
				int jrycscount=0;//当月已催收笔数
				
				totalCSmoney = 0; //总催收金额
				
				jrtotalCSmoney1 = 0; //当月催收总金额
				
				totalYCSmoney = 0;//总已催收金额
				
				totalYCSmoney2 = accountShenheService.getYCSJEYQM1(userid,startDate, endDate, name, phone,yuqts,number);//总已催收延期金额
				
				totalYCSmoney3 =0;//总已催收部分金额
				
				int countyq = accountShenheService.getYCSJEYQCountM1(userid,startDate, endDate, name, phone,yuqts,number);//总已催收延期金额
				
				
				totalYCSmoney  = totalYCSmoney + totalYCSmoney3;
				
				jrtotalCSmoney = jrtotalCSmoney1; // 总催收金额（不包括延期）
				jrtotalYCSmoney1 = 0;//当月总已催收金额
				jrtotalYCSmoney2 = 0;//当月总已催收金额（部分延期）
				jrtotalYCSmoney3 = 0;//当月总已催收金额（部分延期）
				jrtotalYCSmoney = jrtotalYCSmoney1 + jrtotalYCSmoney3;
				if(totalCSmoney1 != 0){
					ycsbl = totalYCSmoney * 100 / totalCSmoney1 ;
				}else{
					ycsbl = 0;
				}
				//延期的比例
				if(totalCSmoney != 0){
					ycsblyq = totalYCSmoney2 * 100 / totalCSmoney ;
				}else{
					ycsblyq = 0;
				}
				if(jrtotalCSmoney != 0){
					jrycsbl = jrtotalYCSmoney * 100 / jrtotalCSmoney ;
				}else{
					jrycsbl = 0;
				}
				account.setNowCount(nowCount);
				account.setCount(count);
				account.setCountSS(countSS);
				account.setCountyq(countyq);
				account.setJrcount(jrcount);
				account.setJrycscount(jrycscount);
				
				//account.setNowOnesh(nowOnesh);
			
				account.setOne(one);
				account.setOneSS(oneSS);
				account.setOnemoney(famt.format(onemoney));
				
				account.setYcsbl(ycsbl);
				account.setKpi(kpi);
				account.setYcsblyq(ycsblyq);
				account.setJrycsbl(jrycsbl);
				account.setTotalCSmoney(famt.format(totalCSmoney));
				account.setTotalYCSmoneyDT(famt.format(totalYCSmoneyDT));
				account.setTotalCSmoneySS(famt.format(totalCSmoneySS));
				account.setTotalCSmoney1(famt.format(totalCSmoney1));
				account.setJrtotalCSmoney(famt.format(jrtotalCSmoney));
				account.setTotalYCSmoney(famt.format(totalYCSmoney));
				account.setTotalYCSmoneySS(famt.format(totalYCSmoneySS));
				account.setTotalYCSmoney2(famt.format(totalYCSmoney2));
				account.setJrtotalYCSmoney(famt.format(jrtotalYCSmoney));
				shenke.add(account);
				
			}
		}else if(recode == 1){
			for (int i=0 ;i<list.size();i++) {
				DataRow dataRow = list.get(i);
				int userid=dataRow.getInt("user_id");
				ShenKeAccount account=new ShenKeAccount();
				account.setUserid(userid);
				account.setUserName(dataRow.getString("name"));
				account.setPhone(dataRow.getString("phone"));
				/*//KPI
				int countSS=accountShenheService.getCSNowCountM1SS(userid,startDate, endDate,name, phone,number);	  //总笔数
				
				int oneSS=accountShenheService.getCSOneCountM1SS(userid,startDate, endDate,name,phone,number);   //M1笔数
				totalCSmoneySS = accountShenheService.getCSJEM1SS(userid,startDate, endDate,name, phone,yuqts,number); //总催收金额
				totalYCSmoneySS = accountShenheService.getYCSJEM1SS(userid,startDate, endDate, name, phone,yuqts,number);//总已催收金额
				totalYCSmoney3SS = accountShenheService.getYCSJEBFM1SS(userid,startDate, endDate, name, phone,yuqts,number);//总已催收部分金额
				totalYCSmoneySS  = totalYCSmoneySS + totalYCSmoney3SS;
				if(totalCSmoneySS != 0){
					kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
				}else{
					kpi = 0;
				}*/
				//KPI
				int countSS=accountShenheService.getCSNowCountM0SS(userid,startDate, endDate,name, phone,number);	  //总笔数
				
				int oneSS=accountShenheService.getCSOneCountM0SS(userid,startDate, endDate,name,phone,number);   //M1笔数
				
				SimpleDateFormat sdf111 = new SimpleDateFormat("yyyy-MM-dd");
				String time111 = sdf111.format(new Date());
				
				totalCSmoneySS = accountShenheService.getCSJEM1SSBG(userid,startDate, endDate,name, phone,yuqts,number); //总催收金额
				totalYCSmoneySS = accountShenheService.getYCSJEM1SSBG(userid,startDate, endDate, name, phone,yuqts,number);//总已催收金额
				
				if(totalCSmoneySS != 0){
					kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
				}else{
					kpi = 0;
				}
				//当天
				int nowCount=accountShenheService.getCSDateNowCountM0(userid,nowdate, dateno,name, phone); //总笔数
				int nowOnesh=accountShenheService.getCSNowOneCountM0(userid, nowdate, dateno,name,phone);	  //当天M1笔数
				totalYCSmoneyDT = accountShenheService.getCSDateNowCountDTJEM0(userid,nowdate, dateno,name, phone); //当天催收全额还款金额
				//总数
				int count=accountShenheService.getCSNowCountM0(userid,startDate, endDate,name, phone);	  //总笔数
				int one=accountShenheService.getCSOneCountM0(userid,startDate, endDate,name,phone);   //M1笔数
				onemoney = accountShenheService.getOneMoneyM0(userid,startDate, endDate,name, phone);
				
				int jrcount=accountShenheService.getDYCSNowCountM0(userid,startDate, endDate,nowdate,name, phone);//当月新增逾期总笔数
				int jrycscount=accountShenheService.getDYCSCountM0(userid,startDate, endDate,nowdate,name, phone);//当月已催收笔数
				
				totalCSmoney = accountShenheService.getCSJEM0(userid,startDate, endDate,name, phone,yuqts); //总催收金额
				
				jrtotalCSmoney1 = accountShenheService.getDYCSJEM0(userid,startDate, endDate,nowdate,name, phone,yuqts); //当月催收总金额
				
				totalYCSmoney = accountShenheService.getYCSJEM0(userid,startDate, endDate, name, phone,yuqts);//总已催收金额
				
				totalYCSmoney2 = accountShenheService.getYCSJEYQM0(userid,startDate, endDate, name, phone,yuqts,number);//总已催收延期金额
				
				totalYCSmoney3 = accountShenheService.getYCSJEBFM0(userid,startDate, endDate, name, phone,yuqts);//总已催收部分金额
				
				int countyq = accountShenheService.getYCSJEYQCountM0(userid,startDate, endDate, name, phone,yuqts,number);//总已催收延期金额
				totalCSmoney1 = totalCSmoney - totalYCSmoney2 ; // 总催收金额（不包括延期）
				totalYCSmoney  = totalYCSmoney + totalYCSmoney3;
				
				jrtotalCSmoney = jrtotalCSmoney1; // 总催收金额（不包括延期）
				jrtotalYCSmoney1 = accountShenheService.getDYYCSJEM0(userid,startDate, endDate,nowdate, name, phone,yuqts);//当月总已催收金额
				jrtotalYCSmoney2 = accountShenheService.getDYYCSJE1M0(userid,startDate, endDate, name, phone,yuqts);//当月总已催收金额（部分延期）
				jrtotalYCSmoney3 = accountShenheService.getDYYCSJE2M0(userid,startDate, endDate,nowdate, name, phone,yuqts);//当月总已催收金额（部分延期）
				jrtotalYCSmoney = jrtotalYCSmoney1 + jrtotalYCSmoney3;
				if(totalCSmoney1 != 0){
					ycsbl = totalYCSmoney * 100 / totalCSmoney1 ;
				}else{
					ycsbl = 0;
				}
				
				//延期的比例
				if(totalCSmoney != 0){
					ycsblyq = totalYCSmoney2 * 100 / totalCSmoney ;
				}else{
					ycsblyq = 0;
				}
				if(jrtotalCSmoney != 0){
					jrycsbl = jrtotalYCSmoney * 100 / jrtotalCSmoney ;
				}else{
					jrycsbl = 0;
				}
				account.setNowCount(nowCount);
				account.setCount(count);
				account.setCountSS(countSS);
				account.setCountyq(countyq);
				account.setJrcount(jrcount);
				account.setJrycscount(jrycscount);
				
				account.setNowOnesh(nowOnesh);
			
				account.setOne(one);
				account.setOneSS(oneSS);
				account.setOnemoney(famt.format(onemoney));
				
				account.setYcsbl(ycsbl);
				account.setKpi(kpi);
				account.setYcsblyq(ycsblyq);
				account.setJrycsbl(jrycsbl);
				account.setTotalCSmoney(famt.format(totalCSmoney));
				account.setTotalYCSmoneyDT(famt.format(totalYCSmoneyDT));
				account.setTotalCSmoneySS(famt.format(totalCSmoneySS));
				account.setTotalCSmoney1(famt.format(totalCSmoney1));
				account.setJrtotalCSmoney(famt.format(jrtotalCSmoney));
				account.setTotalYCSmoney(famt.format(totalYCSmoney));
				account.setTotalYCSmoneySS(famt.format(totalYCSmoneySS));
				account.setTotalYCSmoney2(famt.format(totalYCSmoney2));
				account.setJrtotalYCSmoney(famt.format(jrtotalYCSmoney));
				shenke.add(account);
				
			}
		}
		double countaa[] = new double[list.size()];
		int useridaa[] = new int[list.size()];
		for(int j=0 ;j<list.size();j++){
			DataRow dataRow1 = list.get(j);
			useridaa[j]=dataRow1.getInt("user_id");
			totalYCSmoney = accountShenheService.getYCSJEM0(useridaa[j],startDate, endDate, name, phone,yuqts);//总已催收金额
			countaa[j]=totalYCSmoney; //总笔数
		}
		int nn = countaa.length;
		for (int m = 0; m < nn - 1; m++) {   
			for (int n = 0; n < nn - 1; n++) { 
				if (countaa[n] > countaa[n + 1]) { 
					double tempaa = countaa[n];   
					int tempaaa = useridaa[n]; 
					countaa[n] = countaa[n + 1];   
					useridaa[n] = useridaa[n + 1]; 
					countaa[n + 1] = tempaa;   
					useridaa[n + 1] = tempaaa;   
				}   
			}   
		}
		for(int k=0 ;k<list.size();k++){
			DataRow row = new DataRow();
			row.set("user_id", useridaa[k]);
			row.set("px", list.size()-k);
			accountShenheService.updatePX(row);	
		}
		page.setData(shenke);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
		return null;
	}
	/**
	 * 得到催收统计M1
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetCuishouAccountM1() throws java.text.ParseException{
		//分页查询所有的后台用户
		int temp = getIntParameter("temp",0);
		int curPage  =getIntParameter("curPage",1);	
		String  tempVelue = getStrParameter("tempvl");
		String  startDate =getStrParameter("startDate");
		String  endDate =getStrParameter("endDate");
		int  recode =getIntParameter("recode",0);
		
		String name="";
		String phone="";
		String yuqts="";
		if(temp==1){
			name=tempVelue;
		}else if(temp==2){
			phone=tempVelue;
		}else if(temp==3){
			yuqts=tempVelue;
		}
		DBPage page = userService.selectUserListByCondtionCSM1(curPage,40, 0, name, phone); 
		List<DataRow> list=page.getData();
		List<ShenKeAccount> shenke=new ArrayList<ShenKeAccount>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String time = sdf.format(new Date());
		Calendar cd = Calendar.getInstance();
		try {
			cd.setTime(sdf.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//cd.add(Calendar.DATE, -i); // 减一天
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 减一天
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		DecimalFormat famt = new DecimalFormat("###,###");
		DecimalFormat df = new DecimalFormat( "0.00"); //设置double类型小数点后位数格式
		double onemoney = 0; 
		
		double totalCSmoney=0;  //总催收金额
		
		double totalCSmoneySS=0;  //总催收金额
		
		double jrtotalCSmoney=0;  //总催收金额
		double jrtotalCSmoney1=0;  //总催收金额
		
		double totalYCSmoney=0;  //总已催收金额
		double totalYCSmoneySS=0;  //总已催收金额
		
		double jrtotalYCSmoney=0;  //总已催收金额
		double jrtotalYCSmoney1=0;  //总已催收金额
		double jrtotalYCSmoney2=0;  //总已催收金额
		double jrtotalYCSmoney3=0;  //总已催收金额
		double totalCSmoney1=0;  //总已催收金额
		
		double totalYCSmoney2=0;  //总已催收金额
		
		double totalYCSmoney2SS=0;  //总已催收金额
		
		double totalYCSmoney3=0;  //总已催收金额
		
		double totalYCSmoney3SS=0;  //总已催收金额
		
		double totalYCSmoneyDT=0;  //当天总已催收金额
		
		double ycsbl=0;  //总已催收比例
		double kpi=0;  //总已催收比例
		double ycsblyq=0;  //总已催收比例
		double jrycsbl=0;  //总已催收比例
		
		int number = Integer.parseInt(time.substring(3, 5));
		if(recode == 0){
			for (int i=0 ;i<list.size();i++) {
				DataRow dataRow = list.get(i);
				int userid=dataRow.getInt("user_id");
				ShenKeAccount account=new ShenKeAccount();
				account.setUserid(userid);
				account.setUserName(dataRow.getString("name"));
				account.setPhone(dataRow.getString("phone"));
				/*//KPI
				int countSS=accountShenheService.getCSNowCountM1SS(userid,startDate, endDate,name, phone,number);	  //总笔数
				
				int oneSS=accountShenheService.getCSOneCountM1SS(userid,startDate, endDate,name,phone,number);   //M1笔数
				totalCSmoneySS = accountShenheService.getCSJEM1SS(userid,startDate, endDate,name, phone,yuqts,number); //总催收金额
				totalYCSmoneySS = accountShenheService.getYCSJEM1SS(userid,startDate, endDate, name, phone,yuqts,number);//总已催收金额
				totalYCSmoney3SS = accountShenheService.getYCSJEBFM1SS(userid,startDate, endDate, name, phone,yuqts,number);//总已催收部分金额
				totalYCSmoneySS  = totalYCSmoneySS + totalYCSmoney3SS;
				if(totalCSmoneySS != 0){
					kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
				}else{
					kpi = 0;
				}*/
				//KPI
				int countSS=accountShenheService.getCSNowCountM1SS(userid,startDate, endDate,name, phone,number);	  //总笔数
				
				int oneSS=accountShenheService.getCSOneCountM1SS(userid,startDate, endDate,name,phone,number);   //M1笔数
				
				SimpleDateFormat sdf111 = new SimpleDateFormat("yyyy-MM-dd");
				String time111 = sdf111.format(new Date());
				
				totalCSmoneySS = accountShenheService.getCSJEM1SSBG(userid,startDate, endDate,name, phone,yuqts,number); //总催收金额
				totalYCSmoneySS = accountShenheService.getYCSJEM1SSBG(userid,startDate, endDate, name, phone,yuqts,number);//总已催收金额
				
				if(totalCSmoneySS != 0){
					kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
				}else{
					kpi = 0;
				}
				
				//当天
				int nowCount=accountShenheService.getCSDateNowCountM1(userid,nowdate, dateno,name, phone); //总笔数
				totalYCSmoneyDT = accountShenheService.getCSDateNowCountDTJE(userid,nowdate, dateno,name, phone); //当天催收全额还款金额
				
				//总数
				int count=0;	  //总笔数
				int one=0;   //M1笔数
				onemoney =0;
				
				int jrcount=0;//当月新增逾期总笔数
				int jrycscount=0;//当月已催收笔数
				
				totalCSmoney = 0; //总催收金额
				
				jrtotalCSmoney1 = 0; //当月催收总金额
				
				totalYCSmoney = 0;//总已催收金额
				
				totalYCSmoney2 = accountShenheService.getYCSJEYQM1(userid,startDate, endDate, name, phone,yuqts,number);//总已催收延期金额
				
				totalYCSmoney3 =0;//总已催收部分金额
				
				int countyq = accountShenheService.getYCSJEYQCountM1(userid,startDate, endDate, name, phone,yuqts,number);//总已催收延期金额
				
				
				totalYCSmoney  = totalYCSmoney + totalYCSmoney3;
				
				jrtotalCSmoney = jrtotalCSmoney1; // 总催收金额（不包括延期）
				jrtotalYCSmoney1 = 0;//当月总已催收金额
				jrtotalYCSmoney2 = 0;//当月总已催收金额（部分延期）
				jrtotalYCSmoney3 = 0;//当月总已催收金额（部分延期）
				jrtotalYCSmoney = jrtotalYCSmoney1 + jrtotalYCSmoney3;
				if(totalCSmoney1 != 0){
					ycsbl = totalYCSmoney * 100 / totalCSmoney1 ;
				}else{
					ycsbl = 0;
				}
				//延期的比例
				if(totalCSmoney != 0){
					ycsblyq = totalYCSmoney2 * 100 / totalCSmoney ;
				}else{
					ycsblyq = 0;
				}
				if(jrtotalCSmoney != 0){
					jrycsbl = jrtotalYCSmoney * 100 / jrtotalCSmoney ;
				}else{
					jrycsbl = 0;
				}
				account.setNowCount(nowCount);
				account.setCount(count);
				account.setCountSS(countSS);
				account.setCountyq(countyq);
				account.setJrcount(jrcount);
				account.setJrycscount(jrycscount);
				
				//account.setNowOnesh(nowOnesh);
			
				account.setOne(one);
				account.setOneSS(oneSS);
				account.setOnemoney(famt.format(onemoney));
				
				account.setYcsbl(ycsbl);
				account.setKpi(kpi);
				account.setYcsblyq(ycsblyq);
				account.setJrycsbl(jrycsbl);
				account.setTotalCSmoney(famt.format(totalCSmoney));
				account.setTotalYCSmoneyDT(famt.format(totalYCSmoneyDT));
				account.setTotalCSmoneySS(famt.format(totalCSmoneySS));
				account.setTotalCSmoney1(famt.format(totalCSmoney1));
				account.setJrtotalCSmoney(famt.format(jrtotalCSmoney));
				account.setTotalYCSmoney(famt.format(totalYCSmoney));
				account.setTotalYCSmoneySS(famt.format(totalYCSmoneySS));
				account.setTotalYCSmoney2(famt.format(totalYCSmoney2));
				account.setJrtotalYCSmoney(famt.format(jrtotalYCSmoney));
				shenke.add(account);
				
			}
		}else if(recode == 1){
			for (int i=0 ;i<list.size();i++) {
				DataRow dataRow = list.get(i);
				int userid=dataRow.getInt("user_id");
				ShenKeAccount account=new ShenKeAccount();
				account.setUserid(userid);
				account.setUserName(dataRow.getString("name"));
				account.setPhone(dataRow.getString("phone"));
				/*//KPI
				int countSS=accountShenheService.getCSNowCountM1SS(userid,startDate, endDate,name, phone,number);	  //总笔数
				
				int oneSS=accountShenheService.getCSOneCountM1SS(userid,startDate, endDate,name,phone,number);   //M1笔数
				totalCSmoneySS = accountShenheService.getCSJEM1SS(userid,startDate, endDate,name, phone,yuqts,number); //总催收金额
				totalYCSmoneySS = accountShenheService.getYCSJEM1SS(userid,startDate, endDate, name, phone,yuqts,number);//总已催收金额
				totalYCSmoney3SS = accountShenheService.getYCSJEBFM1SS(userid,startDate, endDate, name, phone,yuqts,number);//总已催收部分金额
				totalYCSmoneySS  = totalYCSmoneySS + totalYCSmoney3SS;
				if(totalCSmoneySS != 0){
					kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
				}else{
					kpi = 0;
				}*/
				//KPI
				int countSS=accountShenheService.getCSNowCountM1SS(userid,startDate, endDate,name, phone,number);	  //总笔数
				
				int oneSS=accountShenheService.getCSOneCountM1SS(userid,startDate, endDate,name,phone,number);   //M1笔数
				
				SimpleDateFormat sdf111 = new SimpleDateFormat("yyyy-MM-dd");
				String time111 = sdf111.format(new Date());
				
				totalCSmoneySS = accountShenheService.getCSJEM1SSBG(userid,startDate, endDate,name, phone,yuqts,number); //总催收金额
				totalYCSmoneySS = accountShenheService.getYCSJEM1SSBG(userid,startDate, endDate, name, phone,yuqts,number);//总已催收金额
				
				if(totalCSmoneySS != 0){
					kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
				}else{
					kpi = 0;
				}
				//当天
				int nowCount=accountShenheService.getCSDateNowCountM1(userid,nowdate, dateno,name, phone); //总笔数
				int nowOnesh=accountShenheService.getCSNowOneCountM1(userid, nowdate, dateno,name,phone);	  //当天M1笔数
				totalYCSmoneyDT = accountShenheService.getCSDateNowCountDTJE(userid,nowdate, dateno,name, phone); //当天催收全额还款金额
				//总数
				int count=accountShenheService.getCSNowCountM1(userid,startDate, endDate,name, phone);	  //总笔数
				int one=accountShenheService.getCSOneCountM1(userid,startDate, endDate,name,phone);   //M1笔数
				onemoney = accountShenheService.getOneMoneyM1(userid,startDate, endDate,name, phone);
				
				int jrcount=accountShenheService.getDYCSNowCountM1(userid,startDate, endDate,nowdate,name, phone);//当月新增逾期总笔数
				int jrycscount=accountShenheService.getDYCSCountM1(userid,startDate, endDate,nowdate,name, phone);//当月已催收笔数
				
				totalCSmoney = accountShenheService.getCSJEM1(userid,startDate, endDate,name, phone,yuqts); //总催收金额
				
				jrtotalCSmoney1 = accountShenheService.getDYCSJEM1(userid,startDate, endDate,nowdate,name, phone,yuqts); //当月催收总金额
				
				totalYCSmoney = accountShenheService.getYCSJEM1(userid,startDate, endDate, name, phone,yuqts);//总已催收金额
				
				totalYCSmoney2 = accountShenheService.getYCSJEYQM1(userid,startDate, endDate, name, phone,yuqts,number);//总已催收延期金额
				
				totalYCSmoney3 = accountShenheService.getYCSJEBFM1(userid,startDate, endDate, name, phone,yuqts);//总已催收部分金额
				
				int countyq = accountShenheService.getYCSJEYQCountM1(userid,startDate, endDate, name, phone,yuqts,number);//总已催收延期金额
				totalCSmoney1 = totalCSmoney - totalYCSmoney2 ; // 总催收金额（不包括延期）
				totalYCSmoney  = totalYCSmoney + totalYCSmoney3;
				
				jrtotalCSmoney = jrtotalCSmoney1; // 总催收金额（不包括延期）
				jrtotalYCSmoney1 = accountShenheService.getDYYCSJEM1(userid,startDate, endDate,nowdate, name, phone,yuqts);//当月总已催收金额
				jrtotalYCSmoney2 = accountShenheService.getDYYCSJE1M1(userid,startDate, endDate, name, phone,yuqts);//当月总已催收金额（部分延期）
				jrtotalYCSmoney3 = accountShenheService.getDYYCSJE2M1(userid,startDate, endDate,nowdate, name, phone,yuqts);//当月总已催收金额（部分延期）
				jrtotalYCSmoney = jrtotalYCSmoney1 + jrtotalYCSmoney3;
				if(totalCSmoney1 != 0){
					ycsbl = totalYCSmoney * 100 / totalCSmoney1 ;
				}else{
					ycsbl = 0;
				}
				
				//延期的比例
				if(totalCSmoney != 0){
					ycsblyq = totalYCSmoney2 * 100 / totalCSmoney ;
				}else{
					ycsblyq = 0;
				}
				if(jrtotalCSmoney != 0){
					jrycsbl = jrtotalYCSmoney * 100 / jrtotalCSmoney ;
				}else{
					jrycsbl = 0;
				}
				account.setNowCount(nowCount);
				account.setCount(count);
				account.setCountSS(countSS);
				account.setCountyq(countyq);
				account.setJrcount(jrcount);
				account.setJrycscount(jrycscount);
				
				account.setNowOnesh(nowOnesh);
			
				account.setOne(one);
				account.setOneSS(oneSS);
				account.setOnemoney(famt.format(onemoney));
				
				account.setYcsbl(ycsbl);
				account.setKpi(kpi);
				account.setYcsblyq(ycsblyq);
				account.setJrycsbl(jrycsbl);
				account.setTotalCSmoney(famt.format(totalCSmoney));
				account.setTotalYCSmoneyDT(famt.format(totalYCSmoneyDT));
				account.setTotalCSmoneySS(famt.format(totalCSmoneySS));
				account.setTotalCSmoney1(famt.format(totalCSmoney1));
				account.setJrtotalCSmoney(famt.format(jrtotalCSmoney));
				account.setTotalYCSmoney(famt.format(totalYCSmoney));
				account.setTotalYCSmoneySS(famt.format(totalYCSmoneySS));
				account.setTotalYCSmoney2(famt.format(totalYCSmoney2));
				account.setJrtotalYCSmoney(famt.format(jrtotalYCSmoney));
				shenke.add(account);
				
			}
		}
		double countaa[] = new double[list.size()];
		int useridaa[] = new int[list.size()];
		for(int j=0 ;j<list.size();j++){
			DataRow dataRow1 = list.get(j);
			useridaa[j]=dataRow1.getInt("user_id");
			totalYCSmoney = accountShenheService.getYCSJE(useridaa[j],startDate, endDate, name, phone,yuqts);//总已催收金额
			countaa[j]=totalYCSmoney; //总笔数
		}
		int nn = countaa.length;
		for (int m = 0; m < nn - 1; m++) {   
			for (int n = 0; n < nn - 1; n++) { 
				if (countaa[n] > countaa[n + 1]) { 
					double tempaa = countaa[n];   
					int tempaaa = useridaa[n]; 
					countaa[n] = countaa[n + 1];   
					useridaa[n] = useridaa[n + 1]; 
					countaa[n + 1] = tempaa;   
					useridaa[n + 1] = tempaaa;   
				}   
			}   
		}
		for(int k=0 ;k<list.size();k++){
			DataRow row = new DataRow();
			row.set("user_id", useridaa[k]);
			row.set("px", list.size()-k);
			accountShenheService.updatePX(row);	
		}
		page.setData(shenke);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
		return null;
	}
	/**
	 * 得到催收统计M2
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetCuishouAccountM2() throws java.text.ParseException{
		//分页查询所有的后台用户
		int temp = getIntParameter("temp",0);
		int curPage  =getIntParameter("curPage",1);	
		String  tempVelue = getStrParameter("tempvl");
		String  startDate =getStrParameter("startDate");
		String  endDate =getStrParameter("endDate");
		
		
		String name="";
		String phone="";
		String yuqts="";
		if(temp==1){
			name=tempVelue;
		}else if(temp==2){
			phone=tempVelue;
		}else if(temp==3){
			yuqts=tempVelue;
		}
		DBPage page = userService.selectUserListByCondtionCSM2(curPage, 25, 0, name, phone); 
		List<DataRow> list=page.getData();
		List<ShenKeAccount> shenke=new ArrayList<ShenKeAccount>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String time = sdf.format(new Date());
		Calendar cd = Calendar.getInstance();
		try {
			cd.setTime(sdf.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//cd.add(Calendar.DATE, -i); // 减一天
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 减一天
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		DecimalFormat famt = new DecimalFormat("###,###");
		DecimalFormat df = new DecimalFormat( "0.00"); //设置double类型小数点后位数格式
		
		double twomoney = 0; 
		
		double totalCSmoney=0;  //总催收金额
		double jrtotalCSmoney=0;  //总催收金额
		double jrtotalCSmoney1=0;  //总催收金额
		double totalYCSmoney=0;  //总已催收金额
		double jrtotalYCSmoney=0;  //总已催收金额
		double jrtotalYCSmoney1=0;  //总已催收金额
		double jrtotalYCSmoney2=0;  //总已催收金额
		double jrtotalYCSmoney3=0;  //总已催收金额
		double totalCSmoney1=0;  //总已催收金额
		double totalYCSmoney2=0;  //总已催收金额
		double totalYCSmoney3=0;  //总已催收金额
		double totalYCSmoneyDT=0;  //总已催收金额
		double ycsbl=0;  //总已催收比例
		double ycsblyq=0;  //总已催收比例
		double jrycsbl=0;  //总已催收比例
		
		double totalYCSmoneySS=0;  //总已催收金额
		double totalCSmoneySS=0;
		double totalYCSmoney3SS=0;
		double kpi =0;
		int number = Integer.parseInt(time.substring(3, 5));
		for (int i=0 ;i<list.size();i++) {
			DataRow dataRow = list.get(i);
			int userid=dataRow.getInt("user_id");
			ShenKeAccount account=new ShenKeAccount();
			account.setUserid(userid);
			account.setUserName(dataRow.getString("name"));
			account.setPhone(dataRow.getString("phone"));
			
			/*//KPI
			int countSS=accountShenheService.getCSNowCountM2SS(userid,startDate, endDate,name, phone,number);	  //总笔数
			
			int oneSS=accountShenheService.getCSOneCountM2SS(userid,startDate, endDate,name,phone,number);   //M1笔数
			totalCSmoneySS = accountShenheService.getCSJEM2SS(userid,startDate, endDate,name, phone,yuqts,number); //总催收金额
			totalYCSmoneySS = accountShenheService.getYCSJEM2SS(userid,startDate, endDate, name, phone,yuqts,number);//总已催收金额
			totalYCSmoney3SS = accountShenheService.getYCSJEBFM2SS(userid,startDate, endDate, name, phone,yuqts,number);//总已催收部分金额
			totalYCSmoneySS  = totalYCSmoneySS + totalYCSmoney3SS;
			if(totalCSmoneySS != 0){
				kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
			}else{
				kpi = 0;
			}*/
			
			//KPI
			int countSS=accountShenheService.getCSNowCountM2SS(userid,startDate, endDate,name, phone,number);	  //总笔数
			
			int oneSS=accountShenheService.getCSOneCountM2SS(userid,startDate, endDate,name,phone,number);   //M1笔数
			totalCSmoneySS = accountShenheService.getCSJEM2SSBG(userid,startDate, endDate,name, phone,yuqts,number); //总催收金额
			totalYCSmoneySS = accountShenheService.getYCSJEM2SSBG(userid,startDate, endDate, name, phone,yuqts,number);//总已催收金额
			
			if(totalCSmoneySS != 0){
				kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
			}else{
				kpi = 0;
			}
			
			int nowTwosh=accountShenheService.getCSNowTwoCountM2(userid, nowdate, dateno,name,phone);	//当天M2笔数
			
			int count=accountShenheService.getCSNowCountM2(userid,startDate, endDate,name, phone);	  //总笔数
			int jrcount=accountShenheService.getDYCSNowCountM2(userid,startDate, endDate,nowdate,name, phone);//当月新增逾期总笔数
			int jrycscount=accountShenheService.getDYCSCountM2(userid,startDate, endDate,nowdate,name, phone);//当月已催收笔数
			
			int two=accountShenheService.getCSTwoCountM2(userid,startDate, endDate,name,phone);	//M2笔数
			
			int nowCount=accountShenheService.getCSDateNowCountM2(userid,nowdate, dateno,name, phone); //总笔数
			totalYCSmoneyDT = accountShenheService.getCSDateNowCountDTJEM2(userid,nowdate, dateno,name, phone); //当天催收全额还款金额
			
			twomoney = accountShenheService.getTwoMoneyM2(userid,startDate, endDate,name, phone);
			
			totalCSmoney = accountShenheService.getCSJEM2(userid,startDate, endDate,name, phone,yuqts); //总催收金额
			jrtotalCSmoney1 = accountShenheService.getDYCSJEM2(userid,startDate, endDate,nowdate,name, phone,yuqts); //当月催收总金额
			totalYCSmoney = accountShenheService.getYCSJEM2(userid,startDate, endDate, name, phone,yuqts);//总已催收金额
			totalYCSmoney2 = accountShenheService.getYCSJEYQM2(userid,startDate, endDate, name, phone,yuqts,number);//总已催收延期金额
			totalYCSmoney3 = accountShenheService.getYCSJEBFM2(userid,startDate, endDate, name, phone,yuqts);//总已催收部分金额
			int countyq = accountShenheService.getYCSJEYQCountM2(userid,startDate, endDate, name, phone,yuqts,number);//总已催收延期金额
			totalCSmoney1 = totalCSmoney - totalYCSmoney2 ; // 总催收金额（不包括延期）
			totalYCSmoney  = totalYCSmoney + totalYCSmoney3;
			jrtotalCSmoney = jrtotalCSmoney1; // 总催收金额（不包括延期）
			jrtotalYCSmoney1 = accountShenheService.getDYYCSJEM2(userid,startDate, endDate,nowdate, name, phone,yuqts);//当月总已催收金额
			jrtotalYCSmoney2 = accountShenheService.getDYYCSJE1M2(userid,startDate, endDate, name, phone,yuqts);//当月总已催收金额（部分延期）
			jrtotalYCSmoney3 = accountShenheService.getDYYCSJE2M2(userid,startDate, endDate,nowdate, name, phone,yuqts);//当月总已催收金额（部分延期）
			jrtotalYCSmoney = jrtotalYCSmoney1 + jrtotalYCSmoney3;
			if(totalCSmoney1 != 0){
				ycsbl = totalYCSmoney * 100 / totalCSmoney1 ;
			}else{
				ycsbl = 0;
			}
			//延期的比例
			if(totalCSmoney != 0){
				ycsblyq = totalYCSmoney2 * 100 / totalCSmoney ;
			}else{
				ycsblyq = 0;
			}
			if(jrtotalCSmoney != 0){
				jrycsbl = jrtotalYCSmoney * 100 / jrtotalCSmoney ;
			}else{
				jrycsbl = 0;
			}
			account.setCountSS(countSS);
			account.setOneSS(oneSS);
			account.setTotalCSmoneySS(famt.format(totalCSmoneySS));
			account.setTotalYCSmoneySS(famt.format(totalYCSmoneySS));
			account.setKpi(kpi);
			
			account.setNowCount(nowCount);
			account.setCount(count);
			account.setCountyq(countyq);
			account.setJrcount(jrcount);
			account.setJrycscount(jrycscount);
	
			account.setNowTwosh(nowTwosh);
		
			account.setTwomoney(famt.format(twomoney));
			
			account.setTwo(two);
			account.setYcsbl(ycsbl);
			account.setYcsblyq(ycsblyq);
			account.setJrycsbl(jrycsbl);
			account.setTotalCSmoney(famt.format(totalCSmoney));
			account.setTotalYCSmoneyDT(famt.format(totalYCSmoneyDT));
			account.setTotalCSmoney1(famt.format(totalCSmoney1));
			account.setJrtotalCSmoney(famt.format(jrtotalCSmoney));
			account.setTotalYCSmoney(famt.format(totalYCSmoney));
			account.setTotalYCSmoney2(famt.format(totalYCSmoney2));
			account.setJrtotalYCSmoney(famt.format(jrtotalYCSmoney));
			shenke.add(account);
			
		}
		double countaa[] = new double[list.size()];
		int useridaa[] = new int[list.size()];
		for(int j=0 ;j<list.size();j++){
			DataRow dataRow1 = list.get(j);
			useridaa[j]=dataRow1.getInt("user_id");
			totalYCSmoney = accountShenheService.getYCSJE(useridaa[j],startDate, endDate, name, phone,yuqts);//总已催收金额
			countaa[j]=totalYCSmoney; //总笔数
		}
		int nn = countaa.length;
		for (int m = 0; m < nn - 1; m++) {   
			for (int n = 0; n < nn - 1; n++) { 
				if (countaa[n] > countaa[n + 1]) { 
					double tempaa = countaa[n];   
					int tempaaa = useridaa[n]; 
					countaa[n] = countaa[n + 1];   
					useridaa[n] = useridaa[n + 1]; 
					countaa[n + 1] = tempaa;   
					useridaa[n + 1] = tempaaa;   
				}   
			}   
		}
		for(int k=0 ;k<list.size();k++){
			DataRow row = new DataRow();
			row.set("user_id", useridaa[k]);
			row.set("px", list.size()-k);
			accountShenheService.updatePX(row);	
		}
		page.setData(shenke);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
		return null;
	}
	/**
	 * 得到催收统计M2
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetCuishouAccountM3() throws java.text.ParseException{
		//分页查询所有的后台用户
		int temp = getIntParameter("temp",0);
		int curPage  =getIntParameter("curPage",1);	
		String  tempVelue = getStrParameter("tempvl");
		String  startDate =getStrParameter("startDate");
		String  endDate =getStrParameter("endDate");
		
		
		String name="";
		String phone="";
		String yuqts="";
		if(temp==1){
			name=tempVelue;
		}else if(temp==2){
			phone=tempVelue;
		}else if(temp==3){
			yuqts=tempVelue;
		}
		DBPage page = userService.selectUserListByCondtionCSM3(curPage, 25, 0, name, phone); 
		List<DataRow> list=page.getData();
		List<ShenKeAccount> shenke=new ArrayList<ShenKeAccount>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String time = sdf.format(new Date());
		Calendar cd = Calendar.getInstance();
		try {
			cd.setTime(sdf.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//cd.add(Calendar.DATE, -i); // 减一天
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 减一天
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		DecimalFormat famt = new DecimalFormat("###,###");
		DecimalFormat df = new DecimalFormat( "0.00"); //设置double类型小数点后位数格式
		
		double twomoney = 0; 
		
		double totalCSmoney=0;  //总催收金额
		double jrtotalCSmoney=0;  //总催收金额
		double jrtotalCSmoney1=0;  //总催收金额
		double totalYCSmoney=0;  //总已催收金额
		double jrtotalYCSmoney=0;  //总已催收金额
		double jrtotalYCSmoney1=0;  //总已催收金额
		double jrtotalYCSmoney2=0;  //总已催收金额
		double jrtotalYCSmoney3=0;  //总已催收金额
		double totalCSmoney1=0;  //总已催收金额
		double totalYCSmoney2=0;  //总已催收金额
		double totalYCSmoney3=0;  //总已催收金额
		double totalYCSmoneyDT=0;  //总已催收金额
		double ycsbl=0;  //总已催收比例
		double ycsblyq=0;  //总已催收比例
		double jrycsbl=0;  //总已催收比例
		
		double totalYCSmoneySS=0;  //总已催收金额
		double totalCSmoneySS=0;
		double totalYCSmoney3SS=0;
		double kpi =0;
		int number = Integer.parseInt(time.substring(3, 5));
		for (int i=0 ;i<list.size();i++) {
			DataRow dataRow = list.get(i);
			int userid=dataRow.getInt("user_id");
			ShenKeAccount account=new ShenKeAccount();
			account.setUserid(userid);
			account.setUserName(dataRow.getString("name"));
			account.setPhone(dataRow.getString("phone"));
			
			/*//KPI
			int countSS=accountShenheService.getCSNowCountM2SS(userid,startDate, endDate,name, phone,number);	  //总笔数
			
			int oneSS=accountShenheService.getCSOneCountM2SS(userid,startDate, endDate,name,phone,number);   //M1笔数
			totalCSmoneySS = accountShenheService.getCSJEM2SS(userid,startDate, endDate,name, phone,yuqts,number); //总催收金额
			totalYCSmoneySS = accountShenheService.getYCSJEM2SS(userid,startDate, endDate, name, phone,yuqts,number);//总已催收金额
			totalYCSmoney3SS = accountShenheService.getYCSJEBFM2SS(userid,startDate, endDate, name, phone,yuqts,number);//总已催收部分金额
			totalYCSmoneySS  = totalYCSmoneySS + totalYCSmoney3SS;
			if(totalCSmoneySS != 0){
				kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
			}else{
				kpi = 0;
			}*/
			
			//KPI
			int countSS=accountShenheService.getCSNowCountM3SS(userid,startDate, endDate,name, phone,number);	  //总笔数
			
			int oneSS=accountShenheService.getCSOneCountM3SS(userid,startDate, endDate,name,phone,number);   //M1笔数
			totalCSmoneySS = accountShenheService.getCSJEM3SSBG(userid,startDate, endDate,name, phone,yuqts,number); //总催收金额
			totalYCSmoneySS = accountShenheService.getYCSJEM3SSBG(userid,startDate, endDate, name, phone,yuqts,number);//总已催收金额
			
			if(totalCSmoneySS != 0){
				kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
			}else{
				kpi = 0;
			}
			
			int nowTwosh=accountShenheService.getCSNowTwoCountM3(userid, nowdate, dateno,name,phone);	//当天M2笔数
			
			int count=accountShenheService.getCSNowCountM3(userid,startDate, endDate,name, phone);	  //总笔数
			int jrcount=accountShenheService.getDYCSNowCountM3(userid,startDate, endDate,nowdate,name, phone);//当月新增逾期总笔数
			int jrycscount=accountShenheService.getDYCSCountM3(userid,startDate, endDate,nowdate,name, phone);//当月已催收笔数
			
			int two=accountShenheService.getCSTwoCountM3(userid,startDate, endDate,name,phone);	//M2笔数
			
			int nowCount=accountShenheService.getCSDateNowCountM3(userid,nowdate, dateno,name, phone); //总笔数
			totalYCSmoneyDT = accountShenheService.getCSDateNowCountDTJEM3(userid,nowdate, dateno,name, phone); //当天催收全额还款金额
			
			twomoney = accountShenheService.getTwoMoneyM3(userid,startDate, endDate,name, phone);
			
			totalCSmoney = accountShenheService.getCSJEM3(userid,startDate, endDate,name, phone,yuqts); //总催收金额
			jrtotalCSmoney1 = accountShenheService.getDYCSJEM3(userid,startDate, endDate,nowdate,name, phone,yuqts); //当月催收总金额
			totalYCSmoney = accountShenheService.getYCSJEM3(userid,startDate, endDate, name, phone,yuqts);//总已催收金额
			totalYCSmoney2 = accountShenheService.getYCSJEYQM3(userid,startDate, endDate, name, phone,yuqts,number);//总已催收延期金额
			totalYCSmoney3 = accountShenheService.getYCSJEBFM3(userid,startDate, endDate, name, phone,yuqts);//总已催收部分金额
			int countyq = accountShenheService.getYCSJEYQCountM3(userid,startDate, endDate, name, phone,yuqts,number);//总已催收延期金额
			totalCSmoney1 = totalCSmoney - totalYCSmoney2 ; // 总催收金额（不包括延期）
			totalYCSmoney  = totalYCSmoney + totalYCSmoney3;
			jrtotalCSmoney = jrtotalCSmoney1; // 总催收金额（不包括延期）
			jrtotalYCSmoney1 = accountShenheService.getDYYCSJEM3(userid,startDate, endDate,nowdate, name, phone,yuqts);//当月总已催收金额
			jrtotalYCSmoney2 = accountShenheService.getDYYCSJE1M3(userid,startDate, endDate, name, phone,yuqts);//当月总已催收金额（部分延期）
			jrtotalYCSmoney3 = accountShenheService.getDYYCSJE2M3(userid,startDate, endDate,nowdate, name, phone,yuqts);//当月总已催收金额（部分延期）
			jrtotalYCSmoney = jrtotalYCSmoney1 + jrtotalYCSmoney3;
			if(totalCSmoney1 != 0){
				ycsbl = totalYCSmoney * 100 / totalCSmoney1 ;
			}else{
				ycsbl = 0;
			}
			//延期的比例
			if(totalCSmoney != 0){
				ycsblyq = totalYCSmoney2 * 100 / totalCSmoney ;
			}else{
				ycsblyq = 0;
			}
			if(jrtotalCSmoney != 0){
				jrycsbl = jrtotalYCSmoney * 100 / jrtotalCSmoney ;
			}else{
				jrycsbl = 0;
			}
			account.setCountSS(countSS);
			account.setOneSS(oneSS);
			account.setTotalCSmoneySS(famt.format(totalCSmoneySS));
			account.setTotalYCSmoneySS(famt.format(totalYCSmoneySS));
			account.setKpi(kpi);
			
			account.setNowCount(nowCount);
			account.setCount(count);
			account.setCountyq(countyq);
			account.setJrcount(jrcount);
			account.setJrycscount(jrycscount);
	
			account.setNowTwosh(nowTwosh);
		
			account.setTwomoney(famt.format(twomoney));
			
			account.setTwo(two);
			account.setYcsbl(ycsbl);
			account.setYcsblyq(ycsblyq);
			account.setJrycsbl(jrycsbl);
			account.setTotalCSmoney(famt.format(totalCSmoney));
			account.setTotalYCSmoneyDT(famt.format(totalYCSmoneyDT));
			account.setTotalCSmoney1(famt.format(totalCSmoney1));
			account.setJrtotalCSmoney(famt.format(jrtotalCSmoney));
			account.setTotalYCSmoney(famt.format(totalYCSmoney));
			account.setTotalYCSmoney2(famt.format(totalYCSmoney2));
			account.setJrtotalYCSmoney(famt.format(jrtotalYCSmoney));
			shenke.add(account);
			
		}
		double countaa[] = new double[list.size()];
		int useridaa[] = new int[list.size()];
		for(int j=0 ;j<list.size();j++){
			DataRow dataRow1 = list.get(j);
			useridaa[j]=dataRow1.getInt("user_id");
			totalYCSmoney = accountShenheService.getYCSJE(useridaa[j],startDate, endDate, name, phone,yuqts);//总已催收金额
			countaa[j]=totalYCSmoney; //总笔数
		}
		int nn = countaa.length;
		for (int m = 0; m < nn - 1; m++) {   
			for (int n = 0; n < nn - 1; n++) { 
				if (countaa[n] > countaa[n + 1]) { 
					double tempaa = countaa[n];   
					int tempaaa = useridaa[n]; 
					countaa[n] = countaa[n + 1];   
					useridaa[n] = useridaa[n + 1]; 
					countaa[n + 1] = tempaa;   
					useridaa[n + 1] = tempaaa;   
				}   
			}   
		}
		for(int k=0 ;k<list.size();k++){
			DataRow row = new DataRow();
			row.set("user_id", useridaa[k]);
			row.set("px", list.size()-k);
			accountShenheService.updatePX(row);	
		}
		page.setData(shenke);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
		return null;
	}
	/**
	 * 得到催收外包的催收统计
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetCSWBCuishouAccount() throws java.text.ParseException{
		//分页查询所有的后台用户
		int temp = getIntParameter("temp",0);
		int curPage  =getIntParameter("curPage",1);	
		String  tempVelue = getStrParameter("tempvl");
		String  startDate =getStrParameter("startDate");
		String  endDate =getStrParameter("endDate");
		
		String name="";
		String phone="";
		String yuqts="";
		if(temp==1){
			name=tempVelue;
		}else if(temp==2){
			phone=tempVelue;
		}else if(temp==3){
			yuqts=tempVelue;
		}
		DBPage page = userService.selectCSWBUserListByCondtionCS(curPage, 25, 0, name, phone); 
		List<DataRow> list=page.getData();
		List<ShenKeAccount> shenke=new ArrayList<ShenKeAccount>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String time = sdf.format(new Date());
		Calendar cd = Calendar.getInstance();
		try {
			cd.setTime(sdf.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//cd.add(Calendar.DATE, -i); // 减一天
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 减一天
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		DecimalFormat famt = new DecimalFormat("###,###");
		DecimalFormat df = new DecimalFormat( "0.00"); //设置double类型小数点后位数格式
		double onemoney = 0; 
		double twomoney = 0; 
		double threemoney = 0; 
		double threeTGmoney = 0; 
		double totalCSmoney=0;  //总催收金额
		double jrtotalCSmoney=0;  //总催收金额
		double jrtotalCSmoney1=0;  //总催收金额
		double totalYCSmoney=0;  //总已催收金额
		double jrtotalYCSmoney=0;  //总已催收金额
		double jrtotalYCSmoney1=0;  //总已催收金额
		double jrtotalYCSmoney2=0;  //总已催收金额
		double jrtotalYCSmoney3=0;  //总已催收金额
		double totalCSmoney1=0;  //总已催收金额
		double totalYCSmoney2=0;  //总已催收金额
		double totalYCSmoney3=0;  //总已催收金额
		double ycsbl=0;  //总已催收比例
		double ycsblyq=0;  //总已催收比例
		double jrycsbl=0;  //总已催收比例
		for (int i=0 ;i<list.size();i++) {
			DataRow dataRow = list.get(i);
			int userid=dataRow.getInt("user_id");
			ShenKeAccount account=new ShenKeAccount();
			account.setUserid(userid);
			account.setUserName(dataRow.getString("name"));
			account.setPhone(dataRow.getString("phone"));
			int jrnowOnesh=accountShenheService.getJRCSNowOneCountCSWB(userid, nowdate, dateno,name,phone);	  //当天M1笔数
			int nowOnesh=accountShenheService.getCSNowOneCountCSWB(userid, nowdate, dateno,name,phone);	  //当天M1笔数
			int nowTwosh=accountShenheService.getCSNowTwoCountCSWB(userid, nowdate, dateno,name,phone);	//当天M2笔数
			int nowThreesh=accountShenheService.getCSNowThreeCountCSWB(userid, nowdate, dateno,name,phone);	//当天M3笔数
			int nowFoursh=accountShenheService.getCSNowFourCountCSWB(userid, nowdate, dateno, name, phone); //当天M4笔数
			int count=accountShenheService.getCSNowCountCSWB(userid,startDate, endDate,name, phone);	  //总笔数
			int jrcount=accountShenheService.getDYCSNowCountCSWB(userid,startDate, endDate,nowdate,name, phone);//当月新增逾期总笔数
			int jrycscount=accountShenheService.getDYCSCountCSWB(userid,startDate, endDate,nowdate,name, phone);//当月已催收笔数
			int one=accountShenheService.getCSOneCountCSWB(userid,startDate, endDate,name,phone);   //M1笔数
			int two=accountShenheService.getCSTwoCountCSWB(userid,startDate, endDate,name,phone);	//M2笔数
			int three=accountShenheService.getCSThreeCountCSWB(userid,startDate, endDate,name,phone); //M3笔数
			int threeTG = accountShenheService.getCSFourCountCSWB(userid,startDate, endDate, name, phone);//M4笔数
			int nowCount=accountShenheService.getCSDateNowCountCSWB(userid,nowdate, dateno,name, phone); //总笔数
			onemoney = accountShenheService.getOneMoneyCSWB(userid,startDate, endDate,name, phone);
			twomoney = accountShenheService.getTwoMoneyCSWB(userid,startDate, endDate,name, phone);
			threemoney = accountShenheService.getThreeMoneyCSWB(userid,startDate, endDate,name, phone);
			threeTGmoney = accountShenheService.getThreeTGMoneyCSWB(userid,startDate, endDate,name, phone);
			totalCSmoney = accountShenheService.getCSJECSWB(userid,startDate, endDate,name, phone,yuqts); //总催收金额
			jrtotalCSmoney1 = accountShenheService.getDYCSJECSWB(userid,startDate, endDate,nowdate,name, phone,yuqts); //当月催收总金额
			totalYCSmoney = accountShenheService.getYCSJECSWB(userid,startDate, endDate, name, phone,yuqts);//总已催收金额
			totalYCSmoney2 = accountShenheService.getYCSJEYQCSWB(userid,startDate, endDate, name, phone,yuqts);//总已催收延期金额
			totalYCSmoney3 = accountShenheService.getYCSJEBFCSWB(userid,startDate, endDate, name, phone,yuqts);//总已催收部分金额
			int countyq = accountShenheService.getYCSJEYQCountCSWB(userid,startDate, endDate, name, phone,yuqts);//总已催收延期金额
			totalCSmoney1 = totalCSmoney - totalYCSmoney2 ; // 总催收金额（不包括延期）
			//totalYCSmoney  = totalYCSmoney + totalYCSmoney3;
			jrtotalCSmoney = jrtotalCSmoney1; // 总催收金额（不包括延期）
			jrtotalYCSmoney1 = accountShenheService.getDYYCSJECSWB(userid,startDate, endDate,nowdate, name, phone,yuqts);//当月总已催收金额
			jrtotalYCSmoney2 = accountShenheService.getDYYCSJE1CSWB(userid,startDate, endDate, name, phone,yuqts);//当月总已催收金额（部分延期）
			jrtotalYCSmoney3 = accountShenheService.getDYYCSJE2CSWB(userid,startDate, endDate,nowdate, name, phone,yuqts);//当月总已催收金额（部分延期）
			jrtotalYCSmoney = jrtotalYCSmoney1;
			if(totalCSmoney1 != 0){
				ycsbl = totalYCSmoney * 100 / totalCSmoney1 ;
			}else{
				ycsbl = 0;
			}
			//延期的比例
			if(totalCSmoney != 0){
				ycsblyq = totalYCSmoney2 * 100 / totalCSmoney ;
			}else{
				ycsblyq = 0;
			}
			if(jrtotalCSmoney != 0){
				jrycsbl = jrtotalYCSmoney * 100 / jrtotalCSmoney ;
			}else{
				jrycsbl = 0;
			}
			account.setNowCount(nowCount);
			account.setCount(count);
			account.setCountyq(countyq);
			account.setJrcount(jrcount);
			account.setJrycscount(jrycscount);
			account.setNowFoursh(nowFoursh);
			account.setJrnowOnesh(jrnowOnesh);
			account.setNowOnesh(nowOnesh);
			account.setNowThreesh(nowThreesh);
			account.setNowTwosh(nowTwosh);
			account.setOne(one);
			account.setOnemoney(famt.format(onemoney));
			account.setTwomoney(famt.format(twomoney));
			account.setThreemoney(famt.format(threemoney));
			account.setThreeTGmoney(famt.format(threeTGmoney));
			account.setThree(three);
			account.setThreeTG(threeTG);
			account.setTwo(two);
			account.setYcsbl(ycsbl);
			account.setYcsblyq(ycsblyq);
			account.setJrycsbl(jrycsbl);
			account.setTotalCSmoney(famt.format(totalCSmoney));
			account.setTotalCSmoney1(famt.format(totalCSmoney1));
			account.setJrtotalCSmoney(famt.format(jrtotalCSmoney));
			account.setTotalYCSmoney(famt.format(totalYCSmoney));
			account.setTotalYCSmoney2(famt.format(totalYCSmoney2));
			account.setJrtotalYCSmoney(famt.format(jrtotalYCSmoney));
			shenke.add(account);
			
		}
		double countaa[] = new double[list.size()];
		int useridaa[] = new int[list.size()];
		for(int j=0 ;j<list.size();j++){
			DataRow dataRow1 = list.get(j);
			useridaa[j]=dataRow1.getInt("user_id");
			totalYCSmoney = accountShenheService.getYCSJECSWB(useridaa[j],startDate, endDate, name, phone,yuqts);//总已催收金额
			countaa[j]=totalYCSmoney; //总笔数
		}
		int nn = countaa.length;
		for (int m = 0; m < nn - 1; m++) {   
			for (int n = 0; n < nn - 1; n++) { 
				if (countaa[n] > countaa[n + 1]) { 
					double tempaa = countaa[n];   
					int tempaaa = useridaa[n]; 
					countaa[n] = countaa[n + 1];   
					useridaa[n] = useridaa[n + 1]; 
					countaa[n + 1] = tempaa;   
					useridaa[n + 1] = tempaaa;   
				}   
			}   
		}
		for(int k=0 ;k<list.size();k++){
			DataRow row = new DataRow();
			row.set("user_id", useridaa[k]);
			row.set("px", list.size()-k);
			accountShenheService.updatePXCSWB(row);	
		}
		page.setData(shenke);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
		return null;
	}
	/**
	 * 得到催收统计
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetTXHKAccount() throws java.text.ParseException{
		//分页查询所有的后台用户
		int temp = getIntParameter("temp",0);
		int curPage  =getIntParameter("curPage",1);	
		String  tempVelue = getStrParameter("tempvl");
		String  startDate =getStrParameter("startDate");
		String  endDate =getStrParameter("endDate");
		
		String name="";
		String phone="";
		if(temp==1){
			name=tempVelue;
		}else if(temp==2){
			phone=tempVelue;
		}
		DBPage page = userService.selectUserListByCondtionTX(curPage, 15, 0, name, phone); 
		List<DataRow> list=page.getData();
		List<ShenKeAccount> shenke=new ArrayList<ShenKeAccount>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String time = sdf.format(new Date());
		Calendar cd = Calendar.getInstance();
		try {
			cd.setTime(sdf.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//cd.add(Calendar.DATE, -i); // 减一天
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 减一天
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		cd.add(Calendar.DATE, 2);
	    String nextDay = sdf.format(cd.getTime());
	    String ri="";
	    String yue="";
	    if(nowdate.substring(3, 5).equals(nextDay.substring(3, 5))){
	    	ri= nextDay.substring(0, 2);
	    	yue= nextDay.substring(3, 5);
	    }else{
	    	ri= nextDay.substring(0, 2);
	    	yue= nextDay.substring(3, 5);
	    }
		DecimalFormat famt = new DecimalFormat("###,###");
		DecimalFormat df = new DecimalFormat( "0.00"); //设置double类型小数点后位数格式
	
		
		double jrtotalCSmoney=0;  //总催收金额
		
		double jrtotalYCSmoney=0;  //总已催收金额
		double jrtotalYCSmoney1=0;  //总已催收金额
		double jrtotalYCSmoney2=0;  //总已催收金额
		double jrycsbl=0;  //总已催收比例
		for (int i=0 ;i<list.size();i++) {
			DataRow dataRow = list.get(i);
			int userid=dataRow.getInt("user_id");
			ShenKeAccount account=new ShenKeAccount();
			account.setUserid(userid);
			account.setUserName(dataRow.getString("name"));
			account.setPhone(dataRow.getString("phone"));
			int dtxztxhk = accountShenheService.getJRCSZCount(userid, nowdate, dateno,name,phone);	
			int mqdtxzs = accountShenheService.getMQZBSCount(userid, nowdate, dateno,name,phone);	
			
			int jrnowOnesh=accountShenheService.getJRCSNowOneCount(userid, nowdate, dateno,name,phone);	  //今日已还款笔数
			
			int dtzbs=accountShenheService.getDTZBSNowOneCount(userid, nowdate, dateno,name,phone);	  //今日已还款笔数
			
			int dtddh=accountShenheService.getDTDDHNowOneCount(userid, nowdate, dateno,name,phone);	  //今日已还款笔数
			
			int count=accountShenheService.getCSNowCountTX(userid,startDate, endDate,name, phone);	  //总笔数
			int jrcount=accountShenheService.getJRCSNowCount(userid,startDate, endDate,name, phone);	  //提醒还款总笔数
			int jrycscount=accountShenheService.getJRCSCount(userid,startDate, endDate,name, phone);	  //提醒总还款笔数
			
			jrtotalCSmoney = accountShenheService.getJRCSJE(userid,startDate, endDate,name, phone); //今日提醒总金额
			
			jrtotalYCSmoney1 = accountShenheService.getJRYCSJE(userid,startDate, endDate, name, phone);//总已催收金额
			jrtotalYCSmoney2 = accountShenheService.getJRYCSJE1(userid,startDate, endDate, name, phone);//总已催收金额
			
			//int dtxztxhk = accountShenheService.getAllYQList(userid,ri,yue, name, phone);
			jrtotalYCSmoney = jrtotalYCSmoney1+jrtotalYCSmoney2;
			
			
			if(jrtotalCSmoney != 0){
				jrycsbl = jrtotalYCSmoney * 100 / jrtotalCSmoney ;
			}else{
				jrycsbl = 0;
			}
			account.setCount(count);
			account.setJrcount(jrcount);
			account.setMqdtxzs(mqdtxzs);
			account.setDtzbs(dtzbs);
			account.setDtddh(dtddh);
			account.setJrycscount(jrycscount);
			account.setJrnowOnesh(jrnowOnesh);
			account.setDtxztxhk(dtxztxhk);
			account.setJrycsbl(jrycsbl);
			account.setJrtotalCSmoney(famt.format(jrtotalCSmoney));
			account.setJrtotalYCSmoney(famt.format(jrtotalYCSmoney));
			shenke.add(account);
			
		}
		double countaa[] = new double[list.size()];
		int useridaa[] = new int[list.size()];
		for(int j=0 ;j<list.size();j++){
			DataRow dataRow1 = list.get(j);
			useridaa[j]=dataRow1.getInt("user_id");
			jrtotalYCSmoney1 = accountShenheService.getJRYCSJE(useridaa[j],startDate, endDate, name, phone);//总已催收金额
			jrtotalYCSmoney2 = accountShenheService.getJRYCSJE1(useridaa[j],startDate, endDate, name, phone);//总已催收金额
			countaa[j]=jrtotalYCSmoney1+jrtotalYCSmoney2; //总笔数
		}
		int nn = countaa.length;
		for (int m = 0; m < nn - 1; m++) {   
			for (int n = 0; n < nn - 1; n++) { 
				if (countaa[n] > countaa[n + 1]) { 
					double tempaa = countaa[n];   
					int tempaaa = useridaa[n]; 
					countaa[n] = countaa[n + 1];   
					useridaa[n] = useridaa[n + 1]; 
					countaa[n + 1] = tempaa;   
					useridaa[n + 1] = tempaaa;   
				}   
			}   
		}
		for(int k=0 ;k<list.size();k++){
			DataRow row = new DataRow();
			row.set("user_id", useridaa[k]);
			row.set("px", list.size()-k);
			accountShenheService.updatePX(row);	
		}
		page.setData(shenke);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
		return null;
	}
	/**
	 * 得到催收统计
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetTXHKTJAccount() throws java.text.ParseException{
		//分页查询所有的后台用户
		
		String name="";
		String phone="";
		
		DBPage page = userService.selectUserListByCondtionTX(1, 15, 0, name, phone); 
		List<DataRow> list=page.getData();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String time = sdf.format(new Date());
		Calendar cd = Calendar.getInstance();
		try {
			cd.setTime(sdf.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//cd.add(Calendar.DATE, -i); // 减一天
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 减一天
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		
        
		DecimalFormat famt = new DecimalFormat("###,###");
		DecimalFormat df = new DecimalFormat( "0.00"); //设置double类型小数点后位数格式
		
		int ashk[] =new int[list.size()];
		int yqhk[] =new int[list.size()];
		int sqyq15[] =new int[list.size()];
		int sqyq30[] =new int[list.size()];
		int bjdh[] =new int[list.size()];
		int gj[] =new int[list.size()];
		int tj[] =new int[list.size()];
		
		int dtashk[] =new int[list.size()];
		int dtyqhk[] =new int[list.size()];
		int dtsqyq15[] =new int[list.size()];
		int dtsqyq30[] =new int[list.size()];
		int dtbjdh[] =new int[list.size()];
		int dtgj[] =new int[list.size()];
		int dttj[] =new int[list.size()];
		
		for (int i=0 ;i<list.size();i++) {
			DataRow dataRow = list.get(i);
			int userid = dataRow.getInt("user_id");
			ashk[i] = accountShenheService.getASHK(userid);
			yqhk[i] = accountShenheService.getYQHK(userid);
			sqyq15[i] = accountShenheService.getSQYQ15(userid);
			sqyq30[i] = accountShenheService.getSQYQ30(userid);
			bjdh[i] = accountShenheService.getBJDH(userid);
			gj[i] = accountShenheService.getGJ(userid);
			tj[i] = accountShenheService.getTJ(userid);
			
			dtashk[i] = accountShenheService.getDTASHK(userid,nowdate);
			dtyqhk[i] = accountShenheService.getDTYQHK(userid,nowdate);
			dtsqyq15[i] = accountShenheService.getDTSQYQ15(userid,nowdate);
			dtsqyq30[i] = accountShenheService.getDTSQYQ30(userid,nowdate);
			dtbjdh[i] = accountShenheService.getDTBJDH(userid,nowdate);
			dtgj[i] = accountShenheService.getDTGJ(userid,nowdate);
			dttj[i] = accountShenheService.getDTTJ(userid,nowdate);
			
		}
		DataRow row = new DataRow();
		row.set("list",page);
		row.set("ashk", ashk);
		row.set("yqhk", yqhk);
		row.set("sqyq15", sqyq15);
		row.set("sqyq30", sqyq30);
		row.set("bjdh", bjdh);
		row.set("gj", gj);
		row.set("tj", tj);
		
		row.set("dtashk", dtashk);
		row.set("dtyqhk", dtyqhk);
		row.set("dtsqyq15", dtsqyq15);
		row.set("dtsqyq30", dtsqyq30);
		row.set("dtbjdh", dtbjdh);
		row.set("dtgj", dtgj);
		row.set("dttj", dttj);
		
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());	
		return null;
	}
}
