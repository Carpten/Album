package com.ysq.example.album.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ysq.album.activity.AlbumActivity;
import com.ysq.example.album.R;

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

    public void picBrowse(View view) {
        startActivity(new Intent(MainActivity.this, BrowseActivity.class));
    }
}
