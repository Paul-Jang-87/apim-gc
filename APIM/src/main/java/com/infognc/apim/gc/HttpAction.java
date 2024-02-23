package com.infognc.apim.gc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;

@Component
public class HttpAction {
	private static final Logger logger = LoggerFactory.getLogger(HttpAction.class);
	
	private final RestTemplate restTemplate;
	
	@Autowired
	public HttpAction(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		System.out.println(">>>> HTTP Action :: ");
	}

	/*
	// Multi Thread Singleton 구성을 위한 LazyHolder
	public static HttpAction getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	private static class LazyHolder{
        private static final HttpAction INSTANCE = new HttpAction();
    }
	*/
	
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
	
	
	public String restTemplateService(UriComponents uriBuilder, HttpHeaders headers, String type) {
		String result = "";
		HttpMethod method = null;
		try {

			if("POST".equals(type))	method = HttpMethod.POST;
			else					method = HttpMethod.GET;
			
			HttpEntity<String> entity = new HttpEntity<String>(headers);
			
			ResponseEntity<String> res = restTemplate.exchange(
											uriBuilder.toUriString(), 
											method, 
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
	
	

	
	
//==========================[ OLD CODE ] =====================================================	
	
	
/*	
	
	/**
	 * REST API 통신 (HTTP URL Connection 사용)
	 * 
	 * @param url - REST API 요청 URL
	 * @param jsonMsg - request body messege (JSON)
	 * @param token - Genesys Cloud API OAuth 2.0 access token
	 * @param action - RESTful Http Action (GET, POST, PUT, DELETE, PATCH...)
	 * 
	 * 
	 */
	public JSONObject requestRestAPI(String url, String jsonMsg, String token, String action) {
		try {
            
            URL http_url = new URL(url);
            
            HttpURLConnection con = (HttpURLConnection) http_url.openConnection();
            con.setConnectTimeout(5000); //서버에 연결되는 Timeout 시간 설정
            con.setReadTimeout(5000); // InputStream 읽어 오는 Timeout 시간 설정
            con.setRequestMethod(action); //어떤 요청으로 보낼 것인지?

            //json으로 message를 전달하고자 할 때
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-type", "application/json");
            //con.setRequestProperty("authorization", "bearer " + token);
            
            if("POST".equals(action)) {
                con.setDoInput(true);
                con.setDoOutput(true); //POST 데이터를 OutputStream으로 넘겨 주겠다는 설정
                con.setUseCaches(false);
                con.setDefaultUseCaches(false);

                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(jsonMsg); //json 형식의 message 전달
                wr.flush();
            }
            
            System.out.println("## con.getResponseCode() :: " + con.getResponseCode());
            StringBuilder sb = new StringBuilder();
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                JSONObject responseData=new JSONObject(sb.toString());
                System.out.println("" + sb.toString());
                return responseData;
            } else {
                System.out.println(con.getResponseMessage());
            }
        } catch (Exception e){
        	e.printStackTrace();
            //System.err.println(e.toString());
        }
        return null;
	}
	
	public static String getURLQuery(String[] items) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder(); 
		
		for(int i=0; i<items.length/2; i++) {
			if(i!=0) sb.append("&");
			sb.append(URLEncoder.encode(items[i*2], "UTF-8"));
			sb.append("=");
			sb.append(URLEncoder.encode(items[i*2+1], "UTF-8"));
		}
		
		return sb.toString();
		
	}

	
}
