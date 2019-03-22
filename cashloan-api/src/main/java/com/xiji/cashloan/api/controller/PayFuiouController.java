package com.xiji.cashloan.api.controller;

import com.alibaba.fastjson.JSON;
import com.xiji.cashloan.api.controller.assist.PaymentNotifyAssist;
import com.xiji.cashloan.api.user.service.ContractService;
import com.xiji.cashloan.cl.domain.PayLog;
import com.xiji.cashloan.cl.domain.PayReqLog;
import com.xiji.cashloan.cl.domain.PayRespLog;
import com.xiji.cashloan.cl.model.PayLogModel;
import com.xiji.cashloan.cl.model.PayRespLogModel;
import com.xiji.cashloan.cl.model.pay.common.constant.PayConstant;
import com.xiji.cashloan.cl.model.pay.common.dto.RepaymentNotifyDto;
import com.xiji.cashloan.cl.model.pay.fuiou.payfor.PayforNotifyModel;
import com.xiji.cashloan.cl.model.pay.fuiou.payfor.PayforRefundNotifyModel;
import com.xiji.cashloan.cl.model.pay.helipay.constant.HelipayConstant;
import com.xiji.cashloan.cl.model.pay.helipay.util.HelipayUtil;
import com.xiji.cashloan.cl.model.pay.helipay.vo.response.HeliPayForPaymentNotifyVo;
import com.xiji.cashloan.cl.service.PayLogService;
import com.xiji.cashloan.cl.service.PayReqLogService;
import com.xiji.cashloan.cl.service.PayRespLogService;
import com.xiji.cashloan.core.common.context.Global;
import com.xiji.cashloan.core.common.web.controller.BaseController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import tool.util.DateUtil;
import tool.util.StringUtil;

/**
 * 实时付款(代付)异步通知
 * 
 * @author wnb
 * @version 1.0.0
 * @date 2018/12/03
 *
 *
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@Controller
@Scope("prototype")
public class PayFuiouController extends BaseController{

	private static final Logger logger = LoggerFactory.getLogger(PayFuiouController.class);

	@Resource
	private PayReqLogService payReqLogService;
	@Resource
	private PayRespLogService payRespLogService;
	@Resource
	private PayLogService payLogService;
	@Resource
    private ContractService contractService;

	@Autowired
	private PaymentNotifyAssist paymentNotifyAssist;

	@Resource
	private ClSmsService clSmsService;

	@RequestMapping(value = "/test.htm")
	public void test(HttpServletRequest request) throws Exception {
		contractService.buildPdf("37");
	}
	/**
	 * 富有异步通知
	 * 代付（支付）- 成功通知接口 - 异步通知处理
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(value = "/pay/fuiou/paymentNotify.htm")
	public void payforNotify(PayforRefundNotifyModel model) throws Exception {
		doNotifyMessage(model,true);
	}
	/**
	 * 富有异步通知
	 * 代付（支付）业务 - 失败退票 - 异步通知处理
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(value = "/pay/fuiou/refundNotify.htm")
	public void refundNotify(PayforRefundNotifyModel model) throws Exception {
		doNotifyMessage(model,false);
	}

	private void doNotifyMessage(PayforNotifyModel model, boolean ifPaySuccess) throws Exception {
		String params = JSON.toJSONString(model);
		logger.info("实时付款 - 异步通知:" + params);

		String merid = Global.getValue("fuiou_merid");
		String pwd = Global.getValue("fuiou_pwd");
		boolean verifySignFlag = model.checkSign(merid,pwd);

		if (!verifySignFlag) {
			logger.error("验签失败" + model.getOrderno());
			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("进入订单" + model.getOrderno() + "处理中.....");
		}

		PayReqLog payReqLog = payReqLogService.findByOrderNo(model.getOrderno());

		if (payReqLog != null) {
			// 保存respLog
			PayRespLog payRespLog = new PayRespLog(model.getOrderno(),PayRespLogModel.RESP_LOG_TYPE_NOTIFY,params);
			payRespLogService.save(payRespLog);

			// 更新reqLog
			modifyPayReqLog(payReqLog,params);
		}

		PayLog payLog = payLogService.findByOrderNo(model.getOrderno());

		if(null  == payLog ){
			logger.warn("未查询到对应的支付订单");
			return ;
		}
		RepaymentNotifyDto dto = new RepaymentNotifyDto();
		dto.setPayPlatNo(model.getFuorderno());
		if (ifPaySuccess) {
			dto.setStatus(PayConstant.RESULT_SUCCESS);
		}else {
			dto.setStatus(PayConstant.STATUS_FAIL);
		}

		String message = "";
		if (PayLogModel.SCENES_LOANS.equals(payLog.getScenes())) {
			message = paymentNotifyAssist.doScenesLoans(dto,payLog);
		}else if (PayLogModel.SCENES_PROFIT.equals(payLog.getScenes())){
			message = paymentNotifyAssist.doScenesProfit(dto,payLog);
		}else if (PayLogModel.SCENES_REFUND.equals(payLog.getScenes())){
			message = paymentNotifyAssist.doScenesRefund(dto,payLog);
		}else {
			logger.error("没有合适的场景，异步通知处理失败" + model.getOrderno());
		}

		if (StringUtil.equals(message, PayConstant.RESULT_SUCCESS)) {
			writeResult(response, "1");
		}
	}

	/**
	 * 合利宝异步通知接口：
	 * 代付（支付）- 成功通知接口 - 异步通知处理
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(value = "/pay/helipay/paymentNotify.htm")
	public void helipayPaymentNotify(HeliPayForPaymentNotifyVo model) throws Exception {
		String params = JSON.toJSONString(model);
		logger.info("实时付款 - 异步通知:" + params);

		String orderNo = model.getRt5_orderId();
		if (!HelipayUtil.checkNotifySign(model)) {
			logger.error("验签失败" + orderNo);
			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("进入订单" + orderNo + "处理中.....");
		}

		PayReqLog payReqLog = payReqLogService.findByOrderNo(model.getRt5_orderId());

		if (payReqLog != null) {
			// 保存respLog
			PayRespLog payRespLog = new PayRespLog(orderNo,PayRespLogModel.RESP_LOG_TYPE_NOTIFY,params);
			payRespLogService.save(payRespLog);

			// 更新reqLog
			modifyPayReqLog(payReqLog,params);
		}

		PayLog payLog = payLogService.findByOrderNo(orderNo);

		if(null  == payLog ){
			logger.warn("未查询到对应的支付订单");
			return ;
		}
		RepaymentNotifyDto dto = new RepaymentNotifyDto();
		dto.setPayPlatNo(model.getRt6_serialNumber());
		if (StringUtil.equals(model.getRt2_retCode(), HelipayConstant.RESULT_CODE_SUCCESS)) {
			dto.setStatus(PayConstant.RESULT_SUCCESS);
		}else {
			dto.setStatus(PayConstant.STATUS_FAIL);
		}

		String message = "";
		if (PayLogModel.SCENES_LOANS.equals(payLog.getScenes())) {
			message = paymentNotifyAssist.doScenesLoans(dto,payLog);
		}else if (PayLogModel.SCENES_PROFIT.equals(payLog.getScenes())){
			message = paymentNotifyAssist.doScenesProfit(dto,payLog);
		}else if (PayLogModel.SCENES_REFUND.equals(payLog.getScenes())){
			message = paymentNotifyAssist.doScenesRefund(dto,payLog);
		}else {
			logger.error("没有合适的场景，异步通知处理失败" + orderNo);
		}

		if (StringUtil.equals(message, PayConstant.RESULT_SUCCESS)) {
			writeResult(response, "success");
		}
	}

	private void writeResult(HttpServletResponse response,String result)throws Exception {
		response.setContentType("application/json");
		response.setCharacterEncoding("utf8");
		response.getOutputStream().write(result.getBytes("utf8"));
	}

	private void modifyPayReqLog (PayReqLog payReqLog,String params){
		payReqLog.setNotifyParams(params);
		payReqLog.setNotifyTime(DateUtil.getNow());
		payReqLogService.updateById(payReqLog);
	}
}