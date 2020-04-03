package com.iyangcong.reader.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.LimitedEdittext;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

public class ReplyPrivateMessageActivity extends SwipeBackActivity {

	@BindView(R.id.btnBack)
	ImageButton btnBack;
	@BindView(R.id.textHeadTitle)
	TextView textHeadTitle;
	@BindView(R.id.btnFunction)
	ImageButton btnFunction;
	@BindView(R.id.tv_goods_num)
	TextView tvGoodsNum;
	@BindView(R.id.btnFunction1)
	ImageButton btnFunction1;
	@BindView(R.id.tv_goods_num1)
	TextView tvGoodsNum1;
	@BindView(R.id.layout_header)
	LinearLayout layoutHeader;
	@BindView(R.id.et_private_message_title)
	LimitedEdittext etPrivateMessageTitle;
	@BindView(R.id.et_private_message_context)
	LimitedEdittext etPrivateMessageContext;

	private long toUserId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reply_private_message);
		ButterKnife.bind(this);
		setMainHeadView();
		initView();
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		toUserId = getIntent().getLongExtra(Constants.TO_USER_ID,-1);
		Logger.i("wzp toUserId:" + toUserId);
	}

	@Override
	protected void initView() {
		etPrivateMessageTitle.setTextWatcher(context,20,true,getString(R.string.content_toolong),getString(R.string.content_no_emoji));
		etPrivateMessageContext.setTextWatcher(context,400,getString(R.string.content_toolong));
	}

	@Override
	protected void setMainHeadView() {
		btnBack.setImageResource(R.drawable.btn_back);
		btnBack.setVisibility(View.VISIBLE);
		textHeadTitle.setText("发私信");
		btnFunction.setImageResource(R.drawable.ic_send);
		btnFunction.setVisibility(View.VISIBLE);
	}

	@OnClick({R.id.btnBack, R.id.btnFunction})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btnBack:
				finish();
				break;
			case R.id.btnFunction:
				sendPrivateMessageToSB(etPrivateMessageTitle.getText().toString(),etPrivateMessageContext.getText().toString(),toUserId);
				break;
		}
	}

	private void sendPrivateMessageToSB(String title, String body, long toUserId){
		if(isNull(context,title,"标题不得为空")||isNull(context,body,"内容不得为空")){
			return;
		}
		if(toUserId == 0){
			Logger.e("wzp 接收私信的用户的id=" +toUserId);
			return;
		}
		if(!CommonUtil.getLoginState()){
			Logger.e("wzp 还没登陆");
			return;
		}
		long userId = CommonUtil.getUserId();
		OkGo.get(Urls.URL + "/personCenter/writeMessageToSomebody")
				.params("title",title)
				.params("body",body)
				.params("toUserId",toUserId)
				.params("userId",userId)
				.execute(new StringCallback() {
					@Override
					public void onSuccess(String s, Call call, Response response) {
						Logger.i("wzp 成功");
						finish();
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
						Logger.e("wzp " + e.getMessage());
					}
				});
	}
}
