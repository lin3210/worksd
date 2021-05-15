package com.task;


/**
 * 银行卡签约类
 * @author duzl
 *
 */
public class BankCardAgreeBean extends TranBaseBean{

    private static final long serialVersionUID = 1L;
    private String            oid_partner;          // 商户编号
    private String            busi_partner;         // 商户业务类型
    private String            pay_type;             // 支付方式
    private String            card_no;              // 银行卡号
    private String            bank_code;            // 银行编号
    private String            acct_name;            // 银行账号姓名
    private String            bind_mob;             // 绑定手机号
    private String            id_type;              // 证件类型
    private String            id_no;                // 证件号码
    private String            verify_code;          // 短信验证码
    private String            oid_paybill;          // 支付单号
    private String            result_pay;           // 支付结果
    private String            settle_date;          // 清算日期
    private String            oid_userno;           // 用户编号
    private String            valid_order;          // 订单有效期
    private String            token;                // 授权码
    private String            times_errmsg;         // 验证码输错次数
    private String            agreementno;          // 协议编号

    private String            user_id;              // 商户唯一ID
    private String            no_agree;             // 银通协议编号

    // 360 API 改造参数
    private String            api_version;          // 版本标识

    private String            platform;             // 平台来源标示
    
    
    private String            repayment_plan;       // 还款计划
    private String            repayment_no;         // 还款计划编号
    private String            sms_param;            // 短信参数
    
    private String            flag_sms_verify;     // 短信验证标识

    
    public String getOid_partner() {
		return oid_partner;
	}

	public void setOid_partner(String oid_partner) {
		this.oid_partner = oid_partner;
	}

	public String getBusi_partner() {
		return busi_partner;
	}

	public void setBusi_partner(String busi_partner) {
		this.busi_partner = busi_partner;
	}

	public String getPay_type() {
		return pay_type;
	}

	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}

	public String getCard_no() {
		return card_no;
	}

	public void setCard_no(String card_no) {
		this.card_no = card_no;
	}

	public String getBank_code() {
		return bank_code;
	}

	public void setBank_code(String bank_code) {
		this.bank_code = bank_code;
	}

	public String getAcct_name() {
		return acct_name;
	}

	public void setAcct_name(String acct_name) {
		this.acct_name = acct_name;
	}

	public String getBind_mob() {
		return bind_mob;
	}

	public void setBind_mob(String bind_mob) {
		this.bind_mob = bind_mob;
	}

	public String getId_type() {
		return id_type;
	}

	public void setId_type(String id_type) {
		this.id_type = id_type;
	}

	public String getId_no() {
		return id_no;
	}

	public void setId_no(String id_no) {
		this.id_no = id_no;
	}

	public String getVerify_code() {
		return verify_code;
	}

	public void setVerify_code(String verify_code) {
		this.verify_code = verify_code;
	}

	public String getOid_paybill() {
		return oid_paybill;
	}

	public void setOid_paybill(String oid_paybill) {
		this.oid_paybill = oid_paybill;
	}

	public String getResult_pay() {
		return result_pay;
	}

	public void setResult_pay(String result_pay) {
		this.result_pay = result_pay;
	}

	public String getSettle_date() {
		return settle_date;
	}

	public void setSettle_date(String settle_date) {
		this.settle_date = settle_date;
	}

	public String getOid_userno() {
		return oid_userno;
	}

	public void setOid_userno(String oid_userno) {
		this.oid_userno = oid_userno;
	}

	public String getValid_order() {
		return valid_order;
	}

	public void setValid_order(String valid_order) {
		this.valid_order = valid_order;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getTimes_errmsg() {
		return times_errmsg;
	}

	public void setTimes_errmsg(String times_errmsg) {
		this.times_errmsg = times_errmsg;
	}

	public String getAgreementno() {
		return agreementno;
	}

	public void setAgreementno(String agreementno) {
		this.agreementno = agreementno;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getNo_agree() {
		return no_agree;
	}

	public void setNo_agree(String no_agree) {
		this.no_agree = no_agree;
	}

	public String getApi_version() {
		return api_version;
	}

	public void setApi_version(String api_version) {
		this.api_version = api_version;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getRepayment_plan() {
		return repayment_plan;
	}

	public void setRepayment_plan(String repayment_plan) {
		this.repayment_plan = repayment_plan;
	}

	public String getRepayment_no() {
		return repayment_no;
	}

	public void setRepayment_no(String repayment_no) {
		this.repayment_no = repayment_no;
	}

	public String getSms_param() {
		return sms_param;
	}

	public void setSms_param(String sms_param) {
		this.sms_param = sms_param;
	}

	public String getFlag_sms_verify() {
		return flag_sms_verify;
	}

	public void setFlag_sms_verify(String flag_sms_verify) {
		this.flag_sms_verify = flag_sms_verify;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
    
    
}
