package com.infognc.apim.service;

import org.json.JSONArray;

public interface OracleService {
	
	// UCUBE - SELECT
	JSONArray selectUcube() throws Exception;
	
	// UCUBE DELETE BY ORDER_ID
	void deleteUcubeSdw(String orderid) throws Exception;
	
	// PCUBE
	JSONArray selectPcube() throws Exception;
	
}
