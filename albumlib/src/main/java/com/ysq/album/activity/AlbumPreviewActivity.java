package com.ysq.album.activity;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.ysq.album.R;
import com.ysq.album.bean.ImageBean;

import java.io.File;
import java.util.List;

/**
 * Author: yangshuiqiang
 * Date:2017/6/15.
 */


public class AlbumPreviewActivity extends AppCompatActivity {

    public static final String ARG_BUCKET_INDEX = "ARG_BUCKET_INDEX";
    public static final String ARG_INDEX = "ARG_INDEX";

    private List<ImageBean> mImageBeen;

    private int mPicW, mPicH;

    private int mBucketIndex, mIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
        }
        setContentView(R.layout.ysq_activity_album_preview);
        mBucketIndex = getIntent().getIntExtra(ARG_BUCKET_INDEX, 0);
        mIndex = getIntent().getIntExtra(ARG_INDEX, 0);
        mImageBeen = AlbumActivity.albumPicker.getBuckets().get(mBucketIndex).getImageBeen();
        setupViewPager();
    }


    private void readPicSize() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mImageBeen.get(getIntent().getIntExtra(ARG_INDEX, 0)).getImage_path(), options);
        mPicW = options.outWidth;
        mPicH = options.outHeight;
    }

    private void setupViewPager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(mPreviewAdapter);
        viewPager.setCurrentItem(mIndex);
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
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(container.getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setImageURI(Uri.fromFile(new File(mImageBeen.get(position).getImage_path())));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String transitionName = container.getContext().getString(R.string.ysq_transition_name, position);
                imageView.setTransitionName(transitionName);
                imageView.setTag(transitionName);
                // TODO: 2017/6/26
                if (position == mIndex)
                    setStartPostTransition(imageView);

            }
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    };


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


    private Bitmap getThumbnailPic() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int inSampleSize = 8;
        while (true) {
            float widthRatio = (mPicW * 8) / (dm.widthPixels * inSampleSize);
            float heightRatio = (mPicH * 8) / (dm.heightPixels * inSampleSize);
            if (widthRatio > 1 || heightRatio > 1) {
                inSampleSize *= 2;
            } else {
                break;
            }
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        return BitmapFactory.decodeFile(getIntent().getStringExtra(ARG_INDEX), options);
    }

}
