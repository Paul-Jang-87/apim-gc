package com.infognc.apim;

import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
	private static final Logger logger = LoggerFactory.getLogger(RestTemplateConfig.class);
	private final int READ_TIMEOUT 			= 20000;
	private final int CONNECT_TIMEOUT		= 10000;
	
    @Bean
    RestTemplate restTemplate() {
    	System.out.println("### RestTemplateConfig >>>>>>>>>>>>>>>>>>");
        return new RestTemplate();
    }
    
  /*  
	@Bean
	public RestTemplate restTemplate() {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
//		BufferingClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
		CloseableHttpClient httpClient;
		
		System.out.println(">>>>>>> 여기?");
		
		// SSL setting
	    try {
	    	SSLContext sslcontext = SSLContext.getInstance("TLS");
	    				sslcontext = SSLContexts.custom()
	    											.loadTrustMaterial(null, new TrustAllStrategy())
	    											.build();
	    	
	    	SSLConnectionSocketFactory sslSocketFactory = 
	    			SSLConnectionSocketFactoryBuilder.create()
	    											.setSslContext(sslcontext)
	    											.build();
	    	HttpClientConnectionManager cm = 
	    			PoolingHttpClientConnectionManagerBuilder.create()
						                			.setSSLSocketFactory(sslSocketFactory)
						                			.setMaxConnPerRoute(100)
						                			.setMaxConnTotal(300)
						                			.build();
	    	
	    	httpClient = HttpClientBuilder.create()
	    								.setConnectionManager(cm)
//	    								.setDefaultRequestConfig(reqConfig)
	    								.evictExpiredConnections()
	    								.build();
	    	
	    	
	    	httpClient = HttpClients.custom().setConnectionManager(cm)
	                						.evictExpiredConnections()
	                						.build();
	    }catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
	    	logger.error(e.getMessage());
	    	e.printStackTrace();
	    	return new RestTemplate();
	    }
	    
	    
	    factory.setConnectTimeout(CONNECT_TIMEOUT);
	    factory.setConnectionRequestTimeout(READ_TIMEOUT);
	    factory.setHttpClient(httpClient);
	    
	    System.out.println("@@@@@@@@@@@@@@   RestTemplate Config Set        @@@@@@@@@@@@@");
	    
//	    RestTemplate restTemplate = new RestTemplate(factory); 
	    
	    return new RestTemplate(factory);
	    		
	}
	
*/


	
	
}
