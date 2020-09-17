package com.ysq.album.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ysq.album.R;
import com.ysq.album.adapter.AlbumAdapter0;
import com.ysq.album.adapter.AlbumAdapter1;
import com.ysq.album.adapter.AlbumAdapter2;
import com.ysq.album.adapter.AlbumBucketAdapter;
import com.ysq.album.bean.BucketBean;
import com.ysq.album.bean.ImageBean;
import com.ysq.album.bean.ImageBean0;
import com.ysq.album.divider.AlbumDecoration;
import com.ysq.album.other.AlbumPicker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.FileProvider.getUriForFile;


public class AlbumActivity extends AppCompatActivity {

    public static final String ARG_PATH = "ARG_INDEX";

    public static final String ARG_MODE = "ARG_MODE";

    public static final String ARG_DATA = "ARG_DATA";

    public static final String ARG_MAX_COUNT = "ARG_MAX_COUNT";

    public static final int PERMISSIONS_READ_EXTERNAL_STORAGE = 200;

    public static final int PERMISSIONS_CAMERA = 201;

    private static final int INTENT_CAMERA = 100;

    private static final int INTENT_ZOOM = 101;

    public static final int INTENT_PREVIEW = 102;

    public static final int INTENT_SINGLE_SELECT = 103;

    public static final int MODE_MULTI_SELECT = 0;

    public static final int MODE_SINGLE_SELECT = 1;

    public static final int MODE_PORTRAIT = 2;

    public static final int MODE_PORTRAIT_WITHOUT_PHOTO = 3;

    public static final int MODE_PORTRAIT_JUST_PHOTO = 4;

    private static final int SPAN_COUNT = 4;

    private static final int DEFAULT_MAX_COUNT = 9;

    public static AlbumPicker albumPicker;

    private RecyclerView mRecyclerView;

    private int mMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMode = getIntent().getIntExtra(ARG_MODE, MODE_MULTI_SELECT);
        setContentView(R.layout.ysq_activity_album);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        View bottomView = findViewById(R.id.bottom);
        bottomView.setVisibility(mMode == MODE_MULTI_SELECT ? View.VISIBLE : View.GONE);
        invalidateOptionsMenu();
        if (ContextCompat.checkSelfPermission(AlbumActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AlbumActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_READ_EXTERNAL_STORAGE);
        } else {
            initAlbum();
        }
    }


    void initAlbum() {

//        CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
//        try {
//            String[] cameraIdList = cameraManager.getCameraIdList();
//            for (String i : cameraIdList) {
//                Log.i("test", "i:" + i);
//            }
//        } catch (CameraAccessException e) {
//
//
//        }
        albumPicker = new AlbumPicker(AlbumActivity.this, getIntent().getIntExtra(ARG_MAX_COUNT, DEFAULT_MAX_COUNT));
        @SuppressWarnings("unchecked") List<ImageBean> imageBeen = (List<ImageBean>) getIntent().getSerializableExtra(ARG_DATA);
        if (imageBeen != null && imageBeen.size() > 0)
            albumPicker.setSelectImages(imageBeen);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        mRecyclerView.addItemDecoration(new AlbumDecoration(this));
        refreshSelectNum();
        setBucket(0);
        initSwitchIcon();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAlbum();
                } else {
                    new MaterialDialog.Builder(AlbumActivity.this)
                            .cancelable(false)
                            .content(R.string.permissions_read_external_storage_deny)
                            .positiveText(R.string.ysq_dialog_positive)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    finish();
                                }
                            })
                            .show();
                }
                break;
            case PERMISSIONS_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    new MaterialDialog.Builder(AlbumActivity.this)
                            .cancelable(false)
                            .content(R.string.permissions_carmea_deny)
                            .positiveText(R.string.ysq_dialog_positive)
                            .show();
                }
                break;
        }
    }


    private void initSwitchIcon() {
        if (albumPicker.getBuckets().get(0).getImageBeen().size() != 0) {
            findViewById(R.id.iv_switch_bucket).setVisibility(View.VISIBLE);
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
        if (mMode == MODE_MULTI_SELECT)
            mRecyclerView.setAdapter(new AlbumAdapter0(AlbumActivity.this, bucketIndex));
        else if (mMode == MODE_SINGLE_SELECT)
            mRecyclerView.setAdapter(new AlbumAdapter1(AlbumActivity.this, bucketIndex));
        else if (mMode == MODE_PORTRAIT)
            mRecyclerView.setAdapter(new AlbumAdapter2(AlbumActivity.this, bucketIndex, 0));
        else if (mMode == MODE_PORTRAIT_WITHOUT_PHOTO)
            mRecyclerView.setAdapter(new AlbumAdapter2(AlbumActivity.this, bucketIndex, 1));

    }

    public void takePhoto() {
        if (ContextCompat.checkSelfPermission(AlbumActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AlbumActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_CAMERA);
        } else {
            startPhoto();
        }
    }

    private Uri getUri(File file) {
        if (Build.VERSION.SDK_INT < 24) {
            return Uri.fromFile(file);
        } else {
            return getUriForFile(AlbumActivity.this,
                    getPackageName() + ".fileprovider", file);
        }
    }

    public void startPhoto() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
            intent.putExtra("android.intent.extras.CAMERA_FACING_FRONT", 1);
            intent.putExtra("camerasensortype", 2); // 调用前置摄像头
            File file = new File(getExternalCacheDir(), getString(R.string.ysq_album_original));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getUri(file));
            if (Build.VERSION.SDK_INT >= 24) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            startActivityForResult(intent, INTENT_CAMERA);
        }
    }


    /**
     * 裁剪图片方法实现
     */
    public void startZoom(File file) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(getUri(file), "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
//            intent.putExtra("outputX", 800);
//            intent.putExtra("outputY", 800);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(getExternalCacheDir(), getString(R.string.ysq_album_zoom))));
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true);
            intent.putExtra("return-data", false);
            if (Build.VERSION.SDK_INT >= 24) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            startActivityForResult(intent, INTENT_ZOOM);
        }
    }

    public void startPreview(View view) {
        if (albumPicker.getSelectImages().size() > 0) {
            Intent intent = new Intent(AlbumActivity.this, AlbumPreviewActivity.class);
            startActivityForResult(intent, AlbumActivity.INTENT_PREVIEW);
        }
    }

    public void refreshSelectNum() {
        TextView tvChooseNum = (TextView) findViewById(R.id.tv_choose_num);
        tvChooseNum.setText(getString(R.string.ysq_choose_num, albumPicker.getCurrentCount(), albumPicker.getMaxCount()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case INTENT_CAMERA:
                if (resultCode == RESULT_OK)
                    startZoom(new File(getExternalCacheDir(), getString(R.string.ysq_album_original)));
                break;
            case INTENT_ZOOM:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent();
                    intent.putExtra(ARG_PATH, new File(getExternalCacheDir(), getString(R.string.ysq_album_zoom)).getPath());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            case INTENT_PREVIEW:
                if (resultCode == RESULT_CANCELED) {
                    refreshSelectNum();
                } else {
                    setResult(RESULT_OK, data);
                    finish();
                }
                break;
            case INTENT_SINGLE_SELECT:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK, data);
                    finish();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.ysq_menu_done, menu);
        menu.findItem(R.id.action_done).setVisible(mMode == MODE_MULTI_SELECT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            onBackPressed();
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
            }
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }
}
