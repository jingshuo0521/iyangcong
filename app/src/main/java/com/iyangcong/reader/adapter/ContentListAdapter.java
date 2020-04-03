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
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.BookNoteBean;
import com.iyangcong.reader.bean.CommentNew;
import com.iyangcong.reader.bean.PublicComment;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.utils.BookInfoUtils;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import org.geometerplus.android.fbreader.FBReader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;


public class ContentListAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ContentDetailAdapter commentDetailAdapter;
    private LayoutInflater mLayoutInflater;
    private List<PublicComment> listData;
    public ContentListAdapter(Context mContext, List<PublicComment> listData) {
        this.mContext = mContext;
        this.listData = listData;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mContext, mLayoutInflater.from(mContext).inflate(R.layout.content_child, parent, false));
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
        @BindView(R.id.allCount)
        TextView allCount;

        @BindView(R.id.ll_comment)
        LinearLayout llComment;

        ViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        void setData(PublicComment publicComment) {
            tvContent.setText(publicComment.getContent());
            allCount.setText(publicComment.getAllcount()+"");
            llComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCommentNew(publicComment);
                }
            });


        }

        private void getCommentNew(PublicComment publicComment){
            OkGo.get(Urls.COMMENTSNEW)
                    .tag(this)
                    .params("type", publicComment.getType())
                    .params("id", publicComment.getId())
                    .execute(new JsonCallback<IycResponse<List<CommentNew>>>(mContext) {
                        @Override
                        public void onSuccess(IycResponse<List<CommentNew>> listIycResponse, Call call, Response response) {
                            Logger.e("gft获取公开笔记评论成功！",listIycResponse.getData().toString());
                            List<CommentNew> commentNewList = listIycResponse.getData().subList(1,listIycResponse.getData().size());
                            if (commentNewList.size()>0){
                                showDialog(commentNewList);
                            }
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            Logger.e("gft获取公开笔记评论失败！");
                            ToastCompat.makeText(mContext, "获取公开笔记失败！", Toast.LENGTH_SHORT);
                        }
                    });

        }
        private void showDialog(List<CommentNew> list){
            // 以特定的风格创建一个dialog
            Dialog dialog = new Dialog(mContext,R.style.MyDialog);
            // 加载dialog布局view
            View purchase = LayoutInflater.from(mContext).inflate(R.layout.content_detail_list, null);
            ImageView tvCancel = purchase.findViewById(R.id.img_back);
            commentDetailAdapter = new ContentDetailAdapter(mContext,list);
            RecyclerView rvComment = purchase.findViewById(R.id.rv_comment_detail_list);
            LinearLayoutManager llm = new LinearLayoutManager(mContext);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            rvComment.setLayoutManager(llm);
            rvComment.setAdapter(commentDetailAdapter);
            // 设置外部点击 取消dialog
            dialog.setCancelable(true);
            // 获得窗体对象
            Window window = dialog.getWindow();
            // 设置窗体的对齐方式
            window.setGravity(Gravity.TOP);
            // 设置窗体动画
            window.setWindowAnimations(R.style.AnimBottom);
            // 设置窗体的padding
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            dialog.setContentView(purchase);
            dialog.show();
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

    }
}
