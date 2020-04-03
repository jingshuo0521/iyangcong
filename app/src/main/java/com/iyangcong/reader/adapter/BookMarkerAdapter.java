package com.iyangcong.reader.adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.ui.IYangCongToast;

import org.geometerplus.fbreader.book.Bookmark;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ljw on 2017/4/19.
 */

public class BookMarkerAdapter extends BaseAdapter {

    private List<Bookmark> bookmarks;
    private Context context;

    public BookMarkerAdapter(Context context, List<Bookmark> bookmarks) {
        this.bookmarks = bookmarks;
        this.context = context;
    }

    @Override
    public int getCount() {
        return bookmarks == null ? 0 : bookmarks.size();
    }

    @Override
    public Object getItem(int i) {
        return bookmarks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_book_marker, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvMarkerContent.setText(bookmarks.get(i).getText());
        return convertView;
    }


    static class ViewHolder {
        @BindView(R.id.tv_marker_content)
        TextView tvMarkerContent;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
