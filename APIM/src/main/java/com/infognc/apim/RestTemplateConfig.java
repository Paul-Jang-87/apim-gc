package com.infognc.apim;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Configuration
public class RestTemplateConfig {
	private static final Logger logger = LoggerFactory.getLogger(RestTemplateConfig.class);
//	private final int READ_TIMEOUT 			= 20000;
//	private final int CONNECT_TIMEOUT		= 10000;
	
    @Bean
    public RestTemplate restTemplate() {
    	logger.info("### RestTemplateConfig >>>>>>>>>>>>>>>>>>");
    	
    	// SSL 세팅을 위해 Factory 사용
    	SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory() {
    		
    		@Override
    		protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
    			if (connection instanceof HttpsURLConnection) {
    	            ((HttpsURLConnection) connection).setHostnameVerifier((hostname, session) -> true); // 호스트 검증을 항상 pass하고
    	            SSLContext sc;
    	            try {
    	                sc = SSLContext.getInstance("TLSv1.3"); // SSLContext를 생성하여
    	                sc.init(null, new TrustManager[] { new X509TrustManager() { // 공개키 암호화 설정을 무력화시킨다.

    	                    @Override
    	                    public X509Certificate[] getAcceptedIssuers() {
    	                        return null;
    	                    }

    	                    @Override
    	                    public void checkServerTrusted(X509Certificate[] chain, String authType)
    	                            throws CertificateException {

    	                    }

    	                    @Override
    	                    public void checkClientTrusted(X509Certificate[] chain, String authType)
    	                            throws CertificateException {

    	                    }
    	                } }, new SecureRandom());
    	                ((HttpsURLConnection) connection).setSSLSocketFactory(sc.getSocketFactory());
    	            } catch (NoSuchAlgorithmException e) {
    	            	logger.error("Exception 발생 : {}", e.getMessage(), e);
    	            } catch (KeyManagementException e) {
    	            	logger.error("Exception 발생 : {}", e.getMessage(), e);
    	            }
    	        }
    			super.prepareConnection(connection, httpMethod);
    		}
    	};
    	
//    	return new RestTemplate(factory);
    	return new RestTemplateBuilder()
    			.setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(20))
                .additionalInterceptors(clientHttpRequestInterceptor(), new LoggingInterceptor())	// 재시도 설정/요청응답 로깅을 위한 인터셉터 설정
                .requestFactory(()-> new BufferingClientHttpRequestFactory(factory))
//                .additionalMessageConverters(new MappingJackson2HttpMessageConverter()) // JSON 변환기 추가
//                .additionalMessageConverters(new StringHttpMessageConverter(Charset.defaultCharset())) // 문자열 변환기 추가
//                .additionalMessageConverters(new FormHttpMessageConverter()) // application/x-www-form-urlencoded 데이터 처리
                .build();
                
    }
    
    
    public ClientHttpRequestInterceptor clientHttpRequestInterceptor() {
        return (request, body, execution) -> {
            RetryTemplate retryTemplate = new RetryTemplate();
            retryTemplate.setRetryPolicy(new SimpleRetryPolicy(3));	// retry 시도 횟수
            try {
                return retryTemplate.execute(context -> execution.execute(request, body));
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        };
    }
    
    static class LoggingInterceptor implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(HttpRequest req, byte[] body, ClientHttpRequestExecution ex) throws IOException {
            final String sessionNumber = makeSessionNumber();
            printRequest(sessionNumber, req, body);
            ClientHttpResponse response = ex.execute(req, body);
            printResponse(sessionNumber, response);
            return response;
        }

        private String makeSessionNumber() {
            return Integer.toString((int) (Math.random() * 1000000));
        }

        private void printRequest(final String sessionNumber, final HttpRequest req, final byte[] body) {
            logger.info("[{}] URI: {}, Method: {}, Headers:{}, Body:{} ",
                    sessionNumber, req.getURI(), req.getMethod(), req.getHeaders(), new String(body, StandardCharsets.UTF_8));
        }

        private void printResponse(final String sessionNumber, final ClientHttpResponse res) throws IOException {
            String body = new BufferedReader(new InputStreamReader(res.getBody(), StandardCharsets.UTF_8)).lines()
                    .collect(Collectors.joining("\n"));

            logger.info("[{}] Status: {}, Headers:{}, Body:{} ",
                    sessionNumber, res.getStatusCode(), res.getHeaders(), body);
        }
    }

	
}
