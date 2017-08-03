package com.ysq.example.album.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


/**
 * author:ysq
 * date:2017/2/16.
 */

public class PreviewViewPager extends ViewPager {

    private boolean mScrollEnable = true;

    private int mState = ViewPager.SCROLL_STATE_IDLE;

    private NextIdleListener mNextIdleListener;

    public PreviewViewPager(Context context) {
        super(context);
        init();
    }

    public PreviewViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        addOnPageChangeListener(mOnPageChangeListener);
    }

    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mState = state;
            if (state == SCROLL_STATE_IDLE && mNextIdleListener != null) {
                mNextIdleListener.onNextIdle();
                mNextIdleListener = null;
            }
        }
    };

    public boolean isIdle() {
        return mState == ViewPager.SCROLL_STATE_IDLE;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        setPadding(getPaddingLeft(), 0, getPaddingRight(), getPaddingBottom());
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mScrollEnable && super.onTouchEvent(ev);
    }

    public void setScrollEnable(boolean enable) {
        mScrollEnable = enable;
    }

    public void setNextIdleListener(NextIdleListener nextIdleListener) {
        mNextIdleListener = nextIdleListener;
    }

    public interface NextIdleListener {
        void onNextIdle();
    }
}
