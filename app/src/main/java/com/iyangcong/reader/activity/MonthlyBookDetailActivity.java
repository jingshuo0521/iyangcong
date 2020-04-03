package com.iyangcong.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.MonthlyBookListAdapter;
import com.iyangcong.reader.bean.MarketBookListItem;
import com.iyangcong.reader.bean.MonthlyMarketBookListItem;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.enumset.MonthlyBookFrom;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.LoadCountHolder;
import com.iyangcong.reader.utils.LoginUtils;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;
import static com.iyangcong.reader.utils.Urls.MonthlyBookPaymentStatus;

/**
 * Created by WuZepeng on 2017-04-26.
 * 需要给MonthlyBookDetailActivity传一个mId(String);
 */
//TODO 增加一些业务上的处理。第一个就是在没有登录的时候的处理
public class MonthlyBookDetailActivity extends SwipeBackActivity {

    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.btnFunction1)
    ImageButton btnFunction1;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.rv_monthly_book_list)
    RecyclerView rvMonthlyBookList;
    @BindView(R.id.ptrClassicFrameLayout_monthly_book_list)
    PtrClassicFrameLayout ptrClassicFrameLayoutMonthlyBookList;
    @BindView(R.id.tv_shop_now)
    TextView tvShopNow;
    LoadCountHolder loadCountHolder = new LoadCountHolder() {
        @Override
        public void refresh() {
            setPage(1);
            setRefresh(true);
        }
    };
    @BindView(R.id.cb_user_protocol)
    CheckBox cbUserProtocol;
    @BindView(R.id.tv_user_protocol_tip)
    TextView tvUserProtocolTip;
    @BindView(R.id.tv_user_protocol_title)
    TextView tvUserProtocolTitle;
    @BindView(R.id.layout_user_protocol)
    LinearLayout layoutUserProtocol;
    @BindView(R.id.layout_protocol)
    LinearLayout layoutProtocol;
    //	private List<MarketBookListItem> books;
    private MonthlyMarketBookListItem monthlyMarketBookListItem;
    RecyclerAdapterWithHF adapterWithHF;
    private int mId;//包月id;
    private String mTitle;//activity的标题
    private Handler mHandler = new Handler();
    private long mUserId;
    private double mSpecialPrice;
    private boolean isFirstIn = true;
    private MonthlyBookFrom mState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_book_detail);
        ButterKnife.bind(this);
        setMainHeadView();
        initView();
    }


    @Override
    protected void initData(Bundle savedInstanceState) {
        mId = getIntent().getIntExtra(Constants.MONTHLY_BOOK_ID, 0);
        mUserId = CommonUtil.getUserId();
        mTitle = getIntent().getStringExtra(Constants.MONTHLY_BOOK_LIST_NAME);
        mSpecialPrice = getIntent().getDoubleExtra(Constants.MONTHLY_BOOK_SPECIAL_PRICE, 0.0);
        mState = (MonthlyBookFrom) getIntent().getSerializableExtra(Constants.FROM_BOOK_MARKET_OR_MINE);
        monthlyMarketBookListItem = new MonthlyMarketBookListItem();
        monthlyMarketBookListItem.setBookImageUrl("");
        monthlyMarketBookListItem.setId(mId + "");
        monthlyMarketBookListItem.setName(mTitle);
        monthlyMarketBookListItem.setBookSpecialPrice(mSpecialPrice);
        monthlyMarketBookListItem.setBooks(new ArrayList<MarketBookListItem>());
    }

    @Override
    protected void initView() {
        rvMonthlyBookList.setLayoutManager(new LinearLayoutManager(this));
        adapterWithHF = new RecyclerAdapterWithHF(new MonthlyBookListAdapter(context, monthlyMarketBookListItem));
        rvMonthlyBookList.setAdapter(adapterWithHF);

        ptrClassicFrameLayoutMonthlyBookList.setHorizontalScrollBarEnabled(false);
        ptrClassicFrameLayoutMonthlyBookList.post(new Runnable() {
            @Override
            public void run() {
                ptrClassicFrameLayoutMonthlyBookList.autoRefresh(true);
            }
        });
        ptrClassicFrameLayoutMonthlyBookList.setPtrHandler(new PtrDefaultHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Logger.i("refresh");
                        loadCountHolder.refresh();
                        refreshHandler();
                    }
                });
            }
        });
        ptrClassicFrameLayoutMonthlyBookList.setLoadMoreEnable(false);
        cbUserProtocol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                tvShopNow.setTextColor(b ? getResources().getColor(R.color.main_color) : getResources().getColor(R.color.text_color_lightgray));
            }
        });
    }

    @Override
    protected void setMainHeadView() {
        btnBack.setImageResource(R.drawable.btn_back);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle.setText(mTitle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirstIn) {
            isFirstIn = false;
        } else {
            loadCountHolder.refresh();
            refreshHandler();
        }
    }

    private void refreshHandler() {
        getDatasFromNetwork();
    }

    private void getDatasFromNetwork() {
        getMonthlyBookList(mId);
    }

    private void notLoginState() {
        monthlyMarketBookListItem.setEndTime("");
        monthlyMarketBookListItem.setStatus(0);
    }


    private void getMonthlyBookList(int paymentId) {
        if (paymentId <= 0)
            return;
        HashMap<String,String> params = new HashMap<>();
        //如果从书城首页的包月包列表里进来，调用接口的时候不用type
        if(mState == MonthlyBookFrom.BOOK_MARKET){
            params.put("paymentId",paymentId+"");
        }else if(mState == MonthlyBookFrom.MINE){
            //如果从我的-包月包列表进来，调用接口的时候需要type
            params.put("paymentId",paymentId+"");
            params.put("type",2+"");
        }
        OkGo.get(Urls.MonthlyBookPaymentDetail)
                .params(params)
                .execute(new JsonCallback<IycResponse<MonthlyMarketBookListItem>>(context) {
                    @Override
                    public void onSuccess(IycResponse<MonthlyMarketBookListItem> iycrResponse, Call call, Response response) {
                        successHandler(iycrResponse);
                    }

                    @Override
                    public void onAfter(IycResponse<MonthlyMarketBookListItem> datas, Exception e) {
                        super.onAfter(datas, e);
                        finishRefeshOrLoadMoreStatus(datas);
                    }
                });
    }

    /**
     * 接口调用成功以后进行的操作
     * @param iycrResponse
     */
    private void successHandler(IycResponse<MonthlyMarketBookListItem> iycrResponse) {
        if (loadCountHolder.isRefresh()) {
			monthlyMarketBookListItem.getBooks().clear();
		}
        monthlyMarketBookListItem.setEndTime(iycrResponse.getData().getEndTime());
        monthlyMarketBookListItem.getBooks().addAll(iycrResponse.getData().getBooks());
        monthlyMarketBookListItem.setBookImageUrl(iycrResponse.getData().getBookImageUrl());
        adapterWithHF.notifyDataSetChanged();

        if (CommonUtil.getLoginState()) {
			getMonthlyBookState(mId, mUserId);
		} else {
			notLoginState();
		}
    }

    /**
     * 接口调用结束以后的操作，主要是对刷新控件地状态操作。
     * @param datas
     */
    private void finishRefeshOrLoadMoreStatus(IycResponse<MonthlyMarketBookListItem> datas) {
        if (isNull(datas) || isNull(datas.getData()) || isNull(datas.getData().getBooks())) {
			if (loadCountHolder.isRefresh()) {
				refreshFailed(ptrClassicFrameLayoutMonthlyBookList);
			} else {
				loadCountHolder.loadMoreFailed();
				loadMoreFailed(ptrClassicFrameLayoutMonthlyBookList);
			}
		} else {
			boolean isEnd = datas.getData().getBooks().size() < loadCountHolder.getPageSize();
			if (loadCountHolder.isRefresh()) {
				refreshSuccess(ptrClassicFrameLayoutMonthlyBookList);
				ptrClassicFrameLayoutMonthlyBookList.setLoadMoreEnable(false);
			} else {
				loadMoreSuccess(ptrClassicFrameLayoutMonthlyBookList, true);
			}
		}
    }


    private void notifyShopButtonVisibility(boolean visible) {
        setProtocolVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void setProtocolVisibility(int visibility) {
        layoutProtocol.setVisibility(visibility);
        layoutUserProtocol.setVisibility(visibility);
        cbUserProtocol.setVisibility(visibility);
        tvUserProtocolTip.setVisibility(visibility);
        tvUserProtocolTitle.setVisibility(visibility);
        tvShopNow.setVisibility(visibility);
        adjustPtr(visibility == View.VISIBLE);
    }

    /***
     * 设置ptr到屏幕底部的距离
     *
     * @param isProtocolVisible
     */
    private void adjustPtr(boolean isProtocolVisible) {
    }

    private void getMonthlyBookState(int paymentId, long userId) {
        if (!CommonUtil.getLoginState() || paymentId == 0) {
            return;
        }
        OkGo.get(MonthlyBookPaymentStatus)
                .params("paymentId", paymentId)
                .params("userId", userId)
                .execute(new JsonCallback<IycResponse<MonthlyMarketBookListItem>>(context) {
                    @Override
                    public void onSuccess(IycResponse<MonthlyMarketBookListItem> IycResponse, Call call, Response response) {
                        monthlyMarketBookListItem.setStatus(IycResponse.getData().getStatus());
                        monthlyMarketBookListItem.setDeadLine(IycResponse.getData().getEndTime());
                        btnFunctionVisibility();
                        adapterWithHF.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void btnFunctionVisibility() {
        if ((monthlyMarketBookListItem.getStatus() == 1 && !monthlyMarketBookListItem.getEndTime().equals("00-00 00"))
                ||(monthlyMarketBookListItem.getStatus() == 2 && !monthlyMarketBookListItem.getEndTime().equals("00-00 00"))) {
            notifyShopButtonVisibility((Long.parseLong(monthlyMarketBookListItem.getEndTime()) > System.currentTimeMillis()));
        } else {
            setProtocolVisibility(View.GONE);
        }
    }

    @OnClick({R.id.btnBack, R.id.tv_shop_now, R.id.tv_user_protocol_tip, R.id.tv_user_protocol_title})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.tv_shop_now:
                LoginUtils loginUtils = new LoginUtils() {
                    @Override
                    public void setIntentData(Intent intent) {
                        super.setIntentData(intent);
                        intent.putExtra(Constants.MONTHLY_BOOK_LIST_NAME, monthlyMarketBookListItem.getName());
                        intent.putExtra(Constants.MONTHLY_BOOK_SPECIAL_PRICE, monthlyMarketBookListItem.getBookSpecialPrice());
                        intent.putExtra(Constants.MONTHLY_BOOK_ID, Integer.parseInt(monthlyMarketBookListItem.getId()));
                    }
                };
                if (isProtocolRead())
                    loginUtils.startIntent(context, MinePayActivity.class);
                break;
            case R.id.tv_user_protocol_tip:
            case R.id.tv_user_protocol_title:
                Intent intent = new Intent(MonthlyBookDetailActivity.this, AgreementActivity.class);
                String url = Urls.URL + "/onion/baoyue.html";
                intent.putExtra(Constants.USERAGREEMENT, url);
                startActivity(intent);
                break;
        }
    }

    private boolean isProtocolRead() {
        boolean isRead = cbUserProtocol.isChecked();
        if (!isRead) {
            ToastCompat.makeText(context, "购买包月服务请先同意包月协议", Toast.LENGTH_SHORT).show();
        }
        return isRead;
    }
}
