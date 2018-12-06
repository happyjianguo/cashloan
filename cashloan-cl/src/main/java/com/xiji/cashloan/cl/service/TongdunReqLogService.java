package com.xiji.cashloan.cl.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
import com.xiji.cashloan.cl.domain.TongdunReqLog;
import com.xiji.cashloan.cl.model.TongdunReqLogModel;
import com.xiji.cashloan.core.common.service.BaseService;
import com.xiji.cashloan.core.domain.Borrow;
import com.xiji.cashloan.rc.domain.TppBusiness;

/**
 * 同盾第三方请求记录Service
 *
 * @author wnb
 * @date 2018/11/27
 * @version 1.0.0
 *
 *
 *
 * 未经授权不得进行修改、复制、出售及商业使用
 */
public interface TongdunReqLogService extends BaseService<TongdunReqLog, Long>{
	/**
	 * 请求同盾返回结果
	 * @param userId
	 * @param borrow
	 * @return
	 */
	int preloanApplyRequest(Long userId, Borrow borrow,TppBusiness bussiness,String mobileType);
	/**
	 * 根据订单号查询同盾审核报告
	 * @param orderId
	 * @param bussiness
	 * @return
	 */
	String preloanReportRequest(String orderId,TppBusiness bussiness,String mobileType);
	/**
	 * 同盾请求记录列表
	 */
	Page<TongdunReqLogModel> pageListModel(Map<String, Object> params, int current,
			int pageSize);
	/**
	 * 同盾请求记录详细信息
	 * @param id
	 * @return
	 */
	TongdunReqLogModel getModelById(long id);
	/**
	 * 同盾请求记录查询
	 * @param params
	 * @return
	 */
	List<TongdunReqLogModel> listByMap(Map<String, Object> params);

}
