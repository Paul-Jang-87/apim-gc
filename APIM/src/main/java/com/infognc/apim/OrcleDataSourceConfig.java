package com.infognc.apim;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@PropertySource("file:/config/apim.properties")
//@PropertySource("classpath:/config/apim_dev.properties")
@EnableTransactionManagement
@EnableJpaRepositories(
		basePackages = "com.infognc.apim.repositories.oracle", // 참고할 repository
		entityManagerFactoryRef = "oracleEntityManagerFactory", 
		transactionManagerRef = "oracleTransactionManager"
		)
public class OrcleDataSourceConfig {

	@Value("${spring.datasource.oracle.url}")
	private String url;
	
	@Value("${spring.datasource.oracle.username}")
	private String username;
	
	@Value("${spring.datasource.oracle.password}")
	private String password;
	
	@Value("${spring.datasource.oracle.driver-class-name}")
	private String driverClassName;
	
	@Value("${spring.datasource.oracle.maximum-pool-size}")
	private int maximumPoolSize;
	
	@Value("${spring.datasource.oracle.minimum-idle}")
	private int minimumIdle;
	
	@Value("${spring.datasource.oracle.connection-timeout}")
	private int connectionTimeout;
	
	@Value("${spring.datasource.oracle.idle-timeout}")
	private int idleTimeout;
	
	@Value("${spring.datasource.oracle.pool-name}")
	private String poolName;
	
	@Bean
	public DataSource oracleDataSource() {
		HikariConfig hikariConfig = new HikariConfig();
		
		hikariConfig.setUsername(username);
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setPassword(password);
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setMaximumPoolSize(maximumPoolSize);
        hikariConfig.setMinimumIdle(minimumIdle);
        hikariConfig.setConnectionTimeout(connectionTimeout);
        hikariConfig.setIdleTimeout(idleTimeout);
        hikariConfig.setPoolName(poolName);
        
        
		return new HikariDataSource(hikariConfig);
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean oracleEntityManagerFactory(
			EntityManagerFactoryBuilder builder,
			@Qualifier("oracleDataSource") DataSource dataSource) {
		
		return builder.dataSource(dataSource).packages("com.infognc.apim.entities.oracle")// 참고할 엔티티
//				.persistenceUnit("oracle")
				.properties(hibernateProperties()) // Apply Hibernate properties here
				.build();
	}
	
	@Bean
	public PlatformTransactionManager oracleTransactionManager(
			@Qualifier("oracleEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

	private Map<String, Object> hibernateProperties() {// Hibernate 옵션들 설정.
		Map<String, Object> hibernateProperties = new HashMap<>();
		hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.OracleDialect");
		hibernateProperties.put("hibernate.hbm2ddl.auto", "none");
		hibernateProperties.put("hibernate.show_sql", false);	// sql 로그 출력 활성화 
		hibernateProperties.put("hibernate.format_sql", false);	// sql 단일 라인으로 출력
		return hibernateProperties;
	}

}
