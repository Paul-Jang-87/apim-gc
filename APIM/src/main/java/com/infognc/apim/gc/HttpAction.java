package com.infognc.apim.gc;

import java.util.Arrays;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;

@Component
public class HttpAction {
	private static final Logger logger = LoggerFactory.getLogger(HttpAction.class);
	
	private final RestTemplate restTemplate;
	
	public HttpAction(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	/**
	 * 
	 * [GET] G.C API 호출 restTemplate
	 * 
	 * @param uriBuilder
	 * @param token
	 * @return
	 */
	public String restTemplateService(UriComponents uriBuilder, String token) {
		String result = "";
		try {
			// header 세팅
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("authorization", "bearer " + token);
			
			HttpEntity<String> entity = new HttpEntity<String>(headers);
			
			ResponseEntity<String> res = restTemplate.exchange(
											uriBuilder.toUriString(), 
											HttpMethod.GET, 
											entity, 
											String.class
											);
			
			result = res.toString();
			
		}catch(HttpClientErrorException hce) {
			hce.printStackTrace();
			logger.error(hce.toString());
			return null;
		}catch(HttpServerErrorException hse) {
			hse.printStackTrace();
			logger.error(hse.toString());
			return null;
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
			return null;
		}
		
		return result;
	}
	
	/**
	 * 
	 * [POST] G.C API 호출 restTemplate
	 * 
	 * @param uriBuilder
	 * @param token
	 * @param reqBody
	 * @return
	 */
	public String restTemplateService(UriComponents uriBuilder, String token, JSONObject reqBody) {
		String result = "";
		try {
			// header 세팅
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("authorization", "bearer " + token);
			
			HttpEntity<String> entity = new HttpEntity<String>(reqBody.toString(), headers);
			
			ResponseEntity<String> res = restTemplate.exchange(
											uriBuilder.toUriString(), 
											HttpMethod.POST, 
											entity, 
											String.class
											);
			result = res.toString();
			
		}catch(HttpClientErrorException hce) {
			hce.printStackTrace();
			logger.error(hce.toString());
			return null;
		}catch(HttpServerErrorException hse) {
			hse.printStackTrace();
			logger.error(hse.toString());
			return null;
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
			return null;
		}

		return result;
	}
	
	/**
	 * 
	 * APIM REST API 통신
	 * 
	 * @param uriBuilder
	 * @param entity
	 * @param type
	 * @return
	 */
	public String restTemplateService(UriComponents uriBuilder, HttpEntity<String> entity, String type) {
		String result = "";
//		Map<String, Object> resBody = new HashMap<String, Object>();
		
		HttpMethod method = null;
		try {

			if("POST".equals(type))	method = HttpMethod.POST;
			else					method = HttpMethod.GET;
			
//			HttpEntity<String> entity = new HttpEntity<String>(headers);
			
			ResponseEntity<String> res = restTemplate.exchange(
											uriBuilder.toUriString(), 
											method, 
											entity, 
											String.class
											);
			
//			resBody = res.getBody();
			result = res.toString();
			
		}catch(HttpClientErrorException hce) {
			hce.printStackTrace();
			logger.error(hce.toString());
			return null;
		}catch(HttpServerErrorException hse) {
			hse.printStackTrace();
			logger.error(hse.toString());
			return null;
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
			return null;
		}
		
		return result;
	}
	
	
}
