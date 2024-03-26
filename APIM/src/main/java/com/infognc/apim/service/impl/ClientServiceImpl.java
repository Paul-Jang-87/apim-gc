package com.infognc.apim.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.infognc.apim.ApimMakeToken;
import com.infognc.apim.gc.HttpAction;
import com.infognc.apim.service.ClientService;
import com.infognc.apim.utl.ApiUtil;
import com.infognc.apim.utl.ApimCode;
import com.infognc.apim.utl.Configure;

@Service
public class ClientServiceImpl implements ClientService{
	private static final Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);
	private final HttpAction httpAction;
	
	public ClientServiceImpl(HttpAction httpAction) {
		this.httpAction = httpAction;
	}
	
	
	/**
	 *  IF-CCS-005, IF-CCS-006 캠페인 결과 
	 *  gc-api-client에서 캠페인 결과(TKDA, DIRT) 데이터 수신
	 */
	@Override
	public HashMap<String, Object> sendCmpnRsltList(List<Map<String, Object>> reqBodyList) throws Exception {
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
				
				String apiUrl 	= Configure.get("API.035101.URI");
				
				String response = callApim(apiUrl, "POST", reqBodyMap);
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

	
	/**
	 * 캠페인 마스터 데이터 송신 (IF-CCS-007, IF-CCS-008)
	 * gc-api-client에서 캠페인 마스터 데이터 수신
	 */
	@Override
	public HashMap<String, Object> sendCmpnMaData(List<Map<String, Object>> reqBodyList) throws Exception {
		HashMap<String, Object> dsRstlInfoMap = new HashMap<String, Object>();
		
		HashMap<String, Object> reqBodyMap = new HashMap<String, Object>();
		
		List<Map<String, Object>> rsList = new ArrayList<Map<String, Object>>();
		HashMap<String, Object> cmpnMaMap = new HashMap<String, Object>();
		
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
			
			
			// TO-BE는 Insert는 1분마다 G.C Campaign List 조회
			// 기존 DB테이블과 비교하여 없으면 신규 캠페인 insert
			// update, delete는 G.C에서 event bridge로 catch
		
			if(reqBodyList.size() > 0) {
				String cmd 		= "";	// insert, update, delete 구분
				String cpid 	= "";	// 캠페인 ID
				String cpNm 	= "";	// 캠페인 명
				String gubun	= "";	// 작업 구분코드
				String ctiDivsCd = "";	// CTI위치구분코드 (H:홈/기업, M:모바일)
				String restMethod = "";	// http 통신 메소드
				
				
				if(cmd.toUpperCase().equals("INSERT")) {
					restMethod= "POST";
				}else {
					restMethod= "PUT";
				}
				
				for(int i=0; i>reqBodyList.size(); i++) {

					logger.info("## reqBodyList.get({}) :: {}", i, reqBodyList.get(i));
					
					cmpnMaMap = new HashMap<String, Object>();
					if(cmd.toUpperCase().equals("INSERT") || cmd.toUpperCase().equals("UPDATE")) {
						cmpnMaMap.put("cnbkId", cpid);				// 상담콜백ID
						cmpnMaMap.put("cnbkNm", cpNm);				// 상담콜백명
						cmpnMaMap.put("cnslCntrDivsCd", gubun);		// 상담센터구분코드
						cmpnMaMap.put("ctLocDivsCd", ctiDivsCd);	// CTI위치구분코드
						
					} else if(cmd.toUpperCase().equals("DELETE")) {
						cmpnMaMap.put("cnbkId", cpid);				// 상담콜백ID
						
					} else {
					}
					
					rsList.add(i, reqBodyMap);
					logger.info("## cmpnRsltMap :: {}", cmpnMaMap);
				}
				// apim request body 세팅
				reqBodyMap.put("cmpnDspRslt", rsList);
				logger.info("## reqBodyMap :: {}", reqBodyMap);
				
				String apiUrl 	= Configure.get("API.035102.URI");
				
				String response = callApim(apiUrl, restMethod, reqBodyMap);
				logger.info("## API-035102 Get Data !! : {}", response);
				
				JSONObject resObj = new JSONObject(response);
				if(resObj == null || !resObj.get("rsltCd").equals("Y")) {
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
		}
		
		return dsRstlInfoMap;
	}
	
	
	/**
	 * 
	 * Genesys Cloud DataAction에서 APIM 호출 ( 443포트만 가능 )
	 * 
	 */
	@Override
	public JSONObject callApimByDataAction(JSONObject reqJson) throws Exception {
		String url 			= (String) reqJson.get("url");		// APIM 호출 URL
		String method		= (String) reqJson.get("method");	// REST CRUD (GET, POST, PUT, DELETE)
//		JSONArray bodyList 	= reqJson.getJSONArray("bodyList");	// APIM request body List (List로 받을 필요 없다)
		JSONObject bodyJson	= reqJson.getJSONObject("apimBody");
		
		String response = callApim(url, method, ApiUtil.toMap(bodyJson));
		
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
	public String callApim(String apiUrl, String method, HashMap<String, Object> reqBodyMap) throws Exception {
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
	
	
	
}
