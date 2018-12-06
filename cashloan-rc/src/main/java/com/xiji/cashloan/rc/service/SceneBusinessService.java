package com.xiji.cashloan.rc.service;

import java.util.Map;

import com.github.pagehelper.Page;
import com.xiji.cashloan.core.common.service.BaseService;
import com.xiji.cashloan.rc.domain.SceneBusiness;
import com.xiji.cashloan.rc.model.ManageSceneBusinessModel;

/**
 * 场景与第三方征信接口关联关系Service
 *
 * @author wnb
 * @version 1.0.0
 * @date 2018/11/27
 *
 *
 *
 * 未经授权不得进行修改、复制、出售及商业使用
 */
public interface SceneBusinessService extends BaseService<SceneBusiness, Long> {

	/**
	 * 场景与第三方征信接口关联关系分页查询
	 * 
	 * @param params
	 * @param current
	 * @param pageSize
	 * @return
	 */
	Page<ManageSceneBusinessModel> page(Map<String, Object> paramMap,
										int current, int pageSize);
	
	/**
	 * 添加场景与第三方征信接口关联关系
	 * 
	 * @param sceneBusiness
	 * @return
	 */
	boolean save(SceneBusiness sceneBusiness);

	/**
	 * 修改场景与第三方征信接口关联关系
	 * 
	 * @param sceneBusiness
	 * @return
	 */
	boolean update(SceneBusiness sceneBusiness);

	/**
	 * 启用场景与第三方征信接口关联关系
	 * 
	 * @param id
	 * @return
	 */
	boolean enable(Long id);

	/**
	 * 禁用场景与第三方征信接口关联关系
	 * 
	 * @param id
	 * @return
	 */
	boolean disable(Long id);
	
	/**
	 * 校验场景接口关系是否存在
	 * @param scene
	 * @param businessId
	 * @return
	 */
	boolean validExist(String scene, Long businessId, String type);
}
