package com.mys.example.demo.framework.common;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class ResUtil {

    /**成功但不带数据**/
    public static BaseRes<Object> success(){
        return success(null);
    }
    /**成功且带数据**/
    public static BaseRes<Object> success(Object object){

        String requestId = getRequestHeaderValue("requestId");

        BaseRes<Object> res = new BaseRes<>();
        res.setRequestId(requestId);
        res.setCode(ResEnum.SUCCESS.getCode());
        res.setMessage(ResEnum.SUCCESS.getMessage());
        res.setData(object);
        return res;
    }
    /**失败**/
    public static BaseRes<Object> error(){
        return error(ResEnum.UNKNOWN_ERROR);
    }
    /**失败**/
    public static BaseRes<Object> error(ResEnum resEnum){
        BaseRes<Object> result = new BaseRes<>(resEnum);
        String requestId = getRequestHeaderValue("requestId");
        result.setRequestId(requestId);
        return result;
    }
    /**失败**/
    public static BaseRes<Object> error(Integer code, String message){
        BaseRes<Object> result = new BaseRes<>(code, message);
        String requestId = getRequestHeaderValue("requestId");
        result.setRequestId(requestId);
        return result;
    }
    /**
     * 获取指定key的请求头的值
     */
    private static String getRequestHeaderValue(String headerKey) {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();

        // 获取请求体 request
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 返回请求头 headerValue
        return request.getHeader(headerKey);
    }
}

