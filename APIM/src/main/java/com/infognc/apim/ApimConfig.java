package com.infognc.apim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.ResourcePropertySource;

import com.infognc.apim.util.Configure;

@Configuration
public class ApimConfig {
	private static final Logger logger = LoggerFactory.getLogger(ApimConfig.class);
	
	@Bean
	public Configure configure() throws Exception {
		String propertiesPath = "";
		
		// 쿠버네티스 configMap으로 생성한 properties 파일부터 탐색 없으면 내부 /config
		propertiesPath = "file:/config/apim.properties";
		
		System.out.println("## propertiesPath :: " + propertiesPath);
		logger.info(">> propertiesPath :: " + propertiesPath);
		
		Configure configure = new Configure();
		
		try {
			ResourcePropertySource res = new ResourcePropertySource(propertiesPath);
			String[] properiNames = res.getPropertyNames();
			
			for(String name : properiNames) {
				Configure.put(name, (String) res.getProperty(name));
//				logger.info(">> name : {}, value : {}", name, res.getProperty(name));
			}

		}catch(Exception e) {
			// configMap으로 생성된 properties 파일 제대로 못찾으면 내부 properties로 
			propertiesPath = "classpath:/config/apim_prd.properties";
			ResourcePropertySource res = new ResourcePropertySource(propertiesPath);
			String[] properiNames = res.getPropertyNames();
			
			for(String name : properiNames) {
				Configure.put(name, (String) res.getProperty(name));
//				logger.info(">> name : {}, value : {}", name, res.getProperty(name));
			}
		}

		
		return configure;
	}
	
}
