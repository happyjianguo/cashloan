package com.xiji.cashloan.cl.service.impl;

import javax.annotation.Resource;

import com.xiji.cashloan.cl.domain.OperatorRespDetail;
import com.xiji.cashloan.cl.service.OperatorRespDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.xiji.cashloan.cl.mapper.OperatorRespDetailMapper;
import com.xiji.cashloan.core.common.mapper.BaseMapper;
import com.xiji.cashloan.core.common.service.impl.BaseServiceImpl;

import java.util.Map;


/**
 * 运营商认证通知详情表ServiceImpl
 * 
 * @author xx
 * @version 1.0.0
 * @date 2017-05-17 12:38:22
 * Copyright 杭州融都科技股份有限公司  cashloan All Rights Reserved
 * 官方网站：www.xiji.com
 * 未经授权不得进行修改、复制、出售及商业使用
 */
 
@Service("operatorRespDetailService")
public class OperatorRespDetailServiceImpl extends BaseServiceImpl<OperatorRespDetail, Long> implements OperatorRespDetailService {
	
    @SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(OperatorRespDetailServiceImpl.class);
   
    @Resource
    private OperatorRespDetailMapper operatorRespDetailMapper;


	@Override
	public BaseMapper<OperatorRespDetail, Long> getMapper() {
		return operatorRespDetailMapper;
	}

	@Override
	public OperatorRespDetail getByTaskId(String taskId) {
		return operatorRespDetailMapper.getByTaskId(taskId);
	}

	@Override
	public int updateSelective(Map<String, Object> params) {
		return operatorRespDetailMapper.updateSelective(params);
	}
}