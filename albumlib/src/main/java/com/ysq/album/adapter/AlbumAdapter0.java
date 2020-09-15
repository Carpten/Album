package com.ysq.album.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.snackbar.Snackbar;
import com.ysq.album.R;
import com.ysq.album.activity.AlbumActivity;
import com.ysq.album.activity.AlbumPreviewActivity;
import com.ysq.album.bean.ImageBean0;
import com.ysq.album.view.AlbumCheckBox;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Author: yangshuiqiang
 * Date:2017/4/14.
 */

public class AlbumAdapter0 extends RecyclerView.Adapter<AlbumAdapter0.VH> implements View.OnClickListener
        , CompoundButton.OnCheckedChangeListener, AlbumCheckBox.OnCannotCheckMoreListener {

    private AlbumActivity mAlbumActivity;

    private List<ImageBean0> mImageBeen;

    public static WeakReference<RecyclerView> mWeakRecyclerViewRef;

    private int mBucketIndex;

    public AlbumAdapter0(AlbumActivity albumActivity, int bucketIndex) {
        mAlbumActivity = albumActivity;
        mBucketIndex = bucketIndex;
        mImageBeen = AlbumActivity.albumPicker.getBuckets().get(bucketIndex).getImageBeen();
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mWeakRecyclerViewRef = new WeakReference<>(recyclerView);
    }

    @Override
    public AlbumAdapter0.VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(mAlbumActivity).inflate(R.layout.ysq_recyclerview_album2, parent, false));
    }

    @Override
    public void onBindViewHolder(final AlbumAdapter0.VH holder, int position) {
        Glide.with(mAlbumActivity).clear(holder.imageView);
        Glide.with(mAlbumActivity).load(mImageBeen.get(position).getImage_path())
                .transition(DrawableTransitionOptions.withCrossFade(400))
                .placeholder(R.drawable.ic_album_default).centerCrop().into(holder.imageView);
        holder.imageView.setTag(R.id.tag_position, position);
        holder.imageView.setOnClickListener(this);
        setCheckBox(holder);
    }

    public void setCheckBox(VH holder) {
        holder.checkBox.setTag(R.id.tag_viewholder, holder);
        holder.checkBox.setImageResource(mImageBeen.get(holder.getAdapterPosition()).isSelected()
                ? R.drawable.ysq_checked : R.drawable.ysq_unchecked);
        holder.mask.setBackgroundColor(ContextCompat.getColor(mAlbumActivity, mImageBeen
                .get(holder.getAdapterPosition()).isSelected() ? R.color.ysq_mask1 : R.color.ysq_mask0));
        holder.checkBox.setOnClickListener(this);

//        if (holder.checkBox.isChecked() != mImageBeen.get(holder.getAdapterPosition()).isSelected()) {
//            holder.checkBox.setOnCheckedChangeListener(null);
//            holder.checkBox.setChecked(mImageBeen.get(holder.getAdapterPosition()).isSelected());
//            holder.checkBox.setOnCheckedChangeListener(this);
//            holder.checkBox.setOnCannotCheckMoreListener(this);
//            holder.mask.setBackgroundColor(ContextCompat.getColor(mAlbumActivity, mImageBeen
//                    .get(holder.getAdapterPosition()).isSelected() ? R.color.ysq_mask1 : R.color.ysq_mask0));
//        } else if (holder.checkBox.getOnCannotCheckMoreListener() == null) {
//            holder.checkBox.setOnCheckedChangeListener(this);
//            holder.checkBox.setOnCannotCheckMoreListener(this);
//        }
    }

    public String getPicPath(VH viewHolder) {
        return mImageBeen.get(viewHolder.getAdapterPosition()).getImage_path();
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.checkbox) {
            AlbumAdapter0.VH holder = (VH) v.getTag(R.id.tag_viewholder);
            if (!mImageBeen.get(holder.getAdapterPosition()).isSelected() && AlbumActivity.albumPicker.getCurrentCount() >= AlbumActivity.albumPicker.getMaxCount()) {
                Snackbar.make(mAlbumActivity.getWindow().getDecorView()
                        , mAlbumActivity.getString(R.string.ysq_cannot_select_more, AlbumActivity.albumPicker.getMaxCount())
                        , Snackbar.LENGTH_SHORT).show();
            } else {
                if (!mImageBeen.get(holder.getAdapterPosition()).isSelected()) {
                    mImageBeen.get(holder.getAdapterPosition()).setSelected(true);
                    AlbumActivity.albumPicker.add(mImageBeen.get(holder.getAdapterPosition()));
                } else {
                    mImageBeen.get(holder.getAdapterPosition()).setSelected(false);
                    AlbumActivity.albumPicker.remove(mImageBeen.get(holder.getAdapterPosition()));
                }
                holder.checkBox.setImageResource(mImageBeen.get(holder.getAdapterPosition()).isSelected()
                        ? R.drawable.ysq_checked : R.drawable.ysq_unchecked);
                holder.mask.setBackgroundColor(ContextCompat.getColor(mAlbumActivity, mImageBeen
                        .get(holder.getAdapterPosition()).isSelected() ? R.color.ysq_mask1 : R.color.ysq_mask0));
                mAlbumActivity.refreshSelectNum();
            }
        } else {
            Intent intent = new Intent(mAlbumActivity, AlbumPreviewActivity.class);
            intent.putExtra(AlbumPreviewActivity.ARG_BUCKET_INDEX, mBucketIndex);
            intent.putExtra(AlbumPreviewActivity.ARG_INDEX, (int) v.getTag(R.id.tag_position));
            intent.putExtra(AlbumPreviewActivity.ARG_MODE, AlbumActivity.MODE_MULTI_SELECT);
            mAlbumActivity.startActivityForResult(intent, AlbumActivity.INTENT_PREVIEW);
        }
    }

    @Override
    public int getItemCount() {
        return mImageBeen.size();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        AlbumAdapter0.VH holder = (VH) buttonView.getTag(R.id.tag_viewholder);
        if (isChecked) {
            holder.mask.setBackgroundColor(ContextCompat.getColor(mAlbumActivity, R.color.ysq_mask1));
            mImageBeen.get(holder.getAdapterPosition()).setSelected(true);
            AlbumActivity.albumPicker.add(mImageBeen.get(holder.getAdapterPosition()));
        } else {
            holder.mask.setBackgroundColor(ContextCompat.getColor(mAlbumActivity, R.color.ysq_mask0));
            mImageBeen.get(holder.getAdapterPosition()).setSelected(false);
            AlbumActivity.albumPicker.remove(mImageBeen.get(holder.getAdapterPosition()));
        }
        mAlbumActivity.refreshSelectNum();
    }

    @Override
    public void OnCannotCheckMore() {
        Snackbar.make(mAlbumActivity.getWindow().getDecorView()
                , mAlbumActivity.getString(R.string.ysq_cannot_select_more, AlbumActivity.albumPicker.getMaxCount())
                , Snackbar.LENGTH_SHORT).show();
    }


    public class VH extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView checkBox;
        View mask;

        private VH(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageview);
            checkBox = (ImageView) itemView.findViewById(R.id.checkbox);
            mask = itemView.findViewById(R.id.mask);
            imageView.setDrawingCacheEnabled(true);
        }
    }
}