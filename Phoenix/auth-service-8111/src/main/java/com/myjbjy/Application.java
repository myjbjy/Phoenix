package com.myjbjy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author myj
 */
@EnableAsync
@EnableRetry
@SpringBootApplication
@EnableDiscoveryClient  // 开启服务的注册与发现功能
@MapperScan(basePackages = "com.myjbjy.mapper")
@EnableFeignClients("com.myjbjy.api.feign")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
