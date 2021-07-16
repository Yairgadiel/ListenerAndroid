package com.gy.listener.model.db;

import androidx.room.Room;

import com.gy.listener.MyApplication;


public class DatabaseHelper  {
    public static final AppLocalDB db = Room.databaseBuilder(MyApplication.getAppContext(), AppLocalDB.class, "listener.db").build();
}

