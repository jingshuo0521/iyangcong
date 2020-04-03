//package com.iyangcong.reader.activity;
//
//import android.content.IntentFilter;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.iyangcong.reader.R;
//import com.iyangcong.reader.ui.button.FlatButton;
//import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
//import com.iyangcong.reader.utils.Constants;
//import com.iyangcong.reader.utils.PayUtils;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//
///**
// * Created by WuZepeng on 2017-05-03.
// */
//
//public class MineMonthlyBookPayActivity extends SwipeBackActivity {
//
//	@BindView(R.id.btnBack)
//	ImageButton btnBack;
//	@BindView(R.id.textHeadTitle)
//	TextView textHeadTitle;
//	@BindView(R.id.btnFunction)
//	ImageButton btnFunction;
//	@BindView(R.id.btnFunction1)
//	ImageButton btnFunction1;
//	@BindView(R.id.layout_header)
//	LinearLayout layoutHeader;
//	@BindView(R.id.mine_pay_chart_image)
//	ImageView minePayChartImage;
//	@BindView(R.id.tv_tip1)
//	TextView tvTip1;
//	@BindView(R.id.tv_book_count)
//	TextView tvBookCount;
//	@BindView(R.id.tv_tip2)
//	TextView tvTip2;
//	@BindView(R.id.tv_price)
//	TextView tvPrice;
//	@BindView(R.id.ti_weixin)
//	FlatButton tiWeixin;
//	@BindView(R.id.ti_alipay)
//	FlatButton tiAlipay;
//	@BindView(R.id.activity_mine_pay)
//	LinearLayout activityMinePay;
//	private String mTitle;
//	private Double mPrice;
//	private int mId;
//	private PayUtils payUtils;
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_mine_pay);
//		ButterKnife.bind(this);
//		setMainHeadView();
//		initView();
//
//	}
//
//	@Override
//	protected void initData(Bundle savedInstanceState) {
//		mTitle = getIntent().getStringExtra(Constants.MONTHLY_BOOK_LIST_NAME);
//		mPrice = getIntent().getDoubleExtra(Constants.MONTHLY_BOOK_SPECIAL_PRICE,0.0);
//		mId = getIntent().getIntExtra(Constants.MONTHLY_BOOK_ID,0);
////		payUtils = new PayUtils(this,mId+"",mPrice,PayUtils.MONTHLY_ORDER);
//		payUtils.initBroadCast();
//	}
//
//	@Override
//	protected void initView() {
//
//	}
//
//	@Override
//	protected void setMainHeadView() {
//		textHeadTitle.setText("购买 " + mTitle);
//		tvBookCount.setVisibility(View.INVISIBLE);
//		tvTip2.setVisibility(View.INVISIBLE);
//		tvTip1.setText("已选择" + mTitle);
//		tvPrice.setText("¥" + mPrice);
//	}
//
//	@OnClick({R.id.btnBack, R.id.ti_weixin, R.id.ti_alipay})
//	public void onClick(View view) {
//		switch (view.getId()) {
//			case R.id.btnBack:
//				finish();
//				break;
//			case R.id.ti_weixin:
//				payUtils.wechatPayProcess();
//				break;
//			case R.id.ti_alipay:
//				payUtils.aliPayProcess();
//				break;
//		}
//	}
//}
