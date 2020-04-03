package com.iyangcong.reader.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.iyangcong.reader.R;

/**
 * Created by Administrator on 2017/3/16.
 */

public class QQListView2 extends ListView {

    private int mScreenWidth; // 屏幕宽度
    private int mDownX; // 按下点的x值
    private int mDownY; // 按下点的y值
    private int mDeleteBtnWidth;// 删除按钮的宽度

    private boolean isDeleteShown; // 删除按钮是否正在显示

    private ViewGroup mPointChild; // 当前处理的item
    private LinearLayout.LayoutParams mLayoutParams; // 当前处理的item的LayoutParams
    private int myItemDis;
    // private Context mContext;
    private int ListItemMarginBeside;
    private boolean isSliding;

    public QQListView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QQListView2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // 获取屏幕宽度
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isSliding) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //performActionDown(ev);
                    break;
                case MotionEvent.ACTION_MOVE:
                    //return performActionMove(ev);
                case MotionEvent.ACTION_UP:
                    //performActionUp();
                    isSliding=false;
                    break;
            }
            return true;
        }

        return super.onTouchEvent(ev);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        int action = ev.getAction();
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) ev.getX();
                mDownY = (int) ev.getY();
                // 获取当前点的item
                mPointChild = (ViewGroup) getChildAt(pointToPosition(mDownX, mDownY)
                        - getFirstVisiblePosition());
                if(mPointChild!=null){
                    mDeleteBtnWidth = mPointChild.getChildAt(1).getLayoutParams().width
                            + ListItemMarginBeside;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                int nowX = (int) ev.getX();
                int nowY = (int) ev.getY();

                if (Math.abs(nowX - mDownX) > Math.abs(nowY - mDownY)) {
                    // 如果向左滑动
                    if (nowX < mDownX) {
                        // 计算要偏移的距离
                        int scroll = (nowX - mDownX) / 2;
                        // 如果大于了删除按钮的宽度， 则最大为删除按钮的宽度
                        if (-scroll >= mDeleteBtnWidth) {
                            scroll = -mDeleteBtnWidth;
                        }
                        myItemDis = scroll;
                        isSliding = true;

                    } else if (nowX > mDownX) {
                        int scroll = (nowX - mDownX) / 2;
                        // 如果大于了删除按钮的宽度， 则最大为删除按钮的宽度
                        if (scroll >= mDeleteBtnWidth) {
                            scroll = mDeleteBtnWidth;
                        }
                        myItemDis = scroll;
                        isSliding = true;
                    }

                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mPointChild != null) {
                    if (myItemDis < 0 && -myItemDis >= mDeleteBtnWidth / 2) {
                        isDeleteShown = true;
                        LinearLayout mpointchild_0=(LinearLayout)mPointChild.findViewById(R.id.mpointchild_0);
                        LinearLayout item_container=(LinearLayout)mpointchild_0.findViewById(R.id.item_container);
                        FrameLayout recite_frame=(FrameLayout)item_container.findViewById(R.id.recite_frame);
                        LinearLayout first_layout=(LinearLayout)recite_frame.findViewById(R.id.first_layout);
                        LinearLayout second_layout=(LinearLayout)recite_frame.findViewById(R.id.second_layout);

                        if(second_layout!=null){
                            second_layout.setVisibility(View.VISIBLE);
                        }
                        isDeleteShown = true;
                    } else if (myItemDis > 0 && myItemDis >= mDeleteBtnWidth / 2) {

                        LinearLayout mpointchild_0=(LinearLayout)mPointChild.findViewById(R.id.mpointchild_0);
                        LinearLayout item_container=(LinearLayout)mpointchild_0.findViewById(R.id.item_container);
                        FrameLayout recite_frame=(FrameLayout)item_container.findViewById(R.id.recite_frame);
                        LinearLayout first_layout=(LinearLayout)recite_frame.findViewById(R.id.first_layout);
                        LinearLayout second_layout=(LinearLayout)recite_frame.findViewById(R.id.second_layout);

                        if(first_layout!=null){
                            first_layout.setVisibility(View.VISIBLE);
                            second_layout.setVisibility(View.INVISIBLE);
                            isDeleteShown = false;
                        }

                    } else {
                        //turnToNormal();
                    }
                }
                break;

            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }





    /**
     * 变为正常状态
     */
    public void turnToNormal() {

        if (mPointChild != null) {
            LinearLayout mpointchild_0=(LinearLayout)mPointChild.findViewById(R.id.mpointchild_0);
            LinearLayout item_container=(LinearLayout)mpointchild_0.findViewById(R.id.item_container);
            FrameLayout recite_frame=(FrameLayout)item_container.findViewById(R.id.recite_frame);
            LinearLayout first_layout=(LinearLayout)recite_frame.findViewById(R.id.first_layout);
            LinearLayout second_layout=(LinearLayout)recite_frame.findViewById(R.id.second_layout);

            if(first_layout!=null){
                first_layout.setVisibility(View.VISIBLE);
                second_layout.setVisibility(View.INVISIBLE);
                isDeleteShown = false;
            }
        }
        isDeleteShown = false;
    }



    /**
     * 当前是否可点击
     *
     * @return 是否可点击
     */
    public boolean canClick() {
        return !isDeleteShown;
    }
}

