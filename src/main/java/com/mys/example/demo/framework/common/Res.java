package com.mys.example.demo.framework.common;

import com.mys.example.demo.framework.exception.BussException;
import lombok.Data;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Data
public class Res<T> {
    public Res() {
        super();
    }

    private String requestId;

    private String message;

    private Integer code;

    private T data;

    public Res(CodeEnum codeEnum) {
        this(codeEnum.getCode(), codeEnum.getMessage());
    }
    public Res(Integer code, String message) {
        this(code, message, (T)null);
    }

    public Res(Integer code, String message, T data) {
        this((String)null, code, message, data);
    }

    public Res(String requestId, Integer code, String message, T data) {
        this.requestId = requestId;
        this.code = code;
        this.message = message;
        this.data = data;
    }


    /**成功但不带数据**/
    public static <T> Res<T> success(){
        return success(null);
    }
    /**成功且带数据**/
    public static <T> Res<T> success(T object){

        String requestId = getRequestHeaderValue("requestId");

        Res<T> res = new Res<>();
        res.setRequestId(requestId);
        res.setCode(CodeEnum.SUCCESS.getCode());
        res.setMessage(CodeEnum.SUCCESS.getMessage());
        res.setData(object);
        return res;
    }
    public static <T> Res<T> exception(BussException exception){
        Res<T> result = new Res<>(exception.getCode(), exception.getMessage());
        String requestId = getRequestHeaderValue("requestId");
        result.setRequestId(requestId);
        return result;
    }
    /**失败**/
    public static <T> Res<T> error(){
        return error(CodeEnum.UNKNOWN_ERROR);
    }
    /**失败**/
    public static <T> Res<T> error(CodeEnum codeEnum){
        Res<T> result = new Res<>(codeEnum);
        String requestId = getRequestHeaderValue("requestId");
        result.setRequestId(requestId);
        return result;
    }
    /**失败**/
    public static <T> Res<T> error(Integer code, String message){
        Res<T> result = new Res<>(code, message);
        String requestId = getRequestHeaderValue("requestId");
        result.setRequestId(requestId);
        return result;
    }
    public static <T> Res<T> error(String message){
        Res<T> result = new Res<>(CodeEnum.DEFAULT_ERROR.getCode(), message);
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

