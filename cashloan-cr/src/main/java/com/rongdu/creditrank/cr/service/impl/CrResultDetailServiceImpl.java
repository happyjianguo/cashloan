package com.rongdu.creditrank.cr.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.rongdu.cashloan.core.common.mapper.BaseMapper;
import com.rongdu.cashloan.core.common.service.impl.BaseServiceImpl;
import com.rongdu.creditrank.cr.domain.CrResultDetail;
import com.rongdu.creditrank.cr.mapper.CrResultDetailMapper;
import com.rongdu.creditrank.cr.mapper.CrResultMapper;
import com.rongdu.creditrank.cr.model.CrResultDetailModel;
import com.rongdu.creditrank.cr.model.CrResultFactorDetail;
import com.rongdu.creditrank.cr.model.CrResultItemDetail;
import com.rongdu.creditrank.cr.service.CrResultDetailService;


/**
 * 评分结果ServiceImpl
 * 
 * @author ctt
 * @version 1.0.0
 * @date 2017-01-05 16:46:34
 * Copyright 杭州融都科技股份有限公司  creditrank All Rights Reserved
 * 官方网站：www.erongdu.com
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
 
@Service("crResultDetailService")
public class CrResultDetailServiceImpl extends BaseServiceImpl<CrResultDetail, Long> implements CrResultDetailService {
	
    @Resource
    private CrResultDetailMapper crResultDetailMapper;
    @Resource
    private CrResultMapper crResultMapper;

	@Override
	public BaseMapper<CrResultDetail, Long> getMapper() {
		return crResultDetailMapper;
	}

	@Override
	public Page<CrResultDetail> page(Map<String, Object> secreditrankhMap,int current, int pageSize) {
		PageHelper.startPage(current, pageSize);
		List<CrResultDetail> list = crResultDetailMapper.listResultDetail(secreditrankhMap);
		return (Page<CrResultDetail>)list;
	}

	@Override
	public List<CrResultDetailModel> listDetailTree(Long resultId) {
//		CrResult resutl =  crResultMapper.findByPrimary(resultId);
		List<CrResultDetailModel> cardList = crResultDetailMapper.findDetail(resultId, CrResultDetail.LEVEL_CARD);
		for(int i=0;i<cardList.size();i++){
			List<CrResultItemDetail> itemList = crResultDetailMapper.findItemDetail(resultId, CrResultDetail.LEVEL_ITEM);
			CrResultDetailModel cardDetail = cardList.get(i);
			cardList.set(i, cardDetail);
			for(int m=0;m<itemList.size();m++){
				List<CrResultFactorDetail> factorList = crResultDetailMapper.findFactorDetail(resultId, CrResultDetail.LEVEL_FACTOR);
				CrResultItemDetail itemDetail = itemList.get(m);
				itemDetail.setFactorList(factorList);
				itemList.set(m, itemDetail);
			}
			cardDetail.setItemList(itemList);
		}
		return cardList;
	}

	@Override
	public List<CrResultDetail> listInfo(Long cardId) {
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("cardId", cardId);
		return crResultDetailMapper.listSelective(paramMap);
	}
	
}