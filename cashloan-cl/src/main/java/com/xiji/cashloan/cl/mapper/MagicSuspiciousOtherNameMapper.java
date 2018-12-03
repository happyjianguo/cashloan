package com.xiji.cashloan.cl.mapper;

import com.xiji.cashloan.core.common.mapper.BaseMapper;
import com.xiji.cashloan.core.common.mapper.RDBatisDao;
import com.xiji.cashloan.cl.domain.MagicSuspiciousOtherName;

import java.util.List;

/**
 * 魔杖2.0报告-基础信息表Dao
 * 
 * @author szb
 * @version 1.0.0
 * @date 2018-12-01 10:56:05
 */
@RDBatisDao
public interface MagicSuspiciousOtherNameMapper extends BaseMapper<MagicSuspiciousOtherName, Long> {

    int saveBatch(List<MagicSuspiciousOtherName> magicSuspiciousOtherNames);

}
