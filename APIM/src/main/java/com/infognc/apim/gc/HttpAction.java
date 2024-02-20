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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;

public class HttpAction {

	private String HTTP_URL = "https://api.apne2.pure.cloud";
	
	private HttpAction() {}

	// Multi Thread Singleton 구성을 위한 LazyHolder
	public static HttpAction getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	private static class LazyHolder{
        private static final HttpAction INSTANCE = new HttpAction();
    }

	public void test(String url) {
		this.HTTP_URL = url;
		System.out.println("## HTTP_URL :: " + HTTP_URL + " ||");
	}
	
	public String restTemplateService(UriComponents uriBuilder, String token) {

		// header 세팅
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("authorization", "bearer " + token);
		
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		RestTemplate restTemplate = new RestTemplate();
		
		ResponseEntity<String> result = restTemplate.exchange(
										uriBuilder.toUriString(), 
										HttpMethod.GET, 
										entity, 
										String.class
										);
		
		System.out.println(result.getStatusCodeValue());
		System.out.println(result.getStatusCode().value());
		
		
		return result.getBody();
	}
	
	public String restTemplateService(UriComponents uriBuilder, String token, JSONObject reqBody) {

		// header 세팅
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("authorization", "bearer " + token);
		
		HttpEntity<String> entity = new HttpEntity<String>(reqBody.toString(), headers);
		
		RestTemplate restTemplate = new RestTemplate();
		
		ResponseEntity<String> result = restTemplate.exchange(
										uriBuilder.toUriString(), 
										HttpMethod.POST, 
										entity, 
										String.class
										);
		
		return result.getBody();
	}
	
	
	
	
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
