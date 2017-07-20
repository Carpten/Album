package com.ysq.album.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import com.ysq.album.bean.ImageBean;
import com.ysq.album.bean.ImageBean0;
import com.ysq.album.divider.AlbumDecoration;
import com.ysq.album.other.AlbumPicker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class AlbumActivity extends AppCompatActivity {

    public static final String ARG_PATH = "ARG_INDEX";

    public static final String ARG_MODE = "ARG_MODE";

    public static final String ARG_DATA = "ARG_DATA";

    public static final String ARG_MAX_COUNT = "ARG_MAX_COUNT";

    private static final int INTENT_CAMERA = 100;

    private static final int INTENT_ZOOM = 101;

    public static final int INTENT_PREVIEW = 102;

    public static final int MODE_SELECT = 0;

    public static final int MODE_PORTRAIT = 1;

    private static final int SPAN_COUNT = 4;

    private static final int DEFAULT_MAX_COUNT = 9;

    public static AlbumPicker albumPicker;

    private RecyclerView mRecyclerView;

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
        albumPicker = new AlbumPicker(AlbumActivity.this, getIntent().getIntExtra(ARG_MAX_COUNT, DEFAULT_MAX_COUNT));
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        mRecyclerView.addItemDecoration(new AlbumDecoration(this));
        refreshSelectNum();
        setBucket(0);
        initSwitchIcon();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AlbumActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    private void initSwitchIcon() {
        if (albumPicker.getBuckets().get(0).getImageBeen().size() == 0) {
            findViewById(R.id.iv_switch_bucket).setVisibility(View.GONE);
        } else {
            findViewById(R.id.switch_bucket).setOnClickListener(mSwitchIconClickListener);
        }
    }

    private MaterialDialog mBucketDialog;

    private View.OnClickListener mSwitchIconClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mBucketDialog = new MaterialDialog.Builder(AlbumActivity.this)
                    .adapter(new AlbumBucketAdapter(AlbumActivity.this, albumPicker.getBuckets()), new LinearLayoutManager(AlbumActivity.this))
                    .autoDismiss(true)
                    .title(getString(R.string.ysq_switch_bucket_title))
                    .negativeText(getString(R.string.ysq_cancel))
                    .show();
        }
    };

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

    public void startPreview(View view) {
        if (albumPicker.getSelectImages().size() > 0) {
            Intent intent = new Intent(AlbumActivity.this, AlbumPreviewActivity.class);
            intent.putExtra(AlbumPreviewActivity.ARG_MODE, AlbumPreviewActivity.MODE_ALL);
            startActivityForResult(intent, AlbumActivity.INTENT_PREVIEW);
        }
    }

    public void refreshSelectNum() {
        TextView tvChooseNum = (TextView) findViewById(R.id.tv_choose_num);
        tvChooseNum.setText(getString(R.string.ysq_choose_num, albumPicker.getCurrentCount(), albumPicker.getMaxCount()));
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
            case INTENT_PREVIEW:
                int childCount = mRecyclerView.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childView = mRecyclerView.getChildAt(i);
                    AlbumAdapter0.VH childViewHolder = (AlbumAdapter0.VH) mRecyclerView.getChildViewHolder(childView);
                    AlbumAdapter0 adapter = (AlbumAdapter0) mRecyclerView.getAdapter();
                    adapter.setCheckBox(childViewHolder);
                }
                mRecyclerView.getAdapter().notifyDataSetChanged();
                refreshSelectNum();
                break;
        }
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
            albumPicker = null;
            return true;
        } else if (i == R.id.action_done) {
            if (albumPicker.getCurrentCount() > 0) {
                ArrayList<ImageBean> imageBeen = new ArrayList<>();
                for (ImageBean0 imageBean0 : albumPicker.getSelectImages()) {
                    ImageBean imageBean = new ImageBean();
                    imageBean.setImage_name(imageBean0.getImage_name());
                    imageBean.setImage_path(imageBean0.getImage_path());
                    imageBeen.add(imageBean);
                }
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(ARG_DATA, imageBeen);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
                albumPicker = null;
            }
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }
}
