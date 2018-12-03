package com.rongdu.cashloan.cl.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.rongdu.cashloan.core.common.mapper.BaseMapper;
import com.rongdu.cashloan.core.common.service.impl.BaseServiceImpl;
import com.rongdu.cashloan.cl.mapper.MagicSuspiciousOtherIdcardMapper;
import com.rongdu.cashloan.cl.domain.MagicSuspiciousOtherIdcard;
import com.rongdu.cashloan.cl.service.MagicSuspiciousOtherIdcardService;


/**
 * 魔杖2.0报告-身份证存疑ServiceImpl
 * 
 * @author szb
 * @version 1.0.0
 * @date 2018-12-01 11:56:46
 */
 
@Service("magicSuspiciousOtherIdcardService")
public class MagicSuspiciousOtherIdcardServiceImpl extends BaseServiceImpl<MagicSuspiciousOtherIdcard, Long> implements MagicSuspiciousOtherIdcardService {
	
    private static final Logger logger = LoggerFactory.getLogger(MagicSuspiciousOtherIdcardServiceImpl.class);
   
    @Resource
    private MagicSuspiciousOtherIdcardMapper magicSuspiciousOtherIdcardMapper;

	@Override
	public BaseMapper<MagicSuspiciousOtherIdcard, Long> getMapper() {
		return magicSuspiciousOtherIdcardMapper;
	}
	
}