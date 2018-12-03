package com.rongdu.cashloan.rc.service;

import com.rongdu.cashloan.core.common.service.BaseService;
import com.rongdu.cashloan.rc.domain.TppBusiness;
import com.rongdu.cashloan.rc.domain.Zx91Detail;

/**
 * 风控数据统计-91征信
 * 
 * @author caitt
 * @version 1.0.0
 * @date 2017-09-08 15:52:59
 * Copyright 杭州融都科技股份有限公司  cashloan All Rights Reserved
 * 官方网站：www.erongdu.com
 * 未经授权不得进行修改、复制、出售及商业使用
 */
public interface Zx91DetailService extends BaseService<Zx91Detail, Long>{

	int query91zx1003(String idNo, String realName, Long userId, TppBusiness business);
}
