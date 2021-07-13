package com.gy.listener.myLists.models;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Record implements Serializable, IJsonConverter, ICloneable {
    protected static String TEXT = "Text";
    protected static String IMG_PATH = "ImgPath";

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

    /**
     * Copy constructor
     */
    public Record(Record record) {
        _text = record.getText();
        _imgPath = record.getImgPath();
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

    @Override
    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<>();
        json.put(TEXT, _text);
        json.put(IMG_PATH, _imgPath);

        return json;
    }

    public static Record create(Map<String, Object> json) {
        Record record = new Record(
                (String) json.get(TEXT),
                (String) json.get(IMG_PATH)
        );

        return record;
    }

    @Override
    public Record getClone() {
        return new Record(this);
    }
}
