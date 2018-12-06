package com.xiji.cashloan.cl.model.pay.lianlian;

/**
 * 短信参数model
 *
 * @author wnb
 * @date 2018/11/30
 * @version 1.0.0
 *
 *
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
public class SmsParams {

	/**
	 * 备注
	 */
	private String contract_type;
	
	/**
	 * 号码
	 */
	private String contact_way;

	/** 
	 * 获取备注
	 * @return contract_type
	 */
	public String getContract_type() {
		return contract_type;
	}

	/** 
	 * 设置备注
	 * @param contract_type
	 */
	public void setContract_type(String contract_type) {
		this.contract_type = contract_type;
	}

	/** 
	 * 获取号码
	 * @return contact_way
	 */
	public String getContact_way() {
		return contact_way;
	}

	/** 
	 * 设置号码
	 * @param contact_way
	 */
	public void setContact_way(String contact_way) {
		this.contact_way = contact_way;
	}
	
	
}
