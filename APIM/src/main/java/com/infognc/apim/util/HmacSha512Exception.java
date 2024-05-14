package com.infognc.apim.util;

public class HmacSha512Exception extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2781791907541280400L;
	private static String errMsg = "HmacSha512Exception";
	
	public HmacSha512Exception() {
		super(errMsg );
	}
	
	public HmacSha512Exception(String errMsg) {
		super(errMsg );
	}
	

}
