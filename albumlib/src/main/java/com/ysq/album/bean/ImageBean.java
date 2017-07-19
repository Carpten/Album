package com.ysq.album.bean;

import java.io.Serializable;

/**
 * Author: yangshuiqiang
 * Date:2017/4/14.
 */

public class ImageBean implements Serializable {

    private String image_name;

    private String image_path;


    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

}
