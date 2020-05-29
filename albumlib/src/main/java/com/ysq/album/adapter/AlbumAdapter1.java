package com.ysq.album.adapter;

import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.ysq.album.R;
import com.ysq.album.activity.AlbumActivity;
import com.ysq.album.activity.AlbumPreviewActivity;
import com.ysq.album.bean.ImageBean0;

import java.util.List;

/**
 * Author: yangshuiqiang
 * Date:2017/4/14.
 */

public class AlbumAdapter1 extends Adapter<AlbumAdapter1.VH> implements View.OnClickListener {

    private AlbumActivity mAlbumActivity;

    private List<ImageBean0> mImageBeen;

    private int mBucketIndex;

    public AlbumAdapter1(AlbumActivity albumActivity, int bucketIndex) {
        mAlbumActivity = albumActivity;
        mBucketIndex = bucketIndex;
        mImageBeen = AlbumActivity.albumPicker.getBuckets().get(bucketIndex).getImageBeen();
    }

    @Override
    public AlbumAdapter1.VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(mAlbumActivity).inflate(R.layout.ysq_recyclerview_album1, parent, false));
    }

    @Override
    public void onBindViewHolder(final AlbumAdapter1.VH holder, int position) {
        Glide.with(mAlbumActivity).clear(holder.imageView);
        Glide.with(mAlbumActivity).load(mImageBeen.get(position).getImage_path())
                .placeholder(R.drawable.ic_album_default)
                .transition(DrawableTransitionOptions.withCrossFade(400))
                .centerCrop().into(holder.imageView);
        holder.imageView.setTag(R.id.tag_position, position);
        holder.imageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mAlbumActivity, AlbumPreviewActivity.class);
        intent.putExtra(AlbumPreviewActivity.ARG_BUCKET_INDEX, mBucketIndex);
        intent.putExtra(AlbumPreviewActivity.ARG_INDEX, (int) v.getTag(R.id.tag_position));
        intent.putExtra(AlbumPreviewActivity.ARG_MODE, AlbumActivity.MODE_SINGLE_SELECT);
        mAlbumActivity.startActivityForResult(intent, AlbumActivity.INTENT_SINGLE_SELECT);
    }

    @Override
    public int getItemCount() {
        return mImageBeen.size();
    }

    class VH extends RecyclerView.ViewHolder {
        ImageView imageView;

        private VH(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageview);
        }
    }
}