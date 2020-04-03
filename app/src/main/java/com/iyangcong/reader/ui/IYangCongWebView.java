package com.iyangcong.reader.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.iyangcong.reader.R;

/**
 * Created by Administrator on 2017/3/23.
 */

public class IYangCongWebView extends WebView {
    private ProgressBar progressbar;
    private Context mContext;
    private CustomProgressDialog dialogProgress;

    public IYangCongWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.getSettings().setSavePassword(false);
        this.getSettings().setAllowFileAccess(false);
        this.removeJavascriptInterface("searchBoxJavaBridge_");
        this.removeJavascriptInterface("accessibility");
        this.removeJavascriptInterface("accessibilityTraversal");
        mContext = context;
        progressbar = (ProgressBar) LayoutInflater.from(context).inflate(R.layout.progressbar_webview, null);
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 6, 0, 0));
        addView(progressbar);

        setWebChromeClient(new WebChromeClient());
        setWebViewClient(new WebViewClient());
        this.getSettings().setBuiltInZoomControls(true);
        this.getSettings().setUseWideViewPort(true);
    }
    public class WebChromeClient extends android.webkit.WebChromeClient{
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressbar.setVisibility(GONE);

            } else {
                if (progressbar.getVisibility() == GONE)
                    progressbar.setVisibility(VISIBLE);
                progressbar.setProgress(newProgress);

            }
            super.onProgressChanged(view, newProgress);
        }
    }
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }
}
