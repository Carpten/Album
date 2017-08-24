package com.ysq.example.album.transition;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.transition.ChangeBounds;
import android.transition.TransitionValues;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ysq.example.album.R;


/**
 * Author: yangshuiqiang
 * Date:2017/6/20.
 */

@RequiresApi(api = Build.VERSION_CODES.KITKAT)

public class AlbumEnterV21Transition extends ChangeBounds {

    private int mOffset = 2;

    private static final String PROPNAME_BOUNDS = "android:changeBounds:bounds";

    private int mPicW, mPicH;

    private Context mContext;

    private int mIndex;

    public AlbumEnterV21Transition(Context context, int picW, int picH, int index) {
        mPicW = picW;
        mPicH = picH;
        mIndex = index;
        mContext = context;
    }


    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        super.captureStartValues(transitionValues);
        Rect bounds = (Rect) transitionValues.values.get(PROPNAME_BOUNDS);
        if (mPicW >= mPicH) {
            int h = bounds.bottom - bounds.top;
            int w = h * mPicW / mPicH;
            if (w * mPicH == h * mPicW) mOffset = 0;
            int margin = (w - h) / 2;
            bounds.left -= margin;
            bounds.right += margin + mOffset;
        } else {
            int w = bounds.right - bounds.left;
            int h = w * mPicH / mPicW;
            if (w * mPicH == h * mPicW) mOffset = 0;
            int margin = (h - w) / 2;
            bounds.top -= margin;
            bounds.bottom += margin + mOffset;
        }
        transitionValues.values.put(PROPNAME_BOUNDS, bounds);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, final TransitionValues endValues) {
        final ImageView imageView = (ImageView) endValues.view;
        final Rect startBounds = (Rect) startValues.values.get(PROPNAME_BOUNDS);
        final Rect endBounds = (Rect) endValues.values.get(PROPNAME_BOUNDS);
        final int startLeft = startBounds.left;
        final int endLeft = endBounds.left;
        final int startTop = startBounds.top;
        final int endTop = endBounds.top;
        final int startRight = startBounds.right;
        final int endRight = endBounds.right;
        final int startBottom = startBounds.bottom;
        final int endBottom = endBounds.bottom;
        invalidateDrawable(imageView, startLeft, startRight, startTop, startBottom);
        final Rect startClipRect;
        if (mPicW >= mPicH) {
            int margin = (startBounds.right - startBounds.left - (startBounds.bottom - startBounds.top)) / 2;
            startClipRect = new Rect(margin, 0, startBounds.right - startBounds.left - margin - mOffset, startBounds.bottom - startBounds.top);
        } else {
            int margin = (startBounds.bottom - startBounds.top - (startBounds.right - startBounds.left)) / 2;
            startClipRect = new Rect(0, margin, startBounds.right - startBounds.left, startBounds.bottom - startBounds.top - margin - mOffset);
        }
        final Rect endClipRect = new Rect(0, 0, endBounds.right - endBounds.left, endBounds.bottom - endBounds.top);
        final ValueAnimator valueAnimator = ObjectAnimator.ofFloat(0, 1).setDuration(mContext.getResources().getInteger(R.integer.scene_duration_in));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int leftClip = (int) ((endClipRect.left - startClipRect.left) * (float) animation.getAnimatedValue() + startClipRect.left);
                int rightClip = (int) ((endClipRect.right - startClipRect.right) * (float) animation.getAnimatedValue() + startClipRect.right);
                int topClip = (int) ((endClipRect.top - startClipRect.top) * (float) animation.getAnimatedValue() + startClipRect.top);
                int bottomClip = (int) ((endClipRect.bottom - startClipRect.bottom) * (float) animation.getAnimatedValue() + startClipRect.bottom);
                int left = (int) ((endLeft - startLeft) * (float) animation.getAnimatedValue() + startLeft);
                int right = (int) ((endRight - startRight) * (float) animation.getAnimatedValue() + startRight);
                int top = (int) ((endTop - startTop) * (float) animation.getAnimatedValue() + startTop);
                int bottom = (int) ((endBottom - startBottom) * (float) animation.getAnimatedValue() + startBottom);
                invalidateDrawable(imageView, left, right, top, bottom);
                endValues.view.setClipBounds(new Rect(leftClip, topClip, rightClip, bottomClip));
            }
        });
        return valueAnimator;
    }

    private void invalidateDrawable(ImageView imageView, int left, int right, int top, int bottom) {
        imageView.setLeft(left);
        imageView.setRight(right);
        imageView.setTop(top);
        imageView.setBottom(bottom);
        //noinspection ConstantConditions
        imageView.setImageDrawable(imageView.getDrawable().getConstantState().newDrawable());
    }
}