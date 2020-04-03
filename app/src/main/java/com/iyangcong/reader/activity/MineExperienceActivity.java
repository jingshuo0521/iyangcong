package com.iyangcong.reader.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.interfaceset.DeviceType;
import com.iyangcong.reader.ui.IYangCongWebView;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.ShareUtils;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;

import static com.iyangcong.reader.utils.ShareUtils.MINE_EXPERIENCE_SHARE;
import static com.iyangcong.reader.utils.ShareUtils.URLS_KEY;

/**
 * Created by Administrator on 2017/3/23.
 */

public class MineExperienceActivity extends SwipeBackActivity {


    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.webView)
    IYangCongWebView webView;
    @BindView(R.id.activity_mine_experience)
    LinearLayout activityMineExperience;

    private String url = new String();
    private String title = new String();
    private boolean request = false;
    private ShareUtils shareUtils;
    @Override
    protected void initData(Bundle savedInstanceState) {
        HashMap<String,String> map = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        sb.append(Urls.URL).append("/onion/yueli.html?userId=").append(CommonUtil.getUserId()).append("&deviceType=").append(DeviceType.WEB_1);
        map.put(URLS_KEY,sb.toString());
        shareUtils = new ShareUtils(context,map, MINE_EXPERIENCE_SHARE);
        shareUtils.addImagUrl(SharedPreferenceUtil.getInstance().getString(SharedPreferenceUtil.USER_PORTAIT_URL,null));
    }

    @Override
    protected void initView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSavePassword(false);
        if (!request) {
            StringBuilder sb = new StringBuilder();
            sb.append(Urls.URL).append("/onion/yueli.html?userId=").append(CommonUtil.getUserId()).append("&deviceType=").append(DeviceType.WEB_1);
            webView.loadUrl(sb.toString());
        } else {
            if (url != null) {
                webView.loadUrl(url);
            } else {
                ToastCompat.makeText(this, "加载数据出问题，请稍后再试", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_experience);
        ButterKnife.bind(this);
        this.url = getIntent().getStringExtra("url");
        this.title = getIntent().getStringExtra("title");
        this.request = getIntent().getBooleanExtra("request", false);
        initView();
        setMainHeadView();
    }

    @Override
    protected void setMainHeadView() {
        if (request == false) {
            textHeadTitle.setText("我的阅历");
        } else {
            textHeadTitle.setText(title);
        }
        btnBack.setImageResource(R.drawable.btn_back);
        btnFunction.setImageResource(R.drawable.share);
        btnFunction.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.btnBack, R.id.btnFunction})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnFunction:
                shareUtils.open();
                break;
        }
    }
}
