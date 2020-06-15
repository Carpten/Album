package com.ysq.album.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ysq.album.R;

import java.io.File;

import static androidx.core.content.FileProvider.getUriForFile;

public class PhotoUtils {

    public static final int PERMISSIONS_CAMERA = 201;
    public static final int INTENT_CAMERA = 3930;
    public static final int INTENT_ZOOM = 3931;


    public static void takePhoto(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_CAMERA);
        } else {
            startPhoto(activity);
        }
    }


    private static Uri getUri(Activity activity, File file) {
        if (Build.VERSION.SDK_INT < 24) {
            return Uri.fromFile(file);
        } else {
            return getUriForFile(activity,
                    activity.getPackageName() + ".ysq.fileprovider", file);
        }
    }

    public static void startPhoto(Activity activity) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra("camerasensortype", 2);
            File file = new File(activity.getExternalCacheDir(), activity.getString(R.string.ysq_album_original));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getUri(activity, file));
            if (Build.VERSION.SDK_INT >= 24) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            activity.startActivityForResult(intent, INTENT_CAMERA);
        }
    }

    public static void onRequestPermissionsResult(Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto(activity);
            } else {
                new MaterialDialog.Builder(activity)
                        .cancelable(false)
                        .content(R.string.permissions_carmea_deny)
                        .positiveText(R.string.ysq_dialog_positive)
                        .show();
            }
        }
    }

    /**
     * 裁剪图片方法实现
     */
    private void startZoom(Activity activity, File file) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(getUri(activity, file), "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 828);
            intent.putExtra("outputY", 828);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(activity.getExternalCacheDir(), activity.getString(R.string.ysq_album_zoom))));
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true);
            intent.putExtra("return-data", false);
            if (Build.VERSION.SDK_INT >= 24) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            activity.startActivityForResult(intent, INTENT_ZOOM);
        }
    }


    protected void onActivityResult(Activity activity, int requestCode, int resultCode, ResultListener resultListener) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case INTENT_CAMERA:
                    startZoom(activity, new File(activity.getExternalCacheDir(), activity.getString(R.string.ysq_album_original)));
                    break;
                case INTENT_ZOOM:
                    resultListener.onSuccess(new File(activity.getExternalCacheDir(), activity.getString(R.string.ysq_album_zoom)).getPath());
                    break;
            }
        }
    }

    public interface ResultListener {
        void onSuccess(String path);
    }
}


