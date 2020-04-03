package com.iyangcong.reader.ui.swipecardview;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;
import android.widget.FrameLayout;

import java.util.ArrayList;

public class SwipeFlingAdapterView2 extends BaseFlingAdapterView {
    // 缓存view列表
    private ArrayList<View> cacheItems = new ArrayList<>();

    private Adapter mAdapter;
    //    private onFlingListener mFlingListener;
//    private AdapterDataSetObserver mDataSetObserver;
//    private FlingCardListener flingCardListener;
    private boolean mInLayout = false;
    private int mActivePosition;
    private View mActiveCard = null;
    private AdapterDataSetObserver mDataSetObserver;
    private float originX;
    private float originY;
    private SwipeViewListener mSwipeViewListener;

    public SwipeFlingAdapterView2(Context context) {
        super(context);
    }

    public SwipeFlingAdapterView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeFlingAdapterView2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setSwipeViewListener(SwipeViewListener swipeViewListener) {
        mSwipeViewListener = swipeViewListener;
    }

    @Override
    public Adapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (mAdapter != null && mDataSetObserver != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
            mDataSetObserver = null;
        }

        mAdapter = adapter;
        final int adapterCount = mAdapter.getCount();
        mActivePosition = adapterCount-1;
        Log.e("papa", "setAdapter mActivePosition: " + mActivePosition);
        initChildren(0, adapterCount);

        if (mAdapter != null && mDataSetObserver == null) {
            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);
        }
    }

    @Override
    public View getSelectedView() {
        return mActiveCard;
    }

    @Override
    public void requestLayout() {
        if (!mInLayout) {
            super.requestLayout();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // if we don't have an adapter, we don't need to do anything
        if (mAdapter == null) {
            return;
        }

        mInLayout = true;
//        final int adapterCount = mAdapter.getCount();
        Log.e("papa", "onLayout mActivePosition: " + mActivePosition);
//        layoutChildren(0, adapterCount);
//        if(cacheItems.size() != adapterCount){
//            removeAndAddToCache(0);
//            Log.e("papa", "onLayout: " + cacheItems.size());
//        }
        Log.e("papa", "onLayout getChildCount(): " + getChildCount());
        if(mSwipeViewListener != null){
            mSwipeViewListener.onIndexChanged(mAdapter.getCount() - mActivePosition);
        }
        layoutChildren();
        mInLayout = false;
    }

//    private void removeAndAddToCache(int remain) {
//        View view;
//        for (int i = 0; i < getChildCount() - remain; ) {
//            view = getChildAt(i);
//            removeViewInLayout(view);
//            cacheItems.add(view);
//        }
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            //如果是view:触发view的测量;如果是ViewGroup，触发测量ViewGroup中的子view
//            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getChildAt(i).getLayoutParams();
            int childWidthSpec = getChildMeasureSpec(getWidthMeasureSpec(),
                    getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin,
                    lp.width);
            int childHeightSpec = getChildMeasureSpec(getHeightMeasureSpec(),
                    getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin,
                    lp.height);
            getChildAt(i).measure(childWidthSpec, childHeightSpec);
        }
    }

    private void layoutChildren(){
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            makeAndAddView(childView, 0, true);
        }
    }

    private void initChildren(int startingIndex, int adapterCount){
        while (startingIndex < adapterCount) {
            View item = null;
//            if (cacheItems.size() > 0) {
//                item = cacheItems.get(startingIndex);
//                cacheItems.remove(item);
//            }
            View newUnderChild = mAdapter.getView(startingIndex, item, this);
            boolean isAdded = false;
            if (newUnderChild.getVisibility() != GONE) {
                makeAndAddView(newUnderChild, startingIndex, isAdded);
            }
            startingIndex++;
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void makeAndAddView(View child, int index, boolean isAdded) {

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
        if(!isAdded){
            addViewInLayout(child, 0, lp, true);
        }

//        final boolean needToMeasure = child.isLayoutRequested();
//        Log.e("papa", "needToMeasure: " + needToMeasure);
//        if (needToMeasure) {
//            int childWidthSpec = getChildMeasureSpec(getWidthMeasureSpec(),
//                    getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin,
//                    lp.width);
//            int childHeightSpec = getChildMeasureSpec(getHeightMeasureSpec(),
//                    getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin,
//                    lp.height);
//            child.measure(childWidthSpec, childHeightSpec);
//        } else {
//            cleanupLayoutState(child);
//        }

        int w = child.getMeasuredWidth();
        int h = child.getMeasuredHeight();

        int gravity = lp.gravity;
        if (gravity == -1) {
            gravity = Gravity.TOP | Gravity.START;
        }

        int layoutDirection = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)
            layoutDirection = getLayoutDirection();
        final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
        final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

        int childLeft;
        int childTop;
        switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.CENTER_HORIZONTAL:
                childLeft = (getWidth() + getPaddingLeft() - getPaddingRight()  - w) / 2 +
                        lp.leftMargin - lp.rightMargin;
                break;
            case Gravity.END:
                childLeft = getWidth() + getPaddingRight() - w - lp.rightMargin;
                break;
            case Gravity.START:
            default:
                childLeft = getPaddingLeft() + lp.leftMargin;
                break;
        }
        switch (verticalGravity) {
            case Gravity.CENTER_VERTICAL:
                childTop = (getHeight() + getPaddingTop() - getPaddingBottom()  - h) / 2 +
                        lp.topMargin - lp.bottomMargin;
                break;
            case Gravity.BOTTOM:
                childTop = getHeight() - getPaddingBottom() - h - lp.bottomMargin;
                break;
            case Gravity.TOP:
            default:
                childTop = getPaddingTop() + lp.topMargin;
                break;
        }
        child.layout(childLeft, childTop, childLeft + w, childTop + h);
        if (originX == 0)
            originX = child.getX();
        if (originY == 0)
            originY = child.getY();
        // 缩放层叠效果
//        adjustChildView(child, index);
    }

    public void next() {
        int index = mAdapter.getCount() - mActivePosition - 1;
        mActiveCard = getChildAt(mActivePosition);
        if(getChildAt(mActivePosition-1) == null ||index < 0 || index> mAdapter.getCount()-1){
            return;
        }
        mActivePosition--;
        FlingCardLisener2 fcl = new FlingCardLisener2(mActiveCard, mAdapter.getItem(index),
                0,originX,originY, new FlingCardLisener2.FlingListener() {
            @Override
            public void onAnimaEnd() {
            }

            @Override
            public void onObjectEnd(Object dataObject) {
            }
        });
        fcl.goNext(400);
        Log.e("papa", "goNext mActivePosition: " + mActivePosition);
    }

    public void pre() {
        int index = mAdapter.getCount() - mActivePosition - 1;
        mActiveCard = getChildAt(mActivePosition+1);
        if(mActiveCard == null || index <= 0 || index> mAdapter.getCount()-1){
            return;
        }
        mActivePosition ++;
        FlingCardLisener2 fcl = new FlingCardLisener2(mActiveCard, mAdapter.getItem(index),
                0,originX,originY, new FlingCardLisener2.FlingListener() {
            @Override
            public void onAnimaEnd() {
            }
            @Override
            public void onObjectEnd(Object dataObject) {
            }
        });
        fcl.goPre(400);
        Log.e("papa", "pre mActivePosition: " + mActivePosition);
    }

    public void setActivePosition(int position){
        final int position1 = mAdapter.getCount() - position;
        if(position1 < 0 || position1 > mAdapter.getCount()-1 || mActivePosition == position1){
            return;
        }
        if(position1 < mActivePosition){
            this.post(new Runnable() {
                @Override
                public void run() {
                    while(mActivePosition != position1){
                        next();
                    }
                }
            });
        }else {
            this.post(new Runnable() {
                @Override
                public void run() {
                    while(mActivePosition != position1){
                        pre();
                    }
                }
            });
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new FrameLayout.LayoutParams(getContext(), attrs);
    }

    private class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            requestLayout();
        }

        @Override
        public void onInvalidated() {
            requestLayout();
        }
    }

    public interface SwipeViewListener{
        void onIndexChanged(int nowIndex);
    }
}