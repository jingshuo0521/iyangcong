package com.iyangcong.reader.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.base.BaseActivity;
import com.iyangcong.reader.bean.MineBasicInfo;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.SignCalendar;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;


public class SignCalendarActivity extends BaseActivity implements View.OnClickListener {
    private SignCalendar calendar;
    private String date;
    private TextView tv_sign_year_month;
    private TextView activityRules;
    private TextView signContinue;
    private TextView signAll;
    private TextView btnSign;
    private ImageView signSure;
    private RelativeLayout rlGetGiftData;
    private ImageView left;
    private ImageButton signBack;
    private ImageView right;
    private TextView getCoin;
    List<String> list = new ArrayList<>();//list中存储的格式为2019-06-02
    private int month;
    private int year;
    private int coin = 0;

    private SignCalendar.OnCalendarDateChangedListener onCalendarDateChangedListener = new SignCalendar.OnCalendarDateChangedListener() {
        @Override
        public void onCalendarDateChanged(int calendarYear, int calendarMonth) {
            year = calendarYear;
            month = calendarMonth + 1;
            tv_sign_year_month.setText(year + "年" + month + "月");
            getThisMonthSign();
        }
    };
    private Date thisday = new Date(); // 今天



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_calendar);

        //获取当前的月份
        month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        //获取当前的年份
        year = Calendar.getInstance().get(Calendar.YEAR);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        // 获取当前时间
        Date curDate = new Date(System.currentTimeMillis());
        date = formatter.format(curDate);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        calendar = findViewById(R.id.sc_main);
        signAll = findViewById(R.id.all_num);
        btnSign = findViewById(R.id.sign);
        signBack = findViewById(R.id.sign_back);
        signSure = findViewById(R.id.rl_queding_btn);
        rlGetGiftData = findViewById(R.id.sign_success);
        signContinue = findViewById(R.id.sign_continue);
        activityRules = findViewById(R.id.activity_rules);
        tv_sign_year_month = findViewById(R.id.tv_sign_year_month);
        getCoin = findViewById(R.id.get_integer);
        signBack.setImageResource(R.drawable.btn_back);


        getThisMonthSign();
        getDatasFromNetwork();
        getUserContinuousSignDays();
        //设置当前日期
        tv_sign_year_month.setText(year + "年" + month + "月"
        );
        calendar.setOnCalendarDateChangedListener(onCalendarDateChangedListener);

        signSure.setOnClickListener(this);
        btnSign.setOnClickListener(this);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        signBack.setOnClickListener(this);
        activityRules.setOnClickListener(this);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setMainHeadView() {

    }

    /**
     * 获取当月签到信息
     */
    private void getThisMonthSign() {
        if (!CommonUtil.getLoginState()) {
            return;
        }
        OkGo.get(Urls.GetMonthSign)
                .params("userId", CommonUtil.getUserId())
                .params("year", year + "")
                .params("month", month + "")
                .execute(new JsonCallback<IycResponse<List<String>>>(this) {
                    @Override
                    public void onSuccess(IycResponse<List<String>> iycResponse, Call call, Response response) {
                        for (String today : iycResponse.getData()) {
                            if (today.equals(date)) {
                                setAlreadySign();
//                                rlBtnSign.setClickable(false);
                            }
                        }
                        List<String> signDay = iycResponse.getData();
                        calendar.addMarks(signDay, 1);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
//                        ToastCompat.makeText(context,e.getMessage(),500);
                    }
                });

    }

    /**
     * 获取头像积分等信息
     */
    private void getDatasFromNetwork() {
        if (!CommonUtil.getLoginState()) {
            return;
        }
        OkGo.get(Urls.PersonBasicInfoURL)
                .params("userId", CommonUtil.getUserId() + "")
                .params("toUserId", CommonUtil.getUserId() + "")
                .execute(new JsonCallback<IycResponse<MineBasicInfo>>(this) {
                    @Override
                    public void onSuccess(IycResponse<MineBasicInfo> mineBasicInfoIycResponse, Call call, Response response) {
                        signAll.setText(mineBasicInfoIycResponse.getData().getCoin()+"");
                        coin = mineBasicInfoIycResponse.getData().getCoin();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {

                    }

                    @Override
                    public void parseError(Call call, Exception e) {
                        super.parseError(call, e);

                    }
                });
    }

    /**
     * 再次获取积分信息
     */
    private void getCoinFromNetwork() {
        if (!CommonUtil.getLoginState()) {
            return;
        }
        OkGo.get(Urls.PersonBasicInfoURL)
                .params("userId", CommonUtil.getUserId() + "")
                .params("toUserId", CommonUtil.getUserId() + "")
                .execute(new JsonCallback<IycResponse<MineBasicInfo>>(this) {
                    @Override
                    public void onSuccess(IycResponse<MineBasicInfo> mineBasicInfoIycResponse, Call call, Response response) {
                        signAll.setText(mineBasicInfoIycResponse.getData().getCoin()+"");
                        getCoin.setText("您获得了 "+(mineBasicInfoIycResponse.getData().getCoin()-coin)+" 积分");
                        rlGetGiftData.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {

                    }

                    @Override
                    public void parseError(Call call, Exception e) {
                        super.parseError(call, e);

                    }
                });
    }

    private void setAlreadySign(){
        btnSign.setEnabled(false);
        btnSign.setBackground(getResources().getDrawable(R.drawable.al_sign));
        btnSign.setText("今日已签到");
        btnSign.setTextColor(getResources().getColor(R.color.text_color));
    }
    /**
     * 签到
     */
    private void signIn() {
        if (CommonUtil.getLoginState()) {
            OkGo.get(Urls.SignInURL1)
                    .params("userId", CommonUtil.getUserId())
                    .execute(new JsonCallback<IycResponse<String>>(this) {
                        @Override
                        public void onSuccess(IycResponse<String> iycResponse, Call call, Response response) {

                            calendar.addMark(date, 0);
                            getCoinFromNetwork();
                            getUserContinuousSignDays();
                            setAlreadySign();
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            ToastCompat.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void getUserContinuousSignDays() {
        if (!CommonUtil.getLoginState()) {
            return;
        }
        OkGo.get(Urls.GetUserContinuousSignDays)
                .params("userId", CommonUtil.getUserId())
                .execute(new JsonCallback<IycResponse<String>>(this) {
                    @Override
                    public void onSuccess(IycResponse<String> iycResponse, Call call, Response response) {
                        signContinue.setText("连续签到"+iycResponse.getData()+"天啦～");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
//                        ToastCompat.makeText(context, e.getMessage(), 500);
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left:
                if (month == 1) {
                    left.setClickable(false);
                } else {
                    calendar.lastMonth();
                }
                right.setClickable(true);
                break;
            case R.id.right:
                if (month == 12) {
                    right.setClickable(false);
                } else {
                    calendar.nextMonth();
                }
                left.setClickable(true);
                break;
//            case R.id.btnBack:
//                finish();
//                break;
            case R.id.activity_rules:
                Intent intent = new Intent(this, AgreementActivity.class);
                intent.putExtra(Constants.USERAGREEMENT, Urls.URL + "/onion/coin.html");
                startActivity(intent);
                break;
            case R.id.rl_queding_btn:
                rlGetGiftData.setVisibility(View.GONE);
                    break;
            case R.id.sign:
                signIn();
                break;
            case R.id.sign_back:
                finish();
                break;
        }
    }
}
