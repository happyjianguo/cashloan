package com.xiji.cashloan.cl.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.xiji.cashloan.cl.model.UserEducationInfoModel;
import com.xiji.cashloan.cl.service.UserEducationInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import tool.util.StringUtil;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xiji.cashloan.cl.domain.UserEducationInfo;
import com.xiji.cashloan.cl.mapper.UserEducationInfoMapper;
import com.xiji.cashloan.core.common.mapper.BaseMapper;
import com.xiji.cashloan.core.common.service.impl.BaseServiceImpl;
import com.xiji.cashloan.core.domain.UserBaseInfo;
import com.xiji.cashloan.core.mapper.UserBaseInfoMapper;
import com.xiji.cashloan.core.mapper.UserMapper;

/**
 * 学信查询记录表ServiceImpl
 *
 * @author wnb
 * @date 2018/11/27
 * @version 1.0.0
 *
 *
 *
 * 未经授权不得进行修改、复制、出售及商业使用
 */
 
@Service("userEducationService")
public class UserEducationInfoServiceImpl extends BaseServiceImpl<UserEducationInfo, Long> implements UserEducationInfoService {
	
    @SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(UserEducationInfoServiceImpl.class);
   
    @Resource
    private UserEducationInfoMapper userEducationInfoMapper;
    @Resource
	private UserBaseInfoMapper userBaseInfoMapper;
    @Resource
	private UserMapper userMapper;



	@Override
	public BaseMapper<UserEducationInfo, Long> getMapper() {
		return userEducationInfoMapper;
	}




	@Override
	public int save(UserEducationInfo ue) {
		int msg = 0;
		Map<String,Object> map = new HashMap<>();
		map.put("userId", ue.getUserId());
		UserBaseInfo ubi = userBaseInfoMapper.findSelective(map);
		if (StringUtil.isNotBlank(ubi.getEducation())) {
			if (ubi.getEducation().equals(ue.getEducationBackground())) {
				ue.setState("10");
			}else {
				ue.setState("20");
			}
			UserEducationInfo uei = userEducationInfoMapper.findSelective(map);
			if (uei==null) {
				msg = userEducationInfoMapper.save(ue);
			}else {
				ue.setId(uei.getId());
				msg = userEducationInfoMapper.update(ue);
			}
		}
		return msg;
	}




	@Override
	public int update(UserEducationInfo uei) {
		int msg = 0;
		Map<String,Object> map = new HashMap<>();
		map.put("userId", uei.getUserId());
		UserBaseInfo ubi = userBaseInfoMapper.findSelective(map);
		if (ubi!=null&&ubi.getEducation().equals(uei.getEducationBackground())) {
			uei.setState("10");
			UserEducationInfo ue = userEducationInfoMapper.findSelective(map);
			if (ue!=null) {
				msg = userEducationInfoMapper.update(ue);
			}
		}
		return msg;
	}




	@Override
	public Page<UserEducationInfoModel> list(Map<String, Object> searchMap,
											 int current, int pageSize) {
		PageHelper.startPage(current, pageSize);
		List<UserEducationInfoModel> list = userEducationInfoMapper.page(searchMap);
		for (UserEducationInfoModel model : list) {
			if ("10".equals(model.getState())) {
				model.setStateStr("匹配");
			}else if ("20".equals(model.getState())) {
				model.setStateStr("不匹配");
			}
		}
		return (Page<UserEducationInfoModel>)list;
	}
	
}