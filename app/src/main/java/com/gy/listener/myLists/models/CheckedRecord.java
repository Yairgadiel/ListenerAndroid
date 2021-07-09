package com.gy.listener.myLists.models;

import androidx.annotation.Nullable;

public class CheckedRecord extends Record {
    private boolean _isChecked;

    public CheckedRecord(String text, boolean isChecked) {
        super(text);
        _isChecked = isChecked;
    }

    public CheckedRecord(String text, @Nullable String imgPath, boolean isChecked) {
        super(text, imgPath);
        _isChecked = isChecked;
    }

    public boolean getIsChecked() {
        return _isChecked;
    }

    public void setIsChecked(boolean checked) {
        _isChecked = checked;
    }
}
