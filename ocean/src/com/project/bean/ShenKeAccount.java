package com.project.bean;

/**
 * 审核统计实体类
 * @author Administrator
 *
 */
public class ShenKeAccount {
	Integer userid;   //审核人编号
	String userName;  //审核人姓名
	String phone;     //手机号码
	String kfcxcs;     //手机号码
	int h5twotgfk;     //h5进来的
	int apptwotgfk;    //app进来的
	
	public int getH5twotgfk() {
		return h5twotgfk;
	}
	public void setH5twotgfk(int h5twotgfk) {
		this.h5twotgfk = h5twotgfk;
	}
	public int getApptwotgfk() {
		return apptwotgfk;
	}
	public void setApptwotgfk(int apptwotgfk) {
		this.apptwotgfk = apptwotgfk;
	}
	public String getKfcxcs() {
		return kfcxcs;
	}
	public void setKfcxcs(String kfcxcs) {
		this.kfcxcs = kfcxcs;
	}
	int nowCount;	  //当天总笔数
	int nowCountsjrs;	  //当天总笔数
	public int getNowCountsjrs() {
		return nowCountsjrs;
	}
	public void setNowCountsjrs(int nowCountsjrs) {
		this.nowCountsjrs = nowCountsjrs;
	}
	public int getCountsjrs() {
		return countsjrs;
	}
	public void setCountsjrs(int countsjrs) {
		this.countsjrs = countsjrs;
	}
	int jrnowOnesh;	  //当天一审笔数
	public int getJrnowOnesh() {
		return jrnowOnesh;
	}
	public void setJrnowOnesh(int jrnowOnesh) {
		this.jrnowOnesh = jrnowOnesh;
	}
	int nowOnesh;	  //当天一审笔数
	int nowTwosh;	//当天二审笔数
	int nowThreesh;	//当天三审笔数
	int nowCountSB;	  //失败当天总笔数
	int nowOneshSB;	  //失败当天一审笔数
	int nowOneshSBXXCW;	  //失败当天一审笔数
	int nowTwoshSB;	//失败当天二审笔数
	public int getNowCountSB() {
		return nowCountSB;
	}
	public void setNowCountSB(int nowCountSB) {
		this.nowCountSB = nowCountSB;
	}
	public int getNowOneshSB() {
		return nowOneshSB;
	}
	public void setNowOneshSB(int nowOneshSB) {
		this.nowOneshSB = nowOneshSB;
	}
	public int getNowTwoshSB() {
		return nowTwoshSB;
	}
	public void setNowTwoshSB(int nowTwoshSB) {
		this.nowTwoshSB = nowTwoshSB;
	}
	public int getNowThreeshSB() {
		return nowThreeshSB;
	}
	public void setNowThreeshSB(int nowThreeshSB) {
		this.nowThreeshSB = nowThreeshSB;
	}
	public int getCountSB() {
		return countSB;
	}
	public void setCountSB(int countSB) {
		this.countSB = countSB;
	}
	public int getOneSB() {
		return oneSB;
	}
	public void setOneSB(int oneSB) {
		this.oneSB = oneSB;
	}
	public int getTwoSB() {
		return twoSB;
	}
	public void setTwoSB(int twoSB) {
		this.twoSB = twoSB;
	}
	public int getThreeSB() {
		return threeSB;
	}
	public void setThreeSB(int threeSB) {
		this.threeSB = threeSB;
	}
	int nowThreeshSB;	//失败当天三审笔数
	int nowFoursh; //当天四审笔数
	int count; //总笔数
	int countSS; //总笔数
	int countsjrs; //总笔数
	int jrcount; //总笔数
	int mqdtxzs; //总笔数
	int dtzbs; //总笔数
	int dtddh; //总笔数
	public int getDtddh() {
		return dtddh;
	}
	public void setDtddh(int dtddh) {
		this.dtddh = dtddh;
	}
	public int getMqdtxzs() {
		return mqdtxzs;
	}
	public void setMqdtxzs(int mqdtxzs) {
		this.mqdtxzs = mqdtxzs;
	}
	public int getDtzbs() {
		return dtzbs;
	}
	public void setDtzbs(int dtzbs) {
		this.dtzbs = dtzbs;
	}
	int jrycscount; //总笔数
	public int getJrycscount() {
		return jrycscount;
	}
	public void setJrycscount(int jrycscount) {
		this.jrycscount = jrycscount;
	}
	public int getJrcount() {
		return jrcount;
	}
	public void setJrcount(int jrcount) {
		this.jrcount = jrcount;
	}
	int one;   //一审笔数
	int oneSS;   //一审笔数
	public int getCountSS() {
		return countSS;
	}
	public void setCountSS(int countSS) {
		this.countSS = countSS;
	}
	public int getOneSS() {
		return oneSS;
	}
	public void setOneSS(int oneSS) {
		this.oneSS = oneSS;
	}
	int two;	//二审笔数
	int three; //三审笔数
	int countSB; //失败总笔数
	int countyq; //失败总笔数
	public int getCountyq() {
		return countyq;
	}
	public void setCountyq(int countyq) {
		this.countyq = countyq;
	}
	int oneSB;   //失败一审笔数
	int oneSBXXCW;   //失败一审笔数
	public int getNowOneshSBXXCW() {
		return nowOneshSBXXCW;
	}
	public void setNowOneshSBXXCW(int nowOneshSBXXCW) {
		this.nowOneshSBXXCW = nowOneshSBXXCW;
	}
	public int getOneSBXXCW() {
		return oneSBXXCW;
	}
	public void setOneSBXXCW(int oneSBXXCW) {
		this.oneSBXXCW = oneSBXXCW;
	}
	int twoSB;	//失败二审笔数
	int threeSB; //失败三审笔数
	int threeTG; //三审通过笔数
	int nowthreeTG; //三审通过笔数
	public int getNowthreeTG() {
		return nowthreeTG;
	}
	public void setNowthreeTG(int nowthreeTG) {
		this.nowthreeTG = nowthreeTG;
	}
	int twotgfk; //三审通过笔数
	int nowtwotgfk; //三审通过笔数
	public int getNowtwotgfk() {
		return nowtwotgfk;
	}
	public void setNowtwotgfk(int nowtwotgfk) {
		this.nowtwotgfk = nowtwotgfk;
	}
	int twotgzhk; //三审通过笔数
	public int getTwotgzhk() {
		return twotgzhk;
	}
	public void setTwotgzhk(int twotgzhk) {
		this.twotgzhk = twotgzhk;
	}
	int twotgfkyqwh; //三审通过笔数
	public int getTwotgfk() {
		return twotgfk;
	}
	public void setTwotgfk(int twotgfk) {
		this.twotgfk = twotgfk;
	}
	public int getTwotgfkyqwh() {
		return twotgfkyqwh;
	}
	public void setTwotgfkyqwh(int twotgfkyqwh) {
		this.twotgfkyqwh = twotgfkyqwh;
	}
	String onemoney;
	String twomoney;
	String threemoney;
	String threeTGmoney;
	public String getOnemoney() {
		return onemoney;
	}
	public void setOnemoney(String onemoney) {
		this.onemoney = onemoney;
	}
	public String getTwomoney() {
		return twomoney;
	}
	public void setTwomoney(String twomoney) {
		this.twomoney = twomoney;
	}
	public String getThreemoney() {
		return threemoney;
	}
	public void setThreemoney(String threemoney) {
		this.threemoney = threemoney;
	}
	public String getThreeTGmoney() {
		return threeTGmoney;
	}
	public void setThreeTGmoney(String threeTGmoney) {
		this.threeTGmoney = threeTGmoney;
	}
	String totalCSmoney;//总催收
	String totalCSmoneySS;//总催收
	String totalCSmoney1;//总催收
	String jrtotalCSmoney;//总催收
	String totalYCSmoney;//总已催收
	String totalYCSmoneySS;//总已催收
	String totalYCSmoney1;//总已催收
	String totalYCSmoney2;//总已催收
	public String getTotalCSmoney1() {
		return totalCSmoney1;
	}
	public void setTotalCSmoney1(String totalCSmoney1) {
		this.totalCSmoney1 = totalCSmoney1;
	}
	public String getTotalYCSmoney1() {
		return totalYCSmoney1;
	}
	public void setTotalYCSmoney1(String totalYCSmoney1) {
		this.totalYCSmoney1 = totalYCSmoney1;
	}
	public String getTotalYCSmoney2() {
		return totalYCSmoney2;
	}
	public void setTotalYCSmoney2(String totalYCSmoney2) {
		this.totalYCSmoney2 = totalYCSmoney2;
	}
	String jrtotalYCSmoney;//总已催收
	public String getJrtotalCSmoney() {
		return jrtotalCSmoney;
	}
	public void setJrtotalCSmoney(String jrtotalCSmoney) {
		this.jrtotalCSmoney = jrtotalCSmoney;
	}
	public String getJrtotalYCSmoney() {
		return jrtotalYCSmoney;
	}
	public void setJrtotalYCSmoney(String jrtotalYCSmoney) {
		this.jrtotalYCSmoney = jrtotalYCSmoney;
	}
	public double getJrycsbl() {
		return jrycsbl;
	}
	public void setJrycsbl(double jrycsbl) {
		this.jrycsbl = jrycsbl;
	}
	public String getTotalYCSmoney() {
		return totalYCSmoney;
	}
	public void setTotalYCSmoney(String totalYCSmoney) {
		this.totalYCSmoney = totalYCSmoney;
	}
	public int getThreeTG() {
		return threeTG;
	}
	public void setThreeTG(int threeTG) {
		this.threeTG = threeTG;
	}
	int four;	//四审笔数
	int dtxztxhk;	//四审笔数
	public int getDtxztxhk() {
		return dtxztxhk;
	}
	public void setDtxztxhk(int dtxztxhk) {
		this.dtxztxhk = dtxztxhk;
	}
	int yqbl; //逾期比例
	int nowlyh; //逾期比例
	public int getNowlyh() {
		return nowlyh;
	}
	public void setNowlyh(int nowlyh) {
		this.nowlyh = nowlyh;
	}
	int yqwhbl; //逾期未还比例
	int offlinezs; //逾期未还比例
	int agribank; //逾期未还比例
	int vietcombank; //逾期未还比例
	int offSacombank; //逾期未还比例
	int onSacombank; //逾期未还比例
	public int getOfflinezs() {
		return offlinezs;
	}
	public void setOfflinezs(int offlinezs) {
		this.offlinezs = offlinezs;
	}
	public int getAgribank() {
		return agribank;
	}
	public void setAgribank(int agribank) {
		this.agribank = agribank;
	}
	public int getVietcombank() {
		return vietcombank;
	}
	public void setVietcombank(int vietcombank) {
		this.vietcombank = vietcombank;
	}
	public int getOffSacombank() {
		return offSacombank;
	}
	public void setOffSacombank(int offSacombank) {
		this.offSacombank = offSacombank;
	}
	public int getOnSacombank() {
		return onSacombank;
	}
	public void setOnSacombank(int onSacombank) {
		this.onSacombank = onSacombank;
	}
	double ycsbl; //已催收比例
	double kpi; //已催收比例
	public String getTotalCSmoneySS() {
		return totalCSmoneySS;
	}
	public void setTotalCSmoneySS(String totalCSmoneySS) {
		this.totalCSmoneySS = totalCSmoneySS;
	}
	public String getTotalYCSmoneySS() {
		return totalYCSmoneySS;
	}
	public void setTotalYCSmoneySS(String totalYCSmoneySS) {
		this.totalYCSmoneySS = totalYCSmoneySS;
	}
	public double getKpi() {
		return kpi;
	}
	public void setKpi(double kpi) {
		this.kpi = kpi;
	}
	double ycsblyq; //已催收比例
	public double getYcsblyq() {
		return ycsblyq;
	}
	public void setYcsblyq(double ycsblyq) {
		this.ycsblyq = ycsblyq;
	}
	double jrycsbl; //已催收比例
	String totalYCSmoneyDT; //已催收比例
	public String getTotalYCSmoneyDT() {
		return totalYCSmoneyDT;
	}
	public void setTotalYCSmoneyDT(String totalYCSmoneyDT) {
		this.totalYCSmoneyDT = totalYCSmoneyDT;
	}
	public int getYqbl() {
		return yqbl;
	}
	public void setYqbl(int yqbl) {
		this.yqbl = yqbl;
	}
	public double getYcsbl() {
		return ycsbl;
	}
	public void setYcsbl(double ycsbl) {
		this.ycsbl = ycsbl;
	}
	public int getYqwhbl() {
		return yqwhbl;
	}
	public void setYqwhbl(int yqwhbl) {
		this.yqwhbl = yqwhbl;
	}
	public Integer getUserid() {
		return userid;
	}
	public void setUserid(Integer userid) {
		this.userid = userid;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public int getNowCount() {
		return nowCount;
	}
	public void setNowCount(int nowCount) {
		this.nowCount = nowCount;
	}
	public int getNowOnesh() {
		return nowOnesh;
	}
	public void setNowOnesh(int nowOnesh) {
		this.nowOnesh = nowOnesh;
	}
	public int getNowTwosh() {
		return nowTwosh;
	}
	public void setNowTwosh(int nowTwosh) {
		this.nowTwosh = nowTwosh;
	}
	public int getNowThreesh() {
		return nowThreesh;
	}
	public void setNowThreesh(int nowThreesh) {
		this.nowThreesh = nowThreesh;
	}
	public int getNowFoursh() {
		return nowFoursh;
	}
	public void setNowFoursh(int nowFoursh) {
		this.nowFoursh = nowFoursh;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getOne() {
		return one;
	}
	public void setOne(int one) {
		this.one = one;
	}
	public int getTwo() {
		return two;
	}
	public void setTwo(int two) {
		this.two = two;
	}
	public int getThree() {
		return three;
	}
	public void setThree(int three) {
		this.three = three;
	}
	public int getFour() {
		return four;
	}
	public void setFour(int four) {
		this.four = four;
	}
	public String getTotalCSmoney() {
		return totalCSmoney;
	}
	public void setTotalCSmoney(String totalCSmoney) {
		this.totalCSmoney = totalCSmoney;
	}
	
	
	

}
