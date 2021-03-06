package com.xiji.cashloan.cl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xiji.cashloan.cl.domain.OperatorReport;
import com.xiji.cashloan.cl.domain.OperatorReqLog;
import com.xiji.cashloan.cl.domain.OperatorRespDetail;
import com.xiji.cashloan.cl.domain.UserAuth;
import com.xiji.cashloan.cl.mapper.UserAuthMapper;
import com.xiji.cashloan.cl.model.UserAuthModel;
import com.xiji.cashloan.cl.model.moxie.MxCreditRequest;
import com.xiji.cashloan.cl.service.*;
import com.xiji.cashloan.core.common.context.Global;
import com.xiji.cashloan.core.common.mapper.BaseMapper;
import com.xiji.cashloan.core.common.service.impl.BaseServiceImpl;
import com.xiji.cashloan.core.common.util.DateUtil;
import com.xiji.cashloan.core.common.util.StringUtil;
import com.xiji.cashloan.core.domain.UserBaseInfo;
import com.xiji.cashloan.core.mapper.UserBaseInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户认证信息表ServiceImpl
 *
 * @author wnb
 * @date 2018/11/27
 * @version 1.0.0
 *
 *
 *
 * 未经授权不得进行修改、复制、出售及商业使用
 */

@Service("userAuthService")
public class UserAuthServiceImpl extends BaseServiceImpl<UserAuth, Long> implements UserAuthService {

	private static final Logger logger = LoggerFactory.getLogger(UserAuthServiceImpl.class);

	@Resource
	private UserAuthMapper userAuthMapper;
	@Resource
	private OperatorReqLogService operatorReqLogService;
	@Resource
	private OperatorRespDetailService operatorRespDetailService;
	@Resource
	private OperatorService operatorService;
	@Resource
	private UserBaseInfoMapper userBaseInfoMapper;
	@Resource
	private OperatorReportService operatorReportService;

	@Override
	public BaseMapper<UserAuth, Long> getMapper() {
		return userAuthMapper;
	}

	@Override
	public UserAuth getUserAuth(Map<String, Object> paramMap) {
		UserAuth userAuth = userAuthMapper.findSelective(paramMap);
		String phoneState = userAuth.getPhoneState();
		OperatorReqLog operatorReqLog = operatorReqLogService.findLastRecord(paramMap);

		if (UserAuthModel.STATE_ERTIFICATION.equals(userAuth.getPhoneState()) &&
				null != operatorReqLog) {
			int resetTime = 10;
			int diffTime = DateUtil.minuteBetween(operatorReqLog.getCreateTime(),DateUtil.getNow());
			//判断是否超过10分钟。
			if (resetTime <= diffTime) {
				Map<String,Object> params = new HashMap<String, Object>();
				params.put("userId", userAuth.getUserId());
				UserBaseInfo baseInfo = userBaseInfoMapper.findSelective(params);

				String operatorSelect = Global.getValue("operator_select");

				if (!"moxie".equals(operatorSelect) && !"yunqiao".equals(operatorSelect)){
					logger.error("运营商选择异常,请选择 魔蝎或云桥");
					return userAuth;
				}
				String reportHost = null;
				String host = null;
				if ("moxie".equals(operatorSelect)){
					host = Global.getValue("mx_operator_mxdata");
					reportHost = Global.getValue("mx_operator_report");
				} else {
					host = Global.getValue("yq_operator_mxdata");
					reportHost = Global.getValue("yq_operator_report");
				}

				final String token = Global.getValue("mx_token");
				final String yq_token = Global.getValue("yq_token");
				Map<String, String> headMap = new HashMap<>();

				if ("moxie".equals(operatorSelect)){
					headMap.put("Authorization", "token" + " " + token);
				} else {
					headMap.put("Authorization", "token" + " " + yq_token);
				}

				host = host.replace("{mobile}", String.valueOf(baseInfo.getPhone()));
				host += "?task_id=" + operatorReqLog.getTaskId();
				String result = null;
				try {
					result = MxCreditRequest.get(host, headMap);
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}

				Map<String, String> reportHeadMap = new HashMap<>();
				if ("moxie".equals(operatorSelect)){
					reportHeadMap.put("Authorization", "token" + " " + token);
				}else {
					reportHeadMap.put("Authorization", "token" + " " + yq_token);
				}

				reportHost = reportHost.replace("{mobile}", String.valueOf(baseInfo.getPhone()));
				String reportResult = null;
				try {
					reportResult = MxCreditRequest.get(reportHost, reportHeadMap);
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}


				if(StringUtil.isNotBlank(result) && StringUtil.isNotBlank(reportResult)) {
					JSONObject json = JSON.parseObject(result);
					String code = json.getString("code");
					if("0".equals(code)){
						phoneState = UserAuthModel.STATE_VERIFIED;
						OperatorRespDetail oldDetail = operatorRespDetailService.getByTaskId(operatorReqLog.getTaskId());
						if(oldDetail == null) {
							OperatorRespDetail operatorRespDetail = new OperatorRespDetail(operatorReqLog.getId(), operatorReqLog.getTaskId(), result);
							operatorRespDetailService.insert(operatorRespDetail);
						} else {
							Map<String, Object> updateMap = new HashMap<>();
							updateMap.put("id", oldDetail.getId());
							updateMap.put("operatorData", result);
							operatorRespDetailService.updateSelective(updateMap);
						}
						operatorService.saveOperatorInfos(result, userAuth.getUserId(),DateUtil.getNow(), baseInfo.getPhone(), operatorReqLog.getId());


						OperatorReport oldReport = operatorReportService.getByTaskId(operatorReqLog.getTaskId());
						if (oldReport == null) {
							OperatorReport operatorReport = new OperatorReport(operatorReqLog.getUserId(), operatorReqLog.getId(), operatorReqLog.getTaskId(), reportResult);
							operatorReportService.insert(operatorReport);
						} else {
							Map<String, Object> updateMap = new HashMap<>();
							updateMap.put("id", oldReport.getId());
							updateMap.put("report", reportResult);
							updateMap.put("gmtModified", DateUtil.getNow());
							operatorReportService.updateSelective(updateMap);
						}
					} else {
						phoneState = UserAuthModel.STATE_NOT_CERTIFIED;
					}
				}
				Map<String, Object> modifyMap = new HashMap<String, Object>();
				modifyMap.put("phoneTime", DateUtil.getNow());
				modifyMap.put("userId", userAuth.getUserId());
				modifyMap.put("phoneState", phoneState);
				this.updateByUserId(modifyMap);
			}
		}
		userAuth.setPhoneState(phoneState);
		return userAuth;
	}

	@Override
	public Integer updateByUserId(Map<String, Object> paramMap) {
		return userAuthMapper.updateByUserId(paramMap);
	}

	@Override
	public Page<UserAuthModel> listUserAuth(Map<String, Object> params,
											int currentPage, int pageSize) {
		PageHelper.startPage(currentPage, pageSize);
		List<UserAuthModel> list = userAuthMapper.listUserAuthModel(params);
		return (Page<UserAuthModel>) list;
	}

	@Override
	public UserAuth findSelective(long userId) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		return userAuthMapper.findSelective(map);
	}

	/**
	 * 借款申请条件
	 * @param paramMap
	 * @return
	 */
	@Override
	public Map<String, Object> getAuthState(Map<String, Object> paramMap) {
		//定义需要的变量
		String resultSql="";//result返回值中的语句
		String qualifiedSql="";//qualified返回值中的语句
		int qualifiedCount=4;//基础必填项数量
		//芝麻信用sql语句拼接，需要sys_config表里设置的zhima_auth属性，10-去除 20-选填 30-必填
//		String zhima_auth=Global.getValue("zhima_auth");
//		if("30".equals(zhima_auth)){
//			resultSql="+IF (zhima_state = 30, 1, 0)";
//			qualifiedSql="+IF (zhima_state = 30, 1, 0)";
//			qualifiedCount++;
//		}else if("20".equals(zhima_auth)){
//			resultSql="+IF (zhima_state = 30, 1, 0)";
//		}
		//芝麻信用sql拼接结束
		//拼接整个sql
		String sql="	SELECT ("
				+ "IF (id_state = 30, 1, 0) +"
				+ "IF (phone_state = 30, 1, 0) +"
				+ "IF (contact_state = 30, 1, 0) +"
				+ "IF (bank_card_state = 30, 1, 0)"
				+ resultSql
				+ ") AS result,"
				+ Global.getValue("auth_total")+" AS total,"
				+ "IF ("
				+ "(IF (id_state = 30, 1, 0) +"
				+ "IF (phone_state = 30, 1, 0) +"
				+ "IF (contact_state = 30, 1, 0) +"
				+ "IF (bank_card_state = 30, 1, 0)"
				+ qualifiedSql
				+ ") = "
				+ qualifiedCount
				+ ",1,0) AS qualified "
				+ "FROM cl_user_auth "
				+ "WHERE user_id = "+paramMap.get("userId");
		paramMap=new HashMap<String,Object>();
		paramMap.put("sqlstring", sql);
		return userAuthMapper.executeSql(paramMap);
	}

	@Override
	public int updatePhoneState(Map<String, Object> userAuth) {
		return userAuthMapper.updatePhoneState(userAuth);
	}

	/**
	 * 根据时间更新认证状态
	 * @param userAuth
	 * @return
	 */
	@Override
	public int updateAuthByTime(Map<String, Object> userAuth){
		return userAuthMapper.updateAuthByTime(userAuth);
	}


	/**
	 *
	 *
	 *
	 * 	认证状态 - 未认证/未完善  10
	 * 	认证状态 - 认证中/完善中  20
	 *  认证状态 - 已认证/已完善  30
	 *
	 * 查询用户认证列表
	 * @param params
	 * @return
	 */
	@Override
	public List<UserAuthModel> listUserAuthModel(Map<String, Object> params){

		List<UserAuthModel> userAuthModels = userAuthMapper.listUserAuthModel(params);

		for(UserAuthModel userAuthModel : userAuthModels){
			if ("10".equals(userAuthModel.getIdState())){
				userAuthModel.setIdStateStr("未认证");
			}
			if ("20".equals(userAuthModel.getIdState())){
				userAuthModel.setIdStateStr("认证中");
			}
			if ("30".equals(userAuthModel.getIdState())){
				userAuthModel.setIdStateStr("已认证");
			}

			if ("10".equals(userAuthModel.getBankCardState())){
				userAuthModel.setBankCardStateStr("未认证");
			}
			if ("20".equals(userAuthModel.getBankCardState())){
				userAuthModel.setBankCardStateStr("认证中");
			}
			if ("30".equals(userAuthModel.getBankCardState())){
				userAuthModel.setBankCardStateStr("已认证");
			}

			if ("10".equals(userAuthModel.getContactState())){
				userAuthModel.setContactStateStr("未完善");
			}
			if ("20".equals(userAuthModel.getContactState())){
				userAuthModel.setContactStateStr("完善中");
			}
			if ("30".equals(userAuthModel.getContactState())){
				userAuthModel.setContactStateStr("已完善");
			}

			if ("10".equals(userAuthModel.getPhoneState())){
				userAuthModel.setPhoneStateStr("未认证");
			}
			if ("20".equals(userAuthModel.getPhoneState())){
				userAuthModel.setPhoneStateStr("认证中");
			}
			if ("30".equals(userAuthModel.getPhoneState())){
				userAuthModel.setPhoneStateStr("已认证");
			}

			if("1".equals(userAuthModel.getUserFlag())){
				userAuthModel.setUserFlag("是");
			}
			if("0".equals(userAuthModel.getUserFlag())){
				userAuthModel.setUserFlag("否");
			}
		}
		return userAuthModels;
	}
}
