package com.xiji.cashloan.cl.model.pay.common.vo.response;

/**
 * @Auther: king
 * @Date: 2019/1/26 15:31
 * @Description:
 */
public class UnbindCardResponseVo {
    /**
     * 状态
     */
    private String status;
    private String message;
    private String protocolNo;//协议号

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
