package com.iyangcong.reader.ui.galleryflow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.iyangcong.reader.activity.BookMarketBookListActivity;
import com.iyangcong.reader.bean.BookClassify;

import java.util.List;

public class ImageAdapter extends BaseAdapter {
    int mGalleryItemBackground;
    private Context mContext;
    private ImageView[] mImages;
    private List<BookClassify.CommonBook> commonBookList;

    public ImageAdapter(Context c, ImageView[] mImages, List<BookClassify.CommonBook> commonBookList) {
        mContext = c;
        this.mImages = mImages;
        this.commonBookList = commonBookList;
    }

    public void createReflectedImages() {
        final int reflectionGap = 4;
        int index = 0;
        for (ImageView img : mImages) {
            Bitmap originalImage = ((BitmapDrawable) img.getDrawable()).getBitmap();
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();
            Matrix matrix = new Matrix();
            matrix.preScale(1, -1);
            Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
                    height / 2, width, height / 2, matrix, false);
            Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
                    (height + height / 2), Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmapWithReflection);
            canvas.drawBitmap(originalImage, 0, 0, null);
            Paint defaultPaint = new Paint();
            canvas.drawRect(0, height, width, height + reflectionGap,
                    defaultPaint);
            canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
            Paint paint = new Paint();
            LinearGradient shader = new LinearGradient(0,
                    originalImage.getHeight(), 0,
                    bitmapWithReflection.getHeight() + reflectionGap,
                    0x70ffffff, 0x00ffffff, TileMode.CLAMP);
            paint.setShader(shader);
            paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
            canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
                    + reflectionGap, paint);
            ImageView imageView = new ImageView(mContext);
            imageView.setImageBitmap(bitmapWithReflection);
            imageView.setLayoutParams(new GalleryFlow.LayoutParams(160, 240));
            mImages[index++] = imageView;
        }

    }

    public int getCount() {
        return mImages.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        return mImages[position];
    }


    public float getScale(boolean focused, int offset) {
        return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
    }

}
