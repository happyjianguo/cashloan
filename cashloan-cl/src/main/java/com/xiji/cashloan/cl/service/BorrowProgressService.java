package com.xiji.cashloan.cl.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
import com.xiji.cashloan.cl.domain.BorrowProgress;
import com.xiji.cashloan.cl.model.ManageBorrowProgressModel;
import com.xiji.cashloan.core.common.service.BaseService;
import com.xiji.cashloan.core.domain.Borrow;

/**
 * 借款进度表Service
 *
 * @author wnb
 * @date 2018/11/27
 * @version 1.0.0
 * Copyright 杭州融都科技股份有限公司  arc All Rights Reserved
 * 官方网站：www.xiji.com
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
public interface BorrowProgressService extends BaseService<BorrowProgress, Long>{

	/**
	 * 进度查询
	 * @param borrow
	 * @return
	 */
	Map<String,Object> result(Borrow borrow);

	/**
	 * 后台还款进度列表
	 * @param params
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	Page<ManageBorrowProgressModel> listModel(Map<String, Object> params,
											  int currentPage, int pageSize);

	/**
	 * 保存借款进度
	 * @param borrowProgress
	 * @return
	 */
	boolean save(BorrowProgress borrowProgress);

	/**
	 * 查询列表
	 * @param map
	 * @return
	 */
	List<BorrowProgress> listSeletetiv(Map<String, Object> map);

	/**
	 * 查询当前借款逾期或坏账进度条数
	 * 
	 * @param borrowId
	 * @return
	 */
	int isNormalBorrowProgress(long borrowId);
}
