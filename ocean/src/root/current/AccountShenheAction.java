package root.current;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import com.project.bean.ShenKeAccount;
import com.project.service.account.AccountShenheService;
import com.project.service.role.UserService;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;
import com.thinkive.base.jdbc.DBPage;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.util.SessionHelper;
import com.thinkive.base.util.StringHelper;
import com.thinkive.web.base.ActionResult;
import com.thinkive.web.base.BaseAction;

/**
 * 锟斤拷锟酵筹拷锟�
 * @author Administrator
 *
 */
public class AccountShenheAction extends BaseAction {

	private static Logger logger= Logger.getLogger(AccountShenheAction.class);
	private static AccountShenheService accountShenheService=new AccountShenheService();
	private static UserService userService=new UserService();
	
	//xiong工具方法查询集合中不同小組的每天的审核老员工次数
		public TreeMap<String, Integer> dayshenhedaycs(List<String> listshenhe1, int curPage) {
			
			//每个小组每个审核员工每天的审核数量的集合
			List<DataRow> listxz1 = new ArrayList<DataRow>();
			for (int i = 0; i < listshenhe1.size(); i++) {
				DBPage page = accountShenheService.selectGetOldUser(listshenhe1.get(i), curPage, 40);
				List<DataRow> listdr = page.getData();
				listxz1.addAll(listdr);
			}
			
			// 审核1小组每天1审核的数量（key日期，value值,按照日期降序）
			TreeMap<String, Integer> result1 = new TreeMap<String, Integer>(new Comparator<String>() {
				public int compare(String o1, String o2) {
					return -o1.compareTo(o2);// 按照key顺序排列，o2-o1是逆序
				}
			});
			//这个计算好像有问题
			for (DataRow map1 : listxz1) {
				String id = map1.get("riqi").toString();
				int value = Integer.parseInt(map1.get("oldyonghunum").toString());
				if (result1.containsKey(id)) {
					int tempxz1 = Integer.parseInt(result1.get(id).toString());
					value += tempxz1;
				}
				result1.put(id, value);
			}
			return result1;
		}	
	
	/**
	 * xiong 根据天数时间段查询时间
	 * @return
	 * @throws java.text.ParseException 
	 */	
	public  List<String> addDates(String cntDateBeg, String cntDateEnd) {
        List<String> list = new ArrayList<String>();
        //拆分成数组
        String[] dateBegs = cntDateBeg.split("-");
        String[] dateEnds = cntDateEnd.split("-");
        //开始时间转换成时间戳
        Calendar start = Calendar.getInstance();
        start.set(Integer.valueOf(dateBegs[0]), Integer.valueOf(dateBegs[1]) - 1, Integer.valueOf(dateBegs[2]));
        Long startTIme = start.getTimeInMillis();
        //结束时间转换成时间戳
        Calendar end = Calendar.getInstance();
        end.set(Integer.valueOf(dateEnds[0]), Integer.valueOf(dateEnds[1]) - 1, Integer.valueOf(dateEnds[2]));
        Long endTime = end.getTimeInMillis();
        //定义一个一天的时间戳时长
        Long oneDay = 1000 * 60 * 60 * 24l;
        Long time = startTIme;
        //循环得出
        while (time <= endTime) {
            list.add(new SimpleDateFormat("yyyy-MM-dd").format(new Date(time)));
            time += oneDay;
        }
        Collections.reverse(list);
        return list;
    }
	
	
	
	/**
	 * xiong 鐧惧垎姣旀崲绠�
	 * @return
	 * @throws java.text.ParseException 
	 */	
	public String baifenbi(double a,double b) {
		
		if(a==0||b==0) {
			return 0+"%";
		}			 		
		// 鍒涘缓涓�涓暟鍊兼牸寮忓寲瀵硅薄   
	     NumberFormat numberFormat = NumberFormat.getInstance();   
	     // 璁剧疆绮剧‘鍒板皬鏁扮偣鍚�2浣�   
	     numberFormat.setMaximumFractionDigits(2);   
	     String result = numberFormat.format((float)a/(float)b*100);	     
	     return result+"%";
	}
	
	/**  Lin
	 * 鑾峰彇鐢ㄦ埛缁熻鍊�
	 * @param riqi   yh_total
	 * @param listData
	 * @return
	 */
	public int getListDataValue(String riqi,List<DataRow> listData ) {
		int listDatvalue =0;
		 for (int i = 0; i < listData.size(); i++) {
			 DataRow dataRow = listData.get(i);
			 String listDatariqi = dataRow.getString("riqi");
			 if (riqi.equals(listDatariqi)) {
				 return listDatvalue = dataRow.getInt("yh_total");
			}
		}
		return listDatvalue;
	}
	
	/**
	 * xiong查询每日每个小组审核多少
	 * @return
	 * @throws java.text.ParseException 
	 */
	public List<Map<String,Object>> GetOldYongHuXiaoZuAccount(int curPage){
		// 分页查询所有的后台用户
		int temp = getIntParameter("temp", 0);
		//int curPage = getIntParameter("curPage", 1);
		
		JSONObject jsonObject = new JSONObject();

		// 审核小组数据库规则表拆分
		List<DataRow> listguize = accountShenheService.selectShenHeList();
		// 审核小组1
		List<String> listshenhe1 = new ArrayList<String>();
		// 审核小组2
		List<String> listshenhe2 = new ArrayList<String>();
		// 审核小组4
		List<String> listshenhe4 = new ArrayList<String>();

		for (int i = 0; i < listguize.size(); i++) {
			DataRow dataRow = listguize.get(i);
			String shenhexiaozu = dataRow.getString("guizeneirong1");
			if (shenhexiaozu.equals("审核一组组长")) {
				String[] sh1 = dataRow.getString("guizebianliang2").split(",");
				listshenhe1 = Arrays.asList(sh1);
			} else if (shenhexiaozu.equals("审核二组组长")) {
				String[] sh2 = dataRow.getString("guizebianliang2").split(",");
				listshenhe2 = Arrays.asList(sh2);
			} else if (shenhexiaozu.equals("审核四组组长")) {
				String[] sh4 = dataRow.getString("guizebianliang2").split(",");
				listshenhe4 = Arrays.asList(sh4);
			}
		}
		
		// 查询出审核一组审核成员不管日期所有的审核数量放在一起
		TreeMap<String, Integer> b =dayshenhedaycs(listshenhe1,curPage);		
		//查询出审核2组审核成员不管日期所有的审核数量放在一起
		TreeMap<String, Integer> c =dayshenhedaycs(listshenhe2,curPage);
		//查询出审核4组审核成员不管日期所有的审核数量放在一起
		TreeMap<String, Integer> d =dayshenhedaycs(listshenhe4,curPage);
		
		//拿出所有的日期			
		TreeMap<String, Integer> e=(b.size()>c.size()? (b.size()>d.size()?b:d):(c.size()>d.size()?c:d));

		//拼接行数据 
		 List<String> dates = new ArrayList(e.keySet());
		 
	        Collections.sort(dates,new Comparator<String>(){
	            public int compare(String o1,String o2){
	                return-o1.compareTo(o2);
	            }
	        });
	        //传给前端封装数据的对象的集合
	        List<Map<String,Object>> results = new ArrayList<Map<String,Object>>();
	        for(String date:dates){
	            Map<String,Object> map = new HashMap<String,Object>();
	            map.put("day",date);
	            map.put("1",(b.get(date)==null?0:b.get(date)));
	            map.put("2",(c.get(date)==null?0:c.get(date)));
	            map.put("4",(d.get(date)==null?0:d.get(date)));
	            results.add(map);
	        }    
	        			 					
		// 封装好响应给前端的数据
//		DBPage page =new DBPage(curPage,40) ;
//		page.setData(results);
//		DataRow row = new DataRow();
//		row.set("list", page);
//		row.set("temp", temp);
//		JSONObject object = JSONObject.fromBean(row);
//		this.getWriter().write(object.toString());
		return results;
	}
	
	/**
	 * xiong查询每日老用户
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetOldYongHuAccount() throws java.text.ParseException{
		//分页查询所有的后台用户
		int temp = getIntParameter("temp",0);
		int curPage  =getIntParameter("curPage",1);
		String  tempVelue = getStrParameter("tempvl");
		String  startDate =getStrParameter("startDate");
		String  endDate =getStrParameter("endDate");
		//JSONObject jsonObject = new JSONObject();
		     	
		//查询字段每天老用户申请成功的数量
		DBPage page = accountShenheService.selectGetOldUserList(curPage,40,startDate,endDate);
		//查询字段每天老用户审核失败的数量
		DBPage pageshibaisb = accountShenheService.selectGetOldUserListsb(curPage,40,startDate,endDate);
		
		DBPage pagecghk = accountShenheService.selectGetOldUserListCGHK(curPage,40,startDate,endDate);
		
		DBPage pagecghjk = accountShenheService.selectGetOldUserListCGHKJK(curPage,40,startDate,endDate);
		//已经还款总数
		int cghk = accountShenheService.getDayCGHK();
		
		//已经复借总数
		int cghkjk = accountShenheService.getDayCGHKJK();
		
		
		List<DataRow> list=page.getData();
		
		List<DataRow> listsb=pageshibaisb.getData();
		
		List<DataRow> listcghk=pagecghk.getData();
		
		List<DataRow> listcghkjk=pagecghjk.getData();
		//System.out.println("++++++++++++++++++"+list.toString()+"----"+listsb.toString());
		
//		
//		  if(list== null || list.isEmpty()||listsb== null || listsb.isEmpty()) {
//		  jsonObject.put("code", -1); 
//		  jsonObject.put("error", "Bên trong hệ thống");
//		  this.getWriter().write(jsonObject.toString()); 
//		   return null; 
//		  }		 							
		//传给前端封装数据的对象的集合
		List<Map<String,Object>> ListShenhe=new ArrayList<Map<String,Object>>();
		//通过日期查询老用户通过多少，拒绝多少，老用户的通过率是多少，老用户的拒绝率是多少。
		//在另外一个借口中的字段：审核1小组通过多少，审核小组2通过多少，审核小组4通过多少
		for (int i = 0; i < list.size(); i++) {		
			DataRow dataRow = list.get(i);
			String riqi = dataRow.getString("riqi");
			Map<String, Object> map = new 	HashMap<String, Object>();
			//每天的日期
			map.put("day", riqi);
			//每天成功的老用户
			map.put("oldusercg", dataRow.getInt("oldyonghunum"));
			//查询字段每天还款成功的数量
			int a = 0;
			if(listsb.size()>0) {
				for (int j = 0; j < listsb.size(); j++) {		
					DataRow dataRowsb = listsb.get(j);
					String riqisb = dataRowsb.getString("riqi");
					if(riqisb!=null && riqi.equals(riqisb)) {
						a = dataRowsb.getInt("oldyonghunum");
					}
				}
			}
			map.put("oldusersb", a);
			int all=dataRow.getInt("oldyonghunum")+a;
			//每天通过率
			map.put("oldusercgl",baifenbi(dataRow.getInt("oldyonghunum"),all));
			//每天拒绝率
			map.put("oldusersbl",baifenbi(a,all));	
			//每天总的老用户
			map.put("olduserall",all);
			int b = 0;
			if(listcghk.size()>0) {
				for (int m = 0; m < listcghk.size(); m++) {		
					DataRow dataRowcghk = listcghk.get(m);
					if (riqi!=null && riqi.equals(dataRowcghk.getString("riqi"))) {
						b =  dataRowcghk.getInt("daycghk");
					}
				}
			}
			map.put("daycghk",b);
			int c = 0 ;
			if(listcghkjk.size()>0) {
				for (int n = 0; n < listcghkjk.size(); n++) {		
					DataRow dataRowcghkjk = listcghkjk.get(n);
					if (riqi!=null && riqi.equals(dataRowcghkjk.getString("riqi"))) {
						c =  dataRowcghkjk.getInt("daycghkjk");
					}
				}
			}
			map.put("daycghkjk",c);
			ListShenhe.add(map);
			
		}
	
		//公司没有分审核1组审核2组合审核3组
//			List<Map<String,Object>> listshxz= GetOldYongHuXiaoZuAccount(curPage);
//			List<Map<String,Object>> ListShenheold=new ArrayList<Map<String,Object>>();
//			for (int i = 0; i < ListShenhe.size(); i++) {
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put("day", listshxz.get(i).get("day"));
//				map.put("onezu", listshxz.get(i).get("1"));
//				map.put("twozu", listshxz.get(i).get("2"));
//				map.put("fourzu", listshxz.get(i).get("4"));
//				map.put("oldusercg",ListShenhe.get(i).get("oldusercg"));
//				map.put("oldusersb",ListShenhe.get(i).get("oldusersb"));
//				map.put("oldusercgl",ListShenhe.get(i).get("oldusercgl"));
//				map.put("oldusersbl",ListShenhe.get(i).get("oldusersbl"));
//				map.put("olduserall",ListShenhe.get(i).get("olduserall"));
//				ListShenheold.add(map);
//			}	
//		
	    //封装好响应给前端的数据
		page.setData(ListShenhe);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("cghk", cghk);
		row.set("cghkjk", cghkjk);
		row.set("temp",temp);
		row.set("tempvalu",tempVelue);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());		
		return null;	
	}
	/**
	 * xiong查询每月老用户的还款情况
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetOldYongHuAccountHK() throws java.text.ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		String nowtime = sdf.format(new Date());
		String time = "2019-05";
		Calendar cd = Calendar.getInstance();
		Calendar cd2 = Calendar.getInstance();
		try {
			cd.setTime(sdf.parse(time));
			cd2.setTime(sdf.parse(nowtime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int changdu = cd2.get(Calendar.MONTH) - cd.get(Calendar.MONTH); 
		List<Map<String,Object>> ListShenhe=new ArrayList<Map<String,Object>>();
		for (int i = 0; i <= changdu; i++) {	
			cd.setTime(sdf.parse(time));
			cd.add(Calendar.MONTH, i); 
			Date date = cd.getTime();
			String timemonth = sdf.format(date);
			Map<String, Object> map = new 	HashMap<String, Object>();
			//每天的日期
			int fjzs15 = accountShenheService.getFJZS15(timemonth);
			int fjzs30 = accountShenheService.getFJZS30(timemonth);
			int fjhk15 = accountShenheService.getFJHK15(timemonth);
			int fjhk30 = accountShenheService.getFJHK30(timemonth);
			map.put("day", timemonth);
			//每天成功的老用户
			map.put("fjzs15", fjzs15);
			//查询字段每天还款成功的数量
			map.put("fjzs30", fjzs30);
			//每天通过率
			map.put("fjhk15",fjhk15);
			//每天拒绝率
			map.put("fjhk30",fjhk30);
			int fjhk15bl=0;
			int fjhk30bl=0;
			if(fjzs15!=0) {
				fjhk15bl =100*  fjhk15 / fjzs15 ;
			}
			if(fjzs30!=0) {
				fjhk30bl =100*  fjhk30 / fjzs30 ;
			}
			map.put("fjhk15bl",fjhk15bl+"%");	
			
			map.put("fjhk30bl",fjhk30bl+"%");	
			ListShenhe.add(map);
			
		}
		DBPage page =new DBPage(1, 12);
		page.setData(ListShenhe);
		DataRow row = new DataRow();
		row.set("list", page);
		JSONObject object = JSONObject.fromBean(row);	
		this.getWriter().write(object.toString());		
		return null;	
	}
	/**
	 * 锟矫碉拷锟斤拷锟酵筹拷锟�
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetShenheAccount() throws java.text.ParseException{
		//锟斤拷页锟斤拷询锟斤拷锟叫的猴拷台锟矫伙拷
		int temp = getIntParameter("temp",0);
		int curPage  =getIntParameter("curPage",1);	
		String  tempVelue = getStrParameter("tempvl");
		String  startDate =getStrParameter("startDate");
		String  endDate =getStrParameter("endDate");
		int  recode =getIntParameter("recode",0);
		logger.info(recode);
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
		//cd.add(Calendar.DATE, -i); // 锟斤拷一锟斤拷
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 锟斤拷一锟斤拷
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
				
				//锟斤拷锟斤拷
				int nowOnesh=accountShenheService.getNowOneCount(userid, nowdate, dateno,name,phone);	  //锟斤拷锟斤拷一锟斤拷锟斤拷锟�
				int nowTwosh=accountShenheService.getNowTwoCount(userid, nowdate, dateno,name,phone);	//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
				int nowThreesh=accountShenheService.getNowThreeCount(userid, nowdate, dateno,name,phone);	//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
				int nowOneshsjrs=accountShenheService.getNowOnesjrsCount(userid, startDate,endDate,nowdate, dateno,name,phone);	  //锟斤拷锟斤拷一锟斤拷锟斤拷锟�
				int nowlyh = accountShenheService.getNowThreeCountTGOLD(userid,startDate,endDate, nowdate, dateno,name, phone);//锟斤拷锟斤拷通锟斤拷锟斤拷锟斤拷
				int nowOneshSB=accountShenheService.getNowOneCountSB(userid, nowdate, dateno,name,phone);	  //失锟杰碉拷锟斤拷一锟斤拷锟斤拷锟�
				int nowOneshSBXXCW=accountShenheService.getNowOneCountSBXXCW(userid, nowdate, dateno,name,phone);	  //失锟杰碉拷锟斤拷一锟斤拷锟斤拷锟�
				int nowTwoshSB=accountShenheService.getNowTwoCountSB(userid, nowdate, dateno,name,phone);	//失锟杰碉拷锟斤拷锟斤拷锟斤拷锟斤拷
				int nowThreeshSB=accountShenheService.getNowThreeCountSB(userid, nowdate, dateno,name,phone);	//失锟杰碉拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
				int nowtwotgfk=accountShenheService.getNowTwoCountTGFK(userid,startDate,endDate,nowdate, dateno,name,phone);	//锟斤拷锟斤拷通锟斤拷锟斤拷锟揭放匡拷锟斤拷锟�
				int nowthreeTG = accountShenheService.getNowThreeCountTG(userid,startDate,endDate, nowdate, dateno,name, phone);//锟斤拷锟斤拷通锟斤拷锟斤拷锟斤拷
				
				int nowCount=nowOnesh+nowTwosh+nowThreesh;	  //锟斤拷锟斤拷锟杰憋拷锟斤拷
				int nowCountsjrs=nowOneshsjrs;	  //锟斤拷锟斤拷锟杰憋拷锟斤拷
				int nowCountSB=nowOneshSB+nowTwoshSB+nowThreeshSB;	  //失锟杰碉拷锟斤拷锟杰憋拷锟斤拷
				
				//锟斤拷锟斤拷
				int one=0;   //一锟斤拷锟斤拷锟�
				int onesjrs=0;   //一锟斤拷锟斤拷锟�
				int two=0;	//锟斤拷锟斤拷锟斤拷锟�
				//int twosjrs=accountShenheService.getTwosjrsCount(userid,startDate, endDate,name,phone);	//锟斤拷锟斤拷锟斤拷锟�
				int twotgfk=0;	//锟斤拷锟斤拷通锟斤拷锟斤拷锟揭放匡拷锟斤拷锟�
				int twotghk=0;	//锟斤拷锟斤拷通锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
				int twotgfkyq=0;	//锟斤拷锟斤拷通锟斤拷锟斤拷锟揭放匡拷锟斤拷锟�
				int twotgfkyqwh=0;	//锟斤拷锟斤拷通锟斤拷锟斤拷锟揭放匡拷锟斤拷锟�
				int three=0; //锟斤拷锟斤拷锟斤拷锟�
				//int threesjrs=accountShenheService.getThreesjrsCount(userid,startDate, endDate,name,phone); //锟斤拷锟斤拷锟斤拷锟�
				int oneSB=0;   //失锟斤拷一锟斤拷锟斤拷锟�
				int oneSBXXCW=0;   //失锟斤拷一锟斤拷锟斤拷息锟斤拷锟斤拷锟斤拷锟�
				int twoSB=0;	//失锟杰讹拷锟斤拷锟斤拷锟�
				int threeSB=0; //失锟斤拷锟斤拷锟斤拷锟斤拷锟�
				int threeTG = 0;//锟斤拷锟斤拷通锟斤拷锟斤拷锟斤拷
				//int yh = accountShenheService.getYH(userid, startDate, endDate,name, phone);//锟斤拷锟斤拷锟窖伙拷
				//int four=accountShenheService.getThreeCountYQ(userid, startDate, endDate,name, phone);	//锟斤拷锟斤拷锟斤拷锟节憋拷锟斤拷
				int four = twotgfkyq ; 
				//int yqwh = accountShenheService.getYQWH(userid, startDate, endDate,name, phone);//锟斤拷锟斤拷未锟斤拷锟斤拷锟斤拷
				int count=one+two+three; //锟杰憋拷锟斤拷
				//int countsjrs=onesjrs+twosjrs+threesjrs; //锟杰憋拷锟斤拷
				int countsjrs=onesjrs; //锟杰憋拷锟斤拷
				int countSB=oneSB+twoSB+threeSB; //失锟斤拷锟杰憋拷锟斤拷
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
				
				//锟斤拷锟斤拷
				int nowOnesh=accountShenheService.getNowOneCount(userid, nowdate, dateno,name,phone);	  //锟斤拷锟斤拷一锟斤拷锟斤拷锟�
				int nowTwosh=accountShenheService.getNowTwoCount(userid, nowdate, dateno,name,phone);	//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
				int nowThreesh=accountShenheService.getNowThreeCount(userid, nowdate, dateno,name,phone);	//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
				int nowOneshsjrs=accountShenheService.getNowOnesjrsCount(userid, startDate,endDate,nowdate, dateno,name,phone);	  //锟斤拷锟斤拷一锟斤拷锟斤拷锟�
				//int nowTwoshsjrs=accountShenheService.getNowTwosjrsCount(userid,startDate,endDate, nowdate, dateno,name,phone);	//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
				//int nowThreeshsjrs=accountShenheService.getNowThreesjrsCount(userid,startDate,endDate, nowdate, dateno,name,phone);	//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
				int nowOneshSB=accountShenheService.getNowOneCountSB(userid, nowdate, dateno,name,phone);	  //失锟杰碉拷锟斤拷一锟斤拷锟斤拷锟�
				int nowOneshSBXXCW=accountShenheService.getNowOneCountSBXXCW(userid, nowdate, dateno,name,phone);	  //失锟杰碉拷锟斤拷一锟斤拷锟斤拷锟�
				int nowTwoshSB=accountShenheService.getNowTwoCountSB(userid, nowdate, dateno,name,phone);	//失锟杰碉拷锟斤拷锟斤拷锟斤拷锟斤拷
				int nowThreeshSB=accountShenheService.getNowThreeCountSB(userid, nowdate, dateno,name,phone);	//失锟杰碉拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
				int nowtwotgfk=accountShenheService.getNowTwoCountTGFK(userid,startDate,endDate,nowdate, dateno,name,phone);	//锟斤拷锟斤拷通锟斤拷锟斤拷锟揭放匡拷锟斤拷锟�
				int nowthreeTG = accountShenheService.getNowThreeCountTG(userid,startDate,endDate, nowdate, dateno,name, phone);//锟斤拷锟斤拷通锟斤拷锟斤拷锟斤拷
				
				int nowCount=nowOnesh+nowTwosh+nowThreesh;	  //锟斤拷锟斤拷锟杰憋拷锟斤拷
				int nowCountsjrs=nowOneshsjrs;	  //锟斤拷锟斤拷锟杰憋拷锟斤拷
				int nowCountSB=nowOneshSB+nowTwoshSB+nowThreeshSB;	  //失锟杰碉拷锟斤拷锟杰憋拷锟斤拷
				int nowlyh = accountShenheService.getNowThreeCountTGOLD(userid,startDate,endDate, nowdate, dateno,name, phone);//锟斤拷锟斤拷通锟斤拷锟斤拷锟斤拷
				//锟斤拷锟斤拷
				int one=accountShenheService.getOneCount(userid,startDate, endDate,name,phone);   //一锟斤拷锟斤拷锟�
				int onesjrs=accountShenheService.getOnesjrsCount(userid,startDate, endDate,name,phone);   //一锟斤拷锟斤拷锟�
				int two=accountShenheService.getTwoCount(userid,startDate, endDate,name,phone);	//锟斤拷锟斤拷锟斤拷锟�
				//int twosjrs=accountShenheService.getTwosjrsCount(userid,startDate, endDate,name,phone);	//锟斤拷锟斤拷锟斤拷锟�
				int twotgfk=accountShenheService.getTwoCountTGFK(userid,startDate, endDate,name,phone);	//锟斤拷锟斤拷通锟斤拷锟斤拷锟揭放匡拷锟斤拷锟�
				int twotghk=accountShenheService.getTwoCountTGHK(userid,startDate, endDate,name,phone);	//锟斤拷锟斤拷通锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
				int twotgfkyq=accountShenheService.getTwoCountTGFKYQ(userid,startDate, endDate,name,phone);	//锟斤拷锟斤拷通锟斤拷锟斤拷锟揭放匡拷锟斤拷锟�
				int twotgfkyqwh=accountShenheService.getTwoCountTGFKYQWH(userid,startDate, endDate,name,phone);	//锟斤拷锟斤拷通锟斤拷锟斤拷锟揭放匡拷锟斤拷锟�
				int three=accountShenheService.getThreeCount(userid,startDate, endDate,name,phone); //锟斤拷锟斤拷锟斤拷锟�
				//int threesjrs=accountShenheService.getThreesjrsCount(userid,startDate, endDate,name,phone); //锟斤拷锟斤拷锟斤拷锟�
				int oneSB=accountShenheService.getOneCountSB(userid,startDate, endDate,name,phone);   //失锟斤拷一锟斤拷锟斤拷锟�
				int oneSBXXCW=accountShenheService.getOneCountSBXXCW(userid,startDate, endDate,name,phone);   //失锟斤拷一锟斤拷锟斤拷息锟斤拷锟斤拷锟斤拷锟�
				int twoSB=accountShenheService.getTwoCountSB(userid,startDate, endDate,name,phone);	//失锟杰讹拷锟斤拷锟斤拷锟�
				int threeSB=accountShenheService.getThreeCountSB(userid,startDate, endDate,name,phone); //失锟斤拷锟斤拷锟斤拷锟斤拷锟�
				int threeTG = accountShenheService.getThreeCountTG(userid, startDate, endDate,name, phone);//锟斤拷锟斤拷通锟斤拷锟斤拷锟斤拷
				//int yh = accountShenheService.getYH(userid, startDate, endDate,name, phone);//锟斤拷锟斤拷锟窖伙拷
				//int four=accountShenheService.getThreeCountYQ(userid, startDate, endDate,name, phone);	//锟斤拷锟斤拷锟斤拷锟节憋拷锟斤拷
				int four = twotgfkyq ; 
				//int yqwh = accountShenheService.getYQWH(userid, startDate, endDate,name, phone);//锟斤拷锟斤拷未锟斤拷锟斤拷锟斤拷
				int count=one+two+three; //锟杰憋拷锟斤拷
				//int countsjrs=onesjrs+twosjrs+threesjrs; //锟杰憋拷锟斤拷
				int countsjrs=onesjrs; //锟杰憋拷锟斤拷
				int countSB=oneSB+twoSB+threeSB; //失锟斤拷锟杰憋拷锟斤拷
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
			int nowOneshsjrs=accountShenheService.getNowOnesjrsCount(useridaa[j], startDate,endDate,nowdate, dateno,name,phone);	  //锟斤拷锟斤拷一锟斤拷锟斤拷锟�
			int nowTwoshsjrs=accountShenheService.getNowTwosjrsCount(useridaa[j],startDate,endDate, nowdate, dateno,name,phone);	//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
			int nowThreeshsjrs=accountShenheService.getNowThreesjrsCount(useridaa[j],startDate,endDate, nowdate, dateno,name,phone);	//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
			countaa[j]=nowOneshsjrs+nowTwoshsjrs+nowThreeshsjrs; //锟杰憋拷锟斤拷
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
	public ActionResult doGetShenheAccountDay() throws java.text.ParseException{
		//锟斤拷页锟斤拷询锟斤拷锟叫的猴拷台锟矫伙拷
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
		//cd.add(Calendar.DATE, -i); // 锟斤拷一锟斤拷
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 锟斤拷一锟斤拷
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
				
				//chuli danshu
				int nowOneshsjrs=accountShenheService.getNowOnesjrsCount(userid, startDate,endDate,nowdate, dateno,name,phone);	
				//dahui danshu
				int nowOneshSBXXCW=accountShenheService.getNowOneCountSBXXCWDay(userid,startDate,endDate, nowdate, dateno,name,phone);
				//dahui huilai danshu
				int nowOneshSBXXCWHL=accountShenheService.getNowOneCountSBXXCWDayHL(userid,startDate,endDate, nowdate, dateno,name,phone);
				//jujue danshu
				int nowOneshSB=accountShenheService.getNowOneCountSBDay(userid, startDate,endDate,nowdate, dateno,name,phone);	
				//tongguo danshu
				int nowOnetgfk=accountShenheService.getNowOneCountTGFK(userid,startDate,endDate,nowdate, dateno,name,phone);	
				int dhbl = 0;
				int jjbl = 0;
				int tgbl = 0;
				if(nowOneshsjrs!=0) {
					dhbl = nowOneshSBXXCW * 100 / nowOneshsjrs;
					jjbl = nowOneshSB * 100 / nowOneshsjrs;
					tgbl = nowOnetgfk * 100 / nowOneshsjrs;
				}
				account.setNowCountsjrs(nowOneshsjrs);
				account.setNowOneshSBXXCW(nowOneshSBXXCW);
				account.setOneSB(nowOneshSBXXCWHL);
				account.setNowOneshSB(nowOneshSB);
				account.setNowtwotgfk(nowOnetgfk);
				account.setYqbl(dhbl);
				account.setYqwhbl(jjbl);
				account.setNowCount(tgbl);
				shenke.add(account);
		}
		int countaa[] = new int[list.size()];
		int useridaa[] = new int[list.size()];
		for(int j=0 ;j<list.size();j++){
			DataRow dataRow1 = list.get(j);
			useridaa[j]=dataRow1.getInt("user_id");
			int nowOneshsjrs=accountShenheService.getNowOnesjrsCount(useridaa[j], startDate,endDate,nowdate, dateno,name,phone);	
			countaa[j]=nowOneshsjrs; //锟杰憋拷锟斤拷
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
	 * 锟矫碉拷锟斤拷锟酵筹拷锟�
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetShenheAccountRSKPI() throws java.text.ParseException{
		//锟斤拷页锟斤拷询锟斤拷锟叫的猴拷台锟矫伙拷
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
		//cd.add(Calendar.DATE, -i); // 锟斤拷一锟斤拷
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 锟斤拷一锟斤拷
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
				
				//锟斤拷锟斤拷
				int twotgfk=accountShenheService.getTwoCountTGFK(userid,startDate, endDate,name,phone);	//锟斤拷锟斤拷通锟斤拷锟斤拷锟揭放匡拷锟斤拷锟�
				int h5twotgfk = accountShenheService.getH5TwoCountTGFK(userid, startDate, endDate, name, phone);//h5锟斤拷锟斤拷锟斤拷锟矫伙拷
				int nowlyh = accountShenheService.getCountLYH(userid, startDate, endDate, name, phone);//老用户
				int twotgzhk = twotgfk - nowlyh ;
				int apptwotgfk = twotgfk - h5twotgfk;
				account.setNowlyh(nowlyh);
				account.setTwotgfk(twotgfk);
				account.setTwotgzhk(twotgzhk);
				account.setH5twotgfk(h5twotgfk);
				account.setApptwotgfk(apptwotgfk);
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
	 * 锟矫碉拷锟斤拷锟酵筹拷锟�
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetShuJuAccount() throws java.text.ParseException{
		//锟斤拷页锟斤拷询锟斤拷锟叫的猴拷台锟矫伙拷
		int temp = getIntParameter("temp",0);
		int curPage  =getIntParameter("curPage",1);	
		String  tempVelue = getStrParameter("tempvl");
		String  startDate =getStrParameter("startDate");
		String  endDate =getStrParameter("endDate");
		
		SimpleDateFormat sdfymd = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String time = sdf.format(new Date());
		Calendar cd = Calendar.getInstance();
		try {
			cd.setTime(sdf.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if("".equals(endDate)) {
			endDate =sdfymd.format(new Date());
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
	/*	zong[0] = "锟斤拷统锟斤拷";
		fifteen[0]="锟斤拷锟斤拷锟斤拷锟�15锟斤拷";
		thirty[0]="锟斤拷锟斤拷锟斤拷锟�30锟斤拷";
		newuser[0]="锟斤拷锟矫伙拷(一锟轿斤拷锟�)";
		olduser[0]="锟斤拷锟矫伙拷(锟斤拷锟斤拷一锟轿斤拷锟�)";
		havashuju[0]="锟斤拷匹锟戒到锟斤拷锟斤拷";
		noshuju[0]="没锟斤拷匹锟戒到锟斤拷锟斤拷";
		pingfen3[0]="锟斤拷锟斤拷为3";
		pingfen2[0]="锟斤拷锟斤拷为2";
		pingfen1[0]="锟斤拷锟斤拷为1";
		pingfenf1[0]="锟斤拷锟斤拷为-1";*/
		
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
		
		//zong[3] = zong[1] + zong[5];
		zong[3] =accountShenheService.getZong2hk(startDate, endDate) + accountShenheService.getZong2hkfq(startDate, endDate);
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
		
		//cd.add(Calendar.DATE, -i); // 锟斤拷一锟斤拷
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 锟斤拷一锟斤拷
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
	 * 锟矫碉拷锟脚匡拷统锟斤拷
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetFKAccount() throws java.text.ParseException{
		//锟斤拷页锟斤拷询锟斤拷锟叫的猴拷台锟矫伙拷
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
		//cd.add(Calendar.DATE, -i); // 锟斤拷一锟斤拷
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 锟斤拷一锟斤拷
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		for (int i=0 ;i<list.size();i++) {
			DataRow dataRow = list.get(i);
			int userid=dataRow.getInt("user_id");
			ShenKeAccount account=new ShenKeAccount();
			account.setUserid(userid);
			account.setUserName(dataRow.getString("name"));
			account.setPhone(dataRow.getString("phone"));
			int nowFKCG=accountShenheService.getFKCGCount(userid, nowdate, dateno,name,phone);	  //锟斤拷锟斤拷趴锟缴癸拷锟斤拷锟斤拷
			
			int nowCount=nowFKCG;	  //锟斤拷锟斤拷锟杰憋拷锟斤拷
			//锟斤拷锟斤拷锟斤拷锟斤拷Agribank
			int offlinezs = accountShenheService.getOfflineZS(userid, nowdate, dateno,name,phone);//锟斤拷锟斤拷锟斤拷锟斤拷
			int agribank = accountShenheService.getAgriBank(userid, nowdate, dateno,name,phone);
			int vietcombank = accountShenheService.getVietcomBank(userid, nowdate, dateno,name,phone);
			int offSacombank = offlinezs - agribank - vietcombank ;
			//锟斤拷锟斤拷锟斤拷锟斤拷
			int onSacombank = accountShenheService.getOnlineSacombank(userid, nowdate, dateno,name,phone);
			
			int FKCG=accountShenheService.getFKCGCountZS(userid,name,phone);   //锟脚匡拷晒锟斤拷锟斤拷锟�
			
			int count=FKCG; //锟杰憋拷锟斤拷
			
			
			
				
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
			int FKCG1=accountShenheService.getFKCGCountZS(useridaa[j],name,phone);   //锟脚匡拷晒锟斤拷锟斤拷锟�
			int FKSB1=accountShenheService.getFKSBCountZS(useridaa[j],name,phone);	//锟脚匡拷失锟杰憋拷锟斤拷
			countaa[j]=FKCG1+FKSB1; //锟杰憋拷锟斤拷
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
	 * 锟矫碉拷锟脚匡拷统锟斤拷
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetKFHFAccount() throws java.text.ParseException{
		//锟斤拷页锟斤拷询锟斤拷锟叫的猴拷台锟矫伙拷
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
		//cd.add(Calendar.DATE, -i); // 锟斤拷一锟斤拷
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 锟斤拷一锟斤拷
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		for (int i=0 ;i<list.size();i++) {
			DataRow dataRow = list.get(i);
			int userid=dataRow.getInt("user_id");
			ShenKeAccount account=new ShenKeAccount();
			account.setUserid(userid);
			account.setUserName(dataRow.getString("name"));
			account.setPhone(dataRow.getString("phone"));
			int nowFKCG=accountShenheService.getKFHFCGCount(userid, nowdate, dateno,name,phone);	  //锟斤拷锟斤拷趴锟缴癸拷锟斤拷锟斤拷
			
			int nowCount=nowFKCG;	  //锟斤拷锟斤拷锟杰憋拷锟斤拷
			int FKCG=accountShenheService.getKFHFCGCountZS(userid,name,phone);   //锟脚匡拷晒锟斤拷锟斤拷锟�
			int count=FKCG; //锟杰憋拷锟斤拷
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
			int FKCG1=accountShenheService.getKFHFCGCountZS(useridaa[j],name,phone);   //锟脚匡拷晒锟斤拷锟斤拷锟�
			countaa[j]=FKCG1; //锟杰憋拷锟斤拷
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
	 * 锟矫碉拷锟斤拷锟斤拷统锟斤拷
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetCuishouAccount() throws java.text.ParseException{
		//锟斤拷页锟斤拷询锟斤拷锟叫的猴拷台锟矫伙拷
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
		//cd.add(Calendar.DATE, -i); // 锟斤拷一锟斤拷
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 锟斤拷一锟斤拷
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		DecimalFormat famt = new DecimalFormat("###,###");
		DecimalFormat df = new DecimalFormat( "0.00"); //锟斤拷锟斤拷double锟斤拷锟斤拷小锟斤拷锟斤拷锟轿伙拷锟斤拷锟绞�
		double onemoney = 0; 
		double twomoney = 0; 
		double threemoney = 0; 
		double threeTGmoney = 0; 
		double totalCSmoney=0;  //锟杰达拷锟秸斤拷锟�
		double jrtotalCSmoney=0;  //锟杰达拷锟秸斤拷锟�
		double jrtotalCSmoney1=0;  //锟杰达拷锟秸斤拷锟�
		double totalYCSmoney=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney1=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney2=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney3=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double totalCSmoney1=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double totalYCSmoney2=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double totalYCSmoney3=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double ycsbl=0;  //锟斤拷锟窖达拷锟秸憋拷锟斤拷
		double ycsblyq=0;  //锟斤拷锟窖达拷锟秸憋拷锟斤拷
		double jrycsbl=0;  //锟斤拷锟窖达拷锟秸憋拷锟斤拷
		for (int i=0 ;i<list.size();i++) {
			DataRow dataRow = list.get(i);
			int userid=dataRow.getInt("user_id");
			ShenKeAccount account=new ShenKeAccount();
			account.setUserid(userid);
			account.setUserName(dataRow.getString("name"));
			account.setPhone(dataRow.getString("phone"));
			int jrnowOnesh=accountShenheService.getJRCSNowOneCount(userid, nowdate, dateno,name,phone);	  //锟斤拷锟斤拷M1锟斤拷锟斤拷
			int nowOnesh=accountShenheService.getCSNowOneCount(userid, nowdate, dateno,name,phone);	  //锟斤拷锟斤拷M1锟斤拷锟斤拷
			int nowTwosh=accountShenheService.getCSNowTwoCount(userid, nowdate, dateno,name,phone);	//锟斤拷锟斤拷M2锟斤拷锟斤拷
			int nowThreesh=accountShenheService.getCSNowThreeCount(userid, nowdate, dateno,name,phone);	//锟斤拷锟斤拷M3锟斤拷锟斤拷
			int nowFoursh=accountShenheService.getCSNowFourCount(userid, nowdate, dateno, name, phone); //锟斤拷锟斤拷M4锟斤拷锟斤拷
			int count=accountShenheService.getCSNowCount(userid,startDate, endDate,name, phone);	  //锟杰憋拷锟斤拷
			int jrcount=accountShenheService.getDYCSNowCount(userid,startDate, endDate,nowdate,name, phone);//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟杰憋拷锟斤拷
			int jrycscount=accountShenheService.getDYCSCount(userid,startDate, endDate,nowdate,name, phone);//锟斤拷锟斤拷锟窖达拷锟秸憋拷锟斤拷
			int one=accountShenheService.getCSOneCount(userid,startDate, endDate,name,phone);   //M1锟斤拷锟斤拷
			int two=accountShenheService.getCSTwoCount(userid,startDate, endDate,name,phone);	//M2锟斤拷锟斤拷
			int three=accountShenheService.getCSThreeCount(userid,startDate, endDate,name,phone); //M3锟斤拷锟斤拷
			int threeTG = accountShenheService.getCSFourCount(userid,startDate, endDate, name, phone);//M4锟斤拷锟斤拷
			int nowCount=accountShenheService.getCSDateNowCount(userid,nowdate, dateno,name, phone); //锟杰憋拷锟斤拷
			onemoney = accountShenheService.getOneMoney(userid,startDate, endDate,name, phone);
			twomoney = accountShenheService.getTwoMoney(userid,startDate, endDate,name, phone);
			threemoney = accountShenheService.getThreeMoney(userid,startDate, endDate,name, phone);
		    threeTGmoney = accountShenheService.getThreeTGMoney(userid,startDate, endDate,name, phone);
			totalCSmoney = accountShenheService.getCSJE(userid,startDate, endDate,name, phone,yuqts); //锟杰达拷锟秸斤拷锟�
			jrtotalCSmoney1 = accountShenheService.getDYCSJE(userid,startDate, endDate,nowdate,name, phone,yuqts); //锟斤拷锟铰达拷锟斤拷锟杰斤拷锟�
			totalYCSmoney = accountShenheService.getYCSJE(userid,startDate, endDate, name, phone,yuqts);//锟斤拷锟窖达拷锟秸斤拷锟�
			totalYCSmoney2 = accountShenheService.getYCSJEYQ(userid,startDate, endDate, name, phone,yuqts);//锟斤拷锟窖达拷锟斤拷锟斤拷锟节斤拷锟�
			totalYCSmoney3 = accountShenheService.getYCSJEBF(userid,startDate, endDate, name, phone,yuqts);//锟斤拷锟窖达拷锟秸诧拷锟街斤拷锟�
			int countyq = accountShenheService.getYCSJEYQCount(userid,startDate, endDate, name, phone,yuqts);//锟斤拷锟窖达拷锟斤拷锟斤拷锟节斤拷锟�
			totalCSmoney1 = totalCSmoney - totalYCSmoney2 ; // 锟杰达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷锟斤拷冢锟�
			totalYCSmoney  = totalYCSmoney + totalYCSmoney3;
			jrtotalCSmoney = jrtotalCSmoney1; // 锟杰达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷锟斤拷冢锟�
			jrtotalYCSmoney1 = accountShenheService.getDYYCSJE(userid,startDate, endDate,nowdate, name, phone,yuqts);//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷锟�
			jrtotalYCSmoney2 = accountShenheService.getDYYCSJE1(userid,startDate, endDate, name, phone,yuqts);//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷冢锟�
			jrtotalYCSmoney3 = accountShenheService.getDYYCSJE2(userid,startDate, endDate,nowdate, name, phone,yuqts);//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷冢锟�
			jrtotalYCSmoney = jrtotalYCSmoney1 + jrtotalYCSmoney3;
			if(totalCSmoney1 != 0){
				ycsbl = totalYCSmoney * 100 / totalCSmoney1 ;
			}else{
				ycsbl = 0;
			}
			//锟斤拷锟节的憋拷锟斤拷
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
			totalYCSmoney = accountShenheService.getYCSJE(useridaa[j],startDate, endDate, name, phone,yuqts);//锟斤拷锟窖达拷锟秸斤拷锟�
			countaa[j]=totalYCSmoney; //锟杰憋拷锟斤拷
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
	 * 锟矫碉拷锟斤拷锟斤拷统锟斤拷M123
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetCuishouAccount123() throws java.text.ParseException{
		//锟斤拷页锟斤拷询锟斤拷锟叫的猴拷台锟矫伙拷
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
		//cd.add(Calendar.DATE, -i); // 锟斤拷一锟斤拷
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 锟斤拷一锟斤拷
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		DecimalFormat famt = new DecimalFormat("###,###");
		DecimalFormat df = new DecimalFormat( "0.00"); //锟斤拷锟斤拷double锟斤拷锟斤拷小锟斤拷锟斤拷锟轿伙拷锟斤拷锟绞�
		double onemoney = 0; 
		
		double totalCSmoney=0;  //锟杰达拷锟秸斤拷锟�
		
		double totalCSmoneySS=0;  //锟杰达拷锟秸斤拷锟�
		
		double jrtotalCSmoney=0;  //锟杰达拷锟秸斤拷锟�
		double jrtotalCSmoney1=0;  //锟杰达拷锟秸斤拷锟�
		
		double totalYCSmoney=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double totalYCSmoneySS=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		
		double jrtotalYCSmoney=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney1=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney2=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney3=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double totalCSmoney1=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		
		double totalYCSmoney2=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		
		double totalYCSmoney2SS=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		
		double totalYCSmoney3=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		
		double totalYCSmoney3SS=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		
		double totalYCSmoneyDT=0;  //锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷锟�
		
		double ycsbl=0;  //锟斤拷锟窖达拷锟秸憋拷锟斤拷
		double kpi=0;  //锟斤拷锟窖达拷锟秸憋拷锟斤拷
		double ycsblyq=0;  //锟斤拷锟窖达拷锟秸憋拷锟斤拷
		double jrycsbl=0;  //锟斤拷锟窖达拷锟秸憋拷锟斤拷
		
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
				int countSS=accountShenheService.getCSNowCountM1SS(userid,startDate, endDate,name, phone,number);	  //锟杰憋拷锟斤拷
				
				int oneSS=accountShenheService.getCSOneCountM1SS(userid,startDate, endDate,name,phone,number);   //M1锟斤拷锟斤拷
				
				SimpleDateFormat sdf111 = new SimpleDateFormat("yyyy-MM-dd");
				String time111 = sdf111.format(new Date());
				
				totalCSmoneySS = accountShenheService.getYQ1T(userid,startDate, endDate,name, phone,yuqts,number); //锟杰达拷锟秸斤拷锟�
				totalYCSmoneySS = accountShenheService.getYQ2T(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟秸斤拷锟�
				
				
				//锟斤拷锟斤拷
				int nowCount=(int)accountShenheService.getYQ3T(userid,startDate, endDate, name, phone,yuqts,number); //锟杰憋拷锟斤拷
				totalYCSmoneyDT = totalCSmoneySS +  totalYCSmoneySS +  nowCount ;
				

				/*if(totalYCSmoneyDT != 0 && countSS != 0 ){
					kpi = totalYCSmoneyDT * 100 / countSS ;
				}else{
					kpi = 0;
				}*/
				//锟斤拷锟斤拷
				int count=0;	  //锟杰憋拷锟斤拷
				int one=0;   //M1锟斤拷锟斤拷
				onemoney =0;
				
				int jrcount=0;//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟杰憋拷锟斤拷
				int jrycscount=0;//锟斤拷锟斤拷锟窖达拷锟秸憋拷锟斤拷
				
				totalCSmoney = 0; //锟杰达拷锟秸斤拷锟�
				
				jrtotalCSmoney1 = 0; //锟斤拷锟铰达拷锟斤拷锟杰斤拷锟�
				
				totalYCSmoney = 0;//锟斤拷锟窖达拷锟秸斤拷锟�
				
				//totalYCSmoney2 = accountShenheService.getYCSJEYQM1(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟斤拷锟斤拷锟节斤拷锟�
				
				totalYCSmoney3 =0;//锟斤拷锟窖达拷锟秸诧拷锟街斤拷锟�
				
				//int countyq = accountShenheService.getYCSJEYQCountM1(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟斤拷锟斤拷锟节斤拷锟�
				
				
				totalYCSmoney  = totalYCSmoney + totalYCSmoney3;
				
				jrtotalCSmoney = jrtotalCSmoney1; // 锟杰达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷锟斤拷冢锟�
				jrtotalYCSmoney1 = 0;//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷锟�
				jrtotalYCSmoney2 = 0;//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷冢锟�
				jrtotalYCSmoney3 = 0;//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷冢锟�
				jrtotalYCSmoney = jrtotalYCSmoney1 + jrtotalYCSmoney3;
				/*if(totalCSmoney1 != 0){
					ycsbl = totalYCSmoney * 100 / totalCSmoney1 ;
				}else{
					ycsbl = 0;
				}*/
				//锟斤拷锟节的憋拷锟斤拷
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
				int countSS=accountShenheService.getCSNowCountM1SS(userid,startDate, endDate,name, phone,number);	  //锟杰憋拷锟斤拷
				
				int oneSS=accountShenheService.getCSOneCountM1SS(userid,startDate, endDate,name,phone,number);   //M1锟斤拷锟斤拷
				totalCSmoneySS = accountShenheService.getCSJEM1SS(userid,startDate, endDate,name, phone,yuqts,number); //锟杰达拷锟秸斤拷锟�
				totalYCSmoneySS = accountShenheService.getYCSJEM1SS(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟秸斤拷锟�
				totalYCSmoney3SS = accountShenheService.getYCSJEBFM1SS(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟秸诧拷锟街斤拷锟�
				totalYCSmoneySS  = totalYCSmoneySS + totalYCSmoney3SS;
				if(totalCSmoneySS != 0){
					kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
				}else{
					kpi = 0;
				}*/
				//KPI
				int countSS=accountShenheService.getCSNowCountM1SS(userid,startDate, endDate,name, phone,number);	  //锟杰憋拷锟斤拷
				
				int oneSS=accountShenheService.getCSOneCountM1SS(userid,startDate, endDate,name,phone,number);   //M1锟斤拷锟斤拷
				
				SimpleDateFormat sdf111 = new SimpleDateFormat("yyyy-MM-dd");
				String time111 = sdf111.format(new Date());
				
				totalCSmoneySS = accountShenheService.getCSJEM1SSBG(userid,startDate, endDate,name, phone,yuqts,number); //锟杰达拷锟秸斤拷锟�
				totalYCSmoneySS = accountShenheService.getYCSJEM1SSBG(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟秸斤拷锟�
				
				if(totalCSmoneySS != 0){
					kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
				}else{
					kpi = 0;
				}
				//锟斤拷锟斤拷
				int nowCount=accountShenheService.getCSDateNowCountM1(userid,nowdate, dateno,name, phone); //锟杰憋拷锟斤拷
				int nowOnesh=accountShenheService.getCSNowOneCountM1(userid, nowdate, dateno,name,phone);	  //锟斤拷锟斤拷M1锟斤拷锟斤拷
				totalYCSmoneyDT = accountShenheService.getCSDateNowCountDTJE(userid,nowdate, dateno,name, phone); //锟斤拷锟斤拷锟斤拷锟饺拷罨癸拷锟斤拷锟�
				//锟斤拷锟斤拷
				int count=accountShenheService.getCSNowCountM1(userid,startDate, endDate,name, phone);	  //锟杰憋拷锟斤拷
				int one=accountShenheService.getCSOneCountM1(userid,startDate, endDate,name,phone);   //M1锟斤拷锟斤拷
				onemoney = accountShenheService.getOneMoneyM1(userid,startDate, endDate,name, phone);
				
				int jrcount=accountShenheService.getDYCSNowCountM1(userid,startDate, endDate,nowdate,name, phone);//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟杰憋拷锟斤拷
				int jrycscount=accountShenheService.getDYCSCountM1(userid,startDate, endDate,nowdate,name, phone);//锟斤拷锟斤拷锟窖达拷锟秸憋拷锟斤拷
				
				totalCSmoney = accountShenheService.getCSJEM1(userid,startDate, endDate,name, phone,yuqts); //锟杰达拷锟秸斤拷锟�
				
				jrtotalCSmoney1 = accountShenheService.getDYCSJEM1(userid,startDate, endDate,nowdate,name, phone,yuqts); //锟斤拷锟铰达拷锟斤拷锟杰斤拷锟�
				
				totalYCSmoney = accountShenheService.getYCSJEM1(userid,startDate, endDate, name, phone,yuqts);//锟斤拷锟窖达拷锟秸斤拷锟�
				
				totalYCSmoney2 = accountShenheService.getYCSJEYQM1(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟斤拷锟斤拷锟节斤拷锟�
				
				totalYCSmoney3 = accountShenheService.getYCSJEBFM1(userid,startDate, endDate, name, phone,yuqts);//锟斤拷锟窖达拷锟秸诧拷锟街斤拷锟�
				
				int countyq = accountShenheService.getYCSJEYQCountM1(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟斤拷锟斤拷锟节斤拷锟�
				totalCSmoney1 = totalCSmoney - totalYCSmoney2 ; // 锟杰达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷锟斤拷冢锟�
				totalYCSmoney  = totalYCSmoney + totalYCSmoney3;
				
				jrtotalCSmoney = jrtotalCSmoney1; // 锟杰达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷锟斤拷冢锟�
				jrtotalYCSmoney1 = accountShenheService.getDYYCSJEM1(userid,startDate, endDate,nowdate, name, phone,yuqts);//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷锟�
				jrtotalYCSmoney2 = accountShenheService.getDYYCSJE1M1(userid,startDate, endDate, name, phone,yuqts);//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷冢锟�
				jrtotalYCSmoney3 = accountShenheService.getDYYCSJE2M1(userid,startDate, endDate,nowdate, name, phone,yuqts);//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷冢锟�
				jrtotalYCSmoney = jrtotalYCSmoney1 + jrtotalYCSmoney3;
				if(totalCSmoney1 != 0){
					ycsbl = totalYCSmoney * 100 / totalCSmoney1 ;
				}else{
					ycsbl = 0;
				}
				
				//锟斤拷锟节的憋拷锟斤拷
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
			totalYCSmoney = accountShenheService.getYCSJE(useridaa[j],startDate, endDate, name, phone,yuqts);//锟斤拷锟窖达拷锟秸斤拷锟�
			countaa[j]=totalYCSmoney; //锟杰憋拷锟斤拷
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
	 * 锟矫碉拷锟斤拷锟斤拷统锟斤拷M0
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetCuishouAccountM0() throws java.text.ParseException{
		//锟斤拷页锟斤拷询锟斤拷锟叫的猴拷台锟矫伙拷
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
		//cd.add(Calendar.DATE, -i); // 锟斤拷一锟斤拷
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 锟斤拷一锟斤拷
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		DecimalFormat famt = new DecimalFormat("###,###");
		DecimalFormat df = new DecimalFormat( "0.00"); //锟斤拷锟斤拷double锟斤拷锟斤拷小锟斤拷锟斤拷锟轿伙拷锟斤拷锟绞�
		double onemoney = 0; 
		
		double totalCSmoney=0;  //锟杰达拷锟秸斤拷锟�
		
		double totalCSmoneySS=0;  //锟杰达拷锟秸斤拷锟�
		
		double jrtotalCSmoney=0;  //锟杰达拷锟秸斤拷锟�
		double jrtotalCSmoney1=0;  //锟杰达拷锟秸斤拷锟�
		
		double totalYCSmoney=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double totalYCSmoneySS=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		
		double jrtotalYCSmoney=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney1=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney2=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney3=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double totalCSmoney1=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		
		double totalYCSmoney2=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		
		double totalYCSmoney2SS=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		
		double totalYCSmoney3=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		
		double totalYCSmoney3SS=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		
		double totalYCSmoneyDT=0;  //锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷锟�
		
		double ycsbl=0;  //锟斤拷锟窖达拷锟秸憋拷锟斤拷
		double kpi=0;  //锟斤拷锟窖达拷锟秸憋拷锟斤拷
		double ycsblyq=0;  //锟斤拷锟窖达拷锟秸憋拷锟斤拷
		double jrycsbl=0;  //锟斤拷锟窖达拷锟秸憋拷锟斤拷
		
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
				int countSS=accountShenheService.getCSNowCountM1SS(userid,startDate, endDate,name, phone,number);	  //锟杰憋拷锟斤拷
				
				int oneSS=accountShenheService.getCSOneCountM1SS(userid,startDate, endDate,name,phone,number);   //M1锟斤拷锟斤拷
				totalCSmoneySS = accountShenheService.getCSJEM1SS(userid,startDate, endDate,name, phone,yuqts,number); //锟杰达拷锟秸斤拷锟�
				totalYCSmoneySS = accountShenheService.getYCSJEM1SS(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟秸斤拷锟�
				totalYCSmoney3SS = accountShenheService.getYCSJEBFM1SS(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟秸诧拷锟街斤拷锟�
				totalYCSmoneySS  = totalYCSmoneySS + totalYCSmoney3SS;
				if(totalCSmoneySS != 0){
					kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
				}else{
					kpi = 0;
				}*/
				//KPI
				int countSS=accountShenheService.getCSNowCountM0SS(userid,startDate, endDate,name, phone,number);	  //锟杰憋拷锟斤拷
				
				int oneSS=accountShenheService.getCSOneCountM0SS(userid,startDate, endDate,name,phone,number);   //M1锟斤拷锟斤拷
				
				SimpleDateFormat sdf111 = new SimpleDateFormat("yyyy-MM-dd");
				String time111 = sdf111.format(new Date());
				
				totalCSmoneySS = accountShenheService.getCSJEM1SSBG(userid,startDate, endDate,name, phone,yuqts,number); //锟杰达拷锟秸斤拷锟�
				totalYCSmoneySS = accountShenheService.getYCSJEM1SSBG(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟秸斤拷锟�
				
				if(totalCSmoneySS != 0){
					kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
				}else{
					kpi = 0;
				}
				
				//锟斤拷锟斤拷
				int nowCount=accountShenheService.getCSDateNowCountM0(userid,nowdate, dateno,name, phone); //锟杰憋拷锟斤拷
				totalYCSmoneyDT = accountShenheService.getCSDateNowCountDTJEM0(userid,nowdate, dateno,name, phone); //锟斤拷锟斤拷锟斤拷锟饺拷罨癸拷锟斤拷锟�
				
				//锟斤拷锟斤拷
				int count=0;	  //锟杰憋拷锟斤拷
				int one=0;   //M1锟斤拷锟斤拷
				onemoney =0;
				
				int jrcount=0;//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟杰憋拷锟斤拷
				int jrycscount=0;//锟斤拷锟斤拷锟窖达拷锟秸憋拷锟斤拷
				
				totalCSmoney = 0; //锟杰达拷锟秸斤拷锟�
				
				jrtotalCSmoney1 = 0; //锟斤拷锟铰达拷锟斤拷锟杰斤拷锟�
				
				totalYCSmoney = 0;//锟斤拷锟窖达拷锟秸斤拷锟�
				
				totalYCSmoney2 = accountShenheService.getYCSJEYQM1(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟斤拷锟斤拷锟节斤拷锟�
				
				totalYCSmoney3 =0;//锟斤拷锟窖达拷锟秸诧拷锟街斤拷锟�
				
				int countyq = accountShenheService.getYCSJEYQCountM1(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟斤拷锟斤拷锟节斤拷锟�
				
				
				totalYCSmoney  = totalYCSmoney + totalYCSmoney3;
				
				jrtotalCSmoney = jrtotalCSmoney1; // 锟杰达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷锟斤拷冢锟�
				jrtotalYCSmoney1 = 0;//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷锟�
				jrtotalYCSmoney2 = 0;//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷冢锟�
				jrtotalYCSmoney3 = 0;//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷冢锟�
				jrtotalYCSmoney = jrtotalYCSmoney1 + jrtotalYCSmoney3;
				if(totalCSmoney1 != 0){
					ycsbl = totalYCSmoney * 100 / totalCSmoney1 ;
				}else{
					ycsbl = 0;
				}
				//锟斤拷锟节的憋拷锟斤拷
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
				int countSS=accountShenheService.getCSNowCountM1SS(userid,startDate, endDate,name, phone,number);	  //锟杰憋拷锟斤拷
				
				int oneSS=accountShenheService.getCSOneCountM1SS(userid,startDate, endDate,name,phone,number);   //M1锟斤拷锟斤拷
				totalCSmoneySS = accountShenheService.getCSJEM1SS(userid,startDate, endDate,name, phone,yuqts,number); //锟杰达拷锟秸斤拷锟�
				totalYCSmoneySS = accountShenheService.getYCSJEM1SS(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟秸斤拷锟�
				totalYCSmoney3SS = accountShenheService.getYCSJEBFM1SS(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟秸诧拷锟街斤拷锟�
				totalYCSmoneySS  = totalYCSmoneySS + totalYCSmoney3SS;
				if(totalCSmoneySS != 0){
					kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
				}else{
					kpi = 0;
				}*/
				//KPI
				int countSS=accountShenheService.getCSNowCountM0SS(userid,startDate, endDate,name, phone,number);	  //锟杰憋拷锟斤拷
				
				int oneSS=accountShenheService.getCSOneCountM0SS(userid,startDate, endDate,name,phone,number);   //M1锟斤拷锟斤拷
				
				SimpleDateFormat sdf111 = new SimpleDateFormat("yyyy-MM-dd");
				String time111 = sdf111.format(new Date());
				
				totalCSmoneySS = accountShenheService.getCSJEM1SSBG(userid,startDate, endDate,name, phone,yuqts,number); //锟杰达拷锟秸斤拷锟�
				totalYCSmoneySS = accountShenheService.getYCSJEM1SSBG(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟秸斤拷锟�
				
				if(totalCSmoneySS != 0){
					kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
				}else{
					kpi = 0;
				}
				//锟斤拷锟斤拷
				int nowCount=accountShenheService.getCSDateNowCountM0(userid,nowdate, dateno,name, phone); //锟杰憋拷锟斤拷
				int nowOnesh=accountShenheService.getCSNowOneCountM0(userid, nowdate, dateno,name,phone);	  //锟斤拷锟斤拷M1锟斤拷锟斤拷
				totalYCSmoneyDT = accountShenheService.getCSDateNowCountDTJEM0(userid,nowdate, dateno,name, phone); //锟斤拷锟斤拷锟斤拷锟饺拷罨癸拷锟斤拷锟�
				//锟斤拷锟斤拷
				int count=accountShenheService.getCSNowCountM0(userid,startDate, endDate,name, phone);	  //锟杰憋拷锟斤拷
				int one=accountShenheService.getCSOneCountM0(userid,startDate, endDate,name,phone);   //M1锟斤拷锟斤拷
				onemoney = accountShenheService.getOneMoneyM0(userid,startDate, endDate,name, phone);
				
				int jrcount=accountShenheService.getDYCSNowCountM0(userid,startDate, endDate,nowdate,name, phone);//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟杰憋拷锟斤拷
				int jrycscount=accountShenheService.getDYCSCountM0(userid,startDate, endDate,nowdate,name, phone);//锟斤拷锟斤拷锟窖达拷锟秸憋拷锟斤拷
				
				totalCSmoney = accountShenheService.getCSJEM0(userid,startDate, endDate,name, phone,yuqts); //锟杰达拷锟秸斤拷锟�
				
				jrtotalCSmoney1 = accountShenheService.getDYCSJEM0(userid,startDate, endDate,nowdate,name, phone,yuqts); //锟斤拷锟铰达拷锟斤拷锟杰斤拷锟�
				
				totalYCSmoney = accountShenheService.getYCSJEM0(userid,startDate, endDate, name, phone,yuqts);//锟斤拷锟窖达拷锟秸斤拷锟�
				
				totalYCSmoney2 = accountShenheService.getYCSJEYQM0(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟斤拷锟斤拷锟节斤拷锟�
				
				totalYCSmoney3 = accountShenheService.getYCSJEBFM0(userid,startDate, endDate, name, phone,yuqts);//锟斤拷锟窖达拷锟秸诧拷锟街斤拷锟�
				
				int countyq = accountShenheService.getYCSJEYQCountM0(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟斤拷锟斤拷锟节斤拷锟�
				totalCSmoney1 = totalCSmoney - totalYCSmoney2 ; // 锟杰达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷锟斤拷冢锟�
				totalYCSmoney  = totalYCSmoney + totalYCSmoney3;
				
				jrtotalCSmoney = jrtotalCSmoney1; // 锟杰达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷锟斤拷冢锟�
				jrtotalYCSmoney1 = accountShenheService.getDYYCSJEM0(userid,startDate, endDate,nowdate, name, phone,yuqts);//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷锟�
				jrtotalYCSmoney2 = accountShenheService.getDYYCSJE1M0(userid,startDate, endDate, name, phone,yuqts);//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷冢锟�
				jrtotalYCSmoney3 = accountShenheService.getDYYCSJE2M0(userid,startDate, endDate,nowdate, name, phone,yuqts);//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷冢锟�
				jrtotalYCSmoney = jrtotalYCSmoney1 + jrtotalYCSmoney3;
				if(totalCSmoney1 != 0){
					ycsbl = totalYCSmoney * 100 / totalCSmoney1 ;
				}else{
					ycsbl = 0;
				}
				
				//锟斤拷锟节的憋拷锟斤拷
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
			totalYCSmoney = accountShenheService.getYCSJEM0(useridaa[j],startDate, endDate, name, phone,yuqts);//锟斤拷锟窖达拷锟秸斤拷锟�
			countaa[j]=totalYCSmoney; //锟杰憋拷锟斤拷
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
	 * 锟矫碉拷锟斤拷锟斤拷统锟斤拷M1
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetCuishouAccountM1() throws java.text.ParseException{
		//锟斤拷页锟斤拷询锟斤拷锟叫的猴拷台锟矫伙拷
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
		//cd.add(Calendar.DATE, -i); // 锟斤拷一锟斤拷
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 锟斤拷一锟斤拷
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		DecimalFormat famt = new DecimalFormat("###,###");
		DecimalFormat df = new DecimalFormat( "0.00"); //锟斤拷锟斤拷double锟斤拷锟斤拷小锟斤拷锟斤拷锟轿伙拷锟斤拷锟绞�
		double onemoney = 0; 
		
		double totalCSmoney=0;  //锟杰达拷锟秸斤拷锟�
		
		double totalCSmoneySS=0;  //锟杰达拷锟秸斤拷锟�
		
		double jrtotalCSmoney=0;  //锟杰达拷锟秸斤拷锟�
		double jrtotalCSmoney1=0;  //锟杰达拷锟秸斤拷锟�
		
		double totalYCSmoney=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double totalYCSmoneySS=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		
		double jrtotalYCSmoney=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney1=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney2=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney3=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double totalCSmoney1=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		
		double totalYCSmoney2=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		
		double totalYCSmoney2SS=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		
		double totalYCSmoney3=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		
		double totalYCSmoney3SS=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		
		double totalYCSmoneyDT=0;  //锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷锟�
		
		double ycsbl=0;  //锟斤拷锟窖达拷锟秸憋拷锟斤拷
		double kpi=0;  //锟斤拷锟窖达拷锟秸憋拷锟斤拷
		double ycsblyq=0;  //锟斤拷锟窖达拷锟秸憋拷锟斤拷
		double jrycsbl=0;  //锟斤拷锟窖达拷锟秸憋拷锟斤拷
		
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
				int countSS=accountShenheService.getCSNowCountM1SS(userid,startDate, endDate,name, phone,number);	  //锟杰憋拷锟斤拷
				
				int oneSS=accountShenheService.getCSOneCountM1SS(userid,startDate, endDate,name,phone,number);   //M1锟斤拷锟斤拷
				totalCSmoneySS = accountShenheService.getCSJEM1SS(userid,startDate, endDate,name, phone,yuqts,number); //锟杰达拷锟秸斤拷锟�
				totalYCSmoneySS = accountShenheService.getYCSJEM1SS(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟秸斤拷锟�
				totalYCSmoney3SS = accountShenheService.getYCSJEBFM1SS(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟秸诧拷锟街斤拷锟�
				totalYCSmoneySS  = totalYCSmoneySS + totalYCSmoney3SS;
				if(totalCSmoneySS != 0){
					kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
				}else{
					kpi = 0;
				}*/
				//KPI
				int countSS=accountShenheService.getCSNowCountM1SS(userid,startDate, endDate,name, phone,number);	  //锟杰憋拷锟斤拷
				
				int oneSS=accountShenheService.getCSOneCountM1SS(userid,startDate, endDate,name,phone,number);   //M1锟斤拷锟斤拷
				
				SimpleDateFormat sdf111 = new SimpleDateFormat("yyyy-MM-dd");
				String time111 = sdf111.format(new Date());
				
				totalCSmoneySS = accountShenheService.getCSJEM1SSBG(userid,startDate, endDate,name, phone,yuqts,number); //锟杰达拷锟秸斤拷锟�
				totalYCSmoneySS = accountShenheService.getYCSJEM1SSBG(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟秸斤拷锟�
				
				if(totalCSmoneySS != 0){
					kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
				}else{
					kpi = 0;
				}
				
				//锟斤拷锟斤拷
				int nowCount=accountShenheService.getCSDateNowCountM1(userid,nowdate, dateno,name, phone); //锟杰憋拷锟斤拷
				totalYCSmoneyDT = accountShenheService.getCSDateNowCountDTJE(userid,nowdate, dateno,name, phone); //锟斤拷锟斤拷锟斤拷锟饺拷罨癸拷锟斤拷锟�
				
				//锟斤拷锟斤拷
				int count=0;	  //锟杰憋拷锟斤拷
				int one=0;   //M1锟斤拷锟斤拷
				onemoney =0;
				
				int jrcount=0;//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟杰憋拷锟斤拷
				int jrycscount=0;//锟斤拷锟斤拷锟窖达拷锟秸憋拷锟斤拷
				
				totalCSmoney = 0; //锟杰达拷锟秸斤拷锟�
				
				jrtotalCSmoney1 = 0; //锟斤拷锟铰达拷锟斤拷锟杰斤拷锟�
				
				totalYCSmoney = 0;//锟斤拷锟窖达拷锟秸斤拷锟�
				
				totalYCSmoney2 = accountShenheService.getYCSJEYQM1(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟斤拷锟斤拷锟节斤拷锟�
				
				totalYCSmoney3 =0;//锟斤拷锟窖达拷锟秸诧拷锟街斤拷锟�
				
				int countyq = accountShenheService.getYCSJEYQCountM1(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟斤拷锟斤拷锟节斤拷锟�
				
				
				totalYCSmoney  = totalYCSmoney + totalYCSmoney3;
				
				jrtotalCSmoney = jrtotalCSmoney1; // 锟杰达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷锟斤拷冢锟�
				jrtotalYCSmoney1 = 0;//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷锟�
				jrtotalYCSmoney2 = 0;//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷冢锟�
				jrtotalYCSmoney3 = 0;//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷冢锟�
				jrtotalYCSmoney = jrtotalYCSmoney1 + jrtotalYCSmoney3;
				if(totalCSmoney1 != 0){
					ycsbl = totalYCSmoney * 100 / totalCSmoney1 ;
				}else{
					ycsbl = 0;
				}
				//锟斤拷锟节的憋拷锟斤拷
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
				int countSS=accountShenheService.getCSNowCountM1SS(userid,startDate, endDate,name, phone,number);	  //锟杰憋拷锟斤拷
				
				int oneSS=accountShenheService.getCSOneCountM1SS(userid,startDate, endDate,name,phone,number);   //M1锟斤拷锟斤拷
				totalCSmoneySS = accountShenheService.getCSJEM1SS(userid,startDate, endDate,name, phone,yuqts,number); //锟杰达拷锟秸斤拷锟�
				totalYCSmoneySS = accountShenheService.getYCSJEM1SS(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟秸斤拷锟�
				totalYCSmoney3SS = accountShenheService.getYCSJEBFM1SS(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟秸诧拷锟街斤拷锟�
				totalYCSmoneySS  = totalYCSmoneySS + totalYCSmoney3SS;
				if(totalCSmoneySS != 0){
					kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
				}else{
					kpi = 0;
				}*/
				//KPI
				int countSS=accountShenheService.getCSNowCountM1SS(userid,startDate, endDate,name, phone,number);	  //锟杰憋拷锟斤拷
				
				int oneSS=accountShenheService.getCSOneCountM1SS(userid,startDate, endDate,name,phone,number);   //M1锟斤拷锟斤拷
				
				SimpleDateFormat sdf111 = new SimpleDateFormat("yyyy-MM-dd");
				String time111 = sdf111.format(new Date());
				
				totalCSmoneySS = accountShenheService.getCSJEM1SSBG(userid,startDate, endDate,name, phone,yuqts,number); //锟杰达拷锟秸斤拷锟�
				totalYCSmoneySS = accountShenheService.getYCSJEM1SSBG(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟秸斤拷锟�
				
				if(totalCSmoneySS != 0){
					kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
				}else{
					kpi = 0;
				}
				//锟斤拷锟斤拷
				int nowCount=accountShenheService.getCSDateNowCountM1(userid,nowdate, dateno,name, phone); //锟杰憋拷锟斤拷
				int nowOnesh=accountShenheService.getCSNowOneCountM1(userid, nowdate, dateno,name,phone);	  //锟斤拷锟斤拷M1锟斤拷锟斤拷
				totalYCSmoneyDT = accountShenheService.getCSDateNowCountDTJE(userid,nowdate, dateno,name, phone); //锟斤拷锟斤拷锟斤拷锟饺拷罨癸拷锟斤拷锟�
				//锟斤拷锟斤拷
				int count=accountShenheService.getCSNowCountM1(userid,startDate, endDate,name, phone);	  //锟杰憋拷锟斤拷
				int one=accountShenheService.getCSOneCountM1(userid,startDate, endDate,name,phone);   //M1锟斤拷锟斤拷
				onemoney = accountShenheService.getOneMoneyM1(userid,startDate, endDate,name, phone);
				
				int jrcount=accountShenheService.getDYCSNowCountM1(userid,startDate, endDate,nowdate,name, phone);//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟杰憋拷锟斤拷
				int jrycscount=accountShenheService.getDYCSCountM1(userid,startDate, endDate,nowdate,name, phone);//锟斤拷锟斤拷锟窖达拷锟秸憋拷锟斤拷
				
				totalCSmoney = accountShenheService.getCSJEM1(userid,startDate, endDate,name, phone,yuqts); //锟杰达拷锟秸斤拷锟�
				
				jrtotalCSmoney1 = accountShenheService.getDYCSJEM1(userid,startDate, endDate,nowdate,name, phone,yuqts); //锟斤拷锟铰达拷锟斤拷锟杰斤拷锟�
				
				totalYCSmoney = accountShenheService.getYCSJEM1(userid,startDate, endDate, name, phone,yuqts);//锟斤拷锟窖达拷锟秸斤拷锟�
				
				totalYCSmoney2 = accountShenheService.getYCSJEYQM1(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟斤拷锟斤拷锟节斤拷锟�
				
				totalYCSmoney3 = accountShenheService.getYCSJEBFM1(userid,startDate, endDate, name, phone,yuqts);//锟斤拷锟窖达拷锟秸诧拷锟街斤拷锟�
				
				int countyq = accountShenheService.getYCSJEYQCountM1(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟斤拷锟斤拷锟节斤拷锟�
				totalCSmoney1 = totalCSmoney - totalYCSmoney2 ; // 锟杰达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷锟斤拷冢锟�
				totalYCSmoney  = totalYCSmoney + totalYCSmoney3;
				
				jrtotalCSmoney = jrtotalCSmoney1; // 锟杰达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷锟斤拷冢锟�
				jrtotalYCSmoney1 = accountShenheService.getDYYCSJEM1(userid,startDate, endDate,nowdate, name, phone,yuqts);//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷锟�
				jrtotalYCSmoney2 = accountShenheService.getDYYCSJE1M1(userid,startDate, endDate, name, phone,yuqts);//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷冢锟�
				jrtotalYCSmoney3 = accountShenheService.getDYYCSJE2M1(userid,startDate, endDate,nowdate, name, phone,yuqts);//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷冢锟�
				jrtotalYCSmoney = jrtotalYCSmoney1 + jrtotalYCSmoney3;
				if(totalCSmoney1 != 0){
					ycsbl = totalYCSmoney * 100 / totalCSmoney1 ;
				}else{
					ycsbl = 0;
				}
				
				//锟斤拷锟节的憋拷锟斤拷
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
			totalYCSmoney = accountShenheService.getYCSJE(useridaa[j],startDate, endDate, name, phone,yuqts);//锟斤拷锟窖达拷锟秸斤拷锟�
			countaa[j]=totalYCSmoney; //锟杰憋拷锟斤拷
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
	 * 锟矫碉拷锟斤拷锟斤拷统锟斤拷M2
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetCuishouAccountM2() throws java.text.ParseException{
		//锟斤拷页锟斤拷询锟斤拷锟叫的猴拷台锟矫伙拷
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
		//cd.add(Calendar.DATE, -i); // 锟斤拷一锟斤拷
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 锟斤拷一锟斤拷
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		DecimalFormat famt = new DecimalFormat("###,###");
		DecimalFormat df = new DecimalFormat( "0.00"); //锟斤拷锟斤拷double锟斤拷锟斤拷小锟斤拷锟斤拷锟轿伙拷锟斤拷锟绞�
		
		double twomoney = 0; 
		
		double totalCSmoney=0;  //锟杰达拷锟秸斤拷锟�
		double jrtotalCSmoney=0;  //锟杰达拷锟秸斤拷锟�
		double jrtotalCSmoney1=0;  //锟杰达拷锟秸斤拷锟�
		double totalYCSmoney=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney1=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney2=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney3=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double totalCSmoney1=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double totalYCSmoney2=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double totalYCSmoney3=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double totalYCSmoneyDT=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double ycsbl=0;  //锟斤拷锟窖达拷锟秸憋拷锟斤拷
		double ycsblyq=0;  //锟斤拷锟窖达拷锟秸憋拷锟斤拷
		double jrycsbl=0;  //锟斤拷锟窖达拷锟秸憋拷锟斤拷
		
		double totalYCSmoneySS=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
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
			int countSS=accountShenheService.getCSNowCountM2SS(userid,startDate, endDate,name, phone,number);	  //锟杰憋拷锟斤拷
			
			int oneSS=accountShenheService.getCSOneCountM2SS(userid,startDate, endDate,name,phone,number);   //M1锟斤拷锟斤拷
			totalCSmoneySS = accountShenheService.getCSJEM2SS(userid,startDate, endDate,name, phone,yuqts,number); //锟杰达拷锟秸斤拷锟�
			totalYCSmoneySS = accountShenheService.getYCSJEM2SS(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟秸斤拷锟�
			totalYCSmoney3SS = accountShenheService.getYCSJEBFM2SS(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟秸诧拷锟街斤拷锟�
			totalYCSmoneySS  = totalYCSmoneySS + totalYCSmoney3SS;
			if(totalCSmoneySS != 0){
				kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
			}else{
				kpi = 0;
			}*/
			
			//KPI
			int countSS=accountShenheService.getCSNowCountM2SS(userid,startDate, endDate,name, phone,number);	  //锟杰憋拷锟斤拷
			
			int oneSS=accountShenheService.getCSOneCountM2SS(userid,startDate, endDate,name,phone,number);   //M1锟斤拷锟斤拷
			totalCSmoneySS = accountShenheService.getCSJEM2SSBG(userid,startDate, endDate,name, phone,yuqts,number); //锟杰达拷锟秸斤拷锟�
			totalYCSmoneySS = accountShenheService.getYCSJEM2SSBG(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟秸斤拷锟�
			
			if(totalCSmoneySS != 0){
				kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
			}else{
				kpi = 0;
			}
			
			int nowTwosh=accountShenheService.getCSNowTwoCountM2(userid, nowdate, dateno,name,phone);	//锟斤拷锟斤拷M2锟斤拷锟斤拷
			
			int count=accountShenheService.getCSNowCountM2(userid,startDate, endDate,name, phone);	  //锟杰憋拷锟斤拷
			int jrcount=accountShenheService.getDYCSNowCountM2(userid,startDate, endDate,nowdate,name, phone);//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟杰憋拷锟斤拷
			int jrycscount=accountShenheService.getDYCSCountM2(userid,startDate, endDate,nowdate,name, phone);//锟斤拷锟斤拷锟窖达拷锟秸憋拷锟斤拷
			
			int two=accountShenheService.getCSTwoCountM2(userid,startDate, endDate,name,phone);	//M2锟斤拷锟斤拷
			
			int nowCount=accountShenheService.getCSDateNowCountM2(userid,nowdate, dateno,name, phone); //锟杰憋拷锟斤拷
			totalYCSmoneyDT = accountShenheService.getCSDateNowCountDTJEM2(userid,nowdate, dateno,name, phone); //锟斤拷锟斤拷锟斤拷锟饺拷罨癸拷锟斤拷锟�
			
			twomoney = accountShenheService.getTwoMoneyM2(userid,startDate, endDate,name, phone);
			
			totalCSmoney = accountShenheService.getCSJEM2(userid,startDate, endDate,name, phone,yuqts); //锟杰达拷锟秸斤拷锟�
			jrtotalCSmoney1 = accountShenheService.getDYCSJEM2(userid,startDate, endDate,nowdate,name, phone,yuqts); //锟斤拷锟铰达拷锟斤拷锟杰斤拷锟�
			totalYCSmoney = accountShenheService.getYCSJEM2(userid,startDate, endDate, name, phone,yuqts);//锟斤拷锟窖达拷锟秸斤拷锟�
			totalYCSmoney2 = accountShenheService.getYCSJEYQM2(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟斤拷锟斤拷锟节斤拷锟�
			totalYCSmoney3 = accountShenheService.getYCSJEBFM2(userid,startDate, endDate, name, phone,yuqts);//锟斤拷锟窖达拷锟秸诧拷锟街斤拷锟�
			int countyq = accountShenheService.getYCSJEYQCountM2(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟斤拷锟斤拷锟节斤拷锟�
			totalCSmoney1 = totalCSmoney - totalYCSmoney2 ; // 锟杰达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷锟斤拷冢锟�
			totalYCSmoney  = totalYCSmoney + totalYCSmoney3;
			jrtotalCSmoney = jrtotalCSmoney1; // 锟杰达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷锟斤拷冢锟�
			jrtotalYCSmoney1 = accountShenheService.getDYYCSJEM2(userid,startDate, endDate,nowdate, name, phone,yuqts);//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷锟�
			jrtotalYCSmoney2 = accountShenheService.getDYYCSJE1M2(userid,startDate, endDate, name, phone,yuqts);//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷冢锟�
			jrtotalYCSmoney3 = accountShenheService.getDYYCSJE2M2(userid,startDate, endDate,nowdate, name, phone,yuqts);//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷冢锟�
			jrtotalYCSmoney = jrtotalYCSmoney1 + jrtotalYCSmoney3;
			if(totalCSmoney1 != 0){
				ycsbl = totalYCSmoney * 100 / totalCSmoney1 ;
			}else{
				ycsbl = 0;
			}
			//锟斤拷锟节的憋拷锟斤拷
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
			totalYCSmoney = accountShenheService.getYCSJE(useridaa[j],startDate, endDate, name, phone,yuqts);//锟斤拷锟窖达拷锟秸斤拷锟�
			countaa[j]=totalYCSmoney; //锟杰憋拷锟斤拷
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
	 * 锟矫碉拷锟斤拷锟斤拷统锟斤拷M2
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetCuishouAccountM3() throws java.text.ParseException{
		//锟斤拷页锟斤拷询锟斤拷锟叫的猴拷台锟矫伙拷
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
		//cd.add(Calendar.DATE, -i); // 锟斤拷一锟斤拷
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 锟斤拷一锟斤拷
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		DecimalFormat famt = new DecimalFormat("###,###");
		DecimalFormat df = new DecimalFormat( "0.00"); //锟斤拷锟斤拷double锟斤拷锟斤拷小锟斤拷锟斤拷锟轿伙拷锟斤拷锟绞�
		
		double twomoney = 0; 
		
		double totalCSmoney=0;  //锟杰达拷锟秸斤拷锟�
		double jrtotalCSmoney=0;  //锟杰达拷锟秸斤拷锟�
		double jrtotalCSmoney1=0;  //锟杰达拷锟秸斤拷锟�
		double totalYCSmoney=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney1=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney2=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney3=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double totalCSmoney1=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double totalYCSmoney2=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double totalYCSmoney3=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double totalYCSmoneyDT=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double ycsbl=0;  //锟斤拷锟窖达拷锟秸憋拷锟斤拷
		double ycsblyq=0;  //锟斤拷锟窖达拷锟秸憋拷锟斤拷
		double jrycsbl=0;  //锟斤拷锟窖达拷锟秸憋拷锟斤拷
		
		double totalYCSmoneySS=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
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
			int countSS=accountShenheService.getCSNowCountM2SS(userid,startDate, endDate,name, phone,number);	  //锟杰憋拷锟斤拷
			
			int oneSS=accountShenheService.getCSOneCountM2SS(userid,startDate, endDate,name,phone,number);   //M1锟斤拷锟斤拷
			totalCSmoneySS = accountShenheService.getCSJEM2SS(userid,startDate, endDate,name, phone,yuqts,number); //锟杰达拷锟秸斤拷锟�
			totalYCSmoneySS = accountShenheService.getYCSJEM2SS(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟秸斤拷锟�
			totalYCSmoney3SS = accountShenheService.getYCSJEBFM2SS(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟秸诧拷锟街斤拷锟�
			totalYCSmoneySS  = totalYCSmoneySS + totalYCSmoney3SS;
			if(totalCSmoneySS != 0){
				kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
			}else{
				kpi = 0;
			}*/
			
			//KPI
			int countSS=accountShenheService.getCSNowCountM3SS(userid,startDate, endDate,name, phone,number);	  //锟杰憋拷锟斤拷
			
			int oneSS=accountShenheService.getCSOneCountM3SS(userid,startDate, endDate,name,phone,number);   //M1锟斤拷锟斤拷
			totalCSmoneySS = accountShenheService.getCSJEM3SSBG(userid,startDate, endDate,name, phone,yuqts,number); //锟杰达拷锟秸斤拷锟�
			totalYCSmoneySS = accountShenheService.getYCSJEM3SSBG(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟秸斤拷锟�
			
			if(totalCSmoneySS != 0){
				kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
			}else{
				kpi = 0;
			}
			
			int nowTwosh=accountShenheService.getCSNowTwoCountM3(userid, nowdate, dateno,name,phone);	//锟斤拷锟斤拷M2锟斤拷锟斤拷
			
			int count=accountShenheService.getCSNowCountM3(userid,startDate, endDate,name, phone);	  //锟杰憋拷锟斤拷
			int jrcount=accountShenheService.getDYCSNowCountM3(userid,startDate, endDate,nowdate,name, phone);//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟杰憋拷锟斤拷
			int jrycscount=accountShenheService.getDYCSCountM3(userid,startDate, endDate,nowdate,name, phone);//锟斤拷锟斤拷锟窖达拷锟秸憋拷锟斤拷
			
			int two=accountShenheService.getCSTwoCountM3(userid,startDate, endDate,name,phone);	//M2锟斤拷锟斤拷
			
			int nowCount=accountShenheService.getCSDateNowCountM3(userid,nowdate, dateno,name, phone); //锟杰憋拷锟斤拷
			totalYCSmoneyDT = accountShenheService.getCSDateNowCountDTJEM3(userid,nowdate, dateno,name, phone); //锟斤拷锟斤拷锟斤拷锟饺拷罨癸拷锟斤拷锟�
			
			twomoney = accountShenheService.getTwoMoneyM3(userid,startDate, endDate,name, phone);
			
			totalCSmoney = accountShenheService.getCSJEM3(userid,startDate, endDate,name, phone,yuqts); //锟杰达拷锟秸斤拷锟�
			jrtotalCSmoney1 = accountShenheService.getDYCSJEM3(userid,startDate, endDate,nowdate,name, phone,yuqts); //锟斤拷锟铰达拷锟斤拷锟杰斤拷锟�
			totalYCSmoney = accountShenheService.getYCSJEM3(userid,startDate, endDate, name, phone,yuqts);//锟斤拷锟窖达拷锟秸斤拷锟�
			totalYCSmoney2 = accountShenheService.getYCSJEYQM3(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟斤拷锟斤拷锟节斤拷锟�
			totalYCSmoney3 = accountShenheService.getYCSJEBFM3(userid,startDate, endDate, name, phone,yuqts);//锟斤拷锟窖达拷锟秸诧拷锟街斤拷锟�
			int countyq = accountShenheService.getYCSJEYQCountM3(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟斤拷锟斤拷锟节斤拷锟�
			totalCSmoney1 = totalCSmoney - totalYCSmoney2 ; // 锟杰达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷锟斤拷冢锟�
			totalYCSmoney  = totalYCSmoney + totalYCSmoney3;
			jrtotalCSmoney = jrtotalCSmoney1; // 锟杰达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷锟斤拷冢锟�
			jrtotalYCSmoney1 = accountShenheService.getDYYCSJEM3(userid,startDate, endDate,nowdate, name, phone,yuqts);//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷锟�
			jrtotalYCSmoney2 = accountShenheService.getDYYCSJE1M3(userid,startDate, endDate, name, phone,yuqts);//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷冢锟�
			jrtotalYCSmoney3 = accountShenheService.getDYYCSJE2M3(userid,startDate, endDate,nowdate, name, phone,yuqts);//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷冢锟�
			jrtotalYCSmoney = jrtotalYCSmoney1 + jrtotalYCSmoney3;
			if(totalCSmoney1 != 0){
				ycsbl = totalYCSmoney * 100 / totalCSmoney1 ;
			}else{
				ycsbl = 0;
			}
			//锟斤拷锟节的憋拷锟斤拷
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
			totalYCSmoney = accountShenheService.getYCSJE(useridaa[j],startDate, endDate, name, phone,yuqts);//锟斤拷锟窖达拷锟秸斤拷锟�
			countaa[j]=totalYCSmoney; //锟杰憋拷锟斤拷
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
	public ActionResult doGetCuishouAccountM1ZG() throws java.text.ParseException{
		//锟斤拷页锟斤拷询锟斤拷锟叫的猴拷台锟矫伙拷
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
		DBPage page = userService.selectUserListByCondtionCSM1ZG(curPage,40, 0, name, phone); 
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
		//cd.add(Calendar.DATE, -i); // 锟斤拷一锟斤拷
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 锟斤拷一锟斤拷
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		DecimalFormat famt = new DecimalFormat("###,###");
		double totalCSmoneySS=0;  //锟杰达拷锟秸斤拷锟�
		double totalYCSmoneySS=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double totalYCSmoney2=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double totalYCSmoneyDT=0;  //锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷锟�
		double kpi=0;  //锟斤拷锟窖达拷锟秸憋拷锟斤拷
		
		int number = Integer.parseInt(time.substring(3, 5));
		for (int i=0 ;i<list.size();i++) {
			DataRow dataRow = list.get(i);
			int userid=dataRow.getInt("user_id");
			ShenKeAccount account=new ShenKeAccount();
			account.setUserid(userid);
			account.setUserName(dataRow.getString("name"));
			account.setPhone(dataRow.getString("phone"));
			
			//KPI
			int countSS=accountShenheService.getCSNowCountM1SS(userid,startDate, endDate,name, phone,number);	  //锟杰憋拷锟斤拷
			int oneSS=accountShenheService.getCSOneCountM1SS(userid,startDate, endDate,name,phone,number);   //M1锟斤拷锟斤拷
			totalCSmoneySS = accountShenheService.getCSJEM1SSBG(userid,startDate, endDate,name, phone,yuqts,number); //锟杰达拷锟秸斤拷锟�
			totalYCSmoneySS = accountShenheService.getYCSJEM1SSBG(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟秸斤拷锟�
			
			if(totalCSmoneySS != 0){
				kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
			}else{
				kpi = 0;
			}
			int nowCount=accountShenheService.getCSDateNowCountM1(userid,nowdate, dateno,name, phone); //锟杰憋拷锟斤拷
			totalYCSmoneyDT = accountShenheService.getCSDateNowCountDTJE(userid,nowdate, dateno,name, phone); //锟斤拷锟斤拷锟斤拷锟饺拷罨癸拷锟斤拷锟�
			
			totalYCSmoney2 = accountShenheService.getYCSJEYQM1(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟斤拷锟斤拷锟节斤拷锟�
			int countyq = accountShenheService.getYCSJEYQCountM1(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟斤拷锟斤拷锟节斤拷锟�
			
			
			account.setNowCount(nowCount);
			account.setCountSS(countSS);
			account.setCountyq(countyq);
			account.setOneSS(oneSS);
			account.setKpi(kpi);
			account.setTotalYCSmoneyDT(famt.format(totalYCSmoneyDT));
			account.setTotalCSmoneySS(famt.format(totalCSmoneySS));
			account.setTotalYCSmoneySS(famt.format(totalYCSmoneySS));
			account.setTotalYCSmoney2(famt.format(totalYCSmoney2));
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
	 * 锟矫碉拷锟斤拷锟斤拷统锟斤拷M2
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetCuishouAccountM2ZG() throws java.text.ParseException{
		//锟斤拷页锟斤拷询锟斤拷锟叫的猴拷台锟矫伙拷
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
		DBPage page = userService.selectUserListByCondtionCSM2ZG(curPage, 25, 0, name, phone); 
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
		//cd.add(Calendar.DATE, -i); // 锟斤拷一锟斤拷
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 锟斤拷一锟斤拷
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		DecimalFormat famt = new DecimalFormat("###,###");
		double totalYCSmoney2=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double totalYCSmoneyDT=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		
		double totalYCSmoneySS=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double totalCSmoneySS=0;
		double kpi =0;
		int number = Integer.parseInt(time.substring(3, 5));
		for (int i=0 ;i<list.size();i++) {
			DataRow dataRow = list.get(i);
			int userid=dataRow.getInt("user_id");
			ShenKeAccount account=new ShenKeAccount();
			account.setUserid(userid);
			account.setUserName(dataRow.getString("name"));
			account.setPhone(dataRow.getString("phone"));
			//KPI
			int countSS=accountShenheService.getCSNowCountM2SS(userid,startDate, endDate,name, phone,number);	  //锟杰憋拷锟斤拷
			
			int oneSS=accountShenheService.getCSOneCountM2SS(userid,startDate, endDate,name,phone,number);   //M1锟斤拷锟斤拷
			totalCSmoneySS = accountShenheService.getCSJEM2SSBG(userid,startDate, endDate,name, phone,yuqts,number); //锟杰达拷锟秸斤拷锟�
			totalYCSmoneySS = accountShenheService.getYCSJEM2SSBG(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟秸斤拷锟�
			
			if(totalCSmoneySS != 0){
				kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
			}else{
				kpi = 0;
			}
			int nowCount=accountShenheService.getCSDateNowCountM2(userid,nowdate, dateno,name, phone); //锟杰憋拷锟斤拷
			totalYCSmoneyDT = accountShenheService.getCSDateNowCountDTJEM2(userid,nowdate, dateno,name, phone); //锟斤拷锟斤拷锟斤拷锟饺拷罨癸拷锟斤拷锟�
			totalYCSmoney2 = accountShenheService.getYCSJEYQM2(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟斤拷锟斤拷锟节斤拷锟�
			int countyq = accountShenheService.getYCSJEYQCountM2(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟斤拷锟斤拷锟节斤拷锟�
			
			account.setCountSS(countSS);
			account.setOneSS(oneSS);
			account.setTotalCSmoneySS(famt.format(totalCSmoneySS));
			account.setTotalYCSmoneySS(famt.format(totalYCSmoneySS));
			account.setKpi(kpi);
			account.setNowCount(nowCount);
			account.setCountyq(countyq);
			account.setTotalYCSmoneyDT(famt.format(totalYCSmoneyDT));
			account.setTotalYCSmoney2(famt.format(totalYCSmoney2));
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
	 * 锟矫碉拷锟斤拷锟斤拷统锟斤拷M2
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetCuishouAccountM3ZG() throws java.text.ParseException{
		//锟斤拷页锟斤拷询锟斤拷锟叫的猴拷台锟矫伙拷
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
		DBPage page = userService.selectUserListByCondtionCSM3ZG(curPage, 25, 0, name, phone); 
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
		//cd.add(Calendar.DATE, -i); // 锟斤拷一锟斤拷
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 锟斤拷一锟斤拷
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		DecimalFormat famt = new DecimalFormat("###,###");
		double totalYCSmoney2=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double totalYCSmoneyDT=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		
		double totalYCSmoneySS=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double totalCSmoneySS=0;
		double kpi =0;
		int number = Integer.parseInt(time.substring(3, 5));
		for (int i=0 ;i<list.size();i++) {
			DataRow dataRow = list.get(i);
			int userid=dataRow.getInt("user_id");
			ShenKeAccount account=new ShenKeAccount();
			account.setUserid(userid);
			account.setUserName(dataRow.getString("name"));
			account.setPhone(dataRow.getString("phone"));
			//KPI
			int countSS=accountShenheService.getCSNowCountM3SS(userid,startDate, endDate,name, phone,number);	  //锟杰憋拷锟斤拷
			
			int oneSS=accountShenheService.getCSOneCountM3SS(userid,startDate, endDate,name,phone,number);   //M1锟斤拷锟斤拷
			totalCSmoneySS = accountShenheService.getCSJEM3SSBG(userid,startDate, endDate,name, phone,yuqts,number); //锟杰达拷锟秸斤拷锟�
			totalYCSmoneySS = accountShenheService.getYCSJEM3SSBG(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟秸斤拷锟�
			
			if(totalCSmoneySS != 0){
				kpi = totalYCSmoneySS * 100 / totalCSmoneySS ;
			}else{
				kpi = 0;
			}
			int nowCount=accountShenheService.getCSDateNowCountM3(userid,nowdate, dateno,name, phone); //锟杰憋拷锟斤拷
			totalYCSmoneyDT = accountShenheService.getCSDateNowCountDTJEM3(userid,nowdate, dateno,name, phone); //锟斤拷锟斤拷锟斤拷锟饺拷罨癸拷锟斤拷锟�
			totalYCSmoney2 = accountShenheService.getYCSJEYQM3(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟斤拷锟斤拷锟节斤拷锟�
			int countyq = accountShenheService.getYCSJEYQCountM3(userid,startDate, endDate, name, phone,yuqts,number);//锟斤拷锟窖达拷锟斤拷锟斤拷锟节斤拷锟�
			
			account.setCountSS(countSS);
			account.setOneSS(oneSS);
			account.setTotalCSmoneySS(famt.format(totalCSmoneySS));
			account.setTotalYCSmoneySS(famt.format(totalYCSmoneySS));
			account.setKpi(kpi);
			account.setNowCount(nowCount);
			account.setCountyq(countyq);
			account.setTotalYCSmoneyDT(famt.format(totalYCSmoneyDT));
			account.setTotalYCSmoney2(famt.format(totalYCSmoney2));
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
	 * 锟矫碉拷锟斤拷锟斤拷锟斤拷锟斤拷拇锟斤拷锟酵筹拷锟�
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetCSWBCuishouAccount() throws java.text.ParseException{
		//锟斤拷页锟斤拷询锟斤拷锟叫的猴拷台锟矫伙拷
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
		//cd.add(Calendar.DATE, -i); // 锟斤拷一锟斤拷
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 锟斤拷一锟斤拷
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		DecimalFormat famt = new DecimalFormat("###,###");
		DecimalFormat df = new DecimalFormat( "0.00"); //锟斤拷锟斤拷double锟斤拷锟斤拷小锟斤拷锟斤拷锟轿伙拷锟斤拷锟绞�
		double onemoney = 0; 
		double twomoney = 0; 
		double threemoney = 0; 
		double threeTGmoney = 0; 
		double totalCSmoney=0;  //锟杰达拷锟秸斤拷锟�
		double jrtotalCSmoney=0;  //锟杰达拷锟秸斤拷锟�
		double jrtotalCSmoney1=0;  //锟杰达拷锟秸斤拷锟�
		double totalYCSmoney=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney1=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney2=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney3=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double totalCSmoney1=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double totalYCSmoney2=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double totalYCSmoney3=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double ycsbl=0;  //锟斤拷锟窖达拷锟秸憋拷锟斤拷
		double ycsblyq=0;  //锟斤拷锟窖达拷锟秸憋拷锟斤拷
		double jrycsbl=0;  //锟斤拷锟窖达拷锟秸憋拷锟斤拷
		for (int i=0 ;i<list.size();i++) {
			DataRow dataRow = list.get(i);
			int userid=dataRow.getInt("user_id");
			ShenKeAccount account=new ShenKeAccount();
			account.setUserid(userid);
			account.setUserName(dataRow.getString("name"));
			account.setPhone(dataRow.getString("phone"));
			int jrnowOnesh=accountShenheService.getJRCSNowOneCountCSWB(userid, nowdate, dateno,name,phone);	  //锟斤拷锟斤拷M1锟斤拷锟斤拷
			int nowOnesh=accountShenheService.getCSNowOneCountCSWB(userid, nowdate, dateno,name,phone);	  //锟斤拷锟斤拷M1锟斤拷锟斤拷
			int nowTwosh=accountShenheService.getCSNowTwoCountCSWB(userid, nowdate, dateno,name,phone);	//锟斤拷锟斤拷M2锟斤拷锟斤拷
			int nowThreesh=accountShenheService.getCSNowThreeCountCSWB(userid, nowdate, dateno,name,phone);	//锟斤拷锟斤拷M3锟斤拷锟斤拷
			int nowFoursh=accountShenheService.getCSNowFourCountCSWB(userid, nowdate, dateno, name, phone); //锟斤拷锟斤拷M4锟斤拷锟斤拷
			int count=accountShenheService.getCSNowCountCSWB(userid,startDate, endDate,name, phone);	  //锟杰憋拷锟斤拷
			int jrcount=accountShenheService.getDYCSNowCountCSWB(userid,startDate, endDate,nowdate,name, phone);//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟杰憋拷锟斤拷
			int jrycscount=accountShenheService.getDYCSCountCSWB(userid,startDate, endDate,nowdate,name, phone);//锟斤拷锟斤拷锟窖达拷锟秸憋拷锟斤拷
			int one=accountShenheService.getCSOneCountCSWB(userid,startDate, endDate,name,phone);   //M1锟斤拷锟斤拷
			int two=accountShenheService.getCSTwoCountCSWB(userid,startDate, endDate,name,phone);	//M2锟斤拷锟斤拷
			int three=accountShenheService.getCSThreeCountCSWB(userid,startDate, endDate,name,phone); //M3锟斤拷锟斤拷
			int threeTG = accountShenheService.getCSFourCountCSWB(userid,startDate, endDate, name, phone);//M4锟斤拷锟斤拷
			int nowCount=accountShenheService.getCSDateNowCountCSWB(userid,nowdate, dateno,name, phone); //锟杰憋拷锟斤拷
			onemoney = accountShenheService.getOneMoneyCSWB(userid,startDate, endDate,name, phone);
			twomoney = accountShenheService.getTwoMoneyCSWB(userid,startDate, endDate,name, phone);
			threemoney = accountShenheService.getThreeMoneyCSWB(userid,startDate, endDate,name, phone);
			threeTGmoney = accountShenheService.getThreeTGMoneyCSWB(userid,startDate, endDate,name, phone);
			totalCSmoney = accountShenheService.getCSJECSWB(userid,startDate, endDate,name, phone,yuqts); //锟杰达拷锟秸斤拷锟�
			jrtotalCSmoney1 = accountShenheService.getDYCSJECSWB(userid,startDate, endDate,nowdate,name, phone,yuqts); //锟斤拷锟铰达拷锟斤拷锟杰斤拷锟�
			totalYCSmoney = accountShenheService.getYCSJECSWB(userid,startDate, endDate, name, phone,yuqts);//锟斤拷锟窖达拷锟秸斤拷锟�
			totalYCSmoney2 = accountShenheService.getYCSJEYQCSWB(userid,startDate, endDate, name, phone,yuqts);//锟斤拷锟窖达拷锟斤拷锟斤拷锟节斤拷锟�
			totalYCSmoney3 = accountShenheService.getYCSJEBFCSWB(userid,startDate, endDate, name, phone,yuqts);//锟斤拷锟窖达拷锟秸诧拷锟街斤拷锟�
			int countyq = accountShenheService.getYCSJEYQCountCSWB(userid,startDate, endDate, name, phone,yuqts);//锟斤拷锟窖达拷锟斤拷锟斤拷锟节斤拷锟�
			totalCSmoney1 = totalCSmoney - totalYCSmoney2 ; // 锟杰达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷锟斤拷冢锟�
			//totalYCSmoney  = totalYCSmoney + totalYCSmoney3;
			jrtotalCSmoney = jrtotalCSmoney1; // 锟杰达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷锟斤拷冢锟�
			jrtotalYCSmoney1 = accountShenheService.getDYYCSJECSWB(userid,startDate, endDate,nowdate, name, phone,yuqts);//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷锟�
			jrtotalYCSmoney2 = accountShenheService.getDYYCSJE1CSWB(userid,startDate, endDate, name, phone,yuqts);//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷冢锟�
			jrtotalYCSmoney3 = accountShenheService.getDYYCSJE2CSWB(userid,startDate, endDate,nowdate, name, phone,yuqts);//锟斤拷锟斤拷锟斤拷锟窖达拷锟秸斤拷睿拷锟斤拷锟斤拷锟斤拷冢锟�
			jrtotalYCSmoney = jrtotalYCSmoney1;
			if(totalCSmoney1 != 0){
				ycsbl = totalYCSmoney * 100 / totalCSmoney1 ;
			}else{
				ycsbl = 0;
			}
			//锟斤拷锟节的憋拷锟斤拷
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
			totalYCSmoney = accountShenheService.getYCSJECSWB(useridaa[j],startDate, endDate, name, phone,yuqts);//锟斤拷锟窖达拷锟秸斤拷锟�
			countaa[j]=totalYCSmoney; //锟杰憋拷锟斤拷
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
	 * 锟矫碉拷锟斤拷锟斤拷统锟斤拷
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetTXHKAccount() throws java.text.ParseException{
		//锟斤拷页锟斤拷询锟斤拷锟叫的猴拷台锟矫伙拷
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
		//cd.add(Calendar.DATE, -i); // 锟斤拷一锟斤拷
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 锟斤拷一锟斤拷
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
		DecimalFormat df = new DecimalFormat( "0.00"); //锟斤拷锟斤拷double锟斤拷锟斤拷小锟斤拷锟斤拷锟轿伙拷锟斤拷锟绞�
	
		
		double jrtotalCSmoney=0;  //锟杰达拷锟秸斤拷锟�
		
		double jrtotalYCSmoney=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney1=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrtotalYCSmoney2=0;  //锟斤拷锟窖达拷锟秸斤拷锟�
		double jrycsbl=0;  //锟斤拷锟窖达拷锟秸憋拷锟斤拷
		for (int i=0 ;i<list.size();i++) {
			DataRow dataRow = list.get(i);
			int userid=dataRow.getInt("user_id");
			ShenKeAccount account=new ShenKeAccount();
			account.setUserid(userid);
			account.setUserName(dataRow.getString("name"));
			account.setPhone(dataRow.getString("phone"));
			int dtxztxhk = accountShenheService.getJRCSZCount(userid, nowdate, dateno,name,phone);	
			int mqdtxzs = accountShenheService.getMQZBSCount(userid, nowdate, dateno,name,phone);	
			
			int jrnowOnesh=accountShenheService.getJRCSNowOneCount(userid, nowdate, dateno,name,phone);	  //锟斤拷锟斤拷锟窖伙拷锟斤拷锟斤拷锟�
			
			int dtzbs=accountShenheService.getDTZBSNowOneCount(userid, nowdate, dateno,name,phone);	  //锟斤拷锟斤拷锟窖伙拷锟斤拷锟斤拷锟�
			
			int dtddh=accountShenheService.getDTDDHNowOneCount(userid, nowdate, dateno,name,phone);	  //锟斤拷锟斤拷锟窖伙拷锟斤拷锟斤拷锟�
			
			int count=accountShenheService.getCSNowCountTX(userid,startDate, endDate,name, phone);	  //锟杰憋拷锟斤拷
			int jrcount=accountShenheService.getJRCSNowCount(userid,startDate, endDate,name, phone);	  //锟斤拷锟窖伙拷锟斤拷锟杰憋拷锟斤拷
			int jrycscount=accountShenheService.getJRCSCount(userid,startDate, endDate,name, phone);	  //锟斤拷锟斤拷锟杰伙拷锟斤拷锟斤拷锟�
			
			jrtotalCSmoney = accountShenheService.getJRCSJE(userid,startDate, endDate,name, phone); //锟斤拷锟斤拷锟斤拷锟斤拷锟杰斤拷锟�
			
			jrtotalYCSmoney1 = accountShenheService.getJRYCSJE(userid,startDate, endDate, name, phone);//锟斤拷锟窖达拷锟秸斤拷锟�
			jrtotalYCSmoney2 = accountShenheService.getJRYCSJE1(userid,startDate, endDate, name, phone);//锟斤拷锟窖达拷锟秸斤拷锟�
			
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
			jrtotalYCSmoney1 = accountShenheService.getJRYCSJE(useridaa[j],startDate, endDate, name, phone);//锟斤拷锟窖达拷锟秸斤拷锟�
			jrtotalYCSmoney2 = accountShenheService.getJRYCSJE1(useridaa[j],startDate, endDate, name, phone);//锟斤拷锟窖达拷锟秸斤拷锟�
			countaa[j]=jrtotalYCSmoney1+jrtotalYCSmoney2; //锟杰憋拷锟斤拷
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
	 * 锟矫碉拷锟斤拷锟斤拷统锟斤拷
	 * @return
	 * @throws java.text.ParseException 
	 */
	public ActionResult doGetTXHKTJAccount() throws java.text.ParseException{
		//锟斤拷页锟斤拷询锟斤拷锟叫的猴拷台锟矫伙拷
		
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
		//cd.add(Calendar.DATE, -i); // 锟斤拷一锟斤拷
		Date datenow = cd.getTime();
		String nowdate = sdf.format(datenow);
		cd.add(Calendar.DATE, 1); // 锟斤拷一锟斤拷
		Date date = cd.getTime();
		String dateno = sdf.format(date);
		
        
		DecimalFormat famt = new DecimalFormat("###,###");
		DecimalFormat df = new DecimalFormat( "0.00"); //锟斤拷锟斤拷double锟斤拷锟斤拷小锟斤拷锟斤拷锟轿伙拷锟斤拷锟绞�
		
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
	
	
	

	 /**
	    * Lin  20190627
	     * 逾期统计表
	    * @return
	    * @throws java.text.ParseException
	    */
		public ActionResult doGetyhyuqlrtongjAccount() throws java.text.ParseException{
		       
			logger.info("进入用户逾期率统计表");
//			 分页查询所有的后台用户
			int temp = getIntParameter("temp",1);
			int curPage  =getIntParameter("curPage",1);	
			int  tempVelue = getIntParameter("tempvl");
			String  startDate =getStrParameter("startDate");
			String  endDate =getStrParameter("endDate");
			int  recode =getIntParameter("recode",0);
			temp =temp+1;
			int  userId = -1;
			String  name ="";
			String phone ="";
			String cuishouz = "";
			
			
//	      g对时间进行处理
			SimpleDateFormat sdfYMD = new SimpleDateFormat("dd-MM-yyyy");
			SimpleDateFormat sdfYM = new SimpleDateFormat("MM-yyyy");
			
			String curtimeday  =sdfYMD.format(new Date()) ;
			String curtimeMon  =sdfYM.format(new Date()) ;
			
			List<Map> listdataList =new ArrayList<Map>();
			Map<String, Object> mapall = new HashMap<String, Object>();
			/** hhhhh 暂未使用***/
			//总计
			int tjyql_dhkzs = accountShenheService.getnewoldyqltjInteger(-1,false,-1,-1,-1,"","",-1 );  // 待还款总数
			int tjyql_yuqzs = accountShenheService.getnewoldyqltjInteger(-1,false,-1,0,-1,"","",-1 );  // 逾期总数
			int tjyql_yuqyhzs = accountShenheService.getnewoldyqltjInteger(-1,false,1 ,0,-1,"","",-1 ); //逾期已还总数
			int tjyql_yuqwhzs = accountShenheService.getnewoldyqltjInteger(-1,false,0 ,0,-1,"","",-1 ); //逾期未还总数
			
			int tjyql_yuqwhzs0 = accountShenheService.getnewoldyqltjInteger(-1,false,0,0,3,"","",-1 );  // 逾期未还总数0-3
			int tjyql_yuqwhzs1 = accountShenheService.getnewoldyqltjInteger(-1,false,0,3,7,"","",-1 );  // 逾期未还总数3-7
			int tjyql_yuqwhzs2 = accountShenheService.getnewoldyqltjInteger(-1,false,0,7,14,"","",-1 );  // 逾期未还总数7-14
			int tjyql_yuqwhzs3 = accountShenheService.getnewoldyqltjInteger(-1,false,0,14,30,"","",-1 );  // 逾期未还总数14-30
			int tjyql_yuqwhzs4 = accountShenheService.getnewoldyqltjInteger(-1,false,0,30,-1,"","",-1 );  // 逾期未还总数大于30
			//老用户
			int tjyql_dhkzsold = accountShenheService.getnewoldyqltjInteger(-1,true,-1,-1,-1,"","",-1 );  // 待还款总数
			int tjyql_yuqzsold = accountShenheService.getnewoldyqltjInteger(-1,true,-1,0,-1,"","",-1 );  // 逾期总数
			int tjyql_yuqyhzsold = accountShenheService.getnewoldyqltjInteger(-1,true,1 ,0,-1,"","",-1 ); //逾期已还总数
			int tjyql_yuqwhzsold = accountShenheService.getnewoldyqltjInteger(-1,true,0 ,0,-1,"","",-1 ); //逾期未还总数
			
			int tjyql_yuqwhzs0old = accountShenheService.getnewoldyqltjInteger(-1,true,0,0,3,"","",-1 );  // 逾期未还总数0-3
			int tjyql_yuqwhzs1old = accountShenheService.getnewoldyqltjInteger(-1,true,0,3,7,"","",-1 );  // 逾期未还总数3-7
			int tjyql_yuqwhzs2old = accountShenheService.getnewoldyqltjInteger(-1,true,0,7,14,"","",-1 );  // 逾期未还总数7-14
			int tjyql_yuqwhzs3old = accountShenheService.getnewoldyqltjInteger(-1,true,0,14,30,"","",-1 );  // 逾期未还总数14-30
			int tjyql_yuqwhzs4old = accountShenheService.getnewoldyqltjInteger(-1,true,0,30,-1,"","",-1 );  // 逾期未还总数大于30
			/****  hhh****/
			
			//总计 hkyq_time
			DBPage DbPage_dhkzs = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,1,false,-1,-1,-1,startDate,endDate,temp );  // 待还款
			DBPage DbPage_yuqzs = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,1,false,-1,0,-1,startDate,endDate,temp );  // 逾期总数
			DBPage DbPage_yuqyhzs = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,1,false,1,0,-1,startDate,endDate,temp ); //逾期已还总数
			DBPage DbPage_yuqwhzs = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,1,false,0 ,0,-1,startDate,endDate,temp ); //逾期未还总数
			
//			DBPage DBPage_yuqzs1 = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,1,false,-1,0,7,startDate,endDate,temp);  // 逾期总数0-7
//			DBPage DBPage_yuqzs2 = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,1,false,-1,7,14,startDate,endDate,temp);  // 逾期总数7-14
//			DBPage DBPage_yuqzs3 = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,1,false,-1,14,30,startDate,endDate,temp );  // 逾期总数14-30
//			DBPage DBPage_yuqzs4 = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,1,false,-1,30,-1,startDate,endDate,temp);  // 逾期总数大于30
			DBPage DBPage_yuqwhzs0 = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,1,false,0,0,3,startDate,endDate,temp);  // 逾期未还总数0-3
			DBPage DBPage_yuqwhzs1 = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,1,false,0,3,7,startDate,endDate,temp);  // 逾期未还总数3-7
			DBPage DBPage_yuqwhzs2 = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,1,false,0,7,14,startDate,endDate,temp);  // 逾期未还总数7-14
			DBPage DBPage_yuqwhzs3 = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,1,false,0,14,30,startDate,endDate,temp);  // 逾期未还总数14-30
			DBPage DBPage_yuqwhzs4 = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,1,false,0,30,-1,startDate,endDate,temp);  // 逾期未还总数大于30
			//老用户
			DBPage DBPage_yuqzsold = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,1,true,-1,0,-1,startDate,endDate,temp);  // 逾期总数
			DBPage DBPage_yuqyhzsold = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,1,true,1 ,0,-1,startDate,endDate,temp ); //逾期已还总数
			DBPage DBPage_yuqwhzsold = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,1,true,0 ,0,-1,startDate,endDate,temp ); //逾期未还总数		
//			DBPage DBPage_yuqzs1old = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,1,true,-1,0,7,startDate,endDate,temp);  // 逾期总数0-7
//			DBPage DBPage_yuqzs2old = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,1,true,-1,7,14,startDate,endDate,temp);  // 逾期总数7-14
//			DBPage DBPage_yuqzs3old = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,1,true,-1,14,30,startDate,endDate,temp);  // 逾期总数14-30
//			DBPage DBPage_yuqzs4old = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,1,true,-1,30,-1,startDate,endDate,temp);  // 逾期总数大于30
			DBPage DBPage_yuqwhzs0old = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,1,true,0,0,3,startDate,endDate,temp);  // 逾期未还总数0-3
			DBPage DBPage_yuqwhzs1old = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,1,true,0,3,7,startDate,endDate,temp);  // 逾期未还总数3-7
			DBPage DBPage_yuqwhzs2old = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,1,true,0,7,14,startDate,endDate,temp);  // 逾期未还总数7-14
			DBPage DBPage_yuqwhzs3old = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,1,true,0,14,30,startDate,endDate,temp);  // 逾期未还总数14-30
			DBPage DBPage_yuqwhzs4old = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,1,true,0,30,-1,startDate,endDate,temp);  // 逾期未还总数大于30
			
			List<DataRow> listtg_dhkzs = DbPage_dhkzs.getData();
			List<DataRow> listtg_yuqzs = DbPage_yuqzs.getData();
			List<DataRow> listtg_yuqyhzs = DbPage_yuqyhzs.getData();
			List<DataRow> listtg_yuqwhzs = DbPage_yuqwhzs.getData();
			List<DataRow> listtg_yuqwhzs0 = DBPage_yuqwhzs0.getData();
			List<DataRow> listtg_yuqwhzs1 = DBPage_yuqwhzs1.getData();
			List<DataRow> listtg_yuqwhzs2 = DBPage_yuqwhzs2.getData();
			List<DataRow> listtg_yuqwhzs3 = DBPage_yuqwhzs3.getData();
			List<DataRow> listtg_yuqwhzs4 = DBPage_yuqwhzs4.getData();
			
			List<DataRow> listtg_yuqzsold = DBPage_yuqzsold.getData();
			List<DataRow> listtg_yuqyhzsold = DBPage_yuqyhzsold.getData();
			List<DataRow> listtg_yuqwhzsold = DBPage_yuqwhzsold.getData();
			List<DataRow> listtg_yuqwhzs0old = DBPage_yuqwhzs0old.getData();
			List<DataRow> listtg_yuqwhzs1old = DBPage_yuqwhzs1old.getData();
			List<DataRow> listtg_yuqwhzs2old = DBPage_yuqwhzs2old.getData();
			List<DataRow> listtg_yuqwhzs3old = DBPage_yuqwhzs3old.getData();
			List<DataRow> listtg_yuqwhzs4old = DBPage_yuqwhzs4old.getData();
			
			
			//总计 hkfq_time
			DBPage DbPage2_dhkzs = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,2,false,-1,-1,-1,startDate,endDate,temp );  // 待还款
			DBPage DbPage2_yuqzs = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,2,false,-1,0,-1,startDate,endDate,temp );  // 逾期总数
			DBPage DbPage2_yuqyhzs = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,2,false,1 ,0,-1,startDate,endDate,temp ); //逾期已还总数
			DBPage DbPage2_yuqwhzs = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,2,false,0 ,0,-1,startDate,endDate,temp ); //逾期未还总数
			DBPage DBPage2_yuqwhzs0 = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,2,false,0,0,3,startDate,endDate,temp);  // 逾期未还总数0-3
			DBPage DBPage2_yuqwhzs1 = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,2,false,0,3,7,startDate,endDate,temp);  // 逾期未还总数3-7
			DBPage DBPage2_yuqwhzs2 = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,2,false,0,7,14,startDate,endDate,temp);  // 逾期未还总数7-14
			DBPage DBPage2_yuqwhzs3 = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,2,false,0,14,30,startDate,endDate,temp);  // 逾期未还总数14-30
			DBPage DBPage2_yuqwhzs4 = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,2,false,0,30,-1,startDate,endDate,temp);  // 逾期未还总数大于30
			//老用户
			DBPage DBPage2_yuqzsold = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,2,true,-1,0,-1,startDate,endDate,temp);  // 逾期总数
			DBPage DBPage2_yuqyhzsold = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,2,true,1,0,-1,startDate,endDate,temp ); //逾期已还总数
			DBPage DBPage2_yuqwhzsold = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,2,true,0,0,-1,startDate,endDate,temp ); //逾期未还总数
			DBPage DBPage2_yuqwhzs0old = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,2,true,0,0,3,startDate,endDate,temp);  // 逾期未还总数0-3
			DBPage DBPage2_yuqwhzs1old = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,2,true,0,3,7,startDate,endDate,temp);  // 逾期未还总数3-7
			DBPage DBPage2_yuqwhzs2old = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,2,true,0,7,14,startDate,endDate,temp);  // 逾期未还总数7-14
			DBPage DBPage2_yuqwhzs3old = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,2,true,0,14,30,startDate,endDate,temp);  // 逾期未还总数14-30
			DBPage DBPage2_yuqwhzs4old = accountShenheService.getnewoldyqltjdDbPage(curPage,30 ,2,true,0,30,-1,startDate,endDate,temp);  // 逾期未还总数大于30
			
			List<DataRow> listtg2_dhkzs = DbPage2_dhkzs.getData();
			List<DataRow> listtg2_yuqzs = DbPage2_yuqzs.getData();
			List<DataRow> listtg2_yuqyhzs = DbPage2_yuqyhzs.getData();
			List<DataRow> listtg2_yuqwhzs = DbPage2_yuqwhzs.getData();
			List<DataRow> listtg2_yuqwhzs0 = DBPage2_yuqwhzs0.getData();
			List<DataRow> listtg2_yuqwhzs1 = DBPage2_yuqwhzs1.getData();
			List<DataRow> listtg2_yuqwhzs2 = DBPage2_yuqwhzs2.getData();
			List<DataRow> listtg2_yuqwhzs3 = DBPage2_yuqwhzs3.getData();
			List<DataRow> listtg2_yuqwhzs4 = DBPage2_yuqwhzs4.getData();
			
			List<DataRow> listtg2_yuqzsold = DBPage2_yuqzsold.getData();
			List<DataRow> listtg2_yuqyhzsold = DBPage2_yuqyhzsold.getData();
			List<DataRow> listtg2_yuqwhzsold = DBPage2_yuqwhzsold.getData();
			List<DataRow> listtg2_yuqwhzs0old = DBPage2_yuqwhzs0old.getData();
			List<DataRow> listtg2_yuqwhzs1old = DBPage2_yuqwhzs1old.getData();
			List<DataRow> listtg2_yuqwhzs2old = DBPage2_yuqwhzs2old.getData();
			List<DataRow> listtg2_yuqwhzs3old = DBPage2_yuqwhzs3old.getData();
			List<DataRow> listtg2_yuqwhzs4old = DBPage2_yuqwhzs4old.getData();
			
			//总计
			tjyql_dhkzs = 0;  // 待还款总数
			tjyql_yuqzs =0;  // 逾期总数
			tjyql_yuqyhzs = 0; //逾期已还总数
			tjyql_yuqwhzs = 0; //逾期未还总数
			
			tjyql_yuqwhzs0 = 0;  // 逾期未还总数0-3
			tjyql_yuqwhzs1 = 0;  // 逾期未还总数3-7
			tjyql_yuqwhzs2 =0 ;  // 逾期未还总数7-14
			tjyql_yuqwhzs3 =0 ;  // 逾期未还总数14-30
			tjyql_yuqwhzs4 =0 ;  // 逾期未还总数大于30
			//老用户
			tjyql_dhkzsold = 0;  // 待还款总数
			tjyql_yuqzsold =0 ;  // 逾期总数
			tjyql_yuqyhzsold =0 ; //逾期已还总数
			tjyql_yuqwhzsold = 0; //逾期未还总数
			
			tjyql_yuqwhzs0old =0 ;  // 逾期未还总数0-3
			tjyql_yuqwhzs1old =0 ;  // 逾期未还总数3-7
			tjyql_yuqwhzs2old =0 ;  // 逾期未还总数7-14
			tjyql_yuqwhzs3old = 0;  // 逾期未还总数14-30
			tjyql_yuqwhzs4old = 0;  // 逾期未还总数大于30
			
			for(int i =0;i<listtg_dhkzs.size();i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				
				DataRow dataRow = listtg_dhkzs.get(i);
				String riqi = dataRow.getString("riqi");
				int dhkzs = dataRow.getInt("yh_total"); // 待还款总数hkyq_time

				//总计
				int _dhkzs = dhkzs + getListDataValue(riqi,listtg2_dhkzs);   //待还款总计
				int _yuqzs = getListDataValue(riqi,listtg_yuqzs) + getListDataValue(riqi,listtg2_yuqzs); // 逾期总数
				int _yuqyhzs =  getListDataValue(riqi,listtg_yuqyhzs)+ getListDataValue(riqi,listtg2_yuqyhzs); //逾期已还总数
				int _yuqwhzs =  getListDataValue(riqi,listtg_yuqwhzs)+ getListDataValue(riqi,listtg2_yuqwhzs); //逾期未还总数
				
				int _yuqwhzs0 = getListDataValue(riqi,listtg_yuqwhzs0) + getListDataValue(riqi,listtg2_yuqwhzs0);  // 逾期未还总数0-3
				int _yuqwhzs1 = getListDataValue(riqi,listtg_yuqwhzs1) + getListDataValue(riqi,listtg2_yuqwhzs1);  // 逾期未还总数3-7
				int _yuqwhzs2 = getListDataValue(riqi,listtg_yuqwhzs2) + getListDataValue(riqi,listtg2_yuqwhzs2);  // 逾期未还总数7-14
				int _yuqwhzs3 = getListDataValue(riqi,listtg_yuqwhzs3) + getListDataValue(riqi,listtg2_yuqwhzs3);  // 逾期未还总数14-30
				int _yuqwhzs4 = getListDataValue(riqi,listtg_yuqwhzs4) + getListDataValue(riqi,listtg2_yuqwhzs4);  // 逾期未还总数大于30
				//老用户
				int _yuqzsold = getListDataValue(riqi,listtg_yuqzsold) + getListDataValue(riqi,listtg2_yuqzsold)  ;  // 逾期总数
				int _yuqyhzsold = getListDataValue(riqi,listtg_yuqyhzsold) + getListDataValue(riqi,listtg2_yuqyhzsold)  ; //逾期已还总数
				int _yuqwhzsold = getListDataValue(riqi,listtg_yuqwhzsold) + getListDataValue(riqi,listtg2_yuqwhzsold)  ; //逾期未还总数
				int _yuqwhzs0old = getListDataValue(riqi,listtg_yuqwhzs0old) + getListDataValue(riqi,listtg2_yuqwhzs0old) ;  // 逾期未还总数0-3
				int _yuqwhzs1old = getListDataValue(riqi,listtg_yuqwhzs1old) + getListDataValue(riqi,listtg2_yuqwhzs1old) ;  // 逾期未还总数3-7
				int _yuqwhzs2old = getListDataValue(riqi,listtg_yuqwhzs2old) + getListDataValue(riqi,listtg2_yuqwhzs2old) ;  // 逾期未还总数7-14
				int _yuqwhzs3old = getListDataValue(riqi,listtg_yuqwhzs3old) + getListDataValue(riqi,listtg2_yuqwhzs3old) ;  // 逾期未还总数14-30
				int _yuqwhzs4old = getListDataValue(riqi,listtg_yuqwhzs4old) + getListDataValue(riqi,listtg2_yuqwhzs4old)  ;  // 逾期未还总数大于30
				
				//总计
				tjyql_dhkzs += _dhkzs;  // 待还款总数
				tjyql_yuqzs += _yuqzs;  // 逾期总数
				tjyql_yuqyhzs += _yuqyhzs; //逾期已还总数
				tjyql_yuqwhzs += _yuqwhzs; //逾期未还总数
				
				tjyql_yuqwhzs0 +=_yuqwhzs0 ;  // 逾期未还总数0-3
				tjyql_yuqwhzs1 += _yuqwhzs1;  // 逾期未还总数3-7
				tjyql_yuqwhzs2 += _yuqwhzs2;  // 逾期未还总数7-14
				tjyql_yuqwhzs3 += _yuqwhzs3;  // 逾期未还总数14-30
				tjyql_yuqwhzs4 += _yuqwhzs4;  // 逾期未还总数大于30
				//老用户
				tjyql_yuqzsold += _yuqyhzsold;  // 逾期总数
				tjyql_yuqyhzsold += _yuqwhzsold; //逾期已还总数
				tjyql_yuqwhzsold += _yuqwhzs0old; //逾期未还总数
				
				tjyql_yuqwhzs0old += _yuqwhzs0old;  // 逾期未还总数0-3
				tjyql_yuqwhzs1old += _yuqwhzs1old;  // 逾期未还总数3-7
				tjyql_yuqwhzs2old += _yuqwhzs2old;  // 逾期未还总数7-14
				tjyql_yuqwhzs3old += _yuqwhzs3old;  // 逾期未还总数14-30
				tjyql_yuqwhzs4old +=_yuqwhzs4old ;  // 逾期未还总数大于30

				map.put("riqi", riqi);
				map.put("tjdhkzs", _dhkzs);
				map.put("tjyuqzs", _yuqzs);
				map.put("tjyuqyhzs", _yuqyhzs);
				map.put("tjyuqwhzs", _yuqwhzs);
				map.put("tjyuql", baifenbi(_yuqzs,_dhkzs));
				map.put("tjyuqyhl", baifenbi(_yuqyhzs,_yuqzs));
				map.put("tjyuqwhl", baifenbi(_yuqwhzs,_dhkzs));
				
				map.put("tjnewyuql", baifenbi(_yuqzs-_yuqzsold,_dhkzs));
				map.put("tjnewyuqyhl", baifenbi(_yuqyhzs-_yuqyhzsold,_yuqzs));
				map.put("tjnewyuqwhl", baifenbi(_yuqwhzs-_yuqwhzsold,_dhkzs));
				map.put("tjnewyuqwhl0", baifenbi(_yuqwhzs0-_yuqwhzs0old ,_dhkzs ));
				map.put("tjnewyuqwhl1", baifenbi(_yuqwhzs1-_yuqwhzs1old ,_dhkzs ));
				map.put("tjnewyuqwhl2", baifenbi(_yuqwhzs2-_yuqwhzs2old , _dhkzs));
				map.put("tjnewyuqwhl3", baifenbi(_yuqwhzs3-_yuqwhzs3old,_dhkzs ));
				map.put("tjnewyuqwhl4", baifenbi(_yuqwhzs4-_yuqwhzs4old,_dhkzs ));
				
				map.put("tjoldyuql", baifenbi(_yuqzsold,_dhkzs));
				map.put("tjoldyuqyhl", baifenbi(_yuqyhzsold,_yuqzs));
				map.put("tjoldyuqwhl", baifenbi(_yuqwhzsold,_dhkzs));
				map.put("tjoldyuqwhl0", baifenbi(_yuqwhzs0old ,_dhkzs ));
				map.put("tjoldyuqwhl1", baifenbi(_yuqwhzs1old ,_dhkzs ));
				map.put("tjoldyuqwhl2", baifenbi(_yuqwhzs2old , _dhkzs));
				map.put("tjoldyuqwhl3", baifenbi(_yuqwhzs3old,_dhkzs ));
				map.put("tjoldyuqwhl4", baifenbi(_yuqwhzs4old,_dhkzs ));
				
				listdataList.add(map);

			}
			
			mapall.put("riqi", "总计");
			mapall.put("tjdhkzs", tjyql_dhkzs);
			mapall.put("tjyuqzs", tjyql_yuqzs);
			mapall.put("tjyuqyhzs", tjyql_yuqyhzs);
			mapall.put("tjyuqwhzs", tjyql_yuqwhzs);
			mapall.put("tjyuql", baifenbi(tjyql_yuqzs,tjyql_dhkzs));
			mapall.put("tjyuqyhl", baifenbi(tjyql_yuqyhzs,tjyql_yuqzs));
			mapall.put("tjyuqwhl", baifenbi(tjyql_yuqwhzs,tjyql_dhkzs));

			mapall.put("tjnewyuql", baifenbi(tjyql_yuqzs-tjyql_yuqzsold,tjyql_dhkzs));
			mapall.put("tjnewyuqyhl", baifenbi(tjyql_yuqyhzs-tjyql_yuqyhzsold,tjyql_yuqzs));
			mapall.put("tjnewyuqwhl", baifenbi(tjyql_yuqwhzs-tjyql_yuqwhzsold,tjyql_dhkzs));
			mapall.put("tjnewyuqwhl0", baifenbi(tjyql_yuqwhzs0-tjyql_yuqwhzs0old ,tjyql_dhkzs ));
			mapall.put("tjnewyuqwhl1", baifenbi(tjyql_yuqwhzs1-tjyql_yuqwhzs1old ,tjyql_dhkzs ));
			mapall.put("tjnewyuqwhl2", baifenbi(tjyql_yuqwhzs2-tjyql_yuqwhzs2old , tjyql_dhkzs));
			mapall.put("tjnewyuqwhl3", baifenbi(tjyql_yuqwhzs3-tjyql_yuqwhzs3old,tjyql_dhkzs ));
			mapall.put("tjnewyuqwhl4", baifenbi(tjyql_yuqwhzs4-tjyql_yuqwhzs4old,tjyql_dhkzs ));
			
			mapall.put("tjoldyuql", baifenbi(tjyql_yuqzsold,tjyql_dhkzs));
			mapall.put("tjoldyuqyhl", baifenbi(tjyql_yuqyhzsold,tjyql_yuqzs));
			mapall.put("tjoldyuqwhl", baifenbi(tjyql_yuqwhzsold,tjyql_dhkzs));
			mapall.put("tjoldyuqwhl0", baifenbi(tjyql_yuqwhzs0old ,tjyql_dhkzs ));
			mapall.put("tjoldyuqwhl1", baifenbi(tjyql_yuqwhzs1old ,tjyql_dhkzs ));
			mapall.put("tjoldyuqwhl2", baifenbi(tjyql_yuqwhzs2old , tjyql_dhkzs));
			mapall.put("tjoldyuqwhl3", baifenbi(tjyql_yuqwhzs3old,tjyql_dhkzs ));
			mapall.put("tjoldyuqwhl4", baifenbi(tjyql_yuqwhzs4old,tjyql_dhkzs ));
			
			listdataList.add(mapall);
			
//			封装好响应给前端的数据
			DBPage setpagerow = accountShenheService.getnewoldyqltjdDbPage(curPage,31,2,false,-1,-1,-1,"","",temp );  // 逾期总数
			setpagerow.setData(listdataList);
			DataRow row = new DataRow();
			row.set("list", setpagerow);
			row.set("temp",temp-1);
			row.set("tempvalu",tempVelue);
			JSONObject object = JSONObject.fromBean(row);	
			this.getWriter().write(object.toString());		
			return null;
		}
		
		/**
	     * Lin  20190625
	             * 催收单实时统计页面
	     * @return
	     * @throws java.text.ParseException
	     */
		public ActionResult doGetcuishoudantongjAccount() throws java.text.ParseException{
		       
			logger.info("进入催收单统计表");
//			 分页查询所有的后台用户
			int temp = getIntParameter("temp",0);
			int curPage  =getIntParameter("curPage",1);	
			String  tempVelue = getStrParameter("tempvl","");
			String  startDate =getStrParameter("startDate");
			String  endDate =getStrParameter("endDate");
			int  recode =getIntParameter("recode",0);
		    logger.info("recode :"+recode);
		    
	       int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());
		   DataRow maprole =accountShenheService.getSdcmsUser(cmsuserid);
		   int cms_roleid=maprole.getInt("roleid");
//			    //xiong-20180810-限制催收人员搜索框
//			    if(1!=maproleid){
//			    	tempVelue="";
//			    	startDate="";
//			    	endDate="";
//			    }
			int  userId = -1;
			String  name ="";
			String phone ="";
			String cuishouz = "";
			
//	       g对时间进行处理
			SimpleDateFormat sdfyMd = new SimpleDateFormat("yyyy-MM-dd");
			String endtimeday  =sdfyMd.format(new Date());

			if (!StringHelper.isEmpty(tempVelue)) {
				userId = Integer.valueOf(tempVelue).intValue();
			}
			if (StringHelper.isEmpty(endDate)) {
				endDate= endtimeday;
			}

	        Calendar rightNow = Calendar.getInstance();
	       //提醒时间设置
			Date curtimeyMd  = new Date();
			if (!StringHelper.isEmpty(endDate)) {
				curtimeyMd = sdfyMd.parse(endDate);
			}
			String curtime  =sdfyMd.format(curtimeyMd);        
	        // 减3天
	        rightNow.setTime(curtimeyMd);
	        rightNow.add(rightNow.DATE,-3);
	        Date dt2=rightNow.getTime();
	        String curtimeyMd3 = sdfyMd.format(dt2);  
	        // 减7天
	        rightNow.add(rightNow.DATE,-4);
	        Date dt3=rightNow.getTime();
	        String curtimeyMd7 = sdfyMd.format(dt3);   
	        
			DecimalFormat dfamt = new DecimalFormat("###,###");
			DecimalFormat df = new DecimalFormat( "0.00"); //设置double类型小数点后位数格式
			
			//催收人员统计
			DBPage page = accountShenheService.getcuishoutjUserDbPage(curPage,100,userId, name, phone, cuishouz,startDate,endDate); 
			List<DataRow> listuseridall=page.getData();
			
			List<Map> listdataList =new ArrayList<Map>();
			Map<String, Object> mapall = new HashMap<String, Object>();
			
			//total all person
			//double tj_ycszjineall = accountShenheService.getCSTJycsjinewhw("",0) +accountShenheService.getCSTJychzje(0 ,startDate,endDate,2,"");
			double tj_ycszjineall =  accountShenheService.getCSFendanjine(0 ,startDate,endDate,2,"");//，入催收总金额
			double tj_ychzjineallMon =accountShenheService.getCSTJychzje(0 ,startDate,endDate,2,"");//， 已催回总金额
			tj_ychzjineallMon +=  getBeforeMonthlastDay_chje(0,startDate,endDate,1,cuishouz,1);//上个月最后一天催回金额
			double tj_ychzjineallDay =accountShenheService.getCSTJychzje(0 ,startDate,endDate,1,"");//， 当日已催回总金额
			String chbl = baifenbi(tj_ychzjineallMon,tj_ycszjineall);   //d比例
			int tj_ycszdsall =  accountShenheService.getCSTJcsds(0 ,startDate,endDate,2,"");//，入催收总单数
			int tj_ychzdsallMon = accountShenheService.getCSTJychds1(0 ,startDate,endDate,2,"");  //，已催回总单数 当月
			tj_ychzdsallMon+=  getBeforeMonthlastDay_chje(0,startDate,endDate,1,cuishouz,2);
			int tj_yqfqdsallMon = accountShenheService.getcuishouhkyqAccount(0 ,startDate,endDate,2,"");  //a 已逾期再分期用户
			int tj_wchzdsall =  accountShenheService.getCSTJwchds(0 ,startDate,endDate,-1,"");// 未催回总单数
			int tj_ychzdsallDay = accountShenheService.getCSTJychds1(0 ,startDate,endDate,1,"");  //，已催回总单数 当日
			int tj_txscurtime =  accountShenheService.getCSTJtxzs(0 ,curtime,curtime,1,"");  //当日提醒数
			int tj_txs3 = accountShenheService.getCSTJtxzs(0 ,curtimeyMd3,curtime,1,"");  //当月三天提醒数
			//int tj_txs7 = accountShenheService.getCSTJtxzs(0 ,curtimeyMd7,curtime,1);  //当月7天提醒数
			//int tj_cwtxs = accountShenheService.getCSTJtxzs(0 ,"","",-1);  //从未提醒数
			int tj_curfendans= accountShenheService.getcuitimefendanAccount(0 ,startDate,endDate,""); 
			int tj_wtxs3 = 0;// tj_wchzdsall - tj_txs3;   //当月三天提醒数
			int tj_wtxs7 = 0; //  tj_wchzdsall - tj_txs7;    //当月7天提醒数
			int tj_wtxs =  0; //tj_wchzdsall - tj_txs7;   //从未提醒数


			if (userId <=0) {
				if(6== recode) {//M0
					DBPage pageM0 = accountShenheService.getcuishoutjUserDbPage(curPage,100,userId, name, phone, "M0",startDate,endDate); 
					List<DataRow> listM0=pageM0.getData();
					listdataList= getCuishoudansetList(listdataList, listM0, startDate,endDate, curtimeyMd3,curtimeyMd7,curtime, "M0",recode );
				}else if (1== recode) {//M1
					DBPage pageM1 = accountShenheService.getcuishoutjUserDbPage(curPage,100,userId, name, phone, "M1",startDate,endDate); 
					List<DataRow> listM1=pageM1.getData();
					listdataList= getCuishoudansetList(listdataList, listM1, startDate,endDate, curtimeyMd3,curtimeyMd7,curtime, "M1",recode );
				}else if (2== recode) { //m2
					DBPage pageM2 = accountShenheService.getcuishoutjUserDbPage(curPage,100,userId, name, phone,"M2",startDate,endDate); 
					List<DataRow> listM2=pageM2.getData();
					listdataList= getCuishoudansetList(listdataList, listM2, startDate,endDate, curtimeyMd3,curtimeyMd7,curtime,"M2",recode);
				}else if (3== recode) {//m3
					DBPage pageM3 = accountShenheService.getcuishoutjUserDbPage(curPage,100,userId, name, phone, "M3",startDate,endDate); 
					List<DataRow> listM3 = pageM3.getData();
					listdataList= getCuishoudansetList(listdataList, listM3, startDate,endDate, curtimeyMd3,curtimeyMd7,curtime, "M3",recode);
				}else if (7== recode) {//M3_A
					DBPage pageM3_A = accountShenheService.getcuishoutjUserDbPage(curPage,100,userId, name, phone, "M3_A",startDate,endDate); 
					List<DataRow> listM3_A = pageM3_A.getData();
					listdataList= getCuishoudansetList(listdataList, listM3_A, startDate,endDate, curtimeyMd3,curtimeyMd7,curtime, "M3_A",recode);
				}else { //    M0\m1\m2\m3
					DBPage pageM0 = accountShenheService.getcuishoutjUserDbPage(curPage,100,userId, name, phone, "M0",startDate,endDate); 
					List<DataRow> listM0=pageM0.getData();
					listdataList= getCuishoudansetList(listdataList, listM0, startDate,endDate, curtimeyMd3,curtimeyMd7,curtime, "M0",recode );
					
					DBPage pageM1 = accountShenheService.getcuishoutjUserDbPage(curPage,100,userId, name, phone, "M1",startDate,endDate); 
					List<DataRow> listM1=pageM1.getData();
					listdataList= getCuishoudansetList(listdataList, listM1, startDate,endDate, curtimeyMd3,curtimeyMd7,curtime, "M1",recode );
					
					DBPage pageM2 = accountShenheService.getcuishoutjUserDbPage(curPage,100,userId, name, phone,"M2",startDate,endDate); 
					List<DataRow> listM2=pageM2.getData();
					listdataList= getCuishoudansetList(listdataList, listM2, startDate,endDate, curtimeyMd3,curtimeyMd7,curtime,"M2",recode);
					
					DBPage pageM3 = accountShenheService.getcuishoutjUserDbPage(curPage,100,userId, name, phone, "M3",startDate,endDate); 
					List<DataRow> listM3 = pageM3.getData();
					listdataList= getCuishoudansetList(listdataList, listM3, startDate,endDate, curtimeyMd3,curtimeyMd7,curtime, "M3",recode);
					
					if(1==cms_roleid) {
						DBPage pageM3_A = accountShenheService.getcuishoutjUserDbPage(curPage,100,userId, name, phone, "M3_A",startDate,endDate); 
						List<DataRow> listM3_A = pageM3_A.getData();
						listdataList= getCuishoudansetList(listdataList, listM3_A, startDate,endDate, curtimeyMd3,curtimeyMd7,curtime, "M3_A",recode);
					}
					
				}
				
			}else {
				listdataList= getCuishoudansetList(listdataList, listuseridall, startDate,endDate, curtimeyMd3,curtimeyMd7,curtime,"",recode);
			}
			
			mapall.put("paixu", "");
			mapall.put("userid","Total" );
			mapall.put("name", "All");
			mapall.put("state", "");
			mapall.put("tj_ycszjine",  dfamt.format(tj_ycszjineall));
			mapall.put("tj_ychzjineMon",  dfamt.format(tj_ychzjineallMon));
			mapall.put("tj_ychzjineDay",  dfamt.format(tj_ychzjineallDay));
			mapall.put("chbuserl", baifenbi(tj_ychzjineallMon,tj_ycszjineall));
			
			mapall.put("tj_ycszdsuser",  tj_ycszdsall);
			mapall.put("tj_ychzdsuserMon",  tj_ychzdsallMon);
			mapall.put("tj_yqfqdsallMon", tj_yqfqdsallMon);
			mapall.put("tj_wchzdsuser",  tj_wchzdsall);
			
			mapall.put("tj_ychzduserlDay", tj_ychzdsallDay);
			mapall.put("tj_txscurtime",tj_txscurtime);  //???? tj_txscurtime
			mapall.put("tj_wtxs3",tj_wtxs3);  //d?????
			mapall.put("tj_wtxs7",tj_wtxs7);  //d?????
			mapall.put("tj_wtxs",tj_wtxs);  //d?????
			mapall.put("fendans",tj_curfendans);
			
			listdataList.add(mapall);
			
			//logger.info("tj_txscurtime"+tj_txscurtime);
			
//			封装好响应给前端的数据
			//DBPage pagerow = new DBPage();

			page.setData(listdataList);
			DataRow row = new DataRow();
			row.set("list", page);
			row.set("temp",temp);
			row.set("tempvalu",tempVelue);
			JSONObject object = JSONObject.fromBean(row);	
			this.getWriter().write(object.toString());		
			return null;

		}
		/**
		 * 催收人员单统计 方法
		 * 
		 */
		public List<Map>  getCuishoudansetList(List<Map> returndataList,List<DataRow> listuserid,String startDate,String endDate, String curtimeyMd3,String curtimeyMd7,String curtime,String cuishouz,int codepage){
			
			DecimalFormat dfamt = new DecimalFormat("###,###");
			
			double total_ycszjine =0;
			double total_ychzjineMon =0;
			double total_ychzjineDay =0;
			String total_cuishoubl="";
			
			int total_ycszdsuser =0;
			int total_ychzdsuserMon =0;
			int total_yqfqdsallMon =0;
			int total_wchzdsuser =0;
			int total_ychzduserlDay =0;
			int total_tjtxcuitime =0;
			int total_wtxsuser3 =0;
			int total_wtxsuser7 =0;
			int total_wtxsuser =0;
			int total_fendans =0;
			
			
			if(4==codepage) {
				int paixu = 0;
				for(int i =0;i<listuserid.size();i++) {
					DataRow dataRow = listuserid.get(i);
					Map<String, Object> map = new HashMap<String, Object>();
					int user_id = dataRow.getInt("user_id");
					String username = dataRow.getString("name");
					int  state = dataRow.getInt("state");
					// everyone
		            //double tj_ycszjine = accountShenheService.getCSTJcszjehk(user_id ,startDate,endDate,2);//当月应催收总金额
					//double tj_ycszjine = accountShenheService.getCSTJycsjinewhw(cuishouz,user_id) +accountShenheService.getCSTJychjine(user_id ,startDate,endDate,2) ;	
					//当月应催收总金额
					//double tj_ycszjine = accountShenheService.getCSTJycsjinewhw(cuishouz,user_id) +accountShenheService.getCSTJychzje(user_id,startDate,endDate,2,cuishouz);
					double tj_ycszjine = accountShenheService.getCSFendanjine(user_id ,startDate,endDate,2,cuishouz) ;	        
		            double tj_ychzjineMon = accountShenheService.getCSTJychzje(user_id,startDate,endDate,2,cuishouz);//， 当月已催回总金额
		            tj_ychzjineMon +=  getBeforeMonthlastDay_chje(user_id,startDate,endDate,1,cuishouz,1);//上个月最后一天催回金额
		            if(tj_ycszjine > tj_ychzjineMon && state==1) {
		            	paixu++;
		            	double tj_ychzjineDay = accountShenheService.getCSTJychzje(user_id,startDate,endDate,1,cuishouz);//， 当日已催回总金额
		            	String chbuserl = baifenbi(tj_ychzjineMon,tj_ycszjine);   //d比例
						//
						int tj_ycszdsuser = accountShenheService.getCSTJcsds(user_id ,startDate,endDate,2,cuishouz);//，  当月应催收总单数
						int tj_ychzdsuserMon = accountShenheService.getCSTJychds1(user_id ,startDate,endDate,2,cuishouz);  //， 当月已催回总单数
						tj_ychzdsuserMon+=  getBeforeMonthlastDay_chje(user_id,startDate,endDate,1,cuishouz,2);
						int tj_yqfqdsallMon = accountShenheService.getcuishouhkyqAccount(user_id,startDate,endDate,2,cuishouz);  //a 已逾期再分期用户
						int tj_wchzdsuser = accountShenheService.getCSTJwchds(user_id,startDate,endDate,2,cuishouz);//，  当前未催回总单数
						int tj_ychzduserlDay = accountShenheService.getCSTJychds1(user_id,startDate,endDate,1,cuishouz);  //，已经催回总单数 当日
						
						int tj_txsusercur = accountShenheService.getCSTJtxzs(user_id,curtime,curtime,1,cuishouz);  //
						int tj_txsuser3 = accountShenheService.getCSTJtxzs(user_id,curtimeyMd3,curtime,1,cuishouz);  //当月3天提醒数
						int tj_txsuser7 = accountShenheService.getCSTJtxzs(user_id,curtimeyMd7,curtime,1,cuishouz);  //当月7天提醒数
						int tj_cwtxsuser = accountShenheService.getCSTJtxzs(user_id ,"","",-1,cuishouz);  //提醒总数
						int tj_wtxsuser3 = tj_wchzdsuser - tj_txsuser3;   // 3天未提醒数
						int tj_wtxsuser7 = tj_wchzdsuser - tj_txsuser7;   // 7天未提醒数
						int tj_wtxsuser = tj_wchzdsuser- tj_cwtxsuser;
						int curfendans= accountShenheService.getcuitimefendanAccount(user_id,startDate,endDate,cuishouz);  //d当天分单数
						
						map.put("paixu", paixu);
						if (!StringHelper.isEmpty(cuishouz)) {
							map.put("userid", cuishouz+"--"+user_id);
						}else {
							map.put("userid", user_id);
						}
						//logger.info("tj_txscurtime"+tj_txsusercur);
						map.put("name", username);
						if (1==state) {
							map.put("state", "Yes");
						}else {
							map.put("state", "NO");
						}
						
						map.put("tj_ycszjine",  dfamt.format(tj_ycszjine));
						map.put("tj_ychzjineMon",  dfamt.format(tj_ychzjineMon));
						map.put("tj_ychzjineDay",  dfamt.format(tj_ychzjineDay));
						map.put("chbuserl", chbuserl);
						
						map.put("tj_ycszdsuser",  tj_ycszdsuser);
						map.put("tj_ychzdsuserMon",  tj_ychzdsuserMon);
						map.put("tj_yqfqdsallMon", tj_yqfqdsallMon);
						map.put("tj_wchzdsuser",  tj_wchzdsuser);
					//	map.put("tj_ychzdsuserMon",  tj_ychzdsuserMon);
						map.put("tj_ychzduserlDay",  tj_ychzduserlDay);
						map.put("tj_txscurtime", tj_txsusercur);
						map.put("tj_wtxs3", tj_wtxsuser3);
						map.put("tj_wtxs7", tj_wtxsuser7);
						map.put("tj_wtxs",tj_wtxsuser);  //d未处理单数
						map.put("fendans",curfendans);
						
						returndataList.add(map);
						//分组时，统计
						total_ycszjine += tj_ycszjine;
						total_ychzjineMon += tj_ychzjineMon;
						total_ychzjineDay += tj_ychzjineDay;
					
						total_ycszdsuser += tj_ycszdsuser;
						total_ychzdsuserMon += tj_ychzdsuserMon;
						total_yqfqdsallMon += tj_yqfqdsallMon;
						total_wchzdsuser += tj_wchzdsuser ;
						total_ychzduserlDay += tj_ychzduserlDay ;
						
						total_tjtxcuitime +=  tj_txsusercur;
						total_wtxsuser3 += tj_wtxsuser3 ;
						total_wtxsuser7 += tj_wtxsuser7 ;
						total_wtxsuser += tj_wtxsuser ;
						total_fendans +=curfendans;
		            }
					
				}
			}else if(5==codepage){
				int paixu = 0;
				for(int i =0;i<listuserid.size();i++) {
					DataRow dataRow = listuserid.get(i);
					Map<String, Object> map = new HashMap<String, Object>();
					int user_id = dataRow.getInt("user_id");
					String username = dataRow.getString("name");
					int  state = dataRow.getInt("state");
					
					// everyone
		            //double tj_ycszjine = accountShenheService.getCSTJcszjehk(user_id ,startDate,endDate,2);//当月应催收总金额
					//double tj_ycszjine = accountShenheService.getCSTJycsjinewhw(cuishouz,user_id) +accountShenheService.getCSTJychjine(user_id ,startDate,endDate,2) ;	
					//当月应催收总金额
					//double tj_ycszjine = accountShenheService.getCSTJycsjinewhw(cuishouz,user_id) +accountShenheService.getCSTJychzje(user_id,startDate,endDate,2,cuishouz);
					double tj_ycszjine = accountShenheService.getCSFendanjine(user_id ,startDate,endDate,2,cuishouz) ;	        
		            double tj_ychzjineMon = accountShenheService.getCSTJychzje(user_id,startDate,endDate,2,cuishouz);//， 当月已催回总金额
		            tj_ychzjineMon +=  getBeforeMonthlastDay_chje(user_id,startDate,endDate,1,cuishouz,1);//上个月最后一天催回金额
		            if(tj_ycszjine <= tj_ychzjineMon || state==0) {
		            	paixu++;
		            	double tj_ychzjineDay = accountShenheService.getCSTJychzje(user_id,startDate,endDate,1,cuishouz);//， 当日已催回总金额
		            	String chbuserl = baifenbi(tj_ychzjineMon,tj_ycszjine);   //d比例
						//
						int tj_ycszdsuser = accountShenheService.getCSTJcsds(user_id ,startDate,endDate,2,cuishouz);//，  当月应催收总单数
						int tj_ychzdsuserMon = accountShenheService.getCSTJychds1(user_id ,startDate,endDate,2,cuishouz);  //， 当月已催回总单数 
						tj_ychzdsuserMon+=  getBeforeMonthlastDay_chje(user_id,startDate,endDate,1,cuishouz,2);
						int tj_yqfqdsallMon = accountShenheService.getcuishouhkyqAccount(user_id,startDate,endDate,2,cuishouz);  //a 已逾期再分期用户
						int tj_wchzdsuser = accountShenheService.getCSTJwchds(user_id,startDate,endDate,2,cuishouz);//，  当前未催回总单数
						int tj_ychzduserlDay = accountShenheService.getCSTJychds1(user_id,startDate,endDate,1,cuishouz);  //，已经催回总单数 当日
						
						int tj_txsusercur = accountShenheService.getCSTJtxzs(user_id,curtime,curtime,1,cuishouz);  //
						int tj_txsuser3 = accountShenheService.getCSTJtxzs(user_id,curtimeyMd3,curtime,1,cuishouz);  //当月3天提醒数
						int tj_txsuser7 = accountShenheService.getCSTJtxzs(user_id,curtimeyMd7,curtime,1,cuishouz);  //当月7天提醒数
						int tj_cwtxsuser = accountShenheService.getCSTJtxzs(user_id ,"","",-1,cuishouz);  //提醒总数
						int tj_wtxsuser3 = tj_wchzdsuser - tj_txsuser3;   // 3天未提醒数
						int tj_wtxsuser7 = tj_wchzdsuser - tj_txsuser7;   // 7天未提醒数
						int tj_wtxsuser = tj_wchzdsuser- tj_cwtxsuser;
						int curfendans= accountShenheService.getcuitimefendanAccount(user_id,startDate,endDate,cuishouz);  //d当天分单数
						
						map.put("paixu", paixu);
						if (!StringHelper.isEmpty(cuishouz)) {
							map.put("userid", cuishouz+"--"+user_id);
						}else {
							map.put("userid", user_id);
						}
						//logger.info("tj_txscurtime"+tj_txsusercur);
						map.put("name", username);
						if (1==state) {
							map.put("state", "Yes");
						}else {
							map.put("state", "NO");
						}
						
						map.put("tj_ycszjine",  dfamt.format(tj_ycszjine));
						map.put("tj_ychzjineMon",  dfamt.format(tj_ychzjineMon));
						map.put("tj_ychzjineDay",  dfamt.format(tj_ychzjineDay));
						map.put("chbuserl", chbuserl);
						
						map.put("tj_ycszdsuser",  tj_ycszdsuser);
						map.put("tj_ychzdsuserMon",  tj_ychzdsuserMon);
						map.put("tj_yqfqdsallMon", tj_yqfqdsallMon);
						map.put("tj_wchzdsuser",  tj_wchzdsuser);
					//	map.put("tj_ychzdsuserMon",  tj_ychzdsuserMon);
						map.put("tj_ychzduserlDay",  tj_ychzduserlDay);
						map.put("tj_txscurtime", tj_txsusercur);
						map.put("tj_wtxs3", tj_wtxsuser3);
						map.put("tj_wtxs7", tj_wtxsuser7);
						map.put("tj_wtxs",tj_wtxsuser);  //d未处理单数
						map.put("fendans",curfendans);
						
						returndataList.add(map);
						//分组时，统计
						total_ycszjine += tj_ycszjine;
						total_ychzjineMon += tj_ychzjineMon;
						total_ychzjineDay += tj_ychzjineDay;
					
						total_ycszdsuser += tj_ycszdsuser;
						total_ychzdsuserMon += tj_ychzdsuserMon;
						total_yqfqdsallMon += tj_yqfqdsallMon;
						total_wchzdsuser += tj_wchzdsuser ;
						total_ychzduserlDay += tj_ychzduserlDay ;
						
						total_tjtxcuitime +=  tj_txsusercur;
						total_wtxsuser3 += tj_wtxsuser3 ;
						total_wtxsuser7 += tj_wtxsuser7 ;
						total_wtxsuser += tj_wtxsuser ;
						total_fendans +=curfendans;
		            }
					
				}
				
			}else {
				for(int i =0;i<listuserid.size();i++) {
					DataRow dataRow = listuserid.get(i);
					Map<String, Object> map = new HashMap<String, Object>();
					int user_id = dataRow.getInt("user_id");
					String username = dataRow.getString("name");
					int  state = dataRow.getInt("state");
			        
					// everyone
		            //double tj_ycszjine = accountShenheService.getCSTJcszjehk(user_id ,startDate,endDate,2);//当月应催收总金额
					//double tj_ycszjine = accountShenheService.getCSTJycsjinewhw(cuishouz,user_id) +accountShenheService.getCSTJychjine(user_id ,startDate,endDate,2) ;	
					//当月应催收总金额
					//double tj_ycszjine = accountShenheService.getCSTJycsjinewhw(cuishouz,user_id) +accountShenheService.getCSTJychzje(user_id,startDate,endDate,2,cuishouz);
					double tj_ycszjine = accountShenheService.getCSFendanjine(user_id ,startDate,endDate,2,cuishouz) ;	        
		            double tj_ychzjineMon = accountShenheService.getCSTJychzje(user_id,startDate,endDate,2,cuishouz);//， 当月已催回总金额
		            tj_ychzjineMon +=  getBeforeMonthlastDay_chje(user_id,startDate,endDate,1,cuishouz,1);//上个月最后一天催回金额
		            double tj_ychzjineDay = accountShenheService.getCSTJychzje(user_id,startDate,endDate,1,cuishouz);//， 当日已催回总金额
					String chbuserl = baifenbi(tj_ychzjineMon,tj_ycszjine);   //d比例
					//
					int tj_ycszdsuser = accountShenheService.getCSTJcsds(user_id ,startDate,endDate,2,cuishouz);//，  当月应催收总单数
					int tj_ychzdsuserMon = accountShenheService.getCSTJychds1(user_id ,startDate,endDate,2,cuishouz);  //， 当月已催回总单数 
					tj_ychzdsuserMon+=  getBeforeMonthlastDay_chje(user_id,startDate,endDate,1,cuishouz,2);
					int tj_yqfqdsallMon = accountShenheService.getcuishouhkyqAccount(user_id,startDate,endDate,2,cuishouz);  //a 已逾期再分期用户
					int tj_wchzdsuser = accountShenheService.getCSTJwchds(user_id,startDate,endDate,2,cuishouz);//，  当前未催回总单数
					int tj_ychzduserlDay = accountShenheService.getCSTJychds1(user_id,startDate,endDate,1,cuishouz);  //，已经催回总单数 当日
					
					int tj_txsusercur = accountShenheService.getCSTJtxzs(user_id,curtime,curtime,1,cuishouz);  //
					int tj_txsuser3 = accountShenheService.getCSTJtxzs(user_id,curtimeyMd3,curtime,1,cuishouz);  //当月3天提醒数
					int tj_txsuser7 = accountShenheService.getCSTJtxzs(user_id,curtimeyMd7,curtime,1,cuishouz);  //当月7天提醒数
					int tj_cwtxsuser = accountShenheService.getCSTJtxzs(user_id ,"","",-1,cuishouz);  //提醒总数
					int tj_wtxsuser3 = tj_wchzdsuser - tj_txsuser3;   // 3天未提醒数
					int tj_wtxsuser7 = tj_wchzdsuser - tj_txsuser7;   // 7天未提醒数
					int tj_wtxsuser = tj_wchzdsuser- tj_cwtxsuser;
					int curfendans= accountShenheService.getcuitimefendanAccount(user_id,startDate,endDate,cuishouz);  //d当天分单数
					
					map.put("paixu", i+1);
					if (!StringHelper.isEmpty(cuishouz)) {
						map.put("userid", cuishouz+"--"+user_id);
					}else {
						map.put("userid", user_id);
					}
					//logger.info("tj_txscurtime"+tj_txsusercur);
					map.put("name", username);
					if (1==state) {
						map.put("state", "Yes");
					}else {
						map.put("state", "NO");
					}
					
					map.put("tj_ycszjine",  dfamt.format(tj_ycszjine));
					map.put("tj_ychzjineMon",  dfamt.format(tj_ychzjineMon));
					map.put("tj_ychzjineDay",  dfamt.format(tj_ychzjineDay));
					map.put("chbuserl", chbuserl);
					
					map.put("tj_ycszdsuser",  tj_ycszdsuser);
					map.put("tj_ychzdsuserMon",  tj_ychzdsuserMon);
					map.put("tj_yqfqdsallMon", tj_yqfqdsallMon);
					map.put("tj_wchzdsuser",  tj_wchzdsuser);
				//	map.put("tj_ychzdsuserMon",  tj_ychzdsuserMon);
					map.put("tj_ychzduserlDay",  tj_ychzduserlDay);
					map.put("tj_txscurtime", tj_txsusercur);
					map.put("tj_wtxs3", tj_wtxsuser3);
					map.put("tj_wtxs7", tj_wtxsuser7);
					map.put("tj_wtxs",tj_wtxsuser);  //d未处理单数
					map.put("fendans",curfendans);
					
					returndataList.add(map);
					//分组时，统计
					total_ycszjine += tj_ycszjine;
					total_ychzjineMon += tj_ychzjineMon;
					total_ychzjineDay += tj_ychzjineDay;
				
					total_ycszdsuser += tj_ycszdsuser;
					total_ychzdsuserMon += tj_ychzdsuserMon;
					total_yqfqdsallMon += tj_yqfqdsallMon;
					total_wchzdsuser += tj_wchzdsuser ;
					total_ychzduserlDay += tj_ychzduserlDay ;
					
					total_tjtxcuitime +=  tj_txsusercur;
					total_wtxsuser3 += tj_wtxsuser3 ;
					total_wtxsuser7 += tj_wtxsuser7 ;
					total_wtxsuser += tj_wtxsuser ;
					total_fendans +=curfendans;
				}
			}
			
			
			if (!StringHelper.isEmpty(cuishouz)) {
				Map<String, Object> map = new HashMap<String, Object>();
				
				map.put("paixu", "");
				map.put("userid" ,cuishouz);
				map.put("name","total" );
				map.put("state", "");
				map.put("tj_ycszjine",  dfamt.format(total_ycszjine));
				map.put("tj_ychzjineMon",  dfamt.format(total_ychzjineMon));
				map.put("tj_ychzjineDay",  dfamt.format(total_ychzjineDay));
				map.put("chbuserl", baifenbi(total_ychzjineMon,total_ycszjine));
				
				map.put("tj_ycszdsuser",  total_ycszdsuser);
				map.put("tj_ychzdsuserMon",  total_ychzdsuserMon);
				map.put("tj_yqfqdsallMon", total_yqfqdsallMon);
				map.put("tj_wchzdsuser",  total_wchzdsuser);
				map.put("tj_ychzduserlDay",  total_ychzduserlDay);
				map.put("tj_txscurtime",total_tjtxcuitime);  //????
				map.put("tj_wtxs3", total_wtxsuser3);
				map.put("tj_wtxs7", total_wtxsuser7);
				map.put("tj_wtxs",total_wtxsuser);  //d未处理单数
				map.put("fendans",total_fendans);
				returndataList.add(map);
			}
		
			//logger.info("returndataList " + cuishouz+returndataList);
			
			return returndataList;
		}
		/**
		 * 用催收统计表---统计上个月最后一天的催回（金额、笔数）
		 * @param userid
		 * @param startTime
		 * @param endtTime
		 * @param MonDayType
		 * @param cuishouz
		 * @param return_type 1--金额， 2--笔数
		 * @return
		 */
			public double getBeforeMonthlastDay_chje(int userid ,String startTime,String endtTime,int MonDayType,String cuishouz,int return_type)  {
				double re_number =0;
				
				String setMinTime ="2019-08-31";  //a从九月份开始
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Calendar cdar = Calendar.getInstance();
				try {
					cdar.setTime(sdf.parse(setMinTime));
				} catch (java.text.ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				long mintime = cdar.getTimeInMillis();
				
				String setMxaTime ="2019-10-01";  //a从10月份结束
				try {
					cdar.setTime(sdf.parse(setMxaTime));
				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				long maxtime=cdar.getTimeInMillis();
				
				try {
					cdar.setTime(sdf.parse(endtTime));
				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				long endtime=cdar.getTimeInMillis();
				
				if(maxtime > endtime && endtime > mintime && cuishouz =="M1") {
					cdar.add(Calendar.MONTH, -1);	
					cdar.set(cdar.DAY_OF_MONTH, cdar.getActualMaximum(Calendar.DAY_OF_MONTH));
					String gtimelast = sdf.format(cdar.getTime()); 
					if(1==return_type) {
						re_number = accountShenheService.getCSTJychzje(userid,gtimelast,gtimelast,MonDayType,cuishouz);//， 当日已催回总金额
					}else if(2==return_type) {
						re_number = accountShenheService.getCSTJychds1(userid,gtimelast,gtimelast,MonDayType,cuishouz);//， 当日已催回笔数
					}
				     
				}
				return re_number;
			}



		
}
