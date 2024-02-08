package com.infognc.apim.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class ProviderController {

	@PostMapping(value="/ars/hs/v1/hmArsStafDatRtmTsms")
	public Map<String, Object> saveStafDat(
			HttpServletRequest req, HttpServletResponse res,
			@RequestHeader(value="X-APP-NAME")String appName,
			@RequestHeader(value="X-Header-Authorization") String headerAuth,
			@RequestHeader(value="X-AuthorizationTime") String authTime,
			@RequestHeader(value="X-Global-transaction-Id") String gtid,
			@RequestBody(required=false) List<Map<String, String>> inParmList
			) throws Exception {
		
		HashMap dsRsltInfoMap = new HashMap();	
		
		try {
			// Request Header Check!!
			// 요청 헤더 인증 확인 : HMAC 인증 (req/res 위변조 체크)
			// Exception 처리
		}catch(Exception e){
			// APIM 가이드에 맞춰서 
			// response body(dsRsltInfoMap)에 "rsltCd" : 02, "rsltMsg" : e.getMessage 세팅
			dsRsltInfoMap.put("rsltCd", 02); 
			dsRsltInfoMap.put("rsltMsg", e.getMessage()); 
			// 강제 status 발생
			// response header에 "bizError" : "N" 세팅
			// response status에 417 세팅
			res.setHeader("BizError", "N");
			res.setStatus(417);
			
			
		}
		
		
		return dsRsltInfoMap;
	}
	
	
}
