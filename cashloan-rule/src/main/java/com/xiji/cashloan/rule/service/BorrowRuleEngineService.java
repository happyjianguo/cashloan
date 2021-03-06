package com.xiji.cashloan.rule.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
import com.xiji.cashloan.core.common.service.BaseService;
import com.xiji.cashloan.rule.domain.BorrowRuleConfig;
import com.xiji.cashloan.rule.domain.BorrowRuleEngine;

/**
 * 借款规则管理Service
 *
 * @author wnb
 * @version 1.0.0
 * @date 2018/11/27
 *
 *
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
public interface BorrowRuleEngineService extends BaseService<BorrowRuleEngine, Long>{

	/**
	 * 查询借款规则
	 * @param params
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	Page<BorrowRuleEngine> page(Map<String, Object> params, int currentPage, int pageSize);
	
	int insert(BorrowRuleEngine bre);
	
	int updateSelective(Map<String, Object> params);
	
	int deleteById(long id);
	
	/**
	 * 编辑借款规则(规则类型、借款类型和规则类型适用场景)
	 * @param bre
	 * @return
	 */
	int update(BorrowRuleEngine bre);
	 
	/**
	 * 新增或修改借款規則信息
	 * @param brc
	 * @param configlist
	 * @return
	 */
	int update(BorrowRuleEngine brc, List<BorrowRuleConfig> configlist);
}
