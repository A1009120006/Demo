package com.mys.example.demo.common;

public enum ResEnum {
    //这里是可以自己定义的，方便与前端交互即可
    SUCCESS(1,"成功"),
    FAIL(0,"失败"),
    UNKNOWN_ERROR(999,"未知错误"),
    ;

    private Integer code;
    private String message;

    ResEnum(Integer code, String message){
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

