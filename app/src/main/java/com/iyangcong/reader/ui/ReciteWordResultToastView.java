package com.iyangcong.reader.ui;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.RelativeLayout;

public class ReciteWordResultToastView {

	public static String TAG = "OpenFolder";
	/**
	 * folder animaltion execution time
	 */
	private static int ANIMALTION_TIME = 300;

	private Context mContext;
	private WindowManager mWindowManager;
	private boolean mWindowIsAdd = false;
	private int mWindowLayoutType = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
	private int mInputModeType = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
	private OpenFolderContainer container;
	private View mBackgroundView;
	private View mFolderView;

	private int mSrceenwidth;
	private int mSrceenheigh;

	/**
	 * Listener that is called when this OpenFolder window is closed.
	 */
	public interface OnFolderClosedListener {
		/**
		 * Called when this OpenFolder window is closed.
		 */
		public void onClosed();

		public void onOpened();
	}

	private OnFolderClosedListener mOnFolderClosedListener;

	/**
	 * the folder open Animation Listener
	 */
	private Animation.AnimationListener mOpenAnimationListener = new Animation.AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			mOnFolderClosedListener.onOpened();
		}
	};

	/**
	 * the folder colse Animation Listener
	 */
	private Animation.AnimationListener mClosedAnimationListener = new Animation.AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			container.post(new Runnable() {

				@Override
				public void run() {
					container.removeAllViews();
				}
			});

			mWindowManager.removeView(container);
			mWindowIsAdd = false;
			// 清空画图缓存区，否则获取的还是原来的图像
			mBackgroundView.setDrawingCacheEnabled(false);
			if (mOnFolderClosedListener != null) {
				mOnFolderClosedListener.onClosed();
			}
		}
	};

	private static ReciteWordResultToastView openFolder;


	public ReciteWordResultToastView(Context context) {

		mContext = context;
		mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		container = new OpenFolderContainer(mContext);
	}

	public void ToastView(View backgroundView, View folderView) {
		mBackgroundView = backgroundView;
		mFolderView = folderView;
		mSrceenwidth = backgroundView.getWidth();
		mSrceenheigh = backgroundView.getHeight();
		prepareLayout();
		startOpenAnimation();
		mFolderView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		mFolderView.invalidate();
	}

	private void prepareLayout() {

		if (mWindowIsAdd) {
			Log.e(TAG,
					"container view has already been added to the window manager!!!");
			return;
		}
		container.removeAllViews();
		container.addView(mFolderView);
		if (!mWindowIsAdd) {
			mWindowManager.addView(container,
					createPopupLayout(mBackgroundView.getWindowToken()));
			mWindowIsAdd = true;
		}

	}

	private void startOpenAnimation() {
		AnimationSet animationSet = new AnimationSet(true);
		// 创建一个AlphaAnimation对象，参数从完全的透明度，到完全的不透明
		AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
		// 设置动画执行的时间
		alphaAnimation.setDuration(500);
		// 将alphaAnimation对象添加到AnimationSet当中
		animationSet.addAnimation(alphaAnimation);
		// 使用ImageView的startAnimation方法执行动画
		animationSet.setAnimationListener(mOpenAnimationListener);
		mFolderView.startAnimation(animationSet);

	}

	private WindowManager.LayoutParams createPopupLayout(IBinder token) {
		WindowManager.LayoutParams p = new WindowManager.LayoutParams();
		p.gravity = Gravity.LEFT | Gravity.TOP;
		p.width = mSrceenwidth;
		p.height = mSrceenheigh;
		p.format = PixelFormat.OPAQUE;
		p.token = token;
		p.type = mWindowLayoutType;
		p.setTitle("OpenFolder:" + Integer.toHexString(hashCode()));
		p.softInputMode = mInputModeType;
		return p;
	}

	/**
	 * Sets the listener to be called when the openFolder is colsed.
	 * 
	 */
	public void setmOnFolderClosedListener(
			OnFolderClosedListener onFolderClosedListener) {
		this.mOnFolderClosedListener = onFolderClosedListener;
	}

	/**
	 * colse the folder
	 */
	public void dismiss() {
		AnimationSet animationSet = new AnimationSet(true);
		// 创建一个AlphaAnimation对象，参数从完全的透明度，到完全的不透明
		AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
		// 设置动画执行的时间
		alphaAnimation.setDuration(500);
		// 将alphaAnimation对象添加到AnimationSet当中
		animationSet.addAnimation(alphaAnimation);
		// 使用ImageView的startAnimation方法执行动画
		animationSet.setAnimationListener(mClosedAnimationListener);
		mFolderView.startAnimation(animationSet);
		mFolderView.bringToFront();
		// 关闭动画

	}

	private class OpenFolderContainer extends RelativeLayout {

		public OpenFolderContainer(Context context) {
			super(context);
		}

	}
	
	public void mydismiss(){
		container.removeAllViews();
		mWindowManager.removeView(container);
		mWindowIsAdd = false;
		// 清空画图缓存区，否则获取的还是原来的图像
		mBackgroundView.setDrawingCacheEnabled(false);
		if (mOnFolderClosedListener != null) {
			mOnFolderClosedListener.onClosed();
		}
	}

}
