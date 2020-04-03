package com.iyangcong.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.MineSettingBindAdapter;
import com.iyangcong.reader.base.BaseActivity;
import com.iyangcong.reader.bean.BindSituation;
import com.iyangcong.reader.bean.IYangCongBind;
import com.iyangcong.reader.bean.ThirdLoginReturn;
import com.iyangcong.reader.bean.UserAccountType;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.enumset.BindType;
import com.iyangcong.reader.interfaceset.DeviceType;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.orhanobut.logger.Logger;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.shareboard.SnsPlatform;
//import com.umeng.socialize.utils.Log;
import com.umeng.socialize.utils.SocializeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

public class MineSettingBindActivity extends BaseActivity implements MineSettingBindAdapter.ClickTypeCallback {


    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.mine_setting_bind_lv)
    ListView mineSettingBindLv;
    @BindView(R.id.activity_mine_setting_bind)
    LinearLayout activityMineSettingBind;
    @BindView(R.id.tv_user)
    TextView tvUser;

    @OnClick({R.id.btnBack})
    void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
        }
    }

    private MineSettingBindAdapter mineSettingBindAdapter;
    private List<BindSituation> mBindSituation;
    private List<IYangCongBind> mBindList;
    private int unboundType;

    private SHARE_MEDIA[] thirdLoginList = {SHARE_MEDIA.WEIXIN, SHARE_MEDIA.QQ, SHARE_MEDIA.SINA, SHARE_MEDIA.DOUBAN};
    public ArrayList<SnsPlatform> platforms = new ArrayList<SnsPlatform>();
    private SharedPreferenceUtil sharedPreferenceUtil;
    private String userPhoneAccount = null, userEmailAccount = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_setting_bind);
        ButterKnife.bind(this);
        init3rdPlatforms();
        initView();
        setMainHeadView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getByUserId();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
        mBindSituation = new ArrayList<>();
        mBindList = new ArrayList<>();
    }

    @Override
    protected void initView() {
        tvUser.setText(sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_USER_ACCOUNT, null));
        mineSettingBindLv.setDivider(null);
        mineSettingBindLv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText("账号绑定");
        btnBack.setImageResource(R.drawable.btn_back);

    }

    public void getByUserId() {
        OkGo.get(Urls.BindingSituationURL)
                .tag(this)
                .params("userId", CommonUtil.getUserId())
                .execute(new JsonCallback<IycResponse<List<BindSituation>>>(this) {
                    @Override
                    public void onSuccess(IycResponse<List<BindSituation>> listIycResponse, Call call, Response response) {
                        dismissLoadingDialig();
                        mBindSituation.clear();
                        for (BindSituation sit : listIycResponse.getData()) {
                            mBindSituation.add(sit);
                        }
                        mBindList = getBindList(mBindSituation);
                        mineSettingBindAdapter = new MineSettingBindAdapter(context, mBindList);
                        mineSettingBindLv.setAdapter(mineSettingBindAdapter);
                        mineSettingBindAdapter.setDialogCallback(MineSettingBindActivity.this);
                        getMobileAndEmailInfo();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dismissLoadingDialig();
                    }
                });
    }

    /**
     * 向后台请求绑定信息的接口
     */
    private void getMobileAndEmailInfo() {
        if (!CommonUtil.getLoginState()) {
            return;
        }
        long userId = CommonUtil.getUserId();
        OkGo.get(Urls.URL + "/logincontroller/getaccount")
                .params("userId", userId)
                .execute(new JsonCallback<IycResponse<List<UserAccountType>>>(context) {
                    @Override
                    public void onSuccess(IycResponse<List<UserAccountType>> userAccountTypeIycResponse, Call call, Response response) {
                        if (isNull(userAccountTypeIycResponse) || isNull(userAccountTypeIycResponse.getData()))
                            return;
                        boolean needRefresh = false;
                        for (UserAccountType userType : userAccountTypeIycResponse.getData()) {
                            if (userType.getMobile() != null) {
                                userPhoneAccount = userType.getMobile();
                            }
                            if (userType.getEmail() != null) {
                                userEmailAccount = userType.getEmail();
                            }
                            if (!isNull(context, userEmailAccount, "") && !isNull(mBindList) && mBindList.size() == (thirdLoginList.length + 2)) {
                                IYangCongBind bind = mBindList.get(1);
                                bind.setIsbind(true);
                                bind.setName(userEmailAccount);
                                needRefresh = true;
                            }
                            if(!isNull(context,userPhoneAccount,"") && !isNull(mBindList) && mBindList.size() == (thirdLoginList.length + 2)){
                                IYangCongBind bind = mBindList.get(0);
                                bind.setIsbind(true);
                                bind.setName(userPhoneAccount);
                                needRefresh = true;
                            }
                        }
                        if(needRefresh){
                            mineSettingBindAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Logger.i("MineSettingBindActivity:" + e.getMessage());
                    }
                });
    }


    public List<IYangCongBind> getBindList(List<BindSituation> mBindSituation) {
        List<IYangCongBind> BindList = new ArrayList<>();
        IYangCongBind iYangCongBind5 = new IYangCongBind(R.drawable.ic_phone,false,"","手机");
        IYangCongBind iYangCongBind0 = new IYangCongBind(R.drawable.umeng_socialize_gmail, false, "", "邮箱");
        IYangCongBind iYangCongBind1 = new IYangCongBind(R.drawable.umeng_socialize_wechat, false, "", "微信");
        IYangCongBind iYangCongBind2 = new IYangCongBind(R.drawable.ic_mine_setting_bind_qq, false, "", "QQ");
        IYangCongBind iYangCongBind3 = new IYangCongBind(R.drawable.ic_mine_setting_bind_weibo, false, "", "微博");
        IYangCongBind iYangCongBind4 = new IYangCongBind(R.drawable.ic_mine_setting_bind_douban, false, "", "豆瓣");
        if (mBindSituation != null) {
            if (mBindSituation.size() > 0 && !isNull(context, mBindSituation.get(0).getEmail(), "")) {
                iYangCongBind0.setIsbind(true);
            }
            for (int i = 0; i < mBindSituation.size(); i++) {
                switch (mBindSituation.get(i).getType()) {
                    case 1:
                        iYangCongBind1.setIsbind(true);
                        break;
                    case 2:
                        iYangCongBind2.setIsbind(true);
                        break;
                    case 3:
                        iYangCongBind3.setIsbind(true);
                        break;
                    case 4:
                        iYangCongBind4.setIsbind(true);
                        break;
//                    case 5:
//                        iYangCongBind4.setIsbind(true);
//                        break;
                }
            }
        }
        String account = sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_USER_ACCOUNT, null);
        if (account != null && account.length() != 0) {
            if (account.indexOf("@") != -1) {
//                iYangCongBind0.setUser(sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_USER_ACCOUNT, null));
                iYangCongBind0.setIsbind(true);
            }
        }
        BindList.add(iYangCongBind5);
        BindList.add(iYangCongBind0);
        BindList.add(iYangCongBind1);
        BindList.add(iYangCongBind2);
        BindList.add(iYangCongBind3);
        BindList.add(iYangCongBind4);
        return BindList;
    }

    public void cutConnect(int unboundType) {
        showLoadingDialog();
        OkGo.post(Urls.CutconnectURL)
                .tag(this)
                .params("userId", CommonUtil.getUserId())
                .params("type", unboundType)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        ToastCompat.makeText(context, "解绑成功", Toast.LENGTH_SHORT).show();
                        getByUserId();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dismissLoadingDialig();
                        ToastCompat.makeText(context, "解绑失败，请稍候再试...", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void getClickType(int type, boolean isbind) {
        if (type == 0 ) {
            goToEmailBind(isbind,BindType.TELEPHONE);
        } else if(type == 1){
            goToEmailBind(isbind,BindType.EMAIL);
        } else if (isbind == true) {
            unboundType = type - 1;
            cutConnect(type - 1);
        } else if (isbind == false) {
            unboundType = type - 1;
            goToAuth(type -2);
            showLoadingDialog();
        }

    }

    private void goToEmailBind(boolean isBinding, BindType type) {
        Intent intent = new Intent(MineSettingBindActivity.this, BindEmailActivity.class);
        intent.putExtra(Constants.BIND_TYPE,type);
        intent.putExtra(Constants.IS_BINDING, isBinding);
        startActivity(intent);
    }

    /**
     * 根据type进入不同的第三方登录界面
     *
     * @param type
     */
    private void goToAuth(int type) {
        if (type == 3) {
            UMShareAPI.get(this).doOauthVerify(this, platforms.get(type).mPlatform, authListener);
        } else {
            UMShareAPI.get(this).getPlatformInfo(this, platforms.get(type).mPlatform, authListener);
        }
    }

    /**
     * 实现第三方登录初始化
     */

    private void init3rdPlatforms() {
        platforms.clear();
        for (SHARE_MEDIA e : thirdLoginList) {
            if (!e.toString().equals(SHARE_MEDIA.GENERIC.toString())) {
                platforms.add(e.toSnsPlatform());
            }
        }
    }

    /**
     * 绑定三方账号
     *
     * @param thirdLoginID   第三方登录id
     * @param thirdLoginName 第三方登录用户名
     * @param thirdLoginType 第三方登录类型
     * @param username       本账号的账号
     */
    private void bindThirdAccount(String thirdLoginID, String thirdLoginName, int thirdLoginType, String username) {
        showLoadingDialog();
        OkGo.get(Urls.URL + "/login/threelogin/loginphone")
                .params("deviceType", DeviceType.WEB_1)
                .params("macAddress", CommonUtil.getLocalMacAddressFromIp(context))
                .params("thirdLoginID", thirdLoginID)
                .params("thirdLoginName", thirdLoginName)
                .params("thirdLoginType", thirdLoginType)
                .params("username", username)
                .execute(new JsonCallback<IycResponse<BindingInfo>>(context) {
                    @Override
                    public void onSuccess(IycResponse<BindingInfo> bindingInfo, Call call, Response response) {
                        if (isNull(bindingInfo) || isNull(bindingInfo.getData()))
                            return;
                        switch (bindingInfo.getData().getIsSuccess()) {
                            case 0:
                                ToastCompat.makeText(context, "绑定成功", Toast.LENGTH_SHORT).show();
                                getByUserId();
                                break;
                            case 1:
                                ToastCompat.makeText(context, "账户未注册", Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                ToastCompat.makeText(context, "账户已绑定了相同类型的第三方账号", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        dismissLoadingDialig();
                        Logger.i("wzp binding:" + e.getMessage());
                    }

                    @Override
                    public void onAfter(IycResponse<BindingInfo> bindingInfoIycResponse, Exception e) {
                        super.onAfter(bindingInfoIycResponse, e);
                    }
                });
    }

    UMAuthListener authListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {

            SocializeUtils.safeShowDialog(dialog);
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            SocializeUtils.safeCloseDialog(dialog);
            showLoadingDialog();
            for (String in : data.keySet()) {
                //map.keySet()返回的是所有key的值
                String str = data.get(in);//得到每个key多对用value的值
                Logger.e("test", in + "     " + str);
            }
            final String uId;
            if (platform.equals(SHARE_MEDIA.SINA) || platform.equals(SHARE_MEDIA.DOUBAN)) {
                uId = data.get("uid");
            } else {
                uId = data.get("unionid");
            }
            final String name = data.get("name");
            if (name != null) {
                Logger.i("wzp name", name);
            }

            OkGo.get(Urls.BookMarketTestBound)
                    .params("macAddress", CommonUtil.getLocalMacAddressFromIp(context))
                    .params("thirdLoginID", uId)
                    .params("thirdLoginType", unboundType)
                    .execute(new JsonCallback<IycResponse<ThirdLoginReturn>>(context) {
                        @Override
                        public void onSuccess(IycResponse<ThirdLoginReturn> listIycResponse, Call call, Response
                                response) {
                            ThirdLoginReturn userReturn = listIycResponse.getData();
                            if (userReturn.getIsbinding() == 0) {
                                ToastCompat.makeText(MineSettingBindActivity.this, "此第三方账号已绑定其他用户", Toast.LENGTH_LONG).show();
                            } else {
                                if (!isNull(context, mBindList.get(0).getName(), "") || !isNull(context, userPhoneAccount, "")) {
                                    String userName = !isNull(context, userPhoneAccount, "") ? userPhoneAccount : mBindList.get(0).getName();
                                    Logger.i("wzp userName:" + userName);
                                    bindThirdAccount(uId, name, unboundType, userName);
                                }
//                                 Intent intent = new Intent(context, RegisterActivity.class);
//                                intent.putExtra("state", "BINDING");
//                                intent.putExtra("thirdLoginID", uId);
//                                intent.putExtra("thirdLoginType", unboundType + "");
//                                startActivity(intent);
//                                finish();
                            }
                        }

                        @Override
                        public void onAfter(IycResponse<ThirdLoginReturn> thirdLoginReturnIycResponse, Exception e) {
                            super.onAfter(thirdLoginReturnIycResponse, e);
                            dismissLoadingDialig();
                        }
                    });
//            ToastCompat.makeText(context, "成功了", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            SocializeUtils.safeCloseDialog(dialog);
            ToastCompat.makeText(context, "失败：" + t.getMessage(), Toast.LENGTH_LONG).show();
            dismissLoadingDialig();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            SocializeUtils.safeCloseDialog(dialog);
            ToastCompat.makeText(context, "取消了", Toast.LENGTH_LONG).show();
            dismissLoadingDialig();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        UMShareAPI.get(this).onSaveInstanceState(outState);
    }
}
