package com.infognc.apim.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientController {
	private static final Logger logger = LoggerFactory.getLogger(ClientController.class);
	
	
	
	@PostMapping(value="/dspRsltHs")
	public Map<String, Object> postDspRsltHs() throws Exception {	// IF-API-035101 (IF-CCS-005) - PDS발신결과수신_상담_유선
		logger.info("## IF-API-035101 (IF-CCS-005) - PDS발신결과수신_상담_유선 start");
		HashMap dsRstlInfoMap = new HashMap();
		
		return dsRstlInfoMap;
	}
	
	
	@PostMapping(value="/dspRsltPm")
	public Map<String, Object> postDspRsltPm() throws Exception {	// IF-API-035101 (IF-CCS-006) - PDS발신결과수신_상담_무선
		logger.info("## IF-API-035101 (IF-CCS-006) - PDS발신결과수신_상담_무선 start");
		HashMap dsRstlInfoMap = new HashMap();
		
		return dsRstlInfoMap;
	}
	
	
	@PostMapping(value="/cmpnMstrRegistHs")
	public Map<String, Object> saveCampMstrHs() throws Exception {	// IF-API-035102 (IF-CCS-007) - PDS캠페인마스터수신_상담_유선
		logger.info("## IF-API-035102 (IF-CCS-007) - PDS캠페인마스터수신_상담_유선 start");
		HashMap dsHsRsltInfoMap = new HashMap();
		
		return dsHsRsltInfoMap;
	}
	
	
	
	@PostMapping(value="/cmpnMstrRegistPm")
	public Map<String, Object> saveCampMstrPm() throws Exception {	// IF-API-035102 (IF-CCS-008) - PDS캠페인마스터수신_상담_무선
		logger.info("## IF-API-035102 (IF-CCS-008) - PDS캠페인마스터수신_상담_무선 start");
		HashMap dsHsRsltInfoMap = new HashMap();
		
		return dsHsRsltInfoMap;
	}
	
	
	
	// IF-API-003704 (IF-CCS-100) - 매장상담원관리
	// IF-API-050604 (IF-CCS-101) - 매장상담원관리
	// IF-API-033701 (IF-CCS-853) - ARS 만족도 결과 실시간 자료전송
	// IF-API-033701 (IF-CCSN-002)- BS 고객만족도 결과 수신

	
	
	
}
