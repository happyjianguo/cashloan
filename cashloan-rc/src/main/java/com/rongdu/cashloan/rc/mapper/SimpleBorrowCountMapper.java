package com.rongdu.cashloan.rc.mapper;

import com.rongdu.cashloan.core.common.mapper.BaseMapper;
import com.rongdu.cashloan.core.common.mapper.RDBatisDao;
import com.rongdu.cashloan.rc.domain.SimpleBorrowCount;

/**
 * 风控数据统计-（简）借款统计Dao
 * 
 * @author xx
 * @version 1.0.0
 * @date 2017-07-06 18:12:18
 * Copyright 杭州融都科技股份有限公司  cashloan All Rights Reserved
 * 官方网站：www.erongdu.com
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@RDBatisDao
public interface SimpleBorrowCountMapper extends BaseMapper<SimpleBorrowCount, Long> {

    /**
     * 借款人有逾期30天以上已还借款数
     * @param userId
     * @return
     */
    int countOne(long userId);
    
//    /**
//     * 借款人有逾期未还借款数
//     * @param userId
//     * @return
//     */
//    int countTwo(long userId);
//    
//    /**
//     * 借款人有逾期已还借款数
//     * @param userId
//     * @return
//     */
//    int countThree(long userId);
//    
//    /**
//     * 借款人正常还款数
//     * @param userId
//     * @return
//     */
//    int countFour(long userId);

}
