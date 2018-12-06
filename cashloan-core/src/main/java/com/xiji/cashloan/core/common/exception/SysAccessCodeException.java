package com.xiji.cashloan.core.common.exception;

/**
 * 访问码异常
 * @author wnb
 * @date 2018/11/27
 * @version 1.0.0
 *
 *
 *
 * 未经授权不得进行修改、复制、出售及商业使用
 */
public class SysAccessCodeException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	
	protected static final int SERVICE_EXCEPTION__CODE = 400;
	
	protected int code;
	
	public SysAccessCodeException(int code, String businessMessage) {
		this(businessMessage);
	}

	public SysAccessCodeException(String businessMessage, Throwable cause, int code) {
		this(businessMessage, cause);
	}

	public SysAccessCodeException(String businessMessage, Throwable cause) {
		super(businessMessage, cause);
		this.code = SERVICE_EXCEPTION__CODE;
	}

	public SysAccessCodeException(String message) {
		super(message);
		this.code = SERVICE_EXCEPTION__CODE;

	}

	public SysAccessCodeException(Throwable t) {
		this(t.getMessage(), t);
	}
	
	
	
}
