package com.ysq.album.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ysq.album.R;
import com.ysq.album.adapter.AlbumAdapter;
import com.ysq.album.adapter.AlbumAdapter0;
import com.ysq.album.adapter.AlbumBucketAdapter;
import com.ysq.album.bean.BucketBean;
import com.ysq.album.divider.AlbumDecoration;
import com.ysq.album.itf.IAlbum;
import com.ysq.album.other.AlbumPicker;

import java.io.File;
import java.util.List;
import java.util.Map;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class AlbumActivity extends AppCompatActivity implements View.OnClickListener, IAlbum {

    public static final String ARG_PATH = "ARG_INDEX";

    public static final String ARG_MODE = "ARG_MODE";

    private static final int INTENT_CAMERA = 100;

    private static final int INTENT_ZOOM = 101;

    public static final int MODE_SELECT = 0;

    public static final int MODE_PORTRAIT = 1;

    private static final int SPAN_COUNT = 4;

    public static AlbumPicker albumPicker;

    private RecyclerView mRecyclerView;

    private MaterialDialog mBucketDialog;

    private int mMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ysq_activity_album);
        mMode = getIntent().getIntExtra(ARG_MODE, MODE_SELECT);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        View bottomView = findViewById(R.id.bottom);
        bottomView.setVisibility(mMode == MODE_SELECT ? View.VISIBLE : View.GONE);
        invalidateOptionsMenu();
        AlbumActivityPermissionsDispatcher.initAlbumWithCheck(this);
    }


    @SuppressLint("InlinedApi")
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void initAlbum() {
        albumPicker = new AlbumPicker(AlbumActivity.this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        mRecyclerView.addItemDecoration(new AlbumDecoration(this));
        setBucket(0);
        initSwitchIcon();
    }


    public void setBucket(int bucketIndex) {
        if (mBucketDialog != null && mBucketDialog.isShowing()) {
            mBucketDialog.dismiss();
        }
        List<BucketBean> buckets = albumPicker.getBuckets();
        ((TextView) findViewById(R.id.tv_bucket_name)).setText(buckets.get(bucketIndex).getBucket_name());
        if (mMode == MODE_PORTRAIT)
            mRecyclerView.setAdapter(new AlbumAdapter(AlbumActivity.this, bucketIndex));
        else
            mRecyclerView.setAdapter(new AlbumAdapter0(AlbumActivity.this, bucketIndex));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AlbumActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onClick(View v) {
        mBucketDialog = new MaterialDialog.Builder(AlbumActivity.this)
                .adapter(new AlbumBucketAdapter(AlbumActivity.this, albumPicker.getBuckets()), new LinearLayoutManager(AlbumActivity.this))
                .title(getString(R.string.ysq_switch_bucket_title))
                .negativeText(getString(R.string.ysq_cancel))
                .show();
    }

    private void initSwitchIcon() {
        if (albumPicker.getBuckets().get(0).getImageBeen().size() == 0) {
            findViewById(R.id.iv_switch_bucket).setVisibility(View.GONE);
        } else {
            findViewById(R.id.switch_bucket).setOnClickListener(this);
        }
    }

    @Override
    public void takePhoto() {
        AlbumActivityPermissionsDispatcher.startPhotoWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.CAMERA})
    public void startPhoto() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(getExternalCacheDir(), getString(R.string.ysq_album_original));
            Uri uri = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            startActivityForResult(intent, INTENT_CAMERA);
        }
    }


    /**
     * 裁剪图片方法实现
     */
    @Override
    public void startZoom(Uri uri) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 320);
            intent.putExtra("outputY", 320);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(getExternalCacheDir(), getString(R.string.ysq_album_zoom))));
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, INTENT_ZOOM);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case INTENT_CAMERA:
                startZoom(Uri.fromFile(new File(getExternalCacheDir(), getString(R.string.ysq_album_original))));
                break;
            case INTENT_ZOOM:
                Intent intent = new Intent();
                intent.putExtra(ARG_PATH, new File(getExternalCacheDir(), getString(R.string.ysq_album_zoom)).getPath());
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            int position = data.getIntExtra(AlbumPreviewActivity.ARG_INDEX, 0);
            setCallback(position);
        }
    }

    @TargetApi(21)
    private void setCallback(final int position) {
        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                AlbumAdapter0.VH viewHolder = (AlbumAdapter0.VH) mRecyclerView.findViewHolderForAdapterPosition(position);
                if (names != null && sharedElements != null && viewHolder != null) {
                    names.clear();
                    sharedElements.clear();
                    View view = viewHolder.imageView;
                    names.add(view.getTransitionName());
                    sharedElements.put(view.getTransitionName(), view);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.ysq_menu_done, menu);
        menu.findItem(R.id.action_done).setVisible(mMode == MODE_SELECT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (i == R.id.action_done) {
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }
}
