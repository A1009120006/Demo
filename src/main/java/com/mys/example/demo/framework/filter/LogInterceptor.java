package com.mys.example.demo.framework.filter;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

// 日志参数拦截器
public class LogInterceptor implements HandlerInterceptor {
    Logger logger = LoggerFactory.getLogger(LogInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestId = request.getHeader("X-Request-ID");
        if (StringUtils.isBlank(requestId)) {
            requestId = UUID.randomUUID().toString().replace("-","");
        }
        MDC.put("requestId", requestId); // Mapped Diagnostic Context, Spring Boot默认集成logback时可用
        logger.info(">>>>" + JSON.toJSONString(request));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.info("<<<<" + JSON.toJSONString(response));
        MDC.remove("requestId"); // 请求结束后清理，避免线程池复用导致的交叉污染
    }
}

