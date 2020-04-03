package com.iyangcong.reader.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.LoginActivity;
import com.iyangcong.reader.ui.IYangCongToast;

/**
 * Created by WuZepeng on 2017-03-27.
 */

public class LoginUtils {

    private boolean mIsLogin;

    public boolean ismIsLogin() {
        mIsLogin = CommonUtil.getLoginState();
        return mIsLogin;
    }

    public void startIntent(Context from, Class<?> klass) {
        Intent intent;
        Bundle bundle = new Bundle();
        if (ismIsLogin()) {
            intent = new Intent(from, klass);
            setBundleData(bundle);
            setIntentData(intent);
        } else {
            intent = new Intent(from, LoginActivity.class);
        }
        intent.putExtras(bundle);
        from.startActivity(intent);
    }

    public void setBundleData(Bundle bundle) {
    }

    public void setIntentData(Intent intent) {

    }

    public boolean isLogin(Context context) {
        if (!ismIsLogin()) {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
            return false;
        }
        return true;
    }
}
