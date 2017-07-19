package com.ysq.album.bean;

import java.util.List;

/**
 * Author: yangshuiqiang
 * Date:2017/4/14.
 */

public class BucketBean {

    private int bucket_id;

    private String bucket_name;

    private List<ImageBean0> imageBeen;


    public int getBucket_id() {
        return bucket_id;
    }

    public void setBucket_id(int bucket_id) {
        this.bucket_id = bucket_id;
    }

    public String getBucket_name() {
        return bucket_name;
    }

    public void setBucket_name(String bucket_name) {
        this.bucket_name = bucket_name;
    }

    public List<ImageBean0> getImageBeen() {
        return imageBeen;
    }

    public void setImageBeen(List<ImageBean0> imageBeen) {
        this.imageBeen = imageBeen;
    }

    public void addImageBean(ImageBean0 imageBean) {
        this.imageBeen.add(imageBean);
    }
}
