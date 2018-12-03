package com.rongdu.cashloan.cl.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.rongdu.cashloan.cl.model.dsdata.TKCreditRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.rongdu.cashloan.cl.domain.FireeyesBlackLog;
import com.rongdu.cashloan.cl.mapper.FireeyesBlackLogMapper;
import com.rongdu.cashloan.cl.service.FireeyesBlackLogService;
import com.rongdu.cashloan.core.common.mapper.BaseMapper;
import com.rongdu.cashloan.core.common.service.impl.BaseServiceImpl;
import com.rongdu.cashloan.core.common.util.DateUtil;
import com.rongdu.cashloan.core.common.util.StringUtil;
import com.rongdu.cashloan.core.domain.Borrow;
import com.rongdu.cashloan.core.domain.UserBaseInfo;
import com.rongdu.cashloan.core.service.UserBaseInfoService;
import com.rongdu.cashloan.rc.domain.TppBusiness;

import credit.CreditRequest;
import credit.Header;

/**
 * 火眼黑名单ServiceImpl
 * 
 * @author jchen
 * @version 1.0.0
 * @date 2017-07-05 14:53:05
 * Copyright 杭州融都科技股份有限公司  cashloan All Rights Reserved
 * 官方网站：www.erongdu.com
 * 未经授权不得进行修改、复制、出售及商业使用
 */
 
@Service("fireeyesBlackLogService")
public class FireeyesBlackLogServiceImpl extends BaseServiceImpl<FireeyesBlackLog, Long> implements FireeyesBlackLogService {
	
    private static final Logger logger = LoggerFactory.getLogger(FireeyesBlackLogServiceImpl.class);
   
    @Resource
    private FireeyesBlackLogMapper fireeyesBlackLogMapper;
    @Resource
    private UserBaseInfoService userBaseInfoService;

	@Override
	public BaseMapper<FireeyesBlackLog, Long> getMapper() {
		return fireeyesBlackLogMapper;
	}

	@Override
	public int queryFireeyesBlack(Borrow borrow, TppBusiness business) {
		int status = 0;
		try {
			JSONObject apiParamJson = JSONObject.parseObject(business.getExtend());
			final String APIHOST = business.getUrl();//正式接口地址

			UserBaseInfo baseInfo = userBaseInfoService.findByUserId(borrow.getUserId());//(long)3borrow.getUserId()
			Map<String, Object> payload = new HashMap<>();
			payload.put("name", baseInfo.getRealName());
			payload.put("idCard", baseInfo.getIdNo());
			payload.put("mobile", baseInfo.getPhone());
			TKCreditRequest tkCreditRequest = new TKCreditRequest(APIHOST, payload);

			FireeyesBlackLog blackLog = new FireeyesBlackLog();
			blackLog.setCreateTime(DateUtil.getNow());
			blackLog.setPhone(baseInfo.getPhone());
			blackLog.setUserId(borrow.getUserId());//3
			blackLog.setUserCard(baseInfo.getIdNo());
			blackLog.setUserName(baseInfo.getRealName());
			String result = tkCreditRequest.request();
			logger.debug("火眼黑名单返回数据："+result);
			if (StringUtil.isNotBlank(result)) {
				JSONObject resultJson = JSONObject.parseObject(result);
				String code = resultJson.getString("code");
				if (code.equals("200")) {
					blackLog.setOrderNo(resultJson.getString("orderNo"));
					blackLog.setRespCode(code);
					String res = resultJson.getString("res");
					if (StringUtil.isNotBlank(res)) {
						JSONObject resJson = JSONObject.parseObject(res);
						blackLog.setIsBlack(resJson.getString("status"));
						blackLog.setRespParams(resJson.getString("result"));
					}
				} else {
					blackLog.setRespCode(code);
					blackLog.setRespParams(resultJson.getString("message"));
				}
			}
			status = fireeyesBlackLogMapper.save(blackLog);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return status;
	}
	
}