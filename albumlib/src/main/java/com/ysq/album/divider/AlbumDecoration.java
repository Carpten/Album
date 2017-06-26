package com.ysq.album.divider;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.State;
import android.view.View;

import com.ysq.album.R;

public class AlbumDecoration extends RecyclerView.ItemDecoration {

    private int mPadding;

    public AlbumDecoration(Context context) {
        mPadding = context.getResources().getDimensionPixelSize(R.dimen.album_divide);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        outRect.set(mPadding, mPadding, mPadding, mPadding);
    }
}