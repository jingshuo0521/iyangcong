package com.iyangcong.reader.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.iyangcong.reader.MainActivity;
import com.iyangcong.reader.R;
import com.iyangcong.reader.ui.Policy;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.PermissionsChecker;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;

import me.relex.circleindicator.CircleIndicator;

public class SplashActivity extends FragmentActivity implements  Policy.RuleListener{

    private Button btnHome;
    private CircleIndicator indicator;
    private ViewPager pager;
    private GalleryPagerAdapter adapter;

    private int[] images = {
            R.drawable.guide_one,
            R.drawable.guide_two,
            R.drawable.guide_three
    };

    private static final int REQUEST_CODE = 0; // 请求码

    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            PermissionsChecker.PERMISSION_READ_PHONE_STATE,
            PermissionsChecker.PERMISSION_READ_EXTERNAL_STORAGE,
            PermissionsChecker.PERMISSION_WRITE_EXTERNAL_STORAGE,
            PermissionsChecker.PERMISSION_CAMERA
    };

    private PermissionsChecker mPermissionsChecker; // 权限检测器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mPermissionsChecker = new PermissionsChecker(this);

        final boolean firstTimeUse = SharedPreferenceUtil.getInstance().getBoolean("first-time-use", true);
        // 缺少权限时, 进入权限配置页面
//        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
//            startPermissionsActivity();
//        } else {
//            if (!firstTimeUse) {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                }, 800);
//            }
//        }

        if (firstTimeUse) {
            String text="欢迎使用爱洋葱阅读！\n"+
                    "请你务必审慎阅读，充分理解“爱洋葱用户服务协议”和“隐私政策”各项条款，包括但不限于：为了向你提供内容阅读等服务，我们需要搜集你的设备信息，操作日志等个人信息。\n"+
                    "你可以在“设置”中查看、变更、删除个人信息并管理你的授权。\n"+
                    "你可阅读《爱洋葱用户服务协议》和《隐私政策》了解详细信息。\n"+
                    "如你同意，请点击“同意”开始接受我们的服务";
            Policy.getInstance().showRuleDialog(this, "用户协议和隐私政策", text, R.color.link, this);

           // final IyangcongDialog normalDialog = new IyangcongDialog(this);
            //DialogUtils.setAlertDialogNormalStyle(normalDialog, getResources().getString(R.string.privacy_title),"请你务必审慎阅读，充分理解“爱洋葱用户服务协议”和“隐私政策”各项条款，包括但不限于：为了向你提供内容阅读等服务，我们需要搜集你的设备信息，操作日志等个人信息。你可以在“设置”中查看、变更、删除个人信息并管理你的授权。你可阅读<a href='http://edu.iyangcong.com/onion/user_protocol.html'>《爱洋葱用户服务协议》</a>和<a href='http://edu.iyangcong.com/onion/privacy.html'>《隐私政策》</a>了解详细信息。如你同意，请点击“同意”开始接受我们的服务" ,"暂不使用","同意");
            /*normalDialog.setOnBtnClickL(new OnBtnClickL() {
                @Override
                public void onBtnClick() {
                    normalDialog.dismiss();
                    //暂不使用
                    System.exit(0);
                }
            }, new OnBtnClickL() {
                @Override
                public void onBtnClick() {
                    normalDialog.dismiss();
                    //同意
                    if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                        startPermissionsActivity();
                    }
                    SharedPreferenceUtil.getInstance().putBoolean(SharedPreferenceUtil.IS_RECEIVE_NOTICE, true);
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Animation fadeOut = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fadeout);
                            fadeOut.setFillAfter(true);
                            findViewById(R.id.guideImage).startAnimation(fadeOut);
                            initGuideGallery();

                        }
                    });
                }
            });*/

        }else{
            if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                startPermissionsActivity();
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 800);
        }

    }


    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    //初始化并弹出对话框方法







    private void initGuideGallery() {
        final Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        btnHome = (Button) findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setVisibility(View.INVISIBLE);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setVisibility(View.VISIBLE);

        adapter = new GalleryPagerAdapter();
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == images.length - 1) {
                    btnHome.setVisibility(View.VISIBLE);
                    btnHome.startAnimation(fadeIn);
                } else {
                    btnHome.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void rule(boolean agree) {

        if (agree) {
            if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                startPermissionsActivity();
            }
            SharedPreferenceUtil.getInstance().putBoolean(SharedPreferenceUtil.IS_RECEIVE_NOTICE, true);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    Animation fadeOut = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fadeout);
                    fadeOut.setFillAfter(true);
                    findViewById(R.id.guideImage).startAnimation(fadeOut);
                    initGuideGallery();

                }
            });
        } else {
            System.exit(0);
        }
    }

    @Override
    public void oneClick() {
//        Intent intent = new Intent(this, RuleActivity.class);
//        intent.putExtra("privateRule", false);
//        intent.putExtra("url", "file:////android_asset/userRule.html");
//        startActivity(intent);
        String url = Urls.URL + "/onion/user_protocol.html";
        Intent intent = new Intent(this, AgreementActivity.class);
        intent.putExtra(Constants.USERAGREEMENT,url);
        startActivity(intent);
    }

    @Override
    public void twoClick() {

        String url = Urls.URL + "/onion/privacy.html";
        Intent intent = new Intent(this, AgreementActivity.class);
        intent.putExtra(Constants.USERAGREEMENT,url);
        startActivity(intent);
//        Intent intent = new Intent(this, RuleActivity.class);
//        intent.putExtra("privateRule", true);
//        intent.putExtra("url", "file:////android_asset/privateRule.html");
//        startActivity(intent);
    }

    public class GalleryPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView item = new ImageView(SplashActivity.this);
            item.setScaleType(ImageView.ScaleType.CENTER_CROP);
            item.setImageResource(images[position]);
            container.addView(item);
            return item;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }
    }

}
