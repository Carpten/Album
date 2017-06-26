package com.ysq.album.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;


/**
 * 作者:ysq
 * 时间:2017/2/16.
 */

public class FitSystemViewPager extends ViewPager {
    public FitSystemViewPager(Context context) {
        super(context);
    }

    public FitSystemViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        setPadding(getPaddingLeft(), 0, getPaddingRight(), getPaddingBottom());
        super.onLayout(changed, l, t, r, b);
    }
}
