package com.infognc.apim.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.infognc.apim.ApimMakeToken;
import com.infognc.apim.gc.DataAction;
import com.infognc.apim.gc.HttpAction;
import com.infognc.apim.service.ClientService;
import com.infognc.apim.service.OracleService;
import com.infognc.apim.utl.ApiUtil;
import com.infognc.apim.utl.ApimCode;
import com.infognc.apim.utl.Configure;

@Service
public class ClientServiceImpl implements ClientService{
	private static final Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);
	private final HttpAction httpAction;
	private final OracleService oracleService;
	
	public ClientServiceImpl(HttpAction httpAction, OracleService oracleService) {
		this.httpAction = httpAction;
		this.oracleService = oracleService;
	}
	
	
	/**
	 *  IF-CCS-005, IF-CCS-006 캠페인 결과 
	 *  gc-api-client에서 캠페인 결과(TKDA, DIRT) 데이터 수신
	 */
	@Override
	public HashMap<String, Object> sendCmpnRsltList(JSONArray reqBodyList) throws Exception {
		HashMap<String, Object> dsRstlInfoMap = new HashMap<String, Object>();
		
//		HashMap<String, Object> reqBodyMap = new HashMap<String, Object>();
		JSONObject reqBodyJson = new JSONObject();;
		JSONObject cmpnRsltJson = null;
		
		List<JSONObject> rsList = new ArrayList<JSONObject>();
		
		logger.info("## reqBodyList length :: {}", reqBodyList.length());
		
		try {
			// 캠페인 종료시점을 gc-api-client app에서 이벤트브릿지를 통해 전달 받음.
			// gc-api-client에서 UCRM, 콜봇 캠페인이 아닌경우 CAMPRT 데이터를 APIM으로 전달 (reqBody)
			if(reqBodyList.length() > 0) {
				String tkda = "";	// 토큰데이터
				String dirt = "";	// 발신결과코드
				
				for(int i=0; i<reqBodyList.length(); i++) {
					
					logger.info("## reqBodyList.get({}) :: {}", i, reqBodyList.getJSONObject(i));

//					if(reqBodyList.get(i).get("tkda") != null) tkda = reqBodyList.get(i).get("tkda").toString();
//					if(reqBodyList.get(i).get("dirt") != null) dirt = reqBodyList.get(i).get("dirt").toString();
					tkda = reqBodyList.getJSONObject(i).optString("tkda");
					dirt = reqBodyList.getJSONObject(i).optString("dirt");
					
					logger.info("## tkda_{} : {}", i, tkda);
					logger.info("## dirt_{} : {}", i, dirt);
					
					String cnslTodoId 	= tkda.split(",")[1];		// 상담TODO ID
					String totoInstId 	= tkda.split(",")[3];		// TODO인스턴스 ID
					String hldrCustId 	= tkda.split(",")[4];		// 명의자 고객 ID
					String entrId 		= tkda.split(",")[5];		// 가입 ID
					String clbkRsltCd 	= dirt;						// 콜백결과코드
					/*
					cmpnRsltMap = new HashMap<String, Object>();
					cmpnRsltMap.put("cnslTodoId", cnslTodoId);
					cmpnRsltMap.put("todoInstId", totoInstId);
					cmpnRsltMap.put("hldrCustId", hldrCustId);
					cmpnRsltMap.put("entrId", entrId);
					cmpnRsltMap.put("clbkRsltCd", clbkRsltCd);
					*/
					cmpnRsltJson = new JSONObject();
					cmpnRsltJson.put("cnslTodoId", cnslTodoId);
					cmpnRsltJson.put("todoInstId", totoInstId);
					cmpnRsltJson.put("hldrCustId", hldrCustId);
					cmpnRsltJson.put("entrId", entrId);
					cmpnRsltJson.put("clbkRsltCd", clbkRsltCd);
					
					rsList.add(i, cmpnRsltJson);
					
					logger.info("## cmpnRsltJson :: {}", cmpnRsltJson);
				}
				// apim request body 세팅
				reqBodyJson.put("pdsDspRslt", rsList);
				logger.info("## reqBodyMap :: {}", reqBodyJson);
				
				String apiUrl 	= Configure.get("API.035101.URI");
				
				String response = callApim(apiUrl, "POST", reqBodyJson);
				logger.info("## API-035101 Get Data !! : {}", response);
				
				JSONObject resObj = new JSONObject(response);
				String rsltCd = resObj.optString("rsltCd", "N");
				if(resObj == null || !rsltCd.equals("Y")) {
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
			e.printStackTrace();
		}
		
		return dsRstlInfoMap;
	}

	
	/**
	 * 캠페인 마스터 데이터 송신 (IF-CCS-007, IF-CCS-008)
	 * gc-api-client에서 캠페인 마스터 데이터 수신
	 */
	@Override
	public HashMap<String, Object> sendCmpnMaData(JSONObject reqBody) throws Exception {
		HashMap<String, Object> dsRstlInfoMap = new HashMap<String, Object>();
		
		JSONObject reqBodyJson = new JSONObject();
		
		List<JSONObject> rsList = new ArrayList<JSONObject>();
		
		try {
			// select DB(CAMPMA) 
			// get Campaign List (From Genesys Cloud) [API ENDPOINT : /api/v2/outbound/campaigns ]
			
			// DB select List, G.C API 캠페인 리스트 비교
			// DB에 없는 Campaign 정보를 CAMPMA 테이블에 insert
			// AS-IS는 SDW 테이블 'CMD'( isnert, update, delete )에 따라 보내는 데이터가 다르다.
			/*
			 *  UCUBE - delete : 캠페인ID 전송
			 *  UCRM - update : 캠페인명 전송
			 *         delete : 캠페인ID, 센터구분코드 전송
			 *  콜봇 - update : 캠페인명, 캠페인삭제여부 전송
			 */
			
			logger.info("## reqBody JSON : {}", reqBody);
			
			// TO-BE는 Insert는 1분마다 G.C Campaign List 조회
			// 기존 DB테이블과 비교하여 없으면 신규 캠페인 insert
			// update, delete는 G.C에서 event bridge로 catch
		
			if(!reqBody.isEmpty()) {
				String cmd 		= ApiUtil.nullToString(reqBody.get("cmd"));		// insert, update, delete 구분
				String cpid 	= ApiUtil.nullToString(reqBody.get("cpid"));	// 캠페인 ID
				String cpNm 	= ApiUtil.nullToString(reqBody.get("cpna"));	// 캠페인 명
				String gubun	= ApiUtil.nullToString(reqBody.get("gubun"));	// 작업 구분코드
				String ctiDivsCd = ApiUtil.nullToString(gubun.substring(0, 1));	// CTI위치구분코드 (H:홈/기업, M:모바일)
				if(ctiDivsCd.equals("T")) {
					ctiDivsCd = "M";
				}
				
				String restMethod = "";	// http 통신 메소드
				
				if(cmd.toUpperCase().equals("INSERT")) {
					restMethod= "POST";
				}else {
					restMethod= "PUT";
				}
				
				JSONObject cmpnmaJson = new JSONObject();
				
				if(cmd.toUpperCase().equals("INSERT") || cmd.toUpperCase().equals("UPDATE")) {
					cmpnmaJson.put("cnbkId", cpid);				// 상담콜백ID
					cmpnmaJson.put("cnbkNm", cpNm);				// 상담콜백명
					cmpnmaJson.put("cnslCntrDivsCd", gubun);		// 상담센터구분코드
					cmpnmaJson.put("ctiLocDivsCd", ctiDivsCd);	// CTI위치구분코드
					
					
				} else if(cmd.toUpperCase().equals("DELETE")) {
					cmpnmaJson.put("cnbkId", cpid);				// 상담콜백ID
					
				} else {
				}
				
				rsList.add(0, cmpnmaJson);
				logger.info("## cmpnmaJson :: {}", cmpnmaJson);
				
				// apim request body 세팅
				reqBodyJson.put("cmpnDspRslt", rsList);
				logger.info("## reqBodyMap :: {}", reqBodyJson);
				
				String apiUrl 	= Configure.get("API.035102.URI");
				
				String response = callApim(apiUrl, restMethod, reqBodyJson);
				logger.info("## API-035102 Get Data !! : {}", response);
				
				JSONObject resObj = new JSONObject(response);
				String rsltCd = resObj.optString("rsltCd", "N");
				if(resObj == null || !rsltCd.equals("Y")) {
					logger.info("## API-035102 DATA NOT FOUND >> " + ApimCode.RESULT_FAIL_MSG);
					dsRstlInfoMap.put("rsltCd", ApimCode.RESULT_FAIL_MSG);
					return dsRstlInfoMap;
				} else {
					// 성공
					dsRstlInfoMap.put("rsltCd", resObj.get("rsltCd"));
					logger.info("## API-035102 데이터 전송 성공 ");
				}
				
			} else {
				dsRstlInfoMap.put("rsltCd", ApimCode.RESULT_SUCCESS_N);
				dsRstlInfoMap.put("rsltMsg", ApimCode.RESULT_FAIL_MSG);
				logger.info("## API-035102 NO DATA => " + ApimCode.RESULT_FAIL_MSG);
			}
			
		}catch(Exception e) {
			dsRstlInfoMap.put("rsltCd", ApimCode.RESULT_SUCCESS_N);
			e.printStackTrace();
		}
		
		return dsRstlInfoMap;
	}
	

	/**
	 * ARS 만족도 결과 실시간 전송 (IF-CCS-853)
	 * 
	 * SELECT 	ORDERID, 
	 * 		 	NEW_SEQ_NO, 
	 * 			NEW_SUR_SURVEY1, 
	 * 			NEW_SUR_SURVEY2,
	 * 			TO_CHAR(NEW_SUR_ANS_DATE, 'YYYY/MM/DD HH24:MI:SS') NEW_SUR_ANS_DATE
	 * FROM		TB_IVR_SURVEY_UCUBE_SDW
	 * 
	 */
	@Override
	public HashMap<String, Object> sendArsSatfRslt() throws Exception {
		HashMap<String, Object> dsRstlInfoMap = new HashMap<String, Object>();
		
		JSONArray reqBodyList = oracleService.selectUcube();
		
		JSONObject reqBodyJson = new JSONObject();;
		JSONObject arsRsltJson = null;
		
		List<JSONObject> rsList = new ArrayList<JSONObject>();
		
		try {
			if(reqBodyList.length() > 0) {
				
				for(int i=0; i<reqBodyList.length(); i++) {
					
					logger.info("## reqBodyList :: {}", reqBodyList.getJSONObject(i));
					
					arsRsltJson = new JSONObject();
					arsRsltJson.put("ismsSendSno", reqBodyList.getJSONObject(i).optString("seqNo", ""));
					arsRsltJson.put("ansrNm1", reqBodyList.getJSONObject(i).optString("surServey1", ""));
					arsRsltJson.put("ansrNm4", reqBodyList.getJSONObject(i).optString("surServey2", ""));
					arsRsltJson.put("ismsAnsrDttm", reqBodyList.getJSONObject(i).optString("surAnsDate", ""));
					
					rsList.add(i, arsRsltJson);
					
					logger.info("## cmpnRsltJson :: {}", arsRsltJson);
				}
				
				reqBodyJson.put("satfIsmsRslt", rsList);
				logger.info("## reqBodyJson :: {}", reqBodyJson);
				
				String apiUrl 	= Configure.get("API.033701.URI");
				
				String response = callApim(apiUrl, "PUT", reqBodyJson);
				logger.info("## API-033701 Get Data !! : {}", response);
				
				JSONObject resObj = new JSONObject(response);
				String rsltCd = resObj.optString("rsltCd", "N");
				if(resObj == null || !rsltCd.equals("Y")) {
					logger.info("## API-033701 GET DATA ERROR !!!! ");
					dsRstlInfoMap.put("rsltCd", ApimCode.RESULT_FAIL_MSG);
					return dsRstlInfoMap;
				} else {
					// 성공
					// 결과가 성공이면 전송했던 데이터 삭제
					dsRstlInfoMap.put("rsltCd", resObj.get("rsltCd"));
					logger.info("## API-033701 데이터 전송 성공 ");
					
					// delete ????
					
					
					
					
				}
				
			}else {
				dsRstlInfoMap.put("rsltCd", ApimCode.RESULT_SUCCESS_N);
				dsRstlInfoMap.put("rsltMsg", ApimCode.RESULT_FAIL_MSG);
				logger.info("## API-033701 NO DATA => " + ApimCode.RESULT_FAIL_MSG);
			}
			
		}catch(Exception e) {
			dsRstlInfoMap.put("rsltCd", ApimCode.RESULT_SUCCESS_N);
			e.printStackTrace();
		}

		return dsRstlInfoMap;
	}
	
	/**
	 * BS 고객만족도 결과 전송 (IF-CCSN-002)
	 * 
	 * SELECT 	SEQ_NO, 
	 * 			SUR_SURVEY1, 
	 * 			SUR_SURVEY2, 
	 * 			TO_CHAR(SUR_ANS_DATE, 'YYYY/MM/DD HH24:MI:SS') SUR_ANS_DATE
	 * FROM 	TB_IVR_SURVEY_PCUBE
	 * WHERE 	SUR_ANS_DATE > TRUNC(SYSDATE)
	 * AND 		SUR_INPUTCODE = 'Y'
	 * AND 		SUR_SURVEY1 IS NOT NULL 
	 * 
	 */
	@Override
	public HashMap<String, Object> sendBsArsSatfRslt() throws Exception {
		HashMap<String, Object> dsRstlInfoMap = new HashMap<String, Object>();
		
		JSONArray reqBodyList = oracleService.selectPcube();
		
		JSONObject reqBodyJson = null;
		JSONObject arsRsltJson = null;
		
		List<JSONObject> rsList = new ArrayList<JSONObject>();
		
		try {
			if(reqBodyList.length() > 0) {
				int sDataCnt = 0;
				
				for(int i=0; i<reqBodyList.length(); i++) {
					
					logger.info("## reqBodyList :: {}", reqBodyList.getJSONObject(i));
					
					arsRsltJson = new JSONObject();
					arsRsltJson.put("ismsSendSno", reqBodyList.getJSONObject(i).optString("seqNo", ""));
					arsRsltJson.put("ansrNm1", reqBodyList.getJSONObject(i).optString("surServey1", ""));
					arsRsltJson.put("ansrNm4", reqBodyList.getJSONObject(i).optString("surServey2", ""));
					arsRsltJson.put("ismsAnsrDttm", reqBodyList.getJSONObject(i).optString("surAnsDate", ""));
					
					rsList.add(i, arsRsltJson);
					logger.info("## cmpnRsltJson :: {}", arsRsltJson);
					
					// 건수가 많아서 500건씩 보냄???
					
					sDataCnt++;
					if(sDataCnt % 500 == 0 || sDataCnt == reqBodyList.length()) {
						reqBodyJson = new JSONObject();
						
						reqBodyJson.put("satfIsmsRslt", rsList);
						logger.info("## reqBodyJson :: {}", reqBodyJson);
						
						String apiUrl 	= Configure.get("API.033701.URI");
						
						String response = callApim(apiUrl, "PUT", reqBodyJson);
						logger.info("## API-033701 Get Data !! : {}", response);
						
						JSONObject resObj = new JSONObject(response);
						String rsltCd = resObj.optString("rsltCd", "N");
						if(resObj == null || !rsltCd.equals("Y")) {
							logger.info("## API-033701 GET DATA ERROR !!!! ");
							dsRstlInfoMap.put("rsltCd", ApimCode.RESULT_FAIL_MSG);
							return dsRstlInfoMap;
						} else {
							// 성공
							// 결과가 성공이면 전송했던 데이터 삭제
							dsRstlInfoMap.put("rsltCd", resObj.get("rsltCd"));
							logger.info("## API-033701 데이터 전송 성공 ");
							
							rsList.clear();
							rsList = new ArrayList<JSONObject>();
						}
					}
				}
				
			}else {
				dsRstlInfoMap.put("rsltCd", ApimCode.RESULT_SUCCESS_N);
				dsRstlInfoMap.put("rsltMsg", ApimCode.RESULT_FAIL_MSG);
				logger.info("## API-033701 NO DATA => " + ApimCode.RESULT_FAIL_MSG);
			}
			
		}catch(Exception e) {
			dsRstlInfoMap.put("rsltCd", ApimCode.RESULT_SUCCESS_N);
			e.printStackTrace();
		}
		
		return dsRstlInfoMap;
	}
	
	
	
	/**
	 * 
	 * Genesys Cloud DataAction에서 APIM 호출 ( 443포트만 가능 )
	 * 
	 */
	@Override
	public JSONObject callApimByDataAction(DataAction reqJson) throws Exception {
		String response = "";
		
		try {
			String url 				= reqJson.getUrl();		// APIM 호출 URL
			String method			= reqJson.getMethod();	// REST CRUD (GET, POST, PUT, DELETE)
			JSONObject headerJson	= ApiUtil.toJson(reqJson.getApimHeader());
			JSONObject bodyJson		= ApiUtil.toJson(reqJson.getApimBody());
			
			logger.info(">>> \n #url = {} \n #method = {} \n #header = {} \n #body = {}", url, method, headerJson, bodyJson);
			
			// 호출하는 api들이 GET, POST 다르고, 데이터 셋이 제각각 다르기때문에 따로 로직 구성해준다. 
			// =================   APIM 호출 ===============================
			String callPath = Configure.get("callPath");
			String ApiServer = "";
			if(callPath.equals("0") || callPath.equals("1")) {
				ApiServer = Configure.get("api.server");
			} else {
				ApiServer = Configure.get("cos.api.server");
			}
			
			// get OAuth Token
			String token	= Configure.get("api.auth.token");
			if(token.isEmpty() || token == null) {
				ApimMakeToken makeToken = new ApimMakeToken(httpAction);
				token = makeToken.getToken();
			}
			
			String clientId		= Configure.get("client.id");
			String clientSecret	= Configure.get("client.secret");
			
			String apiCont	= Configure.get("api.content.type");
			String apiAuth	= Configure.get("api.auth");
			String apiAppnm	= Configure.get("api.app.name");
			
			UriComponents uriBuilder = null;
			
			// set http header
			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", apiCont);
			headers.set("X-Forwarded-Appname", apiAppnm);
			headers.set("X-IBM-Client-id", clientId);
			headers.set("X-IBM-Client-Secret", clientSecret);
			headers.set("Authorization", apiAuth + " " + token);
			
			if("POST".equals(method.toUpperCase())) {
				// make URI builder
				uriBuilder = UriComponentsBuilder.fromUriString(ApiServer + url).build(true);
				
			} else if("GET".equals(method.toUpperCase())) {
				
				if(bodyJson != null) {
					// Query Parameter 세팅
					MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
					Iterator<String> keys = bodyJson.keySet().iterator();
					while(keys.hasNext()) {
						String key = keys.next();
						params.add(key, bodyJson.getString(key));
					}
					uriBuilder = UriComponentsBuilder.fromUriString(ApiServer + url).queryParams(params).build(true);
				} else {
					// Query Param 없으면 그냥
					uriBuilder = UriComponentsBuilder.fromUriString(ApiServer + url).build(true);
				}
				
			} else {
				
			}
			
			// APIM 호출 시 header data 세팅이 있는 경우 추가 헤더 세팅
			if(headerJson != null) {
				Iterator<String> keys = headerJson.keySet().iterator();
				while(keys.hasNext()) {
					String key = keys.next();
					headers.set(key, headerJson.getString(key));
				}
			}
			
			HttpEntity<String> entity = new HttpEntity<String>(bodyJson.toString(), headers);
			response = httpAction.restTemplateService(uriBuilder, entity, method);
			logger.info("## APIM Get Data !! : {}", response);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		return new JSONObject(response);
	}
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * APIM 호출 함수
	 * 
	 * @param apiUrl
	 * @param method
	 * @param reqBodyMap
	 * @return
	 * @throws Exception
	 */
	public String callApim(String apiUrl, String method, JSONObject reqBodyMap) throws Exception {
		String res = "";
		
		// =================   APIM 호출 ===============================
		String callPath = Configure.get("callPath");
		String ApiServer = "";
		if(callPath.equals("0") || callPath.equals("1")) {
			ApiServer = Configure.get("api.server");
		} else {
			ApiServer = Configure.get("cos.api.server");
		}
		
		String token	= Configure.get("api.auth.token");
		if(token.isEmpty() || token == null) {
			ApimMakeToken makeToken = new ApimMakeToken(httpAction);
			token = makeToken.getToken();
		}
		
		String clientId		= Configure.get("client.id");
		String clientSecret	= Configure.get("client.secret");
		
		String apiCont	= Configure.get("api.content.type");
		String apiAuth	= Configure.get("api.auth");
		String apiAppnm	= Configure.get("api.app.name");
		
		// make URI builder
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(ApiServer + apiUrl).build(true);
		// set http header
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", apiCont);
		headers.set("X-Forwarded-Appname", apiAppnm);
		headers.set("X-IBM-Client-id", clientId);
		headers.set("X-IBM-Client-Secret", clientSecret);
		headers.set("Authorization", apiAuth + " " + token);
		
		HttpEntity<String> entity = new HttpEntity<String>(reqBodyMap.toString(), headers);
		
		res = httpAction.restTemplateService(uriBuilder, entity, method);
		logger.info("## APIM Get Data !! : {}", res);
		
		
		return res;
	}
	
	
	@Override
	public String kafkaTest(Map<String, Object> reqBody) throws Exception {
		String result = "";
		
		// make URI builder
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString("http://localhost:8083/saveucrmdata").build(true);
		// set http header
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		
		HttpEntity<String> entity = new HttpEntity<String>(reqBody.toString(), headers);
		
		result =httpAction.restTemplateService(uriBuilder, entity, "POST");
		logger.info("## APIM Get Data !! : {}", result);
		
		return result;
	}
	
	
}
