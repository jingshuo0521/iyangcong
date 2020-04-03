
package com.iyangcong.reader.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.flyco.dialog.widget.NormalDialog;
import com.iyangcong.reader.MainActivity;
import com.iyangcong.reader.R;
import com.iyangcong.reader.app.AppContext;
import com.iyangcong.reader.broadcast.NetBroadcastReceiver;
import com.iyangcong.reader.event.ExitLoginEvent;
import com.iyangcong.reader.ui.CustomProgressDialog;
import com.iyangcong.reader.ui.networkerrolayout.VaryViewHelper;
import com.iyangcong.reader.ui.networkerrolayout.VaryViewHelperUtils;
import com.iyangcong.reader.utils.ActivityCollector;
import com.iyangcong.reader.utils.AppManager;
import com.iyangcong.reader.utils.NetUtil;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.lzy.okgo.OkGo;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.utils.SocializeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import static com.iyangcong.reader.utils.NetUtil.NETWORK_MOBILE;
import static com.iyangcong.reader.utils.NetUtil.NETWORK_NONE;
import static com.iyangcong.reader.utils.NetUtil.NETWORK_WIFI;
import static com.iyangcong.reader.utils.NetUtil.REMOTE_DATABASE_ERROR;


/**
 * 应用程序Activity的基类
 */
public abstract class BaseActivity extends FragmentActivity implements NetBroadcastReceiver.NetEvent {


    private final int LOGINTYPE_WEIXIN = 1;
    private SharedPreferenceUtil sharedPreferenceUtil;
    /**
     * activity标签
     */
    protected final String TAG = this.getClass().getSimpleName();
    protected final Activity context = this;
    protected AppContext appContext = null;

    /**
     * fragment管理器
     */
    protected FragmentManager fragmentManager = getSupportFragmentManager();

    /**
     * 提示框
     */
    protected NormalDialog dialog;

    /**
     * 加载框
     */
    protected CustomProgressDialog dialogProgress;

    /**
     * 网络类型
     */
    protected int netMobile;

    private VaryViewHelperUtils varyViewHelperUtils;

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config=new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config,res.getDisplayMetrics() );
        return res;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushAgent.getInstance(context).onAppStart();
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        appContext = AppContext.getInstance();
        AppManager.getAppManager().addActivity(this);
        MobclickAgent.setCatchUncaughtExceptions(true);
        initSystemBarTint();
//        getNetworkErrorLayout(this);
        varyViewHelperUtils = new VaryViewHelperUtils();
        initData(savedInstanceState);
        NetBroadcastReceiver.setNetEvent(this);
        ActivityCollector.addActivity(this);//将启动的Activity添加进来
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    /**
     * 数据初始化
     *
     * @param savedInstanceState
     */
    protected abstract void initData(Bundle savedInstanceState);

    /**
     * ui初始化
     */
    protected abstract void initView();

    /**
     * 设置当前页面头部视图
     */
    protected abstract void setMainHeadView();

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(context);
        changeLayoutAccrodToNetwork(getNetworkState(context));
    }

    protected int getNetworkState(Context mctx) {
        netMobile = NetUtil.getNetWorkState(mctx);
        return netMobile;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userEventBus(ExitLoginEvent exitLoginEvent) {
        sharedPreferenceUtil.putBoolean(SharedPreferenceUtil.LOGIN_STATE, false);
        sharedPreferenceUtil.putLong(SharedPreferenceUtil.PRE_USER_ID, 0);
        sharedPreferenceUtil.putString(SharedPreferenceUtil.CLASS_NAMES, "");
        sharedPreferenceUtil.putString(SharedPreferenceUtil.CLASS_IDS, "");
        sharedPreferenceUtil.putString(SharedPreferenceUtil.SEMESTER_ID, "");
        sharedPreferenceUtil.putString(SharedPreferenceUtil.SEMESTER_NAME, "");
        sharedPreferenceUtil.putLong(SharedPreferenceUtil.USER_ID, -1);
        sharedPreferenceUtil.putString(SharedPreferenceUtil.LOGIN_USER_ACCOUNT, "");
        sharedPreferenceUtil.putInt(SharedPreferenceUtil.SHOW_TYPE, 0);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("警告")
                .setMessage(exitLoginEvent.getMsg())
                .setCancelable(false)//将对话框设置为不可取消
                .setPositiveButton("重新登录", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCollector.finishAll();
                        dologout();//销毁掉所有的活动，下面是重新启动MainActivity活动
                        Intent intent = new Intent(BaseActivity.this, MainActivity.class);
                        //在广播里面启动活动需要加 FLAG_ACTIVITY_NEW_TASK
                        intent.putExtra("ExitLogin", true);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        builder.create().show();

    }

    UMAuthListener authListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            SocializeUtils.safeShowDialog(dialog);
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            SocializeUtils.safeCloseDialog(dialog);
            //Toast.makeText(MineSettingActivity.this, "成功了", Toast.LENGTH_LONG).show();
            //notifyDataSetChanged();
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            SocializeUtils.safeCloseDialog(dialog);
            //Toast.makeText(MineSettingActivity.this, "失败：" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            SocializeUtils.safeCloseDialog(dialog);
            Toast.makeText(BaseActivity.this, "取消了", Toast.LENGTH_LONG).show();
        }
    };

    private void dologout() {
        int loginType = sharedPreferenceUtil.getInt(SharedPreferenceUtil.LOGIN_TYPE, 0);

        if (loginType == LOGINTYPE_WEIXIN) {
            UMShareAPI.get(this).deleteOauth(this, SHARE_MEDIA.WEIXIN, authListener);
            sharedPreferenceUtil.putInt(SharedPreferenceUtil.LOGIN_TYPE, 0);
        }
    }

    private void changeLayoutAccrodToNetwork(int networkState) {
        switch (networkState) {
            case NETWORK_NONE:
                if (varyViewHelperUtils.getVaryViewHelper() != null) {
                    varyViewHelperUtils.getVaryViewHelper().showErrorView();
                }
                break;
            case NETWORK_MOBILE:
            case NETWORK_WIFI:
                if (varyViewHelperUtils.getVaryViewHelper() != null)
                    varyViewHelperUtils.getVaryViewHelper().showDataView();
                break;
            case REMOTE_DATABASE_ERROR:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(context);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
        ActivityCollector.removeActivity(this);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    protected void refreshSuccessAndSetLoadMoreStatus(PtrClassicFrameLayout layout, boolean canLoadMore) {
        if (layout != null) {
            layout.refreshComplete();
            layout.setLoadMoreEnable(canLoadMore);
        }
    }

    protected void refreshSuccess(PtrClassicFrameLayout ptrClassicFrameLayout) {
        if (ptrClassicFrameLayout != null) {
            ptrClassicFrameLayout.refreshComplete();
        }
    }

    protected void refreshFailed(PtrClassicFrameLayout ptrClassicFrameLayout) {
        if (ptrClassicFrameLayout != null) {
            if (ptrClassicFrameLayout.isRefreshing()) {
                ptrClassicFrameLayout.refreshComplete();
                ptrClassicFrameLayout.setLoadMoreEnable(false);
            }
        }
    }

    protected void loadMoreSuccess(PtrClassicFrameLayout ptrClassicFrameLayout, boolean isEnd) {
        if (ptrClassicFrameLayout != null) {
            ptrClassicFrameLayout.loadMoreComplete(true);
            if (isEnd) {
                ptrClassicFrameLayout.setLoadMoreEnable(false);
            } else {
                ptrClassicFrameLayout.setLoadMoreEnable(true);
            }
        }
    }

    protected void refreshSuccess(PtrClassicFrameLayout ptrClassicFrameLayout, boolean isLoadMore) {
        if (ptrClassicFrameLayout != null) {
            ptrClassicFrameLayout.refreshComplete();
            if (isLoadMore) {
                ptrClassicFrameLayout.setLoadMoreEnable(false);
            } else {
                ptrClassicFrameLayout.setLoadMoreEnable(true);
            }
        }
    }

    protected void loadMoreFailed(PtrClassicFrameLayout ptrClassicFrameLayout) {
        if (ptrClassicFrameLayout != null) {
            ptrClassicFrameLayout.loadMoreComplete(true);
            ptrClassicFrameLayout.setLoadMoreEnable(false);
        }
    }

    protected void autoRefresh(final PtrClassicFrameLayout ptrClassicFrameLayout) {
        if (ptrClassicFrameLayout != null) {
            if (!ptrClassicFrameLayout.isAutoRefresh() && !ptrClassicFrameLayout.isRefreshing()) {
                ptrClassicFrameLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        ptrClassicFrameLayout.autoRefresh(true);
                    }
                });
            }
        }
    }

    //控制界面的控件显示隐藏
    protected void gone(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    protected void visible(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    protected boolean isVisible(View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    /**
     * 子类可以重写改变状态栏颜色
     */
    protected int setStatusBarColor() {
        return getColorPrimary();
    }

    /**
     * 子类可以重写决定是否使用透明状态栏
     */
    protected boolean translucentStatusBar() {
        return true;
    }

    /**
     * 设置状态栏颜色
     */
    protected void initSystemBarTint() {
        Window window = getWindow();
        if (translucentStatusBar()) {
            // 设置状态栏全透明
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            return;
        }
        // 沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0以上使用原生方法
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(setStatusBarColor());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4-5.0使用三方工具类，有些4.4的手机有问题，这里为演示方便，不使用沉浸式
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(setStatusBarColor());
        }
    }

    public void showLoadingDialog() {
        showLoadingDialog(getString(R.string.loading_tip));
    }

    public void showLoadingDialog(String content) {

        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            } else {
                if (dialogProgress == null) {
                    dialogProgress = new CustomProgressDialog(this, content);
                    dialogProgress.show();
                } else {
                    if (!dialogProgress.isShowing())
                        dialogProgress.show();
                }
            }
        }
    }

    public void showUpDialog() {
        if (dialogProgress == null) {
            dialogProgress = new CustomProgressDialog(this, getString(R.string.upload_tip));
            dialogProgress.show();
        } else {
            dialogProgress.show();
        }
    }

    public void dismissLoadingDialig() {
        if (dialogProgress != null) {
            dialogProgress.dismiss();
        }
    }

    /**
     * 获取主题色
     */
    public int getColorPrimary() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    /**
     * 获取深主题色
     */
    public int getDarkColorPrimary() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        return typedValue.data;
    }

    @Override
    public void onNetChange(int netMobile) {
        this.netMobile = netMobile;
        isNetConnect(netMobile);
    }

    /**
     * 判断有无网络 。
     *
     * @param net
     * @return true 有网, false 没有网络.
     */
    //TODO暂时屏蔽，测试一下在fragment里面加网络变化的模块
    public boolean isNetConnect(int net) {
//        switch (net){
//            case NetUtil.NETWORK_WIFI:
//            case NetUtil.NETWORK_MOBILE:
//                if(varyViewHelperUtils.getVaryViewHelper() != null)
//                    varyViewHelperUtils.getVaryViewHelper().showDataView();
//                return true;
//            case NetUtil.NETWORK_NONE:
//                ToastCompat.makeText(this, getString(R.string.net_error_tip), 2000);
//                if(varyViewHelperUtils.getVaryViewHelper() != null){
//                    varyViewHelperUtils.getVaryViewHelper().showErrorView();
//                }
//                return false;
//            case NetUtil.REMOTE_DATABASE_ERROR:
//                ToastCompat.makeText(this,getString(R.string.remote_database_error),Toast.LENGTH_SHORT).show();
//                return false;
//        }
        return false;
    }

    /**
     * 初始化布局，该方法用来动态增加网络错误时的布局
     *
     * @param context         上下文
     * @param view            要被网络错误布局取代的布局的view实例
     * @param onClickListener 网络错误布局的点击事件
     * @return
     */
    public VaryViewHelper initVaryViewHelper(Context context, View view, View.OnClickListener
            onClickListener) {
        return varyViewHelperUtils.initVaryViewHelper(context, view, onClickListener);
    }

    public void showDataView() {
        varyViewHelperUtils.showDataView();
    }

    public void showErrorView() {
        varyViewHelperUtils.showErrorView();
    }

    //    private void getNetworkErrorLayout(Activity context){
//        mNetworkErrorLayout.init(context);
//    }
//
//    public void setNetworkErrorLayoutClicked(NetworkErrorLayout.OnRefreshClicked onRefreshClicked){
//        mNetworkErrorLayout.setOnRefreshClicked(onRefreshClicked);
//    }
//
//    public void setNetworkErrorLayoutVisibility(int visibility){
//        mNetworkErrorLayout.setLayoutVisibility(visibility);
//    }
//
//    public void addContentView(int layoutId,Activity context){
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
//        View view = LayoutInflater.from(context).inflate(layoutId,null,false);
//        context.addContentView(view,params);
//    }
//    /**
//     * 获取具体的activity的UI的root view;
//     * @param context
//     * @return
//     */
//    private ViewGroup getRootView(Context context){
//        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
//        return viewGroup;
//    }

    @Override
    protected void onStop() {
        super.onStop();
        //取消所有请求
        OkGo.getInstance().cancelAll();
        if (dialogProgress != null) {
            dialogProgress.dismiss();
            dialogProgress = null;
        }
    }
}
