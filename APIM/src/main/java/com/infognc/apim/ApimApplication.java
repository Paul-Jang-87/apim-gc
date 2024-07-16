package com.infognc.apim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Controller
@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
@EnableAsync
@ComponentScan(basePackages = {"com"}, includeFilters = {@Filter(type = FilterType.ANNOTATION, classes = {Component.class, Repository.class, Service.class, Controller.class} )})
public class ApimApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApimApplication.class, args);
	}

}
