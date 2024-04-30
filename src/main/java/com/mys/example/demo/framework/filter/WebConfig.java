package com.mys.example.demo.framework.filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 配置日志参数拦截器
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new LogInterceptor());
        registry.addInterceptor(new AuthenticationInterceptor())
                .addPathPatterns("/**") // 对所有路径应用鉴权拦截器
                .excludePathPatterns("/login"); // 排除登录接口
    }
}