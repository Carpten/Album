package com.ysq.album.activity;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.ysq.album.R;
import com.ysq.album.adapter.AlbumAdapter0;
import com.ysq.album.bean.ImageBean;
import com.ysq.album.bean.ImageBean0;
import com.ysq.album.view.AlbumCheckBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: yangshuiqiang
 * Date:2017/6/15.
 */


public class AlbumPreviewActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String ARG_BUCKET_INDEX = "ARG_BUCKET_INDEX";

    public static final String ARG_INDEX = "ARG_INDEX";

    public static final String ARG_MODE = "ARG_MODE";

    private List<ImageBean0> mImageBeen;

    private AlbumCheckBox mCheckBox;

    private ViewPager mViewPager;

    private AppBarLayout mAppbarLayout;

    private int mMode;

    private boolean mIsFullScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ysq_activity_album_preview);
        mAppbarLayout = (AppBarLayout) findViewById(R.id.appbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initActionbar();
        mMode = getIntent().getIntExtra(ARG_MODE, -1);
        int currentPosition = getIntent().getIntExtra(ARG_INDEX, 0);
        if (AlbumActivity.albumPicker == null)
            finish();
        if (mMode == AlbumActivity.MODE_MULTI_SELECT || mMode == AlbumActivity.MODE_SINGLE_SELECT)
            mImageBeen = AlbumActivity.albumPicker.getBuckets().get(getIntent().getIntExtra(ARG_BUCKET_INDEX, 0)).getImageBeen();
        else {
            mImageBeen = new ArrayList<>();
            for (ImageBean0 bean : AlbumActivity.albumPicker.getSelectImages()) {
                mImageBeen.add(bean);
            }
        }
        getSupportActionBar().setTitle(getTitle(mImageBeen.get(currentPosition).getImage_name()));
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mPreviewAdapter);
        mViewPager.setCurrentItem(currentPosition);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
        mCheckBox = (AlbumCheckBox) findViewById(R.id.checkbox);
        if (mMode != AlbumActivity.MODE_SINGLE_SELECT) {
            mCheckBox.setVisibility(View.VISIBLE);
            mCheckBox.setChecked(mImageBeen.get(currentPosition).isSelected());
            mCheckBox.setOnCheckedChangeListener(mOnCheckedChangeListener);
            mCheckBox.setOnCannotCheckMoreListener(mOnCannotCheckMoreListener);
        } else {
            mCheckBox.setVisibility(View.GONE);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        mAppbarLayout.setVisibility(View.VISIBLE);
    }

    private PagerAdapter mPreviewAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return mImageBeen.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            PhotoView imageview = new PhotoView(AlbumPreviewActivity.this);
            imageview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageview.setTag(R.id.tag_position, position);
            imageview.setOnClickListener(AlbumPreviewActivity.this);
            Glide.with(AlbumPreviewActivity.this).load(mImageBeen.get(position).getImage_path())
                    .transition(DrawableTransitionOptions.withCrossFade(400))
                    .fitCenter().into(imageview);
            container.addView(imageview);
            return imageview;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    };


    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(getTitle(mImageBeen.get(position).getImage_name()));
            if (mCheckBox.getVisibility() == View.VISIBLE) {
                mCheckBox.setOnCheckedChangeListener(null);
                mCheckBox.setChecked(mImageBeen.get(position).isSelected());
                mCheckBox.setOnCheckedChangeListener(mOnCheckedChangeListener);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mImageBeen.get(mViewPager.getCurrentItem()).setSelected(isChecked);
            RecyclerView recyclerView = AlbumAdapter0.mWeakRecyclerViewRef.get();
            int childCount = recyclerView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = recyclerView.getChildAt(i);
                AlbumAdapter0.VH childViewHolder = (AlbumAdapter0.VH) recyclerView.getChildViewHolder(childView);
                AlbumAdapter0 adapter = (AlbumAdapter0) recyclerView.getAdapter();
                if (mImageBeen.get(mViewPager.getCurrentItem()).getImage_path()
                        .equals(adapter.getPicPath(childViewHolder))) {
                    adapter.setCheckBox(childViewHolder);
                    break;
                }
            }
            if (isChecked)
                AlbumActivity.albumPicker.add(mImageBeen.get(mViewPager.getCurrentItem()));
            else
                AlbumActivity.albumPicker.remove(mImageBeen.get(mViewPager.getCurrentItem()));
        }
    };

    private AlbumCheckBox.OnCannotCheckMoreListener mOnCannotCheckMoreListener = new AlbumCheckBox.OnCannotCheckMoreListener() {
        @Override
        public void OnCannotCheckMore() {
            Snackbar.make(getWindow().getDecorView()
                    , getString(R.string.ysq_cannot_select_more, AlbumActivity.albumPicker.getMaxCount())
                    , Snackbar.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onBackPressed() {
        if (mMode != AlbumActivity.MODE_SINGLE_SELECT)
            setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.ysq_menu_done, menu);
        menu.findItem(R.id.action_done).setVisible(getIntent().getIntExtra(ARG_MODE, -1) == AlbumActivity.MODE_SINGLE_SELECT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (i == R.id.action_done) {
            ImageBean imageBean = new ImageBean();
            imageBean.setImage_name(mImageBeen.get(mViewPager.getCurrentItem()).getImage_name());
            imageBean.setImage_path(mImageBeen.get(mViewPager.getCurrentItem()).getImage_path());
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable(AlbumActivity.ARG_DATA, imageBean);
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    private void initActionbar() {
        int statusBarHeight = 0;
        int id = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (id > 0) {
            statusBarHeight = getResources().getDimensionPixelOffset(id);
        }
        mAppbarLayout.setPadding(0, statusBarHeight, 0, 0);
    }

    private String getTitle(String title) {
        TextPaint paint = new TextPaint();
        paint.setTextSize(sp2px(20));
        return TextUtils.ellipsize(title, paint, dp2px(200), TextUtils.TruncateAt.END).toString();
    }

    private int dp2px(int dpValue) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }

    private int sp2px(int dpValue) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, dpValue, getResources().getDisplayMetrics());
    }

    @Override
    public void onClick(View v) {
        transFullScreen();
    }

    private void transFullScreen() {
        if (mIsFullScreen) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            if (mAppbarLayout.getAnimation() != null) {
                mAppbarLayout.getAnimation().setAnimationListener(null);
                mAppbarLayout.clearAnimation();
            }
            Animation animation = AnimationUtils.loadAnimation(AlbumPreviewActivity.this, R.anim.ysq_actionbar_in);
            animation.setInterpolator(new LinearOutSlowInInterpolator());
            mAppbarLayout.setAnimation(animation);
            mAppbarLayout.setVisibility(View.VISIBLE);
            animation.start();
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            mAppbarLayout.clearAnimation();
            Animation animation = AnimationUtils.loadAnimation(AlbumPreviewActivity.this, R.anim.ysq_actionbar_out);
            animation.setInterpolator(new LinearOutSlowInInterpolator());
            mAppbarLayout.setAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mAppbarLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        }
        mIsFullScreen = !mIsFullScreen;
    }
}
