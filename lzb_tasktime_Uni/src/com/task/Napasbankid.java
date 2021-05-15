package com.task;

import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.service.AotuZDSHALLService;
import com.service.OLVSacombankService;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.timerengine.Task;

public class Napasbankid implements Task {
	private static Logger logger = Logger.getLogger(Napasbankid.class);
	private static AotuZDSHALLService aotuZDSHALLService = new AotuZDSHALLService();
	private static OLVSacombankService olvsacombankservice = new OLVSacombankService();
	@Override
	public void execute() 
	{
		Date date = new Date();
	    Calendar calendar = Calendar.getInstance();//日历对象
	    calendar.setTime(date);//设置当前日期			
	    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");		 
        String today = sdf.format(date);
        calendar.add(Calendar.DATE, 3);
        String nextDay = sdf.format(calendar.getTime());
        String ri="";
        String yue="";
        if(today.substring(3, 5).equals(nextDay.substring(3, 5))){
        	ri= nextDay.substring(0, 2);
        	yue= nextDay.substring(3, 5);
        }else{
        	ri= nextDay.substring(0, 2);
        	yue= nextDay.substring(3, 5);
        }
		List<DataRow> listuser = aotuZDSHALLService.getAllBank();
		String bankzu[] = {"ACB","VietcomBank","VietinBank","Techcombank","BIDV","VPBank","Eximbank","DongA Bank","VIB","MB Bank","Viet Capital Bank","OceanBank","VietABank","TPBank","HDBank","SCB","LienVietPostBank","SeABank","ABBank","Nam A Bank","OCB","GBBank","PG Bank","Saigon Bank","Kien Long Bank","NCB","BacABank","PVcomBank","VRB","Maritime Bank","Vietbank","BVB","Wooribank"};
	    String napasbankzu[] = {"NH TMCP A Chau (ACB)","Ngan Hang Vietcombank (VCB)","NH TMCP Cong Thuong Viet Nam (Vietinbank)","Ngan hang Ky Thuong Viet Nam(Techcombank)","NH TMCP Dau Tu va Phat Trien Viet Nam (BIDV)","Ngan Hang Viet Nam Thinh Vuong (VPBANK)","NH TMCP Xuat Nhap khau Viet Nam (Eximbank)","Ngan hang TMCP Dong A (DongA Bank)","NH TMCP Quoc Te Viet Nam (VIB)","NH TMCP Quan Doi (MBBank)",
	    		"NH TMCP Ban Viet (VCCB)","NH TMCP Dai Duong (OceanBank)","NH TMCP Viet A (VAB)","NH TMCP Tien Phong (TPBank)","NH TMCP PTTP Ho Chi Minh (HDBank)","NH TMCP Sai Gon (SCB)","Ngan Hang LienVietPostBank","NH TMCP Dong Nam A (SeABank)","NH TMCP An Binh (ABBank)","Ngan Hang TMCP Nam A","NH TMCP Phuong Dong (OCB)","NH TMCP Dau Khi Toan Cau (GPBank)","NH TMCP Xang Dau Petrolimex (PG Bank)","NH TMCP Sai Gon Cong Thuong (Saigonbank)",
	    		"Ngan hang TMCP Kien Long","NH TMCP Quoc Dan (NCB)","NH TMCP Bac A (BacABank)","NH TMCP Dai Chung VN (PVCombank)","NH Lien Doanh Viet Nga (VRB)","NH TMCP Hang Hai Viet Nam (MSB)","NH Viet Nam Thuong Tin (VietBank)","NH TMCP Bao Viet (BVB)","Ngan hang Wooribank"};
		
	    SimpleDateFormat famat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	    SimpleDateFormat famatid = new SimpleDateFormat("yyyyMMddhhmmssSSS");
	    for(int m =0 ; m<listuser.size();m++){
	    	DataRow datarow = listuser.get(m);
	    	int userid = datarow.getInt("userid");
	    	String userbankname = olvsacombankservice.getUserBankname(userid);
	    	String userbankcard = olvsacombankservice.getUserBankcardNo(userid);
	    	
			userbankname = userbankname.substring(0, userbankname.indexOf("-")).toLowerCase().trim();
			
			String banknamenapas = "NAPAS";
			if("sacombank".equals(userbankname)){
				banknamenapas = "STB";
			}
			for(int i =0 ;i<bankzu.length;i++){
				if(userbankname.equals(bankzu[i].toLowerCase())){
					userbankname = napasbankzu[i];
				}
			}
		    String time = famat.format(new Date());
		    String checkid = famatid.format(new Date());
		    
			try {
				String hostUrl = "https://emm.sacombank.com/bank-api/v1/bankcode/getbankcodenapas";
				String mysign = "";
				HttpHeaders header = null;
				ResponseEntity<String> resp = null;
				OLVSacombank myClass = new OLVSacombank();
				String xmlRequest = "<DOCUMENT><TRANSACTION_ID>"+checkid+"</TRANSACTION_ID><PARTNER_ID>CTYXT</PARTNER_ID><LOCAL_DATETIME>"+time+"</LOCAL_DATETIME></DOCUMENT>";
				PrivateKey privateKey = myClass.getPrivateKey(OLVSacombank.getDefaultPrivateKey());
				mysign = myClass.signature(xmlRequest,privateKey);
				header = myClass.addHeaderValue("Signature", mysign, header);
				String encoding = Base64.encode(("xuongthinhco:xtpass").getBytes("UTF-8"));
				header = myClass.addHeaderValue("Authorization", "Basic " + encoding, header);
				HttpEntity<String> requestEntity = new HttpEntity<String>(xmlRequest, header);
				RestTemplate restTemplate = new RestTemplate();
				ResponseEntity<String> response = restTemplate.exchange(hostUrl, HttpMethod.POST, requestEntity, String.class);
				String responseBody = response.getBody(); 
				HttpHeaders responseHeader = response.getHeaders();
				String sigData = responseHeader.getFirst("signature");
				if (myClass.verifySignature(responseBody, sigData)) {
					System.out.println("verify success");
				} else {
					System.out.println("verify fail");
				}
				Document doc = null;
				Document doccheck = null;
				
				 String userbankcode = "0";
				 String bankstatus = "";
				 String bankcode[] = {};
				 String bankname[] = {};
				 try {
				     doc = DocumentHelper.parseText(responseBody); // 将字符串转为XML
				     Element rootElt = doc.getRootElement(); // 获取根节点
				     bankstatus = rootElt.elementTextTrim("TRANSACTION_STATUS"); // 拿到head节点下的子节点title值
				     System.out.println("paystatus:" + bankstatus);
				     Element zijiedian = rootElt.element("LISTBANKCODE"); // 获取根节点
				     System.out.println("zijiedian:" + zijiedian);
				     List<Element> list = zijiedian.elements("BANKCODE");
					 // 遍历head节点
				     bankcode = new String[list.size()];
				     bankname = new String[list.size()];
					 for(int i =0 ; i<list.size();i++){
						 Element recordEle = list.get(i);
						 bankcode[i] = recordEle.elementTextTrim("BANKCODE"); // 拿到head节点下的子节点title值
						 bankname[i] = recordEle.elementTextTrim("BANKNAME"); // 拿到head节点下的子节点title值
						 if(userbankname.equals(bankname[i])){
				    		 userbankcode = bankcode[i] ;
				    	 }
					 }
				 } catch (DocumentException e) {
				     e.printStackTrace();
				 } catch (Exception e) {
				     e.printStackTrace();
				 }
				 System.out.println(userbankcode);
				 
				
				String username = olvsacombankservice.getRealname(userid+"");
				String r =username.replaceAll("[aáàãảạăắằẵẳặâấầẫẩậAÁÀÃẢẠĂẮẰẴẲẶÂẤẦẪẨẬ]", "a")
						.replaceAll("[eéèẽẻẹêếềễểệễEÉÈẼẺẸÊẾỀỄỂỆ]", "e")
						.replaceAll("[oóòõỏọôốồỗổộơớờỡởợOÓÒÕỎỌÔỐỒỖỔỘƠỚỜỠỞỢ]", "o")
						.replaceAll("[uúùũủụưứừữửựUÚÙŨỦỤƯỨỪỮỬỰ]", "u")
						.replaceAll("[iíìĩỉịIÍÌĨỈỊ]", "i")
						.replaceAll("[yýỳỹỷỵYÝỲỸỶỴ]", "y")
						.replaceAll("[đĐ]", "d");
				System.out.println("r=" + r);
				
				String checkUrl = "https://emm.sacombank.com/bank-api/v1/checkaccount";
				String checksign = "";
				HttpHeaders checkheader = null;
				ResponseEntity<String> checkresp = null;
				String checkxmlRequest = "<DOCUMENT><TRANSACTION_ID>"+checkid+"</TRANSACTION_ID><PARTNER_ID>CTYXT</PARTNER_ID><LOCAL_DATETIME>"+time+"</LOCAL_DATETIME><ACCOUNT_ID>"+userbankcard+"</ACCOUNT_ID><ACC_TYPE>ACC</ACC_TYPE><CHECK_TYPE>"+banknamenapas+"</CHECK_TYPE><BENF_NAME></BENF_NAME><BANK_ID>"+userbankcode+"</BANK_ID></DOCUMENT>";
				
				checksign = myClass.signature(checkxmlRequest,privateKey);
				System.out.println("data sign: " + checksign);
				checkheader = myClass.addHeaderValue("Signature", checksign, checkheader);
				
				String checkencoding = Base64.encode(("xuongthinhco:xtpass").getBytes("UTF-8"));
				checkheader = myClass.addHeaderValue("Authorization", "Basic " + checkencoding, checkheader);
				
				HttpEntity<String> checkrequestEntity = new HttpEntity<String>(checkxmlRequest, checkheader);
				RestTemplate checkrestTemplate = new RestTemplate();
				ResponseEntity<String> checkresponse = checkrestTemplate.exchange(checkUrl, HttpMethod.POST, checkrequestEntity, String.class);
				String checkresponseBody = checkresponse.getBody(); 
				System.out.println("responseBody: " + checkresponseBody);
				HttpHeaders checkresponseHeader = checkresponse.getHeaders();
				String checksigData = checkresponseHeader.getFirst("signature");
				if (myClass.verifySignature(checkresponseBody, checksigData)){
					System.out.println("verify success");
				} else {
					System.out.println("verify fail");
				}
				doccheck = DocumentHelper.parseText(checkresponseBody); // 将字符串转为XML
				Element checkElt = doccheck.getRootElement(); // 获取根节点
				bankstatus = checkElt.elementTextTrim("TRANSACTION_STATUS"); // 拿到head节点下的子节点title值
				String requstname = checkElt.elementTextTrim("ACC_NAME"); // 拿到head节点下的子节点title值
				if("0".equals(bankstatus)){
					if(r.equals(requstname.toLowerCase())){
						 DataRow rowbank = new DataRow();
						 rowbank.set("userid", userid);
						 rowbank.set("napasbankno", userbankcode);
						 rowbank.set("napasbankname", userbankname);
						 olvsacombankservice.updateUserBank(rowbank);
					}
				}
				 
			} catch (RestClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
		
	}
}

