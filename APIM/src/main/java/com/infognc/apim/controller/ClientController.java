package com.infognc.apim.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.infognc.apim.gc.DataAction;
import com.infognc.apim.service.ClientService;

@RestController
public class ClientController {
	private static final Logger logger = LoggerFactory.getLogger(ClientController.class);
	private final String ENDPOINT_PDS_RSLT = "/dspRslt";
	private final String ENDPOINT_PDS_RGST = "/cmpnMstrRegist";
	private final String ENDPOINT_ARSSATF_RSLT = "/arsSatfRsltRtmDat";
	private final String ENDPOINT_BSARSSATF_RSLT = "/bsArsSatfRsltRtmDat";
	private final String GC_API_ACTION = "/gcapi-action";	
	
	private final ClientService clService;
	
	public ClientController(ClientService clService) {
		this.clService = clService;
	}
	
	/**
	 * 
	 * IF-API-035101 (IF-CCS-005) - PDS발신결과수신_상담_유선
	 * IF-API-035101 (IF-CCS-006) - PDS발신결과수신_상담_무선
	 * 
	 * @param reqBodyList
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value=ENDPOINT_PDS_RSLT)
	public Map<String, Object> postDspRsltHs(@RequestBody String reqBodyList) throws Exception {	
		logger.info("## IF-API-035101 - PDS발신결과수신 start");
		HashMap<String, Object> dsRstlInfoMap = new HashMap<String, Object>();
		
		logger.info("## Request Body :: " + reqBodyList);
		
		dsRstlInfoMap = clService.sendCmpnRsltList(new JSONArray(reqBodyList));
		
		return dsRstlInfoMap;
	}
	
	/**
	 * 
	 * IF-API-035102 (IF-CCS-007) - 캠페인마스터 송신_유선
	 * IF-API-035102 (IF-CCS-008) - 캠페인마스터 송신_무선
	 * 
	 * @param reqBody
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value=ENDPOINT_PDS_RGST)
	public Map<String, Object> saveCampMstrHs(@RequestBody String reqBody) throws Exception {	// IF-API-035102 (IF-CCS-007) - PDS캠페인마스터수신_상담_유선
		logger.info("## IF-API-035102 - PDS캠페인마스터수신_상담 start");
		HashMap<String, Object> dsHsRsltInfoMap = new HashMap<String, Object>();
		
		logger.info("## Request Body :: " + reqBody);
		
		dsHsRsltInfoMap = clService.sendCmpnMaData(new JSONObject(reqBody));
		
		return dsHsRsltInfoMap;
	}
	
	
	
	/**
	 * 배치 1분
	 * 
	 * IF-API-033701 (IF-CCS-835) - ARS 만족도결과 실시간 자료전송
	 * 
	 * @param reqBodyList
	 * @return
	 * @throws Exception
	 */
	@Scheduled(fixedDelay=60*1000)
	@PostMapping(value=ENDPOINT_ARSSATF_RSLT)
	public Map<String, Object> arsSatfRslt() throws Exception {
		logger.info("## IF-API-033701 - ARS 만족도결과 실시간 자료전송(IF-CCS-853) start");
		HashMap<String, Object> dsHsRsltInfoMap = new HashMap<String, Object>();
		
		dsHsRsltInfoMap = clService.sendArsSatfRslt();
		
		return dsHsRsltInfoMap;
	}
	
	
	
	/**
	 * 일배치 16:30 
	 * 서버시간이 UTC 기준이라 +09:00 필요
	 * 
	 * IF-API-033701 (IF-CCSN-002) -  BS 고객만족도결과 결과수신
	 * 
	 * @param reqBodyList
	 * @return
	 * @throws Exception
	 */
	@Scheduled(cron="0 30 07 * * *")
	@PostMapping(value=ENDPOINT_BSARSSATF_RSLT)
	public Map<String, Object> bsArsSatfRslt() throws Exception {
		logger.info("## IF-API-033701 - BS 고객만족도결과 결과수신 (IF-CCSN-002) start");
		HashMap<String, Object> dsHsRsltInfoMap = new HashMap<String, Object>();
		
		dsHsRsltInfoMap = clService.sendBsArsSatfRslt();
		
		return dsHsRsltInfoMap;
	}
	
	
	/**
	 * G.C DataAction에서 APIM 호출을 위한 EndPoint
	 * 
	 * DEV : https://dev-gckafka.lguplus.co.kr/gcapi-action
	 * PRD : https://gckafka.lguplus.co.kr/gcapi-action
	 * 
	 * @param reqBodyList
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value=GC_API_ACTION)
	public String getApimDataToDataAction(
							@RequestBody DataAction reqBody) throws Exception {	
		logger.info("## GenesysCloud DataAction 호출 ");
		
		String result = clService.callApimByDataAction(reqBody).toString();
		
		return result;
//		return clService.callApimByDataAction(reqBody);
	}
	
	/**
	 * gc-api-client 컨테이너에서 따로 스케쥴러 돌아가고 있어서 현재는 사용 X (참고용으로 둔다)
	 * log_back.xml *.log.gz 파일 tar.gz로 변환 스크립트 스케쥴러
	 * 보관주기 설정
	 */
//  @Scheduled(cron = "0 0 0 * * ?")
//	@Scheduled(fixedDelay=60*1000)
    @GetMapping("/logback")
    public String compressLogs() throws IOException {
    	logger.info("########### log_back.sh 실행 ");
    	String result = "";
        ProcessBuilder pb = new ProcessBuilder("/logs/log_back.sh");
        pb.inheritIO();
        Process process = pb.start();
        try {
            process.waitFor();
            result = ">>>> log_back.sh 실행 성공";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            result = ">>>> log_back.sh 실행 실패";
            logger.error(">> [log_back.sh] Log compression script interrupted :: {} ", e.getMessage());
            throw new IOException("Log compression script interrupted", e);
        }
        
        return result;
        
    }
	
	/**
	 * 
	 * EKS Health Check 용도
	 * 반드시 필요하다고 하니 일단 두자
	 * 
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/gethc")
	public String getHealthCheck() throws Exception {
		
		return "TEST RESPONSE";
	}

	/**
	 * 부하테스트 health check 용도
	 * 
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/apim-gw")
	public String getHealthCheckAPIM() throws Exception {
		return "TEST RESPONSE";
	}
	
	/**
	 * 매일 00시(UTC) date 로그 생성
	 * 
	 * @return
	 * @throws Exception
	 */
	@Scheduled(cron = "0 0 0 * * ?")
	@GetMapping("/today")
	public void getHealthCheckKafka() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        
        logger.info("### Current date and time: " + formattedDateTime);
        logger.error("### Current date and time: " + formattedDateTime);
        
	}
	
	
}
