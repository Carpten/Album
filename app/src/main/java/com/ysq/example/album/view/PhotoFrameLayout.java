package com.ysq.example.album.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Author: yangshuiqiang
 * Date:2017/7/25.
 */

public class PhotoFrameLayout extends FrameLayout implements ScaleGestureDetector.OnScaleGestureListener, GestureDetector.OnGestureListener {

    private static final float MAX_SCALE_FACTOR = 8;

    private static final float MIN_SCALE_FACTOR = 1;

    private ScaleGestureDetector mScaleGestureDetector;

    private GestureDetector mGestureDetector;

    private float mTotalScaleFactor;

    private float mFocusX, mFocusY;

    private boolean mIsScaling;

    private int mOffset;

    public PhotoFrameLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public PhotoFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PhotoFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTotalScaleFactor = 1;
        mOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        mIsScaling = false;
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), this);
        mGestureDetector = new GestureDetector(getContext(), this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean b = mScaleGestureDetector.onTouchEvent(event);
        boolean b1 = mGestureDetector.onTouchEvent(event);
        Log.i("test", "b:" + b + ",b1:" + b1);
        boolean b2 = MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN;
        return mIsScaling || b1 || b2;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        if ((mTotalScaleFactor >= MAX_SCALE_FACTOR && detector.getScaleFactor() > 1)
                || (mTotalScaleFactor <= MIN_SCALE_FACTOR && detector.getScaleFactor() < 1)
                || getChildImageView().getDrawable() == null) {
            return true;
        } else {
            int[] imageViewSize = getImageViewSize();
            int iw = imageViewSize[0];
            int ih = imageViewSize[1];
            float preTotalScaleFactor = mTotalScaleFactor;
            mTotalScaleFactor *= detector.getScaleFactor();
            if (mTotalScaleFactor >= MAX_SCALE_FACTOR) mTotalScaleFactor = MAX_SCALE_FACTOR;
            if (mTotalScaleFactor <= MIN_SCALE_FACTOR) mTotalScaleFactor = MIN_SCALE_FACTOR;
            LayoutParams layoutParams = (LayoutParams) getChildImageView().getLayoutParams();
            float realScaleFactor = mTotalScaleFactor / preTotalScaleFactor;
            if (ih * mTotalScaleFactor <= getHeight()) {
                layoutParams.topMargin = (int) ((getHeight() - getHeight() * mTotalScaleFactor) / 2);
            } else {
                layoutParams.topMargin = (int) (mFocusY
                        - (mFocusY - layoutParams.topMargin) * realScaleFactor) - 1;
                if (-layoutParams.topMargin < (getHeight() * mTotalScaleFactor - ih * mTotalScaleFactor) / 2)
                    layoutParams.topMargin = -(int) ((getHeight() * mTotalScaleFactor - ih * mTotalScaleFactor) / 2) - mOffset;
                if (-layoutParams.topMargin > (getHeight() * mTotalScaleFactor / 2 + ih * mTotalScaleFactor / 2 - getHeight()))
                    layoutParams.topMargin = -(int) (getHeight() * mTotalScaleFactor / 2 + ih * mTotalScaleFactor / 2 - getHeight()) + mOffset;
            }
            if (iw * mTotalScaleFactor <= getWidth()) {
                layoutParams.leftMargin = (int) ((getWidth() - getWidth() * mTotalScaleFactor) / 2);
            } else {
                layoutParams.leftMargin = (int) (mFocusX
                        - (mFocusX - layoutParams.leftMargin) * realScaleFactor);
                if (-layoutParams.leftMargin < (getWidth() * mTotalScaleFactor - iw * mTotalScaleFactor) / 2)
                    layoutParams.leftMargin = -(int) ((getWidth() * mTotalScaleFactor - iw * mTotalScaleFactor) / 2) - mOffset;
                if (-layoutParams.leftMargin > (getWidth() * mTotalScaleFactor / 2 + iw * mTotalScaleFactor / 2 - getWidth()))
                    layoutParams.leftMargin = -(int) (getWidth() * mTotalScaleFactor / 2 + iw * mTotalScaleFactor / 2 - getWidth()) + mOffset;

            }
            layoutParams.width = (int) (getWidth() * mTotalScaleFactor);
            layoutParams.height = (int) (getHeight() * mTotalScaleFactor);
            if (Build.VERSION.SDK_INT >= 21)
                getChildImageView().setClipBounds(null);
            getChildImageView().setLayoutParams(layoutParams);
            return true;
        }
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        mFocusX = detector.getFocusX();
        mFocusY = detector.getFocusY();
        mIsScaling = true;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        mIsScaling = false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (mIsScaling)
            return true;
        else {
            boolean ret = true;
            int[] imageViewSize = getImageViewSize();
            int iw = imageViewSize[0];
            int ih = imageViewSize[1];
            float dx = -distanceX;
            float dy = -distanceY;
            LayoutParams layoutParams = (LayoutParams) getChildImageView().getLayoutParams();
            if (iw * mTotalScaleFactor > getWidth()) {
                layoutParams.leftMargin += dx;
                if (-layoutParams.leftMargin < (getWidth() * mTotalScaleFactor - iw * mTotalScaleFactor) / 2) {
                    layoutParams.leftMargin = -(int) ((getWidth() * mTotalScaleFactor - iw * mTotalScaleFactor) / 2) - mOffset;
                    ret = false;
                } else if (-layoutParams.leftMargin > (getWidth() * mTotalScaleFactor / 2 + iw * mTotalScaleFactor / 2 - getWidth())) {
                    layoutParams.leftMargin = -(int) (getWidth() * mTotalScaleFactor / 2 + iw * mTotalScaleFactor / 2 - getWidth()) + mOffset;
                    ret = false;
                }
            }
            if (ih * mTotalScaleFactor > getHeight()) {
                layoutParams.topMargin += dy;
                if (-layoutParams.topMargin < (getHeight() * mTotalScaleFactor - ih * mTotalScaleFactor) / 2) {
                    layoutParams.topMargin = -(int) ((getHeight() * mTotalScaleFactor - ih * mTotalScaleFactor) / 2) - mOffset;
                    ret = false;
                } else if (-layoutParams.topMargin > (getHeight() * mTotalScaleFactor / 2 + ih * mTotalScaleFactor / 2 - getHeight())) {
                    layoutParams.topMargin = -(int) (getHeight() * mTotalScaleFactor / 2 + ih * mTotalScaleFactor / 2 - getHeight()) + mOffset;
                    ret = false;
                }
            }
            getChildImageView().setLayoutParams(layoutParams);
            return ret;
        }
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }


    private int[] getImageViewSize() {
        Drawable drawable = getChildImageView().getDrawable();
        int dh = drawable.getBounds().height();
        int dw = drawable.getBounds().width();
        int iw, ih;
        if (dh * getWidth() > dw * getHeight()) {
            iw = dw * getHeight() / dh;
            ih = getHeight();
        } else {
            iw = getWidth();
            ih = dh * getWidth() / dw;
        }
        return new int[]{iw, ih};
    }

    private ImageView getChildImageView() {
        return (ImageView) getChildAt(0);
    }
}