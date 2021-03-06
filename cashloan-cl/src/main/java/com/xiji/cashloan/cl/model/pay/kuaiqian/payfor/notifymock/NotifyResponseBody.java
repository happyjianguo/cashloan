package com.xiji.cashloan.cl.model.pay.kuaiqian.payfor.notifymock;

import com.xiji.cashloan.cl.model.pay.kuaiqian.payfor.paymock.SealDataType;

import javax.xml.bind.annotation.*;

/**
 * 报文实体
 * @author zan.liang
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)  
@XmlRootElement  
@XmlType(name = "notifyResponseBody", propOrder = {"sealDataType","isReceived"})  
public class NotifyResponseBody {
	
	
	@XmlElement(name = "sealDataType")  
	private SealDataType sealDataType;
	@XmlElement(name = "isReceived")  
	private String isReceived;

	public SealDataType getSealDataType() {
		return sealDataType;
	}

	public void setSealDataType(SealDataType sealDataType) {
		this.sealDataType = sealDataType;
	}

	public String getIsReceived() {
		return isReceived;
	}

	public void setIsReceived(String isReceived) {
		this.isReceived = isReceived;
	}



	
}
