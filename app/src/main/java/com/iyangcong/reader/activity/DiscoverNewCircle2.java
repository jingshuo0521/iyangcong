package com.iyangcong.reader.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.CirclePhoto;
import com.iyangcong.reader.bean.DiscoverCreateCircle;
import com.iyangcong.reader.bean.RepeatedName;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.enumset.StateEnum;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.ui.LimitedEdittext;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.BitmapUtils;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.NewGildeImageLoader;
import com.iyangcong.reader.utils.Urls;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.loader.ImageLoader;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.activity.MineSuggestActivity.REQUEST_CODE_PREVIEW;
import static com.iyangcong.reader.activity.MineSuggestActivity.REQUEST_CODE_SELECT;
import static com.iyangcong.reader.utils.NotNullUtils.isNull;

public class DiscoverNewCircle2 extends SwipeBackActivity {


    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.activity_discover_new_circle2)
    LinearLayout activityDiscoverNewCircle2;
    @BindView(R.id.et_circle_name)
    LimitedEdittext etCircleName;
    @BindView(R.id.iv_circle_cover)
    ImageView ivCircleCover;
    @BindView(R.id.tv_upload_pic)
    TextView tvUploadPic;

    private StateEnum stateEnum;
    private int maxLen = 20;
    private boolean receviePic = false;
    private ArrayList<ImageItem> imageItems = new ArrayList<>();
    private DiscoverCreateCircle receivedCircle = new DiscoverCreateCircle();
    private int mGroupId;

    @OnClick({R.id.btnBack, R.id.btnFunction})
    void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnFunction:
                if (stateEnum == StateEnum.CREATE) {
                    checkCircleNameIsReapted(etCircleName.getText().toString());
                } else {
                    compressBitmap();
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_new_circle2);
        ButterKnife.bind(this);
        initView();
        setMainHeadView();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (imageItems.size() == 0)
//            ImagePicker.getInstance().getImageLoader().displayImage((Activity) context, "", ivCircleCover, 0, 0);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        receivedCircle = (DiscoverCreateCircle) getIntent().getParcelableExtra(Constants.CREATE_CIRLCE);
        stateEnum = (StateEnum) getIntent().getSerializableExtra(Constants.CREATE_CIRLE_OR_MODIFY);
        mGroupId = getIntent().getIntExtra(Constants.groupId, -1);
    }

    @Override
    protected void initView() {
        initImagePicker();
        if (stateEnum == StateEnum.MODIFY) {
            Logger.i("wzp cover:" + receivedCircle.getCover());
            etCircleName.setText(receivedCircle.getGroupname());
            showPic(receivedCircle.getCover());
        }
        etCircleName.setTextWatcher(context, maxLen, true,getString(R.string.content_toolong),getString(R.string.content_no_emoji));
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText(stateEnum.equals(StateEnum.MODIFY) ? getString(R.string.edit_circle) : getString(R.string.create_circle));
        btnBack.setImageResource(R.drawable.btn_back);
        btnFunction.setImageResource(R.drawable.ic_next);
        btnFunction.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.iv_circle_cover, R.id.tv_upload_pic})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_circle_cover:
                handleWhenIvClicked();
                break;
            case R.id.tv_upload_pic:
                choosePicByTextView();
                break;
        }
    }

    /**
     * 如果imageItems里面没有内容，让用户去选择照片，如果有内容，让用户替换照片
     */
    private void handleWhenIvClicked() {
        if (imageItems.size() == 0)
            choosePicByTextView();
        else
            choosePicByImageView();

    }

    private void choosePicByTextView() {
        ImagePicker.getInstance().setSelectLimit(1);
        Intent intent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT);
    }

    private void choosePicByImageView() {
        Intent intentPreview = new Intent(this, ImagePreviewDelActivity.class);
        intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, imageItems);
        intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, 0);
        startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
    }

    private boolean canNext() {
        if (etCircleName.getText() != null)
            receivedCircle.setGroupname(etCircleName.getText().toString());
        if(!isNull(context,receivedCircle.getGroupname(),"请输入圈子名称")&&!isNull(context,receivedCircle.getCover(),"请选择照片"))
            return true;
        return false;
    }

    private void nextActivity() {
        if (!canNext()) {
            return;
        }
        Intent intent = new Intent(this, DiscoverNewCircle3.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.groupId, mGroupId);
        bundle.putSerializable(Constants.CREATE_CIRLE_OR_MODIFY, stateEnum);
        bundle.putParcelable(Constants.CREATE_CIRLCE, receivedCircle);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageItems.clear();
        ArrayList<ImageItem> images = new ArrayList<>();
        Logger.i("imageItems0" + imageItems.size());
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
            }
        }
        imageItems.addAll(images);
        Logger.i("imageItems1" + imageItems.size());
        showPic(imageItems);
        if (imageItems.size() > 0) {
            receivedCircle.setCover(imageItems.get(0).path);
        }
    }

    private void showPic(String url) {
        if (url == null)
            return;
        GlideImageLoader imageLoader = new GlideImageLoader();
        imageLoader.onDisplayImage(this, ivCircleCover, url);
//        ImagePicker.getInstance().
//                getImageLoader().
//                displayImage(context, url, ivCircleCover, 0, 0);
    }

    private void showPic(List<ImageItem> list) {
        if (list.size() > 0)
            ImagePicker.getInstance().
                    getImageLoader().
                    displayImage((Activity) context,
                            list.get(list.size() - 1).path,
                            ivCircleCover, 0, 0);
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new NewGildeImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(false);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(1);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    private void checkCircleNameIsReapted(String name) {
        if(isNull(context,name,"请输入圈子名称")){
            return;
        }
        showLoadingDialog();
        OkGo.get(Urls.DiscoverCircleCheckNameRepeated)
                .params("groupname", name)
                .execute(new JsonCallback<IycResponse<RepeatedName>>(this) {
                    @Override
                    public void onSuccess(IycResponse<RepeatedName> booleanIycResponse, Call call, Response response) {
                        boolean isNameRepeated = booleanIycResponse.getData().isused();
                        if(!isNameRepeated){
                            compressBitmap();
                        }else{
                            ToastCompat.makeText(context, getString(R.string.circle_name_repeated), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dismissLoadingDialig();
                        super.onError(call, response, e);
                    }
                });
    }

    public void compressBitmap(){
        BitmapUtils.compressImage(context, receivedCircle.getCover(), new BitmapUtils.ProcessImgCallBack() {
            @Override
            public void compressSuccess(String imgPath) {
                Logger.i("wzp afterCompress" + imgPath);
                modifyCircleCover(stateEnum,imgPath);
            }
        });
    }

    public void modifyCircleCover(final StateEnum stateEnum,String path){
        OkGo.post(Urls.DiscoverCircleUploadPhoto)
                .params("photo", new File(path))
                .execute(new JsonCallback<IycResponse<CirclePhoto>>(context) {
                    @Override
                    public void onSuccess(IycResponse<CirclePhoto> circlePhotoIycResponse, Call call, Response response) {
                        dismissLoadingDialig();
                        String path = circlePhotoIycResponse.getData().getDbPath();
                        if (null != path && !"".equals(path)) {
                            receivedCircle.setCover(path);
                            nextActivity();
                        } else {
//                            receivedCircle.setCover("");
                            ToastCompat.makeText(context, "照片上传失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        dismissLoadingDialig();
                        if (stateEnum == StateEnum.MODIFY) {
                            nextActivity();
                            return;
                        }
                        if (e.getMessage().contains(" (No such file or directory)")) {
                            ToastCompat.makeText(context, "请选择照片", Toast.LENGTH_SHORT).show();
                        }
//                        receivedCircle.setCover("");
                    }
                });
    }

}
