package com.infognc.apim.utl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.ResourcePropertySource;

@Configuration
public class ApimConfig {
	private static final Logger logger = LoggerFactory.getLogger(ApimConfig.class);
	private static final String ENV_SYSTEM = System.getProperty("env.system");
	
	@Bean
	public Configure configure() throws Exception {
		String propertiesPath = "classpath:config/prop_" + ENV_SYSTEM + ".properties";
		
		Configure configure = new Configure();
		ResourcePropertySource res = new ResourcePropertySource(propertiesPath);
		String[] properiNames = res.getPropertyNames();
		
		for(String name : properiNames) {
			Configure.put(name, (String) res.getProperty(name));
			logger.info(">> name : {}, value : {}", name, res.getProperty(name));
		}
		
		return configure;
	}
	
	
}
