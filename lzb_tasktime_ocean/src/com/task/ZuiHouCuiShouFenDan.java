package com.task;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import com.service.AotuZDSHALLService;
import com.service.ZuiHouCuiShouFenDanService;
import com.sun.jmx.snmp.Timestamp;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.timerengine.Task;

public class ZuiHouCuiShouFenDan {
	private static Logger logger = Logger.getLogger(Fenpeicuishou.class);
	private static ZuiHouCuiShouFenDanService CuiShouFenDanService = new ZuiHouCuiShouFenDanService();
	
	
	
	//全局分50%单参数
	//int suiji=2;	
	
	public static void main(String[] args) {
   
	    /**
	     * 用于批量分单
	     * 
	     */
     	if(true) {
     		//分单的类型：1.是催收人员对催收人员。2.
			 int type=1;
			//未提醒的天数 weitixingday:0重没有提醒，3是3天没有提醒。7是7天没有提醒。
			 int weitixingday=10;
			 //催收组:m1:1,m2:2,m3：3
			 int cuishouzu=1;
			 
			 //要分出去的催收人员名单0是代表所有数量  -1：表示当日未分配的单
			 int cuishouidout=50115   ;
			 //分出去的数量
			 int num=10;
			 //接收单子的催收人员名单 ： -1：表示分给本组人员
			 int cuishouidin= 50232;
			 //逾期天数
			 int yuqts=7;
			 

			 
			 
			 ZuiHouCuiShouFenDan fc = new ZuiHouCuiShouFenDan();
			 fc.cuishouM(cuishouzu,weitixingday,cuishouidout,num,cuishouidin,yuqts);
     	}
			
     	

     	/**
     	 * 根据分单记录表还原
     	 */
		 if(false) {

			 int cuishouzu=2;          //催收组:m1:1,m2:2,m3：3
			 int oldcuishou_id=  50103;    //原来催收ID
			 int cmsuser_id=  50248;       //操作后催收ID
			 int cmsuserld_id=   12;    //操作人id
			 String creat_time="2019-10-10";     //操作时间   "2019-08-01 0

			 
			 
			 ZuiHouCuiShouFenDan fc = new ZuiHouCuiShouFenDan();
			 boolean outid =fc.findStr(cuishouzu,oldcuishou_id);
			 
			 List<DataRow> hyrow = CuiShouFenDanService.getfendanhy(creat_time,oldcuishou_id+"",cmsuser_id+"",cmsuserld_id+"");
			 
			 for(DataRow row:hyrow) {
				    int jk_id= row.getInt("rec_id");
					//要分出去的催收人员
					 int cuishouidout= row.getInt("cmsuser_id"); 
					//接收单子的催收人员名单
					 int cuishouidin= row.getInt("oldcuishou_id");
					 
					
					//判断cuishouidout和cuishouidin是不是都和一个小组并且是不是有分单权限和在职		
//					boolean outid =fc.findStr(cuishouzu,cuishouidout);
					boolean inid =fc.findStr(cuishouzu,cuishouidin);
					boolean xt=cuishouidout!=cuishouidin;
					if(false==outid|| false==inid || false==xt||!(cuishouzu==1||cuishouzu==2||cuishouzu==3)) {
						logger.info("成员小组匹配错误");
						return;
					}
					 
					 Singlefendan( jk_id,cuishouidout, cuishouidin, cuishouzu );
			 }
			 
		 }
	 }
	
	
	public void cuishouM(int cuishouzu, int weitixingday,int cuishouidout,int num,int cuishouidin,int yuqts ) {
		
		Calendar calendar = Calendar.getInstance();// 日历对象
		calendar.setTime(new Date());// 设置当前日期
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");		 
        String today = sdf.format(new Date());
		 //中国方式的当前时间表示
        SimpleDateFormat fmtrq  = new SimpleDateFormat("yyyy-MM-dd");
		String time111 = fmtrq.format(new Date());
		
		//判断cuishouidout和cuishouidin是不是都和一个小组并且是不是有分单权限和在职		
		boolean outid =findStr(cuishouzu,cuishouidout);
		boolean inid =findStr(cuishouzu,cuishouidin);
		boolean xt=cuishouidout!=cuishouidin;
		//false==outid|| false==inid || false==xt||weitixingday!=0||weitixingday!=3||weitixingday!=7||num<0||cuishouzu!=1||cuishouzu!=2||cuishouzu!=3
		//||!(weitixingday==0||weitixingday==3||weitixingday==7)
		if(!(cuishouidout==-1 || cuishouidin==-1 )&& (false==outid|| false==inid || false==xt) ||num<0||!(cuishouzu==1||cuishouzu==2||cuishouzu==3)) {
			logger.info("成员小组匹配错误");
			return;
		}
		
		System.err.println("分出人："+cuishouidout);
		System.err.println("接收人："+cuishouidin);
		
		List<DataRow> listyqm1=null;
		
		if(cuishouidout==-1 ) {
			// 在职的催收人员的数量
			int[] cuishouAll =getAllcuishouM(cuishouzu);
			//查询今天要需要分的所有单数
			 listyqm1 =CuiShouFenDanService.getAllYQListYQ(cuishouzu,cuishouAll);
		}else {
			/* cuishouzu的意思：m1:1,m2:2,m3：3.
			 * weitixingday:0从没有提醒，3是3天没有提醒。7是7天没有提醒。
			  cuishouidout是查询的哪个催收人员。
			  .num需要查询出催收单数量.0是代表所有数量
			 */
			 listyqm1 = CuiShouFenDanService.getYqFenDan(cuishouzu,2,cuishouidout,num,yuqts);		
		}
		
		// 查询没有请假需要分单的催收人员
		int[] cuishouzuyq1 = getUserLeavetList(cuishouzu);
		
		int sizeyqm1 = listyqm1.size();		
		
		int aa = 0;
		int bb = cuishouzuyq1.length;		
		try{
			for(int i=0; i<sizeyqm1; i++){
				long kaishi=System.currentTimeMillis();
								
				//这个没有什么作用
				Random random = new Random();
				//下面有aa++
				int xiabiao = aa%bb;
				int cuishou;
				if(cuishouidin==-1) {
				 cuishou = cuishouzuyq1[xiabiao];
				}else {
				 cuishou =cuishouidin;
				}
				
				//获取借款表id
				DataRow dataRow = listyqm1.get(i);
				int jkid = dataRow.getInt("id");					
				//原来的催收人员
				int oldcuishou= dataRow.getInt("cuishou_id");
				
				//查询借款表的cuishou_m1
				int cuism1 = CuiShouFenDanService.getCuishouBG(jkid+"");
				
				//查询借款表的cuishouzz
				String cuishouzz = CuiShouFenDanService.getCuishouzz(jkid+""); 
				
				
				
				//修改
				dataRow.set("id", jkid);
				dataRow.set("cuishou_id", cuishou);
				if(cuishouzu==1) {
					dataRow.set("cuishou_m1", cuishou);
				}else if(cuishouzu==2){
					dataRow.set("cuishou_m2", cuishou);
				}else if(cuishouzu==3) {
					dataRow.set("cuishou_m3", cuishou);
				}
				
				//过年之后更改去掉的sd_new_jkyx				
				dataRow.set("cuishouzz", cuishouzz + "," + cuishou);
				//更改sd_new_jkyx上面相关id
				//aotuZDSHALLService.updateUserFP(dataRow);
				
				//分单记录表sd_cuishou_record
				DataRow rowre = new DataRow();
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
				//查询借款表的userid
				String userid = CuiShouFenDanService.getUserid(jkid+""); 
				rowre.set("user_id",userid);
				rowre.set("rec_id",jkid);
				rowre.set("cmsuser_id",cuishou);
				rowre.set("create_time",df.format(new Date()));
				rowre.set("cmsuserld_id",12);
				rowre.set("oldcuishou_id",oldcuishou);
				//添加信息sd_cuishou_record
				//aotuZDSHALLService.insertCuishouRecord(rowre);
				
				
				
			//sd_new_jkyx实际借款金额sjsh_money
			String sjshjine = CuiShouFenDanService.getSJSHJE(jkid+"");
			//sd_new_jkyx逾期利息yuq_lx
			String yuqlx = CuiShouFenDanService.getYQLX(jkid+"");
			//int能不能装的数值  
			int zje = Integer.parseInt(sjshjine.replace(",", ""))+Integer.parseInt(yuqlx.replace(",", ""));
			//sd_accountuserhk催收表格，查询催收id和事件
			 DataRow datacs = CuiShouFenDanService.getCuishouBG(cuishou,time111);
			 
			//sd_cuishou_fendan 表 lin，后面的传参:m1:1,m2:2,m3:3
				List<DataRow> cuishoufendanList =CuiShouFenDanService.getcuishoufendanid(jkid,time111.substring(0, 7),cuishouzu);
				int a = cuishoufendanList.size();
				 //f 分出人更新
				DataRow  dataRow1 = new DataRow();
				if (0!=a ) {
					DataRow datafendan = cuishoufendanList.get(0);   //只获取最大id，（最新的数据）
					int fendanid = datafendan.getInt("id");
					int fendancs =datafendan.getInt("fendan_cs");
					int cuishou_id= datafendan.getInt("cuishou_id");
					double cuihuijine_old = CuiShouFenDanService.getfendancuihuijine(jkid,cuishou_id,time111.substring(0, 7));  //以前催回金额
					double cuihuijinezs = CuiShouFenDanService.getrechargeMoneyAccount(jkid,cuishou_id,today.substring(3, 10));  //a总催回金额
					
					dataRow1.set("id",fendanid);
					dataRow1.set("cuishou_jine",cuihuijinezs-cuihuijine_old);
					dataRow1.set("recharge_money",cuihuijinezs-cuihuijine_old);
					logger.info("M"+cuishouzu+" 更新 ：fendanid： "+fendanid);
					//aotuZDSHALLService.updatefendandata(dataRow1);
									
				}
																		
				//xiong添加sd_cuishou_fendan信息，金额不相加			 
				 DataRow  acf = new DataRow();
				 
				 acf.set("user_id",dataRow.getInt("userid"));
				 acf.set("jk_id",jkid);
				 acf.set("cuishou_id",cuishou);
				 acf.set("fendan_time",time111);
				 acf.set("cuishou_jine",zje);
				 acf.set("cuishou_z",cuishouzu);
				 //acf.set("fendan_cs",fendancs);
				 acf.set("recharge_money",0);
				 //aotuZDSHALLService.insertSdCuishouFendan(acf);
			 
				 
				//sd_accountuserhk催收表格，增加催收人员的金额
				DataRow row11 = new DataRow();
			   if(datacs == null){
					
				   row11.set("csid", cuishou);
				   row11.set("totaljine", zje);
				   row11.set("time", time111);
				   //aotuZDSHALLService.insertCuiBG(row11);
				   
			   }else{
				   //sd_accountuserhk催收表格，增加催收人员的金额
				   double ysje = datacs.getDouble("totaljine");
				   int cuiid = datacs.getInt("id");
				  // DataRow row11 = new DataRow();
				   row11.set("id", cuiid);
				   row11.set("totaljine", ysje+zje);
				   //aotuZDSHALLService.updateCuiBG(row11);
			   }
				aa++;
				
				//dataRow:借款表 。rowre：分单记录表。dataRow1：a为判断条件，分单表分出去。acf：分担表添加。datacs：sd_accountuserhk：添加和修改
				CuiShouFenDanService.FenDanShiWu(dataRow, rowre,a, dataRow1, acf, row11, datacs );
				long jiesu=System.currentTimeMillis()-kaishi;
				
				logger.info("结束一个分单所花的时间："+jiesu);
			}
			logger.info("所有分单已经完成");			
		}
		catch (Exception e) {
			logger.info("分单失败,异常");
			// TODO: handle exception
			e.printStackTrace() ;
		}
		
				
	}
	

				
	/**
	 * 查询在职的催收人员	
	 */
	public int[] getAllcuishouM(int cuishouM){
	//催收人员m
	List<DataRow> cuishouid = CuiShouFenDanService.getAllcuishou(cuishouM);
	//催收人员的数量
	int[] cuishouidnum = new int[cuishouid.size()];	
	//催收人员的的id数组
	for (int m = 0; m < cuishouid.size(); m++) {		
		DataRow row = cuishouid.get(m);
		cuishouidnum[m] = row.getInt("user_id");
		int num =m+1;
		logger.info("第"+num+"催收M"+cuishouM+"人：" +cuishouidnum[m]);
	}
				
		return cuishouidnum;
	}
	
		
	
	
	/**
	 * 没有请假的在职催收人员	
	 */
	private int[] getUserLeavetList(int a) {

		// 催收不要分单的名单人员名单
		String bufendansql = " ";

		// 查询没有请假需要分单的催收人员
		List<DataRow> UserLeavet = CuiShouFenDanService.getUserLeavetList(a, bufendansql);

		// 催收人员的数量
		int[] UserLeavetnum = new int[UserLeavet.size()];

		// 催收人员的的id数组
		for (int m = 0; m < UserLeavet.size(); m++) {
			DataRow row = UserLeavet.get(m);
			UserLeavetnum[m] = row.getInt("user_id");
		}
		for (int mmm = 0; mmm < UserLeavetnum.length; mmm++) {
			int num = mmm + 1;
			if (1 == a) {
				logger.info("没请假" + num + "位催收M1的人：" + UserLeavetnum[mmm]);
			} else if (2 == a) {
				logger.info("没请假" + num + "位催收M2的人：" + UserLeavetnum[mmm]);
			} else if (3 == a) {
				logger.info("没请假" + num + "位催收M3的人：" + UserLeavetnum[mmm]);
			} else {
				logger.info("没请假" + num + "位异常催收的人：" + UserLeavetnum[mmm]);
			}
		}
		return UserLeavetnum;
	}
	
	
	
	// 查询该员工是否
	private boolean findStr(int cuishouzu,int str) {
		
		//查询在职的催收人员
		int[] a = getAllcuishouM(cuishouzu);

		boolean result = false;

		for (int s : a) {
			if (s == str) {
				return true;
			}
		}
		return result;
	}
	
	
	
	/**
	 * 指定userID、指定催收人-----分单方式
	 */
	
	public static void Singlefendan(int jkid,int oldcuishou,int cuishou,int cuishouzu ) {
		
		/*
		 * 需要设定参数
		 */
//		int jk_userid= 0;
//		//分入催收人
//		int cuishou=0;			
//		//原来的催收人员
//		int oldcuishou= 0;
//		int cuishouzu =1;
		
		
		try{
			
		    //中国方式的当前时间表示
            SimpleDateFormat fmtrq  = new SimpleDateFormat("yyyy-MM-dd");
		    String time111 = fmtrq.format(new Date());
		    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");		 
	        String today = sdf.format(new Date());
	        
			long kaishi=System.currentTimeMillis();
			
//			DataRow jkrow = CuiShouFenDanService.getnewjkyx(jk_userid+"");
//			int jkid =jkrow.getInt("id");
			//查询借款表的cuishou_m1
			int cuism1 = CuiShouFenDanService.getCuishouBG(jkid+"");
			
			//查询借款表的cuishouzz
			String cuishouzz = CuiShouFenDanService.getCuishouzz(jkid+""); 
			
			
			
			//修改
			DataRow  dataRow = new DataRow();
			dataRow.set("id", jkid);
			dataRow.set("cuishou_id", cuishou);
			if(cuishouzu==1) {
				dataRow.set("cuishou_m1", cuishou);
			}else if(cuishouzu==2){
				dataRow.set("cuishou_m2", cuishou);
			}else if(cuishouzu==3) {
				dataRow.set("cuishou_m3", cuishou);
			}
			
			//过年之后更改去掉的sd_new_jkyx				
			dataRow.set("cuishouzz", cuishouzz + "," + cuishou);
			//更改sd_new_jkyx上面相关id
			//aotuZDSHALLService.updateUserFP(dataRow);
			
			//分单记录表sd_cuishou_record
			DataRow rowre = new DataRow();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
			//查询借款表的userid
			String userid = CuiShouFenDanService.getUserid(jkid+""); 
			rowre.set("user_id",userid);
			rowre.set("rec_id",jkid);
			rowre.set("cmsuser_id",cuishou);
			rowre.set("create_time",df.format(new Date()));
			rowre.set("cmsuserld_id",12);
			rowre.set("oldcuishou_id",oldcuishou);
			//添加信息sd_cuishou_record
			//aotuZDSHALLService.insertCuishouRecord(rowre);
				
				
				
			//sd_new_jkyx实际借款金额sjsh_money
			String sjshjine = CuiShouFenDanService.getSJSHJE(jkid+"");
			//sd_new_jkyx逾期利息yuq_lx
			String yuqlx = CuiShouFenDanService.getYQLX(jkid+"");
			//int能不能装的数值  
			int zje = Integer.parseInt(sjshjine.replace(",", ""))+Integer.parseInt(yuqlx.replace(",", ""));
			//sd_accountuserhk催收表格，查询催收id和事件
			 DataRow datacs = CuiShouFenDanService.getCuishouBG(cuishou,time111);
			 
			//sd_cuishou_fendan 表 lin，后面的传参:m1:1,m2:2,m3:3
			List<DataRow> cuishoufendanList =CuiShouFenDanService.getcuishoufendanid(jkid,time111.substring(0, 7),cuishouzu);
			int a = cuishoufendanList.size();
			 //f 分出人更新
			DataRow  dataRow1 = new DataRow();
			if (0!=a ) {
				DataRow datafendan = cuishoufendanList.get(0);   //只获取最大id，（最新的数据）
				int fendanid = datafendan.getInt("id");
				int fendancs =datafendan.getInt("fendan_cs");
				int cuishou_id= datafendan.getInt("cuishou_id");
				double cuihuijine_old = CuiShouFenDanService.getfendancuihuijine(jkid,cuishou_id,time111.substring(0, 7));  //以前催回金额
				double cuihuijinezs = CuiShouFenDanService.getrechargeMoneyAccount(jkid,cuishou_id,today.substring(3, 10));  //a总催回金额
				
				dataRow1.set("id",fendanid);
				dataRow1.set("cuishou_jine",cuihuijinezs-cuihuijine_old);
				dataRow1.set("recharge_money",cuihuijinezs-cuihuijine_old);
				logger.info("M"+cuishouzu+" 更新 ：fendanid： "+fendanid);
				//aotuZDSHALLService.updatefendandata(dataRow1);
								
			}
																	
			//xiong添加sd_cuishou_fendan信息，金额不相加			 
			 DataRow  acf = new DataRow();
			 
			 acf.set("user_id",dataRow.getInt("userid"));
			 acf.set("jk_id",jkid);
			 acf.set("cuishou_id",cuishou);
			 acf.set("fendan_time",time111);
			 acf.set("cuishou_jine",zje);
			 acf.set("cuishou_z",cuishouzu);
			 //acf.set("fendan_cs",fendancs);
			 acf.set("recharge_money",0);
			 //aotuZDSHALLService.insertSdCuishouFendan(acf);
		 
			 
			//sd_accountuserhk催收表格，增加催收人员的金额
			DataRow row11 = new DataRow();
		   if(datacs == null){
				
			   row11.set("csid", cuishou);
			   row11.set("totaljine", zje);
			   row11.set("time", time111);
			   //aotuZDSHALLService.insertCuiBG(row11);
			   
		   }else{
			   //sd_accountuserhk催收表格，增加催收人员的金额
			   double ysje = datacs.getDouble("totaljine");
			   int cuiid = datacs.getInt("id");
			  // DataRow row11 = new DataRow();
			   row11.set("id", cuiid);
			   row11.set("totaljine", ysje+zje);
			   //aotuZDSHALLService.updateCuiBG(row11);
		   }
			
			//dataRow:借款表 。rowre：分单记录表。dataRow1：a为判断条件，分单表分出去。acf：分担表添加。datacs：sd_accountuserhk：添加和修改
			CuiShouFenDanService.FenDanShiWu(dataRow, rowre,a, dataRow1, acf, row11, datacs );
			long jiesu=System.currentTimeMillis()-kaishi;
			
			logger.info("结束一个分单所花的时间："+jiesu);
		
		    logger.info("所有分单已经完成");			
		}
		catch (Exception e) {
			logger.info("分单失败,异常");
			// TODO: handle exception
			e.printStackTrace() ;
		}
	}
	
			 	 		 					
}
