package com.task;

import java.util.List;

/**
 * 付款 response bean
 * 
 */
public class ResponseBankCardBean extends BaseResponseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1198276503805567647L;

	private String            oid_partner;          // 商户编号
    private String            sign_type;         // 
    private String            pay_type;             // 支付方式
    private int               offset;              // 
    private String            user_id;              // 商户唯一ID
    private String 				no_agree;
    private String[]            agreement_list;
	
	

	


	



	public String[] getAgreement_list() {
		return agreement_list;
	}


	public void setAgreement_list(String[] agreement_list) {
		this.agreement_list = agreement_list;
	}


	public String getNo_agree() {
		return no_agree;
	}


	public void setNo_agree(String no_agree) {
		this.no_agree = no_agree;
	}


	public String getOid_partner() {
		return oid_partner;
	}


	public void setOid_partner(String oid_partner) {
		this.oid_partner = oid_partner;
	}


	public String getSign_type() {
		return sign_type;
	}


	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}


	public String getPay_type() {
		return pay_type;
	}


	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}


	public int getOffset() {
		return offset;
	}


	public void setOffset(int offset) {
		this.offset = offset;
	}


	public String getUser_id() {
		return user_id;
	}


	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PaymentResponseBean [oid_partner=");
		builder.append(oid_partner);
		builder.append(", ret_code=");
		builder.append(ret_code);
		builder.append(", ret_msg=");
		builder.append(ret_msg);
		builder.append(", agreement_list=");
		builder.append(agreement_list);
		builder.append("]");
		return builder.toString();
	}

}
