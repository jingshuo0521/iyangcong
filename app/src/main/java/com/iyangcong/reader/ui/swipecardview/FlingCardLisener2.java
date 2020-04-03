package com.iyangcong.reader.ui.swipecardview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

public class FlingCardLisener2 {

    private final float objectX;
    private final float objectY;
    private final int objectH;
    private final int objectW;
    private final int parentWidth;
    private final int parentHeight;
    private final float originX;
    private final float originY;
    private final FlingListener mFlingListener;
    private final Object dataObject;
    private final float halfWidth;
    private final float halfHeight;
    private float BASE_ROTATION_DEGREES;

    private float aPosX;
    private float aPosY;
    private float aDownTouchX;
    private float aDownTouchY;
    private static final int INVALID_POINTER_ID = -1;

    // The active pointer is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;
    private View frame = null;

    private final int TOUCH_ABOVE = 0;
    private final int TOUCH_BELOW = 1;
    private int touchPosition;
    // private final Object obj = new Object();
    private boolean isAnimationRunning = false;
    private float MAX_COS = (float) Math.cos(Math.toRadians(45));
    // 支持左右滑
    private boolean isNeedSwipe = true;

    private float aTouchUpX;

    private int animDuration = 300;
    private float scale;

    public FlingCardLisener2(View frame, Object itemAtPosition, float rotation_degrees, float originX, float originY, FlingListener flingListener) {
        super();
        this.frame = frame;
        this.objectX = frame.getX();
        this.objectY = frame.getY();
        this.objectW = frame.getWidth();
        this.objectH = frame.getHeight();
        this.halfWidth = objectW/2f;
        this.halfHeight = objectH/2f;
        this.dataObject = itemAtPosition;
        this.parentWidth = ((ViewGroup) frame.getParent()).getWidth();
        this.parentHeight = ((ViewGroup) frame.getParent()).getHeight();
        this.originX = originX;
        this.originY = originY;
        this.BASE_ROTATION_DEGREES = rotation_degrees;
        this.mFlingListener = flingListener;
    }

    public void onSelected(final boolean isLeft, float exitY, long duration){
        isAnimationRunning = true;
        float exitX;
        if(isLeft) {
            exitX = -objectW-getRotationWidthOffset();
        }else {
            exitX = parentWidth+getRotationWidthOffset();
        }
        Log.e("papa", "parentWidth:" + parentWidth + ",getRotationWidthOffset()" + getRotationWidthOffset() + ",exitX:" + exitX + ",objectX:" + objectX);
        this.frame.animate()
                .setDuration(duration)
                .setInterpolator(new LinearInterpolator())
                .translationX(exitX)
                .translationY(exitY)
                //.rotation(isLeft ? -BASE_ROTATION_DEGREES:BASE_ROTATION_DEGREES)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Log.e("papa", "goNext end" + ",objectX:" + objectX);
                        mFlingListener.onAnimaEnd();
                        mFlingListener.onObjectEnd(dataObject);
                        isAnimationRunning = false;
                        frame.setVisibility(View.GONE);
                    }
                }).start();
    }

    public void onReset(){
        isAnimationRunning = true;
        frame.setVisibility(View.VISIBLE);
        frame.animate()
                .setDuration(animDuration)
                .setInterpolator(new LinearInterpolator())
                .x(originX)
                .y(originY)
//                .rotation(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mFlingListener.onAnimaEnd();
                        mFlingListener.onObjectEnd(dataObject);
                        isAnimationRunning = false;
                    }
                }).start();
    }
    /**
     * When the object rotates it's width becomes bigger.
     * The maximum width is at 45 degrees.
     *
     * The below method calculates the width offset of the rotation.
     *
     */
    private float getRotationWidthOffset() {
        return objectW/MAX_COS - objectW;
    }

    private float getRotationHeightOffset() {
        return objectH/MAX_COS - objectH;
    }

    protected interface FlingListener {
        void onAnimaEnd();
        void onObjectEnd(Object dataObject);
    }

    public void goNext(long duration){
        if(!isAnimationRunning)
            onSelected(false, objectY, duration);
    }

    public void goPre(long duration){
        if(!isAnimationRunning)
            onReset();
    }
}

