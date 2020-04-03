package com.iyangcong.reader.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.base.BaseFragment;
import com.iyangcong.reader.bean.CodeReturn;
import com.iyangcong.reader.bean.RegisterReturn;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.ui.button.FlatButton;
import com.iyangcong.reader.utils.CodeUtils;
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


public class PhoneFindFragment extends BaseFragment {

    @BindView(R.id.phone_number_find)
    EditText phone_number_find;
    @BindView(R.id.send_code_find)
    TextView send_code_find;
    @BindView(R.id.code_find)
    EditText code_find;
    @BindView(R.id.password_1_find)
    EditText password_1_find;
    @BindView(R.id.password_2_find)
    EditText password_2_find;
    @BindView(R.id.button_find)
    FlatButton button_find;
    @BindView(R.id.image)
    ImageView randomImage;
    @BindView(R.id.random_code_et)
    EditText randomCode_et;

    private int recLen;
    private Handler handler = new Handler();
    boolean isStop = false;
    private String Code;
    private String lastAccount;
    private int userId;
    private CodeUtils codeUtils;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!isStop) {
                if (recLen > 0) {
                    recLen--;
                    send_code_find.setText("（" + recLen + "）秒后重新发送");
                    send_code_find.setEnabled(false);
                    send_code_find.setTextColor(mContext.getResources().getColor(R.color.text_color_hint));
                    handler.postDelayed(this, 1000);
                } else {
                    send_code_find.setEnabled(true);
                    send_code_find.setTextColor(mContext.getResources().getColor(R.color.main_color));
                    send_code_find.setText("发送验证码");
                    changeRandomCode();
                }
            }
        }
    };

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone_find, container, false);
        ButterKnife.bind(this, view);
        //shao add being
        codeUtils = CodeUtils.getInstance();
        changeRandomCode();
        //shao add end
        return view;
    }

    @OnClick({R.id.send_code_find, R.id.button_find,R.id.image})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_code_find:
                if (StringUtils.isMobileNO(phone_number_find.getText().toString())) {
                    String code = codeUtils.getCode();
                    Log.e("code", code);
                    String codeStr=randomCode_et.getText().toString().trim();
                    if (code.equalsIgnoreCase(codeStr)) {
                        lastAccount = phone_number_find.getText().toString();
                        getCodeMethod();
                        ToastCompat.makeText(mContext, "验证码已发送", Toast.LENGTH_SHORT).show();
                        recLen = 60;
                        handler.postDelayed(runnable, 0);
                       // changeRandomCode();
                    }else{
                        if("".equals(codeStr)){
                            Toast.makeText(mContext, "图片验证码不能为空", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(mContext, "图片验证码错误", Toast.LENGTH_LONG).show();
                        }
                    }
                 } else {
                    ToastCompat.makeText(mContext, "请填写标准手机号", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.button_find:
                if (!isNull()) {
                    if (phone_number_find.getText().toString().equals(lastAccount)) {
                        if (Code.equals(code_find.getText().toString())) {
                            findPassword();
                        } else {
                            ToastCompat.makeText(mContext, "验证码输入错误！", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ToastCompat.makeText(mContext, "手机号已改变，请重新获取验证码", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ToastCompat.makeText(mContext, "请填写规范信息", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.image:
                changeRandomCode();
                break;
            default:break;


        }
    }

    //填入信息是空
    private boolean isNull() {
        if (phone_number_find.getText().toString().equals("") || code_find.getText().toString().equals("") || password_1_find.getText().toString().equals("") || password_2_find.getText().toString().equals(""))
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
                .params("userName", phone_number_find.getText().toString())
                .execute(new JsonCallback<IycResponse<CodeReturn>>(mContext) {
                    @Override
                    public void onSuccess(IycResponse<CodeReturn> codeReturnIycResponse, Call call, Response response) {
                        Code = codeReturnIycResponse.getData().getCode();
                        userId = codeReturnIycResponse.getData().getUserId();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {

                       // ToastCompat.makeText(mContext, "一天只能获取三次验证码,请明天再试！", 1000);
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
            String pwdMD5 = StringUtils.MD5(password_1_find.getText().toString());
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

    //两次输入密码不一致
    private boolean passwordNotEqual() {
        String pswd1 = password_1_find.getText().toString();
        String pswd2 = password_2_find.getText().toString();
        int minLength = getContext().getResources().getInteger(R.integer.password_min_length);
        return !PasswordVerifer.verifer.isPSWValid(pswd1,pswd2,minLength);
    }

    private void changeRandomCode(){
        Bitmap bitmap = codeUtils.createBitmap();
        randomImage.setImageBitmap(bitmap);
    }

}
