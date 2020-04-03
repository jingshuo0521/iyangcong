package com.iyangcong.reader.adapter;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.BookNoteBean;
import com.iyangcong.reader.bean.CommentNew;
import com.iyangcong.reader.utils.GlideImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ContentDetailAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<CommentNew> listData;
    public ContentDetailAdapter(Context mContext, List<CommentNew> listData) {
        this.mContext = mContext;
        this.listData = listData;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(mContext, mLayoutInflater.from(mContext).inflate(R.layout.content_detail_child, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final Context mContext;
        @BindView(R.id.contentId)
        TextView tvContent;
        @BindView(R.id.tvLike)
        TextView tvLikeCount;
        @BindView(R.id.iv_mine_head)
        ImageView imPhoto;
        @BindView(R.id.tv_huifu)
        TextView tvHuifu;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.ll_background)
        LinearLayout tvBackground;


        @BindView(R.id.ll_content)
        LinearLayout llContent;

        ViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        void setData(CommentNew commentNew) {
            tvContent.setText(commentNew.getUsercontent());
            tvLikeCount.setText(commentNew.getLikecount()+"");
            tvHuifu.setText(commentNew.getHuifushu()+"");
            tvName.setText(commentNew.getUsername());
            GlideImageLoader.displayProtrait(mContext, commentNew.getUserphoto(), imPhoto);

        }
    }
}
