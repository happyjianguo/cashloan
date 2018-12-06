package com.xiji.cashloan.rc.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
import com.xiji.cashloan.core.common.service.BaseService;
import com.xiji.cashloan.rc.domain.Statistics;

/**
 * 风控数据统计分类Service
 *
 * @author wnb
 * @version 1.0.0
 * @date 2018/11/27
 *
 *
 *
 * 未经授权不得进行修改、复制、出售及商业使用
 */
public interface StatisticsService extends BaseService<Statistics, Long> {

	/**
	 * 风控数据统计分类分页查询
	 * @param params
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	Page<Statistics> page(Map<String, Object> params, int currentPage, int pageSize);
	
	/**
	 * 查询有效数据分类--下拉框使用
	 * @param params
	 * @return
	 */
	List<Statistics> listAll();
}
