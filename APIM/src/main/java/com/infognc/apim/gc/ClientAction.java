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

import com.infognc.apim.util.Configure;
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

		credentialsAuth();
		
		if("".equals(accessToken) || accessToken == null || accessToken.isEmpty()) {
			accessToken = authResponse.getBody().getAccess_token();
			logger.info(accessToken);
		}
	}
	
	
	public void credentialsAuth() throws IOException, ApiException{

		apiClient = ApiClient.Builder.standard().withBasePath(REGION).build();
		authResponse = apiClient.authorizeClientCredentials(clientId, clientSecret);
		
		// Don't actually do this, this logs your auth token to the console!
		logger.info("## authResponse.getBody() :: " + authResponse.getBody().toString());
		
		// Use the ApiClient instancer
		Configuration.setDefaultApiClient(apiClient);
	}
	
	@Scheduled(fixedDelay=86400*1000)
	public void getAccessToken() throws IOException, ApiException{
		logger.info("## get access token !! ");
		init();
		accessToken = authResponse.getBody().getAccess_token();
		logger.info(accessToken);
	}
	
	
	
	/*
	 * [GET]
	 */
	
	/**
	 * 
	 * @param url
	 */
	public String callApiRestTemplate_GET(String url) {
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(apiUrl + url)
									.build(true);

		System.out.println("## uriBuilder :: " + uriBuilder.toString());
		
		String res = httpAction.restTemplateService(uriBuilder, accessToken);
		
		return res;
	}
	
	/**
	 * PathVariable
	 * 
	 * @param url
	 * @param path
	 */
	public String callApiRestTemplate_GET(String url, String path) {
		
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(apiUrl + url)
									.buildAndExpand(path)
									;
		System.out.println("## uriBuilder :: " + uriBuilder.toString());
		
		String res = httpAction.restTemplateService(uriBuilder, accessToken);
		
		return res;
	}
	
	/**
	 * QueryPatameter
	 * 
	 * @param url
	 * @param params
	 */
	public String callApiRestTemplate_GET(String url, MultiValueMap<String, String> params) {

		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(apiUrl + url)
									.queryParams(params)
									.build(true);
				
		System.out.println("## uriBuilder :: " + uriBuilder.toString());
		
		String res = httpAction.restTemplateService(uriBuilder, accessToken);
		
		return res;
		
	}
	
	/**
	 * PathVariable, QueryPatameter
	 * 
	 * @param url
	 * @param path
	 * @param params
	 */
	public String callApiRestTemplate_GET(String url, String path, MultiValueMap<String, String> params) {
		
		UriComponents uriBuilder = null;
		
		uriBuilder = UriComponentsBuilder.fromUriString(apiUrl + url)
					.queryParams(params)
					.buildAndExpand(path)
					;
		
		System.out.println("## uriBuilder :: " + uriBuilder.toString());
		
		String res = httpAction.restTemplateService(uriBuilder, accessToken);

	
		return res;
	}
	
	
	/*
	 * [POST]
	 */
	
	/**
	 * 
	 * @param url
	 */
	public String callApiRestTemplate_POST(String url, JSONObject reqBody) {
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(apiUrl + url)
									.build(true);

		System.out.println("## uriBuilder :: " + uriBuilder.toString());
		
		String res = httpAction.restTemplateService(uriBuilder, accessToken, reqBody);

		return res;
	}
	
	/**
	 * PathVariable
	 * 
	 * @param url
	 * @param path
	 */
	public String callApiRestTemplate_POST(String url, String path) {
		
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(apiUrl + url)
				.buildAndExpand(path)
				;
		System.out.println("## uriBuilder :: " + uriBuilder.toString());
		
		String res = httpAction.restTemplateService(uriBuilder, accessToken);
		
		return res;
	}
	
	/**
	 * PathVariable
	 * 
	 * @param url
	 * @param path
	 */
	public String callApiRestTemplate_POST(String url, String path, Object reqBody) {
		
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(apiUrl + url)
									.buildAndExpand(path)
									;
		System.out.println("## uriBuilder :: " + uriBuilder.toString());
		
		String res = httpAction.restTemplateService(uriBuilder, accessToken, reqBody);
		
		return res;
	}
	
	/**
	 * QueryPatameter
	 * 
	 * @param url
	 * @param params
	 */
	public String callApiRestTemplate_POST(String url, MultiValueMap<String, String> params, JSONObject reqBody) {

		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(apiUrl + url)
									.queryParams(params)
									.build(true);
				
		System.out.println("## uriBuilder :: " + uriBuilder.toString());
		
		String res = httpAction.restTemplateService(uriBuilder, accessToken, reqBody);
		
		return res;
	}
	
	/**
	 * PathVariable, QueryPatameter
	 * 
	 * @param url
	 * @param path
	 * @param params
	 */
	public String callApiRestTemplate_POST(String url, String path, MultiValueMap<String, String> params, JSONObject reqBody) {
		
		UriComponents uriBuilder = null;
		
		uriBuilder = UriComponentsBuilder.fromUriString(apiUrl + url)
					.queryParams(params)
					.buildAndExpand(path)
					;
		
		System.out.println("## uriBuilder :: " + uriBuilder.toString());
		
		String res = httpAction.restTemplateService(uriBuilder, accessToken, reqBody);
		
		return res;
	}
	
	
}
