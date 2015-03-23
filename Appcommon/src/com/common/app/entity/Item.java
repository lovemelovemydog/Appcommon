package com.common.app.entity;

import java.io.Serializable;

/**
 * Created by fangzhu on 2015/3/18.
 */
public class Item implements Serializable {

    private String imageUrl;

    private int width;
    private int height;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
