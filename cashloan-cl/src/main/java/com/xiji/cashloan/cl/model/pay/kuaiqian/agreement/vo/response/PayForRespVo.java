package com.xiji.cashloan.cl.model.pay.kuaiqian.agreement.vo.response;

import com.xiji.cashloan.cl.model.pay.kuaiqian.constant.KuaiqianPayConstant;

import tool.util.StringUtil;

public class PayForRespVo {

    private String version;		//版本号(1.0)

    private String errorCode;//B.MGW.0001 错误代码

    private String errorMessage;//错误消息

    private String txnType;//交易类型 编码

    private String refNumber;//系统参考 号

    private String interactiveStatus;//消息状态

    private String amount;//金额

    private String transTime;//交易传输 时间

    private String merchantId;//商户号

    private String terminalId; //终端号

    private String entryTime;//商户端交 易时间

    private String customerId;//客户号

    private String externalRefNumber;//外部跟踪 编号

    private String storablePan;//缩略卡号

    private String responseCode;//应答码

    private String responseTextMessage;//应答码文 本消息

    private String bindType;//接入方式

    private String settleMerchantId;//结算商户 号

    private String payToken;//支付协议号

    private String cardOrg;//卡组织编 号

    private String issuer;//发卡行银 行名称

    private String authorizationCode;//授权码

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getRefNumber() {
        return refNumber;
    }

    public void setRefNumber(String refNumber) {
        this.refNumber = refNumber;
    }

    public String getInteractiveStatus() {
        return interactiveStatus;
    }

    public void setInteractiveStatus(String interactiveStatus) {
        this.interactiveStatus = interactiveStatus;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransTime() {
        return transTime;
    }

    public void setTransTime(String transTime) {
        this.transTime = transTime;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getExternalRefNumber() {
        return externalRefNumber;
    }

    public void setExternalRefNumber(String externalRefNumber) {
        this.externalRefNumber = externalRefNumber;
    }

    public String getStorablePan() {
        return storablePan;
    }

    public void setStorablePan(String storablePan) {
        this.storablePan = storablePan;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseTextMessage() {
        return responseTextMessage;
    }

    public void setResponseTextMessage(String responseTextMessage) {
        this.responseTextMessage = responseTextMessage;
    }

    public String getBindType() {
        return bindType;
    }

    public void setBindType(String bindType) {
        this.bindType = bindType;
    }

    public String getSettleMerchantId() {
        return settleMerchantId;
    }

    public void setSettleMerchantId(String settleMerchantId) {
        this.settleMerchantId = settleMerchantId;
    }

    public String getPayToken() {
        return payToken;
    }

    public void setPayToken(String payToken) {
        this.payToken = payToken;
    }

    public String getCardOrg() {
        return cardOrg;
    }

    public void setCardOrg(String cardOrg) {
        this.cardOrg = cardOrg;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public boolean checkReturn() {
        return StringUtil.equals(responseCode, KuaiqianPayConstant.RESPONSE_SUCCESS_CODE);
    }
}
