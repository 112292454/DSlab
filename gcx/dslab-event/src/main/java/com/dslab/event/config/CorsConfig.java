package com.dslab.event.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @program: DSlab
 * @description: 跨域访问配置
 * @author: 郭晨旭
 * @create: 2023-05-26 14:55
 * @version: 1.0
 **/
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/*")
                .allowedHeaders("*")
                .allowedMethods("*")
                .maxAge(1800)
                .allowedOrigins("null");
        WebMvcConfigurer.super.addCorsMappings(registry);
    }
}
