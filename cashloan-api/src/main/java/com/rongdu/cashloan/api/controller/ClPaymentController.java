package com.rongdu.cashloan.api.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.rongdu.cashloan.cl.service.BorrowRepayService;
import com.rongdu.cashloan.core.common.context.Constant;
import com.rongdu.cashloan.core.common.util.ServletUtils;
import com.rongdu.cashloan.core.common.web.controller.BaseController;

/**
 * 主动还款 Controller
 * @author yhjiang
 * @version 1.0
 * @date 2017年8月2日
 * Copyright 杭州融都科技股份有限公司 arc All Rights Reserved
 * 官方网站：www.erongdu.com
 * 创新一部：rdc@erongdu.com
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@Scope("prototype")
@Controller
public class ClPaymentController extends BaseController {
	public static final Logger logger = LoggerFactory.getLogger(ClPaymentController.class);
	@Resource
	private BorrowRepayService borrowRepayService;
	
	/**
	 * 还款方法 ：
	 * 	若参数封装成功，还款状态变更为还款处理中
	 * @param payType
	 * @param borrowId
	 * @param ip
	 */
	@RequestMapping(value = "/api/act/borrow/repay/repayment.htm", method = RequestMethod.POST)
	public void repayment(@RequestParam(value="borrowId") Long borrowId,
			@RequestParam(value="ip") String ip) {
		long userId = Long.parseLong(request.getSession().getAttribute("userId").toString());
		Map<String, String> sdkParams = borrowRepayService.repayment(borrowId, userId, ip);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, sdkParams);
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "参数封装成功");
		ServletUtils.writeToResponse(response,result);
	}
	
	/**
	 * 主动还款 同步响应
	 * @param payResult
	 * @param payOrderNo
	 */
	@RequestMapping(value = "/api/act/borrow/repay/repaymentReturn.htm", method = RequestMethod.POST)
	public void repaymentReturn(@RequestParam(value = "payResult") String payResult, @RequestParam(value = "payOrderNo") String payOrderNo) {
		borrowRepayService.repaymentReturn(payResult, payOrderNo);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "处理成功");
		ServletUtils.writeToResponse(response,result);
	}

	/**
	 * 确认还款
	 * @param borrowId
	 * @param ip
	 * @param type 1-代扣还款，2-展期
	 */
	@RequestMapping(value = "/api/act/borrow/repay/confirmPay.htm", method = RequestMethod.POST)
	public void repaymentReturn(@RequestParam(value="borrowId") Long borrowId,
		@RequestParam(value="ip",required = false) String ip,
		@RequestParam(value="type") String type) {

		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "处理成功");
		ServletUtils.writeToResponse(response,result);
	}
}