package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/23.
 */

public class ThirdBindingReturn implements Serializable {
    private static final long serialVersionUID = 2660692590622975958L;
    private Long userId;
    @SerializedName(value = "isSuccess",alternate = {"isbinding"})
    private int isSuccess;

    public Long getUserId() {
        return userId;
    }

    public int getIsSuccess() {
        return isSuccess;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setIsSuccess(int isSuccess) {
        this.isSuccess = isSuccess;
    }
}
