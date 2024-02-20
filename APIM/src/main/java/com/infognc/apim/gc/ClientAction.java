package com.infognc.apim.gc;

import java.io.IOException;

import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
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

public class ClientAction {
	private static final PureCloudRegionHosts REGION = PureCloudRegionHosts.ap_northeast_2;
	private static HttpAction httpAction = HttpAction.getInstance();
	private ApiClient apiClient = null;
	private ApiResponse<AuthResponse> authResponse = null;
	private String clientId = "";
	private String clientSecret = "";
	private String accessToken = "";
	
	
	
	public ClientAction() throws IOException, ApiException{
		this.clientId 		= Configure.get("gc.client.id");
		this.clientSecret 	= Configure.get("gc.client.secret");
		this.accessToken	= Configure.get("gc.auth.token");
		credentialsAuth();
		
		if("".equals(accessToken) || accessToken == null || accessToken.isEmpty()) {
			accessToken = getAccessToken();
		}
	}
	
	public void credentialsAuth() throws IOException, ApiException{

		apiClient = ApiClient.Builder.standard().withBasePath(REGION).build();
		authResponse = apiClient.authorizeClientCredentials(clientId, clientSecret);

		// Don't actually do this, this logs your auth token to the console!
		System.out.println(authResponse.getBody().toString());
		
		// Use the ApiClient instancer
		Configuration.setDefaultApiClient(apiClient);
	}
	
	
	@Scheduled(fixedDelay=86400*1000)
	public String getAccessToken() {
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
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(url)
									.build(true);

		System.out.println("## uriBuilder :: " + uriBuilder.toString());
		
		String res = httpAction.restTemplateService(uriBuilder, accessToken);
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
		
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(url)
									.buildAndExpand(path)
									;
		System.out.println("## uriBuilder :: " + uriBuilder.toString());
		
		String res = httpAction.restTemplateService(uriBuilder, accessToken);
		
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

		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(url)
									.queryParams(params)
									.build(true);
				
		System.out.println("## uriBuilder :: " + uriBuilder.toString());
		
		String res = httpAction.restTemplateService(uriBuilder, accessToken);
		
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
		
		uriBuilder = UriComponentsBuilder.fromUriString(url)
					.queryParams(params)
					.buildAndExpand(path)
					;
		
		System.out.println("## uriBuilder :: " + uriBuilder.toString());
		
		String res = httpAction.restTemplateService(uriBuilder, accessToken);
		
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
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(url)
									.build(true);

		System.out.println("## uriBuilder :: " + uriBuilder.toString());
		
		String res = httpAction.restTemplateService(uriBuilder, accessToken, reqBody);
		
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
		
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(url)
									.buildAndExpand(path)
									;
		System.out.println("## uriBuilder :: " + uriBuilder.toString());
		
		String res = httpAction.restTemplateService(uriBuilder, accessToken, reqBody);
		
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

		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(url)
									.queryParams(params)
									.build(true);
				
		System.out.println("## uriBuilder :: " + uriBuilder.toString());
		
		String res = httpAction.restTemplateService(uriBuilder, accessToken, reqBody);
		
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
		
		uriBuilder = UriComponentsBuilder.fromUriString(url)
					.queryParams(params)
					.buildAndExpand(path)
					;
		
		System.out.println("## uriBuilder :: " + uriBuilder.toString());
		
		String res = httpAction.restTemplateService(uriBuilder, accessToken, reqBody);
		
		System.out.println(res);
		
		return new JSONObject(res);
	}
}
