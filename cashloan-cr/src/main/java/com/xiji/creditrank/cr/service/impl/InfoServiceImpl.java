package com.xiji.creditrank.cr.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.xiji.creditrank.cr.domain.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xiji.cashloan.core.common.mapper.BaseMapper;
import com.xiji.cashloan.core.common.service.impl.BaseServiceImpl;
import com.xiji.creditrank.cr.mapper.InfoMapper;
import com.xiji.creditrank.cr.model.InfoModel;
import com.xiji.creditrank.cr.service.InfoService;


/**
 * 评分关联表ServiceImpl
 *
 * @author wnb
 * @version 1.0.0
 * @date 2018/11/27
 * Copyright  All Rights Reserved
 *
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
 
@Service("infoService")
public class InfoServiceImpl extends BaseServiceImpl<Info, Long> implements InfoService {
	
    @SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(InfoServiceImpl.class);
   
    @Resource
    private InfoMapper infoMapper;




	@Override
	public BaseMapper<Info, Long> getMapper() {
		return infoMapper;
	}




	@Override
	public Info findByTbNid(String tbNid) {
		return infoMapper.findByTbNid(tbNid);
	}




	@Override
	public int save(String tbNid, String tbName, String detail) {
		Info info = new Info();
		info.setTbNid(tbNid);
		info.setTbName(tbName);
		info.setDetail(detail);
		info.setState("10");
		info.setAddTime(new Date());
		return infoMapper.save(info);
	}




	@Override
	public Page<InfoModel> page(Map<String, Object> secreditrankhMap, int current,
			int pageSize) {
		PageHelper.startPage(current, pageSize);
		List<InfoModel> list = infoMapper.listSelect(secreditrankhMap);
		return (Page<InfoModel>)list;
	}




	@Override
	public int updateSelective(Map<String, Object> updateMap) {
		return infoMapper.updateSelective(updateMap);
	}




	@Override
	public List<Info> listSelective(Map<String, Object> map) {
		return infoMapper.listSelective(map);
	}
	
	 
	@Override
	public List<Map<String, Object>> findTable() {
		// TODO Auto-generated method stub
		List<Map<String, Object>> list = infoMapper.findTable();
		return list;
	}
	
	
	@Override
	public List<Map<String, Object>> findColumnByName(Map<String, Object> map) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> list = infoMapper.findColumnByName(map);
		return list;
	}




	@Override
	public Info findByPrimary(long id) {
		return infoMapper.findByPrimary(id);
	}
}