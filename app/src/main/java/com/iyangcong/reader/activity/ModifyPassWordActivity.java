package com.iyangcong.reader.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.RegisterReturn;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.ui.LimitedEdittext;
import com.iyangcong.reader.ui.button.FlatButton;
import com.iyangcong.reader.ui.customtablayout.listener.CustomTabEntity;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.PasswordVerifer;
import com.iyangcong.reader.utils.StringUtils;
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

/**
 * Created by sheng on 2017/1/16.
 */

public class ModifyPassWordActivity extends SwipeBackActivity {
    @BindView(R.id.code_find)
    EditText code_find;
    @BindView(R.id.password_1_find)
    LimitedEdittext password_1_find;
    @BindView(R.id.password_2_find)
    LimitedEdittext password_2_find;
    @BindView(R.id.button_find)
    FlatButton button_find;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnBack)
    ImageButton btnBack;

    private ArrayList<CustomTabEntity> tabTitles = new ArrayList<>();
    private List<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        ButterKnife.bind(this);
        initView();
        setMainHeadView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initView() {
        textHeadTitle.setText("修改密码");
        btnBack.setImageResource(R.drawable.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        password_1_find.setTextNumberDownlineTextWatcher(this,getString(R.string.content_not_demand));
        password_2_find.setTextNumberDownlineTextWatcher(this,getString(R.string.content_not_demand));
    }

    @Override
    protected void setMainHeadView() {

    }


    @OnClick({R.id.button_find})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.button_find:
                if (!isNull()) {
                    findPassword();
                } else {
                    ToastCompat.makeText(this, "请填写规范信息", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    //填入信息是空
    private boolean isNull() {
        if (code_find.getText().toString().equals("") || password_1_find.getText().toString().equals("") || password_2_find.getText().toString().equals(""))
            return true;
        return false;
    }


    //修改密码接口
    private void findPassword() {
        if (passwordNotEqual()) {
            ToastCompat.makeText(this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
        } else {
            String pwdMD5 = StringUtils.MD5(password_1_find.getText().toString());
            String pwdOldMD5 = StringUtils.MD5(code_find.getText().toString());
            OkGo.get(Urls.ModifyPasswordURL)//
                    .tag(this)//
                    .params("passWord", pwdMD5)
                    .params("oldPassWord",pwdOldMD5)
                    .params("userId", CommonUtil.getUserId())
                    .execute(new JsonCallback<IycResponse<RegisterReturn>>(ModifyPassWordActivity.this) {
                        @Override
                        public void onSuccess(IycResponse<RegisterReturn> listIycResponse, Call call, Response
                                response) {
                            ToastCompat.makeText(ModifyPassWordActivity.this, "修改密码成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            ToastCompat.makeText(ModifyPassWordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    //两次输入密码不一致
    private boolean passwordNotEqual() {
        String pswd1 = password_1_find.getText().toString();
        String pswd2 = password_2_find.getText().toString();
        int minLength = getResources().getInteger(R.integer.password_min_length);
        return !PasswordVerifer.verifer.isPSWValid(pswd1,pswd2,minLength);
    }

}