package com.gy.listener.model.items.records;

import androidx.annotation.Nullable;

public class QuantityRecord extends Record {
    private int _quantity;

    public QuantityRecord(String text, int quantity) {
        super(text);
        _quantity = quantity;
    }

    public QuantityRecord(String text, @Nullable String imgPath, int quantity) {
        super(text, imgPath);
        _quantity = quantity;
    }

    public int getQuantity() {
        return _quantity;
    }

    public void setQuantity(int quantity) {
        _quantity = quantity;
    }
}
