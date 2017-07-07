package com.ysq.album.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.ysq.album.R;
import com.ysq.album.bean.ImageBean;

import java.util.List;

/**
 * Author: yangshuiqiang
 * Date:2017/6/15.
 */


public class AlbumPreviewActivity extends AppCompatActivity {

    public static final String ARG_BUCKET_INDEX = "ARG_BUCKET_INDEX";

    public static final String ARG_INDEX = "ARG_INDEX";

    private List<ImageBean> mImageBeen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ysq_activity_album_preview);
        mImageBeen = AlbumActivity.albumPicker.getBuckets().get(getIntent().getIntExtra(ARG_BUCKET_INDEX, 0))
                .getImageBeen();
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(mPreviewAdapter);
        viewPager.setCurrentItem(getIntent().getIntExtra(ARG_INDEX, 0));
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
}
