package com.iyangcong.reader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.dialog.listener.OnBtnClickL;
import com.iyangcong.reader.MainActivity;
import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.LoginActivity;
import com.iyangcong.reader.activity.RegisterActivity;
import com.iyangcong.reader.base.BaseFragment;
import com.iyangcong.reader.bean.CodeReturn;
import com.iyangcong.reader.bean.MineAgreement;
import com.iyangcong.reader.bean.RegisterReturn;
import com.iyangcong.reader.bean.ThirdBindingReturn;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.interfaceset.DeviceType;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.ui.LimitedEdittext;
import com.iyangcong.reader.ui.button.FlatButton;
import com.iyangcong.reader.ui.dialog.AgreementDialog;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.PasswordVerifer;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.StringUtils;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.IntentUtils.goToWebViewActivity;

/**
 * Created by ljw on 2016/12/30.
 */

public class EmailRegisterFragment extends BaseFragment {

    @BindView(R.id.register_email)
    EditText register_email;
    @BindView(R.id.send_code_email)
    TextView send_code_email;
    @BindView(R.id.code_email)
    EditText code_email;
    @BindView(R.id.register_password_email_1)
    LimitedEdittext register_password_email_1;
    @BindView(R.id.register_password_email_2)
    LimitedEdittext register_password_email_2;
    @BindView(R.id.register_button_email)
    FlatButton register_button_email;
    @BindView(R.id.tv_agreement)
    TextView tvAgreement;
    @BindView(R.id.register_password_email_1_container)
    LinearLayout registerPasswordEmail1Container;
    @BindView(R.id.register_password_email_2_container)
    LinearLayout registerPasswordEmail2Container;
    @BindView(R.id.binding_introduction_container)
    LinearLayout bindingIntroductionContainer;
    @BindView(R.id.tv_agreement_dialog)
    TextView tvAgreementDialog;
    @BindView(R.id.ll_agreement)
    LinearLayout llAgreement;

    private String Code;
    private int recLen;
    private BaseAnimatorSet mBasIn;
    private BaseAnimatorSet mBasOut;
    private MineAgreement Agreement = new MineAgreement();
    private Handler handler = new Handler();
    boolean isStop = false;
    boolean isNotRegister = false;
    private String lastAccount;

    private SharedPreferenceUtil sharedPreferenceUtil;

    public MineAgreement getAgreement() {
        return Agreement;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!isStop) {
                if (recLen > 0) {
                    recLen--;
                    send_code_email.setText("（" + recLen + "）秒后重新发送");
                    send_code_email.setEnabled(false);
                    send_code_email.setTextColor(getResources().getColor(R.color.text_color_hint));
                    handler.postDelayed(this, 0);
                } else {
                    send_code_email.setEnabled(true);
                    send_code_email.setTextColor(getResources().getColor(R.color.main_color));
                    send_code_email.setText("发送验证码");
                }
            }
        }
    };

    public void setAgreement(MineAgreement agreement) {
        Agreement = agreement;
    }

    @OnClick({R.id.send_code_email, R.id.register_button_email, R.id.tv_agreement_dialog})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_code_email:
                if (isEmail(register_email.getText().toString())) {
                    lastAccount = register_email.getText().toString();

                    if ("BINDING".equals(((RegisterActivity) getActivity()).getCurrentState())) {
                        showLoadingDialog();
                        getCodeMethod();
                    } else {
                        isUserRegisted();
                    }

//                    getCodeMethod();
//                    Toast.makeText(mContext, "验证码已发送", Toast.LENGTH_LONG).show();
//                    recLen = 60;
//                    handler.postDelayed(runnable, 1000);
                } else {
                    Toast.makeText(mContext, "请填写标准邮箱账号", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.register_button_email:
                if (!isNull()) {
                    if (register_email.getText().toString().equals(lastAccount)) {
                        if ("BINDING".equals(((RegisterActivity) getActivity()).getCurrentState()) && isNotRegister == false) {
                            binding();
                        } else if ("BINDING".equals(((RegisterActivity) getActivity()).getCurrentState()) && isNotRegister == true) {
                            bindAndRegister();
                        } else {
                            register();
                        }
                    } else {
                        ToastCompat.makeText(mContext, "邮箱地址已改变，请重新获取验证码", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ToastCompat.makeText(mContext, "请填写规范信息", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_agreement_dialog:
                String url = Urls.URL + "/onion/user_protocol.html";
                goToWebViewActivity(getActivity(), url);
//                AgreementDialog();
                break;
        }
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email_register, container, false);
        ButterKnife.bind(this, view);
        if ("BINDING".equals(((RegisterActivity) getActivity()).getCurrentState())) {
            initBindingView();
        } else {
            initRegisterView();
        }
        tvAgreement.setMovementMethod(ScrollingMovementMethod.getInstance());
        register_password_email_1.setTextNumberDownlineTextWatcher(this.getContext(),getString(R.string.content_not_demand));
        register_password_email_2.setTextNumberDownlineTextWatcher(this.getContext(),getString(R.string.content_not_demand));
        return view;
    }

    private void initBindingView() {
        registerPasswordEmail1Container.setVisibility(View.GONE);
        registerPasswordEmail2Container.setVisibility(View.GONE);
        register_button_email.setText("绑定");
        bindingIntroductionContainer.setVisibility(View.VISIBLE);
        llAgreement.setVisibility(View.GONE);
    }

    private void initRegisterView() {
        registerPasswordEmail1Container.setVisibility(View.VISIBLE);
        registerPasswordEmail2Container.setVisibility(View.VISIBLE);
        register_button_email.setText("注册");
        bindingIntroductionContainer.setVisibility(View.GONE);
        llAgreement.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
    }

    //获取验证码接口
    private void getCodeMethod() {
        OkGo.get(Urls.SendcodeURL)
                .tag(this)
                .params("userName", register_email.getText().toString())
                .execute(new JsonCallback<IycResponse<CodeReturn>>(mContext) {
                    @Override
                    public void onSuccess(IycResponse<CodeReturn> codeReturnIycResponse, Call call, Response response) {
                        dismissLoadingDialig();
                        ToastCompat.makeText(mContext, "验证码已发送", Toast.LENGTH_SHORT).show();
                        Code = codeReturnIycResponse.getData().getCode();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        ToastCompat.makeText(mContext, "获取验证码失败，请重新获取", Toast.LENGTH_SHORT).show();
                        dismissLoadingDialig();
                    }
                });
    }

    private void isUserRegisted() {
        showLoadingDialog();
        OkGo.get(Urls.IsUserRegistURL)
                .tag(this)
                .params("userName", register_email.getText().toString())
                .execute(new JsonCallback<IycResponse<String>>(mContext) {
                    @Override
                    public void onSuccess(IycResponse<String> iycResponse, Call call, Response response) {
                        getCodeMethod();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dismissLoadingDialig();
                        if (e.getMessage().contains("该账号已存在，不能继续注册")) {
                            ToastCompat.makeText(mContext, "该账号已存在，不能继续注册", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //    绑定接口
    private void binding() {
        if (isCodeNotSame(Code)) {
            Toast.makeText(mContext, "验证码不正确", Toast.LENGTH_LONG).show();
        }
//        else if (passwordNotEqual()) {
//            Toast.mak登录eText(mContext, "两次输入密码不一致", Toast.LENGTH_LONG).show();
//        }
        else {
            showLoadingDialog();
            OkGo.get(Urls.BookMarketBoundLoginPhone)
                    .tag(this)//
                    .params("username", register_email.getText().toString())
                    .params("macAddress", CommonUtil.getLocalMacAddressFromIp(getActivity()))
                    .params("thirdLoginName",((RegisterActivity) getActivity()).getThirdName())
                    .params("thirdLoginID", ((RegisterActivity) getActivity()).getThirdLoginID())
                    .params("thirdLoginType", ((RegisterActivity) getActivity()).getThirdLoginType())
                    .execute(new JsonCallback<IycResponse<ThirdBindingReturn>>(mContext) {
                        @Override
                        public void onSuccess(IycResponse<ThirdBindingReturn> listIycResponse, Call call, Response
                                response) {
                            dismissLoadingDialig();
                            ThirdBindingReturn userReturn = listIycResponse.getData();
                            if (userReturn.getIsSuccess() == 0) {
                                ToastCompat.makeText(mContext, "绑定成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(mContext, MainActivity.class);
                                sharedPreferenceUtil.putLong(SharedPreferenceUtil.USER_ID, userReturn.getUserId());
                                sharedPreferenceUtil.putBoolean(SharedPreferenceUtil.LOGIN_STATE, true);
                                startActivity(intent);
                                getActivity().finish();
                            } else if (userReturn.getIsSuccess() == 2) {
                                ToastCompat.makeText(mContext, "账户已绑定了相同类型的第三方账号", Toast.LENGTH_SHORT).show();
                            } else if (userReturn.getIsSuccess() == 1) {
                                ToastCompat.makeText(mContext, "账户未注册", Toast.LENGTH_SHORT).show();
                                isNotRegister = true;
                                initRegisterView();
                            }
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {

                            dismissLoadingDialig();
                            ToastCompat.makeText(mContext, "绑定失败", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    //    注册接口
    private void register() {
        if (isCodeNotSame(Code)) {
            Toast.makeText(mContext, "验证码不正确", Toast.LENGTH_LONG).show();
        } else if (passwordNotEqual()) {
            Toast.makeText(mContext, "两次输入密码不一致", Toast.LENGTH_LONG).show();
        } else {
            String pwdMD5 = StringUtils.MD5(register_password_email_1.getText().toString());
            String pwdMD5sure = StringUtils.MD5(register_password_email_2.getText().toString());
            showLoadingDialog();
            OkGo.get(Urls.RegisterURL)//
                    .tag(this)//
                    .params("passWord", pwdMD5)
                    .params("passForSure", pwdMD5sure)
                    .params("userName", register_email.getText().toString())
                    .execute(new JsonCallback<IycResponse<RegisterReturn>>(mContext) {
                        @Override
                        public void onSuccess(IycResponse<RegisterReturn> listIycResponse, Call call, Response
                                response) {
                            dismissLoadingDialig();
                            ToastCompat.makeText(mContext, "注册成功", Toast.LENGTH_SHORT).show();
                            if(listIycResponse!=null&&listIycResponse.getData()!=null){
                                RegisterReturn user=listIycResponse.getData();
                                Intent intent = new Intent(mContext, MainActivity.class);
                                sharedPreferenceUtil.putLong(SharedPreferenceUtil.USER_ID, user.getUserId());
                                sharedPreferenceUtil.putBoolean(SharedPreferenceUtil.LOGIN_STATE, true);
                                sharedPreferenceUtil.putInt(SharedPreferenceUtil.SEMESTER_ID, user.getSemesterId());
                                sharedPreferenceUtil.putInt(SharedPreferenceUtil.USER_TYPE, user.getUserType());
                                sharedPreferenceUtil.putInt(SharedPreferenceUtil.LOGIN_TYPE,0);
                                sharedPreferenceUtil.putInt(SharedPreferenceUtil.SEMESTER_ID,user.getSemesterId());
                                sharedPreferenceUtil.putString(SharedPreferenceUtil.SEMESTER_NAME,user.getSemesterName());
                                startActivity(intent);
                                getActivity().finish();
                            }
//                            Intent intent = new Intent(mContext, LoginActivity.class);
//                            startActivity(intent);
//                            getActivity().finish();
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            dismissLoadingDialig();
                            ToastCompat.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    //绑定未注册账号
    private void bindAndRegister() {
        if (isCodeNotSame(Code)) {
            Toast.makeText(mContext, "验证码不正确", Toast.LENGTH_LONG).show();
        } else if (passwordNotEqual()) {
            Toast.makeText(mContext, "两次输入密码不一致", Toast.LENGTH_LONG).show();
        } else {
            String pwdMD5 = StringUtils.MD5(register_password_email_1.getText().toString());
            showLoadingDialog();
            OkGo.get(Urls.BoundNotRegisterURL)
                    .tag(this)
                    .params("password", pwdMD5)
                    .params("macAddress", CommonUtil.getLocalMacAddressFromIp(getActivity()))
                    .params("thirdLoginName",((RegisterActivity) getActivity()).getThirdName())
                    .params("thirdLoginID", ((RegisterActivity) getActivity()).getThirdLoginID())
                    .params("thirdLoginType", ((RegisterActivity) getActivity()).getThirdLoginType())
                    .params("username", register_email.getText().toString())
                    .execute(new JsonCallback<IycResponse<ThirdBindingReturn>>(mContext) {

                        @Override
                        public void onSuccess(IycResponse<ThirdBindingReturn> thirdBindingReturnIycResponse, Call call, Response response) {
                            dismissLoadingDialig();
                            ThirdBindingReturn userReturn = thirdBindingReturnIycResponse.getData();
                            if (userReturn.getIsSuccess() == 0) {
                                ToastCompat.makeText(mContext, "绑定成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(mContext, MainActivity.class);
                                sharedPreferenceUtil.putLong(SharedPreferenceUtil.USER_ID, thirdBindingReturnIycResponse.getData().getUserId());
                                sharedPreferenceUtil.putBoolean(SharedPreferenceUtil.LOGIN_STATE, true);
                                startActivity(intent);
                                getActivity().finish();
//                                Intent intent = new Intent(mContext, LoginActivity.class);
//                                startActivity(intent);
//                                getActivity().finish();
                            } else if (userReturn.getIsSuccess() == 1) {
                                ToastCompat.makeText(mContext, "绑定失败", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            dismissLoadingDialig();
                            ToastCompat.makeText(mContext, "绑定失败", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    //填入信息是空
    private boolean isNull() {
        if (register_email.getText().toString().equals("") || code_email.getText().toString().equals("") /*|| register_password_email_1.getText().toString().equals("") || register_password_email_2.getText().toString().equals("")*/)
            return true;
        return false;
    }

    //验证码不正确
    private boolean isCodeNotSame(String sendCode) {
        if (code_email.getText().toString().equals(sendCode))
            return false;
        return true;
    }

    //两次输入密码不一致
    private boolean passwordNotEqual() {
        String pswd1 = register_password_email_1.getText().toString();
        String pswd2 = register_password_email_2.getText().toString();
        int minLength = getContext().getResources().getInteger(R.integer.password_min_length);
        return !PasswordVerifer.verifer.isPSWValid(pswd1,pswd2,minLength);
    }

    //判断email格式是否正确
    public boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        getDatasFromNetwork();
        return rootView;
    }

    private void AgreementDialog() {
        final AgreementDialog dialog = new AgreementDialog(mContext);
        dialog.content(Agreement.getUserAgreement())
                .titleTextSize(20)//
                .btnNum(1)
                .btnText("确定")
                .title("用户协议")
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .show();

        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                });

    }

    private void getDatasFromNetwork() {
        OkGo.get(Urls.PersonUserAgreementURL)
                .tag(this)
                .execute(new JsonCallback<IycResponse<MineAgreement>>(mContext) {
                    @Override
                    public void onSuccess(IycResponse<MineAgreement> mineAgreementIycResponse, Call call, Response response) {
                        setAgreement(mineAgreementIycResponse.getData());
                        Logger.i("darkflame" + Agreement.getUserAgreement());
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {

                        if (response != null)
                            Logger.i(TAG + "linjunwei" + response.body().toString());
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        isStop = true;
    }
}
