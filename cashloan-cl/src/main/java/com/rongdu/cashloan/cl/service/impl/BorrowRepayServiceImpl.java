package com.rongdu.cashloan.cl.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fuiou.util.MD5;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.rongdu.cashloan.cl.domain.BankCard;
import com.rongdu.cashloan.cl.domain.BorrowProgress;
import com.rongdu.cashloan.cl.domain.BorrowRepay;
import com.rongdu.cashloan.cl.domain.BorrowRepayLog;
import com.rongdu.cashloan.cl.domain.PayLog;
import com.rongdu.cashloan.cl.domain.PayReqLog;
import com.rongdu.cashloan.cl.domain.UrgeRepayOrder;
import com.rongdu.cashloan.cl.domain.UrgeRepayOrderLog;
import com.rongdu.cashloan.cl.domain.UserInvite;
import com.rongdu.cashloan.cl.mapper.BankCardMapper;
import com.rongdu.cashloan.cl.mapper.BorrowProgressMapper;
import com.rongdu.cashloan.cl.mapper.BorrowRepayLogMapper;
import com.rongdu.cashloan.cl.mapper.BorrowRepayMapper;
import com.rongdu.cashloan.cl.mapper.ClBorrowMapper;
import com.rongdu.cashloan.cl.mapper.PayLogMapper;
import com.rongdu.cashloan.cl.mapper.PayReqLogMapper;
import com.rongdu.cashloan.cl.mapper.ProfitAgentMapper;
import com.rongdu.cashloan.cl.mapper.ProfitLogMapper;
import com.rongdu.cashloan.cl.mapper.UserInviteMapper;
import com.rongdu.cashloan.cl.model.AlipayModel;
import com.rongdu.cashloan.cl.model.BorrowRepayLogModel;
import com.rongdu.cashloan.cl.model.BorrowRepayModel;
import com.rongdu.cashloan.cl.model.ManageBRepayModel;
import com.rongdu.cashloan.cl.model.ManageBorrowModel;
import com.rongdu.cashloan.cl.model.PayLogModel;
import com.rongdu.cashloan.cl.model.RepayExcelModel;
import com.rongdu.cashloan.cl.model.UrgeRepayOrderModel;
import com.rongdu.cashloan.cl.model.pay.fuiou.agreement.OrderQryByMSsn;
import com.rongdu.cashloan.cl.model.pay.fuiou.agreement.OrderQryResp;
import com.rongdu.cashloan.cl.model.pay.fuiou.util.FuiouAgreementPayHelper;
import com.rongdu.cashloan.cl.model.pay.lianlian.CertifiedPayModel;
import com.rongdu.cashloan.cl.model.pay.lianlian.QueryRepaymentModel;
import com.rongdu.cashloan.cl.model.pay.lianlian.constant.LianLianConstant;
import com.rongdu.cashloan.cl.model.pay.lianlian.util.LianLianHelper;
import com.rongdu.cashloan.cl.service.BankCardService;
import com.rongdu.cashloan.cl.service.BorrowRepayService;
import com.rongdu.cashloan.cl.service.ClBorrowService;
import com.rongdu.cashloan.cl.service.ClSmsService;
import com.rongdu.cashloan.cl.service.PayLogService;
import com.rongdu.cashloan.cl.service.ProfitLogService;
import com.rongdu.cashloan.cl.service.UrgeRepayOrderLogService;
import com.rongdu.cashloan.cl.service.UrgeRepayOrderService;
import com.rongdu.cashloan.core.common.context.Constant;
import com.rongdu.cashloan.core.common.context.Global;
import com.rongdu.cashloan.core.common.exception.BussinessException;
import com.rongdu.cashloan.core.common.mapper.BaseMapper;
import com.rongdu.cashloan.core.common.service.impl.BaseServiceImpl;
import com.rongdu.cashloan.core.common.util.DateUtil;
import com.rongdu.cashloan.core.common.util.OrderNoUtil;
import com.rongdu.cashloan.core.common.util.excel.ReadExcelUtils;
import com.rongdu.cashloan.core.domain.Borrow;
import com.rongdu.cashloan.core.domain.User;
import com.rongdu.cashloan.core.domain.UserBaseInfo;
import com.rongdu.cashloan.core.mapper.UserBaseInfoMapper;
import com.rongdu.cashloan.core.mapper.UserMapper;
import com.rongdu.cashloan.core.model.BorrowModel;
import com.rongdu.creditrank.cr.domain.Credit;
import com.rongdu.creditrank.cr.domain.CreditLog;
import com.rongdu.creditrank.cr.mapper.CreditLogMapper;
import com.rongdu.creditrank.cr.mapper.CreditMapper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tool.util.BigDecimalUtil;
import tool.util.NumberUtil;
import tool.util.StringUtil;

/**
 * 还款计划ServiceImpl
 * 
 * @author lyang
 * @version 1.0.0
 * @date 2017-02-14 13:42:32 
 * Copyright 杭州融都科技股份有限公司 arc All Rights Reserved
 * 官方网站：www.erongdu.com
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
    private BankCardMapper bankCardMapper;
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
		String beheadFee = Global.getValue("behead_fee");// 是否启用砍头息
		//放款成功,保存还款计划
		BorrowRepay br = new BorrowRepay();
		if ("10".equals(beheadFee)) {//启用
			br.setAmount(borrow.getAmount());
		}else {
			br.setAmount(borrow.getAmount() + borrow.getFee());
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
		int result = borrowRepayMapper.save(br);
	
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
	 * @param borrow
	 * @param date
	 */
	private void authApply(final BorrowRepay borrowRepay) {
		// 查询用户信息及银行卡信息 用于授权
//		new Thread() {
//			@Override
//			public void run() {
//				Map<String, Object> paramMap = new HashMap<String, Object>();
//				paramMap.put("userId", borrowRepay.getUserId());
//				BankCard bankCard = bankCardMapper.findSelective(paramMap);
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
			
			String isImproveCredit = Global.getValue("is_improve_credit");//提额开关 -- 10开，20关
			
			if(!BorrowModel.STATE_DELAY.equals(state) && "10".equals(isImproveCredit)){//未逾期且提额开关为10 ---提额
				String oneRepayCredit = Global.getValue("one_repay_credit");//还款成功题额  --固定额度
				String improveCreditLimit = Global.getValue("imporove_credit_limit");//提额上限
				
				
				Credit c = creditMapper.findByConsumerNo(StringUtil.isNull(br.getUserId()));
				Map<String, Object> map = new HashMap<String, Object>();// 封装提额参数
				map.put("consumerNo", br.getUserId());
				map.put("total", oneRepayCredit);// 总额度
				map.put("unuse", oneRepayCredit);// 未使用额度
				
				int x = 1;
				
				if ((c.getCount()+1) * Double.parseDouble(oneRepayCredit) <= Double.parseDouble(improveCreditLimit)) {// 提额上线为500
					x = creditMapper.updateByUserId(map);
					if(x>=1){//添加额度修改日志
						CreditLog log = new CreditLog();
						log.setConsumerNo(c.getConsumerNo());
						log.setCreditType(c.getCreditType());
						log.setModifyTotal(Double.parseDouble(oneRepayCredit));
						log.setModifyUser("system");
						log.setNow(c.getTotal()+Double.parseDouble(oneRepayCredit));
						log.setPre(c.getTotal());
						log.setRemark("还款成功，自动提额"+oneRepayCredit+"（元）");
						log.setType("70");// 70 --自动提额
						log.setModifyTime(new Date());
						creditLogMapper.save(log);
						
					}
				}
				if (x < 1) {
					logger.error("自动提额失败");
				}
			}
			
		    return result;
	}

	/**
	 * 更新借款表和借款进度状态
	 * 
	 * @param borrowId
	 * @param userId
	 * @param repayTime
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
			if (!repay_time.after(repayPlanTime)) {
				br.setPenaltyDay(String.valueOf(0));
				br.setPenaltyAmout(Double.valueOf(0));
				paramMap.put("penaltyDay","0");
				paramMap.put("penaltyAmout", 0.00);
			}
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
		    if (!repay_time.after(repayPlanTime)) {
				log.setPenaltyAmout(0.00);
				log.setPenaltyDay("0");
			} else {
				// 金额减免时 罚金可页面填写
				String penaltyAmout = StringUtil.isNull(param.get("penaltyAmout"));
				if (StringUtil.isNotBlank(penaltyAmout)) {
					log.setPenaltyAmout(NumberUtil.getDouble(penaltyAmout));
				} else {
					log.setPenaltyAmout(br.getPenaltyAmout());
				}
			}

			log.setSerialNumber((String) param.get("serialNumber"));
			log.setRepayAccount((String) param.get("repayAccount"));
			log.setRepayWay((String) param.get("repayWay"));
			log.setCreateTime(DateUtil.getNow());
			return borrowRepayLogMapper.save(log);
		}
		return i;
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
				!BorrowModel.STATE_BAD.equals(state)) {
			throw new BussinessException("借款状态有误");
		}
		
		// 根据借款标识(还款标识) 查询应还款金额
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("borrowId", borrowId);
		paramMap.put("userId", userId);
		BorrowRepay borrowRepay = findSelective(paramMap);
		
		// 还款金额
		double principal = borrowRepay.getAmount();
		double penaltyAmout = borrowRepay.getPenaltyAmout();
		double amount = BigDecimalUtil.add(principal ,penaltyAmout); 

		// 根据类型 走不同支付渠道，获取SDK所需参数
		String orderNo = OrderNoUtil.getSerialNumber();
		// APP应用 + 小额贷款
		
		BankCard bankCard = bankCardService.getBankCardByUserId(borrow.getUserId());
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
	 * @param type
	 * @param amount
	 * @param ip
	 * @param bodyL
	 * @param orderNo
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
		BorrowRepay borrowRepay = borrowRepayMapper.findSelective(repayMap);
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
			OrderQryByMSsn beanreq = new OrderQryByMSsn();
			beanreq.setMchntOrderId(log.getOrderNo());
			FuiouAgreementPayHelper payHelper = new FuiouAgreementPayHelper();
			OrderQryResp resp = payHelper.checkResult(beanreq);
			String key = Global.getValue("protocol_mchntcd_key");
			if (resp.checkReturn() && resp.checkSign(key)) {
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
}
