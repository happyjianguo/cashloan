package com.xiji.cashloan.cl.mapper;

import java.util.List;
import java.util.Map;

import com.xiji.cashloan.cl.domain.TongdunReqLog;
import com.xiji.cashloan.cl.model.TongdunReqLogModel;
import com.xiji.cashloan.core.common.mapper.BaseMapper;
import com.xiji.cashloan.core.common.mapper.RDBatisDao;

/**
 * 同盾第三方请求记录Dao
 *
 * @author wnb
 * @version 1.0.0
 * @date 2018/12/03
 * Copyright 杭州融都科技股份有限公司  arc All Rights Reserved
 * 官方网站：www.xiji.com
 * 创新一部：rdc@xiji.com
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@RDBatisDao
public interface TongdunReqLogMapper extends BaseMapper<TongdunReqLog,Long> {

	List<TongdunReqLogModel> listModelByMap(Map<String, Object> params);

	TongdunReqLogModel findModelById(long id);

    

}
