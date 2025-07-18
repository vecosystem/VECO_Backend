package com.example.Veco.global.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> requestTemplate.header(
                "Content-Type",
                "application/x-www-form-urlencoded; charset=UTF-8"
        );
    }
}
