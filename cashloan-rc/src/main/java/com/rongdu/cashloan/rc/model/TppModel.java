package com.rongdu.cashloan.rc.model;

import java.util.List;

import com.rongdu.cashloan.rc.domain.Tpp;
import com.rongdu.cashloan.rc.domain.TppBusiness;

/**
 * 第三方信息model
 * @author zlh
 * @version 1.0
 * @date 2017年3月14日 下午1:59:17
 * Copyright 杭州融都科技股份有限公司 arc All Rights Reserved
 * 官方网站：www.erongdu.com
 * 创新一部：rdc@erongdu.com
 * 未经授权不得进行修改、复制、出售及商业使用
 */
public class TppModel extends Tpp {

	private static final long serialVersionUID = 1L;

	/**
	 * 接口集合
	 */
	private List<TppBusiness> businessList;
	
	/**
	 * 获取 接口集合
	 * @return 
	 */
	public List<TppBusiness> getBusinessList() {
		return businessList;
	}

	/**
	 * 设置 接口集合
	 * @param 
	 */
	public void setBusinessList(List<TppBusiness> businessList) {
		this.businessList = businessList;
	}
}
