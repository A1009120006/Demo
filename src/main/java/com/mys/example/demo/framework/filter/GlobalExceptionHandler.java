package com.mys.example.demo.framework.filter;

import com.mys.example.demo.framework.common.Res;
import com.mys.example.demo.framework.exception.BussException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
 
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    Logger logger = LoggerFactory.getLogger(LogInterceptor.class);

    /**
     * 处理自定义异常
     *
     */
    @ExceptionHandler(value = BussException.class)
    @ResponseBody
    public Res<Object> bizExceptionHandler(BussException e) {
        logger.error("======", e);
        return Res.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理其他异常
     *
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Res<Object> exceptionHandler(Exception e) {
        logger.error("======", e);
        return Res.error();
    }
}