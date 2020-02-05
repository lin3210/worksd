package root.current;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;

import root.SendMsg;

import com.alibaba.fastjson.JSON;
import com.project.constant.IConstants;
import com.project.service.account.JBDUserService;
import com.project.service.account.JBDcmsService;
import com.shove.security.Encrypt;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;
import com.thinkive.base.jdbc.DBPage;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.util.SessionHelper;
import com.thinkive.base.util.StringHelper;
import com.thinkive.web.base.ActionResult;
import com.thinkive.web.base.BaseAction;

public class M99User1 extends BaseAction {

	private static Logger logger = Logger.getLogger(M99User1.class);
	private static JBDcmsService jbdcmsService = new JBDcmsService();
	private static JBDUserService jbdUserService = new JBDUserService();
	
	String jiami = "G0eHIW3op8dYIWsdsdeqFSDeafhklRG";

	public static long sendCount = 0;
	long time = 1000 * 60 * 3;
	
	/**
	 * 获得客户的真实IP地址
	 * 
	 * @return
	 */
	public String getipAddr() {
		String ip = getRequest().getHeader("X-Real-IP");
		if (StringHelper.isEmpty(ip)) {
			ip = jbdUserService.getRemortIP(getRequest());
		}
		return ip;
	}
	// 第一步审核 获取指定借款ID 用户信息
	public ActionResult doGetUserM99YN() throws Exception {
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
		JSONObject jsonObject = new JSONObject();
		com.alibaba.fastjson.JSONObject jsonObj = jsonString;
		SimpleDateFormat fmtrq  = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		SimpleDateFormat famt = new SimpleDateFormat("dd-MM-yyyy");
		Date datenow = new Date();
		String idno = jsonObj.getString("idno");
	    String miwen = jsonObj.getString("secret");
	    String jiamiwen = Encrypt.MD5(idno+jiami);
	    if(jiamiwen.equals(miwen)){
	    	List<DataRow> listaa = jbdcmsService.getALLshenfenSalo(idno);
	    	int userid=0;
	    	int vipstatus=0;
	    	if(listaa.size() == 1){
	    		userid = jbdcmsService.getUserIDSF(idno);
	        	vipstatus = jbdcmsService.getHHZT(userid);
	    	}else{
	    		for (int i = 0; i < listaa.size(); i++) {
					DataRow datarow = listaa.get(i);
		        	vipstatus = jbdcmsService.getHHZT(datarow.getInt("userid"));
		        	if(vipstatus == 1){
		        		userid = datarow.getInt("userid");
		        	}
				}
	    	}
        	DataRow row1 = new DataRow();
       		row1.set("idno", idno); 
       	    row1.set("vipstatus",vipstatus);
       	    row1.set("createtime",fmtrq.format(new Date()));
       	    jbdcmsService.insertPPCX(row1);
       	    if(vipstatus == 1){
       	    	DataRow row = new DataRow();
       	    	String userrealname = jbdcmsService.getUserName(userid);
       			
       	    	List<DataRow> numcount1 = jbdcmsService.getJKSB1ListOne(userid);
       			List<DataRow> numcount2 = jbdcmsService.getJKSBListTwo(userid);
       			List<DataRow> numcount3 = jbdcmsService.getJKSBListThree(userid);
       			// 根据用户获取用户借款成功的信息
       			List<DataRow> windrsize = jbdcmsService.getJKCGList(userid);
       			// 根据用户获取用户催收的信息
       			List<DataRow> cuishouxx = jbdcmsService.getJKCSList(userid);
       			// 根据用户ID获取用户信息
       			DataRow user = jbdcmsService.getUserRecOneInfo(userid);
       			// 根据用户ID获取用户信息
       			DataRow cuishoubaogao = jbdcmsService.getCuishoubaogao(userid);
       			// 根据用户ID获取用户申请借款的总次数
       			int userJK = jbdcmsService.getUserJKCS(userid);
       			String JKFQCS = jbdcmsService.getUserJKFQCS(userid);
       			int userJKFQ = 0;
       			if (!TextUtils.isEmpty(JKFQCS)) {
       				userJKFQ = JKFQCS.length();
       			}
       			// 根据用户ID 获取用户成功申请借款的次数
       			int userCgJK = jbdcmsService.getUserJKcg(userid);
       			// 根据用户ID获取用户申请借款的逾期次数
       			int yuq_cs = jbdcmsService.getUserYQCS(userid);
       		
       			DataRow userfinance = jbdcmsService.personFinance(userid);
       		
       			String address = userfinance.getString("address");
       			String csrq = userfinance.getString("age");
       			
       			String r =userrealname.replaceAll("[aáàãảạăắằẵẳặâấầẫẩậAÁÀÃẢẠĂẮẰẴẲẶÂẤẦẪẨẬ]", "a")
       					.replaceAll("[eéèẽẻẹêếềễểệEÉÈẼẺẸÊẾỀỄỂỆ]", "e")
       					.replaceAll("[oóòõỏọôốồỗổộơớờỡởợOÓÒÕỎỌÔỐỒỖỔỘƠỚỜỠỞỢ]", "o")
       					.replaceAll("[uúùũủụưứừữửựUÚÙŨỦỤƯỨỪỮỬỰ]", "u")
       					.replaceAll("[iíìĩỉịIÍÌĨỈỊ]", "i")
       					.replaceAll("[yýỳỹỷỵYÝỲỸỶỴ]", "y")
       					.replaceAll("[đĐ]", "d");
       		
       			List<DataRow> listname = jbdcmsService.getUserRealnamelist();
       			String useridzuage[] = new String[3];
       			int jishu = 0;
       			for (int i = 0; i < listname.size(); i++) {
       				DataRow rowid = listname.get(i);
       				String realname = rowid.getString("realname");
       				String ageage = rowid.getString("age");
       				String useridid = rowid.getString("userid");
       				realname =realname.replaceAll("[aáàãảạăắằẵẳặâấầẫẩậAÁÀÃẢẠĂẮẰẴẲẶÂẤẦẪẨẬ]", "a")
       						.replaceAll("[eéèẽẻẹêếềễểệEÉÈẼẺẸÊẾỀỄỂỆ]", "e")
       						.replaceAll("[oóòõỏọôốồỗổộơớờỡởợOÓÒÕỎỌÔỐỒỖỔỘƠỚỜỠỞỢ]", "o")
       						.replaceAll("[uúùũủụưứừữửựUÚÙŨỦỤƯỨỪỮỬỰ]", "u")
       						.replaceAll("[iíìĩỉịIÍÌĨỈỊ]", "i")
       						.replaceAll("[yýỳỹỷỵYÝỲỸỶỴ]", "y")
       						.replaceAll("[đĐ]", "d");
       		
       				if(r.trim().toLowerCase().equals(realname.trim().toLowerCase()) && csrq.equals(ageage)){
       					useridzuage[jishu] = useridid ;
       					jishu ++ ;
       				}
       				if(jishu >2){
       					break;
       				}
       			}
       			
       			String homeaddress = userfinance.getString("homeaddress");
       		
       			int age = 0;
       			if (!TextUtils.isEmpty(csrq)) {
       				age = Integer.parseInt(famt.format(datenow).substring(6))
       						- Integer.parseInt(csrq.substring(6));
       			}
       			// 获取用户的工作信息
       			DataRow personInfo = jbdcmsService.personInfoYN(userid);
       			// 联系人信息
       			DataRow lianxi = jbdcmsService.personLianXi(userid);
       			// 银行卡信息
       			DataRow bank = jbdcmsService.personBank(userid);
       			// 审核状态
       			DataRow shenfen = jbdcmsService.personShenfen(userid);
       			/*
       			 * //获取用户教育信息 DataRow userTonghua =
       			 * jbdcmsService.getUserTonghua(userid);
       			 */
       		
       			String p1 = shenfen.getString("p1");
       			String p2 = shenfen.getString("p2");
       			String p3 = shenfen.getString("p3");
       		
       			row.set("p1", p1);
       			row.set("p2", p2);
       			row.set("p3", p3);
       			
       			row.set("user_id", user.getString("userid3"));// 编号
       			row.set("useridzuage", useridzuage);// 生日姓名相同
       			row.set("username", user.getString("username"));// 手机型号
       			row.set("profession", user.getString("profession"));// 编号
       			row.set("realname", user.getString("cardusername")); // 姓名
       			row.set("napasbankno", user.getString("napasbankno")); // 姓名
       			row.set("cardid", idno); // 身份证
       			row.set("address", address); // 地址
       			
       			row.set("csrq", csrq); // 出生日期
       			row.set("age", age); // 出生日期
       			row.set("homeaddress", homeaddress); // 出生日期
       			row.set("userrealname", userrealname); // 出生日期
       			row.set("cellphone", user.getString("mobilephone")); // 手机号
       			row.set("createtime", user.getString("createtime")); // 注册时间
       			row.set("windrsize", user.getString("chenggong_cs")); // 成功放款次数
       			row.set("cou", userJK); // 申请借款次数
       			row.set("jkfqcs", userJKFQ); // 申请借款次数
       			row.set("yuqcount", yuq_cs); // 逾期次数
       			row.set("usercgjk", userCgJK); // 成功次数
       			if (personInfo != null) {
       				row.set("company", personInfo.getString("company"));
       				row.set("tel", personInfo.getString("tel"));
       				row.set("workaddress", personInfo.getString("address"));
       				row.set("workname", personInfo.getString("workname"));
       				row.set("position", personInfo.getString("position"));
       				row.set("pay", personInfo.getString("pay"));
       				row.set("worktime", personInfo.getString("time"));
       				row.set("status", personInfo.getString("status"));
       			} else {
       				row.set("company", "");
       				row.set("tel", "");
       				row.set("workaddress", "");
       				row.set("workname", "");
       				row.set("position", "");
       				row.set("pay", "");
       				row.set("worktime", "");
       				row.set("status", "");
       			}
       			row.set("lianxi", lianxi);
       			row.set("bank", bank);
       			row.set("shenfen", shenfen);
       			
       			row.set("numcount1", numcount1);
       			row.set("numcount2", numcount2);
       			row.set("numcount3", numcount3);
       			row.set("windrsize", windrsize);
       			
       			row.set("cuishouxx", cuishouxx);
       			row.set("cuishoubaogao", cuishoubaogao);
       			row.set("error", "1");
       			row.set("msg", "系统正常");
       			JSONObject object = JSONObject.fromBean(row);
       			this.getResponse().setHeader("content-type", "application/json;charset=utf-8");
       			this.getWriter().write(object.toString());
       			return null;
       	    }else{
       	    	jsonObject.put("error", 0);
      		    jsonObject.put("msg", "False!");
      		    this.getWriter().write(jsonObject.toString());
      		    return null;
       	    }
	    }else{
	    	return null;
	    }
		
	}
	
	public ActionResult doGetUserM99YNH5() throws Exception {
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
		JSONObject jsonObject = new JSONObject();
		com.alibaba.fastjson.JSONObject jsonObj = jsonString;
		SimpleDateFormat fmtrq  = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String idno = jsonObj.getString("idno");
	    String miwen = jsonObj.getString("secret");
	    String jiamiwen = Encrypt.MD5(idno+jiami);
	    if(jiamiwen.equals(miwen)){
	    	List<DataRow> listaa = jbdcmsService.getALLshenfenSalo(idno);
	    	int userid=0;
	    	int vipstatus=0;
	    	if(listaa.size() == 1){
	    		userid = jbdcmsService.getUserIDSF(idno);
	        	vipstatus = jbdcmsService.getHHZT(userid);
	    	}else{
	    		for (int i = 0; i < listaa.size(); i++) {
					DataRow datarow = listaa.get(i);
		        	vipstatus = jbdcmsService.getHHZT(datarow.getInt("userid"));
		        	if(vipstatus == 1){
		        		userid = datarow.getInt("userid");
		        	}
				}
	    	}
       	    if(vipstatus == 1){
       	    	DataRow row = new DataRow();
       			// 审核状态
       			DataRow shenfen = jbdcmsService.personShenfen(userid);
       			String p1 = shenfen.getString("p1");
       			String p2 = shenfen.getString("p2");
       			String p3 = shenfen.getString("p3");
       		
       			row.set("p1", p1);
       			row.set("p2", p2);
       			row.set("p3", p3);
       			
       			JSONObject object = JSONObject.fromBean(row);
       			this.getWriter().write(object.toString());
       			return null;
       	    }else{
       	    	jsonObject.put("error", 0);
      		    jsonObject.put("msg", "False!");
      		    this.getWriter().write(jsonObject.toString());
      		    return null;
       	    }
	    }else{
	    	return null;
	    }
		
	}
	public ActionResult doGetM99TXL() throws Exception {
		
		JSONObject jsonObject = new JSONObject();
		
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
		com.alibaba.fastjson.JSONObject jsonObj = jsonString;
		SimpleDateFormat fmtrq  = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		SimpleDateFormat famt = new SimpleDateFormat("dd-MM-yyyy");
		Date datenow = new Date();
		String idno = jsonObj.getString("idno");
		if(TextUtils.isEmpty(idno)){
			idno="0";
		}
		int temp = jsonObj.getInteger("temp");
		String tempVelue = jsonObj.getString("tempvl");
		int curPage = jsonObj.getInteger("curPage");
	    String miwen = jsonObj.getString("secret");
	    String jiamiwen = Encrypt.MD5(idno+jiami);
	    if(jiamiwen.equals(miwen)){
	    	List<DataRow> listaa = jbdcmsService.getALLshenfenSalo(idno);
	    	int userid=0;
	    	int vipstatus=0;
	    	if(listaa.size() == 1){
	    		userid = jbdcmsService.getUserIDSF(idno);
	        	vipstatus = jbdcmsService.getHHZT(userid);
	    	}else{
	    		for (int i = 0; i < listaa.size(); i++) {
					DataRow datarow = listaa.get(i);
		        	vipstatus = jbdcmsService.getHHZT(datarow.getInt("userid"));
		        	if(vipstatus == 1){
		        		userid = datarow.getInt("userid");
		        	}
				}
	    	}
	    	DataRow row1 = new DataRow();
       		row1.set("idno", idno); 
       	    row1.set("vipstatus",vipstatus);
       	    row1.set("createtime",fmtrq.format(new Date()));
       	    jbdcmsService.insertPPCX(row1);
       	 if(vipstatus == 1){
       		 	DataRow row = new DataRow();
	       		String phone = "";
	     		String name = "";
	     		String cxid = "";
	     		if (temp == 1) {
	    			cxid = tempVelue;
	    		} else if (temp == 2) {
	    			phone = tempVelue;
	    		} else if (temp == 3) {
	    			name = tempVelue;
	    		}
	     		DBPage page = jbdcmsService.getUserTongxunlu(curPage, 15, userid, cxid,
						phone, name);
	     		int tongxunluCount = jbdcmsService.getTongxunluCountYN(userid);
	
	     		row.set("tongxunlucount", tongxunluCount);
	     		row.set("list", page);
	     		row.set("tempvalu", tempVelue);
	     		row.set("temp", temp);

    			row.set("error", "1");
    			row.set("msg", "系统正常");
    			JSONObject object = JSONObject.fromBean(row);
    			this.getResponse().setHeader("content-type", "application/json;charset=utf-8");
    			this.getWriter().write(object.toString());
    			return null;
    	    }else{
    	    	jsonObject.put("error", 0);
   		    jsonObject.put("msg", "False!");
   		    this.getWriter().write(jsonObject.toString());
   		    return null;
    	    }
	    }else{
	    	return null;
	    }
	}
	private  com.alibaba.fastjson.JSONObject getRequestJson(HttpServletRequest request) throws IOException {
		  InputStream in = request.getInputStream();
		  byte[] b = new byte[10240];
		  int len;
		  ByteArrayOutputStream baos = new ByteArrayOutputStream();
		  while ((len = in.read(b)) > 0) {
		      baos.write(b, 0, len);
		  }
		  String bodyText1 = new String(baos.toByteArray(), "UTF-8");
		  bodyText1 = bodyText1.replaceAll("%(?![0-9a-fA-F]{2})", "%25");  
		  String bodyText = URLDecoder.decode(bodyText1,"UTF-8");
		  com.alibaba.fastjson.JSONObject json =  (com.alibaba.fastjson.JSONObject) JSON.parse(bodyText);
		  if (true) {
			  logger.info("传输成功！");
		  }
		  return json;
}
	// 手机运营商数据

	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}
	
   public ActionResult doGetM99jkAccount() throws Exception {
		
		JSONObject jsonObject = new JSONObject();
		
		HttpServletRequest request = getRequest();
		com.alibaba.fastjson.JSONObject jsonString = getRequestJson(request);
		com.alibaba.fastjson.JSONObject jsonObj = jsonString;

		String idno = jsonObj.getString("idno");
		
		if(TextUtils.isEmpty(idno)){
			idno="0";
		}

		String phone = jsonObj.getString("phone");
		String name = jsonObj.getString("name");
	    String miwen = jsonObj.getString("secret");
	    String jiamiwen = Encrypt.MD5(jiami+idno);
	    if(jiamiwen.equals(miwen)){
	    	
	    	List<DataRow> listaa = jbdcmsService.getALLshenfenSalo(idno);
	    	int userid=0;
	    	int vipstatus=0;
	    	if(listaa.size() == 1){
	    		userid = jbdcmsService.getUserIDSF(idno);
	        	vipstatus = jbdcmsService.getHHZT(userid);
	    	}else{
	    		for (int i = 0; i < listaa.size(); i++) {
					DataRow datarow = listaa.get(i);
		        	vipstatus = jbdcmsService.getHHZT(datarow.getInt("userid"));
		        	if(vipstatus == 1){
		        		userid = datarow.getInt("userid");
		        	}
				}
	    	}
	    	int heihu_state =jbdcmsService.getHHZT_state(userid);
	    	
       	 if(vipstatus == 1 && heihu_state ==0){
       		 	
	       		int jkcs = jbdcmsService.getuserjkcs(userid);
	     		
	     		
	     		DataRow row = new DataRow();
	     		row.set("idno", idno);
	     		row.set("jkcs", jkcs);
    			row.set("error", "1");
    			row.set("msg", "系统正常");
    			
    			JSONObject object = JSONObject.fromBean(row);
    			this.getResponse().setHeader("content-type", "application/json;charset=utf-8");
    			this.getWriter().write(object.toString());
    			return null;
    	    }else{
    	    	jsonObject.put("error", 0);
   		        jsonObject.put("msg", "False!");
   		        this.getWriter().write(jsonObject.toString());
   		        return null;
    	    }
	    }else{
	    	return null;
	    }
	}
}
