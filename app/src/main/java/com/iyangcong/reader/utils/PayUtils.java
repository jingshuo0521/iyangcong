package com.iyangcong.reader.utils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.alipay.share.sdk.openapi.algorithm.MD5;
import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.MinePayActivity;
import com.iyangcong.reader.bean.AlipayOrderInfo;
import com.iyangcong.reader.bean.WeChatPayResult;
import com.iyangcong.reader.bean.WechatpayOrderInfo;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.BookDao;
import com.iyangcong.reader.interfaceset.DeviceType;
import com.iyangcong.reader.interfaceset.RetryCounter;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.utils.AlipayUtils.PayResult;
import com.iyangcong.reader.wxapi.ScanPayReqData;
import com.iyangcong.reader.wxapi.WeiXinUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.orhanobut.logger.Logger;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by WuZepeng on 2017-05-03.
 */

public class PayUtils {

    private MinePayActivity context;
    private static final int SDK_PAY_FLAG = 1;
    private AlipayOrderInfo orderInfo = new AlipayOrderInfo();
    private WechatpayOrderInfo mWechatpayOrderInfo;
    private IWXAPI api;
    private String bookIds;
    private double price;
    private int orderType;
    private static final int SUCCESSFUL = 1;
    private static final int FAILURE = 3;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private String pricesStr;
    //订单类型
    public static final int PERSON_ORDER = 0;//个人订单
    public static final int RECOMMOND_BOOK_ORDER = 1;//必读书目订单
    public static final int MONTHLY_ORDER = 3;//包月订单

    public final static int WeiXin_TYPE = 14;
    public static final int AliPay_TYPE = 11;
    public PayUtils(MinePayActivity context, String bookIds, double price, int orderType, String priceStr) {
        this.context = context;
        this.bookIds = bookIds;
        this.price = price;
        this.orderType = orderType;
        this.pricesStr = priceStr;
    }

    /**
     * 支付宝支付业务：入参app_id
     */
    /** 商户私钥，pkcs8格式 */
    /** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 */
    /** 如果商户两个都设置了，优先使用 RSA2_PRIVATE */
    /** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE */
    /** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， */
    /**
     * 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1
     */
    private void startAlipay() {
        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(context);
                Map<String, String> result = alipay.payV2(orderInfo.getOrderInfo(), true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {


            @SuppressWarnings("unchecked")
            PayResult payResult = new PayResult((Map<String, String>) msg.obj);
            /**
             对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
             */
            String resultInfo = payResult.getResult();// 同步返回需要验证的信息
            String resultStatus = payResult.getResultStatus();
            // 判断resultStatus 为9000则代表支付成功
            if (TextUtils.equals(resultStatus, "9000")) {
                // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//                Toast.makeText(MinePayActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
//                finish();
                saveAppPayResult(SUCCESSFUL, AliPay_TYPE);
            } else {
                // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//                Toast.makeText(MinePayActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
//                finish();
                saveAppPayResult(FAILURE, AliPay_TYPE);
            }
        }
    };

    private void saveAppPayResult(final int payResult, final int payType) {
        final HashMap<String, String> hashmap = new HashMap();
        hashmap.put("bookIds", bookIds);
        hashmap.put("deviceType", DeviceType.ANDROID_3);
        hashmap.put("orderType", orderType + "");
        hashmap.put("payResult", payResult + "");
        hashmap.put("payType", payType + "");
        hashmap.put("totalPrice", price + "");
        hashmap.put("userId", CommonUtil.getUserId() + "");
        if (payType == AliPay_TYPE) {
            hashmap.put("orderInfo", "支付宝支付");
        } else if (payType == WeiXin_TYPE) {
            hashmap.put("orderInfo", "微信支付");
        }
        hashmap.put("priceString",pricesStr);
        postResult(payResult, hashmap);
    }

    private int count = 0;

    private void postResult(final int payResult, final HashMap<String, String> hashmap) {
        context.showLoadingDialog();
        OkGo.get(Urls.SaveAppPayResultURL)
                .tag(this)
                .params(hashmap)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        context.dismissLoadingDialig();
                        if (payResult == 1) {
                            if (sharedPreferenceUtil == null) {
                                sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
                            }
                            long currentBookId = sharedPreferenceUtil.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, -1);
                            if (bookIds.contains(currentBookId+"")) {
                                sharedPreferenceUtil.putLong(SharedPreferenceUtil.CURRENT_BUYED_BOOK_ID, currentBookId);
                            }
                            ToastCompat.makeText(context, "支付成功", Toast.LENGTH_LONG).show();
                            upDateLocalBook(bookIds.split(","));
                            context.finish();
                        } else if (payResult == 3) {
                            ToastCompat.makeText(context, "支付未成功，请重新支付", Toast.LENGTH_LONG).show();
                            context.changeButtonBackground(true);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        context.dismissLoadingDialig();
                        if (payResult == 1) {
                            if (++count < RetryCounter.MAX_RETRY_TIMES) {
                                postResult(payResult, hashmap);
                            } else {
                                setNotUploadOrder();
//                                ToastCompat.makeText(context, "网络状态差，上传支付成功信息失败，网络情况改善后再次上传", 1000);
                                context.finish();
                            }
                        } else if (payResult == 3) {
                            ToastCompat.makeText(context, "支付未成功，请重新支付", Toast.LENGTH_LONG).show();
                            context.changeButtonBackground(true);
                        }
                    }

//                    @Override
//                    public void parseError(Call call, Exception e) {
//                        super.parseError(call, e);
//                        if(count++< RetryCounter.MAX_RETRY_TIMES){
//                            postResult(payResult, hashmap);
//                        }else{
//                            count = 0;
//                            context.dismissLoadingDialig();
//                        }
//                    }
                });
    }

    private void upDateLocalBook(String[] bookIds) {
        if(!NotNullUtils.isNull(bookIds)){
            BookDao bookDao = new BookDao(DatabaseHelper.getHelper(context));
            for(String id:bookIds){
                bookDao.deleteByColumn("bookId",id);
            }
        }
    }

    private void getOrderInfo(String orderTitle, double price) {
        context.showLoadingDialog();
        OkGo.get(Urls.GetAlipayOrderInfoURL)
                .tag(this)
                .params("orderTitle", orderTitle)
                .params("price", price + "")
                .params("userId", CommonUtil.getUserId() + "")
                .execute(new JsonCallback<IycResponse<AlipayOrderInfo>>(context) {
                    @Override
                    public void onSuccess(IycResponse<AlipayOrderInfo> alipayOrderInfoIycResponse, Call call, Response response) {
                        orderInfo.setOrderInfo(alipayOrderInfoIycResponse.getData().getOrderInfo());
                        context.dismissLoadingDialig();
                        startAlipay();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        ToastCompat.makeText(context, "订单获取失败，请重新支付", Toast.LENGTH_LONG).show();
                        context.changeButtonBackground(true);
                    }
                });
    }

    private boolean isWXAppInstalledAndSupported() {
        IWXAPI msgApi = WXAPIFactory.createWXAPI(context, null);
        msgApi.registerApp(Constants.WECHAT_APP_ID);

        boolean sIsWXAppInstalledAndSupported = msgApi.isWXAppInstalled()
                && msgApi.isWXAppSupportAPI();

        return sIsWXAppInstalledAndSupported;
    }

    private void weChatPay() throws Exception {

        api = WXAPIFactory.createWXAPI(context, Constants.WECHAT_APP_ID);
        // TODO: 2019/6/13 记得修改
//        price = 0.01;

        OkGo.get(Urls.GetWechatpayOrderInfoURL)
                .tag(this)
                .params("userId", CommonUtil.getUserId() + "")
                .params("bookIds", bookIds)
                .params("price", price + "")
                .params("orderType",orderType)
                .params("priceStr",price+"")
                .execute(new JsonCallback<IycResponse<WechatpayOrderInfo>>(context) {
                    @Override
                    public void onSuccess(IycResponse<WechatpayOrderInfo> wechatpayOrderInfoIycResponse, Call call, Response response) {
                        Logger.e("okgo wechat pay info:"+wechatpayOrderInfoIycResponse.data.getResultString());
                        Logger.e("okgo wechat pay OutTradeNo:"+wechatpayOrderInfoIycResponse.data.getOutTradeNo());
                        mWechatpayOrderInfo = wechatpayOrderInfoIycResponse.data;
                        sendPayReq(wechatpayOrderInfoIycResponse.data.getResultString());
                        context.dismissLoadingDialig();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        context.dismissLoadingDialig();
                        ToastCompat.makeText(context, "订单获取失败，请重新支付", Toast.LENGTH_SHORT).show();
                        context.changeButtonBackground(true);
                        Logger.e("生成预支付订单出错");
                    }
                });

//        api.registerApp(Constants.WECHAT_APP_ID);
//        StringBuffer sb = new StringBuffer();
//        String packageSign = MD5.getMessageDigest(sb.toString().getBytes())
//                .toUpperCase();
//        String productId = WeiXinUtil.getTimeStamp();// 获取商品订单号
//        String outTradeNo = WeiXinUtil.getOrderNo();// WeiXinUtil.getOrderNo();//获取该笔交易的订单号
//        String ip = CommonUtil.getHostIP();// 获取发起请求的IP，我这里就直接获取了本机的IP了，实际开发需要获取实际的请求IP
//
//        String body = "爱洋葱客户端图书支付";
//
//
//        int money = (int) (price * 100);
//
//        data = new ScanPayReqData(body, money, productId, outTradeNo, ip);// 组成要发送给微信的数据为一个实体类
//        String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
//        data.SetValue("appid", data.getAppid());// 公众账号ID
//        data.SetValue("body", data.getBody());//
//        data.SetValue("mch_id", data.getMch_id());// 商户号
//        data.SetValue("spbill_create_ip",
//                data.getSpbill_create_ip());// 终端ip
//        data.SetValue("nonce_str", data.getNonce_str());// 随机字符串
//        data.SetValue("trade_type", "APP");
//        data.SetValue("notify_url", data.getNotify_url());//
//        data.SetValue("out_trade_no", data.getOut_trade_no());//
//        // data.SetValue("trade_type", data.getTrade_type());//
//        data.SetValue("total_fee", data.getTotal_fee());//
//
//        String sign = data.MakeSign("MD5");
//        data.SetValue("sign", sign);
//        boolean b = data.CheckSign();
//        String xmls = data.ToXml();
//        OkGo.post(url)
//                .tag(this)
//                .upString(xmls.toString(), MediaType.parse("text/xml;charset=utf-8"))
//                .execute(new StringCallback() {
//                    @Override
//                    public void onSuccess(String s, Call call, Response response) {
//                        sendPayReq(s);
//                        context.dismissLoadingDialig();
//                    }
//
//                    @Override
//                    public void onError(Call call, Response response, Exception e) {
//                        context.dismissLoadingDialig();
//                        ToastCompat.makeText(context, "生成预支付订单出错", Toast.LENGTH_SHORT).show();
//                        Logger.e("生成预支付订单出错");
//                    }
//                });
    }


    private void sendPayReq(String reuslt) {
        try {
            ScanPayReqData rdata = new ScanPayReqData();
            rdata.FromXml(reuslt);
            String prepay_id = rdata.getValue("prepay_id");
            PayReq req = new PayReq();
            req.appId = Constants.WECHAT_APP_ID;
            req.partnerId = rdata.getValue("mch_id");
            req.prepayId = prepay_id;
            req.packageValue = "Sign=WXPay";
            req.nonceStr = WeiXinUtil.getRandomStringByLength(32);
            req.timeStamp = String.valueOf(rdata.genTimeStamp());

            @SuppressWarnings("deprecation")
            List<NameValuePair> signParams = new LinkedList<NameValuePair>();
            signParams.add(new BasicNameValuePair("appid", req.appId));
            signParams.add(new BasicNameValuePair("noncestr",
                    req.nonceStr));
            signParams.add(new BasicNameValuePair("package",
                    req.packageValue));
            signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
            signParams.add(new BasicNameValuePair("prepayid",
                    req.prepayId));
            signParams.add(new BasicNameValuePair("timestamp",
                    req.timeStamp));
            req.sign = rdata.genAppSign(signParams);
            Logger.e("wechat pay sign:"+req.sign);
            api.registerApp(Constants.WECHAT_APP_ID);
            api.sendReq(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
                .getBytes());
    }

    private void setNotUploadOrder() {
        SharedPreferenceUtil sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
        if (sharedPreferenceUtil.getString(SharedPreferenceUtil.NOT_UPLOAD_ORDER_BOOKS, null) == null) {
            sharedPreferenceUtil.putString(SharedPreferenceUtil.NOT_UPLOAD_ORDER_BOOKS, bookIds);
        } else {
            sharedPreferenceUtil.putString(SharedPreferenceUtil.NOT_UPLOAD_ORDER_BOOKS, bookIds + "," + sharedPreferenceUtil.getString(SharedPreferenceUtil.NOT_UPLOAD_ORDER_BOOKS, null));
        }
        if (sharedPreferenceUtil.getFloat(SharedPreferenceUtil.NOT_UPLOAD_ORDER_PRICE, 0) == 0) {
            sharedPreferenceUtil.putFloat(SharedPreferenceUtil.NOT_UPLOAD_ORDER_PRICE, (float) price);
        } else {
            sharedPreferenceUtil.putFloat(SharedPreferenceUtil.NOT_UPLOAD_ORDER_PRICE, (float) price + sharedPreferenceUtil.getFloat(SharedPreferenceUtil.NOT_UPLOAD_ORDER_PRICE, 0));
        }
        if(TextUtils.isEmpty(sharedPreferenceUtil.getString(SharedPreferenceUtil.NOT_UPLOAD_ORDER_PRICESTR,null))){
            sharedPreferenceUtil.putString(SharedPreferenceUtil.NOT_UPLOAD_ORDER_PRICESTR,pricesStr);
        }else{
            sharedPreferenceUtil.putString(SharedPreferenceUtil.NOT_UPLOAD_ORDER_PRICESTR,pricesStr+","+sharedPreferenceUtil.getString(SharedPreferenceUtil.NOT_UPLOAD_ORDER_PRICESTR,null));
        }
    }

    public void wechatPayProcess() {
        if (price <= 0) {
            ToastCompat.makeText(context, "价格有误暂不可支付", Toast.LENGTH_SHORT).show();
        } else {
            if (isWXAppInstalledAndSupported()) {
                try {
                    context.showLoadingDialog();
                    weChatPay();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ToastCompat.makeText(context, "您未安装最新版本微信，不支持微信支付，请安装或升级微信版本", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void aliPayProcess() {
        if (price <= 0) {
            ToastCompat.makeText(context, "价格有误暂不可支付", Toast.LENGTH_SHORT).show();
        } else {
            getOrderInfo(context.getResources().getString(R.string.order_title), price);
        }
    }
    private int retryCount = 0;
    public void checkStatusOfWechatPay(){
        OkGo.get(Urls.CheckStatusOfWechatPay)
                .tag(this)
                .params("outTradeNo", mWechatpayOrderInfo.getOutTradeNo())
                .execute(new JsonCallback<IycResponse<String>>(context) {
                    @Override
                    public void onSuccess(IycResponse<String> stringIycResponse, Call call, Response response) {
                        context.dismissLoadingDialig();
                        ToastCompat.makeText(context, stringIycResponse.msg , Toast.LENGTH_SHORT).show();
                        if(stringIycResponse.data.equals("1")){
                            saveAppPayResult(SUCCESSFUL, WeiXin_TYPE);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Logger.e("订单核对失败");
                        if(retryCount++< RetryCounter.MAX_RETRY_TIMES){
                            checkStatusOfWechatPay();
                        }else{
                            ToastCompat.makeText(context, "网络状态差，订单核对失败，请查看购买书籍是否到帐，如未到帐请联系客服！", Toast.LENGTH_SHORT).show();
                            context.dismissLoadingDialig();
                            context.finish();
                        }
                    }

                    @Override
                    public void parseError(Call call, Exception e) {
                        if(retryCount++< RetryCounter.MAX_RETRY_TIMES){
                            checkStatusOfWechatPay();
                        }else{
                            context.dismissLoadingDialig();
                        }
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWechatPayFinish(WeChatPayResult weChatPayResult) {
        if (weChatPayResult.errCode == 0 && mWechatpayOrderInfo != null) {
            ToastCompat.makeText(context, "支付成功，订单核对中，请勿退出...", Toast.LENGTH_SHORT).show();
            context.showLoadingDialog();
            checkStatusOfWechatPay();
//            saveAppPayResult(SUCCESSFUL, WeiXin_TYPE);
        } else {
            ToastCompat.makeText(context, "支付失败"+ weChatPayResult.errStr==null || weChatPayResult.errStr.equals("")?"，请重新支付！":"，失败原因："+ weChatPayResult.errStr
                    , Toast.LENGTH_SHORT).show();
            saveAppPayResult(FAILURE, WeiXin_TYPE);
        }
    }

    public void registerEventBus(){
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public void unregisterEventBus(){
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    //微信支付后回调广播
    public BroadcastReceiver mReceiver = new  BroadcastReceiver(){
        int code;

        @Override
        public void onReceive(Context context, Intent intent) {
            int errCode = intent.getIntExtra("errCode", 1);
            code = errCode;
            if (code == 0) {
                saveAppPayResult(SUCCESSFUL, WeiXin_TYPE);
            } else {
                saveAppPayResult(FAILURE, WeiXin_TYPE);
            }
        }
    };

//    public void initBroadCast() {
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("com.iyangcong.errCode");
//        context.registerReceiver(mReceiver, intentFilter, FBReaderIntents.Event.CUSTOM_BROADCAST_PSERMISSION,null);
//    }

//    public void unregisterBroadCast(){
//        if(context != null){
//            context.unregisterReceiver(mReceiver);
//        }
//    }
}
