package com.infognc.apim.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.infognc.apim.service.ClientService;

@RestController
public class ClientController {
	private static final Logger logger = LoggerFactory.getLogger(ClientController.class);
	private final String ENDPOINT_PDS_RSLT = "/dsprslt";
	private final String ENDPOINT_PDS_RGST = "/cmpnmstrregist";
	private final String GC_API_ACTION = "/gcapi-action";
	
	private final ClientService clService;
	
	public ClientController(ClientService clService) {
		this.clService = clService;
	}
	
	/*
	 * IF-API-035101 (IF-CCS-005) - PDS발신결과수신_상담_유선
	 * IF-API-035101 (IF-CCS-006) - PDS발신결과수신_상담_무선
	 * 10분주기?
	 */
	@PostMapping(value=ENDPOINT_PDS_RSLT)
	public Map<String, Object> postDspRsltHs(@RequestBody List<Map<String, Object>> reqBodyList) throws Exception {	
		logger.info("## IF-API-035101 - PDS발신결과수신 start");
		HashMap<String, Object> dsRstlInfoMap = new HashMap<String, Object>();
		
		dsRstlInfoMap = clService.sendCmpnRsltList(reqBodyList);
		
		return dsRstlInfoMap;
	}
	
	
	@PostMapping(value=ENDPOINT_PDS_RGST)
	public Map<String, Object> saveCampMstrHs(@RequestBody List<Map<String, Object>> reqBodyList) throws Exception {	// IF-API-035102 (IF-CCS-007) - PDS캠페인마스터수신_상담_유선
		logger.info("## IF-API-035102 - PDS캠페인마스터수신_상담 start");
		HashMap<String, Object> dsHsRsltInfoMap = new HashMap<String, Object>();
		
		dsHsRsltInfoMap = clService.sendCmpnMaData(reqBodyList);
		
		return dsHsRsltInfoMap;
	}
	
	
	/**
	 * G.C DataAction에서 APIM 호출을 위한 EndPoint
	 * 
	 * @param reqBodyList
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value=GC_API_ACTION)
	public JSONObject getApimDataToDataAction(@RequestBody JSONObject reqBodyJson) throws Exception {	
		logger.info("## GenesysCloud DataAction 호출 ");
		
		return clService.callApimByDataAction(reqBodyJson);
	}
	
	
}
