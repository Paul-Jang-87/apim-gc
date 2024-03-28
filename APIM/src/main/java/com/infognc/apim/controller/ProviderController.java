package com.infognc.apim.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.infognc.apim.service.ProviderService;
import com.infognc.apim.utl.ApiUtil;
import com.infognc.apim.utl.ApimCode;
import com.infognc.apim.utl.HmacSha512Exception;
import com.infognc.apim.utl.HmacTimeoutException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class ProviderController {
	private static final Logger logger = LoggerFactory.getLogger(ProviderController.class);
	private final ProviderService pvService;
	private final String ENDPOINT_CRM = "/clcc/hmCucn/v1/luncRsvCallTsms";
	private final String ENDPOINT_CCS = "/clcc/hmCucn/v1/hmArsStafDatRtmTsms";
	
	
	public ProviderController(ProviderService pvService) {
		this.pvService = pvService;
	}
	
	/*
	 * 
	 * [유큐브 캠패인 대상자 전송]
	 * IF-API-076702 (IF-CRM-010)
	 * 
	 */
	@PostMapping(value=ENDPOINT_CRM)
	public Map<String, Object> saveStafDat(
			HttpServletRequest req, HttpServletResponse res,
			@RequestHeader(value="X-APP-NAME")String appName,
			@RequestHeader(value="X-Header-Authorization") String headerAuth,
			@RequestHeader(value="X-AuthorizationTime") String authTime,
			@RequestHeader(value="X-Global-transaction-ID") String gtid,
			@RequestBody(required=false) List<Map<String, String>> inParamList
			) throws Exception {
		
		logger.info("## IF-API-076702 START ");
		System.out.println("## IF-API-076702 START ");
		ApiUtil apiUtil = new ApiUtil();
		HashMap<String, Object> dsRsltInfoMap = new HashMap<String, Object>();	

		try {
			// Request Header Check!!
			// 요청 헤더 인증 확인 : HMAC 인증 (req/res 위변조 체크)
			// Exception 처리
			apiUtil.checkHmacAuth(appName, headerAuth, authTime, gtid);
			
		}catch(HmacSha512Exception e) {
			dsRsltInfoMap.put("rsltCd", ApimCode.RESULT_CODE_01);
			dsRsltInfoMap.put("rsltMsg", e.getMessage());
			res.setHeader("BizError", ApimCode.HEADER_BIZ_ERR_N);
			res.setStatus(417);
			
			return dsRsltInfoMap;
		}catch(HmacTimeoutException e) {
			dsRsltInfoMap.put("rsltCd", ApimCode.RESULT_CODE_02);
			dsRsltInfoMap.put("rsltMsg", e.getMessage());
			res.setHeader("BizError", ApimCode.HEADER_BIZ_ERR_N);
			res.setStatus(417);
			
			return dsRsltInfoMap;
		}catch(Exception e){
			// APIM 가이드에 맞춰서 
			// response body(dsRsltInfoMap)에 "rsltCd" : 02, "rsltMsg" : e.getMessage 세팅
			dsRsltInfoMap.put("rsltCd", ApimCode.RESULT_SUCCESS_N); 
			dsRsltInfoMap.put("rsltMsg", e.getMessage()); 
			e.printStackTrace();
			// 강제 status 발생
			// response header에 "bizError" : "N" 세팅
			// response status에 417 세팅
			res.setHeader("BizError", ApimCode.HEADER_BIZ_ERR_N);
			res.setStatus(417);
			
			return dsRsltInfoMap;
		}

		HashMap<String, String> paramChkMap = new HashMap<String, String>();
		logger.info("## IF-API-076702 inParamMap :: " + inParamList.size());
		
		HashMap<String,Object> rtnMap = new HashMap<String, Object>();
		System.out.println("## inParamList.size() :: " + inParamList.size());
		try {
			if(inParamList.size() == 0) {
				// array[Object] 상태에서 하위컬럼이 필수인 경우 []만 호출해도 필수체크를 해야합니다. -- 고한솔 사원 요청 204.03.26
				throw new Exception();
			} else {
				for(int i=0; i<inParamList.size(); i++) {
					paramChkMap = new HashMap<String,String>();
					paramChkMap.put("cpid", inParamList.get(i).get("cpid"));
					paramChkMap.put("cpsq", inParamList.get(i).get("cpsq"));
					apiUtil.hasValue(paramChkMap, new String[] {"cpid", "cpsq"});
				}
			}

		}catch(Exception ex) {
			// 강제 status 발생
			res.setStatus(200);
			res.setHeader("bizError", ApimCode.HEADER_BIZ_ERR_Y);
			
			rtnMap.put("serverName", "clcc");
			rtnMap.put("url", ENDPOINT_CRM);
			rtnMap.put("method", "POST");
			rtnMap.put("errorStack", ex.getMessage());
			rtnMap.put("errorServer", "clcc");
			rtnMap.put("xForwardService", "clcc");
			rtnMap.put("errorCode", "400");
			rtnMap.put("errorDetail", "BizException [code=400,message="+ex.getMessage()+"]");
			rtnMap.put("hasErrorDetail", true);
			rtnMap.put("version", "wafful-3.0");
			rtnMap.put("errorMsg", "필수입력 항목 누락입니다.");
			dsRsltInfoMap.put("moreInfomation", rtnMap);
			return dsRsltInfoMap;
		}
		
		Integer resInt = 0;
		try {
			resInt = pvService.sendCampListToGc(inParamList);
			if(resInt > 0) {
				dsRsltInfoMap.put("rstlCd", ApimCode.RESULT_SUCCESS_Y);
				dsRsltInfoMap.put("rstlMsg", ApimCode.RESULT_SUCC_SAVE_MSG);
				logger.info("## IF-API-076702 PRSS result >> " + ApimCode.RESULT_SUCC_SAVE_MSG);
			} else {
				// error
				dsRsltInfoMap.put("rstlCd", ApimCode.RESULT_SUCCESS_N);
				dsRsltInfoMap.put("rstlMsg", ApimCode.RESULT_FAIL_SAVE_MSG);
				logger.info("## IF-API-076702 PRSS result >> " + ApimCode.RESULT_FAIL_SAVE_MSG);
			}
			
		}catch(Exception ex) {
			// 강제 status 발생
			res.setStatus(200);
			res.setHeader("bizError", ApimCode.HEADER_BIZ_ERR_Y);
			
			rtnMap.put("serverName", "clcc");
			rtnMap.put("url", ENDPOINT_CRM);
			rtnMap.put("method", "POST");
			rtnMap.put("errorStack", ex.getMessage());
			rtnMap.put("errorServer", "clcc");
			rtnMap.put("xForwardService", "clcc");
			rtnMap.put("errorCode", "400");
			rtnMap.put("errorDetail", ex.getMessage());
			rtnMap.put("hasErrorDetail", true);
			rtnMap.put("version", "wafful-3.0");
			rtnMap.put("errorMsg", ex.getMessage());
			dsRsltInfoMap.put("moreInfomation", rtnMap);
		}
		
		
		return dsRsltInfoMap;
	}
	
	
	
	/*
	 * [ARS 고객만족도 실시간 자료전송, BS고객만족도 조사수행]
	 * IF-API-076701 (IF-CCS-852, IF-CCSN-001)
	 */
	@PostMapping(value=ENDPOINT_CCS)
	public Map<String, Object> saveRsvCallDat(
			HttpServletRequest req, HttpServletResponse res,
			@RequestHeader(value="X-APP-NAME")String appName,
			@RequestHeader(value="X-Header-Authorization") String headerAuth,
			@RequestHeader(value="X-AuthorizationTime") String authTime,
			@RequestHeader(value="X-Global-transaction-Id") String gtid,
			@RequestBody(required=false) List<Map<String, String>> inParamList
			) throws Exception {
		
		logger.info("## IF-API-076701 START ");
		System.out.println("## IF-API-076701 START - syso");
		
		logger.info(">> X-APP-NAME : {}", appName);
		logger.info(">> X-Header-Authorization : {}", headerAuth);
		logger.info(">> X-AuthorizationTime : {}", authTime);
		logger.info(">> X-Global-transaction-Id : {}", gtid);
		
		ApiUtil apiUtil = new ApiUtil();
		HashMap<String, Object> dsRsltInfoMap = new HashMap<String, Object>();	
		
		try {
			// Request Header Check!!
			// 요청 헤더 인증 확인 : HMAC 인증 (req/res 위변조 체크)
			// Exception 처리
			apiUtil.checkHmacAuth(appName, headerAuth, authTime, gtid);
			
		}catch(HmacSha512Exception e) {
			dsRsltInfoMap.put("rsltCd", ApimCode.RESULT_CODE_01);
			dsRsltInfoMap.put("rsltMsg", e.getMessage());
			res.setHeader("BizError", ApimCode.HEADER_BIZ_ERR_N);
			res.setStatus(417);
			
			return dsRsltInfoMap;
		}catch(HmacTimeoutException e) {
			dsRsltInfoMap.put("rsltCd", ApimCode.RESULT_CODE_02);
			dsRsltInfoMap.put("rsltMsg", e.getMessage());
			res.setHeader("BizError", ApimCode.HEADER_BIZ_ERR_N);
			res.setStatus(417);
			
			return dsRsltInfoMap;
		}catch(Exception e){
			// APIM 가이드에 맞춰서 
			// response body(dsRsltInfoMap)에 "rsltCd" : 02, "rsltMsg" : e.getMessage 세팅
			dsRsltInfoMap.put("rsltCd", ApimCode.RESULT_SUCCESS_N); 
			dsRsltInfoMap.put("rsltMsg", e.getMessage()); 
			// 강제 status 발생
			// response header에 "bizError" : "N" 세팅
			// response status에 417 세팅
			res.setHeader("BizError", ApimCode.HEADER_BIZ_ERR_N);
			res.setStatus(417);
			
			return dsRsltInfoMap;
		}
		
		HashMap<String, String> paramChkMap = new HashMap<String, String>();
		logger.info("## IF-API-076701 inParamMap :: " + inParamList.size());
		
		
		List<Map<String, String>> paramMaps = new ArrayList<Map<String,String>>();
		HashMap<String,Object> rtnMap = new HashMap<String, Object>();
		String rtnStr = "";
		try {
			for(int i=0; i<inParamList.size(); i++) {
				paramChkMap = new HashMap<String,String>();
				paramChkMap.put("seqNo", inParamList.get(i).get("seqNo"));
				paramChkMap.put("surAni", inParamList.get(i).get("surAni"));
				paramChkMap.put("surGubun", inParamList.get(i).get("surGubun"));
				apiUtil.hasValue(paramChkMap, new String[] {"seqNo", "surAni", "surGubun"});
				
				if(!inParamList.get(i).get("surGubun").equals("BS") && !inParamList.get(i).get("surGubun").equals("C")) {
					// 강제 status 발생
					res.setStatus(200);
					res.setHeader("bizError", ApimCode.HEADER_BIZ_ERR_Y);
					
					rtnMap.put("serverName", "clcc");
					rtnMap.put("url", ENDPOINT_CCS);
					rtnMap.put("method", "POST");
					rtnMap.put("errorStack", "잘못된 구분값입니다.");
					rtnMap.put("errorServer", "clcc");
					rtnMap.put("xForwardService", "clcc");
					rtnMap.put("errorCode", "400");
					rtnMap.put("errorDetail", "BizException [code=400,message="+"잘못된 구분값입니다."+"]");
					rtnMap.put("hasErrorDetail", true);
					rtnMap.put("version", "wafful-3.0");
					rtnMap.put("errorMsg", "잘못된 구분값입니다.");
					dsRsltInfoMap.put("moreInfomation", rtnMap);
					return dsRsltInfoMap;
				} else {
					
				}
			}
		}catch(Exception ex) {
			// 강제 status 발생
			res.setStatus(200);
			res.setHeader("bizError", ApimCode.HEADER_BIZ_ERR_Y);
			
			rtnMap.put("serverName", "clcc");
			rtnMap.put("url", ENDPOINT_CCS);
			rtnMap.put("method", "POST");
			rtnMap.put("errorStack", ex.getMessage());
			rtnMap.put("errorServer", "clcc");
			rtnMap.put("xForwardService", "clcc");
			rtnMap.put("errorCode", "400");
			rtnMap.put("errorDetail", "BizException [code=400,message="+rtnStr+"]");
			rtnMap.put("hasErrorDetail", true);
			rtnMap.put("version", "wafful-3.0");
			rtnMap.put("errorMsg", "누적번호는(은) 필수입력 항목입니다.");
			dsRsltInfoMap.put("moreInfomation", rtnMap);
			return dsRsltInfoMap;
		}
		
		Integer resInt = 0;
		try {
			resInt = pvService.sendArsStafData(inParamList);
			if(resInt > 0) {
				dsRsltInfoMap.put("rstlCd", ApimCode.RESULT_SUCCESS_Y);
				dsRsltInfoMap.put("rstlMsg", ApimCode.RESULT_SUCC_SAVE_MSG);
				logger.info("## IF-API-076701 PRSS result >> " + ApimCode.RESULT_SUCC_SAVE_MSG);
			} else {
				// error
				dsRsltInfoMap.put("rstlCd", ApimCode.RESULT_SUCCESS_N);
				dsRsltInfoMap.put("rstlMsg", ApimCode.RESULT_FAIL_SAVE_MSG);
				logger.info("## IF-API-076701 PRSS result >> " + ApimCode.RESULT_FAIL_SAVE_MSG);
			}
			
			
		}catch(Exception ex) {
			// 강제 status 발생
			res.setStatus(200);
			res.setHeader("bizError", ApimCode.HEADER_BIZ_ERR_Y);
			
			rtnMap.put("serverName", "clcc");
			rtnMap.put("url", ENDPOINT_CCS);
			rtnMap.put("method", "POST");
			rtnMap.put("errorStack", ex.getMessage());
			rtnMap.put("errorServer", "clcc");
			rtnMap.put("xForwardService", "clcc");
			rtnMap.put("errorCode", "400");
			rtnMap.put("errorDetail", ex.getMessage());
			rtnMap.put("hasErrorDetail", true);
			rtnMap.put("version", "wafful-3.0");
			rtnMap.put("errorMsg", ex.getMessage());
			dsRsltInfoMap.put("moreInfomation", rtnMap);
		}
		
		return dsRsltInfoMap;
	}
	

	
}
