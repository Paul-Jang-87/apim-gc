package com.infognc.apim.gc;

import java.io.IOException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.infognc.apim.utl.Configure;
import com.mypurecloud.sdk.v2.ApiClient;
import com.mypurecloud.sdk.v2.ApiException;
import com.mypurecloud.sdk.v2.ApiResponse;
import com.mypurecloud.sdk.v2.Configuration;
import com.mypurecloud.sdk.v2.PureCloudRegionHosts;
import com.mypurecloud.sdk.v2.extensions.AuthResponse;

@Component
public class ClientAction {
	private static final Logger logger = LoggerFactory.getLogger(ClientAction.class);
	private static final PureCloudRegionHosts REGION = PureCloudRegionHosts.ap_northeast_2;
	private final HttpAction httpAction;
	private ApiClient apiClient = null;
	private ApiResponse<AuthResponse> authResponse = null;
	private String apiUrl = "";
	private String clientId = "";
	private String clientSecret = "";
	private String accessToken = "";
	
//	private final RestTemplate restTemplate;
	
	public ClientAction(HttpAction httpAction) {

//		httpAction = HttpAction.getInstance();
//		this.restTemplate = restTemplate;
		this.httpAction = httpAction;

	}
	
	public void init() throws IOException, ApiException {
		apiUrl			= Configure.get("gc.api.url");
		clientId 		= Configure.get("gc.client.id");
		clientSecret 	= Configure.get("gc.client.secret");
		accessToken		= Configure.get("gc.auth.token");
		
		System.out.println("url : " + apiUrl);
		System.out.println("clientID : " + clientId);
		System.out.println("clientSercret : " + clientSecret);
		
		credentialsAuth();
		
		if("".equals(accessToken) || accessToken == null || accessToken.isEmpty()) {
			System.out.println("### client init() call");
			accessToken = getAccessToken();
			System.out.println(accessToken);
		}
	}
	
	
	public void credentialsAuth() throws IOException, ApiException{

		if(apiClient==null)
			apiClient = ApiClient.Builder.standard().withBasePath(REGION).build();
		
		if(authResponse==null)
			authResponse = apiClient.authorizeClientCredentials(clientId, clientSecret);
		
		// Don't actually do this, this logs your auth token to the console!
		System.out.println(authResponse.getBody().toString());
		logger.info(authResponse.getBody().toString());
		
		// Use the ApiClient instancer
		Configuration.setDefaultApiClient(apiClient);
	}
	
	@Scheduled(fixedDelay=86400*1000, initialDelay=86400*1000)
	public String getAccessToken() {
		System.out.println("## get access token !! ");
		return authResponse.getBody().getAccess_token();
	}
	
	/*
	 * [GET]
	 */
	
	/**
	 * 
	 * @param url
	 */
	public JSONObject callApiRestTemplate_GET(String url) {
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(apiUrl + url)
									.build(true);

		System.out.println("## uriBuilder :: " + uriBuilder.toString());
		
		String res = httpAction.restTemplateService(uriBuilder, accessToken);
//		String res = restTemplateService(uriBuilder, accessToken);
		System.out.println(res);
		
		return new JSONObject(res);
	}
	
	/**
	 * PathVariable
	 * 
	 * @param url
	 * @param path
	 */
	public JSONObject callApiRestTemplate_GET(String url, String path) {
		
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(apiUrl + url)
									.buildAndExpand(path)
									;
		System.out.println("## uriBuilder :: " + uriBuilder.toString());
		System.out.println("## accessToken :: " + accessToken);
		
		String res = httpAction.restTemplateService(uriBuilder, accessToken);
//		String res = restTemplateService(uriBuilder, accessToken);
		
		System.out.println(res);
		
		return new JSONObject(res);
	}
	
	/**
	 * QueryPatameter
	 * 
	 * @param url
	 * @param params
	 */
	public JSONObject callApiRestTemplate_GET(String url, MultiValueMap<String, String> params) {

		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(apiUrl + url)
									.queryParams(params)
									.build(true);
				
		System.out.println("## uriBuilder :: " + uriBuilder.toString());
		
		String res = httpAction.restTemplateService(uriBuilder, accessToken);
//		String res = restTemplateService(uriBuilder, accessToken);
		
		System.out.println(res);
		
		return new JSONObject(res);
		
	}
	
	/**
	 * PathVariable, QueryPatameter
	 * 
	 * @param url
	 * @param path
	 * @param params
	 */
	public JSONObject callApiRestTemplate_GET(String url, String path, MultiValueMap<String, String> params) {
		
		UriComponents uriBuilder = null;
		
		uriBuilder = UriComponentsBuilder.fromUriString(apiUrl + url)
					.queryParams(params)
					.buildAndExpand(path)
					;
		
		System.out.println("## uriBuilder :: " + uriBuilder.toString());
		
		String res = httpAction.restTemplateService(uriBuilder, accessToken);
//		String res = restTemplateService(uriBuilder, accessToken);
		
		System.out.println(res);
		
		return new JSONObject(res);
	}
	
	
	/*
	 * [POST]
	 */
	
	/**
	 * 
	 * @param url
	 */
	public JSONObject callApiRestTemplate_POST(String url, JSONObject reqBody) {
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(apiUrl + url)
									.build(true);

		System.out.println("## uriBuilder :: " + uriBuilder.toString());
		
		String res = httpAction.restTemplateService(uriBuilder, accessToken, reqBody);
//		String res = restTemplateService(uriBuilder, accessToken, reqBody);
		
		System.out.println(res);
		
		return new JSONObject(res);
	}
	
	/**
	 * PathVariable
	 * 
	 * @param url
	 * @param path
	 */
	public JSONObject callApiRestTemplate_POST(String url, String path, JSONObject reqBody) {
		
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(apiUrl + url)
									.buildAndExpand(path)
									;
		System.out.println("## uriBuilder :: " + uriBuilder.toString());
		
		String res = httpAction.restTemplateService(uriBuilder, accessToken, reqBody);
//		String res = restTemplateService(uriBuilder, accessToken, reqBody);
		
		System.out.println(res);
		
		return new JSONObject(res);
	}
	
	/**
	 * QueryPatameter
	 * 
	 * @param url
	 * @param params
	 */
	public JSONObject callApiRestTemplate_POST(String url, MultiValueMap<String, String> params, JSONObject reqBody) {

		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(apiUrl + url)
									.queryParams(params)
									.build(true);
				
		System.out.println("## uriBuilder :: " + uriBuilder.toString());
		
		String res = httpAction.restTemplateService(uriBuilder, accessToken, reqBody);
//		String res = restTemplateService(uriBuilder, accessToken, reqBody);
		
		System.out.println(res);
		
		return new JSONObject(res);
	}
	
	/**
	 * PathVariable, QueryPatameter
	 * 
	 * @param url
	 * @param path
	 * @param params
	 */
	public JSONObject callApiRestTemplate_POST(String url, String path, MultiValueMap<String, String> params, JSONObject reqBody) {
		
		UriComponents uriBuilder = null;
		
		uriBuilder = UriComponentsBuilder.fromUriString(apiUrl + url)
					.queryParams(params)
					.buildAndExpand(path)
					;
		
		System.out.println("## uriBuilder :: " + uriBuilder.toString());
		
		String res = httpAction.restTemplateService(uriBuilder, accessToken, reqBody);
//		String res = restTemplateService(uriBuilder, accessToken, reqBody);
		
		System.out.println(res);
		
		return new JSONObject(res);
	}
	
	
//
//	
//	/**
//	 * 
//	 * [GET] G.C API 호출 restTemplate
//	 * 
//	 * @param uriBuilder
//	 * @param token
//	 * @return
//	 */
//	public String restTemplateService(UriComponents uriBuilder, String token) {
//		String result = "";
//		try {
//			// header 세팅
//			HttpHeaders headers = new HttpHeaders();
//			headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
//			headers.setContentType(MediaType.APPLICATION_JSON);
//			headers.set("authorization", "bearer " + token);
//			
//			HttpEntity<String> entity = new HttpEntity<String>(headers);
//			
//			ResponseEntity<String> res = restTemplate.exchange(
//											uriBuilder.toUriString(), 
//											HttpMethod.GET, 
//											entity, 
//											String.class
//											);
//			
//			result = res.toString();
//			
//		}catch(HttpClientErrorException hce) {
//			hce.printStackTrace();
//			logger.error(hce.toString());
//			return null;
//		}catch(HttpServerErrorException hse) {
//			hse.printStackTrace();
//			logger.error(hse.toString());
//			return null;
//		}catch(Exception e) {
//			e.printStackTrace();
//			logger.error(e.toString());
//			return null;
//		}
//		
//		return result;
//	}
//	
//	/**
//	 * 
//	 * [POST] G.C API 호출 restTemplate
//	 * 
//	 * @param uriBuilder
//	 * @param token
//	 * @param reqBody
//	 * @return
//	 */
//	public String restTemplateService(UriComponents uriBuilder, String token, JSONObject reqBody) {
//		String result = "";
//		try {
//			// header 세팅
//			HttpHeaders headers = new HttpHeaders();
//			headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
//			headers.setContentType(MediaType.APPLICATION_JSON);
//			headers.set("authorization", "bearer " + token);
//			
//			HttpEntity<String> entity = new HttpEntity<String>(reqBody.toString(), headers);
//			
//			ResponseEntity<String> res = restTemplate.exchange(
//											uriBuilder.toUriString(), 
//											HttpMethod.POST, 
//											entity, 
//											String.class
//											);
//			result = res.toString();
//			
//		}catch(HttpClientErrorException hce) {
//			hce.printStackTrace();
//			logger.error(hce.toString());
//			return null;
//		}catch(HttpServerErrorException hse) {
//			hse.printStackTrace();
//			logger.error(hse.toString());
//			return null;
//		}catch(Exception e) {
//			e.printStackTrace();
//			logger.error(e.toString());
//			return null;
//		}
//
//		return result;
//	}
//	
//	
//	public String restTemplateService(UriComponents uriBuilder, HttpHeaders headers, String type) {
//		String result = "";
//		HttpMethod method = null;
//		try {
//
//			if("POST".equals(type))	method = HttpMethod.POST;
//			else					method = HttpMethod.GET;
//			
//			HttpEntity<String> entity = new HttpEntity<String>(headers);
//			
//			ResponseEntity<String> res = restTemplate.exchange(
//											uriBuilder.toUriString(), 
//											method, 
//											entity, 
//											String.class
//											);
//			
//			result = res.toString();
//			
//		}catch(HttpClientErrorException hce) {
//			hce.printStackTrace();
//			logger.error(hce.toString());
//			return null;
//		}catch(HttpServerErrorException hse) {
//			hse.printStackTrace();
//			logger.error(hse.toString());
//			return null;
//		}catch(Exception e) {
//			e.printStackTrace();
//			logger.error(e.toString());
//			return null;
//		}
//		
//		return result;
//	}
//	
	
}
