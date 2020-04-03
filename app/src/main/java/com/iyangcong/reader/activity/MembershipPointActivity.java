package com.iyangcong.reader.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.iyangcong.reader.R;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.ClearEditText;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.DialogUtils;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

public class MembershipPointActivity extends SwipeBackActivity implements ClearEditText.ClearListener {

    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.point_delete)
    ClearEditText point_delete;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;

    @OnClick({R.id.btnBack, R.id.btnFunction, R.id.mine_point_btn})
    void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnFunction:
                break;
            case R.id.mine_point_btn:
                getDatasFromNetwork();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_membershippoint);
        ButterKnife.bind(this);
        setMainHeadView();
        initView();
       // testdata();
    }




    protected void initView() {
        point_delete.setClearListener(this);
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText("积分充值");
        btnBack.setImageResource(R.drawable.btn_back);
    }

    @Override
    public void clear() {
        point_delete.setText("");
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    private int count = 0;

    private void getDatasFromNetwork() {
        String exCode;
        exCode = point_delete.getText().toString();
        if (exCode.equals("")) {
            showDialog("请输入充值兑换码...");
//            ToastCompat.makeText(context, "请输入兑换码...", Toast.LENGTH_SHORT).show();
        } else if (exCode.length() < 14) {
            showDialog("充值兑换码长度有误，请输入正确的兑换码");
//            ToastCompat.makeText(context, "兑换码长度有误，请输入正确的兑换码", Toast.LENGTH_SHORT).show();
        } else {
            showLoadingDialog();
            OkGo.get(Urls.MembershipPointURL)
                    .tag(this)
                    .params("userId", CommonUtil.getUserId())
                    .params("cardPass", exCode)
                    .execute(new JsonCallback<IycResponse<String>>(this) {
                        @Override
                        public void onSuccess(IycResponse<String> stringIycResponse, Call call, Response response) {
                            dismissLoadingDialig();
                            showDialog("充值成功，积分已存入个人积分中");
//                            ToastCompat.makeText(context, "兑换成功，已放入我的图书", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            dismissLoadingDialig();
                            //showDialog(e.getMessage());
                            ToastCompat.makeText(MembershipPointActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                            ToastCompat.makeText(context, "兑换码错误或已使用，兑换失败！", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void parseError(Call call, Exception e) {
                            super.parseError(call, e);
                            if (count < 3) {
                                getDatasFromNetwork();
                                count++;
                            } else {
                                dismissLoadingDialig();
                            }
                        }
                    });
        }
    }

    private void showDialog(String context) {

        final NormalDialog normalDialog = new NormalDialog(this);
        DialogUtils.setAlertDialogOneButtonStyle(normalDialog, getResources().getString(R.string.tips), context);
        normalDialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                normalDialog.dismiss();
            }
        });
    }

}

