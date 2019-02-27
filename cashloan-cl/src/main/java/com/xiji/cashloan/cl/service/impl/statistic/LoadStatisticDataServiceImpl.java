package com.xiji.cashloan.cl.service.impl.statistic;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xiji.cashloan.cl.domain.statistic.LoadStatisticData;
import com.xiji.cashloan.cl.mapper.statistic.LoadStatisticDataMapper;
import com.xiji.cashloan.cl.service.statistic.LoadStatisticDataService;
import com.xiji.cashloan.cl.util.black.CollectionUtil;
import com.xiji.cashloan.core.common.mapper.BaseMapper;
import com.xiji.cashloan.core.common.service.impl.BaseServiceImpl;
import com.xiji.cashloan.core.common.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 放款统计数据ServiceImpl
 * 
 * @author wnb
 * @version 1.0.0
 * @date 2019-02-15 15:38:59
 */
 
@Service("loadStatisticDataService")
public class LoadStatisticDataServiceImpl extends BaseServiceImpl<LoadStatisticData, Long> implements LoadStatisticDataService {
	
    private static final Logger logger = LoggerFactory.getLogger(LoadStatisticDataServiceImpl.class);
   
    @Resource
    private LoadStatisticDataMapper loadStatisticDataMapper;

	@Override
	public BaseMapper<LoadStatisticData, Long> getMapper() {
		return loadStatisticDataMapper;
	}


	/**
	 * 最近时间
	 * @return
	 */
	public Date getLateDate(){
		return loadStatisticDataMapper.getLateDate();
	}

	/**
	 * 查询 放款统计数据
	 * @param params
	 * @return
	 */
	public List<LoadStatisticData> listLoadStatisticData(Map<String,Object> params){
		return loadStatisticDataMapper.listLoadStatisticData(params);
	}

	/**
	 * 查询 放款统计数据
	 * @param params
	 * @return
	 */
	@Override
	public Page<LoadStatisticData> listLoadStatistic(Map<String,Object> params, Integer current, Integer pageSize){
		PageHelper.startPage(current, pageSize);
		Page<LoadStatisticData> loadStatisticData = (Page<LoadStatisticData>) loadStatisticDataMapper.listLoadStatistic(params);
		if (CollectionUtil.isNotEmpty(loadStatisticData)){
			for(LoadStatisticData statisticData :loadStatisticData){
				statisticData.setCountTimeStr(DateUtil.dateStr(statisticData.getCountTime(),DateUtil.DATEFORMAT_STR_002));
			}
		}
		return loadStatisticData;
	}
	
}