package com.infognc.apim.util;

public class HmacTimeoutException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3077615476643902081L;
	private static String errMsg = "Request not vaild (timeout)";
	
	public HmacTimeoutException() {
		super(errMsg );
	}

	public HmacTimeoutException(String errMsg) {
		super(errMsg );
	}
}
