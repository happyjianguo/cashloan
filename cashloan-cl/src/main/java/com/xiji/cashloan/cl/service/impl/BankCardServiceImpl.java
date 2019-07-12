package com.xiji.cashloan.cl.service.impl;

import com.xiji.cashloan.cl.domain.BankCard;
import com.xiji.cashloan.cl.manage.BankCardManage;
import com.xiji.cashloan.cl.model.pay.common.PayCommonUtil;
import com.xiji.cashloan.cl.model.pay.common.vo.request.UnbindCardVo;
import com.xiji.cashloan.cl.model.pay.common.vo.response.UnbindCardResponseVo;
import com.xiji.cashloan.cl.service.BankCardService;
import com.xiji.cashloan.core.common.mapper.BaseMapper;
import com.xiji.cashloan.core.common.service.impl.BaseServiceImpl;
import com.xiji.cashloan.core.domain.User;
import com.xiji.cashloan.core.mapper.UserMapper;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tool.util.StringUtil;


/**
 * 银行卡ServiceImpl
 *
 *
 * @author wnb
 * @date 2018/11/30
 * @version 1.0.0
 *
 *
 *
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
 
@Service("bankCardService")
public class BankCardServiceImpl extends BaseServiceImpl<BankCard, Long> implements BankCardService {
	
	private static final Logger logger = LoggerFactory.getLogger(BankCardServiceImpl.class);
   
    @Resource
    private BankCardManage bankCardManage;
    
    @Resource
    private UserMapper userMapper;
    
	@Override
	public BaseMapper<BankCard, Long> getMapper() {
		return bankCardManage;
	}

	@Override
	public boolean save(BankCard bankCard) {
		int result = bankCardManage.save(bankCard);
		if (result > 0) {
			return true;
		}
		return false;
	}

	@Override
	public BankCard getBankCardByUserId(Long userId) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("userId", userId);
			return bankCardManage.findSelective(paramMap);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}

	@Override
	public BankCard findSelective(Map<String, Object> paramMap) {
		return bankCardManage.findSelective(paramMap);
	}

	@Override
	public int cancelAuthSign(BankCard card) {
		int result;
		// 查询用户并解约
		User user  = userMapper.findByPrimary(card.getUserId());
		UnbindCardResponseVo responseVo = null;
		if(null != user && StringUtil.isNotBlank(card.getAgreeNo())){
			UnbindCardVo vo = new UnbindCardVo();
			vo.setProtocolNo(card.getAgreeNo());
			vo.setUserId(user.getUuid());
			vo.setShareKey(card.getUserId());
			vo.setCardNo(card.getCardNo());
			responseVo = PayCommonUtil.unbind(vo);
		}
		// 解约成功 修改银行卡
		if(null != responseVo &&  PayCommonUtil.success(responseVo.getStatus())){
			result = bankCardManage.update(card);
		}else{
			result = bankCardManage.update(card);
		}
		return result;
	}

	@Override
	public boolean updateSelective(Map<String, Object> paramMap) {
		int result = bankCardManage.updateSelective(paramMap);
		if (result > 0L) {
			return true;
		}
		return false;
	}

	
}
