package com.infognc.apim.service;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.infognc.apim.gc.DataAction;

public interface ClientService {

	// 캠페인결과 송신 (IF-API-035101) 
	public HashMap<String, Object> sendCmpnRsltList(JSONArray reqBodyList) throws Exception; 
	
	// 캠페인마스터 송신 (IF-API-035102)
	public HashMap<String, Object> sendCmpnMaData(JSONObject reqBody) throws Exception;
	
	// ARS 만족도 결과 실시간 자료 전송 (IF-CCS-853)
//	public HashMap<String, Object> sendArsSatfRslt(JSONArray reqBodyList) throws Exception;
	public HashMap<String, Object> sendArsSatfRslt() throws Exception;

	// BS 고객만족도 결과 전송 (IF-CCSN-002)
//	public HashMap<String, Object> sendBsArsSatfRslt(JSONArray reqBodyList) throws Exception;
	public HashMap<String, Object> sendBsArsSatfRslt() throws Exception;
	
	// Genesys Cloud DataAction에서 APIM 호출
	public JSONObject callApimByDataAction(DataAction reqJson) throws Exception;
	
	// 컨테이너 내부 호출
	public String callEntContainer(String url) throws Exception;
	
}
