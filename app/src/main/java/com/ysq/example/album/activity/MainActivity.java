package com.ysq.example.album.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.ysq.album.activity.AlbumActivity;
import com.ysq.album.bean.ImageBean;
import com.ysq.example.album.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    public void picSelect(View view) {
        startActivityForResult(new Intent(MainActivity.this, AlbumActivity.class), 1000);
    }

    public void portraitUpload(View view) {
        startActivity(new Intent(MainActivity.this, PersonalActivity.class));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            List<ImageBean> imageBeen = (List<ImageBean>) data.getSerializableExtra(AlbumActivity.ARG_PATH);
            Log.i("test", "imageBeen is null?" + (imageBeen == null));
            for (ImageBean imageBean : imageBeen) {
                Log.i(MainActivity.class.getSimpleName(), imageBean.getImage_path());
            }
        }
    }
}
