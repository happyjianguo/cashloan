package com.xiji.cashloan.rc.mapper;

import org.apache.ibatis.annotations.Param;

import com.xiji.cashloan.core.common.mapper.BaseMapper;
import com.xiji.cashloan.core.common.mapper.RDBatisDao;
import com.xiji.cashloan.rc.domain.SimpleContactCount;

/**
 * 风控数据统计-（简）通讯录统计Dao
 *
 * @author wnb
 * @version 1.0.0
 * @date 2018/11/27
 *
 *
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@RDBatisDao
public interface SimpleContactCountMapper extends BaseMapper<SimpleContactCount, Long> {

	/**
	 * 通讯录总条数
	 * @param tableName
	 * @param userId
	 * @return
	 */
	int countOne(@Param("tableName") String tableName, @Param("userId") long userId);

}
