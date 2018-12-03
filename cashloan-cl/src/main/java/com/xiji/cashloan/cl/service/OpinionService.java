package com.xiji.cashloan.cl.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
import com.xiji.cashloan.cl.domain.Opinion;
import com.xiji.cashloan.cl.model.OpinionModel;
import com.xiji.cashloan.core.common.service.BaseService;


/**
 * 意见反馈表Service
 * 
 * @author lyang
 * @version 1.0.0
 * @date 2017-02-14 11:30:57
 * Copyright 杭州融都科技股份有限公司  arc All Rights Reserved
 * 官方网站：www.xiji.com
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
public interface OpinionService extends BaseService<Opinion, Long> {
	
	int save(long userId, String opinion);
	
	int updateSelective(Map<String, Object> searchMap);

	List<Opinion> getOpinion(Map<String, Object> paramMap);
	
	Page<OpinionModel> page(Map<String, Object> searchMap, int current, int pageSize);

}
