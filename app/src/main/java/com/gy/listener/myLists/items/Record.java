package com.gy.listener.myLists.items;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class Record implements Serializable {
    private String _text;

    @Nullable
    private String _imgPath;

    public Record(String text) {
        _text = text;
    }

    public Record(String text, @Nullable String imgPath) {
        _text = text;
        _imgPath = imgPath;
    }

    public String getText() {
        return _text;
    }

    public void setText(String text) {
        _text = text;
    }

    @Nullable
    public String getImgPath() {
        return _imgPath;
    }

    public void setImgPath(@Nullable String imgPath) {
        _imgPath = imgPath;
    }
}
