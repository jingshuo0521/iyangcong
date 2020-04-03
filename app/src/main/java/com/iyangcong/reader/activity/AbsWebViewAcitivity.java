package com.iyangcong.reader.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
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
import com.orhanobut.logger.Logger;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;

import static com.iyangcong.reader.utils.ShareUtils.MINE_EXPERIENCE_SHARE;
import static com.iyangcong.reader.utils.ShareUtils.URLS_KEY;

public abstract class AbsWebViewAcitivity extends SwipeBackActivity {

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
    @BindView(R.id.webView)
    IYangCongWebView webView;

    private ShareUtils shareUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abs_web_view);
        ButterKnife.bind(this);
        initData(savedInstanceState);
        setMainHeadView();
        initView();
    }

    protected void iniShareUtils() {
        HashMap<String, String> map = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        sb.append(Urls.URL).append("/onion/yueli.html?userId=").append(CommonUtil.getUserId()).append("&deviceType=").append(DeviceType.ANDROID_3);
        map.put(URLS_KEY, sb.toString());
        shareUtils = new ShareUtils(context, map, MINE_EXPERIENCE_SHARE);
        shareUtils.addImagUrl(SharedPreferenceUtil.getInstance().getString(SharedPreferenceUtil.USER_PORTAIT_URL, null));
    }

    @Override
    protected void initView() {
        WebSettings settings = webView.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setSavePassword(false);
        if (!getTitle().equals("我的阅历")) {
            settings.setTextSize(WebSettings.TextSize.LARGEST);
        }
        String url = getUrl();
        Logger.v("wzp url" + url);
        if (url != null) {
            webView.loadUrl(getUrl());
        } else {
            ToastCompat.makeText(this, getString(R.string.load_url_failure), Toast.LENGTH_SHORT).show();
        }
    }

    public abstract String getUrl();

    protected abstract boolean isShareMode();

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText(getTitle());
        btnBack.setImageResource(R.drawable.btn_back);
        btnBack.setVisibility(View.VISIBLE);
        if (isShareMode()) {
            btnFunction.setImageResource(R.drawable.share);
            btnFunction.setVisibility(View.VISIBLE);
        } else {
            btnFunction.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.btnBack, R.id.btnFunction})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnFunction:
                if (shareUtils != null) {
                    shareUtils.open();
                }
                break;
        }
    }
}
