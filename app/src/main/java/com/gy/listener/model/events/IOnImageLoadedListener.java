package com.gy.listener.model.events;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

public interface IOnImageLoadedListener {
    void onLoaded(@Nullable Bitmap image);
}
