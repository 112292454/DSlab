package com.dslab.event;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.dslab.event.mapper")
@EnableTransactionManagement
//@EnableDiscoveryClient(autoRegister = true)
public class DslabEventApplication {

    public static void main(String[] args) {
        SpringApplication.run(DslabEventApplication.class, args);
    }

}
