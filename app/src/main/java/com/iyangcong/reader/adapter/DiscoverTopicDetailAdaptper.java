package com.iyangcong.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.MinePageActivity;
import com.iyangcong.reader.bean.DiscoverCircleMember;
import com.iyangcong.reader.bean.DiscoverComment;
import com.iyangcong.reader.bean.DiscoverTopicComments;
import com.iyangcong.reader.bean.DiscoverTopicDetail;
import com.iyangcong.reader.bean.DiscoverTopicDetailBean;
import com.iyangcong.reader.bean.ReplyComment;
import com.iyangcong.reader.interfaceset.OnLikeClickedListener;
import com.iyangcong.reader.interfaceset.OnLikedButtonClicked;
import com.iyangcong.reader.ui.ninegridview.ImageInfo;
import com.iyangcong.reader.ui.ninegridview.NineGridView;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.RichTextUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

/**
 * Created by WuZepeng on 2017-03-18.
 */

public class DiscoverTopicDetailAdaptper extends RecyclerView.Adapter {
    /**
     * 话题详情
     */
    private final int TOPIC_DETAIL = 0;
    /**
     * 话题点赞列表
     */
    private final int TOPIC_LIKE = 1;
    /**
     * 话题详情页全部回复的标题
     */
//    private final int TOPIC_COMMENT_TITLE = 2;
    /**
     * 话题评论
     */
    private final int TOPIC_COMMENT = 2;

    private static final int LIKE_TOPIC_DESC = 1;
    public static final int LIKE_TOPIC_COMMENT = 2;

    private int crruentType = TOPIC_DETAIL;
    private Context mContext;
    private DiscoverTopicDetailBean discoverTopicDetailBean;
    private LayoutInflater inflater;
    //shao add begin
    OnContentFromClickedListener onContentFromClickedListener;
    //shao add end
    private OnAddCommentClickedListener onAddCommentClickedListener;
    private OnCommentItemClickedListener onCommentItemClickedListener;
    private OnLikedButtonClicked onLikedButtonClicked;
    private OnLikeClickedListener<DiscoverComment> discoverCommentOnLikeClickedListener;
    private OnLikeClickedListener<ReplyComment> replyCommentOnLikeClickedListener;
    private CommentViewHolder commentViewHolder;
    private TopicDescViewHolder topicDescViewHolder;
    public DiscoverTopicDetailAdaptper(Context mContext, DiscoverTopicDetailBean discoverTopicDetailBean) {
        this.mContext = mContext;
        this.discoverTopicDetailBean = discoverTopicDetailBean;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (getItemViewType(viewType) == TOPIC_DETAIL)
            return new TopicDescViewHolder(mContext, inflater.inflate(R.layout.item_discover_topic, parent,false));
        else if (getItemViewType(viewType) == TOPIC_LIKE)
            return new LikesPersoniewHolder(mContext, inflater.inflate(R.layout.gv_discover_circle_member, parent, false));
//        else if (getItemViewType(viewType) == TOPIC_COMMENT_TITLE)
//            return new TitleViewHolder(inflater.inflate(R.layout.ll_discover_thought_my_reply, null));
        else if (getItemViewType(viewType) == TOPIC_COMMENT)
            return new CommentViewHolder(mContext, inflater.inflate(R.layout.rv_comment, parent, false));
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TOPIC_DETAIL) {
            TopicDescViewHolder topicDescViewHolder = (TopicDescViewHolder) holder;
            topicDescViewHolder.bindData(discoverTopicDetailBean.getDiscoverTopicDetail());
        } else if (getItemViewType(position) == TOPIC_LIKE) {
            LikesPersoniewHolder viewHolder = (LikesPersoniewHolder) holder;
            viewHolder.bindData(discoverTopicDetailBean.getDiscoverCircleMemberList());
        }
//        else if (getItemViewType(position) == TOPIC_COMMENT_TITLE) {
//            TitleViewHolder viewHolder = (TitleViewHolder) holder;
//            viewHolder.bindData(discoverTopicDetailBean.getDiscoverTopicCommentList());
//        }
        else if (getItemViewType(position) == TOPIC_COMMENT) {
            commentViewHolder = (CommentViewHolder) holder;
            commentViewHolder.bindData(discoverTopicDetailBean.getDiscoverTopicCommentList());
        }

    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case TOPIC_DETAIL:
                crruentType = TOPIC_DETAIL;
                break;
            case TOPIC_LIKE:
                crruentType = TOPIC_LIKE;
                break;
//            case TOPIC_COMMENT_TITLE:
//                crruentType = TOPIC_COMMENT_TITLE;
//                break;
            case TOPIC_COMMENT:
                crruentType = TOPIC_COMMENT;
                break;
        }
        return crruentType;
    }

    @Override
    public int getItemCount() {
        return 3;
    }




    /**
     * 话题详情的ViewHolder
     */
    public class TopicDescViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        @BindView(R.id.iv_user_image)
        ImageView ivUserImage;
        @BindView(R.id.tv_topic_title)
        TextView tvTopicTitle;
        @BindView(R.id.tv_user_name)
        TextView tvUserName;
        @BindView(R.id.tv_topic_describe)
        TextView tvTopicDescribe;
        @BindView(R.id.mediaWebview)
        WebView mediaWebview;
        @BindView(R.id.ngv_image)
        NineGridView ngvImage;
        @BindView(R.id.iv_book_cover)
        ImageView ivBookCover;
        @BindView(R.id.tv_book_title)
        TextView tvBookTitle;
        @BindView(R.id.tv_book_author)
        TextView tvBookAuthor;
        @BindView(R.id.tv_book_version)
        TextView tvBookVersion;
        @BindView(R.id.book_item)
        LinearLayout bookItem;
        @BindView(R.id.tv_deliver_time)
        TextView tvDeliverTime;
        @BindView(R.id.iv_topic_like)
        ImageView ivTopicLike;
        @BindView(R.id.content_from)
        LinearLayout content_from;
        @BindView(R.id.topic_groupname)
        TextView topic_groupname;
        @BindView(R.id.tv_topic_like_num)
        TextView tvTopicLikeNum;
        @BindView(R.id.iv_topic_message)
        ImageView ivTopicMessage;
        @BindView(R.id.tv_message_num)
        TextView tvMessageNum;
        @BindView(R.id.v_topic_diliver)
        View vTopicDiliver;
        @BindView(R.id.container)
        LinearLayout container;
        @BindView(R.id.cardView)
        CardView cardView;

        TopicDescViewHolder(Context context, View view) {
            super(view);
            this.context = context;
            ButterKnife.bind(this, view);
        }

        @OnClick({R.id.iv_topic_message, R.id.tv_message_num,R.id.iv_topic_like,R.id.tv_topic_like_num,R.id.content_from})
        public void onClick(View view) {
            DiscoverTopicDetail detail = (DiscoverTopicDetail)cardView.getTag();
            switch (view.getId()) {
                case R.id.iv_topic_message:
                case R.id.tv_message_num:
                    if(detail != null && onAddCommentClickedListener != null)
                        onAddCommentClickedListener.onClicked(detail);
                    break;
                case R.id.iv_topic_like:
                case R.id.tv_topic_like_num:
                    if(detail != null && onLikedButtonClicked != null)
                        onLikedButtonClicked.onClicked(0,LIKE_TOPIC_DESC);
                    break;
                case R.id.content_from:
                    if(detail != null && onContentFromClickedListener != null)
                        onContentFromClickedListener.onClicked(detail);
                    break;
                    default:break;
            }
        }

        public void bindData(final DiscoverTopicDetail detail) {



            if (detail == null) {
                setTheWholeItemVisibility(View.GONE);

                return;
            }
            setTheWholeItemVisibility(View.VISIBLE);
            setBookIntroductionVisibility(View.GONE);
            GlideImageLoader.displayProtrait(context, detail.getCreateuserimage(), ivUserImage);
//            GlideImageLoader.displaysetdefault(context,ivUserImage,detail.getCreateuserimage(),R.drawable.ic_head_default);
            tvUserName.setText(detail.getCreateusername());
            tvTopicTitle.setText(detail.getTopicname());
//            RichTextUtils.showHtmlText(detail.getPath(),detail.getTopicdesc(),tvTopicDescribe);
//            tvTopicDescribe.setSingleLine(false);
            mediaWebview.getSettings().setSavePassword(false);
            mediaWebview.removeJavascriptInterface("searchBoxJavaBridge_");
            mediaWebview.removeJavascriptInterface("accessibility");
            mediaWebview.removeJavascriptInterface("accessibilityTraversal");
            //shao add begin
            String desc = detail.getTopicdesc();
            if(desc.contains("</audio>")){
                //tvTopicDescribe.setText("aaa");
                mediaWebview.loadData(detail.getTopicdesc(),"text/html; charset=UTF-8", null);
                mediaWebview.setVisibility(View.VISIBLE);
                tvTopicDescribe.setEllipsize(null);
                tvTopicDescribe.setSingleLine(false);
            }else{
                RichTextUtils.showHtmlText(detail.getPath(),detail.getTopicdesc(),tvTopicDescribe);
                tvTopicDescribe.setSingleLine(false);
                mediaWebview.setVisibility(View.GONE);
                tvTopicDescribe.setEllipsize(null);
                tvTopicDescribe.setSingleLine(false);
            }

            topic_groupname.setText(detail.getGroupname());
            content_from.setVisibility(View.VISIBLE);
            //shao add end
            tvDeliverTime.setText(detail.getCreatetime());
            tvTopicLikeNum.setText(detail.getLikecount() + "");
            tvMessageNum.setText(detail.getResponsesum() + "");
            cardView.setTag(detail);
//            tvTopicDescribe.setEllipsize(null);
//            tvTopicDescribe.setSingleLine(false);
            ngvImage.setVisibility(View.GONE);
            ivTopicLike.setImageResource((detail.isLiked()?R.drawable.ic_discover_heart_liked :R.drawable.ic_discover_heart));
//            List<String> urls = HtmlParserUtils.getPictureUrl(detail.getTopicdesc(), Urls.URL);
//            NineGridViewClickAdapter nineGridViewAdapter = new NineGridViewClickAdapter(context, getImageInfoList(urls));
//            ngvImage.setAdapter(nineGridViewAdapter);
            ivUserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MinePageActivity.class);
                    intent.putExtra(Constants.USER_ID , detail.getCreateuser()+"");
                    intent.putExtra(Constants.IS_MYSELF,false);
                    mContext.startActivity(intent);
                }
            });
        }

        private void setBookIntroductionVisibility(int visibility) {
            bookItem.setVisibility(visibility);
            ivBookCover.setVisibility(visibility);
            tvBookTitle.setVisibility(visibility);
            tvBookVersion.setVisibility(visibility);
            tvBookAuthor.setVisibility(visibility);
        }

        private void setTheWholeItemVisibility(int visibility) {
            cardView.setVisibility(visibility);
        }

        private List<ImageInfo> getImageInfoList(List<String> list) {
            List<ImageInfo> tempList = new ArrayList<>();
            for (String url : list) {
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.setThumbnailUrl(url);
                imageInfo.setBigImageUrl(url);
                tempList.add(imageInfo);
            }
            return tempList;
        }
        private void notifyDetailLike() {
            notifyItemChanged(0);
        }
    }

    public void notifyDetailLikeStatus() {
        if (!isNull(topicDescViewHolder)) {
            topicDescViewHolder.notifyDetailLike();
        } else {
            Logger.e("gft topicDescViewHolder = " + topicDescViewHolder);
        }
    }


    public OnLikedButtonClicked getOnLikedButtonClicked() {
        return onLikedButtonClicked;
    }

    public void setOnLikedButtonClicked(OnLikedButtonClicked onLikedButtonClicked) {
        this.onLikedButtonClicked = onLikedButtonClicked;
    }

    /**
     * 点赞列表的ViewHolder
     */
    public class LikesPersoniewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_ring)
        ImageView ivRing;
        @BindView(R.id.tv_bar_title)
        TextView tvBarTitle;
        @BindView(R.id.tv_more)
        TextView tvMore;
        @BindView(R.id.iv_more)
        ImageView ivMore;
        @BindView(R.id.im_comment_edit)
        ImageView imCommentEdit;
        @BindView(R.id.iv_bar_divide)
        ImageView ivBarDivide;
        @BindView(R.id.rl_discover_bar)
        RelativeLayout rlDiscoverBar;
        @BindView(R.id.gv_circle_member)
        GridView gvCircleMember;
        @BindView(R.id.layout_discover_circle_member)
        LinearLayout layoutDiscoverCircleMember;


        LikesPersoniewHolder(Context context, View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public boolean bindData(List<DiscoverCircleMember> memberList) {
            if(memberList == null || memberList.size() == 0){
                rlDiscoverBar.setVisibility(View.GONE);
                layoutDiscoverCircleMember.setVisibility(View.GONE);
                gvCircleMember.setVisibility(View.GONE);
                return false;
            }
            layoutDiscoverCircleMember.setVisibility(View.VISIBLE);
            gvCircleMember.setVisibility(View.VISIBLE);
            rlDiscoverBar.setVisibility(View.VISIBLE);
            tvBarTitle.setText(mContext.getResources().getString(R.string.discover_thought_praise_title));
            tvMore.setVisibility(View.INVISIBLE);
            ivMore.setVisibility(View.INVISIBLE);
            DiscoverCircleMemberAdapter discoverCircleMemberAdapter = new DiscoverCircleMemberAdapter(mContext, memberList);
            gvCircleMember.setAdapter(discoverCircleMemberAdapter);
            return true;
        }
    }


//    /**
//     * 全部回复标题栏的ViewHolder
//     */
//    public class TitleViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.iv_user_image)
//        ImageView ivUserImage;
//        @BindView(R.id.tv_user_name)
//        TextView tvUserName;
//        @BindView(R.id.tv_deliver_time)
//        TextView tvDeliverTime;
//        @BindView(R.id.layout_thought_content_title)
//        LinearLayout layoutThoughtContentTitle;
//
//        TitleViewHolder(View view) {
//            super(view);
//            ButterKnife.bind(this, view);
//        }
//
//        public void bindData(List<DiscoverTopicComments> commentList) {
//            if(commentList == null || commentList.size() == 0){
//                layoutThoughtContentTitle.setErrorLayoutVisibility(View.GONE);
//                return;
//            }
//            if(layoutThoughtContentTitle.getVisibility() != View.VISIBLE)
//                layoutThoughtContentTitle.setErrorLayoutVisibility(View.VISIBLE);
//        }
//    }




    public class CommentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rv_comment)
        RecyclerView rvComment;
        @BindView(R.id.layout_comment)
        LinearLayout layoutComment;
        @BindView(R.id.layout_sofa)
        LinearLayout layoutSofa;
        @BindView(R.id.rl_discover_bar)
        RelativeLayout rlDiscoverBar;
        @BindView(R.id.iv_ring)
        ImageView ivRing;
        @BindView(R.id.tv_bar_title)
        TextView tvBarTitle;
        @BindView(R.id.tv_more)
        TextView tvMore;
        @BindView(R.id.iv_more)
        ImageView ivMore;
        @BindView(R.id.im_comment_edit)
        ImageView imCommentEdit;
        @BindView(R.id.iv_bar_divide)
        ImageView ivBarDivide;
        CommentAdapter commentAdapter;

        CommentViewHolder(Context context, View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

		/**
         * 根据list判断哪些控件应该显示
         * 如果list为空，则全部不显示；
         * 如果list不为空，但是list里面没有数据，则显示沙发；
         * 如果list有数据，则显示数据；
         * @param list
         */
        private void setLayoutVisibility(List<DiscoverTopicComments> list){
//            如果list为空，则全部不显示
            if(list == null ){
                layoutComment.setVisibility(View.GONE);
                return;
            }
            layoutComment.setVisibility(View.VISIBLE);
//            如果list不为空，但是list里面没有数据，则显示沙发；
            if(list.size() == 0){
                layoutSofa.setVisibility(View.VISIBLE);
                rvComment.setVisibility(View.GONE);
            }else{
//                如果list有数据，则显示数据；
                layoutSofa.setVisibility(View.GONE);
                rvComment.setVisibility(View.VISIBLE);
            }

            tvBarTitle.setText(R.string.discover_topic_comment);
            rlDiscoverBar.setVisibility(View.VISIBLE);
            tvMore.setVisibility(View.INVISIBLE);
            ivMore.setVisibility(View.INVISIBLE);
        }

        void bindData(List<DiscoverTopicComments> commentList) {
            setLayoutVisibility(commentList);
            initAdapter(commentList);
        }

        private void initAdapter(List<DiscoverTopicComments> list) {
            if(list != null)
                Logger.i("DiscoverTopicComments"+list.toString());
            commentAdapter = new CommentAdapter(mContext, list,0);
            commentAdapter.setOnReplyCommentItemClicked(new CommentAdapter.OnReplyCommentItemClicked() {
                @Override
                public void onClicked(DiscoverComment data, int position) {
                    if(onCommentItemClickedListener != null)
                        onCommentItemClickedListener.onFirstClassCommentClicked(data,position);
                }

                @Override
                public void onClicked(ReplyComment data) {
                    if(onCommentItemClickedListener != null)
                        onCommentItemClickedListener.onSecondClassCommentClicked(data);
                }
            });
            commentAdapter.setDiscoverCommentOnLikeClickedListener(new OnLikeClickedListener<DiscoverComment>() {
                @Override
                public void onLikeButtonClicked(DiscoverComment comment) {
                    if(discoverCommentOnLikeClickedListener != null){
                        discoverCommentOnLikeClickedListener.onLikeButtonClicked(comment);
                    }
                }
            });
            commentAdapter.setReplyCommentOnLikeClickedListener(new OnLikeClickedListener<ReplyComment>() {
                @Override
                public void onLikeButtonClicked(ReplyComment comment) {
                    if(replyCommentOnLikeClickedListener != null){
                        replyCommentOnLikeClickedListener.onLikeButtonClicked(comment);
                    }
                }
            });
            ((SimpleItemAnimator)rvComment.getItemAnimator()).setSupportsChangeAnimations(false);
            rvComment.setAdapter(commentAdapter);
            GridLayoutManager manager = new GridLayoutManager(mContext, 1);
            rvComment.setLayoutManager(manager);
        }

        private void notifySubLikeStatus(int position){
			if(isNull(commentAdapter)){
				Logger.e("wzp commentAdapter = " + commentAdapter);
			}else{
				commentAdapter.notifyItemChanged(position);
			}
        }
    }

    public final void notifySubLikeStatus(int position){
        if(isNull(commentViewHolder)){
            Logger.e("wzp commentViewHolder = " + commentViewHolder);
        }else{
            commentViewHolder.notifySubLikeStatus(position);
        }
    }


    //shao add begin
    public interface OnContentFromClickedListener{
        public void onClicked(DiscoverTopicDetail detail);
    }

    public OnContentFromClickedListener getContentFromListener(){
        return onContentFromClickedListener;
    }

    public void setOnContentFromClickedListener(OnContentFromClickedListener onContentFromClickedListener){
        this.onContentFromClickedListener=onContentFromClickedListener;
    }
    //shao add end
    public interface OnAddCommentClickedListener {
        public void onClicked(DiscoverTopicDetail detail);
    }

    public interface OnCommentItemClickedListener {
        public void onFirstClassCommentClicked(DiscoverComment data, int position);

        public void onSecondClassCommentClicked(ReplyComment data);
    }

    public OnAddCommentClickedListener getOnAddCommentClickedListener() {
        return onAddCommentClickedListener;
    }

    public void setOnAddCommentClickedListener(OnAddCommentClickedListener onAddCommentClickedListener) {
        this.onAddCommentClickedListener = onAddCommentClickedListener;
    }

    public OnCommentItemClickedListener getOnCommentItemClickedListener() {
        return onCommentItemClickedListener;
    }

    public void setOnCommentItemClickedListener(OnCommentItemClickedListener onCommentItemClickedListener) {
        this.onCommentItemClickedListener = onCommentItemClickedListener;
    }

    public OnLikeClickedListener<DiscoverComment> getDiscoverCommentOnLikeClickedListener() {
        return discoverCommentOnLikeClickedListener;
    }

    public void setDiscoverCommentOnLikeClickedListener(OnLikeClickedListener<DiscoverComment> discoverCommentOnLikeClickedListener) {
        this.discoverCommentOnLikeClickedListener = discoverCommentOnLikeClickedListener;
    }

    public OnLikeClickedListener<ReplyComment> getReplyCommentOnLikeClickedListener() {
        return replyCommentOnLikeClickedListener;
    }

    public void setReplyCommentOnLikeClickedListener(OnLikeClickedListener<ReplyComment> replyCommentOnLikeClickedListener) {
        this.replyCommentOnLikeClickedListener = replyCommentOnLikeClickedListener;
    }
}
