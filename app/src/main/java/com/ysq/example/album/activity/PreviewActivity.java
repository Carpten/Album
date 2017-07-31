package com.ysq.example.album.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ysq.example.album.R;
import com.ysq.example.album.adapter.BrowseAdapter;
import com.ysq.example.album.transition.AlbumEnterTransition;
import com.ysq.example.album.transition.SimpleTransitionListener;
import com.ysq.example.album.view.PhotoFrameLayout;
import com.ysq.example.album.view.PreviewViewPager;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PreviewActivity extends AppCompatActivity {

    public static final String ARG_INDEX = "ARG_INDEX";

    private int mIndex;

    @BindView(R.id.viewpager)
    public PreviewViewPager mViewPager;

    private boolean mIniting = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            postponeEnterTransition();
        }
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);
        mIndex = getIntent().getIntExtra(ARG_INDEX, 0);
        setupViewPager();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), BrowseAdapter.BROWSE_IDS[mIndex], options);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setSharedElementEnterTransition(new AlbumEnterTransition(PreviewActivity.this, options.outWidth, options.outHeight)
                    .addTarget(getString(R.string.transition_name, mIndex)).setDuration(getResources().getInteger(R.integer.scene_duration_in)).addListener(new SimpleTransitionListener() {
                        @Override
                        public void onTransitionEnd(Transition transition) {
                            mIniting = false;
                            mViewPager.setScrollEnable(true);
                        }
                    }));
        }
    }


    private void setupViewPager() {
        mViewPager.setAdapter(mPreviewAdapter);
        mViewPager.setCurrentItem(mIndex);
        if (Build.VERSION.SDK_INT >= 21)
            mViewPager.setScrollEnable(false);
    }

    private PagerAdapter mPreviewAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return BrowseAdapter.BROWSE_IDS.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView imageview = new ImageView(PreviewActivity.this);
            imageview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageview.setTag(R.id.tag_position, position);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageview.setTransitionName(container.getContext().getString(R.string.transition_name, position));
                if (position == mIndex && mIniting) {
                    setStartPostTransition(imageview);
                }
            }
            if (mIniting && position == mIndex) {
                BrowseAdapter.VH viewholder = (BrowseAdapter.VH) BrowseAdapter.mWeakRecyclerViewRef.get()
                        .findViewHolderForLayoutPosition(mIndex);
                Glide.with(PreviewActivity.this).load(BrowseAdapter.BROWSE_IDS[position])
                        .placeholder(viewholder.imageView.getDrawable()).dontAnimate().fitCenter().into(imageview);
            } else
                Glide.with(PreviewActivity.this).load(BrowseAdapter.BROWSE_IDS[position])
                        .fitCenter().into(imageview);

            PhotoFrameLayout layout = new PhotoFrameLayout(container.getContext());
            layout.setLayoutParams(new ViewPager.LayoutParams());
            layout.addView(imageview);
            container.addView(layout);
            return layout;
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
        String transitionName = getString(R.string.transition_name, currentItem);
        if (Build.VERSION.SDK_INT >= 21) {
            for (int i = 0; i < mViewPager.getChildCount(); i++) {
                if (transitionName.equals(((FrameLayout) mViewPager.getChildAt(i)).getChildAt(0).getTransitionName())) {
                    BrowseAdapter.VH viewholder = (BrowseAdapter.VH) BrowseAdapter.mWeakRecyclerViewRef.get()
                            .findViewHolderForAdapterPosition(currentItem);
                    ImageView imageView = (ImageView) ((FrameLayout) mViewPager.getChildAt(i)).getChildAt(0);
                    imageView.setImageDrawable(viewholder.imageView.getDrawable());
                    setSharedElementCallback(((FrameLayout) mViewPager.getChildAt(i)).getChildAt(0));
                    break;
                }
            }
        }
        super.finishAfterTransition();
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= 21 && !mIniting) {
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
        } else if (Build.VERSION.SDK_INT <= 21) {
            super.onBackPressed();
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
