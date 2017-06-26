package com.ysq.example.album.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ysq.example.album.R;

/**
 * Author: yangshuiqiang
 * Date:2017/5/3.
 */

public class PersonalAdapter extends RecyclerView.Adapter<PersonalAdapter.VH> {

    @Override
    public PersonalAdapter.VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_personal, parent, false));
    }

    @Override
    public void onBindViewHolder(PersonalAdapter.VH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class VH extends RecyclerView.ViewHolder {

        private VH(View itemView) {
            super(itemView);
        }
    }
}
