package com.gy.listener.model.events;

import androidx.annotation.Nullable;

import com.gy.listener.model.items.users.User;

import java.util.List;

public interface IOnUsersFetchListener {
    void onFetch(@Nullable List<User> users);
}
