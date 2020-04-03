package com.iyangcong.reader.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.iyangcong.reader.R;
public class TestAdapter extends PagerAdapter {

    private Context mContext;
    private int[] imags;

    public TestAdapter(Context cx) {
        mContext = cx.getApplicationContext();
        imags = new int[]{R.drawable.fbreader_guide_first,R.drawable.fbreader_guide_second};
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 2;
    }
    @Override
    public boolean isViewFromObject(View view, Object obj) {
        // TODO Auto-generated method stub
        return view == (View) obj;
    }

    @Override
    public Object instantiateItem (ViewGroup container, int position) {
        ImageView iv = new ImageView(mContext);
        iv.setBackgroundResource(imags[position]);
        ((ViewPager)container).addView(iv, 0);
        return iv;
    }
    @Override
    public void destroyItem (ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}

