package com.xiji.cashloan.cl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fuiou.mpay.encrypt.DESCoderFUIOU;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xiji.cashloan.cl.domain.*;
import com.xiji.cashloan.cl.mapper.*;
import com.xiji.cashloan.cl.model.*;
import com.xiji.cashloan.cl.model.pay.common.PayCommonHelper;
import com.xiji.cashloan.cl.model.pay.common.PayCommonUtil;
import com.xiji.cashloan.cl.model.pay.common.constant.PayConstant;
import com.xiji.cashloan.cl.model.pay.common.vo.request.RepaymentQueryVo;
import com.xiji.cashloan.cl.model.pay.common.vo.request.RepaymentReqVo;
import com.xiji.cashloan.cl.model.pay.common.vo.response.RepaymentQueryResponseVo;
import com.xiji.cashloan.cl.model.pay.common.vo.response.RepaymentResponseVo;
import com.xiji.cashloan.cl.model.pay.fuiou.agreement.OrderXmlBeanReq;
import com.xiji.cashloan.cl.model.pay.fuiou.agreement.OrderXmlBeanResp;
import com.xiji.cashloan.cl.model.pay.fuiou.constant.FuiouConstant;
import com.xiji.cashloan.cl.model.pay.fuiou.util.FuiouAgreementPayHelper;
import com.xiji.cashloan.cl.model.pay.lianlian.CertifiedPayModel;
import com.xiji.cashloan.cl.model.pay.lianlian.constant.LianLianConstant;
import com.xiji.cashloan.cl.model.pay.lianlian.util.LianLianHelper;
import com.xiji.cashloan.cl.service.*;
import com.xiji.cashloan.cl.util.black.CollectionUtil;
import com.xiji.cashloan.cl.util.fuiou.AmtUtil;
import com.xiji.cashloan.cl.util.fuiou.XMapUtil;
import com.xiji.cashloan.core.common.context.Constant;
import com.xiji.cashloan.core.common.context.Global;
import com.xiji.cashloan.core.common.exception.BussinessException;
import com.xiji.cashloan.core.common.mapper.BaseMapper;
import com.xiji.cashloan.core.common.service.impl.BaseServiceImpl;
import com.xiji.cashloan.core.common.util.DateUtil;
import com.xiji.cashloan.core.common.util.IpUtil;
import com.xiji.cashloan.core.common.util.OrderNoUtil;
import com.xiji.cashloan.core.common.util.excel.ReadExcelUtils;
import com.xiji.cashloan.core.domain.Borrow;
import com.xiji.cashloan.core.domain.User;
import com.xiji.cashloan.core.domain.UserBaseInfo;
import com.xiji.cashloan.core.mapper.UserBaseInfoMapper;
import com.xiji.cashloan.core.mapper.UserMapper;
import com.xiji.cashloan.core.model.BorrowModel;
import com.xiji.cashloan.core.service.CloanUserService;
import com.xiji.cashloan.core.service.UserBaseInfoService;
import com.xiji.creditrank.cr.domain.Credit;
import com.xiji.creditrank.cr.domain.CreditLog;
import com.xiji.creditrank.cr.mapper.CreditLogMapper;
import com.xiji.creditrank.cr.mapper.CreditMapper;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import tool.util.*;

import java.util.*;

/**
 * 还款计划ServiceImpl
 *
 * @author wnb
 * @date 2018/11/27
 * @version 1.0.0
 *
 *
 * 未经授权不得进行修改、复制、出售及商业使用
 */

@Service("borrowRepayService")
public class BorrowRepayServiceImpl extends BaseServiceImpl<BorrowRepay, Long> implements BorrowRepayService {

	private static final Logger logger = LoggerFactory.getLogger(BorrowRepayServiceImpl.class);

	@Resource
	private BorrowRepayMapper borrowRepayMapper;
	@Resource
	private BorrowRepayLogMapper borrowRepayLogMapper;
	@Resource
	private ClBorrowMapper clBorrowMapper;
	@Resource
	private BorrowProgressMapper borrowProgressMapper;
	@Resource
	private CreditMapper creditMapper;
	@Resource
	private UrgeRepayOrderService urgeRepayOrderService;
	@Resource
	private UrgeRepayOrderLogService urgeRepayOrderLogService;
	@Resource
	private ProfitLogService profitLogService;
	@Resource
	private UserInviteMapper userInviteMapper;
	@Resource
	private ProfitLogMapper profitLogMapper;
	@Resource
	private BankCardService bankCardService;
	@Resource
	private UserMapper userMapper;
	@Resource
	private UserBaseInfoMapper userBaseInfoMapper;
	@Resource
	private PayLogService payLogService;
	@Resource
	private PayLogMapper payLogMapper;
	@Resource
	private ClBorrowService clBorrowService;
	@Resource
	private ClSmsService clSmsService;
	@Resource
	private CreditLogMapper creditLogMapper;
	@Resource
	private PayReqLogMapper payReqLogMapper;
	@Resource
	private ProfitAgentMapper profitAgentMapper;
	@Resource
	private CloanUserService cloanUserService;
	@Resource
	private UserBaseInfoService userBaseInfoService;
	@Resource
	private ChannelMapper channelMapper;
	@Resource
	private ManualRepayOrderMapper manualRepayOrderMapper;

	@Override
	public BaseMapper<BorrowRepay, Long> getMapper() {
		return borrowRepayMapper;
	}

	@Override
	public int save(BorrowRepay borrowRepay) {
		return borrowRepayMapper.save(borrowRepay);
	}

	@Override
	public boolean genRepayPlan(Borrow borrow){
        User user = cloanUserService.getById(borrow.getUserId());
        Channel channel = channelMapper.getChannelById(user.getChannelId());
        String beheadFee = channel.getBeheadFee();// 是否启用砍头息
		//放款成功,保存还款计划
		BorrowRepay br = new BorrowRepay();
		if ("10".equals(beheadFee)) {//启用
			br.setAmount(borrow.getAmount());
			br.setBorrowAmount(borrow.getAmount());
		}else {
			br.setAmount(borrow.getAmount() + borrow.getFee());
			br.setBorrowAmount(borrow.getAmount() + borrow.getFee());
		}
		br.setBorrowId(borrow.getId());
		br.setUserId(borrow.getUserId());
		String repay = DateUtil.dateStr2(DateUtil.rollDay(DateUtil.getNow(),(Integer.parseInt(borrow.getTimeLimit())) - 1));
		repay = repay + " 23:59:59";
		br.setRepayTime(DateUtil.valueOf(repay, "yyyy-MM-dd HH:mm:ss"));
		br.setState(BorrowRepayModel.STATE_REPAY_NO);
		br.setPenaltyAmout(0.0);
		br.setPenaltyDay("0");
		br.setCreateTime(DateUtil.getNow());
		//  默认还款计划类型为1
		br.setType("1");
		int result = borrowRepayMapper.save(br);

		//插入人工到期订单表
		UserBaseInfo userInfo = userBaseInfoMapper.findByUserId(borrow.getUserId());
		manualRepayOrderMapper.save(getManualRepayOrder(borrow.getId(), userInfo, br.getId(), null));

		if (result > 0) {
			// 调用连连支付接口进行授权
			authApply(br);
			return true;
		}
		return false;
	}

	@Override
	public void authSignApply(Long userId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		paramMap.put("state", BorrowRepayModel.STATE_REPAY_NO);
		List<BorrowRepay> borrowRepayList = borrowRepayMapper.findUnRepay(paramMap);

		// 若未还款列表不为null 并列表数目大于0 则进行授权
		if (null != borrowRepayList && !borrowRepayList.isEmpty()) {
			for (BorrowRepay borrowRepay : borrowRepayList) {
				authApply(borrowRepay);
			}
		}
	}

	/**
	 * 调用连连支付 分期付 授权接口 为用户授权
	 *
	 */
	private void authApply(final BorrowRepay borrowRepay) {
		// 查询用户信息及银行卡信息 用于授权
//		new Thread() {
//			@Override
//			public void run() {
//				Map<String, Object> paramMap = new HashMap<String, Object>();
//				paramMap.put("userId", borrowRepay.getUserId());
//				BankCard bankCard = bankCardManage.findSelective(paramMap);
//
//				User user = userMapper.findByPrimary(borrowRepay.getUserId()); // 用户UUID
//				Borrow borrow = clBorrowMapper.findByPrimary(borrowRepay.getBorrowId()); // 借款标识 OrderNo 作为还款编号
//
//				String orderNo = OrderNoUtil.getSerialNumber();
//				AuthApplyModel authApply = new AuthApplyModel(orderNo);
//				authApply.setUser_id(user.getUuid());
//				Map<String, Object> repaymentPlanMap = new HashMap<String, Object>();
//				List<RepaymentPlan> list = new ArrayList<RepaymentPlan>();
//				RepaymentPlan plan = new RepaymentPlan();
//				plan.setDate(DateUtil.dateStr2(borrowRepay.getRepayTime()));
//				plan.setAmount(StringUtil.isNull(borrowRepay.getAmount()));
//				list.add(plan);
//				repaymentPlanMap.put("repaymentPlan", list);
//				authApply.setRepayment_plan(JSONObject.toJSONString(repaymentPlanMap));
//				authApply.setRepayment_no(borrow.getOrderNo());
//				SmsParams smsParams = new SmsParams();
//				smsParams.setContract_type(Global.getValue("title"));
//				smsParams.setContact_way(Global.getValue("phone"));
//				authApply.setSms_param(JSONObject.toJSONString(smsParams));
//				authApply.setNo_agree(bankCard.getAgreeNo());
//				LianLianHelper helper = new LianLianHelper();
//				authApply = (AuthApplyModel) helper.authApply(authApply);
//				if (authApply.checkReturn()){
//					logger.info("Borrow",borrow.getOrderNo(),"授权成功");
//				}else{
//					logger.info("Borrow"+borrow.getOrderNo()+ "授权失败,原因："+authApply.getRet_msg());
//				}
//			}
//		}.start();
	}


	@Override
	public Page<ManageBRepayModel> listModel(Map<String, Object> params, int currentPage, int pageSize) {
		PageHelper.startPage(currentPage, pageSize);
		List<ManageBRepayModel> list = borrowRepayMapper.listModel(params);
		return (Page<ManageBRepayModel>) list;
	}

	@Override
	public Map<String, Object> confirmRepay(Map<String, Object> param) {
		Map<String, Object> result = new HashMap<String, Object>();
		Long id = (Long) param.get("id");
		logger.debug("进入确认还款...借款id="+id);
		logger.info("进入确认还款...借款state="+ param.get("state"));
		BorrowRepay br = borrowRepayMapper.findByPrimary(id);
		String state = (String) param.get("state");
		if ("10".equals(state)) {
			state = BorrowModel.STATE_FINISH;
			param.put("amount",StringUtil.isNull(br.getAmount()));
			param.put("penaltyAmout", StringUtil.isNull(br.getPenaltyAmout()));
		} else if ("20".equals(state)) {
			state = BorrowModel.STATE_REMISSION_FINISH;
			double repayAmount = NumberUtil.getDouble(param.get("amount") != null ? (String) param.get("amount") : "0");
			if (br.getAmount() < repayAmount) {
				result.put("Code", Constant.FAIL_CODE_VALUE);
				result.put("Msg", "还款金额不能大于应还金额");
				return result;
			}
			double penaltyAmount = NumberUtil.getDouble(param.get("penaltyAmout") != null ? (String) param.get("penaltyAmout") : "0");
			if (br.getPenaltyAmout() < penaltyAmount) {
				result.put("Code", Constant.FAIL_CODE_VALUE);
				result.put("Msg", "逾期罚金不能大于原逾期罚金");
				return result;
			}
		}
		logger.info("进入确认还款...借款,更新还款信息");
		Date repayTime = (Date) param.get("repayTime");
		// 更新还款信息
		int msg = updateBorrowReplay(br, repayTime, param);
		if (msg <= 0) {
			throw new BussinessException("更新还款信息出错" + br.getBorrowId());
		}
		// 更新借款表和借款进度状态
		msg = updateBorrow(br.getBorrowId(), br.getUserId(),state);
		if (msg <= 0) {
			throw new BussinessException("更新借款表和借款进度状态出错" + br.getBorrowId());
		}
		Borrow borrow=clBorrowMapper.findByPrimary(br.getBorrowId());
		// 信用额度修改
		Map<String, Object> creditMap = new HashMap<>();
		creditMap.put("consumerNo", br.getUserId());
		Credit credit = creditMapper.findSelective(creditMap);
		if (credit != null) {
			credit.setUnuse(credit.getUnuse() + borrow.getAmount());
			credit.setUsed(credit.getUsed() > borrow.getAmount() ? credit
					.getUsed() - borrow.getAmount() : 0);
			creditMapper.update(credit);
		} else {
			throw new BussinessException("用户信用额度信息不存在" + br.getUserId());
		}
		// 更新催收订单中的状态
		Map<String, Object> orderMap = new HashMap<>();
		orderMap.put("borrowId", br.getBorrowId());
		UrgeRepayOrder order = urgeRepayOrderService.findOrderByMap(orderMap);
		if (order != null) {
			logger.debug("更新存在的催收订单中的状态");
			UrgeRepayOrderLog orderLog = new UrgeRepayOrderLog();
			orderLog.setRemark("用户还款成功");
			orderLog.setWay("50");
			orderLog.setCreateTime(DateUtil.getNow());
			orderLog.setState(UrgeRepayOrderModel.STATE_ORDER_SUCCESS);
			urgeRepayOrderLogService.saveOrderInfo(orderLog, order);
		}

		// 判断是否有邀请人,若有邀请人则更新借款人的代理商资金奖励
		Map<String, Object> inviteMap = new HashMap<>();
		inviteMap.put("inviteId", br.getUserId());
		UserInvite invite = userInviteMapper.findSelective(inviteMap);
		if (StringUtil.isNotBlank(invite)) {
			// 判断是否已分配奖金
			Map<String, Object> profitMap = new HashMap<>();
			profitMap.put("borrowId", br.getBorrowId());
			int count = profitLogMapper.count(profitMap);
			if (count == 0) {
				profitLogService.save(br.getBorrowId(), DateUtil.getNow());
			}
		}
		result.put("Code", Constant.SUCCEED_CODE_VALUE);
		result.put("Msg", "还款成功");
        User user = cloanUserService.getById(borrow.getUserId());
        Channel channel = channelMapper.getChannelById(user.getChannelId());
		String[] counts;
		String repayCounts = channel.getCountImproveCredit();//达到当次提额的还款次数
		counts =repayCounts.split(",");
		Credit c = creditMapper.findByConsumerNo(StringUtil.isNull(br.getUserId()));
		//单次提额额度
		String oneRepayCredit=channel.getOneRepayCredit();

        String isImproveCredit = channel.getIsImproveCredit();//提额开关 -- 10开，20关
        String improveCreditLimit = channel.getImproveCreditLimit();//提额上限

        if (!BorrowModel.STATE_DELAY.equals(state) && Integer.parseInt(br.getPenaltyDay()) <= 0 && "10".equals(isImproveCredit)) {//未逾期且提额开关为10 ---提额

			String initCredit = channel.getInitCredit();//初始额度
			Double maxCredit = Double.valueOf(improveCreditLimit) + Double.valueOf(initCredit);//可使用额度的最大上线
			Map<String, Object> map = new HashMap<String, Object>();// 封装提额参数
			map.put("consumerNo", br.getUserId());
			map.put("total", oneRepayCredit);// 总额度
			map.put("unuse", oneRepayCredit);// 未使用额度

			Map<String, Object> mapMax = new HashMap<String, Object>();//封装最大额度参数
			mapMax.put("consumerNo", br.getUserId());
			mapMax.put("total", maxCredit);// 总额度
			mapMax.put("unuse", maxCredit);// 未使用额度

			Map<String, Object> numMap = new HashMap<>();
			numMap.put("id", credit.getId());


			if((maxCredit-c.getTotal()) < Double.parseDouble(oneRepayCredit) && maxCredit>c.getTotal()){
				logger.info("用户提额次数已达上限，进入修改用户额度");
				int y = 1;
				y = creditMapper.updateByConsumerNo(mapMax);
				if (y >= 1) {//添加额度修改日志
					CreditLog log = new CreditLog();
					log.setConsumerNo(c.getConsumerNo());
					log.setCreditType(c.getCreditType());
					log.setModifyTotal(maxCredit-c.getTotal());
					log.setModifyUser("system");
					log.setNow(maxCredit);
					log.setPre(c.getTotal());
					log.setRemark("还款成功，用户最大额度修改成功" + (maxCredit-c.getTotal()) + "（元）");
					log.setType("80");// 80 --提额到上限
					log.setModifyTime(new Date());
					creditLogMapper.save(log);
					logger.info("恭喜！！！用户最大额度修改成功");
				}else{
					logger.error("用户最大额度修改成功失败");
				}
			} else if(c.getCount()<counts.length) {
					int x = 1;
					if ((c.getCount() + 1) * Double.parseDouble(oneRepayCredit) <= Double.parseDouble(improveCreditLimit)) {
						creditMapper.addNum(numMap);// 提额度未达上限时，有效还款加1
						Credit b = creditMapper.findByConsumerNo(StringUtil.isNull(br.getUserId()));
						if (counts[b.getCount()].equals(b.getNum().toString())) {// 提额上线为500
							x = creditMapper.updateByUserId(map);
							creditMapper.subtractNum(numMap);// 提额成功时有效还款次数清零
							if (x >= 1) {//添加额度修改日志
								CreditLog log = new CreditLog();
								log.setConsumerNo(c.getConsumerNo());
								log.setCreditType(c.getCreditType());
								log.setModifyTotal(Double.parseDouble(oneRepayCredit));
								log.setModifyUser("system");
								log.setNow(c.getTotal() + Double.parseDouble(oneRepayCredit));
								log.setPre(c.getTotal());
								log.setRemark("还款成功，自动提额" + oneRepayCredit + "（元）");
								log.setType("70");// 70 --自动提额
								log.setModifyTime(new Date());
								creditLogMapper.save(log);
							}
						}
					}else {
						logger.error("自动提额失败");
					}
				}
			}
		return result;
	}

	/**
	 * 更新借款表和借款进度状态
	 *
	 * @param borrowId
	 * @param userId
	 * @return
	 */
	public int updateBorrow(long borrowId, long userId,String state){
		int i = 0;
		// 更新借款状态
		Map<String, Object> stateMap = new HashMap<String, Object>();
		stateMap.put("id", borrowId);
		stateMap.put("state", state);
		i=clBorrowMapper.updateSelective(stateMap);
		if(i>0){
			// 添加借款进度
			BorrowProgress bp = new BorrowProgress();
			bp.setBorrowId(borrowId);
			bp.setUserId(userId);
			bp.setRemark(BorrowModel.convertBorrowRemark(state));
			bp.setState(state);
			bp.setCreateTime(DateUtil.getNow());
			return borrowProgressMapper.save(bp);
		}
		return i;
	}

	/**
	 * 修改还款金额
	 * @param id
	 * @param repayTotal
	 * @return
	 */
	public int updateRepayAmount(Long id,Double repayTotal) {
		int i = 0;
        BorrowRepay rapay = borrowRepayMapper.findByPrimary(id);
        if(repayTotal>=rapay.getPenaltyAmout()){
            Map<String, Object> repayMap = new HashMap<String, Object>();
            repayMap.put("id",id);
            repayMap.put("repayAmount",repayTotal-rapay.getPenaltyAmout());
            i = borrowRepayMapper.updateRepayAmount(repayMap);
            if (i!=1){
                throw new BussinessException("修改还款金额失败");
            }
        }else if(repayTotal>=0){
            Map<String, Object> repayMap2 = new HashMap<String, Object>();
            repayMap2.put("id",id);
            repayMap2.put("repayAmount",repayTotal-repayTotal);
            repayMap2.put("penaltyAmout",repayTotal);
            i = borrowRepayMapper.updateRepayAmount(repayMap2);
            if (i!=1){
                throw new BussinessException("修改还款金额失败");
            }
        }else{
			throw new BussinessException("还款金额不能为负数");
		}
		return i;
	}
	/**
	 * 展期成功。
	 * @param param
	 * @return
	 */
	@Override
	public Map<String, Object> 	confirmDelayPay(Map<String, Object> param) {
		Map<String, Object> result = new HashMap<String, Object>();
		Long id = (Long) param.get("id");
		logger.debug("进入确认展期...借款id="+id);
		logger.info("进入确认展期...借款state="+ param.get("state"));
		BorrowRepay br = borrowRepayMapper.findByPrimary(id);
		String state = (String) param.get("state");

		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd");
		Date now = new Date();
		Date repayPlanTime = DateUtil.valueOf(time.format(br.getRepayTime()));
		Date nowDate = DateUtil.valueOf(time.format(now));
		Date repayTime = null;
		String type = br.getType();// 还款计划类型
		int delayDays;
		if(param.get("delayDays") != null) {
			delayDays = NumberUtil.getInt(param.get("delayDays").toString());
		} else {
			delayDays = 6;
		}
		if (nowDate.after(repayPlanTime)){
			repayTime = tool.util.DateUtil.rollDay(now, delayDays);
		}else {
			repayTime = tool.util.DateUtil.rollDay(br.getRepayTime(), delayDays);
		}
		result.put("repayTime", repayTime);
		// 更新还款信息
		int msg = updateBorrowReplayByDelayPay(br, repayTime);
		if (msg <= 0) {
			throw new BussinessException("更新还款信息出错" + br.getBorrowId());
		}
		//展期金额
		double repayAmount = NumberUtil.getDouble(param.get("amount") != null ? (String) param.get("amount") : "0.0") - br.getPenaltyAmout();
		if (br.getAmount() < repayAmount) {
			result.put("Code", Constant.FAIL_CODE_VALUE);
			result.put("Msg", "展期金额不能大于还款金额");
			return result;
		}
		//逾期罚金
		double penaltyAmount = param.get("penaltyAmout") != null ? NumberUtil.getDouble((String) param.get("penaltyAmout")) : br.getPenaltyAmout();
		if (br.getPenaltyAmout() < penaltyAmount) {
			result.put("Code", Constant.FAIL_CODE_VALUE);
			result.put("Msg", "逾期罚金不能大于原逾期罚金");
			return result;
		}
		//插入展期扣款的还款计划,状态为展期成功
		BorrowRepay newBr = new BorrowRepay();
		newBr.setBorrowAmount(br.getAmount());
		newBr.setAmount(repayAmount);
		newBr.setBorrowId(br.getBorrowId());
		newBr.setUserId(br.getUserId());
		String repay = DateUtil.dateStr2(DateUtil.rollDay(br.getRepayTime(), 0));
		repay = repay + " 23:59:59";
		newBr.setRepayTime(DateUtil.valueOf(repay, "yyyy-MM-dd HH:mm:ss"));
		newBr.setState(BorrowRepayModel.STATE_REPAY_DELAY_YES);
		newBr.setPenaltyAmout(penaltyAmount);
		newBr.setPenaltyDay(br.getPenaltyDay());
		newBr.setCreateTime(DateUtil.getNow());
		newBr.setType(type);
		msg = borrowRepayMapper.saveReturnId(newBr);
		if (msg <= 0) {
			throw new BussinessException("插入展期还款计划出错" + newBr.getBorrowId());
		}
		//新增一条展期还款记录
		BorrowRepayLog delayRepayLog = new BorrowRepayLog();
		delayRepayLog.setBorrowId(br.getBorrowId());
		delayRepayLog.setRepayId(newBr.getId());
		delayRepayLog.setUserId(br.getUserId());
		delayRepayLog.setAmount(repayAmount);// 实际还款金额
		delayRepayLog.setRepayTime(DateUtil.getNow());// 实际还款时间
		delayRepayLog.setPenaltyAmout(penaltyAmount);
		delayRepayLog.setPenaltyDay(br.getPenaltyDay());
		delayRepayLog.setSerialNumber((String) param.get("serialNumber"));
		delayRepayLog.setRepayAccount((String) param.get("repayAccount"));
		delayRepayLog.setRepayWay((String) param.get("repayWay"));
		delayRepayLog.setType(BorrowRepayLogModel.REPAY_TYPE_DELAY);
		delayRepayLog.setCreateTime(DateUtil.getNow());
		msg = borrowRepayLogMapper.save(delayRepayLog);
		if (msg <= 0) {
			throw new BussinessException("插入展期还款记录出错" + newBr.getBorrowId());
		}
		// 更新借款表和借款进度状态
		msg = updateBorrow(br.getBorrowId(), br.getUserId(),state);
		if (msg <= 0) {
			throw new BussinessException("更新借款表和借款进度状态出错" + br.getBorrowId());
		}

		// 更新催收订单中的状态
		Map<String, Object> orderMap = new HashMap<>();
		orderMap.put("borrowId", br.getBorrowId());
		UrgeRepayOrder order = urgeRepayOrderService.findOrderByMap(orderMap);
		if (order != null) {
			logger.debug("更新存在的催收订单中的状态");
			UrgeRepayOrderLog orderLog = new UrgeRepayOrderLog();
			orderLog.setRemark("展期成功");
			orderLog.setWay("50");
			orderLog.setCreateTime(DateUtil.getNow());
			//催收订单状态修改为催收成功
			orderLog.setState(UrgeRepayOrderModel.STATE_ORDER_SUCCESS);
			urgeRepayOrderLogService.saveOrderInfo(orderLog, order);
		}
		result.put("Code", Constant.SUCCEED_CODE_VALUE);
		result.put("Msg", "展期成功");
		return result;
	}

	/**
	 * 更新还款计划和还款记录表
	 *
	 * @param br
	 * @param repayTime
	 * @param param
	 * @return
	 */
	public int updateBorrowReplay(BorrowRepay br, Date repayTime,
								  Map<String, Object> param) {
		// 更新还款计划状态
		int i = 0;
		logger.info("进入确认还款...借款,更新还款计划状态");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", br.getId());
		paramMap.put("state", BorrowRepayModel.STATE_REPAY_YES);

		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd");
		Date repayPlanTime = DateUtil.valueOf(time.format(br.getRepayTime()));
		Date repay_time = DateUtil.valueOf(time.format(repayTime));

		if (StringUtil.isNotBlank(br.getPenaltyDay()) && br.getPenaltyAmout() > 0) {
			//实际还款时间在应还款时间之前或当天（不对比时分秒），重置逾期金额和天数
			//这里要考虑到逾期展期的客户,在结清的时候要处理逾期费用,所以这个逻辑去除
//			if (!repay_time.after(repayPlanTime)) {
//				br.setPenaltyDay(String.valueOf(0));
//				br.setPenaltyAmout(Double.valueOf(0));
//				paramMap.put("penaltyDay","0");
//				paramMap.put("penaltyAmout", 0.00);
//			}
		}
		i=borrowRepayMapper.updateParam(paramMap);
		if(i>0){
			// 生成还款记录
			BorrowRepayLog log = new BorrowRepayLog();
			log.setBorrowId(br.getBorrowId());
			log.setRepayId(br.getId());
			log.setUserId(br.getUserId());
			log.setAmount(Double.valueOf((String) param.get("amount")));// 实际还款金额
			log.setRepayTime(repayTime);// 实际还款时间
			log.setPenaltyDay(br.getPenaltyDay());
			// 实际还款时间在应还款时间之前或当天（不对比时分秒），重置逾期金额和天数
			//这里要考虑到逾期展期的客户,在结清的时候要处理逾期费用,所以这个逻辑去除
//			if (!repay_time.after(repayPlanTime)) {
//				log.setPenaltyAmout(0.00);
//				log.setPenaltyDay("0");
//			} else {
			// 金额减免时 罚金可页面填写
			String penaltyAmout = StringUtil.isNull(param.get("penaltyAmout"));
			if (StringUtil.isNotBlank(penaltyAmout)) {
				log.setPenaltyAmout(NumberUtil.getDouble(penaltyAmout));
			} else {
				log.setPenaltyAmout(br.getPenaltyAmout());
			}
//			}

			log.setSerialNumber((String) param.get("serialNumber"));
			log.setRepayAccount((String) param.get("repayAccount"));
			log.setRepayWay((String) param.get("repayWay"));
			log.setCreateTime(DateUtil.getNow());
			log.setType(BorrowRepayLogModel.REPAY_TYPE_CHARGE);
			return borrowRepayLogMapper.save(log);
		}
		return i;
	}
	public int updateBorrowReplayByDelayPay(BorrowRepay br,Date repayTime){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", br.getId());
		paramMap.put("repayTime", repayTime);
		paramMap.put("state", BorrowRepayModel.STATE_REPAY_NO);
		paramMap.put("penaltyAmout", "0.0");
		paramMap.put("penaltyDay", "0");
		paramMap.put("type","2");
		return borrowRepayMapper.updateParam(paramMap);
	}
	@Override
	public List<BorrowRepay> listSelective(Map<String, Object> paramMap) {
		return borrowRepayMapper.listSelective(paramMap);
	}


	@Override
	public int updateLate(BorrowRepay data) {
		return borrowRepayMapper.updateLate(data);
	}
	@Override
	public int updateSelective(Map<String , Object> paramMap) {
		return borrowRepayMapper.updateSelective(paramMap);
	}


	@Override
	public Page<ManageBorrowModel> listRepayModel(Map<String, Object> params,
												  int currentPage, int pageSize) {
		PageHelper.startPage(currentPage, pageSize);
		List<ManageBorrowModel> list = borrowRepayMapper.listRepayModel(params);
		return (Page<ManageBorrowModel>) list;
	}

	@Override
	public Page<ManageBorrowModel> listModelNotUrge(Map<String, Object> params,
													int currentPage, int pageSize) {
		PageHelper.startPage(currentPage, pageSize);
		List<ManageBorrowModel> list = borrowRepayMapper
				.listModelNotUrge(params);
		return (Page<ManageBorrowModel>) list;
	}

	@Override
	public List<BorrowRepay> findUnRepay(Map<String, Object> paramMap) {
		return borrowRepayMapper.findUnRepay(paramMap);
	}

	@Override
	public BorrowRepay findSelective(Map<String, Object> paramMap) {
		return borrowRepayMapper.findSelective(paramMap);
	}

	@Override
	public List<ManageBRepayModel> listAllModel(Map<String, Object> params) {
		List<ManageBRepayModel> list = borrowRepayMapper.listModel(params);

		for(ManageBRepayModel manageBRepayModel : list){
			if ("10".equals(manageBRepayModel.getState())){
				manageBRepayModel.setStateStr("已还款");
			}else if ("20".equals(manageBRepayModel.getState())){
				manageBRepayModel.setStateStr("未还款");
			}
			else if ("30".equals(manageBRepayModel.getState())){
				manageBRepayModel.setStateStr("展期还款");
			}else {
				manageBRepayModel.setStateStr("-");
			}
		}
		return list;
	}

	@Override
	public List<List<String>> fileBatchRepay(MultipartFile repayFile, String type) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", 20);
		List<ManageBRepayModel> list = borrowRepayMapper.listModel(params);
		List<List<String>>  result=new ArrayList<List<String>>();
		String ext = repayFile.getOriginalFilename().substring(repayFile.getOriginalFilename().lastIndexOf("."));
		if(".xlsx".equals(ext)||".xls".equals(ext)){
			String title = "批量还款匹配结果";
			if(type.equals("alpay")){
				result=parserByFile(repayFile,list);
			}else if("bank".equals(type)){
				result=toPaseBank(repayFile,list);
				RepayExcelModel.createWorkBook(result,title);
			}else{
				throw new BussinessException("请上传格式正确的文档。");
			}
		}else{
			throw new BussinessException("支持.xls和.xlsx格式文档，请上传格式正确的文档。");
		}
		return result;
	}

	/**
	 * 解析支付宝财务明显账单,与备注信息有匹配信息确认还款
	 * @param repayFile
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public List<List<String>> parserByFile(MultipartFile repayFile,List<ManageBRepayModel> list) throws Exception  {
		// csv文档格式解析
		// CsvParser parser=new CsvParser("");
		// arr=parser.getParsedArrayList(repayFile);
		// xls文档格式解析
		ReadExcelUtils excelReader = new ReadExcelUtils(repayFile);
		List<List<String>> arr = excelReader.readExcelContent();
		// logger.info("获得支付宝账单表格的内容:"+JSONObject.toJSONString(arr));
		int j = 0;
		List<AlipayModel> alipayList = new ArrayList<AlipayModel>();
		AlipayModel model = null;
		for (int i = 0; i < arr.size(); i++) {
			model = new AlipayModel();
			List<String> ls = arr.get(i);
			for (int k = 0; k < ls.size(); k++) {
				String item = ls.get(k);
				if ("".equals(item)) {
					continue;
				}
				if (item.contains("账务流水号")) {
					j = i;
					arr.get(i).add(ls.size(), "是否有备注信息与还款计划匹配");
				}
			}
			if (j != 0 && j + 1 < ls.size() && j + 1 <= i) {
				model.setSerialNumber(ls.get(1));
				model.setAccount(ls.get(5));
				model.setAmount(ls.get(6));
				model.setRemark(ls.get(11));
				model.setRepayTime(ls.get(4) != null ? DateUtil.valueOf(ls.get(4),"yyyy/MM/dd HH:mm") : null);
				if (model != null && model.getAccount() != null) {
					alipayList.add(model);
				}
				boolean flag = false;
				flag = remarkPay(flag, model, list, BorrowRepayLogModel.REPAY_WAY_ALIPAY_TRANSFER);
				arr.get(i).add(ls.size(), flag ? "有" : "无");
			}
		}
		if(alipayList.size()<=0){
			throw new BussinessException("没有解析到匹配的数据，请上传正确的文档");
		}
		// logger.info("=获得支付宝账单表格的内容==="+JSONObject.toJSONString(arr));
		return arr;
	}

	public List<List<String>> toPaseBank(MultipartFile file,List<ManageBRepayModel> list) throws Exception{
		ReadExcelUtils excelReader = new ReadExcelUtils(file);
		List<List<String>> arr = excelReader.readExcelContent();
		//logger.info("获得银行卡帐单内容:" + JSONObject.toJSONString(arr));
		List<AlipayModel> alipayList = new ArrayList<AlipayModel>();
		AlipayModel model = null;
		int j = 0;
		for (int i = 0; i < arr.size(); i++) {
			model = new AlipayModel();
			List<String> ls = arr.get(i);
			for (int k = 0; k < ls.size(); k++) {
				String item = ls.get(k);
				if ("".equals(item)) {
					continue;
				}
				if (item.contains("交易日")) {
					j = i;
					arr.get(i).add(ls.size(), "是否有备注信息与还款计划匹配");
				}
			}
			if (j != 0 && j + 1 < ls.size() && j + 1 <= i) {
				String repayTime = ls.get(0) + ls.get(1);
				model.setRepayTime(repayTime != "" ? DateUtil.valueOf(repayTime, DateUtil.DATEFORMAT_STR_011) : null);
				model.setSerialNumber(ls.get(8));
				model.setAccount(ls.get(17));
				model.setAmount(ls.get(6));
				model.setRemark(ls.get(7));
				if (model != null && model.getAccount() != null) {
					alipayList.add(model);
				}
				boolean flag = false;
				flag = remarkPay(flag, model, list, BorrowRepayLogModel.REPAY_WAY_TRANSFER);
				arr.get(i).add(ls.size(), flag ? "有" : "无");
			}
		}
		if(alipayList.size()<=0){
			throw new BussinessException("没有解析到匹配的数据，请上传正确的文档");
		}
		// logger.info("==对比之后的值==:"+JSONObject.toJSONString(arr));
		return arr;
	}
	/**
	 * 对账文档解析结果与还款计划对比
	 * @param flag
	 * @param model
	 * @param list
	 * @return
	 */
	public boolean remarkPay(boolean flag, AlipayModel model, List<ManageBRepayModel> list, String repayWay){
		for (ManageBRepayModel repay : list) {
			// 账单中存在未还款的用户信息   只匹配手机号码
			if (model.getRemark().contains(repay.getPhone())) {
				flag=true;
				logger.debug("批量还款匹配到的还款数据==" + repay.getId() + "="+ model.getRemark() + "=" + repay.getPhone()+ "=" + repay.getRealName());
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("id", repay.getId());
				param.put("repayTime", model.getRepayTime());
				param.put("repayWay", repayWay);
				param.put("repayAccount", model.getAccount());
				param.put("penaltyAmout", 0);
				if (Double.valueOf(model.getAmount()) < repay.getRepayAmount()) {
					param.put("state", "20");
				} else {
					param.put("state", "10");
				}
				param.put("serialNumber", model.getSerialNumber());
				param.put("amount", model.getAmount());
				confirmRepay(param);
				break;
			}
		}
		return flag;
	}

	@Override
	public Map<String, String> repayment(Long borrowId,Long userId, String ip) {
		// 借款状态判断，如果不是贷款中或者逾期，抛出异常
		Borrow borrow = clBorrowMapper.findByPrimary(borrowId);
		if (borrow == null) {
			throw new BussinessException("借款信息有误");
		}

		String state = borrow.getState();
		if (BorrowModel.STATE_REPAY_PROCESSING.equals(state)) {
			throw new BussinessException("还款正在处理中");
		}
		if (!BorrowModel.STATE_REPAY.equals(state) &&
				!BorrowModel.STATE_DELAY.equals(state) &&
			!BorrowModel.STATE_BAD.equals(state) &&
			!BorrowModel.STATE_DELAY_PAY.equals(state)) {
			throw new BussinessException("借款状态有误");
		}

		// 根据借款标识(还款标识) 查询应还款金额
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("borrowId", borrowId);
		paramMap.put("userId", userId);
		paramMap.put("state", BorrowRepayModel.STATE_REPAY_NO);
		BorrowRepay borrowRepay = findSelective(paramMap);

		// 还款金额
		double principal = borrowRepay.getAmount();
		double penaltyAmout = borrowRepay.getPenaltyAmout();
		double amount = BigDecimalUtil.add(principal ,penaltyAmout);

		// 根据类型 走不同支付渠道，获取SDK所需参数
		String orderNo = OrderNoUtil.getSerialNumber();
		// APP应用 + 小额贷款

		BankCard bankCard = bankCardService.getBankCardByUserId(borrow.getUserId());
		if (bankCard == null) {
			throw new BussinessException("支付失败");
		}
		Map<String, String> sdkParam = paySdkParams(userId, bankCard.getAgreeNo(), amount, orderNo);

		// 若参数封装失败，直接返回失败，业务不再执行
		if (sdkParam == null || sdkParam.size() < 1) {
			throw new BussinessException("支付失败");
		}

		// 若参数封装成功， 标的状态修改为赎回处理中（还款处理中）
		// updateBorrow(borrowId, userId, BorrowModel.STATE_REPAY_PROCESSING);

		// 插入一条待支付的支付记录
		String cardNo = bankCard.getCardNo();
		String bank = bankCard.getBank();

		String scenes = PayLogModel.SCENES_ACTIVE_REPAYMENT;

		payLogService.savePayLog(orderNo, userId, borrowId, amount, cardNo, bank, scenes);
		PayReqLog payReqLog = new PayReqLog();
		payReqLog.setOrderNo(orderNo);
		payReqLog.setService("authPay");
		payReqLog.setCreateTime(DateUtil.getNow());
		payReqLog.setParams(JSONObject.toJSONString(sdkParam));
		payReqLog.setReqDetailParams(JSONObject.toJSONString(sdkParam));
		payReqLog.setIp(ip);
		payReqLogMapper.save(payReqLog);

		return sdkParam;
	}

	/**
	 * 参数封装
	 *
	 * @return
	 */
	@Override
	public Map<String, String> paySdkParams(Long userId, String agreeNo, double amount, String orderNo) {

		// 查询所需参数
		User user = userMapper.findByPrimary(userId);
		UserBaseInfo userBaseInfo = userBaseInfoMapper.findByUserId(userId);
		if (StringUtil.isBlank(agreeNo)) {
			throw new BussinessException(StringUtil.isNull(Constant.FAIL_CODE_VALUE), "银行卡未绑定,请先绑定银行卡");
		}

		// 连连认证支付所需参数加入Map集合
		CertifiedPayModel certifiedPay = new CertifiedPayModel(orderNo);
		certifiedPay.setNo_agree(agreeNo);
		certifiedPay.setNotify_url(Global.getValue("server_host") + "/pay/lianlian/certifiedNotify.htm");
		String registTime = DateUtil.dateStr3(user.getRegistTime());
		String sdkParams = LianLianHelper.certifiedPay(user.getUuid(), registTime, userBaseInfo, certifiedPay, amount);

		Map<String, String> params = new HashMap<String, String>();
		params.put("payOrderNo", orderNo);
		params.put("sdkParams", sdkParams);
		return params;

	}

	@Override
	public void repaymentReturn(String payResult, String payOrderNo) {
		// 根据订单号查询支付记录
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("orderNo", payOrderNo);
		PayLog payLog = payLogMapper.findSelective(paramMap);
		PayReqLog reqLog = payReqLogMapper.findSelective(paramMap);
		if(reqLog!=null){
			reqLog.setReturnParams(payResult);
			reqLog.setReturnTime(new Date());
			payReqLogMapper.update(reqLog);
		}

		if (PayLogModel.STATE_PAYMENT_WAIT.equals(payLog.getState())) {
			logger.info("已处理");
			return;
		}
		// 失败时更改支付记录状态， 成功更新借款状态， 支付状态等待异步再修改
		String state = PayLogModel.STATE_PAYMENT_FAILED;
		if (LianLianConstant.RESULT_SUCCESS.equals(payResult) || LianLianConstant.RESULT_PROCESSING.equals(payResult)) {
			state = PayLogModel.STATE_PAYMENT_SUCCESS;
		}

		if (PayLogModel.STATE_PAYMENT_FAILED.equals(state)) {
			payLog.setState(state);
			payLogMapper.update(payLog);
		}
		Borrow borrow = clBorrowMapper.findByPrimary(payLog.getBorrowId());
		if(BorrowModel.STATE_FINISH.equals(borrow.getState())){
			clBorrowService.modifyBorrowState(payLog.getBorrowId(), payLog.getUserId(),BorrowModel.STATE_REPAY_PROCESSING);
		}else{
			logger.error("还款同步回调：借款状态不可修改");
		}

		/*if(BorrowModel.STATE_REPAY.equals(borrow.getState())
				|| BorrowModel.STATE_DELAY.equals(borrow.getState())
				|| BorrowModel.AUDIT_LOAN_PASS.equals(borrow.getState())
				|| BorrowModel.STATE_BAD.equals(borrow.getState())){
			int msg = clBorrowService.modifyBorrowState(payLog.getBorrowId(), payLog.getUserId(),BorrowModel.STATE_REPAY_PROCESSING);
			if (msg<1) {
				logger.error("保存还款中状态失败");
			}
		}else{
			logger.error("还款同步回调：借款状态不可修改");
		}*/
	}

	@Override
	public void repaymentNotify(PayLog payLog, String logState, String amount, String repayWay, String repayAccount) {

		Map<String, Object> repayMap = new HashMap<String, Object>();
		repayMap.put("userId", payLog.getUserId());
		repayMap.put("borrowId", payLog.getBorrowId());
		BorrowRepay borrowRepay = borrowRepayMapper.findByBorrowIdState(repayMap);
		// 若已完成还款，那么直接返回success
		if (borrowRepay == null || BorrowRepayModel.STATE_REPAY_YES.equals(borrowRepay.getState())) {
			logger.warn("还款计划有误");
			return;
		}

		Long borrowId = borrowRepay.getBorrowId();

		// 判断交易状态，若为成功 调用确认还款； 若为失败， 修改还款状态
		if (PayLogModel.STATE_PAYMENT_SUCCESS.equals(logState)) {
			logger.info("还款支付成功，订单还款及生成续借记录...");
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("id", borrowRepay.getId());
			param.put("repayTime", DateUtil.getNow());
			param.put("repayWay", repayWay);
			param.put("repayAccount", "");
			param.put("amount", amount);
			param.put("serialNumber", payLog.getOrderNo());
			param.put("penaltyAmout", borrowRepay.getPenaltyAmout());
			param.put("state", BorrowRepayModel.STATE_REPAY_YES);

			confirmRepay(param);

			// 发送扣款成功短信
			clSmsService.repayInform(borrowRepay.getUserId(), borrowId);
		} else if (PayLogModel.STATE_PAYMENT_FAILED.equals(logState)) {
			logger.info("还款支付失败，还原借款状态");
			String state = "";
			// 如果还款时间在当前时间前面，说明已经逾期
			if (borrowRepay.getRepayTime().before(DateUtil.getNow())) {
				state = BorrowModel.STATE_DELAY;
			} else {
				state = BorrowModel.STATE_REPAY;
			}
			// 修改标的状态为 借款中/逾期
			clBorrowMapper.updateState(state, borrowId);

		}
	}

	@Override
	public void repayCheck(long borrowId) {
		Borrow borrow = clBorrowMapper.findByPrimary(borrowId);

		Map<String, Object> payLogMap = new HashMap<String, Object>();
		payLogMap.put("borrowId", borrowId);
		payLogMap.put("type", PayLogModel.TYPE_AUTH_PAY);
		payLogMap.put("scenes",PayLogModel.SCENES_ACTIVE_REPAYMENT);
		PayLog log = payLogService.findLatestOne(payLogMap);

		if (log!=null&&!PayLogModel.STATE_PAYMENT_FAILED.equals(log.getState())) {
			RepaymentQueryVo vo = new RepaymentQueryVo();
			vo.setOrderNo(log.getOrderNo());
			vo.setShareKey(log.getUserId());
			RepaymentQueryResponseVo responseVo = PayCommonUtil.queryOrder(vo);
			if (StringUtil.equals(responseVo.getCode(),PayConstant.QUERY_PAY_SUCCESS)) {
				// 更新订单状态
				repaymentNotify(log,PayLogModel.STATE_PAYMENT_SUCCESS,
						StringUtil.isNull(log.getAmount()), BorrowRepayLogModel.REPAY_WAY_CERTIFIED,
						log.getCardNo());
				return;
			}

			BorrowRepayLog repayLog = borrowRepayLogMapper.findByBorrowId(borrowId);
			if (repayLog == null ){
				borrow.setState(BorrowModel.STATE_REPAY);
				clBorrowMapper.update(borrow);
			}
		} else {
			throw new BussinessException(StringUtil.isNull(Constant.FAIL_CODE_VALUE), "订单号不存在");
		}
	}

	@Override
	public Map<String, String> confirmPay(Long borrowId,Long userId, String ip,String type) {
		Map<String, String> result = new HashMap<>();
		// 查询用户、用户详情、借款及用户银行卡信息
		User user = cloanUserService.getById(userId);
//		UserBaseInfo baseInfo = userBaseInfoService.findByUserId(userId);
		Borrow borrow = clBorrowService.getById(borrowId);
        BankCard bankCard = bankCardService.getBankCardByUserId(userId);
        Channel channel = channelMapper.getChannelById(user.getChannelId());

        if (PayCommonHelper.isEmpty(bankCard)) {
			result.put("code", "12");
			result.put("msg", "绑定的银行卡信息失效，请重新绑卡！");
			return result;
		}

		Map<String, Object> paramRepayMap = new HashMap<String, Object>();
		paramRepayMap.put("borrowId", borrowId);
		paramRepayMap.put("userId", userId);
		paramRepayMap.put("state", BorrowRepayModel.STATE_REPAY_NO);
		BorrowRepay borrowRepay = findSelective(paramRepayMap);

		//1、查询是否存在待支付记录
		//检查待还款记录
		Map<String, String> checkResult = checkRepaymentLog(borrowRepay, bankCard);
		if (checkResult != null) {
			return checkResult;
		}
		//检查展期支付的记录
		checkResult = checkDelayPayLog(borrowRepay, bankCard);
		if (checkResult != null) {
			return checkResult;
		}

		//2、走支付逻辑
		Double sourceAmount = 0.0;
		if ((StringUtil.equals("2", type))) {
            Double amount = borrow.getAmount();
            Double delay_fee=0.0;
            Double delayFee=0.0;
            //获取展期费用的占比,
            if(StringUtil.isNotBlank(channel.getDelayFee())) {
                delay_fee=Double.parseDouble(channel.getDelayFee());
                //展期费用
                delayFee=amount * delay_fee;
            }else {
                //展期费用为综合费用;
                delayFee=borrow.getFee();
            }
			//展期扣除部费用
			if(borrowRepay.getPenaltyAmout() > 0) {
				sourceAmount = BigDecimalUtil.add(delayFee,borrowRepay.getPenaltyAmout());
			} else {
				sourceAmount = delayFee;
			}
		}else {
			// 还款金额
			double principal = borrowRepay.getAmount();
			double penaltyAmout = borrowRepay.getPenaltyAmout();
			sourceAmount = BigDecimalUtil.add(principal ,penaltyAmout);
		}

		//其他情况，为代扣还款
		Date payReqTime = DateUtil.getNow();
		RepaymentReqVo vo = new RepaymentReqVo();
		if ("dev".equals(Global.getValue("app_environment"))) {
			vo.setAmount(0.2);
		} else {
			vo.setAmount(sourceAmount);
		}

		if (StringUtil.isNotEmpty(ip)) {
			vo.setIp(ip);
		}else {
			vo.setIp(IpUtil.getLocalIp());
		}

		vo.setUserId(user.getUuid());
		vo.setProtocolNo(bankCard.getAgreeNo());
		vo.setBorrowOrderNo(borrow.getOrderNo());
		if ((StringUtil.equals("2", type))) {
			vo.setRemark("展期还款" + borrow.getOrderNo());
		} else {
			vo.setRemark("还款" + borrow.getOrderNo());
		}

		vo.setRemark2("repayment_" + borrow.getOrderNo());
		vo.setTerminalId(UUID.randomUUID().toString());
		vo.setTerminalType("OTHER");
		vo.setShareKey(bankCard.getUserId());
		RepaymentResponseVo responseVo = PayCommonUtil.repayment(vo);

		String payOrderNo = "";
		boolean paySuccess = false;
		if (PayCommonUtil.success(responseVo.getStatus())) {
			paySuccess = true;
			payOrderNo = responseVo.getPayPlatNo();
		}else {
			result.put("code", "12");
			result.put("msg", responseVo.getMessage());
		}
		PayLog payLog = new PayLog();
		payLog.setOrderNo(responseVo.getOrderNo());
		if (StringUtil.isNotEmpty(payOrderNo)) {
			payLog.setPayOrderNo(payOrderNo);
		}
		payLog.setUserId(userId);
		payLog.setBorrowId(borrowId);
		payLog.setAmount(sourceAmount);
		payLog.setCardNo(bankCard.getCardNo());
		payLog.setBank(bankCard.getBank());
		payLog.setSource(PayLogModel.SOURCE_FUNDS_OWN);

		//只有2为展期
		if (StringUtil.equals("2", type)) {
			payLog.setType(PayLogModel.TYPE_AUTH_DELAY);
			payLog.setScenes(PayLogModel.SCENES_ACTIVE_DELAYPAY);
			if (paySuccess) {
				result.put("code", "10");
				result.put("msg", "展期处理中,请耐心等候！");
			}
		} else {
			payLog.setType(PayLogModel.TYPE_AUTH_PAY);
			payLog.setScenes(PayLogModel.SCENES_ACTIVE_REPAYMENT);
			if (paySuccess) {
				result.put("code", "10");
				result.put("msg", "还款处理中,请耐心等候！");
			}
		}

		payLog.setState(PayLogModel.STATE_PAYMENT_WAIT);
		payLog.setCode(responseVo.getStatusCode());
		payLog.setRemark(responseVo.getMessage());
		payLog.setPayReqTime(payReqTime);
		payLog.setCreateTime(DateUtil.getNow());
		payLogService.save(payLog);

		return result;
	}

	@Override
	public Map<String, String> getReqParameter(Long borrowId, long userId ,String ip,String type) {
        Map<String, String> reqParameterMap = new HashMap<>();
        // 查询用户、用户详情、借款及用户银行卡信息
        User user = cloanUserService.getById(userId);
		UserBaseInfo baseInfo = userBaseInfoService.findByUserId(userId);
        Borrow borrow = clBorrowService.getById(borrowId);
        BankCard bankCard = bankCardService.getBankCardByUserId(userId);

        if (PayCommonHelper.isEmpty(bankCard)) {
            reqParameterMap.put("code", "12");
            reqParameterMap.put("msg", "绑定的银行卡信息失效，请重新绑卡！");
            return reqParameterMap;
        }

        Map<String, Object> paramRepayMap = new HashMap<String, Object>();
        paramRepayMap.put("borrowId", borrowId);
        paramRepayMap.put("userId", userId);
        paramRepayMap.put("state", BorrowRepayModel.STATE_REPAY_NO);
        BorrowRepay borrowRepay = findSelective(paramRepayMap);

        //1、查询是否存在待支付记录
        //检查待还款记录
        Map<String, String> checkResult = checkRepaymentLog(borrowRepay, bankCard);
        if (checkResult != null) {
            return checkResult;
        }
        //检查展期支付的记录
        checkResult = checkDelayPayLog(borrowRepay, bankCard);
        if (checkResult != null) {
            return checkResult;
        }

        //2、走支付逻辑
        Double sourceAmount = 0.0;
        if ((StringUtil.equals("2", type))) {
            //展期扣除部费用
            if(borrowRepay.getPenaltyAmout() > 0) {
                sourceAmount = BigDecimalUtil.add(borrow.getFee() ,borrowRepay.getPenaltyAmout());
            } else {
                sourceAmount = borrow.getFee();
            }
        }else {
            // 还款金额
            double principal = borrowRepay.getAmount();
            double penaltyAmout = borrowRepay.getPenaltyAmout();
            sourceAmount = BigDecimalUtil.add(principal ,penaltyAmout);
        }

        //其他情况，为代扣还款
        RepaymentReqVo vo = new RepaymentReqVo();
        if ("dev".equals(Global.getValue("app_environment"))) {
            vo.setAmount(2.0);//以分为单位;
        } else {
            vo.setAmount(sourceAmount);
        }

        if (StringUtil.isNotEmpty(ip)) {
            vo.setIp(ip);
        }else {
            vo.setIp(IpUtil.getLocalIp());
        }

        vo.setUserId(user.getUuid());
        vo.setProtocolNo(bankCard.getAgreeNo());
        vo.setBorrowOrderNo(borrow.getOrderNo());
        if ((StringUtil.equals("2", type))) {
            vo.setRemark("展期还款" + borrow.getOrderNo());
        } else {
            vo.setRemark("还款" + borrow.getOrderNo());
        }

        vo.setRemark2("repayment_" + borrow.getOrderNo());
        vo.setTerminalId(UUID.randomUUID().toString());
        vo.setTerminalType("OTHER");
        vo.setShareKey(bankCard.getUserId());
        String mchntcd = Global.getValue("fuiou_protocol_mchntcd");
        OrderXmlBeanReq beanReq = new OrderXmlBeanReq();
        beanReq.setUserId(vo.getUserId());
        beanReq.setUserIp(IpUtil.getLocalIp());
        beanReq.setType("02");
        beanReq.setMchntCd(mchntcd);
        String orderNo = OrderNoUtil.getSerialNumber();
        beanReq.setMchntOrderId(orderNo);
        if ("dev".equals(Global.getValue("app_environment"))) {
            beanReq.setAmt(AmtUtil.convertAmtToBranch("0.02"));
        } else {
            beanReq.setAmt(AmtUtil.convertAmtToBranch(vo.getAmount()));
        }
        beanReq.setProtocolNo(vo.getProtocolNo());
        beanReq.setBackUrl(Global.getValue("server_host")+ "/pay/fuiou/repaymentNotify.htm");
        beanReq.setVersion(FuiouConstant.BIBIVERIFY_VERSION);
        beanReq.setCardNo(bankCard.getCardNo());
        beanReq.setUserName(baseInfo.getRealName());
        beanReq.setIdCardNo(baseInfo.getIdNo());
        beanReq.setIdCardType(FuiouConstant.BIBIVERIFY_IDCARDTYPE);//身份证0
        reqParameterMap.put("mchntCd",mchntcd);
        reqParameterMap.put("amt",AmtUtil.convertAmtToBranch(vo.getAmount()) + "");
        reqParameterMap.put("orderId",orderNo);
        String key = Global.getValue("fuiou_protocol_mchntcd_key");//商户密钥
        String privatekey = Global.getValue("fuiou_protocol_privatekey");//商户RSA私钥
        String signStr = beanReq.signStrMsg(key);
        try {
            signStr = FuiouAgreementPayHelper.getSign(signStr, FuiouConstant.SIGNTP, privatekey);//MD5加密sign
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw  new RuntimeException("MD5加密异常");
        }
        logger.info("MD5加密后的signStr:"+signStr);
        reqParameterMap.put("signKey",signStr);
        reqParameterMap.put("userId",vo.getUserId());
        reqParameterMap.put("cardNo",bankCard.getCardNo());
        reqParameterMap.put("idNo",baseInfo.getIdNo());
        reqParameterMap.put("IDCardType",FuiouConstant.BIBIVERIFY_IDCARDTYPE);//身份证0
        reqParameterMap.put("userName",baseInfo.getRealName());
        reqParameterMap.put("backUrl",Global.getValue("server_host")+ "/pay/fuiou/repaymentNotify.htm");
        reqParameterMap.put("payType","mobilePay");//手机支付固定mobilePay
        //保存支付请求记录表
        PayReqLog payReqLog = new PayReqLog();
        payReqLog.setOrderNo(orderNo);
        payReqLog.setService(FuiouConstant.BIBIVERIFY_ORDER);
        payReqLog.setCreateTime(tool.util.DateUtil.getNow());
        payReqLog.setParams(signStr);
        payReqLog.setReqDetailParams(JSON.toJSONString(beanReq));
        if (null != ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())){
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String remortIp = IPUtil.getRemortIP(request);
            payReqLog.setIp(remortIp);
        }
        payReqLogMapper.save(payReqLog);
        //保存支付记录表
        Date payReqTime = DateUtil.getNow();
        PayLog payLog = new PayLog();
        payLog.setOrderNo(orderNo);
        payLog.setUserId(userId);
        payLog.setBorrowId(borrowId);
        payLog.setAmount(sourceAmount);
        payLog.setCardNo(bankCard.getCardNo());
        payLog.setBank(bankCard.getBank());
        payLog.setSource(PayLogModel.SOURCE_FUNDS_OWN);
        //只有2为展期
        if (StringUtil.equals("2", type)) {
            payLog.setType(PayLogModel.TYPE_AUTH_DELAY);
            payLog.setScenes(PayLogModel.SCENES_ACTIVE_DELAYPAY);
        } else {
            payLog.setType(PayLogModel.TYPE_AUTH_PAY);
            payLog.setScenes(PayLogModel.SCENES_ACTIVE_REPAYMENT);
        }

        payLog.setState(PayLogModel.STATE_PAYMENT_WAIT);
        payLog.setPayReqTime(payReqTime);
        payLog.setCreateTime(DateUtil.getNow());
        payLogService.save(payLog);

        return reqParameterMap;
	}

    @Override
    public Map<String, String> saveResParameter(String body) {

        //创建响应对象
        OrderXmlBeanResp bindResult = new OrderXmlBeanResp();
        RepaymentResponseVo responseVo = new RepaymentResponseVo();
        Map<String, String> result = new HashMap<>();
        String key = Global.getValue("fuiou_protocol_mchntcd_key");
        if (!isException(body)) {
            try{
                //将ios返回的字典转换成json字符串
                body=URLDecoder.decode(body);
                //将json字符串转换成OrderXmlBeanResp对象,
                bindResult = JSONObject.parseObject(body, OrderXmlBeanResp.class);
            }catch (Exception e){
                body = body.replace("\"", "");
                body =body.replace("\\", "\"");
                //将Android返回的xml结果转换成对象
                bindResult = XMapUtil.parseStr2Obj(OrderXmlBeanResp.class, body);
            }
        } else {
            bindResult.setErrorCode(body);
        }
        //获取PayLogService
        PayLogService payLogService = (PayLogService) BeanUtil.getBean("payLogService");
        //根据商户订单号获取paylog
        PayLog payLog = payLogService.findByOrderNo(bindResult.getMchntOrderId());
        if (payLog==null){
            result.put("code","12");
            result.put("msg","该商户订单号不存在.");
            return result;
        }
        String payOrderNo = "";
        String payMsg = bindResult.getResponseMsg();
        //结果验签
        if (bindResult.checkSign(key)) {
            if (bindResult.checkReturn() && StringUtil.isNotEmpty(bindResult.getOrderId())) {
                payMsg = bindResult.getOrderId()+"|" + payMsg;
            }
            responseVo.setStatus(PayConstant.RESULT_SUCCESS);
            responseVo.setPayPlatNo(bindResult.getOrderId());
        }else {
            responseVo.setStatus(PayConstant.STATUS_FAIL);
        }

        if (PayCommonUtil.success(responseVo.getStatus())) {
            payOrderNo = bindResult.getOrderId();
        }
        if (StringUtil.isNotEmpty(payOrderNo)) {
            payLog.setPayOrderNo(payOrderNo);
        }
        payLog.setCode(bindResult.getResponseCode());
        payLog.setRemark(payMsg);
        //更新paylog返回的数据
        int i = payLogService.updateById(payLog);
        //更新payReqlog数据
        int j = modifyPayReqLog(bindResult.getMchntOrderId(), body);
        if (i>0 && j>0){
            result.put("code","10");
            result.put("msg","参数更新成功.");
        }else{
            result.put("code","12");
            result.put("msg","更新失败.");
        }
        return result;
    }

    /**
     * 更新返回数据
     *
     * @param orderNo
     * @param resp
     */
    protected int modifyPayReqLog(String orderNo, String resp) {
        PayReqLogService payReqLogService = (PayReqLogService) BeanUtil.getBean("payReqLogService");
        PayReqLog reqLog = payReqLogService.findByOrderNo(orderNo);
        if (null == reqLog) {
            return 0;
        }
        reqLog.setReturnParams(resp);
        reqLog.setReturnTime(tool.util.DateUtil.getNow());
        return payReqLogService.updateById(reqLog);
    }

    protected boolean isException(String resp) {
        return StringUtils.startsWith(resp, "300");
    }


    public Map<String, String> checkRepaymentLog(BorrowRepay borrowRepay,BankCard bankCard) {
		Map<String, String> result = new HashMap<>();
		Map<String, Object> payLogMap = new HashMap<String, Object>();
		payLogMap.put("userId", borrowRepay.getUserId());
		payLogMap.put("borrowId", borrowRepay.getBorrowId());
		payLogMap.put("type", PayLogModel.TYPE_AUTH_PAY);
		payLogMap.put("scenes", PayLogModel.SCENES_ACTIVE_REPAYMENT);
		PayLog repaymentLog = payLogService.findLatestOne(payLogMap);
		//是否存在还款记录
		if (null != repaymentLog && !PayLogModel.STATE_PAYMENT_FAILED.equals(repaymentLog.getState())) {
			if (PayLogModel.STATE_PAYMENT_SUCCESS.equals(repaymentLog.getState())) {
				result.put("code", "10");
				result.put("msg", "还款成功！");
				return result;
			}
			RepaymentQueryVo vo = new RepaymentQueryVo();
			vo.setOrderNo(repaymentLog.getOrderNo());
			vo.setPayPlatNo(repaymentLog.getPayOrderNo());
			vo.setShareKey(repaymentLog.getUserId());
			RepaymentQueryResponseVo responseVo = PayCommonUtil.queryOrder(vo);

			if (StringUtil.equals(responseVo.getCode(), PayConstant.QUERY_PAY_SUCCESS)) {
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
					confirmRepay(param);
				}

				// 更新订单状态
				Map<String, Object> payLogParamMap = new HashMap<String, Object>();
				payLogParamMap.put("state",PayLogModel.STATE_PAYMENT_SUCCESS);
				payLogParamMap.put("updateTime", DateUtil.getNow());
				payLogParamMap.put("id", repaymentLog.getId());
				payLogService.updateSelective(payLogParamMap);

				// 发送代扣还款成功短信提醒
				clSmsService.repayInform(borrowRepay.getUserId(), borrowRepay.getBorrowId());
				result.put("code", "10");
				result.put("msg", "还款成功！");
				return result;
			}else if (StringUtil.equals(responseVo.getCode(),PayConstant.QUERY_PAY_PROCESSING)) {
				result.put("code", "11");
				result.put("msg", "还款处理中，请稍后再试!");
				return result;
			} else if (StringUtil.equals(responseVo.getCode(), PayConstant.QUERY_PAY_FAIL)) {
				// 更新订单状态
				Map<String, Object> payLogParamMap = new HashMap<String, Object>();
				payLogParamMap.put("state", PayLogModel.STATE_PAYMENT_FAILED);
				payLogParamMap.put("updateTime", DateUtil.getNow());
				payLogParamMap.put("id", repaymentLog.getId());
				payLogService.updateSelective(payLogParamMap);
			} else {
				result.put("code", "12");
				result.put("msg", "调用支付接口异常！");
				return result;
			}
		}
		return null;
	}
	public Map<String, String> checkDelayPayLog(BorrowRepay borrowRepay,BankCard bankCard) {
		Map<String, String> result = new HashMap<>();
		Map<String, Object> payLogMap = new HashMap<String, Object>();
		payLogMap.put("userId", borrowRepay.getUserId());
		payLogMap.put("borrowId", borrowRepay.getBorrowId());
		payLogMap.put("type", PayLogModel.TYPE_AUTH_DELAY);
		payLogMap.put("scenes", PayLogModel.SCENES_ACTIVE_DELAYPAY);
		PayLog repaymentLog = payLogService.findLatestOne(payLogMap);
		//是否存在还款记录
		if (null != repaymentLog && (PayLogModel.STATE_PAYMENT_WAIT.equals(repaymentLog.getState())
			|| PayLogModel.STATE_PENDING_REVIEW.equals(repaymentLog.getState()))) {
			RepaymentQueryVo vo = new RepaymentQueryVo();
			vo.setOrderNo(repaymentLog.getOrderNo());
			vo.setPayPlatNo(repaymentLog.getPayOrderNo());
			vo.setShareKey(repaymentLog.getUserId());
			RepaymentQueryResponseVo responseVo = PayCommonUtil.queryOrder(vo);

			if (StringUtil.equals(responseVo.getCode(), PayConstant.QUERY_PAY_SUCCESS)) {
				// 查找对应的还款计划
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("id", borrowRepay.getId());
				param.put("state", BorrowModel.STATE_DELAY_PAY);
				param.put("repayAccount", repaymentLog.getCardNo());
				param.put("serialNumber", repaymentLog.getOrderNo());
				param.put("amount", String.valueOf(repaymentLog.getAmount()));
				param.put("repayWay", BorrowRepayLogModel.REPAY_WAY_CHARGE);
				if(StringUtil.isNotBlank(Global.getValue("delay_days"))) {
					param.put("delayDays", Global.getValue("delay_days"));
				}
				Date repayTime = null;
				Map<String, Object> delayPayMap = confirmDelayPay(param);
				if (delayPayMap != null) {
					repayTime = (Date) delayPayMap.get("repayTime");
				}
				// 更新订单状态
				Map<String, Object> payLogParamMap = new HashMap<String, Object>();
				payLogParamMap.put("state",PayLogModel.STATE_PAYMENT_SUCCESS);
				payLogParamMap.put("updateTime", DateUtil.getNow());
				payLogParamMap.put("id", repaymentLog.getId());
				payLogService.updateSelective(payLogParamMap);

				//发送展期成功短信
				clSmsService.delayPlan(repaymentLog.getUserId(), repaymentLog.getBorrowId(),repayTime);
				result.put("code", "10");
				result.put("msg", "展期成功！");
				return result;
			}else if (StringUtil.equals(responseVo.getCode(),PayConstant.QUERY_PAY_PROCESSING)) {
				result.put("code", "11");
				result.put("msg", "展期处理中，请稍后再试!");
				return result;
			} else if (StringUtil.equals(responseVo.getCode(), PayConstant.QUERY_PAY_FAIL)) {
				// 更新订单状态
				Map<String, Object> payLogParamMap = new HashMap<String, Object>();
				payLogParamMap.put("state",PayLogModel.STATE_PAYMENT_FAILED);
				payLogParamMap.put("updateTime", DateUtil.getNow());
				payLogParamMap.put("id", repaymentLog.getId());
				payLogService.updateSelective(payLogParamMap);
			} else {
				result.put("code", "12");
				result.put("msg", "调用支付接口异常！");
				return result;
			}
		}
		return null;
	}


	/**
	 * 更新还款类型
	 * @return
	 */
	@Override
	public int updateBatchType(){
		List<Integer> listRepayId = borrowRepayMapper.listRepayId();

		List<Integer> listFirstRepayId = borrowRepayMapper.listFirstRepayId();

		List<Integer> repayIds = new ArrayList<>();

		for(Integer repayId: listRepayId){
			boolean flag = true;
			for(Integer firstRepayId:listFirstRepayId){
				if ( repayId != null && repayId.equals(firstRepayId)){
					flag = false;
					break;
				}
			}
			if (flag){
				repayIds.add(repayId);
			}
		}
		int count = 0;
		if (CollectionUtil.isNotEmpty(repayIds)){
			count = borrowRepayMapper.updateBatchTypeByRepayId(repayIds);
		}
		return count;
	}

	private ManualRepayOrder getManualRepayOrder(Long borrowId, UserBaseInfo userInfo, Long borrowRepayId, Long operatorUserId) {
		ManualRepayOrder manualRepayOrder = new ManualRepayOrder();
		manualRepayOrder.setCreateTime(DateUtil.getNow());
		manualRepayOrder.setReviewTime(DateUtil.getNow());
		manualRepayOrder.setBorrowUserId(userInfo.getUserId());
		manualRepayOrder.setBorrowId(borrowId);
		manualRepayOrder.setBorrowName(userInfo.getRealName());
		manualRepayOrder.setPhone(userInfo.getPhone());
		manualRepayOrder.setBorrowRepayId(borrowRepayId);
		manualRepayOrder.setUserId(operatorUserId);
		if(operatorUserId != null) {
			manualRepayOrder.setState(ManualRepayOrderModel.STATE_ORDER_ALLOT);
		} else {
			manualRepayOrder.setState(ManualRepayOrderModel.STATE_ORDER_NO_ALLOT);
		}
		return manualRepayOrder;
	}
}
