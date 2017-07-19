package com.ysq.album.bean;

import java.io.Serializable;

/**
 * Author: yangshuiqiang
 * Date:2017/4/14.
 */

public class ImageBean0 implements Serializable {

    private int image_id;

    private String image_name;

    private String image_path;

    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

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
