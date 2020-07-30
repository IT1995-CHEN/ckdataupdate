package com.ck.dataupdate;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Slf4j
@EnableCaching
@EnableSwagger2
@MapperScan(basePackages = {"com.ck.dataupdate.mapper"})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableScheduling
@ServletComponentScan
public class CkdataupdateApplication {

  public static void main(String[] args) {
    SpringApplication.run(CkdataupdateApplication.class, args);
  }

}
