package com.iyangcong.reader.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.dialog.widget.NormalDialog;
import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.CirclePhoto;
import com.iyangcong.reader.bean.Comment;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.ui.LimitedEdittext;
import com.iyangcong.reader.ui.button.FlatButton;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.BitmapUtils;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.DialogUtils;
import com.iyangcong.reader.utils.HtmlParserUtils;
import com.iyangcong.reader.utils.NewGildeImageLoader;
import com.iyangcong.reader.utils.NotNullUtils;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.richeditor.RichEditor;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.activity.MineSuggestActivity.REQUEST_CODE_SELECT;

public class RichTextActivity extends SwipeBackActivity {


    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.et_title)
    LimitedEdittext etTitle;
    @BindView(R.id.fb_addpicture)
    FlatButton fbAddpicture;
    @BindView(R.id.re_content)
    RichEditor reContent;
    @BindView(R.id.layout_rich_text)
    RelativeLayout layoutRichText;
    private int mMaxLen = 15;
    private int mCircleId = 0;
    private long mUserId = 0;
    ArrayList<ImageItem> imageItems;
    private int mMaxContentLength = 9800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rich_text);
        ButterKnife.bind(this);
        setMainHeadView();
        initView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mUserId = CommonUtil.getUserId();
        mCircleId = getIntent().getIntExtra(Constants.circleId, 0);
        imageItems = new ArrayList<>();
    }

    @Override
    protected void initView() {
        initImagePicker();
        reContent.setEditorHeight(200);
        reContent.setEditorFontSize(14);
        reContent.setEditorFontColor(Color.GRAY);
        reContent.setPadding(0, 10, 0, 10);
        reContent.setPlaceholder("请输入话题内容");
        etTitle.setTextWatcher(context, mMaxLen,getString(R.string.content_toolong));
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText(R.string.post_new_topic);
        btnBack.setImageResource(R.drawable.btn_back);
        btnFunction.setImageResource(R.drawable.ic_send);
        btnFunction.setVisibility(View.VISIBLE);
    }


    @OnClick({R.id.btnBack, R.id.btnFunction, R.id.fb_addpicture})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnFunction:
                String relativePathHtml = HtmlParserUtils.handleAbsPathHtml(reContent.getHtml());
                String content = HtmlParserUtils.getContent(reContent.getHtml());
                if (content.length() > mMaxContentLength) {
                    ToastCompat.makeText(this, "发表话题字数超过限制", 1000);
                    return;
                }
                postATopic(mCircleId, mUserId, relativePathHtml, etTitle.getText().toString());
                break;
            case R.id.fb_addpicture:
                choosePicByTextView();
                break;
        }
    }


    private void choosePicByTextView() {
        ImagePicker.getInstance().setSelectLimit(9 - imageItems.size());
        Intent intent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<ImageItem> images = new ArrayList<>();
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            }
        }
        for (ImageItem item : images) {
//            compressBitmap(item);
            postPicture(item);
        }
    }

    /**
     * 往RichEditor中插入图片链接
     *
     * @param list
     */
    private void insertImage(ArrayList<ImageItem> list) {
        for (ImageItem imageItem : list) {
            insertImage(imageItem);
        }
    }

    /**
     * 往RichEditor中插入图片链接
     *
     * @param imageItem
     */
    private void insertImage(ImageItem imageItem) {
        Logger.i("hahahaha: path:" + imageItem.path + "  name:" + imageItem.name);
        reContent.focusEditor();
        reContent.insertImage(imageItem.path, imageItem.name);
    }

    /***
     * 初始化图片加载器
     */
    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new NewGildeImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(false);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(9);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    /**
     * 发表话题接口
     *
     * @param groupId
     * @param userId
     * @param topicDesc 话题描述
     * @param topicName 话题名称
     */
    private void postATopic(int groupId, long userId, String topicDesc, String topicName) {
        if (NotNullUtils.isNull(context, topicName) || NotNullUtils.isNull(context, topicDesc)) {
            return;
        }
        showLoadingDialog();
        OkGo.get(Urls.DiscoverCircleDetailAddTopic)
                .params("contentsize", topicDesc.length())
                .params("groupid", groupId)
                .params("topicname", topicName)
                .params("topicdesc", topicDesc)
                .params("userid", userId)
                .execute(new JsonCallback<IycResponse<String>>(context) {
                    @Override
                    public void onSuccess(IycResponse<String> stringIycResponse, Call call, Response response) {
                        ToastCompat.makeText(context, stringIycResponse.getMsg(), Toast.LENGTH_SHORT).show();
                        dismissLoadingDialig();
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dismissLoadingDialig();
                        ToastCompat.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 压缩照片
     * @param images
     */
    private void compressBitmap(final ImageItem images){
        BitmapUtils.compressImage(context, images.path, new BitmapUtils.ProcessImgCallBack() {
            @Override
            public void compressSuccess(String imgPath) {
                images.path = imgPath;
                postPicture(images);
            }
        });
    }
    /**
     * 上传图片
     *
     * @param images
     */
    private void postPicture(final ImageItem images) {
        OkGo.post(Urls.DiscoverCircleDetailUploadPicture)
                .params("photo", new File(images.path))
                .execute(new JsonCallback<IycResponse<CirclePhoto>>(context) {
                    @Override
                    public void onSuccess(IycResponse<CirclePhoto> circlePhotoIycResponse, Call call, Response response) {
                        images.path = circlePhotoIycResponse.getData().getPhotoUrl();
                        Logger.i("hahahaha receive images.path:" + images.path);
                        insertImage(images);
                        imageItems.add(images);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        try {
                            NormalDialog dialog = new NormalDialog(context);
                            DialogUtils.setAlertDialogNormalStyle(dialog, "提示", e.getMessage());
                        } catch (Exception error) {
                            Logger.d(e.getMessage());
                        }
                    }
                });
    }
}
