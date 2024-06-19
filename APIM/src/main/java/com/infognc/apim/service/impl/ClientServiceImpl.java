package com.infognc.apim.service.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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

import com.infognc.apim.ApimConfig;
import com.infognc.apim.ApimMakeToken;
import com.infognc.apim.gc.ClientAction;
import com.infognc.apim.gc.DataAction;
import com.infognc.apim.gc.HttpAction;
import com.infognc.apim.service.ClientService;
import com.infognc.apim.service.OracleService;
import com.infognc.apim.util.ApiUtil;
import com.infognc.apim.util.ApimCode;
import com.infognc.apim.util.Configure;

@Service
public class ClientServiceImpl implements ClientService{
	private static final Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);
	private final HttpAction httpAction;
	private final ClientAction clientAction;
	private final OracleService oracleService;
	
	public ClientServiceImpl(HttpAction httpAction, ClientAction clientAction, OracleService oracleService) {
		this.httpAction = httpAction;
		this.clientAction = clientAction;
		this.oracleService = oracleService;
	}
	
	
	/**
	 *  IF-CCS-005, IF-CCS-006 캠페인 결과 
	 *  gc-api-client에서 캠페인 결과(TKDA, DIRT) 데이터 수신
	 */
	@Override
	public HashMap<String, Object> sendCmpnRsltList(JSONArray reqBodyList) throws Exception {
		ApiUtil apiUtil = new ApiUtil();
		HashMap<String, Object> dsRstlInfoMap = new HashMap<String, Object>();
		
//		HashMap<String, Object> reqBodyMap = new HashMap<String, Object>();
		JSONObject reqBodyJson = new JSONObject();
		JSONObject cmpnRsltJson = null;
		
		List<JSONObject> rsList = new ArrayList<JSONObject>();
		
		logger.info("## reqBodyList length :: {}", reqBodyList.length());
		
		try {
			// 캠페인 종료시점을 gc-api-client app에서 이벤트브릿지를 통해 전달 받음.
			// gc-api-client에서 UCRM, 콜봇 캠페인이 아닌경우 CAMPRT 데이터를 APIM으로 전달 (reqBody)
			if(reqBodyList.length() > 0) {
				String tkda = "";	// 토큰데이터
				String dirt = "";	// 발신결과코드
				String dict = "";	// 발신시도 횟수
				
				for(int i=0; i<reqBodyList.length(); i++) {
					
					logger.info("## reqBodyList.get({}) :: {}", i, reqBodyList.getJSONObject(i));

//					if(reqBodyList.get(i).get("tkda") != null) tkda = reqBodyList.get(i).get("tkda").toString();
//					if(reqBodyList.get(i).get("dirt") != null) dirt = reqBodyList.get(i).get("dirt").toString();
					tkda = reqBodyList.getJSONObject(i).optString("tkda");
					dirt = reqBodyList.getJSONObject(i).optString("dirt");
					dict = reqBodyList.getJSONObject(i).optString("dict");
					
					logger.info("## tkda_{} : {}", i, tkda);
					logger.info("## dirt_{} : {}", i, dirt);
					logger.info("## dict_{} : {}", i, dict);
					
					if(dirt.equals("")) dirt = "0";
					
					String tkdaInitial = tkda.substring(0, 1);
					if(tkdaInitial.equals("S") && Integer.parseInt(dirt) > 1) {
//					if(dict.equals("2") && tkdaInitial.equals("S") && Integer.parseInt(dirt) > 1) {
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
						cmpnRsltJson.put("hldrCustId", apiUtil.encode(hldrCustId));
						cmpnRsltJson.put("entrId", apiUtil.encode(entrId));
						cmpnRsltJson.put("clbkRsltCd", clbkRsltCd);
						
						rsList.add(i, cmpnRsltJson);
						
						logger.info("## cmpnRsltJson :: {}", cmpnRsltJson);
					}
				}
				// apim request body 세팅
				if(rsList.size() > 0) {
					reqBodyJson.put("pdsDspRslt", rsList);
				}
				logger.info("## reqBodyMap :: {}", reqBodyJson);
				
				String apiUrl 	= Configure.get("API.035101.URI");
				
				if(reqBodyJson.length() > 0) {
					
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
				}
				
			} else {
				dsRstlInfoMap.put("rsltCd", ApimCode.RESULT_SUCCESS_N);
				dsRstlInfoMap.put("rsltMsg", ApimCode.RESULT_FAIL_MSG);
				logger.info("## API-035101 NO DATA => " + ApimCode.RESULT_FAIL_MSG);
			}
			
			
		}catch(Exception e) {
			dsRstlInfoMap.put("rsltCd", ApimCode.RESULT_SUCCESS_N);
			logger.error("Exception 발생 : {}", e.getMessage(), e);
		}
		
		return dsRstlInfoMap;
	}

	
	/**
	 * 캠페인 마스터 데이터 송신 (IF-CCS-007, IF-CCS-008)
	 * gc-api-client에서 캠페인 마스터 데이터 수신
	 */
	@Override
	public HashMap<String, Object> sendCmpnMaData(JSONObject reqBody) throws Exception {
		String logAPINm = "";
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
			
			// 기본적으로 gc-api-client에서 아래 서비스 로직 구현
			// 1. TO-BE는 Insert는 1분마다 G.C Campaign List 조회
			// 2. 기존 DB테이블과 비교하여 없으면 신규 캠페인 insert
			// 3. update, delete는 G.C에서 event bridge로 전송 (gc-api-client endpoint 호출)
		
			if(!reqBody.isEmpty()) {
				String cmd 		= ApiUtil.nullToString(reqBody.get("cmd"));		// insert, update, delete 구분
				String cpid 	= ApiUtil.nullToString(reqBody.get("cpid"));	// 캠페인 ID
				String cpNm 	= ApiUtil.nullToString(reqBody.get("cpna"));	// 캠페인 명
				String gubun	= ApiUtil.nullToString(reqBody.get("gubun"));	// 작업 구분코드
				String ctiDivsCd = "";											// CTI위치구분코드 (H:홈/기업, M:모바일)
				String divisionId = "";
				String divisionNm = "";
				
				if(cmd.toUpperCase().equals("INSERT") || cmd.toUpperCase().equals("UPDATE")) {
					// GC API 호출
					clientAction.init();
					
					String gcUrl = "/api/v2/outbound/campaigns/{campaignId}";
					// CampID로 ContactListId 가져온다.
					// API Enpoint [GET] /api/v2/outbound/campaigns/{campaignId}
					String resCmpList = clientAction.callApiRestTemplate_GET(gcUrl, cpid);
					JSONObject cmpList = new JSONObject(resCmpList);
					if(!cmpList.isEmpty()) {
						divisionId = ((JSONObject) cmpList.optJSONObject("division", new JSONObject())).optString("id", "");
						divisionNm = ((JSONObject) cmpList.optJSONObject("division", new JSONObject())).optString("name", "");
					}
					
					logger.info("## divisionId :: " + divisionId);
					logger.info("## divisionNm :: " + divisionNm);
					
					String divUcubeHome 	= Configure.get("DIV_UCUBE_HOME");
					String divUcubeMobile 	= Configure.get("DIV_UCUBE_MOBILE");
					
					if("유큐브모바일".equals(divisionNm) || divUcubeMobile.equals(divisionId)) {
						ctiDivsCd = "M";
					} else if ("유큐브홈".equals(divisionNm) || divUcubeHome.equals(divisionId)) {
						ctiDivsCd = "H";
					} else {
						ctiDivsCd = "";
					}
				}
				
				JSONObject cmpnmaJson = new JSONObject();
				String restMethod = "";	// http 통신 메소드
				String apiUrl = "";
				if(cmd.toUpperCase().equals("INSERT")) {
					restMethod= "POST";
					cmpnmaJson.put("cnbkId", cpid);				// 상담콜백ID
					cmpnmaJson.put("cnbkNm", cpNm);				// 상담콜백명
					cmpnmaJson.put("cnslCntrDivsCd", gubun);	// 상담센터구분코드
					cmpnmaJson.put("ctiLocDivsCd", ctiDivsCd);	// CTI위치구분코드
					
					apiUrl 	= Configure.get("API.035102.URI");
					logAPINm = "IF-API-035102";
					
				} else if(cmd.toUpperCase().equals("UPDATE")) {
					restMethod= "PUT";
					cmpnmaJson.put("cnbkId", cpid);				// 상담콜백ID
					cmpnmaJson.put("cnbkNm", cpNm);				// 상담콜백명
					cmpnmaJson.put("cnslCntrDivsCd", gubun);	// 상담센터구분코드
					cmpnmaJson.put("ctiLocDivsCd", ctiDivsCd);	// CTI위치구분코드
					
					apiUrl 	= Configure.get("API.035103.URI");
					logAPINm = "IF-API-035103";
					
				} else if(cmd.toUpperCase().equals("DELETE")) {
					restMethod= "PUT";
					cmpnmaJson.put("cnbkId", cpid);				// 상담콜백ID
					
					apiUrl 	= Configure.get("API.035104.URI");
					logAPINm = "IF-API-035104";
					
				} else {
				}
				
				rsList.add(0, cmpnmaJson);
				logger.info("## cmpnmaJson :: {}", cmpnmaJson);
				
				// apim request body 세팅
				reqBodyJson.put("cmpnDspRslt", rsList);
				logger.info("## reqBodyMap :: {}", reqBodyJson);
				
				// 유큐브가 아닌경우는 호출 X
				if(!ctiDivsCd.equals("")) {
					String response = callApim(apiUrl, restMethod, reqBodyJson);
					logger.info("## {} Get Data !! : {}", logAPINm, response);
					
					JSONObject resObj = new JSONObject(response);
					String rsltCd = resObj.optString("rsltCd", "N");
					if(resObj == null || !rsltCd.equals("Y")) {
						logger.info("## {} DATA NOT FOUND >> {} ", logAPINm, ApimCode.RESULT_FAIL_MSG);
						dsRstlInfoMap.put("rsltCd", ApimCode.RESULT_FAIL_MSG);
						return dsRstlInfoMap;
					} else {
						// 성공
						dsRstlInfoMap.put("rsltCd", resObj.get("rsltCd"));
						logger.info("## {} 데이터 전송 성공 ", logAPINm);
					}
				}
				
			} else {
				dsRstlInfoMap.put("rsltCd", ApimCode.RESULT_SUCCESS_N);
				dsRstlInfoMap.put("rsltMsg", ApimCode.RESULT_FAIL_MSG);
				logger.info("## {} NO DATA => {}", logAPINm, ApimCode.RESULT_FAIL_MSG);
			}
			
		}catch(Exception e) {
			dsRstlInfoMap.put("rsltCd", ApimCode.RESULT_SUCCESS_N);
			logger.error("Exception 발생 : {}", e.getMessage(), e);
		}
		
		return dsRstlInfoMap;
	}
	

	/**
	 * 배치 (1분)
	 * ARS 만족도 결과 실시간 전송 (IF-CCS-853)
	 * 
	 * SELECT 	ORDERID, 
	 * 		 	NEW_SEQ_NO, 
	 * 			NEW_SUR_SURVEY1, 
	 * 			NEW_SUR_SURVEY2,
	 * 			TO_CHAR(NEW_SUR_ANS_DATE, 'YYYY/MM/DD HH24:MI:SS') NEW_SUR_ANS_DATE
	 * FROM		TB_IVR_SURVEY_UCUBE_W
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
					arsRsltJson.put("ismsSendSno", reqBodyList.getJSONObject(i).optString("seq_no", ""));
					arsRsltJson.put("ansrNm1", reqBodyList.getJSONObject(i).optString("sur_survey1", ""));
					arsRsltJson.put("ansrNm4", reqBodyList.getJSONObject(i).optString("sur_survey2", ""));
					arsRsltJson.put("ismsAnsrDttm", reqBodyList.getJSONObject(i).optString("sur_ans_date", ""));
					
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
					for(int j=0; j<rsList.size(); j++) {
						String ismsSendSno = rsList.get(j).optString("ismsSendSno", "");
						oracleService.deleteUcubeSdw(ismsSendSno);
					}
				}
				
			}else {
				dsRstlInfoMap.put("rsltCd", ApimCode.RESULT_SUCCESS_N);
				dsRstlInfoMap.put("rsltMsg", ApimCode.RESULT_FAIL_MSG);
				logger.info("## API-033701 NO DATA => " + ApimCode.RESULT_FAIL_MSG);
			}
			
		}catch(Exception e) {
			dsRstlInfoMap.put("rsltCd", ApimCode.RESULT_SUCCESS_N);
			logger.error("Exception 발생 : {}", e.getMessage(), e);
		}

		return dsRstlInfoMap;
	}
	
	/**
	 * 배치 ( 일 16:30 )
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
					arsRsltJson.put("ismsSendSno", reqBodyList.getJSONObject(i).optString("seq_no", ""));
					arsRsltJson.put("ansrNm1", reqBodyList.getJSONObject(i).optString("sur_survey1", ""));
					arsRsltJson.put("ansrNm4", reqBodyList.getJSONObject(i).optString("sur_survey2", ""));
					arsRsltJson.put("ismsAnsrDttm", reqBodyList.getJSONObject(i).optString("sur_ans_date", ""));
					
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
							
//							rsList.clear();
//							rsList = new ArrayList<JSONObject>();
							
							
							
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
			logger.error("Exception 발생 : {}", e.getMessage(), e);
		}
		
		return dsRstlInfoMap;
	}
	
	
	
	/**
	 * 
	 * Genesys Cloud DataAction에서 APIM 호출 ( 443포트만 가능 )
	 * 
	 */
	@SuppressWarnings("null")
	@Override
	public JSONObject callApimByDataAction(DataAction reqJson) throws Exception {
		String response = "";
		
		try {
			String url 				= reqJson.getUrl();			// APIM 호출 URL
			String method			= reqJson.getMethod();		// REST CRUD (GET, POST, PUT, DELETE)
			String pathParam		= reqJson.getApimPath();	// URL Path parameter
			JSONObject queryParam	= ApiUtil.toJson(reqJson.getApimQuery());	// URL Query parameter
			JSONObject headerJson	= ApiUtil.toJson(reqJson.getApimHeader());	// APIM request Header
			JSONObject bodyJson		= ApiUtil.toJson(reqJson.getApimBody());	// APIM request Body (POST)
			
			logger.info(">>> \n # url = {} \n # method = {} \n # path = {} \n # query = {} \n # header = {} \n # body = {}", url, method, pathParam, queryParam, headerJson, bodyJson);
			
			
			// 따로 필요하면 사용, 아니면 주석
			/*
			// 호출하는 api들이 GET, POST 다르고, 데이터 셋이 제각각 다르기때문에 따로 로직 구성해준다. 
			// =================   APIM 호출 ===============================
			String callPath = Configure.get("callPath");
			String ApiServer = "";
			if(callPath.equals("0") || callPath.equals("1")) {
				ApiServer = Configure.get("api.server");
			} else {
				ApiServer = Configure.get("cos.api.server");
			}
			url = ApiServer + url;
			*/
			// get OAuth Token
			
			ApimConfig apimConfig = new ApimConfig();
			apimConfig.configure();
			
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
			
			// URI 세팅
			uriBuilder = setURI(url, pathParam, queryParam);
			
			// APIM 호출 시 header data 세팅이 있는 경우 추가 헤더 세팅
			if(!headerJson.isEmpty()) {
				Iterator<String> keys = headerJson.keySet().iterator();
				while(keys.hasNext()) {
					String key = keys.next();
					String value = headerJson.getString(key);
					headers.set(key, value);
				}
			}
			
			HttpEntity<String> entity = null;
			if(!bodyJson.isEmpty()) {
				entity = new HttpEntity<String>(bodyJson.toString(), headers);
			} else {
				entity = new HttpEntity<String>(headers);
			}
			
			response = httpAction.restTemplateService(uriBuilder, entity, method);
			logger.info("## APIM Get Data !! : {}", response);
			
		}catch(Exception e) {
			logger.error("Exception 발생 : {}", e.getMessage(), e);
		}
		
		// 2024.05.08 JJH
		// Genesys Cloud에서 Base64 Decoding이 안되기 때문에 데이터중에 Base64 인코딩된 데이터는 디코딩해서 넘겨준다.
		
		return ApiUtil.transferBase64EncodingToJson(new JSONObject(response));
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
		ApimConfig apimConfig = new ApimConfig();
		apimConfig.configure();
		
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
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(ApiServer + apiUrl).build();
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
	
	/**
	 * 
	 * RestTemplate를 사용한 UriComponent 세팅
	 * query parameter, path variable 설정
	 * 
	 * @param url
	 * @param path
	 * @param queryJson
	 * @return
	 * @throws Exception
	 */
	public UriComponents setURI(String url, String path, JSONObject queryJson) throws Exception {
		UriComponents uriBuilder = null;
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		logger.info("## path variable :: " + path);
		// URI Query parameter BASE64 SafeURL 변경
		if(!queryJson.isEmpty()) {
			logger.info("## queryJson :: " + queryJson.toString());
			for (String key : queryJson.keySet()) {
				params.add(key, queryJson.optString(key, ""));
			}
		}
		
		// uri 특수문자
		if(!"".equals(path) && !queryJson.isEmpty()) {
			// path O, query O
			System.out.println("## path O, query O");
			Object[] pathArr = path.trim().split(",");
			uriBuilder = UriComponentsBuilder.fromUriString(url).queryParams(params).buildAndExpand(pathArr);
		} else if(!queryJson.isEmpty()) {
			// path X, query O
			System.out.println("## path X, query O");
//			uriBuilder = UriComponentsBuilder.fromUriString(url).queryParams(params).build(true);
			URI uri = new URI(url);
			uriBuilder = UriComponentsBuilder.fromUri(uri).queryParams(params).build();
		} else if(!"".equals(path)) {
			// path O, query X
			System.out.println("## path O, query X");
			Object[] pathArr = path.trim().split(",");
			uriBuilder = UriComponentsBuilder.fromUriString(url).buildAndExpand(pathArr);
		} else {
			// path X, query X
			System.out.println("## path X, query X");
			uriBuilder = UriComponentsBuilder.fromUriString(url).build(true);
		}
		
		System.out.println("## uriBuilder :: " + uriBuilder.toString());
		
		return uriBuilder;
	}
	
	
}
