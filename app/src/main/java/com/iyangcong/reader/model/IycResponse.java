package com.iyangcong.reader.model;

import java.io.Serializable;


/**
 * Created by ljw on 2017/2/13.
 */

public class IycResponse<T> implements Serializable {

    private static final long serialVersionUID = -4767949990830541557L;
    public int statusCode;
    public String msg;
    public T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }
}
