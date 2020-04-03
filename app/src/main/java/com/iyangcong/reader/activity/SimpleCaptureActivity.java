package com.iyangcong.reader.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.GuessYourLove;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.StringUtils;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.xudaojie.qrcodelib.CaptureActivity;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by xdj on 16/9/17.
 */

public class SimpleCaptureActivity extends CaptureActivity {
    protected Activity mActivity = this;
    private List<GuessYourLove> recommendedBooklist;
    private AlertDialog mDialog;
    private RecommendedBookAdapter recommendedbookAdapter;
    private GridView gvRecommendedBook;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mActivity = this;
        super.onCreate(savedInstanceState);
        gvRecommendedBook = (GridView) findViewById(R.id.gv_recommended_book);

        initData();
    }

    private void initData(){
        recommendedBooklist = new ArrayList<GuessYourLove>();
        getDatasFromNetwork();
        recommendedbookAdapter = new RecommendedBookAdapter();
        gvRecommendedBook.setAdapter(recommendedbookAdapter);
        gvRecommendedBook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(SimpleCaptureActivity.this, BookMarketBookDetailsActivity.class);
                intent.putExtra("bookId",  recommendedBooklist.get(position).getBookId());
                intent.putExtra("bookName", recommendedBooklist.get(position).getBookName());
                startActivity(intent);
            }
        });
    }

    private void getDatasFromNetwork() {
        OkGo.get(Urls.GetOutLessBooks)
                .tag(this)
                .params("userid", CommonUtil.getUserId())
                .execute(new JsonCallback<IycResponse <List<GuessYourLove>>>(this) {
                    @Override
                    public void onSuccess(IycResponse <List<GuessYourLove>> iycResponse, Call call, Response response) {

                        for (GuessYourLove marketBookDetailsSameBooks:iycResponse.getData()){
                            recommendedBooklist.add(marketBookDetailsSameBooks);
                        }
                        recommendedbookAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onError(Call call, Response response, Exception e) {

                    }
                });
    }

    class RecommendedBookAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            int maxSize = 0;
            if (recommendedBooklist != null) {
                if (recommendedBooklist.size() > 3) {
                    maxSize = 3;
                } else {
                    maxSize = recommendedBooklist.size();
                }
            }
            return recommendedBooklist == null ? 0 : maxSize;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(SimpleCaptureActivity.this, R.layout.item_recommended_book, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            GlideImageLoader.displayBookCover(SimpleCaptureActivity.this, holder.ivShelfBookImage, recommendedBooklist.get(position).getBookImgUrl());
            holder.tvBookName.setText(recommendedBooklist.get(position).getBookName());
            //holder.tvBookName.setGravity(Gravity.CENTER_HORIZONTAL);
            return convertView;
        }

        public class ViewHolder {

            @BindView(R.id.iv_shelf_book_image)
            ImageView ivShelfBookImage;
            @BindView(R.id.tv_book_name)
            TextView tvBookName;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    @Override
    protected void handleResult(String resultString) {
        if (TextUtils.isEmpty(resultString)) {
            Toast.makeText(mActivity, R.string.scan_failed, Toast.LENGTH_SHORT).show();
            restartPreview();
        } else {
            if(resultString.indexOf(",") >= 0) {
                try {
                    String[] book = resultString.split(",");
                    String id = book[0].replaceAll("\\D+", "").replaceAll("\r", "").replaceAll("\n", "").trim();
                    if (StringUtils.isInteger(id)) {
                        Intent intent = new Intent(SimpleCaptureActivity.this, BookMarketBookDetailsActivity.class);
                        intent.putExtra("bookId", Integer.parseInt(id));
                        intent.putExtra("bookName", book[1]);
                        startActivity(intent);
                    }else{
                        if(resultString.contains("iycbookid=")){
                            String[] bookid = resultString.split("iycbookid=");
                            if(bookid.length>1){
                                Intent intent = new Intent(SimpleCaptureActivity.this, BookMarketBookDetailsActivity.class);
                                intent.putExtra("bookId", Integer.parseInt(bookid[1]));
                                intent.putExtra("bookName", "");
                                startActivity(intent);
                            }
                        }else {
                            Toast.makeText(mActivity, R.string.scan_notsupport, Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(getIntent());
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(mActivity, R.string.scan_notsupport, Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                }
            }else{
                if(resultString.contains("iycbookid=")){
                    String[] bookid = resultString.split("iycbookid=");
                    if(bookid.length>1){
                        Intent intent = new Intent(SimpleCaptureActivity.this, BookMarketBookDetailsActivity.class);
                        intent.putExtra("bookId", Integer.parseInt(bookid[1]));
                        intent.putExtra("bookName", "");
                        startActivity(intent);
                    }
                }else {
                    Toast.makeText(mActivity, R.string.scan_notsupport, Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                }
            }
        }
    }
}
