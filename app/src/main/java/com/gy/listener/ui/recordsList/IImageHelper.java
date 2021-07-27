package com.gy.listener.ui.recordsList;

import com.gy.listener.model.events.IOnCompleteListener;
import com.gy.listener.model.events.IOnImageLoadedListener;
import com.gy.listener.model.events.IOnImageUploadedListener;

public interface IImageHelper {
    void pickImage(IOnImageUploadedListener listener);

    void loadImage(String path, IOnImageLoadedListener listener);

    void deleteImage(String name, IOnCompleteListener listener);

}
