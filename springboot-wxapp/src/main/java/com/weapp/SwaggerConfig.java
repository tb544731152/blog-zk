package com.weapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	@Value("${server.context-path}")
	private String pathMapping;
	@Value("${server.port}")
	private String port;

	@Bean
	public Docket createRestApi() {
		System.out.println("http://localhost:"+port+ pathMapping + "/swagger-ui.html");
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("test")
				.genericModelSubstitutes(ResponseEntity.class)
				.useDefaultResponseMessages(true)
				.forCodeGeneration(false)
				.pathMapping(pathMapping)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.weapp.controller"))
				.paths(PathSelectors.any())
				.build();
	}
}
