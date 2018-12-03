package com.rongdu.cashloan.cl.service.impl;


import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.rongdu.cashloan.cl.mapper.SystemRcMapper;
import com.rongdu.cashloan.cl.model.DayPassApr;
import com.rongdu.cashloan.cl.model.SystemDayData;
import com.rongdu.cashloan.cl.service.SystemRcService;


/**
 * 平台数据日报
 * @author caitt
 * @version 1.0
 * @date 2017年3月20日下午4:56:46
 * Copyright 杭州融都科技股份有限公司 现金贷  All Rights Reserved
 * 官方网站：www.erongdu.com
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@Service("systemRcService")
public class SystemRcServiceImpl implements SystemRcService {
	
	@Resource
	private SystemRcMapper systemRcMapper;

	@Override
	public Page<SystemDayData> findDayData(Map<String,Object> params,Integer current,Integer pageSize) {
		PageHelper.startPage(current, pageSize);
		Page<SystemDayData> page = (Page<SystemDayData>) systemRcMapper.dayData(params);
		return page;
	}

	@Override
	public Page<DayPassApr> findDayApr(Map<String,Object> params,Integer current, Integer pageSize) {
		PageHelper.startPage(current, pageSize);
		List<DayPassApr> list = (Page<DayPassApr>) systemRcMapper.dayApr(params);
		/*NumberFormat nf = NumberFormat.getInstance();
		for (DayPassApr dayPassApr : list) {
			dayPassApr.setBorrowPassApr(nf.format(Double.parseDouble(dayPassApr.getBorrowPassApr())));
			dayPassApr.setBorrowApr(nf.format(Double.parseDouble(dayPassApr.getBorrowApr())));
		}*/
		return (Page<DayPassApr>)list;
	}

}
