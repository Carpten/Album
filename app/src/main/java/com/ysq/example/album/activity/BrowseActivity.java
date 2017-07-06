package com.ysq.example.album.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.ysq.example.album.R;
import com.ysq.example.album.adapter.BrowseAdapter;
import com.ysq.example.album.divide.BrowseDecoration;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BrowseActivity extends AppCompatActivity {

    private long mThrottleTimeMillis, mLastTimeMills;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mThrottleTimeMillis = getResources().getInteger(R.integer.scene_duration_in);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mRecyclerView.addItemDecoration(new BrowseDecoration(this));
        mRecyclerView.setAdapter(new BrowseAdapter(this));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            if (System.currentTimeMillis() - mLastTimeMills >= mThrottleTimeMillis)
                onBackPressed();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            int position = data.getIntExtra(PreviewActivity.ARG_INDEX, 0);
            setCallback(position);
        }
    }

    @TargetApi(21)
    private void setCallback(final int position) {
        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                if (names != null && sharedElements != null) {
                    names.clear();
                    sharedElements.clear();
                    BrowseAdapter.VH viewHolder = (BrowseAdapter.VH) mRecyclerView.findViewHolderForAdapterPosition(position);
                    if (viewHolder != null) {
                        View view = viewHolder.imageView;
                        names.add(view.getTransitionName());
                        sharedElements.put(view.getTransitionName(), view);
                    }
                }
            }
        });
    }

    public boolean isThrottle() {
        if (System.currentTimeMillis() - mLastTimeMills >= mThrottleTimeMillis) {
            mLastTimeMills = System.currentTimeMillis();
            return false;
        } else {
            return true;
        }
    }
}
