package com.ysq.album.adapter;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ysq.album.R;
import com.ysq.album.activity.AlbumActivity;
import com.ysq.album.bean.ImageBean0;

import java.io.File;
import java.util.List;

/**
 * Author: yangshuiqiang
 * Date:2017/4/14.
 */

public class AlbumAdapter2 extends Adapter implements View.OnClickListener {

    private AlbumActivity mAlbumActivity;

    private List<ImageBean0> mImageBeen;

    private int mBucketIndex;

    public AlbumAdapter2(AlbumActivity albumActivity, int bucketIndex) {
        mAlbumActivity = albumActivity;
        mBucketIndex = bucketIndex;
        mImageBeen = AlbumActivity.albumPicker.getBuckets().get(bucketIndex).getImageBeen();
    }

    @Override
    public int getItemViewType(int position) {
        if (mBucketIndex == 0 && position == 0)
            return 0;
        else
            return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new VH0(LayoutInflater.from(mAlbumActivity).inflate(R.layout.ysq_recyclerview_album0, parent, false));
        else
            return new VH(LayoutInflater.from(mAlbumActivity).inflate(R.layout.ysq_recyclerview_album1, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VH) {
            Glide.with(mAlbumActivity).load(mImageBeen.get(position - (mBucketIndex == 0 ? 1 : 0)).getImage_path())
                    .placeholder(R.drawable.ic_album_default).centerCrop().into(((VH) holder).imageView);
            ((VH) holder).imageView.setTag(R.id.tag_path, mImageBeen.get(position - (mBucketIndex == 0 ? 1 : 0)).getImage_path());
            ((VH) holder).imageView.setOnClickListener(this);

        }

    }

    @Override
    public void onClick(View v) {
        String path = (String) v.getTag(R.id.tag_path);
        mAlbumActivity.startZoom(new File(path));
    }

    @Override
    public int getItemCount() {
        return mImageBeen.size() + (mBucketIndex == 0 ? 1 : 0);
    }

    private class VH0 extends RecyclerView.ViewHolder {

        private VH0(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAlbumActivity.takePhoto();
                }
            });
        }
    }

    private class VH extends RecyclerView.ViewHolder {
        ImageView imageView;

        private VH(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageview);
        }
    }
}