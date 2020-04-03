
package com.iyangcong.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.MineUserSetting;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.interfaceset.DeviceType;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.ui.SettingItem;
import com.iyangcong.reader.ui.dialog.SettingDialog;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.NewGildeImageLoader;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
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

import static com.iyangcong.reader.activity.MineSuggestActivity.REQUEST_CODE_ACIVITE;
import static com.iyangcong.reader.activity.MineSuggestActivity.REQUEST_CODE_SELECT;
import static com.iyangcong.reader.utils.NotNullUtils.isNull;

/**
 * Created by Administrator on 2017/3/20.
 */

public class MinePersonSettingActivity extends SwipeBackActivity implements SettingDialog.DialogCallback {
    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.pic)
    ImageView pic;
    @BindView(R.id.arrow)
    ImageView arrow;
    @BindView(R.id.item_pic)
    RelativeLayout itemPic;
    @BindView(R.id.si_mine_nickName)
    SettingItem siMineNickName;
    @BindView(R.id.si_mine_accounts)
    SettingItem siMineAccounts;
    @BindView(R.id.si_mine_phone)
    SettingItem siMinePhone;
    @BindView(R.id.si_mine_name)
    SettingItem siMineName;
    @BindView(R.id.si_mine_sex)
    SettingItem siMineSex;
    @BindView(R.id.si_mine_region)
    SettingItem siMineRegion;
    @BindView(R.id.si_mine_introduction)
    SettingItem siMineIntroduction;
    @BindView(R.id.si_mine_modify_passward)
    SettingItem siMineModifyPassward;
    @BindView(R.id.si_mine_schoolaccount)
    SettingItem siMineSchoolAccount;

    private final int SI_MINE_NICKNAME = 0;
    private final int SI_MINE_PHONE = 1;
    private final int SI_MINE_NAME = 2;
    private final int SI_MINE_SEX = 3;
    private final int SI_MINE_REGION = 4;
    private final int SI_MINE_INTRODUCTION = 5;
    private ArrayList<ImageItem> imageItems = new ArrayList<>();
    private MineUserSetting mineUserSetting;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private int userType =-1;

    public MineUserSetting getMineUserSetting() {
        return mineUserSetting;
    }

    public void setMineUserSetting(MineUserSetting mineUserSetting) {
        this.mineUserSetting = mineUserSetting;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_settings);
        ButterKnife.bind(this);
        initView();
        setMainHeadView();
    }

    @Override
    protected void initView() {
        initImagePicker();
        //siMineAccounts.setHintText(sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_USER_ACCOUNT, null));
        loadUserSetting();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText("个人设置");
        btnBack.setImageResource(R.drawable.btn_back);
        btnFunction.setImageResource(R.drawable.btn_keep);
    }

    @OnClick({R.id.btnBack, R.id.btnFunction, R.id.item_pic, R.id.si_mine_nickName, R.id.si_mine_name, R.id.si_mine_sex, R.id.si_mine_region, R.id.si_mine_introduction, R.id.si_mine_modify_passward,R.id.si_mine_schoolaccount})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnFunction:
                break;
            case R.id.item_pic:
                choosePicByTextView();
                break;
            case R.id.si_mine_nickName:
                settingDialog(siMineNickName.getHintText(), getString(R.string.setting_nickName), SI_MINE_NICKNAME);
                break;
            case R.id.si_mine_name:
                settingDialog(siMineName.getHintText(), getString(R.string.setting_nickName), SI_MINE_NAME);
                break;
            case R.id.si_mine_sex:
                setSexDialog();
                break;
            case R.id.si_mine_region:
//                settingDialog (siMineRegion.getHintText(),getString(R.string.setting_region),SI_MINE_REGION);
                break;
            case R.id.si_mine_introduction:
                settingDialog(siMineIntroduction.getHintText(), getString(R.string.setting_introduction), SI_MINE_INTRODUCTION);
                break;
            case R.id.si_mine_modify_passward:
                startActivity(new Intent(this, ModifyPassWordActivity.class));
                break;
            case R.id.si_mine_schoolaccount:
                if(userType==0){
                    Intent intent = new Intent(this,ActivationActivity.class);
                    intent.putExtra(Constants.NICK_NAME, mineUserSetting.getUserName());
                    // intent.putExtra(Constants.USER_NAME, mineUserSetting.);
                    intent.putExtra(Constants.USER_ACCOUNT,mineUserSetting.getUserAccounts());
                    //intent.putExtra(Constants.USER_ID,mineUserSetting.getu)
                    //startActivity(intent);
                    startActivityForResult(intent, REQUEST_CODE_ACIVITE);
                }
//                Intent intent = new Intent(this,ActivationActivity.class);
//                    intent.putExtra(Constants.NICK_NAME, mineUserSetting.getUserName());
//                    // intent.putExtra(Constants.USER_NAME, mineUserSetting.);
//                    intent.putExtra(Constants.USER_ACCOUNT,mineUserSetting.getUserAccounts());
//                    //intent.putExtra(Constants.USER_ID,mineUserSetting.getu)
//                    startActivity(intent);
                break;
            default:break;
        }
    }

    private void choosePicByTextView() {
        ImagePicker.getInstance().setSelectLimit(1);
        Intent intent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT);
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
        }else if(requestCode == REQUEST_CODE_ACIVITE){
            if(resultCode==0){
                siMineSchoolAccount.setHintText("已激活");
            }
        }
        imageItems.addAll(images);
        Logger.i("imageItems1" + imageItems.size());
        showPic(imageItems);
    }

    private void showPic(List<ImageItem> list) {
        if (list.size() == 0) {

        } else {
            new GlideImageLoader().displayProtrait(context, list.get(0).path, pic);
            uploadUserSetting();
        }
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new NewGildeImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(true);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(1);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    public void settingDialog(String setstr, String title, int sign) {
        final SettingDialog dialog = new SettingDialog(this, setstr, title, sign);
        dialog.setDialogCallback(this);
        dialog.show();
        if (title.equals(getString(R.string.setting_introduction))) {
            dialog.setContentLines(3);
        }
    }

    @Override
    public String getEditText(String editText, int sign) {
        switch (sign) {
            case 0:
                siMineNickName.setHintText(editText);
                uploadUserSetting();
                break;
//            case 1:siMinePhone.setHintText(editText);uploadUserSetting();break;
            case 2:
                siMineName.setHintText(editText);
                uploadUserSetting();
                break;
//            case 3:siMineSex.setHintText(editText);break;
//            case 4:siMineRegion.setHintText(editText);uploadUserSetting();break;
            case 5:
                siMineIntroduction.setHintText(editText);
                uploadUserSetting();
                break;
        }
        return null;
    }

    private void setSexDialog() {
        final String[] mStringItems = {"男", "女"};
        final NormalListDialog dialog = new NormalListDialog(this, mStringItems);
        dialog.layoutAnimation(null)
                .isTitleShow(false)//
                .show();
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    siMineSex.setHintText("男");
                } else if (position == 1) {
                    siMineSex.setHintText("女");
                }
                dialog.dismiss();
                uploadUserSetting();
            }
        });
    }

    private void loadUserSetting() {
        showLoadingDialog();
        OkGo.get(Urls.PersonUserSettingLoad)
                .tag(this)
                .params("userId", CommonUtil.getUserId())
                .execute(new JsonCallback<IycResponse<MineUserSetting>>(context) {
                    @Override
                    public void onSuccess(IycResponse<MineUserSetting> mineUserSettingIycResponse, Call call, Response response) {
                        dismissLoadingDialig();
                        setMineUserSetting(mineUserSettingIycResponse.getData());
                        Logger.i("load succeed" + mineUserSettingIycResponse.getData().toString());
//                        siMineNickName.setHintText(mineUserSetting.getUserNickname());
                        siMineAccounts.setHintText((mineUserSetting.getUserAccounts()));
                        siMinePhone.setHintText(mineUserSetting.getUserPhoneNum());
                        siMineName.setHintText(mineUserSetting.getUserName());
                        siMineSex.setHintText(mineUserSetting.getUserSex());
//                        siMineRegion.setHintText(mineUserSetting.getUserRegion());
                         userType =mineUserSetting.getUserType();
                        if(userType==0){
                            siMineSchoolAccount.setHintText("激活");
                        }else if(userType==1){
                            siMineSchoolAccount.setHintText("已激活");
                        }else if(userType==3||userType==2){
                            siMineSchoolAccount.setVisibility(View.GONE);
                        }
                        siMineIntroduction.setHintText(mineUserSetting.getUserIntroduction());
                        new GlideImageLoader().displayProtrait(context, mineUserSetting.getUserHead(), pic);
                        SharedPreferenceUtil.getInstance().putString(SharedPreferenceUtil.USER_PORTAIT_URL, mineUserSetting.getUserHead());
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dismissLoadingDialig();
                    }
                });
    }

    private void uploadUserSetting(){
        uploadUserSetting(siMineName.getHintText());
    }

    private void uploadUserSetting(String nickName) {
        if(isNull(context,nickName,"")){
            ToastCompat.makeText(context,"请输入昵称", Toast.LENGTH_SHORT).show();
            return;
        }
        OkGo.post(Urls.PersonUserSettingUpload)
                .tag(this)
                .params("birthday", "1999-02-31")
                .params("gender", siMineSex.getHintText().equals("男")?"1":"0")
                .params("languageVersion", "1")
                .params("languageDifficuty", "1")
                .params("majarType", "63")
                .params("nickName",nickName)
                .params("personIntro", siMineIntroduction.getHintText())
                .params("userId", CommonUtil.getUserId() + "")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
//                        ToastCompat.makeText(context,"个人信息上传成功"+response.message(),300);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        ToastCompat.makeText(context, "个人信息上传失败" + response.message(), 300);
                    }
                });

        String head = new String();
        if (imageItems.size() == 0) {

        } else {
            head = imageItems.get(0).path;
            OkGo.post(Urls.PersonUserHeadupLoad)
                    .tag(this)
                    .params("userId", CommonUtil.getUserId())
//                    .params("userId", "1")
                    .params("deviceType ", DeviceType.WEB_1)
                    .params("file", new File(head))
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            ToastCompat.makeText(context, "头像" + response.message(), 300);
                            dismissLoadingDialig();
                        }

                        @Override
                        public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                            super.upProgress(currentSize, totalSize, progress, networkSpeed);
                            showUpDialog();
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            ToastCompat.makeText(context, "头像上传失败", 300);
                            dismissLoadingDialig();
                        }
                    });
        }
    }


}
