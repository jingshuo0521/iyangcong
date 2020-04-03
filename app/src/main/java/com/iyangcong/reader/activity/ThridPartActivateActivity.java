package com.iyangcong.reader.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.MainActivity;
import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.CommonFragmentAdapter;
import com.iyangcong.reader.bean.CodeReturn;
import com.iyangcong.reader.bean.MineAgreement;
import com.iyangcong.reader.bean.RegisterReturn;
import com.iyangcong.reader.bean.ThirdBindingReturn;
import com.iyangcong.reader.bean.ThirdPartBindingReturn;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.fragment.EmailRegisterFragment;
import com.iyangcong.reader.fragment.PhoneRegisterFragment;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.ui.LimitedEdittext;
import com.iyangcong.reader.ui.button.FlatButton;
import com.iyangcong.reader.ui.customtablayout.CommonTabLayout;
import com.iyangcong.reader.ui.customtablayout.listener.CustomTabEntity;
import com.iyangcong.reader.ui.customtablayout.listener.OnTabSelectListener;
import com.iyangcong.reader.ui.customtablayout.listener.TabEntity;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CodeUtils;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.PasswordVerifer;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.StringUtils;
import com.iyangcong.reader.utils.UIHelper;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.IntentUtils.goToWebViewActivity;

/**
 * Created by sheng on 2016/12/26.
 */
public class ThridPartActivateActivity extends SwipeBackActivity {



    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.register_phone_number_or_email)
    EditText register_phone_number;
    @BindView(R.id.activate_send_code)
    TextView send_code;
    @BindView(R.id.activate_code)
    EditText code;
    @BindView(R.id.register_password_1)
    LimitedEdittext register_password_1;
    @BindView(R.id.register_password_2)
    LimitedEdittext register_password_2;
    @BindView(R.id.register_button)
    FlatButton register_button;
    @BindView(R.id.register_password_1_container)
    LinearLayout registerPassword1Container;
    @BindView(R.id.register_password_2_container)
    LinearLayout registerPassword2Container;
    @BindView(R.id.binding_introduction_container)
    LinearLayout bindingIntroductionContainer;
    @BindView(R.id.tv_agreement)
    TextView tvAgreement;
    @BindView(R.id.tv_agreement_dialog)
    TextView tvAgreementDialog;
    @BindView(R.id.ll_agreement)
    LinearLayout llAgreement;
    @BindView(R.id.image)
    ImageView randomImage;
    @BindView(R.id.random_code_et)
    EditText randomCode_et;
    @BindView(R.id.thirdpartfrom)
    TextView thirdpartfrom;
    private String currentState = "";
    private String thirdLoginID = "";
    private int  thirdLoginType ;
    private String keyfrom;

    boolean isStop = false;
    private String Code;
    private int recLen;
    private MineAgreement Agreement = new MineAgreement();
    private Handler handler = new Handler();
    private CodeUtils codeUtils;
    private SharedPreferenceUtil sharedPreferenceUtil;
    boolean isNotRegister = false;
    private String lastAccount="";
    private boolean ifHasIYCAccountButNotBind;
    private boolean ifgetyanzheng;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thirdpartactivate);
        ButterKnife.bind(this);
        initRegisterView();
        initView();
        setMainHeadView();

    }


    @Override
    protected void initData(Bundle savedInstanceState) {

        currentState = getIntent().getStringExtra("state");
        thirdLoginID = getIntent().getStringExtra("thirdLoginID");
        thirdLoginType = getIntent().getIntExtra("thirdLoginType",0);
        keyfrom = getIntent().getStringExtra("keyfrom");
        currentState="BINDING";
       // thirdLoginID="201812071111";
        thirdLoginType=5;
        }

    @Override
    protected void initView() {
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
        if(keyfrom.equals("beiwaionline_xueli")){
            thirdpartfrom.setText(R.string.thirdpart_beiwaionline_xueli);
        }

    }


    public void setAgreement(MineAgreement agreement) {
        Agreement = agreement;
    }

    @OnClick({R.id.activate_send_code, R.id.register_button, R.id.tv_agreement_dialog,R.id.image,R.id.btnBack})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                Intent intent = new Intent(ThridPartActivateActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.activate_send_code:
                int accountType=getAccountType(register_phone_number.getText().toString());
                if(accountType==0){
                    Toast.makeText(context, "请填写标准手机号或邮箱号", Toast.LENGTH_LONG).show();
                }else{
                    lastAccount = register_phone_number.getText().toString();

                    String code = codeUtils.getCode();
                    Log.e("code", code);
                    String codeStr=randomCode_et.getText().toString().trim();
                    if (code.equalsIgnoreCase(codeStr)) {
//                        if ("BINDING".equals(((RegisterActivity) getActivity()).getCurrentState())) {
//                            showLoadingDialog();
//                            getCodeMethod();
//                        } else {
                            isUserRegisted();
//                        }

                    } else {
                        if("".equals(codeStr)){
                            Toast.makeText(context, "图片验证码不能为空", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(context, "图片验证码错误", Toast.LENGTH_LONG).show();
                        }

                    }
                }

                break;

            case R.id.register_button:
                if (!isNull()) {
                   if (register_phone_number.getText().toString().equals(lastAccount)) {

                      // bindAndRegister();
                       doActivate();

                    } else {
                        ToastCompat.makeText(context, "手机号已改变，请重新获取验证码", 1000);
                    }
                } else {
                    ToastCompat.makeText(context, "请填写规范信息", 1000);
                }
                break;
            case R.id.tv_agreement_dialog:
                String url = Urls.URL + "/onion/user_protocol.html";
                goToWebViewActivity(ThridPartActivateActivity.this, url);
                break;
            case R.id.image:
                changeRandomCode();
                break;
            default:break;
        }
    }

    private void doActivate(){

        if(ifgetyanzheng){
            if (isCodeNotSame(Code)) {
                Toast.makeText(context, "验证码不正确", Toast.LENGTH_LONG).show();
            } else if (passwordNotEqual()) {
                Toast.makeText(context, "两次输入密码不一致", Toast.LENGTH_LONG).show();
            }else{
                if(ifHasIYCAccountButNotBind){
                    doBindAction();
                }else{
                    goBindAndRegister();
                }
            }
        }else{
            ToastCompat.makeText(context, "点周获取验证码", 1000);
        }
    }

    private void isUserRegisted() {
        showLoadingDialog();
        OkGo.get(Urls.IsUserRegistURL)
                .tag(this)
                .params("userName", register_phone_number.getText().toString())
                .execute(new JsonCallback<IycResponse<String>>(context) {
                    @Override
                    public void onSuccess(IycResponse<String> iycResponse, Call call, Response response) {
                        ifHasIYCAccountButNotBind=false;
                        ifgetyanzheng=true;
                        getCodeMethod();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dismissLoadingDialig();
                        if (e.getMessage().contains("该账号已存在，不能继续注册")) {
                            //ToastCompat.makeText(context, "该账号已存在，不能继续注册", Toast.LENGTH_LONG).show();
                            ifHasIYCAccountButNotBind=true;
                            ifgetyanzheng=true;
                            getCodeMethod();
                        }
                        if(response.body()!=null)
                            Log.i("PhoneRegisterError", response.body().toString());
                    }
                });
    }

    //绑定未注册账号登录
    private void goBindAndRegister() {
       String macaddress= CommonUtil.getLocalMacAddressFromIp(ThridPartActivateActivity.this);

        if (!UIHelper.isNetAvailable(context)) {
            ToastCompat.makeText(context, "网络不稳定，请稍后再试", Toast.LENGTH_SHORT);
            return;
        }

            String pwdMD5 = StringUtils.MD5(register_password_1.getText().toString());
            showLoadingDialog();
            OkGo.get(Urls.ThirdPartBoundNotRegister)
                    .tag(this)
                    .params("password", pwdMD5)
                    .params("macAddress", macaddress)
                    .params("thirdLoginID", thirdLoginID)
                    .params("thirdLoginType", thirdLoginType)
                    .params("username", register_phone_number.getText().toString())
                    .params("deviceType",3)
                    .execute(new JsonCallback<IycResponse<ThirdPartBindingReturn>>(context) {

                        @Override
                        public void onSuccess(IycResponse<ThirdPartBindingReturn> thirdBindingReturnIycResponse, Call call, Response response) {
                            dismissLoadingDialig();
                            ThirdPartBindingReturn userReturn = thirdBindingReturnIycResponse.getData();
                            if (userReturn.getIsSuccess() == 0) {
                                ToastCompat.makeText(context, "激活成功", 1000);
                                Intent intent = new Intent(context, MainActivity.class);
                                sharedPreferenceUtil.putLong(SharedPreferenceUtil.USER_ID, userReturn.getUserId());
                                sharedPreferenceUtil.putBoolean(SharedPreferenceUtil.LOGIN_STATE, true);
                                sharedPreferenceUtil.putString(SharedPreferenceUtil.LOGIN_USER_ACCOUNT, register_phone_number.getText().toString());
                                sharedPreferenceUtil.putInt(SharedPreferenceUtil.LOGIN_TYPE,thirdLoginType);
                                sharedPreferenceUtil.putInt(SharedPreferenceUtil.USER_TYPE, userReturn.getUserType());
                                sharedPreferenceUtil.putString(SharedPreferenceUtil.SEMESTER_NAME,userReturn.getSemesterName());
                                sharedPreferenceUtil.putInt(SharedPreferenceUtil.SHOW_TYPE,5);
                                startActivity(intent);
                                finish();
                            } else if (userReturn.getIsSuccess() == 1) {
                                ToastCompat.makeText(context, "激活失败", 1000);
                            }
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            dismissLoadingDialig();
                            ToastCompat.makeText(context, "激活失败", 1000);
                        }
                    });

    }
    //绑定接口
    private void doBindAction() {
            String pwd=StringUtils.MD5(register_password_1.getText().toString());
            showLoadingDialog();
            OkGo.get(Urls.ThirdPartBindLogin)
                    .tag(this)//
                    .params("username", register_phone_number.getText().toString())
                    .params("deviceType", "3")
                    .params("macAddress", CommonUtil.getLocalMacAddressFromIp(context))
                    .params("thirdLoginID", thirdLoginID)
                    .params("thirdLoginType", thirdLoginType)
                    .params("password",pwd)
                    .execute(new JsonCallback<IycResponse<ThirdPartBindingReturn>>(context) {
                        @Override
                        public void onSuccess(IycResponse<ThirdPartBindingReturn> listIycResponse, Call call, Response
                                response) {
                            dismissLoadingDialig();
                            ThirdPartBindingReturn userReturn = listIycResponse.getData();
                            if (userReturn.getIsSuccess() == 0) {
                                ToastCompat.makeText(context, "激活成功", 1000);
                                Intent intent = new Intent(context, MainActivity.class);
                                sharedPreferenceUtil.putLong(SharedPreferenceUtil.USER_ID, userReturn.getUserId());
                                sharedPreferenceUtil.putBoolean(SharedPreferenceUtil.LOGIN_STATE, true);
                                sharedPreferenceUtil.putString(SharedPreferenceUtil.LOGIN_USER_ACCOUNT, register_phone_number.getText().toString());
                                sharedPreferenceUtil.putInt(SharedPreferenceUtil.LOGIN_TYPE,thirdLoginType);
                                sharedPreferenceUtil.putInt(SharedPreferenceUtil.USER_TYPE, userReturn.getUserType());
                                sharedPreferenceUtil.putString(SharedPreferenceUtil.SEMESTER_NAME,userReturn.getSemesterName());
                                sharedPreferenceUtil.putInt(SharedPreferenceUtil.SHOW_TYPE,5);
                                startActivity(intent);
                                finish();
                            } else if (userReturn.getIsSuccess() == 2) {
                                ToastCompat.makeText(context, "账户已绑定了相同类型的第三方账号", 1000);
                            } else if (userReturn.getIsSuccess() == 1) {
                                ToastCompat.makeText(context, "账户未注册", 1000);
                                isNotRegister = true;
                                initRegisterView();
                            }
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            dismissLoadingDialig();
                            ToastCompat.makeText(context, e.getMessage(), 1000);
//                            if (e.getMessage().contains("此账号已绑定")) {
//                                ToastCompat.makeText(context, "此账号已经与其他用户进行绑定，请尝试使用其他账号进行绑定", 1000);
//                            }else {
//                                ToastCompat.makeText(context, e.getMessage(), 1000);
//                            }
                        }
                    });

    }


    //获取验证码接口
    private void getCodeMethod() {

        OkGo.get(Urls.SendcodeURL)
                .tag(this)
                .params("userName", register_phone_number.getText().toString())
                .execute(new JsonCallback<IycResponse<CodeReturn>>(context) {
                    @Override
                    public void onSuccess(IycResponse<CodeReturn> codeReturnIycResponse, Call call, Response response) {
                        dismissLoadingDialig();
                        ToastCompat.makeText(context, "验证码已发送", Toast.LENGTH_LONG).show();
                        recLen = 60;
                        handler.postDelayed(runnable, 0);
                        Code = codeReturnIycResponse.getData().getCode();
                        changeRandomCode();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        try{
                            dismissLoadingDialig();
                            ToastCompat.makeText(context, e.getMessage(), 1000);
                            Log.i("PhoneRegisterError", response.body().toString());
                        }catch(Exception ee){

                        }

                    }
                });
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText("账号激活");
        btnBack.setImageResource(R.drawable.btn_back);
        changeRandomCode();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        finish();
    }

    private void changeRandomCode(){
        Bitmap bitmap = codeUtils.createBitmap();
        randomImage.setImageBitmap(bitmap);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!isStop) {
                if (recLen > 0) {
                    recLen--;
                    send_code.setText("（" + recLen + "）秒后重新发送");
                    send_code.setEnabled(false);
                    send_code.setTextColor(getResources().getColor(R.color.text_color_hint));
                    handler.postDelayed(this, 1000);
                } else {
                    send_code.setEnabled(true);
                    send_code.setTextColor(getResources().getColor(R.color.main_color));
                    send_code.setText("发送验证码");
                    changeRandomCode();
                }
            }
        }
    };

    //验证码不正确
    private boolean isCodeNotSame(String sendCode) {
        if (code.getText().toString().equals(sendCode))
            return false;
        return true;
    }

    //两次输入密码不一致
    private boolean passwordNotEqual() {
        String pswd1= register_password_1.getText().toString();
        String pswd2 = register_password_2.getText().toString();
        int minLength = context.getResources().getInteger(R.integer.password_min_length);
        return !PasswordVerifer.verifer.isPSWValid(pswd1,pswd2,minLength);
    }

    //填入信息是空
    private boolean isNull() {
        if (register_phone_number.getText().toString().equals("") || code.getText().toString().equals("")/* || register_password_1.getText().toString().equals("") || register_password_2.getText().toString().equals("")*/)
            return true;
        return false;
    }

    private void initRegisterView() {
        registerPassword1Container.setVisibility(View.VISIBLE);
        registerPassword2Container.setVisibility(View.VISIBLE);
        register_button.setText("激活");
        bindingIntroductionContainer.setVisibility(View.GONE);
        llAgreement.setVisibility(View.VISIBLE);
        //shao add being
        codeUtils = CodeUtils.getInstance();
        changeRandomCode();
        //shao add end
    }


    private int getAccountType(String account){
        if(account.length()>0){
            if(StringUtils.isMobileNO(account)){
                return 1;
            }
            if(StringUtils.checkEmaile(account)){
                return 2;
            }
        }
        return 0;
    }



}
