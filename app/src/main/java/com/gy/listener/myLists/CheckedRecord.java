package com.gy.listener.myLists;

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

    public boolean getChecked() {
        return _isChecked;
    }

    public void setChecked(boolean checked) {
        _isChecked = checked;
    }
}
