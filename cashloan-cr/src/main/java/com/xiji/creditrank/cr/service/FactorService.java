package com.xiji.creditrank.cr.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
import com.xiji.cashloan.core.common.exception.CreditException;
import com.xiji.cashloan.core.common.service.BaseService;
import com.xiji.creditrank.cr.domain.Factor;
import com.xiji.creditrank.cr.model.FactorModel;

/**
 * 评分因子Service
 *
 * @author wnb
 * @version 1.0.0
 * @date 2018/11/27
 * Copyright 杭州融都科技股份有限公司  creditrank All Rights Reserved
 * 官方网站：www.xiji.com
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
public interface FactorService extends BaseService<Factor, Long>{

	/**
	 * 根据factorName查询
	 * @param factorName
	 * @return
	 */
	Factor findByFactorName(String factorName);
	

	/**
	 * 分页查询
	 * @param secreditrankhMap
	 * @param current
	 * @param pageSize
	 * @return
	 */
	Page<FactorModel> page(Map<String, Object> secreditrankhMap, int current, int pageSize);
	
	/**
	 * 查询因子列表
	 * @param param
	 * @return
	 */
	List<FactorModel> listSelect(Map<String, Object> factor);

	/**
	 * 根据主键查询
	 * @param id
	 * @return
	 */
	Factor findByPrimary(long id);

	/**
	 * 根据itemId查询
	 * @param id
	 * @return
	 */
	List<Factor> findByItemId(long id);


	/**
	 * 保存数据
	 * @param factorMap
	 * @param list
	 * @return
	 * @throws CreditException
	 */
	Map<String, Object> save(Map<String, Object> factorMap, List<Map<String, Object>> list) throws CreditException;

	/**
	 * 修改因子和参数
	 * @param factorMap
	 * @param factor
	 * @param list
	 * @return
	 * @throws CreditException
	 */
	Map<String, Object> updateSelective(Map<String, Object> factorMap,
			List<Map<String, Object>> list) throws CreditException;


	/**
	 * 删除因子
	 * @param id
	 * @throws CreditException 
	 */
	Map<String, Object> deleteSelective(long id) throws CreditException;
	
	/**
	 * 引用删除
	 */
	int deleteSelective(Long id);

	/**
	 * 修改数据
	 * @param updateMap
	 * @return
	 */
	int updateSelective(Map<String, Object> updateMap);

	/**
	 * 查询所属项目因子最高分
	 * @param itemId
	 * @return
	 */
	int findSumScore(long itemId);

	/**
	 * 查询所有
	 * @param factor
	 * @return
	 */
	List<FactorModel> listFactorModel(Map<String, Object> factor);
}
