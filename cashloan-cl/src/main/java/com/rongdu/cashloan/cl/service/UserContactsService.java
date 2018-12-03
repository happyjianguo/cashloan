package com.rongdu.cashloan.cl.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
import com.rongdu.cashloan.cl.domain.UserContacts;
import com.rongdu.cashloan.core.common.service.BaseService;

/**
 * 用户资料--联系人Service
 * 
 * @author chenxy
 * @version 1.0.0
 * @date 2017-03-04 11:52:26
 * Copyright 杭州融都科技股份有限公司  arc All Rights Reserved
 * 官方网站：www.erongdu.com
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
public interface UserContactsService extends BaseService<UserContacts, Long>{

	/**
	 * 查询通讯录
	 * @param userId
	 * @param current
	 * @param pageSize
	 * @return
	 */
	Page<UserContacts> listContacts(long userId, int current, int pageSize,String name,String phone);

	/**
	 * 保存前删除原有记录
	 * @param userId 
	 * @param userId
	 * @param clUserContacts 
	 * @return
	 */
	boolean deleteAndSave(List<Map<String, Object>> infos, long userId);

}
