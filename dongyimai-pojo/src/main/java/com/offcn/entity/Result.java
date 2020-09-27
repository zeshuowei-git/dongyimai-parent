package com.offcn.entity;

import java.io.Serializable;

/**
 * @author ：yz
 * @date ：Created in 2020/8/25 14:55
 * @version: 1.0
 */
public class Result implements Serializable {


    private boolean success;
    private String message;

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Result{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}
