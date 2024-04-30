package com.mys.example.demo.framework.exception;
import com.mys.example.demo.framework.common.BaseRes;
import com.mys.example.demo.framework.common.ResUtil;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.Map;
@ControllerAdvice
public class GlobalExceptionResolver {
// 返回错误json数据
    @ResponseBody
    @ExceptionHandler
    public BaseRes<Object> handler(Exception e){
        BaseRes<Object> res = null;
        if (e instanceof BussException){
            BussException bussException = (BussException) e;
            res = ResUtil.error(bussException.getCode(), bussException.getMessage());
        }else {
            res = ResUtil.error();
        }
        return res;
    }
}