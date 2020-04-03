package com.iyangcong.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.ImagePickerAdapter;
import com.iyangcong.reader.bean.SuggestionChoices;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.interfaceset.DeviceType;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.ui.button.FlatButton;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.NewGildeImageLoader;
import com.iyangcong.reader.utils.Urls;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

public class MineSuggestActivity extends SwipeBackActivity implements ImagePickerAdapter.OnRecyclerViewItemClickListener {

    public static final int IMAGE_ITEM_ADD = -1;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    public static final int REQUEST_CODE_ACIVITE=102;
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.ll_suggest_choice)
    LinearLayout llSuggestChoice;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.fb_submit)
    FlatButton fbSubmit;

    private ImagePickerAdapter adapter;
    private ArrayList<ImageItem> selImageList = new ArrayList<>();
    private int maxImgCount = 8;               //允许选择图片最大数
    private SuggestionChoices suggestionChoices = new SuggestionChoices();
    private String[] mChoicesItems;
    private List<File> files = new ArrayList<>();

    private HashMap<String, String> hashMapImages = new HashMap<>();

    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.edit_Suggest)
    EditText editSuggest;
    @BindView(R.id.rv_Suggest_img)
    RecyclerView rvSuggestimg;
    @BindView(R.id.activity_mine_suggest)
    LinearLayout activityMineSuggest;

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
        setContentView(R.layout.activity_mine_suggest);
        ButterKnife.bind(this);
        initView();
        setMainHeadView();
        editSuggest.addTextChangedListener(mTextWatcher);
        //图片加载
        initImagePicker();
        initWidget();
    }

    TextWatcher mTextWatcher = new TextWatcher() {
        private CharSequence temp;
        private int editStart;
        private int editEnd;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            temp = s;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            editStart = editSuggest.getSelectionStart();
            editEnd = editSuggest.getSelectionEnd();
            if (temp.length() > 200) {
                Toast.makeText(MineSuggestActivity.this,
                        "你输入的字数已经超过了限制！", Toast.LENGTH_SHORT)
                        .show();
                s.delete(editStart - 1, editEnd);
                int tempSelection = editStart;
                editSuggest.setText(s);
                editSuggest.setSelection(tempSelection);
            }
        }
    };

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initView() {
        getSuggestionChoices();
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText("意见反馈");
        btnBack.setImageResource(R.drawable.btn_back);
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new NewGildeImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(false);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(maxImgCount);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    private void initWidget() {
        selImageList = new ArrayList<>();
        adapter = new ImagePickerAdapter(this, selImageList, maxImgCount);
        adapter.setOnItemClickListener(this);

        rvSuggestimg.setLayoutManager(new GridLayoutManager(this, 4));
        rvSuggestimg.setHasFixedSize(true);
        rvSuggestimg.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case IMAGE_ITEM_ADD:
                //打开选择,本次允许选择的数量
                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                Intent intent = new Intent(this, ImageGridActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT);
                break;
            default:
                //打开预览
                Intent intentPreview = new Intent(this, ImagePreviewDelActivity.class);
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) adapter.getImages());
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS,true);
                startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                for (ImageItem imageItem : images) {
                    if (!hashMapImages.containsKey(imageItem.path)) {
                        hashMapImages.put(imageItem.path, "");
                        selImageList.add(imageItem);
                    }
                }
                adapter.setImages(selImageList);
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                selImageList.clear();
                selImageList.addAll(images);
                adapter.setImages(selImageList);
            }
        }
    }

    private void getSuggestionChoices() {
        OkGo.get(Urls.PersonSuggestionChoicesURL)
                .tag(this)
                .execute(new JsonCallback<IycResponse<SuggestionChoices>>(this) {
                    @Override
                    public void onSuccess(IycResponse<SuggestionChoices> suggestionChoicesIycResponse, Call call, Response response) {
                        suggestionChoices = suggestionChoicesIycResponse.getData();
                        if (suggestionChoices != null && suggestionChoices.getSuggestionChoices().size() != 0) {
                            mChoicesItems = new String[suggestionChoices.getSuggestionChoices().size()];
                            for (int i = 0; i < suggestionChoices.getSuggestionChoices().size(); i++) {
                                mChoicesItems[i] = suggestionChoices.getSuggestionChoices().get(i);
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {

                    }
                });

    }

    private void setChoicesDialog() {
        final NormalListDialog dialog = new NormalListDialog(this, mChoicesItems);
        dialog.layoutAnimation(null)
                .isTitleShow(false)//
                .show();
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                tvHint.setText(mChoicesItems[position]);
                dialog.dismiss();
            }
        });
    }


    private void submitSuggestion() {
        if(TextUtils.isEmpty(etPhone.getText().toString())){
            ToastCompat.makeText(context,getString(R.string.write_down_telephone),Toast.LENGTH_SHORT).show();
            return;
        }
        int versionCodeTmp = appContext.getPackageInfo().versionCode;
        showUpDialog();
        for (ImageItem img : selImageList) {
            files.add(new File(img.path));
        }
        long userID = CommonUtil.getUserId();
        //shao add begin
        String versionStr=CommonUtil.getVersion(this);
        String suggestStr=editSuggest.getText().toString()+"_android_"+versionStr;
        //shao add end
        if (files.size() != 0) {
            OkGo.post(Urls.PersonSubmitSuggestionURL)
                    .tag(this)
                    .params("choices", tvHint.getText().toString())
                    //.params("suggestion", editSuggest.getText().toString())
                    .params("suggestion",suggestStr)
                    .params("telephone", etPhone.getText().toString())
                    .params("userId", (int) userID)
                    .params("deviceType", DeviceType.ANDROID_3)
                    .params("versionCode",versionCodeTmp)
                    .addFileParams("files", files)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            Logger.i("" + response.message());
                            ToastCompat.makeText(context, "问题已提交", Toast.LENGTH_SHORT).show();
                            dismissLoadingDialig();
                            finish();
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            ToastCompat.makeText(context, "问题提交失败", Toast.LENGTH_SHORT).show();
                            dismissLoadingDialig();
                        }
                    });
        } else {
            OkGo.get(Urls.PersonSubmitSuggestionNoImageURL)
                    .tag(this)
                    .params("choices", tvHint.getText().toString())
                    //.params("suggestion", editSuggest.getText().toString())
                    .params("suggestion",suggestStr)
                    .params("telephone", etPhone.getText().toString())
                    .params("userId", (int) userID)
                    .params("versionCode",versionCodeTmp)
                    .params("deviceType", DeviceType.ANDROID_3)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            Logger.i("" + response.message());
                            ToastCompat.makeText(context, "问题已提交", Toast.LENGTH_SHORT).show();
                            dismissLoadingDialig();
                            finish();
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            ToastCompat.makeText(context, "问题提交失败", Toast.LENGTH_SHORT).show();
                            dismissLoadingDialig();
                        }
                    });
        }
    }

    @OnClick({R.id.ll_suggest_choice, R.id.fb_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_suggest_choice:
                if (mChoicesItems != null) {
                    setChoicesDialog();
                }
                break;
            case R.id.fb_submit:
                if (isInput()) {
                    submitSuggestion();
                } else {
                    ToastCompat.makeText(context, getString(R.string.not_input_error), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public boolean isInput() {
        if (tvHint.getText().toString().equals("点击选择反馈问题") && editSuggest.getText().toString().equals("") && etPhone.getText().toString().equals("")) {
            return false;
        }
        return true;
    }
}
