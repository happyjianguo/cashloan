package com.xiji.cashloan.cl.mapper;

import java.util.List;

import com.xiji.cashloan.cl.domain.Adver;
import com.xiji.cashloan.core.common.mapper.BaseMapper;
import com.xiji.cashloan.core.common.mapper.RDBatisDao;

/**
 * 广告Dao
 * 
 * @author wmc
 * @version 1.0.0
 * @date 2017-06-21 14:33:20
 * Copyright 杭州融都科技股份有限公司  cashloan All Rights Reserved
 * 官方网站：www.xiji.com
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@RDBatisDao
public interface AdverMapper extends BaseMapper<Adver, Long> {
	
	
	List<Adver> getBanner();

}
