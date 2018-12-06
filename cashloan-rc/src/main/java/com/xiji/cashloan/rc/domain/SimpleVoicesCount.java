package com.xiji.cashloan.rc.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 风控数据统计-（简）通话记录统计实体
 *
 * @author wnb
 * @version 1.0.0
 * @date 2018/11/27
 *
 *
 * 未经授权不得进行修改、复制、出售及商业使用
 */
 public class SimpleVoicesCount implements Serializable {

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
    * 通话记录总次数
    */
    private Integer countOne;
    
    /**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 获取主键Id
	 * @return id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * 设置主键Id
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 获取用户标识
	 * @return userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * 设置用户标识
	 * @param userId
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * 获取通话记录总次数
	 * @return countOne
	 */
	public Integer getCountOne() {
		return countOne;
	}

	/**
	 * 设置通话记录总次数
	 * @param countOne
	 */
	public void setCountOne(Integer countOne) {
		this.countOne = countOne;
	}

	/**
	 * 获取创建时间
	 * @return createTime
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * 设置创建时间
	 * @param createTime
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}