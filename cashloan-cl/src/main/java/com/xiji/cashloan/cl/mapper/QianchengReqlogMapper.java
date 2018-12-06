package com.xiji.cashloan.cl.mapper;

import java.util.List;
import java.util.Map;

import com.xiji.cashloan.cl.domain.QianchengReqlog;
import org.apache.ibatis.annotations.Param;

import com.xiji.cashloan.cl.model.QianchengReqlogModel;
import com.xiji.cashloan.core.common.mapper.BaseMapper;
import com.xiji.cashloan.core.common.mapper.RDBatisDao;

/**
 * 浅橙借款申请审核Dao
 *
 * @author wnb
 * @version 1.0.0
 * @date 2018/12/03
 *
 *
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@RDBatisDao
public interface QianchengReqlogMapper extends BaseMapper<QianchengReqlog,Long> {
	
	/**
	 * 测试用（演示环境新增）
	 * @param qianchengReqlog
	 * @return
	 */
	int demoSave(QianchengReqlog qianchengReqlog);

    /**
     * 根据订单号查找日志
     * @param orderNo
     * @return
     */
	QianchengReqlog findByOrderNo(@Param("orderNo")String orderNo);
	
	
	/**
	 * 机审请求记录查询
	 * @param params
	 * return
	 */
	List<QianchengReqlogModel> listQianchengReqlog(Map<String, Object> params);
	
	/**
	 * 根据借款申请查找
	 * @param borrowId
	 * @return
	 */
	QianchengReqlog findByBorrowId(@Param("borrowId")Long borrowId);
	
}
