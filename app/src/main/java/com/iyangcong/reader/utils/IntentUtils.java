package com.iyangcong.reader.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.iyangcong.reader.activity.AgreementActivity;
import com.iyangcong.reader.activity.DiscoverNewCircle2;
import com.iyangcong.reader.activity.MinePayActivity;
import com.iyangcong.reader.activity.MineShoppingActivity;
import com.iyangcong.reader.base.BaseActivity;
import com.iyangcong.reader.bean.DiscoverCreateCircle;

import java.math.BigDecimal;

/**
 * Created by WuZepeng on 2017-03-14.
 */

public class IntentUtils {

    public static void goToWebViewActivity(Activity from, String url){
        Intent intent = new Intent(from, AgreementActivity.class);
        intent.putExtra(Constants.USERAGREEMENT,url);
        from.startActivity(intent);
    }


    public static void goToPayActivity(Activity from,String ids,String prices,double totalPrice,int count){
        BigDecimal bigDecimal = new BigDecimal(totalPrice);
        Intent intent = new Intent(from, MinePayActivity.class);
        intent.putExtra("bookIds", ids);
        intent.putExtra("pricesString",prices);//如果有多本书，那么将选中的书的价格按照ids的顺序用逗号拼接起来；
        intent.putExtra("totalPrice", bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        intent.putExtra("count", count);
        from.startActivity(intent);
    }
}
