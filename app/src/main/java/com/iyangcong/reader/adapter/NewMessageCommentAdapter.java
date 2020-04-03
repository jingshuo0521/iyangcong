package com.iyangcong.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.DiscoverBookReviewDetailActivity;
import com.iyangcong.reader.activity.DiscoverTopicActivity;
import com.iyangcong.reader.bean.MessageComment;
import com.iyangcong.reader.interfaceset.OnItemClickedListener;
import com.iyangcong.reader.ui.button.FlatButton;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.RichTextUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

/**
 * Created by Administrator on 2017/1/1 0001.
 */

public class NewMessageCommentAdapter extends RecyclerView.Adapter {


    private Context context;
    private List<MessageComment> commentList;
    private LayoutInflater mInflater;
    private NewMessageCommentAdapter.OnDeleteListener onDeleteListener;
    private OnItemClickedListener onItemClickedListener;
    //已经删除
    private static final int DELETE = 0;
    //回复话题
    private static final int REPLY_TOPIC = 1;
    //回复读后感
    private static final int REPLY_REVIEW = 2;
    //回复书摘评论
    private static final int REPLYC_BOOK_COMMENT = 3;
    //喜欢了我的评论
    private static final int LIKE_MY_COMMENT = 4;
    //喜欢了我的读后感
    private static final int LIKE_MY_REVIEW = 5;
    //回复了读后感评论
    private static final int REPLY_MY_RIEW_COMMENT = 6;
    //回复了话题评论
    private static final int REPLY_MY_TOPIC_COMENT = 7;


    public NewMessageCommentAdapter(Context context, List<MessageComment> list) {
        commentList = list;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_comment,parent,false);
        switch (viewType){
            case DELETE:
                return new DeleteItemViewHolder(view);
            case REPLY_TOPIC:
                return new ReplyTopicViewHolder(view);
            case REPLY_REVIEW:
                return new ReplyReviewViewHolder(view);
            case REPLYC_BOOK_COMMENT:
                return new ReplyBookCommentViewHolder(view);
            case LIKE_MY_COMMENT:
                return new LikeCommentViewHolder(view);
            case LIKE_MY_REVIEW:
                return new LikeMyReviewViewHolder(view);
            case REPLY_MY_RIEW_COMMENT:
                return new ReplyMyRiewComment(view);
            case REPLY_MY_TOPIC_COMENT:
                return new ReplyMyTopicComment(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case DELETE:
                DeleteItemViewHolder commentViewHolder = (DeleteItemViewHolder)holder;
                commentViewHolder.setData(commentList.get(position));
                break;
            case REPLY_TOPIC:
                ReplyTopicViewHolder replyTopicViewHolder = (ReplyTopicViewHolder)holder;
                replyTopicViewHolder.setData(commentList.get(position));
                break;
            case REPLY_REVIEW:
                ReplyReviewViewHolder replyReviewViewHolder = (ReplyReviewViewHolder)holder;
                replyReviewViewHolder.setData(commentList.get(position));
                break;
            case REPLYC_BOOK_COMMENT:
                ReplyBookCommentViewHolder replyBookCommentViewHolder = (ReplyBookCommentViewHolder)holder;
                replyBookCommentViewHolder.setData(commentList.get(position));
                break;
            case LIKE_MY_COMMENT:
                LikeCommentViewHolder likeMyCommentViewHolder = (LikeCommentViewHolder)holder;
                likeMyCommentViewHolder.setData(commentList.get(position));
                break;
            case LIKE_MY_REVIEW:
                LikeMyReviewViewHolder likeMyReviewViewHolder = (LikeMyReviewViewHolder)holder;
                likeMyReviewViewHolder.setData(commentList.get(position));
                break;
            case REPLY_MY_RIEW_COMMENT:
                ReplyMyRiewComment replyMyRiewComment = (ReplyMyRiewComment)holder;
                replyMyRiewComment.setData(commentList.get(position));
                break;
            case REPLY_MY_TOPIC_COMENT:
                ReplyMyTopicComment replyMyTopicComment = (ReplyMyTopicComment)holder;
                replyMyTopicComment.setData(commentList.get(position));
                break;

        }

    }

    @Override
    public int getItemCount() {
        return commentList == null ? 0:commentList.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (commentList.get(position).getType()){
            case 0:
                return DELETE;
            case 1:
                return REPLY_TOPIC;
            case 2:
                return REPLY_REVIEW;
            case 3:
                return REPLYC_BOOK_COMMENT;
            case 4:
                return LIKE_MY_COMMENT;
            case 5:
                return LIKE_MY_REVIEW;
            case 6:
                return REPLY_MY_RIEW_COMMENT;
            case 7:
                return REPLY_MY_TOPIC_COMENT;
        }
        return -1;
    }

    public abstract class  CommentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.message_bell_ll)
        LinearLayout messageBellLl;
        @BindView(R.id.notice_response_user)
        TextView noticeResponseUser;
        @BindView(R.id.notice_response_title)
        TextView noticeResponseTitle;
        @BindView(R.id.notice_response_content)
        TextView noticeResponseContent;
        @BindView(R.id.delete_comment)
        FlatButton delete_comment;
        @BindView(R.id.tv_at)
        TextView tvAt;
        @BindView(R.id.tv_say)
        TextView tvSay;
        @BindView(R.id.layout_item_comment)
        LinearLayout layoutItemComment;
        @BindView(R.id.iv_read_status)
        ImageView ivReadStatus;

        CommentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private boolean setLayoutVisibility(MessageComment bean){
            if(bean == null){
                layoutItemComment.setVisibility(View.GONE);
                delete_comment.setVisibility(View.GONE);
                return false;
            }
            layoutItemComment.setVisibility(View.VISIBLE);
            delete_comment.setVisibility(View.VISIBLE);
            return true;
        }

        private void bindData(final MessageComment bean){
            ivReadStatus.setImageResource(bean.getStatus()==1?R.drawable.has_not_read:R.drawable.has_read);
            noticeResponseUser.setText(bean.getUserName());
            tvAt.setText(bean.getAction());
            noticeResponseTitle.setText(bean.getResponsedName());
            if(!isNull(context,bean.getResponseContent(),"")){
                RichTextUtils.showHtmlText(null, bean.getResponseContent(),noticeResponseContent);
            }
//            noticeResponseContent.setText(bean.getResponseContent());
            tvSay.setText(" 说：");
            setLayoutContent(bean);
            layoutItemComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickedListener != null){
                        onItemClickedListener.onItemClickedListener(commentList.indexOf(bean));
                    }
                }
            });
            delete_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onDeleteListener != null)
                        onDeleteListener.onDelete(bean.getMessageId(),commentList.indexOf(bean));
                }
            });
        }

        public void setData(MessageComment comment) {
            if(setLayoutVisibility(comment)){
                bindData(comment);
            }
        }

        abstract void setLayoutContent(MessageComment comment);
    }

    public class DeleteItemViewHolder extends  CommentViewHolder{

        public DeleteItemViewHolder(View view) {
            super(view);
        }

        @Override
        void setLayoutContent(MessageComment comment) {
        }
    }
    public class ReplyTopicViewHolder extends CommentViewHolder{

        public ReplyTopicViewHolder(View view) {
            super(view);
        }
        @Override
        void setLayoutContent(final MessageComment comment) {
            noticeResponseTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToTopic(comment);
                }
            });
            noticeResponseContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToTopic(comment);
                }
            });
        }
    }
    public class ReplyReviewViewHolder extends CommentViewHolder{
        public ReplyReviewViewHolder(View view) {
            super(view);
        }

        @Override
        void setLayoutContent(final MessageComment comment) {
            noticeResponseTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToReivew(comment);
                }
            });
            noticeResponseContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToReivew(comment);
                }
            });
        }
    }
    public class ReplyBookCommentViewHolder extends CommentViewHolder{
        public ReplyBookCommentViewHolder(View view) {
            super(view);
        }

        @Override
        void setLayoutContent(final MessageComment comment) {
            noticeResponseTitle.setText(comment.getCommentedContent());
            noticeResponseContent.setEllipsize(null);
            noticeResponseContent.setSingleLine(false);
//            noticeResponseTitle.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    goToReivew(comment);
//                }
//            });
//            noticeResponseContent.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    goToReivew(comment);
//                }
//            });
        }
    }
    public class LikeCommentViewHolder extends CommentViewHolder{
        public LikeCommentViewHolder(View view) {
            super(view);
        }

        @Override
        void setLayoutContent(final MessageComment comment) {
            noticeResponseTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToReivew(comment);
                }
            });
            noticeResponseContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToReivew(comment);
                }
            });
        }
    }
    public class LikeMyReviewViewHolder extends CommentViewHolder{
        public LikeMyReviewViewHolder(View view) {
            super(view);
        }

        @Override
        void setLayoutContent(final MessageComment comment) {
            noticeResponseTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToReivew(comment);
                }
            });
            noticeResponseContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToReivew(comment);
                }
            });
        }
    }
    //目前暂时不用
    public class ReplyMyRiewComment extends CommentViewHolder{
        public ReplyMyRiewComment(View view) {
            super(view);
        }

        @Override
        void setLayoutContent(final MessageComment comment) {
            noticeResponseTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToReivew(comment);
                }
            });
            noticeResponseContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToReivew(comment);
                }
            });
        }
    }
    public class ReplyMyTopicComment extends CommentViewHolder{
        public ReplyMyTopicComment(View view) {
            super(view);
        }

        @Override
        void setLayoutContent(final MessageComment comment) {
            noticeResponseTitle.setText(comment.getParentContent());
            if(!isNull(context,comment.getChildContent(),"")){
                RichTextUtils.showHtmlText(comment.getChildContent(),noticeResponseContent);
            }
            tvSay.setText(" 说：");
            noticeResponseTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToTopic(comment);
                }
            });
            noticeResponseContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToTopic(comment);
                }
            });
        }
    }

    /**
     * 跳转到话题详情
     * @param comment
     */
    private void goToTopic(MessageComment comment) {
        int topicId;
        int groupId;
        try {
			topicId = Integer.parseInt(comment.getResponsedId());
			groupId = Integer.parseInt(comment.getGroupId());
		} catch (NumberFormatException e) {
			topicId = 0;
			e.printStackTrace();
			return;
		}
        Intent intent = new Intent(context, DiscoverTopicActivity.class);
        intent.putExtra(Constants.topicId,topicId);
        intent.putExtra(Constants.groupId,groupId);
        intent.putExtra(Constants.TOPIC_ACITIVITY_TITLE,comment.getResponsedName());
        context.startActivity(intent);
    }

    /**
     * 跳转到书评页面
     * @param comment
     */
    private void goToReivew(MessageComment comment) {
        int reviewId = 0;
        try {
            reviewId = Integer.parseInt(comment.getResponsedId());
        } catch (NumberFormatException e) {
            reviewId = 0;
            e.printStackTrace();
        }
        Intent intent = new Intent(context, DiscoverBookReviewDetailActivity.class);
        intent.putExtra(Constants.reviewId,reviewId);
        intent.putExtra(Constants.THOUGHT_ACTIVITY_TITLE,comment.getResponsedName());
        context.startActivity(intent);
    }


    public OnDeleteListener getOnDeleteListener() {
        return onDeleteListener;
    }

    public void setOnDeleteListener(NewMessageCommentAdapter.OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    public OnItemClickedListener getOnItemClickedListener() {
        return onItemClickedListener;
    }

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

    public interface OnDeleteListener{
        public void onDelete(int id,int position);
    }
}
