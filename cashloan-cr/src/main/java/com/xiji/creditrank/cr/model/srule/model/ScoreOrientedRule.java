package com.xiji.creditrank.cr.model.srule.model;

/**
 * 分值导向规则信息，在结果导向规则基础上增加评分处理
 * @author wnb
 * @version 1.0.0
 * @date 2018/11/27
 * Copyright 杭州融都科技股份有限公司 creditrank  All Rights Reserved
 * 官方网站：www.xiji.com
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
public class ScoreOrientedRule extends SimpleRule {
	
	/**
	 * 结果分值
	 */
	public String resultScore;
	
	/**
	 * 条件匹配时的分值
	 */
	public String score;

	/** 
	 * 获取结果分值
	 * @return resultScore
	 */
	public String getResultScore() {
		return resultScore;
	}

	/** 
	 * 设置结果分值
	 * @param resultScore
	 */
	public void setResultScore(String resultScore) {
		this.resultScore = resultScore;
	}

	/** 
	 * 获取条件匹配时的分值
	 * @return score
	 */
	public String getScore() {
		return score;
	}

	/** 
	 * 设置条件匹配时的分值
	 * @param score
	 */
	public void setScore(String score) {
		this.score = score;
	}

	
}
