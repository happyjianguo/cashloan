package com.xiji.cashloan.cl.mapper;

import java.util.List;
import java.util.Map;

import com.xiji.cashloan.cl.model.DayNeedAmountModel;
import com.xiji.cashloan.cl.model.ExpendDetailModel;
import com.xiji.cashloan.cl.model.IncomeAndExpendModel;
import com.xiji.cashloan.cl.model.IncomeDetailModel;
import com.xiji.cashloan.core.common.mapper.RDBatisDao;

/**
 * 统计管理
 * @author wnb
 * @version 1.0.0
 * @date 2018/12/03
 *
 *
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@RDBatisDao
public interface StatisticManageMapper {

	/**
	 * 每日收支记录
	 * @return
	 */
	List<IncomeAndExpendModel> repayIncomeAndExpend(Map<String,Object> params);
	
	/**
	 * 每日未还本金
	 * @return
	 */
	List<DayNeedAmountModel> dayNeedAmount(Map<String,Object> params);
	
	/**
	 * 收入明细
	 * @return
	 */
	List<IncomeDetailModel> incomeDetail(Map<String,Object> params);
	
	/**
	 * 收入总额
	 * @param params
	 * @return
	 */
	Double incomeSum(Map<String,Object> params);
	
	/**
	 * 支出明细
	 * @return
	 */
	List<ExpendDetailModel> expendDetail(Map<String,Object> params);
	
	/**
	 * 支出总额
	 * @param params
	 * @return
	 */
	Double expendSum(Map<String,Object> params);
}
