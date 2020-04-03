package com.iyangcong.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.CircleLabel;
import com.iyangcong.reader.bean.DiscoverCreateCircle;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.enumset.StateEnum;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.CircleBaseDialog;
import com.iyangcong.reader.ui.LimitedEdittext;
import com.iyangcong.reader.ui.TagGroup;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

public class DiscoverNewCircle3 extends SwipeBackActivity {

    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
//    @BindView(R.id.discover_new_circle3_image)
//    ImageView discoverNewCircle3Image;
    @BindView(R.id.activity_discover_new_circle3)
    LinearLayout activityDiscoverNewCircle3;
    @BindView(R.id.discover_new_circle3_tv)
    TextView discoverNewCircle3Tv;
    //    @BindView(R.id.et_circle_label)
//    EditText etCircleLabel;
    @BindView(R.id.tag_group)
    TagGroup tagGroup;
    @BindView(R.id.et_review_editor)
    LimitedEdittext etReviewEditor;
    private int maxLine= 150;
    private List<CircleLabel> labelList = new ArrayList<>();
    private CircleBaseDialog circleBaseDialog;
    private DiscoverCreateCircle receivedCircle;
    private StateEnum mStateEnum;
    private int mGroupId;

    @OnClick({R.id.btnBack, R.id.btnFunction})
    void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnFunction:
                if(checkInfo()){
                    switch (mStateEnum){
                        case MODIFY:
                            modifyCircleInfo(receivedCircle,mGroupId);
                            break;
                        case CREATE:
                            Intent intent = new Intent(this,DiscoverNewCircleInviteFriends.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(Constants.CREATE_CIRLCE,receivedCircle);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            break;
                    }
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_new_circle3);
        ButterKnife.bind(this);
        setMainHeadView();
        initView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        receivedCircle = (DiscoverCreateCircle)getIntent().getParcelableExtra(Constants.CREATE_CIRLCE);
        mStateEnum = (StateEnum)getIntent().getSerializableExtra(Constants.CREATE_CIRLE_OR_MODIFY);
        Logger.i("wzp discoverNewCircle3 state:" + mStateEnum);
        mGroupId = getIntent().getIntExtra(Constants.groupId,-1);
        Logger.i("wzp discoverNewCircle3 group:" + mGroupId);
    }

    @Override
    protected void initView() {
        etReviewEditor.setTextWatcher(context,maxLine,getString(R.string.content_toolong));
        if(mStateEnum == StateEnum.MODIFY){
            etReviewEditor.setText(receivedCircle.getGroupdesc());
            String[] string = receivedCircle.getTag().split(",");
            tagGroup.setTags(Arrays.asList(string));
        }
        circleBaseDialog = new CircleBaseDialog(DiscoverNewCircle3.this, labelList);
        circleBaseDialog.noftifyDataChanged();
        circleBaseDialog.setOnDialogDismissListener(new CircleBaseDialog.OnDialogDismissListener() {
            @Override
            public void onDialogDismiss(List<CircleLabel> list) {
                List<String> labelNames = new ArrayList<String>();
                if(tagGroup.getTags().length > 0)
                    for(String tags:tagGroup.getTags())
                        if(!labelNames.contains(tags))
                            labelNames.add(tags);
                for (CircleLabel label:list) {
                    if(!labelNames.contains(label.getTagName())){
                        labelNames.add(label.getTagName());
                    }
                }
                tagGroup.setTags(labelNames);
            }
        });

    }


    //限制label名字的长度，使之不超过12个字
    private String limitStringLength(String str){
        if(str.length()>12)
            return str.substring(0,12);
        return str;
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText(mStateEnum == StateEnum.MODIFY?getString(R.string.edit_circle):getString(R.string.create_circle));
        btnBack.setImageResource(R.drawable.btn_back);
        btnFunction.setImageResource(R.drawable.ic_next);
        btnFunction.setVisibility(View.VISIBLE);
        discoverNewCircle3Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleBaseDialog.show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataFromNetwork();
    }

    private boolean checkInfo() {
        StringBuilder sb = new StringBuilder("");
        tagGroup.submitTag();
        int size = tagGroup.getTags().length;
        String[] tags = tagGroup.getTags();
//                circleBaseDialog.getLabelList().size();
        if(size == 0) {

        }else if(size == 1)
            sb.append(tags[0]);
//            sb.append(circleBaseDialog.getLabelList().get(0).getTagName());
        else{
//            sb.append(circleBaseDialog.getLabelList().get(0).getTagName());
//            for(CircleLabel circleLabel:circleBaseDialog.getLabelList().subList(1,size))
//                sb.append(","+circleLabel.getTagName());
            sb.append(tags[0]);
            for (int i = 1; i < tags.length; i++) {
                sb.append("," + tags[i]);
            }
        }

        receivedCircle.setTag(sb.toString());
        if(etReviewEditor.getText() != null)
            receivedCircle.setGroupdesc(etReviewEditor.getText().toString());
        if(!isNull(context,receivedCircle.getTag(),"请输入标签")&&!isNull(context,receivedCircle.getGroupdesc(),"请输入圈子描述"))
            return true;
        return false;
    }

     /**
        *  方法作用  :
        *  方法参数  ：
        *  modified by WuZepeng  in 18:54
        *
        */


    private void getDataFromNetwork() {
        OkGo.get(Urls.DiscoverCircleLabelURL)
                .execute(new JsonCallback<IycResponse<List<CircleLabel>>>(this) {
                    @Override
                    public void onSuccess(IycResponse<List<CircleLabel>> listIycResponse, Call call, Response response) {
                        labelList.clear();
                        for (CircleLabel label : listIycResponse.getData())
                            labelList.add(label);
                        circleBaseDialog.noftifyDataChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        ToastCompat.makeText(context,getString(R.string.net_error_tip),Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void modifyCircleInfo(DiscoverCreateCircle createCircle, int groupId){
        if(createCircle.getCover().contains("http")&&createCircle.getCover().contains(createCircle.getPath())){
            String temp = createCircle.getCover().replace(createCircle.getPath(),"");
            createCircle.setCover(temp);
        }
        Logger.i("wzp relativePath:" + createCircle.getCover());
        modifyCircleInfo(createCircle.getAuthority(),createCircle.getCategory(),createCircle.getCover(),createCircle.getGroupdesc(),groupId,createCircle.getGroupname());
    }

    private void modifyCircleInfo(int authority, int category, String cover, String groupdesc, final int groupId, String groupName){
        OkGo.get(Urls.URL+"/groups/modifygroupinfo")
                .params("authority",authority)
                .params("category",category)
                .params("cover",cover)
                .params("groupdesc",groupdesc)
                .params("groupid",groupId)
                .params("groupname",groupName)
                .execute(new JsonCallback<IycResponse<String>>(context) {
                    @Override
                    public void onSuccess(IycResponse<String> stringIycResponse, Call call, Response response) {
                        Intent intent = new Intent(context,DiscoverModifyCircleBook.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt(Constants.groupId,mGroupId);
                        bundle.putParcelable(Constants.CREATE_CIRLCE,receivedCircle);
                        intent.putExtras(bundle);
                        startActivity(intent);
//                        AppManager.getAppManager().finishActivity(DiscoverCircleDetailActivity.class,DiscoverNewCircle2.class);
//                        finish();
//                        Intent intent=new Intent(context,DiscoverCircleDetailActivity.class);
//                        intent.putExtra(Constants.circleId,groupId);
//                        intent.putExtra(Constants.circleName,receivedCircle.getGroupname());
//                        intent.putExtra(Constants.CIRCLE_CATEGORY,receivedCircle.getCategory());
//                        startActivity(intent);
                        Logger.i("category:" + receivedCircle.getCategory());
                    }
                });
    }
}
