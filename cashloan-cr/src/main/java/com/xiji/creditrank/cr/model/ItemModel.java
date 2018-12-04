package com.xiji.creditrank.cr.model;

import java.util.List;

import com.xiji.creditrank.cr.domain.Item;

/**
 * @author wnb
 * @version 1.0.0
 * @date 2018/11/27
 * Copyright 杭州融都科技股份有限公司 资产风控系统  All Rights Reserved
 * 官方网站：www.xiji.com
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
public class ItemModel extends Item{

	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("rawtypes")
	/**
	 * 分组
	 */
	private List children;
	
	/**
	 * 栏目
	 */
	private String article;
	
	/**
	 * 权重
	 */
	private String weight;

	/**
	 * 获取children
	 * @return children
	 */
	@SuppressWarnings("rawtypes")
	public List getChildren() {
		return children;
	}

	/**
	 * 设置children
	 * @param children
	 */
	@SuppressWarnings("rawtypes")
	public void setChildren(List children) {
		this.children = children;
	}

	/**
	 * 获取article
	 * @return article
	 */
	public String getArticle() {
		return article;
	}

	/**
	 * 设置article
	 * @param article
	 */
	public void setArticle(String article) {
		this.article = article;
	}

	/**
	 * 获取weight
	 * @return weight
	 */
	public String getWeight() {
		return weight;
	}

	/**
	 * 设置weight
	 * @param weight
	 */
	public void setWeight(String weight) {
		this.weight = weight;
	}

}
