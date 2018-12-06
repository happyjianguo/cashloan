package com.xiji.cashloan.cl.service;

import java.util.Map;

import com.xiji.cashloan.cl.domain.BankCard;
import com.xiji.cashloan.core.common.service.BaseService;

/**
 * 银行卡Service
 *
 * @author wnb
 * @date 2018/11/27
 * @version 1.0.0
 *
 *
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
public interface BankCardService extends BaseService<BankCard, Long>{

	/**
	 * 保存记录
	 * @param bankCard
	 * @return
	 */
	boolean save(BankCard bankCard);
	
	/**
	 * 据userId查询银行卡信息
	 * 
	 * @param userId
	 * @return
	 */
	BankCard getBankCardByUserId(Long userId);

	/**
	 * 据条件查询银行卡信息
	 * 
	 * @param paramMap
	 * @return
	 */
	BankCard findSelective(Map<String, Object> paramMap);

	/**
	 * 解约并修改银行卡
	 * 
	 * @param card
	 * @return
	 */
	int cancelAuthSign(BankCard card);
	
	/**
	 * 修改银行卡信息
	 * 
	 * @param paramMap
	 * @return
	 */
	boolean updateSelective(Map<String, Object> paramMap);

}
