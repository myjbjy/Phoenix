package com.myjbjy;

import io.seata.spring.boot.autoconfigure.SeataAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author myj
 */
@SpringBootApplication(exclude = {
            DataSourceAutoConfiguration.class,
            SeataAutoConfiguration.class
})
@EnableDiscoveryClient  // 开启注册中心的服务注册和发现功能
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
