package com.ck.dataupdate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author chenlei
 * @version V1.0
 * @Title: SwaggerConfig.java
 * @Description: Swagger配置
 *
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

  String basePackge = "com.ck.dataupdate.controller";

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2).select()
        .apis(RequestHandlerSelectors.basePackage(basePackge))
        .paths(PathSelectors.any()).build().apiInfo(apiInfo());
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder().title("ck数据表结构和数据更新--RESTful 接口").description("ck数据表结构和数据更新")
        .version("1.0.0")
        .termsOfServiceUrl("").license("").licenseUrl("").build();
  }

}
