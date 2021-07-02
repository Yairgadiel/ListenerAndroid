package com.gy.listener.myLists;

import androidx.annotation.Nullable;

public class Record {
    private String _mame;

    @Nullable
    private String _imgPath;

    public Record(String mame) {
        _mame = mame;
    }

    public Record(String mame, @Nullable String imgPath) {
        _mame = mame;
        _imgPath = imgPath;
    }

    public String getMame() {
        return _mame;
    }

    public void setMame(String mame) {
        _mame = mame;
    }

    @Nullable
    public String getImgPath() {
        return _imgPath;
    }

    public void setImgPath(@Nullable String imgPath) {
        _imgPath = imgPath;
    }
}
