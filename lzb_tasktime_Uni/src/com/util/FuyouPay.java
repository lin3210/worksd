package com.util;

import com.fuiou.http.HttpClientHelper;
import com.fuiou.model.Incomeforreq;
import com.fuiou.model.Payforreq;
import com.fuiou.model.Qrytransreq;
import com.fuiou.model.TCustmrBusi;
import com.fuiou.util.DateUtils;
import com.fuiou.util.MD5Util;
import com.fuiou.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

public class FuyouPay
{
  private static Logger logger = Logger.getLogger(FuyouPay.class);
  public static Map<String, String> props = new HashMap();
  public static String REQADDRESS = "reqAddress";
  public static String CONTRACTADDRESS = "contractAddress";
  public static String mchntCd="0002900F0345178";
  static {
    ResourceBundle bundle = ResourceBundle.getBundle("fht");
    Enumeration enume = bundle.getKeys();
    String key = null;
    while (enume.hasMoreElements()) {
      key = (String)enume.nextElement();
      props.put(key, bundle.getString(key).trim());
    }
  }

  public static void main(String[] args) {
    System.out.println("》》欢迎使用富友代收付对接DEMO");
  /*  FuyouPay fuiouFHTService  = new FuyouPay();
    String merchOrderId = "sd" + System.currentTimeMillis();
    Payforreq payforreq = new Payforreq();
    payforreq.setAmt(new StringBuilder(String.valueOf(100)).toString());
    payforreq.setBankno("0302");
    payforreq.setMobile("13543281052");
    payforreq.setMchntCd("0002900F0345178");
    payforreq.setOrderno("sd1492163495229");
    payforreq.setMerdt("20170417");
    payforreq.setCityno("5840");
    payforreq.setAccntno("6217710301551743");
    payforreq.setAccntnm("李磊");
    String tCustmrBusi2 = fuiouFHTService.payforreq(payforreq);	
  
    System.out.println(tCustmrBusi2);*/
	 /* Qrytransreq queryTrans = new Qrytransreq();
	    queryTrans.setVer("1.0");
	    queryTrans.setOrderno("sd1492163495225");
	    queryTrans.setStartdt("20170414");
	    queryTrans.setEnddt("20170415");
	    queryTrans.setMchntCd(mchntCd);
	    queryTrans.setBusicd("AP01");
	    String queryTrans2 = queryTrans(queryTrans);
	    System.out.println(queryTrans2);*/
   
  }

  public static String contract(TCustmrBusi tCustmrBusi)
  {
    String isCallback = tCustmrBusi.getIsCallback() == null ? "0" : tCustmrBusi.getIsCallback();
    String busiCd = tCustmrBusi.getBusiCd() == null ? "AC01" : tCustmrBusi.getBusiCd();
    String credtTp = tCustmrBusi.getCredtTp() == null ? "0" : tCustmrBusi.getCredtTp();
    String acntNo = tCustmrBusi.getAcntNo() == null ? "" : tCustmrBusi.getAcntNo();
    String bankCd = tCustmrBusi.getBankCd() == null ? "" : tCustmrBusi.getBankCd();
    String userNm = tCustmrBusi.getUserNm() == null ? "" : tCustmrBusi.getUserNm();
    String credtNo = tCustmrBusi.getCredtNo() == null ? "" : tCustmrBusi.getCredtNo();
    String srcChnl = tCustmrBusi.getSrcChnl() == null ? "DSF" : tCustmrBusi.getSrcChnl();
    String acntTp = tCustmrBusi.getAcntTp() == null ? "01" : tCustmrBusi.getAcntTp();
    String mobileNo = tCustmrBusi.getMobileNo() == null ? "" : tCustmrBusi.getMobileNo();
    String mchntCd = tCustmrBusi.getMchntCd() == null ? "" : tCustmrBusi.getMchntCd();
    String reserved1 = tCustmrBusi.getReserved1() == null ? "" : tCustmrBusi.getReserved1();
    tCustmrBusi.setSignature(getSignature(tCustmrBusi, props.get("mchntkey")));
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><custmrBusi><isCallback>" + isCallback + "</isCallback><busiCd>" + busiCd + "</busiCd><credtTp>" + credtTp + "</credtTp><acntNo>" + acntNo + "</acntNo><bankCd>" + bankCd + "</bankCd><userNm>" + userNm + "</userNm><credtNo>" + credtNo + "</credtNo><srcChnl>" + srcChnl + "</srcChnl><acntTp>" + acntTp + "</acntTp><mobileNo>" + mobileNo + "</mobileNo><mchntCd>" + mchntCd + "</mchntCd><reserved1>" + reserved1 + "</reserved1><signature>" + tCustmrBusi.getSignature() + "</signature></custmrBusi>";
    List list = new ArrayList();
    String[] nv2 = { "xml", xml.trim() };
    list.add(nv2);
    String url = props.get(CONTRACTADDRESS);
    String nvPairs = HttpClientHelper.getNvPairs(list, "UTF-8");
    logger.info(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss SSS") + ":发送报文" + xml);
    logger.info(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss SSS") + ":发送参数" + nvPairs);
    String outStr = HttpClientHelper.doHttp(url, "POST", "utf-8", nvPairs, props.get("timeout"));
    if (StringUtils.isNotEmpty(outStr)) {
      return outStr;
    }
    return null;
  }

  public static String queryTrans(Qrytransreq qrytransreq) {
    String ver = qrytransreq.getVer() == null ? "" : qrytransreq.getVer();
    String orderno = qrytransreq.getOrderno() == null ? "" : qrytransreq.getOrderno();
    String busicd = qrytransreq.getBusicd() == null ? "" : qrytransreq.getBusicd();
    String startdt = qrytransreq.getStartdt() == null ? "" : qrytransreq.getStartdt();
    String enddt = qrytransreq.getEnddt() == null ? "" : qrytransreq.getEnddt();
    String transst = qrytransreq.getTransst() == null ? "" : qrytransreq.getTransst();
    String xml = "<qrytransreq><ver>" + ver + "</ver><orderno>" + orderno + "</orderno><busicd>" + busicd + "</busicd><startdt>" + startdt + "</startdt><enddt>" + enddt + "</enddt><transst>" + transst + "</transst></qrytransreq>";
    String reqType = "qrytransreq";
    List list = new ArrayList();
    String[] nv1 = { "merid", qrytransreq.getMchntCd() };
    String[] nv2 = { "xml", xml };
    list.add(nv1);
    list.add(nv2);
    String[] nv3 = { "reqtype", reqType };
    list.add(nv3);
    String url = props.get(REQADDRESS);
    String macSource = qrytransreq.getMchntCd() + "|" + props.get("mchntkey") + "|" + reqType + "|" + xml;
    String mac = MD5Util.encode(macSource, "UTF-8").toUpperCase();
    String[] nv4 = { "mac", mac };
    list.add(nv4);
    String nvPairs = HttpClientHelper.getNvPairs(list, "UTF-8");
    logger.info(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss SSS") + reqType + ":发送报文" + xml);
    logger.info(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss SSS") + reqType + ":发送参数" + nvPairs);
    String outStr = HttpClientHelper.doHttp(url, "POST", "UTF-8", nvPairs, props.get("timeout"));
    if (StringUtils.isNotEmpty(outStr)) {
      return outStr;
    }
    return null;
  }

  public String payforreq(Payforreq payforreq)
  {
    String ver = payforreq.getVer() == null ? "1.0" : payforreq.getVer();
    String amt = payforreq.getAmt() == null ? "0" : payforreq.getAmt();
    String cityno = payforreq.getCityno() == null ? "" : payforreq.getCityno();
    String entseq = payforreq.getEntseq() == null ? "" : payforreq.getEntseq();
    String bankno = payforreq.getBankno() == null ? "" : payforreq.getBankno();
    String merdt = payforreq.getMerdt() == null ? "" : payforreq.getMerdt();
    String accntno = payforreq.getAccntno() == null ? "" : payforreq.getAccntno();
    String orderno = payforreq.getOrderno() == null ? "" : payforreq.getOrderno();
    String accntnm = payforreq.getAccntnm() == null ? "" : payforreq.getAccntnm();
    String branchnm = payforreq.getBranchnm() == null ? "" : payforreq.getBranchnm();
    String mobile = payforreq.getMobile() == null ? "" : payforreq.getMobile();
    String memo = payforreq.getMemo() == null ? "" : payforreq.getMemo();
    String mchntCd = payforreq.getMchntCd() == null ? "" : payforreq.getMchntCd();
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><payforreq><ver>" + 
      ver + "</ver>" + 
      "<cityno>" + cityno + "</cityno>" + 
      "<amt>" + amt + "</amt>" + 
      "<entseq>" + entseq + "</entseq>" + 
      "<bankno>" + bankno + "</bankno>" + 
      "<merdt>" + merdt + "</merdt>" + 
      "<accntno>" + accntno + "</accntno>" + 
      "<orderno>" + orderno + "</orderno>" + 
      "<accntnm>" + accntnm + "</accntnm>" + 
      "<branchnm>" + branchnm + "</branchnm>" + 
      "<mobile>" + mobile + "</mobile>" + 
      "<memo>" + memo + "</memo>" + 
      "</payforreq>";

    String reqType = "payforreq";
    List list = new ArrayList();
    String[] nv1 = { "merid", payforreq.getMchntCd() };
    String[] nv2 = { "xml", xml };
    list.add(nv1);
    list.add(nv2);
    String[] nv3 = { "reqtype", "payforreq" };
    list.add(nv3);
    String url = props.get(REQADDRESS);
    String macSource = mchntCd + "|" + props.get("mchntkey") + "|payforreq|" + xml;
    String mac = MD5Util.encode(macSource, "UTF-8").toUpperCase();
    logger.info("mac" + mac);
    String[] nv4 = { "mac", mac };
    list.add(nv4);
    String nvPairs = HttpClientHelper.getNvPairs(list, "UTF-8");
    logger.info(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss SSS") + reqType + ":发送报文" + xml);
    logger.info(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss SSS") + reqType + ":发送参数" + nvPairs);
    String outStr = HttpClientHelper.doHttp(url, "POST", "UTF-8", nvPairs, props.get("timeout"));

    logger.info(outStr);
    if (StringUtils.isNotEmpty(outStr)) {
      return outStr;
    }
    return null;
  }

  public static String incomeforreq(Incomeforreq incomeforreq) {
    String ver = incomeforreq.getVer() == null ? "1.0" : incomeforreq.getVer();
    String merdt = incomeforreq.getMerdt() == null ? "" : incomeforreq.getMerdt();
    String orderno = incomeforreq.getOrderno() == null ? "" : incomeforreq.getOrderno();
    String bankno = incomeforreq.getBankno() == null ? "" : incomeforreq.getBankno();
    String accntno = incomeforreq.getAccntno() == null ? "" : incomeforreq.getAccntno();
    String accntnm = incomeforreq.getAccntnm() == null ? "" : incomeforreq.getAccntnm();
    String amt = incomeforreq.getAmt() == null ? "0" : incomeforreq.getAmt();
    String mobile = incomeforreq.getMobile() == null ? "" : incomeforreq.getMobile();
    String certtp = incomeforreq.getCerttp() == null ? "0" : incomeforreq.getCerttp();
    String certno = incomeforreq.getCertno() == null ? "" : incomeforreq.getCertno();
    String mchntCd = incomeforreq.getMchntCd() == null ? "" : incomeforreq.getMchntCd();
    String entseq = incomeforreq.getEntseq() == null ? "" : incomeforreq.getEntseq();
    String memo = incomeforreq.getMemo() == null ? "" : incomeforreq.getMemo();

    String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?><incomeforreq><ver>" + ver + "</ver><merdt>" + merdt + "</merdt><orderno>" + orderno + "</orderno><bankno>" + bankno + "</bankno><accntno>" + accntno + "</accntno><accntnm>" + accntnm + "</accntnm><amt>" + amt + "</amt><entseq>" + entseq + "</entseq><memo>" + memo + "</memo><mobile>" + mobile + "</mobile><certtp>" + certtp + "</certtp><certno>" + certno + "</certno></incomeforreq>";
    String reqType = "sincomeforreq";
    List list = new ArrayList();
    String[] nv1 = { "merid", mchntCd };
    String[] nv2 = { "xml", xml };
    list.add(nv1);
    list.add(nv2);
    String[] nv3 = { "reqtype", reqType };
    list.add(nv3);
    String url = props.get(REQADDRESS);
    String macSource = mchntCd + "|" + props.get("mchntkey") + "|sincomeforreq|" + xml;
    String mac = MD5Util.encode(macSource, "utf-8").toUpperCase();
    String[] nv4 = { "mac", mac };
    list.add(nv4);
    String nvPairs = HttpClientHelper.getNvPairs(list, "UTF-8");
    logger.info(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss SSS") + reqType + ":发送报文" + xml);
    logger.info(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss SSS") + reqType + ":发送参数" + nvPairs);
    String outStr = HttpClientHelper.doHttp(url, "POST", "UTF-8", nvPairs, props.get("timeout"));
    if (StringUtils.isNotEmpty(outStr)) {
      return outStr;
    }
    return null;
  }

  private static String hex(List<String> values, String key) {
    String[] strs = new String[values.size()];
    for (int i = 0; i < strs.length; i++) {
      strs[i] = values.get(i);
    }
    Arrays.sort(strs);
    StringBuffer source = new StringBuffer();
    for (String str : strs) {
      source.append(str).append("|");
    }
    String bigstr = source.substring(0, source.length() - 1);
    logger.debug("bigstr:" + bigstr);
    System.out.println(bigstr);
    String result = DigestUtils.shaHex(DigestUtils.shaHex(bigstr) + "|" + key);
    logger.debug("bigstr hex result:" + result);
    return result;
  }

  private static String getSignature(Object bean, String key) {
    List values = new ArrayList();
    String signature = null;

    for (Method method : bean.getClass().getMethods()) {
      try
      {
        if ((!method.getName().startsWith("get")) || ("getClass".equalsIgnoreCase(method.getName())))
          continue;
        Object o = method.invoke(bean, null);
        if ((o != null) && (StringUtils.isNotEmpty(o.toString())))
          if ("getSignature".equalsIgnoreCase(method.getName().toLowerCase()))
            signature = o.toString();
          else
            values.add(o.toString());
      }
      catch (IllegalArgumentException e)
      {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    }
    signature = hex(values, key);
    return signature;
  }
}