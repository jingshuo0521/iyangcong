package com.iyangcong.reader.app;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.multidex.MultiDex;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.iyangcong.reader.bean.ChapterTestBean;
import com.iyangcong.reader.epub.database.DaoSession;
import com.iyangcong.reader.epub.database.EpubBookDatabase;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.store.PersistentCookieStore;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;

import org.geometerplus.android.fbreader.FBReaderApplication;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;


public class AppContext extends FBReaderApplication{

    public static final String TAG = "AppContext";
    public boolean isLogin = false;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private String deviceToken;
    // 电子书对象
    private String key;
    private boolean isComplete;
    private EpubBookDatabase mEpubBookDatabase;

    private ChapterTestBean mChapterTestBean;// 章节测试对象

    public ChapterTestBean getChapterTestBean() {
        return mChapterTestBean;
    }

    public void setChapterTestBean(ChapterTestBean chapterTestBean) {
        mChapterTestBean = chapterTestBean;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }


    public String getDeviceToken() {
        if (deviceToken == null) {
            deviceToken = obtainDeviceToken();
        }
        return deviceToken;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }


    /**
     * @Fields IS_NETWORK_AVAILABLE : TODO 0表示网络通，1不通；以后可以根据要求设置成2表示gprs，3表示wifi
     */
    private int IS_NETWORK_AVAILABLE = 0;

    /**
     * 网络是否可用
     *
     * @return
     */
    public int getIS_NETWORK_AVAILABLE() {
        return IS_NETWORK_AVAILABLE;
    }

    public void setIS_NETWORK_AVAILABLE(int iS_NETWORK_AVAILABLE) {
        IS_NETWORK_AVAILABLE = iS_NETWORK_AVAILABLE;
    }

    private static AppContext appInstance;

    public static AppContext getInstance() {
        return appInstance;
    }


    /**
     * 获取App安装包信息
     *
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

    /**
     * 初始化友盟第三方登录内容
     */
    private void initUmeng3rdPlatform() {
//        Config.DEBUG = true;
        //UMConfigure.init(AppContext.getInstance(), "5954bc1f717c1910a900198a", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "76695062a84a60fa0d2556f4a9eb96eb");
//		UMConfigure.setLogEnabled(false);
        UMConfigure.init(AppContext.getInstance(), "5954bc1f717c1910a900198a", "umeng", UMConfigure.DEVICE_TYPE_PHONE,
                "76695062a84a60fa0d2556f4a9eb96eb");
        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(false);
        UMShareAPI shareAPI = UMShareAPI.get(this);
        shareAPI.setShareConfig(config);

        PlatformConfig.setWeixin(Constants.WECHAT_APP_ID, "667cb1006271371a2e2cca37bce3d87f");
        PlatformConfig.setQQZone(Constants.QQ_APP_ID, "siK8jbES1K5ZTAI2");
        PlatformConfig.setSinaWeibo(Constants.WEIBO_APP_KEY, Constants.WEIBO_APP_SECRET, "http://www.iyangcong.com/app/index");
    }



    private void initUmengSDK(){
        //UMConfigure.setLogEnabled(true);

        //PushAgent.DEBUG=true;
        PushAgent.getInstance(this).register(new IUmengRegisterCallback(){

            @Override
            public void onSuccess(String s) {
                Log.i("walle", "--->>> onSuccess, s is " + s);
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.i("walle", "--->>> onFailure, s is " + s + ", s1 is " + s1);
            }
        });
    }

    private void initUmengSDKDelay(){
        if(getApplicationContext().getPackageName().equals(getCurrentProcessName())){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initUmengSDK();
                }
            }, 5000);
        } else {
            initUmengSDK();
        }
    }

    /**
     * 获取当前进程名
     */
    private String getCurrentProcessName(){
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService
                (Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
            }
        }
        return processName;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
        initUmeng3rdPlatform();
        if (sharedPreferenceUtil.getBoolean(SharedPreferenceUtil.IS_RECEIVE_NOTICE, false)) {
            initUmengSDK();
        }
//        deviceToken = obtainDeviceToken();
        //okhttp请求框架初始化方法
        OkGo.init(this);

//        AuthInfo tmpAuthInfo = new AuthInfo(this,"4049139728","http://www.iyangcong.com/app/index","");
//        WbSdk.install(this,tmpAuthInfo);
        //shao add begin
        AppCrashHandler crashHandler = AppCrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        //shao add end
        //以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
        //好处是全局参数统一,特定请求可以特别定制参数
        try {
            //以下都不是必须的，根据需要自行选择,一般来说只需要 debug,缓存相关,cookie相关的 就可以了
            OkGo.getInstance()

                    // 打开该调试开关,打印级别INFO,并不是异常,是为了显眼,不需要就不要加入该行
                    // 最后的true表示是否打印okgo的内部异常，一般打开方便调试错误
                    .debug("OkGo", Level.INFO, true)

                    //如果使用默认的 60秒,以下三行也不需要传
                    .setConnectTimeout(OkGo.DEFAULT_MILLISECONDS)  //全局的连接超时时间
                    .setReadTimeOut(OkGo.DEFAULT_MILLISECONDS)     //全局的读取超时时间
                    .setWriteTimeOut(OkGo.DEFAULT_MILLISECONDS)    //全局的写入超时时间

                    //4式,默认是不使用缓存,可以不传,具体其他模式看 github 介绍 https://github.com/jeasonlzy/
                    .setCacheMode(CacheMode.NO_CACHE)

                    //可以全局统一设置缓存时间,默认永不过期,具体使用方法看 github 介绍
                    .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)

                    //可以全局统一设置超时重连次数,默认为三次,那么最差的情况会请求4次(一次原始请求,三次重连请求),不需要可以设置为0
                    .setRetryCount(3)

                    //如果不想让框架管理cookie（或者叫session的保持）,以下不需要
//                .setCookieStore(new MemoryCookieStore())            //cookie使用内存缓存（app退出后，cookie消失）
                    .setCookieStore(new PersistentCookieStore())        //cookie持久化存储，如果cookie不过期，则一直有效

                    //可以设置https的证书,以下几种方案根据需要自己设置
//                    .setCertificates();                                  //方法一：信任所有证书,不安全有风险
//                    .setCertificates(new SafeTrustManager())            //方法二：自定义信任规则，校验服务端证书
                    .setCertificates(getAssets().open("https/iyangcong.com.jks"));     //方法三：使用预埋证书，校验服务端证书（自签名证书）
//                    //方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
//                    .setCertificates(getAssets().open("xxx.bks"), "123456", getAssets().open("yyy.cer"))//

            //配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
//                    .setHostnameVerifier(new SafeHostnameVerifier())

            //可以添加全局拦截器，不需要就不要加入，错误写法直接导致任何回调不执行
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        return chain.proceed(chain.request());
//                    }
//                })

        } catch (Exception e) {
            e.printStackTrace();
        }

        //解决系统没有读取文件安装apk的权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        Logger.addLogAdapter(new AndroidLogAdapter());//日志框架初始化；
//        SoLoader.init(this, /* native exopackage */ false);
        mEpubBookDatabase = new EpubBookDatabase(this);
    }

    public DaoSession getEpubSession(){
        if(mEpubBookDatabase != null)
            return mEpubBookDatabase.getSession();
        return null;
    }

    private String obtainDeviceToken() {
        String deviceToken = "";
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        deviceToken = manager.getDeviceId();
        if (TextUtils.isEmpty(deviceToken)) {
            deviceToken = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
        if (TextUtils.isEmpty(deviceToken)) {
        }
        if (TextUtils.isEmpty(deviceToken)) {
            deviceToken = "UnknownAndroidDevice";
        }
        return deviceToken;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}
