package com.gy.listener.myLists.models;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CheckedRecord extends Record {
    protected static String IS_CHECKED = "IsChecked";

    private boolean _isChecked;

    public CheckedRecord(String text, boolean isChecked) {
        super(text);
        _isChecked = isChecked;
    }

    public CheckedRecord(String text, @Nullable String imgPath, boolean isChecked) {
        super(text, imgPath);
        _isChecked = isChecked;
    }

    /**
     * Copy constructor
     */
    public CheckedRecord(CheckedRecord record) {
        super(record);
        _isChecked = record.getIsChecked();
    }

    public boolean getIsChecked() {
        return _isChecked;
    }

    public void setIsChecked(boolean checked) {
        _isChecked = checked;
    }

    @Override
    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<>(super.toJson());
        json.put(IS_CHECKED, _isChecked);

        return json;
    }

    public static CheckedRecord create(Map<String, Object> json) {
        return new CheckedRecord(
                (String) json.get(TEXT),
                (String) json.get(IMG_PATH),
                Boolean.parseBoolean((String) json.get(IS_CHECKED))
        );
    }

    @Override
    public Record getClone() {
        return new CheckedRecord(this);
    }
}
