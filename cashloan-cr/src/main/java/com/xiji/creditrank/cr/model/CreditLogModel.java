package com.xiji.creditrank.cr.model;

import com.xiji.creditrank.cr.domain.CreditLog;


/** 
 * @author lyang
 * @version 1.0
 * @date 2017-1-20 下午2:14:39
 * Copyright 杭州融都科技股份有限公司 资产风控系统  All Rights Reserved
 * 官方网站：www.xiji.com
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
public class CreditLogModel extends CreditLog {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户状态
	 */
	private String type;
	
	@SuppressWarnings("unused")
	private String stateStr;
	
	/**
	 * 真实姓名
	 */
	private String realName;
	
	/**
	 * 手机号码
	 */
	private String phone;
	
	/**
	 * 身份证
	 */
	private String idNo;
	
	/**
	 * 可使用额度
	 */
	private String unuse;
	
	/**
	 * 额度类型
	 */
	private String creditName;
	
	
	public String convert(String type){
		String stateStr = null;
		if ("10".equals(type)) {
			stateStr=  "增加";
		}else if("20".equals(type)){
			stateStr=  "减少";
		}else if("30".equals(type)){
			stateStr=  "冻结";
		}else if("40".equals(type)){
			stateStr=  "解冻";
		}else if("70".equals(type)){
			stateStr=  "提额";
		}
		return stateStr;
	}
	
	/**
	 * 获取type
	 * @return type
	 */
	public String getType() {
		return type;
	}


	/**
	 * 设置type
	 * @param type
	 */
	public void setType(String type) {
		this.type = convert(type);
	}

	/**
	 * 获取realName
	 * @return realName
	 */
	public String getRealName() {
		return realName;
	}

	/**
	 * 设置realName
	 * @param realName
	 */
	public void setRealName(String realName) {
		this.realName = realName;
	}

	/**
	 * 获取phone
	 * @return phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * 设置phone
	 * @param phone
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * 获取idNo
	 * @return idNo
	 */
	public String getIdNo() {
		return idNo;
	}

	/**
	 * 设置idNo
	 * @param idNo
	 */
	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	/**
	 * 获取unuse
	 * @return unuse
	 */
	public String getUnuse() {
		return unuse;
	}

	/**
	 * 设置unuse
	 * @param unuse
	 */
	public void setUnuse(String unuse) {
		this.unuse = unuse;
	}

	/**
	 * 获取creditType
	 * @return creditType
	 */
	public String getCreditName() {
		return creditName;
	}

	/**
	 * 设置creditType
	 * @param creditType
	 */
	public void setCreditName(String creditName) {
		this.creditName = creditName;
	}

	
}
