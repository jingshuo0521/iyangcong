package com.iyangcong.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.iyangcong.reader.R;
import com.iyangcong.reader.app.UpdateManager;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.SettingItem;
import com.iyangcong.reader.ui.button.FlatButton;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CleanMessageUtil;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.DataCleanManager;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.UIHelper;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheManager;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.utils.SocializeUtils;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

public class MineSettingActivity extends SwipeBackActivity {


    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.mine_setting_bind_item)
    SettingItem mineSettingBindItem;
    @BindView(R.id.activity_mine_setting)
    LinearLayout activityMineSetting;
    @BindView(R.id.fb_log_out)
    FlatButton fbLogOut;
    @BindView(R.id.si_setting_delete_cache)
    SettingItem siSettingDeleteCache;
    @BindView(R.id.si_setting_receive_notice)
    SettingItem siSettingReceiveNotice;
    @BindView(R.id.mine_setting_about_item)
    SettingItem mineSettingAboutItem;
    @BindView(R.id.mine_setting_update)
    SettingItem mineSettingUpdate;

    private SharedPreferenceUtil sharedPreferenceUtil;

    private String cache;
    private CacheManager cacheManager = CacheManager.INSTANCE;

    private boolean isReceive;
    private final int LOGINTYPE_WEIXIN =1;


    @OnClick({R.id.btnBack, R.id.mine_setting_bind_item, R.id.fb_log_out, R.id.mine_setting_about_item, R.id.mine_setting_update, R.id.si_setting_delete_cache})
    void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.mine_setting_bind_item:
                if (!CommonUtil.getLoginState()) {
                    Intent intent_login = new Intent(context, LoginActivity.class);
                    startActivity(intent_login);
                    finish();
                } else {
                    startActivity(new Intent(this, MineSettingBindActivity.class));
                }
                break;
            case R.id.fb_log_out:
                ToastCompat.makeText(MineSettingActivity.this, "退出登录成功", Toast.LENGTH_LONG).show();
                fbLogOut.setVisibility(View.GONE);
//                sharedPreferenceUtil.putInt(SharedPreferenceUtil.DELETED_DEFAULT_BOOK, 0);
                sharedPreferenceUtil.putBoolean(SharedPreferenceUtil.LOGIN_STATE, false);
                sharedPreferenceUtil.putLong(SharedPreferenceUtil.PRE_USER_ID, 0);
                sharedPreferenceUtil.putString(SharedPreferenceUtil.CLASS_NAMES,"");
                sharedPreferenceUtil.putString(SharedPreferenceUtil.CLASS_IDS,"");
                sharedPreferenceUtil.putString(SharedPreferenceUtil.SEMESTER_ID,"");
                sharedPreferenceUtil.putString(SharedPreferenceUtil.SEMESTER_NAME,"");
                sharedPreferenceUtil.putLong(SharedPreferenceUtil.USER_ID, -1);
                sharedPreferenceUtil.putString(SharedPreferenceUtil.LOGIN_USER_ACCOUNT, "");
                sharedPreferenceUtil.putInt(SharedPreferenceUtil.SHOW_TYPE,0);
                dologout();
                finish();
                break;
            case R.id.mine_setting_about_item:
                startActivity(new Intent(this, MineAboutProductsActivity.class));
                break;
            case R.id.mine_setting_update:
                autoUpgrade();
                break;
            case R.id.si_setting_delete_cache:
                deleteCacheDialog();
                break;
        }
    }

    private void dologout(){
        int loginType=sharedPreferenceUtil.getInt(SharedPreferenceUtil.LOGIN_TYPE,0);

        if(loginType==LOGINTYPE_WEIXIN) {
            UMShareAPI.get(this).deleteOauth(this, SHARE_MEDIA.WEIXIN, authListener);
            sharedPreferenceUtil.putInt(SharedPreferenceUtil.LOGIN_TYPE,0);
        }
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
            Toast.makeText(MineSettingActivity.this, "取消了", Toast.LENGTH_LONG).show();
        }
    };
    private void logout() {
        OkGo.get(Urls.LogOutURL)
                .execute(new JsonCallback<IycResponse<String>>(this) {
                    @Override
                    public void onSuccess(IycResponse<String> listIycResponse, Call call, Response response) {
                        ToastCompat.makeText(MineSettingActivity.this, "退出登录成功", Toast.LENGTH_LONG).show();
                        fbLogOut.setVisibility(View.GONE);
                        sharedPreferenceUtil.putBoolean(SharedPreferenceUtil.LOGIN_STATE, false);
                        sharedPreferenceUtil.putInt(SharedPreferenceUtil.USER_TYPE,-1);
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_setting);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
    }

    @Override
    protected void initView() {
        setMainHeadView();
        setAutoAddNewWordState();
        if (!CommonUtil.getLoginState()) {
            fbLogOut.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText("更多设置");
        btnBack.setImageResource(R.drawable.btn_back);
        try {
            cache = CleanMessageUtil.getTotalCacheSize(context);
        } catch (Exception e) {
        }
        siSettingDeleteCache.setHintText(cache);
    }

    private void autoUpgrade() {
        if (UIHelper.isNetAvailable(this)) {
            UpdateManager updateManager = UpdateManager.getUpdateManager();
            updateManager.checkAppUpdate(this, true,
                    appContext.getDeviceToken(), true);
        } else {
            ToastCompat.makeText(this, "网络不稳定，请稍后再试", Toast.LENGTH_SHORT).show();
        }
    }

    private void setAutoAddNewWordState() {
        boolean isReceive = sharedPreferenceUtil.getBoolean(SharedPreferenceUtil.IS_RECEIVE_NOTICE, false);
        siSettingReceiveNotice.getCheckedTextView().setChecked(isReceive);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) siSettingReceiveNotice.getCheckedTextView().getLayoutParams();
        lp.setMargins(0, 0, 0, 0);
//        lp.gravity = Gravity.RIGHT;
        siSettingReceiveNotice.getCheckedTextView().setLayoutParams(lp);
        siSettingReceiveNotice.getCheckedTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checkedState = siSettingReceiveNotice.getCheckedTextView().isChecked();
                siSettingReceiveNotice.getCheckedTextView().setChecked(!checkedState);
                sharedPreferenceUtil.putBoolean(SharedPreferenceUtil.IS_RECEIVE_NOTICE, !checkedState);
//                if (!checkedState) {
//                    ToastCompat.makeText(MineSettingActivity.this, "推送接收已开启", Toast.LENGTH_SHORT).show();
//                    if (!PushManager.isPushEnabled(getApplication())) {
//                        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, Constants.BAIDU_API_KEY);
//                    }
//                } else {
//                    ToastCompat.makeText(MineSettingActivity.this, "推送接收已关闭", Toast.LENGTH_SHORT).show();
//                    if (PushManager.isPushEnabled(getApplication())) {
//                        PushManager.stopWork(getApplicationContext());
//                    }
//                }
            }
        });
    }

    private void deleteCacheDialog() {
        final NormalDialog dialog = new NormalDialog(context);
        dialog.content("是否清除缓存？")
                .btnNum(2)
                .btnText("确定", "取消")
                .isTitleShow(false)
                .show();

        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        deleteCache();
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                });

    }

    public void deleteCache() {
        CleanMessageUtil.clearAllCache(context);
        DataCleanManager.cleanInternalCache(context);
        DataCleanManager.cleanCustomCache("/storage/sdcard1/iyangcong/img");
        try {
            cache = CleanMessageUtil.getTotalCacheSize(context);
        } catch (Exception e) {
        }
        siSettingDeleteCache.setHintText(cache);
    }
}
