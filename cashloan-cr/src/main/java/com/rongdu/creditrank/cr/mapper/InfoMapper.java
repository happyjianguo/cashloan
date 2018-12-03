package com.rongdu.creditrank.cr.mapper;

import java.util.List;
import java.util.Map;

import com.rongdu.cashloan.core.common.mapper.BaseMapper;
import com.rongdu.cashloan.core.common.mapper.RDBatisDao;
import com.rongdu.creditrank.cr.domain.Info;
import com.rongdu.creditrank.cr.model.InfoModel;

/**
 * 评分关联表Dao
 * 
 * @author lyang
 * @version 1.0.0
 * @date 2017-01-04 15:05:09
 * Copyright 杭州融都科技股份有限公司  creditrank All Rights Reserved
 * 官方网站：www.erongdu.com
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@RDBatisDao
public interface InfoMapper extends BaseMapper<Info,Long> {

	/**
	 * 根据tbNid返回数据
	 * @param tbNid
	 * @return
	 */
    Info findByTbNid(String tbNid);

    /**
     * 列表查询返回InfoModel
     * @param secreditrankhMap
     * @return
     */
	List<InfoModel> listSelect(Map<String, Object> secreditrankhMap);
	
	/**
     * 查询数据表信息
     */
	List<Map<String, Object>> findTable();

	/**
	 * 查询数据库表字段信息
	 */
	List<Map<String, Object>> findColumnByName(Map<String, Object> map);

}
