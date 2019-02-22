package com.xiji.cashloan.cl.mapper;

import com.xiji.cashloan.cl.domain.statistic.ChannelStatisticData;
import com.xiji.cashloan.core.common.mapper.BaseMapper;
import com.xiji.cashloan.core.common.mapper.RDBatisDao;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 渠道统计数据Dao
 * 
 * @author wnb
 * @version 1.0.0
 * @date 2019-02-15 15:40:18
 */
@RDBatisDao
public interface ChannelStatisticDataMapper extends BaseMapper<ChannelStatisticData, Long> {

    /**
     * 最近统计时间
     * @return
     */
    Date getLateTime();

    /**
     * 获取注册人数
     * @param params
     * @return
     */
    List<ChannelStatisticData> getUserRegister(Map<String,Object> params);

    /**
     * 获取 申请订单数
     * @param params
     * @return
     */
    List<ChannelStatisticData> getBorrowApplyCount(Map<String,Object> params);


    /**
     * 获取 机审通过数
     * @param params
     * @return
     */
    List<ChannelStatisticData> getMachineAuditPassCount(Map<String,Object> params);


    /**
     * 获取 机审拒绝数
     * @param params
     * @return
     */
    List<ChannelStatisticData> getMachineAuditNotPassCount(Map<String,Object> params);


    /**
     * 获取 人工审核通过数
     * @param params
     * @return
     */
    List<ChannelStatisticData> getReviewPassCount(Map<String,Object> params);

    /**
     *获取 人工审核拒绝数
     * @param params
     * @return
     */
    List<ChannelStatisticData> getReviewNotPassCount(Map<String,Object> params);

    /**
     * 获取 首贷放款笔数
     * @param params
     * @return
     */
    List<ChannelStatisticData> getFirstLoadCount(Map<String,Object> params);

    /**
     * 获取 复贷放款笔数
     * @param params
     * @return
     */
    List<ChannelStatisticData> getAgainLoadCount(Map<String,Object> params);

    /**
     * 获取 当日到期逾期笔数
     * @param params
     * @return
     */
    List<ChannelStatisticData> getExpireOverdueCount(Map<String,Object> params);


    /**
     * 获取 当日到期首贷逾期笔数
     * @param params
     * @return
     */
    List<ChannelStatisticData> getFirstExpireOverdueCount(Map<String,Object> params);

    /**
     * 获取当日到期展期数
     * @param params
     * @return
     */
    List<ChannelStatisticData> getExtendCount(Map<String,Object> params);


    /**
     * 获取当日到期展期逾期数
     * @param params
     * @return
     */
    List<ChannelStatisticData> getExtendOverdueCount(Map<String,Object> params);



    /**
     * 获取当日到期首贷展期数
     * @param params
     * @return
     */
    List<ChannelStatisticData> getFirstExtendCount(Map<String,Object> params);


    /**
     * 获取当日到期首贷展期逾期数
     * @param params
     * @return
     */
    List<ChannelStatisticData> getFirstExtendOverdueCount(Map<String,Object> params);


    /**
     * 获取当日到期首贷放款数
     * @param params
     * @return
     */
    List<ChannelStatisticData> getFirstExpireLoadCount(Map<String,Object> params);


    /**
     * 获取当日到期复贷放款数
     * @param params
     * @return
     */
    List<ChannelStatisticData> getAgainExpireLoadCount(Map<String,Object> params);
}
