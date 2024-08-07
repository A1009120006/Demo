package com.mys.example.demo.framework.common;

public enum CodeEnum {
    //这里是可以自己定义的，方便与前端交互即可
    SUCCESS(1,"成功"),
    FAIL(0,"失败"),
    DEFAULT_ERROR(900,"未知错误"),
    UNKNOWN_ERROR(999,"未知错误"),
    ;

    private Integer code;
    private String message;

    CodeEnum(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    public Integer getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }
}

