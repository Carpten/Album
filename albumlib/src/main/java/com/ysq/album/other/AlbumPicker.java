package com.ysq.album.other;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.SparseArray;

import com.ysq.album.R;
import com.ysq.album.bean.BucketBean;
import com.ysq.album.bean.ImageBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: yangshuiqiang
 * Date:2017/4/14.
 */

public class AlbumPicker {

    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA
    };

    private List<BucketBean> mBuckets = new ArrayList<>();

    private List<ImageBean> mSelectBeen = new ArrayList<>();

    public AlbumPicker(Context context) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES
                , "_size>=? and width>=? and height>=?", new String[]{"8192", "20", "20"}
                , "date_modified desc");
        SparseArray<BucketBean> bucketSparse = new SparseArray<>();
        BucketBean totalBucketBean = new BucketBean();
        totalBucketBean.setImageBeen(new ArrayList<ImageBean>());
        totalBucketBean.setBucket_name(context.getString(R.string.ysq_all_images));
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int bucketId = cursor.getInt(0);
                String bucketName = cursor.getString(1);
                int imageId = cursor.getInt(2);
                String imageName = cursor.getString(3);
                String imagePath = cursor.getString(4);
                ImageBean imageBean = new ImageBean();
                imageBean.setImage_id(imageId);
                imageBean.setImage_name(imageName);
                imageBean.setImage_path(imagePath);
                if (bucketSparse.get(bucketId) == null) {
                    BucketBean bucketBean = new BucketBean();
                    bucketBean.setBucket_id(bucketId);
                    bucketBean.setBucket_name(bucketName);
                    bucketBean.setImageBeen(new ArrayList<ImageBean>());
                    bucketBean.addImageBean(imageBean);
                    bucketSparse.put(bucketId, bucketBean);
                } else {
                    BucketBean bucketBean = bucketSparse.get(bucketId);
                    bucketBean.addImageBean(imageBean);
                }
                totalBucketBean.addImageBean(imageBean);
            }
            cursor.close();
        }
        mBuckets.add(totalBucketBean);
        for (int i = 0; i < bucketSparse.size(); i++) {
            mBuckets.add(bucketSparse.valueAt(i));
        }
    }

    public List<BucketBean> getBuckets() {
        return mBuckets;
    }

    public boolean addImage(ImageBean imageBean) {
        if (mSelectBeen.contains(imageBean))
            return false;
        else {
            mSelectBeen.add(imageBean);
            return true;
        }
    }

    public boolean removeImage(ImageBean imageBean) {
        return mSelectBeen.remove(imageBean);
    }

}
