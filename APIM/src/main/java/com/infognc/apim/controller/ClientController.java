package com.infognc.apim.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.infognc.apim.service.ClientService;

@RestController
public class ClientController {
	private static final Logger logger = LoggerFactory.getLogger(ClientController.class);
	private final String ENDPOINT_PDS_RSLT = "/dspRslt";
	private final String ENDPOINT_PDS_005 = "/dspRsltHs";
	private final String ENDPOINT_PDS_006 = "/dspRsltPm";
	private final String ENDPOINT_PDS_007 = "/cmpnMstrRegistHs";
	private final String ENDPOINT_PDS_008 = "/cmpnMstrRegistPm";
	
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
		
		dsRstlInfoMap = clService.getCmpnRsltList(reqBodyList);
		
		return dsRstlInfoMap;
	}
	
	/*
	@PostMapping(value=ENDPOINT_PDS_006)
	public Map<String, Object> postDspRsltPm() throws Exception {	// IF-API-035101 (IF-CCS-006) - PDS발신결과수신_상담_무선
		logger.info("## IF-API-035101 (IF-CCS-006) - PDS발신결과수신_상담_무선 start");
		HashMap dsRstlInfoMap = new HashMap();
		
		return dsRstlInfoMap;
	}
	*/
	
	@PostMapping(value=ENDPOINT_PDS_007)
	public Map<String, Object> saveCampMstrHs() throws Exception {	// IF-API-035102 (IF-CCS-007) - PDS캠페인마스터수신_상담_유선
		logger.info("## IF-API-035102 (IF-CCS-007) - PDS캠페인마스터수신_상담_유선 start");
		HashMap dsHsRsltInfoMap = new HashMap();
		
		return dsHsRsltInfoMap;
	}
	
	
	
	@PostMapping(value=ENDPOINT_PDS_008)
	public Map<String, Object> saveCampMstrPm() throws Exception {	// IF-API-035102 (IF-CCS-008) - PDS캠페인마스터수신_상담_무선
		logger.info("## IF-API-035102 (IF-CCS-008) - PDS캠페인마스터수신_상담_무선 start");
		HashMap dsHsRsltInfoMap = new HashMap();
		
		return dsHsRsltInfoMap;
	}
	
	// IF-API-003704 (IF-CCS-100) - 매장상담원관리
	// IF-API-050604 (IF-CCS-101) - 매장상담원관리
	
	
}
