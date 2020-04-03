package com.iyangcong.reader.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.AuthorIntroduction;
import com.iyangcong.reader.bean.BaseBook;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lg on 2016/12/24.
 */

public class BookMarketAboutAuthorActivity extends SwipeBackActivity {
    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.im_author_head)
    ImageView imAuthorHead;
    @BindView(R.id.im_author_name)
    TextView imAuthorName;
    @BindView(R.id.im_author_introduce)
    TextView imAuthorIntroduce;
    @BindView(R.id.gv_about_author_book)
    GridView gvAboutAuthorBook;

    private AuthorIntroduction bookMarketAboutAuthorContent = new AuthorIntroduction();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_market_about_author);
        ButterKnife.bind(this);
        initViewData();
        initView();
    }

    protected void initView() {
        btnFunction.setVisibility(View.GONE);
        textHeadTitle.setText("作者简介");
        btnBack.setImageResource(R.drawable.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imAuthorName.setText(bookMarketAboutAuthorContent.getName());
        imAuthorIntroduce.setText(bookMarketAboutAuthorContent.getIntroduce());
        gvAboutAuthorBook.setAdapter(new AuthorBookGridAdapter(this, bookMarketAboutAuthorContent.getBookList()));
    }

    @Override
    protected void setMainHeadView() {

    }

    private void initViewData() {
        bookMarketAboutAuthorContent.setName("郭敬明");
        bookMarketAboutAuthorContent.setIntroduce("        郭敬明，1983年6月6日出生于四川自贡，中国作家、上海最世文化发展有限公司董事长、《最小说》等杂志主编。\n" +
                "        高中时期以“第四维”为笔名在网站榕树下发表文章。2002年出版第一部作品《爱与痛的边缘》。高中时期以“第四维”为笔名在网站榕树下发表文章。2002年出版第一部作品《爱与痛的边缘》。2002年出版第一部作品《爱与痛的边缘》。2002年出版第一部作品《爱与痛的边缘》。");
        List<BaseBook> temp = new ArrayList<>();
        temp.add(new BaseBook());
        temp.add(new BaseBook());
        temp.add(new BaseBook());
        temp.add(new BaseBook());
        bookMarketAboutAuthorContent.setBookList(temp);
    }

    class AuthorBookGridAdapter extends BaseAdapter {
        private Context mContext;

        private List<BaseBook> bookList;

        public AuthorBookGridAdapter(Context mContext, List<BaseBook> bookList) {
            this.mContext = mContext;
            this.bookList = bookList;
        }

        @Override
        public int getCount() {
            return bookList.size();
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
                convertView = View.inflate(mContext, R.layout.item_shelf_book, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvBookState.setVisibility(View.GONE);
            holder.pgBookAdded.setVisibility(View.GONE);
            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.iv_shelf_book_image)
            ImageView ivShelfBookImage;
            @BindView(R.id.tv_book_name)
            TextView tvBookName;
            @BindView(R.id.pg_book_added)
            RoundCornerProgressBar pgBookAdded;
            @BindView(R.id.tv_book_state)
            TextView tvBookState;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
    }
}
