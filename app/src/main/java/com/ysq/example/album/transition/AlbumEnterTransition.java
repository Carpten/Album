package com.ysq.example.album.transition;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.transition.ChangeBounds;
import android.transition.TransitionValues;
import android.view.ViewGroup;

import com.ysq.example.album.R;


/**
 * Author: yangshuiqiang
 * Date:2017/6/20.
 */

@RequiresApi(api = Build.VERSION_CODES.KITKAT)

public class AlbumEnterTransition extends ChangeBounds {

    //偏移一个像素
    private int mOffset = 2;

    private static final String PROPNAME_BOUNDS = "android:changeBounds:bounds";

    private int mPicW, mPicH;

    private Context mContext;

    public AlbumEnterTransition(Context context, int picW, int picH) {
        mPicW = picW;
        mPicH = picH;
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

    // TODO: 2017/6/21 toolbar区域clip
    @Override
    public Animator createAnimator(final ViewGroup sceneRoot, final TransitionValues startValues, TransitionValues endValues) {
        Animator animator = super.createAnimator(sceneRoot, startValues, endValues);
        final Rect startBounds = (Rect) startValues.values.get(PROPNAME_BOUNDS);
        final Rect endBounds = (Rect) endValues.values.get(PROPNAME_BOUNDS);
        final Rect startClipRect;
        if (mPicW >= mPicH) {
            int margin = (startBounds.right - startBounds.left - (startBounds.bottom - startBounds.top)) / 2;
            startClipRect = new Rect(margin, 0, startBounds.right - startBounds.left - margin - mOffset, startBounds.bottom - startBounds.top);
        } else {
            int margin = (startBounds.bottom - startBounds.top - (startBounds.right - startBounds.left)) / 2;
            startClipRect = new Rect(0, margin, startBounds.right - startBounds.left, startBounds.bottom - startBounds.top - margin - mOffset);
        }
        final Rect endClipRect = new Rect(0, 0, endBounds.right - endBounds.left, endBounds.bottom - endBounds.top);
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(0, 1).setDuration(mContext.getResources().getInteger(R.integer.scene_duration_in));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int left = (int) ((endClipRect.left - startClipRect.left) * (float) animation.getAnimatedValue() + startClipRect.left);
                int right = (int) ((endClipRect.right - startClipRect.right) * (float) animation.getAnimatedValue() + startClipRect.right);
                int top = (int) ((endClipRect.top - startClipRect.top) * (float) animation.getAnimatedValue() + startClipRect.top);
                int bottom = (int) ((endClipRect.bottom - startClipRect.bottom) * (float) animation.getAnimatedValue() + startClipRect.bottom);
                startValues.view.setClipBounds(new Rect(left, top, right, bottom));
            }
        });
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animator).with(valueAnimator);
        return animatorSet;
    }
}
