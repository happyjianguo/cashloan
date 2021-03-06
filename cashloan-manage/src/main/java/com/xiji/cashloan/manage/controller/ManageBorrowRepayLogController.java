package com.xiji.cashloan.manage.controller;

import com.github.pagehelper.Page;
import com.xiji.cashloan.cl.domain.*;
import com.xiji.cashloan.cl.model.ManageBRepayLogModel;
import com.xiji.cashloan.cl.model.PayLogModel;
import com.xiji.cashloan.cl.model.pay.chanpay.constant.ChanPayConstant;
import com.xiji.cashloan.cl.model.pay.common.PayCommonHelper;
import com.xiji.cashloan.cl.model.pay.common.PayCommonUtil;
import com.xiji.cashloan.cl.model.pay.common.constant.PayConstant;
import com.xiji.cashloan.cl.model.pay.common.vo.request.PaymentReqVo;
import com.xiji.cashloan.cl.model.pay.common.vo.request.RepaymentQueryVo;
import com.xiji.cashloan.cl.model.pay.common.vo.request.RepaymentReqVo;
import com.xiji.cashloan.cl.model.pay.common.vo.response.PaymentResponseVo;
import com.xiji.cashloan.cl.model.pay.common.vo.response.RepaymentQueryResponseVo;
import com.xiji.cashloan.cl.model.pay.common.vo.response.RepaymentResponseVo;
import com.xiji.cashloan.cl.model.pay.helipay.util.HelipayUtil;
import com.xiji.cashloan.cl.model.pay.helipay.vo.delegation.HelipayLoanConInfo;
import com.xiji.cashloan.cl.model.pay.kuaiqian.util.KuaiqianPayUtil;
import com.xiji.cashloan.cl.monitor.BusinessExceptionMonitor;
import com.xiji.cashloan.cl.service.*;
import com.xiji.cashloan.core.common.context.Constant;
import com.xiji.cashloan.core.common.context.Global;
import com.xiji.cashloan.core.common.util.*;
import com.xiji.cashloan.core.domain.Borrow;
import com.xiji.cashloan.core.domain.User;
import com.xiji.cashloan.core.domain.UserBaseInfo;
import com.xiji.cashloan.core.service.CloanUserService;
import com.xiji.cashloan.core.service.UserBaseInfoService;
import com.xiji.cashloan.system.permission.annotation.RequiresPermission;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import tool.util.BigDecimalUtil;
import tool.util.NumberUtil;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 还款记录后台管理Controller
 *
 * @author wnb
 * @version 1.0.0
 * @date 2018/11/27
 *
 *
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */

@Controller
@Scope("prototype")
public class ManageBorrowRepayLogController extends ManageBaseController{

	private static final Logger logger  = LoggerFactory.getLogger(ManageBorrowRepayLogController.class);
	
	@Resource
	private CloanUserService cloanUserService;
	@Resource
	private UserBaseInfoService userBaseInfoService;
	@Resource
	private BankCardService bankCardService;
	@Resource
	private ClBorrowService clBorrowService;
	@Resource
	private BorrowRepayService borrowRepayService;
	@Resource
	private BorrowRepayLogService borrowRepayLogService;
	@Resource
	private PayLogService payLogService;
	@Resource
	private HelipayUserService helipayUserService;

	/**
	 * 还款记录列表
	 *
	 * @param currentPage
	 * @param pageSize
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/modules/manage/borrow/repay/log/list.htm")
	@RequiresPermission(code = "modules:manage:borrow:repay:log:list", name = "还款记录列表")
	public void page(
			@RequestParam(value = "searchParams", required = false) String searchParams,
			@RequestParam(value = "current") int currentPage,
			@RequestParam(value = "pageSize") int pageSize) {
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		Page<ManageBRepayLogModel> page = borrowRepayLogService.listModel(params, currentPage, pageSize);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(Constant.RESPONSE_DATA, page);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
		ServletUtils.writeToResponse(response, result);
	}
	
	/**
	 * 退还 还款金额
	 * @param id
	 * @param amount
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/borrow/repayLog/refund.htm", method = { RequestMethod.POST })
	public void refund(@RequestParam(value = "id") Long id,
			@RequestParam(value = "amount") String amount) throws Exception {

		BorrowRepayLog borrowRepayLog = borrowRepayLogService.getById(id);
		BankCard bankCard = bankCardService.getBankCardByUserId(borrowRepayLog.getUserId());
		UserBaseInfo baseInfo = userBaseInfoService.findByUserId(borrowRepayLog.getUserId());
		Borrow borrow = clBorrowService.getById(borrowRepayLog.getBorrowId());

		Date date = DateUtil.getNow();
		if (borrow.getFee() < NumberUtils.toDouble(amount)) {
			Map<String,Object> result = new HashMap<String, Object>();
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "付款金额超过综合服务费了，请调整支付金额！");
			ServletUtils.writeToResponse(response, result);
			return;
		}
		if (PayCommonHelper.isEmpty(bankCard)) {
			Map<String,Object> result = new HashMap<String, Object>();
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "绑卡信息丢失，可能是切换了支付通道，请联系客户重新绑卡");
			ServletUtils.writeToResponse(response, result);
			return;
		}

		PaymentReqVo vo = new PaymentReqVo();
		if ("dev".equals(Global.getValue("app_environment"))) {
			vo.setAmount(1.0);
			borrow.setInterest(vo.getAmount()*(borrow.getFee()/borrow.getAmount()*0.1));
		} else {
			vo.setAmount(NumberUtils.toDouble(amount));
		}
		vo.setBankCardName(baseInfo.getRealName());
		vo.setBankCardNo(bankCard.getCardNo());
		vo.setBorrowId(borrow.getId());
		vo.setBorrowOrderNo(borrow.getOrderNo());
		vo.setMobile(bankCard.getPhone());
		vo.setShareKey(bankCard.getUserId());
		vo.setBankName(bankCard.getBank());
		vo.setIdNo(baseInfo.getIdNo());
		String payModelSelect = Global.getValue("pay_model_select");
		if ("helipay".equals(payModelSelect)){
			Map<String,Object> params = new HashMap<>();
			params.put("userId",borrow.getUserId());
			HelipayUser helipayUser = helipayUserService.getHelipayUser(params);
            if (helipayUser == null || helipayUser.getHelipayUserId() == null){
                Map<String,Object> result = new HashMap<String, Object>();
                result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
                result.put(Constant.RESPONSE_CODE_MSG, "该用户合利宝未注册认证，请用户退出app重新登录！！！");
                ServletUtils.writeToResponse(response, result);
                return;
            }
			vo.setHelipayUserId(helipayUser.getHelipayUserId());
		}

		HelipayLoanConInfo helipayLoanConInfo = new HelipayLoanConInfo();
		helipayLoanConInfo.setLoanTime(borrow.getTimeLimit());
		helipayLoanConInfo.setLoanTimeUnit("D");// 借款时间单位:D-天;M-月;Y-年
        helipayLoanConInfo.setLoanInterestRate(Double.toString(BigDecimalUtil.decimal(36,2)));
		helipayLoanConInfo.setPeriodization("1");
		helipayLoanConInfo.setPeriodizationDays(borrow.getTimeLimit());
        helipayLoanConInfo.setPeriodizationFee (Double.toString(BigDecimalUtil.decimal(Double.parseDouble(amount)+(Double.parseDouble(amount)*0.36/360*Integer.parseInt(borrow.getTimeLimit())),2)));
        helipayLoanConInfo.setBody("退还");
		helipayLoanConInfo.setPurpose("生活消费");
		vo.setHelipayLoanConInfo(helipayLoanConInfo);

		PaymentResponseVo result = PayCommonUtil.payment(vo);

		PayLog payLog = new PayLog();
		payLog.setOrderNo(result.getOrderNo());
		payLog.setUserId(borrow.getUserId());
		payLog.setBorrowId(borrow.getId());
		payLog.setAmount(NumberUtil.getDouble(amount));
		payLog.setCardNo(bankCard.getCardNo());
		payLog.setBank(bankCard.getBank());
		payLog.setSource(PayLogModel.SOURCE_FUNDS_OWN);
		payLog.setType(PayLogModel.TYPE_PAYMENT);
		payLog.setScenes(PayLogModel.SCENES_REFUND);

		if (PayCommonUtil.success(result.getStatus())) { //受理成功
			payLog.setState(PayLogModel.STATE_PAYMENT_WAIT);
		} else if (PayCommonUtil.needCheck(result.getStatus())) {  // 接口调用异常，待人工审核
			payLog.setState(PayLogModel.STATE_PENDING_REVIEW);
//					payLog.setConfirmCode(payment.getConfirm_code());
			payLog.setUpdateTime(DateUtil.getNow());
		} else {
			BusinessExceptionMonitor.add(BusinessExceptionMonitor.TYPE_11, payLog.getOrderNo(), result.getMessage());
			payLog.setState(PayLogModel.STATE_PAYMENT_FAILED);
			payLog.setUpdateTime(DateUtil.getNow());
		}

		payLog.setCode(result.getStatusCode());
		payLog.setRemark(result.getMessage());
		payLog.setPayReqTime(date);
		payLog.setCreateTime(DateUtil.getNow());
		payLogService.save(payLog);
		
		Map<String,Object> resultMap = new HashMap<String, Object>();
		resultMap.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		resultMap.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_SUCCESS);
		ServletUtils.writeToResponse(response, resultMap);
	}
	
	
	/**
	 * 补扣-还款金额
	 *
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/borrow/repayLog/deduction.htm", method = { RequestMethod.POST })
	public void deduction(@RequestParam(value = "id") Long id,
			@RequestParam(value = "amount") String amount) throws Exception {
		BorrowRepayLog borrowRepayLog = borrowRepayLogService.getById(id);
		BorrowRepay borrowRepay = borrowRepayService.getById(borrowRepayLog.getRepayId());
		User user = cloanUserService.getById(borrowRepayLog.getUserId());
		UserBaseInfo baseInfo = userBaseInfoService.findByUserId(borrowRepayLog.getUserId());
		Borrow borrow = clBorrowService.getById(borrowRepayLog.getBorrowId());
		BankCard bankCard = bankCardService.getBankCardByUserId(borrowRepayLog
				.getUserId());

		// 扣款失败无异步通知 故先查询订单是否已经在支付处理中
		Map<String, Object> payLogMap = new HashMap<String, Object>();
		payLogMap.put("userId", borrowRepayLog.getUserId());
		payLogMap.put("borrowId", borrowRepayLog.getBorrowId());
		payLogMap.put("type", PayLogModel.TYPE_COLLECT);
		payLogMap.put("scenes",PayLogModel.SCENES_DEDUCTION);
		PayLog deductionLog = payLogService.findLatestOne(payLogMap);
		// 订单存在并不是支付失败记录
		if (null != deductionLog
				&& !PayLogModel.STATE_PAYMENT_FAILED.equals(deductionLog.getState())) {
			RepaymentQueryVo vo = new RepaymentQueryVo();
			vo.setOrderNo(deductionLog.getOrderNo());
			vo.setPayPlatNo(deductionLog.getPayOrderNo());
			vo.setShareKey(deductionLog.getUserId());
			RepaymentQueryResponseVo responseVo = PayCommonUtil.queryOrder(vo);

			if (StringUtil.equals(responseVo.getCode(), PayConstant.QUERY_PAY_SUCCESS)) {
				// 更新订单状态
				deductionLog.setState(PayLogModel.STATE_PAYMENT_SUCCESS);
				deductionLog.setUpdateTime(DateUtil.getNow());
				payLogService.updateById(deductionLog);
			}
		}
		logger.info("进行补扣代扣" + amount);
		Date payReqTime = DateUtil.getNow();

		if (PayCommonHelper.isEmpty(bankCard)) {
			Map<String,Object> result = new HashMap<String, Object>();
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "绑卡信息丢失，可能是切换了支付通道，请联系客户重新绑卡");
			ServletUtils.writeToResponse(response, result);
		}

		RepaymentReqVo vo = new RepaymentReqVo();
		if ("dev".equals(Global.getValue("app_environment"))) {
			vo.setAmount(0.2);
		} else {
			vo.setAmount(NumberUtils.toDouble(amount));
		}

		vo.setIp(IpUtil.getLocalIp());
		vo.setUserId(user.getUuid());
		vo.setProtocolNo(bankCard.getAgreeNo());
		vo.setBorrowOrderNo(borrow.getOrderNo());
		vo.setRemark("还款" + borrow.getOrderNo());
		vo.setRemark2("repayment_" + borrow.getOrderNo());
		vo.setTerminalId(UUID.randomUUID().toString());
		vo.setTerminalType("OTHER");
		vo.setShareKey(bankCard.getUserId());
		vo.setCardNo(bankCard.getCardNo());
		String orderNo = "";
		String payModelSelect = Global.getValue("pay_model_select");
		if ("helipay".equals(payModelSelect)){
			orderNo = HelipayUtil.getOrderId();
		}else if ("kuaiqian".equals(payModelSelect)){
			orderNo = KuaiqianPayUtil.getOrderId();
		}else if ("chanpay".equals(payModelSelect)){
			orderNo = ChanPayConstant.getOrderId();
		}
		vo.setOrderNo(orderNo);

		PayLog payLog = new PayLog();
		payLog.setOrderNo(orderNo);
		payLog.setUserId(borrowRepay.getUserId());
		payLog.setBorrowId(borrowRepay.getBorrowId());
		payLog.setAmount(NumberUtil.getDouble(amount));
		payLog.setCardNo(bankCard.getCardNo());
		payLog.setBank(bankCard.getBank());
		payLog.setSource(PayLogModel.SOURCE_FUNDS_OWN);
		payLog.setType(PayLogModel.TYPE_COLLECT);
		payLog.setScenes(PayLogModel.SCENES_DEDUCTION);
		payLog.setState(PayLogModel.STATE_PAYMENT_WAIT);
		payLog.setPayReqTime(payReqTime);
		payLog.setCreateTime(DateUtil.getNow());
		payLogService.save(payLog);
		RepaymentResponseVo responseVo = PayCommonUtil.repayment(vo);

		String payOrderNo = "";
		if (PayCommonUtil.success(responseVo.getStatus())) {
			payOrderNo = responseVo.getOrderNo();
		}
		Map<String,Object> params = new HashMap<>();
		if (StringUtil.isNotEmpty(payOrderNo)) {
			params.put("payOrderNo",responseVo.getPayPlatNo());
		}

		params.put("remark",responseVo.getMessage());
		params.put("id",payLog.getId());
		payLog = payLogService.findByOrderNo(orderNo);
		if ("10".equals(payLog.getState())){
			params.put("code",responseVo.getStatusCode());
			if (PayConstant.RESULT_SUCCESS.equals(responseVo.getStatus())){
				params.put("state",PayLogModel.STATE_PAYMENT_WAIT);
			} else {
				params.put("state",PayLogModel.STATE_PAYMENT_FAILED);
			}
			payLogService.updateSelective(params);
		} else {
			payLogService.updateSelective(params);
		}

		Map<String,Object> result = new HashMap<String, Object>();
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_SUCCESS);
		ServletUtils.writeToResponse(response, result);
	}
	
	
	/**
	 * 还款中检查
	 * @param borrowId
	 */
	@RequestMapping(value = "/modules/manage/borrow/repay/check.htm")
	@RequiresPermission(code = "modules:manage:borrow:repay:check", name = "还款记录列表")
	public void check(@RequestParam(value = "borrowId") String borrowId) {
		borrowRepayService.repayCheck(Long.parseLong(borrowId));
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_SUCCESS);
		ServletUtils.writeToResponse(response, result);
	}
}
