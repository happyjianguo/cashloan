package com.rongdu.cashloan.rule.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
import com.rongdu.cashloan.core.common.service.BaseService;
import com.rongdu.cashloan.rule.domain.RuleInfo;

/**
 * 规则信息Service
 * 
 * @author ctt
 * @version 1.0.0
 * @date 2016-12-20 13:58:48
 * Copyright 杭州融都科技股份有限公司  arc All Rights Reserved
 * 官方网站：www.erongdu.com
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
public interface RuleInfoService extends BaseService<RuleInfo, Long>{
	/**
	 * 查询
	 * @param map
	 * @return
	 */
	List<RuleInfo> findAll(Map<String, Object> map);
	/**
	 * 分页查询
	 * @param params
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	Page<RuleInfo> ruleList(Map<String, Object> params, int currentPage, int pageSize);
	/**
	 * 检验表是否已经保存过
	 * @param list
	 * @param table
	 * @return
	 */
	boolean checkTable(List<RuleInfo> list, String table);
	/**
	 * 检验表字段是否已经保存过
	 * @param list
	 * @param table
	 * @param column
	 * @return
	 */
	boolean checkColumn(List<RuleInfo> list, String table, String column);
	/**
	 * 编辑状态
	 * @param id
	 * @param state
	 * @return
	 */
	int modifyInfoState(Long id, String state);

}
