package com.infognc.apim;

import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
	private static final Logger logger = LoggerFactory.getLogger(RestTemplateConfig.class);
	private final int READ_TIMEOUT 			= 20000;
	private final int CONNECT_TIMEOUT		= 10000;
	  
	@Bean
	public RestTemplate restTemplate() {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		CloseableHttpClient httpClient;
		
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
	    	
	    	
	    	/*
	    	httpClient = HttpClients.custom().setConnectionManager(cm)
	                						.evictExpiredConnections()
	                						.build();
	    	*/
	    }catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
	    	logger.error(e.getMessage());
	    	return new RestTemplate();
	    }
	    
	    
	    factory.setConnectTimeout(CONNECT_TIMEOUT);
	    factory.setHttpClient(httpClient);

	    
	    
	    RestTemplate restTemplate = new RestTemplate(factory); 
	    restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
	    return restTemplate;
	}
	
	
	
	
}
