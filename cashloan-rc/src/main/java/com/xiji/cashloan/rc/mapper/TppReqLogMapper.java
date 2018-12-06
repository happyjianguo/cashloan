package com.xiji.cashloan.rc.mapper;

import java.util.List;
import java.util.Map;

import com.xiji.cashloan.core.common.mapper.BaseMapper;
import com.xiji.cashloan.core.common.mapper.RDBatisDao;
import com.xiji.cashloan.rc.domain.TppReqLog;
import com.xiji.cashloan.rc.model.TppReqLogModel;

/**
 * 第三方征信请求记录Dao
 *
 * @author wnb
 * @version 1.0.0
 * @date 2018/11/27
 *
 *
 *
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@RDBatisDao
public interface TppReqLogMapper extends BaseMapper<TppReqLog,Long> {

	/**
	 * 根据订单号修改记录
	 * @param log
	 * @return
	 */
	int modifyTppReqLog(TppReqLog log);

	/**
	 * 分页查询
	 * @param searchMap
	 * @return
	 */
	List<TppReqLogModel> page(Map<String, Object> searchMap);

}
