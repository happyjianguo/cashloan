package com.xiji.cashloan.cl.mapper;

import java.util.List;
import java.util.Map;

import com.xiji.cashloan.cl.model.UserAuthModel;
import org.apache.ibatis.annotations.Param;

import com.xiji.cashloan.cl.domain.UserAuth;
import com.xiji.cashloan.core.common.mapper.BaseMapper;
import com.xiji.cashloan.core.common.mapper.RDBatisDao;

/**
 * 用户Dao
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
public interface UserAuthMapper extends BaseMapper<UserAuth,Long> {

	List<UserAuthModel> listUserAuthModel(Map<String, Object> params);

	int updateByUserId(Map<String, Object> paramMap);
	
	UserAuth findByUserId(@Param("userId")Long userId);

//    public Map<String,Object> getAuthState(Map<String,Object> paramMap);

	Map<String,Object> executeSql(Map<String,Object> paramMap);
//	/**
//	 * 芝麻必填查询
//	 * 
//	 * @param authMap
//	 * @return
//	 */
//	Map<String, Object> getZmRequiredAuthState(Map<String, Object> authMap);
//
//	/**
//	 * 芝麻选填查询
//	 * 
//	 * @param authMap
//	 * @return
//	 */
//	Map<String, Object> getZmOptionalAuthState(Map<String, Object> authMap);
//
//	/**
//	 * 芝麻去除查询
//	 * 
//	 * @param authMap
//	 * @return
//	 */
//	Map<String, Object> getZmRemoveAuthState(Map<String, Object> authMap);
	

	int updatePhoneState(Map<String, Object> userAuth);

	/**
	 * 实名认证人数统计
	 * @param channelId
	 * @return
	 */
	String idCount(String channelId);

	/**
	 * 紧急联系人认证人数统计
	 * @param channelId
	 * @return
	 */
	String contactCount(String channelId);

	/**
	 * 运营商认证人数统计
	 * @param channelId
	 * @return
	 */
	String phoneCount(String channelId);

	/**
	 * 银行卡认证人数统计
	 * @param channelId
	 * @return
	 */
	String bankCount(String channelId);

	/**
	 * 芝麻认证人数统计
	 * @param channelId
	 * @return
	 */
	String zhimaCount(String channelId);

	/**
	 * 工作信息认证人数统计
	 * @param channelId
	 * @return
	 */
	String workCount(String channelId);

	

}
