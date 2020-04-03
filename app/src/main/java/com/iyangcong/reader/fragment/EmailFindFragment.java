package com.iyangcong.reader.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.base.BaseFragment;
import com.iyangcong.reader.bean.CodeReturn;
import com.iyangcong.reader.bean.RegisterReturn;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.ui.LimitedEdittext;
import com.iyangcong.reader.ui.button.FlatButton;
import com.iyangcong.reader.utils.PasswordVerifer;
import com.iyangcong.reader.utils.StringUtils;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by sheng on 2017/1/16.
 */

public class EmailFindFragment extends BaseFragment {

    @BindView(R.id.email_find)
    EditText email_find;
    @BindView(R.id.send_code_find_email)
    TextView send_code_find_email;
    @BindView(R.id.code_find_email)
    EditText code_find_email;
    @BindView(R.id.password_1_find_email)
    LimitedEdittext password_1_find_email;
    @BindView(R.id.password_2_find_email)
    LimitedEdittext password_2_find_email;
    @BindView(R.id.button_find_email)
    FlatButton button_find_email;


    private int recLen;
    private Handler handler = new Handler();
    boolean isStop = false;
    private String Code;
    private String lastAccount;
    private int userId;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!isStop) {
                if (recLen > 0) {
                    recLen--;
                    send_code_find_email.setText("（" + recLen + "）秒后重新发送");
                    send_code_find_email.setEnabled(false);
                    send_code_find_email.setTextColor(getResources().getColor(R.color.text_color_hint));
                    handler.postDelayed(this, 1000);
                } else {
                    send_code_find_email.setEnabled(true);
                    send_code_find_email.setTextColor(getResources().getColor(R.color.main_color));
                    send_code_find_email.setText("发送验证码");
                }
            }
        }
    };

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email_find, container, false);
        ButterKnife.bind(this, view);
        password_1_find_email.setTextNumberDownlineTextWatcher(getContext(),getString(R.string.content_not_demand));
        password_2_find_email.setTextNumberDownlineTextWatcher(getContext(),getString(R.string.content_not_demand));
        return view;
    }

    @OnClick({R.id.send_code_find_email, R.id.button_find_email})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_code_find_email:
                if (StringUtils.checkEmaile(email_find.getText().toString())) {
                    lastAccount = email_find.getText().toString();
                    getCodeMethod();
                    ToastCompat.makeText(mContext, "验证码已发送", Toast.LENGTH_SHORT).show();
                    recLen = 60;
                    handler.postDelayed(runnable, 0);
                } else {
                    ToastCompat.makeText(mContext, "请填写标准手机号", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.button_find_email:
                if (!isNull()) {
                    if (email_find.getText().toString().equals(lastAccount)) {
                        if (Code!=null&&Code.equals(code_find_email.getText().toString())) {
                            findPassword();
                        } else {
                            ToastCompat.makeText(mContext, "验证码输入错误！", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ToastCompat.makeText(mContext, "邮箱号号已改变，请重新获取验证码", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ToastCompat.makeText(mContext, "请填写规范信息", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    //填入信息是空
    private boolean isNull() {
        if (email_find.getText().toString().equals("") || code_find_email.getText().toString().equals("") || password_1_find_email.getText().toString().equals("") || password_2_find_email.getText().toString().equals(""))
            return true;
        return false;
    }

    @Override
    protected void initData() {

    }

    //获取验证码接口
    private void getCodeMethod() {
        OkGo.get(Urls.FindPasswordCodeURL)
                .tag(this)
                .params("userName", email_find.getText().toString())
                .execute(new JsonCallback<IycResponse<CodeReturn>>(mContext) {
                    @Override
                    public void onSuccess(IycResponse<CodeReturn> codeReturnIycResponse, Call call, Response response) {
                        Code = codeReturnIycResponse.getData().getCode();
                        userId = codeReturnIycResponse.getData().getUserId();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                       // ToastCompat.makeText(mContext, "一天只能获取三次验证码,请明天再试！", Toast.LENGTH_SHORT).show();
                        ToastCompat.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.i("PhoneRegisterError", response.body().toString());
                    }
                });
    }

    //找回密码接口
    private void findPassword() {
        if (passwordNotEqual()) {
            ToastCompat.makeText(mContext, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
        } else {
            String pwdMD5 = StringUtils.MD5(password_1_find_email.getText().toString());
            OkGo.get(Urls.FindPasswordURL)//
                    .tag(this)//
                    .params("passWord", pwdMD5)
                    .params("userId", userId + "")
                    .execute(new JsonCallback<IycResponse<RegisterReturn>>(mContext) {
                        @Override
                        public void onSuccess(IycResponse<RegisterReturn> listIycResponse, Call call, Response
                                response) {
                            ToastCompat.makeText(mContext, "找回密码成功", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            ToastCompat.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(handler!=null)
            handler.removeCallbacks(runnable);
    }

    //两次输入密码不一致
    private boolean passwordNotEqual() {
        String pswd1 = password_1_find_email.getText().toString();
        String pswd2 = password_2_find_email.getText().toString();
        int minLength = getContext().getResources().getInteger(R.integer.password_min_length);
        return !PasswordVerifer.verifer.isPSWValid(pswd1,pswd2,minLength);
    }
}
