package com.dslab.server.guide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient(autoRegister = true)
//@DubboComponentScan(basePackages = "com.dslab.server.guide")
public class GuideApplication {

	public static void main(String[] args) {

		SpringApplication.run(GuideApplication.class, args);
	}

}
