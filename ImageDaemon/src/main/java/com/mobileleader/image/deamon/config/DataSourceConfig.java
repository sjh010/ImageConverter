package com.mobileleader.image.deamon.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//@Configuration
public class DataSourceConfig {
//	
//	@Value("${spring.datasource.driver-class-name}")
//	private String driverClassName;
//	
//	@Value("${spring.datasource.url}")
//	private String url;
//	
//	@Value("${spring.datasource.username}")
//	private String username;
//	
//	@Value("${spring.datasource.password}")
//	private String password;
//	
//	@Bean(name = "dataSource")
//	public DataSource getDataSource() {
//		
//		DriverManagerDataSource dataSource = new DriverManagerDataSource();
//		
//		dataSource.setDriverClassName(driverClassName);
//		dataSource.setUrl(url);
//		dataSource.setUsername(username);
//		dataSource.setPassword(password);
//
//		return dataSource;
//	}
//	
//	@Bean(name = "sqlSessionFactory")
//	public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) throws Exception {
//		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
//		
//		sqlSessionFactory.setDataSource(dataSource);
//		
//		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//		
//		sqlSessionFactory.setMapperLocations(
//				resolver.getResources("classpath*:com/mobileleader/image/data/mapper/*.xml"));
//		
//		return sqlSessionFactory;
//	}
//	
//	@Bean(name = "sqlSessionTemplate", destroyMethod = "clearCache")
//	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
//		return new SqlSessionTemplate(sqlSessionFactory);
//	}
//	
//	@Bean(name = "transactionManager")
//	public PlatformTransactionManager transactionManager(DataSource dataSource) {
//		return new DataSourceTransactionManager(dataSource);
//	}
	
}