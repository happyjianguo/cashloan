package com.xiji.cashloan.cl.model.pay.common.vo.request;

/**
 * @Auther: king
 * @Date: 2019/1/26 15:31
 * @Description:
 */
public class UnbindCardVo  extends PayReq {
    private String userId;
    private String protocolNo;//协议号

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProtocolNo() {
        return protocolNo;
    }

    public void setProtocolNo(String protocolNo) {
        this.protocolNo = protocolNo;
    }
}
