package com.xiji.cashloan.cl.service.impl;

import javax.annotation.Resource;

import com.xiji.cashloan.cl.domain.MagicMultipoint;
import com.xiji.cashloan.cl.mapper.MagicMultipointMapper;
import com.xiji.cashloan.cl.service.MagicMultipointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.xiji.cashloan.core.common.mapper.BaseMapper;
import com.xiji.cashloan.core.common.service.impl.BaseServiceImpl;


/**
 * 魔杖2.0报告-基础信息表ServiceImpl
 * 
 * @author szb
 * @version 1.0.0
 * @date 2018-12-01 10:56:03
 */
 
@Service("magicMultipointService")
public class MagicMultipointServiceImpl extends BaseServiceImpl<MagicMultipoint, Long> implements MagicMultipointService {
	
    private static final Logger logger = LoggerFactory.getLogger(MagicMultipointServiceImpl.class);
   
    @Resource
    private MagicMultipointMapper magicMultipointMapper;

	@Override
	public BaseMapper<MagicMultipoint, Long> getMapper() {
		return magicMultipointMapper;
	}
	
}