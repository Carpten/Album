package com.ysq.example.album.divide;

import android.content.Context;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.State;
import android.view.View;

import com.ysq.example.album.R;


public class BrowseDecoration extends RecyclerView.ItemDecoration {

    private int mPadding;

    public BrowseDecoration(Context context) {
        mPadding = context.getResources().getDimensionPixelSize(R.dimen.recycler_divide);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        outRect.set(mPadding, mPadding, mPadding, mPadding);
    }
}