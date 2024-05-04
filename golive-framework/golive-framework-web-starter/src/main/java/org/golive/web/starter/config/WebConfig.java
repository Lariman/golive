package org.golive.web.starter.config;

import org.golive.web.starter.context.GoliveUserInfoInterceptor;
import org.golive.web.starter.context.RequestLimitInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public GoliveUserInfoInterceptor qiyuUserInfoInterceptor() {
        return new GoliveUserInfoInterceptor();
    }

    @Bean
    public RequestLimitInterceptor requestLimitInterceptor(){
        return new RequestLimitInterceptor();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(qiyuUserInfoInterceptor()).addPathPatterns("/**").excludePathPatterns("/error");
        registry.addInterceptor(requestLimitInterceptor()).addPathPatterns("/**").excludePathPatterns("/error");

    }

}
