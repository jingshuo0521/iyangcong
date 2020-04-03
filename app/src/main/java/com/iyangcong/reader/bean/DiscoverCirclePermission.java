package com.iyangcong.reader.bean;

import android.util.Log;

/**
 * Created by WuZepeng on 2017-03-09.
 * 用于创建圈子的时候记录圈子权限的类。
 */

public class DiscoverCirclePermission {
    /**圈子权限
     * 1.私密（可进入圈子，话题部分为空）
     * 2.半私密（用户可查看话题，但不可发言）
     * 3.公开（用户可查看话题、可发言）
     * */
    private int authority;
//    /*是否选中*/
//    private boolean isCheck;

//    public boolean isCheck() {
//        return isCheck;
//    }
//
//    public void setCheck(boolean check) {
//        isCheck = check;
//    }

    public int getAuthority() {
        return authority;
    }

    public void setAuthority(int authority) {
        this.authority = authority;
    }

    @Override
    public String toString() {
        return "DiscoverCirclePermission{" +
                "authority=" + authority + '}';
    }
}
