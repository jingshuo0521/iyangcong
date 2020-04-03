package com.iyangcong.reader.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.iyangcong.reader.R;

/**
 * Created by WuZepeng on 2017-03-23.
 * 用户Glide切圆角阴影图片
 */

public class GlideRoundShadowTransform extends BitmapTransformation {

	private static float radius = 0f;
	private static Context mContext;
	public GlideRoundShadowTransform(Context context , int dp) {
		super(context);
		this.radius = Resources.getSystem().getDisplayMetrics().density * dp;
		this.mContext = context;
	}

	public GlideRoundShadowTransform(Context context) {
		this(context,3);
	}

	@Override
	protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
		return roundCrop(pool,toTransform);
	}

	private static Bitmap roundCrop(BitmapPool pool, Bitmap source) {
		if (source == null) return null;

		Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
		if (result == null) {
			result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
		}

		Canvas canvas = new Canvas(result);
		Paint paint = new Paint();
		paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
		paint.setAntiAlias(true);
		RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
		canvas.drawRoundRect(rectF, radius, radius, paint);
		//if(hasShadow) {
			Bitmap fg = ((BitmapDrawable) mContext.getResources().getDrawable(R.drawable.book_shadow)).getBitmap();
			Paint paint1 = new Paint();
			paint1.setShader(new BitmapShader(fg, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
			paint1.setAntiAlias(true);
			RectF rectF1 = new RectF(0f, 0f, fg.getWidth(), source.getHeight());
			canvas.drawRoundRect(rectF1, radius, radius, paint1);
		//}
//		canvas.drawBitmap(fg,0,0,null);
		return result;
	}

	@Override
	public String getId() {
		return getClass().getName() + Math.round(radius);
	}
}
