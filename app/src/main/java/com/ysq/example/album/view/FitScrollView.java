package com.ysq.example.album.view;

import android.content.Context;
import android.util.AttributeSet;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;

/**
 * author:ysq
 * date:2017/2/16.
 */

public class FitScrollView extends ObservableScrollView {
    public FitScrollView(Context context) {
        super(context);
    }

    public FitScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FitScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        setPadding(getPaddingLeft(), 0, getPaddingRight(), getPaddingBottom());
        super.onLayout(changed, l, t, r, b);
    }
}
