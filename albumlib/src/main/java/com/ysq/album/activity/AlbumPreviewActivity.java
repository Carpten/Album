package com.ysq.album.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ysq.album.R;
import com.ysq.album.bean.ImageBean;

import java.util.List;
import java.util.Map;

/**
 * Author: yangshuiqiang
 * Date:2017/6/15.
 */


public class AlbumPreviewActivity extends AppCompatActivity {

    public static final String ARG_BUCKET_INDEX = "ARG_BUCKET_INDEX";

    public static final String ARG_INDEX = "ARG_INDEX";

    private static final int DEFAULT_SAMPLE_SIZE = 2;

    private List<ImageBean> mImageBeen;

    private int mBucketIndex, mIndex;

    private int mActivityWidth, mActivityHeight;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
        }
        setContentView(R.layout.ysq_activity_album_preview);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mActivityWidth = dm.widthPixels;
        mActivityHeight = dm.heightPixels;
        mBucketIndex = getIntent().getIntExtra(ARG_BUCKET_INDEX, 0);
        mIndex = getIntent().getIntExtra(ARG_INDEX, 0);
        mImageBeen = AlbumActivity.albumPicker.getBuckets().get(mBucketIndex).getImageBeen();
        setupViewPager();
    }


    private void setupViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mPreviewAdapter);
        mViewPager.setCurrentItem(mIndex);
    }

    private PagerAdapter mPreviewAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return AlbumActivity.albumPicker.getBuckets().get(mBucketIndex).getImageBeen().size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final ImageView imageview = new ImageView(AlbumPreviewActivity.this);
            imageview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
            String transitionName = container.getContext().getString(R.string.ysq_transition_name, position);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageview.setTransitionName(transitionName);
            }
            imageview.setTag(position);

            Glide.with(AlbumPreviewActivity.this).load(mImageBeen.get(position).getImage_path()).asBitmap()
                    .override(mActivityWidth, mActivityHeight)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            imageview.setImageBitmap(resource);
                            if (position == mIndex)
                                setStartPostTransition(imageview);
                        }
                    });

            container.addView(imageview);
            return imageview;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    };


    @Override
    public void finishAfterTransition() {
        int currentItem = mViewPager.getCurrentItem();
        Intent intent = new Intent();
        intent.putExtra(ARG_INDEX, currentItem);
        setResult(RESULT_OK, intent);
        View view = mViewPager.findViewWithTag(currentItem);
        setSharedElementCallback(view);
        super.finishAfterTransition();
    }

    @TargetApi(21)
    private void setSharedElementCallback(final View view) {
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                names.clear();
                sharedElements.clear();
                names.add(view.getTransitionName());
                sharedElements.put(view.getTransitionName(), view);
            }
        });
    }


    @TargetApi(21)
    private void setStartPostTransition(final View view) {
        view.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        view.getViewTreeObserver().removeOnPreDrawListener(this);
                        startPostponedEnterTransition();
                        return false;
                    }
                });
    }


    private Bitmap getThumbnailPic(int position) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mImageBeen.get(position).getImage_path(), options);
        int picW = options.outWidth;
        int picH = options.outHeight;
        int inSampleSize = DEFAULT_SAMPLE_SIZE;
        while (true) {
            float widthRatio = (picW * DEFAULT_SAMPLE_SIZE) / (mActivityWidth * inSampleSize);
            float heightRatio = (picH * DEFAULT_SAMPLE_SIZE) / (mActivityHeight * inSampleSize);
            if (widthRatio > 1 || heightRatio > 1) {
                inSampleSize *= 2;
            } else {
                break;
            }
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        return BitmapFactory.decodeFile(mImageBeen.get(position).getImage_path(), options);
    }

}
