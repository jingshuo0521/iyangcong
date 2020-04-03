package com.iyangcong.reader.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
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
 */

public class CommentEditActivity extends SwipeBackActivity {

    private int bookId;

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
            ToastCompat.makeText(this, "请填写标题", 1000);
        } else if (comment_content.getText().toString().equals("")) {
            ToastCompat.makeText(this, "请填写书评", 1000);
        } else if (comment_rating.getStar() == 0) {
            ToastCompat.makeText(this, "请为该书打分", 1000);
        } else {
            showLoadingDialog();
            sendCommontToNetwork();
        }
    }

    private void sendCommontToNetwork() {
        if (comment_title.getText().toString().equals("")) {
            dismissLoadingDialig();
            ToastCompat.makeText(this, "标题不能为空", 1000);
        } else if (comment_content.getText().toString().equals("")) {
            dismissLoadingDialig();
            ToastCompat.makeText(this, "评论内容不能为空", 1000);
        } else {
            OkGo.post(Urls.BookMarketAddReview)
                    .tag(this)
                    .params("bookid", bookId)
                    .params("draftstatus", "2")
                    .params("grade", "" + (int) comment_rating.getStar() * 2)
                    .params("title", comment_title.getText().toString())
                    .params("userid", CommonUtil.getUserId())
                    .params("contentsize", comment_content.getText().toString().length())
                    .params("content", comment_content.getText().toString())
                    .execute(new JsonCallback<IycResponse<ReViewResult>>(this) {

                        @Override
                        public void onSuccess(IycResponse iycResponse, Call call, Response response) {
                            ToastCompat.makeText(CommentEditActivity.this, "发表书评成功", 1000);
                            dismissLoadingDialig();
                            finish();
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            dismissLoadingDialig();
                            ToastCompat.makeText(CommentEditActivity.this, e.getMessage(), 1000);
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
        bookId = getIntent().getIntExtra("bookId", 0);
    }

    @Override
    protected void initView() {
        setMainHeadView();
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText("发表书评");
        btnFunction.setVisibility(View.VISIBLE);
        btnFunction.setImageResource(R.drawable.ic_send);
        btnBack.setImageResource(R.drawable.btn_back);
    }

    class ReViewResult {
        int reviewId;
    }
}
