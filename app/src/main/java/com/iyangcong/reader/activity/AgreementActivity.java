package com.iyangcong.reader.activity;

import android.os.Bundle;

import com.iyangcong.reader.utils.Constants;

/**
 * Created by WuZepeng on 2017-05-18.
 * 用来展示用户协议的activity，需要传用户协议的url
 */

public class AgreementActivity extends AbsWebViewAcitivity {
    String url;

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    protected boolean isShareMode() {
        return false;
    }


    @Override
    protected void initData(Bundle savedInstanceState) {
        url = getIntent().getStringExtra(Constants.USERAGREEMENT);
        if (url.contains("privacy")) {
            setTitle("隐私政策");
        } else if (url.contains("user_protocol")) {
            setTitle("爱洋葱阅读用户协议");
        } else if (url.contains("coin")) {
            setTitle("积分和头衔说明");
        } else if(url.contains("baoyue")){
            setTitle("包月协议");
        }else if(url.contains("privacy")){
            setTitle("隐私政策");
        }else{
            setTitle("报名信息");
        }
    }
}
