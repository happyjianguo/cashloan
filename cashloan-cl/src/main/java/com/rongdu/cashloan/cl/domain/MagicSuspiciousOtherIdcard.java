package com.rongdu.cashloan.cl.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 魔杖2.0报告-身份证存疑实体
 * 
 * @author szb
 * @version 1.0.0
 * @date 2018-12-01 11:56:46
 */
 public class MagicSuspiciousOtherIdcard implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键Id
    */
    private Long id;

    /**
    * 用户id
    */
    private Long userId;

    /**
    * 报告id
    */
    private String transId;

    /**
    * 身份证号
    */
    private String idcard;

    /**
    * 身份证是否命中黑灰名单 0-否 1-是
    */
    private Integer isblack;

    /**
    * 最后使用时间
    */
    private String latestUsedTime;

    /**
    * 存疑类型 mobile-手机号 device-设备
    */
    private String type;

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
    * 获取用户id
    *
    * @return 用户id
    */
    public Long getUserId(){
        return userId;
    }

    /**
    * 设置用户id
    * 
    * @param userId 要设置的用户id
    */
    public void setUserId(Long userId){
        this.userId = userId;
    }

    /**
    * 获取报告id
    *
    * @return 报告id
    */
    public String getTransId(){
        return transId;
    }

    /**
    * 设置报告id
    * 
    * @param transId 要设置的报告id
    */
    public void setTransId(String transId){
        this.transId = transId;
    }

    /**
    * 获取身份证号
    *
    * @return 身份证号
    */
    public String getIdcard(){
        return idcard;
    }

    /**
    * 设置身份证号
    * 
    * @param idcard 要设置的身份证号
    */
    public void setIdcard(String idcard){
        this.idcard = idcard;
    }

    /**
    * 获取身份证是否命中黑灰名单 0-否 1-是
    *
    * @return 身份证是否命中黑灰名单 0-否 1-是
    */
    public Integer getIsblack(){
        return isblack;
    }

    /**
    * 设置身份证是否命中黑灰名单 0-否 1-是
    * 
    * @param isblack 要设置的身份证是否命中黑灰名单 0-否 1-是
    */
    public void setIsblack(Integer isblack){
        this.isblack = isblack;
    }

    /**
    * 获取最后使用时间
    *
    * @return 最后使用时间
    */
    public String getLatestUsedTime(){
        return latestUsedTime;
    }

    /**
    * 设置最后使用时间
    * 
    * @param latestUsedTime 要设置的最后使用时间
    */
    public void setLatestUsedTime(String latestUsedTime){
        this.latestUsedTime = latestUsedTime;
    }

    /**
    * 获取存疑类型 mobile-手机号 device-设备
    *
    * @return 存疑类型 mobile-手机号 device-设备
    */
    public String getType(){
        return type;
    }

    /**
    * 设置存疑类型 mobile-手机号 device-设备
    * 
    * @param type 要设置的存疑类型 mobile-手机号 device-设备
    */
    public void setType(String type){
        this.type = type;
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