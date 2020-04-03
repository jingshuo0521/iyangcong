package com.iyangcong.reader.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.DiscoverReadPartyVedioAdapter;
import com.iyangcong.reader.bean.CommonVideo;
import com.iyangcong.reader.bean.DiscoverReadingPartyDetailsContent;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.ui.MyGridView;
import com.iyangcong.reader.ui.TagGroup;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.RGBLuminanceSource;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;


/**
 * 类作用  : 显示读书会详情的内容
 * 需要的参数：(int)Id、(String)Title
 * modified by WuZepeng  in 2017-03-24 9:43
 */

public class DiscoverReadingPartyDetailsActivity extends SwipeBackActivity {

    DiscoverReadingPartyDetailsContent content = new DiscoverReadingPartyDetailsContent();

    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.im_reading_party_head)
    ImageView imReadingPartyHead;
    @BindView(R.id.tv_reading_party_title)
    TextView tvReadingPartyTitle;
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_theme)
    TagGroup tvTheme;
    @BindView(R.id.rl_enter_tip)
    RelativeLayout rlEnterTip;
    @BindView(R.id.iv_qrcode)
    ImageView ivQRCode;
    @BindView(R.id.tv_introduce)
    TextView tvIntroduce;
    @BindView(R.id.gv_video_review)
    MyGridView gvVideoReview;
    @BindView(R.id.sv_party_detail)
    ScrollView svPartyDetail;

    private DiscoverReadPartyVedioAdapter discoverReadPartyVedioAdapter;

    private int Id;
    private String Title;
    private String time;
    private File file = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_reading_party_details);
        ButterKnife.bind(this);
        initView();
        setMainHeadView();
    }

    @OnClick(R.id.btnBack)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
        }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initView() {
        svPartyDetail.setVisibility(View.INVISIBLE);
        Id = getIntent().getIntExtra(Constants.readingPartyId, 0);
        Title = getIntent().getStringExtra(Constants.readingPartyTitle);

        View introduce = findViewById(R.id.include_introduce);
        TextView tvTitle = (TextView) introduce.findViewById(R.id.tv_bar_title);
        tvTitle.setText("简介");
        introduce.findViewById(R.id.tv_more).setVisibility(View.GONE);
        introduce.findViewById(R.id.iv_more).setVisibility(View.GONE);
        View videoReview = findViewById(R.id.include_video_review);
        TextView tvTitleVideo = (TextView) videoReview.findViewById(R.id.tv_bar_title);
        tvTitleVideo.setText("精彩回顾");
        videoReview.findViewById(R.id.tv_more).setVisibility(View.GONE);
        videoReview.findViewById(R.id.iv_more).setVisibility(View.GONE);
        discoverReadPartyVedioAdapter = new DiscoverReadPartyVedioAdapter(this, content.getReviewVideoList());
        gvVideoReview.setAdapter(discoverReadPartyVedioAdapter);
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText(Title);
        btnBack.setImageResource(R.drawable.btn_back);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getDatasFromNetwork();
    }

    public void getDatasFromNetwork() {
        getReadingPartyDetails(Id);
        getVideoList(Id);
    }

    public void getReadingPartyDetails(int clubId) {
        showLoadingDialog();
        OkGo.get(Urls.DiscoverCircleReadPartyDetail)
                .params("clubid", clubId + "")
                .execute(new JsonCallback<IycResponse<DiscoverReadingPartyDetailsContent>>(context) {
                    @Override
                    public void onSuccess(IycResponse<DiscoverReadingPartyDetailsContent> readingPartyDetailsIycResponse, Call call, Response response) {
                        dismissLoadingDialig();
                        svPartyDetail.setVisibility(View.VISIBLE);
                        if (readingPartyDetailsIycResponse.getData() != null) {
                            updateReadingPartyDetailsUI(readingPartyDetailsIycResponse.getData());
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dismissLoadingDialig();
                        super.onError(call, response, e);
                    }
                });
    }

    public void updateReadingPartyDetailsUI(DiscoverReadingPartyDetailsContent content) {
        GlideImageLoader glideImageLoader = new GlideImageLoader();
        glideImageLoader.onDisplayImage(context, imReadingPartyHead, content.getImage());//读书会背景图
        tvReadingPartyTitle.setText(content.getName());//读书会名称
        tvCity.setText(content.getAdviser());//主讲人
        tvTime.setText(content.getTime());//时间
        tvLocation.setText(content.getLocation());//位置
        List<String> tagList = new ArrayList<>();
        tagList.add(content.getSubject());
        tagList.add(content.getPeriod());
        tvTheme.setTags(tagList);
//        tvTheme.setText(content.getSubject());//主题
        Glide.with(this).load(content.getUrl())//
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)//
                .into(ivQRCode);
        if (content.getUrl().contains("/null")) {
            rlEnterTip.setVisibility(View.GONE);
        }
        tvIntroduce.setText(content.getGuide());
        ivQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 长按识别二维码
                showLoadingDialog();
                saveCurrentImage();
            }
        });
    }

    private void getVideoList(int clubId) {
        OkGo.get(Urls.DiscoverCirlceReadPartyDetailVideoList)
                .params("clubid", clubId)
                .execute(new JsonCallback<IycResponse<List<CommonVideo>>>(context) {
                    @Override
                    public void onSuccess(IycResponse<List<CommonVideo>> listIycResponse, Call call, Response response) {
                        List<CommonVideo> tempList = listIycResponse.getData();
                        if(content.getReviewVideoList() == null){
                            content.setReviewVideoList(new ArrayList<CommonVideo>());
                        }
                        if (tempList.size() > 0){
                            content.getReviewVideoList().clear();
                            content.getReviewVideoList().addAll(tempList);
                            discoverReadPartyVedioAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {

                    }
                });
    }

    //这种方法状态栏是空白，显示不了状态栏的信息
    private void saveCurrentImage() {
        //获取当前屏幕的大小
        int width = getWindow().getDecorView().getRootView().getWidth();
        int height = getWindow().getDecorView().getRootView().getHeight();
        //生成相同大小的图片
        Bitmap temBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //找到当前页面的根布局
        View view = getWindow().getDecorView().getRootView();
        //设置缓存
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        //从缓存中获取当前屏幕的图片,创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
        temBitmap = view.getDrawingCache();
        SimpleDateFormat df = new SimpleDateFormat("yyyymmddhhmmss");
        time = df.format(new Date());
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/screen", time + ".png");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file);
                temBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/screen/" + time + ".png";
                    final Result result = parseQRcodeBitmap(path);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            dismissLoadingDialig();
                            if (null != result) {
                                Intent intent = new Intent(DiscoverReadingPartyDetailsActivity.this, AgreementActivity.class);
                                intent.putExtra(Constants.USERAGREEMENT, result.toString());
                                startActivity(intent);
                            } else {
                                ToastCompat.makeText(context, "无法识别", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }).start();
            //禁用DrawingCahce否则会影响性能 ,而且不禁止会导致每次截图到保存的是缓存的位图
            view.setDrawingCacheEnabled(false);
        }
    }

    private Result parseQRcodeBitmap(String bitmapPath) {
        //解析转换类型UTF-8
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        //获取到待解析的图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        //如果我们把inJustDecodeBounds设为true，那么BitmapFactory.decodeFile(String path, Options opt)
        //并不会真的返回一个Bitmap给你，它仅仅会把它的宽，高取回来给你
        options.inJustDecodeBounds = true;
        //此时的bitmap是null，这段代码之后，options.outWidth 和 options.outHeight就是我们想要的宽和高了
        Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath, options);
        //我们现在想取出来的图片的边长（二维码图片是正方形的）设置为400像素
        /**
         options.outHeight = 400;
         options.outWidth = 400;
         options.inJustDecodeBounds = false;
         bitmap = BitmapFactory.decodeFile(bitmapPath, options);
         */
        //以上这种做法，虽然把bitmap限定到了我们要的大小，但是并没有节约内存，如果要节约内存，我们还需要使用inSimpleSize这个属性
        options.inSampleSize = options.outHeight / 400;
        if (options.inSampleSize <= 0) {
            options.inSampleSize = 1; //防止其值小于或等于0
        }
        /**
         * 辅助节约内存设置
         *
         * options.inPreferredConfig = Bitmap.Config.ARGB_4444;    // 默认是Bitmap.Config.ARGB_8888
         * options.inPurgeable = true;
         * options.inInputShareable = true;
         */
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(bitmapPath, options);
        //新建一个RGBLuminanceSource对象，将bitmap图片传给此对象
        RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(bitmap);
        //将图片转换成二进制图片
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(rgbLuminanceSource));
        //初始化解析对象
        QRCodeReader reader = new QRCodeReader();
        //开始解析
        Result result = null;
        try {
            result = reader.decode(binaryBitmap, hints);
        } catch (Exception ignored) {
        }

        return result;
    }
}
