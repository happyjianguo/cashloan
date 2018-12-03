package com.xiji.cashloan.cl.mapper;

import java.util.Map;

import com.xiji.cashloan.cl.domain.UserBlackInfo;
import org.apache.ibatis.annotations.Param;

import com.xiji.cashloan.core.common.mapper.BaseMapper;
import com.xiji.cashloan.core.common.mapper.RDBatisDao;

/**
 * 黑名单Dao
 * 
 * @author caitt
 * @version 1.0.0
 * @date 2017-07-12 15:23:07
 * Copyright 杭州融都科技股份有限公司  cashloan All Rights Reserved
 * 官方网站：www.xiji.com
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@RDBatisDao
public interface UserBlackInfoMapper extends BaseMapper<UserBlackInfo, Long> {

	UserBlackInfo findByIdNo(@Param("idNo")String idNo);
	
	int deleteByID(Long id);
	
	int deleteByPhone(Map<String,Object> params);
	
	int deleteByIdNo(Map<String,Object> params);
}
