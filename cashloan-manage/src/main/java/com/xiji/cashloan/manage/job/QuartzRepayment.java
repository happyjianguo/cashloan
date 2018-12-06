package com.xiji.cashloan.manage.job;

import com.xiji.cashloan.cl.domain.BankCard;
import com.xiji.cashloan.cl.domain.BorrowRepay;
import com.xiji.cashloan.cl.domain.PayLog;
import com.xiji.cashloan.cl.model.BorrowRepayLogModel;
import com.xiji.cashloan.cl.model.BorrowRepayModel;
import com.xiji.cashloan.cl.model.PayLogModel;
import com.xiji.cashloan.cl.model.pay.fuiou.agreement.OrderXmlBeanReq;
import com.xiji.cashloan.cl.model.pay.fuiou.agreement.OrderXmlBeanResp;
import com.xiji.cashloan.cl.model.pay.fuiou.agreement.QueryPayOrderInfo;
import com.xiji.cashloan.cl.model.pay.fuiou.util.FuiouAgreementPayHelper;
import com.xiji.cashloan.cl.service.BankCardService;
import com.xiji.cashloan.cl.service.BorrowRepayService;
import com.xiji.cashloan.cl.service.ClBorrowService;
import com.xiji.cashloan.cl.service.ClSmsService;
import com.xiji.cashloan.cl.service.PayLogService;
import com.xiji.cashloan.cl.util.fuiou.AmtUtil;
import com.xiji.cashloan.core.common.context.Global;
import com.xiji.cashloan.core.common.exception.ServiceException;
import com.xiji.cashloan.core.common.util.DateUtil;
import com.xiji.cashloan.core.common.util.IpUtil;
import com.xiji.cashloan.core.common.util.OrderNoUtil;
import com.xiji.cashloan.core.common.util.StringUtil;
import com.xiji.cashloan.core.domain.Borrow;
import com.xiji.cashloan.core.domain.User;
import com.xiji.cashloan.core.domain.UserBaseInfo;
import com.xiji.cashloan.core.model.BorrowModel;
import com.xiji.cashloan.core.service.CloanUserService;
import com.xiji.cashloan.core.service.UserBaseInfoService;
import com.xiji.cashloan.manage.domain.QuartzInfo;
import com.xiji.cashloan.manage.domain.QuartzLog;
import com.xiji.cashloan.manage.service.QuartzInfoService;
import com.xiji.cashloan.manage.service.QuartzLogService;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import tool.util.BeanUtil;
import tool.util.BigDecimalUtil;

/**
 * 自动扣款还款
 *
 * @author wnb
 * @version 1.0.0
 * @date 2018/11/27
 *
 *
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@Component
@Lazy(value = false)
public class QuartzRepayment implements Job {

	private static final Logger logger = Logger.getLogger(QuartzRepayment.class);

	private String repayment() throws ServiceException {
		logger.info("进入代扣还款任务...");
		CloanUserService cloanUserService = (CloanUserService) BeanUtil.getBean("cloanUserService");
		UserBaseInfoService userBaseInfoService = (UserBaseInfoService) BeanUtil.getBean("userBaseInfoService");
		BankCardService bankCardService = (BankCardService) BeanUtil.getBean("bankCardService");
		ClBorrowService clBorrowService = (ClBorrowService) BeanUtil.getBean("clBorrowService");
		BorrowRepayService borrowRepayService = (BorrowRepayService) BeanUtil.getBean("borrowRepayService");
		PayLogService payLogService = (PayLogService) BeanUtil.getBean("payLogService");
		ClSmsService clSmsService = (ClSmsService) BeanUtil.getBean("clSmsService");
		int doRepaymentMax = Global.getInt("do_repayment_max");//代扣最大次数
		
		// 查询待还计划
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String doRepaymentToday = Global.getValue("do_repayment_today"); // 是否代扣今天待还的
		if("10".equals(doRepaymentToday)){ // 是
			paramMap.put("repayTime", DateUtil.rollDay(DateUtil.getDayStartTime(DateUtil.getNow()), 1));
		} else { // 否
			paramMap.put("repayTime", DateUtil.getNow());
		}
		paramMap.put("state", BorrowRepayModel.STATE_REPAY_NO);
		List<BorrowRepay> borrowRepayList = borrowRepayService.findUnRepay(paramMap);
		logger.info("代扣还款任务，待处理的还款计划总数为：" + borrowRepayList.size());
		
		String quartzRemark = null;
		int succeed = 0;
		int fail = 0;
		int total = 0;
		for (BorrowRepay borrowRepay : borrowRepayList) {
			logger.info("代扣还款任务，还款计划borrowReapyId：" + borrowRepay.getId() + "开始处理");
			try {
				// 查询用户、用户详情、借款及用户银行卡信息
				User user = cloanUserService.getById(borrowRepay.getUserId());
				UserBaseInfo baseInfo = userBaseInfoService.findByUserId(borrowRepay.getUserId());
				Borrow borrow = clBorrowService.getById(borrowRepay.getBorrowId());
				BankCard bankCard = bankCardService.getBankCardByUserId(borrowRepay.getUserId());
				
				// 达到单笔代扣次数上限的不用再代扣
				int doRepaymentCount = payLogService.doRepaymentNum(borrow.getId());
				if(doRepaymentMax > 0 && doRepaymentCount >= doRepaymentMax){
					continue;
				}
				
				// 已坏账的不用再代扣
				if(BorrowModel.STATE_BAD.equals(borrow.getState())){
					continue;
				}

				// 扣款失败无异步通知 故先查询订单是否已经在支付处理中
				Map<String, Object> payLogMap = new HashMap<String, Object>();
				payLogMap.put("userId", borrowRepay.getUserId());
				payLogMap.put("borrowId", borrowRepay.getBorrowId());
				payLogMap.put("type", PayLogModel.TYPE_COLLECT);
				payLogMap.put("scenes", PayLogModel.SCENES_REPAYMENT);
				PayLog repaymentLog = payLogService.findLatestOne(payLogMap);
				FuiouAgreementPayHelper payHelper = new FuiouAgreementPayHelper();

				// 支付记录存在且不是支付失败，需要查询支付方得到准确结果
				if (null != repaymentLog && !PayLogModel.STATE_PAYMENT_FAILED.equals(repaymentLog.getState())) {
					if (PayLogModel.STATE_PAYMENT_SUCCESS.equals(repaymentLog.getState())) {
						continue;
					}
					QueryPayOrderInfo payOrderInfo = payHelper.queryPayInfo(repaymentLog);

					if (StringUtil.equals(payOrderInfo.getCode(),QueryPayOrderInfo.PAY_SUCCESS)) {
						// 查找对应的还款计划
						Map<String, Object> param = new HashMap<String, Object>();
						param.put("id", borrowRepay.getId());
						param.put("repayTime", DateUtil.getNow());
						param.put("repayWay",BorrowRepayLogModel.REPAY_WAY_CHARGE);
						param.put("repayAccount", bankCard.getCardNo());
						param.put("amount", borrowRepay.getAmount());
						param.put("serialNumber", repaymentLog.getOrderNo());
						param.put("penaltyAmout",borrowRepay.getPenaltyAmout());
						param.put("state", "10");
						if (!borrowRepay.getState().equals(BorrowRepayModel.STATE_REPAY_YES)) {
							borrowRepayService.confirmRepay(param);
						}

						// 更新订单状态
						Map<String, Object> payLogParamMap = new HashMap<String, Object>();
						payLogParamMap.put("state",PayLogModel.STATE_PAYMENT_SUCCESS);
						payLogParamMap.put("updateTime", DateUtil.getNow());
						payLogParamMap.put("id", repaymentLog.getId());
						payLogService.updateSelective(payLogParamMap);

						// 发送代扣还款成功短信提醒
						clSmsService.repayInform(borrowRepay.getUserId(), borrowRepay.getBorrowId());
						continue;
					}else if (StringUtil.equals(payOrderInfo.getCode(),QueryPayOrderInfo.PAY_PROCESSING)) {
						continue;
					} else if (StringUtil.equals(payOrderInfo.getCode(), QueryPayOrderInfo.PAY_FAIL)) {
						// 更新订单状态
						Map<String, Object> payLogParamMap = new HashMap<String, Object>();
						payLogParamMap.put("state",PayLogModel.STATE_PAYMENT_FAILED);
						payLogParamMap.put("updateTime", DateUtil.getNow());
						payLogParamMap.put("id", repaymentLog.getId());
						payLogService.updateSelective(payLogParamMap);
					}
				}

				Date payReqTime = DateUtil.getNow();
				String orderNo = OrderNoUtil.getSerialNumber();
//				RepaymentModel repayment = new RepaymentModel(orderNo);
//				repayment.setUser_id(user.getUuid());
//				repayment.setBusi_partner(LianLianConstant.GOODS_VIRTUAL);
//				repayment.setDt_order(DateUtil.dateStr3(payReqTime));
//				repayment.setName_goods("还款" + borrow.getOrderNo());
//				repayment.setInfo_order("repayment_" + borrow.getOrderNo());
//
				double amount = BigDecimalUtil.add(borrowRepay.getAmount(), borrowRepay.getPenaltyAmout());  //计算实际还款金额
//				if ("dev".equals(Global.getValue("app_environment"))) {
//					repayment.setMoney_order("0.01");
//				} else {
//					repayment.setMoney_order(StringUtil.isNull(amount));
//				}
//
//				repayment.setAmount(amount);
//				RiskItems riskItems = new RiskItems();
//				riskItems.setFrms_ware_category("2010");
//				riskItems.setUser_info_mercht_userno(user.getUuid());
//				riskItems.setUser_info_bind_phone(baseInfo.getPhone());
//				riskItems.setUser_info_dt_register(DateUtil.dateStr3(user.getRegistTime()));
//				riskItems.setUser_info_full_name(baseInfo.getRealName());
//				riskItems.setUser_info_id_no(baseInfo.getIdNo());
//				riskItems.setUser_info_identify_type("1");
//				riskItems.setUser_info_identify_state("1");
//				repayment.setRisk_item(JSONObject.toJSONString(riskItems));
//				repayment.setSchedule_repayment_date(DateUtil.dateStr2(borrowRepay.getRepayTime()));
//				repayment.setRepayment_no(borrow.getOrderNo());
//				repayment.setNo_agree(bankCard.getAgreeNo());
//				repayment.setNotify_url(Global.getValue("server_host") + "/pay/lianlian/repaymentNotify.htm");
//
//
//				LianLianHelper helper = new LianLianHelper();
//				//请求连连代扣，repayment里面有返回值
//				repayment = (RepaymentModel) helper.repayment(repayment);


				OrderXmlBeanReq beanReq = new OrderXmlBeanReq();
				beanReq.setUserId(user.getUuid());
				beanReq.setUserIp(IpUtil.getLocalIp());
				beanReq.setType("03");
				beanReq.setMchntOrderId(orderNo);
				if ("dev".equals(Global.getValue("app_environment"))) {
					beanReq.setAmt(AmtUtil.convertAmtToBranch("0.01"));
				} else {
					beanReq.setAmt(AmtUtil.convertAmtToBranch(amount));
				}

				beanReq.setProtocolNo(bankCard.getAgreeNo());
				beanReq.setNeedSendMsg("0");
				beanReq.setBackUrl(Global.getValue("server_host")+ "/pay/fuiou/repaymentNotify.htm");
				beanReq.setRem1("还款" + borrow.getOrderNo());
				beanReq.setRem2("repayment_" + borrow.getOrderNo());
				String key = Global.getValue("protocol_mchntcd_key");
				OrderXmlBeanResp resp = payHelper.repayment(beanReq);
				String payMsg = "";
				String payOrderNo = "";
				if (resp.checkSign(key)) {
					payMsg = resp.getResponseMsg();
					if (resp.checkReturn() && StringUtil.isNotEmpty(resp.getOrderId())) {
						payMsg = resp.getOrderId()+"|" + payMsg;
					}
					payOrderNo = resp.getOrderId();
				}
				PayLog payLog = new PayLog();
				payLog.setOrderNo(orderNo);
				if (StringUtil.isNotEmpty(payOrderNo)) {
					payLog.setPayOrderNo(payOrderNo);
				}
				payLog.setUserId(borrowRepay.getUserId());
				payLog.setBorrowId(borrowRepay.getBorrowId());
				payLog.setAmount(amount);
				payLog.setCardNo(bankCard.getCardNo());
				payLog.setBank(bankCard.getBank());
				payLog.setSource(PayLogModel.SOURCE_FUNDS_OWN);
				payLog.setType(PayLogModel.TYPE_COLLECT);
				payLog.setScenes(PayLogModel.SCENES_REPAYMENT);
				payLog.setState(PayLogModel.STATE_PAYMENT_WAIT);
				payLog.setCode(resp.getResponseCode());
				payLog.setRemark(payMsg);
				payLog.setPayReqTime(payReqTime);
				payLog.setCreateTime(DateUtil.getNow());
				payLogService.save(payLog);

				succeed++;
				total++;
				
				//8104就是没有该还款计划的code
//				if(repayment.getRet_code().equals("8104")){
//					//重新上传还款计划
//					logger.info("借款订单号："+borrow.getId()+"无扣款计划信息，重新生成还款计划");
//					borrowRepayService.authSignApply(borrowRepay.getUserId());
//				}
			} catch (Exception e) {
				fail++;
				total++;
				logger.error(e.getMessage(), e);
			}
		}
		
		quartzRemark = "处理总数"+total+"个，成功"+succeed+"个，失败"+fail+"个";
		logger.info("代扣还款任务，执行完毕，" + quartzRemark);
		return quartzRemark;

	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		QuartzInfoService quartzInfoService = (QuartzInfoService) BeanUtil.getBean("quartzInfoService");
		QuartzLogService quartzLogService = (QuartzLogService) BeanUtil.getBean("quartzLogService");
		// 查询当前任务信息
		QuartzInfo quartzInfo = quartzInfoService.findByCode("doRepayment");
		Map<String, Object> qiData = new HashMap<>();
		qiData.put("id", quartzInfo.getId());

		QuartzLog quartzLog = new QuartzLog();
		quartzLog.setQuartzId(quartzInfo.getId());
		quartzLog.setStartTime(DateUtil.getNow());
		try {
			String remark = repayment();
			quartzLog.setTime(DateUtil.getNow().getTime() - quartzLog.getStartTime().getTime());
			quartzLog.setResult("10");
			quartzLog.setRemark(remark);
			qiData.put("succeed", quartzInfo.getSucceed() + 1);
		} catch (Exception e) {
			quartzLog.setResult("20");
			qiData.put("fail", quartzInfo.getFail() + 1);
			logger.error(e.getMessage(), e);
		} finally {
			logger.info("保存代扣还款定时任务执行记录");
			quartzLogService.save(quartzLog);
			quartzInfoService.update(qiData);
		}

	}
}