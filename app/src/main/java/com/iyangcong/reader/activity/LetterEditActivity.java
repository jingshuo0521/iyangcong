package com.iyangcong.reader.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.RatingBar;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by sheng on 2017/1/17.
 * 发送私信页面
 */

public class LetterEditActivity extends SwipeBackActivity {

    private String toUserId;
    private String toUserName;

    @BindView(R.id.comment_rating)
    RatingBar comment_rating;
    @BindView(R.id.comment_title)
    EditText comment_title;
    @BindView(R.id.comment_content)
    EditText comment_content;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.textNotification)
    TextView notification;

    @OnClick({R.id.btnFunction, R.id.btnBack})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnFunction:
                sendCommont();
                break;
        }
    }

    private void sendCommont() {
        if (comment_title.getText().toString().equals("")) {
            ToastCompat.makeText(this, "请填写标题", Toast.LENGTH_SHORT).show();
        } else if (comment_content.getText().toString().equals("")) {
            ToastCompat.makeText(this, "请填写私信内容", Toast.LENGTH_SHORT).show();
        }  else {
            showLoadingDialog();
            sendCommontToNetwork();
        }
    }

    private void sendCommontToNetwork() {
        if (comment_title.getText().toString().equals("")) {
            dismissLoadingDialig();
            ToastCompat.makeText(this, "标题不能为空", Toast.LENGTH_SHORT).show();
        } else if (comment_content.getText().toString().equals("")) {
            dismissLoadingDialig();
            ToastCompat.makeText(this, "私信内容不能为空", Toast.LENGTH_SHORT).show();
        } else {
            OkGo.post(Urls.SendPrivateLetter)
                    .tag(this)
                    .params("userId", CommonUtil.getUserId())
                    .params("toUserId", toUserId)
                    .params("title", comment_title.getText().toString())
                    .params("body", comment_content.getText().toString())
                    .execute(new JsonCallback<IycResponse<ReViewResult>>(this) {

                        @Override
                        public void onSuccess(IycResponse iycResponse, Call call, Response response) {
                            ToastCompat.makeText(LetterEditActivity.this, "私信已发送", Toast.LENGTH_SHORT).show();
                            dismissLoadingDialig();
                            finish();
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            dismissLoadingDialig();
                            ToastCompat.makeText(LetterEditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_edit);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        toUserId = getIntent().getStringExtra("toUserId");
        toUserName = getIntent().getStringExtra("toUserName");
    }

    @Override
    protected void initView() {
        setMainHeadView();
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText("发送私信");
        comment_content.setHint(getResources().getString(R.string.input_title));
        comment_title.setHint(getResources().getString(R.string.input_content));
        btnFunction.setVisibility(View.VISIBLE);
        comment_rating.setVisibility(View.GONE);
        btnFunction.setImageResource(R.drawable.ic_send);
        btnBack.setImageResource(R.drawable.btn_back);
        notification.setText("收件人："+toUserName);
    }

    class ReViewResult {
        int reviewId;
    }
}
