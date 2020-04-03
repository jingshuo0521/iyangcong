package com.iyangcong.reader.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by youshengshao on 2017/9/12.
 */

//public class NormalQuestionActivity  extends ReactActivity {
//
//    /**
//     * Returns the name of the main component registered from JavaScript.
//     * This is used to schedule rendering of the component.
//     */
//    @Override
//    protected String getMainComponentName() {
//        return "Iyangcong";
//    }
//}

public class NormalQuestionActivity extends SwipeBackActivity {


    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;


    @OnClick({R.id.btnBack, R.id.btnFunction})
    void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnFunction:
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mine_normalquestion);
        ButterKnife.bind(this);
        setMainHeadView();


/*//        mReactRootView = new ReactRootView(this);注释掉这一行，不新建一个RN的view了，因为我们已经引用了，不需要重新创建了
        mReactRootView = (ReactRootView)findViewById(R.id.rnview);
        mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(getApplication())
                .setBundleAssetName("index.android.bundle")
                .setJSMainModuleName("index.android")
                .addPackage(new MainReactPackage())
                .setUseDeveloperSupport(false)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                //.setUseOldBridge(true) // uncomment this line if your app crashes
                .build();
        mReactRootView.startReactApplication(mReactInstanceManager, "Iyangcong", null);*/

//        mReactRootView = new ReactRootView(this);
//        mReactInstanceManager = ReactInstanceManager.builder()
//                .setApplication(getApplication())
//                .setBundleAssetName("index.android.bundle")
//                .setJSMainModuleName("index.android")
//                .addPackage(new MainReactPackage())
//                //.setUseDeveloperSupport(BuildConfig.DEBUG)
//                .setInitialLifecycleState(LifecycleState.RESUMED)
//                .build();
//        // 注意这里的MyReactNativeApp必须对应“index.js”中的
//        // “AppRegistry.registerComponent()”的第一个参数
//        mReactRootView.startReactApplication(mReactInstanceManager, "Iyangcong", null);
//
//        setContentView(mReactRootView);

    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText("常见问题");
        btnBack.setImageResource(R.drawable.btn_back);
    }



}