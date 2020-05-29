package com.ysq.album.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.ysq.album.R;
import com.ysq.album.activity.AlbumActivity;
import com.ysq.album.bean.BucketBean;

import java.util.List;

/**
 * Author: yangshuiqiang
 * Date:2017/4/14.
 */

public class AlbumBucketAdapter extends RecyclerView.Adapter<AlbumBucketAdapter.VH> implements View.OnClickListener {

    private AlbumActivity mAlbumActivity;

    private List<BucketBean> mBuckets;

    public AlbumBucketAdapter(AlbumActivity albumActivity, List<BucketBean> buckets) {
        mAlbumActivity = albumActivity;
        mBuckets = buckets;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.ysq_recyclerview_switch, parent, false));
    }

    @Override
    public void onBindViewHolder(final VH holder, int position) {
        holder.name.setText(String.format(mAlbumActivity.getString(R.string.ysq_switch_bucket_item), mBuckets.get(position).getBucket_name(), mBuckets.get(position).getImageBeen().size()));
        Glide.with(mAlbumActivity).load(mBuckets.get(position).getImageBeen().get(0).getImage_path())
                .placeholder(R.drawable.ic_album_default)
                .transition(DrawableTransitionOptions.withCrossFade(400))
                .centerCrop().into(holder.imageView);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mBuckets.size();
    }

    @Override
    public void onClick(View v) {
        mAlbumActivity.setBucket((Integer) v.getTag());
    }

    class VH extends RecyclerView.ViewHolder {
        TextView name;
        ImageView imageView;

        private VH(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.textview);
            imageView = (ImageView) itemView.findViewById(R.id.imageview);
        }
    }
}
