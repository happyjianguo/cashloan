package com.xiji.cashloan.api.controller;

import com.xiji.cashloan.core.common.util.StringUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.xiji.cashloan.cl.domain.UserSdkLog;
import com.xiji.cashloan.cl.service.UserSdkLogService;
import com.xiji.cashloan.core.common.context.Constant;
import com.xiji.cashloan.core.common.context.Global;
import com.xiji.cashloan.core.common.util.ServletUtils;
import com.xiji.cashloan.core.common.web.controller.BaseController;

import credit.Header;
import credit.LinkfaceOCRRequest;
import credit.RecordRequest;

 /**
 * sdk识别记录表Controller
 * 
 * @author wnb
 * @version 1.0.0
 * @date 2018/12/03
 *
 *
 *
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@Scope("prototype")
@Controller
public class UserSdkLogController extends BaseController {

	public static final Logger logger = LoggerFactory.getLogger(UserSdkLogController.class);
	
	@Resource
	private UserSdkLogService userSdkLogService;
	
	@RequestMapping(value = "/api/act/mine/sdk/find.htm")
	public void find(
			@RequestParam(value="userId") long userId) throws Exception {
		Map<String,Object> searchMap = new HashMap<>();
		searchMap.put("userId", userId);
		Map<String,Object> data = userSdkLogService.countDayTime(searchMap);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, data); 
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "查询成功");
		ServletUtils.writeToResponse(response,result);
	}

	 @RequestMapping(value = "/api/act/mine/sdk/select.htm")
	 public void select(
		 @RequestParam(value="userId", required = false) Long userId) throws Exception {
		 Map<String,Object> data = new HashMap<>();
		 String ocrType = Global.getValue("orc_sdk_select");
		 data.put("ocrType",1);//face++,默认使用

		 if (StringUtil.equals("1", ocrType)) {
			 data.put("ocrType",1);//face++
		 }else if (StringUtil.equals("2", ocrType)){
			 data.put("ocrType",2);//有盾
		 }else if (StringUtil.contains(ocrType,"=")){
			 if (userId != null) {
				 int lastKey = (int) (userId%10);
				 if (ocrType.contains(String.valueOf(lastKey))) {
					 data.put("ocrType",2);//有盾
				 }
			 }
		 }

		 Map<String,Object> result = new HashMap<String,Object>();
		 result.put(Constant.RESPONSE_DATA, data);
		 result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		 result.put(Constant.RESPONSE_CODE_MSG, "查询成功");
		 ServletUtils.writeToResponse(response,result);
	 }
	
	/**
	 * 同步OCR数据
	 * @param name
	 * @param idCard
	 * @throws Exception
	 */
	@RequestMapping(value = "/api/act/mine/sdk/synchron.htm")
	private void synchron(
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "idCard", required = false) String idCard) throws Exception {
		long userId = Long.parseLong(request.getSession().getAttribute("userId").toString());
		Map<String,Object> result = new HashMap<String,Object>();
		JSONObject resultJson = null;
		String vType = Global.getValue("verify_type");
		String LINKFACEHOST = null;
		long timestamp = new Date().getTime();
		Header header = new Header(Global.getValue("apikey"), timestamp);
		
		if ("20".equals(type)&&"30".equals(vType)) {//商汤
			
			LINKFACEHOST = Global.getValue("linkfacehost_real");
			
	        LinkfaceOCRRequest linkfaceRequest = new LinkfaceOCRRequest(LINKFACEHOST, header);

	        linkfaceRequest.setName(name);
	        linkfaceRequest.setIdCard(idCard);

	        linkfaceRequest.signByKey(Global.getValue("secretkey"));

	        String resultStr = linkfaceRequest.request();
	        logger.info("返回参数"+resultStr);
	        resultJson = JSONObject.parseObject(resultStr);
	        if (resultJson!=null) {
	        	result.put(Constant.RESPONSE_CODE, resultJson.get("code"));
	        	result.put(Constant.RESPONSE_CODE_MSG, resultJson.get("msg"));
	        }else {
	        	result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
				result.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_SUCCESS);
	        }
		}else if("10".equals(type)&&"20".equals(vType)){//小视
			
			LINKFACEHOST = Global.getValue("living_record");
			
			RecordRequest recordRequest = new RecordRequest(LINKFACEHOST, header);
			
			recordRequest.setName("app");
			recordRequest.setIdCard("0000");
			recordRequest.signByKey(Global.getValue("secretkey"));
			String resultStr = recordRequest.request();
			
			logger.info("返回参数"+resultStr);
			resultJson = JSONObject.parseObject(resultStr);
			if (resultJson!=null) {
	        	result.put(Constant.RESPONSE_CODE, resultJson.get("code"));
	        	result.put(Constant.RESPONSE_CODE_MSG, resultJson.get("msg"));
	        }else {
	        	result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
				result.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_SUCCESS);
	        }
		}else {
			result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_SUCCESS);
		}
        UserSdkLog usl = new UserSdkLog();
		usl.setUserId(userId);
		usl.setTimeType(type);
		usl.setCreateTime(new Date());
		userSdkLogService.save(usl);
        ServletUtils.writeToResponse(response,result);
    }

}