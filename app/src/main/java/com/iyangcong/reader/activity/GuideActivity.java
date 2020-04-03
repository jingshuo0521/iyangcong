package com.iyangcong.reader.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.iyangcong.reader.MainActivity;
import com.iyangcong.reader.R;
import com.iyangcong.reader.base.BaseActivity;
import com.iyangcong.reader.bean.ThirdBindingReturn;
import com.iyangcong.reader.bean.ThirdLoginReturn;
import com.iyangcong.reader.bean.ThirdPartBindingReturn;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.PermissionsChecker;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.utils.SocializeUtils;

import java.util.Map;

import me.drakeet.support.toast.ToastCompat;
import me.relex.circleindicator.CircleIndicator;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

public class GuideActivity extends BaseActivity {




    private static final int REQUEST_CODE = 0; // 请求码
    private final int LOGINTYPE_WEIXIN =1;

    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            PermissionsChecker.PERMISSION_READ_PHONE_STATE,
            PermissionsChecker.PERMISSION_READ_EXTERNAL_STORAGE,
            PermissionsChecker.PERMISSION_WRITE_EXTERNAL_STORAGE,
            PermissionsChecker.PERMISSION_CAMERA
    };

    private PermissionsChecker mPermissionsChecker; // 权限检测器
    private Intent mIntent;
    private String keyfrom;
    private String mThirdPartUserId;//来自第三方的用户ID
    private String bookId;
    private int unboundType=5;
    private String userName;
    SharedPreferenceUtil sharedPreferenceUtil = SharedPreferenceUtil.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mPermissionsChecker = new PermissionsChecker(this);
        Bundle mBundle = this.getIntent().getExtras();
        if(mBundle!=null){
           // keyfrom=mIntent.getStringExtra("keyfrom");
            keyfrom = mBundle.getString("keyfrom");
            mThirdPartUserId=mBundle.getString("userId");
            userName=mBundle.getString("userName");
            bookId=mBundle.getString("bookId","");

            if(keyfrom.equals("beiwaionline_xueli")){
                unboundType=5;
            }
            //Toast.makeText(this,keyfrom,Toast.LENGTH_LONG).show();
            boolean loginState = CommonUtil.getLoginState();
           // loginState = true;
            if(loginState){
                final long userId=CommonUtil.getUserId();
                OkGo.get(Urls.BookMarketTestBound)
                        .params("macAddress", CommonUtil.getLocalMacAddressFromIp(GuideActivity.this))
                        .params("thirdLoginID", mThirdPartUserId)
                        .params("thirdLoginType", unboundType)
                        .execute(new JsonCallback<IycResponse<ThirdLoginReturn>>(GuideActivity.this) {
                            @Override
                            public void onSuccess(IycResponse<ThirdLoginReturn> listIycResponse, Call call, Response
                                    response) {
                                ThirdLoginReturn userReturn = listIycResponse.getData();
                                if (userReturn.getIsbinding() == 0) {
                                    //ToastCompat.makeText(GuideActivity.this, "此账号已绑定其他用户", Toast.LENGTH_LONG).show();
                                    if(userReturn.getUserId()==userId){
                                        //当前的账号就是要绑定的账号，直接跳到书城界面
                                        Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                                        sharedPreferenceUtil.putInt(SharedPreferenceUtil.SHOW_TYPE,5);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("bookId",bookId);
                                        bundle.putInt("keyfrom", Constants.THIRDPART_TYPE_BFSU);
                                        bundle.putInt("redirectionType", 0);
                                        intent.putExtras(bundle);
                                        intent.setAction("ToBookDetail");
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        //如果当前的账号不是绑定的账号，则退出当前账号，跳到登录界面重新登录。
                                        GuideActivity.this.Logout(false);
                                        Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("bookId",bookId);
                                        bundle.putInt("keyfrom", Constants.THIRDPART_TYPE_BFSU);
                                        intent.setAction("ToBookDetail");
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                        //startActivity(new Intent(GuideActivity.this, LoginActivity.class));
                                        finish();
                                    }
                                } else {
                                    //已登录，未绑定，则进行绑定操作
                                    binding();
                                }
                            }

                            @Override
                            public void onAfter(IycResponse<ThirdLoginReturn> thirdLoginReturnIycResponse, Exception e) {
                                super.onAfter(thirdLoginReturnIycResponse, e);
                               // dismissLoadingDialig();
                            }
                        });

            }else{
                OkGo.get(Urls.BookMarketTestBound)
                        .params("macAddress", CommonUtil.getLocalMacAddressFromIp(GuideActivity.this))
                        .params("thirdLoginID", mThirdPartUserId)
                        .params("thirdLoginType", unboundType)
                        .execute(new JsonCallback<IycResponse<ThirdLoginReturn>>(GuideActivity.this) {
                            @Override
                            public void onSuccess(IycResponse<ThirdLoginReturn> listIycResponse, Call call, Response
                                    response) {
                               dismissLoadingDialig();
                                ThirdLoginReturn userReturn = listIycResponse.getData();
                                if (userReturn.getIsbinding() == 0) {
                                    //未登录，有账号则跳到登录界面
                                    Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
                                    //我们给他添加一个参数表示从apk1传过去的
                                    Bundle bundle = new Bundle();
                                    bundle.putString("bookId",bookId);
                                    bundle.putInt("keyfrom", Constants.THIRDPART_TYPE_BFSU);
                                    intent.putExtras(bundle);
                                    intent.setAction("ToBookDetail");
                                    startActivity(intent);

                                } else {
                                    //未登录，无账号，则跳到激活界面
                                    Intent intent = new Intent(GuideActivity.this, ThridPartActivateActivity.class);
                                    intent.putExtra("state", "BINDING");
                                    intent.putExtra("thirdLoginID", mThirdPartUserId);
                                    intent.putExtra("thirdLoginType", unboundType + "");
                                    intent.putExtra("keyfrom",keyfrom);
                                    intent.putExtra("bookId",bookId);
                                    intent.setAction("ToBookDetail");
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void onAfter(IycResponse<ThirdLoginReturn> thirdLoginReturnIycResponse, Exception e) {
                                super.onAfter(thirdLoginReturnIycResponse, e);
                                 dismissLoadingDialig();
                            }
                        });
            }


       }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setMainHeadView() {

    }

    private boolean getUserLoginState(){

        return true;
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    //shao add begin


    protected void Logout(boolean showToast){
        if(showToast) {
            ToastCompat.makeText(this, "退出登录成功", Toast.LENGTH_LONG).show();
        }

        sharedPreferenceUtil.putBoolean(SharedPreferenceUtil.LOGIN_STATE, false);
        sharedPreferenceUtil.putLong(SharedPreferenceUtil.PRE_USER_ID, 0);
        sharedPreferenceUtil.putString(SharedPreferenceUtil.CLASS_NAMES,"");
        sharedPreferenceUtil.putString(SharedPreferenceUtil.CLASS_IDS,"");
        sharedPreferenceUtil.putString(SharedPreferenceUtil.SEMESTER_ID,"");
        sharedPreferenceUtil.putString(SharedPreferenceUtil.SEMESTER_NAME,"");
        sharedPreferenceUtil.putLong(SharedPreferenceUtil.USER_ID, -1);
        sharedPreferenceUtil.putString(SharedPreferenceUtil.LOGIN_USER_ACCOUNT, "");
        int loginType=sharedPreferenceUtil.getInt(SharedPreferenceUtil.LOGIN_TYPE,0);
        sharedPreferenceUtil.putInt(SharedPreferenceUtil.SHOW_TYPE,0);

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
            //SocializeUtils.safeCloseDialog(dialog);
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
            //Toast.makeText(MineSettingActivity.this, "取消了", Toast.LENGTH_LONG).show();
        }
    };


    //绑定接口
    private void binding() {

        String username =sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_USER_ACCOUNT,"");
        if("".equals(username)){
            return;
        }

            showLoadingDialog();
            OkGo.get(Urls.BookMarketBoundLoginPhone)
                    .tag(this)//
                    .params("username", username)
                    .params("deviceType", "3")
                    .params("macAddress", CommonUtil.getLocalMacAddressFromIp(GuideActivity.this))
                    .params("thirdLoginID", mThirdPartUserId)
                    .params("thirdLoginType", unboundType)
                    .execute(new JsonCallback<IycResponse<ThirdPartBindingReturn>>(context) {
                        @Override
                        public void onSuccess(IycResponse<ThirdPartBindingReturn> listIycResponse, Call call, Response
                                response) {
                            dismissLoadingDialig();
                            ThirdPartBindingReturn userReturn = listIycResponse.getData();
                            if (userReturn.getIsSuccess() == 0) {
                                ToastCompat.makeText(context, "绑定成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, MainActivity.class);
                                sharedPreferenceUtil.putLong(SharedPreferenceUtil.USER_ID, userReturn.getUserId());
                                sharedPreferenceUtil.putBoolean(SharedPreferenceUtil.LOGIN_STATE, true);
                                sharedPreferenceUtil.putInt(SharedPreferenceUtil.LOGIN_TYPE,5);
                                sharedPreferenceUtil.putInt(SharedPreferenceUtil.USER_TYPE, userReturn.getUserType());
                                sharedPreferenceUtil.putString(SharedPreferenceUtil.SEMESTER_NAME,userReturn.getSemesterName());
                                sharedPreferenceUtil.putInt(SharedPreferenceUtil.SHOW_TYPE,5);
                                startActivity(intent);
                                finish();
                            } else if (userReturn.getIsSuccess() == 2) {
                                //登录A账号，切缺切到B账号（未注册），则退出当前账号，重新进行绑定。
                              //  ToastCompat.makeText(context, "账户已绑定了相同类型的第三方账号", 1000);
                                GuideActivity.this.Logout(false);
                                Intent intent = new Intent(GuideActivity.this, ThridPartActivateActivity.class);
                                intent.putExtra("state", "BINDING");
                                intent.putExtra("thirdLoginID", mThirdPartUserId);
                                intent.putExtra("thirdLoginType", unboundType + "");
                                intent.putExtra("keyfrom",keyfrom);
                                intent.putExtra("bookId",bookId);
                                intent.setAction("ToBookDetail");
                                startActivity(intent);
                                finish();
                            } else if (userReturn.getIsSuccess() == 1) {
                                ToastCompat.makeText(context, "账户未注册", Toast.LENGTH_SHORT).show();
//                                isNotRegister = true;
//                                initRegisterView();
                            }
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            dismissLoadingDialig();
                            ToastCompat.makeText(context, "绑定失败", Toast.LENGTH_SHORT).show();
                        }
                    });

    }

}
