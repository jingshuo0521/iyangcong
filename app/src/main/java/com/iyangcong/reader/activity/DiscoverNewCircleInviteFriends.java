package com.iyangcong.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.DiscoverInviteFriendsAdapter;
import com.iyangcong.reader.bean.CircleLabel;
import com.iyangcong.reader.bean.DiscoverCircleDegree;
import com.iyangcong.reader.bean.DiscoverCircleFriends;
import com.iyangcong.reader.bean.DiscoverCircleGroup;
import com.iyangcong.reader.bean.DiscoverCreateCircle;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.CircleBaseDialog;
import com.iyangcong.reader.ui.ClearEditText;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.LoadCountHolder;
import com.iyangcong.reader.utils.SearchLocalFriendUtils;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class DiscoverNewCircleInviteFriends extends SwipeBackActivity {

    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.ceSearch)
    ClearEditText ceSearch;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.activity_invite_friends)
    LinearLayout activityInviteFriends;
    @BindView(R.id.invite_friends_lv)
    RecyclerView inviteFriendsLv;
    private DiscoverInviteFriendsAdapter discoverInviteFriendsAdapter;
    private List<DiscoverCircleFriends> friendlist;
    private long userId;
    private CircleBaseDialog dialog;
    private List<DiscoverCircleDegree> degreeList = new ArrayList<>();
    private List<CircleLabel> tempList = new ArrayList<>();
    private DiscoverCreateCircle receivedCircle;
    private List<LoadCountHolder> holderList = new ArrayList<>();
    @OnClick({R.id.btnBack,R.id.btnFunction})
    void onBtnClick(View view){
        switch (view.getId()){
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnFunction:
                loadDialog();
            case R.id.tv_search:
                doSearch();
                break;

        }
    }

    private void doSearch() {

    }

    /**
    *  方法作用  :  加载dialog
    *  方法参数  ：   无
    *  modified by WuZepeng  in 16:33
    *
    */
    private void loadDialog(){
        dialog = new CircleBaseDialog(context,tempList);
        dialog.show();
        dialog.setOnDialogItemSelectedListener(new CircleBaseDialog.OnDialogItemSelectedListener() {
            @Override
            public void onDialogItemSelected(int position) {
                DiscoverCircleDegree degree = degreeList.get(position);
                checkCircle(friendlist,degree);
                Intent intent=new Intent(context,DiscoverNewCircleChooseBook.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.CREATE_CIRLCE,receivedCircle);
                intent.putExtras(bundle);
                startActivity(intent);
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);
        ButterKnife.bind(this);
        initView();
        setMainHeadView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        friendlist=new ArrayList<>();
        receivedCircle = getIntent().getParcelableExtra(Constants.CREATE_CIRLCE);
        userId = CommonUtil.getUserId();
    }

    private List<CircleLabel> dialogsToLabels(List<DiscoverCircleDegree> degreeList){
        List<CircleLabel> list = new ArrayList<>();
        for(DiscoverCircleDegree degree:degreeList){
            list.add(new CircleLabel(degree.getTitle(),degree.getId()));
        }
        return list;
    }

    private boolean checkCircle(List<DiscoverCircleFriends> flist,DiscoverCircleDegree degree){
        receivedCircle.setSfriends(getFriendStr(flist));
        receivedCircle.setDegree(degree.getId());
        if(!receivedCircle.getSfriends().equals("")&&receivedCircle.getDegree()!=0)
            return true;
        return false;
    }

    /**
     * 获得被选中的好友
     * @param flist
     * @return
     */
    public List<DiscoverCircleFriends> getChooseFriend(List<DiscoverCircleFriends> flist){
        List<DiscoverCircleFriends> tempList = new ArrayList<>();
        for (DiscoverCircleFriends friends:flist)
                if(friends.isChecked())
                    tempList.add(friends);
                else
                    tempList.remove(friends);
        return tempList;
    }

    /**
     * 取选择到地好友地id拼接成后台需要地数据格式
     * @param flist
     * @return string
     */
    public String getFriendStr(List<DiscoverCircleFriends> flist){
        List<DiscoverCircleFriends> tList = getChooseFriend(flist);
        StringBuilder sb = new StringBuilder("");
        if(tList.size() == 0)
            return "-1";
        else if(tList.size() == 1)
            return tList.get(0).getUserId() + "";
        else if(tList.size() > 1) {
            sb.append(tList.get(0).getUserId()).append("");
            for(DiscoverCircleFriends fri:tList.subList(1,tList.size()))
                sb.append(",").append(fri.getUserId());
        }
        return sb.toString();
    }

    @Override
    protected void initView() {
        initVaryViewHelper(context, inviteFriendsLv, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onResume();
            }
        });
        receivedCircle = (DiscoverCreateCircle)getIntent().getParcelableExtra(Constants.CREATE_CIRLCE);
        discoverInviteFriendsAdapter=new DiscoverInviteFriendsAdapter(this,friendlist);
//        discoverInviteFriendsAdapter.setClickListener(new DiscoverInviteFriendsAdapter.ClickListener() {
//            @Override
//            public void clicked(int position) {
//                boolean tempState = friendlist.get(position).isChecked();
//                friendlist.get(position).setChecked(!tempState);
//                LoadCountHolder holder = new LoadCountHolder();
//                holder.setPage(position);
//                if(tempState){
//                    for (LoadCountHolder countHolder:holderList)
//                        if(countHolder.getPage()== position)
//                            holder = countHolder;
//                    holderList.remove(holder);
//                }else{
//                    holderList.add(holder);
//                }
//                Logger.i("holderList:"+holderList);
//                discoverInviteFriendsAdapter.notifyDataSetChanged();
//            }
//        });
        inviteFriendsLv.setAdapter(discoverInviteFriendsAdapter);
        inviteFriendsLv.setLayoutManager(new LinearLayoutManager(context));
        initSearch();
//        inviteFriendsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                boolean tempState = friendlist.get(i).isChecked();
//                friendlist.get(i).setChecked(!tempState);
//                discoverInviteFriendsAdapter.notifyDataSetChanged();
//            }
//        });
    }

    private void initSearch(){
        ceSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Logger.e("wzp afterTextChanged:" + editable.toString());
                if(editable.toString().equals("")){
                    clearSearch();
                }else{
                    SearchLocalFriendUtils.getSearchedList(editable.toString(),friendlist);
                    discoverInviteFriendsAdapter.notifyDataSetChanged();
                }
            }
        });
        ceSearch.setClearListener(new ClearEditText.ClearListener() {
            @Override
            public void clear() {
                ceSearch.setText("");
                clearSearch();
            }
        });
    }

    private void clearSearch(){
        for(DiscoverCircleFriends friends:friendlist){
            if(!friends.isVisibile()){
                friends.setVisibile(true);
            }
        }
        discoverInviteFriendsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText(R.string.invite_friends);
        btnBack.setImageResource(R.drawable.btn_back);
        btnFunction.setImageResource(R.drawable.ic_next);
        btnFunction.setVisibility(View.VISIBLE);
        ceSearch.setHint(R.string.input_username);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataFromNetwork();
    }

    private void getDataFromNetwork(){
        OkGo.get(Urls.DiscoverCircleGetPersonAndAllFriends)
                .params("userId",userId)
                .execute(new JsonCallback<IycResponse<List<DiscoverCircleGroup>>>(this) {
                    @Override
                    public void onSuccess(IycResponse<List<DiscoverCircleGroup>> listIycResponse, Call call, Response response) {
                        List<DiscoverCircleFriends> checkedFriends = getChooseFriend(friendlist);
                        friendlist.clear();
                        for(DiscoverCircleGroup group:listIycResponse.getData())
                            for(DiscoverCircleFriends friends:group.getFriendsList()) {
                                for (DiscoverCircleFriends checked : checkedFriends) {
                                    if (checked.getUserId() == friends.getUserId()) {
                                        friends.setChecked(true);
                                        break;
                                    }
                                }
                                if(!friendlist.contains(friends)){
                                    friends.setVisibile(true);
                                    friendlist.add(friends);
                                }
                            }
                        discoverInviteFriendsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }
                });

        OkGo.get(Urls.DiscoverCircleMemberDegree)
                .execute(new JsonCallback<IycResponse<List<DiscoverCircleDegree>>>(this) {
                    @Override
                    public void onSuccess(IycResponse<List<DiscoverCircleDegree>> discoverCircleDegrees, Call call, Response response) {
                        degreeList.clear();
                        tempList.clear();
                        for(DiscoverCircleDegree degree:discoverCircleDegrees.getData())
                            degreeList.add(degree);
                        tempList = dialogsToLabels(degreeList);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }
                });
    }

}
