package com.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import service.bfpay.SignServlet;
import sun.misc.BASE64Decoder;

import com.RonBao.config.DsfFunction;
import com.RonBao.util.HttpClientUtil;
import com.service.AotuTxService;
import com.thinkive.base.config.Configuration;
import com.thinkive.base.jdbc.DataRow;

public class TxUtil 
{
	
	private static Logger logger = Logger.getLogger(TxUtil.class);
	private static AotuTxService aotuTxService = new AotuTxService();
	
	//�ڱ�
	private static String signType = Configuration.getString("rb_pay.sign_type");
	private static String batchBizid = Configuration.getString("rb_pay.batchBizid");
	private static String _input_charset = Configuration.getString("rb_pay._input_charset");
	private static String batchBiztype = Configuration.getString("rb_pay.batchBiztype");
	private static String batchVersion = Configuration.getString("rb_pay.batchVersion");
	private static String dfUrl = Configuration.getString("rb_pay.dfUrl");
	private static String key=Configuration.getString("rb_pay.key");
	private static String dfcxUrl=Configuration.getString("rb_pay.dfcxUrl");
	
	//��
	public static void pay(DataRow data)
	{
        logger.info("�����û���Ϣ~~~~~"+data);
        try {
			
        	DataRow rz = aotuTxService.getUserRz(data.getInt("userid"));
        	
            JSONObject object = new JSONObject();
			object.put("return_url", "http://www.lvzbao.com/servlet/order/BfTxNotifyAction");
			//object.put("customer_id", data.getString("orderid"));
			object.put("customer_id", rz.getString("customer_id"));
			object.put("out_trade_no", data.getString("orderid"));
			object.put("card_no", rz.getString("cardno"));
			object.put("real_name", rz.getString("realname"));
			object.put("cert_no", rz.getString("idno"));
			object.put("cert_type", "01");
			object.put("amount_str", data.getString("sum"));
			object.put("bind_mobile", rz.getString("cellphone"));
			object.put("bank_code", rz.getString("bankbs"));
			object.put("withdraw_time", data.getString("applytime"));
			
			JSONObject ret_object = SignServlet.pay_tx(object);
			logger.info("���ַ�����Ϣ��"+ret_object);
			DataRow withdraw = new DataRow();
			if("0".equals(ret_object.getString("trade_status"))) 
			{
				withdraw.set("status", 6);
				withdraw.set("id", data.getString("id"));
				withdraw.set("remark", ret_object.getString("error_message"));
				aotuTxService.updateWithdraw(withdraw);
			}
			else
			{
				withdraw.set("status", 5);
				withdraw.set("id", data.getString("id"));
				withdraw.set("remark", ret_object.getString("error_message"));
				aotuTxService.updateWithdraw(withdraw);
			}
        } catch (Exception e) {
			// TODO: handle exception
        	logger.error(e);
		}
		
	}
	
	//�ڱ�֧��
	public static void rb_pay(DataRow data)
	{
		 logger.info("[�ڱ�֧��]�������ڱ�����>>>>>>>");
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		 String batchDate = sdf.format(new Date());
		 String batchCurrnum =data.getString("orderid");
         int ramdom=(int) (Math.random()*9000+1000);
		 String ite=ramdom+","+data.getString("cardno")+","+data.getString("realname")+","+data.getString("bankname")+",����,֧��,˽,"+data.getString("sum")+",CNY,ʡ,��,"+data.getString("cellphone");
		 logger.info("[�ڱ�֧��]��������Ϣ>>>>>>>"+ite);
		 String pa=Configuration.getString("rb_pay.rbcer");
		 Map<String, String> map =new HashMap<String, String>();
		 try {
		 String items = DsfFunction.jm(ite,pa);
		
		 Map<String,String> sPara = new HashMap<String, String>();
		 sPara.put("batchBizid",batchBizid);
		 sPara.put("batchVersion",batchVersion);
		 sPara.put("batchBiztype",batchBiztype);
		 sPara.put("batchDate",batchDate);
		 sPara.put("batchCurrnum",batchCurrnum);
		 sPara.put("batchContent",ite);
		 sPara.put("_input_charset",_input_charset);
		 Map<String,String> sParaNew = DsfFunction.ParaFilter(sPara); //��ȥ�����еĿ�ֵ��ǩ�����
		 String sign = DsfFunction.BuildMysign(sParaNew, key);//���ǩ����
		 
		 sPara.put("batchContent", items);
		 sPara.put("sign", sign);
		 sPara.put("signType", signType);
		 logger.info("[�ڱ�֧��]�����ֲ���>>>>>>>"+sPara);
		 String post;
		 post = HttpClientUtil.post(dfUrl, sPara);
		 logger.info("[�ڱ�֧��]�����ַ���>>>>>>>"+post);
	     new DsfFunction();
		map = DsfFunction.parse(post);
		 String status = map.get("status").toString();
		 DataRow withdraw = new DataRow();
		 if(status.equals("succ"))
		 {
			 withdraw.set("status", 6);
			 withdraw.set("id", data.getString("id"));
			 withdraw.set("remark", "�ύ�ɹ�");
			 aotuTxService.updateWithdraw(withdraw);
		 }
		 else
		 {
			 String reason = map.get("reason").toString();
			 withdraw.set("status", 5);
			 withdraw.set("id", data.getString("id"));
			 withdraw.set("remark", reason);
			 aotuTxService.updateWithdraw(withdraw);
		 }
		 
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * ��ѯ����
	 */
	public static void rb_query(DataRow data)
	{
		Map sPara = new HashMap();
		sPara.put("batchBizid", batchBizid);
		sPara.put("batchVersion", batchVersion);
		sPara.put("batchDate", data.getString("checktime"));
		sPara.put("batchCurrnum", data.getString("orderid"));
		sPara.put("_input_charset", _input_charset);
		
		Map sParaNew = DsfFunction.ParaFilter(sPara); // ��ȥ�����еĿ�ֵ��ǩ�����
		String sign = DsfFunction.BuildMysign(sParaNew, key);// ���ǩ����
		
		sPara.put("sign", sign);
		sPara.put("signType", signType);
		String post="";
		try {
			post = HttpClientUtil.post("http://entrust.reapal.com/agentpay/payquery", sPara);
			logger.info("[�ڱ�֧��]����ѯ���ַ���>>>>>>>"+post);
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] re = decoder.decodeBuffer(post);
			String pa = Configuration.getString("rb_pay.rbp12");
			post = DsfFunction.jim(re, pa);
			post = post.replaceAll(" ", "").replaceAll("\n", "");
			System.out.println(post);
			Map<String, String> map =new HashMap<String, String>();
			new DsfFunction();
			map = DsfFunction.parse(post);
			if(!map.containsKey("status"))
			{
				String batchContent = map.get("batchContent").toString();
				String batchContents[] = batchContent.split(",");
				DataRow withdraw = new DataRow();
				if(batchContents.length==10)//������
				{
					withdraw.set("status", 6);
					withdraw.set("id", data.getString("id"));
					aotuTxService.updateWithdraw(withdraw);
				}
				if(batchContents.length==12)//�ɹ�
				{
					withdraw.set("status", 2);
					withdraw.set("id", data.getString("id"));
					withdraw.set("remark", "ת�˳ɹ�");
					aotuTxService.updateWithdraw(withdraw);
					//���ö������ѣ��ͻ����ֳɹ���
					SendMsg.tplSendSms(1129913,"#name#="+data.getString("name")+"&#money#="+data.getString("sum"),data.getString("cellphone"));
					
				}
				if(batchContents.length==13)//ʧ��
				{
					withdraw.set("status", 5);
					withdraw.set("id", data.getString("id"));
					withdraw.set("remark", "����ʧ��");
					aotuTxService.updateWithdraw(withdraw);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
//		List<DataRow> list = aotuTxService.getAllTxList();
//		for (DataRow dataRow : list) 
//		{
//			try {
//				TxUtil.pay(dataRow);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				logger.error(e);
//			}
//		}
		
//		DataRow data = new DataRow();
//		data.set("bind_no", "6222980019708926");
//		data.set("realname", "��ѫ");
//		data.set("bankname", "ƽ������");
//		data.set("sum", "1.00");
//		data.set("cellphone", "18676396113");
//		data.set("orderid", "LZB"+System.currentTimeMillis());
//		new TxUtil().rb_pay(data);
		
//		DataRow data = new DataRow();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//		 String batchDate = sdf.format(new Date());
//		data.set("orderid", "LZB1451988342903");
//		data.set("checktime", batchDate);
//		new TxUtil().rb_query(data);
		
		System.out.println("1,6222980019708926,��ѫ,����,֧��,ƽ������,0,1.00,CNY,null,,�ɹ�,".split(",").length);
		
	}
		
}
