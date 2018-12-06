package com.xiji.cashloan.cl.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.xiji.cashloan.cl.domain.QianchengReqlog;
import com.xiji.cashloan.cl.domain.UserContacts;
import com.xiji.cashloan.cl.domain.Zhima;
import com.xiji.cashloan.cl.service.RcQianchenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import tool.util.DateUtil;
import tool.util.StringUtil;
import vo.qiancheng.BasicInfo;
import vo.qiancheng.OrderDetail;
import vo.qiancheng.RiskData;
import vo.qiancheng.UserContact;
import vo.qiancheng.UserMobileContacts;
import vo.qiancheng.UserProofMateria;
import vo.qiancheng.ZmData;

import com.alibaba.fastjson.JSONObject;
import com.xiji.cashloan.cl.domain.UserEmerContacts;
import com.xiji.cashloan.cl.mapper.BorrowProgressMapper;
import com.xiji.cashloan.cl.mapper.ClBorrowMapper;
import com.xiji.cashloan.cl.mapper.OperatorReqLogMapper;
import com.xiji.cashloan.cl.mapper.QianchengReqlogMapper;
import com.xiji.cashloan.cl.mapper.UserContactsMapper;
import com.xiji.cashloan.cl.mapper.UserEmerContactsMapper;
import com.xiji.cashloan.cl.mapper.ZhimaMapper;
import com.xiji.cashloan.core.common.context.Global;
import com.xiji.cashloan.core.common.exception.BussinessException;
import com.xiji.cashloan.core.common.util.ShardTableUtil;
import com.xiji.cashloan.core.domain.Borrow;
import com.xiji.cashloan.core.domain.UserBaseInfo;
import com.xiji.cashloan.core.mapper.UserBaseInfoMapper;
import com.xiji.cashloan.core.mapper.UserMapper;
import com.xiji.cashloan.rc.domain.TppBusiness;

import credit.Header;
import credit.QianchengRiskRequest;

/**
 * 现金白卡风控接口
 *
 * @author wnb
 * @date 2018/11/27
 * @version 1.0.0
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@Service("rcQianchenService")
public class RcQianchenServiceImpl implements RcQianchenService {

	public static final Logger logger = LoggerFactory.getLogger(RcQianchenServiceImpl.class);

	@Resource
	private UserEmerContactsMapper userEmerContactsMapper;
	@Resource
	private UserMapper userMapper;
	@Resource
	private UserBaseInfoMapper userBaseInfoMapper;
	@Resource
	private UserContactsMapper userContactsMapper;
	@Resource
	private OperatorReqLogMapper operatorReqLogMapper;
	@Resource
	private QianchengReqlogMapper qianchengReqlogMapper;
	@Resource
	private BorrowProgressMapper borrowProgressMapper;
	@Resource
	private ClBorrowMapper clBorrowMapper;
	@Resource
	private ZhimaMapper zhimaMapper;

	@Override
	public String qianchenRiskRequest(Long userId,Borrow borrow,String operatorNo,String reqOrderNo,TppBusiness tpp) throws BussinessException {
		UserBaseInfo userBaseinfo = userBaseInfoMapper.findUserImags(userId);
		/*下面开始构造风控参数，建议尽可能的多填信息*/
        RiskData riskData = new RiskData();

		Date nowDate = DateUtil.getNow();
		String nowDateStr = DateUtil.dateStr(nowDate,DateUtil.DATEFORMAT_STR_001);
		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setAmount((int) (borrow.getAmount() * 100));// 单位是分，1元=100
		orderDetail.setLoan_term(Integer.valueOf(borrow.getTimeLimit()));
		orderDetail.setOrder_time(nowDateStr);
		orderDetail.setOrder_id(Long.valueOf(reqOrderNo));// 你自己的订单号
		riskData.setOrder_detail(orderDetail);

		/* 基本信息部分 */
		BasicInfo basicInfo = new BasicInfo();

		// 紧急联系人
		List<UserContact> ucList = new ArrayList<>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		List<UserEmerContacts> EmerContacts = userEmerContactsMapper.listSelective(params);
		UserContact userContact;
		for (UserEmerContacts info : EmerContacts) {
			userContact = new UserContact();
			userContact.setName(info.getName());
			userContact.setMobile(info.getPhone());
			if ("父亲".equals(info.getRelation())) {
				userContact.setRelation(0);
			} else if ("母亲".equals(info.getRelation())) {
				userContact.setRelation(1);
			} else if ("配偶".equals(info.getRelation())) {
				userContact.setRelation(2);
			} else if ("朋友".equals(info.getRelation())) {
				userContact.setRelation(3);
			} else {
				userContact.setRelation(4);
			}
			ucList.add(userContact);
		}

		basicInfo.setUser_contact(ucList);
		// basicInfo.setCompany_name("");
		// basicInfo.setCompany_address("杭州市拱墅区");
		// basicInfo.setCompany_phone("0571-85132616");
		//
		// basicInfo.setCur_address("杭州市拱墅区");
		// basicInfo.setPerm_address("杭州市拱墅区");
		riskData.setBasic_info(basicInfo);

		/* 人像图片部分 */
		List<UserProofMateria> user_proof_materia = new ArrayList<>();
		String picPath = Global.getValue("server_host") + "/readFile.htm?path=";
		UserProofMateria upm;
		// 活体照片
		if (StringUtil.isNotBlank(userBaseinfo.getLivingImg())) {
			upm = new UserProofMateria();
			upm.setUrl(picPath + userBaseinfo.getLivingImg());
			upm.setType(0);
			user_proof_materia.add(upm);
		}

		// 身份证正面
		if (StringUtil.isNotBlank(userBaseinfo.getFrontImg())) {
			upm = new UserProofMateria();
			upm.setUrl(picPath + userBaseinfo.getFrontImg());
			upm.setType(1);
			user_proof_materia.add(upm);
		}

		// 身份证反面
		if (StringUtil.isNotBlank(userBaseinfo.getBackImg())) {
			upm = new UserProofMateria();
			upm.setUrl(picPath + userBaseinfo.getBackImg());
			upm.setType(2);
			user_proof_materia.add(upm);
		}

		// 身份证信息
		riskData.setUser_proof_materia(user_proof_materia);

		/* 通讯录部分 */
		List<UserMobileContacts> user_mobile_contacts = new ArrayList<>();
		String tableName = ShardTableUtil.generateTableNameById("cl_user_contacts", userId, 30000);
		int countTable = userContactsMapper.countTable(tableName);
		if (countTable == 0) {
			userContactsMapper.createTable(tableName);
		}

		params = new HashMap<String, Object>();
		params.put("userId", userId);
		List<UserContacts> userContacts = userContactsMapper.listShardSelective(tableName, params);
		
		for (UserContacts contact : userContacts) {
			UserMobileContacts umc = new UserMobileContacts();
			umc.setMobile(contact.getPhone());
			umc.setName(contact.getName());
			user_mobile_contacts.add(umc);
		}
		riskData.setUser_mobile_contacts(user_mobile_contacts);

		/* 芝麻信用部分 */
		Zhima zhima = zhimaMapper.findByUserId(userId);
//		if(zhima != null){
//			ZmData zmData = new ZmData();
//			zmData.setZm_score(Integer.parseInt(zhima.getScore()+""));
//			riskData.setZm_data(zmData);
//		}
		try {
			if(zhima != null){
				ZmData zmData = new ZmData();
				double dScore = zhima.getScore();
				int score = (int)dScore;
				zmData.setZm_score(score);
				riskData.setZm_data(zmData);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}

		/* 主体部分 */
		long timestamp = new Date().getTime();
		JSONObject apiParamJson = JSONObject.parseObject(tpp.getExtend());
		final String APIKEY = apiParamJson.getString("apikey");
		final String SECRETKEY = apiParamJson.getString("secretkey");
		Header header = new Header(APIKEY, timestamp);
		final String url = tpp.getUrl();

		QianchengReqlog reqLog = new QianchengReqlog(reqOrderNo, borrow.getId(), userId, "10", nowDate);
		int logResult = qianchengReqlogMapper.save(reqLog);
		if(logResult == 1){
			logger.debug("保存浅橙请求记录成功" + "，borrowId:"+borrow.getId());
		} else {
			logger.debug("保存浅橙请求记录失败" + "，borrowId:"+borrow.getId());
		}
		
		String result = "";
		logger.debug("浅橙请求记录，reqLog.getId():"+reqLog.getId());
		if (reqLog.getId() > 0) {
			QianchengRiskRequest qianchengRiskRequest = new QianchengRiskRequest(url, header);
			qianchengRiskRequest.setName(userBaseinfo.getRealName());
			qianchengRiskRequest.setMobile(userBaseinfo.getPhone());
			qianchengRiskRequest.setIdCard(userBaseinfo.getIdNo());
			qianchengRiskRequest.setTelecomOrderNo(operatorNo);
			qianchengRiskRequest.setData(riskData);

			qianchengRiskRequest.signByKey(SECRETKEY);
			
			logger.debug("请求浅橙参数"+ JSONObject.toJSONString(qianchengRiskRequest));
			try {
				reqLog.setReqParams(JSONObject.toJSONString(qianchengRiskRequest));
				qianchengReqlogMapper.update(reqLog);
				result = qianchengRiskRequest.request();
				logger.info("qianchenRiskRequest中qiancheng返回结果"+result);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
			logger.debug("浅橙同步响应返回结果："+result);
		}
		return result;
	}

}
