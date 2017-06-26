package com.ysq.album.adapter;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public AlbumAdapter0(AlbumActivity albumActivity, int bucketIndex) {
        mAlbumActivity = albumActivity;
        mBucketIndex = bucketIndex;
        mImageBeen = AlbumActivity.albumPicker.getBuckets().get(bucketIndex).getImageBeen();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(mAlbumActivity).inflate(R.layout.ysq_recyclerview_album2, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Glide.clear(((VH) holder).imageView);
        Glide.with(mAlbumActivity).load(mImageBeen.get(position).getImage_path())
                .placeholder(R.drawable.ic_album_default).centerCrop().into(((VH) holder).imageView);
        ((VH) holder).imageView.setTag(R.id.tag_position, position);
        ((VH) holder).imageView.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= 21) {
            String transitionName = mAlbumActivity.getString(R.string.ysq_transition_name, position);
            ((VH) holder).imageView.setTransitionName(transitionName);
            ((VH) holder).imageView.setTag(R.id.tag_transition_name, transitionName);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mAlbumActivity, AlbumPreviewActivity.class);
        intent.putExtra(AlbumPreviewActivity.ARG_BUCKET_INDEX, mBucketIndex);
        intent.putExtra(AlbumPreviewActivity.ARG_INDEX, (int) v.getTag(R.id.tag_position));
        String tag = (String) v.getTag(R.id.tag_transition_name);
        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(mAlbumActivity, v,tag );
        mAlbumActivity.startActivity(intent, activityOptions.toBundle());
    }

    @Override
    public int getItemCount() {
        return mImageBeen.size();
    }


    private class VH extends RecyclerView.ViewHolder {
        ImageView imageView;

        VH(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageview);
        }
    }
}