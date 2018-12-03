package com.rongdu.cashloan.cl.mapper;

import java.util.List;
import java.util.Map;

import com.rongdu.cashloan.cl.model.DayPassApr;
import com.rongdu.cashloan.cl.model.SystemDayData;
import com.rongdu.cashloan.core.common.mapper.RDBatisDao;

/**
 * 平台数据日报
 * @author caitt
 * @version 1.0
 * @date 2017年3月20日下午5:18:19
 * Copyright 杭州融都科技股份有限公司 现金贷  All Rights Reserved
 * 官方网站：www.erongdu.com
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@RDBatisDao
public interface SystemRcMapper {

	/**
	 * 平台数据日报
	 * @return
	 */
	List<SystemDayData> dayData(Map<String,Object> params);
	
	/**
	 * 每日通过率
	 */
	List<DayPassApr> dayApr(Map<String,Object> params);
}
