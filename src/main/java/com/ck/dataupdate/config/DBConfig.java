package com.ck.dataupdate.config;

import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author ChenLei
 */
@Configuration

public class DBConfig {

  /**
   * Title: dataSource1 Description: TODO
   *
   * @return DataSource remark : prefix 为 application.properteis中对应属性的前缀
   */
  @Bean(name = "db1")
  @Primary
  @ConfigurationProperties(prefix = "spring.datasource")
  public DataSource dataSource1() {

    return DataSourceBuilder.create().build();
  }

//	@Bean(name = "db2")
//
//	@ConfigurationProperties(prefix = "spring.datasource.druid")
//	public DataSource dataSource2() {
//		return DataSourceBuilder.create().build();
//	}

}
