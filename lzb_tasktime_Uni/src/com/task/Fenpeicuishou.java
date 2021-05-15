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
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.timerengine.Task;

public class Fenpeicuishou implements Task {
	private static Logger logger = Logger.getLogger(Fenpeicuishou.class);
	private static AotuZDSHALLService aotuZDSHALLService = new AotuZDSHALLService();
	@Override
	public void execute() 
	{
		Date date = new Date();
	    Calendar calendar = Calendar.getInstance();//日历对象
	    calendar.setTime(date);//设置当前日期			
	    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");		 
        String today = sdf.format(date);
        calendar.add(Calendar.DATE,2);
        //+2天的天数
        String nextDay = sdf.format(calendar.getTime());
        String ri="";
        String yue="";
        //中国方式的当前时间表示
        SimpleDateFormat fmtrq  = new SimpleDateFormat("yyyy-MM-dd");
		String time111 = fmtrq.format(new Date());
		
        if(today.substring(3, 5).equals(nextDay.substring(3, 5))){
        	ri= nextDay.substring(0, 2);
        	yue= nextDay.substring(3, 5);
        }else{
        	ri= nextDay.substring(0, 2);
        	yue= nextDay.substring(3, 5);
        }
        
       
		// 这里提取的是什么用户借款表,客服分单
		List<DataRow> list = aotuZDSHALLService.getAllYQList(ri, yue);
		// 把借款单根据用户名分单是分类
		int size = list.size();
      		
		int txhk1 = aotuZDSHALLService.getTXHK1();
		int txhkbl1 = aotuZDSHALLService.getTXHKBL1();
		int txhk2 = aotuZDSHALLService.getTXHK2();
		int txhkbl2 = aotuZDSHALLService.getTXHKBL2();
		int txhk3 = aotuZDSHALLService.getTXHK3();	
		//客服人员
		int txhk4 = aotuZDSHALLService.getTXHK4();
		//没有作用
		int txhkbl3 = aotuZDSHALLService.getTXHKBL3();
		
		
		int kefufendan=0;
		try{
			for(int i=0; i<size; i++){
				DataRow dataRow = list.get(i);
				int cuishou = 8888;
				String username = dataRow.getString("username");								
				
				cuishou = txhk1;
				if(kefufendan%2==0) {
					cuishou = txhk1;
				}else {
				    cuishou = txhkbl1;	
				}	
				
				int jkid = dataRow.getInt("id");
				DataRow row11 = new DataRow();
				row11.set("id", jkid);
				row11.set("cuishou_id", cuishou);
				row11.set("cuishou_tx", cuishou);					
				//按照规则表-系统设定的一些规则把
				aotuZDSHALLService.updateUserFP(row11);	
				kefufendan++;
			}
		}
			 catch (Exception e) {
				// TODO: handle exception
				 e.printStackTrace() ;
			}
		

		
//****************************************q客服分单
//第二种分单方式	
//		int kefufendan=2;
//		int  xiangmu=0;
//		try{
//			for(int i=0; i<size; i++){
//				DataRow dataRow = list.get(i);
//				int cuishou = txhk3;
//				String username = dataRow.getString("username");								
//				
//				if("M88".equals(username.substring(0, 3))) {
////					if(kefufendan%2==0) {
////					cuishou = 8026;
////					}else {
////					cuishou = 8024;	
////					}	
//					cuishou = 8024;	
//					kefufendan++;		
//				}else if("M99".equals(username.substring(0, 3))){					
////					if(kefufendan%2==0) {
////						cuishou = 8026;
////					}else {
////						cuishou = 8024;	
////					}	
//					cuishou = 8024;	
//					kefufendan++;
//				}else if("MOFA".equals(username.substring(0, 4))){
//					cuishou =8011;
//					
//				}else if("F168".equals(username.substring(0, 4))){
//					cuishou =8011;
//					
//				//这是什么用户名开头前是这个做什么用的
//				}else if("TAVA".equals(username.substring(0, 4)) || "TAFA".equals(username.substring(0, 4))){
//					cuishou = 8011;					
//				}else if("F458".equals(username.substring(0, 4))){
//					xiangmu=3;
//					cuishou = 8013;
//					kefufendan++;					
//				}else if("VANA".equals(username.substring(0, 4))){
//					cuishou = 8024;				
//				}
//					int jkid = dataRow.getInt("id");
//					DataRow row11 = new DataRow();
//					row11.set("id", jkid);
//					row11.set("cuishou_id", cuishou);
//					row11.set("cuishou_tx", cuishou);					
//					//按照规则表-系统设定的一些规则把
//					aotuZDSHALLService.updateUserFP(row11);													
//			}
//		}catch (Exception e) {
//			// TODO: handle exception
//			 e.printStackTrace() ;
//		}
		
		
//************************** M 1 2 3 分单**************************//
//		int cuishou_zu_m123 =123;
//		int cuishouM123=0;
//		int[] cuishouzuyqM123= getUserLeavetList(123);
//		// 催收人员的数量
//		int cuishouzuyqm123[] = new int[cuishouzuyqM123.length];
//		// 催收人员的的id数组
//		String  cuishou_M123="";
//		for (int m = 0; m < cuishouzuyqM123.length; m++) {
//			cuishouzuyqm123[m]=cuishouzuyqM123[m] ;
//			cuishou_M123+= " and cuishou_id <>"+cuishouzuyqm123[m] ;
//			logger.info("第"+m+"催收M123人：" +cuishouzuyqM123[m]);
//		}
//				
//		List<DataRow> listyqdayM123 = aotuZDSHALLService.getAllYQListYQNewM123(cuishou_M123);
//		
//		// 1号统计催收人员剩余催收单数 lin 2019-7-8 M123
//		if (time111.substring(7, 10).equals("-01")) {
//			everyMoncuishoudantongji(cuishouzuyqm123, 123);
//		}
//		
//		int ccM123 =0;
//		int M123 =0;
//		int ddM123 =cuishouzuyqM123.length;
//		try{
//			for(int i=0; i<listyqdayM123.size(); i++){
//				
//				int xiabiao = ccM123%ddM123;
//				int cuishou = cuishouzuyqM123[xiabiao];
//				
//				if(findStr(cuishou)) {
//					if(cuishou==M123) {
//						cuishouM123++;
//					}
//					if(cuishouM123 == 0) {
//						M123 = cuishou;
//						cuishouM123++;
//					}
//					if(cuishouM123%2==0) {						
//						Random random1 = new Random();
//						int xiabiao1= random1.nextInt(cuishouzuyqM123.length);
//						cuishou = cuishouzuyqM123[xiabiao1];
//						for(int ij=0;ij<10;ij++) {
//							 if(findStr(cuishou)) {
//								int xia= random1.nextInt(cuishouzuyqM123.length);
//								cuishou = cuishouzuyqM123[xia];
//							 }else {
//								 break;
//							 }
//						 }
//					}
//				}
//				DataRow dataRow = listyqdayM123.get(i);	
//				int oldcuishou= dataRow.getInt("cuishou_id");
//				int jkid = dataRow.getInt("id");
//				String cuishouzz = aotuZDSHALLService.getCuishouzz(jkid+""); 
//				
//				dataRow.set("id", jkid);
//				dataRow.set("cuishou_id", cuishou);
//				dataRow.set("cuishou_m0", cuishou);
//				dataRow.set("cuishou_m1", cuishou);
//				dataRow.set("cuishou_m2", cuishou);
//				dataRow.set("cuishou_m3", cuishou);
//				dataRow.set("cuishouzz", cuishouzz + "," + cuishou);
//				
//				DataRow rowre = new DataRow();
//				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
//
//				String userid = aotuZDSHALLService.getUserid(jkid+""); 
//				rowre.set("user_id",userid);
//				rowre.set("rec_id",jkid);
//				rowre.set("cmsuser_id",cuishou);
//				rowre.set("create_time",df.format(new Date()));
//				rowre.set("cmsuserld_id",6);
//				rowre.set("oldcuishou_id",oldcuishou);
//				
//				String sjshjine = aotuZDSHALLService.getSJSHJE(jkid+"");
//				String yuqlx = aotuZDSHALLService.getYQLX(jkid+"");
//				int zje = Integer.parseInt(sjshjine.replace(",", ""))+Integer.parseInt(yuqlx.replace(",", ""));
//				DataRow datacs = aotuZDSHALLService.getCuishouBG(cuishou,time111);
//				List<DataRow> cuishoufendanList =aotuZDSHALLService.getcuishoufendanid(jkid,time111.substring(0, 7),cuishou_zu_m123);
//				int a = cuishoufendanList.size();
//				//f 分出人更新
//				DataRow  dataRow1 = new DataRow();
//				if (0!=a ) {
//					DataRow datafendan = cuishoufendanList.get(0);   //只获取最大id，（最新的数据）
//					int fendanid = datafendan.getInt("id");
//					int fendancs =datafendan.getInt("fendan_cs");
//					int cuishou_id= datafendan.getInt("cuishou_id");
//					double cuihuijine_old = aotuZDSHALLService.getfendancuihuijine(jkid,cuishou_id,time111.substring(0, 7));  //以前催回金额
//					double cuihuijinezs = aotuZDSHALLService.getrechargeMoneyAccount(jkid,cuishou_id,today.substring(3, 10));  //a总催回金额
//					
//					dataRow1.set("id",fendanid);
//					dataRow1.set("cuishou_jine",cuihuijinezs-cuihuijine_old);
//					dataRow1.set("recharge_money",cuihuijinezs-cuihuijine_old);
//					logger.info("M0 更新 ：fendanid： "+fendanid);
//				}			 
//				DataRow  acf = new DataRow();
//				acf.set("user_id",dataRow.getInt("userid"));
//				acf.set("jk_id",jkid);
//				acf.set("cuishou_id",cuishou);
//				acf.set("fendan_time",time111);
//				acf.set("cuishou_jine",zje);
//				acf.set("cuishou_z",cuishou_zu_m123);
//				acf.set("recharge_money",0);
//				DataRow row11 = new DataRow();
//				ccM123++;				
//				aotuZDSHALLService.FenDanShiWu(dataRow, rowre,a, dataRow1, acf, row11, datacs );
//			}						
//		}
//		catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace() ;
//		}
		


			//*******************M0*********************///
					int suijiM0=0;
					int suijiM0old=0;
					
					List<DataRow> listyqday1M0 = aotuZDSHALLService.getAllYQListYQNewM0();
					int[] cuishouzuyqday1M0 = getUserLeavetList(0);
					// 1号统计催收人员剩余催收单数 lin 2019-7-8 M1
					if (time111.substring(7, 10).equals("-01")) {
						everyMoncuishoudantongji(cuishouzuyqday1M0, 4);
						//z在1号增加1-3的催收单
						//List<DataRow> listyqm0_t = aotuZDSHALLService.getAllYQListYQM1_T();
						//listyqday1M0.addAll(listyqm0_t);
					}
					int ccM0 =0;
					int M0 =0;
					int ddM0=cuishouzuyqday1M0.length;
					try{
						for(int i=0; i<listyqday1M0.size(); i++){
							
							int xiabiao = ccM0%ddM0;
							int cuishou = cuishouzuyqday1M0[xiabiao];
							
							if(findStr(cuishou)) {
								if(cuishou==M0) {
									suijiM0++;
								}
								if(suijiM0 == 0) {
									M0 = cuishou;
									suijiM0++;
								}
								if(suijiM0%2==0) {						
									Random random1 = new Random();
									int xiabiao1= random1.nextInt(cuishouzuyqday1M0.length);
									cuishou = cuishouzuyqday1M0[xiabiao1];						
								}
							}
							DataRow dataRow = listyqday1M0.get(i);	
							int oldcuishou= dataRow.getInt("cuishou_id");
							int jkid = dataRow.getInt("id");
							String cuishouzz = aotuZDSHALLService.getCuishouzz(jkid+""); 
							
							dataRow.set("id", jkid);
							dataRow.set("cuishou_id", cuishou);
							dataRow.set("cuishou_m0", cuishou);
							dataRow.set("cuishouzz", cuishouzz + "," + cuishou);
							
							DataRow rowre = new DataRow();
							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式

							String userid = aotuZDSHALLService.getUserid(jkid+""); 
							rowre.set("user_id",userid);
							rowre.set("rec_id",jkid);
							rowre.set("cmsuser_id",cuishou);
							rowre.set("create_time",df.format(new Date()));
							rowre.set("cmsuserld_id",6);
							rowre.set("oldcuishou_id",oldcuishou);
							
							String sjshjine = aotuZDSHALLService.getSJSHJE(jkid+"");
							String yuqlx = aotuZDSHALLService.getYQLX(jkid+"");
							int zje = Integer.parseInt(sjshjine.replace(",", ""))+Integer.parseInt(yuqlx.replace(",", ""));
							DataRow datacs = aotuZDSHALLService.getCuishouBG(cuishou,time111);
							List<DataRow> cuishoufendanList =aotuZDSHALLService.getcuishoufendanid(jkid,time111.substring(0, 7),4);
							int a = cuishoufendanList.size();
							//f 分出人更新
							DataRow  dataRow1 = new DataRow();
							if (0!=a ) {
								DataRow datafendan = cuishoufendanList.get(0);   //只获取最大id，（最新的数据）
								int fendanid = datafendan.getInt("id");
								int fendancs =datafendan.getInt("fendan_cs");
								int cuishou_id= datafendan.getInt("cuishou_id");
								double cuihuijine_old = aotuZDSHALLService.getfendancuihuijine(jkid,cuishou_id,time111.substring(0, 7));  //以前催回金额
								double cuihuijinezs = aotuZDSHALLService.getrechargeMoneyAccount(jkid,cuishou_id,today.substring(3, 10));  //a总催回金额
								
								dataRow1.set("id",fendanid);
								dataRow1.set("cuishou_jine",cuihuijinezs-cuihuijine_old);
								dataRow1.set("recharge_money",cuihuijinezs-cuihuijine_old);
								logger.info("M0 更新 ：fendanid： "+fendanid);
							}			 
							DataRow  acf = new DataRow();
							acf.set("user_id",dataRow.getInt("userid"));
							acf.set("jk_id",jkid);
							acf.set("cuishou_id",cuishou);
							acf.set("fendan_time",time111);
							acf.set("cuishou_jine",zje);
							acf.set("cuishou_z",4);
							acf.set("recharge_money",0);
							DataRow row11 = new DataRow();
							ccM0++;				
							aotuZDSHALLService.FenDanShiWu(dataRow, rowre,a, dataRow1, acf, row11, datacs );
						}						
					}
					catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace() ;
					}
					List<DataRow> cuishoum0 = aotuZDSHALLService.getAllcuishouM0();
					// 催收人员的数量
					int cuishouzuyqm0[] = new int[cuishoum0.size()];
					// 催收人员的的id数组
					for (int m = 0; m < cuishoum0.size(); m++) {
						DataRow row = cuishoum0.get(m);
						cuishouzuyqm0[m] = row.getInt("user_id");
						 int num =m+1;
						logger.info("第"+num+"催收M0人：" +cuishouzuyqm0[m]);
					}
					// 查询用户借款表逾期0-15，并且不等于分配了催收人员
					List<DataRow> listyqm0 = aotuZDSHALLService.getAllYQListYQM0(cuishouzuyqm0);
					
					int sizeyqm0 = listyqm0.size();	
					
					int aaM0 = 0;
					int bbM0 = cuishouzuyqm0.length;
					
					//查询最大催收人员催回比例是否都为0；		
					Set<Integer> setM0=cuishouIdNew(4,cuishouzuyqm0);
					//判断是最大催收比例为0
					boolean cuishousetM0= setM0.contains(0);
					//xian pai xu
					float countaaM0[] = new float[cuishouzuyqm0.length];
					int useridaaM0[] = new int[cuishouzuyqm0.length];
					for(int j=0 ;j<cuishouzuyqm0.length;j++){
						useridaaM0[j]=cuishouzuyqm0[j];
						double tj_ycszjine = aotuZDSHALLService.getCSFendanjine(useridaaM0[j],"",time111,2,4);
						double tj_ychzjineMon = aotuZDSHALLService.getCSTJychzje(useridaaM0[j],"",time111,2,4);
						float chbuserl=baifenbi(tj_ychzjineMon,tj_ycszjine);
						countaaM0[j]=chbuserl; //总笔数
					}
					int nnM0 = countaaM0.length;
					for (int mM0 = 0; mM0 < nnM0 - 1; mM0++) {   
						for (int n = 0; n < nnM0 - 1; n++) { 
							if (countaaM0[n] < countaaM0[n + 1]) { 
								float tempaa = countaaM0[n];   
								int tempaaa = useridaaM0[n]; 
								countaaM0[n] = countaaM0[n + 1];   
								useridaaM0[n] = useridaaM0[n + 1]; 
								countaaM0[n + 1] = tempaa;   
								useridaaM0[n + 1] = tempaaa;   
							}   
						}   
					}
					for (int ma = 0; ma < countaaM0.length; ma++) {   
						logger.info("userid:"+useridaaM0[ma]);
						logger.info("bl:"+countaaM0[ma]);
					}
					int znumM0 =0;
					for(int i=0;i<bbM0;i++){
						znumM0 += bbM0 - i;
					}
					logger.info(znumM0);
					logger.info(sizeyqm0);
					//求平均数
					int avgM0=sizeyqm0/(bbM0<=0?1:bbM0);
					logger.info(avgM0);
					int midnumM0 = bbM0 / 2 ;
					int	avgminM0=avgM0-5;
					int	avgmaxM0=avgM0+5;
					if(avgmaxM0<=10) {
						avgminM0=avgM0-3;
						avgmaxM0=avgM0+3;
					}
					if(avgminM0<0) {
						avgminM0=0;
					}
					int aaaM0 = 0;
					int bbbM0 = 1;
					try{
						for(int i=0; i<sizeyqm0; i++){
							long kaishi=System.currentTimeMillis();
							
							//这个没有什么作用
							Random random = new Random();
							//下面有aa++
							int xiabiao = aaM0%bbM0;
							int cuishou = cuishouzuyqm0[xiabiao];
							
							
							cuishou = useridaaM0[aaaM0];
							aaaM0++;
							if(aaaM0>=useridaaM0.length) {
								aaaM0 = 0;
							} 
							if(bbbM0 == 1 && bbM0 != 0) {
								if(sizeyqm0 < znumM0) {
									for(int k=0 ;k<useridaaM0.length;k++){
										//ru guo xu yao jia da ((bb-k)-midnum)*x  wei jia da de bei shu
										int zdchaju = 0;
										if(bbM0 != 0) {
											zdchaju = (2 * ((bbM0-k-1)-midnumM0)/midnumM0) ;//4
										}
										int xyfendanshu = avgM0 + zdchaju ;
										//mofa 5017 fen 50%
										if(useridaaM0[k] == 5017) {
											xyfendanshu = avgM0/2 + 1;
										}
										if(xyfendanshu<=0) {
											xyfendanshu = 1 ;
										}
										int curfendans=aotuZDSHALLService.getcuitimefendanAccount(useridaaM0[k],"",time111,"M0");
										if(curfendans < xyfendanshu) {
											cuishou = useridaaM0[k];
											break;
										}
										if(k == bbM0-1 && curfendans >= xyfendanshu) {
											aaaM0=0;
											bbbM0=0;
											cuishou = useridaaM0[aaaM0];
											aaaM0++;
										}
									}
								}else {
									for(int k=0 ;k<useridaaM0.length;k++){
										//ru guo xu yao jia da ((bb-k)-midnum)*x  wei jia da de bei shu
										int zdchaju = 0;
										if(bbM0 != 0 && midnumM0 != 0) {
											zdchaju = (3 * ((bbM0-k-1)-midnumM0)/midnumM0) ;//4
										}
										int xyfendanshu = avgM0 + zdchaju ;
										//mofa 5017 fen 50%
										if(useridaaM0[k] == 5017) {
											xyfendanshu = avgM0/2 + 1;
										}
										int curfendans=aotuZDSHALLService.getcuitimefendanAccount(useridaaM0[k],"",time111,"M0");
										if(curfendans < xyfendanshu) {
											cuishou = useridaaM0[k];
											break;
										}
										if(k == bbM0-1 && curfendans == xyfendanshu) {
											aaaM0=0;
											bbbM0=0;
											cuishou = useridaaM0[aaaM0];
											aaaM0++;
										}
									}
								}
							}
							if(findStr(cuishou)) {
								if(cuishou==M0) {
									suijiM0old++;
								}
								if(suijiM0old == 0) {
									M0 = cuishou;
									suijiM0old++;
								}
								if(suijiM0old%2==0) {						
									Random random1 = new Random();
									int xiabiao1= random1.nextInt(cuishouzuyqm0.length);
									cuishou = cuishouzuyqm0[xiabiao1];						
								}
							}
							//获取借款表id
							DataRow dataRow = listyqm0.get(i);	
							//原来的催收人员
							int oldcuishou= dataRow.getInt("cuishou_id");
							
							int jkid = dataRow.getInt("id");
							//查询借款表的cuishou_m1
							int cuism0 = aotuZDSHALLService.getCuishouM0(jkid+"");
							
							//查询借款表的cuishouzz
							String cuishouzz = aotuZDSHALLService.getCuishouzz(jkid+""); 
							int ylcuishoum0state = aotuZDSHALLService.getOLDfdqxM0(cuism0);
							if(ylcuishoum0state == 1) {
								cuishou = cuism0;
							}
							//修改
							dataRow.set("id", jkid);
							dataRow.set("cuishou_id", cuishou);
							dataRow.set("cuishou_m0", cuishou);
							dataRow.set("cuishouzz", cuishouzz + "," + cuishou);

							DataRow rowre = new DataRow();
							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
							//查询借款表的userid
							String userid = aotuZDSHALLService.getUserid(jkid+""); 
							rowre.set("user_id",userid);
							rowre.set("rec_id",jkid);
							rowre.set("cmsuser_id",cuishou);
							rowre.set("create_time",df.format(new Date()));
							rowre.set("cmsuserld_id",6);
							rowre.set("oldcuishou_id",oldcuishou);
							
							//sd_new_jkyx实际借款金额sjsh_money
							String sjshjine = aotuZDSHALLService.getSJSHJE(jkid+"");
							//sd_new_jkyx逾期利息yuq_lx
							String yuqlx = aotuZDSHALLService.getYQLX(jkid+"");
							//int能不能装的数值  
							int zje = Integer.parseInt(sjshjine.replace(",", ""))+Integer.parseInt(yuqlx.replace(",", ""));
							//sd_accountuserhk催收表格，查询催收id和事件
							DataRow datacs = aotuZDSHALLService.getCuishouBG(cuishou,time111);
							
							//sd_cuishou_fendan 表 lin，后面的传参:m1:1,m2:2,m3:3
							List<DataRow> cuishoufendanList =aotuZDSHALLService.getcuishoufendanid(jkid,time111.substring(0, 7),4);
							int a = cuishoufendanList.size();
							//f 分出人更新
							DataRow  dataRow1 = new DataRow();
							if (0!=a ) {
								DataRow datafendan = cuishoufendanList.get(0);   //只获取最大id，（最新的数据）
								int fendanid = datafendan.getInt("id");
								int fendancs =datafendan.getInt("fendan_cs");
								int cuishou_id= datafendan.getInt("cuishou_id");
								double cuihuijine_old = aotuZDSHALLService.getfendancuihuijine(jkid,cuishou_id,time111.substring(0, 7));  //以前催回金额
								double cuihuijinezs = aotuZDSHALLService.getrechargeMoneyAccount(jkid,cuishou_id,today.substring(3, 10));  //a总催回金额
								
								dataRow1.set("id",fendanid);
								dataRow1.set("cuishou_jine",cuihuijinezs-cuihuijine_old);
								dataRow1.set("recharge_money",cuihuijinezs-cuihuijine_old);
								logger.info("M0 更新 ：fendanid： "+fendanid);
							}		 
							DataRow  acf = new DataRow();
							acf.set("user_id",dataRow.getInt("userid"));
							acf.set("jk_id",jkid);
							acf.set("cuishou_id",cuishou);
							acf.set("fendan_time",time111);
							acf.set("cuishou_jine",zje);
							acf.set("cuishou_z",4);
							acf.set("recharge_money",0);
							//sd_accountuserhk催收表格，增加催收人员的金额
							DataRow row11 = new DataRow();
							aaM0++;
							//dataRow:借款表 。rowre：分单记录表。dataRow1：a为判断条件，分单表分出去。acf：分担表添加。datacs：sd_accountuserhk：添加和修改
							aotuZDSHALLService.FenDanShiWu(dataRow, rowre,a, dataRow1, acf, row11, datacs );
							long jiesu=System.currentTimeMillis()-kaishi;
							
							logger.info("结束一个分单所花的时间："+jiesu);
						}
					}
					catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace() ;
					}
					
					
			//*****************************************全局分50%单参数
							int suiji=0;
							int M1=0;
							int xs1=2;
							int xs2=2;
							int xs3=2;
							int xs4=2;
							int xs5=2;
							
							List<DataRow> cuishoum1 = aotuZDSHALLService.getAllcuishouM1();

							// 催收人员的数量
							int cuishouzuyq[] = new int[cuishoum1.size()];

							// 催收人员的的id数组
							for (int m = 0; m < cuishoum1.size(); m++) {
								DataRow row = cuishoum1.get(m);
								cuishouzuyq[m] = row.getInt("user_id");
								 int num =m+1;
								logger.info("第"+num+"催收M1人：" +cuishouzuyq[m]);
							}

							//新的催收单子
							List<DataRow> listyqday1 = aotuZDSHALLService.getAllYQListYQNew();
							
							// 1号统计催收人员剩余催收单数 lin 2019-7-8 M1
							if (time111.substring(7, 10).equals("-01")) {
								everyMoncuishoudantongji(cuishouzuyq, 1);
								//z在1号增加8-15的催收单
								List<DataRow> listyqm1_t = aotuZDSHALLService.getAllYQListYQM1_T();
								listyqday1.addAll(listyqm1_t);
							}
							
							
			//*****************************************新单子平均分
							
							int numday1=listyqday1.size();
							//查询没有请假需要分单的催收人员
							int[] cuishouzuyqday1 = getUserLeavetList(1);
							int cc =0;
							int dd=cuishouzuyqday1.length;
							try{
								//后5名分50%给前五名
								//xian pai xu
								int zcsr = cuishouzuyqday1.length;
								float countaa[] = new float[zcsr];
								int useridaa[] = new int[zcsr];
								for(int j=0 ;j<zcsr;j++){
									useridaa[j]=cuishouzuyqday1[j];
									double tj_ycszjine = aotuZDSHALLService.getCSFendanjine(useridaa[j],"",time111,2,1);
									double tj_ychzjineMon = aotuZDSHALLService.getCSTJychzje(useridaa[j],"",time111,2,1);
									float chbuserl=baifenbi(tj_ychzjineMon,tj_ycszjine);
									countaa[j]=chbuserl; //总笔数
								}
								for (int m = 0; m < zcsr - 1; m++) {   
									for (int n = 0; n < zcsr - 1; n++) { 
										if (countaa[n] < countaa[n + 1]) { 
											float tempaa = countaa[n];   
											int tempaaa = useridaa[n]; 
											countaa[n] = countaa[n + 1];   
											useridaa[n] = useridaa[n + 1]; 
											countaa[n + 1] = tempaa;   
											useridaa[n + 1] = tempaaa;   
										}   
									}   
								}
								for (int ma = 0; ma < countaa.length; ma++) {   
									logger.info("userid:"+useridaa[ma]);
									logger.info("bl:"+countaa[ma]);
								}
								for(int i=0; i<listyqday1.size(); i++){
									//long kaishi=System.currentTimeMillis();
																	
									int xiabiao = cc%dd;
									int cuishou = cuishouzuyqday1[xiabiao];
									
									if (Integer.parseInt(time111.substring(8, 10))>=15) {
										if(zcsr>5) {
											if(cuishou == useridaa[zcsr-1]) {
												xs1++;
												if(xs1%4==0) {			
													cuishou = useridaa[0];						
												}
											}
											if(cuishou == useridaa[zcsr-2]) {
												xs2++;
												if(xs2%4==0) {			
													cuishou = useridaa[1];						
												}
											}
											if(cuishou == useridaa[zcsr-3]) {
												xs3++;
												if(xs3%4==0) {			
													cuishou = useridaa[2];						
												}
											}
											if(cuishou == useridaa[zcsr-4]) {
												xs4++;
												if(xs4%4==0) {			
													cuishou = useridaa[3];						
												}
											}
											if(cuishou == useridaa[zcsr-5]) {
												xs5++;
												if(xs5%4==0) {			
													cuishou = useridaa[4];						
												}
											}
										}
									}	
									//第二种方法：看哪个催收员工给你需要分50%的单子
									if(findStr(cuishou)) {
//										Random r = new Random();
//										int sj=r.nextInt(2);
										if(cuishou==M1) {
											suiji++;
										}
										if(suiji == 0) {
											M1 = cuishou;
											suiji++;
										}
										if(suiji%2==0) {						
										Random random1 = new Random();
										int xiabiao1= random1.nextInt(cuishouzuyqday1.length);
										 cuishou = cuishouzuyqday1[xiabiao1];						
										}
									}
									
																																								
									//获取借款表id
					 				DataRow dataRow = listyqday1.get(i);	
									//原来的催收人员
									int oldcuishou= dataRow.getInt("cuishou_id");
									 
									 int jkid = dataRow.getInt("id");
									//查询借款表的cuishou_m1
									int cuism1 = aotuZDSHALLService.getCuishouBG(jkid+"");
									
									//查询借款表的cuishouzz
									String cuishouzz = aotuZDSHALLService.getCuishouzz(jkid+""); 
																	
									//修改
									dataRow.set("id", jkid);
									dataRow.set("cuishou_id", cuishou);
									dataRow.set("cuishou_m1", cuishou);
									//过年之后更改去掉的sd_new_jkyx				
									dataRow.set("cuishouzz", cuishouzz + "," + cuishou);
									//更改sd_new_jkyx上面相关id
									//aotuZDSHALLService.updateUserFP(dataRow);
									
									//分单记录表sd_cuishou_record
									DataRow rowre = new DataRow();
									SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
									//查询借款表的userid
									String userid = aotuZDSHALLService.getUserid(jkid+""); 
									rowre.set("user_id",userid);
									rowre.set("rec_id",jkid);
									rowre.set("cmsuser_id",cuishou);
									rowre.set("create_time",df.format(new Date()));
									rowre.set("cmsuserld_id",6);
									rowre.set("oldcuishou_id",oldcuishou);
									//添加信息sd_cuishou_record
									//aotuZDSHALLService.insertCuishouRecord(rowre);
									
									
									
								//sd_new_jkyx实际借款金额sjsh_money
								String sjshjine = aotuZDSHALLService.getSJSHJE(jkid+"");
								//sd_new_jkyx逾期利息yuq_lx
								String yuqlx = aotuZDSHALLService.getYQLX(jkid+"");
								//int能不能装的数值  
								int zje = Integer.parseInt(sjshjine.replace(",", ""))+Integer.parseInt(yuqlx.replace(",", ""));
								//sd_accountuserhk催收表格，查询催收id和事件
								 DataRow datacs = aotuZDSHALLService.getCuishouBG(cuishou,time111);
								 
								//sd_cuishou_fendan 表 lin，后面的传参:m1:1,m2:2,m3:3
									List<DataRow> cuishoufendanList =aotuZDSHALLService.getcuishoufendanid(jkid,time111.substring(0, 7),1);
									int a = cuishoufendanList.size();
									 //f 分出人更新
									DataRow  dataRow1 = new DataRow();
									if (0!=a ) {
										DataRow datafendan = cuishoufendanList.get(0);   //只获取最大id，（最新的数据）
										int fendanid = datafendan.getInt("id");
										int fendancs =datafendan.getInt("fendan_cs");
										int cuishou_id= datafendan.getInt("cuishou_id");
										double cuihuijine_old = aotuZDSHALLService.getfendancuihuijine(jkid,cuishou_id,time111.substring(0, 7));  //以前催回金额
										double cuihuijinezs = aotuZDSHALLService.getrechargeMoneyAccount(jkid,cuishou_id,today.substring(3, 10));  //a总催回金额
										
										dataRow1.set("id",fendanid);
										dataRow1.set("cuishou_jine",cuihuijinezs-cuihuijine_old);
										dataRow1.set("recharge_money",cuihuijinezs-cuihuijine_old);
										logger.info("M1 更新 ：fendanid： "+fendanid);
										//aotuZDSHALLService.updatefendandata(dataRow1);
														
									}
								
																								
									//xiong添加sd_cuishou_fendan信息，金额不相加			 
									 DataRow  acf = new DataRow();
									 
									 acf.set("user_id",dataRow.getInt("userid"));
									 acf.set("jk_id",jkid);
									 acf.set("cuishou_id",cuishou);
									 acf.set("fendan_time",time111);
									 acf.set("cuishou_jine",zje);
									 acf.set("cuishou_z",1);
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
								   cc++;				
									//dataRow:借款表 。rowre：分单记录表。dataRow1：a为判断条件，分单表分出去。acf：分担表添加。datacs：sd_accountuserhk：添加和修改
									aotuZDSHALLService.FenDanShiWu(dataRow, rowre,a, dataRow1, acf, row11, datacs );
									//long jiesu=System.currentTimeMillis()-kaishi;
									
									//logger.info("结束一个分单所花的时间："+jiesu);
								}						
							}
							catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace() ;
							}
					
					
			// *******************************************m1分单
							
				//1号M1把所有7天以上的分平衡给大家, 保留7天一下的单
					// 再职催收人员m1
					

					// 查询用户借款表逾期0-15，并且不等于分配了催收人员
					List<DataRow> listyqm1 = aotuZDSHALLService.getAllYQListYQM1(cuishouzuyq);
				
					int sizeyqm1 = listyqm1.size();				
					// 查询没有请假需要分单的催收人员
					int[] cuishouzuyq1 = getUserLeavetList(1);
						
					int aa = 0;
					int bb = cuishouzuyq1.length;
					
					//查询最大催收人员催回比例是否都为0；		
					Set<Integer> setM1=cuishouIdNew(1,cuishouzuyq1);
					//判断是最大催收比例为0
					boolean cuishouset= setM1.contains(0);
					//xian pai xu
					float countaa[] = new float[cuishouzuyq1.length];
					int useridaa[] = new int[cuishouzuyq1.length];
					for(int j=0 ;j<cuishouzuyq1.length;j++){
						useridaa[j]=cuishouzuyq1[j];
						double tj_ycszjine = aotuZDSHALLService.getCSFendanjine(useridaa[j],"",time111,2,1);
						double tj_ychzjineMon = aotuZDSHALLService.getCSTJychzje(useridaa[j],"",time111,2,1);
						//tj_ychzjineMon += getBeforeMonthlastDay_chje(useridaa[j],"",time111,1,1,1);
						float chbuserl=baifenbi(tj_ychzjineMon,tj_ycszjine);
						countaa[j]=chbuserl; //总笔数
					}
					
					int nn = countaa.length;
					for (int m = 0; m < nn - 1; m++) {   
						for (int n = 0; n < nn - 1; n++) { 
							if (countaa[n] < countaa[n + 1]) { 
								float tempaa = countaa[n];   
								int tempaaa = useridaa[n]; 
								countaa[n] = countaa[n + 1];   
								useridaa[n] = useridaa[n + 1]; 
								countaa[n + 1] = tempaa;   
								useridaa[n + 1] = tempaaa;   
							}   
						}   
					}
					for (int ma = 0; ma < countaa.length; ma++) {   
						logger.info("userid:"+useridaa[ma]);
						logger.info("bl:"+countaa[ma]);
					}
					int znum =0;
					for(int i=0;i<bb;i++){
						znum += bb - i;
					}
					logger.info(znum);
					logger.info(sizeyqm1);
					//求平均数
					int avg=sizeyqm1/(bb<=0?1:bb);
					logger.info(avg);
					int midnum = bb / 2 ;
					int	avgmin=avg-5;
					int	avgmax=avg+5;
					if(avgmax<=10) {
						avgmin=avg-3;
						avgmax=avg+3;
					}
					if(avgmin<0) {
						avgmin=0;
					}
					int aaa = 0;
					int bbb = 1;
					try{
						for(int i=0; i<sizeyqm1; i++){
							long kaishi=System.currentTimeMillis();
							
							//这个没有什么作用
							Random random = new Random();
							//下面有aa++
							int xiabiao = aa%bb;
							int cuishou = cuishouzuyq1[xiabiao];
							
							
							cuishou = useridaa[aaa];
							aaa++;
							if(aaa>=useridaa.length) {
								aaa = 0;
							} 
							//判断是否是新人
							//int fdqx=aotuZDSHALLService.getfdqx(cuishou);
							//判断是否有新人
							//boolean xinrenbaohu= setM1.contains(cuishou);
							if(bbb == 1 && bb != 0) {
								if(sizeyqm1 < znum) {
									for(int k=0 ;k<useridaa.length;k++){
										//ru guo xu yao jia da ((bb-k)-midnum)*x  wei jia da de bei shu
										int zdchaju = 0;
										if(bb != 0) {
											zdchaju = (2 * ((bb-k-1)-midnum)/midnum) ;//4
										}
										int xyfendanshu = avg + zdchaju ;
										//mofa 5017 fen 50%
										if(useridaa[k] == 5017) {
											xyfendanshu = avg/2 + 1;
										}
										if(xyfendanshu<=0) {
											xyfendanshu = 1 ;
										}
										int curfendans=aotuZDSHALLService.getcuitimefendanAccount(useridaa[k],"",time111,"M1");
										if(curfendans < xyfendanshu) {
											cuishou = useridaa[k];
											break;
										}
										if(k == bb-1 && curfendans >= xyfendanshu) {
											aaa=0;
											bbb=0;
											cuishou = useridaa[aaa];
											aaa++;
										}
									}
								}else {
									for(int k=0 ;k<useridaa.length;k++){
										//ru guo xu yao jia da ((bb-k)-midnum)*x  wei jia da de bei shu
										int zdchaju = 0;
										if(bb != 0) {
											zdchaju = (3 * ((bb-k-1)-midnum)/midnum) ;//4
										}
										int xyfendanshu = avg + zdchaju ;
										//mofa 5017 fen 50%
										if(useridaa[k] == 5017) {
											xyfendanshu = avg/2 + 1;
										}
										int curfendans=aotuZDSHALLService.getcuitimefendanAccount(useridaa[k],"",time111,"M1");
										if(curfendans < xyfendanshu) {
											cuishou = useridaa[k];
											break;
										}
										if(k == bb-1 && curfendans == xyfendanshu) {
											aaa=0;
											bbb=0;
											cuishou = useridaa[aaa];
											aaa++;
										}
									}
								}
							}
							//获取催回比例最大的催收人员和判断员工是否是新人 && 2!=fdqx
							/*
							 * if(!time111.substring(7, 10).equals("-01") && cuishouset==false
							 * &&xinrenbaohu==false && curfendans>avgmin) { int
							 * cuishou0=cuishouIdMax(1,cuishouzuyq1,avgmax);
							 * cuishou=cuishou0==0?cuishou:cuishou0; }
							 */
							
							
							//第一种方法：看哪个催收员工给你需要分50%的单子
//							if(findStr(cuishou)) {					
//								if(i%2==1) {						
//								Random random1 = new Random();
//								int xiabiao1= random1.nextInt(cuishouzuyq1.length);
//								 cuishou = cuishouzuyq1[xiabiao1];						
//								}
//							}											
							
							//第二种方法：看哪个催收员工给你需要分50%的单子
//							if(findStr(cuishou)) {
//								Random r = new Random();
//								int sj=r.nextInt(2);
//								suiji++;
//								if(suiji%2==0) {						
//								Random random1 = new Random();
//								int xiabiao1= random1.nextInt(cuishouzuyq1.length);
//								 cuishou = cuishouzuyq1[xiabiao1];						
//								}
//							}
							
							
															
							//获取借款表id
			 				DataRow dataRow = listyqm1.get(i);	
							//原来的催收人员
							int oldcuishou= dataRow.getInt("cuishou_id");
							 
							 int jkid = dataRow.getInt("id");
							//查询借款表的cuishou_m1
							int cuism1 = aotuZDSHALLService.getCuishouBG(jkid+"");
							
							//查询借款表的cuishouzz
							String cuishouzz = aotuZDSHALLService.getCuishouzz(jkid+""); 

							int ylcuishoum1state = aotuZDSHALLService.getOLDfdqx(cuism1);
							if(ylcuishoum1state == 1) {
								cuishou = cuism1;
							}
							//查询cuishou_m1是不是在职
//							int cuism1state = aotuZDSHALLService.getCuishouBGState(cuism1);
							
							//判断这个订单是否存在以及是否是组
//							if(cuism1 != 0 && cuism1state == 1 && cuism1 != 5012 && cuism1 != 5017){
//								cuishou = cuism1 ;
//							}
							
							//修改
							dataRow.set("id", jkid);
							dataRow.set("cuishou_id", cuishou);
							dataRow.set("cuishou_m1", cuishou);
							//过年之后更改去掉的sd_new_jkyx				
							dataRow.set("cuishouzz", cuishouzz + "," + cuishou);
							//更改sd_new_jkyx上面相关id
							//aotuZDSHALLService.updateUserFP(dataRow);
							
							//分单记录表sd_cuishou_record
							DataRow rowre = new DataRow();
							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
							//查询借款表的userid
							String userid = aotuZDSHALLService.getUserid(jkid+""); 
							rowre.set("user_id",userid);
							rowre.set("rec_id",jkid);
							rowre.set("cmsuser_id",cuishou);
							rowre.set("create_time",df.format(new Date()));
							rowre.set("cmsuserld_id",6);
							rowre.set("oldcuishou_id",oldcuishou);
							//添加信息sd_cuishou_record
							//aotuZDSHALLService.insertCuishouRecord(rowre);
							
							
							
						//sd_new_jkyx实际借款金额sjsh_money
						String sjshjine = aotuZDSHALLService.getSJSHJE(jkid+"");
						//sd_new_jkyx逾期利息yuq_lx
						String yuqlx = aotuZDSHALLService.getYQLX(jkid+"");
						//int能不能装的数值  
						int zje = Integer.parseInt(sjshjine.replace(",", ""))+Integer.parseInt(yuqlx.replace(",", ""));
						//sd_accountuserhk催收表格，查询催收id和事件
						 DataRow datacs = aotuZDSHALLService.getCuishouBG(cuishou,time111);
						 
						//sd_cuishou_fendan 表 lin，后面的传参:m1:1,m2:2,m3:3
							List<DataRow> cuishoufendanList =aotuZDSHALLService.getcuishoufendanid(jkid,time111.substring(0, 7),1);
							int a = cuishoufendanList.size();
							 //f 分出人更新
							DataRow  dataRow1 = new DataRow();
							if (0!=a ) {
								DataRow datafendan = cuishoufendanList.get(0);   //只获取最大id，（最新的数据）
								int fendanid = datafendan.getInt("id");
								int fendancs =datafendan.getInt("fendan_cs");
								int cuishou_id= datafendan.getInt("cuishou_id");
								double cuihuijine_old = aotuZDSHALLService.getfendancuihuijine(jkid,cuishou_id,time111.substring(0, 7));  //以前催回金额
								double cuihuijinezs = aotuZDSHALLService.getrechargeMoneyAccount(jkid,cuishou_id,today.substring(3, 10));  //a总催回金额
								
								dataRow1.set("id",fendanid);
								dataRow1.set("cuishou_jine",cuihuijinezs-cuihuijine_old);
								dataRow1.set("recharge_money",cuihuijinezs-cuihuijine_old);
								logger.info("M1 更新 ：fendanid： "+fendanid);
								//aotuZDSHALLService.updatefendandata(dataRow1);
												
							}
						
							
							
							
							
							
							//xiong添加sd_cuishou_fendan信息，金额不相加			 
							 DataRow  acf = new DataRow();
							 
							 acf.set("user_id",dataRow.getInt("userid"));
							 acf.set("jk_id",jkid);
							 acf.set("cuishou_id",cuishou);
							 acf.set("fendan_time",time111);
							 acf.set("cuishou_jine",zje);
							 acf.set("cuishou_z",1);
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
							aotuZDSHALLService.FenDanShiWu(dataRow, rowre,a, dataRow1, acf, row11, datacs );
							long jiesu=System.currentTimeMillis()-kaishi;
							
							logger.info("结束一个分单所花的时间："+jiesu);
						}
						
						
					}
					catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace() ;
					}
					
					
				//*****************************m2分单
					//查询在职的催收人员
					List<DataRow> cuishoum2 = aotuZDSHALLService.getAllcuishouM2();
					int cuishouzuyqm2[] = new int[cuishoum2.size()];
					
					for (int m = 0; m < cuishoum2.size(); m++) {
						DataRow row = cuishoum2.get(m);
						cuishouzuyqm2[m] = row.getInt("user_id");
						 int num =m+1;
						 logger.info("第"+num+"催收M2人：" +cuishouzuyqm2[m]);
					}
					
					List<DataRow> listyqm2 = aotuZDSHALLService.getAllYQListYQM2(cuishouzuyqm2);
					//1统计催收人员剩余催收单数   lin 2019-7-8   M2
					if(time111.substring(7,10).equals("-01")) {
						everyMoncuishoudantongji(cuishouzuyqm2,2);
						//z在1号增加8-15的催收单
						List<DataRow> listyqm2_t = aotuZDSHALLService.getAllYQListYQM2_T();
						listyqm2.addAll(listyqm2_t);
					}
					
					int sizeyqm2 = listyqm2.size();
						
					
					//查询没有请假需要分单的催收人员	
					int[] cuishouzuyq2=getUserLeavetList(2);
					
					int aaM2 = 0;
					int suiji2 = 0;
					int M2 = 0;
					int bbM2 = cuishouzuyq2.length;
											
					try{
						for(int i=0; i<sizeyqm2; i++){
							Random random = new Random();
							
							//下面有aa++
							int xiabiao = aaM2%bbM2;
							int cuishou = cuishouzuyq2[xiabiao];
							
							
							//看哪个催收员工给你需要分50%的单子
							if(findStr(cuishou)) {
//								Random r = new Random();
//								int sj=r.nextInt(2);
								if(cuishou==M2) {
									suiji2++;
								}
								if(suiji2 == 0) {
									M2 = cuishou;
									suiji2++;
								}
								if(suiji2%2==0) {						
								Random random2 = new Random();
								int xiabiao2= random2.nextInt(cuishouzuyq2.length);
								 cuishou = cuishouzuyq2[xiabiao2];						
								}
							}
							
							
							
							//看哪个催收员工给你需要分50%的单子
//							if(findStr(cuishou)) {
//								Random r = new Random();
//								int sj=r.nextInt(2);
//								if(sj==1) {						
//								Random random2 = new Random();
//								int xiabiao2= random2.nextInt(cuishouzuyq2.length);
//								 cuishou = cuishouzuyq2[xiabiao2];						
//								}
//							}
							
																							
							DataRow dataRow = listyqm2.get(i);
							
							//原来的催收人员
							int oldcuishou= dataRow.getInt("cuishou_id");
							
							int jkid = dataRow.getInt("id");
							//xiong-查询借款表的cuishouzz
							String cuishouzz = aotuZDSHALLService.getCuishouzz(jkid+""); 
							dataRow.set("cuishouzz", cuishouzz + "," + cuishou);		
							dataRow.set("id", jkid);
							dataRow.set("cuishou_id", cuishou);
							dataRow.set("cuishou_m2", cuishou);
							//aotuZDSHALLService.updateUserFP(dataRow);
							
							
							
							
							//添加分单记录表
							DataRow rowre = new DataRow();
							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
							//查询借款表的userid
							String userid = aotuZDSHALLService.getUserid(jkid+""); 
							rowre.set("user_id",userid);
							rowre.set("rec_id",jkid);
							rowre.set("cmsuser_id",cuishou);
							rowre.set("create_time",df.format(new Date()));
							//自动分单to
							rowre.set("cmsuserld_id",6);
							rowre.set("oldcuishou_id",oldcuishou);
							//添加信息sd_cuishou_record
							//aotuZDSHALLService.insertCuishouRecord(rowre);
							
							String sjshjine = aotuZDSHALLService.getSJSHJE(jkid+"");
							String yuqlx = aotuZDSHALLService.getYQLX(jkid+"");
								  
						   int zje = Integer.parseInt(sjshjine.replace(",", ""))+Integer.parseInt(yuqlx.replace(",", ""));
						   DataRow datacs = aotuZDSHALLService.getCuishouBG(cuishou,time111);
						   
						 //sd_cuishou_fendan 表 lin
							List<DataRow> cuishoufendanList =aotuZDSHALLService.getcuishoufendanid(jkid,time111.substring(0, 7),2);
							int a = cuishoufendanList.size();				
							//f 分出人更新
							DataRow  dataRow1 = new DataRow();
							if (0!=a ) {
								DataRow datafendan = cuishoufendanList.get(0);   //只获取最大id，（最新的数据）
								int fendanid = datafendan.getInt("id");
								int fendancs =datafendan.getInt("fendan_cs");
								int cuishou_id= datafendan.getInt("cuishou_id");
								double cuihuijine_old = aotuZDSHALLService.getfendancuihuijine(jkid,cuishou_id,time111.substring(0, 7));  //以前催回金额
								double cuihuijinezs = aotuZDSHALLService.getrechargeMoneyAccount(jkid,cuishou_id, today.substring(3, 10));  //a总催回金额
								 
								dataRow1.set("id",fendanid);
								dataRow1.set("cuishou_jine",cuihuijinezs-cuihuijine_old);
								dataRow1.set("recharge_money",cuihuijinezs-cuihuijine_old);
								logger.info("M2 更新 ：fendanid： "+fendanid);
								//aotuZDSHALLService.updatefendandata(dataRow1);
												
							}
							//xiong添加sd_cuishou_fendan信息，金额不相加			 
							 DataRow  acf = new DataRow();
							 
							 acf.set("user_id",dataRow.getInt("userid"));
							 acf.set("jk_id",jkid);
							 acf.set("cuishou_id",cuishou);
							 acf.set("fendan_time",time111);
							 acf.set("cuishou_jine",zje);
							 acf.set("cuishou_z",2);
							 //acf.set("fendan_cs",fendancs);
							 acf.set("recharge_money",0);
							 //aotuZDSHALLService.insertSdCuishouFendan(acf);		
							 
							 DataRow row11 = new DataRow();
						   if(datacs == null){
							   
							  
							   row11.set("csid", cuishou);
							   row11.set("totaljine", zje);
							   row11.set("time", time111);
							   //aotuZDSHALLService.insertCuiBG(row11);
							   
						   }else{
							   double ysje = datacs.getDouble("totaljine");
							   int cuiid = datacs.getInt("id");
							  // DataRow row11 = new DataRow();
							   row11.set("id", cuiid);
							   row11.set("totaljine", ysje+zje);
							   //aotuZDSHALLService.updateCuiBG(row11);				   				  				
						   }
						   aaM2++;
						   
						 //dataRow:借款表 。rowre：分单记录表。dataRow1：a为判断条件，分单表分出去。acf：分担表添加。datacs：sd_accountuserhk：添加和修改
						aotuZDSHALLService.FenDanShiWu(dataRow, rowre,a, dataRow1, acf, row11, datacs );	 				
						}
						  
					}
					catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace() ;
					}
					
					
			//*****************************m3分单
					List<DataRow> cuishoum3 = aotuZDSHALLService.getAllcuishouM3();
					int cuishouzuyqm3[] = new int[cuishoum3.size()];
					
					for (int m = 0; m < cuishoum3.size(); m++) {
						DataRow row = cuishoum3.get(m);
						cuishouzuyqm3[m] = row.getInt("user_id");
						 int num =m+1;
						logger.info("第"+num+"催收M3人："+cuishouzuyqm3[m]);
					}
							
					
					
					List<DataRow> listyqm3 = aotuZDSHALLService.getAllYQListYQM3(cuishouzuyqm3);
					//1统计催收人员剩余催收单数   lin 2019-7-8   M3
					if(time111.substring(7,10).equals("-01")) {
						everyMoncuishoudantongji(cuishouzuyqm3,3);
						//z在1号增加45的催收单
						List<DataRow> listyqm3_t = aotuZDSHALLService.getAllYQListYQM3_T();
						listyqm3.addAll(listyqm3_t);
					}
					int sizeyqm3 = listyqm3.size();
									
					//查询没有请假需要分单的催收人员	
					int[] cuishouzuyq3=getUserLeavetList(3);			
					
					int aaM3 = 0;
					int suiji3 = 0;
					int M3 = 0;
					int bbM3 = cuishouzuyq3.length;
					try{
						for(int i=0; i<sizeyqm3; i++){
							Random random = new Random();
							//下面有aa++
							int xiabiao = 0;
							if(bbM3!=0) {
								xiabiao = aaM3%bbM3;
							}
							int cuishou = 0;
							if(cuishouzuyq3.length!=0) {
								cuishou = cuishouzuyq3[xiabiao];
							}		
							
							if(findStr(cuishou)) {
//								Random r = new Random();
//								int sj=r.nextInt(2);
								if(cuishou==M3) {
									suiji3++;
								}
								if(suiji3 == 0) {
									M3 = cuishou;
									suiji3++;
								}
								if(suiji3%2==0) {						
								Random random3 = new Random();
								int xiabiao3= random3.nextInt(cuishouzuyq3.length);
								 cuishou = cuishouzuyq3[xiabiao3];	
								 
								}
							}
							
							
							//F458的组长只分别人50%的单子和mofa5017分50%
//							if(5039==cuishou) {
//								Random r = new Random();
//								int sj=r.nextInt(3);
//								if(sj==1||sj==2) {						
//								Random random3 = new Random();
//								int xiabiao3= random3.nextInt(cuishouzuyq3.length);
//								 cuishou = cuishouzuyq3[xiabiao3];						
//								}
//							}
							
							DataRow dataRow = listyqm3.get(i);
							int jkid = dataRow.getInt("id");
							
							//原来的催收人员
							int oldcuishou= dataRow.getInt("cuishou_id");
							//xiong-查询借款表的cuishouzz
							String cuishouzz = aotuZDSHALLService.getCuishouzz(jkid+""); 
							dataRow.set("cuishouzz", cuishouzz + "," + cuishou);
							dataRow.set("id", jkid);
							dataRow.set("cuishou_id", cuishou);
							dataRow.set("cuishou_m3", cuishou);
							//aotuZDSHALLService.updateUserFP(dataRow);
							
							//添加分单记录表
							DataRow rowre = new DataRow();
							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
							//查询借款表的userid
							String userid = aotuZDSHALLService.getUserid(jkid+""); 
							rowre.set("user_id",userid);
							rowre.set("rec_id",jkid);
							rowre.set("cmsuser_id",cuishou);
							rowre.set("create_time",df.format(new Date()));
							rowre.set("cmsuserld_id",6);
							rowre.set("oldcuishou_id",oldcuishou);
							//添加信息sd_cuishou_record
							//aotuZDSHALLService.insertCuishouRecord(rowre);
							
							String sjshjine = aotuZDSHALLService.getSJSHJE(jkid+"");
							String yuqlx = aotuZDSHALLService.getYQLX(jkid+"");
							
							int zje = Integer.parseInt(sjshjine.replace(",", ""))+Integer.parseInt(yuqlx.replace(",", ""));
							DataRow datacs = aotuZDSHALLService.getCuishouBG(cuishou,time111);
							
							//sd_cuishou_fendan 表 lin
							List<DataRow> cuishoufendanList =aotuZDSHALLService.getcuishoufendanid(jkid,time111.substring(0, 7),3);
							int a = cuishoufendanList.size();
							
							 //f 分出人更新
							DataRow  dataRow1 = new DataRow();
							if (0!=a ) {
								DataRow datafendan = cuishoufendanList.get(0);   //只获取最大id，（最新的数据）
								int fendanid = datafendan.getInt("id");
								int fendancs =datafendan.getInt("fendan_cs");
								int cuishou_id= datafendan.getInt("cuishou_id");
								double cuihuijine_old = aotuZDSHALLService.getfendancuihuijine(jkid,cuishou_id,time111.substring(0, 7));  //以前催回金额
								double cuihuijinezs = aotuZDSHALLService.getrechargeMoneyAccount(jkid,cuishou_id,today.substring(3, 10));  //a总催回金额
								
								dataRow1.set("id",fendanid);
								dataRow1.set("cuishou_jine",cuihuijinezs-cuihuijine_old);
								dataRow1.set("recharge_money",cuihuijinezs-cuihuijine_old);
								logger.info("M3 更新 ：fendanid： "+fendanid);
								//aotuZDSHALLService.updatefendandata(dataRow1);
											
							}
							//xiong添加sd_cuishou_fendan信息，金额不相加			 
							 DataRow  acf = new DataRow();
							 
							 acf.set("user_id",dataRow.getInt("userid"));
							 acf.set("jk_id",jkid);
							 acf.set("cuishou_id",cuishou);
							 acf.set("fendan_time",time111);
							 acf.set("cuishou_jine",zje);
							 acf.set("cuishou_z",3);
							// acf.set("fendan_cs",fendancs);
							 acf.set("recharge_money",0);
							 //aotuZDSHALLService.insertSdCuishouFendan(acf);
							
							
							 
								DataRow row11 = new DataRow();
							if(datacs == null){
								
							
								row11.set("csid", cuishou);
								row11.set("totaljine", zje);
								row11.set("time", time111);
								//aotuZDSHALLService.insertCuiBG(row11);
								
							}else{
								double ysje = datacs.getDouble("totaljine");
								int cuiid = datacs.getInt("id");
								//DataRow row11 = new DataRow();
								row11.set("id", cuiid);
								row11.set("totaljine", ysje+zje);
								//aotuZDSHALLService.updateCuiBG(row11);										
							}
							aaM3++;
							
							//dataRow:借款表 。rowre：分单记录表。dataRow1：a为判断条件，分单表分出去。acf：分担表添加。datacs：sd_accountuserhk：添加和修改
							aotuZDSHALLService.FenDanShiWu(dataRow, rowre,a, dataRow1, acf, row11, datacs );
						}
					}
					catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace() ;
					}

	}
	
		
	//xiong-20190803判断催收人员是否是只需要分50%单的人
	private boolean findStr(int str) {
		// 需要分配50%的催收人员
		
		String cs_fd50= aotuZDSHALLService.getcuishou_50Needfendan();
		String[] a = cs_fd50.split(",");
		//int[] a = { 5017,50250,50251,50220,50223  };

		boolean result = false;

		for (String s : a) {
			if (s.equals(str+"")) {
				return true;
			}
		}
		return result;
	}
	
	
					
	//xiong-20190803-不用分单的催收人员
	
	private int[] getUserLeavetList(int a) {

		// q催收不要分单的名单人员名单
		//String bufendansql = " AND  user_id<>5002  AND user_id<>5020  AND user_id<>5089 and user_id<>50236";
		String cs_nnfd= aotuZDSHALLService.getcuishou_NoNeedfendan();
		String[] csnnfd_z = cs_nnfd.split(",");
		String bufendansql ="";
		for(String csmd:csnnfd_z) {
			bufendansql+=" and user_id<>"+csmd;
		}
		
		// 查询没有请假需要分单的催收人员
		List<DataRow>  UserLeavet= aotuZDSHALLService.getUserLeavetList(a, bufendansql);
				
								
		//催收人员的数量
		int[] UserLeavetnum = new int[UserLeavet.size()];
				
		//催收人员的的id数组
		for (int m = 0; m < UserLeavet.size(); m++) {
			DataRow row = UserLeavet.get(m);
			UserLeavetnum[m] = row.getInt("user_id");
		}
		for(int mmm = 0;mmm<UserLeavetnum.length;mmm++){
			int num=mmm+1;
			if(0==a) {			
				logger.info("没请假"+num+"位催收M0的人：" + UserLeavetnum[mmm]);
			}else if(1==a) {			
				logger.info("没请假"+num+"位催收M1的人：" + UserLeavetnum[mmm]);
			}else if(2==a) {
				logger.info("没请假"+num+"位催收M2的人：" + UserLeavetnum[mmm]);
			}else if(3==a) {
				logger.info("没请假"+num+"位催收M3的人：" + UserLeavetnum[mmm]);
			}else if(123==a) {
				logger.info("没请假"+num+"位催收M123的人：" + UserLeavetnum[mmm]);
			}else {
				logger.info("没请假"+num+"位异常催收的人：" + UserLeavetnum[mmm]);	
			}					
		}										
		return UserLeavetnum;
				
	}
	

			
	/**
	 * lin 2019-7-8 
	 * 在每月的1号统计催收人员手上的余留的催收单
	 */
	 private void everyMoncuishoudantongji(int cuishouzuyuan[],int cuishouzu) {
	      logger.info("进入催收单统计分配方法 everyMoncuishoudantongji");

			SimpleDateFormat sdfyMd = new SimpleDateFormat("yyyy-MM-dd");
			Date timeDate = new Date();
			String endDate =sdfyMd.format(timeDate);
			
			Calendar cd = Calendar.getInstance();
			cd.setTime(timeDate);
			cd.add(Calendar.DATE, -1);
			String lastDay = sdfyMd.format(cd.getTime());
			
			if(endDate.substring(7,10).equals("-01")) {   //判断是否是1号
				
				for (int j = 0; j < cuishouzuyuan.length; j++) {
					logger.info("统计分配催收员："+cuishouzuyuan[j]);
					int fendancs =2;  //第几次分单
					String Strcuishou_z ="m"+cuishouzu;
					
					// m1 时，1--7天的单
					List<DataRow> datarow_whk = aotuZDSHALLService.getwcuihuijkDataRows(Strcuishou_z,cuishouzuyuan[j]); 
					//List<DataRow> datarow_yhk = aotuZDSHALLService.getycuihuidanDataRows(cuishouzuyuan[j] ,"",endDate,2) ;
					
					//M1增加上个月最后一天的催回金额
					//if(cuishouzu==1) {
					//	List<DataRow> datarow_yhklast = aotuZDSHALLService.getycuihuidanDataRows(cuishouzuyuan[j] ,"",lastDay,1) ;
					//	datarow_yhk.addAll(datarow_yhklast);
					//}
					
					   
			        //已还款金额
			        /*for (int i = 0; i < datarow_yhk.size(); i++) {
						DataRow  datajkDataRow = datarow_yhk.get(i);
						//1号前入催金额
					   int jkid = datajkDataRow.getInt("id");
					   int user_id = datajkDataRow.getInt("userid");
					   String  sjsh_money =datajkDataRow.getString("sjsh_money");
					   String  yuq_lx =datajkDataRow.getString("yuq_lx");
					   int ycsjine = Integer.parseInt(sjsh_money.replace(",", ""))+Integer.parseInt(yuq_lx.replace(",", ""));
					   
					   DataRow  dataRow = new DataRow();
					   dataRow.set("user_id",user_id);
					   dataRow.set("jk_id",jkid);
					   dataRow.set("cuishou_id",cuishouzuyuan[j]);
					   dataRow.set("fendan_time",endDate);
					   dataRow.set("cuishou_jine",ycsjine);
					   dataRow.set("cuishou_z",cuishouz);
					   dataRow.set("fendan_cs",fendancs);
					   aotuZDSHALLService.insertSdCuishouFendan(dataRow);
					    
				   }*/
			        
					//未还款金额
			        for (int i = 0; i < datarow_whk.size(); i++) {
						DataRow  datajkDataRow = datarow_whk.get(i);
						//1号前入催金额
						
					   int jkid = datajkDataRow.getInt("id");
					   int user_id = datajkDataRow.getInt("userid");
					   String  sjsh_money =datajkDataRow.getString("sjsh_money");
					   String  yuq_lx =datajkDataRow.getString("yuq_lx");
					   int ycsjine = Integer.parseInt(sjsh_money.replace(",", ""))+Integer.parseInt(yuq_lx.replace(",", ""));
					   
					   
					   DataRow  dataRow = new DataRow();
					   dataRow.set("user_id",user_id);
					   dataRow.set("jk_id",jkid);
					   dataRow.set("cuishou_id",cuishouzuyuan[j]);
					   dataRow.set("fendan_time",endDate);
					   dataRow.set("cuishou_jine",ycsjine);
					   dataRow.set("cuishou_z",cuishouzu);
					   dataRow.set("fendan_cs",fendancs);
					   aotuZDSHALLService.insertSdCuishouFendan(dataRow);
			        }
								        
				}
				
			}
		}
	 
	 
		/**
		 * xiong 百分比换算
		 * @return
		 * @throws java.text.ParseException 
		 */	
		public float baifenbi(double a,double b) {
			
			if(a==0||b==0) {
				return 0F;
			}			 		
			// 创建一个数值格式化对象   
		     NumberFormat numberFormat = NumberFormat.getInstance();   
		     // 设置精确到小数点后2位   
		     numberFormat.setMaximumFractionDigits(2);
		     
		     String result = numberFormat.format((float)a/(float)b*100);
		     				
		     return Float.parseFloat(result);
		    // return result+"%";
		}
	 
	 //查询催回比例最大的催收人员
		public  int  cuishouIdMax(int cuishouz,int[] cuishouzum,int avgmax){
		
		    //g对时间进行处理
			SimpleDateFormat sdfyMd = new SimpleDateFormat("yyyy-MM-dd");
			String endDate  =sdfyMd.format(new Date());	
			String startDate="";
		/*
		 * //参考前面5天 Calendar calendar = Calendar.getInstance();//日历对象
		 * calendar.setTime(new Date());//设置当前日期 calendar.add(Calendar.DATE,-2);
		 * //-3天的天数 String nextDay = sdfyMd.format(calendar.getTime());
		 * 
		 * String startDate=nextDay;
		 */									
			//查询今天要分单的催收人员id		
			//int[] cuishouzum1 = getUserLeavetList(cuishouz);						
			float biliMax=0F;
			int cuishouIdMax=0;
			//查询今天分单催收人员以前的催收比例
			for(int i =0;i<cuishouzum.length;i++) {
				int cuishouid=cuishouzum[i];
				double tj_ycszjine = aotuZDSHALLService.getCSFendanjine(cuishouid,startDate,endDate,2,cuishouz);
				double tj_ychzjineMon = aotuZDSHALLService.getCSTJychzje(cuishouid,startDate,endDate,2,cuishouz);
				//tj_ychzjineMon += getBeforeMonthlastDay_chje(cuishouid,startDate,endDate,1,cuishouz,1);
				//查询每个催收员工今天分多少单
				int curfendans=aotuZDSHALLService.getcuitimefendanAccount(cuishouid,"",endDate,"M1");
				float chbuserl=baifenbi(tj_ychzjineMon,tj_ycszjine);
				if(chbuserl>biliMax && curfendans<=avgmax) {
					biliMax=chbuserl;
					cuishouIdMax=cuishouid;
				}				
			}			
			return cuishouIdMax;
		}
		
		
		
		 //查询催回比例最大的催收人员和新人是否都为0
		public  Set<Integer>  cuishouIdNew(int cuishouz,int[] cuishouzum){
		
		    //g对时间进行处理
			SimpleDateFormat sdfyMd = new SimpleDateFormat("yyyy-MM-dd");
			String endDate  =sdfyMd.format(new Date());
			String startDate="";									
			//查询今天要分单的催收人员id		
			//int[] cuishouzum1 = getUserLeavetList(cuishouz);	
			
			Set<Integer> setcuishouid= new HashSet<Integer>();
			float biliMax=0F;
			int cuishouIdMax=0;
			//查询今天分单催收人员以前的催收比例
			for(int i =0;i<cuishouzum.length;i++) {
				int cuishouid=cuishouzum[i];
				double tj_ycszjine = aotuZDSHALLService.getCSFendanjine(cuishouid,startDate,endDate,2,cuishouz);
				double tj_ychzjineMon = aotuZDSHALLService.getCSTJychzje(cuishouid,startDate,endDate,2,cuishouz);				
				//tj_ychzjineMon +=  getBeforeMonthlastDay_chje(cuishouid,startDate,endDate,1,cuishouz,1);//上个月最后一天催回金额
				float chbuserl=baifenbi(tj_ychzjineMon,tj_ycszjine);
				if(chbuserl>biliMax) {
					biliMax=chbuserl;
					cuishouIdMax=cuishouid;
				}				
				if(0F==chbuserl) {
					setcuishouid.add(cuishouid);
				}
			}
			
			int biliqz=(int)biliMax;
			setcuishouid.add(biliqz);
			
			for(Integer set :setcuishouid) {
				logger.info("分单新人"+set);
			}
//			int[] cuishouMax= {biliqz,cuishouIdMax};			
			return setcuishouid;
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
//			public double getBeforeMonthlastDay_chje(int userid ,String startTime,String endtTime,int MonDayType,int cuishouz,int return_type)  {
//				double re_number =0;
//				
//				String setMinTime ="2019-08-31";  //a从九月份开始
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//				Calendar cdar = Calendar.getInstance();
//				try {
//					cdar.setTime(sdf.parse(setMinTime));
//				} catch (java.text.ParseException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//				long mintime = cdar.getTimeInMillis();
//				
//				try {
//					cdar.setTime(sdf.parse(endtTime));
//				} catch (java.text.ParseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				long endtime=cdar.getTimeInMillis();
//				
//				if(endtime > mintime && cuishouz ==1) {
//					cdar.add(Calendar.MONTH, -1);	
//					cdar.set(cdar.DAY_OF_MONTH, cdar.getActualMaximum(Calendar.DAY_OF_MONTH));
//					String gtimelast = sdf.format(cdar.getTime()); 
//					if(1==return_type) {
//						re_number = aotuZDSHALLService.getCSTJychzje(userid,startTime,gtimelast,MonDayType,cuishouz);//， 当日已催回总金额
//					}else if(2==return_type) {
//						re_number = aotuZDSHALLService.getCSTJychds1(userid,startTime,gtimelast,MonDayType,cuishouz);//， 当日已催回笔数
//					}
//				     
//				}
//				return re_number;
//			}
		
		
	 
	 public static void main(String[] args) {
		 Fenpeicuishou fc = new Fenpeicuishou();
		             fc.execute();
	     
	   }
}

