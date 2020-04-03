package com.iyangcong.reader.bean;

import java.io.Serializable;

/**
 * Created by sheng on 2017/3/1.
 */

public class CodeReturn implements Serializable {
    private String code;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    private int userId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "CodeReturn{" +
                "code='" + code + '\'' +
                '}';
    }
}
