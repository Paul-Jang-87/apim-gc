package com.infognc.apim;

import java.util.HashMap;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.infognc.apim.gc.HttpAction;
import com.infognc.apim.utl.ApimCode;
import com.infognc.apim.utl.Configure;

public class ApimMakeToken {
	private static final Logger logger = LoggerFactory.getLogger(ApimMakeToken.class);
	private final HttpAction httpAction;
//	private final ClientAction clientAction;
	
	public ApimMakeToken(HttpAction httpAction) {
		this.httpAction = httpAction;
//		this.clientAction = clientAction;
	}
	
	@Scheduled(fixedDelay=86400*1000)
	public String getToken() throws Exception {
//		clientAction.init();
		
		String callPath 		= Configure.get("callPath");
		String tokenApiServer 	= "";
		if(callPath.equals("0") || callPath.equals("2")) {
			tokenApiServer		= Configure.get("token.cos.api.server");
		} else {
			tokenApiServer		= Configure.get("token.api.server");
		}
		
		String clientId			= Configure.get("client.id");
		String clientSecret		= Configure.get("client.secret");
		String methodType		= "POST";
		String scope			= "BL CM CC RC EA PM CM";
		
		HashMap<String,String> dsRsltInfoMap = new HashMap<String,String>();
		
		// token 호출 url
		String url 				= "/uplus/intuser/oauth2/token";
		url 					= "https://" + tokenApiServer + ":443" + url;
		// header 세팅
		HttpHeaders headers = new HttpHeaders();
//		headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
		headers.set("Content-Type", "application/x-www-form-urlencoded");
		headers.set("X-Forwarded-Appname", "UPLUS_ARS");
		
		// parameter 세팅
		MultiValueMap<String, String> param = new LinkedMultiValueMap<String, String>();
		param.add("grant_type", "client_credentials");
		param.add("client_id", clientId);
		param.add("client_secret", clientSecret);
		param.add("scope", scope);
		
		// url 세팅
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(url)
									.queryParams(param)
									.build(true);
		
		
//		String result = clientAction.restTemplateService(uriBuilder, headers, methodType);
		String result = httpAction.restTemplateService(uriBuilder, headers, methodType);
		if(result==null) {
			logger.info(">>> get token error !!!");
			return null;
		}
		
		String token = "";
		try {
			JSONObject tokenObj = new JSONObject(result);
			token = (String) tokenObj.get("access_token");
			if(isNull(token)) return null;
		}catch(Exception e) {
			logger.info(e.toString());
			return null;
		}
		
		
		dsRsltInfoMap.put("rsltCd", ApimCode.RESULT_SUCCESS_Y);
		logger.info(">> token :: " + token);
		
		return token;
	}
	
	public static boolean isNull(String str) {
		return str==null || str.equals("");
	}
	
}
