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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.BookMarketBookDetailsActivity;
import com.iyangcong.reader.activity.MinePageActivity;
import com.iyangcong.reader.bean.DiscoverCircleMember;
import com.iyangcong.reader.bean.DiscoverComment;
import com.iyangcong.reader.bean.DiscoverReviewDetails;
import com.iyangcong.reader.bean.DiscoverThoughtDetail;
import com.iyangcong.reader.bean.DiscoverTopic;
import com.iyangcong.reader.bean.ReplyComment;
import com.iyangcong.reader.interfaceset.OnLikeClickedListener;
import com.iyangcong.reader.ui.ninegridview.NineGridView;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.ConvertHelper;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.LoginUtils;
import com.iyangcong.reader.utils.RichTextUtils;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

/**
 * Created by ljw on 2016/12/28.
 * modified by wzp
 * 书评详情的Adapter
 */

public class DiscoverBookReviewAdapter extends RecyclerView.Adapter {

    /**
     * 类型1：读后感内容--使用LinearLayout实现
     */
    /**
     * 类型1：读后感内容--使用LinearLayout实现
     */
    private static final int THOUGH_CONTENT_LL = 0;

    /**
     * 类型2：点赞好友--使用GridView实现
     */
    private static final int THOUGHT_PRAISE_MEMBER = 1;

    /**
     * 类型3：我的回复--使用LinearLayout实现
     */
//    private static final int THOUGHT_MYREPLY_LL = 2;

    /**
     * 类型4：所有回复--使用ListView实现
     */
    private static final int THOUGHT_ALL_REPLY_LV = 2;


    /**
     * 当前类型
     */
    private int currentType = THOUGH_CONTENT_LL;

    private Context context;
    private OnReplyItemClicked onReplyItemClicked;
    /**
     * 使用mLayoutInflater来初始化布局
     */
    private LayoutInflater mLayoutInflater;

    private DiscoverThoughtDetail discoverThoughtDetail;

    private OnReviewDetaiComementClickListener onDetailCommentClickListener;

    private OnLikeClickedListener<DiscoverReviewDetails> onDetailLikeClickListener;
    private OnLikeClickedListener<DiscoverComment> commentLikedClickListener;
    private OnLikeClickedListener<ReplyComment> subCommentLikedClickListener;
    private ThoughtAllReplyViewHolder thoughtAllReplyViewHolder;
    private ThoughtContentViewHolder thoughtContentViewHolder;

    public DiscoverBookReviewAdapter(Context context, DiscoverThoughtDetail discoverThoughtDetail) {
        this.context = context;
        this.discoverThoughtDetail = discoverThoughtDetail;
        mLayoutInflater = LayoutInflater.from(context);
    }

    /**
     * 创建ViewHolder布局
     *
     * @param viewType 当前类型
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == THOUGH_CONTENT_LL) {
            return new ThoughtContentViewHolder(context, mLayoutInflater.inflate(R.layout.ll_discover_thought_content, null));
        } else if (viewType == THOUGHT_PRAISE_MEMBER) {
            return new ThoughtPriseMemberViewHolder(context, mLayoutInflater.inflate(R.layout.gv_discover_circle_member, null));
        }
//        else if (viewType == THOUGHT_MYREPLY_LL) {
//            return new ThoughtMyReplyViewHolder(context, mLayoutInflater.inflate(R.layout.ll_discover_thought_my_reply, null));
//        }
        else if (viewType == THOUGHT_ALL_REPLY_LV) {
            return new ThoughtAllReplyViewHolder(context, mLayoutInflater.inflate(R.layout.rv_comment, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == THOUGH_CONTENT_LL) {
            thoughtContentViewHolder = (ThoughtContentViewHolder) holder;
            thoughtContentViewHolder.setData(discoverThoughtDetail.getDiscoverReviewDetails());
        } else if (getItemViewType(position) == THOUGHT_PRAISE_MEMBER) {
            ThoughtPriseMemberViewHolder thoughtPriseMemberViewHolder = (ThoughtPriseMemberViewHolder) holder;
            thoughtPriseMemberViewHolder.setData(discoverThoughtDetail.getDiscoverCircleMemberList());
        }
//        else if (getItemViewType(position) == THOUGHT_MYREPLY_LL) {
//            ThoughtMyReplyViewHolder thoughtMyReplyViewHolder = (ThoughtMyReplyViewHolder) holder;
//            thoughtMyReplyViewHolder.setData(discoverThoughtDetail.getCommentList());
//        }
        else if (getItemViewType(position) == THOUGHT_ALL_REPLY_LV) {
            thoughtAllReplyViewHolder = (ThoughtAllReplyViewHolder) holder;
            thoughtAllReplyViewHolder.setData(discoverThoughtDetail.getCommentList());
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case THOUGH_CONTENT_LL:
                currentType = THOUGH_CONTENT_LL;
                break;
            case THOUGHT_PRAISE_MEMBER:
                currentType = THOUGHT_PRAISE_MEMBER;
                break;
//            case THOUGHT_MYREPLY_LL:
//                currentType = THOUGHT_MYREPLY_LL;
//                break;
            case THOUGHT_ALL_REPLY_LV:
                currentType = THOUGHT_ALL_REPLY_LV;
                break;
            default:
                break;
        }
        return currentType;
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    /**
     * 读后感内容 ViewHolder
     */
    public class ThoughtContentViewHolder extends RecyclerView.ViewHolder {


        private final Context mContext;

        private DiscoverTopic discoverTopic;

        @BindView(R.id.layout_thought_content)
        LinearLayout layoutThoughtContent;
        @BindView(R.id.iv_user_image)
        ImageView ivUserImage;
        @BindView(R.id.tv_user_name)
        TextView tvUserName;
        @BindView(R.id.tv_topic_title)
        TextView tvTopicTitle;
        @BindView(R.id.tv_topic_describe)
        TextView tvTopicDescribe;
        @BindView(R.id.ngv_image)
        NineGridView ngvImage;
        @BindView(R.id.tv_deliver_time)
        TextView tvDeliverTime;
        @BindView(R.id.iv_topic_like)
        ImageView ivTopicLike;
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
        @BindView(R.id.iv_book_cover)
        ImageView ivBookCover;
        @BindView(R.id.tv_book_title)
        TextView tvBookTitle;
        @BindView(R.id.tv_book_author)
        TextView tvBookAuthor;
        @BindView(R.id.tv_book_version)
        TextView tvBookVersion;
        @BindView(R.id.book_item)
        LinearLayout llBookItem;
        GlideImageLoader imageLoader;
        @BindView(R.id.tv_book_publish_house)
        TextView mTvBookPublishHouse;

        ThoughtContentViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
            imageLoader = new GlideImageLoader();
        }

        @OnClick({R.id.iv_topic_message, R.id.tv_message_num, R.id.iv_topic_like, R.id.tv_topic_like_num, R.id.book_item})
        public void onClick(View view) {
            DiscoverReviewDetails DRD = (DiscoverReviewDetails) tvMessageNum.getTag();
            LoginUtils loginUtils = new LoginUtils();
            switch (view.getId()) {
                case R.id.iv_topic_message:
                case R.id.tv_message_num:
                    if (loginUtils.isLogin(context)) {
                        if (onDetailCommentClickListener != null) {
                            onDetailCommentClickListener.onDetailComentClicked(DRD);
                        }
                    }
                    break;
                case R.id.iv_topic_like:
                case R.id.tv_topic_like_num:
                    if (loginUtils.isLogin(context)) {
                        if (onDetailLikeClickListener != null) {
                            onDetailLikeClickListener.onLikeButtonClicked(DRD);
                        }
                    }
                    break;
                case R.id.book_item:
                    Intent intent = new Intent(context, BookMarketBookDetailsActivity.class);
                    intent.putExtra(Constants.BOOK_ID, DRD.getBookid());
                    intent.putExtra(Constants.BOOK_NAME, DRD.getTitleZh());
                    context.startActivity(intent);
                    break;
            }
        }

        void setData(final DiscoverReviewDetails details) {
            if (details == null) {
                layoutThoughtContent.setVisibility(View.GONE);
                return;
            }
            if (layoutThoughtContent.getVisibility() != View.VISIBLE) {
                layoutThoughtContent.setVisibility(View.VISIBLE);
            }
            llBookItem.setVisibility(View.VISIBLE);
            imageLoader.displayProtrait(context, details.getUserImageUrl(), ivUserImage);
//            imageLoader.displaysetdefault(context,ivUserImage,details.getUserImageUrl(),R.drawable.ic_head_default);
            imageLoader.displayBookCover(context, ivBookCover, details.getBookcover());
            tvUserName.setText(details.getUserName());
            tvTopicTitle.setText(details.getTitle());
            mTvBookPublishHouse.setText(mContext.getString(R.string.public_house) + details.getPublishHouse());
//            tvTopicDescribe.setText(Html.fromHtml(HtmlParserUtils.getContent(details.getContent())));
            RichTextUtils.showHtmlText(details.getPath(), details.getContent(), tvTopicDescribe);
            tvDeliverTime.setText(details.getCreatetime());
            tvTopicLikeNum.setText(details.getReviewsLike() + "");
            tvMessageNum.setText(details.getMessageNum() + "");
            tvMessageNum.setTag(details);
            tvBookTitle.setText(details.getTitleZh());
            tvBookAuthor.setText(details.getAuther());
            tvBookVersion.setText(ConvertHelper.getEdition(details.getEdition()));
            ivTopicLike.setImageResource(details.isLiked() ? R.drawable.ic_discover_heart_liked : R.drawable.ic_discover_heart);
            tvTopicDescribe.setEllipsize(null);
            tvTopicDescribe.setSingleLine(false);
            ivUserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MinePageActivity.class);
                    intent.putExtra(Constants.USER_ID, details.getUserId() + "");
                    intent.putExtra(Constants.IS_MYSELF, false);
                    mContext.startActivity(intent);
                }
            });
        }

        private void notifyDetailLike() {
            notifyItemChanged(0);
        }
    }

    /*
    *刷新书评详情界面；
     */
    public void notifyDetailLikeStatus() {
        if (!isNull(thoughtContentViewHolder)) {
            thoughtContentViewHolder.notifyDetailLike();
        } else {
            Logger.e("wzp thoughtContentViewHolder = " + thoughtContentViewHolder);
        }
    }

    public OnLikeClickedListener<DiscoverReviewDetails> getOnDetailLikeClickListener() {
        return onDetailLikeClickListener;
    }

    public void setOnDetailLikeClickListener(OnLikeClickedListener<DiscoverReviewDetails> onDetailLikeClickListener) {
        this.onDetailLikeClickListener = onDetailLikeClickListener;
    }

    public interface OnReviewDetaiComementClickListener {

        public void onDetailComentClicked(DiscoverReviewDetails details);
    }

    public OnReviewDetaiComementClickListener getOnDetailCommentClickListener() {
        return onDetailCommentClickListener;
    }

    public void setOnDetailCommentClickListener(OnReviewDetaiComementClickListener onDetailCommentClickListener) {
        this.onDetailCommentClickListener = onDetailCommentClickListener;
    }


    /**
     * 点赞成员列表 ViewHolder
     */
    public class ThoughtPriseMemberViewHolder extends RecyclerView.ViewHolder {

        private final Context mContext;
        @BindView(R.id.layout_discover_circle_member)
        LinearLayout layoutDiscoverCircleMember;
        @BindView(R.id.iv_ring)
        ImageView ivRing;
        @BindView(R.id.tv_bar_title)
        TextView tvBarTitle;
        @BindView(R.id.tv_more)
        TextView tvMore;
        @BindView(R.id.iv_more)
        ImageView ivMore;
        @BindView(R.id.iv_bar_divide)
        ImageView ivBarDivide;
        @BindView(R.id.rl_discover_bar)
        RelativeLayout rlDiscoverBar;
        @BindView(R.id.gv_circle_member)
        GridView gvCircleMember;

        ThoughtPriseMemberViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
            tvMore.setVisibility(View.GONE);
            ivMore.setVisibility(View.GONE);
        }


        void setData(List<DiscoverCircleMember> discoverCircleMemberList) {
            if (setLayoutVisibility(discoverCircleMemberList))
                bindData(discoverCircleMemberList);
        }

        private void bindData(List<DiscoverCircleMember> discoverCircleMemberList) {
            DiscoverCircleMemberAdapter discoverCircleMemberAdapter = new DiscoverCircleMemberAdapter(mContext, discoverCircleMemberList);
            gvCircleMember.setAdapter(discoverCircleMemberAdapter);
        }

        private boolean setLayoutVisibility(List<DiscoverCircleMember> discoverCircleMemberList) {
            if (discoverCircleMemberList == null || discoverCircleMemberList.size() == 0) {
                rlDiscoverBar.setVisibility(View.GONE);
                gvCircleMember.setVisibility(View.GONE);
                layoutDiscoverCircleMember.setVisibility(View.GONE);
                return false;
            }
            layoutDiscoverCircleMember.setVisibility(View.VISIBLE);
            rlDiscoverBar.setVisibility(View.VISIBLE);
            gvCircleMember.setVisibility(View.VISIBLE);
            tvBarTitle.setText(R.string.discover_thought_praise_title);
            ivBarDivide.setVisibility(View.GONE);
            return true;
        }
    }

    /**
     * 我的回复 ViewHolder
     */
    public class ThoughtMyReplyViewHolder extends RecyclerView.ViewHolder {

        private final Context mContext;
        @BindView(R.id.iv_ring)
        ImageView ivRing;
        @BindView(R.id.tv_bar_title)
        TextView tvBarTitle;
        @BindView(R.id.tv_more)
        TextView tvMore;
        @BindView(R.id.iv_more)
        ImageView ivMore;
        @BindView(R.id.rl_discover_bar)
        RelativeLayout rlDiscoverBar;
        @BindView(R.id.iv_user_image)
        ImageView ivUserImage;
        @BindView(R.id.tv_user_name)
        TextView tvUserName;
        @BindView(R.id.tv_comment_content)
        TextView tvCommentContent;
        @BindView(R.id.tv_deliver_time)
        TextView tvDeliverTime;
        @BindView(R.id.v_comment_diliver)
        View vCommentDiliver;
        @BindView(R.id.cv_comment)
        CardView cvComment;
        @BindView(R.id.layout_thought_content_title)
        LinearLayout layoutThoughtContentTitle;

        ThoughtMyReplyViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        void setData(List<DiscoverComment> commmentList) {
            if (commmentList == null || commmentList.size() == 0) {
                layoutThoughtContentTitle.setVisibility(View.GONE);
                return;
            }
            if (layoutThoughtContentTitle.getVisibility() != View.VISIBLE) {
                layoutThoughtContentTitle.setVisibility(View.VISIBLE);
            }
            tvBarTitle.setText(R.string.discover_thought_all_reply);
            tvMore.setVisibility(View.GONE);
            ivMore.setVisibility(View.GONE);
        }
    }

    /**
     * 所有回复 ViewHolder
     */
    public class ThoughtAllReplyViewHolder extends RecyclerView.ViewHolder {

        private final Context mContext;
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
        @BindView(R.id.tv_sofa_title)
        TextView tvSofaTitle;
        @BindView(R.id.layout_sofa)
        LinearLayout layoutSofa;
        @BindView(R.id.rv_comment)
        RecyclerView rvComment;
        @BindView(R.id.layout_comment)
        LinearLayout layoutComment;
        private CommentAdapter commentAdapter;

        ThoughtAllReplyViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        /**
         * 根据装载的数据来判断显示的内容。如果list为空，直接不显示这个item；
         * 如果list不为空但是没有数据，就显示沙发;
         * 如果list不为空，且有数据，就显示数据
         *
         * @param list
         */
        private boolean setlayoutVisibility(List<DiscoverComment> list) {
//            如果list为空，直接不显示这个item；
            if (list == null) {
                layoutComment.setVisibility(View.GONE);
                return false;
            }
            layoutComment.setVisibility(View.VISIBLE);
            rlDiscoverBar.setVisibility(View.VISIBLE);
            tvBarTitle.setText(R.string.discover_thought_all_reply);
            tvMore.setVisibility(View.GONE);
            ivMore.setVisibility(View.GONE);
//            如果list不为空但是没有数据，就显示沙发;
            if (list.size() == 0) {
                layoutSofa.setVisibility(View.VISIBLE);
                rvComment.setVisibility(View.GONE);
                return false;
            } else {
//                如果list不为空，且有数据，就显示数据；
                layoutSofa.setVisibility(View.GONE);
                rvComment.setVisibility(View.VISIBLE);
            }
            return true;
        }

        /**
         * 初始化RecyclerView;
         *
         * @param list
         */
        private void initRecyclerView(List<DiscoverComment> list) {
            ((SimpleItemAnimator)rvComment.getItemAnimator()).setSupportsChangeAnimations(false);
            commentAdapter = new CommentAdapter(mContext, list,1);
            setAdapter(commentAdapter);
            rvComment.setAdapter(commentAdapter);
            GridLayoutManager manager = new GridLayoutManager(mContext, 1);
            rvComment.setLayoutManager(manager);
            layoutComment.setTag(list);
        }

        private void setAdapter(final CommentAdapter commentAdapter) {
            commentAdapter.setOnReplyCommentItemClicked(new CommentAdapter.OnReplyCommentItemClicked() {
                @Override
                public void onClicked(DiscoverComment data, int position) {
                    if (onReplyItemClicked != null)
                        onReplyItemClicked.onClicked(data, position);
                }

                @Override
                public void onClicked(ReplyComment data) {
                    if (onReplyItemClicked != null)
                        onReplyItemClicked.onClicked(data);
                }
            });
            commentAdapter.setDiscoverCommentOnLikeClickedListener(new OnLikeClickedListener<DiscoverComment>() {
                @Override
                public void onLikeButtonClicked(DiscoverComment comment) {
                    if (commentLikedClickListener != null)
                        commentLikedClickListener.onLikeButtonClicked(comment);
                }
            });
            commentAdapter.setReplyCommentOnLikeClickedListener(new OnLikeClickedListener<ReplyComment>() {
                @Override
                public void onLikeButtonClicked(ReplyComment comment) {
                    if (subCommentLikedClickListener != null) {
                        subCommentLikedClickListener.onLikeButtonClicked(comment);
                    }
                }
            });
        }

        /**
         * 加载数据
         *
         * @param commmentList
         */
        void setData(List<DiscoverComment> commmentList) {
            if (setlayoutVisibility(commmentList)) {
                initRecyclerView(commmentList);
            }
        }

        public void notifyLikeStatus(int position) {
            if (!isNull(commentAdapter)) {
                commentAdapter.notifyItemChanged(position);
            } else {
                Logger.e("wzp commentAdapter = " + commentAdapter);
            }
        }
    }

    public void notifySubLikeStatus(int pos) {
        if (isNull(thoughtAllReplyViewHolder)) {
            Logger.e("wzp thoughtAllReplyViewHolder = " + thoughtAllReplyViewHolder);
        } else {
            thoughtAllReplyViewHolder.notifyLikeStatus(pos);
        }
    }

    public OnReplyItemClicked getOnReplyItemClicked() {
        return onReplyItemClicked;
    }

    public void setOnReplyItemClicked(OnReplyItemClicked onReplyItemClicked) {
        this.onReplyItemClicked = onReplyItemClicked;
    }

    public interface OnReplyItemClicked {
        public void onClicked(DiscoverComment data, int position);

        public void onClicked(ReplyComment data);
    }

    public OnLikeClickedListener<ReplyComment> getSubCommentLikedClickListener() {
        return subCommentLikedClickListener;
    }

    public void setSubCommentLikedClickListener(OnLikeClickedListener<ReplyComment> subCommentLikedClickListener) {
        this.subCommentLikedClickListener = subCommentLikedClickListener;
    }

    public OnLikeClickedListener<DiscoverComment> getCommentLikedClickListener() {
        return commentLikedClickListener;
    }

    public void setCommentLikedClickListener(OnLikeClickedListener<DiscoverComment> commentLikedClickListener) {
        this.commentLikedClickListener = commentLikedClickListener;
    }
}
