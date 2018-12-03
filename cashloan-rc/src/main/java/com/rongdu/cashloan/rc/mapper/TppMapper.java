package com.rongdu.cashloan.rc.mapper;

import java.util.List;
import java.util.Map;

import com.rongdu.cashloan.core.common.mapper.BaseMapper;
import com.rongdu.cashloan.core.common.mapper.RDBatisDao;
import com.rongdu.cashloan.rc.domain.Tpp;
import com.rongdu.cashloan.rc.model.ManageTppModel;
import com.rongdu.cashloan.rc.model.TppModel;

/**
 * 第三方征信信息Dao
 * 
 * @author zlh
 * @version 1.0.0
 * @date 2017-03-14 13:41:05
 * Copyright 杭州融都科技股份有限公司  arc All Rights Reserved
 * 官方网站：www.erongdu.com
 * 创新一部：rdc@erongdu.com
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@RDBatisDao
public interface TppMapper extends BaseMapper<Tpp,Long> {

	/**
	 * 查询所有
	 * 
	 * @return
	 */
	List<TppModel> listAll();

	/**
	 * 条件查询List
	 * 
	 * @param paramMap
	 * @return
	 */
	List<ManageTppModel> list(Map<String, Object> paramMap);

	/**
	 * 遍历所有第三方信息
	 * 
	 * @return
	 */
	List<TppModel> listAllWithBusiness();
}
