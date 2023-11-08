package com.example.camera.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Result {
    private String code;
    private String msg;
    private Object data;

    public static Result success(String msg) {
        return new Result("200", msg, null);
    }

    public static Result success(Object data,String msg){
        return new Result("200",msg,data);
    }
    public static Result success(Object data) {
        return new Result("200", "", data);
    }

    public static Result error() {
        return new Result("500", "系统错误", null);
    }

    /**
     * @param code
     * @param msg
     * @return {@link Result}
     */
    public static Result error(String code, String msg) {
        return new Result(code, msg, null);
    }
}
