package org.seed.commons;


import java.io.Serializable;

/**
 * Controller返回结果集
 *
 * @param <T>
 */
public class Result<T> implements Serializable {

    private int code;

    private String message;

    private T data;

    public Result(){}

    public static Result ok() {
        Result rst = new Result();
        rst.setCode(0);
        rst.setData(null);
        rst.setMessage("执行成功");
        return rst;
    }

    public static <T> Result ok(T data) {
        Result rst = new Result();
        rst.setCode(0);
        rst.setData(data);
        rst.setMessage("执行成功");
        return rst;
    }


    public static <T> Result ok(T data, String message) {
        Result rst = new Result();
        rst.setCode(0);
        rst.setData(data);
        rst.setMessage(message);
        return rst;
    }

    public static <T> Result okTip(T data, String message) {
        Result rst = new Result();
        rst.setCode(1);
        rst.setData(data);
        rst.setMessage(message);
        return rst;
    }

    public static Result error() {
        Result rst = new Result();
        rst.setCode(-1);
        rst.setData(null);
        rst.setMessage("不符合执行条件中止，或声明式退出流程");
        return rst;
    }


    public static <T> Result error(T data) {
        Result rst = new Result();
        rst.setCode(-1);
        rst.setData(data);
        rst.setMessage("不符合执行条件中止，或声明式退出流程");
        return rst;
    }

    public static <T> Result error(T data, String message) {
        Result rst = new Result();
        rst.setCode(-1);
        rst.setData(data);
        rst.setMessage(message);
        return rst;
    }


    public static <T> Result data(int code,T data, String message) {
        Result rst = new Result();
        rst.setCode(code);
        rst.setData(data);
        rst.setMessage(message);
        return rst;
    }

    public static <T> Result data(CodeEnum type,T data) {
        Result rst = new Result();
        rst.setCode(type.getValue());
        rst.setData(data);
        rst.setMessage(type.getDescription());
        return rst;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public static enum CodeEnum {

        SUCCESS_AND_TIP(1,"执行成功，携带提示消息"),
        SUCCESS(0,"执行成功"),
        ERROR(-1,"不符合执行条件中止，或声明式退出流程"),
        UNAUTHORIZED(-3,"无系统级执行权限"),
        EXCEPTION(-4,"未预料到的异常或运行期错误"),
        ;

        int value;
        String description;

        CodeEnum(int value,String description){
            this.value = value;
            this.description = description;
        }


        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }



}
