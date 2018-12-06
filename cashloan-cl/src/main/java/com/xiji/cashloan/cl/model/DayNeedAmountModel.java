package com.xiji.cashloan.cl.model;

/**
 * 每日未还本金
 * @author wnb
 * @date 2018/11/30
 * @version 1.0.0
 *
 *
 *
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
public class DayNeedAmountModel {

	/**
	 * 未还本金
	 */
	private String amount;
	
	/**
	 * 逾期罚息
	 */
	private String penaltyAmount;
	
	/**
	 * 日期
	 */
	private String date;

	/** 
	 * 获取未还本金
	 * @return amount
	 */
	public String getAmount() {
		return amount;
	}

	/** 
	 * 设置未还本金
	 * @param amount
	 */
	public void setAmount(String amount) {
		this.amount = amount;
	}

	/** 
	 * 获取逾期罚息
	 * @return penaltyAmount
	 */
	public String getPenaltyAmount() {
		return penaltyAmount;
	}

	/** 
	 * 设置逾期罚息
	 * @param penaltyAmount
	 */
	public void setPenaltyAmount(String penaltyAmount) {
		this.penaltyAmount = penaltyAmount;
	}

	/** 
	 * 获取日期
	 * @return date
	 */
	public String getDate() {
		return date;
	}

	/** 
	 * 设置日期
	 * @param date
	 */
	public void setDate(String date) {
		this.date = date;
	}
	
	
}
