package com.xiji.cashloan.core.common.exception;

/**
 * 图片验证码异常
 * @author wnb
 * @date 2018/11/27
 * @version 1.0.0
 *
 *
 *
 * 未经授权不得进行修改、复制、出售及商业使用
 */
public class ImgCodeException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	
	protected static final int IMGCODE_EXCEPTION__CODE = 400;
	
	protected int code;
	
	public ImgCodeException(int code, String businessMessage) {
		this(businessMessage);
	}

	public ImgCodeException(String businessMessage, Throwable cause, int code) {
		this(businessMessage, cause);
	}

	public ImgCodeException(String businessMessage, Throwable cause) {
		super(businessMessage, cause);
		this.code = IMGCODE_EXCEPTION__CODE;
	}

	public ImgCodeException(String message) {
		super(message);
		this.code = IMGCODE_EXCEPTION__CODE;

	}

	public ImgCodeException(Throwable t) {
		this(t.getMessage(), t);
	}
	
	
	
}
