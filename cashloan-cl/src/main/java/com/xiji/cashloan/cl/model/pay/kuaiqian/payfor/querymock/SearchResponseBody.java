package com.xiji.cashloan.cl.model.pay.kuaiqian.payfor.querymock;

import com.xiji.cashloan.cl.model.pay.kuaiqian.payfor.paymock.SealDataType;

import javax.xml.bind.annotation.*;

/**
 * 报文实体
 * @author zan.liang
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)  
@XmlRootElement  
@XmlType(name = "searchResponseBody", propOrder = {"sealDataType","errorCode","errorMsg"})  
public class SearchResponseBody {
	
	@XmlElement(name = "errorCode")  
	private String errorCode;
	
	@XmlElement(name = "errorMsg")  
	private String errorMsg;
	
	@XmlElement(name = "sealDataType")  
	private SealDataType sealDataType;

	public SealDataType getSealDataType() {
		return sealDataType;
	}

	public void setSealDataType(SealDataType sealDataType) {
		this.sealDataType = sealDataType;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}


	
}
