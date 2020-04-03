package com.iyangcong.reader.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.DiscoverCircleDetailActivity;
import com.iyangcong.reader.activity.DiscoverTopicActivity;
import com.iyangcong.reader.activity.MinePageActivity;
import com.iyangcong.reader.bean.DiscoverCircleDetail;
import com.iyangcong.reader.bean.DiscoverTopic;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.ninegridview.ImageInfo;
import com.iyangcong.reader.ui.ninegridview.NineGridView;
import com.iyangcong.reader.ui.ninegridview.NineGridViewClickAdapter;
import com.iyangcong.reader.ui.textview.AutoLinkTextView;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.HtmlParserUtils;
import com.iyangcong.reader.utils.StringUtils;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static com.j256.ormlite.field.DataPersisterManager.clear;
import static java.util.Collections.addAll;

/**
 * 发现页面话题适配器
 */
public class DiscoverTopicAdapter extends RecyclerView.Adapter {

    private Context context;

    private DiscoverCircleDetail discoverCircleDetail;
    private LayoutInflater mLayoutInflater;
    private List<DiscoverTopic> discoverTopicList;
    private boolean isMyself;
    private boolean isTrue;


//    public DiscoverTopicAdapter(Context mContext, List<DiscoverTopic> discoverTopicList) {
//        this(mContext, discoverTopicList, false);
//    }

    public DiscoverTopicAdapter(Context mContext, List<DiscoverTopic> discoverTopicList, boolean isMyself) {
        this(mContext, discoverTopicList,isMyself,false,null);
    }

    public DiscoverTopicAdapter(Context mContext, List<DiscoverTopic> discoverTopicList,boolean isMyself, boolean isTrue,DiscoverCircleDetail discoverCircleDetail) {

        this.context = mContext;
        this.isMyself = isMyself;
        this.discoverTopicList = discoverTopicList;
        this.isTrue = isTrue;
        this.discoverCircleDetail = discoverCircleDetail;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void updateData(List<DiscoverTopic> discoverTopicList) {
        clear();
        addAll(discoverTopicList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DiscoverTopicViewHolder(context, mLayoutInflater.inflate(R.layout.item_discover_topic, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DiscoverTopicViewHolder discoverTopicViewHolder = (DiscoverTopicViewHolder) holder;
        discoverTopicViewHolder.setData(discoverTopicList.get(position));
    }

//    public interface DialogItemOnClickListener {
//
//        void onTop();
//
//        void onCancel();
//
//    }
//
//    public void setItemOnClickListener(DialogItemOnClickListener itemOnClickListener) {
//        this.itemOnClickListener = itemOnClickListener;
//    }


    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return discoverTopicList == null ? 0 : discoverTopicList.size();
    }

    public class DiscoverTopicViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;
        @BindView(R.id.iv_user_image)
        ImageView ivUserImage;
        @BindView(R.id.iv_logoimg)
        ImageView ivLogoImg;
        @BindView(R.id.tv_user_name)
        TextView tvUserName;
        @BindView(R.id.tv_topic_title)
        TextView tvTopicTitle;
        @BindView(R.id.tv_topic_describe)
        AutoLinkTextView tvTopicDescribe;
        @BindView(R.id.interactive_info)
        RelativeLayout interactiveInfo;
        @BindView(R.id.ngv_image)
        NineGridView ngvImage;
        @BindView(R.id.tv_deliver_time)
        TextView tvDeliverTime;
        @BindView(R.id.tv_topic_like_num)
        TextView tvTopicLikeNum;
        @BindView(R.id.iv_topic_message)
        ImageView ivTopicMessage;
        @BindView(R.id.tv_message_num)
        TextView tvMessageNum;
        @BindView(R.id.book_item)
        LinearLayout llBookItem;
        @BindView(R.id.tv_book_top)
        TextView tvBooktTop;
        @BindView(R.id.im_topic_down)
        TextView ImTopicDown;
        @BindView(R.id.tv_book_title)
        TextView tvBookTitle;
        @BindView(R.id.tv_book_author)
        TextView tvBookAuthor;
        @BindView(R.id.tv_book_version)
        TextView tvBookVersion;
        @BindView(R.id.iv_book_cover)
        ImageView ivBookCover;
        @BindView(R.id.cardView)
        CardView cardView;

        @OnClick({R.id.cardView})
        void onItemClick(View view) {
            switch (view.getId()) {
                case R.id.cardView:
                    DiscoverTopic topic = (DiscoverTopic) llBookItem.getTag();
                    if (topic == null)
                        return;
                    Intent intent = new Intent(context, DiscoverTopicActivity.class);
                    intent.putExtra(Constants.topicId, topic.getTopicId());
                    intent.putExtra(Constants.groupId, topic.getGroupId());
                    intent.putExtra(Constants.TOPIC_ACITIVITY_TITLE, topic.getTopicName());
                    Logger.i("groupIddddd" + topic.getGroupId());
                    context.startActivity(intent);
                    break;
            }
        }

        public DiscoverTopicViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        void setData(final DiscoverTopic discoverTopic) {

//            cardView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//
//                }
//            });
            if(CommonUtil.getActivityByContext(context).getLocalClassName().equals("activity.DiscoverySearchActivity")){
                //从搜索页面过来的就不显示下面的评论标识
                interactiveInfo.setVisibility(View.GONE);
                ivLogoImg.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams params = ivLogoImg.getLayoutParams();
                params.height=context.getResources().getDimensionPixelSize(R.dimen.market_book_topic_height);
                params.width =context.getResources().getDimensionPixelSize(R.dimen.market_book_topic_height);
                ivLogoImg.setLayoutParams(params);
                GlideImageLoader.displayBookCover(mContext,ivLogoImg, discoverTopic.getUserImageUrl());
                ivUserImage.setVisibility(View.GONE);
            }else{
                GlideImageLoader.displayProtrait(mContext, discoverTopic.getUserImageUrl(), ivUserImage);
            }
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DiscoverTopic topic = (DiscoverTopic) llBookItem.getTag();
                    if (topic == null)
                        return;
                    Intent intent = new Intent(context, DiscoverTopicActivity.class);
                    intent.putExtra(Constants.topicId, topic.getTopicId());
                    intent.putExtra(Constants.groupId, topic.getGroupId());
                    intent.putExtra(Constants.TOPIC_ACITIVITY_TITLE, topic.getTopicName());
                    Logger.i("groupIddddd" + topic.getGroupId());
                    context.startActivity(intent);
                }
            });
            ivLogoImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, DiscoverCircleDetailActivity.class);
                    intent.putExtra(Constants.circleId,discoverTopic.getGroupId());
                    //intent.putExtra(Constants.circleName,dcc.getName());
                    context.startActivity(intent);
                }
            });

            ImTopicDown.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    View view1 = View.inflate(context, R.layout.dialog_topic, null);
                    LinearLayout linear1=view1.findViewById(R.id.ll_topic1);
                    LinearLayout linear2=view1.findViewById(R.id.ll_topic2);
                    LinearLayout linear3=view1.findViewById(R.id.ll_topic3);
                    LinearLayout linear4=view1.findViewById(R.id.ll_topic4);
                    LinearLayout linear5=view1.findViewById(R.id.ll_topic5);

                    if (CommonUtil.getUserId()==discoverCircleDetail.getDiscoverCircleDescribe().getCreaterId()){
                        linear3.setVisibility(View.VISIBLE);
                        if (discoverTopic.getStatus()==4){
                            linear2.setVisibility(View.VISIBLE);
                        }else{
                            linear1.setVisibility(View.VISIBLE);
                        }
                        if (discoverTopic.getUserId()==discoverCircleDetail.getDiscoverCircleDescribe().getCreaterId()){
                            linear4.setVisibility(View.VISIBLE);
                        }
                    }else{
                        linear5.setVisibility(View.VISIBLE);
                    }

                    final AlertDialog alertDialog = new AlertDialog.Builder(context)
                            .setView(view1)
                            .create();
                    alertDialog.show();

                    linear1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            int topicNum = 1;
                            for (DiscoverTopic discoverTopic1:discoverTopicList){
                                if (discoverTopic1.getStatus()==4){
                                    topicNum++;
                                }
                            }
                            if (topicNum<=5){
                                tvBooktTop.setVisibility(View.VISIBLE);
                                setTopicstatus(discoverTopic.getTopicId());
                                discoverTopic.setStatus(4);
                                //设置置顶时间
                                discoverTopic.setTime(System.currentTimeMillis());
                                refreshView();
                                notifyDataSetChanged();
                            }else{
                                Toast.makeText(context, "最多置顶5个话题！" , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    linear2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            tvBooktTop.setVisibility(View.INVISIBLE);
                            setTopicstatus(discoverTopic.getTopicId());
                            discoverTopic.setStatus(1);
                            refreshView();
                            notifyDataSetChanged();
                        }
                    });
                    linear3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            deleteTopicComment(discoverTopic);
                            discoverTopicList.remove(discoverTopic);
                            notifyDataSetChanged();
                        }
                    });
                    linear4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(context, "编辑" , Toast.LENGTH_SHORT).show();

                        }
                    });
                    linear5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            reportComment(discoverTopic);
                        }
                    });
//                    return true;
                }
            });

            if (isTrue){
                ImTopicDown.setVisibility(View.VISIBLE);
            }
            llBookItem.setVisibility(View.GONE);
            if (discoverTopic == null) {
                cardView.setVisibility(View.GONE);
                return;
            }
            if(discoverTopic.getStatus()==4){
                tvBooktTop.setVisibility(View.VISIBLE);
            }else{
                tvBooktTop.setVisibility(View.GONE);
            }
            cardView.setVisibility(View.VISIBLE);
            tvUserName.setText(discoverTopic.getUserName());
            tvTopicTitle.setText(StringUtils.delHTMLTag(discoverTopic.getTopicName()));
            tvTopicDescribe.setText(discoverTopic.getTopicDescribe());
            tvDeliverTime.setText(discoverTopic.getTopicTime());
            tvTopicLikeNum.setText(discoverTopic.getTopicLike() + "");
            tvMessageNum.setText(discoverTopic.getMessageNum() + "");
            llBookItem.setTag(discoverTopic);


//            GlideImageLoader.displaysetdefault(mContext,ivUserImage,discoverTopic.getUserImageUrl(),R.drawable.ic_head_default);
            String html = discoverTopic.getTopicDescribe();
            tvTopicDescribe.setText(Html.fromHtml(HtmlParserUtils.getContent(html)));
//            String ip = Urls.URL.substring(0,Urls.URL.length()-1);
            List<String> urls = HtmlParserUtils.getPicUrl(discoverTopic.getPath(), discoverTopic.getTopicDescribe());
            List<ImageInfo> imageInfoList = new ArrayList<ImageInfo>();
            for (String url : urls) {
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.setBigImageUrl(url);
                imageInfo.setThumbnailUrl(url);
                imageInfoList.add(imageInfo);
                Logger.i("imageInfo" + imageInfo.toString());
            }
            NineGridViewClickAdapter nineGridViewAdapter = new NineGridViewClickAdapter(mContext, imageInfoList);
            ngvImage.setAdapter(nineGridViewAdapter);
            if (isMyself == false) {
                ivUserImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, MinePageActivity.class);
                        intent.putExtra(Constants.USER_ID, discoverTopic.getUserId() + "");
                        intent.putExtra(Constants.IS_MYSELF, false);
                        mContext.startActivity(intent);
                    }
                });
            }
        }





        public void setData(List<DiscoverTopic> list) {
            for (DiscoverTopic tp : list) {
                setData(tp);
            }
        }
    }
    private void refreshView() {
        //如果不调用sort方法，是不会进行排序的，也就不会调用compareTo
        Collections.sort(discoverTopicList);
        this.updateData(discoverTopicList);
    }


    private void deleteTopicComment(final DiscoverTopic discoverTopic){
        OkGo.get(Urls.TopicDeleteComment)
                .params("userId", CommonUtil.getUserId())
                .params("itemId",discoverTopic.getTopicId())
                .params("type",1)//mType=1 话题  2 回复
                .execute(new JsonCallback<IycResponse<String>>(context){
                    @Override
                    public void onSuccess(IycResponse<String> IycResponse, Call call, Response response) {

                    }
                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);

                    }
                });
    }

    private void setTopicstatus(final int topicId) {
        OkGo.get(Urls.Settopicstatus)
                .params("topicId",topicId)
                .execute(new JsonCallback<IycResponse<String>>(context){
                    @Override
                    public void onSuccess(IycResponse<String> IycResponse, Call call, Response response) {
                    }
                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }
                });
    }

    private void reportComment(final DiscoverTopic discoverTopic) {
        OkGo.get(Urls.DiscoverReportComment)
                .params("userid", discoverTopic.getUserId())
                .params("id",discoverTopic.getTopicId())
                .params("type",1)//mType=1 话题  2 回复
                .execute(new JsonCallback<IycResponse<String>>(context){
                    @Override
                    public void onSuccess(IycResponse<String> IycResponse, Call call, Response response) {
                        Toast.makeText(context, "举报成功!" , Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(context, "举报失败！" , Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
