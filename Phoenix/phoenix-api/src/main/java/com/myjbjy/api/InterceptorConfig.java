package com.myjbjy.api;

import com.myjbjy.api.intercept.JWTCurrentUserInterceptor;
import com.myjbjy.api.intercept.SMSInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author myj
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    /**
     * 在springboot容器中放入拦截器
     * @return
     */
    @Bean
    public SMSInterceptor smsInterceptor(){
        return new SMSInterceptor();
    }

    @Bean
    public JWTCurrentUserInterceptor jwtCurrentUserInterceptor(){
        return new JWTCurrentUserInterceptor();
    }

    /**
     * 注册拦截器，并且拦截指定的路由，否则不生效
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(smsInterceptor())
                .addPathPatterns("/passport/getSMSCode");

        registry.addInterceptor(jwtCurrentUserInterceptor())
                .addPathPatterns("/**");
    }
}
