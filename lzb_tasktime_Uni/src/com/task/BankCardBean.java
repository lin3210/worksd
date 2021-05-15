package com.task;


/**
 * 银行卡签约类
 * @author duzl
 *
 */
public class BankCardBean extends TranBaseBean{

    private static final long serialVersionUID = 1L;
    private String            oid_partner;          // 商户编号
    private String            sign_type;         // 
    private String            pay_type;             // 支付方式
    private int            offset;              // 
    private String            user_id;              // 商户唯一ID
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
    


    
  
    
}
