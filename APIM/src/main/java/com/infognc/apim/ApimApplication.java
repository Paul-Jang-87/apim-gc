package com.infognc.apim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@ComponentScan("com.infognc.apim.*")
public class ApimApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApimApplication.class, args);
	}

}
