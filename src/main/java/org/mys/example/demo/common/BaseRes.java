package org.mys.example.demo.common;

import lombok.Data;

@Data
public class BaseRes<T> {
    public BaseRes() {
        super();
    }

    private String requestId;

    private String message;

    private Integer code;

    private T data;

    public BaseRes(ResEnum resEnum) {
        this(resEnum.getCode(), resEnum.getMessage());
    }
    public BaseRes(Integer code, String message) {
        this(code, message, (T)null);
    }

    public BaseRes(Integer code, String message, T data) {
        this((String)null, code, message, data);
    }

    public BaseRes(String requestId, Integer code, String message, T data) {
        this.requestId = requestId;
        this.code = code;
        this.message = message;
        this.data = data;
    }

}

