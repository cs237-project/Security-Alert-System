package com.securityalertsystem.common;

import com.securityalertsystem.entity.AlertMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response<T> {

    private int code;
    private String message;
    private T data;

    private Response(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    private Response(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private Response(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public static<T> Response<T> createByErrorMessage(String message){
        return new Response<T>(ErrorCode.EXCEPTION,message);
    }
    public static<T> Response<T> createBySuccess(String message,T data){
        return new Response<T>(ErrorCode.SUCCESS,message,data);
    }
    public static<T> Response<T> createBySuccessMessage(String message){
        return new Response<T>(ErrorCode.SUCCESS,message);
    }
}
