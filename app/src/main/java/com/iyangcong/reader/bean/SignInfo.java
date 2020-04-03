package com.iyangcong.reader.bean;

/**
 * Created by ljw on 2017/3/25.
 */

public class SignInfo {
    private int getCoinValue;
    private boolean isTodayHaveSigned;
    private int lastSignInDays;

    public int getGetCoinValue() {
        return getCoinValue;
    }

    public void setGetCoinValue(int getCoinValue) {
        this.getCoinValue = getCoinValue;
    }

    public boolean isTodayHaveSigned() {
        return isTodayHaveSigned;
    }

    public void setTodayHaveSigned(boolean todayHaveSigned) {
        isTodayHaveSigned = todayHaveSigned;
    }

    public int getLastSignInDays() {
        return lastSignInDays;
    }

    public void setLastSignInDays(int lastSignInDays) {
        this.lastSignInDays = lastSignInDays;
    }
}
