package com.iyangcong.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.ui.button.FlatButton;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.PayUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MinePayActivity extends SwipeBackActivity {

    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.mine_pay_chart_image)
    ImageView minePayChartImage;
    @BindView(R.id.tv_book_count)
    TextView tvBookCount;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.ti_alipay)
    FlatButton tiAlipay;
    @BindView(R.id.ti_weixin)
    FlatButton tiWeixin;
    @BindView(R.id.tv_tip1)
    TextView tvTip1;
    @BindView(R.id.tv_tip2)
    TextView tvTip2;

    private String bookIds;
    private int count;
    private double price;
    private String orderTitle = "";
    private PayUtils payUtils;


    private String mTitle;
    private int mId;

    /**
     * true: 包月购买     false:正常购买
     */
    private boolean isMonthBook = false;
    private String priceString;

    @OnClick({R.id.btnBack, R.id.btnFunction, R.id.ti_weixin, R.id.ti_alipay})
    void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnFunction:
                break;
            case R.id.ti_weixin:
                changeButtonBackground(false);
                payUtils.wechatPayProcess();
                break;
            case R.id.ti_alipay:
                changeButtonBackground(false);
                payUtils.aliPayProcess();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_pay);
        ButterKnife.bind(this);
        setMainHeadView();
        initView();

    }

    public void changeButtonBackground(boolean isClickable) {
        if (isClickable) {
//            tiAlipay.setBackgroundColor(getResources().getColor(R.color.blue2));
//            tiAlipay.setButtonColor(R.color.blue2);
//            tiWeixin.setBackgroundColor(getResources().getColor(R.color.green2));
            tiWeixin.setClickable(true);
            tiAlipay.setClickable(true);
        } else {
//            tiAlipay.setBackgroundColor(getResources().getColor(R.color.gray));
//            tiWeixin.setBackgroundColor(getResources().getColor(R.color.gray));
//            tiAlipay.setButtonColor(R.color.gray);
//            tiWeixin.setButtonColor(R.color.gray);
            tiWeixin.setClickable(false);
            tiAlipay.setClickable(false);
        }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (getIntent().getIntExtra(Constants.MONTHLY_BOOK_ID, 0) != 0) {
            isMonthBook = true;
            mTitle = getIntent().getStringExtra(Constants.MONTHLY_BOOK_LIST_NAME);
            price = getIntent().getDoubleExtra(Constants.MONTHLY_BOOK_SPECIAL_PRICE, 0.0);
            //price = 0.01;
            mId = getIntent().getIntExtra(Constants.MONTHLY_BOOK_ID, 0);
            payUtils = new PayUtils(this, mId + "", price, PayUtils.MONTHLY_ORDER,price+"");
            payUtils.registerEventBus();
//            payUtils.initBroadCast();
        } else {
            bookIds = intent.getStringExtra("bookIds");
            count = intent.getIntExtra("count", 1);
            price = intent.getDoubleExtra("totalPrice", 0);

            priceString = intent.getStringExtra("pricesString");
            payUtils = new PayUtils(this, bookIds, price, payUtils.PERSON_ORDER,priceString);
            payUtils.registerEventBus();
//            payUtils.initBroadCast();
        }
    }

    @Override
    protected void initView() {
        tvBookCount.setText(count + "");
        tvPrice.setText(String.format("¥%s", price));
    }

    @Override
    protected void setMainHeadView() {
        if (isMonthBook) {
            textHeadTitle.setText(String.format("购买 %s", mTitle));
            tvBookCount.setVisibility(View.INVISIBLE);
            tvTip2.setVisibility(View.INVISIBLE);
            if (mTitle.length() > 6) {
                tvTip1.setText("已选择" + " " + mTitle.substring(0, 6) + "...");
            } else {
                tvTip1.setText(String.format("已选择%s", mTitle));
            }
            tvPrice.setText(String.format("¥%s", price));
            btnBack.setImageResource(R.drawable.btn_back);
        } else {
            textHeadTitle.setText("购买页面");
            btnBack.setImageResource(R.drawable.btn_back);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        payUtils.unregisterBroadCast();
        payUtils.unregisterEventBus();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
