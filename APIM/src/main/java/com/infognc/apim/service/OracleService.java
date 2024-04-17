package com.infognc.apim.service;

import org.json.JSONArray;

public interface OracleService {
	
	// UCUBE
	JSONArray selectUcube() throws Exception;
	
	// UCUBE_SDW DELETE
//	void deleteUcubeSdw() throws Exception;
	
	// PCUBE
	JSONArray selectPcube() throws Exception;
}
