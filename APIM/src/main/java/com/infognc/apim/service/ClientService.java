package com.infognc.apim.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

public interface ClientService {

	// 캠페인결과 송신 (IF-API-035101) 
	public HashMap<String, Object> sendCmpnRsltList(List<Map<String, Object>> reqBodyList) throws Exception; 
	
	// 캠페인마스터 송신 (IF-API-035102)
	public HashMap<String, Object> sendCmpnMaData(List<Map<String, Object>> reqBodyList) throws Exception;
	
	// Genesys Cloud DataAction에서 APIM 호출
	public JSONObject callApimByDataAction(JSONObject reqJson) throws Exception;
}
