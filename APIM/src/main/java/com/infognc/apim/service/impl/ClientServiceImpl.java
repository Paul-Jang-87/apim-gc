package com.infognc.apim.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.infognc.apim.ApimMakeToken;
import com.infognc.apim.gc.HttpAction;
import com.infognc.apim.service.ClientService;
import com.infognc.apim.service.PostgreService;
import com.infognc.apim.utl.ApimCode;
import com.infognc.apim.utl.Configure;

public class ClientServiceImpl implements ClientService{
	private static final Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);
	private final PostgreService postgreService;
	private final HttpAction httpAction;
	
	public ClientServiceImpl(PostgreService postgreService, HttpAction httpAction) {
		this.postgreService = postgreService;
		this.httpAction = httpAction;
	}
	
	
	/*
	 * IF-CCS-005, IF-CCS-006 캠페인 결과 
	 */
	@Override
	public HashMap<String, Object> getCmpnRsltList(List<Map<String, Object>> reqBodyList) throws Exception {
		HashMap<String, Object> dsRstlInfoMap = new HashMap<String, Object>();
		
		HashMap<String, Object> reqBodyMap = new HashMap<String, Object>();
		
		List<Map<String, Object>> rsList = new ArrayList<Map<String, Object>>();
		HashMap<String, Object> cmpnRsltMap = new HashMap<String, Object>();
		
		try {
			// 캠페인 종료시점을 gc-api-client app에서 이벤트브릿지를 통해 전달 받음.
			// gc-api-client에서 UCRM, 콜봇 캠페인이 아닌경우 CAMPRT 데이터를 APIM으로 전달 (reqBody)
			if(reqBodyList.size() > 0) {
				String tkda = "";	// 토큰데이터
				String dirt = "";	// 발신결과코드
				
				for(int i=0; i>reqBodyList.size(); i++) {
					
					logger.info("## reqBodyList.get({}) :: {}", i, reqBodyList.get(i));

					if(reqBodyList.get(i).get("tkda") != null) tkda = reqBodyList.get(i).get("tkda").toString();
					if(reqBodyList.get(i).get("dirt") != null) dirt = reqBodyList.get(i).get("dirt").toString();
					
					String cnslTodoId 	= tkda.split(",")[1];		// 상담TODO ID
					String totoInstId 	= tkda.split(",")[3];		// TODO인스턴스 ID
					String hldrCustId 	= tkda.split(",")[4];		// 명의자 고객 ID
					String entrId 		= tkda.split(",")[5];		// 가입 ID
					String clbkRsltCd 	= dirt;						// 콜백결과코드
					
					cmpnRsltMap = new HashMap<String, Object>();
					cmpnRsltMap.put("cnslTodoId", cnslTodoId);
					cmpnRsltMap.put("todoInstId", totoInstId);
					cmpnRsltMap.put("hldrCustId", hldrCustId);
					cmpnRsltMap.put("entrId", entrId);
					cmpnRsltMap.put("clbkRsltCd", clbkRsltCd);
					rsList.add(i, cmpnRsltMap);
					
					logger.info("## cmpnRsltMap :: {}", cmpnRsltMap);
				}
				// apim request body 세팅
				reqBodyMap.put("pdsDspRslt", rsList);
				logger.info("## reqBodyMap :: {}", reqBodyMap);
				
				
				// =================   APIM 호출 ===============================
				String token	= Configure.get("api.auth.token");
				if(token.isEmpty() || token == null) {
					ApimMakeToken makeToken = new ApimMakeToken(httpAction);
					token = makeToken.getToken();
				}
				
				String clientId		= Configure.get("client.id");
				String clientSecret	= Configure.get("client.secret");
				
				String apiUrl 	= Configure.get("API.035101.URI");
//				String apiAcpt	= Configure.get("api.accept");
				String apiCont	= Configure.get("api.content.type");
				String apiAuth	= Configure.get("api.auth");
				String apiAppnm	= Configure.get("api.app.name");
				
				// make URI builder
				UriComponents uriBuilder = UriComponentsBuilder.fromUriString(apiUrl).build(true);
				// set http header
				HttpHeaders headers = new HttpHeaders();
				headers.set("Content-Type", apiCont);
				headers.set("X-Forwarded-Appname", apiAppnm);
				headers.set("X-IBM-Client-id", clientId);
				headers.set("X-IBM-Client-Secret", clientSecret);
				headers.set("Authorization", apiAuth + " " + token);
				
				HttpEntity<String> entity = new HttpEntity<String>(reqBodyMap.toString(), headers);
				
				String response = httpAction.restTemplateService(uriBuilder, entity, "POST");
//				resBodyMap = httpAction.restTemplateService(uriBuilder, entity, "POST");
				logger.info("## API-035101 Get Data !! : {}", response);
				
				JSONObject resObj = new JSONObject(response);
				if(resObj == null || !resObj.get("rsltCd").equals("Y")) {
					logger.info("## API-035101 DATA NOT FOUND >> " + ApimCode.RESULT_FAIL_MSG);
					dsRstlInfoMap.put("rsltCd", ApimCode.RESULT_FAIL_MSG);
					return dsRstlInfoMap;
				} else {
					// 성공
					dsRstlInfoMap.put("rsltCd", resObj.get("rsltCd"));
					logger.info("## API-035101 데이터 전송 성공 ");
				}
				
			} else {
				dsRstlInfoMap.put("rsltCd", ApimCode.RESULT_SUCCESS_N);
				dsRstlInfoMap.put("rsltMsg", ApimCode.RESULT_FAIL_MSG);
				logger.info("## API-035101 NO DATA => " + ApimCode.RESULT_FAIL_MSG);
			}
			
			
		}catch(Exception e) {
			dsRstlInfoMap.put("rsltCd", ApimCode.RESULT_SUCCESS_N);
		}
		
		return dsRstlInfoMap;
	}
	
}
