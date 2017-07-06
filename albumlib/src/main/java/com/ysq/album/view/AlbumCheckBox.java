package com.ysq.album.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Author: yangshuiqiang
 * Date:2017/7/4.
 */

public class AlbumCheckBox extends android.support.v7.widget.AppCompatCheckBox {

    private boolean mCheckEnable = true;

    public AlbumCheckBox(Context context) {
        super(context);
    }

    public AlbumCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlbumCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return !mCheckEnable || super.onTouchEvent(event);
    }

    public void setCheckEnable(boolean checkEnable) {
        mCheckEnable = checkEnable;
    }


}
