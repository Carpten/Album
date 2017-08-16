package com.ysq.example.album.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ysq.album.activity.AlbumActivity;
import com.ysq.album.bean.ImageBean;
import com.ysq.example.album.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private ArrayList<ImageBean> mImageBeen;

    public void picSingleSelect(View view) {
        Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
        intent.putExtra(AlbumActivity.ARG_MODE, AlbumActivity.MODE_SINGLE_SELECT);
        startActivityForResult(intent, 1000);
    }

    public void picMultiSelect(View view) {
        Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
        intent.putExtra(AlbumActivity.ARG_MODE, AlbumActivity.MODE_MULTI_SELECT);
        intent.putExtra(AlbumActivity.ARG_MAX_COUNT, 8);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AlbumActivity.ARG_DATA, mImageBeen);
        intent.putExtras(bundle);
        startActivityForResult(intent, 1001);
    }


    public void portraitUpload(View view) {
        startActivity(new Intent(MainActivity.this, PersonalActivity.class));
    }

    public void picBrowse(View view) {
        startActivity(new Intent(MainActivity.this, BrowseActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            ImageBean imageBean = (ImageBean) data.getSerializableExtra(AlbumActivity.ARG_DATA);
            Toast.makeText(this, "path:" + imageBean.getImage_path(), Toast.LENGTH_SHORT).show();
        } else if (requestCode == 1001 && resultCode == RESULT_OK) {
            //noinspection unchecked
            mImageBeen = (ArrayList<ImageBean>) data.getSerializableExtra(AlbumActivity.ARG_DATA);
            for (ImageBean imageBean : mImageBeen) {
                Log.i("test", imageBean.getImage_path());
            }
            Toast.makeText(this, getString(R.string.picture_select_num, mImageBeen.size()), Toast.LENGTH_SHORT).show();
        }
    }


}
