package com.xiji.cashloan.cl.mapper;

import java.util.Map;

import com.xiji.cashloan.cl.domain.UserSdkLog;
import com.xiji.cashloan.core.common.mapper.BaseMapper;
import com.xiji.cashloan.core.common.mapper.RDBatisDao;


/**
 * sdk识别记录表Dao
 * 
 * @author lyang
 * @version 1.0.0
 * @date 2017-04-20 09:52:08
 * Copyright 杭州融都科技股份有限公司  arc All Rights Reserved
 * 官方网站：www.xiji.com
 * 创新一部：rdc@xiji.com
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@RDBatisDao
public interface UserSdkLogMapper extends BaseMapper<UserSdkLog,Long> {

	/**
	 * 查询当日可识别次数
	 * @param searchMap
	 * @return
	 */
	int countDayTime(Map<String, Object> searchMap);

    

}
