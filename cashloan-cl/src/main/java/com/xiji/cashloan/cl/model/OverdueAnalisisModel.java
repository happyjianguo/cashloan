package com.xiji.cashloan.cl.model;

import tool.util.NumberUtil;
import tool.util.StringUtil;

/**
 * 每月逾期分析
 * @author wnb
 * @date 2018/11/30
 * @version 1.0.0
 * Copyright 杭州融都科技股份有限公司 现金贷  All Rights Reserved
 * 官方网站：www.xiji.com
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
public class OverdueAnalisisModel {

	/**
	 * 月份
	 */
	private String date;
	
	/**
	 * 逾期笔数
	 */
	private String overdueCount;
	
	/**
	 * 逾期金额
	 */
	private String overdueAmt;
	
	/**
	 * 逾期罚息
	 */
	private String penaltyAmout;
	
	/**
	 * 催收笔数
	 */
	private String urgeRepayBorrow;
	
	/**
	 * 催收次数
	 */
	private String urgeRepayCount;
	
	/**
	 * 催收成功数
	 */
	private String urgeRepaySuccess;
	
	/**
	 * 坏账数
	 */
	private String badCount;
	
	/**
	 * 催收成功率
	 */
	private String apr;

	/** 
	 * 获取月份
	 * @return date
	 */
	public String getDate() {
		return date;
	}

	/** 
	 * 设置月份
	 * @param date
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/** 
	 * 获取逾期笔数
	 * @return overdueCount
	 */
	public String getOverdueCount() {
		return overdueCount;
	}

	/** 
	 * 设置逾期笔数
	 * @param overdueCount
	 */
	public void setOverdueCount(String overdueCount) {
		this.overdueCount = overdueCount;
	}

	/** 
	 * 获取逾期金额
	 * @return overdueAmt
	 */
	public String getOverdueAmt() {
		return overdueAmt;
	}

	/** 
	 * 设置逾期金额
	 * @param overdueAmt
	 */
	public void setOverdueAmt(String overdueAmt) {
		this.overdueAmt = overdueAmt;
	}

	/** 
	 * 获取逾期罚息
	 * @return penaltyAmout
	 */
	public String getPenaltyAmout() {
		return penaltyAmout;
	}

	/** 
	 * 设置逾期罚息
	 * @param penaltyAmout
	 */
	public void setPenaltyAmout(String penaltyAmout) {
		this.penaltyAmout = penaltyAmout;
	}

	/** 
	 * 获取催收笔数
	 * @return urgeRepayBorrow
	 */
	public String getUrgeRepayBorrow() {
		return urgeRepayBorrow;
	}

	/** 
	 * 设置催收笔数
	 * @param urgeRepayBorrow
	 */
	public void setUrgeRepayBorrow(String urgeRepayBorrow) {
		this.urgeRepayBorrow = urgeRepayBorrow;
	}

	/** 
	 * 获取催收次数
	 * @return urgeRepayCount
	 */
	public String getUrgeRepayCount() {
		return urgeRepayCount;
	}

	/** 
	 * 设置催收次数
	 * @param urgeRepayCount
	 */
	public void setUrgeRepayCount(String urgeRepayCount) {
		this.urgeRepayCount = urgeRepayCount;
	}

	/** 
	 * 获取催收成功数
	 * @return urgeRepaySuccess
	 */
	public String getUrgeRepaySuccess() {
		return urgeRepaySuccess;
	}

	/** 
	 * 设置催收成功数
	 * @param urgeRepaySuccess
	 */
	public void setUrgeRepaySuccess(String urgeRepaySuccess) {
		this.urgeRepaySuccess = urgeRepaySuccess;
	}

	/** 
	 * 获取坏账数
	 * @return badCount
	 */
	public String getBadCount() {
		return badCount;
	}

	/** 
	 * 设置坏账数
	 * @param badCount
	 */
	public void setBadCount(String badCount) {
		this.badCount = badCount;
	}

	/** 
	 * 获取催收成功率
	 * @return apr
	 */
	public String getApr() {
		String urgeRepayCount = this.getUrgeRepayCount();
		String urgeRepaySuccess = this.getUrgeRepaySuccess();
		if(StringUtil.isNotBlank(urgeRepayCount)&&StringUtil.isNotBlank(urgeRepaySuccess)){
			Double count = Double.valueOf(urgeRepayCount);
			Double success = Double.valueOf(urgeRepaySuccess);
			if(count>0 && success>0){
				Double aprValue = success/count;
				apr = NumberUtil.format2Str(aprValue*100);
			}else{
				apr = "0.00";
			}
		}
		return apr;
	}

	/** 
	 * 设置催收成功率
	 * @param apr
	 */
	public void setApr(String apr) {
		this.apr = apr;
	}
	
	
}
