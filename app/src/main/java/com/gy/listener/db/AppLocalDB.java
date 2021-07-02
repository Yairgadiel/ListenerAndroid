package com.gy.listener.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.gy.listener.myLists.RecordsList;

@Database(entities = {RecordsList.class}, version = 1)
public abstract class AppLocalDB extends RoomDatabase {
    public abstract IItemsListDAO itemsListDAO();
}