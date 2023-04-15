package com.dslab.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
//@EnableDiscoveryClient(autoRegister = true)
public class DslabEventApplication {

    public static void main(String[] args) {
        SpringApplication.run(DslabEventApplication.class, args);
    }

}
