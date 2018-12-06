package com.xiji.cashloan.cl.mapper;

import java.util.List;
import java.util.Map;

import com.xiji.cashloan.cl.domain.PayCheck;
import com.xiji.cashloan.cl.model.ManagePayCheckModel;
import com.xiji.cashloan.core.common.mapper.BaseMapper;
import com.xiji.cashloan.core.common.mapper.RDBatisDao;


/**
 * 资金对账记录Dao
 *
 * @author wnb
 * @version 1.0.0
 * @date 2018/12/03
 *
 *
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@RDBatisDao
public interface PayCheckMapper extends BaseMapper<PayCheck,Long> {

    
	/**
	 *
	 * @param searchMap
	 * @return
	 */
	 List<ManagePayCheckModel> page(Map<String, Object> searchMap);

}
