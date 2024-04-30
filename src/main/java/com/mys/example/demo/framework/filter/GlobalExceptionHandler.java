package com.mys.example.demo.framework.filter;

import com.mys.example.demo.framework.common.BaseRes;
import com.mys.example.demo.framework.common.ResUtil;
import com.mys.example.demo.framework.exception.BussException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
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
    public BaseRes<Object> bizExceptionHandler(BussException e) {
        logger.error("======", e);
        return ResUtil.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理其他异常
     *
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public BaseRes<Object> exceptionHandler( Exception e) {
        logger.error("======", e);
        return ResUtil.error();
    }
}