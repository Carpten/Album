package com.ysq.album.itf;

import android.net.Uri;

import com.ysq.album.bean.ImageBean;

/**
 * Author: yangshuiqiang
 * Date:2017/4/24.
 */

public interface IAlbum {

    void takePhoto();

    void startZoom(Uri uri);

}
