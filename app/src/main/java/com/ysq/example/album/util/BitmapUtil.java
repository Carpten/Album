package com.ysq.example.album.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Author: yangshuiqiang
 * Date:2017/6/14.
 */

public class BitmapUtil {

    public static Bitmap getScaleBitmap(Bitmap bitmap, int scaleFactor) {
        float scale = 1f / scaleFactor;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap ret = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return ret;
    }


    public static Bitmap getColorBitmap(Bitmap bitmap, float[] colorArray) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap grayImg = null;
        try {
            grayImg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(grayImg);
            Paint paint = new Paint();
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.set(colorArray);
            ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
            paint.setColorFilter(colorMatrixFilter);
            canvas.drawBitmap(bitmap, 0, 0, paint);
            bitmap.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return grayImg;
    }
}
