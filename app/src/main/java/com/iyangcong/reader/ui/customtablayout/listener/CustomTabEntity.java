package com.iyangcong.reader.ui.customtablayout.listener;

import android.support.annotation.DrawableRes;

/**
 * 自定义tab的接口定义
 */
public interface CustomTabEntity {
    String getTabTitle();

    @DrawableRes
    int getTabSelectedIcon();

    @DrawableRes
    int getTabUnselectedIcon();
}