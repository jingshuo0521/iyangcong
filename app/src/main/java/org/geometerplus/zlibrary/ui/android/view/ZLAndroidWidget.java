/*
 * Copyright (C) 2007-2015 FBReader.ORG Limited <contact@fbreader.org>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.zlibrary.ui.android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import com.iyangcong.reader.ReadingTask.SingleTimer;
import org.geometerplus.android.fbreader.FBReader;
import org.geometerplus.fbreader.Paths;
import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.core.application.ZLKeyBindings;
import org.geometerplus.zlibrary.core.util.SystemInfo;
import org.geometerplus.zlibrary.core.view.ZLView;
import org.geometerplus.zlibrary.core.view.ZLViewWidget;
import org.geometerplus.zlibrary.text.view.ZLTextView;
import org.geometerplus.zlibrary.text.view.ZLTextWordCursor;
import org.geometerplus.zlibrary.ui.android.view.animation.AnimationProvider;
import org.geometerplus.zlibrary.ui.android.view.animation.CurlAnimationProvider;
import org.geometerplus.zlibrary.ui.android.view.animation.NoneAnimationProvider;
import org.geometerplus.zlibrary.ui.android.view.animation.ShiftAnimationProvider;
import org.geometerplus.zlibrary.ui.android.view.animation.SlideAnimationProvider;
import org.geometerplus.zlibrary.ui.android.view.animation.SlideOldStyleAnimationProvider;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ZLAndroidWidget extends MainView implements ZLViewWidget, View.OnLongClickListener {
    public final ExecutorService PrepareService = Executors.newSingleThreadExecutor();

    private final Paint myPaint = new Paint();

    private final BitmapManagerImpl myBitmapManager = new BitmapManagerImpl(this);
    private Bitmap myFooterBitmap;
    private Bitmap myHeadBitmap;
    private final SystemInfo mySystemInfo;

    public ZLAndroidWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mySystemInfo = Paths.systemInfo(context);
        init();
    }

    public ZLAndroidWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        mySystemInfo = Paths.systemInfo(context);
        init();
    }

    public ZLAndroidWidget(Context context) {
        super(context);
        mySystemInfo = Paths.systemInfo(context);
        init();
    }

    private void init() {
        // next line prevent ignoring first onKeyDown DPad event
        // after any dialog was closed
        setFocusableInTouchMode(true);
        setDrawingCacheEnabled(false);
        setOnLongClickListener(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        getAnimationProvider().terminate();
        if (myScreenIsTouched) {
            final ZLView view = ZLApplication.Instance().getCurrentView();
            myScreenIsTouched = false;
            view.onScrollingFinished(ZLView.PageIndex.current);
        }
    }

/* author:GuoFangtao </br>
 * time:2019-2-20 10:00 </br>
 * ZLAndroidWidget类的onDraw方法被调用时，代码会进行一个判断，看当前是否处于翻页动画中。
 * 如果当前处在翻页动画中，那么就调用ZLAndroidWidget类的onDrawInScrolling方法
 *（翻页动画的原理其实就是不断得在屏幕上画两个页面，一个页面一直不动，
 * 另一个页面则不断向左边或右边移动，这个部分之后会有章节专门介绍）。
 * 如果不在翻页动画中，那么就调用ZLAndroidWidget类的onDrawStatic方法。
 * 初次打开程序的时候，无疑一定是不会在翻页动画中的。
 */
    @Override
    protected void onDraw(final Canvas canvas) {
        final Context context = getContext();
        if (context instanceof FBReader) {
            ((FBReader) context).createWakeLock();
        } else {
            System.err.println("A surprise: view's context is not an FBReader");
        }
        super.onDraw(canvas);

//		final int w = getWidth();
//		final int h = getMainAreaHeight();

        myBitmapManager.setSize(getWidth(), getMainAreaHeight()/* - getHeadHeight()*/);
        //判断程序是否处于翻页动画中
        if (getAnimationProvider().inProgress()) {
            onDrawInScrolling(canvas);
        } else {
            onDrawStatic(canvas);
            ZLApplication.Instance().onRepaintFinished();
        }
    }



    private AnimationProvider myAnimationProvider;
    private ZLView.Animation myAnimationType;

    private AnimationProvider getAnimationProvider() {
        final ZLView.Animation type = ZLApplication.Instance().getCurrentView().getAnimationType();
        if (myAnimationProvider == null || myAnimationType != type) {
            myAnimationType = type;
            switch (type) {
                case none:
                    myAnimationProvider = new NoneAnimationProvider(myBitmapManager);
                    break;
                case curl:
                    myAnimationProvider = new CurlAnimationProvider(myBitmapManager);
                    break;
                case slide:
                    myAnimationProvider = new SlideAnimationProvider(myBitmapManager);
                    break;
                case slideOldStyle:
                    myAnimationProvider = new SlideOldStyleAnimationProvider(myBitmapManager);
                    break;
                case shift:
                    myAnimationProvider = new ShiftAnimationProvider(myBitmapManager);
                    break;
            }
        }
        return myAnimationProvider;
    }

    private void onDrawInScrolling(Canvas canvas) {
        final ZLView view = ZLApplication.Instance().getCurrentView();

        final AnimationProvider animator = getAnimationProvider();
        final AnimationProvider.Mode oldMode = animator.getMode();
        animator.doStep();
        if (animator.inProgress()) {
            animator.draw(canvas);
            if (animator.getMode().Auto) {
                postInvalidate();
            }
            drawFooter(canvas, animator);
            drawHead(canvas, animator);
        } else {
            switch (oldMode) {
                case AnimatedScrollingForward: {
                    final ZLView.PageIndex index = animator.getPageToScrollTo();
                    myBitmapManager.shift(index == ZLView.PageIndex.next);
                    view.onScrollingFinished(index);
                    ZLApplication.Instance().onRepaintFinished();
                    break;
                }
                case AnimatedScrollingBackward:
                    view.onScrollingFinished(ZLView.PageIndex.current);
                    break;
            }
            onDrawStatic(canvas);
        }
    }

    @Override
    public void reset() {
        myBitmapManager.reset();
    }

    @Override
    public void repaint() {
        postInvalidate();
    }

    @Override
    public void startManualScrolling(int x, int y, ZLView.Direction direction) {
        final AnimationProvider animator = getAnimationProvider();
        animator.setup(direction, getWidth(), getMainAreaHeight(), myColorLevel);
        animator.startManualScrolling(x, y);
    }

    @Override
    public void scrollManuallyTo(int x, int y) {
        final ZLView view = ZLApplication.Instance().getCurrentView();
        final AnimationProvider animator = getAnimationProvider();
        //ljw	在这判断是否到最后一页了
        if (view.canScroll(animator.getPageToScrollTo(x, y))) {
            animator.scrollTo(x, y);
            postInvalidate();
        } else {
            checkLastPage(view);
        }
    }

    /**
     * 判断是否跳到最后一页
     *
     * @param view
     */
    private void checkLastPage(ZLView view) {
        if (view instanceof ZLTextView) {
            final ZLTextWordCursor cursor = ((ZLTextView) view).getEndCursor();
            if (cursor != null && !cursor.isNull() && cursor.isEndOfText()) {
                final Context context = getContext();
                if (context instanceof FBReader) {
                    ((FBReader) context).createWakeLock();
                    ((FBReader) context).showLastPage();
                }
            }
        }
    }

    @Override
    public void startAnimatedScrolling(ZLView.PageIndex pageIndex, int x, int y, ZLView.Direction direction, int speed) {
        final ZLView view = ZLApplication.Instance().getCurrentView();
        if (pageIndex == ZLView.PageIndex.current || !view.canScroll(pageIndex)) {
            checkLastPage(view);
            return;
        }
        final AnimationProvider animator = getAnimationProvider();
        animator.setup(direction, getWidth(), getMainAreaHeight(), myColorLevel);
        animator.startAnimatedScrolling(pageIndex, x, y, speed);
        if (animator.getMode().Auto) {
            postInvalidate();
        }
    }

    @Override
    public void startAnimatedScrolling(ZLView.PageIndex pageIndex, ZLView.Direction direction, int speed) {
        final ZLView view = ZLApplication.Instance().getCurrentView();
        if (pageIndex == ZLView.PageIndex.current || !view.canScroll(pageIndex)) {
            checkLastPage(view);
            return;
        }
        final AnimationProvider animator = getAnimationProvider();
        animator.setup(direction, getWidth(), getMainAreaHeight(), myColorLevel);
        animator.startAnimatedScrolling(pageIndex, null, null, speed);
        if (animator.getMode().Auto) {
            postInvalidate();
        }
    }

    @Override
    public void startAnimatedScrolling(int x, int y, int speed) {
        final ZLView view = ZLApplication.Instance().getCurrentView();
        final AnimationProvider animator = getAnimationProvider();
        if (!view.canScroll(animator.getPageToScrollTo(x, y))) {
            animator.terminate();
            checkLastPage(view);
            return;
        }
        animator.startAnimatedScrolling(x, y, speed);
        postInvalidate();
    }
    /*
     在android程序是不直接对Bitmap类进行操作的，而是通过Canva类来对Bitmap类进行操作。
     所以这个方法中以Bitmap类为参数新建一个Canvas类。接着，代码就调用了ZLTextView类的paint方法
     */
    void drawOnBitmap(Bitmap bitmap, ZLView.PageIndex index) {
        final ZLView view = ZLApplication.Instance().getCurrentView();
        if (view == null) {
            return;
        }

        final ZLAndroidPaintContext context = new ZLAndroidPaintContext(
                mySystemInfo,
                new Canvas(bitmap),
                new ZLAndroidPaintContext.Geometry(
                        getWidth(),
                        getHeight(),
                        getWidth(),
                        getMainAreaHeight() - getHeadHeight(),
                        0,
                        getHeadHeight()
                ),
                view.isScrollbarShown() ? getVerticalScrollbarWidth() : 0
        );
        view.paint(context, index);
    }

    private void drawHead(Canvas canvas, AnimationProvider animator) {
        final ZLView view = ZLApplication.Instance().getCurrentView();
        final ZLView.HeadArea head = view.getHeadArea();

        if (head == null) {
            myHeadBitmap = null;
            return;
        }
        if (myHeadBitmap != null &&
                (myHeadBitmap.getWidth() != getWidth() ||
                        myHeadBitmap.getHeight() != head.getHeight())) {
            myHeadBitmap = null;
        }

        if (myHeadBitmap == null) {
            myHeadBitmap = Bitmap.createBitmap(getWidth(), head.getHeight(), Bitmap.Config.RGB_565);
        }
        final ZLAndroidPaintContext context = new ZLAndroidPaintContext(
                mySystemInfo,
                new Canvas(myHeadBitmap),
                new ZLAndroidPaintContext.Geometry(
                        getWidth(),
                        getHeight(),
                        getWidth(),
                        head.getHeight(),
                        0,
                        0
                ),
                view.isScrollbarShown() ? getVerticalScrollbarWidth() : 0
        );
        head.paint(context);
        final int voffset = 0;
        if (animator != null) {
            animator.drawHeadBitmap(canvas, myHeadBitmap, voffset);
        } else {
            canvas.drawBitmap(myHeadBitmap, 0, voffset, myPaint);
        }
    }

    private void drawFooter(Canvas canvas, AnimationProvider animator) {
        final ZLView view = ZLApplication.Instance().getCurrentView();
        final ZLView.FooterArea footer = view.getFooterArea();

        if (footer == null) {
            myFooterBitmap = null;
            return;
        }

        if (myFooterBitmap != null &&
                (myFooterBitmap.getWidth() != getWidth() ||
                        myFooterBitmap.getHeight() != footer.getHeight())) {
            myFooterBitmap = null;
        }
        if (myFooterBitmap == null) {
            myFooterBitmap = Bitmap.createBitmap(getWidth(), footer.getHeight(), Bitmap.Config.RGB_565);
        }
        final ZLAndroidPaintContext context = new ZLAndroidPaintContext(
                mySystemInfo,
                new Canvas(myFooterBitmap),
                new ZLAndroidPaintContext.Geometry(
                        getWidth(),
                        getHeight(),
                        getWidth(),
                        footer.getHeight(),
                        0,
                        getMainAreaHeight()
                ),
                view.isScrollbarShown() ? getVerticalScrollbarWidth() : 0
        );
        footer.paint(context);
        final int voffset = getHeight() - footer.getHeight();
        if (animator != null) {
            animator.drawFooterBitmap(canvas, myFooterBitmap, voffset);
        } else {
            canvas.drawBitmap(myFooterBitmap, 0, voffset, myPaint);
        }
    }

    /*
    onDrawStatic方法中调用了Canvas类的drawBitmap方法。drawBitmap方法会要求一个Bitmap类作为参数。
    Bitmap类对应的是一段缓存，这段缓存最终会被显示在屏幕上。代码会把屏幕上需要显示的内容写入这段内存。
     */
    private void onDrawStatic(final Canvas canvas) {
        canvas.drawBitmap(myBitmapManager.getBitmap(ZLView.PageIndex.current), 0, getHeadHeight(), myPaint);
        drawFooter(canvas, null);
        drawHead(canvas, null);
        post(new Runnable() {
            public void run() {
                PrepareService.execute(new Runnable() {
                    public void run() {
                        final ZLView view = ZLApplication.Instance().getCurrentView();
                        final ZLAndroidPaintContext context = new ZLAndroidPaintContext(
                                mySystemInfo,
                                canvas,
                                new ZLAndroidPaintContext.Geometry(
                                        getWidth(),
                                        getHeight(),
                                        getWidth(),
                                        getMainAreaHeight() - getHeadHeight(),
                                        0,
                                        getHeadHeight()
                                ),
                                view.isScrollbarShown() ? getVerticalScrollbarWidth() : 0
                        );
                        view.preparePage(context, ZLView.PageIndex.next);
                    }
                });
            }
        });
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            onKeyDown(KeyEvent.KEYCODE_DPAD_CENTER, null);
        } else {
            ZLApplication.Instance().getCurrentView().onTrackballRotated((int) (10 * event.getX()), (int) (10 * event.getY()));
        }
        return true;
    }

    private class LongClickRunnable implements Runnable {
        @Override
        public void run() {
            if (performLongClick()) {
                myLongClickPerformed = true;
            }
        }
    }

    private volatile LongClickRunnable myPendingLongClickRunnable;
    private volatile boolean myLongClickPerformed;

    private void postLongClickRunnable() {
        myLongClickPerformed = false;
        myPendingPress = false;
        if (myPendingLongClickRunnable == null) {
            myPendingLongClickRunnable = new LongClickRunnable();
        }
        postDelayed(myPendingLongClickRunnable, 2 * ViewConfiguration.getLongPressTimeout());
    }

    private class ShortClickRunnable implements Runnable {
        @Override
        public void run() {
            final ZLView view = ZLApplication.Instance().getCurrentView();
            view.onFingerSingleTap(myPressedX, myPressedY);
            myPendingPress = false;
            myPendingShortClickRunnable = null;
        }
    }

    private volatile ShortClickRunnable myPendingShortClickRunnable;

    private volatile boolean myPendingPress;
    private volatile boolean myPendingDoubleTap;
    private int myPressedX, myPressedY;
    private boolean myScreenIsTouched;
    private boolean isChangingLanguage;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if(SingleTimer.getInstance().hasExecuteDelayTask()){
//            EventBus.getDefault().post(new StartEvent());
//        }
//        SingleTimer.getInstance().delayExecute(SingleTimer.TEN_MINUTES);
        SingleTimer.canRecord = true;
        int x = (int) event.getX();
        int y = (int) event.getY() - getHeadHeight();

        final ZLView view = ZLApplication.Instance().getCurrentView();
        switch (event.getAction()) {
            case MotionEvent.ACTION_CANCEL:
                myPendingDoubleTap = false;
                myPendingPress = false;
                myScreenIsTouched = false;
                myLongClickPerformed = false;
                if (myPendingShortClickRunnable != null) {
                    removeCallbacks(myPendingShortClickRunnable);
                    myPendingShortClickRunnable = null;
                }
                if (myPendingLongClickRunnable != null) {
                    removeCallbacks(myPendingLongClickRunnable);
                    myPendingLongClickRunnable = null;
                }
                view.onFingerEventCancelled();
                break;
            case MotionEvent.ACTION_UP:
                if (myPendingDoubleTap) {
                    view.onFingerDoubleTap(x, y);
                } else if (myLongClickPerformed) {
                    view.onFingerReleaseAfterLongPress(x, y);
                } else {
                    if (myPendingLongClickRunnable != null) {
                        removeCallbacks(myPendingLongClickRunnable);
                        myPendingLongClickRunnable = null;
                    }
                    if (myPendingPress) {
                        if (view.isDoubleTapSupported()) {
                            if (myPendingShortClickRunnable == null) {
                                myPendingShortClickRunnable = new ShortClickRunnable();
                            }
                            postDelayed(myPendingShortClickRunnable, ViewConfiguration.getDoubleTapTimeout());
                        } else {

                            //关闭选择框
                            final Context context = getContext();
                            if (context instanceof FBReader) {
                                if (((FBReader) context).isSelectionPopShowing()) {
                                    ((FBReader) context).hideSelectionPanel();
                                    ZLApplication.Instance().runAction(ActionCode.SELECTION_CLEAR);
                                    return false;
                                }
                            }
                            //ljw:这里做了单击操作
                            view.onFingerSingleTap(x, y);
                        }
                    } else {
                        view.onFingerRelease(x, y);
                    }
                }
                myPendingDoubleTap = false;
                myPendingPress = false;
                myScreenIsTouched = false;
                break;
            case MotionEvent.ACTION_DOWN:
                isChangingLanguage = false;
                if (myPendingShortClickRunnable != null) {
                    removeCallbacks(myPendingShortClickRunnable);
                    myPendingShortClickRunnable = null;
                    myPendingDoubleTap = true;
                } else {
                    postLongClickRunnable();
                    myPendingPress = true;
                }
                myScreenIsTouched = true;
                myPressedX = x;
                myPressedY = y;
                break;
            case MotionEvent.ACTION_MOVE: {
                final int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                final boolean isAMove =
                        Math.abs(myPressedX - x) > slop || Math.abs(myPressedY - y) > slop;
                if (isAMove) {
                    myPendingDoubleTap = false;
                }
                if (myLongClickPerformed) {
                    view.onFingerMoveAfterLongPress(x, y);
                } else {
                    if (myPendingPress) {
                        if (isAMove) {
                            if (myPendingShortClickRunnable != null) {
                                removeCallbacks(myPendingShortClickRunnable);
                                myPendingShortClickRunnable = null;
                            }
                            if (myPendingLongClickRunnable != null) {
                                removeCallbacks(myPendingLongClickRunnable);
                            }
                            view.onFingerPress(myPressedX, myPressedY);
                            myPendingPress = false;
                        }
                    }
                    if (!myPendingPress) {
                        view.onFingerMove(x, y);
                    }
                }
                if (Math.abs(myPressedX - x) < 80 && Math.abs(myPressedY - y) > 200 && !isChangingLanguage) {
                    isChangingLanguage = true;
                    view.onChangeLanguage();
                }
                break;
            }
        }

        return true;
    }

    @Override
    public boolean onLongClick(View v) {
        final ZLView view = ZLApplication.Instance().getCurrentView();
        return view.onFingerLongPress(myPressedX, myPressedY);
    }

    private int myKeyUnderTracking = -1;
    private long myTrackingStartTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        final ZLApplication application = ZLApplication.Instance();
        final ZLKeyBindings bindings = application.keyBindings();
        if (bindings.hasBinding(keyCode, true) ||
                bindings.hasBinding(keyCode, false)) {
            if (myKeyUnderTracking != -1) {
                if (myKeyUnderTracking == keyCode) {
                    return true;
                } else {
                    myKeyUnderTracking = -1;
                }
            }
            if (bindings.hasBinding(keyCode, true)) {
                myKeyUnderTracking = keyCode;
                myTrackingStartTime = System.currentTimeMillis();
                return true;
            } else {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return false;
                }
                return application.runActionByKey(keyCode, false);
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (myKeyUnderTracking != -1) {
            if (myKeyUnderTracking == keyCode) {
                final boolean longPress = System.currentTimeMillis() >
                        myTrackingStartTime + ViewConfiguration.getLongPressTimeout();
                ZLApplication.Instance().runActionByKey(keyCode, longPress);
            }
            myKeyUnderTracking = -1;
            return true;
        } else {
            final ZLKeyBindings bindings = ZLApplication.Instance().keyBindings();
            return
                    bindings.hasBinding(keyCode, false) ||
                            bindings.hasBinding(keyCode, true);
        }
    }

    @Override
    protected int computeVerticalScrollExtent() {
        final ZLView view = ZLApplication.Instance().getCurrentView();
        if (!view.isScrollbarShown()) {
            return 0;
        }
        final AnimationProvider animator = getAnimationProvider();
        if (animator.inProgress()) {
            final int from = view.getScrollbarThumbLength(ZLView.PageIndex.current);
            final int to = view.getScrollbarThumbLength(animator.getPageToScrollTo());
            final int percent = animator.getScrolledPercent();
            return (from * (100 - percent) + to * percent) / 100;
        } else {
            return view.getScrollbarThumbLength(ZLView.PageIndex.current);
        }
    }

    @Override
    protected int computeVerticalScrollOffset() {
        final ZLView view = ZLApplication.Instance().getCurrentView();
        if (!view.isScrollbarShown()) {
            return 0;
        }
        final AnimationProvider animator = getAnimationProvider();
        if (animator.inProgress()) {
            final int from = view.getScrollbarThumbPosition(ZLView.PageIndex.current);
            final int to = view.getScrollbarThumbPosition(animator.getPageToScrollTo());
            final int percent = animator.getScrolledPercent();
            return (from * (100 - percent) + to * percent) / 100;
        } else {
            return view.getScrollbarThumbPosition(ZLView.PageIndex.current);
        }
    }

    @Override
    protected int computeVerticalScrollRange() {
        final ZLView view = ZLApplication.Instance().getCurrentView();
        if (!view.isScrollbarShown()) {
            return 0;
        }
        return view.getScrollbarFullSize();
    }

    private int getMainAreaHeight() {
        final ZLView.FooterArea footer = ZLApplication.Instance().getCurrentView().getFooterArea();
        return footer != null ? getHeight() - footer.getHeight() : getHeight();
    }

    private int getHeadHeight() {
        final ZLView.HeadArea head = ZLApplication.Instance().getCurrentView().getHeadArea();
        return head.getHeight();
    }

    @Override
    protected void updateColorLevel() {
        ViewUtil.setColorLevel(myPaint, myColorLevel);
    }
}
