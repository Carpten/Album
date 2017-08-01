package com.ysq.example.album.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Scroller;

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
        mIsScaling = false;
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), this);
        mGestureDetector = new GestureDetector(getContext(), this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);
        return true;
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
                adjustVeticalBounds();
            }
            if (iw * mTotalScaleFactor <= getWidth()) {
                layoutParams.leftMargin = (int) ((getWidth() - getWidth() * mTotalScaleFactor) / 2);
            } else {
                layoutParams.leftMargin = (int) (mFocusX
                        - (mFocusX - layoutParams.leftMargin) * realScaleFactor);
                adjustHorizonBounds();
            }
            layoutParams.width = (int) (getWidth() * mTotalScaleFactor);
            layoutParams.height = (int) (getHeight() * mTotalScaleFactor);
            getChildImageView().setLayoutParams(layoutParams);
            if (Build.VERSION.SDK_INT >= 21)
                getChildImageView().setClipBounds(null);
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
        getParent().requestDisallowInterceptTouchEvent(true);
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
        if (mIsScaling) {
            return true;
        } else {
            int[] imageViewSize = getImageViewSize();
            int iw = imageViewSize[0];
            int ih = imageViewSize[1];
            float dx = -distanceX;
            float dy = -distanceY;
            LayoutParams layoutParams = (LayoutParams) getChildImageView().getLayoutParams();
            if (iw * mTotalScaleFactor > getWidth()) {
                layoutParams.leftMargin += dx;
                if (adjustHorizonBounds()) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    MotionEvent obtain = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN
                            , e2.getX() - distanceX, e2.getY() - distanceY, 0);
                    ((View) getParent()).onTouchEvent(obtain);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
            } else {
                getParent().requestDisallowInterceptTouchEvent(false);
            }
            if (ih * mTotalScaleFactor > getHeight()) {
                layoutParams.topMargin += dy;
                adjustVeticalBounds();
            }
            getChildImageView().setLayoutParams(layoutParams);
            return true;
        }
    }

    private boolean adjustHorizonBounds() {
        int[] imageViewSize = getImageViewSize();
        int iw = imageViewSize[0];
        LayoutParams layoutParams = (LayoutParams) getChildImageView().getLayoutParams();
        if (-layoutParams.leftMargin < (getWidth() * mTotalScaleFactor - iw * mTotalScaleFactor) / 2) {
            layoutParams.leftMargin = -(int) ((getWidth() * mTotalScaleFactor - iw * mTotalScaleFactor) / 2);
            return false;
        } else if (-layoutParams.leftMargin > (getWidth() * mTotalScaleFactor / 2 + iw * mTotalScaleFactor / 2 - getWidth())) {
            layoutParams.leftMargin = -(int) (getWidth() * mTotalScaleFactor / 2 + iw * mTotalScaleFactor / 2 - getWidth());
            return false;
        } else
            return true;
    }


    private void adjustVeticalBounds() {
        int[] imageViewSize = getImageViewSize();
        int ih = imageViewSize[1];
        LayoutParams layoutParams = (LayoutParams) getChildImageView().getLayoutParams();
        if (-layoutParams.topMargin < (getHeight() * mTotalScaleFactor - ih * mTotalScaleFactor) / 2) {
            layoutParams.topMargin = -(int) ((getHeight() * mTotalScaleFactor - ih * mTotalScaleFactor) / 2);
        } else if (-layoutParams.topMargin > (getHeight() * mTotalScaleFactor / 2 + ih * mTotalScaleFactor / 2 - getHeight())) {
            layoutParams.topMargin = -(int) (getHeight() * mTotalScaleFactor / 2 + ih * mTotalScaleFactor / 2 - getHeight());
        }
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (mIsScaling)
            return true;
        else {
//            new FlingRunnable((int) e2.getX(), (int) e2.getY(), (int) velocityX, (int) velocityY).run();
            return false;
        }
    }

    private class FlingRunnable implements Runnable {

        private Scroller mScroller;

        private int lastX, lastY;

        public FlingRunnable(int startX, int startY, int velocityX, int velocityY) {
            mScroller = new Scroller(getContext());
            lastX = startX;
            lastY = startY;
            int[] imageViewSize = getImageViewSize();
            int iw = imageViewSize[0];
            int ih = imageViewSize[1];
            LayoutParams layoutParams = (LayoutParams) getChildImageView().getLayoutParams();
            int maxY = startY - (int) ((getHeight() * mTotalScaleFactor - ih * mTotalScaleFactor) / 2 + layoutParams.topMargin);
            int minY = startY - (int) (getHeight() * mTotalScaleFactor / 2 + ih * mTotalScaleFactor / 2 - getHeight() + layoutParams.topMargin);
            int maxX = startX - (int) ((getWidth() * mTotalScaleFactor - iw * mTotalScaleFactor) / 2 + layoutParams.leftMargin);
            int minX = startX - (int) (getWidth() * mTotalScaleFactor / 2 + iw * mTotalScaleFactor / 2 - getWidth() + layoutParams.leftMargin);
            mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
        }

        @Override
        public void run() {
            while (true) {
                if (mScroller.isFinished()) return;
                try {
                    Thread.sleep(SIXTY_FPS_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mScroller.computeScrollOffset();
            }
        }
    }

    private static final int SIXTY_FPS_INTERVAL = 1000 / 60;


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