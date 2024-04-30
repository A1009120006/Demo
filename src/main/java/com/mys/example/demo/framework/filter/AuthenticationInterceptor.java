package com.mys.example.demo.framework.filter;

import org.slf4j.MDC;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Permission;

// 日志参数拦截器
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法直接通过
        if(!(handler instanceof HandlerMethod)){
            return true;
        }
        // 这里写上鉴权逻辑，例如检查Token
        String token = request.getHeader("Authorization");
        boolean isTokenValid = validateToken(token);

        if (!isTokenValid) {
            // 如果鉴权失败，返回错误信息
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 如果鉴权成功，继续请求处理
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

    public boolean validateToken(String token) {

        return true;
    }
}

