package com.ysq.album.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.ysq.album.R;
import com.ysq.album.adapter.AlbumAdapter0;
import com.ysq.album.bean.ImageBean0;
import com.ysq.album.view.AlbumCheckBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: yangshuiqiang
 * Date:2017/6/15.
 */


public class AlbumPreviewActivity extends AppCompatActivity {

    public static final String ARG_BUCKET_INDEX = "ARG_BUCKET_INDEX";

    public static final String ARG_INDEX = "ARG_INDEX";

    public static final String ARG_MODE = "ARG_MODE";

    public static final int MODE_SELECT = 0;

    public static final int MODE_PREVIEW = 1;

    private List<ImageBean0> mImageBeen;

    private AlbumCheckBox mCheckBox;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ysq_activity_album_preview);
        int mode = getIntent().getIntExtra(ARG_MODE, MODE_SELECT);
        int currentPosition = getIntent().getIntExtra(ARG_INDEX, 0);
        if (AlbumActivity.albumPicker == null)
            finish();
        if (mode == MODE_SELECT)
            mImageBeen = AlbumActivity.albumPicker.getBuckets().get(getIntent().getIntExtra(ARG_BUCKET_INDEX, 0)).getImageBeen();
        else {
            mImageBeen = new ArrayList<>();
            for (ImageBean0 bean : AlbumActivity.albumPicker.getSelectImages()) {
                mImageBeen.add(bean);
            }
        }
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mPreviewAdapter);
        mViewPager.setCurrentItem(currentPosition);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
        mCheckBox = (AlbumCheckBox) findViewById(R.id.checkbox);
        mCheckBox.setChecked(mImageBeen.get(currentPosition).isSelected());
        mCheckBox.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mCheckBox.setOnCannotCheckMoreListener(mOnCannotCheckMoreListener);
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
            Glide.with(AlbumPreviewActivity.this).load(mImageBeen.get(position).getImage_path()).fitCenter().into(imageview);
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
            mCheckBox.setOnCheckedChangeListener(null);
            mCheckBox.setChecked(mImageBeen.get(position).isSelected());
            mCheckBox.setOnCheckedChangeListener(mOnCheckedChangeListener);
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
        setResult(RESULT_OK);
        finish();
    }
}
