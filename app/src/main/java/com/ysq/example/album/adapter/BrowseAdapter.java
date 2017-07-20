package com.ysq.example.album.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ysq.example.album.R;
import com.ysq.example.album.activity.BrowseActivity;
import com.ysq.example.album.activity.PreviewActivity;

/**
 * Author: yangshuiqiang
 * Date:2017/7/5.
 */

public class BrowseAdapter extends RecyclerView.Adapter<BrowseAdapter.VH> implements View.OnClickListener {

    public static Drawable drawable;

    public static final int[] BROWSE_IDS = {R.mipmap.pic0, R.mipmap.pic1, R.mipmap.pic2, R.mipmap.pic3, R.mipmap.pic4
            , R.mipmap.pic5, R.mipmap.pic6, R.mipmap.pic7};

    private BrowseActivity mActivity;
    private int mPicSize;

    public BrowseAdapter(BrowseActivity activity) {
        mActivity = activity;
        mPicSize = dp2px(activity, 100);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(mActivity).inflate(R.layout.recyclerview_browse, parent, false));
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Glide.clear(holder.imageView);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(mActivity.getResources(), BROWSE_IDS[position], options);
        int picW, picH;
        if (options.outWidth > options.outHeight) {
            picW = mPicSize * options.outWidth / options.outHeight + 1;
            picH = mPicSize;
        } else if (options.outWidth == options.outHeight) {
            picW = mPicSize;
            picH = mPicSize;
        } else {
            picW = mPicSize;
            picH = mPicSize * options.outHeight / options.outWidth + 1;
        }
        Glide.with(mActivity).load(BROWSE_IDS[position]).placeholder(R.drawable.ic_placeholder)
                .override(picW, picH).into(holder.imageView);
        holder.imageView.setTag(R.id.tag_position, position);
        holder.imageView.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= 21) {
            String transitionName = mActivity.getString(R.string.transition_name, position);
            holder.imageView.setTransitionName(transitionName);
            holder.imageView.setTag(R.id.tag_transition_name, transitionName);
        }
    }

    @Override
    public int getItemCount() {
        return BROWSE_IDS.length;
    }

    @Override
    public void onClick(View v) {
        if (!mActivity.isThrottle()) {
            drawable = ((ImageView) v).getDrawable();
            Intent intent = new Intent(mActivity, PreviewActivity.class);
            intent.putExtra(PreviewActivity.ARG_INDEX, (int) v.getTag(R.id.tag_position));
            String tag = (String) v.getTag(R.id.tag_transition_name);
            mActivity.setExitSharedElementCallback((SharedElementCallback) null);
            ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, v, tag);
            mActivity.startActivity(intent, activityOptions.toBundle());
        }
    }

    private int dp2px(Context context, int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public class VH extends RecyclerView.ViewHolder {
        public ImageView imageView;

        private VH(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageview);
        }
    }
}
