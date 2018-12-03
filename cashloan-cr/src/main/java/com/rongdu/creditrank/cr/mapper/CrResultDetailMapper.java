package com.rongdu.creditrank.cr.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.rongdu.cashloan.core.common.mapper.BaseMapper;
import com.rongdu.cashloan.core.common.mapper.RDBatisDao;
import com.rongdu.creditrank.cr.domain.CrResultDetail;
import com.rongdu.creditrank.cr.model.CrResultDetailModel;
import com.rongdu.creditrank.cr.model.CrResultFactorDetail;
import com.rongdu.creditrank.cr.model.CrResultItemDetail;

/**
 * 评分结果Dao
 * 
 * @author ctt
 * @version 1.0.0
 * @date 2017-01-05 16:46:34
 * Copyright 杭州融都科技股份有限公司  creditrank All Rights Reserved
 * 官方网站：www.erongdu.com
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@RDBatisDao
public interface CrResultDetailMapper extends BaseMapper<CrResultDetail,Long> {

	List<CrResultDetail> listResultDetail(Map<String, Object> secreditrankhMap);

	/**
	 * 统计因子得分
	 * @param resultId
	 * @return
	 */
	List<CrResultDetail> countFactorScore(@Param("resultId")Long resultId);
	
	/**
	 * 统计项目得分
	 * @param resultId
	 * @return
	 */
	List<CrResultDetail> countItemScore(@Param("resultId")Long resultId);
	
	/**
	 * 统计评分卡得分
	 * @param resultId
	 * @return
	 */
	List<CrResultDetail> countCardScore(@Param("resultId")Long resultId);
	
	/**
	 * 保存统计结果
	 * @param list
	 */
	void saveCountScore(List<CrResultDetail> list);
	
	/**
	 * 根据级别查询评分结果
	 * @param resultId
	 * @param level
	 * @return
	 */
	List<CrResultDetailModel> findDetail(@Param("resultId")Long resultId,@Param("level")String level);
	List<CrResultFactorDetail> findFactorDetail(@Param("resultId")Long resultId,@Param("level")String level);
	List<CrResultItemDetail> findItemDetail(@Param("resultId")Long resultId,@Param("level")String level);
}
