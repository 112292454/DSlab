package com.dslab.guide;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableDubbo(scanBasePackages = "com.dslab.guide")
public class GuideApplication {

	public static void main(String[] args) {

		SpringApplication.run(GuideApplication.class, args);
	}

}
