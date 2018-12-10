package com.xiji.cashloan.cl.model.pay.fuiou.agreement;

import com.xiji.cashloan.core.common.context.Global;
import org.apache.commons.lang3.StringUtils;

/**
 * @Auther: king
 * @Date: 2018/11/27 15:44
 * @Description:
 */
public class ProtocolResp {
    private String errorCode;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public boolean error() {
        return StringUtils.startsWith(errorCode, "300");
    }

    public String getkey() {
        return Global.getValue("fuiou_protocol_mchntcd_key");
    }
}
