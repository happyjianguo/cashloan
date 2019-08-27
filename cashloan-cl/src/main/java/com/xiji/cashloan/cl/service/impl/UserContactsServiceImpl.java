package com.xiji.cashloan.cl.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.xiji.cashloan.cl.mapper.OperatorVoiceMapper;
import com.xiji.cashloan.cl.domain.UserContacts;
import com.xiji.cashloan.cl.mapper.UserAuthMapper;
import com.xiji.cashloan.cl.mapper.UserContactsMapper;
import com.xiji.cashloan.cl.model.OperatorVoiceModel;
import com.xiji.cashloan.core.common.context.Global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xiji.cashloan.cl.domain.UserAuth;
import com.xiji.cashloan.cl.service.UserContactsService;
import com.xiji.cashloan.core.common.mapper.BaseMapper;
import com.xiji.cashloan.core.common.service.impl.BaseServiceImpl;
import com.xiji.cashloan.core.common.util.ShardTableUtil;
import com.xiji.cashloan.core.common.util.StringUtil;


/**
 * 用户资料--联系人ServiceImpl
 *
 * @author wnb
 * @date 2018/11/27
 * @version 1.0.0
 *
 *
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
 
@Service("clUserContactsService")
public class UserContactsServiceImpl extends BaseServiceImpl<UserContacts, Long> implements UserContactsService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserContactsServiceImpl.class);
   
    @Resource
    private UserContactsMapper userContactsMapper;

    @Resource
    private OperatorVoiceMapper operatorVoiceMapper;
    
    @Resource
    private UserAuthMapper userAuthMapper;
    
	@Override
	public BaseMapper<UserContacts, Long> getMapper() {
		return userContactsMapper;
	}

	@Override
	public Page<UserContacts> listContacts(long userId, int current, int pageSize,String name,String phone) {
		// 分表
		String tableName = ShardTableUtil.generateTableNameById("cl_user_contacts", userId, 30000);
		int countTable = userContactsMapper.countTable(tableName);
		if (countTable == 0) {
			userContactsMapper.createTable(tableName);
		}
		PageHelper.startPage(current, pageSize);
		Map<String,Object> params = new HashMap<>();
		params.put("userId", userId);
		params.put("name", name);
		params.put("phone", phone);
		List<UserContacts> list = userContactsMapper.listShardSelective(tableName, params);
		return (Page<UserContacts>)list;
	}
	
	@Override
	public boolean deleteAndSave(List<Map<String, Object>> infos, long userId) {
		int msg = 0;
		String name = null;
		String phone = null;
		boolean flag = false;
		
		// 分表
		String tableName = ShardTableUtil.generateTableNameById("cl_user_contacts", userId, 30000);
		int countTable = userContactsMapper.countTable(tableName);
		if (countTable == 0) {
			userContactsMapper.createTable(tableName);
		}
		UserAuth auth = userAuthMapper.findByUserId(userId);
		String tableName1 = ShardTableUtil.generateTableNameById("cl_operator_voice", userId, 30000);
		for (Map<String, Object> map : infos) {
			logger.debug("保存用户userId："+userId+"通讯录，name："+StringUtil.isNull(map.get("name"))+"，phone："+StringUtil.isNull(map.get("phone")));
			name = StringUtil.isNull(map.get("name")).replaceAll("(null)", "").replace("()", "");
			phone = StringUtil.isNull(map.get("phone")).replaceAll("\\+86", "").replaceAll("-", "").replaceAll(" ", "");
			logger.debug("保存用户userId："+userId+"通讯录，name___："+name+"，phone___："+phone);
			if(StringUtil.isNotBlank(name) && name.length() <= 20 && StringUtil.isNotBlank(phone) && phone.length() <= 20){
				try {
					UserContacts userContacts = new UserContacts();
					userContacts.setUserId(userId);
					userContacts.setName(name);
					userContacts.setPhone(phone);
					if(auth.getPhoneState().equals("30")){

						String operatorSelect = Global.getValue("operator_select");
						OperatorVoiceModel voicesModel = new OperatorVoiceModel();
						if ("yunqiao".equals(operatorSelect)){
							if (StringUtil.isNotBlank(userContacts.getPhone()) && userContacts.getPhone().length() > 4){
								String phonePre;
								switch (userContacts.getPhone().length()){
									case 9: phonePre = phone.trim().substring(0,1);break;
									case 10: phonePre = phone.trim().substring(0,2);break;
									case 11: phonePre = phone.trim().substring(0,3);break;
									case 12: phonePre = phone.trim().substring(0,4);break;
									default: phonePre = phone.trim().substring(0,3);break;
								}
								String phoneSuffix = phone.substring(phone.length()-4,phone.length());
								voicesModel = operatorVoiceMapper.operatorVoicesCount3(tableName1, userId, phonePre,phoneSuffix);
							}
						}else {
							voicesModel = operatorVoiceMapper.operatorVoicesCount1(tableName, userId, phone);
						}
						userContacts.setTotalCount(voicesModel.getTotalCount());
						userContacts.setSumDuration(voicesModel.getSumDuration());
					} else {
						userContacts.setTotalCount(0);
						userContacts.setSumDuration(0);
					}
					msg = userContactsMapper.saveShard(tableName, userContacts);
				} catch (Exception e) {
					logger.error("保存用户userId："+userId+"通讯录异常， name： " + name + "， phone：" + phone);
				}
			} else {
				logger.error("保存用户userId："+userId+"通讯录失败，name： " + name + "， phone：" + phone);
			}
		}
		if (msg>0) {
			flag = true;
		}
		return flag;
	}
	
}