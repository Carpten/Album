package com.ysq.album.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ysq.album.R;
import com.ysq.album.adapter.AlbumAdapter0;
import com.ysq.album.bean.ImageBean;
import com.ysq.album.transition.AlbumEnterTransition;
import com.ysq.album.transition.SimpleTransitionListener;
import com.ysq.album.view.PreviewViewPager;

import java.util.List;
import java.util.Map;

/**
 * Author: yangshuiqiang
 * Date:2017/6/15.
 */


public class AlbumPreviewActivity extends AppCompatActivity {

    public static final String ARG_BUCKET_INDEX = "ARG_BUCKET_INDEX";

    public static final String ARG_INDEX = "ARG_INDEX";

    private List<ImageBean> mImageBeen;

    private int mBucketIndex, mIndex;

    private PreviewViewPager mViewPager;

    private boolean mIniting = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            postponeEnterTransition();
        }
        setContentView(R.layout.ysq_activity_album_preview);
        mBucketIndex = getIntent().getIntExtra(ARG_BUCKET_INDEX, 0);
        mIndex = getIntent().getIntExtra(ARG_INDEX, 0);
        mImageBeen = AlbumActivity.albumPicker.getBuckets().get(mBucketIndex).getImageBeen();
        setupViewPager();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mImageBeen.get(mIndex).getImage_path(), options);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setSharedElementEnterTransition(new AlbumEnterTransition(AlbumPreviewActivity.this, options.outWidth, options.outHeight)
                    .addTarget(getString(R.string.ysq_transition_name, mIndex)).setDuration(getResources().getInteger(R.integer.album_scene_duration_in)).addListener(new SimpleTransitionListener() {
                        @Override
                        public void onTransitionEnd(Transition transition) {
                            mIniting = false;
                            mViewPager.setScrollEnable(true);
                        }
                    }));
        }
    }


    private void setupViewPager() {
        mViewPager = (PreviewViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mPreviewAdapter);
        mViewPager.setCurrentItem(mIndex);
        mViewPager.setScrollEnable(false);
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
            ImageView imageview = new ImageView(AlbumPreviewActivity.this);
            imageview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageview.setTag(R.id.tag_position, position);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageview.setTransitionName(container.getContext().getString(R.string.ysq_transition_name, position));
                if (position == mIndex && mIniting) {
                    setStartPostTransition(imageview);
                }
            }
            if (mIniting && position == mIndex) {
                Glide.with(AlbumPreviewActivity.this).load(mImageBeen.get(position).getImage_path())
                        .placeholder(AlbumAdapter0.drawable).dontAnimate().fitCenter().into(imageview);
            } else
                Glide.with(AlbumPreviewActivity.this).load(mImageBeen.get(position).getImage_path())
                        .fitCenter().into(imageview);
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
        AlbumAdapter0.drawable = null;
        int currentItem = mViewPager.getCurrentItem();
        Intent intent = new Intent();
        intent.putExtra(ARG_INDEX, currentItem);
        setResult(RESULT_OK, intent);
        String transitionName = getString(R.string.ysq_transition_name, currentItem);
        if (Build.VERSION.SDK_INT >= 21) {
            for (int i = 0; i < mViewPager.getChildCount(); i++) {
                if (transitionName.equals(mViewPager.getChildAt(i).getTransitionName())) {
                    setSharedElementCallback(mViewPager.getChildAt(i));
                    break;
                }
            }
        }
        super.finishAfterTransition();
    }

    @Override
    public void onBackPressed() {
        if (!mIniting) {
            if (mViewPager.isIdle()) {
                mViewPager.setScrollEnable(false);
                super.onBackPressed();
            } else {
                mViewPager.setNextIdleListener(new PreviewViewPager.NextIdleListener() {
                    @Override
                    public void onNextIdle() {
                        onBackPressed();
                    }
                });
            }
        }
    }


    @RequiresApi(21)
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


    @RequiresApi(21)
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
}
