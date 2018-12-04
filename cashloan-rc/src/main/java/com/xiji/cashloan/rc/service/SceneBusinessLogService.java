package com.xiji.cashloan.rc.service;

import com.xiji.cashloan.core.common.service.BaseService;
import com.xiji.cashloan.rc.domain.SceneBusinessLog;

/**
 * 场景与第三方征信接口执行记录
 * @author wnb
 * @version 1.0.0
 * @date 2018/11/27
 * Copyright 杭州融都科技股份有限公司 现金贷  All Rights Reserved
 * 官方网站：www.xiji.com
 * 研发中心：rdc@xiji.com
 * 未经授权不得进行修改、复制、出售及商业使用
 */
public interface SceneBusinessLogService extends BaseService<SceneBusinessLog, Long> {

	
	/**
	 * 是否有未执行完的接口
	 * @return
	 */
	boolean haveNeedExcuteService(Long borrowId);
	
	/**
	 * 是否需要执行
	 * @param userId
	 * @param info
	 * @return true  有未完成的接口，false 没有未完成的接口
	 * @throws Exception
	 */
	boolean needExcute(Long userId, Long busId, String getWay, String period);
}
