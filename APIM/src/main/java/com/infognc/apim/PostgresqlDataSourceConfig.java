package com.infognc.apim;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
@EnableJpaRepositories(basePackages = "com.infognc.apim.repositories", // 참고할 repository
		entityManagerFactoryRef = "postgresqlEntityManagerFactory", transactionManagerRef = "postgresqlTransactionManager")
public class PostgresqlDataSourceConfig {


	@Value("${spring.datasource.url}")
	private String url;
	
	@Value("${spring.datasource.username}")
	private String username;
	
	@Value("${spring.datasource.password}")
	private String password;
	
	@Value("${spring.datasource.driver-class-name}")
	private String driverClassName;
	
	@Value("${spring.datasource.maximum-pool-size}")
	private int maximumPoolSize;
	
	@Value("${spring.datasource.minimum-idle}")
	private int minimumIdle;
	
	@Value("${spring.datasource.connection-timeout}")
	private int connectionTimeout;
	
	@Value("${spring.datasource.idle-timeout}")
	private int idleTimeout;
	
	@Value("${spring.datasource.pool-name}")
	private String poolName;
	
	
	@Bean
	@Primary
	public DataSource dataSource() {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl(url);
		hikariConfig.setUsername(username);
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setMaximumPoolSize(maximumPoolSize);
        hikariConfig.setMinimumIdle(minimumIdle);
        hikariConfig.setConnectionTimeout(connectionTimeout);
        hikariConfig.setIdleTimeout(idleTimeout);
        hikariConfig.setPoolName(poolName);
        
		return new HikariDataSource(hikariConfig);
	}

	
/*	
	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource postgresqlDataSource() {// DB커넥션을 위한 정보들. application.properties에서 확인할 수 있다.
		return DataSourceBuilder.create().build();
	}
*/	
	
	@Bean
	@Primary
	public LocalContainerEntityManagerFactoryBean postgresqlEntityManagerFactory(EntityManagerFactoryBuilder builder,
			DataSource dataSource) {
		return builder.dataSource(dataSource).packages("com.infognc.apim.entities")// 참고할 엔티티
				.properties(hibernateProperties()) // Apply Hibernate properties here
				.build();
	}
	
	@Bean
	@Primary
	public PlatformTransactionManager postgresqlTransactionManager(EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

	private Map<String, Object> hibernateProperties() {// Hibernate 옵션들 설정.
		Map<String, Object> hibernateProperties = new HashMap<>();
		hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		hibernateProperties.put("hibernate.hbm2ddl.auto", "none");
		hibernateProperties.put("hibernate.show_sql", true);
		hibernateProperties.put("hibernate.format_sql", true);
		return hibernateProperties;
	}

}
