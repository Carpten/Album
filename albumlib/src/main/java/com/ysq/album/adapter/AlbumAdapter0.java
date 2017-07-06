package com.ysq.album.adapter;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ysq.album.R;
import com.ysq.album.activity.AlbumActivity;
import com.ysq.album.activity.AlbumPreviewActivity;
import com.ysq.album.bean.ImageBean;

import java.util.List;

/**
 * Author: yangshuiqiang
 * Date:2017/4/14.
 */

public class AlbumAdapter0 extends Adapter implements View.OnClickListener {

    private AlbumActivity mAlbumActivity;

    private List<ImageBean> mImageBeen;

    private int mBucketIndex;

    private int mImageLesserLength;

    public static Drawable drawable;

    public AlbumAdapter0(AlbumActivity albumActivity, int bucketIndex, int spanCount) {
        mAlbumActivity = albumActivity;
        mBucketIndex = bucketIndex;
        mImageBeen = AlbumActivity.albumPicker.getBuckets().get(bucketIndex).getImageBeen();
        DisplayMetrics dm = new DisplayMetrics();
        albumActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        mImageLesserLength = (int) (((float) dm.widthPixels - (spanCount + 1) * mAlbumActivity.getResources().getDimensionPixelSize(R.dimen.album_divide) * 2 - 0.5f) / spanCount + 1);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(mAlbumActivity).inflate(R.layout.ysq_recyclerview_album2, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mImageBeen.get(position).getImage_path(), options);
        Glide.clear(((VH) holder).imageView);
        int picW, picH;
        if (options.outWidth >= options.outHeight) {
            picW = mImageLesserLength * options.outWidth / options.outHeight;
            picH = mImageLesserLength;
        } else {
            picW = mImageLesserLength;
            picH = mImageLesserLength * options.outHeight / options.outWidth;
        }
        Glide.with(mAlbumActivity).load(mImageBeen.get(position).getImage_path())
                .placeholder(R.drawable.ic_album_default).override(picW, picH).into(((VH) holder).imageView);
        ((VH) holder).imageView.setTag(R.id.tag_position, position);
        ((VH) holder).imageView.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= 21) {
            String transitionName = mAlbumActivity.getString(R.string.ysq_transition_name, position);
            ((VH) holder).imageView.setTransitionName(transitionName);
            ((VH) holder).imageView.setTag(R.id.tag_transition_name, transitionName);
        }
    }

    @Override
    public void onClick(final View v) {
        if (!mAlbumActivity.isThrottle()) {
            drawable = ((ImageView) v).getDrawable();
            Intent intent = new Intent(mAlbumActivity, AlbumPreviewActivity.class);
            intent.putExtra(AlbumPreviewActivity.ARG_BUCKET_INDEX, mBucketIndex);
            intent.putExtra(AlbumPreviewActivity.ARG_INDEX, (int) v.getTag(R.id.tag_position));
            String tag = (String) v.getTag(R.id.tag_transition_name);
            mAlbumActivity.setExitSharedElementCallback((SharedElementCallback) null);
            ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(mAlbumActivity, v, tag);
            mAlbumActivity.startActivity(intent, activityOptions.toBundle());
        }
    }

    @Override
    public int getItemCount() {
        return mImageBeen.size();
    }


    public class VH extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public CheckBox checkBox;

        VH(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageview);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            imageView.setDrawingCacheEnabled(true);
        }
    }
}