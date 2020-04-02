package com.xiji.cashloan.cl.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 指迷模型分实体
 * 
 * @author szb
 * @version 1.0.0
 * @date 2019-05-05 20:09:28
 */
 public class ZmModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键Id
    */
    private Long id;

    /**
    * 用户标识
    */
    private Long userId;

    /**
    * 借款订单id
    */
    private Long borrowId;

    /**
    * 模型分
    */
    private Double score;

    /**
    * 请求流水号
    */
    private String requestId;

    /**
    * 创建时间
    */
    private Date createTime;


    /**
    * 获取主键Id
    *
    * @return id
    */
    public Long getId(){
        return id;
    }

    /**
    * 设置主键Id
    * 
    * @param 要设置的主键Id
    */
    public void setId(Long id){
        this.id = id;
    }

    /**
    * 获取用户标识
    *
    * @return 用户标识
    */
    public Long getUserId(){
        return userId;
    }

    /**
    * 设置用户标识
    * 
    * @param userId 要设置的用户标识
    */
    public void setUserId(Long userId){
        this.userId = userId;
    }

    /**
    * 获取借款订单id
    *
    * @return 借款订单id
    */
    public Long getBorrowId(){
        return borrowId;
    }

    /**
    * 设置借款订单id
    * 
    * @param borrowId 要设置的借款订单id
    */
    public void setBorrowId(Long borrowId){
        this.borrowId = borrowId;
    }

    /**
    * 获取模型分
    *
    * @return 模型分
    */
    public Double getScore(){
        return score;
    }

    /**
    * 设置模型分
    * 
    * @param score 要设置的模型分
    */
    public void setScore(Double score){
        this.score = score;
    }

    /**
    * 获取请求流水号
    *
    * @return 请求流水号
    */
    public String getRequestId(){
        return requestId;
    }

    /**
    * 设置请求流水号
    * 
    * @param requestId 要设置的请求流水号
    */
    public void setRequestId(String requestId){
        this.requestId = requestId;
    }

    /**
    * 获取创建时间
    *
    * @return 创建时间
    */
    public Date getCreateTime(){
        return createTime;
    }

    /**
    * 设置创建时间
    * 
    * @param createTime 要设置的创建时间
    */
    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }

}