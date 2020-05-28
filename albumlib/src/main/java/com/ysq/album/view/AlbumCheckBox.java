package com.ysq.album.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.ysq.album.activity.AlbumActivity;

/**
 * Author: yangshuiqiang
 * Date:2017/7/4.
 */

public class AlbumCheckBox extends androidx.appcompat.widget.AppCompatCheckBox {


    private OnCannotCheckMoreListener mOnCannotCheckMoreListener;

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
        if (AlbumActivity.albumPicker.getCurrentCount() >= AlbumActivity.albumPicker.getMaxCount() && !isChecked()) {
            if (event.getAction() == MotionEvent.ACTION_UP && mOnCannotCheckMoreListener != null)
                mOnCannotCheckMoreListener.OnCannotCheckMore();
            return true;
        } else
            return super.onTouchEvent(event);
    }

    public void setOnCannotCheckMoreListener(OnCannotCheckMoreListener onCannotCheckMoreListener) {
        mOnCannotCheckMoreListener = onCannotCheckMoreListener;
    }

    public OnCannotCheckMoreListener getOnCannotCheckMoreListener() {
        return mOnCannotCheckMoreListener;
    }

    public interface OnCannotCheckMoreListener {
        void OnCannotCheckMore();
    }
}
