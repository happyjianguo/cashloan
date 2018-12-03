package com.rongdu.cashloan.rc.model;

import com.rongdu.cashloan.rc.domain.Tpp;

/**
 * 第三方征信信息Model - 后台管理  
 * 
 * @author gc
 * @version 1.0.0
 * @date 2017年3月18日 上午11:22:21
 * Copyright 杭州融都科技股份有限公司 Arc All Rights Reserved
 * 官方网站：www.erongdu.com
 * 创新一部：rdc@erongdu.com
 * 未经授权不得进行修改、复制、出售及商业使用
 */
public class ManageTppModel extends Tpp {

	private static final long serialVersionUID = 1L;

	/**
	 * 状态中文描述
	 */
	private String stateStr;

	/**
	 * 获取状态中文描述
	 * 
	 * @return stateStr
	 */
	public String getStateStr() {
		return stateStr;
	}

	/**
	 * 设置状态中文描述
	 * 
	 * @param stateStr
	 */
	public void setStateStr(String stateStr) {
		this.stateStr = stateStr;
	}

}
