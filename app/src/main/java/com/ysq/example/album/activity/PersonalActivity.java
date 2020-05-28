package com.ysq.example.album.activity;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.ysq.album.activity.AlbumActivity;
import com.ysq.example.album.R;
import com.ysq.example.album.util.BitmapUtil;

import net.qiujuer.genius.blur.StackBlur;

import java.io.File;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PersonalActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.iv_portrait)
    ImageView mIvPortrait;

    @BindView(R.id.iv_head_background)
    ImageView mIvHeadBackground;

    @BindView(R.id.scrollview)
    ObservableScrollView mObservableScrollView;

    @BindView(R.id.iv_toolbar)
    ImageView mIvToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.getChildAt(0).setAlpha(0);
        mToolbar.getChildAt(0).setScaleX(0.8f);
        mObservableScrollView.setScrollViewCallbacks(mObservableScrollViewCallbacks);
        Glide.with(PersonalActivity.this).load(R.mipmap.ic_default_portrait).asBitmap().placeholder(R.drawable.ic_placeholder)
                .listener(mGlideRequestListener).centerCrop().into(mIvPortrait);
    }

    private RequestListener<Object, Bitmap> mGlideRequestListener = new RequestListener<Object, Bitmap>() {
        @Override
        public boolean onException(Exception e, Object model, Target<Bitmap> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
            Bitmap blurBitmap = resource.copy(resource.getConfig(), true);
            float[] colorArray = {0.8f, 0, 0, 0, 0, 0, 0.8f, 0, 0, 0, 0, 0, 0.8f, 0, 0, 0, 0, 0, 1, 0};
            mIvHeadBackground.setImageBitmap(BitmapUtil.getColorBitmap(StackBlur.blurNatively(BitmapUtil.getScaleBitmap(blurBitmap, 8), 6, true), colorArray));
            mIvHeadBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mIvHeadBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    mIvHeadBackground.buildDrawingCache();
                    Bitmap bitmap = mIvHeadBackground.getDrawingCache();
                    setToolbarMatrix(mPreScroll);
                    mIvToolbar.setImageBitmap(bitmap.copy(bitmap.getConfig(), true));
                    return true;
                }
            });
            return false;
        }
    };

    private ObservableScrollViewCallbacks mObservableScrollViewCallbacks = new ObservableScrollViewCallbacks() {
        @Override
        public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
            setToolbarMatrix(scrollY);
        }

        @Override
        public void onDownMotionEvent() {

        }

        @Override
        public void onUpOrCancelMotionEvent(ScrollState scrollState) {

        }
    };

    @BindDimen(R.dimen.personal_toolbar_expand_distance)
    int mToolbarExpandDistance;

    private int mPreScroll;

    private void setToolbarMatrix(int scroll) {
        if (scroll > mToolbarExpandDistance && mPreScroll > mToolbarExpandDistance) {
            mPreScroll = scroll;
            return;
        }
        mPreScroll = scroll;
        if (scroll >= mToolbarExpandDistance) {
            animateToolbar(true);
            scroll = mToolbarExpandDistance;
        } else {
            animateToolbar(false);
        }
        Matrix matrix = new Matrix();
        matrix.setTranslate(0, -scroll);
        mIvToolbar.setImageMatrix(matrix);
        float alpha = ((float) scroll) / mToolbarExpandDistance;
        mIvToolbar.setAlpha(alpha);
    }

    private boolean mTitleVisible;

    private void animateToolbar(boolean visible) {
        if (mTitleVisible == visible) return;
        mTitleVisible = visible;
        if (visible) {
            View t = mToolbar.getChildAt(0);
            if (t != null && t instanceof TextView) {
                TextView title = (TextView) t;
                title.animate().cancel();
                title.animate()
                        .alpha(1f)
                        .scaleX(1f)
                        .setListener(null)
                        .setDuration(900)
                        .setInterpolator(new FastOutSlowInInterpolator());
            }
        } else {
            View t = mToolbar.getChildAt(0);
            if (t != null && t instanceof TextView) {
                final TextView title = (TextView) t;
                title.animate().cancel();
                title.animate()
                        .alpha(0f)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                title.setScaleX(0.8f);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        })
                        .setDuration(600)
                        .setInterpolator(new FastOutSlowInInterpolator());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_person_center, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            Intent intent = new Intent(PersonalActivity.this, AlbumActivity.class);
            intent.putExtra(AlbumActivity.ARG_MODE, AlbumActivity.MODE_PORTRAIT);
            startActivityForResult(intent, 1001);
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String path = data.getStringExtra(AlbumActivity.ARG_PATH);
            Glide.with(PersonalActivity.this).load(new File(path)).asBitmap().skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE).listener(mGlideRequestListener)
                    .centerCrop().into(mIvPortrait);
        }
    }
}