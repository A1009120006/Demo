package com.mys.example.demo.framework.exception;
import com.mys.example.demo.framework.common.Res;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionResolver {
// 返回错误json数据
    @ResponseBody
    @ExceptionHandler
    public Res<Object> handler(Exception e){
        Res<Object> res = null;
        if (e instanceof BussException){
            BussException bussException = (BussException) e;
            res = Res.error(bussException.getCode(), bussException.getMessage());
        }else {
            res = Res.error();
        }
        return res;
    }
}