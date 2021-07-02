package com.gy.listener.db;

import androidx.room.Room;

import com.gy.listener.MyApplication;
import com.gy.listener.utilities.Helpers;


public class DatabaseHelper  {
    final static public AppLocalDB db = Room.databaseBuilder(MyApplication.getAppContext(), AppLocalDB.class, "listener.db").build();
}

